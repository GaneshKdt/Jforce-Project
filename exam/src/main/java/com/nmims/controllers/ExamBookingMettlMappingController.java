package com.nmims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nmims.beans.ExamBookingMettlMappingBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.daos.ExamBookingMettlMappingDAO;
import com.nmims.helpers.MettlHelper;

@Controller
public class ExamBookingMettlMappingController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	@Qualifier("mbaWxMettlHelper")
	MettlHelper mettlHelper;
	
	
	@RequestMapping(value="/mettlAssessmentCreateInPortal", method=RequestMethod.GET)
	public ModelAndView mettlAssessmentCreateInPortal() {
		ModelAndView mv = new ModelAndView("mettlAssessmentCreateInPortal");
		ExamBookingMettlMappingDAO examBookingMettlMappingDAO = (ExamBookingMettlMappingDAO) act.getBean("examBookingMettlMappingDAO");
		JsonObject jsonResponse = mettlHelper.getPGAssessments();
		if(jsonResponse != null) {
			String status = jsonResponse.get("status").getAsString();
			if("SUCCESS".equalsIgnoreCase(status)) {
				JsonArray assessmentList = jsonResponse.get("assessments").getAsJsonArray();
				for (JsonElement assessmentElement : assessmentList) {
					JsonObject assessmentObject = assessmentElement.getAsJsonObject();
					String name = assessmentObject.get("name").getAsString();
					if(name.indexOf("_") == -1){
						continue;
					}
					ExamBookingMettlMappingBean examBookingMettlMappingBean = new ExamBookingMettlMappingBean();
					examBookingMettlMappingBean.setAssessmentId(assessmentObject.get("id").getAsString());
					
					String[] nameSubjectCode = name.split("_");
					String assessment_name = nameSubjectCode[0];
					String subject_code = nameSubjectCode[1];
					if(nameSubjectCode.length > 2) {
						String tmp_name = "";
						for(int i=0;i < nameSubjectCode.length - 1;i++) {
							if(i==0) {
								tmp_name += nameSubjectCode[i];
							}else {
								tmp_name += "_" + nameSubjectCode[i];
							}
						} 
						assessment_name = tmp_name;
						subject_code = nameSubjectCode[nameSubjectCode.length - 1];
					}
					
					examBookingMettlMappingBean.setName(assessment_name);
					examBookingMettlMappingBean.setSifyCode(subject_code);
					examBookingMettlMappingDAO.insertIntoExamBookingMettlMapping(examBookingMettlMappingBean);
					
					
				}
				
			}
		}else {
		}
		
		return mv;
	}
	
}
