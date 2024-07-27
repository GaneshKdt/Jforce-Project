package com.nmims.daos;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBACentersBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAExamConflictTransactionBean;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;

@Repository("mbaxExamBookingDAO")
public class MBAXExamBookingDAO extends BaseDAO{

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Value("${MBA_X_EXAM_BOOKING_CHARGES}")
	private String MBA_X_EXAM_BOOKING_CHARGES;
	
	@Value("${MBA_X_EXAM_BOOKING_SLOT_CHANGE_CHARGES}")
	private String MBA_X_EXAM_BOOKING_SLOT_CHANGE_CHARGES;

	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsForStudent(MBAStudentDetailsBean studentDetails) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT "
				+ " `mpf`.`sapid`, "
				+ " `mpf`.`timeboundId` AS `timeboundId`, "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `mpf`.`isPass` AS `isPass`, "
				+ " `mpf`.`status` AS `status`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `ssc`.`examYear` AS `year`, "
				+ " `ssc`.`examMonth` AS `month`, "
				+ " coalesce(`mbaebc`.`bookingAmount`, " + MBA_X_EXAM_BOOKING_CHARGES +") AS `bookingAmount`, "
				+ " coalesce(`mbaebc`.`slotChangeAmount`, " + MBA_X_EXAM_BOOKING_SLOT_CHANGE_CHARGES +") AS `slotChangeAmount` "
			+ " FROM "
			+ " `exam`.`mbax_passfail` `mpf` "
			
			    /* get timebound details */
			    + " INNER JOIN  `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `mpf`.`timeboundId` "

			    /* get batch details */
			    + " INNER JOIN `exam`.`batch` `batch` "
			    + " ON `batch`.`id` = `ssc`.`batchId` "

			    /* get Subject and semester details */
			    + " INNER JOIN `exam`.`program_sem_subject` `pss` "
			    + " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

			    /* get Subject and semester details */
			    + " LEFT JOIN `exam`.`mba_x_exam_booking_charges` `mbaebc` "
			    + " ON `ssc`.`id` = `mbaebc`.`timeboundId` "

			+ " WHERE "
				+ " `sapid` = ? "
			+ " AND `isPass` = 'N' "
			+ " AND `isResultLive` = 'Y' "
			+ " AND `ssc`.`examMonth` = ? "
			+ " AND `ssc`.`examYear` = ? "
			/* Dont let booking for BOP */
			+ " AND `ssc`.`prgm_sem_subj_id` <> '1789' ";
		

		List<MBAStudentSubjectMarksDetailsBean> list = jdbcTemplate.query(
			sql, 
			new Object[]{ 
				studentDetails.getSapid(), studentDetails.getCurrentExamMonth(), studentDetails.getCurrentExamYear() 
			},
			new BeanPropertyRowMapper<MBAStudentSubjectMarksDetailsBean>(MBAStudentSubjectMarksDetailsBean.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<MBAStudentSubjectMarksDetailsBean> getResitFailedSubjectsForStudent(MBAStudentDetailsBean studentDetails) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of Resit subjects for current cycle (if any)
		String sql = ""
			+ " SELECT "
				+ " `tum`.`userId` AS `sapid`, "
				+ " `tum`.`timebound_subject_config_id` AS `timeboundId`, "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `ssc`.`examYear` AS `year`, "
				+ " `ssc`.`examMonth` AS `month`, "
				+ " `tum`.`role` AS `role`, "
				+ " coalesce(`mbaebc`.`bookingAmount`, " + MBA_X_EXAM_BOOKING_CHARGES +") AS `bookingAmount`, "
				+ " coalesce(`mbaebc`.`slotChangeAmount`, " + MBA_X_EXAM_BOOKING_SLOT_CHANGE_CHARGES +") AS `slotChangeAmount` "
			+ " FROM `lti`.`timebound_user_mapping` `tum` "
			
			    /* get timebound details */
			    + " INNER JOIN  `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

			    /* get batch details */
			    + " INNER JOIN `exam`.`batch` `batch` "
			    + " ON `batch`.`id` = `ssc`.`batchId` "

			    /* get Subject and semester details */
			    + " INNER JOIN `exam`.`program_sem_subject` `pss` "
			    + " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

			    /* get booking charges */
			    + " LEFT JOIN `exam`.`mba_x_exam_booking_charges` `mbaebc` "
			    + " ON `ssc`.`id` = `mbaebc`.`timeboundId` "

			+ " WHERE "
				+ " `userId` = ? "
			+ " AND `tum`.`role` = 'Resit' "
			+ " AND `ssc`.`acadYear` = ?"
			+ " AND `ssc`.`acadMonth` = ?";
		

		List<MBAStudentSubjectMarksDetailsBean> list = jdbcTemplate.query(
			sql, 
			new Object[]{ 
				studentDetails.getSapid(), CURRENT_MBAX_ACAD_YEAR, CURRENT_MBAX_ACAD_MONTH
			},
			new BeanPropertyRowMapper<MBAStudentSubjectMarksDetailsBean>(MBAStudentSubjectMarksDetailsBean.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<String> getAppliedSubjects(MBAStudentDetailsBean studentDetails) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects the student has applied for
		
		String sql = ""
				+ " SELECT `timeboundId` "
				+ " FROM `exam`.`mba_x_bookings` `b` "
				+ " WHERE "
					+ " `sapid` = ? "
				+ " AND `bookingStatus` = ? "
				+ " AND `b`.`month` = ?"
				+ " AND `b`.`year` = ?"; 
		List<String> listOfAppliedSubjects = jdbcTemplate.queryForList(
			sql, 
			new Object[]{ 
				studentDetails.getSapid(), MBAExamBookingRequest.BOOKING_STATUS_BOOKED, 
				studentDetails.getCurrentExamMonth(), studentDetails.getCurrentExamYear()
			},
			String.class
		);
		
		return listOfAppliedSubjects;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamBookingLive(MBAStudentDetailsBean mbaStudentDetailsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Check if Exam Booking is live
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_x_exam_live_setting` `mels` "
			+ " WHERE "
				+ " (sysdate() BETWEEN `startTime` AND `endTime`)"
		    + " AND `acadsMonth` = ? AND `acadsYear` = ? "
		    + " AND `examMonth` = ? AND `examYear` = ? "
			+ " AND `type` = 'Exam Registration' ";
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				mbaStudentDetailsBean.getCurrentAcadMonth(), mbaStudentDetailsBean.getCurrentAcadYear(), 
				mbaStudentDetailsBean.getCurrentExamMonth(), mbaStudentDetailsBean.getCurrentExamYear()
			},
			Integer.class
		);
		
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public List<MBACentersBean> getAllCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ "SELECT * FROM `exam`.`mba_x_centers` WHERE `active` = 'Y'";
		
		
		List<MBACentersBean> list = jdbcTemplate.query(
			sql, 
			new BeanPropertyRowMapper<MBACentersBean>(MBACentersBean.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getAllStudentBookings(MBAStudentDetailsBean studentDetails) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `b`.`bookingStatus` AS `bookingStatus`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `ssc`.`id` AS `timeboundId`, "
				+ " `ssc`.`examYear` AS `year`, "
				+ " `ssc`.`examMonth` AS `month`, "
				+ " `centers`.`name` AS `centerName`, "
				+ " `centers`.`address` AS `centerAddress`, "
				+ " `centers`.`googleMapURL` AS `centerMapURL`, "
				+ " `centers`.`city` AS `centerCity` "
				
				
				+ " FROM `exam`.`mba_x_bookings` `b`" 

				+ " INNER JOIN `exam`.`mba_x_slots` `slots` " 
				+ " ON `b`.`slotId` = `slots`.`slotId`" 

				/* Get center info */
				+ " INNER JOIN `exam`.`mba_x_centers` `centers` " 
				+ " ON `centers`.`centerId` = `slots`.`centerId`" 

				/* Get time table info */
				+ " INNER JOIN `exam`.`mba_x_time_table` `tt` " 
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` " 

				+ " INNER JOIN  `lti`.`student_subject_config` `ssc` " 
				+ " ON `ssc`.`id` = `b`.`timeboundId` " 

				/* get batch details */
				+ " INNER JOIN `exam`.`batch` `batch` " 
				+ " ON `batch`.`id` = `ssc`.`batchId` " 

				/* get Subject and semester details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` " 
				+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				
				+ " WHERE "
					+ " `b`.`sapid` = ? "
				+ " AND `b`.`bookingStatus` = ?"
				
				/* Bookings for the latest registration only */
				+ " AND `b`.`month` = ?"
				+ " AND `b`.`year` = ?";
		
		
		List<MBAExamBookingRequest> list = jdbcTemplate.query(
			sql, 
			new Object[] { 
				studentDetails.getSapid(), MBAExamBookingRequest.BOOKING_STATUS_BOOKED, 
				studentDetails.getCurrentExamMonth(), studentDetails.getCurrentExamYear()
			},
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getAllStudentApprovedBookings(MBAStudentDetailsBean studentDetails) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT `pr`.`amount`, `pr`.`trackId` "
				+ " FROM `exam`.`mba_x_payment_records` `pr` "
				
				+ " INNER JOIN `exam`.`mba_x_bookings` `b` "
				+ " ON `pr`.`id` = `b`.`paymentRecordId` " 
				
				+ " WHERE "
					+ " `paymentType` = 'Exam Booking' "
				+ " AND `pr`.`sapid` = ? "
				+ " AND `b`.`month` = ? "
				+ " AND `b`.`year` = ? "
				+ " AND `bookingStatus` IN (?, ?)";
		
		
		List<MBAExamBookingRequest> list = jdbcTemplate.query(
			sql, 
			new Object[] { 
				studentDetails.getSapid(), studentDetails.getCurrentExamMonth(), studentDetails.getCurrentExamYear(), 
				MBAExamBookingRequest.BOOKING_STATUS_BOOKED, MBAExamBookingRequest.BOOKING_STATUS_RELEASED 
			},
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getAllStudentBookingsForTrackId(String sapid, String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `b`.`bookingStatus` AS `bookingStatus`, "
				+ " `pr`.`tranStatus` AS `tranStatus`, "
				+ " `pss`.`sem` AS `term`, "
				+ " `ssc`.`id` AS `timeboundId`, "
				+ " `ssc`.`examYear` AS `year`, "
				+ " `ssc`.`examMonth` AS `month`, "
				+ " `centers`.`name` AS `centerName`, "
				+ " `centers`.`address` AS `centerAddress`, "
				+ " `centers`.`googleMapURL` AS `centerMapURL`, "
				+ " `centers`.`city` AS `centerCity` "
				
				
				+ " FROM `exam`.`mba_x_bookings` `b`" 

				+ " INNER JOIN `exam`.`mba_x_payment_records` `pr` " 
				+ " ON `pr`.`id` = `b`.`paymentRecordId` " 
				
				+ " INNER JOIN `exam`.`mba_x_slots` `slots` " 
				+ " ON `b`.`slotId` = `slots`.`slotId`" 

				/* Get center info */
				+ " INNER JOIN `exam`.`mba_x_centers` `centers` " 
				+ " ON `centers`.`centerId` = `slots`.`centerId`" 

				/* Get time table info */
				+ " INNER JOIN `exam`.`mba_x_time_table` `tt` " 
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` " 

				+ " INNER JOIN  `lti`.`student_subject_config` `ssc` " 
				+ " ON `ssc`.`id` = `b`.`timeboundId` " 

				/* get batch details */
				+ " INNER JOIN `exam`.`batch` `batch` " 
				+ " ON `batch`.`id` = `ssc`.`batchId` " 

				/* get Subject and semester details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` " 
				+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				
				+ " WHERE `b`.`sapid` = ? AND `pr`.`trackId` = ?";
		
		
		List<MBAExamBookingRequest> list = jdbcTemplate.query(
			sql, 
			new Object[] { sapid, trackId },
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public MBAExamBookingRequest getLatestStudentBookingForTimeboundId(String sapid, String timeboundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				+ " `b`.`bookingStatus` AS `bookingStatus`, "
				+ " `b`.`slotId` AS `slotId`, "
				+ " `centers`.`name` AS `centerName`, "
				+ " `centers`.`address` AS `centerAddress`, "
				+ " `centers`.`googleMapURL` AS `centerMapURL`, "
				+ " `centers`.`city` AS `centerCity` "
				
				
				+ " FROM `exam`.`mba_x_bookings` `b`" 

				+ " INNER JOIN `exam`.`mba_x_slots` `slots` " 
				+ " ON `b`.`slotId` = `slots`.`slotId`" 

				/* Get center info */
				+ " INNER JOIN `exam`.`mba_x_centers` `centers` " 
				+ " ON `centers`.`centerId` = `slots`.`centerId`" 

				/* Get time table info */
				+ " INNER JOIN `exam`.`mba_x_time_table` `tt` " 
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` " 

				+ " INNER JOIN  `lti`.`student_subject_config` `ssc` " 
				+ " ON `ssc`.`id` = `b`.`timeboundId` " 

				/* get batch details */
				+ " INNER JOIN `exam`.`batch` `batch` " 
				+ " ON `batch`.`id` = `ssc`.`batchId` " 

				/* get Subject and semester details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` " 
				+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				
				+ " WHERE "
					+ " `sapid` = ? "
				+ " AND `b`.`timeboundId` = ? "
				+ " AND `bookingStatus` = ? "
				+ " ORDER BY `b`.`lastUpdatedOn` DESC "
				+ " LIMIT 1"
				+ " ";
		
		try {
			MBAExamBookingRequest details = jdbcTemplate.queryForObject(
					sql, 
					new Object[] { sapid, timeboundId, MBAExamBookingRequest.BOOKING_STATUS_BOOKED },
					new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
				);
				
			return details;
		}catch (Exception e) {
			return null;
		}

	}
	
	@Transactional(readOnly = true)
	public List<MBASlotBean> getAllSlotsByTimeboundIdAndCenterId(Long timeboundId, Long centerId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  getSlotSelectionSql() + " "
				+ " LEFT JOIN `lti`.`student_subject_config` `ssc` "
					+ " ON `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` "
					+ " AND `ssc`.`examMonth` = `tt`.`examMonth` "
					+ " AND `ssc`.`examYear` = `tt`.`examYear`  "
				+ " WHERE `slots`.`centerId` = ? AND `ssc`.`id` = ? ";

		List<MBASlotBean> list = jdbcTemplate.query(
			sql, 
			new Object[]{ centerId, timeboundId },
			new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<MBASlotBean> getAllSlotsByCenterId(Long centerId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = getSlotSelectionSql() + " WHERE `slots`.`centerId` = ? ";
		
		
		List<MBASlotBean> list = jdbcTemplate.query(
			sql, 
			new Object[]{ 
					centerId
				},
			new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
		);
		
		return list;
	}
	
	
	private String getSlotSelectionSql() {
		String sql = ""
				+ " SELECT "
				
				/* Slot availability info */			
				+ " (`slots`.`capacity` - coalesce(`number_bookings`.`count`, 0)) AS `availableSlots`, "
				+ " coalesce(`number_bookings`.`count`, 0) AS `bookedSlots`, "
				
				/* Slot info */
				+ " `slots`.`slotId` AS `slotId`, "
				+ " `slots`.`capacity` AS `capacity`, "

				/* Schedule info */
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				
				/* MISC fields */
				+ " `slots`.`centerId` AS `centerId`, "
				+ " `slots`.`timeTableId` AS `timeTableId`, "
				+ " `tt`.`programSemSubjectId` AS `programSemSubjectId`, "
				+ " `tt`.`examYear` AS `examYear`, "
				+ " `tt`.`examMonth` AS `examMonth` "
				
				+ " FROM `exam`.`mba_x_slots` `slots` "

				/* Get time table info */				
				+ " LEFT JOIN `exam`.`mba_x_time_table` `tt` "
				+ " ON `slots`.`timeTableId` = `tt`.`timeTableId` "
				
				/* Get number of occupied seats */
				+" LEFT JOIN ( "
					+ " SELECT "
					+ " count(*) AS `count`, "
					+ " `slotId` "
					
					/* Get unique user bookings. This is done to make sure only one booking from each user is counted. */
					+ " FROM `exam`.`mba_x_bookings` `b` " 
					
					/* Only get slots with booking status Success or Initiated */
					+ " WHERE `b`.`bookingStatus` = 'Y' "
					+ " GROUP BY `b`.`slotId` "
				+ " ) `number_bookings` "
				+ " ON `number_bookings`.`slotId` = `slots`.`slotId` ";
		return sql;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSuccessfulBookingsForSeatRelease(MBAExamBookingRequest bookingRequest) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT `bookingId` FROM `exam`.`mba_x_bookings` WHERE `timeboundId` IN ( "
					+ " SELECT `timeboundId` "
					+ " FROM `exam`.`mba_x_bookings` `b` "
					+ " INNER JOIN `exam`.`mba_x_payment_records` `pr` ON `b`.`paymentRecordId` = `pr`.`id` "
					+ " WHERE `pr`.`trackId` = ? "
				+ " ) "
				+ " AND `sapid` = ? "
				+ " AND `bookingStatus` = ?";
		
		List<String> listOfBookings = jdbcTemplate.queryForList(
			sql, 
			new Object[]{ 
				bookingRequest.getTrackId(),
				bookingRequest.getSapid(),
				MBAExamBookingRequest.BOOKING_STATUS_BOOKED 
			},
			String.class
		);
		return listOfBookings;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MBAExamBookingRequest> getUnSuccessfulExamBookings(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mxb.bookingId,mxb.sapid,mxb.slotId,mxb.term,mxb.year,mxb.month,mxb.paymentRecordId,mxb.bookingStatus," +
				"mxp.paymentOption,mxp.trackId,mxp.amount,mxp.tranStatus " +
				"FROM exam.mba_x_bookings mxb, exam.mba_x_payment_records mxp where mxb.paymentRecordId= mxp.id " +
				"and mxb.sapid = ?  " +
				"and mxb.bookingStatus <> 'Y' " +
				"and mxb.bookingStatus <> 'RL' " +
				"and mxb.bookingStatus <> 'RF' " +
				"and mxb.bookingStatus <> 'Online Payment Manually Approved' " +
				"group by mxp.trackId";

		ArrayList<MBAExamBookingRequest> bookingList = (ArrayList<MBAExamBookingRequest>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class));
		return bookingList;
	}
	
	

	
	
	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getAllStudentBookingsForTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
					+ " `b`.`bookingId` AS `bookingId`, "
					+ " `b`.`bookingStatus` AS `bookingStatus`, "
					+ " `pr`.`tranStatus` AS `tranStatus`, "
					+ " `b`.`timeboundId` AS `timeboundId`, "
					+ " `b`.`sapid` AS `sapid`, "
					+ " `b`.`term` AS `term`, "
					+ " `b`.`year` AS `year`, "
					+ " `b`.`month` AS `month` "
					
				+ " FROM `exam`.`mba_x_bookings` `b` " 

				+ " INNER JOIN `exam`.`mba_x_payment_records` `pr` " 
					+ " ON `pr`.`id` = `b`.`paymentRecordId` " 
				
				+ " WHERE `pr`.`trackId` = ?";
		
		
		List<MBAExamBookingRequest> list = jdbcTemplate.query(
			sql, 
			new Object[] { trackId },
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfStudentHasSuccessfulBookingsForTimeboundId(String timeboundId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mba_x_bookings` `b` " 
				+ " LEFT JOIN `exam`.`mba_x_payment_records` `pr` " 
					+ " ON `pr`.`id` = `b`.`paymentRecordId` " 
				+ " WHERE `b`.`timeboundId` = ? AND `b`.`sapid` = ? AND `b`.`bookingStatus` = ?";
		
		
		int numRecords = jdbcTemplate.queryForObject(
			sql, 
			new Object[] { timeboundId, sapid, MBAExamBookingRequest.BOOKING_STATUS_BOOKED },
			Integer.class
		);
		
		return numRecords > 0;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MBAExamBookingRequest> getConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mxb.bookingId,mxb.sapid,mxb.slotId,mxb.term,mxb.year,mxb.month,mxb.paymentRecordId,mxb.bookingStatus, " 
				+ " mxp.paymentOption,mxp.trackId,mxp.amount,mxp.amount,mxp.tranStatus " 
				+ " FROM exam.mba_x_bookings mxb, exam.mba_x_payment_records mxp where mxb.paymentRecordId= mxp.id "
				+ " and mxb.sapid = ? "
				+ " and bookingStatus = 'Y' "
				+ " order by createdDate asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<MBAExamBookingRequest> bookingList = (ArrayList<MBAExamBookingRequest>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class));
		return bookingList;
	}
	
	@Transactional(readOnly = false)
	public Long insertNewPaymentRecord(final MBAExamBookingRequest examBooking) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " "
			+ " INSERT INTO `exam`.`mba_x_payment_records` "
			+ " ( "
				+ " `sapid`, `paymentType`, `paymentOption`, "
				+ " `source`, `trackId`, `amount`, "
				+ " `description`, `tranStatus`, `transactionID`, "
				+ " `requestID`,  `lastModifiedBy`, `createdBy`, "
				+ " `tranDateTime`,  `lastModifiedDate`, `createdDate` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " sysdate(), sysdate(), sysdate() "
			+ " ) ";
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(
			new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
    				ps.setString(1, examBooking.getSapid());
    				ps.setString(2, examBooking.getPaymentType());
    				ps.setString(3, examBooking.getPaymentOption());
    				ps.setString(4, examBooking.getSource());
    				ps.setString(5, examBooking.getTrackId());
    				ps.setString(6, examBooking.getAmount());
    				ps.setString(7, examBooking.getDescription());
    				ps.setString(8, examBooking.getTranStatus());
    				ps.setString(9, examBooking.getTransactionID());
    				ps.setString(10, examBooking.getRequestID());
    				ps.setString(11, examBooking.getCreatedBy());
    				ps.setString(12, examBooking.getLastModifiedBy());
                    return ps;
                }
            }, 
			holder
		);
		return holder.getKey().longValue();
	}
	
	@Transactional(readOnly = false)
	public void addConflictTransaction(MBAExamBookingRequest conflictRequest) {
		// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " "
			+ " INSERT INTO `exam`.`mba_x_exam_booking_conflict_transactions` "
			+ " ( "
				+ " `trackId`, `bookingId`, "
				+ " `lastModifiedBy`, `createdBy`, "
				+ " `lastModifiedOn`, `createdOn` "
			+ " ) VALUES ("
				+ " ?, ?, "
				+ " ?, ?, "
				+ " sysdate(), sysdate() "
			+ " ) ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				conflictRequest.getTrackId(), conflictRequest.getBookingId(),
				conflictRequest.getLastModifiedBy(), conflictRequest.getCreatedBy()
			}
		);

	}
	
	@Transactional(readOnly = true)
	public List<MBAExamConflictTransactionBean> getAllConflictTransactions(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
				+ " SELECT * "
					+ " FROM `exam`.`mba_x_exam_booking_conflict_transactions` `c` "
				
				+ " RIGHT JOIN `exam`.`mba_x_bookings` `b` "
					+ " ON `b`.`bookingId` = `c`.`bookingId` "
				+ " WHERE `b`.`year` = ? AND `b`.`month` = ?"
				+ " ORDER BY `trackId` asc";


		List<MBAExamConflictTransactionBean> transactionList = jdbcTemplate.query(
				sql, 
				new Object[]{ year, month }, 
				new BeanPropertyRowMapper<MBAExamConflictTransactionBean>(MBAExamConflictTransactionBean.class));
		
		return transactionList;
	}
	
	@Transactional(readOnly = true)
	public List<String> getActiveTimeTableTimeboundId(List<String> timeboundIds) {
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String sql = " SELECT " + 
				"    `ssc`.`id` " + 
				"FROM " + 
				"    `exam`.`mba_x_slots` `slots` " + 
				"        INNER JOIN " + 
				"    `exam`.`mba_x_time_table` `tt` ON `slots`.`timeTableId` = `tt`.`timeTableId` " + 
				"        INNER JOIN " + 
				"    `lti`.`student_subject_config` `ssc` ON `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` " + 
				"        AND `ssc`.`examMonth` = `tt`.`examMonth` " + 
				"        AND `ssc`.`examYear` = `tt`.`examYear` " + 
				"WHERE " + 
				"    `ssc`.`id` IN ( :timeboundIds ) " + 
				"        AND (CURRENT_TIMESTAMP() < tt.examEndDateTime) ";

		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("timeboundIds", timeboundIds);
		List<String> timeboundId = namedJdbcTemplate.queryForList(sql, queryParams, String.class);
		return timeboundId;
	}
	
	@Transactional(readOnly = true)
	public int getNumberOfStudentMappingsForSubject(MBAStudentSubjectMarksDetailsBean subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT " + 
			"    COUNT(tum.userId) " + 
			"FROM " + 
			"    `lti`.`timebound_user_mapping` `tum` " + 
			"        INNER JOIN " + 
			"    `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` " + 
			"        INNER JOIN " + 
			"    `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` " + 
			"WHERE " + 
			"    tum.userId = ? " + 
			"        AND `pss`.`subject` = ?  ";
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				subject.getSapid(), subject.getSubjectName()
			},
			Integer.class
		);
		
		return count;
	}
	
}
