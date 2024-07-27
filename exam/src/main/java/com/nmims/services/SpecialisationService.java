package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.Specialisation;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;

@Component
public class SpecialisationService {

	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	SpecialisationDAO specialisationDAO;
	
	@Autowired
	SalesforceHelper salesforceHelper;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	private String CURRENT_MBAWX_ACAD_YEAR = "2023";

	private String CURRENT_MBAWX_ACAD_MONTH = "Apr";
	
	@Value("${NEXT_MBAWX_ACAD_YEAR}")
	private String NEXT_MBAWX_ACAD_YEAR;

	@Value("${NEXT_MBAWX_ACAD_MONTH}")
	private String NEXT_MBAWX_ACAD_MONTH;

	private static final Logger logger = LoggerFactory.getLogger("electiveSelection");
	
	public ArrayList<Specialisation> isSpecialisationDone(String sapid, int maxTerm) {
		//Added +1 in current Term to check specialization has been done or not for next Term.
		maxTerm = maxTerm + 1;
		return specialisationDAO.isSpecialisationDone(sapid, maxTerm, NEXT_MBAWX_ACAD_YEAR, NEXT_MBAWX_ACAD_MONTH);
	}
	
	public ArrayList<Specialisation> getSpecialisationDoneSubjects(String sapid, int maxTerm) {
		ArrayList<Specialisation> previousElectiveDetails = new ArrayList<Specialisation>();
		previousElectiveDetails = specialisationDAO.getPreviousElectiveDetails(sapid, maxTerm, CURRENT_MBAWX_ACAD_YEAR, CURRENT_MBAWX_ACAD_MONTH);
		//If Term Repeat student
		if (previousElectiveDetails.size() < 5) {
			previousElectiveDetails = specialisationDAO.getRepeatStudentElectives(sapid);
		}
		
		return previousElectiveDetails;
	}
	
	public String getSpecilizationNameBasedOnAlphabeticalOrder(String sapid) throws Exception{
		String specialisationNameBasedOnNomenclature = "";
		Specialisation specialisation =  null;
		try {
			specialisation = specialisationDAO.getSpecializationIds(sapid);
		}
		catch(EmptyResultDataAccessException emptyEx)
		{
			return specialisationNameBasedOnNomenclature;
		}
		if(specialisation != null)
		{
			String specialisationIds = specialisation.getSpecialisation1();
			
			if(!StringUtils.isBlank(specialisation.getSpecialisation2()))
				specialisationIds = specialisationIds+", "+specialisation.getSpecialisation2();
			
			specialisationNameBasedOnNomenclature = specialisationDAO.getSpecializationBasedOnNomenclature(specialisationIds);
		}
		return specialisationNameBasedOnNomenclature;
	}

	public List<ProgramSubjectMappingExamBean> getSubjectForBatch(String batchId, String acadYear, String acadMonth, Integer sem) 
			throws Exception{
		
		List<ProgramSubjectMappingExamBean> specialisationSubjects = specialisationDAO.getSubjectForBatch(batchId, acadYear, acadMonth);
		List<String> specializationList = specialisationSubjects
			.stream()
			.map(subject -> subject.getSpecializationType())
			.distinct()
			.collect(Collectors.toList());
		
		specializationList.forEach(specialization -> {
			try {
				specialisationSubjects.addAll(specialisationDAO.getCommonSubject(acadYear, acadMonth, sem, specialization));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return specialisationSubjects;
		
	}

	public List<Specialisation> getSpecializationDetails(String year, String month, Integer sem) 
			throws Exception{
		
		List<Specialisation> specialisationSubjects = specialisationDAO.getSpecializationDetails(year, month, sem);
		return specialisationSubjects;
		
	}

	public Boolean checkIfSpecializationDetailsExists(String year, String month, Integer sem) 
			throws Exception{
		
		Boolean exists = specialisationDAO.checkIfSpecializationDetailsExists(year, month, sem);
		return exists;
		
	}

	public List<Specialisation> getBatchsForYearMonth(String acadYear, String acadMonth) 
			throws Exception{
		
		List<Specialisation> batches = specialisationDAO.getBatchsForYearMonth(acadYear, acadMonth);
		return batches;
		
	}
	
	public void saveSpecializationDetails(List<Specialisation> details) throws Exception{
		
		specialisationDAO.saveSpecializationDetails(details);
		
	}

	public void deleteSpecializationInstance(String year, String month, Integer sem, String timeBoundId, String specialization) 
			throws Exception{
		
		specialisationDAO.deleteSpecializationInstance(year, month, sem, timeBoundId, specialization);
		return;
		
	}

	public HashMap<Integer, List<Specialisation>> fetchBlockDetails() throws Exception{

		HashMap<Integer, List<Specialisation>> blockDetails = new HashMap<>();
		
		IntStream.rangeClosed(3, 5)
	        .forEach(sem ->{
	    		HashMap<String, String> yearMonthMap = fetchYearMonthBasedOnSem(sem);
	    		List<Specialisation> specialisationSubjects = new ArrayList<>();
				try {
					specialisationSubjects = specialisationDAO.getSpecializationDetails(yearMonthMap.get("year"), 
							yearMonthMap.get("month"), sem);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		blockDetails.put(sem, specialisationSubjects);
	        });
		
		return blockDetails;
		
	}

	public List<Specialisation> fetchBlockSequenceDetails(Integer sem) throws Exception{
		
		HashMap<String, String> yearMonthMap = fetchYearMonthBasedOnSem(sem);
		List<Specialisation> blockSequenceDetails = specialisationDAO.fetchBlockSequenceDetails(yearMonthMap.get("year"), 
				yearMonthMap.get("month"), sem);
		return blockSequenceDetails;
		
	}
	
	private HashMap<String, String> fetchYearMonthBasedOnSem(Integer sem){
		
		HashMap<String, String> yearMonthMap = new HashMap<>();
		
		switch(sem){
		case 3:
			yearMonthMap.put("year", "2023");
			yearMonthMap.put("month", "Jul");
			break;
		case 4:
			yearMonthMap.put("year", "2023");
			yearMonthMap.put("month", "Oct");
			break;
		case 5:
			yearMonthMap.put("year", "2024");
			yearMonthMap.put("month", "Jan");
			break;
		default:
			break;
		}
		
		return yearMonthMap;
	}

	public Specialisation fetchStudentDetailsForSpecialization(String sapid) throws Exception{
		
		Specialisation specialisation = specialisationDAO.fetchStudentDetailsForSpecialization(sapid);
		
		return specialisation;
		
	}

	public void saveSpecializationForStudent(List<Specialisation> specialization) throws Exception{

		Specialisation updatedDetails = specialization.get(0);
		Specialisation existingSpecilisation = new Specialisation();
		StudentExamBean student = new StudentExamBean();
		ServiceRequestBean serviceRequestBean = new ServiceRequestBean();
		
		List<Specialisation> optedinSpecialization = new ArrayList<>();

		MailSender mailer = (MailSender)act.getBean("mailer");
		
		String errorMessage = "";
		Long serviceRequestId = null;
		
		/*
		 * setting sapid as to avoid any conflicts in the existing methods for moving staging to history
		 * and fetching student data which will be needed during triggering the mail 
		 * */
		updatedDetails.setSapid(updatedDetails.getUserId());
		student = specialisationDAO.getSingleStudentsData(updatedDetails.getUserId());

		logger.info("attempting to save the following specialization for the student : "+updatedDetails.getUserId());
		logger.info("logging pattern : subject~sem~specialization");
		
		if(updatedDetails.getIsReSelect()) {
			/*
		     * insert existing specialization entry in history
			 * delete existing specialization entry
			 * save new selections in staging
			 * */
			logger.info("specialization status : reselect");
			optedinSpecialization = specialisationDAO.fetchOptedinSpecialization(updatedDetails.getUserId());
			specialisationDAO.batchInsertTimeBoundStudentMappingHistory(optedinSpecialization);
			specialisationDAO.deleteStudentsExistingSpecialization(updatedDetails.getUserId());
			specialisationDAO.batchInsertTimeBoundStudentMapping(specialization);
			mailer.sendSpecialisationSelectionSummary(student, specialization);
		}else if(updatedDetails.isServiceRequest()){
			logger.info("specialization status : Change in specialization service request");
			/*
			 * in case of change in specialization
			 * fetch the existing specialization details for the student
			 * fetch opted in electives for the student
			 * */
			existingSpecilisation = specialisationDAO.getExistingSpecialisationDetails(updatedDetails.getUserId());
			optedinSpecialization = specialisationDAO.fetchOptedinSpecialization(updatedDetails.getUserId());
			/*
			 * update new specialization details in sfdc
			 * should only happen on production environment
			 * */
			if("PROD".equalsIgnoreCase(ENVIRONMENT)){
				errorMessage = salesforceHelper.updateSpecilisationDetails(updatedDetails);
			}
			
			if(errorMessage == null || "".equals(errorMessage)){
				/* 
				 * insert existing specialization into history table
				 * and then update existing specialization
				 * */
				boolean isInserted = specialisationDAO.insertExistingSpecialisationIntoHistory(existingSpecilisation);
				boolean isUpdated =  specialisationDAO.updateSpecilisationDetails(updatedDetails);
				
				/*
				 * create a record for change in specialization 
				 * */
				serviceRequestId = specialisationDAO.insertStudentSpecialisationDetailsinSR(updatedDetails);
				if (!isInserted || !isUpdated || serviceRequestId.equals(null)) {
					throw new Exception("Error While Updating Extising Specilisation Details.");
				}

				/*
				 * insert existing specialization entry in history
				 * delete existing specialization entry
				 * save new selections in staging
				 * */
				specialisationDAO.batchInsertTimeBoundStudentMappingHistory(optedinSpecialization);
				specialisationDAO.deleteStudentsExistingSpecialization(updatedDetails.getUserId());
				specialisationDAO.batchInsertTimeBoundStudentMapping(specialization);

				/*
				 * send service request mail
				 * send elective selection mail
				 * */
				serviceRequestBean = specialisationDAO.getSingleSR(serviceRequestId);
				mailer.sendSREmail(student, serviceRequestBean);
				mailer.sendSpecialisationSelectionSummary(student, specialization);
			}else
				throw new Exception("Error While Updating Extising Specilisation Details.");
			
		}else {
			/*
			 * first instance of elective selection
			 * insert selection in staging
			 * send elective selection mail
			 * */
			logger.info("specialization status : first attempt");
			specialisationDAO.batchInsertTimeBoundStudentMapping(specialization);
			mailer.sendSpecialisationSelectionSummary(student, specialization);
		}

		logger.info("specialization saved for the student : "+updatedDetails.getUserId());
		
		return;
		
	}

	public List<Specialisation> fetchOptedinSpecialization(String sapid) throws Exception{
		
		List<Specialisation> optedinSpecialization = specialisationDAO.fetchOptedinSpecialization(sapid);
		
		return optedinSpecialization;
		
	}

	public void moveStagingToTimeBoundTable(TimeBoundUserMapping mappingBean, String userId) throws Exception{

		ArrayList<Specialisation> stagingTableList = specialisationDAO.getAllStagingTableTimeBoundMapping_V2(mappingBean);

		if(stagingTableList.size() < 1)
			throw new Exception("0 Records found for "+mappingBean.getAcadMonth()+ " - " +mappingBean.getAcadYear());
			
		if(StringUtils.isBlank(userId))
			userId = "batchInsertSpecializationMappings";

		specialisationDAO.batchInsertTimeBoundIds_V2(stagingTableList);
		specialisationDAO.batchInsertSpecializationMappings_V2(stagingTableList, mappingBean.getAcadYear(), mappingBean.getAcadMonth(), userId);
		specialisationDAO.batchUpdateStagingMovedToTimebound(stagingTableList, mappingBean.getAcadYear(), mappingBean.getAcadMonth(), userId);

	}
	
	public ArrayList<Specialisation> electiveCompleteProdReport(String month,String year,String term,String masterkey){
		
		//Get the student list from the input parameters
		
		ArrayList<String> userList = specialisationDAO.getAllRegistrationDetails(month, year, term, masterkey);
		ArrayList<Specialisation> specials = new ArrayList<Specialisation>();
		if(userList.size() > 0) {
			specials = specialisationDAO.getAllStudentSpecializationDetails(userList,month,year);
			HashMap<String,Specialisation> studentDetails = specialisationDAO.getStudentDetails(userList);
	
			specials.forEach(f -> f.setName((studentDetails.get(f.getSapid()).getName())));
			specials.forEach(f -> f.setEmailId((studentDetails.get(f.getSapid()).getEmailId())));
			specials.forEach(f -> f.setMobile((studentDetails.get(f.getSapid()).getMobile())));
			specials.forEach(f -> f.setTerm(term));
		
		}
		return specials;
		
	}
	
	public HashMap<String,MDMSubjectCodeMappingBean> getSubjectNameFromPssId(){
		return specialisationDAO.getSubjectNameFromPssId();
	}
}
