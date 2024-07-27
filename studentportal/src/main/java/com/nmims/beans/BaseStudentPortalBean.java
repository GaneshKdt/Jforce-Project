package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - BaseBean
 * @author others and vil_m
 *
 */
public class BaseStudentPortalBean  implements Serializable {
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	@Override
	public String toString() {
		return "BaseStudentPortalBean [createdBy=" + createdBy + ", createdDate="
				+ createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + "]";
	}
	
	
	//Added for test module Start
		protected String compareStringAndSet(String value, String compare, String defaultValue) {
			return compare.equalsIgnoreCase(value) ? compare : defaultValue;
		}
		
		protected String checkYElseSetN(String value) {
			return compareStringAndSet(value, "Y", "N");
		}
		protected String formatDate(String date) {
			if(null == date) return date;
			if(date.length() > 19) {
				return date.substring(0, 19).replace(' ', 'T');
			} else {
				return date.replace(' ', 'T');
			}
		}
		//Added for test module End
		
}
