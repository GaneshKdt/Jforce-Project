package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//spring security related changes rename ExamRegistrationBeanAPIResponse to ExamRegistrationBeanAPIResponseExam
public class ExamRegistrationBeanAPIResponseExam  implements Serializable  {
Boolean isExamRegistraionLive;
String mostRecentTimetablePeriod;
String status;
String error;
HashMap<String,String> corporateCenterUserMapping;
Map<String, String> examCenterIdNameMap;
String hasConfirmedBookings;
ArrayList<String> dateTimeBookedList;
ArrayList<String> approvedOnlineTransactionSubjects;
ArrayList<String> freeApplicableSubjects; 
ArrayList<String> releasedSubjects;
ArrayList<String> releasedNoChargeSubjects;
HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap;
Boolean hasApprovedOnlineTransactions;
Boolean hasFreeSubjects;
Boolean hasReleasedSubjects;
HashMap<String,Integer> mapOfSubjectNameAndExamFee;
ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList;
int applicableSubjectsListCount;
ExamBookingExamBean examBooking;
int subjectsToPay;

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public int getSubjectsToPay() {
	return subjectsToPay;
}

public void setSubjectsToPay(int subjectsToPay) {
	this.subjectsToPay = subjectsToPay;
}

public ExamBookingExamBean getExamBookingBean() {
	return examBooking;
}

public void setExamBookingBean(ExamBookingExamBean examBooking) {
	this.examBooking = examBooking;
}


public int getApplicableSubjectsListCount() {
	return applicableSubjectsListCount;
}

public void setApplicableSubjectsListCount(int applicableSubjectsListCount) {
	this.applicableSubjectsListCount = applicableSubjectsListCount;
}

public ArrayList<ProgramSubjectMappingExamBean> getApplicableSubjectsList() {
	return applicableSubjectsList;
}

public void setApplicableSubjectsList(ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList) {
	this.applicableSubjectsList = applicableSubjectsList;
}

public HashMap<String, Integer> getMapOfSubjectNameAndExamFee() {
	return mapOfSubjectNameAndExamFee;
}

public void setMapOfSubjectNameAndExamFee(HashMap<String, Integer> mapOfSubjectNameAndExamFee) {
	this.mapOfSubjectNameAndExamFee = mapOfSubjectNameAndExamFee;
}

public Boolean getHasFreeSubjects() {
	return hasFreeSubjects;
}

public void setHasFreeSubjects(Boolean hasFreeSubjects) {
	this.hasFreeSubjects = hasFreeSubjects;
}

public Boolean getHasApprovedOnlineTransactions() {
	return hasApprovedOnlineTransactions;
}

public void setHasApprovedOnlineTransactions(Boolean hasApprovedOnlineTransactions) {
	this.hasApprovedOnlineTransactions = hasApprovedOnlineTransactions;
}


public Boolean getHasReleasedSubjects() {
	return hasReleasedSubjects;
}

public void setHasReleasedSubjects(Boolean hasReleasedSubjects) {
	this.hasReleasedSubjects = hasReleasedSubjects;
}

Boolean hasReleasedNoChargeSubjects;


public Boolean getHasReleasedNoChargeSubjects() {
	return hasReleasedNoChargeSubjects;
}

public void setHasReleasedNoChargeSubjects(Boolean hasReleasedNoChargeSubjects) {
	this.hasReleasedNoChargeSubjects = hasReleasedNoChargeSubjects;
}

public HashMap<String, ProgramSubjectMappingExamBean> getSubjectProgramSemMap() {
	return subjectProgramSemMap;
}

public void setSubjectProgramSemMap(HashMap<String, ProgramSubjectMappingExamBean> subjectProgramSemMap) {
	this.subjectProgramSemMap = subjectProgramSemMap;
}

public ArrayList<String> getReleasedNoChargeSubjects() {
	return releasedNoChargeSubjects;
}

public void setReleasedNoChargeSubjects(ArrayList<String> releasedNoChargeSubjects) {
	this.releasedNoChargeSubjects = releasedNoChargeSubjects;
}

public ArrayList<String> getReleasedSubjects() {
	return releasedSubjects;
}

public void setReleasedSubjects(ArrayList<String> releasedSubjects) {
	this.releasedSubjects = releasedSubjects;
}

public ArrayList<String> getFreeApplicableSubjects() {
	return freeApplicableSubjects;
}

public void setFreeApplicableSubjects(ArrayList<String> freeApplicableSubjects) {
	this.freeApplicableSubjects = freeApplicableSubjects;
}

public ArrayList<String> getApprovedOnlineTransactionSubjects() {
	return approvedOnlineTransactionSubjects;
}

public void setApprovedOnlineTransactionSubjects(ArrayList<String> approvedOnlineTransactionSubjects) {
	this.approvedOnlineTransactionSubjects = approvedOnlineTransactionSubjects;
}

public ArrayList<String> getDateTimeBookedList() {
	return dateTimeBookedList;
}

public void setDateTimeBookedList(ArrayList<String> dateTimeBookedList) {
	this.dateTimeBookedList = dateTimeBookedList;
}

public String getHasConfirmedBookings() {
	return hasConfirmedBookings;
}

public void setHasConfirmedBookings(String hasConfirmedBookings) {
	this.hasConfirmedBookings = hasConfirmedBookings;
}

public Map<String, String> getExamCenterIdNameMap() {
	return examCenterIdNameMap;
}

public void setExamCenterIdNameMap(Map<String, String> examCenterIdNameMap) {
	this.examCenterIdNameMap = examCenterIdNameMap;
}

public HashMap<String, String> getCorporateCenterUserMapping() {
	return corporateCenterUserMapping;
}

public void setCorporateCenterUserMapping(HashMap<String, String> corporateCenterUserMapping) {
	this.corporateCenterUserMapping = corporateCenterUserMapping;
}

public String getError() {
	return error;
}

public void setError(String error) {
	this.error = error;
}

public String getMostRecentTimetablePeriod() {
	return mostRecentTimetablePeriod;
}

public void setMostRecentTimetablePeriod(String mostRecentTimetablePeriod) {
	this.mostRecentTimetablePeriod = mostRecentTimetablePeriod;
}

public Boolean getIsExamRegistraionLive() {
	return isExamRegistraionLive;
}

public void setIsExamRegistraionLive(Boolean isExamRegistraionLive) {
	this.isExamRegistraionLive = isExamRegistraionLive;
}
}
