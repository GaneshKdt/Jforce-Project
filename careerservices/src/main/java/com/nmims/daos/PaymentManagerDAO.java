package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import com.nmims.beans.PaymentDetails;
import com.nmims.beans.PackageBean;
import com.nmims.beans.PaymentGatewayConstants;
import com.nmims.beans.PaymentInitiationModelBean;
import com.nmims.helpers.DataValidationHelpers;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class PaymentManagerDAO {

	//initiate jdbc
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(PaymentManagerDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Value("${ENVIRONMENT}")
	String ENVIRONMENT;
	@Autowired
	private EntitlementManagementDAO entitlementManagementDAO;

	public boolean cancelPayment(String paymentId, String sapid) {
		String sql = ""
				+ "DELETE FROM "
					+ "`products`.`student_packages` "
				+ "WHERE "
					+ "`paymentId` = ?";
		try {
			jdbcTemplate.update(
					sql,
					new Object[] { paymentId });
			checkAndUpdateKeyInStudents(sapid);
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
		return true;
	}

	public boolean checkAndUpdateKeyInStudents(String sapid) {

		int numberOfPackagesPurchased = getNumberOfPurchasedPackages(sapid);

		if(!(numberOfPackagesPurchased > 0)) {
			return updateStudentPurchaseKeyInExamStudents(sapid, false);
		}
		return true;
	}

	private int getNumberOfPurchasedPackages(String sapid) {
		String sql = ""
				+ "SELECT "
					+ "COUNT(*) "
				+ "FROM "
					+ "`products`.`student_packages` "
				+ "WHERE "
					+ "sapid = ?";
		try {
			int count = jdbcTemplate.queryForObject(
					sql,
					new Object[] { sapid },
					Integer.class);

			return count;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return 0;
		}
	}

	public boolean checkIfPaymentIfExists(String paymentId) {
		return checkIfPaymentIfExistsQuery(paymentId);
	}

	private boolean checkIfPaymentIfExistsQuery(String paymentId) {
		String sql = ""
				+ "SELECT "
					+ "count(*) "
				+ "FROM "
					+ "`products`.`student_packages` "
				+ "WHERE "
					+ "`paymentId`=?";
		int result = jdbcTemplate.queryForObject(sql, new Object[] { paymentId }, Integer.class);
		if(result > 0) {
			return true;
		}
		return false;
	}

/*
 * 	----- These functions check for and add the packages bought at lead level -----
 */

	//Change status for this lead level purchase
	public void setSuccessfulPurchaseStatusForStudent(PaymentDetails paymentDetails) {
		String sql = ""
				+ "UPDATE "
					+ "`products`.`purchases_approved` "
				+ "SET "
					+ "`pending` = ?,"
					+ "`updatedBy` = ? "
				+ "WHERE "
					+ "`id` = ?";
		try {
			jdbcTemplate.update(sql, new Object[] {
					false, 
					paymentDetails.getSource(), 
					paymentDetails.getId()
			});
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
	}

/*
 *  ------------------------------------------------------ END ------------------------------------------------------
 */


/*
 * 	----- These functions work on the 'check for initiations before adding a package purchase for the student' -----
 */
	/*
	 * 	Check for pending transactions for this student
	 */
		public boolean checkNumberOfTransactions(String sapid) {
			String sql = ""
					+ "SELECT "
						+ "COUNT(*) "
					+ "FROM "
						+ "`products`.`purchases_payment_status_records` "
					+ "WHERE "
						+ "`sapid` = ? ";
	
			try {
				int count = jdbcTemplate.queryForObject(
						sql,
						new Object[] { 
							sapid
						},
						Integer.class
				);
				if(count == 0) {
					return true;
				}
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
			}
			return false;
		}

		public void checkIfStudentCanInitiateNewTransactions(PaymentInitiationModelBean paymentInitiationModelBean){
			String sapid = paymentInitiationModelBean.getSapid();
			
//			//if no pending transactions
//			if(checkNumberOfTransactions(sapid)) {
//				paymentInitiationModelBean.setPaymentPreviouslyInitiated(false);
//				return;
//			}
//
//			if(checkPendingTransactions(sapid)) {
//				return;
//			}

			String sql = ""
					+ " SELECT count(*) "
					+ " FROM `products`.`purchases_payment_status_records` "
					+ " WHERE "
						+ " `sapid` = ? "
					/* if status isnt initiated or started */
					+ " AND `status` IN (?, ?) "
					/* if payment not less than 15 mins old */
					+ " AND ((TIMESTAMPDIFF(second,`lastUpdateOn` , sysdate())) < 900) "
					/* if payment is more than 2 hours old */
					+ " AND ((TIMESTAMPDIFF(hour,`addedOn` , sysdate())) > 2) ";
				
			try {
				//this bean syncs with salesforce app
				int initiatedPayments =  jdbcTemplate.queryForObject(
					sql,
					new Object[] {
						sapid,
						PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_INITIATED,
						PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_STARTED
					}, 
					Integer.class
				);
				
				if(initiatedPayments == 0) {
					paymentInitiationModelBean.setPaymentPreviouslyInitiated(false);
					return;
				} else {
					paymentInitiationModelBean.setPaymentPreviouslyInitiated(true);
				}

			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				paymentInitiationModelBean.setPaymentPreviouslyInitiated(true);
			}
			return;
		}
		
		private boolean checkPendingTransactions(String sapid) {
			
			String sql = ""
					+ "SELECT "
						+ "count(*) "
					+ "FROM "
						+ "`products`.`purchases_payment_status_records` "
					+ "WHERE "
						+ "`sapid` = ? "
					+ "AND "
						+ "`status`"
					+ "IN "
						+ "("
							+ "?, ?"
						+ ")";
				
			try {
				//this bean syncs with salesforce app
				int count =  jdbcTemplate.queryForObject(
						sql,
						new Object[] {
							sapid,
							PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_INITIATED,
							PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_MADE
						}, Integer.class);

				if(count == 0) {
					return false;
				}
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
			}
			return true;
		}
	/*
	 * 	Initialize a transaction and return the bean
	 */
		
		private boolean initializeTransaction(String sapid, String salesForcePackageId, String source, String paymentId) {
	
			String sql = ""
					+ "INSERT INTO "
						+ "`products`.`purchases_payment_status_records` "
					+ "("
						+ "`status`, "
						+ "`sapid`, `salesForcePackageId`, "
						+ "`addedBySource`, `lastUpdateSource`, `paymentId`"
					+ ")"
					+ "VALUES"
					+ "("
						+ "?, "
						+ "?, ?, "
						+ "?, ?, ?"
					+ ") "
					//Change timestamp to current
					+ "ON "
					+ "DUPLICATE KEY "
						+ "UPDATE "
						+ "`lastUpdateSource` = ?, "
						+ "`lastUpdateOn` = CURRENT_TIMESTAMP";
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] {
								PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_STARTED, 
								sapid, salesForcePackageId, 
								source, source, paymentId,
								source
		            }
				);
				return true;
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
			}
			return false;
		}

			public boolean addInitializedTransaction(PaymentInitiationModelBean paymentInitiationModelBean) {
				return initializeTransaction(
							paymentInitiationModelBean.getSapid(), 
							paymentInitiationModelBean.getSalesForcePackageId(), 
							paymentInitiationModelBean.getSource(), 
							paymentInitiationModelBean.getPaymentInitializedId()
						);
			}
	
	/*
	 * 	Change state from initiated
	 */
		public void changePaymentRecordStatus(String status, String lastUpdateSource, String message, String sapid, String recordId, String merchantTrackId) {
			String sql = ""
					+ "UPDATE "
						+ "`products`.`purchases_payment_status_records` "
					+ "SET "
						+ "`status` = ?, "
						+ "`lastUpdateSource` = ?, "
						+ "`message` = ?, "
						+ "`merchantTrackId` = ? "
					+ "WHERE "
						+ "`sapid` = ? "
					+ "AND "
						+ "`paymentId` = ?";
			
			jdbcTemplate.update(
					sql, 
					new Object[] {
						status, lastUpdateSource, message, merchantTrackId, sapid, recordId
					}
			);
		}

/*
 *  ------------------------------------------------------ END ------------------------------------------------------
 */

	

/*
 * 	----------- To add a package for a student -----------
 */


		/*
		 * 	This method is used to store the constraints as when the payment returns as success from SFDC but fails at the portal.
		 */
			public void addFailedProductIntimation(PaymentDetails paymentDetails) {
				String sql = ""
							+ "INSERT INTO `products`.`purchases_failed_initiations`"
							+ "( "
								+ "`resolved`, `paymentInitializedId`, `sapid`,"
								+ "`packageId`, `message`, `paymentTrackId`,"
								+ "`status`, `checkSumHash`, `checkSumStatus`,"
								+ "`packageAddStatus`, `entitlementAddStatus`,"
								+ "`addedBy`, `updatedBy`)"
							+ "VALUES"
							+ "("
								+ "?, ?, ?, "
								+ "?, ?, ?, "
								+ "?, ?, ?, "
								+ "?, ?, "
								+ "?, ? "
							+ ");";
				
				jdbcTemplate.update(
						sql, 
						new Object[] {
							false,  paymentDetails.getPaymentInitializationId(), paymentDetails.getSapid(),
							paymentDetails.getPackageId(), paymentDetails.getMessage(), paymentDetails.getPaymentId(),
							paymentDetails.getStatus(), paymentDetails.getCheckSumHash(), paymentDetails.isCheckSumStatus(), 
							paymentDetails.isPackageAddStatus(), paymentDetails.isEntitlementAddStatus(), 
							paymentDetails.getSource(), paymentDetails.getSource()
						}
				);
			}
		
		/*
		 * 	Add payment details to db
		 */
			//This whole process might fail at any point. revert changes if it does.
			@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
			public void addPayment(PaymentDetails paymentResponse) throws Exception {
			
				//check if the payment id is duplicate
				if(checkIfPaymentIfExists(paymentResponse.getPaymentId())) {
					paymentResponse.setPaymentStatus(false);
					paymentResponse.setReasonForFail("Transaction details already present");
					throw new Exception("Transaction details already present");
				}
				//add the purchase details to database
				
				paymentResponse.setReasonForFail("Error adding transaction");
				if(!addPurchaseForStudent(paymentResponse)) {
					paymentResponse.setPaymentStatus(false);
					throw new Exception("Error adding transaction");
				}
				paymentResponse.setPackageAddStatus(true);
	
				paymentResponse.setReasonForFail("Error adding entitlements");
				if(!addEntitlementDetails(paymentResponse)) {
					paymentResponse.setPaymentStatus(false);
					throw new Exception("Error adding entitlements");
				}
				paymentResponse.setReasonForFail(null);
				paymentResponse.setEntitlementAddStatus(true);
				paymentResponse.setPaymentStatus(true);
			}
	
	/*
	 * 	Add entitlements for the user for this purchase
	 */
		private boolean addEntitlementDetails(PaymentDetails paymentResponse) {
			return entitlementManagementDAO.addEntitlementsForPurchase(paymentResponse.getPaymentId());
		}
		private boolean addPurchaseForStudent(PaymentDetails paymentDetails) {

			String salesForceUID = paymentDetails.getPackageId();
			String sapid = paymentDetails.getSapid();
			String paymentId = paymentDetails.getPaymentId();
			DataValidationHelpers validationHelper = new DataValidationHelpers();
			PackageBean packageBean = getPackageFromSalesForceUID(paymentDetails.getPackageId());
			int months = packageBean.getDurationMax();
			
			Date endDate = validationHelper.addMonthsToDate(validationHelper.getCurrentDate(), months);
	
			String sql = "INSERT INTO "
						+ "`products`.`student_packages` "
					+ "( "
						+ "`salesForceUID`, `sapid`, `paymentId`, "
						+ "`endDate` "
					+ ") "
					+ "VALUES "
					+ "("
						+ "?, ?, ?, "
						+ "? "
					+ ")";
	
			jdbcTemplate.update(sql, new Object[] { salesForceUID, sapid, paymentId, endDate });
			return updateStudentPurchaseKeyInExamStudents(sapid, true);
		}

		private boolean updateStudentPurchaseKeyInExamStudents(String sapid, boolean value) {
			String sql = ""
					+ "UPDATE "
						+ "`exam`.`students` "
					+ "SET "
						+ "`purchasedOtherPackages` = ?, "
						+ "`lastModifiedDate` = CURDATE() "
					+ "WHERE "
						+ "`sapid` = ?";
	
			try {
				jdbcTemplate.update(sql, new Object[] { value, sapid });
				return true;
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
			}
			return false;
		}
	
		private PackageBean getPackageFromSalesForceUID(String salesForceUID) {
	
			String sql = ""
					+ "SELECT "
						+ "* "
					+ "FROM "
						+ "`products`.`packages` "
					+ "WHERE "
						+ "`salesForceUID` = ?";
	
			try {
				PackageBean packageBean = jdbcTemplate.queryForObject(
					sql,
					new Object[] { salesForceUID },
					new BeanPropertyRowMapper<PackageBean>(PackageBean.class));
				return packageBean;
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				return new PackageBean();
			}
		}
		
		
		public List<PaymentDetails> getListOfPendingAdditions() {
			String sql = ""
					+ "SELECT "
						+ "*, `paymentRecordId` AS `paymentId` "
					+ "FROM "
						+ "`products`.`purchases_approved` "
					+ "WHERE "
						+ "pending = 1";
			try {
				List<PaymentDetails> results = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<PaymentDetails>(PaymentDetails.class)
				);

				return results;
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				return null;
			}
		}
		
		
		public List<PaymentDetails> getPendingPaymentToSynch(PartnerConnection connection) throws ConnectionException {
			
			ArrayList<PaymentDetails> detials = new ArrayList<>();
			
			String query = ""
					+ "SELECT "
//					+ " Id, "
//					+ " nm_Amount__c, "
					+ " nm_Merchant_Track_Id__c, "
//					+ " nm_PaymentStatus__c, "
					+ " CareerServiceProgram__r.Student_Number_Of_Account__c, "
					+ " CareerServiceProgram__r.CareerServiceProgram__c, "
//					+ " CareerServiceProgram__r.Key__c, "
//					+ " CareerServiceProgram__r.CareerServiceProgram__r.Package_Name__c, "
//					+ " CareerServiceProgram__r.CareerServiceProgram__r.Group_Name__c, "
					+ " CSPID__c "
					+ " FROM nm_Payment__c "
					+ " WHERE nm_PaymentStatus__c='Payment Approved'"
					+ " AND nm_PaymentType__c='Career Service' "
					+ " AND CareerServiceProgram__c != null "
					+ " AND CareerServiceProgram__r.Stage__c = 'Payment Done'";

			QueryResult qResult = connection.query(query);
			SObject[] records = qResult.getRecords();
			for(SObject record : records) {
				
				PaymentDetails payment = new PaymentDetails();
				if(StringUtils.isNumeric((String)record.getChild("CareerServiceProgram__r").getField("Student_Number_Of_Account__c"))) {
					payment.setPaymentId((String)record.getField("CSPID__c"));
					payment.setSapid((String)record.getChild("CareerServiceProgram__r").getField("Student_Number_Of_Account__c"));
					payment.setPackageId((String)record.getChild("CareerServiceProgram__r").getField("CareerServiceProgram__c"));
					payment.setMerchantTrackId((String)record.getField("nm_Merchant_Track_Id__c"));
					payment.setPaymentStatus(true);
					
					try {
						detials.add(payment);
					}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
				}
			}

			return detials;
		}
		
		public boolean checkIfRecordExist(PaymentDetails payment) throws Exception{
			
			String sql = "SELECT count(id)>0 FROM products.purchases_approved "
					+ "WHERE sapid=? AND paymentRecordId=? "; 
			
			boolean exist = jdbcTemplate.queryForObject( sql, new Object[] { payment.getSapid(), payment.getPaymentId()}, Boolean.class);
			return exist;
			
		}
		
		public void addSyncDetials(final PaymentDetails payment) throws Exception{

			String sql = "INSERT INTO `products`.`purchases_approved` " + 
					"(`sapid`,`packageId`,`paymentRecordId`,`merchantTrackId`, " + 
					"`pending`,`addedBy`,`addedOn`,`updatedBy`,`updatedOn`) " + 
					"VALUES " + 
					"(?,?,?,?,?, 'Scheduler', sysdate() , 'Scheduler', sysdate());";

			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, payment.getSapid());
			        statement.setString(2, payment.getPackageId());
			        statement.setString(3, payment.getPaymentId());
			        statement.setString(4, payment.getMerchantTrackId());
			        statement.setBoolean(5, payment.isPaymentStatus());
			        return statement;
			    }
			}, holder);
			
			return;
		}
/*
 * 	---------------------- END ----------------------
 */
		
}
