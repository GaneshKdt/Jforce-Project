package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class EntitlementAccessFields  implements Serializable{
	
/*
 * 	if the user has access to thie entitlement at all.
 */
	private boolean userHasAccess;

/*
 * 	if this entitlement has expired.
 */
	private boolean entitlementValidityExpired;

/*
 * 	if the dependencies check of this package was successful.
 */
	private boolean dependenciesFulfilled;
	/*
	 * 	list of dependencies yet to be fulfilled. 
	 * 	basically a list of featureIds that will be scanned and the appropriate result will be returned depending on the restricting constraint
	 */
		private List<DependencyStatus> dependenciesStatusList;


/*
 * 	If everything above is positive, the check for this constraint starts
 * 	If Required is true and Completed is false the user gets the option to activate this feature.
 */
	private boolean manualActivationComplete;

/*
 * 	extra fields. if activationsLeft is 0 then student can use the static data in the entitlement but not "activate" it
 */
	private int activationsLeft;
	private int totalActivations;
	
/*
 * 	also return the students entitlement relationship information
 */
	private StudentEntitlement studentEntitlementInfo;
	
	public boolean isUserHasAccess() {
		return userHasAccess;
	}
	public void setUserHasAccess(boolean userHasAccess) {
		this.userHasAccess = userHasAccess;
	}
	public boolean isEntitlementValidityExpired() {
		return entitlementValidityExpired;
	}
	public void setEntitlementValidityExpired(boolean entitlementValidityExpired) {
		this.entitlementValidityExpired = entitlementValidityExpired;
	}
	public boolean isDependenciesFulfilled() {
		return dependenciesFulfilled;
	}
	public void setDependenciesFulfilled(boolean dependenciesFulfilled) {
		this.dependenciesFulfilled = dependenciesFulfilled;
	}
	public List<DependencyStatus> getDependenciesStatusList() {
		return dependenciesStatusList;
	}
	public void setDependenciesStatusList(List<DependencyStatus> dependenciesStatusList) {
		this.dependenciesStatusList = dependenciesStatusList;
	}
	public boolean isManualActivationComplete() {
		return manualActivationComplete;
	}
	public void setManualActivationComplete(boolean manualActivationComplete) {
		this.manualActivationComplete = manualActivationComplete;
	}
	public int getActivationsLeft() {
		return activationsLeft;
	}
	public void setActivationsLeft(int activationsLeft) {
		this.activationsLeft = activationsLeft;
	}
	public int getTotalActivations() {
		return totalActivations;
	}
	public void setTotalActivations(int totalActivations) {
		this.totalActivations = totalActivations;
	}
	public StudentEntitlement getStudentEntitlementInfo() {
		return studentEntitlementInfo;
	}
	public void setStudentEntitlementInfo(StudentEntitlement studentEntitlementInfo) {
		this.studentEntitlementInfo = studentEntitlementInfo;
	}
}
