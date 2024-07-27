package com.nmims.helpers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.StudentMarksBean;

@Service("resultsToCacheHelper")
public class ResultsToCacheHelper {

	
	
	@Async
	public void restApiCallForSetResultsInCache(ExamOrderExamBean exam,String SERVER_PATH) {

		RestTemplate restTemplate = new RestTemplate();
		try {
			StudentMarksBean requestData = new StudentMarksBean();
			requestData.setYear(exam.getYear());
			requestData.setMonth(exam.getMonth());
			  String url = SERVER_PATH+"timeline/api/results/setAllResultsDataInRedisCacheByYearMonth";
	    	  HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<StudentMarksBean> entity = new HttpEntity<StudentMarksBean>(requestData,headers);
			  
			   restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, StudentMarksBean.class).getBody();
		} catch (RestClientException e) {
			
		}
	
	}
}
