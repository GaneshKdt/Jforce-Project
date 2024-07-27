package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.CopyCaseFactoryInterface;
import com.nmims.interfaces.CopyCaseInterface;
import com.nmims.services.AssignmentCopyCaseService;
import com.nmims.services.ProjectCopyCaseService;

@Service("copyCaseFactory")
public class CopyCaseFactory implements CopyCaseFactoryInterface{

	public enum ProductType {
			ASSIGNMENT, PROJECT
	}
	
	@Autowired
	AssignmentCopyCaseService assignmentCCService;
	
	@Autowired
	ProjectCopyCaseService projectCCService;
	
	public CopyCaseInterface getProductType(ProductType type) {
		CopyCaseInterface CCInterface = null;
		
		switch (type) {
		case ASSIGNMENT:
			CCInterface = assignmentCCService;
			break;
		case PROJECT:
			CCInterface = projectCCService;
			break;
		}
		return CCInterface;
	};
}
