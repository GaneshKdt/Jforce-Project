package com.nmims.beans;

public class MbaWxDemoExamKeysBean {
	private String 	id;
	private String subject;
	private String accessKey;
	private String link;
	private String acadYear;
	private String acadMonth;
	private String program;
	private String active;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
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
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	@Override
	public String toString() {
		return "MbaWxDemoExamKeysBean [id=" + id + ", subject=" + subject + ", accessKey=" + accessKey + ", link="
				+ link + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", program=" + program + ", active="
				+ active + "]";
	}
	
}
