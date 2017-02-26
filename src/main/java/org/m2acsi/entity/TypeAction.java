package org.m2acsi.entity;

public enum TypeAction {
	ETUDE("Etude","etude"),
	ETUDE_DETAILLEE("Etude détaillée","etude_detaillee"),
	APPROBATION("Approbation","approbation"),
        REFUS("Refus","refus"),
        CREATION_ORDRE_MAINTENANCE("Création d'un ordre de maintenance","creation_ordre_maintenance"),
        CREATION_COMMANDE("Creation d'une commande","creation_commande_reparation"),
        CONFIRMATION_REPARATION("Confirmation de la réparation","confirmation_reparation"),
	CLOTURE("Cloture de la demande","");
        
	
	private String nom = "";
	
	/**
	 * suffixe correspondant au nom du privilege qu'il faut posseder pour poster cette action.
	 * ex : perm_post_action_etude_detaillee => etude_detaillee
	 */
	private String nomPrivilege = "";
	
	TypeAction(String nom, String nomPrivilege){
		this.nom = nom;
		this.nomPrivilege = nomPrivilege;
	}
	
	public String toString(){
		return this.nom;
	}
	
	public String getNomPrivilege(){
		return this.nomPrivilege;
	}
}
