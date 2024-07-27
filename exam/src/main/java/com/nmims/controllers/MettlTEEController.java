package com.nmims.controllers;

import java.text.ParseException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;	
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.MettlHookResponseBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.PGMettlResponseBean;
import com.nmims.beans.ResponseBean;

import com.nmims.daos.MettlTeeDAO;
import com.nmims.helpers.TNSMailSender;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.listeners.TEELinkScheduler;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class MettlTEEController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MettlTEEController.class);

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	MettlTeeDAO teeDao;

	@Autowired
	TeeSSOHelper teeSSOHelper;
	
	@Autowired
	TEELinkScheduler scheduler;
	
	@RequestMapping(value = "/student/viewAssessmentDetails", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ModelAndView getAllCenters(HttpServletRequest request, HttpServletResponse response, @RequestParam("showJoinLink") String showJoinLink, @ModelAttribute MettlSSOInfoBean input) {

		ModelAndView mv = new ModelAndView("mettl/viewExamInfo");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		try{
			logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails " +input);
			input.setSapid(sapid);
			if("true".equals(showJoinLink)) {
				try {
					 
					MettlSSOInfoBean studentBooking = teeDao.getScheduleInfoForStudent(input);
					mv.addObject("bookingInfo", studentBooking);
					mv.addObject("joinLink", teeSSOHelper.generateMettlLink(studentBooking));
					
				}catch (EmptyResultDataAccessException e) {
				
					MettlSSOInfoBean studentBooking = teeDao.getExamBookingForStudent(input);
					mv.addObject("bookingInfo", studentBooking);
					mv.addObject("joinLink", teeSSOHelper.generateMettlLink(studentBooking));
				}	 
			} else {
	
				MettlSSOInfoBean studentBooking = teeDao.getExamBookingInfoForStudent(input);
				MettlSSOInfoBean bean = teeDao.getExamCenterGoogleMapUrl(studentBooking.getCenterId(), input.getYear(), input.getMonth());

				studentBooking.setExamCenterName(bean.getExamCenterName());
				studentBooking.setGoogleMapUrl(bean.getGoogleMapUrl());
				mv.addObject("bookingInfo", studentBooking);
				
			}
			
			logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails " + input + " Success "+response);
		} catch(Exception e) {
			
			mv.addObject("bookingInfo", new MettlSSOInfoBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error fetching exam booking for the subject!<br>Please Contact Support Immediately.");
			// TODO: Send email to us about this
			
			logger.info("\n"+SERVER+": "+new Date()+" getAssessmentDetails "+ input + " Error : " +e.getMessage());
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/m/getExamCenterGoogleMapUrl", method = {RequestMethod.POST})
	public ResponseEntity<MettlSSOInfoBean> getExamCenterGoogleMapUrl(@RequestBody MettlSSOInfoBean input) {
		HttpHeaders headers = new HttpHeaders();
		MettlSSOInfoBean bean = new MettlSSOInfoBean();
		headers.add("Content-Type", "application/json");
		String googleMapUrl = teeDao.getExamCenterGoogleMapUrl(input.getExamCenterName(), input.getYear(), input.getMonth());
		bean.setGoogleMapUrl(googleMapUrl);
		return new ResponseEntity<MettlSSOInfoBean>(bean, headers,  HttpStatus.OK);
	}
	

	@RequestMapping(value = "/ssoTestLinkGen", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> ssoTestLinkGen(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		String sapid = (String) request.getParameter("sapid");
		List<MettlSSOInfoBean> bookings = teeDao.getTestExamBookingBean(sapid);
		
		String toReturn = "";
		for (MettlSSOInfoBean booking : bookings) {
			toReturn += "<br>";
			try {
				toReturn += ""
						+ "<a href='" + teeSSOHelper.generateMettlLink(booking) + "'> " 
							+ "Link(" + booking.getSubject()  + ")"
						+ "</a>";
			} catch (Exception e) {
				
				toReturn += "Error " + "(" + booking.getSubject()  + ")";
			}
		}
		return ResponseEntity.ok(toReturn);
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/m/getExamStatus", method = {RequestMethod.POST})
	public ResponseEntity<ResponseBean> getExamStatus(@RequestBody ExamBookingTransactionBean examBookingTransactionBean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		ResponseBean responseBean = new ResponseBean();
		
        try {
        	List<ExamBookingTransactionBean> examBookingTransactionBeanList  = teeDao.getExamStatus(examBookingTransactionBean);
        	responseBean.setExamBookingTransactionBeanList(examBookingTransactionBeanList);
        	responseBean.setCode(200);
	        return new ResponseEntity<ResponseBean>(responseBean, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	//e.printStackTrace();
        	responseBean.setCode(422);
        	responseBean.setMessage(e.getMessage());
	        return new ResponseEntity<ResponseBean>(responseBean, headers,  HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/startPortalExamProd", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> startPortalExamProd(@RequestBody MettlHookResponseBean mettlHookResponseBean) {
		/*
		 * This is a hook from mettl.
		 * After student starts an exam, mettl sends a response indicating when the student 
		 * 
		 */
//		String url = "https://ngasce-content.nmims.edu/exam/startMettlExamProd";
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        HashMap<String, String> response = new HashMap<String, String>();
//        RestTemplate restTemplate = new RestTemplate();
//        
//        
//        JsonObject request = new JsonObject();
//        request.addProperty("email", mettlHookResponseBean.getEmail());
//        request.addProperty("name", mettlHookResponseBean.getName());
//        request.addProperty("EVENT_TYPE", mettlHookResponseBean.getEVENT_TYPE());
//        request.addProperty("assessment_id", mettlHookResponseBean.getAssessment_id());
//        request.addProperty("context_data", mettlHookResponseBean.getContext_data());
//        request.addProperty("timestamp_GMT", mettlHookResponseBean.getTimestamp_GMT());
//        request.addProperty("source_app", mettlHookResponseBean.getSource_app());
//        request.addProperty("notification_url", mettlHookResponseBean.getNotification_url());
//        request.addProperty("invitation_key", mettlHookResponseBean.getInvitation_key());
//        request.addProperty("candidate_instance_id", mettlHookResponseBean.getCandidate_instance_id());
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
//        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//        System.out.println("------------->>>>>>>>>> in error ");
//        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
      	HashMap<String, String> response = new HashMap<String, String>();
		
      	System.out.println("---------->>>>>> mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail());
      	
        try {
        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
        	if(booking == null) {
        		booking = teeDao.getExamBookingByMettlResponseInfoHistory(mettlHookResponseBean);
        		if(booking == null) {
        			return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        		}
        	}
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(booking.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        teeDao.updateExamAttendanceStatus("Portal Start Test Hook", MettlHookResponseBean.TEST_STARTED_ON_PORTAL, booking,"",null);
	        response.put("status", "success");
			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	response.put("status", "error");
			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
	
	
	@RequestMapping(value = "/m/startMettlExamProd", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> startMettlTest(@RequestBody  Map<String,String> request) {
		/*
		 * This is a hook from mettl.
		 * After student starts an exam, mettl sends a response indicating when the student 
		 * 
		 */
//		String url = "https://ngasce-content.nmims.edu/exam/startMettlExamProd";
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        HashMap<String, String> response = new HashMap<String, String>();
//        RestTemplate restTemplate = new RestTemplate();
//        
//        
//        JsonObject request = new JsonObject();
//        request.addProperty("email", mettlHookResponseBean.getEmail());
//        request.addProperty("name", mettlHookResponseBean.getName());
//        request.addProperty("EVENT_TYPE", mettlHookResponseBean.getEVENT_TYPE());
//        request.addProperty("assessment_id", mettlHookResponseBean.getAssessment_id());
//        request.addProperty("context_data", mettlHookResponseBean.getContext_data());
//        request.addProperty("timestamp_GMT", mettlHookResponseBean.getTimestamp_GMT());
//        request.addProperty("source_app", mettlHookResponseBean.getSource_app());
//        request.addProperty("notification_url", mettlHookResponseBean.getNotification_url());
//        request.addProperty("invitation_key", mettlHookResponseBean.getInvitation_key());
//        request.addProperty("candidate_instance_id", mettlHookResponseBean.getCandidate_instance_id());
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
//        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//        System.out.println("------------->>>>>>>>>> in error ");
//        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		
		
		/*System.out.println("-------->>>>>> json response ------->>> " + request);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("status", "success");
		return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);*/ 
		MettlHookResponseBean mettlHookResponseBean = new MettlHookResponseBean();
		mettlHookResponseBean.setInvitation_key(request.get("invitation_key"));
		mettlHookResponseBean.setEmail(request.get("email"));
		mettlHookResponseBean.setName(request.get("name"));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
      	HashMap<String, String> response = new HashMap<String, String>();
		String data = "{";
		for(Entry<String, String> entry : request.entrySet()) {
			data = data + "'" +entry.getKey() + "':'" + entry.getValue() + "',";
		}
		data = data.substring(0,data.length()-1);
		data = data + "}";
      	logger.info("---------->>>>>> mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail());
      	
        try {
        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
        	if(booking == null) {
        		booking = teeDao.getExamBookingByMettlResponseInfoHistory(mettlHookResponseBean);
        		if(booking == null) {
        			return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        		}
        	}
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(booking.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        teeDao.updateExamAttendanceStatus("Mettl Start Test Hook", MettlHookResponseBean.TEST_STARTED_ON_METTL, booking,"",data);
	        response.put("status", "success");
			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	response.put("status", "error");
			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/m/endMettlExamProd", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> endMettlExamDemo(@RequestBody Map<String, String> request){
		
		 
//      String url = "https://ngasce-content.nmims.edu/exam/endMettlExamProd";
//		HttpHeaders headers = new HttpHeaders();
//      headers.add("Content-Type", "application/json"); 
//      HashMap<String, String> response = new HashMap<String, String>();
//      RestTemplate restTemplate = new RestTemplate();
//      
//      JsonObject request = new JsonObject();
//      request.addProperty("email", mettlHookResponseBean.getEmail());
//      request.addProperty("name", mettlHookResponseBean.getName());
//      request.addProperty("EVENT_TYPE", mettlHookResponseBean.getEVENT_TYPE());
//      request.addProperty("assessment_id", mettlHookResponseBean.getAssessment_id());
//      request.addProperty("context_data", mettlHookResponseBean.getContext_data());
//      request.addProperty("timestamp_GMT", mettlHookResponseBean.getTimestamp_GMT());
//      request.addProperty("source_app", mettlHookResponseBean.getSource_app());
//      request.addProperty("notification_url", mettlHookResponseBean.getNotification_url());
//      request.addProperty("invitation_key", mettlHookResponseBean.getInvitation_key());
//      request.addProperty("candidate_instance_id", mettlHookResponseBean.getCandidate_instance_id());
//      HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
//      restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//      System.out.println("------------->>>>>>>>>> in error ");
//      return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		
		MettlHookResponseBean mettlHookResponseBean = new MettlHookResponseBean();
		mettlHookResponseBean.setInvitation_key(request.get("invitation_key"));
		mettlHookResponseBean.setEmail(request.get("email"));
		mettlHookResponseBean.setName(request.get("name"));
		mettlHookResponseBean.setAssessment_id(request.get("assessment_id"));
		mettlHookResponseBean.setFinish_node(request.get("finish_mode"));
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
    	HashMap<String, String> response = new HashMap<String, String>();
    	String data = "{";
		for(Entry<String, String> entry : request.entrySet()) {
			data = data + "'" +entry.getKey() + "':'" + entry.getValue() + "',";
		}
		data = data.substring(0,data.length()-1);
		data = data + "}";
		logger.info("---------->>>>>> mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail());
		
      try {
      	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(booking.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        teeDao.updateEndExamAttendanceStatus("Mettl End Test Hook", MettlHookResponseBean.TEST_ENDED_ON_METTL, booking,mettlHookResponseBean.getFinish_node(),data);
	        response.put("status", "success");
			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
      }
      catch (Exception e) {
      	e.printStackTrace();
      	response.put("status", "error");
			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
      	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/m/resumeEnabledMettlExamProd", method = {RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> resumeEnabledMettlExamProd(@RequestBody Map<String, String> request){
		MettlHookResponseBean mettlHookResponseBean = new MettlHookResponseBean();
		mettlHookResponseBean.setInvitation_key(request.get("invitation_key"));
		mettlHookResponseBean.setEmail(request.get("email"));
	
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
      	HashMap<String, String> response = new HashMap<String, String>();
      	String data = "{";
		for(Entry<String, String> entry : request.entrySet()) {
			data = data + "'" +entry.getKey() + "':'" + entry.getValue() + "',";
		}
		data = data.substring(0,data.length()-1);
		data = data + "}";
      	System.out.println("---------->>>>>> mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail());
		
        try {
        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(booking.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        teeDao.updateResumeExamAttendanceStatus("Mettl Resume Test Hook", MettlHookResponseBean.TEST_ENDED_ON_METTL, booking,mettlHookResponseBean.getFinish_node(),data);
	        response.put("status", "success");
			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	response.put("status", "error");
			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/m/gradedNotificationMettlExamProd", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, Object>> gradedNotificationMettlExamProd(@RequestBody Map<String, Object> request){
		MettlHookResponseBean mettlHookResponseBean = new MettlHookResponseBean();
		mettlHookResponseBean.setInvitation_key(request.get("invitation_key").toString());
		mettlHookResponseBean.setEmail(request.get("email").toString());
	
		logger.info("---------->>>>>> call gradedNotificationMettlExamProd mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
      	HashMap<String, Object> response = new HashMap<String, Object>();
//      	String data = "{";
//		for(Entry<String, String> entry : request.entrySet()) {
//			data = data + "'" +entry.getKey() + "':'" + entry.getValue() + "',";
//		}
//		data = data.substring(0,data.length()-1);
//		data = data + "}";
      	String data = new Gson().toJson(request);
		logger.info("---------->>>>>> mettlHookResponseBean.getInvitation_key() : "+ mettlHookResponseBean.getInvitation_key() +" mettlHookResponseBean.getEmail() : " + mettlHookResponseBean.getEmail() 
		+" \n data: "+data);
		
        try {
        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	        Date dateobj = new Date();
	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
	        demoExamAttendanceBean.setSapid(booking.getSapid());
	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
	        teeDao.updateGradedExamAttendanceStatus(null, MettlHookResponseBean.TEST_ENDED_ON_METTL, booking,mettlHookResponseBean.getFinish_node(),data);
	        response.put("status", "success");
			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
	        return new ResponseEntity<HashMap<String, Object>>(response, headers,  HttpStatus.OK);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	response.put("status", "error");
			logger.error(SERVER+":  endMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
        	return new ResponseEntity<HashMap<String, Object>>(response, headers,  HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	@RequestMapping(value = "/m/mettlStatusDashboard", method = {RequestMethod.GET})
	public ModelAndView mettlStatusDashboard(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("mettlStatusDashboard");
		
//		String midTime = "15:00";
		
		Date dateobj = new Date();
		SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		Date currentHour = new Date();
		Date before2slotHour = null ;
		Date lastslotHour = null;
		try {
			currentHour = sdfHour.parse(sdfHour.format(dateobj));
			before2slotHour = sdfHour.parse("12:30");
			lastslotHour = sdfHour.parse("16:30");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String date = sdfDate.format(dateobj);
		String time;
		Map<String, String> countRequest = new HashMap<String, String>();
		String examDateTime;
		if(currentHour.after(lastslotHour)) {
			time = "17:00:00";
			examDateTime = date + " " + time;
		}else if(currentHour.after(before2slotHour)){
			time = "13:00:00";
			examDateTime = date + " " + time;
		}else{
			time = "09:00:00";
			examDateTime = date + " " + time;
		}
		countRequest.put("examDate", date);
		countRequest.put("examTime", time);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<String> entity = new HttpEntity<String>(new Gson().toJson(countRequest), headers);
		String portal_started = "";
		try {
			String countResponse = restTemplate.postForObject("https://studentzone-ngasce.nmims.edu/exam/m/examTestTakenStatus", entity, String.class);
	        JsonObject responseJsonObj = new JsonParser().parse(countResponse).getAsJsonObject();
	        portal_started = responseJsonObj.get("count").getAsString();	
		}catch (Exception e) {
			portal_started = e.getMessage();
		}
		String mettl_completed = teeDao.getExamStatusCount(examDateTime,"Mettl Completed");
		String mettl_started = teeDao.getExamStatusCount(examDateTime,"Mettl Started");
		String no_action = teeDao.getExamStatusCount(examDateTime,null);
		mv.addObject("portal_started", portal_started);
		mv.addObject("mettl_completed", mettl_completed);
		mv.addObject("no_action", no_action);
		mv.addObject("mettl_started", mettl_started);
		mv.addObject("date", date);
		mv.addObject("time", time);
		return mv;
	}
	
		
			
	

	/*@RequestMapping(value = "/m/startMettlExamProd", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> startMettlTest(@RequestBody MettlHookResponseBean mettlHookResponseBean) {
		 
		String url = "https://ngasce-content.nmims.edu/exam/m/startMettlExamProd";
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        RestTemplate restTemplate = new RestTemplate();
        
        
        JsonObject request = new JsonObject();
        request.addProperty("email", mettlHookResponseBean.getEmail());
        request.addProperty("name", mettlHookResponseBean.getName());
        request.addProperty("EVENT_TYPE", mettlHookResponseBean.getEVENT_TYPE());
        request.addProperty("assessment_id", mettlHookResponseBean.getAssessment_id());
        request.addProperty("context_data", mettlHookResponseBean.getContext_data());
        request.addProperty("timestamp_GMT", mettlHookResponseBean.getTimestamp_GMT());
        request.addProperty("source_app", mettlHookResponseBean.getSource_app());
        request.addProperty("notification_url", mettlHookResponseBean.getNotification_url());
        request.addProperty("invitation_key", mettlHookResponseBean.getInvitation_key());
        request.addProperty("candidate_instance_id", mettlHookResponseBean.getCandidate_instance_id());
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
        
//        try {
//        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
//	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
//	        Date dateobj = new Date();
//	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
//	        demoExamAttendanceBean.setSapid(booking.getSapid());
//	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
//	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
//	        teeDao.updateExamAttendanceStatus("Mettl Start Test Hook", MettlHookResponseBean.TEST_STARTED_ON_METTL, booking);
//	        response.put("status", "success");
//			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
//	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
//        }
//        catch (Exception e) {
//        	
//        	response.put("status", "error");
//			logger.info("\n"+SERVER+": "+new Date()+" startMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
//        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
//		}
	}*/
	
	/*@RequestMapping(value = "/m/endMettlExamProd", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> endMettlExamDemo(@RequestBody MettlHookResponseBean mettlHookResponseBean){
		
 
        String url = "https://ngasce-content.nmims.edu/exam/m/endMettlExamProd";
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        RestTemplate restTemplate = new RestTemplate();
        
        JsonObject request = new JsonObject();
        request.addProperty("email", mettlHookResponseBean.getEmail());
        request.addProperty("name", mettlHookResponseBean.getName());
        request.addProperty("EVENT_TYPE", mettlHookResponseBean.getEVENT_TYPE());
        request.addProperty("assessment_id", mettlHookResponseBean.getAssessment_id());
        request.addProperty("context_data", mettlHookResponseBean.getContext_data());
        request.addProperty("timestamp_GMT", mettlHookResponseBean.getTimestamp_GMT());
        request.addProperty("source_app", mettlHookResponseBean.getSource_app());
        request.addProperty("notification_url", mettlHookResponseBean.getNotification_url());
        request.addProperty("invitation_key", mettlHookResponseBean.getInvitation_key());
        request.addProperty("candidate_instance_id", mettlHookResponseBean.getCandidate_instance_id());
        request.addProperty("finish_node", mettlHookResponseBean.getFinish_node());
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
//        try {
//        	MettlSSOInfoBean booking = teeDao.getExamBookingByMettlResponseInfo(mettlHookResponseBean);
//	        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
//	        Date dateobj = new Date();
//	        DemoExamAttendanceBean demoExamAttendanceBean = new DemoExamAttendanceBean();
//	        demoExamAttendanceBean.setSapid(booking.getSapid());
//	        demoExamAttendanceBean.setAccessKey(mettlHookResponseBean.getInvitation_key());
//	        demoExamAttendanceBean.setStartedTime(df.format(dateobj));
//	        teeDao.updateExamAttendanceStatus("Mettl End Test Hook", MettlHookResponseBean.TEST_ENDED_ON_METTL, booking);
//	        response.put("status", "success");
//			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " success : " + booking.toString());
//	        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
//        }
//        catch (Exception e) {
//        	
//        	response.put("status", "error");
//			logger.info("\n"+SERVER+": "+new Date()+" endMettlExamProd "+ mettlHookResponseBean.toString() + " error : " + e.getMessage());
//        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
//		}
	}*/
	
	@RequestMapping(value = "/m/sendExamEmail", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> sendExamEmail(@RequestBody MettlSSOInfoBean input){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        MettlSSOInfoBean studentBooking;
        try {
			studentBooking = teeDao.getExamBookingForStudent(input);
        } catch (Exception e) {
        	
        	response.put("status", "fail");
			response.put("error", "Error getting booking info " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
		try {
			String joinLink = teeSSOHelper.generateMettlLink(studentBooking);
			studentBooking.setJoinURL(joinLink);
		} catch (Exception e) {
			
			studentBooking.setError("Error generating Join Link \n" + e.getMessage());
			response.put("status", "fail");
			response.put("error", "Error generating Link " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
		try {
			String formattedDateTime = teeSSOHelper.getFormattedDateTimeForEmail(studentBooking.getExamStartDateTime());
			studentBooking.setFormattedDateStringForEmail(formattedDateTime);
			teeSSOHelper.sendJoinMail(studentBooking);
			response.put("status", "success");
		}catch (Exception e) {
			
			response.put("status", "fail");
			response.put("error", "Error sending Mail " + e.getMessage());
        	return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
		}
		
        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/m/sendExamEmailTest", method = {RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>> sendExamEmailTest(){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        
        scheduler.sendInvitationLinksForUpcomingExams();
        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/m/getGDMettlData", method = {RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<PGMettlResponseBean> getGDMettlData(){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
		try {
			
	        MettlTeeDAO mettlTeeDAO = (MettlTeeDAO) act.getBean("mettlTeeDAO");
	        Date dateobj = new Date();
	        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	        String date = sdfDate.format(dateobj);
	        List<MettlSSOInfoBean> mettlSSOInfoBeans = mettlTeeDAO.getPGScheduleData(date + " 00:00:00",date + " 23:59:59");
	        PGMettlResponseBean response = new PGMettlResponseBean();
	        response.setStatus("success");
	        response.setData(mettlSSOInfoBeans);
	        return new ResponseEntity(response,headers,HttpStatus.OK);
		}
		catch (Exception e) {
			// TODO: handle exception
			PGMettlResponseBean response = new PGMettlResponseBean();
	        response.setStatus("fail");
	        return new ResponseEntity(response,headers,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/getGDMettlDatav2", method = {RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<PGMettlResponseBean> getGDMettlDatav2(HttpServletRequest request){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
		try {
	        MettlTeeDAO mettlTeeDAO = (MettlTeeDAO) act.getBean("mettlTeeDAO");
	        List<MettlSSOInfoBean> mettlSSOInfoBeans = mettlTeeDAO.getPGScheduleData(request.getParameter("fromDate"),request.getParameter("toDate"));
	        PGMettlResponseBean response = new PGMettlResponseBean();
	        response.setStatus("success");
	        response.setData(mettlSSOInfoBeans);
	        return new ResponseEntity(response,headers,HttpStatus.OK);
		}
		catch (Exception e) {
			// TODO: handle exception
			PGMettlResponseBean response = new PGMettlResponseBean();
	        response.setStatus("fail");
	        return new ResponseEntity(response,headers,HttpStatus.OK);
		}
	}
	@RequestMapping(value = "/m/sendDemoExamSecondCommunication", method = {RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>> sendDemoExamSecondCommunicationTest(){
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> response = new HashMap<String, String>();
        
        scheduler.sendDemoExamSecondCommunication();
        return new ResponseEntity<HashMap<String, String>>(response, headers,  HttpStatus.OK);
	}
}
