/**
 * 
 */

package com.nmims.daos;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;


import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlRegisterCandidateReportBean;
import com.nmims.beans.MettlScheduleExamBean;

/**
 * @author vil_m 
 *
 */
public class MettlDAO  extends BaseDAO {
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	public static final String DB_SCHEMA_EXAM = "exam";
	
	public static final String SPACE = " ";
	public static final String ROUND_OPENB = "(";
	public static final String ROUND_CLOSEB = ")";
	public static final String COMMA = ",";
	public static final String SINGLE_QUOTE = "'";
	public static final String Q_MARK = "?";
	
	public static final String TABLE_SCHEDULE_METTL = "exams_schedule_mettl";
	public static final String TABLE_EXAMS_SCHEDULE_METTL =  DB_SCHEMA_EXAM + "." + TABLE_SCHEDULE_METTL;
	
	public static final String TABLE_EXAMS_CANDIDATES_METTL = "exam.exams_candidates_mettl";
	public static final String S_INSERT_INTO = "insert into";
	public static final String S_VALUES = "values";
	public static final String C_SAP_ID = "sapId"; 
	public static final String C_SCHEDULE_ACCESS_KEY = "scheduleAccessKey"; 
	public static final String C_REGIS_STATUS = "registrationStatus"; 
	public static final String C_REGIS_MSG = "registrationMessage"; 
	public static final String C_REGIS_URL = "registeredUrl";
	
	public static final String S_EQUALTO = "=";
	public static final String S_AND = "and";
	public static final String S_SET = "set";
	public static final String S_UPDATE = "update";
	public static final String S_WHERE = " where";
	public static final String TABLE_EXAMBOOKINGS = "exam.exambookings";
	public static final String C_EMAIL_ID = "emailId"; 
	public static final String C_BOOKED = "booked";  
	public static final String C_SUBJECT = "subject"; 
	public static final String C_MONTH = "month";  
	public static final String C_YEAR = "year"; 
	public static final String C_LAST_MODIFIEDBY ="lastModifiedBy";
	public static final String C_EXAMDATE = "examDate";
	public static final String C_EXAMTIME = "examTime";
	public static final String C_EXAMENDTIME = "examEndTime";
	
	public static final String TABLE_PG_SCHEDULEINFO_METTL = "exams_pg_scheduleinfo_mettl";
	public static final String TABLE_EXAMS_PG_SCHEDULEINFO_METTL = DB_SCHEMA_EXAM + "." + TABLE_PG_SCHEDULEINFO_METTL;
	public static final String C_FIRSTNAME = "firstname";
	public static final String C_LASTNAME = "lastname";
	public static final String C_EMAILID = "emailId";
	public static final String C_TRACKID = "trackId";
	public static final String C_TESTTAKEN = "testTaken";
	public static final String C_SCHEDULENAME = "scheduleName";
	public static final String C_SIFYSUBJECTCODE = "sifySubjectCode";
	public static final String C_ACCESSSTARTDATETIME = "accessStartDateTime";
	public static final String C_ACCESSENDDATETIME = "accessEndDateTime";
	public static final String C_EXAMSTARTDATETIME = "examStartDateTime";
	public static final String C_REPORTING_START_TIME = "reporting_start_date_time";
	public static final String C_REPORTING_FINISH_TIME = "reporting_finish_date_time";
	public static final String C_EXAMENDDATETIME = "examEndDateTime";
	public static final String C_ACCESSKEY = "acessKey";
	public static final String C_JOINURL = "joinURL";
	public static final String C_EXAMCENTERNAME = "examCenterName";
	
	public static final String S_FROM = "from";
	public static final String S_SELECT = "select";
	public static final String S_DISTINCT = "distinct";
	public static final String S_ORDER_BY = "order by";
	
	public static final String TABLE_EXAM_TIMETABLE = DB_SCHEMA_EXAM + ".timetable";
	public static final String C_DATE = "date";
	public static final String C_EXAMYEAR = "examYear";
	public static final String C_EXAMMONTH = "examMonth";
	
	public static final String SQL_UPDATE_EXAMBOOKINGS;
	public static final String SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL;
	public static final String SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL_WAITING_ROOM;
	
	static {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(S_UPDATE).append(SPACE).append(TABLE_EXAMBOOKINGS).append(SPACE);
		strBuf.append(S_SET).append(SPACE);
		strBuf.append(C_EMAIL_ID).append(S_EQUALTO).append("?").append(COMMA).append(SPACE);
		strBuf.append(C_LAST_MODIFIEDBY).append(S_EQUALTO).append("'METTL RegCandidate'").append(SPACE);
		strBuf.append(S_WHERE).append(SPACE).append(C_SAP_ID).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_SUBJECT).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_YEAR).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_MONTH).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_BOOKED).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_EXAMDATE).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_EXAMTIME).append(S_EQUALTO).append("?").append(SPACE).append(S_AND);
		strBuf.append(SPACE).append(C_EXAMENDTIME).append(S_EQUALTO).append("?");
		
		SQL_UPDATE_EXAMBOOKINGS = strBuf.toString();
		emptyStringBuffer(strBuf);
		
		strBuf = new StringBuffer();
		strBuf.append(S_INSERT_INTO).append(SPACE).append(TABLE_EXAMS_PG_SCHEDULEINFO_METTL).append(SPACE);
		strBuf.append(ROUND_OPENB);
		strBuf.append(C_SUBJECT).append(COMMA).append(C_YEAR).append(COMMA).append(C_MONTH).append(COMMA).append(C_TRACKID).append(COMMA);
		strBuf.append("sapid").append(COMMA).append(C_TESTTAKEN).append(COMMA).append(C_FIRSTNAME).append(COMMA).append(C_LASTNAME).append(COMMA);
		strBuf.append(C_EMAILID).append(COMMA).append(C_EXAMSTARTDATETIME).append(COMMA).append(C_EXAMENDDATETIME).append(COMMA).append(C_ACCESSSTARTDATETIME).append(COMMA);
		strBuf.append(C_ACCESSENDDATETIME).append(COMMA).append(C_SIFYSUBJECTCODE).append(COMMA).append(C_SCHEDULENAME).append(COMMA).append(C_ACCESSKEY).append(COMMA);
		strBuf.append(C_JOINURL).append(COMMA).append(C_EXAMCENTERNAME);
		strBuf.append(ROUND_CLOSEB).append(SPACE);
		strBuf.append(S_VALUES).append(SPACE);
		strBuf.append(ROUND_OPENB);
		strBuf.append("?,?,?,?,?,?,?,?,?");
		strBuf.append(",STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')");
		strBuf.append(",?,?,?,?,?");
		strBuf.append(ROUND_CLOSEB);
		
		SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL = strBuf.toString();
		emptyStringBuffer(strBuf);
		
		strBuf = new StringBuffer();
		strBuf.append(S_INSERT_INTO).append(SPACE).append(TABLE_EXAMS_PG_SCHEDULEINFO_METTL).append(SPACE);
		strBuf.append(ROUND_OPENB);
		strBuf.append(C_SUBJECT).append(COMMA).append(C_YEAR).append(COMMA).append(C_MONTH).append(COMMA).append(C_TRACKID).append(COMMA);
		strBuf.append("sapid").append(COMMA).append(C_TESTTAKEN).append(COMMA).append(C_FIRSTNAME).append(COMMA).append(C_LASTNAME).append(COMMA);
		strBuf.append(C_EMAILID).append(COMMA).append(C_EXAMSTARTDATETIME).append(COMMA).append(C_EXAMENDDATETIME).append(COMMA).append(C_ACCESSSTARTDATETIME).append(COMMA);
		strBuf.append(C_ACCESSENDDATETIME).append(COMMA).append(C_REPORTING_START_TIME).append(COMMA).append(C_REPORTING_FINISH_TIME).append(COMMA);
		strBuf.append(C_SIFYSUBJECTCODE).append(COMMA).append(C_SCHEDULENAME).append(COMMA).append(C_ACCESSKEY).append(COMMA);
		strBuf.append(C_JOINURL).append(COMMA).append(C_EXAMCENTERNAME);
		strBuf.append(ROUND_CLOSEB).append(SPACE);
		strBuf.append(S_VALUES).append(SPACE);
		strBuf.append(ROUND_OPENB);
		strBuf.append("?,?,?,?,?,?,?,?,?,");
		strBuf.append("STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),");
		strBuf.append("STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),");
		strBuf.append("STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),");
		strBuf.append("?,?,?,?,?");
		strBuf.append(ROUND_CLOSEB);
		
		SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL_WAITING_ROOM = strBuf.toString();
		emptyStringBuffer(strBuf);
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
		// super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public synchronized List<MettlScheduleExamBean> fetchAssessmentsList() {
		logger.info("Entering MettlDAO.fetchAssessmentsList");
		List<MettlScheduleExamBean> list1 = null;
		
		//Add at start in Stored procedure, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_assessments()}",
				new CallableStatementCallback<List<MettlScheduleExamBean>>() {
					@Override
					public List<MettlScheduleExamBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlScheduleExamBean> mList = null;
						MettlScheduleExamBean bean = null;
						mList = new ArrayList<MettlScheduleExamBean>();
						while (rs.next()) {
							bean = new MettlScheduleExamBean();
							bean.setAssessmentId(rs.getString("assessmentId"));
							bean.setStartsOnDate(rs.getString("startsOnDate"));
							bean.setDate(rs.getString("date"));
							bean.setStartTime(rs.getString("startTime"));
							bean.setEndTime(rs.getString("endTime"));
							
							bean.setCustomUrlId(rs.getString("CUSTOM_URL_ID"));
							bean.setEndTime2(rs.getString("endTime2"));//(date + startTime) + 1 hour
							//bean.setExamYear(rs.getString("examYear"));
							//bean.setExamMonth(rs.getString("examMonth"));
							//bean.setProgramStructure(rs.getString("PrgmStructApplicable"));
							//bean.setSubject(rs.getString("subject"));
							//bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
							
							mList.add(bean);
						}
						return mList;
					}
				});
		if(null != list1 && !list1.isEmpty()) {
			logger.info("MettlDAO.fetchAssessmentsList : List Size "+ list1.size());
		}
		return list1;
	}
	
	public synchronized List<MettlScheduleExamBean> fetchAssessmentsListWaitingRoom() {
		logger.info(" Entering MettlDAO.fetchAssessmentsListWaitingRoom ");
		List<MettlScheduleExamBean> list1 = null;
		
		//Add at start in Stored procedure, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_assessments_waiting_room()}",
				new CallableStatementCallback<List<MettlScheduleExamBean>>() {
					@Override
					public List<MettlScheduleExamBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlScheduleExamBean> mList = null;
						MettlScheduleExamBean bean = null;
						mList = new ArrayList<MettlScheduleExamBean>();
						while (rs.next()) {
							bean = new MettlScheduleExamBean();
							bean.setAssessmentId(rs.getString("assessmentId"));
							bean.setStartsOnDate(rs.getString("startsOnDate"));
							bean.setDate(rs.getString("date"));
							bean.setStartTime(rs.getString("startTime"));
							bean.setEndTime(rs.getString("endTime"));
							
							bean.setCustomUrlId(rs.getString("CUSTOM_URL_ID"));
							bean.setEndTime2(rs.getString("endTime2"));//(date + startTime) + 1 hour
							bean.setScheduleEndTime(rs.getString("scheduleEndTime"));
							
							//time range for METTL waiting room API changes DEC 2022
							bean.setReportingStartTime(rs.getString("reportingStartTime"));
							bean.setReportingFinishTime(rs.getString("reportingFinishTime"));
							
							//bean.setExamYear(rs.getString("examYear"));
							//bean.setExamMonth(rs.getString("examMonth"));
							//bean.setProgramStructure(rs.getString("PrgmStructApplicable"));
							//bean.setSubject(rs.getString("subject"));
							//bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
							
							mList.add(bean);
						}
						return mList;
					}
				});
		if(null != list1 && !list1.isEmpty()) {
			logger.info(" MettlDAO.fetchAssessmentsListWaitingRoom : List Size {}", list1.size());
		}
		return list1;
	}
	
	protected void endTransaction(boolean activity) {
		if(activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}
	
	public void end_Transaction(boolean activity) {
		this.endTransaction(activity);
	}

	/*@Deprecated
	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}*/

	/*@Deprecated
	protected void startTransaction(String transactionName, boolean readOnly) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(readOnly);//for update - Boolean.FALSE, read - Boolean.TRUE
		this.status = transactionManager.getTransaction(def);
	}*/

	protected void startTransaction(String transactionName, boolean readOnly, int propagationBehaviour) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(propagationBehaviour);
		def.setReadOnly(readOnly);//for update - Boolean.FALSE, read - Boolean.TRUE
		this.status = transactionManager.getTransaction(def);
	}
	
	public void start_Transaction_U_PR(String transactionName) {
		this.startTransaction(transactionName, Boolean.FALSE, TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public void start_Transaction_R_PR(String transactionName) {
		this.startTransaction(transactionName, Boolean.TRUE, TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public boolean saveSchedule(final MettlScheduleExamBean bean, final String userId) {
		logger.info("Entering MettlDAO.saveSchedule");
		int rowsAffected = -1;
		Boolean isSaved = Boolean.FALSE;
		String query = null;
		final String newQuery;

		try {
			this.start_Transaction_U_PR("saveSchedule");
			
			query = "insert into " + MettlDAO.TABLE_EXAMS_SCHEDULE_METTL + " ";
			query += "(assessments_id, schedule_id, schedule_name, schedule_accessKey, schedule_accessUrl, schedule_status, ";
			query += " exam_start_date_time, exam_end_date_time, active, isResultLive, max_score, createdBy, lastModifiedBy)";
			query += " values (?,?,?,?,?,?,STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?,?,?,?)";
			newQuery = query;

			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(newQuery);

					ps.setInt(1, toInteger(bean.getAssessmentId()));
					ps.setInt(2, toInteger(bean.getScheduleId()));//ScheduleId from mettl
					ps.setString(3, bean.getScheduleName());
					ps.setString(4, bean.getScheduleAccessKey());
					ps.setString(5, bean.getScheduleAccessURL());
					ps.setString(6, bean.getScheduleStatus());
					ps.setString(7, bean.getStartTime());
					ps.setString(8, bean.getEndTime());
					ps.setString(9, bean.getActive());
					ps.setString(10, bean.getResultLive());
					ps.setString(11, bean.getMaxScore());
					ps.setString(12, bean.getCreatedBy());
					ps.setString(13, bean.getLastModifiedBy());
					return ps;
				}
			};
			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isSaved = Boolean.TRUE;
			}
			this.end_Transaction(isSaved);
			logger.info("MettlDAO.saveSchedule (ScheduleId, ScheduleName) : (" + bean.getScheduleId() + ","
					+ bean.getScheduleName() + ")");
		} catch (Exception e) {
			
			logger.error("MettlDAO.saveSchedule : Exception : " + e.getMessage());
			this.end_Transaction(Boolean.FALSE);
			throw e;
		} finally {
			logger.info("MettlDAO.saveSchedule (Schedule created count) : (" + rowsAffected + ")");
		}
		return isSaved;
	}
	
	public boolean saveScheduleWaitingRoom(final MettlScheduleExamBean bean, final String userId) {
		logger.info(" Entering MettlDAO.saveScheduleWaitingRoom ");
		int rowsAffected = -1;
		Boolean isSaved = Boolean.FALSE;
		String query = null;
		final String newQuery;

		try {
			this.start_Transaction_U_PR("saveSchedule");
			
			query = "insert into " + MettlDAO.TABLE_EXAMS_SCHEDULE_METTL + " ";
			query += "(assessments_id, schedule_id, schedule_name, schedule_accessKey, schedule_accessUrl, schedule_status, ";
			query += " exam_start_date_time, exam_end_date_time,reporting_start_date_time, reporting_finish_date_time, ";
			query += " active, isResultLive, max_score, createdBy, lastModifiedBy) ";
			query += " values (?,?,?,?,?,?,STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),";
			query += " STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?,?,?,?)";
			
			newQuery = query;

			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(newQuery);

					ps.setInt(1, toInteger(bean.getAssessmentId()));
					ps.setInt(2, toInteger(bean.getScheduleId()));//ScheduleId from mettl
					ps.setString(3, bean.getScheduleName());
					ps.setString(4, bean.getScheduleAccessKey());
					ps.setString(5, bean.getScheduleAccessURL());
					ps.setString(6, bean.getScheduleStatus());
					ps.setString(7, bean.getStartTime());
//					ps.setString(8, bean.getEndTime());
					ps.setString(8, bean.getScheduleEndTime());
					ps.setString(9, bean.getReportingStartTime());
					ps.setString(10, bean.getReportingFinishTime());
					ps.setString(11, bean.getActive());
					ps.setString(12, bean.getResultLive());
					ps.setString(13, bean.getMaxScore());
					ps.setString(14, bean.getCreatedBy());
					ps.setString(15, bean.getLastModifiedBy());
					return ps;
				}
			};
			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isSaved = Boolean.TRUE;
			}
			this.end_Transaction(isSaved);
			logger.info("MettlDAO.saveSchedule (ScheduleId, ScheduleName) : ({} , {})", bean.getScheduleId() ,bean.getScheduleName());
		} catch (Exception e) {
			
			logger.error("MettlDAO.saveSchedule : Exception : {}", e.getMessage());
			this.end_Transaction(Boolean.FALSE);
			throw e;
		} finally {
			logger.info("MettlDAO.saveSchedule (Schedule created count) : ( {} )", rowsAffected);
		}
		return isSaved;
	}
	
	public synchronized List<MettlRegisterCandidateBean> fetchAll_Candidates_SchedulesList(final String examDate) {
		logger.info("Entering MettlDAO.fetchAll_Candidates_SchedulesList : examDate : "+examDate);
		List<MettlRegisterCandidateBean> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_candidates_schedules(?)}",
				new CallableStatementCallback<List<MettlRegisterCandidateBean>>() {
					@Override
					public List<MettlRegisterCandidateBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setString(1, examDate);
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlRegisterCandidateBean> mList = null;
						MettlRegisterCandidateBean bean = null;
						mList = new ArrayList<MettlRegisterCandidateBean>();
						while (rs.next()) {
							bean = new MettlRegisterCandidateBean();
							bean.setSapId(rs.getString("studentid"));
							bean.setFirstName(rs.getString("firstName"));
							bean.setLastName(rs.getString("lastName"));
							bean.setEmailAddress(rs.getString("emailId"));
							bean.setCandidateImage(rs.getString("imageUrl"));
							bean.setRegistrationImage(rs.getString("imageUrl"));
							bean.setScheduleAccessKey(rs.getString("schedule_accessKey"));
							bean.setScheduleAccessURL(rs.getString("schedule_accessUrl"));
							if("0".equals(rs.getString("openLinkFlag"))) {
								bean.setOpenLinkFlag(Boolean.TRUE);
							} else {
								bean.setOpenLinkFlag(Boolean.FALSE);
							}
							
							bean.setSubject(rs.getString("subject"));
							bean.setBooked(rs.getString("booked"));
							bean.setYear(rs.getString("year"));
							bean.setMonth(rs.getString("month"));
							bean.setExamDate(rs.getString("examDate"));
							bean.setExamTime(rs.getString("examTime"));
							bean.setExamEndTime(rs.getString("examEndTime"));
							
							bean.setTrackId(rs.getString("trackId"));
							bean.setTestTaken(rs.getString("testTaken"));
							bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
							bean.setExamStartDateTime(rs.getString("examStartDateTime"));
							bean.setExamEndDateTime(rs.getString("examEndDateTime"));
							bean.setAccessStartDateTime(rs.getString("accessStartDateTime"));
							bean.setAccessEndDateTime(rs.getString("accessEndDateTime"));
							bean.setScheduleName(rs.getString("scheduleName"));
							bean.setExamCenterName(rs.getString("examCenterName"));
							
							mList.add(bean);
						}
						return mList;
					}
				});
		
		if(null != list1 && !list1.isEmpty()) {
			logger.info("MettlDAO.fetchAll_Candidates_SchedulesList : List Size : "+ list1.size());
		}
		return list1;
	}
	
	public synchronized List<MettlRegisterCandidateBean> fetchAll_Candidates_SchedulesList_Waiting_Room(final String examDate) {
		logger.info("Entering MettlDAO.fetchAll_Candidates_SchedulesList_Waiting_Room : examDate : "+examDate);
		List<MettlRegisterCandidateBean> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_candidates_schedules_waiting_room(?)}",
				new CallableStatementCallback<List<MettlRegisterCandidateBean>>() {
			@Override
			public List<MettlRegisterCandidateBean> doInCallableStatement(CallableStatement cs)
					throws SQLException, DataAccessException {
				cs.setString(1, examDate);
				cs.executeUpdate();
				ResultSet rs = cs.getResultSet();
				
				List<MettlRegisterCandidateBean> mList = null;
				MettlRegisterCandidateBean bean = null;
				mList = new ArrayList<MettlRegisterCandidateBean>();
				while (rs.next()) {
					bean = new MettlRegisterCandidateBean();
					bean.setSapId(rs.getString("studentid"));
					bean.setFirstName(rs.getString("firstName"));
					bean.setLastName(rs.getString("lastName"));
					bean.setEmailAddress(rs.getString("emailId"));
					bean.setCandidateImage(rs.getString("imageUrl"));
					bean.setRegistrationImage(rs.getString("imageUrl"));
					bean.setScheduleAccessKey(rs.getString("schedule_accessKey"));
					bean.setScheduleAccessURL(rs.getString("schedule_accessUrl"));
					if("0".equals(rs.getString("openLinkFlag"))) {
						bean.setOpenLinkFlag(Boolean.TRUE);
					} else {
						bean.setOpenLinkFlag(Boolean.FALSE);
					}
					
					bean.setSubject(rs.getString("subject"));
					bean.setBooked(rs.getString("booked"));
					bean.setYear(rs.getString("year"));
					bean.setMonth(rs.getString("month"));
					bean.setExamDate(rs.getString("examDate"));
					bean.setExamTime(rs.getString("examTime"));
					bean.setExamEndTime(rs.getString("examEndTime"));
					
					bean.setTrackId(rs.getString("trackId"));
					bean.setTestTaken(rs.getString("testTaken"));
					bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
					bean.setExamStartDateTime(rs.getString("examStartDateTime"));
					bean.setExamEndDateTime(rs.getString("examEndDateTime"));
					bean.setAccessStartDateTime(rs.getString("accessStartDateTime"));
					bean.setAccessEndDateTime(rs.getString("accessEndDateTime"));
					bean.setReportStartDateTime(rs.getString("reporting_start_date_time"));
					bean.setReportFinishDateTime(rs.getString("reporting_finish_date_time"));
					bean.setScheduleName(rs.getString("scheduleName"));
					bean.setExamCenterName(rs.getString("examCenterName"));
					
					mList.add(bean);
				}
				return mList;
			}
		});
		
		if(null != list1 && !list1.isEmpty()) {
			logger.info("MettlDAO.fetchAll_Candidates_SchedulesList : List Size : "+ list1.size());
		}
		return list1;
	}
	
	/**
	 * start TRANSACTION from method calling this.
	 * @param sapId
	 * @param scheduleAccessKey
	 * @param status
	 * @param message
	 * @param url
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean saveCandidateRegisteredMettlInfo(final String sapId, final String scheduleAccessKey,
			final int status, final String message, final String url) {
		int rowsAffected = -1;
		boolean isSaved = Boolean.FALSE;
		StringBuffer strBuf = null;
		/*logger.info("Entering MettlDAO.saveCandidateRegisteredMettlInfo (sapId, scheduleAccessKey, status, message, url) ("
				+ sapId + "," + scheduleAccessKey + "," + status + "," + message + "," + url + ")");*/
		try {
			strBuf = new StringBuffer();
			strBuf.append(S_INSERT_INTO).append(SPACE).append(TABLE_EXAMS_CANDIDATES_METTL).append(SPACE);
			strBuf.append(ROUND_OPENB);
			strBuf.append(C_SAP_ID).append(COMMA);
			strBuf.append(C_SCHEDULE_ACCESS_KEY).append(COMMA).append(C_REGIS_STATUS).append(COMMA);
			strBuf.append(C_REGIS_MSG).append(COMMA);
			strBuf.append(C_REGIS_URL);
			strBuf.append(ROUND_CLOSEB).append(SPACE);
			strBuf.append(S_VALUES).append(SPACE).append(ROUND_OPENB);
			strBuf.append("?, ?, ?, ?, ?").append(ROUND_CLOSEB);
			
			final String sql = strBuf.toString();
			//logger.info("MettlDAO >> saveCandidateRegisteredMettlInfo 3 "+ sql);
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, sapId);
					ps.setString(2, scheduleAccessKey);
					ps.setInt(3, status);
					ps.setString(4, message);
					ps.setString(5, url);
					return ps;
				}
			};

			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isSaved = Boolean.TRUE;
			}
		} catch (Exception e) {
			//
			logger.error("MettlDAO.saveCandidateRegisteredMettlInfo : Exception : " + e.getMessage());
			throw e;
		} finally {
			logger.info("MettlDAO.saveCandidateRegisteredMettlInfo (row created count) : (" + rowsAffected + ")");
		}
		emptyStringBuffer(strBuf);
		return isSaved;
	}
	
	/**
	 * start TRANSACTION from method calling this.
	 * @param sapId
	 * @param email
	 * @param subject
	 * @param booked
	 * @param year
	 * @param month
	 * @param examDate
	 * @param examTime
	 * @param examEndTime
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean updateExamBooking(final String sapId, final String email, final String subject, final String booked,
			final String year, final String month, final String examDate, final String examTime, final String examEndTime) {
		int rowsAffected = -1;
		boolean isUpdated = Boolean.FALSE;
		try {
			final String sql = SQL_UPDATE_EXAMBOOKINGS; 
			//logger.info("MettlDAO >> updateExamBooking : SQL : "+ sql);
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, email);
					ps.setString(2, sapId);
					ps.setString(3, subject);
					ps.setString(4, year);
					ps.setString(5, month);
					ps.setString(6, booked);
					ps.setString(7, examDate);
					ps.setString(8, examTime);
					ps.setString(9, examEndTime);
					return ps;
				}
			};

			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isUpdated = Boolean.TRUE;
			}
		} catch (Exception e) {
			//
			logger.error("MettlDAO.updateExamBooking : Exception : " + e.getMessage());
			throw e;
		} finally {
			logger.info("MettlDAO.updateExamBooking (row updated count) : (" + rowsAffected + ")");
		}
		return isUpdated;
	}
	
	/**
	 * start TRANSACTION from method calling this.
	 * @param sapId
	 * @param email
	 * @param subject
	 * @param year
	 * @param month
	 * @param trackId
	 * @param testTaken
	 * @param firstName
	 * @param lastName
	 * @param examStartDateTime
	 * @param examEndDateTime
	 * @param accessStartDateTime
	 * @param accessEndDateTime
	 * @param sifySubjectCode
	 * @param scheduleName
	 * @param scheduleAccessKey
	 * @param joinURL
	 * @return
	 * 
	 */
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean saveScheduleInfo(final String sapId, final String email, final String subject, final String year,
			final String month, final String trackId, final String testTaken, final String firstName,
			final String lastName, final String examStartDateTime, final String examEndDateTime,
			final String accessStartDateTime, final String accessEndDateTime, final String sifySubjectCode,
			final String scheduleName, final String scheduleAccessKey, final String joinURL, final String examCenterName) {
		int rowsAffected = -1;
		Boolean isSaved = Boolean.FALSE;
		final String newQuery = SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL;

		try {
			//logger.info("MettlDAO >> saveScheduleInfo Qry : " + newQuery);

			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(newQuery);

					ps.setString(1, subject);
					ps.setString(2, year);
					ps.setString(3, month);
					ps.setString(4, trackId);
					ps.setString(5, sapId);
					ps.setString(6, testTaken);
					ps.setString(7, firstName);
					ps.setString(8, lastName);
					ps.setString(9, email);
					ps.setString(10, examStartDateTime);
					ps.setString(11, examEndDateTime);
					ps.setString(12, accessStartDateTime);
					ps.setString(13, accessEndDateTime);
					ps.setString(14, sifySubjectCode);
					ps.setString(15, scheduleName);
					ps.setString(16, scheduleAccessKey);
					ps.setString(17, joinURL);
					ps.setString(18, examCenterName);

					return ps;
				}
			};
			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isSaved = Boolean.TRUE;
			}
		} catch (Exception e) {
			// 
			logger.error("MettlDAO.saveScheduleInfo : Exception : " + e.getMessage());
			throw e;
		} finally {
			logger.info("MettlDAO.saveScheduleInfo (row inserted count) : (" + rowsAffected + ")");
		}
		return isSaved;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean saveScheduleInfoWaitingRoom(final String sapId, final String email, final String subject, final String year,
			final String month, final String trackId, final String testTaken, final String firstName,
			final String lastName, final String examStartDateTime, final String examEndDateTime,
			final String accessStartDateTime, final String accessEndDateTime, final String reportingStartDateTime,
			final String reportingFinishDateTime,final String sifySubjectCode,final String scheduleName,
			final String scheduleAccessKey, final String joinURL,final String examCenterName) {
		int rowsAffected = -1;
		Boolean isSaved = Boolean.FALSE;
		final String newQuery = SQL_INSERT_EXAMS_PG_SCHEDULEINFO_METTL_WAITING_ROOM;
		
		try {
			//logger.info("MettlDAO >> saveScheduleInfo Qry : " + newQuery);
			
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(newQuery);
					
					ps.setString(1, subject);
					ps.setString(2, year);
					ps.setString(3, month);
					ps.setString(4, trackId);
					ps.setString(5, sapId);
					ps.setString(6, testTaken);
					ps.setString(7, firstName);
					ps.setString(8, lastName);
					ps.setString(9, email);
					ps.setString(10, examStartDateTime);
					ps.setString(11, examEndDateTime);
					ps.setString(12, accessStartDateTime);
					ps.setString(13, accessEndDateTime);
					ps.setString(14, reportingStartDateTime);
					ps.setString(15, reportingFinishDateTime);
					ps.setString(16, sifySubjectCode);
					ps.setString(17, scheduleName);
					ps.setString(18, scheduleAccessKey);
					ps.setString(19, joinURL);
					ps.setString(20, examCenterName);
					
					return ps;
				}
			};
			rowsAffected = jdbcTemplate.update(psc);
			if(rowsAffected == 1) {
				isSaved = Boolean.TRUE;
			}
		} catch (Exception e) {
			// 
			logger.error("MettlDAO.saveScheduleInfoWaitingRoom : Exception : " + e.getMessage());
			throw e;
		} finally {
			logger.info("MettlDAO.saveScheduleInfoWaitingRoom (row inserted count) : (" + rowsAffected + ")");
		}
		return isSaved;
	}
	
	public synchronized Integer deleteScheduleInfo(final MettlRegisterCandidateBean bean) {
		Integer retVal = null;
		try {
			this.start_Transaction_U_PR("DeletePGScheduleInfo");
			retVal = jdbcTemplate.execute(
					"delete from exam.exams_pg_scheduleinfo_mettl where examStartDateTime = STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')",
					new PreparedStatementCallback<Integer>() {
						@Override
						public Integer doInPreparedStatement(PreparedStatement ps)
								throws SQLException, DataAccessException {
							Integer val = null;
							ps.setString(1, bean.getExamStartDateTime());
							val = ps.executeUpdate();
							return val;
						}
					});
			bean.setStatus(KEY_SUCCESS);
			bean.setMessage("Total Rows Deleted : " + retVal);
			this.end_Transaction(Boolean.TRUE);
			logger.info("MettlDAO.deleteScheduleInfo : Total Rows Deleted : " + retVal);
		} catch (Exception ex) {
			this.end_Transaction(Boolean.FALSE);
			logger.error("MettlDAO.deleteScheduleInfo : " + ex.getMessage());

			bean.setStatus(KEY_ERROR);
			bean.setMessage(ex.getMessage());
		}
		return retVal;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public synchronized List<MettlRegisterCandidateBean> fetchAll_ScheduleInfo(final MettlRegisterCandidateBean bean) {
		List<MettlRegisterCandidateBean> list1 = null;
		StringBuffer strBuf = null;
		String query = null;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append(S_SELECT).append(SPACE);
			strBuf.append(C_SUBJECT).append(COMMA).append(C_YEAR).append(COMMA).append(C_MONTH).append(COMMA).append(C_TRACKID).append(COMMA);
			strBuf.append("sapid").append(COMMA).append(C_TESTTAKEN).append(COMMA).append(C_FIRSTNAME).append(COMMA).append(C_LASTNAME).append(COMMA);
			strBuf.append(C_EMAILID).append(COMMA);
			strBuf.append("date_format(examStartDateTime,'%Y-%m-%d %H:%i:%s') as examStartDateTime").append(COMMA);
			strBuf.append("date_format(examEndDateTime,'%Y-%m-%d %H:%i:%s') as examEndDateTime").append(COMMA);
			strBuf.append("date_format(accessStartDateTime,'%Y-%m-%d %H:%i:%s') as accessStartDateTime").append(COMMA);
			strBuf.append("date_format(accessEndDateTime,'%Y-%m-%d %H:%i:%s') as accessEndDateTime").append(COMMA);
			strBuf.append(C_SIFYSUBJECTCODE).append(COMMA).append(C_SCHEDULENAME).append(COMMA).append(C_ACCESSKEY).append(COMMA);
			strBuf.append(C_JOINURL).append(COMMA).append(C_EXAMCENTERNAME).append(SPACE);
			strBuf.append(S_FROM).append(SPACE).append(TABLE_EXAMS_PG_SCHEDULEINFO_METTL).append(SPACE);
			strBuf.append(S_WHERE).append(SPACE);
			strBuf.append(C_EXAMSTARTDATETIME).append(S_EQUALTO).append("STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')");
			query = strBuf.toString();
			
			//this.startTransaction("fetchAll_ScheduleInfo", Boolean.TRUE);
			
			list1 = jdbcTemplate.execute(query, new PreparedStatementCallback<List<MettlRegisterCandidateBean>>() {
				@Override
				public List<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setString(1, bean.getExamStartDateTime());
					ps.execute();
					ResultSet rs = ps.getResultSet();

					List<MettlRegisterCandidateBean> mList = null;
					MettlRegisterCandidateBean bean = null;
					mList = new ArrayList<MettlRegisterCandidateBean>();
					while (rs.next()) {
						bean = new MettlRegisterCandidateBean();

						bean.setSubject(rs.getString("subject"));
						bean.setYear(rs.getString("year"));
						bean.setMonth(rs.getString("month"));
						bean.setTrackId(rs.getString("trackId"));
						bean.setSapId(rs.getString("sapid"));
						bean.setTestTaken(rs.getString("testTaken"));
						bean.setFirstName(rs.getString("firstname"));
						bean.setLastName(rs.getString("lastname"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setExamStartDateTime(rs.getString("examStartDateTime"));
						bean.setExamEndDateTime(rs.getString("examEndDateTime"));
						bean.setAccessStartDateTime(rs.getString("accessStartDateTime"));
						bean.setAccessEndDateTime(rs.getString("accessEndDateTime"));
						bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
						bean.setScheduleName(rs.getString("scheduleName"));
						bean.setScheduleAccessKey(rs.getString("acessKey"));
						bean.setJoinURL(rs.getString("joinURL"));

						mList.add(bean);
					}
					logger.info("MettlDAO.fetchAll_ScheduleInfo : Total ScheduleInfo(s) :"+mList.size());
					return mList;
				}
			});
			bean.setStatus(KEY_SUCCESS);
			bean.setMessage("Total Rows fetched : " + list1.size());
			//this.endTransaction(Boolean.TRUE);
		} catch (Exception ex) {
			logger.error("MettlDAO.fetchAll_ScheduleInfo : " + ex.getMessage());

			bean.setStatus(KEY_ERROR);
			bean.setMessage(ex.getMessage());
			//this.endTransaction(Boolean.FALSE);
		} finally {
			emptyStringBuffer(strBuf);
		}
		return list1;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public synchronized List<MettlRegisterCandidateBean> fetchAll_ScheduleInfo_Waiting_Room(final MettlRegisterCandidateBean bean) {
		List<MettlRegisterCandidateBean> list1 = null;
		StringBuffer strBuf = null;
		String query = null;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append(S_SELECT).append(SPACE);
			strBuf.append(C_SUBJECT).append(COMMA).append(C_YEAR).append(COMMA).append(C_MONTH).append(COMMA).append(C_TRACKID).append(COMMA);
			strBuf.append("sapid").append(COMMA).append(C_TESTTAKEN).append(COMMA).append(C_FIRSTNAME).append(COMMA).append(C_LASTNAME).append(COMMA);
			strBuf.append(C_EMAILID).append(COMMA);
			strBuf.append("date_format(examStartDateTime,'%Y-%m-%d %H:%i:%s') as examStartDateTime").append(COMMA);
			strBuf.append("date_format(examEndDateTime,'%Y-%m-%d %H:%i:%s') as examEndDateTime").append(COMMA);
			strBuf.append("date_format(accessStartDateTime,'%Y-%m-%d %H:%i:%s') as accessStartDateTime").append(COMMA);
			strBuf.append("date_format(accessEndDateTime,'%Y-%m-%d %H:%i:%s') as accessEndDateTime").append(COMMA);
			strBuf.append("date_format(reporting_start_date_time,'%Y-%m-%d %H:%i:%s') as reporting_start_date_time").append(COMMA);
			strBuf.append("date_format(reporting_finish_date_time,'%Y-%m-%d %H:%i:%s') as reporting_finish_date_time").append(COMMA);
			strBuf.append(C_SIFYSUBJECTCODE).append(COMMA).append(C_SCHEDULENAME).append(COMMA).append(C_ACCESSKEY).append(COMMA);
			strBuf.append(C_JOINURL).append(COMMA).append(C_EXAMCENTERNAME).append(SPACE);
			strBuf.append(S_FROM).append(SPACE).append(TABLE_EXAMS_PG_SCHEDULEINFO_METTL).append(SPACE);
			strBuf.append(S_WHERE).append(SPACE);
			strBuf.append(C_EXAMSTARTDATETIME).append(S_EQUALTO).append("STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')");
			query = strBuf.toString();
			
			//this.startTransaction("fetchAll_ScheduleInfo", Boolean.TRUE);
			
			list1 = jdbcTemplate.execute(query, new PreparedStatementCallback<List<MettlRegisterCandidateBean>>() {
				@Override
				public List<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setString(1, bean.getExamStartDateTime());
					ps.execute();
					ResultSet rs = ps.getResultSet();
					
					List<MettlRegisterCandidateBean> mList = null;
					MettlRegisterCandidateBean bean = null;
					mList = new ArrayList<MettlRegisterCandidateBean>();
					while (rs.next()) {
						bean = new MettlRegisterCandidateBean();
						
						bean.setSubject(rs.getString("subject"));
						bean.setYear(rs.getString("year"));
						bean.setMonth(rs.getString("month"));
						bean.setTrackId(rs.getString("trackId"));
						bean.setSapId(rs.getString("sapid"));
						bean.setTestTaken(rs.getString("testTaken"));
						bean.setFirstName(rs.getString("firstname"));
						bean.setLastName(rs.getString("lastname"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setExamStartDateTime(rs.getString("examStartDateTime"));
						bean.setExamEndDateTime(rs.getString("examEndDateTime"));
						bean.setAccessStartDateTime(rs.getString("accessStartDateTime"));
						bean.setAccessEndDateTime(rs.getString("accessEndDateTime"));
						bean.setReportStartDateTime(rs.getString("reporting_start_date_time"));
						bean.setReportFinishDateTime(rs.getString("reporting_finish_date_time"));
						bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
						bean.setScheduleName(rs.getString("scheduleName"));
						bean.setScheduleAccessKey(rs.getString("acessKey"));
						bean.setJoinURL(rs.getString("joinURL"));
						
						mList.add(bean);
					}
					logger.info("MettlDAO.fetchAll_ScheduleInfo_Waiting_Room : Total ScheduleInfo(s) :"+mList.size());
					return mList;
				}
			});
			bean.setStatus(KEY_SUCCESS);
			bean.setMessage("Total Rows fetched : " + list1.size());
			//this.endTransaction(Boolean.TRUE);
		} catch (Exception ex) {
			logger.error("MettlDAO.fetchAll_ScheduleInfo_Waiting_Room : " + ex.getMessage());
			
			bean.setStatus(KEY_ERROR);
			bean.setMessage(ex.getMessage());
			//this.endTransaction(Boolean.FALSE);
		} finally {
			emptyStringBuffer(strBuf);
		}
		return list1;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean batchSaveScheduleInfo(final List<MettlRegisterCandidateBean> list) {
		boolean isSuccess = Boolean.FALSE;
		String query = null;
		
		try {	
			query = "INSERT INTO exam.exams_pg_scheduleinfo_history_mettl ";
			query += "(subject, year, month, trackId, sapid, testTaken, firstname, lastname, emailId,"; 
			query += "examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime, ";
			query += "sifySubjectCode, scheduleName, acessKey, joinURL, examCenterName) ";
			query += "VALUES ";
			query += "(?, ?, ?, ?, ?, ?, ?, ?, ?,";
			query += "STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),";
			query += "?, ?, ?, ?, ?)";
	
			//this.startTransaction("BatchSaveScheduleInfo", Boolean.FALSE);
	
			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSubject());
					ps.setString(2, list.get(arg1).getYear());
					ps.setString(3, list.get(arg1).getMonth());
					ps.setString(4, list.get(arg1).getTrackId());
					ps.setString(5, list.get(arg1).getSapId());
					ps.setString(6, list.get(arg1).getTestTaken());
					ps.setString(7, list.get(arg1).getFirstName());
					ps.setString(8, list.get(arg1).getLastName());
					ps.setString(9, list.get(arg1).getEmailAddress());
					ps.setString(10, list.get(arg1).getExamStartDateTime());
					ps.setString(11, list.get(arg1).getExamEndDateTime());
					ps.setString(12, list.get(arg1).getAccessStartDateTime());
					ps.setString(13, list.get(arg1).getAccessEndDateTime());
					ps.setString(14, list.get(arg1).getSifySubjectCode());
					ps.setString(15, list.get(arg1).getScheduleName());
					ps.setString(16, list.get(arg1).getScheduleAccessKey());
					ps.setString(17, list.get(arg1).getJoinURL());
					ps.setString(18, list.get(arg1).getExamCenterName());
				}
				
				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			logger.info("MettlDAO.batchSaveScheduleInfo : Total Rows created(backedup) : " + arrResults.length);
			/*for(int k = 0; k < arrResults.length; k++) {
				logger.info("Array : " + k + " > " + arrResults[k]);
			}*/
			isSuccess = Boolean.TRUE;
			//this.endTransaction(isSuccess);
		} catch (Exception e) {
			//this.endTransaction(Boolean.FALSE);
			logger.error("MettlDAO.batchSaveScheduleInfo : " + e.getMessage());
			throw e;
		}  finally {
			query = null;
		}
		return isSuccess;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public boolean batchSaveScheduleInfoWaitingRoom(final List<MettlRegisterCandidateBean> list) {
		boolean isSuccess = Boolean.FALSE;
		String query = null;
		
		try {	
			query = "INSERT INTO exam.exams_pg_scheduleinfo_history_mettl ";
			query += "(subject, year, month, trackId, sapid, testTaken, firstname, lastname, emailId,"; 
			query += "examStartDateTime, examEndDateTime, accessStartDateTime,accessEndDateTime, ";
			query += "reporting_start_date_time,reporting_finish_date_time, ";
			query += "sifySubjectCode, scheduleName, acessKey, joinURL, examCenterName) ";
			query += "VALUES ";
			query += "(?, ?, ?, ?, ?, ?, ?, ?, ?,";
			query += "STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),";
			query += "STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),";
			query += "STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),";
			query += "?, ?, ?, ?, ?)";
			
			//this.startTransaction("BatchSaveScheduleInfo", Boolean.FALSE);
			
			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					ps.setString(1, list.get(arg1).getSubject());
					ps.setString(2, list.get(arg1).getYear());
					ps.setString(3, list.get(arg1).getMonth());
					ps.setString(4, list.get(arg1).getTrackId());
					ps.setString(5, list.get(arg1).getSapId());
					ps.setString(6, list.get(arg1).getTestTaken());
					ps.setString(7, list.get(arg1).getFirstName());
					ps.setString(8, list.get(arg1).getLastName());
					ps.setString(9, list.get(arg1).getEmailAddress());
					ps.setString(10, list.get(arg1).getExamStartDateTime());
					ps.setString(11, list.get(arg1).getExamEndDateTime());
					ps.setString(12, list.get(arg1).getAccessStartDateTime());
					ps.setString(13, list.get(arg1).getAccessEndDateTime());
					ps.setString(14, list.get(arg1).getReportStartDateTime());
					ps.setString(15, list.get(arg1).getReportFinishDateTime());
					ps.setString(16, list.get(arg1).getSifySubjectCode());
					ps.setString(17, list.get(arg1).getScheduleName());
					ps.setString(18, list.get(arg1).getScheduleAccessKey());
					ps.setString(19, list.get(arg1).getJoinURL());
					ps.setString(20, list.get(arg1).getExamCenterName());
				}
				
				@Override
				public int getBatchSize() {
					return list.size();
				}
			});
			logger.info("MettlDAO.batchSaveScheduleInfo : Total Rows created(backedup) : " + arrResults.length);
			/*for(int k = 0; k < arrResults.length; k++) {
				logger.info("Array : " + k + " > " + arrResults[k]);
			}*/
			isSuccess = Boolean.TRUE;
			//this.endTransaction(isSuccess);
		} catch (Exception e) {
			//this.endTransaction(Boolean.FALSE);
			logger.error("MettlDAO.batchSaveScheduleInfo : " + e.getMessage());
			throw e;
		}  finally {
			query = null;
		}
		return isSuccess;
	}
	
	public synchronized List<MettlRegisterCandidateReportBean> fetchAll_Candidates_ErrorList(final String examDate) {
		List<MettlRegisterCandidateReportBean> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_candidates_errors(?)}",
				new CallableStatementCallback<List<MettlRegisterCandidateReportBean>>() {
					@Override
					public List<MettlRegisterCandidateReportBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setString(1, examDate);
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlRegisterCandidateReportBean> mList = null;
						MettlRegisterCandidateReportBean bean = null;
						mList = new ArrayList<MettlRegisterCandidateReportBean>();
						while (rs.next()) {
							bean = new MettlRegisterCandidateReportBean();
							bean.setSapId(rs.getString("sapId"));
							
							bean.setSubject(rs.getString("subject"));
							bean.setExamDate(rs.getString("examDate"));
							bean.setExamTime(rs.getString("examTime"));
							bean.setExamEndTime(rs.getString("examEndTime"));
							
							bean.setAssessmentId(rs.getString("assessmentId"));
							bean.setScheduleAccessKey(rs.getString("scheduleAccessKey"));
							bean.setMessage(rs.getString("registrationMessage"));
							
							mList.add(bean);
						}
						return mList;
					}
				});
		if(null != list1 && !list1.isEmpty()) {
			logger.info("MettlDAO.fetchAll_Candidates_ErrorList : List Size "+ list1.size());
		}
		return list1;
	}
	
	public synchronized List<MettlRegisterCandidateReportBean> fetchAll_Candidates_SummaryList(final String examDate) {
		List<MettlRegisterCandidateReportBean> list2 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list2 = jdbcTemplate.execute("{call exam.getall_candidates_summary(?)}",
				new CallableStatementCallback<List<MettlRegisterCandidateReportBean>>() {
					@Override
					public List<MettlRegisterCandidateReportBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setString(1, examDate);
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlRegisterCandidateReportBean> sList = null;
						MettlRegisterCandidateReportBean bean = null;
						sList = new ArrayList<MettlRegisterCandidateReportBean>();
						while (rs.next()) {
							bean = new MettlRegisterCandidateReportBean();
							bean.setExamDate(rs.getString("Date-EB"));
							
							bean.setTotalCandidatesExamBookings(rs.getString("TotalCandidates-EB"));
							bean.setTotalCandidates(rs.getString("TotalCandidates"));
							
							bean.setSuccessSlot1(rs.getString("Slot1-S"));
							bean.setErrorSlot1(rs.getString("Slot1-E"));
							bean.setSuccessSlot2(rs.getString("Slot2-S"));
							bean.setErrorSlot2(rs.getString("Slot2-E"));
							bean.setSuccessSlot3(rs.getString("Slot3-S"));
							bean.setErrorSlot3(rs.getString("Slot3-E"));

							bean.setTotalErrors(rs.getString("Errors"));
							bean.setTotalFailures(rs.getString("Failures"));
							
							sList.add(bean);
						}
						return sList;
					}
				});
		if(null != list2 && !list2.isEmpty()) {
			logger.info("MettlDAO.fetchAll_Candidates_SummaryList : List Size "+ list2.size());
		}
		return list2;
	}
	
	//register all candidates
	
	public synchronized List<String> fetchAllDates(final String examYear, final String examMonth) {
		List<String> list1 = null;
		StringBuffer strBuf = null;
		String query = null;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append(S_SELECT).append(SPACE).append(S_DISTINCT).append(SPACE).append(C_DATE).append(SPACE);
			strBuf.append(S_FROM).append(SPACE).append(TABLE_EXAM_TIMETABLE).append(SPACE);
			strBuf.append(S_WHERE).append(SPACE);
			strBuf.append(C_EXAMYEAR).append(SPACE).append(S_EQUALTO).append(SPACE).append(Q_MARK).append(SPACE).append(S_AND).append(SPACE);
			strBuf.append(C_EXAMMONTH).append(SPACE).append(S_EQUALTO).append(SPACE).append(Q_MARK).append(SPACE);
			strBuf.append(S_ORDER_BY).append(SPACE).append(C_DATE);
			query = strBuf.toString();
			
			logger.info("MettlDAO.fetchAllDates : Query : "+query);
			
			this.start_Transaction_R_PR("fetchAllDates");
			
			list1 = jdbcTemplate.execute(query, new PreparedStatementCallback<List<String>>() {
				@Override
				public List<String> doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setString(1, examYear);
					ps.setString(2, examMonth);
					
					ps.execute();
					ResultSet rs = ps.getResultSet();

					List<String> dList = null;
					dList = new LinkedList<String>();
					while (rs.next()) {
						dList.add(rs.getString(C_DATE));
					}
					logger.info("MettlDAO.fetchAllDates : Total Date(s) : "+dList.size());
					return dList;
				}
			});
			
			this.end_Transaction(Boolean.TRUE);
		} catch (Exception ex) {
			logger.error("MettlDAO.fetchAllDates : " + ex.getMessage());
			this.end_Transaction(Boolean.FALSE);

		} finally {
			emptyStringBuffer(strBuf);
		}
		return list1;
	}
	
	public boolean createPartition(final String tableName, final String columnName, final Map<String, String> dateMap) {
		Boolean isSaved = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;
		Set<Entry<String, String>> setEntry = null;
		Iterator<Map.Entry<String, String>> iterator = null;
		Entry<String, String> entry = null;
		strBuf = new StringBuffer();
		try {
			strBuf.append("ALTER TABLE ").append(tableName);// exam.exams_pg_scheduleinfo_mettl;
			strBuf.append(" PARTITION BY RANGE COLUMNS (").append(columnName).append(") (");// examStartDateTime
			setEntry = dateMap.entrySet();
			iterator = setEntry.iterator();
			while (iterator.hasNext()) {
				entry = iterator.next();
				if (null == entry.getValue()) {
					strBuf.append("PARTITION p").append(entry.getKey()).append(" VALUES LESS THAN MAXVALUE");
				} else {
					strBuf.append("PARTITION p").append(entry.getKey()).append(" VALUES LESS THAN ('").append(entry.getValue()).append("'), ");
				}
			}
			strBuf.append(" )");
			query = strBuf.toString();
			logger.info("MettlDAO.createPartition : Partition Query : "+query);
			
			this.start_Transaction_U_PR("createPartition");
			isSaved = createPartition(query);
			this.end_Transaction(isSaved);
		} catch (Exception e) {
			// 
			logger.error("MettlDAO.createPartition : Exception : " + e.getMessage());
			this.end_Transaction(Boolean.FALSE);
			throw e;
		} finally {
			emptyStringBuffer(strBuf);
			strBuf = null;
			query = null;
		}
		return isSaved;
	}
	
	protected synchronized boolean createPartition(final String query) {
		Boolean isSaved = Boolean.FALSE;

		try {
			jdbcTemplate.execute(query);
			isSaved = Boolean.TRUE;
		} catch (Exception e) {
			//
			throw e;
		}
		return isSaved;
	}
	
	public Integer fetchPartitionCount(final String schemaName, final String tableName) {
		logger.info("Entering MettlDAO.fetchPartitionCount");
		String sql = null;
		StringBuffer strBuf = null;
		Integer totalPartitions = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("SELECT COUNT(PARTITION_NAME) totalPartitionCount FROM INFORMATION_SCHEMA.PARTITIONS");
			strBuf.append(" WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
	
			sql = strBuf.toString();
			emptyStringBuffer(strBuf);
			logger.info("MettlDAO.fetchPartitionCount : Query :" + sql);
			
			this.start_Transaction_R_PR("fetchPartitionCount");
			
			totalPartitions = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {
	
				@Override
				public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					int idx = 1;
					arg0.setString(idx++, schemaName);
					arg0.setString(idx++, tableName);
	
					Integer count = null;
					ResultSet rs = arg0.executeQuery();
					while (rs.next()) {
						count = rs.getInt("totalPartitionCount");
					}
					return count;
				}
			});
			
			this.end_Transaction(Boolean.TRUE);
		} catch(Exception e) {
			this.end_Transaction(Boolean.FALSE);
			logger.error("MettlDAO.fetchPartitionCount : Exception : " + e.getMessage());
			throw e;
		}
		logger.info("MettlDAO.fetchPartitionCount : Total Partition(s) : " + totalPartitions);
		return totalPartitions;
	}
	
	public static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
	
	
	public synchronized List<MettlRegisterCandidateBean> getOne_Candidate_Schedules(final String examDate,final String sapid,final String examMonth,final String examYear) {
		logger.info("Entering MettlDAO.getOne_Candidate_Schedules : examDate : "+examDate);
		List<MettlRegisterCandidateBean> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getOne_Candidate_Schedules(?,?,?,?)}",
				new CallableStatementCallback<List<MettlRegisterCandidateBean>>() {
					@Override
					public List<MettlRegisterCandidateBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setString(1, examDate);
						cs.setString(2, sapid);
						cs.setString(3, examMonth);
						cs.setString(4, examYear);
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						List<MettlRegisterCandidateBean> mList = null;
						MettlRegisterCandidateBean bean = null;
						mList = new ArrayList<MettlRegisterCandidateBean>();
						while (rs.next()) {
							bean = new MettlRegisterCandidateBean();
							bean.setSapId(rs.getString("studentid"));
							bean.setFirstName(rs.getString("firstName"));
							bean.setLastName(rs.getString("lastName"));
							bean.setEmailAddress(rs.getString("emailId"));
							bean.setCandidateImage(rs.getString("imageUrl"));
							bean.setRegistrationImage(rs.getString("imageUrl"));
							bean.setScheduleAccessKey(rs.getString("schedule_accessKey"));
							bean.setScheduleAccessURL(rs.getString("schedule_accessUrl"));
							if("0".equals(rs.getString("openLinkFlag"))) {
								bean.setOpenLinkFlag(Boolean.TRUE);
							} else {
								bean.setOpenLinkFlag(Boolean.FALSE);
							}
							
							bean.setSubject(rs.getString("subject"));
							bean.setBooked(rs.getString("booked"));
							bean.setYear(rs.getString("year"));
							bean.setMonth(rs.getString("month"));
							bean.setExamDate(rs.getString("examDate"));
							bean.setExamTime(rs.getString("examTime"));
							bean.setExamEndTime(rs.getString("examEndTime"));
							
							bean.setTrackId(rs.getString("trackId"));
							bean.setTestTaken(rs.getString("testTaken"));
							bean.setSifySubjectCode(rs.getString("sifySubjectCode"));
							bean.setExamStartDateTime(rs.getString("examStartDateTime"));
							bean.setExamEndDateTime(rs.getString("examEndDateTime"));
							bean.setAccessStartDateTime(rs.getString("accessStartDateTime"));
							bean.setAccessEndDateTime(rs.getString("accessEndDateTime"));
							bean.setReportStartDateTime(rs.getString("reporting_start_date_time"));
							bean.setReportFinishDateTime(rs.getString("reporting_finish_date_time"));
							bean.setScheduleName(rs.getString("scheduleName"));
							bean.setExamCenterName(rs.getString("examCenterName"));
							
							mList.add(bean);
						}
						return mList;
					}
				});
		
		if(null != list1 && !list1.isEmpty()) {
			logger.info("MettlDAO.getOne_Candidate_Schedules: List Size : "+ list1.size());
		}
		return list1;
	}
	
	
	//----//
	/*DELIMITER $$

	CREATE DEFINER=`root`@`localhost` PROCEDURE `getall_assessments`()
	BEGIN

	Select
	exa.assessmentId, exa.name, exa.sifyCode,
	concat(SUBSTRING(dayname(date),1,3),', ', day(date),' ', SUBSTRING(monthname(date),1,3),' ', year(date)) as 'startsOnDate', 
	concat(sifySubjectCode,DATE_FORMAT(date,"%w%m%e%Y"),DATE_FORMAT(startTime,"%H%i%s"),DATE_FORMAT(endTime,"%H%i%s")) as CUSTOM_URL_ID, t.*, 
	psss.*,count(*) as duplicateEntry from (  
	SELECT examYear,examMonth,date,startTime,endTime,time(date_add(str_to_date(concat(date,' ',startTime), '%Y-%m-%d %H:%i:%s'), INTERVAL 60 MINUTE)) endTime2,PrgmStructApplicable 
	FROM exam.timetable where examYear=2020 and examMonth='Jun'
	) t 
	Inner join (SELECT 
	    p_s_s.subject, p_s_s.sifySubjectCode,ps.program_structure
	FROM
	    exam.program_sem_subject p_s_s 
	        inner JOIN
	exam.consumer_program_structure cpp on cpp.id = p_s_s.consumerProgramStructureId 
	inner JOIN
	    exam.program_structure ps on ps.id = cpp.programStructureId
	WHERE
	    p_s_s.subject NOT IN ('Project' , 'Module 4 - Project',
	        'Simulation: Mimic Pro',
	        'Simulation: Mimic Social')
	        AND p_s_s.active = 'Y' and p_s_s.sifySubjectCode <> 0 
	        and ps.program_structure in ('Jul2014','Jul2017','Jul2018','Jul2019')
	GROUP BY p_s_s.sifySubjectCode,ps.program_structure) psss on t.PrgmStructApplicable = psss.program_structure
	 INNER join
	(select assessmentId, sifyCode, name from exam.pg_assessment order by name asc) exa 
	on exa.sifyCode = psss.sifySubjectCode
	group by sifySubjectCode,date,startTime,endTime
	order by sifySubjectCode,subject,date,startTime,endTime asc;

	END*/
	//----//

}