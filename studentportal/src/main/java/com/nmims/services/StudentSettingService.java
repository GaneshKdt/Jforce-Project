package com.nmims.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentSettingDao;

@Service
public class StudentSettingService {

	@Autowired
	ApplicationContext act;

	@Autowired
	StudentSettingDao studentSettingDao;
	
	public void updateStudentSettings(String columnName,String sapid,int value ) {
		 studentSettingDao.updateStudentSettings(columnName,sapid,value ) ;
	}
	
	public StudentStudentPortalBean getSingleStudentsData(HttpServletRequest request) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		StudentStudentPortalBean bean = pDao.getSingleStudentsData(userId);
		return bean;
	}
}
