package com.nmims.services;

import java.util.List;
import java.util.Map;

import com.nmims.beans.ProgramSubjectMappingExamBean;


/**
 * 
 * @author Siddheshwar_K
 *
 */
public interface ICacheService {
	public Map<String, String> getCorporateCenterUserMapping() throws Exception;
	
	public void refreshCache() throws Exception;
	
	public Map<String, String> getExamCenterIdNameMap() throws Exception;
	
	public Map<String, String> getCorporateExamCenterIdNameMap() throws Exception;
	
	public Map<String, String> getProgramMap() throws Exception;
	
	public List<String> getSubjectList() throws Exception;
	
	public List<String> getProgramList() throws Exception;
	
	public List<ProgramSubjectMappingExamBean> getProgramSubjectMappingList() throws Exception;
}
