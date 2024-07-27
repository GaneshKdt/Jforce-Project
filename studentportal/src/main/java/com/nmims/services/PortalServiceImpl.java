package com.nmims.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;

@Service
public class PortalServiceImpl implements PortalService{
	
	@Autowired
	PortalDao portalDao;

	@Override
	public List<SessionQueryAnswerStudentPortal> getStudentQueries(String sapId) {
		// TODO Auto-generated method stub
		return portalDao.getQueriesByStudent(sapId);
	}

	@Override
	public StudentStudentPortalBean getSingleStudentsData(String sapId) {
		// TODO Auto-generated method stub
		return portalDao.getSingleStudentsData(sapId);
	}

}
