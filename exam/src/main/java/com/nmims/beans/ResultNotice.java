package com.nmims.beans;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


/**
 * The persistent class for the program database table.
 * 
 */
public class ResultNotice extends BaseExamBean implements Serializable {
	private String Year;
	private String month;
	private String program;
	private String programStructure;
	private String title;
	private String description;
	private String type;
	private List<String> programList;
	public List<String> getProgramList() {
		return programList;
	}
	public void setProgramList(List<String> programList) {
		this.programList = programList;
	}
	public String getYear() {
		return Year;
	}
	public void setYear(String year) {
		Year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}


	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ResultNotice [Year=" + Year + ", month=" + month + ", program=" + program + ", programStructure="
				+ programStructure + ", title=" + title + ", description=" + description + ", type=" + type
				+ ", programList=" + programList + "]";
	}
	
	
	
}
