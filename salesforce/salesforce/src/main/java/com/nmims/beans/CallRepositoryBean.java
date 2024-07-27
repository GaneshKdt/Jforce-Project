package com.nmims.beans;

import org.springframework.web.multipart.MultipartFile;

public class CallRepositoryBean {

	private String id;
	private String name;
	private String documentStatus;
	private MultipartFile files[];
	private String url;
	private String function;
	private String category;
	private String status;
	private String message;
	private String userId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentStatus() {
		return documentStatus;
	}
	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}
	public MultipartFile[] getFiles() {
		return files;
	}
	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "CallRepositoryBean [id=" + id + ", name=" + name + ", documentStatus=" + documentStatus + ", files="
				+ files + ", url=" + url + ", function=" + function + ", category=" + category + ", status=" + status
				+ ", message=" + message + "]";
	}

}
