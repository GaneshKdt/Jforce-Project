package com.nmims.beans;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class Specialisation implements Serializable {

	private Long id;
	private String specializationType;
	private String specializationInitials;
	private String specialisation1;
	private String specialisation2;
	private String term;
	private String sapid;
	private String name;
	private String emailId;
	private String mobile;
	private String subject_specialization;
	private LinkedList<ProgramSubjectMappingExamBean> specialisationSubjectList;
	private String subject;
	private String consumerProgramStructureId;
	private String program_sem_subject_id;
	private String timeBoundId;
	private boolean serviceRequest =  false;
	private String sfdcProgram1;
	private String sfdcProgram2;
	private String acadYear;
	private String acadMonth;
	private String nextYear;
	private String nextMonth;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String specialization;
	private boolean paymentApplicable;
	private int amount;
	private String batchName;
	private String batchId;
	private Integer block;
	private Integer sequence;
	private Integer sem;
	private Boolean isCoreSubject;
	private Boolean hasPrerequisite;
	private String prerequisite;
	private String userId;
	private String message;
	private Boolean status;
	private Integer maxSequenceInBlock;
	private Boolean isReSelect;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSpecializationType() {
		return specializationType;
	}
	public void setSpecializationType(String specializationType) {
		this.specializationType = specializationType;
	}
	public String getSpecializationInitials() {
		return specializationInitials;
	}
	public void setSpecializationInitials(String specializationInitials) {
		this.specializationInitials = specializationInitials;
	}
	public String getSpecialisation1() {
		return specialisation1;
	}
	public void setSpecialisation1(String specialisation1) {
		this.specialisation1 = specialisation1;
	}
	public String getSpecialisation2() {
		return specialisation2;
	}
	public void setSpecialisation2(String specialisation2) {
		this.specialisation2 = specialisation2;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSapid() {
		return sapid; 
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getSubject_specialization() {
		return subject_specialization;
	}
	public void setSubject_specialization(String subject_specialization) {
		this.subject_specialization = subject_specialization;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(String program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(String timeBoundId) {
		this.timeBoundId = timeBoundId;
	}
	public boolean isServiceRequest(){
		return this.serviceRequest;
	}
	public void setServiceRequest(boolean serviceRequest){
		this.serviceRequest = serviceRequest;
	}
	public String getSfdcProgram1() {
		return sfdcProgram1;
	}
	public void setSfdcProgram1(String sfdcProgram1) {
		this.sfdcProgram1 = sfdcProgram1;
	}
	public String getSfdcProgram2() {
		return sfdcProgram2;
	}
	public void setSfdcProgram2(String sfdcProgram2) {
		this.sfdcProgram2 = sfdcProgram2;
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
	public String getNextYear() {
		return nextYear;
	}
	public void setNextYear(String nextYear) {
		this.nextYear = nextYear;
	}
	public String getNextMonth() {
		return nextMonth;
	}
	public void setNextMonth(String nextMonth) {
		this.nextMonth = nextMonth;
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
	public LinkedList<ProgramSubjectMappingExamBean> getSpecialisationSubjectList() {
		return specialisationSubjectList;
	}
	public void setSpecialisationSubjectList(LinkedList<ProgramSubjectMappingExamBean> specialisationSubjectList) {
		this.specialisationSubjectList = specialisationSubjectList;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public boolean isPaymentApplicable() {
		return paymentApplicable;
	}
	public void setPaymentApplicable(boolean paymentApplicable) {
		this.paymentApplicable = paymentApplicable;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public Integer getBlock() {
		return block;
	}
	public void setBlock(Integer block) {
		this.block = block;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getSem() {
		return sem;
	}
	public void setSem(Integer sem) {
		this.sem = sem;
	}
	public Boolean getIsCoreSubject() {
		return isCoreSubject;
	}
	public void setIsCoreSubject(Boolean isCoreSubject) {
		this.isCoreSubject = isCoreSubject;
	}
	public Boolean getHasPrerequisite() {
		return hasPrerequisite;
	}
	public void setHasPrerequisite(Boolean hasPrerequisite) {
		this.hasPrerequisite = hasPrerequisite;
	}
	public String getPrerequisite() {
		return prerequisite;
	}
	public void setPrerequisite(String prerequisite) {
		this.prerequisite = prerequisite;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Integer getMaxSequenceInBlock() {
		return maxSequenceInBlock;
	}
	public void setMaxSequenceInBlock(Integer maxSequenceInBlock) {
		this.maxSequenceInBlock = maxSequenceInBlock;
	}
	public Boolean getIsReSelect() {
		return isReSelect;
	}
	public void setIsReSelect(Boolean isReSelect) {
		this.isReSelect = isReSelect;
	}
	@Override
	public String toString() {
		return "Specialisation [specializationType=" + specializationType + ", specialisation1=" + specialisation1
				+ ", specialisation2=" + specialisation2 + ", term=" + term + ", sapid=" + sapid + ", subject="
				+ subject + ", consumerProgramStructureId=" + consumerProgramStructureId + ", program_sem_subject_id="
				+ program_sem_subject_id + ", timeBoundId=" + timeBoundId + ", serviceRequest=" + serviceRequest
				+ ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", specialization=" + specialization
				+ ", paymentApplicable=" + paymentApplicable + ", block=" + block + ", sequence=" + sequence + ", sem="
				+ sem + ", isCoreSubject=" + isCoreSubject + ", hasPrerequisite=" + hasPrerequisite + ", prerequisite="
				+ prerequisite + ", userId=" + userId + ", status=" + status + ", isReSelect=" + isReSelect + "]";
	}
	
	private String programStructre;

	public String getProgramStructre() {
		return programStructre;
	}
	public void setProgramStructre(String programStructre) {
		this.programStructre = programStructre;
	}
	
	
}
