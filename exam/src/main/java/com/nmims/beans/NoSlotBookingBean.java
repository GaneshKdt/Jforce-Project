package com.nmims.beans;

import java.io.Serializable;

public class NoSlotBookingBean implements Serializable {
	private Long id;
	private String sapid;
	private Long timeboundId;
	private String type;
	private Long paymentRecordId;
	private String status;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSapid() {
		return sapid;
	}
	
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	
	public Long getTimeboundId() {
		return timeboundId;
	}
	
	public void setTimeboundId(Long timeboundId) {
		this.timeboundId = timeboundId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getPaymentRecordId() {
		return paymentRecordId;
	}
	
	public void setPaymentRecordId(Long paymentRecordId) {
		this.paymentRecordId = paymentRecordId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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
	public String toString() {
		return "NoslotBookingBean [id=" + id + ", sapid=" + sapid + ", timeboundId=" + timeboundId + ", type=" + type
				+ ", paymentRecordId=" + paymentRecordId + ", status=" + status + ", createdBy=" + createdBy
				+ ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + "]";
	}
}
