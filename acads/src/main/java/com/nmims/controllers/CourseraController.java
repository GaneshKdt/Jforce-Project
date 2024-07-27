package com.nmims.controllers;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CourseraMappingBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.services.CourseraService;


@Controller
@RequestMapping("/student")
public class CourseraController extends BaseController{
	
	@Autowired
	CourseraService courseraService;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@RequestMapping(value = "/showCourseraProducts", method = RequestMethod.GET)
	public ModelAndView viewPackages(HttpServletRequest request, HttpServletResponse response, Model model) {
		ModelAndView mv = new ModelAndView();
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		StudentAcadsBean student_acads = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		StudentAcadsBean registration = (StudentAcadsBean) request.getSession().getAttribute("studentRegData");
		
		String courseraPaymentURL="";
		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			courseraPaymentURL="https://ngasce.secure.force.com/nmLogin_new";
		}else {
			courseraPaymentURL="https://sandbox-ngasce.cs5.force.com/nmLogin_new";
		}
		
		try {
			boolean isCorusraApplicable = courseraService.isCourseraProgramApplicableForMasterKey(student_acads.getConsumerProgramStructureId());
			//If Coursera not applicable 
			if (!isCorusraApplicable) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Coursera is not applicable for you!");
				mv.setViewName("courseraApplicable");
				return mv;
			}
			CourseraMappingBean bean = courseraService.checkStudentPaidForCoursera(student_acads.getSapid());
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expiryDate = bean.getExpiryDate();
			boolean isBefore = now.isBefore(expiryDate);

			if (isBefore) {
				mv.addObject("sapId", student_acads.getSapid());
				mv.addObject("CourseraAccess", true);
				mv.setViewName("courseraLogin");
			} else {
				mv.addObject("CourseraAccess", false);
				mv.setViewName("courseraProduct");
				
				String leanersURL = courseraService.getLeanersURL(registration);
				String dob = student_acads.getDob();
				dob = DateTimeHelper.getDateInFormat("yyyy-MM-dd", dob);
				
				mv.addObject("sapId", student_acads.getSapid());
				mv.addObject("dob", dob);
				mv.addObject("leanersURL", leanersURL);
				mv.addObject("courseraPaymentURL", courseraPaymentURL);
			}

		} catch (Exception e) {
//			e.printStackTrace();
//			If Coursera not yet purchased by the student
			mv.setViewName("courseraProduct");
			
			String leanersURL = courseraService.getLeanersURL(registration);
			String dob = student_acads.getDob();
			dob = DateTimeHelper.getDateInFormat("yyyy-MM-dd", dob);
			
			mv.addObject("sapId", student_acads.getSapid());
			mv.addObject("dob", dob);
			mv.addObject("leanersURL", leanersURL);
			mv.addObject("courseraPaymentURL", courseraPaymentURL);
		}

		return mv;
	}
	
	/*@RequestMapping(value = "/showCourseraLogin", method = RequestMethod.GET)
	public ModelAndView showCourseraLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		ModelAndView mv=new ModelAndView("courseraLogin");
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		StudentAcadsBean student=(StudentAcadsBean) request.getSession().getAttribute("student_acads");
		mv.addObject("sapId", student_acads.getSapid());
		
		return mv;
	}*/

}
