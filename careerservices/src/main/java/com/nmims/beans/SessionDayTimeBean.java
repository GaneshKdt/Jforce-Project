package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class SessionDayTimeBean implements Serializable{
	
	private String id;
	private String date;
	private String startTime;
	private String endTime;
	
	private String startDate;
	private String endDate;
	
	private String sessionName;
//	private String year;
//	private String month;
	private String day;
	
	
	//
	private String facultyId;
	private String facultyLocation;
	private String facultyName;
	private String facultyImageURL = "Faculty/default.png";
	
	private String bookingStatus;
	private String meetingKey;
	private String joinUrl;
	private String hostUrl;
	private String hostKey;
	private String room;
	private String hostId;
	private String hostPassword;
	
	private String smsSent;
	private String  emailSent;
	
	private String isCancelled;
	private String cancellationSubject;
	private String reasonForCancellation;
	private String cancellationSmsSent;
	private String cancellationSMSBody;
	
	private String cancellationEmailSent;
	private String cancellationEmailBody;
	
	private String productId;
	private String description;
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String thumbnailUrl = "";

	private String reScheduleDate;
	private String reScheduleStartTime;
	private String reScheduleEndTime;
	
	
	private String type;

	private String packageName;
	
	private String device;
	
	private VideoContentCareerservicesBean videoContent;
	private boolean hasVideoContent;
	
	//for showing data to user
	private Date attendTime;

	private int seats;
	
	private String bypassAllChecks;
	
	private ActivationInfo activationInfo;
	

	public String getReScheduleDate() {
		return reScheduleDate;
	}
	public void setReScheduleDate(String reScheduleDate) {
		this.reScheduleDate = reScheduleDate;
	}
	public String getReScheduleStartTime() {
		return reScheduleStartTime;
	}
	public void setReScheduleStartTime(String reScheduleStartTime) {
		this.reScheduleStartTime = reScheduleStartTime;
	}
	public String getReScheduleEndTime() {
		return reScheduleEndTime;
	}
	public void setReScheduleEndTime(String reScheduleEndTime) {
		this.reScheduleEndTime = reScheduleEndTime;
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
	public String getCancellationSMSBody() {
		return cancellationSMSBody;
	}
	public void setCancellationSMSBody(String cancellationSMSBody) {
		this.cancellationSMSBody = cancellationSMSBody;
	}
	public String getCancellationEmailBody() {
		return cancellationEmailBody;
	}
	public void setCancellationEmailBody(String cancellationEmailBody) {
		this.cancellationEmailBody = cancellationEmailBody;
	}
	public String getCancellationSubject() {
		return cancellationSubject;
	}
	public void setCancellationSubject(String cancellationSubject) {
		this.cancellationSubject = cancellationSubject;
	}
	public int getSeats() {
		return seats;
	}
	public void setSeats(int seats) {
		this.seats = seats;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
//	public String getYear() {
//		return year;
//	}
//	public void setYear(String year) {
//		this.year = year;
//	}
//	public String getMonth() {
//		return month;
//	}
//	public void setMonth(String month) {
//		this.month = month;
//	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFacultyLocation() {
		return facultyLocation;
	}
	public void setFacultyLocation(String facultyLocation) {
		this.facultyLocation = facultyLocation;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
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
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostPassword() {
		return hostPassword;
	}
	public void setHostPassword(String hostPassword) {
		this.hostPassword = hostPassword;
	}
	public String getSmsSent() {
		return smsSent;
	}
	public void setSmsSent(String smsSent) {
		this.smsSent = smsSent;
	}
	public String getEmailSent() {
		return emailSent;
	}
	public void setEmailSent(String emailSent) {
		this.emailSent = emailSent;
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
	public String getCancellationSmsSent() {
		return cancellationSmsSent;
	}
	public void setCancellationSmsSent(String cancellationSmsSent) {
		this.cancellationSmsSent = cancellationSmsSent;
	}
	public String getCancellationEmailSent() {
		return cancellationEmailSent;
	}
	public void setCancellationEmailSent(String cancellationEmailSent) {
		this.cancellationEmailSent = cancellationEmailSent;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
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
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "SessionDayTimeBean [id=" + id + ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", sessionName=" + sessionName + ", year=" 
//				+ year + ", month=" 
//				+ month 
				+ ", day=" + day
				+ ", facultyId=" + facultyId + ", facultyLocation=" + facultyLocation + ", bookingStatus="
				+ bookingStatus + ", meetingKey=" + meetingKey + ", joinUrl=" + joinUrl + ", hostUrl=" + hostUrl
				+ ", hostKey=" + hostKey + ", room=" + room + ", hostId=" + hostId + ", hostPassword=" + hostPassword
				+ ", smsSent=" + smsSent + ", emailSent=" + emailSent + ", isCancelled=" + isCancelled
				+ ", reasonForCancellation=" + reasonForCancellation + ", cancellationSmsSent=" + cancellationSmsSent
				+ ", cancellationEmailSent=" + cancellationEmailSent + ", productId=" + productId + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", errorRecord="
				+ errorRecord + "]" ;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getFacultyImageURL() {
		return facultyImageURL;
	}
	public void setFacultyImageURL(String facultyImageURL) {
		if(facultyImageURL != null) {
			this.facultyImageURL = facultyImageURL;
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public Date getAttendTime() {
		return attendTime;
	}
	public void setAttendTime(Date attendTime) {
		this.attendTime = attendTime;
	}
	public VideoContentCareerservicesBean getVideoContent() {
		return videoContent;
	}
	public void setVideoContent(VideoContentCareerservicesBean videoContent) {
		this.videoContent = videoContent;
	}
	public int getseats() {
		return seats;
	}
	public void setseats(int seats) {
		this.seats = seats;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public ActivationInfo getActivationInfo() {
		return activationInfo;
	}
	public void setActivationInfo(ActivationInfo activationInfo) {
		this.activationInfo = activationInfo;
	}
	public boolean isHasVideoContent() {
		return hasVideoContent;
	}
	public void setHasVideoContent(boolean hasVideoContent) {
		this.hasVideoContent = hasVideoContent;
	}
	public String getBypassAllChecks() {
		return bypassAllChecks;
	}
	public void setBypassAllChecks(String bypassAllChecks) {
		this.bypassAllChecks = bypassAllChecks;
	}
	
	
	
	
}
