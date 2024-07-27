package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class SessionReviewBean extends BaseAcadsBean  implements Serializable {

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	public static final String REVIEWED = "Reviewed";
	public static final String NOT_REVIEWED = "Not Reviewed";
	private String sessionId;
	private String sessionName;
	private String subject;
	private String reviewerFacultyId;
	private String q1Response;
	private String q2Response;
	private String q3Response;
	private String q4Response;
	private String q5Response;
	private String q6Response;
	private String q7Response;
	private String q8Response;
	private String q1Remarks;
	private String q2Remarks;
	private String q3Remarks;
	private String q4Remarks;
	private String q5Remarks;
	private String q6Remarks;
	private String q7Remarks;
	private String q8Remarks;
	private String reviewed;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String facultyId;
	private String reviewerName;
	private String month;
	private String year;
    private String firstName;
    private String lastName;
	private String q1Average;
	private String q2Average;
	private String q3Average;
	private String q4Average;
	private String q5Average;
	private String q6Average;
	private String peerReviewAvg;
	private String date;
	private String startTime;
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getPeerReviewAvg() {
		return ((Integer.parseInt(q5Response)+Integer.parseInt(q6Response))/2)+ "";
	}
	public void setPeerReviewAvg(String peerReviewAvg) {
		this.peerReviewAvg = peerReviewAvg;
	}
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getReviewerFacultyId() {
		return reviewerFacultyId;
	}
	public void setReviewerFacultyId(String reviewerFacultyId) {
		this.reviewerFacultyId = reviewerFacultyId;
	}
	public String getQ1Response() {
		return q1Response;
	}
	public void setQ1Response(String q1Response) {
		this.q1Response = q1Response;
	}
	public String getQ2Response() {
		return q2Response;
	}
	public void setQ2Response(String q2Response) {
		this.q2Response = q2Response;
	}
	public String getQ3Response() {
		return q3Response;
	}
	public void setQ3Response(String q3Response) {
		this.q3Response = q3Response;
	}
	public String getQ4Response() {
		return q4Response;
	}
	public void setQ4Response(String q4Response) {
		this.q4Response = q4Response;
	}
	public String getQ5Response() {
		return q5Response;
	}
	public void setQ5Response(String q5Response) {
		this.q5Response = q5Response;
	}
	public String getQ6Response() {
		return q6Response;
	}
	public void setQ6Response(String q6Response) {
		this.q6Response = q6Response;
	}
	public String getQ7Response() {
		return q7Response;
	}
	public void setQ7Response(String q7Response) {
		this.q7Response = q7Response;
	}
	public String getQ8Response() {
		return q8Response;
	}
	public void setQ8Response(String q8Response) {
		this.q8Response = q8Response;
	}
	public String getQ1Remarks() {
		return q1Remarks;
	}
	public void setQ1Remarks(String q1Remarks) {
		this.q1Remarks = q1Remarks;
	}
	public String getQ2Remarks() {
		return q2Remarks;
	}
	public void setQ2Remarks(String q2Remarks) {
		this.q2Remarks = q2Remarks;
	}
	public String getQ3Remarks() {
		return q3Remarks;
	}
	public void setQ3Remarks(String q3Remarks) {
		this.q3Remarks = q3Remarks;
	}
	public String getQ4Remarks() {
		return q4Remarks;
	}
	public void setQ4Remarks(String q4Remarks) {
		this.q4Remarks = q4Remarks;
	}
	public String getQ5Remarks() {
		return q5Remarks;
	}
	public void setQ5Remarks(String q5Remarks) {
		this.q5Remarks = q5Remarks;
	}
	public String getQ6Remarks() {
		return q6Remarks;
	}
	public void setQ6Remarks(String q6Remarks) {
		this.q6Remarks = q6Remarks;
	}
	public String getQ7Remarks() {
		return q7Remarks;
	}
	public void setQ7Remarks(String q7Remarks) {
		this.q7Remarks = q7Remarks;
	}
	public String getQ8Remarks() {
		return q8Remarks;
	}
	public void setQ8Remarks(String q8Remarks) {
		this.q8Remarks = q8Remarks;
	}
	
	public String getReviewed() {
		return reviewed;
	}
	public void setReviewed(String reviewed) {
		this.reviewed = reviewed;
	}
	
	@Override
	public String toString() {
		return "SessionReviewBean [sessionId=" + sessionId + ", subject="
				+ subject + ", reviewerFacultyId=" + reviewerFacultyId
				+ ", q1Response=" + q1Response + ", q2Response=" + q2Response
				+ ", q3Response=" + q3Response + ", q4Response=" + q4Response
				+ ", q5Response=" + q5Response + ", q6Response=" + q6Response
				+ ", q7Response=" + q7Response + ", q8Response=" + q8Response
				+ ", q1Remarks=" + q1Remarks + ", q2Remarks=" + q2Remarks
				+ ", q3Remarks=" + q3Remarks + ", q4Remarks=" + q4Remarks
				+ ", q5Remarks=" + q5Remarks + ", q6Remarks=" + q6Remarks
				+ ", q7Remarks=" + q7Remarks + ", q8Remarks=" + q8Remarks
				+ ", reviewed=" + reviewed + ", errorMessage=" + errorMessage
				+ ", errorRecord=" + errorRecord + ", facultyId=" + facultyId
				+ ", reviewerName=" + reviewerName + ", month=" + month
				+ ", year=" + year + "]";
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
	
	public String getQ1Average(){
		return q1Average;
	}
	public void setQ1Average(String q1Average){
		this.q1Average = q1Average;
	}
	
	public String getQ2Average(){
		return q2Average;
	}
	public void setQ2Average(String q2Average){
		this.q2Average = q2Average;
	}
	
	public String getQ3Average(){
		return q3Average;
	}
	public void setQ3Average(String q3Average){
	    this.q3Average = q3Average;
	}
	
	public String getQ4Average(){
		return q4Average;
	}
	public void setQ4Average(String q4Average){
	     this.q4Average = q4Average;
	}
	
	public String getQ5Average(){
		return q5Average;
	}
	public void setQ5Average(String q5Average){
		this.q5Average = q5Average;
	}
	
	public String getQ6Average(){
		return q6Average;
	}
	public void setQ6Average(String q6Average){
		this.q6Average = q6Average;
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
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	
}
