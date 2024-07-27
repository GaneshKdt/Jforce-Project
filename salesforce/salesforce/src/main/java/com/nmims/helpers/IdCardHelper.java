package com.nmims.helpers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nmims.dto.StudentIdCardDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.StudentBean;

@Component
public class IdCardHelper {
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	
	public HashMap<String, String> generateIdCardURL(StudentBean bean) {
		HashMap<String, String> mapOfResponse=new HashMap<String, String>();
		try {
			String url="";
			if("PROD".equalsIgnoreCase(ENVIRONMENT)  || "TEST".equalsIgnoreCase(ENVIRONMENT)) {
				url=SERVER_PATH+"studentportal/m/generateIdCard";
			}else {
				url="http://localhost:8080/studentportal/m/generateIdCard";
			}
		StudentIdCardDto dto=new StudentIdCardDto();
		dto.setImageUrl(bean.getImageUrl());
		dto.setSapid(bean.getSapid());
		dto.setFirstName(bean.getFirstName());
		dto.setLastName(bean.getLastName());
		dto.setProgram(bean.getProgram());
		dto.setEnrollmentMonth(bean.getEnrollmentMonth());
		dto.setEnrollmentYear(bean.getEnrollmentYear());
		dto.setCenterName(bean.getCenterName());
		dto.setAddress(bean.getAddress());
		dto.setMobile(bean.getMobile());
		dto.setDob(bean.getDob());
		dto.setBloodGroup(bean.getBloodGroup());
		dto.setImageUrl(bean.getImageUrl());
		dto.setLc(bean.getLc());
		dto.setValidityEndMonth(bean.getValidityEndMonth());
		dto.setValidityEndYear(bean.getValidityEndYear());
		dto.setRegDate(bean.getRegDate());
		dto.setConsumerProgramStructureId(bean.getConsumerProgramStructureId());
		dto.setBatchName(bean.getBatchName());
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> response = restTemplate.postForEntity(url, dto, String.class);
		JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
		 if ("200".equalsIgnoreCase(response.getStatusCode().toString()) && "success".equalsIgnoreCase(jsonResponse.get("status").getAsString())) {
			 mapOfResponse.put("status",jsonResponse.get("status").getAsString());
			 mapOfResponse.put("response", jsonResponse.get("response").getAsString());
		 }else {
			 mapOfResponse.put("status","error");
			 mapOfResponse.put("response","Error Getting code other than 200 "+response.getStatusCode().toString());
		}
		}catch (Exception e) {
			mapOfResponse.put("status","error");
			mapOfResponse.put("response","Error in IdCard generation API with error message : "+e.getMessage());
		}
		return mapOfResponse;
	}
}
