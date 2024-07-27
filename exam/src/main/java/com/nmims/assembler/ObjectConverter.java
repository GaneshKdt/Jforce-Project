package com.nmims.assembler;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
	public static <T> T convertObjToXXX(Object object, TypeReference<T> ref) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
	    
	    return objectMapper.convertValue(object, ref);
	}
	
	/**
	 * Converts an Object to the TypeReference Class object using Jackson ObjectMapper.
	 * All the String fields which are empty are deserialized as null.
	 * @param object - Object which needs to be converted
	 * @param ref - TypeReference of Class which is to be returned
	 * @return the converted Object (Object Class of TypeReference)
	 */
	public static <T> T convertObjWithEmptyStringAsNull(Object object, TypeReference<T> ref) {
		//Creating a Custom Deserializer which deserializes empty String values as null
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new JsonDeserializer<String>() {
			@Override
			public String deserialize(JsonParser jsonParser, DeserializationContext ctxt)
					throws IOException, JsonProcessingException {
				String value = jsonParser.getText();
				return (value == null || value.isEmpty()) ? null : value;
			}
		});
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);		//unknown properties won't throw JsonMappingException
//		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));	//Add when a JsonFilter is present in the Serializable class but filter is not applied
		objectMapper.registerModule(module);	//Registering a module which provides the desired functionality of converting empty String properties to null

		return objectMapper.convertValue(object, ref);
	}
	
	/**
	 * Map the fields of an Object to a String in JSON format
	 * @param obj - The Object which contains the fields that needs to be used for mapping 
	 * @return A String which contains the Object fields in JSON format (key : value pairs)
	 * @throws JsonProcessingException
	 */
	public static String mapToJson(Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
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
	public static <T> T mapFromJson(String json, Class<T> returnClass)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

		return objectMapper.readValue(json, returnClass);
	}
}
