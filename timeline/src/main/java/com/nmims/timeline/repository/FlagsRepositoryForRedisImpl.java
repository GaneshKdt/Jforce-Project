package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.FlagBean;

@Repository
public class FlagsRepositoryForRedisImpl implements FlagsRepositoryForRedis {



	
	private static final String FLAGS_TABLE_NAME = "FLAGS";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, FlagBean> hashOperations;


    public FlagsRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }
    	
	@Override
	public FlagBean getByKey(String key) {
		
		return 	hashOperations.get(FLAGS_TABLE_NAME, key);
	}

	@Override
	public List<FlagBean> getByKeysCommaSeperated(String keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlagBean> getAll() {
		
		return 	hashOperations.values(FLAGS_TABLE_NAME);
	}

	@Override
	public String save(FlagBean flagBean) {
    	
    	try {
			hashOperations.put(FLAGS_TABLE_NAME, flagBean.getKey(), flagBean);

    		return "";
    	} catch (Exception e) {
    		//System.out.println("IN FlagsRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }

	@Override
	public String deleteByKey(String key) {

        try {
			hashOperations.delete(FLAGS_TABLE_NAME, key);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in deleting flag , Key :  "+key+". Error : "+e.getMessage();
		}
    }

}
