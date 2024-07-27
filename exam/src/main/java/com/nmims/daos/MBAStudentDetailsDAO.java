package com.nmims.daos;


import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.StudentExamBean;

public class MBAStudentDetailsDAO extends BaseDAO{

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			StudentExamBean student = jdbcTemplate.queryForObject(
					sql,
					new Object[] { 
						sapid, 
						sapid 
					}, 
					new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
				);
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

			return student;
		} catch (Exception e) {
			
		}
		return null;
	}
	
	// Get current registration/timebound details for the student
	public MBAStudentDetailsBean getTimeboundDetailsForStudentForMonthYear(String sapid, String acadMonth, String acadYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
				+ " SELECT "
					+ " `tum`.`userId` AS `sapid`, "
					+ " `s`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
					+ " `ssc`.`acadYear` AS `currentAcadYear`, "
					+ " `ssc`.`acadMonth` AS `currentAcadMonth`, "
					+ " `ssc`.`examYear` AS `currentExamYear`, "
					+ " `ssc`.`examMonth` AS `currentExamMonth` "
				+ " FROM `lti`.`timebound_user_mapping` `tum` "

			    + " INNER JOIN  `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

			    + " INNER JOIN  `exam`.`students` `s` "
			    + " ON `s`.`sapid` = `tum`.`userId` "
			    
				+ " WHERE `userId` = ? "
				+ " AND `ssc`.`acadYear` = ?"
				+ " AND `ssc`.`acadMonth` = ? "
				+ " GROUP BY `acadYear`, `acadMonth` ";


		MBAStudentDetailsBean currentRegistrationDetails = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ 
					sapid, acadYear, acadMonth 
				}, 
				new BeanPropertyRowMapper<MBAStudentDetailsBean>(MBAStudentDetailsBean.class));
		
		return currentRegistrationDetails;
	}
	
	
	// Get current registration/timebound details for the student
	public MBAStudentDetailsBean getLatestRegistrationForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
				+ " SELECT "
					+ " `r`.`sapid` AS `sapid`, "
					+ " `r`.`consumerProgramStructureId`, "
					+ " `ssc`.`acadYear` AS `currentAcadYear`, "
					+ " `ssc`.`acadMonth` AS `currentAcadMonth`, "
					+ " `ssc`.`examYear` AS `currentExamYear`, "
					+ " `ssc`.`examMonth` AS `currentExamMonth` "
				+ " FROM `exam`.`registration` `r` "

				+ " LEFT JOIN "
				+ " ( "
					+ " SELECT `acadYear`, `acadMonth`, `examYear`, `examMonth` "
					+ " FROM `lti`.`student_subject_config` "
					/* 
					 * This will group the subjects from each sem.
					 * This is done to group and get the latest month/year for the students current registration 
					*/
					+ " GROUP BY `acadYear`, `acadMonth`, `examYear`, `examMonth` "
				+ " ) `ssc` "
				+ " ON `ssc`.`acadYear` = `r`.`year` AND `ssc`.`acadMonth` = `r`.`month` "
				
				/* Latest registration details */
				+ " WHERE (`sapid`, `sem`) IN ( "
					+ " SELECT "
					+ " `sapid`, "
					+ " MAX(`sem`) AS `sem` "
					+ " FROM `exam`.`registration` "
					+ " WHERE `sapid` = ? "
					+ " GROUP BY `sapid` "
				+ " ) ";


		MBAStudentDetailsBean currentRegistrationDetails = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ sapid }, 
				new BeanPropertyRowMapper<MBAStudentDetailsBean>(MBAStudentDetailsBean.class));
		
		return currentRegistrationDetails;
	}
}
