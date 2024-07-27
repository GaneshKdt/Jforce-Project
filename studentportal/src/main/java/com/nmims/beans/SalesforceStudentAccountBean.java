package com.nmims.beans;

import java.io.Serializable;

public class SalesforceStudentAccountBean   implements Serializable  {

	private String sapid;
	private boolean sem1;
	private boolean sem2;
	private boolean sem3;
	private boolean sem4;
	private boolean passout;
	private String acadYear;
	private String acadMonth;
	private String sem;
	private String id;
	private String programType="";
	private String opportunityId;
	private String termCleared="";
	
	public String getTermCleared() {
		return termCleared;
	}
	public void setTermCleared(String termCleared) {
		this.termCleared = termCleared;
	}
	public String getOpportunityId() {
		return opportunityId;
	}
	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public boolean isSem1() {
		return sem1;
	}
	public void setSem1(boolean sem1) {
		this.sem1 = sem1;
	}
	public boolean isSem2() {
		return sem2;
	}
	public void setSem2(boolean sem2) {
		this.sem2 = sem2;
	}
	public boolean isSem3() {
		return sem3;
	}
	public void setSem3(boolean sem3) {
		this.sem3 = sem3;
	}
	public boolean isSem4() {
		return sem4;
	}
	public void setSem4(boolean sem4) {
		this.sem4 = sem4;
	}
	public boolean isPassout() {
		return passout;
	}
	public void setPassout(boolean passout) {
		this.passout = passout;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	@Override
	public String toString() {
		return "SalesforceStudentAccountBean [sapid=" + sapid + ", sem1=" + sem1 + ", sem2=" + sem2 + ", sem3=" + sem3
				+ ", sem4=" + sem4 + ", passout=" + passout + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth
				+ ", sem=" + sem + ", id=" + id + ", programType=" + programType + ", opportunityId=" + opportunityId
				+ ", termCleared=" + termCleared + "]";
	}
	
	
	
}