/**
 * 
 */
package com.nmims.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FlagBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.RedisPassFailBean;
import com.nmims.beans.RedisStudentBean;
import com.nmims.beans.RedisStudentMarksBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.services.RedisResultsOrderService;
import com.nmims.services.RedisResultsStoreService;

/**
 * @author vil_m
 *
 */
@Service("resultsFromRedisHelper")
public class ResultsFromRedisHelper {
	
	public static final String EXAM_STAGE_TEE = "TEE";//
	
	public static final String KEY_STUDENT_DETAILS = "studentDetails";
	public static final String KEY_DECLARE_DATE = "declareDate";
	public static final String KEY_SIZE = "size";
	public static final String KEY_MOST_RECENT_RESULT_PERIOD = "mostRecentResultPeriod";
	public static final String KEY_STUDENT_MARKSLIST = "studentMarksList";
	public static final String KEY_PASSFAIL_STATUS = "passFailStatus";
	public static final String KEY_STUDENT_MARKS_HISTORY = "studentMarksHistory";
	public static final String KEY_ASSIGNMENT_MARKS = "assignmentMarks";
	
	protected static final String FLAG_MOVING_RESULTS_TO_CACHE = "movingResultsToCache";
	protected static final String FLAG_SHOW_RESULTS_FROM_CACHE = "showResultsFromCache";
	public static final String VALUE_Y = "Y";
	public static final String VALUE_N = "N";
	
	public static final Logger logger = Logger.getLogger(ResultsFromRedisHelper.class);
	
	@Autowired
	private RedisResultsStoreService redisResultsStoreService;
	
	@Autowired
	private RedisResultsOrderService redisResultsOrderService;
	
	public ResultsFromRedisHelper() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected RedisResultsStoreService getRedisResultsStoreService() {
		return redisResultsStoreService;
	}
	
	protected List<FlagBean> getAllFlags() {
		return this.getRedisResultsStoreService().getAllFalgs();
	}

	protected boolean checkIfMovingResultsToCache() {
		boolean isMoving = Boolean.FALSE;
		List<FlagBean> flagsList = null;

		flagsList = this.getAllFlags();

		for (FlagBean f : flagsList) {
			if (FLAG_MOVING_RESULTS_TO_CACHE.equals(f.getKey())) {
				if (VALUE_Y.equalsIgnoreCase(f.getValue())) {
					isMoving = Boolean.TRUE;
					break;
				}
			}
		}
		logger.info("ResultsFromRedisHelper : checkIfMovingResultsToCache : " + isMoving);
		return isMoving;
	}

	protected boolean checkNotMovingResultsToCache() {
		boolean isNotMoving = Boolean.FALSE;
		List<FlagBean> flagsList = null;

		flagsList = this.getAllFlags();

		for (FlagBean f : flagsList) {
			if (FLAG_MOVING_RESULTS_TO_CACHE.equals(f.getKey())) {
				if (VALUE_N.equalsIgnoreCase(f.getValue())) {
					isNotMoving = Boolean.TRUE;
					break;
				}
			}
		}
		logger.info("ResultsFromRedisHelper : checkNotMovingResultsToCache : " + isNotMoving);
		return isNotMoving;
	}
	
	public boolean sendingResultsToCache() {
		return this.checkIfMovingResultsToCache();
	}
	
	public boolean notSendingResultsToCache() {
		return this.checkNotMovingResultsToCache();
	}

	protected boolean checkIfShowResultsFromCache() {
		boolean isResultsFromCache = Boolean.FALSE;
		List<FlagBean> flagsList = null;

		flagsList = this.getAllFlags();

		for (FlagBean f : flagsList) {
			if (FLAG_SHOW_RESULTS_FROM_CACHE.equals(f.getKey())) {
				if (VALUE_Y.equalsIgnoreCase(f.getValue())) {
					isResultsFromCache = Boolean.TRUE;
					break;
				}
			}
		}
		logger.info("ResultsFromRedisHelper : checkIfShowResultsFromCache : " + isResultsFromCache);
		return isResultsFromCache;
	}

	protected boolean checkNotShowResultsFromCache() {
		boolean notFromCache = Boolean.FALSE;
		List<FlagBean> flagsList = null;

		flagsList = this.getAllFlags();

		for (FlagBean f : flagsList) {
			if (FLAG_SHOW_RESULTS_FROM_CACHE.equals(f.getKey())) {
				if (VALUE_N.equalsIgnoreCase(f.getValue())) {
					notFromCache = Boolean.TRUE;
					break;
				}
			}
		}
		logger.info("ResultsFromRedisHelper : checkNotShowResultsFromCache : " + notFromCache);
		return notFromCache;
	}
	
	public boolean displayResultsFromCache() {
		return this.checkIfShowResultsFromCache();
	}
	
	public boolean notDisplayResultsFromCache() {
		return this.checkNotShowResultsFromCache();
	}

	public boolean readFromCache() {
		Boolean readfrom = Boolean.FALSE;

		if (this.checkNotMovingResultsToCache() && this.checkIfShowResultsFromCache()) {
			readfrom = Boolean.TRUE;
		}
		logger.info("ResultsFromRedisHelper : readFromCache : " + readfrom);
		return readfrom;
	}

	protected StudentsDataInRedisBean fetchResultsForSapId(String sapId) {
		StudentsDataInRedisBean redisBean = null;

		// from Redis, fetch result for SapId
		redisBean = this.getRedisResultsStoreService().getRedisResultDataBySapid(sapId);

		if (null == redisBean) {
			logger.info("ResultsFromRedisHelper : fetchResultsForSapId : Results for SapId (" + sapId
					+ ")  : EMPTY/Null data from REDIS " + redisBean);
		} else {
			logger.info("ResultsFromRedisHelper : fetchResultsForSapId : Results for SapId (" + sapId + ")  : "
					+ redisBean);
		}

		return redisBean;
	}

	protected Map<String, List> fetchResults(String sapId) {
		StudentsDataInRedisBean redisBean = null;
		Map<String, List> redisMap = null;

		redisBean = this.fetchResultsForSapId(sapId);

		if (null != redisBean) {
			if (sapId.equalsIgnoreCase(redisBean.getSapid())) {
				logger.info(
						"ResultsFromRedisHelper : fetchResults : Results for SapId (" + sapId + ")  : " + redisBean);
				redisMap = redisBean.getResultsData();
			} else {
				logger.error("ResultsFromRedisHelper : fetchResults : Results for SapId (" + sapId
						+ ")  : Wrong SapId data " + redisBean);
			}
		}

		return redisMap;
	}

	protected StudentExamBean extractStudent(Map<String, List> redisMap) {
		RedisStudentBean redisBean = null;
		StudentExamBean student = null;
		
		if(null != redisMap.get(KEY_STUDENT_DETAILS)) {
			redisBean = (RedisStudentBean) redisMap.get(KEY_STUDENT_DETAILS).get(0);
			student = new StudentExamBean();
			student.setConsumerType(redisBean.getConsumerType());
			student.setProgram(redisBean.getProgram());
			student.setPrgmStructApplicable(redisBean.getPrgmStructApplicable());
			student.setExamMode(redisBean.getExamMode());
			student.setProgramType(redisBean.getProgramType());
	
			student.setFirstName(redisBean.getFirstName());
			student.setLastName(redisBean.getLastName());
			student.setEmailId(redisBean.getEmailId());
			student.setMobile(redisBean.getMobile());
			student.setCity(redisBean.getCity());
		} else {
			logger.error("ResultsFromRedisHelper : extractStudent : Map with Key (" + KEY_STUDENT_DETAILS
					+ ")  value : " + redisMap.get(KEY_STUDENT_DETAILS));
		}

		return student;
	}

	protected Object extractDeclareDate(Map<String, List> redisMap) {
		Object obj = null;
		if(null != redisMap.get(KEY_DECLARE_DATE)) {
			obj = redisMap.get(KEY_DECLARE_DATE);
		} else {
			logger.error("ResultsFromRedisHelper : extractDeclareDate : Map with Key (" + KEY_DECLARE_DATE
					+ ")  value : " + redisMap.get(KEY_DECLARE_DATE));
		}
		return obj;
	}

	protected Integer extractSize(Map<String, List> redisMap) {
		Integer sizeStr = null;
		
		if(null != redisMap.get(KEY_SIZE)) {
			sizeStr = (Integer) redisMap.get(KEY_SIZE).get(0);
		} else {
			logger.error("ResultsFromRedisHelper : extractSize : Map with Key (" + KEY_SIZE
					+ ")  value : " + redisMap.get(KEY_SIZE));
		}
		return sizeStr;
	}

	protected Object extractMostRecentResultPeriod(Map<String, List> redisMap) {
		Object obj = null;
		if(null != redisMap.get(KEY_MOST_RECENT_RESULT_PERIOD)) {
			obj = redisMap.get(KEY_MOST_RECENT_RESULT_PERIOD).get(0);
		} else {
			logger.error("ResultsFromRedisHelper : extractMostRecentResultPeriod : Map with Key (" + KEY_MOST_RECENT_RESULT_PERIOD
					+ ")  value : " + redisMap.get(KEY_MOST_RECENT_RESULT_PERIOD));
		}
		return obj;
	}

	protected List<StudentMarksBean> extractMarksList(Map<String, List> redisMap) {
		List<RedisStudentMarksBean> list = null;
		List<StudentMarksBean> listBean = null;
		StudentMarksBean studentMarks = null;

		if(null != redisMap.get(KEY_STUDENT_MARKSLIST)) {
			list = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKSLIST);
			//list = redisResultsOrderService.orderMarksList(list); //order beans in list after fetch from REDIS, commented working.
			listBean = new ArrayList<StudentMarksBean>();
			if(null != list) {
				for (RedisStudentMarksBean redisStudMarksBean : list) {
					studentMarks = new StudentMarksBean();
		
					studentMarks.setSem(redisStudMarksBean.getSem());
					studentMarks.setSubject(redisStudMarksBean.getSubject());
					studentMarks.setWritenscore(redisStudMarksBean.getWritenscore());
					studentMarks.setAssignmentscore(redisStudMarksBean.getAssignmentscore());
					studentMarks.setMcq(redisStudMarksBean.getMcq());
					studentMarks.setPart4marks(redisStudMarksBean.getPart4marks());
					studentMarks.setRemarks(redisStudMarksBean.getRemarks());
					
					//required for /exam/studentSelfMarksheet
					studentMarks.setSapid(redisStudMarksBean.getSapid());
					studentMarks.setYear(redisStudMarksBean.getYear());
					studentMarks.setMonth(redisStudMarksBean.getMonth());
					
					listBean.add(studentMarks);
				}
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractMarksList : Map with Key (" + KEY_STUDENT_MARKSLIST
					+ ")  value : " + redisMap.get(KEY_STUDENT_MARKSLIST));
		}

		return listBean;
	}

	protected List<PassFailExamBean> extractPassfailStatus(Map<String, List> redisMap) {
		List<RedisPassFailBean> list = null;
		List<PassFailExamBean> listBean = null;
		PassFailExamBean passFailBean = null;

		if(null != redisMap.get(KEY_PASSFAIL_STATUS)) {
			list = (List<RedisPassFailBean>) redisMap.get(KEY_PASSFAIL_STATUS).get(1);
			//list = redisResultsOrderService.orderPassFailList(list);//order beans in list after fetch from REDIS, commented working.
			listBean = new ArrayList<PassFailExamBean>();
			if(null != list) {
				for (RedisPassFailBean redisPFBean : list) {
					passFailBean = new PassFailExamBean();
		
					passFailBean.setSem(redisPFBean.getSem());
					passFailBean.setSubject(redisPFBean.getSubject());
					passFailBean.setWrittenscore(redisPFBean.getWrittenscore());
					passFailBean.setWrittenYear(redisPFBean.getWrittenYear());
					passFailBean.setWrittenMonth(redisPFBean.getWrittenMonth());
					passFailBean.setAssignmentscore(redisPFBean.getAssignmentscore());
					passFailBean.setAssignmentYear(redisPFBean.getAssignmentYear());
					passFailBean.setAssignmentMonth(redisPFBean.getAssignmentMonth());
					passFailBean.setRemarks(redisPFBean.getRemarks());
					passFailBean.setGracemarks(redisPFBean.getGracemarks());
					passFailBean.setTotal(redisPFBean.getTotal());
					passFailBean.setIsPass(redisPFBean.getIsPass());
					
					passFailBean.setResultProcessedMonth(redisPFBean.getResultProcessedMonth());
					passFailBean.setResultProcessedYear(redisPFBean.getResultProcessedYear());
					
					//required for /exam/studentSelfMarksheet
					passFailBean.setSapid(redisPFBean.getSapid());
					passFailBean.setProgram(redisPFBean.getProgram());
		
					listBean.add(passFailBean);
				}
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractPassfailStatus : Map with Key (" + KEY_PASSFAIL_STATUS
					+ ")  value : " + redisMap.get(KEY_PASSFAIL_STATUS));
		}

		return listBean;
	}

	protected List<StudentMarksBean> extractMarksHistoryList(Map<String, List> redisMap) {
		List<RedisStudentMarksBean> list = null;
		List<StudentMarksBean> listBean = null;
		StudentMarksBean studentMarks2 = null;
		
		if(null != redisMap.get(KEY_STUDENT_MARKS_HISTORY)) {
			list = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKS_HISTORY);
			//list = redisResultsOrderService.orderMarksHistoryList(list);//order beans in list after fetch from REDIS, commented working.
			listBean = new ArrayList<StudentMarksBean>();
			if(null != list) {
				for (RedisStudentMarksBean redisStudMarksBean2 : list) {
					studentMarks2 = new StudentMarksBean();
					
					studentMarks2.setSem(redisStudMarksBean2.getSem());
					studentMarks2.setSubject(redisStudMarksBean2.getSubject());
					studentMarks2.setYear(redisStudMarksBean2.getYear());
					studentMarks2.setMonth(redisStudMarksBean2.getMonth());
					studentMarks2.setWritenscore(redisStudMarksBean2.getWritenscore());
					studentMarks2.setAssignmentscore(redisStudMarksBean2.getAssignmentscore());
					studentMarks2.setGracemarks(redisStudMarksBean2.getGracemarks());
					
					//In EXAM, studentSide Marksheet code needs below for PF processing.
					studentMarks2.setSapid(redisStudMarksBean2.getSapid());
					studentMarks2.setStudentname(redisStudMarksBean2.getStudentname());
					studentMarks2.setGrno(redisStudMarksBean2.getGrno());
					studentMarks2.setProgram(redisStudMarksBean2.getProgram());
					studentMarks2.setExamorder(redisStudMarksBean2.getExamorder());
					
					listBean.add(studentMarks2);
				}
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractMarksHistoryList : Map with Key (" + KEY_STUDENT_MARKS_HISTORY
					+ ")  value : " + redisMap.get(KEY_STUDENT_MARKS_HISTORY));
		}

		return listBean;
	}
	
	/**
	 * Results are stored in Redis specific classes. Extract, pack in, send this
	 * Data. eg. from RedisPassFailExamBean extract data and store in Map as
	 * PassFailExamBean etc.
	 * 
	 * @param examStage exam type/point at which exam taken.
	 * @param sapId     Students Id.
	 * @return Map with data.
	 */
	public Map<String, Object> fetchResultsFromRedis(String examStage, String sapId) {
		Integer sizeStr = null;
		StudentExamBean student = null;
		List<PassFailExamBean> listBean = null;
		List<StudentMarksBean> listStudentMarksBean = null;
		List<StudentMarksBean> listStudentMarksBean2 = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);
			
			if(null != redisMap) {
				student = this.extractStudent(redisMap);
				simpleMap.put(KEY_STUDENT_DETAILS, student);
				simpleMap.put(KEY_DECLARE_DATE, this.extractDeclareDate(redisMap));
	
				sizeStr = this.extractSize(redisMap);
				simpleMap.put(KEY_SIZE, sizeStr);
				simpleMap.put(KEY_MOST_RECENT_RESULT_PERIOD, this.extractMostRecentResultPeriod(redisMap));
	
				listStudentMarksBean = this.extractMarksList(redisMap);
				simpleMap.put(KEY_STUDENT_MARKSLIST, listStudentMarksBean);
	
				listBean = this.extractPassfailStatus(redisMap);
				simpleMap.put(KEY_PASSFAIL_STATUS, listBean);
	
				listStudentMarksBean2 = this.extractMarksHistoryList(redisMap);
				simpleMap.put(KEY_STUDENT_MARKS_HISTORY, listStudentMarksBean2);
			}

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchResultsFromRedis : " + ex.getMessage());
		} finally {
			// System.out.println("ResultsFromRedisHelper : fetchResultsFromRedis : Results
			// (SapId, Size) (" + sapId + ","
			// + simpleMap.size() + ")");
			logger.info("ResultsFromRedisHelper : fetchResultsFromRedis : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}
	
	public Map<String, Object> fetchOnlyMarksHistory(String examStage, String sapId) {
		List<StudentMarksBean> listStudentMarksBean2 = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();
			
			redisMap = this.fetchResults(sapId);

			if(null != redisMap) {
				listStudentMarksBean2 = this.extractMarksHistoryList(redisMap);
			}
			simpleMap.put(KEY_STUDENT_MARKS_HISTORY, listStudentMarksBean2);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchOnlyMarksHistory : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyMarksHistory : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}

	@Deprecated
	public Map<String, Object> fetchMarksRecordsForStudent(final String examStage, final String sem, final String sapId,
			final String month, final String year) {
		List<StudentMarksBean> listStudentMarksBean = null;
		List<StudentMarksBean> list = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);

			if(null != redisMap) {
				listStudentMarksBean = this.extractMarksList(redisMap);
				list = new ArrayList<StudentMarksBean>();
				for (StudentMarksBean bean : listStudentMarksBean) {
	
					//&& month.equalsIgnoreCase(bean.getMonth()) && year.equalsIgnoreCase(bean.getYear())
					if (null != bean.getSem() && null != bean.getSapid()
							&& (sem.equalsIgnoreCase(bean.getSem()) && sapId.equalsIgnoreCase(bean.getSapid()))) {
						list.add(bean);
					}
				}
			}
			simpleMap.put(KEY_STUDENT_MARKSLIST, list);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchMarksRecordsForStudent : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchMarksRecordsForStudent : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}
	
	public List<StudentMarksBean> fetchOnlyMarksHistory(final Map<String, Object> simpleMap, String examStage,
			String sapId) {
		List<StudentMarksBean> listStudentMarksBean2 = null;

		try {
			if (null != simpleMap) {
				listStudentMarksBean2 = (List<StudentMarksBean>) simpleMap.get(KEY_STUDENT_MARKS_HISTORY);
				if (null != listStudentMarksBean2) {
					logger.info("ResultsFromRedisHelper : fetchOnlyMarksHistory : Results (SapId, Size) (" + sapId + ","
							+ listStudentMarksBean2.size() + ")");
				}
			}
		} catch (Exception ex) {
			listStudentMarksBean2 = null;
			logger.error("ResultsFromRedisHelper : fetchOnlyMarksHistory : " + ex.getMessage());
		} finally {

		}
		return listStudentMarksBean2;
	}
	
	/**
	 * In MarksHistory pulled from REDIS, checks for the first row that matches.
	 * @param simpleMap
	 * @param examStage
	 * @param sem
	 * @param sapId
	 * @param month
	 * @param year
	 * @return null if any exception or Map or List empty, true/false if found.
	 */
	public Boolean checkMarksHistoryForStudent(final Map<String, Object> simpleMap, final String examStage,
			final String sem, final String sapId, final String month, final String year) {
		List<StudentMarksBean> listBean2 = null;
		Boolean attempted = Boolean.FALSE;
		try {
			listBean2 = this.fetchOnlyMarksHistory(simpleMap, examStage, sapId);
			if (null != listBean2 && !listBean2.isEmpty()) {
				for (StudentMarksBean bean : listBean2) {
					// && sapId.equalsIgnoreCase(bean.getSapid())
					if ((null != bean.getSem() && null != bean.getMonth() && null != bean.getYear())
							&& (sem.equalsIgnoreCase(bean.getSem()) && month.equalsIgnoreCase(bean.getMonth())
									&& year.equalsIgnoreCase(bean.getYear()))) {
						attempted = Boolean.TRUE;
						logger.info(
								"ResultsFromRedisHelper : checkMarksHistoryForStudent : Found (SapId, Sem, Year, Month) ("
										+ sapId + "," + sem + "," + year + "," + month + ")");
						break;
					}
				}
			} else if (null == listBean2 || (null != listBean2 && listBean2.isEmpty())) {
				// In REDIS - student's Marks empty returned, so that db fetch happens.
				attempted = null;
			}
		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : checkMarksHistoryForStudent : " + ex.getMessage());
			attempted = null;
		} finally {
			logger.info("ResultsFromRedisHelper : checkMarksHistoryForStudent : Results (SapId, Attempted) (" + sapId
					+ "," + attempted + ")");
		}
		return attempted;
	}
	
	/**
	 * In MarksHistory pulled from REDIS, create List of matching rows.
	 * @param simpleMap
	 * @param examStage
	 * @param sem
	 * @param sapId
	 * @param month
	 * @param year
	 * @return  null if any exception or Map/List from REDIS empty, otherwise List of Marks.
	 */
	public List<StudentMarksBean> fetchMarksHistoryOnSem(final Map<String, Object> simpleMap, final String examStage,
			final String sem, final String sapId, final String month, final String year) {
		List<StudentMarksBean> listBean2 = null;
		List<StudentMarksBean> list = null;
		try {
			listBean2 = this.fetchOnlyMarksHistory(simpleMap, examStage, sapId);
			if (null != listBean2 && !listBean2.isEmpty()) {
				list = new ArrayList<StudentMarksBean>();
				for (StudentMarksBean bean : listBean2) {
					// month.equalsIgnoreCase(bean.getMonth()) &&
					// year.equalsIgnoreCase(bean.getYear())
					if ((null != bean.getSem() && null != bean.getSapid())
							&& (sem.equalsIgnoreCase(bean.getSem()) && sapId.equalsIgnoreCase(bean.getSapid()))) {
						list.add(bean);
						logger.info(
								"ResultsFromRedisHelper : fetchMarksHistoryOnSem : Adding (SapId, Sem, Subject, Year, Month) ("
										+ sapId + "," + sem + "," + bean.getSubject() + "," + bean.getYear() + ","
										+ bean.getMonth() + ")");
					}
				}
			} else if (null == listBean2 || (null != listBean2 && listBean2.isEmpty())) {
				// In REDIS - student's Marks empty returned, so that db fetch happens.
				list = null;
			}
		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchMarksHistoryOnSem : " + ex.getMessage());
			list = null;
		} finally {
		}
		return list;
	}
	
	/**
	 * Passfail data in List.
	 * @param simpleMap
	 * @param examStage
	 * @param sapId
	 * @return  null if any exception or Map/List from REDIS empty, otherwise List of Passfail.
	 */
	public List<PassFailExamBean> fetchOnlyPassfailList(final Map<String, Object> simpleMap, final String examStage,
			final String sapId) {
		List<PassFailExamBean> list = null;
		try {
			if (null != simpleMap && !simpleMap.isEmpty()) {
				list = (List<PassFailExamBean>) simpleMap.get(KEY_PASSFAIL_STATUS);
			}
		} catch (Exception ex) {
			list = null;
			logger.error("ResultsFromRedisHelper : fetchOnlyPassfailList : " + ex.getMessage());
		} finally {
			if (null != list) {
				logger.info("ResultsFromRedisHelper : fetchOnlyPassfailList : Results (SapId, List Size) (" + sapId
						+ "," + list.size() + ")");
			}
		}
		return list;
	}
	
	//keep this method fetchYearMonthFromPassfailForSemBoth, same named method will come from other branch -Vilpesh 20220628
	/**
	 * Overloaded method.
	 * @param sem
	 * @param listPassFailBean
	 * @return
	 */
	public Map<Integer, Set<String>> fetchYearMonthFromPassfailForSemBoth(final String sem,
			final List<PassFailExamBean> listPassFailBean) {
		Integer rowCount = 0;
		String yearMonthStr = null;
		Set<String> yearMonthSet = null;
		Map<Integer, Set<String>> yearMonthMap = null;
		if (null != listPassFailBean && !listPassFailBean.isEmpty()) {
			yearMonthMap = new HashMap<Integer, Set<String>>();
			yearMonthSet = new HashSet<String>();
			for (PassFailExamBean bean : listPassFailBean) {
				if (null != bean.getSem() && null != sem && sem.equals(bean.getSem())) {
					rowCount++; // Count - total subject passed or failed.
					if (ResultsFromRedisHelper.isNotBlank(bean.getAssignmentYear())
							&& ResultsFromRedisHelper.isNotBlank(bean.getAssignmentMonth())) {
						yearMonthStr = bean.getAssignmentYear() + "-" + bean.getAssignmentMonth();
						yearMonthSet.add(yearMonthStr);
					}
					if (ResultsFromRedisHelper.isNotBlank(bean.getWrittenYear())
							&& ResultsFromRedisHelper.isNotBlank(bean.getWrittenMonth())) {
						yearMonthStr = bean.getWrittenYear() + "-" + bean.getWrittenMonth();
						yearMonthSet.add(yearMonthStr);
					}
					
					if (ResultsFromRedisHelper.isNotBlank(bean.getResultProcessedMonth())
							&& ResultsFromRedisHelper.isNotBlank(bean.getResultProcessedYear())) {
						yearMonthStr = bean.getResultProcessedYear() + "-" + bean.getResultProcessedMonth();
						yearMonthSet.add(yearMonthStr);
					}
				}
			}
			yearMonthMap.put(rowCount, yearMonthSet);
			logger.info(
					"ResultsFromRedisHelper - fetchYearMonthFromPassfailForSemBoth - (Sem, Total Subject Passed/Failed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet.size() + ")");
		} else {
			logger.info(
					"ResultsFromRedisHelper - fetchYearMonthFromPassfailForSemBoth - (Sem, Total Subject Passed/Failed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet + ")");
		}
		return yearMonthMap;
	}

	public Map<String, Object> fetchPassfailForStudent(final String examStage, final String sem, final String sapId,
			final String month, final String year) {
		List<PassFailExamBean> listBean = null;
		List<PassFailExamBean> list = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);

			if(null != redisMap) {
				listBean = this.extractPassfailStatus(redisMap);
				list = new ArrayList<PassFailExamBean>();
				for (PassFailExamBean bean : listBean) {
					
					//&& month.equalsIgnoreCase(bean.getWrittenMonth()) && year.equalsIgnoreCase(bean.getWrittenYear())
					if (null != bean.getSem() && null != bean.getSapid()
							&& (sem.equalsIgnoreCase(bean.getSem()) && sapId.equalsIgnoreCase(bean.getSapid()))) {
						list.add(bean);
					}
				}
			}
			simpleMap.put(KEY_PASSFAIL_STATUS, list);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchPassfailForStudent : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchPassfailForStudent : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}
	
	public Map<String, Object> fetchOnlyPassfail(final String examStage, final String sapId) {
		List<PassFailExamBean> list = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);
			
			if(null != redisMap) {
				list = this.extractPassfailStatus(redisMap);
			}
			simpleMap.put(KEY_PASSFAIL_STATUS, list);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchOnlyPassfail : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyPassfail : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}
	
	/**
	 * Unpacks and return Passfail list in Map (Overloaded).
	 * @param simpleMap Map with result details
	 * @param examStage
	 * @param sapId
	 * @return
	 */
	public Map<String, Object> fetchOnlyPassfail(final Map<String, Object> simpleMap, final String examStage,
			final String sapId) {
		List<PassFailExamBean> list = null;
		Map<String, Object> tempMap = null;

		try {
			tempMap = new HashMap<String, Object>();

			if (null != simpleMap && !simpleMap.isEmpty()) {
				list = (List<PassFailExamBean>) simpleMap.get(KEY_PASSFAIL_STATUS);
				logger.info("ResultsFromRedisHelper : fetchOnlyPassfail : Results (SapId, simpleMap Size) (" + sapId + ","
						+ simpleMap.size() + ")");
			}
			tempMap.put(KEY_PASSFAIL_STATUS, list);

		} catch (Exception ex) {
			tempMap = null;
			logger.error("ResultsFromRedisHelper : fetchOnlyPassfail : " + ex.getMessage());
		} finally {
		}
		return tempMap;
	}
	
	public Map<String, Object> fetchOnlyMarkslist(String examStage, String sapId) {
		List<StudentMarksBean> listStudentMarksBean = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();
			
			redisMap = this.fetchResults(sapId);

			if(null != redisMap) {
				listStudentMarksBean = this.extractMarksList(redisMap);
			}
			simpleMap.put(KEY_STUDENT_MARKSLIST, listStudentMarksBean);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchOnlyMarkslist : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyMarkslist : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}
	
	public Integer fetchOnlyMarkslistSize(final String examStage, final String sapId) {
		Integer markslistSize = null;
		Map<String, List> redisMap = null;

		try {
			redisMap = this.fetchResults(sapId);
			
			if(null != redisMap) {
				markslistSize = this.extractSize(redisMap);
			}

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchOnlyMarkslistSize : " + ex.getMessage());
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyMarkslistSize : Results (SapId, Markslist Size) (" + sapId
					+ "," + markslistSize + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return markslistSize;
	}
	
	/**
	 * Only for StudentPortal, called from Intermediate Page in HomController.
	 * @param redisBean
	 */
	public void orderMarksList(StudentsDataInRedisBean redisBean) {
		Map<String, List> redisMap = null;
		List<RedisStudentMarksBean> listRedisStudentMarksBean = null;
		List<RedisStudentMarksBean> list = null;
		redisMap = redisBean.getResultsData();
		if(null != redisMap.get(KEY_STUDENT_MARKSLIST)) {
			listRedisStudentMarksBean = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKSLIST);
			
			list = redisResultsOrderService.orderMarksList(listRedisStudentMarksBean);
			
			redisMap.put(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST, list);
		}
	}
	
	/**
	 * Only for StudentPortal, called from Intermediate Page in HomController.
	 * @param redisBean
	 */
	public void orderPassfailStatus(StudentsDataInRedisBean redisBean) {
		Map<String, List> redisMap = null;
		List<RedisPassFailBean> listRedisPassFailBean = null;
		List<RedisPassFailBean> list = null;
		redisMap = redisBean.getResultsData();
		if(null != redisMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS)) {
			listRedisPassFailBean = (List<RedisPassFailBean>) redisMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS).get(1);
			
			list = redisResultsOrderService.orderPassFailList(listRedisPassFailBean);
			
			redisMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS).set(1, list);
		}
	}
	
	/**
	 * Only for StudentPortal, called from Intermediate Page in HomController. 
	 * @param redisBean
	 */
	public void orderMarksHistoryList(StudentsDataInRedisBean redisBean) {
		Map<String, List> redisMap = null;
		List<RedisStudentMarksBean> listRedisStudentMarksBean2 = null;
		List<RedisStudentMarksBean> list = null;
		redisMap = redisBean.getResultsData();
		if(null != redisMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY)) {
			listRedisStudentMarksBean2 = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKS_HISTORY);
			
			list =  redisResultsOrderService.orderMarksHistoryList(listRedisStudentMarksBean2);
			
			redisMap.put(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY, list);
		}
	}
	
	public static boolean isNotBlank(String arg) {
		return StringUtils.isNotBlank(arg);
	}
	
	public Map<Integer, Set<String>> fetchYearMonthFromPassfailForSemBoth(final String sem,
			final Map<String, Object> simpleMap) {
		Integer rowCount = 0;
		String yearMonthStr = null;
		Set<String> yearMonthSet = null;
		List<PassFailExamBean> listPassFailBean = null;
		Map<Integer, Set<String>> yearMonthMap = null;

		listPassFailBean = (List<PassFailExamBean>) simpleMap.get(KEY_PASSFAIL_STATUS);

		if (null != listPassFailBean && !listPassFailBean.isEmpty()) {
			yearMonthMap = new HashMap<Integer, Set<String>>();
			yearMonthSet = new HashSet<String>();

			for (PassFailExamBean bean : listPassFailBean) {
				if (null != bean.getSem() && null != sem && sem.equals(bean.getSem())
						) {
					rowCount++; // Count - total subject passed or failed.

					if (ResultsFromRedisHelper.isNotBlank(bean.getAssignmentYear())
							&& ResultsFromRedisHelper.isNotBlank(bean.getAssignmentMonth())) {
						yearMonthStr = bean.getAssignmentYear() + "-" + bean.getAssignmentMonth();
						yearMonthSet.add(yearMonthStr);
					}
					if (ResultsFromRedisHelper.isNotBlank(bean.getWrittenYear())
							&& ResultsFromRedisHelper.isNotBlank(bean.getWrittenMonth())) {
						yearMonthStr = bean.getWrittenYear() + "-" + bean.getWrittenMonth();
						yearMonthSet.add(yearMonthStr);
					}
					
					if (ResultsFromRedisHelper.isNotBlank(bean.getResultProcessedYear())
							&& ResultsFromRedisHelper.isNotBlank(bean.getResultProcessedMonth())) {
						yearMonthStr = bean.getResultProcessedYear() + "-" + bean.getResultProcessedMonth();
						yearMonthSet.add(yearMonthStr);
					}
				}
			}
			yearMonthMap.put(rowCount, yearMonthSet);
			logger.info(
					"ResultsFromRedisHelper : fetchYearMonthFromPassfailForSemBoth : (Sem, Total Subject Passed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet.size() + ")");
		} else {
			logger.info(
					"ResultsFromRedisHelper : fetchYearMonthFromPassfailForSemBoth : (Sem, Total Subject Passed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet + ")");
		}
		return yearMonthMap;
	}

}
