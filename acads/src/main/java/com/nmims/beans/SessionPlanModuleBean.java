package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionPlanModuleBean  implements Serializable  {
	
	private Long id;
	private Long sessionPlanId;
	private String topic;
	private String outcomes;
	private String pedagogicalTool;
	private String chapter;
    private String createdBy;
 	private String lastModifiedBy;
 	private String createdDate;
 	private String lastModifiedDate;
 	private Long sessionModuleNo;

 	private String sessionId;
 	private String sessionplan_module_id;

 	private String sessionDate;
 	private String sessionTime;
 	private String date;
 	private String startTime;
 	private String timebondFacultyId;
 	
 	private String sessionplanMonth;
 	private String sessionplanYear;
 	private String sessionplanCreatedBy;
 	private String sessionplanLastModifiedBy;
 	private String sessionplanSubject;
 	private String corporateName;
 	private String moduleSessionPlanId;
 	
	private SessionDayTimeAcadsBean session;
 	
 	private String testName;
 	private String testStartDate;
 	private String testEndDate;
 	private String examYear;
 	private String examMonth;
	private String batchName;
	private ArrayList<String> chapters;
	private long syllabusId;
	private boolean cloneSyllabus;
	private String sapId;
	private TestAcadsBean test;
	private String action;
	private String consumerProgramStructureId;
	private int subjectCodeId;
	
	private List<SessionPlanModuleBean> moduleDetails;
	
	//===============//////////////////////////////
    private String subject;
	private String consumerType;
	private String program;
	private String programStructure;
	private int noOfClassroomSessions;
	private int noOf_Practical_Group_Work;
	private int noOfAssessments;	
	private String courseRationale;	
	private String objectives;	
	private String learningOutcomes;	
	private String prerequisites;	
	private String pedagogy;	
	private String textbook;	
	private String journals;	
	private String links;	

	private String pedagogyUsed;
	private String casestudyName;
	private String teachingMethod;
	private String casestudySource;
	private String casestudyType;
	private String acadMonth;
	private String acadYear;
	
	private String sessionPlanTimeStamp;
	private String sessionPlanModuleTimeStamp;
	private String sessionTimeStamp;
	private String programId;
	private String modulePedagogicalTool;
	
	////////////////////////////================//
	
	public String getModulePedagogicalTool() {
		return modulePedagogicalTool;
	}
	public void setModulePedagogicalTool(String modulePedagogicalTool) {
		this.modulePedagogicalTool = modulePedagogicalTool;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getSessionTimeStamp() {
		return sessionTimeStamp;
	}
	public void setSessionTimeStamp(String sessionTimeStamp) {
		this.sessionTimeStamp = sessionTimeStamp;
	}
	public String getSessionPlanTimeStamp() {
		return sessionPlanTimeStamp;
	}
	public void setSessionPlanTimeStamp(String sessionPlanTimeStamp) {
		this.sessionPlanTimeStamp = sessionPlanTimeStamp;
	}
	public String getSessionPlanModuleTimeStamp() {
		return sessionPlanModuleTimeStamp;
	}
	public void setSessionPlanModuleTimeStamp(String sessionPlanModuleTimeStamp) {
		this.sessionPlanModuleTimeStamp = sessionPlanModuleTimeStamp;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public int getNoOfClassroomSessions() {
		return noOfClassroomSessions;
	}
	public void setNoOfClassroomSessions(int noOfClassroomSessions) {
		this.noOfClassroomSessions = noOfClassroomSessions;
	}
	public int getNoOf_Practical_Group_Work() {
		return noOf_Practical_Group_Work;
	}
	public void setNoOf_Practical_Group_Work(int noOf_Practical_Group_Work) {
		this.noOf_Practical_Group_Work = noOf_Practical_Group_Work;
	}
	public int getNoOfAssessments() {
		return noOfAssessments;
	}
	public void setNoOfAssessments(int noOfAssessments) {
		this.noOfAssessments = noOfAssessments;
	}
	public String getCourseRationale() {
		return courseRationale;
	}
	public void setCourseRationale(String courseRationale) {
		this.courseRationale = courseRationale;
	}
	public String getObjectives() {
		return objectives;
	}
	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}
	public String getLearningOutcomes() {
		return learningOutcomes;
	}
	public void setLearningOutcomes(String learningOutcomes) {
		this.learningOutcomes = learningOutcomes;
	}
	public String getPrerequisites() {
		return prerequisites;
	}
	public void setPrerequisites(String prerequisites) {
		this.prerequisites = prerequisites;
	}
	public String getPedagogy() {
		return pedagogy;
	}
	public void setPedagogy(String pedagogy) {
		this.pedagogy = pedagogy;
	}
	public String getTextbook() {
		return textbook;
	}
	public void setTextbook(String textbook) {
		this.textbook = textbook;
	}
	public String getJournals() {
		return journals;
	}
	public void setJournals(String journals) {
		this.journals = journals;
	}
	public String getLinks() {
		return links;
	}
	public void setLinks(String links) {
		this.links = links;
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
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	private String facultyName;
	
	private String facultyId;
	
	private String prgm_sem_subj_id;
	
	private String batchId;
	
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public TestAcadsBean getTest() {
		return test;
	}
	public void setTest(TestAcadsBean test) {
		this.test = test;
	}

 	private String sessionName;
 	
 	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getModuleSessionPlanId() {

		return moduleSessionPlanId;
	}
	public void setModuleSessionPlanId(String moduleSessionPlanId) {
		this.moduleSessionPlanId = moduleSessionPlanId;
	}
	public String getCorporateName() {
		return corporateName;
	}
	public void setCorporateName(String corporateName) {
		this.corporateName = corporateName;
	}
	public String getSessionplanMonth() {
		return sessionplanMonth;
	}
	public void setSessionplanMonth(String sessionplanMonth) {
		this.sessionplanMonth = sessionplanMonth;
	}
	public String getSessionplanYear() {
		return sessionplanYear;
	}
	public void setSessionplanYear(String sessionplanYear) {
		this.sessionplanYear = sessionplanYear;
	}
	public String getSessionplanCreatedBy() {
		return sessionplanCreatedBy;
	}
	public void setSessionplanCreatedBy(String sessionplanCreatedBy) {
		this.sessionplanCreatedBy = sessionplanCreatedBy;
	}
	public String getSessionplanLastModifiedBy() {
		return sessionplanLastModifiedBy;
	}
	public void setSessionplanLastModifiedBy(String sessionplanLastModifiedBy) {
		this.sessionplanLastModifiedBy = sessionplanLastModifiedBy;
	}

	public String getSessionplanSubject() {
		return sessionplanSubject;
	}
	public void setSessionplanSubject(String sessionplanSubject) {
		this.sessionplanSubject = sessionplanSubject;
	}
	public String getTimebondFacultyId() {
		return timebondFacultyId;
	}
	public void setTimebondFacultyId(String timebondFacultyId) {
		this.timebondFacultyId = timebondFacultyId;
	}
	
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
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

	public String getSessionplan_module_id() {
		return sessionplan_module_id;
	}
	public void setSessionplan_module_id(String sessionplan_module_id) {
		this.sessionplan_module_id = sessionplan_module_id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getSessionModuleNo() {
		return sessionModuleNo;
	}
	public void setSessionModuleNo(Long sessionModuleNo) {
		this.sessionModuleNo = sessionModuleNo;
	}


	private boolean sessionAttended;
 	private boolean testAttended;

 	private boolean sessionOver;
 	private boolean testOver;

 	private String testId;
 	private int testScoreObtained;
 	private String showResultsToStudents;
 	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getSessionPlanId() {
		return sessionPlanId;
	}


	public void setSessionPlanId(Long sessionPlanId) {
		this.sessionPlanId = sessionPlanId;
	}


	public String getTopic() {
		return topic;
	}


	public void setTopic(String topic) {
		this.topic = topic;
	}


	public String getOutcomes() {
		return outcomes;
	}


	public void setOutcomes(String outcomes) {
		this.outcomes = outcomes;
	}


	public String getPedagogicalTool() {
		return pedagogicalTool;
	}


	public void setPedagogicalTool(String pedagogicalTool) {
		this.pedagogicalTool = pedagogicalTool;
	}


	public String getChapter() {
		return chapter;
	}


	public void setChapter(String chapter) {
		this.chapter = chapter;
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


	
	public SessionDayTimeAcadsBean getSession() {
		return session;
	}


	public void setSession(SessionDayTimeAcadsBean session) {
		this.session = session;
	}


	public boolean isSessionAttended() {
		return sessionAttended;
	}


	public void setSessionAttended(boolean sessionAttended) {
		this.sessionAttended = sessionAttended;
	}


	public boolean isTestAttended() {
		return testAttended;
	}


	public void setTestAttended(boolean testAttended) {
		this.testAttended = testAttended;
	}


	public boolean isSessionOver() {
		return sessionOver;
	}


	public void setSessionOver(boolean sessionOver) {
		this.sessionOver = sessionOver;
	}


	public boolean isTestOver() {
		return testOver;
	}


	public void setTestOver(boolean testOver) {
		this.testOver = testOver;
	}


	public String getTestId() {
		return testId;
	}


	public void setTestId(String testId) {
		this.testId = testId;
	}


	public int getTestScoreObtained() {
		return testScoreObtained;
	}


	public void setTestScoreObtained(int testScoreObtained) {
		this.testScoreObtained = testScoreObtained;
	}


 	public String getTestStartDate() {
		return testStartDate;
	}


	public void setTestStartDate(String testStartDate) {
		this.testStartDate = testStartDate;
	}


	public String getTestEndDate() {
		return testEndDate;
	}


	public void setTestEndDate(String testEndDate) {
		this.testEndDate = testEndDate;
	}
	
	@Override
	public String toString() {
		return "SessionPlanModuleBean [id=" + id + ", sessionPlanId=" + sessionPlanId + ", topic=" + topic
				+ ", outcomes=" + outcomes + ", pedagogicalTool=" + pedagogicalTool + ", chapter=" + chapter
				+ ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", createdDate=" + createdDate
				+ ", lastModifiedDate=" + lastModifiedDate + ", sessionModuleNo=" + sessionModuleNo + ", sessionId="
				+ sessionId + ", sessionplan_module_id=" + sessionplan_module_id + ", sessionDate=" + sessionDate
				+ ", sessionTime=" + sessionTime + ", date=" + date + ", startTime=" + startTime
				+ ", timebondFacultyId=" + timebondFacultyId + ", sessionplanMonth=" + sessionplanMonth
				+ ", sessionplanYear=" + sessionplanYear + ", sessionplanCreatedBy=" + sessionplanCreatedBy
				+ ", sessionplanLastModifiedBy=" + sessionplanLastModifiedBy + ", sessionplanSubject="
				+ sessionplanSubject + ", corporateName=" + corporateName + ", moduleSessionPlanId="
				+ moduleSessionPlanId + ", session=" + session + ", testName=" + testName + ", testStartDate="
				+ testStartDate + ", testEndDate=" + testEndDate + ", examYear=" + examYear + ", examMonth=" + examMonth
				+ ", batchName=" + batchName + ", chapters=" + chapters + ", syllabusId=" + syllabusId
				+ ", cloneSyllabus=" + cloneSyllabus + ", sapId=" + sapId + ", test=" + test + ", action=" + action
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", subjectCodeId=" + subjectCodeId
				+ ", moduleDetails=" + moduleDetails + ", subject=" + subject + ", consumerType=" + consumerType
				+ ", program=" + program + ", programStructure=" + programStructure + ", noOfClassroomSessions="
				+ noOfClassroomSessions + ", noOf_Practical_Group_Work=" + noOf_Practical_Group_Work
				+ ", noOfAssessments=" + noOfAssessments + ", courseRationale=" + courseRationale + ", objectives="
				+ objectives + ", learningOutcomes=" + learningOutcomes + ", prerequisites=" + prerequisites
				+ ", pedagogy=" + pedagogy + ", textbook=" + textbook + ", journals=" + journals + ", links=" + links
				+ ", pedagogyUsed=" + pedagogyUsed + ", casestudyName=" + casestudyName + ", teachingMethod="
				+ teachingMethod + ", casestudySource=" + casestudySource + ", casestudyType=" + casestudyType
				+ ", acadMonth=" + acadMonth + ", acadYear=" + acadYear + ", sessionPlanTimeStamp="
				+ sessionPlanTimeStamp + ", sessionPlanModuleTimeStamp=" + sessionPlanModuleTimeStamp
				+ ", sessionTimeStamp=" + sessionTimeStamp + ", programId=" + programId + ", modulePedagogicalTool="
				+ modulePedagogicalTool + ", facultyName=" + facultyName + ", facultyId=" + facultyId
				+ ", prgm_sem_subj_id=" + prgm_sem_subj_id + ", batchId=" + batchId + ", name=" + name
				+ ", sessionName=" + sessionName + ", sessionAttended=" + sessionAttended + ", testAttended="
				+ testAttended + ", sessionOver=" + sessionOver + ", testOver=" + testOver + ", testId=" + testId
				+ ", testScoreObtained=" + testScoreObtained + ", showResultsToStudents=" + showResultsToStudents + "]";
	}


	public String getTestName() {
		return testName;
	}


	public void setTestName(String testName) {
		this.testName = testName;
	}


	public String getShowResultsToStudents() {
		return showResultsToStudents;
	}


	public void setShowResultsToStudents(String showResultsToStudents) {
		this.showResultsToStudents = showResultsToStudents;
	}
	public List<SessionPlanModuleBean> getModuleDetails() {
		return moduleDetails;
	}
	public void setModuleDetails(List<SessionPlanModuleBean> moduleDetails) {
		this.moduleDetails = moduleDetails;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public ArrayList<String> getChapters() {
		return chapters;
	}
	public void setChapters(ArrayList<String> chapters) {
		this.chapters = chapters;
	}
	public long getSyllabusId() {
		return syllabusId;
	}
	public void setSyllabusId(long syllabusId) {
		this.syllabusId = syllabusId;
	}
	public boolean isCloneSyllabus() {
		return cloneSyllabus;
	}
	public void setCloneSyllabus(boolean cloneSyllabus) {
		this.cloneSyllabus = cloneSyllabus;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public int getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(int subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
}
