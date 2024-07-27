package com.nmims.daos;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.nmims.beans.AmazonS3Bean;
import com.nmims.beans.FileMigrationBean;

public class AmazonS3Dao 
{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		System.out.println("Setting Data Source " + dataSource);
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		System.out.println("jdbcTemplate = " + jdbcTemplate);
	}

	public JdbcTemplate getJdbCTempalte() {
		return jdbcTemplate;
	}

	

	public List<FileMigrationBean> getHallTickets()
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT id,filePath FROM exam.receipt_hallticket WHERE filePath  like 'E:/%' ";
		List<FileMigrationBean> tickets = null;
		try {		
			tickets = jdbcTemplate.query(sql,new BeanPropertyRowMapper(FileMigrationBean.class));
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return tickets;
	}
	
	
	public int updateHallTicketsUrlLink(final FileMigrationBean fileData)
	{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE exam.receipt_hallticket SET filePath = ? WHERE id = ?";
		
				
			return jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					 	preparedStatement.setString(1,fileData.getFilePath());
		                preparedStatement.setInt(2,fileData.getId());
		   		
				}
			});
		 
		
		
	}
	
	
	public int  updateStudentUrl(final String Originalurl, final String updatedUrl)
	{
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "UPDATE exam.students SET "
						+ " imageUrl=?  "
						+ " WHERE imageUrl=?; ";
			return jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					 	preparedStatement.setString(1,updatedUrl);
		                preparedStatement.setString(2,Originalurl);
		               
					
				}
			});

	
		}
	
	
	
}
