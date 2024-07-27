package com.nmims.timeline.repository;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.Post;
import com.nmims.timeline.model.TestBean;
import com.nmims.timeline.model.TestQuestionBean;
@Repository
public class TestQuestionRepositoryForRedisImpl implements TestQuestionRepositoryForRedis {

	
	private static final String TEST_QUESTIONS_TABLE_NAME = "TEST_QUESTIONS";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, Long, List<TestQuestionBean>> hashOperations;

    private ListOperations listOperations;
    private SetOperations setOperations;


    public TestQuestionRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
        listOperations = redisTemplate.opsForList();
        setOperations = redisTemplate.opsForSet();
    }
    
    @Override
    public String save(List<TestQuestionBean> testQuestions) {
    	
    	try {
			hashOperations.put(TEST_QUESTIONS_TABLE_NAME, testQuestions.get(0).getTestId(), testQuestions);
			

    		//listOperations.rightPush(POST_TABLE_NAME+post.getSubjectConfigId(), post);
    		
    		//setOperations.add(post.getSubjectConfigId(), post.getSubjectConfigId());
    		
    		return "";
    	} catch (Exception e) {
    		//System.out.println("IN PostRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
			return e.getMessage();
		}
    
    }
	
	@Override
	public String delete(Long testId) {

        try {
			hashOperations.delete(TEST_QUESTIONS_TABLE_NAME, testId);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in deleting test questions, "+e.getMessage();
		}
    }

	@Override
	public List<TestQuestionBean> findAllByTestId(Long testId) {
		
		return 	hashOperations.get(TEST_QUESTIONS_TABLE_NAME, testId);
	}
    
}
