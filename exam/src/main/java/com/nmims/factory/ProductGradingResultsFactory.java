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

import com.nmims.interfaces.GradingTypeResultsServiceInterface;
import com.nmims.interfaces.ProductGradingResultsFactoryInterface;

/**
 * @author vil_m
 *
 */
@Service("productGradingResultsFactory")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ProductGradingResultsFactory implements ProductGradingResultsFactoryInterface {
	
	public static final Logger logger = LoggerFactory.getLogger(ProductGradingResultsFactory.class);
	
	@Autowired
	@Qualifier("gradingTypeRemarkResultsService") 
	private GradingTypeResultsServiceInterface gradingTypeRemarkResultsService;
	
	@Override
	public GradingTypeResultsServiceInterface getProductGradingResultsType(String productType, String gradingType) {
		// TODO Auto-generated method stub
		if (ProductGradingResultsFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_UG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			logger.info("Entering getProductGradingResultsType...PRODUCT_UG + GRADING_REMARK...");
			return gradingTypeRemarkResultsService;// service layer class - object
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_PG.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_OLDPROCESS.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_CREDIT.equals(gradingType)) {
			return null;
		} else if (ProductGradingResultsFactoryInterface.PRODUCT_MBAWX.equals(productType)
				&& ProductGradingResultsFactoryInterface.GRADING_REMARK.equals(gradingType)) {
			return null;
		} else {
			return null;
		}
	}

	/*public GradingTypeResultsServiceInterface getGradingTypeRemarkResultsService() {
		return gradingTypeRemarkResultsService;
	}*/

//	public void setGradingTypeRemarkResultsService(GradingTypeResultsServiceInterface gradingTypeRemarkResultsService) {
//		this.gradingTypeRemarkResultsService = gradingTypeRemarkResultsService;
//	}

}
