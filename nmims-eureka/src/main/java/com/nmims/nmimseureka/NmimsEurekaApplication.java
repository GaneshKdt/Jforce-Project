package com.nmims.nmimseureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class NmimsEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NmimsEurekaApplication.class, args);
		System.out.println("Registry Server Started");
	}

}
