package com.nmims.nmimsgateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile("!local-discovery")
@Configuration
public class LocalHostRouteConfig {
	
	@Bean
	public RouteLocator localHostRoutes(RouteLocatorBuilder builder) {
		return builder.routes().route(r -> r.path("/studentportal*", "/studentportal/*")
				.uri("http://localhost:8080")).build();
	}

}
