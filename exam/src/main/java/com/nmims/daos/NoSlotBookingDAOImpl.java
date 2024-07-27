package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.NoSlotBookingBean;
import com.nmims.beans.StudentSubjectConfigExamBean;

@Repository
public class NoSlotBookingDAOImpl implements NoSlotBookingDAOInterface {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	@Transactional(readOnly = true)
	public StudentSubjectConfigExamBean getTimeboundDetails(final Long timeboundId) {
		String query =  "SELECT acadYear, acadMonth, examYear, examMonth, prgm_sem_subj_id " + 
						"FROM lti.student_subject_config " + 
						"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(StudentSubjectConfigExamBean.class), timeboundId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int getTimeboundMappingCountByUserIdTimeboundIdRole(final String userId, final Long timeboundId, final String role) {
		final String query = "SELECT COUNT(*) " + 
							"FROM lti.timebound_user_mapping " + 
							"WHERE userId = ? " + 
							"	AND timebound_subject_config_id = ? " + 
							"	AND role = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, userId, timeboundId, role);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getCPSIdBySapidYearMonth(final String sapid, final String acadYear, final String acadMonth) {
		String query =  "SELECT consumerProgramStructureId " + 
						"FROM exam.registration " + 
						"WHERE sapid = ?" + 
						"	AND year = ?" + 
						"	AND month = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, sapid, acadYear, acadMonth);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Integer> getSessionPlanByTimebound(final Long timeboundId) {
		String query =  "SELECT sessionPlanId " + 
						"FROM acads.sessionplanid_timeboundid_mapping " + 
						"WHERE timeboundId = ?";
		
		return jdbcTemplate.query(query, new SingleColumnRowMapper<>(Integer.class), timeboundId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public String getNoSlotBookingStatus(final String sapid, final Long timeboundId, final String type, final Long paymentRecordId) {
		String query =  "SELECT status " + 
						"FROM exam.mba_wx_noslot_bookings " + 
						"WHERE sapid = ?" + 
						"	AND timeboundId = ?" + 
						"	AND type = ?" + 
						"	AND paymentRecordId = ?";

		return jdbcTemplate.queryForObject(query, String.class, sapid, timeboundId, type, paymentRecordId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NoSlotBookingBean> getNoSlotBookingBySapidTimeboundType(final Long timeboundId, final String sapid, final String type) {
		String query =  "SELECT paymentRecordId, status " + 
						"FROM exam.mba_wx_noslot_bookings " + 
						"WHERE sapid = ?" + 
						"	AND timeboundId = ?" + 
						"	AND type = ?";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(NoSlotBookingBean.class), sapid, timeboundId, type);
	}
	
	@Override
	@Transactional(readOnly = false)
	public int insertNoSlotBooking(final String sapid, final long timeboundId, final String type, 
									final long paymentRecordId, final String status) {
		String query =  "INSERT INTO `exam`.`mba_wx_noslot_bookings` " + 
						"(`sapid`, `timeboundId`, `type`, `paymentRecordId`, `status`, " + 
						"`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
						"VALUES (?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate())";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, sapid);
				ps.setLong(2, timeboundId);
				ps.setString(3, type);
				ps.setLong(4, paymentRecordId);
				ps.setString(5, status);
				ps.setString(6, sapid);
				ps.setString(7, sapid);
			}
		});
	}
	
	@Override
	@Transactional(readOnly = false)
	public int updateNoSlotBookingStatus(final String sapid, final String timeboundId, final String type, 
										final long paymentRecordId, final String status) {
		String query =  "UPDATE `exam`.`mba_wx_noslot_bookings` " + 
						"SET `status` = ?, `lastModifiedBy` = ?, `lastModifiedDate` = sysdate() " + 
						"WHERE `sapid` = ?" + 
						"	AND `timeboundId` = ?" + 
						"	AND `type` = ?" + 
						"	AND `paymentRecordId` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, status);
				ps.setString(2, sapid);
				ps.setString(3, sapid);
				ps.setString(4, timeboundId);
				ps.setString(5, type);
				ps.setLong(6, paymentRecordId);
			}
		});
	}
	
	@Override
	@Transactional(readOnly = true)
	public int checkNoSlotBookingStatus(final String sapid, final Long timeboundId, final String type, final String status) {
		String query =  "SELECT count(*) " + 
						"FROM exam.mba_wx_noslot_bookings " + 
						"WHERE sapid = ?" + 
						"	AND timeboundId = ?" + 
						"	AND type = ?" + 
						"	AND status = ?";

		return jdbcTemplate.queryForObject(query, Integer.class, sapid, timeboundId, type, status);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NoSlotBookingBean> noSlotBookingsByTrackId(final String trackId) {
		String query =  "SELECT `b`.* " + 
						"FROM `exam`.`mba_wx_noslot_bookings` `b` " + 
						"INNER JOIN `exam`.`mba_wx_payment_records` `pr` " + 
						"	ON `pr`.`id` = `b`.`paymentRecordId` " + 
						"WHERE `pr`.`trackId` = ?";

		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(NoSlotBookingBean.class), trackId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public int updateNoSlotBookingStatusBySapidPaymentId(final String status, final String modifiedByUser, 
														final String sapid, final Long paymentRecordId) {
		String query =  "UPDATE `exam`.`mba_wx_noslot_bookings` " + 
						"SET `status` = ?," + 
						"	`lastModifiedBy` = ?," + 
						"	`lastModifiedDate` = sysdate() " + 
						"WHERE `sapid` = ?" + 
						"	AND `paymentRecordId` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, status);
				ps.setString(2, modifiedByUser);
				ps.setString(3, sapid);
				ps.setLong(4, paymentRecordId);
			}
		});
	}
	
	@Override
	@Transactional(readOnly = false)
	public int insertNoSlotBookingConflictTransaction(final String trackId, final long bookingId, final String userId) {
		String query =  "INSERT INTO `exam`.`mba_wx_noslot_booking_conflict_transactions` " + 
						"(`trackId`, `bookingId`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
						"VALUES (?, ?, ?, sysdate(), ?, sysdate())";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, trackId);
				ps.setLong(2, bookingId);
				ps.setString(3, userId);
				ps.setString(4, userId);
			}
		});
	}
}
