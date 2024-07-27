package com.nmims.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface StringUtility {
	
	public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }//isEmpty(-)

	
	public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
            	if(cs.charAt(i)=='.')
            		continue;
                return false;
            }
        }
        return true;
    }//isNumeric(-)
	
	/**
	 * Generate comma separated string based on the given string which having the new line or carriage return as separator.
	 * @param sapIdList - The string having the new line or carriage return as separator.
	 * @return comma separated string.
	 */
	public static String generateCommaSeparatedList(String sapIdList) {
		
		//Replace carriage return or new line or carriage return and new line with comma.
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		
		//Remove the last comma if string ends with comma.
		if(commaSeparatedList.endsWith(","))
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		
		//return comma separated string.
		return commaSeparatedList;
	}


	public static String createCommaSeprateStringByList(List<String> list) {
			String commaSepratedString  = list.stream().collect(Collectors.joining("','", "'","'"));
			return commaSepratedString;
		}
	
	public static String generateCommaSeprateStringByInteger(List<Integer> list) {
		StringBuilder stringBuilder =  new StringBuilder();
		list.stream().forEach(id ->stringBuilder.append(id).append(","));
		stringBuilder.deleteCharAt(stringBuilder.length()-1);
		String commaSepratedString = stringBuilder.toString();
		return commaSepratedString;
	}
}
