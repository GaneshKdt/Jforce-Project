package com.nmims.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nmims.beans.FacultyReallocationBean;
import com.nmims.daos.FacultyReallocationDao;
import com.nmims.services.FacultyReallocationService;

@Controller
public class FacultyReallocationController 
{
	/*Variables*/
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@Value( "${MAX_ASSIGNMENTS_PER_FACULTY}" )
	private String MAX_ASSIGNMENTS_PER_FACULTY;
	
	@Autowired
	public FacultyReallocationDao facultyReallocationDao;
	
	@Autowired
	public FacultyReallocationService facultyReallocationService;
	
	private static final Logger logger = LoggerFactory.getLogger("projectReallocation");
	
	
	
	/*Methods*/
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}

	 }		
	
	
	/*Mappings*/
	@RequestMapping(value = "/admin/reallocateProjectEvaluationForm", method = RequestMethod.GET)
	public ModelAndView reallocateProjectEvaluationForm(HttpServletRequest request, HttpServletResponse response, Model m) throws SQLException {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		FacultyReallocationBean searchBean = new FacultyReallocationBean();
		
		ArrayList<FacultyReallocationBean> facultyList = facultyReallocationDao.getAllFacultyWithNameAndId();
		request.getSession().setAttribute("facultyList", facultyList);
		request.getSession().setAttribute("MAX_ASSIGNMENTS_PER_FACULTY", MAX_ASSIGNMENTS_PER_FACULTY);
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("facultyList", facultyList);
		return new ModelAndView("project/reallocateProjectEvaluation");
	}
	
	@RequestMapping(value="/admin/getNoOfProjectsForReallocation", method = RequestMethod.POST)
	public ModelAndView getNoOfProjectsForReallocation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute FacultyReallocationBean searchBean,
		Model m) throws JsonParseException, JsonMappingException, IOException 
	{
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ArrayList<FacultyReallocationBean> facultyList = new ArrayList<>();
		ArrayList<FacultyReallocationBean> allocatedProjectList = new ArrayList<>();
		HashMap<String,FacultyReallocationBean> reallocatedProjectList = new HashMap<>();
		ArrayList<FacultyReallocationBean> searchedFacultyList = new ArrayList<>();
		
		ModelAndView modelnView = new ModelAndView("project/reallocateProjectEvaluation");
		facultyList = (ArrayList<FacultyReallocationBean>) request.getSession().getAttribute("facultyList");
		
		try
		{
			
			Map<String, FacultyReallocationBean> mapfacultyList = facultyList.stream().collect(Collectors.toMap(FacultyReallocationBean::getFacultyId, bean -> bean));
			
			allocatedProjectList = facultyReallocationService.getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(searchBean.getYear(), searchBean.getMonth(), searchBean.getFacultyId());

			if(!allocatedProjectList.isEmpty())
			{
				//Dao Call From service to fetch reallocated data of each faculty
				reallocatedProjectList = facultyReallocationService.getProjectsNotEvaluatedByYearAndMonthOrFacultyId(searchBean.getYear(), searchBean.getMonth(), searchBean.getFacultyId());
				//Logic method call from Service to set allocated and yeEvalauated property in list of bean
				searchedFacultyList = facultyReallocationService.getSearchedFacultyList(reallocatedProjectList, allocatedProjectList, mapfacultyList);
			}
			else
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Records Found!.");
			}
			
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("facultyList", facultyList);
			modelnView.addObject("searchedFacultyList", searchedFacultyList);
			modelnView.addObject("showFaculties", "true");
			return modelnView;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("facultyList", facultyList);
			modelnView.addObject("searchedFacultyList", searchedFacultyList);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
			logger.error("Error in Getting Faculty For Reallocate = "+e);
			return modelnView;
		}
	}
	
	@RequestMapping(value="/admin/reallocateProjectsToFaculty", method = RequestMethod.POST)
	public ModelAndView reallocateProjectsToFaculty(HttpServletRequest request, HttpServletResponse response , 
			@ModelAttribute FacultyReallocationBean reallocateBean, Model m) throws JsonParseException, JsonMappingException, IOException 
	{
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ArrayList<FacultyReallocationBean> facultyList = new ArrayList<>();
		ModelAndView modelnView = new ModelAndView("project/reallocateProjectEvaluation");
		facultyList = (ArrayList<FacultyReallocationBean>) request.getSession().getAttribute("facultyList");
		reallocateBean.setUser((String)request.getSession().getAttribute("userId"));
		
		try
		{
			int reallocatedCount = facultyReallocationService.reallocateProjectsToFaculty(reallocateBean);
			if(reallocatedCount > 0)
			{
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Projects reallocation for "+reallocatedCount+" projects are successfull, from "+reallocateBean.getFacultyId()+" to "+reallocateBean.getToFacultyId()+" faculty id.");
			}
			else
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Projects reallocation for "+reallocatedCount+" projects are successfull, from "+reallocateBean.getFacultyId()+" to "+reallocateBean.getToFacultyId()+" faculty id.");
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in Reallocating the Projects!!, Error: "+e);
			logger.error("Error in reallocating projects = "+e);
		}
		
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("searchBean", reallocateBean);
		modelnView.addObject("facultyList", facultyList);
		
		return modelnView;
		
	}
}
