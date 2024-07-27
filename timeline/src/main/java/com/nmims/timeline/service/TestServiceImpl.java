package com.nmims.timeline.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.nmims.timeline.model.GetTestQuestionsFromRedisByTestIdResponseBean;
import com.nmims.timeline.model.Student;
import com.nmims.timeline.model.TestBean;
import com.nmims.timeline.model.TestQuestionBean;
import com.nmims.timeline.model.TestQuestionConfigBean;
import com.nmims.timeline.model.TestQuestionOptionBean;
import com.nmims.timeline.model.Timebound;
import com.nmims.timeline.repository.StudentRepository;
import com.nmims.timeline.repository.TestQuestionConfigRepository;
import com.nmims.timeline.repository.TestQuestionRepository;
import com.nmims.timeline.repository.TestQuestionRepositoryForRedis;
import com.nmims.timeline.repository.TestQuestionsOptionRepository;
import com.nmims.timeline.repository.TestRepository;
import com.nmims.timeline.repository.TestRepositoryForRedis;
import com.nmims.timeline.repository.TimeboundRepository;
import com.nmims.timeline.repository.UpcomingTestRepositoryForRedis;

@Service
public class TestServiceImpl implements TestService {

	
	
    private TestRepository testRepository;
    private TestRepositoryForRedis testRepositoryForRedis;
    private TestQuestionConfigRepository testQuestionConfigRepository;
    private TestQuestionRepository testQuestionRepository;
    private TestQuestionRepositoryForRedis testQuestionRepositoryForRedis;
    private TestQuestionsOptionRepository testQuestionsOptionRepository;
    private StudentRepository studentRepository;
    private UpcomingTestRepositoryForRedis upcomingTestRepositoryForRedis;
    private TimeboundRepository timeboundRepository;
    
    private static final Logger logger = LogManager.getLogger(TestServiceImpl.class);
    
    public TestServiceImpl(TestRepository testRepository,
    					  TestRepositoryForRedis testRepositoryForRedis,
    					  TestQuestionConfigRepository testQuestionConfigRepository,
    					  TestQuestionRepository testQuestionRepository,
    					  TestQuestionRepositoryForRedis testQuestionRepositoryForRedis,
    					  TestQuestionsOptionRepository testQuestionsOptionRepository,
    					  StudentRepository studentRepository,
    					  UpcomingTestRepositoryForRedis upcomingTestRepositoryForRedis,
    					  TimeboundRepository timeboundRepository) {
    	
        this.testRepository = testRepository;    	
        this.testRepositoryForRedis = testRepositoryForRedis;
        this.testQuestionConfigRepository = testQuestionConfigRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.testQuestionRepositoryForRedis = testQuestionRepositoryForRedis;
        this.testQuestionsOptionRepository = testQuestionsOptionRepository;
        this.studentRepository = studentRepository;
        this.upcomingTestRepositoryForRedis = upcomingTestRepositoryForRedis;
        this.timeboundRepository = timeboundRepository;
        
    }
    
    public List<TestQuestionBean> getAllQuestionsByTestId(Long testId){
    	
    	List<TestQuestionBean> testQuestions = new ArrayList<>();
		try {
			testQuestions = testQuestionRepository.getAllQuestionsByTestId(testId);
			//System.out.println("IN getAllQuestionsByTestId got testQuestions size : "+testQuestions.size());
			for(TestQuestionBean question : testQuestions) {
				//if(question.getType() == 6 || question.getType() == 7) {
				//}
				List<TestQuestionOptionBean> options = testQuestionsOptionRepository.findAllByQuestionIdOrderById(question.getId());

				//System.out.println("IN getAllQuestionsByTestId got options size : "+options.size());
				question.setOptionsList(options);
				
				//TO BE IMPLEMENTED LATER WHEN CASE STUDY GOES LIVE.
				//if(question.getType()==3) {
					//question.setSubQuestionsList(getTestSubQuestions(question.getId()));
				//}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return testQuestions;
    }

	@Override
	public String setTestQuestionsInRedisByTestId(Long testId) {
		
		
		
		//1. Delete Old Data
		
		String deleteErrorMessageForTest = testRepositoryForRedis.delete(testId);

		if(!StringUtils.isBlank(deleteErrorMessageForTest) ) {
			return "Error in deleting test from Redis for testid : "+testId+". Error : "+deleteErrorMessageForTest;
		}
		
		String deleteErrorMessage = testQuestionRepositoryForRedis.delete(testId);

		if(!StringUtils.isBlank(deleteErrorMessage) ) {
			return "Error in deleting testQuestions from Redis for testid : "+testId+". Error : "+deleteErrorMessage;
		}
		
		
		//2. Get Test Questions by testId from mysql
		TestBean test =null;
		try {
			test = testRepository.findFirstById(testId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in getting test from DB for testid : "+testId+". Error : "+e.getMessage();
			
		}



		if(test == null ) {
			return "Error in getting test from DB for testid : "+testId+". Error : Test return null with no error. ";
		}
		
		List<TestQuestionBean> testQuestions = getAllQuestionsByTestId(testId);
	    
		if(testQuestions == null || testQuestions.size() < 1 ) {
			return "Error in getting testQuestions from DB for testid : "+testId;
		}
		
		//Check if testquestions are as per required start.
		if(! (test.getMaxQuestnToShow() <= testQuestions.size()) ) {
			return "Error in saving test to Redis for testid : "+testId+". Error : NoOf testQuestions < MaxQuestnToShow";
		}
		
		List<TestQuestionConfigBean> testConfigs = testQuestionConfigRepository.findAllByTestId(testId);
		
		if(testConfigs != null && testConfigs.size() > 0) {
			int noOfQuestionsByTestConfig = 0;
			Map<Integer,Integer> testQuestionsTypeToNoOfQuestionsMap = new HashMap<>();
			
			for(TestQuestionBean testQuetion: testQuestions) {
				if(testQuestionsTypeToNoOfQuestionsMap.containsKey(testQuetion.getType())) {
					Integer temp =testQuestionsTypeToNoOfQuestionsMap.get(testQuetion.getType());
					testQuestionsTypeToNoOfQuestionsMap.put(testQuetion.getType(), temp+1);
				}else {
					testQuestionsTypeToNoOfQuestionsMap.put(testQuetion.getType(), 1);
				}
				
				//check if options for questions are valid start
				
				if(testQuetion.getType() != 4 && testQuetion.getType() != 8 ) {
					if(testQuetion.getOptionsList().size() < 2) {
						return "Error in saving test to Redis for testid : "+testId+". Error : Options not present for questinId : "+testQuetion.getId();
					}
				}
				
				//check if options for questions are valid end
				
			}
			
			for(TestQuestionConfigBean testConfig : testConfigs ) { 
				noOfQuestionsByTestConfig = noOfQuestionsByTestConfig + testConfig.getMaxNoOfQuestions();
				
				if(testQuestionsTypeToNoOfQuestionsMap.containsKey(testConfig.getType()) && testConfig.getMaxNoOfQuestions() > 0) {
					
					if(testQuestionsTypeToNoOfQuestionsMap.get(testConfig.getType()) < testConfig.getMaxNoOfQuestions() ) {
						return "Error in saving test to Redis for testid : "+testId+". Error : No of testquestions < testconfig questions for type "+testConfig.getType();
					}
				}else {
					return "Error in saving test to Redis for testid : "+testId+". Error : No testquestions for type "+testConfig.getType();
				}
				
			};
			
			if(! (noOfQuestionsByTestConfig <= testQuestions.size()) ) {
				return "Error in saving test to Redis for testid : "+testId+". Error : NoOf testQuestions < noOfQuestionsByTestConfig";
			}
			
		}
		
		
		//Check if testquestions are as per required end.
		
		//3. Add test questions to redis 
		String saveErrorMessageForTest = testRepositoryForRedis.save(test);

		if(!StringUtils.isBlank(saveErrorMessageForTest) ) {
			return "Error in saving test to Redis for testid : "+testId+". Error : "+saveErrorMessageForTest;
		}
		

		
		String saveErrorMessage = testQuestionRepositoryForRedis.save(testQuestions);

		if(!StringUtils.isBlank(saveErrorMessage) ) {
			return "Error in saving testQuestions to Redis for testid : "+testId+". Error : "+saveErrorMessage;
		}
		
		
		
		return "Sucess in setTestQuestionsInRedisByTestId for testId : "+testId;
	}

	@Override
	public GetTestQuestionsFromRedisByTestIdResponseBean getTestQuestionsFromRedisByTestId(Long testId) {

		GetTestQuestionsFromRedisByTestIdResponseBean returnBean = new GetTestQuestionsFromRedisByTestIdResponseBean();
		List<TestQuestionBean> testQuestions = new ArrayList<>();
		TestBean test = new TestBean();
		try {
			test = testRepositoryForRedis.findFirstById(testId);
			testQuestions = testQuestionRepositoryForRedis.findAllByTestId(testId);
			//System.out.println("IN testQuestions size : "+testQuestions.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		returnBean.setTest(test);
		returnBean.setTestQuestions(testQuestions);
		
		return returnBean;
		}

	@Override
	public String setUpcomingTestsPerStudentInRedisByTestId(Long testId) {
		
		//1. get timeboundId by testId
		Timebound timeboundIdBean = timeboundRepository.getTimeboundIdByTestId(testId);
		//System.out.println("IN setUpcomingTestsPerStudentInRedisByTestId() got timeboundId : ");
		//System.out.println(timeboundIdBean !=null ? timeboundIdBean.getId() : 0);
		
		Integer timeboundId = timeboundIdBean.getId();
		
		return setUpcomingTestsPerStudentInRedisByTimeboundId(timeboundId);
		
	}

	@Override
	public List<TestBean> getUpcomingTestsBySapid(String sapid) {
		
		return upcomingTestRepositoryForRedis.getUpcomingTestsBySapid(sapid);
		
	}

	@Override
	public String setUpcomingTestsPerStudentInRedisByTimeboundId(Integer timeboundId) {
		String setUpcomingTestsErrorMessage = "";
		logger.info("TestServiceImpl.setUpcomingTestsPerStudentInRedisByTimeboundId() - START");
		
			//2. get upcoming tests for timeboundid
				List<TestBean> upcomingTestsForAfterLoginPage = testRepository.getUpcomingTestsForAfterLoginPage(timeboundId);
				logger.info("IN setUpcomingTestsPerStudentInRedisByTimeboundId() got upcomingTestsForAfterLoginPage : ");
				logger.info(upcomingTestsForAfterLoginPage !=null ? upcomingTestsForAfterLoginPage.size() : 0);
				
				//3. get list of all students for timeboundid
				List<Student> studentsList = studentRepository.getAllStudentsByTimeboundId(timeboundId);
				logger.info("IN setUpcomingTestsPerStudentInRedisByTimeboundId() got studentsList : ");
				logger.info(studentsList !=null ? studentsList.size() : 0);
				
				
				//4. set tests for each student
				
				for(Student s : studentsList) {
					logger.info("IN setUpcomingTestsPerStudentInRedisByTimeboundId() setting for sapid : "+s.getSapid());
					setUpcomingTestsErrorMessage = setUpcomingTestsErrorMessage + upcomingTestRepositoryForRedis.saveUpcomingTestsForSapid(s.getSapid(),upcomingTestsForAfterLoginPage);
				}
				logger.info("TestServiceImpl.setUpcomingTestsPerStudentInRedisByTimeboundId() - END");
				return setUpcomingTestsErrorMessage;
		
	}
    
    
}
