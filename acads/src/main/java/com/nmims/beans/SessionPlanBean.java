package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SessionPlanBean  implements Serializable {

	private Long id;
	private String sapid;
    private String title;
    private String subject;
    private String month;
    private Integer year;
    private String createdBy;
 	private String lastModifiedBy;
 	private String createdDate;
 	private String lastModifiedDate;
	
 	private Long consumerProgramStructureId;
 	private Long programSemSubjectId;
 	
 	

	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	
	

 
	private String consumerType;
	private String program;
	private String programStructure;
	private String programStructureDataId;
	
	private Integer countOfProgramsApplicableTo;
	
	private boolean allowedToUpdate;
	
	private int noOfClassroomSessions;
	private int noOf_Practical_Group_Work;
	private int noOfTutorials;
	private int noOfAssessments;
	
	private int continuousEvaluationPercentage;
	private int tEEPercentage;
	
	private String courseRationale;
	
	private String objectives;
	
	private String learningOutcomes;
	
	private String prerequisites;
	
	private String pedagogy;
	
	private String textbook;
	
	private String journals;
	
	private String links;
	
	private Long timeboundId;
	
 	private String batchName;
 	
 	private String type;
 	
 	private String pedagogyUsed;
 	private String casestudyName;
 	private String pedagogicalTool;
 	private String teachingMethod;
 	private String casestudySource;
 	private String casestudyType;
 	
// 	added following fields and methods for batch logic in manage session plan
 	private String applicableType;
 	private String referenceBatchOrModuleName;
 	private String referenceId;
 	
 	private String startDate;
 	private String acadMonth;
 	private String acadYear;
 	private List<String> listOfStringDateData;
 	
 	private String subjectCode;
 	
 	private String subjectCodeId;
 	
 	public String getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	public String getAcadYear() {
		return acadYear;
	}

	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}

	public List<String> getListOfStringDateData() {
		return listOfStringDateData;
	}

	public void setListOfStringDateData(List<String> listOfStringDateData) {
		this.listOfStringDateData = listOfStringDateData;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getApplicableType() {
		return applicableType;
	}

	public void setApplicableType(String applicableType) {
		this.applicableType = applicableType;
	}
	
	public String getReferenceBatchOrModuleName() {
		return referenceBatchOrModuleName;
	}

	public void setReferenceBatchOrModuleName(String referenceBatchOrModuleName) {
		this.referenceBatchOrModuleName = referenceBatchOrModuleName;
	}
	
	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	

//
 	
 	
	
	public Long getTimeboundId() {
		return timeboundId;
	}

	public void setTimeboundId(Long timeboundId) {
		this.timeboundId = timeboundId;
	}

	public int getNoOfAssessments() {
		return noOfAssessments;
	}

	public void setNoOfAssessments(int noOfAssessments) {
		this.noOfAssessments = noOfAssessments;
	}

	public String getPrerequisites() {

		if(StringUtils.isBlank(prerequisites)) {
			return "NA";
		}
		return prerequisites;
	}

	public void setPrerequisites(String prerequisites) {
		this.prerequisites = prerequisites;
	}

	public String getPedagogy() {

		if(StringUtils.isBlank(pedagogy)) {
			return "NA";
		}
		return pedagogy;
	}

	public void setPedagogy(String pedagogy) {
		this.pedagogy = pedagogy;
	}

	public String getTextbook() {
		if(StringUtils.isBlank(textbook)) {
			return "NA";
		}
		return textbook;
	}

	public void setTextbook(String textbook) {
		this.textbook = textbook;
	}

	public String getJournals() {
		if(StringUtils.isBlank(journals)) {
			return "NA";
		}
		return journals;
	}

	public void setJournals(String journals) {
		this.journals = journals;
	}

	public String getLinks() {
		if(StringUtils.isBlank(links)) {
			return "NA";
		}
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getLearningOutcomes() {

		if(StringUtils.isBlank(learningOutcomes)) {
			return "NA";
		}
		return learningOutcomes;
	}

	public void setLearningOutcomes(String learningOutcomes) {
		this.learningOutcomes = learningOutcomes;
	}

	public String getObjectives() {
		
		if(StringUtils.isBlank(objectives)) {
			return "NA";
		}
		
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	public String getProgramStructureId() {
		return programStructureId;
	}

	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramStructureDataId() {
		return programStructureDataId;
	}

	public void setProgramStructureDataId(String programStructureDataId) {
		this.programStructureDataId = programStructureDataId;
	}

	public String getCourseRationale() {
		if(courseRationale == null || "".equalsIgnoreCase(courseRationale)) {
			return "NA";
		}
		return courseRationale;
	}

	public void setCourseRationale(String courseRationale) {
		this.courseRationale = courseRationale;
	}

	public int gettEEPercentage() {
		return tEEPercentage;
	}

	public void settEEPercentage(int tEEPercentage) {
		this.tEEPercentage = tEEPercentage;
	}

	public int getContinuousEvaluationPercentage() {
		return continuousEvaluationPercentage;
	}

	public void setContinuousEvaluationPercentage(int continuousEvaluationPercentage) {
		this.continuousEvaluationPercentage = continuousEvaluationPercentage;
	}

	public int getNoOfTutorials() {
		return noOfTutorials;
	}

	public void setNoOfTutorials(int noOfTutorials) {
		this.noOfTutorials = noOfTutorials;
	}

	public int getNoOf_Practical_Group_Work() {
		return noOf_Practical_Group_Work;
	}

	public void setNoOf_Practical_Group_Work(int noOf_Practical_Group_Work) {
		this.noOf_Practical_Group_Work = noOf_Practical_Group_Work;
	}

	public int getNoOfClassroomSessions() {
		return noOfClassroomSessions;
	}

	public void setNoOfClassroomSessions(int noOfClassroomSessions) {
		this.noOfClassroomSessions = noOfClassroomSessions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(Long consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public Long getProgramSemSubjectId() {
		return programSemSubjectId;
	}

	public void setProgramSemSubjectId(Long programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}

	
	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getProgramStructure() {
		return programStructure;
	}

	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	public Integer getCountOfProgramsApplicableTo() {
		return countOfProgramsApplicableTo;
	}

	public void setCountOfProgramsApplicableTo(Integer countOfProgramsApplicableTo) {
		this.countOfProgramsApplicableTo = countOfProgramsApplicableTo;
	}

	public boolean isAllowedToUpdate() {
		return allowedToUpdate;
	}

	public void setAllowedToUpdate(boolean allowedToUpdate) {
		this.allowedToUpdate = allowedToUpdate;
	}

	@Override
	public String toString() {
		return "SessionPlanBean [id=" + id + ", sapid=" + sapid + ", title=" + title + ", subject=" + subject
				+ ", month=" + month + ", year=" + year + ", createdBy=" + createdBy + ", lastModifiedBy="
				+ lastModifiedBy + ", createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", programSemSubjectId="
				+ programSemSubjectId + ", consumerTypeId=" + consumerTypeId + ", programStructureId="
				+ programStructureId + ", programId=" + programId + ", consumerType=" + consumerType + ", program="
				+ program + ", programStructure=" + programStructure + ", programStructureDataId="
				+ programStructureDataId + ", countOfProgramsApplicableTo=" + countOfProgramsApplicableTo
				+ ", allowedToUpdate=" + allowedToUpdate + ", noOfClassroomSessions=" + noOfClassroomSessions
				+ ", noOf_Practical_Group_Work=" + noOf_Practical_Group_Work + ", noOfTutorials=" + noOfTutorials
				+ ", noOfAssessments=" + noOfAssessments + ", continuousEvaluationPercentage="
				+ continuousEvaluationPercentage + ", tEEPercentage=" + tEEPercentage + ", courseRationale="
				+ courseRationale + ", objectives=" + objectives + ", learningOutcomes=" + learningOutcomes
				+ ", prerequisites=" + prerequisites + ", pedagogy=" + pedagogy + ", textbook=" + textbook
				+ ", journals=" + journals + ", links=" + links + ", timeboundId=" + timeboundId + ", batchName="
				+ batchName + ", type=" + type + ", pedagogyUsed=" + pedagogyUsed + ", casestudyName=" + casestudyName
				+ ", pedagogicalTool=" + pedagogicalTool + ", teachingMethod=" + teachingMethod + ", casestudySource="
				+ casestudySource + ", casestudyType=" + casestudyType + ", applicableType=" + applicableType
				+ ", referenceBatchOrModuleName=" + referenceBatchOrModuleName + ", referenceId=" + referenceId
				+ ", startDate=" + startDate + ", acadMonth=" + acadMonth + ", acadYear=" + acadYear
				+ ", listOfStringDateData=" + listOfStringDateData + ", subjectCode=" + subjectCode + ", subjectCodeId="
				+ subjectCodeId + "]";
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPedagogyUsed() {
		return pedagogyUsed;
	}

	public void setPedagogyUsed(String pedagogyUsed) {
		this.pedagogyUsed = pedagogyUsed;
	}

	public String getCasestudyName() {
		return casestudyName;
	}

	public void setCasestudyName(String casestudyName) {
		this.casestudyName = casestudyName;
	}

	public String getPedagogicalTool() {
		return pedagogicalTool;
	}

	public void setPedagogicalTool(String pedagogicalTool) {
		this.pedagogicalTool = pedagogicalTool;
	}

	public String getTeachingMethod() {
		return teachingMethod;
	}

	public void setTeachingMethod(String teachingMethod) {
		this.teachingMethod = teachingMethod;
	}

	public String getCasestudySource() {
		return casestudySource;
	}

	public void setCasestudySource(String casestudySource) {
		this.casestudySource = casestudySource;
	}

	public String getCasestudyType() {
		return casestudyType;
	}

	public void setCasestudyType(String casestudyType) {
		this.casestudyType = casestudyType;
	}
	
}
