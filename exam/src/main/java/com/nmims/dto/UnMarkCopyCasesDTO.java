package com.nmims.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class UnMarkCopyCasesDTO implements Serializable{

	private String sapId;
	private String year;
	private String month;
	private String subject;
    private String reason;
    private ArrayList<UnMarkCopyCasesDTO> unMarkCCList;
	
	public ArrayList<UnMarkCopyCasesDTO> getUnMarkCCList() {
		return unMarkCCList;
	}
	public void setUnMarkCCList(ArrayList<UnMarkCopyCasesDTO> unMarkCCList) {
		this.unMarkCCList = unMarkCCList;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		return "UnMarkCopyCasesDTO [sapId=" + sapId + ", year=" + year + ", month=" + month + ", subject=" + subject
				+ ", reason=" + reason + ", unMarkCCList=" + unMarkCCList + "]";
	}
}
