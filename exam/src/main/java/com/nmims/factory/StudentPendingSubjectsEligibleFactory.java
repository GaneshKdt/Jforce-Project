package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.StudentPendingSubjectsEligibilityInterface;
import com.nmims.stratergies.StudentPendingSubjectsEligibilityStrategyInterface;
import com.nmims.stratergies.impl.PGStudentPendingSubjectsEligibilityStrategyInterface;

@Service("studentPendingSubjectsEligibleFactory")
public class StudentPendingSubjectsEligibleFactory implements StudentPendingSubjectsEligibilityInterface {

	public enum ProductType {
		MBAWX, MBAX, PG
	}
	
	@Autowired
	PGStudentPendingSubjectsEligibilityStrategyInterface pgEligibilityStrategy;
	
	@Override
	public StudentPendingSubjectsEligibilityStrategyInterface getProductType(ProductType type) throws Exception {

		switch (type) {
			case PG: 
				return pgEligibilityStrategy;
			case MBAX:
				throw new Exception("Unimplemented!");
			case MBAWX: 
				throw new Exception("Unimplemented!");
		}
		
		return null;
	};
}
