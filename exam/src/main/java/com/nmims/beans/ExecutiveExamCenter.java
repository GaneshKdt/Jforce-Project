package com.nmims.beans;

import java.io.Serializable;

public class ExecutiveExamCenter extends BaseExamBean  implements Serializable  {
	private String centerId;
	private String examCenterName;
	private String city;
	private String capacity;
	private String address;
	private String state;
	private String locality;
	private String starttime;
	private String endtime;
	private int available;
	private int onHold;
	private int booked;
	private int slotsBooked;
	private String ic;
	private String googleMapUrl;
	
	private String batchYear;
	private String batchMonth;
	
	
	
	public String getBatchYear() {
		return batchYear;
	}
	public void setBatchYear(String batchYear) {
		this.batchYear = batchYear;
	}
	public String getBatchMonth() {
		return batchMonth;
	}
	public void setBatchMonth(String batchMonth) {
		this.batchMonth = batchMonth;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
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
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
	public int getOnHold() {
		return onHold;
	}
	public void setOnHold(int onHold) {
		this.onHold = onHold;
	}
	public int getBooked() {
		return booked;
	}
	public void setBooked(int booked) {
		this.booked = booked;
	}
	public int getSlotsBooked() {
		return slotsBooked;
	}
	public void setSlotsBooked(int slotsBooked) {
		this.slotsBooked = slotsBooked;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	@Override
	public String toString() {
		return "ExecutiveExamCenter [centerId=" + centerId
				+ ", examCenterName=" + examCenterName + ", city=" + city
				+ ", capacity=" + capacity + ", address=" + address
				+ ", state=" + state + ", locality=" + locality
				+ ", starttime=" + starttime + ", endtime=" + endtime
				+ ", available=" + available + ", onHold=" + onHold
				+ ", booked=" + booked + ", slotsBooked=" + slotsBooked
				+ ", ic=" + ic + "]";
	}
	public String getGoogleMapUrl() {
		return googleMapUrl;
	}
	public void setGoogleMapUrl(String googleMapUrl) {
		this.googleMapUrl = googleMapUrl;
	}
	
}
