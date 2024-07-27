/**
 * 
 */
package com.nmims.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.StudentSessionCoursesFactoryInterface;
import com.nmims.interfaces.StudentSessionCoursesServiceInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesFactory")
public class StudentSessionCoursesFactory implements StudentSessionCoursesFactoryInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesFactory.class);
	
	@Autowired
	private StudentSessionCoursesServiceInterface studentSessionCoursesService;

	@Override
	public StudentSessionCoursesServiceInterface getStudentSessionCoursesService(String productType) {
		logger.info("Entering StudentSessionCoursesFactory : getStudentSessionCoursesService");
		// TODO Auto-generated method stub
		if (StudentSessionCoursesFactoryInterface.PRODUCT_UG.equals(productType)) {
			return null;
		} else if (StudentSessionCoursesFactoryInterface.PRODUCT_PG.equals(productType)) {
			logger.info("StudentSessionCoursesFactory.getStudentSessionCoursesService for " + productType);
			return studentSessionCoursesService;
		} else if (StudentSessionCoursesFactoryInterface.PRODUCT_MBAX.equals(productType)) {
			return null;
		} else if (StudentSessionCoursesFactoryInterface.PRODUCT_MBAWX.equals(productType)) {
			return null;
		} else {
			return null;
		}
	}
}
