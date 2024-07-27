package com.nmims.timeline;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan({"com.nmims.timeline","com.nmims.timeline.controller"})
@EnableCaching
@EnableScheduling
@EnableEurekaClient
@PropertySources({
 @PropertySource("file:C:\\NMIMS_PROPERTY_FILE\\ngasce.properties"),
 @PropertySource("file:${catalina.base}/conf/application.properties")
})
public class TimelineApplication {
	 
	/*
	//catalinaBase

	@Value( "${server.tomcat.basedir}" )
	private String catalinaBase;
	

@Value("${server.port}")
private int serverport;

    @LocalServerPort
    int randomServerPort;
    
    @Autowired
    Environment environment;
    */
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate<Object, Object> redisTemplate() {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
 	}
	  
	@Bean
	public RestTemplate getRestTemplate() {
	  return new RestTemplate();
	}
	
	/*
	 @Bean
	  public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		 String port = "";
		    if(environment != null) {
		    	port = environment.getProperty("local.server.port");
		    }
		 System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
		 System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
		 String configLocation = System.getProperty("global.appconf.dir"); //get the default config directory location
		 System.out.println("configLocation: " + configLocation);
		     String configPath = configLocation + File.separator + "springApplication"  + File.separator + "application.yml"; //set the configpath of this application instance exclusively
	     System.out.println("Configpath: " + configPath);
	     System.out.println("Starting to run Spring boot app...");
	
		 System.out.println("serverport:"+serverport);
		 System.out.println(environment);
		 System.out.println(port);
		 System.out.println(randomServerPort);
		 System.out.println(catalinaBase);
		 System.out.println(System.getProperty("catalina.home"));
		 System.out.println(System.getenv("CATALINA_HOME"));
		 System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
		 System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
		 
		PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
	    properties.setLocation(new FileSystemResource("../../conf/application.properties"));
	    properties.setIgnoreResourceNotFound(false);

	    return properties;
	  }
	*/	
	 /*
	 @Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	 String configLocation = System.getProperty("global.appconf.dir"); //get the default config directory location
	     String configPath = configLocation + File.separator + "springApplication"  + File.separator + "application.yml"; //set the configpath of this application instance exclusively
	     System.out.println("Configpath: " + configPath);
	     System.out.println("Starting to run Spring boot app...");
	     if(configLocation != null && !configLocation.isEmpty()) {
	     Properties props = new Properties();
	     props.setProperty("spring.config.location", configPath); //set the config file to use    
	     application.application().setDefaultProperties(props);
	     }else{
	     System.out.println("No global.appconf.dir property found, starting with default on classpath");
	     }
	 return application.sources(SpringApplication.class);
	 }*/
	 
	public static void main(String[] args) {
		SpringApplication.run(TimelineApplication.class, args);
	}

}
