/**
 * 
 */
package com.nmims.interfaces;

/**
 * @author vil_m
 *
 */
public interface ProductGradingResultsFactoryInterface {

	public static final String GRADING_OLDPROCESS = "OLDPROCESS";
	public static final String GRADING_CREDIT = "CREDIT";
	public static final String GRADING_REMARK = "REMARK";

	public static final String PRODUCT_UG = "PRODUCT_UG";
	public static final String PRODUCT_PG = "PRODUCT_PG";
	public static final String PRODUCT_MBAX = "PRODUCT_MBAX";
	public static final String PRODUCT_MBAWX = "PRODUCT_MBAWX";
	
	public abstract GradingTypeResultsServiceInterface getProductGradingResultsType(String productType, String gradingType);

}
