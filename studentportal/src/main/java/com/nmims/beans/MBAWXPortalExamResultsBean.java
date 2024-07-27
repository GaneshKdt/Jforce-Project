package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAWXPortalExamResultsBean  implements Serializable  {

	private String status;
	private String errorMessage;
	private List<MBAWXPortalExamResultForSubject> data;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<MBAWXPortalExamResultForSubject> getData() {
		return data;
	}
	public void setData(List<MBAWXPortalExamResultForSubject> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "MBAWXExamResultsBean [status=" + status + ", errorMessage=" + errorMessage + ", data=" + data + "]";
	}
	
}
