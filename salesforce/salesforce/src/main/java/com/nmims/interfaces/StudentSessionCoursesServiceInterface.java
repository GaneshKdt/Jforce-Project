/**
 * 
 */
package com.nmims.interfaces;

import com.nmims.beans.StudentSessionCoursesBean;

/**
 * @author vil_m
 *
 */
public interface StudentSessionCoursesServiceInterface {
	
	public StudentSessionCoursesBean create(StudentSessionCoursesBean bean);
	
	public StudentSessionCoursesBean read(StudentSessionCoursesBean bean);
	
	public int delete(StudentSessionCoursesBean bean);
	
	public StudentSessionCoursesBean update(StudentSessionCoursesBean bean);
}
