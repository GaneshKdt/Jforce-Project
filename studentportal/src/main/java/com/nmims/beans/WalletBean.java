package com.nmims.beans;

import java.io.Serializable;

//Extending ExamBookingTransactionBean since all the transaction parameters are mentioned under that bean
public class WalletBean extends ExamBookingTransactionStudentPortalBean  implements Serializable{
	public static final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	public static final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful"; 
	public static final String TRAN_STATUS_SUCCESSFUL = "Payment Successful"; 
	public static final String TRAN_STATUS_EXPIRED = "Expired";
	
	public static final String REQUEST_STATUS_PAYMENT_FAILED = "Payment Failed";
	public static final String REQUEST_STATUS_SUBMITTED = "Submitted";
	public static final String REQUEST_STATUS_CLOSED = "Submitted";
	public static final String CREDIT = "CREDIT";
	public static final String DEBIT = "DEBIT";
	
public Long id;

public String balance;
public String walletId;
public String userId; //Different from sapid. Can span across multiple users//
public String transactionType;
public String walletBalance;
public String startDate;
public String endDate;
public String tid;



public String getTid() {
	return tid;
}
public void setTid(String tid) {
	this.tid = tid;
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
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getBalance() {
	return balance;
}
public void setBalance(String balance) {
	this.balance = balance;
}
public String getWalletId() {
	return walletId;
}
public void setWalletId(String walletId) {
	this.walletId = walletId;
}
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getTransactionType() {
	return transactionType;
}
public void setTransactionType(String transactionType) {
	this.transactionType = transactionType;
}
public String getWalletBalance() {
	return walletBalance;
}
public void setWalletBalance(String walletBalance) {
	this.walletBalance = walletBalance;
}

}
