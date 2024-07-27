package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class DemoExamBean  extends BaseExamBean implements Serializable {

	private int id;
	private String subject;
	private String key;
	private String link;
	private String lastmodified_by;
	private CommonsMultipartFile fileData;
	private String subject_code;
	private String count;
	private String status;
	private String message;
	private String attemptStatus;
	private String latestAttemptDateTime;
	private String total;
    private String centerCode;
    private String mobile;
    private String lastName;
    private String firstName;
    private String emailId;
    
	 
	

	
	
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSubject_code() {
		return subject_code;
	}
	public void setSubject_code(String subject_code) {
		this.subject_code = subject_code;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getLastmodified_by() {
		return lastmodified_by;
	}
	public void setLastmodified_by(String lastmodified_by) {
		this.lastmodified_by = lastmodified_by;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}
	public String getLatestAttemptDateTime() {
		return latestAttemptDateTime;
	}
	public void setLatestAttemptDateTime(String latestAttemptDateTime) {
		this.latestAttemptDateTime = latestAttemptDateTime;
	}
	
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	@Override
	public String toString() {
		return "DemoExamBean [id=" + id + ", subject=" + subject + ", key=" + key + ", link=" + link
				+ ", lastmodified_by=" + lastmodified_by + ", fileData=" + fileData + ", subject_code=" + subject_code
				+ ", count=" + count + ", status=" + status + ", message=" + message + ", attemptStatus="
				+ attemptStatus + ", latestAttemptDateTime=" + latestAttemptDateTime + ", total=" + total
				+ ", centerCode=" + centerCode + ", mobile=" + mobile + ", lastName=" + lastName + ", firstName="
				+ firstName + ", emailId=" + emailId + "]";
	}
	
	
	
	
	
}
