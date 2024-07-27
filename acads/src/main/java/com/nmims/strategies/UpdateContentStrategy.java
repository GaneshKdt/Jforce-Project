package com.nmims.strategies;

import java.util.HashMap;

import com.nmims.beans.ContentAcadsBean;

public interface UpdateContentStrategy {

	public abstract HashMap<String,String> updateContent(ContentAcadsBean contentFromForm) throws Exception;
	
	public abstract HashMap<String,String> updateContentSingleSetup(ContentAcadsBean ContentFromForm) throws Exception;
	
	public abstract HashMap<String,String> updateContentByDistinct(ContentAcadsBean ContentFromForm,String masterKeys) throws Exception;


}

