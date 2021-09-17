package com.app.notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SpringbootNotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootNotificacionesApplication.class, args);
	}

}
