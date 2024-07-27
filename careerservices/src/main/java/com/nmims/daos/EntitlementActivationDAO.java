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

import com.google.gson.Gson;
import com.nmims.beans.ActivationAvailability;
import com.nmims.beans.EntitlementDependency;
import com.nmims.beans.PackageEntitlementInfo;
import com.nmims.beans.StudentEntitlement;
import com.nmims.helpers.DataValidationHelpers;

public class EntitlementActivationDAO {
	Gson gson = new Gson();

/*
 * 	-------------- INITIALIZATIONS --------------
 */
	private static final Logger logger = LoggerFactory.getLogger(EntitlementActivationDAO.class);
	 
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
	SessionAttendanceDao sessionAttendanceDao = new SessionAttendanceDao();
/*
 * 	-------------------- END --------------------
 */
	/*
	 * 	get latest entitlement
	 */
		public StudentEntitlement getApplicableEntitlementForPurchase(String sapid, int featureId) {
			return getApplicableStudentEntitlement(sapid, featureId);
		}
		
		/*
		 * 	get the latest student entitlement
		 */
			private StudentEntitlement getApplicableStudentEntitlement(String sapid, int featureId) {
		
				List<StudentEntitlement> entitlements = getStudentEntitlementsForFeature(sapid, featureId);
				
				if(entitlements == null) {
					return null;
				}
				if(entitlements.size() > 0) {
					StudentEntitlement entitlementToReturn = new StudentEntitlement();
					//currently assuming that the entitlement activations are "additive" rather than "expiring".
					entitlementToReturn = entitlements.get(0);
					
					//ordered by descending. all elements are the previously purchased package with this entitlement.
					for(StudentEntitlement entitlement: entitlements) {
						//if no activations are left, and this is the latest entitlement all other(previously purchased) 
						//entitlements are either expired or have no activations left.
						//return the previously found entitlementToReturn

						if(entitlement.getActivationsLeft() > 0 && 
								//double check to see if the total activations for this entitlement were greater than 0
								entitlement.getEntitlementInfo().getTotalActivations() > 0) {
							
							entitlementToReturn = entitlement;

							if(checkIfDependenciesFulfilled(entitlementToReturn, sapid, featureId)) {
								entitlementToReturn.setNextAvailableDate(nextActivationAvailableDate(entitlementToReturn));
							}
							if(entitlement.getDateEnded() != null) {
								if(validationHelper.checkIfDateBeforeCurrent(entitlement.getDateEnded())) {
									entitlementToReturn.setEnded(true);
									return entitlementToReturn;
								}
							}

						}
					}
					return entitlementToReturn;
				}
				return null;
			}
				
			

			public boolean checkIfDependenciesFulfilled(StudentEntitlement entitlement, String sapid, int featureId) {
				List<EntitlementDependency> dependencies = getDependencies(entitlement.getEntitlementId());
				if(dependencies.size() == 0) {
					return true;
				}
				for(EntitlementDependency dependency: dependencies) {
					int monthsAfterCompletion = dependency.getMonthsAfterCompletion();
					int monthsAfterActivation = dependency.getMonthsAfterActivation();
//					int daysAfterCompletion = dependency.get
//					int daysAfterActivation = dependency.getMonthsAfterActivation();
					
					try {
						List<StudentEntitlement> studentEntitlements = getStudentEntitlementsForFeature(
								sapid, 
								Integer.parseInt(dependency.getDependsOnFeatureId())
								);

						for (StudentEntitlement studentEntitlement : studentEntitlements) {
							boolean toReturn = true;
							if(dependency.isRequiresCompletion()) {
								Date endDate = null;
								if(studentEntitlement.getDateEnded() != null) {
									endDate = studentEntitlement.getDateEnded();
								}else {
									if(studentEntitlement.getActivationDate() != null) {
										endDate = validationHelper.addMonthsToDate(entitlement.getActivationDate(), studentEntitlement.getEntitlementInfo().getDuration());
									}
								}
								if(endDate != null &&validationHelper.checkIfDateBeforeCurrent(endDate)) {
									Date canActivateDate = validationHelper.addMonthsToDate(endDate, monthsAfterCompletion);
									if(validationHelper.checkIfDateBeforeCurrent(canActivateDate)) {
										if(toReturn) {
											toReturn = true;
										}
									}else {
										toReturn = false;
									}
								}else {
									toReturn = false;
								}
							}
							if(dependency.isRequiresActivationOnly()) {
								if(studentEntitlement.getActivationDate() != null) {
									Date activationDate = studentEntitlement.getActivationDate();
									if(validationHelper.checkIfDateBeforeCurrent(activationDate)) {
										Date canActivateDate = validationHelper.addMonthsToDate(activationDate, monthsAfterActivation);
										if(validationHelper.checkIfDateBeforeCurrent(canActivateDate)) {
											if(toReturn) {
												toReturn = true;
											}
										}else {
											toReturn = false;
										}
									}else {
										toReturn = false;
									}
								}else {
									toReturn = false;
								}
							}
							int activationsCompleted = studentEntitlement.getEntitlementInfo().getTotalActivations() - studentEntitlement.getActivationsLeft();
							if(activationsCompleted >= dependency.getActivationsMinimumRequired()) {
								if(toReturn) {
									toReturn = true;
								}
							}else {
								toReturn = false;
							}
							
							if(toReturn) {
								return true;
							}
						}
					}catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
					
				}
				return false;
			}
			
			private List<EntitlementDependency> getDependencies(String entitlementId) {
				
				String sql = ""
						+ "SELECT "
							+ "* "
						+ "FROM "
						+ "`products`.`entitlements_dependencies` "
						+ "WHERE "
							+ "`entitlementId`=?";
				try {
					List<EntitlementDependency> dependencies = jdbcTemplate.query(
							sql, 
							new Object[] {entitlementId}, 
							new BeanPropertyRowMapper<EntitlementDependency>(EntitlementDependency.class)
					);
					return dependencies;
				}catch (Exception e) {
					logger.info("exception : "+e.getMessage());
				}
				return new ArrayList<EntitlementDependency>();
			}
			
	public List<StudentEntitlement> getStudentEntitlementsForFeature(String sapid, int featureId){
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
					+ "`pf`.`featureId`= ?  "
					+ "AND "
					+ "`sp`.`sapid`= ? "
				+ "ORDER BY "
					+ "`dateAdded` DESC";
		try {
			List<StudentEntitlement> entitlements = jdbcTemplate.query(sql, new Object[] { featureId, sapid }, 
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
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public boolean checkIfStudentActivatedSessionWithThisId(String sapid, String sessionId) {

		//check session_attendance_feedback if a student with sapid has viewed this session
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`session_attendance` `saf` "
				+ "WHERE "
				+ "`sapid` = ? "
				+ "AND "
				+ "`sessionId` = ?";
		int count = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { 
					sapid, 
					sessionId 
				}, 
				Integer.class);
	    if (count > 0) {
	      return true;
	    }
		return false;
	}
	
	public boolean consumeActivation(StudentEntitlement entitlement, String webinarId, int attendanceFeedbackId) {
		String purchaseId = entitlement.getPurchaseId();
		
		//Add to student activated webinars table
		if(addWebinarActivation(purchaseId, webinarId, attendanceFeedbackId)) {
			reduceTotalActivationsLeft(entitlement);
		}
		return true;
	}

	private void reduceTotalActivationsLeft(StudentEntitlement entitlement) {
		String sql = "UPDATE "
				+ "`products`.`entitlements_student_data` "
				+ "SET "
				+ "`activationsLeft`= ? "
				+ "WHERE"
				+ "`entitlementId` = ? "
				+ "AND "
				+ "`purchaseId` = ?";
		
		try {
			jdbcTemplate.update(
					sql,
					new Object[] { 
							(entitlement.getActivationsLeft() - 1),
							entitlement.getEntitlementId(),
							entitlement.getPurchaseId()
					}
			);
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
	}
	
	private boolean addWebinarActivation(String purchaseId, String webinarId, int attendanceFeedbackId) {

		try {
			String sql = "INSERT INTO "
					+ "`products`.`webinar_activations` "
					+ "( `purchaseId`, `webinarId`, `attendanceFeedbackId` ) "
					+ "VALUES "
					+ "( "
					+ "?, ?, ?"
					+ ")";
			jdbcTemplate.update(sql, new Object[] { purchaseId, webinarId, attendanceFeedbackId});
			
			
		}catch(Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
		
		return true;
	}

	
	/*
	 * 	Returns how many activations for this entitlement is left
	 */
		public int getActivationsLeft(String purchaseId, String entitlementId) {
			
			
			return entitlementCheckerDAO.getActivationsLeft(purchaseId, entitlementId);
		}


	/*
	 * 	returns the number of activations a user can currently perform
	 */
		public int activationsCurrentlyPossible(StudentEntitlement entitlement){
			
			List<ActivationAvailability> activationSchedule = getActivationSchedule(entitlement);
	
			//total activations available for a user of this entitlement
			int totalActivationsAvailable = entitlement.getEntitlementInfo().getTotalActivations();
			
			//total activations left for the user to consume
			int totalActivationsLeft = entitlement.getActivationsLeft();
			
			//total activations consumed by this user
			int totalActivationsConsumed = totalActivationsAvailable - totalActivationsLeft;
			
			//total activations granted to this user by virtue of time passed
			int totalActivationsGranted = 0;
			
			if(activationSchedule != null) {
				//loop to increase the number of activations granted
				for(ActivationAvailability activationAvailability: activationSchedule) {
					if(validationHelper.checkIfDateBeforeCurrent(activationAvailability.getAvailablilityDate())) {
						totalActivationsGranted += activationAvailability.getAdditionalAvailabile();
					}
				}
			}
			
			//get the gross amount of activations that the user can use at this date
			int activationsLeft = totalActivationsGranted - totalActivationsConsumed;
			
			return activationsLeft;
		}
		
	/*
	 * 	returns the date when the next activition will be available.
	 * 	returns null if all activations are complete
	 */
		public Date nextActivationAvailableDate(StudentEntitlement entitlement){
			
			//get the schedule of the dates additional activations will be available for the user.
			List<ActivationAvailability> activationSchedule = getActivationSchedule(entitlement);

			//loop through the schedule
			
			if(activationSchedule != null) {
				for(ActivationAvailability activationAvailability: activationSchedule) {
					if(!(validationHelper.checkIfDateBeforeCurrent(activationAvailability.getAvailablilityDate()))) {
						//return the first date thats not before the current date
						return activationAvailability.getAvailablilityDate();
					}
				}
			}
			//return null if no more activations are possible
			return null;
		}

	/*
	 * 	returns the date when the next activation will be available.
	 * 	returns null if all activations are complete
	 */
		public int activationsAvailableOnNextCycleCompletion(StudentEntitlement entitlement){

			//get the schedule of the dates additional activations will be available for the user.
			List<ActivationAvailability> activationSchedule = getActivationSchedule(entitlement);
			//loop through the schedule
			if(activationSchedule != null) {
				for(ActivationAvailability activationAvailability: activationSchedule) {
					if(!(validationHelper.checkIfDateBeforeCurrent(activationAvailability.getAvailablilityDate()))) {
						//return the first date thats not before the current date
						return activationAvailability.getAdditionalAvailabile();
					}
				}
			}

			//return 0 if no more activations are possible
			return 0;
		}
	
	/*
	 * 	returns the list of dates of entitlement availability and number of entitlements granted
	 */
		public List<ActivationAvailability> getActivationSchedule(StudentEntitlement entitlement){
			
			List<ActivationAvailability> activationSchedule = new ArrayList<ActivationAvailability>();
			
			int activationsAvailable = entitlement.getEntitlementInfo().getTotalActivations();
			int totalMonths = entitlement.getEntitlementInfo().getDuration();
			int totalMonthsSinceInitialActivations = 0;
			//when the package was activated.
			Date activationDate = entitlement.getActivationDate();
			
			if(activationDate == null) {
				return null;
			}
			
			if(!(activationsAvailable > 0 && totalMonthsSinceInitialActivations < totalMonths)) {
				return null;
			}
			//gap and number of initial acivations
			int initialCycleGapMonths = entitlement.getEntitlementInfo().getInitialCycleGapMonths();
			int initialCycleGapDays = entitlement.getEntitlementInfo().getInitialCycleGapDays();
			int numInitialActivations = entitlement.getEntitlementInfo().getInitialActivations();
	
			//get the date when the next activations will be available
			Date activationDateAddingMonth = validationHelper.addMonthsToDate(activationDate, initialCycleGapMonths);
			Date dateOfInitialActivationsAvailability = validationHelper.addDaysToDate(activationDateAddingMonth, initialCycleGapDays);
			
			//add the date to the List
			ActivationAvailability initialActivationAvailability = new ActivationAvailability();
	
			initialActivationAvailability.setAvailablilityDate(dateOfInitialActivationsAvailability);
			initialActivationAvailability.setAdditionalAvailabile(numInitialActivations);
			
			activationSchedule.add(initialActivationAvailability);
			
			//reduce the number of activations available
			activationsAvailable = activationsAvailable - numInitialActivations;
			
			//stores the date of the last activation availability
			Date lastActivition = dateOfInitialActivationsAvailability;
			totalMonthsSinceInitialActivations = validationHelper.getMonthsBetweenDates(activationDate, lastActivition);
			
			
			//get number of months between each initial activation and activations every cycle
			int monthsBetweenActivations = entitlement.getEntitlementInfo().getActivationCycleMonths();
			int daysBetweenActivations = entitlement.getEntitlementInfo().getActivationCycleDays();
			int activationsEveryCycle = entitlement.getEntitlementInfo().getActivationsEveryCycle();

			while(activationsAvailable > 0 && totalMonthsSinceInitialActivations < totalMonths && activationsEveryCycle > 0) {
	
				//get the date when the next activations will be available
				Date thisActivation = validationHelper.addMonthsToDate(lastActivition, monthsBetweenActivations);
				thisActivation = validationHelper.addDaysToDate(thisActivation, daysBetweenActivations);
				//change constraint values.
				
					
				lastActivition = thisActivation;
				
				//add thisActivation to the schedule
				ActivationAvailability activationAvailability = new ActivationAvailability();
	
				activationAvailability.setAvailablilityDate(thisActivation);
				
				if(activationsAvailable < activationsEveryCycle ) {
					activationAvailability.setAdditionalAvailabile(activationsAvailable);
				}else {
					activationAvailability.setAdditionalAvailabile(activationsEveryCycle);
				}

				//if all activations are available at this point then set activations available to 0
				if(activationsAvailable < activationsEveryCycle ) {
					activationsAvailable = 0;
				}else {
					activationsAvailable = activationsAvailable - activationsEveryCycle;
				}
				
				activationSchedule.add(activationAvailability);

				totalMonthsSinceInitialActivations = validationHelper.getMonthsBetweenDates(activationDate, lastActivition);
				
			}
			return activationSchedule;
		}

		
}
