package com.nmims.timeline.model;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="exam.upgrad_test", schema="exam")
public class MBAXTestBean  implements Serializable{

	/*
	 id, year, month, acadYear, acadMonth, 
	 testName, testDescription, startDate, endDate, subject, 
	 courseId, maxQuestnToShow, showResultsToStudents, active, facultyId, 
	 maxAttempt, randomQuestion, testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, 
	 sendSmsAlert, maxScore, duration, passScore, testType, 
	 createdBy, createdDate, lastModifiedBy, lastModifiedDate, consumerTypeIdFormValue, 
	 programStructureIdFormValue, programIdFormValue, applicableType, referenceId, emailSent, 
	 smsSent, isPlagiarismCheckDone 
	 * */
	
	private static final long serialVersionUID = 1L;


	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Transient
	private Long testId;
	@Transient
	private Long score;
	@Transient
	private Long wieghtageassigned;
	@Transient
	private String rollNo;
	
	@Transient
	private String randomQuestion;
	
	@Transient
	private String testQuestionWeightageReq;
	
	private String acadMonth;

	private Integer acadYear;
	
	@Transient
	private String examMonth;
	@Transient
	private Integer examYear;

	private String active;

	private String allowAfterEndDate;

	/* private String dueDate; */

	private String endDate;

	private Integer maxAttempt = 1;

	@Transient
	private Integer attempt;

	private Integer maxScore;

	private Integer duration;

	private Integer passScore;

	private String testName;

	private String sendEmailAlert;

	private String sendSmsAlert;

	private String showResultsToStudents;
	
	//comes from studentstestdetails not in test table
	@Transient
	private String showResult;

	private String startDate;

	@Transient
	private Long courseId;

	private String facultyId;

	private int maxQuestnToShow;

	@Transient
	String idOfCourse;

	@Transient
	private String groupId;

	@Transient
	private String testCompleted;

	@Transient
	private String completionTime;

	private String testType;

	private String testDescription;
	
	private String subject;
	@Transient
	private String program;
	@Transient
	private String programStructure;
	@Transient
	private String consumerType;
	
	private String year;
	private String month;
	
	
	@Transient
	private String errorMessage = "";
	@Transient
	private boolean errorRecord = false;
	
	//for answer checking faulty view
	@Transient
	private int noOfAnswers ;
	
	@Transient
	private Integer consumerProgramStructureId;
	@Transient
	private String consumerTypeId;
	@Transient
	private String programStructureId;
	@Transient
	private String programId;

	private String consumerTypeIdFormValue;
	private String programStructureIdFormValue;
	private String programIdFormValue;
	
	@Transient
	private String liveType;
	

	@Transient
	private String acadsMonth;

	@Transient
	private Integer acadsYear;

	@Transient
	private Integer countOfProgramsApplicableTo;
	@Transient
	private String testConfigIds;
	
	private String applicableType;
	
	private Integer referenceId;
	
	@Transient
	private Integer batchId;
	
	@Transient
	private Integer moduleBatchId;
	@Transient
	private String referenceBatchOrModuleName;
	
	@Transient
	private String name;
	@Transient
	private String topic;
	
	@Transient
	private String userId;

	@Transient
	private String extendedStartTime;
	@Transient
	private String extendedEndTime;
	
	@Transient
	private String sapidList;
	
	
	//added following attributes to get Student test details in test
	@Transient
	private String testStartedOn;
	@Transient
	private String testEndedOn;
	
	@Transient
	private Integer noOfAnswersToEvaluate;
	
	//Added For Tee Results
	@Transient
	private String tEEScore;
	@Transient
	private String isResultLive;
	//Added For IA Evaluate Reports
	@Transient
	private String facultyName;
	@Transient
	private String sessionName;
	@Transient
	private String sapid;
	@Transient
	private String firstName;
	@Transient
	private String lastName;
	@Transient
	private String noOfDescriptive;
	@Transient
	private String noOfQuestions;
	@Transient
	private String evaluated;
	@Transient
	private String batch;
	
	

	@Transient
	private int remainingTime;
	

	@Transient
	private String sem;
	

	@Transient
	private String questionType;

//	following added for preview as stud for faculty
	
	@Transient
	private String previewedByFaculty;
	
	@Transient
	private String  attemptStatus;
	
	@Transient
	private String answer; 
	
	@Transient
	private String question;
	@Transient
	private String remark;
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

	public String getNoOfQuestions() {
		return noOfQuestions;
	}

	public void setNoOfQuestions(String noOfQuestions) {
		this.noOfQuestions = noOfQuestions;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	
	public String getPreviewedByFaculty() {
		return previewedByFaculty;
	}

	public void setPreviewedByFaculty(String previewedByFaculty) {
		this.previewedByFaculty = previewedByFaculty;
	}

	@Transient
	private String iaType;

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public Integer getNoOfAnswersToEvaluate() {
		return noOfAnswersToEvaluate;
	}

	public void setNoOfAnswersToEvaluate(Integer noOfAnswersToEvaluate) {
		this.noOfAnswersToEvaluate = noOfAnswersToEvaluate;
	}

	public String getSapidList() {
		return sapidList;
	}

	public void setSapidList(String sapidList) {
		this.sapidList = sapidList;
	}

	public String getExtendedStartTime() {
		return extendedStartTime;
	}

	public void setExtendedStartTime(String extendedStartTime) {
		this.extendedStartTime = extendedStartTime;
	}

	public String getExtendedEndTime() {
		return extendedEndTime;
	}

	public void setExtendedEndTime(String extendedEndTime) {
		this.extendedEndTime = extendedEndTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getModuleBatchId() {
		return moduleBatchId;
	}

	public void setModuleBatchId(Integer moduleBatchId) {
		this.moduleBatchId = moduleBatchId;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getReferenceBatchOrModuleName() {
		return referenceBatchOrModuleName;
	}

	public void setReferenceBatchOrModuleName(String referenceBatchOrModuleName) {
		this.referenceBatchOrModuleName = referenceBatchOrModuleName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getApplicableType() {
		return applicableType;
	}

	public void setApplicableType(String applicableType) {
		this.applicableType = applicableType;
	}

	/**
	 * @return the testId
	 */
	public Long getTestId() {
		return testId;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setTestId(Long testId) {
		this.testId = testId;
	}

	/**
	 * @return the consumerProgramStructureId
	 */
	public Integer getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	/**
	 * @param consumerProgramStructureId the consumerProgramStructureId to set
	 */
	public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	/**
	 * @return the testConfigIds
	 */
	public String getTestConfigIds() {
		return testConfigIds;
	}

	/**
	 * @param testConfigIds the testConfigIds to set
	 */
	public void setTestConfigIds(String testConfigIds) {
		this.testConfigIds = testConfigIds;
	}

	/**
	 * @return the countOfProgramsApplicableTo
	 */
	public Integer getCountOfProgramsApplicableTo() {
		return countOfProgramsApplicableTo;
	}

	/**
	 * @param countOfProgramsApplicableTo the countOfProgramsApplicableTo to set
	 */
	public void setCountOfProgramsApplicableTo(Integer countOfProgramsApplicableTo) {
		this.countOfProgramsApplicableTo = countOfProgramsApplicableTo;
	}

	/**
	 * @return the acadsMonth
	 */
	public String getAcadsMonth() {
		return acadsMonth;
	}

	/**
	 * @param acadsMonth the acadsMonth to set
	 */
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}

	/**
	 * @return the acadsYear
	 */
	public Integer getAcadsYear() {
		return acadsYear;
	}

	/**
	 * @param acadsYear the acadsYear to set
	 */
	public void setAcadsYear(Integer acadsYear) {
		this.acadsYear = acadsYear;
	}

	/**
	 * @return the liveType
	 */
	public String getLiveType() {
		return liveType;
	}

	/**
	 * @param liveType the liveType to set
	 */
	public void setLiveType(String liveType) {
		this.liveType = liveType;
	}

	/**
	 * @return the examMonth
	 */
	public String getExamMonth() {
		return examMonth;
	}

	/**
	 * @param examMonth the examMonth to set
	 */
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	/**
	 * @return the examYear
	 */
	public Integer getExamYear() {
		return examYear;
	}

	/**
	 * @param examYear the examYear to set
	 */
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}

	/**
	 * @return the programStructure
	 */
	public String getProgramStructure() {
		return programStructure;
	}

	/**
	 * @param programStructure the programStructure to set
	 */
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	/**
	 * @return the consumerType
	 */
	public String getConsumerType() {
		return consumerType;
	}

	/**
	 * @param consumerType the consumerType to set
	 */
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	/**
	 * @return the consumerTypeIdFormValue
	 */
	public String getConsumerTypeIdFormValue() {
		return consumerTypeIdFormValue;
	}

	/**
	 * @param consumerTypeIdFormValue the consumerTypeIdFormValue to set
	 */
	public void setConsumerTypeIdFormValue(String consumerTypeIdFormValue) {
		this.consumerTypeIdFormValue = consumerTypeIdFormValue;
	}

	/**
	 * @return the programStructureIdFormValue
	 */
	public String getProgramStructureIdFormValue() {
		return programStructureIdFormValue;
	}

	/**
	 * @param programStructureIdFormValue the programStructureIdFormValue to set
	 */
	public void setProgramStructureIdFormValue(String programStructureIdFormValue) {
		this.programStructureIdFormValue = programStructureIdFormValue;
	}

	/**
	 * @return the programIdFormValue
	 */
	public String getProgramIdFormValue() {
		return programIdFormValue;
	}

	/**
	 * @param programIdFormValue the programIdFormValue to set
	 */
	public void setProgramIdFormValue(String programIdFormValue) {
		this.programIdFormValue = programIdFormValue;
	}

	/**
	 * @return the programStructureId
	 */
	public String getProgramStructureId() {
		return programStructureId;
	}

	/**
	 * @param programStructureId the programStructureId to set
	 */
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * @return the consumerTypeId
	 */
	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	/**
	 * @param consumerTypeId the consumerTypeId to set
	 */
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	public int getNoOfAnswers() {
		return noOfAnswers;
	}

	public void setNoOfAnswers(int noOfAnswers) {
		this.noOfAnswers = noOfAnswers;
	}

	public String getShowResult() {
		return showResult;
	}

	public void setShowResult(String showResult) {
		this.showResult = showResult;
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

	public String getTestQuestionWeightageReq() {
		return testQuestionWeightageReq;
	}

	public void setTestQuestionWeightageReq(String testQuestionWeightageReq) {
		this.testQuestionWeightageReq = testQuestionWeightageReq;
	}

	public String getRandomQuestion() {
		return randomQuestion;
	}

	public void setRandomQuestion(String randomQuestion) {
		this.randomQuestion = randomQuestion;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}



	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public Long getWieghtageassigned() {
		return wieghtageassigned;
	}

	public void setWieghtageassigned(Long wieghtageassigned) {
		this.wieghtageassigned = wieghtageassigned;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}


	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime, int duration) {

		DateFormat date = new SimpleDateFormat("yyyy-mm-dd");
		int hour = completionTime.getHours();
		int minutes = completionTime.getMinutes();

		String time = hour + ":" + minutes;

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date d;
		String newTime = null;
		try {
			d = df.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, duration);
			newTime = date.format(completionTime) + " "
					+ df.format(cal.getTime());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			////logger.error("Exception", e);
		}

		this.completionTime = newTime;

	}

	public int getMaxQuestnToShow() {
		return maxQuestnToShow;
	}

	public void setMaxQuestnToShow(int maxQuestnToShow) {
		this.maxQuestnToShow = maxQuestnToShow;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getTestDescription() {
		return testDescription;
	}

	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}

	public String getTestCompleted() {
		return testCompleted;
	}

	public void setTestCompleted(String testCompleted) {
		this.testCompleted = testCompleted;
	}

	public String getIdOfCourse() {
		return idOfCourse;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setIdOfCourse(String idOfCourse) {
		this.idOfCourse = idOfCourse;
	}
 
	

	public MBAXTestBean() {
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	public Integer getAcadYear() {
		return acadYear;
	}

	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getAllowAfterEndDate() {
		return allowAfterEndDate;
	}

	public void setAllowAfterEndDate(String allowAfterEndDate) {
		this.allowAfterEndDate = checkYElseSetN(allowAfterEndDate);
	}

	/*
	 * public String getDueDate() { return formatDate(dueDate); }
	 * 
	 * public void setDueDate(String dueDate) { this.dueDate = dueDate; }
	 */
	public String getEndDate() {
		return formatDate(endDate);
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getMaxAttempt() {
		return maxAttempt;
	}

	public void setMaxAttempt(Integer maxAttempt) {
		this.maxAttempt = maxAttempt;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getPassScore() {
		if(passScore == null) {
			return 0;
		}else {
		return passScore;
		}
	}

	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getSendEmailAlert() {
		return sendEmailAlert;
	}

	public void setSendEmailAlert(String sendEmailAlert) {
		this.sendEmailAlert = checkYElseSetN(sendEmailAlert);
	}

	public String getSendSmsAlert() {
		return sendSmsAlert;
	}

	public void setSendSmsAlert(String sendSmsAlert) {
		this.sendSmsAlert = checkYElseSetN(sendSmsAlert);
	}

	public String getShowResultsToStudents() {
		return showResultsToStudents;
	}

	public void setShowResultsToStudents(String showResultsToStudents) {
		this.showResultsToStudents = checkYElseSetN(showResultsToStudents);
	}

	public String getStartDate() {
		return formatDate(startDate);
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	

	public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

/*	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}*/

/*	public String getCourseName() {
		return course.getCourseName();
	}

	public void setCourseName(String courseName) {
		course.setCourseName(courseName);
	}*/


	/*public void setStudents(List<String> students) {
		this.students = students;
		for (String username : students) {
			StudentTest studentTest = new StudentTest();
			studentTest.setUsername(username);
			studentTest.setTestId(getId());
			studentTests.add(studentTest);
		}
	}*/
	
	/*
	public Integer getDurationLeft() {
		if (null == duration) {
			//logger.info("whfirst----------second----12313123------>>>>");
			return duration;
		}
		if (null == studentTest.getTestStartTime()) {
			//logger.info("whent test is not ended and the time reset to 59 minutes--------------------->>>>");
			return duration * 60;
		} else {
			Integer durationLeft = (int) (duration * 60 - (System
					.currentTimeMillis() - studentTest.getTestStartTime()
					.getTime()) / 1000);
			//logger.info("whent test is not ended and the time reset to 59 minutes-----------second----12313123------>>>>");
			return durationLeft > 0 ? durationLeft : 2;
		}
	}
	*/
	
	public String getTestStartedOn() {
		return testStartedOn;
	}

	public void setTestStartedOn(String testStartedOn) {
		this.testStartedOn = testStartedOn;
	}
	
	public String getTestEndedOn() {
		return testEndedOn;
	}

	public void setTestEndedOn(String testEndedOn) {
		this.testEndedOn = testEndedOn;
	}
	
	public String gettEEScore() {
		return tEEScore;
	}

	public void settEEScore(String tEEScore) {
		this.tEEScore = tEEScore;
	}

	public String getIsResultLive() {
		return isResultLive;
	}

	public void setIsResultLive(String isResultLive) {
		this.isResultLive = isResultLive;
	}


	

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}


	
	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}


	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNoOfDescriptive() {
		return noOfDescriptive;
	}

	public void setNoOfDescriptive(String noOfDescriptive) {
		this.noOfDescriptive = noOfDescriptive;
	}

	public String getEvaluated() {
		return evaluated;
	}

	public void setEvaluated(String evaluated) {
		this.evaluated = evaluated;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}


	@Override
	public String toString() {
		return "*******MBAXTestBean ["
				+ "id : "+id+"; testId : "+testId
				+ ";\n testName="+testName+"; Year="+year+"; Month="+month+"; "
				+ "\nProgram="+program+"; Subject="+subject+"; \nStrtDate="+startDate+""
				+ "; EndDate="+endDate+" "
				+ "\n consumerProgramStructureId : "+consumerProgramStructureId
				+"; consumerTypeId : "+consumerTypeId + 
				"; programStructureId : "+programStructureId + 
				"; programId : "+programId +
				"; \n applicableType : "+applicableType +
				"; referenceId : "+referenceId+"; userId : "+userId 
				+"; \n TestConfigIds :"+testConfigIds+ 
				";\n acadMonth : "+acadMonth+ 
				"; acadYear : "+acadYear+ 
				"; Month : "+month+
				"; Year : "+year+ 
				"; examMonth : "+examMonth+
				"; examYear : "+examYear
				+ "\n liveType : "+liveType
				+ "\n startedOn :" + testStartedOn
				+ "\n endedOn : " + testEndedOn 
				+ "\n tEEScore : " + tEEScore 
				+ "\n previewedByFaculty : " + previewedByFaculty 
				+ "\n isResultLive : "+isResultLive + "]********\n"
				+"BaseBean [sapid=" + sapid + ", year=" + year + ", month="
				+ month + ", createdBy=" + createdBy + ", createdDate="
				+ createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", tranDateTime="
				+ tranDateTime + ", date=" + date + "]";
	}



	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getIaType() {
		return iaType;
	}

	public void setIaType(String iaType) {
		this.iaType = iaType;
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
	

	//From basebean end
}

