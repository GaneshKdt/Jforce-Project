package com.nmims.beans;

import java.util.List;

public class BulkTranscriptGenerationBean {
	List<String> serviceRequestId;
	List<String> SRErrorList ;
	String  successList;
	List<String> SRSuccessList;
	List<MarksheetBean> studentForSRList;
	List<String> errorwhileCreatingSuccessfull ;
	private String mergedFileName;

	private List<String> barcodePDFFilePathList;

	public List<String> getBarcodePDFFilePathList() {
		return barcodePDFFilePathList;
	}

	public void setBarcodePDFFilePathList(List<String> barcodePDFFilePathList) {
		this.barcodePDFFilePathList = barcodePDFFilePathList;
	}

	public String getMergedFileName() {
		return mergedFileName;
	}

	public void setMergedFileName(String mergedFileName) {
		this.mergedFileName = mergedFileName;
	}
	public List<String> getServiceRequestId() {
		return serviceRequestId;
	}
	public void setServiceRequestId(List<String> serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public List<String> getSRErrorList() {
		return SRErrorList;
	}
	public void setSRErrorList(List<String> sRErrorList) {
		SRErrorList = sRErrorList;
	}
	public String getSuccessList() {
		return successList;
	}
	public void setSuccessList(String successList) {
		this.successList = successList;
	}
	public List<String> getSRSuccessList() {
		return SRSuccessList;
	}
	public void setSRSuccessList(List<String> sRSuccessList) {
		SRSuccessList = sRSuccessList;
	}
	public List<MarksheetBean> getStudentForSRList() {
		return studentForSRList;
	}
	public void setStudentForSRList(List<MarksheetBean> studentForSRList) {
		this.studentForSRList = studentForSRList;
	}
	public List<String> getErrorwhileCreatingSuccessfull() {
		return errorwhileCreatingSuccessfull;
	}
	public void setErrorwhileCreatingSuccessfull(List<String> errorwhileCreatingSuccessfull) {
		this.errorwhileCreatingSuccessfull = errorwhileCreatingSuccessfull;
	}

	@Override
	public String toString() {
		return "BulkTranscriptGenerationBean [serviceRequestId=" + serviceRequestId + ", SRErrorList=" + SRErrorList
				+ ", successList=" + successList + ", SRSuccessList=" + SRSuccessList + ", studentForSRList="
				+ studentForSRList + ", errorwhileCreatingSuccessfull=" + errorwhileCreatingSuccessfull
				+ ", mergedFileName=" + mergedFileName + ", barcodePDFFilePathList=" + barcodePDFFilePathList + "]";
	}
	
	
}
