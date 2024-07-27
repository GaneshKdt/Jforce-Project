package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class InterviewBean implements Serializable{
	
	private CommonsMultipartFile fileData;
	private String id; 
	private String interviewId; 
	private String facultyId;
	private String facultyName;
	private byte[] byteData;
	private String fileName;
	private String date;
	private String startTime;
	private String endTime;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private String sapid;
	private String featureId;
	private String featureName;
	private String packageId;
	private String packageName;
	private String entitlementId;
	private String attended;
	private String feedback;
	private String startDate;        // package start date 
	private String endDate;          // package end date 
	private String hostId;
	private String hostPassword;
	private String meetingKey;
	private String joinUrl;
	private String hostUrl;
	private String bookingStatus;
	private String hostKey;
	private String room;
	private String isCancelled;
	private String isReschedule;
	private String reasonForCancellation;
	private String status;
	private String activationDate;
	private int totalActivations;
	private int activated;
	private int activationsLeft;
	private boolean piActive = false;
	private boolean errorRecord = false;
	private String joinTime;
	private String joinWindowEndTime;
	private String successMessage;
	
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInterviewId() {
		return interviewId;
	}
	public void setInterviewId(String interviewId) {
		this.interviewId = interviewId;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public byte[] getByteData() {
		return byteData;
	}
	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getEntitlementId() {
		return entitlementId;
	}
	public String getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}
	public int getTotalActivations() {
		return totalActivations;
	}
	public void setTotalActivations(int totalActivations) {
		this.totalActivations = totalActivations;
	}
	public int getActivated() {
		return activated;
	}
	public void setActivated(int activated) {
		this.activated = activated;
	}
	public int getActivationsLeft() {
		return activationsLeft;
	}
	public void setActivationsLeft(int activationsLeft) {
		this.activationsLeft = activationsLeft;
	}
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getAttended() {
		return attended;
	}
	public void setAttended(String attended) {
		this.attended = attended;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getMeetingKey() {
		return meetingKey;
	}
	public void setMeetingKey(String meetingKey) {
		this.meetingKey = meetingKey;
	}
	public String getJoinUrl() {
		return joinUrl;
	}
	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getHostPassword() {
		return hostPassword;
	}
	public void setHostPassword(String hostPassword) {
		this.hostPassword = hostPassword;
	}
	public String getHostKey() {
		return hostKey;
	}
	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getIsCancelled() {
		return isCancelled;
	}
	public void setIsCancelled(String isCancelled) {
		this.isCancelled = isCancelled;
	}
	public String getReasonForCancellation() {
		return reasonForCancellation;
	}
	public void setReasonForCancellation(String reasonForCancellation) {
		this.reasonForCancellation = reasonForCancellation;
	}
	public String getIsReschedule() {
		return isReschedule;
	}
	public void setIsReschedule(String isReschedule) {
		this.isReschedule = isReschedule;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isPiActive() {
		return piActive;
	}
	public void setPiActive(boolean piActive) {
		this.piActive = piActive;
	}
	public String getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}
	public String getJoinWindowEndTime() {
		return joinWindowEndTime;
	}
	public void setJoinWindowEndTime(String joinWindowEndTime) {
		this.joinWindowEndTime = joinWindowEndTime;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	@Override
	public String toString() {
		return "InterviewBean [sapid=" + sapid + ", featureId=" + featureId + ", featureName=" + featureName
				+ ", packageName=" + packageName + ", entitlementId=" + entitlementId + ", totalActivations="
				+ totalActivations + ", activationsLeft=" + activationsLeft + "]";
	}
}
