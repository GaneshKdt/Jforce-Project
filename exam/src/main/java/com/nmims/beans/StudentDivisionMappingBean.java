package com.nmims.beans;

import java.io.Serializable;
import java.util.Objects;

public class StudentDivisionMappingBean implements Serializable {
	private String id;
	private String sapId;
	private String divisionId;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String error;

	public String getError() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isErrorRecord() {
		return errorRecord;
	}

	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

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
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(sapId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StudentDivisionMappingBean bean=(StudentDivisionMappingBean)obj;
		return sapId.equals(bean.sapId);
	}
	
	@Override
	public String toString() {
		return "StudentDivisionMappingBean [id=" + id + ", sapId=" + sapId + ", divisionId=" + divisionId
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", error=" + error + "]";
	}

}