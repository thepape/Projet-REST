package org.m2acsi.boundary;

import org.m2acsi.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RepositoryRestResource
public interface TokenRepository extends JpaRepository<Token, String>{

}