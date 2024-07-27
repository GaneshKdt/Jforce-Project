package com.nmims.beans;

import java.io.Serializable;

public class MBAXExamCenterSlotMappingBean  implements Serializable  {

	private String year;
	private String month;
	private String centerId;
	private String examCenterName;
	private String examDate;
	private String examStartTime;
	private String city;
	private String address;
	private String capacity;
	private String booked;
	private String onHold;
	private String available;
	public String getYear() {
		return year;
	}
	public String getMonth() {
		return month;
	}
	public String getCenterId() {
		return centerId;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public String getExamDate() {
		return examDate;
	}
	public String getExamStartTime() {
		return examStartTime;
	}
	public String getCity() {
		return city;
	}
	public String getAddress() {
		return address;
	}
	public String getCapacity() {
		return capacity;
	}
	public String getBooked() {
		return booked;
	}
	public String getOnHold() {
		return onHold;
	}
	public String getAvailable() {
		return available;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public void setOnHold(String onHold) {
		this.onHold = onHold;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	@Override
	public String toString() {
		return "MBAXExamCenterSlotMappingBean [year=" + year + ", month=" + month + ", centerId=" + centerId
				+ ", examCenterName=" + examCenterName + ", examDate=" + examDate + ", examStartTime=" + examStartTime
				+ ", city=" + city + ", address=" + address + ", capacity=" + capacity + ", booked=" + booked
				+ ", onHold=" + onHold + ", available=" + available + "]";
	}
	
}
