package com.nmims.dto;

public class BaseDTO {
	private String status;
	private String message;

	private String sapid;
	
	/**
	 * 
	 */
	public BaseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sapid
	 */
	public BaseDTO(String sapid) {
		super();
		this.sapid = sapid;
	}

	/**
	 * @param status
	 * @param message
	 */
	public BaseDTO(String status, String message) {
		super();
		this.status = status;
		this.message = message;
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

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
}
