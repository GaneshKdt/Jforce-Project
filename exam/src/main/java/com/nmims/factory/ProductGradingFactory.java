/**
 * 
 */
package com.nmims.factory;

import com.nmims.interfaces.ProductGradingFactoryInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.GradingTypeServiceInterface;

/**
 * @author vil_m
 *
 */

@Service("productGradingFactory")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductGradingFactory implements ProductGradingFactoryInterface {
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Autowired
	@Qualifier("gradingTypeRemarkService")
	private GradingTypeServiceInterface gradingTypeRemarkService;// GradingTypeRemarkService

	/*@Autowired
	public ProductGradingFactory(GradingTypeServiceInterface gradingTypeRemarkService) {
		this.gradingTypeRemarkService = gradingTypeRemarkService;
	}*/

	public GradingTypeServiceInterface getProductGradingType(String productType, String gradingType) {
		if (ProductGradingFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			logger.info("Entering getProductGradingType...PRODUCT_UG + GRADING_REMARK...");
			return gradingTypeRemarkService;// service layer class - object
		} else if (ProductGradingFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else {
			return null;
		}
	}

	/*public GradingTypeServiceInterface getGradingTypeRemarkService() {
		return gradingTypeRemarkService;
	}*/

//	public void setGradingTypeRemarkService(GradingTypeServiceInterface gradingTypeRemarkService) {
//		this.gradingTypeRemarkService = gradingTypeRemarkService;
//	}

}
