package com.nmims.util;

import java.util.List;

/**
 * @author Siddheshwar_Khanse
 * */

public interface ContentUtil {

	/**
	 * @param values - Contains List of Integer values. 
	 * @return String -	Returns comma separated single string.
	 */
	public static String frameINClauseString(List<Integer> values){
		StringBuilder sb=null; 
		sb = new StringBuilder();
		
		//If list of string is empty or null then return empty string
		if(values == null || values.isEmpty())
			return "''";
		
		//Preparing a comma separated single string builder object of multiple list of integer values
		for(Integer val:values){
			sb.append("'");
			sb.append(val);
			sb.append("',");
		}
		//Get the index of last comma
		int commaIndex=sb.lastIndexOf(",");
		//Remove the last comma
		if(commaIndex!=-1)
			sb.delete(commaIndex,commaIndex+1);
		
		//Return a string
		return sb.toString();
	}//frameINClauseString()
}