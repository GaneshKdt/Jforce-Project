package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Repository
public class RescheduledCancelledSlotChangeReportDAOImpl implements RescheduledCancelledSlotChangeReportDAO{
	
	/*Variables*/
	@Autowired
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private final String SEAT_RELEASED = "Seat Released";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	private final String SEAT_RELEASED_SUBJECT_CLEARED = "Seat Released - Subject Cleared";
	private final String SEAT_CANCELLED_WITH_REFUND = "Cancellation With Refund";
	private final String SEAT_CANCELLED_WITHOUT_REFUND = "Cancellation Without Refund";
	
	
	
	/*Implemented DAO Methods*/
	@Override
	public List<RescheduledCancelledSlotChangeReportBean> getAllRLList() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "" +
				"SELECT " + 
				"    sapid," + 
				"    subject," + 
				"    examDate," + 
				"    examTime," +
				"    program," + 
				"    tranStatus," + 
				"    booked," + 
				"    createdDate " +
				"FROM " + 
				"    exam.exambookings_audit " + 
				"WHERE " + 
				"    booked = 'RL'";
				//"	 AND year = '2022' " + 
				//"	 AND month = 'Dec' ";
		
		List<RescheduledCancelledSlotChangeReportBean> allRLList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(RescheduledCancelledSlotChangeReportBean.class));
		
		return allRLList;
	}
	
	@Override
	public List<RescheduledCancelledSlotChangeReportBean> getAllCLList() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "" +
				"SELECT " + 
				"    sapid," + 
				"    subject," + 
				"    examDate," + 
				"    examTime," + 
				"    program," + 
				"    tranStatus," + 
				"    booked," + 
				"    createdDate " + 
				"FROM " + 
				"    exam.exambookings_audit " + 
				"WHERE " + 
				"    booked = 'CL'" +
				"    OR tranStatus = 'Cancellation With Refund'";
				//"	 AND year = '2022' " + 
				//"	 AND month = 'Dec' ";
		
		List<RescheduledCancelledSlotChangeReportBean> allCLList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(RescheduledCancelledSlotChangeReportBean.class));
		
		return allCLList;
	}
	
	@Override
	public List<RescheduledCancelledSlotChangeReportBean> getAllICList() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "" +
				"SELECT " + 
				"    sapid," +
				"    centerName AS ic," + 
				"    centerCode " + 
				"FROM " + 
				"    exam.students ";
		
		List<RescheduledCancelledSlotChangeReportBean> allICList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(RescheduledCancelledSlotChangeReportBean.class));
		
		return allICList;
	}

	@Override
	public List<RescheduledCancelledSlotChangeReportBean> getAllLCList() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "" +
				" SELECT" + 
				"    centerCode, lc" + 
				" FROM" + 
				"    `exam`.`centers`" +
				" WHERE centerCode <> '' ";
		
		List<RescheduledCancelledSlotChangeReportBean> allLCList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(RescheduledCancelledSlotChangeReportBean.class));
		
		return allLCList;
	}
	
	@Override
	public int batchInsertExamBookingAudit(List<ExamBookingTransactionBean> toReleaseSubjectList, 
			String releasedStatus, String createdBy)
			throws Exception 
	{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String status = "";
		String booked = "RL";
		String concatenate = "";
		//Set tranStatus
		if ("Passed".equalsIgnoreCase(releasedStatus)) {
			status = SEAT_RELEASED_SUBJECT_CLEARED;
		} else if ("true".equalsIgnoreCase(releasedStatus)) {
			status = SEAT_RELEASED_NO_CHARGES;
		} else if ("false".equalsIgnoreCase(releasedStatus)) {
			status = SEAT_RELEASED;
		}
		//Set tranStatus and booked for Cancel Seats
		else if ("canceltrue".equalsIgnoreCase(releasedStatus)) {
			status = SEAT_CANCELLED_WITH_REFUND;
			booked="N";
			concatenate=" to be refunded";
		}
		else if ("cancelfalse".equalsIgnoreCase(releasedStatus)) {
			status = SEAT_CANCELLED_WITHOUT_REFUND;
			booked="CL";
		}
		
		//Created to append some static text on description if "SEAT_CANCELLED_WITH_REFUND"
		final String descriptionConcat = concatenate;
		
		String query = " INSERT INTO exam.exambookings_audit "
				+ "(sapid,"
				+ " subject,"
				+ " year, "
				+ " month, "
				+ " program, "
				+ " sem, "
				+ " trackId, "
				+ " amount, "
				+ " tranDateTime, "
				+ " tranStatus, "
				+ " booked,"
				+ " paymentMode,"
				+ " ddno,"
				+ " bank,"
				+ " ddAmount,"
				+ " centerId,"
				+ " examDate,"
				+ " examTime,"
				+ " examMode,"
				+ " transactionId,"
				+ " requestId,"
				+ " merchantRefNo,"
				+ " secureHash,"
				+ " respAmount,"
				+ " description,"
				+ " responseCode,"
				+ " respPaymentMethod,"
				+ " isFlagged,"
				+ " paymentID,"
				+ " responseMessage,"
				+ " error,"
				+ " respTranDateTime,"
				+ " ddReason,"
				+ " ddDate,"
				+ " bookingCompleteTime,"
				+ " htDownloaded,"
				+ " lastModifiedDate,"
				+ " lastModifiedBy,"
				+ " password,"
				+ " htDownloadedDate,"
				+ " paymentOption,"
				+ " bankName,"
				+ " emailId,"
				+ " testTaken,"
				+ " releaseReason,"
				+ " action,"
				+ " createdBy,"
				+ " createdDate)"

			+ "	VALUES(?,?,?,?,?,?,?,?,?,'"+status+"','"+booked+"',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),'"+createdBy+"',?,?,?,?,?,?,?,'UPDATE','"+createdBy+"',sysdate())";
		
		int[] rowCount = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExamBookingTransactionBean bean = toReleaseSubjectList.get(i);
				ps.setString(1, bean.getSapid());
				ps.setString(2, bean.getSubject());
				ps.setString(3, bean.getYear());
				ps.setString(4, bean.getMonth());
				ps.setString(5, bean.getProgram());
				ps.setString(6, bean.getSem());
				ps.setString(7, bean.getTrackId());
				ps.setString(8, bean.getAmount());
				ps.setString(9, bean.getTranDateTime());
				ps.setString(10, bean.getPaymentMode());
				ps.setString(11, bean.getDdno());
				ps.setString(12, bean.getBank());
				ps.setString(13, bean.getDdAmount());
				ps.setString(14, bean.getCenterId());
				ps.setString(15, bean.getExamDate());
				ps.setString(16, bean.getExamTime());
				ps.setString(17, bean.getExamMode());
				ps.setString(18, bean.getTransactionID());
				ps.setString(19, bean.getRequestID());
				ps.setString(20, bean.getMerchantRefNo());
				ps.setString(21, bean.getSecureHash());
				ps.setString(22, bean.getRespAmount());
				ps.setString(23, bean.getDescription()+descriptionConcat);
				ps.setString(24, bean.getResponseCode());
				ps.setString(25, bean.getRespPaymentMethod());
				ps.setString(26, bean.getIsFlagged());
				ps.setString(27, bean.getPaymentID());
				ps.setString(28, bean.getResponseMessage());
				ps.setString(29, bean.getError());
				ps.setString(30, bean.getRespTranDateTime());
				ps.setString(31, bean.getDdReason());
				ps.setString(32, bean.getDdDate());
				ps.setString(33, bean.getBookingCompleteTime());
				ps.setString(34, bean.getHtDownloaded());
				ps.setString(35, bean.getPassword());
				ps.setString(36, bean.getHtDownloaded());
				ps.setString(37, bean.getPaymentOption());
				ps.setString(38, bean.getBankName());
				ps.setString(39, bean.getEmailId());
				ps.setString(40, bean.getTestTaken());
				ps.setString(41, bean.getReleaseReason());
			}

			public int getBatchSize() {
				return toReleaseSubjectList.size();
			}
		});
		
		return rowCount.length;
	}
	
}
