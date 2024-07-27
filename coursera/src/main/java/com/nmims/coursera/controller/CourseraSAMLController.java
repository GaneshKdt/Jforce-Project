package com.nmims.coursera.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.coursera.beans.CourseraStudentSSOBean;
import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.helpers.openSAML.CourseraSamlRequestHelper;
import com.nmims.coursera.services.CourseraUsersService;

@Controller
public class CourseraSAMLController {
	
	private static final Logger courseraSamlSSOLogger = LoggerFactory.getLogger("coursera_saml_sso");
	
	@Autowired
	CourseraSamlRequestHelper samlHelper;
	
	@Autowired
	CourseraUsersService courseraUsersService;
	
	@RequestMapping(value = "/coursera_sso", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView coursera_sso(HttpServletRequest request) {
		try {
			String samlRequest = request.getParameter("SAMLRequest");
			courseraSamlSSOLogger.info("SAMLRequest :"+samlRequest);
			CourseraStudentSSOBean ssoDetails = new CourseraStudentSSOBean();
			
			if(request.getSession().getAttribute("courseraSSODetails") != null) {
				ModelAndView modelAndView = new ModelAndView("postSamlForm");
				ssoDetails = (CourseraStudentSSOBean) request.getSession().getAttribute("courseraSSODetails");
				courseraSamlSSOLogger.info("coursera_sso "+ssoDetails.getSapid());
				
				StudentCourseraBean studentInfo = courseraUsersService.getSingleStudentsData(ssoDetails.getSapid());
				
				// Get student details
				samlHelper.generateSamlRequestForCoursera(samlRequest, request, modelAndView, studentInfo);
				
				return modelAndView;
			}else {
				ModelAndView modelAndView = new ModelAndView("courseraSSOError");
				modelAndView.addObject("error", "No Logged in user found!");

				courseraSamlSSOLogger.info("coursera_sso No Logged in user found ");
				return modelAndView;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			ModelAndView modelAndView = new ModelAndView("courseraSSOError");
			modelAndView.addObject("error", "Internal Server Error!");
			modelAndView.addObject("errorHidden", e.getMessage());
			
			courseraSamlSSOLogger.info("Exception in coursera_sso ", e);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/coursera_sso_idp_metadata", method = { RequestMethod.GET, RequestMethod.POST })
	public void coursera_sso_idp_metadata(HttpServletResponse response) {
		String metadata = "";
		
		try {
			metadata = samlHelper.generateCourseraMetadata();
			response.setContentType("application/xml");
			response.setContentLength(metadata.getBytes().length);
			
			InputStream is = IOUtils.toInputStream(metadata, StandardCharsets.UTF_8);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			metadata = "Error " + e.getMessage();
			throw new RuntimeException("IOError writing file to output stream" + " Error " + e.getMessage());
		}
	}
	
}
