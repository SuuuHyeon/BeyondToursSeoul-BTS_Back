package com.beyondtoursseoul.bts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
//@EnableScheduling
@SpringBootApplication
public class BtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtsApplication.class, args);
	}

}
