package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class ForumAcadsBean extends BaseAcadsBean  implements Serializable{
	
	
	/**
	 * Change Name from ForumBean to ForumAcadsBean for serializable issue
	 */
	private String parentPostId ;
	private String parentReplyId;
	private String year;
	private String month;
	private String subject;
	private String title;
	private String description ;
	private String isActive ;
	
	private String status ;
	private String firstName;
	private String lastName;
	@Override
	public String toString() {
		return "ForumBean [parentPostId=" + parentPostId + ", parentReplyId="
				+ parentReplyId + ", year=" + year + ", month=" + month
				+ ", subject=" + subject + ", title=" + title
				+ ", description=" + description + ", isActive=" + isActive
				+ ", status=" + status + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", threadReplies=" + threadReplies
				+ ", facultyId=" + facultyId + ", imageUrl=" + imageUrl
				+ ", orderBy=" + orderBy + "]";
	}
	private ArrayList<ForumAcadsBean> threadReplies;
	private String facultyId;
	private String imageUrl;
	
	private String orderBy;
	
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	

	public ArrayList<ForumAcadsBean> getThreadReplies() {
		return threadReplies;
	}
	public void setThreadReplies(ArrayList<ForumAcadsBean> threadReplies) {
		this.threadReplies = threadReplies;
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
	
	public String getParentPostId() {
		return parentPostId;
	}
	public void setParentPostId(String parentPostId) {
		this.parentPostId = parentPostId;
	}
	public String getParentReplyId() {
		return parentReplyId;
	}
	public void setParentReplyId(String parentReplyId) {
		this.parentReplyId = parentReplyId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	
	
}
