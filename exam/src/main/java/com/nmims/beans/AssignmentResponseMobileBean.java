package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class AssignmentResponseMobileBean implements Serializable{
	private String userId;
	private String startDate;
	private String endDate;
	private String subject;
	private String status;
	private String errorMessage;
	private String assignmentPaymentPending = "N";
	private String submissionAllowed;
	private int maxAttempts;
	private String subjectForPayment;
	private AssignmentFileBean assignmentFile;
	private ArrayList<String> yearList;
	private ArrayList<String> subjectList;
	private ArrayList<String> timeExtendedStudentIdSubjectList;
	private ExamBookingExamBean examBooking;
	private String mostRecentTimetablePeriod;
	private ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList;
	private int applicableSubjectsListCount;
	private int subjectsToPay = 0;
	private int feesPerSubject = 0;
	private String goToPaymentGatewayUrl;
	private List<String> applicableSubjects;
	private String paymentOptionName;
	

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getAssignmentPaymentPending() {
		return assignmentPaymentPending;
	}
	public void setAssignmentPaymentPending(String assignmentPaymentPending) {
		this.assignmentPaymentPending = assignmentPaymentPending;
	}
	public String getSubmissionAllowed() {
		return submissionAllowed;
	}
	public void setSubmissionAllowed(String submissionAllowed) {
		this.submissionAllowed = submissionAllowed;
	}
	public int getMaxAttempts() {
		return maxAttempts;
	}
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	public String getSubjectForPayment() {
		return subjectForPayment;
	}
	public void setSubjectForPayment(String subjectForPayment) {
		this.subjectForPayment = subjectForPayment;
	}
	public AssignmentFileBean getAssignmentFile() {
		return assignmentFile;
	}
	public void setAssignmentFile(AssignmentFileBean assignmentFile) {
		this.assignmentFile = assignmentFile;
	}
	public ArrayList<String> getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList<String> yearList) {
		this.yearList = yearList;
	}
	public ArrayList<String> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<String> subjectList) {
		this.subjectList = subjectList;
	}
	public ArrayList<String> getTimeExtendedStudentIdSubjectList() {
		return timeExtendedStudentIdSubjectList;
	}
	public void setTimeExtendedStudentIdSubjectList(ArrayList<String> timeExtendedStudentIdSubjectList) {
		this.timeExtendedStudentIdSubjectList = timeExtendedStudentIdSubjectList;
	}
	public ExamBookingExamBean getExamBooking() {
		return examBooking;
	}
	public void setExamBooking(ExamBookingExamBean examBooking) {
		this.examBooking = examBooking;
	}
	public String getMostRecentTimetablePeriod() {
		return mostRecentTimetablePeriod;
	}
	public void setMostRecentTimetablePeriod(String mostRecentTimetablePeriod) {
		this.mostRecentTimetablePeriod = mostRecentTimetablePeriod;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getApplicableSubjectsList() {
		return applicableSubjectsList;
	}
	public void setApplicableSubjectsList(ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList) {
		this.applicableSubjectsList = applicableSubjectsList;
	}
	public int getApplicableSubjectsListCount() {
		return applicableSubjectsListCount;
	}
	public void setApplicableSubjectsListCount(int applicableSubjectsListCount) {
		this.applicableSubjectsListCount = applicableSubjectsListCount;
	}
	public int getSubjectsToPay() {
		return subjectsToPay;
	}
	public void setSubjectsToPay(int subjectsToPay) {
		this.subjectsToPay = subjectsToPay;
	}
	public int getFeesPerSubject() {
		return feesPerSubject;
	}
	public void setFeesPerSubject(int feesPerSubject) {
		this.feesPerSubject = feesPerSubject;
	}
	public String getGoToPaymentGatewayUrl() {
		return goToPaymentGatewayUrl;
	}
	public void setGoToPaymentGatewayUrl(String goToPaymentGatewayUrl) {
		this.goToPaymentGatewayUrl = goToPaymentGatewayUrl;
	}
	public List<String> getApplicableSubjects() {
		return applicableSubjects;
	}
	public void setApplicableSubjects(List<String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}
	public String getPaymentOptionName() {
		return paymentOptionName;
	}
	public void setPaymentOptionName(String paymentOptionName) {
		this.paymentOptionName = paymentOptionName;
	}
	
    
	
	
}