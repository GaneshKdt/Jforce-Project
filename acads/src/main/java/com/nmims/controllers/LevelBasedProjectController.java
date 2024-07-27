package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.PersonAcads;
import com.nmims.beans.VivaSlotBookingBean;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.helpers.ZoomManager;

@Controller
@RequestMapping("/admin")
public class LevelBasedProjectController extends BaseController{
	
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO;
	
	
	@RequestMapping(value = "/viewVivaSlots", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewVivaSlots(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");
		PersonAcads user = (PersonAcads) request.getSession().getAttribute("user_acads");
		List<VivaSlotBookingBean> vivaSlotBookingBeanList = new ArrayList<VivaSlotBookingBean>();
		if(user != null && user.getRoles() != null && (user.getRoles().indexOf("Faculty") != -1)){
			vivaSlotBookingBeanList = levelBasedProjectDAO.getVivaSlotByFacultyId(userId);
		}
		ModelAndView mv = new ModelAndView("levelBasedProject/vivaSlotList");
		mv.addObject("vivaSlotBookingBeanList", vivaSlotBookingBeanList);
		return mv;
	}
	
	@RequestMapping(value = "/startMeeting", method = {RequestMethod.POST})
	public @ResponseBody HashMap<String, String> startMeeting(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> hashMapResponse = new HashMap<String, String>();
		if(request.getParameter("meetingkey") == null || request.getParameter("hostId") == null) {
			hashMapResponse.put("status", "error");
			return hashMapResponse;
		}
		ZoomManager zoomManager = new ZoomManager();
		String url = zoomManager.getStartWebinarLink(request.getParameter("meetingkey"), request.getParameter("hostId"));
		if("error".equalsIgnoreCase(url)) {
			hashMapResponse.put("status", "error");
		}else {
			hashMapResponse.put("status", "success");
			hashMapResponse.put("url", url);
		}
		return hashMapResponse;
	}
	
}
