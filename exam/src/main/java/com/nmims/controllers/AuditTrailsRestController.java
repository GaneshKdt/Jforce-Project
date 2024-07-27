package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.services.AuditTrailsService;

@RestController
@RequestMapping("m")
public class AuditTrailsRestController {
	
    @Autowired
    AuditTrailsService auditTrailsService;
	
	@PostMapping(path = "/getTestsBySapid", consumes = "application/json", produces = "application/json")
	public ResponseEntity<GetTestsBySapidResponseBean> getTestsBySapid(@RequestBody TestExamBean bean){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		GetTestsBySapidResponseBean response = new GetTestsBySapidResponseBean();

		List<TestExamBean> tests = auditTrailsService.getTestsBySapid(bean.getSapid());

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
		
		StudentsTestDetailsExamBean studentTestDetails = auditTrailsService.getStudentsTestDetailsBySapidAndTestId(sapid,testId);
		
		try {
			test = auditTrailsService.getTestById(testId);
		} catch (Exception e) {
			
		}
		test.setTestJoinURL( auditTrailsService.getTestJoinLink( sapid, bean.getTestId().toString()) );
		
		response.setStudentTestDetails(studentTestDetails);
		response.setTest(test);

		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	
	@PostMapping(path = "/getLogDetailsForAttemptedStudents",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<AccumulateAuditTrailsBean> getLogDetailsForAttemptedStudents(@RequestBody AuditTrailExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		AccumulateAuditTrailsBean response = auditTrailsService.getLogDetailsForAttemptedStudents( bean );
	
		return new ResponseEntity<>( response, headers, HttpStatus.OK );
	}
	
	@PostMapping(path = "/getLogDetailsForNotAttemptedStudents",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<AccumulateAuditTrailsBean> getLogDetailsForNotAttemptedStudents(@RequestBody AuditTrailExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		AccumulateAuditTrailsBean response = new AccumulateAuditTrailsBean();
		response = auditTrailsService.getLogDetailsForNotAttemptedStudents( bean );

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
			
			bean = auditTrailsService.updateStartDateTime( bean);
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
			
			bean = auditTrailsService.updateRefreshCount( bean);
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
			
			bean = auditTrailsService.updateOtherIssues( bean );
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
			test =  auditTrailsService.updateTestTime( test );
		} catch (Exception e) {
			
			test.setErrorRecord(true);
			test.setErrorMessage("An error occured while updating the extended time for the student.");
		}

		return new ResponseEntity<>(test,headers, HttpStatus.OK);	
			
	}
}
