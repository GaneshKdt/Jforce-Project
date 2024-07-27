package com.nmims.factory.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.factory.IExamBookingSeatReleaseFactory;
import com.nmims.services.IExamBookingSeatReleaseService;
/**
 * 
 * @author Siddheshwar_K
 *
 */
@Service("examBookingSeatReleaseFactory")
public class ExamBookingSeatReleaseFactoryImpl implements IExamBookingSeatReleaseFactory {

	@Autowired
	@Qualifier("pgBookingSeatReleaseService")
	private IExamBookingSeatReleaseService pgBookingSeatReleaseService;
	
	@Override
	public IExamBookingSeatReleaseService getBookingReleaseInstance(String productType) throws Exception{
		if(IExamBookingSeatReleaseFactory.PRODUCT_PG.equalsIgnoreCase(productType)){
			return pgBookingSeatReleaseService;
		}
		else if(IExamBookingSeatReleaseFactory.PRODUCT_MBAWX.equalsIgnoreCase(productType)) {
			return null;
		}
		else if(IExamBookingSeatReleaseFactory.PRODUCT_MBAX.equalsIgnoreCase(productType)) {
			return null;
		}
		
		return pgBookingSeatReleaseService;
	}

}
