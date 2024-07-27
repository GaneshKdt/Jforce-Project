package com.nmims.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.nmims.beans.AboutCS;
import com.nmims.beans.ActivationInfo;
import com.nmims.beans.EventScheduleStatusBean;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.VideoContentCareerservicesBean;
import com.nmims.beans.VideoContentTypes;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.daos.LoginDAO;
import com.nmims.daos.VideoRecordingDao;
import com.nmims.daos.WebinarSchedulerDAO;
import com.nmims.helpers.DataValidationHelpers;

@Controller
public class CSPortalBaseController {

	
	@Autowired
	LoginDAO loginDAO;

	@Autowired
	private WebinarSchedulerDAO webinarSchedulerDAO;

	@Autowired
	private VideoRecordingDao videoRecordingDao;
	
	public StudentCareerservicesBean getStudent(String sapid) {
		return loginDAO.getSingleStudentsData(sapid);
	}

	@Autowired
	public EntitlementActivationDAO entitlementActivationDAO;

	private static final Logger logger = LoggerFactory.getLogger(CSPortalBaseController.class);
 
	protected DataValidationHelpers dataHelpers = new DataValidationHelpers();
	Gson gson = new Gson();
	
	FeatureTypes featureTypes = new FeatureTypes();
	
	public EventScheduleStatusBean getActiveAndUpcoming(List<SessionDayTimeBean> allEvents) {

		EventScheduleStatusBean events = new EventScheduleStatusBean();
		
		List<SessionDayTimeBean> activeEvents = new ArrayList<SessionDayTimeBean>();
		List<SessionDayTimeBean> upcomingEvents = new ArrayList<SessionDayTimeBean>();
		
		for(SessionDayTimeBean event: allEvents) {
			String sessionDate = event.getDate();
			String sessionTime = event.getEndTime();
			
			String sessionDateString = sessionDate + " " + sessionTime;
			Date sessionStartDateTime = new Date();
			try {
				sessionStartDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDateString);
			} catch (ParseException e) {
				logger.info("in CSPortalBaseController class got exception : "+e.getMessage());
			}
			if(!dataHelpers.checkIfDateBeforeCurrent(sessionStartDateTime)) {
				//this event hasnt happened yet. add it to upcoming
				if(dataHelpers.checkIfeventActive(event)) {
					activeEvents.add(event);
				}else {
					upcomingEvents.add(event);
				}
			}
		}
		events.setActiveEvents(activeEvents);
		events.setUpcomingEvents(upcomingEvents);
		
		return events;
	}
	
	
	public boolean checkCSAccess(StudentCareerservicesBean student) {
		
		if(student == null) {
			return false;
		}
		
		if(student.isPurchasedOtherPackages()) {
			return true;
		}
		return false;
	}


	
	public AboutCS getAboutCS() {
		AboutCS about = new AboutCS();
		
		about.setText("Career Services is a pioneer service by NGA-SCE. It is built with an aim to tap into the potential students & alumni carry and then delve into their strengths and weaknesses to unearth their innate skills which would then be matched with unique career opportunities.");
		return about;
	}
	
	public boolean checkCSAccess(HttpServletRequest request) {
		
		StudentCareerservicesBean student = (StudentCareerservicesBean) request.getSession().getAttribute("student_careerservices");
		if(student == null) {
			return false;
		}
		if(student.isPurchasedOtherPackages()) {
			return true;
		}
		return false;
	}
	

	/*
	 * 	get all career forum activation data	
	 */
		public ActivationInfo getActivationInfo(String sapid, int featureId) {
			ActivationInfo modelBean = new ActivationInfo();
			StudentEntitlement studentEntitlement = entitlementActivationDAO.getApplicableEntitlementForPurchase(sapid, featureId);
			//calculate the total activations left 
			if(studentEntitlement == null) {
				return null;
			}
			if(entitlementActivationDAO.checkIfDependenciesFulfilled(studentEntitlement, sapid, featureId)) {
				int activationsLeft = studentEntitlement.getActivationsLeft();
				int activationsPossible = entitlementActivationDAO.activationsCurrentlyPossible(studentEntitlement);
				Date nextActivationDate = entitlementActivationDAO.nextActivationAvailableDate(studentEntitlement);
				int totalActivations = studentEntitlement.getEntitlementInfo().getTotalActivations();
				modelBean.setActivationsLeft(activationsLeft);
				modelBean.setTotalActivations(totalActivations);
				modelBean.setNextActivationAvailableDate(nextActivationDate);
				modelBean.setActivationsPossible(activationsPossible);
				modelBean.setFeatureName(studentEntitlement.getFeatureName());
				modelBean.setPackageName(studentEntitlement.getPackageName());
				modelBean.setReceiptId(studentEntitlement.getPurchaseId());
				modelBean.setPackageStartDate(studentEntitlement.getPackageStartDate());
				modelBean.setPackageEndDate(studentEntitlement.getPackageEndDate());
			}else {
				int activationsLeft = studentEntitlement.getActivationsLeft();
				int activationsPossible = 0;
				int totalActivations = studentEntitlement.getEntitlementInfo().getTotalActivations();
				modelBean.setActivationsLeft(activationsLeft);
				modelBean.setTotalActivations(totalActivations);
				modelBean.setActivationsPossible(activationsPossible);
				modelBean.setFeatureName(studentEntitlement.getFeatureName());
				modelBean.setPackageName(studentEntitlement.getPackageName());
				modelBean.setReceiptId(studentEntitlement.getPurchaseId());
				modelBean.setPackageStartDate(studentEntitlement.getPackageStartDate());
				modelBean.setPackageEndDate(studentEntitlement.getPackageEndDate());
			}
			
			return modelBean;
		}

		public List<SessionDayTimeBean> getAllEvents(String sapid){
			
			List<SessionDayTimeBean> sessions = new ArrayList<>();
			
			try {
				sessions = webinarSchedulerDAO.getAllWebinars();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CSPortalBaseController class got exception : "+e.getMessage());
			}
			
			ActivationInfo activationInfo = getActivationInfo(sapid, FeatureTypes.CAREER_FORUM);
		
			//Add video content to sessions that have it
				List<SessionDayTimeBean> sessionsToReturn = new ArrayList<SessionDayTimeBean>();
				
				List<VideoContentCareerservicesBean> allVideoContent = videoRecordingDao.getAllVideoContentByTypeId(VideoContentTypes.SESSION_VIDEO);
				for (SessionDayTimeBean sessionDayTimeBean : sessions) {
					String sessionId = sessionDayTimeBean.getId();
					sessionDayTimeBean.setActivationInfo(activationInfo);
					for(VideoContentCareerservicesBean videoContent: allVideoContent) {
						String videoContentSessionId = videoContent.getSessionId();
						if(videoContentSessionId != null) {
							if(sessionId.equals(videoContentSessionId)) {
								sessionDayTimeBean.setVideoContent(videoContent);
								sessionDayTimeBean.setHasVideoContent(true);
							}
						}
					}
					if(sessionDayTimeBean.getVideoContent() == null){
						sessionDayTimeBean.setHasVideoContent(false);
					}
					sessionsToReturn.add(sessionDayTimeBean);
				}
			// end
			return sessionsToReturn;
		}

		public boolean checkLogin(HttpServletRequest request) {

			String sapid = (String) request.getSession().getAttribute("userId");
			
			if(sapid != null) {
				if(request.getSession().getAttribute("student_careerservices") != null) {
					return true;
				}
			}
			return false;
		}
		

		public boolean checkLoginFacultyAndStudent(HttpServletRequest request) {

			String sapid = (String) request.getSession().getAttribute("userId");
			if(sapid != null) {
				return true;
			}
			return false;
		}
}
