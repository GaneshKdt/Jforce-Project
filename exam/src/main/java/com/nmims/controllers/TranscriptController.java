package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamResultsBean;
import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.MBAMarksheetHelper;
import com.nmims.helpers.MBAWXTranscriptPDFCreator;
import com.nmims.helpers.MBAXTranscriptPDFCreator;
import com.nmims.helpers.MSCAI_ML_OPS_PCDS_PDDSTranscriptPDFCreator;
import com.nmims.helpers.PDDMTranscriptPDFCreator;
import com.nmims.helpers.TranscriptPDFCreator;
import com.nmims.services.MarksheetService;
import com.nmims.services.StudentService;
import com.nmims.services.impl.TranscriptGenerationServiceInterface;
import com.nmims.stratergies.impl.MBAXTranscriptStrategy;
import com.nmims.util.StringUtility;


@Controller
public class TranscriptController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	@Autowired
	TranscriptGenerationServiceInterface BulkTranscript;
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	MarksheetService mservice;

	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;

	@Value( "${STUDENT_PHOTOS_PATH}" )
	private String STUDENT_PHOTOS_PATH;

	@Value( "${HALLTICKET_PATH}" )
	private String HALLTICKET_PATH;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;

	private static final Logger logger = LoggerFactory.getLogger(TranscriptController.class);
	private static final Logger trasncript_logger = LoggerFactory.getLogger("trasncript");
	private final int pageSize = 10;
	private String mostRecentTimetablePeriod = null;
	private HashMap<String, String> programCodeNameMap = null;
	private HashMap<String, ProgramExamBean> programDetailsMap = null;
	private HashMap<String, String> examCenterIdNameMap = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	TreeMap<String,String> offlineExamCenterMap = null;
	TreeMap<String,String> onlineExamCenterMap = null;
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016","2017","2018","2019" , "2020" )); 

	private ArrayList<String> stateList = new ArrayList<String>(Arrays.asList( 
			"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir",
			"Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan",
			"Sikkim","Tamil Nadu","Telangana","Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", 
			"Chandigarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Pondicherry")); 

	public static final List<Integer> NON_GRADED_MASTER_KEY_LIST = (List<Integer>) Arrays.asList(145,146,147,148,149);
	
	private static final List<String> MBAWX_MASTERKEYS = Arrays.asList("111","151","160");
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		examCenterIdNameMap = null;
		getExamCenterIdNameMap();
		
		mostRecentTimetablePeriod = null;
		getMostRecentTimetablePeriod();
		
		programCodeNameMap = null;
		getProgramMap();
		
		offlineExamCenterMap = null;
		getOfflineExamCenterMapForDropDown();
		
		programDetailsMap = null;
		getAllProgramMap();
		
		return null;
	}
	
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null || this.subjectList.size()==0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null || this.programList.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public HashMap<String, String> getExamCenterIdNameMap(){
		if(this.examCenterIdNameMap == null || this.examCenterIdNameMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdNameMap = dao.getExamCenterIdNameMap();
		}
		return examCenterIdNameMap;
	}



	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate){
		//if(this.examCenterIdCenterMap == null || this.examCenterIdCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap(isCorporate);
		//}
		return examCenterIdCenterMap;
	}

	public TreeMap<String, String> getOfflineExamCenterMapForDropDown(){
		if(this.offlineExamCenterMap == null || this.offlineExamCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(false);
			this.offlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if(!"Offline".equalsIgnoreCase(bean.getMode())){
					continue;
				}
				offlineExamCenterMap.put(centerId, bean.getExamCenterName()+","+bean.getCity()+" ("+bean.getMode()+")");
			}
		}
		return offlineExamCenterMap;
	}

	public TreeMap<String, String> getOnlineExamCenterMapForDropDown(boolean isCorporate){
		if(this.onlineExamCenterMap == null || this.onlineExamCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(isCorporate);
			this.onlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if(!"Online".equalsIgnoreCase(bean.getMode())){
					continue;
				}
				onlineExamCenterMap.put(centerId, bean.getExamCenterName()+","+bean.getCity()+" ("+bean.getMode()+")");
			}
		}
		return onlineExamCenterMap;
	}

	public String getMostRecentTimetablePeriod(){
		if(this.mostRecentTimetablePeriod == null){
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.mostRecentTimetablePeriod = sDao.getMostRecentTimeTablePeriod();
		}

		return this.mostRecentTimetablePeriod;
	}

	public HashMap<String, String> getProgramMap(){
		if(this.programCodeNameMap == null || this.programCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	public HashMap<String, ProgramExamBean> getAllProgramMap(){
		if(this.programDetailsMap == null || this.programDetailsMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programDetailsMap = dao.getProgramMap();
		}
		return programDetailsMap;
	}


/*
	@RequestMapping(value = "/downloadHallTicket", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("studentHome");
		String sapid = (String)request.getSession().getAttribute("userId");
		String userDownloadingHallTicket = sapid; //Logged in user
		if(request.getParameter("userId") != null){
			//Coming from Admin page used for downloaing student hall ticket
			sapid = request.getParameter("userId");
			modelnView = new ModelAndView("downloadHallTicket");
		}else if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}else{
			//if he is a student and is logged in , then check if it is available for download
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			boolean isHallTicketAvailable = dao.isConfigurationLive("Hall Ticket Download");
			modelnView.addObject("isHallTicketAvailable", isHallTicketAvailable);
			if(!isHallTicketAvailable){
				setError(request, "Hall Ticket is not available for download currently");
				return modelnView;
			}
		}


		try{
			ArrayList<String> subjects = new ArrayList<>();


			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> subjectsBooked = eDao.getConfirmedBooking(sapid);

			if(subjectsBooked.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No subjects booked for Exam. Hall Ticket not available.");
				return modelnView;

			}

			StudentBean student = eDao.getSingleStudentWithValidity(sapid);
			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				subjectBookingMap.put(bean.getSubject(), bean);
			}

			for (int i = 0; i < subjectsBooked.size(); i++) {
				subjects.add(subjectsBooked.get(i).getSubject());
			}

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, student.getProgram(), student.getPrgmStructApplicable());

			HallTicketPDFCreator hallTicketCreator = new HallTicketPDFCreator();
			String fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
					HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(), subjectBookingMap, STUDENT_PHOTOS_PATH);

			dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Hall Ticket", e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Hall Ticket.");
		}

		return modelnView;

	}


	@RequestMapping(value = "/printBookingStatus", method = {RequestMethod.GET, RequestMethod.POST})
	public String printBookingStatus(HttpServletRequest request, HttpServletResponse response) {

		try{

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			
			if(userId == null){
				//Link clicked from Support portal by Agent
				userId = request.getParameter("userId");
			}
			
			StudentBean student = dao.getSingleStudentWithValidity(userId);
			
			List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(userId);
			if(examBookings == null || examBookings.isEmpty()){
				setError(request, "No Exam Bookings found current Exam Cycle");
				
				String redirectUrl = SERVER_PATH + "studentportal/home";
		        return "redirect:" + redirectUrl;
		        
			}
			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
			pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), MARKSHEETS_PATH, student);
			//String fileName = (String)request.getSession().getAttribute("fileName");
			String fileName = student.getSapid()+"_Booking.pdf";

			String filePathToBeServed = MARKSHEETS_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to download file.");
		}
		return null;

	}

	@RequestMapping(value = "/downloadHallTicketForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String downloadHallTicketForm(HttpServletRequest request, HttpServletResponse response) {
		return  "downloadHallTicket";
	}

	@RequestMapping(value = "/printDDForm", method = {RequestMethod.GET, RequestMethod.POST})
	public void printDDForm(HttpServletRequest request, HttpServletResponse response) {

		try{

			List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookings");
			StudentBean student = (StudentBean)request.getSession().getAttribute("student");

			ExamBookingPDFCreator pdfCreator = new ExamBookingPDFCreator();
			pdfCreator.createPDF(examBookings, getExamCenterIdNameMap(), MARKSHEETS_PATH, student);
			//String fileName = (String)request.getSession().getAttribute("fileName");
			String fileName = "testFile.pdf";

			//String filePathToBeServed = "D:\\Marksheets\\"+fileName;
			String filePathToBeServed = MARKSHEETS_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to download file.");
		}
	}

	@RequestMapping(value = "/attendanceSheetForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String attendanceSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean",bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("attendanceSearchBean", bean);

		return "attendanceSheet";
	}


	@RequestMapping(value = "/getAttendanceSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAttendanceSheet(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean bean){
		ModelAndView modelnView = new ModelAndView("attendanceSheet");
		request.getSession().setAttribute("attendanceSearchBean", bean);
		modelnView.addObject("bean",bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());



		if("Offline".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOfflineCenterId());
		}else if("Online".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOnlineCenterId());
		}

		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown());

		String userId = (String)request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> bookingsList = dao.getBookingsForAttendanceSheet(bean);

			if(bookingsList == null || bookingsList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No bookings found for given criteria to generate Attendance Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createAttendanceSheet(bookingsList, getMostRecentTimetablePeriod(), HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("attendanceFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Attendance Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Attendance Sheet.");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Attendance Sheet", e);
		}



		return modelnView;
	}

	@RequestMapping(value = "/downloadAttendanceSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAttendanceSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String)request.getSession().getAttribute("attendanceFileName");

		try{
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Attendance Sheet.");
		}

		return modelnView;

	}


	@RequestMapping(value = "/evaluationSheetForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String evaluationSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean",bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("evaluationSearchBean", bean);

		return "evaluationSheet";
	}

	@RequestMapping(value = "/getEvaluationSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getEvaluationSheet(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean bean){
		ModelAndView modelnView = new ModelAndView("evaluationSheet");
		request.getSession().setAttribute("evaluationSearchBean", bean);
		modelnView.addObject("bean",bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown());

		if("Offline".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOfflineCenterId());
		}else if("Online".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOnlineCenterId());
		}

		String userId = (String)request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> bookingsList = dao.getBookingsForAttendanceSheet(bean);

			if(bookingsList == null || bookingsList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No bookings found for given criteria to generate Evaluation Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createEvaluationSheet(bookingsList, getMostRecentTimetablePeriod(), HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("evaluationFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Evaluation Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Evaluation Sheet.");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Evaluation Sheet", e);
		}

		return modelnView;
	}

	@RequestMapping(value = "/downloadEvaluationSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadEvaluationSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String)request.getSession().getAttribute("evaluationFileName");

		try{
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Evaluation Sheet.");
		}

		return modelnView;

	}
	
	@RequestMapping(value = "/markCheckSheetForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String markCheckSheetForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		m.addAttribute("bean",bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("evaluationSearchBean", bean);

		return "markCheckSheet";
	}
	
	
	@RequestMapping(value = "/getMarkCheckSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getMarkCheckSheet(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean bean){
		ModelAndView modelnView = new ModelAndView("markCheckSheet");
		request.getSession().setAttribute("evaluationSearchBean", bean);
		modelnView.addObject("bean",bean);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown());

		if("Offline".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOfflineCenterId());
		}else if("Online".equals(bean.getExamMode())){
			bean.setCenterId(bean.getOnlineCenterId());
		}

		String userId = (String)request.getSession().getAttribute("userId");
		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> marksList = dao.getMarksCheckingSheet(bean);

			if(marksList == null || marksList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No record found for given criteria to generate Mark Check Sheet.");
				return modelnView;
			}
			HallTicketPDFCreator pdfCreator = new HallTicketPDFCreator();
			String fileName = pdfCreator.createMarksCheckingSheet(marksList, getMostRecentTimetablePeriod(), HALLTICKET_PATH, userId, bean);
			request.getSession().setAttribute("MarksChekingSheetFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Marks Cheking Sheet generated successfully. Please click link below to download.");

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Marks Cheking Sheet.");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Marks Cheking Sheet", e);
		}

		return modelnView;
	}
	
	
	@RequestMapping(value = "/downloadMarkCheckSheet", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadMarkCheckSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("home");
		String fileName = (String)request.getSession().getAttribute("MarksChekingSheetFileName");

		try{
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(filePathToBeServed);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		}catch(Exception e){
			

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Marks Cheking Sheet.");
		}

		return modelnView;

	}*/
	
	
	@RequestMapping(value = "/admin/transcriptForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String transcriptForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		StudentExamBean student = new StudentExamBean();
		m.addAttribute("student",student);

		return "transcriptSheet";
	}
	
	@RequestMapping(value = "/admin/getTranscript", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getTranscript(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student); 
		ModelAndView modelnView = new ModelAndView("transcriptSheet");
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("student",student);
		student.setFromAdmin("Y");
		generateTranscript(request, student, false);
		return modelnView;
	}
	@RequestMapping(value = "/student/generateStudentSelfTranscriptForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView generateStudentSelfTranscriptForm(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response) || request.getSession().getAttribute("studentExam") == null ) {
			redirectToPortalApp(response);
			return null;
		}
		
		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			return new ModelAndView("noDataAvailable");
		}

		ModelAndView mv = new ModelAndView("studentSelfTranscript");
		getStudentMarksHistory(request, response);
		mv.addObject("yearList", yearList);
		try {

			PassFailDAO passFailDao =  (PassFailDAO)act.getBean("passFailDAO");
			ArrayList<PassFailExamBean> passList = passFailDao.getPassRecords((String)request.getSession().getAttribute("userId"));
			if(passList.size() > 0) {
				mv.addObject("passList", passList);
				mv.addObject("showGenerateButton", "true");
			} else {
				mv.addObject("passList", new ArrayList<PassFailExamBean>());
				mv.addObject("error", "true");
				mv.addObject("errorMessage", "No subjects available for transcript!");
			}
		}catch (Exception e) {
			
			mv.addObject("passList", new ArrayList<PassFailExamBean>());
			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Error getting subjects for transcript");
		}
		return mv;
	}
	
	@RequestMapping(value = "/student/generateStudentSelfTranscript", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView generateStudentSelfTranscript(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response) || request.getSession().getAttribute("studentExam") == null ) {
			redirectToPortalApp(response);
			return null;
		}
		
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");

		ModelAndView modelnView = new ModelAndView("studentSelfTranscript");
		getStudentMarksHistory(request, response);
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("student",student);
		generateTranscript(request, student, true);
		return modelnView;
	}
	
	private void generateTranscript(HttpServletRequest request, StudentExamBean student, boolean showWatermark) {

		try {
			String fileName= BulkTranscript.generateSingleTranscript(student);
//			PassFailDAO passFailDao =  (PassFailDAO)act.getBean("passFailDAO");
//			ArrayList<PassFailExamBean> passList = passFailDao.getPassRecordsForStudentSelfTranscript(student.getSapid());
//			String lastExamMonthYear = "";
//			String lateralLastExamMonthYear = "";
//			double lastWrittenAttemptOrder = passFailDao.getLastWrittenPassExamMonthYear(student.getSapid());
//			double lastAssignmentAttemptOrder = passFailDao.getLastAssignmentPassExamMonthYear(student.getSapid());
//			
////			Added to find out last Exam month year of student to be printed on transcript pdf
//			
//			if (lastWrittenAttemptOrder > lastAssignmentAttemptOrder) {
//				lastExamMonthYear = passFailDao.getWrittenLastPassExamMonth(student.getSapid());
//			} else {
//				lastExamMonthYear = passFailDao.getAssignmentLastPassExamMonth(student.getSapid());
//			}
//			String logoRequired = student.getLogoRequired();
//			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
//			student = eDao.getSingleStudentsData(student.getSapid());
//			
//			
//			ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
//			
//			StudentExamBean lateralStudent = new StudentExamBean();
//			//lateralStudent = eDao.getSingleStudentsData(student.getPreviousStudentId());
//			
//			/*Added by Steffi to consider lateral students.
//			Check if student is lateral student and has done program with NGASCE in past. In this case his passed subjects will become waived off subjects
//			Course Waiver is not applicable for Jul2009 program Structure Students*/			
//			HashMap<String,ArrayList<String>> semWiseSubjectMap = passFailDao.getSemWiseSubjectsMap(student.getProgram(),student.getPrgmStructApplicable());
//			StudentService studentService = (StudentService) act.getBean("studentService");
//			studentService.mgetWaivedOffSubjects(student);
//			
//			if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && student.getWaivedOffSubjects().contains("Business Statistics")) {
//				student.getWaivedOffSubjects().add("Decision Science");
//			} 
//			
//			HashMap<String, String> lateralSapidLastExamMonthYearMap = new HashMap<String, String>();
//			if(student.getIsLateral().equalsIgnoreCase("Y") ) {
//				
//				lateralStudent = eDao.getSingleStudentsData(student.getPreviousStudentId());
//				lateralStudentList.add(lateralStudent);
//				if(lateralStudent.getIsLateral().equalsIgnoreCase("Y") ) {
//					List<StudentExamBean> lateralStudentList2 = getLateralStudentsData(eDao, lateralStudent);
//					lateralStudentList.addAll(lateralStudentList2);
//				}
//				Collections.reverse(lateralStudentList);//sorting in descending order to get old program and after next program
//				
//				student.getWaivedOffSubjects().remove("Project"); 
//				//fetching previous program pass list
//				ArrayList<PassFailExamBean> lateralPassList = mservice.getLateralPasslist(student,lateralStudentList,semWiseSubjectMap,passFailDao );
//				passList.addAll(lateralPassList); // adding lateral passlist detail in current passlist 
//				
//				//to find out last Exam month year of lateral student to be printed on transcript pdf
//				
//				for(StudentExamBean lateralData :lateralStudentList) {
//					//double lateralLastWrittenAttemptOrder = passFailDao.getLastWrittenPassExamMonthYear(student.getPreviousStudentId());
//					//double lateralLastAssignmentAttemptOrder = passFailDao.getLastAssignmentPassExamMonthYear(student.getPreviousStudentId());
//					double lateralLastWrittenAttemptOrder = passFailDao.getLastWrittenPassExamMonthYear(lateralData.getSapid());
//					double lateralLastAssignmentAttemptOrder = passFailDao.getLastAssignmentPassExamMonthYear(lateralData.getSapid());
//					if (lateralLastWrittenAttemptOrder > lateralLastAssignmentAttemptOrder) {
//						lateralLastExamMonthYear = passFailDao.getWrittenLastPassExamMonth(lateralData.getSapid());
//					} else {
//						lateralLastExamMonthYear = passFailDao.getAssignmentLastPassExamMonth(lateralData.getSapid());
//					}
//					lateralSapidLastExamMonthYearMap.put(lateralData.getSapid(), lateralLastExamMonthYear);
//				}
//			}
//			
//			if(passList == null || passList.size() == 0){
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "No Pass record found for given Student Number.");
//				return;
//			}
//			TranscriptPDFCreator pdfCreator = new TranscriptPDFCreator();
//			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//			student.setLogoRequired(logoRequired);
//			//String fileName = pdfCreator.createTrascript(passList, student,lateralStudent, getAllProgramMap(), HALLTICKET_PATH, lastExamMonthYear, dao.getExamOrderMap(),semWiseSubjectMap ,getAllProgramMap(), dao,showWatermark,lateralLastExamMonthYear);
//			String fileName = pdfCreator.createTrascript(passList, student,lateralStudentList, getAllProgramMap(), HALLTICKET_PATH, lastExamMonthYear, dao.getExamOrderMap(),semWiseSubjectMap ,getAllProgramMap(), dao,showWatermark,lateralSapidLastExamMonthYearMap);
			//bean = service.generatetranscript
			request.getSession().setAttribute("transcriptSheetFileName", fileName);
			System.out.println("This is the filename"+fileName);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transcript generated successfully. Please click link below to download.");

		} catch (Exception e) {
			System.out.println("this is the catch");
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Transcript."+e.getMessage());
		}
	}
	
//	@RequestMapping(value = "/downloadTrascriptSheet", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView downloadTrascriptSheet(HttpServletRequest request, HttpServletResponse response, Model m) {
//		ModelAndView modelnView = new ModelAndView("home");
//		String fileName = (String)request.getSession().getAttribute("transcriptSheetFileName");
//
//		try{
//			String filePathToBeServed = HALLTICKET_PATH + fileName;
//			File fileToDownload = new File(filePathToBeServed);
//			InputStream inputStream = new FileInputStream(fileToDownload);
//			response.setContentType("application/pdf");
//			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
//			IOUtils.copy(inputStream, response.getOutputStream());
//			response.flushBuffer();
//
//		}catch(Exception e){
//			
//
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "Error in generating Marks Cheking Sheet.");
//		}
//
//		return modelnView;
//
//	}
//	

	@RequestMapping(value = "/downloadTrascriptSheet", method = { RequestMethod.GET, RequestMethod.POST })
	public void download(HttpServletRequest request, HttpServletResponse response, Model m) {
		try {
			String fileName = (String)request.getSession().getAttribute("transcriptSheetFileName");
			File fileToDownload = new File(HALLTICKET_PATH + fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader(
					"Content-Disposition",
					"attachment; filename="
							+ fileName.substring(fileName.lastIndexOf("/") + 1,
									fileName.length()));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to download file.");
		}
	}


	private void getStudentMarksHistory(HttpServletRequest request,
			HttpServletResponse response) {
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute(
				"studentExam");
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(student.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		if ("Online".equals(student.getExamMode())) {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOnline(bean);
		} else {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOffline(bean);
		}

		request.setAttribute("studentMarksListForMarksHistory",
				studentMarksListForMarksHistory);
	}
	

	@RequestMapping(value = "/admin/transcriptFormMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public String transcriptFormMBAWX(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		StudentExamBean student = new StudentExamBean();
		m.addAttribute("student",student);

		return "transcriptSheetMBAWX";
	}
	
	@RequestMapping(value = "/admin/getTranscriptMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getTranscriptMBAWX(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){
		ModelAndView modelnView = new ModelAndView("transcriptSheetMBAWX");
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("student",student);
		MBAMarksheetHelper marksheetHelper = (MBAMarksheetHelper) act.getBean("MBAMarksheetHelper");
		String sapid = student.getSapid();
		try {
		MBATranscriptBean transcriptBean =  marksheetHelper.getTranscriptBeanForSapid(sapid);
		transcriptBean.setLogoRequired("Y".equals(student.getLogoRequired()) ? "Y" : "N");
		// transcriptBean.setSoftCopy(true); 
		
		String fileName=null;
		
			MBAWXTranscriptPDFCreator transcriptHelper = new MBAWXTranscriptPDFCreator();
		    MSCAI_ML_OPS_PCDS_PDDSTranscriptPDFCreator transcriptHelpers = new  MSCAI_ML_OPS_PCDS_PDDSTranscriptPDFCreator();
		    
		  
			if(MBAWX_MASTERKEYS.contains(transcriptBean.getStudent().getConsumerProgramStructureId())) {
				 fileName = transcriptHelper.createTrascript(transcriptBean, HALLTICKET_PATH, getAllProgramMap());
			}
			else
			{
				fileName = transcriptHelpers.createTrascripts(transcriptBean, HALLTICKET_PATH, getAllProgramMap());
			}
			request.getSession().setAttribute("transcriptSheetFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transcript generated successfully. Please click link below to download.");

		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Transcript.");
			trasncript_logger.error("Error in generating Trasncript for sapid" + student.getSapid() +"Error is" + e.getMessage());
		}
		
		return modelnView;
	}
	


	@RequestMapping(value = "/admin/transcriptFormMBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public String transcriptFormMBAX(HttpServletRequest request, HttpServletResponse respnse, Model m) {


		StudentExamBean student = new StudentExamBean();
		m.addAttribute("student",student);

		return "transcriptSheetMBAX";
	}
	
	@RequestMapping(value = "/admin/getTranscriptMBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getTranscriptMBAX(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){
		ModelAndView modelnView = new ModelAndView("transcriptSheetMBAX");
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("student",student);
		MBAXTranscriptStrategy transcriptStrategy = (MBAXTranscriptStrategy) act.getBean("mbaxTranscriptStrategy");
		String sapid = student.getSapid();
		
		// transcriptBean.setSoftCopy(true);
		
		try {

			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			StudentExamBean studentBean = dao.getSingleStudentsData(sapid);
			MBATranscriptBean transcriptBean =  transcriptStrategy.getTranscriptBeanForStudent(sapid, "Y", studentBean);
			transcriptBean.setLogoRequired("Y".equals(student.getLogoRequired()) ? "Y" : "N");
			
			MBAXTranscriptPDFCreator transcriptHelper = new MBAXTranscriptPDFCreator();
			String fileName = transcriptHelper.createTrascript(transcriptBean, HALLTICKET_PATH, getAllProgramMap());
			
			request.getSession().setAttribute("transcriptSheetFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transcript generated successfully. Please click link below to download.");

		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Transcript." + e.getMessage());
		}
		
		return modelnView;
	}
	
	
	public ArrayList<StudentExamBean> getLateralStudentsData(ExamBookingDAO eDao, StudentExamBean lateralStudent){
		ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
		StudentExamBean prevLateralStudent = eDao.getSingleStudentsData(lateralStudent.getPreviousStudentId());
		lateralStudentList.add(prevLateralStudent);
		if(prevLateralStudent.getIsLateral().equalsIgnoreCase("Y") ) { // check again if lateral student is again lateral or not
			List<StudentExamBean> lateralDataList2 = getLateralStudentsData(eDao,prevLateralStudent); // if yes then again repeat the method
			lateralStudentList.addAll(lateralDataList2);
		}
		return lateralStudentList;
	}
	
	@RequestMapping(value = "/admin/transcriptPDDMForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String transcripPDDMtForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Add New Company Page");

		StudentExamBean student = new StudentExamBean();
		m.addAttribute("student",student);

		return "transcriptSheetPDDM";
	}
	
	@RequestMapping(value = "/admin/getPDDMTranscript", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPDDMTranscript(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student); 
		ModelAndView modelnView = new ModelAndView("transcriptSheetPDDM");
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("student",student);
		generatePDDMTranscript(request, student, false);
		return modelnView;
	}
	
	private void generatePDDMTranscript(HttpServletRequest request, StudentExamBean student, boolean showWatermark) {

		try {
			
			PassFailDAO passFailDao =  (PassFailDAO)act.getBean("passFailDAO");
			String lastExamMonthYear = "";
			List<PassFailExamBean>passList = passFailDao.getPassFailBySapid(student.getSapid());
			if(passList == null || passList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Pass record found for given Student Number.");
				return;
				}
			Collections.reverse(passList);
			if(passList.size() > 0) {
				PassFailExamBean passbean = passList.get(0);
				//String lastExamMonthYear = "";
				
				lastExamMonthYear = passbean.getExamMonth() +"-"+ passbean.getExamYear();
			}
			String logoRequired = student.getLogoRequired();
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			student = eDao.getSingleStudentsData(student.getSapid());
			HashMap<String,ArrayList<String>> semWiseSubjectMap = passFailDao.getSemWiseSubjectsMap(student.getProgram(),student.getPrgmStructApplicable());
			PDDMTranscriptPDFCreator pdfCreator = new PDDMTranscriptPDFCreator();
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			student.setLogoRequired(logoRequired);
			String fileName = pdfCreator.createTrascriptForPDDM(passList, student, getAllProgramMap(), HALLTICKET_PATH, lastExamMonthYear, dao.getExamOrderMap(),semWiseSubjectMap ,getAllProgramMap(), dao,showWatermark);
			
			request.getSession().setAttribute("transcriptSheetFileName", fileName);

			request.setAttribute("success","true");
			request.setAttribute("successMessage","Transcript generated successfully. Please click link below to download.");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Transcript.");
		}
	}
	
	
}
