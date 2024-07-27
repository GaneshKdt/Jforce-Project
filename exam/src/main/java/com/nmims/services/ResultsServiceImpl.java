package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.FlagBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.repository.ResultsRepositoryForRedis;
import com.nmims.repository.StudentRepository;



@Service("resultsService")
public class ResultsServiceImpl implements ResultsService {

	@Override
	public String setAllResultsDataInRedisCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentsDataInRedisBean getResultsDataFromRedisBySapid(String sapid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentMarksBean setAllResultsDataInRedisCacheByYearMonth(StudentMarksBean studentMarksBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StudentMarksBean setResultsDataInRedisCacheBySapid(StudentMarksBean studentMarksBean) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
