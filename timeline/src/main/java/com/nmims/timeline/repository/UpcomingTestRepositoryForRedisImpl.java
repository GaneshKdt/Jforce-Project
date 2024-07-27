package com.nmims.timeline.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.TestBean;

@Repository
public class UpcomingTestRepositoryForRedisImpl implements UpcomingTestRepositoryForRedis {



	
	private static final String UPCOMING_TEST_TABLE_NAME = "UPCOMING_TEST";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, List<TestBean>> hashOperations;

    private static final Logger logger = LogManager.getLogger(UpcomingTestRepositoryForRedisImpl.class);

    public UpcomingTestRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
    }


	@Override
	public String saveUpcomingTestsForSapid(String sapid, List<TestBean> upcomingTestsForAfterLoginPage) {
		logger.info("UpcomingTestRepositoryForRedisImpl.saveUpcomingTestsForSapid() - START");
    	try {
			hashOperations.put(UPCOMING_TEST_TABLE_NAME, sapid, upcomingTestsForAfterLoginPage);
			logger.info("UpcomingTestRepositoryForRedisImpl.saveUpcomingTestsForSapid() - END");
    		return "";
    	} catch (Exception e) {
    		logger.error("IN UpcomingTestRepositoryForRedisImpl saveUpcomingTestsForSapid() catch got error : "+e.getMessage());
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }


	@Override
	public List<TestBean> getUpcomingTestsBySapid(String sapid) {
		List<TestBean> upcomingTestsBySapid = new ArrayList<>();
		
		try {
			upcomingTestsBySapid = hashOperations.get(UPCOMING_TEST_TABLE_NAME, sapid);
			if(upcomingTestsBySapid == null) {
				upcomingTestsBySapid = new ArrayList<>();
			}
		} catch (Exception e) {
    		//System.out.println("IN UpcomingTestRepositoryForRedisImpl getUpcomingTestsBySapid() catch got error : ");
    		e.printStackTrace();
		}
		
		return upcomingTestsBySapid; 
	}
    	

}
