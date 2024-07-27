package com.nmims.daos;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.CustomCourseWaiverDTO;
import com.nmims.repository.CustomCourseWaiverRepository;

@Repository
public class CustomCourseWaiverDAO implements CustomCourseWaiverRepository{

	@Autowired
	DataSource dataSource;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate nameParamerterJdbcTemplate;
	
	@Override
	public List<Integer> getWaivedInPss(String sapid,int sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mdm_pss_id FROM exam.custom_waived_in_subjects where sapid = ?  and sem = ?" ; 
		
		return jdbcTemplate.queryForList(sql,new Object[] {sapid,sem},Integer.class);
	}
	
	@Override
	public List<Integer> getWaivedOffPss(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mdm_pss_id FROM exam.waivedoff_subject where sapid = ?" ; 
		
		return jdbcTemplate.queryForList(sql,new Object[] {sapid},Integer.class);
	}
	

	@Override
	public List<CustomCourseWaiverDTO> getSubjectCodeId(List<Integer> waivedInPssId) {
		String sql = " SELECT subjectCodeId ,id as pssId,sem FROM exam.mdm_subjectcode_mapping msm "
				+ " WHERE msm.id IN (:waivedInPssId) ";

		nameParamerterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("waivedInPssId", waivedInPssId);

		
		List<CustomCourseWaiverDTO> subject = nameParamerterJdbcTemplate.query(sql, paramSource,
				new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		return subject;

	}



	@Override
	public List<CustomCourseWaiverDTO> getSubjectName(Set<Integer> subjectCodeIds) {
		String sql = " SELECT id,subjectName FROM exam.mdm_subjectcode ms where id in (:subjectCodeIds) ";
		
		nameParamerterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
	
		paramSource.addValue("subjectCodeIds", subjectCodeIds);
	
		return nameParamerterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		 
}

	@Override
	public int getStudentCurrentSem(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select max(sem) from exam.registration where sapid = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
	}

	@Override
	public int checkSapidExist(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select exists(select * from exam.custom_waived_in_subjects where sapid = ?) as count";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
	}
}
