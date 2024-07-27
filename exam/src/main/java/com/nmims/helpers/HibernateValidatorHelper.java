/**
 * 
 */
package com.nmims.helpers;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author vil_m
 *
 */
public class HibernateValidatorHelper {
	
	private static ValidatorFactory factory = null;
	private static Validator validator = null;
	
	public static ValidatorFactory getFactory() {
		factory = Validation.buildDefaultValidatorFactory();
		return factory;
	}
	
	public static Validator getValidator() {
		getFactory();
		validator = factory.getValidator();
		return validator;
	}
}
