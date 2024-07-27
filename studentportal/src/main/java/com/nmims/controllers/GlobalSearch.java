package com.nmims.controllers;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.util.StringUtil;
import com.nmims.beans.ResponceBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.searchResultBean;
import com.nmims.daos.StudentDAO;

/**
 * we can create on seprate method to filter data whicj will take key word and data it will form the processing and provide filtered data
 * */
@Controller
public class GlobalSearch extends BaseController{

	/**
	 * created for storing gloval search all data so we can perform logical operations
	 * 
	 * */
	@Value("${SEARCH_APP_URL}")
	private String SEARCH_APP_URL;
	

	@GetMapping("/student/searchResultPage")
	public String searchResultPage(@RequestParam("search")String searchValue,@RequestParam(name = "type",required = false)String type ,Model model,HttpServletRequest request) {
	
		List<ResponceBean> resourceList = new ArrayList<ResponceBean>();
		List<ResponceBean> videoList = new ArrayList<ResponceBean>();
		List<ResponceBean> qnaList = new ArrayList<ResponceBean>();
		List<ResponceBean> allList= new ArrayList<ResponceBean>();
		List<Integer>subjectCode= new ArrayList<Integer>();
		List<ResponceBean> responseList= new ArrayList<ResponceBean>();
		String originalSearchParam="";
	try {
		originalSearchParam = URLDecoder.decode(searchValue, "UTF-8");
		subjectCode=(List<Integer>)request.getSession().getAttribute("subjectCodeId_studentportal");
		responseList = getAllSearchResultPageData(searchValue,subjectCode);
		resourceList = responseList.stream().filter(e->e.getContentType().equalsIgnoreCase("pdf")).collect(Collectors.toList());
		videoList = responseList.stream().filter(e->e.getContentType().equalsIgnoreCase("video")).collect(Collectors.toList());
		qnaList = responseList.stream().filter(e->e.getContentType().equalsIgnoreCase("qna")).collect(Collectors.toList());
		allList.addAll(resourceList);
		allList.addAll(videoList);
		allList.addAll(qnaList);
	} catch (Exception e) {
		// TODO: handle exception
		//e.printStackTrace();
	}
		model.addAttribute("searchValue", searchValue.isEmpty()?" ":originalSearchParam);
		model.addAttribute("type", StringUtils.isEmpty(type)?" ":type);
		model.addAttribute("allList", allList);
		model.addAttribute("resourceList", resourceList);
		model.addAttribute("videoList", videoList);
		model.addAttribute("qnaList", qnaList);
		
		return "jsp/searchResultPage1";
	}
	

	/**
	 * to get all search data and use it at various 
	 * */
	public List<ResponceBean> getAllSearchResultPageData(String keyword,List<Integer> subjectCodeIds){
		
		RestTemplate restTemplet= new RestTemplate();
		String url= SEARCH_APP_URL+"fetchSearchResult";
		String responseBody="";
		List<ResponceBean>responseList= new ArrayList<ResponceBean>();
	    HttpHeaders headers = new HttpHeaders();
	     headers.setContentType(MediaType.APPLICATION_JSON);

	     Map<String, Object> requestBody = new HashMap<>();
	     requestBody.put("q", keyword);
	     requestBody.put("subjectCodeIds", subjectCodeIds);
	     // Add more key-value pairs as needed

	     HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

	     // Send the POST request
	     ResponseEntity<String> responseEntity = restTemplet.exchange(url, HttpMethod.POST, requestEntity,String.class);
	     // Get the response body
	     responseBody = responseEntity.getBody();
	     TypeFactory typeFactory = TypeFactory.defaultInstance();
	     CollectionType responseType = typeFactory.constructCollectionType(List.class, ResponceBean.class);
	     try {
			responseList = new ObjectMapper().readValue(responseBody, responseType);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return responseList;
	}
	
	
}
