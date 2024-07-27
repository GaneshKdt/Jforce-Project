package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.ExamBookingRefundRequestReportBean;
import com.nmims.beans.MettlTEEAttendanceReportBean;
import com.nmims.services.MettlTEEAttendanceReportService;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Controller
public class MettlTEEAttendanceReportController 
{
	/*Variables*/
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${ACAD_YEAR_LIST}")
	private String ACAD_YEAR_LIST;
	
	@Autowired
	private MettlTEEAttendanceReportService teeAttendanceService;
	
	private List<MettlTEEAttendanceReportBean> allProgramList;
	
	public static final Logger teeAttendanceLogger = LoggerFactory.getLogger("teeAttendance");
	
	
	
	/*Methods*/
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}

	}
	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	public List<MettlTEEAttendanceReportBean> getAllProgramList() throws Exception
	{
		return teeAttendanceService.getAllProgramList();
	}
	
	
	
	/*Mappings*/
	@RequestMapping(value = "admin/teeAttendanceReportForm", method = RequestMethod.GET)
	public ModelAndView teeAttendanceReportForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("teeAttendanceReport");
		
		try
		{
			allProgramList = getAllProgramList();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			teeAttendanceLogger.info("Error In Getting All Program List ==> "+e);
		}
		
		mv.addObject("teeAttendanceReport", null);
		mv.addObject("teeAttendance", new MettlTEEAttendanceReportBean());
		mv.addObject("programList", allProgramList);
		mv.addObject("yearList", ACAD_YEAR_LIST);
		
		return mv;
	}
		
	@RequestMapping(value = "admin/teeAttendanceReport", method = RequestMethod.POST)
	public ModelAndView teeAttendanceReport(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute MettlTEEAttendanceReportBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<MettlTEEAttendanceReportBean> teeAttendanceReport = new ArrayList<>();
		ModelAndView mv = new ModelAndView("teeAttendanceReport");
		
		try
		{
			teeAttendanceReport = teeAttendanceService.getTEEAttendanceReportByCycle(bean);
			
			if(teeAttendanceReport != null && teeAttendanceReport.size() > 0)
			{
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "TEE Attendance report generated Successfully!");
				request.getSession().setAttribute("teeAttendanceReports", teeAttendanceReport);
			}
			else
			{
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "0 records found!");
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			teeAttendanceLogger.info("Error In Getting TEE Attendance Report ==> "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to generate TEE Atendnace report, please try again!");
			request.getSession().setAttribute("teeAttendanceReports", null);
		}
		
		mv.addObject("programList", allProgramList);
		mv.addObject("yearList", ACAD_YEAR_LIST);
		mv.addObject("teeAttendance", bean);
		mv.addObject("teeAttendanceReport", teeAttendanceReport);
		
		return mv;
	}
	
	@RequestMapping(value = "/admin/TEEAttendanceReport", method = RequestMethod.GET)
	public ModelAndView downloadBlockStudentsCenterReport(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		List<ExamBookingRefundRequestReportBean> teeAttendanceReport = (List<ExamBookingRefundRequestReportBean>) request
				.getSession().getAttribute("teeAttendanceReports");

		return new ModelAndView("mettlTEEAttendanceReportExcelView", "teeAttendanceReport",
				teeAttendanceReport);
	}
}
