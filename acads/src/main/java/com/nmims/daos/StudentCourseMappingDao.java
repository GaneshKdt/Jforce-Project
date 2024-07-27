package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConsumerProgramStructureAcads;

@Repository("studentCourseMappingDao")
public class StudentCourseMappingDao 
{
	final String[] electiveMasterkeys = {"128"};
	
	final String[] electiveSem = {"5","6"};
	
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPSSIds(String sapid,String month,String year) {
	
		String sql =" SELECT programSemSubjectId FROM exam.student_current_subject  WHERE sapid =:sapid and year =:year and month =:month ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);

		ArrayList<String> pssIdList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getCurrentCycleSubjects(String sapid,String month,String year) {
		
		String sql =" SELECT subject FROM exam.student_current_subject WHERE sapid =:sapid and year =:year and month =:month ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);

		ArrayList<String> pssIdList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFailSubjectsNamesForAStudent(String sapid) {
		
		String sql = "select subject from exam.passfail where isPass = 'N' and sapid =:sapid order by sem  asc ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);

		ArrayList<String> subjectsList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getNotPassedSubjectsBasedOnSapid(String sapid,String sem,String masterkey){
		
		StringBuffer sql = new StringBuffer("select ps.subject from exam.registration er,exam.program_subject ps, exam.students s "
				+" where er.sapid =:sapid " 
				+" and s.sapid = er.sapid "
				+" and er.program = ps.program "
				+" and er.sem = ps.sem "
				+" and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+" and ps.subject not in (select subject from exam.passfail where sapid =:sapid )");
		
		if(Arrays.asList(electiveMasterkeys).contains(masterkey) && Arrays.asList(electiveSem).contains(sem)) //For Elective Masterkeys
			sql.append(" and er.sem < :sem  ");
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("sem", sem);
		
		ArrayList<String> notPassedSubjectsList = (ArrayList<String>) namedParameterJdbcTemplate.query(sql.toString(), queryParams, 
				new SingleColumnRowMapper(String.class));
		return notPassedSubjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getPSSId(String sapid,String month,String year) {
	
		String sql =" SELECT programSemSubjectId FROM exam.student_current_subject  WHERE sapid =:sapid and year =:year and month =:month ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);

		ArrayList<Integer> pssIdList = (ArrayList<Integer>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(Integer.class));
		return pssIdList;
	}
}
