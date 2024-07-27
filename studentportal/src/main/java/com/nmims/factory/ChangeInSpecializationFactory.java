package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.ChangeInSpecializationFactoryInterface;
import com.nmims.interfaces.ChangeInSpecializationServiceInterface;
import com.nmims.services.ChangeInSpecializationService;

@Service("changeInSpecializationFactory")
public class ChangeInSpecializationFactory implements ChangeInSpecializationFactoryInterface{

	public enum ProductType {
		MBAWX , MBAX
	}

	@Autowired
	ChangeInSpecializationService changeInSpecialization;
	
	@Override
	public ChangeInSpecializationServiceInterface getProductType(ProductType type) {
		// TODO Auto-generated method stub

		ChangeInSpecializationServiceInterface changeInSpecializationService = null;

		switch (type) {
		case MBAWX:
			changeInSpecializationService = changeInSpecialization;
			break;
		case MBAX:
			//no specialization for x
		}
		
		return changeInSpecializationService;

	}

}
