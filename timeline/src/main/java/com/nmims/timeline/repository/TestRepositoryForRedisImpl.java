package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestBean;
import com.nmims.timeline.model.TestQuestionBean;

@Repository
public class TestRepositoryForRedisImpl implements TestRepositoryForRedis {



	
	private static final String TEST_TABLE_NAME = "TEST";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, Long, TestBean> hashOperations;


    public TestRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }
    	
	@Override
	public String save(TestBean test) {
    	
    	try {
			hashOperations.put(TEST_TABLE_NAME, test.getId(), test);

    		return "";
    	} catch (Exception e) {
    		//System.out.println("IN TestRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }

	@Override
	public String delete(Long id) {

        try {
			hashOperations.delete(TEST_TABLE_NAME, id);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in deleting test , "+e.getMessage();
		}
    }

	@Override
	public TestBean findFirstById(Long id) {
		
		return 	hashOperations.get(TEST_TABLE_NAME, id);
	}

}
