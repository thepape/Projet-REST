
package org.m2acsi.entity;

import java.util.Date;
import java.util.UUID;

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
    private String IdAction;
    
    /**
     * nom de l'action
     */
    private String nomAction;
    
    /**
     * etat de l'action
     * etat EN COURS et TERMINE
     */
    private String etatAction;
    
    /**
     * date de l'action
     * Au format JJ-MM-AAAA HH:mm:ss
     */
    private Date dateAction;
    
    private TypeAction type;
    
    public Demande getDemande() {
		return demande;
	}

	public void setDemande(Demande demande) {
		this.demande = demande;
	}

	@ManyToOne
    private Demande demande;
    
  public Action(String p_nom, String p_etat, TypeAction type){
      super();
      
      this.nomAction = p_nom;
      this.etatAction = "EN COURS";
      this.type = type;
      this.dateAction = new Date();
      
      this.init();
  }
  
  public void init(){
	  this.IdAction = UUID.randomUUID().toString().replaceAll("-", "");
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
  
  
  
}
