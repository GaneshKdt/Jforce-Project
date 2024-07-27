package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExamRegistrationBeanAPIRequest  implements Serializable  {
	List<String> ApplicableSubjects  ;
	HashMap<String,Integer> mapOfSubjectNameAndExamFee;
	private String sapid;
	HashMap<String,String> corporateCenterUserMapping;
	 
	public HashMap<String, String> getCorporateCenterUserMapping() {
		return corporateCenterUserMapping;
	}

	public void setCorporateCenterUserMapping(HashMap<String, String> corporateCenterUserMapping) {
		this.corporateCenterUserMapping = corporateCenterUserMapping;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public HashMap<String, Integer> getMapOfSubjectNameAndExamFee() {
		return mapOfSubjectNameAndExamFee;
	}

	public void setMapOfSubjectNameAndExamFee(HashMap<String, Integer> mapOfSubjectNameAndExamFee) {
		this.mapOfSubjectNameAndExamFee = mapOfSubjectNameAndExamFee;
	}

	public List<String> getApplicableSubjects() {
		return ApplicableSubjects;
	}

	public void setApplicableSubjects(ArrayList<String> applicableSubjects) {
		ApplicableSubjects = applicableSubjects;
	}


}
