package com.gestiondesannotateurs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ✅ very important for spams detection in real time
public class GestiondesannotateursApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestiondesannotateursApplication.class, args);
	}

}
