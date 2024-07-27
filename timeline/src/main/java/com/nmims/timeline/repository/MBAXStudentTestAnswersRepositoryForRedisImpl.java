package com.nmims.timeline.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;
import com.nmims.timeline.service.ErrorAnalyticsService;

@Repository
public class MBAXStudentTestAnswersRepositoryForRedisImpl implements MBAXStudentTestAnswersRepositoryForRedis {

	
	private static final String STUDENTS_ANSWERS_TABLE_NAME = "MBAX_STUDENTS_ANSWERS";
	
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, MBAXStudentQuestionResponseBean> hashOperations;

    //private ListOperations listOperations;
    //private SetOperations setOperations;
    
    private ErrorAnalyticsService errorAnalyticsService;

    public MBAXStudentTestAnswersRepositoryForRedisImpl(RedisTemplate<Object, Object> redisTemplate,
    		ErrorAnalyticsService errorAnalyticsService) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
        //listOperations = redisTemplate.opsForList();
        //setOperations = redisTemplate.opsForSet();
        this.errorAnalyticsService = errorAnalyticsService;
    }
    
	@Override
	public String save(MBAXStudentQuestionResponseBean answer) {
    	
    	try {
			hashOperations.put(STUDENTS_ANSWERS_TABLE_NAME, answer.getSapid()+"-"+answer.getQuestionId()+"-"+answer.getAttempt(), answer);
			return "";
    	} catch (Exception e) {
    		//System.out.println("IN MBAXStudentTestAnswersRepositoryForRedisImpl save() catch got error : ");
    		e.printStackTrace();
    		String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"MBAXStudentTestAnswersRepositoryForRedisImpl-save()",answer.toString());
			
			return e.toString()+"-"+saveErrorAnalyticsMessage;
		}
    
    }

	@Override
	public String delete(String sapidQuestionIdAttemptConcatString) {
    	
    	try {
			hashOperations.delete(STUDENTS_ANSWERS_TABLE_NAME, sapidQuestionIdAttemptConcatString);
			return "";
    	} catch (Exception e) {
    		//System.out.println("IN MBAXStudentTestAnswersRepositoryForRedisImpl delete() catch got error : ");
    		e.printStackTrace();
    		String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",sapidQuestionIdAttemptConcatString,e,"MBAXStudentTestAnswersRepositoryForRedisImpl-delete()",sapidQuestionIdAttemptConcatString);
			
    		return e.toString()+"-"+saveErrorAnalyticsMessage;
    	}
    
    }

	@Override
	public MBAXStudentQuestionResponseBean findBySapidQuestionIdAttemptConcatString(String sapidQuestionIdAttemptConcatString) {
		
		return 	hashOperations.get(STUDENTS_ANSWERS_TABLE_NAME, sapidQuestionIdAttemptConcatString);
	}

}
