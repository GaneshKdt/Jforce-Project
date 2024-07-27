package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class ActivationInfo  implements Serializable{
	
	private String featureName;
	private String packageName;
	private Date packageStartDate;
	private Date packageEndDate;
	private String receiptId;
	
	private int totalActivations;
	private int activationsLeft;
	private int activationsPossible;
	private Date nextActivationAvailableDate;
	
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Date getPackageStartDate() {
		return packageStartDate;
	}
	public void setPackageStartDate(Date packageStartDate) {
		this.packageStartDate = packageStartDate;
	}
	public Date getPackageEndDate() {
		return packageEndDate;
	}
	public void setPackageEndDate(Date packageEndDate) {
		this.packageEndDate = packageEndDate;
	}
	public String getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
	public int getTotalActivations() {
		return totalActivations;
	}
	public void setTotalActivations(int totalActivations) {
		this.totalActivations = totalActivations;
	}
	public int getActivationsLeft() {
		return activationsLeft;
	}
	public void setActivationsLeft(int activationsLeft) {
		this.activationsLeft = activationsLeft;
	}
	public int getActivationsPossible() {
		return activationsPossible;
	}
	public void setActivationsPossible(int activationsPossible) {
		this.activationsPossible = activationsPossible;
	}
	public Date getNextActivationAvailableDate() {
		return nextActivationAvailableDate;
	}
	public void setNextActivationAvailableDate(Date nextActivationAvailableDate) {
		this.nextActivationAvailableDate = nextActivationAvailableDate;
	}
}
