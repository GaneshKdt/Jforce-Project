package com.nmims.beans;

import java.io.Serializable;

public class TransactionBean  implements Serializable  {
	public final static String PAYMENT_METHOD="WALLET";
	public final static String ERRROR="1";
	public final static String SUCCESS="0";
	


public String channel;
public String accountId;
public String referenceNo;
public String amount;
public String mode;
public String currency;
public String currencyCode;
public String description;
public String returnUrl;
public String name;
public String address;
public String city;
public String country;
public String postalCode;
public String phone;
public String email;
public String sapid;
public String requestId;
public String payApi;
public String walletPayApi;



public String getPayApi() {
	return payApi;
}
public void setPayApi(String payApi) {
	this.payApi = payApi;
}
public String getWalletPayApi() {
	return walletPayApi;
}
public void setWalletPayApi(String walletPayApi) {
	this.walletPayApi = walletPayApi;
}
public String getRequestId() {
	return requestId;
}
public void setRequestId(String requestId) {
	this.requestId = requestId;
}
public String getChannel() {
	return channel;
}
public void setChannel(String channel) {
	this.channel = channel;
}
public String getAccountId() {
	return accountId;
}
public void setAccountId(String accountId) {
	this.accountId = accountId;
}
public String getReferenceNo() {
	return referenceNo;
}
public void setReferenceNo(String referenceNo) {
	this.referenceNo = referenceNo;
}
public String getAmount() {
	return amount;
}
public void setAmount(String amount) {
	this.amount = amount;
}
public String getMode() {
	return mode;
}
public void setMode(String mode) {
	this.mode = mode;
}
public String getCurrency() {
	return currency;
}
public void setCurrency(String currency) {
	this.currency = currency;
}
public String getCurrencyCode() {
	return currencyCode;
}
public void setCurrencyCode(String currencyCode) {
	this.currencyCode = currencyCode;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getReturnUrl() {
	return returnUrl;
}
public void setReturnUrl(String returnUrl) {
	this.returnUrl = returnUrl;
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
public String getPostalCode() {
	return postalCode;
}
public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
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
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public static String getSuccess() {
	return SUCCESS;
}




}
