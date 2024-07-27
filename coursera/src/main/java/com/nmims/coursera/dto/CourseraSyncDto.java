package com.nmims.coursera.dto;

import java.io.Serializable;

public class CourseraSyncDto implements Serializable{
	

	private String sapId;
	private String status;
	private String message;
	
	
	public CourseraSyncDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param sapId
	 * @param status
	 * @param message
	 */
	public CourseraSyncDto(String sapId, String status, String message) {
		super();
		this.sapId = sapId;
		this.status = status;
		this.message = message;
	}


	public String getSapId() {
		return sapId;
	}


	public void setSapId(String sapId) {
		this.sapId = sapId;
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
		return "CourseraSyncDto [sapId=" + sapId + ", status=" + status + ", message=" + message + "]";
	}

}
