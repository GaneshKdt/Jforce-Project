package com.nmims.beans;

/**
 * @author shivam.sangale.EXT
 *
 */
public class VerifyContactDetailsBean {
	private String firstName;
	private String subType;
	private String emailId;
	private String mobile;
	private String otp;
	private String message;
	private String sapid;
	private String createdDate;
	private String Srid;
	private boolean status;

	
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getSrid() {
		return Srid;
	}
	public void setSrid(String srid) {
		Srid = srid;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	@Override
	public String toString() {
		return "VerifyContactDetailsBean [firstName=" + firstName + ", subType=" + subType + ", emailId=" + emailId
				+ ", mobile=" + mobile + ", otp=" + otp + ", message=" + message + ", sapid=" + sapid + ", createdDate="
				+ createdDate + ", Srid=" + Srid + "]";
	}
	

}
