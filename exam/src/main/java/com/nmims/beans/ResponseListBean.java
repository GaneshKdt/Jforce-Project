package com.nmims.beans;

import java.util.List;

public class ResponseListBean {

	private List<MettlStudentTestInfo> successList;
	private List<MettlStudentTestInfo> errorList;
	private String error;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public List<MettlStudentTestInfo> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<MettlStudentTestInfo> successList) {
		this.successList = successList;
	}
	public List<MettlStudentTestInfo> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<MettlStudentTestInfo> errorList) {
		this.errorList = errorList;
	}
	
}
