package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.IUpsertTimeboundStudentProjectPassFailStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxStudentUpsertProjectPassFailStrategy")
public class MBAWXStudentUpsertProjectPassFailStrategy implements IUpsertTimeboundStudentProjectPassFailStrategy {
	
	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Override
	public List<String> upsertTimeboundStudentProjectPassFail(List<EmbaPassFailBean> studentPassFailList) {
		timeboundProjectMarksUploadLogger.info("MBAWXStudentUpsertProjectPassFailStrategy.upsertTimeboundStudentProjectPassFail() - START");
		List<String> errorList = new ArrayList<>();
		
		//Iterate pass-fail result and update/insert pass-fail status.
		studentPassFailList.forEach((passFailBean)->{
			try{
				//Fetch student pass-fail record based on the sapId and PSSId(program_sem_subject_id)
				Optional<EmbaPassFailBean> passFailRecord = this.getTimeboundStudentProjectPassFail(passFailBean.getSapid(), passFailBean.getPrgm_sem_subj_id());
				
				//If the record is already present in the pass-fail then go for updating existing one else insert new record in pass-fail.
				if(passFailRecord.isPresent()){
					//Updating existing pass-fail record.
					updateProjectPassFail(passFailBean);
				}else{
					//Inserting new pass-fail record.
					insertProjectPassFail(passFailBean);
				}
			}catch(Exception e){
				timeboundProjectMarksUploadLogger.error("Failed to upsert Error for '"+passFailBean.getSapid()+"', Error:"+e.getStackTrace());
				//If any exception occurs then add exception message along with sapId in errorList.
				errorList.add("Upsert Error for '"+passFailBean.getSapid()+"', Message:"+e.getMessage());
			}
		});//forEach loop
		timeboundProjectMarksUploadLogger.info("MBAWXStudentUpsertProjectPassFailStrategy.upsertTimeboundStudentProjectPassFail() - END");
		//return error list occurred while updating/inserting the pass-fail records.
		return errorList;
	}//upsertTimeboundStudentProjectPassFail(-)

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private int updateProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception {
		//Update existing pass-fail record of a student.
		int passFailUpdatedCount = this.updateTimeboundStudentProjectPassFail(studentPassFailBean);
		//Update time-bound project marks table as pass-fail processed. 
		int marksUpdatedCount = this.updateTimeboundStudentProjectMarks(studentPassFailBean);
		//return pass-fail and project marks table updated count together
		return passFailUpdatedCount+marksUpdatedCount;
	}//updateProjectPassFail(-)
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private int insertProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception {
		//Insert new pass-fail record.
		int passFailInsertedCount = this.insertTimeboundStudentProjectPassFail(studentPassFailBean);
		//Update time-bound project marks table as pass-fail processed. 
		int marksUpdatedCount = this.updateTimeboundStudentProjectMarks(studentPassFailBean);
		//return pass-fail inserted and project marks table updated count together
		return passFailInsertedCount+marksUpdatedCount;
	}//insertProjectPassFail(-)
	
	@Override
	public int updateTimeboundStudentProjectMarks(EmbaPassFailBean studentPassFailBean) throws Exception {
		//Update time-bound project marks table as pass-fail processed and return updated count. 
		return examsAssessmentsDAO.updateProcessedTimeboundProjectStatus(studentPassFailBean);
	}//updateTimeboundStudentProjectMarks(-)

	@Override
	public int updateTimeboundStudentProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception {
		//Update existing pass-fail record of a student and return updated count.
		return examsAssessmentsDAO.updateEmbaPassFailStatus(studentPassFailBean);
	}//updateTimeboundStudentProjectPassFail(-)

	@Override
	public int insertTimeboundStudentProjectPassFail(EmbaPassFailBean studentPassFailBean) throws Exception {
		//Insert new pass-fail record and return updated count.
		return examsAssessmentsDAO.insertEmbaPassFailStatus(studentPassFailBean);
	}//insertTimeboundStudentProjectPassFail(-)

	@Override
	public Optional<EmbaPassFailBean> getTimeboundStudentProjectPassFail(String sapId, Integer pssId) {
		//Get student pass-fail record based on the sapId and PSSId(program_sem_subject_id)
		return examsAssessmentsDAO.getPassFailRecord(sapId, pssId);
	}//getTimeboundStudentProjectPassFail(-,-)

}
