package com.nmims.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.PersonStudentPortalBean;

@Controller
public class FacultyController {

	@Autowired
	ApplicationContext act;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/getFacultyDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<FacultyStudentPortalBean> getFacultyDetails(HttpServletRequest request, @RequestBody PersonStudentPortalBean input) {

		String userId = input.getUserId();
		FacultyStudentPortalBean response = new FacultyStudentPortalBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		try {
			response = pDao.getFacultyData(userId);
			ArrayList<String> subjectList = pDao.getSubjectList(userId);
			response.setSubjectList(subjectList);

		} catch (Exception e) {
			//e.printStackTrace();
		}

		return new ResponseEntity(response, HttpStatus.OK);

	}

}
