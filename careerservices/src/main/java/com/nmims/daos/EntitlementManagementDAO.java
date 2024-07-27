package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nmims.beans.PackageEntitlementInfo;
import com.nmims.beans.StudentEntitlement;
import com.nmims.helpers.DataValidationHelpers;

public class EntitlementManagementDAO {

/*
 * 	-------------- INITIALIZATIONS --------------
 */
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	DataValidationHelpers validationHelper = new DataValidationHelpers();
	EntitlementCheckerDAO entitlementCheckerDAO = new EntitlementCheckerDAO();
/*
 * 	-------------------- END --------------------
 */

/*
 * 	CUD for EntitlementInfo
 */
	public boolean addInfoForPackageFeatureId(PackageEntitlementInfo packageEntitlementInfo) {
		
		if(packageEntitlementInfo.getEntitlementId() != null) {
			if(updateInfoForPackageFeatureId(packageEntitlementInfo)) {
				return true;
			}
		}else {
			if(insertInfoForPackageFeatureId(packageEntitlementInfo)) {
				return true;
			}
		}
		return false;
	}

	private boolean insertInfoForPackageFeatureId(PackageEntitlementInfo packageEntitlementInfo) {
		String sql = "INSERT INTO `products`.`entitlements_info`"
					+ "(`packageFeaturesId`, `requiresOtherEntitlement`, `extendByMax`, "
					+ "`requiresStudentActivation`, `totalActivations`, `initialActivations`, "
					+ "`initialCycleGapMonths`, `initialCycleGapDays`, `activationCycleMonths`, "
					+ "`activationCycleDays`, `activationsEveryCycle`, `extendIfActivationsLeft`) "
					+ "VALUES "
					+ "("
					+ "?, ?, ?, "
					+ "?, ?, ?, ? "
					+ "?, ?, ?, "
					+ "?"
					+ ")";
		
		
			jdbcTemplate.update(sql, new Object[] {
				packageEntitlementInfo.getPackageFeaturesId(), packageEntitlementInfo.isRequiresOtherEntitlement(), packageEntitlementInfo.getExtendByMaxMonths(), 
				packageEntitlementInfo.isRequiresStudentActivation(), packageEntitlementInfo.getTotalActivations(), packageEntitlementInfo.getInitialActivations(), 
				packageEntitlementInfo.getInitialCycleGapMonths(), packageEntitlementInfo.getInitialCycleGapDays(), packageEntitlementInfo.getActivationCycleMonths(), 
				packageEntitlementInfo.getActivationCycleDays(), packageEntitlementInfo.getActivationsEveryCycle(), packageEntitlementInfo.isExtendIfActivationsLeft()
			});
			return true;
		
	}
	
	private boolean updateInfoForPackageFeatureId(PackageEntitlementInfo packageEntitlementInfo) {
		String sql = "UPDATE"
				+ " `products`.`entitlements_info` "
				+ "SET "
				+ "`packageFeaturesId` = ?, "
				+ "`requiresOtherEntitlement` = ?, "
				+ "`extendByMax` = ?, "
				+ "`requiresStudentActivation` = ?, "
				+ "`totalActivations` = ?, "
				+ "`initialActivations` = ?, "
				+ "`initialCycleGapMonths` = ?, "
				+ "`initialCycleGapDays` = ?, "
				+ "`activationCycleMonths` = ?, "
				+ "`activationCycleDays` = ?, "
				+ "`activationsEveryCycle` = ?, "
				+ "`extendIfActivationsLeft` = ?"
				+ "WHERE"
				+ "`entitlementId`=?";
		
		
			jdbcTemplate.update(sql, new Object[] {
				packageEntitlementInfo.getPackageFeaturesId(), 
				packageEntitlementInfo.isRequiresOtherEntitlement(), 
				packageEntitlementInfo.getExtendByMaxMonths(), 
				packageEntitlementInfo.isRequiresStudentActivation(), 
				packageEntitlementInfo.getTotalActivations(), 
				packageEntitlementInfo.getInitialActivations(), 
				packageEntitlementInfo.getInitialCycleGapMonths(), 
				packageEntitlementInfo.getInitialCycleGapDays(), 
				packageEntitlementInfo.getActivationCycleMonths(), 
				packageEntitlementInfo.getActivationCycleDays(), 
				packageEntitlementInfo.getActivationsEveryCycle(), 
				packageEntitlementInfo.isExtendIfActivationsLeft(),
				packageEntitlementInfo.getEntitlementId()
			});
			return true;
		
	}

	public boolean deleteInfoForPackageFeatureId(String packageFeaturesId) {
		String sql = "DELETE "
				+ "FROM "
				+ "`products`.`entitlements_info` "
				+ "WHERE "
				+ "`packageFeaturesId` = ?";
		try {
			jdbcTemplate.update(sql, packageFeaturesId);
			return true;
		}catch(Exception e) {

			return false;	
		}
	}
	
/*
 * 	CUD for EntitlementInitial student data
 * 	this table stores the values that should be inserted in a new users
 */
	public boolean addInfoForEntitlementId(StudentEntitlement packageEntitlementInfo) {
		
		if(insertInfoForEntitlementId(packageEntitlementInfo)) {
			return true;
		}
		
		return false;
	}

	private boolean insertInfoForEntitlementId(StudentEntitlement studentEntitlementInfo) {
		String sql = "INSERT INTO "
				+ "`products`.`entitlements_initial_student_data` "
				  + "( "
				  + "`entitlementId`, `activated`, `activatedByStudent`, "
				  + "`activationDate`, `activationsLeft`, `ended`, "
				  + "`dateEnded` "
				  + ") "
				  + "VALUES "
				  + "( "
				  + "?, ?, ?, "
				  + "?, ?, ?, "
				  + "? "
				  + ")";
		
		
			jdbcTemplate.update(sql, new Object[] {
				studentEntitlementInfo.getEntitlementId(), studentEntitlementInfo.isActivated(), studentEntitlementInfo.isActivatedByStudent(), 
				studentEntitlementInfo.getActivationDate(), studentEntitlementInfo.getActivationsLeft(), studentEntitlementInfo.isEnded(), 
				studentEntitlementInfo.getDateEnded()
			});
			return true;
		
	}
	
	public boolean updateEntitlementStudentInfoFields(StudentEntitlement studentEntitlementInfo) {	
		return updateInfoForEntitlementId(studentEntitlementInfo);
	}

	private boolean updateInfoForEntitlementId(StudentEntitlement studentEntitlementInfo) {
		String sql = "UPDATE `products`.`entitlements_initial_student_data` "
				+ "SET "
				+ "`activated` = ?, "
				+ "`activatedByStudent` = ?, "
				+ "`activationDate` = "
				+ "`activationsLeft` = "
				+ "`ended` = "
				+ "`dateEnded` = "
				+ "`dateAdded` = "
				+ "WHERE "
				+ "`entitlementId` = ?";
		
			jdbcTemplate.update(sql, new Object[] {
					studentEntitlementInfo.isActivated(),
					studentEntitlementInfo.isActivatedByStudent(),
					studentEntitlementInfo.getActivationDate(),
					studentEntitlementInfo.getActivationsLeft(),
					studentEntitlementInfo.isEnded(),
					studentEntitlementInfo.getDateEnded(),
					studentEntitlementInfo.getDateAdded(),
					studentEntitlementInfo.getEntitlementId()
			});
			return true;
		
	}


/*
 * 	CU for Student entitlement data
 */	
	public boolean addEntitlementsForPurchase(String purchaseId) {

		//check if purchase is valid at all
		
		
		//get entitlementIds for this purchaseID	
			String sql = "SELECT "
					+ "`eisd`.* ,"
					+ "`ei`.`requiresStudentActivation` AS `requiresStudentActivation`, "
					+ "`sp`.`sapid` AS `sapid` "
					+ "FROM `products`.`student_packages` `sp`"
					+ "LEFT JOIN "
					+ "`products`.`packages` `packages` "
					+ "ON  "
					+ "`packages`.`salesForceUID` = `sp`.`salesForceUID` "
					+ "LEFT JOIN "
					+ "`products`.`package_features` `pf` "
					+ "ON  "
					+ "`pf`.`packageId` = `packages`.`packageId` "
					+ "LEFT JOIN "
					+ "`products`.`entitlements_info` `ei` "
					+ "ON  "
					+ "`ei`.`packageFeaturesId` = `pf`.`uid` "
					+ "LEFT JOIN "
					+ "`products`.`entitlements_initial_student_data` `eisd`"
					+ "ON "
					+ "`eisd`.`entitlementId` = `ei`.`entitlementId` "
					+ "WHERE "
					+ "`sp`.`paymentId` = ?"; 
			
			List<StudentEntitlement> listOfStudentEntitlements = jdbcTemplate.query(
					sql, 
					new Object[] { purchaseId }, 
					new RowMapper<StudentEntitlement>() {
						@Override
						public StudentEntitlement mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentEntitlement studentEntitlement = new StudentEntitlement();

							studentEntitlement.setPurchaseId(purchaseId);
							studentEntitlement.setSapid(rs.getString("sapid"));
							studentEntitlement.setEntitlementId(rs.getString("entitlementId"));
							studentEntitlement.setActivated(rs.getBoolean("activated"));
							studentEntitlement.setActivatedByStudent(rs.getBoolean("activatedByStudent"));
							studentEntitlement.setActivationsLeft(rs.getInt("activationsLeft"));
							studentEntitlement.setEnded(false);
							
							if(!rs.getBoolean("requiresStudentActivation")) {
								studentEntitlement.setActivationDate(new Date());
							}
								
							return studentEntitlement;
						}
					});

			//add student info to table
			for(StudentEntitlement studentEntitlement: listOfStudentEntitlements) {
				addStudentEntitlementData(studentEntitlement);
			}
			return true;
	}
		
/*
 * 	CU for Student entitlement data
 */
	private boolean addStudentEntitlementData(StudentEntitlement studentEntitlement) {
		int resultUid = checkIfStudentEntitlementDataExists(studentEntitlement);
		if(resultUid != 0) {
			return updateStudentEntitlementData(studentEntitlement, resultUid);
		}
		
		//if the activationDate is to be entered
		if(studentEntitlement.getActivationDate() != null) {

			String sql = "INSERT INTO `products`.`entitlements_student_data`"
					+ "(`sapid`, `purchaseId`,"
					+ "`entitlementId`, `activated`, "
					+ "`activatedByStudent`, `activationsLeft`, "
					+ "`ended`, `activationDate`) "
					+ "VALUES"
					+ "( "
					+ "?, ?, "
					+ "?, ?, "
					+ "?, ?, "
					+ "?, ?"
					+ ")";
		
			jdbcTemplate.update(sql, new Object[] {
				studentEntitlement.getSapid(), studentEntitlement.getPurchaseId(),
				studentEntitlement.getEntitlementId(), studentEntitlement.isActivated(), 
				studentEntitlement.isActivatedByStudent(), studentEntitlement.getActivationsLeft(), 
				studentEntitlement.isEnded(), studentEntitlement.getActivationDate()
			});
		}else {
			String sql = "INSERT INTO `products`.`entitlements_student_data`"
					+ "(`sapid`, `purchaseId`,"
					+ "`entitlementId`, `activated`, "
					+ "`activatedByStudent`, `activationsLeft`, "
					+ "`ended`) "
					+ "VALUES"
					+ "( "
					+ "?, ?, "
					+ "?, ?, "
					+ "?, ?, "
					+ "?"
					+ ")";
		
		
			jdbcTemplate.update(sql, new Object[] {
				studentEntitlement.getSapid(), studentEntitlement.getPurchaseId(),
				studentEntitlement.getEntitlementId(), studentEntitlement.isActivated(), 
				studentEntitlement.isActivatedByStudent(), studentEntitlement.getActivationsLeft(), 
				studentEntitlement.isEnded()
			});
		}
		
		return false;
	}
	
/*
 * 	If data already exists, this is a duplicate call. just reset the table data to reflect the current timestamp
 */
	private int checkIfStudentEntitlementDataExists(StudentEntitlement studentEntitlement) {
		String sql = "SELECT count(*) "
				+ "FROM `products`.`entitlements_student_data` "
				+ "WHERE "
				+ "`purchaseId` = ? "
				+ "AND "
				+ "`entitlementId` = ?";
		int resultUid = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { studentEntitlement.getPurchaseId(), studentEntitlement.getEntitlementId() }, 
				Integer.class);
		
		if(resultUid > 0) {
			return resultUid;
		}
		return 0;
	}

	private boolean updateStudentEntitlementData(StudentEntitlement studentEntitlement, int resultUid) {
		String sql = "UPDATE `products`.`entitlements_student_data` "
				+ "SET "
				+ "`activated` = ?, "
				+ "`activatedByStudent` = ?, "
				+ "`activationDate` = null, "
				+ "`activationsLeft` = ?, "
				+ "`ended` = ?, "
				+ "`dateEnded` = null, "
				+ "`dateAdded` = CURRENT_TIMESTAMP "
				+ "WHERE "
				+ "`uid` = ?";
		
		try {
			jdbcTemplate.update(
					sql,
					new Object[] { 
							studentEntitlement.isActivated(),
							studentEntitlement.isActivatedByStudent(),
							studentEntitlement.getActivationsLeft(),
							studentEntitlement.isEnded(),
							resultUid
							}
					);
			
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
}