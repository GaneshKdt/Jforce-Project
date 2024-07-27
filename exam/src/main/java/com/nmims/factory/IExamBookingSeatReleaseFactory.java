package com.nmims.factory;

import com.nmims.services.IExamBookingSeatReleaseService;

/**
 * 
 * @author Siddheshwar_K
 *
 */
public interface IExamBookingSeatReleaseFactory {
	public static final String PRODUCT_UG = "PRODUCT_UG";
	public static final String PRODUCT_PG = "PRODUCT_PG";
	public static final String PRODUCT_MBAX = "PRODUCT_MBAX";
	public static final String PRODUCT_MBAWX = "PRODUCT_MBAWX";
	
	/**
	 * Factory method used to create an instance of the supplied product type.
	 * @param productType - Product category for which an instance to be create.
	 * @return IExamBookingSeatReleaseService implemented class instance based on the given product type.
	 * @throws Exception throws an exception while creating the instance of the given product type. 
	 */
	public IExamBookingSeatReleaseService getBookingReleaseInstance(String productType) throws Exception;
}
