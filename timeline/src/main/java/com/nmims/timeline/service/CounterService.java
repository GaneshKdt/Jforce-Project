package com.nmims.timeline.service;

public interface CounterService {

	 String save(String tableName,String keyName,String counterData);
	 
	 String findByTableNameKeyName(String tableName,String keyName);

	String upsert(String resultsCounterTableName, String keyName, Integer totalNoOfRecords, String reset);
	
}
