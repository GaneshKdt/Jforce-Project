package com.nmims.factory;

import com.nmims.services.ITimeboundProjectPassFailService;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundProjectPassFailFactory {
	
	public static final String MBAWX = "MBA - WX";
	public static final String MBAX = "MBA - X";
	public static final String MSCAIMLOPS = "M.Sc. (AI & ML Ops)";
	public static final String MSCAI = "M.Sc. (AI)";
	public static final String PCDS = "PC-DS";
	public static final String PDDS = "PD-DS";
	
	/**
	 * This factory method will return the service implementation instance based on the given productType.
	 * @param productType - Contains the product type e.g MBA - (WX).
	 * @return	returns the service implementation instance of {@code productType}.
	 */
	public ITimeboundProjectPassFailService getTimeboundProjectPassFailProcessingInstance(String productType);

}
