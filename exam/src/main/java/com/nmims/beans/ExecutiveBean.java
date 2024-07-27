package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExecutiveBean extends BaseExamBean  implements Serializable  {
//
	private List<ProgramSubjectMappingExamBean> executiveProgramsubjectList = new ArrayList<>();
	private ArrayList<String> eligibleSubjectsList=new ArrayList<>();
	private ArrayList<String> applicableSubjects=new ArrayList<>();
	private ArrayList<String> selectedCenters = new ArrayList<>();
	private Integer centerId;
	private String examCenterName;
	private String city;
	private String state;
	private Integer capacity;
	private String address;
	private String locality;
	private Double order;
	private String live;
	private String timeTableLive;
	private String declareDate;
	private String subjectName;
	private String hasExam;
	private String registered;
	private String configurationType;
	private String startTime;
	private String endTime;
	private String program;
	private String prgmStructApplicable;
	private String subject;
	private String sem;
	private Integer examYear;
	private String examMonth;
	private String date;
	private String booked;
	private String changeOfCenter;
	private String password;
	private String examDate;
	private String examTime;
	private String role;
	

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList<String> getSelectedCenters() {
		return selectedCenters;
	}
	public void setSelectedCenters(ArrayList<String> selectedCenters) {
		this.selectedCenters = selectedCenters;
	}
	public String getChangeOfCenter() {
		return changeOfCenter;
	}
	public void setChangeOfCenter(String changeOfCenter) {
		this.changeOfCenter = changeOfCenter;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public Integer getCenterId() {
		return centerId;
	}
	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public Double getOrder() {
		return order;
	}
	public void setOrder(Double order) {
		this.order = order;
	}
	public String getLive() {
		return live;
	}
	public void setLive(String live) {
		this.live = live;
	}
	public String getTimeTableLive() {
		return timeTableLive;
	}
	public void setTimeTableLive(String timeTableLive) {
		this.timeTableLive = timeTableLive;
	}
	public String getDeclareDate() {
		return declareDate;
	}
	public void setDeclareDate(String declareDate) {
		this.declareDate = declareDate;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getHasExam() {
		return hasExam;
	}
	public void setHasExam(String hasExam) {
		this.hasExam = hasExam;
	}
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}
	public String getConfigurationType() {
		return configurationType;
	}
	public void setConfigurationType(String configurationType) {
		this.configurationType = configurationType;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public Integer getExamYear() {
		return examYear;
	}
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<ProgramSubjectMappingExamBean> getExecutiveProgramsubjectList() {
		return executiveProgramsubjectList;
	}
	public void setExecutiveProgramsubjectList(List<ProgramSubjectMappingExamBean> executiveProgramsubjectList) {
		this.executiveProgramsubjectList = executiveProgramsubjectList;
	}
	public ArrayList<String> getEligibleSubjectsList() {
		return eligibleSubjectsList;
	}
	public void setEligibleSubjectsList(ArrayList<String> eligibleSubjectsList) {
		this.eligibleSubjectsList = eligibleSubjectsList;
	}
	public ArrayList<String> getApplicableSubjects() {
		return applicableSubjects;
	}
	public void setApplicableSubjects(ArrayList<String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}
	@Override
	public String toString() {
		return "ExecutiveBean [executiveProgramsubjectList=" + executiveProgramsubjectList + ", eligibleSubjectsList="
				+ eligibleSubjectsList + ", applicableSubjects=" + applicableSubjects + ", selectedCenters="
				+ selectedCenters + ", centerId=" + centerId + ", examCenterName=" + examCenterName + ", city=" + city
				+ ", state=" + state + ", capacity=" + capacity + ", address=" + address + ", locality=" + locality
				+ ", order=" + order + ", live=" + live + ", timeTableLive=" + timeTableLive + ", declareDate="
				+ declareDate + ", subjectName=" + subjectName + ", hasExam=" + hasExam + ", registered=" + registered
				+ ", configurationType=" + configurationType + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", program=" + program + ", prgmStructApplicable=" + prgmStructApplicable + ", subject=" + subject
				+ ", sem=" + sem + ", examYear=" + examYear + ", examMonth=" + examMonth + ", date=" + date
				+ ", booked=" + booked + ", changeOfCenter=" + changeOfCenter + ", password=" + password + ", examDate="
				+ examDate + ", examTime=" + examTime + ", role=" + role + "]";
	}


}

