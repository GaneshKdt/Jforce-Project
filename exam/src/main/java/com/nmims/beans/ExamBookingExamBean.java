package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

//Should be deleted
//spring security related changes rename ExamBookingBean to ExamBookingExamBean
public class ExamBookingExamBean  implements Serializable {

	private ArrayList<String> applicableSubjects = new ArrayList<>();
	private ArrayList<String> selectedCenters = new ArrayList<>();
	private ArrayList<String> freeApplicableSubjects = new ArrayList<>();
	private String year;
	private String month;
	
	//Used for passedRevalYetRegistered report only
	
	private String sapid;
	private String subject;
	private String name;
	private String program;
	private String sem;
	private String bookedYear;
	private String bookedMonth;
	private String booked;
	private String examDate; 
	private String totalFeesAmount;
	//Used for report only
	
	
	
	public String getSapid() {
		return sapid;
	}

	public String getTotalFeesAmount() {
		return totalFeesAmount;
	}

	public void setTotalFeesAmount(String totalFeesAmount) {
		this.totalFeesAmount = totalFeesAmount;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getBookedYear() {
		return bookedYear;
	}

	public void setBookedYear(String bookedYear) {
		this.bookedYear = bookedYear;
	}

	public String getBookedMonth() {
		return bookedMonth;
	}

	public void setBookedMonth(String bookedMonth) {
		this.bookedMonth = bookedMonth;
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

	public ArrayList<String> getSelectedCenters() {
		return selectedCenters;
	}

	public void setSelectedCenters(ArrayList<String> selectedCenters) {
		this.selectedCenters = selectedCenters;
	}

	public ArrayList<String> getApplicableSubjects() {
		return applicableSubjects;
	}

	public ArrayList<String> getFreeApplicableSubjects() {
		return freeApplicableSubjects;
	}

	public void setFreeApplicableSubjects(ArrayList<String> freeApplicableSubjects) {
		this.freeApplicableSubjects = freeApplicableSubjects;
	}

	public void setApplicableSubjects(ArrayList<String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}

	public String getBooked() {
		return booked;
	}

	public void setBooked(String booked) {
		this.booked = booked;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	@Override
	public String toString() {
		return "ExamBookingBean [applicableSubjects=" + applicableSubjects + ", selectedCenters=" + selectedCenters
				+ ", freeApplicableSubjects=" + freeApplicableSubjects + ", year=" + year + ", month=" + month
				+ ", sapid=" + sapid + ", subject=" + subject + ", name=" + name + ", program=" + program + ", sem="
				+ sem + ", bookedYear=" + bookedYear + ", bookedMonth=" + bookedMonth + ", booked=" + booked + ", examDate="
				+ examDate + "]";
	}

	
	
	
	
	
	

}
