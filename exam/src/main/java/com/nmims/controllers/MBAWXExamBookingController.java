package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;


@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAWXExamBookingController extends BaseController {

	
	@Autowired
	MBAWXExamBookingDAO examBookingDAO;
	
	@Autowired
	private MBAPaymentHelper examPaymentHelper;

	@Autowired
	private MBAWXPaymentsDao examPaymentDao;
	
	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Autowired(required=false)
	ApplicationContext act;
	
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018","2019","2020","2021")); 

	
//	to be deleted
//	@RequestMapping(value = "/m/getAllCenters", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
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

	
//	to be deleted
//	@RequestMapping(value = "/m/getAllSlotsForCenter", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
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
	

	
//	to be deleted
//	@RequestMapping(value = "/m/getExamBookingDashboardDetails_MBAWX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getDashboardInfo(@RequestBody StudentBean student,HttpServletRequest request) {
//
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
	
	
//	to be deleted
//	@RequestMapping(value = "/m/getStudentExamBookings", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
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

//	TODO : Uncomment after Exam Booking is done for Jan-2020
	
//	@RequestMapping(value = "/m/getStudentExamBookingsForTrackId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
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
//	@RequestMapping(value = "/m/getSlotsForCenterAndTimeboundId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getSlotsForCenterAndTimeboundId(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
//		MBAResponseBean response = new MBAResponseBean();
//		try{
//			Long timeboundId = inputSlotInfo.getTimeboundId();
//			Long centerId = inputSlotInfo.getCenterId();
//			List<MBASlotBean> slots = examBookingDAO.getAllSlotsByTimeboundIdAndCenterId(timeboundId, centerId);
//			response.setResponse(slots);
//			response.setStatusSuccess();
//		} catch(Exception e) {
//			response.setStatusFail();
//			response.setMessage(e.getMessage());
//		}
//		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
//	}
	
//
////	TODO : Remove after Exam Booking is done for Jan-2020
//		
//		@RequestMapping(value = "/m/getStudentExamBookingsForTrackId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//		public ResponseEntity<String> getStudentExamBookingsForTrackId(@RequestBody MBAExamBookingRequest bean,HttpServletRequest request) {
//	
//			Map<String, Object> response = new HashMap<String, Object>();
//			
//			try{
//				List<MBAExamBookingRequest> allCenters = examBookingDAO.getAllStudentBookingsForTrackId(bean.getSapid(), bean.getTrackId());
//				response.put("status", "success");
//				response.put("response", allCenters);
//			} catch(Exception e) {
//				response.put("status", "fail");
//				
//			}
//			return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//		}
	
//		@RequestMapping(value = "/m/getSlotsForCenterAndTimeboundId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//		public ResponseEntity<String> getSlotsForCenterAndTimeboundId(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
//	
//			Map<String, Object> response = new HashMap<String, Object>();
//			
//			try{
//				Long timeboundId = inputSlotInfo.getTimeboundId();
//				Long centerId = inputSlotInfo.getCenterId();
//				List<MBASlotBean> slots = examBookingDAO.getAllSlotsByTimeboundIdAndCenterId(timeboundId, centerId);
//				List<Map<String, String>> slotsList = new ArrayList<Map<String, String>>();
//				for (MBASlotBean mbaSlotBean : slots) {
//
//					Map<String, String> slotMap = new HashMap<String, String>();
//					slotMap.put("examYear", mbaSlotBean.getExamYear());
//					slotMap.put("examMonth", mbaSlotBean.getExamMonth());
//					slotMap.put("slotId", Long.toString(mbaSlotBean.getSlotId()));
//					slotMap.put("centerId", Long.toString(mbaSlotBean.getCenterId()));
//					slotMap.put("timeTableId", Long.toString(mbaSlotBean.getTimeTableId()));
//					slotMap.put("capacity", Long.toString(mbaSlotBean.getCapacity()));
//					slotMap.put("bookedSlots", Long.toString(mbaSlotBean.getBookedSlots()));
//					slotMap.put("availableSlots", Long.toString(mbaSlotBean.getAvailableSlots()));
//					
//					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
//					String examStartDateTime = formatter.format(mbaSlotBean.getExamStartDateTime());
//					String examEndDateTime = formatter.format(mbaSlotBean.getExamEndDateTime());
//					slotMap.put("examStartDateTime", examStartDateTime);
//					slotMap.put("examEndDateTime", examEndDateTime);
//					slotsList.add(slotMap);
//				}
//				response.put("status", "success");
//				response.put("response", slotsList);
//			} catch(Exception e) {
//				response.put("status", "fail");
//				
//			}
//			return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//		}

//		x------------------x
		
//	@RequestMapping(value = "/m/getExamBookingData", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<MBAResponseBean> getExamBookingData(@RequestBody StudentBean student,HttpServletRequest request) {
//		
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

	@RequestMapping(value = "/admin/queryTransactionStatusFormMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatusForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("transactionStatus_MBA_WX");
		return modelnView;
	}

	@RequestMapping(value = "/admin/queryTransactionStatus_MBA_WX",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView queryTransactionStatus_MBA_WX(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("transactionStatus_MBA_WX");
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
	@RequestMapping(value = "/admin/approveTransactionsForTrackId_MBA_WX",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView approveTransactionsForTrackId(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("transactionStatus_MBA_WX");

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
			mailSender.sendTransactionApproveEmail_MBA_WX(student, bean);
		
			request.getSession().setAttribute("sapIdForApprovedTransaction", null);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in approving transaction");
		}
		return modelnView;
	}
	

	

	@RequestMapping(value = "/admin/mbawxSearchExamBookingConflictForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView mbawxSearchExamBookingConflictForm( HttpServletRequest request,HttpServletResponse response, Model m){

		ModelAndView modelnView = new ModelAndView ("mbaWxExamBookingConflict");
		MBAExamConflictTransactionBean transaction = new MBAExamConflictTransactionBean();
		m.addAttribute("transaction", transaction);

		m.addAttribute("yearList", yearList);

		return modelnView ;
	}

	@RequestMapping(value = "/admin/mbawxSearchConflictTransaction", method = {RequestMethod.POST})
	public ModelAndView mbawxSearchConflictTransaction( HttpServletRequest request,HttpServletResponse response, @ModelAttribute MBAExamConflictTransactionBean transaction){

		ModelAndView modelnView = new ModelAndView ("mbaWxExamBookingConflict");
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

	
//	to be deleted
//	@RequestMapping(value = "/m/printBookingStatus_MBAWX", method = { RequestMethod.GET, RequestMethod.POST })
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
//			MBAStudentDetailsBean studentDetails;
//			try {		
//				// Get latest timebound details for current acad cycle 
//				studentDetails = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR);
//				
//			}catch (Exception e) {
//				// When no registration info found for current acad month year, process for latest student registration instead.
//				// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
//				studentDetails = studentDetailsDAO.getLatestRegistrationForStudent(sapid);
//			}
//			List<MBAExamBookingRequest> bookings = examBookingDAO.getAllStudentBookings(studentDetails);
//			List<MBAExamBookingRequest> approvedBookings = examBookingDAO.getAllStudentApprovedBookings(studentDetails);
//			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
//
//			StudentBean student = studentDetailsDAO.getSingleStudentsData(sapid);
//			fileName = pdfCreator.createPDF_MBAWX(bookings, approvedBookings, FEE_RECEIPT_PATH, student);
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
			MBAStudentDetailsBean currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(responseBean.getSapid(), CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR);
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
	
	private List<MBAStudentSubjectMarksDetailsBean> getResitSubjectsList(MBAExamBookingDetailsResponseBean responseBean) {
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
		StudentExamBean student = studentDetailsDAO.getSingleStudentsData(responseBean.getSapid());
		
		boolean canBook = true;
		String canNotBookReason = "";

		List<MBAStudentSubjectMarksDetailsBean> resitSubjects = getResitSubjectsList(responseBean);
		List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(responseBean);
		
		List<MBAStudentSubjectMarksDetailsBean> allBookableSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
		allBookableSubjects.addAll(failedSubjects);
		allBookableSubjects.addAll(resitSubjects);
		processSubjectsForBooking(responseBean, allBookableSubjects);
		
		getAppliedSubjects(responseBean);
		
		
		if(!responseBean.isExamBookingLive()) {
			canBook = false;
			canNotBookReason = "Exam Booking is not active!";
		} else if(failedSubjects.size() > 2 && ("111".equals(student.getConsumerProgramStructureId()) || "151".equals(student.getConsumerProgramStructureId()))) {
			checkIfStudentIsReAppearingForSemesterSubjects(responseBean);
			if(!responseBean.isReAppearingForSem()) {
				canBook = false;
				canNotBookReason = "You are not eligible for exam bookings as Re-exam (100 marks) is applicable for students who have failed in not more than two subjects of that respective Term.  ";
			}
		} else if(allBookableSubjects.size() == 0) {
			canBook = false;
			canNotBookReason = "You do not have any subjects eligible for the current Term Re-Exam";
		}
		
		responseBean.setCanBook(canBook);
		responseBean.setCanNotBookReason(canNotBookReason);
	}


	private void checkIfStudentIsReAppearingForSemesterSubjects(MBAExamBookingDetailsResponseBean responseBean) {
		for(MBAStudentSubjectMarksDetailsBean subject : responseBean.getFailedSubjectsList()) {
			int numberOfTimeboundEntriesForStudent = examBookingDAO.getNumberOfStudentMappingsForSubject(subject);
			if(numberOfTimeboundEntriesForStudent > 1) {
				responseBean.setReAppearingForSem(true);
			}
		}
	}
}
