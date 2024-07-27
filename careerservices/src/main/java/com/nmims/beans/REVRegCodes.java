package com.nmims.beans;

import java.io.Serializable;

public class REVRegCodes implements Serializable {

	private String Months6 = "NMIMS6M";
	private String Months12 = "NMIMS12M";
	private String Months24 = "NMIMS24M";
	
	public String getMonths6() {
		return Months6;
	}
	public void setMonths6(String months6) {
		Months6 = months6;
	}
	public String getMonths12() {
		return Months12;
	}
	public void setMonths12(String months12) {
		Months12 = months12;
	}
	public String getMonths24() {
		return Months24;
	}
	public void setMonths24(String months24) {
		Months24 = months24;
	}
}
