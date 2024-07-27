package com.nmims.beans;

import java.io.Serializable;

// spring security related changes rename AdhocPaymentBean to ExamAdhocPaymentBean 
public class ExamAdhocPaymentBean extends BaseExamBean implements Serializable{

	private Long id;
	private String emailId;
	private String paymentType;
	private String sapId;
	private String trackId;
	private String amount;
	private String tranDateTime;
	private String tranStatus;
	private String requestStatus;
	private String requestClosedDate;
	private String transactionID;
	private String requestID;
	private String merchantRefNo;
	private String secureHash;
	private String respAmount;
	private String description;
	private String responseCode;
	private String respPaymentMethod;
	private String isFlagged;
	private String paymentID;
	private String responseMessage;
	private String error;
	private String respTranDateTime;
	private String program;
	private String year;
	private String month;
	private String pendingAmount;
	private String mobile;
	
	
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPendingAmount() {
		return pendingAmount;
	}
	public void setPendingAmount(String pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getRespTranDateTime() {
		return respTranDateTime;
	}
	public void setRespTranDateTime(String respTranDateTime) {
		this.respTranDateTime = respTranDateTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
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
	public String getTranDateTime() {
		return tranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getRequestClosedDate() {
		return requestClosedDate;
	}
	public void setRequestClosedDate(String requestClosedDate) {
		this.requestClosedDate = requestClosedDate;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public String getMerchantRefNo() {
		return merchantRefNo;
	}
	public void setMerchantRefNo(String merchantRefNo) {
		this.merchantRefNo = merchantRefNo;
	}
	public String getSecureHash() {
		return secureHash;
	}
	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}
	public String getRespAmount() {
		return respAmount;
	}
	public void setRespAmount(String respAmount) {
		this.respAmount = respAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRespPaymentMethod() {
		return respPaymentMethod;
	}
	public void setRespPaymentMethod(String respPaymentMethod) {
		this.respPaymentMethod = respPaymentMethod;
	}
	public String getIsFlagged() {
		return isFlagged;
	}
	public void setIsFlagged(String isFlagged) {
		this.isFlagged = isFlagged;
	}
	public String getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
