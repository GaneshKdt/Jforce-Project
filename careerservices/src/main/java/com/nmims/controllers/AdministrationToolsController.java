package com.nmims.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.nmims.beans.CSAdminAuthorizationTypes;
import com.nmims.beans.SessionAttendanceFeedbackReportBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.PackageAdminDAO;
import com.nmims.daos.SessionFeedbackDAO;
import com.nmims.daos.SessionQueryAnswerDAO;

@Controller
public class AdministrationToolsController extends CSAdminBaseController {

	@Autowired
	PackageAdminDAO packageAdminDAO;
	
	@Autowired
	SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Autowired
	SessionFeedbackDAO sessionFeedbackDAO;
	
	Gson gson = new Gson();
	

	@RequestMapping(value = "/showAllStudentsAndPackages", method = RequestMethod.GET, produces = "application/json")
	public String showAllStudentsAndPackages(Locale locale, HttpServletRequest request, Model model) {
		
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSAdmin)) {
			return "redirect:../studentportal/home";
		}

		model.addAttribute("title", "CS Students");
		model.addAttribute("AllStudentData", packageAdminDAO.getAllStudentsWithProducts());
		
		return "admin/products/students_with_packages";
	}
	

	@RequestMapping(value = "/showAllSessionQueryStatus", method = RequestMethod.GET)
	public String showAllSessionQueryStatus(Locale locale, HttpServletRequest request, Model model){
		List<SessionQueryAnswerCareerservicesBean> allQueriesAnswersAndStatus = sessionQueryAnswerDAO.getAllQueries();

		model.addAttribute("title", "All CS Queries");
		model.addAttribute("AllQueries", allQueriesAnswersAndStatus);
		return "admin/session/allQueries";
	}
	

	@RequestMapping(value = "/showAllStudentFeedback", method = RequestMethod.GET)
	public String showAllStudentFeedback(Locale locale, HttpServletRequest request, Model model){
		List<SessionAttendanceFeedbackReportBean> allFeedbackAnswersAndStatus = sessionFeedbackDAO.getAllSessionAndFeedback();

		model.addAttribute("title", "All CS Session And Feedback");
		model.addAttribute("AllFeedback", allFeedbackAnswersAndStatus);
		return "admin/session/allFeedback";
	}
}
