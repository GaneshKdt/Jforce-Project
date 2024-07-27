package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//spring security related changes rename ExamAssignmentResponse to ExamAssignmentResponseBean
public class ExamAssignmentResponseBean  implements Serializable {
   
	private String month;
	
	private String year;
	private ArrayList<String> currentYearList;	
	private ArrayList<String> subjectList;
	private String error;  
	private String yearMonth; 
	private List<AssignmentFileBean> currentSemAssignmentFilesList;
	private String success; 
	private String successMessage; 
	
	private int maxAttempts;


	private int currentSemSubjectsCount;
	private int currentSemSubmissionCount;
	private List<AssignmentFileBean> failSubjectsAssignmentFilesList;
	private int failSubjectsCount;
	private int failSubjectSubmissionCount;
	private String currentSemEndDateTime;
	private String failSubjectsEndDateTime;  
	private ArrayList<String> timeExtendedStudentIdSubjectList;
	private ArrayList<String> applicableSubjects;
	private ArrayList<String> subjectsNotAllowedToSubmit;
	private AssignmentFileBean assignmentFile;
	private String subject;
	private String ErrorMessage;
	private List<AssignmentFileBean> allAssignmentFilesList;
	 
	
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
	
	public List<AssignmentFileBean> getAllAssignmentFilesList() {
		return allAssignmentFilesList;
	}
	public void setAllAssignmentFilesList(List<AssignmentFileBean> allAssignmentFilesList) {
		this.allAssignmentFilesList = allAssignmentFilesList;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public String getErrorMessage() {
		return ErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	public int getMaxAttempts() {
		return maxAttempts;
	}
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public AssignmentFileBean getAssignmentFile() {
		return assignmentFile;
	}
	public void setAssignmentFile(AssignmentFileBean assignmentFile) {
		this.assignmentFile = assignmentFile;
	}
	
	public ArrayList<String> getSubjectsNotAllowedToSubmit() {
		return subjectsNotAllowedToSubmit;
	}
	public void setSubjectsNotAllowedToSubmit(ArrayList<String> subjectsNotAllowedToSubmit) {
		this.subjectsNotAllowedToSubmit = subjectsNotAllowedToSubmit;
	}
	public ArrayList<String> getApplicableSubjects() {
		return applicableSubjects;
	}
	public void setApplicableSubjects(ArrayList<String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}
	public ArrayList<String> getCurrentYearList() {
		return currentYearList;   
	}
	public void setCurrentYearList(ArrayList<String> currentYearList) {
		this.currentYearList = currentYearList;
	}
	public ArrayList<String> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<String> subjectList) {
		this.subjectList = subjectList;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	public List<AssignmentFileBean> getCurrentSemAssignmentFilesList() {
		return currentSemAssignmentFilesList;
	}
	public void setCurrentSemAssignmentFilesList(List<AssignmentFileBean> currentSemAssignmentFilesList) {
		this.currentSemAssignmentFilesList = currentSemAssignmentFilesList;
	}
	public int getCurrentSemSubjectsCount() {
		return currentSemSubjectsCount;
	}
	public void setCurrentSemSubjectsCount(int currentSemSubjectsCount) {
		this.currentSemSubjectsCount = currentSemSubjectsCount;
	}
	public int getCurrentSemSubmissionCount() {
		return currentSemSubmissionCount;
	}
	public void setCurrentSemSubmissionCount(int currentSemSubmissionCount) {
		this.currentSemSubmissionCount = currentSemSubmissionCount;
	}
	public List<AssignmentFileBean> getFailSubjectsAssignmentFilesList() {
		return failSubjectsAssignmentFilesList;
	}
	public void setFailSubjectsAssignmentFilesList(List<AssignmentFileBean> failSubjectsAssignmentFilesList) {
		this.failSubjectsAssignmentFilesList = failSubjectsAssignmentFilesList;
	}
	public int getFailSubjectsCount() {
		return failSubjectsCount;
	}
	public void setFailSubjectsCount(int failSubjectsCount) {
		this.failSubjectsCount = failSubjectsCount;
	}
	public int getFailSubjectSubmissionCount() {
		return failSubjectSubmissionCount;
	}
	public void setFailSubjectSubmissionCount(int failSubjectSubmissionCount) {
		this.failSubjectSubmissionCount = failSubjectSubmissionCount;
	}
	public String getCurrentSemEndDateTime() {
		return currentSemEndDateTime;
	}
	public void setCurrentSemEndDateTime(String currentSemEndDateTime) {
		this.currentSemEndDateTime = currentSemEndDateTime;
	}
	public String getFailSubjectsEndDateTime() {
		return failSubjectsEndDateTime;
	}
	public void setFailSubjectsEndDateTime(String failSubjectsEndDateTime) {
		this.failSubjectsEndDateTime = failSubjectsEndDateTime;
	}
	public ArrayList<String> getTimeExtendedStudentIdSubjectList() {
		return timeExtendedStudentIdSubjectList;
	}
	public void setTimeExtendedStudentIdSubjectList(ArrayList<String> timeExtendedStudentIdSubjectList) {
		this.timeExtendedStudentIdSubjectList = timeExtendedStudentIdSubjectList;
	}
	
	@Override
	public String toString() {
		return "ExamAssignmentResponseBean [month=" + month + ", year=" + year + ", currentYearList=" + currentYearList
				+ ", subjectList=" + subjectList + ", error=" + error + ", yearMonth=" + yearMonth
				+ ", currentSemAssignmentFilesList=" + currentSemAssignmentFilesList + ", success=" + success
				+ ", successMessage=" + successMessage + ", maxAttempts=" + maxAttempts + ", currentSemSubjectsCount="
				+ currentSemSubjectsCount + ", currentSemSubmissionCount=" + currentSemSubmissionCount
				+ ", failSubjectsAssignmentFilesList=" + failSubjectsAssignmentFilesList + ", failSubjectsCount="
				+ failSubjectsCount + ", failSubjectSubmissionCount=" + failSubjectSubmissionCount
				+ ", currentSemEndDateTime=" + currentSemEndDateTime + ", failSubjectsEndDateTime="
				+ failSubjectsEndDateTime + ", timeExtendedStudentIdSubjectList=" + timeExtendedStudentIdSubjectList
				+ ", applicableSubjects=" + applicableSubjects + ", subjectsNotAllowedToSubmit="
				+ subjectsNotAllowedToSubmit + ", assignmentFile=" + assignmentFile + ", subject=" + subject
				+ ", ErrorMessage=" + ErrorMessage + ", allAssignmentFilesList=" + allAssignmentFilesList + "]";
	}
}