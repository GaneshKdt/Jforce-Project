package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.TEEResultBean;

public interface DissertationQ8ResultDaoInterface {

	public List<TEEResultBean> getConsumerAndSubjectId(String subject, int sem);

	public List<BatchExamBean> getBatchList(String consumerProgramStructureId, int sem);

	public int upsertIntoMarks(List<DissertationResultBean> upsertList);

	public List<DissertationResultBean> getQ8MarksList(String timebound_id);

	public int upsertMarkstoStaging(List<DissertationResultBean> finalUpsertListForStaging);

	public List<DissertationResultBean> getGraceApplicabeStudent(String timebound_id);

	public int upsertGraceList(List<DissertationResultBean> processedGrace);

	public List<DissertationResultBean> getPassFailStaging(String timebound_id);

	public int upsertIntoPassFail(List<DissertationResultBean> passfailList);

	public int deleteFromStaging(String timebound_id);

	public int makeLive(String timebound_id);

	public int updateMarksProccessed(List<DissertationResultBean> finalUpsertListForStaging);

	public DissertationResultBean getDissertationResult(String sapid, String timeboundId);

	public List<EmbaGradePointBean> getAllGrade();

	public int upsertGrade(List<DissertationResultBean> upsertList);



	
}
