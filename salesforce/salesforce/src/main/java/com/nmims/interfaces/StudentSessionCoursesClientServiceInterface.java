/**
 * 
 */
package com.nmims.interfaces;

import com.nmims.dto.StudentSessionCoursesDTO;

/**
 * @author vil_m
 *
 */
public interface StudentSessionCoursesClientServiceInterface {
	
	public StudentSessionCoursesDTO create(StudentSessionCoursesDTO dtoObj, String userId);
	
	public StudentSessionCoursesDTO read(StudentSessionCoursesDTO dtoObj);
	
	public StudentSessionCoursesDTO delete(StudentSessionCoursesDTO dtoObj);
	
	public StudentSessionCoursesDTO update(StudentSessionCoursesDTO dtoObj, String userId);
}
