package com.nmims.beans;

public class EmailSmsStudentBean {
	
	
	private String sapid;
	private String sem;
	
	private String subjectCodeId;
	
	private String timeboundId;
	
	private String program;
	private String isPushnotification;
	private String pushContent;
	private String smsContent;
	private String notificationType;
	private String acadMonth;
	private String acadYear;
	private String prgmStructApplicable;
	private String enrollmentMonth;
	private String enrollmentYear;
	private String fromEmailId;
	private String body;
	private String Uploadtype;
	
	private String subject;
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getIsPushnotification() {
		return isPushnotification;
	}
	public void setIsPushnotification(String isPushnotification) {
		this.isPushnotification = isPushnotification;
	}
	public String getPushContent() {
		return pushContent;
	}
	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getFromEmailId() {
		return fromEmailId;
	}
	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
	public String toString() {
		return "EmailSmsStudentBean [sapid=" + sapid + ", sem=" + sem + ", subjectCodeId=" + subjectCodeId
				+ ", timeboundId=" + timeboundId + ", program=" + program + ", isPushnotification=" + isPushnotification
				+ ", pushContent=" + pushContent + ", smsContent=" + smsContent + ", notificationType="
				+ notificationType + ", acadMonth=" + acadMonth + ", acadYear=" + acadYear + ", prgmStructApplicable="
				+ prgmStructApplicable + ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear
				+ ", fromEmailId=" + fromEmailId + ", subject=" + subject + "]";
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getUploadtype() {
		return Uploadtype;
	}
	public void setUploadtype(String uploadtype) {
		Uploadtype = uploadtype;
	}
}
