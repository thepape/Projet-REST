package org.m2acsi.boundary.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.m2acsi.boundary.ActionRepository;
import org.m2acsi.boundary.DemandeRepository;
import org.m2acsi.boundary.TokenRepository;
import org.m2acsi.entity.Action;
import org.m2acsi.entity.Demande;
import org.m2acsi.entity.EtatDemande;
import org.m2acsi.entity.Token;
import org.m2acsi.entity.TypeAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="internal/demandes", produces=MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Demande.class)
public class DemandeInternalController {

	@Autowired
	DemandeRepository dr;
	
	@Autowired
	TokenRepository tr;
	
	@Autowired
	ActionRepository ar;
	
	@PersistenceContext
	EntityManager em;
	
	/**
	 * GET | /demandes/{id} | Acceder a une demande | 2
	 * @param id
	 * @return
	 */
	@GetMapping(value="/{idDemande}")
	public ResponseEntity<?> getOneDemande(@PathVariable("idDemande") String id){
		
		
		
		return Optional.ofNullable(dr.findOne(id))
				.map(d -> new ResponseEntity<>(demandeToResource(d, true), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	/***
	 * GET | /demandes | Acceder aux demandes | 5
	 * @return
	 */
	@GetMapping
	public ResponseEntity<?> getAllDemandes(){
		
		Iterable<Demande> allDemandes = dr.findAll();
		return new  ResponseEntity<>(demandeToResource(allDemandes), HttpStatus.OK);
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
	 * POST | /demandes{id}/actions | Confirmer réparation | 4
	 * POST | /demandes{id}/actions | Décider de la demande | 6
	 * POST | /demandes{id}/actions | Transmettre pour approbation | 7
	 * POST | /demandes{id}/actions | Approuver demande | 8
	 * 
	 * @param bodyDemande
	 * @return
	 */
	@PostMapping(value="/{idDemande}/actions")
	public ResponseEntity<?> sendDemandeAction(@PathVariable("idDemande") String idDemande, @RequestBody Action bodyAction){
		
		
		//Action action = ar.save(bodyAction);
		
		
	
		//on recupere la demande correspondante
		Demande demande = dr.findOne(idDemande);
		
		//si id de demande incorrect, not found
		if(demande == null){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		//on verifie si l'utilisateur a le droit de poster une action de ce type
		//ex : perm_post_action_etude_detaillee
		String privilegeRequis = "perm_post_action_"+ bodyAction.getType().getNomPrivilege();
		
		if(!this.possedePrivilege(privilegeRequis)){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		//on ajoute le nom de l'utilisateur qui a post l'action
		String username = this.getUsername();
		bodyAction.setResponsable(username);
		
		
		
		//on ajoute l'action a la demande
		boolean actionPermise = demande.ajouterAction(bodyAction);
		
		if(!actionPermise){
			return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
		}
		
		Action action = ar.save(bodyAction);
		
		//sauvegarde des changements
		dr.save(demande);
		ar.save(action);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeCitizenController.class)
				.slash(demande.getIdDemande())
				.slash("actions")
				.slash(action.getIdAction())
				.toUri());
		
		return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
	}
        
	
        /**
	 * GET | /demandes/{id}/actions/{id} | Accéder aux actions | 5
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/{idDemande}/actions/{idAction}")
	public ResponseEntity<?> getAction(@PathVariable("idDemande") String idDemande, @PathVariable("idAction") String idAction){
                     
            Action action = ar.findOne(idAction);
            Demande demande = dr.findOne(idDemande);
            
            //on retourne un not found si aucune action n'existe pour cet id, si aucune demande n'existe pour cet id
            //ou si l'id de la demande ne correspond pas a l'id de l'action
            if(action == null || demande == null || !demande.getIdDemande().equals(action.getDemande().getIdDemande())){
            	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            
            
            return new  ResponseEntity<>(actionToResource(action, true), HttpStatus.OK);
        }
	
	/**
	 * GET | /demandes/{id}/actions | Accéder aux actions | 5
	 * @param idDemande
	 * @param idAction
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/{idDemande}/actions")
	public ResponseEntity<?> getActionsFromDemande(@PathVariable("idDemande") String idDemande){
                     
            
            Demande demande = dr.findOne(idDemande);
            
            return new  ResponseEntity<>(actionsToResource(demande), HttpStatus.OK);
        }
        
	
	/**
	 * DELETE | /demandes/{id} | Clore une demande | 10
	 * @param id
	 * @return
	 */
	@DeleteMapping(value="{idDemande}")
	public ResponseEntity<?> cloreDemande(@PathVariable("idDemande") String id){
		
		//on verifie que l'utilisateur possede bien la permission de delete.
		//Deja verifie dans le service OAuth mais bon, ceintures et bretelles !
		if(!possedePrivilege("perm_delete_demande")){
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		//objet introuvable pour cet id
		if(!dr.exists(id)){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
				
		Demande demande = dr.findOne(id);
		
		String username = this.getUsername();
				
		Action cloture = new Action("CLOTURE","TERMINE", username, TypeAction.CLOTURE);
		demande.ajouterAction(cloture);
		
		dr.save(demande);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(linkTo(DemandeCitizenController.class).slash(demande.getIdDemande()).toUri());
				
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
	}
	
	private boolean possedePrivilege(String privilege){
		return getAuthorities().contains(new SimpleGrantedAuthority(privilege));
	}
	
	private String getUsername(){
		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		
		
		return username;
	}
	
	private Collection<SimpleGrantedAuthority> getAuthorities(){
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	
		return authorities;
	}
	
	//////////////////HATEOAS ///////////////////
	
	private Resource<Action> actionToResource(Action action, Boolean isCollection){
		Link selfLink = linkTo(DemandeInternalController.class)
				.slash(action.getDemande().getIdDemande())
				.slash("actions")
				.slash(action.getIdAction())
				.withSelfRel();
		
		Link demandeLink = linkTo(DemandeInternalController.class)
				.slash(action.getDemande().getIdDemande())
				.withRel("demande");
		
		if(isCollection){
			Link collectionLink = linkTo(methodOn(DemandeInternalController.class)
					.getActionsFromDemande(action.getDemande().getIdDemande()))
					.withRel("collection");
			
			return new Resource<>(action, selfLink, collectionLink, demandeLink);
		}
		
		return new Resource<>(action, selfLink, demandeLink);
	}
	
	private Resources<Resource<Action>> actionsToResource(Demande demande){
		
		Link selfLink = linkTo(methodOn(DemandeInternalController.class)
			.getActionsFromDemande(demande.getIdDemande()))
			.withSelfRel();
		
		List<Resource<Action>> listeActions = new ArrayList();
		demande.getListeAction().forEach(action -> listeActions.add(actionToResource(action, false)));
		
		return new Resources<>(listeActions, selfLink);
		
	}
		
	private Resource<Demande> demandeToResource(Demande demande, Boolean isCollection){
		
		Link selfLink = linkTo(DemandeInternalController.class).slash(demande.getIdDemande()).withSelfRel();
		
		Link actionsLink = linkTo(DemandeInternalController.class)
				.slash(demande.getIdDemande())
				.slash("actions")
				.withRel("actions");
		
		if(isCollection){
			Link collectionLink = linkTo(methodOn(DemandeInternalController.class)
					.getAllDemandes())
					.withRel("collection ");
			
			return new Resource<>(demande, selfLink, actionsLink, collectionLink);
		}
		
		return new Resource<>(demande, selfLink, actionsLink);
	}
	
	private Resources<Resource<Demande>> demandeToResource(Iterable<Demande> demandes){
		
		Link selfLink = linkTo(methodOn(DemandeInternalController.class)
			.getAllDemandes())
			.withSelfRel();
		
		List<Resource<Demande>> listDemandes = new ArrayList();
		demandes.forEach(formation -> listDemandes.add(demandeToResource(formation, false)));
		
		return new Resources<>(listDemandes, selfLink);
		
	}
}
