package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectLiveConfigurationBean;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectModuleLiveSettings;
import com.nmims.daos.ProjectStudentEligibilityDAO;
import com.nmims.stratergies.ProjectStudentEligibilityStrategyInterface;

@Service("pgProjectStudentEligibilityStrategy")
public class PGProjectStudentEligibilityStrategy implements ProjectStudentEligibilityStrategyInterface {

	@Autowired
	ProjectStudentEligibilityDAO dao;
	
	@Override
	public ProjectLiveConfigurationBean getProjectConfigurationForStudent(String sapid, String subject) throws Exception {
		ProjectLiveConfigurationBean liveConfiguration = new ProjectLiveConfigurationBean();
		
		String programSemSubjId = getProgramSemSubjectId(sapid, subject);
		String year = "";
		String month = "";
		ProjectConfiguration configuration = getProjectConfiguration(programSemSubjId);
		List<ProjectModuleExtensionBean> extendedModules = getExtendedListForStudent(sapid, programSemSubjId);
		
		// Check for individual modules 
		List<ProjectModuleLiveSettings> modulesPresent = new ArrayList<ProjectModuleLiveSettings>();
		if("Y".equals(configuration.getHasSOP())) {
			ProjectModuleLiveSettings settings = new ProjectModuleLiveSettings();
			boolean isLive = checkIfSOPLive(programSemSubjId, year, month);
			settings.setLive(isLive);
		}
		if("Y".equals(configuration.getHasSOP())) {
			ProjectModuleLiveSettings settings = new ProjectModuleLiveSettings();
			boolean isLive = checkIfSOPLive(programSemSubjId, year, month);
			if(!isLive) {
				for (ProjectModuleExtensionBean extendedModule : extendedModules) {
					if("".equals(extendedModule.getModuleType())) {
						
					}
				}
			}
			settings.setLive(isLive);
		}
		return null;
	}

	@Override
	public ProjectConfiguration getProjectConfiguration(String programSemSubjId) throws Exception {
		return null;
	}

	@Override
	public List<ProjectModuleExtensionBean> getExtendedListForStudent(String sapid, String programSemSubjId) throws Exception {
		return null;
	}

	@Override
	public String getProgramSemSubjectId(String sapid, String subject) throws Exception {
		return null;
	}

	@Override
	public boolean checkIfTitleLive(String programSemSubjId, String year, String month) throws Exception {
		return dao.checkIfTitleLive(programSemSubjId, year, month);
	}

	@Override
	public boolean checkIfSOPLive(String programSemSubjId, String year, String month) throws Exception {
		return dao.checkIfSOPLive(programSemSubjId, year, month);
	}

	@Override
	public boolean checkIfProjectSubmissionLive(String programSemSubjId, String year, String month) throws Exception {
		return dao.checkIfProjectSubmissionLive(programSemSubjId, year, month);
	}

	@Override
	public boolean checkIfSynopsisLive(String programSemSubjId, String year, String month) throws Exception {
		return dao.checkIfSynopsisLive(programSemSubjId, year, month);
	}

	@Override
	public boolean checkIfVivaLive(String programSemSubjId, String year, String month) throws Exception {
		return dao.checkIfVivaLive(programSemSubjId, year, month);
	}
}
