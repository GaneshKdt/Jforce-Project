package com.nmims.beans;

import java.io.Serializable;

public class ZoomIntegrationBean  implements Serializable  {
	private String id;
	private String topic;
	private String downloadurl;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	
	
}
