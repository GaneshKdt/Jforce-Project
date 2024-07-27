package com.nmims.awsfileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableEurekaClient
@PropertySources({
 @PropertySource("file:${catalina.base}/conf/application.properties")
})
public class AwsfileuploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsfileuploadApplication.class, args);
	}

}
