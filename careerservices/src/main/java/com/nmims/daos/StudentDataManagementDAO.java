package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class StudentDataManagementDAO {

	/*
	 * ---------- Initializations ------------
	 */

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/*
	 * ----------------------- END ------------------------
	 */

	/*
	 * ------ Helper functions to reduce boilerplate ------
	 */

	/*
	 * Check if student details exist in database Checks for the existance of the
	 * 'student_qualifications' data for the given sapid
	 */
	public boolean checkIfStudentDetailsExist(String sapid) {
		// returns true if the user exists in student_qualifications
		int result = jdbcTemplate.queryForObject("SELECT count(*) FROM `exam`.`student_qualifications` WHERE sapid=?",
				new Object[] { sapid }, Integer.class);
		if (result > 0) {
			return true;
		}
		return false;
	}
}
