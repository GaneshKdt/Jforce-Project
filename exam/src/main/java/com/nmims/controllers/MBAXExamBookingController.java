package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.MBACentersBean;
import com.nmims.beans.MBAExamBookingDetailsResponseBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAExamConflictTransactionBean;
import com.nmims.beans.MBAExamReceiptBean;
import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXPaymentsDao;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;


@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAXExamBookingController extends BaseController {

	
	@Autowired
	MBAXExamBookingDAO examBookingDAO;
	
	@Autowired
	private MBAPaymentHelper examPaymentHelper;

	@Autowired
	private MBAXPaymentsDao examPaymentDao;
	
	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Autowired(required=false)
	ApplicationContext act;
	
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018","2019","2020","2021")); 

	
//	to be deleted, apio shifted to rest controller
//	@RequestMapping(value = "/m/getAllCenters_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
//	public ResponseEntity<String> getAllCenters(HttpServletRequest request) {
//		Map<String, Object> response = new HashMap<String, Object>();
//		try{
//			List<MBACentersBean> allCenters = examBookingDAO.getAllCenters();
//			response.put("status", "success");
//			response.put("response", allCenters);
//		} catch(Exception e) {
//			response.put("status", "fail");
//			
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}

	
//	@RequestMapping(value = "/m/getSlotsForCenterAndTimeboundId_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<String> getSlotsForCenterAndTimeboundId(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
//		Map<String, Object> response = new HashMap<String, Object>();
//		try{
//			Long timeboundId = inputSlotInfo.getTimeboundId();
//			Long centerId = inputSlotInfo.getCenterId();
//			List<MBASlotBean> slots = examBookingDAO.getAllSlotsByTimeboundIdAndCenterId(timeboundId, centerId);
//			response.put("status", "success");
//			response.put("response", slots);
//		} catch(Exception e) {
//			response.put("status", "fail");
//			
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}
	
//	@RequestMapping(value = "/m/getAllSlotsForCenter_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<String> getAllSlotsForCenter(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
//		Map<String, Object> response = new HashMap<String, Object>();
//
//		try{
//			Long centerId = inputSlotInfo.getCenterId();
//			List<MBASlotBean> slots = examBookingDAO.getAllSlotsByCenterId(centerId);
//			response.put("status", "success");
//			response.put("response", slots);
//		} catch(Exception e) {
//			response.put("status", "fail");
//			
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}
//	
//
//	@RequestMapping(value = "/m/getExamBookingDashboardDetails_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getDashboardInfo(@RequestBody StudentBean student,HttpServletRequest request) {
//		MBAResponseBean response = new MBAResponseBean();
//		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
//		try{
//			responseData.setSapid(student.getSapid());
//			getCurrentRegistrationData(responseData);
//			getExamBookingLive(responseData);
//			checkIfCanBook(responseData);
//			response.setStatusSuccess();
//		} catch(Exception e) {
//			response.setStatusFail();
//			
//		}
//		response.setResponse(responseData);
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/getStudentExamBookings_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getStudentExamBookings(@RequestBody StudentBean student,HttpServletRequest request) {
//
//		MBAResponseBean response = new MBAResponseBean();
//		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
//		try{
//			responseData.setSapid(student.getSapid());
//			getCurrentRegistrationData(responseData);
//			getCurrentCompletedBookings(responseData);
//			response.setStatusSuccess();
//		} catch(Exception e) {
//			response.setStatusFail();
//			
//		}
//		response.setResponse(responseData);
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/m/getStudentExamBookingsForTrackId_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getStudentExamBookingsForTrackId(@RequestBody MBAExamBookingRequest bean,HttpServletRequest request) {
//
//		MBAResponseBean response = new MBAResponseBean();
//		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
//		try{
//			responseData.setSapid(bean.getSapid());
//			getCurrentCompletedBookingsForTrackId(responseData, bean.getTrackId());
//			response.setStatusSuccess();
//		} catch(Exception e) {
//			response.setStatusFail();
//			
//		}
//		response.setResponse(responseData);
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/getExamBookingData_MBAX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getExamBookingData(@RequestBody StudentBean student,HttpServletRequest request) {
//
//		MBAResponseBean response = new MBAResponseBean();
//		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
//		responseData.setSapid(student.getSapid());
//		
//		try {
//
//			getCurrentRegistrationData(responseData);
//			getExamBookingLive(responseData);
//			checkIfCanBook(responseData);
//			
//			response.setStatusSuccess();
//		}catch (Exception e) {
//			response.setStatusFail();
//			
//		}
//		response.setResponse(responseData);
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}

	@RequestMapping(value = "/admin/queryTransactionStatusForm_MBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatusForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("MBA_X_Exam_Booking/transactionStatus");
		return modelnView;
	}

	@RequestMapping(value = "/admin/queryTransactionStatus_MBAX",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView querytransactionStatus(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("MBA_X_Exam_Booking/transactionStatus");
		List<MBAExamBookingRequest> transactionResponseList = new ArrayList<>();
		HashMap<String, MBAExamBookingRequest> trackIdTransactionMap = new HashMap<>();
		try{
			String sapid = request.getParameter("sapid");
			ArrayList<MBAExamBookingRequest> unSuccessfulExamBookings = examBookingDAO.getUnSuccessfulExamBookings(sapid);

			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				MBAExamBookingRequest bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();

				String errorMsg = examPaymentHelper.checkTransactionStatus(bean);

				bean.setTrackId(trackId);
				bean.setSapid(sapid);

				transactionResponseList.add(bean);
				trackIdTransactionMap.put(trackId, bean);
			}
			request.getSession().setAttribute("trackIdTransactionMap", trackIdTransactionMap);
			request.getSession().setAttribute("sapIdForApprovedTransaction", sapid);
			request.setAttribute("transactionResponseList", transactionResponseList);

			if(trackIdTransactionMap.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Unsuccessful Transactions found for "+sapid);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in retriving details. Please try again");
		}
		return modelnView;
	}
	@RequestMapping(value = "/admin/approveTransactionsForTrackId_MBAX",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView approveTransactionsForTrackId(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("MBA_X_Exam_Booking/transactionStatus");

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		HashMap<String, MBAExamBookingRequest> trackIdTransactionMap = (HashMap<String, MBAExamBookingRequest>)request.getSession().getAttribute("trackIdTransactionMap");
		String sapIdForApprovedTransaction = (String)request.getSession().getAttribute("sapIdForApprovedTransaction");
		try{
			String trackId = request.getParameter("trackId");
			MBAExamBookingRequest bean = trackIdTransactionMap.get(trackId);

			bean.setSapid(sapIdForApprovedTransaction);
			bean.setLastModifiedBy(userId);
			bean.setTranStatus(ONLINE_PAYMENT_MANUALLY_APPROVED);
			bean.setBookingStatus("Y");
			
			int numberOfRowsUpdated = examPaymentDao.approveOnlineTransactions(bean);
			
			StudentExamBean student = studentDetailsDAO.getSingleStudentsData(sapIdForApprovedTransaction);
			if(numberOfRowsUpdated > 0) {
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Transaction approved successfully . Please ask student to choose center and book seat");
			}
			else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in approving transaction");
			}

			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendTransactionApproveEmail_MBA_X(student, bean);
		
			request.getSession().setAttribute("sapIdForApprovedTransaction", null);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in approving transaction");
		}
		return modelnView;
	}
	

	

	@RequestMapping(value = "/admin/mbaXSearchExamBookingConflictForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView mbaXSearchExamBookingConflictForm( HttpServletRequest request,HttpServletResponse response, Model m){

		ModelAndView modelnView = new ModelAndView ("MBA_X_Exam_Booking/conflict");
		MBAExamConflictTransactionBean transaction = new MBAExamConflictTransactionBean();
		m.addAttribute("transaction", transaction);

		m.addAttribute("yearList", yearList);

		return modelnView ;
	}

	@RequestMapping(value = "/admin/mbaXSearchConflictTransaction", method = {RequestMethod.POST})
	public ModelAndView mbaXSearchConflictTransaction( HttpServletRequest request,HttpServletResponse response, @ModelAttribute MBAExamConflictTransactionBean transaction){

		ModelAndView modelnView = new ModelAndView ("MBA_X_Exam_Booking/conflict");
		String year = request.getParameter("year");
		String month = request.getParameter("month");

		List<MBAExamConflictTransactionBean> transactionList = examBookingDAO.getAllConflictTransactions(year, month);
		int rowCount = 0;
		
		if (!(transactionList.size() == 0 || transactionList == null) ){
			rowCount = transactionList.size();
		}
		
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("transaction",transaction);
		modelnView.addObject("transactionList",transactionList);
		modelnView.addObject("yearList", yearList);

		return modelnView ;
	}

	
//	@RequestMapping(value = "/m/printBookingStatus_MBAX", method = { RequestMethod.GET, RequestMethod.POST })
//	public ResponseEntity<MBAExamReceiptBean> printBookingStatus(@RequestBody StudentBean input,HttpServletRequest request) {
//
//		MBAExamReceiptBean receipt = new MBAExamReceiptBean();
//		try {
//
//			String fileName = "";
//
//			String sapid = input.getSapid();
//
//			if(sapid == null) {
//				receipt.setStatus("fail");
//				receipt.setErrorMessage("No Sapid!");
//				return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
//			}
//			// Added this avoid null in reciept download for students ending validity in
//			// Oct.
//
//			MBAStudentDetailsBean studentDetails = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(sapid, CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR);
//			List<MBAExamBookingRequest> bookings = examBookingDAO.getAllStudentBookings(studentDetails);
//			List<MBAExamBookingRequest> approvedBookings = examBookingDAO.getAllStudentApprovedBookings(studentDetails);
//			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
//
//			StudentBean student = studentDetailsDAO.getSingleStudentsData(sapid);
//			fileName = pdfCreator.createPDF_MBAX(bookings, approvedBookings, FEE_RECEIPT_PATH, student);
//			receipt.setStatus("success");
//
//			String url = fileName;
//			url = url.split(":/")[1];
//			url = SERVER_PATH + url;
//			receipt.setDownloadURL(url);
//
//			return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
//		} catch (Exception e) {
//			
//			receipt.setStatus("fail");
//			receipt.setErrorMessage("Error generating receipt!");
//			return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
//		}
//	}
	

	private void getCurrentRegistrationData(MBAExamBookingDetailsResponseBean responseBean) {
		try {		
			// Get latest timebound details for current acad cycle 
			MBAStudentDetailsBean currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(responseBean.getSapid(), CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR);
			responseBean.setCurrentRegistrationDetails(currentRegData);
		}catch (Exception e) {
			// When no registration info found for current acad month year, process for latest student registration instead.
			// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
			MBAStudentDetailsBean currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(responseBean.getSapid());
			responseBean.setCurrentRegistrationDetails(currentRegData);
		}
	}

	private List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsList(MBAExamBookingDetailsResponseBean responseBean) {
		return examBookingDAO.getFailedSubjectsForStudent(responseBean.getCurrentRegistrationDetails());
	}
	
	private List<MBAStudentSubjectMarksDetailsBean> getResitSubjectList(MBAExamBookingDetailsResponseBean responseBean) {
		 return examBookingDAO.getResitFailedSubjectsForStudent(responseBean.getCurrentRegistrationDetails());
	}

	private void processSubjectsForBooking(MBAExamBookingDetailsResponseBean responseBean, List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList) {

		// get the booking details (if any) for this subject
		for (MBAStudentSubjectMarksDetailsBean subject : failedSubjectsList) {
			MBAExamBookingRequest previousBookingDetails = examBookingDAO.getLatestStudentBookingForTimeboundId(responseBean.getSapid(), subject.getTimeboundId());
			subject.setPreviousBookingDetails(previousBookingDetails);
		}
		
		responseBean.setFailedSubjectsList(failedSubjectsList);
	}
	
	private void getAppliedSubjects(MBAExamBookingDetailsResponseBean responseBean) {
		// list of timebound ids for bookings with status Y for this month/year
		List<String> subjectsAppliedFor = examBookingDAO.getAppliedSubjects(responseBean.getCurrentRegistrationDetails());
		responseBean.setSubjectsAppliedFor(subjectsAppliedFor);
	}

	private void getCurrentCompletedBookings(MBAExamBookingDetailsResponseBean responseBean) {
		// bookings with status Y for this month/year
		List<MBAExamBookingRequest> bookings = examBookingDAO.getAllStudentBookings(responseBean.getCurrentRegistrationDetails());
		responseBean.setBookings(bookings);
	}
	
	private void getCurrentCompletedBookingsForTrackId(MBAExamBookingDetailsResponseBean responseBean, String trackId) {
		// bookings for this track Id
		List<MBAExamBookingRequest> bookings = examBookingDAO.getAllStudentBookingsForTrackId(responseBean.getSapid(), trackId);
		responseBean.setBookings(bookings);
	}
	
	private void getExamBookingLive(MBAExamBookingDetailsResponseBean responseBean) {
		// checks if exam reg is live for this registration
		boolean isExamBookingLive = examBookingDAO.checkIfExamBookingLive(responseBean.getCurrentRegistrationDetails());
		responseBean.setExamBookingLive(isExamBookingLive);
	}
	
	private void checkIfCanBook(MBAExamBookingDetailsResponseBean responseBean) {

		boolean canBook = true;
		String canNotBookReason = "";

		int minFailedSubjectAllowed = 2;
		int riaCount = 0;


		List<MBAStudentSubjectMarksDetailsBean> resitSubjects = getResitSubjectList(responseBean);
		List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(responseBean);
		
		List<MBAStudentSubjectMarksDetailsBean> allBookableSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
		
		if(resitSubjects.size() > 0)
		responseBean.setBookingForResit(true);
			
		
		allBookableSubjects.addAll(failedSubjects);
		allBookableSubjects.addAll(resitSubjects);
		processSubjectsForBooking(responseBean, allBookableSubjects);
		
		getAppliedSubjects(responseBean);
		if(responseBean.isExamBookingLive()) {
			
			checkSubjectTimeTableIsActive(responseBean);
			
			riaCount = checkIfStudentHasRIA(responseBean);
			if (riaCount > 0) {
				minFailedSubjectAllowed = minFailedSubjectAllowed + riaCount;
			}
			
			int numFailedSubjectsList = responseBean.getFailedSubjectsList().size();
			
			
			if(numFailedSubjectsList > minFailedSubjectAllowed && !responseBean.isBookingForResit()) {
				canBook = false;
				canNotBookReason = "You are not eligible for exam bookings as Re-exam (100 marks) is applicable for students who have failed in not more than two subjects of that respective Term.  ";
			}   else if(allBookableSubjects.size() == 0) {
				canBook = false;
				canNotBookReason = "You do not have any subjects eligible for the current Term Re-Exam";
			}
		}else {
			canBook = false;
			canNotBookReason = "Exam Booking is not active!";
		}
		responseBean.setCanBook(canBook);
		responseBean.setCanNotBookReason(canNotBookReason);
	}

	private int checkIfStudentHasRIA(MBAExamBookingDetailsResponseBean responseBean) {
		int riaCount = 0;
		
		for (MBAStudentSubjectMarksDetailsBean subject : responseBean.getFailedSubjectsList()) {
			if("RIA".equals(subject.getStatus()) || "NV".equals(subject.getStatus())) {
				riaCount ++;
			}
		}
		return riaCount;
	}
	
	private void checkSubjectTimeTableIsActive(MBAExamBookingDetailsResponseBean responseBean) {
		try {
		List<String> failSubjectTimeboundId = 
				responseBean.getFailedSubjectsList().stream()
			              .map(MBAStudentSubjectMarksDetailsBean::getTimeboundId)
			              .collect(Collectors.toList());
		
		List<String> timeboundIds = failSubjectTimeboundId.stream()
		        .filter(p1 -> !responseBean.getSubjectsAppliedFor().stream().anyMatch(p2 -> p2.equals(p1)))
		        .collect(Collectors.toList());
		
		
		if(timeboundIds != null && timeboundIds.size() > 0) {
			List<String> activeTimeTableTimeboundId = examBookingDAO.getActiveTimeTableTimeboundId(timeboundIds);
			
			List<MBAStudentSubjectMarksDetailsBean> removedTimeboundId = 
					responseBean.getFailedSubjectsList().stream()
					.filter(p1 -> !responseBean.getSubjectsAppliedFor().stream().anyMatch(p2 -> p2.equals(p1.getTimeboundId())))
					.filter(p1 -> !activeTimeTableTimeboundId.stream().anyMatch(p2 -> p2.equals(p1.getTimeboundId())))
					.collect(Collectors.toList());
			
			responseBean.getFailedSubjectsList().removeAll(removedTimeboundId);
		}
		
		
		}catch(Exception e) {
			
		}
	}
	
}
