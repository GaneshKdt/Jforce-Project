package com.nmims.beans;


import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;



/**
 * The persistent class for the student_question_response database table.
 * 
 */
//spring security related changes rename StudentQuestionResponseBean to StudentQuestionResponseExamBean
public class StudentQuestionResponseExamBean extends BaseExamBean  implements Serializable  {
	private static final long serialVersionUID = 1L;

	private String answer;

	private Double marks;
	
	private Long questionId;
	
	private Long studentTestId;
	
	private String username;

	private Long id;
	private Long testId;
	private String sapid;
	private int type;
	private String facultyId;
	private int isChecked;
	private double maxMarks;
	private String question;
	private int attempt;
	
	private String[] answers;
	
	private String remark;
	
	private String testName;
	private double questionMarks;

	private String userId;
	private String url;
	


	private String firstName;

	private String lastName;
	

	private double peerToPeerMatchingPercentage;
	
	private Integer copyCaseThreshold;
	
	private String attemptStatus;
	private String uploadType;
    private int minNoOfQuestions;
	private String optionData;
	private String typeString;
    private String answerSavedStatus;
    
	
	public String getAnswerSavedStatus() {
		return answerSavedStatus;
	}



	public void setAnswerSavedStatus(String answerSavedStatus) {
		this.answerSavedStatus = answerSavedStatus;
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



	public double getQuestionMarks() {
		return questionMarks;
	}



	public void setQuestionMarks(double questionMarks) {
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
	private int allocatedAnswers;
	private String facutlyName;
	public StudentQuestionResponseExamBean() {
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



	public double getMaxMarks() {
		return maxMarks;
	}




	public void setMaxMarks(double maxMarks) {
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

	public Double getMarks() {
		return marks;
	}

	public void setMarks(Double marks) {
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

	public String getOptionData() {
		return optionData;
	}
	
	public void setOptionData(String optionData) {
		this.optionData = optionData;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

}