package com.nmims.services;

import java.util.List;

import com.nmims.beans.UserAuthorizationStudentPortalBean;

public interface AuthorizationService {
	
	public UserAuthorizationStudentPortalBean getUserAuthorization(String userId);
	
	public List<UserAuthorizationStudentPortalBean> getAllUserAuthorization();

	public void updateRolesInLdap(String userId, String roles) throws Exception;
	
	public void updateRolesInAuthorizationTable(String userId, String roles);

}
