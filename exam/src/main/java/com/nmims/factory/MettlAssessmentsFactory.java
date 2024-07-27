package com.nmims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.interfaces.MettlAssessments;
import com.nmims.interfaces.ProductType;
import com.nmims.services.MettlAssessmentMBAXService;
import com.nmims.services.MettlAssessmentsMBAWXService;
import com.nmims.services.MettlAssessmentsMSCService;
import com.nmims.services.MettlAssessmentsModulerPDDMService;

@Service("mettlAssessmentsFactory")
public class MettlAssessmentsFactory implements ProductType<MettlAssessments>   {
	
	@Autowired
	MettlAssessmentsMBAWXService mettlAssessmentsMBAWX;
	
	@Autowired
	MettlAssessmentsMSCService mettlAssessmentsMSC;
	
	@Autowired
	MettlAssessmentsModulerPDDMService mettlAssessmentsModulerPDDM;
	
	@Autowired
	MettlAssessmentMBAXService mettlAssessmentMBAXService;
	
	
	@Override
	public  MettlAssessments getProductType(String type) {
		System.out.println("type"+type);
		MettlAssessments assessments = null;
		
		switch (type) {
		case "MBA - WX":
			assessments = mettlAssessmentsMBAWX;
			break;
		case "M.Sc. (AI & ML Ops)":
		case "M.Sc. (AI)":	
			assessments = mettlAssessmentsMSC;
			break;
		case "Modular PD-DM":
			assessments = mettlAssessmentsModulerPDDM;
			break;
		case "MBA - X":
			assessments = mettlAssessmentMBAXService;
			break;
		default : 
			break;
		}
		return assessments;
	}
	
}
