package com.nmims.beans;

import java.io.Serializable;

public class SalesForceSubmitRegistrationRequest implements Serializable {

	private String successURL;
	private String failureURL;
	private String packageId;
	private String paymentInitializationId;
	
	public String getSuccessURL() {
		return successURL;
	}
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}
	public String getFailureURL() {
		return failureURL;
	}
	public void setFailureURL(String failureURL) {
		this.failureURL = failureURL;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getPaymentInitializationId() {
		return paymentInitializationId;
	}
	public void setPaymentInitializationId(String paymentInitializationId) {
		this.paymentInitializationId = paymentInitializationId;
	}
}
