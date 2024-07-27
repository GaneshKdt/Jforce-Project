package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAMarksheetBean  implements Serializable  {

	private List<MBAPassFailBean> marksList;
	
	private String term;
	private String sapid;
	private String program;
	private StudentExamBean student;
	private String examMonth;
	private String acadMonth;
	private String examYear;
	private String acadYear;
	private String gpa;
	private String cgpa;
	private String remark;
	private boolean termCleared;
	private boolean appearedForTerm;
	
	private String enrollmentMonthYear;
	private String clearExamMonth;
	private String clearExamYear;
	private String ConsumerProgramStructureId;
	
	
	public String getEnrollmentMonthYear() {
		return enrollmentMonthYear;
	}
	public void setEnrollmentMonthYear(String enrollmentMonthYear) {
		this.enrollmentMonthYear = enrollmentMonthYear;
	}
	public List<MBAPassFailBean> getMarksList() {
		return marksList;
	}
	public void setMarksList(List<MBAPassFailBean> marksList) {
		this.marksList = marksList;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public StudentExamBean getStudent() {
		return student;
	}
	public void setStudent(StudentExamBean student) {
		this.student = student;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getGpa() {
		return gpa;
	}
	public void setGpa(String gpa) {
		this.gpa = gpa;
	}
	public String getCgpa() {
		return cgpa;
	}
	public void setCgpa(String cgpa) {
		this.cgpa = cgpa;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public boolean isTermCleared() {
		return termCleared;
	}
	public void setTermCleared(boolean termCleared) {
		this.termCleared = termCleared;
	}
	public boolean isAppearedForTerm() {
		return appearedForTerm;
	}
	public void setAppearedForTerm(boolean appearedForTerm) {
		this.appearedForTerm = appearedForTerm;
	}
	public String getClearExamMonth() {
		return clearExamMonth;
	}
	public void setClearExamMonth(String clearExamMonth) {
		this.clearExamMonth = clearExamMonth;
	}
	public String getClearExamYear() {
		return clearExamYear;
	}
	public void setClearExamYear(String clearExamYear) {
		this.clearExamYear = clearExamYear;
	}
	public String getConsumerProgramStructureId() {
		return ConsumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		ConsumerProgramStructureId = consumerProgramStructureId;
	}
	@Override
	public String toString() {
		return "MBAMarksheetBean [marksList=" + marksList + ", term=" + term + ", sapid=" + sapid + ", program="
				+ program + ", student=" + student + ", examMonth=" + examMonth + ", acadMonth=" + acadMonth
				+ ", examYear=" + examYear + ", acadYear=" + acadYear + ", gpa=" + gpa + ", cgpa=" + cgpa + ", remark="
				+ remark + ", termCleared=" + termCleared + ", appearedForTerm=" + appearedForTerm
				+ ", enrollmentMonthYear=" + enrollmentMonthYear + ", clearExamMonth=" + clearExamMonth
				+ ", clearExamYear=" + clearExamYear + ", ConsumerProgramStructureId=" + ConsumerProgramStructureId
				+ "]";
	}	
}
