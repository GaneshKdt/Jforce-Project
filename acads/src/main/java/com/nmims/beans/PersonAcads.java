package com.nmims.beans;

import java.io.Serializable;

public class PersonAcads implements Serializable{
	String firstName;
	String lastName;
	String displayName;
	String email;
	String program;
	String contactNo;
	String altContactNo;
	String lastLogon;
	String postalAddress;
	String identityConfirmed;
	String roles = "";
	String userId;
	String password;
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getAltContactNo() {
		return altContactNo;
	}
	public void setAltContactNo(String altContactNo) {
		this.altContactNo = altContactNo;
	}
	public String getIdentityConfirmed() {
		return identityConfirmed;
	}
	public void setIdentityConfirmed(String identityConfirmed) {
		this.identityConfirmed = identityConfirmed;
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public String getLastLogon() {
		return lastLogon;
	}
	public void setLastLogon(String lastLogon) {
		this.lastLogon = lastLogon;
	}
	public String getDisplayName() {
		if(displayName == null || "".equals(displayName.trim())){
			return firstName + " "+lastName;
		}
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	
	
	@Override
	public String toString() {
		String value = "First Name: "+this.firstName +
				"\n Last Name: "+this.lastName +
				"\n Display Name: "+this.displayName +
				"\n Program: "+this.program +
				"\n Email: "+this.email +
				"\n Last Logon: "+this.lastLogon +
				"\n Mobile: "+this.contactNo +
				"\n Address: "+this.postalAddress +
				"\n Roles: "+this.roles +
				"\n Identitfy Confirmed: "+this.identityConfirmed;

				
		return value;		
				
	}

}
