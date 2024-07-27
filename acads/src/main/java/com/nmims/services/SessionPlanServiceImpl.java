package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionPlanBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ISessionPlanDAO;
import com.nmims.services.SessionPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Service("sessionPlanService")
public class SessionPlanServiceImpl implements SessionPlanService
{
	/*Variables*/ 
	@Autowired
	private ISessionPlanDAO sessionPlanDAO;
	
	@Autowired
	private FacultyDAO facultyDao;
	
	@Autowired
	private ContentDAO contentDao;
	
	private static final Logger logger = LoggerFactory.getLogger(SessionPlanService.class);
	private final Map<String, ProgramBean> mapOfProgramStructure = new HashMap<>();
	
	public Map<String, ProgramBean> getProgramDetailsByCPSID_fromCache(){
		if(this.mapOfProgramStructure == null || this.mapOfProgramStructure.size() == 0) {
			List<ProgramBean> sessionBeanList = contentDao.getProgramStructureDetails();
			
			sessionBeanList.stream().map((element) -> {
				mapOfProgramStructure.put(element.getId(), element);
				return element;
			}).collect(Collectors.toList());
		}
		return mapOfProgramStructure;
	}
	
	/*Methods*/
	@Override
	public String saveSessionPlan(SessionPlanBean saveBean)
	{
		String saveSessionPlanMessage;
		try
		{
			//Save Session Plan
			saveSessionPlanMessage = sessionPlanDAO.saveSessionPlan(saveBean);
		}
		catch(Exception e)
		{
			saveSessionPlanMessage = null;
		}
		return saveSessionPlanMessage;
	}
	
	@Override
	public String updateSessionPlan(SessionPlanBean updateBean)
	{
		String updated;
		try
		{
			//Update Session Plan
			updated = sessionPlanDAO.updateSessionPlan(updateBean);
		}
		catch(Exception e)
		{
			updated = null;
		}
		return updated;
	}
	
	@Override
	public String isSessionPlanAlreadyMapped(SessionPlanBean formBean)
	{
		String isAlreadyMapped;
		
		try
		{
			//To get pss_Id of subject
			ArrayList<String> pssIds = getProgramSemSubjectIdsBySubjectNProgramConfig(formBean.getProgramId(), formBean.getProgramStructureId(),
					formBean.getConsumerTypeId(), formBean.getSubject());
			//To get Timebound Id by pss_Id and batch_Id
			List<Long> timeboundIds = getTimeboundIdsByProgramSemSubjectIds(pssIds, formBean.getReferenceId(), formBean.getStartDate());
			//To count number of session plan mapped with this timebound id
			int count = getCountOfMappedSessionplan(timeboundIds.get(0));
			
			//To Check if count is greater then 0 then marked Yes
			if(count > 0)
			{
				isAlreadyMapped = "Yes";
			}
			//If not then No
			else
			{
				isAlreadyMapped = "No";
			}
		}
		catch(Exception e)
		{
			//Exception Occurred then null
			isAlreadyMapped = null;
		}
		return isAlreadyMapped;
	}
	
	//To Get Pss_Id Of Subject
	public ArrayList<String> getProgramSemSubjectIdsBySubjectNProgramConfig(String programId, String programStructureId,
	String consumerTypeId, String subject)throws Exception
	{
		ArrayList<String> pssIds = sessionPlanDAO.getProgramSemSubjectIdsBySubjectNProgramConfig(programId, programStructureId, consumerTypeId, subject);
		return pssIds;
	}
	
	//To Get Timebound Id By Pss_Id And Batch_Id
	public List<Long> getTimeboundIdsByProgramSemSubjectIds(ArrayList<String> programSemSubjectIds, String referenceId, String startTime)throws Exception
	{
		List<Long> timeboundIds = sessionPlanDAO.getTimeboundIdsByProgramSemSubjectIds(programSemSubjectIds, referenceId, startTime);
		return timeboundIds;
	}
	//To Get Number Of Session Plan Mapped With The Timebound Id
	public int getCountOfMappedSessionplan(Long timeboundId) throws Exception
	{
		int count = sessionPlanDAO.isTimeBoundAlreadyMapped(timeboundId);
		return count;
	}

	@Override
	public Map<String, String> facultyNameMappedWithFacultyId() {
		logger.info("Entered facultyNameMappedWithFacultyId() method of SessionPlanService");
		Map<String, String> facultyNameMappedWithFacultyId = facultyDao.getFacultyMap();
		logger.info("Entered facultyNameMappedWithFacultyId() method of SessionPlanService");
		return facultyNameMappedWithFacultyId;
	}

	@Override
	public ConsumerProgramStructureAcads getSubjectCodeMap(String programSemSubjectId) {
		logger.info("Entered facultyNameMappedWithFacultyId() method of SessionPlanService");
		ConsumerProgramStructureAcads facultyNameMappedWithFacultyId = sessionPlanDAO.getSubjectCode(programSemSubjectId);
		logger.info("Entered facultyNameMappedWithFacultyId() method of SessionPlanService");
		return facultyNameMappedWithFacultyId;
	}

	@Override
	public SessionBean getSessionDetailsBySessionId(Long sessionId) {
		logger.info("Entered getSessionDetailsBySessionId() method of SessionPlanService");
		
		SessionBean sessionObject = new SessionBean();
		
		if(sessionId != 0) {
			sessionObject = contentDao.getSessionDetailsBySessionId(sessionId);
		}
		
		logger.info("Exited getSessionDetailsBySessionId() method of SessionPlanService");
		return sessionObject;
	}

	@Override
	public SessionBean getBatchNameFromBatchId(Long batchId) {
		logger.info("Entered getBatchNameFromBatchId() method of SessionPlanService");
		SessionBean sessionBean = contentDao.getBatchNameFromBatchId(batchId);
		logger.info("Exited getBatchNameFromBatchId() method of SessionPlanService");
		return sessionBean;
	}

	@Override
	public Map<String, ProgramBean> getProgramStructureDetails() {
		logger.info("Entered getProgramStructureDetails() method of SessionPlanService");
		Map<String, ProgramBean> mapOfProgramStructure = getProgramDetailsByCPSID_fromCache();
		logger.info("Exited getProgramStructureDetails() method of SessionPlanService");
		return mapOfProgramStructure;
	}
}
