package com.nmims.beans;

import java.io.Serializable;

public class UpgradePathFamily implements Serializable {

	private String uid;
	private String pathId;
	private String pathName;
	private String packageFamilyName;
	private String packageFamilyId;
	private int levelValue;
	private int minLevelToPurchase;
	private int maxLevelToPurchase;
	private int validityAfterEndDate;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPathId() {
		return pathId;
	}
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}
	public String getPackageFamilyId() {
		return packageFamilyId;
	}
	public void setPackageFamilyId(String packageFamilyId) {
		this.packageFamilyId = packageFamilyId;
	}
	public int getLevelValue() {
		return levelValue;
	}
	public void setLevelValue(int levelValue) {
		this.levelValue = levelValue;
	}
	public int getMinLevelToPurchase() {
		return minLevelToPurchase;
	}
	public void setMinLevelToPurchase(int minLevelToPurchase) {
		this.minLevelToPurchase = minLevelToPurchase;
	}
	public int getMaxLevelToPurchase() {
		return maxLevelToPurchase;
	}
	public void setMaxLevelToPurchase(int maxLevelToPurchase) {
		this.maxLevelToPurchase = maxLevelToPurchase;
	}
	public int getValidityAfterEndDate() {
		return validityAfterEndDate;
	}
	public void setValidityAfterEndDate(int validityAfterEndDate) {
		this.validityAfterEndDate = validityAfterEndDate;
	}
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public String getPackageFamilyName() {
		return packageFamilyName;
	}
	public void setPackageFamilyName(String packageFamilyName) {
		this.packageFamilyName = packageFamilyName;
	}
}
