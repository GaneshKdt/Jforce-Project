package com.nmims.beans;

import java.util.Date;

public class AmazonS3Bean 
{
	private String bucketName;
	
	private String fileName;
	
	private String documentId;
	
	private String accountId;
	
	private int expireTime;
	
	private String expireDate;
	
	private String s3_fileurl;
	
	private String file_status;
	
	private int recurring_count;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getS3_fileurl() {
		return s3_fileurl;
	}

	public void setS3_fileurl(String s3_fileurl) {
		this.s3_fileurl = s3_fileurl;
	}

	public String getFile_status() {
		return file_status;
	}

	public void setFile_status(String file_status) {
		this.file_status = file_status;
	}

	public int getRecurring_count() {
		return recurring_count;
	}

	public void setRecurring_count(int recurring_count) {
		this.recurring_count = recurring_count;
	}

	@Override
	public String toString() {
		return "AmazonS3Bean [bucketName=" + bucketName + ", fileName=" + fileName + ", documentId=" + documentId
				+ ", accountId=" + accountId + ", expireTime=" + expireTime + ", expireDate=" + expireDate
				+ ", s3_fileurl=" + s3_fileurl + ", file_status=" + file_status + ", recurring_count=" + recurring_count
				+ "]";
	}

	
	
	

}
