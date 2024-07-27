package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.AccumulateAuditTrailsBean;
import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.GetTestsBySapidResponseBean;
import com.nmims.beans.LogFileAnalysisBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.helpers.MailSender;
import com.nmims.services.MBAXAuditTrailsService;

@RestController
@RequestMapping("/mbax/ia/student/m")
public class MBAXAuditTrailsRestController {

    @Autowired
    MBAXAuditTrailsService mbaxAuditTrailsService;
    
    @Autowired
    MailSender mailer;
    
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@RequestMapping(value = "/getStudentLogFileDetails", method = { RequestMethod.POST }, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<String>> getStudentLogFileDetails(HttpServletRequest request, @RequestBody LogFileAnalysisBean bean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ArrayList<String> logDetails = mbaxAuditTrailsService.getStudentLogFileDetails( bean );
		
		return new ResponseEntity<>(logDetails, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/sendEmailForUnfairMeans", method = { RequestMethod.POST }, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<LostFocusLogExamBean> sendEmailForUnfairMeans(HttpServletRequest request, 
			@RequestBody List<LostFocusLogExamBean> studentList) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		LostFocusLogExamBean status = new LostFocusLogExamBean();
		LostFocusLogExamBean studentDetails = new LostFocusLogExamBean();
		int successCount = 0;
		int failureCount = 0;
		
		for(LostFocusLogExamBean bean : studentList) {
			studentDetails = mbaxAuditTrailsService.getStudentDetailsForUnfairMeans(bean);
			bean.setEmailId(studentDetails.getEmailId());
			try{
				mailer.sendMailsForUnfairMeans(bean);
				successCount++;
			}catch (Exception e) {
				failureCount++;
				
			}
		}
		
		if(successCount == 0) {
			status.setSuccess(false);
			status.setSuccessMessage(failureCount + " mails encountered an error");
		}else if(failureCount ==0){
			status.setSuccess(true);
			status.setSuccessMessage(successCount+" mails where sent successfully.");
		}else {
			status.setSuccess(true);
			status.setSuccessMessage(successCount+" mails where sent successfully. "+failureCount + " mails encountered an error");
		}
		
		return new ResponseEntity<>(status,headers, HttpStatus.OK);	
	}

	@RequestMapping(value = "/getRecentTest", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<LostFocusLogExamBean>> getRecentTest(HttpServletRequest request){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<LostFocusLogExamBean> recentTest = new ArrayList<>();
		
		try {
			recentTest = mbaxAuditTrailsService.getRecentTest();
		} catch (Exception e) {
			
		}

		return new ResponseEntity<>(recentTest ,headers, HttpStatus.OK);
	}
	

	@PostMapping(path = "/getTestsBySapid", consumes = "application/json", produces = "application/json")
	public ResponseEntity<GetTestsBySapidResponseBean> getTestsBySapid(@RequestBody TestExamBean bean){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		GetTestsBySapidResponseBean response = new GetTestsBySapidResponseBean();

		List<TestExamBean> tests = mbaxAuditTrailsService.getTestsBySapid(bean.getSapid());

		response.setTests(tests);

		return new ResponseEntity<>(response,headers, HttpStatus.OK);

	}
	
	@PostMapping(path = "/getStudentTestDetails", consumes = "application/json", produces = "application/json")
	public ResponseEntity<AccumulateAuditTrailsBean> getStudentTestDetails(@RequestBody TestExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		AccumulateAuditTrailsBean response = new AccumulateAuditTrailsBean();
		String sapid = bean.getSapid();
		Long testId = bean.getTestId();
		TestExamBean test = new TestExamBean();
		
		StudentsTestDetailsExamBean studentTestDetails = mbaxAuditTrailsService.getStudentsTestDetailsBySapidAndTestId(sapid,testId);
		
		try {
			test = mbaxAuditTrailsService.getTestById(testId);
		} catch (Exception e) {
			
		}
		test.setTestJoinURL( mbaxAuditTrailsService.getTestJoinLink( sapid, bean.getTestId().toString()) );
		
		response.setStudentTestDetails(studentTestDetails);
		response.setTest(test);

		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	
	@PostMapping(path = "/getLogDetailsForAttemptedStudents",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<AccumulateAuditTrailsBean> getLogDetailsForAttemptedStudents(@RequestBody AuditTrailExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		AccumulateAuditTrailsBean response = mbaxAuditTrailsService.getLogDetailsForAttemptedStudents( bean );
	
		return new ResponseEntity<>( response, headers, HttpStatus.OK );
	}
	
	@PostMapping(path = "/getLogDetailsForNotAttemptedStudents",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<AccumulateAuditTrailsBean> getLogDetailsForNotAttemptedStudents(@RequestBody AuditTrailExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		AccumulateAuditTrailsBean response = new AccumulateAuditTrailsBean();
		response = mbaxAuditTrailsService.getLogDetailsForNotAttemptedStudents( bean );

		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	}

	
	@PostMapping(path = "/updateStartDateTime", 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestExamBean> updateStartDateTime(@RequestBody TestExamBean bean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, bean.getTestExtendDuration());
		calendar.add(Calendar.MINUTE, -bean.getDuration());
		date = calendar.getTime();
		String extendedTime = formater.format(date);
		bean.setTestStartedOn(extendedTime);

		try {
			
			bean = mbaxAuditTrailsService.updateStartDateTime( bean);
			bean.setErrorRecord(false);
			return new ResponseEntity<>(bean ,headers, HttpStatus.OK);
			
		}catch (Exception e) {

			
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error has occured while updating the details: "+e.getCause());
			return new ResponseEntity<>(bean ,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PostMapping(path = "/updateRefreshCount",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestExamBean> updateRefreshCount(@RequestBody TestExamBean bean)  {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			
			bean = mbaxAuditTrailsService.updateRefreshCount( bean);
			bean.setErrorRecord(false);
			return new ResponseEntity<>(bean ,headers, HttpStatus.OK);
			
		} catch (Exception e) {
			
			
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occured while updating refresh count for the student: "+e.getCause());
			return new ResponseEntity<>(bean ,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	@PostMapping(path = "/saveSupportForOtherIssues",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestExamBean> saveSupportForOtherIssues(@RequestBody TestExamBean bean)  {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		try {
			
			bean = mbaxAuditTrailsService.updateOtherIssues( bean );
			bean.setErrorRecord(false);
			return new ResponseEntity<>(bean ,headers, HttpStatus.OK);
			
		}catch (Exception e) {
			
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occured while saving students issue: "+e.getCause());
			return new ResponseEntity<>(bean ,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	@PostMapping(path = "/extendTestWindow",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestExamBean> extendTestTime(@RequestBody TestExamBean test){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		String est= test.getExtendedStartTime().replaceAll("T", " ");
		String eet= test.getExtendedEndTime().replaceAll("T", " ");
		test.setExtendedStartTime(est+":00"); // added +":00" as was not coming from page by PS
		test.setExtendedEndTime(eet+":00");
		
		try {
			test =  mbaxAuditTrailsService.updateTestTime( test );
		} catch (Exception e) {
			
			test.setErrorRecord(true);
			test.setErrorMessage("An error occured while updating the extended time for the student.");
		}

		return new ResponseEntity<>(test,headers, HttpStatus.OK);	
			
	}
	
}
