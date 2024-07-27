package com.nmims.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.services.MassUploadTrackingSRService;

@Component
public class MassUploadTrackingScheduler {
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	private static final Logger logger = LoggerFactory.getLogger(MassUploadTrackingScheduler.class);
	
	@Autowired
	MassUploadTrackingSRService massUploadTrackingSRService;
	
	//mail will be send at 7pm
	@Scheduled(cron="0 0 19 * * ?")
	public void notifyStudentForTrackingDetails() {
		
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		try {
			massUploadTrackingSRService.notifyStudentForTrackingDetails();
		} 
		catch (Exception e) {
			// TODO: handle exception
			logger.info("Error during notifyStudentForTrackingDetails due to  : "+e);
		}
	}
	
}
