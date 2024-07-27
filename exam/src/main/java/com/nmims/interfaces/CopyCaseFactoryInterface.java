package com.nmims.interfaces;

import com.nmims.factory.CopyCaseFactory.ProductType;

public interface CopyCaseFactoryInterface {

	public abstract CopyCaseInterface getProductType(ProductType type);

}
