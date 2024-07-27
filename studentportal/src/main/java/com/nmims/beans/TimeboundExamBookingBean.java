package com.nmims.beans;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public class TimeboundExamBookingBean {
	private String year;
	private String month;
	private Integer term;
	private String subject;
	private double amount;
	
	public TimeboundExamBookingBean() {
		super();
	}
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "TimeboundExamBookingBean [year=" + year + ", month=" + month + ", term=" + term + ", subject=" + subject
				+ ", amount=" + amount + "]";
	}
	
}
