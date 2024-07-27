package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename NetworkLogsBean to NetworkLogsExamBean
public class NetworkLogsExamBean implements Serializable{

	private int id;
	private int api_id;
	private String name;
	private String sapid;
	private String api;
	private String resquest_time;
	private String response_time;
	private String duration;
	private String response_payload_size;
	private String networkInfo;
	private String status;
	private String error_message;
	private String carrier;
	private String platform;
	private String created_at;
	private String String;
	private long testId;
	private String updated_at;
	private String description;
	
	public int getApi_id() {
		return api_id;
	}
	public void setApi_id(int api_id) {
		this.api_id = api_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getResquest_time() {
		return resquest_time;
	}
	public void setResquest_time(String resquest_time) {
		this.resquest_time = resquest_time;
	}
	public String getResponse_time() {
		return response_time;
	}
	public void setResponse_time(String response_time) {
		this.response_time = response_time;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getResponse_payload_size() {
		return response_payload_size;
	}
	public void setResponse_payload_size(String response_payload_size) {
		this.response_payload_size = response_payload_size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getNetworkInfo() {
		return networkInfo;
	}
	public void setNetworkInfo(String networkInfo) {
		this.networkInfo = networkInfo;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getString() {
		return String;
	}
	public void setString(String string) {
		String = string;
	}
	public long getTestId() {
		return testId;
	}
	public void setTestId(long testId) {
		this.testId = testId;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "NetworkLogsBean [id=" + id + ", api_id=" + api_id + ", sapid=" + sapid + ", api=" + api
				+ ", resquest_time=" + resquest_time + ", response_time=" + response_time + ", duration=" + duration
				+ ", response_payload_size=" + response_payload_size + ", status=" + status + ", error_message="
				+ error_message + ", carrier=" + carrier + ", platform=" + platform + ", networkInfo=" + networkInfo
				+ "]";
	}
}
