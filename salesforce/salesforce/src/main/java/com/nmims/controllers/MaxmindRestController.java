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
import com.nmims.beans.IpAddress;
import com.nmims.services.MaxmindService;
import com.nmims.dto.IpAddressDetails;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class MaxmindRestController {
	
	@Autowired
	MaxmindService maxmindService;
	
	private static final Logger logger = LoggerFactory.getLogger(MaxmindRestController.class);
	
	@PostMapping(value="/getIpDetails", consumes = "application/json", produces = "application/json")
	public ResponseEntity<IpAddressDetails> checkIpaddress(@RequestBody IpAddress bean)
	{	
        IpAddressDetails ipDetails =new IpAddressDetails();
		try
		{
			String ip=bean.getIp();
			ipDetails=maxmindService.getIpDetails(ip);
			return new ResponseEntity<>(ipDetails,HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.info("Exception in maxmindcontroller:"+e.getMessage());
			ipDetails.setErrorMessage(e.getMessage());
			ipDetails.setError("true");
			return new ResponseEntity<>(ipDetails,HttpStatus.BAD_REQUEST);
		}
	}
}
