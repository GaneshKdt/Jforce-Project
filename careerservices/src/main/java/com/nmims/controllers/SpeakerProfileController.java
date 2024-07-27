package com.nmims.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionsDAO;

@Controller
public class SpeakerProfileController extends CSPortalBaseController {

	@Autowired
	FacultyDAO facultyDAO;

	@Autowired
	SessionsDAO sessionsDAO;

	Gson gson = new Gson();
	
	@RequestMapping(value = "/speakerProfile", method = RequestMethod.GET)
	public String speakerProfile(Locale locale, HttpServletRequest request, Model model, @RequestParam("id") String facultyId) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		if(!checkCSAccess(request)) {
			return "redirect:/showAllProducts";
		}
		
		FacultyCareerservicesBean faculty = facultyDAO.getSpeakerDetails(facultyId);
		List<SessionDayTimeBean> sessionsBySpeaker = sessionsDAO.findScheduledSessionsByFacultyId(facultyId);
		model.addAttribute("Faculty", faculty);
		model.addAttribute("Sessions", sessionsBySpeaker);
		return "portal/career_forum/speakerProfile";
	}
	
//	speakerProfile
}
