package com.nmims.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.DDDetails;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.ResitExamBookingDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.XMLParser;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.ExamBookingPDFCreator;

@Controller
public class AssignmentPaymentController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	ResitExamBookingDAO resitExamBookingDAO; 

	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 

	private int examFeesPerSubject = 500;
	private int totalFeesForRebooking = 200;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 

	private final String FEE_IN_ADMISSION = "Exam Fees part of Registration Fees/Exam Fees Exempted";
	private final String DD_APPROVAL_PENDING = "DD Approval Pending";
	private final String DD_APPROVED = "DD Approved";
	private final String DD_REJECTED = "DD Rejected";
	private final String NOT_BOOKED = "Not Booked";
	private final String BOOKED = "Booked";
	private final String SEAT_RELEASED = "Seat Released";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	private final String SEAT_RELEASED_SUBJECT_CLEARED = "Seat Released - Subject Cleared";
	private final String CENTER_CHANGED_BOOKED = "Center Changed and Booked";
	private final String NOT_ELIGIBLE_TO_BOOK = "Not Eligible to Book";
	private final String BOOKING_SUCCESS_MSG = "Your Assignment Fees is received. Please proceed to Assignment submission."
	+ "Please click <a href=\"student/viewAssignmentsForm\"> here </a> to submit Assignments.";

	private static final String GATEWAY_STATUS_FAILED = "Payment Failed";
	private static final String GATEWAY_STATUS_SUCCESSFUL = "Payment Successfull";
	private static final String ASSIGNMENT_PAYMENT_SUCCESSFUL = "Online Payment Successful";
	private static final String ASSIGNMENT_TRANSACTION_FAILED  = "Transaction Failed";
	private static final String ASSIGNMENT_MANUALLY_APPROVED  = "Online Payment Manually Approved";
	private static final String WEBHOOK_API = "Webhook API";
	
	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	@Value( "${V3URL}" )
	private String V3URL;
	@Value( "${ASSGN_PAYMENT_RETURN_URL}" )
	private String ASSGN_PAYMENT_RETURN_URL;
	
	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE;

	private static final Logger logger = LoggerFactory.getLogger(AssignmentPaymentController.class);
	
	private static final Logger razorpayLogger = LoggerFactory.getLogger("webhook_payments");

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016")); 

	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	private Map<String, String> examCenterIdNameMap = null;

	private ArrayList<StudentExamBean> exemptStudentList = null;
	@Autowired
	ExamBookingPDFCreator examFeeReceiptCreator;
		
	@Autowired
	ExamBookingHelper examBookingHelper;
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();		
		
		subjectList = null;
		getSubjectList();
		
		exemptStudentList = null;
		getExemptStudentList();
		
		programList = null;
		getProgramList();
		
		return null;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}

	public ArrayList<StudentExamBean> getExemptStudentList(){
		if(this.exemptStudentList == null || this.exemptStudentList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.exemptStudentList = eDao.getExemptStudentList();
		}
		return exemptStudentList;
	}

	public Map<String, String> getExamCenterIdNameMap(){
		//if(this.examCenterIdNameMap == null || examCenterIdNameMap.size() == 0){
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		//}
		return examCenterIdNameMap;
	}

	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	

	@RequestMapping(value = "/student/selectAssignmentPaymentSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView selectAssignmentPaymentSubjectsForm(HttpServletRequest request, HttpServletResponse response)  {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjectsDemo");
		String sapid = (String)request.getSession().getAttribute("userId");
		ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		
		String subjectForPayment = (String)request.getParameter("subject");
		int subjectsToPay = 0;
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		try{

			ExamBookingExamBean examBooking = new ExamBookingExamBean();
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			
			StudentExamBean student = resitExamBookingDAO.getSingleStudentWithValidity(sapid);
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("studentExam", student);
			
			if("Offline".equals(student.getExamMode())){
				modelnView = new ModelAndView("studentPortalHome");
				setError(request, "You are not authorized to register for Resit Examination");
				modelnView.addObject("examBooking", examBooking);
			}
			
			String mostRecentTimetablePeriod = sDao.getMostRecentResitAssignmentPeriod();
			ExamOrderExamBean exam = sDao.getUpcomingResitAssignmentExam();
			examBooking.setYear(exam.getYear());
			examBooking.setMonth(exam.getMonth());
			modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
			request.getSession().setAttribute("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

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
			modelnView.addObject("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("applicableSubjectsListCount", applicableSubjectsList.size());

			request.getSession().setAttribute("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("examBooking", examBooking);
			modelnView.addObject("subjectsToPay", subjectsToPay);

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
		}
		return modelnView;
	}

	


	@RequestMapping(value = "/student/assignmentPaymentGotoGateway", method = {RequestMethod.POST})
	public ModelAndView assignmentPaymentGotoGateway(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingExamBean examBooking, ModelMap model/*,RedirectAttributes ra*/) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
		int noOfSubjects = 0;
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try{
			String subjectForTestPayment = (String)request.getSession().getAttribute("subjectForTestPayment");
			ArrayList<String> subjects = examBooking.getApplicableSubjects();
			String sapid = (String)request.getSession().getAttribute("userId");

			String trackId = sapid + System.currentTimeMillis() ;
			request.getSession().setAttribute("trackId", trackId);

			String message = "Assignment fees for "+sapid;
			Map<String, PassFailExamBean> subjectPassFailBeanMap = getSubjectPassFailBeanMap(sapid);
			StudentExamBean student = sDao.getSingleStudentsData(sapid);
			boolean isCertificate = (boolean)request.getSession().getAttribute("isCertificate");
			List<ExamBookingTransactionBean> bookingsList = new ArrayList<>();

			//ArrayList<String> selectedCenters = examBooking.getSelectedCenters();

			String examYear = examBooking.getYear();
			String examMonth = examBooking.getMonth();


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
					bean.setProgram(student.getProgram());
					bean.setSem(student.getSem());
				}
				bean.setTrackId(trackId);
				bean.setAmount(examFeesPerSubject * noOfSubjects+"");
				bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
				bean.setBooked("N");
				bean.setPaymentMode("Online");
				if(!StringUtils.isBlank(subjectForTestPayment)) {
				if(subjectForTestPayment.equalsIgnoreCase(bean.getSubject())) {

					String testId = (String)request.getSession().getAttribute("testId");
					String testAttempt = (String)request.getSession().getAttribute("testAttempt");
					try {
						if(!StringUtils.isBlank(testId) && !StringUtils.isBlank(testAttempt)) {
						bean.setTestId(new Long(testId));
						bean.setTestAttempt(new Long(testAttempt));
						}else {
							bean.setTestId(new Long("0"));
							bean.setTestAttempt(new Long("0"));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}
				}
				}

				bookingsList.add(bean);
			}


			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			
			
			if(!StringUtils.isBlank(subjectForTestPayment)) {
				dao.upsertOnlineInitiationTransactionWithTestIdAttempt(sapid, bookingsList);
				
			}else{
				dao.upsertOnlineInitiationTransaction(sapid, bookingsList);
				dao.upsertPaymentStatusInQuickTable(sapid, bookingsList);
			}
			
			int totalFees = examFeesPerSubject * noOfSubjects;
			

			request.getSession().setAttribute("totalFees", isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE) + "":totalFees);
			request.getSession().setAttribute("subjects", subjects);
			
			ModelAndView mv = new ModelAndView("payment");
			mv.addObject("track_id", trackId);
			mv.addObject("sapid", sapid);
			mv.addObject("type", "Assignment");
			mv.addObject("amount", isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE):totalFees);
			mv.addObject("description", message);
			mv.addObject("source", "web");
			mv.addObject("portal_return_url", ASSGN_PAYMENT_RETURN_URL);
			mv.addObject("created_by", sapid);
			mv.addObject("updated_by", sapid);
			mv.addObject("mobile", student.getMobile());
			mv.addObject("email_id", student.getEmailId());
			mv.addObject("first_name", student.getFirstName());
			return mv;
			
			
			//fillPaymentParametersInMap(model, student, totalFees, trackId, message,isCertificate);

			//request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
			/*return proceedToPayOptions(model,trackId,ra);*/
			//return new ModelAndView(new RedirectView("/exam/pay"), model);
		}catch(Exception e){
			

			modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in initiating Online transaction. Error: "+e.getMessage());
			modelnView.addObject("examBooking", examBooking);
			return modelnView;
		}

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

	


	private void fillPaymentParametersInMap(ModelMap model,	StudentExamBean student, int totalFees, String trackId, String message,boolean isCertificate) {
 
		String address = student.getAddress();
		
		if(address == null || address.trim().length() == 0){
			address = "Not Available";
		}else if(address.length() > 200){
			address = address.substring(0, 200);
		}

		String city = student.getCity();
		if(city == null || city.trim().length() == 0){
			city = "Not Available";
		}else if(city.length() > 30){
			city = city.substring(0, 30);
		}

		String pin = student.getPin();
		if(pin == null || pin.trim().length() == 0){
			pin = "000000";
		}else if(pin.length() > 8){
			pin = pin.substring(0, 8);
		}

		String mobile = student.getMobile();
		if(mobile == null || mobile.trim().length() == 0){
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if(emailId == null || emailId.trim().length() == 0){
			emailId = "notavailable@email.com";
		}else if(emailId.length() > 100){
			emailId = emailId.substring(0, 100);
		}

		model.addAttribute("udf1", message);
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", trackId);
		model.addAttribute("amount", isCertificate ? generateAmountBasedOnCriteria(String.valueOf(totalFees),SERVICE_TAX_RULE):totalFees);
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", message);
		model.addAttribute("return_url", ASSGN_PAYMENT_RETURN_URL);
		model.addAttribute("name", student.getFirstName()+ " "+student.getLastName());
		model.addAttribute("address",URLEncoder.encode(address));
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", student.getSapid());
	}

	@RequestMapping(value = "assignmentFeesResponse", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView assignmentFeesResponse(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		saveAllTransactionDetails(request);

		
	    //String typeOfPayment = (String)request.getParameter("PaymentMethod"); String
	    String trackId = (String)request.getSession().getAttribute("trackId"); 
	    String totalFees = String.valueOf(request.getSession().getAttribute("totalFees"));
	  
	    //boolean isHashMatching = isHashMatching(request); 
	    boolean isAmountMatching = isAmountMatching(request, totalFees); 
	    boolean isTrackIdMatching = isTrackIdMatching(request, trackId); 
	    boolean isSuccessful = isTransactionSuccessful(request);
		 
					
		
		
		/*boolean isHashMatching = true;
		boolean isTrackIdMatching = true;
		boolean isAmountMatching = true;
		boolean isSuccessful = isTransactionSuccessful(request);
		
		if(!"Wallet".equals(typeOfPayment)){
			 isHashMatching = isHashMatching(request);
			 isAmountMatching = isAmountMatching(request, totalFees);
			 isTrackIdMatching = isTrackIdMatching(request, trackId);
		}*/
		String errorMessage = null;

		if(!isSuccessful){
			errorMessage = "Error in processing payment. Error: " + request.getParameter("error")+ " Code: "+request.getParameter("response_code");
		}
			
		/*if(!isHashMatching){
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "+trackId;
		}*/
			
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
			return selectAssignmentPaymentSubjectsForm(request, response);
		}else{
			return saveSuccessfulTransaction(request, response, model);
		}
	}

	/**
	 * API to update assignment payments received from Payment Gateway
	 * 
	 * @param transaction bean from payment gateway
	 * @return response entity
	 */
	@RequestMapping(value = "/m/assignmentGatewayResponse", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> assignmentGatewayResponse(@RequestBody TransactionsBean bean) {

		razorpayLogger.info("received webhook payload from payment gateways : " + bean);

		String responseMessage = null;

		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");

		try {

			// get payment status and subject to include in email

			Map<String, String> statusAndSubject = dao.getAssignmentPaymentStatusByTrackId(bean.getTrack_id());

			if (statusAndSubject.isEmpty()) {
				responseMessage = "No record found for track id : " + bean.getTrack_id();
				razorpayLogger.info(responseMessage);
				return sendOkayResponse(responseMessage);
			}

			if (transactionHasError(statusAndSubject, bean)) {
				return sendOkayResponse("payment was already marked as " + bean.getTransaction_status()
						+ " for track id " + bean.getTrack_id());
			}

			ExamBookingTransactionBean examBean = new ExamBookingTransactionBean();

			if (GATEWAY_STATUS_SUCCESSFUL.equalsIgnoreCase(bean.getTransaction_status())) {

				populateAssignmentSuccessBean(bean, examBean);

				razorpayLogger.info("trying to update dao with bean for track id : " + bean.getTrack_id());

				dao.updateSeatsForOnlineUsingSingleConnection(examBean);

				dao.setPaymentStatusInQuickTable(examBean);

				razorpayLogger.info("Table updated now sending mail for track id : " + bean.getTrack_id());


				StudentExamBean student = sDao.getSingleStudentsData(examBean.getSapid());

				String mostRecentTimetablePeriod = sDao.getMostRecentResitAssignmentPeriod();

				examBookingHelper.createAndUploadAssignmentFeeReceipt(examBean.getSapid(), examBean.getTrackId());

				try {
					MailSender mailSender = (MailSender) act.getBean("mailer");
					
					mailSender.sendAssignmentBookingSummaryEmail(student,
							new ArrayList<>(Arrays.asList(statusAndSubject.get("subject"))), mostRecentTimetablePeriod);
					
				} catch (Exception e) {
					razorpayLogger.info("Error occurred while sending mail : " + e.getMessage());
				}

				responseMessage = "Table updated for track id " + bean.getTrack_id();

				return sendOkayResponse(responseMessage);

			} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(bean.getTransaction_status())) {

				razorpayLogger.info("payment received for failed status so marking transaction as failed for trackid : "
						+ bean.getTrack_id());

				examBean.setError(bean.getError());
				examBean.setPaymentOption(bean.getPayment_option());
				examBean.setTrackId(bean.getTrack_id());
				dao.markTransactionsFailed(examBean);
				responseMessage = "Payment marked as failed for track id " + bean.getTrack_id();
				razorpayLogger.info(responseMessage);
				return sendOkayResponse(responseMessage);
			} else {
				responseMessage = "INVALID transaction status : " + bean.getTransaction_status() + " for track id : "
						+ bean.getTrack_id();
				razorpayLogger.info(responseMessage);
				return sendOkayResponse(responseMessage);
			}

		} catch (Exception e) {

			responseMessage = "Error while processing webhook transaction for track id : " + bean.getTrack_id() + e.getMessage();
			razorpayLogger.info(responseMessage);
			try {
				MailSender mailSender = (MailSender) act.getBean("mailer");
				mailSender.mailStackTrace("Error in Saving Successful Transaction", e);
			} catch (Exception e2) {
				razorpayLogger.info("Error sending stack trace mail for payload : " + bean + e2.getMessage());
			}
			return sendOkayResponse(responseMessage);

		}
	}

	private void populateAssignmentSuccessBean(TransactionsBean bean, ExamBookingTransactionBean examBean) {

		examBean.setSapid(bean.getSapid());
		examBean.setTrackId(bean.getTrack_id());
		examBean.setResponseMessage(bean.getResponse_payment_method());
		examBean.setTransactionID(bean.getTransaction_id());
		examBean.setRequestID(bean.getRequest_id());
		examBean.setMerchantRefNo(bean.getMerchant_ref_no());
		examBean.setSecureHash(bean.getSecure_hash());
		examBean.setRespAmount(bean.getResponse_amount());
		examBean.setRespTranDateTime(bean.getResponse_transaction_date_time());
		examBean.setResponseCode(bean.getResponse_code());
		examBean.setRespPaymentMethod(bean.getResponse_method());
		// examBean.setIsFlagged(request.getParameter("IsFlagged"));
		examBean.setPaymentID(bean.getPayment_id());
		examBean.setError(bean.getError());
		examBean.setDescription(bean.getDescription());
		examBean.setPaymentOption(bean.getPayment_option());

		razorpayLogger.info("populated bean to update assignment table for successful transaction : " + examBean);
	}

	private boolean transactionHasError(Map<String, String> statusAndSubject, TransactionsBean bean) {
		String status = statusAndSubject.get("status");

		if (ASSIGNMENT_MANUALLY_APPROVED.equalsIgnoreCase(status)
				|| ASSIGNMENT_PAYMENT_SUCCESSFUL.equalsIgnoreCase(status)) {
			razorpayLogger.info("Payment was already marked as successful for track id : " + bean.getTrack_id());
			return true;

		} else if (GATEWAY_STATUS_FAILED.equalsIgnoreCase(bean.getTransaction_status())
				&& ASSIGNMENT_TRANSACTION_FAILED.equalsIgnoreCase(status)) {
			razorpayLogger.info("Payment was already marked as failed for trackid : " + bean.getTrack_id());
			return true;
		}
		return false;
	}

	private ResponseEntity<String> sendOkayResponse(String responseMessage) {
		return new ResponseEntity<String>(responseMessage, HttpStatus.OK);
	}

	private ModelAndView processSuccessTransaction(HttpServletRequest request, HttpServletResponse response) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", BOOKING_SUCCESS_MSG);
			request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		return selectAssignmentPaymentSubjectsForm(request,response);
		}
	
	public ModelAndView saveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
		
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

					return processSuccessTransaction(request, response);
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

			request.getSession().setAttribute("onlineSeatBookingComplete", "false");
			request.getSession().setAttribute("examCenterIdNameMap", getExamCenterIdNameMap());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Seats NOT booked. Error in recording your transaction details. Please contact Head Office to get it sorted out.");
			return new ModelAndView("assignment/selectAssignmentPaymentSubjects");
		}
		request.getSession().setAttribute("onlineSeatBookingComplete", "true");
		return selectAssignmentPaymentSubjectsForm(request, response);
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
			
		}
	}
	
	
	
	private boolean isTransactionSuccessful(HttpServletRequest request) {
		
		String transaction_status = request.getParameter("transaction_status");
		if("Payment Successfull".equalsIgnoreCase(transaction_status)) {
			return true;
		}else {
			return false;
		}
		
		/*String error = request.getParameter("Error");
		//Error parameter should be absent to call it successful 
		if(error == null){
			//Response code should be 0 to call it successful
			String responseCode = request.getParameter("ResponseCode");
			if("0".equals(responseCode)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}*/

	}


	//=======This is GetTextBetweenTags function which return the value between two XML tags or two string =====
	public String GetTextBetweenTags(String InputText,String Tag1,String Tag2)
	{
		String Result;

		int index1 = InputText.indexOf(Tag1);
		int index2 = InputText.indexOf(Tag2);
		index1=index1+Tag1.length();
		Result=InputText.substring(index1, index2);
		return Result;

	}   

	public String GetSHA256(String str)
	{	
		StringBuffer strhash=new StringBuffer();
		try
		{
			//-------- Tampering code starts here -----
			String message = str;
			MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
			messagedigest.update(message.getBytes());
			byte digest[] = messagedigest.digest();
			strhash = new StringBuffer(digest.length*2);
			int length = digest.length;

			for (int n=0; n < length; n++)
			{
				int number = digest[n];
				if(number < 0)
				{			   
					number= number + 256;
				}
				//number = (number < 0) ? (number + 256) : number; // shift to positive range
				String str1="";
				if(Integer.toString(number,16).length()==1)
				{
					str1="0"+String.valueOf(Integer.toString(number,16));
				}
				else
				{
					str1=String.valueOf(Integer.toString(number,16));
				}
				strhash.append(str1);
			}		   
		}catch(Exception e)
		{
		} 	  
		return strhash.toString(); 
	}

	private boolean isHashMatching(HttpServletRequest request) {
		try{
			String md5HashData = SECURE_SECRET;
			HashMap testMap = new HashMap();
			Enumeration<String> en = request.getParameterNames();

			while(en.hasMoreElements()) {
				String fieldName = (String) en.nextElement();
				String fieldValue = request.getParameter(fieldName);
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					testMap.put(fieldName, fieldValue);
				}
			}

			//Sort the HashMap
			Map requestFields = new TreeMap<>(testMap);

			String V3URL = (String) requestFields.remove("V3URL");
			requestFields.remove("submit");
			requestFields.remove("SecureHash");

			for (Iterator i = requestFields.keySet().iterator(); i.hasNext(); ) {

				String key = (String)i.next();
				String value = (String)requestFields.get(key);
				md5HashData += "|"+value;

			}

			String hashedvalue = md5(md5HashData);
			String receivedHashValue = request.getParameter("SecureHash");

			if(receivedHashValue != null && receivedHashValue.equals(hashedvalue)){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			
		}
		return false;
	}

	private boolean isAmountMatching(HttpServletRequest request, String totalFees) {
		try {
			double feesSent = Double.parseDouble(totalFees);
			double amountReceived = Double.parseDouble(request.getParameter("response_amount"));

			if(feesSent == amountReceived){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			
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

	private String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();

		m.update(data,0,data.length);

		BigInteger i = new BigInteger(1,m.digest());

		String hash = String.format("%1$032X", i);

		return hash;
	}


//code for test 2nd or higher attempt payemnt start
	@RequestMapping(value = "/selectTestPaymentForSecondAttemptSubjectsForm", method = RequestMethod.GET)
	public ModelAndView selectTestPaymentForSecondAttemptSubjectsForm(HttpServletRequest request, HttpServletResponse response)  {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("assignment/selectAssignmentPaymentSubjects");
		String sapid = (String)request.getSession().getAttribute("userId");
		String testId = (String)request.getParameter("testId");
		String testAttempt = (String)request.getParameter("testAttempt");
		
		
		ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> bookedSubjects = new ArrayList<>();
		
		String subjectForPayment = (String)request.getParameter("subject");
		String subjectForTestPayment = subjectForPayment;
		int subjectsToPay = 0;
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		try{

			ExamBookingExamBean examBooking = new ExamBookingExamBean();
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			
			StudentExamBean student = resitExamBookingDAO.getSingleStudentWithValidity(sapid);
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("studentExam", student);
			
			if("Offline".equals(student.getExamMode())){
				modelnView = new ModelAndView("studentPortalHome");
				setError(request, "You are not authorized to register for Resit Examination");
				modelnView.addObject("examBooking", examBooking);
			}
			
			String mostRecentTimetablePeriod = sDao.getMostRecentResitAssignmentPeriod();
			ExamOrderExamBean exam = sDao.getUpcomingResitAssignmentExam();
			examBooking.setYear(exam.getYear());
			examBooking.setMonth(exam.getMonth());
			modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
			request.getSession().setAttribute("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

			/*ArrayList<String> subjectsNeedingPayment = (ArrayList<String>)dao.getSubjectsNeedingAssignmentPayments(sapid);*/
			ArrayList<String> subjectsNeedingPayment = new ArrayList<String>();
			subjectsNeedingPayment.add(subjectForPayment);
			bookedSubjects = (ArrayList<String>)dao.getSubjectsMadeTestPaymentsForTestIdAndAttempt(sapid,testId,testAttempt);
			
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
			modelnView.addObject("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("applicableSubjectsListCount", applicableSubjectsList.size());

			request.getSession().setAttribute("applicableSubjectsList", applicableSubjectsList);
			modelnView.addObject("examBooking", examBooking);
			modelnView.addObject("subjectsToPay", subjectsToPay);
			request.getSession().setAttribute("paymentForSecondOrHigherTestAttempt", "true");
			request.getSession().setAttribute("testId", testId);
			request.getSession().setAttribute("testAttempt", testAttempt);
			request.getSession().setAttribute("subjectForTestPayment", subjectForTestPayment);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects.");
			request.getSession().setAttribute("paymentForSecondOrHigherTestAttempt", "false");
		}
		return modelnView;
	}
	//code for test 2nd or higher attempt payemnt end


}