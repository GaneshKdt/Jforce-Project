package com.nmims.timeline.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name="exam.test_questions", schema="exam")
public class TestQuestionBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	/*
	 * id, testId, marks, type, chapter, 
	 * question, description, option1, option2, option3, 
	 * option4, option5, option6, option7, option8, 
	 * correctOption, isSubQuestion, active, createdBy, createdDate, 
	 * lastModifiedBy, lastModifiedDate, copyCaseThreshold, uploadType 
	 **/
	
	private String active;

	private String correctOption;

	private String description;

	private int type;

	private Integer marks;

	private String option1;

	private String option2;

	private String option3;

	private String option4;

	private String option5;

	private String option6;

	private String option7;

	private String option8;

	private Long testId;
	

	@Transient
	private String testTypeInTestQuestion;

	private String chapter;


	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Transient
	private String[] correctOptions;
	
	@Transient
	private String errorMessage = "";
	@Transient
	private boolean errorRecord = false;

	private String question;

	@Transient
	private List<TestQuestionOptionBean> optionsList;
	
	@Column(name="isSubQuestion")
	private int isSubQuestion;
	
	@Transient
	private Long mainQuestionId;
	
	@Transient
	private List<TestQuestionBean> subQuestionsList;
	
	@Transient
	private String answeredCorrect;
	
	@Transient
	private String typeInString;
	
	@Transient
	private String isAttempted;
	
	@Transient
	private String url;
	
	@Transient
	private String sapid;
	
	@Transient
	private int marksObtained;
	
	@Transient
	private String remarks;
	
	@Transient
	private int studentAnswerCorrect; 
	
	@Transient
	private String answer;
	@Transient
	private String[] answers;
	
	private String uploadType;
	
	//start added sectionId, sectionName field by Abhay
	
	@Column(name="sectionId")
	private Integer sectionId;
	
	@Transient
	private String sectionName;
	
	@ManyToOne( fetch = FetchType.LAZY)
	@JoinColumn(name = "sectionId",  insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
	private TestSectionsBean testSectionsBean ;
	
	// end
	
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

	public int getMarksObtained() {
		return marksObtained;
	}

	public void setMarksObtained(int marksObtained) {
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

	public List<TestQuestionBean> getSubQuestionsList() {
		return subQuestionsList;
	}

	public void setSubQuestionsList(List<TestQuestionBean> subQuestionsList) {
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

	public List<TestQuestionOptionBean> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<TestQuestionOptionBean> optionsList) {
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

	public TestQuestionBean() {
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

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
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


	public void setAnswer(String answer) {
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

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	
	public String displayAllOptionInfo() {
		StringBuilder str = new StringBuilder();
		for(TestQuestionOptionBean o : getOptionsList()) {
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
				+ Arrays.toString(correctOptions) + ", studentQuestionResponse=" + ""
				+ ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord + ", question=" + question
				+ ", optionsList=" + optionsList + ", isSubQuestion=" + isSubQuestion + ", mainQuestionId="
				+ mainQuestionId + ", subQuestionsList=" + subQuestionsList + ", answeredCorrect=" + answeredCorrect
				+ ", typeInString=" + typeInString + ", isAttempted=" + isAttempted + ", url=" + url + ", sapid="
				+ sapid + ", marksObtained=" + marksObtained + ", remarks=" + remarks + ", studentAnswerCorrect="
				+ studentAnswerCorrect + ", answer=" + answer + ", answers=" + Arrays.toString(answers)+" copyCaseThreshold: "+copyCaseThreshold + "]";
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
	
	//From Basebean start

	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	@Transient
	private String tranDateTime;
	@Transient
	private String date;

	

	public String getTranDateTime() {
		return tranDateTime;
	}

	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
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


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	//Added for test module Start
	protected String compareStringAndSet(String value, String compare, String defaultValue) {
		return compare.equalsIgnoreCase(value) ? compare : defaultValue;
	}
	
	protected String checkYElseSetN(String value) {
		return compareStringAndSet(value, "Y", "N");
	}
	protected String formatDate(String date) {
		if(null == date) return date;
		if(date.length() > 19) {
			return date.substring(0, 19).replace(' ', 'T');
		} else {
			return date.replace(' ', 'T');
		}
	}
	//Added for test module End

	public TestQuestionBean( Long id, Long testId,
			Integer marks, int type, String chapter,
			String question, String description, String option1, String option2,
			String option3, String option4, String option5, String option6, String option7, String option8, 
			String correctOption, int isSubQuestion, String active, Integer  copyCaseThreshold,
			String uploadType, Integer sectionId, String sectionName
			) {
		super();
		this.id = id;
		this.testId = testId;
		this.marks = marks;
		this.type = type;
		this.chapter = chapter;
		this.question = question;
		this.description = description;
		this.option1 = option1;
		this.option2 = option2;
		this.option3 = option3;
		this.option4 = option4;
		this.option5 = option5;
		this.option6 = option6;
		this.option7 = option7;
		this.option8 = option8;
		this.correctOption = correctOption;
		this.isSubQuestion = isSubQuestion;
		this.active = active;
		this.copyCaseThreshold = copyCaseThreshold;
		this.uploadType = uploadType;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		
	}

	//From basebean end
	
	
}