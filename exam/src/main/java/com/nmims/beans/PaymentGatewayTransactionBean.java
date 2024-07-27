package com.nmims.beans;


import java.io.Serializable;

//spring security related changes
public class PaymentGatewayTransactionBean implements Serializable{
	
	private String track_id;
	private String sapid;
	private String amount;
	private String transaction_status;
	private String transaction_id;
	private String response_payment_method;
	private String payment_option;
	private String bank_name;
	private String created_at;
	public String getTrack_id() {
		return track_id;
	}
	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransaction_status() {
		return transaction_status;
	}
	public void setTransaction_status(String transaction_status) {
		this.transaction_status = transaction_status;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getResponse_payment_method() {
		return response_payment_method;
	}
	public void setResponse_payment_method(String response_payment_method) {
		this.response_payment_method = response_payment_method;
	}
	public String getPayment_option() {
		return payment_option;
	}
	public void setPayment_option(String payment_option) {
		this.payment_option = payment_option;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	@Override
	public String toString() {
		return "PaymentGatewayTransactionBean [track_id=" + track_id + ", sapid=" + sapid + ", amount=" + amount
				+ ", transaction_status=" + transaction_status + ", transaction_id=" + transaction_id
				+ ", response_payment_method=" + response_payment_method + ", payment_option=" + payment_option
				+ ", bank_name=" + bank_name + ", created_at=" + created_at + "]";
	}
	
	
	
	

}
