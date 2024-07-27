package com.nmims.beans;

import java.util.ArrayList;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class BodBean {
	
	private String createdBy;
	private String lastModifiedBy;
	private CommonsMultipartFile fileData;
	private String examYear;
	private String examMonth;
	private ArrayList<String> questionIdList;
	
	public ArrayList<String> getQuestionIdList() {
		return questionIdList;
	}
	public void setQuestionIdList(ArrayList<String> questionIdList) {
		this.questionIdList = questionIdList;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	@Override
	public String toString() {
		return "BodBean [createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", questionIdList=" + questionIdList + "]";
	}
	
	
}
