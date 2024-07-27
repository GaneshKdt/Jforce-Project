package com.nmims.beans;

import java.io.Serializable;

public class DDDetails  implements Serializable {
	
	private String ddno;
	private String bank;
	private String ddDate;
	private String amount;
	private String tranStatus;
	private String email;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getDdno() {
		return ddno;
	}
	public void setDdno(String ddno) {
		this.ddno = ddno;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getDdDate() {
		return ddDate;
	}
	public void setDdDate(String ddDate) {
		this.ddDate = ddDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	

}
