package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.AccumulateAuditTrailsBean;
import com.nmims.beans.GetTestsBySapidResponseBean;
import com.nmims.beans.LogFileAnalysisBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.helpers.MailSender;
import com.nmims.services.AuditTrailsService;

@Controller
public class AuditTrailsController extends BaseController{
    
    @Autowired
    AuditTrailsService auditTrailsService;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@RequestMapping(value = "/admin/studentTestAuditTrailAnalysisForm", method =  RequestMethod.GET)
	public String studentTestAuditTrailAnalysisForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		List<StudentExamBean> studentsList = auditTrailsService.getAllStudentsForAuditTrails();
		m.addAttribute("studentsListForSelect", studentsList);
		return "test/testLogs";
		
	}

	@RequestMapping(value = "/m/getStudentLogFileDetails", method = { RequestMethod.POST }, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<String>> getStudentLogFileDetails(HttpServletRequest request, @RequestBody LogFileAnalysisBean bean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ArrayList<String> logDetails = auditTrailsService.getStudentLogFileDetails( bean );
		
		return new ResponseEntity<>(logDetails, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/lostFocusDetails", method = RequestMethod.GET)
	public ModelAndView lostFocusDetails(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam Long testId ){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}

		ModelAndView modelAndView = new ModelAndView("test/lostFocusDetails");
		TestExamBean bean = new TestExamBean();
		
		try {
			bean = auditTrailsService.getTestById( testId );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modelAndView.addObject("testId", testId);
		modelAndView.addObject("testName", bean.getTestName());
		
		return modelAndView;

	}
	
	@RequestMapping(value = "/m/sendEmailForUnfairMeans", method = { RequestMethod.POST }, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<LostFocusLogExamBean> sendEmailForUnfairMeans(HttpServletRequest request, 
			@RequestBody List<LostFocusLogExamBean> studentList) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		LostFocusLogExamBean status = new LostFocusLogExamBean();
		MailSender mailSender = (MailSender)act.getBean("mailer");
		LostFocusLogExamBean studentDetails = new LostFocusLogExamBean();
		int successCount = 0;
		int failureCount = 0;
		
		for(LostFocusLogExamBean bean : studentList) {
			studentDetails = auditTrailsService.getStudentDetailsForUnfairMeans(bean);
			bean.setEmailId(studentDetails.getEmailId());
			try{
				mailSender.sendMailsForUnfairMeans(bean);
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
	
	@RequestMapping(value = "/admin/getDataForLostFocusReport", method = {RequestMethod.GET, RequestMethod.POST}, 
			consumes = "application/json", produces = "application/json")
	public void getDataForLostFocusReport(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody List<LostFocusLogExamBean> studentList) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return;
		}
		LostFocusLogExamBean studentDetails = new LostFocusLogExamBean();
		ArrayList<LostFocusLogExamBean> studentListForUnfairMeans = new ArrayList<>();
		
		for(LostFocusLogExamBean bean : studentList) {
			studentDetails = auditTrailsService.getStudentDetailsForUnfairMeans(bean);
			bean.setEmailId(studentDetails.getEmailId());
			bean.setFirstName(studentDetails.getFirstName());
			bean.setLastName(studentDetails.getLastName());
			bean.setMobile(studentDetails.getMobile());

			studentListForUnfairMeans.add(bean);
		}
		
		request.getSession().setAttribute("studentListForUnfairMeans", studentListForUnfairMeans);

		return;
	}
	
	@RequestMapping(value = "/Exam_Lost_Focus_Report", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamLostFocusReport(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<LostFocusLogExamBean> studentListForUnfairMeans = (ArrayList<LostFocusLogExamBean>)request.getSession().
			getAttribute("studentListForUnfairMeans");

		return new ModelAndView("lostFocusReportView", "studentListForUnfairMeans",studentListForUnfairMeans);
	}
	
	@RequestMapping(value = "/m/getRecentTest", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<LostFocusLogExamBean>> getRecentTest(HttpServletRequest request){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<LostFocusLogExamBean> recentTest = new ArrayList<>();
		
		try {
			recentTest = auditTrailsService.getRecentTest();
		} catch (Exception e) {
			
		}

		return new ResponseEntity<>(recentTest ,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/unfairMeansDetials", method = RequestMethod.GET)
	public ModelAndView unfairMeansDetials(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView("test/unfairMeansDetails");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		
		ArrayList<LostFocusLogExamBean> testList = new ArrayList<>();
		ArrayList<LostFocusLogExamBean> subjectList = new ArrayList<>();
		
		try {
			
			testList = auditTrailsService.getTestForLostFocus();
			subjectList = auditTrailsService.getSubjectList();
			
		} catch (Exception e) {

			
		}
		
		modelAndView.addObject("testList",testList);
		modelAndView.addObject("subjectList",subjectList);

		return modelAndView;

	}
	
	@RequestMapping(value = "/m/getStudentLostFocusDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<LostFocusLogExamBean>> getStudentLostFocusDetails( @RequestBody List<LostFocusLogExamBean> studentList ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<LostFocusLogExamBean> studentListForUnfairMeans = new ArrayList<>();

		studentListForUnfairMeans = auditTrailsService.getLostFocusListWithStudentAndTestDetails(studentList);
		 
		return new ResponseEntity<>(studentListForUnfairMeans ,headers, HttpStatus.OK);
		
	}

	@RequestMapping(value = "/m/getTestForSubjectAndDuration", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<LostFocusLogExamBean>> getTestForSubjectAndDuration(HttpServletRequest request, @RequestBody LostFocusLogExamBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<LostFocusLogExamBean> recentTest = new ArrayList<>();
		
		try {
			recentTest = auditTrailsService.getTestForSubjectAndDuration(bean);
		} catch (Exception e) {
			
		}

		return new ResponseEntity<>(recentTest ,headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/saveReasonForLostFocus", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestExamBean> saveReasonForLostFocus(@RequestBody TestExamBean bean)  {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		try {
			auditTrailsService.updateOtherIssues(bean);
			bean.setErrorRecord(false);
			return new ResponseEntity<>(bean ,headers, HttpStatus.OK);
		}catch (Exception e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occured while saving students issue: "+e.getCause());
			return new ResponseEntity<>(bean ,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@RequestMapping(value = "/m/markCopyCase", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<LostFocusLogExamBean> markCopyCase(HttpServletRequest request, @RequestBody List<LostFocusLogExamBean> studentList){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		LostFocusLogExamBean response = markCopyCase( studentList );

		return new ResponseEntity<>(response ,headers, HttpStatus.OK);
	}
	
	private LostFocusLogExamBean markCopyCase( List<LostFocusLogExamBean> studentList ) {

		LostFocusLogExamBean response = new LostFocusLogExamBean();
		
		for( LostFocusLogExamBean bean : studentList) {
			
			try {
				auditTrailsService.markCopyCaseForLostFocus(bean);
				response.setSuccess(true);
				response.setSuccessMessage("Updates CopyCase Successfully: Marked");
			} catch (Exception e) {
				
				response.setSuccess(false);
				response.setSuccessMessage("An error occured while updating CopyCase");
			}
			
		}
		
		return response;
	}
	
	@RequestMapping(value = "/m/unmarkCopyCase", method = RequestMethod.POST, 
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<LostFocusLogExamBean> unmarkCopyCase(HttpServletRequest request, @RequestBody List<LostFocusLogExamBean> studentList){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		LostFocusLogExamBean response = new LostFocusLogExamBean();

		for( LostFocusLogExamBean bean : studentList) {
			
			try {
				auditTrailsService.unmarkCopyCaseForLostFocus(bean);
				response.setSuccess(true);
				response.setSuccessMessage("Updates CopyCase Successfully: Unmarked");
			} catch (Exception e) {
				
				response.setSuccess(false);
				response.setSuccessMessage("An error occured while updating CopyCase");
			}
			
		}

		return new ResponseEntity<>(response ,headers, HttpStatus.OK);
	}
	
}
