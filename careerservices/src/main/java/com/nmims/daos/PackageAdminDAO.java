package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.google.gson.Gson;
import java.sql.Statement;

import com.nmims.beans.ReturnStatus;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.Feature;
import com.nmims.beans.PackageBean;
import com.nmims.beans.PackageFamily;
import com.nmims.beans.PackageFeature;
import com.nmims.beans.PackageRequirements;
import com.nmims.beans.PackageRequirementsMasterMapping;
import com.nmims.beans.PackageRequirementsMasterMappingData;
import com.nmims.beans.StudentPackageBean;
import com.nmims.beans.UpgradePath;
import com.nmims.beans.UpgradePathFamily;
import com.nmims.beans.PackageEntitlementInfo;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.EntitlementDependencies;
import com.nmims.beans.EntitlementDependency;
import com.nmims.helpers.DataValidationHelpers;
import com.nmims.helpers.SalesForceHelper_Packages;

public class PackageAdminDAO {


/*
 *Initialize 
 */
	/*
	 *JDBC 
	 */
		private JdbcTemplate jdbcTemplate;
		private DataSource dataSource;

		private static final Logger logger = LoggerFactory.getLogger(PackageAdminDAO.class);
	 
		public DataSource getDataSource() {
			return dataSource;
		}
		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
	
	//mostly does date functions,etc
	DataValidationHelpers validationHelpers = new DataValidationHelpers();
	
	//object to avoid having to create a map for status and response every time
	ReturnStatus returnStatus = new ReturnStatus();


	@Autowired
	SalesForceHelper_Packages salesForceHelper;


/*
 * 	public functions to perform operations
 */
	/*
	 *	families 
	 */
		public ReturnStatus addPackageFamily(PackageFamily packageFamily) {
			addPackageFamilyQuery(packageFamily);
			return returnStatus;
		}
		public ReturnStatus updatePackageFamily(PackageFamily packageFamily) {
			updatePackageFamilyQuery(packageFamily);
			return returnStatus;
		}
		public ReturnStatus deletePackageFamily(String packageFamilyId) {
			deletePackageFamilyQuery(packageFamilyId);
			return returnStatus;
		}
	
		public List<PackageFamily> getAllPackageFamilies(){
			return getAllPackageFamiliesQuery();
		}
		
		private List<PackageFamily> getAllPackageFamiliesQuery() {

			String sql = ""
					+ "SELECT "
					+ "`pf`.*, "
					+ "COUNT(`p`.`packageFamily`) AS `numberOfPackages` "
					+ "FROM "
					+ "`products`.`package_families` `pf` "
					+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
					+ "ON "
					+ "`p`.`packageFamily` = `pf`.`familyId` "
					+ "GROUP BY "
					+ "`pf`.`familyId`";
			List<PackageFamily> families = jdbcTemplate.query(sql, new BeanPropertyRowMapper<PackageFamily>(PackageFamily.class));
			return families;
		}
		
	/*
	 *	packages 
	 */
		public ReturnStatus addPackage(PackageBean packageBean) {
			packageBean = resetRequiredPackageBeanValuesIfNull(packageBean);
			addPackageQuery(packageBean);
			return returnStatus;
		}
		public ReturnStatus updatePackage(PackageBean packageBean) {
			packageBean = resetRequiredPackageBeanValuesIfNull(packageBean);
			updatePackageQuery(packageBean);
			return returnStatus;
		}
		public ReturnStatus deletePackage(String packageId) {
			deletePackageQuery(packageId);
			return returnStatus;
		}
	
		public List<PackageBean> getAllPackages(){
			return getAllPackagesQuery();
		}
		
		private List<PackageBean> getAllPackagesQuery() {
			String sql = "SELECT "
					+ "`p`.*,"
					+ "`pfa`.*, "
					+ "COUNT(`pfe`.`packageId`) AS `numberOfFeatures` "
					+ "FROM "
					+ "`products`.`packages` `p`"
					+ "LEFT JOIN "
					+ "`products`.`package_families` `pfa`"
					+ "ON "
					+ "`p`.`packageFamily` = `pfa`.`familyId` "
					+ "LEFT JOIN "
					+ "`products`.`package_features` `pfe`"
					+ "ON "
					+ "`p`.`packageId` = `pfe`.`packageId` "
					+ "GROUP BY "
					+ "`p`.`packageId`";
			try {
				List<PackageBean> packages = jdbcTemplate.query(
						sql,
						new BeanPropertyRowMapper<PackageBean>(PackageBean.class));
				return packages;
			}catch (Exception e) {
				return null;
			}
			
		}
		/*
	 * 	packageRequirements
	 */
		public ReturnStatus addPackageRequirements(PackageRequirementsMasterMapping masterMapping) {
			addPackageRequirementsQuery(masterMapping);
			return returnStatus;
		}
		public ReturnStatus updatePackageRequirements(PackageRequirementsMasterMapping masterMapping) {
			updatePackageRequirementsQuery(masterMapping);
			return returnStatus;
		}
		public ReturnStatus deletePackageRequirements(PackageRequirementsMasterMapping masterMapping) {
			deletePackageRequirementsQuery(masterMapping);
			return returnStatus;
		}

		
	/*
	 *	features 
	 */
		public ReturnStatus addFeature(Feature feature) {
			feature = resetRequiredFeatureValuesIfNull(feature);
			addFeatureQuery(feature);
			return returnStatus;
		}
		public ReturnStatus updateFeature(Feature feature) {
			feature = resetRequiredFeatureValuesIfNull(feature);
			updateFeatureQuery(feature);
			return returnStatus;
		}
		public ReturnStatus deleteFeature(Feature feature) {
			feature = resetRequiredFeatureValuesIfNull(feature);
			deleteFeatureQuery(feature);
			return returnStatus;
		}
	
	
	/*
	 *	package features 
	 */
		public ReturnStatus addPackageFeature(PackageFeature packageFeature) {
			addPackageFeatureQuery(packageFeature);
			return returnStatus;
		}
		public ReturnStatus deletePackageFeature(String packageId, String featureId) {
			deletePackageFeatureQuery(packageId, featureId);
			return returnStatus;
		}

		
	/*
	 * 	entitlementDependencies
	 */
		public ReturnStatus addEntitlementDependencies(EntitlementDependencies dependencies) {
			
			List<String> offenderList = new ArrayList<String>();
			for(EntitlementDependency dependency: dependencies.getDependencies()) {
				ReturnStatus status = addEntitlementDependency(dependency);
				
				//if theres an error
				if(status.getStatus().equals("0")) {
					
					//for each offender
					String offender = "";
					offender = dependency.getDependsOnFeatureId();
					if(!status.getMessage().isEmpty()) {
						offender += " " + status.getMessage() ;
					}
					offenderList.add(offender);
				}
			}
			
			returnStatus.setOffenderList(offenderList);
			return returnStatus;
		}		
		public ReturnStatus updateEntitlementDependencies(EntitlementDependencies dependencies) {
			
			List<String> offenderList = new ArrayList<String>();
			for(EntitlementDependency dependency: dependencies.getDependencies()) {
				ReturnStatus status = updateEntitlementDependency(dependency);
				
				//if theres an error
				if(status.getStatus().equals("0")) {
					
					//for each offender
					String offender = "";
					offender = dependency.getDependsOnFeatureId();
					if(!status.getMessage().isEmpty()) {
						offender +=  " " + status.getMessage();
					}
					offenderList.add(offender);
				}
			}
			
			returnStatus.setOffenderList(offenderList);
			return returnStatus;
		}
		public ReturnStatus deleteEntitlementDependency(EntitlementDependency dependency) {
			deleteDependency(dependency);
			return returnStatus;
		}
			
	/*
	 * 	entitlement
	 */
		public ReturnStatus addEntitlement(PackageEntitlementInfo packageEntitlementInfo) {
			addEntitlementInfo(packageEntitlementInfo);
			return returnStatus;
		}
		public ReturnStatus updateEntitlement(PackageEntitlementInfo packageEntitlementInfo) {
			updateEntitlementInfo(packageEntitlementInfo);
			return returnStatus;
		}
		public ReturnStatus deleteEntitlement(PackageEntitlementInfo packageEntitlementInfo) {
			deleteEntitlementInfo(packageEntitlementInfo);
			return returnStatus;
		}
			
	/*
	 * 	initialStudentInfo
	 */
		public ReturnStatus updateEntitlementInitialStudentInfo(StudentEntitlement studentEntitlement) {
			updateInitialEntitlementInfo(studentEntitlement);
			return returnStatus;
		}	
		public ReturnStatus deleteEntitlementInitialStudentInfo(StudentEntitlement studentEntitlement) {
			deleteInitialEntitlementInfo(studentEntitlement);
			return returnStatus;
		}

		
	/*
	 * 	packageFeature
	 */
		private void addPackageFeatureQuery(PackageFeature packageFeature) {
			if(packageFeature.getFeatureId() == null) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Invalid or empty feature");
			}else if(packageFeature.getPackageId() == null) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Invalid or empty package");
			}
			String sql = "INSERT INTO "
					+ "`products`.`package_features` "
					+ "( `packageId`, `featureId` ) "
					+ "VALUES "
					+ "( ?, ? )";
			try {
				jdbcTemplate.update(sql, new Object[] { packageFeature.getPackageId(), packageFeature.getFeatureId() });
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		private void deletePackageFeatureQuery(String packageId, String featureId) {
			String sql = "DELETE "
					+ "FROM "
					+ "`products`.`package_features` "
					+ "WHERE "
					+ "packageId = ? "
					+ "AND "
					+ "featureId = ?";
			
			try {
				jdbcTemplate.update(sql, new Object[] { packageId, featureId });
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}

		}

		public boolean checkIfPackageFeatureExists(String packageId, String featureId) {
			return checkIfPackageFeatureExistsQuery(packageId, featureId);
		}

		private boolean checkIfPackageFeatureExistsQuery(String packageId, String featureId) {
			String sql = "SELECT "
					+ "count(*) "
					+ "FROM "
					+ "`products`.`package_features` "
					+ "WHERE "
					+ "`packageId` = ? "
					+ "AND "
					+ "`featureId` = ?";
			try {
				int numResults = jdbcTemplate.queryForObject(sql, new Object[] { packageId, featureId } , Integer.class);
				if(numResults > 0) {
					return true;
				}else {
					return false;
				}
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				return false;
			}
		}
		

		public boolean checkIfFeatureExists(String featureId) {
			return checkIfFeatureExistsQuery(featureId);
		}

		private boolean checkIfFeatureExistsQuery(String featureId) {
			String sql = "SELECT "
					+ "count(*) "
					+ "FROM "
					+ "`products`.`features` "
					+ "WHERE "
					+ "`featureId` = ?";
			try {
				int numResults = jdbcTemplate.queryForObject(sql, new Object[] { featureId } , Integer.class);
				if(numResults > 0) {
					return true;
				}else {
					return false;
				}
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				return false;
			}
		}
		
		public boolean checkIfPackageFamilyExistsInPath(String pathId, String packageFamilyId) {
			return checkIfPackageFamilyExistsInPathQuery(pathId, packageFamilyId);
		}

		
	/*
	 * 	Upgrade Path
	 */
		public ReturnStatus addUpgradePath(UpgradePath upgradePath) {
			addUpgradePathQuery(upgradePath);
			return returnStatus;
		}

		public ReturnStatus updateUpgradePath(UpgradePath upgradePath) {
			updateUpgradePathQuery(upgradePath);
			return returnStatus;
		}
		
		public ReturnStatus deleteUpgradePath(String upgradePathId) {
			deleteUpgradePathQuery(upgradePathId);
			return returnStatus;
		}
		
		public UpgradePath getUpgradePath(String upgradePathId) {
			return getUpgradePathQuery(upgradePathId);
		}
		
		public List<UpgradePath> getAllUpgradePaths() {
			return getAllUpgradePathsQuery();
		}

		public boolean checkIfUpgradePathWithIdExists(String pathId) {
			return checkIfUpgradePathWithIdExistsQuery(pathId);
		}
		
	/*
	 * 	Upgrade Path Packages
	 */

		public ReturnStatus addFamilyToUpgradePath(UpgradePathFamily upgradePathFamily) {
			addFamilyToUpgradePathQuery(upgradePathFamily);
			return returnStatus;
		}

		private void addFamilyToUpgradePathQuery(UpgradePathFamily upgradePathFamily) {

			String sql = "INSERT INTO "
					+ "`products`.`upgrade_path_packages`"
					+ "("
					+ "`pathId`, `packageFamilyId`, `levelValue`,"
					+ "`minLevelToPurchase`, `maxLevelToPurchase`, `validityAfterEndDate`"
					+ ")"
					+ "VALUES"
					+ "("
					+ "?, ?, ?,"
					+ "?, ?, ?"
					+ ")";
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								upgradePathFamily.getPathId(), upgradePathFamily.getPackageFamilyId(), upgradePathFamily.getLevelValue(),
								upgradePathFamily.getMinLevelToPurchase(), upgradePathFamily.getMaxLevelToPurchase(), upgradePathFamily.getValidityAfterEndDate()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}

		}
		
		public ReturnStatus updateFamilyInUpgradePath(UpgradePathFamily upgradePathFamily) {
			updateFamilyInUpgradePathQuery(upgradePathFamily);
			return returnStatus;
		}

		private void updateFamilyInUpgradePathQuery(UpgradePathFamily upgradePathFamily) {
			
			String sql = "UPDATE "
					+ "`products`.`upgrade_path_packages` "
					+ "SET "
					+ "`levelValue` = ?, "
					+ "`minLevelToPurchase` = ?, "
					+ "`maxLevelToPurchase` = ?, "
					+ "`validityAfterEndDate` = ? "
					+ "WHERE "
					+ "`pathId` = ? "
					+ "AND "
					+ "`packageFamilyId` = ?";

			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								upgradePathFamily.getLevelValue(), upgradePathFamily.getMinLevelToPurchase(), upgradePathFamily.getMaxLevelToPurchase(), 
								upgradePathFamily.getValidityAfterEndDate(), upgradePathFamily.getPathId(), upgradePathFamily.getPackageFamilyId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}

		public ReturnStatus deleteFamilyFromUpgradePath(String upgradePathId, String familyId) {
			deleteFamilyFromUpgradePathQuery(upgradePathId, familyId);
			return returnStatus;
		}
		
		private void deleteFamilyFromUpgradePathQuery(String upgradePathId, String familyId) {
			String sql = "DELETE FROM "
					+ "`products`.`upgrade_path_packages` "
					+ "WHERE "
					+ "`pathId` = ? "
					+ "AND "
					+ "`packageFamilyId` = ?";

			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								upgradePathId, familyId
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}

		public List<PackageFamily> getListOfPackagesNotInUpgradePath(String upgradePathId){
			return getListOfPackagesNotInUpgradePathQuery(upgradePathId);
		}
		
		public List<Feature> getListOfFeaturesNotInPackage(String pacakgeId){
			return getListOfFeaturesNotInPackageQuery(pacakgeId);
		}

		private List<Feature> getListOfFeaturesNotInPackageQuery(String pacakgeId) {
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`features` "
					+ "WHERE "
					+ "`featureId` "
					+ "NOT IN "
					+ "("
					+ "SELECT "
						+ "`featureId` "
						+ "FROM "
						+ "`products`.`package_features` "
						+ "WHERE "
						+ "`packageId` = ? "
					+ ")";

			try {
				List<Feature> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new Object[] { pacakgeId },
						new BeanPropertyRowMapper<Feature>(Feature.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies;
				}
			}catch (Exception e) {

				
			}
			return null;
		}
		public UpgradePathFamily getUpgradePathFamily(String upgradePathId, String familyId) {
			return getUpgradePathFamilyQuery(upgradePathId, familyId);
		}
		
		private UpgradePathFamily getUpgradePathFamilyQuery(String upgradePathId, String familyId) {
			
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`upgrade_path_packages` "
					+ "WHERE "
					+ "`pathId` = ? "
					+ "AND "
					+ "`packageFamilyId` = ?";

			try {
				List<UpgradePathFamily> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new Object[] { 
								upgradePathId,
								familyId
							},
						new BeanPropertyRowMapper<UpgradePathFamily>(UpgradePathFamily.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies.get(0);
				}
			}catch (Exception e) {

				
			}
			return null;
		}

		public List<UpgradePathFamily> getAllFamiliesInUpgradePath(String upgradePathId) {
			return getAllFamiliesInUpgradePathQuery(upgradePathId);
		}
		
		private List<UpgradePathFamily> getAllFamiliesInUpgradePathQuery(String upgradePathId) {
			
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`upgrade_path_packages` "
					+ "WHERE "
					+ "`pathId` = ?";

			try {
				List<UpgradePathFamily> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new Object[] { upgradePathId },
						new BeanPropertyRowMapper<UpgradePathFamily>(UpgradePathFamily.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies;
				}
			}catch (Exception e) {

				
			}
		
			return null;
		}
		
		
		
		public List<UpgradePathFamily> getAllUpgradePathFamily(String pathId) {
			return getAllUpgradePathFamilyQuery(pathId);
		}
		
		private List<UpgradePathFamily> getAllUpgradePathFamilyQuery(String pathId) {
			
			String sql = "SELECT "
					+ "`upp`.* , "
					+ " `pf`.`familyName` AS `packageFamilyName`,"
					+ " `up`.`pathName` AS `pathName`"
					+ "FROM "
					+ "`products`.`upgrade_path_packages` `upp` "
					+ "LEFT JOIN "
					+ "`products`.`upgrade_paths` `up` "
					+ "ON "
					+ "`upp`.`pathId` = `up`.`pathId` "
					+ "LEFT JOIN "
					+ "`products`.`package_families` `pf` "
					+ "ON "
					+ "`upp`.`packageFamilyId` = `pf`.`familyid` "
					+ "WHERE "
					+ "`up`.`pathId` = ?";

			try {
				List<UpgradePathFamily> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new Object[] { pathId },
						new BeanPropertyRowMapper<UpgradePathFamily>(UpgradePathFamily.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies;
				}
			}catch (Exception e) {

				
			}
		
			return null;
		}
		
		
		
		public List<UpgradePathFamily> getAllUpgradePathFamily() {
			return getAllUpgradePathFamilyQuery();
		}
		
		private List<UpgradePathFamily> getAllUpgradePathFamilyQuery() {
			
			String sql = "SELECT "
					+ "`upp`.* , "
					+ " `pf`.`familyName` AS `packageFamilyName`,"
					+ " `up`.`pathName` AS `pathName`"
					+ "FROM "
					+ "`products`.`upgrade_path_packages` `upp` "
					+ "LEFT JOIN "
					+ "`products`.`upgrade_paths` `up` "
					+ "ON "
					+ "`upp`.`pathId` = `up`.`pathId` "
					+ "LEFT JOIN "
					+ "`products`.`package_families` `pf` "
					+ "ON "
					+ "`upp`.`packageFamilyId` = `pf`.`familyid` ";

			try {
				List<UpgradePathFamily> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new BeanPropertyRowMapper<UpgradePathFamily>(UpgradePathFamily.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies;
				}
			}catch (Exception e) {

				
			}
		
			return null;
		}
		
		
		private List<PackageFamily> getListOfPackagesNotInUpgradePathQuery(String upgradePathId) {
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`package_families` "
					+ "WHERE "
					+ "`familyId` "
					+ "NOT IN "
					+ "("
					+ "SELECT "
						+ "`packageFamilyId` "
						+ "FROM "
						+ "`products`.`upgrade_path_packages` "
						+ "WHERE "
						+ "`pathId` = ? "
						+ "AND "
						+ "`packageFamilyId` "
						+ "GROUP BY `packageFamilyId`"
					+ ")";

			try {
				List<PackageFamily> upgradePathsFamilies = jdbcTemplate.query(
						sql,
						new Object[] { upgradePathId },
						new BeanPropertyRowMapper<PackageFamily>(PackageFamily.class));

				if(upgradePathsFamilies.size() > 0) {
					return upgradePathsFamilies;
				}
			}catch (Exception e) {

				
			}
			return null;
		}
		public List<PackageRequirements> getAllPackageRequirements() {
			return getAllPackageRequirementsQuery();
		}
		public List<PackageRequirements> getAllPackageRequirements(String packageId) {
			return getAllPackageRequirementsQuery(packageId);
		}
		public PackageRequirementsMasterMapping getPackageRequirements(String packageId) {
			return getPackageRequirementsQuery(packageId);
		}
		public PackageFamily getPackageFamily(String familyId) {
			return getPackageFamilyQuery(familyId);
		}
		public PackageBean getPackageFromId(String packageId) {
			return getPackageFromIdQuery(packageId);
		}
		
/*
 *	----------------- queries ------------------------
 */

	/*
	 * 	package
	 */
		private void addPackageQuery(PackageBean packageBean) {
			
			String salesForceUID = salesForceHelper.addPackageToSalesForce(
						packageBean.getPackageName(), 
						packageBean.getDurationType()
					);
			if(salesForceUID == null) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error Adding package to salesforce");
				return;
			}
			packageBean.setSalesForceUID(salesForceUID);

			String sql = "INSERT INTO "
					+ "`products`.`packages`"
					+ "("
						+ "`salesForceUID`, `packageName`, `openForSale`, "
						+ "`durationMax`, `durationType`, `packageFamily`"
					+ ")"
					+ "VALUES"
					+ "("
						+ "?, ?, ?, "
						+ "?, ?, ? "
					+ ")";
			
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								packageBean.getSalesForceUID(), packageBean.getPackageName(), packageBean.isOpenForSale(),
								packageBean.getDurationMax(), packageBean.getDurationType(), packageBean.getPackageFamily()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		
		private void updatePackageQuery(PackageBean packageBean) {
			checkIfPackageExists(packageBean);
			if(returnStatus.getStatus().equals("0")) {
				returnStatus.setMessage("Invalid package .");
				return;
			}
			returnStatus = new ReturnStatus();
			String sql = "UPDATE "
					+ "`products`.`packages` "
					+ "SET "
					+ "`packageName` = ?, "
					+ "`durationMax` = ?, "
					+ "`durationType` = ?, "
					+ "`packageFamily` = ?, "
					+ "`openForSale` = ? "
					+ "WHERE "
					+ "`packageId` = ? ";
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								packageBean.getPackageName(), 
								packageBean.getDurationMax(), 
								packageBean.getDurationType(), 
								packageBean.getPackageFamily(),
								packageBean.isOpenForSale(),
								packageBean.getPackageId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		private ReturnStatus checkIfPackageExists(PackageBean packageBean) {
			if(!validationHelpers.checkIfStringEmptyOrNull(packageBean.getPackageId())) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Invalid or empty Package Id");
			}
			return checkIfPackageExists(packageBean.getPackageId());
		}
		
		private void deletePackageQuery(String packageId) {
			String sql = "DELETE "
					+ "FROM "
					+ "`products`.`packages` "
					+ "WHERE "
					+ "`packageId` = ?";
			try {
				jdbcTemplate.update(sql, new Object[] { packageId });
				returnStatus.setStatus("success");
			}catch(Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		
		

	/*
	 * 	packageFamily
	 */
		private void addPackageFamilyQuery(PackageFamily packageFamily) {
			
			
			
			String sql = "INSERT INTO "
					+ "`products`.`package_families`"
					+ "("
					+ "`familyName`, `descriptionShort`, `description`, "
					+ "`keyHighlights`, `eligibilityCriteria`, `componentEligibilityCriteria` "
					+ ")"
					+ "VALUES"
					+ "("
					+ "?, ?, ?, "
					+ "?, ?, ?"
					+ ")";
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								packageFamily.getFamilyName(), packageFamily.getDescriptionShort(), packageFamily.getDescription(),
								packageFamily.getKeyHighlights(), packageFamily.getEligibilityCriteria(), packageFamily.getComponentEligibilityCriteria()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		
		private void updatePackageFamilyQuery(PackageFamily packageFamily) {

			returnStatus = new ReturnStatus();
			String sql = "UPDATE "
					+ "`products`.`package_families` "
					+ "SET "
					+ "`familyName` = ?, "
					+ "`description` = ?, "
					+ "`descriptionShort` = ?, "
					+ "`keyHighlights` = ?, "
					+ "`eligibilityCriteria` = ?, "
					+ "`componentEligibilityCriteria` = ? "
					+ "WHERE "
					+ "`familyId` = ?";

			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								packageFamily.getFamilyName(),
								packageFamily.getDescription(),
								packageFamily.getDescriptionShort(), 
								packageFamily.getKeyHighlights(), 
								packageFamily.getEligibilityCriteria(),
								packageFamily.getComponentEligibilityCriteria(),
								packageFamily.getFamilyId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
//		private void checkIfPackageFamliyExists(PackageFamily packageFamily) {
//			if(!validationHelpers.checkIfStringEmptyOrNull(packageFamily.getFamilyId())) {
//				returnStatus.setStatus("failure");
//				returnStatus.setMessage("Invalid or empty Package Family Id");
//			}
//			checkIfPackageFamilyExists(packageFamily.getFamilyId());
//		}
			
		
		private void deletePackageFamilyQuery(String packageFamilyId) {
			String sql = "DELETE FROM "
					+ "`products`.`package_families` "
					+ "WHERE "
					+ "`familyId` = ?";

			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								packageFamilyId
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}

		
	
	/*
	 * 	feature	
	 */
		private void addFeatureQuery(Feature feature) {
			String sql = "INSERT INTO "
					+ "`products`.`features`"
					+ "("
						+ "`featureName`, `featureDescription`, `validityFast`,"
						+ "`validityNormal`, `validitySlow`)"
					+ "VALUES"
					+ "("
						+ "?, ?, ?,"
						+ "?, ?, ?"
					+ ")";

			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								feature.getFeatureName(), feature.getFeatureDescription(), feature.getValidityFast(),
								feature.getValidityNormal(), feature.getValiditySlow()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		
		private void updateFeatureQuery(Feature feature) {

			String sql = "UPDATE "
					+ "`products`.`features` "
					+ "SET "
					+ "`featureName` = ?, "
					+ "`featureDescription` = ?, "
					+ "`validityFast` = ?, "
					+ "`validityNormal` = ?, "
					+ "`validitySlow` = ? "
					+ "WHERE "
					+ "`featureId` = ?" ;
	
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								feature.getFeatureName(), 
								feature.getFeatureDescription(), 
								feature.getValidityFast(),
								feature.getValidityNormal(), 
								feature.getValiditySlow(),
								feature.getFeatureId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}
		
		private void deleteFeatureQuery(Feature feature) {
			String sql = "DELETE FROM "
					+ "`products`.`features` "
					+ "WHERE "
					+ "`featureId` = ?";
			
			try {
				jdbcTemplate.update(sql, 
						new Object[] { 
								feature.getFeatureId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("Error");
				returnStatus.setError("" +  e);
			}
		}

		

	/*
	 * 	entitlement dependencies
	 */
		
		public ReturnStatus addEntitlementDependency(EntitlementDependency dependency) {
			return addEntitlementDependencyQuery(dependency);
		}
		
		private ReturnStatus addEntitlementDependencyQuery(EntitlementDependency dependency) {
			ReturnStatus status = new ReturnStatus();
			if(checkIfDependencyExists(dependency)) {
				status.setStatus("failure");
				status.setMessage("Dependency already exists!");
				return status;
			}
			
			String sql = "INSERT INTO "
					+ "`products`.`entitlements_dependencies` "
					+ "("
						+ "`entitlementId`, `dependsOnFeatureId`, `requiresCompletion`, "
						+ "`monthsAfterCompletion`, `requiresActivationOnly`, `monthsAfterActivation`, "
						+ "`activationsMinimumRequired`"
					+ ") "
					+ "VALUES "
					+ "( "
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "?"
					+ ")";

			
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								dependency.getEntitlementId(), dependency.getDependsOnFeatureId(), dependency.isRequiresCompletion(),
								dependency.getMonthsAfterCompletion(), dependency.isRequiresActivationOnly(), dependency.getMonthsAfterActivation(),
								dependency.getActivationsMinimumRequired()
						});
				
				status.setStatus("success");
			}catch (Exception e) {
				status.setStatus("failure");
				status.setMessage("");
				
			}
			return status;
		}
		private boolean checkIfDependencyExists(EntitlementDependency dependency) {
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`entitlements_dependencies` "
					+ "`entitlementId` = ? "
					+ "AND "
					+ "`dependsOnFeatureId` = ?";
			try {
				int numResults = jdbcTemplate.queryForObject(
						sql, 
						new Object[] { 
								dependency.getEntitlementId(), 
								dependency.getDependsOnFeatureId()
							} ,
						Integer.class);
				if(numResults > 0) {
					return true;
				}else {
					return false;
				}
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
				return false;
			}
			
		}
			
		public ReturnStatus updateEntitlementDependency(EntitlementDependency dependency) {
			return updateEntitlementDependencyQuery(dependency);
		}
		
		private ReturnStatus updateEntitlementDependencyQuery(EntitlementDependency dependency) {
			ReturnStatus status = new ReturnStatus();
			
			if(validationHelpers.checkIfStringEmptyOrNull(dependency.getId()) && checkIfDependencyIdExists(dependency)) {
				status.setStatus("failure");
				status.setMessage("Id doesnt exist");
				return status;
			}
			
			String sql = "UPDATE "
					+ "`products`.`entitlements_dependencies` "
					+ "SET "
					+ "`entitlementId` = ?, "
					+ "`dependsOnFeatureId` = ?, "
					+ "`requiresCompletion` = ?, "
					+ "`monthsAfterCompletion` = ?, "
					+ "`requiresActivationOnly` = ?, "
					+ "`monthsAfterActivation` = ?, "
					+ "`activationsMinimumRequired` = ? "
					+ "WHERE `id` = ? ";
			
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								dependency.getEntitlementId(), 
								dependency.getDependsOnFeatureId(), 
								dependency.isRequiresCompletion(),
								dependency.getMonthsAfterCompletion(), 
								dependency.isRequiresActivationOnly(), 
								dependency.getMonthsAfterActivation(),
								dependency.getActivationsMinimumRequired() ,
								dependency.getId()
						});
				
				status.setStatus("success");
			}catch (Exception e) {
				status.setStatus("failure");
				status.setMessage("" );
				status.setError("" + e);
			}
			return status;
		}
		private boolean checkIfDependencyIdExists(EntitlementDependency dependency) {
			return checkIfDependencyIdExists(dependency.getId());
		}
//		private boolean checkIfAnyOtherSuchDependencyExists(EntitlementDependency dependency) {
//			return checkIfAnyOtherSuchDependencyExists(dependency.getEntitlementId(),dependency.getDependsOnFeatureId(),dependency.getId() );
//		}
	
		private void deleteDependency(EntitlementDependency dependency) {
			String sql = "DELETE FROM "
					+ "`products`.`entitlements_dependencies` "
					+ "WHERE `id` = ? ";

			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								dependency.getId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("" +  e);
			}
		}

			
	
	/*
	 * 	EntitlementInfo 
	 */
			
		private void addEntitlementInfo(PackageEntitlementInfo packageEntitlementInfo) {
			String sql = "INSERT INTO `products`.`entitlements_info`"
						+ "(`packageFeaturesId`, `requiresOtherEntitlement`, `extendByMaxMonths`, "
						+ "`extendByMaxDays`, `requiresStudentActivation`, `totalActivations`, "
						+ "`initialActivations`, `initialCycleGapMonths`, `initialCycleGapDays`, "
						+ "`activationCycleMonths`, `activationCycleDays`, `activationsEveryCycle`, "
						+ "`extendIfActivationsLeft`) "
						+ "VALUES "
						+ "("
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "?"
						+ ")";
			
			try {
				jdbcTemplate.update(sql, new Object[] {
					packageEntitlementInfo.getPackageFeaturesId(), packageEntitlementInfo.isRequiresOtherEntitlement(), packageEntitlementInfo.getExtendByMaxMonths(),
					packageEntitlementInfo.getExtendByMaxDays(), packageEntitlementInfo.isRequiresStudentActivation(), packageEntitlementInfo.getTotalActivations(), 
					packageEntitlementInfo.getInitialActivations(), packageEntitlementInfo.getInitialCycleGapMonths(), packageEntitlementInfo.getInitialCycleGapDays(), 
					packageEntitlementInfo.getActivationCycleMonths(), packageEntitlementInfo.getActivationCycleDays(), packageEntitlementInfo.getActivationsEveryCycle(),  
					packageEntitlementInfo.isExtendIfActivationsLeft()
				});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setStatus("" + e);
			}
		}	
		
		private void updateEntitlementInfo(PackageEntitlementInfo packageEntitlementInfo) {
	
			String sql = "UPDATE"
					+ " `products`.`entitlements_info` "
					+ "SET "
					+ "`requiresOtherEntitlement` = ?, "
					+ "`requiresStudentActivation` = ?, "
					+ "`totalActivations` = ?, "
					+ "`initialActivations` = ?, "
					+ "`initialCycleGapMonths` = ?, "
					+ "`initialCycleGapDays` = ?, "
					+ "`activationCycleMonths` = ?, "
					+ "`activationCycleDays` = ?, "
					+ "`activationsEveryCycle` = ?, "
					+ "`extendIfActivationsLeft` = ?, "
					+ "`extendByMaxMonths` = ?, "
					+ "`extendByMaxDays` = ?, "
					+ "`duration` = ?, "
					+ "`giveAccessAfterExpiry` = ?, "
					+ "`hasViewableData` = ?, "
					+ "`giveAccessAfterActivationsConsumed` = ? "
					+ "WHERE "
					+ "`entitlementId`=?";
			
			try {
				jdbcTemplate.update(sql, new Object[] {
						packageEntitlementInfo.isRequiresOtherEntitlement(), 
						packageEntitlementInfo.isRequiresStudentActivation(), 
						packageEntitlementInfo.getTotalActivations(), 
						packageEntitlementInfo.getInitialActivations(), 
						packageEntitlementInfo.getInitialCycleGapMonths(), 
						packageEntitlementInfo.getInitialCycleGapDays(), 
						packageEntitlementInfo.getActivationCycleMonths(), 
						packageEntitlementInfo.getActivationCycleDays(), 
						packageEntitlementInfo.getActivationsEveryCycle(), 
						packageEntitlementInfo.isExtendIfActivationsLeft(),
						packageEntitlementInfo.getExtendByMaxMonths(),
						packageEntitlementInfo.getExtendByMaxDays(),
						packageEntitlementInfo.getDuration(),
						packageEntitlementInfo.isGiveAccessAfterExpiry(),
						packageEntitlementInfo.isHasViewableData(),
						packageEntitlementInfo.isGiveAccessAfterActivationsConsumed(),
						packageEntitlementInfo.getEntitlementId()
					});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setStatus("" + e);
			}
		}
		
		private void deleteEntitlementInfo(PackageEntitlementInfo packageEntitlementInfo) {
	
			String sql = "DELETE "
					+ "FROM "
					+ "`products`.`entitlements_info` "
					+ "WHERE "
					+ "`packageFeaturesId` = ?";
			try {
				jdbcTemplate.update(sql, new Object[] { packageEntitlementInfo.getEntitlementId() });
				returnStatus.setStatus("success");
			}catch(Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("" +  e);
			}
			
		}
		
		
	
	/*
	 *	Entitlement Initial student data 
	 * 	this table stores the values that should be inserted in a new users
	 */
		private boolean insertInitialEntitlementInfo(String entitlementId) {
			String sql = "INSERT INTO "
					+ "`products`.`entitlements_initial_student_data` "
					+ "( "
						+ "`entitlementId`"
					+ ") "
					+ "VALUES "
					+ "( "
						+ "?"
					+ ")";
			
			try {
				jdbcTemplate.update(sql, new Object[] {
						entitlementId
				});
				return true;
			}catch (Exception e) {
				return false;
			}
			
		}

		private void updateInitialEntitlementInfo(StudentEntitlement studentEntitlementInfo) {
			String sql = "UPDATE `products`.`entitlements_initial_student_data` "
					+ "SET "
					+ "`activated` = ?, "
					+ "`activatedByStudent` = ?, "
					+ "`activationsLeft` = ? "
					+ "WHERE "
					+ "`entitlementId` = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
						studentEntitlementInfo.isActivated(),
						studentEntitlementInfo.isActivatedByStudent(),
						studentEntitlementInfo.getActivationsLeft(),
						studentEntitlementInfo.getEntitlementId()
				});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setStatus("" + e);
			}
			
		}
	
		private void deleteInitialEntitlementInfo(StudentEntitlement studentEntitlementInfo) {

			String sql = "DELETE "
					+ "FROM "
					+ "`products`.`entitlements_initial_student_data` "
					+ "WHERE "
					+ "`entitlementId` = ?";
			try {
				jdbcTemplate.update(sql, new Object[] { studentEntitlementInfo.getEntitlementId() });
				returnStatus.setStatus("success");
			}catch(Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("" + e);
			}
			
		}


	/*
	 * 	package requirements
	 */
		private void addPackageRequirementsQuery(PackageRequirementsMasterMapping masterMapping) {
			
			if(masterMapping.getConsumerProgramStructureId().size() > 0) {

				GeneratedKeyHolder holder = new GeneratedKeyHolder();
				
				String sql = "INSERT INTO `products`.`package_requirements`"
							+ "("
							+ "`packageId`, `requiredSemMin`, `requiredSemMax`,"
							+ "`minSubjectsClearedTotal`, `minSubjectsClearedPerSem`, `availableForAlumni`, "
							+ "`availableForAlumniOnly`, `alumniMaxMonthsAfterLastRegistration`)"
							+ "VALUES"
							+ "("
							+ "?, ?, ?,"
							+ "?, ?, ?,"
							+ "?, ?"
							+ ")";

				// using prepared statement creator to get the inserted key
			    final PreparedStatementCreator psc = new PreparedStatementCreator() {
			        @Override
			        public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
			          final PreparedStatement ps = connection.prepareStatement(sql,
			              Statement.RETURN_GENERATED_KEYS);
			          ps.setString(1, masterMapping.getPackageId());
			          ps.setInt(2, masterMapping.getRequiredSemMin());
			          ps.setInt(3, masterMapping.getRequiredSemMax());
			          ps.setInt(4, masterMapping.getMinSubjectsClearedTotal());
			          ps.setInt(5, masterMapping.getMinSubjectsClearedPerSem());
			          ps.setBoolean(6, masterMapping.isAvailableForAlumni());
			          ps.setBoolean(7, masterMapping.isAvailableForAlumniOnly());
			          ps.setInt(8, masterMapping.getAlumniMaxMonthsAfterLastRegistration());
			          return ps;
			        }
			      };
					
				try {
					jdbcTemplate.update(
							psc,
							holder
					);
					String requirementsId = holder.getKey().toString();
					returnStatus.setStatus("success");
					addMasterMapping(masterMapping, requirementsId, masterMapping.getPackageId());
				}catch (Exception e) {
					returnStatus.setStatus("failure");
					returnStatus.setError("" + e);
				}
			}else {
				returnStatus.setStatus("failure");
				returnStatus.setMessage("No consumer types selected");
			}
		}
		
		private void addMasterMapping(PackageRequirementsMasterMapping masterMapping, String requirementsId, String packageId) {

			for( String consumerProgramStructureId :masterMapping.getConsumerProgramStructureId()) {
				
				//if a record with this requirements id exists in another package, delete those
				if(checkIfMappingExistsInPackageRequirementsMasterMappingQuery(packageId, consumerProgramStructureId)){
					deleteMappingFromPackageRequirementsMasterMappingQuery(packageId, consumerProgramStructureId);
				}
				String sql = "INSERT INTO `products`.`package_requirements_master_mapping` " 
						+ "( "
							+ "consumerProgramStructureId, "
							+ "requirementsId, "
							+ "packageId "
						+ ") " 
						+ "VALUES "
						+ " ( " 
							+ "'" + consumerProgramStructureId + "', "
							+ "'" + requirementsId + "', "
							+ "'" + packageId + "'"
						+ ")";

				try {
					jdbcTemplate.update(
							sql
					);				
				}catch (Exception e) {
					returnStatus.setStatus("failure");
					returnStatus.setError("addMasterMapping Id: " + consumerProgramStructureId + " E " + e);
					List<String> offenderList = returnStatus.getOffenderList();
					offenderList.add(consumerProgramStructureId + e);
					returnStatus.setOffenderList(offenderList);
				}
			}
		    
		}
		
		private void updatePackageRequirementsQuery(PackageRequirementsMasterMapping masterMapping) {
//			try {
				
				String sql = "UPDATE "
						+ "`products`.`package_requirements` "
						+ "SET "
						+ "`requiredSemMin` = ?, "
						+ "`requiredSemMax` = ?, "
						+ "`minSubjectsClearedTotal` = ?, "
						+ "`minSubjectsClearedPerSem` = ?, "
						+ "`availableForAlumni` = ?, "
						+ "`availableForAlumniOnly` = ?, "
						+ "`alumniMaxMonthsAfterLastRegistration` = ? "
						+ "WHERE "
						+ "`requirementsId` = ?";
				jdbcTemplate.update(
						sql, 
						new Object[] {
								masterMapping.getRequiredSemMin(),
								masterMapping.getRequiredSemMax(),
								masterMapping.getMinSubjectsClearedTotal(),
								masterMapping.getMinSubjectsClearedPerSem(),
								masterMapping.isAvailableForAlumni(),
								masterMapping.isAvailableForAlumniOnly(),
								masterMapping.getAlumniMaxMonthsAfterLastRegistration(),
								masterMapping.getRequirementsId()
						}
				);
				
				returnStatus.setStatus("success");
				
				deletePackageRequirementsMasterMappingQuery(masterMapping);
				addMasterMapping(masterMapping, masterMapping.getRequirementsId(), masterMapping.getPackageId());
//			}catch (Exception e) {
//				returnStatus.setStatus("failure");
//				returnStatus.setError("updatePackageRequirementsQuery" + e);
//			}
		}
		private void deletePackageRequirementsQuery(PackageRequirementsMasterMapping masterMapping) {
			String sql = "DELETE FROM "
					+ "`products`.`package_requirements` "
					+ "WHERE `requirementsId` = ? ";
			
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								masterMapping.getRequirementsId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("deletePackageRequirementsQuery" + e);
			}
		}

		private void deletePackageRequirementsMasterMappingQuery(PackageRequirementsMasterMapping packageRequirements) {
			String sql = "DELETE FROM "
					+ "`products`.`package_requirements_master_mapping` "
					+ "WHERE `requirementsId` = ? ";
		
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								packageRequirements.getRequirementsId()
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("deletePackageRequirementsMasterMappingQuery" + e);
			}
		}
		
		private boolean checkIfMappingExistsInPackageRequirementsMasterMappingQuery(String packageId, String consumerProgramStructureId) {
			String sql = "SELECT "
					+ "count(*) "
					+ "FROM "
					+ "`products`.`package_requirements_master_mapping` "
					+ "WHERE "
					+ "`packageId` = ? "
					+ "AND "
					+ "`consumerProgramStructureId` = ?";
			try {
				int returnCount = jdbcTemplate.queryForObject(
						sql, 
						new Object[] { 
								packageId, consumerProgramStructureId
						}, 
						Integer.class);
				if(returnCount > 0) {
					return true;
				}
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("checkIfMappingExistsInPackageRequirementsMasterMappingQuery" + e);
			}
			return false;
		}
		
		private void deleteMappingFromPackageRequirementsMasterMappingQuery(String packageId, String consumerProgramStructureId) {
			String sql = "DELETE FROM "
					+ "`products`.`package_requirements_master_mapping` "
					+ "WHERE "
					+ "`packageId` = ? "
					+ "AND "
					+ "`consumerProgramStructureId` = ?";
		
			try {
				jdbcTemplate.update(
						sql, 
						new Object[] { 
								packageId, consumerProgramStructureId
						});
				returnStatus.setStatus("success");
			}catch (Exception e) {
				returnStatus.setStatus("failure");
				returnStatus.setError("deleteMappingFromPackageRequirementsMasterMappingQuery" + e);
			}
		}

	private PackageBean resetRequiredPackageBeanValuesIfNull(PackageBean packageBean) {
		if(packageBean.getDurationType() == null) {
			packageBean.setDurationType("");
		}
		if(packageBean.getEndDate() == null) {
			packageBean.setEndDate("");
		}
		if(packageBean.getSalesForceUID() == null) {
			packageBean.setSalesForceUID("");
		}
		return packageBean;
	}
	private Feature resetRequiredFeatureValuesIfNull(Feature feature) {

		if(feature.getFeatureName() == null) {
			feature.setFeatureName("");
		}
		if(feature.getFeatureDescription() == null) {
			feature.setFeatureDescription("");
		}
		if(feature.getValidityFast() == 0) {
			feature.setValidityFast(6);
		}
		if(feature.getValidityNormal() == 0) {
			feature.setValidityNormal(12);
		}
		if(feature.getValiditySlow() == 0) {
			feature.setValiditySlow(24);
		}
		return feature;
	}


	private ReturnStatus checkIfPackageExists(String packageId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`packages` "
				+ "WHERE "
				+ "`packageId` = ?";
		try {
			int returnCount = jdbcTemplate.queryForObject(sql, new Object[] { packageId }, Integer.class);

			if(returnCount > 0) {
				returnStatus.setStatus("success");
			}else {
				returnStatus.setStatus("failure");
			}
		}catch (Exception e) {
			returnStatus.setStatus("failure");
			returnStatus.setError("" + e);
		}
		return returnStatus;
	}
	public boolean checkIfPackageWithIdExists(String packageId) {
		ReturnStatus ret = new ReturnStatus();
		ret = checkIfPackageExists(packageId);
		if(ret.getStatus().equals("success")) {
			return true;
		}
		return false;
	}
	
	public boolean checkIfRequirementsExistForPackage(String requirementsId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`package_requirements` "
				+ "WHERE "
				+ "`packageId` = ?";
		try {
			int returnCount = jdbcTemplate.queryForObject(sql, new Object[] { requirementsId }, Integer.class);
			
			if(returnCount > 0) {
				return true;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
	
	private boolean checkIfDependencyIdExists(String id) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_dependencies` "
				+ "`id` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { id } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	
	private PackageBean getPackageFromIdQuery(String packageId) {

		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`packages` `p`"
				+ "LEFT JOIN "
				+ "`products`.`package_families` `pf`"
				+ "ON "
				+ "`p`.`packageFamily` = `pf`.`familyId` "
				+ "WHERE "
				+ "`p`.`packageId`=? ";

		List<PackageBean> packages = jdbcTemplate.query(
				sql, 
				new Object[] { packageId },
				new BeanPropertyRowMapper<PackageBean>(PackageBean.class));

		if(packages.size() > 0) {
			return packages.get(0);
		}
		return null;
	}
	
	private PackageFamily getPackageFamilyQuery(String familyId) {

		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`package_families` "
				+ "WHERE "
				+ "familyId=?";
		
		List<PackageFamily> families = jdbcTemplate.query(
				sql, 
				new Object[] { familyId }, 
				new BeanPropertyRowMapper<PackageFamily>(PackageFamily.class));

		Gson gson = new Gson();

		if(families.size() > 0) {
			return families.get(0);
		}

		return null;
	}
	
	private PackageRequirementsMasterMapping getPackageRequirementsQuery(String id) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`package_requirements` "
				+ "WHERE "
				+ "`requirementsId`=?";
		List<PackageRequirements> requirements = jdbcTemplate.query(
				sql, 
				new Object[] { id },
				new BeanPropertyRowMapper<PackageRequirements>(PackageRequirements.class));
		if(requirements.size() > 0) {
			PackageRequirements packageRequirements = requirements.get(0);
			PackageRequirementsMasterMapping mapping = new PackageRequirementsMasterMapping();
			String requirementsId = packageRequirements.getRequirementsId();
			
			mapping.setRequirementsId(requirementsId);
			mapping.setAlumniMaxMonthsAfterLastRegistration(packageRequirements.getAlumniMaxMonthsAfterLastRegistration());
			mapping.setAvailableForAlumni(packageRequirements.isAvailableForAlumni());
			mapping.setAvailableForAlumniOnly(packageRequirements.isAvailableForAlumniOnly());
			mapping.setConsumerType(packageRequirements.getConsumerType());
			mapping.setMinSubjectsClearedPerSem(packageRequirements.getMinSubjectsClearedPerSem());
			mapping.setMinSubjectsClearedTotal(packageRequirements.getMinSubjectsClearedTotal());
			mapping.setPackageId(packageRequirements.getPackageId());
			mapping.setRequiredSemMax(packageRequirements.getRequiredSemMax());
			mapping.setRequiredSemMin(packageRequirements.getRequiredSemMin());
			
			
			List<PackageRequirementsMasterMappingData> consumerProgramStructures = getConsumerProgramStructuresForRequirementsId(requirementsId);
			
			mapping.setConsumerProgramStructureMappingData(consumerProgramStructures);
			
			return mapping;
		}
		return null;
	}
	
	
		private List<PackageRequirementsMasterMappingData> getConsumerProgramStructuresForRequirementsId(String requirementsId) {
			String sql = "SELECT "
					+ "`pr`.`packageId` AS `packageId`, "
					+ "`p`.`packageName` AS `packageName`, "
					+ "`prmm`.`requirementsId` AS `requirementsId`, "
					+ "`prmm`.`consumerProgramStructureId`, "
					+ "`ct`.`id` AS `consumerTypeId`, "
					+ "`ps`.`program_structure` AS `programStructureName`,"
					+ "`ps`.`id` AS `programStructureId`, "
					+ "`p`.`program` AS `programName`  , "
					+ "`p`.`id` AS `programId`  "
					+ "FROM "
					+ "`products`.`package_requirements_master_mapping` `prmm` "
					+ "LEFT JOIN "
					+ "`products`.`package_requirements` `pr` "
					+ "ON "
					+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
					+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
					+ "ON "
					+ "`pr`.`packageId` = `p`.`packageId` "
					+ "LEFT JOIN "
					+ "`exam`.`consumer_program_structure` `cps` "
					+ "ON "
					+ "`prmm`.`consumerProgramStructureId` = `cps`.`id` "
					+ "LEFT JOIN "
					+ "`exam`.`consumer_type` `ct` "
					+ "ON "
					+ "`cps`.`consumerTypeId` = `ct`.`id`"
					+ "LEFT JOIN "
					+ "`exam`.`program_structure` `ps` "
					+ "ON "
					+ "`cps`.`programStructureId` = `ps`.`id`"
					+ "LEFT JOIN "
					+ "`exam`.`programs` `p` "
					+ "ON "
					+ "`cps`.`programId` = `p`.`id`"
					+ "WHERE "
					+ "`prmm`.`requirementsId` = ?";
			try {
				List<PackageRequirementsMasterMappingData> consumerProgramStructureIds = jdbcTemplate.query(
						sql, 
						new Object[] { requirementsId },
						new BeanPropertyRowMapper<PackageRequirementsMasterMappingData>(PackageRequirementsMasterMappingData.class));
				
				return consumerProgramStructureIds;
			}catch (Exception e) {
				logger.info("exception : "+e.getMessage());
			}
		return null;
	}

	private List<PackageRequirements> getAllPackageRequirementsQuery() {
		String sql = "SELECT "
				+ "`pr`.*, "
				+ "`pr`.`packageId` AS `packageId`, "
				+ "`p`.`packageName` AS `packageName`, "
				+ "`prmm`.`requirementsId` AS `requirementsId`, "
				+ "`prmm`.`consumerProgramStructureId`, "
				+ "`ct`.`id` AS `consumerTypeId`, "
				+ "`ps`.`program_structure` AS `programStructureName`,"
				+ "`ps`.`id` AS `programStructureId`, "
				+ "`p`.`program` AS `programName`  , "
				+ "`p`.`id` AS `programId` "
				+ "FROM "
				+ "`products`.`package_requirements_master_mapping` `prmm` "
				+ "LEFT JOIN "
				+ "`products`.`package_requirements` `pr` "
				+ "ON "
				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`pr`.`packageId` = `p`.`packageId` "
				+ "LEFT JOIN "
				+ "`exam`.`consumer_program_structure` `cps` "
				+ "ON "
				+ "`prmm`.`consumerProgramStructureId` = `cps`.`id` "
				+ "LEFT JOIN "
				+ "`exam`.`consumer_type` `ct` "
				+ "ON "
				+ "`cps`.`consumerTypeId` = `ct`.`id`"
				+ "LEFT JOIN "
				+ "`exam`.`program_structure` `ps` "
				+ "ON "
				+ "`cps`.`programStructureId` = `ps`.`id`"
				+ "LEFT JOIN "
				+ "`exam`.`programs` `p` "
				+ "ON "
				+ "`cps`.`programId` = `p`.`id`";
	
		List<PackageRequirements> requirements = jdbcTemplate.query(
				sql,
				new BeanPropertyRowMapper<PackageRequirements>(PackageRequirements.class));
		if(requirements.size() > 0) {
			return requirements;
		}
	
		return null;
	}
	

	private List<PackageRequirements> getAllPackageRequirementsQuery(String packageId) {
		String sql = "SELECT "
				+ "`pr`.*, "
				+ "`pr`.`packageId` AS `packageId`, "
				+ "`p`.`packageName` AS `packageName`, "
				+ "`prmm`.`requirementsId` AS `requirementsId`, "
				+ "`prmm`.`consumerProgramStructureId`, "
				+ "`ct`.`id` AS `consumerTypeId`, "
				+ "`ct`.`name` AS `consumerType`, "
				+ "`ps`.`program_structure` AS `programStructureName`,"
				+ "`ps`.`id` AS `programStructureId`, "
				+ "`p`.`code` AS `programCode`  , "
				+ "`p`.`name` AS `programName`  , "
				+ "`p`.`id` AS `programId` "
				+ "FROM "
				+ "`products`.`package_requirements_master_mapping` `prmm` "
				+ "LEFT JOIN "
				+ "`products`.`package_requirements` `pr` "
				+ "ON "
				+ "`pr`.`requirementsId` = `prmm`.`requirementsId` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`pr`.`packageId` = `p`.`packageId` "
				+ "LEFT JOIN "
				+ "`exam`.`consumer_program_structure` `cps` "
				+ "ON "
				+ "`prmm`.`consumerProgramStructureId` = `cps`.`id` "
				+ "LEFT JOIN "
				+ "`exam`.`consumer_type` `ct` "
				+ "ON "
				+ "`cps`.`consumerTypeId` = `ct`.`id`"
				+ "LEFT JOIN "
				+ "`exam`.`program_structure` `ps` "
				+ "ON "
				+ "`cps`.`programStructureId` = `ps`.`id`"
				+ "LEFT JOIN "
				+ "`exam`.`program` `p` "
				+ "ON "
				+ "`cps`.`programId` = `p`.`id` "
				+ "WHERE "
				+ "`p`.`packageId` = ?";
	
		List<PackageRequirements> requirements = jdbcTemplate.query(
				sql,
				new Object[] { packageId },
				new BeanPropertyRowMapper<PackageRequirements>(PackageRequirements.class));
		if(requirements.size() > 0) {
			return requirements;
		}
	
		return null;
	}

	private boolean checkIfUpgradePathWithIdExistsQuery(String pathId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`upgrade_paths` "
				+ "WHERE "
				+ "`pathId` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { pathId } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	
	private boolean checkIfPackageFamilyExistsInPathQuery(String pathId, String packageFamilyId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`upgrade_path_packages` "
				+ "WHERE "
				+ "`pathId` = ? "
				+ "AND "
				+ "`packageFamilyId` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { pathId, packageFamilyId } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
		

	private void addUpgradePathQuery(UpgradePath upgradePath) {
		String sql = "INSERT INTO "
				+ "`products`.`upgrade_paths` "
				+ "( `pathName` ) "
				+ "VALUES "
				+ "( ?)";
		try {
			jdbcTemplate.update(sql, new Object[] { upgradePath.getPathName() });
			returnStatus.setStatus("success");
		}catch (Exception e) {
			returnStatus.setStatus("failure");
			returnStatus.setError("" + e);
		}
	}
	
	private void updateUpgradePathQuery(UpgradePath upgradePath) {
		String sql = "UPDATE "
				+ "`products`.`upgrade_paths` "
				+ "SET "
				+ "`pathName`=? "
				+ "WHERE "
				+ "`pathId` = ?";

		try {
			jdbcTemplate.update(sql, new Object[] { upgradePath.getPathName(), upgradePath.getPathId() });
			returnStatus.setStatus("success");
		}catch (Exception e) {
			returnStatus.setStatus("failure");
			returnStatus.setError("" + e);
		}
	}

	private void deleteUpgradePathQuery(String upgradePathId) {
		String sql = "DELETE FROM "
				+ "`products`.`upgrade_paths` "
				+ "WHERE "
				+ "`pathId` = ?";
		
		try {
			jdbcTemplate.update(sql, new Object[] { upgradePathId });
			returnStatus.setStatus("success");
		}catch (Exception e) {
			returnStatus.setStatus("failure");
			returnStatus.setMessage("Error");
			returnStatus.setError("" + e);
		}
	}
	
	private UpgradePath getUpgradePathQuery(String upgradePathId) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`upgrade_paths` "
				+ "WHERE "
				+ "`pathId` = ?";

		try {
			List<UpgradePath> upgradePaths = jdbcTemplate.query(
					sql,
					new Object[] { upgradePathId },
					new BeanPropertyRowMapper<UpgradePath>(UpgradePath.class));

			if(upgradePaths.size() > 0) {
				return upgradePaths.get(0);
			}
		}catch (Exception e) {

			
		}
		
		return null;
	}

	private List<UpgradePath> getAllUpgradePathsQuery() {
		String sql = "SELECT "
				+ "`up`.*,"
				+ "COUNT(`upp`.`pathId`) AS `numberOfFamilies` "
				+ "FROM "
				+ "`products`.`upgrade_paths` `up` "
				+ "LEFT JOIN "
				+ "`products`.`upgrade_path_packages` `upp` "
				+ "ON "
				+ "`up`.`pathId` = `upp`.`pathId` "
				+ "GROUP BY "
				+ "`up`.`pathId`";

		try {
			List<UpgradePath> upgradePaths = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<UpgradePath>(UpgradePath.class));

			return upgradePaths;
		}catch (Exception e) {
			return null;
		}
	}
	
	public Feature getFeature(String featureId) {

		String sql = "SELECT * FROM `products`.`features` WHERE `featureId`=?";
		
		try {
			List<Feature> features = jdbcTemplate.query(
					sql,
					new Object[] { featureId },
					new BeanPropertyRowMapper<Feature>(Feature.class));
			if(features.size() > 0) {
				return features.get(0);
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public List<Feature> getAllFeatures() {

		String sql = "SELECT * FROM `products`.`features`";
		
		try {
			List<Feature> features = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<Feature>(Feature.class));
			if(features.size() > 0) {
				return features;
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public List<PackageFeature> getAllPackageFeatures() {

		String sql = "SELECT "
				+ "`pf`.*, "
				+ "`p`.`packageName`, "
				+ "`f`.`featureName`, "
				+ "`p`.`durationType` "
				+ "FROM "
				+ "`products`.`package_features` `pf` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`p`.`packageId` = `pf`.`packageId` "
				+ "LEFT JOIN "
				+ "`products`.`features` `f` "
				+ "ON "
				+ "`f`.`featureId` = `pf`.`featureId` ";
		
		try {
			List<PackageFeature> features = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<PackageFeature>(PackageFeature.class));
			if(features.size() > 0) {
				return features;
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public List<PackageFeature> getAllPackageFeatures(String packageId) {

		String sql = "SELECT "
				+ "`pf`.*, "
				+ "`p`.`packageName`, "
				+ "`f`.`featureName`, "
				+ "`p`.`durationType` "
				+ "FROM "
				+ "`products`.`package_features` `pf` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`p`.`packageId` = `pf`.`packageId` "
				+ "LEFT JOIN "
				+ "`products`.`features` `f` "
				+ "ON "
				+ "`f`.`featureId` = `pf`.`featureId` "
				+ "WHERE "
				+ "`p`.`packageId` = ?";
		
		try {
			List<PackageFeature> features = jdbcTemplate.query(
					sql,
					new Object[] { packageId },
					new BeanPropertyRowMapper<PackageFeature>(PackageFeature.class));
			if(features.size() > 0) {
				return features;
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public boolean checkIfPackageFeatureWithIdExists(String packageFeatureId) {
		
		return checkIfPackageFeatureWithIdExistsQuery(packageFeatureId);
	}
	

	private boolean checkIfPackageFeatureWithIdExistsQuery(String packageFeatureId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`package_features` "
				+ "WHERE "
				+ "`uid` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { packageFeatureId } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	
	public boolean checkIfEntitlementInfoExistsForPackageFeature(String packageFeatureId) {
		return checkIfEntitlementInfoExistsForPackageFeatureQuery(packageFeatureId);
	}

	private boolean checkIfEntitlementInfoExistsForPackageFeatureQuery(String packageFeatureId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`entitlements_info` "
				+ "WHERE "
				+ "`packageFeaturesId` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { packageFeatureId } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	public boolean addNewEntitlement(String packageFeatureId) {
		return addEntitlementInfoForPackageFeatureId(packageFeatureId);
	}
	
	
	private boolean addEntitlementInfoForPackageFeatureId(String packageFeatureId) {
		String sql = "INSERT INTO `products`.`entitlements_info`"
					+ "("
						+ "`packageFeaturesId`"
					+ ") "
					+ "VALUES "
					+ "("
						+ "?"
					+ ")";
		
		try {
			jdbcTemplate.update(sql, new Object[] {
					packageFeatureId
			});
			
			String entitlementId = getEntitlementId(packageFeatureId);
			insertInitialEntitlementInfo(entitlementId);
			insertEntitlementInitialStudentInfo(entitlementId);
			return true;
		}catch (Exception e) {
			
		}
		return false;
	}
	
	private String getEntitlementId(String packageFeatureId) {
		String sql = "SELECT `entitlementId` "
				+ "FROM "
				+ "`products`.`entitlements_info` "
				+ "WHERE "
				+ "`entitlementId`=?";
	
		try {
			String entitlementId = 	jdbcTemplate.queryForObject(
					sql, 
					new Object[] { packageFeatureId } , 
					String.class);
			return entitlementId;
		}catch (Exception e) {
			
		}
		return "";
	}
	

	public PackageEntitlementInfo getEntitlementWithPackageFeature(String packageFeatureId) {
		return getEntitlementWithPackageFeatureQuery(packageFeatureId);
	}	
	

	private PackageEntitlementInfo getEntitlementWithPackageFeatureQuery(String packageFeatureId) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_info` `ei`"
				+ "LEFT JOIN "
				+ "("
				+ "SELECT "
				+ "`pf`.`uid`, "
				+ "`p`.`packageName` AS `packageName`, "
				+ "`f`.`featureName` AS `featureName`, "
				+ "`p`.`durationType` AS `durationType` "
				+ "FROM "
				+ "`products`.`package_features` `pf` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`p`.`packageId` = `pf`.`packageId` "
				+ "LEFT JOIN "
				+ "`products`.`features` `f` "
				+ "ON "
				+ "`f`.`featureId` = `pf`.`featureId`"
				+ ") `extra` "
				+ "ON "
				+ "`extra`.`uid` = `ei`.`packageFeaturesId` "
				+ "WHERE "
				+ "`packageFeaturesId`= ?";
		
		try {
			List<PackageEntitlementInfo> entitlementInfos = jdbcTemplate.query(
					sql,
					new Object[] { packageFeatureId }, 
					new BeanPropertyRowMapper<PackageEntitlementInfo>(PackageEntitlementInfo.class));
			if(entitlementInfos.size() > 0) {
				return entitlementInfos.get(0);
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	

	public PackageEntitlementInfo getEntitlementWithId(String entitlementId) {
		return getEntitlementWithIdQuery(entitlementId);
	}	
	

	private PackageEntitlementInfo getEntitlementWithIdQuery(String entitlementId) {
		String sql = ""
				+ "SELECT "
					+ "* "
				+ "FROM "
				+ "`products`.`entitlements_info` `ei`"
					+ "LEFT JOIN "
					+ "("
						+ "SELECT "
						+ "`pf`.`uid`, "
						+ "`p`.`packageName` AS `packageName`, "
						+ "`f`.`featureName` AS `featureName`, "
						+ "`p`.`durationType` AS `durationType` "
						+ "FROM "
						+ "`products`.`package_features` `pf` "
						+ "LEFT JOIN "
						+ "`products`.`packages` `p` "
						+ "ON "
						+ "`p`.`packageId` = `pf`.`packageId` "
						+ "LEFT JOIN "
						+ "`products`.`features` `f` "
						+ "ON "
						+ "`f`.`featureId` = `pf`.`featureId`"
					+ ") `extra` "
					+ "ON "
					+ "`extra`.`uid` = `ei`.`packageFeaturesId` "
				+ "WHERE "
					+ "`entitlementId`= ?";
		
		try {
			List<PackageEntitlementInfo> entitlementInfos = jdbcTemplate.query(
					sql,
					new Object[] { entitlementId }, 
					new BeanPropertyRowMapper<PackageEntitlementInfo>(PackageEntitlementInfo.class));
			if(entitlementInfos.size() > 0) {
				return entitlementInfos.get(0);
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public List<PackageEntitlementInfo> getAllEntitlementInfo() {
		return getAllEntitlementInfoQuery();
	}	
	

	private List<PackageEntitlementInfo> getAllEntitlementInfoQuery() {

//		packageName
//		featureName
//		durationType
		String sql = ""
				+ "SELECT "
					+ "* "
				+ "FROM "
				+ "`products`.`entitlements_info` `ei`"
					+ "LEFT JOIN "
						+ "("
						+ "SELECT "
							+ "`pf`.`uid`, "
							+ "`p`.`packageName` AS `packageName`, "
							+ "`f`.`featureName` AS `featureName`, "
							+ "`p`.`durationType` AS `durationType` "
						+ "FROM "
						+ "`products`.`package_features` `pf` "
							+ "LEFT JOIN "
							+ "`products`.`packages` `p` "
							+ "ON "
							+ "`p`.`packageId` = `pf`.`packageId` "
							
							+ "LEFT JOIN "
							+ "`products`.`features` `f` "
							+ "ON "
							+ "`f`.`featureId` = `pf`.`featureId`"
						+ ") `extra` "
					+ "ON "
					+ "`extra`.`uid` = `ei`.`packageFeaturesId`";
		
		try {
			List<PackageEntitlementInfo> entitlementInfos = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<PackageEntitlementInfo>(PackageEntitlementInfo.class));
			if(entitlementInfos.size() > 0) {
				return entitlementInfos;
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	

	public List<PackageEntitlementInfo> getAllEntitlementInfo(String packageFeatureId) {
		return getAllEntitlementInfoQuery(packageFeatureId);
	}	
	private List<PackageEntitlementInfo> getAllEntitlementInfoQuery(String packageFeatureId) {

//		packageName
//		featureName
//		durationType
		String sql = ""
				+ "SELECT "
					+ "* "
				+ "FROM "
				+ "`products`.`entitlements_info` `ei`"
					+ "LEFT JOIN "
						+ "("
						+ "SELECT "
							+ "`pf`.`uid`, "
							+ "`p`.`packageName` AS `packageName`, "
							+ "`p`.`packageId` AS `packageId`, "
							+ "`f`.`featureName` AS `featureName`, "
							+ "`p`.`durationType` AS `durationType` "
						+ "FROM "
						+ "`products`.`package_features` `pf` "
						
							+ "LEFT JOIN "
							+ "`products`.`packages` `p` "
							+ "ON "
							+ "`p`.`packageId` = `pf`.`packageId` "
							
							+ "LEFT JOIN "
							+ "`products`.`features` `f` "
							+ "ON "
							+ "`f`.`featureId` = `pf`.`featureId`"
						+ ") `extra` "
					+ "ON "
					+ "`extra`.`uid` = `ei`.`packageFeaturesId` "
				+ "WHERE "
					+ "`packageId` IN "
					+ "("
						+ "SELECT "
						+ "`packageId` "
						+ "FROM "
						+ "`products`.`package_features` "
						+ "WHERE `uid` = ?"
					+ ")";
		
		try {
			List<PackageEntitlementInfo> entitlementInfos = jdbcTemplate.query(
					sql,
					new Object[] { packageFeatureId },
					new BeanPropertyRowMapper<PackageEntitlementInfo>(PackageEntitlementInfo.class));
			if(entitlementInfos.size() > 0) {
				return entitlementInfos;
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	
	public boolean checkIfEntitlementDependencyExists(String id) {
		
		return checkIfEntitlementDependencyExistsQuery(id);
	}
	
	private boolean checkIfEntitlementDependencyExistsQuery(String id){
		String sql = ""
				+ "SELECT "
					+ "count(*) "
				+ "FROM "
					+ "`products`.`entitlements_dependencies` "
				+ "WHERE "
					+ "`id` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { id } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	public boolean checkIfEntitlementExists(String entitlementId) {
		return checkIfEntitlementExistsQuery(entitlementId);
	}
	private boolean checkIfEntitlementExistsQuery(String entitlementId) {
		String sql = ""
				+ "SELECT "
					+ "count(*) "
				+ "FROM "
					+ "`products`.`entitlements_info` "
				+ "WHERE "
					+ "`entitlementId` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { entitlementId } , Integer.class);
			if(numResults > 0) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	}
	public List<Feature> getFeaturesNotInDependency(String entitlementId) {
		return getFeaturesNotInDependencyQuery(entitlementId);
	}
	
	private List<Feature> getFeaturesNotInDependencyQuery(String entitlementId) {
		
		String sql = "SELECT * FROM "
					+ "`products`.`features` "
				+ "WHERE "
					+ "`featureId` "
					+ "NOT IN "
					+ "("
						+ "SELECT "
						+ "`dependsOnFeatureId` AS `featureId` "
						+ "FROM "
						+ "`products`.`entitlements_dependencies`"
						+ "WHERE "
						+ "`entitlementId` = ? "
					+ ")";
		
		try {
			List<Feature> features = jdbcTemplate.query(
					sql,
					new Object[] { entitlementId },
					new BeanPropertyRowMapper<Feature>(Feature.class));
			return features;
		}catch (Exception e) {
		}
		return null;
	}
	public EntitlementDependency getEntitlementDependency(String entitlementId, String dependencyId) {
		return getEntitlementDependencyQuery(entitlementId, dependencyId);
	}
	private EntitlementDependency getEntitlementDependencyQuery(String entitlementId, String dependencyId) {

		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_dependencies` `ed`"
				+ "LEFT JOIN "
				+ "("
					+ "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`entitlements_info` `ei`"
					+ "LEFT JOIN "
					+ "("
					+ "SELECT "
					+ "`pf`.`uid`, "
					+ "`p`.`packageName` AS `packageName`, "
					+ "`f`.`featureName` AS `featureName`, "
					+ "`p`.`durationType` AS `durationType` "
					+ "FROM "
					+ "`products`.`package_features` `pf` "
					+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
					+ "ON "
					+ "`p`.`packageId` = `pf`.`packageId` "
					+ "LEFT JOIN "
					+ "`products`.`features` `f` "
					+ "ON "
					+ "`f`.`featureId` = `pf`.`featureId`"
					+ ") `extra` "
					+ "ON "
					+ "`extra`.`uid` = `ei`.`packageFeaturesId`"
				+ ") `extraData` "
				+ "ON "
				+ "`extraData`.`entitlementId` = `ed`.`entitlementId` "
				+ "WHERE "
				+ "`ed`.`entitlementId` = ? "
				+ "AND "
				+ "`ed`.`id` = ?";
		
		try {
			List<EntitlementDependency> dependencies = jdbcTemplate.query(
					sql,
					new Object[] { entitlementId, dependencyId },
					new BeanPropertyRowMapper<EntitlementDependency>(EntitlementDependency.class));
			if(dependencies.size() > 0) {
				return dependencies.get(0);
			}
		}catch (Exception e) {
		}
		return null;
	}
	public List<EntitlementDependency> getAllEntitlementDependencies() {
		return getAllEntitlementDependenciesQuery();
	}
	
	private List<EntitlementDependency> getAllEntitlementDependenciesQuery() {

		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_dependencies` `ed` "
					+ "LEFT JOIN "
					+ "("
						+ "SELECT "
						+ "* "
						+ "FROM "
						+ "`products`.`entitlements_info` `ei`"
						+ "LEFT JOIN "
						+ "("
							+ "SELECT "
							+ "`pf`.`uid`, "
							+ "`p`.`packageName` AS `packageName`, "
							+ "`f`.`featureName` AS `featureName`, "
							+ "`p`.`durationType` AS `durationType` "
							+ "FROM "
							+ "`products`.`package_features` `pf` "
							+ "LEFT JOIN "
							+ "`products`.`packages` `p` "
							+ "ON "
							+ "`p`.`packageId` = `pf`.`packageId` "
							+ "LEFT JOIN "
							+ "`products`.`features` `f` "
							+ "ON "
							+ "`f`.`featureId` = `pf`.`featureId`"
						+ ") `extra` "
						+ "ON "
						+ "`extra`.`uid` = `ei`.`packageFeaturesId`"
					+ ") `extraData` "
					+ "ON "
					+ "`extraData`.`entitlementId` = `ed`.`entitlementId` ";
		
		try {
			List<EntitlementDependency> dependencies = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<EntitlementDependency>(EntitlementDependency.class));
			if(dependencies.size() > 0) {
				return dependencies;
			}
		}catch (Exception e) {
		}
		return null;
	}
	
	public boolean checkIfInitialStudentInfoExists(String entitlementId) {
		String sql = ""
				+ "SELECT "
					+ "count(*) "
				+ "FROM "
					+ "`products`.`entitlements_initial_student_data` "
				+ "WHERE "
					+ "`entitlementId` = ?";
		try {
			int numResults = jdbcTemplate.queryForObject(sql, new Object[] { entitlementId } , Integer.class);
			if(numResults > 0) {
				return true;
			}
		}catch (Exception e) {

		}
		return insertEntitlementInitialStudentInfo(entitlementId);
	}
	
	private boolean insertEntitlementInitialStudentInfo(String entitlementId) {
		
		String sql = "INSERT INTO "
				+ "`products`.`entitlements_initial_student_data` "
				+ "("
					+ "`entitlementId`"
				+ ") "
				+ "VALUES "
				+ "("
					+ "?"
				+ ")";
	
		try {
			jdbcTemplate.update(sql, new Object[] {
					entitlementId
			});
			return true;
		}catch (Exception e) {
			
		}
		return false;
	}
	
	public StudentEntitlement getInitialStudentInfo(String entitlementId) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_initial_student_data` `eisd` "
					+ "LEFT JOIN "
					+ "("
						+ "SELECT "
						+ "* "
						+ "FROM "
						+ "`products`.`entitlements_info` `ei`"
						+ "LEFT JOIN "
						+ "("
							+ "SELECT "
							+ "`pf`.`uid`, "
							+ "`p`.`packageName` AS `packageName`, "
							+ "`f`.`featureName` AS `featureName`, "
							+ "`p`.`durationType` AS `durationType` "
							+ "FROM "
							+ "`products`.`package_features` `pf` "
							+ "LEFT JOIN "
							+ "`products`.`packages` `p` "
							+ "ON "
							+ "`p`.`packageId` = `pf`.`packageId` "
							+ "LEFT JOIN "
							+ "`products`.`features` `f` "
							+ "ON "
							+ "`f`.`featureId` = `pf`.`featureId`"
						+ ") `extra` "
						+ "ON "
						+ "`extra`.`uid` = `ei`.`packageFeaturesId`"
					+ ") `extraData` "
					+ "ON "
					+ "`extraData`.`entitlementId` = `eisd`.`entitlementId` "
				+ "WHERE "
					+ "`eisd`.`entitlementId` = ?";
		
		try {
			List<StudentEntitlement> initialInfos = jdbcTemplate.query(
					sql,
					new Object[] { entitlementId },
					new BeanPropertyRowMapper<StudentEntitlement>(StudentEntitlement.class));
			if(initialInfos.size() > 0) {
				return initialInfos.get(0);
			}
		}catch (Exception e) {
		}
		return null;
	}
	

	public List<StudentEntitlement> getAllInitialStudentInfo() {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`entitlements_initial_student_data` `eisd` "
					+ "LEFT JOIN "
					+ "("
						+ "SELECT "
						+ "* "
						+ "FROM "
						+ "`products`.`entitlements_info` `ei`"
						+ "LEFT JOIN "
						+ "("
							+ "SELECT "
							+ "`pf`.`uid`, "
							+ "`p`.`packageName` AS `packageName`, "
							+ "`f`.`featureName` AS `featureName`, "
							+ "`p`.`packageId` AS `packageId`, "
							+ "`f`.`featureId` AS `featureId`, "
							+ "`p`.`durationType` AS `durationType` "
							+ "FROM "
							+ "`products`.`package_features` `pf` "
							+ "LEFT JOIN "
							+ "`products`.`packages` `p` "
							+ "ON "
							+ "`p`.`packageId` = `pf`.`packageId` "
							+ "LEFT JOIN "
							+ "`products`.`features` `f` "
							+ "ON "
							+ "`f`.`featureId` = `pf`.`featureId`"
						+ ") `extra` "
						+ "ON "
						+ "`extra`.`uid` = `ei`.`packageFeaturesId`"
					+ ") `extraData` "
					+ "ON "
					+ "`extraData`.`entitlementId` = `eisd`.`entitlementId` ";
		
		try {
			List<StudentEntitlement> initialInfos = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<StudentEntitlement>(StudentEntitlement.class));
			if(initialInfos.size() > 0) {
				return initialInfos;
			}
		}catch (Exception e) {
		}
		return null;
	}

	public List<StudentPackageBean> getAllStudentsWithProducts(){
		return getAllStudentsWithProductsQuery();
	}
	
	private List<StudentPackageBean> getAllStudentsWithProductsQuery() {
		String sql = ""
				+ "SELECT "
				+ "* "
				+ "FROM "
					+ "`products`.`student_packages` `sp` "
				+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
				+ "ON "
					+ "`p`.`salesForceUID` = `sp`.`salesForceUID`"
				+ "LEFT JOIN "
					+ "`products`.`package_families` `pf` "
				+ "ON "
					+ "`p`.`packageFamily` = `pf`.`familyId`"
				+ "LEFT JOIN "
					+ "`exam`.`students` `s` "
				+ "ON "
					+ "`s`.`sapid` = `sp`.`sapid`";
		
		try {
			List<StudentPackageBean> packages = jdbcTemplate.query(
					sql,
//					new BeanPropertyRowMapper<StudentPackageBean>(StudentPackageBean.class));
					new RowMapper<StudentPackageBean>() {
						@Override
						public StudentPackageBean mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentPackageBean packageDetails = (new BeanPropertyRowMapper<>(StudentPackageBean.class)).mapRow(rs,rowNum);
					        StudentCareerservicesBean studentDetails = (new BeanPropertyRowMapper<>(StudentCareerservicesBean.class)).mapRow(rs,rowNum);
					        packageDetails.setStudent(studentDetails);
					        return packageDetails;
						}
					});
			return packages;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return null;
	}

}


