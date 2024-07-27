package com.nmims.beans;

import java.io.Serializable;

public class PackageEntitlementInfo implements Serializable{

	private String entitlementId;
	private String packageFeaturesId;
	private String packageName;
	private String featureName;
	private String durationType;
	private boolean requiresOtherEntitlement;
	private boolean requiresStudentActivation;
	private int totalActivations;
	private int initialActivations;
	private int initialCycleGapMonths;
	private int initialCycleGapDays;
	private int activationCycleMonths;
	private int activationCycleDays;
	private int activationsEveryCycle;
	private boolean extendIfActivationsLeft;
	private int extendByMaxMonths;
	private int extendByMaxDays;
	private int duration;
	private EntitlementDependencies dependencies;
	private boolean hasViewableData;
	private boolean giveAccessAfterExpiry;
	private boolean giveAccessAfterActivationsConsumed;

	public int getInitialCycleGapMonths() {
		return initialCycleGapMonths;
	}
	public void setInitialCycleGapMonths(int initialCycleGapMonths) {
		this.initialCycleGapMonths = initialCycleGapMonths;
	}
	public int getInitialCycleGapDays() {
		return initialCycleGapDays;
	}
	public void setInitialCycleGapDays(int initialCycleGapDays) {
		this.initialCycleGapDays = initialCycleGapDays;
	}
	public int getActivationCycleDays() {
		return activationCycleDays;
	}
	public void setActivationCycleDays(int activationCycleDays) {
		this.activationCycleDays = activationCycleDays;
	}
	public int getExtendByMaxDays() {
		return extendByMaxDays;
	}
	public void setExtendByMaxDays(int extendByMaxDays) {
		this.extendByMaxDays = extendByMaxDays;
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
	public String getEntitlementId() {
		return entitlementId;
	}
	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}
	public boolean isRequiresOtherEntitlement() {
		return requiresOtherEntitlement;
	}
	public void setRequiresOtherEntitlement(boolean RequiresOtherEntitlement) {
		this.requiresOtherEntitlement = RequiresOtherEntitlement;
	}
	public boolean isRequiresStudentActivation() {
		return requiresStudentActivation;
	}
	public void setRequiresStudentActivation(boolean requiresStudentActivation) {
		this.requiresStudentActivation = requiresStudentActivation;
	}
	public int getTotalActivations() {
		return totalActivations;
	}
	public void setTotalActivations(int totalActivations) {
		this.totalActivations = totalActivations;
	}
	public int getInitialActivations() {
		return initialActivations;
	}
	public void setInitialActivations(int initialActivations) {
		this.initialActivations = initialActivations;
	}
	public int getActivationCycleMonths() {
		return activationCycleMonths;
	}
	public void setActivationCycleMonths(int activationCycleMonths) {
		this.activationCycleMonths = activationCycleMonths;
	}
	public int getActivationsEveryCycle() {
		return activationsEveryCycle;
	}
	public void setActivationsEveryCycle(int activationsEveryCycle) {
		this.activationsEveryCycle = activationsEveryCycle;
	}
	public boolean isExtendIfActivationsLeft() {
		return extendIfActivationsLeft;
	}
	public void setExtendIfActivationsLeft(boolean extendIfActivationsLeft) {
		this.extendIfActivationsLeft = extendIfActivationsLeft;
	}
	public int getExtendByMaxMonths() {
		return extendByMaxMonths;
	}
	public void setExtendByMaxMonths(int extendByMaxMonths) {
		this.extendByMaxMonths = extendByMaxMonths;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public EntitlementDependencies getDependencies() {
		return dependencies;
	}
	public void setDependencies(EntitlementDependencies dependencies) {
		this.dependencies = dependencies;
	}
	public String getPackageFeaturesId() {
		return packageFeaturesId;
	}
	public void setPackageFeaturesId(String packageFeaturesId) {
		this.packageFeaturesId = packageFeaturesId;
	}
	public boolean isGiveAccessAfterExpiry() {
		return giveAccessAfterExpiry;
	}
	public void setGiveAccessAfterExpiry(boolean giveAccessAfterExpiry) {
		this.giveAccessAfterExpiry = giveAccessAfterExpiry;
	}
	public boolean isHasViewableData() {
		return hasViewableData;
	}
	public void setHasViewableData(boolean hasViewableData) {
		this.hasViewableData = hasViewableData;
	}
	public boolean isGiveAccessAfterActivationsConsumed() {
		return giveAccessAfterActivationsConsumed;
	}
	public void setGiveAccessAfterActivationsConsumed(boolean giveAccessAfterActivationsConsumed) {
		this.giveAccessAfterActivationsConsumed = giveAccessAfterActivationsConsumed;
	}
}
