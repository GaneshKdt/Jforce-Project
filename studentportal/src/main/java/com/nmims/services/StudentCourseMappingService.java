package com.nmims.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;

import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.interfaces.CustomCourseWaiverService;
import com.nmims.interfaces.StudentCourseMappingInterface;
import com.nmims.dto.PassFailDataDTO;
import com.nmims.dto.ResultsDTO;
import com.nmims.dto.ResultsDataDTO;
@Service
public class StudentCourseMappingService implements StudentCourseMappingInterface
{
	@Autowired
	PortalDao portalDAO;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentCourseMappingDao studentCourseMapDao;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	RegistrationHelper registrationHelper;
	
	@Autowired
	CustomCourseWaiverService customCourseWaiverService;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	private static final Logger logger = LoggerFactory.getLogger("studentCourses");
	
	private ArrayList<String> ugPrograms = new ArrayList<String>(Arrays.asList("BBA","B.Com","BBA-BA"));  
	
	private final static String response_code = "200";
	
	private final static String notPass = "N";
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private static final Logger result_logger = LoggerFactory.getLogger("redis_result");
	
	public static final String STATUS_SUCCESS = "success";
	
	public static final boolean Is_redis = false;
	
	public static final String EnrollmentMonth_disable = "Jan";
	public static final String EnrollmentYear_disable = "2023";
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	@Override
	public int insertInStudentCourseTable(String sapid,boolean sfdcCall) throws Exception {
		// TODO Auto-generated method stub
		
		//Get the Student Data 
		StudentStudentPortalBean student = portalDAO.getSingleStudentsData(sapid);
		
		//Get The latest Registration Details 
		StudentStudentPortalBean studentRegistrationData = portalDAO.getStudentsMostRecentRegistrationData(sapid);
		
		//Check already exist subject of that student
		if(studentCourseMapDao.checkSapidInCourseTable(studentRegistrationData.getSapid(),studentRegistrationData.getMonth(),studentRegistrationData.getYear()) > 0)
			studentCourseMapDao.deleteCourseOfStudent(studentRegistrationData.getSapid(), studentRegistrationData.getYear(), studentRegistrationData.getMonth());
		
		//get the Current Subjects
		ArrayList<String> currentList = getCurrentCycleSujects(sapid,studentRegistrationData.getConsumerProgramStructureId(),studentRegistrationData.getSem(),sfdcCall);
	
		//Get the Waived Off and  waived Inn Subjects Subjects only for lateral
		if(student.getIsLateral().equals("Y")) {
			
			boolean sapidExistForWaivedIn = customCourseWaiverService.checkSapidExist(student.getSapid());
			
			//this will allow to run and get waivedIn Subjects for another sem than current sem if only sapid exist in custom_waived_in_subjects table
			if(studentCourseMapDao.getSemesterCountBySapid(sapid) == 1 || sapidExistForWaivedIn) 
				currentList.addAll(studentService.mgetWaivedInSubjects(student));
		
			ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
			currentList.removeAll(waivedOffSubjects);
		}

		// Block to check the remark pass fail subjects which currently is only applicable for BBA and BCOM students.
		if(ugPrograms.contains(student.getProgram())) {
				List<String> ugPassSubjectsList = getUGPassSubjects(portalDAO, student);
				for(String subject:ugPassSubjectsList){
					if(currentList.contains(subject)){
						currentList.remove(subject);
					}
				}
		}
	
		//Get the PssId of respective Subjects
		HashMap<String,String> subjectWithPssId = portalDAO.getProgramSemSubjectId(currentList, studentRegistrationData.getConsumerProgramStructureId());
		
		//Insert the currentPssId in course table
		int result = studentCourseMapDao.batchInsertStudentPssIdsMappings(subjectWithPssId,sapid,studentRegistrationData.getMonth(),studentRegistrationData.getYear());
		
		if(result <= 0) {
			logger.error("Student Subject course Error For Sapid "+sapid);
			logger.error("subjectWithPssId Subjects "+subjectWithPssId);
		}
		return result;
	}

	@Override
	public ArrayList<String> getCurrentCycleSujects(String sapid,String masterkey,String sem,boolean sfdcCall) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<String> currentList = studentCourseMapDao.getCurrentCycleSubjectlist(masterkey , sem,sapid,sfdcCall);

		return currentList;
	}
	
	public void populateStudentCourseData(String month,String year,boolean sfdcCall) throws Exception{
		//Get the student list from month and year 
		List<String> studentList = studentCourseMapDao.getListOfStudents(month,year);
	
		int success_count = 0;
		List<String> failed_sapids = new ArrayList<String>();
		int count = 0;
		for(String sapid : studentList )
		{
			count++;
			System.out.println("  Iteration "+(count)+"/"+studentList.size());
			try {
				if(studentCourseMapDao.checkSapidInCourseTable(sapid,month,year) > 0) {
					success_count++;
					continue;
				}
			//Insert the currentPssId in course table	
			int result = insertInStudentCourseTable(sapid,sfdcCall);
			
				if(result > 0)
					success_count++;
				else {
					failed_sapids.add(sapid);
					logger.error("Service Method populateStudentCourseData :-  Student :- "+sapid+" . 0 inserted in db");
				}
	
			}catch(Exception e)
			{
				logger.error("Service Method populateStudentCourseData :-  Student :- "+sapid+" Error "+e);
				failed_sapids.add(sapid);
			}
		} 
		logger.error("Service Method populateStudentCourseData :- Total Student :- "+studentList.size()+" Success/Failure "+success_count+"/"+failed_sapids.size());
		logger.error("Service Method populateStudentCourseData :-  Error in populating Student in Courses :- "+failed_sapids.toString());	}

	@Override
	public ArrayList<String> getFailSubjectsNamesForAStudent(String sapid) {
			return portalDAO.getFailSubjectsNamesForAStudent(sapid);//if error occur, fetch Data from normal dao
	}
	
	@Override
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		return  portalDAO.getPassSubjectsNamesForAStudent(sapid);
	}

	public ArrayList<String> getPSSIds(String sapid,String year,String month) {
		ArrayList<String> subjects = new ArrayList<String>();
		try{
			subjects = studentCourseMapDao.getPSSIds(sapid,year,month);
		}catch(Exception e) {
			logger.error("Service Method getPSSIds For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		return subjects;
	}
	
	public ArrayList<Integer> getPSSID(String sapid,String year,String month) {
		ArrayList<Integer> subjects = new ArrayList<Integer>();
		try{
			subjects = studentCourseMapDao.getPSSID(sapid,year,month);
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Service Method getPSSIds For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		return subjects;
	}
	
	public ArrayList<String> getCurrentCycleSubjects(String sapid,String year,String month) {
		ArrayList<String> subjects = new ArrayList<String>();
		try{
			subjects = studentCourseMapDao.getCurrentCycleSubjects(sapid,year,month);
		}catch(Exception e) {
			logger.error("Service Method getCurrentCycleSubjects For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		return subjects;
		
	}

	public StudentCourseMappingBean getcourses(StudentStudentPortalBean student,StudentMarksBean studentRegistrationData,double current_order,double acadContentLiveOrder,double reg_order)
	{
		ArrayList<String> currentSemSubjects = new ArrayList<String>();
		ArrayList<String> failedSubjects = new ArrayList<String>();
		ArrayList<String> allsubjects = new ArrayList<String>();

		HashMap<String,String> programSemSubjectIdWithSubject = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForBacklog = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForCurrentsem = new HashMap<String,String>();
		
		StudentCourseMappingBean subjects = new StudentCourseMappingBean();
		subjects.setIsRedis(Is_redis);
		try {

		if(registrationHelper.twoAcadCycleCourses(current_order,reg_order,acadContentLiveOrder)){
			student.setSem(studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			student.setConsumerProgramStructureId(studentRegistrationData.getConsumerProgramStructureId());	
			currentSemSubjects = studentCourseMapDao.getCurrentCycleSubjects(studentRegistrationData.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
		}
		//discuss
		try {
			failedSubjects =  getStudentRedisPassfailSubjects(student.getSapid(),notPass);
			subjects.setIsRedis(!Is_redis);
			
		}catch(Exception e)
		{
			result_logger.error(" Error in getting failed subjects from redis for sapid "+student.getSapid()+" . ErrorMessage :- ",e);
			failedSubjects = getFailSubjectsNamesForAStudent(student.getSapid());	//if error occur, fetch Data from normal dao
		}
		
		if(student.getEnrollmentMonth().equals(EnrollmentMonth_disable) && student.getEnrollmentYear().equals(EnrollmentYear_disable)){
			failedSubjects = studentCourseMapDao.getCurrentCycleSubjects(studentRegistrationData.getSapid(),EnrollmentYear_disable,EnrollmentMonth_disable);
		}
		//discuss
		
		//Comment not passed Dao call ( as it is not required after result is live )
		// Uncomment when next acad cycle is live and result is not live for previous cycle
		//failedSubjects.addAll(studentCourseMapDao.getNotPassedSubjectsBasedOnSapid(studentRegistrationData.getSapid(),studentRegistrationData.getSem(),studentRegistrationData.getConsumerProgramStructureId())); 
	
		if(student.getIsLateral().equalsIgnoreCase("Y")) {
			
			for(String subject : student.getWaivedOffSubjects()) {
			
				if(failedSubjects.contains(subject)) {
					
					failedSubjects.remove(subject);
				}
			}
			failedSubjects.addAll(student.getWaivedInSubjects());
		}

		if(failedSubjects != null && failedSubjects.size() > 0){
			//added by sachin
			//in case if student is in current acad cycle then he/she will get all subjects of current sem and previous sem also thats why i am removing here
			failedSubjects.removeAll(currentSemSubjects);
		}
		
		// Block to check the remark pass fail subjects which currently is only applicable for BBA and BCOM students.
		if(ugPrograms.contains(student.getProgram())) {
				List<String> ugPassSubjectsList = getUGPassSubjects(portalDAO, student);
				for(String subject:ugPassSubjectsList){
					if(currentSemSubjects.contains(subject)){
						currentSemSubjects.remove(subject);
					}
					if(failedSubjects.contains(subject)){
						failedSubjects.remove(subject);
					}
				}
		}
		
		//Add Current subjects and failed subjects in final list
		allsubjects.addAll(currentSemSubjects);
		allsubjects.addAll(failedSubjects);
	
		allsubjects = new ArrayList<String>(new LinkedHashSet<String>(allsubjects));
		
		if(currentSemSubjects.size() > 0) {
			programSemSubjectIdWithSubjectForCurrentsem = studentCourseMapDao.getProgramSemSubjectId(student.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
			programSemSubjectIdWithSubject.putAll(programSemSubjectIdWithSubjectForCurrentsem);	
		}
		if(failedSubjects.size() > 0) {
			programSemSubjectIdWithSubjectForBacklog = portalDAO.getProgramSemSubjectId(failedSubjects,student.getConsumerProgramStructureId());
			programSemSubjectIdWithSubject.putAll(programSemSubjectIdWithSubjectForBacklog);
		}
	
		
		}catch(Exception e) {
	
			logger.info("Error in getting courses service For Sapid "+student.getSapid()+" :- ",e);
		}
	
		subjects.setCurrentSemSubjectsmap(programSemSubjectIdWithSubjectForCurrentsem);
		subjects.setFailedSubjectListsmap(programSemSubjectIdWithSubjectForBacklog);
		subjects.setListOfApplicableSUbjectssmap(programSemSubjectIdWithSubject);
	
		//get all the subjectcodeId  from its respective pssIds
				if(programSemSubjectIdWithSubject.size() > 0) {
					Set<String> pssIds = programSemSubjectIdWithSubject.keySet();
					subjects.setSubjectCodeId(getSubjectCodeIdByPssId(pssIds));
				}
				
				
		return subjects;
	}
	
	private List<String> getUGPassSubjects(PortalDao pDao, StudentStudentPortalBean student) {
		return pDao.getUGPassSubjectsForAStudent(student.getSapid());
	} 
	
	public ArrayList<String> getStudentRedisPassfailSubjects(String sapid,String isPass) throws Exception {
		
			ArrayList<String> failSubject = new ArrayList<String>();
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> requestEntity = new HttpEntity<>("", getHeaders());
			String url =  SERVER_PATH + "passfail/m/results/"+sapid;
			ResponseEntity<String> response = restTemplate.exchange(url,  HttpMethod.GET,requestEntity ,String.class);	
			
			if (response_code.equalsIgnoreCase(response.getStatusCode().toString())) {
				//parse the Json data to StudentsDataInRedisBean bean
				JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
				ObjectMapper objectMapper = new ObjectMapper();
				ResultsDTO response_jackson = objectMapper.readValue(jsonResponse.toString(), ResultsDTO.class);	
				List<ResultsDataDTO> results = response_jackson.getListResultsDataDTO();
			
				if(response_jackson.getStatus().equalsIgnoreCase(STATUS_SUCCESS)) {
					for(ResultsDataDTO data : results ) {
						List<PassFailDataDTO>  passfailsubjects = data.getPassFailStatus(); //Get the passfail list from resultData
						failSubject.addAll(passfailsubjects.stream().filter(e -> e.getIsPass().equalsIgnoreCase(isPass)).map(PassFailDataDTO::getSubject).collect(Collectors.toCollection(ArrayList::new)));
					}
				}else 
					throw new Exception(response_jackson.getMessage());
			}
		return failSubject;
	}
	public List<Integer> getSubjectCodeIdByPssId(Set<String> pssId) {
		return studentCourseMapDao.getSubjectCodeIdByPssId(pssId);
	}
}
