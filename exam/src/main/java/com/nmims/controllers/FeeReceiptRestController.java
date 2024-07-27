package com.nmims.controllers;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.dto.FeeReceiptDTO;
import com.nmims.interfaces.FeeReceiptInterface;

@RestController
@RequestMapping("m")
public class FeeReceiptRestController {
	
	@Autowired
	FeeReceiptInterface feeReceipt;
	
	private static final Logger logger = LoggerFactory.getLogger(FeeReceiptRestController.class);
	
	@RequestMapping(value = "/getAdmissionFeeReceipt",  method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<ArrayList<FeeReceiptDTO>> getCourseCoordinator(@RequestBody JSONObject inputJsonObj){
		ArrayList<FeeReceiptDTO> documents = new ArrayList<FeeReceiptDTO>();
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.add("Content-Type", "application/json");			
			String sapid = (String) inputJsonObj.get("sapid");
			
			documents  = feeReceipt.getAdmissionFeeReceiptFromSapId(sapid);
			}catch(Exception e) {
				logger.info(" Error in getting admission fee receipt for sapid "+inputJsonObj.toString(),e);
				return new ResponseEntity<ArrayList<FeeReceiptDTO>>(documents, headers, HttpStatus.INTERNAL_SERVER_ERROR);	
			}
			return new ResponseEntity<ArrayList<FeeReceiptDTO>>(documents, headers, HttpStatus.OK);		
		} 
}
