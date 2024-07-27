package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;
import com.nmims.services.RescheduledCancelledSlotChangeReportService;

@Controller
public class RescheduledCancelledSlotChangeReportController {

	/*Variables*/
	@Autowired
	private RescheduledCancelledSlotChangeReportService service;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	
	
	
	/*Methods*/
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}

	}
	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
	
	
	/*Mappings*/
	@RequestMapping(value = "/admin/downloadSortedRLAndCLReport", method = RequestMethod.GET)
	public ModelAndView downloadBlockStudentsCenterReport(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		List<RescheduledCancelledSlotChangeReportBean> sortedRlAndClList = new ArrayList<>();
		
		try
		{
			sortedRlAndClList =  service.getAllReleasedAndCancelledList();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return new ModelAndView("rescheduledCancelledSlotChangeReport", "sortedRLAndCLReport",
				sortedRlAndClList);
	}
	
}
