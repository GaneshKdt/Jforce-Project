package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.services.ITimeboundProjectPassFailService;
import com.nmims.stratergies.IUpsertTimeboundStudentProjectMarksStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxUpsertTimeboundStudentProjectMarksStrategy")
public class MBAWXUpsertTimeboundStudentProjectMarksStrategy implements IUpsertTimeboundStudentProjectMarksStrategy {

	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");
	
	@Autowired
	@Qualifier("mbawxProjectPassFailService")
	private ITimeboundProjectPassFailService mbawxProjectPassFailService ;
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Override
	public List<String> upsertTimeboundStudentProjectMarks(List<TEEResultBean> studentMarksList) {
		timeboundProjectMarksUploadLogger.info("MBAWXUpsertTimeboundStudentProjectMarksStrategy.upsertTimeboundStudentProjectMarks() - START");
		List<String> errorList = new ArrayList<>();
		//Iterate student project marks list and insert or update project marks.
		for(TEEResultBean resultBean:studentMarksList) {
			//Get program sem subject id based on the sapId and timeboundId.
			Optional<Integer> PSSId = mbawxProjectPassFailService.getTimeboundSubjectPSSId(resultBean.getSapid(),resultBean.getTimebound_id());
			
			if(!PSSId.isPresent()) {
				//Add error message with sapId in error list.
				errorList.add(resultBean.getSapid()+" Error: PSSId not found.");
				continue;
			}	
			//Set pssId to bean.
			resultBean.setPrgm_sem_subj_id(PSSId.get());

			try{
				//Get single student project marks based on the sapId and program sem subject id.
				Optional<TEEResultBean> projectMarks = this.getTimeboundStudentProjectMarks(resultBean.getSapid(), resultBean.getPrgm_sem_subj_id());
				//If student project record already exist then update that.
				if(projectMarks.isPresent()) {
					//Set previously inserted scores to bean for maintaining track.
					resultBean.setSimulation_previous_score(projectMarks.get().getSimulation_score());
					resultBean.setCompXM_previous_score(projectMarks.get().getCompXM_score());
					//Update project existing record based on the sapId and pssId
					examsAssessmentsDAO.updateTimeboundProjectMarks(resultBean);
				}
				else {
					//Insert project marks record.
					examsAssessmentsDAO.insertTimeboundProjectMarks(resultBean);
				}
			}catch(Exception e){
				timeboundProjectMarksUploadLogger.error("Failed to upsert project marks for '"+resultBean.getSapid()+"' Error:"+e.getStackTrace());
				//Add error message with sapId in error list.
				errorList.add(resultBean.getSapid()+" Error:"+e.getMessage());
			}
		}//for loop
		timeboundProjectMarksUploadLogger.info("MBAWXUpsertTimeboundStudentProjectMarksStrategy.upsertTimeboundStudentProjectMarks() - END");
		//return error list.
		return errorList;
	}

	
	@Override
	public Optional<TEEResultBean> getTimeboundStudentProjectMarks(String sapId, int pssId) {
		return examsAssessmentsDAO.getTimeboundStudentProjectMarks(sapId, pssId);
	}

}
