package com.nmims.services;

import com.nmims.helpers.PersonStudentPortalBean;

public interface LDAPService {

	public PersonStudentPortalBean findPerson(String userId);
	public void updateRolesInLdap(String userId, String roles) throws Exception;

}
