package com.nmims.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableRedisHttpSession(redisNamespace = "studentportal")
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {
	
	@Value("${REDIS_HOST}")
	private String REDIS_HOST;
	
	@Value("${REDIS_PORT}")
	private int REDIS_PORT;
	
//    @Bean
//    public JedisConnectionFactory connectionFactory() {     // It will create filter for Redis store which will override default Tomcat Session
//        return new JedisConnectionFactory();
//    }
//    
	   @Bean
    public JedisConnectionFactory connectionFactory() {     // It will create filter for Redis store which will override default Tomcat Session
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(REDIS_HOST);
        jedisConnectionFactory.setPort(REDIS_PORT);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;

    }

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory());
        return redisTemplate;
     }

    @Bean
    public RestTemplate getRestTemplate() {
      return new RestTemplate();
    }
}
