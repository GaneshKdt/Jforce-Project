package com.nmims.beans;

import java.io.Serializable;

public class MBAWXExamMarksheetGenerationChecks extends BaseExamBean  implements Serializable  {

	private String timeboundId;
	private String sapid;
	private String term;
	private String subject;
	private String acadsMonth;
	private String acadsYear;
	private String examYear;
	private String examMonth;
	private int numberOfNumberOfPassFailEntries;
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}
	public String getAcadsYear() {
		return acadsYear;
	}
	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public int getNumberOfPassFailEntries() {
		return numberOfNumberOfPassFailEntries;
	}
	public void setNumberOfPassFailEntries(int numberOfNumberOfPassFailEntries) {
		this.numberOfNumberOfPassFailEntries = numberOfNumberOfPassFailEntries;
	}
	@Override
	public String toString() {
		return "MBAWXExamMarksheetGenerationChecks [timeboundId=" + timeboundId + ", sapid=" + sapid + ", term=" + term
				+ ", subject=" + subject + ", acadsMonth=" + acadsMonth + ", acadsYear=" + acadsYear + ", examYear="
				+ examYear + ", examMonth=" + examMonth + ", numberOfNumberOfPassFailEntries=" + numberOfNumberOfPassFailEntries + "]";
	}
	
}
