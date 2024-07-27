package com.nmims.timeline.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.StudentQuestionResponseBean;
import com.nmims.timeline.service.ErrorAnalyticsService;

@Repository
public class StudentTestAnswersRepositoryForRedisImpl implements StudentTestAnswersRepositoryForRedis {

	
	private static final String STUDENTS_ANSWERS_TABLE_NAME = "STUDENTS_ANSWERS";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, StudentQuestionResponseBean> hashOperations;

    //private ListOperations listOperations;
    //private SetOperations setOperations;
    
    private ErrorAnalyticsService errorAnalyticsService;

    public StudentTestAnswersRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate,
    		ErrorAnalyticsService errorAnalyticsService) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
        //listOperations = redisTemplate.opsForList();
        //setOperations = redisTemplate.opsForSet();
        this.errorAnalyticsService = errorAnalyticsService;
    }
    
	@Override
	public String save(StudentQuestionResponseBean answer) {
    	
    	try {
			hashOperations.put(STUDENTS_ANSWERS_TABLE_NAME, answer.getSapid()+"-"+answer.getQuestionId()+"-"+answer.getAttempt(), answer);
			return "";
    	} catch (Exception e) {
    		//System.out.println("IN StudentTestAnswersRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
    		String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"StudentTestAnswersRepositoryForRedisImpl-save()",answer.toString());
			
			return e.toString()+"-"+saveErrorAnalyticsMessage;
		}
    
    }

	@Override
	public String delete(String sapidQuestionIdAttemptConcatString) {
    	
    	try {
			hashOperations.delete(STUDENTS_ANSWERS_TABLE_NAME, sapidQuestionIdAttemptConcatString);
			return "";
    	} catch (Exception e) {
    		//System.out.println("IN StudentTestAnswersRepositoryForRedisImpl delete() catch got error : ");
    		e.printStackTrace();
    		String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",sapidQuestionIdAttemptConcatString,e,"StudentTestAnswersRepositoryForRedisImpl-delete()",sapidQuestionIdAttemptConcatString);
			
    		return e.toString()+"-"+saveErrorAnalyticsMessage;
    	}
    
    }

	@Override
	public StudentQuestionResponseBean findBySapidQuestionIdAttemptConcatString(String sapidQuestionIdAttemptConcatString) {
		
		return 	hashOperations.get(STUDENTS_ANSWERS_TABLE_NAME, sapidQuestionIdAttemptConcatString);
	}

}
