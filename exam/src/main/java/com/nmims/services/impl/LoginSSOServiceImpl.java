package com.nmims.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.dto.LoginSSODto;
import com.nmims.entity.LoginSSO;

import com.nmims.interfaces.LoginSSOInterface;
import com.nmims.repository.SSOLoaderRepositoryForRedis;


@Service
public class LoginSSOServiceImpl implements LoginSSOInterface{

	@Autowired
	SSOLoaderRepositoryForRedis repository;
	
	@Override
	public LoginSSODto getStudentData(String userId) {
		try{
			return setDataInDTO(repository.findOne(userId));
			
		}catch(Exception e) {}
		return  null;
	}
	
	
	public LoginSSODto setDataInDTO(LoginSSO student) {
		LoginSSODto student_dto = new LoginSSODto();
		student_dto.setUserId(student.getUserId());
		student_dto.setStudent(student.getStudent());
		student_dto.setRegData(student.getRegData());
		student_dto.setCurrentOrder(student.getCurrentOrder());
		student_dto.setCurrentSemPSSId(student.getCurrentSemPSSId());
		student_dto.setEarlyAccess(student.getEarlyAccess());
		
		student_dto.setUserBean(student.getUserBean());
		student_dto.setPersonDetails(student.getPersonDetails());
		student_dto.setCsAdmin(student.getCsAdmin());
		student_dto.setAnnouncements(student.getAnnouncements());
		student_dto.setRegOrder(student.getRegOrder());
		student_dto.setFeatureViseAccess(student.getFeatureViseAccess());
		student_dto.setLiveSessionPssIdAccess(student.getLiveSessionPssIdAccess());
		student_dto.setConsumerProgramStructureHasCSAccess(student.isConsumerProgramStructureHasCSAccess());
		student_dto.setCourseraAccess(student.isCourseraAccess());
		student_dto.setIsLoginAsLead(student.getIsLoginAsLead());
		student_dto.setValidityExpired(student.getValidityExpired());
		student_dto.setSubjectCodeId(student.getSubjectCodeId());
		return student_dto;
	}
}
