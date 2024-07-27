package com.nmims.services;

import java.util.List;

import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface PortalService {

	public List<SessionQueryAnswerStudentPortal> getStudentQueries(String sapId);
	public StudentStudentPortalBean getSingleStudentsData(String sapid);

	
	
}
