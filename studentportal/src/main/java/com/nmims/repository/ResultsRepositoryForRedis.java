package com.nmims.repository;

import java.util.Map;

import com.nmims.beans.StudentsDataInRedisBean;

public interface ResultsRepositoryForRedis {
	 
	void save(StudentsDataInRedisBean studentsDataInRedisBean) throws Exception;
	    Map<String, StudentsDataInRedisBean> findAll();
	    StudentsDataInRedisBean findBySapid(String sapid);
	   
}
