package com.nmims.coursera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({"com.nmims.coursera.*","com.nmims.coursera.web.controllers"})
@EnableCaching
@EnableScheduling
@PropertySources({
	@PropertySource("file:C:\\NMIMS_PROPERTY_FILE\\ngasce.properties"),
	@PropertySource("file:${catalina.base}/conf/application.properties")
})
public class CourseraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseraApplication.class, args);
	}

}
