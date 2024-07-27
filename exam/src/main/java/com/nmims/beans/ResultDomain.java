package com.nmims.beans;

import java.io.Serializable;
import java.util.Set;

public class ResultDomain  implements Serializable  {

	public int totalStringLength;
	public double matching;
	public int noOfMatches;
	public int blankLines;
	public String firstFile;
	public String secondFile;
	
	private String matchingFor80to90;
    private String subject;
    private String sapId1;
	private String lastName1;
    private String firstName1;
    private String program1;
    private String sapId2;
	private String lastName2;
    private String firstName2;
    private String program2;
	private String centerName1;
	
	private String centerName2;
	private int maxConseutiveLinesMatched;
    private int numberOfLinesInFirstFile;
    private int numberOfLinesInSecondFile;
    

	private String testDescriptiveAnswer;

	private String firstTestDescriptiveAnswer;

	private String secondTestDescriptiveAnswer;
	
	

	public String iATestAnswerCreatedDate;
	

	public String firstSapidTestAnswerCreatedDate;
	public String secondSapidTestAnswerCreatedDate;
	
	public Long testId;
	
	public Long questionId;
	
	public String markedForCopyCase;
	

	public String attemptStatus;
	public String sapid1AttemptStatus;
	public String sapid2AttemptStatus;
	
	public Integer attempt;
	
	private String sapid;
	private String name;
	private String month;
	private String year;
	private String subjectCode;
	private Set<String> commonText;
	
	
	
	public Set<String> getCommonText() {
		return commonText;
	}
	public void setCommonText(Set<String> commonText) {
		this.commonText = commonText;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
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
	public String getSapid1AttemptStatus() {
		return sapid1AttemptStatus;
	}
	public void setSapid1AttemptStatus(String sapid1AttemptStatus) {
		this.sapid1AttemptStatus = sapid1AttemptStatus;
	}
	public String getSapid2AttemptStatus() {
		return sapid2AttemptStatus;
	}
	public void setSapid2AttemptStatus(String sapid2AttemptStatus) {
		this.sapid2AttemptStatus = sapid2AttemptStatus;
	}
	public Integer getAttempt() {
		return attempt;
	}
	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}
	public String getAttemptStatus() {
		return attemptStatus;
	}
	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}
	public String getMarkedForCopyCase() {
		return markedForCopyCase;
	}
	public void setMarkedForCopyCase(String markedForCopyCase) {
		this.markedForCopyCase = markedForCopyCase;
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	public String getFirstSapidTestAnswerCreatedDate() {
		return firstSapidTestAnswerCreatedDate;
	}
	public void setFirstSapidTestAnswerCreatedDate(String firstSapidTestAnswerCreatedDate) {
		this.firstSapidTestAnswerCreatedDate = firstSapidTestAnswerCreatedDate;
	}
	public String getSecondSapidTestAnswerCreatedDate() {
		return secondSapidTestAnswerCreatedDate;
	}
	public void setSecondSapidTestAnswerCreatedDate(String secondSapidTestAnswerCreatedDate) {
		this.secondSapidTestAnswerCreatedDate = secondSapidTestAnswerCreatedDate;
	}
	public String getiATestAnswerCreatedDate() {
		return iATestAnswerCreatedDate;
	}
	public void setiATestAnswerCreatedDate(String iATestAnswerCreatedDate) {
		this.iATestAnswerCreatedDate = iATestAnswerCreatedDate;
	}
	public String getFirstTestDescriptiveAnswer() {
		return firstTestDescriptiveAnswer;
	}
	public void setFirstTestDescriptiveAnswer(String firstTestDescriptiveAnswer) {
		this.firstTestDescriptiveAnswer = firstTestDescriptiveAnswer;
	}
	public String getSecondTestDescriptiveAnswer() {
		return secondTestDescriptiveAnswer;
	}
	public void setSecondTestDescriptiveAnswer(String secondTestDescriptiveAnswer) {
		this.secondTestDescriptiveAnswer = secondTestDescriptiveAnswer;
	}
	public String getTestDescriptiveAnswer() {
		return testDescriptiveAnswer;
	}
	public void setTestDescriptiveAnswer(String testDescriptiveAnswer) {
		this.testDescriptiveAnswer = testDescriptiveAnswer;
	}
	public int getNumberOfLinesInFirstFile() {
		return numberOfLinesInFirstFile;
	}
	public void setNumberOfLinesInFirstFile(int numberOfLinesInFirstFile) {
		this.numberOfLinesInFirstFile = numberOfLinesInFirstFile;
	}
	public int getNumberOfLinesInSecondFile() {
		return numberOfLinesInSecondFile;
	}
	public void setNumberOfLinesInSecondFile(int numberOfLinesInSecondFile) {
		this.numberOfLinesInSecondFile = numberOfLinesInSecondFile;
	}
	public int getMaxConseutiveLinesMatched() {
		return maxConseutiveLinesMatched;
	}
	public void setMaxConseutiveLinesMatched(int maxConseutiveLinesMatched) {
		this.maxConseutiveLinesMatched = maxConseutiveLinesMatched;
	}
	public String getCenterName1() {
		return centerName1;
	}
	public void setCenterName1(String centerName1) {
		this.centerName1 = centerName1;
	}
	public String getCenterName2() {
		return centerName2;
	}
	public void setCenterName2(String centerName2) {
		this.centerName2 = centerName2;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSapId1() {
		return sapId1;
	}
	public void setSapId1(String sapId1) {
		this.sapId1 = sapId1;
	}
	public String getLastName1() {
		return lastName1;
	}
	public void setLastName1(String lastName1) {
		this.lastName1 = lastName1;
	}
	public String getFirstName1() {
		return firstName1;
	}
	public void setFirstName1(String firstName1) {
		this.firstName1 = firstName1;
	}
	public String getProgram1() {
		return program1;
	}
	public void setProgram1(String program1) {
		this.program1 = program1;
	}
	public String getSapId2() {
		return sapId2;
	}
	public void setSapId2(String sapId2) {
		this.sapId2 = sapId2;
	}
	public String getLastName2() {
		return lastName2;
	}
	public void setLastName2(String lastName2) {
		this.lastName2 = lastName2;
	}
	public String getFirstName2() {
		return firstName2;
	}
	public void setFirstName2(String firstName2) {
		this.firstName2 = firstName2;
	}
	public String getProgram2() {
		return program2;
	}
	public void setProgram2(String program2) {
		this.program2 = program2;
	}
	public String getFirstFile() {
		return firstFile;
	}
	public void setFirstFile(String firstFile) {
		this.firstFile = firstFile;
	}
	public String getSecondFile() {
		return secondFile;
	}
	public void setSecondFile(String secondFile) {
		this.secondFile = secondFile;
	}
	public int getBlankLines() {
		return blankLines;
	}
	public void setBlankLines(int blankLines) {
		this.blankLines = blankLines;
	}
	public int getTotalStringLength() {
		return totalStringLength;
	}
	public void setTotalStringLength(int totalStringLength) {
		this.totalStringLength = totalStringLength;
	}
	public double getMatching() {
		return matching;
	}
	public void setMatching(double matching) {
		this.matching = matching;
	}
	public int getNoOfMatches() {
		return noOfMatches;
	}
	public String getMatchingFor80to90() {
		return matchingFor80to90;
	}
	public void setMatchingFor80to90(String matchingFor80to90) {
		this.matchingFor80to90 = matchingFor80to90;
	}
	public void setNoOfMatches(int noOfMatches) {
		this.noOfMatches = noOfMatches;
	}
	@Override
	public String toString() {
		return "ResultDomain [totalStringLength=" + totalStringLength + ", matching=" + matching + ", noOfMatches="
				+ noOfMatches + ", blankLines=" + blankLines + ", firstFile=" + firstFile + ", secondFile=" + secondFile
				+ ", matchingFor80to90=" + matchingFor80to90 + ", subject=" + subject + ", sapId1=" + sapId1
				+ ", lastName1=" + lastName1 + ", firstName1=" + firstName1 + ", program1=" + program1 + ", sapId2="
				+ sapId2 + ", lastName2=" + lastName2 + ", firstName2=" + firstName2 + ", program2=" + program2
				+ ", centerName1=" + centerName1 + ", centerName2=" + centerName2 + ", maxConseutiveLinesMatched="
				+ maxConseutiveLinesMatched + ", numberOfLinesInFirstFile=" + numberOfLinesInFirstFile
				+ ", numberOfLinesInSecondFile=" + numberOfLinesInSecondFile + ", testDescriptiveAnswer="
				+ testDescriptiveAnswer + ", firstTestDescriptiveAnswer=" + firstTestDescriptiveAnswer
				+ ", secondTestDescriptiveAnswer=" + secondTestDescriptiveAnswer + ", iATestAnswerCreatedDate="
				+ iATestAnswerCreatedDate + ", firstSapidTestAnswerCreatedDate=" + firstSapidTestAnswerCreatedDate
				+ ", secondSapidTestAnswerCreatedDate=" + secondSapidTestAnswerCreatedDate + ", testId=" + testId
				+ ", questionId=" + questionId + ", markedForCopyCase=" + markedForCopyCase + ", attemptStatus="
				+ attemptStatus + ", sapid1AttemptStatus=" + sapid1AttemptStatus + ", sapid2AttemptStatus="
				+ sapid2AttemptStatus + ", attempt=" + attempt + ", sapid=" + sapid + ", name=" + name + ", month="
				+ month + ", year=" + year + "]";
	}

	
}
