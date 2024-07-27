package com.nmims.dto;

import com.nmims.beans.ProjectStudentStatus;

public class GuidedProjectDto {
	
	private String sapid;
	private Boolean error;
	private String errorMessage;
	ProjectStudentStatus statusBean;
	
	
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public ProjectStudentStatus getStatusBean() {
		return statusBean;
	}
	public void setStatusBean(ProjectStudentStatus statusBean) {
		this.statusBean = statusBean;
	}
	
	

}
