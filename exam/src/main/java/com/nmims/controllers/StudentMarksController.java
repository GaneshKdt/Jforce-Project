package com.nmims.controllers;



import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EMBABatchSubjectBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResultNotice;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.beans.TCSMarksBean;
import com.nmims.beans.TEEResultStudentDetailsBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.ResultNoticeDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.ResultsFromRedisHelper;
import com.nmims.helpers.TCSApis;
import com.nmims.interfaces.ABStudentRecordInterface;
import com.nmims.services.AbRecordsFactoryService;
import com.nmims.services.StudentMarksService;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

/**
 * Handles requests for the application home page. 
 */
@Controller
public class StudentMarksController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	LeadDAO leadDAO;

	@Autowired
	TCSApis tcsHelper;
	
	@Autowired
	StudentMarksService studentMarksService;

	@Autowired
	TCSApiDAO tcsDAO;
	
	@Autowired
	AbRecordsFactoryService absentRecordsFactoryService;
	/*
	@Value( "${SHOW_RESULTS_FROM_REDIS}" )
	String SHOW_RESULTS_FROM_REDIS;
	*/

	@Value( "${SERVER_PATH}" )
	String SERVER_PATH;
	
	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	/*
	 * @Value("#{'${STUDENT_TYPE_LIST}'.split(',')}") private List<String>
	 * STUDENT_TYPE_LIST;
	 */
	
	private final int pageSize = 20;

	private static final Logger logger = LoggerFactory.getLogger(StudentMarksController.class);
	//private static final Logger logger_temp = LoggerFactory.getLogger("slowMethods");
	private ArrayList<String> programList = null;

	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	
	@Value("#{'${SUBJECT_UNDER_PROJECT}'.split(',')}")
	private ArrayList<String> subjectsUnderProject;
	
	private ArrayList<String> semList = new ArrayList<String>(Arrays.asList("1","2","3","4")); 
	private ArrayList<String> subjectList = null; 
	private ArrayList<CenterExamBean> centers = null; 
	private HashMap<String, String> examCenterIdNameMap = null;
	HashMap<String, ExamCenterBean> examCenterIdCenterMap = null;
	TreeMap<String,String> offlineExamCenterMap = null;
	TreeMap<String,String> onlineExamCenterMap = null;
	HashMap<String, StudentExamBean>  sapIdStudentsMap = null;
	private ArrayList<String> resultNoticeTypeList = new ArrayList<String>(Arrays.asList( 
			"Final Result","Assignment")); 
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Apr","Jun","Dec","Jul")); 
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();	
		
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		sapIdStudentsMap = null;
		getStudentsMap();
		
		offlineExamCenterMap = null;
		getOfflineExamCenterMapForDropDown();
		
		centers = null;
		getCentersList();
		
		return null;
	}
	
	public ArrayList<CenterExamBean> getCentersList(){
		if(this.centers == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
		}
		return centers;
	}
	
	public HashMap<String, StudentExamBean> getStudentsMap(){
		if(this.sapIdStudentsMap == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.sapIdStudentsMap = dao.getAllStudents();
		}
		return sapIdStudentsMap;
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}
	
	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate){
		if(this.examCenterIdCenterMap == null || this.examCenterIdCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			this.examCenterIdCenterMap = dao.getExamCenterCenterDetailsMap(isCorporate);
		}
		return examCenterIdCenterMap;
	}
	
	public TreeMap<String, String> getOfflineExamCenterMapForDropDown(){
		if(this.offlineExamCenterMap == null || this.offlineExamCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(false);
			this.offlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if(!"Offline".equalsIgnoreCase(bean.getMode())){
					continue;
				}
				offlineExamCenterMap.put(centerId, bean.getExamCenterName()+","+bean.getCity()+" ("+bean.getMode()+")");
			}
		}
		return offlineExamCenterMap;
	}
	
	public TreeMap<String, String> getOnlineExamCenterMapForDropDown(boolean isCorporate){
		if(this.onlineExamCenterMap == null || this.onlineExamCenterMap.size() == 0){
			ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
			HashMap<String, ExamCenterBean> examCenterIdCenterMap = getExamCenterCenterDetailsMap(isCorporate);
			this.onlineExamCenterMap = new TreeMap<>();
			for (Map.Entry<String, ExamCenterBean> entry : examCenterIdCenterMap.entrySet()) {
				String centerId = entry.getKey();
				ExamCenterBean bean = entry.getValue();
				if(!"Online".equalsIgnoreCase(bean.getMode())){
					continue;
				}
				onlineExamCenterMap.put(centerId, bean.getExamCenterName()+","+bean.getCity()+" ("+bean.getMode()+")");
			}
		}
		return onlineExamCenterMap;
	}
	

	@RequestMapping(value = "/admin/addStudentMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String addStudentMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("studentMarks", marks);
		
		return "addStudentMarks";
	}
	
	@RequestMapping(value = "/admin/searchStudentMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchStudentMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("studentMarks", marks);
		
		return "searchStudentMarks";
	}
	
		
	@RequestMapping(value = "/admin/addStudentMarks", method = RequestMethod.POST)
	public ModelAndView addStudentMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("studentMarks");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		String userId = (String)request.getSession().getAttribute("userId");
		studentMarks.setCreatedBy(userId);
		studentMarks.setLastModifiedBy(userId);
		
		int id = dao.insertStudentMarks(studentMarks);
		studentMarks.setId(id);
		studentMarks = dao.findById(id+"");
		modelnView.addObject("studentMarks", studentMarks);
		request.getSession().setAttribute("studentMarks", studentMarks);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchStudentMarks", method = RequestMethod.POST)
	public ModelAndView searchStudentMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("searchStudentMarks");
		request.getSession().setAttribute("studentMarks", studentMarks);
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page =new Page<StudentMarksBean>();
		
		if(!StringUtils.isEmpty(studentMarks.getSubjectType()) && studentMarks.getSubjectType().equals("waivedIn")  ) {
		    List<StudentMarksBean>getWaivedInMarksList=studentMarksService.getStudentMarksList(studentMarks);
			modelnView.addObject("rowCount", getWaivedInMarksList.size());			
			page.setPageItems(getWaivedInMarksList);
		}else {
			page=dao.getStudentMarksPage(1, pageSize, studentMarks, getAuthorizedCodes(request));
			modelnView.addObject("rowCount", page.getRowCount());	
		}
		
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		studentMarksList = studentMarksService.getStudentAssignmentRemarks(studentMarksList);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("studentMarks", studentMarks);
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;	
	}
	

	@RequestMapping(value = "/admin/downloadStudentMarksResults", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentMarksResults(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentMarksBean studentMarks = (StudentMarksBean)request.getSession().getAttribute("studentMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page =new Page<StudentMarksBean>();
		if(studentMarks.getSubjectType().equals("waivedIn")) {
			List<StudentMarksBean>getWaivedInMarksList=studentMarksService.getStudentMarksList(studentMarks);
            page.setPageItems(getWaivedInMarksList);
		}else {
			page =dao.getStudentMarksPage(1, Integer.MAX_VALUE, studentMarks, getAuthorizedCodes(request));
		}		
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		studentMarksList = studentMarksService.getStudentAssignmentRemarks(studentMarksList);
		
		return new ModelAndView("studentMarksResultsExcelView","studentMarksList",studentMarksList);
	}
	
	@RequestMapping(value = "/admin/searchStudentMarksPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchStudentMarksPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchStudentMarks");
		int pageNo = 1;
		if(request.getParameter("pageNo") != null){
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		}
		StudentMarksBean studentMarks = (StudentMarksBean)request.getSession().getAttribute("studentMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getStudentMarksPage(pageNo, pageSize, studentMarks);

	    Page<StudentMarksBean>page = dao.getStudentMarksPage(pageNo, pageSize, studentMarks, getAuthorizedCodes(request));
	
	    List<StudentMarksBean> studentMarksList = page.getPageItems();
		studentMarksList = studentMarksService.getStudentAssignmentRemarks(studentMarksList);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchRegisteredStudentMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchRegisteredStudentMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("studentMarks", marks);
		
		m.addAttribute("offlineCentersList", getOfflineExamCenterMapForDropDown());
		m.addAttribute("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		
		return "searchRegisteredStudentMarks";
	}
	
	@RequestMapping(value = "/admin/searchRegisteredStudentMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchRegisteredStudentMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("searchRegisteredStudentMarks");
		request.getSession().setAttribute("studentMarks", studentMarks);
		int pageNo = 1;
		
		if("Offline".equals(studentMarks.getExamMode())){
			studentMarks.setCenterId(studentMarks.getOfflineCenterId());
		}else if("Online".equals(studentMarks.getExamMode())){
			studentMarks.setCenterId(studentMarks.getOnlineCenterId());
		}
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page = dao.getRegisteredStudentMarksPage(pageNo, pageSize, studentMarks);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchRegisteredStudentMarksPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchRegisteredStudentMarksPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchRegisteredStudentMarks");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		StudentMarksBean studentMarks = (StudentMarksBean)request.getSession().getAttribute("studentMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getStudentMarksPage(pageNo, pageSize, studentMarks);
		
		Page<StudentMarksBean> page = dao.getRegisteredStudentMarksPage(pageNo, pageSize, studentMarks);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("offlineCentersList", getOfflineExamCenterMapForDropDown());
		modelnView.addObject("onlineCentersList", getOnlineExamCenterMapForDropDown(false));
		
		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadRegisteredStudentMarksResults", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadRegisteredStudentMarksResults(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Process Pass Fail Page");
		StudentMarksBean studentMarks = (StudentMarksBean)request.getSession().getAttribute("studentMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page = dao.getRegisteredStudentMarksPage(1, Integer.MAX_VALUE, studentMarks);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		
		return new ModelAndView("examRegisteredStudentMarksExcelView","studentMarksList",studentMarksList);
	}
	@RequestMapping(value = "/admin/updateStudentMarks", method = RequestMethod.POST)
	public ModelAndView updateStudentMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("studentMarks");
		String userId = (String)request.getSession().getAttribute("userId");
		studentMarks.setCreatedBy(userId);
		studentMarks.setLastModifiedBy(userId);
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		try {
			dao.updateStudentMarks(studentMarks);
		} catch (SQLException e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in updating record");
			
		}
		studentMarks = dao.findById(studentMarks.getId()+"");
		modelnView.addObject("studentMarks", studentMarks);
		request.getSession().setAttribute("studentMarks", studentMarks);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/editStudentMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editStudentMarks(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView modelnView = new ModelAndView("addStudentMarks");
		StudentMarksBean studentMarks; // = (Company)request.getSession().getAttribute("company");
		String id = request.getParameter("id");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		studentMarks = dao.findById(id);
		modelnView.addObject("studentMarks", studentMarks);
		m.addAttribute("studentMarks",studentMarks);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.setAttribute("edit", "true");
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/getAllStudentMarks",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllStudentMarks(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("studentMarksList");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getAllStudentMarks();
		Page<StudentMarksBean> page = dao.getAllStudentMarksPage(1,pageSize);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		return modelnView;
	}
	
	
	@RequestMapping(value = "/student/viewNotice",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewNotice(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String sapId = (String)request.getSession().getAttribute("userId");
		
		StudentExamBean student = dao.getSingleStudentsData(sapId);
		String mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
		
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student);
		ModelAndView modelnView = new ModelAndView("resultNotice");
		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		return modelnView;
	}

	//added by Meeta on 13/4/2018
	@RequestMapping(value = "/admin/addResultNoticeForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String addResultNoticeForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ResultNotice resultNotice = new ResultNotice();
		m.addAttribute("resultNotice",resultNotice);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("programStructureList",dao.getProgramStructureList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("monthList",monthList);
		m.addAttribute("resultNoticeTypeList",resultNoticeTypeList);
		request.getSession().setAttribute("studentMarks", resultNotice);
		return "addResultNotice";
	}
	
	@RequestMapping(value = "/admin/addResultNotice", method = RequestMethod.POST)
	public ModelAndView addResultNotice(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultNotice resultNotice){
		ModelAndView modelnView = new ModelAndView("addResultNotice");
		try{
		ResultNoticeDAO resultNoticeDAO = (ResultNoticeDAO) act.getBean("resultNoticeDAO");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("programStructureList",dao.getProgramStructureList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("monthList",monthList);
		resultNotice.setCreatedBy(userId);
		resultNotice.setLastModifiedBy(userId);
		resultNoticeDAO.InsertResultNotice(resultNotice);
		setSuccess(request, "Successfully Added ");
		modelnView.addObject("resultNotice", resultNotice);
		}
		catch(Exception e ){
			logger.error("Error ");
		}
		return modelnView;
	}
	//ends
	
	@RequestMapping(value = "/student/getMostRecentResults",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getMostRecentResults(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String sap_Id = (String) request.getSession().getAttribute("userId");
		ModelAndView modelnView = new ModelAndView("/examHome/singleStudentRecentMarks");
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		
		try {
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
			
			resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
			
			//TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			//if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			if(resultsFromRedisHelper.sendingResultsToCache()) {
				return new ModelAndView("noDataAvailable");
			}
					
			//ModelAndView modelnView = new ModelAndView("/examHome/singleStudentRecentMarks");
			
			boolean showResFromCache = Boolean.FALSE;
			//showResFromCache = daoForRedis.checkForFlagValueInCache("showResultsFromCache", "Y");
			showResFromCache = resultsFromRedisHelper.displayResultsFromCache();
			logger.info("Flag in /getMostRecentResults : (showResultsFromCache, sapId) : (" + showResFromCache + ", " + sap_Id + ")");
			//Results from REDIS displayed from code in below IF. Old code after this IF fetches from DB and works.
			if (showResFromCache) {
				//NOTE: Results fetched from REDIS to display.
				List<StudentMarksBean> markslist = null;
				List<PassFailExamBean> listPFbean = null;
				Map<String, Object> destinationMap = null;
				//ResultsFromRedisHelper resultsFromRedisHelper = null;
	
				//resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
	
				destinationMap = resultsFromRedisHelper.fetchResultsFromRedis(ResultsFromRedisHelper.EXAM_STAGE_TEE, sap_Id);
	
				//REDIS returning empty as Student data not present inside, so fetch from DB
				if(null != destinationMap && null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_DETAILS)) {
					markslist = (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST);
					listPFbean = (List<PassFailExamBean>) destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS);
					
					//studentExam not added in session, as student data not needed from REDIS. Vilpesh 20220208
					//request.getSession().setAttribute("studentExam", destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_DETAILS));
					request.setAttribute("declareDate", destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE));
					request.setAttribute("studentMarksListForPassFail", listPFbean);
					if(null != listPFbean && !listPFbean.isEmpty()) {
						request.setAttribute("sizeOfStudentMarkListForPassFail", listPFbean.size());
					}
					request.setAttribute("studentMarksListForMarksHistory", destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY));
		
					modelnView.addObject("size", destinationMap.get(ResultsFromRedisHelper.KEY_SIZE));
					modelnView.addObject("mostRecentResultPeriod", destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD));
					modelnView.addObject("declareDate", destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE));
					modelnView.addObject("studentMarksList", markslist);
		
					if (null == markslist || (null != markslist && markslist.size() == 0)) {
						setError(request, "No Marks Entries found for " + destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD));
					}
					request.setAttribute("resultSource","REDIS");//REDIS is :, indicates from where result displayed on jsp page.
					return modelnView;
				}
			} else {
				logger.error("StudentMarksController : /getMostRecentResults : Did not get Results from REDIS");
			}
		
		} catch (Exception e) {
			
			logger.error("StudentMarksController : /getMostRecentResults : "+ e.getMessage());
			
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
		}
		logger.info("StudentMarksController : DB Fetch in /getMostRecentResults continued");
		
		/*if("Y".equalsIgnoreCase(SHOW_RESULTS_FROM_REDIS)) {
			return modelnView;
		}*/
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();
		//String sapId = request.getParameter("sapId"); For testing
		String sapId = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapId);
		getPassFailStatus(request, response);
		getStudentMarksHistory(request,response,sapId);
		String centerCode = dao.getStudentCenterDetails(sapId);
		
		/*if(centerCode == null || "".equals(centerCode.trim())){
			modelnView = new ModelAndView("selectCenter");
			ArrayList<CenterBean> centers = getCentersList();
			modelnView.addObject("center",new CenterBean());

			Map<String,String> centerCodes = new LinkedHashMap<String,String>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterBean cBean = centers.get(i);
				centerCodes.put(cBean.getCenterCode(), cBean.getCenterName());
			}
			modelnView.addObject("centerCodes",centerCodes);
			request.getSession().setAttribute("centerCodes",centerCodes);
			return modelnView;
		}else{
			ArrayList<CenterBean> centers = getCentersList();
			Map<String,CenterBean> centerMap = new LinkedHashMap<String,CenterBean>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterBean cBean = centers.get(i);
				centerMap.put(cBean.getCenterCode(), cBean);
			}
			modelnView.addObject("center", centerMap.get(centerCode));
			
		}*/
		
		if(centerCode != null && !"".equals(centerCode.trim())){
			ArrayList<CenterExamBean> centers = getCentersList();
			Map<String,CenterExamBean> centerMap = new LinkedHashMap<String,CenterExamBean>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterExamBean cBean = centers.get(i);
				centerMap.put(cBean.getCenterCode(), cBean);
			}
			modelnView.addObject("center", centerMap.get(centerCode));
		}else{
			CenterExamBean cBean = new CenterExamBean();
			cBean.setCenterName("Center Information Not Available");
			cBean.setAddress("Please contact head office to get your center information updated.");
			modelnView.addObject("center", cBean);
		}
		
		String email = request.getSession().getAttribute("emailId").toString();
		StudentExamBean student = dao.getSingleStudentsData(sapId);
		if(checkLead(request, response))
			try {
			student = leadDAO.getLeadsFromSalesForce(email, student);
			}catch (Exception e) {
			}
		
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student);
		String mostRecentResultPeriod = "";
		String declareDate = "";
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			declareDate = dao.getRecentExamDeclarationDate();
			/*Added by Stef
			 * bean.setYear(mostRecentResultPeriod.substring(4));
			bean.setMonth(mostRecentResultPeriod.substring(0,3));*/
			studentMarksList =  dao.getAStudentsMostRecentMarks(bean);
			if(mostRecentResultPeriod.equalsIgnoreCase("Jun-2019")) {
				for(StudentMarksBean studentMarks : studentMarksList) {
					boolean assignSetToZero = false;
					boolean writtenSetToZero = false;
					boolean graceSetToZero = false;
					if(studentMarks.getSubject().equalsIgnoreCase("Business Statistics" )) {
						
					if(StringUtils.isBlank(studentMarks.getRemarks())) {
						studentMarks.setRemarks("");
					}
					if(StringUtils.isBlank(studentMarks.getAssignmentscore())) {
						studentMarks.setAssignmentscore("0");
						assignSetToZero=true;
					}
					if(StringUtils.isBlank(studentMarks.getWritenscore())) {
						studentMarks.setWritenscore("0");
						writtenSetToZero=true;
					}
					if(StringUtils.isBlank(studentMarks.getGracemarks())) {
						studentMarks.setGracemarks("0");
						graceSetToZero=true;
					}
					}
					
					if(studentMarks.getSubject().equalsIgnoreCase("Business Statistics" ) 
							&& !studentMarks.getAssignmentscore().equalsIgnoreCase("ANS") 
							&& !studentMarks.getWritenscore().equalsIgnoreCase("RIA")
							&& !studentMarks.getWritenscore().equalsIgnoreCase("NV")
							&& !studentMarks.getWritenscore().equalsIgnoreCase("AB")
							&& !studentMarks.getRemarks().equalsIgnoreCase("End of Program validity grace given")
							&& !StringUtils.isBlank(studentMarks.getMcq())
							&& !StringUtils.isBlank(studentMarks.getPart4marks())
							) {
						
						int uiSumTotal =(int) Math.round( (Double.parseDouble(studentMarks.getMcq())+ Double.parseDouble(studentMarks.getPart4marks()) + Double.parseDouble(studentMarks.getAssignmentscore())));
						int marksTotal =  (Integer.parseInt(studentMarks.getWritenscore()) + Integer.parseInt(studentMarks.getAssignmentscore() ) ) -  Integer.parseInt(studentMarks.getGracemarks() ) ;
						if(uiSumTotal !=  marksTotal) {
							String mcqUpdated = ""+(Double.parseDouble(studentMarks.getMcq())+(marksTotal-uiSumTotal));
							studentMarks.setMcq(mcqUpdated);
						}
					}
					
					if(assignSetToZero) {
						if("0".equalsIgnoreCase(studentMarks.getAssignmentscore())) {
							studentMarks.setAssignmentscore("");
						}
					}
					if(writtenSetToZero) {
					if("0".equalsIgnoreCase(studentMarks.getWritenscore())) {
						studentMarks.setWritenscore("");
					}
					}
					if(graceSetToZero) {
					if("0".equalsIgnoreCase(studentMarks.getGracemarks())) {
						studentMarks.setGracemarks("");
					}
					}
				}
			}
		}else{
			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			declareDate = dao.getRecentOfflineExamDeclarationDate();
		/*	Added by Stef
		 * bean.setYear(mostRecentResultPeriod.substring(4));
			bean.setMonth(mostRecentResultPeriod.substring(0,3));*/
			studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(bean);
		}
		
		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		modelnView.addObject("declareDate", declareDate);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		
		if(studentMarksList.size() == 0){
			setError(request, "No Marks Entries found for "+mostRecentResultPeriod);
		}
		request.setAttribute("resultSource","DB");//DB is space,indicates from where result displayed on jsp page
		return modelnView;
	}
	
//START :-CALLING PASSFAIL AND MARKS HISTORY ON RESULT PAGE//
private void getPassFailStatus(HttpServletRequest request,HttpServletResponse response) {
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		String sapId = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapId);
		
		
		
		StudentExamBean student = dao.getSingleStudentsData(sapId);
		String mostRecentResultPeriod = "";
		String declareDate = "";
		List<PassFailExamBean> studentMarksListForPassFail = new ArrayList<PassFailExamBean>();
		//JUL2013 is online course//
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			//declareDate = dao.getRecentExamDeclarationDate();
			studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Online");
		}else{
			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			//declareDate = dao.getRecentOfflineExamDeclarationDate();
			studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Offline");
		}
		
		
		request.setAttribute("declareDate", declareDate);
		request.setAttribute("studentMarksListForPassFail", studentMarksListForPassFail);
		request.setAttribute("sizeOfStudentMarkListForPassFail", studentMarksListForPassFail.size());
		
	}

// getPassFailStatusForExecutive Start
private void getPassFailStatusForExecutive(HttpServletRequest request,HttpServletResponse response) {
	
	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	StudentMarksBean bean = new StudentMarksBean();

	String sapId = (String)request.getSession().getAttribute("userId");
	bean.setSapid(sapId);
	
	
	
	StudentExamBean student = dao.getSingleStudentsData(sapId);
	String mostRecentResultPeriod = "";
	String declareDate = "";
	List<PassFailExamBean> studentMarksListForPassFail = new ArrayList<PassFailExamBean>();
		mostRecentResultPeriod = dao.getMostRecentResultPeriodForExecutive(student);
		declareDate = dao.getRecentExamDeclarationDateForExecutive(student);
		studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarksForExecutive(bean);
	
	
	
	request.setAttribute("mostRecentResultPeriod", mostRecentResultPeriod);
	request.setAttribute("declareDate", declareDate);
	request.setAttribute("studentMarksListForPassFail", studentMarksListForPassFail);
	request.setAttribute("sizeOfStudentMarkListForPassFail", studentMarksListForPassFail.size());
	
}
//getPassFailStatusForExecutive End

private void getStudentMarksHistory(HttpServletRequest request,HttpServletResponse response,String userID){
	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	StudentExamBean student =  dao.getSingleStudentsData(userID);
	StudentMarksBean bean = new StudentMarksBean();
	
	bean.setSapid(student.getSapid());
	List<StudentMarksBean> studentMarksListForMarksHistory =  null;
	if("Online".equals(student.getExamMode())){
		studentMarksListForMarksHistory = dao.getAStudentsMarksForOnline(bean);
	}else{
		studentMarksListForMarksHistory = dao.getAStudentsMarksForOffline(bean);
	}
	
	request.setAttribute("studentMarksListForMarksHistory", studentMarksListForMarksHistory);
}

private void getStudentMarksHistoryForExecutive(HttpServletRequest request,HttpServletResponse response,String userID){
	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	StudentExamBean student =  dao.getSingleStudentsData(userID);
	StudentMarksBean bean = new StudentMarksBean();
	
	bean.setSapid(student.getSapid());
	List<StudentMarksBean> studentMarksListForMarksHistory =  null;
	studentMarksListForMarksHistory = dao.getAStudentsMarksForExecutive(bean);
	request.setAttribute("studentMarksListForMarksHistory", studentMarksListForMarksHistory);
}


//END//
	
	@RequestMapping(value = "/student/getMostRecentPassFailResults",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getMostRecentPassFailResults(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		

		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			return new ModelAndView("noDataAvailable");
		}
		
		ModelAndView modelnView = new ModelAndView("singleStudentPassFailMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		String sapId = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapId);
		
		
		
		StudentExamBean student = dao.getSingleStudentsData(sapId);
		
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student);
		String mostRecentResultPeriod = "";
		String declareDate = "";
		List<PassFailExamBean> studentMarksList = new ArrayList<PassFailExamBean>();
		
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			//declareDate = dao.getRecentExamDeclarationDate();
			studentMarksList =  dao.getAStudentsMostRecentPassFailMarks(bean, "Online");
		}else{
			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			//declareDate = dao.getRecentOfflineExamDeclarationDate();
			studentMarksList =  dao.getAStudentsMostRecentPassFailMarks(bean, "Offline");
		}
		
		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		modelnView.addObject("declareDate", declareDate);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		return modelnView;
	}
	
	@RequestMapping(value = "/student/getMostRecentAssignmentResults",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getMostRecentAssignmentResults(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			return new ModelAndView("noDataAvailable");
		}
		
		ModelAndView modelnView = new ModelAndView("singleStudentAssignmentMarks");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		String sapId = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapId);
		
		
		String mostRecentResultPeriod = dao.getMostRecentAssignmentResultPeriod();
		String year = mostRecentResultPeriod.substring(4);
		String month = mostRecentResultPeriod.substring(0,3);
		bean.setYear(year);
		bean.setMonth(month);
		
		//String declareDate = dao.getRecentExamDeclarationDate();
		List<StudentMarksBean> studentMarksList =  dao.getAStudentsMostRecentAssignmentMarks(bean);
		modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
		//modelnView.addObject("declareDate", declareDate);
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		
		if(studentMarksList.size() == 0){
			setError(request, "No Assignment Marks found for "+mostRecentResultPeriod);
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/saveStudentCenter", method = RequestMethod.POST)
	public ModelAndView saveStudentCenter(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CenterExamBean center){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("singleStudentRecentMarks");
		String sapId = (String)request.getSession().getAttribute("userId");
		Map<String,String> centerCodes = (Map<String,String>)request.getSession().getAttribute("centerCodes");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String centerName = centerCodes.get(center.getCenterCode());
		try {
			dao.updateStudentCenter(center.getCenterCode(),centerName, sapId);
			return getMostRecentResults(request, response);
		} catch (Exception e) {
			
			modelnView = new ModelAndView("studentHome");
		}
		return modelnView;
	}
	
	
	/*@RequestMapping(value = "/getAStudentMarks",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAStudentMarks(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("singleStudentMarksList");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		
		String sapId = (String)request.getSession().getAttribute("userId");
		bean.setSapid(sapId);
		List<StudentMarksBean> studentMarksList =  null;
		StudentBean student = dao.getSingleStudentsData(sapId);
		
		if("Jul2014".equals(student.getPrgmStructApplicable()) || "Jul2013".equals(student.getPrgmStructApplicable())){
			studentMarksList =  dao.getAStudentsMarksForOnline(bean);
		}else{
			studentMarksList =  dao.getAStudentsMarksForOffline(bean);
		}
		
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		modelnView.addObject("studentMarks", new StudentMarksBean());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		return modelnView;
	}*/
	
	@RequestMapping(value = "/student/searchSingleStudentMarks",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchSingleStudentMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks) throws UnsupportedEncodingException{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("singleStudentMarksList");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");

		//String sapId = request.getParameter("sapId");For testing
		
		String sapId = (String)request.getSession().getAttribute("userId");
		studentMarks.setSapid(sapId);
		

		StudentExamBean student = dao.getSingleStudentsData(sapId);
		String programStructure = student.getPrgmStructApplicable();
		List<StudentMarksBean> studentMarksList =  new ArrayList<StudentMarksBean>();
		if("Online".equals(student.getExamMode())){
			studentMarksList = dao.searchSingleStudentMarks(studentMarks, "Online");
		}else{
			studentMarksList = dao.searchSingleStudentMarks(studentMarks, "Offline");
		}
		
		
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("size", studentMarksList.size());
		modelnView.addObject("studentMarks", studentMarks);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/getAllStudentMarksPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllStudentMarksPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("studentMarksList");

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		//int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		
		Page<StudentMarksBean> page = dao.getAllStudentMarksPage(pageNo,pageSize);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/deleteStudentMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteStudentMarks(HttpServletRequest request, HttpServletResponse response){

		String id = request.getParameter("id");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		dao.deleteStudentMarks(id);

		return searchStudentMarksPage(request,response);
	}
	
	@RequestMapping(value = "/admin/viewStudentMarksDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewStudentMarksDetails(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("studentMarks");
		StudentMarksBean studentMarks; // = (Company)request.getSession().getAttribute("company");
		String id = request.getParameter("id");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		studentMarks = dao.findById(id);
		modelnView.addObject("studentMarks", studentMarks);

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadExcelMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadExcelMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		
		return "uploadMarksExcel";
	}
	
	@RequestMapping(value = "/admin/uploadWrittenMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadWrittenMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Upload Written Marks page");//
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);
		return "uploadWrittenMarks";
	}
	
	@RequestMapping(value = "/admin/uploadWrittenMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadWrittenMarks(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		logger.info("uploadWrittenMarks : START");	
		ModelAndView modelnView = new ModelAndView("uploadWrittenMarks");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readWrittenMarksExcel(fileBean, getProgramList(), getSubjectList(), userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentMarksBean> marksBeanList = (ArrayList<StudentMarksBean>)resultList.get(0);
				List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);
				
				fileBean = new FileBean();
				m.addAttribute("fileBean",fileBean);
				m.addAttribute("yearList", yearList);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				
				HashMap<String,String> studentTypeMap= new HashMap<String,String>();
					 if(marksBeanList.size()>0) {
						 List<TCSMarksBean> studentTypeList = tcsDAO.getStudentTypeMap();	
						 if(studentTypeList.size()>0) {
							 for(TCSMarksBean bean:studentTypeList) {
								if(!studentTypeMap.containsKey(bean.getSapid())) {
									studentTypeMap.put(bean.getSapid(), bean.getConsumerType());
								}
							 }
							 for(StudentMarksBean bean:marksBeanList) {
								 String studentType = "";
								 try {
									 studentType = studentTypeMap.get(bean.getSapid());
								 }catch(Exception e) {
									 logger.error("uploadWrittenMarks : StudentMarksBean : "+e.getMessage());//by Vilpesh 2022-02-25
								 }
								 bean.setStudentType(studentType);
							 }
						 }
					 }
				//by Vilpesh 2022-02-25
				if(null != studentTypeMap) {
					studentTypeMap.clear();
				}
				ArrayList<String> errorList = dao.batchUpdate(marksBeanList, "written");
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",marksBeanList.size() +" rows out of "+ marksBeanList.size()+" inserted successfully.");
				}else{
					//StudentMarksBean bean = marksBeanList.get(lastRowUpdated);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
					//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
				}
				
			}catch(Exception e){
				logger.error("uploadWrittenMarks : "+e.getMessage());
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records.");

			}
			logger.info("uploadWrittenMarks : FINISH");
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadAssignmentMarksForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadAssignmentMarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", currentYearList);
		return "uploadAssignmentMarks";
	}
	
	@RequestMapping(value = "/admin/uploadAssignmentMarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadAssignmentMarks(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadAssignmentMarks");
			try{
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readAssignmentMarksExcel(fileBean, getProgramList(), getSubjectList(),getProgramSubjectMappingList(), userId, dao);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentMarksBean> marksBeanList = (ArrayList<StudentMarksBean>)resultList.get(0);
				List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);
				
				
				ArrayList<String> errorList = dao.batchUpdate(marksBeanList, "Assignment");
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",marksBeanList.size() +" rows out of "+ marksBeanList.size()+" inserted successfully.");
				}else{
					//StudentMarksBean bean = marksBeanList.get(lastRowUpdated);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
					//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", currentYearList);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/getProgramSubjectMappingForm",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getProgramSubjectMappingForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("studentMarks");
		StudentMarksBean marks = new StudentMarksBean();
		modelnView.addObject("studentMarks",marks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		//StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getAllStudentMarks();
		//Page<StudentMarksBean> page = dao.getAllStudentMarksPage(1,pageSize);
		//List<StudentMarksBean> studentMarksList = page.getPageItems();
		//modelnView.addObject("studentMarksList", studentMarksList);
		//modelnView.addObject("page", page);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/getProgramSubjectMapping",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getProgramSubjectMapping(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("studentMarks");
		StudentMarksBean marks = new StudentMarksBean();
		modelnView.addObject("studentMarks",marks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getAllStudentMarks();
		Page<StudentMarksBean> page = dao.getAllStudentMarksPage(1,pageSize);
		List<StudentMarksBean> studentMarksList = page.getPageItems();
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("page", page);
		
		return modelnView;
	}
	

	@RequestMapping(value = "/admin/uploadExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadExcel(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadMarksExcel");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readMarksExcel(fileBean, getProgramList(), getSubjectList(), userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentMarksBean> marksBeanList = (ArrayList<StudentMarksBean>)resultList.get(0);
				List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpdateOldData(marksBeanList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",marksBeanList.size() +" rows out of "+ marksBeanList.size()+" inserted successfully.");
				}else{
					//StudentMarksBean bean = marksBeanList.get(lastRowUpdated);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
					//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/insertABRecordsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView insertABRecordsForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean searchBean) {
		ModelAndView modelnView = new ModelAndView("insertABRecords");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchABRecordsToInsert", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchANSRecordsToInsert(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean searchBean){
		logger.info("searchABRecordsToInsert : START");// 
		ModelAndView modelnView = new ModelAndView("insertABRecords");
		String studentType = request.getParameter("studentType");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		List<ExamBookingTransactionBean> studentsList = dao.getABRecords(1, Integer.MAX_VALUE, searchBean); // Absent records for offline are added manually by tee team.
		List<ExamBookingTransactionBean> projectForOffline = dao.getProjectABRecordsForOffline(searchBean);
		List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmitted = dao.projectFeeExemptAndNotSubmitted(searchBean);
		int projectAbsentCount=0;
		int studentTEEAbsentList=0;
		
		//Getting the Count of Absent student for below Subjects
		List<ExamBookingTransactionBean> projectAbsentCountOnline =  studentsList.stream().filter(w -> 
			w.getSubject().equalsIgnoreCase("Project") 
			|| w.getSubject().equalsIgnoreCase("Module 4 - Project")
			|| w.getSubject().equalsIgnoreCase("Simulation: Mimic Pro")
			|| w.getSubject().equalsIgnoreCase("Simulation: Mimic Social")).collect(Collectors.toList());
		
		if(projectForOffline.size()>0 || projectFeeExemptAndNotSubmitted.size()>0 || projectAbsentCountOnline.size()>0) {
			studentTEEAbsentList=studentsList.size()-projectAbsentCountOnline.size(); // Removing the count of project subject from Overall Absent list of online
			projectAbsentCount=projectAbsentCountOnline.size()+projectForOffline.size()+projectFeeExemptAndNotSubmitted.size(); // Getting the total count for project Absent Count
			
		}
		
		
		if(!projectForOffline.isEmpty())
		{
			studentsList.addAll(projectForOffline);
		}
		
		if(!projectFeeExemptAndNotSubmitted.isEmpty()) //Added students who are exempted from exam fee and not submitted project.
		{
			studentsList.addAll(projectFeeExemptAndNotSubmitted);
		}
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("projectAbsentCount", projectAbsentCount);
		modelnView.addObject("studentTEEAbsentList", studentTEEAbsentList);
		modelnView.addObject("rowCount", studentsList != null ? studentsList.size() : 0);
		
		modelnView.addObject("yearList", currentYearList);
		if(studentsList == null || studentsList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}else{
			HashMap<String, StudentExamBean>  sapIdStudentsMap = getStudentsMap();
			for(ExamBookingTransactionBean bean : studentsList) {
				bean.setFirstName(sapIdStudentsMap.get(bean.getSapid()).getFirstName());
				bean.setLastName(sapIdStudentsMap.get(bean.getSapid()).getLastName());
				bean.setWritenscore("AB");
				bean.setStudentType(studentType);
			
			}
			
			setSuccess(request, "Please download AB report for verification");
		}

		modelnView.addObject("studentsList", studentsList);
		request.getSession().setAttribute("studentsList", studentsList);
		request.getSession().setAttribute("searchBean", searchBean);
		logger.info("searchABRecordsToInsert : FINISH");
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadABReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadABReport(HttpServletRequest request, HttpServletResponse response) {
		
		List<ExamBookingTransactionBean> studentsList = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("studentsList");
		return new ModelAndView("absentReportExcelView","studentsList",studentsList);
	}
	
	@RequestMapping(value = "/admin/insertABReport", method = {RequestMethod.GET})
	public ModelAndView insertABReport(HttpServletRequest request, HttpServletResponse response) {
		logger.info("insertABReport : START");
		ModelAndView modelnView = new ModelAndView("insertABRecords");
		@SuppressWarnings("unchecked")
		List<ExamBookingTransactionBean> studentsList = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("studentsList");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < studentsList.size(); i++) {
			try{
				ExamBookingTransactionBean bean = studentsList.get(i);
				StudentMarksBean studentMarksBean = convertBean(bean);
				//Upsert AB Records
				dao.UpdateABRecords(studentMarksBean, "written");
			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		if(errorList.size() == 0){
			request.setAttribute("success","true");
			request.setAttribute("successMessage",studentsList.size() +" rows out of "+ studentsList.size()+" inserted successfully.");
		}else{
			//StudentMarksBean bean = marksBeanList.get(lastRowUpdated);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
		}
		ExamBookingTransactionBean searchBean = (ExamBookingTransactionBean)request.getSession().getAttribute("searchBean");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", studentsList != null ? studentsList.size() : 0);
		
		modelnView.addObject("yearList", currentYearList);
		logger.info("insertABReport : FINISH");
		return modelnView;
	}
	
	
	private StudentMarksBean convertBean(ExamBookingTransactionBean bean) {
		StudentMarksBean studentMarksBean = new StudentMarksBean();
		studentMarksBean.setSapid(bean.getSapid());
		studentMarksBean.setGrno("Not Available");
		studentMarksBean.setStudentname(bean.getFirstName()+" "+bean.getLastName() );
		studentMarksBean.setSubject(bean.getSubject());
		studentMarksBean.setWritenscore(bean.getWritenscore());
		studentMarksBean.setYear(bean.getYear());
		studentMarksBean.setMonth(bean.getMonth());
		studentMarksBean.setCreatedBy(bean.getCreatedBy());
		studentMarksBean.setLastModifiedBy(bean.getLastModifiedBy());
		studentMarksBean.setProgram(bean.getProgram());
		studentMarksBean.setSem(bean.getSem());
		studentMarksBean.setStudentType(bean.getStudentType());
		return studentMarksBean;
	}
	
//	to e deleted, api shifted to rest controller
////Mobile Api
//	@RequestMapping(value = "/m/getMostRecentResults" , method = RequestMethod.POST, consumes= "application/json", produces = "application/json")
//		public ResponseEntity<Map<String,List>> mgetMostRecentResults (@RequestBody StudentBean student) throws UnsupportedEncodingException{
	

//		
//		if(StringUtils.isBlank(student.getFromAdmin()) ) {
//			TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
//			
//			if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
//				//Map<String,List> returnDataFromRedis = restApiCallToGetResultsFromCache(student);
//				Map<String,List> returnDataFromRedis = new HashMap<>();
//					
//				return new ResponseEntity<Map<String,List>>(returnDataFromRedis, HttpStatus.OK);
//				
//			}
//
//		}
//		
//		
//		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//		StudentMarksBean bean = new StudentMarksBean();
//		bean.setSapid(student.getSapid());
//		Map<String,List> result_data = new HashMap<String, List>();
//		List passFailStatus = mgetPassFailStatus(student);
//		//Temp hide
//		//passFailStatus = new ArrayList<>(); 
//		result_data.put("passFailStatus",passFailStatus);
//		List studentMarksHistory = mgetStudentMarksHistory(student);
//		//Temp hide
//		//studentMarksHistory = new ArrayList<>();
//		result_data.put("studentMarksHistory",studentMarksHistory);
//		String centerCode = dao.getStudentCenterDetails(student.getSapid());
//		
//		/*if(centerCode == null || "".equals(centerCode.trim())){
//			modelnView = new ModelAndView("selectCenter");
//			ArrayList<CenterBean> centers = getCentersList();
//			modelnView.addObject("center",new CenterBean());
//
//			Map<String,String> centerCodes = new LinkedHashMap<String,String>();
//			
//			for (int i = 0; i < centers.size(); i++) {
//				CenterBean cBean = centers.get(i);
//				centerCodes.put(cBean.getCenterCode(), cBean.getCenterName());
//			}
//			modelnView.addObject("centerCodes",centerCodes);
//			request.getSession().setAttribute("centerCodes",centerCodes);
//			return modelnView;
//		}else{
//			ArrayList<CenterBean> centers = getCentersList();
//			Map<String,CenterBean> centerMap = new LinkedHashMap<String,CenterBean>();
//			
//			for (int i = 0; i < centers.size(); i++) {
//				CenterBean cBean = centers.get(i);
//				centerMap.put(cBean.getCenterCode(), cBean);
//			}
//			modelnView.addObject("center", centerMap.get(centerCode));
//			
//		}*/
//		
//		if(centerCode != null && !"".equals(centerCode.trim())){
//			ArrayList<CenterBean> centers = getCentersList();
//			Map<String,CenterBean> centerMap = new LinkedHashMap<String,CenterBean>();
//			
//			for (int i = 0; i < centers.size(); i++) {
//				CenterBean cBean = centers.get(i);
//				centerMap.put(cBean.getCenterCode(), cBean);
//			}
//			//modelnView.addObject("center", centerMap.get(centerCode));
//			List center = 	new ArrayList<>();
//			center.add(centerMap.get(centerCode));
//			result_data.put("center",center);
//		}else{
//			CenterBean cBean = new CenterBean();
//			cBean.setCenterName("Center Information Not Available");
//			cBean.setAddress("Please contact head office to get your center information updated.");
//			//modelnView.addObject("center", cBean);
//			List center = 	new ArrayList<>();
//			center.add(center);
//			result_data.put("center",center);
//		}
//		
//		String mostRecentResultPeriod = "";
//		String declareDate = "";
//		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
//		
//		if("Online".equals(student.getExamMode())){
//			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
//			declareDate = dao.getRecentExamDeclarationDate();
//			studentMarksList =  dao.getAStudentsMostRecentMarks(bean);
//		}else{
//			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
//			declareDate = dao.getRecentOfflineExamDeclarationDate();
//			studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(bean);
//		}
//		
//		/* //Temp Hide start to be rmoved later by PS
//		//studentMarksList = new ArrayList<StudentMarksBean>();
//		StudentBean studentBean = dao.getSingleStudentsData(student.getSapid());
//		if("Diageo".equalsIgnoreCase(studentBean.getConsumerType())){
//			studentMarksList = new ArrayList<StudentMarksBean>();
//		}
//		//end */
//		
//		result_data.put("studentMarksList",studentMarksList);
//
//		List center = 	new ArrayList<>();
//		center.add(mostRecentResultPeriod);
//		result_data.put("mostRecentResultPeriod",center);
//		List center2 = 	new ArrayList<>();
//		center2.add(declareDate);
//		result_data.put("declareDate",center2);
//		List center4 = 	new ArrayList<>();
//		center4.add( studentMarksList.size());
//		result_data.put("size",center4);
////		
////		if(studentMarksList.size() == 0){
////			setError(request, "No Marks Entries found for "+mostRecentResultPeriod);
////		}
//		
//		//Added by Pranit on 22Apr20 start
//		StudentBean studentDetailsFromDB = dao.getSingleStudentsData(student.getSapid());
//
//		List studentDetailsArray = 	new ArrayList<>();
//		studentDetailsArray.add(studentDetailsFromDB);
//		
//		result_data.put("studentDetails",studentDetailsArray);
//		//Added by Pranit on 22Apr20 end
//		
//		return new ResponseEntity<Map<String,List>>(result_data, HttpStatus.OK);
//	}
//	
//	public Map<String,List> restApiCallToGetResultsFromCache(StudentBean student) {
//
//		RestTemplate restTemplate = new RestTemplate();
//		Map<String,List> returnBean = new HashMap<>();
//		try {
//			  String url = SERVER_PATH+"timeline/api/results/getResultsDataFromRedisBySapid";
//	    	  HttpHeaders headers = new HttpHeaders();
//			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			  HttpEntity<StudentBean> entity = new HttpEntity<StudentBean>(student,headers);
//			  
//			  StudentsDataInRedisBean bean = (StudentsDataInRedisBean)  restTemplate.exchange(
//				 url,
//			     HttpMethod.POST, entity, StudentsDataInRedisBean.class).getBody();
//			  if(bean != null && bean.getResultsData() != null) {
//				  return bean.getResultsData();
//			  }
//		} catch (Exception e) {
//			
//		}
//		
//		return returnBean;
//	}
	
	//Mobile Api
	@RequestMapping(value = "/m/getMostRecentResults" , method = RequestMethod.POST, consumes= "application/json", produces = "application/json")
		public ResponseEntity<Map<String,List>> mgetMostRecentResults (@RequestBody StudentExamBean student) throws UnsupportedEncodingException{
		
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		try {
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
			resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
			
			//TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			if(StringUtils.isBlank(student.getFromAdmin()) ) {
				//TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
				
				//if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
				if(resultsFromRedisHelper.sendingResultsToCache()) {
					//Map<String,List> returnDataFromRedis = restApiCallToGetResultsFromCache(student);
					Map<String,List> returnDataFromRedis = new HashMap<>();
						
					return new ResponseEntity<Map<String,List>>(returnDataFromRedis, HttpStatus.OK);
					
				}
	
			}
			
			boolean showResFromCache = Boolean.FALSE;
			//showResFromCache = daoForRedis.checkForFlagValueInCache("showResultsFromCache", "Y");
			showResFromCache = resultsFromRedisHelper.displayResultsFromCache();
			logger.info("Flag in /m/getMostRecentResults : (showResultsFromCache, sapId) : (" + showResFromCache + ", " + student.getSapid() + ")");
			//Results from REDIS displayed from code in below IF. Old code after this IF fetches from DB and works.
			if (showResFromCache) {
				//NOTE: Results fetched from REDIS to display.
				Map<String, List> resultsMap = new HashMap<String, List>();
	
				Map<String, Object> destinationMap = null;
				//ResultsFromRedisHelper resultsFromRedisHelper = null;
	
				//resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
	
				destinationMap = resultsFromRedisHelper.fetchResultsFromRedis(ResultsFromRedisHelper.EXAM_STAGE_TEE,
						student.getSapid());
				
				if(null != destinationMap && null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_DETAILS)) {
					StudentExamBean[] arrStudentBean = new StudentExamBean[1];
					arrStudentBean[0] = (StudentExamBean) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_DETAILS);
					resultsMap.put("studentDetails", Arrays.asList(arrStudentBean));
		
					resultsMap.put("declareDate", (List<String>) destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE));
					
					List<PassFailExamBean> listPF = (List<PassFailExamBean>) destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS);
					List<Object> list1 = new ArrayList<Object>();
					list1.add("");
					list1.add(listPF);
					list1.add(listPF.size());
					resultsMap.put("passFailStatus", list1);
					resultsMap.put("studentMarksHistory", (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY));
		
					Integer[] arrInteger = new Integer[1];
					arrInteger[0] = (Integer) destinationMap.get(ResultsFromRedisHelper.KEY_SIZE);
					resultsMap.put("size", Arrays.asList(arrInteger));
		
					//MostRecentResultPeriod returned, changed to String, from earlier List<String>, so this code. Vilpesh 2022-01-25
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD)) {
						ArrayList<String> tempList = new ArrayList<String>();
						tempList.add((String) destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD));
						resultsMap.put("mostRecentResultPeriod", tempList);
					}
					resultsMap.put("studentMarksList", (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST));
					
					String[] arrString = new String[1];
					arrString[0] = "REDIS";//REDIS is :, indicates from where result displayed on jsp page.
					resultsMap.put("resultSource", Arrays.asList(arrString));
					
					destinationMap.clear();
					destinationMap = null;
					resultsFromRedisHelper = null;
		
					return new ResponseEntity<Map<String, List>>(resultsMap, HttpStatus.OK);
				} else {
					logger.error("StudentMarksController : /m/getMostRecentResults : Did not get Results from REDIS");
				}
			}
		} catch (Exception e) {
			
			logger.error("StudentMarksController : /m/getMostRecentResults : "+ e.getMessage());
			
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
		}
		logger.info("StudentMarksController : DB Fetch in /m/getMostRecentResults continued");	
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();
		bean.setSapid(student.getSapid());
		Map<String,List> result_data = new HashMap<String, List>();
		List passFailStatus = mgetPassFailStatus(student);
		//Temp hide
		//passFailStatus = new ArrayList<>(); 
		result_data.put("passFailStatus",passFailStatus);
		List studentMarksHistory = mgetStudentMarksHistory(student);
		//Temp hide
		//studentMarksHistory = new ArrayList<>();
		result_data.put("studentMarksHistory",studentMarksHistory);
		String centerCode = dao.getStudentCenterDetails(student.getSapid());
		
		/*if(centerCode == null || "".equals(centerCode.trim())){
			modelnView = new ModelAndView("selectCenter");
			ArrayList<CenterBean> centers = getCentersList();
			modelnView.addObject("center",new CenterBean());

			Map<String,String> centerCodes = new LinkedHashMap<String,String>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterBean cBean = centers.get(i);
				centerCodes.put(cBean.getCenterCode(), cBean.getCenterName());
			}
			modelnView.addObject("centerCodes",centerCodes);
			request.getSession().setAttribute("centerCodes",centerCodes);
			return modelnView;
		}else{
			ArrayList<CenterBean> centers = getCentersList();
			Map<String,CenterBean> centerMap = new LinkedHashMap<String,CenterBean>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterBean cBean = centers.get(i);
				centerMap.put(cBean.getCenterCode(), cBean);
			}
			modelnView.addObject("center", centerMap.get(centerCode));
			
		}*/
		
		if(centerCode != null && !"".equals(centerCode.trim())){
			ArrayList<CenterExamBean> centers = getCentersList();
			Map<String,CenterExamBean> centerMap = new LinkedHashMap<String,CenterExamBean>();
			
			for (int i = 0; i < centers.size(); i++) {
				CenterExamBean cBean = centers.get(i);
				centerMap.put(cBean.getCenterCode(), cBean);
			}
			//modelnView.addObject("center", centerMap.get(centerCode));
			List center = 	new ArrayList<>();
			center.add(centerMap.get(centerCode));
			result_data.put("center",center);
		}else{
			CenterExamBean cBean = new CenterExamBean();
			cBean.setCenterName("Center Information Not Available");
			cBean.setAddress("Please contact head office to get your center information updated.");
			//modelnView.addObject("center", cBean);
			List center = 	new ArrayList<>();
			center.add(center);
			result_data.put("center",center);
		}
		
		String mostRecentResultPeriod = "";
		String declareDate = "";
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			declareDate = dao.getRecentExamDeclarationDate();
			studentMarksList =  dao.getAStudentsMostRecentMarks(bean);
		}else{
			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			declareDate = dao.getRecentOfflineExamDeclarationDate();
			studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(bean);
		}
		
		/* //Temp Hide start to be rmoved later by PS
		//studentMarksList = new ArrayList<StudentMarksBean>();
		StudentBean studentBean = dao.getSingleStudentsData(student.getSapid());
		if("Diageo".equalsIgnoreCase(studentBean.getConsumerType())){
			studentMarksList = new ArrayList<StudentMarksBean>();
		}
		//end */
		
		result_data.put("studentMarksList",studentMarksList);

		List center = 	new ArrayList<>();
		center.add(mostRecentResultPeriod);
		result_data.put("mostRecentResultPeriod",center);
		List center2 = 	new ArrayList<>();
		center2.add(declareDate);
		result_data.put("declareDate",center2);
		List center4 = 	new ArrayList<>();
		center4.add( studentMarksList.size());
		result_data.put("size",center4);
//	
//	if(studentMarksList.size() == 0){
//		setError(request, "No Marks Entries found for "+mostRecentResultPeriod);
//	}
	
	//Added by Pranit on 22Apr20 start
	StudentExamBean studentDetailsFromDB = dao.getSingleStudentsData(student.getSapid());

	List studentDetailsArray = 	new ArrayList<>();
	studentDetailsArray.add(studentDetailsFromDB);
	
	result_data.put("studentDetails",studentDetailsArray);
	//Added by Pranit on 22Apr20 end
	
	String[] arrString1 = new String[1];
	arrString1[0] = "DB";//DB is space,indicates from where result displayed on jsp page
	result_data.put("resultSource", Arrays.asList(arrString1));
	
	return new ResponseEntity<Map<String,List>>(result_data, HttpStatus.OK);
}		
	
private List mgetPassFailStatus(StudentExamBean student) {
	List result_data = new ArrayList<>();

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		bean.setSapid(student.getSapid());
		
		
		
	
		String mostRecentResultPeriod = "";
		String declareDate = "";
		List<PassFailExamBean> studentMarksListForPassFail = new ArrayList<PassFailExamBean>();
		//JUL2013 is online course//
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			//declareDate = dao.getRecentExamDeclarationDate();
			studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Online");
		}else{
			mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			//declareDate = dao.getRecentOfflineExamDeclarationDate();
			studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Offline");
		}
		

		result_data.add(declareDate);
		
	
		result_data.add(studentMarksListForPassFail);
		
	result_data.add(studentMarksListForPassFail.size());
		
	
		 return result_data;
		
	}
private List<StudentMarksBean>  mgetStudentMarksHistory(StudentExamBean student){
	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	StudentMarksBean bean = new StudentMarksBean();
	bean.setSapid(student.getSapid());
	List<StudentMarksBean> studentMarksListForMarksHistory =  null;
	if("Online".equals(student.getExamMode())){
		studentMarksListForMarksHistory = dao.getAStudentsMarksForOnline(bean);
	}else{
		studentMarksListForMarksHistory = dao.getAStudentsMarksForOffline(bean);
	}
	return studentMarksListForMarksHistory;
}


//to be deleted, api shifted to rest controller
//@RequestMapping(value = "/m/getMostRecentAssignmentResults", method = RequestMethod.POST, consumes= "application/json", produces = "application/json")
//	public ResponseEntity<Map<String,List>> mgetMostRecentAssignmentResults (@RequestBody StudentBean student) throws UnsupportedEncodingException{
//	
//	TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
//	
//	if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
//		Map<String,List> returnDataFromRedis = new HashMap<>();
//		return new ResponseEntity<Map<String,List>>(returnDataFromRedis, HttpStatus.OK);
//	}
//	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//	StudentMarksBean bean = new StudentMarksBean();
//
//	//String sapId = request.getParameter("sapId"); For testing
//	bean.setSapid(student.getSapid());
//	String mostRecentResultPeriod = dao.getMostRecentAssignmentResultPeriod();
//	//String declareDate = dao.getRecentExamDeclarationDate();
//	List<StudentMarksBean> studentMarksList =  dao.getAStudentsMostRecentAssignmentMarks(bean);
//	
//	Map<String,List> result_data = new HashMap<String, List>();
//
//	/* //Temp Hide start to be rmoved later by PS
//	StudentBean studentBean = dao.getSingleStudentsData(student.getSapid());
//	if("Diageo".equalsIgnoreCase(studentBean.getConsumerType())){
//		studentMarksList = new ArrayList<StudentMarksBean>();
//	}
//	//end */
//	
//	//modelnView.addObject("declareDate", declareDate);
//	result_data.put("studentMarksList", studentMarksList);
//		
//	
//	List center1 = 	new ArrayList<>();
//	center1.add(mostRecentResultPeriod);
//	result_data.put("mostRecentResultPeriod",center1);
//	
//	
//	List center2 = 	new ArrayList<>();
//	center2.add( studentMarksList.size());
//	result_data.put("size",center2);
//	
////	if(studentMarksList.size() == 0){
////		setError(request, "No Assignment Marks found for "+mostRecentResultPeriod);
////	}
//	
//	return new ResponseEntity<Map<String,List>>(result_data, HttpStatus.OK);
//
//}
//Added for SAS
@RequestMapping(value = "/student/getMostRecentResultsSaS",  method = {RequestMethod.GET, RequestMethod.POST})
public ModelAndView getMostRecentResultsSaS(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
	if(!checkSession(request, response)){
		redirectToPortalApp(response);
		return null;
	}
	
	ModelAndView modelnView = new ModelAndView("/examHome/singleStudentRecentMarksSaS");
	
	StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	StudentMarksBean bean = new StudentMarksBean();

	//String sapId = request.getParameter("sapId"); For testing
	String sapId = (String)request.getSession().getAttribute("userId");
	bean.setSapid(sapId);
	
	getPassFailStatusForExecutive(request, response);
	
	getStudentMarksHistoryForExecutive(request,response,sapId);
	
	String centerCode = dao.getStudentCenterDetails(sapId);

	
	if(centerCode != null && !"".equals(centerCode.trim())){
		ArrayList<CenterExamBean> centers = getCentersList();
		Map<String,CenterExamBean> centerMap = new LinkedHashMap<String,CenterExamBean>();
		
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean cBean = centers.get(i);
			centerMap.put(cBean.getCenterCode(), cBean);
		}
		modelnView.addObject("center", centerMap.get(centerCode));
	}else{
		CenterExamBean cBean = new CenterExamBean();
		cBean.setCenterName("Center Information Not Available");
		cBean.setAddress("Please contact head office to get your center information updated.");
		modelnView.addObject("center", cBean);
	}
	
	StudentExamBean student = dao.getSingleStudentsData(sapId);
	
	String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
	student.setProgramForHeader(programForHeader);
	request.getSession().setAttribute("studentExam", student);
	String mostRecentResultPeriod = "";
	String declareDate = "";
	List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
	 
		mostRecentResultPeriod = dao.getMostRecentResultPeriodForExecutive(student);
		declareDate = dao.getRecentExamDeclarationDateForExecutive(student);
		studentMarksList =  dao.getAStudentsMostRecentMarksForExecutive(bean);
	 
	
	modelnView.addObject("mostRecentResultPeriod", mostRecentResultPeriod);
	modelnView.addObject("declareDate", declareDate);
	modelnView.addObject("studentMarksList", studentMarksList);
	modelnView.addObject("size", studentMarksList.size());
	
	if(studentMarksList.size() == 0){
		setError(request, "No Marks Entries found for "+mostRecentResultPeriod);
	}
	
	return modelnView;
}
//Added for SAS end

//Added for RIA/RIA ALL/NV/NV ALL
      @RequestMapping(value = "/admin/markRIANVCasesForm",method = {RequestMethod.GET})
      public ModelAndView markRIANVCasesForm(@ModelAttribute StudentMarksBean studentMarks){
    	  ModelAndView mav = new ModelAndView("RIANVCases");
    	  mav.addObject("yearList", ACAD_YEAR_LIST);
    	  mav.addObject("monthList", ACAD_MONTH_LIST);
    	  mav.addObject("programList", getProgramList());
    	  mav.addObject("semList", semList);
    	  mav.addObject("studentMarks", studentMarks);
    	  mav.addObject("studentTypeList", tcsHelper.getConsumerTypesList());
    	  return mav;
      }
      @RequestMapping(value = "/admin/searchStudentRIANVCase",method = {RequestMethod.POST})
      public ModelAndView searchStudentRIANVCase(@ModelAttribute StudentMarksBean studentMarks,HttpServletRequest request){
    	  ModelAndView mav = new ModelAndView("RIANVCases");
    	  mav.addObject("yearList", ACAD_YEAR_LIST);
    	  mav.addObject("monthList", ACAD_MONTH_LIST);
    	  mav.addObject("studentTypeList", tcsHelper.getConsumerTypesList());
    	  mav.addObject("programList", getProgramList());
    	  mav.addObject("semList", semList);
    	  mav.addObject("studentMarks", studentMarks);
    	  int rowCount = 0;
    	  StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
    		StudentExamBean student = dao.getSingleStudentsData(studentMarks.getSapid());
    		List<StudentMarksBean> studentMarksList =  new ArrayList<StudentMarksBean>();
    		if("Online".equals(student.getExamMode())){
    			studentMarksList = dao.searchSingleStudentMarksforRiaNv(studentMarks, "Online");
    		}else{
    			studentMarksList = dao.searchSingleStudentMarksforRiaNv(studentMarks, "Offline");
    		}
    		if(studentMarksList != null && studentMarksList.size() > 0){
    			  rowCount = studentMarksList.size();
    		}
    		mav.addObject("rowCount", rowCount);
       	  	mav.addObject("studentMarksList", studentMarksList);
       	  	request.getSession().setAttribute("studentMarks",studentMarks);
    	  return mav;
      }
      
      @RequestMapping(value = "/admin/updateSubjectAsRIANV",method = {RequestMethod.POST})
      public ResponseEntity<HashMap<String, String>> updateSubjectAsRIANV(@RequestParam String subject,
    		  @RequestParam String status,
    		  @RequestParam String sem,
    		  @RequestParam String program,
    		  @RequestParam String year,
    		  @RequestParam String month,
    		  @RequestParam String sapid,
    		  @RequestParam String studentType,
    		  HttpServletRequest request) throws SQLException{
    	  StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
    	  HashMap<String, String> response = new  HashMap<String, String>();
    	  try{
    		  String lastModifiedBy = (String)request.getSession().getAttribute("userId");
    		 // dao.updateSingleSubjectRIANV(sapid,program,sem,subject,year,month,status);
    		  HashMap<String,StudentMarksBean> studentMarks = dao.getStudentMarksBeforeRIANV(sapid,year,month,program);
    		if(studentMarks.size()>0){
    			 for(Entry<String,StudentMarksBean> student:studentMarks.entrySet()){
        			 int j = dao.saveStudentMarksBeforeRIANV(student.getValue());
        			 if(j<0){
        				 	response.put("Status", "Fail"); 
        	       		  	return new ResponseEntity(response, HttpStatus.OK); 
        			 }
        		 }
    		}
    		 int rows=0;
    		 if(status.equalsIgnoreCase("Score")){
    			 status = dao.getStudentPreviousScore(sapid,year,month,program,subject);
    			 boolean state = NumberUtils.isParsable(status) ;
    			 
    			 if(state){
    				 rows = dao.updateSubjectToPreviousScore(sapid,program,sem,subject,year,month,status,studentType,lastModifiedBy);
    			 }
    			 
    		 }else{
    			 rows =  dao.updateSubjectAsRIANV(sapid,program,sem,subject,year,month,status,studentType,lastModifiedBy); 
    		 }
    		 if(rows>0){
    			response.put("Status", "Success"); 
       		  	response.put("writtenScore", status); 
       		  	return new ResponseEntity(response, HttpStatus.OK);
    		 }else{
    			response.put("Status", "Fail"); 
    			response.put("FailReason", "Unable to update written score.");
       		  	return new ResponseEntity(response,HttpStatus.OK);
    		 }
    		 	
    		 
    	  }catch(Exception e){
    		  
    		  response.put("Status", "Fail"); 
    		  return new ResponseEntity(response, HttpStatus.OK);
    	  }
    		
    	 
      }
      
      
      

  	
  	@RequestMapping(value = "/admin/insertABRecordsFormMBAWX", method = RequestMethod.GET)
  	public ModelAndView insertABRecordsFormMBAWX(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
  		ModelAndView modelnView = new ModelAndView("insertABRecords_mbawx");
  		modelnView.addObject("acadYear", ACAD_YEAR_LIST);
  		modelnView.addObject("acadMonth", ACAD_MONTH_LIST);

  		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
  		List<EMBABatchSubjectBean> batchList = dao.getBatchesForMBAWX();
  		Map<String, List<EMBABatchSubjectBean>> subjectList = new HashMap<String, List<EMBABatchSubjectBean>>();
  		
  		for (EMBABatchSubjectBean batch : batchList) {
  			List<EMBABatchSubjectBean> schedules = dao.getSubjectsForBatch(batch.getBatchId());
  			subjectList.put(batch.getBatchId(), schedules);
		}

  		request.getSession().setAttribute("batchListMBAWX", batchList);
  		modelnView.addObject("searchBean", new MettlResponseBean());
  		request.getSession().setAttribute("batchAndSubjectListMBAWX", new Gson().toJson(subjectList));
  		return modelnView;
  	}
    
  	@RequestMapping(value = "/admin/insertABReportMBAWX", method = {RequestMethod.GET})
  	public ModelAndView insertABReportMBAWX(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
  		ModelAndView modelnView = new ModelAndView("insertABRecords_mbawx");
  		modelnView.addObject("acadYear", ACAD_YEAR_LIST);
  		modelnView.addObject("acadMonth", ACAD_MONTH_LIST);
  		
  		List<TEEResultStudentDetailsBean> studentsList = (List<TEEResultStudentDetailsBean>) request.getSession().getAttribute("absentStudentList");
  		
  		String userId = (String) request.getSession().getAttribute("userId");
  		
  		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
  		
  		int i = 0;
  		
  		ArrayList<String> errorList = new ArrayList<>();

  		for (i = 0; i < studentsList.size(); i++) {
  			try{
  				TEEResultStudentDetailsBean bean = studentsList.get(i);
  				//Upsert AB Records
  				dao.UpdateABRecordsMBAWX(bean, userId);
  			}catch(Exception e){
  				int row = 1 + i;
  				errorList.add(Integer.toString(row));
  			}
  		}
  		if(errorList.size() == 0){
  			request.setAttribute("success","true");
  			request.setAttribute("successMessage",studentsList.size() +" rows out of "+ studentsList.size()+" inserted successfully.");
  		}else{
  			request.setAttribute("error", "true");
  			request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
  		}
  		modelnView.addObject("rowCount", studentsList != null ? studentsList.size() : 0);
  		modelnView.addObject("searchBean", request.getSession().getAttribute("searchBean"));
  		return modelnView;
  	}
  	

	@RequestMapping(value = "/admin/searchABRecordsToInsertMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchABRecordsToInsertMBAWX(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MettlResponseBean searchBean){
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("insertABRecords_mbawx");
		modelnView.addObject("acadYear", ACAD_YEAR_LIST);
  		modelnView.addObject("acadMonth", ACAD_MONTH_LIST);
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		List<TEEResultStudentDetailsBean> absentStudentList;
		
		if("100".equals(searchBean.getMax_marks())) {
			absentStudentList = dao.getABRecordsFor100MarksExamMBAWX(searchBean);
		} else {
			absentStudentList = dao.getABRecordsMBAWX(searchBean); // Absent records for offline are added manually by tee team.
		}
		
		request.getSession().setAttribute("searchBean", searchBean);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", absentStudentList != null ? absentStudentList.size() : 0);
		
		request.getSession().setAttribute("absentStudentList", absentStudentList);
		
		if(absentStudentList == null || absentStudentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}else{
			setSuccess(request, "Please download AB report for verification");
		}
		
		return modelnView;
	}
  	
  	@RequestMapping(value = "/admin/downloadABReportMBAWX", method = {RequestMethod.GET, RequestMethod.POST})
  	public ModelAndView downloadABReportMBAWX(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
  		List<TEEResultStudentDetailsBean> absentStudentList = (List<TEEResultStudentDetailsBean>)request.getSession().getAttribute("absentStudentList");
  		return new ModelAndView("absentReportExcelViewMBAWX", "absentStudentList", absentStudentList);
  	}
  	
  	
  	
//Added for RIA/RIA ALL/NV/NV ALL END
  	
    @RequestMapping(value = "/admin/enableScoreForNVRIA",method = {RequestMethod.POST})
    public ModelAndView enableScoreForNVRIA(@ModelAttribute StudentMarksBean studentMarks,HttpServletRequest request){
  	  ModelAndView mav = new ModelAndView("RIANVCases");
  	  mav.addObject("yearList", ACAD_YEAR_LIST);
  	  mav.addObject("monthList", ACAD_MONTH_LIST);
  	  mav.addObject("studentTypeList", tcsHelper.getConsumerTypesList());
  	  mav.addObject("programList", getProgramList());
  	  mav.addObject("semList", semList);
  	  mav.addObject("studentMarks", studentMarks);
  	  int rowCount = 0;
  	  StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
  	  
  		StudentExamBean student = dao.getSingleStudentsData(studentMarks.getSapid());
  		
  		List<StudentMarksBean> studentMarksList =  new ArrayList<StudentMarksBean>();
	  		if("Online".equals(student.getExamMode())){
	  			studentMarksList = dao.searchSingleStudentMarksforRiaNv(studentMarks, "Online");
	  		}else{
	  			studentMarksList = dao.searchSingleStudentMarksforRiaNv(studentMarks, "Offline");
	  		}
	  		if(studentMarksList != null && studentMarksList.size() > 0){
	  			  rowCount = studentMarksList.size();
	  		}
	  		
	  		
	  		//changes start from here
  		List<StudentMarksBean> updatedStudentMarksList =  new ArrayList<StudentMarksBean>();
  			for(StudentMarksBean m:studentMarksList) {
  					updatedStudentMarksList.add(dao.updateWrittenScoreDataByWrittenBeforeRIANV(m));
  				}
  		if(updatedStudentMarksList.size()>0) {
  			studentMarksList=updatedStudentMarksList;
  			rowCount=studentMarksList.size();
  			request.setAttribute("success", "true");
  			request.setAttribute("successMessage"," Update Result Status To Score Successfully");
  		}else {
  			request.setAttribute("error", "true");
  			request.setAttribute("errorMessage"," No Result Updated");
  		}
  		
  			mav.addObject("rowCount", rowCount);
     	  	mav.addObject("studentMarksList", studentMarksList);
     	  	request.getSession().setAttribute("studentMarks",studentMarks);
  	  return mav;
    }

    
    @RequestMapping(value="/admin/uploadProjectNotBookedForm" , method= {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView uploadABRecords(HttpServletRequest request,HttpServletResponse response,@ModelAttribute StudentMarksBean bean) {
    	ModelAndView model = new ModelAndView("insertProjectAbsentRecords");
    	model.addObject("bean",bean);
    	model.addObject("yearList",currentYearList);
		return model;
    	
    }
    
    @RequestMapping(value="/admin/insertNotBookedStudent" ,method = {RequestMethod.POST})
    public ModelAndView insertNotBookedStudent(HttpServletRequest request,HttpServletResponse response,@ModelAttribute StudentMarksBean bean) {
    
		ModelAndView model = new ModelAndView("insertProjectAbsentRecords");
		try {
		
			model.addObject("yearList", currentYearList);
			List<StudentMarksBean> finalListInserted = absentRecordsFactoryService.getExecuteByProductType(bean,"Project", subjectsUnderProject);
			bean = new StudentMarksBean();
			model.addObject("bean", bean);
			model.addObject("count", finalListInserted.size());
			request.getSession().setAttribute("projectNotBookedList", finalListInserted);
			if (finalListInserted.size() > 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage","Total Records Inserted is" + " " + finalListInserted.size() + "");
			} else {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "No Record Found To Insert");
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error Found While Inserting Project Not Booked Students" + errorMessage);
			return model;
		}
		return model;
	}
    
    @RequestMapping(value="/admin/downloadProjectNotBookedStudent", method = {RequestMethod.GET})
    public ModelAndView downloadProjectNotBookedStudent(HttpServletRequest request,HttpServletResponse response) {
    	List<StudentMarksBean> ProjectNotBooked =  (List<StudentMarksBean>) request.getSession().getAttribute("projectNotBookedList");
    	request.getSession().setAttribute("reportType", "ProjectNotBooked");
		return new ModelAndView("downloadReportForPassFail", "ProjectNotBooked", ProjectNotBooked);	
    }
    
}

