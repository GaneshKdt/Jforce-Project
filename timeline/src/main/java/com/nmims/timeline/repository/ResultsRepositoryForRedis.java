package com.nmims.timeline.repository;

import java.util.Map;

import com.nmims.beans.StudentsDataInRedisBean;

public interface ResultsRepositoryForRedis {
	 
	String save(StudentsDataInRedisBean studentsDataInRedisBean);
	    Map<String, StudentsDataInRedisBean> findAll();
	    StudentsDataInRedisBean findBySapid(String sapid);
	   
}
