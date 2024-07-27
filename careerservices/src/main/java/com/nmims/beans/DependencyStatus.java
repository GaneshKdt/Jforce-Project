package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class DependencyStatus  implements Serializable{

	private boolean dependencyFulfilled;

	private String featureId; 
	
	private boolean requiresCompletionConditionFulfilled;
	private boolean requiresActivationConditionFulfilled;

	//months after completion condition
	private boolean monthsAfterCompletionConditionFulfilled;
	private int monthsAfterCompletionConditionMonthsTotal;
	private int monthsAfterCompletionConditionMonthsLeft;
	private long monthsAfterCompletionConditionDaysLeft;
	private Date monthsAfterCompletionConditionWillActivateOn;

	//months after activation condition
	private boolean monthsAfterActivationConditionFulfilled;
	private int monthsAfterActivationConditionMonthsTotal;
	private int monthsAfterActivationConditionMonthsLeft;
	private long monthsAfterActivationConditionDaysLeft;
	private Date monthsAfterActivationConditionWillActivateOn;
	
	//min activations required condition
	private boolean minimumActivationsRequiredConditionFulfilled;
	private int minimumActivationsRequiredConditionActivationsLeft;
	
	
	public boolean isDependencyFulfilled() {
		return dependencyFulfilled;
	}
	public void setDependencyFulfilled(boolean dependencyFulfilled) {
		this.dependencyFulfilled = dependencyFulfilled;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public boolean isRequiresCompletionConditionFulfilled() {
		return requiresCompletionConditionFulfilled;
	}
	public void setRequiresCompletionConditionFulfilled(boolean requiresCompletionConditionFulfilled) {
		this.requiresCompletionConditionFulfilled = requiresCompletionConditionFulfilled;
	}
	public boolean isRequiresActivationConditionFulfilled() {
		return requiresActivationConditionFulfilled;
	}
	public void setRequiresActivationConditionFulfilled(boolean requiresActivationConditionFulfilled) {
		this.requiresActivationConditionFulfilled = requiresActivationConditionFulfilled;
	}
	public boolean isMonthsAfterCompletionConditionFulfilled() {
		return monthsAfterCompletionConditionFulfilled;
	}
	public void setMonthsAfterCompletionConditionFulfilled(boolean monthsAfterCompletionConditionFulfilled) {
		this.monthsAfterCompletionConditionFulfilled = monthsAfterCompletionConditionFulfilled;
	}
	public int getMonthsAfterCompletionConditionMonthsTotal() {
		return monthsAfterCompletionConditionMonthsTotal;
	}
	public void setMonthsAfterCompletionConditionMonthsTotal(int monthsAfterCompletionConditionMonthsTotal) {
		this.monthsAfterCompletionConditionMonthsTotal = monthsAfterCompletionConditionMonthsTotal;
	}
	public int getMonthsAfterCompletionConditionMonthsLeft() {
		return monthsAfterCompletionConditionMonthsLeft;
	}
	public void setMonthsAfterCompletionConditionMonthsLeft(int monthsAfterCompletionConditionMonthsLeft) {
		this.monthsAfterCompletionConditionMonthsLeft = monthsAfterCompletionConditionMonthsLeft;
	}
	public Date getMonthsAfterCompletionConditionWillActivateOn() {
		return monthsAfterCompletionConditionWillActivateOn;
	}
	public void setMonthsAfterCompletionConditionWillActivateOn(Date monthsAfterCompletionConditionWillActivateOn) {
		this.monthsAfterCompletionConditionWillActivateOn = monthsAfterCompletionConditionWillActivateOn;
	}
	public boolean isMonthsAfterActivationConditionFulfilled() {
		return monthsAfterActivationConditionFulfilled;
	}
	public void setMonthsAfterActivationConditionFulfilled(boolean monthsAfterActivationConditionFulfilled) {
		this.monthsAfterActivationConditionFulfilled = monthsAfterActivationConditionFulfilled;
	}
	public int getMonthsAfterActivationConditionMonthsTotal() {
		return monthsAfterActivationConditionMonthsTotal;
	}
	public void setMonthsAfterActivationConditionMonthsTotal(int monthsAfterActivationConditionMonthsTotal) {
		this.monthsAfterActivationConditionMonthsTotal = monthsAfterActivationConditionMonthsTotal;
	}
	public int getMonthsAfterActivationConditionMonthsLeft() {
		return monthsAfterActivationConditionMonthsLeft;
	}
	public void setMonthsAfterActivationConditionMonthsLeft(int monthsAfterActivationConditionMonthsLeft) {
		this.monthsAfterActivationConditionMonthsLeft = monthsAfterActivationConditionMonthsLeft;
	}
	public long getMonthsAfterActivationConditionDaysLeft() {
		return monthsAfterActivationConditionDaysLeft;
	}
	public void setMonthsAfterActivationConditionDaysLeft(long monthsAfterActivationConditionDaysLeft) {
		this.monthsAfterActivationConditionDaysLeft = monthsAfterActivationConditionDaysLeft;
	}
	public Date getMonthsAfterActivationConditionWillActivateOn() {
		return monthsAfterActivationConditionWillActivateOn;
	}
	public void setMonthsAfterActivationConditionWillActivateOn(Date monthsAfterActivationConditionWillActivateOn) {
		this.monthsAfterActivationConditionWillActivateOn = monthsAfterActivationConditionWillActivateOn;
	}
	public boolean isMinimumActivationsRequiredConditionFulfilled() {
		return minimumActivationsRequiredConditionFulfilled;
	}
	public void setMinimumActivationsRequiredConditionFulfilled(boolean minimumActivationsRequiredConditionFulfilled) {
		this.minimumActivationsRequiredConditionFulfilled = minimumActivationsRequiredConditionFulfilled;
	}
	public int getMinimumActivationsRequiredConditionActivationsLeft() {
		return minimumActivationsRequiredConditionActivationsLeft;
	}
	public void setMinimumActivationsRequiredConditionActivationsLeft(
			int minimumActivationsRequiredConditionActivationsLeft) {
		this.minimumActivationsRequiredConditionActivationsLeft = minimumActivationsRequiredConditionActivationsLeft;
	}
	public long getMonthsAfterCompletionConditionDaysLeft() {
		return monthsAfterCompletionConditionDaysLeft;
	}
	public void setMonthsAfterCompletionConditionDaysLeft(long monthsAfterCompletionConditionDaysLeft) {
		this.monthsAfterCompletionConditionDaysLeft = monthsAfterCompletionConditionDaysLeft;
	}
}
