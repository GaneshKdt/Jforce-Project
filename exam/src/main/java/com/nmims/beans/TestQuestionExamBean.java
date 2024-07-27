package com.nmims.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

//spring security related changes rename TestQuestionBean to TestQuestionExamBean
public class TestQuestionExamBean extends BaseExamBean  implements Serializable{

	private String active;

	private String correctOption;

	private String description;

	private int type;

	private Double marks;

	private String option1;

	private String option2;

	private String option3;

	private String option4;

	private String option5;

	private String option6;

	private String option7;

	private String option8;

	private Long testId;

	private String testTypeInTestQuestion;

	private String chapter;

	private Long id;

	private String[] correctOptions;
	private StudentQuestionResponseExamBean studentQuestionResponse = new StudentQuestionResponseExamBean();

	private String errorMessage = "";
	private boolean errorRecord = false;

	private String question;

	private List<TestQuestionOptionExamBean> optionsList;
	
	private int isSubQuestion;
	private Long mainQuestionId;
	
	private List<TestQuestionExamBean> subQuestionsList;
	
	private String answeredCorrect;
	
	private String typeInString;
	
	private String isAttempted;
	
	private String url;
	
	private String sapid;
	
	private double marksObtained;
	
	private String remarks;
	
	private int studentAnswerCorrect; 
	
	private String answer;
	private String[] answers;
	
	private String uploadType;
	
	//Start added sectionId, sectionName, srNoForSections field by Abhay
	
	private Integer sectionId;
	private String sectionName;
	private Integer srNoForSections; // added for display Questions serial wise
	
	//End
	
	public int getStudentAnswerCorrect() {
		return studentAnswerCorrect;
	}

	public void setStudentAnswerCorrect(int studentAnswerCorrect) {
		this.studentAnswerCorrect = studentAnswerCorrect;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getMarksObtained() {
		return marksObtained;
	}

	public void setMarksObtained(double marksObtained) {
		this.marksObtained = marksObtained;
	}

	/**
	 * @return the sapid
	 */
	public String getSapid() {
		return sapid;
	}

	/**
	 * @param sapid the sapid to set
	 */
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsAttempted() {
		return isAttempted;
	}

	public void setIsAttempted(String isAttempted) {
		this.isAttempted = isAttempted;
	}

	public String getTypeInString() {
		return typeInString;
	}

	public void setTypeInString(String typeInString) {
		this.typeInString = typeInString;
	}

	public String getAnsweredCorrect() {
		return answeredCorrect;
	}

	public void setAnsweredCorrect(String answeredCorrect) {
		this.answeredCorrect = answeredCorrect;
	}

	public List<TestQuestionExamBean> getSubQuestionsList() {
		return subQuestionsList;
	}

	public void setSubQuestionsList(List<TestQuestionExamBean> subQuestionsList) {
		this.subQuestionsList = subQuestionsList;
	}

	public Long getMainQuestionId() {
		return mainQuestionId;
	}

	public void setMainQuestionId(Long mainQuestionId) {
		this.mainQuestionId = mainQuestionId;
	}

	public int getIsSubQuestion() {
		return isSubQuestion;
	}

	public void setIsSubQuestion(int isSubQuestion) {
		this.isSubQuestion = isSubQuestion;
	}

	public List<TestQuestionOptionExamBean> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<TestQuestionOptionExamBean> optionsList) {
		this.optionsList = optionsList;
	}

	public String getQuestion() {
		
		return question.replace("\n", "");
	}

	public void setQuestion(String question) {
		this.question = question;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TestQuestionExamBean() {
	}

	public String getTestTypeInTestQuestion() {
		return testTypeInTestQuestion;
	}

	public void setTestTypeInTestQuestion(String testTypeInTestQuestion) {
		this.testTypeInTestQuestion = testTypeInTestQuestion;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(String correctOption) {
		if (null != correctOption)
			correctOptions = correctOption.split(",");
		this.correctOption = correctOption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getMarks() {
		return marks;
	}

	public void setMarks(Double marks) {
		this.marks = marks;
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

	public String getOption6() {
		return option6;
	}

	public void setOption6(String option6) {
		this.option6 = option6;
	}

	public String getOption7() {
		return option7;
	}

	public void setOption7(String option7) {
		this.option7 = option7;
	}

	public String getOption8() {
		return option8;
	}

	public void setOption8(String option8) {
		this.option8 = option8;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getCorrectOptions() {
		return correctOptions;
	}

	public void setCorrectOptions(String[] correctOptions) {
		this.correctOptions = correctOptions;
		if (null != correctOptions)
			correctOption = StringUtils.join(correctOptions, ',');
	}

	public StudentQuestionResponseExamBean getStudentQuestionResponse() {
		return studentQuestionResponse;
	}

	public void setStudentQuestionResponse(StudentQuestionResponseExamBean studentQuestionResponse) {
		this.studentQuestionResponse = studentQuestionResponse;
		if (null != studentQuestionResponse)
			this.studentQuestionResponse.setQuestionId(getId());
	}


	public void setAnswer(String answer) {
		studentQuestionResponse.setAnswer(answer);
		this.answer = answer;
	}

	public String getAnswer() {
		//return studentQuestionResponse.getAnswer();
		return this.answer;
	
	}
	public String[] getAnswers() {
		//return studentQuestionResponse.getAnswers();
		return this.answers;
	}

	public void setAnswers(String[] answers) {
		//studentQuestionResponse.setAnswers(answers);
		this.answers = answers;
	}

	public Long getStudentTestId() {
		return studentQuestionResponse.getStudentTestId();
	}

	public void setStudentTestId(Long studentTestId) {
		studentQuestionResponse.setStudentTestId(studentTestId);
	}

	public Double getStudentMarks() {
		return studentQuestionResponse.getMarks();
	}

	public void setStudentMarks(Double studentMarks) {
		studentQuestionResponse.setMarks(studentMarks);
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	
	public String displayAllOptionInfo() {
		StringBuilder str = new StringBuilder();
		for(TestQuestionOptionExamBean o : getOptionsList()) {
			str.append("\n"+o.toString());
		}
		return str.toString();
	}
	
//	@Override
//	public String toString() {
//		try {
//			return "TestQuestion [active=" + active + ", correctOption=" + correctOption + ", description=" + description
//					+ ", type=" + type + ", marks=" + marks + ", question=" + question + " option1=" + option1
//					+ ", option2=" + option2 + ", option3=" + option3 + ", option4=" + option4 + ", option5=" + option5
//					+ ", option6=" + option6 + ", option7=" + option7 + ", option8=" + option8 + ", testId=" + testId
//					+ ", correctOptions=" + Arrays.toString(correctOptions) + ", studentQuestionResponse="
//					+ studentQuestionResponse + ", getId()=" + getId() + ", getCreatedDate()=" + getCreatedDate()
//					+ ", getLastModifiedDate()=" + getLastModifiedDate() + ", getCreatedBy()=" + getCreatedBy()
//					+ ", getLastModifiedBy()=" + getLastModifiedBy() + "getUrl()="+getUrl()+", isErrorRecord()= , getErrorMessage()=]";
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return e.getMessage();
//		}
//		
//	}

	@Override
	public String toString() {
		return "TestQuestionBean [active=" + active + ", correctOption=" + correctOption + ", description="
				+ description + ", type=" + type + ", marks=" + marks + ", option1=" + option1 + ", option2=" + option2
				+ ", option3=" + option3 + ", option4=" + option4 + ", option5=" + option5 + ", option6=" + option6
				+ ", option7=" + option7 + ", option8=" + option8 + ", testId=" + testId + ", testTypeInTestQuestion="
				+ testTypeInTestQuestion + ", chapter=" + chapter + ", id=" + id + ", correctOptions="
				+ Arrays.toString(correctOptions) + ", studentQuestionResponse=" + studentQuestionResponse
				+ ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord + ", question=" + question
				+ ", optionsList=" + optionsList + ", isSubQuestion=" + isSubQuestion + ", mainQuestionId="
				+ mainQuestionId + ", subQuestionsList=" + subQuestionsList + ", answeredCorrect=" + answeredCorrect
				+ ", typeInString=" + typeInString + ", isAttempted=" + isAttempted + ", url=" + url + ", sapid="
				+ sapid + ", marksObtained=" + marksObtained + ", remarks=" + remarks + ", studentAnswerCorrect="
				+ studentAnswerCorrect + ", answer=" + answer + ", answers=" + Arrays.toString(answers)
				+ ", uploadType=" + uploadType + ", sectionId=" + sectionId + ", sectionName=" + sectionName
				+ ", srNoForSections=" + srNoForSections + ", copyCaseThreshold=" + copyCaseThreshold + "]";
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public Integer copyCaseThreshold;

	public Integer getCopyCaseThreshold() {
		return copyCaseThreshold;
	}

	public void setCopyCaseThreshold(Integer copyCaseThreshold) {
		this.copyCaseThreshold = copyCaseThreshold;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public Integer getSrNoForSections() {
		return srNoForSections;
	}

	public void setSrNoForSections(Integer srNoForSections) {
		this.srNoForSections = srNoForSections;
	}

	

	
	
}