package com.nmims.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.CustomCourseWaiverDTO;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.interfaces.CustomCourseWaiverService;
import com.nmims.repository.CustomCourseWaiverRepository;

@Service
public class CustomCourseWaiverServiceImpl implements CustomCourseWaiverService{
	
	@Autowired
	CustomCourseWaiverRepository customCourseWaiverRepository;

	@Override
	public void getWaivedInSubject(StudentStudentPortalBean student, ArrayList<String> subjects,
			HashMap<String, String> studentSemMapping) {
		
		//student should not have previousSapid && student should be lateral 
		if(null!= student && StringUtils.isBlank(student.getPreviousStudentId())
				&& "Y".equalsIgnoreCase(student.getIsLateral())
				&& !StringUtils.isEmpty(student.getSapid())) {
			
			//getting Current Sem to get pssid, that the subject to be waived In current student registration sem 
			int sem = customCourseWaiverRepository.getStudentCurrentSem(student.getSapid());
			
			//Collecting PssID from custom_waived_in_subjects the subject that to be waived In 
			List<Integer> waivedInPssId = customCourseWaiverRepository.getWaivedInPss(student.getSapid(),sem);
			
			//collecting subjectCodeId,sem from mdm_subjectCode_mapping based on pssIds
			if (!waivedInPssId.isEmpty()) {
				List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository
						.getSubjectCodeId(waivedInPssId);

				// Creating map for further use that will help to not use nested loop
				Map<Integer, CustomCourseWaiverDTO> subjectCodeMap = subjectCodeMapping.stream()
						.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, Function.identity()));

				// Collecting subjectCodeId from map , which will be use to fetch subjectName
				// from mdm_subectCode
				Set<Integer> subjectCodeIds = subjectCodeMap.keySet();

				// Collecting id ,subjectName by subjectCodeId
				List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);

				// Now Id from mdm_subjectCode will be found in hashMap which we have created
				// If id found than we are setting subject Name in list as well in hashMap
				subjectName.stream().forEach(id -> {
					subjectCodeMap.computeIfPresent(id.getId(), (key, value) -> {
						subjects.add(id.getSubjectName());
						studentSemMapping.put(id.getSubjectName(), String.valueOf(value.getSem()));
						return value;
					});
				});

			}
		}

	}
	
	@Override
	public void getWaivedOffSubject(StudentStudentPortalBean student, ArrayList<String> waivedOffSubjects) {
		
		//student should not have previousSapid && student should be lateral
		if(null!= student && StringUtils.isBlank(student.getPreviousStudentId())
				&& "Y".equalsIgnoreCase(student.getIsLateral())
				&& !StringUtils.isEmpty(student.getSapid())) {
			
			
			//Collecting PssID from waivedoff_subject the subject that to be waived Off 
			List<Integer> waivedOffPssId = customCourseWaiverRepository.getWaivedOffPss(student.getSapid());

			if (!waivedOffPssId.isEmpty()) {
				// collecting subjectCodeId,sem from mdm_subjectCode_mapping based on pssIds
				List<CustomCourseWaiverDTO> subjectCodeMapping = customCourseWaiverRepository
						.getSubjectCodeId(waivedOffPssId);

				// Creating map for further use that will help to not use nested loop
				Map<Integer, CustomCourseWaiverDTO> subjectCodeMap = subjectCodeMapping.stream()
						.collect(Collectors.toMap(CustomCourseWaiverDTO::getSubjectCodeId, Function.identity()));

				// Collecting subjectCodeId from map , which will be use to fetch subjectName
				// from mdm_subectCode
				Set<Integer> subjectCodeIds = subjectCodeMap.keySet();

				// Collecting id ,subjectName by subjectCodeId
				List<CustomCourseWaiverDTO> subjectName = customCourseWaiverRepository.getSubjectName(subjectCodeIds);

				// Now Id from mdm_subjectCode will be found in hashMap which we have created
				// If id found than we are setting subject Name in list as well in hashMap
				subjectName.stream().forEach(id -> {
					subjectCodeMap.computeIfPresent(id.getId(), (key, value) -> {
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
	
	
	
	

}
