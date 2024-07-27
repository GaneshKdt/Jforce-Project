package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * old name - FileBean
 * @author
 *
 */
public class FileStudentPortalBean implements Serializable {

	private CommonsMultipartFile fileData;
	private String year;
	private String month;
	private Integer fileId;
	private byte[] byteData;
	private String fileName;
	private String subject;
	private String description;
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public byte[] getByteData() {
		return byteData;
	}

	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public CommonsMultipartFile getFileData()
	{
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData)
	{
		this.fileData = fileData;
	}

}
