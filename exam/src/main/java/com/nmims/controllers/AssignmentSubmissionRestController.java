package com.nmims.controllers;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentHistoryResponseBean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.AssignmentResponseMobileBean;
import com.nmims.beans.BodBean;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseListBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.AssignmentService;
import com.nmims.services.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("m")
public class AssignmentSubmissionRestController extends BaseController {
	
	@Autowired
	ApplicationContext act;

	@Autowired
	StudentService studentService;
	
	@Autowired
	AssignmentService asgService;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	
//	@Value( "${ACCOUNT_ID}" )
//	private String ACCOUNT_ID;
//	@Value( "${V3URL}" )
//	private String V3URL;
	@Value( "${ASSGN_PAYMENT_RETURN_URL_MOBILE}" )
	private String ASSGN_PAYMENT_RETURN_URL_MOBILE;
	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE;
	
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	
	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = LoggerFactory.getLogger(AssignmentSubmissionController.class);
	private static final Logger aws_logger = LoggerFactory.getLogger("fileMigrationService");
	private static final Logger assg_logger = LoggerFactory.getLogger("assignmentSubmission");
	private static final Logger assg_logger_mobile = LoggerFactory.getLogger("assignmentSubmissionMobile");
	private static final Logger razorpayLogger = LoggerFactory.getLogger("webhook_payments");
	
	private static final Long MAX_FILE_SIZE_LIMIT = 6291456L;				//6 MB in bytes (6 * 1024 * 1024)
	
	@Autowired
	ResitExamBookingDAO resitExamBookingDAO; 
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;

	@Value( "${SUBMITTED_ASSIGNMENT_FILES_PATH}" )
	private String SUBMITTED_ASSIGNMENT_FILES_PATH;

	@Value( "${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}" )
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;
	
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	ExamBookingHelper examBookingHelper;
	
//	@Value( "${SECURE_SECRET}" )
//	private String SECURE_SECRET; // secret key;

	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	private ArrayList<String> subjectList = null; 
	
	private int examFeesPerSubject = 500;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private static final String ASSIGNMENT_MANUALLY_APPROVED  = "Online Payment Manually Approved";
	private static final String ASSIGNMENT_PAYMENT_SUCCESSFUL = "Online Payment Successful";
	private final String NOT_BOOKED = "Not Booked";
	private final String BOOKED = "Booked";

	private final String BOOKING_SUCCESS_MSG = "Your Assignment Fees is received. Please proceed to Assignment submission.";
	@Value("${LOCAL_ACTIVE_PROCESSORS}")
	private int LOCAL_ACTIVE_PROCESSORS;
	
	private static final Logger assignmentMakeLive = LoggerFactory.getLogger("assignmentMakeLive");
	
	
	
	@PostMapping(path = "/viewAssignmentsForm", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ExamAssignmentResponseBean> mViewAssignmentsForm(HttpServletRequest request,
			@RequestBody Person input) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		TestDAOForRedis daoForRedis = (TestDAOForRedis) act.getBean("testDaoForRedis");

		if (daoForRedis.checkForFlagValueInCache("movingResultsToCache", "Y")) {

			return new ResponseEntity<>(new ExamAssignmentResponseBean(), headers, HttpStatus.OK);
		}

		String sapId = input.getSapId();
		assg_logger.info("Pg visit /viewAssignmentsForm Sapid - {}",sapId);
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		// ExamAssignmentResponse response = asgService.getAssignments(sapId);
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		List<AssignmentFileBean> quickAssignments = dao.getQuickAssignmentsForSingleStudent(sapId);
		request.getSession().setAttribute("quickAssignments", quickAssignments);

		if (quickAssignments.size() == 0) {
			response.setError("true");
			response.setErrorMessage("No Assignments allocated to you.");
		}
		ArrayList<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<AssignmentFileBean>();
		ArrayList<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<AssignmentFileBean>();
		int failSubjectSubmissionCount = 0;
		int currentSemSubmissionCount = 0;
		ArrayList<String> failSubjects = new ArrayList<String>();
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentsData(sapId);
		AssignmentLiveSetting resitLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
		
		//get most recent marks live from cache
		String asgMarksLiveMonth = dao.getLiveAssignmentMarksMonth();
		String asgMarksLiveYear = dao.getLiveAssignmentMarksYear();  
		String marksLiveYearMonth = asgMarksLiveMonth+"-"+asgMarksLiveYear;
		
		for (AssignmentFileBean q : quickAssignments) {
			if (!q.getCurrentSemSubject().equalsIgnoreCase("Y") 
					&& resitLive.getExamYear().equalsIgnoreCase(q.getYear()) && resitLive.getExamMonth().equalsIgnoreCase(q.getMonth()) ) {
				failSubjectsAssignmentFilesList.add(q);
				failSubjects.add(q.getSubject());
				if ("Submitted".equals(q.getStatus())) {
					failSubjectSubmissionCount++;
				}
			}
		}
		// For ANS cases, where result is not declared, failed subject will also be
		// present in Current sem subject.
		// Give preference to it as Failed, so that assignment can be submitted and
		// remove from Current list
		// If result is live, hide assignments
		for (AssignmentFileBean q : quickAssignments) {
			if (q.getCurrentSemSubject().equalsIgnoreCase("Y") 
					&& !(q.getMonth()+"-"+q.getYear()).equalsIgnoreCase(marksLiveYearMonth)
					&& !(failSubjects.contains(q.getSubject()))) {
				currentSemAssignmentFilesList.add(q);
				if ("Submitted".equals(q.getStatus())) {
					currentSemSubmissionCount++;
				}
			}
		}
		String currentSemEndDateTime = "";
		if (currentSemAssignmentFilesList.size() > 0) {
			currentSemEndDateTime = currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19);
			response.setCurrentSemEndDateTime(currentSemEndDateTime);
		}
		String failSubjectsEndDateTime = "";
		if (failSubjectsAssignmentFilesList.size() > 0) {
			failSubjectsEndDateTime = failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
			response.setFailSubjectsEndDateTime(failSubjectsEndDateTime);
			//response.setFailedSemSubjectsCount(failSubjectsAssignmentFilesList.size());
			response.setFailSubjectsCount(failSubjectsAssignmentFilesList.size());
		}
		response.setCurrentSemSubjectsCount(currentSemAssignmentFilesList.size());
		response.setFailSubjectSubmissionCount(failSubjectSubmissionCount);
		response.setCurrentSemSubmissionCount(currentSemSubmissionCount);
		response.setCurrentSemAssignmentFilesList(currentSemAssignmentFilesList);
		response.setFailSubjectsAssignmentFilesList(failSubjectsAssignmentFilesList);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	
	
	@PostMapping(path = "/submitAssignment", headers = "content-type=multipart/form-data")
	public ResponseEntity<ExamAssignmentResponseBean> MsubmitAssignment(HttpServletRequest request,
			AssignmentFileBean assignmentFile) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		// ModelAndView modelnView = new ModelAndView("assignment/assignment");
		assg_logger_mobile.info("Pg visit /m/submitAssignment Sapid - {} Subject - {} ",assignmentFile.getSapId(),assignmentFile.getSubject());
		response = asgService.submitAssignment(assignmentFile);
		if (response.getError() != null) {
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	
	
	@RequestMapping(value = "/viewPreviousAssignments", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<AssignmentHistoryResponseBean> mViewPreviousAssignments(HttpServletRequest request,
			@RequestBody Person input){
		
		AssignmentHistoryResponseBean response = new AssignmentHistoryResponseBean();
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		

		AssignmentFileBean searchBean = new AssignmentFileBean();
		searchBean.setSapId(input.getSapId());
		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			response.setError("true");
			response.setErrorMessage("No Assignment Submissions found.");
		} else {
			response.setError("false");
			response.setData(assignmentFilesList);
		}
		return new ResponseEntity<AssignmentHistoryResponseBean>(response, HttpStatus.OK);
	}
	
	
//	api to update registration data, add masterkey from Students
	@PostMapping(path = "/updateConsumerPrgStructIdForProgChange")
	public void updateConsumerPrgStructIdForProgChange(HttpServletRequest request
			) throws Exception {

		asgService.updateConsumerPrgStructIdForProgChange();

	}
	
	@RequestMapping(value = "/viewSingleAssignment", method = RequestMethod.POST)
	public ResponseEntity<AssignmentResponseMobileBean> mViewSingleAssignment(HttpServletRequest request1,
			@RequestBody AssignmentFileBean assignmentFile) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		AssignmentResponseMobileBean assignmentResponseMobileBean = new AssignmentResponseMobileBean();
		
		//String sapId = assignmentFile.getUserId();  
		String sapId = assignmentFile.getSapId();  

		StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		assg_logger_mobile.info("Pg visit /m/viewSingleAssignment Sapid - {} Subject - {}",sapId,assignmentFile.getSubject());
		StudentExamBean student = sMarksDao.getSingleStudentsData(sapId);
		try {
		if(student.getWaivedOffSubjects().contains(assignmentFile.getSubject())){
			//If subject is waived off, dont go to assignment submission page.
			assignmentResponseMobileBean.setStatus("error");
			assignmentResponseMobileBean.setErrorMessage(assignmentFile.getSubject() + " subject is not applicable for you.");
			return new ResponseEntity<>(assignmentResponseMobileBean, headers, HttpStatus.OK);
			//return viewAssignmentsForm(request, response, m);
		}
		boolean isOnline = isOnline(student);

		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
	
		List<AssignmentFileBean> assignmentFiles = dao.getQuickAssignmentsForSingleStudent(sapId,
				assignmentFile.getSubject(), assignmentFile.getYear(), assignmentFile.getMonth());
		if (assignmentFiles.size() > 0) {
			for (AssignmentFileBean asg : assignmentFiles) {
				// if(asg.getCurrentSemSubject().equalsIgnoreCase("N")) {
				assignmentFile = asg;
				// }
			}
		}

		

		String startDate = assignmentFile.getStartDate();
		startDate = startDate.replaceAll("T", " ");
		assignmentFile.setStartDate(startDate.substring(0,19));

		String endDate = assignmentFile.getEndDate();
		endDate = endDate.replaceAll("T", " ");
		assignmentFile.setEndDate(endDate.substring(0,19));

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);

		if(isOnline){//Applicble for online students only
			int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFile.getSubject(), sapId);
			if(pastCycleAssignmentAttempts >=2 && !"Submitted".equals(assignmentFile.getStatus())){//Same Condition added by Vikas 02/08/2016//
				boolean hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFile.getSubject(), sapId); //check if Assignment Fee Paid for Current drive 
				if(!hasPaidForAssignment){
					assignmentResponseMobileBean.setAssignmentPaymentPending("Y");
				}
			}
		}
		assignmentResponseMobileBean.setSubmissionAllowed("true");
		assignmentResponseMobileBean.setMaxAttempts(maxAttempts);
		assignmentResponseMobileBean.setAssignmentFile(assignmentFile);
		assignmentResponseMobileBean.setYearList(currentYearList);
	//	assignmentResponseMobileBean.setSubjectList(getSubjectList());
		assignmentResponseMobileBean.setSubject(assignmentFile.getSubject());
		assignmentResponseMobileBean.setSubjectForPayment(assignmentFile.getSubject());
		

		ArrayList<String> timeExtendedStudentIdSubjectList =  dao.assignmentExtendedSubmissionTime();
		assignmentResponseMobileBean.setTimeExtendedStudentIdSubjectList(timeExtendedStudentIdSubjectList);

		
		//return modelnView;
		assignmentResponseMobileBean.setStatus("success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assg_logger_mobile.error("Exception Error /viewSingleAssignment  Sapid - {} Subject - {} Error - {}",sapId,assignmentFile.getSubject(),e);
			assignmentResponseMobileBean.setStatus("error");
			assignmentResponseMobileBean.setErrorMessage(("Exception Error /viewSingleAssignment" + e.getMessage()));
		}
		return new ResponseEntity<>(assignmentResponseMobileBean, headers, HttpStatus.OK);

	}
	
	
	@RequestMapping(value = "/selectAssignmentPaymentSubjectsForm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<AssignmentResponseMobileBean> MselectAssignmentPaymentSubjectsForm(HttpServletRequest request,
			@RequestBody AssignmentFileBean assignmentFile) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		AssignmentResponseMobileBean response = new AssignmentResponseMobileBean();
		// ModelAndView modelnView = new ModelAndView("assignment/assignment");
		
		

		String sapid = assignmentFile.getSapId();
		ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		
		String subjectForPayment = assignmentFile.getSubject();
		// System.out.println("subjectForPayment:"+subjectForPayment);
		int subjectsToPay = 0;
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		try{

			ExamBookingExamBean examBooking = new ExamBookingExamBean();
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			
			StudentExamBean student = resitExamBookingDAO.getSingleStudentWithValidity(sapid);
			
			if("Offline".equals(student.getExamMode())){
			//	setError(request, "You are not authorized to register for Resit Examination");
			//	modelnView.addObject("examBooking", examBooking);
				response.setStatus("error");
				response.setErrorMessage("You are not authorized to register for Resit Examination");
				response.setExamBooking(examBooking);
			}
			
			String mostRecentTimetablePeriod = sDao.getMostRecentResitAssignmentPeriod();
			ExamOrderExamBean exam = sDao.getUpcomingResitAssignmentExam();
			examBooking.setYear(exam.getYear());
			examBooking.setMonth(exam.getMonth());
			response.setMostRecentTimetablePeriod(mostRecentTimetablePeriod);
			//modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
			//request.getSession().setAttribute("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

			/*ArrayList<String> subjectsNeedingPayment = (ArrayList<String>)dao.getSubjectsNeedingAssignmentPayments(sapid);*/
			ArrayList<String> subjectsNeedingPayment = new ArrayList<String>();
			subjectsNeedingPayment.add(subjectForPayment);
			bookedSubjects = (ArrayList<String>)dao.getSubjectsMadeAssignmentPayments(sapid);

			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())  ){
					if(subjectsNeedingPayment.contains(bean.getSubject())){
						bean.setCanBook("Yes");
						bean.setBookingStatus(NOT_BOOKED);
						applicableSubjectsList.add(bean);
						applicableSubjects.add(bean.getSubject());
						subjectsToPay++;
					}

					if(bookedSubjects.contains(bean.getSubject())){
						bean.setCanBook("No");
						bean.setCanFreeBook("No");
						bean.setBookingStatus(BOOKED);
						applicableSubjects.remove(bean.getSubject());
					}
				}
			}

			examBooking.setApplicableSubjects(applicableSubjects);
			response.setApplicableSubjectsList(applicableSubjectsList);
			response.setApplicableSubjectsListCount(applicableSubjectsList.size());
			response.setExamBooking(examBooking);
			response.setSubjectsToPay(subjectsToPay);
			response.setFeesPerSubject(examFeesPerSubject);

		}catch(Exception e){
		//	e.printStackTrace();
			response.setStatus("error");
			response.setErrorMessage("Error in getting subjects.");
		}
	
		
		
//       if(response.getError() !=null) {
//	     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//       }
		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	

	
	@RequestMapping(value = "/assignmentPaymentInitiate",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<AssignmentResponseMobileBean> mAssignmentPaymentInitiate(HttpServletRequest request1,@RequestBody AssignmentResponseMobileBean assignmentFile) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		AssignmentResponseMobileBean response = new AssignmentResponseMobileBean();
	
//		ModelAndView modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
		int noOfSubjects = 0;
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			List<String> subjects = new ArrayList<String>(assignmentFile.getApplicableSubjects());
//			for (int i = 0; i < assignmentFile.getExamBooking().getApplicableSubjects().size(); i++) {
//				subjects.add(assignmentFile.getExamBooking().getApplicableSubjects().get(i));
//				
//			}
			 
			
			String sapid = assignmentFile.getUserId();

			String trackId = sapid + System.currentTimeMillis() ;
			//---------------------------------------------------------request.getSession().setAttribute("trackId", trackId);

			
			Map<String, PassFailExamBean> subjectPassFailBeanMap = getSubjectPassFailBeanMap(sapid);
			StudentExamBean student = sDao.getSingleStudentsData(sapid);
			boolean isCertificate = isStudentOfCertificate(student.getProgram());
			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			//ArrayList<String> selectedCenters = examBooking.getSelectedCenters();
			String examYear = assignmentFile.getExamBooking().getYear();
			String examMonth = assignmentFile.getExamBooking().getMonth();

			noOfSubjects = subjects.size();
			if(subjects.contains("Project") || subjects.contains("Module 4 - Project")){
				noOfSubjects++;
			}
			for (int i = 0; i < subjects.size(); i++) {
				String subject = subjects.get(i);
				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				PassFailExamBean failBean = subjectPassFailBeanMap.get(subject);
				bean.setSapid(sapid);
				bean.setSubject(subject);
				bean.setYear(examYear);
				bean.setMonth(examMonth);
				try {
					bean.setProgram(failBean.getProgram());
					bean.setSem(failBean.getSem());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					bean.setProgram(student.getProgram());
					bean.setSem(student.getSem());
				}
				bean.setTrackId(trackId);
				bean.setAmount(examFeesPerSubject * noOfSubjects+"");
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				bean.setPaymentOption(assignmentFile.getPaymentOptionName());
				bean.setDeviceType("Mobile");
				bookingsList.add(bean);
			}


			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			dao.upsertPaymentStatusInQuickTable(sapid, bookingsList);
		String msg =	dao.upsertOnlineInitiationTransaction(sapid, bookingsList);
					
		if(msg=="success") {
			int totalFees = examFeesPerSubject * noOfSubjects;
			String totalFeesString = String.valueOf((isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE) + "":totalFees));
			response.setGoToPaymentGatewayUrl("assignmentMobileGoToPaymentGateway?sapId="+sapid+"&trackId="+trackId+"&totalFees="+totalFeesString+"&paymentOptionName="+assignmentFile.getPaymentOptionName());
			response.setStatus("success");
			//return new ModelAndView(new RedirectView("pay"), model);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}else {
			response.setStatus("error");
			response.setErrorMessage("Error in initiating Online transaction. Error: "+msg);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
			
		
		}catch(Exception e){
			//e.printStackTrace();
			//modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
			response.setStatus("error");
			response.setErrorMessage("Error in initiating Online transaction. Error: "+e.getMessage());
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}

	}
	
	
	@RequestMapping(value = "/assignmentMobileGoToPaymentGateway",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView proceedToPaymentGatewaySr(HttpServletRequest request,String sapId,String trackId,int totalFees,String paymentOptionName, HttpServletResponse response) {
	
		ModelMap model = new ModelMap();
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student =  sDao.getSingleStudentsData(sapId);
		String message = "Assignment fees for "+sapId;
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> subjects = dao.getSubjectsUsingTrackId(trackId);
		boolean isCertificate = isStudentOfCertificate(student.getProgram());

		request.getSession().setAttribute("userId",sapId);
		request.getSession().setAttribute("totalFees",String.valueOf(totalFees));
		request.getSession().setAttribute("subjects", subjects);
		request.getSession().setAttribute("trackId", trackId);
		//request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
		//request.getSession().setAttribute("paymentOption","hdfc");
		
		
	//	request.getSession().setAttribute("paymentOption",paymentOptionName);
		/////////////////////////////
		
		ModelAndView mv = new ModelAndView("payment");
		mv.addObject("track_id", trackId);
		mv.addObject("sapid", sapId);
		mv.addObject("type", "Assignment");
		mv.addObject("amount", isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE):totalFees);
		mv.addObject("description", message);
		mv.addObject("source", "mobile");
		mv.addObject("portal_return_url", ASSGN_PAYMENT_RETURN_URL_MOBILE);
		mv.addObject("created_by", sapId);
		mv.addObject("updated_by", sapId);
		mv.addObject("mobile", student.getMobile());
		mv.addObject("email_id", student.getEmailId());
		mv.addObject("first_name", student.getFirstName());
		

		return mv;
		///////////////////////////
		
//		fillPaymentParametersInMapMobile(model, student, totalFees, trackId, message,isCertificate);
//		return new ModelAndView(new RedirectView("pay"), model);
		
	}
	
	@RequestMapping(value = "/assignmentFeesResponseMobile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView assignmentFeesResponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		 saveAllTransactionDetails(request);
		 
		    //String typeOfPayment = (String)request.getParameter("PaymentMethod"); String
		    String trackId = (String)request.getSession().getAttribute("trackId"); 
		    String totalFees = String.valueOf(request.getSession().getAttribute("totalFees"));
		  
		    //boolean isHashMatching = isHashMatching(request); 
		    boolean isAmountMatching = isAmountMatching(request, totalFees); 
		    boolean isTrackIdMatching = isTrackIdMatching(request, trackId); 
		    boolean isSuccessful = isTransactionSuccessful(request);
		    
		    String errorMessage = null;

			if(!isSuccessful){
				errorMessage = "Error in processing payment. Error: " + request.getParameter("error")+ " Code: "+request.getParameter("response_code");
			}

			if(!isAmountMatching){
				errorMessage = "Error in processing payment. Error: Fees " + totalFees + " not matching with amount paid "+request.getParameter("response_amount");
			}
				
			if(!isTrackIdMatching){
				errorMessage = "Error in processing payment. Error: Track ID: "+trackId + " not matching with Merchant Ref No. "+request.getParameter("merchant_ref_no");
			}
			if(errorMessage != null){
				
				if(!"paytm".equalsIgnoreCase(request.getParameter("payment_option"))) {
					try {
						AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
						String sapid = (String)request.getSession().getAttribute("userId");
						ExamBookingTransactionBean responseBean = new ExamBookingTransactionBean();
						
						responseBean.setSapid(sapid);
						responseBean.setPaymentOption(request.getParameter("payment_option"));
						responseBean.setTrackId(trackId);
						responseBean.setError(errorMessage);
						
						dao.saveAssignmentPaymentTransactionError(responseBean);
					} catch (Exception e) {
						logger.error("Error in saving assignment transaction failed status,:"+e);
					}
				}
				setError(request, errorMessage);
				return  new ModelAndView( "redirect:" + "/m/paymentResponse?status=error&message="+errorMessage);
			}else{
				return saveSuccessfulTransaction(request, response, model);
				// return new ModelAndView("redirect:" + "/m/paymentResponse?status=success&message="+request.getAttribute("successMessage"));
			}

//		if(errorMessage != null){
//			return new ModelAndView("redirect:" + "/m/paymentResponse?status=error&message="+errorMessage);
//		}else{
//			 saveSuccessfulTransaction(request, response, model);
//			 if(request.getAttribute("success")=="true") {
//				 return new ModelAndView("redirect:" + "/m/paymentResponse?status=success&message="+request.getAttribute("successMessage"));
//
//			 }else {
//				 return new ModelAndView( "redirect:" + "/m/paymentResponse?status=error&message="+request.getAttribute("errorMessage"));
// 
//			 }
//		}
	}
	@RequestMapping(value = "/paymentResponse", method = {RequestMethod.GET})
	public ModelAndView paymentResponse(HttpServletRequest request, HttpServletResponse response,String status,String message) {
		
		return new ModelAndView("paymentResponse");
	}
	
	private void saveAllTransactionDetails(HttpServletRequest request) {
		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			String trackId = (String)request.getSession().getAttribute("trackId");
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			bean.setSapid(sapid);
			bean.setTrackId(trackId);

			bean.setResponseMessage(request.getParameter("response_message"));
			bean.setTransactionID(request.getParameter("transaction_id"));
			bean.setRequestID(request.getParameter("request_id"));
			bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
			bean.setSecureHash(request.getParameter("secure_hash"));
			bean.setRespAmount(request.getParameter("response_amount"));
			bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
			bean.setResponseCode(request.getParameter("response_code"));
			bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
			//bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));

			dao.insertOnlineTransaction(bean);

		} catch (Exception e) {
//			return "Error: " + e.getMessage();
		}

	}
	
	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
	

		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String sapid = (String)request.getSession().getAttribute("userId");
		String trackId = (String)request.getSession().getAttribute("trackId");
		
		try {
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			Map<String, String> statusAndSubject = dao.getAssignmentPaymentStatusByTrackId(trackId);
			if (statusAndSubject != null) {
				if (ASSIGNMENT_MANUALLY_APPROVED.equalsIgnoreCase(statusAndSubject.get("status"))
						|| ASSIGNMENT_PAYMENT_SUCCESSFUL.equalsIgnoreCase(statusAndSubject.get("status"))) {

					razorpayLogger.info(
							"payment was already marked successfull by webhook so return model and view for track id "
									+ trackId);
					request.setAttribute("successMessage", BOOKING_SUCCESS_MSG);
					return  new ModelAndView("redirect:" + "/m/paymentResponse?status=success&message="+request.getAttribute("successMessage"));
				}
			}
			
			ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
			
			bean.setSapid(sapid);
			bean.setTrackId(trackId);

			bean.setResponseMessage(request.getParameter("response_message"));
			bean.setTransactionID(request.getParameter("transaction_id"));
			bean.setRequestID(request.getParameter("request_id"));
			bean.setMerchantRefNo(request.getParameter("merchant_ref_no"));
			bean.setSecureHash(request.getParameter("secure_hash"));
			bean.setRespAmount(request.getParameter("response_amount"));
			bean.setRespTranDateTime(request.getParameter("response_transaction_date_time"));
			bean.setResponseCode(request.getParameter("response_code"));
			bean.setRespPaymentMethod(request.getParameter("response_payment_method"));
			//bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("payment_id"));
			bean.setError(request.getParameter("error"));
			bean.setDescription(request.getParameter("description"));
			bean.setPaymentOption(request.getParameter("payment_option"));
			
			//List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForOnlineTransaction(bean);

			//Below method is made using single Connection to ensure Commit and Rollback
			dao.updateSeatsForOnlineUsingSingleConnection(bean);
			dao.setPaymentStatusInQuickTable(bean);
			//request.getSession().setAttribute("examBookings", examBookings);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");

			request.setAttribute("success","true");
			request.setAttribute("successMessage",BOOKING_SUCCESS_MSG);

			MailSender mailSender = (MailSender)act.getBean("mailer");
			
			StudentExamBean student = sDao.getSingleStudentsData(sapid);
			ArrayList<String> subjects= (ArrayList<String>)request.getSession().getAttribute("subjects");
			String mostRecentTimetablePeriod = (String)request.getSession().getAttribute("mostRecentTimetablePeriod");
			examBookingHelper.createAndUploadAssignmentFeeReceipt(sapid,trackId);
			mailSender.sendAssignmentBookingSummaryEmail(student, subjects, mostRecentTimetablePeriod);
		} catch (Exception e) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Saving Successful Transaction", e);

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			 return new ModelAndView( "redirect:" + "/m/paymentResponse?status=error&message="+request.getAttribute("errorMessage"));
			//return new ModelAndView("assignment/selectAssignmentPaymentSubjects");
			//return "redirect:" + "/m/paymentResponse?status=error&message=Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.";
		}
		return new ModelAndView("redirect:" + "/m/paymentResponse?status=success&message="+request.getAttribute("successMessage"));

	}
	

	private boolean isAmountMatching(HttpServletRequest request, String totalFees) {
		try {
			double feesSent = Double.parseDouble(totalFees);
			double amountReceived = Double.parseDouble(request.getParameter("response_amount"));

			// System.out.println("feesSent: "+feesSent + " amountReceived: "+amountReceived);
			if(feesSent == amountReceived){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}

	private boolean isTrackIdMatching(HttpServletRequest request, String trackId) {
		if(trackId != null && trackId.equals(request.getParameter("merchant_ref_no"))){
			return true;
		}else{
			return false;
		}
	}
	private boolean isTransactionSuccessful(HttpServletRequest request) {
		String transaction_status = request.getParameter("transaction_status");
		if("Payment Successfull".equalsIgnoreCase(transaction_status)) {
			return true;
		}else {
			return false;
		}
//		String error = request.getParameter("Error");
//		//Error parameter should be absent to call it successful 
//		if(error == null){
//			//Response code should be 0 to call it successful
//			String responseCode = request.getParameter("ResponseCode");
//			if("0".equals(responseCode)){
//				return true;
//			}else{
//				return false;
//			}
//		}else{
//			return false;
//		}

	}
	private String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();

		m.update(data,0,data.length);

		BigInteger i = new BigInteger(1,m.digest());

		String hash = String.format("%1$032X", i);

		return hash;
	}
	
	
	private Map<String, PassFailExamBean> getSubjectPassFailBeanMap(String sapid) {
		ArrayList<PassFailExamBean> failList = (ArrayList<PassFailExamBean>)resitExamBookingDAO.getFailedSubjectsList(sapid);
		Map<String, PassFailExamBean> subjectPassFailBeanMap = new HashMap<String, PassFailExamBean>();
		if(failList != null){
			for (PassFailExamBean passFailBean : failList) {
				subjectPassFailBeanMap.put(passFailBean.getSubject(), passFailBean);
			}
		}

		return subjectPassFailBeanMap;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			// System.out.println("Refresh programSubjectMappingList inside AssignmentPaymentController");
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	private ArrayList<String> getFailSubjectsNames(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> failSubjectList = dao.getFailSubjectsNamesForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<String> getANSSubjectNames(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> failSubjectList = dao.getANSNotProcessedSubjectNames(student.getSapid());
		return failSubjectList;
	}
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			// System.out.println("Refresh getSubjectList inside AssignmentSubmissionController");
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
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
	
	@PostMapping(value="/admin/submitAssignmentLiveData")
	public ResponseEntity<ResponseListBean> submitAssignmentLiveData(HttpServletRequest request,final AssignmentFilesSetbean filesSet){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseListBean response = new ResponseListBean();
		Executor executor = Executors.newFixedThreadPool(LOCAL_ACTIVE_PROCESSORS);
		List<Integer> completeStatus = (List<Integer>) Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
		assignmentMakeLive.info("---------------------------------Started  Assignment Make Live---------------------------------");
		try
		{
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			
			String userId = (String)request.getSession().getAttribute("userId");
		  

						// If Any Option is Selected Is "All"
			ArrayList<String> consumerProgramStructureIds = new ArrayList<String>();
			try {
				consumerProgramStructureIds = dao.getconsumerProgramStructureIds(filesSet.getProgramId(),filesSet.getProgramStructureId(),filesSet.getConsumerTypeId());
			} catch (Exception e1) {
				assignmentMakeLive.error("Exception Error getconsumerProgramStructureIds Error-{} ",e1);
				//e1.printStackTrace();
			}
			assignmentMakeLive.info("Assignment Make Live Masterkey's : {}", consumerProgramStructureIds);
			
			try {
				dao.batchInsertOfMakeAssignmentLive(filesSet,consumerProgramStructureIds);
				assignmentMakeLive.info("Successfully Saved Masterkey's in Assignment Live Setting table");
			} catch (Exception e) {
				assignmentMakeLive.error("Exception Error batchInsertOfMakeAssignmentLive Error-{} ",e);
				//e.printStackTrace();
			}
			
			assignmentMakeLive.info("Getting Students List for Assignments Make Live......");
			ArrayList<StudentExamBean> students =  dao.getStudentsApplicableForAssignments(filesSet,consumerProgramStructureIds);
			assignmentMakeLive.info(" Assignment Make Live Students list: {}", students.size());
//			double  i=1;
			
//		    final List<Future<?>> futures = new ArrayList<>();
//		    final AtomicInteger studentCount = new AtomicInteger(1);
//		    List<Integer> completeStatus = (List<Integer>) Arrays.asList(10,20,30,40,50,60,70,80,90,100);
//		    try {
//		    	Future<?> future = executor.submit(
//			
//		    			() -> students.parallelStream().forEach((student) -> {
//				
////	        	assignmentMakeLive.info("entry for student:"+i+"/"+students.size());
////	        	i++;
//		    				int runningCount = studentCount.get();
//		    				studentCount.incrementAndGet();
//		    				assignmentMakeLive.info("Entry for Sapid: {} and count: {}",student.getSapid(),runningCount);
//		    					try {
//									ExamAssignmentResponseBean respons = asgService.getAssignments(student.getSapid(),filesSet.getLiveType());
//									List<AssignmentFileBean>allAssignmentFilesList = respons.getAllAssignmentFilesList(); 
//									ArrayList<String> subjectsNotAllowedToSubmit=respons.getSubjectsNotAllowedToSubmit();
//    		 
//									for(AssignmentFileBean assignment : allAssignmentFilesList) {
//										//String status = assignment.getStatus();  
//										if(subjectsNotAllowedToSubmit!=null ) {
//											if(subjectsNotAllowedToSubmit.contains(assignment.getSubject())){
//												assignment.setStatus("Results Awaited");
//												assignment.setSubmissionAllow("N");
//											}  
//										}
//									}
//									dao.insertIntoQuickAssignments(userId,student.getSapid(),allAssignmentFilesList,filesSet.getExamYear(),filesSet.getExamMonth());
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									//e.printStackTrace();
//									assignmentMakeLive.error("Error in Assignment make live Sapid - {} Count - {} Error- {}",student.getSapid(),runningCount,e);
//								}
//					
//		    				
//		    				float completedCount = ((float) runningCount/(float) students.size()) * 100;
//		    				assignmentMakeLive.info(" Assignment make live completed status - {}%",(int) completedCount);
//		    				int countLeft = students.size() - runningCount;
//		    				if(completeStatus.contains((int) completedCount)) {
//		    					assignmentMakeLive.info(" Assignment make live {}% completed || Students inserted entries count = {} || Students left count = {}/{}",(int) completedCount,runningCount,countLeft,students.size());
//		    				}
//		    			})
//		    	);
//		    	futures.add(future);
//		    	for(Future<?> f: futures) {
//		    	  	f.get();
//		    	}
//		    }
//			catch(InterruptedException | ExecutionException ex) {
//				assignmentMakeLive.error("Error Assignemnt Make Live Multi-threading : {}", ex);
//				response.setError(ex.getMessage());
//				return new ResponseEntity<>(response,headers,HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			finally {
//				executor.shutdown();
//			}
		    
		    
		    List<CompletableFuture<String>> pageContentFutures =
		    		IntStream.range(0, students.size())
		    		.mapToObj(index->
		    					getStudentsAssignment(executor, userId,students.get(index), filesSet, students.size(),index+1, completeStatus )
		    				)
		    		.collect(Collectors.toList());
		    
		    assignmentMakeLive.info("pageContentFutures "+pageContentFutures.size());
		    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
		    		pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
		    		);
		    assignmentMakeLive.info("allFutures ");
		    CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v ->{
		    	return pageContentFutures.stream()
		    			.map(pageContentFuture -> pageContentFuture.join())
		    			.collect(Collectors.toList());
		    });
		    assignmentMakeLive.info("allPageContentsFuture ");
		    CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents ->{
		    	return pageContents.stream()
		    			.filter(pageContent -> pageContent.contains("CompletableFuture"))
		    			.count();
		    });
		    assignmentMakeLive.info("Number of Web Pages having CompletableFuture keyword - ",countFuture.get());
			assignmentMakeLive.info("---------------------------------Ended  Assignment Make Live---------------------------------");

			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		catch(Exception e)
		{
			assignmentMakeLive.error("Error in Assignment make live - {}",e);
			response.setError(e.getMessage());
			return new ResponseEntity<>(response,headers,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	CompletableFuture<String> getStudentsAssignment( Executor executor, String adminId, StudentExamBean student, AssignmentFilesSetbean filesSet, int listSize, int runningCount, List<Integer> completeStatus){
		return CompletableFuture.supplyAsync(() -> {
			
			assignmentMakeLive.info("Entry for Sapid: {} and count: {}", student.getSapid(), runningCount);
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			try {
				ExamAssignmentResponseBean respons = asgService.getAssignments(student.getSapid(),
						filesSet.getLiveType());
				List<AssignmentFileBean> allAssignmentFilesList = respons.getAllAssignmentFilesList();
				ArrayList<String> subjectsNotAllowedToSubmit = respons.getSubjectsNotAllowedToSubmit();

				for (AssignmentFileBean assignment : allAssignmentFilesList) {
					// String status = assignment.getStatus();
					if (subjectsNotAllowedToSubmit != null) {
						if (subjectsNotAllowedToSubmit.contains(assignment.getSubject())) {
							assignment.setStatus("Results Awaited");
							assignment.setSubmissionAllow("N");
						}
					}
				}
				assignmentMakeLive.info("Insert Student Subjects in temp table Sapid:{} Subject List: {}", student.getSapid(), allAssignmentFilesList.size());
				dao.insertIntoQuickAssignments(adminId, student.getSapid(), allAssignmentFilesList,
						filesSet.getExamYear(), filesSet.getExamMonth());
			} catch (Exception e) {
				// e.printStackTrace();
				assignmentMakeLive.error("Error in Assignment make live Sapid - {} Count - {} Error- {}",
						student.getSapid(), runningCount, e);
			}

			float completedCount = ((float) runningCount / (float) listSize) * 100;
//			assignmentMakeLive.info(" Assignment make live completed status - {}%", (int) completedCount);
			if (completeStatus.contains((int) completedCount)) {
				int countLeft = listSize - runningCount;
				assignmentMakeLive.info(
						" Assignment make live {}% completed || Students inserted entries count = {} || Students left count = {}/{}",
						(int) completedCount, runningCount, countLeft, listSize);
			}

			return "abc";
		}, executor
		);
	}
	
}
