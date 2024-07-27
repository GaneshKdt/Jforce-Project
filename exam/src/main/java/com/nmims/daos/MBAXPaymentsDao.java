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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.MBAStudentSubjectMarksDetailsBean;

@Repository("mbaxPaymentsDao")
public class MBAXPaymentsDao  extends BaseDAO{

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
	public boolean insertExamBooking(MBAStudentSubjectMarksDetailsBean selectedSubject, Long id, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
				+ " INSERT INTO `exam`.`mba_x_bookings` "
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
			+ " SELECT *, `pr`.`id` AS `paymentRecordId` FROM `exam`.`mba_x_bookings` `b` "
			+ " INNER JOIN `exam`.`mba_x_payment_records` `pr` ON `b`.`paymentRecordId` = `pr`.`id` "
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
			+ " SELECT * FROM `exam`.`mba_x_payment_records` "
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
	
	@Transactional(readOnly = false)
	public void updateTransactionDetails(MBAPaymentRequest responseBean) {
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
					+ " UPDATE "
						+ " `exam`.`mba_x_payment_records` "
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
	
	@Transactional(readOnly = false)
	public boolean updateTransactionStatusInExamBooking(MBAPaymentRequest bookingRequest) {
		String sql = ""
				+ " UPDATE `exam`.`mba_x_bookings` "
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
		String sql = " SELECT * FROM `exam`.`mba_x_payment_records` WHERE `tranStatus` = ? AND `paymentType` = 'Exam Booking' AND createdDate < DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 MINUTE)";
		
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
				+ " UPDATE `exam`.`mba_x_payment_records` "
				+ " SET `tranStatus` = ?, `lastModifiedBy` = ?, `lastModifiedDate` = sysdate() "

				+ " WHERE `id` NOT IN ( "
					+ " SELECT `paymentRecordId` "
						+ " FROM `exam`.`mba_x_bookings` "
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
	
	@Transactional(readOnly = false)
	public void releaseBookingsForIds(List<String> bookingIdsList, String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String bookingIds = String.join(",", bookingIdsList);
		
		String sql = ""
				+ " UPDATE `exam`.`mba_x_bookings` "
				
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
	
	@Transactional(readOnly = false)
	public int updateTransactionStatusInExamBookingForManualAprrove(MBAExamBookingRequest bookingRequest) {
		String sql = ""
				+ " UPDATE `exam`.`mba_x_bookings` "
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
	
	@Transactional(readOnly = false)
	public int updateTransactionDetailsForManualAprrove(MBAPaymentRequest responseBean) {
		int numOfRecordsUpdated = 0;
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
					+ " UPDATE "
						+ " `exam`.`mba_x_payment_records` "
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
	
	

}
