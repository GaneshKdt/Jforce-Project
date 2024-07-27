package com.nmims.daos;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AnalyticsObjectStudentPortal;

@Repository("errorAnalyticsDAO")
public class ErrorAnalyticsDAO {
	
	@Autowired
	private DataSource dataSource;
	

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional(readOnly = false)
	public boolean registerError(AnalyticsObjectStudentPortal analyticsObject) {
		String sql = ""
				+ " INSERT INTO "
					+ " `portal`.`error_analytics` "
				+ " ( "
					+ " `sapid`, `ipAddress`, "
					+ " `userAgent`, `module`, `stackTrace`, "
					+ " `createdBy`, `updatedBy` "
				+ " ) "
				+ " VALUES "
				+ " ( "
					+ " ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ? "
				+ " )";

		if( StringUtils.isBlank(analyticsObject.getStackTrace()) ) {
			analyticsObject.setStackTrace("");
		}
		
		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					analyticsObject.getSapid(), analyticsObject.getIpAddress(),
					analyticsObject.getUserAgent(), analyticsObject.getModule(), analyticsObject.getStackTrace(),
					analyticsObject.getSapid(), analyticsObject.getSapid()
				}
			);
			return true;
		}catch (Exception e) {
			
			return false;
		}
	}
}
