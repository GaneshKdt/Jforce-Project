package com.nmims.interfaces;

import com.nmims.factory.FeeReceiptFactory.ProductType;

public interface FeeReceiptFactoryInterface {
	public abstract FeeReceiptInterface getSRType(ProductType type);
}
