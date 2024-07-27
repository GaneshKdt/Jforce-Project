
package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

//spring security related changes rename ProgramSubjectMappingBean to ProgramSubjectMappingExamBean
public class ProgramSubjectMappingExamBean extends BaseExamBean implements Serializable{
	private String program;
	private String sem;
	private String subject;
	private String prgmStructApplicable;
	private String assignmentSubmitted;
	private String canBook;
	private String canFreeBook;
	private String bookingStatus;
	private String centerName;
	private String examFees;
	private int passScore;
	private String active;
	
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
	private int sifySubjectCode;
	private String hasTest;

	// hasIA = has-Internal-Assessmeent, It means subject is applicable for assignment/MCQ Test/viva/something else
	// hasIA has to be checked alongwith hasAssignments/hasTest
	private String hasIA;
	private String consumerProgramStructureId;

	private String consumerType;
	private String description;
	
	private int sessionTime;

	//END:
	
	private Double subjectCredits;
	
	public int getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(int sessionTime) {
		this.sessionTime = sessionTime;
	}
	/**
	 * @return the hasIA
	 */
	public String getHasIA() {
		return hasIA;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param hasIA the hasIA to set
	 */
	public void setHasIA(String hasIA) {
		this.hasIA = hasIA;
	}

	private String pss_key;

	//MBA- WX
	private List<StudentSubjectConfigExamBean> studentConfigFile;
	private String studentType;
	private String prgm_sem_subj_id;
	private String startDate;
	private String endDate;
	private String acadYear;
	private String acadMonth;
	private String consumerTypeId;
	private String programId;
	private String programStructureId;
	private int batchId;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;

	private String specializationType;
	private String specializationName;
	private String sequence;
	private String specializationTypeName;
	private String timeBoundId;
	private String examYear;
	private String examMonth;
	private Boolean isPrerequisite;
	private String parent;
	private String child;
	private String userId;
	private String subjectCount;
	
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getHasTest() {
		return hasTest;
	}
	public void setHasTest(String hasTest) {
		this.hasTest = hasTest;
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
	public String getAssignmentNeededBeforeWritten() {
		return assignmentNeededBeforeWritten;
	}
	public void setAssignmentNeededBeforeWritten(String assignmentNeededBeforeWritten) {
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
	public String getHasAssignment() {
		return hasAssignment;
	}
	public void setHasAssignment(String hasAssignment) {
		this.hasAssignment = hasAssignment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public Integer getPassScore() {
		return passScore;
	}
	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getExamFees() {
		return examFees;
	}
	public void setExamFees(String examFees) {
		this.examFees = examFees;
	}
	public String getCenterName() {
		if(centerName == null){
			return "";
		}else{
			return centerName;
		}
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
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public int getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(int sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}

	public String getPss_key() {
		return pss_key;
	}
	public void setPss_key(String pss_key) {
		this.pss_key = pss_key;
	}



	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
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
	
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
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
	public List<StudentSubjectConfigExamBean> getStudentConfigFile() {
		return studentConfigFile;
	}
	public void setStudentConfigFile(List<StudentSubjectConfigExamBean> studentConfigFile) {
		this.studentConfigFile = studentConfigFile;
	}
	public String getSpecializationType() {
		return specializationType;
	}
	public void setSpecializationType(String specializationType) {
		this.specializationType = specializationType;
	}
	public String getSpecializationName() {
		return specializationName;
	}
	public void setSpecializationName(String specializationName) {
		this.specializationName = specializationName;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public String getSpecializationTypeName() {
		return specializationTypeName;
	}
	public void setSpecializationTypeName(String specializationTypeName) {
		this.specializationTypeName = specializationTypeName;
	}
	public String getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(String timeBoundId) {
		this.timeBoundId = timeBoundId;
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
	public Boolean getIsPrerequisite() {
		return isPrerequisite;
	}
	public void setIsPrerequisite(Boolean isPrerequisite) {
		this.isPrerequisite = isPrerequisite;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSubjectCount() {
		return subjectCount;
	}
	public void setSubjectCount(String subjectCount) {
		this.subjectCount = subjectCount;
	}
	public Double getSubjectCredits() {
		return subjectCredits;
	}
	public void setSubjectCredits(Double subjectCredits) {
		this.subjectCredits = subjectCredits;
	}
	@Override
	public String toString() {
		return "ProgramSubjectMappingExamBean [program=" + program + ", sem=" + sem + ", subject=" + subject
				+ ", prgmStructApplicable=" + prgmStructApplicable + ", assignmentSubmitted=" + assignmentSubmitted
				+ ", canBook=" + canBook + ", canFreeBook=" + canFreeBook + ", bookingStatus=" + bookingStatus
				+ ", centerName=" + centerName + ", examFees=" + examFees + ", passScore=" + passScore + ", active="
				+ active + ", activeStatus=" + activeStatus + ", id=" + id + ", hasAssignment=" + hasAssignment
				+ ", assignmentNeededBeforeWritten=" + assignmentNeededBeforeWritten + ", writtenScoreModel="
				+ writtenScoreModel + ", assignmentScoreModel=" + assignmentScoreModel + ", isGraceApplicable="
				+ isGraceApplicable + ", maxGraceMarks=" + maxGraceMarks + ", assignQueryToFaculty="
				+ assignQueryToFaculty + ", createCaseForQuery=" + createCaseForQuery + ", sifySubjectCode="
				+ sifySubjectCode + ", hasTest=" + hasTest + ", hasIA=" + hasIA + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", consumerType=" + consumerType + ", description=" + description
				+ ", sessionTime=" + sessionTime + ", subjectCredits=" + subjectCredits + ", pss_key=" + pss_key
				+ ", studentConfigFile=" + studentConfigFile + ", studentType=" + studentType + ", prgm_sem_subj_id="
				+ prgm_sem_subj_id + ", startDate=" + startDate + ", endDate=" + endDate + ", acadYear=" + acadYear
				+ ", acadMonth=" + acadMonth + ", consumerTypeId=" + consumerTypeId + ", programId=" + programId
				+ ", programStructureId=" + programStructureId + ", batchId=" + batchId + ", createdBy=" + createdBy
				+ ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + ", specializationType=" + specializationType + ", specializationName="
				+ specializationName + ", sequence=" + sequence + ", specializationTypeName=" + specializationTypeName
				+ ", timeBoundId=" + timeBoundId + ", examYear=" + examYear + ", examMonth=" + examMonth
				+ ", isPrerequisite=" + isPrerequisite + ", parent=" + parent + ", child=" + child + ", userId="
				+ userId + ", subjectCount=" + subjectCount + "]";
	}

}
