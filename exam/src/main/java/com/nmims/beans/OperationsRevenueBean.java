package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class OperationsRevenueBean extends BaseExamBean  implements Serializable {
	private String startDate;
	private String endDate;
	private String revenueSource;
	private double amount;
	private double totalAmount;
	private String lastName;
    private String firstName;
    private String fullName;
    private String lc;
    private String ic;
    private ArrayList<String> lc_list;
    private ArrayList<String> ic_list;
    private String programType;
    private String paymentOption;
    private String serviceRequestType;
    
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	public ArrayList<String> getLc_list() {
		return lc_list;
	}
	public void setLc_list(ArrayList<String> lc_list) {
		this.lc_list = lc_list;
	}
	public ArrayList<String> getIc_list() {
		return ic_list;
	}
	public void setIc_list(ArrayList<String> ic_list) {
		this.ic_list = ic_list;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFullName() {
		return firstName +" "+lastName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getRevenueSource() {
		return revenueSource;
	}
	public void setRevenueSource(String revenueSource) {
		this.revenueSource = revenueSource;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "OperationsRevenueBean [startDate=" + startDate + ", endDate=" + endDate + ", revenueSource="
				+ revenueSource + ", amount=" + amount + ", LC=" + lc + ", LC list=" + lc_list + ", IC list=" + ic_list+"]";
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	public String getServiceRequestType() {
		return serviceRequestType;
	}
	public void setServiceRequestType(String serviceRequestType) {
		this.serviceRequestType = serviceRequestType;
	}

	

}
