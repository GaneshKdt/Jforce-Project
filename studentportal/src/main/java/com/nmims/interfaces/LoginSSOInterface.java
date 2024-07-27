package com.nmims.interfaces;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.nmims.dto.LoginSSODto;
import com.nmims.entity.LoginSSO;

public interface LoginSSOInterface {
	
	LoginSSODto getStudentData(String userId);
	
	void deleteStudentDataFromRedis(String userId);
	
	void insertStudentDataIntoRedisFromSession(LoginSSO login_details);
}
