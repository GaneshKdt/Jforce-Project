package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StudentEntitlements implements Serializable {
	private List<StudentEntitlement> entitlements;
	private String familyId;
	private String purchaseId;
	private int featureId;
	private String packageName;
	private Date startDate;
	private Date endDate;
	private String packageDescription;
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
	public List<StudentEntitlement> getEntitlements() {
		return entitlements;
	}
	public void setEntitlements(List<StudentEntitlement> entitlements) {
		this.entitlements = entitlements;
	}
	public String getPackageDescription() {
		return packageDescription;
	}
	public void setPackageDescription(String packageDescription) {
		this.packageDescription = packageDescription;
	}
	public String getFamilyId() {
		return familyId;
	}
	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}
	public int getFeatureId() {
		return featureId;
	}
	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
}
