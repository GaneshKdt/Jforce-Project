package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.nmims.beans.FailedSubjectCountCriteriaBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.services.FailedSubjectCountCriteriaService;

@Service("failedSubjectCountCriteria")
public class FailedSubjectCountCriteriaServiceImpl implements FailedSubjectCountCriteriaService {
	
	@Autowired
	DashboardDAO dashboardDao;
	
	@Override
	public void insertFailedSubjectCountCriteria(FailedSubjectCountCriteriaBean bean)
	{
		List<Integer> consumerProgramStructureId  = getConsumerProgramStructureIdList(bean.getProgramType());
		dashboardDao.insertFailedSubjectCountCriteria(consumerProgramStructureId,bean);
	}
	
	@Override
	public void updateFailedSubjectCountCriteria(FailedSubjectCountCriteriaBean bean)
	{
		dashboardDao.updateFailedSubjectCountCriteria(bean);
	}
	
	public List<Integer> getConsumerProgramStructureIdList(String programType){
		List<Integer> consumerProgramStructureId = new ArrayList<Integer>();
		switch (programType) {
		case "MBA - WX":
			consumerProgramStructureId.add(111);
			consumerProgramStructureId.add(151);
			consumerProgramStructureId.add(160); 
			return consumerProgramStructureId;
		case "M.Sc. (AI & ML Ops)":
			consumerProgramStructureId.add(131);
			return consumerProgramStructureId;
		case "M.Sc. (AI)":	
			consumerProgramStructureId.add(158);
			return consumerProgramStructureId;
		case "Modular PD-DM":
			consumerProgramStructureId.add(148);
			consumerProgramStructureId.add(144);
			consumerProgramStructureId.add(149);
			consumerProgramStructureId.add(142);
			consumerProgramStructureId.add(143);
			consumerProgramStructureId.add(147);
			consumerProgramStructureId.add(145);
			consumerProgramStructureId.add(146);
			return consumerProgramStructureId;
		default : 
			return null;
		}
	}
}
