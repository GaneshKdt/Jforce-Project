package com.nmims.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.CourseraMappingBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.CourseraDao;

@Service
public class CourseraService {
	
	@Autowired
	CourseraDao cDAo;
	
	public String getLeanersURL(StudentAcadsBean registration) {
		String leanersURL="";
		leanersURL=cDAo.getLeanersURLbyMasterKey(registration.getConsumerProgramStructureId());
		return leanersURL;
	}
	
	public CourseraMappingBean checkStudentPaidForCoursera(String sapId) {
		CourseraMappingBean bean=cDAo.checkStudentOptedForCoursera(sapId);
		return bean;
	}
	
	public boolean isCourseraProgramApplicableForMasterKey(String consumerProgramStructureId) {
		int countForMasterKeyCourseraMapping = cDAo.checkCourseraProgramApplicableForMasterKey(consumerProgramStructureId);
		if (countForMasterKeyCourseraMapping > 0) {
			return true;
		}else {
			return false;
		}
	}

}
