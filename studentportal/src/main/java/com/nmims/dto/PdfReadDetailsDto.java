package com.nmims.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PdfReadDetailsDto implements Serializable {

	private String subject_name;
	private String content_file_name;
	private int total_pdf;
	
	public PdfReadDetailsDto() {
		super();
	}
	
	public String getSubject_name() {
		return subject_name;
	}
	
	public String getContent_file_name() {
		return content_file_name;
	}
	
	public int getTotal_pdf() {
		return total_pdf;
	}
	
	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}
	
	public void setContent_file_name(String content_file_name) {
		this.content_file_name = content_file_name;
	}
	
	public void setTotal_pdf(int total_pdf) {
		this.total_pdf = total_pdf;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PdfReadDetailsDto [subject_name=");
		builder.append(subject_name);
		builder.append(", content_file_name=");
		builder.append(content_file_name);
		builder.append(", total_pdf=");
		builder.append(total_pdf);
		builder.append("]");
		return builder.toString();
	}
	
}