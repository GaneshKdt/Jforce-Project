package com.nmims.interfaces;

import com.nmims.factory.CertificateFactory.ProductType;

public interface CertificateFactoryInterface {
	
	public abstract CertificateInterface getProductType(ProductType type);
	
}
