package com.nmims.beans;

import java.io.Serializable;

public class RedisStudentMarksBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String year;
	private String month;
	private String syllabusYear;
	private String examorder;
	private String grno;
	private String sapid;
	private String studentname;
	private String program;
	private String sem;
	private String subject;
	private String writenscore;
	private String assignmentscore;
	private String gracemarks;
	private String total;
	private String attempt;
	private String source;
	private String location;
	private String centercode;
	private String remarks;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String examMode;
	private String offlineCenterId;
	private String onlineCenterId;
	private String centerId;
	private String firstName;
	private String lastName;
	private String examCenterName;
	private String city;
	private String subjetsCleared;
	private String reason;
	private String markedForRevaluation;
	private String revaluationRemarks;
	private String revaluationScore;
	private String markedForPhotocopy;
	private String centerName;
	private String mcq;
	private String part4marks;
	private String oldWrittenScore;
	private String oldAssignmentScore;
	
	
	private String sapid1;
	private String sapid1FName;
	private String sapid1LName;
	private String program1;
	private String ic1;
	private String sapid2;
	private String sapid2FName;
	private String sapid2LName;
	private String program2;
	private String ic2;
	private String matchPercent;
	private String consecutiveLines;
	private String file1Lines;
	private String file2Lines;
	private String totalLinesMatched;
	private String assignmentBeforeReval;
	private String writtenBeforeReval;
	private String revaulationResultDeclared;
	
	//added for executive reports 
	private String password;
	private String examDate;
	private String examTime;
	private String booked;
	private String emailId;
	private String mobile;
	private String altPhone;
	private String programStructApplicable;
    private String enrollmentMonth;
    private String enrollmentYear;
	
	private String studentType;
	private String oldStatus;
	private String newStatus;
	private String newTotal;
	
	private String totalMarks;
	private String sifySubjectCode;
	private String writtenBeforeRIANV;

	private String consumerType;
	private String consumerProgramStructureId;
	private String writtenscore;
	
	private String testTaken;
	
	
	
	public String getTestTaken() {
		return testTaken;
	}
	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
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
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getProgramStructApplicable() {
		return programStructApplicable;
	}
	public void setProgramStructApplicable(String programStructApplicable) {
		this.programStructApplicable = programStructApplicable;
	}

	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAltPhone() {
		return altPhone;
	}
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
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
	public String getOldWrittenScore() {
		return oldWrittenScore;
	}
	public void setOldWrittenScore(String oldWrittenScore) {
		this.oldWrittenScore = oldWrittenScore;
	}
	public String getOldAssignmentScore() {
		return oldAssignmentScore;
	}
	public void setOldAssignmentScore(String oldAssignmentScore) {
		this.oldAssignmentScore = oldAssignmentScore;
	}
	public String getRevaulationResultDeclared() {
		return revaulationResultDeclared;
	}
	public void setRevaulationResultDeclared(String revaulationResultDeclared) {
		this.revaulationResultDeclared = revaulationResultDeclared;
	}
	public String getAssignmentBeforeReval() {
		return assignmentBeforeReval;
	}
	public void setAssignmentBeforeReval(String assignmentBeforeReval) {
		this.assignmentBeforeReval = assignmentBeforeReval;
	}
	public String getWrittenBeforeReval() {
		return writtenBeforeReval;
	}
	public void setWrittenBeforeReval(String writtenBeforeReval) {
		this.writtenBeforeReval = writtenBeforeReval;
	}
	public String getSapid1() {
		return sapid1;
	}
	public void setSapid1(String sapid1) {
		this.sapid1 = sapid1;
	}
	public String getSapid1FName() {
		return sapid1FName;
	}
	public void setSapid1FName(String sapid1fName) {
		sapid1FName = sapid1fName;
	}
	public String getSapid1LName() {
		return sapid1LName;
	}
	public void setSapid1LName(String sapid1lName) {
		sapid1LName = sapid1lName;
	}
	public String getProgram1() {
		return program1;
	}
	public void setProgram1(String program1) {
		this.program1 = program1;
	}
	public String getIc1() {
		return ic1;
	}
	public void setIc1(String ic1) {
		this.ic1 = ic1;
	}
	public String getSapid2() {
		return sapid2;
	}
	public void setSapid2(String sapid2) {
		this.sapid2 = sapid2;
	}
	public String getSapid2FName() {
		return sapid2FName;
	}
	public void setSapid2FName(String sapid2fName) {
		sapid2FName = sapid2fName;
	}
	public String getSapid2LName() {
		return sapid2LName;
	}
	public void setSapid2LName(String sapid2lName) {
		sapid2LName = sapid2lName;
	}
	public String getProgram2() {
		return program2;
	}
	public void setProgram2(String program2) {
		this.program2 = program2;
	}
	public String getIc2() {
		return ic2;
	}
	public void setIc2(String ic2) {
		this.ic2 = ic2;
	}
	public String getMatchPercent() {
		return matchPercent;
	}
	public void setMatchPercent(String matchPercent) {
		this.matchPercent = matchPercent;
	}
	public String getConsecutiveLines() {
		return consecutiveLines;
	}
	public void setConsecutiveLines(String consecutiveLines) {
		this.consecutiveLines = consecutiveLines;
	}
	public String getFile1Lines() {
		return file1Lines;
	}
	public void setFile1Lines(String file1Lines) {
		this.file1Lines = file1Lines;
	}
	public String getFile2Lines() {
		return file2Lines;
	}
	public void setFile2Lines(String file2Lines) {
		this.file2Lines = file2Lines;
	}
	public String getTotalLinesMatched() {
		return totalLinesMatched;
	}
	public void setTotalLinesMatched(String totalLinesMatched) {
		this.totalLinesMatched = totalLinesMatched;
	}
	public String getMcq() {
		return mcq;
	}
	public void setMcq(String mcq) {
		this.mcq = mcq;
	}
	public String getPart4marks() {
		return part4marks;
	}
	public void setPart4marks(String part4marks) {
		this.part4marks = part4marks;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getMarkedForPhotocopy() {
		return markedForPhotocopy;
	}
	public void setMarkedForPhotocopy(String markedForPhotocopy) {
		this.markedForPhotocopy = markedForPhotocopy;
	}
	public String getRevaluationRemarks() {
		return revaluationRemarks;
	}
	public void setRevaluationRemarks(String revaluationRemarks) {
		this.revaluationRemarks = revaluationRemarks;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMarkedForRevaluation() {
		return markedForRevaluation;
	}
	public void setMarkedForRevaluation(String markedForRevaluation) {
		this.markedForRevaluation = markedForRevaluation;
	}
	public String getSubjetsCleared() {
		return subjetsCleared;
	}
	public void setSubjetsCleared(String subjetsCleared) {
		this.subjetsCleared = subjetsCleared;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	public String getOfflineCenterId() {
		return offlineCenterId;
	}
	public void setOfflineCenterId(String offlineCenterId) {
		this.offlineCenterId = offlineCenterId;
	}
	public String getOnlineCenterId() {
		return onlineCenterId;
	}
	public void setOnlineCenterId(String onlineCenterId) {
		this.onlineCenterId = onlineCenterId;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getExamMode() {
		return examMode;
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getSyllabusYear() {
		return syllabusYear;
	}
	public void setSyllabusYear(String syllabusYear) {
		this.syllabusYear = syllabusYear;
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
	public String getExamorder() {
		return examorder;
	}
	public void setExamorder(String examorder) {
		this.examorder = examorder;
	}
	public String getGrno() {
		return grno;
	}
	public void setGrno(String grno) {
		this.grno = grno;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getStudentname() {
		return studentname;
	}
	public void setStudentname(String studentname) {
		this.studentname = studentname;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getWritenscore() {
		return writenscore;
	}
	public void setWritenscore(String writenscore) {
		this.writenscore = writenscore;
	}
	public String getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public String getGracemarks() {
		return gracemarks;
	}
	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getAttempt() {
		return attempt;
	}
	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCentercode() {
		return centercode;
	}
	public void setCentercode(String centercode) {
		this.centercode = centercode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "StudentMarksBean [id=" + id + ", year=" + year + ", month="
				+ month + ", grno=" + grno + ", sapid=" + sapid
				+ ", studentname=" + studentname + ", program=" + program
				+ ", sem=" + sem + ", subject=" + subject + ", writenscore="
				+ writenscore + ", assignmentscore=" + assignmentscore
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + "]";
	}
	public String getRevaluationScore() {
		return revaluationScore;
	}
	public void setRevaluationScore(String revaluationScore) {
		this.revaluationScore = revaluationScore;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getOldStatus() {
		return oldStatus;
	}
	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}
	public String getNewStatus() {
		return newStatus;
	}
	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	public String getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(String totalMarks) {
		this.totalMarks = totalMarks;
	}
	public String getNewTotal() {
		return newTotal;
	}
	public void setNewTotal(String newTotal) {
		this.newTotal = newTotal;
	}
	public String getWrittenBeforeRIANV() {
		return writtenBeforeRIANV;
	}
	public void setWrittenBeforeRIANV(String writtenBeforeRIANV) {
		this.writtenBeforeRIANV = writtenBeforeRIANV;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getWrittenscore() {
		return writtenscore;
	}
	public void setWrittenscore(String writtenscore) {
		this.writtenscore = writtenscore;
	}

	
}