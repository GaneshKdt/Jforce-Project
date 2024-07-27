package com.nmims.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nmims.beans.MBAWXConfigurationBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXReRegistrationDAO;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class MBAWXReRegistrationRestController {
	
	@Autowired
	MBAWXReRegistrationDAO mbawxReRegistrationDAO;

	@PostMapping(path = "/checkIfReRegApplicableForStudent", produces = "application/json; charset=UTF-8", consumes = "application/json")
	public ResponseEntity<String> mbaCreateBookingRequest(HttpServletRequest request, Model model, @RequestBody StudentExamBean student) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		if(student.getSapid() == null) {
			response.put("status", "fail");
			response.put("message", "Empty or invalid student!");
		} else {
			boolean live = mbawxReRegistrationDAO.checkIfReRegistrationLiveForSapid(student.getSapid());
			if(live) {

				boolean applicableForStudent = mbawxReRegistrationDAO.checkIfReRegistrationApplicableForSapid(student.getSapid());
				if(applicableForStudent) {
					response.put("status", "success");
					MBAWXConfigurationBean configuration = mbawxReRegistrationDAO.getReRegistrationBeanForSapid(student.getSapid());
					response.put("configuration", configuration);
					response.put("url", "https://ngasce.secure.force.com/nmlogin_new?type=reregistration");
				} else {
					response.put("status", "fail");
					response.put("message", "Student cant apply for Re-Registration!");
				}
			} else {
				response.put("status", "fail");
				response.put("message", "Re-Resigtration not live!");
			}
		}
		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
	}

}
