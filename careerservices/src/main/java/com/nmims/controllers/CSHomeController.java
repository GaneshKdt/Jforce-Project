package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.PackageBean;
import com.nmims.beans.VideoContentCareerservicesBean;
import com.nmims.beans.VideoContentTypes;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.StudentEntitlements;
import com.nmims.beans.CSResponse;
import com.nmims.beans.InterviewBean;
import com.nmims.beans.TermsAndConditions;
import com.nmims.beans.CSHomeEntitlementInfo;
import com.nmims.beans.CSHomeModelBean;
import com.nmims.beans.CSHomePackageInfo;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.CounsellingDAO;
import com.nmims.daos.VideoRecordingDao;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.daos.EntitlementCheckerDAO;
import com.nmims.daos.InterviewDAO;
import com.nmims.helpers.DataValidationHelpers;

@Controller
public class CSHomeController extends CSPortalBaseController{

	
	@Autowired
	private EntitlementCheckerDAO entitlementCheckerDAO;

	@Autowired
	private EntitlementActivationDAO entitlementActivationDAO;
	
	@Autowired
	private VideoRecordingDao videoRecordingDao;
	
	@Autowired
	private CareerServicesDAO careerServicesDAO;

	@Autowired
	private CounsellingDAO counsellingDAO;
	
	@Autowired
	InterviewDAO interviewDAO;
	
	DataValidationHelpers dataHelpers = new DataValidationHelpers();
	private Gson gson = new Gson();

	private static final Logger logger = LoggerFactory.getLogger(CSHomeController.class);
 
	@RequestMapping(value = {"", "/", "/home", "/Home"}, method = RequestMethod.GET)
	public ModelAndView studentOverview( HttpServletRequest request ) {

		if(!checkLogin(request)) {
			return new ModelAndView("login");
		}
		if(!checkCSAccess(request)) {
			return new ModelAndView("login");
		}

		ModelAndView modelAndView = new ModelAndView("portal/dashboard/dashboard_home");
		
		Boolean isCounsellingActive = Boolean.FALSE;
		Boolean isPracticeInterviewActive = Boolean.FALSE;
		
		InterviewBean bean = new InterviewBean();
		String sapid = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapid);
		
		try {
			isCounsellingActive = counsellingDAO.checkIfCareerCounsellingActive(sapid) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CSHomeController class got exception : "+e.getMessage());
		}

		try {
			isPracticeInterviewActive = interviewDAO.checkIfPracticeInterviewActive(bean) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CSHomeController class got exception : "+e.getMessage());
		}
		
		request.setAttribute("packageId", careerServicesDAO.getPackageId( sapid ));
		
		modelAndView.addObject( "isPracticeInterviewActive", isPracticeInterviewActive );
		modelAndView.addObject( "isCounsellingActive", isCounsellingActive );

		return modelAndView;
	}	
	
	@RequestMapping(value = "/m/termsAndConditions", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
	public ResponseEntity<PackageBean> termsAndConditions(@RequestBody PackageBean bean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		try {
			bean = careerServicesDAO.getTermsAndConditionForPackage(bean);
		} catch (Exception e) {
			logger.info("in CSHomeController class got exception : "+e.getMessage());
		}
		
		return new ResponseEntity<>(bean, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/aboutCS", method = RequestMethod.GET)
	public ResponseEntity<String> aboutCS(Locale locale,HttpServletRequest request, Model model) {
		
		return ResponseEntity.ok(gson.toJson(getAboutCS()));
	}	
	
	@RequestMapping(value = "/m/getStudentDashboardInfo", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> getStudentDashboardInfo(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
		CSResponse csResponse = new CSResponse();
		
		if(!requestParams.containsKey("sapid")) {
			csResponse.setNotValidRequest();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		String sapid = requestParams.get("sapid");
		StudentCareerservicesBean student = getStudent(sapid);
		if(student == null) {
			csResponse.setInvalidSapid();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		if(!checkCSAccess(student)) {
			csResponse.setNoCSAccess();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		CSHomeModelBean modelBean = getEntitlementsInfo(sapid);
		
		modelBean.setOrientationVideos(getOrientationVideos());
		csResponse.setStatusSuccess();
		csResponse.setResponse(modelBean);
		
		String response = gson.toJson(csResponse);
		
		return ResponseEntity.ok(response);
	}
	
	private List<VideoContentCareerservicesBean> getOrientationVideos(){
		return videoRecordingDao.getAllVideoContentByTypeId(VideoContentTypes.ORIENTATION_VIDEO);
	}
	
	private CSHomeModelBean getEntitlementsInfo(String sapid) {
		
		CSHomeModelBean modelBean = new CSHomeModelBean();
		List<StudentEntitlements> studentEntitlements = entitlementCheckerDAO.getAllStudentEntitlements(sapid);
		
		List<CSHomePackageInfo> homePackageInfos = new ArrayList<CSHomePackageInfo>();
		
		for (StudentEntitlements studentEntitlement : studentEntitlements) {
			
			CSHomePackageInfo homePackageInfo = new CSHomePackageInfo();
			homePackageInfo.setDescription(studentEntitlement.getPackageDescription());
			homePackageInfo.setFamilyId(studentEntitlement.getFamilyId());
			homePackageInfo.setPackageName(studentEntitlement.getPackageName());
			homePackageInfo.setUpgradeAvailable(false);
			homePackageInfo.setUpgradeUrl("");
			homePackageInfo.setValidTo(studentEntitlement.getEndDate());
			homePackageInfo.setValidFrom(studentEntitlement.getStartDate());
			homePackageInfo.setAboutPackagePage("packageDetailsWebView?productId=" + studentEntitlement.getFamilyId());
			List<CSHomeEntitlementInfo> entitlementInfos = new ArrayList<CSHomeEntitlementInfo>();
			
			for (StudentEntitlement entitlement : studentEntitlement.getEntitlements()) {
				
				CSHomeEntitlementInfo info = new CSHomeEntitlementInfo();
//				activationsCurrentlyPossible
				info.setEntitlementName(entitlement.getFeatureName());
				info.setEntitlementType(Integer.toString(entitlement.getFeatureId()));
				
				if(entitlementActivationDAO.checkIfDependenciesFulfilled(entitlement, sapid, entitlement.getFeatureId())) {
					info.setSessionsActivationsAvailable(entitlementActivationDAO.activationsCurrentlyPossible(entitlement));
					info.setNextActivationAvailableDate(entitlementActivationDAO.nextActivationAvailableDate(entitlement));
				}
				info.setSessionsLeft(entitlement.getActivationsLeft());
				info.setTotalSessions(entitlement.getEntitlementInfo().getTotalActivations());
			
				entitlementInfos.add(info);
			}
			
			homePackageInfo.setEntitlementsInfo(entitlementInfos);
			
			homePackageInfos.add(homePackageInfo);
		}
		
		modelBean.setPackages(homePackageInfos);
		
		modelBean.setTermsAndConditions(new TermsAndConditions());
		
		return modelBean;
	}

		

	/*
	 * 	Upcoming events
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/m/getUpcomingEventsSchedule", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getUpcomingWebinarSchedule(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			//check if a sapid is entered and its not null
			CSResponse csResponse = new CSResponse();

			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			
			csResponse.setStatusSuccess();
			csResponse.setResponse(getActiveAndUpcoming(getAllEvents(sapid)));
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		
}
