package com.nmims.daos;


import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.StudentExamBean;

@Repository("mbaxHallTicketDAO")
public class MBAXHallTicketDAO extends BaseDAO{

	
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

	@Transactional(readOnly = true)
	public String getSubjectByTimeboundId(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `ps`.`subject` "
				+ " FROM `lti`.`student_subject_config` `ssc` "
				+ " INNER JOIN `exam`.`program_sem_subject` `ps` "
				+ " ON ssc.prgm_sem_subj_id = ps.id "
				+ " WHERE `ssc`.`id` = ? "; 

		String subjectName = jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
		return subjectName;  
	}
	
	@Transactional(readOnly = true)
	public boolean hallTicketDownloadedStatus(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = ""
					+ " SELECT count(*) "
					+ " FROM `exam`.`mba_x_bookings` `b` "
					
					+ " LEFT JOIN `exam`.`students` `s` "
					+ " ON `b`.`sapid` = `s`.`sapid` "
					
					/* Get bookings for current live period */
					+ " RIGHT JOIN `exam`.`mba_x_exam_live_setting` `ls` "
					+ " ON `b`.`year` = `ls`.`examYear`"
					+ " AND `b`.`month` = `ls`.`examMonth` "
					+ " AND `s`.`consumerProgramStructureId` = `ls`.`consumerProgramStructureId` "
					
					+ " WHERE `sapid` = ? "
					+ " AND `htDownloaded` = 'Y' "
					+ " AND `bookingStatus` = 'Y' "
					+ " AND `ls`.`startTime` < sysdate() AND `ls`.`endTime` > sysdate() "
					+ " AND `ls`.`type` = 'Hall Ticket' ";
			
			int count = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { sapid },
				Integer.class
			);
			
			return count> 0 ? true : false;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfHallTicketDownloadActive(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = ""
					+ " SELECT count(*) "
					+ " FROM `exam`.`students` `s` "
					
					/* Get bookings for current live period */
					+ " RIGHT JOIN `exam`.`mba_x_exam_live_setting` `ls` "
					+ " ON `s`.`consumerProgramStructureId` = `ls`.`consumerProgramStructureId` "
					
					+ " WHERE `s`.`sapid` = ? "
					+ " AND `ls`.`startTime` < sysdate() AND `ls`.`endTime` > sysdate() "
					+ " AND `ls`.`type` = 'Hall Ticket' ";
			
			int count = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { sapid },
				Integer.class
			);
			
			return count> 0 ? true : false;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";


			List<StudentExamBean> studentList = jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));

			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);
				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			return student;
		}catch(Exception e){
			
			return null;
		}

	}
}
