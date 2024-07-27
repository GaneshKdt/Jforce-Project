package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ParseConversionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.interfaces.DashboardWidgesInterface;

@Controller
public class DashboardWidgeController implements DashboardWidgesInterface {

	@Autowired
	ApplicationContext act;
	
	@Value("${ACAD_YEAR_LIST}")
	private int[] yearList;
	
	@Value("${ACAD_MONTH_LIST}")
	private String[] monthList;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String year;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String month;
	
	@GetMapping("/DashBoardWidge")
	@Override
	public ModelAndView DashBoardWidges(HttpServletRequest request) {
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		//get Missing users information
		ArrayList<StudentStudentPortalBean> studentInfoCheck = studentDao.getStudentWithNullDataRow();
		ModelAndView mv = new ModelAndView("jsp/studentEmptyData");
		mv.addObject("IncorrectStudentDataList",studentInfoCheck);
		return mv;
	}

	@RequestMapping(value="/DashboardExamDataWidge",method= {RequestMethod.GET})
	@Override
	public ModelAndView ExamDataWidges(HttpServletRequest request) {
		
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		int year = request.getParameter("year") != null?Integer.parseInt(request.getParameter("year")):0;
		String month = request.getParameter("month");
		if(year == 0 || month == null) {
			month = this.month;
			
			year = Integer.parseInt(this.year);
		}
		
		
		
		int ExamOfflineBookedCount = studentDao.getStudentOfflineBookedSeatCount(month,year);
		
	
		
		int ExamOnlineBookedCount = studentDao.getStudentOnlineBookedSeatCount(month,year);
		
		
		
		int ExamOnlineReleaseSeatCount = studentDao.getStudentOnlineReleaseSeatCount(month,year);
		
		
		int ExamOfflineReleaseSeatCount = studentDao.getStudentOfflineReleaseSeatCount(month,year);
		

		
		int TwiceExamBooking = studentDao.getStudentBooktwice(month,year);
		
		
		
		ModelAndView mv = new ModelAndView("jsp/DashBoardExamDataRecord");
		mv.addObject("selectedYear",year);
		mv.addObject("selectedMonth",month);
		mv.addObject("years",this.yearList);
		mv.addObject("months",this.monthList);
		mv.addObject("ExamOfflineBookedCount",ExamOfflineBookedCount);
		mv.addObject("ExamOnlineBookedCount",ExamOnlineBookedCount);
		mv.addObject("ExamOnlineReleaseSeatCount",ExamOnlineReleaseSeatCount);
		mv.addObject("ExamOfflineReleaseSeatCount",ExamOfflineReleaseSeatCount);
		mv.addObject("TwiceExamBooking",TwiceExamBooking);
		return mv;
	}



	
	
}
