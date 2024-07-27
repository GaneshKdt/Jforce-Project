package com.nmims.beans;

import java.io.Serializable;
import java.util.Map;

public class MBATranscriptBean  implements Serializable  {

	private String sapid;
	private String program;
	private String instructionMedium;
	private String specialisation;
	private String durationOfProgram;
	private String enrollmentYearMonth;
	private String passYearMonth;
	
	private StudentExamBean student;
	private Map<Integer, MBAMarksheetBean> semSubjectList;
	private String logoRequired;
	private boolean softCopy;
	
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
	public String getInstructionMedium() {
		return instructionMedium;
	}
	public void setInstructionMedium(String instructionMedium) {
		this.instructionMedium = instructionMedium;
	}
	public String getSpecialisation() {
		return specialisation;
	}
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}
	public String getDurationOfProgram() {
		return durationOfProgram;
	}
	public void setDurationOfProgram(String durationOfProgram) {
		this.durationOfProgram = durationOfProgram;
	}
	public String getEnrollmentYearMonth() {
		return enrollmentYearMonth;
	}
	public void setEnrollmentYearMonth(String enrollmentYearMonth) {
		this.enrollmentYearMonth = enrollmentYearMonth;
	}
	public String getPassYearMonth() {
		return passYearMonth;
	}
	public void setPassYearMonth(String passYearMonth) {
		this.passYearMonth = passYearMonth;
	}
	public Map<Integer, MBAMarksheetBean> getSemSubjectList() {
		return semSubjectList;
	}
	public void setSemSubjectList(Map<Integer, MBAMarksheetBean> semSubjectList) {
		this.semSubjectList = semSubjectList;
	}
	public String getLogoRequired() {
		return logoRequired;
	}
	public void setLogoRequired(String logoRequired) {
		this.logoRequired = logoRequired;
	}
	public StudentExamBean getStudent() {
		return student;
	}
	public void setStudent(StudentExamBean student) {
		this.student = student;
	}
	public boolean isSoftCopy() {
		return softCopy;
	}
	public void setSoftCopy(boolean softCopy) {
		this.softCopy = softCopy;
	}
	@Override
	public String toString() {
		return "MBATranscriptBean [sapid=" + sapid + ", program=" + program + ", instructionMedium=" + instructionMedium
				+ ", specialisation=" + specialisation + ", durationOfProgram=" + durationOfProgram
				+ ", enrollmentYearMonth=" + enrollmentYearMonth + ", passYearMonth=" + passYearMonth + ", student="
				+ student + ", semSubjectList=" + semSubjectList + ", logoRequired=" + logoRequired + ", softCopy="
				+ softCopy + "]";
	}
	
	
}
