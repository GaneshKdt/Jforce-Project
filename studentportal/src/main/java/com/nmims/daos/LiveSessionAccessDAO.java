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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author vil_m
 *
 */
public class LiveSessionAccessDAO extends BaseDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(LiveSessionAccessDAO.class);
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
		// super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		// TODO Auto-generated method stub
		this.baseDataSource = this.dataSource;
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
	
	@Transactional(readOnly = true)
	public List<Integer> fetchPSSforLiveSessionAccess(final String sapId, final String year, final String month) {
		logger.info("Entering LiveSessionAccessDAO : fetchPSSforLiveSessionAccess");
		String sql = null;
		StringBuffer strBuf = null;
		List<Integer> pssIdList = null;
		
		try {
			strBuf = new StringBuffer();
			/*Base Query
			strBuf.append("SELECT sscm.program_sem_subject_id");
			strBuf.append(" FROM exam.student_session_courses_mapping sscm JOIN exam.registration reg ON sscm.userId = reg.sapid");
			strBuf.append(" AND reg.year = sscm.acadYear AND reg.month = sscm.acadMonth WHERE sscm.userId = ?");
			strBuf.append(" AND sem = (SELECT max(sem) FROM exam.registration WHERE sapid = ?)");*/
			
			strBuf.append("SELECT sscm.program_sem_subject_id");
			strBuf.append(" FROM exam.student_session_courses_mapping sscm");
			strBuf.append(" WHERE sscm.acadYear = ? AND sscm.acadMonth = ? AND sscm.userId = ?");
			
			sql = strBuf.toString();
			logger.info("LiveSessionAccessDAO : fetchPSSforLiveSessionAccess : (SapId, Year, Month) : ("+ sapId + "," + year +  "," + month + ")");
			logger.info("LiveSessionAccessDAO : fetchPSSforLiveSessionAccess : Query : "+ sql);
			
			pssIdList = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<Integer>>() {
	
				@Override
				public List<Integer> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					int idx = 1;
					arg0.setString(idx++, year);
					arg0.setString(idx++, month);
					arg0.setString(idx++, sapId);
					
					List<Integer> list = null;
					list = new ArrayList<Integer>();
					ResultSet rs = arg0.executeQuery();
					while (rs.next()) {
						list.add(rs.getInt("program_sem_subject_id"));
					}
					return list;
				}
			});
			if(null != pssIdList) {
				logger.info("LiveSessionAccessDAO : fetchPSSforLiveSessionAccess : List Size : "+ pssIdList.size());
			}
		
		} catch(DataAccessException de) {
			logger.error("LiveSessionAccessDAO : fetchPSSforLiveSessionAccess : " + de.getMessage());
			throw de;
		} catch(Exception e) {
			logger.error("LiveSessionAccessDAO : fetchPSSforLiveSessionAccess : " + e.getMessage());
			throw e;
		} finally {
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return pssIdList;
	}
	
	public static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
	 
 	@Transactional(readOnly = true)
 	public ArrayList<String> getNonPgProgramsList(List<String> timeboundPortalList) {
 		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select counsumer_program_structure_id from exam.live_programs_mapping where hasPaidSessionApplicable=0 and counsumer_program_structure_id not in (:masterkeysList)";
		ArrayList<String> data =new ArrayList<String>();
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("masterkeysList", timeboundPortalList);
 		data = (ArrayList<String>) namedParameterJdbcTemplate.query(sql,queryParams,new SingleColumnRowMapper<>(String.class));
 		return data;
 	}

}
