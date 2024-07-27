package com.nmims.enums;

import java.util.Arrays;

public enum ProfileDetailEnum {
	EMAIL_ID("emailId"),
	MOBILE_NO("mobile"),
	FATHER_NAME("fatherName"), 
	MOTHER_NAME("motherName"), 
	SPOUSE_NAME("husbandName");
	
	private String value;
	private ProfileDetailEnum(String value) {		//Create a Constructor which holds a value for an Enum
		this.value = value;
	}
	
	public String getValue() {		//Getter
		return value;
	}
	
	/**
	 * Enum element is returned based on the passed value.
	 * @param enumValue - value of the enum that is to be returned
	 * @return Enum element
	 */
	public static final ProfileDetailEnum getByValue(String enumValue) {
	    return Arrays.stream(ProfileDetailEnum.values()).filter(profileDetail -> profileDetail.value.equals(enumValue)).findFirst().get();
	}
}
