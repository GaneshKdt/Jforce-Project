package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import com.nmims.beans.LoginLogBean;

public class LoginLogDAO extends BaseDAO{

	@Autowired
	@Qualifier("analyticsDataSource")
	private DataSource analyticsDataSource;
	
	private JdbcTemplate jdbcTemplate;

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	@Async
	public void insertLoginDetails( LoginLogBean bean ) throws Exception {

		Thread.sleep(10000);
			
		jdbcTemplate = new JdbcTemplate(analyticsDataSource);

		String sql = "insert into portal.logins "
				+ "( sapid ,logintime,ipAddress,os ,browser) values "
				+ " (?,sysdate(),?,?,?)";

		jdbcTemplate.update(sql, new Object[] { 
				bean.getSapid(), bean.getIpAddress(), bean.getOperatingSystem(), bean.getBrowserDetails()
		});

	}
	
	public LoginLogBean getLoginLogs( LoginLogBean bean ) throws Exception{
	
		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
		
		String query = "SELECT  "
				+ "    MIN(logintime) AS firstLogin, "
				+ "    MAX(logintime) AS lastLogin "
				+ "FROM "
				+ "    portal.logins "
				+ "WHERE "
				+ "    sapid = ?";
		
		LoginLogBean loginLogBean = jdbcTemplate.queryForObject( query, new Object[] { bean.getSapid() }, 
				new BeanPropertyRowMapper<>( LoginLogBean.class ));
		
		bean.setFirstLogin( loginLogBean.getFirstLogin() );
		bean.setLastLogin( loginLogBean.getLastLogin() );
		
		return bean;
		
	}

	public LoginLogBean getMailerTriggredOn( String sapid ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT sapid, createdDate AS mailerTriggredOn FROM exam.students WHERE sapid = ?";
		
		LoginLogBean bean = jdbcTemplate.queryForObject( query, new Object[] { sapid }, 
				new BeanPropertyRowMapper<>( LoginLogBean.class ));
		
		return bean;
		
	}

}
