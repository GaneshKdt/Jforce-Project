package com.nmims.daos;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBAExamBookingReportBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.ReExamEligibleStudentBean;
import com.nmims.beans.StudentExamBean;

@Repository("mbawxReportsDAO")
public class MBAWXReportsDAO extends BaseDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		jdbcTemplate = new JdbcTemplate(dataSource);
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}

	@Transactional(readOnly = true)
	public List<MBASlotBean> getSlotBookingDetails(MBASlotBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
				
					// Exam M/Y and subject
					+ " `tt`.`examYear`, "
					+ " `tt`.`examMonth`, "
					+ " `pss`.`subject` AS `subjectName`, "
					
					// Center
					+ " `centers`.`name` AS `centerName`, "
					+ " `centers`.`city` AS `centerCity`, "
					+ " `centers`.`address` AS `centerAddress`, "
					
					// Date and time
					+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
					+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
					+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime`, "
	
					// Slot bookings
					+ " COALESCE(`bookingDetails`.`booked`, 0) AS `bookedSlots`, "
					+ " `slots`.`capacity` - COALESCE(`bookingDetails`.`booked`, 0) AS `availableSlots`, "
					+ " `slots`.`capacity` "

				+ " FROM `exam`.`mba_wx_slots` `slots` "
				
				 /* Get center info */ 
				+ " INNER JOIN `exam`.`mba_wx_centers` `centers` "
				+ " ON `centers`.`centerId` = `slots`.`centerId` "

				 /* Get time table info */
				+ " INNER JOIN `exam`.`mba_wx_time_table` `tt` "
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` "

				 /* get Subject and semester details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `tt`.`programSemSubjectId` = `pss`.`id` "

				/* Get exam booking details */
				+ " LEFT JOIN ( "
					+ " SELECT `slotId`, COUNT(*) AS `booked` "
					+ " FROM `exam`.`mba_wx_bookings` "
					+ " WHERE `bookingStatus` = 'Y' "
					+ " GROUP BY `slotId` "
				+ " ) `bookingDetails` "
				+ " ON `slots`.`slotId` = `bookingDetails`.`slotId` "

				+ " WHERE "
					+ " `tt`.`examYear` = ? "
				+ " AND `tt`.`examMonth` = ? ";
		
		List<MBASlotBean> slotBookingDetails = jdbcTemplate.query(
			sql,
			new Object[] { searchBean.getExamYear(), searchBean.getExamMonth() },
			new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
		);
		
		return slotBookingDetails;
	}
	
	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getBookings(MBAExamBookingReportBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT"
					+ " `b`.`bookingId`, "
					+ " `b`.`paymentRecordId`, "
					+ " `b`.`sapid`, "
					+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
					+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
					+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime`, "
					+ " `pss`.`subject` AS `subjectName`, "
					+ " `pss`.`sem` AS `term`, "
					+ " `b`.`bookingStatus` AS `bookingStatus`, "
					+ " `pss`.`sem` AS `term`, "
					+ " `ssc`.`id` AS `timeboundId`, "
					+ " `ssc`.`examYear` AS `year`, "
					+ " `ssc`.`examMonth` AS `month`, "
					+ " `centers`.`name` AS `centerName`, "
					+ " `centers`.`address` AS `centerAddress`, "
					+ " `centers`.`city` AS `centerCity` "
				
				+ " FROM `exam`.`mba_wx_bookings` `b`" 

				+ " INNER JOIN `exam`.`mba_wx_slots` `slots` " 
				+ " ON `b`.`slotId` = `slots`.`slotId`" 

				/* Get center info */
				+ " INNER JOIN `exam`.`mba_wx_centers` `centers` " 
				+ " ON `centers`.`centerId` = `slots`.`centerId`" 

				/* Get time table info */
				+ " INNER JOIN `exam`.`mba_wx_time_table` `tt` " 
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` " 

				+ " INNER JOIN  `lti`.`student_subject_config` `ssc` " 
				+ " ON `ssc`.`id` = `b`.`timeboundId` " 

				/* get batch details */
				+ " INNER JOIN `exam`.`batch` `batch` " 
				+ " ON `batch`.`id` = `ssc`.`batchId` " 

				/* get Subject and semester details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` " 
				+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				+ " WHERE `bookingStatus` = 'Y' "
				+ " AND `year` = ? AND `month` = ? ";
		
		List<MBAExamBookingRequest> bookingsList = jdbcTemplate.query(
			sql,
			new Object[] { searchBean.getExamYear(), searchBean.getExamMonth() },
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		
		return bookingsList;
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getStudentDetails(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT * FROM `exam`.`students` WHERE `sapid` = ? ";
		
		StudentExamBean student = jdbcTemplate.queryForObject(
			sql,
			new Object[] { sapid },
			new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
		);
		
		return student;
	}
	
	@Transactional(readOnly = true)
	public MBAPaymentRequest getPaymentDetails(String paymentRecordId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT * "
				+ " FROM `exam`.`mba_wx_payment_records` "
				+ " WHERE `id` = ? ";
		
		MBAPaymentRequest bookingsList = jdbcTemplate.queryForObject(
			sql,
			new Object[] { paymentRecordId },
			new BeanPropertyRowMapper<MBAPaymentRequest>(MBAPaymentRequest.class)
		);
		
		return bookingsList;
	}
	
	@Transactional(readOnly = true)
	public List<String> getListOfFailedStudents(String authorizedCenterCodes) {
		String sql = ""
				+ " SELECT `s`.`sapid` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `pf`.`timeboundId` "
				+ " INNER JOIN `exam`.`students` `s` ON `s`.`sapid` = `pf`.`sapid` "
				+ " WHERE (`pf`.`isPass` = 'N') ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql+ " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql += " GROUP BY `sapid` ";
		return jdbcTemplate.queryForList(
			sql,
			String.class
		);
	}
	
	@Transactional(readOnly = true)
	public List<ReExamEligibleStudentBean> getListOfFailedSubjectsForStudent(String sapid) {
		String sql = ""
			+ " SELECT "
				+ " `pf`.`sapid`, "
				+ " `pss`.`subject`, "
				+ " `pss`.`sem`, "
				+ " `tum`.`timebound_subject_config_id`, "
				+ " `ssc`.`acadYear`, "
				+ " `ssc`.`acadMonth`, "
				+ " `ssc`.`examYear`, "
				+ " `ssc`.`examMonth`, "
				+ " COALESCE(`pf`.`iaScore`, 0) AS `iaScore`, "
				+ " COALESCE(`pf`.`teeScore`, 0) AS `teeScore`, "
				+ " `pf`.`isPass` AS `isPass`, "
				+ " `tum`.`role` "
			+ " FROM `exam`.`mba_passfail` `pf` "
			+ " INNER JOIN `lti`.`timebound_user_mapping` `tum` ON `tum`.`userId` = `pf`.`sapid` AND `tum`.`timebound_subject_config_id` = `pf`.`timeboundId` "
			+ " INNER JOIN `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
			+ " WHERE `pf`.`isPass` <> 'Y' "
			+ " AND `pf`.`sapid` = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		return jdbcTemplate.query(
			sql, 
			new Object[] { sapid },
			new BeanPropertyRowMapper<ReExamEligibleStudentBean>(ReExamEligibleStudentBean.class)
		);
	}
	
	//Get all failed students subjects as re-exam eligible  
	@Transactional(readOnly = true)
	public List<ReExamEligibleStudentBean> getListOfFailedSubjects(String authorizedCenterCodes, String masterKeys) throws Exception{
		//Prepare SQL query.
		StringBuilder GET_RE_EXAM_ELIGIBLE_STUDENTS = new StringBuilder("SELECT pf.sapid,  pss.subject, pss.sem, tum.timebound_subject_config_id, ")
		.append("ssc.acadMonth, ssc.examYear,  ssc.examMonth, pf.isPass AS isPass,tum.role ,ssc.acadYear, ")
		.append("COALESCE(pf.iaScore, 0) AS iaScore,COALESCE(pf.teeScore, 0) AS teeScore FROM exam.mba_passfail pf ")  
		.append("INNER JOIN lti.timebound_user_mapping tum ON tum.userId = pf.sapid AND tum.timebound_subject_config_id = pf.timeboundId ") 
		.append("INNER JOIN lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id ") 
		.append("INNER JOIN exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id ") 
		.append("INNER JOIN exam.students s ON s.sapid = pf.sapid ") 
		.append("WHERE pf.isPass <> 'Y' AND s.consumerProgramStructureId in ("+masterKeys+")  ");
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			GET_RE_EXAM_ELIGIBLE_STUDENTS.append(" and s.centerCode in (" + authorizedCenterCodes + ") ");
		}
		
		//Return re-eligible eligible students with subjects.
		return jdbcTemplate.query(GET_RE_EXAM_ELIGIBLE_STUDENTS.toString(),
			new BeanPropertyRowMapper<ReExamEligibleStudentBean>(ReExamEligibleStudentBean.class));
	}
}
