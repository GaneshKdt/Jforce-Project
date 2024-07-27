package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectLiveConfigurationBean;
import com.nmims.beans.ProjectModuleExtensionBean;

public interface ProjectStudentEligibilityStrategyInterface {

	public String getProgramSemSubjectId(String sapid, String subject) throws Exception;
	ProjectConfiguration getProjectConfiguration(String programSemSubjId) throws Exception;

	public boolean checkIfTitleLive(String programSemSubjId, String year, String month) throws Exception;
	public boolean checkIfSOPLive(String programSemSubjId, String year, String month) throws Exception;
	public boolean checkIfProjectSubmissionLive(String programSemSubjId, String year, String month) throws Exception;
	public boolean checkIfSynopsisLive(String programSemSubjId, String year, String month) throws Exception;
	public boolean checkIfVivaLive(String programSemSubjId, String year, String month) throws Exception;
	
	public ProjectLiveConfigurationBean getProjectConfigurationForStudent(String sapid, String subject) throws Exception;
	
	public List<ProjectModuleExtensionBean> getExtendedListForStudent(String sapid, String programSemSubjId) throws Exception;

}
