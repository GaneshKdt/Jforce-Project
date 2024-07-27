package com.nmims.beans;

import java.io.Serializable;

public class EndPointBean implements Serializable {
	private String name;
	private String ip;
	private String type;
	private String hostId;
	private String hostPassword;
	private String zoomUID;
	private int capacity;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostPassword() {
		return hostPassword;
	}
	public void setHostPassword(String hostPassword) {
		this.hostPassword = hostPassword;
	}
	public String getZoomUID() {
		return zoomUID;
	}
	public void setZoomUID(String zoomUID) {
		this.zoomUID = zoomUID;
	}
	
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public String toString() {
		return "EndPointBean [name=" + name + ", ip=" + ip + ", type=" + type + ", hostId=" + hostId + ", hostPassword="
				+ hostPassword + ", zoomUID=" + zoomUID + ", capacity=" + capacity + "]";
	}

}
