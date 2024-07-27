package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;

@Component
public class StudentDAO extends BaseDAO {

	@Autowired
	ApplicationContext act;
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingAcadsBean> getAllApplicableSubjectsForStudent(StudentAcadsBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `sem`, `subject` "
			+ " FROM `exam`.`program_sem_subject` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `sem` < ( SELECT MAX(`sem`) FROM `exam`.`registration` WHERE `sapid` = ? ) "
			+ " AND `active` = 'Y' ";
		return jdbcTemplate.query(
			sql,
			new Object[] {	student.getConsumerProgramStructureId(), student.getSapid() },
			new BeanPropertyRowMapper<ProgramSubjectMappingAcadsBean>(ProgramSubjectMappingAcadsBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfStudentIsLateral(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`students` `s1` "
			+ " WHERE `sapid` = ? AND `isLateral` = 'Y' "
			+ " AND `sem` = ( SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s1`.`sapid` ) " ;
		try {
			int count = jdbcTemplate.queryForObject(
				sql,
				new Object[] { sapid },
				Integer.class
			);
					
			return count > 0;
		} catch (Exception e) {
			  
			return false;
		}
	}

	@Transactional(readOnly = true)
	public String getPreviousStudentNumber(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `previousStudentId` "
			+ " FROM `exam`.`students` `s1` "
			+ " WHERE `sapid` = ? AND `isLateral` = 'Y' "
			+ " AND `sem` = ( SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s1`.`sapid` ) " ;
		try {
			return jdbcTemplate.queryForObject(
				sql,
				new Object[] { sapid },
				String.class
			);
		} catch (Exception e) {
			  
			return null;
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}
	
	@Transactional(readOnly = true)
	public List<Integer> fetchPSSforLiveSessionAccess(final String sapId) {
		logger.info("Entering StudentDAO : fetchPSSforLiveSessionAccess");
		String sql = null;
		StringBuffer strBuf = null;
		List<Integer> pssIdList = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			strBuf = new StringBuffer();
			//Base Query
			strBuf.append("SELECT sscm.program_sem_subject_id");
			strBuf.append(" FROM exam.student_session_courses_mapping sscm JOIN exam.registration reg ON sscm.userId = reg.sapid");
			strBuf.append(" AND reg.year = sscm.acadYear AND reg.month = sscm.acadMonth WHERE sscm.userId = ?");
			strBuf.append(" AND sem = (SELECT max(sem) FROM exam.registration WHERE sapid = ?)");
			
			/*strBuf.append("SELECT sscm.program_sem_subject_id");
			strBuf.append(" FROM exam.student_session_courses_mapping sscm");
			strBuf.append(" WHERE sscm.acadYear = ? AND sscm.acadMonth = ? AND sscm.userId = ?");*/
			
			sql = strBuf.toString();
			logger.info("StudentDAO : fetchPSSforLiveSessionAccess : (SapId) : ("+ sapId +")");
			logger.info("StudentDAO : fetchPSSforLiveSessionAccess : Query : "+ sql);
			
			pssIdList = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<Integer>>() {
	
				@Override
				public List<Integer> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					int idx = 1;
					arg0.setString(idx++, sapId);
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
				logger.info("StudentDAO : fetchPSSforLiveSessionAccess : List Size : "+ pssIdList.size());
			}
		
		} catch(DataAccessException de) {
			logger.error("StudentDAO : fetchPSSforLiveSessionAccess : " + de.getMessage());
			throw de;
		} catch(Exception e) {
			logger.error("StudentDAO : fetchPSSforLiveSessionAccess : " + e.getMessage());
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
	public ArrayList<String> getSem1and2PassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? and sem in(1,2) order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}
	
	@Transactional(readOnly = true)
 	public ArrayList<String> getNon_pgProgramList(List<String> timeboundPortalList) {
 		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select counsumer_program_structure_id from exam.live_programs_mapping where hasPaidSessionApplicable=0 and counsumer_program_structure_id not in (:masterkeysList)";
		ArrayList<String> data =new ArrayList<String>();
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("masterkeysList", timeboundPortalList);
 		data = (ArrayList<String>) namedParameterJdbcTemplate.query(sql,queryParams,new SingleColumnRowMapper<>(String.class));
 		return data;
 	}

}
