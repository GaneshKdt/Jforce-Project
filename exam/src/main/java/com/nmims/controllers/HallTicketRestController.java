package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.HallTicketPDFCreator;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.HtServiceLayer;

@RestController
@RequestMapping("m")
public class HallTicketRestController {
	
	@Autowired
	ApplicationContext act;

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	HtServiceLayer htServiceLayer;
	
	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;

	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;
	
	
	HashMap<String, String> corporateCenterUserMapping = null;
	private String mostRecentTimetablePeriod = null;
	private HashMap<String, String> programCodeNameMap = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	
	public HashMap<String, String> getCorporateCenterUserMapping() {
		if (this.corporateCenterUserMapping == null || this.corporateCenterUserMapping.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			this.corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
		}
		return this.corporateCenterUserMapping;
	}
	
	public String getMostRecentTimetablePeriod() {
		if (this.mostRecentTimetablePeriod == null) {
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
		}

		return this.mostRecentTimetablePeriod;
	}
	
	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null || this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate) {

		ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
		this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap(isCorporate);
		return examCenterIdCenterMap;
	}
	
	@PostMapping(path = "/previewHallTicket", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ServiceRequestBean> MpreviewHallTicket(@RequestBody StudentExamBean input) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/json");
				
		ServiceRequestBean response = htServiceLayer.getHallTicketData(input.getSapid(), getMostRecentTimetablePeriod(), getCorporateCenterUserMapping(), getProgramMap());

		return new ResponseEntity<ServiceRequestBean>(response, headers, HttpStatus.OK);

	}
	
	@PostMapping(path = "/previewHallTicketForMbaWx", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ServiceRequestBean> MpreviewHallTicketForMbaWx(@RequestBody StudentExamBean input) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/json");
				
		ServiceRequestBean response = htServiceLayer.getHallTicketDataForMbaWx(input.getSapid(), getProgramMap());

		
		return new ResponseEntity<ServiceRequestBean>(response, headers, HttpStatus.OK);

	}
	
	
	@PostMapping(path = "/downloadHallTicket", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> downloadHallTicket(@RequestBody StudentExamBean input) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new HashMap<String, String>();
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		List<String> blockedSapids = dao.getBlockedSapids();
		String fileName = "";
		String userDownloadingHallTicket = input.getSapid(); // Logged in user
		StudentExamBean student;
		student = dao.getSingleStudentsData(input.getSapid());
		if (blockedSapids.contains(student.getSapid())) {
			response.put("error", "Your Hall Ticket is on hold. Please contact NGASCE to get access for same");
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
		boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
		if (!isHallTicketAvailable) {
			response.put("error", "Hall Ticket is not available for download currently");
			return new ResponseEntity(response, headers, HttpStatus.OK);
		}
		try {
			ArrayList<String> subjects = new ArrayList<>();
			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(student.getSapid());
			if (subjectsBooked.size() == 0) {
				response.put("error", "No subjects booked for Exam. Hall Ticket not available.");
				return new ResponseEntity(response, headers, HttpStatus.OK);
			}
			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();
			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				subjectBookingMap.put(bean.getSubject(), bean);

			} 
			List<ExamBookingTransactionBean> passwordPresent = new ArrayList<ExamBookingTransactionBean>();

			List<ExamBookingTransactionBean> passwordAbsent = new ArrayList<ExamBookingTransactionBean>();
			for (ExamBookingTransactionBean bean : subjectsBooked) {
				if (StringUtils.isBlank(bean.getPassword())) {
					passwordAbsent.add(bean);
				} else {
					passwordPresent.add(bean);
				}
			}
			String password = "";
			if (passwordAbsent.size() > 0 && passwordPresent.size() > 0) {
				password = passwordPresent.get(0).getPassword();
				String month = passwordPresent.get(0).getMonth();
				String year = passwordPresent.get(0).getYear();
				eDao.assignPass(student.getSapid(), password, month, year);
			}

			if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
				String month = passwordAbsent.get(0).getMonth();
				String year = passwordAbsent.get(0).getYear();
				password = generateRandomPass(student.getSapid());
				eDao.assignPass(student.getSapid(), password, month, year);
			}
			if (passwordAbsent.size() == 0 && passwordPresent.size() > 0) {
				password = passwordPresent.get(0).getPassword();
			}
			for (int i = 0; i < subjectsBooked.size(); i++) {
				subjects.add(subjectsBooked.get(i).getSubject());
			}

			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student);

			HallTicketPDFCreator hallTicketCreator = new HallTicketPDFCreator();
			corporateCenterUserMapping = getCorporateCenterUserMapping();

			if(corporateCenterUserMapping.containsKey(student.getSapid())){
			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(true), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
		}else{
			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(false), subjectBookingMap, STUDENT_PHOTOS_PATH,password);
		}
		dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
		response.put("success", fileName);
		return new ResponseEntity(response, headers, HttpStatus.OK);


		} catch (Exception e) {
			
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Hall Ticket: " + student.getSapid(), e);
			response.put("error", "Error in generating Hall Ticket.");
			return new ResponseEntity(response, headers, HttpStatus.OK);
			// TODO Auto-generated catch block
		}

	}
	
	
	public String generateRandomPass(String sapid) {
		String generatedString =null;

		try {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 99 + 1);
			String FirstString = String.valueOf(randomNum);
			String SecondString = RandomStringUtils.randomNumeric(8);
			generatedString = FirstString + SecondString;
		} catch (Exception e) {
			
			return generatedString;
		}
	
	return generatedString;
	}

}
