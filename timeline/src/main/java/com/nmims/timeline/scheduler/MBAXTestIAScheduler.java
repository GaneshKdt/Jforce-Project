package com.nmims.timeline.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.nmims.timeline.model.MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean;
import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;
import com.nmims.timeline.model.MBAXStudentsTestDetailsBean;
import com.nmims.timeline.repository.MBAXStudentTestAnswersRepository;
import com.nmims.timeline.repository.MBAXStudentTestAnswersRepositoryForRedis;
import com.nmims.timeline.repository.MBAXStudentTestDetailsRepository;
import com.nmims.timeline.repository.MBAXTestExtendedSapidsRepository;
import com.nmims.timeline.repository.MBAXTestRepository;
import com.nmims.timeline.service.ErrorAnalyticsService;
import com.nmims.timeline.service.MBAXTestAnswerService;

@Component
public class MBAXTestIAScheduler {
	
	/*
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	*/
    private static final Logger logger = LogManager.getLogger(MBAXTestIAScheduler.class);

	
	private MBAXStudentTestDetailsRepository studentTestDetailsRespository;
	private MBAXTestRepository testRepository;
	private MBAXTestExtendedSapidsRepository testExtendedSapidsRepository;
	private MBAXStudentTestAnswersRepositoryForRedis studentTestAnswersRepositoryForRedis;
	private MBAXStudentTestAnswersRepository studentTestAnswersRepository;
	
	private MBAXTestAnswerService testAnswerService;
	
	private ErrorAnalyticsService errorAnalyticsService;
	
	public MBAXTestIAScheduler(MBAXStudentTestDetailsRepository studentTestDetailsRespository,
								 MBAXTestRepository testRepository,
								 MBAXTestExtendedSapidsRepository testExtendedSapidsRepository,
								 MBAXStudentTestAnswersRepositoryForRedis studentTestAnswersRepositoryForRedis,
								 MBAXStudentTestAnswersRepository studentTestAnswersRepository,
								 MBAXTestAnswerService testAnswerService,
								 ErrorAnalyticsService errorAnalyticsService) {
		this.studentTestDetailsRespository = studentTestDetailsRespository;
		this.testRepository = testRepository;
		this.testExtendedSapidsRepository= testExtendedSapidsRepository;
		this.studentTestAnswersRepositoryForRedis = studentTestAnswersRepositoryForRedis;
		this.studentTestAnswersRepository = studentTestAnswersRepository;
		
		this.testAnswerService = testAnswerService;
		this.errorAnalyticsService =errorAnalyticsService;
	}
	
	
	//@Scheduled(fixedDelay=2*60*1000)
	public void moveTestDescriptiveAnswersFromCacheToDB(){

		//System.out.println("IN MBAXTestIAScheduler : moveTestDescriptiveAnswersFromCacheToDB scheduler called ");
		 
		 /* if(!"tomcat4".equalsIgnoreCase(SERVER) ||
		     !"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
			 ////System.out.println("Not running MBAXTestIAScheduler : runPlagiarismCheckForTestDescriptiveAnswers scheduler since this is not tomcat4. This is "+SERVER);
			 return; 
			 
		 }*/
		
		
		
		List<MBAXStudentsTestDetailsBean> studentTestDetails = new ArrayList<>();
		try {
			studentTestDetails = studentTestDetailsRespository.getStudentTestDetailsForAnswersMovedToDb();
			//System.out.println("IN MBAXTestIAScheduler : moveTestDescriptiveAnswersFromCacheToDB scheduler called studentTestDetails List size :  ");
			//System.out.println(studentTestDetails!=null ? studentTestDetails.size() : 0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				logger.info(" IN moveTestDescriptiveAnswersFromCacheToDB() catch 1 got error : "+e.toString());
				String saveErrorAnalyticsMessage = errorAnalyticsService.save("test-scheduler","Scheduler",e,"moveTestDescriptiveAnswersFromCacheToDB()-getStudentTestDetailsForAnswersMovedToDb()","NA");
				logger.info(" IN moveTestDescriptiveAnswersFromCacheToDB() catch 1 got saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//ErrorAnalyticsRepository
			
			//send mail of error.
		}
		
		List<MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean> errorList = new ArrayList<>();
		
		for(MBAXStudentsTestDetailsBean studentTestDetail : studentTestDetails ) {

			MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean answersByTestDetails=	testAnswerService.getAnswersFromRedisByStudentsTestDetails(studentTestDetail);
			
			try {
				if("Success".equalsIgnoreCase(answersByTestDetails.getStatus())) {

					List<MBAXStudentQuestionResponseBean> answersFromRedis = answersByTestDetails.getAnswersFromReds();
					
					for(MBAXStudentQuestionResponseBean answer :answersFromRedis ) {
						boolean answerExists = studentTestAnswersRepository.existsBySapidAndTestIdAndAttemptAndQuestionId(answer.getSapid(),answer.getTestId(),answer.getAttempt(),answer.getQuestionId());
						
						//System.out.println(answer.toString());
						//System.out.println("answerExists: "+answerExists);
						
						if(answerExists) {
							 
							MBAXStudentQuestionResponseBean answerFromDB = studentTestAnswersRepository.findFirstBySapidAndTestIdAndAttemptAndQuestionId(answer.getSapid(),answer.getTestId(),answer.getAttempt(),answer.getQuestionId());
							
							//add logic to check timestamp start
							boolean isAnswerFromRedisIsLatest = checkIfAnswerFromRedisIsLatest(answer,answerFromDB);
							//add logic to check timestamp end
							
							if(isAnswerFromRedisIsLatest) {
								answerFromDB.setAnswer(answer.getAnswer());		
								answerFromDB.setLastModifiedDate(answer.getLastModifiedDate());
								studentTestAnswersRepository.save(answerFromDB);
								
							}
							
							
						}else {
							
							studentTestAnswersRepository.save(answer);
						}
					}
					
				}else {
					errorList.add(answersByTestDetails);
				}
				
				studentTestDetail.setAnswersMovedFromCacheToDB("Y");
				studentTestDetailsRespository.save(studentTestDetail);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try {
					logger.info(" IN moveTestDescriptiveAnswersFromCacheToDB() catch 2 got error : "+e.toString());
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test-scheduler","Scheduler",e,"moveTestDescriptiveAnswersFromCacheToDB()-ForLoop_For_studentTestDetail",studentTestDetail.toString());
					logger.info(" IN moveTestDescriptiveAnswersFromCacheToDB() catch 2 got saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				errorList.add(answersByTestDetails);
			}
			
			
		}
		  
	 }


	private boolean checkIfAnswerFromRedisIsLatest(MBAXStudentQuestionResponseBean answerFromRedis,
			MBAXStudentQuestionResponseBean answerFromDB) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date lastModifiedDateInRedis = sdf.parse(answerFromRedis.getLastModifiedDate());
			//System.out.println("IN checkIfAnswerFromRedisIsLatest lastModifiedDateInRedis:  "+lastModifiedDateInRedis);
			Date lastModifiedDateInDB = sdf.parse(answerFromDB.getLastModifiedDate());
			//System.out.println("IN checkIfAnswerFromRedisIsLatest lastModifiedDateInDB:  "+lastModifiedDateInDB);
				
			if(lastModifiedDateInRedis.after(lastModifiedDateInDB)) {
				//System.out.println("IN checkIfAnswerFromRedisIsLatest lastModifiedDateInRedis after lastModifiedDateInDB:  ");
				return true;
			}else {
				//System.out.println("IN checkIfAnswerFromRedisIsLatest lastModifiedDateInRedis after lastModifiedDateInDB:  ");
				return false;
			}
			
			} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}
}
