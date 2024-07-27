package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

//
@Component("customStringToArrayConverter")
//@ConfigurationPropertiesBinding
public class CustomStringToArrayConverter implements Converter<String, ArrayList<String>>{
	
	private static final Logger logger = LoggerFactory.getLogger(CustomStringToArrayConverter.class);
	
	public CustomStringToArrayConverter(){
		//System.out.println("CustomStringToArrayConverter Bean created");
		logger.info("CustomStringToArrayConverter Bean created");
	}
	
	@Override 
    public ArrayList<String> convert(String source) {
		//System.out.println("Source = "+source);
		logger.info("Source = "+source);
        return new ArrayList<String>(Arrays.asList(source.split(";")));
    }

}
