package com.nmims.beans;

import java.io.Serializable;

public class IAReportsBean implements Serializable{
	private Long id;
	private String testName;
	private String testInitials;
	private String testDescription;
	
	private int year;
	private String month;
	private int acadYear;
	private String acadMonth;
	
	private String consumerType;
	private String consumerTypeIdFormValue;
	private String program;
	private String programIdFormValue;
	private String programStructure;
	private String programStructureIdFormValue;
	
	private Long referenceId;
	private String applicableType;
	private String batchName;
	private String moduleName;
	private String subject;
	
	private String startDate;
	private String endDate;
	private int windowTime;
	private int duration;
	
	private String templateType;
	private String submissionType;
	private String facultyId;
	private String facultyName;
	private String testType;
	private String remark;
	
	private int maxQuestnToShow;
	private String questionsConfigured;
	private String questionsUploaded;
	private int maxScore;
	
	private String allowAfterEndDate;
	private String sendEmailAlert;
	private String sendSmsAlert;
	
	private String proctoringEnabled;
	private String showCalculator;
	
	private String testLive;
	private String showResultsToStudents;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public String getTestInitials() {
		return testInitials;
	}
	
	public void setTestInitials(String testInitials) {
		this.testInitials = testInitials;
	}
	
	public String getTestDescription() {
		return testDescription;
	}
	
	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getMonth() {
		return month;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
	
	public int getAcadYear() {
		return acadYear;
	}
	
	public void setAcadYear(int acadYear) {
		this.acadYear = acadYear;
	}
	
	public String getAcadMonth() {
		return acadMonth;
	}
	
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	
	public String getConsumerType() {
		return consumerType;
	}
	
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	
	public String getConsumerTypeIdFormValue() {
		return consumerTypeIdFormValue;
	}
	
	public void setConsumerTypeIdFormValue(String consumerTypeIdFormValue) {
		this.consumerTypeIdFormValue = consumerTypeIdFormValue;
	}
	
	public String getProgram() {
		return program;
	}
	
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getProgramIdFormValue() {
		return programIdFormValue;
	}
	
	public void setProgramIdFormValue(String programIdFormValue) {
		this.programIdFormValue = programIdFormValue;
	}
	
	public String getProgramStructure() {
		return programStructure;
	}
	
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	
	public String getProgramStructureIdFormValue() {
		return programStructureIdFormValue;
	}
	
	public void setProgramStructureIdFormValue(String programStructureIdFormValue) {
		this.programStructureIdFormValue = programStructureIdFormValue;
	}
	
	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}
	
	public String getApplicableType() {
		return applicableType;
	}
	
	public void setApplicableType(String applicableType) {
		this.applicableType = applicableType;
	}
	
	public String getBatchName() {
		return batchName;
	}
	
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
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
	
	public int getWindowTime() {
		return windowTime;
	}
	
	public void setWindowTime(int windowTime) {
		this.windowTime = windowTime;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public String getTemplateType() {
		return templateType;
	}
	
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	public String getSubmissionType() {
		return submissionType;
	}
	
	public void setSubmissionType(String submissionType) {
		this.submissionType = submissionType;
	}
	
	public String getFacultyId() {
		return facultyId;
	}
	
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	
	public String getFacultyName() {
		return facultyName;
	}
	
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	
	public String getTestType() {
		return testType;
	}
	
	public void setTestType(String testType) {
		this.testType = testType;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public int getMaxQuestnToShow() {
		return maxQuestnToShow;
	}
	
	public void setMaxQuestnToShow(int maxQuestnToShow) {
		this.maxQuestnToShow = maxQuestnToShow;
	}
	
	public String getQuestionsConfigured() {
		return questionsConfigured;
	}

	public void setQuestionsConfigured(String questionsConfigured) {
		this.questionsConfigured = questionsConfigured;
	}

	public String getQuestionsUploaded() {
		return questionsUploaded;
	}

	public void setQuestionsUploaded(String questionsUploaded) {
		this.questionsUploaded = questionsUploaded;
	}

	public int getMaxScore() {
		return maxScore;
	}
	
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public String getAllowAfterEndDate() {
		return allowAfterEndDate;
	}
	
	public void setAllowAfterEndDate(String allowAfterEndDate) {
		this.allowAfterEndDate = allowAfterEndDate;
	}
	
	public String getSendEmailAlert() {
		return sendEmailAlert;
	}
	
	public void setSendEmailAlert(String sendEmailAlert) {
		this.sendEmailAlert = sendEmailAlert;
	}
	
	public String getSendSmsAlert() {
		return sendSmsAlert;
	}
	
	public void setSendSmsAlert(String sendSmsAlert) {
		this.sendSmsAlert = sendSmsAlert;
	}
	
	public String getProctoringEnabled() {
		return proctoringEnabled;
	}
	
	public void setProctoringEnabled(String proctoringEnabled) {
		this.proctoringEnabled = proctoringEnabled;
	}
	
	public String getShowCalculator() {
		return showCalculator;
	}
	
	public void setShowCalculator(String showCalculator) {
		this.showCalculator = showCalculator;
	}
	
	public String getTestLive() {
		return testLive;
	}

	public void setTestLive(String testLive) {
		this.testLive = testLive;
	}
	
	public String getShowResultsToStudents() {
		return showResultsToStudents;
	}
	
	public void setShowResultsToStudents(String showResultsToStudents) {
		this.showResultsToStudents = showResultsToStudents;
	}

	@Override
	public String toString() {
		return "IAReportsBean [id=" + id + ", testName=" + testName + ", testInitials=" + testInitials
				+ ", testDescription=" + testDescription + ", year=" + year + ", month=" + month + ", acadYear="
				+ acadYear + ", acadMonth=" + acadMonth + ", consumerType=" + consumerType
				+ ", consumerTypeIdFormValue=" + consumerTypeIdFormValue + ", program=" + program
				+ ", programIdFormValue=" + programIdFormValue + ", programStructure=" + programStructure
				+ ", programStructureIdFormValue=" + programStructureIdFormValue + ", referenceId=" + referenceId
				+ ", applicableType=" + applicableType + ", batchName=" + batchName + ", moduleName=" + moduleName
				+ ", subject=" + subject + ", startDate=" + startDate + ", endDate=" + endDate + ", windowTime="
				+ windowTime + ", duration=" + duration + ", templateType=" + templateType + ", submissionType="
				+ submissionType + ", facultyId=" + facultyId + ", facultyName=" + facultyName + ", testType="
				+ testType + ", remark=" + remark + ", maxQuestnToShow=" + maxQuestnToShow + ", questionsConfigured="
				+ questionsConfigured + ", questionsUploaded=" + questionsUploaded + ", maxScore=" + maxScore
				+ ", allowAfterEndDate=" + allowAfterEndDate + ", sendEmailAlert=" + sendEmailAlert + ", sendSmsAlert="
				+ sendSmsAlert + ", proctoringEnabled=" + proctoringEnabled + ", showCalculator=" + showCalculator
				+ ", testLive=" + testLive + ", showResultsToStudents=" + showResultsToStudents + "]";
	}
}
