package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class PaymentInitiationModelBean  implements Serializable{

	private String sapid;
	private String packageId;
	private String salesForcePackageId;
	private String source;
	private String salesForceURL;
	private String paymentInitializedId;
	private boolean packageApplicable;
	private boolean canStartPayment;
	private boolean paymentPreviouslyInitiated;
	private String errorMessage;
	
	private Date lastUpdateOn;
	private String merchantTrackId;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getSalesForceURL() {
		return salesForceURL;
	}
	public void setSalesForceURL(String salesForceURL) {
		this.salesForceURL = salesForceURL;
	}
	public boolean isPackageApplicable() {
		return packageApplicable;
	}
	public void setPackageApplicable(boolean packageApplicable) {
		this.packageApplicable = packageApplicable;
	}
	public boolean isCanStartPayment() {
		return canStartPayment;
	}
	public void setCanStartPayment(boolean canStartPayment) {
		this.canStartPayment = canStartPayment;
	}
	public boolean isPaymentPreviouslyInitiated() {
		return paymentPreviouslyInitiated;
	}
	public void setPaymentPreviouslyInitiated(boolean paymentPreviouslyInitiated) {
		this.paymentPreviouslyInitiated = paymentPreviouslyInitiated;
	}
	public String getSalesForcePackageId() {
		return salesForcePackageId;
	}
	public void setSalesForcePackageId(String salesForcePackageId) {
		this.salesForcePackageId = salesForcePackageId;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPaymentInitializedId() {
		return paymentInitializedId;
	}
	public void setPaymentInitializedId(String paymentInitializedId) {
		this.paymentInitializedId = paymentInitializedId;
	}
	
}
