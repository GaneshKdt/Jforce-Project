package com.nmims.interfaces;

import com.nmims.factory.ReExamEligibleStudentsReportFactory.ProductType;

public interface ReExamEligibleStudentsReportFactoryInterface {
	
	public abstract ReExamEligibleStudentsReportServiceInterface getProductType(ProductType type);
}
