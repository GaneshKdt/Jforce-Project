package com.nmims.beans;

import java.io.Serializable;

public class MettlResultCandidateBean implements Serializable  {
	private String email;
	private MettlResultCandidateTestStatusBean testStatus;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public MettlResultCandidateTestStatusBean getTestStatus() {
		return testStatus;
	}
	public void setTestStatus(MettlResultCandidateTestStatusBean testStatus) {
		this.testStatus = testStatus;
	}
	@Override
	public String toString() {
		return "MettlResultCandidateBean [email=" + email + ", testStatus=" + testStatus + "]";
	}
}
