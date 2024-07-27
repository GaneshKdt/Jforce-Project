package com.nmims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.IdCardExamBean;
import com.nmims.interfaces.IdCardServiceInterface;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class IdCardRestController {
	
	@Autowired
	IdCardServiceInterface idCardService;
	
	
	@PostMapping("/getStudeIdCardDetails")
	public IdCardExamBean getIdCardDetails(@RequestBody IdCardExamBean idCardExamBean) {
		IdCardExamBean idCardBean=new IdCardExamBean();
		idCardBean=idCardService.getIdCardForStudent(idCardExamBean.getSapid());
		return idCardBean;
	}
	
	

}
