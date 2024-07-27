package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class AssignmentHistoryResponseBean  implements Serializable {
	
	List<AssignmentFileBean> data;
	String error;
	String errorMessage;
	public List<AssignmentFileBean> getData() {
		return data;
	}
	public void setData(List<AssignmentFileBean> data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
