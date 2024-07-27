package com.nmims.daos;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.SessionPlanModulePg;
import com.nmims.beans.SessionPlanPgBean;

@Repository("sessionPlanPgDao")
public class SessionPlanPgDao extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;		
	}
	
	@Transactional(readOnly = true)
	public int getSubjectCodeIdByPssId(String programSemSubjectId) throws Exception{
		String sql = "select subjectCodeId from exam.mdm_subjectcode_mapping where id  = :programSemSubjectId";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();

		queryParams.addValue("programSemSubjectId", programSemSubjectId);

		return namedParameterJdbcTemplate.queryForObject(sql, queryParams, new SingleColumnRowMapper<>(Integer.class));
	}
	
	@Transactional(readOnly = true)
	public SessionPlanPgBean getSessionPlanDetails(int subjectCodeId) throws Exception{
		String sql = "SELECT * FROM acads.sessionplan_pg where subjectCodeId = :subjectCodeId";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();

		queryParams.addValue("subjectCodeId", subjectCodeId);

		return namedParameterJdbcTemplate.queryForObject(sql, queryParams, new BeanPropertyRowMapper<>(SessionPlanPgBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<SessionPlanModulePg> getSessionPlanModuleDetails(int sessionPlanId) throws Exception{
		String sql = "SELECT * FROM acads.sessionplan_module_pg where sessionPlanId = :sessionPlanId";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();

		queryParams.addValue("sessionPlanId", sessionPlanId);

		return namedParameterJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<>(SessionPlanModulePg.class));
	}
	
	@Transactional(readOnly = true)
	public List<SessionPlanModulePg> getAttemptedQuizForStudent(String sapid) throws Exception{
		String sql = "SELECT testId,score as quizScore,attemptStatus FROM internal_assessment.test_student_testdetails where sapid= :sapid ";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();

		queryParams.addValue("sapid", sapid);

		return namedParameterJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<>(SessionPlanModulePg.class));
	}
	
	@Transactional(readOnly = true)
	public List<SessionPlanModulePg> getListOfQuizesForModules(List<Integer> moduleIdList) throws Exception{
		String sql = "SELECT id as testId, referenceId as testReferenceId FROM internal_assessment.test where referenceId IN (:moduleIdList) AND testType ='Quiz' ";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();

		queryParams.addValue("moduleIdList", moduleIdList);

		return namedParameterJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<>(SessionPlanModulePg.class));
	}

}
