package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * old name - CaseStudyBean
 * @author
 *
 */
public class CaseStudyStudentPortalBean implements Serializable {
	private int id;
	private String topic;
	private String status;
	private String lastModifiedDate;
	private String lastModifiedBy;
	private String month;
	private String year;
	private List<CaseStudyStudentPortalBean> caseStudyFiles;
	private CommonsMultipartFile fileData;
	private String filePath;
	private String studentFilePath;
	private String questionFilePreviewPath;
	private String startDate;
	private String endDate;
	
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	
	public List<CaseStudyStudentPortalBean> getCaseStudyFiles() {
		return caseStudyFiles;
	}

	public void setCaseStudyFiles(List<CaseStudyStudentPortalBean> caseStudyFiles) {
		this.caseStudyFiles = caseStudyFiles;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}

	public String getStudentFilePath() {
		return studentFilePath;
	}
	public void setStudentFilePath(String studentFilePath) {
		this.studentFilePath = studentFilePath;
	}
	public String getQuestionFilePreviewPath() {
		return questionFilePreviewPath;
	}
	public void setQuestionFilePreviewPath(String questionFilePreviewPath) {
		this.questionFilePreviewPath = questionFilePreviewPath;
	}
	@Override
	public String toString() {
		return "CaseStudyStudentPortalBean [id=" + id + ", topic=" + topic + ", status="
				+ status + ", lastModifiedDate=" + lastModifiedDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", month=" + month
				+ ", year=" + year + ", caseStudyFiles=" + caseStudyFiles
				+ ", fileData=" + fileData + ", filePath=" + filePath
				+ ", studentFilePath=" + studentFilePath
				+ ", questionFilePreviewPath=" + questionFilePreviewPath + "]";
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}



}
