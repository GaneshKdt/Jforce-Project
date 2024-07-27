/**
 * 
 */
package com.nmims.interfaces;

/**
 * @author vil_m
 *
 */
public interface LiveSessionReportAdminFactoryInterface {
	
	public static final String PRODUCT_UG = "PRODUCT_UG";
	public static final String PRODUCT_PG = "PRODUCT_PG";
	public static final String PRODUCT_MBAX = "PRODUCT_MBAX";
	public static final String PRODUCT_MBAWX = "PRODUCT_MBAWX";

	public abstract LiveSessionReportAdminServiceInterface getReportService(String productType);
}
