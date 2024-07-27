/**
 * 
 */
package com.nmims.stratergies.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentSessionCoursesBean;
import com.nmims.daos.StudentSessionCoursesDAO;
import com.nmims.stratergies.StudentSessionCoursesReadStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesReadPGStrategy")
public class StudentSessionCoursesReadPGStrategy implements StudentSessionCoursesReadStrategyInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesReadPGStrategy.class);
	
	@Autowired
	protected StudentSessionCoursesDAO studentSessionCoursesDAO;
	
	@Override
	public StudentSessionCoursesBean read(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		StudentSessionCoursesBean returnBean = null;
		
		returnBean = studentSessionCoursesDAO.read(bean);
		
		return returnBean;
	}

}
