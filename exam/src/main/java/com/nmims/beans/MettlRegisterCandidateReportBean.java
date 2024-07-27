/**
 * 
 */
package com.nmims.beans;

/**
 * @author vil_m 
 *
 */
public class MettlRegisterCandidateReportBean extends MettlRegisterCandidateBean {
	//Error Details
	private String assessmentId;
	
	//Summary Details
	private String totalCandidatesExamBookings;
	private String totalCandidates;
	private String successSlot1;
	private String errorSlot1;
	private String successSlot2;
	private String errorSlot2;
	private String successSlot3;
	private String errorSlot3;
	private String totalErrors;
	private String totalFailures;
	
	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getTotalCandidatesExamBookings() {
		return totalCandidatesExamBookings;
	}

	public void setTotalCandidatesExamBookings(String totalCandidatesExamBookings) {
		this.totalCandidatesExamBookings = totalCandidatesExamBookings;
	}

	public String getTotalCandidates() {
		return totalCandidates;
	}

	public void setTotalCandidates(String totalCandidates) {
		this.totalCandidates = totalCandidates;
	}

	public String getSuccessSlot1() {
		return successSlot1;
	}

	public void setSuccessSlot1(String successSlot1) {
		this.successSlot1 = successSlot1;
	}

	public String getErrorSlot1() {
		return errorSlot1;
	}

	public void setErrorSlot1(String errorSlot1) {
		this.errorSlot1 = errorSlot1;
	}

	public String getSuccessSlot2() {
		return successSlot2;
	}

	public void setSuccessSlot2(String successSlot2) {
		this.successSlot2 = successSlot2;
	}

	public String getErrorSlot2() {
		return errorSlot2;
	}

	public void setErrorSlot2(String errorSlot2) {
		this.errorSlot2 = errorSlot2;
	}

	public String getSuccessSlot3() {
		return successSlot3;
	}

	public void setSuccessSlot3(String successSlot3) {
		this.successSlot3 = successSlot3;
	}

	public String getErrorSlot3() {
		return errorSlot3;
	}

	public void setErrorSlot3(String errorSlot3) {
		this.errorSlot3 = errorSlot3;
	}

	public String getTotalErrors() {
		return totalErrors;
	}

	public void setTotalErrors(String totalErrors) {
		this.totalErrors = totalErrors;
	}

	public String getTotalFailures() {
		return totalFailures;
	}

	public void setTotalFailures(String totalFailures) {
		this.totalFailures = totalFailures;
	}
	
}