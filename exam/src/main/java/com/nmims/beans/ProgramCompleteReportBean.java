package com.nmims.beans;

import java.io.Serializable;

public class ProgramCompleteReportBean  implements Serializable  {
	private String sapid;
    private String sem;
    private String lastName;
    private String firstName;
    private String fatherName;
    private String motherName;
    private String declareDate;
    private String month;
    private String year;
    private String additionalInfo1;
	

	private String programname;
    private String oflineResultsDeclareDate;
    
    
    public String getAdditionalInfo1() {
		return additionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}
	public String getProgramname() {
		return programname;
	}

	public void setProgramname(String programname) {
		this.programname = programname;
	}
	 public String getOflineResultsDeclareDate() {
			return oflineResultsDeclareDate;
	}
    public void setOflineResultsDeclareDate(String oflineResultsDeclareDate) {
		this.oflineResultsDeclareDate = oflineResultsDeclareDate;
	} 
	public String getDeclareDate() {
		return declareDate;
	}

	public void setDeclareDate(String declareDate) {
		this.declareDate = declareDate;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	@Override
	public String toString() {
		return "ProgramCompleteReportBean [sapid=" + sapid + ", sem=" + sem
				+ ", lastName=" + lastName + ", firstName=" + firstName
				+ ", fatherName=" + fatherName + ", motherName=" + motherName
				+ ", declareDate=" + declareDate + ", month=" + month
				+ ", year=" + year + ", programname=" + programname + "]";
	}


	
    
}
