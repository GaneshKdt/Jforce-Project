package com.nmims.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.ViewTestDetailsForStudentsAPIResponse;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.MailSender;
import com.nmims.services.IATestService;
import com.nmims.services.StudentService;
import com.nmims.services.impl.StudentTestService;

@RestController
@RequestMapping("m")
public class StudentTestRestController extends BaseController {
	
	
private static final Logger logger = LoggerFactory.getLogger(StudentTestRestController.class);
	
	
	private static final String COPY_CASE_REMARK = "Marked For Copy Case.";
	private List<String> programList;
	private List<String> subjectList;
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${IA_ASSIGNMENT_FILES_PATH}" )
	private String IA_ASSIGNMENT_FILES_PATH;

	@Value("${AWS_IA_FILE_BUCKET}")
	private String awsIAFileBucket;
	
	@Value("${LEAD_FINAL_ASSESSMENT_TEST_IDS}")
	private List<Long> LEAD_FINAL_ASSESSMENT_TEST_IDS;
	
    @Autowired
    private AmazonS3Helper amazonS3Helper;
    
	//private String TEST_ANSWER_BASE_PATH= "E:/TESTASSIGNMENTS_ANSWER_FILES/";
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Autowired
	StudentService studentService;

	@Autowired
	private IATestService iaTestService;
	
	@Autowired 
	StudentTestService studentTestService;
	
	@Autowired
	AuditTrailsDAO auditDao;
	
	private static final int BUFFER_SIZE = 4096;


	private static final int MINS_BUFFER_TO_START_IA = 2;
	
	
	
	@PostMapping(path = "/viewTestsForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<TestExamBean>> m_viewTestsForStudent(@RequestBody StudentExamBean student){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<TestExamBean> testsList = getTestDataForStudent(student.getSapid());
		return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
	}

	
	@PostMapping(path = "/viewTestDetailsForStudents", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<ViewTestDetailsForStudentsAPIResponse> m_viewTestDetailsForStudents(@RequestParam("sapId") String userId,
																	   @RequestParam("id") Long id,
																	   @RequestParam("message") String message){
																
		
		ViewTestDetailsForStudentsAPIResponse responseBean =  getViewTestDetailsForStudentsAPIResponse(userId,id,message);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		return new ResponseEntity<ViewTestDetailsForStudentsAPIResponse>(responseBean,headers, HttpStatus.OK);	
	}
	
	
	//code to addStudentsQuestionResponse start
		@PostMapping(path = "/addStudentsQuestionResponse", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> m_addStudentsQuestionResponse(@RequestBody StudentQuestionResponseExamBean answer){
			
			logger.info("\n"+SERVER+": "+new Date()+" IN m_addStudentsQuestionResponse got sapid :  "+answer.getSapid()+" questionId : "+answer.getQuestionId()+". answer : "+answer.getAnswer());
			
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			TestDAO dao = (TestDAO)act.getBean("testDao");
			HashMap<String,String> response = new HashMap<>();
			try {
				
				String updateAttemptsReaminingTime = updateAttemptsReaminingTime(answer.getSapid(),answer.getTestId());
				if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {


						
					response.put("Status", updateAttemptsReaminingTime);
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				//List<StudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
				
				int noOfAnswersAlreadySaved = dao.getCountOfAnswersBySapidAndQuestionIdNAttempt(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
				
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
				}
				//update currentQuestion
				boolean updateStudentsTestDetailsCurrentQuestion = dao.updateStudentsTestDetailsCurrentQuestion(answer.getQuestionId(),answer.getTestId(),answer.getSapid(),answer.getAttempt());
				if(!updateStudentsTestDetailsCurrentQuestion) {
					response.put("Status", "Fail updateStudentsTestDetailsCurrentQuestion");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				response.put("Status", "Success");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}catch(Exception e) {


				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/m/addStudentsQuestionResponse";
				String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+answer.toString() +
						",errors=" + errors.toString();
				dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		//code to addStudentsQuestionResponse end
		
		
		//code to saveStudentsTestDetails start
		@PostMapping(path = "/saveStudentsTestDetails", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> m_saveStudentsTestDetails(@RequestBody StudentsTestDetailsExamBean bean){
			


			logger.info("\n"+SERVER+": "+new Date()+" IN m_saveStudentsTestDetails got sapid :  "+bean.getSapid()+" testId : "+bean.getTestId());
			
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				TestDAO dao = (TestDAO)act.getBean("testDao");
				HashMap<String,String> response = new HashMap<>();
				try {
					StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(bean.getSapid(), bean.getTestId());
				
					if(studentsTestDetails.getId() != null) {
						//Do update
						studentsTestDetails.setAttempt(studentsTestDetails.getAttempt());
						studentsTestDetails.setTestCompleted("Y");
						studentsTestDetails.setLastModifiedBy(bean.getSapid());
						studentsTestDetails.setScore(0);
						studentsTestDetails.setTestEndedStatus(bean.getTestEndedStatus());
						
						/* Conmmented on 18 feb to not calculate score here
						try {
							studentsTestDetails.setScore(dao.caluclateTestScore(bean.getSapid(),bean.getTestId()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							studentsTestDetails.setScore(0);
						}
						*/
						
						boolean updated = dao.updateStudentsTestDetails(studentsTestDetails);
						if(!updated) {
							response.put("Status", "Fail");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						

						//Send mail after test is completed start
						try {
								MailSender mailSender = (MailSender)act.getBean("mailer");
								StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
								StudentExamBean student = sMarksDao.getSingleStudentsData(bean.getSapid());
								TestExamBean test = dao.getTestById(bean.getTestId());
								StudentsTestDetailsExamBean studentsTestDetailsForMail =  dao.getStudentsTestDetailsBySapidAndTestId(bean.getSapid(), bean.getTestId());
								
								mailSender.sendTestEndedEmail(student,test,studentsTestDetailsForMail);
							
						} catch (Exception e) {
							//
							//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
							logger.info("\n"+SERVER+": "+"IN Send mail error got sapid: "+studentsTestDetails.getSapid()+" testId: "+bean.getId()+" Error: "+e.getMessage());
							
						}
						//Send mail after test is completed end
						
						
					}else {			
						response.put("Status", "Fail");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					response.put("Status", "Success");
					
			
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				}catch(Exception e) {


					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String apiCalled = "studentTestController/m/saveStudentsTestDetails";
					String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ bean.getTestId()+ 
							",errors=" + errors.toString();
					dao.setObjectAndCallLogError(stackTrace,bean.getSapid());
					response.put("Status", "Fail");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
		}
		//code to saveStudentsTestDetails end
		
		
		@PostMapping(path = "/getTestDataForTODO", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<List<TestExamBean>> getTestDataForTODO(@RequestBody TestExamBean testBean){
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			List<TestExamBean> testsList = getTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
			return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
		}
		
		@PostMapping(path = "/getIABySapIdNTimeBoundIds", consumes = "application/json", produces = "application/json")
		public ResponseEntity<HashMap<String,List<TestExamBean>>> getIABySapIdNTimeBoundIds(@RequestBody TestExamBean bean){
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HashMap<String,List<TestExamBean>> response = new HashMap<>();
			
			try {

				List<TestExamBean> testsForStudent = iaTestService.getAllLiveTestsBySapId(bean.getUserId());
				response.put("testsForStudent", testsForStudent);
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		//getIABySapIdNTimeBoundIds end
		
		
		@PostMapping(path = "/getDueTestDataForTODO", consumes = "application/json", produces = "application/json")
		public ResponseEntity<List<TestExamBean>> getDueTestDataForTODO(@RequestBody TestExamBean testBean){
			

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			List<TestExamBean> testsList = getDueTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
			return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
		}
		
		
		@PostMapping(path = "/getPendingTestDataForTODO", consumes = "application/json", produces = "application/json")
		public ResponseEntity<List<TestExamBean>> getPendingTestDataForTODO(@RequestBody TestExamBean testBean){
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			List<TestExamBean> testsList = getPendingTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
			return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
		}
		
		
		@PostMapping(path = "/getFinishedTestDataForTODO", consumes = "application/json", produces = "application/json")
		public ResponseEntity<List<TestExamBean>> getFinishedTestDataForTODO(@RequestBody TestExamBean testBean){
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			List<TestExamBean> testsList = getFinishedTestDataForStudentTODO(testBean.getId(),testBean.getSapid());
			return new ResponseEntity<>(testsList,headers, HttpStatus.OK);	
		}
		
		
		public List<TestExamBean> getFinishedTestDataForStudentTODO(Long id, String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			// get all test for the subjects alongwith students attempt details.
							
			List<TestExamBean> attemptedTestsBySapidNSubject = dao.getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
			
			


			
					


			
			return attemptedTestsBySapidNSubject;
			
		}
		
		@PostMapping(path = "/uploadTestAnswerFile", produces = "application/json")
		public ResponseEntity<HashMap<String,String>> uploadTestAnswerFile(MultipartHttpServletRequest request){
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			TestDAO dao = (TestDAO)act.getBean("testDao");
			String testId= (String)request.getParameter("testId");
			String userId= (String)request.getParameter("userId");

			HashMap<String,String> response = new HashMap<>();
			
			String errorMessage = "";
			String returnLink="";
			Iterator<String> it = request.getFileNames();
			
	        while (it.hasNext()) {
	        	
	            String uploadFile = it.next();
	            MultipartFile file = request.getFile(uploadFile);

	            returnLink = uploadTestAnsAssignmentToAWS(file,testId,userId);
	            
	        }

			if(StringUtils.isBlank(returnLink) || 
	        		Pattern.compile(Pattern.quote("failed to upload"), Pattern.CASE_INSENSITIVE).matcher(returnLink).find() ) {

				errorMessage ="Error";
			}
			
	        if("Error".equalsIgnoreCase(errorMessage)) {
				response.put("Status", "Error");
				response.put("errroMessage", "Error in uploading answer file.");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	        
			else {
				response.put("Status", "Success");
				response.put("imageUrl", returnLink);
				response.put("successMessage", errorMessage);
				return new ResponseEntity<>(response,headers, HttpStatus.OK);	
			}
		}
		
		//log auto save descriptive start
		@PostMapping(path = "/logAutoSaveApiHit", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> logAutoSaveApiHit(@RequestBody StudentQuestionResponseExamBean answer){
			
			logger.info("\n"+SERVER+": "+new Date()+" IN logAutoSaveApiHit got sapid :  "+answer.getSapid()+" answer : "+answer.getQuestionId());


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HashMap<String,String> response = new HashMap<>();
			response.put("Status", "Success");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			
		}
	//log auto save descriptive end
		
		
		@PostMapping(path = "/addStudentsQuestionResponseForLeads", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> m_addStudentsQuestionResponseForLeads(@RequestBody StudentQuestionResponseExamBean answer){
			
			logger.info("\n"+SERVER+": "+new Date()+" IN m_addStudentsQuestionResponseForLeads got sapid :  "+answer.getSapid()+" questionId : "+answer.getQuestionId()+". answer : "+answer.getAnswer());
			
			


			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			TestDAO dao = (TestDAO)act.getBean("testDao");
			HashMap<String,String> response = new HashMap<>();
			try {
				
				String updateAttemptsReaminingTime = updateAttemptsReaminingTimeForLeads(answer.getSapid(),answer.getTestId());
				if(!StringUtils.isBlank(updateAttemptsReaminingTime)) {

					response.put("Status", updateAttemptsReaminingTime);
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				//List<StudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
				
				int noOfAnswersAlreadySaved = dao.getCountOfAnswersBySapidAndQuestionIdNAttemptForLeads(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
				
				if(noOfAnswersAlreadySaved == 0) {
					//Do insert
		
					if(answer.getType() == 2) {
						String savedAns=saveType2AnswersForLeads(answer);
						if("error".equalsIgnoreCase(savedAns)) {
							response.put("Status", "Fail in saveType2Answers ");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}else {
						long saved = dao.saveStudentsTestAnswerForLeads(answer);
						if(saved==0) {
							response.put("Status", "Fail in saveStudentsTestAnswer");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
					
					//update noOfQuestionsAttempted
					boolean updateStudentsTestDetailsNoOfQuestionsAttempted = dao.updateStudentsTestDetailsNoOfQuestionsAttemptedForLeads(answer.getTestId(),answer.getSapid(),answer.getAttempt(),answer.getQuestionId());
					if(!updateStudentsTestDetailsNoOfQuestionsAttempted) {
						response.put("Status", "Fail in updateStudentsTestDetailsNoOfQuestionsAttempted ");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else {
					//Do update
					if(answer.getType() == 2) {
						//if type is 2 i.e. multiselect delete old answers first
						boolean deletedAns = dao.deleteStudentsAnswersBySapidQuestionIdForLeads(answer.getSapid(), answer.getQuestionId());
						if(!deletedAns) {
							response.put("Status", "Fail deleteStudentsAnswersBySapidQuestionId ");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						String savedAns = saveType2AnswersForLeads(answer);
						if("error".equalsIgnoreCase(savedAns)) {
							response.put("Status", "Fail saveType2Answers");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						
					}else if(answer.getType() == 1 || answer.getType() == 3 || answer.getType() == 4 || answer.getType() == 5 || answer.getType() == 6 || answer.getType() == 7 || answer.getType() == 8) {
					boolean updated = dao.updateStudentsQuestionResponseForLeads(answer);
					if(!updated) {
						response.put("Status", "Fail updateStudentsQuestionResponse ");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}else {
						response.put("Status", "Fail type no mentioned ");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						
					}
				}
				//update currentQuestion
				boolean updateStudentsTestDetailsCurrentQuestion = dao.updateStudentsTestDetailsCurrentQuestionForLeads(answer.getQuestionId(),answer.getTestId(),answer.getSapid(),answer.getAttempt());
				if(!updateStudentsTestDetailsCurrentQuestion) {
					response.put("Status", "Fail updateStudentsTestDetailsCurrentQuestion");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				response.put("Status", "Success");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}catch(Exception e) {

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "studentTestController/m/addStudentsQuestionResponse";
				String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+answer.toString() +
						",errors=" + errors.toString();
				dao.setObjectAndCallLogError(stackTrace,answer.getSapid());
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		
		public String saveType2AnswersForLeads(StudentQuestionResponseExamBean answer) {
			try {
				TestDAO dao = (TestDAO)act.getBean("testDao");
				
				String[] studentsAnswers = answer.getAnswer().split("~",-1);
				//insert all answers onebyone
				for(int i = 0; i<studentsAnswers.length;i++) {

					answer.setAnswer(studentsAnswers[i]);
					long saved = dao.saveStudentsTestAnswerForLeads(answer);
					if(saved==0) {
						return "error";
					}
				}
				return "success";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				

				return "error";
			}
		}
		public String updateAttemptsReaminingTimeForLeads(String sapId, Long testId) {

			TestDAO dao = (TestDAO)act.getBean("testDao");
			StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(sapId,testId);
			TestExamBean test = dao.getTestByIdForLeads(testId);
			//test = updateStartEndTimeIfExtended(test,sapId);
			
			Integer duration = test.getDuration();
			int remainingTime;
			
			//get time left im min
			try {
				String startDateTime = studentsTestDetails.getTestStartedOn();
				//String endDateTime = studentsTestDetails.getTestEndedOn();
				
				String testEndDateTimeString = test.getEndDate().replace('T', ' ');
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDateTime = sdf.parse(startDateTime);
				Date testEndDateTime = sdf.parse(testEndDateTimeString);
				Date cDateTime = new Date();
				Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
				
				//adding buffer to enddate to avoid saving error
				Date eDateTimeWithBuffer = addMinutesToDate(5,eDateTime);
				//Date testEndDateTimeWithBuffer = addMinutesToDate(5,testEndDateTime);
				

				if(cDateTime.before(eDateTimeWithBuffer)) {
					 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);

				}else {


					return "TimeOver! Your Test Was Started at "+startDateTime+". Test EndTime Was "+eDateTime+". Current Time : "+cDateTime;
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN updateAttemptsReaminingTime  sapId:  "+sapId+" testId : "+testId+" Error : "+e.getMessage());
				
				return "Error in calculating remining time.";
			}
			
			//update into db
			boolean updateRemainingTIme = dao.updateStudentsTestDetailsRemainingTimeForLeads(remainingTime, studentsTestDetails.getId());
			if(updateRemainingTIme) {
				return "";
			}else {

				return "Error in updateing remaining time to db.";
			}
			
			
		}
		
		
		@PostMapping(path = "/saveStudentsTestDetailsForLeads", consumes = "application/json", produces = "application/json; charset=UTF-8")
		public ResponseEntity<HashMap<String,String>> m_saveStudentsTestDetailsForLeads(@RequestBody StudentsTestDetailsExamBean bean){
			

			logger.info("\n"+SERVER+": "+new Date()+" IN m_saveStudentsTestDetailsForLeads got sapid :  "+bean.getSapid()+" testId : "+bean.getTestId());
			
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				TestDAO dao = (TestDAO)act.getBean("testDao");
				HashMap<String,String> response = new HashMap<>();
				try {
					StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(bean.getSapid(), bean.getTestId());
				
					if(studentsTestDetails.getId() != null) {
						//Do update
						studentsTestDetails.setAttempt(studentsTestDetails.getAttempt());
						studentsTestDetails.setTestCompleted("Y");
						studentsTestDetails.setLastModifiedBy(bean.getSapid());
						studentsTestDetails.setScore(0);
						
						try {
							studentsTestDetails.setScore(dao.caluclateTestScoreForLeads(bean.getSapid(),bean.getTestId()));
							studentsTestDetails.setShowResult("Y");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							studentsTestDetails.setScore(0);
							studentsTestDetails.setShowResult("N");
						}
						
						boolean updated = dao.updateStudentsTestDetailsForLeads(studentsTestDetails);
						if(!updated) {
							response.put("Status", "Fail");
							return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						

						//Send mail after test is completed start
						try {
								MailSender mailSender = (MailSender)act.getBean("mailer");
								StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
								//StudentBean student = sMarksDao.getSingleStudentsData(bean.getSapid());
								StudentExamBean student = dao.getLeadsDataByLeadId(bean.getSapid());
								student.setSapid(student.getLeadId());
								TestExamBean test = dao.getTestByIdForLeads(bean.getTestId());
								StudentsTestDetailsExamBean studentsTestDetailsForMail =  dao.getStudentsTestDetailsBySapidAndTestIdForLeads(bean.getSapid(), bean.getTestId());
								
								if(LEAD_FINAL_ASSESSMENT_TEST_IDS.contains((long)bean.getTestId())) {
									mailSender.sendFinalAssessmentTestEndedEmailForLeads(student,test,studentsTestDetailsForMail);
								}
								else
									mailSender.sendTestEndedEmailForLeads(student,test,studentsTestDetailsForMail);
							
						} catch (Exception e) {
							//
							//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
							logger.info("\n"+SERVER+": "+"IN Send mail error got sapid: "+studentsTestDetails.getSapid()+" testId: "+bean.getId()+" Error: "+e.getMessage());
							
						}
						//Send mail after test is completed end
						
						
					}else {			
						response.put("Status", "Fail");
						return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					response.put("Status", "Success");
					
			
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				}catch(Exception e) {



					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String apiCalled = "studentTestController/m/saveStudentsTestDetailsForLeads";
					String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ bean.getTestId()+ 
							",errors=" + errors.toString();
					dao.setObjectAndCallLogError(stackTrace,bean.getSapid());
					response.put("Status", "Fail");
					return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
		}
		
		
	
		
		
		public List<TestExamBean> getTestDataForStudent(String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			//List<String> applicableSubjects = applicableSubjectsForTest(sapId);
			List<TestExamBean> testsList = getLiveApplicableTestBySapid(sapId);
			HashMap<Long, StudentsTestDetailsExamBean>  testIdAndTestByStudentsMap = dao.getStudentsTestDetailsAndTestIdMapBySapid(sapId);
			
			for(TestExamBean test : testsList) {
				StudentsTestDetailsExamBean tempTest = testIdAndTestByStudentsMap.get(test.getId());
				
				if(tempTest !=null) {
					test.setAttempt(tempTest.getAttempt());
				}else {
					test.setAttempt(0);
				}
				
			}
			
			return testsList;
		}
		
		public List<TestExamBean> getLiveApplicableTestBySapid(String sapId){
			


			StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			StudentExamBean student = sMarksDao.getSingleStudentsData(sapId);
			TestDAO tDao = (TestDAO)act.getBean("testDao");
			
			setLiveYearMonthForTest(tDao,student);
			
			
			/*if("Offline".equalsIgnoreCase(student.getExamMode())) {
				return viewPreviousAssignments(request,response, new AssignmentFileBean());
			}*/
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO");
			boolean isOnline = isOnline(student);

			ArrayList<String> currentSemSubjects = new ArrayList<>();
			ArrayList<String> failSubjects = new ArrayList<>();
			ArrayList<String> applicableSubjects = new ArrayList<>();
			ArrayList<String> ANSSubjects = new ArrayList<>();

			
			HashMap<String, String> subjectSemMap = new HashMap<>();
			
			StudentExamBean studentRegistrationData = tDao.getStudentRegistrationDataForTest(sapId);
		
			String currentSem = null;

			
			if(student == null){
				return new ArrayList<>();
			}else{
				// removed waived off logic in favor of common helper logic
				studentService.mgetWaivedOffSubjects(student);
			}

			if(studentRegistrationData != null){
				//Take program from Registration data and not Student data. 
				student.setProgram(studentRegistrationData.getProgram());
				student.setSem(studentRegistrationData.getSem());
				currentSem = studentRegistrationData.getSem();
				currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
			}

			ArrayList<String> passSubjectsList = getPassSubjects(student,pdao);
			if(!passSubjectsList.isEmpty() && passSubjectsList != null){
				for(String subject:passSubjectsList){
					if(currentSemSubjects.contains(subject)){
						currentSemSubjects.remove(subject);
					}
				}
			}
			
			
			failSubjects = new ArrayList<>();
			
			ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjectsForTest(student);
			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
			
				for (AssignmentFileBean bean : failSubjectsBeans) {
					String subject = bean.getSubject();
					String sem = bean.getSem();
					failSubjects.add(bean.getSubject());
					subjectSemMap.put(subject, sem);
					
					if("ANS".equalsIgnoreCase(bean.getAssignmentscore())){
						ANSSubjects.add(subject);
					}
				}
			}

			//}

			ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student);
			if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0){

				for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
					String subject = failANSSubjectsBeans.get(i).getSubject();
					String sem = failANSSubjectsBeans.get(i).getSem();
					failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
					subjectSemMap.put(subject, sem);
					
					ANSSubjects.add(subject);
				}
			}
			ArrayList<AssignmentFileBean> currentSemResultAwaitedSubjectsList = new ArrayList<AssignmentFileBean>();
			
			//Check if result is live for last submission cycle
			boolean isResultLiveForLastSubmissionCycle = sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
			ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
			if(!isResultLiveForLastSubmissionCycle){
				ArrayList<String> subjectsSubmittedInLastCycle = dao.getFailedSubjectsSubmittedInLastCycle(sapId, failSubjects);
				ArrayList<String> subjectsExamBookedInLastCycle = dao.getFailedSubjectsExamBookedInLastCycle(sapId, failSubjects);
				
				
				for (String subject : subjectsSubmittedInLastCycle) {
					ANSSubjects.remove(subject);
				}
				if(subjectsSubmittedInLastCycle.size() > 0 || subjectsExamBookedInLastCycle.size() > 0){
					//There are failed subjects submitted in last submission cycle 
					
						//If result is not live then subjects submitted in last cycle cannot be submitted till results are live
						subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
						subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle);
				}


			}
			
			
			

			for (String failedSubject : failSubjects) {
				//For ANS cases, where result is not declared, failed subject will also be present in Current sem subject.
				//Give preference to it as Failed, so that assignment can be submitted and remove  from Current list
				if(currentSemSubjects.contains(failedSubject)){
					currentSemSubjects.remove(failedSubject);
				}
			}
			
			currentSemSubjects.remove("Project");
			failSubjects.remove("Project");
			currentSemSubjects.remove("Module 4 - Project");
			failSubjects.remove("Module 4 - Project");
			applicableSubjects.addAll(currentSemSubjects);
			applicableSubjects.addAll(failSubjects);
			applicableSubjects.remove("Project");
			applicableSubjects.remove("Module 4 - Project");

			List<TestExamBean> allAapplicableTestList = new ArrayList<>();
		
				List<TestExamBean> currentSemTests = null;
				List<TestExamBean> failSubjecTests = null;
				
				if(currentSemSubjects != null && currentSemSubjects.size()>0){
					currentSemTests = tDao.getLiveTestForCurrentSemSubjectsBySubjectsAndMasterkey(currentSemSubjects, student);
				}
				if(failSubjects != null && failSubjects.size()>0){
					failSubjecTests = tDao.getLiveTestForFailedSubjecsBySubjectsAndMasterkey(failSubjects, student);
				}

				if(currentSemTests != null){
					allAapplicableTestList.addAll(currentSemTests);
				}

				if(failSubjecTests != null){

					allAapplicableTestList.addAll(failSubjecTests);
				}
				
				return allAapplicableTestList;
		}
		
		private void setLiveYearMonthForTest(TestDAO dao,StudentExamBean student) {
			
			
			try {
				//Check for hasTest start
				boolean hasTest = dao.checkHasTest(student.getConsumerProgramStructureId());
				if(hasTest) {
					TestExamBean testLiveSettingRegular = dao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
					if(testLiveSettingRegular != null) {
						dao.setLiveRegularTestYear(testLiveSettingRegular.getAcadsYear());
						dao.setLiveRegularTestMonth(testLiveSettingRegular.getAcadsMonth());
					}
					
					TestExamBean testLiveSettingResit = dao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
					if(testLiveSettingResit != null) {
						dao.setLiveResitTestYear(testLiveSettingResit.getAcadsYear());
						dao.setLiveResitTestMonth(testLiveSettingResit.getAcadsMonth());
					}		
				}
				//Check for hasTest end
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			
		}

		private ArrayList<String> getSubjectsForStudent(StudentExamBean student, HashMap<String, String> subjectSemMap) {

			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
			
			ArrayList<String> subjects = new ArrayList<>();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(
						bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
						&& bean.getSem().equals(student.getSem())
						&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
						&& "Y".equalsIgnoreCase(bean.getHasIA())
						&& "Y".equalsIgnoreCase(bean.getHasTest())
						){
					subjects.add(bean.getSubject());
					subjectSemMap.put(bean.getSubject(), bean.getSem());

				}
				
				
				//Below code is for creating map of subject and sem
				if(
						bean.getConsumerProgramStructureId().equalsIgnoreCase(student.getConsumerProgramStructureId())
						){
					subjectSemMap.put(bean.getSubject(), bean.getSem());

				}
			}
			if(student.getWaivedInSubjects() != null) {
				subjects.addAll(student.getWaivedInSubjects());
				subjectSemMap.putAll(student.getWaivedInSubjectSemMapping());
			}
			
			
			return subjects;
		}
		
		public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
			if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){

				TestDAO dao = (TestDAO)act.getBean("testDao");
				this.programSubjectMappingList = dao.getProgramSubjectMappingList();
			}
			return programSubjectMappingList;
		} 
		
		private ArrayList<String> getPassSubjects(StudentExamBean student, PassFailDAO dao) {
			ArrayList<String> passSubjectList = dao.getPassSubjectsNamesForSingleStudent(student.getSapid());
			return passSubjectList;
		}	
		
		private ArrayList<AssignmentFileBean> getFailSubjectsForTest(StudentExamBean student) {
			TestDAO dao = (TestDAO)act.getBean("testDao");
			ArrayList<AssignmentFileBean> failSubjectList = dao.getFailSubjectsForAStudentApplicableForTest(student.getSapid());
			return failSubjectList;
		}

		private ArrayList<AssignmentFileBean> getANSNotProcessed(StudentExamBean student) {
			PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
			ArrayList<AssignmentFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
			return failSubjectList;
		}

		public boolean isOnline(StudentExamBean student) {
			String programStucture = student.getPrgmStructApplicable();
			boolean isOnline = false;

			if("Online".equals(student.getExamMode())){
				//New batch students and certificate program students will be considered online and with 4 attempts for assginmnet submission
				isOnline = true; 
				//NA
			}
			return isOnline;
		}
		
		
		public ViewTestDetailsForStudentsAPIResponse getViewTestDetailsForStudentsAPIResponse(String userId,Long id,String message) {
			ViewTestDetailsForStudentsAPIResponse returnBean = new ViewTestDetailsForStudentsAPIResponse();
			TestDAO dao = (TestDAO)act.getBean("testDao");
			logger.info("\n"+SERVER+": "+"IN getViewTestDetailsForStudentsAPIResponse got id "+id+" userId: "+userId+" message: "+message);
			
			
			TestExamBean test = dao.getTestById(id);
			
			test = updateStartEndTimeIfExtended(test,userId);

			//test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(),test.getEndDate()));
			//test.setRemainingTime(test.getDuration());
			test.setRemainingTime(getDurationTimeByIAType(test.getTestType(),test.getDuration(),test.getEndDate()));
			
			StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(userId,test.getId());
			
			String continueAttempt = "N";
			boolean canContinueAttempt = canContinueAttempt(test,studentsTestDetails);
			if(canContinueAttempt) {
				continueAttempt= "Y";
				studentsTestDetails.setAttempt(studentsTestDetails.getAttempt()-1);
				//test.setRemainingTime(getDurationOfTestWRTTestStartedOn(test.getDuration(),studentsTestDetails.getTestStartedOn()));
				test.setRemainingTime(getRemainTimeByIAType(test.getTestType(),test.getDuration(),studentsTestDetails.getTestStartedOn(),test.getEndDate()));
			}
			
			studentsTestDetails = checkIfCanShowResultsAndAttemptedQuestions(studentsTestDetails,test);
			

			/*
			 * int score=studentsTestDetails.getScore();
			 * if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
			 * if(studentsTestDetails.getScore() == 0) { //if student didnot click submit
			 * test and left test page
			 * studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.
			 * getSapid(),studentsTestDetails.getTestId()));
			 * +studentsTestDetails.getScore()); score=studentsTestDetails.getScore();
			 * boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);
			 * +updatedSore); } } studentsTestDetails.setScore(score);
			 */

			double score=studentsTestDetails.getScore();
			/*if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
				if(studentsTestDetails.getScore() == 0) { //if student didnot click submit test and left test page
					studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.getSapid(),studentsTestDetails.getTestId()));
					score=studentsTestDetails.getScore();
					boolean updatedSore=dao.updateStudentsTestDetails(studentsTestDetails);
				}
			}*/
			studentsTestDetails.setScore(score);

			
			List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestId(userId, test.getId());
			Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap = new HashMap<>();
			
			ArrayList<TestQuestionExamBean> attemptDetail1 = new ArrayList<TestQuestionExamBean>();
			ArrayList<TestQuestionExamBean> attemptDetail2 = new ArrayList<TestQuestionExamBean>();
			ArrayList<TestQuestionExamBean> attemptDetail3 = new ArrayList<TestQuestionExamBean>();
			
			 
			//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
			
			if("Y".equalsIgnoreCase(studentsTestDetails.getShowResult())) {
			
				 attemptNoNQuestionsMap = getAttemptNoNQuestionsMap(test.getId(),userId);
				
				for (Map.Entry<Integer, List<TestQuestionExamBean>> entry : attemptNoNQuestionsMap.entrySet()) {			
					if(entry.getKey() == 1) {
					attemptDetail1 = (ArrayList<TestQuestionExamBean>) entry.getValue();
					}
					if(entry.getKey() == 2) {
						attemptDetail2 = (ArrayList<TestQuestionExamBean>) entry.getValue();
						}
					if(entry.getKey() == 3) {
						attemptDetail3 = (ArrayList<TestQuestionExamBean>) entry.getValue();
						}
						
				}
			}
			
			List<Integer> bodQuestionsList = iaTestService.bodAppliedQuestions(id);						//Gets a list of BoD applied question IDs
			
			//Updated : 17Feb19 by Pranit to only take attemptdetails after results are live start
			
			if("testTimeOut".equalsIgnoreCase(message)) {
				message = "Time Over";
			}
			if("testEnded".equalsIgnoreCase(message)) {
				message = "Test Ended";
			}
			
			
			String paymentPendingForSecondOrHigherAttempt = "";
			
			if("old".equalsIgnoreCase(test.getApplicableType())) {
				StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				StudentExamBean student = sMarksDao.getSingleStudentsData(userId);
				
				//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt start
				
				AssignmentsDAO aDao = (AssignmentsDAO)act.getBean("asignmentsDAO");
				boolean isOnline = new AssignmentSubmissionController().isOnline(student);
				//m.addAttribute("assignmentPaymentPending","false");
				returnBean.setAssignmentPaymentPending("false");
				if(isOnline){//Applicble for online students only
					int pastCycleAssignmentAttempts = aDao.getPastCycleAssignmentAttempts(test.getSubject(),userId);
					int pastCycleTestAttempts = dao.getPastCycleTestAttempts(test.getSubject(),userId);
					
					if((pastCycleAssignmentAttempts + pastCycleTestAttempts) >=2){
						boolean hasPaidForAssignment = aDao.checkIfAssignmentFeesPaid(test.getSubject(), userId); //check if Assignment Fee Paid for Current drive 
						if(!hasPaidForAssignment){
							//m.addAttribute("assignmentPaymentPending","true");
							returnBean.setAssignmentPaymentPending("true");
						}
					}
				}
				//Logic to check 3rd attempt of subject and enable payment if it is 3rd attempt end
				
				//code to take charge for 2nd or higher attempt start
				paymentPendingForSecondOrHigherAttempt = "N";
				if(studentsTestDetails.getAttempt()+1 <= test.getMaxAttempt() && (studentsTestDetails.getAttempt()+1) > 1) {
				
				if( (studentsTestDetails.getAttempt() > 0) && (!"Y".equalsIgnoreCase(continueAttempt)) ) {
					boolean checkIfTestFeesPaidForAttempt = dao.checkIfTestFeesPaidForAttempt(test.getSubject(), userId, test.getId(), studentsTestDetails.getAttempt()+1);
					if(!checkIfTestFeesPaidForAttempt) {
						paymentPendingForSecondOrHigherAttempt="Y";
					}
				}
				}
				//code to take charge for 2nd or higher attempt end
				
				
			}else {
				paymentPendingForSecondOrHigherAttempt = "N";
				returnBean.setAssignmentPaymentPending("false");
				
			}
					
			
			
			//Send mail after test is completed start
			try {


				if(("Time Over".equalsIgnoreCase(message) || "Test Ended".equalsIgnoreCase(message)) && "PROD".equalsIgnoreCase(ENVIRONMENT)) {
					MailSender mailSender = (MailSender)act.getBean("mailer");
					StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
					StudentExamBean student = sMarksDao.getSingleStudentsData(userId);
					


					mailSender.sendTestEndedEmail(student,test,studentsTestDetails);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				//mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
				logger.info("\n"+SERVER+": "+"IN Send mail error got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.getMessage());
				
			}
			//Send mail after test is completed end
			
			//Check show start test button start
			boolean showStartTestButton = false;

			if( !("Y".equalsIgnoreCase(continueAttempt))) {
				//check for test between given startDate and endDate start
				try {
					String startDate = test.getStartDate();
					String endDate = test.getEndDate();


					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date sDate = sdf.parse(startDate.replaceAll("T", " "));
					Date cDate = new Date();
					Date eDateWithOutBuffer = sdf.parse(endDate.replaceAll("T", " "));
					Date eDate = addMinutesToDate(MINS_BUFFER_TO_START_IA,eDateWithOutBuffer);// adding 2mins to end datetime to give buffer for late joining.
					


					
					if(cDate.after(sDate) && cDate.before(eDate)) {


						showStartTestButton = true;
					}else {
						if(cDate.before(sDate)) {


							showStartTestButton = false;
						}
						else if(cDate.after(eDate)) {

							showStartTestButton = false;;
						}
						else {

							showStartTestButton = false;
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					showStartTestButton = false;
				}
				//check for test between given startDate and endDate end
			}
			else {
				showStartTestButton = true;
			}
			//Check show start test button end
			
			/*
			if(showStartTestButton && "Y".equalsIgnoreCase(continueAttempt)) {
				String endDateTimeFromStudentDetails = getEndDateInStringByStartDate(test.getDuration(),studentsTestDetails.getTestStartedOn());

				String endDateTimeForCalculatingRemainingTime = "";
				boolean takeEndDateFromTestDetails = getIfTakeEndDateFromTestDetails(test.getEndDate(),endDateTimeFromStudentDetails);

				if(takeEndDateFromTestDetails) {
					endDateTimeForCalculatingRemainingTime = test.getEndDate();
				}else{
					endDateTimeForCalculatingRemainingTime = endDateTimeFromStudentDetails;
				}
				test.setRemainingTime(getDurationOfTestWRTCurrentTime(test.getDuration(), endDateTimeForCalculatingRemainingTime));
			}
			*/
			
			returnBean.setShowStartTestButton(showStartTestButton);
			
			//m.addAttribute("test", test);
			returnBean.setTest(test);
			
			//m.addAttribute("studentsTestDetails", studentsTestDetails);
			returnBean.setStudentsTestDetails(studentsTestDetails);
			
			//m.addAttribute("messageDetails", message);
			returnBean.setMessageDetails(message);
			
			//m.addAttribute("attemptsDetails", attemptsDetails);
			returnBean.setAttemptsDetails(attemptsDetails);
			
			//m.addAttribute("attemptNoNQuestionsMap", attemptNoNQuestionsMap);
			returnBean.setAttemptNoNQuestionsMap(attemptNoNQuestionsMap);
			
			//m.addAttribute("continueAttempt", continueAttempt);
			returnBean.setContinueAttempt(continueAttempt);
			
			//m.addAttribute("subject",test.getSubject());
			returnBean.setSubject(test.getSubject());
			
			//m.addAttribute("paymentPendingForSecondOrHigherAttempt", paymentPendingForSecondOrHigherAttempt);
			returnBean.setPaymentPendingForSecondOrHigherAttempt(paymentPendingForSecondOrHigherAttempt);
			
			returnBean.setAttemptDetail1(attemptDetail1);
			
			returnBean.setAttemptDetail2(attemptDetail2);
			
			returnBean.setAttemptDetail3(attemptDetail3);
			
			returnBean.setBodQuestions(bodQuestionsList);


			logger.info("\n"+SERVER+": "+new Date()+" IN getViewTestDetailsForStudentsAPIResponse got id "+id+" userId: "+userId+" returnBean: "+returnBean.toString());
			
			return returnBean;
		}
		
		
		private int getRemainTimeByIAType(String testType,Integer duration,String testStartedOn,String testEndDateTime) {
			
			
			try {
				if("Test".equalsIgnoreCase(testType)) {
					return getDurationOfTestWRTTestStartedOn(duration,testStartedOn);
				}else if("Assignment".equalsIgnoreCase(testType)) {
					return getDurationOfTestWRTCurrentTime(duration,testEndDateTime);
				}else {
					return getDurationOfTestWRTTestStartedOn(duration,testStartedOn);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTTestStartedOn got testType "+testType+" duration  "+duration+" endDateTime: "+testStartedOn+" Error: "+e.getMessage());
				return 0;
			}
		
}
		
		private int getDurationOfTestWRTCurrentTime(Integer duration,String endDateTime) {
			
			int remainingTime;
			
			//get time left im min
			try {
				

				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date eDateTime = sdf.parse(endDateTime.replaceAll("T", " "));
				Date cDateTime = new Date();
				
				

				
				if(cDateTime.before(eDateTime)) {
					 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);


					 return remainingTime > duration ? duration : remainingTime;
				}else {


					return 0;
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTCurrentTime got duration  "+duration+" endDateTime: "+endDateTime+" Error: "+e.getMessage());
				return 0;
			}
		
}
	

	private int getDurationTimeByIAType(String testType,Integer duration,String testEndDateTime) {
		
			
			try {
				if("Test".equalsIgnoreCase(testType)) {
					return duration;
				}else if("Assignment".equalsIgnoreCase(testType)) {
					return getDurationOfTestWRTCurrentTime(duration,testEndDateTime);
				}else {
					return duration;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getDurationTimeByIAType got testType "+testType+" duration  "+duration+" Error: "+e.getMessage());
				return 0;
			}
		
}		
	
	private int getDurationOfTestWRTTestStartedOn(Integer duration,String testStartedOn) {
		
		int remainingTime;
		
		//get time left im min
		try {
			

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date eDateTime = addMinutesToDate(duration,sdf.parse(testStartedOn.replaceAll("T", " ")));
			Date cDateTime = new Date();
			
			


			
			if(cDateTime.before(eDateTime)) {
				 remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);

				 return remainingTime > duration ? duration : remainingTime;
			}else {

				return 0;
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+"IN getDurationOfTestWRTTestStartedOn got duration  "+duration+" endDateTime: "+testStartedOn+" Error: "+e.getMessage());
			return 0;
		}
	
}

	//Code for start test end


public String checkValidityOfTest(TestExamBean test,StudentsTestDetailsExamBean studentsTestDetailsCheck) {
	
	if(test==null) {
		return "Test details not found.";
	}else {

		//check for test attempt still open start
			boolean canContinueAttempt = canContinueAttempt(test,studentsTestDetailsCheck);
			if(canContinueAttempt) {
				return "ATTEMPT_STILL_OPEN";
			}
		//check for test attempt still open end
		
		//check for test between given startDate and endDate start
		try {
			String startDate = test.getStartDate();
			String endDate = test.getEndDate();


			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sDate = sdf.parse(startDate.replaceAll("T", " "));
			Date cDate = new Date();
			Date eDateWithOutBuffer = sdf.parse(endDate.replaceAll("T", " "));
			Date eDate = addMinutesToDate(MINS_BUFFER_TO_START_IA,eDateWithOutBuffer);// adding 2mins to end datetime to give buffer for late joining.
			


			
			if(cDate.after(sDate) && cDate.before(eDate)) {


			}else {
				if(cDate.before(sDate)) {


					return "Test has not started yet.";
				}
				else if(cDate.after(eDate)) {


					return "Test has ended.";
				}
				else {


					return "Error in starting Test";
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+" IN checkValidityOfTest > check for test between given startDate and endDate  got testId : "+test.getId()+" sapId : "+studentsTestDetailsCheck.getSapid()+", Error :  "+e.getMessage());
			
			return "Error in Starting Test. ";
		}
		//check for test between given startDate and endDate end

		
		//check for no of attempts start
		try {
			int maxAttempts = test.getMaxAttempt();
			int attempts = studentsTestDetailsCheck.getAttempt();
			
			if(attempts >= maxAttempts) {
				return "You have exhausted your attempts.";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+" IN checkValidityOfTest > check for no of attempts  got testId : "+test.getId()+" sapId : "+studentsTestDetailsCheck.getSapid()+", Error :  "+e.getMessage());
			
			return "Error in Starting Test. ";
		}
		//check for no of attempts end 
	}


	return null;
}


		
		private boolean getIfTakeEndDateFromTestDetails(String testEndDateTime, String endDateTimeFromStudentDetails) {
			try {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date testEndDate = sdf.parse(testEndDateTime.replaceAll("T", " "));
				Date endDateFromStudentDetails = sdf.parse(endDateTimeFromStudentDetails.replaceAll("T", " "));
				
				
				if(testEndDate.before(endDateFromStudentDetails)) {
					return true;
				}else {
					return false;
				}
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
				return true;
			}
			
		}


		private String getEndDateInStringByStartDate(Integer duration, String startDateTime) {
			try {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDateTime = sdf.parse(startDateTime.replaceAll("T", " "));
				Date eDateTime = addMinutesToDate(duration,sDateTime);
				
				return sdf.format(eDateTime);
			} catch (ParseException e) {
				
				return null;
			}
			
		}

		private TestExamBean updateStartEndTimeIfExtended(TestExamBean test, String userId) {
			TestDAO dao = (TestDAO)act.getBean("testDao");

			TestExamBean extendedTime = dao.getExtendedTimeBySapidNUserId(userId,test.getId());
			
			if(extendedTime != null) {
				test.setStartDate(extendedTime.getExtendedStartTime());
				test.setEndDate(extendedTime.getExtendedEndTime());

			}
			
			
			return test;
		}

		private StudentsTestDetailsExamBean checkIfCanShowResultsAndAttemptedQuestions(
				StudentsTestDetailsExamBean studentsTestDetails, TestExamBean test) {
			
			
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date eDateTime = sdf.parse(test.getEndDate().replace("T", " "));
				Date cDateTime = new Date();

				if(cDateTime.after(eDateTime) && "Y".equalsIgnoreCase(studentsTestDetails.getShowResult()) ) {
					studentsTestDetails.setShowResult("Y");
				}else {
					studentsTestDetails.setShowResult("N");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN checkIfCanShowResultsAndAttemptedQuestions got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getSapid()+" Error: "+e.getMessage());
				
			}
			

			return studentsTestDetails;
		}

		public boolean canContinueAttempt(TestExamBean test,StudentsTestDetailsExamBean studentsTestDetails) {
			
			//check for test attempt still open start
			try {
				if(studentsTestDetails.getId() != null) { //id is null that means it would be 1st attempt and studentsTestDetails would not be present
					if(!"Y".equalsIgnoreCase(studentsTestDetails.getTestCompleted())) {
							String startDateTime = studentsTestDetails.getTestStartedOn();
							Integer duration = test.getDuration();
							

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date sDateTime = sdf.parse(startDateTime);
							Date cDateTime = new Date();
							Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
							

							
							if(cDateTime.before(eDateTime)) {
								return true;
							}
					}else {

					}
				}else {


				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN canContinueAttempt got sapid  "+studentsTestDetails.getSapid()+" testId: "+test.getSapid()+" Error: "+e1.getMessage());
				
			}
			//check for test attempt still open end
			
			
			return false;
		}
		
		public Map<Integer, List<TestQuestionExamBean>> getAttemptNoNQuestionsMap(Long testId, String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			List<StudentsTestDetailsExamBean> attemptsDetails =  dao.getAttemptsDetailsBySapidNTestId(sapId, testId);
			HashMap<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = dao.getAttemptAnswersMapBySapidNTestId(sapId, testId);
			Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap= new HashMap<>();
			for(StudentsTestDetailsExamBean b : attemptsDetails) {


				List<TestQuestionExamBean> qList = dao.getTestQuestionsPerAttempt(b.getTestQuestions());
				List<StudentQuestionResponseExamBean> answersByAttempt = attemptsAnswerMap.get(b.getAttempt());
				
				
				for(TestQuestionExamBean q : qList) {
					
					List<StudentQuestionResponseExamBean> answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>(); 
					
					if(q.getType() == 1 || q.getType() == 2 || q.getType() == 5 || q.getType() == 6  || q.getType() == 7 ) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {
								
								for(TestQuestionOptionExamBean o : q.getOptionsList()) {

									if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


										o.setSelected("Y");
										q.setIsAttempted("Y");
									}
								}							
							
								
								if(q.getId().equals(a.getQuestionId())) {								
								 answerListByQuestionId.add(a);		
									
									}
								
									
										
							}
						}
					}else if(q.getType() == 3) {
						for(TestQuestionExamBean sq : q.getSubQuestionsList()) {
							if(answersByAttempt !=null) {
								for(StudentQuestionResponseExamBean a : answersByAttempt) {
									for(TestQuestionOptionExamBean o : sq.getOptionsList()) {


										if(a.getAnswer().equalsIgnoreCase(o.getId().toString())) {


											o.setSelected("Y");
											sq.setIsAttempted("Y");
											q.setIsAttempted("Y");
										
										}
									}
								}
							}
						}
						
					}else if(q.getType() == 4) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {


								

								if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {


									q.setIsAttempted("Y");
									q.setAnswer(a.getAnswer());
									q.setMarksObtained(a.getMarks());

									if("CopyCase".equalsIgnoreCase(b.getAttemptStatus())) {
										q.setRemarks(COPY_CASE_REMARK);
									}else {
										q.setRemarks(a.getRemark());	
									}

									q.setRemarks(a.getRemark());
								}else {
									if(!"Y".equalsIgnoreCase(q.getIsAttempted())) {
										q.setIsAttempted("N");
									}
								}	
									
							}
						}
					
					
					}
					else if(q.getType() == 8) {
						if(answersByAttempt !=null) {
							for(StudentQuestionResponseExamBean a : answersByAttempt) {

								if((q.getId()+"").equalsIgnoreCase((a.getQuestionId()+""))) {


									q.setIsAttempted("Y");
									q.setAnswer(a.getAnswer());
									q.setMarksObtained(a.getMarks());
									q.setRemarks(a.getRemark());
								}else {
									q.setIsAttempted("N");
								}	
									
							}
							


						}
					
					
					}
				 
					try {
						


							 double score = dao.checkType1n2Question(q, answerListByQuestionId);
							
							if(q.getMarks() == score) {
								q.setStudentAnswerCorrect(1);
							}else {
								q.setStudentAnswerCorrect(0);
							}
							}catch(Exception e) {
								//
								logger.info("\n"+SERVER+": "+" IN getAttemptNoNQuestionsMap   got testId : "+testId+" sapId : "+sapId+", Error :  "+e.getMessage());
								
								
							}																
					
						answerListByQuestionId = new ArrayList<StudentQuestionResponseExamBean>();
						
				
				}
				attemptNoNQuestionsMap.put(b.getAttempt(), qList);
			}
			
			/*
			 * for(TestQuestionBean bean:attemptNoNQuestionsMap.get(1)) {
			 * }

			 */
			
			return attemptNoNQuestionsMap;
		}
		
		
		public String updateAttemptsReaminingTime(String sapId, Long testId) {


			TestDAO dao = (TestDAO)act.getBean("testDao");
			StudentsTestDetailsExamBean studentsTestDetails =  dao.getStudentsTestDetailsBySapidAndTestId(sapId,testId);
			TestExamBean test = dao.getTestById(testId);
			test = updateStartEndTimeIfExtended(test,sapId);
			
			Integer duration = test.getDuration();
			int remainingTime;
			
			//get time left im min
			try {
				String startDateTime = getIAStartDateTimeByTestType(test.getTestType(),test.getStartDate(),studentsTestDetails.getTestStartedOn()); //studentsTestDetails.getTestStartedOn();
				//String endDateTime = studentsTestDetails.getTestEndedOn();
				
				//String testEndDateTimeString = test.getEndDate().replace('T', ' ');
				

				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date sDateTime = sdf.parse(startDateTime);
				//Date testEndDateTime = sdf.parse(testEndDateTimeString);
				Date cDateTime = new Date();
				Date eDateTime = addMinutesToDate(duration.intValue(),sDateTime);
				
				//adding buffer to enddate to avoid saving error
				Date eDateTimeWithBuffer = addMinutesToDate(5,eDateTime);
				//Date testEndDateTimeWithBuffer = addMinutesToDate(5,testEndDateTime);
				

				
				if(cDateTime.before(eDateTimeWithBuffer)) {
					 //remainingTime = differenceInMinutesBetweenTwoDates(cDateTime, eDateTime);


				}else {


					return "TimeOver! Your Test Was Started at "+startDateTime+". Test EndTime Was "+eDateTime+". Current Time : "+cDateTime;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+" IN updateAttemptsReaminingTime  sapId:  "+sapId+" testId : "+testId+" Error : "+e.getMessage());
				
				return "Error in calculating remining time.";
			}
			
			/*
			//update into db
			boolean updateRemainingTIme = dao.updateStudentsTestDetailsRemainingTime(remainingTime, studentsTestDetails.getId());
			if(updateRemainingTIme) {
				return "";
			}else {

				return "Error in updateing remaining time to db.";
			}
			*/
			return "";
			
		}
		
		
		private String getIAStartDateTimeByTestType(String testType, String testStartDate, String studentsTestStartedOn) {
			try {
				if("Test".equalsIgnoreCase(testType)) {
					return studentsTestStartedOn;
				}else if("Assignment".equalsIgnoreCase(testType)) {
					return testStartDate.replace('T', ' ');
				}else {
					return studentsTestStartedOn;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getIAStartDateTimeByTestType got testType "+testType+" studentsTestStartedOn  "+studentsTestStartedOn+" testStartDate "+testStartDate+" Error: "+e.getMessage());
				return studentsTestStartedOn;
			}
		

		
		}

		private Integer getIADurationByTestType(String testType, Integer duration) {
		
			try {
				if("Test".equalsIgnoreCase(testType)) {
					return duration;
				}else if("Assignment".equalsIgnoreCase(testType)) {
					return 0;
				}else {
					return duration;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
				logger.info("\n"+SERVER+": "+"IN getIADurationByTestType got testType "+testType+" duration  "+duration+" Error: "+e.getMessage());
				return duration;
			}
		
	}

		private  Date addMinutesToDate(int minutes, Date beforeTime){
		    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

		    long curTimeInMs = beforeTime.getTime();
		    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
		    return afterAddingMins;
		}
		private int differenceInMinutesBetweenTwoDates(Date sDate, Date eDate) {


			long diff = eDate.getTime() - sDate.getTime();
			
			if(diff <= 0) {
				return 0;
			}
			
		    //long diffSeconds = diff / 1000 % 60;
		    float diffMinutes = (float)diff / (float)(60 * 1000);
		    //long diffHours = diff / (60 * 60 * 1000);
		    //int diffInDays = (int) ((dt2.getTime() - dt1.getTime()) / (1000 * 60 * 60 * 24));

		    
			return (int) Math.ceil(diffMinutes);
		}
		public String saveType2Answers(StudentQuestionResponseExamBean answer) {
			try {
				TestDAO dao = (TestDAO)act.getBean("testDao");
				
				String[] studentsAnswers = answer.getAnswer().split("~",-1);
				//insert all answers onebyone
				for(int i = 0; i<studentsAnswers.length;i++) {


					answer.setAnswer(studentsAnswers[i]);
					long saved = dao.saveStudentsTestAnswer(answer);
					if(saved==0) {
						return "error";
					}
				}
				return "success";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				

				return "error";
			}
		}
		
		
		public List<TestExamBean> getPendingTestDataForStudentTODO(Long id, String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			// get all test for the subjects alongwith students attempt details.
			List<TestExamBean> returnList = new ArrayList<>();
			List<TestExamBean> attemptedTestsBySapidNSubject = dao.getApplicablePendingTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);

			List<TestExamBean> onGoingTestsBySapidNSubject = dao.getOngoingTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
			List<TestExamBean> extendedTestsBySapidNSubject = dao.getExtendedTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);

					

			
			if(attemptedTestsBySapidNSubject !=null) {
				returnList.addAll(attemptedTestsBySapidNSubject);
			}
			if(onGoingTestsBySapidNSubject !=null) {
				returnList.addAll(onGoingTestsBySapidNSubject);
			}
			if(extendedTestsBySapidNSubject !=null) {
				returnList.addAll(extendedTestsBySapidNSubject);
			}
			
			
			return returnList;
			
		}
		
		public List<TestExamBean> getTestDataForStudentTODO(Long id, String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			// get all test for the subjects alongwith students attempt details.
							
			//List<TestBean> attemptedTestsBySapidNSubject = dao.getApplicableTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
			List<TestExamBean> attemptedTestsBySapidNSubject = getPendingTestDataForStudentTODO(id,sapId);
							
					


			
			return attemptedTestsBySapidNSubject;
			
		}
		
		public List<TestExamBean> getDueTestDataForStudentTODO(Long id, String sapId){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			// get all test for the subjects alongwith students attempt details.
							
			//List<TestBean> attemptedTestsBySapidNSubject = dao.getApplicableDueTestsWithAttemptDetailsBySapidNSubject_todo(id,sapId);
			List<TestExamBean> attemptedTestsBySapidNSubject = getPendingTestDataForStudentTODO(id,sapId);
					

			
			return attemptedTestsBySapidNSubject;
			
		}
		
		
		private String uploadTestAnsAssignmentToServer(MultipartFile imageBean,String userId,String testId) {

	        String fileName = imageBean.getOriginalFilename();  
	        
			//File file = convertFile(imageBean);
	        MultipartFile file = imageBean;
			/*
			 * if(!(fileName.toUpperCase().endsWith(".PDF")) ){
			 * 
			 * return ""; }
			 */
			fileName = fileName.replaceAll("'", "_");
			fileName = fileName.replaceAll(",", "_");
			fileName = fileName.replaceAll("&", "and");
			fileName = fileName.replaceAll(" ", "_");
			fileName = fileName.replaceAll(":", "");
			

	        InputStream inputStream = null;   
	        OutputStream outputStream = null;
			String returnUrl = "";
			try {
	            inputStream = file.getInputStream();   
	            //String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
	            String extention =fileName.substring(fileName.lastIndexOf("."), fileName.length()); 
	            String imagePathToReturn ="assignment_"+testId+"_"+userId+"_"+ RandomStringUtils.randomAlphanumeric(12)+extention;
	             			
	            
	            String filePath = IA_ASSIGNMENT_FILES_PATH+imagePathToReturn;



				File folderPath = new File(IA_ASSIGNMENT_FILES_PATH);
				      if (!folderPath.exists()) {



				            boolean created = folderPath.mkdirs();



				      }else {
				    	  
				      }
				      

				      File newFile = new File(filePath);   
				      outputStream = new FileOutputStream(newFile);   
				      int read = 0;   
				      byte[] bytes = new byte[1024];   

				      while ((read = inputStream.read(bytes)) != -1) {   
				            outputStream.write(bytes, 0, read);   
				      }
				      outputStream.close();
				      inputStream.close();
				      returnUrl=SERVER_PATH+"IATestAssignmentFiles/"+imagePathToReturn;



			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				returnUrl="";
			}
			
			return returnUrl;
		}
		
		public File convertFile(MultipartFile file)
		{    
		    File convFile = new File(file.getOriginalFilename());
		    try {
				convFile.createNewFile(); 
				FileOutputStream fos = new FileOutputStream(convFile); 
				fos.write(file.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			} 
		    return convFile;
		}


		private String uploadTestAnsAssignmentToAWS(MultipartFile fileBean, String testId, String userId) {

			String fileOriginalName = fileBean.getOriginalFilename();  

			fileOriginalName = fileOriginalName.replaceAll("'", "_");
			fileOriginalName = fileOriginalName.replaceAll(",", "_");
			fileOriginalName = fileOriginalName.replaceAll("&", "and");
			fileOriginalName = fileOriginalName.replaceAll(" ", "_");
			fileOriginalName = fileOriginalName.replaceAll(":", "");
			
			String extention = fileOriginalName.substring( fileOriginalName.lastIndexOf("."), fileOriginalName.length() ); 
			String folderPath = "submissions/";
			String fileName = folderPath + ENVIRONMENT+ "_assignment_"+testId+"_"+userId+"_"+ RandomStringUtils.randomAlphanumeric(12)+extention;
			String returnUrl = "";
			
			try {

				returnUrl = amazonS3Helper.uploadMiltipartFile( fileBean, folderPath, awsIAFileBucket, fileName);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				returnUrl="";
				
			}

			return returnUrl;
		}

	@PostMapping(path = "/getTestDataForTODO_V2", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<TestExamBean>> getTestDataForTODO_V2(@RequestBody TestExamBean testBean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		List<TestExamBean> testsList = new ArrayList<>();

		try {
			testsList = studentTestService.getTestDataForTODO( testBean.getId(), testBean.getSapid() );
		} catch (Exception e) {
				// TODO Auto-generated catch block
		}
			
		return new ResponseEntity<>(testsList, headers, HttpStatus.OK);
			
	}

}
