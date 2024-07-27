package com.nmims.controllers;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.ExecutiveTimetableBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExecutiveConfigurationDao;
import com.nmims.daos.ExecutiveTimeTableDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExecutiveExcelReader;

@Controller
public class ExecutiveTimeTableController extends ExecutiveBaseController{
	@Autowired
	ApplicationContext act;
   
	
	
	
	@RequestMapping(value = "/uploadSASTimetableForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadSASTimetableForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("uploadSASTimeTable");
		mav.addObject("fileBean", new FileBean());
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList", SAS_EXAM_MONTH_LIST);
		mav.addObject("programStructureList", SAS_PROGRAM_STRUCTURE_LIST);
		//mav.addObject("corporateCenterList",corporateCenterList);
		return mav;
	}
	
	@RequestMapping(value = "/uploadSASTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadTimeTable(@ModelAttribute FileBean fileBean, BindingResult result,HttpServletRequest request){
		ModelAndView mav = new ModelAndView("uploadSASTimeTable");
		try{
			String userId = (String)request.getSession().getAttribute("userId");
			ExecutiveExcelReader excelHelper = new ExecutiveExcelReader();
			@SuppressWarnings("rawtypes")
			ArrayList<List> resultList = excelHelper.readTimeTableExcel(fileBean, getAllProgramList(), getAllSubjectList(), userId, ACAD_YEAR_SAS_LIST, SAS_EXAM_MONTH_LIST);
			@SuppressWarnings("unchecked")
			ArrayList<ExecutiveTimetableBean> timeTableList = (ArrayList<ExecutiveTimetableBean>)resultList.get(0);
			@SuppressWarnings("unchecked")
			ArrayList<ExecutiveTimetableBean> errorBeanList = (ArrayList<ExecutiveTimetableBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return mav;
			}

			ExecutiveTimeTableDao sdao = (ExecutiveTimeTableDao)act.getBean("executiveTimeTableDao");
			ArrayList<String> errorList = sdao.batchUpdateSASTimeTable(timeTableList,fileBean);

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
		mav.addObject("fileBean", new FileBean());
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		return mav;
	}
	
	
	@RequestMapping(value = "/makeExecutiveTimetableLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeExecutiveTimetableLiveForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamOrderBean exam) {
		ModelAndView mav = new ModelAndView("makeExecutiveTimetableLive");
		ExecutiveConfigurationDao dao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
		List<ExecutiveExamOrderBean> examsList = dao.getExecutiveExamOrderList(); 
		mav.addObject("examsList", examsList);
		mav.addObject("exam", exam);
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList", SAS_EXAM_MONTH_LIST);
		mav.addObject("acadMonthList", SAS_ENROLLMENT_MONTH_LIST);
		return mav;
	}
	
	@RequestMapping(value = "/makeExecutiveTimetableLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeExecutiveTimetableLive(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExecutiveExamOrderBean exam) {
		
		ModelAndView mav = new ModelAndView("makeExecutiveTimetableLive");
		ExecutiveConfigurationDao dao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
		try{
			dao.updateExamOrderTimetableStats(exam);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Timetable for "+exam.getMonth()+" "+exam.getYear()+"  updated successfully");
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in making timetable live.");
		}
		List<ExecutiveExamOrderBean> examsList = dao.getExecutiveExamOrderList(); 
		mav.addObject("examsList", examsList);
		mav.addObject("exam", exam);
		mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList", SAS_EXAM_MONTH_LIST);
		mav.addObject("acadMonthList", SAS_ENROLLMENT_MONTH_LIST);
		return mav;
	}
	
	
	@RequestMapping(value = "/executiveStudentTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView executiveStudentTimeTable(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
				
		ModelAndView mav = new ModelAndView("executiveStudentTimeTable");
		
		ExecutiveTimeTableDao tdao = (ExecutiveTimeTableDao) act.getBean("executiveTimeTableDao");
		ArrayList<ExecutiveTimetableBean> timeTableList = tdao.getMostRecentExecutiveTimetable(student);
		
		String examYear = "";
		String examMonth = "";
		String mostRecentTimetablePeriod = "";
		Boolean isTimetableLive = true;
		
		if(timeTableList.isEmpty()){
			setError(request, "TimeTable is not live");
			mav.addObject("isTimetableLive",false);
			mav.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
			request.getSession().setAttribute("isTimetableLive",false);
			request.setAttribute("timtableList", null);
			return mav;
		}
		
		for (int i = 0; i < timeTableList.size(); i++) {
			ExecutiveTimetableBean bean = timeTableList.get(i);
			examYear = bean.getExamYear();
			examMonth = bean.getExamMonth();
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		mav.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
		mav.addObject("isTimetableLive",isTimetableLive);
		request.setAttribute("timtableList", timeTableList);
		request.getSession().setAttribute("isTimetableLive",isTimetableLive);
		
		return mav;
	}
	
	
	
	@RequestMapping(value = "/adminExecutiveTimeTableForm", method = RequestMethod.GET)
	public String adminExecutiveTimeTableForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		TimetableBean bean = new TimetableBean();
		m.addAttribute("bean", bean);
		
		return "adminExecutiveTimeTable";
	}
	
	@RequestMapping(value = "/adminExecutiveTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView adminExecutiveTimeTable(HttpServletRequest request, HttpServletResponse response, Model m,@ModelAttribute TimetableBean bean) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		//String examYear = request.getParameter("examYear");
		//String examMonth = request.getParameter("examMonth");
		String examYear = request.getParameter("examYear");
		String examMonth = request.getParameter("examMonth");
						
		ModelAndView modelnView = new ModelAndView("executivetimeTable");

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		String mostRecentTimetablePeriod; //= dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = dao.getAdminExecutiveTimetableList(bean);
		modelnView.addObject("timeTableList", timeTableList);
		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		
		
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean tempBean = timeTableList.get(i);

			if(!programTimetableMap.containsKey(tempBean.getProgram() + " - "+ tempBean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(tempBean);
				programTimetableMap.put(tempBean.getProgram() + " - "+ tempBean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(tempBean.getProgram() + " - "+ tempBean.getPrgmStructApplicable() + " Program Structure");
				list.add(tempBean);
			}
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		modelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		request.setAttribute("programTimetableMap", treeMap);
		return modelnView;
	}


}
