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
import com.nmims.services.ChangeFatherMotherSpouseNameSR;
import com.nmims.stratergies.ChangeDetailsSRStrategyInterface;

@Service
@Qualifier("ChangeMotherNameSR")
public class ChangeMotherNameSRStrategy extends ChangeFatherMotherSpouseNameSR implements ChangeDetailsSRStrategyInterface {
	private static final ProfileDetailEnum DETAIL_TYPE= ProfileDetailEnum.MOTHER_NAME;
	private static final boolean IS_LDAP_ATTRIBUTE = false;
	
	private final ServiceRequestRepository serviceRequestRepository;
	private final SalesforceHelper salesforceHelper;
	
	@Autowired
	public ChangeMotherNameSRStrategy(ServiceRequestRepository serviceRequestRepository, SalesforceHelper salesforceHelper, MailSender mailSender, AWSHelper awsHelper) {
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
		return ServiceRequestTypeEnum.CHANGE_FATHER_MOTHER_SPOUSE_NAME.getValue() + " Service Request [Change " + getDetailType().getValue() + "] StudentNo: " + sapid + 
				" | Update Mother FirstName: " + updateValue + " | Current Mother FirstName: " + currentValue;
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
		Pattern pattern = Pattern.compile("^[a-zA-Z\\.\\-'\\xC0-\\uFFFF]+$");	
	    Matcher matcher = pattern.matcher(input);

        return matcher.matches();
	}
	
	@Override
	public void performChangeDetailsSrChecks(String valuePresent, ChangeDetailsSRDto srDto) {
		if(!checkSrEligibility(srDto.getSapid()))
			throw new IllegalArgumentException("Failed to request " + getDetailType().getValue() + " Service Request, Student " + srDto.getSapid() + 
												" Not Eligible to raise Service Request!");
		
		if(checkInputEquality(valuePresent, srDto.getUpdateValue()))
			throw new IllegalArgumentException("Failed to raise Service Request, entered Mother name same as current Mother name.");
		
		if(!checkValidInput(srDto.getUpdateValue()))
			throw new IllegalArgumentException("Failed to raise Service Request, entered Mother name contains illegal characters.");
		
		performDocumentChecks(srDto.getSupportingDocument());
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
		return serviceRequestRepository.studentMotherNameUpdate(studentSapid, value, user);
	}
	
	@Override
	public String updateSalesforceField(Long sapid, String value) {
		Map<String, String> response = salesforceHelper.updateSalesforceAccountField(sapid, getDetailType().getValue(), value);
		
		if(response.get("responseCode").equals("200"))
			return response.get("responseMessage");
		else
			throw new RuntimeException("Error while updating " + getDetailType().toString() + " Field in Salesforce. Error Message: " + response.get("responseMessage"));
	}
	
	@Override
	public String getStoredLdapAttributeValue(Long sapid) {			//Not Applicable for MotherName detail
		throw new UnsupportedOperationException("This method is not applicable for the ChangeMotherNameSR Strategy implementation.");
	}

	@Override
	public void updateLdapAttribute(Long sapid, String attributeValue) {		//Not Applicable for MotherName detail
		throw new UnsupportedOperationException("This method is not applicable for the ChangeFatherNameSR Strategy implementation.");
	}
}
