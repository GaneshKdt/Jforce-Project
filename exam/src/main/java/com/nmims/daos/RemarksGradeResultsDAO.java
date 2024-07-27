/**
 * 
 */
package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.RemarksGradeResultsBean;

/**
 * @author vil_m
 *
 */
public class RemarksGradeResultsDAO extends BaseDAO {

	private DataSource dataSource;
	//private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;

	public static final String DB_SCHEMA_REMARKPF = "remarkpassfail";
	public static final String TABLE_MARKS = "marks";
	public static final String TABLE_MARKS_HISTORY = "marks_history";
	public static final String TABLE_STAGING_PF = "staging_passfail";
	public static final String TABLE_PF = "passfail";

	private static final Logger logger = LoggerFactory.getLogger(RemarksGradeResultsDAO.class);

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		//this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	public boolean checkResultsAvailable(final String sapid, final Integer resultLive,
			final String status, final Integer statusPassFail, final Integer processedFlag, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : checkResultsAvailable");
		Boolean areResultsAvailable = Boolean.FALSE;
		Integer count1 = 0;
		//Integer count2 = 0;
		Integer count3 = 0;
		
		logger.info(
				"RemarksGradeResultsDAO : checkResultsAvailable (sapid, resultLive, status, statusPassFail, processedFlag, activeFlag) ("
						+ sapid + ", " + resultLive + ", " + status + ", " + statusPassFail + ", " + processedFlag
						+ ", " + activeFlag + ")");
		
		count1 = fetchCountInMarks(sapid, resultLive, status, processedFlag, activeFlag);
		//count2 = fetchCountInTable((DB_SCHEMA_REMARKPF+"."+TABLE_STAGING_PF), remarksGradeResultsBean, resultLive, status, statusPassFail, activeFlag);
		count3 = fetchCountInTable((DB_SCHEMA_REMARKPF+"."+TABLE_PF), sapid, resultLive, status, statusPassFail, activeFlag);
		
		if(count1 > 0 && count3 > 0) {
			areResultsAvailable = Boolean.TRUE;
		}
		logger.info(
				"RemarksGradeResultsDAO : checkResultsAvailable (areResultsAvailable) (" + areResultsAvailable + ")");
		return areResultsAvailable;
	}
	
	/*@Deprecated
	protected Integer fetchCountInMarks(final String sapid, final Integer resultLive,
			final String status, final Integer processedFlag, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchCountInMarks");
		String sql = null;
		StringBuffer strBuf = null;
		Integer rowCount = 0;

		strBuf = new StringBuffer();
		strBuf.append("SELECT count(m.id) countId");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" WHERE m.isResultLive = ").append(resultLive);
		//strBuf.append(" AND m.status = '").append(status).append("'");// temporary comment for dec2020 processing
		strBuf.append(" AND m.active = '").append(activeFlag).append("'");
		strBuf.append(" AND m.processed = ").append(processedFlag);
		//strBuf.append(" AND m.totalScore is not null");// temporary comment for dec2020 processing
		strBuf.append(" AND m.sapid =  ?");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchCountInMarks : Query : " + sql);

		rowCount = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				arg0.setString(1, sapid);

				Integer ct = 0;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					ct = rs.getInt("countId");
				}
				return ct;
			}
		});
		logger.info("RemarksGradeResultsDAO : fetchCountInMarks : Row Count : " + rowCount);
		return rowCount;
	}*/
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	protected Integer fetchCountInMarks(final String sapid, final Integer resultLive,
			final String status, final Integer processedFlag, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchCountInMarks");
		String sql = null;
		StringBuffer strBuf = null;
		Integer rowCount = 0;
		final MapSqlParameterSource paramSource;

		strBuf = new StringBuffer();
		strBuf.append("SELECT count(m.id) countId");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m");
		strBuf.append(" WHERE m.isResultLive = :resultLive");
		//strBuf.append(" AND m.status = '").append(status).append("'");// temporary comment for dec2020 processing
		strBuf.append(" AND m.active = :activeFlag");
		strBuf.append(" AND m.processed = :processedFlag");
		//strBuf.append(" AND m.totalScore is not null");// temporary comment for dec2020 processing
		strBuf.append(" AND m.sapid = :sapid");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchCountInMarks : Query : " + sql);
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("resultLive", resultLive);
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("processedFlag", processedFlag);
		paramSource.addValue("sapid", sapid);

		rowCount = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub

				Integer ct = 0;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					ct = rs.getInt("countId");
				}
				return ct;
			}
		});
		logger.info("RemarksGradeResultsDAO : fetchCountInMarks : Row Count : " + rowCount);
		return rowCount;
	}
	
	/*@Deprecated
	protected Integer fetchCountInTable(String tableName, final String sapid,
			final Integer resultLive, final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ")");
		String sql = null;
		StringBuffer strBuf = null;
		Integer rowCount = 0;

		strBuf = new StringBuffer();
		strBuf.append("SELECT count(t.id) countId");
		strBuf.append(" FROM ").append(tableName).append(" t");
		strBuf.append(" WHERE t.isResultLive = ").append(resultLive);
		//strBuf.append(" AND t.status = '").append(status).append("'");// temporary comment for dec2020 processing
		//strBuf.append(" AND t.isPass <> ").append(statusPassFail);// temporary comment for dec2020 processing
		strBuf.append(" AND t.active = '").append(activeFlag).append("'");
		//strBuf.append(" AND t.totalScore is not null");// temporary comment for dec2020 processing
		strBuf.append(" AND t.sapid =  ?");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ") : Query : " + sql);

		rowCount = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				arg0.setString(1, sapid);

				Integer ct = 0;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					ct = rs.getInt("countId");
				}
				return ct;
			}
		});
		logger.info("RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ") : Row Count : " + rowCount);
		return rowCount;
	}*/
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	protected Integer fetchCountInTable(String tableName, final String sapid,
			final Integer resultLive, final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ")");
		String sql = null;
		StringBuffer strBuf = null;
		Integer rowCount = 0;
		final MapSqlParameterSource paramSource;

		strBuf = new StringBuffer();
		strBuf.append("SELECT count(t.id) countId");
		strBuf.append(" FROM ").append(tableName).append(" t");
		strBuf.append(" WHERE t.isResultLive = :resultLive");
		//strBuf.append(" AND t.status = '").append(status).append("'");// temporary comment for dec2020 processing
		//strBuf.append(" AND t.isPass <> ").append(statusPassFail);// temporary comment for dec2020 processing
		strBuf.append(" AND t.active = :activeFlag");
		//strBuf.append(" AND t.totalScore is not null");// temporary comment for dec2020 processing
		strBuf.append(" AND t.sapid = :sapid");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ") : Query : " + sql);
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("resultLive", resultLive);
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("sapid", sapid);

		rowCount = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub

				Integer ct = 0;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					ct = rs.getInt("countId");
				}
				return ct;
			}
		});
		logger.info("RemarksGradeResultsDAO : fetchCountInTable (" + tableName + ") : Row Count : " + rowCount);
		return rowCount;
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
	public List<RemarksGradeResultsBean> fetchStudentsResult(final String sapid,
			final Integer resultLive, final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchStudentsResult");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeResultsBean> list = null;
		final MapSqlParameterSource paramSource;

		strBuf = new StringBuffer();
		strBuf.append("SELECT scm.sem, sc.subjectname, pf.sapid, pf.grade, pf.failReason, pf.remarks, IFNULL(pf.totalScore,'-') totalScore");
		strBuf.append(", pf.assignmentYear, pf.assignmentMonth");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF).append(" pf");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = pf.programSemSubjectId");
		strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
		strBuf.append(" AND pf.isResultLive = :resultLive");
		//strBuf.append(" AND pf.status = '").append(status).append("'");// temporary comment for dec2020 processing
		//strBuf.append(" AND pf.isPass <> ").append(statusPassFail);//  temporary comment for dec2020 processing
		strBuf.append(" AND pf.active = :activeFlag");
		//strBuf.append(" AND pf.totalScore is not null");// temporary comment for dec2020 processing
		// strBuf.append(" AND spf.assignmentYear = ?").append(" AND spf.assignmentMonth
		// = ?");
		strBuf.append(" WHERE pf.sapid = :sapid");
		strBuf.append(" ORDER BY scm.sem, pf.assignmentYear ASC");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchStudentsResult : Query : " + sql);
		
		paramSource = new MapSqlParameterSource();
		paramSource.addValue("resultLive", resultLive);
		paramSource.addValue("activeFlag", activeFlag);
		paramSource.addValue("sapid", sapid);

		list = this.namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<RemarksGradeResultsBean>>() {

			@Override
			public List<RemarksGradeResultsBean> doInPreparedStatement(PreparedStatement arg0)
					throws SQLException, DataAccessException {
				// TODO Auto-generated method stub

				RemarksGradeResultsBean bean = null;
				List<RemarksGradeResultsBean> listRG = null;
				listRG = new ArrayList<RemarksGradeResultsBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeResultsBean(rs.getString("sem"), rs.getString("subjectname"),
							rs.getString("grade"), rs.getString("assignmentYear"), rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setFailReason(rs.getString("failReason"));
					bean.setRemarks(rs.getString("remarks"));
					bean.setScoreTotal(rs.getString("totalScore"));

					listRG.add(bean);
				}
				return listRG;
			}
		});
		if (null != list && !list.isEmpty()) {
			logger.info("RemarksGradeResultsDAO : fetchStudentsResult : List Size : " + list.size());
		}
		return list;
	}
	
	/*@Deprecated
	public List<RemarksGradeResultsBean> fetchStudentsResult(final String sapid,
			final Integer resultLive, final String status, final Integer statusPassFail, final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchStudentsResult");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeResultsBean> list = null;

		strBuf = new StringBuffer();
		strBuf.append("SELECT scm.sem, sc.subjectname, pf.sapid, pf.grade, pf.failReason, pf.remarks, IFNULL(pf.totalScore,'-') totalScore");
		strBuf.append(", pf.assignmentYear, pf.assignmentMonth");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF).append(" pf");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = pf.programSemSubjectId");
		strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
		strBuf.append(" AND pf.isResultLive = ").append(resultLive);
		//strBuf.append(" AND pf.status = '").append(status).append("'");// temporary comment for dec2020 processing
		//strBuf.append(" AND pf.isPass <> ").append(statusPassFail);//  temporary comment for dec2020 processing
		strBuf.append(" AND pf.active = '").append(activeFlag).append("'");
		//strBuf.append(" AND pf.totalScore is not null");// temporary comment for dec2020 processing
		// strBuf.append(" AND spf.assignmentYear = ?").append(" AND spf.assignmentMonth
		// = ?");
		strBuf.append(" WHERE pf.sapid =  ?");
		strBuf.append(" ORDER BY scm.sem, pf.assignmentYear ASC");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchStudentsResult : Query : " + sql);

		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeResultsBean>>() {

			@Override
			public List<RemarksGradeResultsBean> doInPreparedStatement(PreparedStatement arg0)
					throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx++, sapid);

				RemarksGradeResultsBean bean = null;
				List<RemarksGradeResultsBean> listRG = null;
				listRG = new ArrayList<RemarksGradeResultsBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeResultsBean(rs.getString("sem"), rs.getString("subjectname"),
							rs.getString("grade"), rs.getString("assignmentYear"), rs.getString("assignmentMonth"));
					bean.setSapid(rs.getString("sapid"));
					bean.setFailReason(rs.getString("failReason"));
					bean.setRemarks(rs.getString("remarks"));
					bean.setScoreTotal(rs.getString("totalScore"));

					listRG.add(bean);
				}
				return listRG;
			}
		});
		if (null != list && !list.isEmpty()) {
			logger.info("RemarksGradeResultsDAO : fetchStudentsResult : List Size : " + list.size());
		}
		return list;
	}*/

	/*public List<RemarksGradeResultsBean> fetchStudentsResult(final RemarksGradeResultsBean remarksGradeResultsBean,
			final Integer resultLive, final String status, final Integer statusPassFail, final Integer processedFlag,
			final String activeFlag) {
		logger.info("Entering RemarksGradeResultsDAO : fetchStudentsResult");
		String sql = null;
		StringBuffer strBuf = null;
		List<RemarksGradeResultsBean> list = null;

		strBuf = new StringBuffer();
		strBuf.append("SELECT scm.sem, sc.subjectname, pf.sapid, pf.grade");
		strBuf.append(" FROM ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_PF).append(" pf");
		strBuf.append(" JOIN exam.students s ON s.sapid = pf.sapid");
		strBuf.append(" AND s.sem = (SELECT MAX(sem) FROM exam.students s1 WHERE s1.sapid = s.sapid)");
		strBuf.append(" AND pf.isResultLive = ").append(resultLive);
		strBuf.append(" AND pf.status = '").append(status).append("'");
		strBuf.append(" AND pf.isPass <> ").append(statusPassFail);
		strBuf.append(" AND pf.active = '").append(activeFlag).append("'");
		strBuf.append(" AND pf.totalScore is not null");
		// strBuf.append(" AND spf.assignmentYear = ?").append(" AND spf.assignmentMonth
		// = ?");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm ON scm.id = pf.programSemSubjectId");
		strBuf.append(" JOIN exam.mdm_subjectcode sc ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId");
		strBuf.append(" AND scm.consumerProgramStructureId = s.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" JOIN ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_MARKS).append(" m on m.sapid = s.sapid");
		strBuf.append(" AND m.isResultLive = ").append(resultLive);
		strBuf.append(" AND m.status = '").append(status).append("'");
		strBuf.append(" AND m.active = '").append(activeFlag).append("'");
		strBuf.append(" AND m.processed = ").append(processedFlag);
		strBuf.append(" AND m.totalScore is not null");
		strBuf.append(" LEFT JOIN ").append(DB_SCHEMA_REMARKPF).append(".").append(TABLE_STAGING_PF).append(" spf ON spf.sapid = s.sapid");
		strBuf.append(" AND spf.isResultLive = ").append(resultLive);
		strBuf.append(" AND spf.status = '").append(status).append("'");
		strBuf.append(" AND spf.isPass <> ").append(statusPassFail);
		strBuf.append(" AND spf.active = '").append(activeFlag).append("'");
		strBuf.append(" AND spf.totalScore is not null");
		strBuf.append(" WHERE pf.sapid =  ?");

		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("RemarksGradeResultsDAO : fetchStudentsResult : Query : " + sql);

		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<RemarksGradeResultsBean>>() {

			@Override
			public List<RemarksGradeResultsBean> doInPreparedStatement(PreparedStatement arg0)
					throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx++, remarksGradeResultsBean.getSapid());

				RemarksGradeResultsBean bean = null;
				List<RemarksGradeResultsBean> listRG = null;
				listRG = new ArrayList<RemarksGradeResultsBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new RemarksGradeResultsBean(rs.getString("sem"), rs.getString("subjectname"),
							rs.getString("grade"));
					bean.setSapid(rs.getString("sapid"));

					listRG.add(bean);
				}
				return listRG;
			}
		});
		if (null != list && !list.isEmpty()) {
			logger.info("RemarksGradeResultsDAO : fetchStudentsResult : List Size : " + list.size());
		}
		return list;
	}*/

	protected static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}

	protected static void emptyStringBuffer(StringBuffer strBuf) {
		if (null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}

	protected static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
}
