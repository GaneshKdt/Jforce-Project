package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * old name - ForumBean
 * @author
 *
 */
public class ForumStudentPortalBean extends BaseStudentPortalBean  implements Serializable{
	
	private long id;
	private String parentPostId ;
	private String parentReplyId;
	
	private String year;
	private String month;
	private String subject;
	private String title;
	private String description ;
	private String isActive ;
	private String status ;
	private ArrayList<ForumStudentPortalBean> threadReplies;
	private String firstName;
	private String lastName;
	private String imageUrl;
	private String facultyId;
	private String facultyEmail;
	private String facultyFullName;
	
	private String orderBy;

	@Override
	public String toString() {
		return "ForumStudentPortalBean [id=" + id + ", parentPostId=" + parentPostId
				+ ", parentReplyId=" + parentReplyId + ", year=" + year
				+ ", month=" + month + ", subject=" + subject + ", title="
				+ title + ", description=" + description + ", isActive="
				+ isActive + ", status=" + status + ", threadReplies="
				+ threadReplies + ", firstName=" + firstName + ", lastName="
				+ lastName + ", imageUrl=" + imageUrl + ", facultyId="
				+ facultyId + ", facultyEmail=" + facultyEmail
				+ ", facultyFullName=" + facultyFullName + ", orderBy="
				+ orderBy + "]";
	}
	public String getFacultyFullName() {
		return facultyFullName;
	}
	public void setFacultyFullName(String facultyFullName) {
		this.facultyFullName = facultyFullName;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFacultyEmail() {
		return facultyEmail;
	}
	public void setFacultyEmail(String facultyEmail) {
		this.facultyEmail = facultyEmail;
	}
	public String getParentReplyId() {
		return parentReplyId;
	}
	public void setParentReplyId(String parentReplyId) {
		this.parentReplyId = parentReplyId;
	}
	
	public ArrayList<ForumStudentPortalBean> getThreadReplies() {
		return threadReplies;
	}
	public void setThreadReplies(ArrayList<ForumStudentPortalBean> threadReplies) {
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
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getParentPostId() {
		return parentPostId;
	}
	public void setParentPostId(String parentPostId) {
		this.parentPostId = parentPostId;
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
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	
}
