package org.m2acsi.boundary.controller;

import org.m2acsi.boundary.DemandeRepository;
import org.m2acsi.boundary.TokenRepository;
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
import org.springframework.web.bind.annotation.RequestHeader;
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

/**
 * Controleur d'acces aux Demandes pour les citoyens
 *
 */
@RestController
@RequestMapping(value="citizen/demandes", produces=MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Demande.class)
public class DemandeCitizenController {

	@Autowired
	DemandeRepository dr;
	
	@Autowired
	TokenRepository tr;
	
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
	
		//Liaison d'un token a cette demande dans la table TOKEN 
		Token token = new Token();
		token.setIdDemande(demande.getIdDemande());
		tr.save(token);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeCitizenController.class).slash(demande.getIdDemande()).toUri());
		
		Link selfLink = linkTo(DemandeCitizenController.class).slash(demande.getIdDemande()).withSelfRel();
		
		//on repond avec l'id de la demande, son URI pour y acceder, et le token associe
		String message = "{ \"idDemande\" : \""+demande.getIdDemande()+"\","
				+ " \"uri\" : \""+selfLink.getHref()+"\", "
				+ " \"token\" : \""+token.getToken()+"\" }";
		
		return new ResponseEntity<>(message, responseHeaders, HttpStatus.CREATED);
	}
	
	/**
	 * GET | /demandes/{id} | Suivre sa demande | 2
	 * @param id
	 * @return
	 */
	@GetMapping(value="/{idDemande}")
	public ResponseEntity<?> getOneDemandeCitizen(@PathVariable("idDemande") String id, @RequestHeader("Citizen-token") String token){
		
		Query query = em.createNamedQuery("verifyToken");
		query.setParameter("idDemande", id);
		query.setParameter("token", token);
		
		boolean tokenIsValid = ( Integer.parseInt(query.getSingleResult().toString()) == 1);
		
		if(!tokenIsValid){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		return Optional.ofNullable(dr.findOne(id))
				.map(d -> new ResponseEntity<>(demandeToResource(d), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	/**
	 * PUT | /demandes/{id} | Modifier sa demande | 3
	 * @param bodyDemande
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT, value="/{idDemande}")
	public ResponseEntity<?> updateDemande(@RequestBody Demande bodyDemande, @PathVariable("idDemande") String id, @RequestHeader("Citizen-token") String token){
		
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
		
		Query query = em.createNamedQuery("verifyToken");
		query.setParameter("idDemande", id);
		query.setParameter("token", token);
		
		boolean tokenIsValid = ( Integer.parseInt(query.getSingleResult().toString()) == 1);
		
		if(!tokenIsValid){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
	
	////////////////// HATEOAS ///////////////////
	
	private Resource<Demande> demandeToResource(Demande demande){
		//A modifier ici !
		
		Link selfLink = linkTo(DemandeCitizenController.class).slash(demande.getIdDemande()).withSelfRel();
		
		
		return new Resource<>(demande, selfLink);
	}
}
