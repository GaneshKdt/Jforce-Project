package com.nmims.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;

public interface DissertationQ8ResultService {

	public List<Integer> getConsumerAndSubjectId();

	public List<BatchExamBean> getBatchList(List<Integer> consumerProgramStructureId);

	public List<DissertationResultBean> getDissertationQ8Scores(String timebound_id,
			List<TEEResultBean> eligibleIAStudents, List<DissertationResultBean> errorList, String loggerInUser);

	public int upsertIntoMarks(List<DissertationResultBean> upsertList);

	public List<DissertationResultBean> getQ8MarksList(String timebound_id);

	public List<DissertationResultBean> processUpsertList(List<DissertationResultBean> upsertListForStaging, String loggedInUser);

	public int upsertMarkstoStaging(List<DissertationResultBean> finalUpsertListForStaging);

	public List<DissertationResultBean> getGraceApplicabeStudent(String timebound_id);

	public List<DissertationResultBean> applyGrace(List<DissertationResultBean> graceStudent);

	public int upsertGraceList(List<DissertationResultBean> processedGrace);

	public int transferToPassFail(String timebound_id, List<DissertationResultBean> passFailList) throws SQLException;

	public int makeLive(String timebound_id);

	public List<DissertationResultBean> getPassFailStaging(String timebound_id);

	public int updateMarksProccessed(List<DissertationResultBean> finalUpsertListForStaging);

	public MBAWXPassFailStatus mapPassFailBean(String sapid, String timeboundId);

	public MBAWXExamResultForSubject getMastersDissertationResult(String sapid,
			StudentSubjectConfigExamBean timeboundSubject, MBAWXPassFailStatus passFailStatus, String hasIA,
			String hasTEE);
	
	public List<EmbaGradePointBean>  getAllGrades();

	public List<DissertationResultBean> applyGrade(List<DissertationResultBean> passFailStaging, String loggedInUser,
			List<EmbaGradePointBean> gradeList);

	public int upsertGrade(List<DissertationResultBean> upsertList);

	

}
