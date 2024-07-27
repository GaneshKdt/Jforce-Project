package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CSHomePackageInfo  implements Serializable{

	private String familyId;
	private String packageName;
	private String description;
	private Date validTo;
	private Date validFrom;
	private String aboutPackagePage;
	private boolean upgradeAvailable;
	private String upgradeUrl;

	private List<CSHomeEntitlementInfo> entitlementsInfo;
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	public boolean isUpgradeAvailable() {
		return upgradeAvailable;
	}
	public void setUpgradeAvailable(boolean upgradeAvailable) {
		this.upgradeAvailable = upgradeAvailable;
	}
	public String getUpgradeUrl() {
		return upgradeUrl;
	}
	public void setUpgradeUrl(String upgradeUrl) {
		this.upgradeUrl = upgradeUrl;
	}
	public List<CSHomeEntitlementInfo> getEntitlementsInfo() {
		return entitlementsInfo;
	}
	public void setEntitlementsInfo(List<CSHomeEntitlementInfo> entitlementsInfo) {
		this.entitlementsInfo = entitlementsInfo;
	}
	public String getFamilyId() {
		return familyId;
	}
	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public String getAboutPackagePage() {
		return aboutPackagePage;
	}
	public void setAboutPackagePage(String aboutPackagePage) {
		this.aboutPackagePage = aboutPackagePage;
	}
	@Override
	public String toString() {
		return "CSHomePackageInfo [familyId=" + familyId + ", packageName=" + packageName + ", description="
				+ description + ", validTo=" + validTo + ", validFrom=" + validFrom + ", aboutPackagePage="
				+ aboutPackagePage + ", upgradeAvailable=" + upgradeAvailable + ", upgradeUrl=" + upgradeUrl
				+ ", entitlementsInfo=" + entitlementsInfo + "]";
	}
	
}
