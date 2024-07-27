package com.nmims.interfaces;

import com.nmims.factory.ChangeInSpecializationFactory.ProductType;

public interface ChangeInSpecializationFactoryInterface {

	public abstract ChangeInSpecializationServiceInterface getProductType(ProductType type);

}
