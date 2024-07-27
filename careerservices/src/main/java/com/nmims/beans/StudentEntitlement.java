package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class StudentEntitlement implements Serializable {

	private String sapid;
	private String purchaseId;
	private boolean activated;
	private boolean activatedByStudent;
	private Date activationDate;
	private Date nextAvailableDate;
	private Date packageStartDate;
	private Date packageEndDate;
	private int activationsLeft;
	private int activationsCurrentlyPossible;
	private boolean ended;
	private Date dateEnded;
	private Date dateAdded;
	private String entitlementId;
	private String packageId;
	private int featureId;
	private String packageName;
	private String featureName;
	private String durationType;
	private String packageFeatureId;
	
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
	private PackageEntitlementInfo entitlementInfo;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
	public boolean isActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	public boolean isActivatedByStudent() {
		return activatedByStudent;
	}
	public void setActivatedByStudent(boolean activatedByStudent) {
		this.activatedByStudent = activatedByStudent;
	}
	public Date getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	public int getActivationsLeft() {
		return activationsLeft;
	}
	public void setActivationsLeft(int activationsLeft) {
		this.activationsLeft = activationsLeft;
	}
	public boolean isEnded() {
		return ended;
	}
	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	public Date getDateEnded() {
		return dateEnded;
	}
	public void setDateEnded(Date dateEnded) {
		this.dateEnded = dateEnded;
	}
	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	public PackageEntitlementInfo getEntitlementInfo() {
		return entitlementInfo;
	}
	public void setEntitlementInfo(PackageEntitlementInfo entitlementInfo) {
		this.entitlementInfo = entitlementInfo;
	}
	public String getEntitlementId() {
		return entitlementId;
	}
	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public int getFeatureId() {
		return featureId;
	}
	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getDurationType() {
		return durationType;
	}
	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}
	public String getPackageFeatureId() {
		return packageFeatureId;
	}
	public void setPackageFeatureId(String packageFeatureId) {
		this.packageFeatureId = packageFeatureId;
	}
	public int getActivationsCurrentlyPossible() {
		return activationsCurrentlyPossible;
	}
	public void setActivationsCurrentlyPossible(int activationsCurrentlyPossible) {
		this.activationsCurrentlyPossible = activationsCurrentlyPossible;
	}
	public Date getNextAvailableDate() {
		return nextAvailableDate;
	}
	public void setNextAvailableDate(Date nextAvailableDate) {
		this.nextAvailableDate = nextAvailableDate;
	}
	
}
