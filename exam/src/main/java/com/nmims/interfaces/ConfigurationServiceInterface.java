package com.nmims.interfaces;

import java.util.List;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.TimeBoundUserMapping;

public interface ConfigurationServiceInterface {
	
	public ConsumerProgramStructureExam updateMasterKeyDetails(ConsumerProgramStructureExam bean);
	
	public List<TimeBoundUserMapping> downloadTimeBoundExcelService(String timeBoundSubjectConfigId,String prgm_sem_subj_id,String batchId) throws Exception;
}
