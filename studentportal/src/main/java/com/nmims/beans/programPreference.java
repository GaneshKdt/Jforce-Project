package com.nmims.beans;

import java.io.Serializable;

public class programPreference extends BaseStudentPortalBean   implements Serializable  {
	
	public String studentName;
	public String mobileNo;
	public String email;
	public String regNo;
	public String preference1;
	public String preference2;
	public String preference3;
	public String preference4;
	public String preference5;
	public String preference6;
	public String preference7;
	
	public String latestProgramCategory;

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getPreference1() {
		return preference1;
	}

	public void setPreference1(String preference1) {
		this.preference1 = preference1;
	}

	public String getPreference2() {
		return preference2;
	}

	public void setPreference2(String preference2) {
		this.preference2 = preference2;
	}

	public String getPreference3() {
		return preference3;
	}

	public void setPreference3(String preference3) {
		this.preference3 = preference3;
	}

	public String getPreference4() {
		return preference4;
	}

	public void setPreference4(String preference4) {
		this.preference4 = preference4;
	}

	public String getPreference5() {
		return preference5;
	}

	public void setPreference5(String preference5) {
		this.preference5 = preference5;
	}

	public String getPreference6() {
		return preference6;
	}

	public void setPreference6(String preference6) {
		this.preference6 = preference6;
	}

	public String getPreference7() {
		return preference7;
	}

	public void setPreference7(String preference7) {
		this.preference7 = preference7;
	}

	public String getLatestProgramCategory() {
		return latestProgramCategory;
	}

	public void setLatestProgramCategory(String latestProgramCategory) {
		this.latestProgramCategory = latestProgramCategory;
	}
	
	
}
