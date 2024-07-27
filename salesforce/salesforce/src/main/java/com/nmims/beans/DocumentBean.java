package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class DocumentBean implements Serializable{
	
	private String id;
	private String documentName;
	private String documentStatus;
	private MultipartFile file;
	private String sfdcDocumentRecordId;
	private String documentURL;
	private ArrayList<String> fedExInValidPinCodeList;
	
	
	public ArrayList<String> getFedExInValidPinCodeList() {
		return fedExInValidPinCodeList;
	}
	public void setFedExInValidPinCodeList(ArrayList<String> fedExInValidPinCodeList) {
		this.fedExInValidPinCodeList = fedExInValidPinCodeList;
	}
	public String getDocumentURL() {
		return documentURL;
	}
	public void setDocumentURL(String documentURL) {
		this.documentURL = documentURL;
	}
	public String getSfdcDocumentRecordId() {
		return sfdcDocumentRecordId;
	}
	public void setSfdcDocumentRecordId(String sfdcDocumentRecordId) {
		this.sfdcDocumentRecordId = sfdcDocumentRecordId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getDocumentStatus() {
		if(documentStatus == null || "null".equals(documentStatus)){
			return "";
		}
		return documentStatus;
	}
	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	@Override
	public String toString() {
		return "DocumentBean [id=" + id + ", documentName=" + documentName + ", documentStatus=" + documentStatus
				+ ", file=" + file + ", sfdcDocumentRecordId=" + sfdcDocumentRecordId + ", documentURL=" + documentURL
				+ "]";
	}
	
	
	
	
	
	
	
	

}
