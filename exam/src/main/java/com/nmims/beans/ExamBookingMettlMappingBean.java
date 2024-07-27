package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingMettlMappingBean  implements Serializable {
	public String sifyCode;
	public String name;
	public String assessmentId;
	public String created_at;
	public String updated_at;

	public String getSifyCode() {
		return sifyCode;
	}
	public void setSifyCode(String sifyCode) {
		this.sifyCode = sifyCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAssessmentId() {
		return assessmentId;
	}
	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	
	
}
