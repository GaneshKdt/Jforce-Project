package com.nmims.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class ExamCenterSlotMappingBean  implements Serializable  {
	
	private String examcenterId;
    private String date;
    private String starttime;
    private String endtime;
    private String capacity;
    private String booked;
    private String onHold;
    private String year;
    private String month;
    private String available;
    private String Day;
    private String examCenterName;
    private String city;
    private String address;
	
    
    public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getExamcenterId() {
		return examcenterId;
	}
	public void setExamcenterId(String examcenterId) {
		this.examcenterId = examcenterId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getCapacity() {
	
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getOnHold() {
		return onHold;
	}
	public void setOnHold(String onHold) {
		this.onHold = onHold;
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
	public String getDay() {
		return Day;
	}
	public void setDay(String examDay) {
		Day = examDay;
	}
	
	
	
    
    
    
}
