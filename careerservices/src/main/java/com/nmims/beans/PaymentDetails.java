package com.nmims.beans;

import java.io.Serializable;

public class PaymentDetails implements Serializable {

	private String id;
	private String checkSumHash;
	private String message;
	private String packageId;
	private String paymentId;
	private String merchantTrackId;
	private String paymentInitializationId;
	private String sapid;
	private String status;
	
	private boolean packageAddStatus;
	private boolean entitlementAddStatus;
	private boolean checkSumStatus;
	private boolean paymentStatus;
	private String source;
	private String reasonForFail;
	
	public boolean isPackageAddStatus() {
		return packageAddStatus;
	}
	public void setPackageAddStatus(boolean packageAddStatus) {
		this.packageAddStatus = packageAddStatus;
	}
	public boolean isEntitlementAddStatus() {
		return entitlementAddStatus;
	}
	public void setEntitlementAddStatus(boolean entitlementAddStatus) {
		this.entitlementAddStatus = entitlementAddStatus;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public boolean isCheckSumStatus() {
		return checkSumStatus;
	}
	public void setCheckSumStatus(boolean checkSumStatus) {
		this.checkSumStatus = checkSumStatus;
	}
	public String getPaymentInitializationId() {
		return paymentInitializationId;
	}
	public void setPaymentInitializationId(String paymentInitializationId) {
		this.paymentInitializationId = paymentInitializationId;
	}
	public String getCheckSumHash() {
		return checkSumHash;
	}
	public void setCheckSumHash(String checkSumHash) {
		this.checkSumHash = checkSumHash;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReasonForFail() {
		return reasonForFail;
	}
	public void setReasonForFail(String reasonForFail) {
		this.reasonForFail = reasonForFail;
	}
	public String getMerchantTrackId() {
		return merchantTrackId;
	}
	public void setMerchantTrackId(String merchantTrackId) {
		this.merchantTrackId = merchantTrackId;
	}
	@Override
	public String toString() {
		return "PaymentDetails [id=" + id + ", checkSumHash=" + checkSumHash + ", message=" + message + ", packageId="
				+ packageId + ", paymentId=" + paymentId + ", merchantTrackId=" + merchantTrackId
				+ ", paymentInitializationId=" + paymentInitializationId + ", sapid=" + sapid + ", status=" + status
				+ ", packageAddStatus=" + packageAddStatus + ", entitlementAddStatus=" + entitlementAddStatus
				+ ", checkSumStatus=" + checkSumStatus + ", paymentStatus=" + paymentStatus + ", source=" + source
				+ ", reasonForFail=" + reasonForFail + "]";
	}
	
}
