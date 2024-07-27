package com.nmims.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.dto.LoginSSODto;
import com.nmims.interfaces.LoginSSOInterface;
import com.nmims.repository.SSOLoaderRepositoryForRedis;

@RestController
public class SSOLoaderRedisController {
	
		@Autowired
	    SSOLoaderRepositoryForRedis repository;
	 
		@Autowired
		ApplicationContext act;
		
		@Autowired
		LoginSSOInterface loginSSO;
		
		private static final Logger redis_logger = LoggerFactory.getLogger("redis_loginsso");
		
		@RequestMapping(value = "/m/getStudentDataFromRedis", method = RequestMethod.GET)
		public LoginSSODto getDataByRedis(@RequestParam String userId)
		{
			LoginSSODto student = new LoginSSODto();
			try {
				student = loginSSO.getStudentData(userId);
						
			}catch(Exception e)
			{
				e.printStackTrace();
				student.setErrorMessage(e.getMessage());	
			}
			return student;
		}
	
		@RequestMapping(value = "/m/deleteStudentDataInRedis", method = RequestMethod.GET)
		public String deleteDataByRedis(@RequestParam String userId)
		{
			try {
				loginSSO.deleteStudentDataFromRedis(userId);
			}catch(Exception e)
			{
					return e.getMessage();
			}			
			return "success";
		}

}
