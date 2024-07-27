package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.daos.SessionQueryAnswerDAO;

@Controller
public class CacheController {


	@Autowired
	ApplicationContext act;
	
	@Autowired
	SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	/*@Autowired
	DashboardDAO dashboardDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	@Autowired
	ExamCenterDAO examCenterDAO;
	
	@Autowired
	FacultyDAO facultyDAO;
	
	@Autowired
	PassFailDAO passFailDAO;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	ReportsDAO reportsDAO;
	
	@Autowired
	ResitExamBookingDAO resitExamBookingDAO;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;*/
	
	
	//Make an entry in this method for every DAO that is made
	@RequestMapping(value = "/admin/refreshCache", method = {RequestMethod.GET})
	public ModelAndView refreshCache(HttpServletRequest request, HttpServletResponse respnse) {
		
		sessionQueryAnswerDAO.refreshLiveFlagSettings();
		
		/*assignmentsDAO.refreshLiveFlagSettings();
		dashboardDAO.refreshLiveFlagSettings();
		examBookingDAO.refreshLiveFlagSettings();
		examCenterDAO.refreshLiveFlagSettings();
		facultyDAO.refreshLiveFlagSettings();
		passFailDAO.refreshLiveFlagSettings();
		projectSubmissionDAO.refreshLiveFlagSettings();
		reportsDAO.refreshLiveFlagSettings();
		resitExamBookingDAO.refreshLiveFlagSettings();
		studentMarksDAO.refreshLiveFlagSettings();*/
		
		return null;
	}


}
