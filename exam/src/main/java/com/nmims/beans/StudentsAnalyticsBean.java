package com.nmims.beans;

import java.io.Serializable;

public class StudentsAnalyticsBean  implements Serializable  {
	private String sapid;
	private String assignmentScore;
	private String studentName;
	private String subject;
	private String assignmentAttempts;
	private String teeAttempts;
	private String total;
	private String failReason;
	private String isPass;
	private String enrollmentMonth;
	private String enrollmentYear;
	private String emailId;
	private String mobile;
	private String isLateral;
	private String lc;
	private String centerName;
	private String programCleared;
	private String validityEndMonth;
	private String validityEndYear;
	private String currentSem;
	private String state;
	private String city;
	private String clearedSem1;
	private String clearedSem2;
	private String clearedSem3;
	private String clearedSem4;
	private String subjectsCleared;
	private String currentSubjectsApplicable;
	private String applicableSubjects;
	private String remarks;	

	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
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
	public String getProgramCleared() {
		return programCleared;
	}
	public void setProgramCleared(String programCleared) {
		this.programCleared = programCleared;
	}
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getCurrentSem() {
		return currentSem;
	}
	public void setCurrentSem(String currentSem) {
		this.currentSem = currentSem;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getClearedSem1() {
		return clearedSem1;
	}
	public void setClearedSem1(String clearedSem1) {
		this.clearedSem1 = clearedSem1;
	}
	public String getClearedSem2() {
		return clearedSem2;
	}
	public void setClearedSem2(String clearedSem2) {
		this.clearedSem2 = clearedSem2;
	}
	public String getClearedSem3() {
		return clearedSem3;
	}
	public void setClearedSem3(String clearedSem3) {
		this.clearedSem3 = clearedSem3;
	}
	public String getClearedSem4() {
		return clearedSem4;
	}
	public void setClearedSem4(String clearedSem4) {
		this.clearedSem4 = clearedSem4;
	}
	public String getSubjectsCleared() {
		return subjectsCleared;
	}
	public void setSubjectsCleared(String subjectsCleared) {
		this.subjectsCleared = subjectsCleared;
	}
	public String getCurrentSubjectsApplicable() {
		return currentSubjectsApplicable;
	}
	public void setCurrentSubjectsApplicable(String currentSubjectsApplicable) {
		this.currentSubjectsApplicable = currentSubjectsApplicable;
	}
	public String getApplicableSubjects() {
		return applicableSubjects;
	}
	public void setApplicableSubjects(String applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getAssignmentScore() {
		return assignmentScore;
	}
	public void setAssignmentScore(String assignmentScore) {
		this.assignmentScore = assignmentScore;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAssignmentAttempts() {
		return assignmentAttempts;
	}
	public void setAssignmentAttempts(String assignmentAttempts) {
		this.assignmentAttempts = assignmentAttempts;
	}
	public String getTeeAttempts() {
		return teeAttempts;
	}
	public void setTeeAttempts(String teeAttempts) {
		this.teeAttempts = teeAttempts;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	@Override
	public String toString() {
		return "StudentsAnalyticsBean [sapid=" + sapid + ", assignmentScore=" + assignmentScore + ", studentName="
				+ studentName + ", subject=" + subject + ", assignmentAttempts=" + assignmentAttempts + ", teeAttempts="
				+ teeAttempts + ", total=" + total + ", failReason=" + failReason + ", isPass=" + isPass
				+ ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear + ", emailId=" + emailId
				+ ", mobile=" + mobile + ", isLateral=" + isLateral + ", lc=" + lc + ", centerName=" + centerName
				+ ", programCleared=" + programCleared + ", validityEndMonth=" + validityEndMonth + ", validityEndYear="
				+ validityEndYear + ", currentSem=" + currentSem + ", state=" + state + ", city=" + city
				+ ", clearedSem1=" + clearedSem1 + ", clearedSem2=" + clearedSem2 + ", clearedSem3=" + clearedSem3
				+ ", clearedSem4=" + clearedSem4 + ", subjectsCleared=" + subjectsCleared
				+ ", currentSubjectsApplicable=" + currentSubjectsApplicable + ", applicableSubjects="
				+ applicableSubjects + ", remarks=" + remarks + "]";
	}

}
