package com.nmims.beans;

import java.io.Serializable;

public class EmbaGradePointBean  implements Serializable {
	private int id;
	private String grade;
	private String points;
	private String marksFrom;
	private String marksTill;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getMarksFrom() {
		return marksFrom;
	}
	public void setMarksFrom(String marksFrom) {
		this.marksFrom = marksFrom;
	}
	public String getMarksTill() {
		return marksTill;
	}
	public void setMarksTill(String marksTill) {
		this.marksTill = marksTill;
	}

}
