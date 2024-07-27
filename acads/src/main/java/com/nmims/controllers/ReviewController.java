package com.nmims.controllers;

import java.sql.Timestamp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.SessionReviewBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.SessionReviewDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.helpers.WebExMeetingManager;



@Controller
public class ReviewController {
	

	@Autowired(required=false)
	ApplicationContext act;


	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;
	@Autowired
	private WebExMeetingManager webExManager;
	
	@Autowired
	private SalesforceHelper salesforceHelper;

	@Value( "${WEB_EX_API_URL}" )
	private String WEB_EX_API_URL;

	@Value( "${WEB_EX_LOGIN_API_URL}" )
	private String WEB_EX_LOGIN_API_URL;

	@Value( "${WEBEX_ID}" )
	private String WEBEX_ID;

	@Value( "${WEBEX_PASS}" )
	private String WEBEX_PASS;

	@Value( "${MAX_WEBEX_USERS}" )
	private int MAX_WEBEX_USERS2;
	private int MAX_WEBEX_USERS=2000;

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private int CURRENT_ACAD_YEAR;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;

	private ArrayList<String> facultyList = null;
	private ArrayList<String> sessionList = null;
	private ArrayList<EndPointBean> endPointList = null;
	private final int pageSize = 10;
	private static final Logger logger = LoggerFactory.getLogger(QueryAnswerController.class);
	
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList("Jan","Apr","Jul","Sep","Dec"));  
	
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2015","2016","2017","2018","2019","2020")); 
	private HashMap<String, ProgramSubjectMappingAcadsBean> subjectProgramMap = null;

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();



	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList(){
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = dao.getSubjectProgramList();
		return subjectProgramList;
	}
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public ArrayList<String> getFacultyList(){
		//if(this.facultyList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.facultyList = dao.getAllFaculties();
		//}
		return facultyList;
	}
	
	public ArrayList<String> getSessionList(){
		//if(this.facultyList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.sessionList = dao.getAllSessions();
		//}
		return sessionList;
	}
	
	public ArrayList<EndPointBean> getEndPointList(){
		if(this.endPointList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.endPointList = dao.getAllFacultyRoomEndPoints();
		}
		return endPointList;
	}
	
	
	@RequestMapping(value = "/admin/viewReviewForFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewReviewForFacultyForm(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView mav = new ModelAndView("viewReviewForFaculty");
		SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
		String action = (String)request.getParameter("action");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		List<SessionDayTimeAcadsBean> reviewListFromFacultyId = srDao.reviewListByFacultyId(userId,action);
		
		mav.addObject("rowCount", reviewListFromFacultyId.size());
		mav.addObject("action", action);
		mav.addObject("reviewFacultyList",reviewListFromFacultyId);
		return mav;
		
	}
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	@RequestMapping(value="/admin/reviewFacultyForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView reviewFacultyForm(@RequestParam String reviewId,@RequestParam String action){
		SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
		ModelAndView mav = new ModelAndView("reviewFaculty");
		SessionReviewBean reviewBean = srDao.findSessionReviewById(reviewId);
		mav.addObject("reviewId", reviewId);
		mav.addObject("action", action);
		mav.addObject("reviewBean",reviewBean);
		return mav;
	}
	
	/*stef commented on Sep-2017
	 * 
	 * @RequestMapping(value="/searchFacultyReviewForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchFacultyReviewForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mav = new ModelAndView("searchFacultyReview");
		mav.addObject("reviewBean",new SessionReviewBean());
		return mav;
	}*/
	
	/*stef added on Sep*/	
	
	@RequestMapping(value = "/admin/searchFacultyReviewForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyReviewForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mav = new ModelAndView("searchFacultyReview");
		mav.addObject("reviewBean",new SessionReviewBean());
		mav.addObject("yearList", ACAD_YEAR_LIST);
		mav.addObject("monthList", monthList);
		mav.addObject("subjectList", getSubjectList());
		return mav;
	}
	
	@RequestMapping(value="/admin/searchFacultyReview",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchFacultyReview(HttpServletRequest request, HttpServletResponse response,@ModelAttribute SessionReviewBean reviewBean){
		SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
		ModelAndView mav = new ModelAndView("searchFacultyReview");
		mav.addObject("reviewBean",reviewBean);
		mav.addObject("yearList", ACAD_YEAR_LIST);
		mav.addObject("monthList", monthList);
		mav.addObject("subjectList", getSubjectList());
		try{
			List<SessionReviewBean> reviewListBasedOnCriteria = srDao.reviewListBasedOnCriteria(reviewBean);
			
			mav.addObject("reviewBean",reviewBean);
			mav.addObject("rowCount",reviewListBasedOnCriteria.size());
			
			if(reviewListBasedOnCriteria.isEmpty())
			{
				setError(request, "No Record Found");
			}
			request.getSession().setAttribute("reviewListBasedOnCriteria", reviewListBasedOnCriteria);
			
			
		double q5Average = 0.0;
		double q6Average = 0.0;
		

		if(reviewListBasedOnCriteria != null && reviewListBasedOnCriteria.size() > 0){
			for (SessionReviewBean sessionReviewBean : reviewListBasedOnCriteria) {
				q5Average += Integer.parseInt(!StringUtils.isEmpty(sessionReviewBean.getQ5Response()) ? sessionReviewBean.getQ5Response() :"0");
				q6Average += Integer.parseInt(!StringUtils.isEmpty(sessionReviewBean.getQ6Response()) ? sessionReviewBean.getQ6Response() :"0");
			}
			
			q5Average = q5Average / reviewListBasedOnCriteria.size();
			q6Average = q6Average / reviewListBasedOnCriteria.size();
		
			mav.addObject("q5Average",q5Average);
			mav.addObject("q6Average",q6Average);
			
		}
		
		}catch(Exception e){
			  
			setError(request, "Error Occurse While retriving Details "+e.getMessage());
			return searchFacultyReviewForm(request,response);
		}
		return mav;
	}
	/*---------------------------*/
	
	/*
	 * stef commented on Sep-2017
	 * 
	 @RequestMapping(value = "/downloadSessionFacultyReviews", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadSessionFacultyReviews(HttpServletRequest request, HttpServletResponse response) {
		List<SessionReviewBean> reviewListBasedOnCriteria = (List<SessionReviewBean>)request.getSession().getAttribute("reviewListBasedOnCriteria");
		TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("facultyIdAndFacultyBeanMap", tDao.getAllFacultyMapper());
		return new ModelAndView("SessionFacultyReviewsExcelView","reviewListBasedOnCriteria",reviewListBasedOnCriteria);
	}*/
	
/*stef added on Sep*/	
  @RequestMapping(value = "/admin/downloadSessionFacultyReviews", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadSessionFacultyReviews(HttpServletRequest request, HttpServletResponse response,@ModelAttribute SessionReviewBean reviewBean) {
	  
	  SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
	  FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
	  
	  List<SessionReviewBean> reviewListBasedOnCriteria = srDao.reviewListBasedOnCriteria(reviewBean);
      ArrayList<FacultyAcadsBean> allFacultyRecords = fDao.getAllFacultyRecords();
      
	try{ 
		if(reviewListBasedOnCriteria.isEmpty())
		{
			setError(request, "No Record Found");
			return searchFacultyReviewForm(request,response);
		}
		request.getSession().setAttribute("facultyIdAndFacultyBeanMap", srDao.getAllFacultyMapper());
		request.getSession().setAttribute("reviewListBasedOnCriteria", reviewListBasedOnCriteria);
		request.getSession().setAttribute("allFacultyRecords", allFacultyRecords);
			
	}catch(Exception e){
		  
		setError(request, "Error Occurse While retriving Details "+e.getMessage());
		return searchFacultyReviewForm(request,response);
	}
	return new ModelAndView("SessionFacultyReviewsExcelView","reviewListBasedOnCriteria",reviewListBasedOnCriteria);
  }
	
/*--------------------------------------------------------*/
	@RequestMapping(value="/admin/saveReviewForFaculty",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView saveReviewForFaculty(HttpServletRequest request, HttpServletResponse response,@ModelAttribute SessionReviewBean reviewBean){
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		 SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
		try{
			reviewBean.setLastModifiedBy(userId);
			reviewBean.setReviewed(SessionReviewBean.REVIEWED);
			reviewBean.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		srDao.updateFacultyReview(reviewBean);
			setSuccess(request,"Review Saved Successfully");
			
		}catch(Exception e){
			  
			setError(request,"Error in saving Review");
		}
		
		return viewReviewForFacultyForm(request,response);
		
		
	}
	
	
	
	@RequestMapping(value = "/admin/uploadSessionReviewFacultyMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadSessionReviewFacultyMappingForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		FileAcadsBean fileBean = new FileAcadsBean();
		m.addAttribute("fileBean",fileBean);
		return "uploadSessionReviewFacultyMapping";
	}
	
	@RequestMapping(value = "/admin/uploadSessionReviewFacultyMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadSessionReviewFacultyMapping(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadSessionReviewFacultyMapping");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			 SessionReviewDAO srDao = (SessionReviewDAO)act.getBean("sessionReviewDAO");
			 FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
			 
			 ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readSessionReviewFacultyMappingExcel(fileBean, getFacultyList(), getSubjectList(), userId);
			List<SessionReviewBean> sessionReviewBeanForBatchInsert = new ArrayList<SessionReviewBean>();
			List<SessionReviewBean> sessionReviewFacultyMapingList = (ArrayList<SessionReviewBean>)resultList.get(0);
			List<SessionReviewBean> errorBeanList = (ArrayList<SessionReviewBean>)resultList.get(1);
			Set<String> setOfSubject = new HashSet<String>();
			
			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				request.setAttribute("error", "true");
				return modelnView;
			}
			
			Map<String,String> mapOfSubjectNamesAndReviewer = new HashMap<String,String>();
			for(SessionReviewBean sessionReview : sessionReviewFacultyMapingList){
				setOfSubject.add(sessionReview.getSubject().trim());
				mapOfSubjectNamesAndReviewer.put(sessionReview.getSubject().trim(), sessionReview.getReviewerFacultyId());
			}
			
			HashMap<String,SessionDayTimeAcadsBean> mapOfIdAndSessionBean = srDao.mapOfSessionIdAndSessionBeanFromGivenSubjectList(setOfSubject,CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR);
			
			for(String sessionId : mapOfIdAndSessionBean.keySet()){
				
				if(mapOfIdAndSessionBean.get(sessionId)!=null){
					String subjectName = mapOfIdAndSessionBean.get(sessionId).getSubject();
					
					String reviewerFacultyId = mapOfSubjectNamesAndReviewer.get(subjectName.trim());
					
					SessionDayTimeAcadsBean sessionDayTime = mapOfIdAndSessionBean.get(sessionId);
					if(!StringUtils.isBlank(sessionDayTime.getFacultyId())){
						SessionReviewBean reviewBean1 = new SessionReviewBean();
						reviewBean1.setSessionId(sessionId);
						reviewBean1.setReviewerFacultyId(reviewerFacultyId);
						reviewBean1.setReviewed(SessionReviewBean.NOT_REVIEWED);
						reviewBean1.setFacultyId(sessionDayTime.getFacultyId());
						reviewBean1.setLastModifiedBy(userId);
						reviewBean1.setCreatedBy(userId);
						sessionReviewBeanForBatchInsert.add(reviewBean1);
					}
					if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId())){
						SessionReviewBean reviewBean2 = new SessionReviewBean();
						reviewBean2.setSessionId(sessionId);
						reviewBean2.setReviewerFacultyId(reviewerFacultyId);
						reviewBean2.setReviewed(SessionReviewBean.NOT_REVIEWED);
						reviewBean2.setFacultyId(sessionDayTime.getAltFacultyId());
						reviewBean2.setLastModifiedBy(userId);
						reviewBean2.setCreatedBy(userId);
						sessionReviewBeanForBatchInsert.add(reviewBean2);
					}
					if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId2())){
						SessionReviewBean reviewBean3 = new SessionReviewBean();
						reviewBean3.setSessionId(sessionId);
						reviewBean3.setReviewerFacultyId(reviewerFacultyId);
						reviewBean3.setReviewed(SessionReviewBean.NOT_REVIEWED);
						reviewBean3.setFacultyId(sessionDayTime.getAltFacultyId2());
						reviewBean3.setLastModifiedBy(userId);
						reviewBean3.setCreatedBy(userId);
						sessionReviewBeanForBatchInsert.add(reviewBean3);
					}
					if(!StringUtils.isBlank(sessionDayTime.getAltFacultyId3())){
						SessionReviewBean reviewBean4 = new SessionReviewBean();
						reviewBean4.setSessionId(sessionId);
						reviewBean4.setReviewerFacultyId(reviewerFacultyId);
						reviewBean4.setReviewed(SessionReviewBean.NOT_REVIEWED);
						reviewBean4.setFacultyId(sessionDayTime.getAltFacultyId3());
						reviewBean4.setLastModifiedBy(userId);
						reviewBean4.setCreatedBy(userId);
						sessionReviewBeanForBatchInsert.add(reviewBean4);
					}
				}
				
			}
						
			ArrayList<String> errorList = srDao.batchUpdateSessionReviewFacultyMapping(sessionReviewBeanForBatchInsert);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",sessionReviewFacultyMapingList.size() +" rows out of "+ sessionReviewFacultyMapingList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

		}

		return modelnView;
	}
}

	
	
	
