package com.partyst.app.partystapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.yml", encoding = "UTF-8")
public class PartystappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartystappApplication.class, args);
	}

}
