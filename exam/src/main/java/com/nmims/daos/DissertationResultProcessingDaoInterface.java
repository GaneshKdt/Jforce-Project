package com.nmims.daos;

import java.util.List;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.dto.DissertationResultProcessingDTO;


public interface DissertationResultProcessingDaoInterface {
	
	public List<TEEResultBean> getConsumerAndSubjectId(String subject, int sem);

	public List<BatchExamBean> getBatchList(String consumerProgramStructureId,int sem);


	public List<TimeBoundUserMapping> getMappedStudent(String timeBound);

	public String getStudentSubjectConfig(String timeboundId);

	public DissertationResultProcessingDTO getProgram(String subjectId);

	

	
	
	


	public int getTimboundDetails(String sapid, String timeboundId);

	public int getSessionPlanId(int timeboundId);
	
	public List<Integer> getSessionIds(int sessionPlanId);
	
	public List<Integer> getTestIds(String refId);
	
	public List<DissertationResultProcessingDTO> getTestScores(String sapidList, String commaSepratedTestId);
	

	public List<TestExamBean> getExamTest(String commaSepratedTestId);

	public int upsertMarks(List<DissertationResultBean> insertMarksData);

	public List<DissertationResultBean> getNotProcessedList(String timeBoundId);

	public int upsertPassFailStaging(List<DissertationResultBean> upsertList);

	public List<DissertationResultBean> getStagedDissertationList(String timeBoundId);

	public int transferrToPassFailQ7(List<DissertationResultBean> transferList);

	public int deleteStagingData(String timeBoundId);

	public int updateUpsertList(List<DissertationResultBean> transferListForStaging);

	public DissertationResultBean getDissertationResult(String sapid, String timeboundId);

	public int makeResultLive(String timebound_id);

	public int upsertGradeInPassFailStaging(List<DissertationResultBean> processListForGrade);

	public List<EmbaGradePointBean> getAllGrades();

	public DissertationResultBean getPassFail(String sapid);

	public int checkSapidExist(String sapid);

	public DissertationResultProcessingDTO getSubjectName(int subjectIdForQ7);

	public List<Integer> getTimeBoundUser(String sapid);

	public List<DissertationResultProcessingDTO> getTimeBounds(String commaSepratedTimeBoundIds);


}
