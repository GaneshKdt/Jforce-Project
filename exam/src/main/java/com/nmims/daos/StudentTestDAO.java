package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;

public class StudentTestDAO extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	public String getActiveSubject( Long id ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT "
				+ "    id "
				+ "FROM "
				+ "    lti.student_subject_config "
				+ "WHERE "
				+ "    SYSDATE() BETWEEN startDate AND endDate ";
		
		String configId = jdbcTemplate.queryForObject( query, String.class );
		
		return configId;
		
	}
	
	public List<String> getTimeboundId(String userId) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT "
				+ "    timebound_subject_config_id	 "
				+ "FROM "
				+ "    lti.timebound_user_mapping "
				+ "WHERE "
				+ "    userId = ? "
				+ "        AND role = 'Student' ";
		
		List<String> timeboundId = jdbcTemplate.queryForList(query, String.class, userId);
		
		return timeboundId;
		
	}
	
	public String getSessionPlan( String id ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT "
				+ "    sessionPlanId "
				+ "FROM "
				+ "    acads.sessionplanid_timeboundid_mapping "
				+ "WHERE "
				+ "    timeboundId = ?";
		
		String sessionPlanId = jdbcTemplate.queryForObject( query, String.class, id );
		
		return sessionPlanId;
		
	}

	public List<String> getSessionPlanModuleList( String sessionPlanId ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT "
				+ "    id "
				+ "FROM "
				+ "    acads.sessionplan_module "
				+ "WHERE "
				+ "    sessionPlanId = ?";
		
		List<String> sessionPlanModules = jdbcTemplate.query( query, new SingleColumnRowMapper<>(String.class), sessionPlanId );
		
		return sessionPlanModules;
		
	}

	public List<String> getTestLiveSettings( List<String> referenceIds ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    referenceId "
				+ "FROM "
				+ "    exam.test_live_settings "
				+ "WHERE "
				+ "    liveType = 'Regular' "
				+ "        AND applicableType = 'module'"
				+ "    	   AND referenceId IN ( :referenceId ) ";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("referenceId", referenceIds);
		
		List<String> sessionPlanModules = namedParameterJdbcTemplate.query( query, parameters, new SingleColumnRowMapper<>(String.class) );
		
		return sessionPlanModules;
		
	}

	public List<String> getArchiveTestLiveSettings( List<String> referenceIds ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    referenceId "
				+ "FROM "
				+ "    exam.test_live_settings_archive "
				+ "WHERE "
				+ "    liveType = 'Regular' "
				+ "        AND applicableType = 'module'"
				+ "    	   AND referenceId IN ( :referenceId ) ";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("referenceId", referenceIds);
		
		List<String> sessionPlanModules = namedParameterJdbcTemplate.query( query, parameters, new SingleColumnRowMapper<>(String.class) );
		
		return sessionPlanModules;
		
	}

	public List<String> getTestLiveSettingsForSingleId( String referenceIds ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    referenceId "
				+ "FROM "
				+ "    exam.test_live_settings "
				+ "WHERE "
				+ "    liveType = 'Regular' "
				+ "        AND applicableType = 'module'"
				+ "    	   AND referenceId = ? ";
		
		List<String> sessionPlanModules = jdbcTemplate.query( query, new SingleColumnRowMapper<>(String.class), referenceIds );
		
		return sessionPlanModules;
		
	}

	public List<String> getArchiveTestLiveSettingsForSingleId( String referenceIds ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    referenceId "
				+ "FROM "
				+ "    exam.test_live_settings_archive "
				+ "WHERE "
				+ "    liveType = 'Regular' "
				+ "        AND applicableType = 'module'"
				+ "    	   AND referenceId = ? ";
		
		List<String> sessionPlanModules = jdbcTemplate.query( query, new SingleColumnRowMapper<>(String.class), referenceIds );
		
		return sessionPlanModules;
		
	}
	
	public List<String> getTestConfigurations( List<String> referenceIds ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    testId "
				+ "FROM "
				+ "    exam.test_testid_configuration_mapping "
				+ "WHERE "
				+ "    referenceId IN ( :referenceId );";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("referenceId", referenceIds);
		
		List<String> sessionPlanModules = namedParameterJdbcTemplate.query( query, parameters, new SingleColumnRowMapper<>(String.class) );
		
		return sessionPlanModules;
		
	}
	
	public List<TestExamBean> getPendingTest( List<String> testId ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    id IN ( :testId ) "
				+ "        AND endDate >= SYSDATE()";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("testId", testId);
		
		List<TestExamBean> tests = namedParameterJdbcTemplate.query( query, parameters, new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return tests;
		
	}

	public List<TestExamBean> getOngoingTest( List<String> testId ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    id IN ( :testId ) "
				+ "        AND SYSDATE() >= endDate "
				+ "        AND SYSDATE() <= DATE_ADD( endDate, "
				+ "        INTERVAL duration MINUTE)";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("testId", testId);
		
		List<TestExamBean> tests = namedParameterJdbcTemplate.query( query, parameters, new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return tests;
		
	}

	public List<TestExamBean> getExtendedTest( List<String> testId, String sapid ){

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test t "
				+ "        INNER JOIN "
				+ "    exam.test_testextended_sapids tes ON t.id = tes.testId "
				+ "WHERE "
				+ "    id IN ( :testId ) "
				+ "        AND sapid = :sapid "
				+ "        AND SYSDATE() <= DATE_ADD(tes.extendedEndTime, "
				+ "        INTERVAL t.duration MINUTE)";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("testId", testId);
		parameters.addValue("sapid", sapid);
		
		List<TestExamBean> tests = namedParameterJdbcTemplate.query( query, parameters, new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return tests;
		
	}

	public List<TestExamBean> getFinishedTest( List<String> testId ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    id IN ( :testId ) "
				+ "        AND sysdate() > endDate";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("testId", testId);
		
		List<TestExamBean> tests = namedParameterJdbcTemplate.query( query, parameters, new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return tests;
		
	}

	public List<TestExamBean> getTestDetails( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ " id as testDetailsId,"
				+ "	   testId, "
				+ "    COALESCE(attempt, 0) AS attempt, "
				+ "    testStartedOn, "
				+ "    COALESCE(remainingTime, 0) AS remainingTime, "
				+ "    testEndedOn, "
				+ "    testCompleted, "
				+ "    COALESCE(score, 0) AS score, "
				+ "    COALESCE(score, 0) AS scoreInInteger, "
				+ "    COALESCE(noOfQuestionsAttempted, 0) AS noOfQuestionsAttempted, "
				+ "    COALESCE(currentQuestion, 0) AS currentQuestion, "
				+ "    attemptStatus "
				+ "FROM "
				+ "    exam.test_student_testdetails "
				+ "WHERE "
				+ "    sapid = ? ";
		
		List<TestExamBean> testDetails = jdbcTemplate.query( query, new BeanPropertyRowMapper<>(TestExamBean.class), sapid );
		
		return testDetails;
		
	}

	public List<TestExamBean> getArchiveTestDetails( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ " id as testDetailsId,"
				+ "	   testId, "
				+ "    COALESCE(attempt, 0) AS attempt, "
				+ "    testStartedOn, "
				+ "    COALESCE(remainingTime, 0) AS remainingTime, "
				+ "    testEndedOn, "
				+ "    testCompleted, "
				+ "    COALESCE(score, 0) AS score, "
				+ "    COALESCE(score, 0) AS scoreInInteger, "
				+ "    COALESCE(noOfQuestionsAttempted, 0) AS noOfQuestionsAttempted, "
				+ "    COALESCE(currentQuestion, 0) AS currentQuestion, "
				+ "    attemptStatus "
				+ "FROM "
				+ "    exam.test_student_testdetails_archive "
				+ "WHERE "
				+ "    sapid = ? ";
		
		List<TestExamBean> testDetails = jdbcTemplate.query( query, new BeanPropertyRowMapper<>(TestExamBean.class), sapid );
		
		return testDetails;
		
	}
	
	public Boolean checkIfTimeboundOfCurrentCycle(String timeboundId, String current_mbawx_acad_year, 
			String current_mbawx_acad_month) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    EXISTS( SELECT  "
				+ "            * "
				+ "        FROM "
				+ "            lti.student_subject_config "
				+ "        WHERE "
				+ "            id = ? AND acadYear = ? "
				+ "                AND acadMonth = ?)";
		
		Boolean exists = jdbcTemplate.queryForObject(query, Boolean.class, timeboundId, 
				current_mbawx_acad_year, current_mbawx_acad_month);
		
		return exists;
		
	}

	public List<TestExamBean> getTestForReferenceId( List<String> referenceIds ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    referenceId IN ( :referenceId );";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("referenceId", referenceIds);
		
		List<TestExamBean> test  = namedParameterJdbcTemplate.query( query, parameters, 
				new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return test;
		
	}

	public List<TestExamBean> getArchiveTestForReferenceId( List<String> referenceIds ) throws Exception{

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test_archive "
				+ "WHERE "
				+ "    referenceId IN ( :referenceId );";

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("referenceId", referenceIds);
		
		List<TestExamBean> test  = namedParameterJdbcTemplate.query( query, parameters, 
				new BeanPropertyRowMapper<>(TestExamBean.class) );
		
		return test;
		
	}

	public List<TestExamBean> getStudentTestExtendedDetails( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    sapid, testId, extendedStartTime,  extendedEndTime "
				+ "FROM "
				+ "    exam.test_testextended_sapids "
				+ "WHERE "
				+ "    sapid = ?";

		List<TestExamBean> tests = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TestExamBean.class), sapid);
		
		return tests;
		
	}

	public List<TestExamBean> getArchiveStudentTestExtendedDetails( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    sapid, testId, extendedStartTime,  extendedEndTime "
				+ "FROM "
				+ "    exam.test_testextended_sapids_archive "
				+ "WHERE "
				+ "    sapid = ?";

		List<TestExamBean> tests = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TestExamBean.class), sapid);
		
		return tests;
		
	}

	public List<TestExamBean> getCurrentCycleTest( String current_mbawx_acad_year, String current_mbawx_acad_month )throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT "
				+ "    * "
				+ "FROM "
				+ "    exam.test "
				+ "WHERE "
				+ "    acadYear = ? AND acadMonth = ?";

		List<TestExamBean> tests = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TestExamBean.class), 
				current_mbawx_acad_year, current_mbawx_acad_month);
		
		return tests;
		
	}

	public List<StudentsTestDetailsExamBean> getAttemptDetailsByTestidAndSapid(String sapid,Long testId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select * from exam.test_student_testdetails where sapid = ? and testId = ? order by id ";
		
		List<StudentsTestDetailsExamBean> testsByStudent = jdbcTemplate.query(sql, new Object[] {sapid,testId}, 
				new BeanPropertyRowMapper<>(StudentsTestDetailsExamBean.class));

		return testsByStudent;
		
	}

	public StudentsTestDetailsExamBean getStudentsTestDetailsBySapidAndTestId(String sapid, Long testId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//get latest entry of students attempts
		String sql="select * from exam.test_student_testdetails where sapid=? and testId=? order by id desc limit 1";
	
		StudentsTestDetailsExamBean testByStudent = jdbcTemplate.queryForObject(sql, new Object[] {sapid,testId}, 
					new BeanPropertyRowMapper<>(StudentsTestDetailsExamBean.class));

		return testByStudent;
	}

	public Map<Integer,List<StudentQuestionResponseExamBean>> getAttemptAnswersMapBySapidAndTestid(String sapid, Long testId){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.test_students_answers "
				+ "WHERE "
				+ "    sapid = ? "
				+ "        AND testId = ? "
				+ "ORDER BY id";


		List<StudentQuestionResponseExamBean> testAnswersByStudent = jdbcTemplate.query(sql, new Object[] {sapid,testId,sapid,testId}, 
				new BeanPropertyRowMapper<>(StudentQuestionResponseExamBean.class));
		
		Map<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = testAnswersByStudent.stream()
			.collect(Collectors.groupingBy(StudentQuestionResponseExamBean::getAttempt));

		return attemptsAnswerMap;
	}
	
	/**
	 * @method check user role is student
	 *@return Integer: 1 if user is student
	 * @param id,sapid 
	 * */
	public int checkRole(String timeBountid,String sapid) throws Exception{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String query="SELECT COUNT(*) "
				+ "FROM lti.timebound_user_mapping "
				+ "WHERE timebound_subject_config_id= ? "
				+ "AND role ='student' "
				+ "AND userid= ?";
		return jdbcTemplate.queryForObject( query, Integer.class, timeBountid ,sapid );
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, Integer> getApplicableTestsMaxScore(final List<Integer> sessionPlanIdList) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String query =  "SELECT t.id, t.maxScore " + 
						"FROM exam.test t " + 
						"INNER JOIN exam.test_live_settings tls" + 
						"	ON t.referenceId = tls.referenceId" + 
						"	AND t.year = tls.examYear" + 
						"	AND t.month = tls.examMonth" + 
						"   AND t.acadYear = tls.acadYear" + 
						"   AND t.acadMonth = tls.acadMonth " + 
						"INNER JOIN acads.sessionplan_module spm" + 
						"	ON tls.referenceId = spm.id " + 
						"WHERE spm.sessionPlanId IN (:sessionPlanIds)" +
						"	AND t.showResultsToStudents = 'Y'";
		
		return namedParameterJdbcTemplate.query(query, new MapSqlParameterSource("sessionPlanIds", sessionPlanIdList), (ResultSet rs) ->  resultSetMapper(rs, Integer.class, Integer.class));
	}
	
	@Transactional(readOnly = true)
	public double getStudentTotalScoreByTestIds(final Set<Integer> testIdSet, final String sapid) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String query =  "SELECT COALESCE(SUM(score), 0) " + 		//Using COALESCE to avoid NullPointerException for students who haven't appeared any tests
						"FROM exam.test_student_testdetails " + 
						"WHERE testId IN (:testIds)" + 
						"	AND sapid = :sapid";
		
		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("testIds", testIdSet);
		parameterMap.addValue("sapid", sapid);
		
		return namedParameterJdbcTemplate.queryForObject(query, parameterMap, Double.class);
	}
	
	@Transactional(readOnly = true)
	public List<Long> getTimeboundIdsBySapid(final String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String query = "SELECT ssc.id " + 
							"FROM lti.student_subject_config ssc " + 
							"INNER JOIN lti.timebound_user_mapping tum " + 
							"	ON ssc.id = tum.timebound_subject_config_id " + 
							"WHERE tum.userId = ?";
		
		return jdbcTemplate.queryForList(query, Long.class, sapid);
	}
	
	@Transactional(readOnly = true)
	public Long getTimeboundIdBySessionPlanModuleId(final Integer sessionPlanModuleId) {
		final String query = "SELECT ssc.id " + 
							"FROM lti.student_subject_config ssc " + 
							"INNER JOIN acads.sessionplanid_timeboundid_mapping sptm " + 
							"	ON ssc.id = sptm.timeboundId " + 
							"INNER JOIN acads.sessionplan_module spm " + 
							"	ON sptm.sessionPlanId = spm.sessionPlanId " + 
							"WHERE spm.id = ?";
		
		return jdbcTemplate.queryForObject(query, Long.class, sessionPlanModuleId);
	}
	
	@Transactional(readOnly = true)
	public Long getPssIdByTimeboundId(final Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String query = "SELECT prgm_sem_subj_id " + 
							"FROM lti.student_subject_config " + 
							"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, Long.class, id);
	}
	
	@Transactional(readOnly = true)
	public ProgramSubjectMappingExamBean getCpsIdSubjectById(final Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String query = "SELECT consumerProgramStructureId, subject " + 
							"FROM exam.program_sem_subject " + 
							"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ProgramSubjectMappingExamBean.class), id);
	}
	
	@Transactional(readOnly = true)
	public List<TestExamBean> getTestByReferenceIdMaxScore(final List<String> referenceIdList, final int maxScore, final String testType) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		final String query = "SELECT * FROM exam.test " + 
							"WHERE maxScore = :score " + 
							"	AND testType = :type " + 
							"	AND referenceId IN (:referenceIds)";
		
		MapSqlParameterSource parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("score", maxScore);
		parameterMap.addValue("type", testType);
		parameterMap.addValue("referenceIds", referenceIdList);
		
		return namedParameterJdbcTemplate.query(query, parameterMap, new BeanPropertyRowMapper<>(TestExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public TestExamBean getTestExtendedBySapidTestId(final String sapid, final Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String query = "SELECT * FROM exam.test_testextended_sapids " + 
							"WHERE sapid = ? " + 
							"	AND testId = ?";
		
		return jdbcTemplate.queryForObject(query, TestExamBean.class, sapid, testId);
	}
	
	@Transactional(readOnly = true)
	public StudentsTestDetailsExamBean getStudentTestDetailsBySapidTestIdAttempt(final String sapid, final Long testId, final int attempt) {
		final String query = "SELECT testId, " + 
							"	COALESCE(attempt, 0) AS attempt, " + 
							"	attemptStatus, " + 
							"	testStartedOn, " + 
							"	COALESCE(remainingTime, 0) AS remainingTime, " + 
							"	testEndedOn, " + 
							"	testCompleted, " + 
							"	COALESCE(score, 0) AS score, " + 
							"	COALESCE(noOfQuestionsAttempted, 0) AS noOfQuestionsAttempted, " + 
							"	COALESCE(currentQuestion, 0) AS currentQuestion, " + 
							"	showResult " + 
							"FROM exam.test_student_testdetails " + 
							"WHERE sapid = ? " + 
							"	AND testId = ? " + 
							"	AND attempt = ?";
		
		return jdbcTemplate.queryForObject(query, StudentsTestDetailsExamBean.class, sapid, testId, attempt);
	}
	
	/**
	 * A ResultSet Mapper which returns a HashMap wherein the first ResultSet value is set as the Key, and second as it's Value 
	 * @param rs - ResultSet consisting of the result returned from the database query
	 * @param keyClass - Class to be set for Key
	 * @param valueClass - Class to be set for Value
	 * @return - HashMap containing the values from the ResultSet provided
	 * @throws SQLException
	 */
	private <K, V> Map<K, V> resultSetMapper(final ResultSet rs, final Class<K> keyClass, final Class<V> valueClass) throws SQLException {
		Map<K, V> resultMap= new HashMap<>();
        while(rs.next()) {
        	resultMap.put(keyClass.cast(rs.getObject(1)), valueClass.cast(rs.getObject(2)));
        }
        
        return resultMap;
	}
}
