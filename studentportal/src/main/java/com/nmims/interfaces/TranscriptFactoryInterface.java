package com.nmims.interfaces;

import com.nmims.factory.TranscriptFactory.ProductType;;

public interface TranscriptFactoryInterface {
	
	public abstract TranscriptServiceInterface getProductType(ProductType type);


}
