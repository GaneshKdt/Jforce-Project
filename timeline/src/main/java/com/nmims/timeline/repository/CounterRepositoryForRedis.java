package com.nmims.timeline.repository;

public interface CounterRepositoryForRedis {
	
	 String save(String tableName,String keyName,String counterData);
	 
	 String findByTableNameKeyName(String tableName,String keyName);
		    
	
}
