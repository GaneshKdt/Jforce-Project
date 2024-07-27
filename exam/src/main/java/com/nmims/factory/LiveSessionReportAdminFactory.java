/**
 * 
 */
package com.nmims.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.LiveSessionReportAdminFactoryInterface;
import com.nmims.interfaces.LiveSessionReportAdminServiceInterface;

/**
 * @author vil_m
 *
 */

@Service("liveSessionReportAdminFactory")
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LiveSessionReportAdminFactory implements LiveSessionReportAdminFactoryInterface {
	
	public static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminFactory.class);
	
	@Autowired
	@Qualifier("liveSessionReportAdminService")
	LiveSessionReportAdminServiceInterface liveSessionReportAdminService;
	
	@Override
	public LiveSessionReportAdminServiceInterface getReportService(String productType) {
		// TODO Auto-generated method stub
		logger.info("Entering LiveSessionReportAdminFactory : getReportService");
		LiveSessionReportAdminServiceInterface liveSessionReportAdminServiceInterface = null;
		
		if(LiveSessionReportAdminFactoryInterface.PRODUCT_UG.equals(productType)) {
			liveSessionReportAdminServiceInterface = null;
		} else if(LiveSessionReportAdminFactoryInterface.PRODUCT_PG.equals(productType)) {
			logger.info("Entering getReportService...PRODUCT_PG...");
			liveSessionReportAdminServiceInterface = liveSessionReportAdminService;
		} else if(LiveSessionReportAdminFactoryInterface.PRODUCT_MBAX.equals(productType)) {
			liveSessionReportAdminServiceInterface = null;
		} else if(LiveSessionReportAdminFactoryInterface.PRODUCT_MBAWX.equals(productType)) {
			liveSessionReportAdminServiceInterface = null;
		} else {
			liveSessionReportAdminServiceInterface = null;
		}
		return liveSessionReportAdminServiceInterface;
	}

}
