package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nmims.beans.MBAExamBookingDetailsResponseBean;
import com.nmims.beans.MBAExamBookingPaytmResponse;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;


@RestController
@RequestMapping("m")
public class MBAWXPaymentsRestController {
	
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
	
	@Value("${PDDM_EXAM_BOOKING_CHARGES}")
	private String PDDM_EXAM_BOOKING_CHARGES;
	
	@Value("${PDDM_EXAM_BOOKING_SLOT_CHANGE_CHARGES}")
	private String PDDM_EXAM_BOOKING_SLOT_CHANGE_CHARGES;
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${CURRENT_PDDM_ACAD_MONTH}")
	private String CURRENT_PDDM_ACAD_MONTH; 
	
	@Value("${CURRENT_PDDM_ACAD_YEAR}")
	private String CURRENT_PDDM_ACAD_YEAR;
	
	private static final List<Integer> pddmMasterkeylist = Arrays.asList(142,143,144,145,146,147,148,149);
	
	@PostMapping(path = "/saveExamBookingRequest" , produces = "application/json; charset=UTF-8", consumes = "application/json")
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
	
	
	@RequestMapping(value = { 
			"/embaExamBookingPaymentFailure", "/embaExamBookingPaymentSuccess", "/embaPaymentError"
	})
	public String embaExamBookingPaymentFailure(HttpServletRequest request, ModelMap model, Model m) {
		return "embaPayments/embaMessage";
	}
	
	@PostMapping(path = "/mbaExamBookingCallbackPaytm", produces = "application/json; charset=UTF-8")
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
	
	
	private boolean checkForDoubleBookings(MBAExamBookingRequest bookingRequest) {
		StudentExamBean examBean = studentDetailsDAO.getSingleStudentsData(bookingRequest.getSapid());
		MBAStudentDetailsBean currentRegData;
		String acadYear = null;
		String acadMonth=null;
		
		if(pddmMasterkeylist.contains(Integer.parseInt(examBean.getConsumerProgramStructureId()))) {
			acadYear = CURRENT_PDDM_ACAD_YEAR;
			acadMonth = CURRENT_PDDM_ACAD_MONTH;
		}else {
			acadYear = CURRENT_MBAWX_ACAD_YEAR;
			acadMonth = CURRENT_MBAWX_ACAD_MONTH;
		}
		
try {
		
		currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(bookingRequest.getSapid(), acadMonth, acadYear);
		
		//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
		if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
			currentRegData.setCurrentAcadMonth("Jan"); currentRegData.setCurrentAcadYear("2023");
			currentRegData.setCurrentExamMonth("Apr"); currentRegData.setCurrentExamYear("2023");
		}//if
		
	}catch (Exception e) {
		// When no registration info found for current acad month year, process for latest student registration instead.
		// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
		currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(bookingRequest.getSapid());
		
		//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
		if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
			currentRegData.setCurrentAcadMonth("Jan"); currentRegData.setCurrentAcadYear("2023");
			currentRegData.setCurrentExamMonth("Apr"); currentRegData.setCurrentExamYear("2023");
		}//if
		
	}
	
	
	
	
		
		
		
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
		StudentExamBean examBean = studentDetailsDAO.getSingleStudentsData(bookingRequest.getSapid());
		MBAStudentDetailsBean currentRegData;
		String acadYear = null;
		String acadMonth=null;
		
		if(pddmMasterkeylist.contains(Integer.parseInt(examBean.getConsumerProgramStructureId()))) {
			acadYear = CURRENT_PDDM_ACAD_YEAR;
			acadMonth = CURRENT_PDDM_ACAD_MONTH;
		}else {
			acadYear = CURRENT_MBAWX_ACAD_YEAR;
			acadMonth = CURRENT_MBAWX_ACAD_MONTH;
		}
		
		try {
				
				currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(bookingRequest.getSapid(), acadMonth, acadYear);
				
				//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
				if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
					currentRegData.setCurrentAcadMonth("Apr"); currentRegData.setCurrentAcadYear("2023");
					currentRegData.setCurrentExamMonth("Jul"); currentRegData.setCurrentExamYear("2023");
				}//if
				
			}catch (Exception e) {
				// When no registration info found for current acad month year, process for latest student registration instead.
				// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
				currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(bookingRequest.getSapid());
				
				//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
				if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
					currentRegData.setCurrentAcadMonth("Apr"); currentRegData.setCurrentAcadYear("2023");
					currentRegData.setCurrentExamMonth("Jul"); currentRegData.setCurrentExamYear("2023");
				}//if
				
			}
		
		List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(currentRegData);
		
		updateExamboookingCharges(failedSubjects, Integer.parseInt(currentRegData.getConsumerProgramStructureId()) );
		
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
			List<MBAStudentSubjectMarksDetailsBean> reSitFailedSubjectsList = new ArrayList<>();
			if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
				reSitFailedSubjectsList = examBookingDAO.getResitFailedSubjectsForStudent(currentRegData.getSapid(),
						currentRegData.getCurrentAcadYear(), currentRegData.getCurrentAcadMonth());
			}else {
				reSitFailedSubjectsList = examBookingDAO.getResitFailedSubjectsForStudent(currentRegData);
			}
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
	
	private void updateExamboookingCharges(List<MBAStudentSubjectMarksDetailsBean> list, Integer consumerProgramStructureId) {
		List<Integer> pddmMasterkeylist = Arrays.asList(142,143,144,145,146,147,148,149);
		if(pddmMasterkeylist.contains(consumerProgramStructureId)) {
			for(MBAStudentSubjectMarksDetailsBean bean : list) {
				bean.setBookingAmount(PDDM_EXAM_BOOKING_CHARGES);
				bean.setSlotChangeAmount(PDDM_EXAM_BOOKING_SLOT_CHANGE_CHARGES);
			}
		}
	}
}
