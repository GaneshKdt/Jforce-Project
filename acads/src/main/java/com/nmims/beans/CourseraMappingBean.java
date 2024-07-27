package com.nmims.beans;

import java.time.LocalDateTime;

public class CourseraMappingBean {
	private String sapId;
	private int count;
	private LocalDateTime expiryDate;
	private int coursera_program_id;
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
	public int getCoursera_program_id() {
		return coursera_program_id;
	}
	public void setCoursera_program_id(int coursera_program_id) {
		this.coursera_program_id = coursera_program_id;
	}
	
	@Override
	public String toString() {
		return "CourseraMappingBean [sapId=" + sapId + ", count=" + count + ", expiryDate=" + expiryDate
				+ ", coursera_program_id=" + coursera_program_id + "]";
	}


}
