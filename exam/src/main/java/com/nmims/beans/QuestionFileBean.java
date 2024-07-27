package com.nmims.beans;

import java.io.Serializable;

public class QuestionFileBean  implements Serializable  {
	private String questionId;
	private String examCode;
	private String subjectCode;
	private String sectionCode;
	private String questionText;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private String option5;
	private String correctAnswer;
	private String marks;
	private String chapterNo;
	private String chapterName;
	private String reviewRemarks;
	
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String rowNumber;
	
	private String negativeMarks;
	private String questionType;
	private String difficulty;
	private String subsectionCode;
	private String caseId;
	
	
	
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getNegativeMarks() {
		return negativeMarks;
	}
	public void setNegativeMarks(String negativeMarks) {
		this.negativeMarks = negativeMarks;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public String getSubsectionCode() {
		return subsectionCode;
	}
	public void setSubsectionCode(String subsectionCode) {
		this.subsectionCode = subsectionCode;
	}
	public String getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getExamCode() {
		return examCode;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getOption1() {
		return option1;
	}
	public void setOption1(String option1) {
		this.option1 = option1;
	}
	public String getOption2() {
		return option2;
	}
	public void setOption2(String option2) {
		this.option2 = option2;
	}
	public String getOption3() {
		return option3;
	}
	public void setOption3(String option3) {
		this.option3 = option3;
	}
	public String getOption4() {
		return option4;
	}
	public void setOption4(String option4) {
		this.option4 = option4;
	}
	public String getOption5() {
		return option5;
	}
	public void setOption5(String option5) {
		this.option5 = option5;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	public String getChapterNo() {
		return chapterNo;
	}
	public void setChapterNo(String chapterNo) {
		this.chapterNo = chapterNo;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public String getReviewRemarks() {
		if(reviewRemarks == null){
			return "";
		}else{
			return reviewRemarks;
		}
		
	}
	public void setReviewRemarks(String reviewRemarks) {
		this.reviewRemarks = reviewRemarks;
	}
	
	
	

}
