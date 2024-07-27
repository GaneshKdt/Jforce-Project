package com.nmims.strategies;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.nmims.beans.ContentFilesSetbean;

@Component
public interface UploadStratergy {
	
	public abstract HashMap<String,String> createContent(ContentFilesSetbean filesSet) throws Exception;
	
}
