package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class StudentPackageBean implements Serializable {
	//This class stores the required student data and the package/s he has purchased
	private String sapid;
	private StudentCareerservicesBean student;
	private PackageBean purchasedPackage;
	private Date startDate;
	private Date endDate;
	private String packageId;
	private String packageFamily;
	private String salesForceUID;
	private String packageName;
	private String familyName;
	private String description;
	private PackageRequirements packageRequirements;
	private float packageBaseCost;
	private int durationMax;
	private String numberOfFeatures;
	private boolean openForSale;
	
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getPackageFamily() {
		return packageFamily;
	}
	public void setPackageFamily(String packageFamily) {
		this.packageFamily = packageFamily;
	}
	public String getSalesForceUID() {
		return salesForceUID;
	}
	public void setSalesForceUID(String salesForceUID) {
		this.salesForceUID = salesForceUID;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getNumberOfFeatures() {
		return numberOfFeatures;
	}
	public void setNumberOfFeatures(String numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}
	public boolean isOpenForSale() {
		return openForSale;
	}
	public void setOpenForSale(boolean openForSale) {
		this.openForSale = openForSale;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public PackageBean getPurchasedPackage() {
		return purchasedPackage;
	}
	public void setPurchasedPackage(PackageBean studentPackage) {
		this.purchasedPackage = studentPackage;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public StudentCareerservicesBean getStudent() {
		return student;
	}
	public void setStudent(StudentCareerservicesBean student) {
		this.student = student;
	}
}
