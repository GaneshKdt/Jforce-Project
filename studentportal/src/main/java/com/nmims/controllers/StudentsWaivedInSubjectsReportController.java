package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.services.StudentService;
import com.nmims.views.StudentWaivedinExcelReportView;

@Controller
public class StudentsWaivedInSubjectsReportController extends BaseController{

	
	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentWaivedinExcelReportView studentWaivedinExcelReportView;
	
	
	@Value("${ACAD_MONTH_LIST}")
	private List<String> ACAD_MONTH_LIST; 
	
	@Value("${ACAD_YEAR_LIST}")
	private List<String> ACAD_YEAR_LIST;
	
	@RequestMapping(value = "/admin/getWaivedInSubjectsReportForm", method = {RequestMethod.GET})
	public String getWaivedInSubjectsReportFrm(@ModelAttribute(name = "studentBean") StudentStudentPortalBean studentBean,Model m) {
		m.addAttribute("acadMonthList",ACAD_MONTH_LIST);
		m.addAttribute("acadYearList",ACAD_YEAR_LIST);
		
		return "jsp/waivedinSubjectsReport";
	}
	
	@RequestMapping(value = "/admin/getWaivedInSubjectsReport", method = {RequestMethod.POST})
	public ModelAndView getWaivedInSubjectsReport(HttpServletRequest request,HttpServletResponse response,@ModelAttribute(name = "studentBean") StudentStudentPortalBean studentBean) {
		
		ModelAndView modelnView = new ModelAndView("jsp/waivedinSubjectsReport");
		List<StudentStudentPortalBean> studentlist=new ArrayList<StudentStudentPortalBean>();
		try {
			studentlist=studentService.getWaivedinSubjectsReport(studentBean.getAcadMonth(), studentBean.getAcadYear());
		} catch (Exception e) {
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "error while searching waivedIn subject for month :"+studentBean.getAcadMonth()+" and Year :"+studentBean.getAcadYear());
			return  new ModelAndView("jsp/getWaivedInSubjectsReportForm");
		}
		
		if(!(studentlist.size()>0)) {
			setError(request,"No records found for added filters");
		}
		request.getSession().setAttribute("studentExtraSubjectlist",studentlist);
		modelnView.addObject("studentlist", studentlist);
		modelnView.addObject("studentBean", studentBean);
		modelnView.addObject("acadMonthList",ACAD_MONTH_LIST);
		modelnView.addObject("acadYearList",ACAD_YEAR_LIST);
		
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/downloadWaivedInReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudents(HttpServletRequest request, HttpServletResponse response ) {
		List<StudentStudentPortalBean> studentlist=null;
		studentlist=(List<StudentStudentPortalBean>) request.getSession().getAttribute("studentExtraSubjectlist");
		if(!studentlist.isEmpty())
		return new ModelAndView(studentWaivedinExcelReportView,"StudentPortalBeanList",studentlist);
		else 
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", " Excel cannot be generated because no data is available");
			return  new ModelAndView("jsp/waivedinSubjectsReport");
		}
	}
	
}
