package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.nmims.beans.MBAExamBookingDetailsResponseBean;
import com.nmims.beans.MBAExamBookingPaytmResponse;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAWXPaymentsController {

	@Autowired
	private MBAPaymentHelper paymentHelper;
	
	@Autowired
	private MBAWXPaymentsDao paymentsDao;

	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;
	
	@Autowired
	private MBAWXExamBookingDAO examBookingDAO;
	
	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	@Autowired
	private MailSender mailSender;
	
	private String examBookingErrorURLWeb = "timeline/examBookingError";
//	private String examBookingFailURLWeb = "timeline/examBookingError";
	private String examBookingSuccessURLWeb = "timeline/examBookingSuccess";
	
	private String examBookingErrorURLMobile = "embaPaymentError";
	private String examBookingFailURLMobile = "embaExamBookingPaymentFailure";
	private String examBookingSuccessURLMobile = "embaExamBookingPaymentSuccess";
	
//	@RequestMapping(value = "/m/saveExamBookingRequest", method = RequestMethod.POST , produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<String> mbaCreateBookingRequest(HttpServletRequest request, Model model, @RequestBody MBAExamBookingRequest bookingRequest) {
//		
//		Map<String, Object> response = new HashMap<String, Object>();
//		boolean requestHasNoErrors = checkIfExamBookingRequestHasNoErrors(bookingRequest, response);
//		if(requestHasNoErrors) {
//			try {
//				// Create records in db for the subjects
//				boolean requestGeneratedSuccessfully = populateExamBookingObject(bookingRequest);
//				if(requestGeneratedSuccessfully) {
//					// Create records in db for the subjects
//					boolean recordsCreatedSucceddfully = applyForFailedSubjects(bookingRequest);
//					if(recordsCreatedSucceddfully) {
//						
//						if(bookingRequest.isGeneratePaymentMap()) {
//							getPaymentMap(bookingRequest, request, response);
//							getBookingsForSeatRelease(bookingRequest);
//						} else {
//							response.put("status", "success");
//							response.put("response", bookingRequest);
//						}
//					}
//				} else {
//					response.put("status", "fail");
//					response.put("message", bookingRequest.getError());
//				}
//			}catch (Exception e) {
//				
//				response.put("status", "fail");
//			}
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}
	
	private void getBookingsForSeatRelease(MBAExamBookingRequest bookingRequest) {
		if(bookingRequest.isForSlotChange()) {
			List<String> bookingIdsForRelease = examBookingDAO.getSuccessfulBookingsForSeatRelease(bookingRequest);
			bookingRequest.setBookingsForSeatRelease(bookingIdsForRelease);
		}
	}

	public boolean checkIfExamBookingRequestHasNoErrors(MBAExamBookingRequest bookingRequest, Map<String, Object> response) {
		if(bookingRequest.getSapid() == null) {
			response.put("status", "fail");
			response.put("message", "Empty or invalid student!");
			return false;
		}

		if(bookingRequest.getPaymentOption() == null) { 
			response.put("status", "fail");
			response.put("message", "Payment Option not selected!");
			return false;
		}

		if(bookingRequest.getSelectedSubjects() == null) {
			response.put("status", "fail");
			response.put("message", "Subjects not selected!");
			return false;
		}

		if(bookingRequest.getSource() == null) {
			response.put("status", "fail");
			response.put("message", "Source not selected!");
			return false;
		}
		boolean invalidNumberOfSubjects = bookingRequest.getSelectedSubjects().size() == 0 && bookingRequest.getSelectedSubjects().size() > 2;

		if(invalidNumberOfSubjects) {
			response.put("status", "fail");
			response.put("message", "Invalid number of subjects selected!");
			return false;
		}
		if(!bookingRequest.isForSlotChange()) {
			boolean hasDoubleBookings = checkForDoubleBookings(bookingRequest);
			
			if(hasDoubleBookings) {
				response.put("status", "fail");
				response.put("message", "Double booking found for student!");
				return false;
			}
		}
		
		return true;
	}

	private boolean populateExamBookingObject(MBAExamBookingRequest bookingRequest) {
		String sapid = bookingRequest.getSapid();
		String trackId = sapid + System.currentTimeMillis();
		bookingRequest.setTrackId(trackId);
		bookingRequest.setPaymentType(MBAExamBookingRequest.PAYMENT_TYPE_BOOKING);
		bookingRequest.setDescription("Exam Booking for student " + sapid);
		bookingRequest.setTranStatus(MBAExamBookingRequest.TRAN_STATUS_INITIATED);
		bookingRequest.setCreatedBy(sapid);
		bookingRequest.setLastModifiedBy(sapid);
		if(!setSelectedSubjectsList(bookingRequest)) {
			bookingRequest.setError("Invalid subject entry found!");
			return false;
		}
		setTotalBookingAmount(bookingRequest);
		return true;
	}
	
	@RequestMapping(value = "/embaInitiateExamBooking", method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json; charset=UTF-8")
	public String srFeeResponsePOST(HttpServletRequest request, Model model) {

		Map<String, Object> response = new HashMap<String, Object>();
		if(request.getParameter("sapid") == null) {
			response.put("status", "fail");
			response.put("message", "Empty or invalid student!");
		} else if(request.getParameter("trackId") == null) { 
			response.put("status", "fail");
			response.put("message", "Payment Id not selected!");
		} else {
			String sapid = request.getParameter("sapid");
			String trackId = request.getParameter("trackId");
			String isForSlotChange = request.getParameter("isForSlotChange");
			String isWeb = request.getParameter("isWeb");
			
			
			MBAExamBookingRequest bookingRequest = paymentsDao.getInitiatedPaymentRequestByTrackIdAndSapid(trackId, sapid);
			if(bookingRequest == null) {
				response.put("status", "fail");
				response.put("message", "Payment attempt already made!");
			} else {

				if(!StringUtils.isBlank(isForSlotChange) && isForSlotChange.equals("true")) {
					bookingRequest.setForSlotChange(true);
				}
				if(!StringUtils.isBlank(isWeb) && isWeb.equals("true")) {
					bookingRequest.setWeb(true);
				}
				
				getPaymentMap(bookingRequest, request, response);
			}
			model.addAttribute("bookingRequest", bookingRequest);
		}

		if(response.get("status").equals("success")) {
			return "embaPayments/pay";
		} else {
			model.addAttribute("errorMessage", response.get("message"));
			if(request.getParameter("source") != null && request.getParameter("source").equalsIgnoreCase("webapp")) {
				model.addAttribute("status", "fail");
				return "redirect:" + "../" + examBookingErrorURLWeb;
			}
			return "redirect:/m/" + examBookingErrorURLMobile;
		}
	}

	public Map<String, Object> getPaymentMap(MBAExamBookingRequest bookingRequest, HttpServletRequest request, Map<String, Object> response ) {

		StudentExamBean student = studentMarksDAO.getSingleStudentsData(bookingRequest.getSapid());
		
		String checkSumResponse = paymentHelper.generateCommonCheckSum(bookingRequest, student, "Exam Registration (MBA - WX)");
		if(checkSumResponse.equalsIgnoreCase("true")) {

			bookingRequest.setTranStatus(MBAExamBookingRequest.TRAN_STATUS_INITIATED);
			response.put("status", "success");
			response.put("response", bookingRequest);
		} else {
			response.put("status", "fail");
			response.put("message", checkSumResponse);
		}

		getBookingsForSeatRelease(bookingRequest);
		request.getSession().setAttribute("embaBookingRequest", bookingRequest);
		return response;
	}

	private boolean checkForDoubleBookings(MBAExamBookingRequest bookingRequest) {

		MBAStudentDetailsBean currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(bookingRequest.getSapid());
		List<String> subjectsAppliedFor = examBookingDAO.getAppliedSubjects(currentRegData);
		List<MBAStudentSubjectMarksDetailsBean> selectedSubjects = bookingRequest.getSelectedSubjects();

		for (MBAStudentSubjectMarksDetailsBean selectedSubject : selectedSubjects) {
			if(subjectsAppliedFor.contains(selectedSubject.getTimeboundId())) {
				return true;
			}
		}
		return false;
	}

	public boolean setSelectedSubjectsList(MBAExamBookingRequest bookingRequest){

		MBAStudentDetailsBean currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(bookingRequest.getSapid());
		List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(currentRegData);
		
		List<MBAStudentSubjectMarksDetailsBean> selectedSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
		
		for (MBAStudentSubjectMarksDetailsBean thisSubject : bookingRequest.getSelectedSubjects()) {
			MBAStudentSubjectMarksDetailsBean subjectToReturn = null;
			
			String timeboundId = thisSubject.getTimeboundId();
			for (MBAStudentSubjectMarksDetailsBean failedSubject : failedSubjects) {
				if(failedSubject.getTimeboundId().equals(timeboundId)) {
					subjectToReturn = failedSubject;
				}
			}
			
			if(subjectToReturn != null) {
				subjectToReturn.setSlotId(thisSubject.getSlotId());
				selectedSubjects.add(subjectToReturn);
			}
		}
		if(selectedSubjects.size() == bookingRequest.getSelectedSubjects().size()) {
			bookingRequest.setSelectedSubjects(selectedSubjects);
			return true;
		}
		return false;
	}
	
	private List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsList(MBAStudentDetailsBean currentRegData) {
		// get the students the student has failed
		List<MBAStudentSubjectMarksDetailsBean> applicableSubjectsList = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
		try {
			List<MBAStudentSubjectMarksDetailsBean> reSitFailedSubjectsList = examBookingDAO.getResitFailedSubjectsForStudent(currentRegData);
			applicableSubjectsList.addAll(reSitFailedSubjectsList);
		}catch (Exception e) {
			// TODO: handle exception
			
		}
		try {
			List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList = examBookingDAO.getFailedSubjectsForStudent(currentRegData);
			applicableSubjectsList.addAll(failedSubjectsList);
		}catch (Exception e) {
			// TODO: handle exception
			
		}
		return applicableSubjectsList;
		
	}
	
	private void setTotalBookingAmount(MBAExamBookingRequest bookingRequest) {
		int total = 0;
		for (MBAStudentSubjectMarksDetailsBean selectedSubject : bookingRequest.getSelectedSubjects()) {
			if(bookingRequest.isForSlotChange()) {
				total = total + Integer.parseInt(selectedSubject.getSlotChangeAmount());
			} else {
				total = total + Integer.parseInt(selectedSubject.getBookingAmount());
			}
		}
		bookingRequest.setAmount(Integer.toString(total));
	}
	
	public boolean applyForFailedSubjects(MBAExamBookingRequest bookingRequest){

		try {
			Long id = paymentsDao.insertNewPaymentRecord(bookingRequest);
			bookingRequest.setPaymentRecordId(id.toString());
			for (MBAStudentSubjectMarksDetailsBean selectedSubject : bookingRequest.getSelectedSubjects()) {
				selectedSubject.setSapid(bookingRequest.getSapid());
				paymentsDao.insertExamBooking(selectedSubject, id, bookingRequest.getSapid());
			}
			return true;
		}catch (Exception e) {
			
			bookingRequest.setError("Error adding request object to database");
		}
		return false;
	}

	@RequestMapping(value = { 
//			"/m/embaExamBookingPaymentFailure", "/m/embaExamBookingPaymentSuccess", "/m/embaPaymentError",
			"/embaExamBookingPaymentFailure", "/embaExamBookingPaymentSuccess", "/embaPaymentError"
	})
	public String embaExamBookingPaymentFailure(HttpServletRequest request, ModelMap model, Model m) {
		return "embaPayments/embaMessage";
	}
	
	
//	to be deleted,api shifted
//	@RequestMapping(value = "/m/mbaExamBookingCallbackPaytm", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
//	public ResponseEntity<String> mbaExamBookingCallbackPaytm(HttpServletRequest request, @RequestBody MBAExamBookingPaytmResponse inputRequest) {
//
//		Map<String, Object> response = new HashMap<String, Object>();
//		if(inputRequest.getApiresponse() == null) {
//			response.put("status", "fail");
//			response.put("message", "No Api Response Found!");
//		} else {
//			MBAExamBookingRequest bookingRequest = paymentsDao.getExamBookingRequestByTransactionIdAndSapid(inputRequest.getTrackId(), inputRequest.getSapid());
//			
//			String errorMessage = paymentHelper.checkPaytmChecksum(bookingRequest, inputRequest);
//			
//
//			if(StringUtils.isBlank(errorMessage)) {
//				try {
//					setSuccessfulTransactionStatus(bookingRequest);
//					response.put("status", "success");
//					response.put("message", "Payment successfully completed");
//				}catch (Exception e) {
//					response.put("status", "fail");
//					response.put("message", bookingRequest.getError());
//				}
//			} else {
//				response.put("status", "fail");
//				response.put("message", errorMessage);
//			}
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}

	@RequestMapping(value = "/mbaExamBookingCallback", method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json; charset=UTF-8")
	public String mbaExamBookingCallback(HttpServletRequest request, Model model, Model m) {

		if(request.getSession().getAttribute("embaBookingRequest") == null) {
			model.addAttribute("status", "fail");
			model.addAttribute("message", "Invlid or empty request!");
			return "redirect:" + examBookingFailURLMobile;
		} else {
			MBAExamBookingRequest bookingRequest = (MBAExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");
			if(bookingRequest.getSource().equalsIgnoreCase("webapp")) {
				if(bookingRequest.getPaymentOption() != null) { 
					model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
				}
				if(bookingRequest.getSelectedSubjects() != null) {
					model.addAttribute("timeboundIds", bookingRequest.getSelectedSubjects());
				}
				if(bookingRequest.getSapid() != null) {
					model.addAttribute("sapid", bookingRequest.getSapid());
				}
				if(bookingRequest.getTrackId() != null) {
					model.addAttribute("trackId", bookingRequest.getTrackId());
				}
			}
			return parseExamBookingResponse(request, model, bookingRequest);
		}
	}
	
	private String parseExamBookingResponse(HttpServletRequest request, Model model, MBAExamBookingRequest bookingRequest) {
		paymentHelper.createResponseBean(request, bookingRequest);
		
		String errorMessage = paymentHelper.checkErrorInPayment(request, bookingRequest);
		

		if(errorMessage == null) {
			try {
				
				setSuccessfulTransactionStatus(bookingRequest);
				model.addAttribute("status", "success");

				if(bookingRequest.getSource() != null && bookingRequest.getSource().equalsIgnoreCase("webapp")) {
					return "redirect:" + "../" + examBookingSuccessURLWeb;
				}
				return "redirect:" + examBookingSuccessURLMobile;
			}catch (Exception e) {
				
				
				// Send a mail to admin along with stack trace.
				mailSender.mailStackTrace("MBA - WX : Error in Saving Successful Transaction", e);
				
				model.addAttribute("status", "fail");
				model.addAttribute("message", bookingRequest.getError());
			}
		} else {
			model.addAttribute("status", "fail");
			model.addAttribute("message", errorMessage);
		}

		if(bookingRequest.getSource() != null && bookingRequest.getSource().equalsIgnoreCase("webapp")) {
			return "redirect:" + "../" + examBookingErrorURLWeb;
		}
		return "redirect:" + examBookingFailURLMobile;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void setSuccessfulTransactionStatus(MBAExamBookingRequest bookingRequest) {
		bookingRequest.setLastModifiedBy(bookingRequest.getSapid());
		paymentHelper.processSuccessfulTrancation_MBAWX(bookingRequest);
		

		// Release seats.
		releaseForSlotChange(bookingRequest);
	}

	private void releaseForSlotChange(MBAExamBookingRequest bookingRequest) {

		
		List<String> bookingIdsList = bookingRequest.getBookingsForSeatRelease();

		if(bookingIdsList != null && bookingIdsList.size() > 0) {
			paymentsDao.releaseBookingsForIds(bookingIdsList, bookingRequest.getSapid());
		}
	}

	
	// Code added temporarily to check trans status in PROD
	@RequestMapping(value = "/1112222/checkTransactionStatus", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<String> test(@RequestBody MBAExamBookingRequest bean,HttpServletRequest request) {

		MBAExamBookingRequest bookingRequest = paymentsDao.getInitiatedPaymentRequestByTrackIdAndSapid(bean.getTrackId(), bean.getSapid());
		paymentHelper.checkTransactionStatus(bookingRequest);
		return new ResponseEntity<String>(new Gson().toJson(bookingRequest), HttpStatus.OK);
	}
}
