package com.nmims.beans;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class StudentPhotoUploadBean {
	private CommonsMultipartFile fileData;
	private String filePath;
	private String previewPath;
	private String fileName;
	private String registrationNo;
	private String awsFilePath;
	

	public String getAwsFilePath() {
		return awsFilePath;
	}

	public void setAwsFilePath(String awsFilePath) {
		this.awsFilePath = awsFilePath;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPreviewPath() {
		return previewPath;
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	
	
}
