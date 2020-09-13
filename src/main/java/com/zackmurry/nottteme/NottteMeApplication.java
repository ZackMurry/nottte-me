package com.zackmurry.nottteme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * todo create note if you're unauthenticated (just can't save it)
 * todo figure out how to unit test this
 * todo style shortcuts with multiple styles
 */
@SpringBootApplication
public class NottteMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NottteMeApplication.class, args);
	}

}
