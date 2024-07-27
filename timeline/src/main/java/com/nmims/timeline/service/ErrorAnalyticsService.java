package com.nmims.timeline.service;

import java.util.List;

import com.nmims.timeline.model.ErrorAnalytics;

public interface ErrorAnalyticsService {

	String save(String module, String sapid, Exception e, String apiCalled,String data);

	List<ErrorAnalytics> findAllByModule(String module);

}
