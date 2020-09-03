package com.zackmurry.nottteme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * todo create note if you're unauthenticated (just can't save it)
 * users can't create two notes with the same name
 * todo figure out how to unit test this
 */
@SpringBootApplication
public class NottteMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NottteMeApplication.class, args);
	}

}
