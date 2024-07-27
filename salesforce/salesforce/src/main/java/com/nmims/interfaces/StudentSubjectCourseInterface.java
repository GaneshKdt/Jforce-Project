package com.nmims.interfaces;

import com.nmims.dto.StudentCourseMappingDTO;

public interface StudentSubjectCourseInterface 
{
	public void insertIntoStudentSubjectCourse(String sapid);
	public void insertIntoElectiveSubjectInCourseTable(StudentCourseMappingDTO studentDetails);
	
}
