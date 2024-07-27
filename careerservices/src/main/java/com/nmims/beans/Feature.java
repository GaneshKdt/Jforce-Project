package com.nmims.beans;

import java.io.Serializable;

public class Feature implements Serializable{
	

	private String featureId;

	private String featureDescription;

	private String featureName;
	
	//validity of this feature (in months)
	private int validityNormal;
	private int validityFast;
	private int validitySlow;
	private int validity;
	
	private StudentEntitlement entitlementDetails;
	
	public int getValidity() {
		return validity;
	}
	public void setValidity(int validity) {
		this.validity = validity;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getFeatureDescription() {
		return featureDescription;
	}
	public void setFeatureDescription(String featureDescription) {
		this.featureDescription = featureDescription;
	}
	public int getValidityNormal() {
		return validityNormal;
	}
	public void setValidityNormal(int validityNormal) {
		this.validityNormal = validityNormal;
	}
	public int getValidityFast() {
		return validityFast;
	}
	public void setValidityFast(int validityFast) {
		this.validityFast = validityFast;
	}
	public int getValiditySlow() {
		return validitySlow;
	}
	public void setValiditySlow(int validitySlow) {
		this.validitySlow = validitySlow;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public StudentEntitlement getEntitlementDetails() {
		return entitlementDetails;
	}
	public void setEntitlementDetails(StudentEntitlement entitlementDetails) {
		this.entitlementDetails = entitlementDetails;
	}
}
