package org.m2acsi.entity;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@NamedQueries({
    @NamedQuery(name="demandesParEtat",
		query = " SELECT d FROM Demande d WHERE"
				+ " d.etat = :etat"),
    /*@NamedQuery(name="demandesParAction",
                query="SELECT c FROM Country c WHERE c.name = :name"),*/
}) 



@Entity
@Table(name="Demande")
public class Demande {

	//Pour JPA
	public Demande(){
		super();
		
		this.init();
	}

	/**
	 * id de la demande
	 */
	@Id //@GeneratedValue(strategy=GenerationType.AUTO)
	private String idDemande;
	
	/**
	 * nom du citoyen qui a fait la demande
	 */
	private String nomCitoyen;
	
	/**
	 * prenom du citoyen qui a fait la demande
	 */
	private String prenomCitoyen;
	
	/**
	 * adresse du citoyen qui a fait la demande
	 */
	private String adresseCitoyen;
	
	/**
	 * commentaire eventuel sur la demande
	 */
	private String commentaireCitoyen;
	
	/**
	 * date de soumission de la demande.
	 * Au format JJ-MM-AAAA HH:mm:ss
	 */
	private Date dateDemande;
	
	/**
	 * état de la demande (type énuméré)
	 */
	private EtatDemande etat;
        
        /**
         * Liste d'actions de la demande
         */
	@OneToMany(mappedBy="demande")
       	private List<Action> listeAction;
	
	public Demande(String p_nom, 
			String p_prenom,
			String p_adresse,
			String p_commentaire){
		super();
		
		this.nomCitoyen = p_nom;
		this.prenomCitoyen = p_prenom;
		this.adresseCitoyen = p_adresse;
		this.commentaireCitoyen = p_commentaire;
		
		this.init();
	}
	
	private void init(){
		this.dateDemande = new Date();
		this.etat = EtatDemande.DEBUT;
		this.listeAction = new ArrayList<Action>();
		
		this.idDemande = UUID.randomUUID().toString().replaceAll("-", "");
	}

	public String getNomCitoyen() {
		return nomCitoyen;
	}

	public void setNomCitoyen(String nomCitoyen) {
		this.nomCitoyen = nomCitoyen;
	}

	public String getPrenomCitoyen() {
		return prenomCitoyen;
	}

	public void setPrenomCitoyen(String prenomCitoyen) {
		this.prenomCitoyen = prenomCitoyen;
	}

	public String getAdresseCitoyen() {
		return adresseCitoyen;
	}

	public void setAdresseCitoyen(String adresseCitoyen) {
		this.adresseCitoyen = adresseCitoyen;
	}

	public String getCommentaireCitoyen() {
		return commentaireCitoyen;
	}

	public void setCommentaireCitoyen(String commentaireCitoyen) {
		this.commentaireCitoyen = commentaireCitoyen;
	}

	public EtatDemande getEtat() {
		return etat;
	}

	public void setEtat(EtatDemande etat) {
		this.etat = etat;
	}

	public String getIdDemande() {
		return idDemande;
	}
	
	public void setIdDemande(String id){
		this.idDemande = id;
	}

	public Date getDateDemande() {
		return dateDemande;
	}
	
	public void setDateDemande(Date date){
		this.dateDemande = date;
	}
	
	public boolean ajouterAction(Action action){
		
		//////// logique metier des modification d'etat de la demande //////
		/////// voir diagramme d'etat de Demande ///////
		
		if(action.getType().equals(TypeAction.CLOTURE)){
			
			//on ne peut clore une demande qu'une fois refusee ou approuvee
			if(!this.etat.equals(EtatDemande.REJET) && !this.etat.equals(EtatDemande.APPROUVEE)){
				return false;
			}
			
			this.etat = EtatDemande.FIN;
			action.setEtatAction("TERMINE");
		}
		else if(action.getType().equals(TypeAction.ETUDE)){
			
			//on ne peut mettre une demande a l'etude que si elle etait en DEBUT
			if(!this.etat.equals(EtatDemande.DEBUT)){
				return false;
			}
			
			this.etat = EtatDemande.ETUDE;
		}
		else if(action.getType().equals(TypeAction.ETUDE_DETAILLEE)){
			
			//on ne peut mettre une demande a l'etude detaillee qu'apres etude preliminaire
			if(!this.etat.equals(EtatDemande.ETUDE)){
				return false;
			}
			
			this.etat = EtatDemande.ETUDE_DETAILLEE;
		}
		else if(action.getType().equals(TypeAction.APPROBATION)){
			
			//on ne peut approuver une demande qu'apres etude ou etude detaillee
			if(!this.etat.equals(EtatDemande.ETUDE) && !this.etat.equals(EtatDemande.ETUDE_DETAILLEE)){
				return false;
			}
			
			this.etat = EtatDemande.APPROUVEE;
			action.setEtatAction("TERMINE");
		}
		else if(action.getType().equals(TypeAction.REFUS)){
			
			//on ne peut refusee une demande qu'apres etude ou etude detaillee
			if(!this.etat.equals(EtatDemande.ETUDE) && !this.etat.equals(EtatDemande.ETUDE_DETAILLEE)){
				return false;
			}
			
			this.etat = EtatDemande.REJET;
			action.setEtatAction("TERMINE");
		}
		////////////// pour les actions qui n'impactent pas l'etat, on verifie quand meme si
		////////////// la logique metier permet l'action
		else if(action.getType().equals(TypeAction.CREATION_ORDRE_MAINTENANCE)){
			
			//la creation de l'ordre de maintenance doit suivre une approbation
			if(!this.etat.equals(EtatDemande.APPROUVEE)){
				return false;
			}
		}
		else if(action.getType().equals(TypeAction.CREATION_COMMANDE)){
			
			//la creation d'une commande doit suivre une approbation
			if(!this.etat.equals(EtatDemande.APPROUVEE)){
				return false;
			}
		}
		else if(action.getType().equals(TypeAction.CONFIRMATION_REPARATION)){
			
			//la confirmation de reparation doit logiquemeent suivre un ordre de maintenance ou une commande de reparation
			if( ! (this.listeAction.size() > 0
					&& ( this.getLastAction().getType().equals(TypeAction.CREATION_ORDRE_MAINTENANCE)
							|| this.getLastAction().getType().equals(TypeAction.CREATION_COMMANDE))
					)){
				return false;
			}
		}
		
		//si il y avait une action avant, on passe son etat a TERMINE
				Action lastAction = null;
				
				if(this.listeAction.size() > 0){
					lastAction = this.listeAction.get(this.listeAction.size()-1);
				}
				
				if(lastAction != null && lastAction.getEtatAction().equals("EN COURS")){
					lastAction.setEtatAction("TERMINE");
				}
		
		//binding de l'action a la demande
		this.listeAction.add(action);
		action.setDemande(this);
		
		return true;
	}
	
	public boolean estClose(){
		Action lastAction = this.listeAction.get(this.listeAction.size()-1);
		
		if(lastAction.getType().equals(TypeAction.CLOTURE)){
			return true;
		}
		
		return false;
	}

	public List<Action> getListeAction() {
		return listeAction;
	}
	
	private Action getLastAction(){
		if(this.listeAction.size() == 0)
			return null;
		return this.listeAction.get(this.listeAction.size()-1);
	}
	
	
}
