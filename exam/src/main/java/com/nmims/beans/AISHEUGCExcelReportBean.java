package com.nmims.beans;

import java.io.Serializable;

public class AISHEUGCExcelReportBean extends StudentExamBean implements Serializable {
	
	
	private String enrollmentMonth;
	private String enrollmentYear;
	private String program;
	private String totalNoOfStudentsAppearedInFinalYear;
	private String totalNoOfGirlsStudentsAppearedInFinalYear;
	private String totalNoOfStudentsPassed;
	private String totalNoOfGirlsStudentsPasseded;
	private Integer totalNoOfStudentsAbove60percentage;
	private Integer totalNoOfGirlsStudentsAbove60Percentage;
	private String sem;
	
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getTotalNoOfStudentsAppearedInFinalYear() {
		return totalNoOfStudentsAppearedInFinalYear;
	}
	public void setTotalNoOfStudentsAppearedInFinalYear(String totalNoOfStudentsAppearedInFinalYear) {
		this.totalNoOfStudentsAppearedInFinalYear = totalNoOfStudentsAppearedInFinalYear;
	}
	public String getTotalNoOfGirlsStudentsAppearedInFinalYear() {
		return totalNoOfGirlsStudentsAppearedInFinalYear;
	}
	public void setTotalNoOfGirlsStudentsAppearedInFinalYear(String totalNoOfGirlsStudentsAppearedInFinalYear) {
		this.totalNoOfGirlsStudentsAppearedInFinalYear = totalNoOfGirlsStudentsAppearedInFinalYear;
	}
	public String getTotalNoOfStudentsPassed() {
		return totalNoOfStudentsPassed;
	}
	public void setTotalNoOfStudentsPassed(String totalNoOfStudentsPassed) {
		this.totalNoOfStudentsPassed = totalNoOfStudentsPassed;
	}
	public String getTotalNoOfGirlsStudentsPasseded() {
		return totalNoOfGirlsStudentsPasseded;
	}
	public void setTotalNoOfGirlsStudentsPasseded(String totalNoOfGirlsStudentsPasseded) {
		this.totalNoOfGirlsStudentsPasseded = totalNoOfGirlsStudentsPasseded;
	}
	
	public Integer getTotalNoOfStudentsAbove60percentage() {
		return totalNoOfStudentsAbove60percentage;
	}
	public void setTotalNoOfStudentsAbove60percentage(Integer totalNoOfStudentsAbove60percentage) {
		this.totalNoOfStudentsAbove60percentage = totalNoOfStudentsAbove60percentage;
	}
	public Integer getTotalNoOfGirlsStudentsAbove60Percentage() {
		return totalNoOfGirlsStudentsAbove60Percentage;
	}
	public void setTotalNoOfGirlsStudentsAbove60Percentage(Integer totalNoOfGirlsStudentsAbove60Percentage) {
		this.totalNoOfGirlsStudentsAbove60Percentage = totalNoOfGirlsStudentsAbove60Percentage;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	@Override
	public String toString() {
		return "AISHEUGCExcelReportBean [enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear
				+ ", program=" + program + ", totalNoOfStudentsAppearedInFinalYear="
				+ totalNoOfStudentsAppearedInFinalYear + ", totalNoOfGirlsStudentsAppearedInFinalYear="
				+ totalNoOfGirlsStudentsAppearedInFinalYear + ", totalNoOfStudentsPassed=" + totalNoOfStudentsPassed
				+ ", totalNoOfGirlsStudentsPasseded=" + totalNoOfGirlsStudentsPasseded
				+ ", totalNoOfStudentsAbove60percentage=" + totalNoOfStudentsAbove60percentage
				+ ", totalNoOfGirlsStudentsAbove60Percentage=" + totalNoOfGirlsStudentsAbove60Percentage + ", sem="
				+ sem + "]";
	}
	
}


