package com.nmims.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nmims.helpers.MailSender;
import com.nmims.services.StudentCourseMappingService;

@Controller
public class StudentCourseMappingController 
{
	@Autowired
	StudentCourseMappingService studentCourseService;
	
	private static final Logger logger = LoggerFactory.getLogger("studentCourses");
	
	@Autowired
	MailSender mailSender;
	
	/**
	 * To populate the single student course 
	 * @param month
	 * @param year
	 * @param false SFDC call ( Dont  fetch elective subject from sfdc) 
	 */
	@RequestMapping(value = "/m/populateCurrentCourseOfStudent", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void populateCourseOfStudent(HttpServletRequest request,@RequestParam(value="sapid") String sapid,@RequestParam(value="sfdcCall") boolean sfdcCall)
	{
		try {
			studentCourseService.insertInStudentCourseTable(sapid,sfdcCall);
			
		}catch(Exception e)
		{
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			logger.error("Method populateCourseOfStudent :-  Student - "+sapid+". Error in inserting Student in Courses :- "+e);
			mailSender.studentCourseMailTrace("Student Course Error: Inserting in Student Course table for sapid "+sapid,stackTrace);
		}
	}
	
	/**
	 * To populate the existing student of that month and year
	 * @param month
	 * @param year
	 * @param true SFDC call ( to fetch elective subject from sfdc) 
	 */
	@RequestMapping(value = "/m/populateStudentsCurrentCourse", method = RequestMethod.POST)
	public void populateStudentCourse(@RequestParam String month,@RequestParam String year)
	{
		try {
			studentCourseService.populateStudentCourseData(month,year,true);
		}catch(Exception e)
		{
			logger.error("Method populateStudentCourse :-  Error in populating Student in Courses :- "+e);
		}
		
	}
	
}
