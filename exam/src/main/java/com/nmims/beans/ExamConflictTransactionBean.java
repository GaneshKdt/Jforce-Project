package com.nmims.beans;

import java.io.Serializable;

public class ExamConflictTransactionBean  implements Serializable  {

	private String trackId;
	private String sapid;
	private String year;
	private String month;
	private String amount;
	private String gatewayAmount;
	private String emailId;
	private String mobile;
	private String transactionTime;
	private String action;
	private String createdBy;
	@Override
	public String toString() {
		return "ExamConflictTransactionBean [trackId=" + trackId + ", sapid="
				+ sapid + ", year=" + year + ", month=" + month + ", amount="
				+ amount + ", gatewayAmount=" + gatewayAmount + ", emailId="
				+ emailId + ", mobile=" + mobile + ", transactionTime="
				+ transactionTime + ", action=" + action + ", createdBy="
				+ createdBy + "]";
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getGatewayAmount() {
		return gatewayAmount;
	}
	public void setGatewayAmount(String gatewayAmount) {
		this.gatewayAmount = gatewayAmount;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
	
}
