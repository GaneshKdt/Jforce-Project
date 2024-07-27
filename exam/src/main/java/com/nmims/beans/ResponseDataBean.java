package com.nmims.beans;

import java.io.Serializable;

public class ResponseDataBean implements Serializable{
	private boolean success;
	private int code;
	private String message;
	private Object data;
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ResponseDataBean [success=" + success + ", code=" + code + ", message=" + message + ", data=" + data
				+ "]";
	}
}
