/**
 * 
 */
package com.nmims.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author vil_m
 *
 */
public class StudentSessionCoursesDTO extends BaseDTO implements Serializable {

	private String sapId;
	private String acadYear;
	private String acadMonth;
	private List<Integer> courseIds;

	/**
	 * 
	 */
	public StudentSessionCoursesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sapId
	 * @param acadYear
	 * @param acadMonth
	 * @param courseIds
	 * @param role
	 */
	public StudentSessionCoursesDTO(String sapId, String acadYear, String acadMonth, List<Integer> courseIds) {
		super();
		this.sapId = sapId;
		this.acadYear = acadYear;
		this.acadMonth = acadMonth;
		this.courseIds = courseIds;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
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

	public List<Integer> getCourseIds() {
		return courseIds;
	}

	public void setCourseIds(List<Integer> courseIds) {
		this.courseIds = courseIds;
	}

}
