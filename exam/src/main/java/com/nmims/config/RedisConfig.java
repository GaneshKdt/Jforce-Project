package com.nmims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RedisConfig {

//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        return new JedisConnectionFactory();
//    }
//
//    @Bean
//    RedisTemplate<Object, Object> redisTemplate() {
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//		redisTemplate.setConnectionFactory(jedisConnectionFactory());
//		
//        return redisTemplate;
//     }
//
//    @Bean
//    public RestTemplate getRestTemplate() {
//      return new RestTemplate();
//    }
}