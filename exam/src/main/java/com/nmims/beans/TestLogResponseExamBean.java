package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename TestLogResponseBean to TestLogResponseExamBean
public class TestLogResponseExamBean implements Serializable{
	
	private String page;
	private String visitedDate;
	private String timespent;
	private String details;
	private String type;
	private String apiName;
	private String status;
	private String errorMessage;
	private String network;
	private String description;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getVisitedDate() {
		return visitedDate;
	}

	public void setVisitedDate(String visitedDate) {
		this.visitedDate = visitedDate;
	}

	public String getTimespent() {
		return timespent;
	}

	public void setTimespent(String timespent) {
		this.timespent = timespent;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "TestLogResponseBean [page=" + page + ", visitedDate=" + visitedDate + ", timespent=" + timespent
				+ ", details=" + details + ", type=" + type + ", apiName=" + apiName + ", status=" + status
				+ ", errorMessage=" + errorMessage + ", network=" + network + "]";
	}

}
