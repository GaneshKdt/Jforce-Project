package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;


public class MBAWXPaymentsDao  extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	

	public Long insertNewPaymentRecord(final MBAExamBookingRequest examBooking) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " "
			+ " INSERT INTO `exam`.`mba_wx_payment_records` "
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
	
	public boolean insertExamBooking(MBAStudentSubjectMarksDetailsBean selectedSubject, Long id, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
				+ " INSERT INTO `exam`.`mba_wx_bookings` "
				+ " ( "
					+ " `sapid`, `slotId`, `timeboundId`, "
					+ " `term`, `year`, `month`, "
					+ " `paymentRecordId`, `createdBy`, `lastUpdatedBy`, "
					+ " `bookingStatus`, "
					+ " `createdOn`, `lastUpdatedOn` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, "
					+ " sysdate(), sysdate() "
				+ " ) ";
		jdbcTemplate.update(
				sql, 
				new Object[] {
					sapid, selectedSubject.getSlotId(), selectedSubject.getTimeboundId(), 
					selectedSubject.getTerm(), selectedSubject.getYear(), selectedSubject.getMonth(), 
					id, sapid, sapid,
					MBAExamBookingRequest.BOOKING_STATUS_FAIL
				} 
			);
		return true;
	}

	@Transactional(readOnly = true)
	public MBAExamBookingRequest getExamBookingRequestByTransactionIdAndSapid(String trackId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
			+ " SELECT *, `pr`.`id` AS `paymentRecordId` FROM `exam`.`mba_wx_bookings` `b` "
			+ " INNER JOIN `exam`.`mba_wx_payment_records` `pr` ON `b`.`paymentRecordId` = `pr`.`id` "
			+ " WHERE `pr`.`trackId` = ? AND `b`.`sapid` = ? "
			+ " LIMIT 1";
		try {
			MBAExamBookingRequest result = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ trackId, sapid },
				new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
			);
			return result;
		}catch (Exception e) {

			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public MBAExamBookingRequest getInitiatedPaymentRequestByTrackIdAndSapid(String trackId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
			+ " SELECT * FROM `exam`.`mba_wx_payment_records` "
			+ " WHERE `trackId` = ? AND `sapid` = ? AND `tranStatus` = ? "
			+ " LIMIT 1";
		try {
			MBAExamBookingRequest result = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ trackId, sapid, MBAExamBookingRequest.TRAN_STATUS_INITIATED },
				new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
			);
			return result;
		}catch (Exception e) {

			
			return null;
		}
	}
	
	
	public void updateTransactionDetails(MBAPaymentRequest responseBean) {
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
					+ " UPDATE "
						+ " `exam`.`mba_wx_payment_records` "
                    + " SET "
	                    + " `responseMessage` = ?,  `transactionID` = ?,  `requestID` = ?, "
	                    + " `merchantRefNo` = ?, `secureHash` = ?, `respAmount` = ?, "
	                    + " `responseCode` = ?, `respPaymentMethod` = ?, `isFlagged` = ?, "
	                    + " `paymentID` = ?, `error` = ?, `respTranDateTime` = ?, "
	                    + " `tranStatus` = ?,  `bankName` = ?, `lastModifiedBy` = ?, "
	                    + " `lastModifiedDate` = sysdate() "
                    + " WHERE "
                    	+ " `sapid` = ? "
                    + " AND `trackId` = ?";

			jdbcTemplate.update(sql, new Object[] { 
					responseBean.getResponseMessage(), responseBean.getTransactionID(), responseBean.getRequestID(),
					responseBean.getMerchantRefNo(), responseBean.getSecureHash(), responseBean.getRespAmount(),
					responseBean.getResponseCode(), responseBean.getRespPaymentMethod(), responseBean.getIsFlagged(),
					responseBean.getPaymentID(), responseBean.getError(), responseBean.getRespTranDateTime(),
					responseBean.getTranStatus(), responseBean.getBankName(), responseBean.getLastModifiedBy(), 
					
					responseBean.getSapid(), responseBean.getTrackId()
			});
		}catch(Exception e){
			
			throw e;
		}

	}

	public boolean updateTransactionStatusInExamBooking(MBAPaymentRequest bookingRequest) {
		String sql = ""
				+ " UPDATE `exam`.`mba_wx_bookings` "
                + " SET "
	                + " `bookingStatus` = ?, "
	                + " `lastUpdatedBy` = ?, "
	                + " `lastUpdatedOn` = sysdate() "
                + " WHERE `sapid` = ? AND `paymentRecordId` = ?";
		
		int numUpdated = jdbcTemplate.update(
			sql, 
			new Object[] { 
				bookingRequest.getBookingStatus(),
				bookingRequest.getLastModifiedBy(),
				bookingRequest.getSapid(),
				bookingRequest.getId()
			}
		);
		if(numUpdated == 0) {
			return false;
		}
		return true;
	}

	@Transactional(readOnly = true)
	public List<MBAExamBookingRequest> getAllInitiatedTransactionsForExamBooking() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM `exam`.`mba_wx_payment_records` WHERE `tranStatus` = ? AND `paymentType` = 'Exam Booking' AND createdDate < DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 MINUTE) ";
		
		List<MBAExamBookingRequest> listOfRequests = jdbcTemplate.query(
			sql, 
			new Object[]{ MBAPaymentRequest.TRAN_STATUS_INITIATED },
			new BeanPropertyRowMapper<MBAExamBookingRequest>(MBAExamBookingRequest.class)
		);
		return listOfRequests;
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void clearOldOnlineInitiationTransactionPeriodically(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`mba_wx_payment_records` "
				+ " SET `tranStatus` = ?, `lastModifiedBy` = ?, `lastModifiedDate` = sysdate() "

				+ " WHERE `id` NOT IN ( "
					+ " SELECT `paymentRecordId` "
						+ " FROM `exam`.`mba_wx_bookings` "
					+ " WHERE `bookingStatus` <> ? "
				+ " ) "
				+ " AND `tranStatus` = ? "
				+ " AND ((TIMESTAMPDIFF(second, `tranDateTime`, sysdate())) > 10800) ";


		try{
			int noOfRowsChanged = jdbcTemplate.update(
				sql, 
				new Object[] { 
					MBAPaymentRequest.TRAN_STATUS_EXPIRED, 
					"Expired Scheduler", 
					MBAExamBookingRequest.BOOKING_STATUS_BOOKED, 
					MBAPaymentRequest.TRAN_STATUS_INITIATED 
				}
			);


		}catch(Exception e){
			try{
				int noOfRowsChanged = jdbcTemplate.update(sql);
			}catch(Exception e2){
			}
		}
	}

	public void releaseBookingsForIds(List<String> bookingIdsList, String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String bookingIds = String.join(",", bookingIdsList);
		
		String sql = ""
				+ " UPDATE `exam`.`mba_wx_bookings` "
				
				+ " SET "
					+ " `bookingStatus` = ?, "
	                + " `lastUpdatedBy` = ?, "
	                + " `lastUpdatedOn` = sysdate() "
				+ " WHERE "
					+ " `bookingStatus` = ? "
					+ " AND `sapid` = ? "
					+ " AND `bookingId` IN ( " + bookingIds + " ) ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				MBAExamBookingRequest.BOOKING_STATUS_RELEASED,
				// Bookings released only by the student
				sapid,
				MBAExamBookingRequest.BOOKING_STATUS_BOOKED,
				sapid
			}
		);

	}
	
	public int approveOnlineTransactions(MBAExamBookingRequest responseBean) throws Exception{
		int noOfRowsUpdated = 0;
	
		int numOfPaymentRec = updateTransactionDetailsForManualAprrove(responseBean);
		int numOfBookingRec = updateTransactionStatusInExamBookingForManualAprrove(responseBean);
//		adding following condition because there can be differnce in numOfPaymentRec and numOfBookingRec, but we need
//		numOfPaymentRec
		if(numOfPaymentRec >0 && numOfBookingRec>0) {
			noOfRowsUpdated =  numOfPaymentRec;
		}
		return noOfRowsUpdated;
	}
	
	public int updateTransactionStatusInExamBookingForManualAprrove(MBAExamBookingRequest bookingRequest) {
		String sql = ""
				+ " UPDATE `exam`.`mba_wx_bookings` "
                + " SET `bookingStatus` = ?, "
                + " `lastUpdatedBy` = ? ,`lastUpdatedOn` = sysdate() "
                + " WHERE `sapid` = ? AND `paymentRecordId` = ?";
		int numUpdated = jdbcTemplate.update(
			sql, 
			new Object[] { 
				bookingRequest.getBookingStatus(),
				bookingRequest.getLastModifiedBy(),
				bookingRequest.getSapid(),
				bookingRequest.getPaymentRecordId()
			}
		);
		return numUpdated;
	}
	
	public int updateTransactionDetailsForManualAprrove(MBAPaymentRequest responseBean) {
		int numOfRecordsUpdated = 0;
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
					+ " UPDATE "
						+ " `exam`.`mba_wx_payment_records` "
                    + " SET "
	                    + " `responseMessage` = ?,  `transactionID` = ?,  `requestID` = ?, "
	                    + " `merchantRefNo` = ?, `secureHash` = ?, `respAmount` = ?, "
	                    + " `responseCode` = ?, `respPaymentMethod` = ?, `isFlagged` = ?, "
	                    + " `paymentID` = ?, `error` = ?, `respTranDateTime` = ?, "
	                    + " `tranStatus` = ?,  `bankName` = ?, "
	                    + " `lastModifiedBy` = ?,  `lastModifiedDate` = sysdate() "
                    + " WHERE "
                    	+ " `sapid` = ? "
                    + " AND `trackId` = ?";

			
			numOfRecordsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getResponseMessage(), responseBean.getTransactionID(), responseBean.getRequestID(),
					responseBean.getMerchantRefNo(), responseBean.getSecureHash(), responseBean.getRespAmount(),
					responseBean.getResponseCode(), responseBean.getRespPaymentMethod(), responseBean.getIsFlagged(),
					responseBean.getPaymentID(), responseBean.getError(), responseBean.getRespTranDateTime(),
					responseBean.getTranStatus(), responseBean.getBankName(), 
					responseBean.getLastModifiedBy(),
					
					responseBean.getSapid(), responseBean.getTrackId()
			});
		}catch(Exception e){
			
			throw e;
		}
		return numOfRecordsUpdated;

	}
	
	@Transactional(readOnly = true)
	public List<MBAPaymentRequest> getPaymentDetailsBySapidTrackId(final String sapid, final String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT `id`, `paymentType`, `paymentOption`, `amount`, `tranStatus` " + 
						"FROM `exam`.`mba_wx_payment_records` " + 
						"WHERE `sapid` = ?" + 
						"	AND `trackId` = ?";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(MBAPaymentRequest.class), sapid, trackId);
	}
	
	@Transactional(readOnly = true)
	public List<MBAPaymentRequest> getPaymentRecordsBySapidTrackIdTranStatus(final String sapid, final String trackId, final String tranStatus) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT id, paymentOption, trackId, amount, tranDateTime, description, source " + 
						"FROM exam.mba_wx_payment_records " + 
						"WHERE sapid = ?" + 
						"	AND trackId = ?" + 
						"	AND tranStatus = ?";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(MBAPaymentRequest.class), sapid, trackId, tranStatus);
	}
	
	@Transactional(readOnly = false)
	public long insertPaymentRecord(final String paymentType, final String sapid, final String paymentOption, final String trackId,
									final String amount, final String tranStatus, final String description, final String source) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "INSERT INTO `exam`.`mba_wx_payment_records` " + 
						"(`paymentType`, `sapid`, `paymentOption`, `trackId`, `amount`, `tranDateTime`, `tranStatus`, " + 
						"`description`, `source`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
						"VALUES (?, ?, ?, ?, ?, sysdate(), ?, ?, ?, ?, sysdate(), ?, sysdate())";
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, paymentType);
				statement.setString(2, sapid);
				statement.setString(3, paymentOption);
				statement.setString(4, trackId);
				statement.setString(5, amount);
				statement.setString(6, tranStatus);
				statement.setString(7, description);
				statement.setString(8, source);
				statement.setString(9, sapid);
				statement.setString(10, sapid);
				return statement;
			}
		}, keyHolder);
		
		return keyHolder.getKey().longValue();
	}
	
	@Transactional(readOnly = false)
	public int updatePaymentRecords(final MBAPaymentRequest paymentRequest, final String transactionStatus, 
									final String sapid, final String modifiedByUser) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE `exam`.`mba_wx_payment_records` " + 
						"SET `tranStatus` = ?, `transactionID` = ?, `requestID` = ?, `merchantRefNo` = ?," + 
						"	`secureHash` = ?, `respAmount` = ?, `responseCode` = ?, `respPaymentMethod` = ?," + 
						"	`isFlagged` = ?, `paymentID` = ?, `responseMessage` = ?, `error` = ?, `respTranDateTime` = ?," + 
						"	`lastModifiedBy` = ?, `lastModifiedDate` = sysdate(), `bankName` = ? " + 
						"WHERE `sapid` = ?" + 
						"	AND `trackId` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, transactionStatus);
				ps.setString(2, paymentRequest.getTransactionID());
				ps.setString(3, paymentRequest.getRequestID());
				ps.setString(4, paymentRequest.getMerchantRefNo());
				ps.setString(5, paymentRequest.getSecureHash());
				ps.setString(6, paymentRequest.getRespAmount());
				ps.setString(7, paymentRequest.getResponseCode());
				ps.setString(8, paymentRequest.getRespPaymentMethod());
				ps.setString(9, paymentRequest.getIsFlagged());
				ps.setString(10, paymentRequest.getPaymentID());
				ps.setString(11, paymentRequest.getResponseMessage());
				ps.setString(12, paymentRequest.getError());
				ps.setString(13, paymentRequest.getRespTranDateTime());
				ps.setString(14, modifiedByUser);
				ps.setString(15, paymentRequest.getBankName());
				ps.setString(16, sapid);
				ps.setString(17, paymentRequest.getTrackId());
			}
		});
	}
	
	@Transactional(readOnly = true)
	public List<MBAPaymentRequest> getAllTransactionsByTypeStatus(final List<String> paymentTypeList, final String tranStatus) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String query =  "SELECT * FROM `exam`.`mba_wx_payment_records` " + 
						"WHERE `paymentType` IN (:paymentTypes)" + 
						"	AND `tranStatus` = :tranStatus" + 
						"	AND `createdDate` < DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 MINUTE)";
		
		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("paymentTypes", paymentTypeList);
		parameterMap.addValue("tranStatus", tranStatus);
		
		return namedParameterJdbcTemplate.query(query, parameterMap, new BeanPropertyRowMapper<>(MBAPaymentRequest.class));
	}
	
	@Transactional(readOnly = true)
	public List<MBAPaymentRequest> getExpiredTransactionsByTypeStatus(final List<String> paymentTypeList, final String tranStatus) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String query =  "SELECT * FROM `exam`.`mba_wx_payment_records` " + 
						"WHERE `paymentType` IN (:paymentTypes)" + 
						"	AND `tranStatus` = :tranStatus" + 
						"	AND TIMESTAMPDIFF(second, `tranDateTime`, sysdate()) > 10800";			//10800 seconds ~ 180 minutes ~ 3 hours
		
		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("paymentTypes", paymentTypeList);
		parameterMap.addValue("tranStatus", tranStatus);
		
		return namedParameterJdbcTemplate.query(query, parameterMap, new BeanPropertyRowMapper<>(MBAPaymentRequest.class));
	}
	
	@Transactional(readOnly = false)
	public int updatePaymentRecord(final long id, final String tranStatus, final String modifiedByUser) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE `exam`.`mba_wx_payment_records` " + 
						"SET `tranStatus` = ?," + 
						"	`lastModifiedBy` = ?," + 
						"	`lastModifiedDate` = sysdate() " + 
						"WHERE `id` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, tranStatus);
				ps.setString(2, modifiedByUser);
				ps.setLong(3, id);
			}
		});
	}
}
