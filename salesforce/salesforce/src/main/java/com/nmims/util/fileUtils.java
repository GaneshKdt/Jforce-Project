package com.nmims.util;

import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;

public class fileUtils 
{
	
	
	
	public static int checkFileExtensions(String extension)
	{
		String[] allowedExtension = {
				"jpg","jpeg","png","gif","pdf","doc","docx","ppt","pptx","xls","xlsx","zip","rar"};
		
		if(Arrays.asList(allowedExtension).contains(extension.toLowerCase()))
			return 1;
		else
			return 0;
		
	}
	
	
	public static int checkFileExtensionsForStudentImage(String extension)
	{  
		String[] allowedExtension = {"jpg","jpeg","png"};
		
		if(Arrays.asList(allowedExtension).contains(extension.toLowerCase()))
			return 1;
		else
			return 0;
		
	}
	
	

}
