package com.nmims.interfaces;

import java.sql.SQLException;
import java.util.List;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;


public interface DissertationResultProcessingService {
	
	public List<Integer> getConsumerAndSubjectId();

	public List<BatchExamBean>  getBatchList(List<Integer> consumerProgramStructureId);
	

	//IaScore to marks
	public List<TEEResultBean> getEligibleIAStudents(String timeBoundId, List<DissertationResultBean> errorList);

	public List<DissertationResultBean> getDissertationQ7Scores(String timeboundId, List<TEEResultBean> eligibleIAStudents, List<DissertationResultBean> errorList, String loggedInUser);

	public int upsertMarks(List<DissertationResultBean> insertMarksData);
	//
	
	//search marks for UI
	public List<DissertationResultBean> getNotProcessedList(String timeBoundId);
	//
	
	
	//Marks to Staging
	public List<DissertationResultBean> processUpsertList(List<DissertationResultBean> upsertList, String loggedInUser);
	
	public int upsertPassFailStaging(List<DissertationResultBean> upsertList);
	
	public int updateUpsertList(List<DissertationResultBean> transferListForStaging);
	//
	
	//Staging to Passfail
	public List<DissertationResultBean> getStagedDissertationList(String timebound_id);

	public int transferToPassFail(List<DissertationResultBean> transferList, String timeBoundId) throws SQLException;
	//
	
	//Make Live
	public int makeResultLive(String timebound_id);

	


	
	//Student Side
	public MBAWXPassFailStatus mapPassFailBean(String sapid, String timeboundId);

	public MBAWXExamResultForSubject getMastersDissertationResult(String sapid,
			StudentSubjectConfigExamBean timeboundSubject, MBAWXPassFailStatus passFailStatus, String hasIA,
			String hasTEE);
	//

	
	public List<DissertationResultBean> applyGradeForQ7(List<DissertationResultBean> passFailStaging, String loggedInUser, List<EmbaGradePointBean> grades);

	public int upsertGradeInPassFailStaging(List<DissertationResultBean> processListForGrade);

	public List<EmbaGradePointBean> getAllGrades();

	



}
