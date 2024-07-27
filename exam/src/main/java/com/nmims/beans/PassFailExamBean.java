package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename PassFailBean to PassFailExamBean
public class PassFailExamBean implements Serializable {

	private String sapid;
	private String subject;
	private String grno;
	private String writtenYear;
	private String writtenMonth;
	private String assignmentYear;
	private String assignmentMonth;
	private String name;
	private String program;
	private String sem;
	private String writtenscore;
	private String assignmentscore;
	private String total;
	private String failReason;
    private String remarks;
    private String isPass;
	private String gracemarks;
	private double assignmentAttemptOrder;
	private double writtenAttemptOrder;
    private String graceGiven;
    private String assignmentSubmitted;
	private String examMode;
    private String completionYear;
    private String completionMonth;
	private String serviceRequestIdList;
    private String centerCode;
    private String validityEndMonth;
    private String validityEndYear;
    private String serviceRequestId;
    private String subjectCutoffCleared;
    private String prgmStructApplicable;
    private String gender;
    private String obtainedTotalMarks;
    private String outOfMarks;
    private String passPercentage;
    private String attempt;
    
    private String studentType;
    
    private String latestTeeClearingDate;

    private String latestAssignClearingDate;
    
    
    private String lastModifiedBy;
    private String lastWrittenscore;
	private String lastAssignmentscore;
    
    private String sifySubjectCode;

    private String assignmentRemarks;

    

	private String resultProcessedYear;
	private String resultProcessedMonth;
	
	private String consumerType;
	
	private String oldIsPassStatus;
    

	private String batchMonth;
	private String batchYear;
	private String examYear;
	private String examMonth;

	
	 private String year;
	 private String month;
	 
	 private String logoRequired;
	 private String grade;
	 
	 private boolean isWaivedOff;
	 
	 private String oldProgram;
	 
	 public String getOldProgram() {
		return oldProgram;
	}
	public void setOldProgram(String oldProgram) {
		this.oldProgram = oldProgram;
	}
	public String getLogoRequired() {
		return logoRequired;
	}
	public void setLogoRequired(String logoRequired) {
		this.logoRequired = logoRequired;
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
		
		
	/**
	 * @return the consumerType
	 */
	public String getConsumerType() {
		return consumerType;
	}
	/**
	 * @param consumerType the consumerType to set
	 */
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	/**
	 * @return the oldIsPassStatus
	 */
	public String getOldIsPassStatus() {
		return oldIsPassStatus;
	}
	/**
	 * @param oldIsPassStatus the oldIsPassStatus to set
	 */
	public void setOldIsPassStatus(String oldIsPassStatus) {
		this.oldIsPassStatus = oldIsPassStatus;
	}
	/**
	 * @return the resultProcessedYear
	 */
	public String getResultProcessedYear() {
		return resultProcessedYear;
	}
	/**
	 * @param resultProcessedYear the resultProcessedYear to set
	 */
	public void setResultProcessedYear(String resultProcessedYear) {
		this.resultProcessedYear = resultProcessedYear;
	}
	/**
	 * @return the resultProcessedMonth
	 */
	public String getResultProcessedMonth() {
		return resultProcessedMonth;
	}
	/**
	 * @param resultProcessedMonth the resultProcessedMonth to set
	 */
	public void setResultProcessedMonth(String resultProcessedMonth) {
		this.resultProcessedMonth = resultProcessedMonth;
	}
	/**
	 * @return the sifySubjectCode
	 */
	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	/**
	 * @param sifySubjectCode the sifySubjectCode to set
	 */
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}
	/**
	 * @return the latestTeeClearingDate
	 */
	public String getLatestTeeClearingDate() {
		return latestTeeClearingDate;
	}
	/**
	 * @param latestTeeClearingDate the latestTeeClearingDate to set
	 */
	public void setLatestTeeClearingDate(String latestTeeClearingDate) {
		this.latestTeeClearingDate = latestTeeClearingDate;
	}
	/**
	 * @return the latestAssignClearingDate
	 */
	public String getLatestAssignClearingDate() {
		return latestAssignClearingDate;
	}
	/**
	 * @param latestAssignClearingDate the latestAssignClearingDate to set
	 */
	public void setLatestAssignClearingDate(String latestAssignClearingDate) {
		this.latestAssignClearingDate = latestAssignClearingDate;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}
	public String getSubjectCutoffCleared() {
		return subjectCutoffCleared;
	}
	public void setSubjectCutoffCleared(String subjectCutoffCleared) {
		this.subjectCutoffCleared = subjectCutoffCleared;
	}
	public String getServiceRequestId() {
		return serviceRequestId;
	}
	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getServiceRequestIdList() {
		return serviceRequestIdList;
	}
	public void setServiceRequestIdList(String serviceRequestIdList) {
		this.serviceRequestIdList = serviceRequestIdList;
	}
	public String getCompletionYear() {
		return completionYear;
	}
	public void setCompletionYear(String completionYear) {
		this.completionYear = completionYear;
	}
	public String getCompletionMonth() {
		return completionMonth;
	}
	public void setCompletionMonth(String completionMonth) {
		this.completionMonth = completionMonth;
	}
	public String getExamMode() {
		return examMode;
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	public String getAssignmentSubmitted() {
		return assignmentSubmitted;
	}
	public void setAssignmentSubmitted(String assignmentSubmitted) {
		this.assignmentSubmitted = assignmentSubmitted;
	}
	public String getGraceGiven() {
		return graceGiven;
	}
	public void setGraceGiven(String graceGiven) {
		this.graceGiven = graceGiven;
	}
	public double getWrittenAttemptOrder() {
		return writtenAttemptOrder;
	}
	public void setWrittenAttemptOrder(double writtenAttemptOrder) {
		this.writtenAttemptOrder = writtenAttemptOrder;
	}
	public double getAssignmentAttemptOrder() {
		return assignmentAttemptOrder;
	}
	public void setAssignmentAttemptOrder(double assignmentAttemptOrder) {
		this.assignmentAttemptOrder = assignmentAttemptOrder;
	}
	public String getGracemarks() {
		return gracemarks;
	}
	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getSapid() {
		return sapid;
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
	public String getGrno() {
		return grno;
	}
	public void setGrno(String grno) {
		this.grno = grno;
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
	public String getWrittenscore() {
		return writtenscore;
	}
	public void setWrittenscore(String writtenscore) {
		this.writtenscore = writtenscore;
	}
	public String getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getWrittenYear() {
		return writtenYear;
	}
	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}
	public String getWrittenMonth() {
		return writtenMonth;
	}
	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
	}
	public String getAssignmentYear() {
		return assignmentYear;
	}
	public void setAssignmentYear(String assignmentYear) {
		this.assignmentYear = assignmentYear;
	}
	public String getAssignmentMonth() {
		return assignmentMonth;
	}
	public void setAssignmentMonth(String assignmentMonth) {
		this.assignmentMonth = assignmentMonth;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getObtainedTotalMarks() {
		return obtainedTotalMarks;
	}
	public void setObtainedTotalMarks(String obtainedTotalMarks) {

		this.obtainedTotalMarks = obtainedTotalMarks;
	}
	public String getOutOfMarks() {
		return outOfMarks;
	}
	public void setOutOfMarks(String outOfMarks) {
		
		this.outOfMarks = outOfMarks;
	}
	public String getPassPercentage() {
		return passPercentage;
	}
	public void setPassPercentage(String passPercentage) {
		this.passPercentage = passPercentage;
	}
	public String getAttempt() {
		return attempt;
	}
	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastWrittenscore() {
		return lastWrittenscore;
	}
	public void setLastWrittenscore(String lastWrittenscore) {
		this.lastWrittenscore = lastWrittenscore;
	}
	public String getLastAssignmentscore() {
		return lastAssignmentscore;
	}
	public void setLastAssignmentscore(String lastAssignmentscore) {
		this.lastAssignmentscore = lastAssignmentscore;
	}

	public String getAssignmentRemarks() {
		return assignmentRemarks;
	}
	public void setAssignmentRemarks(String assignmentRemarks) {
		this.assignmentRemarks = assignmentRemarks;
	}
	public String getBatchMonth() {
		return batchMonth;
	}
	public void setBatchMonth(String batchMonth) {
		this.batchMonth = batchMonth;
	}
	public String getBatchYear() {
		return batchYear;
	}
	public void setBatchYear(String batchYear) {
		this.batchYear = batchYear;

	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public boolean isWaivedOff() {
		return isWaivedOff;
	}
	public void setWaivedOff(boolean isWaivedOff) {
		this.isWaivedOff = isWaivedOff;
	}
	
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
	//Added by shivam.pandey.EXT - START
	private String resultDeclaredDate;
	
	public String getResultDeclaredDate() {
		return resultDeclaredDate;
	}
	public void setResultDeclaredDate(String resultDeclaredDate) {
		this.resultDeclaredDate = resultDeclaredDate;
	}
	//Added by shivam.pandey.EXT - END
	@Override
	public String toString() {
		return "PassFailExamBean [sapid=" + sapid + ", subject=" + subject + ", grno=" + grno + ", writtenYear="
				+ writtenYear + ", writtenMonth=" + writtenMonth + ", assignmentYear=" + assignmentYear
				+ ", assignmentMonth=" + assignmentMonth + ", name=" + name + ", program=" + program + ", sem=" + sem
				+ ", writtenscore=" + writtenscore + ", assignmentscore=" + assignmentscore + ", total=" + total
				+ ", failReason=" + failReason + ", remarks=" + remarks + ", isPass=" + isPass + ", gracemarks="
				+ gracemarks + ", assignmentAttemptOrder=" + assignmentAttemptOrder + ", writtenAttemptOrder="
				+ writtenAttemptOrder + ", graceGiven=" + graceGiven + ", assignmentSubmitted=" + assignmentSubmitted
				+ ", examMode=" + examMode + ", completionYear=" + completionYear + ", completionMonth="
				+ completionMonth + ", serviceRequestIdList=" + serviceRequestIdList + ", centerCode=" + centerCode
				+ ", validityEndMonth=" + validityEndMonth + ", validityEndYear=" + validityEndYear
				+ ", serviceRequestId=" + serviceRequestId + ", subjectCutoffCleared=" + subjectCutoffCleared
				+ ", prgmStructApplicable=" + prgmStructApplicable + ", gender=" + gender + ", obtainedTotalMarks="
				+ obtainedTotalMarks + ", outOfMarks=" + outOfMarks + ", passPercentage=" + passPercentage
				+ ", attempt=" + attempt + ", studentType=" + studentType + ", latestTeeClearingDate="
				+ latestTeeClearingDate + ", latestAssignClearingDate=" + latestAssignClearingDate + ", lastModifiedBy="
				+ lastModifiedBy + ", lastWrittenscore=" + lastWrittenscore + ", lastAssignmentscore="
				+ lastAssignmentscore + ", sifySubjectCode=" + sifySubjectCode + ", assignmentRemarks="
				+ assignmentRemarks + ", resultProcessedYear=" + resultProcessedYear + ", resultProcessedMonth="
				+ resultProcessedMonth + ", consumerType=" + consumerType + ", oldIsPassStatus=" + oldIsPassStatus
				+ ", batchMonth=" + batchMonth + ", batchYear=" + batchYear + ", examYear=" + examYear + ", examMonth="
				+ examMonth + ", year=" + year + ", month=" + month + ", logoRequired=" + logoRequired + ", grade="
				+ grade + ", isWaivedOff=" + isWaivedOff + "]";
	}
	
}
