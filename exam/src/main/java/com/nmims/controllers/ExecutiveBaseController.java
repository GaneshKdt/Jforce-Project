package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nmims.daos.StudentMarksDAO;

@Component
public class ExecutiveBaseController {
	@Autowired
	ApplicationContext act;
	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}")
	protected List<String> ACAD_YEAR_SAS_LIST ;
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}")
	protected List<String> SAS_EXAM_MONTH_LIST2;
	
	protected List<String> SAS_EXAM_MONTH_LIST = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	@Value("#{'${SAS_PROGRAM_STRUCTURE_LIST}'}")
	protected List<String> SAS_PROGRAM_STRUCTURE_LIST;
	@Value("#{'${SAS_ENROLLMENT_MONTH_LIST}'.split(',')}")
	protected List<String> SAS_ENROLLMENT_MONTH_LIST2 ;
	
	//SAS_ENROLLMENT_MONTH_LIST
	protected List<String> SAS_ENROLLMENT_MONTH_LIST = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	protected ArrayList<String> programList = null;
	protected ArrayList<String> subjectList = null;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		subjectList = null;
		getAllSubjectList();
		
		programList = null;
		getAllProgramList();
		
		return null;
	}
	
	public ArrayList<String> getAllProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllProgramsSAS();
		}
		return programList;
	}
	
	public ArrayList<String> getAllSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjectsSAS();
		}
		return subjectList;
	}
	
	public ArrayList<String> stateList = new ArrayList<String>(Arrays.asList( 
			"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir",
			"Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan",
			"Sikkim","Tamil Nadu","Telangana","Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", 
			"Chandigarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Lakshadweep", "Pondicherry")); 
	
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
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}

	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}


	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
	
	}
}
