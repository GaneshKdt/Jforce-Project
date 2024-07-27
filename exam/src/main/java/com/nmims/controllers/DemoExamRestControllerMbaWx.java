package com.nmims.controllers;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.MbaWxDemoExamScheduleDetailBean;
import com.nmims.beans.MettlHookResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.DemoExamDAO;
import com.nmims.helpers.DemoExamHelper;
import com.nmims.services.DemoExamServices;

@RestController
@RequestMapping("m")
public class DemoExamRestControllerMbaWx {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	DemoExamHelper demoExamHelper; 
	
	@Autowired
	DemoExamDAO demoExamDAO;
	
	@Autowired
	DemoExamServices demoExamService;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	public static final Logger demoExamMbaWXlogger = LoggerFactory.getLogger("demoExamCreationMbaWX");
	
	@RequestMapping(value="/getDemoExamForMbaWx",method=RequestMethod.GET)
	public ModelAndView getDemoExamForMbaWx(@RequestParam String sapid) throws Exception{
		
		TreeMap<String, String> params = new TreeMap<String, String>();
		try {
			demoExamMbaWXlogger.info("Demo Exam launching for sapid:"+sapid);
			MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail = demoExamService.checkIfDemoExamKeyPresent(sapid);
			
			params.put("sapid", scheduleAndSapidDetail.getSapid());
			params.put("subject", scheduleAndSapidDetail.getScheduleName());
			params.put("accessUrl", scheduleAndSapidDetail.getScheduleAccessUrl());
			params.put("emailId", scheduleAndSapidDetail.getEmailId());
			params.put("firstname", scheduleAndSapidDetail.getFirstName());
			params.put("lastname", scheduleAndSapidDetail.getLastName());
			params.put("scheduleName", scheduleAndSapidDetail.getScheduleName());
			params.put("scheduleId",  scheduleAndSapidDetail.getScheduleId());
			params.put("error", "false");			
			
			demoExamMbaWXlogger.info("schedule and sapid details before attendance creation for sapid:"+sapid+":"+scheduleAndSapidDetail.toString());
			
			demoExamService.createAttendanceForDemoExamMBAWX(scheduleAndSapidDetail);
			
			demoExamMbaWXlogger.info("demo exam attendance created in db for sapid"+sapid+":"+scheduleAndSapidDetail.toString());
			
		}catch(Exception e) {
			//e.printStackTrace();
			demoExamMbaWXlogger.info("Exception:"+e);
			params.put("error", "true");
			params.put("errorMessage", e.getMessage());
		}
		
		String redirectUrl =  SERVER_PATH + "ltidemo/mettl_sso_mbawx_demo_student?joinKey=" + URLEncoder.encode(demoExamService.encryptParameters(params), "UTF-8");
		
		return new ModelAndView("redirect:"+redirectUrl);
	}
	
	@RequestMapping(value = "/startMettlExamDemoMbaWX", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> startMettlExamDemoMbaWx(@RequestBody MettlHookResponseBean mettlHookResponseBean){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        demoExamMbaWXlogger.info("startWebhook demo exam mbaWX: Student EmailId: " + mettlHookResponseBean.getEmail() + " | invitation key: " + mettlHookResponseBean.getInvitation_key());
        try {
	        DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
	        StudentExamBean studentBean = demoExamDAO.getStudentByEmailId(mettlHookResponseBean.getEmail());
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(studentBean.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamDAO.updateStartExamAttendanceMbaWX(demoExamAttendanceBean);
	        response.put("status", "success");
	        demoExamMbaWXlogger.info("---->>> startwebhook demo exam mbaWX: completed start request with success : " + mettlHookResponseBean.getEmail() + " | " + mettlHookResponseBean.getInvitation_key());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
			// TODO: handle exception
        	demoExamMbaWXlogger.info("Exception:"+e);
        	response.put("status", "error");
        	demoExamMbaWXlogger.info("-------->>>> startwebhook demo exam mbaWX: error in mettl webhook : " + e);
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
	

	@RequestMapping(value = "/endMettlExamDemoMbaWX", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> endMettlExamDemoMbaWX(@RequestBody MettlHookResponseBean mettlHookResponseBean){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        demoExamMbaWXlogger.info("EndWebhook demo exam mbaWX: Student EmailId: " + mettlHookResponseBean.getEmail() + " | invitation key: " + mettlHookResponseBean.getInvitation_key());
        try {
	        DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
	        StudentExamBean studentBean = demoExamDAO.getStudentByEmailId(mettlHookResponseBean.getEmail());
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(studentBean.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setMarkAttend("Y");
	        demoExamDAO.updateEndExamAttendanceMbaWX(demoExamAttendanceBean);
	        response.put("status", "success");
	        demoExamMbaWXlogger.info("---->>> endwebhook demo exam mbaWX: completed request with success : " + mettlHookResponseBean.getEmail() + " | " + mettlHookResponseBean.getInvitation_key());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
			// TODO: handle exception
        	demoExamMbaWXlogger.info("Exception:"+e);
        	response.put("status", "error");
        	demoExamMbaWXlogger.info("-------->>>> endwebhook demo exam mbaWX: error in mettl webhook : " + e);
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
	
	@RequestMapping(value="/getDemoExamAttemptStatusMbaWx",method= {RequestMethod.GET,RequestMethod.POST})
	public void getDemoExamForMbaWx() {
		demoExamService.checkDemoExamAttemptMbaWx();
	}
}
