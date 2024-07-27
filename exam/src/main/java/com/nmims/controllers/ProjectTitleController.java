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
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.ProjectTitleConfig;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class ProjectTitleController extends BaseController {

	@Autowired
	ProjectTitleDAO projectTitlesDAO;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	private ArrayList<String> subjectList = null; 

	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}
	
	@RequestMapping(value = "/addProjectTitlesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitlesForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("project/title/add");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		
		List<ProjectTitle> titleList = projectTitlesDAO.getAllProjectTitles();
		
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("titleList", titleList);
		modelnView.addObject("projectTitle", new ProjectTitle());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		return modelnView;
	}

	@RequestMapping(value = "/addProjectTitles", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ProjectTitle projectTitle){
		ModelAndView modelnView = new ModelAndView("project/title/add");

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		projectTitle.setUpdatedBy(userId);
		projectTitle.setCreatedBy(userId);

		ExcelHelper excelHelper = new ExcelHelper();
		List<ProjectTitle> listOfTitles = excelHelper.addExcelProjectTitles(projectTitle);
		

		List<ProjectTitle> successList = new ArrayList<ProjectTitle>();
		List<ProjectTitle> errorList = new ArrayList<ProjectTitle>();
		for (ProjectTitle bean : listOfTitles) {
			if(StringUtils.isBlank(bean.getError())) {

				boolean mappingExists = projectTitlesDAO.checkIfPSSIdExists(bean.getConsumerTypeId(), bean.getProgramId(), bean.getProgramStructureId(), bean.getSubject());
				if(mappingExists) {
					try {
	
						String prgm_sem_subj_id = projectTitlesDAO.getProgramSemSubjectIdByKeys(bean.getConsumerTypeId(), bean.getProgramId(), bean.getProgramStructureId(), bean.getSubject());
						bean.setPrgm_sem_subj_id(prgm_sem_subj_id);

						boolean subjectActive = projectTitlesDAO.checkIfSubjectActive(bean.getPrgm_sem_subj_id());
						if(!subjectActive) {
							throw new Exception("Subject Inactive for mapping!");
						}
						
						String program = projectTitlesDAO.getProgramById(bean.getProgramId());
						bean.setProgramName(program);
						String programStructure = projectTitlesDAO.getProgramStrucutreById(bean.getProgramStructureId());
						bean.setProgramStructure(programStructure);
						
						projectTitlesDAO.saveProjectTitle(bean);
						successList.add(bean);
					}catch (Exception e) {
						
						bean.setError("Error Inserting Records : " + e.getMessage());
						errorList.add(bean);
					}
				}
			} else {
				errorList.add(bean);
			}
		}

		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("successList", successList);
		modelnView.addObject("errorList", errorList);
		modelnView.addObject("titleList", projectTitlesDAO.getAllProjectTitles());
		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		setSuccess(request, successList.size() + " records inserted successfully!");
		setError(request, errorList.size() + " records were not inserted. Kindly check the table below for the list of errors.");
		return modelnView;
	}

	@RequestMapping(value="/updateProjectTitleForm")
	public ModelAndView updateProjectTitleForm(@ModelAttribute ProjectTitle projectTitle, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/title/update");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(projectTitle == null) {
			projectTitle = new ProjectTitle();
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			modelnView.addObject("projectTitle", projectTitle);
			return modelnView;
		}
		
		// check fields
		if(projectTitle.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			modelnView.addObject("projectTitle", projectTitle);
			return modelnView;
		}
		try {
			projectTitle = projectTitlesDAO.getSingleProjectTitle(projectTitle.getId());
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error retrieving live setting details!");
		}

		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("subjectList", getSubjectList());
		return modelnView;
	}
	
	@RequestMapping(value="/updateProjectTitle" , method=RequestMethod.POST)
	public ModelAndView updateProjectTitle(@ModelAttribute ProjectTitle projectTitle, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("project/title/update");
		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		if(projectTitle == null) {
			projectTitle = new ProjectTitle();
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		// check fields
		if(projectTitle.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			
			projectTitle.setUpdatedBy(userId);
			
			String error = projectTitlesDAO.updateProjectTopic(projectTitle);
			
			if(StringUtils.isBlank(error)) {
				modelnView.addObject("success", "true");
				modelnView.addObject("successMessage", "Successfully inserted record.");
			} else {
				modelnView.addObject("error", "true");
				modelnView.addObject("errorMessage", error);
			}
			
			modelnView.addObject("projectTitle", projectTitlesDAO.getSingleProjectTitle(projectTitle.getId()));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error updating time table details : " + e.getMessage());
		}
		
		return modelnView;
	}

	@RequestMapping(value = "/toggleProjectTitleActive", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> toggleStudentBooking(HttpServletResponse response, HttpServletRequest request) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

		String id = request.getParameter("id");
		String active = request.getParameter("active");
		String message = projectTitlesDAO.toggleActiveProjectTitle(id, active);
		res.put("message", message);
		res.put("status", StringUtils.isBlank(message) ? "fail" : "success");
		
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/addProjectTitlesConfigForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitlesConfigForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("project/title_config/add");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("titleList", projectTitlesDAO.getAllProjectTitleConfig());
		modelnView.addObject("projectTitle", new ProjectTitleConfig());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		return modelnView;
	}

	@RequestMapping(value = "/addProjectTitlesConfig", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addProjectTitlesConfig(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ProjectTitleConfig projectTitle){
		ModelAndView modelnView = new ModelAndView("project/title_config/add");

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
	
		List<ProjectTitleConfig> successList = new ArrayList<ProjectTitleConfig>();
		List<ProjectTitleConfig> errorList = new ArrayList<ProjectTitleConfig>();

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(projectTitle.getProgramId(), projectTitle.getProgramStructureId(), projectTitle.getConsumerTypeId());
		
		for (String consumerProgramStructureId : consumerProgramStructureIds) {

			ProjectTitleConfig bean = new ProjectTitleConfig();
			bean.setUpdatedBy(userId);
			bean.setCreatedBy(userId);
			bean.setActive("Y");
			bean.setSubject(projectTitle.getSubject());
			bean.setExamMonth(projectTitle.getExamMonth());
			bean.setExamYear(projectTitle.getExamYear());
			bean.setStart_date(projectTitle.getStart_date());
			bean.setEnd_date(projectTitle.getEnd_date());

			String program = projectTitlesDAO.getProgramByConsumerProgramStructureId(consumerProgramStructureId);
			bean.setProgramName(program);
			
			String programStructure = projectTitlesDAO.getProgramStrucutreByConsumerProgramStructureId(consumerProgramStructureId);
			bean.setProgramStructure(programStructure);
			
			boolean mappingExists = projectTitlesDAO.checkIfPSSIdExistsByMasterKey(consumerProgramStructureId, projectTitle.getSubject());
			if(mappingExists) {
				try {
					
					String prgm_sem_subj_id = projectTitlesDAO.getProgramSemSubjectIdByMasterKey(consumerProgramStructureId, projectTitle.getSubject());
					bean.setProgramSemSubjId(prgm_sem_subj_id);

					boolean subjectActive = projectTitlesDAO.checkIfSubjectActive(prgm_sem_subj_id);
					if(!subjectActive) {
						throw new Exception("Subject Inactive for mapping!");
					}
					
					projectTitlesDAO.saveProjectTitleConfig(bean);
					successList.add(bean);
				}catch (Exception e) {
					bean.setError("Error Inserting Records : " + e.getMessage());
					errorList.add(bean);
					
				}
			}
		}

		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("successList", successList);
		modelnView.addObject("errorList", errorList);
		modelnView.addObject("titleList", projectTitlesDAO.getAllProjectTitleConfig());
		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		setSuccess(request, successList.size() + " records inserted/updated successfully!");
		setError(request, errorList.size() + " records were not inserted. Kindly check the table below for the list of errors.");
		return modelnView;
	}

	@RequestMapping(value="/updateProjectTitleFormConfig")
	public ModelAndView updateProjectTitleConfigForm(@ModelAttribute ProjectTitleConfig projectTitle, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("project/title_config/update");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(projectTitle == null) {
			projectTitle = new ProjectTitleConfig();
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			modelnView.addObject("projectTitle", projectTitle);
			return modelnView;
		}
		
		// check fields
		if(projectTitle.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			modelnView.addObject("projectTitle", projectTitle);
			return modelnView;
		}
		try {
			projectTitle = projectTitlesDAO.getSingleProjectTitleConfig(projectTitle.getId());
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error retrieving live setting details!");
		}

		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("subjectList", getSubjectList());
		return modelnView;
	}
	
	@RequestMapping(value="/updateProjectTitleConfig" , method=RequestMethod.POST)
	public ModelAndView updateProjectTitleConfig(@ModelAttribute ProjectTitleConfig projectTitle, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("project/title_config/update");
		modelnView.addObject("projectTitle", projectTitle);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		if(projectTitle == null) {
			projectTitle = new ProjectTitleConfig();
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		// check fields
		if(projectTitle.getId() == null) {
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error!");
			return modelnView;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			
			projectTitle.setUpdatedBy(userId);
			
			String error = projectTitlesDAO.updateProjectTitleConfig(projectTitle);
			
			if(StringUtils.isBlank(error)) {
				modelnView.addObject("success", "true");
				modelnView.addObject("successMessage", "Successfully inserted record.");
			} else {
				modelnView.addObject("error", "true");
				modelnView.addObject("errorMessage", error);
			}
			
			modelnView.addObject("projectTitle", projectTitlesDAO.getSingleProjectTitleConfig(projectTitle.getId()));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			modelnView.addObject("errorMessage", "Error updating time table details : " + e.getMessage());
		}
		
		return modelnView;
	}

	@RequestMapping(value = "/toggleProjectTitleConfigActive", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> toggleProjectTitleConfigActive(HttpServletResponse response, HttpServletRequest request) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

		String id = request.getParameter("id");
		String active = request.getParameter("active");
		String message = projectTitlesDAO.toggleActiveProjectTitleConfig(id, active);
		res.put("message", message);
		res.put("status", StringUtils.isBlank(message) ? "fail" : "success");
		
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	


	
}
