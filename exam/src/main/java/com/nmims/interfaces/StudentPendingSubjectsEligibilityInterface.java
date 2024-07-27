package com.nmims.interfaces;

import com.nmims.factory.StudentPendingSubjectsEligibleFactory.ProductType;
import com.nmims.stratergies.StudentPendingSubjectsEligibilityStrategyInterface;

public interface StudentPendingSubjectsEligibilityInterface {
	public StudentPendingSubjectsEligibilityStrategyInterface getProductType(ProductType type) throws Exception;
}
