package org.m2acsi.entity;

public enum TypeAction {
	ETUDE("Etude"),
	APPROBATION("Approbation"),
    REFUS("Refus"),
    CREATION_COMMANDE("Creation d'une commande"),
	CLOTURE("Cloture de la demande");
        
	
	private String nom = "";
	
	TypeAction(String nom){
		this.nom = nom;
	}
	
	public String toString(){
		return this.nom;
	}
}
