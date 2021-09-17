package com.app.notificaciones.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notificaciones")
public class Notificaciones {

	@Id
	private String id;

	@Indexed(unique = true)
	private String username;

	private List<List<String>> mensajes;

	private Boolean activar;

	private Integer codigo;

	public Notificaciones() {
	}

	public Notificaciones(String username, List<List<String>> mensajes, Boolean activar, Integer codigo) {
		super();
		this.username = username;
		this.mensajes = mensajes;
		this.activar = activar;
		this.codigo = codigo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<List<String>> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<List<String>> mensajes) {
		this.mensajes = mensajes;
	}

	public Boolean getActivar() {
		return activar;
	}

	public void setActivar(Boolean activar) {
		this.activar = activar;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
