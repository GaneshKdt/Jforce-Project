package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.IdCardExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExamBookingPDFCreator;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.HallTicketPDFCreator;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.IdCardServiceInterface;
import com.nmims.services.HtServiceLayer;

@Controller
public class HallTicketStudentController extends BaseController
{
	
	@Autowired
	ApplicationContext act;

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	HtServiceLayer htServiceLayer;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${STUDENT_PHOTOS_PATH}")
	private String STUDENT_PHOTOS_PATH;

	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;

	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;
	
	@Value("${SR_FILES_S3_PATH}")
	private String SR_FILES_S3_PATH;

	@Autowired
	HallTicketPDFCreator hallTicketCreator;
	
	@Autowired
	IdCardServiceInterface idCardService;
	
	@Autowired
	FileUploadHelper fileUploadHelper;

	

	private String mostRecentTimetablePeriod = null;
	private HashMap<String, String> programCodeNameMap = null;

	private HashMap<String, String> examCenterIdNameMap = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	TreeMap<String, String> offlineExamCenterMap = null;
	TreeMap<String, String> onlineExamCenterMap = null;

	HashMap<String, String> corporateCenterUserMapping = null;
	
	@Value( "${MARKSHEET_BUCKENAME}" )
	private String 	MARKSHEET_BUCKENAME;
	
	public String RefreshCache() {
	

		examCenterIdNameMap = null;
		getExamCenterIdNameMap();

		mostRecentTimetablePeriod = null;
		getMostRecentTimetablePeriod();

	

		corporateCenterUserMapping = null;
		getCorporateCenterUserMapping();

		return null;
	}
	
	public HashMap<String, String> getExamCenterIdNameMap() {
		if (this.examCenterIdNameMap == null || this.examCenterIdNameMap.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		}
		return examCenterIdNameMap;
	}
	
	
	public String getMostRecentTimetablePeriod() {
		if (this.mostRecentTimetablePeriod == null) {
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
		}

		return this.mostRecentTimetablePeriod;
	}
	
	public HashMap<String, String> getCorporateCenterUserMapping() {
		if (this.corporateCenterUserMapping == null || this.corporateCenterUserMapping.size() == 0) {
			ExamCenterDAO dao = (ExamCenterDAO) act.getBean("examCenterDAO");
			this.corporateCenterUserMapping = dao.getCorporateCenterUserMapping();
		}
		return this.corporateCenterUserMapping;
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

	
	@RequestMapping(value = "/student/myDocuments", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myDocuments(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("myDocuments");
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");

		getAdmissionFeeReceiptFromSapId(student, request);

		getExamFeeReceiptFromSapId(student, request);

		getHallTicketFromSapId(student, request);
		
		getProjectFeeReceiptFromSapId(student, request);
		
		getAssignmentFeeReceiptFromSapId(student, request);

		getPCPBookingsFromSapid(student, request);

		List<ServiceRequestBean> listOfSRDocumentsBasedOnSapid = getMySRDocumentsFromSapId(student.getSapid());
		modelAndView.addObject("listOfSRDocumentsBasedOnSapid", listOfSRDocumentsBasedOnSapid);
		
		IdCardExamBean idCardBean=idCardService.getIdCardForStudent(student.getSapid());
		request.setAttribute("idCardBean", idCardBean);
		
		return modelAndView;
	}

	private void getProjectFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfProjectFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Project Fee Receipt");
		request.setAttribute("listOfProjectFeeReceiptsBasedOnSapid", listOfProjectFeeReceiptsBasedOnSapid);
	}

	private void getPCPBookingsFromSapid(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfPCPBookingsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "PCP Fee Receipt");
		request.setAttribute("listOfPCPBookingsBasedOnSapid", listOfPCPBookingsBasedOnSapid);

	}
	
	private void getAssignmentFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfAssignmentFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Assignment Fee Receipt");
		request.setAttribute("listOfAssignmentFeeReceiptsBasedOnSapid", listOfAssignmentFeeReceiptsBasedOnSapid);
		}

	private void getAdmissionFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ArrayList<StudentExamBean> lstOfAdmissionPaymentReceipt = salesforceHelper.listOfPaymentsMade(student.getSapid());
		request.setAttribute("lstOfAdmissionPaymentReceipt", lstOfAdmissionPaymentReceipt);
	}

	public void getExamFeeReceiptFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Exam Fee Receipt");
		request.setAttribute("listOfFeeReceiptsBasedOnSapid", listOfFeeReceiptsBasedOnSapid);

	}

	public void getHallTicketFromSapId(StudentExamBean student, HttpServletRequest request) {
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		ArrayList<ExamBookingTransactionBean> listOfHallTicketsBasedOnSapid = dao
				.listOfDocumentsBasedOnSapidAndDocType(student.getSapid(), "Hall Ticket");
		request.setAttribute("listOfHallTicketsBasedOnSapid", listOfHallTicketsBasedOnSapid);

	}

	
	
	@RequestMapping(value = "/student/previewHallTicket", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView previewHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {

		ModelAndView modelnView = new ModelAndView("previewHallTicket");
		String sapid = (String) request.getSession().getAttribute("userId");
			
		ServiceRequestBean serviceRequestBean=new ServiceRequestBean();
		try {
			serviceRequestBean = htServiceLayer.getHallTicketData(sapid, getMostRecentTimetablePeriod(), getCorporateCenterUserMapping(), getProgramMap());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}  
	
		m.addAttribute("serviceLayerResponse", serviceRequestBean);
		
		return modelnView;
	}
	
	
	@RequestMapping(value = "/student/downloadHallTicket", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("studentHallTicket");
		String sapid = (String) request.getSession().getAttribute("userId");
		String userDownloadingHallTicket = sapid; // Logged in user
		ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
		List<String> blockedSapids = dao.getBlockedSapids();
		String fileName = "";
		StudentExamBean student;
		if (request.getParameter("userId") != null) {
			// Coming from Admin page used for downloaing student hall ticket
			sapid = request.getParameter("userId");
			modelnView = new ModelAndView("downloadHallTicket");
			ExamBookingDAO ebDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			student = ebDao.getSingleStudentsData(sapid);
		} else if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		} else {

			if (blockedSapids.contains(sapid)) {
				setError(request, "Your Hall Ticket is on hold. Please contact NGASCE to get access for same");
				return modelnView;
			}
			student = (StudentExamBean) request.getSession().getAttribute("studentExam");

			// if he is a student and is logged in , then check if it is available for
			// download

			boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
			modelnView.addObject("isHallTicketAvailable", isHallTicketAvailable);
			if (!isHallTicketAvailable) {
				setError(request, "Hall Ticket is not available for download currently");
				return modelnView;
			}
		}

		try {
			ArrayList<String> subjects = new ArrayList<>();

			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(sapid);

			if (subjectsBooked.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No subjects booked for Exam. Hall Ticket not available.");
				return modelnView;

			}

			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

			HashMap<String, ExamBookingTransactionBean> subjectDoubleBookingMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				// Added by Steffi
				String key1 = bean.getSapid() + bean.getSubject();
				String key2 = bean.getSapid() + bean.getExamDate() + bean.getExamTime();
				if (!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)) {
					subjectDoubleBookingMap.put(key1, bean);
					subjectDoubleBookingMap.put(key2, bean);
					subjectBookingMap.put(bean.getSubject(), bean);
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error");
					return modelnView;
				}
				// Added by Steffi end

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
				eDao.assignPass(sapid, password, month, year);
			}

			if (passwordAbsent.size() > 0 && passwordPresent.size() == 0) {
				String month = passwordAbsent.get(0).getMonth();
				String year = passwordAbsent.get(0).getYear();
				password = generateRandomPass(sapid);
				eDao.assignPass(sapid, password, month, year);
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
			
			/**
			 * Commented by Riya as hallticket will be served from s3
			 */
			// String filePathToBeServed = HALLTICKET_PATH + fileName;
			/*File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();*/
			String hallticket_url = fileUploadHelper.uploadDocument(fileName, fileName, MARKSHEET_BUCKENAME);
			if(StringUtils.isBlank(hallticket_url)) { 
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in generating Hall Ticket.");
			}
			modelnView.addObject("fileName", hallticket_url);
		} catch (Exception e) {
			
			MailSender mailSender = (MailSender) act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Hall Ticket: " + sapid, e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Hall Ticket.");
		}

		return modelnView;

	}
	
	@RequestMapping(value = "/student/printBookingStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public String printBookingStatus(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		try {

			ExamBookingDAO dao = (ExamBookingDAO) act.getBean("examBookingDAO");
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			ExamCenterDAO examCenterDao = (ExamCenterDAO) act.getBean("examCenterDAO");
			HashMap<String, String> corporateCenterUserMapping = new HashMap<String, String>();
			HashMap<String, String> getCorporateExamCenterIdNameMap = new HashMap<String, String>();
			corporateCenterUserMapping = examCenterDao.getCorporateCenterUserMapping();
			boolean isCorporate = false;
			String fileName = "";

			String userId = (String) request.getSession().getAttribute("userId");

			if (userId == null) {
				// Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}

			// Added this avoid null in reciept download for students ending validity in
			// Oct.
			StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
			// StudentBean student = dao.getSingleStudentWithValidity(userId);
			if (corporateCenterUserMapping.containsKey(student.getSapid())) {
				isCorporate = true;
			}
			getCorporateExamCenterIdNameMap = examCenterDao.getCorporateExamCenterIdNameMap();
			List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(userId);
			List<ExamBookingTransactionBean> confirmedOrReleasedExamBookings = dao.getConfirmedOrRelesedBooking(userId);
			if (examBookings == null || examBookings.isEmpty()) {
				setError(request, "No Exam Bookings found for current Exam Cycle");
				/*
				 * redirectAttributes.addFlashAttribute("error", "true");
				 * redirectAttributes.addFlashAttribute("errorMessage",
				 * "No Exam Bookings found current Exam Cycle");
				 * 
				 * String redirectUrl = SERVER_PATH + "studentportal/home";
				 */
				return "examBookingReceipt";

			}
			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
			if (isCorporate) {
				fileName = pdfCreator.createPDF(examBookings, getCorporateExamCenterIdNameMap, FEE_RECEIPT_PATH,
						student, confirmedOrReleasedExamBookings);
			} else {
				fileName = pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), FEE_RECEIPT_PATH, student,
						confirmedOrReleasedExamBookings);
			}

			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + userId + "_exam_Fee_Receipt.pdf");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Exam Fee Receipt: " + e.getMessage());
			return "examBookingReceipt";
		}
		return null;

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

	private List<ServiceRequestBean> getMySRDocumentsFromSapId(String sapid) {
		ServiceRequestDAO sDao = (ServiceRequestDAO) act.getBean("serviceRequestDao");
        List<ServiceRequestBean> listOfSRDocumentsBasedOnSapid = new ArrayList<>();
        try {
        	listOfSRDocumentsBasedOnSapid = sDao.getGeneratedSrDocuments(sapid);
        	listOfSRDocumentsBasedOnSapid.stream().forEach(list -> {
        		list.setFilePath(SR_FILES_S3_PATH + list.getFilePath());
        	});
		}
        catch(Exception e) {
//			e.printStackTrace();
		}
        
        return listOfSRDocumentsBasedOnSapid;
	}
}