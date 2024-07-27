package com.nmims.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.netflix.discovery.converters.Auto;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VerifyContactDetailsBean;
import com.nmims.controllers.VerifyContactDetailsController;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.OtpHelper;
import com.nmims.interfaces.VerifyContactDetailsInterface;
import com.nmims.stratergies.VerifyDetailsStrategyInterface;
@Service
public class VerifyOtpService implements VerifyContactDetailsInterface{
	
	Logger logger  = LoggerFactory.getLogger(VerifyContactDetailsController.class);
	
	@Autowired
	private VerifyDetailsStrategyInterface verifyDetailsInterface;
	
	@Autowired
	@Qualifier("portalDAO")
	private PortalDao portalDao;
	
	@Autowired
	private StudentDAO studDao;
	
	@Autowired
	private OtpHelper contactHelper;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;  
	
	@Autowired
	private SMSSender smsSender;


	@Autowired
	private MailSender mailSender;
	
	
	
	/** send otp to the Email/Mobile entered 
	 * @param- {@link HttpServletRequest} {@link HttpServletResponse}, {@link VerifyContactDetailsBean}
	 * @return- {@link ResponseEntity}
	 * 
	 */
	public VerifyContactDetailsBean sendOtpAndInsertIntoTable(VerifyContactDetailsBean verifyBean)  {
		String otp = "";
		String sapid= verifyBean.getSapid();
		try {
			if(StringUtils.isEmpty(verifyBean.getSubType()))
				throw new Exception("Please Enter Valid Details For Contact Details");
			otp = contactHelper.generatingOtp();
			if(otp.isEmpty())
				throw new Exception("Error In Generating Otp");
			logger.info(sapid  +" Generated Otp {} ", otp);

			int srTypeId = serviceRequestDao.getContactDetailSrId();
			verifyBean.setOtp(otp);
			verifyBean.setSrid(Integer.toString(srTypeId));
			if (serviceRequestDao.insertIntoOtpVerification(verifyBean)) {
				if ("emailid".equalsIgnoreCase(verifyBean.getSubType())) {
					StudentStudentPortalBean firstName=portalDao.getSingleStudentsData(verifyBean.getSapid());
					verifyBean.setFirstName(firstName.getFirstName());
					mailSender.sendOtpForContactDetails(verifyBean);
					verifyBean.setMessage("SuccessFully Send Email and Otp");
					logger.info(sapid  +" send MAIL For Email Id    "+ verifyBean );
				} else { 
					smsSender.sendRequestOTP(verifyBean);
					verifyBean.setMessage("SuccessFully Send SMS and Otp");

					logger.info(sapid  +" send SMS For Mobile  "+ verifyBean );
					
				}
				verifyBean.setStatus(true);
				
		} }catch (Exception e) {
			e.printStackTrace();
			verifyBean.setStatus(false);
			verifyBean.setMessage(e.getMessage());
	
		}
		logger.info(sapid  +" returning response as Faild ");
		return verifyBean;
		}



	@Override
	public VerifyContactDetailsBean verifyDetails(VerifyContactDetailsBean verifyBean)  {
		VerifyContactDetailsBean verification = new VerifyContactDetailsBean();
		try {
			verification = verifyDetailsInterface.validateOtpAndUpdateContactDetails(verifyBean);
			if (verification.isStatus()) {
				logger.info(verifyBean.getSapid() +"   OtpVerified successfully");
				return verification;
			}else {
				throw new Exception("Otp Verification Failed");
			}
			}
		catch (Exception e) {
			e.printStackTrace();
			verification.setStatus(false);
			verification.setMessage(e.getMessage());
		
		}
		return verification;	
	}
		}
	
