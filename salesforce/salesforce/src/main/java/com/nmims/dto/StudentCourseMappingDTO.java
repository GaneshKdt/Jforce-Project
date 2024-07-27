package com.nmims.dto;

import java.util.ArrayList;

public class StudentCourseMappingDTO 
{
	private String sapId;
	private String electiveSubjectType;
	private String sem;
	private String regMonth;
	private String regYear;
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getElectiveSubjectType() {
		return electiveSubjectType;
	}
	public void setElectiveSubjectType(String electiveSubjectType) {
		this.electiveSubjectType = electiveSubjectType;
	}
	@Override
	public String toString() {
		return "StudentCourseMappingDTO [sapId=" + sapId + ", electiveSubjectType=" + electiveSubjectType + ", sem="
				+ sem + "]";
	}
	public String getRegMonth() {
		return regMonth;
	}
	public void setRegMonth(String regMonth) {
		this.regMonth = regMonth;
	}
	public String getRegYear() {
		return regYear;
	}
	public void setRegYear(String regYear) {
		this.regYear = regYear;
	}
}
