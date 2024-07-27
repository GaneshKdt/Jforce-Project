package com.nmims.beans;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalesForceSubmitRegistrationResponseObject implements Serializable {
	
	@SerializedName("payment_url")
	@Expose
	private String paymentUrl;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("paymentInitializationId")
	@Expose
	private String paymentInitializationId;
	

	public String getPaymentInitializationId() {
		return paymentInitializationId;
	}

	public void setPaymentInitializationId(String paymentInitializationId) {
		this.paymentInitializationId = paymentInitializationId;
	}
	public String getPaymentUrl() {
	return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
	this.paymentUrl = paymentUrl;
	}

	public String getStatus() {
	return status;
	}

	public void setStatus(String status) {
	this.status = status;
	}
}
