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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@RestController
@RequestMapping(value="/demandes", produces=MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Demande.class)
public class DemandeRepresentation {

	@Autowired
	DemandeResource dr;
	
	@PersistenceContext
	EntityManager em;
	
	
	/**
	 * POST | /demandes | Déposer une demande | 1
	 * @param bodyDemande
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> sendDemande(@RequestBody Demande bodyDemande){
		
		Demande demande = dr.save(bodyDemande);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeRepresentation.class).slash(demande.getIdDemande()).toUri());
		
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}
	
	/**
	 * GET | /demandes/{id} | Suivre sa demande | 2
	 * @param id
	 * @return
	 */
	@GetMapping(value="/{idDemande}")
	public ResponseEntity<?> getOneDemande(@PathVariable("idDemande") String id){
		
		return Optional.ofNullable(dr.findOne(id))
				.map(d -> new ResponseEntity<>(demandeToResource(d, true), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	/**
	 * PUT | /demandes/{id} | Modifier sa demande | 3
	 * @param bodyDemande
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT, value="/{idDemande}")
	public ResponseEntity<?> updateDemande(@RequestBody Demande bodyDemande, @PathVariable("idDemande") String id){
		
		//tester si deja en base ou present dans le body
		Optional<Demande> body = Optional.ofNullable(bodyDemande);
		
		//objet non present dans le body
		if(!body.isPresent()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//objet introuvable pour cet id
		if(!dr.exists(id)){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Demande oldDemande = dr.findOne(id);
		
		//si l'etat de la demande est autre que DEBUT, on ne peut pas modifier la demande
		if(!oldDemande.getEtat().equals(EtatDemande.DEBUT)){
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}
		
		bodyDemande.setIdDemande(id);
		bodyDemande.setDateDemande(oldDemande.getDateDemande());	//la date de submission ne doit pas changer
		
		Demande demande = dr.save(bodyDemande);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/***
	 * GET | /demandes | Acceder aux demandes | 5
	 * @return
	 */
	@GetMapping
	public ResponseEntity<?> getAllDemandes(){
		
		Iterable<Demande> allFormations = dr.findAll();
		return new  ResponseEntity<>(demandeToResource(allFormations), HttpStatus.OK);
	}
	
	/**
	 * GET | /demandes/?status={status} | Acceder aux demandes | 5
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, params = {"status"})
	public ResponseEntity<?> getDemandeByStatus(@RequestParam(value="status") String status){
		
		EtatDemande etatDemande = null;
		
		try{
			
			etatDemande = EtatDemande.valueOf(status);
			
		}catch(IllegalArgumentException e){
			//si l'etat passe en parametre n'existe pas, on retourne une erreur 400
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Query query = em.createNamedQuery("demandesParEtat");
		query.setParameter("etat", etatDemande);
		
		Iterable<Demande> resultat = query.getResultList();
		
		return new  ResponseEntity<>(demandeToResource(resultat), HttpStatus.OK);
	}
	
	/**
	 * DELETE | /demandes/{id} | Clore une demande | 10
	 * @param id
	 * @return
	 */
	@DeleteMapping(value="{idDemande}")
	public ResponseEntity<?> cloreDemande(@PathVariable("idDemande") String id){
		
		//objet introuvable pour cet id
		if(!dr.exists(id)){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
				
		Demande demande = dr.findOne(id);
				
		Action cloture = new Action("CLOTURE","TERMINE", TypeAction.CLOTURE);
		demande.ajouterAction(cloture);
		
		dr.save(demande);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeRepresentation.class).slash(demande.getIdDemande()).toUri());
				
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
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
