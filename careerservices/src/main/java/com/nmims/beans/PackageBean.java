package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PackageBean implements Serializable{

	private String packageId;
	private String packageFamily;
	private String salesForceUID;
	private String packageName;
	private String familyName;
	private String description;
//	private String packageDescriptionHighlights;
	private String endDate;
	private PackageRequirements packageRequirements;
	private float packageBaseCost;
	private int durationMax;
	private String numberOfFeatures;
	private String termsandconditions;
	private boolean openForSale;
	private boolean upcoming;
	
	//Enum - Slow, Normal, Fast
	private String durationType;
	private List<Feature> featuresList;
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public PackageRequirements getPackageRequirements() {
		return packageRequirements;
	}
	public void setPackageRequirements(PackageRequirements packageRequirements) {
		this.packageRequirements = packageRequirements;
	}
	public float getPackageBaseCost() {
		return packageBaseCost;
	}
	public void setPackageBaseCost(float packageBaseCost) {
		this.packageBaseCost = packageBaseCost;
	}
	public int getDurationMax() {
		return durationMax;
	}
	public void setDurationMax(int durationMax) {
		this.durationMax = durationMax;
	}
	public String getDurationType() {
		return durationType;
	}
	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}
	public List<Feature> getFeaturesList() {
		return featuresList;
	}
	public void setFeaturesList(List<Feature> featuresList) {
		this.featuresList = featuresList;
	}
	public String getSalesForceUID() {
		return salesForceUID;
	}
	public void setSalesForceUID(String salesForceUID) {
		this.salesForceUID = salesForceUID;
	}
	public String getPackageFamily() {
		return packageFamily;
	}
	public void setPackageFamily(String packageFamily) {
		this.packageFamily = packageFamily;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getNumberOfFeatures() {
		return numberOfFeatures;
	}
	public void setNumberOfFeatures(String numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}
	public String getTermsandconditions() {
		return termsandconditions;
	}
	public void setTermsandconditions(String termsandconditions) {
		this.termsandconditions = termsandconditions;
	}
	public boolean isOpenForSale() {
		return openForSale;
	}
	public void setOpenForSale(boolean openForSale) {
		this.openForSale = openForSale;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isUpcoming() {
		return upcoming;
	}
	public void setUpcoming(boolean upcoming) {
		this.upcoming = upcoming;
	}

}
