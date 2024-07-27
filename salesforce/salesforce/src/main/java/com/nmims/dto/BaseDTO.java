/**
 * 
 */
package com.nmims.dto;

/**
 * @author vil_m
 *
 */
public class BaseDTO {
	
	private String status;
	private String message;
	
	/**
	 * 
	 */
	public BaseDTO() {
		super();
		// TODO Auto-generated constructor stub
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
}
