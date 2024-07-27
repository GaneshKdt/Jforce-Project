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
import com.nmims.stratergies.StudentSessionCoursesCreateStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesCreatePGStrategy")
public class StudentSessionCoursesCreatePGStrategy implements StudentSessionCoursesCreateStrategyInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesCreatePGStrategy.class);
	
	@Autowired
	protected StudentSessionCoursesDAO studentSessionCoursesDAO;
	
	public StudentSessionCoursesBean create(StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesCreatePGStrategy : create");
		int retValue = -1;
		
		retValue = studentSessionCoursesDAO.create(bean);
		//NOTE : clone logic, not needed, for this case of CRUD, might be needed for other usecases.
		
		return bean;
	}
}
