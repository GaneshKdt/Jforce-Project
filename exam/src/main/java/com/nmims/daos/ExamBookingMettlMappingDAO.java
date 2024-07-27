package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamBookingMettlMappingBean;

@Repository("examBookingMettlMappingDAO")
public class ExamBookingMettlMappingDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = false)
	public String insertIntoExamBookingMettlMapping(ExamBookingMettlMappingBean examBookingMettlMappingBean) {
		try {
			String sql = "insert into exam.exambooking_mettl_mapping(`name`,`sifyCode`,`assessmentId`) values(?,?,?) ON DUPLICATE KEY UPDATE name=? and sifyCode=?";
			jdbcTemplate.update(sql,new Object[] {examBookingMettlMappingBean.getName(),examBookingMettlMappingBean.getSifyCode(),examBookingMettlMappingBean.getAssessmentId(),examBookingMettlMappingBean.getName(),examBookingMettlMappingBean.getSifyCode()});
			return "true";
		}
		catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}
	}

}
