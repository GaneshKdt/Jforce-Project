package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.ProductType;
import com.nmims.interfaces.SubjectRepeatSR;
import com.nmims.services.SubjectRepeatSrMBAWX;
import com.nmims.services.SubjectRepeatSrMBAX;
import com.nmims.services.SubjectRepeatSrMSCAI;
import com.nmims.services.SubjectRepeatSrMSCAIOPS;

@Service("subjectRepeatSRFactory")
public class SubjectRepeatSRFactory  implements ProductType<SubjectRepeatSR>{
	
	@Autowired
	SubjectRepeatSrMBAWX subjectRepeatSrMBAWX;
	
	@Autowired
	SubjectRepeatSrMSCAIOPS subjectRepeatSrMSCAIOPS;
	
	@Autowired
	SubjectRepeatSrMSCAI subjectRepeatSrMSCAI;
	
	@Autowired
	SubjectRepeatSrMBAX subjectRepeatSrMBAX;

	@Override
	public SubjectRepeatSR getProductType(String type) {
		
		SubjectRepeatSR subjectRepeatSR = null;
		
		switch (type) {
		case "MBA - WX":
			subjectRepeatSR = subjectRepeatSrMBAWX;
			break;
		case "M.Sc. (AI & ML Ops)":
			subjectRepeatSR = subjectRepeatSrMSCAIOPS;
			break;
		case "M.Sc. (AI)":	
		case "PD-DS":	
		case "PC-DS":	
			subjectRepeatSR = subjectRepeatSrMSCAI;
			break;
		case "MBA - X":	
			subjectRepeatSR = subjectRepeatSrMBAX;
			break;
		default : 
			break;
		}
		
		return subjectRepeatSR;
	}

}
