package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PacakageAvailabilityBean  implements Serializable{
	
	private boolean available;
	private boolean purchased;
	private boolean upcoming;
	
	private String durationType;
	private String price;
	private String packageId;
	private String descriptionShort;
	private String description;
	private String salesForceUID;
	private String familyId;
	private String familyName;
	private String keyHighlights;
	private String eligibilityCriteria;
	private String componentEligibilityCriteria;
	private String pdfURL;
	private List<String> availableFeatures;
	
	//used for mobile to open this package details in webview
	private String viewDetailsURL;
	
	public String getViewDetailsURL() {
		return viewDetailsURL;
	}
	public void setViewDetailsURL(String viewDetailsURL) {
		this.viewDetailsURL = viewDetailsURL;
	}
	public String getDurationType() {
		return durationType;
	}
	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
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
	public String getSalesForceUID() {
		return salesForceUID;
	}
	public void setSalesForceUID(String salesForceUID) {
		this.salesForceUID = salesForceUID;
	}
	public String getDescriptionShort() {
		return descriptionShort;
	}
	public void setDescriptionShort(String descriptionShort) {
		this.descriptionShort = descriptionShort;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public boolean isPurchased() {
		return purchased;
	}
	public void setPurchased(boolean purchased) {
		this.purchased = purchased;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getAvailableFeatures() {
		return availableFeatures;
	}
	public void setAvailableFeatures(List<String> availableFeatures) {
		this.availableFeatures = availableFeatures;
	}
	public String getPdfURL() {
		return pdfURL;
	}
	public void setPdfURL(String pdfURL) {
		this.pdfURL = pdfURL;
	}
	public boolean isUpcoming() {
		return upcoming;
	}
	public void setUpcoming(boolean upcoming) {
		this.upcoming = upcoming;
	}
	@Override
	public String toString() {
		return "PacakageAvailabilityBean [available=" + available + ", purchased=" + purchased + ", upcoming="
				+ upcoming + ", durationType=" + durationType + ", salesForceUID=" + salesForceUID + "]";
	}
	
}
