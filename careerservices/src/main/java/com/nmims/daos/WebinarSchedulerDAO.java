package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.SessionAttendance;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.UserViewedWebinar;
import com.nmims.helpers.DataValidationHelpers;

public class WebinarSchedulerDAO {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	EntitlementCheckerDAO entitlementCheckerDAO;
	@Autowired
	EntitlementManagementDAO entitlementManagementDAO;
	@Autowired
	EntitlementActivationDAO entitlementActivationDAO;
	
	DataValidationHelpers dataHelper = new DataValidationHelpers();

	private static final Logger logger = LoggerFactory.getLogger(WebinarSchedulerDAO.class);
 
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${SERVER_PATH}")
	private String SERVER_PATH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value( "${MAX_WEBEX_USERS}" )
	private int MAX_WEBEX_USERS2;
	private int MAX_WEBEX_USERS=1000;
	
	
	/*
	@Autowired private StudentDetailsFieldValues studentDetailsFieldValues;
	*/
	private DataSource dataSource;
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<SessionDayTimeBean> getAllScheduledWebinars(String sapid) throws Exception {
		//all webinars. Includes the ones scheduled for the future AND the ones that were from the past.
		List<SessionDayTimeBean> scheduledWebinars = getWebinarList();
		
		return scheduledWebinars;
	}
	
	public List<SessionDayTimeBean> getAllWebinarData(String sapid) {
		//all webinars. Includes the ones scheduled for the future AND the ones that were from the past.
		List<SessionDayTimeBean> scheduledWebinars = getAllWebinarList();
		
		return scheduledWebinars;
	}
	
	public List<SessionDayTimeBean> getUpcomingWebinars(String sapid) {
		//all webinars. Includes the ones scheduled for the future AND the ones that were from the past.
		List<SessionDayTimeBean> scheduledWebinars = getUpcomingWebinarList();
		
		return scheduledWebinars;
	}

	public List<SessionDayTimeBean> getAllWebinars() throws Exception{
		return getWebinarList();
	}
	
	/*
	 * 	Returns a list of ALL webinars
	 */
		private List<SessionDayTimeBean> getWebinarList() throws Exception{
			
			String sql = "SELECT  "
					+ "    `sessions`.*, "
					+ "    `f`.`imgUrl` AS `facultyImageURL`, "
					+ "    `f`.`facultyId` AS `facultyId`, "
					+ "    `f`.`location` AS `facultyLocation`, "
					+ "    'career_forum' AS 'type', "
					+ "    CASE "
					+ "        WHEN `Seats`.`seatsRemaining` IS NULL THEN 1000 "
					+ "        ELSE `Seats`.`seatsRemaining` "
					+ "    END AS `seats`, "
					+ "    CONCAT_WS(' ', `f`.`firstName`, `f`.`lastName`) AS `facultyName` "
					+ "FROM "
					+ "    `products`.`sessions` `sessions` "
					+ "        LEFT JOIN "
					+ "    `acads`.`faculty` `f` ON `sessions`.`facultyId` = `f`.`facultyId` "
					+ "        LEFT JOIN "
					+ "    (SELECT  "
					+ "        sessionId, (1000 - COUNT(*)) AS `seatsRemaining` "
					+ "    FROM "
					+ "        `products`.`session_attendance` "
					+ "    GROUP BY sessionId) `Seats` ON `Seats`.`sessionId` = `sessions`.`id` "
					+ "WHERE "
					+ "    `sessions`.`isCancelled` = 'N' "
					+ "ORDER BY `date`";
			
			//query and map
			List<SessionDayTimeBean> webinars = jdbcTemplate.query(sql, new Object[] {} , 
					new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
			List<SessionDayTimeBean> webinarsToReturn = new ArrayList<SessionDayTimeBean>();
			
			for(SessionDayTimeBean webinar: webinars) {
		    	if(webinar.getFacultyImageURL() != null) {
			    	webinar.setFacultyImageURL(SERVER_PATH + webinar.getFacultyImageURL());
		    	}
				webinarsToReturn.add(addFacultyDetailsToSessionDayTime(webinar));
			}
			return webinarsToReturn;
		}
		
		private List<SessionDayTimeBean> getAllWebinarList(){
			String sql = ""
					+ "SELECT "
						+ "sessions.*,'career_forum' AS 'type' "
					+ "FROM "
						+ "`products`.`sessions` `sessions` "
					+ "ORDER BY "
						+ "`date` ";
			
			//query and map
			List<SessionDayTimeBean> webinars = jdbcTemplate.query(sql, new Object[] {} , new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
			List<SessionDayTimeBean> webinarsToReturn = new ArrayList<SessionDayTimeBean>();
			for(SessionDayTimeBean webinar: webinars) {
				webinarsToReturn.add(addFacultyDetailsToSessionDayTime(webinar));
			}
			return webinarsToReturn;
		}
		
	/*
	 * 	Returns a list of UPCOMING webinars
	 */
		private List<SessionDayTimeBean> getUpcomingWebinarList() {
			String sql = ""
					+ "SELECT "
						+ "sessions.*,'career_forum' AS 'type' "
					+ "FROM "
						+ "`products`.`sessions` `sessions` "
					+ "WHERE "
						+ "`date` >= curdate() "
					+ "ORDER BY "
						+ "`date` "
					+ "";
			
			//query and map
			List<SessionDayTimeBean> webinars = jdbcTemplate.query(sql, new Object[] {} , new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
			List<SessionDayTimeBean> webinarsToReturn = new ArrayList<SessionDayTimeBean>();
			for(SessionDayTimeBean webinar: webinars) {
				webinarsToReturn.add(addFacultyDetailsToSessionDayTime(webinar));
			}
			return webinarsToReturn;
		}
		
		private SessionDayTimeBean addFacultyDetailsToSessionDayTime(SessionDayTimeBean webinar) {
			String sql = ""
					+ "SELECT "
					+ "* "
					+ "FROM "
						+ "`acads`.`faculty` "
					+ "WHERE "
						+ "`facultyId`=?";
			//query and map
			jdbcTemplate.query(
				sql, 
				new Object[] { webinar.getFacultyId() } , 
				new RowMapper<SessionDayTimeBean>() {
				    @Override
				    public SessionDayTimeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				    	if(rs.getString("imgUrl") != null) {
					    	webinar.setFacultyImageURL(SERVER_PATH + rs.getString("imgUrl"));
				    	}
				    	webinar.setFacultyName(rs.getString("firstName")+ " " + rs.getString("lastName"));
				    	return webinar;
				    }
		    });
			return webinar;
		}
		

	/*
	 * 	return list of webinars viewed by student
	 */
		public List<UserViewedWebinar> getStudentViewedWebinarList(String sapid){
			List<UserViewedWebinar> viewedWebinars = getViewedWebinarList(sapid);
			return viewedWebinars;
		}
		
	/*
	 * 	get a list of all scheduledata
	 */
		public List<SessionDayTimeBean> getAllWebinarSchedule() throws Exception{
			List<SessionDayTimeBean> webinarList = getWebinarList();
			return webinarList;
		}
	/*
	 * 	Get viewed webinars list for this user
	 */
		private List<UserViewedWebinar> getViewedWebinarList(String sapid){
			
			String sql = ""
					+ "SELECT "
						+ "`wa`.`uid` AS `activationId`, "
						+ "`wa`.`attendanceFeedbackId` AS `attendanceFeedbackId`, "
						+ "`wa`.`webinarId` AS `webinarId`, "
						+ "`wa`.`consumptionDate` AS `consumptionDate`,"
						+ "`saf`.`reAttendTime` AS `lastViewedOnDate`,"
						+ "`sessions`.`date` AS `date`,"
						+ "`sessions`.`startTime` AS `startTime`,"
						+ "`sessions`.`endTime` AS `endTime`,"
						+ "`faculty`.`facultyId` AS `facultyId`,"
						+ "`p`.`packageName` AS `packageName`,"
						+ "CONCAT_WS(' ', `faculty`.`firstName`, `faculty`.`lastName`) AS `facultyName`,"
						+ "`sessions`.`sessionName` AS `webinarName`"
					+ "FROM "
					+ "`products`.`webinar_activations` `wa`"
						+ "LEFT JOIN "
						+ "`products`.`student_packages` `sp`"
						+ "ON "
						+ "`wa`.`purchaseId` = `sp`.`paymentId` "
						
						+ "LEFT JOIN "
						+ "`products`.`packages` `p` "
						+ "ON "
						+ "`sp`.`salesForceUID` = `p`.`salesForceUID` "
						
						+ "LEFT JOIN "
						+ "`products`.`sessions` `sessions`"
						+ "ON "
						+ "`wa`.`webinarId` = `sessions`.`id`"
					
						+ "LEFT JOIN "
						+ "`acads`.`faculty` `faculty`"
						+ "ON "
						+ "`sessions`.`facultyId` = `faculty`.`facultyId`"
						
						+ "LEFT JOIN "
						+ "`products`.`session_attendance` `saf`"
						+ "ON "
						+ "`saf`.`id` = `wa`.`attendanceFeedbackId`"
					+ "WHERE "
						+ "`sp`.`sapid`=? "
						+ "AND "
						+ "`sessions`.`isCancelled` = 'N'";
			
			//query and map
			List<UserViewedWebinar> viewedWebinars = jdbcTemplate.query(
					sql, 
					new Object[] {sapid} , 
					new BeanPropertyRowMapper<UserViewedWebinar>(UserViewedWebinar.class));
			return viewedWebinars;
		}


	public HashMap<String,Integer> getMapOfFacultyIdAndRemainingSeats(String sessionId,SessionDayTimeBean session) {

		HashMap<String, Integer> sessionAttendanceMap = new HashMap<>();

		//Initialize with all available seats
		sessionAttendanceMap.put(session.getFacultyId(),MAX_WEBEX_USERS);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ "SELECT "
					+ "facultyId,"
					+ "(" + MAX_WEBEX_USERS + " - count(sapId)) as remainingSeats "
				+ "FROM "
				+ "`products`.`session_attendance` `saf`"
					+ "LEFT JOIN "
					+ "`products`.`sessions` `sessions`"
					+ "ON "
					+ "`sessions`.`id` = `saf`.`sessionId`"
				+ "WHERE "
					+ "sessionId = ? "
				+ "GROUP BY "
					+ "facultyId ";

		List<SessionAttendance> attendance = jdbcTemplate.query(
							sql, 
							new Object[]{sessionId},
							new BeanPropertyRowMapper<SessionAttendance>(SessionAttendance.class));

		for (SessionAttendance sessionAttendanceFeedback : attendance) {
			sessionAttendanceMap.put(sessionAttendanceFeedback.getFacultyId(),sessionAttendanceFeedback.getRemainingSeats());
		}

		return sessionAttendanceMap;
	}
	
	private HashMap<String,FacultyCareerservicesBean> mapOfFacultyIdAndFacultyRecord = null;

	public Map<String,FacultyCareerservicesBean> mapOfFacultyIdAndFacultyRecord(){
		List<FacultyCareerservicesBean> listOfAllFaculties = getAllFacultyRecords();
		if(this.mapOfFacultyIdAndFacultyRecord == null){
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String,FacultyCareerservicesBean>();
			for(FacultyCareerservicesBean faculty : listOfAllFaculties){
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(),faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;
	}
	
	public ArrayList<FacultyCareerservicesBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";
		ArrayList<FacultyCareerservicesBean> facultyNameAndIdList = null;
		try{
			facultyNameAndIdList = (ArrayList<FacultyCareerservicesBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
			ArrayList<FacultyCareerservicesBean> facultyNameAndIdListToReturn = new ArrayList<FacultyCareerservicesBean>();
			for (FacultyCareerservicesBean facultyBean : facultyNameAndIdList) {
		    	if(facultyBean.getImgUrl() != null) {
		    		facultyBean.setImgUrl(SERVER_PATH + facultyBean.getImgUrl());
		    	}
		    	facultyNameAndIdListToReturn.add(facultyBean);
			}
			return facultyNameAndIdList;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}

	}

	public SessionDayTimeBean findScheduledSessionById(String id) {
		String sql = ""
				+ "SELECT "
					+ "`s`.*, "
					+ "CONCAT_WS(' ', `f`.`firstName`, `f`.`lastName`) AS `facultyName`, "
					+ "`f`.`facultyId` AS `facultyId` "
				+ "FROM "
				+ "`products`.`sessions` `s` "
					+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
					+ "ON "
					+ "`s`.`facultyId` = `f`.`facultyId` "
				+ "WHERE "
					+ "`s`.`id` = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeBean session = null;
		try {
			session= (SessionDayTimeBean) jdbcTemplate.queryForObject(
					sql, new Object[] { id }, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return session;
	}
}

