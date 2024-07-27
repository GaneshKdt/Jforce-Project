package com.nmims.beans;

import java.io.Serializable;

public class SalesForceGetRequest implements Serializable {
	private String authToken;
	private String request;
	
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
}
