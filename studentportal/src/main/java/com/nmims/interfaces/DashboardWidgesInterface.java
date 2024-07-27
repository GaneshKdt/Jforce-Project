package com.nmims.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

public interface DashboardWidgesInterface {

	/*
	 * Create DashBoardWidges list
	 * */
	public ModelAndView DashBoardWidges(HttpServletRequest request);
	
	public ModelAndView ExamDataWidges(HttpServletRequest request);
	
}
