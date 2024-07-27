package com.nmims.beans;

import java.io.Serializable;
import java.util.Map;

public class MBAPaymentRequest  implements Serializable  {

	public static final String TRAN_STATUS_FREE = "Free";
	public static final String TRAN_STATUS_INITIATED = "Initiated";
	public static final String TRAN_STATUS_SUCCESSFUL = "Payment Successful"; 
	public static final String TRAN_STATUS_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
	public static final String TRAN_STATUS_EXPIRED = "Expired";
	
	public static final String REQUEST_STATUS_PAYMENT_PENDING = "Payment Pending";
	public static final String REQUEST_STATUS_PAYMENT_FAILED = "Payment Failed";
	public static final String REQUEST_STATUS_SUBMITTED = "Submitted";
	public static final String REQUEST_STATUS_CLOSED = "Submitted";
	
	public static final String PAYMENT_TYPE_BOOKING = "Exam Booking";
	
	
	private String requestStatus;
	private boolean generatePaymentMap;
	private Long id;
	protected String sapid;
	private String paymentType;
	private Map<String, String> formParameters;
	private String transactionUrl;
	private String source;
	private String paymentOption;
	private String trackId;
	private String amount;
	private String tranDateTime;
	protected String tranStatus;
	private String transactionID;
	protected String requestID;
	protected String merchantRefNo;
	protected String secureHash;
	protected String respAmount;
	private String description;
	protected String responseCode;
	protected String respPaymentMethod;
	protected String isFlagged;
	protected String paymentID;
	protected String responseMessage;
	protected String error;
	protected String respTranDateTime;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	protected String bankName;

	private String paymentProviderImage;
	
	private String bookingStatus;
	// For PayTm callback from mobile
	private String apiresponse;
	
	private String callbackURL;
	
	private boolean isWeb;
	
	private boolean successFromGateway;
	
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public boolean isGeneratePaymentMap() {
		return generatePaymentMap;
	}
	public void setGeneratePaymentMap(boolean generatePaymentMap) {
		this.generatePaymentMap = generatePaymentMap;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public Map<String, String> getFormParameters() {
		return formParameters;
	}
	public void setFormParameters(Map<String, String> formParameters) {
		this.formParameters = formParameters;
	}
	public String getTransactionUrl() {
		return transactionUrl;
	}
	public void setTransactionUrl(String transactionUrl) {
		this.transactionUrl = transactionUrl;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
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
	public String getRespTranDateTime() {
		return respTranDateTime;
	}
	public void setRespTranDateTime(String respTranDateTime) {
		this.respTranDateTime = respTranDateTime;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPaymentProviderImage() {
		return paymentProviderImage;
	}
	public void setPaymentProviderImage(String paymentProviderImage) {
		this.paymentProviderImage = paymentProviderImage;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getApiresponse() {
		return apiresponse;
	}
	public void setApiresponse(String apiresponse) {
		this.apiresponse = apiresponse;
	}
	@Override
	public String toString() {
		return "MBAWXPaymentRequest [requestStatus=" + requestStatus + ", generatePaymentMap=" + generatePaymentMap
				+ ", id=" + id + ", sapid=" + sapid + ", paymentType=" + paymentType + ", formParameters="
				+ formParameters + ", transactionUrl=" + transactionUrl + ", source=" + source + ", paymentOption="
				+ paymentOption + ", trackId=" + trackId + ", amount=" + amount + ", tranDateTime=" + tranDateTime
				+ ", tranStatus=" + tranStatus + ", transactionID=" + transactionID + ", requestID=" + requestID
				+ ", merchantRefNo=" + merchantRefNo + ", secureHash=" + secureHash + ", respAmount=" + respAmount
				+ ", description=" + description + ", responseCode=" + responseCode + ", respPaymentMethod="
				+ respPaymentMethod + ", isFlagged=" + isFlagged + ", paymentID=" + paymentID + ", responseMessage="
				+ responseMessage + ", error=" + error + ", respTranDateTime=" + respTranDateTime + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", bankName=" + bankName + ", paymentProviderImage="
				+ paymentProviderImage + ", bookingStatus=" + bookingStatus + ", apiresponse=" + apiresponse + "]";
	}
	public boolean isWeb() {
		return isWeb;
	}
	public void setWeb(boolean isWeb) {
		this.isWeb = isWeb;
	}
	public boolean isSuccessFromGateway() {
		return successFromGateway;
	}
	public void setSuccessFromGateway(boolean successFromGateway) {
		this.successFromGateway = successFromGateway;
	}
	public String getCallbackURL() {
		return callbackURL;
	}
	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}
	
}
