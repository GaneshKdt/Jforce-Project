package com.nmims.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.helpers.MaxmindHelper;

@Controller
public class MaxmindScheduler {

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	MaxmindHelper maxmindhelper;
	
	private static final Logger logger = LoggerFactory.getLogger(MaxmindScheduler.class);
	
	@RequestMapping(value="/updateReader",method = { RequestMethod.GET, RequestMethod.POST })
	public void updateReaderCall()
	{
		logger.info("updateReader method called");
		updateReader();
	}
	
	@Scheduled(cron = "0 0 19 * * 2,5") //to run on Tuesday and Friday every week at 7 pm
	public void updateReaderScheduler()
	{
		logger.info("updateReader method called");
		updateReader();
	}
	
	public void updateReader()
	{
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT))
		{
			  logger.info("Not Running updateReader since it is not PROD ");
			  return; 
		}
		
		maxmindhelper.getUpdatedReader();
	}
	
}
