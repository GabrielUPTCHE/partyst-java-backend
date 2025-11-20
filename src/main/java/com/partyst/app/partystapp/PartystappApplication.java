package com.partyst.app.partystapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource(value = "classpath:application.yml", encoding = "UTF-8")
@EnableScheduling
public class PartystappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartystappApplication.class, args);
	}

}
