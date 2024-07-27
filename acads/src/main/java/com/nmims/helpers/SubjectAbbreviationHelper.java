package com.nmims.helpers;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("subjectAbbreviationHelper")
public class SubjectAbbreviationHelper {

	public String createAbbreviation( String string) {
	
		String abbreviation = "";
		String stringWithoutSpecialChar = string.replaceAll("[^\\w\\s]","");
	    String[] words = stringWithoutSpecialChar.split(" ");
	    
	    
	    for (int i = 0; i < words.length; i++) {
	    	
	        String word = words[i];

	        if( word.matches("^[a-zA-Z]*$") && !( word.equals("I") || word.equals("II") )
	        		&& !StringUtils.isBlank(word) && !word.equals("Module") ) {

	        	abbreviation += word.toUpperCase().charAt(0);
	        
	        }else if( word.equals("I") || word.equals("II") ){
	        	
	        	abbreviation += word.toUpperCase();
	        	
	        }
	    }

	    return abbreviation;
		
	}
	
}
