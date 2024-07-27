package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MailBean  implements Serializable  {
	
	public long id;
	public String subject;
	public String sapid;
	public String mailId;
	public String mailTemplateId;
	public String filterCriteria;
	public String body;
	public String fromEmailId;
	public List<String> sapIdRecipients;
	public List<String> mailIdRecipients;
	public String createdBy;
	public String createdDate;
	public String lastModifiedBy;
	public String lastModifiedDate;
	
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getMailId() {
		return mailId;
	}
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	public String getMailTemplateId() {
		return mailTemplateId;
	}
	public void setMailTemplateId(String mailTemplateId) {
		this.mailTemplateId = mailTemplateId;
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
	public List<String> getSapIdRecipients() {
		return sapIdRecipients;
	}
	public void setSapIdRecipients(List<String> sapIdRecipients) {
		this.sapIdRecipients = sapIdRecipients;
	}
	public List<String> getMailIdRecipients() {
		return mailIdRecipients;
	}
	public void setMailIdRecipients(List<String> mailIdRecipients) {
		this.mailIdRecipients = mailIdRecipients;
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
	
}
