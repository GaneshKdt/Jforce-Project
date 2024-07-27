package com.nmims.beans;

import java.io.Serializable;

public class SalesForceSubmitRegistration implements Serializable {

	private String authToken;
	private String sapId;
	private SalesForceSubmitRegistrationRequest request;
	
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public SalesForceSubmitRegistrationRequest getRequest() {
		return request;
	}
	public void setRequest(SalesForceSubmitRegistrationRequest request) {
		this.request = request;
	}
}
