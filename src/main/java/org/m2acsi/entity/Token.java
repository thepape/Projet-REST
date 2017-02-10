package org.m2acsi.entity;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQuery(name="verifyToken",
query = " SELECT COUNT(t) FROM Token t WHERE"
		+ " t.idDemande = :idDemande AND t.token = :token")

@Entity
@Table(name="Token")
public class Token {

	public Token(){
		super();
		
		this.init();
	}
	
	private void init() {
		SecureRandom random = new SecureRandom();
		this.token = (new BigInteger(128, random)).toString();
	}

	/**
	 * UUID unique de la demande associee a ce token
	 */
	@Id
	private String idDemande;
	
	/**
	 * chaine alphanumerique du token
	 */
	private String token;

	public void setIdDemande(String idDemande) {
		this.idDemande = idDemande;
	}

	public String getToken() {
		return token;
	}
}
