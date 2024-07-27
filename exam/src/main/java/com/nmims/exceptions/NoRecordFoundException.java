package com.nmims.exceptions;

public class NoRecordFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3307493631115211301L;

	public NoRecordFoundException() {
		super();
	}
	
	public NoRecordFoundException(String message) {
		super(message);
	}
	
}
