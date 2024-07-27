package com.nmims.beans;

import java.io.Serializable;

public class ZoomGetJoinURLModelBean implements Serializable {

	private String status;
	private String message;
	private String joinURL;
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
	public String getJoinURL() {
		return joinURL;
	}
	public void setJoinURL(String joinURL) {
		this.joinURL = joinURL;
	}
}
