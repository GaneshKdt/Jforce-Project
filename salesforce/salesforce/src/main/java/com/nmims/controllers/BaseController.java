package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

public class BaseController {
	
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			return false;
		}
		
		/*return true;*/

	}
	
	public boolean checkSession(HttpServletRequest request){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			return false;
		}
		
		/*return true;*/

	}
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	public void setSuccess(Model m, String successMessage){
		m.addAttribute("success","true");
		m.addAttribute("successMessage",successMessage);
	}
	
	public void setError(Model m, String errorMessage){
		m.addAttribute("error", "true");
		m.addAttribute("errorMessage", errorMessage);
	}


}
