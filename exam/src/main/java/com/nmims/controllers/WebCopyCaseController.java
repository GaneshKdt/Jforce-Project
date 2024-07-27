package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.WebCopycaseBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.StudentService;
import com.nmims.views.AssignmentWebCopyCaseReportExcelView;

@Controller
@RequestMapping("/admin")
public class WebCopyCaseController {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Autowired
	AssignmentWebCopyCaseReportExcelView AssignmentWebCopyCaseReportExcelView;
	
	private static final Logger assignmentWebCopyCaseCaseLogger = LoggerFactory.getLogger("assignmentWebCopyCase");
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	private ArrayList<String> subjectList = null; 
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			this.subjectList = asignmentsDAO.getActiveSubjects();
		}
		return subjectList;
	}
	
	@RequestMapping(value = "/getWebCopyCaseReportForm", method = {RequestMethod.GET})
	public String getWebCopyCaseReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ResultDomain searchBean = new ResultDomain();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("examMonthList", EXAM_MONTH_LIST);
		m.addAttribute("subjectList", getSubjectList());
		return "assignment/getWebCopyCaseReport";
	}
	
	

	@RequestMapping(value = "/webCopyCaseCheckForm", method = {RequestMethod.GET})
	public String WebcopyCaseCheckForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		return "assignment/webCopyCaseCheck";
	}
	
	
	@RequestMapping(value = "/downloadAssignmentWebCopyCaseReport", method = {RequestMethod.POST})
	public ModelAndView downloadAssignmentWebCCReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultDomain searchBean)  {
	
		ModelAndView model = new ModelAndView("assignment/getCopyCaseReport");
		model.addObject("searchBean", searchBean);
		model.addObject("yearList", currentYearList);
		model.addObject("examMonthList", EXAM_MONTH_LIST);
		model.addObject("subjectList", getSubjectList());
		
		List<WebCopycaseBean> ccStudentList=new ArrayList<WebCopycaseBean>();
		try {
			 ccStudentList=studentService.getCcStudentList(searchBean.getMonth(),searchBean.getYear(),searchBean.getSubject());
		} catch (Exception e) {
			setError(request, "error while check copycase on web ::"+e.getMessage());
			assignmentWebCopyCaseCaseLogger.error("Error :: while generating webcopycase excel report "+e.getMessage());
		}
		
		return new ModelAndView(AssignmentWebCopyCaseReportExcelView,"unique1CCList",ccStudentList);
	}
	
	
	
	
}
