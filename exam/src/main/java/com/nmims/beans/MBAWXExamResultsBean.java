package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAWXExamResultsBean  implements Serializable  {

	private String status;
	private String errorMessage;
	private List<MBAWXExamResultForSubject> data;
	
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
	public List<MBAWXExamResultForSubject> getData() {
		return data;
	}
	public void setData(List<MBAWXExamResultForSubject> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "MBAWXExamResultsBean [status=" + status + ", errorMessage=" + errorMessage + ", data=" + data + "]";
	}
	
}
