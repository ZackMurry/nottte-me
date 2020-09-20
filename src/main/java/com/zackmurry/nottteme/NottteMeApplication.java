package com.zackmurry.nottteme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * todo create note if you're unauthenticated (just can't save it)
 */
@SpringBootApplication
public class NottteMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NottteMeApplication.class, args);
		System.out.println(System.getenv("NOTTTE_JWT_SECRET_KEY"));
	}

}
