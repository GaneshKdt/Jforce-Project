package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AcadCycleFeedback;
import com.nmims.daos.PortalDao;
@Controller
public class SurveyController {

	@Autowired
	ApplicationContext act;
	
	@Value("${ACAD_YEAR_SAS_LIST}")
	private List<String> ACAD_YEAR_SAS_LIST;
	
	@Value("${SAS_ENROLLMENT_MONTH_LIST}")
	private List<String> SAS_ENROLLMENT_MONTH_LIST;
	
	
	@RequestMapping(value = "/admin/makeSurveyLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeSurveyLiveForm(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute AcadCycleFeedback survey) {
		ModelAndView mv = new ModelAndView("jsp/makeSurveyLive");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<AcadCycleFeedback> surveyConfList =pDao.getAllLiveSurveyDetails();
		int size = 0 ;
		if(!surveyConfList.isEmpty()){
			size = surveyConfList.size();
		}
		mv.addObject("rowcount",size);
		mv.addObject("monthList", SAS_ENROLLMENT_MONTH_LIST);
		mv.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mv.addObject("survey",survey);
		mv.addObject("surveyConfList",surveyConfList);
		return mv;
	}
	@RequestMapping(value = "/admin/makeSurveyLive", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeSurveyLive(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute AcadCycleFeedback survey) {
		ModelAndView mv = new ModelAndView("jsp/makeSurveyLive");
		String userId = (String)request.getSession().getAttribute("userId");
		survey.setCreatedBy(userId);
		survey.setLastModifiedBy(userId);
		//System.out.println("AcadCycleFeedback --- in makelive --- "+survey);
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		int status = pDao.upsertSuveryConfiguration(survey);
		ArrayList<AcadCycleFeedback> surveyConfList =pDao.getAllLiveSurveyDetails();
		if (status == 0){
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Flag updated successfully.");
		}else{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Flag not updated successfully.");
		}
		
		mv.addObject("monthList", SAS_ENROLLMENT_MONTH_LIST);
		mv.addObject("yearList", ACAD_YEAR_SAS_LIST);
		mv.addObject("survey",survey);
		mv.addObject("surveyConfList",surveyConfList);
		return mv;
	}
}
