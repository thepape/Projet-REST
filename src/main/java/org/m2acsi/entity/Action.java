
package org.m2acsi.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Action {
    
    public Action(){
        super();
    }
    
    /**
     * id de l'action
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long IdAction;
    
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
    
  public Action(String p_nom, String p_etat){
      super();
      
      this.nomAction = p_nom;
      this.etatAction = "EN COURS";
      this.dateAction = new Date();
  }

    /**
     * @return the IdAction
     */
    public long getIdAction() {
        return IdAction;
    }

    /**
     * @param IdAction the IdAction to set
     */
    public void setIdAction(long IdAction) {
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
