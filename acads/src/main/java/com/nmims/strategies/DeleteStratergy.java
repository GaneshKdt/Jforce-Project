package com.nmims.strategies;

import java.util.HashMap;

public interface DeleteStratergy {
	public abstract HashMap<String,String> deleteContent(String contentId) throws Exception;
	
	public abstract HashMap<String,String> deleteContentSingleSetup(String contentId, String consumerProgramStructureId) throws Exception;
	
	public abstract HashMap<String, String> deleteContentByDistinct(String contentId, String consumerProgramStructureId) throws Exception;
}
