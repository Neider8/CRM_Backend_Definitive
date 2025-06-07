package com.crmtech360.crmtech360_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- AÑADIR ESTA LÍNEA

@SpringBootApplication
@EnableScheduling
public class Crmtech360BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Crmtech360BackendApplication.class, args);
	}
}