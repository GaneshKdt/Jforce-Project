package com.nmims.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.dto.StudentCourseMappingDTO;
import com.nmims.helpers.MailSender;
import com.nmims.interfaces.StudentSubjectCourseInterface;

@Service("studentSubjectCourse")
public class StudentSubjectCourse implements StudentSubjectCourseInterface
{
	@Autowired
	MailSender mailer;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private static final Logger logger = LoggerFactory.getLogger("studentCourses");
		
	@Autowired
	StudentCourseMappingDao studentCoursedao;
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		return headers;
	}
	
	@Override
	public void insertIntoStudentSubjectCourse(String sapid) {
		try {
			HttpHeaders headers =  this.getHeaders();	
			RestTemplate restTemplate = new RestTemplate();
			 
			String url = SERVER_PATH+"studentportal/m/populateCurrentCourseOfStudent";
			LinkedMultiValueMap<String, Object> courseinput = new LinkedMultiValueMap<>();
			courseinput.add("sapid", sapid);
			courseinput.add("sfdcCall", true);
			
			HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(courseinput, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);

			if (!("200".equalsIgnoreCase(response.getStatusCode().toString()))) {
				logger.info("Error:- Getting response code other than 200 ( student course  portal ) for sapid "+sapid+" And Response :- "+response.toString());
				mailer.studentCourseMailTrace("Student Course Error: Getting error other than 200 for sapid "+sapid,response.toString());
			}
		}
		catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			logger.info("Error in calling api of student course in portal for sapid "+sapid,e);
			mailer.studentCourseMailTrace("Student Course Error: Calling RestTemplate Method for sapid "+sapid, stackTrace);
		}
	}
	
	@Override
	public void insertIntoElectiveSubjectInCourseTable(StudentCourseMappingDTO studentDetails) {
		// TODO Auto-generated method stub
		
		//Get the masterkey of the student
		String masterkey = studentCoursedao.getStudentMasterkey(studentDetails.getSapId());
		
		//Check if any other elective present in table or not of that sem
		ArrayList<String> pssId = studentCoursedao.checkAnyOtherElectiveInTable(studentDetails.getSapId(),masterkey, studentDetails.getSem());
		
		//Delete that old elective
		if(pssId.size() > 0) {
			studentCoursedao.deleteAnyOtherElectiveInTable(studentDetails.getSapId(), pssId);
		}
		//Get PssId of the subject
		HashMap<String,String> subjectlist = studentCoursedao.getsubjectsWithPssId(studentDetails.getElectiveSubjectType(),masterkey, studentDetails.getSem());
		
		//insert elective
		int insertcount = studentCoursedao.batchInsertStudentPssIdsMappings(subjectlist, studentDetails.getSapId(), studentDetails.getRegMonth(), studentDetails.getRegYear());
		
		if(insertcount <= 0) {
			logger.info("Error:- inserting in DB ( salesforce ) for sapid "+studentDetails.getSapId());
			
		}
	}
}
