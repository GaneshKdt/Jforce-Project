package com.nmims.services;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.ForumResponseBean;
import com.nmims.daos.LeadDAO;

@Service("forumService")
public class ForumService {

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ApplicationContext act;

	@Autowired
	StudentService studentService;

	@Autowired
	LeadDAO leadDAO;

	private static final Logger logger = LoggerFactory.getLogger(ForumService.class);

	public ForumResponseBean getForumList(HttpServletRequest request, String programSemSubjectId, String cycle) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json, application/xml");
		headers.add("Content-Type", "application/json");
		ForumResponseBean response = new ForumResponseBean();
		Map<String, Object> requestmap = new HashMap<>();

		try {
			String url = SERVER_PATH + "forum/m/getForumByPssId";
			HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(requestmap, headers);
			requestmap.put("programSemSubjectId", programSemSubjectId);
			requestmap.put("cycle", cycle);

			response = restTemplate.exchange(url, HttpMethod.POST, entity, ForumResponseBean.class).getBody();

		} catch (Exception e) {
			logger.error("Error while getting forum list:", e);
		}
		return response;
	}

}
