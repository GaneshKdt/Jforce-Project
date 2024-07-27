package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - AdhocPaymentBean
 * @author
 *
 */
public class AdhocPaymentStudentPortalBean extends BaseStudentPortalBean implements Serializable{

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
	private String feesType;
	private String refundAmount;
	private String refundStatus;
	private String transactionType;
	private String paymentOption;
	private String status;
	private String refId;
	private String refundId;
	private String firstName;
	
	
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getFeesType() {
		return feesType;
	}
	public void setFeesType(String feesType) {
		this.feesType = feesType;
	}
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Override
	public String toString() {
		return "AdhocPaymentStudentPortalBean [id=" + id + ", emailId=" + emailId + ", paymentType=" + paymentType
				+ ", sapId=" + sapId + ", trackId=" + trackId + ", amount=" + amount + ", tranDateTime=" + tranDateTime
				+ ", tranStatus=" + tranStatus + ", requestStatus=" + requestStatus + ", requestClosedDate="
				+ requestClosedDate + ", transactionID=" + transactionID + ", requestID=" + requestID
				+ ", merchantRefNo=" + merchantRefNo + ", secureHash=" + secureHash + ", respAmount=" + respAmount
				+ ", description=" + description + ", responseCode=" + responseCode + ", respPaymentMethod="
				+ respPaymentMethod + ", isFlagged=" + isFlagged + ", paymentID=" + paymentID + ", responseMessage="
				+ responseMessage + ", error=" + error + ", respTranDateTime=" + respTranDateTime + ", program="
				+ program + ", year=" + year + ", month=" + month + ", pendingAmount=" + pendingAmount + ", mobile="
				+ mobile + ", feesType=" + feesType + ", refundAmount=" + refundAmount + ", refundStatus="
				+ refundStatus + ", transactionType=" + transactionType + ", paymentOption=" + paymentOption
				+ ", status=" + status + ", refId=" + refId + ", refundId=" + refundId + ", firstName=" + firstName
				+ "]";
	}
}
