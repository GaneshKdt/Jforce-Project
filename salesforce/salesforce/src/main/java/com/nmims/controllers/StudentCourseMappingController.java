package com.nmims.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nmims.dto.StudentCourseMappingDTO;
import com.nmims.helpers.MailSender;
import com.nmims.services.StudentSubjectCourse;

@Controller
public class StudentCourseMappingController
{
	@Autowired
	StudentSubjectCourse studentCourseService;
	
	@Autowired
	MailSender mailer;
	
	private static final Logger logger = LoggerFactory.getLogger("studentCourses");
	
	@PostMapping(path = "/updateElectiveSubjectsFromSFDC")
	public @ResponseBody HashMap<String, String> InsertElectiveStudentCourses(@RequestBody StudentCourseMappingDTO dtoObj) {
		HashMap<String, String> response = new HashMap<String, String>();
		try {
			if(StringUtils.isBlank(dtoObj.getSapId()) || StringUtils.isBlank(dtoObj.getSem()) || StringUtils.isBlank(dtoObj.getElectiveSubjectType()) || StringUtils.isBlank(dtoObj.getRegMonth())
					|| StringUtils.isBlank(dtoObj.getRegYear())) {
				logger.info("Error:- Payload is empty ( salesforce ) for Populating elective Subject "+dtoObj.toString());
				response.put("success", "false");
				response.put("message", "Payload is Empty.Please check Following parameters. Sapid:- "+dtoObj.getSapId()+", Sem :- "+dtoObj.getSem()+" , ElectiveSubjectType :- "+dtoObj.getElectiveSubjectType()+
				", RegMonth :- "+dtoObj.getRegMonth()+" , RegYear :- "+dtoObj.getRegYear());
				return response;
			}
			studentCourseService.insertIntoElectiveSubjectInCourseTable(dtoObj);
			
		}catch(Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			logger.info("Error while inserting StudentCourseMappingDTO "+dtoObj.toString(),e);
			mailer.studentCourseMailTrace("Student Course Error: Calling RestTemplate InsertElectiveStudentCourses Method for  "+dtoObj.toString(), stackTrace);
		}
		response.put("success", "true");
		response.put("message", "SuccessFully Added!");
		return response;
	}
}
