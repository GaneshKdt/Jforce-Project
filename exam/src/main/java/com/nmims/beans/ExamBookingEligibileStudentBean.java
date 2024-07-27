package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingEligibileStudentBean  implements Serializable {

	private String sapid;
	private String program;
	private String consumerType;
	private String vailidyEndMonth;
	private String validityEndYear;
	private String sem;
	private String consumerProgramStructureId;
	private String isLateral;
	private String previousStudentId;
	private String programChanged;
	private String PrgmStructApplicable;
	private String bookingStartDateTime;
	private String bookingEndDateTime;

	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getVailidyEndMonth() {
		return vailidyEndMonth;
	}
	public void setVailidyEndMonth(String vailidyEndMonth) {
		this.vailidyEndMonth = vailidyEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getPreviousStudentId() {
		return previousStudentId;
	}
	public void setPreviousStudentId(String previousStudentId) {
		this.previousStudentId = previousStudentId;
	}
	public String getProgramChanged() {
		return programChanged;
	}
	public void setProgramChanged(String programChanged) {
		this.programChanged = programChanged;
	}
	public String getPrgmStructApplicable() {
		return PrgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		PrgmStructApplicable = prgmStructApplicable;
	}

	public String getBookingStartDateTime() {
		return bookingStartDateTime;
	}

	public void setBookingStartDateTime(String bookingStartDateTime) {
		this.bookingStartDateTime = bookingStartDateTime;
	}

	public String getBookingEndDateTime() {
		return bookingEndDateTime;
	}

	public void setBookingEndDateTime(String bookingEndDateTime) {
		this.bookingEndDateTime = bookingEndDateTime;
	}

}
