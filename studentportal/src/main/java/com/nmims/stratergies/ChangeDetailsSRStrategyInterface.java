package com.nmims.stratergies;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.enums.ProfileDetailEnum;

/**
 * Strategy interface created for implementations of Change Details SR types
 * @author Raynal Dcunha
 */
public interface ChangeDetailsSRStrategyInterface {
	static final String[] validFileTypes = {"image/jpeg", "image/png", "image/svg+xml", "application/pdf",
											"application/zip", "application/x-zip-compressed", "application/vnd.rar", "application/x-rar-compressed",
											"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
	
	/**
	 * The type of ProfileDetailEnum is returned.
	 * @return ProfileDetailEnum element
	 */
	ProfileDetailEnum getDetailType();
	
	/**
	 * Create a description for the Change Details Service Request, 
	 * containing the current and updated values of the detail to be updated.
	 * @param sapid - studentNo of the Student
	 * @param updateValue - value of the detail updated by the student
	 * @param currentValue - value of the current student detail
	 * @return Service Request description as a String value
	 */
	String serviceRequestDescription(Long sapid, String updateValue, String currentValue);
	
	/**
	 * Returns true if the particular detail is present in LDAP as an attribute.
	 * @return boolean value indicating if the detail is present as an LDAP attribute
	 */
	Boolean isDetailPresentInLdap();
	
	/**
	 * Check if the student has enrolled after the Salesforce date (June 2014).
	 * @param sapid - studentNo of the Student
	 * @return boolean value indicating if the student record is present in Salesforce or not
	 */
	Boolean checkStudentEnrollmentAfterSalesforceDate(Long sapid);
	
	/**
	 * Check if the student has already raised a Change Details Service Request and is in Open state, 
	 * if there are no Service Request' found, or if the found SR's are not in Open state, 
	 * then the student is eligible to raise a Change Details Service Request.
	 * @param sapid - studentNo of the Student
	 * @return boolean value indicating if the student is eligible to raise the Change Details Service Request or not
	 */
	Boolean checkSrEligibility(Long sapid);
	/**
	 * Checks if the passed input value is valid or not.
	 * @param input - String input
	 * @return boolean value indicating if the input is valid or not
	 */
	Boolean checkValidInput(String input);
	/**
	 * Performing checks for the Change Details Service Request.
	 * @param valuePresent - value of the student detail already present in database
	 * @param changeDetailsSRDto - DTO containing details of the Change Details Service Request
	 */
	void performChangeDetailsSrChecks(String valuePresent, ChangeDetailsSRDto changeDetailsSRDto);
	
	/**
	 * Updates the student detail in database and returns the number of rows updated.
	 * @param studentSapid - studentNo of the Student
	 * @param value - value to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	int updateDetail(Long studentSapid, String value, String user);
	
	/**
	 * Gets the attribute value stored in the LDAP Object of Student.
	 * @param sapid - studentNo of the Student
	 * @return attribute value fetched from LDAP
	 */
	String getStoredLdapAttributeValue(Long sapid);
	/**
	 * Update the LDAP Object attribute value (of the specific detail type) of Student.
	 * @param sapid - studentNo of the Student
	 * @param attributeValue - value of the LDAP attribute
	 */
	void updateLdapAttribute(Long sapid, String attributeValue);
	
	/**
	 * Update the Salesforce field matching the profile detail type 
	 * and return the response received from Salesforce.
	 * @param sapid - studentNo of the Student
	 * @param value - value to be updated
	 * @return response received after the update() call on Salesforce
	 */
	String updateSalesforceField(Long sapid, String value);
	
	/**
	 * The detail type from the Service Request description is returned.
	 * @param description - Service Request description
	 * @return SR detail type as a String
	 */
	default String getDetailTypeFromDescription(String description) {
		Pattern pattern = Pattern.compile("\\[Change(.*?)\\]");		//(.*?) is to return a group (of any characters) containing of a non-greedy match 
		Matcher matcher = pattern.matcher(description);
		
		String detailType = "";
		while (matcher.find())
			detailType = matcher.group(1).trim();
		
		return detailType;
	}
	
	/**
	 * The Service Request description is broken into 3 parts, separated by the pipe (|) character, 
	 * student sapid, updated and current detail values are retrieved from the separated parts.
	 * @param description - Service Request description
	 * @return Map containing detail values retrieved from the SR description
	 */
	default Map<String, String> getValuesFromSrDescription(String description) {
		String[] pipeSplitArray = description.split("\\|", 3);
		List<String> valuesList = new ArrayList<String>(3);
		for(String pipeSplitString: pipeSplitArray) {
			valuesList.add(pipeSplitString.split(":", 2)[1].trim());
		}
		
		Map<String, String> srDescriptionAttrMap = new HashMap<String, String>(3);
		srDescriptionAttrMap.put("sapid", valuesList.get(0));
		srDescriptionAttrMap.put("updateValue", valuesList.get(1));
		srDescriptionAttrMap.put("currentValue", valuesList.get(2));
		return srDescriptionAttrMap;
	}
	
	/**
	 * Checks equality of the passed String inputs.
	 * @param firstInput - first String input
	 * @param secondInput - second String input
	 * @return boolean value indicating the inputs are equal or not
	 */
	default Boolean checkInputEquality(String firstInput, String secondInput) {
		return firstInput.equals(secondInput);
	}
	
	/**
	 * Check if the Content-Type document is valid (as accepted).
	 * @param file - multiPart document file
	 * @return boolean value indicating if the document type is valid  or not
	 */
	default Boolean checkDocumentType(MultipartFile file) {
		String fileType = file.getContentType();
		return Arrays.asList(validFileTypes).contains(fileType);
	}
	/**
	 * Check if the document size is 5 MB or below.
	 * @param file - multiPart document file
	 * @return boolean value indicating if the document size is valid or not
	 */
	default Boolean checkDocumentSize(MultipartFile file) {
		long fileSize = file.getSize();
		return fileSize <= 5 * 1024 * 1024 && fileSize > 0;
	}
	/**
	 * Perform checks for the MultiPart file.
	 * @param file - multiPart document file to be checked
	 */
	default void performDocumentChecks(MultipartFile file) {
		if(Objects.nonNull(file) && !file.isEmpty()) {
			if(!checkDocumentType(file))
				throw new IllegalArgumentException("Failed to raise Service Request, uploaded supporting document type not accepted.");
			
			if(!checkDocumentSize(file))
				throw new IllegalArgumentException("Failed to raise Service Request, uploaded supporting document should be below 5MB in size.");
		}
		else
			throw new IllegalArgumentException("Failed to raise Service Request, uploaded supporting document is empty. Please try again with a different file!");
	}
	
	/**
	 * Checks if the yearMonth is after the Salesforce Date (June 2014)
	 * @param enrollYearMonth - Year and Month in String (yyyyMMM) format
	 * @return boolean value indicating if passed YearMonth is after than Salesforce YearMonth
	 */
	default Boolean enrollmentAfterSalesforceDate(String yearMonth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMM");
		YearMonth enrollmentYearMonth = YearMonth.parse(yearMonth, formatter);
		YearMonth salesforceYearMonth = YearMonth.of(2014, 6);
		
		return enrollmentYearMonth.isAfter(salesforceYearMonth);
	}
}
