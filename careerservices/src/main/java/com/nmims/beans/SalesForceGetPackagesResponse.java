package com.nmims.beans;

import java.io.Serializable;

public class SalesForceGetPackagesResponse implements Serializable {

	
	private String errorCode;
	private String message;
	private SalesForcePackageList packages;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public SalesForcePackageList getPackages() {
		return packages;
	}
	public void setPackages(SalesForcePackageList packages) {
		this.packages = packages;
	}
}
