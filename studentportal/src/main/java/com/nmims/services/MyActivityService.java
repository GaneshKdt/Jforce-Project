package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.beans.PdfReadDetailsBean;
import com.nmims.beans.TimeSpentStudentBean;
import com.nmims.beans.TotalSessionDetailsStudentBean;
import com.nmims.beans.TotalVideoDetailsStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.beans.VideoAndSessionAttendanceCountStudentBean;
import com.nmims.controllers.MyActivityController;
import com.nmims.daos.MyActivityDao;
import com.nmims.dto.PdfReadDetailsDto;
import com.nmims.interfaces.MyAcitivityInterface;
import com.nmims.util.ContentUtil;

@Service
public class MyActivityService implements MyAcitivityInterface {
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String acadMonth;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String acadYear;
	
	@Autowired
	private MyActivityDao myActivityDao;
	
	private static final Logger logger = LoggerFactory.getLogger(MyActivityController.class);
	
	//--- Check Environment And Get URL ---//
	public String getUrl(String path) {
		
		String url = SERVER_PATH + path;
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)) {
			url = "http://localhost:8080/" + path;
		}
		
		return url;
	}

	//--- Get My Activity Details ---//
	@Override
	public Map<String, List<TimeSpentStudentBean>> myActivity(String sapid) {
		
		Map<String, List<TimeSpentStudentBean>> myActivity = new HashMap<String, List<TimeSpentStudentBean>>();
		List<TimeSpentStudentBean> myActivityDetails = new ArrayList<TimeSpentStudentBean>();
		
		//Get this week details from analytics database
		myActivityDetails = myActivityDao.getTimeSpentOfCurrentWeekBySapid(sapid);
		myActivity.put("This_Week", myActivityDetails);
		
		//Get this month details from analytics database
		myActivityDetails = myActivityDao.getTimeSpentOfCurrentMonthBySapid(sapid);
		myActivity.put("Current_Month", myActivityDetails);
		
		//Get previous month details from analytics database
		myActivityDetails = myActivityDao.getTimeSpentOfLastMonthBySapid(sapid);
		myActivity.put("Last_Month", myActivityDetails);
		
		return myActivity;
	}

	//--- Get Session Attendance Count ---//
	@Override
	public List<VideoAndSessionAttendanceCountStudentBean> getSessionAttendanceCount(String sapid) {
		
		List<VideoAndSessionAttendanceCountStudentBean> videoAndSessionAttendanceCountList = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
		List<VideoAndSessionAttendanceCountStudentBean> studentCurrentSubjectList = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
		VideoAndSessionAttendanceCountStudentBean videoAndSessionAttendanceCountStudentBean = new VideoAndSessionAttendanceCountStudentBean();
		List<Integer> sessionIdList = new ArrayList<Integer>();
		List<String> subjects = new ArrayList<String>();
		
		//Create acadDateFormat using acadMonth and acadYear
		String acadDateFormat = ContentUtil.pepareAcadDateFormat(acadMonth, acadYear);
		
		//Get student current subject from analytics database
		studentCurrentSubjectList = myActivityDao.getStudentCurrentSubject(sapid, acadMonth, acadYear);
		
		//Get session id list from analytics database
		sessionIdList = myActivityDao.getSessionIdList(sapid, acadDateFormat);
		
		//Convert session id list to string
		StringJoiner sessionIds = new StringJoiner(",");
        for (Integer sessionId : sessionIdList) {
        	sessionIds.add("'" + sessionId + "'");
        }
        
        //Get session attendance count from analytics database
        if(sessionIdList.size() > 0) {
    		videoAndSessionAttendanceCountList = myActivityDao.getSessionAttendaceCount(sessionIds.toString(), acadMonth, acadYear);
    		videoAndSessionAttendanceCountList.stream().forEach(list -> {
    			subjects.add(list.getSubject_name());
    		});
    		
    		//If any subject details not found
    		studentCurrentSubjectList.stream().forEach(a ->{
    			if(!subjects.contains(a.getSubject_name())) {
    				videoAndSessionAttendanceCountStudentBean.setSubject_name(a.getSubject_name());
    				videoAndSessionAttendanceCountStudentBean.setSubject_count(0);
    				videoAndSessionAttendanceCountStudentBean.setTrack("Not Attened");
    			}
    		});
    		
    		//Add data when subject name is present
    		if(videoAndSessionAttendanceCountStudentBean.getSubject_name() != null) {
    			videoAndSessionAttendanceCountList.add(videoAndSessionAttendanceCountStudentBean);
    		}
    		
        }
		
		//If student not attend any subject
		if(videoAndSessionAttendanceCountList.isEmpty()) {
			videoAndSessionAttendanceCountList.addAll(studentCurrentSubjectList);
			videoAndSessionAttendanceCountList.stream().forEach(list -> list.setTrack("Data Not Found"));
		}
		
		return videoAndSessionAttendanceCountList;
	}

	
	//--- Get Track Details ---//
	@Override
	public List<TracksBean> getTrackDetails() {
		
		List<TracksBean> trackDetailsList = new ArrayList<TracksBean>();
		
		trackDetailsList = myActivityDao.getTrackDetails();
		
		return trackDetailsList;
	}

	//--- Get Total Session Details ---//
	@SuppressWarnings("unchecked")
	@Override
	public TotalSessionDetailsStudentBean getTotalSessionDetails(ArrayList<Integer> pss_id, String sapid) {
		
		TotalSessionDetailsStudentBean totalSessionDetails = new TotalSessionDetailsStudentBean();
		int totalSessioncount = 0;
		List<Object> subjectNameAndIdList = new ArrayList<Object>();
		
		//Create acadDateFormat using acadMonth and acadYear
		String acadDateFormat = ContentUtil.pepareAcadDateFormat(acadMonth, acadYear);
		
		//Convert session id list to string
		StringJoiner pssIds = new StringJoiner(",");
		for (Integer pssId : pss_id) {
			pssIds.add("'" + pssId + "'");
		}
		
		//Get total session count from analytics database
		if(pss_id.size() > 0) {
			totalSessioncount = myActivityDao.getTotalSessionCount(pssIds.toString(), acadMonth, acadYear);
		}
		
		//Get attended session count from analytics database
		int attendedSessionCount = myActivityDao.getAttendedSessionCount(sapid, acadDateFormat);
		
		//Get total duration from analytics database
		List<Integer> totalDurationList = myActivityDao.getTotalDuration(sapid, acadDateFormat);
		
		//Get subject name and id from analytics database
		if(pss_id.size() > 0) {
			subjectNameAndIdList = myActivityDao.getSubjectNameAndId(pssIds.toString(), acadMonth, acadYear);
		}
		
		Map<String, Map<String, String>> subjectDetails = new HashMap<String, Map<String, String>>();
		
		//Get session id list from analytics database
		List<Integer> sessionIdList = myActivityDao.getSessionIdList(sapid, acadDateFormat);
				
		//Convert session id list to string
		StringJoiner sessionIds = new StringJoiner(",");
		for (Integer sessionId : sessionIdList) {
		    sessionIds.add("'" + sessionId + "'");
		}
		
		for (Object subjectObj : subjectNameAndIdList) {
			
			Map<String, String> subjectSessionDetails = new HashMap<String, String>();
			int subjectTotalSessionCount = 0;
			
		    Map<String, String> subjectMap = (Map<String, String>) subjectObj;
		    String subjectName = subjectMap.get("subject");
		    String subjectCodeId = subjectMap.get("subjectCodeId");
		    
		    //Get subject total session count from analytics database
		    if(pss_id.size() > 0) {
		    	subjectTotalSessionCount = myActivityDao.getSubjectTotalSessionCount(pssIds.toString(), acadMonth, acadYear, subjectCodeId);
		    }
		    
		    //Get subject attended session count from analytics database
		    int subjectAttendedSessionCount = 0;
		    if(sessionIds.length() != 0) {
		    	subjectAttendedSessionCount = myActivityDao.getSubjectAttendedSessionCount(sessionIds.toString(), acadMonth, acadYear, subjectName);
		    }
		    
		    //Get subject total duration from analytics database
		    List<Integer> subjectTotalDurationList = myActivityDao.getSubjectTotalDuration(sapid, acadDateFormat, subjectCodeId);
		    
		    subjectSessionDetails.put("subject_total_session", String.valueOf(subjectTotalSessionCount));
		    subjectSessionDetails.put("subject_attended_session", String.valueOf(subjectAttendedSessionCount));
		    subjectSessionDetails.put("subject_total_duration", subjectTotalDurationList.get(0)+"hr "+subjectTotalDurationList.get(1)+"min");
		    
		    subjectDetails.put(subjectName, subjectSessionDetails);
		}
		
		totalSessionDetails.setTotal_session(totalSessioncount);
		totalSessionDetails.setAttended_session(attendedSessionCount);
		totalSessionDetails.setTotal_duration(totalDurationList.get(0)+"hr "+totalDurationList.get(1)+"min");
		totalSessionDetails.setSubject_Details(subjectDetails);
		return totalSessionDetails;
	}

	//--- Get Total Video Details ---//
	@Override
	public TotalVideoDetailsStudentBean getTotalVideoDetails(String sapid) {
		
		TotalVideoDetailsStudentBean totalVideoDetailsStudentBean = new TotalVideoDetailsStudentBean();
		List<VideoAndSessionAttendanceCountStudentBean> studentCurrentSubjectList = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
		Map<String, Map<String, String>> subjectDetails = new HashMap<String, Map<String, String>>();
		List<TotalVideoDetailsStudentBean> totalVideoDetailsList = new ArrayList<TotalVideoDetailsStudentBean>();
		
		AtomicInteger total_attempt = new AtomicInteger(0);
		AtomicInteger total_duration = new AtomicInteger(0);
		
		//Get student current subjects from analytics database
		studentCurrentSubjectList = myActivityDao.getStudentCurrentSubject(sapid, acadMonth, acadYear);
		
		//Get total video count from analytics database
		totalVideoDetailsList = myActivityDao.getTotalVideoCount(sapid, acadMonth, acadYear);
		
		//If student attend any subject
		if(!totalVideoDetailsList.isEmpty()) {
			totalVideoDetailsList.stream().forEach(list -> {
				Map<String, String> subjectData = new HashMap<String, String>();
				
				int duration = Integer.parseInt(list.getTotal_duration());
				
				total_attempt.addAndGet(list.getTotal_attempt());
				total_duration.addAndGet(duration);
				
				//Create duration in hours, minutes for subjects
				int hours = duration / 3600;
		        int minutes = (duration % 3600) / 60;
		        list.setTotal_duration(hours + "hr " + minutes + "min");
		        
		        subjectData.put("subject_total_attempt", String.valueOf(list.getTotal_attempt()));
		        subjectData.put("subject_total_duration", list.getTotal_duration());
		        
		        subjectDetails.put(list.getSubject_name(), subjectData);
			});
			
			//If any subject details not found
			studentCurrentSubjectList.stream().forEach(a -> {
				if(!subjectDetails.containsKey(a.getSubject_name())) {
					Map<String, String> subjectData = new HashMap<String, String>();
					
					subjectData.put("subject_total_attempt", "0");
			        subjectData.put("subject_total_duration", "0hr 0min");
			        
			        subjectDetails.put(a.getSubject_name(), subjectData);
				}
			});
			
		}else{
			studentCurrentSubjectList.stream().forEach(list -> {
				Map<String, String> subjectData = new HashMap<String, String>();
				
			    subjectData.put("subject_total_attempt", "0");
			    subjectData.put("subject_total_duration", "0hr 0min");
			        
			    subjectDetails.put(list.getSubject_name(), subjectData);
			});
		}
		
		
		totalVideoDetailsStudentBean.setTotal_attempt(total_attempt.get());
		
		int hours = total_duration.get() / 3600;
        int minutes = (total_duration.get() % 3600) / 60;
		totalVideoDetailsStudentBean.setTotal_duration(hours + "hr " + minutes + "min");
		
		totalVideoDetailsStudentBean.setSubject_details(subjectDetails);
		
		return totalVideoDetailsStudentBean;
	}

	//--- Get PDF Read Details ---//
	@Override
	public List<Map<String, Object>> getPdfReadDetailsBySapid(ArrayList<Integer> pss_id, String sapid) {
		 
		List<Map<String, Object>> pdfReadDetailsList = new ArrayList<Map<String, Object>>();
		
		PdfReadDetailsBean pdfReadDetails = new PdfReadDetailsBean();
		RestTemplate restTemplate = new RestTemplate();
		String url = getUrl("analytics/m/getPdfReadDetailsBySapid");
		
		pdfReadDetails.setSapid(sapid);
		
		//Get PDF counter details from analytics
		HttpEntity<PdfReadDetailsBean> requestEntity = new HttpEntity<>(pdfReadDetails);
	    ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<HashMap<String, Object>>() {};
	    ResponseEntity<HashMap<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
	    
		if(("200".equalsIgnoreCase(response.getStatusCode().toString()))) {
			
			HashMap<String, Object> pdfData = response.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			
			List<PdfReadDetailsBean> pdfCounterDetails = objectMapper.convertValue(pdfData.get("pdfReadDetailsList"), new TypeReference<List<PdfReadDetailsBean>>() {});
			
			//Convert session id list to string
	  		StringJoiner pssIds = new StringJoiner(",");
	  		for (Integer pssId : pss_id) {
	  			pssIds.add("'" + pssId + "'");
	  		}
	  		
			//Get student current subjects from analytics database
			List<PdfReadDetailsDto> studentCurrentSubjectList = myActivityDao.getStudentCurrentSubjectByPssId(pssIds.toString(), acadMonth, acadYear);
			
			//Get PDF read details list
			pdfReadDetailsList = getPdfReadDetailsList(studentCurrentSubjectList, pdfCounterDetails);
		}else {
			logger.error("Getting response code other than 200 ( PDF read details ) for sapid: "+sapid+" and Response: "+response.toString());
		}
		
		return pdfReadDetailsList;
	}
	
	//--- Get PDF Read Details List ---//
	public List<Map<String, Object>> getPdfReadDetailsList(List<PdfReadDetailsDto> studentCurrentSubjectList, List<PdfReadDetailsBean> pdfCounterDetails){
		
		List<Map<String, Object>> pdfReadDetailsList = new ArrayList<Map<String, Object>>();
		Map<String, Object> pdfReadDetails = new HashMap<String, Object>(); 
		List<Map<String, Object>> subjectDetailsList = new ArrayList<Map<String, Object>>();
		
		//Total PDF and read PDF count
		List<Integer> totalPdfPercentage = new ArrayList<Integer>();
		int totalCount = studentCurrentSubjectList.stream().mapToInt(subject -> subject.getTotal_pdf()).sum();
		int count = pdfCounterDetails.size();
		
		//Subject wise time spent
		AtomicInteger subjectWiseTotalTimeSpent = new AtomicInteger(0);
		
		//Subject wise PDF and read PDF count
		List<String> subjectLists = pdfCounterDetails.stream().map(x->x.getSubject_name()).collect(Collectors.toList());
		
		studentCurrentSubjectList.stream().forEach(subjectList -> {
			
			Map<String, Object> subjectDetails = new HashMap<String, Object>(); 
			
			//Subject wise total PDF and read PDF count
			List<Integer> subjectWiseTotalPercentage = new ArrayList<Integer>();
			int subjectWiseCount = (int) subjectLists.stream().filter(c -> c.contains(subjectList.getSubject_name())).count();
			 
			//PDF wise time spent
			AtomicInteger pdfWiseTotalTimeSpent = new AtomicInteger(0);
			List<Integer> pdfTimeSpent = new ArrayList<Integer>();
			
			//PDF wise PDF and read PDF count
			List<Map<String, String>> pdfList = new ArrayList<Map<String, String>>();
			
			pdfCounterDetails.stream().forEach(pdf -> {
				
				if(pdf.getSubject_name().equals(subjectList.getSubject_name())) {
					
					Map<String, String> pdfData = new HashMap<String, String>();
					
					//PDF wise time spent into hours and minutes format
					String pdfWiseTimeSpent = convertTimeSpentIntoHoursAndMinutes(Integer.parseInt(pdf.getTime_spent()));
					
					//PDF wise total PDF and read PDF count
					int pdfTotalCount = pdf.getTotal_page();
					int pdfCount = pdf.getRead_page();
					int pdfReadPercentage = 0;
					
					//Calculate PDF wise total percentage
					if(pdfTotalCount != 0) {
						pdfReadPercentage = (int) (((double)pdfCount/pdfTotalCount)*100);
					}
					
					//PDF wise details
					pdfData.put("pdf_name", pdf.getContent_file_name());
					pdfData.put("total_page", String.valueOf(pdf.getTotal_page()));
					pdfData.put("read_page", String.valueOf(pdf.getRead_page()));
					pdfData.put("time_spent", pdfWiseTimeSpent);
					pdfData.put("read_percentage", String.valueOf(pdfReadPercentage));
					
					pdfList.add(pdfData);
					subjectWiseTotalPercentage.add(pdfReadPercentage);
					pdfTimeSpent.add(Integer.parseInt(pdf.getTime_spent()));
					pdfWiseTotalTimeSpent.set(pdfTimeSpent.stream().mapToInt(Integer::intValue).sum());
				}
			});
			
			//Subject wise total PDF and read PDF count
			int subjectWisePercentage = subjectWiseTotalPercentage.stream().mapToInt(Integer::intValue).sum();
			int subjectWisePdfReadPercentage = subjectWisePercentage/subjectList.getTotal_pdf();
			
			//Subject wise time spent into hours and minutes format
			String subjectWiseTimeSpent = convertTimeSpentIntoHoursAndMinutes(pdfWiseTotalTimeSpent.intValue());
			
			//Subject wise details
			subjectDetails.put("subject_name", subjectList.getSubject_name());
			subjectDetails.put("total_pdf", subjectList.getTotal_pdf());
			subjectDetails.put("read_pdf", subjectWiseCount);
			subjectDetails.put("read_percentage", subjectWisePdfReadPercentage);
			subjectDetails.put("time_spent", subjectWiseTimeSpent);
			subjectDetails.put("pdf_details", pdfList);
			 
			subjectDetailsList.add(subjectDetails);
			totalPdfPercentage.add(subjectWisePercentage);
			subjectWiseTotalTimeSpent.set(subjectWiseTotalTimeSpent.intValue() + pdfWiseTotalTimeSpent.intValue());
		});
		
		//Total PDF and read PDF count
		int totalPercentage = totalPdfPercentage.stream().mapToInt(Integer::intValue).sum();
		int totalPdfReadPercentage = totalPercentage/totalCount;
		
		//Total time spent into hours and minutes format
		String totalTimeSpent = convertTimeSpentIntoHoursAndMinutes(subjectWiseTotalTimeSpent.intValue());
		
		//Total details
		pdfReadDetails.put("total_pdf", totalCount);
		pdfReadDetails.put("read_pdf", count);
		pdfReadDetails.put("read_percentage", totalPdfReadPercentage);
		pdfReadDetails.put("time_spent", totalTimeSpent);
		pdfReadDetails.put("subject_details", subjectDetailsList);
		
		pdfReadDetailsList.add(pdfReadDetails);
	    
	    return pdfReadDetailsList;
	}
	
	//--- Convert Time Spent Into Hours And Minutes ---//
	public String convertTimeSpentIntoHoursAndMinutes(int timeSpent) {
		
		int timeSpentInMilliSeconds = timeSpent;
		int hours = (timeSpentInMilliSeconds / 1000) / 3600;
		int minutes = ((timeSpentInMilliSeconds / 1000) % 3600) / 60;
		String timeSpentInHoursAndMinutes = hours+"hr "+minutes+"min";
		
		return timeSpentInHoursAndMinutes;
	}

}
