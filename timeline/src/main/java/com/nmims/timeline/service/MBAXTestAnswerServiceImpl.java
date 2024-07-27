package com.nmims.timeline.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.nmims.timeline.model.MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean;
import com.nmims.timeline.model.MBAXStudentQuestionResponseBean;
import com.nmims.timeline.model.MBAXStudentsTestDetailsBean;
import com.nmims.timeline.model.MBAXTestBean;
import com.nmims.timeline.model.MBAXTestExtendedSapids;
import com.nmims.timeline.repository.MBAXStudentTestAnswersRepository;
import com.nmims.timeline.repository.MBAXStudentTestAnswersRepositoryForRedis;
import com.nmims.timeline.repository.MBAXStudentTestDetailsRepository;
import com.nmims.timeline.repository.MBAXTestExtendedSapidsRepository;
import com.nmims.timeline.repository.MBAXTestRepository;


@Service
public class MBAXTestAnswerServiceImpl implements MBAXTestAnswerService {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = LogManager.getLogger(MBAXTestAnswerServiceImpl.class);

    /*
	@Value( "${SERVER}" )
	private String SERVER;
	*/
	
	private MBAXStudentTestDetailsRepository studentTestDetailsRespository;
	private MBAXTestRepository testRepository;
	private MBAXTestExtendedSapidsRepository testExtendedSapidsRepository;
	private MBAXStudentTestAnswersRepositoryForRedis studentTestAnswersRepositoryForRedis;
	
	private ErrorAnalyticsService errorAnalyticsService;
	
	private MBAXStudentTestAnswersRepository studentTestAnswersRepository;
	
	public MBAXTestAnswerServiceImpl(MBAXStudentTestDetailsRepository studentTestDetailsRespository,
								 MBAXTestRepository testRepository,
								 MBAXTestExtendedSapidsRepository testExtendedSapidsRepository,
								 MBAXStudentTestAnswersRepositoryForRedis studentTestAnswersRepositoryForRedis,
								 ErrorAnalyticsService errorAnalyticsService,
								 MBAXStudentTestAnswersRepository studentTestAnswersRepository) {
		this.studentTestDetailsRespository = studentTestDetailsRespository;
		this.testRepository = testRepository;
		this.testExtendedSapidsRepository= testExtendedSapidsRepository;
		this.studentTestAnswersRepositoryForRedis = studentTestAnswersRepositoryForRedis;
		this.errorAnalyticsService = errorAnalyticsService;
		this.studentTestAnswersRepository = studentTestAnswersRepository;
	}
	
	@Override
	public Map<String, String> saveDQAnswerInCache(MBAXStudentQuestionResponseBean answer) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		try {
			
			String updateAttemptsReaminingTime = updateAttemptsReaminingTime(answer.getSapid(),answer.getTestId());
			if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {
					
				response.put("Status", updateAttemptsReaminingTime);
				return response;
				//return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			//List<MBAXStudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
			
			//int noOfAnswersAlreadySaved = dao.getCountOfAnswersBySapidAndQuestionIdNAttempt(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
			/*
			if(noOfAnswersAlreadySaved == 0) {
				//Do insert
	
				if(answer.getType() == 2) {
					String savedAns=saveType2Answers(answer);
					if("error".equalsIgnoreCase(savedAns)) {
						response.put("Status", "Fail in saveType2Answers ");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else {
					long saved = dao.saveStudentsTestAnswer(answer);
					if(saved==0) {
						response.put("Status", "Fail in saveStudentsTestAnswer");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				
				//update noOfQuestionsAttempted
				boolean updateStudentsTestDetailsNoOfQuestionsAttempted = dao.updateStudentsTestDetailsNoOfQuestionsAttempted(answer.getTestId(),answer.getSapid(),answer.getAttempt(),answer.getQuestionId());
				if(!updateStudentsTestDetailsNoOfQuestionsAttempted) {
					response.put("Status", "Fail in updateStudentsTestDetailsNoOfQuestionsAttempted ");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				//Do update
				if(answer.getType() == 2) {
					//if type is 2 i.e. multiselect delete old answers first
					boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionId(answer.getSapid(), answer.getQuestionId());
					if(!deletedAns) {
						response.put("Status", "Fail deleteStudentsAnswersBySapidQuestionId ");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					String savedAns = saveType2Answers(answer);
					if("error".equalsIgnoreCase(savedAns)) {
						response.put("Status", "Fail saveType2Answers");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					
				}else if(answer.getType() == 1 || answer.getType() == 3 || answer.getType() == 4 || answer.getType() == 5 || answer.getType() == 6 || answer.getType() == 7 || answer.getType() == 8) {
				boolean updated = dao.updateStudentsQuestionResponse(answer);
				if(!updated) {
					response.put("Status", "Fail updateStudentsQuestionResponse ");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else {
					response.put("Status", "Fail type no mentioned ");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					
				}
			}*/
			
			String errorFromRedisAccess = "";
			
			MBAXStudentQuestionResponseBean answerFromRedis = new MBAXStudentQuestionResponseBean() ;
			try {
				answerFromRedis = studentTestAnswersRepositoryForRedis.findBySapidQuestionIdAttemptConcatString(answer.getSapid()+"-"+answer.getQuestionId()+"-"+answer.getAttempt());
			} catch (Exception e2) {
				errorFromRedisAccess = "Error in findBySapidQuestionIdAttemptConcatString : "+e2.toString();
				e2.printStackTrace();


				StringWriter errors = new StringWriter();
				e2.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/m/saveDQAnswerInCache studentTestAnswersRepositoryForRedis.findBySapidQuestionIdAttemptConcatString ";
				String stackTrace = "apiCalled="+ apiCalled + ",data= MBAXStudentQuestionResponseBean: "+answer.toString() +
						",errors=" + errors.toString();
				//dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				//response.put("Status", "Fail");
				response.put("message", stackTrace);
				
				try {
					logger.info(" : IN saveDQAnswerInCache studentTestAnswersRepositoryForRedis.findBySapidQuestionIdAttemptConcatString response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" response : "+response);
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e2,"saveDQAnswerInCache()",answer.toString());
					logger.info(" : IN saveDQAnswerInCache studentTestAnswersRepositoryForRedis.findBySapidQuestionIdAttemptConcatString response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			
			}
			
			String saveAnswerInRedisErrorMessage = "";
			
			String currentFormattedDate = sdf.format(new Date());
			
			if(StringUtils.isBlank(errorFromRedisAccess)){
			
			if(answerFromRedis == null || answerFromRedis.getSapid() == null) {

				answer.setMarks(0);
				answer.setRemark("");
				answer.setIsChecked(0);
				answer.setCreatedDate(currentFormattedDate);
				answer.setLastModifiedDate(currentFormattedDate);
				answer.setCreatedBy(answer.getSapid());
				answer.setLastModifiedBy(answer.getSapid());
				answer.setFacultyId("");
				
				saveAnswerInRedisErrorMessage = studentTestAnswersRepositoryForRedis.save(answer);
				if(!StringUtils.isBlank(saveAnswerInRedisErrorMessage)) {
					//response.put("Status", "Failed");
					//response.put("message", "Error in saving to redis. Error : "+saveAnswerInRedisErrorMessage);
					return saveAnswerToDB(answer,response);
				}
				
				try {
						MBAXStudentsTestDetailsBean studentsTestDetails = studentTestDetailsRespository.findFirstBySapidAndTestIdOrderByIdDesc(answer.getSapid(),answer.getTestId());
						if("not saved".equalsIgnoreCase(answer.getAnswerSavedStatus())) {
							studentsTestDetails.setNoOfQuestionsAttempted((studentsTestDetails.getNoOfQuestionsAttempted()+1));
						}
						studentsTestDetails.setNoOfAnswersInCache((studentsTestDetails.getNoOfAnswersInCache()+1));
						studentTestDetailsRespository.save(studentsTestDetails);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//response.put("Status", "Failed");
					response.put("message", "Error in Updating NoOfQuestionsAttempted . Error : "+e.toString());
					//return response;
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"saveDQAnswerInCache()",answer.toString());
					logger.info(" : IN saveDQAnswerInCache error in studentTestDetailsRespository.save()  sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				}
				
			}else {
				
				answerFromRedis.setAnswer(answer.getAnswer());
				answerFromRedis.setLastModifiedDate(currentFormattedDate);
				
				saveAnswerInRedisErrorMessage = studentTestAnswersRepositoryForRedis.save(answerFromRedis);
				if(!StringUtils.isBlank(saveAnswerInRedisErrorMessage)) {
					//response.put("Status", "Failed");
					//response.put("message", "Error in saving to redis. Error : "+saveAnswerInRedisErrorMessage);
					return saveAnswerToDB(answer,response);
				}
			}
			
		}//end of if StringUtils.isBlank(errorFromRedisAccess)
		else {
			
			response = saveAnswerToDB(answer,response);
			
		}//end of else StringUtils.isBlank(errorFromRedisAccess)
			
			
			/*
			//update currentQuestion
			boolean updateStudentsTestDetailsCurrentQuestion = dao.updateStudentsTestDetailsCurrentQuestion(answer.getQuestionId(),answer.getTestId(),answer.getSapid(),answer.getAttempt());
			if(!updateStudentsTestDetailsCurrentQuestion) {
				response.put("Status", "Fail updateStudentsTestDetailsCurrentQuestion");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}*/
			
			try {
				MBAXStudentsTestDetailsBean studentsTestDetails = studentTestDetailsRespository.findFirstBySapidAndTestIdOrderByIdDesc(answer.getSapid(),answer.getTestId());
				studentsTestDetails.setCurrentQuestion(answer.getQuestionId().intValue());
				studentsTestDetails.setAnswersMovedFromCacheToDB("N");
				studentTestDetailsRespository.save(studentsTestDetails);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//response.put("Status", "Failed");
				response.put("message", "Error in Updating NoOfQuestionsAttempted . Error : "+e.toString());
				//return response;

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/m/saveDQAnswerInCache";
				String stackTrace = "apiCalled="+ apiCalled + ",data= MBAXStudentQuestionResponseBean: "+answer.toString() +
						",errors=" + errors.toString();
				//dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				//response.put("Status", "Fail");
				response.put("message", stackTrace);
				
				try {
					logger.info(" : IN saveDQAnswerInCache catch 2 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" response : "+response);
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"saveDQAnswerInCache()",answer.toString());
					logger.info(" : IN saveDQAnswerInCache catch 2 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
			
			
			
			response.put("Status", "Success");
			return response;
			//return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}catch(Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "studentTestController/m/saveDQAnswerInCache";
			String stackTrace = "apiCalled="+ apiCalled + ",data= MBAXStudentQuestionResponseBean: "+answer.toString() +
					",errors=" + errors.toString();
			//dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
			response.put("Status", "Fail");
			response.put("message", stackTrace);
			
			try {
				logger.info(" : IN saveDQAnswerInCache catch 1 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" response : "+response);
				String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"saveDQAnswerInCache()",answer.toString());
				logger.info(" : IN saveDQAnswerInCache catch 1 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return response;
			//return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private HashMap<String, String> saveAnswerToDB(MBAXStudentQuestionResponseBean answer,
			HashMap<String, String> response) {
		
		//System.out.println("In saveAnswerToDB() ");
		//System.out.println(answer.toString());
		

		try {
			String currentFormattedDate = sdf.format(new Date());
			
			
			boolean answerExists = studentTestAnswersRepository.existsBySapidAndTestIdAndAttemptAndQuestionId(answer.getSapid(),answer.getTestId(),answer.getAttempt(),answer.getQuestionId());
			
			//System.out.println("answerExists In DB: "+answerExists);
			 
			if(answerExists) {
				 
				MBAXStudentQuestionResponseBean answerFromDB = studentTestAnswersRepository.findFirstBySapidAndTestIdAndAttemptAndQuestionId(answer.getSapid(),answer.getTestId(),answer.getAttempt(),answer.getQuestionId());
				 
				answerFromDB.setAnswer(answer.getAnswer());
				answerFromDB.setLastModifiedDate(currentFormattedDate);
				
				studentTestAnswersRepository.save(answerFromDB);
				
			}else {
				 
				answer.setMarks(0);
				answer.setRemark("");
				answer.setIsChecked(0);
				answer.setCreatedDate(currentFormattedDate);
				answer.setLastModifiedDate(currentFormattedDate);
				
				answer.setCreatedBy(answer.getSapid());
				answer.setLastModifiedBy(answer.getSapid());
				answer.setFacultyId("");

				studentTestAnswersRepository.save(answer);
				
				
				try {
					if("not saved".equalsIgnoreCase(answer.getAnswerSavedStatus())) {
						MBAXStudentsTestDetailsBean studentsTestDetails = studentTestDetailsRespository.findFirstBySapidAndTestIdOrderByIdDesc(answer.getSapid(),answer.getTestId());
						studentsTestDetails.setNoOfQuestionsAttempted((studentsTestDetails.getNoOfQuestionsAttempted()+1));
						studentTestDetailsRespository.save(studentsTestDetails);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//response.put("Status", "Failed");
					response.put("message", "Error in Updating NoOfQuestionsAttempted . Error : "+e.toString());
					//return response;
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"saveDQAnswerInCache()",answer.toString());
					logger.info(" : IN saveAnswerToDB() error in studentTestDetailsRespository.save()  sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				}
			 
			}
			
			try {
				MBAXStudentsTestDetailsBean studentsTestDetails = studentTestDetailsRespository.findFirstBySapidAndTestIdOrderByIdDesc(answer.getSapid(),answer.getTestId());
				studentsTestDetails.setCurrentQuestion(answer.getQuestionId().intValue());
				//studentsTestDetails.setAnswersMovedFromCacheToDB("N");
				studentTestDetailsRespository.save(studentsTestDetails);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//response.put("Status", "Failed");
				response.put("message", "Error in Updating NoOfQuestionsAttempted . Error : "+e.toString());
				//return response;

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/m/saveDQAnswerInCache";
				String stackTrace = "apiCalled="+ apiCalled + ",data= MBAXStudentQuestionResponseBean: "+answer.toString() +
						",errors=" + errors.toString();
				//dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				//response.put("Status", "Fail");
				response.put("message", stackTrace);
				
				try {
					logger.info(" : IN saveDQAnswerInCache catch 2 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" response : "+response);
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",answer.getSapid(),e,"saveDQAnswerInCache()",answer.toString());
					logger.info(" : IN saveDQAnswerInCache catch 2 response got sapid : "+answer.getSapid()+" questionId : "+answer.getQuestionId()+" answer : "+answer.getAnswer()+" saveErrorAnalyticsMessage : "+saveErrorAnalyticsMessage);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
			
			
			
			response.put("Status", "Success");
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("Status", "Failed");
			response.put("message", "Error in saving to redis. Error : "+e.toString());
			
		}
	
		
		
		return response;
	}

	private String updateAttemptsReaminingTime(String sapid, Long testId) {

		MBAXStudentsTestDetailsBean studentsTestDetails =  studentTestDetailsRespository.findFirstBySapidAndTestIdOrderByIdDesc(sapid,testId);
		MBAXTestBean test = testRepository.findFirstById(testId);
		test = updateStartEndTimeIfExtended(test,sapid);
		
		Integer duration = test.getDuration();
		int remainingTime;
		
		//get time left im min
		try {
			String startDateTime = studentsTestDetails.getTestStartedOn();
			//String endDateTime = studentsTestDetails.getTestEndedOn();
			
			String testEndDateTimeString = test.getEndDate().replace('T', ' ');
			
			if(!StringUtils.isBlank(test.getExtendedEndTime())) {
				 testEndDateTimeString = test.getExtendedEndTime().replace('T', ' ');
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sDateTime = sdf.parse(startDateTime);
			Date testEndDateTime = sdf.parse(testEndDateTimeString);
			Date cDateTime = new Date();
			Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
			
			//adding buffer to enddate to avoid saving error
			Date eDateTimeWithBuffer = addMinutesToDate(5,eDateTime);
			//Date testEndDateTimeWithBuffer = addMinutesToDate(5,testEndDateTime);
			
			if(cDateTime.before(eDateTimeWithBuffer)) {
				 //remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);
				return "";
				
			}else {
				return "TimeOver! Your Test Was Started at "+startDateTime+". Test EndTime Was "+eDateTime+". Current Time : "+cDateTime;
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//logger.info("\n: "+" IN updateAttemptsReaminingTime  sapId:  "+sapId+" testId : "+testId+" Error : "+e.getMessage());
			
			return "Error in calculating remining time.";
		}
		
		//update into db
		/*
		boolean updateRemainingTIme = dao.updateStudentsTestDetailsRemainingTime(remainingTime, studentsTestDetails.getId());
		if(updateRemainingTIme) {
			return "";
		}else {

			return "Error in updateing remaining time to db.";
		}*/
		
		
	}

	private MBAXTestBean updateStartEndTimeIfExtended(MBAXTestBean test, String sapid) {

		MBAXTestExtendedSapids extendedTime = testExtendedSapidsRepository.findFirstBySapidAndTestId(sapid,test.getId());
		
		if(extendedTime != null) {
			test.setExtendedStartTime(extendedTime.getExtendedStartTime());
			test.setExtendedEndTime(extendedTime.getExtendedEndTime());
			//System.out.println("IN updateStartEndTimeIfExtended got : \nstartDate : "+test.getExtendedStartTime()+" \nEnddate : "+test.getExtendedEndTime());
		}
		
		
		return test;
	}

	private  Date addMinutesToDate(int minutes, Date beforeTime){
	    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

	    long curTimeInMs = beforeTime.getTime();
	    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
	    return afterAddingMins;
	}

	@Override
	public MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisByStudentsTestDetails(
			MBAXStudentsTestDetailsBean studentsTestDetails) {
		MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean response = new MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean();
		List<MBAXStudentQuestionResponseBean> answersFromReds = new ArrayList<>(); 
		String errorMessage ="";
		String status ="Success";
		
		try {
			String[] questionIds=studentsTestDetails.getTestQuestions().split(",",-1);
			//System.out.println("questionIds: "+questionIds.toString());

			for(String questionId : questionIds){
				//System.out.println("questionId: "+questionIds);

				try {
					MBAXStudentQuestionResponseBean tempBean =studentTestAnswersRepositoryForRedis
							.findBySapidQuestionIdAttemptConcatString(studentsTestDetails.getSapid()+"-"+questionId+"-"+studentsTestDetails.getAttempt());
					//System.out.println("tempBean: "+tempBean);

					if(tempBean !=null && tempBean.getSapid() !=null) {
						//System.out.println("tempBean.toString(): "+tempBean.toString());
						answersFromReds.add(tempBean);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					errorMessage = errorMessage+" Error in getting answersFromRedis "+studentsTestDetails.getSapid()+"-"+questionId+"-"+studentsTestDetails.getAttempt()+", Error : "+e.toString();
					status = "Failed";
					String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",studentsTestDetails.getSapid(),e,"getAnswersFromRedisByStudentsTestDetails()"," answersFromRedis "+studentsTestDetails.getSapid()+"-"+questionId+"-"+studentsTestDetails.getAttempt());
					
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMessage = errorMessage+" Error in getting answersFromRedis "+studentsTestDetails.getSapid()+"--"+studentsTestDetails.getAttempt()+", Error : "+e.toString();
			status = "Failed";
			String saveErrorAnalyticsMessage = errorAnalyticsService.save("test",studentsTestDetails.getSapid(),e,"getAnswersFromRedisByStudentsTestDetails()"," Error in getting answersFromRedis "+studentsTestDetails.getSapid()+"--"+studentsTestDetails.getAttempt());
			
		}
		
		response.setStatus(status);
		response.setMessage(errorMessage);
		response.setAnswersFromReds(answersFromReds);
		return response;
	}

	@Override
	public MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean getAnswersFromRedisBySapidAndTestIdAndAttempt(
			String sapid, Long testId, int attempt) {
		
		MBAXStudentsTestDetailsBean bean = studentTestDetailsRespository.findFirstBySapidAndTestIdAndAttemptOrderByIdDesc(sapid,testId,attempt);
		
		return getAnswersFromRedisByStudentsTestDetails(bean);
	}

	@Override
	public String deleteAnswersFromRedisBySapidAndTestIdAndAttempt(String sapid, Long questionId, int attempt) {
		return studentTestAnswersRepositoryForRedis.delete(sapid+"-"+questionId+"-"+attempt);
	}
}
