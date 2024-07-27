package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.ReExamEligibleStudentsReportFactoryInterface;
import com.nmims.interfaces.ReExamEligibleStudentsReportServiceInterface;
import com.nmims.services.ReExamEligibleStudentsServiceMBAWX;
import com.nmims.services.ReExamEligibleStudentsServiceMBAX;

@Service("reExamEligibleStudentsFactory")
public class ReExamEligibleStudentsReportFactory implements ReExamEligibleStudentsReportFactoryInterface {

	public enum ProductType {
		MBAWX, MBAX, PDDM
	}
	
	@Autowired
	ReExamEligibleStudentsServiceMBAWX reExamEligibleStudentsServiceMBAWX;

	@Autowired
	ReExamEligibleStudentsServiceMBAX reExamEligibleStudentsServiceMBAX;
	
	@Autowired
	@Qualifier("pddmReExamEligibleStudentsService")
	private ReExamEligibleStudentsReportServiceInterface pddmReExamEligibleStudentsService;
	
	@Override
	public ReExamEligibleStudentsReportServiceInterface getProductType(ProductType type) {
		
		switch (type) {
			case MBAWX: return reExamEligibleStudentsServiceMBAWX;
			case MBAX: return reExamEligibleStudentsServiceMBAX;
			
			case PDDM:
				return pddmReExamEligibleStudentsService;
		}
		
		return null;
	};
}
