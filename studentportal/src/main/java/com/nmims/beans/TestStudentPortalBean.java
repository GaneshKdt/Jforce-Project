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

/**
 * old name - TestBean
 * @author
 *
 */
public class TestStudentPortalBean extends BaseStudentPortalBean  implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long score;
	private Long wieghtageassigned;
	private String rollNo;
	private String randomQuestion;
	private String testQuestionWeightageReq;
	private String acadMonth;

	private Integer acadYear;

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
	//comes from students test details
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
	
	private String year;
	private String month;
	

	private String errorMessage = "";
	private boolean errorRecord = false;
	
	//for answer checking faulty view
	private int noOfAnswers ;
	
	
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

	private static final Logger logger = LoggerFactory.getLogger(TestStudentPortalBean.class);

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
 


	private List<String> students = new ArrayList<String>();

	

	public TestStudentPortalBean() {
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

	
	
	@Override
	public String toString() {
		return "TestStudentPortalBean ["
				+ " testName="+testName+"; Year="+year+"; Month="+month+"; Program="+program+"; Subject="+subject+"; StrtDate="+startDate+""
				+ "; EndDate="+endDate+""
				+ "]";
	}
}