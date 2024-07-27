package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.Person;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.ExecutiveConfigurationDao;
import com.nmims.daos.ExecutiveExamDao;
import com.nmims.daos.StudentMarksDAO;



@Controller
public class ExecutiveConfigurationController extends ExecutiveBaseController {
	@Autowired
	ApplicationContext act;
	

	@Value("#{'${SAS_PROGRAM_STRUCTURE_LIST}'.split(',')}") 
	private List<String> SAS_PROGRAM_STRUCTURE_LIST; 
	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_SAS_LIST; 
	
	@Value("#{'${SAS_ENROLLMENT_MONTH_LIST}'.split(',')}") 
	private List<String> SAS_ENROLLMENT_MONTH_LIST2; 
	protected List<String> SAS_ENROLLMENT_MONTH_LIST = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	
	@Value("#{'${SAS_PROGRAM_LIST}'.split(',')}") 
	private List<String> SAS_PROGRAM_LIST; 

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 

	HashMap<String, StudentExamBean>  sapIdStudentsMap = null;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		sapIdStudentsMap = null;
		getStudentsMap();
		
		
		return null;
	}
	
	public HashMap<String, StudentExamBean> getStudentsMap(){
		if(this.sapIdStudentsMap == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.sapIdStudentsMap = dao.getAllStudents();
		}
		return sapIdStudentsMap;
	}
	
	
		@RequestMapping(value = "/makeExecutiveRegistrationLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView makeExecutiveRegistrationLiveForm(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExecutiveExamOrderBean exam) {
			ModelAndView mav = new ModelAndView("makeExecutiveConfigurationLive");
			ExecutiveConfigurationDao dao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
			List<ExecutiveExamOrderBean> examsList = dao.getExecutiveExamOrderList(); 
			if(!examsList.isEmpty()){
				exam = examsList.get(0);
			}
			mav.addObject("examsList", examsList);
			mav.addObject("exam", exam);
			mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
			mav.addObject("monthList", SAS_EXAM_MONTH_LIST);
			mav.addObject("enrolMonthList", SAS_ENROLLMENT_MONTH_LIST);
			
			return mav;
		}
		
		@RequestMapping(value = "/makeExecutiveRegistrationLive", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView makeExecutiveRegistrationLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ExecutiveExamOrderBean exam) {
			
			ModelAndView mav = new ModelAndView("makeExecutiveConfigurationLive");
			ExecutiveConfigurationDao dao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
			try{
				
				if(!exam.getRegistrationStartDate().isEmpty() && !exam.getRegistrationEndDate().isEmpty()){
					dao.updateExamOrderRegistrationStats(exam);
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Registration for "+exam.getMonth()+" "+exam.getYear()+"  updated successfully");
				}else if(!exam.getHallTicketStartDate().isEmpty() && !exam.getHallTicketEndDate().isEmpty()){
					dao.updateExamOrderHallticketStats(exam);
					request.setAttribute("success","true");
					request.setAttribute("successMessage","HallTicket for "+exam.getMonth()+" "+exam.getYear()+"  updated successfully");
				}else if(!exam.getResultLive().isEmpty() && !exam.getResultDeclareDate().isEmpty()){
					String tempDate = exam.getResultDeclareDate().replaceAll("T", " ") +":00";
					exam.setResultDeclareDate(tempDate);
					dao.updateExamOrderResultStats(exam);
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Result is Live settings for "+exam.getMonth()+" "+exam.getYear()+" N Acad Year Month : "+exam.getAcadYear()+" "+exam.getAcadMonth()+" updated successfully");
				}
				
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in making live.");
			}
			List<ExecutiveExamOrderBean> examsList = dao.getExecutiveExamOrderList(); 
			mav.addObject("examsList", examsList);
			mav.addObject("exam", exam);
			mav.addObject("yearList", ACAD_YEAR_SAS_LIST);
			mav.addObject("monthList", SAS_EXAM_MONTH_LIST);
			mav.addObject("enrolMonthList", SAS_ENROLLMENT_MONTH_LIST);
			return mav;
		}
		
		
		@RequestMapping(value = "/examExecutiveRegistrationChecklist", method = {RequestMethod.GET, RequestMethod.POST})
		public String examExecutiveRegistrationChecklist(HttpServletRequest request, HttpServletResponse respnse,Model m) {
		
			return "examExecutiveRegistrationChecklist";
		}
			
		//Added for CRUD in Live_exam_subjects : START
		@RequestMapping(value = "/makeLiveSubjectsEntryForm", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView makeLiveSubjectsFormEntry(HttpServletRequest request, 
				HttpServletResponse response,@ModelAttribute ExecutiveExamOrderBean executiveExamOrderBean) 
		{ 
		ModelAndView mav=new ModelAndView("makeLiveSubjectsEntry");
		String userId = (String)request.getSession().getAttribute("userId");
		ExecutiveExamDao dao=(ExecutiveExamDao)act.getBean("executiveExamDao");
		request.getSession().setAttribute("executiveYearList", ACAD_YEAR_SAS_LIST);
		mav.addObject("executiveYearList",ACAD_YEAR_SAS_LIST);
		request.getSession().setAttribute("executiveMonthList", SAS_ENROLLMENT_MONTH_LIST);
		mav.addObject("executiveMonthList",SAS_ENROLLMENT_MONTH_LIST);
		mav.addObject("SAS_PROGRAM_STRUCTURE_LIST",SAS_PROGRAM_STRUCTURE_LIST);
		mav.addObject("SAS_PROGRAM_LIST",SAS_PROGRAM_LIST);
		mav.addObject("executiveExamOrderBean",executiveExamOrderBean);
		request.setAttribute("edit", "true");
		return mav;
		}
		
		@RequestMapping(value = "/makeLiveSubjectsEntry", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView makeLiveSubjectsEntry(HttpServletRequest request, HttpServletResponse response,
				@ModelAttribute ExecutiveExamOrderBean executiveExamOrderBean,
				@RequestParam(required=false) Integer id) 
		{ 
		
			ModelAndView mav=new ModelAndView("makeLiveSubjectsEntry");
			String userId = (String)request.getSession().getAttribute("userId");
			ExecutiveExamDao dao=(ExecutiveExamDao)act.getBean("executiveExamDao");
			try {
			executiveExamOrderBean.setCreatedBy(userId);
			executiveExamOrderBean.setLastModifiedBy(userId);
			dao.insertMakeLiveSubjectsEntry(executiveExamOrderBean);
			setSuccess(request, "Inserted Successfully");
			List<ExecutiveExamOrderBean> subjectsLive=dao.getSubjectsList(executiveExamOrderBean);
			
			mav.addObject("executiveYearList",ACAD_YEAR_SAS_LIST);
			mav.addObject("executiveMonthList",SAS_ENROLLMENT_MONTH_LIST);
			mav.addObject("SAS_PROGRAM_STRUCTURE_LIST",SAS_PROGRAM_STRUCTURE_LIST);
			mav.addObject("SAS_PROGRAM_LIST",SAS_PROGRAM_LIST);
			mav.addObject("subjectsLive",subjectsLive);
			
			if(subjectsLive != null && subjectsLive.size() > 0 ){
				mav.addObject("rowCount",subjectsLive.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("subjectEntryReportList",subjectsLive);
			request.setAttribute("edit", "true");
			}
			catch(Exception e) {
				setError(request, "Duplicate Subject Entries Not Allowed");
			}
			return mav;
		}
		
		@RequestMapping(value = "/getSubjectsBasedOnProgram", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView getSubjectsBasedOnProgram(HttpServletRequest request, HttpServletResponse response,
				@ModelAttribute ExecutiveExamOrderBean executiveExamOrderBean,
				@RequestParam String acadYear,String acadMonth,String examYear,String examMonth,String prgmStructApplicable,String program,String subject) {
			ModelAndView mav = new ModelAndView("makeLiveSubjectsEntry");
			ExecutiveExamDao dao=(ExecutiveExamDao)act.getBean("executiveExamDao");
			ExecutiveConfigurationDao edao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");

			List<String> subjectListBasedOnProgram=dao.getSubjectListBasedOnProgram(executiveExamOrderBean.getProgram(),executiveExamOrderBean.getPrgmStructApplicable());
			ArrayList<ExecutiveExamOrderBean> subjectsLive = edao.getExecutiveSubjectSetUp( acadYear, acadMonth, examYear, examMonth, prgmStructApplicable, program, subject); 

			mav.addObject("subjectsLive", subjectsLive);
			mav.addObject("subjectListBasedOnProgram",subjectListBasedOnProgram);
			mav.addObject("executiveYearList",ACAD_YEAR_SAS_LIST);
			mav.addObject("executiveMonthList",SAS_ENROLLMENT_MONTH_LIST);
			mav.addObject("SAS_PROGRAM_STRUCTURE_LIST",SAS_PROGRAM_STRUCTURE_LIST);
			mav.addObject("SAS_PROGRAM_LIST",SAS_PROGRAM_LIST);
			mav.addObject("executiveExamOrderBean",executiveExamOrderBean);
			
			request.getSession().setAttribute("subjectListBasedOnProgram", subjectListBasedOnProgram);
			request.getSession().setAttribute("SAS_PROGRAM_STRUCTURE_LIST", SAS_PROGRAM_STRUCTURE_LIST);
			request.getSession().setAttribute("SAS_PROGRAM_LIST", SAS_PROGRAM_LIST);
			request.setAttribute("edit", "true");
			
			return mav;
}
		
		
		@RequestMapping(value = "/editSubjectsEntry", method = {RequestMethod.POST})
		public String editSubjectsEntry(HttpServletRequest request, HttpServletResponse response, 
				@ModelAttribute ExecutiveExamOrderBean executiveExamOrderBean) {
			ModelAndView mav = new ModelAndView();
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			List<String> subjectListBasedOnProgram=dao.getSubjectListBasedOnProgram(executiveExamOrderBean.getProgram(),executiveExamOrderBean.getPrgmStructApplicable());
			mav.addObject("subjectListBasedOnProgram",subjectListBasedOnProgram);
			mav.addObject("executiveYearList",ACAD_YEAR_SAS_LIST);
			mav.addObject("executiveMonthList",SAS_ENROLLMENT_MONTH_LIST);
			mav.addObject("SAS_PROGRAM_STRUCTURE_LIST",SAS_PROGRAM_STRUCTURE_LIST);
			mav.addObject("SAS_PROGRAM_LIST",SAS_PROGRAM_LIST);
			mav.addObject("executiveExamOrderBean",executiveExamOrderBean);
			request.getSession().getAttribute("SAS_PROGRAM_LIST");
			request.getSession().getAttribute("SAS_PROGRAM_STRUCTURE_LIST");
			request.getSession().getAttribute("subjectListBasedOnProgram");
			dao.updateSubjectsEntry(executiveExamOrderBean);
			setSuccess(request, "Entry edited Successfully");
			request.setAttribute("edit", "false");
			return "makeLiveSubjectsEntry";
		}
		
		
		@RequestMapping(value = "/deleteSubjectsEntry", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView deleteSubjectsEntry(HttpServletRequest request, HttpServletResponse response, 
				@ModelAttribute ExecutiveExamOrderBean executiveExamOrderBean) {

			ModelAndView mav=new ModelAndView("makeLiveSubjectsEntry");
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			int id=executiveExamOrderBean.getId();
			dao.deleteSubjectsEntry(id);
			setSuccess(request, "Entry Deleted Successfully");
			return mav;
		}
		
		
		
		@RequestMapping(value = "/downloadSubjectEntriesReport", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView downloadSubjectEntriesReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
			/*ArrayList<ExecutiveExamOrderBean> subjectEntryReportList = (ArrayList<ExecutiveExamOrderBean>)request.getSession().getAttribute("subjectEntryReportList");*/
			ArrayList<ExecutiveExamOrderBean> subjectEntryReportList = (ArrayList<ExecutiveExamOrderBean>)request.getSession().getAttribute("subjectsLive");
			//m.addAttribute("subjectEntryReportList",subjectEntryReportList);
			return new ModelAndView ("subjectReportExcelView","subjectEntryReportList",subjectEntryReportList);
			
		
		}
		
		//END
		
		
		//code for insert AB records of executive programs Start
		@RequestMapping(value = "/insertExecutiveABRecordsForm", method = RequestMethod.GET)
		public String insertExecutiveABRecordsForm(HttpServletRequest request, HttpServletResponse respnse,Model m) {
			
			m.addAttribute("searchBean",new ExamBookingTransactionBean());
			m.addAttribute("yearList", currentYearList);
			return "insertExecutiveABRecordsForm";
		}
		
		@RequestMapping(value = "/searchExecutiveABRecordsToInsert", method = RequestMethod.POST)
		public String searchExecutiveABRecordsToInsert(HttpServletRequest request, HttpServletResponse response,Model m, @ModelAttribute ExamBookingTransactionBean searchBean){
			
			ExecutiveExamDao dao = (ExecutiveExamDao)act.getBean("executiveExamDao");
			List<ExamBookingTransactionBean> studentsList = dao.getExecutiveABRecords(searchBean);
			
			m.addAttribute("searchBean", searchBean);
			m.addAttribute("rowCount", studentsList != null ? studentsList.size() : 0);
			
			m.addAttribute("yearList", currentYearList);
			if(studentsList == null || studentsList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return "insertExecutiveABRecordsForm";
			}else{
				HashMap<String, StudentExamBean>  sapIdStudentsMap = getStudentsMap();
				for(ExamBookingTransactionBean bean : studentsList) {
					bean.setFirstName(sapIdStudentsMap.get(bean.getSapid()).getFirstName());
					bean.setLastName(sapIdStudentsMap.get(bean.getSapid()).getLastName());
				}
				
				setSuccess(request, "Please download AB report and upload under TEE marks");
			}

			m.addAttribute("studentsList", studentsList);
			request.getSession().setAttribute("studentsList", studentsList);
			return "insertExecutiveABRecordsForm";
		}
		
		@RequestMapping(value = "/downloadExecutiveABReport", method = RequestMethod.GET)
		public String downloadExecutiveABReport(HttpServletRequest request, HttpServletResponse response,Model m) {
			
			List<ExamBookingTransactionBean> studentsList= (List<ExamBookingTransactionBean>)request.getSession().getAttribute("studentsList");
			m.addAttribute("studentsList", studentsList);
			
			return "executiveAbsentReportExcelView";
		}
		//code for insert AB records of executive programs End
		
		
		//code to upload online executive marks 
		
		
		//added to dynamically get the details of subjects set up based on user current input
		@RequestMapping(value = "/getSubjectsSetUp", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView getSubjectsSetUp(HttpServletRequest request, HttpServletResponse respnse, 
				@ModelAttribute ExecutiveExamOrderBean exam,
				@RequestParam String acadYear,String acadMonth,String examYear,String examMonth,String prgmStructApplicable,String program,String subject) {
			ModelAndView mav = new ModelAndView("makeLiveSubjectsEntry");
			ExecutiveConfigurationDao dao = (ExecutiveConfigurationDao)act.getBean("executiveConfigurationDao");
			ArrayList<ExecutiveExamOrderBean> subjectsLive = dao.getExecutiveSubjectSetUp( acadYear, acadMonth, examYear, examMonth, prgmStructApplicable, program, subject); 
			if(!subjectsLive.isEmpty()){
				mav.addObject("subjectsLive", subjectsLive);
				//request.getSession().setAttribute("subjectsLive", subjectsLive);
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			exam.setProgram(program.trim());
			exam.setAcadYear(acadYear.trim());
			exam.setExamMonth(examMonth.trim());
			exam.setExamYear(examYear.trim());
			mav.addObject("executiveYearList",ACAD_YEAR_SAS_LIST);
			mav.addObject("executiveMonthList",SAS_ENROLLMENT_MONTH_LIST);
			mav.addObject("SAS_PROGRAM_STRUCTURE_LIST",SAS_PROGRAM_STRUCTURE_LIST);
			mav.addObject("SAS_PROGRAM_LIST",SAS_PROGRAM_LIST);
			mav.addObject("executiveExamOrderBean",exam);
			request.setAttribute("edit", "true");
			
			
			
			return mav;
		}

}
		

