package com.nmims.assembler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains static methods which make use of Java Reflection 
 * to copy non-null values from one object onto another.
 * Name of the fields need to be similar on both the objects (source & destination),
 * hence copying field values between two object of the same class is preferred.
 * @author Raynal Dcunha
 */
public class ObjectDifferenceCopier {
	
	/**
	 * Copy non-null field values from source object onto destination object
	 * @param destination object
	 * @param source object
	 */
	public static <T> void copyNonNullObjectValuesIntoAnother(T destination, T source) {
		try {
			for(Field field: source.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				
				String sourceFieldName = field.getName();
				Object sourceFieldValue;
				try {
					sourceFieldValue = field.get(source);
				} 
				catch (IllegalArgumentException | IllegalAccessException ex) {
					continue;
				}
				
//				System.out.printf("Source Field name: %s, Field value: %s\n", sourceFieldName, sourceFieldValue);
				
				if(null != sourceFieldValue) {
					Field destinationField;
					try {
						destinationField = destination.getClass().getDeclaredField(sourceFieldName);
						destinationField.setAccessible(true);
						
						destinationField.set(destination, sourceFieldValue);
					} 
					catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
						continue;
					}
					
				}
			}
		}
		catch(Exception ex) {
			//ex.printStackTrace();
		}
	}
	
	/**
	 * Copy non-null field values from source object onto destination object,
	 * if those values aren't already present in the destination object.
	 * @param destination object
	 * @param source object
	 */
	public static <T> void copyObjectDifference(T destination, T source)  {
		try {
			for(Field destinationField: destination.getClass().getDeclaredFields()) {
				destinationField.setAccessible(true);
				
				String destinationFieldName = destinationField.getName();
				Object destinationFieldValue;
				try {
					destinationFieldValue = destinationField.get(destination);
				} 
				catch (IllegalArgumentException | IllegalAccessException ex) {
					continue;
				}
				
//				System.out.printf("Destination Field name: %s, Field value: %s\n", destinationFieldName, destinationFieldValue);
				
				if(null == destinationFieldValue) {
					Field sourceField;
					try {
						sourceField = source.getClass().getDeclaredField(destinationFieldName);
					} 
					catch (NoSuchFieldException | SecurityException ex) {
						continue;
					}
					sourceField.setAccessible(true);
					
					Object sourceFieldValue;
					try {
						sourceFieldValue = sourceField.get(source);
//						System.out.printf("Source Field name: %s, Field value: %s\n", destinationFieldName, sourceFieldValue);
						
						if(null != sourceFieldValue) 
							destinationField.set(destination, sourceFieldValue);
					} 
					catch (IllegalArgumentException | IllegalAccessException ex) {
						continue;
					}
				}
			}
		}
		catch(Exception ex) {
			//ex.printStackTrace();
		}
	}
	
	/**
	 * Copy non-null field values from source object (and its superclass) onto the destination object,
	 * if those values aren't already present in the destination object.
	 * @param destination
	 * @param source
	 */
	public static <T> void copyObjectExtendingSuperClassDifference(T destination, T source)  {
		try {
			Field[] destinationFields = destination.getClass().getDeclaredFields();
			Field[] destinationSuperFields = destination.getClass().getSuperclass().getDeclaredFields();
			
			List<Field> destinationFieldsList = new ArrayList<>(Arrays.asList(destinationFields));
			destinationFieldsList.addAll(Arrays.asList(destinationSuperFields));
			
			Field[] sourceFields = source.getClass().getDeclaredFields();
			Field[] sourceSuperFields = source.getClass().getSuperclass().getDeclaredFields();
			
			List<Field> sourceFieldsList = new ArrayList<>(Arrays.asList(sourceFields));
			sourceFieldsList.addAll(Arrays.asList(sourceSuperFields));
			
			for(Field destinationField: destinationFieldsList) {
				destinationField.setAccessible(true);
				
				Object destinationFieldValue;
				try {
					destinationFieldValue = destinationField.get(destination);
				} 
				catch (IllegalArgumentException | IllegalAccessException ex) {
					continue;
				}
				
//				System.out.printf("Destination Field name: %s, Field value: %s\n", destinationField.getName(), destinationFieldValue);
				
				if(null == destinationFieldValue) {
					Field sourceField;
					try {
						int sourceFieldsListIndex = sourceFieldsList.indexOf(destinationField);
						if(sourceFieldsListIndex > -1)
							sourceField = sourceFieldsList.get(sourceFieldsListIndex);
						else
							continue;
					} 
					catch (SecurityException ex) {
						continue;
					}
					sourceField.setAccessible(true);
					
					Object sourceFieldValue;
					try {
						sourceFieldValue = sourceField.get(source);
//						System.out.printf("Source Field name: %s, Field value: %s\n", sourceField.getName(), sourceFieldValue);
						
						if(null != sourceFieldValue) 
							destinationField.set(destination, sourceFieldValue);
					} 
					catch (IllegalArgumentException | IllegalAccessException ex) {
						continue;
					}
				}
			}
		}
		catch(Exception ex) {
			//ex.printStackTrace();
		}
	}
}
