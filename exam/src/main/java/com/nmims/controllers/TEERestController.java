package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.Demoexam_keysBean;
import com.nmims.beans.Person;
import com.nmims.services.DemoExamServices;


@RestController
@RequestMapping("m")
public class TEERestController {
	
	
	@Autowired
	 DemoExamServices demoService;
	
	
	@PostMapping(path = "/viewModelQuestionForm", consumes = "application/json", produces = "application/json")   
	public ResponseEntity<HashMap<String, ArrayList<Demoexam_keysBean>>> viewMModelQuestionForm(HttpServletRequest request,
			@RequestBody Person input) throws Exception {
		
		//ModelAndView modelnView = new ModelAndView("viewModelQuestion");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ArrayList<Demoexam_keysBean> lstOfApplicableSubjects  =   demoService.retriveSubjects(input);
		
	
		HashMap<String, ArrayList<Demoexam_keysBean>> Response= new HashMap<>();
		Response.put("studentCourses", lstOfApplicableSubjects);
		
		return new ResponseEntity<HashMap<String, ArrayList<Demoexam_keysBean>>>(Response,headers, HttpStatus.OK);
			}

}
