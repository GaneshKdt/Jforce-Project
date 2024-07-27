package com.nmims.beans;

import java.io.Serializable;
import java.util.Map;

public class AppStartupChecksModelBean  implements Serializable{

	private boolean consumerTypeCanPurchaseCS;
	private boolean hasPendingPackages;
	private boolean pendingPackagesAdded;
	private Map<String, Boolean> featureViseAccess;
	
	public boolean isHasPendingPackages() {
		return hasPendingPackages;
	}
	public void setHasPendingPackages(boolean hasPendingPackages) {
		this.hasPendingPackages = hasPendingPackages;
	}
	public Map<String, Boolean> getFeatureViseAccess() {
		return featureViseAccess;
	}
	public void setFeatureViseAccess(Map<String, Boolean> featureViseAccess) {
		this.featureViseAccess = featureViseAccess;
	}
	public boolean isPendingPackagesAdded() {
		return pendingPackagesAdded;
	}
	public void setPendingPackagesAdded(boolean pendingPackagesAdded) {
		this.pendingPackagesAdded = pendingPackagesAdded;
	}
	public boolean isConsumerTypeCanPurchaseCS() {
		return consumerTypeCanPurchaseCS;
	}
	public void setConsumerTypeCanPurchaseCS(boolean consumerTypeCanPurchaseCS) {
		this.consumerTypeCanPurchaseCS = consumerTypeCanPurchaseCS;
	}
}
