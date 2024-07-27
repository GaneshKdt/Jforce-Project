package com.nmims.interfaces;

import com.nmims.factory.SemDeregisterFactory.ProductType;

public interface SemDeregisterFactoryInterface {
	
	public abstract SemDeregisterInterface getProductType(ProductType type);

}
