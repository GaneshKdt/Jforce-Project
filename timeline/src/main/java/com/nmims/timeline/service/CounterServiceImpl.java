 package com.nmims.timeline.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nmims.timeline.repository.CounterRepositoryForRedis;
import com.nmims.timeline.repository.FacultyRepository;
import com.nmims.timeline.repository.PostRepository;
import com.nmims.timeline.repository.PostRepositoryForRedis;
import com.nmims.timeline.repository.RegistrationRepository;
import com.nmims.timeline.repository.ResultsRepositoryForRedis;
import com.nmims.timeline.repository.StudentRepository;

@Service
public class CounterServiceImpl implements CounterService {
	
    private PostRepository postRepository;
    private PostRepositoryForRedis postRepositoryForRedis;
    private ResultsRepositoryForRedis resultsRepositoryForRedis;
    private StudentRepository studentRepository;
    private RegistrationRepository registrationRepository;
    private FacultyRepository facultyRepository;
    private CounterRepositoryForRedis counterRepositoryForRedis;
    
    @Autowired
    private RestTemplate restTemplate;
    

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${SERVER_PATH_FEED_FILES}")
	private String SERVER_PATH_FEED_FILES;
	

	@Value("${SERVER_PORT}")
	private int[] port;
	
	
	static final Map<String, String> acadsMonthMap;

    static {
    	acadsMonthMap = new LinkedHashMap<>(); 
    	acadsMonthMap.put("Jan", "01");
    	acadsMonthMap.put("Apr", "04");
    	acadsMonthMap.put("Jul", "07");
    	acadsMonthMap.put("Oct", "10");
    }
    
    public CounterServiceImpl(PostRepository postRepository,
    					   PostRepositoryForRedis postRepositoryForRedis,
    					   StudentRepository studentRepository,
    					   RegistrationRepository registrationRepository,
    					   FacultyRepository facultyRepository,
    					   CounterRepositoryForRedis counterRepositoryForRedis,
    					   ResultsRepositoryForRedis resultsRepositoryForRedis) {
        this.postRepository = postRepository;
        this.postRepositoryForRedis = postRepositoryForRedis;
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
        this.facultyRepository = facultyRepository;
        this.resultsRepositoryForRedis = resultsRepositoryForRedis;
        this.counterRepositoryForRedis = counterRepositoryForRedis;
    }
	

    @Override
	public String save(String tableName,String keyName,String counterData) {
		 
		 return counterRepositoryForRedis.save(tableName,keyName,counterData);
	 }
	 
	 @Override
	 public String findByTableNameKeyName(String tableName,String keyName) {

		 String counterData = counterRepositoryForRedis.findByTableNameKeyName(tableName,keyName);
		 
		 return counterData;
	 }


	@Override
	public String upsert(String resultsCounterTableName, String keyName, Integer totalNoOfRecords, String reset) {
		
		String errorMessge =""; 

		if("Y".equalsIgnoreCase(reset)) {
			errorMessge = save(resultsCounterTableName,keyName,0+"~"+totalNoOfRecords);
		}else {
			
			String counterData = findByTableNameKeyName(resultsCounterTableName,keyName);
			if(StringUtils.isBlank(counterData)) {
				errorMessge = save(resultsCounterTableName,keyName,0+"~"+totalNoOfRecords);
			}else {
					try {
						String countString = counterData.split("~")[0];
						Integer count = Integer.parseInt(countString);
						
						if(count < totalNoOfRecords) {
							errorMessge = save(resultsCounterTableName,keyName,(count+1)+"~"+totalNoOfRecords);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						errorMessge = errorMessge + e.getMessage();
					}
			}
		}
		
		if(StringUtils.isBlank(errorMessge)) {
			return "Success in upsert for resultsCounterTableName: "+resultsCounterTableName+". keyName: "+keyName;
		}else {	
			return "Error in upsert for resultsCounterTableName: "+resultsCounterTableName+". keyName: "+keyName+", Error : "+errorMessge;
		
		}
	}
	
	
}
