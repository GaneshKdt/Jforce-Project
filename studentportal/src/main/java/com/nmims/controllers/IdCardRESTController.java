package com.nmims.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.interfaces.IdCardServiceInterface;
import com.nmims.services.IdCardService;

@RestController
@RequestMapping("m")
public class IdCardRESTController {
	
	@Autowired
	IdCardServiceInterface idCardService;
	
	@PostMapping("/generateIdCard")
	public HashMap<String, String> createIdCard(@RequestBody StudentStudentPortalBean studentBean) {
		studentBean.setCreatedBy("System");
		HashMap<String, String> response=idCardService.createIdCardPdf(studentBean);
		return response;
	}
	
	@PostMapping("/generateIdCardByBatchJob")
	public String generateIdCardBatchJob(@RequestBody StudentStudentPortalBean studentBean) {
		String returnMessage=idCardService.createIdCardByBatchJob(studentBean.getEnrollmentMonth(), studentBean.getEnrollmentYear());
		return returnMessage;
	}
	
	@PostMapping("/generateIdCardBySapId")
	public String generateIdCardBySapId(@RequestBody StudentStudentPortalBean studentBean) {
		String returnMessage=idCardService.createIdCardBySapId(studentBean.getSapid());
		return returnMessage;
	}

}
