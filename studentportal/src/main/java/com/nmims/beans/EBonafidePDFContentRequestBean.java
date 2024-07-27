package com.nmims.beans;

import java.util.List;

public class EBonafidePDFContentRequestBean {
	
	private String sapId;
	private List<ServiceRequestCustomPDFContentBean> customPDFContent;
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public List<ServiceRequestCustomPDFContentBean> getCustomPDFContent() {
		return customPDFContent;
	}
	public void setCustomPDFContent(List<ServiceRequestCustomPDFContentBean> customPDFContent) {
		this.customPDFContent = customPDFContent;
	}
	@Override
	public String toString() {
		return "EBonafidePDFContentRequestBean [sapId=" + sapId + ", customPDFContent=" + customPDFContent + "]";
	}
	
	

}
