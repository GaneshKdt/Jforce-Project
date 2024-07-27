package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ReturnStatus implements Serializable {

	private String status;
	private String message;
	private String error;
	private List<String> offenderList;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getOffenderList() {
		return offenderList;
	}
	public void setOffenderList(List<String> hashMap) {
		this.offenderList = hashMap;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
