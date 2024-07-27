package com.nmims.timeline.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean;
import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;
import com.nmims.timeline.model.MBAXStudentsTestDetailsBean;
import com.nmims.timeline.scheduler.MBAXTestIAScheduler;
import com.nmims.timeline.service.MBAXTestAnswerService;

@RestController
@RequestMapping("/api/mbax/ia/studentTest")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAXStudentTestController {
	

    private static final Logger logger = LogManager.getLogger(MBAXStudentTestController.class);
    
    /*
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	*/
    

    private MBAXTestAnswerService testAnswerService;

    private MBAXTestIAScheduler testIAScheduler;
    
    public MBAXStudentTestController(MBAXTestAnswerService testAnswerService,
    		MBAXTestIAScheduler testIAScheduler) {
        this.testAnswerService = testAnswerService;
        this.testIAScheduler = testIAScheduler;
    }
    
    @PostMapping("/saveDQAnswerInCache")
    public ResponseEntity<Map<String,String>>  saveDQAnswerInCache(@RequestBody MBAXStudentQuestionResponseBean bean) {

        try {
			logger.info("  : IN saveDQAnswerInCache got sapid : "+bean.getSapid()+" questionId : "+bean.getQuestionId()+" answer : "+bean.getAnswer());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	Map<String,String> response = testAnswerService.saveDQAnswerInCache(bean);
    	String status = response.get("Status");
    	
    	try {
			logger.info("  : IN saveDQAnswerInCache response got sapid : "+bean.getSapid()+" questionId : "+bean.getQuestionId()+" answer : "+bean.getAnswer()+" response : "+response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(status != null && !status.contains("Fail") && !status.contains("TimeOver")) {

			return new ResponseEntity<>(response, HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @PostMapping("/getAnswersFromRedisByStudentsTestDetails")
    public MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisByStudentsTestDetails(@RequestBody MBAXStudentsTestDetailsBean studentsTestDetails) {
    	//System.out.println("IN MBAXStudentTestController getTestQuestionsFromRedisByTestId() called --->");
    	try {
			logger.info("  : IN getAnswersFromRedisByStudentsTestDetails  got sapid : "+studentsTestDetails.getSapid()+" testId : "+studentsTestDetails.getTestId()+" attempt : "+studentsTestDetails.getAttempt());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean response =new MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean();
    	

    	
    	
    	try {
			response = testAnswerService.getAnswersFromRedisByStudentsTestDetails(studentsTestDetails);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	try {
			logger.info("  : IN getAnswersFromRedisByStudentsTestDetails response got sapid : "+studentsTestDetails.getSapid()+" testId : "+studentsTestDetails.getTestId()+" attempt : "+studentsTestDetails.getAttempt()+" response : "+response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return response;
    }
    
    
    @PostMapping("/getAnswersFromRedisBySapidAndTestIdAndAttempt")
    public MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisBySapidAndTestIdAndAttempt(@RequestBody MBAXStudentsTestDetailsBean studentsTestDetails) {
    	//System.out.println("IN MBAXStudentTestController getAnswersFromRedisBySapidAndTestIdAndAttempt() called --->");
    	try {
			logger.info("  : IN getAnswersFromRedisBySapidAndTestIdAndAttempt  got sapid : "+studentsTestDetails.getSapid()+" testId : "+studentsTestDetails.getTestId()+" attempt : "+studentsTestDetails.getAttempt());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean response =new MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean();
    	

    	
    	
    	try {
			response = testAnswerService.getAnswersFromRedisBySapidAndTestIdAndAttempt(studentsTestDetails.getSapid(),studentsTestDetails.getTestId(),studentsTestDetails.getAttempt());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	try {
			logger.info("  : IN getAnswersFromRedisBySapidAndTestIdAndAttempt response got sapid : "+studentsTestDetails.getSapid()+" testId : "+studentsTestDetails.getTestId()+" attempt : "+studentsTestDetails.getAttempt()+" response : "+response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return response;
    }
    
    
    @PostMapping("/deleteAnswersFromRedisBySapidAndTestIdAndAttempt")
    public String deleteAnswersFromRedisBySapidAndTestIdAndAttempt(@RequestBody MBAXStudentQuestionResponseBean answer) {
    	//System.out.println("IN MBAXStudentTestController deleteAnswersFromRedisBySapidAndTestIdAndAttempt() called --->");
    	try {
			logger.info("  : IN deleteAnswersFromRedisBySapidAndTestIdAndAttempt  got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" attempt : "+answer.getAttempt());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	String response ="";

    	
    	
    	try {
			response = testAnswerService.deleteAnswersFromRedisBySapidAndTestIdAndAttempt(answer.getSapid(),answer.getQuestionId(),answer.getAttempt());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	try {
			logger.info("  : IN deleteAnswersFromRedisBySapidAndTestIdAndAttempt response got sapid : "+answer.getSapid()+" getQuesitonId : "+answer.getQuestionId()+" attempt : "+answer.getAttempt()+" response : "+response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return response;
    }
    
    //moveRedisTestAnswersToDB
    @GetMapping("/moveRedisTestAnswersToDB")
    public String moveRedisTestAnswersToDB() {
    	//System.out.println("IN MBAXStudentTestController moveRedisTestAnswersToDB() called --->");
    	try {
			logger.info(" IN moveRedisTestAnswersToDB() ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	    	
    	try {
    		testIAScheduler.moveTestDescriptiveAnswersFromCacheToDB();

	    	return "Success In moveRedisTestAnswersToDB ";
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				logger.info(" IN moveRedisTestAnswersToDB error : "+e1.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	return "Error In moveRedisTestAnswersToDB ";
    	}
    	
    	
    	
    }
    
}
