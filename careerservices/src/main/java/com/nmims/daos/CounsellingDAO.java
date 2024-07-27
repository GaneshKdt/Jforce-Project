package com.nmims.daos;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.CounsellingBean;
import com.nmims.beans.CounsellingFeedbackBean;

public class CounsellingDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(CounsellingDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public String getPackageId(String sapid) throws Exception{
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
	
	public String getFeatureId(String featureName) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    featureId " + 
				"FROM " + 
				"    products.features " + 
				"WHERE " + 
				"    featureName = ?";
		
		String featureId = (String)jdbcTemplate.queryForObject(sql, new Object[] {featureName}, String.class);
		return featureId;
	}
	
	public String getEntitlementId(String packageId, String featureId) throws Exception{
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
		
		String entitlementId = (String)jdbcTemplate.queryForObject(sql, new Object[] {packageId, featureId},  String.class);
		return entitlementId;
	}
	
	public String getFacultyId(String counsellingId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    facultyId " + 
				"FROM " + 
				"    products.counselling " + 
				"WHERE " + 
				"    counsellingId = ? ";
		
		String facultyId = (String)jdbcTemplate.queryForObject(sql, new Object[] {counsellingId}, String.class);
		
		return facultyId;
		
	}
	
	public CounsellingBean getStudentDetails(CounsellingBean bean) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    esd.uid, " + 
				"    esd.sapid, " + 
				"    esd.entitlementId, " + 
				"    sp.startDate, " + 
				"    sp.endDate, " + 
				"    ei.totalActivations, " + 
				"    esd.activated, " + 
				"    esd.activationsLeft, " + 
				"    esd.activationDate, " + 
				"    pf.packageId, " + 
				"    pf.featureId, " + 
				"    p.packageName, " + 
				"    f.featureName " + 
				"FROM " + 
				"    products.entitlements_student_data AS esd " + 
				"        LEFT JOIN " + 
				"    products.entitlements_info AS ei ON esd.entitlementId = ei.entitlementId " + 
				"        LEFT JOIN " + 
				"    products.package_features AS pf ON esd.entitlementId = pf.uid " + 
				"        LEFT JOIN " + 
				"    products.features AS f ON pf.featureId = f.featureId " + 
				"        LEFT JOIN " + 
				"    products.packages AS p ON pf.packageId = p.packageId " + 
				"        LEFT JOIN " + 
				"    products.student_packages AS sp ON esd.sapid = sp.sapid " + 
				"WHERE " + 
				"    esd.sapid = ? "+ 
				"        AND  esd.entitlementId IN (SELECT " + 
				"            uid " + 
				"        FROM " + 
				"            products.package_features " + 
				"        WHERE " + 
				"            featureId = (SELECT  " + 
				"                    featureId " + 
				"                FROM " + 
				"                    products.features " + 
				"                WHERE " + 
				"                    featureName = ?))";
		
		CounsellingBean counsellingDetails = jdbcTemplate.queryForObject(sql, new Object[] { bean.getSapid(), bean.getFeatureName() },
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		return counsellingDetails;
	}
	
	public ArrayList<CounsellingBean> getAllCounselling() throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.counselling where ended <> 'Y'";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<CounsellingBean> counsellingDetailsList = (ArrayList<CounsellingBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(CounsellingBean.class));
		return counsellingDetailsList;
	}
	
	public ArrayList<CounsellingBean> getScheduledCounsellingForStudent(String sapid) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    *, "
				+ "    DATE_SUB(STR_TO_DATE(CONCAT(date, ' ', startTime), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 HOUR) AS joinTime, "
				+ "    CONCAT(date, ' ', startTime) AS joinWindowEndTime, "
				+ "    CONCAT(date, ' ', endTime) AS endTime "
				+ "FROM "
				+ "    products.counselling_scheduled "
				+ "WHERE "
				+ "    sapid = ? "
				+ "ORDER BY date";
		
		ArrayList<CounsellingBean> scheduledCounsellingForStudent = (ArrayList<CounsellingBean>) jdbcTemplate.query(sql, new Object[] {sapid},
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return scheduledCounsellingForStudent;
		
	}
	
	public void setTerminated()  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE products.counselling " + 
				"SET " + 
				"	ended = 'Y' " + 
				"WHERE " + 
				"    SYSDATE() > date";
		
		jdbcTemplate.update(sql);
	}
	
	public ArrayList<String> getAllFaculties() throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT f.facultyid FROM products.speakers AS ps LEFT JOIN acads.faculty  AS f ON ps.facultyTableId = f.id";

		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<>(String.class));
		return facultyList;
	}
	
	public boolean checkFacultyFree(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    count(*) = 0 " + 
				"FROM " + 
				"    products.counselling " + 
				"WHERE " + 
				"    date = ? " + 
				"        AND ( (? BETWEEN startTime AND endTime) " + 
				"        OR ( ? BETWEEN startTime AND endTime)) "
				+ " AND facultyId = ?";
		
		boolean facultyFree = jdbcTemplate.queryForObject(
			sql,
			new Object[] {
				bean.getDate(), bean.getStartTime(), bean.getEndTime(),
				bean.getFacultyId()
			}, 
			boolean.class
		);

		return facultyFree;
	}
	
	public boolean checkFreeSlots(CounsellingBean bean) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    count(*) < 2 " + 
				"FROM " + 
				"    products.counselling " + 
				"WHERE " + 
				"    date = ? " + 
				"        AND ( (? BETWEEN startTime AND endTime) " + 
				"        OR ( ? BETWEEN startTime AND endTime))";

		boolean clashing = jdbcTemplate.queryForObject(
			sql,
			new Object[] {
				bean.getDate(), bean.getStartTime(), bean.getEndTime()
			}, 
			boolean.class
		);

		return clashing;
	}
	
	public void updateCounsellingDates(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		insertCounsellingDates(bean, jdbcTemplate);
		return;
	}
	
	private void insertCounsellingDates(CounsellingBean bean, JdbcTemplate jdbcTemplate)  throws Exception{

		String sql = "INSERT INTO products.counselling(date, startTime, endTime, facultyId, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate) VALUES "
				+ "(?,?,?,?,?,sysdate(),?,sysdate())";

		String dates =bean.getDate();
		String startTime = bean.getStartTime();
		String endTime = bean.getEndTime();
		String facultyId = bean.getFacultyId();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		jdbcTemplate.update(sql, new Object[] { 
				dates,
				startTime,
				endTime,
				facultyId,
				createdBy,
				lastModifiedBy
		});
		return;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void setCounselling(CounsellingBean bean)  throws Exception{
		updateStudentData(bean);
		insertStudentInterview(bean);
		setInterviewUsed(bean);
	}
	
	private void updateStudentData(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.entitlements_student_data " + 
					"SET " + 
					"    activated = true," + 
					"    activationsLeft = ?"+
					" WHERE " + 
					"    sapid = ? AND entitlementId = ?";
		
		jdbcTemplate.update(sql, new Object[] {(bean.getActivationsLeft() - 1) , bean.getSapid(), bean.getEntitlementId()});
	}
	
	private void setInterviewUsed(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE products.counselling " + 
				"SET " + 
				"	ended = 'Y' , scheduled = 'Y' " + 
				"WHERE " + 
				"    counsellingId = ?";
		
		jdbcTemplate.update(sql, new Object[] {bean.getCounsellingId()});
	}
	
	private void insertStudentInterview(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO `products`.`counselling_scheduled` " + 
				"(`counsellingId`, `sapid`, `date`, `startTime`, `endTime`, `facultyId`, `bookingStatus`, " + 
				"`meetingKey`, `joinUrl`, `hostUrl`, `hostKey`, `room`, `hostId`, `hostPassword`, " + 
				"`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";
		
		String counsellingId = bean.getCounsellingId();
		String sapid = bean.getSapid();
		String date =bean.getDate();
		String startTime =bean.getStartTime();
		String endTime = bean.getEndTime();
		String facultyId = bean.getFacultyId();
		String bookingStatus = bean.getBookingStatus();
		String meetingKey = bean.getMeetingKey();
		String joinUrl = bean.getJoinUrl();
		String hostUrl = bean.getHostUrl();
		String hostKey = bean.getHostKey();
		String room = bean.getRoom();
		String hostId = bean.getHostId();
		String hostPassword = bean.getHostPassword();
		String createBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		
		jdbcTemplate.update(sql, new Object[] { 
				counsellingId, sapid, date,startTime, endTime, facultyId, bookingStatus, meetingKey, joinUrl, 
				hostUrl, hostKey, room, hostId, hostPassword, createBy,  lastModifiedBy
		});
	}
	
	public void updateCounsellingAttendance(CounsellingBean bean)  throws Exception{
		
		updateAttendance( bean.getSapid(), bean.getCounsellingId());
		updatePracticeInterviewActivation(bean);
		
	}
	
	private void updateAttendance( String sapid, String counsellingId)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE `products`.`counselling_scheduled` " + 
				"SET " + 
				"	`attended` = 'Y', " + 
				"	`lastModifiedBy` = ?, " + 
				"	`lastModifiedDate` = sysdate() " + 
				"WHERE `counsellingId` = ? AND isCancelled = 'N'";
		
		jdbcTemplate.update(sql, new Object[] { sapid, counsellingId});
	}
	
	private void updatePracticeInterviewActivation(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE products.entitlements_student_data  "
				+ "SET  "
				+ "    activationDate = ?, "
				+ "    dateEnded = ?, "
				+ "    activated = 1 "
				+ "WHERE "
				+ "    entitlementId = (SELECT  "
				+ "            uid "
				+ "        FROM "
				+ "            products.package_features "
				+ "        WHERE "
				+ "            packageId = ? "
				+ "                AND featureId = (SELECT  "
				+ "                    featureId "
				+ "                FROM "
				+ "                    products.features "
				+ "                WHERE "
				+ "                    featureName = 'Practice Interviews')) "
				+ "        AND sapid = ?";
		
		jdbcTemplate.update(sql, new Object[] {bean.getActivationDate(), bean.getEndDate(), bean.getPackageId(), bean.getSapid()});
		
	}
	
	public void updateCounsellingCancellation(CounsellingBean bean)  throws Exception{
		updateCounsellingStatus(bean);
		updateEntitlementData(bean);
		updateCounsellingAsActive(bean);
	}
	
	private void updateCounsellingStatus(CounsellingBean bean)  throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql="UPDATE `products`.`counselling_scheduled` " + 
				"SET " + 
				"	`isCancelled` = 'Y', "+
				"	`reasonForCancellation` = ?, " + 
				"	`lastModifiedBy` = ?, " + 
				"	`lastModifiedDate` = sysdate() " + 
				"WHERE `counsellingId` = ?";

		jdbcTemplate.update(sql, new Object[] {bean.getReasonForCancellation(), bean.getSapid(), bean.getCounsellingId()});
	}
	
	private void updateEntitlementData(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.entitlements_student_data " + 
				"SET " + 
				"    activationsLeft = activationsLeft + 1 " + 
				"WHERE " + 
				"    sapid = ? AND entitlementId = ?";

		jdbcTemplate.update(sql, new Object[] {bean.getSapid(), bean.getEntitlementId()});
	}
	
	private void updateCounsellingAsActive(CounsellingBean bean)  throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.counselling " + 
				"SET " + 
				"    ended = 'N', " + 
				"    scheduled = 'N' " + 
				"WHERE " + 
				"    counsellingId = ?";

		jdbcTemplate.update(sql, new Object[] {bean.getCounsellingId()});
	}
	
	public int getActivationsLeft(String sapid, String entitlementId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    activationsLeft " + 
				"FROM " + 
				"    products.entitlements_student_data " + 
				"WHERE " + 
				"    sapid = ? " +
				"        AND entitlementId = ?";
		
		String activationsLeft = (String)jdbcTemplate.queryForObject(sql, new Object[] { sapid, entitlementId },
				String.class);
		
		return Integer.parseInt(activationsLeft);
		
	}
	
	public boolean checkForClashingCounselling(CounsellingBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(*) > 0 " + 
				"FROM " + 
				"    products.counselling_scheduled " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND `date` = ? " + 
				"        AND startTime = ? " + 
				"        AND isCancelled = 'N'";

		boolean isClashing = (boolean)jdbcTemplate.queryForObject(sql, new Object[] {
				bean.getSapid(), 
				bean.getDate(),
				bean.getStartTime()}, Boolean.class);
		
		return isClashing;
	}

	public ArrayList<CounsellingBean> getStudentCounselling(String sapid){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    *, "
				+ "    CONCAT(date, ' ', startTime) AS joinTime, "
				+ "    CONCAT(date, ' ', endTime) AS endTime "
				+ "FROM "
				+ "    products.counselling_scheduled "
				+ "WHERE "
				+ "    sapid = ? "
				+ "ORDER BY date;";

		ArrayList<CounsellingBean> studentInterview = (ArrayList<CounsellingBean>) jdbcTemplate.query(sql, new Object[] { sapid }, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return studentInterview;
	}
	
	public Boolean checkIfCareerCounsellingActive( String sapid ) throws Exception {
	
		Boolean status = Boolean.TRUE;
		
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
			status = Boolean.FALSE;
		}
		
		return status;
		
	}

	public ArrayList<CounsellingBean> getPendingFeedback( String facultyId ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    * "
				+ "FROM "
				+ "    products.counselling_scheduled "
				+ "WHERE "
				+ "    feedback = 'N' AND attended = 'Y' "
				+ "        AND facultyId = ? "
				+ "ORDER BY `date`";

		ArrayList<CounsellingBean> pendingFeedback = (ArrayList<CounsellingBean>) jdbcTemplate.query(sql, new Object[] { facultyId }, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return pendingFeedback;
		
	}
	
	public ArrayList<CounsellingBean> getFeedback( String facultyId ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.counselling_scheduled WHERE feedback = 'Y' AND facultyId = ? ORDER BY date";

		ArrayList<CounsellingBean> feedback = (ArrayList<CounsellingBean>) jdbcTemplate.query(sql, new Object[] { facultyId }, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return feedback;
		
	}

	public void insertFeedback(CounsellingFeedbackBean bean, String counsellingId ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		updateCounsellingDetails(counsellingId, bean.getUserId());
		
		String sql = "INSERT INTO products.counselling_feedback(counsellingId, preparedness, communication, listeningSkills, "
				+ "bodyLanguage, clarityOfThought, connect, examples, strength, improvements, cvtweaking, careerchoice, "
				+ "createdBy, createdDate, lastModifiedBy, lastModifiedDate ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
				+ "ON DUPLICATE KEY UPDATE "
				+ "preparedness = ?, "
				+ "communication = ?, "
				+ "listeningSkills = ?, "
				+ "bodyLanguage = ?, "
				+ "clarityOfThought = ?, "
				+ "connect = ?, "
				+ "examples = ?, "
				+ "strength = ?, "
				+ "improvements = ?, "
				+ "cvtweaking = ?, "
				+ "careerchoice = ?, "
				+ "lastModifiedBy = ?, "
				+ "lastModifiedDate = sysdate() ";

		String preparedness =bean.getPreparedness();
		String communication = bean.getCommunication();
		String listeningSkills = bean.getListeningSkills();
		String bodyLanguage = bean.getBodyLanguage();
		String clarityOfThought = bean.getClarityOfThought();
		String connect = bean.getConnect();
		String examples = bean.getExamples();
		String strength = bean.getStrength();
		String improvements = bean.getImprovements();
		String cvtweaking = bean.getCvtweaking();
		String careerchoice = bean.getCareerchoice();

		jdbcTemplate.update(sql, new Object[] { 
				counsellingId, preparedness, communication, listeningSkills, bodyLanguage, clarityOfThought,
			connect, examples, strength, improvements, cvtweaking, careerchoice, bean.getUserId(), bean.getUserId(),
			preparedness, communication, listeningSkills, bodyLanguage, clarityOfThought, connect, examples, strength, 
			improvements, cvtweaking, careerchoice, bean.getUserId()
		});

	}

	private void updateCounsellingDetails(String counsellingId, String userId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE `products`.`counselling_scheduled` " + 
				"SET " + 
				"`feedback` = 'Y', lastModifiedBy = ?, lastModifiedDate = sysdate()" + 
				"WHERE `counsellingId` = ? AND isCancelled <> 'Y' ";
		try{
			jdbcTemplate.update(sql, new Object[] { userId, counsellingId });
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
		
	}

	public CounsellingFeedbackBean getFeedbackForCounselling(String counsellingId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    products.counselling_feedback " + 
				"WHERE " + 
				"    counsellingId = ?";

		CounsellingFeedbackBean feedback = (CounsellingFeedbackBean)jdbcTemplate.queryForObject(sql, new Object[] {counsellingId}, 
				new BeanPropertyRowMapper<>(CounsellingFeedbackBean.class));
		
		return feedback;
	}
	
	public boolean checkIfCounsellingFeedbackProvided( CounsellingBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(*) > 0 " + 
				"FROM " + 
				"    products.counselling_scheduled " + 
				"WHERE " + 
				"    counsellingId = ? AND feedback = 'Y'";

		boolean feedbackProvided = (boolean)jdbcTemplate.queryForObject(sql, new Object[] { bean.getCounsellingId() }, Boolean.class);
		
		return feedbackProvided;
	}

	public CounsellingBean getCounselling( String sapid, String counsellingId ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    products.counselling_scheduled "
				+ "WHERE "
				+ "    sapid = ? "
				+ "        AND counsellingId = ? "
				+ "        AND isCancelled <> 'Y' ";
		
		CounsellingBean studentCounselling = jdbcTemplate.queryForObject(sql, 
				new Object[] {sapid, counsellingId}, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return studentCounselling;
	}

	public boolean checkIfCounsellingSchedulePossible( CounsellingBean bean ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    COUNT(*) < 1 "
				+ "FROM "
				+ "    products.counselling_scheduled "
				+ "WHERE "
				+ "    sapid = ? AND isCancelled <> 'Y'";

		boolean feedbackProvided = (boolean)jdbcTemplate.queryForObject(sql, new Object[] { bean.getSapid() }, Boolean.class);
		
		return feedbackProvided;
	}

	public CounsellingBean getCounsellingDetails( String counsellingId ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.counselling where counsellingId= ?";
		
		CounsellingBean counsellingDetails = jdbcTemplate.queryForObject(sql, new Object[] { counsellingId }, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return counsellingDetails;
		
	}

	public CounsellingBean getScheduledCounsellingDetails(String counsellingId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.counselling_scheduled where counsellingId = ?";
		
		CounsellingBean counsellingDetails = jdbcTemplate.queryForObject(sql, new Object[] {counsellingId}, 
				new BeanPropertyRowMapper<>(CounsellingBean.class));
		
		return counsellingDetails;
		
	}
	
}
