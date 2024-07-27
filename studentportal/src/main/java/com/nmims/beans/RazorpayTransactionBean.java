package com.nmims.beans;

import java.io.Serializable;

public class RazorpayTransactionBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String refundId;
	private String trackId;
	private String orderId;
	private String paymentId;
	private String paymentStatus;
	private String refundStatus;
	private String orderStatus;
	private String amount;
	private String createdAt;
	private String transactionMethod;
	private boolean hasError;
	private String error;
	private String currency;
	private String bank;
	private String merchantRefNo;

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getTransactionMethod() {
		return transactionMethod;
	}

	public void setTransactionMethod(String transactionMethod) {
		this.transactionMethod = transactionMethod;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getMerchantRefNo() {
		return merchantRefNo;
	}

	public void setMerchantRefNo(String merchantRefNo) {
		this.merchantRefNo = merchantRefNo;
	}

	@Override
	public String toString() {
		return "TransactionRefundBean [refundId=" + refundId + ", trackId=" + trackId + ", orderId=" + orderId
				+ ", paymentId=" + paymentId + ", paymentStatus=" + paymentStatus + ", refundStatus=" + refundStatus
				+ ", orderStatus=" + orderStatus + ", amount=" + amount + ", createdAt=" + createdAt
				+ ", transactionMethod=" + transactionMethod + ", hasError=" + hasError + ", error=" + error
				+ ", currency=" + currency + ", bank=" + bank + "]";
	}

}
