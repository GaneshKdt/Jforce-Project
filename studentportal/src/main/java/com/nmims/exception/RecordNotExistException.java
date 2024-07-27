package com.nmims.exception;
import org.springframework.http.ResponseEntity;

public class RecordNotExistException extends RuntimeException{
	
	// Custom error message
	private String message;
	
	// Custom error code representing an error in system
	private String errorCode;
	 
	public RecordNotExistException(String message) {
		super(message);
		this.message = message;
	}
	
	public RecordNotExistException(String message, String errorCode) {
		super(message);
		this.message = message;
		this.errorCode = errorCode;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	 
}
