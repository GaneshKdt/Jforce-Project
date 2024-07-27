 	package com.nmims.timeline.repository;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class CounterRepositoryForRedisImpl implements CounterRepositoryForRedis {
		
		private static final String RESULTS_TABLE_NAME = "RESULTS";
		
	    private RedisTemplate<Object, Object> redisTemplate;

	    private HashOperations<Object, String, String> hashOperations;

	    private ListOperations listOperations;
	    private SetOperations setOperations;

	    public CounterRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
	        this.redisTemplate = redisTemplate;

	        hashOperations = redisTemplate.opsForHash();
	        listOperations = redisTemplate.opsForList();
	        setOperations = redisTemplate.opsForSet();
	    }

	    @Override
	    public String save(String tableName,String keyName,String counterData) {
	    	
	    	try {
				hashOperations.put(tableName, keyName, counterData);
	    		return "";
	    	} catch (Exception e) {
	    		e.printStackTrace();
				return e.getMessage();
			}
	    
	    }
	
	


	@Override
	public String findByTableNameKeyName(String tableName,String keyName) {
        return hashOperations.get(tableName, keyName);
    }
	
}
