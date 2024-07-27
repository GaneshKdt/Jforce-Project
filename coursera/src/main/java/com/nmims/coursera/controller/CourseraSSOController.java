package com.nmims.coursera.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.coursera.beans.CourseraStudentSSOBean;
import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.services.CourseraUsersService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CourseraSSOController {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	CourseraUsersService courseraUsersService;

	@RequestMapping(value = "/coursera_sso_student", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView coursera_sso_student(HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView("courseraRedirect");
		try {
			String sapid = request.getParameter("sapid");
			CourseraStudentSSOBean ssoDetails = new CourseraStudentSSOBean();
			
			StudentCourseraBean studentInfo = courseraUsersService.getSingleStudentsData(sapid);
			String learnerURL = courseraUsersService.getStudentlearnerURL(sapid);
			
			ssoDetails.setSapid(studentInfo.getSapid());
			ssoDetails.setEmailId(studentInfo.getEmailId());
			ssoDetails.setFirstName(studentInfo.getFirstName());
			ssoDetails.setLastName(studentInfo.getLastName());
			ssoDetails.setLearnerURL(learnerURL);
			
			request.getSession().setAttribute("courseraSSODetails", ssoDetails);
			modelAndView.addObject("redirectURL", learnerURL);
			
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView = new ModelAndView("courseraErrorRedirect");
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/m/coursera_sso_student", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView m_coursera_sso_student(HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView("courseraRedirect");
		try {
			String sapid = request.getParameter("sapid");
			CourseraStudentSSOBean ssoDetails = new CourseraStudentSSOBean();
			
			StudentCourseraBean studentInfo = courseraUsersService.getSingleStudentsData(sapid);
			String learnerURL = courseraUsersService.getStudentlearnerURL(sapid);
			
			ssoDetails.setSapid(studentInfo.getSapid());
			ssoDetails.setEmailId(studentInfo.getEmailId());
			ssoDetails.setFirstName(studentInfo.getFirstName());
			ssoDetails.setLastName(studentInfo.getLastName());
			ssoDetails.setLearnerURL(learnerURL);
			
			request.getSession().setAttribute("courseraSSODetails", ssoDetails);
			modelAndView.addObject("redirectURL", learnerURL);
			
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView = new ModelAndView("courseraErrorRedirect");
		}
		
		return modelAndView;
	}
}
