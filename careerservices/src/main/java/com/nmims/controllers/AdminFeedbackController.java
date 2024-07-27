package com.nmims.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.nmims.beans.CSAdminAuthorizationTypes;
import com.nmims.beans.CSResponse;
import com.nmims.beans.SessionFeedbackQuestion;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.SessionFeedbackDAO;

@Controller
public class AdminFeedbackController extends CSAdminBaseController {

	@Autowired
	SessionFeedbackDAO sessionFeedbackDAO;
	
	Gson gson = new Gson();
	@RequestMapping(value = "/addFeedbackQuestion", method = RequestMethod.GET, produces = "application/json")
	public String addFeedbackQuestion(Locale locale, HttpServletRequest request, Model model) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		model.addAttribute("QuestionGroups", sessionFeedbackDAO.getFeedbackGroups());
		model.addAttribute("AllQuestions", sessionFeedbackDAO.getAllFeedbackQuestions());
		return "admin/feedback_questions";
	}	
	
	@RequestMapping(value = "/addFeedbackQuestion", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
	public ResponseEntity<String> addPackagePOST(Locale locale, Model model, @RequestBody SessionFeedbackQuestion requestParams ) {
		CSResponse returnStatus = new CSResponse();
		if(sessionFeedbackDAO.addFeedbackQuestion(requestParams)) {
			returnStatus.setStatusSuccess();
		}else {
			returnStatus.setStatusFailure();
		}
		return ResponseEntity.ok(gson.toJson(returnStatus));
	}	
	

	@RequestMapping(value = "/viewAllFeedback", method = RequestMethod.GET)
	public String viewAllFeedback(Locale locale, HttpServletRequest request, Model model) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		model.addAttribute("All Feedback", sessionFeedbackDAO.getAllFeedback());
		return "redirect:addFeedbackQuestion";
	}	

//	@RequestMapping(value = "/deleteFeedbackQuestion", method = RequestMethod.GET, produces = "application/json")
//	public String deleteFeedbackQuestion(Locale locale, Model model, @RequestParam("questionId") String questionId) {
//		
//		if(sessionFeedbackDAO.deleteFeedbackQuestion(questionId)) {
//			model.addAttribute("successMessage","Deleted Question");
//		}else {
//			model.addAttribute("errorMessage",""
//					+ "Error deleting Question" );
//		}
//		return "redirect:addFeedbackQuestion";
//	}	

	@RequestMapping(value = "/deleteFeedbackQuestion", method = RequestMethod.GET, produces = "application/json")
	public String deleteFeedbackQuestion(Locale locale, HttpServletRequest request, Model model, @RequestParam("questionId") String questionId) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		if(sessionFeedbackDAO.deleteFeedbackQuestion(questionId)) {
			model.addAttribute("successMessage","Deleted Question");
		}else {
			model.addAttribute("errorMessage",""
					+ "Error deleting Question" );
		}
		return "redirect:addFeedbackQuestion";
	}	
	
}
