package com.nmims.util;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface NumberUtility {
	/**
	 * Get equivalent Roman number to natural number.
	 * @param semester having natural number. 
	 * @return Roman number
	 */
	public static String getRomanNumber(Integer semester) {
		
		switch(semester) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		default:
			return "";
		}
	}//getRomanNumber(-)
}
