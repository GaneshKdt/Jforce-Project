package com.nmims.dto;

import java.util.List;

import com.nmims.beans.StudentStudentPortalBean;

public class MailDto {
	
	public long id;
	public String subject;
	public String createdDate;
	public String filterCriteria;
	public String body;
	public String fromEmailId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getFilterCriteria() {
		return filterCriteria;
	}
	public void setFilterCriteria(String filterCriteria) {
		this.filterCriteria = filterCriteria;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFromEmailId() {
		return fromEmailId;
	}
	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}
	
	
	



}
