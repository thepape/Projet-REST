package org.m2acsi.entity;

public enum TypeAction {
	// 0
	ETUDE("Etude","etude"),
	
	// 1
	ETUDE_DETAILLEE("Etude détaillée","etude_detaillee"),
	
	// 2
	APPROBATION("Approbation","approbation"),
	
		// 3
        REFUS("Refus","refus"),
        
        // 4
        CREATION_ORDRE_MAINTENANCE("Création d'un ordre de maintenance","creation_ordre_maintenance"),
        
        // 5
        CREATION_COMMANDE("Creation d'une commande","creation_commande_reparation"),
        
        // 6
        CONFIRMATION_REPARATION("Confirmation de la réparation","confirmation_reparation"),
        
    // 7
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
