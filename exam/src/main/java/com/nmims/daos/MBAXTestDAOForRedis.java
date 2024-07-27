package com.nmims.daos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.ExamAnalyticsObject;
import com.nmims.beans.FlagBean;
import com.nmims.beans.GetAnswersFromRedisByStudentsTestDetailsResponseExamBean;
import com.nmims.beans.GetTestQuestionsFromRedisByTestIdResponseExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.services.RedisResultsStoreService;

@Service("mbaxTestDaoForRedis")
public class MBAXTestDAOForRedis {


	private static final Logger logger = LoggerFactory.getLogger(MBAXTestDAOForRedis.class);
	
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	RedisResultsStoreService redisResultsStoreService;
	
	private static final String apiPath ="timeline/api/mbax/ia/test/getTestQuestionsFromRedisByTestId";

	private static final String apiPathForAnswersFromRedis ="timeline/api/mbax/ia/studentTest/getAnswersFromRedisByStudentsTestDetails";

	private static final String apiPathForMoveRedisTestAnswersToDB ="timeline/api/mbax/ia/studentTest/moveRedisTestAnswersToDB";
	
	public List<TestQuestionExamBean> findAllTestQuestionsByTestId(Long testId){

		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH + apiPath;
		
		//String url = "http://localhost:"+getPort()+"/" + apiPath;
		
		List<ExamAnalyticsObject> analyticsObject = new ArrayList<>();
		//GetTestQuestionsFromRedisByTestIdResponseBean
		
		GetTestQuestionsFromRedisByTestIdResponseExamBean responseBean = new GetTestQuestionsFromRedisByTestIdResponseExamBean();
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			TestExamBean beanToPostAsParam = new TestExamBean();
			beanToPostAsParam.setId(testId);
			beanToPostAsParam.setTestId(testId);
			
			 responseBean = restTemplate.postForObject(url, beanToPostAsParam, GetTestQuestionsFromRedisByTestIdResponseExamBean.class);
		}catch(Exception e) {
				
				logger.info("\n"+SERVER+": "+new Date()+" IN findAllTestQuestionsByTestId got testId : "+testId+" , Error :  "+e.getMessage());
				
			}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					logger.info("\n"+SERVER+": "+new Date()+" IN findAllTestQuestionsByTestId got testId : "+testId+" , Error :  "+e.getMessage());
					
				}
			 }
		
		if(responseBean.getTestQuestions() !=null && responseBean.getTest() != null && !"Assignment".equalsIgnoreCase(responseBean.getTest().getTestType())) {
			if(! (responseBean.getTest().getMaxQuestnToShow() <= responseBean.getTestQuestions().size()) ) {
				return null;
			}
		}else {
			return null;
		}
	
		return responseBean.getTestQuestions();
	}

	private int getPort() {
		
		if("tomcat2".equalsIgnoreCase(SERVER)) {
			return 8282;
		}else if("tomcat3".equalsIgnoreCase(SERVER)) {
			return 8383;
		}else if("tomcat4".equalsIgnoreCase(SERVER)) {
			return 8484;
		}else if("tomcat5".equalsIgnoreCase(SERVER)) {
			return 8585;
		}else if("tomcat1".equalsIgnoreCase(SERVER)) {
			if(SERVER_PATH.contains("localhost")) {
				return 8080;
			}else {
				return 8181;		
			}
		}else {
			return 8181;
		}
		
	}

	public List<StudentQuestionResponseExamBean> getAnswersFromRedisByStudentsTestDetails(
			StudentsTestDetailsExamBean studentsTestDetails) {

		//GetAnswersFromRedisByStudentsTestDetailsResponseBean
		//getAnswersFromRedisByStudentsTestDetails
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH + apiPathForAnswersFromRedis;
		
		//String url = "http://localhost:"+getPort()+"/" + apiPathForAnswersFromRedis;
		List<StudentQuestionResponseExamBean> answersFromReds= new ArrayList<>();
		
		GetAnswersFromRedisByStudentsTestDetailsResponseExamBean responseBean = new GetAnswersFromRedisByStudentsTestDetailsResponseExamBean();
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			 responseBean = restTemplate.postForObject(url, studentsTestDetails, GetAnswersFromRedisByStudentsTestDetailsResponseExamBean.class);
		}catch(Exception e) {
				
				logger.info("\n"+SERVER+": "+new Date()+" IN getAnswersFromRedisByStudentsTestDetails got sapid : "+studentsTestDetails.getSapid()+" testId: "+studentsTestDetails.getTestId()+" , Error :  "+e.toString());
				
			}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					logger.info("\n"+SERVER+": "+new Date()+" IN getAnswersFromRedisByStudentsTestDetails got sapid : "+studentsTestDetails.getSapid()+" testId: "+studentsTestDetails.getTestId()+" , Error :  "+e.toString());
					
					
				}
			 }
		
		if(responseBean.getAnswersFromReds() !=null) {
			answersFromReds =responseBean.getAnswersFromReds();
		}
	
		return answersFromReds;
	}

	public void hitApiToMoveRedisTestAnswersToDB() {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		
		String url = SERVER_PATH + apiPathForMoveRedisTestAnswersToDB;
		//String url = "http://localhost:8484/" + apiPathForMoveRedisTestAnswersToDB;
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//		
		
		try {
			String responseBean = restTemplate.getForObject(url, String.class);
//			
		}catch(Exception e) {
				
				logger.info("\n"+SERVER+": "+new Date()+" IN hitApiToMoveRedisTestAnswersToDB got Error :  "+e.toString());
				
				//If error in hitting 8484 then hit 8181 as fallback
				try {
					  url = "http://localhost:8181/" + apiPathForMoveRedisTestAnswersToDB;
					
					String responseBean = restTemplate.getForObject(url, String.class);
//				
				} catch (RestClientException e1) {
					// TODO Auto-generated catch block
					
					logger.info("\n"+SERVER+": "+new Date()+" IN hitApiToMoveRedisTestAnswersToDB in catch of retry got Error :  "+e1.toString());
				
					try {
						  url = "http://localhost:8080/" + apiPathForMoveRedisTestAnswersToDB;
						String responseBean = restTemplate.getForObject(url, String.class);
//					
					} catch (RestClientException e2) {
						// TODO Auto-generated catch block
						logger.info("\n"+SERVER+": "+new Date()+" IN hitApiToMoveRedisTestAnswersToDB in catch of retry of 8080 got Error :  "+e2.toString());
					}
				}
			
				
				
			}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					logger.info("\n"+SERVER+": "+new Date()+" IN hitApiToMoveRedisTestAnswersToDB Error :  "+e.toString());
					
					
				}
			 }
		
	
	} 
	
	public boolean checkForFlagValueInCache(String key, String value) {
		//FlagBean keyBean = apiCallToGetFlagValueByKey(key);
		FlagBean keyBean = redisResultsStoreService.getFlagBeanByKey(key);
		
		if(value.equalsIgnoreCase(keyBean.getValue())) {
			return true;
		}
		
		return false;
	}
	
	public FlagBean apiCallToGetFlagValueByKey(String key) {


		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH+"timeline/api/mbax/ia/flag/getByKey";
		//List<TestBean> testsForStudent = new ArrayList<>();
		FlagBean flagBean = new FlagBean();
		flagBean.setKey(key);
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			FlagBean response = restTemplate.postForObject(url,flagBean, FlagBean.class);
			
			return response;
		}catch(Exception e) {
			
		}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
			 }
			return flagBean;
	}
	
	
	/* 

	private static final String TEST_QUESTIONS_TABLE_NAME = "TEST_QUESTIONS";
	
	
    private RedisTemplate<Object, Object> redisTemplate;
    

    private HashOperations<Object, Long, List<TestQuestionBean>> hashOperations;

    private ListOperations listOperations;
    private SetOperations setOperations;


    public MBAXTestDAOForRedis(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

        hashOperations = redisTemplate.opsForHash();
        listOperations = redisTemplate.opsForList();
        setOperations = redisTemplate.opsForSet();
    }
    
    public List<TestQuestionBean> findAllByTestId(Long testId) {
		
		return 	hashOperations.get(TEST_QUESTIONS_TABLE_NAME, testId);
	}
    */
}
