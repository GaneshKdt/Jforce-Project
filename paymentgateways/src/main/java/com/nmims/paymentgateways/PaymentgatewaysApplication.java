package com.nmims.paymentgateways;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableEurekaClient
@PropertySource("file:c:/NMIMS_PROPERTY_FILE/ngasce.properties")
@PropertySource(value="file:c:/NMIMS_PROPERTY_FILE/application.properties", ignoreResourceNotFound = true)
@PropertySource(value="file:${catalina.base}/conf/application.properties", ignoreResourceNotFound = true)
public class PaymentgatewaysApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentgatewaysApplication.class, args);
	
	}

}
