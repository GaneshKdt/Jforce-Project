package com.nmims.controllers;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.nmims.beans.CSAdminAuthorizationTypes;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.LoginDAO;

@Controller
public class CSAdminBaseController {

	@Autowired
	LoginDAO loginDAO;
	
	public List<String> getAuthorization(String userAuthorizationRoles){
		userAuthorizationRoles = userAuthorizationRoles.replaceAll(", ", ",");
		userAuthorizationRoles = userAuthorizationRoles.trim();
		String strArray[] = userAuthorizationRoles. split(",");
		List<String> authorizationList = Arrays.asList(strArray);
		return authorizationList;
	}

	public boolean checkLogin(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");

		if(userId == null) {
			return false;
		}
		
		if(userId.startsWith("77") || userId.startsWith("79")) {
			return false;
		}
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		if(userAuthorization == null || userAuthorization.getRoles() == null) {
			return false;
		}
		
		return true;
	}
	
	public boolean checkAuthorization(List<String> userAuthorizationList, String adminType) {
		adminType = adminType.trim();
		if(userAuthorizationList.contains(adminType) || userAuthorizationList.contains(CSAdminAuthorizationTypes.CSAdmin)) {
			return true;
		}
		return false;
	}
	
}
