package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.nmims.beans.Specialisation;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.SpecialisationService;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class SpecialisationControllers extends BaseController{
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	SpecialisationService service;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> acad_year_list; 
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> acad_month_list;
	
	static final Map<Integer, String> specializationTypeMasterkeys = ImmutableMap.of(
		    111, "Jul2019",
		  151 , "Oct2020",
		  160 , "Jul2022"
		);
	
	public HashMap<String, String> getSpecializationTypesMap(){
		HashMap<String, String> specializationTypesMap = new HashMap<String, String>();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ArrayList<Specialisation> specialisationList = sDao.getAllSpecialisation();
		for (Specialisation specialisation : specialisationList) {
			specializationTypesMap.put(Long.toString(specialisation.getId()), specialisation.getSpecializationType());
		}
		return specializationTypesMap;
	}
	
	@RequestMapping(value = "/updateSpecialisationInSFDCAndPortal", method = RequestMethod.POST)
	public ResponseEntity<HashMap<String, String>> updateSpecialisationInSFDC(@RequestBody Specialisation specialisation){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String, String> response = new HashMap<String, String>();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		MailSender mailer = (MailSender)act.getBean("mailer");
		
		String errorMessage = "";
		try {

			if(errorMessage == null || "".equals(errorMessage)){
				sDao.updateSpecilisationDetails(specialisation);
				response.put("success","true");
				response.put("successMessage","Profile updated successfully.");
			}else {
				response.put("error","true");
				response.put("errorMessage",errorMessage);
			}
		} catch (Exception e) {
			
			response.put("error","true");
			response.put("errorMessage", "Error in Specialisation profile.");
		}
		return new ResponseEntity<HashMap<String,String>>(response,headers,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/electiveReportForm", method = {RequestMethod.POST, RequestMethod.GET})
	public String electiveReportForm(Model m) {
		
		Specialisation specialisation = new Specialisation();
		m.addAttribute("specialisation", specialisation);
		m.addAttribute("yearList", acad_year_list);
		m.addAttribute("monthList", acad_month_list);
		m.addAttribute("specializationTypeMasterkeys", specializationTypeMasterkeys);
		m.addAttribute("specialisationCompleteReport", new Specialisation());
		return "electiveReport";
	}
	
	@RequestMapping(value = "/electiveReport", method = RequestMethod.POST)
	public ModelAndView electiveReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Specialisation specialisation) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
				
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ModelAndView modelnView = new ModelAndView("electiveReport");
		request.getSession().setAttribute("specialisation", specialisation);
		
		try {

			ArrayList<Specialisation> electiveReport = sDao.getElectiveCompletedReport(specialisation, getAuthorizedCodes(request));
			
			if (electiveReport.size() > 0 ) {
				modelnView.addObject("electiveCompletedRowCount",electiveReport.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("electiveReport", electiveReport);
			
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		
		modelnView.addObject("electiveCompleted", "true");
		modelnView.addObject("specialisation", specialisation);
		modelnView.addObject("yearList", acad_year_list);
		modelnView.addObject("monthList", acad_month_list);
		modelnView.addObject("specializationTypeMasterkeys", specializationTypeMasterkeys);
		modelnView.addObject("specialisationCompleteReport", new Specialisation());
		return modelnView;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadElectiveReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadElectiveReport(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ArrayList<Specialisation> electiveReport = (ArrayList<Specialisation>)request.getSession().getAttribute("electiveReport");
		request.getSession().setAttribute("getSpecializationTypesMap", getSpecializationTypesMap());
		return new ModelAndView("electiveReportExcelView","electiveReport",electiveReport);
	}
	
	@RequestMapping(value = "/electivePendingReport", method = RequestMethod.POST)
	public ModelAndView electivePendingReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Specialisation specialisation) {
	
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
				
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ModelAndView modelnView = new ModelAndView("electiveReport");
		request.getSession().setAttribute("specialisation", specialisation);
		
		try {
			ArrayList<Specialisation> electivePendingReport = sDao.getElectivesPendingReport(specialisation);
			
			if (electivePendingReport.size() > 0 ) {
				modelnView.addObject("electivePendingRowCount",electivePendingReport.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("electivePendingReport", electivePendingReport);
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		
		modelnView.addObject("electivePending", "true");
		modelnView.addObject("specialisation", specialisation);
		modelnView.addObject("yearList", acad_year_list);
		modelnView.addObject("monthList", acad_month_list);
		modelnView.addObject("specializationTypeMasterkeys", specializationTypeMasterkeys);
		modelnView.addObject("specialisationCompleteReport", new Specialisation());
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadElectivePendingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadElectivePendingReport(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Specialisation> electivePendingReport = (ArrayList<Specialisation>)request.getSession().getAttribute("electivePendingReport");
		request.getSession().setAttribute("getSpecializationTypesMap", getSpecializationTypesMap());
		return new ModelAndView("electivePendingReportExcelView","electivePendingReport",electivePendingReport);
	}
	
	@RequestMapping(value = "/moveStagingToTimeBoundTableForm", method = RequestMethod.GET)
	public String moveStagingToTimeBoundTableForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		TimeBoundUserMapping mappingBean = new TimeBoundUserMapping();
		
		m.addAttribute("mappingBean", mappingBean);
		m.addAttribute("yearList", acad_year_list);
		m.addAttribute("monthList", acad_month_list);
		
		return "moveStagingToTimeBoundTable";
	}
	
	@RequestMapping(value = "/moveStagingToTimeBoundTable", method = RequestMethod.POST)
	public ModelAndView moveStagingToTimeBoundTable(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TimeBoundUserMapping mappingBean){
		
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ModelAndView modelnView = new ModelAndView("moveStagingToTimeBoundTable");
		String isError = "";
		
		modelnView.addObject("yearList", acad_year_list);
		modelnView.addObject("monthList", acad_month_list);
		modelnView.addObject("mappingBean", mappingBean);
		
		if (!sDao.checkIsElectiveSelectionLive()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Elective Selection is live now. Please try after Elective Selection End date.");
			return modelnView;
		}
		
		ArrayList<Specialisation> stagingTableList = sDao.getAllStagingTableTimeBoundMapping(mappingBean);
		
		String userId = (String)request.getSession().getAttribute("userId");
		if(StringUtils.isBlank(userId))
			userId = "batchInsertSpecializationMappings";
		
		if (stagingTableList.size() > 0) {
			isError = sDao.batchInsertTimeBoundIds(stagingTableList);
			//insert specialization user subject mapping 
			isError = sDao.batchInsertSpecializationMappings(stagingTableList, mappingBean.getAcadYear(), mappingBean.getAcadMonth(), userId);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "0 Records found for "+mappingBean.getAcadMonth()+ " - " +mappingBean.getAcadYear());
			return modelnView;
		}
		
		if (StringUtils.isBlank(isError)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Moved "+stagingTableList.size()+" Records from Staging to Timebound table.");
			return modelnView;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error while moving data from Staging to Timebound table. Error : "+isError);
			return modelnView;
		}
	}

	@RequestMapping(value = "/moveStagingToTimeBoundTable_V2", method = RequestMethod.POST)
	public ModelAndView moveStagingToTimeBoundTable_V2(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TimeBoundUserMapping mappingBean){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ModelAndView modelnView = new ModelAndView("moveStagingToTimeBoundTable");
		
		modelnView.addObject("yearList", acad_year_list);
		modelnView.addObject("monthList", acad_month_list);
		modelnView.addObject("mappingBean", mappingBean);
		
		if (!sDao.checkIsElectiveSelectionLive()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Elective Selection is live now. Please try after Elective Selection End date.");
			return modelnView;
		}
		
		String userId = (String)request.getSession().getAttribute("userId");
		if(StringUtils.isBlank(userId))
			userId = "batchInsertSpecializationMappings";
		
		try {
			service.moveStagingToTimeBoundTable(mappingBean, userId);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Moved records from Staging to Timebound table.");
			return modelnView;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
			return modelnView;
		}
		
	}
	
	@RequestMapping(value = "/addSpecializationMapping", method = RequestMethod.GET)
	public ModelAndView specializationMapping() {
		
		ModelAndView modelAndView = new ModelAndView("specialization/addSpecializationMapping");
		
		modelAndView.addObject("acad_year_list", acad_year_list);
		modelAndView.addObject("acad_month_list", acad_month_list);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/electiveCompleteProdReport", method = RequestMethod.POST)
	public ModelAndView electiveCompleteProdReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Specialisation specialisation) {
	
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
	
		ModelAndView modelnView = new ModelAndView("electiveReport");
		request.getSession().setAttribute("specialisationCompleteReport", specialisation);
		modelnView.addObject("specializationTypeMasterkeys", specializationTypeMasterkeys);
		modelnView.addObject("yearList", acad_year_list);
		modelnView.addObject("monthList", acad_month_list);
		modelnView.addObject("electiveCompleteReport", "true");
		modelnView.addObject("specialisationCompleteReport", specialisation);
		
		try {
			
			if(StringUtils.isBlank(specialisation.getTerm()) || StringUtils.isBlank(specialisation.getAcadMonth()) || StringUtils.isBlank(specialisation.getAcadYear()) || StringUtils.isBlank(specialisation.getConsumerProgramStructureId()))
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Please select all the inputs.");
			}else {
				ArrayList<Specialisation> electiveCompleteProdReport = service.electiveCompleteProdReport(specialisation.getAcadMonth(),specialisation.getAcadYear(),specialisation.getTerm(),specialisation.getConsumerProgramStructureId());
			
				if (electiveCompleteProdReport.size() > 0 ) {
					modelnView.addObject("electiveCompleteProdRowCount",electiveCompleteProdReport.size());
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
				}
				request.getSession().setAttribute("electiveCompleteProdReport", electiveCompleteProdReport);
		}
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report. Error Message :- "+e.getMessage());
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadElectiveCompleteProdReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadElectiveCompleteProdReport(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Specialisation> electivePendingReport = (ArrayList<Specialisation>)request.getSession().getAttribute("electiveCompleteProdReport");
		request.getSession().setAttribute("getSpecializationTypesMap", getSpecializationTypesMap());
		request.getSession().setAttribute("getMdmMappingMap", service.getSubjectNameFromPssId());
		return new ModelAndView("electiveCompleteProdReportExcelView","electiveCompleteProdReport",electivePendingReport);
	}
	
	
}
