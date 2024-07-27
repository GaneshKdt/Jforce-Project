package com.nmims.timeline.service;

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
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.timeline.model.Student;
import com.nmims.timeline.model.StudentMarksBean;
import com.nmims.timeline.repository.FacultyRepository;
import com.nmims.timeline.repository.PostRepository;
import com.nmims.timeline.repository.PostRepositoryForRedis;
import com.nmims.timeline.repository.RegistrationRepository;
import com.nmims.timeline.repository.ResultsRepositoryForRedis;
import com.nmims.timeline.repository.StudentRepository;



@Service
public class ResultsServiceImpl implements ResultsService {
	

	private static final String RESULTS_COUNTER_TABLE_NAME = "RESULTS_COUNTER";
	
    private PostRepository postRepository;
    private PostRepositoryForRedis postRepositoryForRedis;
    private ResultsRepositoryForRedis resultsRepositoryForRedis;
    private StudentRepository studentRepository;
    private RegistrationRepository registrationRepository;
    private FacultyRepository facultyRepository;
    private CounterService counterService;
    

    private FlagsService flagsService;

    
    @Autowired
    private RestTemplate restTemplate;
    

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${SERVER_PATH_FEED_FILES}")
	private String SERVER_PATH_FEED_FILES;
	

	@Value("${SERVER_PORT}")
	private int[] port;
	
	private Stack<String> portNumbers = new Stack<>();
	
	static final Map<String, String> acadsMonthMap;

    static {
    	acadsMonthMap = new LinkedHashMap<>(); 
    	acadsMonthMap.put("Jan", "01");
    	acadsMonthMap.put("Apr", "04");
    	acadsMonthMap.put("Jul", "07");
    	acadsMonthMap.put("Oct", "10");
    }
    
    public ResultsServiceImpl(PostRepository postRepository,
    					   PostRepositoryForRedis postRepositoryForRedis,
    					   StudentRepository studentRepository,
    					   RegistrationRepository registrationRepository,
    					   FacultyRepository facultyRepository,
    					   ResultsRepositoryForRedis resultsRepositoryForRedis,
    					   CounterService counterService,
    					   FlagsService flagsService) {
        this.postRepository = postRepository;
        this.postRepositoryForRedis = postRepositoryForRedis;
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
        this.facultyRepository = facultyRepository;
        this.resultsRepositoryForRedis = resultsRepositoryForRedis;
        this.counterService = counterService;
        this.flagsService = flagsService;
    }
	
	
	
	@Override
	public String setAllResultsDataInRedisCache() {
		
		//1. get all distinct sapids list of results
		List<Student> sapidsList = new ArrayList<>();
		
		try {
			sapidsList = studentRepository.getDistinctSapidsFromPassFail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sapidsList==null || sapidsList.size() < 1) {
			return "No students records found for year-month";
		}
		
		//2. Set results data in redis cache.
		return getResultDataAndSetInRedisCache(sapidsList,"All");
	}


	private String getResultDataAndSetInRedisCache(List<Student> sapidsList, String keyName) {
		StringBuffer errorMessage = new StringBuffer("");
		Integer totalNoOfRecords=sapidsList!=null ? sapidsList.size() : 0;
		String reset="Y";
		int count=0;
		for(Student studentBean : sapidsList) {
			
			errorMessage.append(getResultDataAndSetInRedisCacheForSingleStudent(studentBean));
			if(count==0) {
				reset="Y";
			}else {
				reset="N";
			}
			updateResultsCounter(keyName,totalNoOfRecords,reset);
			count++;
		}
		
		if(StringUtils.isBlank(errorMessage.toString())) {

			return "Success setAllResultsDataInRedisCache! ";
		}else {
			return errorMessage.toString();
		}
	}
	
	private String updateResultsCounter(String keyName, Integer totalNoOfRecords, String reset) {
		return counterService.upsert(RESULTS_COUNTER_TABLE_NAME,keyName,totalNoOfRecords,reset);
	}



	private String getResultDataAndSetInRedisCacheForSingleStudent(Student studentBean) {
		//System.out.println("IN getResultDataAndSetInRedisCacheForSingleStudent() got sapid : "+studentBean.getSapid());
		StudentsDataInRedisBean studentsDataInRedisBean = new StudentsDataInRedisBean();
		
		studentsDataInRedisBean.setResultsData(getResultDataBySapid(studentBean));
		studentsDataInRedisBean.setSapid(studentBean.getSapid());
		
		if(studentsDataInRedisBean.getResultsData() == null) {
			return studentBean.getSapid()+"Unable to get result data.";
		}
		
		return setStudentsDataInRedisBySapid(studentsDataInRedisBean);
		
	}



	private String setStudentsDataInRedisBySapid(StudentsDataInRedisBean studentsDataInRedisBean) {
		return resultsRepositoryForRedis.save(studentsDataInRedisBean);
	}



	private Map<String, List> getResultDataBySapid(Student studentBean) {
		
		Map<String, List> resultsData = new HashMap<>();
		
		resultsData = restApiCallToGetMostRecentResults(studentBean);
		
		return resultsData;
	}



	private Map<String, List> restApiCallToGetMostRecentResults(Student studentBean) {

	      try {
	  	    //String url = SERVER_PATH+"exam/m/getMostRecentResults";
	    	  String url = getMostRecentResultsUrl();
	    	  //System.out.println("IN restApiCallToGetMostRecentResults() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  studentBean.setFromAdmin("Y");
			  HttpEntity<Student> entity = new HttpEntity<Student>(studentBean,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, Map.class).getBody();
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}



	private String getMostRecentResultsUrl() {
		return SERVER_PATH+"/exam/m/getMostRecentResults";
	}



	private String getPortNumber() {

		try {
			if(port.length == 1  ) {
				return port[0]+"";
			}
			if(portNumbers.isEmpty()) {
				for(int i = 0; i<port.length; i++) {
					portNumbers.push(port[i]+"");
				}
				
				return portNumbers.pop();
			}else {
				return portNumbers.pop();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "8181";
		}
		
		
	}



	@Override
	public StudentsDataInRedisBean getResultsDataFromRedisBySapid(String sapid) {
		return resultsRepositoryForRedis.findBySapid(sapid);
	}



	@Override
	public StudentMarksBean setAllResultsDataInRedisCacheByYearMonth(StudentMarksBean studentMarksBean) {
		
		String errorMessageToSetFlag = setFlagInRedis("showResultsFromCache","N");
		errorMessageToSetFlag =errorMessageToSetFlag + setFlagInRedis("movingResultsToCache","Y");
		
		String errorMessage ="";
		//1. get all distinct sapids list of results
		List<Student> sapidsList = new ArrayList<>();
		
		try {
			sapidsList = studentRepository.getDistinctSapidsForResultProcessingYearAndMonth(studentMarksBean.getYear(),studentMarksBean.getMonth());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sapidsList==null || sapidsList.size() < 1) {
			errorMessage=errorMessage+ "No students records found for year-month : "+studentMarksBean.getYear()+"-"+studentMarksBean.getMonth();
		}
		
		//2. Set results data in redis cache.
		errorMessage=errorMessage+ getResultDataAndSetInRedisCache(sapidsList,studentMarksBean.getYear()+"-"+studentMarksBean.getMonth());
		
		studentMarksBean.setErrorMessage(errorMessage);
		
		errorMessageToSetFlag = errorMessageToSetFlag + setFlagInRedis("showResultsFromCache","Y");
		errorMessageToSetFlag =errorMessageToSetFlag + setFlagInRedis("movingResultsToCache","N");
		
		
		return studentMarksBean;
	}



	private String setFlagInRedis(String key, String value) {
		FlagBean bean = new FlagBean();
		bean.setKey(key);
		bean.setValue(value);
		return flagsService.save(bean);
	}



	@Override
	public StudentMarksBean setResultsDataInRedisCacheBySapid(StudentMarksBean studentMarksBean) {
		String returnMessage = getResultDataAndSetInRedisCacheForSingleStudent(studentRepository.findFirstBySapidOrderBySemDesc(studentMarksBean.getSapid()));
		studentMarksBean.setErrorMessage(returnMessage);
		return studentMarksBean;
	}

}
