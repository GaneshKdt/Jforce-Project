package com.nmims.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.AlmashinesBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.helpers.AlmashinesHelper;

@Service("almashinesService")
public class AlmashinesService implements AlmashinesServiceInterface {

	@Autowired
	AlmashinesHelper almashinesHelper;
	@Override
	public AlmashinesBean almashinesLogin(StudentStudentPortalBean sb) throws Exception{
		// TODO Auto-generated method stub
		AlmashinesBean response = new AlmashinesBean();
		
        String plainText;
        long unixTime = Instant.now().getEpochSecond();
        plainText = "{\"uid\":"+sb.getAlmashinesId()+",\"timestamp\":"+unixTime+"}";
        try {
        	String encryptedText =  almashinesHelper.almashinesEncryptCipher(plainText);
        	String  urlLocation = almashinesHelper.loginUser(encryptedText);
        	response.setUrlLocation(urlLocation);
        	return response;
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception(e.getMessage());
			
		}

	}

}
