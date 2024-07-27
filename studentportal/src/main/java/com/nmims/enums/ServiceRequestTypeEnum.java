package com.nmims.enums;

public enum ServiceRequestTypeEnum {
	CHANGE_IN_CONTACT_DETAILS("Change in Contact Details"),
	CHANGE_FATHER_MOTHER_SPOUSE_NAME("Change Father/Mother/Spouse Name");
	
	private String value;
	private ServiceRequestTypeEnum(String value) {		//Create a Constructor which holds a value for an Enum
		this.value = value;
	}
	
	public String getValue() {		//Getter
		return value;
	}
}
