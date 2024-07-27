package com.nmims.paymentgateways.bean;

import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A Wrapper bean class used to encapsulate payload from multiple incoming APIs
 * 
 * @author Swarup Singh Rajpurohit
 * @since Paytm Webhook integration
 */
public class PayloadWrapper {
	private MultiValueMap<String, String> formdata;

	private JsonNode jsonData;

	public PayloadWrapper(JsonNode jsonData) {
		this.jsonData = jsonData;
	}

	public PayloadWrapper(MultiValueMap<String, String> formdata) {
		this.formdata = formdata;
	}

	public MultiValueMap<String, String> getFormdata() {
		return formdata;
	}

	public void setFormdata(MultiValueMap<String, String> formdata) {
		this.formdata = formdata;
	}

	public JsonNode getJsonData() {
		return jsonData;
	}

	public void setJsonData(JsonNode jsonData) {
		this.jsonData = jsonData;
	}

}
