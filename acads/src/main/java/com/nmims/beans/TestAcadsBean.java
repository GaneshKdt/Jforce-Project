package com.nmims.beans;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;

public class TestAcadsBean extends BaseAcadsBean  implements Serializable  {
	//private static final long serialVersionUID = 1L;
	/**
	 * Change Name from TestBean to TestAcadsBean for serializable issue
	 */
	private Long id;
	private Long testId;
	//WeightageDataBean weightageData;
	private Long score;
	private Long wieghtageassigned;
	private String rollNo;
	private String randomQuestion;
	private String testQuestionWeightageReq;
	private String acadMonth;

	private Integer acadYear;
	private String examMonth;
	private Integer examYear;

	private String active;

	private String allowAfterEndDate;

	/* private String dueDate; */

	private String endDate;

	private Integer maxAttempt = 1;

	private Integer attempt;

	private Integer maxScore;

	private Integer duration;

	private Integer passScore;

	private String testName;

	private String sendEmailAlert;

	private String sendSmsAlert;

	private String showResultsToStudents;
	
	//comes from studentstestdetails not in test table
	private String showResult;

	private String startDate;

	private Long courseId;

	private String facultyId;

	private int maxQuestnToShow;

	String idOfCourse;

	private String groupId;

	private String testCompleted;

	private String completionTime;

	private String testType;

	private String testDescription;
	
	private String subject;
	private String program;
	private String programStructure;
	private String consumerType;
	
	private String year;
	private String month;
	
	
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	//for answer checking faulty view
	private int noOfAnswers ;
	
	private Integer consumerProgramStructureId;
	private String consumerTypeId;
	private String programStructureId;
	private String programId;

	private String consumerTypeIdFormValue;
	private String programStructureIdFormValue;
	private String programIdFormValue;
	
	private String liveType;
	

	private String acadsMonth;

	private Integer acadsYear;

	private Integer countOfProgramsApplicableTo;
	private String testConfigIds;
	
	private String applicableType;
	
	private Long referenceId;
	private Integer batchId;
	private Integer moduleBatchId;
	private String referenceBatchOrModuleName;
	
	private String name;
	private String topic;
	
	private String userId;

	private String extendedStartTime;
	private String extendedEndTime;
	
	private String sapidList;
	
	
	private String startDateForMail;
	
	private String iaType;
	
	
	private List<UpgradTestQuestionBean> testQuestions;
	private String  sessionPlanId;
	private String sessionModuleNo;
	private String date;

	
	
	
	public List<UpgradTestQuestionBean> getTestQuestions() {
		return testQuestions;
	}

	public void setTestQuestions(List<UpgradTestQuestionBean> testQuestions) {
		this.testQuestions = testQuestions;
	}	

	private String batchName;
	
	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSessionPlanId() {
		return sessionPlanId;
	}

	public void setSessionPlanId(String sessionPlanId) {
		this.sessionPlanId = sessionPlanId;
	}

	public String getSessionModuleNo() {
		return sessionModuleNo;
	}

	public void setSessionModuleNo(String sessionModuleNo) {
		this.sessionModuleNo = sessionModuleNo;
	}

	public String getStartDateForMail() {
		return startDateForMail;
	}

	public void setStartDateForMail(String startDateForMail) {
		this.startDateForMail = startDateForMail;
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

	public long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(long saveModule) {
		this.referenceId = saveModule;
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
	/*
	 * public Integer getConsumerProgramStructureId() { return
	 * consumerProgramStructureId; }
	 */

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

	/*
	 * public WeightageDataBean getWeightageDataBean() { return weightageData; }
	 * 
	 * public void setWeightageDataBean(WeightageDataBean weightageData) {
	 * this.weightageData = weightageData; }
	 */

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

	/*public static long getSerialversionuid() {
		return serialVersionUID;
	}*/

	public static Logger getLogger() {
		return logger;
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

	private static final Logger logger = LoggerFactory.getLogger(TestAcadsBean.class);

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
			logger.error("Exception", e);
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
 

	//private StudentTestBean studentTest = new StudentTestBean();

	//private List<StudentTestBean> studentTests = new ArrayList<StudentTestBean>();

	private List<String> students = new ArrayList<String>();

	

	public TestAcadsBean() {
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


	

	/*
	 * public StudentTestBean getStudentTest() { return studentTest; }
	 * 
	 * public void setStudentTest(StudentTestBean studentTest) { this.studentTest =
	 * studentTest; }
	 * 
	 * public List<StudentTestBean> getStudentTests() { return studentTests; }
	 * 
	 * public void setStudentTests(List<StudentTestBean> studentTests) {
	 * this.studentTests = studentTests; }
	 */

	public List<String> getStudents() {
		return students;
	}

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
	 * public Integer getDurationLeft() { if (null == duration) {
	 * logger.info("whfirst----------second----12313123------>>>>"); return
	 * duration; } if (null == studentTest.getTestStartTime()) { logger.
	 * info("whent test is not ended and the time reset to 59 minutes--------------------->>>>"
	 * ); return duration * 60; } else { Integer durationLeft = (int) (duration * 60
	 * - (System .currentTimeMillis() - studentTest.getTestStartTime() .getTime()) /
	 * 1000); logger.
	 * info("whent test is not ended and the time reset to 59 minutes-----------second----12313123------>>>>"
	 * ); return durationLeft > 0 ? durationLeft : 2; } }
	 */
	
	@Override
	public String toString() {
		return "*******TestBean ["
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
				+ "\n]********";
	}

	public String getIaType() {
		return iaType;
	}

	public void setIaType(String iaType) {
		this.iaType = iaType;
	}
}