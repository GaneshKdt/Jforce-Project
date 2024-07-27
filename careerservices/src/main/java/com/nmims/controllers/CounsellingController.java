package com.nmims.controllers;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.CounsellingBean;
import com.nmims.beans.CounsellingFeedbackBean;
import com.nmims.beans.ProgressDetailsBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.CounsellingDAO;
import com.nmims.daos.ProgressDetailsDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.ZoomManager;

@Controller
public class CounsellingController {

	@Autowired
	ProgressDetailsDAO progressDAO;
	
	@Autowired
	CounsellingDAO counsellingDAO;

	private static final Logger logger = LoggerFactory.getLogger(CounsellingController.class);
 
	public boolean checkLogin(HttpServletRequest request) {

		String sapid = (String) request.getSession().getAttribute("userId");
		if(sapid != null) {
			if(request.getSession().getAttribute("student_careerservices") != null) {
				return true;
			}
		}
		return false;
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

	@RequestMapping(value = "/uploadProgressDetailsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadProgressDetailsForm() {

		ModelAndView modelnView = new ModelAndView("uploadProgressDetails");
		ProgressDetailsBean progressBean = new ProgressDetailsBean();
		
		modelnView.addObject("progressBean", progressBean);
		modelnView.addObject("packages", progressDAO.getPackage());
		
		return modelnView;
		
	}
	
	@RequestMapping(value = "/uploadProgressDetails", method = { RequestMethod.POST })
	public ModelAndView uploadProgressDetails( ProgressDetailsBean progressBean ) {
		
		ModelAndView modelAndView = new ModelAndView("uploadProgressDetails");
		
		modelAndView.addObject("progressBean", progressBean);
		modelAndView.addObject("packages", progressDAO.getPackage());
		
		try{
			
			String featureId = progressBean.getFeatureId();
			String packageName = progressBean.getPackageName();
			int durationMax = progressBean.getDurationMax();
			String packageId = progressDAO.getPackageId(packageName, durationMax);
			String entitlementId = progressDAO.getEntitlementId(packageId, featureId);
			String errorMessage = "";

			progressBean.setEntitlementId(entitlementId);

			ArrayList<String> errorList = new ArrayList<String>();
			int count = 1;
			
			ExcelHelper excelHelper = new ExcelHelper();
			
			ArrayList<ArrayList<ProgressDetailsBean>> list = excelHelper.readProgressDetailsFromExcel(progressBean, featureId, packageId);
			
			ArrayList<ProgressDetailsBean> progress = list.get(0);
			
			ArrayList<ProgressDetailsBean> errorBeanList = list.get(1);
			
			if(errorBeanList.size() > 0){
				modelAndView.addObject("errorBeanList", errorBeanList);
				return modelAndView;
			}
			
			for(ProgressDetailsBean bean : progress) {
				try {
					progressDAO.updateProgress(bean);
				}catch (Exception e) {
					errorMessage = e.getMessage();
					errorList.add(count+"");
					count++;
				}
			}

			if(errorList.size() == 0){
				modelAndView.addObject("success",true);
				modelAndView.addObject("successMessage",progress.size() +" rows out of "+ progress.size()+" inserted successfully.");
			}else{
				modelAndView.addObject("error", true);
				modelAndView.addObject("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList+". "+errorMessage);
			}
			
		}catch(Exception e){
			
			logger.info("in CounsellingController class got exception : "+e.getMessage());
			modelAndView.addObject("error", true);
			modelAndView.addObject("errorMessage", "Error in inserting rows.");

		}

		return modelAndView;
	}
	
	@RequestMapping(value = "/uploadCounsellingDetailsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadCounsellingDetailsForm( ) {

		ModelAndView modelAndView = new ModelAndView("uploadCounsellingDetails");
		CounsellingBean counsellingBean = new CounsellingBean();
		
		modelAndView.addObject("counsellingBean", counsellingBean);
		
		return modelAndView;
	}


	@RequestMapping(value = "/uploadCounsellingDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadCounsellingDetails( CounsellingBean counsellingBean ){
		
		ModelAndView modelAndView = new ModelAndView("uploadCounsellingDetails");
		
		try{
			ExcelHelper excelHelper = new ExcelHelper();

			List<CounsellingBean> errorBeanList = new ArrayList<CounsellingBean>();
			List<CounsellingBean> facultyDatesList = new ArrayList<CounsellingBean>();
			List<CounsellingBean> errorList = new ArrayList<CounsellingBean>();
			List<CounsellingBean> successList = new ArrayList<CounsellingBean>();
			
			excelHelper.readCounsellingAvailabilityExcel(counsellingBean, counsellingDAO.getAllFaculties(), facultyDatesList, errorList);
			
			if(errorBeanList.size() > 0){
				modelAndView.addObject("errorBeanList", errorBeanList);
				return modelAndView;
			}
			
			for(CounsellingBean bean: facultyDatesList) {
				
				boolean facultyFree = counsellingDAO.checkFacultyFree(bean);
				boolean slotFree = counsellingDAO.checkFreeSlots(bean);
				
				if( facultyFree ) {
					if( slotFree ) {
						counsellingDAO.updateCounsellingDates(bean);
						successList.add(bean);
					}else {
						bean.setErrorMessage("Slots Clashing : There can not be more than 2 interviews at a given time.");
						bean.setErrorRecord(true);
						errorList.add(bean);
					}
				}else {
					bean.setErrorMessage("One faculty can only have one counselling at a time. ");
					bean.setErrorRecord(true);
					errorList.add(bean);
				}
			}
						
			modelAndView.addObject("successList", successList);
			
			if(errorList.size() > 0) {
				modelAndView.addObject("error", "true");
				modelAndView.addObject("errorMessage", "Error in inserting " + errorList.size() + " rows.");
				modelAndView.addObject("errorList", errorList);
			}
			
			if(successList.size() > 0){
				modelAndView.addObject("success", "true");
				modelAndView.addObject("successMessage", successList.size()+" rows were successfully inserted");
			}
			
		}catch(Exception e){
			logger.info("in CounsellingController class got exception : "+e.getMessage());
			modelAndView.addObject("error", "true");
			modelAndView.addObject("errorMessage", "Error in inserting rows.");
		}
		
		ArrayList<CounsellingBean> counsellings = new ArrayList<CounsellingBean>();
		
		try {
			counsellings = counsellingDAO.getAllCounselling();
		} catch (Exception e) {
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		modelAndView.addObject("counsellings", counsellings);
		
		return modelAndView;
	}

	@RequestMapping(value = "/careerCounselling", method = RequestMethod.GET)
	public String careerCounselling(HttpServletRequest request, Model model) {
		
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		
		if(!checkCSAccess(request)) {
			return "redirect:/showAllProducts";
		}
		
		String sapid = (String)request.getSession().getAttribute("userId");
		CounsellingBean interviewBean = new CounsellingBean();
		ArrayList<CounsellingBean> counsellings = new ArrayList<>();
		ArrayList<CounsellingBean> scheduledCounsellingForStudent = new ArrayList<CounsellingBean>();
		CounsellingBean studentPackageFeatureDetails = new CounsellingBean();
		
		try {
			counsellingDAO.setTerminated();
			counsellings = counsellingDAO.getAllCounselling();
			scheduledCounsellingForStudent = counsellingDAO.getScheduledCounsellingForStudent(sapid);
		}catch (Exception e) {
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}

		interviewBean.setSapid(sapid);
		interviewBean.setFeatureName("Career Counselling");
		
		try {
			studentPackageFeatureDetails = counsellingDAO.getStudentDetails(interviewBean);
		} catch (Exception e) {
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		
		String monthYear = new SimpleDateFormat("MMM YYYY").format( calendar.getTime() );
		String today = sdf.format( date );

		JSONArray jsonFormatedInterviewList = new JSONArray();
		String jsonFormatedInterviews = new Gson().toJson( counsellings );
		
		try {
			jsonFormatedInterviewList = new JSONArray( jsonFormatedInterviews );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		model.addAttribute("studentDetails", studentPackageFeatureDetails);		
		model.addAttribute("counsellingsList", jsonFormatedInterviewList);
		model.addAttribute("scheduledCounsellingForStudent",scheduledCounsellingForStudent);
		model.addAttribute("monthYear",monthYear);
		model.addAttribute("today",today);
		
		return "portal/counselling/counsellingHome";
		
	}

	@RequestMapping(value = "/m/scheduleCounsellingSession", method = RequestMethod.POST, 
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<CounsellingBean> scheduleCounsellingSession( @RequestBody CounsellingBean bean ) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		CounsellingBean counsellingDetails = new CounsellingBean();
		
		boolean isCounsellingSchedulePossible = false;
		
		try {
			isCounsellingSchedulePossible = counsellingDAO.checkIfCounsellingSchedulePossible(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}

		if( isCounsellingSchedulePossible ) {

			try {

				updateCounsellingDetails( bean );

				counsellingDetails = counsellingDAO.getCounsellingDetails( bean.getCounsellingId() );

				bean.setSuccessMessage("Your counselling for date "+ counsellingDetails.getDate() + " at "+ counsellingDetails.getStartTime() +
						" has been successfully scheduled.");

				bean.setStatus("success");
				bean.setErrorRecord(false);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CounsellingController class got exception : "+e.getMessage());
				bean.setStatus("failer");
				bean.setErrorRecord(true);
				bean.setErrorMessage("An error occured while scheduling your counselling : Error : <br>"+e.getMessage());
			}

		}else {

			bean.setStatus("failer");
			bean.setErrorRecord(true);
			bean.setErrorMessage( "Please note that you have already consumed your counselling slots." );

		}

		return new ResponseEntity<>(bean, headers, HttpStatus.OK);
		
	}

	private void updateCounsellingDetails( CounsellingBean bean ) throws Exception {
		
		CounsellingBean counsellingDetails = new CounsellingBean();
		ZoomManager zoom = new ZoomManager();
		
		String packageId = counsellingDAO.getPackageId( bean.getSapid() );
		String featureId = counsellingDAO.getFeatureId("Career Counselling");
		String entitlementId = counsellingDAO.getEntitlementId(packageId, featureId);
		String facultyId = counsellingDAO.getFacultyId( bean.getCounsellingId() );
		
		counsellingDetails = counsellingDAO.getCounsellingDetails( bean.getCounsellingId() );

		bean.setCounsellingId( bean.getCounsellingId() );
		bean.setDate( counsellingDetails.getDate() );
		bean.setStartTime( counsellingDetails.getStartTime() );
		bean.setEndTime( counsellingDetails.getEndTime() );
		bean.setFacultyId( facultyId );
		bean.setSapid( bean.getSapid() );
		bean.setFeatureId(featureId);
		bean.setPackageId(packageId);
		bean.setEntitlementId(entitlementId);
		bean.setActivationsLeft(counsellingDAO.getActivationsLeft( bean.getSapid(), entitlementId ));
		/*
		 * update host ID after testing complete
		 * */
		bean.setHostId("harsh.kumar.EXT@nmims.edu");
		bean.setHostPassword("");
		bean.setCreatedBy( bean.getSapid() );
		bean.setLastModifiedBy( bean.getSapid() );
		
		try {
			zoom.scheduleCounselling(bean);
			bean.setBookingStatus("B");
		} catch (IOException e) {
			logger.info("in CounsellingController class got exception : "+e.getMessage());
			bean.setBookingStatus("F");
		}
		
		try{
			counsellingDAO.setCounselling(bean);
		} catch (Exception e) {
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}

	}
	
	@RequestMapping(value = "/m/updateCounsellingAttendance", method = RequestMethod.POST, 
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<CounsellingBean> updateCounsellingAttendance( @RequestBody CounsellingBean bean ) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, 15);
		String activationDate = format.format(calendar.getTime());
		
		calendar.add(Calendar.DATE, 45);
		String endDate = format.format(calendar.getTime());
		
		String packageId = "";
		
		try {
			packageId = counsellingDAO.getPackageId( bean.getSapid() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		bean.setActivationDate(activationDate);
		bean.setEndDate(endDate);
		bean.setPackageId( packageId );
		
		try {
			counsellingDAO.updateCounsellingAttendance(bean);
			bean.setErrorRecord(false);
		}catch(Exception e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occurred");
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		return new ResponseEntity<>( bean, headers, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/m/cancelCounselling", method = RequestMethod.POST,
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<CounsellingBean> cancelCounselling( @RequestBody CounsellingBean bean ) {
		
		ZoomManager zoom = new ZoomManager();
		String packageId = "";
		String featureId = "";
		String entitlementId = "";

		//cancel the interview on zoom
		try {

			bean = counsellingDAO.getScheduledCounsellingDetails( bean.getCounsellingId() );
			
			try {
				packageId = counsellingDAO.getPackageId( bean.getSapid() );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CounsellingController class got exception : "+e.getMessage());
			}
			try {
				featureId = counsellingDAO.getFeatureId("Career Counselling");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CounsellingController class got exception : "+e.getMessage());
			}
			try {
				entitlementId = counsellingDAO.getEntitlementId(packageId, featureId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CounsellingController class got exception : "+e.getMessage());
			}
			
			zoom.cancelCounselling(bean);
			
			bean.setIsCancelled("Y"); 
			bean.setReasonForCancellation("Counselling cancellation");
			bean.setEntitlementId( entitlementId );

			try {
				counsellingDAO.updateCounsellingCancellation(bean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in CounsellingController class got exception : "+e.getMessage());
			}

			bean.setStatus("success");
			
		} catch (IOException e) {
			
			logger.info("in CounsellingController class got exception : "+e.getMessage());
			bean.setIsCancelled("N");
			bean.setStatus("failed");
			bean.setErrorMessage( e.getMessage() );
			
		}
		
		return ResponseEntity.ok(bean);
		
	}

	@RequestMapping(value = "/counsellingFeedbackDashboard", method = RequestMethod.GET)
	public ModelAndView feedback( HttpServletRequest request, @RequestParam String facultyId){
		
		ModelAndView modelAndView = new ModelAndView("feedbackDashboard");
		
		ArrayList<CounsellingBean> pendingFeedbacks = counsellingDAO.getPendingFeedback( facultyId );
		ArrayList<CounsellingBean> feedbacks = counsellingDAO.getFeedback( facultyId );
		
		modelAndView.addObject("pendingFeedbacks",pendingFeedbacks);
		modelAndView.addObject("feedbacks",feedbacks);
		modelAndView.addObject("type", "Career Counselling");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/counsellingFeedbackForm", method = RequestMethod.GET)
	public ModelAndView counsellingFeedbackForm( @RequestParam( required = false ) String counsellingId , HttpServletRequest request ) {
		
		ModelAndView modelAndView = new ModelAndView("portal/counselling/counsellingFeedback");
		CounsellingFeedbackBean counsellingFeedback = new CounsellingFeedbackBean();
		LinkedHashMap<String,String> feedbackParam = new LinkedHashMap<String,String>();     
		
		feedbackParam.put("preparedness", "Preparedness");  
		feedbackParam.put("communication","Communication & Confidence"); 
		feedbackParam.put("listeningSkills","Listening Skills"); 
		feedbackParam.put("bodyLanguage","Body Language"); 
		feedbackParam.put("clarityOfThought","Clarity of Thought"); 
		feedbackParam.put("connect","Connect/Engage"); 
		feedbackParam.put("examples","Examples"); 
		
		ArrayList<String> rating = new ArrayList<String>(
				Arrays.asList("1","2","3","4","5"));
		
		if( !StringUtils.isBlank( counsellingId ) )
			try {
				counsellingFeedback = counsellingDAO.getFeedbackForCounselling( counsellingId );
			} catch (Exception e) {
				logger.info("in CounsellingController class got exception : "+e.getMessage());
			}
		
		modelAndView.addObject( "counsellingFeedback", counsellingFeedback );
		modelAndView.addObject("feedbackParam", feedbackParam);
		modelAndView.addObject("rating",rating);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/counsellingFeedback", method = RequestMethod.POST)
	public ModelAndView counsellingFeedback(HttpServletRequest request, CounsellingFeedbackBean counsellingFeedbackBean) {
		
		String interviewId = request.getParameter("counsellingId");
		
		try {
			counsellingDAO.insertFeedback( counsellingFeedbackBean,interviewId );
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Your feedback has been successfully submitted");
		}catch(Exception e) {
			request.setAttribute("interviewId",interviewId);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "There was an error while submitting your feedback");
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		return feedback( request, counsellingFeedbackBean.getUserId() );
	}

	@RequestMapping(value = "/m/checkIfCounsellingFeedbackProvided", method = RequestMethod.POST, 
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<CounsellingBean> checkIfCounsellingFeedbackProvided(HttpServletRequest request,  @RequestBody CounsellingBean bean) {

		if( !counsellingDAO.checkIfCounsellingFeedbackProvided(bean) ) {
			bean.setStatus("failer");
			bean.setErrorRecord(true);
			bean.setErrorMessage("Feedback for the interview is yet to be evaluated, "
					+ "please wait until your counselling session is evaluated.");
		}else {
			bean.setStatus("success");
			bean.setErrorRecord(false);
		}
		return ResponseEntity.ok(bean);
	}

	@RequestMapping(value = "/studentCounsellingFeedback", method = RequestMethod.GET)
	public String studentFeedback(HttpServletRequest request, Model model, @RequestParam String counsellingId ) {
		
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		if(!checkCSAccess(request)) {
			return "redirect:showAllProducts"; 
		}

		CounsellingFeedbackBean bean = new CounsellingFeedbackBean();
		
		try {
			bean = counsellingDAO.getFeedbackForCounselling( counsellingId );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		String sapid = (String)request.getSession().getAttribute("userId");
		
		CounsellingBean counsellingBean = new CounsellingBean();
		CounsellingBean counsellingDetails = new CounsellingBean();
		CounsellingBean counselling = new CounsellingBean();
		
		try {
			counselling = counsellingDAO.getCounselling(sapid, counsellingId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		counsellingBean.setSapid(sapid);
		
		try {
			counsellingBean.setPackageId( counsellingDAO.getPackageId(sapid) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}
		
		try {
			counsellingDetails = counsellingDAO.getStudentDetails(counsellingBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in CounsellingController class got exception : "+e.getMessage());
		}

		model.addAttribute("counsellingDetails", counsellingDetails);
		model.addAttribute("feedback", bean);		
		model.addAttribute("counselling", counselling);	
		
		return "portal/counselling/studentFeedback";
	}
	
}
