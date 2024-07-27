package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - ExecutiveExamOrderBean
 * @author
 *
 */
public class ExecutiveExamOrderStudentPortalBean extends BaseStudentPortalBean  implements Serializable{
 private String order;
 private String live;
 private String declareDate;
 private String timeTableLive;
 private String acadYear;
 private String acadMonth;
 private String registrationStartDate;
 private String registrationEndDate;
 private String subject;
 private String program;
 private String prgmStructApplicable;
private String year;
private String month;
 private int id;
 private String hallTicketStartDate;
 public String getExamYear() {
	return examYear;
}
public void setExamYear(String examYear) {
	this.examYear = examYear;
}
public String getExamMonth() {
	return examMonth;
}
public void setExamMonth(String examMonth) {
	this.examMonth = examMonth;
}
private String hallTicketEndDate;
 private String resultLive;
 private String resultDeclareDate;
 private String examYear;
 private String examMonth;
 
 
public String getPrgmStructApplicable() {
	return prgmStructApplicable;
}
public void setPrgmStructApplicable(String prgmStructApplicable) {
	this.prgmStructApplicable = prgmStructApplicable;
}
public String getResultLive() {
	return resultLive;
}
public void setResultLive(String resultLive) {
	this.resultLive = resultLive;
}
public String getResultDeclareDate() {
	return resultDeclareDate;
}
public void setResultDeclareDate(String resultDeclareDate) {
	this.resultDeclareDate = resultDeclareDate;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getProgram() {
	return program;
}
public void setProgram(String program) {
	this.program = program;
}

public String getRegistrationStartDate() {
	return registrationStartDate;
}
public void setRegistrationStartDate(String registrationStartDate) {
	this.registrationStartDate = registrationStartDate;
}
public String getRegistrationEndDate() {
	return registrationEndDate;
}
public void setRegistrationEndDate(String registrationEndDate) {
	this.registrationEndDate = registrationEndDate;
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
public String getOrder() {
	return order;
}
public void setOrder(String order) {
	this.order = order;
}
public String getLive() {
	return live;
}
public void setLive(String live) {
	this.live = live;
}
public String getDeclareDate() {
	return declareDate;
}
public void setDeclareDate(String declareDate) {
	this.declareDate = declareDate;
}
public String getTimeTableLive() {
	return timeTableLive;
}
public void setTimeTableLive(String timeTableLive) {
	this.timeTableLive = timeTableLive;
}

public String getHallTicketEndDate() {
	return hallTicketEndDate;
}
public void setHallTicketEndDate(String hallTicketEndDate) {
	this.hallTicketEndDate = hallTicketEndDate;
}
public String getHallTicketStartDate() {
	return hallTicketStartDate;
}
public void setHallTicketStartDate(String hallTicketStartDate) {
	this.hallTicketStartDate = hallTicketStartDate;
}
@Override
public String toString() {
	return "ExecutiveExamOrderStudentPortalBean [order=" + order + ", live=" + live + ", declareDate=" + declareDate
			+ ", timeTableLive=" + timeTableLive + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth
			+ ", registrationStartDate=" + registrationStartDate + ", registrationEndDate=" + registrationEndDate
			+ ", subject=" + subject + ", program=" + program + ", prgmStructApplicable=" + prgmStructApplicable
			+ ", id=" + id + ", hallTicketStartDate=" + hallTicketStartDate + ", hallTicketEndDate=" + hallTicketEndDate
			+ ", resultLive=" + resultLive + ", resultDeclareDate=" + resultDeclareDate + "]";
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




	
}
