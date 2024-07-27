package com.nmims.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.StudentExamBean;
import com.nmims.dto.CustomCourseWaiverDTO;
import com.nmims.interfaces.CustomCourseWaiverService;
import com.nmims.repository.CustomCourseWaiverRepository;

@Service
public class CustomCourseWaiverServiceImpl implements CustomCourseWaiverService{

	@Autowired
	CustomCourseWaiverRepository customCourseWaiverRepository;
	
	private static final Logger customCourseWaiverLogs = (Logger) LoggerFactory.getLogger("customCourseWaiver");
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;


	@Override
	public void getWaivedInSubject(StudentExamBean student, ArrayList<String> subjects,
			HashMap<String, String> studentSemMapping) {
		customCourseWaiverLogs.info("Custom Course Waiver");
		//student should not have previousSapid && student should be lateral 
		if(null!= student && StringUtils.isBlank(student.getPreviousStudentId())
				&& "Y".equalsIgnoreCase(student.getIsLateral())
				&& !StringUtils.isEmpty(student.getSapid())) {
			
			//getting Current Sem to get pssid, that the subject to be waived In current student registration sem 
			int sem = customCourseWaiverRepository.getStudentCurrentSem(student.getSapid());
			customCourseWaiverLogs.info("Custom Course Waiver sem "+sem);
			//Collecting PssID from custom_waived_in_subjects the subject that to be waived In 
			List<Integer> waivedInPssId = customCourseWaiverRepository.getWaivedInPss(student.getSapid(),sem);
			customCourseWaiverLogs.info("Custom Course Waiver waivedInPssId "+waivedInPssId);
			if(!waivedInPssId.isEmpty()) {
			//collecting subjectCodeId,sem from mdm_subjectCode_mapping based on pssIds
			List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository.getSubjectCodeId(waivedInPssId);
			customCourseWaiverLogs.info("Custom Course Waiver subjectCodeMapping "+subjectCodeMapping);

			//Creating map for further use that will help to not use nested loop
			Map<Integer,CustomCourseWaiverDTO> subjectCodeMap = subjectCodeMapping.stream()
					.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, Function.identity()));
			
			//Collecting subjectCodeId from map , which will be use to fetch subjectName from mdm_subectCode
			Set<Integer> subjectCodeIds = subjectCodeMap.keySet();
			
			//Collecting id ,subjectName by subjectCodeId
			List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);
			customCourseWaiverLogs.info("Custom Course Waiver subjectName "+subjectName);

			//Now Id from mdm_subjectCode will be found in hashMap which we have created
			//If id found than we are setting subject Name in list as well in hashMap
			subjectName.stream().forEach(id ->{
				subjectCodeMap.computeIfPresent(id.getSubjectCodeId(), (key,value)->{
					subjects.add(id.getSubjectName());
					studentSemMapping.put(id.getSubjectName(), String.valueOf(value.getSem()));
					return value;
				});
			});
			}
			customCourseWaiverLogs.info("Custom Course Waiver subjectName "+subjects);
		}
		
	}
	
	@Override
	public void getWaivedOffSubject(StudentExamBean student, ArrayList<String> waivedOffSubjects) {
		
		//student should not have previousSapid && student should be lateral
		if(null!= student && StringUtils.isBlank(student.getPreviousStudentId())
				&& "Y".equalsIgnoreCase(student.getIsLateral())
				&& !StringUtils.isEmpty(student.getSapid())) {
			
			
			//Collecting PssID from waivedoff_subject the subject that to be waived Off 
			List<Integer> waivedOffPssId = customCourseWaiverRepository.getWaivedOffPss(student.getSapid());
			
			if(!waivedOffPssId.isEmpty()) {
			//collecting subjectCodeId,sem from mdm_subjectCode_mapping based on pssIds
			List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository.getSubjectCodeId(waivedOffPssId);
			
			//Creating map for further use that will help to not use nested loop
			Map<Integer,CustomCourseWaiverDTO> subjectCodeMap = subjectCodeMapping.stream()
					.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, Function.identity()));
			
			//Collecting subjectCodeId from map , which will be use to fetch subjectName from mdm_subectCode
			Set<Integer> subjectCodeIds = subjectCodeMap.keySet();
			
			//Collecting id ,subjectName by subjectCodeId
			List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);

			//Now Id from mdm_subjectCode will be found in hashMap which we have created
			//If id found than we are setting subject Name in list as well in hashMap
			subjectName.stream().forEach(id ->{
				subjectCodeMap.computeIfPresent(id.getId(), (key,value)->{
					waivedOffSubjects.add(id.getSubjectName());
					
					return value;
				});
			});
			}
		}
	}

	@Override
	public boolean checkSapidExist(String sapid) {
		int exist = customCourseWaiverRepository.checkSapidExist(sapid);
		if(exist == 1) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, List<?>> getApplicableSubject(Long sapid) {
		 Map<String, List<?>> map =  new HashMap<>();
		
		 int currentRegistrationSem = customCourseWaiverRepository.getStudentCurrentSem(String.valueOf(String.valueOf(sapid)));
		int masterKey = customCourseWaiverRepository.getStudentMasterKey(String.valueOf(sapid));
		
		List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository.getSubjectCodeIdByMasterKey(masterKey);

		//Creating map for further use that will help to not use nested loop
		Map<Integer,CustomCourseWaiverDTO> subjectCodeMap = subjectCodeMapping.stream()
				.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, Function.identity()));
		
		//Collecting subjectCodeId from map , which will be use to fetch subjectName from mdm_subectCode
		Set<Integer> subjectCodeIds = subjectCodeMap.keySet();
		
		//Collecting id ,subjectName by subjectCodeId
		List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);
		
		//based on subjectCodeId from subject code and subjectCodeMapping name is been set
		subjectName.stream().forEach(id ->{
			subjectCodeMap.computeIfPresent(id.getSubjectCodeId(), (key,value)->{
				id.setPssId(subjectCodeMap.get(id.getSubjectCodeId()).getPssId());
				id.setCurrentSem(subjectCodeMap.get(id.getSubjectCodeId()).getSem());
				return value;
			});
		});

		//getting waived Off pss to show on UI
		List<Integer> waivedOffPss = customCourseWaiverRepository.getWaivedOffPss(String.valueOf(sapid));
		List<CustomCourseWaiverDTO> waivedInPss =  customCourseWaiverRepository.getWaivedInPss(sapid);
		Map<Integer,CustomCourseWaiverDTO> waivedInMap = waivedInPss.stream().collect(Collectors.toMap(CustomCourseWaiverDTO::getPssId, Function.identity()));
		
		//setting 1 for waived IN
		//setting 2 for waived Off
		//because when user research for that sapid, it should show the saved waived In and waived Off subject
		subjectName.stream().forEach(id ->{
			if(waivedInMap.containsKey(id.getPssId())) {
				CustomCourseWaiverDTO bean = waivedInMap.get(id.getPssId());
				id.setId(1);
				id.setSem(bean.getSem());
			}
			if(waivedOffPss.contains(id.getPssId())) {
				id.setId(2);
			}
		});
		
		//Getting sem from 1 to last sem of program for waived IN for UI side
		List<Integer> totalSemsList = customCourseWaiverRepository.getTotalNumberOfSem(masterKey);
		
		//sorting to for UI to view subject sem wise
		subjectName.sort(Comparator.comparingInt(CustomCourseWaiverDTO::getCurrentSem));
		
		List<Integer> currentSem = new ArrayList<Integer>();
		currentSem.add(currentRegistrationSem);
		map.put("currentRegistrationSem", currentSem);
		map.put("applicableSubject", subjectName);
		map.put("totalSems", totalSemsList);
		return map;
		 
	}

	@Override
	public int saveWaivedInSubject(CustomCourseWaiverDTO customCourseWaiverDTO,String loggedUser) throws Exception {
		//removing entry if present in waivedOff table
		customCourseWaiverRepository.deleteFromWaivedOff(customCourseWaiverDTO.getSapid(),customCourseWaiverDTO.getPssId());
		
		int sem = customCourseWaiverRepository.getStudentCurrentSem(String.valueOf(customCourseWaiverDTO.getSapid()));
		
		//getting current sem beacuse waived In sem should be equal or greater than current sem
		if(customCourseWaiverDTO.getSem()>=sem) {
			return customCourseWaiverRepository.saveWaivedInSubject(customCourseWaiverDTO.getPssId(),customCourseWaiverDTO.getSem(),customCourseWaiverDTO.getSapid(),loggedUser);
		}
		else {
			throw new Exception("Sem");
		}
		 
	}

	@Override
	public int saveWaivedOffSubject(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser) {
		//deleting from waived IN table
		customCourseWaiverRepository.deleteFromWaivedIN(customCourseWaiverDTO.getSapid(),customCourseWaiverDTO.getPssId());
		
		//deleting from student_current_subject table
		customCourseWaiverRepository.deleteStudentCurrentSubject(customCourseWaiverDTO.getSapid(),customCourseWaiverDTO.getPssId());
		
		//deleting from student_course maping table
		customCourseWaiverRepository.deleteFromStudentCourseMapping(customCourseWaiverDTO.getSapid(),customCourseWaiverDTO.getPssId());
		
		return customCourseWaiverRepository.saveWaivedOffSubject(customCourseWaiverDTO.getPssId(),customCourseWaiverDTO.getSapid(),loggerUser); 
	}

	@Override
	public Map<String,Integer> upsertInCourseWaiver(List<CustomCourseWaiverDTO> courseWaiverList,String loggedUser) {
		List<CustomCourseWaiverDTO> waivedIn =  new ArrayList<CustomCourseWaiverDTO>();
		List<CustomCourseWaiverDTO> waivedOff =  new ArrayList<CustomCourseWaiverDTO>();
		Map<String,Integer> insertedCount =  new HashMap<String, Integer>();
		
		//collecting sapid which wil be used to add data in student_current_subject table through rest call
		Set<Long> sapidList = courseWaiverList.stream().map(CustomCourseWaiverDTO::getSapid).collect(Collectors.toSet());
		
		//iterating list because an excel can have multiple sapid
		courseWaiverList.stream().forEach(id ->{
			int masterKey = customCourseWaiverRepository.getStudentMasterKey(String.valueOf(id.getSapid()));
			List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository.getSubjectCodeIdByMasterKey(masterKey);
			CustomCourseWaiverDTO studentMonth = customCourseWaiverRepository.getAcadMonthAndYear(id.getSapid());
			//Creating map for further use that will help to not use nested loop
			Map<Integer,Integer> subjectCodeMap = subjectCodeMapping.stream()
					.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, CustomCourseWaiverDTO::getPssId));
			
			//Collecting subjectCodeId from map , which will be use to fetch subjectName from mdm_subectCode
			Set<Integer> subjectCodeIds = subjectCodeMap.keySet();
			
			//Collecting id ,subjectName by subjectCodeId
			List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);
			Map<String,CustomCourseWaiverDTO> subjectNameMap = subjectName.stream()
					.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectName, Function.identity()));
			
			//based on subjectCodeId setting subject name
			subjectNameMap.computeIfPresent(id.getSubjectName(), (key,value)->{
				subjectCodeMap.computeIfPresent(value.getSubjectCodeId(), (subject,pssId)->{
					id.setPssId(pssId);
					id.setMonth(studentMonth.getMonth());
					id.setYear(studentMonth.getYear());
					return pssId;
				});
				return value;
			});
		});
		
		//seperating waived In and wwaived off list
		waivedIn = courseWaiverList.stream().filter(waiver-> waiver.getCourseWaiver().equalsIgnoreCase("waivedIn")).collect(Collectors.toList());
		waivedOff = courseWaiverList.stream().filter(waiver-> waiver.getCourseWaiver().equalsIgnoreCase("waivedOff")).collect(Collectors.toList());
		
		//upserting in waived iN AND WAIVED OFF TABLE	
		int waivedInCount = customCourseWaiverRepository.upsertWaivedIn(waivedIn,loggedUser);
		int waivedOffCount = customCourseWaiverRepository.upsertWaivedOff(waivedOff,loggedUser);
		
		//insert here to get data inserted in student current subject table after waived In and waived off
		sapidList.stream().forEach(sapId->{
			upsertInStudentCurrentSubject(sapId);	
		});
		
		//inserting wwaivedIn pss in student course_mapping table
		customCourseWaiverRepository.upsertInDelhivery(waivedIn,loggedUser);
		
		//removing waivedOff pss from student_course _mapping
		customCourseWaiverRepository.deleteFromDelhivery(waivedOff);
		insertedCount.put("waivedInCount", waivedInCount);
		insertedCount.put("waivedOffCount", waivedOffCount);
		
		return insertedCount;
		
	}

	@Override
	public void upsertInStudentCurrentSubject(long sapid) {
			HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/json"); 
	    	headers.add("Accept", "application/json");
			
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			RestTemplate restTemplate = new RestTemplate();
			 
			String url = SERVER_PATH+"studentportal/m/populateCurrentCourseOfStudent";
			LinkedMultiValueMap<String, Object> courseinput = new LinkedMultiValueMap<>();
			courseinput.add("sapid", sapid);
			courseinput.add("sfdcCall", false);
			
			HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = new HttpEntity<>(courseinput, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);

			if (!("200".equalsIgnoreCase(response.getStatusCode().toString()))) {
				customCourseWaiverLogs.info("Error:- Getting response code other than 200 ( student course  portal ) for sapid "+sapid+" And Response :- "+response.toString());
				//mailer.studentCourseMailTrace("Student Course Error: Getting error other than 200 for sapid "+sapid,response.toString());
			}
		
	}

	@Override
	public void upsertInStudentCourseMapping(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser) {
		//getting current acad month & year for upsert in student_course_mapping table
		CustomCourseWaiverDTO studentMonth = customCourseWaiverRepository.getAcadMonthAndYear(customCourseWaiverDTO.getSapid());
		customCourseWaiverDTO.setMonth(studentMonth.getMonth());
		customCourseWaiverDTO.setYear(studentMonth.getYear());
		//inserting in student_course_mapping table
		 customCourseWaiverRepository.upsertInStudentCourseMapping(customCourseWaiverDTO,loggerUser);
		
	}
	
}
