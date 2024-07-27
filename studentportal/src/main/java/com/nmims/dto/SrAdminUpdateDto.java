package com.nmims.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class containing fields of ServiceRequest Bean required for the requestBody of /saveRequestStatusAndReason API
 * @author Raynal Dcunha
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SrAdminUpdateDto implements Serializable {
	@Id
	private Long srId;
	
	private String serviceRequestType;
	private String studentSapid;
	private String requestStatus;
	private String cancellationReason;
	
	@JsonProperty("srId")
	public Long getSrId() {
		return srId;
	}
	
	@JsonProperty("id")
	public Long getId() {
		return srId;
	}
	
	@JsonProperty("id")
	public void setId(Long srId) {
		this.srId = srId;
	}
	
	public String getServiceRequestType() {
		return serviceRequestType;
	}

	public void setServiceRequestType(String serviceRequestType) {
		this.serviceRequestType = serviceRequestType;
	}

	@JsonProperty("studentSapid")
	public String getStudentSapid() {
		return studentSapid;
	}
	
	@JsonProperty("sapId")
	public String getSapid() {
		return studentSapid;
	}
	
	@JsonProperty("sapId")
	public void setSapid(String studentSapid) {
		this.studentSapid = studentSapid;
	}
	
	public String getRequestStatus() {
		return requestStatus;
	}
	
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	public String getCancellationReason() {
		return cancellationReason;
	}
	
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	@Override
	public String toString() {
		return "SrAdminUpdateDto [srId=" + srId + ", serviceRequestType=" + serviceRequestType + ", studentSapid="
				+ studentSapid + ", requestStatus=" + requestStatus + ", cancellationReason=" + cancellationReason
				+ "]";
	}
}