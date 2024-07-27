package com.nmims.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.nmims.beans.CSAdminAuthorizationTypes;
import com.nmims.beans.CSResponse;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionsDAO;

@Controller
public class SpeakerAdminController extends CSAdminBaseController {


	@Autowired
	FacultyDAO facultyDAO;

	@Autowired
	SessionsDAO sessionsDAO;

	Gson gson = new Gson();
	
	@RequestMapping(value = "/m/addCSFaculty", method = RequestMethod.GET)
	public String csFaculties(Locale locale, HttpServletRequest request, Model model) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		model.addAttribute("title", "Add CS Faculty");
		model.addAttribute("tableTitle", "All CS Faculty");
		
		model.addAttribute("FacultiesInCS", facultyDAO.getAllSpeakerDetails());
		model.addAttribute("FacultiesNotInCS", facultyDAO.getAllFacultiesNotInCS());
		return "admin/session/CSFaculty";
	}
	
	@RequestMapping(value = "/addCSFaculty", method = RequestMethod.POST, consumes="application/json" , produces = "application/json")
	public ResponseEntity<String> addCSFaculty(Locale locale, HttpServletRequest request, Model model, @RequestBody Map<String, String> requestParams) {
		CSResponse csResponse = new CSResponse();

		if(requestParams.get("facultyId") == null) {

			csResponse.setStatusFailure();
			csResponse.setMessage("no faculty Id");
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		
		String facultyId = (String) requestParams.get("facultyId");
		if(facultyDAO.addFacultyForCS(facultyId)) {
			csResponse.setStatusSuccess();
		}else {
			csResponse.setStatusFailure();
		}
		
		return ResponseEntity.ok(gson.toJson(csResponse));
	}	
	
	@RequestMapping(value = "/updateCSFaculty", method = RequestMethod.GET)
	public String updateCSFaculty(Locale locale, HttpServletRequest request, Model model, @RequestParam("id") String facultyId) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		model.addAttribute("title", "Update CS Faculty");
		model.addAttribute("tableTitle", "All CS Faculty");
		
		model.addAttribute("FacultiesNotInCS", facultyDAO.getAllFacultiesNotInCS());

		model.addAttribute("FacultiesInCS", facultyDAO.getAllSpeakerDetails());

		model.addAttribute("SpeakerDetails", facultyDAO.getSpeakerDetails(facultyId));
		return "admin/session/CSFaculty";
	}

	@RequestMapping(value = "/m/updateCSFaculty", method = RequestMethod.POST, produces = "application/json", consumes="application/json")
	public ResponseEntity<String> updateCSFaculty(Locale locale, HttpServletRequest request, Model model, @RequestBody FacultyCareerservicesBean requestParams ) {
		CSResponse csResponse = new CSResponse();

		if(facultyDAO.updateCSFaculty(requestParams)) {
			csResponse.setStatusSuccess();
		}else {
			csResponse.setStatusFailure();
		}
		
		return ResponseEntity.ok(gson.toJson(csResponse));
	}	
	
	
	@RequestMapping(value = "/deleteCSFaculty", method = RequestMethod.GET)
	public String deleteCSFaculty(Locale locale, HttpServletRequest request, Model model, @RequestParam("id") String facultyId) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String userAuthorizationRoles = userAuthorization.getRoles();
		if(!checkAuthorization(getAuthorization(userAuthorizationRoles), CSAdminAuthorizationTypes.CSSessionsAdmin)) {
			return "redirect:../studentportal/home";
		}
		
		if(facultyDAO.deleteCSFaculty(facultyId)) {
			model.addAttribute("successMessage", "Successfully Removed Speaker from CS Speakers List");
			return "redirect: /addCSFaculty";
		}else {
			model.addAttribute("errorMessage", "Couldn't Remove Speaker from CS Speakers List");
			return "redirect: /addCSFaculty";
		}
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getFacultySessions", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> getFacultySessions(Locale locale, HttpServletRequest request, Model model, @RequestBody Map<String, String> requestParams) {
		CSResponse csResponse = new CSResponse();
		
		if(!requestParams.containsKey("facultyId")) {
			csResponse.setNotValidRequest();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		if( !(userAuthorization == null || userAuthorization.getRoles() == null )) {
			String userAuthorizationRoles = userAuthorization.getRoles();
			List<String> roles = getAuthorization(userAuthorizationRoles);
			
			if(		roles.contains(CSAdminAuthorizationTypes.CSPackagingAdmin) || 
					roles.contains(CSAdminAuthorizationTypes.CSSessionsAdmin) || 
					roles.contains(CSAdminAuthorizationTypes.CSSessionsSpeaker)|| 
					roles.contains(CSAdminAuthorizationTypes.CSAdmin)) {
				
				return ResponseEntity.ok(gson.toJson(sessionsDAO.findAllScheduledSessions()));
			}
		}
		String facultyId = requestParams.get("facultyId");
		return ResponseEntity.ok(gson.toJson(sessionsDAO.findScheduledSessionsByFacultyId(facultyId)));
	}
}
