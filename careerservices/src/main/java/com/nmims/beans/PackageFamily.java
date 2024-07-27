package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PackageFamily implements Serializable {

	private String familyId;
	private String familyName;
	private String numberOfPackages;
	
	private String description;
	private String descriptionShort;
	private String keyHighlights;
	private String eligibilityCriteria;
	private String componentEligibilityCriteria;
	
	private List<PackageBean> packages;
	
	public List<PackageBean> getPackages() {
		return packages;
	}
	public void setPackages(List<PackageBean> packages) {
		this.packages = packages;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescriptionShort() {
		return descriptionShort;
	}
	public void setDescriptionShort(String descriptionShort) {
		this.descriptionShort = descriptionShort;
	}
	public String getKeyHighlights() {
		return keyHighlights;
	}
	public void setKeyHighlights(String keyHighlights) {
		this.keyHighlights = keyHighlights;
	}
	public String getEligibilityCriteria() {
		return eligibilityCriteria;
	}
	public void setEligibilityCriteria(String eligibilityCriteria) {
		this.eligibilityCriteria = eligibilityCriteria;
	}
	public String getComponentEligibilityCriteria() {
		return componentEligibilityCriteria;
	}
	public void setComponentEligibilityCriteria(String componentEligibilityCriteria) {
		this.componentEligibilityCriteria = componentEligibilityCriteria;
	}
	public String getFamilyId() {
		return familyId;
	}
	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getNumberOfPackages() {
		return numberOfPackages;
	}
	public void setNumberOfPackages(String numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}
}
