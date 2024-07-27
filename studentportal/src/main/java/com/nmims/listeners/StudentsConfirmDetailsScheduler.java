package com.nmims.listeners;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.daos.PortalDao;
import com.nmims.helpers.MailSender;

@Component("studentsConfirmDetailsScheduler")
public class StudentsConfirmDetailsScheduler {
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	 @Autowired
	 ApplicationContext act;
	 
	 @Autowired
	 MailSender mailSender;
	 @Scheduled(cron="0 0 1 */3 * ?")
	 //@Scheduled(cron="0 0 1 1 * ?")
	 //@Scheduled(fixedDelay=60*60*1000)
	 public void showStudentUpdateProfilePage(){
		System.out.println(" showStudentUpdateProfilePage FIRED ");
		// if("PROD".equals(ENVIRONMENT)){
			 try{
		 PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		 ArrayList<String> getStudentListForProfileUpdate = pDao.getStudentListForProfileUpdate();
		 pDao.updateConfirmDetailsFlag(getStudentListForProfileUpdate,mailSender);
			 }catch(Exception e){
				 mailSender.mailStackTrace("Error: showStudentUpdateProfilePage", e);
			 }
		// }
	 }
	 

}