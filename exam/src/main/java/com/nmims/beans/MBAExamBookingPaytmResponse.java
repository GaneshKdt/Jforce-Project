package com.nmims.beans;

import java.io.Serializable;
import java.util.Map;

public class MBAExamBookingPaytmResponse  implements Serializable   {
	private Map<String, String> apiresponse; 
	private String checkSum;
	private String trackId;
    private String amount;
    private String paymentOption;
    private String sapid;
    
	public Map<String, String> getApiresponse() {
		return apiresponse;
	}
	public void setApiresponse(Map<String, String> apiresponse) {
		this.apiresponse = apiresponse;
	}
	public String getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
}
