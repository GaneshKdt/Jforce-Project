package com.nmims.beans;

import java.io.Serializable;

public class SRTeeRevaluationReportBean implements Serializable{
	private String sNo;
	private int examYear;
	private String examMonth;
	private int serviceRequest_no;
	private long sapid;
	private String evaluatorName;
	private String studentName;
	private String program;
	private String sem;
	private String assessment_Name;
	private String subCode;
	private String testTaker_name;
	private String testTaker_EmailId;
	private String testInvitation_Key;
	private String testID;
	private String testTaker_UniqueID;
	private String oringinalEvaluator_Name;
	private float original_Sec_4_Marks;
	private float original_Total;
	private int score_Rounded;

	public String getEvaluatorName() {
		return evaluatorName;
	}

	public void setEvaluatorName(String evaluatorName) {
		this.evaluatorName = evaluatorName;
	}

	public String getTestTaker_name() {
		return testTaker_name;
	}

	public void setTestTaker_name(String testTaker_name) {
		this.testTaker_name = testTaker_name;
	}

	public String getsNo() {
		return sNo;
	}

	public void setsNo(String sNo) {
		this.sNo = sNo;
	}

	public int getExamYear() {
		return examYear;
	}

	public void setExamYear(int examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public int getServiceRequest_no() {
		return serviceRequest_no;
	}
	

	
	public void setServiceRequest_no(int serviceRequest_no) {
		this.serviceRequest_no = serviceRequest_no;
	}

	public long getSapid() {
		return sapid;
	}

	public void setSapid(long sapid) {
		this.sapid = sapid;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getAssessment_Name() {
		return assessment_Name;
	}

	public void setAssessment_Name(String assessment_Name) {
		this.assessment_Name = assessment_Name;
	}



	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	public String getTestTaker_EmailId() {
		return testTaker_EmailId;
	}

	public void setTestTaker_EmailId(String testTaker_EmailId) {
		this.testTaker_EmailId = testTaker_EmailId;
	}

	public String getTestInvitation_Key() {
		return testInvitation_Key;
	}

	public void setTestInvitation_Key(String testInvitation_Key) {
		this.testInvitation_Key = testInvitation_Key;
	}

	public String getTestID() {
		return testID;
	}

	public void setTestID(String testID) {
		this.testID = testID;
	}

	public String getTestTaker_UniqueID() {
		return testTaker_UniqueID;
	}

	public void setTestTaker_UniqueID(String testTaker_UniqueID) {
		this.testTaker_UniqueID = testTaker_UniqueID;
	}

	public String getOringinalEvaluator_Name() {
		return oringinalEvaluator_Name;
	}

	public void setOringinalEvaluator_Name(String oringinalEvaluator_Name) {
		this.oringinalEvaluator_Name = oringinalEvaluator_Name;
	}

	public float getOriginal_Sec_4_Marks() {
		return original_Sec_4_Marks;
	}

	public void setOriginal_Sec_4_Marks(float original_Sec_4_Marks) {
		this.original_Sec_4_Marks = original_Sec_4_Marks;
	}

	public float getOriginal_Total() {
		return original_Total;
	}

	public void setOriginal_Total(float original_Total) {
		this.original_Total = original_Total;
	}

	public int getScore_Rounded() {
		return score_Rounded;
	}

	public void setScore_Rounded(int score_Rounded) {
		this.score_Rounded = score_Rounded;
	}

	@Override
	public String toString() {
		return "SRTeeRevaluationReportBean [sNo=" + sNo + ", examYear=" + examYear + ", examMonth=" + examMonth
				+ ", serviceRequest_no=" + serviceRequest_no + ", sapid=" + sapid + ", evaluatorName=" + evaluatorName
				+ ", studentName=" + studentName + ", program=" + program + ", sem=" + sem + ", assessment_Name="
				+ assessment_Name + ", subCode=" + subCode + ", testTaker_name=" + testTaker_name
				+ ", testTaker_EmailId=" + testTaker_EmailId + ", testInvitation_Key=" + testInvitation_Key
				+ ", testID=" + testID + ", testTaker_UniqueID=" + testTaker_UniqueID + ", oringinalEvaluator_Name="
				+ oringinalEvaluator_Name + ", original_Sec_4_Marks=" + original_Sec_4_Marks + ", original_Total="
				+ original_Total + ", score_Rounded=" + score_Rounded + "]";
	}

}
