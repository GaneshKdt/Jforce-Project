package com.nmims.beans;

import java.io.Serializable;

public class ProgramSubjectMappingAcadsBean implements Serializable, Comparable<ProgramSubjectMappingAcadsBean>{
	/**
	 * Change Name from ProgramSubjectMappingBean to ProgramSubjectMappingAcadsBean for serializable issue
	 */
	private String program;
	private String sem;
	private String subject;
	private String prgmStructApplicable;
	private String assignmentSubmitted;
	private String canBook;
	private String canFreeBook;
	private String bookingStatus;
	private String centerName;
	
	//added because of programSubjectEntries :START
	private String activeStatus;
	private int id;
	private String hasAssignment;
	private String assignmentNeededBeforeWritten ;
	private String writtenScoreModel;
	private String assignmentScoreModel ;
	private String isGraceApplicable;
	private int maxGraceMarks;
	private String assignQueryToFaculty;
	private String createCaseForQuery;
	//END:
	
	private int consumerProgramStructureId;
	
	public String getCenterName() {
		if(centerName == null){
			return "";
		}else{
			return centerName;
		}
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHasAssignment() {
		return hasAssignment;
	}
	public void setHasAssignment(String hasAssignment) {
		this.hasAssignment = hasAssignment;
	}
	public String getAssignmentNeededBeforeWritten() {
		return assignmentNeededBeforeWritten;
	}
	public void setAssignmentNeededBeforeWritten(
			String assignmentNeededBeforeWritten) {
		this.assignmentNeededBeforeWritten = assignmentNeededBeforeWritten;
	}
	public String getWrittenScoreModel() {
		return writtenScoreModel;
	}
	public void setWrittenScoreModel(String writtenScoreModel) {
		this.writtenScoreModel = writtenScoreModel;
	}
	public String getAssignmentScoreModel() {
		return assignmentScoreModel;
	}
	public void setAssignmentScoreModel(String assignmentScoreModel) {
		this.assignmentScoreModel = assignmentScoreModel;
	}
	public String getIsGraceApplicable() {
		return isGraceApplicable;
	}
	public void setIsGraceApplicable(String isGraceApplicable) {
		this.isGraceApplicable = isGraceApplicable;
	}
	public int getMaxGraceMarks() {
		return maxGraceMarks;
	}
	public void setMaxGraceMarks(int maxGraceMarks) {
		this.maxGraceMarks = maxGraceMarks;
	}
	public String getAssignQueryToFaculty() {
		return assignQueryToFaculty;
	}
	public void setAssignQueryToFaculty(String assignQueryToFaculty) {
		this.assignQueryToFaculty = assignQueryToFaculty;
	}
	public String getCreateCaseForQuery() {
		return createCaseForQuery;
	}
	public void setCreateCaseForQuery(String createCaseForQuery) {
		this.createCaseForQuery = createCaseForQuery;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCanFreeBook() {
		return canFreeBook;
	}
	public void setCanFreeBook(String canFreeBook) {
		this.canFreeBook = canFreeBook;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getCanBook() {
		return canBook;
	}
	public void setCanBook(String canBook) {
		this.canBook = canBook;
	}
	public String getAssignmentSubmitted() {
		return assignmentSubmitted;
	}
	public void setAssignmentSubmitted(String assignmentSubmitted) {
		this.assignmentSubmitted = assignmentSubmitted;
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
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}
	
	@Override
	public int compareTo(ProgramSubjectMappingAcadsBean bean) {
		return sem.compareTo(bean.sem);
	}
	public int getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(int consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

}
