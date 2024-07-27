package com.nmims.timeline.repository;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.nmims.beans.StudentsDataInRedisBean;

@Repository
public class ResultsRepositoryForRedisImpl implements ResultsRepositoryForRedis {
		
		private static final String RESULTS_TABLE_NAME = "RESULTS";
		
	    private RedisTemplate<Object, Object> redisTemplate;

	    private HashOperations<Object, String, StudentsDataInRedisBean> hashOperations;

	    private ListOperations listOperations;
	    private SetOperations setOperations;

	    public ResultsRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
	        this.redisTemplate = redisTemplate;

	        hashOperations = redisTemplate.opsForHash();
	        listOperations = redisTemplate.opsForList();
	        setOperations = redisTemplate.opsForSet();
	    }

	    @Override
	    public String save(StudentsDataInRedisBean studentsDataInRedisBean) {
	    	
	    	try {
				hashOperations.put(RESULTS_TABLE_NAME, studentsDataInRedisBean.getSapid(), studentsDataInRedisBean);
				////System.out.println("IN ResultsRepositoryForRedisImpl save()  "+studentsDataInRedisBean.getSapid()+" ");
	    		
	    		return "";
	    	} catch (Exception e) {
	    		////System.out.println("IN ResultsRepositoryForRedisImpl save() catch got error : ");
	    		e.printStackTrace();
				return e.getMessage();
			}
	    
	    }
	
	

	@Override
	public Map<String, StudentsDataInRedisBean> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentsDataInRedisBean findBySapid(String sapid) {
		try {
			return (StudentsDataInRedisBean)hashOperations.get(RESULTS_TABLE_NAME, sapid);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
        
    }
	
}
