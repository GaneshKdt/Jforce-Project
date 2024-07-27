package com.nmims.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nmims.beans.KnowlarityData;
import com.nmims.dto.KnowlarityInsertionResponse;
import com.nmims.services.KnowlarityService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class KnowlarityRestController {
	
	@Autowired
	KnowlarityService knowlarityService;
	
	private static final Logger logger = LoggerFactory.getLogger(KnowlarityRestController.class);
	
	@PostMapping(value="/insertKnowlarityData",consumes="application/json",produces="application/json")
	public ResponseEntity<KnowlarityInsertionResponse> insertKnowlarityData(@RequestBody KnowlarityData kd)
	{
		KnowlarityInsertionResponse response=new KnowlarityInsertionResponse();
		//String id=kd.getId();
		try
		{
			int flag=knowlarityService.insertData(kd);
			response.setStatus(Integer.toString(flag));
			//response.setId(id);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.info("Exception is:"+e.getMessage());
			response.setStatus("0");
			response.setErrorMessage(e.getMessage());
			//response.setId(id);
			return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
		}
	}
}
