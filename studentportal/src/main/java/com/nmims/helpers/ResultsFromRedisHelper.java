/**
 * 
 */
package com.nmims.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FlagBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.RedisPassFailBean;
import com.nmims.beans.RedisStudentBean;
import com.nmims.beans.RedisStudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.services.RedisResultsStoreService;

/**
 * @author vil_m 
 *
 */
@Service("resultsFromRedisHelper")
public class ResultsFromRedisHelper {
	
	public static final String EXAM_STAGE_TEE = "TEE";
	
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

	protected StudentStudentPortalBean extractStudent(Map<String, List> redisMap) {
		RedisStudentBean redisStudentBean = null;
		StudentStudentPortalBean student = null;

		if(null != redisMap.get(KEY_STUDENT_DETAILS)) {
			redisStudentBean = (RedisStudentBean) redisMap.get(KEY_STUDENT_DETAILS).get(0);
			student = new StudentStudentPortalBean();
			student.setConsumerType(redisStudentBean.getConsumerType());
			student.setProgram(redisStudentBean.getProgram());
			student.setPrgmStructApplicable(redisStudentBean.getPrgmStructApplicable());
			student.setExamMode(redisStudentBean.getExamMode());
			student.setProgramType(redisStudentBean.getProgramType());
	
			student.setFirstName(redisStudentBean.getFirstName());
			student.setLastName(redisStudentBean.getLastName());
			student.setEmailId(redisStudentBean.getEmailId());
			student.setMobile(redisStudentBean.getMobile());
			student.setCity(redisStudentBean.getCity());
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
		List<RedisStudentMarksBean> listRedisStudentMarksBean = null;
		List<StudentMarksBean> listStudentMarksBean = null;
		StudentMarksBean studentMarks = null;

		if(null != redisMap.get(KEY_STUDENT_MARKSLIST)) {
			listRedisStudentMarksBean = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKSLIST);
			listStudentMarksBean = new ArrayList<StudentMarksBean>();
			for (RedisStudentMarksBean redisStudMarksBean : listRedisStudentMarksBean) {
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
	
				// set here in STUDENTPORTAL, not set in EXAM
				studentMarks
						.setTotal(addMarks(redisStudMarksBean.getWritenscore(), redisStudMarksBean.getAssignmentscore()));
	
				listStudentMarksBean.add(studentMarks);
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractMarksList : Map with Key (" + KEY_STUDENT_MARKSLIST
					+ ")  value : " + redisMap.get(KEY_STUDENT_MARKSLIST));
		}

		return listStudentMarksBean;
	}

	protected List<PassFailBean> extractPassfailStatus(Map<String, List> redisMap) {
		List<RedisPassFailBean> listRedisPassFailBean = null;
		List<PassFailBean> listPassFailBean = null;
		PassFailBean passFailBean = null;

		if(null != redisMap.get(KEY_PASSFAIL_STATUS)) {
			listRedisPassFailBean = (List<RedisPassFailBean>) redisMap.get(KEY_PASSFAIL_STATUS).get(1);
			listPassFailBean = new ArrayList<PassFailBean>();
			for (RedisPassFailBean redisPFBean : listRedisPassFailBean) {
				passFailBean = new PassFailBean();
	
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
				
				//required for /exam/studentSelfMarksheet
				passFailBean.setSapid(redisPFBean.getSapid());
				passFailBean.setProgram(redisPFBean.getProgram());
	
				listPassFailBean.add(passFailBean);
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractPassfailStatus : Map with Key (" + KEY_PASSFAIL_STATUS
					+ ")  value : " + redisMap.get(KEY_PASSFAIL_STATUS));
		}

		return listPassFailBean;
	}

	protected List<StudentMarksBean> extractMarksHistoryList(Map<String, List> redisMap) {
		List<RedisStudentMarksBean> listRedisStudentMarksBean2 = null;
		List<StudentMarksBean> listStudentMarksBean2 = null;
		StudentMarksBean studentMarks2 = null;

		if(null != redisMap.get(KEY_STUDENT_MARKS_HISTORY)) {
			listRedisStudentMarksBean2 = (List<RedisStudentMarksBean>) redisMap.get(KEY_STUDENT_MARKS_HISTORY);
			listStudentMarksBean2 = new ArrayList<StudentMarksBean>();
			for (RedisStudentMarksBean redisStudMarksBean2 : listRedisStudentMarksBean2) {
				studentMarks2 = new StudentMarksBean();
	
				studentMarks2.setSem(redisStudMarksBean2.getSem());
				studentMarks2.setSubject(redisStudMarksBean2.getSubject());
				studentMarks2.setYear(redisStudMarksBean2.getYear());
				studentMarks2.setMonth(redisStudMarksBean2.getMonth());
				studentMarks2.setWritenscore(redisStudMarksBean2.getWritenscore());
				studentMarks2.setAssignmentscore(redisStudMarksBean2.getAssignmentscore());
				studentMarks2.setGracemarks(redisStudMarksBean2.getGracemarks());
	
				listStudentMarksBean2.add(studentMarks2);
			}
		} else {
			logger.error("ResultsFromRedisHelper : extractMarksHistoryList : Map with Key (" + KEY_STUDENT_MARKS_HISTORY
					+ ")  value : " + redisMap.get(KEY_STUDENT_MARKS_HISTORY));
		}

		return listStudentMarksBean2;
	}
	
	/**
	 * Results are stored in Redis specific classes. Extract, pack in, send this
	 * Data. eg. from RedisPassFailBean extract data and store in Map as
	 * PassFailBean etc.
	 * 
	 * @param examStage exam type/point at which exam taken.
	 * @param sapId     Students Id.
	 * @return Map with data.
	 */
	public Map<String, Object> fetchResultsFromRedis(String examStage, String sapId) {
		Integer sizeStr = null;
		StudentStudentPortalBean student = null;
		List<PassFailBean> listPassFailBean = null;
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
	
				listPassFailBean = this.extractPassfailStatus(redisMap);
				simpleMap.put(KEY_PASSFAIL_STATUS, listPassFailBean);
	
				listStudentMarksBean2 = this.extractMarksHistoryList(redisMap);
				simpleMap.put(KEY_STUDENT_MARKS_HISTORY, listStudentMarksBean2);
			}

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchResultsFromRedis : " + ex.getMessage());
			//ex.printStackTrace();
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
			//ex.printStackTrace();
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyMarksHistory : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}

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

					// && month.equalsIgnoreCase(bean.getMonth()) &&
					// year.equalsIgnoreCase(bean.getYear())
					if (null != bean.getSem() && null != bean.getSapid()
							&& (sem.equalsIgnoreCase(bean.getSem()) && sapId.equalsIgnoreCase(bean.getSapid()))) {
						list.add(bean);
					}
				}
			}
			simpleMap.put(KEY_STUDENT_MARKSLIST, list);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchMarksRecordsForStudent : " + ex.getMessage());
			//ex.printStackTrace();
		} finally {
			logger.info("ResultsFromRedisHelper : fetchMarksRecordsForStudent : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
	}

	public Map<String, Object> fetchPassfailForStudent(final String examStage, final String sem, final String sapId,
			final String month, final String year) {
		List<PassFailBean> listPassFailBean = null;
		List<PassFailBean> list = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);

			if(null != redisMap) {
				listPassFailBean = this.extractPassfailStatus(redisMap);
				list = new ArrayList<PassFailBean>();
				for (PassFailBean bean : listPassFailBean) {

					// && month.equalsIgnoreCase(bean.getWrittenMonth()) &&
					// year.equalsIgnoreCase(bean.getWrittenYear())
					if (null != bean.getSem() && null != bean.getSapid()
							&& (sem.equalsIgnoreCase(bean.getSem()) && sapId.equalsIgnoreCase(bean.getSapid()))) {
						list.add(bean);
					}
				}
			}
			simpleMap.put(KEY_PASSFAIL_STATUS, list);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchPassfailForStudent : " + ex.getMessage());
			//ex.printStackTrace();
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
		List<PassFailBean> listPassFailBean = null;
		Map<String, Object> simpleMap = null;

		Map<String, List> redisMap = null;

		try {
			simpleMap = new HashMap<String, Object>();

			redisMap = this.fetchResults(sapId);
			
			if(null != redisMap) {
				listPassFailBean = this.extractPassfailStatus(redisMap);
			}
			simpleMap.put(KEY_PASSFAIL_STATUS, listPassFailBean);

		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : fetchOnlyPassfail : " + ex.getMessage());
			//ex.printStackTrace();
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyPassfail : Results (SapId, Size) (" + sapId + ","
					+ simpleMap.size() + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return simpleMap;
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
			//ex.printStackTrace();
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
			//ex.printStackTrace();
		} finally {
			logger.info("ResultsFromRedisHelper : fetchOnlyMarkslistSize : Results (SapId, Markslist Size) (" + sapId
					+ "," + markslistSize + ")");
			if (null != redisMap) {
				redisMap.clear();
			}
		}
		return markslistSize;
	}

	protected String addMarks(String marks1, String marks2) {
		String addedMarks;
		addedMarks = String.valueOf(parseAsInt(marks1) + parseAsInt(marks2));
		return addedMarks;
	}

	public int parseAsInt(String x) {
		int retVal = 0;
		try {
			retVal = toInteger(x);
		} catch (Exception ex) {
			logger.error("ResultsFromRedisHelper : parseAsInt : " + ex.getMessage());
		}
		return retVal;
	}

	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}

	public static boolean isNotBlank(String arg) {
		return StringUtils.isNotBlank(arg);
	}
	
	public Map<Integer, Set<String>> fetchYearMonthFromPassfailForSem(final String sem,
			final Map<String, Object> simpleMap) {
		Integer rowCount = 0;
		String yearMonthStr = null;
		Set<String> yearMonthSet = null;
		List<PassFailBean> listPassFailBean = null;
		Map<Integer, Set<String>> yearMonthMap = null;

		listPassFailBean = (List<PassFailBean>) simpleMap.get(KEY_PASSFAIL_STATUS);

		if (null != listPassFailBean && !listPassFailBean.isEmpty()) {
			yearMonthMap = new HashMap<Integer, Set<String>>();
			yearMonthSet = new HashSet<String>();

			for (PassFailBean bean : listPassFailBean) {
				if (null != bean.getSem() && null != sem && sem.equals(bean.getSem())
						&& VALUE_Y.equals(bean.getIsPass())) {
					rowCount++; // Count - total subject passed.

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
				}
			}
			yearMonthMap.put(rowCount, yearMonthSet);
			logger.info(
					"ResultsFromRedisHelper : fetchYearMonthFromPassfailForSem : (Sem, Total Subject Passed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet.size() + ")");
		} else {
			logger.info(
					"ResultsFromRedisHelper : fetchYearMonthFromPassfailForSem : (Sem, Total Subject Passed, yearMonth Size) : ("
							+ sem + "," + rowCount + "," + yearMonthSet + ")");
		}
		return yearMonthMap;
	}

}
