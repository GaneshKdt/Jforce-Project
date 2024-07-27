package com.nmims.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletConfigAware;

import com.google.gson.JsonObject;
import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.daos.DemoExamDAO;
import com.nmims.helpers.MettlHelper;
import com.nmims.services.DemoExamServices;

@Service("demoExamScheduler")
public class DemoExamScheduler implements ApplicationContextAware, ServletConfigAware{
	
	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired
	@Qualifier("mbaWxMettlHelper")
	MettlHelper mettlHelper;
	
	@Autowired
	DemoExamServices demoExamService;
	
	public static final Logger demoExamMbaWXlogger = LoggerFactory.getLogger("demoExamCreationMbaWX");
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;

	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return act;
	}
	
	@Scheduled(fixedDelay=15*60*1000)
	public void markAttendedDemoExam(){
		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		DemoExamDAO demoExamDAO = (DemoExamDAO)act.getBean("demoExamDAO");
		ArrayList<DemoExamAttendanceBean> demoExamAttendanceBeanList = demoExamDAO.getPendingAttendanceData();
		for (DemoExamAttendanceBean demoExamAttendanceBean : demoExamAttendanceBeanList) {
			JsonObject jsonResponse = mettlHelper.getTestStatus(demoExamAttendanceBean.getAccessKey(), demoExamAttendanceBean.getEmailId());
			String status = "pending";
			String endTime = null;
			String markAttend = null;
			try {
				if(jsonResponse != null) {
					JsonObject candidateObject = jsonResponse.get("candidate").getAsJsonObject();
					if("SUCCESS".equalsIgnoreCase(jsonResponse.get("status").getAsString())) {
						if(candidateObject != null) {
							JsonObject testStatusObject = candidateObject.get("testStatus").getAsJsonObject();
							if(testStatusObject != null && "Completed".equalsIgnoreCase(testStatusObject.get("status").getAsString())) {
								status = "success";
								markAttend = "Y";
								try {
									SimpleDateFormat df1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
									SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
									Date dateobj = df1.parse(testStatusObject.get("endTime").getAsString());
									endTime = df.format(dateobj);
								}
								catch (Exception e) {
									// TODO: handle exception
								}
							}else {
								status = testStatusObject.get("status").getAsString();
							}
						}else {
							status = "candidateObject null";
						}
					}else {
						status = jsonResponse.get("status").getAsString();
					}
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				status = "Error: " + e.getMessage();
			}
			demoExamAttendanceBean.setStatus(status);
			demoExamAttendanceBean.setEndTime(endTime);
			demoExamAttendanceBean.setMarkAttend(markAttend);
			demoExamDAO.updateEndExamAttendanceByBatchJob(demoExamAttendanceBean);
		}
	}
	
	@Scheduled(fixedDelay=15*60*1000)
	public void markAttendedDemoExamMbaWX(){
		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		demoExamService.checkDemoExamAttemptMbaWx();
	}
}
