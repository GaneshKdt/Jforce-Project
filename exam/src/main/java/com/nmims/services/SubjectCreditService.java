package com.nmims.services;

import java.util.Map;
import com.nmims.beans.MDMSubjectCodeMappingBean;

public interface SubjectCreditService {
	
	public Map<Integer, MDMSubjectCodeMappingBean> getMappedSubjectCredit() throws Exception;
	public Map<String, MDMSubjectCodeMappingBean> getMappedPssDetail() throws Exception;
}
