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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.LiveSessionReportAdminBean;

/**
 * @author vil_m
 *
 */
public class LiveSessionReportAdminDAO extends BaseDAO {
	private DataSource dataSource;
	//private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	private static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminDAO.class);
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		//this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void endTransaction(boolean activity) {
		if(activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}

	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	/*@Deprecated
	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			final LiveSessionReportAdminBean liveSessionReportAdminBean, final List<String> centerCodesList,
			final String consumerTypeName) {
		logger.info("Entering LiveSessionReportAdminDAO : fetchLiveSessionReport");

		String centerCodes = null;
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<LiveSessionReportAdminBean> list = null;
		
		year = liveSessionReportAdminBean.getAcadYear();
		month = liveSessionReportAdminBean.getAcadMonth();
		if(null != centerCodesList && !centerCodesList.isEmpty()) {
			centerCodes = listAsString(centerCodesList);
		}
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT ifnull( if(sscm.program_sem_subject_id is null, null, 'LIVE'), if( (reg.program = 'MBA - WX' || reg.program = 'BBA' || reg.program = 'B.Com'), 'LIVE', 'RECORDED')) sessionType");
		strBuf.append(" , reg.sapid, reg.year, reg.month, reg.sem");
		//strBuf.append(" , reg.consumerProgramStructureId, reg.program, studcm.program_sem_subject_id 'studcm PSS'");
		//strBuf.append(" , sscm.program_sem_subject_id 'sscm PSS', cen.centerCode, pgm.code, scm.sem, s.firstName, s.lastName ");
		strBuf.append(" , sc.subjectName, s.emailId, s.mobile, cen.centerName");
		strBuf.append(" , ct.name 'consumerTypeName', ps.program_structure 'programStructure', pgm.name 'programName', CONCAT(s.firstName, ' ', s.lastName) name");
		strBuf.append(" FROM exam.registration reg");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm");
		strBuf.append(" ON scm.sem = reg.sem AND scm.consumerProgramStructureId = reg.consumerProgramStructureId");
		strBuf.append(" JOIN exam.mdm_subjectcode sc");
		strBuf.append(" ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN exam.students s");
		strBuf.append(" ON s.sapid = reg.sapid");
		strBuf.append(" JOIN exam.centers cen");
		strBuf.append(" ON cen.centerCode = s.centerCode");//.append("AND cen.lc = 'Mumbai'");
		if(null != centerCodes) {
			strBuf.append(" AND cen.centerCode IN (").append(centerCodes).append(")");
		}
		strBuf.append(" LEFT JOIN exam.student_course_mapping studcm");
		strBuf.append(" ON studcm.acadYear = reg.year AND studcm.acadMonth = reg.month AND studcm.program_sem_subject_id = scm.id AND studcm.userId = reg.sapid");
		strBuf.append(" LEFT JOIN exam.student_session_courses_mapping sscm");
		strBuf.append(" ON sscm.acadYear = reg.year AND sscm.acadMonth = reg.month AND sscm.program_sem_subject_id = scm.id AND sscm.userId = reg.sapid");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = reg.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" WHERE reg.year = ? AND reg.month = ?");
		strBuf.append(" AND ct.name = ?");
		strBuf.append(" ORDER BY reg.sapid, sc.subjectname");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		logger.info("LiveSessionReportAdminDAO : fetchLiveSessionReport : Query : "+ sql);
		
		list = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<LiveSessionReportAdminBean>>() {

			@Override
			public List<LiveSessionReportAdminBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				arg0.setString(1, year);
				arg0.setString(2, month);
				arg0.setString(3, consumerTypeName);
				
				LiveSessionReportAdminBean bean = null;
				List<LiveSessionReportAdminBean> listR = null;
				listR = new ArrayList<LiveSessionReportAdminBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new LiveSessionReportAdminBean();
					bean.setSapId(rs.getString("sapid"));
					bean.setAcadYear(rs.getString("year"));
					bean.setAcadMonth(rs.getString("month"));
					bean.setSem(rs.getString("sem"));
					bean.setSubjectName(rs.getString("subjectName"));
					bean.setEmailId(rs.getString("emailId"));
					bean.setPhone(rs.getString("mobile"));
					bean.setCenterName(rs.getString("centerName"));
					bean.setConsumerTypeName(rs.getString("consumerTypeName"));
					bean.setProgramStructureName(rs.getString("programStructure"));
					bean.setProgramName(rs.getString("programName"));
					bean.setStudentName(rs.getString("name"));
					bean.setSessionType(rs.getString("sessionType"));
					
					listR.add(bean);
				}
				return listR;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("LiveSessionReportAdminDAO : fetchLiveSessionReport : List Size : "+ list.size());
		}
		return list;
	}*/
	@Transactional(readOnly = true)
	public List<LiveSessionReportAdminBean> fetchLiveSessionReport(
			final LiveSessionReportAdminBean liveSessionReportAdminBean, final List<String> centerCodesList,
			final String consumerTypeName) {
		logger.info("Entering LiveSessionReportAdminDAO : fetchLiveSessionReport");

		Boolean centerCodes = Boolean.FALSE;
		final String year;
		final String month;
		String sql = null;
		StringBuffer strBuf = null;
		List<LiveSessionReportAdminBean> list = null;
		final MapSqlParameterSource paramSource;
		
		year = liveSessionReportAdminBean.getAcadYear();
		month = liveSessionReportAdminBean.getAcadMonth();
		if(null != centerCodesList && !centerCodesList.isEmpty()) {
			centerCodes = Boolean.TRUE; //listAsString(centerCodesList);
		}
		
		strBuf = new StringBuffer();
		strBuf.append("SELECT ifnull( if(sscm.program_sem_subject_id is null, null, 'LIVE'), if( (reg.program = 'MBA - WX' || reg.program = 'BBA' || reg.program = 'B.Com'), 'LIVE', 'RECORDED')) sessionType");
		strBuf.append(" , reg.sapid, reg.year, reg.month, reg.sem");
		//strBuf.append(" , reg.consumerProgramStructureId, reg.program, studcm.program_sem_subject_id 'studcm PSS'");
		//strBuf.append(" , sscm.program_sem_subject_id 'sscm PSS', cen.centerCode, pgm.code, scm.sem, s.firstName, s.lastName ");
		strBuf.append(" , sc.subjectName, s.emailId, s.mobile, cen.centerName");
		strBuf.append(" , ct.name 'consumerTypeName', ps.program_structure 'programStructure', pgm.name 'programName', CONCAT(s.firstName, ' ', s.lastName) name");
		strBuf.append(" FROM exam.registration reg");
		strBuf.append(" JOIN exam.mdm_subjectcode_mapping scm");
		strBuf.append(" ON scm.sem = reg.sem AND scm.consumerProgramStructureId = reg.consumerProgramStructureId");
		strBuf.append(" JOIN exam.mdm_subjectcode sc");
		strBuf.append(" ON sc.id = scm.subjectCodeId");
		strBuf.append(" JOIN exam.students s");
		strBuf.append(" ON s.sapid = reg.sapid");
		strBuf.append(" JOIN exam.centers cen");
		strBuf.append(" ON cen.centerCode = s.centerCode");//.append("AND cen.lc = 'Mumbai'");
		if(centerCodes) {
			strBuf.append(" AND cen.centerCode IN (:centerCodes)");
		}
		strBuf.append(" LEFT JOIN exam.student_course_mapping studcm");
		strBuf.append(" ON studcm.acadYear = reg.year AND studcm.acadMonth = reg.month AND studcm.program_sem_subject_id = scm.id AND studcm.userId = reg.sapid");
		strBuf.append(" LEFT JOIN exam.student_session_courses_mapping sscm");
		strBuf.append(" ON sscm.acadYear = reg.year AND sscm.acadMonth = reg.month AND sscm.program_sem_subject_id = scm.id AND sscm.userId = reg.sapid");
		strBuf.append(" JOIN exam.consumer_program_structure cps ON cps.id = reg.consumerProgramStructureId");
		strBuf.append(" JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId");
		strBuf.append(" JOIN exam.program_structure ps ON ps.id = cps.programStructureId");
		strBuf.append(" JOIN exam.program pgm ON pgm.id = cps.programId");
		strBuf.append(" WHERE reg.year = :year AND reg.month = :month");
		strBuf.append(" AND ct.name = :consumerTypeName");
		strBuf.append(" ORDER BY reg.sapid, sc.subjectname");
		
		sql = strBuf.toString();
		emptyStringBuffer(strBuf);
		
		//logger.info("LiveSessionReportAdminDAO : fetchLiveSessionReport : centerCodesList : "+ centerCodesList);
		logger.info("LiveSessionReportAdminDAO : fetchLiveSessionReport : Query : "+ sql);
		
		paramSource = new MapSqlParameterSource();
		if(null != centerCodes) {
			paramSource.addValue("centerCodes", centerCodesList);//single quote removed comma seperated centrecodes in list
		}
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		paramSource.addValue("consumerTypeName", consumerTypeName);
		
		list = namedParameterJdbcTemplate.execute(sql, paramSource, new PreparedStatementCallback<List<LiveSessionReportAdminBean>>() {

			@Override
			public List<LiveSessionReportAdminBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				LiveSessionReportAdminBean bean = null;
				List<LiveSessionReportAdminBean> listR = null;
				listR = new ArrayList<LiveSessionReportAdminBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new LiveSessionReportAdminBean();
					bean.setSapId(rs.getString("sapid"));
					bean.setAcadYear(rs.getString("year"));
					bean.setAcadMonth(rs.getString("month"));
					bean.setSem(rs.getString("sem"));
					bean.setSubjectName(rs.getString("subjectName"));
					bean.setEmailId(rs.getString("emailId"));
					bean.setPhone(rs.getString("mobile"));
					bean.setCenterName(rs.getString("centerName"));
					bean.setConsumerTypeName(rs.getString("consumerTypeName"));
					bean.setProgramStructureName(rs.getString("programStructure"));
					bean.setProgramName(rs.getString("programName"));
					bean.setStudentName(rs.getString("name"));
					bean.setSessionType(rs.getString("sessionType"));
					
					listR.add(bean);
				}
				return listR;
			}
		});
		if(null != list && !list.isEmpty()) {
			logger.info("LiveSessionReportAdminDAO : fetchLiveSessionReport : List Size : "+ list.size());
		}
		return list;
	}
	
	/*public static String listAsString(final List<String> list) {
		String str = null;
		str = list.toString();
		str = str.substring(1, str.length()-1);
		return str;
	}*/
	
	public static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
}
