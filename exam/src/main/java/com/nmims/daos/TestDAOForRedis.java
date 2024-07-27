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

@Service("testDaoForRedis")
public class TestDAOForRedis {


	private static final Logger logger = LoggerFactory.getLogger(TestDAOForRedis.class);
	
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	RedisResultsStoreService redisResultsStoreService;
	
	private static final String apiPath ="timeline/api/test/getTestQuestionsFromRedisByTestId";

	private static final String apiPathForAnswersFromRedis ="timeline/api/studentTest/getAnswersFromRedisByStudentsTestDetails";

	private static final String apiPathForMoveRedisTestAnswersToDB ="timeline/api/studentTest/moveRedisTestAnswersToDB";

	private static final String pathToMoveMBAXTestAnswersFromRedisToDB ="timeline/api/mbax/ia/studentTest/moveRedisTestAnswersToDB";
	
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
		
		if(responseBean.getTestQuestions() !=null && responseBean.getTest() != null 
				&& !"Assignment".equalsIgnoreCase(responseBean.getTest().getTestType())
				&& !"Project".equalsIgnoreCase(responseBean.getTest().getTestType())) {
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

		try {
			moveInternalAssessmentAnswersFromRedisToDB(apiPathForMoveRedisTestAnswersToDB);
		}catch (Exception e) {
			// TODO: handle exception
		}

		try {
			moveInternalAssessmentAnswersFromRedisToDB(pathToMoveMBAXTestAnswersFromRedisToDB);
		}catch (Exception e) {
			// TODO: handle exception
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
		String url = SERVER_PATH+"timeline/api/flag/getByKey";
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
	
	/**
	 * Move internal assessment answers from redis to database<br>
	 * Path is the api that is to be hit, currently we hit for mba-wx and mba-x<br>
	 * By default the api his is on 8484
	 * In case of an exception, fallback api is hit on 8181 or 8080 
	 * 
	 * @param path
	 * @return void
	 */
	private void moveInternalAssessmentAnswersFromRedisToDB(String path){
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		String url = SERVER_PATH + path;

		try {
			
			restTemplate.getForObject(url, String.class);
			
		}catch(Exception e) {

			/*
			 * If error in hitting 8484 then hit 8181 as fallback
			 * 
			 */
			logger.info("\n"+SERVER+": "+new Date()+" IN moveInternalAssessmentAnswersFromRedisToDB got "
					+ "Error :  "+e.toString());
			
			try {
				url = "http://localhost:8181/" + path;
				restTemplate.getForObject(url, String.class);
			} catch (RestClientException e1) {
				// TODO Auto-generated catch block

				logger.info("\n"+SERVER+": "+new Date()+" IN moveInternalAssessmentAnswersFromRedisToDB in "
						+ "catch of retry got Error :  "+e1.toString());

				try {
					url = "http://localhost:8080/" + path;
					restTemplate.getForObject(url, String.class);
				} catch (RestClientException e2) {
					// TODO Auto-generated catch block
					logger.info("\n"+SERVER+": "+new Date()+" IN moveInternalAssessmentAnswersFromRedisToDB in "
							+ "catch of retry of 8080 got Error :  "+e2.toString());
				}
			}

		} finally{
			//Important: Close the connect
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.info("\n"+SERVER+": "+new Date()+" IN moveInternalAssessmentAnswersFromRedisToDB Error :  "
						+e.toString());
			}
		}

	} 

	/* 

	private static final String TEST_QUESTIONS_TABLE_NAME = "TEST_QUESTIONS";
	
	
    private RedisTemplate<Object, Object> redisTemplate;
    

    private HashOperations<Object, Long, List<TestQuestionBean>> hashOperations;

    private ListOperations listOperations;
    private SetOperations setOperations;


    public TestDAOForRedis(RedisTemplate<Object, Object> redisTemplate) {
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
