package com.nmims.beans;

import java.io.Serializable;

public class PackageFeature implements Serializable{
	
	
	private Feature feature = new Feature();
	private PackageEntitlementInfo entitlement;
	private String uid;
	private String packageId;
	private String featureId;
	private String featureName;
	private String packageName;
	private String durationType;
	
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
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public PackageEntitlementInfo getEntitlement() {
		return entitlement;
	}
	public void setEntitlement(PackageEntitlementInfo entitlement) {
		this.entitlement = entitlement;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getDurationType() {
		return durationType;
	}
	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}
}
