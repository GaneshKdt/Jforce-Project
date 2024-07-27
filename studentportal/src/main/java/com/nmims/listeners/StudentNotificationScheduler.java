package com.nmims.listeners;


import java.util.ArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.EmailHelper;
@Component
public class StudentNotificationScheduler {
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentNotificationScheduler.class);
	
	 @Autowired
	 ApplicationContext act;
	 
	 @Autowired
	 EmailHelper emailHelper;
	 
	@Scheduled(cron="0 0 1 1 * ?")
	 public void notifyStudentForValidityExpire(){
//		System.out.println(" notifyStudentForValidityExpire FIRED ");
		 if("PROD".equals(ENVIRONMENT)){
			 
		 PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		 ArrayList<StudentStudentPortalBean> getStudentListForValidityExpiredNotificationList = pDao.getStudentListForValidityExpiredNotification();
		 
		 emailHelper.sendValidityNotificationToStudentList(getStudentListForValidityExpiredNotificationList);
		 logger.info("Validity Expired Student List : " + getStudentListForValidityExpiredNotificationList.size());
		 }
	 }
	
}
