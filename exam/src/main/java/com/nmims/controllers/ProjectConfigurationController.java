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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class ProjectConfigurationController extends BaseController {


	@Autowired
	ProjectTitleDAO projectTitleDAO;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@RequestMapping(value = "/admin/projectConfigurationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitlesForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("project/configuration/add");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		
		modelnView.addObject("configurationList", projectTitleDAO.getAllProjectConfigurations());
		modelnView.addObject("projectConfiguration", new ProjectConfiguration());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		return modelnView;
	}

	@RequestMapping(value = "/admin/projectConfiguration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ProjectConfiguration projectConfigurations){
		ModelAndView modelnView = new ModelAndView("project/configuration/add");

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		projectConfigurations.setUpdatedBy(userId);
		projectConfigurations.setCreatedBy(userId);

		ExcelHelper excelHelper = new ExcelHelper();
		List<ProjectConfiguration> configurations = excelHelper.addExcelProjectConfigurations(projectConfigurations);
		

		List<ProjectConfiguration> successList = new ArrayList<ProjectConfiguration>();
		List<ProjectConfiguration> errorList = new ArrayList<ProjectConfiguration>();
		for (ProjectConfiguration bean : configurations) {
			if(StringUtils.isBlank(bean.getError())) {

				boolean mappingExists = projectTitleDAO.checkIfProgramSemSubjectIdExists(bean);
				if(mappingExists) {
					try {
	
						String programSemSubjId = projectTitleDAO.getProgramSemSubjectId(bean.getConsumerType(), bean.getProgramCode(), bean.getProgramStructure(), bean.getSubject());
						bean.setProgramSemSubjId(programSemSubjId);
						
						boolean subjectActive = projectTitleDAO.checkIfSubjectActive(programSemSubjId);
						if(!subjectActive) {
							throw new Exception("Subject Inactive for mapping!");
						}
						projectTitleDAO.saveProjectConfiguration(bean);
						successList.add(bean);
					}catch (Exception e) {
						bean.setError("Error Inserting Records : " + e.getMessage());
						errorList.add(bean);
					}
				} else {
					bean.setError("Subject ID Not Found!");
					errorList.add(bean);
				}
			} else {
				errorList.add(bean);
			}
		}

		modelnView.addObject("successList", successList);
		modelnView.addObject("errorList", errorList);
		modelnView.addObject("configurationList", projectTitleDAO.getAllProjectConfigurations());
		modelnView.addObject("projectConfiguration", projectConfigurations);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		setSuccess(request, successList.size() + " records inserted successfully!");
		if(errorList.size() > 0) {
			setError(request, errorList.size() + " records were not inserted. Kindly check the table below for the list of errors.");
		}
		return modelnView;
	}


	@RequestMapping(value="/admin/updateProjectConfigurationForm")
	public ModelAndView updateProjectTitleForm(@ModelAttribute ProjectConfiguration projectConfiguration, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/configuration/update");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(projectConfiguration == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");

			modelnView.addObject("projectConfiguration", new ProjectConfiguration());
			return modelnView;
		}
		// check fields
		if(projectConfiguration.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			
			modelnView.addObject("projectConfiguration", new ProjectConfiguration());
			return modelnView;
		}
		
		try {
			modelnView.addObject("projectConfiguration", projectTitleDAO.getSingleProjectConfiguration(projectConfiguration));
			return modelnView;
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error retrieving live setting details!");

			modelnView.addObject("projectConfiguration", new ProjectConfiguration());
			return modelnView;
		}

	}
	
	@RequestMapping(value="/admin/updateProjectConfiguration" , method=RequestMethod.POST)
	public ModelAndView updateProjectTitle(@ModelAttribute ProjectConfiguration projectConfiguration, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("project/configuration/update");
		modelnView.addObject("projectConfiguration", projectConfiguration);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		if(projectConfiguration == null) {
			projectConfiguration = new ProjectConfiguration();
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		// check fields
		if(projectConfiguration.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			
			projectConfiguration.setUpdatedBy(userId);
			
			String error = projectTitleDAO.updateProjectConfiguration(projectConfiguration);
			
			if(StringUtils.isBlank(error)) {
				modelnView.addObject("success", "true");
				modelnView.addObject("successMessage", "Successfully inserted record.");
			} else {
				modelnView.addObject("error", "true");
				modelnView.addObject("errorMessage", error);
			}
			
			modelnView.addObject("projectConfiguration", projectTitleDAO.getSingleProjectConfiguration(projectConfiguration));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error updating time table details : " + e.getMessage());
		}
		
		return modelnView;
	}
	

	@RequestMapping(value = "/admin/deleteProjectConfiguration", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> deleteProjectConfiguration(HttpServletResponse response, HttpServletRequest request) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

		String id = request.getParameter("id");
		String message = projectTitleDAO.deleteProjectConfiguration(id);
		res.put("message", message);
		res.put("status", StringUtils.isBlank(message) ? "fail" : "success");
		
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	

	@RequestMapping(value="/admin/projectModuleExtendedForm")
	public ModelAndView projectModuleExtendedForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/extension/add");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("inputBean", new ProjectModuleExtensionBean());
		modelnView.addObject("extendedList", projectTitleDAO.getProjectModuleExtensionList());
		return modelnView;
	}

	
	@RequestMapping(value="/admin/addProjectModuleExtended" , method=RequestMethod.POST)
	public ModelAndView addProjectModuleExtendedForm(@ModelAttribute ProjectModuleExtensionBean inputBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/extension/add");
		modelnView.addObject("inputBean", new ProjectModuleExtensionBean());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		if(inputBean == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		// check fields
		if(StringUtils.isBlank(inputBean.getSapid()) || StringUtils.isBlank(inputBean.getSubject())) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			inputBean.setUpdatedBy(userId);
			inputBean.setCreatedBy(userId);
			
			
			
			String error = "";
			String pssId = projectTitleDAO.getProgramSemSubjectIdForStudent(inputBean.getSapid(), inputBean.getSubject());
			inputBean.setProgramSemSubjId(pssId);
			if(StringUtils.isBlank(error)) {
				error = projectTitleDAO.insertProjectExtension(inputBean);
			}
			
			if(StringUtils.isBlank(error)) {
				setSuccess(request, "Successfully inserted record.");
			} else {
				setError(request, "Error : " + error);
			}
		}catch (Exception e) {
			
			setError(request, "Error : " + e.getMessage());
		}

		modelnView.addObject("extendedList", projectTitleDAO.getProjectModuleExtensionList());
		return modelnView;
	}

	@RequestMapping(value = "/admin/deleteModuleExtension", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> deleteModuleExtension(HttpServletResponse response, HttpServletRequest request) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

		String id = request.getParameter("id");
		String message = projectTitleDAO.deleteProjectModuleExtension(id);
		res.put("message", message);
		res.put("status", StringUtils.isBlank(message) ? "fail" : "success");
		
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	



	@RequestMapping(value="/admin/updateProjectExtensionForm")
	public ModelAndView updateProjectLiveConfigurationForm(@ModelAttribute ProjectModuleExtensionBean projectExtension, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/extension/update");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(projectExtension == null) {
			setError(request, "Error!");
			return modelnView;
		}
		// check fields
		if(projectExtension.getId() == null) {
			setError(request, "Error!");
			return modelnView;
		}
		
		try {
			modelnView.addObject("projectExtension", projectTitleDAO.getSingleProjectModuleExtension(projectExtension));
			return modelnView;
		}catch (Exception e) {
			
			setError(request, "Error retrieving live setting details!");

			modelnView.addObject("projectExtension", new ProjectModuleExtensionBean());
			return modelnView;
		}

	}
	
	@RequestMapping(value="/admin/updateProjectExtension" , method=RequestMethod.POST)
	public ModelAndView updateProjectExtension(@ModelAttribute ProjectModuleExtensionBean projectExtension, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("project/extension/update");
		modelnView.addObject("projectExtension", projectExtension);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		if(projectExtension == null) {
			setError(request, "Error!");
			return modelnView;
		}
		// check fields
		if(projectExtension.getId() == null) {
			setError(request, "Error!");
			return modelnView;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			projectExtension.setUpdatedBy(userId);
			
			String error = projectTitleDAO.updateProjectModuleExtension(projectExtension);
			if(StringUtils.isBlank(error)) {
				setSuccess(request, "Successfully inserted record.");
			} else {
				setError(request, error);
			}
		}catch (Exception e) {
			
			setError(request, "Error updating time table details : " + e.getMessage());
		}

		modelnView.addObject("projectExtension", projectTitleDAO.getSingleProjectModuleExtension(projectExtension));
		return modelnView;
	}
}
