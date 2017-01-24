package org.m2acsi.boundary;

import org.m2acsi.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface ActionResource extends JpaRepository<Action, String>{

}
