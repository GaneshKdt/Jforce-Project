package com.nmims.timeline.model;

public class CounterBean {
	String tableName;
	String keyName;
	String counterData;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getCounterData() {
		return counterData;
	}
	public void setCounterData(String counterData) {
		this.counterData = counterData;
	}
	
	@Override
	public String toString() {
		return "CounterBean [tableName=" + tableName + ", keyName=" + keyName + ", counterData=" + counterData + "]";
	}
	
	
	
}
