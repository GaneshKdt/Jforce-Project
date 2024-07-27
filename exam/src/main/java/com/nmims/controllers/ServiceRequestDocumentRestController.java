package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.services.ServiceRequestDocumentService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class ServiceRequestDocumentRestController {
	
	@Autowired
	private ServiceRequestDocumentService serviceRequestDocumentService;
	
	private static final Logger logger = LoggerFactory.getLogger("srDocument");
	
	
	
	@PostMapping(path = "/getServiceRequestDocuments") 
	public ResponseEntity<ServiceRequestDocumentBean> getServiceRequestDocuments(HttpServletRequest request,
			@RequestBody ServiceRequestBean bean)throws Exception 
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ServiceRequestDocumentBean response = new ServiceRequestDocumentBean();
		
		try {
			// Getting E-Bonafide Documents by sapid
			List<ServiceRequestBean> mySRDocuments = serviceRequestDocumentService.getMySRDocumentsFromSapId(bean.getSapid());
			response.setServiceRequestBean(mySRDocuments);
		} catch(Exception e) {
			response.setError(true);
			response.setErrorMessage("Something went wrong, please try later!");
			logger.error("Error in fetching E-BonaFide Documents : "+e);
			return new ResponseEntity<ServiceRequestDocumentBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);	
		}
		
		return new ResponseEntity<ServiceRequestDocumentBean>(response,headers, HttpStatus.OK);	
	}
}
