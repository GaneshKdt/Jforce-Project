package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BookedResponse  implements Serializable {
	private String examregistration;
	public String getExamregistration() {
		return examregistration;
	}
	public void setExamregistration(String examregistration) {
		this.examregistration = examregistration;
	}
	private ArrayList<ExamBookingTransactionBean> data;
	private HashMap<String, String> centers;
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
	
	
}
