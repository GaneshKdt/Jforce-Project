package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nmims.beans.MBACentersBean;
import com.nmims.beans.MBAExamBookingDetailsResponseBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAExamReceiptBean;
import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.MBAPaymentHelper;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class MBAWXExamBookingRestController {
	
	@Autowired
	MBAWXExamBookingDAO examBookingDAO;
	
	@Autowired
	private MBAPaymentHelper examPaymentHelper;

	@Autowired
	private MBAWXPaymentsDao examPaymentDao;
	
	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;
	
	@Autowired
	private MBAWXLiveSettingsDAO mBAWXLiveSettingsDAO;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${CURRENT_PDDM_ACAD_MONTH}")
	private String CURRENT_PDDM_ACAD_MONTH; 
	
	@Value("${CURRENT_PDDM_ACAD_YEAR}")
	private String CURRENT_PDDM_ACAD_YEAR;

	@Value("${PDDM_EXAM_BOOKING_CHARGES}")
	private String PDDM_EXAM_BOOKING_CHARGES;
	
	@Value("${PDDM_EXAM_BOOKING_SLOT_CHANGE_CHARGES}")
	private String PDDM_EXAM_BOOKING_SLOT_CHANGE_CHARGES;
	
	@Autowired(required=false)
	ApplicationContext act;
	
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018","2019","2020","2021")); 
	
	private static final List<Integer> pddmMasterkeylist = Arrays.asList(142,143,144,145,146,147,148,149);
	
	@RequestMapping(value = "/getAllCenters", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<String> getAllCenters(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		try{
			List<MBACentersBean> allCenters = examBookingDAO.getAllCenters();
			response.put("status", "success");
			response.put("response", allCenters);
		} catch(Exception e) {
			response.put("status", "fail");
			
		}
		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getAllSlotsForCenter", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<String> getAllSlotsForCenter(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();

		try{
			Long centerId = inputSlotInfo.getCenterId();
			List<MBASlotBean> slots = examBookingDAO.getAllSlotsByCenterId(centerId);
			response.put("status", "success");
			response.put("response", slots);
		} catch(Exception e) {
			response.put("status", "fail");
			
		}
		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getExamBookingDashboardDetails_MBAWX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<MBAResponseBean> getDashboardInfo(@RequestBody StudentExamBean student,HttpServletRequest request) {

		MBAResponseBean response = new MBAResponseBean();
		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
		try{
			responseData.setSapid(student.getSapid());
			getCurrentRegistrationData(responseData);
			getExamBookingLive(responseData);
			checkIfCanBook(responseData);
			response.setStatusSuccess();
		} catch(Exception e) {
			response.setStatusFail();
			
		}
		response.setResponse(responseData);
		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/getStudentExamBookings", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<MBAResponseBean> getStudentExamBookings(@RequestBody StudentExamBean student,HttpServletRequest request) {

		MBAResponseBean response = new MBAResponseBean();
		MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
		try{
			responseData.setSapid(student.getSapid());
			getCurrentRegistrationData(responseData);
			getCurrentCompletedBookings(responseData);
			response.setStatusSuccess();
		} catch(Exception e) {
			response.setStatusFail();
			
		}
		response.setResponse(responseData);
		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
	}
	

//	TODO : Remove after Exam Booking is done for Jan-2020
		
		@RequestMapping(value = "/getStudentExamBookingsForTrackId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
		public ResponseEntity<String> getStudentExamBookingsForTrackId(@RequestBody MBAExamBookingRequest bean,HttpServletRequest request) {
	
			Map<String, Object> response = new HashMap<String, Object>();
			
			try{
				List<MBAExamBookingRequest> allCenters = examBookingDAO.getAllStudentBookingsForTrackId(bean.getSapid(), bean.getTrackId());
				response.put("status", "success");
				response.put("response", allCenters);
			} catch(Exception e) {
				response.put("status", "fail");
				
			}
			return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
		}
		
		
		@RequestMapping(value = "/getSlotsForCenterAndTimeboundId", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
		public ResponseEntity<String> getSlotsForCenterAndTimeboundId(@RequestBody MBASlotBean inputSlotInfo,HttpServletRequest request) {
	
			Map<String, Object> response = new HashMap<String, Object>();
			
			try{
				Long timeboundId = inputSlotInfo.getTimeboundId();
				Long centerId = inputSlotInfo.getCenterId();
				List<MBASlotBean> slots = examBookingDAO.getAllSlotsByTimeboundIdAndCenterId(timeboundId, centerId);
				List<Map<String, String>> slotsList = new ArrayList<Map<String, String>>();
				for (MBASlotBean mbaSlotBean : slots) {

					Map<String, String> slotMap = new HashMap<String, String>();
					slotMap.put("examYear", mbaSlotBean.getExamYear());
					slotMap.put("examMonth", mbaSlotBean.getExamMonth());
					slotMap.put("slotId", Long.toString(mbaSlotBean.getSlotId()));
					slotMap.put("centerId", Long.toString(mbaSlotBean.getCenterId()));
					slotMap.put("timeTableId", Long.toString(mbaSlotBean.getTimeTableId()));
					slotMap.put("capacity", Long.toString(mbaSlotBean.getCapacity()));
					slotMap.put("bookedSlots", Long.toString(mbaSlotBean.getBookedSlots()));
					slotMap.put("availableSlots", Long.toString(mbaSlotBean.getAvailableSlots()));
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
					String examStartDateTime = formatter.format(mbaSlotBean.getExamStartDateTime());
					String examEndDateTime = formatter.format(mbaSlotBean.getExamEndDateTime());
					slotMap.put("examStartDateTime", examStartDateTime);
					slotMap.put("examEndDateTime", examEndDateTime);
					slotsList.add(slotMap);
				}
				response.put("status", "success");
				response.put("response", slotsList);
			} catch(Exception e) {
				response.put("status", "fail");
				
			}
			return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
		}
		
		@RequestMapping(value = "/getExamBookingData", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8", consumes = "application/json")
		public ResponseEntity<MBAResponseBean> getExamBookingData(@RequestBody StudentExamBean student,HttpServletRequest request) {
			

			MBAResponseBean response = new MBAResponseBean();
			MBAExamBookingDetailsResponseBean responseData = new MBAExamBookingDetailsResponseBean();
			responseData.setSapid(student.getSapid());
			
			try {

				getCurrentRegistrationData(responseData);
				getExamBookingLive(responseData);
				checkIfCanBook(responseData);
				
				response.setStatusSuccess();
			}catch (Exception e) {
				response.setStatusFail();
				
			}
			response.setResponse(responseData);
			return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
		}
		
		
		@RequestMapping(value = "/printBookingStatus_MBAWX", method = { RequestMethod.GET, RequestMethod.POST })
		public ResponseEntity<MBAExamReceiptBean> printBookingStatus(@RequestBody StudentExamBean input,HttpServletRequest request) {

			MBAExamReceiptBean receipt = new MBAExamReceiptBean();
			try {

				String fileName = "";

				String sapid = input.getSapid();

				if(sapid == null) {
					receipt.setStatus("fail");
					receipt.setErrorMessage("No Sapid!");
					return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
				}
				MBAStudentDetailsBean studentDetails;
				try {		
					// Get latest timebound details for current acad cycle 
					studentDetails = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR);
					
				}catch (Exception e) {
					// When no registration info found for current acad month year, process for latest student registration instead.
					// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
					studentDetails = studentDetailsDAO.getLatestRegistrationForStudent(sapid);
				}
				List<MBAExamBookingRequest> bookings = examBookingDAO.getAllStudentBookings(studentDetails);
				List<MBAExamBookingRequest> approvedBookings = examBookingDAO.getAllStudentApprovedBookings(studentDetails);
				ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();

				StudentExamBean student = studentDetailsDAO.getSingleStudentsData(sapid);
				fileName = pdfCreator.createPDF_MBAWX(bookings, approvedBookings, FEE_RECEIPT_PATH, student);
				receipt.setStatus("success");

				String url = fileName;
				url = url.split(":/")[1];
				url = SERVER_PATH + url;
				receipt.setDownloadURL(url);

				return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
			} catch (Exception e) {
				
				receipt.setStatus("fail");
				receipt.setErrorMessage("Error generating receipt!");
				return new ResponseEntity<MBAExamReceiptBean>(receipt, HttpStatus.OK);
			}
		}
		
		private void getCurrentRegistrationData(MBAExamBookingDetailsResponseBean responseBean) {
			StudentExamBean examBean = studentDetailsDAO.getSingleStudentsData(responseBean.getSapid());
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
				// Get latest timebound details for current acad cycle
				MBAStudentDetailsBean currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(
						responseBean.getSapid(),acadMonth ,acadYear );
				
				//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
				if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
					currentRegData.setCurrentAcadMonth("Apr"); currentRegData.setCurrentAcadYear("2023");
					currentRegData.setCurrentExamMonth("Jul"); currentRegData.setCurrentExamYear("2023");
				}//if
				
				responseBean.setCurrentRegistrationDetails(currentRegData);
			}catch (Exception e) {
				// When no registration info found for current acad month year, process for latest student registration instead.
				// Gets the registration data along with the exam month/year (derived from timebound ids) for the latest student registration
				MBAStudentDetailsBean currentRegData = studentDetailsDAO.getLatestRegistrationForStudent(responseBean.getSapid());
				
				//Temporary added on July 07, 2022 by Siddheshwar_K for PDDM program Exam Bookings
//				if(pddmMasterkeylist.contains(Integer.parseInt(currentRegData.getConsumerProgramStructureId()))) {
//					currentRegData.setCurrentAcadMonth("Oct"); currentRegData.setCurrentAcadYear("2022");
//					currentRegData.setCurrentExamMonth("Jan"); currentRegData.setCurrentExamYear("2023");
//				}//if
				
				responseBean.setCurrentRegistrationDetails(currentRegData);
			}
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
			boolean isExtended = false;

			List<MBAStudentSubjectMarksDetailsBean> resitSubjects = getResitSubjectsList(responseBean);
			List<MBAStudentSubjectMarksDetailsBean> failedSubjects = getFailedSubjectsList(responseBean);
			
			List<MBAStudentSubjectMarksDetailsBean> allBookableSubjects = new ArrayList<MBAStudentSubjectMarksDetailsBean>();
			allBookableSubjects.addAll(failedSubjects);
			allBookableSubjects.addAll(resitSubjects);
			
			updateExamboookingCharges(allBookableSubjects, Integer.parseInt(responseBean.getCurrentRegistrationDetails().getConsumerProgramStructureId()));
			
			processSubjectsForBooking(responseBean, allBookableSubjects);
			
			getAppliedSubjects(responseBean);
			
			
			if(!responseBean.isExamBookingLive()) {
				
				try {
					isExtended = mBAWXLiveSettingsDAO.isRegistrationExtendedForStudent(responseBean.getSapid(),
							responseBean.getCurrentRegistrationDetails().getCurrentExamMonth(),
							responseBean.getCurrentRegistrationDetails().getCurrentExamYear());
				} catch (Exception e) {
					
				}
				
				if(isExtended) {
					canBook = true;
				} else {
				canBook = false;
				canNotBookReason = "Exam Booking is not active!";
				}
				
			} else if(failedSubjects.size() > 2 && ("111".equals(student.getConsumerProgramStructureId()) || "151".equals(student.getConsumerProgramStructureId()) || "160".equals(student.getConsumerProgramStructureId()))) {
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
		
		
		private List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsList(MBAExamBookingDetailsResponseBean responseBean) {
			return examBookingDAO.getFailedSubjectsForStudent(responseBean.getCurrentRegistrationDetails());
		}
		
		private List<MBAStudentSubjectMarksDetailsBean> getResitSubjectsList(MBAExamBookingDetailsResponseBean responseBean) {
			List<MBAStudentSubjectMarksDetailsBean> list = new ArrayList<>();
			if(pddmMasterkeylist.contains(Integer.parseInt(responseBean.getCurrentRegistrationDetails().getConsumerProgramStructureId()))) {
				list = examBookingDAO.getResitFailedSubjectsForStudent(responseBean.getCurrentRegistrationDetails().getSapid(),
						responseBean.getCurrentRegistrationDetails().getCurrentAcadYear(), 
						responseBean.getCurrentRegistrationDetails().getCurrentAcadMonth());
			}else {
				list = examBookingDAO.getResitFailedSubjectsForStudent(responseBean.getCurrentRegistrationDetails());
			}
			return list;
		}

		private void processSubjectsForBooking(MBAExamBookingDetailsResponseBean responseBean, List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList) {

			// get the booking details (if any) for this subject
			for (MBAStudentSubjectMarksDetailsBean subject : failedSubjectsList) {
				MBAExamBookingRequest previousBookingDetails = examBookingDAO.getLatestStudentBookingForTimeboundId(responseBean.getSapid(), subject.getTimeboundId());
				subject.setPreviousBookingDetails(previousBookingDetails);
			}
			
			responseBean.setFailedSubjectsList(failedSubjectsList);
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
