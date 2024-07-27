package com.nmims.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void save(StudentsDataInRedisBean studentsDataInRedisBean) throws Exception {
	    hashOperations.put(RESULTS_TABLE_NAME, studentsDataInRedisBean.getSapid(), studentsDataInRedisBean);
    }
    
    public Set<String> getAllKeys() throws Exception {
		return hashOperations.keys(RESULTS_TABLE_NAME);
    }
    
    public void deleteKeys(Set<String> keys) throws Exception {
    	for (String key : keys) {
    		hashOperations.delete(RESULTS_TABLE_NAME, key);	
		}
    }
	@Override
	public Map<String, StudentsDataInRedisBean> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentsDataInRedisBean findBySapid(String sapid) {
        return (StudentsDataInRedisBean)hashOperations.get(RESULTS_TABLE_NAME, sapid);
    }
	
}
