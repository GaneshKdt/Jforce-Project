package com.nmims.timeline.repository;

import java.util.List;

import com.nmims.timeline.model.TestBean;

public interface UpcomingTestRepositoryForRedis {

	String saveUpcomingTestsForSapid(String sapid, List<TestBean> upcomingTestsForAfterLoginPage);

	List<TestBean> getUpcomingTestsBySapid(String sapid);
	
}
