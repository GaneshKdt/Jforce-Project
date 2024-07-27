package com.nmims.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.AlmashinesBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.services.AlmashinesService;

@RestController
@RequestMapping("m")
public class AlmashinesRestController {
	
	@Autowired
	AlmashinesService almashinesService;
	
	@PostMapping(value = "/almashinesLogin") 
	public AlmashinesBean malmashinesLogin(@RequestBody StudentStudentPortalBean sb)  {
		AlmashinesBean response = new AlmashinesBean();
		try {
			response = almashinesService.almashinesLogin(sb);
			response.setStatus("success");
			response.setSuccessMessage("Success in login to almashine");
			return response;
		}  catch (Exception e) { 
			
			response.setStatus("error");
			response.setErrorMessage(e.getMessage());
			return response;
		}
	}
}
