package com.nmims.beans;

import java.io.Serializable;

public class VivaSlotBookingConfigBean implements Serializable {
	private int id;
	private String year;
	private String month;
	private int program_sem_subject_id;
	private String payment_applicable;
	private String payment_amount;
	private String start_date;
	private String end_date;
	private String created_at;
	private String updated_at;
	private String created_by;
	private String updated_by;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(int program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getPayment_applicable() {
		return payment_applicable;
	}
	public void setPayment_applicable(String payment_applicable) {
		this.payment_applicable = payment_applicable;
	}
	public String getPayment_amount() {
		return payment_amount;
	}
	public void setPayment_amount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	@Override
	public String toString() {
		return "VivaSlotBookingConfigBean [id=" + id + ", year=" + year + ", month=" + month
				+ ", program_sem_subject_id=" + program_sem_subject_id + ", payment_applicable=" + payment_applicable
				+ ", payment_amount=" + payment_amount + ", start_date=" + start_date + ", end_date=" + end_date
				+ ", created_at=" + created_at + ", updated_at=" + updated_at + ", created_by=" + created_by
				+ ", updated_by=" + updated_by + "]";
	}
	
	 
	
}
