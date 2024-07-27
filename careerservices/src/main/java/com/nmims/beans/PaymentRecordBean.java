package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class PaymentRecordBean implements Serializable {

	private boolean initiationFound; // Records found in nm_Payment_Initiated__c
	private boolean paymentFound; // Records found in nm_Payment__c
	private String sapid; // Student_number__c
	private String paymentType; //nm_PaymentType__c (Career Service)
	private String status; //nm_PaymentStatus__c 
	private String merchantTrackId; //nm_Merchant_Track_Id__c
	private int numberOfSuccessfulPayments;
	private String source;
	private Date lastUpdateOn;
	private String date;
	private String paymentInitiaionId;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	private boolean conflictFound;
	
	public boolean isInitiationFound() {
		return initiationFound;
	}
	public void setInitiationFound(boolean initiationFound) {
		this.initiationFound = initiationFound;
	}
	public boolean isPaymentFound() {
		return paymentFound;
	}
	public void setPaymentFound(boolean paymentFound) {
		this.paymentFound = paymentFound;
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
	public String getMerchantTrackId() {
		return merchantTrackId;
	}
	public void setMerchantTrackId(String merchantTrackId) {
		this.merchantTrackId = merchantTrackId;
	}
	public int getNumberOfSuccessfulPayments() {
		return numberOfSuccessfulPayments;
	}
	public void setNumberOfSuccessfulPayments(int numberOfSuccessfulPayments) {
		this.numberOfSuccessfulPayments = numberOfSuccessfulPayments;
	}
	public boolean isConflictFound() {
		return conflictFound;
	}
	public void setConflictFound(boolean conflictFound) {
		this.conflictFound = conflictFound;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLastUpdateOn() {
		return lastUpdateOn;
	}
	public void setLastUpdateOn(Date lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPaymentInitiaionId() {
		return paymentInitiaionId;
	}
	public void setPaymentInitiaionId(String paymentInitiaionId) {
		this.paymentInitiaionId = paymentInitiaionId;
	}
}
