package com.nmims.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAScheduleInfoBean;
import com.nmims.services.MBAWXTeeService;

@RestController
@RequestMapping("m")
public class MBAWXTEERestController {
	
	private static final Logger logger = LoggerFactory.getLogger(MBAWXTEERestController.class);

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	MBAWXTeeService mbawxTeeService;
	
	
	@PostMapping(value = "/getAssessmentDetails")
	public ResponseEntity<MBAResponseBean> getAssessmentDetails(@RequestBody MBAScheduleInfoBean input) {
		MBAResponseBean response = mbawxTeeService.getAssessmentDetails(input);
		return new ResponseEntity<MBAResponseBean>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/sendExamJoinLinksToMbaWxStudents")
	public ResponseEntity<String> getAssesssmentDetails() {
		 mbawxTeeService.sendExamJoinLinksForMbaWxStudents();
		return new ResponseEntity<String>("Mail sent to mbawx student", HttpStatus.OK);
	}

}
