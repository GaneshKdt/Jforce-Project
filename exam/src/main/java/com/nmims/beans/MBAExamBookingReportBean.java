package com.nmims.beans;

import java.io.Serializable;

public class MBAExamBookingReportBean  implements Serializable  {

	private String examYear;
	private String examMonth;
	
	private MBAExamBookingRequest booking;
	private MBAPaymentRequest paymentDetails;
	private StudentExamBean student;
	
	
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public MBAExamBookingRequest getBooking() {
		return booking;
	}
	public void setBooking(MBAExamBookingRequest booking) {
		this.booking = booking;
	}
	public MBAPaymentRequest getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(MBAPaymentRequest paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	public StudentExamBean getStudent() {
		return student;
	}
	public void setStudent(StudentExamBean student) {
		this.student = student;
	}
	@Override
	public String toString() {
		return "MBAExamBookingReportBean [booking=" + booking + ", paymentDetails=" + paymentDetails + ", student="
				+ student + "]";
	}
	
	
}
