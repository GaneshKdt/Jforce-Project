package com.nmims.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimeTableBeanAPIResponse;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class TimeTableController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	/*
	 * @Value("#{'${CORPORATE_CENTERS}'.split(',')}") private List<String>
	 * corporateCenterList;
	 */
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 
	private final int pageSize = 20;
	private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> timeTableYearList; 
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Jan","Feb","Mar","Apr","Jun","Jul","Sep","Dec")); 

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();
	private HashMap<String, StudentExamBean> sapidStudentMap = new HashMap<>();
	
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
		
		sapidStudentMap = null;
		getAllValidStudents();
		
		programAndProgramStructureAndSubjectsMap = null;
		getProgramSubjectMappingMap();
		
		return null;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		
		return 	this.programSubjectMappingList;
	}

	public List<String> getAllCorporateNamesList(){
			List<String> corporateList= studentMarksDAO.getAllCorporateNames();
		return 	corporateList;
	}
	
	public HashMap<String, StudentExamBean> getAllValidStudents(){
		if(this.sapidStudentMap == null || this.sapidStudentMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<StudentExamBean> allValidStudent = eDao.getAllvalidStudents();
			sapidStudentMap = new HashMap<>();
			
			for (int i = 0; i < allValidStudent.size(); i++) {
				sapidStudentMap.put(allValidStudent.get(i).getSapid(), allValidStudent.get(i));
			}
		}
		
		return sapidStudentMap;
	}
	
	public HashMap<String, ArrayList<String>> getProgramSubjectMappingMap(){
		if(this.programAndProgramStructureAndSubjectsMap == null || this.programAndProgramStructureAndSubjectsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = eDao.getProgramSubjectMappingList();

			programAndProgramStructureAndSubjectsMap = new HashMap<>();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);
				String program = bean.getProgram();
				String programStrucutre = bean.getPrgmStructApplicable();
				String key = program + "|" + programStrucutre;

				if(programAndProgramStructureAndSubjectsMap.containsKey(key)){
					ArrayList<String> subjectsUnderProgram = programAndProgramStructureAndSubjectsMap.get(key);
					subjectsUnderProgram.add(bean.getSubject());
				}else{
					ArrayList<String> subjectsUnderProgram = new ArrayList<>();
					subjectsUnderProgram.add(bean.getSubject());
					programAndProgramStructureAndSubjectsMap.put(key,subjectsUnderProgram);
				}

			}


		}
		return programAndProgramStructureAndSubjectsMap;
	}
	
	@RequestMapping(value = "/uploadTimetableForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadTimetableForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView mav = new ModelAndView("uploadTimeTable");
		mav.addObject("fileBean", new FileBean());
		mav.addObject("yearList", yearList);
		mav.addObject("corporateCenterList",getAllCorporateNamesList());

		return mav;
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

	@RequestMapping(value = "/timeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView timeTable(HttpServletRequest request, HttpServletResponse response, Model m) {

		ModelAndView modelnView = new ModelAndView("timeTable");

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String mostRecentTimetablePeriod = dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = dao.getTimetableList();
		modelnView.addObject("timeTableList", timeTableList);
		request.setAttribute("timeTableList", timeTableList);
		modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);

		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		request.setAttribute("programTimetableMap", treeMap);

		return modelnView;
	}

	@RequestMapping(value = "/studentTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView studentTimeTable(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
				
		ModelAndView modelnView = new ModelAndView("studentTimeTable");
		boolean isCorporate = false;
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		HashMap<String,String> getCorporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();
		if(getCorporateCenterUserMapping.containsKey(student.getSapid())){
			isCorporate = true;
		}
		String mostRecentTimetablePeriod; // = dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = dao.getStudentTimetableList(student,isCorporate);
		modelnView.addObject("timeTableList", timeTableList);
		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		
		String examYear = "";
		String examMonth = "";
		
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			examYear = bean.getExamYear();
			examMonth = bean.getExamMonth();
			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		request.setAttribute("programTimetableMap", treeMap);

		return modelnView;
	}
	/*@RequestMapping(value = "/m/studentTimeTable" , method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<TimeTableBeanAPIResponse> mstudentTimeTable(@RequestBody StudentBean postInput) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json"); 
	    
	    StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean isCorporate = false;

		HashMap<String,String> getCorporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();
		if(getCorporateCenterUserMapping.containsKey(postInput.getSapid())){
			isCorporate = true;
		}
		String mostRecentTimetablePeriod; // = dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = dao.getStudentTimetableList(postInput,isCorporate);
		TimeTableBeanAPIResponse response = new TimeTableBeanAPIResponse();
		response.settimeTableList(timeTableList);
		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		
		String examYear = "";
		String examMonth = "";
		
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			examYear = bean.getExamYear();
			examMonth = bean.getExamMonth();
			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		response.setmostRecentTimetablePeriod(mostRecentTimetablePeriod);
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		response.settreeMap(treeMap);		
		return new ResponseEntity(response, headers, HttpStatus.OK);
	}
	*/
	
	@RequestMapping(value = "/adminTimeTableForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String adminTimeTableForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		TimetableBean bean = new TimetableBean();
		m.addAttribute("bean", bean);
		m.addAttribute("yearList", timeTableYearList);
		m.addAttribute("corporateCenterList",getAllCorporateNamesList());
		return "adminTimeTable";
	}
	
	@RequestMapping(value = "/adminTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView adminTimeTable(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		String examYear = request.getParameter("examYear");
		String examMonth = request.getParameter("examMonth");
		String corporateType = request.getParameter("corporateType");	
		ModelAndView modelnView = new ModelAndView("timeTable");

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String mostRecentTimetablePeriod; //= dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
		
		//added to show corporate timetable in view timetable page
		if(corporateType.equalsIgnoreCase("All")){
			timeTableList = dao.getAdminTimetableList(examYear, examMonth);
		}else{
			timeTableList = dao.getAdminTimetableList(examYear, examMonth, corporateType);
		}
		
		modelnView.addObject("timeTableList", timeTableList);
		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		
		
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);

			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		request.setAttribute("programTimetableMap", treeMap);

		return modelnView;
	}


	

	@RequestMapping(value = "/uploadTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadTimeTable(@ModelAttribute FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadTimeTable");
		try{
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readTimeTableExcel(fileBean, getProgramList(), getSubjectList(), userId);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<TimetableBean> timeTableList = (ArrayList<TimetableBean>)resultList.get(0);
			List<TimetableBean> errorBeanList = (ArrayList<TimetableBean>)resultList.get(1);

			

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			//Use same method for corporates//
			ArrayList<String> errorList = dao.batchUpdateTimeTable(timeTableList,fileBean);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",timeTableList.size() +" rows out of "+ timeTableList.size()+" inserted successfully.");
			}else{
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting Time table rows.");

		}
		modelnView.addObject("fileBean", new FileBean());
		modelnView.addObject("yearList", yearList);
		return modelnView;
	}

	@RequestMapping(value = "/viewTimetableNotice",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewTimetableNotice(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("timeTableNotice");
		return modelnView;
	}

	@RequestMapping(value = "/uploadAssignmentStatusForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadAssignmentStatusForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);

		return "uploadAssignmentStatus";
	}

	@RequestMapping(value = "/uploadAssignmentStatus", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadAssignmentStatus(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadAssignmentStatus");
		try{
			request.getSession().setAttribute("subjectMappingErrorBeanList", null);
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readAssignmentStatusExcel(fileBean, getProgramList(), getSubjectList(), userId);

			List<AssignmentStatusBean> assignmentStatusList = (ArrayList<AssignmentStatusBean>)resultList.get(0);
			List<AssignmentStatusBean> errorBeanList = (ArrayList<AssignmentStatusBean>)resultList.get(1);

			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}


			List<AssignmentStatusBean> subjectMappingErrorBeanList = new ArrayList<AssignmentStatusBean>();
			List<AssignmentStatusBean> subjectMappingSuccessBeanList = new ArrayList<AssignmentStatusBean>();
			HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = getProgramSubjectMappingMap();
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			
			for (int i = 0; i < assignmentStatusList.size(); i++) {
				AssignmentStatusBean bean = assignmentStatusList.get(i); 
				String subject = bean.getSubject();
				String sapid = bean.getSapid();
				String validity = "";
				
				HashMap<String, StudentExamBean> sapidStudentMap = getAllValidStudents();
				StudentExamBean student = sapidStudentMap.get(sapid);
				/*StudentBean student = eDao.getSingleStudentsData(sapid);
				if(student == null){
					bean.setErrorRecord(true);
					bean.setErrorType("Student Details Not Available");
					bean.setRowNumber((i+2)+"");
					bean.setErrorMessage(bean.getErrorMessage()+" Error : Student details for " + sapid + " not available in system.");
				}else{
					validity = student.getValidityEndMonth()+"-"+student.getValidityEndYear();
					student = eDao.getSingleStudentWithValidity(sapid);
				}*/
				if(student == null){
					bean.setErrorRecord(true);
					bean.setErrorType("Student Validity Expired");
					bean.setRowNumber((i+2)+"");
					bean.setErrorMessage(bean.getErrorMessage()+" Error : Student " + sapid + "'s Validity of " + validity +" has expired.");
				}else{
					String program = student.getProgram();
					String programStrucutre = student.getPrgmStructApplicable();
					String key = program + "|" + programStrucutre;

					ArrayList<String> subjectsUnderProgram = programAndProgramStructureAndSubjectsMap.get(key);
					if(!subjectsUnderProgram.contains(subject)){
						bean.setErrorRecord(true);
						bean.setErrorType("Incorrect Enrollment");
						bean.setRowNumber((i+2)+"");
						bean.setErrorMessage(bean.getErrorMessage()+" Error : " + subject + " is not applicable for Student " 
								+ sapid + " who has " + program + " Program and "+ programStrucutre + " Program Structure ");
					}else{
						boolean hasAlreadyPassed = eDao.checkIfStudentAlreadyPassed(bean);
						if(hasAlreadyPassed){
							bean.setErrorRecord(true);
							bean.setErrorType("Subject Already Passed");
							bean.setRowNumber((i+2)+"");
							bean.setErrorMessage(bean.getErrorMessage()+" Error : Student " + sapid + " has already cleared " + subject +".");
						}else{
							List<StudentMarksBean> registrationList = eDao.getRegistrations(sapid);
							if(registrationList != null && registrationList.size() != 0){
								int lastSem = 1;
								for (int j = 0; j < registrationList.size(); j++) {
									StudentMarksBean regBean = registrationList.get(j);
									int sem = Integer.parseInt(regBean.getSem());
									if(sem >= lastSem){
										lastSem = sem;
									}
								}
								
								//ArrayList<String> eligibleSubjectsList = eDao.getSubjectsForStudents(student, lastSem);
								ArrayList<String> eligibleSubjectsList = getEligibleSubject(student.getProgram(), student.getPrgmStructApplicable(), lastSem, student.getIsLateral());
								if(eligibleSubjectsList != null && eligibleSubjectsList.size() > 0 && (!eligibleSubjectsList.contains(subject))){
									bean.setErrorRecord(true);
									bean.setErrorType("Subject from Future Sem");
									bean.setRowNumber((i+2)+"");
									bean.setErrorMessage(bean.getErrorMessage()+" Error : " + subject + " is from future semesters.");
								}
								
							}
						}
					}
				}

				if(bean.isErrorRecord()){
					subjectMappingErrorBeanList.add(bean);
				}else{
					subjectMappingSuccessBeanList.add(bean);
				}

				if(i % 1000 == 0){
				}
			}

			
			request.getSession().setAttribute("subjectMappingSuccessBeanList", subjectMappingSuccessBeanList);
			if(subjectMappingErrorBeanList.size() > 0){
				request.setAttribute("subjectMappingErrorBeanList", subjectMappingErrorBeanList);
				request.getSession().setAttribute("subjectMappingErrorBeanList", subjectMappingErrorBeanList);
				return modelnView;
			}

			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<String> errorList = dao.batchUpdateAssignmentStatus(assignmentStatusList);
			//ArrayList<String> errorList = new ArrayList<>();

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",assignmentStatusList.size() +" rows out of "+ assignmentStatusList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting Assignment Status rows.");

		}

		return modelnView;
	}

	private ArrayList<String> getEligibleSubject(String program, String prgmStructApplicable, int lastSem, String isLateral) {
		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> eligibleSubject = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);
			if(program.equals(bean.getProgram()) && prgmStructApplicable.equals(bean.getPrgmStructApplicable())){
				int sem = Integer.parseInt(bean.getSem());
				if(sem <= lastSem){
					if("Y".equals(isLateral) ){
						if(sem >= 3){
						eligibleSubject.add(bean.getSubject());
						}
					}else{
						eligibleSubject.add(bean.getSubject());
					}
					
				}
			}
			
		}
		return eligibleSubject;
	}

	@RequestMapping(value = "/downloadAsignmentUploadErrorReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAsignmentUploadErrorReport(HttpServletRequest request, HttpServletResponse response) {
		List<AssignmentStatusBean> subjectMappingErrorBeanList =  (ArrayList<AssignmentStatusBean>)request.getSession().getAttribute("subjectMappingErrorBeanList");

		return new ModelAndView("asignmentUploadErrorReportExcelView","subjectMappingErrorBeanList",subjectMappingErrorBeanList);
	}
	
	@RequestMapping(value = "/downloadAsignmentUploadSuccessReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAsignmentUploadSuccessReport(HttpServletRequest request, HttpServletResponse response) {
		List<AssignmentStatusBean> subjectMappingSuccessBeanList =  (ArrayList<AssignmentStatusBean>)request.getSession().getAttribute("subjectMappingSuccessBeanList");

		return new ModelAndView("asignmentUploadSuccessReportExcelView","subjectMappingSuccessBeanList",subjectMappingSuccessBeanList);
	}

	
	
	
}
