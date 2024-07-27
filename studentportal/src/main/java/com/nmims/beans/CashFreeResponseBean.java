package com.nmims.beans;

import java.io.Serializable;

public class CashFreeResponseBean implements Serializable{

	private String type;
	private String token;
	private String status;
	private String message;
	public String getType() {
		return type;
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
	public void setType(String type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		return "CashFreeResponseBean [type=" + type + ", token=" + token + ", status=" + status + ", message=" + message
				+ "]";
	}
}
