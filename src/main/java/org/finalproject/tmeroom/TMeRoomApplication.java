package org.finalproject.tmeroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class TMeRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(TMeRoomApplication.class, args);
	}

}
