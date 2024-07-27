/**
 * 
 */
package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public class RemarksGradeDAO extends BaseDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	public static final String DB_SCHEMA_EXAM = "exam";
	public static final String DB_SCHEMA_REMARKPF = "remarkpassfail";
	public static final String TABLE_MARKS = "marks";
	public static final String TABLE_MARKS_HISTORY = "marks_history";
	public static final String TABLE_STAGING_PF = "staging_passfail";
	public static final String TABLE_PF = "passfail";
	public static final String TABLE_ASSIGNMENTS = "assignments";
	public static final String TABLE_ASSIGNMENTSUBMISSION = "assignmentsubmission";
	public static final String TABLE_STUDENTS = "students";
	public static final String TABLE_REGISTRATION = "registration";
	public static final String TABLE_CONSUMERPROGRAMSTRUCTURE = "consumer_program_structure";
	public static final String TABLE_CONSUMER_TYPE = "consumer_type";
	public static final String TABLE_PROGRAM_STRUCTURE = "program_structure";
	public static final String TABLE_PROGRAM = "program";
	public static final String TABLE_MDM_SUBJECTCODE = "mdm_subjectcode";
	public static final String TABLE_MDM_SUBJECTCODE_MAPPING = "mdm_subjectcode_mapping";
	
	//public static final String SOFTSKILLS_MDM_SUBJECTCODE_ID = "258, 429, 378, 359";//('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')
	public static final ArrayList<Integer> SOFTSKILLS_MDM_SUBJECTCODE_ID = new ArrayList<Integer>(
			Arrays.asList(258, 429, 378, 359));// ('Soft Skills for Managers','Employability Skills - II
												// Tally','Start your Start up','Design Thinking')
	
	public static final String SOFTSKILLS_PROGRAM_BBA = "BBA";
	public static final String SOFTSKILLS_PROGRAM_BCOM = "B.Com";
	public static final String SOFTSKILLS_PROGRAM_BBA_BA = "BBA-BA";
	public static final Integer SOFTSKILLS_PASSSCORE = 15;
	
	public static final String CHAR_Y = "Y";
	
	private static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	protected static String QUERY_FETCH_PROGRAMSEMSUBJECT_ID = null;
	
	static {
		
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID = "SELECT scm.id as programSemSubjectId FROM";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " exam.mdm_subjectcode sc";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " JOIN exam.mdm_subjectcode_mapping scm on scm.subjectCodeId = sc.id and sc.subjectname = ?";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " JOIN exam.students s";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " ON s.consumerProgramStructureId = scm.consumerProgramStructureId";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " AND scm.active = '" + RemarksGradeDAO.CHAR_Y + "' AND s.sapid = ?";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " AND s.sem = (SELECT max(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " JOIN exam.program pgm ON pgm.id = cps.programId";
		QUERY_FETCH_PROGRAMSEMSUBJECT_ID += " AND pgm.code = ?";
		
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
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

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	public void fetchProgramSemSubjectId(final List<RemarksGradeBean> list) {
		logger.info("Entering RemarksGradeDAO : fetchProgramSemSubjectId");
		RemarksGradeBean bean = null;
		Integer programSemSubjectId = null;

		for (int f = 0; f < list.size(); f++) {
			bean = list.get(f);
			programSemSubjectId = fetchProgramSemSubjectId(bean.getSubject(), bean.getSapid(), bean.getProgram());
			bean.setProgramSemSubjectId(programSemSubjectId);
		}
		logger.info("Exiting RemarksGradeDAO : fetchProgramSemSubjectId");
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	protected Integer fetchProgramSemSubjectId(final String subject, final String sapId, final String program) {
		Integer programSemSubjectId = null;
		String sql = QUERY_FETCH_PROGRAMSEMSUBJECT_ID;

		programSemSubjectId = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				Integer pssId = null;
				arg0.setString(1, subject);
				arg0.setString(2, sapId);
				arg0.setString(3, program);

				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					pssId = rs.getInt("programSemSubjectId");
				}
				return pssId;
			}
		});

		return programSemSubjectId;
	}
	
	public synchronized boolean batchSaveMarks(final boolean slaveTransaction, final List<RemarksGradeBean> list,
			final String year, final String month, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveMarks");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS);
			strBuf.append(" (sapid, subject, programSemSubjectId,");
			strBuf.append(" assignmentYear, assignmentMonth, iaScore, status, isResultLive, processed,");
			strBuf.append(" active, createdBy, createdDate, lastModifiedBy, lastModifiedDate)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,?,");
			//strBuf.append(" ?,?,current_timestamp,?,current_timestamp)");
			strBuf.append(" ?,?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(",?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(")");
			//strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, iaScore = ?, totalScore = null, ");
			strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, iaScore = ?, totalScore = ").append(RemarksGradeBean.DB_NULL).append(", ");
			//strBuf.append(" graceMarks = null, ");
			strBuf.append(" status = ?, isResultLive = ?, processed = ?,");
			strBuf.append(
					" active = ?, lastModifiedBy = ?, lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveMarks : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("BatchSaveUGMarks");
				start_Transaction_U_PR("BatchSaveUGMarks");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSapid());
					ps.setString(2, list.get(arg1).getSubject());
					ps.setInt(3, list.get(arg1).getProgramSemSubjectId());
					ps.setString(4, year);//examYear
					ps.setString(5, month);//examMonth
					//ps.setInt(6, toInteger(list.get(arg1).getScoreIA()));
					ps.setInt(6, list.get(arg1).getScoreIA());
					ps.setString(7, list.get(arg1).getStatus());
					ps.setInt(8, list.get(arg1).getResultLive());
					ps.setInt(9, list.get(arg1).getProcessed());
					ps.setString(10, list.get(arg1).getActive());
					ps.setString(11, userId);
					ps.setString(12, userId);
					
					ps.setString(13, list.get(arg1).getSubject());
					ps.setInt(14, list.get(arg1).getScoreIA());
					ps.setString(15, list.get(arg1).getStatus());
					ps.setInt(16, list.get(arg1).getResultLive());
					ps.setInt(17, list.get(arg1).getProcessed());
					ps.setString(18, list.get(arg1).getActive());
					ps.setString(19, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveMarks : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveMarks : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized boolean batchSaveMarks_Absent(final boolean slaveTransaction, final List<RemarksGradeBean> list,
			final String year, final String month, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveMarks_Absent");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS);
			strBuf.append(" (sapid, subject, programSemSubjectId,");
			strBuf.append(" assignmentYear, assignmentMonth, status, isResultLive, processed,");
			strBuf.append(" active, createdBy, createdDate, lastModifiedBy, lastModifiedDate)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,");
			//strBuf.append(" ?,?,current_timestamp,?,current_timestamp)");
			strBuf.append(" ?,?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(",?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(")");
			//strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, iaScore = null, totalScore = null, ");
			strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, iaScore = ").append(RemarksGradeBean.DB_NULL).append(", totalScore = ").append(RemarksGradeBean.DB_NULL).append(", ");
			//strBuf.append(" graceMarks = null, ");
			strBuf.append(" status = ?, isResultLive = ?, processed = ?,");
			strBuf.append(
					" active = ?, lastModifiedBy = ?, lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveMarks_Absent : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("BatchSaveUGMarks_Absent");
				start_Transaction_U_PR("BatchSaveUGMarks_Absent");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSapid());
					ps.setString(2, list.get(arg1).getSubject());
					ps.setInt(3, list.get(arg1).getProgramSemSubjectId());
					ps.setString(4, year);//examYear
					ps.setString(5, month);//examMonth
					
					ps.setString(6, list.get(arg1).getStatus());
					ps.setInt(7, list.get(arg1).getResultLive());
					ps.setInt(8, list.get(arg1).getProcessed());
					ps.setString(9, list.get(arg1).getActive());
					ps.setString(10, userId);
					ps.setString(11, userId);
					
					ps.setString(12, list.get(arg1).getSubject());
					ps.setString(13, list.get(arg1).getStatus());
					ps.setInt(14, list.get(arg1).getResultLive());
					ps.setInt(15, list.get(arg1).getProcessed());
					ps.setString(16, list.get(arg1).getActive());
					ps.setString(17, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveMarks_Absent : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveMarks_Absent : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized boolean batchSaveMarksHistory(final boolean slaveTransaction, final List<RemarksGradeBean> list,
			final String year, final String month, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveMarksHistory");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS_HISTORY);
			strBuf.append(" (sapid, subject, programSemSubjectId,");
			strBuf.append(" assignmentYear, assignmentMonth, iaScore, status, isResultLive, processed,");
			strBuf.append(" active, createdBy, createdDate, lastModifiedBy, lastModifiedDate)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,?,");
			//strBuf.append(" ?,?,current_timestamp,?,current_timestamp)");
			strBuf.append(" ?,?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(",?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(")");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveMarksHistory : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("BatchSaveUGMarksHistory");
				start_Transaction_U_PR("BatchSaveUGMarksHistory");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSapid());
					ps.setString(2, list.get(arg1).getSubject());
					ps.setInt(3, list.get(arg1).getProgramSemSubjectId());
					ps.setString(4, year);//examYear
					ps.setString(5, month);//examMonth
					//ps.setInt(6, toInteger(list.get(arg1).getScoreIA()));
					ps.setInt(6, list.get(arg1).getScoreIA());
					ps.setString(7, list.get(arg1).getStatus());
					ps.setInt(8, list.get(arg1).getResultLive());
					ps.setInt(9, list.get(arg1).getProcessed());
					ps.setString(10, list.get(arg1).getActive());
					ps.setString(11, userId);
					ps.setString(12, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveMarksHistory : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveMarksHistory : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized boolean batchSaveMarksHistory_Absent(final boolean slaveTransaction,
			final List<RemarksGradeBean> list, final String year, final String month, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveMarksHistory_Absent");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS_HISTORY);
			strBuf.append(" (sapid, subject, programSemSubjectId,");
			strBuf.append(" assignmentYear, assignmentMonth, status, isResultLive, processed,");
			strBuf.append(" active, createdBy, createdDate, lastModifiedBy, lastModifiedDate)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,");
			//strBuf.append(" ?,?,current_timestamp,?,current_timestamp)");
			strBuf.append(" ?,?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(",?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(")");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveMarksHistory_Absent : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("BatchSaveUGMarksHistory_Absent");
				start_Transaction_U_PR("BatchSaveUGMarksHistory_Absent");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSapid());
					ps.setString(2, list.get(arg1).getSubject());
					ps.setInt(3, list.get(arg1).getProgramSemSubjectId());
					ps.setString(4, year);//examYear
					ps.setString(5, month);//examMonth
					ps.setString(6, list.get(arg1).getStatus());
					ps.setInt(7, list.get(arg1).getResultLive());
					ps.setInt(8, list.get(arg1).getProcessed());
					ps.setString(9, list.get(arg1).getActive());
					ps.setString(10, userId);
					ps.setString(11, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveMarksHistory_Absent : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveMarksHistory_Absent : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public boolean saveMarks(final List<RemarksGradeBean> list, final String examYear, final String examMonth,
			final String userId) {
		logger.info("Entering RemarksGradeDAO : saveMarks");
		boolean isSaved = Boolean.FALSE;
		boolean isSaved_SPF = Boolean.FALSE;
		//boolean isSaved_PF = Boolean.FALSE;
		boolean isSaved1 = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		
		try {
			//startTransaction("saveMarks");
			start_Transaction_U_PR("saveMarks");
			logger.info("RemarksGradeDAO : saveMarks : About to insert in MARKS table");
			isSaved = batchSaveMarks(Boolean.TRUE, list, examYear, examMonth, userId);
			
			//update in staging_pf
			logger.info("RemarksGradeDAO : saveMarks : About to NULLIFY in STAGING_PF table");
			isSaved_SPF = batchSaveNullify(Boolean.TRUE, list, examYear, examMonth, DB_SCHEMA_REMARKPF + "." + TABLE_STAGING_PF, userId,
					RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.STATUS_RESET_PASSFAIL,
					RemarksGradeBean.RESULT_NOT_LIVE);
			//update in pf
			/*logger.info("RemarksGradeDAO : saveMarks : About to NULLIFY in PASSFAIL table");
			isSaved_PF = batchSaveNullify(Boolean.TRUE, list, examYear, examMonth, DB_SCHEMA_REMARKPF + "." + TABLE_PF, userId,
					RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.STATUS_RESET_PASSFAIL,
					RemarksGradeBean.RESULT_NOT_LIVE);*/
			
			//if(isSaved && isSaved_SPF && isSaved_PF) {
			if(isSaved && isSaved_SPF) {
				logger.info("RemarksGradeDAO : saveMarks : About to insert in MARKS_HISTORY table");
				isSaved1 = batchSaveMarksHistory(Boolean.TRUE, list, examYear, examMonth, userId);
				//toCommit = (isSaved && isSaved1);
				//endTransaction(toCommit);
			}
		} finally {
			//toCommit = (isSaved && isSaved_SPF && isSaved_PF && isSaved1);
			toCommit = (isSaved && isSaved_SPF && isSaved1);
			if(toCommit) {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarks : COMMIT.");
			} else {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarks : ROLLBACK.");
			}
		}
		return toCommit;
	}
	
	/*@Deprecated
	public List<RemarksGradeBean> viewMarks(final RemarksGradeBean remarksGradeBean, final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : viewMarks");
		//jdbcTemplate = new JdbcTemplate(dataSource);
		final Boolean addOptional;
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" m.sapid, m.subject, m.assignmentYear, m.assignmentMonth, m.iaScore, m.totalScore, m.status");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND m.active = '").append(activeFlag).append("' AND m.processed = ").append(processedFlag);
		strBuf.append(" AND m.assignmentYear = ? AND m.assignmentMonth = ?");
		
		if(isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure()) && isBlank(remarksGradeBean.getProgram())) {
			addOptional = Boolean.FALSE;
		} else {
			addOptional = Boolean.TRUE;
			//strBuf.append(" JOIN exam.program_sem_subject pss ON pss.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
			strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = ?");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = ?");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = ?");
		}
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : viewMarks : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx++, year);
				arg0.setString(idx++, month);
				if(addOptional) {
					arg0.setString(idx++, remarksGradeBean.getStudentType());
					arg0.setString(idx++, remarksGradeBean.getProgramStructure());
					arg0.setString(idx++, remarksGradeBean.getProgram());
				}
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(year);
					bean.setMonth(month);
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					bean.setName(rs.getString("name"));
					bean.setStatus(rs.getString("status"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : viewMarks : List Size "+ list.size());
		}
		return list;
	}*/
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> viewMarks(final RemarksGradeBean remarksGradeBean, final String activeFlag,
			final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : viewMarks");
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("processedFlag", processedFlag);
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" m.sapid, m.subject, m.assignmentYear, m.assignmentMonth, m.iaScore, m.totalScore, m.status");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND m.active = :activeFlag AND m.processed = :processedFlag");
		strBuf.append(" AND m.assignmentYear = :year AND m.assignmentMonth = :month");
		
		if(isNotBlank(remarksGradeBean.getStudentType()) && isNotBlank(remarksGradeBean.getProgramStructure()) && isNotBlank(remarksGradeBean.getProgram())) {
			//strBuf.append(" JOIN exam.program_sem_subject pss ON pss.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
			strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = :consumerTypeId");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = :programStructureId");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = :programId");
			
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentType());
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructure());
			paramSource.addValue("programId", remarksGradeBean.getProgram());
		}
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : viewMarks : Query : "+ sql);
		
		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(year);
					bean.setMonth(month);
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					bean.setName(rs.getString("name"));
					bean.setStatus(rs.getString("status"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : viewMarks : List Size "+ list.size());
		}
		return list;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> fetchAbsentStudents(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeDAO : fetchAbsentStudents");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final String examYear = remarksGradeBean.getYear();
		final String examMonth = remarksGradeBean.getMonth();
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		
		if(isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getSem())) {
			isSemPresent = Boolean.FALSE;
		} else {
			isSemPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		strBuf = new StringBuffer();
		strBuf.append(" SELECT s.sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" scm.sem, sc.subjectname, ct.name 'consumerTypeName', pst.program_structure, pgm.code, r.year 'acadYear', r.month 'acadmonth',  scm.id 'programSemSubjectId'");
		strBuf.append(" FROM ");
		strBuf.append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_REGISTRATION).append(" r");
		strBuf.append(" ON s.sapid = r.sapid");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_MDM_SUBJECTCODE_MAPPING).append(" scm");
		strBuf.append(" ON s.consumerProgramStructureId = scm.consumerProgramStructureId AND r.sem = scm.sem AND scm.passScore = ").append(SOFTSKILLS_PASSSCORE);
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_MDM_SUBJECTCODE).append(" sc").append(" ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_CONSUMERPROGRAMSTRUCTURE).append(" cps").append(" ON cps.id = scm.consumerProgramStructureId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_CONSUMER_TYPE).append(" ct").append(" ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_PROGRAM_STRUCTURE).append(" pst").append(" ON pst.id = cps.programStructureId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_PROGRAM).append(" pgm").append(" ON pgm.id = cps.programId");
		strBuf.append(" AND pgm.code in ('").append(SOFTSKILLS_PROGRAM_BBA).append("','").append(SOFTSKILLS_PROGRAM_BCOM).append("','").append(SOFTSKILLS_PROGRAM_BBA_BA).append("')"); 
		strBuf.append(" WHERE");
		if(isSapIdPresent) {
			strBuf.append(" s.sapid = ? AND");
		}
		if(isSemPresent) {
			strBuf.append(" scm.sem = ? AND");
		}
		if(isConsProgStrucProgram) {
			strBuf.append(" cps.consumerTypeId = ? AND");
			strBuf.append(" cps.programStructureId = ? AND");
			strBuf.append(" cps.programId = ? AND");
		}
		strBuf.append(" r.year = ? AND r.month = ?");
		strBuf.append(" AND concat(s.sapid, sc.subjectname) NOT IN");
		strBuf.append(" (SELECT concat(sapid, subject) FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTSUBMISSION); 
		strBuf.append(" WHERE year = ? AND month = ?)");
		//added check to not to include Jan 2023 students
		strBuf.append(" AND (s.enrollmentYear, s.enrollmentMonth) NOT IN ((2023,'Jan')) ");
		strBuf.append(" AND scm.hasAssignment = '").append(CHAR_Y).append("'");
		strBuf.append(" AND concat(s.sapid, sc.subjectname) NOT IN");
		strBuf.append(" (SELECT concat(sapid, subject) FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_PF);
		strBuf.append(" WHERE isPass = '").append(CHAR_Y).append("')");
		strBuf.append(" ORDER BY pgm.id, scm.sem, s.sapid");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : fetchAbsentStudents : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				if(isSapIdPresent) {
					arg0.setString(idx++, remarksGradeBean.getSapid());
				}
				if(isSemPresent) {
					arg0.setString(idx++, remarksGradeBean.getSem());
				}
				if(isConsProgStrucProgram) {
					arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
					arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
					arg0.setString(idx++, remarksGradeBean.getProgramId());
				}
				arg0.setString(idx++, remarksGradeBean.getAcadYear());
				arg0.setString(idx++, remarksGradeBean.getAcadMonth());
				arg0.setString(idx++, examYear);
				arg0.setString(idx++, examMonth);
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(examYear);
					bean.setMonth(examMonth);
					bean.setAcadYear(rs.getString("acadYear"));
					bean.setAcadMonth(rs.getString("acadmonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setSubject(rs.getString("subjectname"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("code"));
					bean.setProgramSemSubjectId(rs.getInt("programSemSubjectId"));//Needed while Saving in marks table.
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : fetchAbsentStudents : List Size "+ list.size());
		}
		return list;
	}
	
	public boolean saveMarksForAbsent(final List<RemarksGradeBean> list, final String year, final String month,
			final String userId) {
		logger.info("Entering RemarksGradeDAO : saveMarksForAbsent");
		boolean isSaved = Boolean.FALSE;
		boolean isSaved_SPF = Boolean.FALSE;
		boolean isSaved1 = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;

		try {
			//startTransaction("saveMarksForAbsent");
			start_Transaction_U_PR("saveMarksForAbsent");
			logger.info("RemarksGradeDAO : saveMarksForAbsent : About to insert in MARKS table");
			isSaved = batchSaveMarks_Absent(Boolean.TRUE, list, year, month, userId);

			// update in staging_pf
			logger.info("RemarksGradeDAO : saveMarksForAbsent : About to NULLIFY in STAGING_PF table");
			isSaved_SPF = batchSaveNullify(Boolean.TRUE, list, year, month, DB_SCHEMA_REMARKPF + "." + TABLE_STAGING_PF,
					userId, RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.STATUS_RESET_PASSFAIL,
					RemarksGradeBean.RESULT_NOT_LIVE);

			if (isSaved && isSaved_SPF) {
				logger.info("RemarksGradeDAO : saveMarksForAbsent : About to insert in MARKS_HISTORY table");
				isSaved1 = batchSaveMarksHistory_Absent(Boolean.TRUE, list, year, month, userId);
			}
		} finally {
			toCommit = (isSaved && isSaved_SPF && isSaved1);
			if (toCommit) {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarksForAbsent : COMMIT.");
			} else {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarksForAbsent : ROLLBACK.");
			}
		}
		return toCommit;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> fetchCopyCaseStudents(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeDAO : fetchCopyCaseStudents");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;
		
		strBuf = new StringBuffer();
		/*strBuf.append("SELECT ags.year, ags.month, ags.sapid, ags.subject, ags.finalScore, s2.name, scm.id 'programSemSubjectId', scm.sem, pgm.code");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTSUBMISSION).append(" ags");
		strBuf.append(" JOIN ");
		strBuf.append(" ( SELECT sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" sem, consumerProgramStructureId ");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" WHERE sem = (SELECT MAX(sem) FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s1");
		strBuf.append(" WHERE s1.sapid = s.sapid)  ) s2");
		strBuf.append(" ON ags.sapid = s2.sapid AND ags.finalReason like '%Copy Case%' AND ags.year = :year AND ags.month = :month");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTS).append(" assign");
		strBuf.append(" ON assign.year = ags.year AND assign.month = ags.month AND assign.subject = ags.subject");
		strBuf.append(" AND assign.consumerProgramStructureId = s2.consumerProgramStructureId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode sc ON sc.subjectname = ags.subject");
		//strBuf.append(" AND sc.id IN (").append(SOFTSKILLS_MDM_SUBJECTCODE_ID).append(")");
		strBuf.append(" AND sc.id IN (:SOFTSKILLS_MDM_SUBJECTCODE_ID)");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode_mapping scm ON scm.subjectCodeId = sc.id");
		strBuf.append(" AND scm.consumerProgramStructureId = assign.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = assign.consumerProgramStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" ORDER BY s2.sapid");*/// limit 0, 20
		
		strBuf.append("SELECT ags.year, ags.month, ags.sapid, ags.subject, ags.finalScore, s2.name, scm.id 'programSemSubjectId', scm.sem, pgm.code");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTSUBMISSION).append(" ags");
		strBuf.append(" JOIN ");
		strBuf.append(" ( SELECT sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" sem, consumerProgramStructureId ");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" WHERE sem = (SELECT MAX(sem) FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s1");
		strBuf.append(" WHERE s1.sapid = s.sapid)  ) s2");
		strBuf.append(" ON ags.sapid = s2.sapid AND ags.finalReason like '%Copy Case%' AND ags.year = :year AND ags.month = :month");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode sc ON sc.subjectname = ags.subject");
		//strBuf.append(" AND sc.id IN (").append(SOFTSKILLS_MDM_SUBJECTCODE_ID).append(")");
		strBuf.append(" AND sc.id IN (:SOFTSKILLS_MDM_SUBJECTCODE_ID)");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode_mapping scm ON scm.subjectCodeId = sc.id");
		strBuf.append(" AND scm.consumerProgramStructureId = s2.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" ORDER BY s2.sapid");// limit 0, 20
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : fetchCopyCaseStudents : Query : "+ sql);
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("year", remarksGradeBean.getYear());
		paramSource.addValue("month", remarksGradeBean.getMonth());
		paramSource.addValue("SOFTSKILLS_MDM_SUBJECTCODE_ID", RemarksGradeDAO.SOFTSKILLS_MDM_SUBJECTCODE_ID);
		
		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("year"));
					bean.setMonth(rs.getString("month"));
					bean.setSapid(rs.getString("sapid"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("finalScore"));
					bean.setProgramSemSubjectId(rs.getInt("programSemSubjectId"));
					bean.setProgram(rs.getString("code"));//Needed in save in staging_pf query;
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : fetchCopyCaseStudents : List Size "+ list.size());
		}
		return list;
	}
	
	/*@Deprecated
	public List<RemarksGradeBean> fetchCopyCaseStudents(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeDAO : fetchCopyCaseStudents");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT ags.year, ags.month, ags.sapid, ags.subject, ags.finalScore, s2.name, scm.id 'programSemSubjectId', scm.sem, pgm.code");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTSUBMISSION).append(" ags");
		strBuf.append(" JOIN ");
		strBuf.append(" ( SELECT sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" sem, consumerProgramStructureId ");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" WHERE sem = (SELECT MAX(sem) FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s1");
		strBuf.append(" WHERE s1.sapid = s.sapid)  ) s2");
		strBuf.append(" ON ags.sapid = s2.sapid AND ags.finalReason like '%Copy Case%' AND ags.year = ? AND ags.month = ?");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_ASSIGNMENTS).append(" assign");
		strBuf.append(" ON assign.year = ags.year AND assign.month = ags.month AND assign.subject = ags.subject");
		strBuf.append(" AND assign.consumerProgramStructureId = s2.consumerProgramStructureId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode sc ON sc.subjectname = ags.subject");
		strBuf.append(" AND sc.id IN (").append(SOFTSKILLS_MDM_SUBJECTCODE_ID).append(")");
		strBuf.append(" JOIN ").append(DB_SCHEMA_EXAM).append(".").append("mdm_subjectcode_mapping scm ON scm.subjectCodeId = sc.id");
		strBuf.append(" AND scm.consumerProgramStructureId = assign.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = assign.consumerProgramStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" ORDER BY s2.sapid");// limit 0, 20
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : fetchCopyCaseStudents : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx++, remarksGradeBean.getYear());
				arg0.setString(idx++, remarksGradeBean.getMonth());
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("year"));
					bean.setMonth(rs.getString("month"));
					bean.setSapid(rs.getString("sapid"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("finalScore"));
					bean.setProgramSemSubjectId(rs.getInt("programSemSubjectId"));
					bean.setProgram(rs.getString("code"));//Needed in save in staging_pf query;
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : fetchCopyCaseStudents : List Size "+ list.size());
		}
		return list;
	}*/
	
	public boolean markPassFailForAbsent(final List<RemarksGradeBean> list, final String status,
			final Integer processedFlag, final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : markPassFailForAbsent : Status :"+status);
		boolean isUpdated = Boolean.FALSE; 
		boolean isSaved = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		
		//NOTE: copy of method - markPassFail
		try {
			//startTransaction("markPassFailForAbsent");
			start_Transaction_U_PR("markPassFailForAbsent");
			logger.info("RemarksGradeDAO : markPassFailForAbsent : About to update MARKS table");
			isUpdated = batchUpdateMarks(Boolean.TRUE, list, status, processedFlag, resultLiveFlag, userId);
			
			logger.info("RemarksGradeDAO : markPassFailForAbsent : About to insert STAGING_PASSFAIL table");
			isSaved = batchSaveInStagingPassFail(Boolean.TRUE, list, status, resultLiveFlag, userId);
			//toCommit = (isSaved && isUpdated);
			//end_Transaction(toCommit);
		} finally {
			toCommit = (isSaved && isUpdated);
			end_Transaction(toCommit);
			if(toCommit) {
				logger.info("RemarksGradeDAO : markPassFailForAbsent : COMMIT.");
			} else {
				logger.error("RemarksGradeDAO : markPassFailForAbsent : ROLLBACK...No rows updated in MARKS and inserted in STAGING_PASSFAIL table(s).");
			}
		}
		return toCommit;
	}
	
	public boolean saveMarksForCopyCase(final List<RemarksGradeBean> list, final String examYear,
			final String examMonth, final String userId) {
		logger.info("Entering RemarksGradeDAO : saveMarksForCopyCase");
		boolean isSaved = Boolean.FALSE;
		boolean isSaved_SPF = Boolean.FALSE;
		//boolean isSaved_PF = Boolean.FALSE;
		boolean isSaved1 = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		
		try {
			//startTransaction("saveMarksForCopyCase");
			start_Transaction_U_PR("saveMarksForCopyCase");
			logger.info("RemarksGradeDAO : saveMarksForCopyCase : About to insert in MARKS table");
			isSaved = batchSaveMarks(Boolean.TRUE, list, examYear, examMonth, userId);
			
			//update in staging_pf
			logger.info("RemarksGradeDAO : saveMarksForCopyCase : About to NULLIFY in STAGING_PF table");
			isSaved_SPF = batchSaveNullify(Boolean.TRUE, list, examYear, examMonth, DB_SCHEMA_REMARKPF + "." + TABLE_STAGING_PF, userId,
					RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.STATUS_RESET_PASSFAIL,
					RemarksGradeBean.RESULT_NOT_LIVE);
			//update in pf
			/*logger.info("RemarksGradeDAO : saveMarksForCopyCase : About to NULLIFY in PASSFAIL table");
			isSaved_PF = batchSaveNullify(Boolean.TRUE, list, examYear, examMonth, DB_SCHEMA_REMARKPF + "." + TABLE_PF, userId,
					RemarksGradeBean.ROWS_NOT_DELETED, RemarksGradeBean.STATUS_RESET_PASSFAIL,
					RemarksGradeBean.RESULT_NOT_LIVE);*/
			
			//if(isSaved && isSaved_SPF && isSaved_PF) {
			if(isSaved && isSaved_SPF) {
				logger.info("RemarksGradeDAO : saveMarksForCopyCase : About to insert in MARKS_HISTORY table");
				isSaved1 = batchSaveMarksHistory(Boolean.TRUE, list, examYear, examMonth, userId);
				//toCommit = (isSaved && isSaved1);
				//end_Transaction(toCommit);
			}
		} finally {
			//toCommit = (isSaved && isSaved_SPF && isSaved_PF && isSaved1);
			toCommit = (isSaved && isSaved_SPF && isSaved1);
			if(toCommit) {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarksForCopyCase : COMMIT.");
			} else {
				end_Transaction(toCommit);
				logger.info("RemarksGradeDAO : saveMarksForCopyCase : ROLLBACK.");
			}
		}
		return toCommit;
	}
	
	public boolean markPassFailForCopyCase(final List<RemarksGradeBean> list, final String status,
			final Integer processedFlag, final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : markPassFailForCopyCase : Status :"+status);
		boolean isUpdated = Boolean.FALSE; 
		boolean isSaved = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		
		//NOTE: copy of method - markPassFail
		try {
			//startTransaction("markPassFailForCopyCase");
			start_Transaction_U_PR("markPassFailForCopyCase");
			logger.info("RemarksGradeDAO : markPassFailForCopyCase : About to update MARKS table");
			isUpdated = batchUpdateMarks(Boolean.TRUE, list, status, processedFlag, resultLiveFlag, userId);
			
			logger.info("RemarksGradeDAO : markPassFailForCopyCase : About to insert STAGING_PASSFAIL table");
			isSaved = batchSaveInStagingPassFail(Boolean.TRUE, list, status, resultLiveFlag, userId);
			//toCommit = (isSaved && isUpdated);
			//end_Transaction(toCommit);
		} finally {
			toCommit = (isSaved && isUpdated);
			end_Transaction(toCommit);
			if(toCommit) {
				logger.info("RemarksGradeDAO : markPassFailForCopyCase : COMMIT.");
			} else {
				logger.error("RemarksGradeDAO : markPassFailForCopyCase : ROLLBACK...No rows updated in MARKS and inserted in STAGING_PASSFAIL table(s).");
			}
		}
		return toCommit;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> searchForStudentMarksStatus(final RemarksGradeBean remarksGradeBean,
			final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : searchForStudentMarksStatus");
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("processedFlag", processedFlag);
		
		if(isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getSem())) {
			isSemPresent = Boolean.FALSE;
		} else {
			isSemPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" m.sapid, m.subject, m.assignmentYear, m.assignmentMonth, m.iaScore, m.totalScore, m.writtenScore, m.status,");
		strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code, ct.id 'consumerTypeId', ps.id 'programStructureId', pgm.id 'programId'");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND m.active = :activeFlag AND m.processed = :processedFlag");
		if(isSapIdPresent) {
			strBuf.append(" AND s.sapid = :sapid");
			paramSource.addValue("sapid", remarksGradeBean.getSapid());
		}
		if(isYear) {
			strBuf.append(" AND m.assignmentYear = :year");
			paramSource.addValue("year", remarksGradeBean.getYear());
		}
		if(isMonth) {
			strBuf.append("  AND m.assignmentMonth = :month");
			paramSource.addValue("month", remarksGradeBean.getMonth());
		}
		
		/*strBuf.append(" JOIN exam.program_sem_subject pss ON pss.id = m.programSemSubjectId");
		if(isSemPresent) {
			strBuf.append(" AND pss.sem = ?");
		}*/
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
		if(isSemPresent) {
			strBuf.append(" AND scm.sem = :sem");
			paramSource.addValue("sem", remarksGradeBean.getSem());
		}
		
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = :consumerTypeId");
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = :programStructureId");
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = :programId");
			paramSource.addValue("programId", remarksGradeBean.getProgramId());
		}
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchForStudentMarksStatus : Query : "+ sql);
		
		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("code"));
					bean.setStatus(rs.getString("status"));
					bean.setStudentTypeId(rs.getString("consumerTypeId"));
					bean.setProgramStructureId(rs.getString("programStructureId"));
					bean.setProgramId(rs.getString("programId"));
					bean.setScoreWritten(rs.getInt("writtenScore"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchForStudentMarksStatus : List Size "+ list.size());
		}
		return list;
	}
	
	/*@Deprecated
	public List<RemarksGradeBean> searchForStudentMarksStatus(final RemarksGradeBean remarksGradeBean,
			final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : searchForStudentMarksStatus");
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		if(isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getSem())) {
			isSemPresent = Boolean.FALSE;
		} else {
			isSemPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" m.sapid, m.subject, m.assignmentYear, m.assignmentMonth, m.iaScore, m.totalScore, m.writtenScore, m.status,");
		strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code, ct.id 'consumerTypeId', ps.id 'programStructureId', pgm.id 'programId'");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND m.active = '").append(activeFlag).append("' AND m.processed = ").append(processedFlag);
		if(isSapIdPresent) {
			strBuf.append(" AND s.sapid = ?");
		}
		if(isYear) {
			strBuf.append(" AND m.assignmentYear = ?");
		}
		if(isMonth) {
			strBuf.append("  AND m.assignmentMonth = ?");
		}
		
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
		if(isSemPresent) {
			strBuf.append(" AND scm.sem = ?");
		}
		
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = ?");
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = ?");
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = ?");
		}
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchForStudentMarksStatus : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				if(isSapIdPresent) {
					arg0.setString(idx++, remarksGradeBean.getSapid());
				}
				if(isYear) {
					arg0.setString(idx++, remarksGradeBean.getYear());
				}
				if(isMonth) {
					arg0.setString(idx++, remarksGradeBean.getMonth());
				}
				if(isSemPresent) {
					arg0.setString(idx++, remarksGradeBean.getSem());
				}
				if(isConsProgStrucProgram) {
					arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
					arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
					arg0.setString(idx++, remarksGradeBean.getProgramId());
				}
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("code"));
					bean.setStatus(rs.getString("status"));
					bean.setStudentTypeId(rs.getString("consumerTypeId"));
					bean.setProgramStructureId(rs.getString("programStructureId"));
					bean.setProgramId(rs.getString("programId"));
					bean.setScoreWritten(rs.getInt("writtenScore"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchForStudentMarksStatus : List Size "+ list.size());
		}
		return list;
	}*/
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public synchronized boolean updateForStudentMarksStatus(final RemarksGradeBean remarksGradeBean,
			final String userId, final String activeSearchFlag, final Integer processedSearchFlag,
			final String activeUpdateFlag, final Integer processedUpdateFlag) {
		logger.info("Entering RemarksGradeDAO : updateForStudentMarksStatus");
		boolean isUpdated = Boolean.FALSE;
		StringBuffer strBuf = null;
		final String sql;
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		int rowsUpdated = 0;
		final MapSqlParameterSource paramSource;
		
		try {
			paramSource = new MapSqlParameterSource();
			paramSource.addValue("activeSearchFlag", activeSearchFlag);
			paramSource.addValue("activeUpdateFlag", activeUpdateFlag);
			paramSource.addValue("processedSearchFlag", processedSearchFlag);
			paramSource.addValue("processedUpdateFlag", processedUpdateFlag);
			paramSource.addValue("status", remarksGradeBean.getStatus());
			paramSource.addValue("userId", userId);
			
			if(isBlank(remarksGradeBean.getSapid())) {
				isSapIdPresent = Boolean.FALSE;
			} else {
				isSapIdPresent = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getYear())) {
				isYear = Boolean.FALSE;
			} else {
				isYear = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getMonth())) {
				isMonth = Boolean.FALSE;
			} else {
				isMonth = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getSem())) {
				isSemPresent = Boolean.FALSE;
			} else {
				isSemPresent = Boolean.TRUE;
			}
			if ((isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure())
					&& isBlank(remarksGradeBean.getProgram()))
					&& (isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId())
							&& isBlank(remarksGradeBean.getProgramId()))) {
				isConsProgStrucProgram = Boolean.FALSE;
			} else {
				isConsProgStrucProgram = Boolean.TRUE;
			}
			
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
			strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
			strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
			strBuf.append(" AND m.active = :activeSearchFlag AND m.processed = :processedSearchFlag");
			if(isSapIdPresent) {
				strBuf.append(" AND s.sapid = :sapid");
				paramSource.addValue("sapid", remarksGradeBean.getSapid());
			}
			if(isYear) {
				strBuf.append(" AND m.assignmentYear = :year");
				paramSource.addValue("year", remarksGradeBean.getYear());
			}
			if(isMonth) {
				strBuf.append("  AND m.assignmentMonth = :month");
				paramSource.addValue("month", remarksGradeBean.getMonth());
			}
			if(isSemPresent) {
				strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
				strBuf.append(" AND scm.sem = :sem");
				paramSource.addValue("sem", remarksGradeBean.getSem());
			}
			if(isConsProgStrucProgram) {
				strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
				if(isSemPresent) {
					strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
				}
				strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
				strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
				strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
				
				if(isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure()) && isBlank(remarksGradeBean.getProgram())) {
					strBuf.append(" AND ct.id = :consumerTypeId AND ps.id = :programStructureId AND pgm.id = :programId");
					paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
					paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
					paramSource.addValue("programId", remarksGradeBean.getProgramId());
				} else {
					strBuf.append(" AND ct.name = :consumerType AND ps.program_structure = :programStructure AND pgm.code = :programCode");
					paramSource.addValue("consumerType", remarksGradeBean.getStudentType());
					paramSource.addValue("programStructure", remarksGradeBean.getProgramStructure());
					paramSource.addValue("programCode", remarksGradeBean.getProgram());
				}
			}
			strBuf.append(" SET");
			//strBuf.append(" iaScore = ").append(remarksGradeBean.getScoreIA()).append(",");
			strBuf.append(" m.status = :status,");
			strBuf.append(" m.processed = :processedUpdateFlag ,");
			strBuf.append(" m.active= :activeUpdateFlag ,");
			strBuf.append(" m.lastModifiedBy = :userId, m.lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			strBuf.append(" WHERE 1 = 1");
			
			sql = strBuf.toString();
			emptyStringBuffer(strBuf);
			logger.info("RemarksGradeDAO : updateForStudentMarksStatus : Query : "+ sql);
			
			rowsUpdated = this.namedParameterJdbcTemplate.update(sql, paramSource);
			isUpdated = Boolean.TRUE;
		} catch (Exception e) {
			//
			logger.error("RemarksGradeDAO : updateForStudentMarksStatus : " + e.getMessage());
			throw e;
		} finally {
			logger.info("RemarksGradeDAO : updateForStudentMarksStatus : Total "+ rowsUpdated +" Rows updated? "+ isUpdated);
		}
		return isUpdated;
	}
	
	/*@Deprecated
	public boolean updateForStudentMarksStatus(final RemarksGradeBean remarksGradeBean, final String userId, final String activeSearchFlag, final Integer processedSearchFlag, final String activeUpdateFlag, final Integer processedUpdateFlag) {
		logger.info("Entering RemarksGradeDAO : updateForStudentMarksStatus");
		boolean isUpdated = Boolean.FALSE;
		StringBuffer strBuf = null;
		final String sql;
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		int rowsUpdated = 0;
		try {
			
			if(isBlank(remarksGradeBean.getSapid())) {
				isSapIdPresent = Boolean.FALSE;
			} else {
				isSapIdPresent = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getYear())) {
				isYear = Boolean.FALSE;
			} else {
				isYear = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getMonth())) {
				isMonth = Boolean.FALSE;
			} else {
				isMonth = Boolean.TRUE;
			}
			if(isBlank(remarksGradeBean.getSem())) {
				isSemPresent = Boolean.FALSE;
			} else {
				isSemPresent = Boolean.TRUE;
			}
			if ((isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure())
					&& isBlank(remarksGradeBean.getProgram()))
					&& (isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId())
							&& isBlank(remarksGradeBean.getProgramId()))) {
				isConsProgStrucProgram = Boolean.FALSE;
			} else {
				isConsProgStrucProgram = Boolean.TRUE;
			}
			
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
			strBuf.append(" JOIN exam.students s ON s.sapid = m.sapid");
			strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
			strBuf.append(" AND m.active = '").append(activeSearchFlag).append("' AND m.processed = ").append(processedSearchFlag);
			if(isSapIdPresent) {
				strBuf.append(" AND s.sapid = ?");
			}
			if(isYear) {
				strBuf.append(" AND m.assignmentYear = ?");
			}
			if(isMonth) {
				strBuf.append(" AND m.assignmentMonth = ?");
			}
			if(isSemPresent) {
				//strBuf.append(" JOIN exam.program_sem_subject pss ON pss.id = m.programSemSubjectId");
				//strBuf.append(" AND pss.sem = ?");
				strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
				strBuf.append(" AND scm.sem = ?");
			}
			if(isConsProgStrucProgram) {
				strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
				if(isSemPresent) {
					//strBuf.append(" AND pss.consumerProgramStructureId = s.consumerProgramStructureId");
					strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
				}
				strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
				strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
				strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
				
				if(isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure()) && isBlank(remarksGradeBean.getProgram())) {
					strBuf.append(" AND ct.id = ? AND ps.id = ? AND pgm.id = ?");
				} else {
					strBuf.append(" AND ct.name = ? AND ps.program_structure = ? AND pgm.code = ?");
				}
			
			}
			strBuf.append(" SET");
			//strBuf.append(" iaScore = ").append(remarksGradeBean.getScoreIA()).append(",");
			strBuf.append(" m.status = ?,");
			strBuf.append(" m.processed = ").append(processedUpdateFlag).append(",");
			strBuf.append(" m.active= '").append(activeUpdateFlag).append("',");
			strBuf.append(" m.lastModifiedBy = ?, m.lastModifiedDate = current_timestamp()");
			strBuf.append(" WHERE 1 = 1");
			sql = strBuf.toString();
			emptyStringBuffer(strBuf);
			logger.info("RemarksGradeDAO : updateForStudentMarksStatus : Query : "+ sql);
			
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int idx = 1;
					PreparedStatement ps = con.prepareStatement(sql);
					
					if(isSapIdPresent) {
						ps.setString(idx++, remarksGradeBean.getSapid());
					}
					if(isYear) {
						ps.setString(idx++, remarksGradeBean.getYear());
					}
					if(isMonth) {
						ps.setString(idx++, remarksGradeBean.getMonth());
					}
					if(isSemPresent) {
						ps.setString(idx++, remarksGradeBean.getSem());
					}
					if(isConsProgStrucProgram) {
						if(isBlank(remarksGradeBean.getStudentType()) && isBlank(remarksGradeBean.getProgramStructure()) && isBlank(remarksGradeBean.getProgram())) {
							ps.setString(idx++, remarksGradeBean.getStudentTypeId());
							ps.setString(idx++, remarksGradeBean.getProgramStructureId());
							ps.setString(idx++, remarksGradeBean.getProgramId());
						} else {
							ps.setString(idx++, remarksGradeBean.getStudentType());
							ps.setString(idx++, remarksGradeBean.getProgramStructure());
							ps.setString(idx++, remarksGradeBean.getProgram());
						}
					}
					ps.setString(idx++, remarksGradeBean.getStatus());
					ps.setString(idx++, userId);
					return ps;
				}
			};
			rowsUpdated = jdbcTemplate.update(psc);
			isUpdated = Boolean.TRUE;
		} catch (Exception e) {
			//
			logger.error("RemarksGradeDAO : updateForStudentMarksStatus : " + e.getMessage());
			throw e;
		} finally {
			logger.info("RemarksGradeDAO : updateForStudentMarksStatus : Total "+ rowsUpdated +" Rows updated? "+ isUpdated);
		}
		return isUpdated;
	}*/
	
	//step passfail
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public Integer findStudents(final RemarksGradeBean remarksGradeBean, final String status, final String activeFlag,
			final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : findStudents : Status : "+status);
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		Integer totalRows = null;
		final MapSqlParameterSource paramSource;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		paramSource.addValue("status", status);
		paramSource.addValue("processedFlag", processedFlag);
		paramSource.addValue("activeFlag", activeFlag);
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT count(m.id) countOfId");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		
		if(isNotBlank(remarksGradeBean.getStudentTypeId()) && isNotBlank(remarksGradeBean.getProgramStructureId()) && isNotBlank(remarksGradeBean.getProgramId())) {
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = :consumerTypeId");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = :programStructureId");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = :programId");
			
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
			paramSource.addValue("programId", remarksGradeBean.getProgramId());
		}
		strBuf.append(" WHERE m.assignmentYear = :year AND m.assignmentMonth = :month AND m.status = :status");
		strBuf.append(" AND m.processed = :processedFlag AND m.active = :activeFlag");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : findStudents : Query :"+sql);
		
		totalRows = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				Integer rowCount = null;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					rowCount = rs.getInt("countOfId");
				}
				return rowCount;
			}
		});
		logger.info("RemarksGradeDAO : findStudents : (Status, Total Rows) ("+ status + "," + totalRows+")");
		return totalRows;
	}
	
	/*@Deprecated
	public Integer findStudents(final RemarksGradeBean remarksGradeBean, final String status, final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : findStudents : Status : "+status);
		//jdbcTemplate = new JdbcTemplate(dataSource);
		final Boolean addOptional;
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		Integer totalRows = null;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT count(m.id) countOfId");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			addOptional = Boolean.FALSE;
		} else {
			addOptional = Boolean.TRUE;
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = ?");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = ?");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = ?");
		}
		//strBuf.append(" WHERE m.active = ?").append(" AND m.processed = ?").append(" AND m.status = ?");
		strBuf.append(" WHERE m.assignmentYear = ? AND m.assignmentMonth = ? AND m.status = ? ");
		strBuf.append(" AND m.processed = ? AND m.active = ?");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : findStudents : Query :"+sql);
		
		totalRows = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				if(addOptional) {
					arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
					arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
					arg0.setString(idx++, remarksGradeBean.getProgramId());
				}
				arg0.setString(idx++, year);
				arg0.setString(idx++, month);
				arg0.setString(idx++, status);
				arg0.setInt(idx++, processedFlag);
				arg0.setString(idx++, activeFlag);
				
				Integer rowCount = null;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					rowCount = rs.getInt("countOfId");
				}
				return rowCount;
			}
		});
		logger.info("RemarksGradeDAO : findStudents : (Status, Total Rows) ("+ status + "," + totalRows+")");
		return totalRows;
	}*/
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> fetchStudents(final RemarksGradeBean remarksGradeBean, final String status,
			final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : fetchStudents : Status : "+status);
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list1 = null;
		final MapSqlParameterSource paramSource;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		paramSource.addValue("status", status);
		paramSource.addValue("processedFlag", processedFlag);
		paramSource.addValue("activeFlag", activeFlag);
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT m.id, m.sapid, m.subject, m.programSemSubjectId, m.assignmentYear, m.assignmentMonth,");
		strBuf.append(" m.iaScore, m.totalScore, m.status, m.processed, m.active");
		//strBuf.append(" , m.graceMarks");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		
		if(isNotBlank(remarksGradeBean.getStudentTypeId()) && isNotBlank(remarksGradeBean.getProgramStructureId()) && isNotBlank(remarksGradeBean.getProgramId())) {
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = :consumerTypeId");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = :programStructureId");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = :programId");
			
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
			paramSource.addValue("programId", remarksGradeBean.getProgramId());
		}
		strBuf.append(" WHERE m.assignmentYear = :year AND m.assignmentMonth = :month  AND m.status = :status");
		strBuf.append(" AND m.processed = :processedFlag AND m.active = :activeFlag");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : fetchStudents : Query : "+sql);
		logger.info("RemarksGradeDAO : fetchStudents : Params : " + remarksGradeBean.getStudentTypeId() + ","
				+ remarksGradeBean.getProgramStructureId() + "," + remarksGradeBean.getProgramId() + "," + activeFlag
				+ "," + processedFlag + "," + status + "," + year + "," + month);
		
		list1 = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setStatus(rs.getString("status"));
					bean.setActive(rs.getString("active"));
					//bean.setName(rs.getString("name"));
					//bean.setSem(rs.getString("sem"));
					//bean.setStudentType(rs.getString("consumerTypeName"));
					//bean.setProgramStructure(rs.getString("program_structure"));
					//bean.setProgram(rs.getString("code"));
					//bean.setStudentTypeId(rs.getString("consumerTypeId"));
					//bean.setProgramStructureId(rs.getString("programStructureId"));
					//bean.setProgramId(rs.getString("programId"));
					
					//bean.setScoreWritten(rs.getInt("writtenScore"));
					bean.setId(rs.getInt("id"));
					bean.setProgramSemSubjectId(toInteger(rs.getString("programSemSubjectId")));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					//if (!isBlank(rs.getString("graceMarks"))) {
						//bean.setGraceMarks(rs.getInt("graceMarks"));
					//}
					bean.setProcessed(rs.getInt("processed"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		logger.info("RemarksGradeDAO : fetchStudents : (Status, Total Rows) ("+ status + "," + list1.size() +")");
		return list1;
	}
	
	/*@Deprecated
	public List<RemarksGradeBean> fetchStudents(final RemarksGradeBean remarksGradeBean, final String status, final String activeFlag, final Integer processedFlag) {
		logger.info("Entering RemarksGradeDAO : fetchStudents : Status : "+status);
		//jdbcTemplate = new JdbcTemplate(dataSource);
		final Boolean addOptional;
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list1 = null;
		
		year = remarksGradeBean.getYear();
		month = remarksGradeBean.getMonth();
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT m.id, m.sapid, m.subject, m.programSemSubjectId, m.assignmentYear, m.assignmentMonth,");
		strBuf.append(" m.iaScore, m.totalScore, m.status, m.processed, m.active");
		//strBuf.append(" , m.graceMarks");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			addOptional = Boolean.FALSE;
		} else {
			addOptional = Boolean.TRUE;
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = m.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId AND cps.consumerTypeId = ?");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId AND cps.programStructureId = ?");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND cps.programId = ?");
		}
		//strBuf.append(" WHERE m.active = ?").append(" AND m.processed = ?").append(" AND m.status = ?");
		strBuf.append(" WHERE m.assignmentYear = ? AND m.assignmentMonth = ?  AND m.status = ?");
		strBuf.append(" AND m.processed = ? AND m.active = ?");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : fetchStudents : Query : "+sql);
		logger.info("RemarksGradeDAO : fetchStudents : Params : " + remarksGradeBean.getStudentTypeId() + ","
				+ remarksGradeBean.getProgramStructureId() + "," + remarksGradeBean.getProgramId() + "," + activeFlag
				+ "," + processedFlag + "," + status + "," + year + "," + month);
		
		list1 = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				if(addOptional) {
					arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
					arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
					arg0.setString(idx++, remarksGradeBean.getProgramId());
				}
				arg0.setString(idx++, year);
				arg0.setString(idx++, month);
				arg0.setString(idx++, status);
				arg0.setInt(idx++, processedFlag);
				arg0.setString(idx++, activeFlag);
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setStatus(rs.getString("status"));
					bean.setActive(rs.getString("active"));
					//bean.setName(rs.getString("name"));
					//bean.setSem(rs.getString("sem"));
					//bean.setStudentType(rs.getString("consumerTypeName"));
					//bean.setProgramStructure(rs.getString("program_structure"));
					//bean.setProgram(rs.getString("code"));
					//bean.setStudentTypeId(rs.getString("consumerTypeId"));
					//bean.setProgramStructureId(rs.getString("programStructureId"));
					//bean.setProgramId(rs.getString("programId"));
					
					//bean.setScoreWritten(rs.getInt("writtenScore"));
					bean.setId(rs.getInt("id"));
					bean.setProgramSemSubjectId(toInteger(rs.getString("programSemSubjectId")));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					//if (!isBlank(rs.getString("graceMarks"))) {
						//bean.setGraceMarks(rs.getInt("graceMarks"));
					//}
					bean.setProcessed(rs.getInt("processed"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		logger.info("RemarksGradeDAO : fetchStudents : (Status, Total Rows) ("+ status + "," + list1.size() +")");
		return list1;
	}*/
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public Map<Integer, Integer> fetchPassScores(final Set<Integer> setObj, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : fetchPassScores");
		Map<Integer, Integer> passScoreMap = null;
		StringBuffer strBuf = null;
		final String sql;
		final MapSqlParameterSource paramSource;

		paramSource = new MapSqlParameterSource();
		paramSource.addValue("setObj", setObj);
		paramSource.addValue("activeFlag", activeFlag);

		strBuf = new StringBuffer();
		strBuf.append(
				"SELECT id,passScore FROM exam.mdm_subjectcode_mapping WHERE active = :activeFlag AND id IN (:setObj) ORDER BY id ASC");
		sql = strBuf.toString();
		logger.info("RemarksGradeDAO : fetchPassScores : Query : " + sql);

		passScoreMap = this.namedParameterJdbcTemplate.query(sql, paramSource,
				new ResultSetExtractor<Map<Integer, Integer>>() {

					@Override
					public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
						// TODO Auto-generated method stub
						Map<Integer, Integer> scoreMap = null;
						scoreMap = new HashMap<Integer, Integer>();

						while (rs.next()) {
							scoreMap.put(rs.getInt("id"), rs.getInt("passScore"));
							logger.info("RemarksGradeDAO : fetchPassScores : (id, passScore)  (" + rs.getInt("id") + ","
									+ rs.getInt("passScore") + ")");
						}
						return scoreMap;
					}
				});
		emptyStringBuffer(strBuf);
		return passScoreMap;
	}
	
	/*@Deprecated
	public Map<Integer, Integer> fetchPassScores(final Set<Integer> setObj, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : fetchPassScores");
		Map<Integer, Integer> passScoreMap = null;
		StringBuffer strBuf = null;

		strBuf = new StringBuffer();
		String tempData = setObj.toString();
		tempData = tempData.substring(1, (tempData.length() - 1));
		strBuf.append("SELECT id,passScore FROM exam.mdm_subjectcode_mapping WHERE active = ? AND id in (")
				.append(tempData).append(") ORDER BY id ASC");
		final String sql = strBuf.toString();
		logger.info("RemarksGradeDAO : fetchPassScores : Query : " + sql);
		passScoreMap = jdbcTemplate.execute(sql, new PreparedStatementCallback<Map<Integer, Integer>>() {

			@Override
			public Map<Integer, Integer> doInPreparedStatement(PreparedStatement arg0)
					throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				arg0.setString(1, activeFlag);

				Map<Integer, Integer> scoreMap = null;
				scoreMap = new HashMap<Integer, Integer>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					scoreMap.put(rs.getInt("id"), rs.getInt("passScore"));
					logger.info("RemarksGradeDAO : fetchPassScores : (id, passScore)  (" + rs.getInt("id") + ","
							+ rs.getInt("passScore") + ")");
				}
				return scoreMap;
			}
		});
		emptyStringBuffer(strBuf);
		return passScoreMap;
	}*/
	
	public synchronized boolean batchUpdateMarks(final boolean slaveTransaction, final List<RemarksGradeBean> list,
			final String status, final Integer processedFlag, final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchUpdateMarks : Status : " + status);
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS);
			strBuf.append(" SET ");
			if (null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(" totalScore = ?, ");
			}
			strBuf.append(" isResultLive = ?, processed = ?, lastModifiedBy = ?, lastModifiedDate = ")
					.append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			strBuf.append(" WHERE id = ?");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchUpdateMarks : Query : " + query);

			if (!slaveTransaction) {
				//startTransaction("batchUpdateMarks");
				start_Transaction_U_PR("batchUpdateMarks");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					int idx = 1;
					if (null != status
							&& (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						ps.setInt(idx++, list.get(arg1).getScoreTotal());
					}
					ps.setInt(idx++, resultLiveFlag);
					ps.setInt(idx++, processedFlag);
					ps.setString(idx++, userId);
					ps.setInt(idx++, list.get(arg1).getId());
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;

			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchUpdateMarks : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			// 
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchUpdateMarks : Error : " + e.getMessage());
			// throw e;
		} finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public boolean markPassFail(final List<RemarksGradeBean> list, final String status, final Integer processedFlag,
			final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : markPassFail : Status :"+status);
		boolean isUpdated = Boolean.FALSE; 
		boolean isSaved = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		
		try {
			//startTransaction("markPassFail");
			start_Transaction_U_PR("markPassFail");
			logger.info("RemarksGradeDAO : markPassFail : About to update MARKS table");
			isUpdated = batchUpdateMarks(Boolean.TRUE, list, status, processedFlag, resultLiveFlag, userId);
			
			logger.info("RemarksGradeDAO : markPassFail : About to insert STAGING_PASSFAIL table");
			isSaved = batchSaveInStagingPassFail(Boolean.TRUE, list, status, resultLiveFlag, userId);
			//toCommit = (isSaved && isUpdated);
			//end_Transaction(toCommit);
		} finally {
			toCommit = (isSaved && isUpdated);
			end_Transaction(toCommit);
			if(toCommit) {
				logger.info("RemarksGradeDAO : markPassFail : COMMIT.");
			} else {
				logger.error("RemarksGradeDAO : markPassFail : ROLLBACK...No rows updated in MARKS and inserted in STAGING_PASSFAIL table(s).");
			}
		}
		return toCommit;
	}
	
	public synchronized boolean batchSaveInStagingPassFail(final boolean slaveTransaction,
			final List<RemarksGradeBean> list, final String status, final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveInStagingPassFail : Status : " + status);
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF);
			strBuf.append(" (sapid, subject, programSemSubjectId, assignmentYear, assignmentMonth");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(", iaScore, totalScore, isPass");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append(", isPass");
			}
			//strBuf.append(" graceMarks");
			strBuf.append(", grade, status, failReason, remarks, isResultLive");
			strBuf.append(", active, createdBy, lastModifiedBy)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,?,?,");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append("?,?,?,");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append("?,");
			}
			strBuf.append(" ?,?,?)");
			strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, assignmentYear = ?, assignmentMonth = ?");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(", iaScore = ?, totalScore = ?, isPass = ?");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append(", isPass = ?");
			}
			strBuf.append(", grade = ?, status = ?, failReason = ?, remarks = ?, isResultLive = ?");
			strBuf.append(", active = ?, lastModifiedBy = ?, lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveInStagingPassFail : Query : "+ query);
			
			if(!slaveTransaction) {
				//startTransaction("batchSaveInStagingPassFail");
				start_Transaction_U_PR("batchSaveInStagingPassFail");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					int idx = 1;
					ps.setString(idx++, list.get(arg1).getSapid());
					ps.setString(idx++, list.get(arg1).getSubject());
					ps.setInt(idx++, list.get(arg1).getProgramSemSubjectId());
					ps.setString(idx++, list.get(arg1).getYear());
					ps.setString(idx++, list.get(arg1).getMonth());
					
					if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						ps.setInt(idx++, list.get(arg1).getScoreIA());
						ps.setInt(idx++, list.get(arg1).getScoreTotal());
						if(list.get(arg1).isPass()) {
							ps.setInt(idx++, RemarksGradeBean.STATUS_PASS);//0 fail, 1 pass
						} else {
							ps.setInt(idx++, RemarksGradeBean.STATUS_FAIL);
						}
					} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
						ps.setInt(idx++, RemarksGradeBean.STATUS_RESET_PASSFAIL);
					}
					//ps.setInt(idx++, list.get(arg1).getGraceMarks());
					ps.setString(idx++, list.get(arg1).getGrade());
					ps.setString(idx++, list.get(arg1).getStatus());
					ps.setString(idx++, list.get(arg1).getFailReason());
					ps.setString(idx++, list.get(arg1).getRemarks());
					ps.setInt(idx++, resultLiveFlag);
					ps.setString(idx++, list.get(arg1).getActive());
					ps.setString(idx++, userId);
					ps.setString(idx++, userId);
					
					ps.setString(idx++, list.get(arg1).getSubject());
					ps.setString(idx++, list.get(arg1).getYear());
					ps.setString(idx++, list.get(arg1).getMonth());
					
					if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						ps.setInt(idx++, list.get(arg1).getScoreIA());
						ps.setInt(idx++, list.get(arg1).getScoreTotal());
						if(list.get(arg1).isPass()) {
							ps.setInt(idx++, RemarksGradeBean.STATUS_PASS);//0 fail, 1 pass
						} else {
							ps.setInt(idx++, RemarksGradeBean.STATUS_FAIL);
						}
					} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
						ps.setInt(idx++, RemarksGradeBean.STATUS_RESET_PASSFAIL);
					}
					ps.setString(idx++, list.get(arg1).getGrade());
					ps.setString(idx++, list.get(arg1).getStatus());
					ps.setString(idx++, list.get(arg1).getFailReason());
					ps.setString(idx++, list.get(arg1).getRemarks());
					ps.setInt(idx++, resultLiveFlag);
					ps.setString(idx++, list.get(arg1).getActive());
					ps.setString(idx++, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if(!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveInStagingPassFail : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			if(!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveInStagingPassFail : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> searchStudentForGrading(final RemarksGradeBean remarksGradeBean, final String status,
			final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : searchStudentForGrading : Status : " + status);
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSubjectCodeIdPresent;
		final Boolean isConsProgStrucProgram;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;

		paramSource = new MapSqlParameterSource();
		paramSource.addValue("status", status);
		paramSource.addValue("activeFlag", activeFlag);

		if (isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getSubject())) {
			isSubjectCodeIdPresent = Boolean.FALSE;
		} else {
			isSubjectCodeIdPresent = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId())
				&& isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}

		strBuf = new StringBuffer();
		strBuf.append("SELECT spf.id, spf.sapid, spf.subject, spf.isPass, spf.status");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF).append(" spf");
		strBuf.append(" JOIN exam.students s ON s.sapid = spf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND spf.status = :status");
		if (null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
			strBuf.append(" AND spf.isPass <> :statusPassFail");
			strBuf.append(" AND spf.totalScore is not null");

			paramSource.addValue("statusPassFail", statusPassFail);
		}
		strBuf.append(" AND spf.grade is null");
		strBuf.append(" AND spf.active = :activeFlag");
		if (isSapIdPresent) {
			strBuf.append(" AND s.sapid = :sapid");
			paramSource.addValue("sapid", remarksGradeBean.getSapid());
		}
		if (isYear) {
			strBuf.append(" AND spf.assignmentYear = :year");
			paramSource.addValue("year", remarksGradeBean.getYear());
		}
		if (isMonth) {
			strBuf.append(" AND spf.assignmentMonth = :month");
			paramSource.addValue("month", remarksGradeBean.getMonth());
		}

		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = spf.programSemSubjectId");
		if (isSubjectCodeIdPresent) {
			strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
			strBuf.append(" AND sc.id = :subjectId");

			paramSource.addValue("subjectId", remarksGradeBean.getSubject());
		}

		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = :consumerTypeId");
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = :programStructureId");
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = :programId");
			paramSource.addValue("programId", remarksGradeBean.getProgramId());
		}

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentForGrading : Query : " + sql);

		list = this.namedParameterJdbcTemplate.query(sql, paramSource,
				new ResultSetExtractor<List<RemarksGradeBean>>() {

					@Override
					public List<RemarksGradeBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
						// TODO Auto-generated method stub
						RemarksGradeBean bean = null;
						List<RemarksGradeBean> listRG = null;
						listRG = new ArrayList<RemarksGradeBean>();

						while (rs.next()) {
							bean = new RemarksGradeBean();
							bean.setId(rs.getInt("id"));
							bean.setSapid(rs.getString("sapid"));
							bean.setSubject(rs.getString("subject"));
							if (null != status && (RemarksGradeBean.ATTEMPTED.equals(status)
									|| RemarksGradeBean.CC.equals(status))) {
								if (rs.getInt("isPass") == RemarksGradeBean.STATUS_PASS) {
									bean.setPass(Boolean.TRUE);
								} else {
									bean.setPass(Boolean.FALSE);
								}
							}
							bean.setStatus(rs.getString("status"));

							listRG.add(bean);
						}
						return listRG;
					}
				});

		if (null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentForGrading : List Size " + list.size());
		}
		return list;
	}
	
	/*@Deprecated
	public List<RemarksGradeBean> searchStudentForGrading(final RemarksGradeBean remarksGradeBean, final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : searchStudentForGrading : Status : "+status);
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSubjectCodeIdPresent;
		final Boolean isConsProgStrucProgram;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		if(isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getSubject())) {
			isSubjectCodeIdPresent = Boolean.FALSE;
		} else {
			isSubjectCodeIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT spf.id, spf.sapid, spf.subject, spf.isPass, spf.status");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF).append(" spf");
		strBuf.append(" JOIN exam.students s ON s.sapid = spf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND spf.status = '").append(status).append("'");
		if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
			strBuf.append(" AND spf.isPass <> ").append(statusPassFail);
			strBuf.append(" AND spf.totalScore is not null");
		}
		strBuf.append(" AND spf.grade is null");
		strBuf.append(" AND spf.active = '").append(activeFlag).append("'");
		if(isSapIdPresent) {
			strBuf.append(" AND s.sapid = ?");
		}
		if(isYear) {
			strBuf.append(" AND spf.assignmentYear = ?");
		}
		if(isMonth) {
			strBuf.append(" AND spf.assignmentMonth = ?");
		}
		
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = spf.programSemSubjectId");
		if(isSubjectCodeIdPresent) {
			strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
			strBuf.append(" AND sc.id = ?");
		}
		
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = ?");
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = ?");
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = ?");
		}
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentForGrading : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				if(isSapIdPresent) {
					arg0.setString(idx++, remarksGradeBean.getSapid());
				}
				if(isYear) {
					arg0.setString(idx++, remarksGradeBean.getYear());
				}
				if(isMonth) {
					arg0.setString(idx++, remarksGradeBean.getMonth());
				}
				if(isSubjectCodeIdPresent) {
					arg0.setString(idx++, remarksGradeBean.getSubject());
				}
				if(isConsProgStrucProgram) {
					arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
					arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
					arg0.setString(idx++, remarksGradeBean.getProgramId());
				}
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setId(rs.getInt("id"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						if(rs.getInt("isPass") == RemarksGradeBean.STATUS_PASS) {
							bean.setPass(Boolean.TRUE);
						} else {
							bean.setPass(Boolean.FALSE);
						}
					}
					bean.setStatus(rs.getString("status"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentForGrading : List Size "+ list.size());
		}
		return list;
	}*/
	
	public synchronized boolean batchSaveStudentsGrade(final List<RemarksGradeBean> list, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveStudentsGrade");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF);
			strBuf.append(" SET grade = ?, isResultLive = ?, lastModifiedBy = ?, lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			strBuf.append(" WHERE id = ?");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveStudentsGrade : Query : "+ query);
			
			//startTransaction("batchSaveStudentsGrade");
			start_Transaction_U_PR("batchSaveStudentsGrade");

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getGrade());
					ps.setInt(2, list.get(arg1).getResultLive());
					ps.setString(3, userId);
					ps.setInt(4, list.get(arg1).getId());
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			end_Transaction(isSuccess);
			logger.info("RemarksGradeDAO : batchSaveStudentsGrade : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			//
			end_Transaction(Boolean.FALSE);
			logger.error("RemarksGradeDAO : batchSaveStudentsGrade : Error : " + e.getMessage());
			//throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public synchronized List<RemarksGradeBean> searchStudentsAfterGrading(final RemarksGradeBean remarksGradeBean,
			final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : searchStudentsAfterGrading : Status : " + status);
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;

		paramSource = new MapSqlParameterSource();
		paramSource.addValue("status", status);
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("year", remarksGradeBean.getYear());
		paramSource.addValue("month", remarksGradeBean.getMonth());

		strBuf = new StringBuffer();
		strBuf.append(
				"SELECT spf.id, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" spf.sapid, spf.subject, spf.assignmentYear, spf.assignmentMonth,");
		strBuf.append(
				" spf.writtenScore, spf.iaScore, spf.totalScore, spf.grade, spf.failReason, spf.isPass, spf.status,");
		strBuf.append(" spf.programSemSubjectId, spf.remarks, spf.isResultLive, spf.active,");
		strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF).append(" spf");
		strBuf.append(" JOIN exam.students s ON s.sapid = spf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND spf.assignmentYear = :year AND spf.assignmentMonth = :month");
		strBuf.append(" AND spf.status = :status");
		if (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status)) {
			strBuf.append(" AND spf.isPass <> :statusPassFail");
			strBuf.append(" AND spf.totalScore is not null");
			strBuf.append(" AND spf.grade is not null");

			paramSource.addValue("statusPassFail", statusPassFail);
		}
		strBuf.append(" AND spf.active = :activeFlag");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = spf.programSemSubjectId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" ORDER BY spf.status, scm.sem, spf.sapid ASC");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentsAfterGrading : Query : " + sql);

		list = this.namedParameterJdbcTemplate.query(sql, paramSource,
				new ResultSetExtractor<List<RemarksGradeBean>>() {

					@Override
					public List<RemarksGradeBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
						// TODO Auto-generated method stub

						RemarksGradeBean bean = null;
						List<RemarksGradeBean> listRG = null;
						listRG = new ArrayList<RemarksGradeBean>();

						while (rs.next()) {
							bean = new RemarksGradeBean();
							bean.setId(rs.getInt("id"));
							bean.setSapid(rs.getString("sapid"));
							bean.setSubject(rs.getString("subject"));
							bean.setName(rs.getString("name"));
							bean.setSem(rs.getString("sem"));
							if (rs.getInt("isPass") == RemarksGradeBean.STATUS_PASS) {
								bean.setPass(Boolean.TRUE);
							} else {
								bean.setPass(Boolean.FALSE);
							}
							bean.setStatus(rs.getString("status"));
							bean.setYear(rs.getString("assignmentYear"));
							bean.setMonth(rs.getString("assignmentMonth"));
							bean.setStudentType(rs.getString("consumerTypeName"));
							bean.setProgramStructure(rs.getString("program_structure"));
							bean.setProgram(rs.getString("code"));
							bean.setScoreWritten(rs.getInt("writtenScore"));
							bean.setScoreIA(rs.getInt("iaScore"));
							bean.setScoreTotal(rs.getInt("totalScore"));
							bean.setGrade(rs.getString("grade"));
							bean.setFailReason(rs.getString("failReason"));
							// bean.setStudentTypeId(rs.getString("consumerTypeId"));
							// bean.setProgramStructureId(rs.getString("programStructureId"));
							// bean.setProgramId(rs.getString("programId"));
							bean.setProgramSemSubjectId(rs.getInt("programSemSubjectId"));
							bean.setRemarks(rs.getString("remarks"));
							bean.setResultLive(rs.getInt("isResultLive"));
							bean.setActive(rs.getString("active"));

							listRG.add(bean);
						}
						return listRG;
					}
				});

		if (null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentsAfterGrading : List Size : " + list.size());
		}
		return list;
	}
	
	/*@Deprecated
	public synchronized List<RemarksGradeBean> searchStudentsAfterGrading(final RemarksGradeBean remarksGradeBean,
			final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeDAO : searchStudentsAfterGrading : Status : "+status);
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT spf.id, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" spf.sapid, spf.subject, spf.assignmentYear, spf.assignmentMonth,");
		strBuf.append(" spf.writtenScore, spf.iaScore, spf.totalScore, spf.grade, spf.failReason, spf.isPass, spf.status,");
		strBuf.append(" spf.programSemSubjectId, spf.remarks, spf.isResultLive, spf.active,");
		strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF).append(" spf");
		strBuf.append(" JOIN exam.students s ON s.sapid = spf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND spf.assignmentYear = ? AND spf.assignmentMonth = ?");
		strBuf.append(" AND spf.status = '").append(status).append("'");
		if(RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status)) {
			strBuf.append(" AND spf.isPass <> ").append(statusPassFail);
			strBuf.append(" AND spf.totalScore is not null");
			strBuf.append(" AND spf.grade is not null");
		}
		strBuf.append(" AND spf.active = '").append(activeFlag).append("'");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = spf.programSemSubjectId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" ORDER BY spf.status, scm.sem, spf.sapid ASC");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentsAfterGrading : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx++, remarksGradeBean.getYear());
				arg0.setString(idx++, remarksGradeBean.getMonth());
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setId(rs.getInt("id"));
					bean.setSapid(rs.getString("sapid"));
					bean.setSubject(rs.getString("subject"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					if(rs.getInt("isPass") == RemarksGradeBean.STATUS_PASS) {
						bean.setPass(Boolean.TRUE);
					} else {
						bean.setPass(Boolean.FALSE);
					}
					bean.setStatus(rs.getString("status"));
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("code"));
					bean.setScoreWritten(rs.getInt("writtenScore"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setScoreTotal(rs.getInt("totalScore"));
					bean.setGrade(rs.getString("grade"));
					bean.setFailReason(rs.getString("failReason"));
					//bean.setStudentTypeId(rs.getString("consumerTypeId"));
					//bean.setProgramStructureId(rs.getString("programStructureId"));
					//bean.setProgramId(rs.getString("programId"));
					bean.setProgramSemSubjectId(rs.getInt("programSemSubjectId"));
					bean.setRemarks(rs.getString("remarks"));
					bean.setResultLive(rs.getInt("isResultLive"));
					bean.setActive(rs.getString("active"));
					
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentsAfterGrading : List Size : "+ list.size());
		}
		return list;
	}*/
	
	public synchronized Integer transferPassFail(final RemarksGradeBean bean, final Map<String, List<RemarksGradeBean>> dataMap,
			final Integer resultLiveFlag, final String activeFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : transferPassFail");
		boolean isDeletedATTEMPTED = Boolean.FALSE;
		boolean isDeletedCC = Boolean.FALSE;
		boolean isDeletedAB = Boolean.FALSE;
		boolean isSavedATTEMPTED = Boolean.FALSE;
		boolean isSavedCC = Boolean.FALSE;
		boolean isSavedAB = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		List<RemarksGradeBean> list1 = null;
		List<RemarksGradeBean> list2 = null;
		List<RemarksGradeBean> list3 = null;
		Integer rowsTransferred = 0;

		try {
			list1 = dataMap.get(RemarksGradeBean.ATTEMPTED);
			list2 = dataMap.get(RemarksGradeBean.CC);
			list3 = dataMap.get(RemarksGradeBean.ABSENT);
			
			//startTransaction("transferPassFail");
			start_Transaction_U_PR("transferPassFail");
			if(checkNonEmptyList(list1)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to insert PASSFAIL table (ATTEMPTED).");
				isSavedATTEMPTED = batchSaveInPassFail(Boolean.TRUE, list1, RemarksGradeBean.ATTEMPTED, resultLiveFlag, userId);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO insert PASSFAIL table (ATTEMPTED).");
			}
			
			if(checkNonEmptyList(list2)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to insert PASSFAIL table (CC).");
				isSavedCC = batchSaveInPassFail(Boolean.TRUE, list2, RemarksGradeBean.CC, resultLiveFlag, userId);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO insert PASSFAIL table (CC).");
			}
			
			if(checkNonEmptyList(list3)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to insert PASSFAIL table (AB).");
				isSavedAB = batchSaveInPassFail(Boolean.TRUE, list3, RemarksGradeBean.ABSENT, resultLiveFlag, userId);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO insert PASSFAIL table (AB).");
			}
			
			if(checkNonEmptyList(list1)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to delete from STAGING_PASSFAIL table (ATTEMPTED).");
				isDeletedATTEMPTED = batchDeleteFromStagingPassFail(Boolean.TRUE, list1);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO delete from STAGING_PASSFAIL table (ATTEMPTED).");
			}
			
			if(checkNonEmptyList(list2)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to delete from STAGING_PASSFAIL table (CC).");
				isDeletedCC = batchDeleteFromStagingPassFail(Boolean.TRUE, list2);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO delete from STAGING_PASSFAIL table (CC).");
			}
			
			if(checkNonEmptyList(list3)) {
				logger.info("RemarksGradeDAO : transferPassFail : About to delete from STAGING_PASSFAIL table (AB).");
				isDeletedAB = batchDeleteFromStagingPassFail(Boolean.TRUE, list3);
			} else {
				logger.info("RemarksGradeDAO : transferPassFail : EMPTY NO delete from STAGING_PASSFAIL table (AB).");
			}
		} finally {
			toCommit = ((isSavedATTEMPTED || isSavedCC || isSavedAB) && (isDeletedATTEMPTED  || isDeletedCC || isDeletedAB));
			end_Transaction(toCommit);
			
			if(toCommit) {
				if(null != list1) {
					rowsTransferred = list1.size();
				}
				if(null != list2) {
					rowsTransferred += list2.size();
				}
				if(null != list3) {
					rowsTransferred += list3.size();
				}
				logger.info("RemarksGradeDAO : transferPassFail : COMMIT.");
			} else {
				logger.error("RemarksGradeDAO : transferPassFail : ROLLBACK...No rows insert in PASSFAIL and delete from STAGING_PASSFAIL table(s).");
			}
		}
		return rowsTransferred;
	}
	
	public static boolean checkNonEmptyList(List<RemarksGradeBean> list) {
		boolean isNonEmpty = Boolean.FALSE;
		if(null != list && !list.isEmpty()) {
			isNonEmpty = Boolean.TRUE;
		}
		return isNonEmpty;
	}
	
	public synchronized boolean batchSaveInPassFail(final boolean slaveTransaction, final List<RemarksGradeBean> list,
			final String status, final Integer resultLiveFlag, final String userId) {
		logger.info("Entering RemarksGradeDAO : batchSaveInPassFail : Status : "+status);
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF);
			strBuf.append(" (sapid, subject, programSemSubjectId, assignmentYear, assignmentMonth");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(", iaScore, totalScore, grade, isPass");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append(", isPass");
			}
			// strBuf.append(" graceMarks");
			strBuf.append(" , remarks, status, failReason, isResultLive,");
			strBuf.append(" active, createdBy, createdDate, lastModifiedBy)");
			strBuf.append(" VALUES (?,?,?,?,?,?,?,?,?");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(" ,?,?,?,?");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append(" ,?");
			}
			strBuf.append(" ,?,?,").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP).append(",?)");
			strBuf.append(" ON DUPLICATE KEY UPDATE subject = ?, assignmentYear = ?, assignmentMonth = ?");
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				strBuf.append(" , iaScore = ?, totalScore = ?, grade = ?, isPass = ?");
			} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
				strBuf.append(" , isPass = ?");
			}
			strBuf.append(" , remarks = ?, status = ?, failReason = ?, isResultLive = ?,");
			strBuf.append(" active = ?, lastModifiedBy = ?, lastModifiedDate = ").append(RemarksGradeBean.DB_CURRENT_TIMESTAMP);
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveInPassFail : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("batchSaveInPassFail");
				start_Transaction_U_PR("batchSaveInPassFail");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					int idx = 1;
					ps.setString(idx++, list.get(arg1).getSapid());
					ps.setString(idx++, list.get(arg1).getSubject());
					ps.setInt(idx++, list.get(arg1).getProgramSemSubjectId());
					ps.setString(idx++, list.get(arg1).getYear());
					ps.setString(idx++, list.get(arg1).getMonth());
					if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						ps.setInt(idx++, list.get(arg1).getScoreIA());
						ps.setInt(idx++, list.get(arg1).getScoreTotal());
						ps.setString(idx++, list.get(arg1).getGrade());
						if (list.get(arg1).isPass()) {
							ps.setInt(idx++, RemarksGradeBean.STATUS_PASS);// 0 fail, 1 pass
						} else {
							ps.setInt(idx++, RemarksGradeBean.STATUS_FAIL);
						}
					} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
						ps.setInt(idx++, RemarksGradeBean.STATUS_RESET_PASSFAIL);
					}
					// ps.setInt(idx++, list.get(arg1).getGraceMarks());
					
					ps.setString(idx++, list.get(arg1).getRemarks());
					ps.setString(idx++, list.get(arg1).getStatus());
					ps.setString(idx++, list.get(arg1).getFailReason());
					ps.setInt(idx++, resultLiveFlag);
					ps.setString(idx++, list.get(arg1).getActive());
					ps.setString(idx++, userId);
					ps.setString(idx++, userId);

					ps.setString(idx++, list.get(arg1).getSubject());
					ps.setString(idx++, list.get(arg1).getYear());
					ps.setString(idx++, list.get(arg1).getMonth());
					
					if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
						ps.setInt(idx++, list.get(arg1).getScoreIA());
						ps.setInt(idx++, list.get(arg1).getScoreTotal());
						ps.setString(idx++, list.get(arg1).getGrade());
						if (list.get(arg1).isPass()) {
							ps.setInt(idx++, RemarksGradeBean.STATUS_PASS);// 0 fail, 1 pass
						} else {
							ps.setInt(idx++, RemarksGradeBean.STATUS_FAIL);
						}
					} else if(null != status && RemarksGradeBean.ABSENT.equals(status)) {
						ps.setInt(idx++, RemarksGradeBean.STATUS_RESET_PASSFAIL);
					}
					
					ps.setString(idx++, list.get(arg1).getRemarks());
					ps.setString(idx++, list.get(arg1).getStatus());
					ps.setString(idx++, list.get(arg1).getFailReason());
					ps.setInt(idx++, resultLiveFlag);
					ps.setString(idx++, list.get(arg1).getActive());
					ps.setString(idx++, userId);
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveInPassFail : Saved " + arrResults.length + " rows.");
		} catch (Exception e) {
			// 
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveInPassFail : Error : " + e.getMessage());
			// throw e;
		} finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized boolean batchDeleteFromStagingPassFail(final boolean slaveTransaction,
			final List<RemarksGradeBean> list) {
		logger.info("Entering RemarksGradeDAO : batchDeleteFromStagingPassFail");
		boolean isDeleted = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("DELETE FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF)
					.append(" WHERE id = ?");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchDeleteFromStagingPassFail : Query : "+ query);
			
			if (!slaveTransaction) {
				//startTransaction("batchDeleteFromStagingPassFail");
				start_Transaction_U_PR("batchDeleteFromStagingPassFail");
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setInt(1, list.get(arg1).getId());
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isDeleted = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isDeleted);
			}
			logger.info("RemarksGradeDAO : batchDeleteFromStagingPassFail : Deleted " + arrResults.length + " rows.");
		} catch (Exception e) {
			// 
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchDeleteFromStagingPassFail : Error : " + e.getMessage());
			// throw e;
		} finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isDeleted;
	}
	
	public synchronized boolean batchSaveNullify(final boolean slaveTransaction, final List<RemarksGradeBean> list, final String year, final String month,
			final String tableName, final String userId, final String activeFlag, final Integer statusPassFail, final Integer resultLive) {
		logger.info("Entering RemarksGradeDAO : batchSaveNullify (for Table : "+tableName+")");
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;

		try {
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(tableName).append(" spf");// .append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF)
			strBuf.append(" JOIN exam.students s ON s.sapid = spf.sapid");
			strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
			strBuf.append(" AND spf.active = '").append(activeFlag).append("'");
			strBuf.append(" AND spf.assignmentYear = ? AND spf.assignmentMonth = ?");
			strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = spf.programSemSubjectId");
			strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
			strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
			strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
			strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
			strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId ");
			strBuf.append(" AND pgm.code = ?").append(" AND s.sapid = ?");
			strBuf.append(" SET spf.totalScore = null, spf.grade = null, spf.failReason = null");
			//strBuf.append(" , spf.graceMarks = null");
			strBuf.append(" , spf.writtenScore = null, spf.iaScore = null");
			strBuf.append(" , spf.isPass = ").append(statusPassFail);// -1
			strBuf.append(" , spf.isResultLive = ").append(resultLive);// 0
			strBuf.append(" , spf.active ='").append(activeFlag).append("'");// Y
			strBuf.append(" , spf.lastModifiedBy = '").append(userId).append("'").append(" , spf.lastModifiedDate = current_timestamp");
			strBuf.append(" where 1 = 1");
			query = strBuf.toString();
			logger.info("RemarksGradeDAO : batchSaveNullify (for Table : "+tableName+") : Query : "+query);
			
			if (!slaveTransaction) {
				//startTransaction("batchSaveNullify"+tableName);
				start_Transaction_U_PR("batchSaveNullify"+tableName);
			}

			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					int idx = 1;
					ps.setString(idx++, year);//examYear
					ps.setString(idx++, month);//examMonth
					ps.setString(idx++, list.get(arg1).getProgram());
					ps.setString(idx++, list.get(arg1).getSapid());
				}

				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			isSuccess = Boolean.TRUE;
			if (!slaveTransaction) {
				end_Transaction(isSuccess);
			}
			logger.info("RemarksGradeDAO : batchSaveNullify (for Table : "+tableName+") : Updated " + arrResults.length + " rows.");
		} catch (Exception e) {
			// 
			if (!slaveTransaction) {
				end_Transaction(Boolean.FALSE);
			}
			logger.error("RemarksGradeDAO : batchSaveNullify (for Table : "+tableName+") : Error : " + e.getMessage());
			// throw e;
		} finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized Boolean changeResultsLiveState(final RemarksGradeBean remarksGradeBean, final Set<String> statusSet,
			final Integer statusPassFail, final Integer processedFlag, final Integer resultLive,
			final String activeFlag,final String userId) {
		logger.info("Entering RemarksGradeDAO : changeResultsLiveState");
		Boolean isSuccess = Boolean.FALSE;
		Integer rowsUpdatedMarks = -1;
		Integer rowsUpdatedPassFail = -1;
		String message = null;
		String status = null;
		try {
			logger.info("Entering RemarksGradeDAO : changeResultsLiveState : isResultLive : "+resultLive);
			//startTransaction("changeResultsLiveState");
			start_Transaction_U_PR("changeResultsLiveState");
			
			status = RemarksGradeBean.ATTEMPTED;
			if(statusSet.contains(status)) {
				rowsUpdatedMarks = changeResultsLiveInMarks(remarksGradeBean, status, processedFlag, resultLive, activeFlag,
						userId);
				rowsUpdatedPassFail = changeResultsLiveInTable((DB_SCHEMA_REMARKPF + "." + TABLE_PF), remarksGradeBean,
						status, statusPassFail, resultLive, activeFlag, userId);
				message = "Total (STATUS, MARKS, PASSFAIL) (" + status + "," + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
			}
			
			status = RemarksGradeBean.CC;
			if(statusSet.contains(status)) {
				rowsUpdatedMarks = changeResultsLiveInMarks(remarksGradeBean, status, processedFlag, resultLive, activeFlag,
						userId);
				rowsUpdatedPassFail = changeResultsLiveInTable((DB_SCHEMA_REMARKPF + "." + TABLE_PF), remarksGradeBean,
						status, statusPassFail, resultLive, activeFlag, userId);
				if(null == message) {
					message = "Total (STATUS, MARKS, PASSFAIL) (" + status + "," + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
				} else {
					message += "\n" + "Total (STATUS, MARKS, PASSFAIL) (" + status + "," + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
				}
			}
			
			status = RemarksGradeBean.ABSENT;
			if(statusSet.contains(status)) {
				rowsUpdatedMarks = changeResultsLiveInMarks(remarksGradeBean, status, processedFlag, resultLive, activeFlag,
						userId);
				rowsUpdatedPassFail = changeResultsLiveInTable((DB_SCHEMA_REMARKPF + "." + TABLE_PF), remarksGradeBean,
						status, statusPassFail, resultLive, activeFlag, userId);
				if(null == message) {
					message = "Total (STATUS, MARKS, PASSFAIL) (" + status + "," + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
				} else {
					message += "\n" + "Total (STATUS, MARKS, PASSFAIL) (" + status + "," + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
				}
			}
			
			isSuccess = Boolean.TRUE;
		} finally {
			end_Transaction(isSuccess);
			//message = "Total (MARKS, PASSFAIL) (" + rowsUpdatedMarks + "," + rowsUpdatedPassFail + ") rows.";
			if (isSuccess) {
				remarksGradeBean.setStatus(RemarksGradeBean.KEY_SUCCESS);
				logger.info("RemarksGradeDAO : changeResultsLiveState : COMMIT.");
			} else {
				message = "FAIL : ResultLive (ResultLive, Year, Month) : ("+ resultLive + "," + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ")";
				remarksGradeBean.setStatus(RemarksGradeBean.KEY_ERROR);
				logger.error(message + " " +
						"RemarksGradeDAO : changeResultsLiveState : ROLLBACK...No rows updated in MARKS and in PASSFAIL table(s).");
			}
			remarksGradeBean.setMessage(message);
			logger.info(message);
		}
		return isSuccess;
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	protected synchronized Integer changeResultsLiveInMarks(final RemarksGradeBean remarksGradeBean,
			final String status, final Integer processedFlag, final Integer resultLive, final String activeFlag, String userId) {
		logger.info("Entering RemarksGradeDAO : changeResultsLiveInMarks");
		String sql = null;
		StringBuffer strBuf = null;
		Integer retVal = null;

		strBuf = new StringBuffer();
		strBuf.append("UPDATE ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m"); 
		strBuf.append(" SET m.isResultLive = ").append(resultLive);
		strBuf.append(" , m.lastModifiedBy = '").append(userId).append("'");
		strBuf.append(" , m.lastModifiedDate = current_timestamp");
		strBuf.append(" WHERE m.assignmentYear = ?").append(" AND m.assignmentMonth = ?");
		if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
			strBuf.append(" AND m.iaScore is not null");
			strBuf.append(" AND m.totalScore is not null");
		}// do nothing for RemarksGradeBean.ABSENT
		strBuf.append(" AND m.status = '").append(status).append("'");
		strBuf.append(" AND m.processed = ").append(processedFlag);
		strBuf.append(" AND m.active = '").append(activeFlag).append("'");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : changeResultsLiveInMarks : Query : " + sql);

		retVal = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				Integer val = null;
				int idx = 1;
				arg0.setString(idx++, remarksGradeBean.getYear());
				arg0.setString(idx++, remarksGradeBean.getMonth());

				val = arg0.executeUpdate();

				return val;
			}
		});
		logger.info("RemarksGradeDAO : changeResultsLiveInMarks : Rows Updated : " + retVal);
		return retVal;
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	protected synchronized Integer changeResultsLiveInTable(final String tableName, final RemarksGradeBean remarksGradeBean,
			final String status, final Integer statusPassFail, final Integer resultLive, final String activeFlag, String userId) {
		logger.info("Entering RemarksGradeDAO : changeResultsLiveInTable ("+tableName+")");
		String sql = null;
		StringBuffer strBuf = null;
		Integer retVal = null;

		strBuf = new StringBuffer();
		strBuf.append("UPDATE ").append(tableName).append(" t");// .append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF)
		strBuf.append(" SET t.isResultLive = ").append(resultLive);
		strBuf.append(" , t.lastModifiedBy = '").append(userId).append("', t.lastModifiedDate = current_timestamp");
		strBuf.append(" WHERE t.status = '").append(status).append("'");
		strBuf.append(" AND t.assignmentYear = ?").append(" AND t.assignmentMonth = ?");
		if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
			strBuf.append(" AND t.isPass <> ").append(statusPassFail);
			strBuf.append(" AND t.totalScore is not null");
			strBuf.append(" AND t.grade is not null");
		}// do nothing for RemarksGradeBean.ABSENT
		strBuf.append(" AND t.active = '").append(activeFlag).append("'");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : changeResultsLiveInTable ("+tableName+") : Query : " + sql);

		retVal = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				Integer val = null;
				int idx = 1;
				arg0.setString(idx++, remarksGradeBean.getYear());
				arg0.setString(idx++, remarksGradeBean.getMonth());

				val = arg0.executeUpdate();

				return val;
			}
		});
		logger.info("RemarksGradeDAO : changeResultsLiveInTable ("+tableName+") : Rows Updated : " + retVal);
		return retVal;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public synchronized List<RemarksGradeBean> searchResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean,
			final String activeFlag, final Boolean countRequired) {
		logger.info("Entering RemarksGradeDAO : searchResultsAsPassFailReport");
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		final Boolean isInfoCentre;
		final Boolean isStudentsResultType;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		Integer totalRowCount = null;
		RemarksGradeBean remarksGradeBeanObj = null;
		final MapSqlParameterSource paramSource;

		paramSource = new MapSqlParameterSource();
		paramSource.addValue("activeFlag", activeFlag);

		if (isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getSem())) {
			isSemPresent = Boolean.FALSE;
		} else {
			isSemPresent = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId())
				&& isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getCenterCode())) {
			isInfoCentre = Boolean.FALSE;
		} else {
			isInfoCentre = Boolean.TRUE;
		}
		if (isBlank(remarksGradeBean.getStudentsResultType())) {
			isStudentsResultType = Boolean.FALSE;
		} else {
			isStudentsResultType = Boolean.TRUE;
		}

		strBuf = new StringBuffer();
		if (countRequired) {
			strBuf.append("SELECT count(pf.id) totalRows");
		} else {
			strBuf.append(
					"SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
			strBuf.append(
					" pf.sapid, pf.subject, pf.assignmentYear, pf.assignmentMonth, pf.iaScore, pf.totalScore, pf.writtenScore, pf.status,");
			strBuf.append(" pf.grade, pf.isPass, pf.failReason, pf.remarks, pf.isResultLive,");
			strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code,");
			// strBuf.append(" ct.id 'consumerTypeId', ps.id 'programStructureId', pgm.id
			// 'programId'");
			strBuf.append(" cen.centerName");// , cen.centerCode
		}
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF).append(" pf");
		strBuf.append(" JOIN exam.students s ON s.sapid = pf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND pf.active = :activeFlag");
		if (isSapIdPresent) {
			strBuf.append(" AND s.sapid = :sapid");
			paramSource.addValue("sapid", remarksGradeBean.getSapid());
		}
		if (isYear) {
			strBuf.append(" AND pf.assignmentYear = :year");
			paramSource.addValue("year", remarksGradeBean.getYear());
		}
		if (isMonth) {
			strBuf.append(" AND pf.assignmentMonth = :month");
			paramSource.addValue("month", remarksGradeBean.getMonth());
		}
		if (isStudentsResultType) {
			if (RemarksGradeBean.STATE_PASS.equals(remarksGradeBean.getStudentsResultType())) {
				strBuf.append(" AND pf.isPass = ").append(RemarksGradeBean.STATUS_PASS);
			} else {
				strBuf.append(" AND pf.isPass = ").append(RemarksGradeBean.STATUS_FAIL);
			}
		}

		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = pf.programSemSubjectId");
		if (isSemPresent) {
			strBuf.append(" AND scm.sem = :sem");
			paramSource.addValue("sem", remarksGradeBean.getSem());
		}

		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = :consumerTypeId");
			paramSource.addValue("consumerTypeId", remarksGradeBean.getStudentTypeId());
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = :programStructureId");
			paramSource.addValue("programStructureId", remarksGradeBean.getProgramStructureId());
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if (isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = :programId");
			paramSource.addValue("programId", remarksGradeBean.getProgramId());
		}
		strBuf.append(" JOIN exam.centers cen ON cen.centerCode = s.centerCode");
		if (isInfoCentre) {
			strBuf.append(" AND cen.centerCode = :centerCode");
			paramSource.addValue("centerCode", remarksGradeBean.getCenterCode());
		}
		strBuf.append(" ORDER BY pf.status, pgm.code ASC");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchResultsAsPassFailReport (countRequired) : (" + countRequired
				+ ") : Query : " + sql);

		if (countRequired) {
			totalRowCount = this.namedParameterJdbcTemplate.query(sql, paramSource, new ResultSetExtractor<Integer>() {

				@Override
				public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					Integer rowCount = null;

					while (rs.next()) {
						rowCount = rs.getInt("totalRows");
					}
					return rowCount;
				}
			});
			logger.info("RemarksGradeDAO : searchResultsAsPassFailReport : Rows found " + totalRowCount);
			remarksGradeBeanObj = new RemarksGradeBean();
			remarksGradeBeanObj.setStatus(RemarksGradeBean.KEY_SUCCESS);
			remarksGradeBeanObj.setMessage("Total Rows found : " + totalRowCount);
			list = new ArrayList<RemarksGradeBean>();
			list.add(remarksGradeBeanObj);
		} else {
			list = this.namedParameterJdbcTemplate.query(sql, paramSource,
					new ResultSetExtractor<List<RemarksGradeBean>>() {

						@Override
						public List<RemarksGradeBean> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							// TODO Auto-generated method stub

							RemarksGradeBean bean = null;
							List<RemarksGradeBean> listRG = null;
							listRG = new ArrayList<RemarksGradeBean>();

							while (rs.next()) {
								bean = new RemarksGradeBean();
								bean.setYear(rs.getString("assignmentYear"));
								bean.setMonth(rs.getString("assignmentMonth"));
								bean.setSapid(rs.getString("sapid"));
								bean.setSubject(rs.getString("subject"));
								bean.setScoreIA(rs.getInt("iaScore"));
								bean.setScoreWritten(rs.getInt("writtenScore"));
								bean.setScoreTotal(rs.getInt("totalScore"));
								bean.setName(rs.getString("name"));
								bean.setSem(rs.getString("sem"));
								bean.setStudentType(rs.getString("consumerTypeName"));
								bean.setProgramStructure(rs.getString("program_structure"));
								bean.setProgram(rs.getString("code"));
								bean.setStatus(rs.getString("status"));
								// bean.setStudentTypeId(rs.getString("consumerTypeId"));
								// bean.setProgramStructureId(rs.getString("programStructureId"));
								// bean.setProgramId(rs.getString("programId"));
								bean.setGrade(rs.getString("grade"));
								bean.setPassStatus(rs.getInt("isPass"));
								/// *if(rs.getInt("isPass") == 1) {
								// bean.setPass(Boolean.TRUE);
								// } else {
								// bean.setPass(Boolean.FALSE);
								// }*/
								bean.setResultLive(rs.getInt("isResultLive"));
								/// *if(rs.getInt("isResultLive") == 1) {
								// bean.setAssignmentMarksLive(RemarksGradeBean.TEXT_RESULT_LIVE);
								// } else {
								// bean.setAssignmentMarksLive(RemarksGradeBean.TEXT_RESULT_NOT_LIVE);
								// }*/
								bean.setFailReason(rs.getString("failReason"));
								bean.setRemarks(rs.getString("remarks"));
								bean.setCenterName(rs.getString("centerName"));
								// bean.setActive(rs.getString("active"));

								listRG.add(bean);
							}
							return listRG;
						}
					});
			if (null != list && !list.isEmpty()) {
				logger.info("RemarksGradeDAO : searchResultsAsPassFailReport : List Size " + list.size());
			}
		}
		return list;
	}
	
	/*@Deprecated
	 public synchronized List<RemarksGradeBean> searchResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean,
			final String activeFlag, final Boolean countRequired) {
		logger.info("Entering RemarksGradeDAO : searchResultsAsPassFailReport");
		final Boolean isYear;
		final Boolean isMonth;
		final Boolean isSapIdPresent;
		final Boolean isSemPresent;
		final Boolean isConsProgStrucProgram;
		final Boolean isInfoCentre;
		final Boolean isStudentsResultType;
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		Integer totalRowCount = null;
		RemarksGradeBean remarksGradeBeanObj = null;
		
		if(isBlank(remarksGradeBean.getSapid())) {
			isSapIdPresent = Boolean.FALSE;
		} else {
			isSapIdPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getYear())) {
			isYear = Boolean.FALSE;
		} else {
			isYear = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getMonth())) {
			isMonth = Boolean.FALSE;
		} else {
			isMonth = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getSem())) {
			isSemPresent = Boolean.FALSE;
		} else {
			isSemPresent = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentTypeId()) && isBlank(remarksGradeBean.getProgramStructureId()) && isBlank(remarksGradeBean.getProgramId())) {
			isConsProgStrucProgram = Boolean.FALSE;
		} else {
			isConsProgStrucProgram = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getCenterCode())) {
			isInfoCentre = Boolean.FALSE;
		} else {
			isInfoCentre = Boolean.TRUE;
		}
		if(isBlank(remarksGradeBean.getStudentsResultType())) {
			isStudentsResultType = Boolean.FALSE;
		} else {
			isStudentsResultType = Boolean.TRUE;
		}
		
		strBuf = new StringBuffer();
		if(countRequired) {
			strBuf.append("SELECT count(pf.id) totalRows");
		} else {
			strBuf.append("SELECT concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
			strBuf.append(" pf.sapid, pf.subject, pf.assignmentYear, pf.assignmentMonth, pf.iaScore, pf.totalScore, pf.writtenScore, pf.status,");
			strBuf.append(" pf.grade, pf.isPass, pf.failReason, pf.remarks, pf.isResultLive,");
			strBuf.append(" scm.sem, ct.name 'consumerTypeName', ps.program_structure, pgm.code,");
			//strBuf.append(" ct.id 'consumerTypeId', ps.id 'programStructureId', pgm.id 'programId'");
			strBuf.append(" cen.centerName");//, cen.centerCode
		}
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF).append(" pf");
		strBuf.append(" JOIN exam.students s ON s.sapid = pf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND pf.active = '").append(activeFlag).append("'");
		if(isSapIdPresent) {
			strBuf.append(" AND s.sapid = ?");
		}
		if(isYear) {
			strBuf.append(" AND pf.assignmentYear = ?");
		}
		if(isMonth) {
			strBuf.append(" AND pf.assignmentMonth = ?");
		}
		if(isStudentsResultType) {
			if(RemarksGradeBean.STATE_PASS.equals(remarksGradeBean.getStudentsResultType())) {
				strBuf.append(" AND pf.isPass = ").append(RemarksGradeBean.STATUS_PASS);
			} else {
				strBuf.append(" AND pf.isPass = ").append(RemarksGradeBean.STATUS_FAIL);
			}
		}
		
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = pf.programSemSubjectId");
		if(isSemPresent) {
			strBuf.append(" AND scm.sem = ?");
		}
		
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.consumerTypeId = ?");
		}
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programStructureId = ?");
		}
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		if(isConsProgStrucProgram) {
			strBuf.append(" AND cps.programId = ?");
		}
		strBuf.append(" JOIN exam.centers cen ON cen.centerCode = s.centerCode");
		if(isInfoCentre) {
			strBuf.append(" AND cen.centerCode = ?");
		}
		strBuf.append(" ORDER BY pf.status, pgm.code ASC");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchResultsAsPassFailReport (countRequired) : ("+ countRequired + ") : Query : "+ sql);
		
		if(countRequired) {
			totalRowCount = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

				@Override
				public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					Integer rowCount = null;
					int index = 1;
					if(isSapIdPresent) {
						arg0.setString(index++, remarksGradeBean.getSapid());
					}
					if(isYear) {
						arg0.setString(index++, remarksGradeBean.getYear());
					}
					if(isMonth) {
						arg0.setString(index++, remarksGradeBean.getMonth());
					}
					if(isSemPresent) {
						arg0.setString(index++, remarksGradeBean.getSem());
					}
					if(isConsProgStrucProgram) {
						arg0.setString(index++, remarksGradeBean.getStudentTypeId());
						arg0.setString(index++, remarksGradeBean.getProgramStructureId());
						arg0.setString(index++, remarksGradeBean.getProgramId());
					}
					if(isInfoCentre) {
						arg0.setString(index++, remarksGradeBean.getCenterCode());
					}

					ResultSet rs = arg0.executeQuery();
					while (rs.next()) {
						rowCount = rs.getInt("totalRows");
					}
					return rowCount;
				}
			});
			logger.info("RemarksGradeDAO : searchResultsAsPassFailReport : Rows found "+ totalRowCount);
			remarksGradeBeanObj = new RemarksGradeBean();
			remarksGradeBeanObj.setStatus(RemarksGradeBean.KEY_SUCCESS);
			remarksGradeBeanObj.setMessage("Total Rows found : "+ totalRowCount);
			list = new ArrayList<RemarksGradeBean>();
			list.add(remarksGradeBeanObj);
		} else {
			list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeBean>>() {
	
				@Override
				public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					int idx = 1;
					if(isSapIdPresent) {
						arg0.setString(idx++, remarksGradeBean.getSapid());
					}
					if(isYear) {
						arg0.setString(idx++, remarksGradeBean.getYear());
					}
					if(isMonth) {
						arg0.setString(idx++, remarksGradeBean.getMonth());
					}
					if(isSemPresent) {
						arg0.setString(idx++, remarksGradeBean.getSem());
					}
					if(isConsProgStrucProgram) {
						arg0.setString(idx++, remarksGradeBean.getStudentTypeId());
						arg0.setString(idx++, remarksGradeBean.getProgramStructureId());
						arg0.setString(idx++, remarksGradeBean.getProgramId());
					}
					if(isInfoCentre) {
						arg0.setString(idx++, remarksGradeBean.getCenterCode());
					}
					
					RemarksGradeBean bean = null;
					List<RemarksGradeBean> listRG = null;
					listRG = new ArrayList<RemarksGradeBean>();
					ResultSet rs = arg0.executeQuery();
					while (rs.next()) {
						bean = new RemarksGradeBean();
						bean.setYear(rs.getString("assignmentYear"));
						bean.setMonth(rs.getString("assignmentMonth"));
						bean.setSapid(rs.getString("sapid"));
						bean.setSubject(rs.getString("subject"));
						bean.setScoreIA(rs.getInt("iaScore"));
						bean.setScoreWritten(rs.getInt("writtenScore"));
						bean.setScoreTotal(rs.getInt("totalScore"));
						bean.setName(rs.getString("name"));
						bean.setSem(rs.getString("sem"));
						bean.setStudentType(rs.getString("consumerTypeName"));
						bean.setProgramStructure(rs.getString("program_structure"));
						bean.setProgram(rs.getString("code"));
						bean.setStatus(rs.getString("status"));
						//bean.setStudentTypeId(rs.getString("consumerTypeId"));
						//bean.setProgramStructureId(rs.getString("programStructureId"));
						//bean.setProgramId(rs.getString("programId"));
						bean.setGrade(rs.getString("grade"));
						bean.setPassStatus(rs.getInt("isPass"));
						//if(rs.getInt("isPass") == 1) {bean.setPass(Boolean.TRUE);} else {bean.setPass(Boolean.FALSE);
						
						bean.setResultLive(rs.getInt("isResultLive"));
						//if(rs.getInt("isResultLive") == 1) {bean.setAssignmentMarksLive(RemarksGradeBean.TEXT_RESULT_LIVE);} else {bean.setAssignmentMarksLive(RemarksGradeBean.TEXT_RESULT_NOT_LIVE);}
						bean.setFailReason(rs.getString("failReason"));
						bean.setRemarks(rs.getString("remarks"));
						bean.setCenterName(rs.getString("centerName"));
						//bean.setActive(rs.getString("active"));
						
						listRG.add(bean);
					}
					return listRG;
				}
			});
			if(null != list && !list.isEmpty()) {
				logger.info("RemarksGradeDAO : searchResultsAsPassFailReport : List Size "+ list.size());
			}
		}
		return list;
	}*/
	
	/*@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeDAO : searchStudentsForAssignments : Assignment Type : "+ remarksGradeBean.getAssignmentType());
		String sql = null;
		final String examYear;
		final String examMonth;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;
		
		paramSource = new MapSqlParameterSource();
		
		strBuf = new StringBuffer();
		
		strBuf.append(" SELECT s.sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" scm.sem, sc.subjectname, ct.name 'consumerTypeName', pst.program_structure, pgm.code 'program', r.year 'acadYear', r.month 'acadmonth',");
		strBuf.append(" scm.id 'programSemSubjectId', rpf.assignmentYear, rpf.assignmentMonth, rpf.remarks, rpf.iaScore, rpf.grade");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" JOIN exam.registration r ON s.sapid = r.sapid");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm");
		strBuf.append(" ON s.consumerProgramStructureId = scm.consumerProgramStructureId AND r.sem = scm.sem AND scm.passScore = ").append(SOFTSKILLS_PASSSCORE).append(" AND scm.hasAssignment = '").append(CHAR_Y).append("'");
		strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure pst ON pst.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND pgm.code in ('").append(SOFTSKILLS_PROGRAM_BBA_BA).append("', '").append(SOFTSKILLS_PROGRAM_BBA).append("', '").append(SOFTSKILLS_PROGRAM_BCOM).append("')");
		strBuf.append(" LEFT JOIN remarkpassfail.passfail rpf on rpf.sapid = s.sapid and rpf.subject = sc.subjectname and rpf.programSemSubjectId = scm.id");
		strBuf.append(" WHERE");
		strBuf.append(" (");
		strBuf.append(" concat(s.sapid, sc.subjectname) IN");
		strBuf.append(" ( SELECT concat(sapid, subject) FROM remarkpassfail.passfail GROUP BY sapid, subject HAVING sum(isPass) in (0, -1) ORDER BY sapid, subject ASC )");
		if(RemarksGradeBean.ASSIGNMENT_NOT_SUBMITTED.equals(remarksGradeBean.getAssignmentType())) {
			strBuf.append(" AND concat(s.sapid, sc.subjectname) NOT IN");
			strBuf.append(" (SELECT concat(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");//exam year+month
		} else if(RemarksGradeBean.ASSIGNMENT_SUBMITTED.equals(remarksGradeBean.getAssignmentType())) {
			strBuf.append(" AND concat(s.sapid, sc.subjectname) IN");
			strBuf.append(" (SELECT concat(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");//exam year+month
		}
		strBuf.append(" )");
		strBuf.append(" OR");
		strBuf.append(" (");
		strBuf.append(" r.year = :acadyear").append(" AND r.month = :acadmonth");//acad cycle - year/month 
		strBuf.append(" AND r.program IN ('").append(SOFTSKILLS_PROGRAM_BBA_BA).append("', '").append(SOFTSKILLS_PROGRAM_BBA).append("', '").append(SOFTSKILLS_PROGRAM_BCOM).append("')"); 
		//strBuf.append(" and (r.sapid, r.consumerProgramStructureId, r.sem)");
		//strBuf.append(" not in (");
		//strBuf.append(" select sapid, scm.consumerProgramStructureId, scm.sem");
		//strBuf.append(" from remarkpassfail.passfail rpf");
		//strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = rpf.programSemSubjectId");
		//strBuf.append(" where rpf.active = 'Y'  AND scm.passScore = 15 and scm.hasAssignment = 'Y') and r.sem < 4");
		strBuf.append(" )");
		strBuf.append(" ORDER BY pgm.id, scm.sem, s.sapid");
		
		examYear = remarksGradeBean.getYear();
		examMonth = remarksGradeBean.getMonth();
		
		paramSource.addValue("eyear", examYear);
		paramSource.addValue("emonth", examMonth);
		paramSource.addValue("acadyear", remarksGradeBean.getAcadYear());
		paramSource.addValue("acadmonth", remarksGradeBean.getAcadMonth());
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentsForAssignments : Query : "+ sql);
		
		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setSapid(rs.getString("sapid"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setSubject(rs.getString("subjectname"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("program"));
					bean.setAcadYear(rs.getString("acadYear"));
					bean.setAcadMonth(rs.getString("acadmonth"));
					
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setRemarks(rs.getString("remarks"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setGrade(rs.getString("grade"));
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentsForAssignments : List Size "+ list.size());
		}
		return list;
	}*/ 
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeDAO : searchStudentsForAssignments : Assignment Type : "+ remarksGradeBean.getAssignmentType());
		String sql = null;
		final String examYear;
		final String examMonth;
		StringBuffer strBuf = null;
		List<RemarksGradeBean> list = null;
		final MapSqlParameterSource paramSource;
		
		paramSource = new MapSqlParameterSource();
		
		strBuf = new StringBuffer();
		
		strBuf.append(" SELECT s.sapid, concat(s.firstName,' ', if( ifnull(s.middleName, '') = '', trim(s.lastName), concat(s.middleName,' ',s.lastName) ) ) name,");
		strBuf.append(" scm.sem, sc.subjectname, ct.name 'consumerTypeName', pst.program_structure, pgm.code 'program', r.year 'acadYear', r.month 'acadmonth',");
		strBuf.append(" scm.id 'programSemSubjectId', rpf.assignmentYear, rpf.assignmentMonth, rpf.remarks, rpf.iaScore, rpf.grade");
		strBuf.append(" FROM ").append(DB_SCHEMA_EXAM).append(".").append(TABLE_STUDENTS).append(" s");
		strBuf.append(" JOIN exam.registration r ON s.sapid = r.sapid");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm");
		strBuf.append(" ON s.consumerProgramStructureId = scm.consumerProgramStructureId AND r.sem = scm.sem AND scm.passScore = ").append(SOFTSKILLS_PASSSCORE).append(" AND scm.hasAssignment = '").append(CHAR_Y).append("'");
		strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = scm.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure pst ON pst.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId AND pgm.code in ('").append(SOFTSKILLS_PROGRAM_BBA_BA).append("', '").append(SOFTSKILLS_PROGRAM_BBA).append("', '").append(SOFTSKILLS_PROGRAM_BCOM).append("')");
		strBuf.append(" LEFT JOIN remarkpassfail.passfail rpf on rpf.sapid = s.sapid and rpf.subject = sc.subjectname and rpf.programSemSubjectId = scm.id");
		if(RemarksGradeBean.ASSIGNMENT_NOT_SUBMITTED.equals(remarksGradeBean.getAssignmentType())) {
			strBuf.append(" AND rpf.isPass in (0, -1) ");
		}
		strBuf.append(" WHERE");
		
		if(RemarksGradeBean.ASSIGNMENT_SUBMITTED.equals(remarksGradeBean.getAssignmentType())) {
			strBuf.append(" CONCAT(s.sapid, sc.subjectname) IN");
			strBuf.append(" ( SELECT concat(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");//exam year+month
		} else if(RemarksGradeBean.ASSIGNMENT_NOT_SUBMITTED.equals(remarksGradeBean.getAssignmentType())) {
			strBuf.append(" (");
			strBuf.append(" (");
			strBuf.append(" CONCAT(r.sapid, r.sem, r.consumerProgramStructureId) IN");
			strBuf.append(" (");
			strBuf.append(" SELECT CONCAT(sapid, sem, consumerProgramStructureId) from exam.registration WHERE program in");
			strBuf.append(" ('").append(SOFTSKILLS_PROGRAM_BBA_BA).append("', '").append(SOFTSKILLS_PROGRAM_BBA).append("', '").append(SOFTSKILLS_PROGRAM_BCOM).append("')");
			strBuf.append(" AND sapid = r.sapid AND sem = r.sem AND consumerProgramStructureId = r.consumerProgramStructureId");
			strBuf.append(" )");
			strBuf.append(" AND");
			strBuf.append(" CONCAT(r.sapid, sc.subjectname) NOT IN");
			strBuf.append(" (");
			strBuf.append(" SELECT CONCAT(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth AND sapid = r.sapid AND subject = sc.subjectname");//exam year+month
			strBuf.append(" UNION");
			strBuf.append(" SELECT CONCAT(sapid, subject) FROM remarkpassfail.passfail WHERE isPass > 0 AND sapid = r.sapid AND subject = sc.subjectname");
			strBuf.append(" )");
			strBuf.append(" ) OR");
			
			strBuf.append(" CONCAT(s.sapid, sc.subjectname) IN");
			strBuf.append(" (");
			strBuf.append(" SELECT CONCAT(sapid, subject) FROM remarkpassfail.passfail");
			strBuf.append(" WHERE CONCAT(sapid, subject) NOT IN ( SELECT CONCAT(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");
			strBuf.append(" GROUP BY sapid, subject HAVING sum(isPass) in (0, -1) ORDER BY sapid, subject ASC");
			strBuf.append(" )");
			strBuf.append(" OR");
			strBuf.append(" CONCAT(s.sapid, sc.subjectname) NOT IN");
			strBuf.append(" ( SELECT CONCAT(sapid, subject) FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");//exam year+month
			strBuf.append(" AND s.sapid NOT IN");
			strBuf.append(" (");
			strBuf.append(" SELECT r1.sapid FROM exam.registration r1 WHERE r1.year = :acadyear AND r1.month = :acadmonth");
			strBuf.append(" AND r1.program IN ('").append(SOFTSKILLS_PROGRAM_BBA_BA).append("', '").append(SOFTSKILLS_PROGRAM_BBA).append("', '").append(SOFTSKILLS_PROGRAM_BCOM).append("')");
			strBuf.append(" AND r1.sapid IN ( SELECT sapid FROM exam.assignmentsubmission WHERE year = :eyear AND month = :emonth )");
			strBuf.append(" AND r1.sapid NOT IN ( SELECT sapid FROM remarkpassfail.passfail WHERE isPass > 0 )");
			strBuf.append(" )");
			strBuf.append(" AND CONCAT(s.sapid, sc.subjectname) NOT IN");
			strBuf.append(" ( SELECT CONCAT(sapid, subject) FROM remarkpassfail.passfail WHERE isPass > 0 )");
			strBuf.append(" )");
			
			// Added by Tushar Supe to remove below program status students from eligibility report
			strBuf.append(" AND s.sapid NOT IN");
			strBuf.append(" (");
			strBuf.append(" SELECT sapid FROM exam.students WHERE programStatus IN ('");
			strBuf.append("Program Suspension");
			strBuf.append("', '");
			strBuf.append("Program Terminated");
			strBuf.append("', '");
			strBuf.append("Program Withdrawal");
			strBuf.append("', '");
			strBuf.append("Program Terminated_");
			strBuf.append("', '");
			strBuf.append("Program Terminated__'");
			strBuf.append(")");
			strBuf.append(" )");
		}
		strBuf.append(" ORDER BY pgm.id, scm.sem, s.sapid");
		
		examYear = remarksGradeBean.getYear();
		examMonth = remarksGradeBean.getMonth();
		
		paramSource.addValue("eyear", examYear);
		paramSource.addValue("emonth", examMonth);
		paramSource.addValue("acadyear", remarksGradeBean.getAcadYear());
		paramSource.addValue("acadmonth", remarksGradeBean.getAcadMonth());
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeDAO : searchStudentsForAssignments : Query : "+ sql);
		
		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeBean>>() {

			@Override
			public List<RemarksGradeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				RemarksGradeBean bean = null;
				List<RemarksGradeBean> listRG = null;
				listRG = new ArrayList<RemarksGradeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeBean();
					bean.setSapid(rs.getString("sapid"));
					bean.setName(rs.getString("name"));
					bean.setSem(rs.getString("sem"));
					bean.setSubject(rs.getString("subjectname"));
					bean.setStudentType(rs.getString("consumerTypeName"));
					bean.setProgramStructure(rs.getString("program_structure"));
					bean.setProgram(rs.getString("program"));
					bean.setAcadYear(rs.getString("acadYear"));
					bean.setAcadMonth(rs.getString("acadmonth"));
					
					bean.setYear(rs.getString("assignmentYear"));
					bean.setMonth(rs.getString("assignmentMonth"));
					bean.setRemarks(rs.getString("remarks"));
					bean.setScoreIA(rs.getInt("iaScore"));
					bean.setGrade(rs.getString("grade"));
					listRG.add(bean);
				}
				return listRG;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("RemarksGradeDAO : searchStudentsForAssignments : List Size "+ list.size());
		}
		return list;
	}
	
	protected static boolean isNotBlank(String arg) {
		return StringUtils.isNotBlank(arg);
	}
	
	protected static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
	
	protected static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
	
	protected static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}

}
