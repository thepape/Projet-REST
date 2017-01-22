package org.m2acsi.entity;

public enum EtatDemande {
	DEBUT("Début", "état initial de la demande"),
	FIN("Fin","état final de la demande"),
	ETUDE("Etude","la demande est à l'étude"),
	ETUDE_DETAILLEE("Etude détaillée","la demande nécessite une étude détaillée"),
	APPROUVEE("Approuvée","la demande a été étudiée puis approuvée"),
	REJET("Rejet","la demande est rejetée, le demandeur est notifié");
	
	private String nom = "";
	private String description = "";
	
	EtatDemande(String nom, String description){
		this.nom = nom;
		this.description = description;
	}
	
	public String toString(){
		return this.nom;
	}
}
