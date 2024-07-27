package com.nmims.assembler;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * ObjectConverter Class contains static methods which use the Jackson ObjectMapper 
 * for reading and writing JSON, either to and from basic POJOs (Plain Old Java Objects), 
 * or to and from a general-purpose JSON.
 * Also convert fields of an Object and map it to another Object or as a String in JSON format.
 * @author Raynal Dcunha
 */
public class ObjectConverter {
	
	/**
	 * Converts an Object to the TypeReference Class object using Jackson ObjectMapper.
	 * @param object - Object which needs to be converted
	 * @param ref - TypeReference of Class which is to be returned
	 * @return the converted Object (Object Class of TypeReference)
	 */
	public static <T> T convertObjToXXX(Object object, TypeReference<T> ref){
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
	    
	    return objectMapper.convertValue(object, ref);
	}
	
	/**
	 * Converts the fields present in the Object to a String in JSON format (key : value pairs).
	 * @param object - The Object which needs to be converted
	 * @param ignoreProperties - String array which contains names of fields which are to be ignored while mapping
	 * @return a JSON formatted String (containing key : value pairs) of the fields present in the Object
	 */
	public static String filterPropertiesFromObjectAsJsonString (Object object, String[] ignoreProperties) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
		
		SimpleBeanPropertyFilter responseFilter = SimpleBeanPropertyFilter.serializeAllExcept(ignoreProperties);
		FilterProvider responseFilterAdder = new SimpleFilterProvider().addFilter("StudentProfileDtoFilter", responseFilter);

		ObjectWriter writer = objectMapper.writer(responseFilterAdder);
		String writeObjectAsString;
		try {
			writeObjectAsString = writer.withDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (JsonProcessingException ex) {
			writeObjectAsString = "Could not process Object as JSON!";
			//ex.printStackTrace();
		}
		
		return writeObjectAsString;
	}
	
	/**
	 * Map the fields of an Object to a String in JSON format
	 * @param obj - The Object which contains the fields that needs to be used for mapping 
	 * @return A String which contains the Object fields in JSON format (key : value pairs)
	 * @throws JsonProcessingException
	 */
	public static String mapToJson(Object obj) throws JsonProcessingException {
	   ObjectMapper objectMapper = new ObjectMapper();
	   objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	   objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
	   
	   return objectMapper.writeValueAsString(obj);
	}
	
	/**
	 * Maps the values from the JSON formatted String into an Object of Class provided as the parameter.
	 * @param json - String which is in JSON format
	 * @param returnClass - the Class from which the Object needs to be created
	 * @return An Object with values obtained from the String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T mapFromJson(String json, Class<T> returnClass) throws JsonParseException, JsonMappingException, IOException {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
		
      return objectMapper.readValue(json, returnClass);
   }
	
}
