package com.nmims.stratergies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface SRFeeResponseStratergyInterface {
	
	public ServiceRequestStudentPortal srFeeResponse(ServiceRequestStudentPortal sr, StudentStudentPortalBean student, HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) throws Exception;
	
}
