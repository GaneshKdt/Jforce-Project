package com.nmims.services.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.DissertationQ8ResultDaoImpl;
import com.nmims.daos.DissertationResultProcessingDaoInterface;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;

@Service
public class DissertationGradeSheet_TranscriptServiceImpl  implements DissertationGradesheet_TranscriptService{

	@Autowired
	DissertationResultProcessingDaoInterface dissertationQ7Dao;
	
	@Autowired
	DissertationQ8ResultDaoImpl dissertationQ8Dao;
	
	private final static int SUBJECT_ID_FOR_Q7= 1990;
	private final static int SUBJECT_ID_FOR_Q8 = 1991;
	
	private final static int MASTER_DISSERTATION_MASTER_KEY = 131;
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	
	private final static int SEM = 8;
	
	@Override
	public EmbaPassFailBean getPassFailForQ7(String sapid) {
	
		EmbaPassFailBean marksBean =  null;
		try {
		DissertationResultBean passFailBean =  dissertationQ7Dao.getPassFail(sapid);
		

		if(null!= passFailBean) {
			
			List<DissertationResultProcessingDTO> timebound = dissertationQ8Dao.getTimeBound(SUBJECT_ID_FOR_Q7);
			DissertationResultProcessingDTO subject = dissertationQ7Dao.getSubjectName(SUBJECT_ID_FOR_Q7);
			List<DissertationResultProcessingDTO> timeBoundUserMap = dissertationQ8Dao.getTimbound(sapid);
			
			DissertationResultProcessingDTO examDetails = timebound.stream()
					.filter(id -> timeBoundUserMap.stream().anyMatch(timebound1 -> id.getId() == timebound1.getId()))
					.findFirst().get();
			
			if(null!=examDetails) {
				marksBean =  new EmbaPassFailBean();
				marksBean.setSapid(String.valueOf(passFailBean.getSapid()));
				marksBean.setTimeboundId(String.valueOf(passFailBean.getTimeBoundId()));
				marksBean.setGrade(passFailBean.getGrade());
				marksBean.setPoints(String.valueOf(passFailBean.getGradePoints()));
				marksBean.setPrgm_sem_subj_id(passFailBean.getPrgm_sem_subj_id());
				marksBean.setExamMonth(examDetails.getExamMonth());
				marksBean.setExamYear(examDetails.getExamYear());
				marksBean.setMonth(examDetails.getAcadMonth());
				marksBean.setYear(examDetails.getAcadYear());
				marksBean.setIsPass(passFailBean.getIsPass());
				marksBean.setSubject(subject.getSubject());
				marksBean.setPsssem(String.valueOf(subject.getSem()));
				marksBean.setIsResultLive(passFailBean.getIsResultLive());
			}	
		}
		}catch(Exception e) {
			e.printStackTrace();
			dissertationLogs.info("Error Found while getting passfail for Q7 : "+" "+e);
		}
		return marksBean;
	}

	@Override
	public int checkSapidExistForQ7(String sapid) {
		return dissertationQ7Dao.checkSapidExist(sapid);
		
	}

	@Override
	public EmbaPassFailBean getPassFailForQ8(String sapid) {
		EmbaPassFailBean marksBean = null;
		try {
		DissertationResultBean passFailBean =  dissertationQ8Dao.getPassFail(sapid);
		
		if(null !=passFailBean) {
			
			List<DissertationResultProcessingDTO> timebound = dissertationQ8Dao.getTimeBound(SUBJECT_ID_FOR_Q8);
			List<DissertationResultProcessingDTO> timeBoundUserMap = dissertationQ8Dao.getTimbound(sapid);
			DissertationResultProcessingDTO subject = dissertationQ7Dao.getSubjectName(SUBJECT_ID_FOR_Q8);
			
			DissertationResultProcessingDTO examDetails = timebound.stream()
					.filter(id -> timeBoundUserMap.stream().anyMatch(timebound1 -> id.getId() == timebound1.getId()))
					.findFirst().get();
			
			if(null!=examDetails) {
				 marksBean =  new EmbaPassFailBean();
				marksBean.setSapid(String.valueOf(passFailBean.getSapid()));
				marksBean.setTimeboundId(String.valueOf(passFailBean.getTimeBoundId()));
				marksBean.setGrade(passFailBean.getGrade());
				marksBean.setPoints(String.valueOf(passFailBean.getGradePoints()));
				marksBean.setPrgm_sem_subj_id(passFailBean.getPrgm_sem_subj_id());
				marksBean.setExamMonth(examDetails.getExamMonth());
				marksBean.setExamYear(examDetails.getExamYear());
				marksBean.setMonth(examDetails.getAcadMonth());
				marksBean.setYear(examDetails.getAcadYear());
				marksBean.setIsPass(passFailBean.getIsPass());
				marksBean.setSubject(subject.getSubject());
				marksBean.setPsssem(String.valueOf(subject.getSem()));
				marksBean.setSem(String.valueOf(SEM));
				marksBean.setIsResultLive(passFailBean.getIsResultLive());
			}
		}
		
		}catch(Exception e) {
			dissertationLogs.info("Error Found while getting passfail for Q8 : "+" "+e);
		}
		return marksBean;
	}

	@Override
	public int checkSapidExistForQ8(String sapid) {
		return dissertationQ8Dao.checkSapidExist(sapid);
	}

	@Override
	public StudentSubjectConfigExamBean getPassFailForQ8Timebound(String sapid) {
		StudentSubjectConfigExamBean subject =  null;
	try {
		DissertationResultBean passFailBean =dissertationQ8Dao.getPassFail(sapid);
		if(null!=passFailBean) {
			
			DissertationResultProcessingDTO registration = dissertationQ8Dao.getRegistration(sapid);
			List<DissertationResultProcessingDTO> timebound = dissertationQ8Dao.getTimeBound(SUBJECT_ID_FOR_Q8);
			List<DissertationResultProcessingDTO> timeBoundUserMap = dissertationQ8Dao.getTimbound(sapid);

			DissertationResultProcessingDTO examDetails = timebound.stream()
					.filter(id -> timeBoundUserMap.stream().anyMatch(timebound1 -> id.getId() == timebound1.getId()))
					.findFirst().get();

		if(null!=examDetails) {
			subject = new StudentSubjectConfigExamBean();
			subject.setExamMonth(examDetails.getExamMonth());
			subject.setExamYear(examDetails.getExamYear());
			subject.setAcadMonth(registration.getAcadMonth());
			subject.setAcadYear(registration.getAcadYear());
			subject.setSem(String.valueOf(SEM));
		}
		}
	} catch (Exception e) {
		dissertationLogs.info("Error Found while checking sapid in passfail for Q8 : "+" "+e);
	}
		return subject;
	}

	@Override
	public MBAPassFailBean getPassFailForQ7Transcript(String sapid) {
		MBAPassFailBean marksBean =  null;
		try {
		DissertationResultBean passFailBean =  dissertationQ7Dao.getPassFail(sapid);
	
		if(null!= passFailBean) {
			
			List<DissertationResultProcessingDTO> timebound = dissertationQ8Dao.getTimeBound(SUBJECT_ID_FOR_Q7);
			List<DissertationResultProcessingDTO> timeBoundUserMap = dissertationQ8Dao.getTimbound(sapid);
			DissertationResultProcessingDTO subject = dissertationQ7Dao.getSubjectName(SUBJECT_ID_FOR_Q7);
			
			DissertationResultProcessingDTO examDetails = timebound.stream()
					.filter(id -> timeBoundUserMap.stream().anyMatch(timebound1 -> id.getId() == timebound1.getId()))
					.findFirst().get();
			
			if(null!=examDetails) {
				marksBean =  new MBAPassFailBean();
				marksBean.setSapid(String.valueOf(passFailBean.getSapid()));
				marksBean.setTimeboundId(String.valueOf(passFailBean.getTimeBoundId()));
				marksBean.setGrade(passFailBean.getGrade());
				marksBean.setPoints(String.valueOf(passFailBean.getGradePoints()));
				marksBean.setPrgm_sem_subj_id(String.valueOf(passFailBean.getPrgm_sem_subj_id()));
				marksBean.setExamMonth(examDetails.getExamMonth());
				marksBean.setExamYear(examDetails.getExamYear());
				marksBean.setAcadMonth(examDetails.getAcadMonth());
				marksBean.setAcadYear(examDetails.getAcadYear());
				marksBean.setIsPass(passFailBean.getIsPass());
				marksBean.setSubject(subject.getSubject());
				
			
			}
		}
		}catch(Exception e) {
			dissertationLogs.info("Error Found while transcript for Q7 : "+" "+e);
		}
		return marksBean;
	}

	@Override
	public MBAPassFailBean getPassFailForQ8Transcript(String sapid) {
		MBAPassFailBean marksBean =  null;
		try {
			DissertationResultBean passFailBean =dissertationQ8Dao.getPassFail(sapid);
			if(null!=passFailBean) {
				
				//DissertationResultProcessingDTO registration = dissertationQ8Dao.getRegistration(sapid);
				List<DissertationResultProcessingDTO> timebound = dissertationQ8Dao.getTimeBound(SUBJECT_ID_FOR_Q8);
				List<DissertationResultProcessingDTO> timeBoundUserMap = dissertationQ8Dao.getTimbound(sapid);
				DissertationResultProcessingDTO subject = dissertationQ7Dao.getSubjectName(SUBJECT_ID_FOR_Q8);
				DissertationResultProcessingDTO examDetails = timebound.stream()
						.filter(id -> timeBoundUserMap.stream().anyMatch(timebound1 -> id.getId() == timebound1.getId()))
						.findFirst().get();

			if(null!=examDetails) {
				marksBean = new MBAPassFailBean();
				marksBean.setSapid(String.valueOf(passFailBean.getSapid()));
				marksBean.setTimeboundId(String.valueOf(passFailBean.getTimeBoundId()));
				marksBean.setGrade(passFailBean.getGrade());
				marksBean.setPoints(String.valueOf(passFailBean.getGradePoints()));
				marksBean.setPrgm_sem_subj_id(String.valueOf(passFailBean.getPrgm_sem_subj_id()));
				marksBean.setExamMonth(examDetails.getExamMonth());
				marksBean.setExamYear(examDetails.getExamYear());
				marksBean.setAcadMonth(examDetails.getAcadMonth());
				marksBean.setAcadYear(examDetails.getAcadYear());
				marksBean.setIsPass(passFailBean.getIsPass());
				marksBean.setSubject(subject.getSubject());
			}
			}
		} catch (Exception e) {
			dissertationLogs.info("Error Found while transcript for Q8 : "+" "+e);
		}
			return marksBean;
		}

	@Override
	public List<Integer> getTimeboundUser(String sapid) {
		
		return dissertationQ7Dao.getTimeBoundUser(sapid);
	}

	@Override
	public DissertationResultBean getPassFail(String sapid) {
		return dissertationQ7Dao.getPassFail(sapid);
	}

	@Override
	public List<DissertationResultProcessingDTO> getTimeBounds(String commaSepratedTimeBoundIds) {
		return dissertationQ7Dao.getTimeBounds(commaSepratedTimeBoundIds);
	}

	@Override
	public DissertationResultProcessingDTO getSubjectDetails(int iaMasterDissertationSubjectId) {
		return dissertationQ7Dao.getProgram(String.valueOf(iaMasterDissertationSubjectId));
	}

	@Override
	public EmbaPassFailBean filterSubjectToClear(DissertationResultBean passFail,
			List<DissertationResultProcessingDTO> timeBound, DissertationResultProcessingDTO subjects) {
		EmbaPassFailBean bean =  new EmbaPassFailBean();
		if(passFail.getPrgm_sem_subj_id() == subjects.getId() && MASTER_DISSERTATION_MASTER_KEY == subjects.getConsumerProgramStructureId()) {
		
			DissertationResultProcessingDTO id= timeBound.stream().filter(timebound -> passFail.getTimeBoundId() == timebound.getId())
					.findFirst().get();

				
				bean.setId(String.valueOf(id.getId()));
				bean.setExamMonth(id.getExamMonth());
				bean.setExamYear(id.getExamYear());					
	
		}

		 return bean;
	}

	@Override
	public boolean hasAppearedForQ8(String sapid) {
		int count = dissertationQ8Dao.checkSapidExist(sapid);
		if(count == 1) {
			return true;
		}
		return false;
	}

	@Override
	public DissertationResultBean getPassFailQ8(String sapid) {
		// TODO Auto-generated method stub
		return dissertationQ8Dao.getPassFail(sapid);
	}
}
