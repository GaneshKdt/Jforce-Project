package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.TotalSessionDetailsStudentBean;
import com.nmims.beans.TotalVideoDetailsStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.beans.VideoAndSessionAttendanceCountStudentBean;
import com.nmims.interfaces.MyAcitivityInterface;

@Controller
public class MyActivityController extends BaseController {
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String acadMonth;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String acadYear;
	
	@Autowired
	private MyAcitivityInterface myAcitivityInterface;
	
	private static final Logger logger = LoggerFactory.getLogger(MyActivityController.class);
	
	//--- Get My Activity Details ---//
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/myActivity", method = RequestMethod.GET)
	public ModelAndView myActivity(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelandview = new ModelAndView("templates/myActivity");
		
		String sapid = (String)request.getSession().getAttribute("userId");
		ArrayList<Integer> pss_id = (ArrayList<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal");
		double reg_order = (double)request.getSession().getAttribute("reg_order");
		double current_order = (double) request.getSession().getAttribute("current_order");
		String myActivityDetails = "";
		List<VideoAndSessionAttendanceCountStudentBean> sessionAttendanceCount = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
		String sessionAttendanceCountList = "";
		String trackDetailsList = "";
		TotalSessionDetailsStudentBean totalSessionDetailsList = new TotalSessionDetailsStudentBean();
		String videoDetailsCountList = "";
		TotalVideoDetailsStudentBean totalVideoDetailsList = new TotalVideoDetailsStudentBean();
		boolean currentCycleStudent = false;
		String currentAcadMonthYear = acadMonth+"-"+acadYear;
		List<Map<String, Object>> pdfReadDetailsList = new ArrayList<Map<String, Object>>();
		
		//Check current cycle student
		if(reg_order == current_order) {
			currentCycleStudent = true;
		}
		
		try {
			//Get my activity details from analytics database
			myActivityDetails = ObjectConverter.mapToJson(myAcitivityInterface.myActivity(sapid));
		}catch (Exception e) {
			logger.error("Failed to get my activity details. "+e.getMessage());
		}
		
		try {
			//Get session attendance count from analytics database
			sessionAttendanceCount = myAcitivityInterface.getSessionAttendanceCount(sapid);
			sessionAttendanceCountList = ObjectConverter.mapToJson(sessionAttendanceCount);
		}catch (Exception e) {
			logger.error("Failed to get video and session attendance count. "+e.getMessage());
		}
		
		//If session attendance count details is present
	    if(sessionAttendanceCount.size() > 0) {
	    	try {
		    	//Get track details from analytics database
		    	trackDetailsList = ObjectConverter.mapToJson(myAcitivityInterface.getTrackDetails());
		    }catch (Exception e) {
		    	logger.error("Failed to get track details. "+e.getMessage());
			}
	    }else {
	    	try {
				trackDetailsList = ObjectConverter.mapToJson(new ArrayList<TracksBean>());
			} catch (JsonProcessingException e) {}
	    }
	    
	    try {
	    	//Get total session details
	    	totalSessionDetailsList = myAcitivityInterface.getTotalSessionDetails(pss_id, sapid);
	    }catch (Exception e) {
	    	logger.error("Failed to get total session details. "+e.getMessage());
		}
	    
	    try {
	    	//Get total video details
	    	totalVideoDetailsList = myAcitivityInterface.getTotalVideoDetails(sapid);
	    	videoDetailsCountList = ObjectConverter.mapToJson(totalVideoDetailsList.getSubject_details());
	    }catch (Exception e) {
	    	logger.error("Failed to get total video details. "+e.getMessage());
		}
	    
	    try {
	    	//Get PDF counter details
	    	pdfReadDetailsList = myAcitivityInterface.getPdfReadDetailsBySapid(pss_id, sapid);
	    }catch (Exception e) {
	    	logger.error("Failed to get read page details. "+e.getMessage());
		}
		
	    modelandview.addObject("currentCycleStudent", currentCycleStudent);
	    modelandview.addObject("currentAcadMonthYear", currentAcadMonthYear);
		modelandview.addObject("myActivity", myActivityDetails);
		modelandview.addObject("sessionAttendanceCountList", sessionAttendanceCountList);
		modelandview.addObject("trackDetailsList", trackDetailsList);
		modelandview.addObject("totalSessionDetailsList", totalSessionDetailsList);
		modelandview.addObject("videoDetailsCountList", videoDetailsCountList);
		modelandview.addObject("totalVideoDetailsList", totalVideoDetailsList);
		modelandview.addObject("pdfReadDetailsList", pdfReadDetailsList);
		return modelandview;
	}
	
}