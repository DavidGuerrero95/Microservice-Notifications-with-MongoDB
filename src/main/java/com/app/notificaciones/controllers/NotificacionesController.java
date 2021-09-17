package com.app.notificaciones.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.notificaciones.clients.SubscripcionesFeignClient;
import com.app.notificaciones.clients.UsuarioFeignClient;
import com.app.notificaciones.models.Notificaciones;
import com.app.notificaciones.models.Subscripciones;
import com.app.notificaciones.models.Usuario;
import com.app.notificaciones.repository.NotificacionesRepository;
import com.app.notificaciones.request.Mensajes;
import com.app.notificaciones.services.EmailSenderService;

@RestController
public class NotificacionesController {

	@Autowired
	EmailSenderService emailSender;

	@Autowired
	UsuarioFeignClient usuariosClient;

	@Autowired
	SubscripcionesFeignClient suscripciones;

	@Autowired
	NotificacionesRepository notificaciones;

	@PostMapping("/notificaciones/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void crearNotificaciones(@RequestParam String nombre) {
		try {
			Notificaciones noti = new Notificaciones();
			noti.setUsername(nombre);
			noti.setActivar(false);
			noti.setMensajes(new ArrayList<List<String>>());
			notificaciones.save(noti);
		} catch (Exception e) {
			System.out.println("Error --> " + e.getLocalizedMessage() + "Error -->" + e.getMessage());
		}

	}

	@DeleteMapping("/notificaciones/eliminar")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarNotificacion(@RequestParam String nombre) {
		Notificaciones not = notificaciones.findByUsername(nombre);
		String id = not.getId();
		notificaciones.deleteById(id);
	}

	@PostMapping("/notificaciones/editEnabled")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeEnabled(@RequestParam String nombre, @RequestParam Boolean enabled) {
		Notificaciones noti = new Notificaciones();
		List<String> notificacion = new ArrayList<>();
		List<List<String>> totalNotificacions = new ArrayList<List<String>>();
		Calendar c = Calendar.getInstance();
		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;
		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;
		Mensajes correo = new Mensajes();
		String estado;
		if (enabled) {
			estado = "Habilitado";
		} else {
			estado = "DesHabilitado";
		}
		String mensaje = "Cambio la disponibilidad del proyecto: " + nombre + ". A: " + estado
				+ " Puedes Revisar sus Estadisticas en la app: City SuperApp.";
		Subscripciones sus = suscripciones.getProyectosByNombre(nombre);
		notificacion.add(nombre);
		notificacion.add(mensaje);
		notificacion.add(fecha);
		notificacion.add(tiempo);
		correo.setMensaje(mensaje);
		List<String> suscritos = sus.getSubscripciones();
		if (!suscritos.isEmpty()) {
			for (int i = 0; i < suscritos.size(); i++) {
				if (usuariosClient.existsByUsername(suscritos.get(i))) {
					noti = notificaciones.findByUsername(suscritos.get(i));
					totalNotificacions = noti.getMensajes();
					totalNotificacions.add(notificacion);
					noti.setMensajes(totalNotificacions);
					noti.setActivar(true);
					notificaciones.save(noti);
					correo.setName(suscritos.get(i));
					correo.setEmail(usuariosClient.findUsername(suscritos.get(i)).getEmail());
					enviarMensaje(correo);
				}
			}
		}
	}

	@PostMapping("/notificaciones/editEstado")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeEstado(@RequestParam String nombre, @RequestParam Integer estado) {
		Notificaciones noti = new Notificaciones();
		List<String> notificacion = new ArrayList<>();
		List<List<String>> totalNotificacions = new ArrayList<List<String>>();
		Calendar c = Calendar.getInstance();
		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;
		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;
		Mensajes correo = new Mensajes();
		String mensajeCom;
		if (estado == 1) {
			mensajeCom = "Produccion";
		} else if (estado == 2) {
			mensajeCom = "Desarrollo";
		} else if (estado == 3) {
			mensajeCom = "Implementacion";
		} else {
			mensajeCom = "Finalizo";
		}
		String mensaje = "Cambio el estado del proyecto: " + nombre + ". A: " + mensajeCom
				+ " Puedes Revisar sus Estadisticas en la app: City SuperApp.";
		Subscripciones sus = suscripciones.getProyectosByNombre(nombre);
		List<String> suscritos = sus.getSubscripciones();
		notificacion.add(nombre);
		notificacion.add(mensaje);
		notificacion.add(fecha);
		notificacion.add(tiempo);
		correo.setMensaje(mensaje);
		if (!suscritos.isEmpty()) {
			for (int i = 0; i < suscritos.size(); i++) {
				if (usuariosClient.existsByUsername(suscritos.get(i))) {
					noti = notificaciones.findByUsername(suscritos.get(i));
					totalNotificacions = noti.getMensajes();
					totalNotificacions.add(notificacion);
					noti.setMensajes(totalNotificacions);
					noti.setActivar(true);
					notificaciones.save(noti);
					correo.setName(suscritos.get(i));
					correo.setEmail(usuariosClient.findUsername(suscritos.get(i)).getEmail());
					enviarMensaje(correo);
				}
			}
		}
	}

	@PostMapping("/notificaciones/registro")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeSuscripciones(@RequestParam String correo, @RequestParam Integer codigo) {
		Mensajes mensaje = new Mensajes();
		String texto = "El codigo de verificacion de la cuenta de City SuperApp es: " + codigo;
		mensaje.setName("Registro: City SuperApp");
		mensaje.setMensaje(texto);
		mensaje.setEmail(correo);
		enviarMensaje(mensaje);
	}

	@PostMapping("/notificaciones/suscripciones")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeSuscripciones(@RequestParam String nombre, @RequestParam String username) {
		Notificaciones noti = new Notificaciones();
		List<String> notificacion = new ArrayList<>();
		List<List<String>> totalNotificacions = new ArrayList<List<String>>();
		Mensajes correo = new Mensajes();
		String mensaje = "Suscrito correctamente al proyecto: " + nombre + ".";

		Calendar c = Calendar.getInstance();
		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;
		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;

		notificacion.add(nombre);
		notificacion.add(mensaje);
		notificacion.add(fecha);
		notificacion.add(tiempo);
		correo.setMensaje(mensaje);
		if (usuariosClient.existsByUsername(username)) {
			noti = notificaciones.findByUsername(username);
			totalNotificacions = noti.getMensajes();
			totalNotificacions.add(notificacion);
			noti.setMensajes(totalNotificacions);
			noti.setActivar(true);
			notificaciones.save(noti);
			correo.setName(username);
			correo.setEmail(usuariosClient.findUsername(username).getEmail());
			enviarMensaje(correo);
		}

	}

	@PostMapping("/notificaciones/inscripciones")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeInscripciones(@RequestParam String nombre, @RequestParam String username) {
		Notificaciones noti = new Notificaciones();
		List<String> notificacion = new ArrayList<>();
		List<List<String>> totalNotificacions = new ArrayList<List<String>>();
		Calendar c = Calendar.getInstance();

		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;

		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;
		Mensajes correo = new Mensajes();
		String mensaje = "Gracias por participar en el proyecto: " + nombre
				+ ", sus aportes serán muy valiosos para el diseño y seguimiento del proyecto."
				+ " Puedes ver las estadística de participación en la City SuperApp."
				+ "\nDeseas adquirir información de la evolución del proyecto, inscríbete!";
		notificacion.add(nombre);
		notificacion.add(mensaje);
		notificacion.add(fecha);
		notificacion.add(tiempo);
		correo.setMensaje(mensaje);
		if (usuariosClient.existsByUsername(username)) {
			noti = notificaciones.findByUsername(username);
			totalNotificacions = noti.getMensajes();
			totalNotificacions.add(notificacion);
			noti.setMensajes(totalNotificacions);
			noti.setActivar(true);
			notificaciones.save(noti);
			correo.setName(username);
			correo.setEmail(usuariosClient.findUsername(username).getEmail());
			enviarMensaje(correo);
		}

	}

	@GetMapping("/notificaciones/editarUsuario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void editarUsuario(@PathVariable String nombre, @RequestParam(value = "codigo") Integer codigo) {
		Usuario usuario = usuariosClient.findUsername(nombre);
		Notificaciones noti = notificaciones.findByUsername(nombre);
		noti.setCodigo(codigo);
		notificaciones.save(noti);
		String correo = usuario.getEmail();
		Mensajes mensaje = new Mensajes();
		String texto = "El codigo de verificacion de para la edición de tu perfil en la City SuperApp es: " + codigo;
		mensaje.setName("Edición de perfil: " + usuario.getUsername());
		mensaje.setMensaje(texto);
		mensaje.setEmail(correo);
		enviarMensaje(mensaje);
	}

	@PutMapping("/notificaciones/enviarMensajeModerator/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensajeModerator(@PathVariable(value = "nombre") String nombre,
			@RequestParam(value = "mensaje") String mensaje) {
		Notificaciones noti = new Notificaciones();

		List<String> notificacion = new ArrayList<>();
		List<List<String>> totalNotificacions = new ArrayList<List<String>>();

		Calendar c = Calendar.getInstance();

		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;

		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;
		Mensajes correo = new Mensajes();

		Subscripciones sus = suscripciones.getProyectosByNombre(nombre);
		List<String> suscritos = sus.getSubscripciones();

		notificacion.add(nombre);
		notificacion.add(mensaje);
		notificacion.add(fecha);
		notificacion.add(tiempo);
		correo.setMensaje(mensaje);

		if (!suscritos.isEmpty()) {
			for (int i = 0; i < suscritos.size(); i++) {
				if (usuariosClient.existsByUsername(suscritos.get(i))) {
					noti = notificaciones.findByUsername(suscritos.get(i));
					totalNotificacions = noti.getMensajes();
					totalNotificacions.add(notificacion);
					noti.setMensajes(totalNotificacions);
					noti.setActivar(true);
					notificaciones.save(noti);
					correo.setName(suscritos.get(i));
					correo.setEmail(usuariosClient.findUsername(suscritos.get(i)).getEmail());
					enviarMensaje(correo);
				}
			}
		}

	}

	@GetMapping("/notificaciones/verificarCodigoUsuario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Integer verificarCodigoUsuario(@PathVariable String nombre) {
		Notificaciones noti = notificaciones.findByUsername(nombre);
		return noti.getCodigo();
	}

	@PutMapping("/notificaciones/eliminarCodigoUsuario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarCodigoUsuario(@PathVariable String nombre) {
		Notificaciones noti = notificaciones.findByUsername(nombre);
		noti.setCodigo(0);
		notificaciones.save(noti);
	}

	@PostMapping("/notificaciones/enviar")
	@ResponseStatus(code = HttpStatus.OK)
	public void enviarMensaje(@RequestBody Mensajes correo) {
		emailSender.sendSimpleEmail(correo.getEmail(), correo.getMensaje(), correo.getName());
	}

	@GetMapping("/notificaciones/revisarNotificacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean revisarNotificacion(@PathVariable String username) {
		if (usuariosClient.existsByUsername(username)) {
			Notificaciones noti = notificaciones.findByUsername(username);
			return noti.getActivar();
		} else {
			return null;
		}
	}

	@PutMapping("/notificaciones/cambiarNotificacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public void cambiarNotificacion(@PathVariable String username) {
		if (usuariosClient.existsByUsername(username)) {
			Notificaciones noti = notificaciones.findByUsername(username);
			noti.setActivar(false);
			notificaciones.save(noti);
		}
	}

	@PutMapping("/notificaciones/borrarNotificacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public void borrarNotificacion(@PathVariable String username) {
		if (usuariosClient.existsByUsername(username)) {
			Notificaciones noti = notificaciones.findByUsername(username);
			noti.setMensajes(new ArrayList<List<String>>());
			noti.setActivar(false);
			notificaciones.save(noti);
		}
	}

	@GetMapping("/notificaciones/verNotificaciones/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<String>> verNotificaciones(@PathVariable String username) {
		Notificaciones noti = notificaciones.findByUsername(username);
		return noti.getMensajes();
	}

	@PutMapping("/notificaciones/arreglarNotificaciones")
	@ResponseStatus(code = HttpStatus.OK)
	public void arreglarNotificaciones() {
		Calendar c = Calendar.getInstance();

		String dia = Integer.toString(c.get(Calendar.DATE));
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String annio = Integer.toString(c.get(Calendar.YEAR));
		String fecha = dia + "/" + mes + "/" + annio;

		Integer hora = c.get(Calendar.HOUR_OF_DAY);
		Integer minutos = c.get(Calendar.MINUTE);
		String tiempo = hora + ":" + minutos;

		List<Notificaciones> listaNotificaciones = notificaciones.findAll();

		for (int i = 0; i < listaNotificaciones.size(); i++) {
			List<List<String>> mensajes = listaNotificaciones.get(i).getMensajes();
			for (int j = 0; j < mensajes.size(); j++) {
				List<String> msnjs = mensajes.get(j);
				msnjs.add(fecha);
				msnjs.add(tiempo);
				System.out.println(msnjs);
				mensajes.set(j, msnjs);
			}
			listaNotificaciones.get(i).setMensajes(mensajes);
			listaNotificaciones.get(i).setCodigo(0);
			notificaciones.save(listaNotificaciones.get(i));
		}

	}

}
