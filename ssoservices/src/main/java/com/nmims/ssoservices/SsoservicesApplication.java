package com.nmims.ssoservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan
@PropertySource("file:${catalina.base}/conf/application.properties")
public class SsoservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoservicesApplication.class, args);
	}

}
