package com.nmims.beans;

import java.util.ArrayList;
import java.util.List;

public class DocumentFileSet {
	ArrayList<DocumentBean> documents = new ArrayList<>();
	private String sfdcRecordId;
	private String recordType;
	private String aepid;

	public ArrayList<DocumentBean> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<DocumentBean> documents) {
		this.documents = documents;
	}

	public String getSfdcRecordId() {
		return sfdcRecordId;
	}

	public void setSfdcRecordId(String sfdcRecordId) {
		this.sfdcRecordId = sfdcRecordId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getAepid() {
		return aepid;
	}

	public void setAepid(String aepid) {
		this.aepid = aepid;
	}

	@Override
	public String toString() {
		return "DocumentFileSet [documents=" + documents + ", sfdcRecordId=" + sfdcRecordId + ", recordType="
				+ recordType + "]";
	}
}
