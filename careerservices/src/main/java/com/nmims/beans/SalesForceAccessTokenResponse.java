package com.nmims.beans;

import java.io.Serializable;

public class SalesForceAccessTokenResponse implements Serializable {
	private String errorCode;
	private String message;
	private String errKey;
	
	private SalesForceAccessToken response;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public SalesForceAccessToken getResponse() {
		return response;
	}
	public void setResponse(SalesForceAccessToken response) {
		this.response = response;
	}
	public String getErrKey() {
		return errKey;
	}
	public void setErrKey(String errKey) {
		this.errKey = errKey;
	}
}
