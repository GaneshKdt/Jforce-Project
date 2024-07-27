package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;

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

import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AISHEUGCExcelReportBean;
import com.nmims.beans.AISHEUGCReportsBean;
import com.nmims.services.impl.AISHEUGCReportsServiceImpl;


@Controller
public class AISHEUGCReportsController extends BaseController {
	
	@Autowired
	private AISHEUGCReportsServiceImpl aishugcReportsServiceImpl;

	

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;

	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018",
					"2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026"));
	

	@RequestMapping(value = "/admin/aisheugcReportForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String aisheugcReportForm(HttpServletRequest request, HttpServletResponse response, Model model
	

	) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		AISHEUGCReportsBean student = new AISHEUGCReportsBean();
		model.addAttribute("student", student);
		model.addAttribute("yearList", ACAD_YEAR_LIST);
		model.addAttribute("monthList", ACAD_MONTH_LIST);
		
		
		return "aisheugcReports";
	
	
	}
	@RequestMapping(value = "/admin/aisheugcReport", method = RequestMethod.POST)
	public ModelAndView aisheugcReport(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AISHEUGCReportsBean student)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("report_enrollmentYear",student.getEnrollmentYear());
		request.getSession().setAttribute("report_enrollmentMonth", student.getEnrollmentMonth());
		ModelAndView modelnView = new ModelAndView("aisheugcReports");
		modelnView.addObject("student", student);
		try
		{
	
	ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = aishugcReportsServiceImpl.getAllListOfProgram(student.getEnrollmentYear(),student.getEnrollmentMonth(),student.getSem());
				  request.getSession().setAttribute("AllListOfProgram", AllListOfProgram);
				 
				  modelnView.addObject("enrollmentYear", student.getEnrollmentYear());
				  modelnView.addObject("enrollmentMonth", student.getEnrollmentMonth());
				  modelnView.addObject("yearList", ACAD_YEAR_LIST);
				  modelnView.addObject("monthList", ACAD_MONTH_LIST);
				 
		
				  if(AllListOfProgram != null || AllListOfProgram.size() > 0){
					  modelnView.addObject("rowCount", AllListOfProgram.size());
						request.setAttribute("success", "true");
						request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
						} else {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "No records found.");	
					}
					}catch(Exception e){
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in generating Report.");
					}		
		return modelnView;
	}
	
	 
	@RequestMapping(value = "/admin/aisheugcReportdownloads", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView aisheugcReportdownloads(HttpServletRequest request, HttpServletResponse response		
	) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<AISHEUGCReportsBean> AllListOfProgram = (ArrayList<AISHEUGCReportsBean>) request.getSession().getAttribute("AllListOfProgram");
		return new ModelAndView("aisheugcReportsExcelView", "AllListOfProgram", AllListOfProgram);
	}
}