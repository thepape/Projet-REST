package org.m2acsi.boundary;

import org.m2acsi.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/demandes", produces=MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Demande.class)
public class DemandeRepresentation {

	@Autowired
	DemandeResource dr;
	
	@GetMapping
	public ResponseEntity<?> getAllDemandes(){
		Iterable<Demande> allFormations = dr.findAll();
		
		return new  ResponseEntity<>(demandeToResource(allFormations), HttpStatus.OK);
	}
	
	
	@GetMapping(value="/{idDemande}")
	public ResponseEntity<?> getOneDemande(@PathVariable("idDemande") String id){
		
		return Optional.ofNullable(dr.findOne(id))
				.map(d -> new ResponseEntity<>(demandeToResource(d, true), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@PostMapping
	public ResponseEntity<?> sendDemande(@RequestBody Demande bodyDemande){
		
		
		Demande demande = dr.save(bodyDemande);
		
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeRepresentation.class).slash(demande.getIdDemande()).toUri());
		
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}
	
	////////////////// HATEOAS ///////////////////
	
	private Resource<Demande> demandeToResource(Demande demande, Boolean isCollection){
		
		Link selfLink = linkTo(DemandeRepresentation.class).slash(demande.getIdDemande()).withSelfRel();
		
		if(isCollection){
			Link collectionLink = linkTo(methodOn(DemandeRepresentation.class)
					.getAllDemandes())
					.withRel("collection ");
			
			return new Resource<>(demande, selfLink, collectionLink);
		}
		
		return new Resource<>(demande, selfLink);
	}
	
	private Resources<Resource<Demande>> demandeToResource(Iterable<Demande> demandes){
		
		Link selfLink = linkTo(methodOn(DemandeRepresentation.class)
				.getAllDemandes())
				.withSelfRel();
		
		List<Resource<Demande>> listDemandes = new ArrayList();
		demandes.forEach(formation -> listDemandes.add(demandeToResource(formation, false)));
		
		return new Resources<>(listDemandes, selfLink);
		
	}
}
