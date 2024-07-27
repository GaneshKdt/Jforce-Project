package com.nmims.beans;

import java.io.Serializable;

public class FeedPostsBean  implements Serializable {
	private String acadYear;
	private String acadMonth;
	private String firstName;
	private String lastName;
	private String sapid;
	private String postDescription;
	private String postURL;
	private String comment;
	private String commentedOn;
	private String program;
	private String term;
	private String subject;
	private String sessionPlan;
	private String facultyName;
	private String ic;
	private String name;
	private String lcName;
	private String postedOn;
	private String noOfComments;
	private String postType;
	private String batch;
	private String post_id;
	private String commentPostedOn;
	private String postPostedOn;
	
	
	

	public String getCommentPostedOn() {
		return commentPostedOn;
	}
	public void setCommentPostedOn(String commentPostedOn) {
		this.commentPostedOn = commentPostedOn;
	}
	public String getPostPostedOn() {
		return postPostedOn;
	}
	public void setPostPostedOn(String postPostedOn) {
		this.postPostedOn = postPostedOn;
	}
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public String getNoOfComments() {
		return noOfComments;
	}
	public void setNoOfComments(String noOfComments) {
		this.noOfComments = noOfComments;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPostDescription() {
		return postDescription;
	}
	public void setPostDescription(String postDescription) {
		this.postDescription = postDescription;
	}
	public String getPostURL() {
		return postURL;
	}
	public void setPostURL(String postURL) {
		this.postURL = postURL;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCommentedOn() {
		return commentedOn;
	}
	public void setCommentedOn(String commentedOn) {
		this.commentedOn = commentedOn;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSessionPlan() {
		return sessionPlan;
	}
	public void setSessionPlan(String sessionPlan) {
		this.sessionPlan = sessionPlan;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLcName() {
		return lcName;
	}
	public void setLcName(String lcName) {
		this.lcName = lcName;
	}
	public String getPostedOn() {
		return postedOn;
	}
	public void setPostedOn(String postedOn) {
		this.postedOn = postedOn;
	}
	@Override
	public String toString() {
		return "FeedPostsBean [acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", sapid=" + sapid + ", postDescription=" + postDescription + ", postURL="
				+ postURL + ", comment=" + comment + ", commentedOn=" + commentedOn + ", program=" + program + ", term="
				+ term + ", subject=" + subject + ", sessionPlan=" + sessionPlan + ", facultyName=" + facultyName
				+ ", ic=" + ic + ", name=" + name + ", lcName=" + lcName + ", postedOn=" + postedOn + "]";
	}
	

}
