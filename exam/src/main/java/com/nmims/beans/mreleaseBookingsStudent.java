package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class mreleaseBookingsStudent implements Serializable  {
	
	private boolean examregistration;
	private int status;
	private String message;
	private String totalExamfees;
	private ArrayList<ExamBookingTransactionBean> data;
	private HashMap<String, String> centers;
	
	
	
	public String getTotalExamfees() {
		return totalExamfees;
	}
	public void setTotalExamfees(String totalExamfees) {
		this.totalExamfees = totalExamfees;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<ExamBookingTransactionBean> getData() {
		return data;
	}
	public void setData(ArrayList<ExamBookingTransactionBean> data) {
		this.data = data;
	}
	public HashMap<String, String> getCenters() {
		return centers;
	}
	public void setCenters(HashMap<String, String> centers) {
		this.centers = centers;
	}
	public boolean getExamregistration() {
		return examregistration;
	}
	public void setExamregistration(boolean examregistration) {
		this.examregistration = examregistration;
	}
}
