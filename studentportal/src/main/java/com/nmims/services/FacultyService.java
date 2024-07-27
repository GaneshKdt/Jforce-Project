package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.interfaces.FacultyServiceInterface;

@Service("facultyService")
public class FacultyService implements FacultyServiceInterface
{

	@Autowired
	ApplicationContext act;
	
	
	public FacultyStudentPortalBean facultyProfile(String facultyId)
	{
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		return pDao.getFacultyData(facultyId);
	}
}
