package com.nmims.services;

import java.util.List;

import javax.naming.InvalidNameException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	PortalDao portalDAO;
	
	@Autowired
	LDAPDao ldapdao;
	@Override
	public List<UserAuthorizationStudentPortalBean> getAllUserAuthorization() {
		// TODO Auto-generated method stub
 
		return portalDAO.getAllUserAuthorization();
	}

	@Override
	public void updateRolesInLdap(String userId, String roles) throws Exception {
		// TODO Auto-generated method stub
		ldapdao.updateRolesLdapAttribute(userId, roles);
		
	}

	@Override
	public void updateRolesInAuthorizationTable(String userId, String roles) {
		
		portalDAO.updateAuthorizationTable(userId, roles);
		
	}

	@Override
	public UserAuthorizationStudentPortalBean getUserAuthorization(String userId) {
		
 		return portalDAO.getUserAuthorization(userId);

	}

	
}
