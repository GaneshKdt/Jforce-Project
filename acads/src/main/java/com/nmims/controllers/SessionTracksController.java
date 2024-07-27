package com.nmims.controllers;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.SessionTrackBean;
import com.nmims.services.SessionTracksService;
@Controller
@RequestMapping("admin")
public class SessionTracksController {
	
	@Autowired
	SessionTracksService sessionTracksService;
			
	@RequestMapping(value = "/sessionTrackColorsForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String sessionTrackColorsForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		m.addAttribute("sessionTrack", new SessionTrackBean());
		m.addAttribute("trackDetails", sessionTracksService.getAllTracksDetails());
		return "sessionTrackColors";
	}
	
	@RequestMapping(value = "/updateSessionTrackColor", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateSessionTrackColor(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute SessionTrackBean trackBean) {
		ModelAndView modelnView = new ModelAndView("sessionTrackColors");
		try {
			sessionTracksService.updateSessionTrackColor(trackBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Updated track details successfully.");
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error while updating track details.");
		}
		modelnView.addObject("sessionTrack", new SessionTrackBean());
		modelnView.addObject("trackDetails", sessionTracksService.getAllTracksDetails());
		return modelnView;
	}
	
	@RequestMapping(value = "/setSessionTrackColor", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView setSessionTrackColor(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute SessionTrackBean trackBean) {
		ModelAndView modelnView = new ModelAndView("sessionTrackColors");
		try {
			sessionTracksService.insertSessionTrackColor(trackBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Inserted track details successfully.");
		}
		catch (DuplicateKeyException e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Already exist track details go to update.");
		}catch(Exception m) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Internal issue, please refresh page and try again!");
		}
		modelnView.addObject("sessionTrack", new SessionTrackBean());
		modelnView.addObject("trackDetails", sessionTracksService.getAllTracksDetails());
		return modelnView;
	}
	
	@RequestMapping(value = "/getTracksName", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ArrayList<String>> getTracksName(){
		try {
		return new ResponseEntity<ArrayList<String>>((ArrayList<String>) sessionTracksService.getTrackNames(),HttpStatus.OK);
		}catch(Exception e) {
		return new ResponseEntity<ArrayList<String>>(new ArrayList<String>() ,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}