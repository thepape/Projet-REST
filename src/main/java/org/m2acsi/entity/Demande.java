package org.m2acsi.entity;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Demande {

	//Pour JPA
	public Demande(){
		super();
	}
	
	/**
	 * id de la demande
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long idDemande;
	
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
	
	public Demande(String p_nom, 
			String p_prenom,
			String p_adresse,
			String p_commentaire){
		super();
		
		this.nomCitoyen = p_nom;
		this.prenomCitoyen = p_prenom;
		this.adresseCitoyen = p_adresse;
		this.commentaireCitoyen = p_commentaire;
		
		this.dateDemande = new Date();
		this.etat = EtatDemande.DEBUT;
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

	public long getIdDemande() {
		return idDemande;
	}

	public Date getDateDemande() {
		return dateDemande;
	}
}
