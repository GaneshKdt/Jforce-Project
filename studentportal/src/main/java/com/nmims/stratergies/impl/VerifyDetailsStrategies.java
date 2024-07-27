package com.nmims.stratergies.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.beans.VerifyContactDetailsBean;
import com.nmims.controllers.VerifyContactDetailsController;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.stratergies.VerifyDetailsStrategyInterface;

@Component
public class VerifyDetailsStrategies implements VerifyDetailsStrategyInterface{
	Logger logger  = LoggerFactory.getLogger(VerifyDetailsStrategies.class);

	@Autowired
	private ServiceRequestDao servDao;
	@Autowired
	private StudentDAO studDao;
	
	public VerifyContactDetailsBean validateOtpAndUpdateContactDetails(VerifyContactDetailsBean verifyBean) {
		VerifyContactDetailsBean bean = new VerifyContactDetailsBean();
		try {
			logger.info("Validating Sapid ...");
			bean = servDao.checkOtp(verifyBean);
			if(bean.getOtp().equalsIgnoreCase(verifyBean.getOtp())) {
				bean.setStatus(true);
				logger.info("Successfully Validated the Otp For "+ verifyBean.getOtp());
			}
			else {
				bean.setStatus(false);
				bean.setMessage("Otp Is Invalid");
			}
		}catch (Exception e) {
		e.printStackTrace();	
		bean.setStatus(false);
		bean.setMessage(e.getMessage());
		}
		return bean;
		}
		
		
	
	
}
