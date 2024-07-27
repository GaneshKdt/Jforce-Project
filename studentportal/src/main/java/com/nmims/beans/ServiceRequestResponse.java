package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public class ServiceRequestResponse extends BaseStudentPortalBean implements Serializable{

	private String error;
	private String errorMessage;
	private String paymentResponse;
	private String sapid;
	private String trackId;

	private ArrayList<String> srTypes;
	 
	public ArrayList<String> getSrTypes() {
		return srTypes;
	}
	public void setSrTypes(ArrayList<String> srTypes) {
		this.srTypes = srTypes;
	}

	private List<ServiceRequestStudentPortal> response;

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getPaymentResponse() {
		return paymentResponse;
	}
	public void setPaymentResponse(String paymentResponse) {
		this.paymentResponse = paymentResponse;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public List<ServiceRequestStudentPortal> getResponse() {
		return response;
	}
	public void setResponse(List<ServiceRequestStudentPortal> response) {
		this.response = response;
	}

	
}
