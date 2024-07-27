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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;



@Component
public class CareerServicesDAO{

	private Gson gson = new Gson();
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
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
		
		if(request.getSession().getAttribute("userAuthorization") != null) {
			UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean) request.getSession().getAttribute("userAuthorization");
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
		
		request.getSession().setAttribute("isCSSpeaker", isCSSpeaker);
		request.getSession().setAttribute("isCSAdmin", isCSAdmin);
		request.getSession().setAttribute("isCSProductsAdmin", isCSProductsAdmin);
		request.getSession().setAttribute("isCSSessionsAdmin", isCSSessionsAdmin);
		request.getSession().setAttribute("isExternallyAffiliatedForProducts", isExternallyAffiliatedForProducts);
	}
	
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	
	private void setCSAccessForMasterKeyInSession(HttpServletRequest request, StudentExamBean student) {
		if(checkIfCSAccessAvailableForMasterKey(student.getConsumerProgramStructureId())) {
			request.getSession().setAttribute("consumerProgramStructureHasCSAccess", true);
		}else {
			request.getSession().setAttribute("consumerProgramStructureHasCSAccess", false);
		}
	}
	
	private void setFeatureAccessInSession(HttpServletRequest request, StudentExamBean student){
		String sapid = student.getSapid();
		
		checkFeatureAccessForCS(sapid);

		Map<String, Boolean> featureViseAccess = getFeatureViseAccess(sapid);
		
		request.getSession().setAttribute("CSFeatureAccess", featureViseAccess);
		
	}

	@Transactional(readOnly = true)
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
							return checkEntitlementValidity(rs);
						} catch (Exception e) {
							
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
	
	private Map<String, Integer> checkEntitlementValidity(ResultSet rs) throws Exception{
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
	
	public void performCSStudentChecks(HttpServletRequest request, StudentExamBean student) {
		setCSAccessForMasterKeyInSession(request, student);
		if(!student.isPurchasedOtherPackages()) {
			if(checkIfStudentHasPendingProductAdditions(student.getSapid())) {
				student.setPurchasedOtherPackages(true);
				
				String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
				student.setProgramForHeader(programForHeader);
				request.getSession().setAttribute("studentExam", student);
			}
		}
		setFeatureAccessInSession(request, student);
	}

	@Transactional(readOnly = true)
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
			
		}
		return false;
	}

	@Transactional(readOnly = true)
	public boolean checkIfStudentHasPendingProductAdditions(String sapid) {
		String sql = ""
					+ "SELECT "
						+ "count(*) "
					+ "FROM "
						+ "`products`.`purchases_sfdc` "
					+ "WHERE "
						+ "`pending` = ? "
					+ "AND "
						+ "`sapid` = ?";
		try {
			int count = jdbcTemplate.queryForObject(sql, new Object[] { true, sapid }, Integer.class);
			
			if( count > 0 ) {
				return true;
			}
		}catch (Exception e) {
			//Nothing because query failed.
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

	public Map<String, Boolean> getStudentAccessDetailsForCS(String sapid) {
		checkFeatureAccessForCS(sapid);
		Map<String, Boolean> featureViseAccess = getFeatureViseAccess(sapid);
		return featureViseAccess;
	}
}
