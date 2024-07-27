package com.nmims.beans;

public class ServiceRequestCustomPDFContentBean {

	private Long serviceRequestId;
	private String contentPosition;
	private String content;
	
	public Long getServiceRequestId() {
		return serviceRequestId;
	}
	public void setServiceRequestId(Long serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public String getContentPosition() {
		return contentPosition;
	}
	public void setContentPosition(String contentPosition) {
		this.contentPosition = contentPosition;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "ServiceRequestCustomPDFContentBean [serviceRequestId=" + serviceRequestId + ", contentPosition="
				+ contentPosition + ", content=" + content + "]";
	}
	
}
