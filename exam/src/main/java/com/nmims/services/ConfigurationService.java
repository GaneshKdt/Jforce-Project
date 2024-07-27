package com.nmims.services;


import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.daos.DashboardDAO;
import com.nmims.interfaces.ConfigurationServiceInterface;

@Service
public class ConfigurationService implements ConfigurationServiceInterface {
	
	@Autowired
	DashboardDAO dashboardDao;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
	
	
	@Override
	public ConsumerProgramStructureExam updateMasterKeyDetails(ConsumerProgramStructureExam bean) {
		if (bean.getHasPaidSessionApplicable().equals("Y")) {
			bean.setHasPaidSessionApplicable("1");
		} else if (bean.getHasPaidSessionApplicable().equals("N")) {
			bean.setHasPaidSessionApplicable("0");
		}
		if (dashboardDao.getConsumerProgramStructure(bean) == 0) {
			String result = dashboardDao.updateConsumerProgramStructureMappingList(bean);
			if (result.equalsIgnoreCase("true")) {
				bean.setStatus("Success");
				bean.setMessage("Successfully data updated");
			} else {
				bean.setStatus("Error");
				bean.setMessage(result);
			}
		} else {
			dashboardDao.updateHasLiveSessionAccessFlag(bean.getHasPaidSessionApplicable(), bean.getId());
			bean.setStatus("Success");
			bean.setMessage("ConsumerProgramStructureId already exist. Only liveSessionAccessFlag updated!");

		}
		return bean;
	}
	
	@Override
	public List<TimeBoundUserMapping> downloadTimeBoundExcelService(String timeBoundSubjectConfigId,
			String prgm_sem_subj_id, String batchId) throws Exception {
		List<TimeBoundUserMapping> studentList = dashboardDao
				.getExistingStudentsByTimeboundId(timeBoundSubjectConfigId);
		String subjectName = dashboardDao.getSubjectNameByprgm_sem_subj_id(Integer.parseInt(prgm_sem_subj_id));
		StudentSubjectConfigExamBean batchDetailsByBatchId = dashboardDao.getBatchDetailsByBatchId(batchId);
		studentList.forEach(c -> {
			c.setAcadMonth(batchDetailsByBatchId.getAcadMonth());
			c.setAcadYear(batchDetailsByBatchId.getAcadYear());
			c.setExamMonth(batchDetailsByBatchId.getExamMonth());
			c.setExamYear(batchDetailsByBatchId.getExamYear());
			c.setBatchName(batchDetailsByBatchId.getBatchName());
			c.setSubject(subjectName);
		});
		
		return studentList;
	}

}
