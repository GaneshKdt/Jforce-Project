package com.nmims.beans;

import java.io.Serializable;
import java.net.URLEncoder;

public class ExamGoToGatewayBeanAPIResponse  implements Serializable  {
	String trackId;
	String error;
	String errorMessage;
	String SECURE_SECRET;
	int totalFees;
	String channel;
	String ACCOUNT_ID;
	String mode;
	String reference_no;
	String amount;
	String description;
	String return_url;
	String name;
	String address;
	String city;
	String country;
	String postal_code;
	String phone;
	String secure_hash;
	public String getSecure_hash() {
		return secure_hash;
	}

	public void setSecure_hash(String secure_hash) {
		this.secure_hash = secure_hash;
	}

	public String getReference_no() {
		return reference_no;
	}

	public void setReference_no(String reference_no) {
		this.reference_no = reference_no;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getV3URL() {
		return V3URL;
	}

	public void setV3URL(String v3url) {
		V3URL = v3url;
	}

	String email;
	String algo;
	String V3URL;
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	String currency;
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	String currency_code;
	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public String getACCOUNT_ID() {
		return ACCOUNT_ID;
	}

	public void setACCOUNT_ID(String aCCOUNT_ID) {
		ACCOUNT_ID = aCCOUNT_ID;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(int totalFees) {
		this.totalFees = totalFees;
	}

	public String getSECURE_SECRET() {
		return SECURE_SECRET;
	}

	public void setSECURE_SECRET(String sECURE_SECRET) {
		SECURE_SECRET = sECURE_SECRET;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
}
