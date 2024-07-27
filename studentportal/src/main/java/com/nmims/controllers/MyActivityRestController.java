package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.MyActivityResponseStudentBean;
import com.nmims.beans.StudentStudentPortalBean;
//bitbucket.org/ngasce/studentportal.git
import com.nmims.beans.TimeSpentStudentBean;
import com.nmims.beans.TotalSessionDetailsStudentBean;
import com.nmims.beans.TotalVideoDetailsStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.beans.VideoAndSessionAttendanceCountStudentBean;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.interfaces.MyAcitivityInterface;

@RestController
@RequestMapping(value = "/m")
public class MyActivityRestController {

	@Autowired
	RegistrationHelper registrationHelper;
	
	@Autowired
	private MyAcitivityInterface myAcitivityInterface;
	
	private static final Logger logger = LoggerFactory.getLogger(MyActivityRestController.class);
	
	//--- Get My Activity Details ---//
	@RequestMapping(value = "/myActivity", method = RequestMethod.POST)
	public ResponseEntity<MyActivityResponseStudentBean> myActivity(@RequestBody TimeSpentStudentBean timeSpentStudentBean){
		
		HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Content-Type", "application/json");
	    MyActivityResponseStudentBean response = new MyActivityResponseStudentBean();
	    HttpStatus status = null;
	    
	    Map<String, List<TimeSpentStudentBean>> myActivityDetails = new HashMap<String, List<TimeSpentStudentBean>>();
	    List<VideoAndSessionAttendanceCountStudentBean> sessionAttendanceCountList = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
	    List<HashMap<String, String>> trackDetailsList = new ArrayList<HashMap<String , String>>();
	    List<TotalSessionDetailsStudentBean> totalSessionDetails = new ArrayList<TotalSessionDetailsStudentBean>();
	    TotalSessionDetailsStudentBean totalSessionDetailsList = new TotalSessionDetailsStudentBean();
	    List<TotalVideoDetailsStudentBean> totalVideoDetails = new ArrayList<TotalVideoDetailsStudentBean>();
	    TotalVideoDetailsStudentBean totalVideoDetailsList = new TotalVideoDetailsStudentBean();
	    List<Map<String, Object>> pdfReadDetailsList = new ArrayList<Map<String,Object>>();
	    
	    //Check current cycle student
	    boolean currentCycleStudent= false;
	    try {
	    	StudentStudentPortalBean student = new StudentStudentPortalBean();
	    	StudentStudentPortalBean checkStudentRegistration = registrationHelper.checkStudentRegistration(timeSpentStudentBean.getSapid(), student);
		    
	    	if(checkStudentRegistration != null) {
		    	currentCycleStudent = true;
		    }
		    
	    	response.setCurrentCycleStudent(currentCycleStudent);
	    	status = HttpStatus.OK;
	    }catch (Exception e) {
	    	logger.error("Failed to get current cycle student details. "+e.getMessage());
	    	response.setStatus("Error");
	    	response.setMessage("Failed to get current cycle student details.");
	    	response.setCurrentCycleStudent(currentCycleStudent);
	    	status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    try {
	    	//Get my activity details from analytics database
	    	myActivityDetails = myAcitivityInterface.myActivity(timeSpentStudentBean.getSapid());
	    	
	    	response.setThis_week(myActivityDetails.get("This_Week"));
	    	response.setCurrent_month(myActivityDetails.get("Current_Month"));
	    	response.setLast_month(myActivityDetails.get("Last_Month"));
	    	status = HttpStatus.OK;
	    }catch (Exception e) {
	    	logger.error("Failed to get my activity details. "+e.getMessage());
	    	response.setStatus("Error");
	    	response.setMessage("Failed to get my activity details.");
	    	status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    try {
	    	//Get video and session attendance count from analytics database
	    	sessionAttendanceCountList = myAcitivityInterface.getSessionAttendanceCount(timeSpentStudentBean.getSapid());
	    	
	    	response.setSessionAttendanceCountList(sessionAttendanceCountList);
	    	status = HttpStatus.OK;
		}catch (Exception e) {
			logger.error("Failed to get video and session attendance count. "+e.getMessage());
			response.setStatus("Error");
	    	response.setMessage("Failed to get video and session attendance count.");
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    //If session attendance count details is present
	    if(sessionAttendanceCountList.size() > 0) {
	    	try {
	    		List<TracksBean> trackDetails = new ArrayList<TracksBean>();
	    		HashMap<String, String> trackData = new HashMap<String, String>();
	    		
		    	//Get track details from analytics database
		    	trackDetails = myAcitivityInterface.getTrackDetails();
		    	
		    	trackDetails.stream().forEach(list -> {
		    		trackData.put(list.getTrack(), list.getHexCode());
		    	});
		    	
		    	trackDetailsList.add(trackData);
		    	
		    	response.setTrackDetailsList(trackDetailsList);
		    	status = HttpStatus.OK;
		    }catch (Exception e) {
		    	logger.error("Failed to get track details. "+e.getMessage());
		    	response.setStatus("Error");
		    	response.setMessage("Failed to get track details.");
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
	    }
	    
	    try {
	    	//Get total session details
	    	totalSessionDetailsList = myAcitivityInterface.getTotalSessionDetails(timeSpentStudentBean.getPss_id(), timeSpentStudentBean.getSapid());
	    	totalSessionDetails.add(totalSessionDetailsList);
	    	
	    	response.setTotalSessionDetails(totalSessionDetails);
	    	status = HttpStatus.OK;
	    }catch (Exception e) {
	    	logger.error("Failed to get total session details. "+e.getMessage());
	    	response.setStatus("Error");
	    	response.setMessage("Failed to get total session details.");
	    	status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    try {
	    	//Get total video details
	    	totalVideoDetailsList = myAcitivityInterface.getTotalVideoDetails(timeSpentStudentBean.getSapid());
	    	totalVideoDetails.add(totalVideoDetailsList);
	    	
	    	response.setTotalVideoDetails(totalVideoDetails);
	    	status = HttpStatus.OK;
	    }catch (Exception e) {
	    	logger.error("Failed to get total video details. "+e.getMessage());
	    	response.setStatus("Error");
	    	response.setMessage("Failed to get total video details.");
	    	status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    try {
	    	//Get PDF counter details
	    	pdfReadDetailsList = myAcitivityInterface.getPdfReadDetailsBySapid(timeSpentStudentBean.getPss_id(), timeSpentStudentBean.getSapid());
	    	
	    	response.setPdfReadDetailsList(pdfReadDetailsList);
	    	status = HttpStatus.OK;
	    }catch (Exception e) {
	    	logger.error("Failed to get read page details. "+e.getMessage());
	    	response.setStatus("Error");
	    	response.setMessage("Failed to get read page details.");
	    	status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
	    
	    if(status == HttpStatus.OK) {
	    	response.setStatus("Success");
	    	response.setMessage("Get my activity details successfully...!!!");
	    }
	    
	    return new ResponseEntity<MyActivityResponseStudentBean>(response, responseHeaders, status);
	}
	
}