package com.nmims.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.dto.LoginSSODto;
import com.nmims.entity.LoginSSO;
import com.nmims.interfaces.LoginSSOInterface;
import com.nmims.repository.SSOLoaderRepositoryForRedis;

@Service
public class LoginSSOServiceImpl implements LoginSSOInterface{

	@Autowired
	SSOLoaderRepositoryForRedis repository;
	
	private static final Logger redis_logger = LoggerFactory.getLogger("redis_loginsso");
	
	@Override
	public LoginSSODto getStudentData(String userId) {
		
		LoginSSODto  student_dto = new LoginSSODto();
		
		LoginSSO student = repository.findOne(userId);
	
		if(student != null) 
			student_dto = setDataInDTO(student);
		
		return student_dto;
	}

	@Override
	public void deleteStudentDataFromRedis(String userId) {
		// TODO Auto-generated method stub
		
		LoginSSO student = repository.findOne(userId); 
		if(student != null)
			repository.delete(student);
	}

	@Override
	public void insertStudentDataIntoRedisFromSession(LoginSSO login_details) {
		// TODO Auto-generated method stub	
			repository.save(login_details);
	}
	
	public LoginSSODto setDataInDTO(LoginSSO student) {
		LoginSSODto student_dto = new LoginSSODto();
		student_dto.setUserId(student.getUserId());
		student_dto.setStudent(student.getStudent());
		student_dto.setAcadSessionLiveOrder(student.getAcadSessionLiveOrder());
		student_dto.setRegData(student.getRegData());
		student_dto.setCurrentOrder(student.getCurrentOrder());
		student_dto.setCurrentSemPSSId(student.getCurrentSemPSSId());
		student_dto.setEarlyAccess(student.getEarlyAccess());
		student_dto.setHarvard(student.getHarvard());
		student_dto.setStukent(student.getStukent());
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
		student_dto.setApplicableSubjects(student.getApplicableSubjects());
		student_dto.setMaxOrderWhereContentLive(student.getMaxOrderWhereContentLive());
		
		return student_dto;
	}

}
