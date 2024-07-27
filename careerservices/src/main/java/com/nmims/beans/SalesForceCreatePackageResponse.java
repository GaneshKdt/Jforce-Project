package com.nmims.beans;

import java.io.Serializable;

public class SalesForceCreatePackageResponse implements Serializable {


	private String errorCode;
	private String message;
	private String errKey;
	private SalesForceCreatePackageResponseObject response;

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

	public String getErrKey() {
		return errKey;
	}

	public void setErrKey(String errKey) {
		this.errKey = errKey;
	}

	public SalesForceCreatePackageResponseObject getResponse() {
		return response;
	}

	public void setResponse(SalesForceCreatePackageResponseObject response) {
		this.response = response;
	}
}
