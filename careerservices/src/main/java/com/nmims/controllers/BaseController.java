package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.nmims.beans.AppStartupChecksModelBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.LoginDAO;
import com.nmims.daos.PaymentManagerDAO;
import com.nmims.helpers.DataValidationHelpers;

public class BaseController {
	
	@Autowired
	ApplicationContext act;

	@Autowired
	PaymentManagerDAO paymentManagerDAO;
	
	@Autowired
	CareerServicesDAO csDAO;

	DataValidationHelpers validationHelpers = new DataValidationHelpers();
	
	public boolean checkIfEmptyOrNull(String s) {
		return validationHelpers.checkIfStringEmptyOrNull(s);
	}
	
	public boolean checkLogin(HttpServletRequest request) {

		String sapid = (String) request.getSession().getAttribute("userId");
		if(sapid != null) {
			if(request.getSession().getAttribute("student_careerservices") != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkCSAccess(HttpServletRequest request) {
		
		StudentCareerservicesBean student = (StudentCareerservicesBean) request.getSession().getAttribute("student_careerservices");
		if(student == null) {
			return false;
		}
		if(student.isPurchasedOtherPackages()) {
			return true;
		}
		return false;
	}
	
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

	public void resetStudentInSession(HttpServletRequest request, HttpServletResponse response) {
		String userId = (String)request.getSession().getAttribute("userId");
		LoginDAO loginDAO = (LoginDAO)act.getBean("loginDAO");
		StudentCareerservicesBean student = loginDAO.getSingleStudentsData(userId);
		
		performCSStudentChecks(request, userId, student);
		
		request.getSession().setAttribute("student_careerservices", student);
	}
	
	public void performCSStudentChecks(HttpServletRequest request, String userId, StudentCareerservicesBean student) {
		csDAO.performCSStudentChecks(request, student);
	}
	
	public void checkIfCSAccessAvailableForMasterKey(StudentCareerservicesBean student, AppStartupChecksModelBean appStartupChecksModelBean) {
		if(csDAO.checkIfCSAccessAvailableForMasterKey(student.getConsumerProgramStructureId())){
			appStartupChecksModelBean.setConsumerTypeCanPurchaseCS(true);
		}else {
			appStartupChecksModelBean.setConsumerTypeCanPurchaseCS(false);
		}
	}
}
