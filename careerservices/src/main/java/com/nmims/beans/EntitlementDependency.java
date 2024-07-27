package com.nmims.beans;

import java.io.Serializable;

public class EntitlementDependency  implements Serializable{
	
	private String packageName;
	private String featureName;
	private String durationType;
	
	private String id;
	private String dependsOnFeatureId;
	private String entitlementId;
	
	private boolean requiresCompletion;
	private int monthsAfterCompletion;
	
	private boolean requiresActivationOnly;
	private int monthsAfterActivation;
	
	private int activationsMinimumRequired;

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

	public String getDependsOnFeatureId() {
		return dependsOnFeatureId;
	}

	public void setDependsOnFeatureId(String dependsOnFeatureId) {
		this.dependsOnFeatureId = dependsOnFeatureId;
	}

	public String getEntitlementId() {
		return entitlementId;
	}

	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}

	public boolean isRequiresCompletion() {
		return requiresCompletion;
	}

	public void setRequiresCompletion(boolean requiresCompletion) {
		this.requiresCompletion = requiresCompletion;
	}

	public int getMonthsAfterCompletion() {
		return monthsAfterCompletion;
	}

	public void setMonthsAfterCompletion(int monthsAfterCompletion) {
		this.monthsAfterCompletion = monthsAfterCompletion;
	}

	public boolean isRequiresActivationOnly() {
		return requiresActivationOnly;
	}

	public void setRequiresActivationOnly(boolean requiresActivationOnly) {
		this.requiresActivationOnly = requiresActivationOnly;
	}

	public int getMonthsAfterActivation() {
		return monthsAfterActivation;
	}

	public void setMonthsAfterActivation(int monthsAfterActivation) {
		this.monthsAfterActivation = monthsAfterActivation;
	}

	public int getActivationsMinimumRequired() {
		return activationsMinimumRequired;
	}

	public void setActivationsMinimumRequired(int activationsMinimumRequired) {
		this.activationsMinimumRequired = activationsMinimumRequired;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
