package com.nmims.paymentgateways.dao;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.helper.ExceptionHelper;

@Repository
public class TransactionDAO {

	public static final String TRAN_STATUS_INITIATED = "Initiated";
	public static final String TRAN_STATUS_SUCCESSFUL = "Payment Successfull";
	public static final String TRAN_STATUS_FAILED = "Failed";
	public static final String TRAN_STATUS_CANCELLED = "Payment Cancelled";
	public static final String REQUEST_STATUS_SUCCESSFUL = "Submitted";
	public static final String REQUEST_STATUS_FAILED = "Payment Failed";
	public static final String REQUEST_STATUS_CANCELLED = "Payment Cancelled";

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	public ArrayList<TransactionsBean> getInitiatedPaymentList(){
		try {
			String sql = "select * from payment_gateway.transaction where transaction_status = 'Initiated' and (time_to_sec(timediff(sysdate(), created_at)) > 1800);";
			return (ArrayList<TransactionsBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper<TransactionsBean>(TransactionsBean.class));
		} catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
	}

	public boolean initiateTransaction(TransactionsBean transactionsBean) {
		try {
			System.out.println("hey i m here");
			String sql = "insert into `payment_gateway`.`transaction` (track_id, sapid, type, amount,transaction_status, secure_hash, description, payment_option, source,created_by,updated_by) values(?,?,?,?,?,?,?,?,?,?,?)";
			int result = jdbcTemplate.update(sql,
					new Object[] { transactionsBean.getTrack_id(), 
							transactionsBean.getSapid(),
							transactionsBean.getType(), 
							transactionsBean.getAmount(), 
							TRAN_STATUS_INITIATED,
							transactionsBean.getSecure_hash(), 
							transactionsBean.getDescription(),
							transactionsBean.getPayment_option(), 
							transactionsBean.getSource(),
							transactionsBean.getCreated_by(), 
							transactionsBean.getCreated_by() });
			if (result > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			exceptionHelper.createLog(e);
			return false;
		}
	}

	@Transactional
	public boolean markAsSuccessTransaction(TransactionsBean transactionsBean) {
		try {

			String sql = "UPDATE `payment_gateway`.`transaction` SET `transaction_status`=?, `transaction_id`=?, `request_id`=?, "
					+ "`merchant_ref_no`=?, `response_amount`=?, `response_code`=?, `response_payment_method`=?, "
					+ "`response_message`=?, `response_transaction_date_time`=?, `is_flagged`=?, "
					+ "`payment_id`=?, `bank_name`=?, `updated_by`=?, `secure_hash` = ? WHERE `track_id`=?;";
			jdbcTemplate.update(sql,
					new Object[] { TRAN_STATUS_SUCCESSFUL, 
							transactionsBean.getTransaction_id(),
							transactionsBean.getRequest_id(), 
							transactionsBean.getMerchant_ref_no(),
							transactionsBean.getResponse_amount(), 
							transactionsBean.getResponse_code(),
							transactionsBean.getResponse_payment_method(), 
							transactionsBean.getResponse_message(),
							transactionsBean.getResponse_transaction_date_time(), 
							transactionsBean.getIs_flagged(),
							transactionsBean.getPayment_id(), 
							transactionsBean.getBank_name(),
							transactionsBean.getUpdated_by(),
							transactionsBean.getSecure_hash(),
							transactionsBean.getTrack_id() });

//			sql = "UPDATE `portal`.`service_request_new` SET `tranStatus` = ?,`requestStatus` = ? where trackId = ? ";
//			jdbcTemplate.update(sql, new Object[] {
//				TRAN_STATUS_SUCCESSFUL,
//				REQUEST_STATUS_SUCCESSFUL,
//				transactionsBean.getTrack_id()
//			});

			return true;

		} catch (Exception e) {
			exceptionHelper.createLog(e);
			e.printStackTrace();
			return false;
		}

	}


	@Transactional
	public boolean markAsFailedTransaction(TransactionsBean transactionsBean) {
		try {
			String sql = "UPDATE `payment_gateway`.`transaction` SET `transaction_status`=?, `response_code`=?,"
					+ "`response_message`=?, `error`=?, `updated_by`=? " + "WHERE `track_id`=?;";
			jdbcTemplate.update(sql,
					new Object[] { TRAN_STATUS_FAILED, 
							transactionsBean.getResponse_code(),
							transactionsBean.getResponse_message(), 
							transactionsBean.getError(),
							transactionsBean.getUpdated_by(), 
							transactionsBean.getTrack_id() });
//			sql = "UPDATE `payment_gateway`.`service_request_new` SET `tranStatus` = ?,`requestStatus` = ? where trackId = ? ";
//			jdbcTemplate.update(sql, new Object[] {
//				TRAN_STATUS_FAILED,
//				REQUEST_STATUS_FAILED,
//				transactionsBean.getTrack_id()
//			}); 
			return true;

		} catch (Exception e) {
			exceptionHelper.createLog(e);
			return false;
		}
	}

	@Transactional
	public boolean markAsCancelledTransaction(String trackId) {
		String sql = "UPDATE `payment_gateway`.`transaction` SET `transaction_status`=?, `error` = ? WHERE `track_id` = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] { TRAN_STATUS_FAILED, TRAN_STATUS_CANCELLED, trackId });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional
	public TransactionsBean getPaymentDetailsByTrackId(String track_id) {
		String sql = "SELECT `track_id`, `sapid`, `type`, `amount`, `transaction_status`, `description`, `payment_option` from `payment_gateway`.`transaction` where `track_id` = ?";
		TransactionsBean bean = new  TransactionsBean();
		try {
			bean = (TransactionsBean) jdbcTemplate.queryForObject(sql,  new Object[] {track_id}, new BeanPropertyRowMapper<TransactionsBean>(TransactionsBean.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bean;
	}

	@Transactional(readOnly = true)
	public TransactionsBean getPaymentOptionByTrackId(String track_id) throws EmptyResultDataAccessException, Exception  {
		paymentSchedulerLogger.info("TransactionDAO.getPaymentOptionByTrackId() - START");
		TransactionsBean txnBean= new TransactionsBean();
		
		String sql = "select * from payment_gateway.transaction where track_id=? ";
		try {
			txnBean = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<TransactionsBean>(TransactionsBean.class), track_id);
		}
		catch (EmptyResultDataAccessException erdae) {
			throw new IllegalArgumentException("No transaction found for trackId: '"+track_id+"' in payment gateways.");
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			throw e;
		}
		paymentSchedulerLogger.info("TransactionDAO.getPaymentOptionByTrackId() - END");
		return txnBean;
	}

	@Transactional(readOnly = false)
	public int updateTransactionDetails(TransactionsBean transactionBean) {
		paymentSchedulerLogger.info("TransactionDAO.updateTransactionDetails() - START");
		int updatedCount=0;
		
		String SQL = "UPDATE payment_gateway.transaction set transaction_status=?,transaction_id=?,request_id=?,merchant_ref_no=?,secure_hash=?,response_amount=?,response_code=?, "+
				"response_payment_method=?,response_message=?,response_transaction_date_time=?,error=?,payment_id=?, "+
				"bank_name=?,updated_by='AutoProjectBooking Scheduler',updated_at=sysdate() where track_id=? ";
		
		try {
			updatedCount=jdbcTemplate.update(SQL,transactionBean.getTransaction_status(),transactionBean.getTransaction_id(),transactionBean.getRequest_id(),transactionBean.getMerchant_ref_no(),transactionBean.getSecure_hash(),
					transactionBean.getResponse_amount(), transactionBean.getResponse_code(), transactionBean.getResponse_payment_method(), transactionBean.getResponse_message(),
					transactionBean.getResponse_transaction_date_time(), transactionBean.getError(), transactionBean.getPayment_id(),transactionBean.getBank_name(), transactionBean.getTrack_id());
		} catch (Exception e) {
			paymentSchedulerLogger.error("Error in saving transaction details. Error Message:"+e.getMessage());
			exceptionHelper.createLog(e);
		}
		
		paymentSchedulerLogger.info("TransactionDAO.updateTransactionDetails() - END");
		return updatedCount;
	}
	
	
}
