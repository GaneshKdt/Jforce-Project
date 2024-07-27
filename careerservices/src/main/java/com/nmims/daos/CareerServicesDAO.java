package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.PackageBean;



@Component
public class CareerServicesDAO{

	private Gson gson = new Gson();
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(CareerServicesDAO.class);
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
/*
 * --------------------- Backend Checks ---------------------
 */
	
	//This sets all the validations and rights to the user. Nothing is set to UserAuthorization roles.
	public void performCSAffiliateUserChecks(HttpServletRequest request, String userId) {

		//Check for CS Affiliated Faculty.
		boolean isExternallyAffiliatedForProducts = false;
		boolean isCSSpeaker = false;
		boolean isCSAdmin = false;
		boolean isCSSessionsAdmin = false;
		boolean isCSProductsAdmin = false;
		isCSSpeaker = checkIfCSSpeaker(userId);
		try {

			if(request.getSession().getAttribute("userAuthorization") != null) {
				UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
				if(userAuthorization.getRoles() != null) {
					String userAuthorizationRoles = userAuthorization.getRoles();
					userAuthorizationRoles = userAuthorizationRoles.replaceAll(", ", ",");
					userAuthorizationRoles = userAuthorizationRoles.replaceAll(" ,", ",");
					List<String> rolesList = Arrays.asList(userAuthorizationRoles.split(","));

					if(rolesList.contains("Externally Affiliated")) {
						isExternallyAffiliatedForProducts = true;
					}
					if(rolesList.contains("Career Services Admin")) {
						isCSAdmin = true;
					}
					if(rolesList.contains("Career Services Products Admin")) {
						isCSProductsAdmin = true;
					}
					if(rolesList.contains("Career Services Sessions Admin")) {
						isCSSessionsAdmin = true;
					}
				}
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		request.getSession().setAttribute("isCSSpeaker", isCSSpeaker);
		request.getSession().setAttribute("isCSAdmin", isCSAdmin);
		request.getSession().setAttribute("isCSProductsAdmin", isCSProductsAdmin);
		request.getSession().setAttribute("isCSSessionsAdmin", isCSSessionsAdmin);
		request.getSession().setAttribute("isExternallyAffiliatedForProducts", isExternallyAffiliatedForProducts);
	}
		
		private boolean checkIfCSSpeaker(String userId) {
			String sql = "SELECT "
					+ "count(*) "
					+ "FROM "
					+ "`products`.`speakers` `s` "
					+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
					+ "ON "
					+ "`s`.`facultyTableId` = `f`.`facultyId` "
					+ "WHERE "
					+ "`f`.`facultyId` = ?";
			try {
				int count = jdbcTemplate.queryForObject(sql, new Object[] { userId }, Integer.class);
				
				if( count > 0 ) {
					return true;
				}
			}catch (Exception e) {
				//Nothing because query failed.
			}
			
			return false;
		}
	
	//For studentportal home page.
	public int getNotAnsCSQueryCount(String facultyId) {
		try {
			String SQL = ""
					+ "SELECT "
						+ "COUNT(id) "
					+ "FROM "
						+ "`products`.`session_query_answer` "
					+ "WHERE "
						+ "`assignedToFacultyId` = ? "
						+ "AND "
						+ "`queryType` <> 'Technical' "
						+ "AND "
						+ "`isAnswered` = 'N'";
			return (int) jdbcTemplate.queryForObject(SQL,new Object[] {facultyId},Integer.class);
		}
		catch(Exception e) {
			logger.info("exception : "+e.getMessage());
			return 0;
		}
		
	}

/*
 * --------------------- Backend Checks END ------------------
 */
	

/*
 * --------------------- Student Checks Start ----------------
 */

	boolean careerForumAccess = false;
	boolean learningPortalAccess = false;
	boolean careerCounsellingAccess = false;
	boolean practiceInterviewAccess = false;
	boolean jobSearchAccess = false;
	
	private void setCSAccessForMasterKeyInSession(HttpServletRequest request, StudentCareerservicesBean student) {
		if(checkIfCSAccessAvailableForMasterKey(student.getConsumerProgramStructureId())) {
			request.getSession().setAttribute("consumerProgramStructureHasCSAccess", true);
		}else {
			request.getSession().setAttribute("consumerProgramStructureHasCSAccess", false);
		}
	}
	
	private void setFeatureAccessInSession(HttpServletRequest request, StudentCareerservicesBean student){
		
		String sapid = student.getSapid();
		
		if(student.isPurchasedOtherPackages()) {
			checkFeatureAccessForCS(sapid);
		}
		
		Map<String, Boolean> featureViseAccess = getFeatureViseAccess(sapid);
		
		request.getSession().setAttribute("CSFeatureAccess", featureViseAccess);
		
	}
	
	private void checkFeatureAccessForCS(String sapid) {
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
						+ "`sp`.`sapid`= ? "
					+ "ORDER BY "
						+ "`dateAdded` DESC";
		
		try {
			List<Map<String, Integer>> results = jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid
				},
				new RowMapper<Map<String, Integer>>() {
					@Override
					public Map<String, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
						try {
							return checkEntitlementValidity(rs, sapid);
						} catch (Exception e) {
							logger.info("exception : "+e.getMessage());
						}
						return null;
					}
				});
			
			//loop through all results and if any are true, return true.
			for(Map<String, Integer> result: results) {

				if(result.get("status") == 1) {
					switch(result.get("featureId")) {
						case 1:
							careerForumAccess = true;
							break;
						case 2:
							learningPortalAccess = true;
							break;
						case 3:
							careerCounsellingAccess = true;
							break;
						case 4:
							practiceInterviewAccess = true;
							break;
						case 5:
							jobSearchAccess = true;
							break;
					}
				}
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
	}

	private Map<String, Boolean> getFeatureViseAccess(String sapid){

		Map<String, Boolean> featureViseAccess = new HashMap<String, Boolean>();
		featureViseAccess.put("Career_Forum", careerForumAccess);
		featureViseAccess.put("Learning_Portal", learningPortalAccess);
		featureViseAccess.put("Career_Counselling", careerCounsellingAccess);
		featureViseAccess.put("Practice_Interviews", practiceInterviewAccess);
		featureViseAccess.put("Job_Search", jobSearchAccess);

		return featureViseAccess;
	}
	
	private Map<String, Integer> checkEntitlementValidity(ResultSet rs, String sapid) throws Exception{
		//loop through all entitlements this student has and return result
		Map<String, Integer> toReturn = new HashMap<String, Integer>();
		toReturn.put("featureId", rs.getInt("featureId"));
		boolean status = true;
		
		Date dateAdded = rs.getDate("dateAdded");
		
		//if giveAccessAfterExpiry is true and an entitlement was found, it will always result in a true output
		boolean giveAccessAfterExpiry = rs.getBoolean("giveAccessAfterExpiry");

		if(giveAccessAfterExpiry) {
			toReturn.put("status", 1);
			return toReturn;
		}
			
		boolean giveAccessAfterActivationsConsumed = rs.getBoolean("giveAccessAfterActivationsConsumed");

		boolean extendIfActivationsLeft = rs.getBoolean("extendIfActivationsLeft");
		int activationsLeft = rs.getInt("activationsLeft");
		int totalActivations = rs.getInt("totalActivations");
		
		int duration = rs.getInt("duration");
		Date endDate = addMonthsToDate(dateAdded, duration);
		boolean entitlementInitialValidityPeriodEnded = checkIfDateBeforeCurrent(endDate);

		if( "3".equals( Integer.toString( rs.getInt("featureId") ) ) && checkIfCareerCounsellingActive( sapid ) ) {

			toReturn.put("status", 1);
			return toReturn;
			
		}else if ( "3".equals( Integer.toString( rs.getInt("featureId") ) ) ) {

			careerCounsellingAccess = false;
			toReturn.put("status", 0);
			return toReturn;
			
		}
			
		if( "4".equals( Integer.toString( rs.getInt("featureId") ) ) && checkIfPracticeInterviewActive( sapid ) ) {

			toReturn.put("status", 1);
			return toReturn;
			
		}else if ( "4".equals( Integer.toString( rs.getInt("featureId") ) ) ) {

			practiceInterviewAccess = false;
			toReturn.put("status", 0);
			return toReturn;
			
		}
		
		if( totalActivations > 0 ) {

		//if more than one activation is available in total.
			if(activationsLeft > 0) {

				//check if the validity period has ended, check for extension
				if(entitlementInitialValidityPeriodEnded) {

					if(extendIfActivationsLeft) {

						int extendByMaxMonths = rs.getInt("extendByMaxMonths");
						int extendByMaxDays = rs.getInt("extendByMaxDays");
						endDate = addMonthsToDate(endDate, extendByMaxMonths);
						endDate = addDaysToDate(endDate, extendByMaxDays);
						
						if(checkIfDateBeforeCurrent(endDate)){
							status = false;
						}
						
					}else {
						//if no extension, return false
						status = false;
					}
				}
			//if no activations are left and no access if given after activations are consumed.
			}else if(!giveAccessAfterActivationsConsumed) {
				status = false;
			}
		//if entitlement validity period has ended and theres no other extension conditions left
		}else if(entitlementInitialValidityPeriodEnded){
			status = false;
		}

		if(status) {
			toReturn.put("status", 1);
		}else {
			toReturn.put("status", 0);
		}
		return toReturn;
	}
	
	public Boolean checkIfCareerCounsellingActive( String sapid ) {
	
		Boolean status = Boolean.FALSE;
		
		String packageId = getPackageId(sapid);
		String featureId = getFeatureId("Career Counselling");
		String entitlementId = getEntitlementId(packageId, featureId);
		
		String query = "SELECT "
				+ "     activated AS isActive "
				+ "FROM "
				+ "    products.entitlements_student_data "
				+ "WHERE "
				+ "    sapid = ? "
				+ "        AND entitlementId = ? "
				+ "        AND sysdate() >= activationDate";

		try {
			status = jdbcTemplate.queryForObject( query, 
					new Object[] { sapid, entitlementId }, Boolean.class);
		}catch (Exception e) {
			// TODO: handle exception
		}

		return status;
		
	}

	public Boolean checkIfPracticeInterviewActive( String sapid ) {

		Boolean status = Boolean.FALSE;
		
		String packageId = getPackageId(sapid);
		String featureId = getFeatureId("Practice Interviews");
		String entitlementId = getEntitlementId(packageId, featureId);

		String query = "SELECT "
				+ "     activated AS isActive "
				+ "FROM "
				+ "    products.entitlements_student_data "
				+ "WHERE "
				+ "    sapid = ? "
				+ "        AND entitlementId = ? "
				+ "        AND sysdate() >= activationDate";

		try {
			status = jdbcTemplate.queryForObject( query, 
					new Object[] { sapid, entitlementId }, Boolean.class);
		}catch (Exception e) {
			// TODO: handle exception
		}

		return status;
		
	}
	
	public String getPackageId(String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    packageId " + 
				"FROM " + 
				"    products.packages " + 
				"WHERE " + 
				"    salesForceUID = (SELECT  " + 
				"            salesForceUID " + 
				"        FROM " + 
				"            products.student_packages " + 
				"        WHERE " + 
				"            sapid = ?)";
		
		String packageId = (String)jdbcTemplate.queryForObject(sql, new Object[] {sapid}, String.class);
		
		return packageId;
	}

	public String getFeatureId(String featureName) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    featureId " + 
				"FROM " + 
				"    products.features " + 
				"WHERE " + 
				"    featureName = ?";
		
		String featureId = (String)jdbcTemplate.queryForObject(sql, new Object[] {featureName},String.class);
		
		return featureId;
	}

	public String getEntitlementId(String packageId, String featureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    entitlementId " + 
				"FROM " + 
				"    products.entitlements_info " + 
				"WHERE " + 
				"    packageFeaturesId = (SELECT " + 
				"            uid " + 
				"        FROM " + 
				"            products.package_features " + 
				"        WHERE " + 
				"            packageId = ? AND featureId = ?)";
		
		String entitlementId = (String)jdbcTemplate.queryForObject(sql, new Object[] {packageId, featureId},String.class);
		
		return entitlementId;
		
	}
	
	public void performCSStudentChecks(HttpServletRequest request, StudentCareerservicesBean student) {
		setCSAccessForMasterKeyInSession(request, student);
		setFeatureAccessInSession(request, student);
	}

	public boolean checkIfCSAccessAvailableForMasterKey(String key) {
		
		String sql = ""
					+ "SELECT "
					+ "COUNT(*) "
					+ "FROM "
					+ "`products`.`packages` `p`"
					+ "INNER JOIN "
					+ "`products`.`package_requirements` `pr` "
					+ "ON "
					+ "`p`.`packageId` = `pr`.`packageId`"
					+ "INNER JOIN "
					+ "`products`.`package_requirements_master_mapping` `prmm` "
					+ "ON "
					+ "`pr`.`requirementsId` = `prmm`.`requirementsId`"
					+ "WHERE "
					+ "`prmm`.`consumerProgramStructureId` = ?";
		
		try {
			int count = jdbcTemplate.queryForObject(sql, new Object[] {key}, Integer.class);
			if(count > 0) {
				return true;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

//--------------------- Student Checks END -------------------
	
//--------------------- Helper Fucntions ---------------------
	
	private boolean checkIfDateBeforeCurrent(Date date) {
		Calendar dateToTest = Calendar.getInstance();
		dateToTest.setTime(date);
		Calendar currentDate = Calendar.getInstance();
		
		if(dateToTest.before(currentDate)) {
			return true;
		}
		return false;
	}

	private Date addMonthsToDate(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	private Date addDaysToDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
	
	public PackageBean getTermsAndConditionForPackage(PackageBean bean) {
		
		String query = "SELECT * FROM products.packages WHERE packageId = ?";
		
		bean = jdbcTemplate.queryForObject(query, new Object[] { bean.getPackageId() }, 
				new BeanPropertyRowMapper<>(PackageBean.class));

		return bean;
	}

}
