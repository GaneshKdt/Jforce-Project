package com.nmims.services;

import java.util.List;
import java.util.Map;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionPlanBean;
import com.nmims.beans.SessionPlanModuleBean;


/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface SessionPlanService 
{
	//To Add Session Plan Details
	public String saveSessionPlan(SessionPlanBean saveBean);
	//To Update Session Plan Details 
	public String updateSessionPlan(SessionPlanBean updateBean);
	//To Check If Any Session Plan Already Mapped With This Timebound Id Or Not
	public String isSessionPlanAlreadyMapped(SessionPlanBean formBean);
	
	public Map<String, String> facultyNameMappedWithFacultyId();
	
	public ConsumerProgramStructureAcads getSubjectCodeMap(String programSemSubjectId);
	
	public SessionBean getSessionDetailsBySessionId(Long sessionId);
	
	public SessionBean getBatchNameFromBatchId(Long batchId);
	
	public Map<String, ProgramBean> getProgramStructureDetails();
}