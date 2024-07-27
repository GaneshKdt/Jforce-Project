
package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceRequestBean implements Serializable  {

	private Long id;
	private String sapid;
	private StudentExamBean student;
	private ArrayList<String> error;
	private boolean htDownloadStatus;
	private String subjectDoubleBookingMap;
	private HashMap<String, ExamBookingTransactionBean> subjectBookingMap;
	private String passwordPresent;
	private String passwordAbsent;
	private String password;
	private boolean corporateCenterUserMapping;
	private String title;
	private String location;
	private String examination;
	private String programFullName;
	private ArrayList<ExamBookingTransactionBean> examBookedList;
	private String serviceRequestType;
	private List<TimetableBean> timeTableList;
    private HashMap<String, ExamBookingMBAWX> subjectBookingMapMbaWx;
    private ArrayList<ExamBookingMBAWX> examBookedListMbaWx;
    
    //Added by Aneel for Bonafide Certificate SR
	private int serviceRequestId;
	private String AdditionalInfo1;
	private String filePath;
	private String documentType;
	private String requestStatus;
	
	public String getServiceRequestType() {
		return serviceRequestType;
	}

	public void setServiceRequestType(String serviceRequestType) {
		this.serviceRequestType = serviceRequestType;
	}

	public ArrayList<ExamBookingMBAWX> getExamBookedListMbaWx() {
		return examBookedListMbaWx;
	}

	public void setExamBookedListMbaWx(ArrayList<ExamBookingMBAWX> examBookedListMbaWx) {
		this.examBookedListMbaWx = examBookedListMbaWx;
	}

	public HashMap<String, ExamBookingMBAWX> getSubjectBookingMapMbaWx() {
		return subjectBookingMapMbaWx;
	}

	public void setSubjectBookingMapMbaWx(HashMap<String, ExamBookingMBAWX> subjectBookingMapMbaWx) {
		this.subjectBookingMapMbaWx = subjectBookingMapMbaWx;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private ArrayList<String> subjects;

	public String getPasswordPresent() {
		return passwordPresent;
	}

	public void setPasswordPresent(String passwordPresent) {
		this.passwordPresent = passwordPresent;
	}

	public String getPasswordAbsent() {
		return passwordAbsent;
	}

	public void setPasswordAbsent(String passwordAbsent) {
		this.passwordAbsent = passwordAbsent;
	}

	public ArrayList<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(ArrayList<String> subjects) {
		this.subjects = subjects;
	}

	public String getSubjectDoubleBookingMap() {
		return subjectDoubleBookingMap;
	}

	public void setSubjectDoubleBookingMap(String subjectDoubleBookingMap) {
		this.subjectDoubleBookingMap = subjectDoubleBookingMap;
	}

	public HashMap<String, ExamBookingTransactionBean> getSubjectBookingMap() {
		return subjectBookingMap;
	}

	public void setSubjectBookingMap(HashMap<String, ExamBookingTransactionBean> subjectBookingMap2) {
		this.subjectBookingMap = subjectBookingMap2;
	}

	public StudentExamBean getStudent() {
		return student;
	}

	public void setStudent(StudentExamBean student) {
		this.student = student;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public List<TimetableBean> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(List<TimetableBean> timeTableList) {
		this.timeTableList = timeTableList;
	}

	public boolean isCorporateCenterUserMapping() {
		return corporateCenterUserMapping;
	}

	public void setCorporateCenterUserMapping(boolean corporateCenterUserMapping) {
		this.corporateCenterUserMapping = corporateCenterUserMapping;
	}

	public ArrayList<String> getError() {
		return error;
	}

	public void setError(ArrayList<String> error) {
		this.error = error;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getExamination() {
		return examination;
	}

	public void setExamination(String examination) {
		this.examination = examination;
	}

	public String getProgramFullName() {
		return programFullName;
	}

	public void setProgramFullName(String programFullName) {
		this.programFullName = programFullName;
	}

	public ArrayList<ExamBookingTransactionBean> getExamBookedList() {
		return examBookedList;
	}

	public void setExamBookedList(ArrayList<ExamBookingTransactionBean> examBookedList) {
		this.examBookedList = examBookedList;
	}

	public boolean isHtDownloadStatus() {
		return htDownloadStatus;
	}

	public void setHtDownloadStatus(boolean htDownloadStatus) {
		this.htDownloadStatus = htDownloadStatus;
	}

	public int getServiceRequestId() {
		return serviceRequestId;
	}

	public void setServiceRequestId(int serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	

	public String getAdditionalInfo1() {
		return AdditionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		AdditionalInfo1 = additionalInfo1;
	}

	@Override
	public String toString() {
		return "ServiceRequestBean [id=" + id + ", sapid=" + sapid + ", serviceRequestType=" + serviceRequestType
				+ ", serviceRequestId=" + serviceRequestId + ", AdditionalInfo1=" + AdditionalInfo1 + ", filePath="
				+ filePath + ", documentType=" + documentType + ", requestStatus=" + requestStatus + "]";
	}
}
