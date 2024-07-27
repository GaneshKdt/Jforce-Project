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

import com.nmims.beans.InterviewBean;
import com.nmims.beans.InterviewFeedbackBean;

public class InterviewDAO {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(InterviewDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ArrayList<String> getAllFaculties() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT f.facultyid FROM products.speakers AS ps LEFT JOIN acads.faculty  AS f ON ps.facultyTableId = f.id";

		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<>(String.class));
		
		return facultyList;
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
	
	public void updateInterviewDates(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		insertInterviewDates(bean, jdbcTemplate);
		
		return;
	}
	
	public boolean checkFacultyFree(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    count(*) = 0 " + 
				"FROM " + 
				"    products.interview " + 
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
	
	public boolean checkFreeSlots(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    count(*) < 2 " + 
				"FROM " + 
				"    products.interview " + 
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
	
	private void insertInterviewDates(InterviewBean bean, JdbcTemplate jdbcTemplate) {

		String sql = "INSERT INTO products.interview(date, startTime, endTime, facultyId, createdBy, createdDate, lastModifiedBy,"
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
	
	
	public InterviewBean getStudentDetails(InterviewBean bean) {
		
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
				"        AND esd.entitlementId = (SELECT  " + 
				"            uid " + 
				"        FROM " + 
				"            products.package_features " + 
				"        WHERE " + 
				"            packageId = ? " + 
				"                AND featureId = (SELECT  " + 
				"                    featureId " + 
				"                FROM " + 
				"                    products.features " + 
				"                WHERE " + 
				"                    featureName = 'Practice Interviews'))";

		InterviewBean interviewDetails = (InterviewBean)jdbcTemplate.queryForObject(sql, 
				new Object[] { bean.getSapid(), bean.getPackageId() },
				new BeanPropertyRowMapper<>(InterviewBean.class));
		
		return interviewDetails;
	}
	
	public ArrayList<InterviewBean> getAllInterview() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.interview where ended = 'N'";

		ArrayList<InterviewBean> interviewList = (ArrayList<InterviewBean>) jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(InterviewBean.class));
		
		return interviewList;
		
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
	
	public String getFacultyId(String interviewId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    facultyId " + 
				"FROM " + 
				"    products.interview " + 
				"WHERE " + 
				"    interviewId = ?";
		
		String facultyId = (String)jdbcTemplate.queryForObject(sql,  new Object[] { interviewId }, 
				String.class);
		
		return facultyId;
	}
	
	public void setTerminated() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE products.interview " + 
				"SET " + 
				"	ended = 'Y' " + 
				"WHERE " + 
				"    SYSDATE() > date";
		
		jdbcTemplate.update(sql);
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void scheduleInterview(InterviewBean bean) throws Exception{
		
		updateStudentData(bean);
		insertStudentInterview(bean);
		setInterviewConsumed(bean);
		
	}
	
	private void updateStudentData(InterviewBean bean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.entitlements_student_data " + 
					"SET " + 
					"    activated = true," + 
					"    activationsLeft = ?"+
					" WHERE " + 
					"    sapid = ? AND entitlementId = ?";
		
		jdbcTemplate.update(sql, new Object[] {(bean.getActivationsLeft() - 1) , bean.getSapid(), bean.getEntitlementId()});
		
	}
	
	private void setInterviewConsumed(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE products.interview " + 
				"SET " + 
				"	ended = 'Y' , scheduled='Y', lastModifiedBy = 'System', lastModifiedDate = sysdate()" + 
				"WHERE " + 
				"    interviewId = ?";
		
		jdbcTemplate.update(sql, new Object[] {bean.getInterviewId()});
		
	}
	
	private void insertStudentInterview(InterviewBean bean) {
		
		String sql = "INSERT INTO `products`.`interview_scheduled` " + 
				"(`interviewId`, `sapid`, `date`, `startTime`, `endTime`, `facultyId`, `bookingStatus`, " + 
				"`meetingKey`, `joinUrl`, `hostUrl`, `hostKey`, `room`, `hostId`, `hostPassword`, " + 
				"`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";
		
		String interviewId = bean.getInterviewId();
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
				interviewId, sapid, date,startTime, endTime, facultyId, bookingStatus, meetingKey, joinUrl, 
				hostUrl, hostKey, room, hostId, hostPassword, createBy,  lastModifiedBy
		});
		
	}

	public ArrayList<InterviewBean> getStudentInterview(String sapid){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    *, "
				+ "    DATE_SUB(STR_TO_DATE(CONCAT(date, ' ', startTime), '%Y-%m-%d %H:%i:%s'), INTERVAL 1 HOUR) AS joinTime, "
				+ "    CONCAT(date, ' ', startTime) AS joinWindowEndTime, "
				+ "    CONCAT(date, ' ', endTime) AS endTime "
				+ "FROM "
				+ "    products.interview_scheduled "
				+ "WHERE "
				+ "    sapid = ? "
				+ "ORDER BY date;";

		ArrayList<InterviewBean> studentInterview = (ArrayList<InterviewBean>) jdbcTemplate.query(sql, new Object[] { sapid }, 
				new BeanPropertyRowMapper<>(InterviewBean.class));
		
		return studentInterview;
	}
	
	public ArrayList<InterviewBean> getPendingFeedback( String facultyId ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    * "
				+ "FROM "
				+ "    products.interview_scheduled "
				+ "WHERE "
				+ "    feedback = 'N' AND attended = 'Y' "
				+ "        AND facultyId = ? "
				+ "ORDER BY `date`";

		ArrayList<InterviewBean> pendingFeedback = (ArrayList<InterviewBean>) jdbcTemplate.query(sql, new Object[] { facultyId }, 
				new BeanPropertyRowMapper<>(InterviewBean.class));
		
		return pendingFeedback;
		
	}
	
	public ArrayList<InterviewBean> getFeedback( String facultyId ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.interview_scheduled WHERE feedback = 'Y' AND facultyId = ? ORDER BY date";

		ArrayList<InterviewBean> feedback = (ArrayList<InterviewBean>) jdbcTemplate.query(sql, new Object[] { facultyId }, 
				new BeanPropertyRowMapper<>(InterviewBean.class));
		
		return feedback;
		
	}
	
	public void insertFeedback(InterviewFeedbackBean bean, String interviewId ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		updateInterview(interviewId, bean.getUserId());
		
		String sql = "INSERT INTO products.interview_feedback(interviewId, preparedness, communication, listeningSkills, "
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
			interviewId, preparedness, communication, listeningSkills, bodyLanguage, clarityOfThought,
			connect, examples, strength, improvements, cvtweaking, careerchoice, bean.getUserId(), bean.getUserId(),
			preparedness, communication, listeningSkills, bodyLanguage, clarityOfThought, connect, examples, strength, 
			improvements, cvtweaking, careerchoice, bean.getUserId()
		});

	}
	
	private void updateInterview(String interviewId, String userId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE `products`.`interview_scheduled` " + 
				"SET " + 
				"`feedback` = 'Y', lastModifiedBy = ?, lastModifiedDate = sysdate()" + 
				"WHERE `interviewId` = ? AND isCancelled <> 'Y' ";
		try{
			jdbcTemplate.update(sql, new Object[] { userId, interviewId });
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
		
	}
	
	public InterviewBean getInterview(String sapid, String interviewId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    products.interview_scheduled "
				+ "WHERE "
				+ "    sapid = ? AND interviewId = ? "
				+ "        AND isCancelled <> 'Y'";
		
		InterviewBean studentInterview = (InterviewBean)jdbcTemplate.queryForObject(sql, 
				new Object[] {sapid, interviewId}, 
				new BeanPropertyRowMapper<InterviewBean>(InterviewBean.class));
		
		return studentInterview;
	}
	
	public InterviewBean getScheduledInterviewDetails(String interviewId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.interview_scheduled where interviewId= ?";
		
		InterviewBean studentInterview = (InterviewBean)jdbcTemplate.queryForObject(sql, new Object[] {interviewId}, 
				new BeanPropertyRowMapper<InterviewBean>(InterviewBean.class));
		
		return studentInterview;
		
	}
	
	public String getFacultyName(String facultyId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    CONCAT(CONCAT(firstName, ' '), lastName) " + 
				"FROM " + 
				"    acads.faculty " + 
				"WHERE " + 
				"    facultyId = ?";
		
		String facultyName = (String)jdbcTemplate.queryForObject(sql, new Object[] {facultyId}, String.class);
		
		return facultyName;
	}

	public InterviewBean getStudentDetails( String userId, String packageId ) {
		
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
				"    esd.sapid = ? " + 
				"        AND esd.entitlementId = (SELECT  " + 
				"            uid " + 
				"        FROM " + 
				"            products.package_features " + 
				"        WHERE " + 
				"            packageId = ? " + 
				"                AND featureId = (SELECT  " + 
				"                    featureId " + 
				"                FROM " + 
				"                    products.features " + 
				"                WHERE " + 
				"                    featureName = 'Practice Interviews'))";

		InterviewBean interviewDetails = (InterviewBean)jdbcTemplate.queryForObject(sql, new Object[] { userId, packageId },
				new BeanPropertyRowMapper<>(InterviewBean.class));
		return interviewDetails;
	}
	
	public void updateInterviewAttendance( String userId, String interviewId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE `products`.`interview_scheduled` " + 
				"SET " + 
				"`attended` = 'Y', " + 
				"`lastModifiedBy` = ?, " + 
				"`lastModifiedDate` = sysdate() " + 
				"WHERE `interviewId` = ? AND isCancelled = 'N'";
		
		jdbcTemplate.update(sql, new Object[] { userId, interviewId });
		
	}
	
	public void updateInterviewCancellation(InterviewBean bean) {
		
		updateInterviewStatus(bean);
		updateEntitlementData(bean);
		updateInterviewAsActive(bean);
		
	}
	
	private void updateInterviewStatus(InterviewBean bean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE `products`.`interview_scheduled` " + 
				"SET " + 
				"	`isCancelled` = 'Y', " +
				"	`reasonForCancellation` = ?, " + 
				"	`lastModifiedBy` = ?, " + 
				"	`lastModifiedDate` = sysdate() " + 
				"WHERE " +
				"`interviewId` = ?";
		jdbcTemplate.update(sql, new Object[] {bean.getReasonForCancellation(), bean.getSapid(), bean.getInterviewId()});
	}
	
	private void updateEntitlementData(InterviewBean bean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.entitlements_student_data " + 
				"SET " + 
				"    activationsLeft = activationsLeft + 1 " + 
				"WHERE " + 
				"    sapid = ? AND entitlementId = ?";
		
		jdbcTemplate.update(sql, new Object[] { bean.getSapid(), bean.getEntitlementId() } );
	}
	
	private void updateInterviewAsActive(InterviewBean bean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE products.interview " + 
				"SET " + 
				"    ended = 'N', " + 
				"    scheduled = 'N' " + 
				"WHERE " + 
				"    interviewId = ?";
		
		jdbcTemplate.update(sql, new Object[] { bean.getInterviewId() });
	}
	
	public boolean checkForClashingInterview(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(*) > 0 " + 
				"FROM " + 
				"    products.interview_scheduled " + 
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
	

	public boolean checkIfPracticeInterviewActive(InterviewBean bean) throws Exception{
		
		String packageId = getPackageId(bean.getSapid());
		String featureId = getFeatureId("Practice Interviews");
		String entitlementId = getEntitlementId(packageId, featureId);
		
		String sql = "SELECT " + 
				"    count(*)>0 AS isActive " + 
				"FROM " + 
				"    products.entitlements_student_data " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND entitlementId = ? " + 
				"        AND activated = 1 " + 
				"        AND sysdate() BETWEEN activationDate AND ADDDATE(activationDate, 45) ";

		boolean isActive = (boolean)jdbcTemplate.queryForObject(sql, new Object[] {
				bean.getSapid(), 
				entitlementId }, Boolean.class);
		
		return isActive;
		
	}

	public InterviewFeedbackBean getFeedbackForInterview(String interviewId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    products.interview_feedback " + 
				"WHERE " + 
				"    interviewId = ?";

		InterviewFeedbackBean feedback = (InterviewFeedbackBean)jdbcTemplate.queryForObject(sql, new Object[] {interviewId}, 
				new BeanPropertyRowMapper<>(InterviewFeedbackBean.class));
		
		return feedback;
	}
	
	public boolean checkIfInterviewFeedbackProvided(InterviewBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(*) > 0 " + 
				"FROM " + 
				"    products.interview_scheduled " + 
				"WHERE " + 
				"    interviewId = ? AND feedback = 'Y'";

		boolean feedbackProvided = (boolean)jdbcTemplate.queryForObject(sql, new Object[] {bean.getInterviewId()}, Boolean.class);
		
		return feedbackProvided;
	}

	public boolean checkIfInterviewSchedulePossible(InterviewBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    COUNT(*) < 2 "
				+ "FROM "
				+ "    products.interview_scheduled "
				+ "WHERE "
				+ "    sapid = ? AND isCancelled <> 'Y'";

		boolean feedbackProvided = (boolean)jdbcTemplate.queryForObject(sql, new Object[] { bean.getSapid() }, Boolean.class);
		
		return feedbackProvided;
	}

	public boolean checkIfSecondInterviewApplicable( InterviewBean bean, String interviewScheduleDate ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    DATEDIFF(date, ?) > 15 AS status "
				+ "FROM "
				+ "    products.interview "
				+ "WHERE "
				+ "    interviewId = ?;";

		boolean interviewApplicable = (boolean)jdbcTemplate.queryForObject(sql, 
				new Object[] { interviewScheduleDate, bean.getInterviewId() }, Boolean.class);
		
		return interviewApplicable;
	}
	
	public String getScheduledInterviewDate( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    date "
				+ "FROM "
				+ "    products.interview_scheduled "
				+ "WHERE "
				+ "    sapid = ? AND isCancelled <> 'Y';";

		String interviewScheduleDate = (String)jdbcTemplate.queryForObject(sql, new Object[] { sapid }, String.class);
		
		return interviewScheduleDate;
		
	}
	
	public InterviewBean getInterviewDetails(String interviewId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM products.interview where interviewId= ?";
		
		InterviewBean interviewDetails = (InterviewBean)jdbcTemplate.queryForObject(sql, new Object[] {interviewId}, 
				new BeanPropertyRowMapper<InterviewBean>(InterviewBean.class));
		
		return interviewDetails;
		
	}
	
}
