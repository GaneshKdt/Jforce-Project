package com.nmims.beans;

import java.util.Date;

public class RevenueReportField {

	private String id;
	
	private String type;
	private String paymentStatus;
	private String amount;
	private String prospect;
	private Date transactionTransactionDate;
	private String actualPaymentAmount;
	private String refundedAmount;
	private String errorMessage;
	private String date; 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getActualPaymentAmount() {
		return actualPaymentAmount;
	}
	public void setActualPaymentAmount(String actualPaymentAmount) {
		this.actualPaymentAmount = actualPaymentAmount;
	}
	public String getRefundedAmount() {
		return refundedAmount;
	}
	public void setRefundedAmount(String refundedAmount) {
		this.refundedAmount = refundedAmount;
	}
	
	@Override
	public String toString() {
		return "RevenueReportField [id=" + id + ", type=" + type + ", paymentStatus=" + paymentStatus + ", amount="
				+ amount + ", prospect=" + prospect + ", transactionTransactionDate=" + transactionTransactionDate
				+ ", actualPaymentAmount=" + actualPaymentAmount + ", refundedAmount=" + refundedAmount
				+ ", errorMessage=" + errorMessage + "]";
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Date getTransactionDate() {
		return transactionTransactionDate;
	}
	public void setTransactionDate(Date transactionTransactionDate) {
		this.transactionTransactionDate = transactionTransactionDate;
	}
	public String getProspect() {
		return prospect;
	}
	public void setProspect(String prospect) {
		this.prospect = prospect;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
