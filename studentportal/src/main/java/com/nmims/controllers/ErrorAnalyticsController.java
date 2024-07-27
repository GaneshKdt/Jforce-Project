package com.nmims.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.AnalyticsObjectStudentPortal;
import com.nmims.daos.ErrorAnalyticsDAO;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class ErrorAnalyticsController {

	@Autowired
	ErrorAnalyticsDAO errorAnalyticsDAO;
	
	@RequestMapping( value = "/m/logError", method = { RequestMethod.POST } )
	public ResponseEntity<Map<String, String>> reportError( HttpServletRequest request, @RequestBody AnalyticsObjectStudentPortal analyticsObject ) throws Exception {
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		analyticsObject.setUserAgent(getUserAgent(request));
		analyticsObject.setIpAddress(getClientIp(request));
		
		Map<String, String> toReturn = new HashMap<String, String>();
		if( errorAnalyticsDAO.registerError(analyticsObject) ) {
			toReturn.put("status", "success");
		}else {
			toReturn.put("status", "failure");
		}
		
		return new ResponseEntity<Map<String, String>>(toReturn, headers,  HttpStatus.OK);
	}
	
	private String getUserAgent(HttpServletRequest request) {

		try {
			return request.getHeader("User-Agent");
		}catch (Exception e) {
			return "";
		}
	}
	
	private String getClientIp(HttpServletRequest request) {
		
		try {
			String remoteAddr = "";
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
			return remoteAddr;
		}catch (Exception e) {
			return "";
		}
	}
}