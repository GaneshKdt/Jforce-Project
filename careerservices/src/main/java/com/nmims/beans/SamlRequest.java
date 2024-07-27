package com.nmims.beans;

import java.io.Serializable;

public class SamlRequest implements Serializable {

	private String firstName;
	private String lastName;
	private String userId;
	private String emailId;
	private String interactionSource = "NMIMS";
	private String commonName;
	private String Reg_Code;
	
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getInteractionSource() {
		return interactionSource;
	}
	public void setInteractionSource(String interactionSource) {
		this.interactionSource = interactionSource;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getReg_Code() {
		return Reg_Code;
	}
	public void setReg_Code6Month() {
		Reg_Code = (new REVRegCodes()).getMonths6();
	}
	public void setReg_Code12Month() {
		Reg_Code = (new REVRegCodes()).getMonths12();
	}
	public void setReg_Code24Month() {
		Reg_Code = (new REVRegCodes()).getMonths24();
	}
}
