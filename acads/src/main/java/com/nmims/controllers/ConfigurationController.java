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

import com.nmims.beans.ConfigurationAcads;
import com.nmims.daos.PCPBookingDAO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ConfigurationController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	PCPBookingDAO pcpBookingDAO;

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	
	public ArrayList<String> configurationList = new ArrayList<String>(Arrays.asList( 
			"PCP Registration")); 

	
	@ModelAttribute("configurationList")
	public ArrayList<String> getConfigurationList(){
		return this.configurationList;
	}
	
	 
	@RequestMapping(value = "/admin/changeConfigurationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("changeConfiguration");
		List<ConfigurationAcads> currentConfList = pcpBookingDAO.getCurrentConfigurationList(); 
		modelnView.addObject("currentConfList", currentConfList);
		
		ConfigurationAcads configuration = new ConfigurationAcads();
		m.addAttribute("configuration", configuration);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/changeConfiguration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView changeConfiguration(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ConfigurationAcads configuration) {
		logger.info("Make results live Page");
		ModelAndView modelnView = new ModelAndView("changeConfiguration");
		try{
			configuration.setLastModifiedBy((String)request.getSession().getAttribute("userId_acads"));
			pcpBookingDAO.updateConfiguration(configuration);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Date/Time changed successfully for "+configuration.getConfigurationType());
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in changing configuration.");
		}
		
		
		List<ConfigurationAcads> currentConfList = pcpBookingDAO.getCurrentConfigurationList(); 
		modelnView.addObject("currentConfList", currentConfList);
		modelnView.addObject("configuration", configuration);
		return modelnView;
	}
	
	
	
	
}

