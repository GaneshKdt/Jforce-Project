/**
 * 
 */
package com.nmims.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentSessionCoursesBean;
import com.nmims.interfaces.StudentSessionCoursesServiceInterface;
import com.nmims.stratergies.StudentSessionCoursesCreateStrategyInterface;
import com.nmims.stratergies.StudentSessionCoursesDeleteStrategyInterface;
import com.nmims.stratergies.StudentSessionCoursesReadStrategyInterface;
import com.nmims.stratergies.StudentSessionCoursesUpdateStrategyInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesService")
public class StudentSessionCoursesService implements StudentSessionCoursesServiceInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesService.class);
	
	@Autowired
	protected StudentSessionCoursesCreateStrategyInterface studentSessionCoursesCreatePGStrategy;
	
	@Autowired
	protected StudentSessionCoursesDeleteStrategyInterface studentSessionCoursesDeletePGStrategy;
	
	@Autowired
	protected StudentSessionCoursesReadStrategyInterface studentSessionCoursesReadPGStrategy;
	
	@Autowired
	StudentSessionCoursesUpdateStrategyInterface studentSessionCoursesUpdatePGStrategy;

	public StudentSessionCoursesBean create(StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesService : create");
		StudentSessionCoursesBean retBean = null;

		retBean = studentSessionCoursesCreatePGStrategy.create(bean);
		
		return retBean;
	}

	@Override
	public StudentSessionCoursesBean read(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesService : read");
		StudentSessionCoursesBean retBean = null;

		retBean = studentSessionCoursesReadPGStrategy.read(bean);
		
		return retBean;
	}

	@Override
	public int delete(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesService : delete");
		int rowsDeleted = -1;
		
		rowsDeleted = studentSessionCoursesDeletePGStrategy.delete(bean);
		
		return rowsDeleted;
	}

	@Override
	public StudentSessionCoursesBean update(StudentSessionCoursesBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesService : update");
		StudentSessionCoursesBean retBean = null;

		retBean = studentSessionCoursesUpdatePGStrategy.update(bean);
		
		return retBean;
	}
}
