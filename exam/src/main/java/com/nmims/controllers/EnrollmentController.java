package com.nmims.controllers;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.dto.StudentProfileDetailsExamDto;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.StudentDetailsServiceInterface;

/**
 * Handles requests for the application home page.
 */
@Controller
public class EnrollmentController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	@Qualifier("studentProfileDetailsService")
	StudentDetailsServiceInterface studentDetailsService;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	private final int pageSize = 20;

	private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);
	
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	
	private ArrayList<String> programList = null;

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026")); 

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 

	
	private ArrayList<String> subjectList = null; 
	private ArrayList<CenterExamBean> centers = null; 
	private ArrayList<String> centersList = null;
	private HashMap<String, String> centerCodeNameMap = null; 
	private ArrayList<String> progStructListFromProgramMaster = null;
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {		
		
		subjectList = null;
		getSubjectList();
		
		
		programList = null;
		getProgramList();
		
		centerCodeNameMap = null;
		getCenterCodeNameMap();
		
		progStructListFromProgramMaster = null;
		getSubjectList();
		
		
		return null;
	}

	public ArrayList<String> getCentersList(){
		//if(this.centers == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
			
			centersList = new ArrayList<>();
			for (int i = 0; i < centers.size(); i++) {
				centersList.add(centers.get(i).getCenterCode());
			}
		//}
		return centersList;
	}
	
	public HashMap<String, String> getCenterCodeNameMap(){
		if(this.centerCodeNameMap == null || this.centerCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<CenterExamBean> centers = dao.getAllCenters();
			centerCodeNameMap = new HashMap<>();
			for (int i = 0; i < centers.size(); i++) {
				CenterExamBean cBean = centers.get(i);
				centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
			}
		}
		return centerCodeNameMap;
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
	
	public ArrayList<String> getProgStructListFromProgramMaster(){
		if(this.progStructListFromProgramMaster == null){
			DashboardDAO dao = (DashboardDAO)act.getBean("dashboardDAO");
			this.progStructListFromProgramMaster = dao.getProgStructListFromProgramMaster();
		}
		return progStructListFromProgramMaster;
	}
	
	
	@RequestMapping(value = "/admin/uploadStudentsMasterForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadStudentsMasterForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		
		return "uploadStudentMaster";
	}
	
	@RequestMapping(value = "/admin/uploadStudentsMaster", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadStudentsMaster(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadStudentMaster");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readStudentMasterExcel(fileBean, getProgramList(), getSubjectList(),getCentersList(), userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentExamBean> studentList = (ArrayList<StudentExamBean>)resultList.get(0);
				List<StudentExamBean> errorBeanList = (ArrayList<StudentExamBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpsertStudentMaster(studentList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",studentList.size() +" rows out of "+ studentList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting marks records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadCentersForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadCentersForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		
		return "uploadCenters";
	}
	
	@RequestMapping(value = "/admin/uploadCenters", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadCenters(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadCenters");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readCentersExcel(fileBean, userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<CenterExamBean> centerList = (ArrayList<CenterExamBean>)resultList.get(0);
				List<CenterExamBean> errorBeanList = (ArrayList<CenterExamBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpsertCenters(centerList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",centerList.size() +" rows out of "+ centerList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadStudentsImageForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadStudentsImageForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		
		return "uploadStudentImage";
	}
	
	
	@RequestMapping(value = "/admin/uploadStudentsImage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadStudentsImage(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadStudentImage");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readStudentImageExcel(fileBean, userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentExamBean> studentList = (ArrayList<StudentExamBean>)resultList.get(0);
				List<StudentExamBean> errorBeanList = (ArrayList<StudentExamBean>)resultList.get(1);
				
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				ArrayList<String> errorList = dao.batchUpsertStudentImage(studentList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",studentList.size() +" rows out of "+ studentList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting image records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadRegistrationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadRegistrationForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");
		
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		
		return "uploadRegistration";
	}
	
	@RequestMapping(value = "/admin/uploadRegistration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadRegistration(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
			ModelAndView modelnView = new ModelAndView("uploadRegistration");
			try{
				String userId = (String)request.getSession().getAttribute("userId");
				ExcelHelper excelHelper = new ExcelHelper();
				ArrayList<List> resultList = excelHelper.readRegistrationExcel(fileBean, getProgramList(), getSubjectList(), userId);
				//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);
				
				List<StudentMarksBean> studentList = (ArrayList<StudentMarksBean>)resultList.get(0);
				List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);
				//Check if there were errors in file
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
			
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				
				HashMap<String, String> studentProramMap = dao.getStudentsProgramMap();
				HashMap<String, String> studentCentersMap = dao.getStudentsCentersMap();
				HashMap<String, String> studentRegistrationMonthYearMap = dao.getStudentRegistrationMonthYearMap();
				errorBeanList = new ArrayList<>(); 
				for (int i = 0; i < studentList.size(); i++) {
					StudentMarksBean bean = studentList.get(i);
					
					if(!studentProramMap.containsKey(bean.getSapid())){
						bean.setErrorMessage(bean.getErrorMessage()+"Student not found in Master Database. SAPID:"+bean.getSapid());
						bean.setErrorRecord(true);
						errorBeanList.add(bean);
						continue;
					}
					
					String programInMasterDB = studentProramMap.get(bean.getSapid());
					String programInFile = bean.getProgram();
					if(!programInMasterDB.equals(programInFile)){
						bean.setErrorMessage(bean.getErrorMessage()+"Program in Student Master not matching with program in File. Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM in File: "+bean.getProgram() + " Program in Master: "+programInMasterDB);
						bean.setErrorRecord(true);
						errorBeanList.add(bean);
					}
					
				
					String centerInMasterDB = studentCentersMap.get(bean.getSapid());
					String centerInFile = bean.getCentercode();
					if(!centerInMasterDB.equals(centerInFile)){
						bean.setErrorMessage(bean.getErrorMessage()+"IC Mismatch. Invalid IC for record with SAPID:"+bean.getSapid()+ " & IC: "+bean.getCenterName());
						bean.setErrorRecord(true);
						errorBeanList.add(bean);
					}
					
					String sapIdAndYearMonthToRegister = bean.getSapid().trim() + bean.getYear().trim() + bean.getMonth().trim();
					if(studentRegistrationMonthYearMap.containsKey(sapIdAndYearMonthToRegister)){
						bean.setErrorMessage(bean.getErrorMessage()+"Student has already registered for given Year and Month. SAPID:"+bean.getSapid() + " : " + bean.getMonth() + "-" + bean.getYear());
						bean.setErrorRecord(true);
						errorBeanList.add(bean);
					}
					
				}
				//Check if there was error w.r.t. mismatch of program or student not being present in DB
				if(errorBeanList.size() > 0){
					request.setAttribute("errorBeanList", errorBeanList);
					return modelnView;
				}
				
				ArrayList<String> errorList = dao.batchUpsertRegistration(studentList);
				
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",studentList.size() +" rows out of "+ studentList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting registration records.");

			}
			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchStudentsRegistrationsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchStudentsRegistrationsForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentMarksBean student = new StudentMarksBean();
		m.addAttribute("student",student);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		
		return "searchStudentRegistrations";
	}
	
	@RequestMapping(value = "/admin/searchStudentRegistraions", method = RequestMethod.POST)
	public ModelAndView searchStudentRegistraions(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean student){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchStudentRegistrations");
		request.getSession().setAttribute("studentRegistration", student);
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page = dao.getStudentRegistrationsPage(1, pageSize, student, getAuthorizedCodes(request));
		List<StudentMarksBean> studentList = page.getPageItems();
		
		modelnView.addObject("studentList", studentList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("student", student);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		
		if(studentList == null || studentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/searchStudentRegistraionsPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchStudentRegistraionsPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("searchStudentRegistrations");
		StudentMarksBean student = (StudentMarksBean)request.getSession().getAttribute("studentRegistration");
		
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentMarksBean> page = dao.getStudentRegistrationsPage(pageNo, pageSize, student, getAuthorizedCodes(request));
		List<StudentMarksBean> studentList = page.getPageItems();
		
		modelnView.addObject("studentList", studentList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("student", student);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		
		if(studentList == null || studentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadStudentRegistrations", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentRegistrations(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentMarksBean student = (StudentMarksBean)request.getSession().getAttribute("studentRegistration");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	
		Page<StudentMarksBean> page = dao.getStudentRegistrationsPage(1, Integer.MAX_VALUE, student, getAuthorizedCodes(request));
		List<StudentMarksBean> studentList = page.getPageItems();
		
		return new ModelAndView("studentRegistrationsExcelView","studentList",studentList);
	}
	
	
	@RequestMapping(value = "/admin/searchStudentsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchStudentForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentExamBean student = new StudentExamBean();
		m.addAttribute("student",student);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("progStructListFromProgramMaster", getProgStructListFromProgramMaster());
		return "searchStudent";
	}
	
	@RequestMapping(value = "/admin/searchStudents", method = RequestMethod.POST)
	public ModelAndView searchStudents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("searchStudent");
		request.getSession().setAttribute("studentExam", student);

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		Page<StudentExamBean> page = dao.getStudentPage(1, pageSize, student, getAuthorizedCodes(request));
		List<StudentExamBean> studentList = page.getPageItems();
		/*
		 * for(StudentBean studentBean:studentList) {
			try{
				isStudentValid(studentBean,studentBean.getSapid());
			}catch (Exception e) {
			}
		}
		*/
		request.getSession().setAttribute("studentList", studentList);
		
		modelnView.addObject("studentList", studentList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("student", student);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("monthList", ACAD_MONTH_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		
		modelnView.addObject("progStructListFromProgramMaster", getProgStructListFromProgramMaster());
		
		if(studentList == null || studentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadStudents", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudents(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		StudentMarksDAO sMarksDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//StudentBean student = sMarksDao.getSingleStudentsData(userId);
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	
		Page<StudentExamBean> page = dao.getStudentPage(1, Integer.MAX_VALUE, student, getAuthorizedCodes(request));
		List<StudentExamBean> studentList = page.getPageItems();
		
		return new ModelAndView("studentsExcelView","studentList",studentList);
	}
	
	
	@RequestMapping(value = "/admin/searchStudentsPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchStudentsPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchStudent");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//List<StudentMarksBean> studentMarksList = dao.getStudentMarksPage(pageNo, pageSize, studentMarks);
		
		Page<StudentExamBean> page = dao.getStudentPage(pageNo, pageSize, student, getAuthorizedCodes(request));
		List<StudentExamBean> studentList = page.getPageItems();
		modelnView.addObject("studentList", studentList);
		modelnView.addObject("page", page);
		
		modelnView.addObject("student", student);
		modelnView.addObject("rowCount", page.getRowCount());
		
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(studentList == null || studentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	/**
	 * Attaching passed parameters as model attributes and returning view
	 * @param sapid - Student No. of the student
	 * @param sem - Enrolled semester of the student
	 * @param model - Model Object to add attributes to the view
	 * @return view
	 */
	@GetMapping(value="/admin/editStudent")
	public String editStudent(@RequestParam Long sapid, @RequestParam Integer sem, Model model) {
		model.addAttribute("studentSapid", sapid);
		model.addAttribute("studentSem", sem);
		logger.info("editStudent View with Model attributes: {}", model.asMap().toString());
		return "editStudent";
	}
	
	@Deprecated
//	@RequestMapping(value = "/admin/editStudent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editStudent(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView modelnView = new ModelAndView("addStudent");
		String sapid = request.getParameter("sapid");
		String sem = request.getParameter("sem");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = dao.findStudentBySAPnSem(sapid,sem);
		/*student.setNewValidityEndMonth(student.getValidityEndMonth());
		student.setNewValidityEndYear(student.getValidityEndYear());
		if(student.getOldValidityEndMonth() == null || student.getOldValidityEndMonth() == ""){
		student.setOldValidityEndMonth(student.getValidityEndMonth());
		student.setOldValidityEndYear(student.getValidityEndYear());
	}*/
		//modelnView.addObject("student", student);
		m.addAttribute("student",student);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getCentersList());
		m.addAttribute("centerList", getCenterCodeNameMap());
		request.setAttribute("edit", "true");
		request.setAttribute("studentExam", student);
		m.addAttribute("progStructListFromProgramMaster", getProgStructListFromProgramMaster());

		return modelnView;
	}
	
	/**
	 * Update student details and redirect view
	 * @param request - HttpServletRequest
	 * @param model - Object to add attributes to the view
	 * @param redirectAttributes - Object to add attributes to the view on redirect
	 * @param studentProfileDetails - Bean containing details of the student
	 * @param bindingResult - validation result of ModelAttribute Object
	 * @return redirect to viewStudent, in case of error return to editStudent view
	 */
	@PostMapping(value = "/admin/updateStudent")
	public String updateStudent(HttpServletRequest request, Model model, final RedirectAttributes redirectAttributes,
								@Valid @ModelAttribute StudentProfileDetailsExamDto studentProfileDetails, BindingResult bindingResult) {
		try {
			if(bindingResult.hasErrors()) {
				String errorMessage = bindingResult.getFieldErrors()		//All field related errors (of ModelAttribute Object) stored as a List
													.stream()
													.map(FieldError::getDefaultMessage)			//Get message from the FieldError Object of the field
													.collect(Collectors.joining("<br/>"));		//Join the messages with a </br> break tag
				
				throw new IllegalArgumentException(errorMessage);		//throw Exception with detail message retrieved from the BindingResult Object
			}
			
			String userId = (String) request.getSession().getAttribute("userId");
			logger.info("Getting userId of the user: {} from stored session attributes and updating profile details of Student: {}", 
						userId, studentProfileDetails.getSapid());
			
			studentDetailsService.updateStudentProfileDetails(studentProfileDetails, userId);
			logger.info("Successfully updated profile details of Student: {}", studentProfileDetails.getSapid());
			
			//Adding Success Message to display to the user
			redirectAttributes.addFlashAttribute("status", "success");
			redirectAttributes.addFlashAttribute("statusMessage", "Details of student: " + studentProfileDetails.getSapid() + " updated successfully!");
			return "redirect:/admin/viewStudent/" + studentProfileDetails.getSapid() + "/" + studentProfileDetails.getSem();
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			model.addAttribute("studentSapid", studentProfileDetails.getSapid());
			model.addAttribute("studentSem", studentProfileDetails.getSem());
			
			//Add Error Message to display to the user
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while trying to update Student details.";		//IllegalArgumentException thrown on BindingResult error
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
			logger.error("Error Message shown to user: {} Exception thrown: {}", errorMessage, ex.toString());
			
			return "editStudent";
		}
	}

	@Deprecated
//	@RequestMapping(value = "/admin/updateStudent", method = RequestMethod.POST)
	public ModelAndView updateStudent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student, Model m){

		ModelAndView modelnView = new ModelAndView("student");
		modelnView.addObject("centerList", getCenterCodeNameMap());
		String userId = (String)request.getSession().getAttribute("userId");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//StudentBean searchBean = dao.getSingleStudentsData(userId);
		StudentExamBean oldStudentDeatilsFromDatabase = dao.getSingleStudentsData(student.getSapid());
		try {

			if(!student.getProgram().equals(student.getOldProgram())){
				student.setProgramChanged("Y");
			}
			/*//to maintain validity history of student
			
			if(!oldStudentDeatilsFromDatabase.getValidityEndMonth().equals(student.getNewValidityEndMonth()) || !oldStudentDeatilsFromDatabase.getValidityEndYear().equals(student.getNewValidityEndYear())){
				student.setOldValidityEndMonth(oldStudentDeatilsFromDatabase.getValidityEndMonth());
				student.setOldValidityEndYear(oldStudentDeatilsFromDatabase.getValidityEndYear());
				dao.updateStudentValidityHistory(student,userId);
			}
			student.setValidityEndMonth(student.getNewValidityEndMonth());
			student.setValidityEndYear(student.getNewValidityEndYear());
			
			//end
*/			
			student.setCenterName(getCenterCodeNameMap().get(student.getCenterCode()));

			String errorMessage = null;
			String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();

			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");

			// format year and Month into date 
			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
			String fullFormatedDate = fulldateFormat.format(dateR);

			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student 

			// bypass student before Jul2014 as Student record not present in Salesforce  //
			if(enrollmentDate.after(salesforceUseStartDate))
			{
				errorMessage = salesforceHelper.updateSalesforceProfile(student,userId);//This is to update Students Shipping Address in SFDC//
			}

			if(errorMessage == null || "".equals(errorMessage)) 
			{
				
				dao.updateStudent(student, userId);
				
				String previousValidity = oldStudentDeatilsFromDatabase.getValidityEndMonth() + "-" + oldStudentDeatilsFromDatabase.getValidityEndYear();
				String newValidity = student.getValidityEndMonth() + "-" + student.getValidityEndYear();
				if(!newValidity.equals(previousValidity)){
					student.setOldValidityEndMonth(oldStudentDeatilsFromDatabase.getValidityEndMonth());
					student.setOldValidityEndYear(oldStudentDeatilsFromDatabase.getValidityEndYear());
					
					student.setNewValidityEndMonth(student.getValidityEndMonth());
					student.setNewValidityEndYear(student.getValidityEndYear());
					
					dao.updateStudentValidityHistory(student,userId);
				}
				
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Student updated successfully");
				return viewSingleStudentDetail(request, response, m);
			}else
			{
				setError(request, errorMessage);
				return editStudent(request, response, m);
			}
		} catch (Exception e) {
			
			setError(request, e.getMessage());
			return editStudent(request, response, m);
		}
	}
	
	/**
	 * Fetch and display student details
	 * @param sapid - Student No. of the student
	 * @param sem - Enrolled semester of the student
	 * @param model - Object to add attributes to the view
	 * @return displayStudentDetails view
	 */
	@GetMapping(value="/admin/viewStudent/{sapid}/{sem}")
	public String viewStudent(@PathVariable Long sapid, @PathVariable Integer sem, Model model) {
		model.addAttribute("studentSapid", sapid);
		model.addAttribute("studentSem", sem);
		try {
			logger.info("Fetching Profile details of Student with sapid: {} and enrolled sem: {}", sapid, sem);
			StudentProfileDetailsExamDto studentDetailsBean = studentDetailsService.viewStudentProfileDetails(sapid, sem);
			model.addAttribute("studentDetails", studentDetailsBean);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error occured while trying to fetch profile details for Student: {} Exception thrown: {}", sapid, ex.toString());
			
			//Add Error Message to display to the user
			model.addAttribute("status", "danger");
			model.addAttribute("statusMessage", "Unable to fetch Student Details. Please refresh the page or try again in some time.");
		}
		
		return "displayStudentDetails";
	}
	
	@RequestMapping(value = "/admin/editStudentRegistration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editStudentRegistration(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView modelnView = new ModelAndView("addStudentRegistration");
		String sapid = request.getParameter("sapid");
		String sem = request.getParameter("sem");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = dao.findRegistrationBySAPnSem(sapid, sem);
		modelnView.addObject("student", student);
		m.addAttribute("student",student);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());

		request.setAttribute("edit", "true");
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/updateStudentRegistration", method = RequestMethod.POST)
	public ModelAndView updateStudentRegistration(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentExamBean student){

		ModelAndView modelnView = new ModelAndView("addStudentRegistration");
		String userId = (String)request.getSession().getAttribute("userId");
		
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		try {

			dao.updateStudentRegistration(student, userId);
			//student = dao.findRegistrationBySAPnSem(student.getSapid(), student.getSem());
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Student updated successfully");
			modelnView.addObject("programList", getProgramList());
			modelnView.addObject("yearList", yearList);
			modelnView.addObject("student", student);
			request.setAttribute("edit", "true");
			return modelnView;
		} catch (Exception e) {
			
			
			setError(request, "Error in updating registration");
			modelnView = new ModelAndView("home");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/deleteStudentRegistration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteStudentRegistration(HttpServletRequest request, HttpServletResponse response, Model m){
		try{
			String sapid = request.getParameter("sapid");
			String sem = request.getParameter("sem");
			
			String id = request.getParameter("id");
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			dao.deleteRegistration(sapid, sem);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}

		return searchStudentRegistraionsPage(request,response);
	}
	
	//Use /viewStudent API instead to display the student details
	@Deprecated
//	@RequestMapping(value = "/admin/viewSingleStudentDetail", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewSingleStudentDetail(HttpServletRequest request, HttpServletResponse response, Model m){
		try{
			String sapid = request.getParameter("sapid");
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		    StudentExamBean studentDetail = dao.studentDetails(sapid);
			m.addAttribute("studentDetail",studentDetail);
		
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in obtaining Record.");
		}

		return new ModelAndView ("studentDetails");
	}

	
	@RequestMapping(value = "/m/upsertCenterInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
	public ResponseEntity<HashMap<String, String>> upsertCenterInfo(@RequestBody CenterExamBean bean) throws Exception {
	
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        List<CenterExamBean> centerList = new  ArrayList<CenterExamBean>();
        HashMap<String, String> result = new HashMap<String, String>();
       
        try {
        	if(StringUtils.isEmpty(bean.getCenterCode()) 
        			|| StringUtils.isEmpty(bean.getCenterName())
        			|| StringUtils.isEmpty(bean.getSfdcId()) 
        			|| StringUtils.isEmpty(bean.getAddress())
        			|| StringUtils.isEmpty(bean.getState()) 
        			|| StringUtils.isEmpty(bean.getCity())
        			|| StringUtils.isEmpty(bean.getLc())
        			|| StringUtils.isEmpty(bean.getActive())){
        	
        		result.put("code", "422");
        		result.put("message", "Parameter missing value");
        		 return  new ResponseEntity<>(result, headers,  HttpStatus.UNPROCESSABLE_ENTITY);
        	}
     
        centerList.add(bean);
        StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
        /*added to update student center name in students table in case of center name change
         */ 
        
        try{
        CenterExamBean centerDetail = dao.findSingleCenterDetails(bean.getCenterCode());
        if(centerDetail!=null){
        	if(!StringUtils.isBlank(centerDetail.getCenterName())){
            	if(!centerDetail.getCenterName().equalsIgnoreCase(bean.getCenterName())){
            		int countOfRowUpdated = dao.updateStudentsCenterName(bean.getCenterCode(), bean.getCenterName());
            	}
            }
        }
        }catch(Exception e){
        	
        }
		ArrayList<String> errorList = dao.batchUpsertCenters(centerList);
		
		if(errorList.size() == 0){
			result.put("code", "200");
    		result.put("message", "Success");
	        return  new ResponseEntity<>(result, headers,  HttpStatus.OK);
		}else{
			result.put("code", "500");
    		result.put("message", "Unsuccessful update!");
			 return  new ResponseEntity<>(result, headers,  HttpStatus.UNPROCESSABLE_ENTITY);
		}
        
        
          }
          catch (Exception i)
          {
          }
        result.put("code", "500");
 		result.put("message", "Unsuccessful update");
	    return  new ResponseEntity<>(result, headers,  HttpStatus.UNPROCESSABLE_ENTITY);
}
}

