package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.TimeTableDAO;

@Controller
public class NotificationsController {

	@Autowired
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

	private ArrayList<String> facultyList = null;
	private final int pageSize = 10;

	
	public NotificationsController(){
	}
	
	public ArrayList<String> getFacultyList(){
		if(this.facultyList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.facultyList = dao.getAllFaculties();
		}
		return facultyList;
	}

	


}
