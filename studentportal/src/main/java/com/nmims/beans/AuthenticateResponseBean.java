package com.nmims.beans;

import java.io.Serializable;

public class AuthenticateResponseBean  implements Serializable{
	private String status;
	private StudentStudentPortalBean data;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public StudentStudentPortalBean getData() {
		return data;
	}
	public void setData(StudentStudentPortalBean data) {
		this.data = data;
	}
	
	
	
}
