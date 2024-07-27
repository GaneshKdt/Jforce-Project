package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.daos.LDAPDao;
import com.nmims.helpers.PersonStudentPortalBean;

@Service("lDAPService")
public class LDAPServiceImpl implements LDAPService {

	@Autowired
	LDAPDao lDAPDao;
	
	@Override
	public PersonStudentPortalBean findPerson(String userId) {
 		return lDAPDao.findPerson(userId);
	}

	@Override
	public void updateRolesInLdap(String userId, String roles) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
