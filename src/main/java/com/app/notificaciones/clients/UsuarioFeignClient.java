package com.app.notificaciones.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.notificaciones.models.Usuario;

@FeignClient(name = "app-usuarios")
public interface UsuarioFeignClient {

	@GetMapping("/users/existUsername")
	public Boolean existsByUsername(@RequestParam String username);

	@GetMapping("/users/findUsername")
	public Usuario findUsername(@RequestParam String username);
	
	@GetMapping("/users/listar")
	public List<Usuario> getUsers();

}
