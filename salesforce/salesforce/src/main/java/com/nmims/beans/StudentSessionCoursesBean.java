/**
 * 
 */
package com.nmims.beans;

import java.util.List;

/**
 * @author vil_m
 *
 */
public class StudentSessionCoursesBean extends BaseBean {
	
	public static final String KEY_ERROR = "error";
	public static final String KEY_SUCCESS = "success";
	public static final String ROLE = "STUDENT";
	
	private String sapId;
	private String acadYear;
	private String acadMonth;
	private List<Integer> courseIds;
	private String role;
	
	private String status;
	private String message;
	
	/**
	 * 
	 */
	public StudentSessionCoursesBean() {
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
	public StudentSessionCoursesBean(String sapId, String acadYear, String acadMonth, List<Integer> courseIds,
			String role) {
		super();
		this.sapId = sapId;
		this.acadYear = acadYear;
		this.acadMonth = acadMonth;
		this.courseIds = courseIds;
		this.role = role;
	}

	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public List<Integer> getCourseIds() {
		return courseIds;
	}
	public void setCourseIds(List<Integer> courseIds) {
		this.courseIds = courseIds;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
	
}
