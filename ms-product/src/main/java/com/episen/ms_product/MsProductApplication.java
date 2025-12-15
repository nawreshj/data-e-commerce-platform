package com.episen.ms_product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProductApplication.class, args);
	}

}
