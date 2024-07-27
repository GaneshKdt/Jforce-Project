package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;

public class AuditTrailsDAO{

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${SERVER}" )
	private String SERVER;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private static final Logger logger = LoggerFactory.getLogger(AuditTrailsDAO.class);
	
	@Transactional(readOnly = true)
	public List<ErrorAnalyticsBean> getErrorAnalyticsBySapidCreatedDate( String testDate ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ErrorAnalyticsBean> eAList=new ArrayList<>();
		
		String sql="SELECT "
				+ "    *, GROUP_CONCAT(stackTrace) AS stackTrace "
				+ "FROM "
				+ "    portal.error_analytics "
				+ "WHERE "
				+ "    createdOn LIKE '"+testDate+"%' "
				+ "GROUP BY sapid ";


		eAList = (List<ErrorAnalyticsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ErrorAnalyticsBean.class));

		return eAList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<AuditTrailExamBean> getTestForIABatchJob() throws Exception{
		
		ArrayList<AuditTrailExamBean> test = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    id AS testId, startDate, duration, testName "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "		proctoringEnabled = 'Y' "
				+ "        AND isLostFocusCheckDone = 'N' "
				+ "        AND TIMESTAMPDIFF( MINUTE, endDate, NOW() ) > 300";

		test = (ArrayList<AuditTrailExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<AuditTrailExamBean>(AuditTrailExamBean.class));

		return test;
	}

	@Transactional(readOnly = true)
	public ArrayList<AuditTrailExamBean> getDateForErrorAnalytics() throws Exception{
		
		ArrayList<AuditTrailExamBean> test = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    DATE_FORMAT(startDate, '%Y-%m-%d') as startDate "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    proctoringEnabled = 'Y' "
				+ "        AND isLostFocusCheckDone = 'N' "
				+ "        AND TIMESTAMPDIFF(MINUTE, endDate, NOW()) > 300 "
				+ "GROUP BY startDate";

		test = (ArrayList<AuditTrailExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<AuditTrailExamBean>(AuditTrailExamBean.class));

		return test;
	}
	
	@Transactional(readOnly = false)
	public void updateLostFocusCheckByTestId( Long testId) throws Exception{
	
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="UPDATE exam.test  "
				+ "SET  "
				+ "    isLostFocusCheckDone = 'Y' "
				+ "WHERE "
				+ "    id = ?";

		jdbcTemplate.update(sql,new Object[] { testId });

	}
	
	@Transactional(readOnly = true)
	public StudentsTestDetailsExamBean getStudentsTestDetailsBySapidAndTestId(String sapid, Long testId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentsTestDetailsExamBean testByStudent= new StudentsTestDetailsExamBean();

		String sql = "SELECT  "
				+ "    std.*,tpd.contactedSupport, GROUP_CONCAT(tpd.reason SEPARATOR ', ') as reason "
				+ "FROM "
				+ "    exam.test_student_testdetails std "
				+ "        LEFT JOIN "
				+ "    exam.test_proctoring_details tpd ON std.testId = tpd.testId "
				+ "        AND std.sapid = tpd.sapid "
				+ "WHERE "
				+ "    std.sapid = ? "
				+ "        AND std.testId = ? "
				+ "GROUP BY std.testId "
				+ "ORDER BY std.id DESC "
				+ "LIMIT 1";
		
		try {
			
			testByStudent = (StudentsTestDetailsExamBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,testId}, 
					new BeanPropertyRowMapper<>(StudentsTestDetailsExamBean.class));
			
		} catch (Exception e) {
	
			logger.info("\n"+SERVER+": "+"IN getStudentsTestDetailsBySapidAndTestId got sapid  "+sapid+" testId: "+testId+" Error: "+e.getMessage());
				
		}
		return testByStudent;
		
	}
	
	@Transactional(readOnly = true)
	public List<ErrorAnalyticsBean> getErrorAnalyticsBySapidCreatedDate(String sapid, String testStartedOnDate) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ErrorAnalyticsBean> eAList=new ArrayList<>();
		String sql=" SELECT  " + 
				"    * " + 
				"FROM " + 
				"    portal.error_analytics " + 
				"WHERE " + 
				"    sapid = ? " + 
				"    AND createdOn BETWEEN DATE_SUB('"+testStartedOnDate+"', INTERVAL 1 HOUR) "+
				"	 AND DATE_ADD('"+testStartedOnDate+"', INTERVAL 1 HOUR) ";
		
		try {
	
			eAList = (List<ErrorAnalyticsBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper<>(ErrorAnalyticsBean.class));
			
		} catch (Exception e) {
			
		}
		return eAList;
	}
	
	@Transactional(readOnly = true)
	private TestExamBean getBatchByBatchId(Integer referenceId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		TestExamBean batch=new TestExamBean();
		String sql="select * from exam.batch where id=?";
		
		try {
			batch = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {referenceId}, 
					new BeanPropertyRowMapper<>(TestExamBean.class));
		} catch (Exception e) {
			
		}
		
		return batch;
	}

	@Transactional(readOnly = true)
	public TestExamBean getModuleByModuleId(Integer referenceId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		TestExamBean module=new TestExamBean();
		String sql="	select " + 
				"		m.topic,b.name,b.id as batchId,ss.facultyId,m.id as id " + 
				"		from " + 
				"		lti.student_subject_config ssc ,  " + 
				"		acads.sessionplanid_timeboundid_mapping stm,   " + 
				"		acads.sessionplan s,   " + 
				"		acads.sessionplan_module m," + 
				"		exam.batch b  " + 
				"		left join acads.sessions ss on ss.moduleid = ? " + 
				"		where m.id=? " + 
				"		and b.id = ssc.batchId  " + 
				"		and ssc.id = stm.timeboundId   " + 
				"		AND s.id = stm.sessionPlanId   " + 
				"			" + 
				"		AND s.id = m.sessionPlanId";
		
		try {
			module = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {referenceId,referenceId}, 
					new BeanPropertyRowMapper<>(TestExamBean.class));
		} catch (Exception e) {
			//
		}
		
		return module;
	}

	public TestExamBean getReference_Batch_Or_Module_Name(TestExamBean bean) {
		
		if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
			
			TestExamBean batch = getBatchByBatchId(bean.getReferenceId());
			bean.setReferenceBatchOrModuleName(batch.getName());
			bean.setBatchId( Integer.parseInt(batch.getId()+""));
			
		}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
			
			TestExamBean module = getModuleByModuleId(bean.getReferenceId());
			bean.setReferenceBatchOrModuleName(module.getTopic());
			bean.setName(module.getName());
			bean.setBatchId(module.getBatchId());
			
		}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
			//Do nothing
		}else {
			return null;
		}
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public TestExamBean getTestById(Long id){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		TestExamBean test=null;
		String sql="select * from exam.test where id=?";
		
		try {
			 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestExamBean.class));
			 test = getReference_Batch_Or_Module_Name(test);
		} catch (Exception e) {
			logger.info("\n"+SERVER+": "+"IN getTestById got id "+id+", Error :"+e.getMessage());
		}
		
		return test;
	}
	
	public void updateDate(TestExamBean bean) throws Exception {
		
		List<String> sapidArray = new ArrayList<>();
		sapidArray.add(bean.getSapid());
		TestExamBean testFromDb = getTestById(bean.getTestId());
		testFromDb.setSapid(bean.getSapid());
		testFromDb.setExtendedStartTime(bean.getTestStartedOn());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date tSODateTime = sdf.parse(bean.getTestStartedOn());
		Date eETDateTime = addMinutesToDate(testFromDb.getDuration().intValue(),tSODateTime);
		testFromDb.setExtendedEndTime(sdf.format(eETDateTime));
		
		
		String errorMessage = batchInsertExtendedTestTime(sapidArray,testFromDb); 
		
		if(StringUtils.isBlank(errorMessage)) {
			 updateStartDate(bean);
		 }else {
			 bean.setErrorRecord(true);
			 bean.setErrorMessage(errorMessage);
		 }
	}
	
	private  Date addMinutesToDate(int minutes, Date beforeTime){
	    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
	
	    long curTimeInMs = beforeTime.getTime();
	    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
	    return afterAddingMins;
	}

	@Transactional(readOnly = false)
	public boolean insertExtendedTestTime( final TestExamBean bean) {
		
		final String sql = "INSERT INTO exam.test_testextended_sapids "
				+ "(sapid, testId, extendedStartTime, extendedEndTime) "
				+ "VALUES(?,?,?,?) "
				+ "on duplicate key update "
				+ " extendedStartTime=?, extendedEndTime=?";
		
		try {

			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        
			        statement.setString(1, bean.getSapid());
			        statement.setLong(2, bean.getTestId());
			        statement.setString(3, bean.getExtendedStartTime());
			        statement.setString(4, bean.getExtendedEndTime());
					
					//On Update
			        statement.setString(5, bean.getExtendedStartTime());
			        statement.setString(6, bean.getExtendedEndTime());
			        
			        return statement;
			    }
			});

			return true;
		} catch (Exception e) {
			
			return false;
		}
		
	}
	
	@Transactional(readOnly = false)
	public String batchInsertExtendedTestTime(final List<String> sapids,final TestExamBean bean) {
		
		String sql = "INSERT INTO exam.test_testextended_sapids "
				+ "(sapid, testId, extendedStartTime, extendedEndTime) "
				+ "VALUES(?,?,?,?) "
				+ "on duplicate key update "
				+ " extendedStartTime=?, extendedEndTime=?";
		String errorMessage = "";
		
		try {
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setString(1,sapids.get(i));
					ps.setLong(2,bean.getId());
					ps.setString(3,bean.getExtendedStartTime());
					ps.setString(4,bean.getExtendedEndTime());
					
					//On Update
					ps.setString(5,bean.getExtendedStartTime());
					ps.setString(6,bean.getExtendedEndTime());
					
				}

				@Override
				public int getBatchSize() {
					return sapids.size();
				}
			  });

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			

			return "Error in extension , Error : "+e.getMessage();
		}
		return errorMessage;
	}
	
	@Transactional(readOnly = false)
	private void updateStartDate(TestExamBean bean) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE exam.test_student_testdetails " + 
				"SET  " + 
				"    testStartedOn = ?,testCompleted='N',showResult='N' " + 
				"WHERE " + 
				"    sapid = ? AND testId = ?";
		
		jdbcTemplate.update(sql, new Object[] {bean.getTestStartedOn(), bean.getSapid(), bean.getTestId()});
	}
	
	@Transactional(readOnly = false)
	public void updateRefreshCount(TestExamBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE exam.test_student_testdetails " + 
				"SET  " + 
				"    countOfRefreshPage = ?, testCompleted='N', showResult='N'" + 
				"WHERE " + 
				"    sapid = ? AND testId = ? AND attempt = ?";
		
		jdbcTemplate.update(sql,new Object[] {bean.getNoOfRefreshAllowed(), bean.getSapid(), bean.getTestId(), bean.getAttempt()});
	}

	@Transactional(readOnly = false)
	public void saveSupportForOtherIssues(final TestExamBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO `exam`.`test_proctoring_details`"
				+ "		(`testId`,`sapid`,`type`,`contactedSupport`,`reason`,`createdBy`,`createdDate`,`lastModifiedBy`,`lastModifiedDate`) "
				+ "VALUES "
				+ "		( ?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate()) "
				+ "ON DUPLICATE KEY UPDATE reason=concat(reason,'. ',sysdate(),' : ',? ) "
				+ ", lastModifiedBy=?, lastModifiedDate= sysdate()";

		jdbcTemplate.update(sql, new Object[] { bean.getTestId(), bean.getSapid(), bean.getType(),
				bean.getContactedSupport(), bean.getReason(), bean.getUserId(), bean.getUserId()
				, bean.getReason()
				, bean.getUserId() });
		
	}
	
	@Transactional(readOnly = true)
	public LostFocusLogExamBean getStudentDetailsForUnfairMeans(LostFocusLogExamBean  bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    firstName, lastName, emailId, mobile " + 
				"FROM " + 
				"    exam.students " + 
				"WHERE " + 
				"    sapid = ?";
		bean = jdbcTemplate.queryForObject(sql, new Object[] {bean.getSapid()}, new BeanPropertyRowMapper<LostFocusLogExamBean>(LostFocusLogExamBean.class));
		
		return bean;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<LostFocusLogExamBean> getTestForLostFocus() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    t.id AS testId, " + 
				"    testName, " + 
				"    t.startDate AS testStartDate, " + 
				"    `Subject` AS `subject`, " + 
				"    b.name AS batchName, " + 
				"    b.id AS batchid " + 
				"FROM " + 
				"    exam.test t " + 
				"        INNER JOIN " + 
				"    acads.sessionplan_module spm ON t.referenceId = spm.id " + 
				"        INNER JOIN " + 
				"    acads.sessionplanid_timeboundid_mapping sptm ON spm.sessionPlanId = sptm.sessionPlanId " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON sptm.timeboundId = ssc.id " + 
				"        INNER JOIN " + 
				"    exam.batch b ON b.id = ssc.batchId " + 
				"ORDER BY t.startDate DESC ";
		
		ArrayList<LostFocusLogExamBean> testIdList = (ArrayList<LostFocusLogExamBean>) jdbcTemplate.query(sql, new 
				BeanPropertyRowMapper<>(LostFocusLogExamBean.class));
		
		return testIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<LostFocusLogExamBean> getRecentTest() throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    t.id AS testId, " + 
				"    testName, " + 
				"    t.startDate AS testStartDate, " + 
				"    `Subject` AS `subject`, " + 
				"    b.name AS batchName, " + 
				"    b.id AS batchid " + 
				"FROM " + 
				"    exam.test t " + 
				"        INNER JOIN " + 
				"    acads.sessionplan_module spm ON t.referenceId = spm.id " + 
				"        INNER JOIN " + 
				"    acads.sessionplanid_timeboundid_mapping sptm ON spm.sessionPlanId = sptm.sessionPlanId " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON sptm.timeboundId = ssc.id " + 
				"        INNER JOIN " + 
				"    exam.batch b ON b.id = ssc.batchId " + 
				"WHERE " + 
				"    t.endDate BETWEEN DATE_SUB(SYSDATE(), INTERVAL 5 DAY) AND SYSDATE()";
		
		ArrayList<LostFocusLogExamBean> testIdList = (ArrayList<LostFocusLogExamBean>) jdbcTemplate.query(sql, new 
				BeanPropertyRowMapper<>(LostFocusLogExamBean.class));
		return testIdList;
	}

	@Transactional(readOnly = true)
	public ArrayList<LostFocusLogExamBean> getTestForSubjectAndDuration(LostFocusLogExamBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    t.id AS testId, "
				+ "    testName, "
				+ "    t.startDate AS testStartDate, "
				+ "    t.subject, "
				+ "    b.id AS batchId, "
				+ "    b.name AS batchName "
				+ "FROM "
				+ "    exam.test t "
				+ "        INNER JOIN "
				+ "    acads.sessionplan_module spm ON t.referenceId = spm.id "
				+ "        INNER JOIN "
				+ "    acads.sessionplanid_timeboundid_mapping sptm ON spm.sessionPlanId = sptm.sessionPlanId "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON sptm.timeboundId = ssc.id "
				+ "        INNER JOIN "
				+ "    exam.batch b ON b.id = ssc.batchId "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
				+ "WHERE "
				+ "    t.startDate BETWEEN ? AND ? "
				+ "        AND pss.id = ? ";
		
		ArrayList<LostFocusLogExamBean> testIdList = (ArrayList<LostFocusLogExamBean>) jdbcTemplate.query(sql, new Object[] { bean.getTestStartDate(), 
				bean.getTestEndDate(), bean.getProgram_sem_subject_id() },new BeanPropertyRowMapper<>(LostFocusLogExamBean.class));
		
		return testIdList;
	}

	public String markCopyCaseForLostFocus(LostFocusLogExamBean bean) throws Exception {

		updateCopyCaseAttemptStatus(bean);
		updateCopyCaseRemark(bean);
		
		return "done";
	}

	@Transactional(readOnly = false)
	private void updateCopyCaseAttemptStatus(LostFocusLogExamBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="UPDATE `exam`.`test_student_testdetails` " + 
				"SET " + 
				"	`attemptStatus` = 'CopyCase', " + 
				"	`score` = 0, " + 
				"	`lastModifiedDate` = sysdate(), " + 
				"	`lastModifiedBy` = 'LostFocusCopyCaseBatchJob' " + 
				"WHERE sapid = ? and testId = ? ";

		jdbcTemplate.update(sql,new Object[] {bean.getSapid(), bean.getTestId()});
		
	}

	@Transactional(readOnly = false)
	private void updateCopyCaseRemark(LostFocusLogExamBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="UPDATE `exam`.`test_students_answers` " + 
				"SET  " + 
				"    `marks` = 0, " + 
				"    `isChecked` = 1, " + 
				"    `remark` = 'Marked For Copy Case', " + 
				"	 `facultyId` = ?, "+
				"	 `lastModifiedDate` = sysdate(), " + 
				"	 `lastModifiedBy` = 'LostFocusCopyCaseBatchJob' " + 
				"WHERE " + 
				"    `testId` = ? AND sapid = ? " + 
				"        AND questionId IN (SELECT  " + 
				"            id " + 
				"        FROM " + 
				"            exam.test_questions " + 
				"        WHERE " + 
				"            testId = ? AND type IN (4 , 8)) ";

		jdbcTemplate.update(sql,new Object[] { bean.getFacultyId(), bean.getTestId(), bean.getSapid(), bean.getTestId() });
		
	}
	
	public String unmarkCopyCaseForLostFocus(LostFocusLogExamBean bean) throws Exception {

		removeCopyCaseAttemptStatus(bean);
		removeCopyCaseRemark(bean);
		
		return null;
	}

	@Transactional(readOnly = false)
	private void removeCopyCaseAttemptStatus(LostFocusLogExamBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="UPDATE `exam`.`test_student_testdetails` " + 
				"SET " + 
				"`attemptStatus` = 'Attempted', " + 
				"`score` = 0 " + 
				"WHERE sapid = ? and testId = ? ";

		jdbcTemplate.update(sql,new Object[] {bean.getSapid(), bean.getTestId()});
		
	}

	@Transactional(readOnly = false)
	private void removeCopyCaseRemark(LostFocusLogExamBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="UPDATE `exam`.`test_students_answers` " + 
				"SET  " + 
				"    `marks` = 0, " + 
				"    `isChecked` = 0, " + 
				"    `remark` = '' " + 
				"WHERE " + 
				"    `testId` = ? AND sapid = ? " + 
				"        AND questionId IN (SELECT  " + 
				"            id " + 
				"        FROM " + 
				"            exam.test_questions " + 
				"        WHERE " + 
				"            testId = ? AND type IN (4 , 8)) ";

		jdbcTemplate.update(sql,new Object[] {bean.getTestId(), bean.getSapid(), bean.getTestId()});
		
	}

	@Transactional(readOnly = true)
	public LostFocusLogExamBean getReasonForLostFocus(LostFocusLogExamBean  bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    GROUP_CONCAT(tpd.reason SEPARATOR ', ') as reason, std.attemptStatus "
				+ "FROM "
				+ "    exam.test_student_testdetails std "
				+ "        LEFT JOIN "
				+ "    exam.test_proctoring_details tpd ON std.testId = tpd.testId "
				+ "        AND std.sapid = tpd.sapid "
				+ "WHERE "
				+ "    std.sapid = ? "
				+ "        AND std.testId = ? "
				+ "GROUP BY std.sapid, std.testId "
				+ "ORDER BY std.id DESC";

		bean = jdbcTemplate.queryForObject(sql, new Object[] { bean.getSapid(), bean.getTestId() }, 
				new BeanPropertyRowMapper<LostFocusLogExamBean>(LostFocusLogExamBean.class));

		return bean;
	}

	@Transactional(readOnly = true)
	public ArrayList<LostFocusLogExamBean> getReasonForTestingRegex() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM exam.test_proctoring_details";
		
		ArrayList<LostFocusLogExamBean> reason = (ArrayList<LostFocusLogExamBean>) jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(LostFocusLogExamBean.class));
		
		return reason;
	}
	
	public List<StudentExamBean> getAllStudentsByMasterKey(List<Integer> consumerProgramStructureIdList) throws Exception{
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query =" SELECT  "
				+ "    s.sapid, s.firstName, s.lastName "
				+ "FROM "
				+ "    exam.students s "
				+ "WHERE "
				+ "    s.consumerProgramStructureId in ( :consumerProgramStructureIdList );";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("consumerProgramStructureIdList", consumerProgramStructureIdList);
		
		List<StudentExamBean> students = namedParameterJdbcTemplate.query( query, parameters, new BeanPropertyRowMapper<>(StudentExamBean.class) );

		return students;

	}
/*
	public void SupportContacted(TestBean bean) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE exam.test_student_testdetails " + 
				"SET " + 
				"    contactedSupport = ?, reason=? " + 
				"WHERE " + 
				"    sapid = ? AND testId = ? ";
		
		jdbcTemplate.update(sql,new Object[] { bean.getContactedSupport(), bean.getReason(), bean.getSapid(), bean.getTestId() });
	}

	public void updateRefreshCountAndSupportContacted(TestBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE exam.test_student_testdetails " + 
				"SET " + 
				"    countOfRefreshPage = ?, contactedSupport = ?, reason=? " + 
				"WHERE " + 
				"    sapid = ? AND testId = ? AND attempt = ?";
		
		jdbcTemplate.update(sql,new Object[] { bean.getNoOfRefreshAllowed(), bean.getContactedSupport(), bean.getReason(), 
				bean.getSapid(), bean.getTestId(), bean.getAttempt() });
	}
*/
	
}
