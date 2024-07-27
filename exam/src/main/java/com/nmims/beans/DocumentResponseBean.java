package com.nmims.beans;

import java.util.ArrayList;
import java.util.List;

public class DocumentResponseBean {
	private String status;
	private String error;
	private String errorMessage;
	private ArrayList<ExamBookingTransactionBean> feeReceipts;
	private ArrayList<ExamBookingTransactionBean> srFeeReceipts;
	private ArrayList<ExamBookingTransactionBean> assignmentFeeReceipts;
	private ArrayList<ExamBookingTransactionBean> projectFeeReceipts;
	private ArrayList<ExamBookingTransactionBean> hallTickets;
	private ArrayList<ExamBookingTransactionBean> pcpBookings;;
	private ArrayList<StudentExamBean> admissionPaymentReceipt;

	public ArrayList<ExamBookingTransactionBean> getFeeReceipts() {
		return feeReceipts;
	}

	public void setFeeReceipts(ArrayList<ExamBookingTransactionBean> feeReceipts) {
		this.feeReceipts = feeReceipts;
	}

	public ArrayList<ExamBookingTransactionBean> getHallTickets() {
		return hallTickets;
	}

	public void setHallTickets(ArrayList<ExamBookingTransactionBean> hallTickets) {
		this.hallTickets = hallTickets;
	}

	public ArrayList<ExamBookingTransactionBean> getPcpBookings() {
		return pcpBookings;
	}

	public void setPcpBookings(ArrayList<ExamBookingTransactionBean> pcpBookings) {
		this.pcpBookings = pcpBookings;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public ArrayList<StudentExamBean> getAdmissionPaymentReceipt() {
		return admissionPaymentReceipt;
	}

	public void setAdmissionPaymentReceipt(ArrayList<StudentExamBean> admissionPaymentReceipt) {
		this.admissionPaymentReceipt = admissionPaymentReceipt;
	}
	
	public ArrayList<ExamBookingTransactionBean> getSrFeeReceipts() {
		return srFeeReceipts;
	}

	public void setSrFeeReceipts(ArrayList<ExamBookingTransactionBean> srFeeReceipts) {
		this.srFeeReceipts = srFeeReceipts;
	}

	public ArrayList<ExamBookingTransactionBean> getAssignmentFeeReceipts() {
		return assignmentFeeReceipts;
	}

	public void setAssignmentFeeReceipts(ArrayList<ExamBookingTransactionBean> assignmentFeeReceipt) {
		this.assignmentFeeReceipts = assignmentFeeReceipt;
	}

	public ArrayList<ExamBookingTransactionBean> getProjectFeeReceipts() {
		return projectFeeReceipts;
	}

	public void setProjectFeeReceipts(ArrayList<ExamBookingTransactionBean> proectFeeReceipts) {
		this.projectFeeReceipts = proectFeeReceipts;
	}

	@Override
	public String toString() {
		return "DocumentResponseBean [status=" + status + ", error=" + error + ", errorMessage=" + errorMessage
				+ ", feeReceipts=" + feeReceipts + ", srFeeReceipts=" + srFeeReceipts + ", assignmentFeeReceipts="
				+ assignmentFeeReceipts + ", projectFeeReceipts=" + projectFeeReceipts + ", hallTickets=" + hallTickets
				+ ", pcpBookings=" + pcpBookings + ", admissionPaymentReceipt=" + admissionPaymentReceipt + "]";
	}



}
