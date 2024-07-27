package com.nmims.timeline.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name="exam.test_students_answers", schema="exam")
public class StudentQuestionResponseBean  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/*
	 id, sapid, testId, questionId, attempt, 
	 answer, marks, facultyId, isChecked, createdBy, 
	 createdDate, lastModifiedBy, lastModifiedDate, remark 
	 * */
	
	private String answer;

	private Integer marks;
	
	private Long questionId;
	
	@Transient
	private Long studentTestId;
	
	@Transient
	private String username;

	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long testId;
	private String sapid;
	@Transient
	private int type;
	private String facultyId;
	private int isChecked;
	@Transient
	private int maxMarks;
	@Transient
	private String question;
	private int attempt;
	
	@Transient
	private String[] answers;
	
	private String remark;
	
	@Transient
	private String testName;
	@Transient
	private int questionMarks;

	@Transient
	private String userId;
	@Transient
	private String url;
	


	@Transient
	private String firstName;

	@Transient
	private String lastName;
	

	@Transient
	private double peerToPeerMatchingPercentage;
	
	@Transient
	private Integer copyCaseThreshold;
	
	@Transient
	private String attemptStatus;
	
	

	@Transient
	private String uploadType;
	@Transient
	private int minNoOfQuestions;
	
	

	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	@Transient
    private String answerSavedStatus;
    
    
	
	public String getAnswerSavedStatus() {
		return answerSavedStatus;
	}



	public void setAnswerSavedStatus(String answerSavedStatus) {
		this.answerSavedStatus = answerSavedStatus;
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



	public int getMinNoOfQuestions() {
		return minNoOfQuestions;
	}



	public void setMinNoOfQuestions(int minNoOfQuestions) {
		this.minNoOfQuestions = minNoOfQuestions;
	}



	public String getAttemptStatus() {
		return attemptStatus;
	}



	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}



	public Integer getCopyCaseThreshold() {
		return copyCaseThreshold;
	}



	public void setCopyCaseThreshold(Integer copyCaseThreshold) {
		this.copyCaseThreshold = copyCaseThreshold;
	}



	public double getPeerToPeerMatchingPercentage() {
		return peerToPeerMatchingPercentage;
	}



	public void setPeerToPeerMatchingPercentage(double peerToPeerMatchingPercentage) {
		this.peerToPeerMatchingPercentage = peerToPeerMatchingPercentage;
	}



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public int getQuestionMarks() {
		return questionMarks;
	}



	public void setQuestionMarks(int questionMarks) {
		this.questionMarks = questionMarks;
	}



	public String getTestName() {
		return testName;
	}



	public void setTestName(String testName) {
		this.testName = testName;
	}



	public String getRemark() {
		return remark;
	}



	public void setRemark(String remark) {
		this.remark = remark;
	}

	//for facutly allocation
	@Transient
	private int allocatedAnswers;
	@Transient
	private String facutlyName;
	public StudentQuestionResponseBean() {
	}
	
	
	
	public int getAttempt() {
		return attempt;
	}



	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}



	public String getFacutlyName() {
		return facutlyName;
	}



	public void setFacutlyName(String facutlyName) {
		this.facutlyName = facutlyName;
	}



	public int getAllocatedAnswers() {
		return allocatedAnswers;
	}



	public void setAllocatedAnswers(int allocatedAnswers) {
		this.allocatedAnswers = allocatedAnswers;
	}



	public String getQuestion() {
		return question;
	}



	public void setQuestion(String question) {
		this.question = question;
	}



	public int getMaxMarks() {
		return maxMarks;
	}




	public void setMaxMarks(int maxMarks) {
		this.maxMarks = maxMarks;
	}




	public String getFacultyId() {
		return facultyId;
	}




	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}




	public int getIsChecked() {
		return isChecked;
	}




	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}




	public int getType() {
		return type;
	}




	public void setType(int type) {
		this.type = type;
	}




	public String getSapid() {
		return sapid;
	}




	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}




	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
		if(null != answer)
			answers = answer.split(",");
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getStudentTestId() {
		return studentTestId;
	}

	public void setStudentTestId(Long studentTestId) {
		this.studentTestId = studentTestId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getAnswers() {
		return answers;
	}

	public void setAnswers(String[] answers) {
		this.answers = answers;
		if(null != answers)
			answer = StringUtils.join(answers, ',');
	}

	@Override
	public String toString() {
		return "StudentQuestionResponse [sapid="+sapid+", testId="+testId+", answer=" + answer + ", questionId=" + questionId
				+ ", marks=" + marks + "" + " attempt : " + attempt + ""
				+ ", facultyId="+facultyId+", allocatedAnswers= "+allocatedAnswers+", facultyName"+facutlyName
				+ ", studentTestId=" + studentTestId + ", username=" + username + ", answers="
				+ Arrays.toString(answers) + ", getId()= ]";
	}

	@Transient
	private String fileLink;




	public String getFileLink() {
		return fileLink;
	}



	public void setFileLink(String fileLink) {
		this.fileLink = fileLink;
	}
	




	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getUploadType() {
		return uploadType;
	}



	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

}