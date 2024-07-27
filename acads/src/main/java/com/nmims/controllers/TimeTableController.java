package com.nmims.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AcadsCalenderBean;
import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.EventBean;
import com.nmims.beans.ExamBookingTransactionAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAvailabilityBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.services.TimeTableService;
import com.nmims.util.ContentUtil;
import com.nmims.daos.SessionTracksDAO;


@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping("/admin")
public class TimeTableController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	TimeTableService timeTableService;
	
	@Autowired
	StudentService studentService;
	
	@Autowired 
	SessionTracksDAO sessionTracksDao;
	
	private static final DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 
	private ArrayList<String> facultyList = null;
	private final int pageSize = 20;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2015","2016","2017")); 

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	
	private ArrayList<String> semList = new ArrayList<String>(Arrays.asList( 
			"1","2","3","4","5","6","7","8")); 

	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();


	/*public HashMap<String, ArrayList<String>> getProgramSubjectMappingList(){
		if(this.programAndProgramStructureAndSubjectsMap == null || this.programAndProgramStructureAndSubjectsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = eDao.getProgramSubjectMappingList();

			programAndProgramStructureAndSubjectsMap = new HashMap<>();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingBean bean = programSubjectMappingList.get(i);
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
	}*/

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

		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		this.facultyList = dao.getAllFaculties();

		return facultyList;
	}

	public HashMap<String, CenterAcadsBean> getICLCMap() {
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		HashMap<String, CenterAcadsBean> icLcMap = dao.getICLCMap();
		return icLcMap;
	}
	/*@RequestMapping(value = "/uploadFacultyUnavailabilityForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadTimetableForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", yearList);


		return "uploadFacultyUnavailability";
	}


	@RequestMapping(value = "/uploadFacultyUnavailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyUnavailability(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFacultyUnavailability");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyUnavailabilityExcel(fileBean, getFacultyList(), userId);

			List<FacultyUnavailabilityBean> facultyDatesList = (ArrayList<FacultyUnavailabilityBean>)resultList.get(0);
			List<FacultyUnavailabilityBean> errorBeanList = (ArrayList<FacultyUnavailabilityBean>)resultList.get(1);

			fileBean = new FileBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", yearList);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateFacultyDates(facultyDatesList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",facultyDatesList.size() +" rows out of "+ facultyDatesList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

		}

		return modelnView;
	}*/


	@RequestMapping(value = "/uploadFacultyAvailabilityForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadFacultyAvailabilityForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean fileBean = new FileAcadsBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);


		return "uploadFacultyAvailability";
	}


	@RequestMapping(value = "/uploadFacultyAvailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyUnavailability(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFacultyAvailability");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			//Commented old as now directly create mapping
			//ArrayList<List> resultList = excelHelper.readFacultyAvailabilityExcel(fileBean, getFacultyList(), userId);
			ArrayList<List> resultList = excelHelper.readFacultyAvailabilityExcelNew(fileBean, getFacultyList(), userId);
			
			List<FacultyAvailabilityBean> facultyDatesList = (ArrayList<FacultyAvailabilityBean>)resultList.get(0);
			List<FacultyAvailabilityBean> errorBeanList = (ArrayList<FacultyAvailabilityBean>)resultList.get(1);

			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateFacultyDates(facultyDatesList, userId);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",facultyDatesList.size() +" rows out of "+ facultyDatesList.size()+" inserted successfully.");
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

	/*@RequestMapping(value = "/searchFacultyUnavailabilityForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchFacultyUnavailabilityForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FacultyUnavailabilityBean searchBean = new FacultyUnavailabilityBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", yearList);

		return "searchFacultyDates";
	}

	@RequestMapping(value = "/searchFacultyUnavailability",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyUnavailability(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyUnavailabilityBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFacultyDates");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		Page<FacultyUnavailabilityBean> page = dao.getFacultyUnavailabilityDatesPage(1, pageSize, searchBean);
		List<FacultyUnavailabilityBean> facultyUnavailabilityList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", yearList);

		if(facultyUnavailabilityList == null || facultyUnavailabilityList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}

		modelnView.addObject("facultyUnavailabilityList", facultyUnavailabilityList);
		return modelnView;
	}

	@RequestMapping(value = "/searchFacultyUnavailabilityPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyUnavailabilityPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchFacultyDates");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		FacultyUnavailabilityBean searchBean = (FacultyUnavailabilityBean)request.getSession().getAttribute("searchBean_acads");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		Page<FacultyUnavailabilityBean> page = dao.getFacultyUnavailabilityDatesPage(pageNo, pageSize, searchBean);
		List<FacultyUnavailabilityBean> facultyUnavailabilityList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", yearList);

		if(facultyUnavailabilityList == null || facultyUnavailabilityList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}

		modelnView.addObject("facultyUnavailabilityList", facultyUnavailabilityList);
		return modelnView;
	}*/

	@RequestMapping(value = "/searchFacultyAvailabilityForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchFacultyAvailabilityForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FacultyAvailabilityBean searchBean = new FacultyAvailabilityBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "searchFacultyAvailability";
	}

	@RequestMapping(value = "/searchFacultyAvailability",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyAvailability(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAvailabilityBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFacultyAvailability");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		PageAcads<FacultyAvailabilityBean> page = dao.getFacultyAvailabilityDatesPage(1, pageSize, searchBean);
		List<FacultyAvailabilityBean> facultyAvailabilityList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(facultyAvailabilityList == null || facultyAvailabilityList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}

		modelnView.addObject("facultyAvailabilityList", facultyAvailabilityList);
		return modelnView;
	}

	@RequestMapping(value = "/searchFacultyAvailabilityPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyAvailabilityPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchFacultyAvailability");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		FacultyAvailabilityBean searchBean = (FacultyAvailabilityBean)request.getSession().getAttribute("searchBean_acads");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		PageAcads<FacultyAvailabilityBean> page = dao.getFacultyAvailabilityDatesPage(pageNo, pageSize, searchBean);
		List<FacultyAvailabilityBean> facultyAvailabilityList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(facultyAvailabilityList == null || facultyAvailabilityList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}

		modelnView.addObject("facultyAvailabilityList", facultyAvailabilityList);
		return modelnView;
	}

	/*@RequestMapping(value = "/deleteFacultyUnavailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteFacultyUnavailability(HttpServletRequest request, HttpServletResponse response){
		try{
			String id = request.getParameter("id");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			dao.deleteFacultyUnavailability(id);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		FacultyUnavailabilityBean searchBean = (FacultyUnavailabilityBean)request.getSession().getAttribute("searchBean_acads");
		if(searchBean == null){
			searchBean = new FacultyUnavailabilityBean();
		}
		return searchFacultyUnavailability(request,response, searchBean);
	}*/

	@RequestMapping(value = "/deleteFacultyAvailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteFacultyAvailability(HttpServletRequest request, HttpServletResponse response){
		try{
			String id = request.getParameter("id");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			dao.deleteFacultyAvailability(id);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		FacultyAvailabilityBean searchBean = (FacultyAvailabilityBean)request.getSession().getAttribute("searchBean_acads");
		if(searchBean == null){
			searchBean = new FacultyAvailabilityBean();
		}
		return searchFacultyAvailability(request,response, searchBean);
	}

	/*@RequestMapping(value = "/editFacultyUnavailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editFacultyUnavailability(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("addFacultyUnavailability");

		FacultyUnavailabilityBean faculty = new FacultyUnavailabilityBean();
		faculty.setId(request.getParameter("id"));
		faculty.setFacultyId(request.getParameter("facultyId"));
		faculty.setUnavailabilityDate(request.getParameter("unavailabilityDate"));

		m.addAttribute("faculty", faculty);

		request.setAttribute("edit", "true");

		return modelnView;
	}*/

	@RequestMapping(value = "/editFacultyAvailability", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editFacultyAvailability(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ModelAndView modelnView = new ModelAndView("addFacultyAvailability");
		String id = request.getParameter("id");
		FacultyAvailabilityBean faculty = dao.findFacultyAvailabilityById(id);
		request.setAttribute("edit", "true");
		m.addAttribute("faculty", faculty);
		return modelnView;
	}

	/*@RequestMapping(value = "/updateFacultyUnavailability", method = RequestMethod.POST)
	public ModelAndView updateFacultyUnavailability(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyUnavailabilityBean faculty){
		ModelAndView modelnView = new ModelAndView("student");

		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		dao.updateFacultyUnavailability(faculty);

		FacultyUnavailabilityBean searchBean = (FacultyUnavailabilityBean)request.getSession().getAttribute("searchBean_acads");
		request.setAttribute("success","true");
		request.setAttribute("successMessage","Record updated successfully");
		return searchFacultyUnavailability(request, response, searchBean);
	}*/

	@RequestMapping(value = "/updateFacultyAvailability", method = RequestMethod.POST)
	public ModelAndView updateFacultyAvailability(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAvailabilityBean faculty){
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		dao.updateFacultyAvailability(faculty, userId);

		FacultyAvailabilityBean searchBean = (FacultyAvailabilityBean)request.getSession().getAttribute("searchBean_acads");
		request.setAttribute("success","true");
		request.setAttribute("successMessage","Record updated successfully");
		return searchFacultyAvailability(request, response, searchBean);
	}

	@RequestMapping(value = "/uploadScheduleSessionForCorporateForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadScheduleSessionForCorporateForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean fileBean = new FileAcadsBean();
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("session",session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadScheduleSessionForCorporate";
	}

	@RequestMapping(value = "/uploadScheduleSessionForCorporate", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadScheduleSessionForCorporate(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadScheduleSessionForCorporate");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readScheduleSessionForCorporateExcel(fileBean, getFacultyList(), getSubjectList(), userId);

			List<SessionDayTimeAcadsBean> corporateSessionList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(0);
			List<SessionDayTimeAcadsBean> errorBeanList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(1);

			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateCorporateSessionMapping(corporateSessionList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",corporateSessionList.size() +" rows out of "+ corporateSessionList.size()+" inserted successfully.");
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

	@RequestMapping(value = "/uploadCourseFacultyMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadCourseFacultyMappingForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean fileBean = new FileAcadsBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadCourseFacultyMapping";
	}

	@RequestMapping(value = "/uploadCourseFacultyMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadCourseFacultyMapping(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadCourseFacultyMapping");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyCourseMappingExcel(fileBean, getFacultyList(), getSubjectList(), userId);

			List<FacultyCourseMappingBean> facultyCourseMapingList = (ArrayList<FacultyCourseMappingBean>)resultList.get(0);
			List<FacultyCourseMappingBean> errorBeanList = (ArrayList<FacultyCourseMappingBean>)resultList.get(1);

			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateFacultyCourseMapping(facultyCourseMapingList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",facultyCourseMapingList.size() +" rows out of "+ facultyCourseMapingList.size()+" inserted successfully.");
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

	@RequestMapping(value = "/searchCourseFacultyMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchCourseFacultyMappingForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FacultyCourseMappingBean searchBean = new FacultyCourseMappingBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());
		return "searchCourseFacultyMapping";
	}

	@RequestMapping(value = "/viewTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewTimeTable(HttpServletRequest request, HttpServletResponse respnse, Model m) throws ParseException {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		PersonAcads user = (PersonAcads)request.getSession().getAttribute("user_acads");
		String roles = "";
		if(user != null){
			roles = user.getRoles();
			if(roles == null){
				roles = "";
			}
		}

		if(roles.indexOf("Faculty") != -1){
			return viewFacultyTimeTable(request, respnse, m);
		}else if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Acads Coordinator") != -1){
			return viewCompleteTimeTable(request, respnse, m);
		}else{
			return viewStudentTimeTable(request, respnse, m);
		}
	}
	
	@RequestMapping(value = "/viewCompleteTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewCompleteTimeTable(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("acadCalendar");
		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		searchBean.setYear(CURRENT_ACAD_YEAR);
		searchBean.setMonth(CURRENT_ACAD_MONTH);
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		//Fetch and store User Authorization in session
		// corporate Center show only there Time Table
		String userId = (String)request.getSession().getAttribute("userId_acads");
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)request.getSession().getAttribute("userAuthorization");

		CenterAcadsBean centers = getICLCMap().get(userAuthorization.getCommaSeparatedAuthorizedCenterCodes());
		if(centers !=null && "Corporate Center".equals(userAuthorization.getRoles()))
		{
			if("Verizon".equals(centers.getCenterName()))
			{
				searchBean.setCorporateName(centers.getCenterName());
			}
		}

		//PageAcads<SessionDayTimeAcadsBean> page = dao.getScheduledSessionPageNew(1, Integer.MAX_VALUE, searchBean);


		//List<SessionDayTimeAcadsBean> scheduledSessionList = page.getPageItems();
	
		List<SessionDayTimeAcadsBean> scheduledSessionList = timeTableService.getScheduledSessionPageNew(searchBean, getSubjectCodeIdsMap());
		
		modelnView.addObject("scheduledSessionList", scheduledSessionList);
		modelnView.addObject("trackList", timeTableService.getTrackList(scheduledSessionList));
		modelnView.addObject("subjectList", getAllSubjectCodeLists());
		modelnView.addObject("programIdMap", getProgramIdNameMap());
		modelnView.addObject("semList",semList);
		request.getSession().setAttribute("allsessions", scheduledSessionList);
		return modelnView;
	}

	@RequestMapping(value = "/viewFacultyTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewFacultyTimeTable(HttpServletRequest request, HttpServletResponse respnse, Model m) throws ParseException {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		  
		ModelAndView modelnView = new ModelAndView("acadCalendar");
		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		String facultyId = (String)request.getSession().getAttribute("userId_acads");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = dao.getScheduledSessionForFaculty(facultyId);//scheduledSessionList.addAll(dao.getScheduledSessionForAltFaculty(facultyId));
		
		modelnView.addObject("scheduledSessionList", scheduledSessionList);
		return modelnView;
	}
	
	@RequestMapping(value = "/viewStudentTimeTableOld", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewStudentTimeTableOld(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("studentAcadCalendar");

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionListFromToday = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> unapplicableScheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			//StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId); need to discuss with sanket sir
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			try{
	
				if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
					studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);
				} else {
					studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
				}
			}catch(NullPointerException e){
				  
			}
			//Start Get Registered Exam Dates  
			List<ExamBookingTransactionAcadsBean> bookedExams = dao.getBookedExams(student.getSapid());
			request.setAttribute("bookedExams", bookedExams);
			//End Get Registered Exam Dates 

			//Start Get Key Events Dates  
			List<EventBean> eventsList = dao.getEventsList();
			request.setAttribute("eventsList", eventsList);
			//End Get Key Events Dates  

			// added temporary for Online Event 
			/*boolean registeredForEvent = dao.getOnlineEventRegistrationBySapid(sapId);
			modelnView.addObject("registeredForEvent",registeredForEvent);*/
			
			
			if(studentRegistrationData == null){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}

			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram()); 
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month =studentRegistrationData.getMonth();
		
			//ArrayList<String> subjects = getSubjectsForStudent(student);
			ArrayList<String> subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
		
				if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {

					//Commented By Somesh as Added session configurable
					//scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
					scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
				}else {
					scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));

					if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
						//Commented By Somesh as Added session configurable
						//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
						
						//Get Common Sessions
						commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
					}else {
						//Get Common Sessions for UG
						commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
					}
				}
				
				if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
					scheduledSessionList.addAll(commonscheduledSessionList);
				}
			
			// Commented as getting Nullpointer Exception 
			//If it is in session, don't fetch from Database.
			//scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getSession().getAttribute("studentSessionList");
			
			//ArrayList<SessionDayTimeBean> commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram());
			
			
			/*if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
				scheduledSessionList.addAll(commonscheduledSessionList); 
			}*/
			
			ArrayList<SessionDayTimeAcadsBean> eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getEventsRegisteredByStudent(sapId);
			if(eventSessionList!=null && eventSessionList.size()>0){
				scheduledSessionList.addAll(eventSessionList);
			}

			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())){
					for(SessionDayTimeAcadsBean sessionList : scheduledSessionList){
						if("Assignment Preparation Session".equalsIgnoreCase(sessionList.getSessionName())) {
							unapplicableScheduledSessionList.add(sessionList);
						}
					}
				}
					scheduledSessionList.removeAll(unapplicableScheduledSessionList);
					modelnView.addObject("scheduledSessionList", scheduledSessionList);
					request.getSession().setAttribute("scheduledSessionList", scheduledSessionList);
					return modelnView;
			}

			scheduledSessionListFromToday.addAll(dao.getScheduledSessionForStudentsFromTodayNew(subjects,year,month));
			
			modelnView.addObject("scheduledSessionList", scheduledSessionList);
			modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
			
			request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
			request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);

			
		}catch(Exception e)
		{
			  
		}
		
		
		return modelnView;
	}

	@RequestMapping(value = "/viewSessionsTimelineOld", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewSessionsTimelineOld(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("sessionsTimeline");
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList =  new ArrayList<SessionDayTimeAcadsBean>();
			//ArrayList<SessionDayTimeBean>  scheduledSessionListFromToday = new ArrayList<SessionDayTimeBean>();
			ArrayList<SessionDayTimeAcadsBean> eventSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			//StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId); need to discuss with sanket sir
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			try{
	
			studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
			
			}catch(NullPointerException e){
				  
			}
			
			if(studentRegistrationData == null){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}

			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month =studentRegistrationData.getMonth();
		
			//ArrayList<String> subjects = getSubjectsForStudent(student);
			ArrayList<String> subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
			
			//Commented By Somesh as Added session configurable
			scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
			
			if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
				//Commented By Somesh as Added session configurable
				//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
				
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
				eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getEventsRegisteredByStudent(sapId);
				//scheduledSessionListFromToday.addAll(dao.getScheduledSessionForStudentsFromTodayNew(subjects,year,month));
			}else {
				//Get Common Sessions for UG
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
			}

			if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
				scheduledSessionList.addAll(commonscheduledSessionList);
			}
			
			if(eventSessionList!=null && eventSessionList.size()>0){
				scheduledSessionList.addAll(eventSessionList);
			}
			
			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData,new ArrayList<Integer>());
				modelnView.addObject("scheduledSessionList", scheduledSessionList);
				return modelnView;
			}
			
//			scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData);
			modelnView.addObject("scheduledSessionList", scheduledSessionList);
		
		}catch(Exception e){
			  
		}
		
		return modelnView;
	}
	
	private ArrayList<SessionDayTimeAcadsBean> getVideosForSessionList(ArrayList<SessionDayTimeAcadsBean> scheduledSessionList, StudentAcadsBean student, List<Integer> currentSemPSSId){
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO");
		List<VideoContentAcadsBean> videos=null;
		HashMap<String, String> getStudentSessionMap = new HashMap<>();
		getStudentSessionMap = tDao.getAttendanceForSessionMap(student.getSapid());
		
		//Prepare date format
		String acadsDateFormat = ContentUtil.prepareAcadDateFormat(student.getMonth(), student.getYear());
		
		String key = "";
		for(SessionDayTimeAcadsBean bean:scheduledSessionList) {
			videos = new ArrayList<>();
			videos= dao.getVideosForSessionFromTemp(bean.getId(),currentSemPSSId,acadsDateFormat);
			bean.setVideosOfSession(videos);
			key = student.getSapid() + " - "+bean.getId();
			
			if (getStudentSessionMap.containsKey(key)) {
				bean.setAttended("Yes");
			}else{
				bean.setAttended("No");
			}
			
		}
		return scheduledSessionList;
	}
	
	private ArrayList<String> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				//Added temporary for PD - WM project lecture
				if (student.getProgram().equalsIgnoreCase("PD - WM") && bean.getSubject().equalsIgnoreCase("Module 4 - Project")) {
					subjects.add("Project");
				}else {
					subjects.add(bean.getSubject());
				}
			}
		}
		return subjects;
	}

	@RequestMapping(value = "/searchCourseFacultyMapping",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchCourseFacultyMapping(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyCourseMappingBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchCourseFacultyMapping");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		PageAcads<FacultyCourseMappingBean> page = dao.getCourseFacultyMappingPage(1, pageSize, searchBean);
		List<FacultyCourseMappingBean> courseFacultyMappingList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());

		if(courseFacultyMappingList == null || courseFacultyMappingList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}

		modelnView.addObject("courseFacultyMappingList", courseFacultyMappingList);
		return modelnView;
	}

	@RequestMapping(value = "/searchCourseFacultyMappingPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchCourseFacultyMappingPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchCourseFacultyMapping");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		FacultyCourseMappingBean searchBean = (FacultyCourseMappingBean)request.getSession().getAttribute("searchBean_acads");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		PageAcads<FacultyCourseMappingBean> page = dao.getCourseFacultyMappingPage(pageNo, pageSize, searchBean);
		List<FacultyCourseMappingBean> courseFacultyMappingList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());

		if(courseFacultyMappingList == null || courseFacultyMappingList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Exam Centers found.");
		}

		modelnView.addObject("courseFacultyMappingList", courseFacultyMappingList);
		return modelnView;
	}

	@RequestMapping(value = "/deleteCourseFacultyMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteCourseFacultyMapping(HttpServletRequest request, HttpServletResponse response){
		try{
			String id = request.getParameter("id");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			dao.deleteCourseFacultyMapping(id);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		FacultyCourseMappingBean searchBean = (FacultyCourseMappingBean)request.getSession().getAttribute("searchBean_acads");
		if(searchBean == null){
			searchBean = new FacultyCourseMappingBean();
		}
		return searchCourseFacultyMapping(request,response, searchBean);
	}

	@RequestMapping(value = "/uploadSessionDayTimeForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadSessionDayTimeForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean fileBean = new FileAcadsBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadSessionDayTime";
	}

	@RequestMapping(value = "/uploadSessionDayTime", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadSessionDayTime(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadSessionDayTime");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readSessionDayTimeExcel(fileBean, userId);

			List<SessionDayTimeAcadsBean> sessionDayTimeList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(0);
			List<SessionDayTimeAcadsBean> errorBeanList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(1);

			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateSessionDayTime(sessionDayTimeList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",sessionDayTimeList.size() +" rows out of "+ sessionDayTimeList.size()+" inserted successfully.");
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


	@RequestMapping(value = "/searchSessionDayTimeForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchSessionDayTimeForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "searchSessionDayTime";
	}

	@RequestMapping(value = "/searchSessionDayTime",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchSessionDayTime(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchSessionDayTime");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);

		PageAcads<SessionDayTimeAcadsBean> page = dao.getSessionDayTime(1, Integer.MAX_VALUE, searchBean);
		List<SessionDayTimeAcadsBean> sessionDayTimeList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);


		if(sessionDayTimeList == null || sessionDayTimeList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}

		modelnView.addObject("sessionDayTimeList", sessionDayTimeList);
		return modelnView;
	}

	@RequestMapping(value = "/deleteSessionDayTime", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteSessionDayTime(HttpServletRequest request, HttpServletResponse response){
		try{
			String id = request.getParameter("id");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			dao.deleteSessionDayTime(id);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		SessionDayTimeAcadsBean searchBean = (SessionDayTimeAcadsBean)request.getSession().getAttribute("searchBean_acads");
		if(searchBean == null){
			searchBean = new SessionDayTimeAcadsBean();
		}
		return searchSessionDayTime(request,response, searchBean);
	}

	@RequestMapping(value = "/setSessionCalenderDatesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView setSessionCalenderDatesForm(HttpServletRequest request, HttpServletResponse response){
		logger.info("Make AcadCalender Live Form");
		ModelAndView modelAndView =new ModelAndView("makeAcadCalenderLive");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<AcadsCalenderBean> calenderList =null;
		try{
			calenderList =dao.getAllAcadsCalender();

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Acads Calender live.");
		}
		modelAndView.addObject("calenderList", calenderList);
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("sessionCalenderBean", new AcadsCalenderBean());
		return modelAndView;
	}

	@RequestMapping(value = "/setSessionCalenderDates", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView setSessionCalenderDates(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AcadsCalenderBean sessionCalenderBean){
		logger.info("Make AcadCalender Live Form");
		ModelAndView modelAndView =new ModelAndView("makeAcadCalenderLive");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");

		try{
			dao.upsertAcadsCalender(sessionCalenderBean);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Online Acads "+sessionCalenderBean.getMonth()+" "+sessionCalenderBean.getYear()+" Session Calender Date Set Successfully");
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Acads Calender live."+e.getMessage());
		}

		ArrayList<AcadsCalenderBean> calenderList =null;
		try{
			calenderList =dao.getAllAcadsCalender();

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making Acads Calender live.");
		}

		modelAndView.addObject("calenderList", calenderList);
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("sessionCalenderBean",sessionCalenderBean);
		return modelAndView;
	}

////			modelnView.addObject("scheduledSessionList", scheduledSessionList);
////			modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
////			request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
////			request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//
//		}catch(Exception e)
//		{
//			  
//		}
//		return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//	}
	
//	@RequestMapping(value = "/m/studentTimeTableOld",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<SessionDayTimeBean>> mstudentTimeTableOld(@RequestBody StudentBean input) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
//		ArrayList<SessionDayTimeBean>  scheduledSessionListFromToday = new ArrayList<SessionDayTimeBean>();
//        try{
//			
//			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//			
//			String sapId = input.getSapid();
//			
//			StudentBean studentRegistrationData = new StudentBean();
//			StudentBean student =  dao.getSingleStudentsData(sapId);
//			try{
//				if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//					studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);
//				}else {
//					studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
//				}
//			}catch(NullPointerException e){
//				  
//			}
//			
//			
//			//Start Get Registered Exam Dates  
//			
//			
//			
//			//List<ExamBookingTransactionBean> bookedExams = dao.getBookedExams(student.getSapid());
//			//End Get Registered Exam Dates 
//
//			//Start Get Key Events Dates  
//			//List<EventBean> eventsList = dao.getEventsList();
//			//End Get Key Events Dates
//			// added temporary for Online Event 
////						boolean registeredForEvent = dao.getOnlineEventRegistrationBySapid(sapId);
////						modelnView.addObject("registeredForEvent",registeredForEvent);
//						
//			
//			
////	if(studentRegistrationData == null){
////				request.setAttribute("error", "true");
////				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
////				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeBean>());
////				return modelnView;
////			}
////			
//			if(studentRegistrationData == null){
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//
//			}
//				//For Old students incomplete data//
//				//Take program from Registration data and not Student data. 
//				
//
//		
//				student.setProgram(studentRegistrationData.getProgram());
//				student.setSem(studentRegistrationData.getSem());
//				String year = studentRegistrationData.getYear();
//				String month =studentRegistrationData.getMonth();
//
//				ArrayList<String> subjects = getSubjectsForStudent(student);
//				ArrayList<SessionDayTimeBean> commonscheduledSessionList = new ArrayList<SessionDayTimeBean>();
//
//				if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//
//					//Commented By Somesh as Added session configurable
//					//scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
//
//					scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
//					
//				}else {
//					scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
//					
//					if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//						//Commented By Somesh as Added session configurable
//						//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
//						commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//					}else {
//						//Get Common Sessions for UG
//						commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//					}
//					
//					if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
//						scheduledSessionList.addAll(commonscheduledSessionList);
//					}
//				}
//				
//			// Commented as getting Nullpointer Exception 
//			//If it is in session, don't fetch from Database.
//			//scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getSession().getAttribute("studentSessionList");
//
//		
//
////			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
////				modelnView.addObject("scheduledSessionList", scheduledSessionList);
////				return modelnView;
////			}
//
//			//ArrayList<SessionDayTimeBean>  scheduledSessionListFromToday = dao.getScheduledSessionForStudentsFromToday(subjects);
//			 
////			scheduledSessionList.addAll(scheduledSessionListFromToday);
////			modelnView.addObject("scheduledSessionList", scheduledSessionList);
////			modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
////			request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
////			request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
//			
//		
//				//scheduledSessionListFromToday.addAll(dao.getScheduledSessionForStudentsFromTodayNew(subjects,year,month));
//			
//			
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//
//		}catch(Exception e)
//		{
//			  
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//
//		}
//	}
//	
//	@RequestMapping(value = "/m/studentTimeTableExams",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<ExamBookingTransactionBean>> mstudentTimeTableExams(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
////		ModelAndView modelnView = new ModelAndView("studentAcadCalendar");
////		if(!checkSession(request, respnse)){
////			return new ModelAndView("studentPortalRediret");
////		}
//		List<ExamBookingTransactionBean> bookedExams = null;
//		try{
//			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//			//Start Get Registered Exam Dates  
//			bookedExams = dao.getBookedExams(student.getSapid());
//			//End Get Registered Exam Dates 
//				return new ResponseEntity<List<ExamBookingTransactionBean>>(bookedExams, headers,  HttpStatus.OK);
//		}catch(Exception e)
//		{
//			  
//		}
//		return new ResponseEntity<List<ExamBookingTransactionBean>>(bookedExams, headers,  HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/studentTimeTableEvents",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<EventBean>> mstudentTimeTableEvents(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//		List<EventBean> eventsList = null;
//		try{
//			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//			//Start Get Key Events Dates  
//			eventsList = dao.getEventsList();
//				return new ResponseEntity<List<EventBean>>(eventsList, headers,  HttpStatus.OK);
//		}catch(Exception e)
//		{
//			  
//		}
//		return new ResponseEntity<List<EventBean>>(eventsList, headers,  HttpStatus.OK);
//	}
	
		
	@RequestMapping(value = "/uploadScheduleSessionForSASForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadScheduleSessionForSASForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean fileBean = new FileAcadsBean();
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("session",session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "uploadScheduleSessionForSAS";
	}

	@RequestMapping(value = "/uploadScheduleSessionForSAS", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadScheduleSessionForSAS(FileAcadsBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadScheduleSessionForSAS");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readScheduleSessionForCorporateExcel(fileBean, getFacultyList(), getSubjectList(), userId);

			List<SessionDayTimeAcadsBean> SASSessionList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(0);
			List<SessionDayTimeAcadsBean> errorBeanList = (ArrayList<SessionDayTimeAcadsBean>)resultList.get(1);

			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateCorporateSessionMapping(SASSessionList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",SASSessionList.size() +" rows out of "+ SASSessionList.size()+" inserted successfully.");
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
	
//	@RequestMapping(value = "/m/viewSessionsTimelineOld", method = {RequestMethod.POST})
//	public ResponseEntity<List<SessionDayTimeBean>> mviewSessionsTimelineOld(@RequestBody StudentBean input) {
//		
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        
//        ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
//		ArrayList<SessionDayTimeBean> commonscheduledSessionList =  new ArrayList<SessionDayTimeBean>();
//		ArrayList<SessionDayTimeBean> eventSessionList = new ArrayList<SessionDayTimeBean>();
//		
//		try{
//			
//			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//			String sapId = input.getSapid();
//			StudentBean student =  dao.getSingleStudentsData(sapId);
//			//StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId); need to discuss with sanket sir
//			StudentBean studentRegistrationData = new StudentBean();
//			
//			try{
//				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
//			}catch(NullPointerException e){
//				  
//			}
//			
//			if(studentRegistrationData == null){
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			}
//
//			//For Old students incomplete data//
//			//Take program from Registration data and not Student data. 
//			student.setProgram(studentRegistrationData.getProgram());
//			student.setSem(studentRegistrationData.getSem());
//			String year = studentRegistrationData.getYear();
//			String month =studentRegistrationData.getMonth();
//		
//			ArrayList<String> subjects = getSubjectsForStudent(student);
//			
//			//Commented By Somesh as Added session configurable
//			scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
//			
//			if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//				//Commented By Somesh as Added session configurable
//				//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
//				
//				commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//				eventSessionList = (ArrayList<SessionDayTimeBean>)dao.getEventsRegisteredByStudent(sapId);
//			}else {
//				//Get Common Sessions for UG
//				commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//			}
//				
//			if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
//				scheduledSessionList.addAll(commonscheduledSessionList);
//			}
//			
//			if(eventSessionList!=null && eventSessionList.size()>0){
//				scheduledSessionList.addAll(eventSessionList);
//			}
//			
//			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
//				scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData);
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			}
//			
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//		
//		}catch(Exception e){
//			  
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//		}	
//	}
	
	public ModelAndView viewStudentTimeTable(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("studentAcadCalendar");
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionListFromToday = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();			
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			
			//Check student registration and return reg details
			StudentAcadsBean studentRegistrationForAcademicSession = timeTableService.checkStudentRegistration(sapId, student);
			
			//Start Get Registered Exam Dates
			List<ExamBookingTransactionAcadsBean> bookedExams = dao.getBookedExams(student.getSapid());
			request.setAttribute("bookedExams", bookedExams);
			//End Get Registered Exam Dates

			//Start Get Key Events Dates
			List<EventBean> eventsList = dao.getEventsList();
			request.setAttribute("eventsList", eventsList);
			//End Get Key Events Dates
			
			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			List<Integer> currentSemPSSId = (List<Integer>) request.getSession().getAttribute("currentSemPSSId");
			
			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationForAcademicSession != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
				} catch (Exception e) {
					  
				}
			}
			
			if(studentRegistrationForAcademicSession == null || examOrderForSession == null){
				
				if (isCourseMappingAvailable) {
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					scheduledSessionListFromToday = dao.getAllSessionsByCourseMappingFromToday(student.getSapid());
					
					modelnView.addObject("scheduledSessionList", scheduledSessionList);
					modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
					
					request.getSession().setAttribute("studentSessionList", scheduledSessionList);
					request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
					
					return modelnView;
				}
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}
			
			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationForAcademicSession.getProgram()); 
			student.setSem(studentRegistrationForAcademicSession.getSem());
			String year = studentRegistrationForAcademicSession.getYear();
			String month =studentRegistrationForAcademicSession.getMonth();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			String sem = studentRegistrationForAcademicSession.getSem();
			String sapid = student.getSapid();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			scheduledSessionListFromToday = timeTableService.getScheduledSessionsFromToday(student, year, month);
			
			modelnView.addObject("scheduledSessionList", scheduledSessionList);
			modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
			modelnView.addObject("trackDetails", sessionTracksDao.getAllTracksDetails());
			request.getSession().setAttribute("trackDetails", sessionTracksDao.getAllTracksDetails());
			
			request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
			request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
			
		}catch (Exception e) {
			  
		}
		return modelnView;
	}
	

	@RequestMapping(value = "/viewSessionsTimeline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewSessionsTimeline(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("sessionsTimeline");
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			
			//Check student registration and return reg details
			StudentAcadsBean studentRegistrationForAcademicSession = timeTableService.checkStudentRegistration(sapId, student);
			
			//Waived-in subject of current cycle
			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			List<Integer> currentSemPSSId = (List<Integer>) request.getSession().getAttribute("currentSemPSSId");
			
			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationForAcademicSession != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
				} catch (Exception e) {
					  
				}
			}
			
			if(studentRegistrationForAcademicSession == null || examOrderForSession == null){
				
				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					scheduledSessionList = getVideosForSessionList(scheduledSessionList,student,dao.getPSSIdFromCourseMapping(student.getSapid()));
					modelnView.addObject("scheduledSessionList", scheduledSessionList);
					request.getSession().setAttribute("studentSessionList", scheduledSessionList);
					return modelnView;
				}
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}
			
			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationForAcademicSession.getProgram());
			student.setSem(studentRegistrationForAcademicSession.getSem());
			String year = studentRegistrationForAcademicSession.getYear();
			String month = studentRegistrationForAcademicSession.getMonth();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			String sem = studentRegistrationForAcademicSession.getSem();
			String sapid = student.getSapid();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				scheduledSessionList = getVideosForSessionList(scheduledSessionList, studentRegistrationForAcademicSession,currentSemPSSId);
				modelnView.addObject("scheduledSessionList", scheduledSessionList);
				return modelnView;
			}
			
		}catch (Exception e) {
			  
		}
		
		return modelnView;
	}
	
//	@RequestMapping(value = "/m/studentTimeTableUpcomingHome",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<SessionDayTimeBean>> mstudentTimeTableUpcomingHome(@RequestBody StudentBean input) {
//		
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//        List<SessionDayTimeBean> finalScheduledSessionList = new ArrayList<SessionDayTimeBean>();
//        ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
//        ArrayList<SessionDayTimeBean> allSessionsByCourseMapping = new ArrayList<SessionDayTimeBean>();
//        
//        try {
//        	String sapId = input.getSapid();
//			StudentBean student =  dao.getSingleStudentsData(sapId);
//			StudentBean studentRegistrationData = new StudentBean();
//			
//			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
//			}else{
//				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
//			}
//			
//			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
//			
//			if(studentRegistrationData == null){
//				if (isCourseMappingAvailable) {
//					//Even if student not register for current cycle Waived-in subject is applicable
//					scheduledSessionList = dao.getAllSessionsByCourseMappingForUpcoming(student.getSapid());
//					return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//				}
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			}
//			
//			
//			//For Old students incomplete data//
//			//Take program from Registration data and not Student data. 
//			student.setProgram(studentRegistrationData.getProgram());
//			student.setSem(studentRegistrationData.getSem());
//			String year = studentRegistrationData.getYear();
//			String month = studentRegistrationData.getMonth();
//			
//			ArrayList<String> subjects = getSubjectsForStudent(student);
//			
//			//Remove WaiveOff Subject from applicable Subject list
//			ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
//			subjects.removeAll(waivedOffSubjects);
//			
//			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) { 
//				scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
//			}else {
//				ArrayList<SessionDayTimeBean> commonscheduledSessionList = new ArrayList<SessionDayTimeBean>();
//				if (student.getConsumerProgramStructureId().equalsIgnoreCase("111") || student.getConsumerProgramStructureId().equalsIgnoreCase("131")) {
//					commonscheduledSessionList = dao.getCommonSessionsForMBAWx(student, year, month, "Upcoming");
//				}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//					commonscheduledSessionList = dao.getCommonSessionsSemesterBasedFromToday(studentRegistrationData.getSem(),studentRegistrationData.getProgram(),student.getConsumerProgramStructureId());
//				}else if("BBA".equalsIgnoreCase(student.getProgram()) || "B.Com".equalsIgnoreCase(student.getProgram())) {
//					//Get Common Sessions for UG
//					commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//				}
//				
//				allSessionsByCourseMapping = dao.getAllSessionsByCourseMappingForUpcoming(student.getSapid());
//				
//				if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
//					scheduledSessionList.addAll(commonscheduledSessionList);
//				}
//				
//				if (allSessionsByCourseMapping != null && allSessionsByCourseMapping.size() > 0) {
//					scheduledSessionList.addAll(allSessionsByCourseMapping);
//				}
//				
//				scheduledSessionList.addAll(dao.getUpcomingScheduledSessionForStudentsByCPSIdV2(student, subjects));
//				
//				//Added for sorting
//				if(scheduledSessionList.size() > 0) {
//					Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeBean>() {
//						@Override
//						public int compare(SessionDayTimeBean sBean1, SessionDayTimeBean sBean2) {
//							return sBean1.getDate().compareTo(sBean2.getDate());
//						}
//					});
//				}
//
//				//If Session list is > 10 then send only 1st 10 sessions
//				if (scheduledSessionList.size() > 10) {
//					finalScheduledSessionList = scheduledSessionList.subList(0, 10);
//				}else {
//					finalScheduledSessionList = scheduledSessionList;
//				}
//			}
//			return new ResponseEntity<List<SessionDayTimeBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
//			
//		} catch (Exception e) {
//			  
//		}
//        
//        return new ResponseEntity<List<SessionDayTimeBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
//	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/studentTimeTable",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<SessionDayTimeBean>> mstudentTimeTable(@RequestBody StudentBean input) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//        ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
//        try {
//			String sapId = input.getSapid();
//			StudentBean student =  dao.getSingleStudentsData(sapId);
//			StudentBean studentRegistrationData = new StudentBean();
//			
//			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
//			}else{
//				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
//			}
//			
//			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
//			
//			if(studentRegistrationData == null){
//				if (isCourseMappingAvailable) {
//					//Even if student not register for current cycle Waived-in subject is applicable
//					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
//					return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//				}
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			}
//			
//			student.setProgram(studentRegistrationData.getProgram());
//			student.setSem(studentRegistrationData.getSem());
//			String year = studentRegistrationData.getYear();
//			String month = studentRegistrationData.getMonth();
//			
//			scheduledSessionList = timeTableService.getAllScheduledSessions(student, year, month);
//			return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			
//		} catch (Exception e) {
//			  
//		}
//        return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/viewSessionsTimeline", method = {RequestMethod.POST})
//	public ResponseEntity<List<SessionDayTimeBean>> mviewSessionsTimeline(@RequestBody StudentBean input) {
//		
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//        
//        ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
//		try {
//			String sapId = input.getSapid();
//			StudentBean student =  dao.getSingleStudentsData(sapId);
//			StudentBean studentRegistrationData = new StudentBean();
//
//			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
//				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
//			}else{
//				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
//			}
//			
//			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
//			
//			if(studentRegistrationData == null){
//				
//				if (isCourseMappingAvailable) {
//					//Even if student not register for current cycle Waived-in subject is applicable
//					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
//					scheduledSessionList = getVideosForSessionList(scheduledSessionList,student);
//					return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers, HttpStatus.OK);
//				}
//				
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//			}
//
//			//For Old students incomplete data//
//			//Take program from Registration data and not Student data. 
//			student.setProgram(studentRegistrationData.getProgram());
//			student.setSem(studentRegistrationData.getSem());
//			String year = studentRegistrationData.getYear();
//			String month = studentRegistrationData.getMonth();
//			
//			scheduledSessionList = timeTableService.getAllScheduledSessions(student, year, month);
//			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
//				scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData);
//				return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers, HttpStatus.OK);
//			}
//			
//		} catch (Exception e) {
//			  
//		}
//		return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers, HttpStatus.OK);
//	}
	

}