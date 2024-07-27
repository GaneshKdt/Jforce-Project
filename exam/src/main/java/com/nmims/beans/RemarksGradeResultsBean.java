/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class RemarksGradeResultsBean extends BaseExamBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//start - BUSINESS Constants
	
	public static final String ATTEMPTED = "ATTEMPTED";
	public static final String RIA = "RIA";
	public static final String NV = "NV";
	public static final String ABSENT = "AB";//ABSENT
	public static final String CC = "CC";//Copy Case
	
	//public static final String GRADE_SATISFACTORY = "SATISFACTORY";
	//public static final String GRADE_NOTSATISFACTORY = "NOT SATISFACTORY";
	
	//end - BUSINESS Constants
	
	//start - PROGRAMMING Constants
	
	//public static final String KEY_ERROR = "error";
	//public static final String KEY_SUCCESS = "success";
	
	//public static final String TEXT_RESULT_LIVE = "YES";
	//public static final String TEXT_RESULT_NOT_LIVE = "NO";
	
	//end - PROGRAMMING Constants
	
	//start - DB Constants
	public static final String ROWS_NOT_DELETED = "Y";
	
	public static final Integer PROCESSED = 1;//for processed - 0 not processed, 1 processed. Initially set not processed.
	//public static final Integer NOTPROCESSED = 0;
	
	public static final Integer RESULT_LIVE = 1;//Displays Result
	//public static final Integer RESULT_NOT_LIVE = 0;//Hides Result
	
	public static final Integer STATUS_RESET_PASSFAIL = -1;
	//public static final Integer STATUS_PASS = 1;
	//public static final Integer STATUS_FAIL = 0;
	
	//public static final String STATE_PASS = "Y";
	//public static final String STATE_FAIL = "N";
	
	//end - DB Constants
	
	private String status;
	private String message;
	
	private Integer id;
	private String active;
	private Integer processed;
	private boolean pass;
	private Integer resultLive;
	
	private String sapid;
	private String sem;
	private String subject;
	private String grade;
	private String remarks;
	private String failReason;
	
	private String productType;//used from Mobile display result.
	private String gradingType;//used from Mobile display result.
	
	private String scoreTotal;
	
	/**
	 * 
	 */
	public RemarksGradeResultsBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sem
	 * @param subject
	 * @param grade
	 */
	public RemarksGradeResultsBean(String sem, String subject, String grade, String year, String month) {
		super();
		this.sem = sem;
		this.subject = subject;
		this.grade = grade;
		this.setYear(year);
		this.setMonth(month);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public Integer getResultLive() {
		return resultLive;
	}

	public void setResultLive(Integer resultLive) {
		this.resultLive = resultLive;
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

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getGradingType() {
		return gradingType;
	}

	public void setGradingType(String gradingType) {
		this.gradingType = gradingType;
	}

	public String getScoreTotal() {
		return scoreTotal;
	}

	public void setScoreTotal(String scoreTotal) {
		this.scoreTotal = scoreTotal;
	}
}
