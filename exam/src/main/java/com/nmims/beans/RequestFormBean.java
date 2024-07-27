package com.nmims.beans;

import java.io.Serializable;

public class RequestFormBean  implements Serializable  {
	private String sapid;
	private String request_action;
	private String month;
	private String year;
	private int total_amount;
	
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(int total_amount) {
		this.total_amount = total_amount;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getRequest_action() {
		return request_action;
	}
	public void setRequest_action(String request_action) {
		this.request_action = request_action;
	}
	
	
}
