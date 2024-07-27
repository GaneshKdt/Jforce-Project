package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FlagBean;
import com.nmims.beans.RedisPassFailBean;
import com.nmims.beans.RedisStudentBean;
import com.nmims.beans.RedisStudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.daos.RedisResultsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.repository.FlagsRepositoryForRedisImpl;
import com.nmims.repository.ResultsRepositoryForRedisImpl;

@Service("redisResultsStoreService")
@SuppressWarnings("rawtypes")
public class RedisResultsStoreService {

	@Autowired
	private ResultsRepositoryForRedisImpl repository;

	@Autowired
	private FlagsRepositoryForRedisImpl flagRepo;

	@Autowired
	private RedisResultsDAO redisResultsDAO;

	@Autowired
	StudentMarksDAO studentMarksDao;
	
	@Autowired
	private RedisResultsOrderService redisResultsOrderService;
	
	private static Map<String, ExamOrderExamBean> examOrderMap;


	private static String onlineMostRecentResultPeriod;
	private static String offlineMostRecentResultPeriod;

	private static String onlineDeclareDate;
	private static String offlineDeclareDate;
	
	private static final Logger logger = LoggerFactory.getLogger("resultsStore");

	private static List<String> enrollmentYears = Arrays.asList("2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022");
	
	public void deleteAllResultsDataFromRedis() {
		try {
			Set<String> keys = repository.getAllKeys();
			repository.deleteKeys(keys);
		}catch (Exception e) {
			
		}
	}
	
	public List<String> fetchAndStoreResultsInRedis(String examYear, String examMonth) throws Exception {
		
		List<String> errors = new ArrayList<String>();
		updateStaticDataBeforeUpsert();
    	for (String enrollmentYear : enrollmentYears) {
    		errors.addAll(storeResultsinRedisCacheForYear(examYear, examMonth, enrollmentYear));
    	}
    	return errors;
	}
	
	private void updateStaticDataBeforeUpsert() {
		// Pre-store values into cache variables as these values are supposed to be the same throughout the process 
		onlineMostRecentResultPeriod = studentMarksDao.getMostRecentResultPeriod();
		offlineMostRecentResultPeriod = studentMarksDao.getMostRecentOfflineResultPeriod();
		onlineDeclareDate = studentMarksDao.getRecentExamDeclarationDate();
		offlineDeclareDate = studentMarksDao.getRecentOfflineExamDeclarationDate();
		examOrderMap = redisResultsDAO.getAllExamOrderMap();
	}
	
	public List<String> storeResultsinRedisCacheForYear(String examYear, String examMonth, String enrollmentYear) throws Exception {

		List<String> errors = new ArrayList<String>();
		
		logger.info("Starting Fetch for enrollment year " + enrollmentYear);
		Map<String, List<RedisStudentMarksBean>> allMarks;
		Map<String, List<RedisPassFailBean>> passFailMarks;
		Map<String, List<RedisStudentMarksBean>> currentCycleMarks;
		Map<String, List<RedisStudentMarksBean>> currentCycleAssignmentMarks;
		Map<String, RedisStudentBean> students;
		try {
			allMarks = redisResultsDAO.getAllStudentMarksMapByRegYear(enrollmentYear);
			passFailMarks = redisResultsDAO.getAllPassFailMarksMapByRegYear(enrollmentYear);
			currentCycleMarks = redisResultsDAO.getAllCurrentCycleMarksMapByRegYear(examYear, examMonth, enrollmentYear);
			currentCycleAssignmentMarks = redisResultsDAO.getAllStudentsMostRecentAssignmentMarks(examYear, examMonth, enrollmentYear);

			students = redisResultsDAO.getAllStudentsByRegYear(enrollmentYear);	
		}catch (Exception e) {
			logger.info("Error Fetching students for Enrollment Year " + enrollmentYear);
			throw new Exception("Error Fetching students for Enrollment Year " + enrollmentYear);
			
		}
		
		
		logger.info("Finished Fetch for enrollment year " + enrollmentYear + " Number of records fetched " + students.size());
		logger.info("Starting redis save for enrollment year " + enrollmentYear); 
		
		for (Map.Entry<String, RedisStudentBean> studentEntry : students.entrySet()) {
			RedisStudentBean student = studentEntry.getValue();
			String sapid = studentEntry.getKey();
						
			errors.addAll(checkAndStoreResultsInRedis(allMarks.get(sapid), passFailMarks.get(sapid), currentCycleMarks.get(sapid), currentCycleAssignmentMarks.get(sapid), student));

			// Some cleanup because of memory issues on local environment. This shouldnt happen on prod because of more ram but will need more testing
			allMarks.remove(sapid);
			passFailMarks.remove(sapid);
			currentCycleMarks.remove(sapid);
		}

		logger.info("Finished redis save for enrollment year " + enrollmentYear);
	
		return errors;
	}
	
	private List<String> checkAndStoreResultsInRedis(
		List<RedisStudentMarksBean> allMarksList, List<RedisPassFailBean> passFailList, List<RedisStudentMarksBean> currentCycleMarks, List<RedisStudentMarksBean> currentCycleAssignmentMarks, RedisStudentBean student) {

		List<String> errors = new ArrayList<String>();
		
		String mostRecentResultPeriod = "";
		String declareDate = "";
		if("Online".equals(student.getExamMode())){
			mostRecentResultPeriod = onlineMostRecentResultPeriod;
			declareDate = onlineDeclareDate;
		}else{
			mostRecentResultPeriod = offlineMostRecentResultPeriod;
			declareDate = offlineDeclareDate;
		}
		
		StudentsDataInRedisBean bean = new StudentsDataInRedisBean();
		bean.setSapid(student.getSapid());

		Map<String, List> result_data = new HashMap<String, List>();
		try {

			List<RedisStudentMarksBean> currentCycleMarksForStudent = filterRedisMarksData(currentCycleMarks, student.getExamMode(), errors);

			// Only store results for students who have registered for current cycle.
			
			//Commented By Shiv as we will serving inner results pages via cache
			if(currentCycleMarksForStudent == null) {
				return errors;
			}

			List<RedisStudentMarksBean> allMarksForStudent = filterRedisMarksData(allMarksList, student.getExamMode(), errors);
			List<RedisPassFailBean> passFailMarksForStudent = filterRedisPassFailData(passFailList, student.getExamMode(), errors);
			List<RedisStudentMarksBean> assignmentMarksForStudent = filterRedisAssignmentMarksData(currentCycleAssignmentMarks, student.getExamMode(), errors);
			
			//added by Vilpesh on 2022-04-04, to order data in list and then store in REDIS
			allMarksForStudent = redisResultsOrderService.orderMarksHistoryList(allMarksForStudent);
			currentCycleMarksForStudent = redisResultsOrderService.orderMarksList(currentCycleMarksForStudent);
			passFailMarksForStudent = redisResultsOrderService.orderPassFailList(passFailMarksForStudent);

			//result_data.put("passFailStatus", Arrays.asList("", passFailMarksForStudent, passFailMarksForStudent.size()));//commented by Vilpesh on 2021-11-22
			result_data.put("passFailStatus", Arrays.asList("", passFailMarksForStudent, (null == passFailMarksForStudent ? 0 : passFailMarksForStudent.size())));
			result_data.put("studentMarksHistory", allMarksForStudent);
			result_data.put("studentMarksList", currentCycleMarksForStudent);
			result_data.put("mostRecentResultPeriod", Arrays.asList(mostRecentResultPeriod));
			result_data.put("declareDate", Arrays.asList(declareDate));
			result_data.put("size", Arrays.asList(currentCycleMarksForStudent == null ? 0 : currentCycleMarksForStudent.size()));
			result_data.put("studentDetails", Arrays.asList(student));
			result_data.put("assignmentMarks", assignmentMarksForStudent);
			bean.setResultsData(result_data);
		}catch (Exception e) {
			
			String error = "Error filtering data for student " + student.getSapid() + " Error : " + e.getMessage();
			logger.error(error);
			errors.add(error);
			return errors;
		}
		
		try {
			repository.save(bean);	
		}catch (Exception e) {
			
			String error = "Error saving data for student " + student.getSapid() + " Error : " + e.getMessage();
			logger.error(error);
			errors.add(error);
			return errors;
		}
		return errors;
	}
	
	private List<RedisPassFailBean> filterRedisPassFailData(List<RedisPassFailBean> marksForStudent, String examMode, List<String> errors) {
		List<RedisPassFailBean> filteredList = new ArrayList<RedisPassFailBean>();

		// Dont process if null
		if(marksForStudent == null) {
			return null;
		}
		for (RedisPassFailBean bean : marksForStudent) {

			// Get the applicable exam order for this subject
			String monthYear = bean.getResultProcessedMonth() + bean.getResultProcessedYear();
			ExamOrderExamBean eo = examOrderMap.get(monthYear);
			
			// If order is found
			if(eo != null) {
				
				// Different flags checked for different exam modes
				if("Online".equals(examMode)) {
					if("Y".equals(eo.getLive())) {
						filteredList.add(bean);
						continue;
					} else {
						errors.add("Student found with not live (ON) " + bean);
						logger.info("Student found with not live (ON) " + bean);
					}
				} else {
					if("Y".equals(eo.getOflineResultslive())) {
						filteredList.add(bean);
						continue;
					} else {
						errors.add("Student found with not live (OFF) " + bean);
						logger.info("Student found with not live (OFF) " + bean);
					}
				}	
			}
			errors.add("Bean not following any exam order found " + bean);
			logger.info("Bean not following any exam order found " + bean);
		}
		return filteredList;
	}


	private List<RedisStudentMarksBean> filterRedisMarksData(List<RedisStudentMarksBean> marksForStudent, String examMode, List<String> errors) {
		List<RedisStudentMarksBean> filteredList = new ArrayList<RedisStudentMarksBean>();
		
		// Dont process if null
		if(marksForStudent == null) {
			return null;
		}
		
		for (RedisStudentMarksBean bean : marksForStudent) {
			
			// Get the applicable exam order for this subject
			String monthYear = bean.getMonth() + bean.getYear();
			ExamOrderExamBean eo = examOrderMap.get(monthYear);
			
			// If order is found
			if(eo != null) {
				
				// Different flags checked for different exam modes
				if("Online".equals(examMode)) {
					if("Y".equals(eo.getLive())) {
						filteredList.add(bean);
						continue;
					} else {
						errors.add("Student found with not live (ON) " + bean);
						logger.info("Student found with not live (ON) " + bean);
					}
				} else {
					if("Y".equals(eo.getOflineResultslive())) {
						filteredList.add(bean);
						continue;
					} else {
						errors.add("Student found with not live (OFF) " + bean);
						logger.info("Student found with not live (OFF) " + bean);
					}
				}	
			}
			errors.add("Bean not following any exam order found " + bean);
			logger.info("Bean not following any exam order found " + bean);
		}
		return filteredList;
	}
	
	private List<RedisStudentMarksBean> filterRedisAssignmentMarksData(List<RedisStudentMarksBean> marksForStudent, String examMode, List<String> errors) {
		List<RedisStudentMarksBean> filteredList = new ArrayList<RedisStudentMarksBean>();
		
		// Dont process if null
		if(marksForStudent == null) {
			return null;
		}
		
		for (RedisStudentMarksBean bean : marksForStudent) {
			
			// Get the applicable exam order for this subject
			String monthYear = bean.getMonth() + bean.getYear();
			ExamOrderExamBean eo = examOrderMap.get(monthYear);
			
			// If order is found
			if(eo != null) {
				
				// Different flags checked for different exam modes
				if("Online".equals(examMode)) {
					if("Y".equals(eo.getAssignmentMarksLive())) {
						filteredList.add(bean);
						continue;
					} else {
						errors.add("Student found with not live (ASSGN) " + bean);
						logger.info("Student found with not live (ASSGN) " + bean);
					}
				}
			}
			errors.add("Bean not following any exam order found " + bean);
			logger.info("Bean not following any exam order found " + bean);
		}
		return filteredList;
	}
	
	public void setFlagInRedis(String key, String value) {
		FlagBean bean = new FlagBean();
		bean.setKey(key);
		bean.setValue(value);
		flagRepo.save(bean);
	}
	
	public List<FlagBean> getAllFalgs() {
		//return flagRepo.getAll();
		
		List<FlagBean> bean = null;
		try {
			bean = flagRepo.getAll();
			return bean;
		} catch (Exception ex) {
			
			logger.error("RedisResultsStoreService : getAllFalgs : error : " + ex.getMessage());
			throw ex;
		} finally {
			logger.info("RedisResultsStoreService : getAllFalgs : bean : " + bean);
		}
		
		//return bean; 
	}
	
	
	public FlagBean getFlagBeanByKey(String key) {
		
		FlagBean bean = new FlagBean();
		try {
			bean = flagRepo.getByKey(key);
			return bean;
		} catch (Exception ex) {
			
			logger.error("RedisResultsStoreService : getFlagBeanByKey : error : " + ex.getMessage());
			throw ex;
		} finally {
			logger.info("RedisResultsStoreService : getFlagBeanByKey : bean : " + bean);
		}
		
	}
	
	
	public StudentsDataInRedisBean getRedisResultDataBySapid(String sapid) {
		//return repository.findBySapid(sapid);
		
		StudentsDataInRedisBean bean = null;
		try {
			bean = repository.findBySapid(sapid);
		} catch (Exception ex) {
			
			logger.error("RedisResultsStoreService : getRedisResultDataBySapid : error : " + ex.getMessage());
		} finally {
			logger.info("RedisResultsStoreService : getRedisResultDataBySapid : bean : " + bean);
		}
		return bean;
	}

	public List<String> fetchAndStoreResultsInRedisForSingleStudent(String examYear, String examMonth, String sapid) {
		RedisStudentBean student = redisResultsDAO.getStudentBySapid(sapid);
		List<RedisStudentMarksBean> currentCycleMarks = redisResultsDAO.getCurrentCycleMarksForSapid(examYear, examMonth, sapid);
		List<RedisStudentMarksBean> allMarksList = redisResultsDAO.getStudentMarksForSapid(sapid);
		List<RedisPassFailBean> passFailList = redisResultsDAO.getPassFailMarksForSapid(sapid);
		List<RedisStudentMarksBean> currentCycleAssignmentMarks = redisResultsDAO.getAStudentsMostRecentAssignmentMarks(examYear, examMonth, sapid);
		updateStaticDataBeforeUpsert();
		return checkAndStoreResultsInRedis(allMarksList, passFailList, currentCycleMarks, currentCycleAssignmentMarks, student);
	}
}
