package com.nmims.beans;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.util.AutoPopulatingList;
 

public class StudentTestBean extends BaseExamBean  implements Serializable  {
	private static final long serialVersionUID = 1L;
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";

	private Integer attempt;
	
	private String attemptStatus;
	
	private String testCompleted;

	private Date testEndTime;

	private Date testStartTime;

	private Integer score;

	private Long testId;

	private String testName;

	private String groupId;

	private String rollNo;
	

	private double copyCaseMatchedPercentage;
	
	
	
	
	public double getCopyCaseMatchedPercentage() {
		return copyCaseMatchedPercentage;
	}

	public void setCopyCaseMatchedPercentage(double copyCaseMatchedPercentage) {
		this.copyCaseMatchedPercentage = copyCaseMatchedPercentage;
	}

	public String getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(String attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	private String courseId;
	private String status;
	String courseName;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	@Override
	public String toString() {
		return "StudentTest [attempt=" + attempt + ", testCompleted="
				+ testCompleted + ", testEndTime=" + testEndTime
				+ ", testStartTime=" + testStartTime + ", score=" + score
				+ ", testId=" + testId + ", testName=" + testName
				+ ", groupId=" + groupId + ", rollNo=" + rollNo + ", courseId="
				+ courseId + ", status=" + status + ", courseName="
				+ courseName + ", studentName=" + studentName + ", startDate="
				+ startDate + ", endDate=" + endDate + ", passScore="
				+ passScore + ", maxScore=" + maxScore
				+ ", studentQuestionResponses=" + studentQuestionResponses
				+ ", student= , course= , program= ]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	private String studentName;

	private String startDate;

	private String endDate;

	private Integer passScore;

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

	public Integer getPassScore() {
		return passScore;
	}

	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	private Integer maxScore;

	/**
	 * Non persistent fields
	 */
	private List<StudentQuestionResponseExamBean> studentQuestionResponses = new AutoPopulatingList<StudentQuestionResponseExamBean>(
			StudentQuestionResponseExamBean.class);

	private StudentExamBean student = new StudentExamBean();
	private ProgramExamBean program = new ProgramExamBean();
	/*@JsonIgnore

	@JsonIgnore
	private Course course = new Course();

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@JsonIgnore
	private Program program = new Program();
*/
	/**
	 * 
	 */

	public StudentTestBean() {
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public String getTestCompleted() {
		return testCompleted;
	}

	public void setTestCompleted(String testCompleted) {
		this.testCompleted = testCompleted;
	}

	public Date getTestEndTime() {
		return testEndTime;
	}

	public void setTestEndTime(Date testEndTime) {
		this.testEndTime = testEndTime;
	}

	public Date getTestStartTime() {
		return testStartTime;
	}

	public void setTestStartTime(Date testStartTime) {

		this.testStartTime = testStartTime;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer totalScore) {
		this.score = totalScore;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public String getStudentBeanName() {
		return studentName;
	}

	public void setStudentBeanName(String studentName) {
		this.studentName = studentName;
	}

	public List<StudentQuestionResponseExamBean> getStudentQuestionResponses() {
		return studentQuestionResponses;
	}

	public void setStudentQuestionResponses(
			List<StudentQuestionResponseExamBean> studentQuestionResponses) {
		this.studentQuestionResponses = studentQuestionResponses;
	}

	public StudentExamBean getStudentBean() {
		return student;
	}

	public void setStudentBean(StudentExamBean student) {
		this.student = student;
	}

	public ProgramExamBean getProgramBean() {
		return program;
	}

	public void setProgramBean(ProgramExamBean program) {
		this.program = program;
	}

	public boolean isCompleted() {
		return "Y".equals(testCompleted);
	}

	public String getFirstName() {
		return student.getFirstName();
	}

	public void setFirstName(String firstName) {
		student.setFirstName(firstName);
	}

	public String getLastName() {
		return student.getLastName();
	}

	public void setLastName(String lastName) {
		student.setLastName(lastName);
	}

	public String getProgramName() {
		return program.getProgramname();
	}

	public void setProgramName(String programName) {
		program.setProgramname(programName);
	}
}
