package com.nmims.listeners;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.nmims.services.QnAOfLiveSessionsService;

@Configuration
@EnableScheduling
public class QnAScheduler{
	
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired
	QnAOfLiveSessionsService qnaService;
	
	@Scheduled(cron = "0 00 23 * *  ?  ")
	public void getQnAOfLiveSession() {
		
		if (!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
			System.out.println("Not running QnAScheduler since this is not tomcat4. This is " + SERVER);
			return;
		}
		
		try {
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String sessionDate = simpleDateFormat.format(new Date());
			qnaService.webinarQnAReport(sessionDate);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			  
		}
		
	}
}
