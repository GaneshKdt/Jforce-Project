package com.nmims.beans;

import java.io.Serializable;

public class CSResponse  implements Serializable{

	private String status;
	private String message;
	private Object response;
	
	public String getStatus() {
		return status;
	}
	public void setStatusSuccess() {
		this.status = "success";
	}
	public void setStatusFailure() {
		this.status = "fail";
	}

	public void setNoSapid() {
		this.status = "fail";
		this.message = "no sapid";
	}

	public void setInvalidSapid() {
		this.status = "fail";
		this.message = "invalid sapid";
	}

	public void setNoPacakgeId() {
		this.status = "fail";
		this.message = "no packageId";
	}

	public void setInvalidPacakgeId() {
		this.status = "fail";
		this.message = "invalid packageId";
	}

	public void setNotValidRequest() {
		this.status = "fail";
		this.message = "invalid or empty request";
	}
	
	public void setNoCSAccess() {
		this.status = "fail";
		this.message = "sapid doesnt have cs access";
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
