package com.nmims.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.services.FreeCourseCertificateServiceLayer;

@RestController
@RequestMapping("m")
public class FreeCourseCertificateRestController {
	
	@Autowired
	ApplicationContext act;

	@Autowired
	FreeCourseCertificateServiceLayer certificateServiceLayer;
	
	@PostMapping(path = {"/generateCertificateForLead", "/genreateCertificateForLead"}, consumes ="application/json",produces = "application/json")
	public @ResponseBody Map<String, String> genreateCertificateForLead(@RequestBody Map<String, String> request){

		String leadId = request.get("leadId");
		String consumerProgramStructureId = request.get("consumerProgramStructureId");
		String certificateType = request.get("certificateType");
		return certificateServiceLayer.generateCertificateForLead(leadId, consumerProgramStructureId, certificateType);
	}

}
