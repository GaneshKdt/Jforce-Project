package com.nmims.beans;

import java.io.Serializable;

public class IdCardExamBean implements Serializable {
	private String sapid;
	private String fileName;
	private String status;
	private String message;
	
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "IdCardExamBean [sapid=" + sapid + ", fileName=" + fileName + ", status=" + status + ", message="
				+ message + "]";
	}
}
