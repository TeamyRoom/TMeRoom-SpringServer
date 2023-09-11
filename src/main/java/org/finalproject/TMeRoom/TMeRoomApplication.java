package org.finalproject.TMeRoom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TMeRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(TMeRoomApplication.class, args);
	}

}
