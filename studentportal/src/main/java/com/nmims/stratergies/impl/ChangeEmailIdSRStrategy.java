package com.nmims.stratergies.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.enums.ProfileDetailEnum;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.repository.ServiceRequestRepository;
import com.nmims.services.ChangeContactDetailsSRService;
import com.nmims.stratergies.ChangeDetailsSRStrategyInterface;

/**
 * A Service Layer created which contains methods for Change EmailId Implementation of Change in Contact Details Service Request.
 * @author Raynal Dcunha
 */
@Service
@Qualifier("ChangeEmailIdSR")
public class ChangeEmailIdSRStrategy extends ChangeContactDetailsSRService implements ChangeDetailsSRStrategyInterface {
	private static final ProfileDetailEnum DETAIL_TYPE = ProfileDetailEnum.EMAIL_ID;
	private static final boolean IS_LDAP_ATTRIBUTE = true;
	
	private final ServiceRequestRepository serviceRequestRepository;
	private final SalesforceHelper salesforceHelper;
	
	@Autowired
	public ChangeEmailIdSRStrategy(ServiceRequestRepository serviceRequestRepository, SalesforceHelper salesforceHelper, 
									MailSender mailSender, AWSHelper awsHelper) {
		super(serviceRequestRepository, mailSender, awsHelper);
		Objects.requireNonNull(serviceRequestRepository);			//Fail-fast approach, field is guaranteed be non-null.
		Objects.requireNonNull(salesforceHelper);
		this.serviceRequestRepository = serviceRequestRepository;
		this.salesforceHelper = salesforceHelper;
	}
	
	@Override
	public ProfileDetailEnum getDetailType() {
		return DETAIL_TYPE;
	}
	
	@Override
	public String serviceRequestDescription(Long sapid, String updateValue, String currentValue) {
		return ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS.getValue() + " Service Request [Change " + getDetailType().getValue() + "] StudentNo: " + sapid + 
				" | Update Email ID: " + updateValue + " | Current Email ID: " + currentValue;
	}

	@Override
	public Boolean isDetailPresentInLdap() {
		return IS_LDAP_ATTRIBUTE;
	}
	
	@Override
	public Boolean checkSrEligibility(Long sapid) {
		List<String> openSrDescriptionList = getOpenSrDescriptionList(sapid);
		
		for(String description: openSrDescriptionList) {
			String detailType = getDetailTypeFromDescription(description);			//Extract the detail type from the passed Service Request description 
			if((getDetailType().getValue()).equals(detailType))			//Check if the type extracted from Service Request description matches the particular detail type
				return false;
		}
		
		return true;
	}
	
	@Override
	public Boolean checkValidInput(String input) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_\\+&\\*\\-]+(?:\\.[a-zA-Z0-9_\\+&\\*\\-]+)*@(?:[a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,7}$");	
	    Matcher matcher = pattern.matcher(input);
        return matcher.matches();
	}
	
	@Override
	public void performChangeDetailsSrChecks(String valuePresent, ChangeDetailsSRDto srDto) {
		if(!checkSrEligibility(srDto.getSapid()))
			throw new IllegalArgumentException("Failed to raise Change in " + getDetailType().getValue() + " Service Request, Student " + srDto.getSapid() + 
												" Not Eligible to raise Service Request!");
		
		if(checkInputEquality(valuePresent, srDto.getUpdateValue()))
			throw new IllegalArgumentException("Failed to raise Service Request, entered Email address same as current Email address.");
		
		if(!checkValidInput(srDto.getUpdateValue()))
			throw new IllegalArgumentException("Failed to raise Service Request, entered Email address contains illegal characters.");
	}

	@Override
	public Boolean checkStudentEnrollmentAfterSalesforceDate(Long sapid) {
		Map<String, Object> studentEnrollYearMonth = studentEnrollmentDetails(sapid);
		//Concatenate the Enrollment Year and Month in yyyyMMM format
		String studentEnrollementYearMonth = String.valueOf(studentEnrollYearMonth.get("enrollmentYear")) + String.valueOf(studentEnrollYearMonth.get("enrollmentMonth"));
		return enrollmentAfterSalesforceDate(studentEnrollementYearMonth);
	}
	
	@Override
	public int updateDetail(Long studentSapid, String value, String user) {
		return serviceRequestRepository.studentEmailIdUpdate(studentSapid, value, user);
	}
	
	@Override
	public String updateSalesforceField(Long sapid, String value) {
		Map<String, String> response = salesforceHelper.updateSalesforceAccountField(sapid, getDetailType().getValue(), value);
		
		if(response.get("responseCode").equals("200")) 
			return response.get("responseMessage");
		else
			throw new RuntimeException("Error while updating " + getDetailType().getValue() + " field in Salesforce. Error Message: " + response.get("responseMessage"));
	}

	@Override
	public String getStoredLdapAttributeValue(Long sapid) {
		return serviceRequestRepository.getStudentEmailAttributeLdap(sapid);
	}

	@Override
	public void updateLdapAttribute(Long sapid, String attributeValue) {
		serviceRequestRepository.updateStudentMailAttributeLdap(sapid, attributeValue);
	}
}
