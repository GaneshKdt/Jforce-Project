package com.nmims.services.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAOInterface;
import com.nmims.daos.PassFailTransferDao;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.services.AssignmentService;
import com.nmims.services.PassFailExecutorService;
import com.nmims.services.PassFailService;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Service("passFailService")
public class PassFailServiceImpl implements PassFailService{

	/*Variable*/
	@Autowired
	private PassFailDAOInterface passFailDAOIntf;
	
	@Autowired
	private PassFailDAO passFailDao;
	
	@Autowired
	private PassFailTransferDao passFailTransferDao;
	
	@Autowired
	private AssignmentService assignmentService;
	
	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");
	
	public static final Logger logger = LoggerFactory.getLogger("processPassFail-multiThreading");
	
	final String lessThan50Mesage = "Less than passing Score/Absent";
	final String lessThan40Mesage = "Less than 40 marks/Absent";
	
	final private String projectCountOnline = "projectCountOnline";
	final private String absentCount = "absentCount";
	final private String projectAbsentCount = "projectAbsentCount"; 
	final private String projectNotBookedCount = "projectNotBookedCount"; 
	final private String nvRiaCount = "nvRiaCount"; 
	final private String ansCount = "ansCount"; 
	final private String assignmentScoreOnlineCount = "assignmentScoreOnlineCount";
	final private String writtenScoreOnlineCount  = "writtenScoreOnlineCount";
	final private String graceMarksOnlineCount  = "graceMarksOnlineCount";
	final private String onlineCount= "onlineCount";

	/*Implemented Methods*/
	@Override
	public List<PassFailExamBean> setResultDecDateInPassFailList(List<PassFailExamBean> studentMarksList) {
		//Declared updated list which include result declared Date in studentMarksList
		List<PassFailExamBean> updatedStudentMarksList = new ArrayList<>();
		
		try
		{
			//To map unique year and month pair for get result declared date, key as "year,month" and value as "written_year"
			ArrayList<String> uniqueYearAndMonthList = generateUniqueYearAndMonth(studentMarksList);
			//To get list of result declared date by list of unique "year+month"
			List<PassFailExamBean> resultDeclaredList = passFailDAOIntf.getResultDeclaredDateByYearAndMonth(uniqueYearAndMonthList);
			//To map result_declared_date with year and month, key as "year+month" and value as "resultDeclareDate"
			HashMap<String, String> mapResultDeclared = getMapResultDeclaredWithYearAndMonth(resultDeclaredList);
			//Set above result_declared_date for each sapid in list by mapping year and month of sapid
			updatedStudentMarksList = setResultDeclareInStudentList(studentMarksList,mapResultDeclared);
			//Return updated list which include resultDeclaredDate for every sapid
			return updatedStudentMarksList;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			//Added logger to store Exception if any occurred
			passFailLogger.error("Error In Adding Result Declared Date In studentMarksList ::->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+e);
			//Return old studentMarksList list which not include any resultDeclaredDate
			return studentMarksList;
		}
	}
	
	//To Generate HashMap Of Unique Year and Month, Key as "year,month" and value as "written_year"
	public ArrayList<String> generateUniqueYearAndMonth(List<PassFailExamBean> studentMarksList)
	{
		//Declared to get year and month uniquely as key in this HashMap
		ArrayList<String> uniqueYearAndMonthList = new ArrayList<>();
		//Iterating studentMarksList
		studentMarksList.forEach(bean -> {
			//Create pattern to reduce repetition of generating unique year and month
			String key = bean.getWrittenYear()+bean.getWrittenMonth();
			//Check if uniqueYearAndMonthList contain this year+month
			if(!uniqueYearAndMonthList.contains(key))
			{
				//Add the pattern key in Map as key and written_year as value
				uniqueYearAndMonthList.add(key);
			}
		});
		//Return list of unique year and month
		return uniqueYearAndMonthList;
	}
	
	//To get Result Declared Date in HashMap Pattern, Key As "year,month" and Value As "resultDeclaredDate"
	public HashMap<String, String> getMapResultDeclaredWithYearAndMonth(
			List<PassFailExamBean> resultDeclaredList)throws Exception
	{
		//Declared HashMap to store declaredDate by mapping year and month
		HashMap<String, String> mapResultDeclared = new HashMap<>();
		
		//Check if resultDeclared date have not null
		resultDeclaredList.forEach(bean -> {
			//Create pattern to reduce repetition of generating unique year and month
			String key = bean.getYear()+bean.getMonth();
			//Check if result declared date is null
			if(bean.getResultDeclaredDate() == null || "".equals(bean.getResultDeclaredDate()))
			{
				//Then marked resultDeclaredDate as 'NA' and add in HashMap
				mapResultDeclared.put(key, "NA");
			}
			//If not
			else
			{
				//Then add resultDeclaredDate in HashMap
				mapResultDeclared.put(key, bean.getResultDeclaredDate());
			}
		});
		//Return HashMap of resultDeclaredDate
		return mapResultDeclared;
	}
	
	//To Set Result Declared Date In studentMarksList and Generate Updated List of studentMarksList
	public List<PassFailExamBean> setResultDeclareInStudentList(List<PassFailExamBean> studentMarksList,
			HashMap<String, String> mapResultDeclared)
	{
		//Declared to store updated list of studentMarksList after adding result declared date
		//List<PassFailExamBean> updatedStudentMarksList = new ArrayList<>();
		//Iterating studentMarksList
		studentMarksList.forEach(bean -> {
			//Create pattern to reduce repetition of generating unique year and month
			String key = bean.getWrittenYear()+bean.getWrittenMonth();
			//Check if mapYearMonthWithResultDeclared contain this year and month as key in above pattern
			if(mapResultDeclared.containsKey(key))
			{
				//Extract key and get value in "extractResultDeclare" variable
				String extractResultDeclare = mapResultDeclared.get(key);
				//Set "extractResultDeclare" in resultDeclared
				bean.setResultDeclaredDate(extractResultDeclare);
			}
		});
		//Return updated list which include resultDeclaredDate for every sapid
		return studentMarksList;
	}

	@Override
	public ArrayList<PassFailExamBean> executePassFailLogicForIndividualStudent(HashMap<String, ArrayList> keysMap,
			HashMap<String, StudentExamBean> studentsMap,
			HashMap<String, ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap,
			HashMap<String, Integer> programSubjectPassScoreMap) {

		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<PassFailExamBean>();
		// ArrayList<PassFailBean> passStudentList = new ArrayList<PassFailBean>();
//		int passCount = 0;
//		int failCount = 0;
		int minPassScoreRequired = 0;

		Iterator entries = keysMap.entrySet().iterator();

		// Code pulled out by Vilpesh on 2022-05-24
		ArrayList<String> softSkillSubjects = new ArrayList<String>(Arrays.asList("Soft Skills for Managers",
				"Employability Skills - II Tally", "Start your Start up", "Design Thinking"));

		/*
		 * Algorithm (Old Algorithm Below, All configured in DB now): 1. Check if
		 * Assignment is given, if not given then ANS, use latest written score and put
		 * result on hold 2. If Assignment is given, then check if assignment is given
		 * before written for Offline cases and cases till Dec-2015. If not then fail 3.
		 * If passed above step, then take best of assignment and latest of written and
		 * decide pass or fail based on total
		 */
		for (int k = 0; k < keysMap.size(); k++) {
			Entry thisEntry = (Entry) entries.next();
			String key = (String) thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList) thisEntry.getValue();
			// PassFailExamBean passFailBean = new PassFailExamBean();//shifted down by
			// Vilpesh on 2021-12-17
			StudentMarksBean currentBean = currentList.get(0);
			String sapId = currentBean.getSapid();
			
			StudentExamBean student = studentsMap.get(sapId);
			
			String prgmStructApplicable = currentBean.getPrgmStructApplicable() != null
					? currentBean.getPrgmStructApplicable()
					: student.getPrgmStructApplicable();
					
			String programSubjectProgramStructureKey = currentBean.getProgram() + "-" + currentBean.getSubject() + "-"
					+ prgmStructApplicable;
			
			ProgramSubjectMappingExamBean passingConfiguration = programSubjectPassingConfigurationMap
					.get(programSubjectProgramStructureKey);
			
			// minPassScoreRequired =
			// programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
			passFailLogger.info("START Temporary changes inside for loop block  " + k + "/" + keysMap.size() + " Sapid "
					+ currentBean.getSapid() + " Subject " + currentBean.getSubject()
					+ " programSubjectProgramStructureKey " + programSubjectProgramStructureKey);

			// Temporary changes, remove later
			if (programSubjectPassScoreMap.get(programSubjectProgramStructureKey) == null) {
				passFailLogger.info("ERROR {} {} value of key : {} not found in map programSubjectPassScoreMap so continuing for loop",
						sapId, currentBean.getSubject(),programSubjectProgramStructureKey);
				continue;
			} else {
				minPassScoreRequired = programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
			}
			passFailLogger.info("END Temporary changes inside for loop block  " + k + "/" + keysMap.size() + " Sapid "
					+ currentBean.getSapid() + " Subject " + currentBean.getSubject()
					+ " programSubjectProgramStructureKey " + programSubjectProgramStructureKey);

			PassFailExamBean passFailBean = new PassFailExamBean();
			// Temporary changes, end
			
			transferDataInPassBean(passFailBean, currentBean);
			
			int assignmentScore = 0;
			int termEndScore = 0;
			passFailLogger.info("START executeAssignmentRelatedPassFailLogic inside for loop block  " + k + "/"
					+ keysMap.size() + " Sapid " + currentBean.getSapid() + " Subject " + currentBean.getSubject());

			if ("Y".equalsIgnoreCase(passingConfiguration.getHasAssignment())
					&& "Y".equalsIgnoreCase(passingConfiguration.getHasIA())) {
				boolean processFurther = passFailDao.executeAssignmentRelatedPassFailLogic(currentList, passFailBean,
						student, passFailStudentList, passingConfiguration);
				if (!processFurther) {
					// There could be scenarios where TEE logic need not be executed, if certain
					// assignment conditions are not met
					passFailLogger.info("Error {} {} recieved processFurther values as {} so continuing for loop", 
							sapId, currentBean.getSubject(), processFurther);
					continue;
				}
			}
			if (StringUtils.isNumeric(passFailBean.getAssignmentscore())) {
				assignmentScore = Integer.parseInt(passFailBean.getAssignmentscore());
			}
			passFailLogger.info("END executeAssignmentRelatedPassFailLogic inside for loop block  " + k + "/"
					+ keysMap.size() + " Sapid " + currentBean.getSapid() + " Subject " + currentBean.getSubject());

			passFailLogger.info("START executeTEERelatedPassFailLogic  inside for loop block  " + k + "/"
					+ keysMap.size() + " Sapid " + currentBean.getSapid() + " Subject " + currentBean.getSubject());

			passFailDao.executeTEERelatedPassFailLogic(currentList, passFailBean, passingConfiguration);
			
			passFailLogger.info("END executeTEERelatedPassFailLogic inside for loop block  " + k + "/" + keysMap.size()
					+ " Sapid " + currentBean.getSapid() + " Subject " + currentBean.getSubject());

			if (StringUtils.isNumeric(passFailBean.getWrittenscore())) {
				termEndScore = Integer.parseInt(passFailBean.getWrittenscore());
			}

			int grace = passFailDao.calculateGraceMarksGiven(currentList, passFailBean, passingConfiguration);

			// Code pulled out so commented by Vilpesh on 2022-05-24
			// ArrayList<String> softSkillSubjects= new
			// ArrayList<String>(Arrays.asList("Soft Skills for Managers","Employability
			// Skills - II Tally","Start your Start up","Design Thinking"));

			if (softSkillSubjects.contains(currentBean.getSubject()) && (assignmentScore + termEndScore + grace < 15)) {
				// do not show this subject
			} else if (assignmentScore + termEndScore + grace >= minPassScoreRequired) {// Pass

				passFailBean.setTotal(assignmentScore + termEndScore + "");
				passFailBean.setIsPass("Y");
//				passFailStudentList.add(passFailBean);
//				passCount++;
			} else {// Fail
				passFailBean.setTotal(assignmentScore + termEndScore + "");
				passFailBean.setFailReason(lessThan50Mesage);
				passFailBean.setIsPass("N");
//				passFailStudentList.add(passFailBean);
//				failCount++;
			}

			/*
			 * added by Abhay Purpose: To not pass students marked as copy-case in
			 * assignments
			 */
			processCopyCaseLogicForPassFail(currentList, student, passFailBean);

			passFailLogger.info("processNew inside for loop block  " + k + "/" + keysMap.size());
			passFailStudentList.add(passFailBean);
		}

		// Unused, so commented by Vilpesh on 2022-05-24
		// if(passFailStudentList != null) {
		// }

		// code by Vilpesh on 2022-05-24
		if (null != softSkillSubjects) {
			softSkillSubjects.clear();
			softSkillSubjects = null;
		}
		
		//commented by swarup as not to make student map since it'll be used for other threads
//		if(null != studentsMap) {
//			studentsMap.clear();
//			studentsMap = null;
//		}

		return passFailStudentList;
	}

	//extracted from pass fail logic to a separate method
	private void processCopyCaseLogicForPassFail(ArrayList<StudentMarksBean> currentList, StudentExamBean student,
			PassFailExamBean passFailBean) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM");
			// Date writtenDate =
			// sdf.parse(passFailBean.getWrittenYear()+"-"+passFailBean.getWrittenMonth());
			// Date assignmentDate =
			// sdf.parse(passFailBean.getAssignmentYear()+"-"+passFailBean.getAssignmentMonth());
			Date date = sdf.parse("2022-Apr");
			List<StudentMarksBean> reversedList = currentList.stream()
					.sorted(Comparator.comparing(StudentMarksBean::getExamorder).reversed())
					.collect(Collectors.toList());
			Date yearMonthDate = sdf.parse(reversedList.get(0).getYear() + "-" + reversedList.get(0).getMonth());

//					if(writtenDate.equals(date) || writtenDate.after(date) || assignmentDate.equals(date) || assignmentDate.after(date) ) {	
			if (yearMonthDate.equals(date) || yearMonthDate.after(date)) {
				if ("Retail".equals(student.getConsumerType())) {
					passFailDao.checkifAnyAssignmentCopyCaseMark(reversedList, passFailBean);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			passFailLogger.error(" Error Occur  " + errors);
		}
	}
	
	//code to be shifted to DAO for transaction management
	public Integer transferRecordsFromStagingToPassFail(String year, String month) throws NoRecordFoundException {

		List<PassFailExamBean> allRecordsFromPassFailStaging = passFailDao.getAllRecordsFromPassFailStagingByYearAndMonth(year, month);

		if (allRecordsFromPassFailStaging == null || allRecordsFromPassFailStaging.isEmpty())
			throw new NoRecordFoundException("No records in passfail staging!");

		passFailLogger.info("Received data from staging {}", allRecordsFromPassFailStaging.size());

		Set<String> existingRecordsFromPassFail = passFailDao.getExistingRecordsFromPassFail();

		passFailLogger.info("Existing pass fail staging table has {} number of records",existingRecordsFromPassFail.size());

		return passFailTransferDao.transferFromStagingToPassFailTransactional(allRecordsFromPassFailStaging,existingRecordsFromPassFail);

	}

	private void transferDataInPassBean(PassFailExamBean passFailBean, StudentMarksBean currentBean) {
		passFailBean.setSapid(currentBean.getSapid());
		passFailBean.setSubject(currentBean.getSubject());
		passFailBean.setSem(currentBean.getSem());
		passFailBean.setProgram(currentBean.getProgram());
		passFailBean.setGrno(currentBean.getGrno());
		passFailBean.setName(currentBean.getStudentname());
		passFailBean.setWrittenscore("");
		passFailBean.setAssignmentscore("");
		passFailBean.setWrittenYear("");
		passFailBean.setWrittenMonth("");
		passFailBean.setAssignmentYear("");
		passFailBean.setAssignmentMonth("");
		passFailBean.setStudentType(currentBean.getStudentType());
	}

	@Override
	public void executeAssignmentLogicPostPassFailProcess(String examYear, String examMonth) {
		try {
			
			if(examMonth == null || examYear == null)
				return;
			
			passFailLogger.info("--------------------------- EXECUTING ASSIGNMENT LOGIC START for year : {} and month : {} ---------------------------",
					examYear, examMonth);
			
			List<PassFailExamBean> recordsFromPassFail = passFailDao.getPassFailRecordsByResultProcessedYearAndMonth(examYear, examMonth);

			passFailLogger.info("--------------------------- FETCHED RECORDS FROM PASSFAIL TABLE TO UPDATE QUICK ASSIGNMENT TABLE : {} --------------------------- ",
					recordsFromPassFail.size());

			assignmentService.updateQuickAssgTableOnPassfailProcess(new ArrayList<>(recordsFromPassFail));


		} catch (Exception e) {
			
			passFailLogger.info("ERROR while trying to update quick assignment table / getting pass fail records for year : {} and month : {}  : {}",
					examYear, examMonth, Throwables.getStackTraceAsString(e));
		}
		
		passFailLogger.info("--------------------------- EXECUTING ASSIGNMENT LOGIC FINISHED ---------------------------");
	}

public List<StudentMarksBean> getstudentProjectList(StudentExamBean searchBean,String recordType) {
		List<StudentMarksBean> pendingRecordForOnlineOfflineProject=new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> projectOnlineRecord=new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> projectOfflineRecord=new ArrayList<StudentMarksBean>();

		try {
			
			pendingRecordForOnlineOfflineProject=passFailDAOIntf.getPendingRecordsForOnlineOfflineProject(searchBean);
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
	

			pendingRecordForOnlineOfflineProject.stream().
			filter( y -> studentDetails.containsKey(y.getSapid()))
			.forEach( x ->  {
				x.setPrgmStructApplicable(studentDetails.get(x.getSapid()).getPrgmStructApplicable());
				StudentExamBean sb = new StudentExamBean();
				sb.setProgram(x.getProgram());
				  sb.setPrgmStructApplicable(x.getProgramStructApplicable());
				  x.setExamMode(sb.getExamMode());
				  if("Online".equalsIgnoreCase(x.getExamMode())) { 						
						projectOnlineRecord.add(x);		
				  }				
				 if("Offline".equalsIgnoreCase(x.getExamMode())){
					 projectOfflineRecord.add(x);		
				 }				
			});
		
			if(recordType.equalsIgnoreCase("Online")) {
				return projectOnlineRecord;
			}else  {
				return projectOfflineRecord;
			}
		}catch(Exception e) {
			return new ArrayList<StudentMarksBean>();
		}	
	}
	@Override
	public List<StudentMarksBean> getAbsentStundentRecord(StudentExamBean searchBean) {
		List<StudentMarksBean>	absentRecord=new ArrayList<StudentMarksBean>();
		try {
			absentRecord= passFailDAOIntf.getAbsentRecord(searchBean);
			return absentRecord;
		}catch(Exception e) {
			return absentRecord;
		}
	}
	@Override
	public List<StudentMarksBean> getPendingCountForNVRIA(StudentExamBean searchBean) {
		List<StudentMarksBean>	getPendingRecordsForNVRIA=new ArrayList<StudentMarksBean>();
		try {
			getPendingRecordsForNVRIA= passFailDAOIntf.getPendingListForNVRIA(searchBean);
			return getPendingRecordsForNVRIA;
		}catch(Exception e) {
			return getPendingRecordsForNVRIA;
		}
	}
	@Override
	public List<StudentMarksBean> getPendingListForANS(StudentExamBean searchBean) {
		List<StudentMarksBean>	pendingListForANS=new ArrayList<StudentMarksBean>();
		try {
			pendingListForANS= passFailDAOIntf.getPendingListForANS(searchBean);
			return pendingListForANS;
		}catch(Exception e) {
			return pendingListForANS;
		}
	}
	
	@Override
	public List<StudentMarksBean> getWrittenScoreRecords(StudentExamBean searchBean,String recordType) {
		List<StudentMarksBean>	writtenScoreRecords=new ArrayList<StudentMarksBean>();
		try {
			writtenScoreRecords= passFailDAOIntf.getPendingRecordsForOnlineOfflineWritten(searchBean);
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
			List<StudentMarksBean> writtenScoreOnline=new ArrayList<StudentMarksBean>();
			List<StudentMarksBean> writtenScoreOffline=new ArrayList<StudentMarksBean>();
		
			writtenScoreRecords.stream().
			filter( y -> studentDetails.containsKey(y.getSapid()))
			.forEach(e ->{
				e.setPrgmStructApplicable(studentDetails.get(e.getSapid()).getPrgmStructApplicable());
				StudentExamBean sb = new StudentExamBean();
				sb.setProgram(e.getProgram());
				sb.setPrgmStructApplicable(e.getProgramStructApplicable());
				e.setExamMode(sb.getExamMode());
				if("Online".equalsIgnoreCase(e.getExamMode())){
					writtenScoreOnline.add(e);
				}
				if("Offline".equalsIgnoreCase(e.getExamMode())){
					writtenScoreOffline.add(e);
				}
			});
			
			
			if(recordType.equalsIgnoreCase("Online")) {
				return writtenScoreOnline;
			}else {
				return writtenScoreOffline;
			}
		}catch(Exception e) {
			return new ArrayList<StudentMarksBean>();
		}
	}
	
	@Override
	public List<StudentMarksBean> getAssignmentSubmittedRecords(StudentExamBean searchBean,String recordType) {
		List<StudentMarksBean>	assignmentSubmittedRecord=new ArrayList<StudentMarksBean>();
		try {
			assignmentSubmittedRecord= passFailDAOIntf.getPendingRecordsForOnlineOfflineAssignment(searchBean);
			List<StudentMarksBean> assignmentSubmittedOnline=new ArrayList<StudentMarksBean>();
			List<StudentMarksBean> assignmentSubmittedOffline=new ArrayList<StudentMarksBean>();
			
	
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
			
			assignmentSubmittedRecord.stream()
			.filter(y -> studentDetails.containsKey(y.getSapid()))
			.forEach(e -> {
					e.setPrgmStructApplicable(studentDetails.get(e.getSapid()).getPrgmStructApplicable());
					StudentExamBean sb = new StudentExamBean();
					sb.setProgram(e.getProgram());
					sb.setPrgmStructApplicable(e.getProgramStructApplicable());
					e.setExamMode(sb.getExamMode());
					if("Online".equalsIgnoreCase(e.getExamMode())){
						assignmentSubmittedOnline.add(e);
					}
					if("Offline".equalsIgnoreCase(e.getExamMode())){
						assignmentSubmittedOffline.add(e);
					}
				});	

			if(recordType.equalsIgnoreCase("Online")) {
				return assignmentSubmittedOnline;
			}else {
				return assignmentSubmittedOffline;
			}
			
		}catch(Exception e) {
			return new ArrayList<StudentMarksBean>();
		}
	}
	
	@Override
	public List<StudentMarksBean> getRecordsForOnlineOffline(StudentExamBean searchBean,String recordType) {
		List<StudentMarksBean>	recordForOnlineOffline=new ArrayList<StudentMarksBean>();
		try {
			recordForOnlineOffline= passFailDAOIntf.getPendingRecordsForOnlineOffline(searchBean);
			List<StudentMarksBean> online=new ArrayList<StudentMarksBean>();
			List<StudentMarksBean> offline=new ArrayList<StudentMarksBean>();
		
			
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
			
			recordForOnlineOffline.stream()
			.filter(y -> studentDetails.containsKey(y.getSapid()))
			.forEach(e -> {
					e.setPrgmStructApplicable(studentDetails.get(e.getSapid()).getPrgmStructApplicable());
					StudentExamBean sb = new StudentExamBean();
					sb.setProgram(e.getProgram());
					sb.setPrgmStructApplicable(e.getProgramStructApplicable());
					e.setExamMode(sb.getExamMode());
					if("Online".equalsIgnoreCase(e.getExamMode())){
						online.add(e);
					}
					if("Offline".equalsIgnoreCase(e.getExamMode())){
						offline.add(e);
					}
				});	
			if(recordType.equalsIgnoreCase("Online")) {
				return online;
			}else {
				return offline;
			}

		}catch(Exception e) {
			return new ArrayList<StudentMarksBean>();
		}
	}

	@Override
	public HashMap<String, StudentExamBean> getAllStudents(StudentExamBean searchBean) {
		List<StudentExamBean> studentList=new ArrayList<>();
		HashMap<String, StudentExamBean> studentMapped =  new HashMap<String, StudentExamBean>();
	
		try {
			studentList = passFailDAOIntf.getStudentList(searchBean);
			studentList.stream().forEach(x->{
				if(!StringUtils.isBlank(x.getSapid())) {
					studentMapped.put(x.getSapid(), x);
				}
			});
			return studentMapped;
		}catch(Exception e) {
			return studentMapped;
		}
		
	}
	

	@Override
	public HashMap<String,Integer> getPendingCountForOnlineOfflineProject(StudentExamBean searchBean,HashMap<String,StudentExamBean> student) {

		List<StudentMarksBean> studentProjectList=new ArrayList<StudentMarksBean>();
		HashMap<String,Integer> projectOfflineOnlineCount = new HashMap<String, Integer>();
		try {
			AtomicInteger projectCountOnline = new AtomicInteger();
			AtomicInteger projectCountOffline = new AtomicInteger();
			AtomicInteger projectAbsentCount = new AtomicInteger();
			AtomicInteger projectNotBookedCount = new AtomicInteger();
			studentProjectList=passFailDAOIntf.getPendingRecordsForOnlineOfflineProject(searchBean);

			
		
		
			studentProjectList.stream().
			filter( y -> student.containsKey(y.getSapid()))
			.forEach( x ->  {
				x.setPrgmStructApplicable(student.get(x.getSapid()).getPrgmStructApplicable());
				StudentExamBean sb = new StudentExamBean();
				sb.setProgram(x.getProgram());
				  sb.setPrgmStructApplicable(x.getProgramStructApplicable());
				  x.setExamMode(sb.getExamMode());
				  if("Online".equalsIgnoreCase(x.getExamMode())) { 						
					  projectCountOnline.getAndIncrement();	
					  if("AB".equalsIgnoreCase(x.getWritenscore())) {
						  projectAbsentCount.getAndIncrement();
					  }
					  if(StringUtils.isBlank(x.getWritenscore())) {
						  projectNotBookedCount.getAndIncrement();
					  }
				  }				
				 if("Offline".equalsIgnoreCase(x.getExamMode())){
					 projectCountOffline.getAndIncrement();			
				 }				
			});
		
			projectOfflineOnlineCount.put("projectCountOnline",projectCountOnline.intValue());
			projectOfflineOnlineCount.put("projectCountOffline",projectCountOffline.intValue());
			projectOfflineOnlineCount.put("projectAbsentCount",projectAbsentCount.intValue());
			projectOfflineOnlineCount.put("projectNotBookedCount", projectNotBookedCount.intValue());
			return projectOfflineOnlineCount;
		}catch(Exception e) {
			e.printStackTrace();
			return projectOfflineOnlineCount;
		}
	}

	@Override
	public HashMap<String, Integer> getPendingCountForOnlineOfflineWritten(StudentExamBean searchBean,HashMap<String,StudentExamBean> student) {
		List<StudentMarksBean> studentWrittenList=new ArrayList<StudentMarksBean>();
		HashMap<String,Integer> writtenOfflineOnlineCount = new HashMap<String, Integer>();
		try {
			AtomicInteger writtenScoreOnlineCount = new AtomicInteger();
			AtomicInteger writtenScoreOfflineCount = new AtomicInteger();
			studentWrittenList=passFailDAOIntf.getPendingRecordsForOnlineOfflineWritten(searchBean);

			studentWrittenList.stream().
			filter( y -> student.containsKey(y.getSapid()))
			.forEach(e ->{
				e.setPrgmStructApplicable(student.get(e.getSapid()).getPrgmStructApplicable());
				StudentExamBean sb = new StudentExamBean();
				sb.setProgram(e.getProgram());
				sb.setPrgmStructApplicable(e.getProgramStructApplicable());
				e.setExamMode(sb.getExamMode());
				if("Online".equalsIgnoreCase(e.getExamMode())){
					writtenScoreOnlineCount.getAndIncrement();
				}
				if("Offline".equalsIgnoreCase(e.getExamMode())){
					writtenScoreOfflineCount.getAndIncrement();
				}
			});
			writtenOfflineOnlineCount.put("writtenScoreOnlineCount",writtenScoreOnlineCount.intValue());
			writtenOfflineOnlineCount.put("writtenScoreOfflineCount",writtenScoreOfflineCount.intValue());
			return writtenOfflineOnlineCount;
		}catch(Exception e) {
			return writtenOfflineOnlineCount;
		}
	}

	@Override
	public HashMap<String, Integer> getPendingCountForOnlineOfflineAssignment(StudentExamBean searchBean,HashMap<String,StudentExamBean> student) {
		List<StudentMarksBean> studentAssignmnetList=new ArrayList<StudentMarksBean>();
		HashMap<String,Integer> assignmentOfflineOnlineCount = new HashMap<String, Integer>();
		try {
			AtomicInteger assignmentScoreOnlineCount = new AtomicInteger();
			AtomicInteger assignemntScoreOfflineCount = new AtomicInteger();
			studentAssignmnetList  = passFailDAOIntf.getPendingRecordsForOnlineOfflineAssignment(searchBean);
	
			
			studentAssignmnetList.stream()
			.filter(y -> student.containsKey(y.getSapid()))
			.forEach(e -> {
					e.setPrgmStructApplicable(student.get(e.getSapid()).getPrgmStructApplicable());
					StudentExamBean sb = new StudentExamBean();
					sb.setProgram(e.getProgram());
					sb.setPrgmStructApplicable(e.getProgramStructApplicable());
					e.setExamMode(sb.getExamMode());
					if("Online".equalsIgnoreCase(e.getExamMode())){
						assignmentScoreOnlineCount.getAndIncrement();
					}
					if("Offline".equalsIgnoreCase(e.getExamMode())){
						assignemntScoreOfflineCount.getAndIncrement();
					}
				});	
			assignmentOfflineOnlineCount.put("assignmentScoreOnlineCount",assignmentScoreOnlineCount.intValue());
			assignmentOfflineOnlineCount.put("assignemntScoreOfflineCount",assignemntScoreOfflineCount.intValue());
			return assignmentOfflineOnlineCount;
		} catch (Exception e) {
			return assignmentOfflineOnlineCount;
		}
	}

	@Override
	public int getPendingRecordsForAbsent(StudentExamBean searchBean) {
		int absentCount =0;
		try{
			absentCount = passFailDAOIntf.getPendingCountForAbsent(searchBean);
		}catch(Exception e) {
			return absentCount;
		}
		return absentCount;	
	}

	@Override
	public int getPendingRecordsForNVRIA(StudentExamBean searchBean) {
		int pendingNVRIA =0;
		try{
			pendingNVRIA = passFailDAOIntf.getPendingCountForNVRIA(searchBean);
		}catch(Exception e) {
			return pendingNVRIA;
		}
		return pendingNVRIA;
	}

	@Override
	public int getPendingRecordsForANS(StudentExamBean searchBean) {
		int pendingANS =0;
		try{
			pendingANS = passFailDAOIntf.getPendingCountForANS(searchBean);
		}catch(Exception e) {
			return pendingANS;
		}
		return pendingANS;
	}

	@Override
	public HashMap<String, Integer> getPendingCountForOnlineOffline(StudentExamBean searchBean,HashMap<String,StudentExamBean> student) {
		List<StudentMarksBean> studentOnlineOfflineList=new ArrayList<StudentMarksBean>();
		HashMap<String,Integer> studentOfflineOnlineCount = new HashMap<String, Integer>();
		try {
			AtomicInteger onlineCount = new AtomicInteger();
			AtomicInteger offlineCount = new AtomicInteger();
			studentOnlineOfflineList  = passFailDAOIntf.getPendingRecordsForOnlineOffline(searchBean);
			
			studentOnlineOfflineList.stream()
			.filter(y -> student.containsKey(y.getSapid()))
			.forEach(e -> {
				StudentExamBean studentMap = student.get(e.getSapid());
					e.setPrgmStructApplicable(studentMap.getPrgmStructApplicable());
					StudentExamBean sb = new StudentExamBean();
					sb.setProgram(e.getProgram());
					sb.setPrgmStructApplicable(e.getProgramStructApplicable());
					e.setExamMode(sb.getExamMode());
					if("Online".equalsIgnoreCase(e.getExamMode())){
						onlineCount.getAndIncrement();
					}
					if("Offline".equalsIgnoreCase(e.getExamMode())){
						offlineCount.getAndIncrement();
					}
				});	
			studentOfflineOnlineCount.put("onlineCount",onlineCount.intValue());
			studentOfflineOnlineCount.put("offlineCount",offlineCount.intValue());
			return studentOfflineOnlineCount;
		} catch (Exception e) {
			return studentOfflineOnlineCount;
		}
	}

	@Override
	public List<StudentMarksBean> getStudentNotBookedStudent(StudentExamBean searchBean) {
		List<StudentMarksBean> reportForProjectNotBooked = new ArrayList<StudentMarksBean>();
		try {
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
			List<StudentMarksBean> projectListForNotBooked = passFailDAOIntf.getStudentNotBookedStudent(searchBean);
			
			reportForProjectNotBooked =	projectListForNotBooked.stream()
					.filter(x -> studentDetails.containsKey(x.getSapid())).collect(Collectors.toList());
					
			return reportForProjectNotBooked;
		}catch(Exception e) {
			return reportForProjectNotBooked;
		}
	
	}

	@Override
	public List<StudentMarksBean> getStudentProjectAbsentList(StudentExamBean searchBean) {
		List<StudentMarksBean> reportForProjectAbsentList =  new ArrayList<StudentMarksBean>();
		try {
			HashMap<String, StudentExamBean> studentDetails = getAllStudents(searchBean);
			List<StudentMarksBean>	projectAbsentList = passFailDAOIntf.getProjectAbsentList(searchBean);
			
			reportForProjectAbsentList = projectAbsentList.stream()
					.filter(x -> studentDetails.containsKey(x.getSapid())).collect(Collectors.toList());
			
			return reportForProjectAbsentList;
		}catch(Exception e) {
			return reportForProjectAbsentList;
		}
	}

	@Override
	public Map<String, Integer> getPassFailTranferReportCount(String year, String month) {
		
		Map<String, Integer> transferReportCountMap = new HashMap<>();
		
		transferReportCountMap.put(projectCountOnline, passFailTransferDao.getProjectCount(year, month));
		transferReportCountMap.put(absentCount, passFailTransferDao.getTEEAbsentStudentsCount(year, month));
		transferReportCountMap.put(projectAbsentCount, passFailTransferDao.getProjectAbsentStudentsCount(year, month));
		transferReportCountMap.put(projectNotBookedCount, passFailTransferDao.getProjectNotBookedCount(year, month));
		transferReportCountMap.put(nvRiaCount, passFailTransferDao.getNVRIARecordsCount(year, month));
		transferReportCountMap.put(ansCount, passFailTransferDao.getAssignmentANSCount(year, month));
		transferReportCountMap.put(assignmentScoreOnlineCount, passFailTransferDao.getAssignmentSubmittedCount(year, month));
		transferReportCountMap.put(writtenScoreOnlineCount, passFailTransferDao.getWrittenScoreCount(year, month));
		transferReportCountMap.put(graceMarksOnlineCount, passFailTransferDao.getGraceMarksCount(year, month));
		transferReportCountMap.put(onlineCount, passFailTransferDao.getAllTransferCount(year, month));
		
		return transferReportCountMap;
	}

	@Override
	public List<PassFailExamBean> getPassFailReportForType(String type, String year, String month) {
		
		switch (type) {
		case projectCountOnline:
			return passFailTransferDao.getProjectRecords(year, month);
			
		case absentCount:
			return passFailTransferDao.getTEEAbsentStudents(year, month);
			
		case projectAbsentCount:
			return passFailTransferDao.getProjectAbsentStudents(year, month);
			
		case projectNotBookedCount:
			return passFailTransferDao.getProjectNotBookedStudents(year, month);
			
		case nvRiaCount:
			return passFailTransferDao.getNVRIARecords(year, month);
			
		case ansCount:
			return passFailTransferDao.getAssignmentANSRecords(year, month);
			
		case assignmentScoreOnlineCount:
			return passFailTransferDao.getAssignmentSubmittedRecords(year, month);
			
		case writtenScoreOnlineCount:
			return passFailTransferDao.getWrittenScoreRecords(year, month);
			
		case graceMarksOnlineCount:
			return passFailTransferDao.getGraceAppliedRecords(year, month);
			
		case onlineCount:
			return passFailTransferDao.getAllTransferRecords(year, month);

		default:
			throw new RuntimeException("No data found for unknown type : " + type + " or year month combination : " + month+year);
		}
	}

	@Override
	public List<PassFailExamBean> filterApplyGraceStudents(List<PassFailExamBean> passFailStudentList) {
		
		HashMap<String,ProgramSubjectMappingExamBean> configurationMap = passFailDao.getProgramSubjectPassingConfigurationMap();
		
		passFailLogger.info(" getProgramSubjectPassingConfigurationMap : {}", configurationMap.size());
		
//		HashMap<String, StudentExamBean> allStudents = passFailDao.getAllStudents();		
		
		Map<String, StudentExamBean> allStudents = passFailDao.getProgramDetailsFromStudents();
		
		passFailLogger.info(" getAllStudents : {}", allStudents.size());
		
		
		return filterPassFailStudentsToApplyGraceTo(passFailStudentList,configurationMap,allStudents);
		
	}

	private List<PassFailExamBean> filterPassFailStudentsToApplyGraceTo(List<PassFailExamBean> passFailStudentList,
			HashMap<String, ProgramSubjectMappingExamBean> configurationMap,
			Map<String, StudentExamBean> allStudents) {

		passFailLogger.info(" filtering to apply grace from list : {}", passFailStudentList.size());
		
		return passFailStudentList.stream().filter(pf -> {

			try {
				return checkGraceApplicableConditions(configurationMap, allStudents, pf);
			} catch (Exception e) {
				passFailLogger.info("{} {} not considering this error : {}", pf.getSapid(), pf.getSubject(), e.getMessage());
				return false;
			}
		}).collect(Collectors.toList());

	}

	private boolean checkGraceApplicableConditions(HashMap<String, ProgramSubjectMappingExamBean> configurationMap,
			Map<String, StudentExamBean> allStudents, PassFailExamBean pf) {
		
		passFailLogger.info("{} {}  passfail bean : {}", pf.getSapid(), pf.getSubject(), pf);
		
		StudentExamBean st = allStudents.get(pf.getSapid());
		
		String key = st.getProgram() + "-" + pf.getSubject() + "-" + st.getPrgmStructApplicable();
		
		passFailLogger.info("{} {} key : {}", pf.getSapid(), pf.getSubject(), key);
		
		
		ProgramSubjectMappingExamBean ps = configurationMap.get(key);
		
		boolean isTotalGreater = Integer.valueOf(pf.getTotal()) >= (ps.getPassScore() - ps.getMaxGraceMarks());
		boolean isGraceApplicable = "Y".equalsIgnoreCase(ps.getIsGraceApplicable());
		boolean isPassScoreGreater = ps.getPassScore() > Integer.valueOf(pf.getTotal());
		
		if (!(isGraceApplicable && isTotalGreater && isPassScoreGreater)) {
			passFailLogger.info("{} {} isGraceApplicable : {} isTotalGreater : {}  isPassScoreGreater : {}", pf.getSapid(),
					pf.getSubject(), isGraceApplicable, isTotalGreater, isPassScoreGreater);
			return false;
		}
		
		if ("BAJAJ".equalsIgnoreCase(st.getConsumerType()) && 
				!("Jul2014".equalsIgnoreCase(st.getPrgmStructApplicable()) 
						&& "DBM".equalsIgnoreCase(st.getProgram()))) {
			passFailLogger.info("{} {} consumerType : {}, prgmStructApplicable : {}, program : {}",pf.getSapid(), pf.getSubject(),
					st.getConsumerType(), st.getPrgmStructApplicable(),st.getProgram());
			return false;
		}
		
		if("Copy Case".equalsIgnoreCase(pf.getRemarks())) {
			passFailLogger.info("{} {} found copy case!",pf.getSapid(),pf.getSubject());
			return false;
		}
		
		//required by grace logic
		pf.setPrgmStructApplicable(st.getPrgmStructApplicable());
		
		return true;
	}
	
	@Override
	public Map<String, String> getLatestYear(List<PassFailExamBean> passFailStudentList) throws Exception{
		Map<String, String> latestYearMonth=new HashMap<String, String>();
		if(passFailStudentList.size()==1) {
			latestYearMonth.put("month", passFailStudentList.get(0).getResultProcessedMonth());
			latestYearMonth.put("year", passFailStudentList.get(0).getResultProcessedYear());
		}
		else {
			Date date = new Date((2001-1900), 7, 1);
			for (PassFailExamBean bean : passFailStudentList) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
				LocalDate localDate = LocalDate.parse("01 " + bean.getResultProcessedMonth() + " " + bean.getResultProcessedYear(), formatter);
				Date BeanDate = convertToLocalDateToDate(localDate);
				if (BeanDate.compareTo(date) > 0) {
					date = BeanDate;
					latestYearMonth.put("month",bean.getResultProcessedMonth());
					latestYearMonth.put("year", bean.getResultProcessedYear());
				}
				
			}
			
		}
		
		return latestYearMonth;
	}
	
	 private static java.util.Date convertToLocalDateToDate(LocalDate localDate) {
	        return java.util.Date.from(localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
	    }
}
