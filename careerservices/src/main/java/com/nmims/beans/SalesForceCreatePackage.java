package com.nmims.beans;

import java.io.Serializable;

public class SalesForceCreatePackage implements Serializable {

	private String authToken;
	private SalesForceCreatePackageRequest request;
	
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public SalesForceCreatePackageRequest getRequest() {
		return request;
	}
	public void setRequest(SalesForceCreatePackageRequest request) {
		this.request = request;
	}
}
