package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.StudentStudentPortalBean;

@Repository
public class StudentSettingDao extends BaseDAO {
	
	@Autowired(required = false)
	ApplicationContext act;

	private DataSource dataSource;
	private JdbcTemplate  jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;		
	}
	
	public void updateStudentSettings(String columnName,String Sapid,int value ) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE exam.students " + 
				"SET " + 
				   columnName +"  = ? " + 
				"WHERE " + 
				"    sapid = ? ";
		jdbcTemplate.update(sql, new Object[] {value,Sapid});
	}
}
