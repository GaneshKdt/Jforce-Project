package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nmims.helpers.DataValidationHelpers;
import com.google.gson.Gson;
import com.nmims.beans.DependencyStatus;
import com.nmims.beans.EntitlementAccessFields;
import com.nmims.beans.EntitlementDependencies;
import com.nmims.beans.EntitlementDependency;
import com.nmims.beans.PackageEntitlementInfo;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.StudentEntitlements;

public class EntitlementCheckerDAO {
	
/*
 * 	This DAO manages validation and returning of data by checking the purchase id and the entitlementid of the student
 */
	
/*
 * 	-------------- INITIALIZATIONS --------------
 */
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(EntitlementCheckerDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	DataValidationHelpers validationHelper = new DataValidationHelpers();
	Gson gson = new Gson();
/*
 * 	-------------------- END --------------------
 */

	
	
	public List<StudentCareerservicesBean> getListOfStudentsThatCanAccessFeature(String featureId){
		String sql = "SELECT "
				+ " * "
				+ "FROM "
				+ "`products`.`package_features` `pf` "
				+ "LEFT JOIN "
					+ "`products`.`entitlements_info` `ei` "
					+ "ON "
					+ "`pf`.`uid` = `ei`.`packageFeaturesId` "
				+ "LEFT JOIN "
					+ "`products`.`entitlements_student_data` `esd` "
					+ "ON "
					+ "`esd`.`entitlementId` = `ei`.`entitlementId` "
				+ "LEFT JOIN "
					+ "`exam`.`students` `students` "
					+ "ON "
					+ "`esd`.`sapid` = `students`.`sapid` "
				+ "WHERE "
				+ "`activationsLeft` > 0 "
				+ "AND "
				+ "`ended` = 0 "
				+ "AND "
				+ "`activated` = 1 "
				+ "AND "
				+ "`featureId` = ? ";
		
		List<StudentCareerservicesBean> listOfStudents = jdbcTemplate.query(
				sql, 
				new Object[] { featureId }, 
				new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));
		
		
		return listOfStudents;
	}
	
	/*
	 * 	get all the entitlements info for the student.
	 */
		private List<StudentEntitlement> getAllEntitlementInfoForPurchase(String purchaseId) {
			
			
			String sql = ""
					+ "SELECT "
					+ "* "
				+ "FROM "
				+ "`products`.`entitlements_student_data` `esd` "
					+ "LEFT JOIN "
					+ "`products`.`entitlements_info` `ei` "
					+ "ON "
					+ "`esd`.`entitlementId` = `ei`.`entitlementId` "
					
					+ "LEFT JOIN "
					+ "`products`.`package_features` `pf` "
					+ "ON "
					+ "`pf`.`uid` = `ei`.`packageFeaturesId` "
					
					+ "LEFT JOIN "
					+ "`products`.`features` `f` "
					+ "ON "
					+ "`f`.`featureId` = `pf`.`featureId` "
					
					+ "LEFT JOIN "
					+ "`products`.`packages` `p` "
					+ "ON "
					+ "`p`.`packageId` = `pf`.`packageId` "
					
					+ "LEFT JOIN "
					+ "`products`.`student_packages` `sp` "
					+ "ON "
					+ "`sp`.`paymentId` = `esd`.`purchaseId` "
					+ "WHERE "
						+ "`esd`.`purchaseId` = ? ";
			
			
			List<StudentEntitlement> entitlements = jdbcTemplate.query(sql, new Object[] { purchaseId }, 
					new RowMapper<StudentEntitlement>() {
						@Override
						public StudentEntitlement mapRow(ResultSet rs, int rowNum) throws SQLException {
							StudentEntitlement entitlement = new StudentEntitlement();
							//in case two records get returned, take the first only.
							
							entitlement.setActivatedByStudent(rs.getBoolean("activatedByStudent"));
							entitlement.setActivationDate(rs.getDate("activationDate"));
							entitlement.setActivationsLeft(rs.getInt("activationsLeft"));
							entitlement.setActivated(rs.getBoolean("activated"));
							entitlement.setDateAdded(rs.getDate("dateAdded"));
							entitlement.setDateEnded(rs.getDate("dateEnded"));
							entitlement.setDurationType(rs.getString("durationType"));
							entitlement.setPackageStartDate(rs.getDate("startDate"));
							entitlement.setPackageEndDate(rs.getDate("endDate"));
							entitlement.setPackageName(rs.getString("packageName"));
							entitlement.setEnded(rs.getBoolean("ended"));
							entitlement.setSapid(rs.getString("sapid"));
							entitlement.setPurchaseId(rs.getString("purchaseId"));
							entitlement.setEntitlementId(rs.getString("entitlementId"));
							entitlement.setFeatureName(rs.getString("featureName"));
							entitlement.setFeatureId(rs.getInt("featureId"));
							
							PackageEntitlementInfo entitlementInfo = new PackageEntitlementInfo();
							entitlementInfo.setActivationCycleMonths(rs.getInt("activationCycleMonths"));
							entitlementInfo.setActivationCycleDays(rs.getInt("activationCycleDays"));
							entitlementInfo.setActivationsEveryCycle(rs.getInt("activationsEveryCycle"));
							entitlementInfo.setDuration(rs.getInt("duration"));
							entitlementInfo.setExtendByMaxMonths(rs.getInt("extendByMaxMonths"));
							entitlementInfo.setExtendByMaxDays(rs.getInt("extendByMaxDays"));
							entitlementInfo.setExtendIfActivationsLeft(rs.getBoolean("extendIfActivationsLeft"));
							entitlementInfo.setPackageFeaturesId(rs.getString("packageFeaturesId"));
							entitlementInfo.setInitialActivations(rs.getInt("initialActivations"));
							entitlementInfo.setInitialCycleGapMonths(rs.getInt("initialCycleGapMonths"));
							entitlementInfo.setInitialCycleGapDays(rs.getInt("initialCycleGapDays"));
							entitlementInfo.setRequiresOtherEntitlement(rs.getBoolean("requiresOtherEntitlement"));
							entitlementInfo.setRequiresStudentActivation(rs.getBoolean("requiresStudentActivation"));
							entitlementInfo.setTotalActivations(rs.getInt("totalActivations"));
							
							String entitlementId = rs.getString("entitlementId");
							entitlementInfo.setEntitlementId(entitlementId);
							
							entitlement.setEntitlementInfo(entitlementInfo);

							return entitlement;
						}
					});
			return entitlements;
		}

	
	/*
	 * 	get the entitlement bean for the student.
	 */
		public StudentEntitlement getEntitlement(String purchaseId, String entitlementId) {
			
			StudentEntitlement entitlement = new StudentEntitlement();
			
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`entitlements_info` `ei` "
					+ "LEFT JOIN "
						+ "`products`.`entitlements_student_data` `esd` "
						+ "ON "
							+ "`esd`.`entitlementId` = `ei`.`entitlementId` "
					+ "WHERE "
						+ "`esd`.`purchaseId` = ? "
						+ "AND "
						+ "`esd`.`entitlementId` = ?";
			
			
			jdbcTemplate.query(
					sql, 
					new Object[] { 
							purchaseId, 
							entitlementId 
					}, 
					new RowMapper<String>() {
						@Override
						public String mapRow(ResultSet rs, int rowNum) throws SQLException {
							//in case two records get returned, take the first only.
							if(entitlement.getPurchaseId() == null) {
								entitlement.setActivatedByStudent(rs.getBoolean("activatedByStudent"));
								entitlement.setActivationDate(rs.getDate("activationDate"));
								entitlement.setActivationsLeft(rs.getInt("activationsLeft"));
								entitlement.setActivated(rs.getBoolean("activated"));
								entitlement.setDateAdded(rs.getDate("dateAdded"));
								entitlement.setDateEnded(rs.getDate("dateEnded"));
								entitlement.setEnded(rs.getBoolean("ended"));
								entitlement.setPurchaseId(purchaseId);
								entitlement.setSapid(rs.getString("sapid"));
								
								PackageEntitlementInfo entitlementInfo = new PackageEntitlementInfo();
								entitlementInfo.setDuration(rs.getInt("duration"));
								entitlementInfo.setExtendByMaxMonths(rs.getInt("extendByMaxMonths"));
								entitlementInfo.setExtendIfActivationsLeft(rs.getBoolean("extendIfActivationsLeft"));
								entitlementInfo.setPackageFeaturesId(rs.getString("packageFeaturesId"));
								entitlementInfo.setInitialActivations(rs.getInt("initialActivations"));
								entitlementInfo.setInitialCycleGapMonths(rs.getInt("initialCycleGapMonths"));
								entitlementInfo.setInitialCycleGapDays(rs.getInt("initialCycleGapDays"));
								entitlementInfo.setActivationCycleMonths(rs.getInt("activationCycleMonths"));
								entitlementInfo.setActivationCycleDays(rs.getInt("activationCycleDays"));
								entitlementInfo.setRequiresOtherEntitlement(rs.getBoolean("requiresOtherEntitlement"));
								entitlementInfo.setRequiresStudentActivation(rs.getBoolean("requiresStudentActivation"));
								entitlementInfo.setTotalActivations(rs.getInt("totalActivations"));
								entitlementInfo.setEntitlementId(entitlementId);
								
								entitlement.setEntitlementInfo(entitlementInfo);
								
								EntitlementDependencies dependencies;
								if(entitlement.getEntitlementInfo().isRequiresOtherEntitlement()) {
									dependencies = getDependencies(entitlementId);
								}else {
									dependencies = new EntitlementDependencies();
									//initialize to avoid error
									dependencies.setDependencies(new ArrayList<EntitlementDependency>());
								}
								entitlement.getEntitlementInfo().setDependencies(dependencies);
							}
							return null;
						}
					});
			
			return entitlement;
		}

		/*
		 * 	check if all dependency conditions are fulfilled for this entitlement
		 */
			public EntitlementDependencies getDependencies(String entitlementId) {
				
				String sql = "SELECT "
						+ "* "
						+ "FROM "
						+ "`products`.`entitlements_dependencies` "
						+ "WHERE "
							+ "`entitlementId` = ?";
				
				List<EntitlementDependency> dependenciesList = jdbcTemplate.query(
						sql, 
						new Object[] { entitlementId }, 
						new BeanPropertyRowMapper<EntitlementDependency>(EntitlementDependency.class));
				EntitlementDependencies dependencies = new EntitlementDependencies();
				
				dependencies.setDependencies(dependenciesList);
				return dependencies;
			}
				

	/*
	 * 	get latest instance of this entitlement that this student purchased
	 */
		private StudentEntitlement getLatestStudentEntitlementFeature(String sapid, String featureId) {
			StudentEntitlement entitlement = new StudentEntitlement();
			
			String sql = "SELECT * "
					+ "FROM "
					+ "`products`.`entitlements_info` `ei` "
					+ "LEFT JOIN "
					+ "`products`.`entitlements_student_data` `esd` "
					+ "ON `esd`.`entitlementId` = `ei`.`entitlementId` "
					+ "LEFT JOIN "
					+ "`products`.`package_features` `pf` "
					+ "ON `ei`.`packageFeaturesId` = `pf`.`uid` "
					+ "WHERE "
					+ "`esd`.`sapid` = ? "
					+ "AND "
					+ "`pf`.`featureId` = ? "
					+ "ORDER BY dateAdded DESC "
					+ "LIMIT 1";
			
			jdbcTemplate.query(sql, new Object[] { sapid, featureId }, 
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						//in case two records get returned, take the first only.
						if(entitlement.getPurchaseId() == null) {
							entitlement.setActivatedByStudent(rs.getBoolean("activatedByStudent"));
							entitlement.setActivationDate(rs.getDate("activationDate"));
							entitlement.setActivationsLeft(rs.getInt("activationsLeft"));
							entitlement.setActivated(rs.getBoolean("activated"));
							entitlement.setDateAdded(rs.getDate("dateAdded"));
							entitlement.setDateEnded(rs.getDate("dateEnded"));
							entitlement.setEnded(rs.getBoolean("ended"));
							entitlement.setPurchaseId(rs.getString("purchaseId"));
							entitlement.setSapid(rs.getString("sapid"));
							
							PackageEntitlementInfo entitlementInfo = new PackageEntitlementInfo();
							entitlementInfo.setDuration(rs.getInt("duration"));
							entitlementInfo.setExtendByMaxMonths(rs.getInt("extendByMaxMonths")); 
							entitlementInfo.setExtendIfActivationsLeft(rs.getBoolean("extendIfActivationsLeft"));
							entitlementInfo.setPackageFeaturesId(rs.getString("packageFeaturesId"));
							entitlementInfo.setInitialCycleGapMonths(rs.getInt("initialCycleGapMonths"));
							entitlementInfo.setInitialCycleGapDays(rs.getInt("initialCycleGapDays"));
							entitlementInfo.setActivationCycleMonths(rs.getInt("activationCycleMonths"));
							entitlementInfo.setActivationCycleDays(rs.getInt("activationCycleDays"));
							entitlementInfo.setRequiresOtherEntitlement(rs.getBoolean("requiresOtherEntitlement"));
							entitlementInfo.setRequiresStudentActivation(rs.getBoolean("requiresStudentActivation"));
							entitlementInfo.setTotalActivations(rs.getInt("totalActivations"));
							entitlementInfo.setEntitlementId(rs.getString("entitlementId"));
							
							entitlement.setEntitlementInfo(entitlementInfo);
							
							EntitlementDependencies dependencies;
							if(entitlement.getEntitlementInfo().isRequiresOtherEntitlement()) {
								dependencies = getDependencies(rs.getString("entitlementId"));
							}else {
								dependencies = new EntitlementDependencies();
								//initialize to avoid error
								dependencies.setDependencies(new ArrayList<EntitlementDependency>());
							}
							entitlement.getEntitlementInfo().setDependencies(dependencies);
						}
						return null;
					}
				}
			);
			return entitlement;
		}
		
	/*
	 * 	Check if student has access to this entitlement against this purchaseId
	 */
		public EntitlementAccessFields checkAccess(String purchaseId, String entitlementId) {
			
			EntitlementAccessFields entitlementAccessFields = new EntitlementAccessFields();

			entitlementAccessFields.setUserHasAccess(true);
			//get the whole student_entitlement tabledata for this user and put it in an entitlement bean
			StudentEntitlement studentEntitlement = getEntitlement(purchaseId, entitlementId);
			
			entitlementAccessFields.setStudentEntitlementInfo(studentEntitlement);
			
			//use this entitlement bean to check various user details.
			entitlementAccessFields = checkIfEntitlementActivated(studentEntitlement, entitlementAccessFields);
			entitlementAccessFields = checkDependencies(studentEntitlement, entitlementAccessFields);
			entitlementAccessFields = checkIfManualActivationRequired(studentEntitlement, entitlementAccessFields);
			entitlementAccessFields = checkIfValidityExpired(studentEntitlement, entitlementAccessFields);
			entitlementAccessFields = getActivationsLeft(studentEntitlement, entitlementAccessFields);
			
			//check each condition and set the value now

				//does this user have any activations left and can they activate after all activations are consumed?
				if(studentEntitlement.getActivationsLeft() < 0 && studentEntitlement.getEntitlementInfo().isGiveAccessAfterActivationsConsumed() == false) {
					entitlementAccessFields.setUserHasAccess(false);
				}
				//does this user have any activations left and can they activate after all activations are consumed?
				if(studentEntitlement.getActivationsLeft() < 0 && studentEntitlement.getEntitlementInfo().isGiveAccessAfterActivationsConsumed() == false) {
					entitlementAccessFields.setUserHasAccess(false);
				}
			return entitlementAccessFields;
		}
	
		private EntitlementAccessFields checkIfValidityExpired(StudentEntitlement studentEntitlement, EntitlementAccessFields entitlementAccessFields) {
			Date startDate = studentEntitlement.getDateAdded();
			int duration = studentEntitlement.getEntitlementInfo().getDuration();

			//Add extra months to duration if some activations are left and the extend if activations are left is true
				int activationsLeft = studentEntitlement.getActivationsLeft();
				boolean extendIfActivationsLeft = studentEntitlement.getEntitlementInfo().isExtendIfActivationsLeft();
				
				if(activationsLeft > 0 && extendIfActivationsLeft) {
					duration = duration + studentEntitlement.getEntitlementInfo().getExtendByMaxMonths();
				}
			//end 
			Date tentativeEndDate = validationHelper.addMonthsToDate(startDate, duration);
			
			boolean validityHasEnded = validationHelper.checkIfDateBeforeCurrent(tentativeEndDate);
			if(validityHasEnded) {
				entitlementAccessFields.setEntitlementValidityExpired(true);
			}else {
				entitlementAccessFields.setEntitlementValidityExpired(false);
			}
			
			return entitlementAccessFields;
		}
		/*
	 * 	checks if the entitlement activated key is true. 
	 * 	also checks for package expiry and entitlement expiry
	 * 	also checks if entitlement accessible after 0 activations
	 * 	also checks if entitlement accessible after package expiry
	 * 	set activated key to false if the checks for these conditions return false
	 */
		private EntitlementAccessFields checkIfEntitlementActivated(StudentEntitlement studentEntitlement, EntitlementAccessFields entitlementAccessFields) {

			if(studentEntitlement.isActivated()) {
//				entitlementAccessFields.
			}
			return entitlementAccessFields;
		}
		
	/*
	 * 	check if all dependency conditions are fulfilled for this entitlement
	 */
		private EntitlementAccessFields checkDependencies(StudentEntitlement entitlement, EntitlementAccessFields entitlementAccessFields) {
			//if the entitlement requires any other entitlement.
			if(entitlement.getEntitlementInfo().isRequiresOtherEntitlement()) {
				//get all dependencies of this entitlement
				EntitlementDependencies dependencies = entitlement.getEntitlementInfo().getDependencies();
				List<DependencyStatus> dependenciesStatusList = new ArrayList<DependencyStatus>();
				
				//loop through the dependencies. check the status of each and add the results to the "dependency status" field
				//if any conditions are false, set the dependenciesNotFulfilled field as false
				for(EntitlementDependency dependency: dependencies.getDependencies()) {
					
					String dependsOnFeatureId = dependency.getDependsOnFeatureId();
					
					DependencyStatus status = new DependencyStatus();
					
					status.setFeatureId(dependsOnFeatureId);
					//fulfilled remains false at start because the feature might not exist for the user.
					status.setDependencyFulfilled(false);
					
					//get the latest purchased entitlement with this featureid
					StudentEntitlement studentEntitlement = getLatestStudentEntitlementFeature(entitlement.getSapid(), dependency.getDependsOnFeatureId());
					
					if(studentEntitlement.getPurchaseId() != null){
						
						//dependency feature found for the user. now check the conditions
						status.setDependencyFulfilled(true);
	
						status = checkRequiresActivationCondition(status, studentEntitlement, dependency);
						status = checkRequiresCompletionCondition(status, studentEntitlement, dependency);
						status = checkMinActionsCondition(status, studentEntitlement, dependency);
						
					}
					//add the status of this dependency to the list
					dependenciesStatusList.add(status);
				}
				
				entitlementAccessFields.setDependenciesStatusList(dependenciesStatusList);

				//initialize as true
				entitlementAccessFields.setDependenciesFulfilled(true);
				
				//check for an unfulfilled dependency
				for(DependencyStatus status:dependenciesStatusList) {
					
					//if an unfulfilled dependency is found, set dependenciesfulfilled as false
					if(status.isDependencyFulfilled() == false) {
						entitlementAccessFields.setDependenciesFulfilled(false);
					}
				}
			}else {
				entitlementAccessFields.setDependenciesFulfilled(true);
			}
			return entitlementAccessFields;
		}
		/*
		 * 	functions to assist readability of the above function
		 */
			private DependencyStatus checkRequiresActivationCondition(DependencyStatus status, StudentEntitlement studentEntitlement, EntitlementDependency dependency ) {
			
				//Requires Activation condition
	
				boolean requiresActivation = dependency.isRequiresActivationOnly();
				if(requiresActivation) {
					if(studentEntitlement.isActivated()) {
						status.setRequiresActivationConditionFulfilled(true);
						
						//months after activation condition
						int monthsAfterActivation = dependency.getMonthsAfterActivation();
						
						Date dependencyActivationDate = studentEntitlement.getActivationDate();
						Date dependencyMonthsAfterActivationConditionFulfillDate = validationHelper.addMonthsToDate(dependencyActivationDate, monthsAfterActivation);
						status.setMonthsAfterActivationConditionWillActivateOn(dependencyMonthsAfterActivationConditionFulfillDate);
						
						if(validationHelper.checkIfDateBeforeCurrent(dependencyMonthsAfterActivationConditionFulfillDate)) {
							status.setMonthsAfterActivationConditionFulfilled(true);
						}else {
							status.setMonthsAfterActivationConditionFulfilled(false);
							
							
							//get the months and days to the date. returns like 2 months 3 days 
							int monthsBetweenDates = validationHelper.getMonthsBetweenCurrentDateAndRequiredDate(dependencyMonthsAfterActivationConditionFulfillDate);
							long daysBetweenDates = validationHelper.getDaysToDate(dependencyMonthsAfterActivationConditionFulfillDate);
							
							
							status.setMonthsAfterActivationConditionMonthsTotal(dependency.getMonthsAfterActivation());
							status.setMonthsAfterActivationConditionMonthsLeft(monthsBetweenDates);
							status.setMonthsAfterActivationConditionDaysLeft(daysBetweenDates);
							
							status.setDependencyFulfilled(false);
						}
						//condition end
						
						
					}else {
						status.setRequiresActivationConditionFulfilled(false);
						status.setDependencyFulfilled(false);
						
						status.setMonthsAfterActivationConditionFulfilled(false);
						status.setMonthsAfterActivationConditionMonthsTotal(dependency.getMonthsAfterActivation());
					}
				}else {
					status.setRequiresActivationConditionFulfilled(true);
					status.setMonthsAfterActivationConditionFulfilled(true);
				}
				return status;
			}
			private DependencyStatus checkRequiresCompletionCondition(DependencyStatus status, StudentEntitlement studentEntitlement, EntitlementDependency dependency ) {
			
				//Requires Completion condition
				boolean requiresCompletion = dependency.isRequiresCompletion();
				if(requiresCompletion) {
					if(studentEntitlement.isEnded()) {
						status.setRequiresCompletionConditionFulfilled(true);
						
						//Months after completion condition
						int monthsAfterCompletion = dependency.getMonthsAfterCompletion();
						Date dependencyCompletionDate = studentEntitlement.getDateEnded();
						Date dependencyMonthsAfterCompletionConditionFulfillDate = validationHelper.addMonthsToDate(dependencyCompletionDate, monthsAfterCompletion);
						status.setMonthsAfterCompletionConditionWillActivateOn(dependencyMonthsAfterCompletionConditionFulfillDate);
						
						if(validationHelper.checkIfDateBeforeCurrent(dependencyMonthsAfterCompletionConditionFulfillDate)) {
							status.setMonthsAfterCompletionConditionFulfilled(true);
						}else {
							status.setMonthsAfterCompletionConditionFulfilled(false);
							status.setDependencyFulfilled(false);
							

							//get the months and days to the date. returns like 2 months 3 days 
							int monthsBetweenDates = validationHelper.getMonthsBetweenCurrentDateAndRequiredDate(dependencyMonthsAfterCompletionConditionFulfillDate);
							long daysBetweenDates = validationHelper.getDaysToDate(dependencyMonthsAfterCompletionConditionFulfillDate);
							
							
							status.setMonthsAfterCompletionConditionMonthsTotal(dependency.getMonthsAfterCompletion());
							status.setMonthsAfterCompletionConditionMonthsLeft(monthsBetweenDates);
							status.setMonthsAfterCompletionConditionDaysLeft(daysBetweenDates);
							
							status.setMonthsAfterCompletionConditionMonthsTotal(dependency.getMonthsAfterCompletion());
						}
						//condition end
						
						
					}else {
						status.setRequiresCompletionConditionFulfilled(false);
						status.setDependencyFulfilled(false);
		
						status.setMonthsAfterCompletionConditionFulfilled(false);
						status.setMonthsAfterCompletionConditionMonthsTotal(dependency.getMonthsAfterCompletion());
					}
				}else {
					status.setRequiresCompletionConditionFulfilled(true);
					status.setMonthsAfterCompletionConditionFulfilled(true);
				}
				return status;
			}	
			private DependencyStatus checkMinActionsCondition(DependencyStatus status, StudentEntitlement studentEntitlement, EntitlementDependency dependency ) {
				
				//min activations condition
				
				int minActionsRequired = dependency.getActivationsMinimumRequired();
				
				int totalActionsTaken = studentEntitlement.getEntitlementInfo().getTotalActivations() 
										- studentEntitlement.getActivationsLeft();
				
				boolean minActionsConditionStatus = minActionsRequired <= totalActionsTaken;
				
				if(minActionsConditionStatus) {
					status.setMinimumActivationsRequiredConditionActivationsLeft(0);
					status.setMinimumActivationsRequiredConditionFulfilled(true);
				}else {
					status.setMinimumActivationsRequiredConditionActivationsLeft(minActionsRequired - totalActionsTaken);
					status.setMinimumActivationsRequiredConditionFulfilled(false);
					status.setDependencyFulfilled(false);
				}
				return status;
			}

	/*
	 * 	check if entitlement requires manual activation
	 */
		private EntitlementAccessFields checkIfManualActivationRequired(StudentEntitlement entitlement, EntitlementAccessFields entitlementAccessFields) {			
			boolean requiresManualActivation = entitlement.getEntitlementInfo().isRequiresStudentActivation();
			boolean activatedByStudent = entitlement.isActivatedByStudent();
			
			if(activatedByStudent) {
				entitlementAccessFields.setManualActivationComplete(true);
			}else if(!requiresManualActivation) {
				entitlementAccessFields.setManualActivationComplete(true);
			}else {
				entitlementAccessFields.setManualActivationComplete(false);
			}
			return entitlementAccessFields;
		}
			
	
	/*
	 * 	get number of activations left and total activations
	 */
		private EntitlementAccessFields getActivationsLeft(StudentEntitlement entitlement, EntitlementAccessFields entitlementAccessFields) {
			int activationsLeft = entitlement.getActivationsLeft();
			entitlementAccessFields.setActivationsLeft(activationsLeft);
			entitlementAccessFields.setTotalActivations(entitlement.getEntitlementInfo().getTotalActivations());
			return entitlementAccessFields;
		}
			
		
	/*
	 * 	get info for this entitlement
	 */
		public StudentEntitlement getEntitlementInfoForStudentEntitlement(StudentEntitlement studentEntitlement) {
			
			String sql = "SELECT "
					+ "* "
					+ "FROM "
					+ "`products`.`entitlements_info` "
					+ "WHERE "
					+ "`entitlementId` = ?";
			
			List<PackageEntitlementInfo> packageEntitlementInfos = jdbcTemplate.query(
					sql, 
					new Object[] { studentEntitlement.getEntitlementId() }, 
					new BeanPropertyRowMapper<PackageEntitlementInfo>(PackageEntitlementInfo.class));
			
			if(packageEntitlementInfos.size() > 0) {
				studentEntitlement.setEntitlementInfo(packageEntitlementInfos.get(0));
			}
			return studentEntitlement;
		}
			
		
	public List<StudentEntitlements> getAllStudentEntitlements(String sapid){
		
		List<StudentEntitlements> listOfEntitlementsPurchaseInfo = getAllStudentEntitlementsPurchaseInfoQuery(sapid);
		List<StudentEntitlements> listOfStudentEntitlements = new ArrayList<StudentEntitlements>();
		
		for (StudentEntitlements studentEntitlement : listOfEntitlementsPurchaseInfo) {
			
			studentEntitlement.setEntitlements(getAllEntitlementInfoForPurchase(studentEntitlement.getPurchaseId()));
			listOfStudentEntitlements.add(studentEntitlement);

		}
		return listOfStudentEntitlements;
	}
	
	private List<StudentEntitlements> getAllStudentEntitlementsPurchaseInfoQuery(String sapid) {


		String sql = "SELECT "
				+ "`paymentId` AS `purchaseId`, "
				+ "`startDate` AS `startDate`, "
				+ "`endDate` AS `endDate`, "
				+ "`familyName` AS `packageName`, "
				+ "`pf`.`description` AS `packageDescription` , "
				+ "`pf`.`familyId` AS `familyId` " 
				+ "FROM "
				+ "`products`.`student_packages` `sp` "
				+ "LEFT JOIN "
				+ "`products`.`packages` `p` "
				+ "ON "
				+ "`p`.`salesForceUID` = `sp`.`salesForceUID` "
				+ "LEFT JOIN "
				+ "`products`.`package_families` `pf` "
				+ "ON "
				+ "`p`.`packageFamily` = `pf`.`familyId` "
				+ "WHERE "
				+ "`sapid` = ?";
		try {

			List<StudentEntitlements> entitlementPurchaseList = jdbcTemplate.query(
					sql, 
					new Object[] { sapid }, 
					new BeanPropertyRowMapper<StudentEntitlements>(StudentEntitlements.class));
			return entitlementPurchaseList;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
		
	}
	/*
 * 	overloaded functions to make them accessible from controller functions
 * 	if null is returned it means entitlement was not found.
 */
	public boolean checkIfEntitlementActivated(String purchaseId, String entitlementId) {
		return getEntitlement(purchaseId, entitlementId).isActivated();
	}
	public int getActivationsLeft(String purchaseId, String entitlementId) {
		//returns a map of totalactivations and number of activations left
		return getActivationsLeft(getEntitlement(purchaseId, entitlementId), new EntitlementAccessFields()).getActivationsLeft();
	}
	public boolean checkIfManualActivationRequired(String purchaseId, String entitlementId) {
		
		return getEntitlement(purchaseId, entitlementId).getEntitlementInfo().isRequiresStudentActivation();
	}
	public List<DependencyStatus> checkDependencies(String purchaseId, String entitlementId) {
		return checkDependencies(getEntitlement(purchaseId, entitlementId), new EntitlementAccessFields()).getDependenciesStatusList();
	}
				
}
