package com.nmims.listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.daos.TCSApiDAO;
import com.nmims.helpers.TCSApis;
import com.nmims.services.MettlTeeMarksService;

@Configuration
@EnableScheduling
public class TeeAbsenteeListScheduler {
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	TCSApis tcsHelper;
	
	@Autowired
	TCSApiDAO tcsApiDAO;
	
	@Autowired
	MettlTeeMarksService mettlTeeMarksService;
	
//	@Scheduled(cron = "0 0 23 * * ?")
	public void runTeeAbsenteeListScheduler() {
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		
		
		try {
//			ExamOrderBean examOrderBean = new ExamOrderBean();
//			examOrderBean = tcsApiDAO.getCurrentExamdetails();
//		
//			SimpleDateFormat formatNew = new SimpleDateFormat("MM/dd/yyyy");
//			Date date = new Date();
//			String appStatus = "All";
//			String fromDate = formatNew.format(date);
//			String toDate = formatNew.format(date);
//			String month = examOrderBean.getMonth();
//			String year = examOrderBean.getYear();
//			ResponseBean responseBean = new ResponseBean();
//			responseBean = tcsHelper.executePullRequest(appStatus, fromDate, toDate, month, year);
//			if(responseBean.getStatus().equals("success")) {
//			}else {
//			}
			
			String examDate =null;
			String responseMessage = "";
//			responseMessage = mettlTeeMarksService.runMettlAbsentTeeListSchedular(examDate);
			
		}catch(Exception e) {
			
		}
		
	}
	
	
	@Scheduled(cron = "0 0 23 * * ?")
	public void runTeeRescheduleExambookingListScheduler() {
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		} 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar now = Calendar.getInstance();
	    now.add(Calendar.DATE,-2);
	    Date dateExamDate = now.getTime(); 
		String examDate = sdf.format(dateExamDate);	
		String responseMessage = "";
		responseMessage = mettlTeeMarksService.runTeeRescheduleExambookingListSchedulerService(examDate);
		
	}
	
}
