package com.nmims.util;

import org.apache.commons.lang3.StringUtils;

public class StudentProfileUtils {
	private static final String CHARACTER_DOT = ".";
	
	/**
	 * Checks if the lastName consists only of dot (period) character.
	 * Dot as lastName is not allowed as per Card: 14965
	 * Or if the lastName is null, empty or contains only whitespace characters.
	 * @param lastName - last name of the student
	 * @return boolean value indicating if the name is valid
	 */
	public static boolean isValidLastName(String lastName) {
		if(StringUtils.isBlank(lastName) || CHARACTER_DOT.equals(lastName.trim()))
			return false;
		
		return true;
	}
	
	/**
	 * Checks if the lastName is null, empty or contains only whitespace, 
	 * and returns lastName as dot (period), same as stored in Salesforce.
	 * @param lastName - last name of the user
	 * @return valid last name
	 */
	public static String getValidLdapLastName(String lastName) {
		if(StringUtils.isBlank(lastName))
			return CHARACTER_DOT;
		
		return lastName;
	}
	
	/**
	 * Checks if the lastName is null, empty or contains only whitespace, 
	 * and returns first name as the display name,
	 * else full name of the user is returned.
	 * @param firstName - first name of the user
	 * @param lastName - last name of the user
	 * @return valid display name
	 */
	public static String getValidLdapDisplayName(String firstName, String lastName) {
		if(StringUtils.isBlank(lastName))
			return firstName;
		
		return firstName + " " + lastName;
	}
}
