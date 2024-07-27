package com.nmims.exam.client.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.MBAResponseBean;
import com.nmims.beans.MBAWXPortalExamResultsBean;
import com.nmims.exam.client.ITimeboundExamResultsClient;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("timeboundExamResultsClient")
public class TimeboundExamResultsClient implements ITimeboundExamResultsClient{
	@Autowired
	private RestTemplate restTemplate;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	private final String EXAM_BASE_URL = "exam/m";
	
	private static final Logger logger = LoggerFactory.getLogger(TimeboundExamResultsClient.class);

	public HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
	
	public String getBaseUrl() {
		if (StringUtils.isBlank(SERVER_PATH)) {
			return "https://studentzone-ngasce.nmims.edu/";
		}
		return SERVER_PATH;
	}

	@Override
	public ResponseEntity<MBAWXPortalExamResultsBean> getSubjectWiseStudentMarksRecord(String sapId) {
		logger.info("TimeboundExamResultsClient.getSubjectWiseStudentMarksRecord() - START");
		String body = "{\"sapid\":\""+sapId+"\"}";
		String url = getBaseUrl() + EXAM_BASE_URL + "/getMarksHistoryForStudent";

		logger.info("Get TEE marks hstory of a student API Body:"+body+" URL:"+url);
		return restTemplate.postForEntity(url,
				new HttpEntity<>(body, getHeaders()), MBAWXPortalExamResultsBean.class);
	}
	
	@Override
	public ResponseEntity<MBAWXPortalExamResultsBean> getStudentPassFailRecords(String sapId) {
		logger.info("TimeboundExamResultsClient.getStudentPassFailRecords() - START");
		String body = "{\"sapid\":\""+sapId+"\"}";
		String url = getBaseUrl() + EXAM_BASE_URL + "/getPassFailStatusForStudent";

		logger.info("Get Pass-Fail details of a student API Body:"+body+" URL:"+url);
		return restTemplate.postForEntity(url,
				new HttpEntity<>(body, getHeaders()), MBAWXPortalExamResultsBean.class);
	}

	@Override
	public ResponseEntity<MBAResponseBean> getStudentAllExamBookings(String sapId) {
		logger.info("TimeboundExamResultsClient.getStudentAllExamBookings() - START");
		String body = "{\"sapid\":\""+sapId+"\"}";
		String url = getBaseUrl() + EXAM_BASE_URL + "/getStudentAllExamBookings";

		logger.info("Get all Exam Bookings of a student API Body:"+body+" URL:"+url);
		return restTemplate.postForEntity(url,
				new HttpEntity<>(body, getHeaders()), MBAResponseBean.class);
	}
}
