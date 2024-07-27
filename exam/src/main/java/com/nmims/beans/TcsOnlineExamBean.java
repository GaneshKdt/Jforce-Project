package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class TcsOnlineExamBean implements Serializable {

	private String examYear;
	private String examMonth;
	private String userId;
	private String password;
	private String subjectId;
	private String subject;
	private String firstName;
	private String lastName;
	private String examDate;
	private String examTime;
	private String centerId;
	private String examCenterName;
	private String uniqueRequestId;
	private String program;
	private String syncExamCenterProvider;
	private String bulkAction;
	private List<TcsOnlineExamBean> tcsOnlineExamList;
	private String lc;
	private String centerName;

	private String joinUrl;
	private String registeredEmailId;
	private String testTaken;
	private String emailBody;
	private String error;
	
	
	@Override
	public String toString() {
		return "TcsOnlineExamBean [examYear=" + examYear + ", examMonth=" + examMonth + ", userId=" + userId
				+ ", password=" + password + ", subjectId=" + subjectId + ", subject=" + subject + ", firstName="
				+ firstName + ", lastName=" + lastName + ", examDate=" + examDate + ", examTime=" + examTime
				+ ", centerId=" + centerId + ", examCenterName=" + examCenterName + ", uniqueRequestId="
				+ uniqueRequestId + ", program=" + program + ", syncExamCenterProvider=" + syncExamCenterProvider
				+ ", bulkAction=" + bulkAction + "]";
	}


	public String getLc() {
		return lc;
	}

	public void setLc(String lc) {
		this.lc = lc;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public List<TcsOnlineExamBean> getTcsOnlineExamList() {
		return tcsOnlineExamList;
	}

	public void setTcsOnlineExamList(List<TcsOnlineExamBean> tcsOnlineExamList) {
		this.tcsOnlineExamList = tcsOnlineExamList;
	}

	public String getBulkAction() {
		return bulkAction;
	}
	public void setBulkAction(String bulkAction) {
		this.bulkAction = bulkAction;
	}
	public String getSyncExamCenterProvider() {
		return syncExamCenterProvider;
	}
	public void setSyncExamCenterProvider(String syncExamCenterProvider) {
		this.syncExamCenterProvider = syncExamCenterProvider;
	}
	public String getUniqueRequestId() {
		return uniqueRequestId;
	}
	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}




	public String getJoinUrl() {
		return joinUrl;
	}




	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}




	public String getError() {
		return error;
	}




	public void setError(String error) {
		this.error = error;
	}




	public String getRegisteredEmailId() {
		return registeredEmailId;
	}




	public void setRegisteredEmailId(String registeredEmailId) {
		this.registeredEmailId = registeredEmailId;
	}




	public String getTestTaken() {
		return testTaken;
	}




	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
	}




	public String getEmailBody() {
		return emailBody;
	}




	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	
	
}
