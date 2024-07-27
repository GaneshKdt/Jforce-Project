package com.nmims.beans;

public class louReportBean {
private String programType;
private String programName;
private String enrollmentmonth;
private String enrollmentyear;
private String semTerm;
private String batch;
private String dataOfSubmission;
public String getProgramType() {
	return programType;
}
public void setProgramType(String programType) {
	this.programType = programType;
}
public String getProgramName() {
	return programName;
}
public void setProgramName(String programName) {
	this.programName = programName;
}

public String getEnrollmentmonth() {
	return enrollmentmonth;
}
public void setEnrollmentmonth(String enrollmentmonth) {
	this.enrollmentmonth = enrollmentmonth;
}
public String getEnrollmentyear() {
	return enrollmentyear;
}
public void setEnrollmentyear(String enrollmentyear) {
	this.enrollmentyear = enrollmentyear;
}
public String getSemTerm() {
	return semTerm;
}
public void setSemTerm(String semTerm) {
	this.semTerm = semTerm;
}
public String getBatch() {
	return batch;
}
public void setBatch(String batch) {
	this.batch = batch;
}
public String getDataOfSubmission() {
	return dataOfSubmission;
}
public void setDataOfSubmission(String dataOfSubmission) {
	this.dataOfSubmission = dataOfSubmission;
}
@Override
public String toString() {
	return "louReportBean [programType=" + programType + ", programName=" + programName + ", enrollmentmonth="
			+ enrollmentmonth+", enrollmentyear="+enrollmentyear + ", semTerm=" + semTerm + ", batch=" + batch + ", dataOfSubmission="
			+ dataOfSubmission + "]";
}



}
