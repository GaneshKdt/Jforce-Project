package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.FacultyCourseBean;

public interface FacultyServiceInterface {
	
	List<FacultyCourseBean> populateFacultySubjectCodeInCourseTable();
	
	int deleteFacultyCourse(String facultyId,String year,String month,String subjectCode);
	
	List<FacultyCourseBean> getallFacultyCourseList(boolean subjectcode);
}
