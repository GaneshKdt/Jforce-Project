package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.nmims.beans.CallRepositoryBean;

public class CallRepositoryDAO {

	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {

		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);

	}
	public ArrayList<CallRepositoryBean> getAllUploadedCalls() {

		ArrayList<CallRepositoryBean> callList = new ArrayList<CallRepositoryBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT * FROM salesforce.call_repository";
		
		callList = (ArrayList<CallRepositoryBean>) jdbcTemplate.query(query, new BeanPropertyRowMapper<>(CallRepositoryBean.class));
		
		return callList;
	}
	
	public CallRepositoryBean createCallRepositoryUploadRecord( final CallRepositoryBean bean ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "INSERT INTO `salesforce`.`call_repository` "
				+ "( `name`, `function`, `category`, `url`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifedDate`) "
				+ "VALUES "
				+ "(?,?,?,?,?,SYSDATE(),?,SYSDATE());";
		
		jdbcTemplate.update( query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				
				 preparedStatement.setString( 1, bean.getName() );
				 preparedStatement.setString( 2, bean.getFunction() );
	             preparedStatement.setString( 3, bean.getCategory() );
	             preparedStatement.setString( 4, bean.getUrl() );
	             preparedStatement.setString( 5, bean.getUserId() );
	             preparedStatement.setString( 6, bean.getUserId());
				
			}
		});
		
		return bean;
	}
	
}
