package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExecutiveTimetableBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ExecutiveExamBookingDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExecutiveHallTicketPDFCreator;
import com.nmims.helpers.HallTicketPDFCreator;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;

@Controller
public class ExecutiveHallTicketController extends BaseController{

	@Autowired
	ApplicationContext act;

	
	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;


	@Value( "${HALLTICKET_PATH}" )
	private String HALLTICKET_PATH;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value("${FEE_RECEIPT_PATH}")
	private String FEE_RECEIPT_PATH;
	
	@Value( "${STUDENT_PHOTOS_PATH}" )
	private String STUDENT_PHOTOS_PATH;
	
	private HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	private String mostRecentTimetablePeriod = null;
	private HashMap<String, String> programCodeNameMap = null;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		mostRecentTimetablePeriod = null;
		getMostRecentTimetablePeriod();
		
		programCodeNameMap = null;
		getProgramMap();
		
		return null;
	}
	
	public String getMostRecentTimetablePeriod(){
		if(this.mostRecentTimetablePeriod == null){
			ExecutiveExamBookingDao dao = (ExecutiveExamBookingDao)act.getBean("executiveExamBookingDao");
			this.mostRecentTimetablePeriod = dao.getMostRecentTimeTablePeriodExecutive();
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
	

	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(Boolean val){
		  ExecutiveExamBookingDao dao = (ExecutiveExamBookingDao)act.getBean("executiveExamBookingDao");
		  this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap();
		 return examCenterIdCenterMap;
	}
	
	

	@RequestMapping(value = "/downloadExecutiveHallTicket", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExecutiveHallTicket(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("studentHallTicket");
		String sapid = (String)request.getSession().getAttribute("userId");
		String userDownloadingHallTicket = sapid; //Logged in user
		ExecutiveExamBookingDao dao = (ExecutiveExamBookingDao)act.getBean("executiveExamBookingDao");
		String fileName ="";
		if(request.getParameter("userId") != null){
			//Coming from Admin page used for downloaing student hall ticket
			sapid = request.getParameter("userId");
			StudentExamBean students = dao.getSingleStudentsData(sapid);
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
			students.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("studentExam",students);
			modelnView = new ModelAndView("downloadExecutiveHallTicket");
		}else if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}else{
		
			//if he is a student and is logged in , then check if it is available for download
			
			boolean isHallTicketAvailable = dao.isHallTicketLive(sapid);
			modelnView.addObject("isHallTicketAvailable", isHallTicketAvailable);
			if(!isHallTicketAvailable){
				setError(request, "Hall Ticket is not available for download currently");
				return modelnView;
			}
		}


		try{
			ArrayList<String> subjects = new ArrayList<>();


			ArrayList<ExamBookingTransactionBean> subjectsBooked = dao.getConfirmedBooking(sapid);

			if(subjectsBooked.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No subjects booked for Exam. Hall Ticket not available.");
				return modelnView;

			}

			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			HashMap<String, ExamBookingTransactionBean> subjectBookingMap = new HashMap<>();

			for (int i = 0; i < subjectsBooked.size(); i++) {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				 /*Added by Steffi
				  * String key1 = bean.getSapid()+bean.getSubject();
				    String key2 = bean.getSapid()+bean.getExamDate()+bean.getExamTime();
				 if(!subjectDoubleBookingMap.containsKey(key1) && !subjectDoubleBookingMap.containsKey(key2)){
					 subjectDoubleBookingMap.put(key1, bean);
					 subjectDoubleBookingMap.put(key2, bean);
					 subjectBookingMap.put(bean.getSubject(), bean);
				 }else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error");
						return modelnView;
				 }*/
				subjectBookingMap.put(bean.getSubject(), bean);
			}

			for (int i = 0; i < subjectsBooked.size(); i++) {
				subjects.add(subjectsBooked.get(i).getSubject());
			}

			List<ExecutiveTimetableBean> timeTableList = dao.getTimetableForExecutiveExamGivenSubjects(subjects,student);

			ExecutiveHallTicketPDFCreator hallTicketCreator = new ExecutiveHallTicketPDFCreator();
			
			fileName = hallTicketCreator.createHallTicket(timeTableList, subjectsBooked, getProgramMap(), student, 
						HALLTICKET_PATH, getMostRecentTimetablePeriod(),getExamCenterCenterDetailsMap(false), subjectBookingMap, STUDENT_PHOTOS_PATH);
			
			dao.saveHallTicketDownloaded(userDownloadingHallTicket, subjectsBooked);
			String filePathToBeServed = HALLTICKET_PATH + fileName;
			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="+fileName); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
			inputStream.close();

		}catch(Exception e){
			
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Generating Hall Ticket: "+sapid, e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Hall Ticket.");
		}

		return modelnView;

	}
	
	
	
	
	@RequestMapping(value = "/downloadExecutiveHallTicketForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String downloadHallTicketForm(HttpServletRequest request, HttpServletResponse response) {
		return  "downloadExecutiveHallTicket";

	}

}
