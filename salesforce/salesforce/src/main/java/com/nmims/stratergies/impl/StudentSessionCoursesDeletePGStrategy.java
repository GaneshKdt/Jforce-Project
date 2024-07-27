package com.nmims.stratergies.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentSessionCoursesBean;
import com.nmims.daos.StudentSessionCoursesDAO;
import com.nmims.stratergies.StudentSessionCoursesDeleteStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesDeletePGStrategy")
public class StudentSessionCoursesDeletePGStrategy implements StudentSessionCoursesDeleteStrategyInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesDeletePGStrategy.class);
	
	@Autowired
	protected StudentSessionCoursesDAO studentSessionCoursesDAO;
	
	@Override
	public int delete(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		int rowsDeleted = -1;
		
		rowsDeleted = studentSessionCoursesDAO.delete(bean);
		
		return rowsDeleted;
	}

}
