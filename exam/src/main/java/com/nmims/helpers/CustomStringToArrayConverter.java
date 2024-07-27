package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class CustomStringToArrayConverter implements Converter<String, ArrayList<String>>{
	
	public CustomStringToArrayConverter(){
	}
	
	@Override
    public ArrayList<String> convert(String source) {
        return new ArrayList<String>(Arrays.asList(source.split(";")));
    }

}
