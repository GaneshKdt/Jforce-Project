package com.nmims.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.TimeTableDAO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SupportController extends BaseController{

	@Autowired
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(SupportController.class);
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;
	
	@Autowired
	TimeTableDAO timeTableDAO;

	
	/*@Autowired
	ExamBookingDAO examBookingDAO;*/
	
	@RequestMapping(value = "/viewStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("support/studentDetails");
		String sapId = request.getParameter("sapId");
		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		
		StudentAcadsBean student = rDao.getSingleStudentsData(sapId);//Query single student data//
		
		modelnView.addObject("studentDetails", student);
		
		/*
		
		ArrayList<SessionDayTimeBean> studentSessionList = new ArrayList<>();
		ArrayList<String> subjects = timeTableDAO.getSubjectsForCurrentCycle(sapId);
		if(subjects != null && subjects.size() > 0){
			studentSessionList = timeTableDAO.getScheduledSessionForStudents(subjects);
		}else{
			setError(request, "No Registration found for current Acad Cycle OR Session Calendar Not live");
		}
		int sessionCount = studentSessionList != null ? studentSessionList.size() : 0;
		m.addAttribute("sessionCount", sessionCount);
		m.addAttribute("studentSessionList", studentSessionList);
		
		*/
		
		return modelnView;
	}
	
}

