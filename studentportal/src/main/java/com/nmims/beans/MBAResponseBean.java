package com.nmims.beans;

import java.io.Serializable;

public class MBAResponseBean  implements Serializable  {
	private String status;
	private String error;
	private String errorMessage;
	private String message;
	private Object response;

	@Override
	public String toString() {
		return "MBAResponseBean [status=" + status + ", error=" + error + ", errorMessage=" + errorMessage
				+ ", message=" + message + "]";
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatusSuccess() {
		this.status = "success";
	}
	public void setStatusFail() {
		this.status = "fail";
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
	
	
}
