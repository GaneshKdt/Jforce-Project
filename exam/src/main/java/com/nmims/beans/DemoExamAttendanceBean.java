package com.nmims.beans;

import java.io.Serializable;

public class DemoExamAttendanceBean  implements Serializable {

	private String sapid;
	private int demoExamId;
	private String accessKey;
	private String startedTime;
	private String endTime;
	private String markAttend;
	private String created_at;
	private String updated_at;
	private String firstName;
	private String lastName;
	private String subject;
	private String total;
	private String mobile;
	private String emailId;
	private String status;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public int getDemoExamId() {
		return demoExamId;
	}
	public void setDemoExamId(int demoExamId) {
		this.demoExamId = demoExamId;
	}
	public String getStartedTime() {
		return startedTime;
	}
	public void setStartedTime(String startedTime) {
		this.startedTime = startedTime;
	}
	
	
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getMarkAttend() {
		return markAttend;
	}
	public void setMarkAttend(String markAttend) {
		this.markAttend = markAttend;
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	@Override
	public String toString() {
		return "DemoExamAttendanceBean [sapid=" + sapid + ", demoExamId=" + demoExamId + ", accessKey=" + accessKey
				+ ", startedTime=" + startedTime + ", endTime=" + endTime + ", markAttend=" + markAttend
				+ ", created_at=" + created_at + ", updated_at=" + updated_at + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", subject=" + subject + ", total=" + total + ", mobile=" + mobile
				+ ", emailId=" + emailId + ", status=" + status + "]";
	}
	
	
}
