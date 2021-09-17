package com.app.notificaciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.notificaciones.models.Notificaciones;

import feign.Param;

public interface NotificacionesRepository extends MongoRepository<Notificaciones, String>{

	@RestResource(path = "buscar-name")
	public Notificaciones findByUsername(@Param("username") String username);
	
}
