package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.CourseraMappingBean;

public class CourseraDao extends BaseDAO{

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	private NamedParameterJdbcTemplate nameJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	@Transactional(readOnly = true)
	public String getLeanersURLbyMasterKey(String consumerProgramStructureId) {
		String url="";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			String sql =  " select learnerURL from coursera.coursera_program_details cpd " + 
					"inner join coursera.coursera_program_mapping cpm " + 
					"on cpm.coursera_program_id=cpd.id where " + 
					"cpm.consumer_program_structure_id=?; ";

			url = jdbcTemplate.queryForObject(sql, new Object[]{consumerProgramStructureId}, String.class);
			
			//set program for header here so as to use it in all other places
			
			return url;
		}catch(Exception e){
			return url;
			//  
		}
	}
	
	@Transactional(readOnly = true)
	public int checkCourseraProgramApplicableForMasterKey(String consumerProgramStructureId) {
		int count=0;
		String sql="select count(*) from coursera.coursera_program_mapping where consumer_program_structure_id=?";

		try {
		count=(int) jdbcTemplate.queryForObject(sql, new Object[] {consumerProgramStructureId}, Integer.class);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return count;
		
	}
	
	@Transactional(readOnly = true)
	public CourseraMappingBean checkStudentOptedForCoursera(String sapId) {
		
		String sql="select count(*) as count,expiryDate from coursera.coursera_student_mapping where sapId=?";

		
		CourseraMappingBean bean=jdbcTemplate.queryForObject(sql, new Object[] {sapId}, new BeanPropertyRowMapper<>(CourseraMappingBean.class));
		
		return bean;
	}

}
