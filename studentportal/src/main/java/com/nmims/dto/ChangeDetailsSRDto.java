package com.nmims.dto;

import org.springframework.web.multipart.MultipartFile;

public class ChangeDetailsSRDto {
	private Long sapid;
	private String detailType;
	private String currentValue;
	private String updateValue;
	private String device;
	private MultipartFile supportingDocument;
	
	public Long getSapid() {
		return sapid;
	}
	
	public void setSapid(Long sapid) {
		this.sapid = sapid;
	}
	
	public String getDetailType() {
		return detailType;
	}
	
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	
	public String getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	
	public String getUpdateValue() {
		return updateValue;
	}
	
	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public MultipartFile getSupportingDocument() {
		return supportingDocument;
	}

	public void setSupportingDocument(MultipartFile supportingDocument) {
		this.supportingDocument = supportingDocument;
	}

	@Override
	public String toString() {
		return "ChangeDetailsSRDto [sapid=" + sapid + ", detailType=" + detailType + ", currentValue=" + currentValue
				+ ", updateValue=" + updateValue + ", device=" + device + ", supportingDocument=" + supportingDocument
				+ "]";
	}
}
