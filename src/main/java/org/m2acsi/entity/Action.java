
package org.m2acsi.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Action")
public class Action {
    
    public Action(){
        super();
        
        this.init();
    }
    
    /**
     * id de l'action
     */
    @Id //@GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id_action")
    private String IdAction;
    
    /**
     * nom de l'action
     */
    @Column(name="nom_action")
    private String nomAction;
    
    /**
     * etat de l'action
     * etat EN COURS et TERMINE
     */
    @Column(name="etat_action")
    private String etatAction;
    
    /**
     * date de l'action
     * Au format JJ-MM-AAAA HH:mm:ss
     */
    @Column(name="date_action")
    private Date dateAction;
    
    @Column(name="type")
    private TypeAction type;
    
    @Column(name="responsable")
    private String responsable;
    
    public Demande getDemande() {
		return demande;
	}

	public void setDemande(Demande demande) {
		this.demande = demande;
	}

	@ManyToOne
    private Demande demande;
    
  public Action(String p_nom, String p_etat, String resp, TypeAction type){
      super();
      
      this.nomAction = p_nom;
      
      this.type = type;
      this.responsable = resp;
      
      this.init();
  }
  
  public String getResponsable() {
	return responsable;
}

public void setResponsable(String responsable) {
	this.responsable = responsable;
}

public void init(){
	  this.IdAction = UUID.randomUUID().toString().replaceAll("-", "");
	  this.dateAction = new Date();
	  this.etatAction = "EN COURS";
  }

    public TypeAction getType() {
	return type;
}

public void setType(TypeAction type) {
	this.type = type;
}

	/**
     * @return the IdAction
     */
    public String getIdAction() {
        return IdAction;
    }

    /**
     * @param IdAction the IdAction to set
     */
    public void setIdAction(String IdAction) {
        this.IdAction = IdAction;
    }

    /**
     * @return the etatAction
     */
    public String getEtatAction() {
        return etatAction;
    }

    /**
     * @param etatAction the etatAction to set
     */
    public void setEtatAction(String etatAction) {
        this.etatAction = etatAction;
    }
  
    public void setNomAction(String nomAction){
    	this.nomAction = nomAction;
    }

	public Date getDateAction() {
		return dateAction;
	}

	public void setDateAction(Date dateAction) {
		this.dateAction = dateAction;
	}

	public String getNomAction() {
		return nomAction;
	}
  
}
