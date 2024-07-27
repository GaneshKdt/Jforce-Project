package com.nmims.beans;

import java.io.Serializable;

public class AlmashinesBean implements Serializable {
	
	private String status;
	private String urlLocation;
	private String errorMessage;
	private String successMessage;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUrlLocation() {
		return urlLocation;
	}
	public void setUrlLocation(String urlLocation) {
		this.urlLocation = urlLocation;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	
	

}
