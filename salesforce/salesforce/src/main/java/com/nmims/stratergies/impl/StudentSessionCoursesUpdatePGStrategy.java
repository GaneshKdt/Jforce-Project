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
import com.nmims.stratergies.StudentSessionCoursesUpdateStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesUpdatePGStrategy")
public class StudentSessionCoursesUpdatePGStrategy implements StudentSessionCoursesUpdateStrategyInterface {

	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesUpdatePGStrategy.class);

	@Autowired
	protected StudentSessionCoursesDAO studentSessionCoursesDAO;

	@Override
	public StudentSessionCoursesBean update(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesUpdatePGStrategy : update");
		int retValue = -1;

		retValue = studentSessionCoursesDAO.update(bean);
		// NOTE : clone logic, not needed, for this case of CRUD, might be needed for
		// other usecases.

		return bean;
	}

}
