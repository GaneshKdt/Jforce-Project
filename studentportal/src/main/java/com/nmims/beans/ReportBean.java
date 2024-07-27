package com.nmims.beans;

import java.io.Serializable;

public class ReportBean implements Serializable{
	
	private int id;
	private String reportname;
	private String link;
	private String category;
	
	public ReportBean() {
		
	}
	
	public ReportBean(int id, String reportname, String link, String category) {
		super();
		this.id = id;
		this.reportname = reportname;
		this.link = link;
		this.category = category;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReportname() {
		return reportname;
	}
	public void setReportname(String reportname) {
		this.reportname = reportname;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "ReportBean [id=" + id + ", reportname=" + reportname + ", link=" + link + ", category=" + category
				+ "]";
	}
	
	
}
