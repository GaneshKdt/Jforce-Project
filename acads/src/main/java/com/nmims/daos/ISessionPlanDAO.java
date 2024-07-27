package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.SessionPlanBean;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface ISessionPlanDAO 
{
	//To Add The Session Plan Details
	public String saveSessionPlan(final SessionPlanBean bean)throws Exception;
	//To Update The Session Plan Details
	public String updateSessionPlan(SessionPlanBean formBean)throws Exception;
	//To Get Number Of Session Plan Mapped With this Timebound Id
	public int isTimeBoundAlreadyMapped(Long timeboundId)throws Exception;
	public ArrayList<String> getProgramSemSubjectIdsBySubjectNProgramConfig(String programId, String programStructureId, String consumerTypeId, String subject);
	public List<Long> getTimeboundIdsByProgramSemSubjectIds(ArrayList<String> programSemSubjectIds, String referenceId, String startTime);
	public ConsumerProgramStructureAcads getSubjectCode(String programSemSubjectId);
}
