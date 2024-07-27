/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author vil_m
 *
 */
public class RemarksGradeBean extends BaseExamBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//start - BUSINESS Constants
	public static final String REMARK_CC = "Copy Case";
	public static final String REMARK_ANS = "ANS";
	
	public static final String ATTEMPTED = "ATTEMPTED";
	public static final String RIA = "RIA";
	public static final String NV = "NV";
	public static final String ABSENT = "AB";//ABSENT
	public static final String CC = "CC";//Copy Case
	
	public static final String GRADE_SATISFACTORY = "SATISFACTORY";
	public static final String GRADE_NOTSATISFACTORY = "NOT SATISFACTORY";
	
	//end - BUSINESS Constants
	
	//start - PROGRAMMING Constants
	public static final String KEY_ERROR = "error";
	public static final String KEY_SUCCESS = "success";
	
	public static final String ASSIGNMENT_RESULT_LIVE = "Y";
	public static final String ASSIGNMENT_RESULT_NOTLIVE = "N";
	
	public static final String TEXT_RESULT_LIVE = "YES";
	public static final String TEXT_RESULT_NOT_LIVE = "NO";
	
	public static final String ASSIGNMENT_SUBMITTED = "ASSIGNMENT_SUBMITTED";
	public static final String ASSIGNMENT_NOT_SUBMITTED = "ASSIGNMENT_NOT_SUBMITTED";
	
	//end - PROGRAMMING Constants
	
	//start - DB Constants
	public static final String ROWS_NOT_DELETED = "Y";
	
	public static final Integer PROCESSED = 1;//for processed - 0 not processed, 1 processed. Initially set not processed.
	public static final Integer NOTPROCESSED = 0;
	
	public static final Integer RESULT_LIVE = 1;//Displays Result
	public static final Integer RESULT_NOT_LIVE = 0;//Hides Result
	
	public static final Integer STATUS_RESET_PASSFAIL = -1;
	public static final Integer STATUS_PASS = 1;
	public static final Integer STATUS_FAIL = 0;
	
	public static final String STATE_PASS = "Y";
	public static final String STATE_FAIL = "N";
	
	public static final String DB_NULL = "null";
	public static final String DB_CURRENT_TIMESTAMP = "current_timestamp";
	//end - DB Constants
	
	private String status;
	private String message;
	
	//private String year;
	//private String month;
	@NotNull(message="Null Score")
	@Max(value=30, message="Invalid Score (Maximum 30)")
	private Integer scoreIA;
	private Integer scoreTotal;
	private Integer scoreWritten;
	private Integer graceMarks;
	
	private Integer programSemSubjectId;
	
	//page1
	private CommonsMultipartFile fileData;
	
	//page2
	private String studentType; //consumerType
	private String programStructure;
	private String name;
	
	@NotNull(message="Null Program")
	//@NotBlank(message="Blank Program")
	private String program;
	
	//page3, page15
	private String acadYear;
	private String acadMonth;
	
	//page5
	private Integer id;
	private String active;
	private Integer passStatus;
	private boolean pass;
	private String failReason;
	
	//page4
	@NotNull
	@Pattern(regexp="\\d{11}+", message="Incorrect length SapId")
	private String sapid;
	
	private String sem;
	
	//page6AG
	@NotNull(message="Null Subject")
//	@NotBlank(message="Blank Subject")
	private String subject;
	private String grade;
	
	//page7
	private String remarks;
	
	//page8
	private String assignmentMarksLive;
	
	//page9
	private String centerCode;
	private String centerName;
	private String studentsResultType;//Y=Pass, N=Fail Students
	
	private String studentTypeId; //consumerTypeId
	private String programStructureId;
	private String programId;
	
	private Integer processed;
	private Integer resultLive;
	
	//page15
	private String assignmentType;
	
	public RemarksGradeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param year
	 * @param month
	 * @param sapid
	 * @param sem
	 * @param studentType
	 * @param programStructure
	 * @param program
	 * @param subject
	 */
	public RemarksGradeBean(String status, String year, String month, String sapid, String sem, String studentType,
			String programStructure, String program, String subject) {
		super();
		this.setYear(year);
		this.setMonth(month);
		this.status = status;
		this.sapid = sapid;
		this.sem = sem;
		this.studentType = studentType;
		this.programStructure = programStructure;
		this.program = program;
		this.subject = subject;
	}

	/*public String getYear() {
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
	}*/

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getProgramStructure() {
		return programStructure;
	}

	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAssignmentMarksLive() {
		return assignmentMarksLive;
	}

	public void setAssignmentMarksLive(String assignmentMarksLive) {
		this.assignmentMarksLive = assignmentMarksLive;
	}

	public Integer getScoreIA() {
		return scoreIA;
	}

	public void setScoreIA(Integer scoreIA) {
		this.scoreIA = scoreIA;
	}

	public Integer getProgramSemSubjectId() {
		return programSemSubjectId;
	}

	public void setProgramSemSubjectId(Integer programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScoreTotal() {
		return scoreTotal;
	}

	public void setScoreTotal(Integer scoreTotal) {
		this.scoreTotal = scoreTotal;
	}

	public String getStudentTypeId() {
		return studentTypeId;
	}

	public void setStudentTypeId(String studentTypeId) {
		this.studentTypeId = studentTypeId;
	}

	public String getProgramStructureId() {
		return programStructureId;
	}

	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Integer getScoreWritten() {
		return scoreWritten;
	}

	public void setScoreWritten(Integer scoreWritten) {
		this.scoreWritten = scoreWritten;
	}

	public Integer getGraceMarks() {
		return graceMarks;
	}

	public void setGraceMarks(Integer graceMarks) {
		this.graceMarks = graceMarks;
	}

	public Integer getProcessed() {
		return processed;
	}

	public void setProcessed(Integer processed) {
		this.processed = processed;
	}

	public boolean isPass() {
		return pass;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getStatus() {
		return status;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getResultLive() {
		return resultLive;
	}

	public void setResultLive(Integer resultLive) {
		this.resultLive = resultLive;
	}

	public String getCenterCode() {
		return centerCode;
	}

	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getStudentsResultType() {
		return studentsResultType;
	}

	public void setStudentsResultType(String studentsResultType) {
		this.studentsResultType = studentsResultType;
	}

	public Integer getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(Integer passStatus) {
		this.passStatus = passStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAcadYear() {
		return acadYear;
	}

	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}
	
}
