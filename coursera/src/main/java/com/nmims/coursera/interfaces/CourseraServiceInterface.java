package com.nmims.coursera.interfaces;

import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.beans.StudentCourseraMappingBean;
import com.nmims.coursera.dto.CourseraSyncDto;
import com.nmims.coursera.dto.StudentCourseraDto;

public interface CourseraServiceInterface {
	
	public StudentCourseraBean getSingleStudentsData(String sapid);
	
	public String getStudentlearnerURL(String sapid);
	
	public CourseraSyncDto create(CourseraSyncDto dtoObj);
	
	public StudentCourseraDto checkForStudentOptedForCoursera(StudentCourseraMappingBean dtoObj);

}
