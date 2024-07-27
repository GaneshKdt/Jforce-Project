package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ProgressDetailsBean implements Serializable {
	private String featureId;
	private String featureName;
	private String packageId;
	private String packageName;
	private String sapid;
	private boolean activated;
	private String activationDate;
	private String entitlementId;
	private int durationMax;
	private CommonsMultipartFile fileData;
	private byte[] byteData;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String status;
	private String piStartDate;
	private String piEndDate;
	private ArrayList<ProgressDetailsBean> duration;            //to fetch the duration array list and use it for ajax based on package
	private ArrayList<ProgressDetailsBean> features;			//to fetch the features array list and use it for ajax based on package and duration 
	
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public boolean getActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	public String getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
	public String getEntitlementId() {
		return entitlementId;
	}
	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}
	public int getDurationMax() {
		return durationMax;
	}
	public void setDurationMax(int durationMax) {
		this.durationMax = durationMax;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public byte[] getByteData() {
		return byteData;
	}
	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getPiStartDate() {
		return piStartDate;
	}
	public void setPiStartDate(String piStartDate) {
		this.piStartDate = piStartDate;
	}
	public String getPiEndDate() {
		return piEndDate;
	}
	public void setPiEndDate(String piEndDate) {
		this.piEndDate = piEndDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<ProgressDetailsBean> getDuration() {
		return duration;
	}
	public void setDuration(ArrayList<ProgressDetailsBean> duration) {
		this.duration = duration;
	}
	public ArrayList<ProgressDetailsBean> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<ProgressDetailsBean> features) {
		this.features = features;
	}
	@Override
	public String toString() {
		return "ProgressDetailsBean [featureId=" + featureId + ", featureName=" + featureName + ", packageId="
				+ packageId + ", packageName=" + packageName + ", sapid=" + sapid + ", activated=" + activated
				+ ", activationDate=" + activationDate + ", entitlementId=" + entitlementId + ", durationMax="
				+ durationMax + ", fileData=" + fileData + ", byteData=" + Arrays.toString(byteData) + ", errorMessage="
				+ errorMessage + ", errorRecord=" + errorRecord + ", status=" + status + ", piStartDate=" + piStartDate
				+ ", piEndDate=" + piEndDate + ", duration=" + duration + ", features=" + features + "]";
	}
	
}
