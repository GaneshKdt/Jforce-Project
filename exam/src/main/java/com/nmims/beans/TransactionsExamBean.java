package com.nmims.beans;

public class TransactionsExamBean {
	
	private String first_name;
	private String email_id;
	private String mobile;
	private String return_url;
	private String portal_return_url;
	
	private String track_id;
	private String sapid;
	private String type;
	private String amount;
	private String transaction_status;
	private String transaction_id;
	private String request_id;
	private String merchant_ref_no;
	private String secure_hash;
	private String description;
	private String response_amount;
	private String response_code;
	private String response_payment_method;
	private String response_message;
	private String response_transaction_date_time;
	private String error;
	private String is_flagged;
	private String payment_id;
	private String payment_option;
	private String bank_name;
	private String source;
	private String created_by;
	private String updated_by;
	private String created_at;
	private String updated_at;
	private String response_method;
	
	public String getResponse_method() {
		return response_method;
	}
	public void setResponse_method(String response_method) {
		this.response_method = response_method;
	}
	public String getPortal_return_url() {
		return portal_return_url;
	}
	public void setPortal_return_url(String portal_return_url) {
		this.portal_return_url = portal_return_url;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public String getMerchant_ref_no() {
		return merchant_ref_no;
	}
	public void setMerchant_ref_no(String merchant_ref_no) {
		this.merchant_ref_no = merchant_ref_no;
	}
	public String getSecure_hash() {
		return secure_hash;
	}
	public void setSecure_hash(String secure_hash) {
		this.secure_hash = secure_hash;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResponse_amount() {
		return response_amount;
	}
	public void setResponse_amount(String response_amount) {
		this.response_amount = response_amount;
	}
	public String getResponse_code() {
		return response_code;
	}
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	public String getResponse_payment_method() {
		return response_payment_method;
	}
	public void setResponse_payment_method(String response_payment_method) {
		this.response_payment_method = response_payment_method;
	}
	public String getResponse_message() {
		return response_message;
	}
	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}
	public String getResponse_transaction_date_time() {
		return response_transaction_date_time;
	}
	public void setResponse_transaction_date_time(String response_transaction_date_time) {
		this.response_transaction_date_time = response_transaction_date_time;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getIs_flagged() {
		return is_flagged;
	}
	public void setIs_flagged(String is_flagged) {
		this.is_flagged = is_flagged;
	}
	public String getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
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
	
	
	@Override
	public String toString() {
		return "TransactionsBean [track_id=" + track_id + ", sapid=" + sapid + ", type=" + type + ", amount=" + amount
				+ ", transaction_status=" + transaction_status
				+ ", transaction_id=" + transaction_id + ", request_id=" + request_id + ", merchant_ref_no="
				+ merchant_ref_no + ", secure_hash=" + secure_hash + ", description=" + description
				+ ", response_amount=" + response_amount + ", response_code=" + response_code
				+ ", response_payment_method=" + response_payment_method + ", response_message=" + response_message
				+ ", response_transaction_date_time=" + response_transaction_date_time + ", error=" + error
				+ ", is_flagged=" + is_flagged + ", payment_id=" + payment_id + ", payment_option=" + payment_option
				+ ", bank_name=" + bank_name + ", source=" + source + ", created_by=" + created_by + ", updated_by="
				+ updated_by + ", created_at=" + created_at + ", updated_at=" + updated_at + "]";
	}
	
	
	
}

