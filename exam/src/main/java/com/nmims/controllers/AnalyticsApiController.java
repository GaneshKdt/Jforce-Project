package com.nmims.controllers;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.StudentsAnalyticsBean;
import com.nmims.daos.AnalyticsApiDAO;

@Controller
public class AnalyticsApiController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	private AnalyticsApiDAO analyticsApiDAO;

	@RequestMapping(value = "/studentsClearedApplicableSubjects", method =RequestMethod.GET)
	public ResponseEntity<List<StudentsAnalyticsBean>> studentsClearedApplicableSubjects(HttpServletRequest request, HttpServletResponse response){
		
		List<StudentsAnalyticsBean> data = analyticsApiDAO.studentsClearedApplicableSubjectsData();
		
		return new ResponseEntity<List<StudentsAnalyticsBean>>(data,HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/studentsAssignmentReport", method =RequestMethod.GET)
	public ResponseEntity<List<StudentsAnalyticsBean>> studentsAssignmentReport(HttpServletRequest request, HttpServletResponse response){
		
		List<StudentsAnalyticsBean> data = analyticsApiDAO.studentsAssignmentData();		
		
		return new ResponseEntity<List<StudentsAnalyticsBean>>(data,HttpStatus.OK);		
	}
}
