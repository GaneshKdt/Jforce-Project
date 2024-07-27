package com.nmims.daos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.TransactionStudentPortalBean;
import com.nmims.beans.WalletBean;
@Repository("walletDao")
public class WalletDAO {
	public DataSource dataSource;
	public JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	@Transactional(readOnly = true)
	public TransactionStudentPortalBean getApiParameters(String requestId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.api_request where requestId = ? ";
		TransactionStudentPortalBean tranBean = null;
		try{
			tranBean = (TransactionStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{requestId}, new BeanPropertyRowMapper(TransactionStudentPortalBean.class));
			return tranBean;

		}catch(Exception e){
			System.out.println("getApiParameters :"+e.getMessage());
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public WalletBean insertWalletRecordAndReturnRecord(String sapid){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO portal.wallet(sapid,balance,createdBy,createdDate,lastModifiedBy,lastModifiedDate) VALUES(?,?,?,sysdate(),?,sysdate()) ";

		try{
			jdbcTemplate.update(sql,new Object[]{sapid,"0.0",sapid,sapid});
			WalletBean studentWallet = getWalletRecord(sapid);
			return studentWallet;
		}catch(Exception e){
			System.out.println("insertWalletRecordAndReturnRecord catch");
			
			return null;
		}
	}

	@Transactional(readOnly = true)
	public WalletBean getWalletRecord(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.wallet where sapid = ? ";
		WalletBean walletRecord = null;
		try{
			walletRecord = (WalletBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(WalletBean.class));
			return walletRecord;
		}catch(Exception e){
			return null;
		}


	}
	@Transactional(readOnly = false)
	public void deleteApiRequest(String requestId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from portal.api_request where requestId = ? ";
		jdbcTemplate.update(sql, new Object[]{requestId});
	}

	@Transactional(readOnly = false)
	public void insertApiRequest(TransactionStudentPortalBean transactionBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO portal.api_request " + 
				" (requestId, " + 
				" channel, " + 
				" accountId, " + 
				" referenceNo, " + 
				" amount, " + 
				" mode, " + 
				" currency, " + 
				" cuurencyCode, " + 
				" description, " + 
				" returnUrl, " + 
				" name, " + 
				" address, " + 
				" city, " + 
				" country, " + 
				" postalCode, " + 
				" phone, " + 
				" email, " + 
				" sapid, " + 
				" lastModifiedDate, " + 
				" createdDate, " + 
				" lastModifiedBy, " + 
				" createdBy)"+
				" VALUES " + 
				" ( ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " +
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" sysdate(), " +
				" sysdate(), " +
				" ?, " + 
				" ?) ";


		jdbcTemplate.update(sql,new Object[]{transactionBean.getRequestId(),transactionBean.getChannel(),
				transactionBean.getAccountId(),transactionBean.getReferenceNo(),transactionBean.getAmount(),transactionBean.getMode(),
				transactionBean.getCurrency(),transactionBean.getCurrencyCode(),transactionBean.getDescription(),transactionBean.getReturnUrl(),
				transactionBean.getName(),transactionBean.getAddress(),transactionBean.getCity(),transactionBean.getCountry(),transactionBean.getPostalCode(),
				transactionBean.getPhone(),transactionBean.getEmail(),transactionBean.getSapid(),transactionBean.getSapid(),transactionBean.getSapid()});


	}
	
	@Transactional(readOnly = false)
	public void insertWMoneyLoad(WalletBean wallet){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO portal.wallet_money_load(sapid,trackId,amount,tranDateTime, "
				+ " tranStatus,paymentMode,transactionID,requestID,merchantRefNo,secureHash, "
				+ " respAmount,description,responseCode,paymentID,responseMessage,error,respTranDateTime, "
				+ " lastModifiedDate,createdDate,lastModifiedBy,createdBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?) ";
		jdbcTemplate.update(sql,new Object[]{wallet.getSapid(),wallet.getTrackId(),wallet.getAmount(),wallet.getTranDateTime(),WalletBean.ONLINE_PAYMENT_INITIATED,
						"Online",wallet.getTransactionID(),wallet.getRequestID(),wallet.getMerchantRefNo(),wallet.getSecureHash(),wallet.getRespAmount(),wallet.getDescription(),wallet.getResponseCode(),wallet.getPaymentID(),
						wallet.getResponseMessage(),wallet.getError(),wallet.getRespTranDateTime(),wallet.getSapid(),wallet.getSapid()});
	}

	@Transactional(readOnly = false)
	public long updateWMoneyLoadAndInsertWTransactionAndUpdateWBalance(WalletBean wallet, boolean updateInWalletMoneyLoadTable) throws Exception{
		Connection conn = dataSource.getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		long walletTransactionId = 0;
		try{
			conn.setAutoCommit(false);
			System.out.println("Wallet record in update :"+wallet);
			if(updateInWalletMoneyLoadTable){
				String sql = "Update portal.wallet_money_load "
						+ " set sapid = ? ,"
						+ " amount = ? ,"
						+ " tranDateTime = ?,"
						+ " tranStatus = ? ,"
						+ " paymentMode = ? ,"
						+ " bank = ? ,"
						+ " transactionID = ? ,"
						+ " requestID = ? ,"
						+ " merchantRefNo = ? ,"
						+ " secureHash = ? ,"
						+ " respAmount = ? ,"
						+ " description = ? ,"
						+ " responseCode = '0' ,"
						+ " respPaymentMethod = ? ,"
						+ " isFlagged = ? ,"
						+ " paymentID = ? ,"
						+ " responseMessage = ? ,"
						+ " error = ? ,"
						+ " respTranDateTime = ? ,"
						+ " lastModifiedDate = sysdate() ,"
						+ " createdDate = sysdate() ,"
						+ " lastModifiedBy = ? ,"
						+ " createdBy = ? "
						+ " where  trackId = ? ";
				
				ps = conn.prepareStatement(sql);
				
				ps.setString(1,wallet.getSapid());
				ps.setString(2,wallet.getAmount());
				ps.setString(3,wallet.getTranDateTime());
				ps.setString(4,wallet.getTranStatus());
				ps.setString(5,"Online");
				ps.setString(6,wallet.getBank());
				ps.setString(7,wallet.getTransactionID());
				ps.setString(8,wallet.getRequestID());
				ps.setString(9,wallet.getMerchantRefNo());
				ps.setString(10,wallet.getSecureHash());
				ps.setString(11,wallet.getRespAmount());
				ps.setString(12,wallet.getDescription());
				ps.setString(13,wallet.getRespPaymentMethod());
				ps.setString(14,wallet.getIsFlagged());
				ps.setString(15,wallet.getPaymentID());
				ps.setString(16,wallet.getResponseMessage());
				ps.setString(17,wallet.getError());
				ps.setString(18,wallet.getRespTranDateTime());
				ps.setString(19,wallet.getSapid());
				ps.setString(20,wallet.getSapid());
				ps.setString(21,wallet.getMerchantRefNo());

				

				int noOfRowsUpdated = ps.executeUpdate();

			}


			String tranSql = "INSERT INTO portal.wallet_transaction (walletId,tid,userId,transactionType, description, amount,walletBalance,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
					+ " VALUES(?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) ";

			ps = conn.prepareStatement(tranSql);
			ps.setString(1, wallet.getWalletId());
			ps.setString(2,wallet.getTid());
			ps.setString(3, wallet.getUserId());
			ps.setString(4, wallet.getTransactionType());
			ps.setString(5, wallet.getDescription());
			ps.setString(6, wallet.getAmount());
			ps.setString(7, wallet.getWalletBalance());
			ps.setString(8, wallet.getSapid());
			ps.setString(9, wallet.getSapid());

			int rowsUpdatedForWalletConfig = ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
	            if(rs != null && rs.next()){
	                System.out.println("Generated  Id: "+rs.getLong(1));
	                walletTransactionId = rs.getLong(1);
	            }

			System.out.println("rowsUpdatedForWalletConfig = "+rowsUpdatedForWalletConfig);

			if(rowsUpdatedForWalletConfig > 0){
				String updateWalletIdSql = "Update portal.wallet set balance = ? , lastModifiedBy = ? , lastModifiedDate = sysdate()  where sapid = ? and id = ? ";
				ps = conn.prepareStatement(updateWalletIdSql);
				ps.setString(1,wallet.getWalletBalance());
				ps.setString(2,wallet.getSapid());
				ps.setString(3,wallet.getSapid());
				ps.setLong(4,wallet.getId());

				int rowsUpdatedWalletId = ps.executeUpdate();

				System.out.println("rowsUpdatedWalletId = "+rowsUpdatedWalletId);
			}
			conn.commit();

		}catch(Exception e){
			System.out.println("Successful Rollback");
			
			conn.rollback();
			throw e;
		}finally{
			if(rs != null) rs.close();
			if(ps != null) ps.close();
			if(conn != null) conn.close();
		}
		return walletTransactionId;
	}

	@Transactional(readOnly = true)
	public ArrayList<WalletBean> getTransactionsUnderWalletRecord(Long id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.wallet_transaction where walletId = ? ";
		ArrayList<WalletBean> transactionsListOfWallet = new ArrayList<WalletBean>();
		try{
			transactionsListOfWallet = (ArrayList<WalletBean>)jdbcTemplate.query(sql, new Object[]{id},new BeanPropertyRowMapper(WalletBean.class));
		}catch(Exception e){
			
			return null;
		}
		return transactionsListOfWallet;
	}

	@Transactional(readOnly = true)
	public ArrayList<WalletBean> getAllTransactionsBetweenDates(WalletBean wallet){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String baseSql = " select * from portal.wallet_transaction where 1 = 1 ";
		ArrayList<WalletBean> transactionsListOfWallet = new ArrayList<WalletBean>();
		try{
			if(wallet.getStartDate()!=null && !"".equals(wallet.getStartDate()) && wallet.getEndDate()!=null && !"".equals(wallet.getEndDate())){
				baseSql = baseSql + "  and createdDate > ? and createdDate < ? ";
				transactionsListOfWallet = (ArrayList<WalletBean>)jdbcTemplate.query(baseSql,new Object[]{wallet.getStartDate(),wallet.getEndDate()},new BeanPropertyRowMapper(WalletBean.class));
			}
			if(wallet.getSapid()!=null && !"".equals(wallet.getSapid())){
				baseSql = baseSql + " and userId = ? ";
				transactionsListOfWallet = (ArrayList<WalletBean>)jdbcTemplate.query(baseSql,new Object[]{wallet.getSapid()},new BeanPropertyRowMapper(WalletBean.class));
			}
			if(wallet.getTransactionType() !=null && !"".equals(wallet.getTransactionType())){
				baseSql = baseSql + " and transactionType = ? ";
				transactionsListOfWallet = (ArrayList<WalletBean>)jdbcTemplate.query(baseSql,new Object[]{wallet.getTransactionType()},new BeanPropertyRowMapper(WalletBean.class));
			}
			System.out.println("baseSql :"+baseSql);
			return transactionsListOfWallet;
		}catch(Exception e){
			
			return null;
		}
		
	}



}
