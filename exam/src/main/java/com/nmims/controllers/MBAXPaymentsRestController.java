package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nmims.beans.MBAExamBookingPaytmResponse;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXPaymentsDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class MBAXPaymentsRestController {
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${SERVER}" )
	private String SERVER;
	
	private static final Logger logger = LoggerFactory.getLogger(MBAXPaymentsRestController.class);
	
	@Autowired
	private MBAPaymentHelper paymentHelper;
	
	@Autowired
	private MBAXPaymentsDao paymentsDao;

	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;
	
	@Autowired
	private MBAXExamBookingDAO examBookingDAO;
	
	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	@Autowired
	private MailSender mailSender;
	
	private String examBookingErrorURLWeb = "ssoservices/mbax/examBookingError";
//	private String examBookingFailURLWeb = "ssoservices/mbax/examBookingError";
	private String examBookingSuccessURLWeb = "ssoservices/mbax/examBookingSuccess";
	
	
	@PostMapping(path = "/saveExamBookingRequest_MBAX", produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<String> mbaCreateBookingRequest(HttpServletRequest request, Model model, @RequestBody MBAExamBookingRequest bookingRequest) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		boolean requestHasNoErrors = checkIfExamBookingRequestHasNoErrors(bookingRequest, response);
		if(requestHasNoErrors) {
			try {
				// Create records in db for the subjects
				boolean requestGeneratedSuccessfully = populateExamBookingObject(bookingRequest);
				if(requestGeneratedSuccessfully) {
					// Create records in db for the subjects
					boolean recordsCreatedSucceddfully = applyForFailedSubjects(bookingRequest);
					if(recordsCreatedSucceddfully) {
						
						if(bookingRequest.isGeneratePaymentMap()) {
							getPaymentMap(bookingRequest, request, response);
							getBookingsForSeatRelease(bookingRequest);
						} else {
							response.put("status", "success");
							response.put("response", bookingRequest);
						}
					}
				} else {
					response.put("status", "fail");
					response.put("message", bookingRequest.getError());
				}
			}catch (Exception e) {
				
				response.put("status", "fail");
			}
		}
		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
	}
	
	
	@PostMapping(path = "/mbaExamBookingCallbackPaytm_MBAX", produces = "application/json; charset=UTF-8")
	public ResponseEntity<String> mbaExamBookingCallbackPaytm(HttpServletRequest request, @RequestBody MBAExamBookingPaytmResponse inputRequest) {

		Map<String, Object> response = new HashMap<String, Object>();
		if(inputRequest.getApiresponse() == null) {
			response.put("status", "fail");
			response.put("message", "No Api Response Found!");
		} else {
			MBAExamBookingRequest bookingRequest = paymentsDao.getExamBookingRequestByTransactionIdAndSapid(inputRequest.getTrackId(), inputRequest.getSapid());
			
			String errorMessage = paymentHelper.checkPaytmChecksum(bookingRequest, inputRequest);
			

			if(StringUtils.isBlank(errorMessage)) {
				try {
					setSuccessfulTransactionStatus(bookingRequest);
					response.put("status", "success");
					response.put("message", "Payment successfully completed");
				}catch (Exception e) {
					response.put("status", "fail");
					response.put("message", bookingRequest.getError());
				}
			} else {
				response.put("status", "fail");
				response.put("message", errorMessage);
			}
		}
		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
	}
	
	private void getBookingsForSeatRelease(MBAExamBookingRequest bookingRequest) {
		if(bookingRequest.isForSlotChange()) {
			List<String> bookingIdsForRelease = examBookingDAO.getSuccessfulBookingsForSeatRelease(bookingRequest);
			bookingRequest.setBookingsForSeatRelease(bookingIdsForRelease);
		}
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
		
		public Map<String, Object> getPaymentMap(MBAExamBookingRequest bookingRequest, HttpServletRequest request, Map<String, Object> response ) {

			StudentExamBean student = studentMarksDAO.getSingleStudentsData(bookingRequest.getSapid());
			
			String checkSumResponse = paymentHelper.generateCommonCheckSum(bookingRequest, student, "Exam Registration (MBA - X)");
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

			logger.info("\n"+SERVER+": "+new Date()+" IN getPaymentMap set sessionInfo "+request.getSession().getAttribute("embaBookingRequest"));
			
			return response;
		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		private void setSuccessfulTransactionStatus(MBAExamBookingRequest bookingRequest) {
			bookingRequest.setLastModifiedBy(bookingRequest.getSapid());
			paymentHelper.processSuccessfulTrancation_MBAX(bookingRequest);
			

			// Release seats.
			releaseForSlotChange(bookingRequest);
		}
		
		public boolean setSelectedSubjectsList(MBAExamBookingRequest bookingRequest){

			MBAStudentDetailsBean currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(bookingRequest.getSapid());
			List<MBAStudentSubjectMarksDetailsBean> resitSubjects = getResitSubjectList(currentRegData);
			List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(currentRegData);
			
			List<MBAStudentSubjectMarksDetailsBean> allBookableSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
			allBookableSubjects.addAll(failedSubjects);
			allBookableSubjects.addAll(resitSubjects);
			
			List<MBAStudentSubjectMarksDetailsBean> selectedSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
			
			for (MBAStudentSubjectMarksDetailsBean thisSubject : bookingRequest.getSelectedSubjects()) {
				MBAStudentSubjectMarksDetailsBean subjectToReturn = null;
				
				String timeboundId = thisSubject.getTimeboundId();
				for (MBAStudentSubjectMarksDetailsBean failedSubject : allBookableSubjects) {
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
			return examBookingDAO.getFailedSubjectsForStudent(currentRegData);
		}
		
		private List<MBAStudentSubjectMarksDetailsBean> getResitSubjectList(MBAStudentDetailsBean currentRegData) {
			 return examBookingDAO.getResitFailedSubjectsForStudent(currentRegData);
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
		
		private void releaseForSlotChange(MBAExamBookingRequest bookingRequest) {

			
			List<String> bookingIdsList = bookingRequest.getBookingsForSeatRelease();

			if(bookingIdsList != null && bookingIdsList.size() > 0) {
				paymentsDao.releaseBookingsForIds(bookingIdsList, bookingRequest.getSapid());
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
}
