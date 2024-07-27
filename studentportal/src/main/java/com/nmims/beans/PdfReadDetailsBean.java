package com.nmims.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PdfReadDetailsBean implements Serializable {

	private long id;
	private String sapid;
	private String pss_id;
	private String subject_name;
	private int content_file_id;
	private String content_file_name;
	private int total_page;
	private int read_page;
	private String time_spent;
	private String current_acads_month;
	private int current_acads_year;
	
	public PdfReadDetailsBean() {
		super();
	}

	public long getId() {
		return id;
	}

	public String getSapid() {
		return sapid;
	}

	public String getPss_id() {
		return pss_id;
	}

	public String getSubject_name() {
		return subject_name;
	}

	public int getContent_file_id() {
		return content_file_id;
	}

	public String getContent_file_name() {
		return content_file_name;
	}

	public int getTotal_page() {
		return total_page;
	}

	public int getRead_page() {
		return read_page;
	}
	
	public String getTime_spent() {
		return time_spent;
	}

	public String getCurrent_acads_month() {
		return current_acads_month;
	}

	public int getCurrent_acads_year() {
		return current_acads_year;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public void setPss_id(String pss_id) {
		this.pss_id = pss_id;
	}

	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}

	public void setContent_file_id(int content_file_id) {
		this.content_file_id = content_file_id;
	}

	public void setContent_file_name(String content_file_name) {
		this.content_file_name = content_file_name;
	}

	public void setTotal_page(int total_page) {
		this.total_page = total_page;
	}

	public void setRead_page(int read_page) {
		this.read_page = read_page;
	}
	
	public void setTime_spent(String time_spent) {
		this.time_spent = time_spent;
	}

	public void setCurrent_acads_month(String current_acads_month) {
		this.current_acads_month = current_acads_month;
	}

	public void setCurrent_acads_year(int current_acads_year) {
		this.current_acads_year = current_acads_year;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PdfReadDetailsBean [id=");
		builder.append(id);
		builder.append(", sapid=");
		builder.append(sapid);
		builder.append(", pss_id=");
		builder.append(pss_id);
		builder.append(", subject_name=");
		builder.append(subject_name);
		builder.append(", content_file_id=");
		builder.append(content_file_id);
		builder.append(", content_file_name=");
		builder.append(content_file_name);
		builder.append(", total_page=");
		builder.append(total_page);
		builder.append(", read_page=");
		builder.append(read_page);
		builder.append(", time_spent=");
		builder.append(time_spent);
		builder.append(", current_acads_month=");
		builder.append(current_acads_month);
		builder.append(", current_acads_year=");
		builder.append(current_acads_year);
		builder.append("]");
		return builder.toString();
	}
	
}