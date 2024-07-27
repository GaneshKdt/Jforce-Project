package com.nmims.interfaces;

import java.util.HashMap;
import java.util.List;

import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface QueryAnswerInterface {
	
	public HashMap<String, List<SessionQueryAnswerStudentPortal>> getCourseQueriesMap(String userId,String year, String month, String subject, String programSemSubjectId);

}
