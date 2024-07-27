package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MBAXPassFailBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.beans.UpgradQuestionAnsweredDetailsBean;
import com.nmims.beans.UpgradTestQuestionExamBean;
import com.nmims.beans.UpgradTestQuestionOptionExamBean;

@Repository("upgradAssessmentDao")
public class UpgradAssessmentDao {
	@Value("${TEST_USER_SAPIDS}")
	private String TEST_USER_SAPIDS;
	
	private PlatformTransactionManager transactionManager;
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;



	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;

	}
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Transactional(readOnly = false)
	public void insertUpgradAssessmentScores(UpgradAssessmentExamBean testBean) {
		//TransactionDefinition def = new DefaultTransactionDefinition();
		//TransactionStatus status = transactionManager.getTransaction(def);

			
				String sql = "INSERT INTO `exam`.`upgrad_student_assesmentscore` " + 
						"(`sapid`, " + 
						"`testId`, " + 
						"`attempt`, " + 
						"`testStartedOn`, " + 
						"`remainingTime`, " + 
						"`testEndedOn`, " + 
						"`testCompleted`, " + 
						"`score`, " + 
						"`testQuestions`, " + 
						"`noOfQuestionsAttempted`, " + 
						"`showResult`, " + 
						"`createdBy`, " + 
						"`createdDate`, " + 
						"`lastModifiedBy`, " + 
						"`lastModifiedDate`, " + 
						"`attemptStatus`) " + 
						"VALUES " + 
						"(?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"'Upgrad', " + 
						"sysdate(), " + 
						"'Upgrad', " + 
						"sysdate(), " +
						"?) " +
						"on duplicate key update" +
						"`testStartedOn`=?, " + 
						"`remainingTime`=?, " + 
						"`testEndedOn`=?, " + 
						"`testCompleted`=?, " + 
						"`score`=?, " + 
						"`testQuestions`=?, " + 
						"`noOfQuestionsAttempted`=?, " + 
						"`showResult`=?, " + 
						" `attempt`=?, " + 
						" `attemptStatus`=?, " + 
						"`lastModifiedDate`=sysdate() ";
				jdbcTemplate.update(sql,testBean.getSapid(),
						testBean.getTestId(),
						testBean.getAttempt(),
						testBean.getTestStartedOn(),
						testBean.getRemainingTime(),
						testBean.getTestEndedOn(),
						testBean.getTestCompleted(),
						testBean.getScore(),
						testBean.getTestQuestionsApplicable(),
						testBean.getNoOfQuestionsAttempted(),
						testBean.getShowResult(),
						testBean.getAttemptStatus(),

						testBean.getTestStartedOn(),
						testBean.getRemainingTime(),
						testBean.getTestEndedOn(),
						testBean.getTestCompleted(),
						testBean.getScore(),
						testBean.getTestQuestionsApplicable(),
						testBean.getNoOfQuestionsAttempted(),
						testBean.getShowResult(),
						testBean.getAttempt(),
						testBean.getAttemptStatus()
						
						);

	}
	
	@Transactional(readOnly = false)
	public void insertUpgradAssessmentDetails(UpgradQuestionAnsweredDetailsBean bean, String sapid, Long testId, Integer marksObtained) {

		String sql = "INSERT INTO `exam`.`upgrad_student_assementsdetails` " +
				"(`sapid`, " + 
				"`testId`, " +  
				"`questionNo`, " + 
				"`question`, " + 
				"`studentAnswer`, " + 
				"`marksObtained`, " + 
				"`peerPenalty`, " + 
				"`onlinePenalty`, " + 
				"`createdBy`, " +
				"`createdDate`, " + 
				"`lastModifiedBy`, " + 
				"`lastModifiedDate`, "+
				"`remark` ) " + 
				"VALUES "+ 
				"(?, " + 
				"?, " + 
				"?, " + 
				"?, " + 
				"?, " + 
				"?, " + 
				"?, " + 
				"?, " + 
				"'Upgrad', " + 
				"sysdate(), " + 
				"'Upgrad', " +
				"sysdate(),  "+
				" ? )"+
				"on duplicate key update" +
				
				"`question`=?, " + 
				"`studentAnswer`=?, " + 
				"`marksObtained`=?, " + 
				"`peerPenalty`=?, " + 
				"`onlinePenalty`=?, " + 
				"`lastModifiedDate`=sysdate(), "+
			    " `remark` = ? ";
		jdbcTemplate.update(sql,
				sapid,
				testId,
				bean.getQuestionNo(),
				bean.getQuestion(), 
				bean.getStudentAnswer(), 
				marksObtained,
				bean.getPeerPenalty(),
				bean.getOnlinePenalty(),
				bean.getRemark(),
				
				bean.getQuestion(), 
				bean.getStudentAnswer(), 
				bean.getMarksObtained(), 
				bean.getPeerPenalty(),
				bean.getOnlinePenalty(),
				bean.getRemark());
	}
	
	@Transactional(readOnly = true)
	public Integer getQuestionTypeById(Integer questionNo, Long testId) {
		Integer questionTypeId = 0;
		String sql = " SELECT type FROM exam.upgrad_test_questions where id = ? AND testId = ? ";
		questionTypeId =  jdbcTemplate.queryForObject(sql,new Object[] {
				 questionNo,testId } ,Integer.class);
		return questionTypeId;
	}
	
	@Transactional(readOnly = false)
	public void deleteAssessmentScoreCascade(UpgradAssessmentExamBean upgradAssessmentBean) {
		String sql = "DELETE FROM `exam`.`upgrad_student_assesmentscore`"
				+   "   WHERE `sapid`= ? and`testId`= ? ";
		 jdbcTemplate.update(sql, new Object[] {
				 upgradAssessmentBean.getSapid(),
				 upgradAssessmentBean.getTestId()	} );
	}
	
	@Transactional(readOnly = true)
	public Integer getTimeboundIdByTestId(Long testId) {
		Integer timeboundId = 0 ;
		String sql = "SELECT " + 
				"map.timeboundId " + 
				"FROM " + 
				"    exam.upgrad_test test " + 
				"        INNER JOIN " + 
				"    acads.upgrad_sessionplan_module module ON test.referenceId = module.id " + 
				"        INNER JOIN " + 
				"    acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = module.sessionPlanId " + 
				"WHERE " + 
				"    test.id = ?"	;
		timeboundId = (Integer)jdbcTemplate.queryForObject(sql, new Object[] {testId},Integer.class);
		return timeboundId;
	}
	
	@Transactional(readOnly = false)
	public void updateMarksAndMarksHistoryProcessedFlag(Integer timebound_id, String sapId) {
		String sql1 = "UPDATE `exam`.`mbax_marks` SET `processed`='N' WHERE `timebound_id`= ? AND `sapid`= ? ";
		String sql2 = "UPDATE `exam`.`mbax_marks_history` SET `processed`='N' WHERE `timebound_id`= ? AND `sapid`= ? ";
		jdbcTemplate.update(sql1, new Object[] {timebound_id, sapId	} );
		jdbcTemplate.update(sql2, new Object[] {timebound_id, sapId	} );
	}
	
	@Transactional(readOnly = false)
	public void updatePassFailIsResultLiveFlag(Integer timebound_id, String sapId) {
		String sql1 = "UPDATE `exam`.`mbax_passfail` SET `isResultLive`='N' WHERE `timeboundId`=  ? and`sapid`= ?"  ;
		jdbcTemplate.update(sql1, new Object[] {timebound_id, sapId	} );
	}

	@Transactional(readOnly = false)
	public long saveUpgradTest(final TestExamBean test) {

		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO exam.upgrad_test "
				+ " (year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate,  subject,"
				+ "  maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, randomQuestion,"
				+ " testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert, maxScore, duration, passScore, testType,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
				+ " consumerTypeIdFormValue, programStructureIdFormValue, programIdFormValue, "
				+ " applicableType, referenceId "
				+ ") "
				+ " VALUES(?,?,?,?,?,?,?,?,?" //9 ?
				+ "		   ,?,?,?,?,?,?" // 6 ? 
				+ "		   ,?,?,?,?,?,?,?,?" // 8 ?
				+ "		   ,?,sysdate(),?,sysdate()"
				+ "		   ,?,?,?"
				+ "		   ,?,?"
				+ ") ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, test.getYear());
					statement.setString(2, test.getMonth());
					statement.setString(3, test.getYear());
					statement.setString(4, test.getAcadMonth()); 
					statement.setString(5, test.getTestName()); 
					statement.setString(6, test.getTestDescription()); 
					statement.setString(7, test.getStartDate()); 
					statement.setString(8, test.getEndDate());
					statement.setString(9, test.getSubject());

					statement.setInt(10, test.getMaxQuestnToShow());
					statement.setString(11, test.getShowResultsToStudents());
					statement.setString(12, test.getActive());     
					statement.setString(13, test.getFacultyId());
					statement.setInt(14, test.getMaxAttempt());
					statement.setString(15, test.getRandomQuestion());

					statement.setString(16, test.getTestQuestionWeightageReq()); 
					statement.setString(17, test.getAllowAfterEndDate()); 
					statement.setString(18, test.getSendEmailAlert()); 
					statement.setString(19, test.getSendSmsAlert()); 
					statement.setInt(20, test.getMaxScore());
					statement.setInt(21, test.getDuration());
					statement.setInt(22, test.getPassScore());
					statement.setString(23, test.getTestType());

					statement.setString(24, test.getCreatedBy());
					statement.setString(25, test.getLastModifiedBy());     

					statement.setString(26, test.getConsumerTypeIdFormValue());
					statement.setString(27, test.getProgramStructureIdFormValue()); 
					statement.setString(28, test.getProgramIdFormValue());


					statement.setString(29, test.getApplicableType()); 
					statement.setInt(30, test.getReferenceId());

					return statement;
				}
			}, holder);
			final long primaryKey = holder.getKey().longValue();
			//TestBean testToshow = getTestById(primaryKey) ;


			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

	}
	
	@Transactional(readOnly = false)
	public String insertUpgradTestIdNConfigurationMappings(final TestExamBean bean,final List<Long> configIds) {
		try {
			String sql = " INSERT INTO exam.upgrad_test_testid_configuration_mapping "
					+ " (testId, type, referenceId, createdBy, createdDate) "
					+ " VALUES (?,?,?,?,sysdate()) "
					+ " "
					;

			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setLong(1,bean.getId());
					ps.setString(2,bean.getApplicableType());

					if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
						ps.setLong(3,configIds.get(i));
					}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(3,configIds.get(i));
					}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(3,configIds.get(i));
					}else {
						ps.setLong(3, (long)0);
					}
					ps.setString(4,bean.getCreatedBy());		
				}

				@Override
				public int getBatchSize() {
					return configIds.size();
				}
			});
			return "";
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return "Error in insertTestIdNConfigurationMappings : "+e.getMessage();
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<UpgradAssessmentExamBean> getCombinedDetailsOfScoreAndAssignmentDetails(Long testId){
		ArrayList<UpgradAssessmentExamBean> assignmentDetailsList = new ArrayList<UpgradAssessmentExamBean>();
		ArrayList<UpgradQuestionAnsweredDetailsBean> testQuestionsAnsDetails = new ArrayList<UpgradQuestionAnsweredDetailsBean>();
		try {

			String sql = " SELECT " + 
					"    sapId, " + 
					"    testId, " + 
					"    attempt, " + 
					"    testStartedOn, " + 
					"    remainingTime, " + 
					"    testEndedOn, " + 
					"    score, " + 
					"    testQuestions, " + 
					"    noOfQuestionsAttempted, " + 
					"    showResult " + 
					" FROM " + 
					"    exam.upgrad_student_assesmentscore" + 
					"    where testId= ? " + 
					"GROUP BY sapid , testId , attempt; ";
			
			assignmentDetailsList =  (ArrayList<UpgradAssessmentExamBean>)jdbcTemplate.query(sql,new Object[] { testId},new BeanPropertyRowMapper<UpgradAssessmentExamBean>(UpgradAssessmentExamBean.class));
			
			
			for(UpgradAssessmentExamBean assignmentDetail : assignmentDetailsList) {
				sql = "SELECT questionNo,question,studentAnswer,marksObtained "
						+ " FROM exam.upgrad_student_assementsdetails where sapid="+ assignmentDetail.getSapid().toString()+
					  " and testId="+ assignmentDetail.getTestId().toString();
				testQuestionsAnsDetails = (ArrayList<UpgradQuestionAnsweredDetailsBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<UpgradQuestionAnsweredDetailsBean>(UpgradQuestionAnsweredDetailsBean.class));
				assignmentDetail.setTestQuestionsAnsDetails(testQuestionsAnsDetails);
			}
			
			
			
//			JSONObject validationObject;
//			JSONObject validatedDataToSend = new JSONObject();
//			
//			for(UpgradAssessmentBean assignmentDetail : assignmentDetailsList) {
//				validationObject = new JSONObject();
//				validationObject.put("sapId", assignmentDetail.getSapid());
//				validationObject.put("testId", assignmentDetail.getTestId());
//				validationObject.put("attempt", assignmentDetail.getAttempt());
//				validationObject.put("testStartedOn", assignmentDetail.getTestStartedOn());
//				validationObject.put("remainingTime", assignmentDetail.getRemainingTime());
//				validationObject.put("testEndedOn", assignmentDetail.getTestEndedOn());
//				validationObject.put("score", assignmentDetail.getScore());
//				validationObject.put("testQuestions", assignmentDetail.getTestQuestions());
//				validationObject.put("noOfQuestionsAttempted", assignmentDetail.getNoOfQuestionsAttempted());
//				validationObject.put("showResult", assignmentDetail.getShowResult());
//				
//				for(UpgradQuestionAnsweredDetailsBean testQuestionsAnsDetailsTemp : assignmentDetail.getTestQuestionsAnsDetails()) {
//					validationObject.put("questionNo", testQuestionsAnsDetailsTemp.getQuestionNo());
//					validationObject.put("question", testQuestionsAnsDetailsTemp.getQuestion());
//					validationObject.put("studentAnswer", testQuestionsAnsDetailsTemp.getStudentAnswer());
//					validationObject.put("marksObtained", testQuestionsAnsDetailsTemp.getMarksObtained());
//					
//				}
//
//				
////				tempObj = checkTestDataValidity(validationObject);
//
//				assignmentDetail.setValidatedTestDetails(checkTestDataValidity(validationObject));
//			}
			
			
		}catch(Exception e) {
			
		}
		return assignmentDetailsList;
	}
	
	public JSONObject checkTestDataValidity(JSONObject jsonObj) {
		
		boolean isValid = false;
		JSONObject validityObject = new JSONObject();
		Set keySet = jsonObj.keySet();
		for (Object key : keySet) {
	        
	        String keyStr = (String)key;
	        Object keyvalue = jsonObj.get(keyStr);

	        //Print key and value
	        
	        switch(keyStr) {
	        case "sapId":
	        	if(Pattern.matches("^[7]{1}[0-9]{10}$", keyvalue.toString()))
	        		isValid = true;
	        	break;
	        case "testId":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "attempt":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "testStartedOn":
	        	if(Pattern.matches("^\\d\\d\\d\\d-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01]) (00|[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9])\\.([0-9]*)$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "testEndedOn":
	        	if(Pattern.matches("^\\d\\d\\d\\d-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01]) (00|[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9])\\.([0-9]*)$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "remainingTime":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "score":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "testQuestions":
	        	if(keyvalue.toString() != null && !"".equals(keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "noOfQuestionsAttempted":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "showResult":
	        	if(Pattern.matches("^[a-zA-Z]{1}$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "questionNo":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "question":
	        	if(keyvalue.toString() != null && !"".equals(keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "studentAnswer":
	        	if(keyvalue.toString() != null && !"".equals(keyvalue.toString()))
	        		isValid = true;
	        	
	        	break;
	        case "marksObtained":
	        	if(Pattern.matches("^[0-9]*$", keyvalue.toString()))
	        		isValid = true;
	        	break;
	        default :
	        	break;
	        }
	        
	        
	        validityObject.put(keyStr, isValid);

	        //for nested objects iteration if required
//	        if (keyvalue instanceof JSONObject)
//	            printJsonObject((JSONObject)keyvalue);
	    }
//		jsonObj.put("isValid", validityObject);
		
		
		
//		return jsonObj;
		return validityObject;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<UpgradAssessmentExamBean> getCombinedDetailsOfScoreAndAssignmentDetailsToDisplay(Long testId){
		ArrayList<UpgradAssessmentExamBean> assignmentDetailsList = new ArrayList<UpgradAssessmentExamBean>();

			String sql = "SELECT " + 
					"  (select count(*) from exam.upgrad_student_assesmentscore where testId = ? ) as sapIdCount, " + 
					"  (select count(*) from exam.upgrad_student_assesmentscore where testId = ? and showResult = 'Y') as showResultCountY," + 
					"  (select count(*) from exam.upgrad_student_assesmentscore where testId = ? and attempt = 1 and attemptStatus = 'Attempted') as attemptCount, "+
					"  (select count(*) from exam.upgrad_student_assesmentscore where testId = ? and attempt = 1 and attemptStatus = 'CopyCase') as copyCaseCount, "+
					"  test.maxQuestnToShow as questionNoCount, "+
					"  (SELECT  max(onlinePenalty) FROM exam.upgrad_student_assementsdetails a where a.testId = ? AND a.sapid = score.sapid)as onlinePenalty, "+
					"  (SELECT  max(peerPenalty) FROM exam.upgrad_student_assementsdetails a where a.testId = ? AND a.sapid = score.sapid)as peerPenalty, "+
					"    test.testName, " + 
					"    test.maxScore, " + 
					"    score.sapid, " + 
					"    concat(s.firstName,' ',s.lastName) as name, " + 
					"    s.emailId, " + 
					"    s.mobile, " + 
					"    b.name as batchName, " + 
					"    score.testId," + 
					"    score.attempt, " + 
					"    score.attemptStatus, " + 
					"    DATE_FORMAT(score.testStartedOn, '%d %b %Y %r') AS testStartedOn, " + 
					"    score.remainingTime, " + 
					"    DATE_FORMAT(score.testEndedOn, '%d %b %Y %r') AS testEndedOn, " + 
					"    score.score, " + 
					"    score.testQuestions, " + 
					"    score.noOfQuestionsAttempted, " + 
					"    score.showResult, " + 
					"	 pss.subject "+
					" FROM " + 
					"    exam.upgrad_test test " + 
					"        INNER JOIN " + 
					"    exam.upgrad_student_assesmentscore score ON test.id = score.testId " +
					"		LEFT JOIN " + 
					"	exam.upgrad_student_assementsdetails details ON details.sapid = score.sapid  AND details.testId = score.testId "+
					" 		INNER JOIN " + 
					"	exam.students s ON s.sapid = score.sapid"+
					"    	INNER JOIN " + 
					"	acads.upgrad_sessionplan_module usm ON usm.id = test.referenceId " + 
					"		INNER JOIN " + 
					"	acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = usm.sessionPlanId " + 
					"		INNER JOIN " + 
					"	lti.student_subject_config ssc ON ssc.id = map.timeboundId " + 
					"       INNER JOIN " + 
					"	exam.batch b ON b.id = ssc.batchId "+
					"	INNER JOIN exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " +
					" WHERE " + 
					"    test.id = ?" + 
					" GROUP BY score.sapid , score.testId , score.attempt";
			assignmentDetailsList =  (ArrayList<UpgradAssessmentExamBean>)jdbcTemplate.query(sql,new Object[] { testId,testId, testId, testId, testId, testId, testId},new BeanPropertyRowMapper<UpgradAssessmentExamBean>(UpgradAssessmentExamBean.class));

		
		return assignmentDetailsList;
		
	}
	
	//Get count of expected student for a Test.
	public Integer getExpectedStudentsForTest(Long testId) throws Exception{
		StringBuilder GET_COUNT_SQL = null;
		Integer count = 0;
		
		//Create StringBuilder Object
		GET_COUNT_SQL = new StringBuilder();
		
		//Prepare SQL Query
		GET_COUNT_SQL.append("SELECT count(*) FROM exam.upgrad_test test ") 
				.append("INNER JOIN acads.upgrad_sessionplan_module usm ON usm.id = test.referenceId ") 
				.append("INNER JOIN acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = usm.sessionPlanId ")
				.append("INNER JOIN lti.timebound_user_mapping umap ON umap.timebound_subject_config_id = map.timeboundId ")
				.append("WHERE test.id  = ? and umap.userId not like '777777%' ");
		
		//Execute jdbcTemplate queryForObject method for testId
		count = jdbcTemplate.queryForObject(GET_COUNT_SQL.toString(), Integer.class,testId);
		
		//return count of expected student for Test
		return count;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<UpgradAssessmentExamBean> getStudentAssessmentDetails(Long testid, String sapid){
		ArrayList<UpgradAssessmentExamBean> studentAssessmentDetailsList = new ArrayList<UpgradAssessmentExamBean>();
		
			String sql = "SELECT  " + 
					"    q.question_type as questionTypeId, d.* " + 
					"FROM " + 
					"    exam.upgrad_student_assementsdetails d " + 
					"        INNER JOIN " + 
					"    exam.upgrad_test_questions q ON d.questionNo = q.questionNo " + 
					"WHERE " + 
					"    d.sapid = ? " + 
					"        AND d.testId = ? " + 
					"GROUP BY q.questionNo";
			studentAssessmentDetailsList =  (ArrayList<UpgradAssessmentExamBean>)jdbcTemplate.query(sql,new Object[] { sapid, testid },new BeanPropertyRowMapper<UpgradAssessmentExamBean>(UpgradAssessmentExamBean.class));
			return studentAssessmentDetailsList;
		
	}
	
	@Transactional(readOnly = true)
	public UpgradAssessmentExamBean getIsCorrectMarksForSingleAndTF(Integer questionNo, Integer studentAnswer) {
		UpgradAssessmentExamBean upgradAssessmentBean = new UpgradAssessmentExamBean() ;
		String sql = "SELECT " + 
				"    IF(op.isCorrect = 'Y', q.marks, 0) AS marksObtained, op.optionData AS studentAnswer " + 
				"FROM " + 
				"    exam.upgrad_test_question_options op " + 
				"        INNER JOIN " + 
				"    exam.upgrad_test_questions q ON q.questionNo = op.questionNo " + 
				"WHERE " + 
				"    op.optionId = ? " + 
				"        AND op.questionNo = ?";
		upgradAssessmentBean = (UpgradAssessmentExamBean) jdbcTemplate.queryForObject(sql, new Object[] {studentAnswer, questionNo},new BeanPropertyRowMapper( UpgradAssessmentExamBean.class));
		
		return upgradAssessmentBean;
		
	}
	
	@Transactional(readOnly = true)
	public Integer getCorrectCountForMulti(Integer questionNo) {
		Integer count = 0 ;
		String sql = "SELECT count(*)  from exam.upgrad_test_question_options where questionNo = ? and isCorrect = 'Y' ";
		count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {questionNo},Integer.class);
		return count;
	}
	
	@Transactional(readOnly = true)
	public Integer getStudentAnswerCountForMulti(Long testId, Integer questionNo, String  sapId) {
		Integer count = 0 ;
		String sql = "SELECT " + 
				"    count(*) " + 
				"FROM " + 
				"    exam.upgrad_student_assementsdetails " + 
				"WHERE " + 
				"    testId = ? AND questionNo = ?" + 
				"        AND sapid = ?"  ;
		count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId, questionNo, sapId},Integer.class);
		return count;
	}
	
	@Transactional(readOnly = true)
	public Integer getIsCorrectCountForMulti(Long testId, Integer questionNo, String sapId ) {
		Integer count = 0 ;
		String sql = "SELECT " + 
				"   count(*) " + 
				"FROM " + 
				"    exam.upgrad_student_assementsdetails d " + 
				"        INNER JOIN " + 
				"    exam.upgrad_test_question_options op ON d.questionNo = op.questionNo " + 
				"WHERE " + 
				"     d.testId = ? " + 
				"        AND op.questionNo  = ? " + 
				"        AND d.sapid = ? " + 
				"        AND d.studentAnswer = op.optionId " + 
				"        AND op.isCorrect = 'Y'";
		count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId, questionNo, sapId},Integer.class);
		return count;
	}
	
	@Transactional(readOnly = true)
	public String getMarksForCorrectMultiAnswer(Integer questionNo, Long testId) {
		String marks = "0" ;
		String sql = "SELECT marks from exam.upgrad_test_questions where questionNo = ? AND testId = ?" ;
		marks = (String) jdbcTemplate.queryForObject(sql, new Object[] { questionNo, testId},String.class);
		return marks;
	}
	
	@Transactional(readOnly = true)
	public List<String> getOptionDataForMulti(Long testId, Integer questionNo, String sapId){
		List<String> optionData;
		String sql = "SELECT  " + 
				"    op.optionData " + 
				"FROM " + 
				"    exam.upgrad_student_assementsdetails d " + 
				"        INNER JOIN " + 
				"    exam.upgrad_test_question_options op ON d.questionNo = op.questionNo " + 
				"WHERE " + 
				"		d.testId = ? " + 
				"        AND d.sapid = ? " + 
				"        AND d.questionNo = ? " + 
				"        AND d.studentAnswer = op.optionId";
		optionData =jdbcTemplate.queryForList(sql, new Object[] {testId, sapId, questionNo}, String.class);
		return optionData;
	}
//	public ArrayList<UpgradAssessmentBean> getStudentAssessmentDetailsForBeforeNormalize(Long testid){
//		ArrayList<UpgradAssessmentBean> studentAssessmentDetailsList = new ArrayList<UpgradAssessmentBean>();
//		
//		
//		String sql = "select * from exam.upgrad_student_assementsdetails d where  d.testId = ?  ";
//		studentAssessmentDetailsList =  (ArrayList<UpgradAssessmentBean>)jdbcTemplate.query(sql,new Object[] { testid },new BeanPropertyRowMapper<UpgradAssessmentBean>(UpgradAssessmentBean.class));
//		return studentAssessmentDetailsList;
//		
//	}

	@Transactional(readOnly = true)
	public ArrayList<TestExamBean> FindBySubjectNameForTest(Integer examYear, String examMonth,Integer acadYear,
			 String acadMonth, Integer pssId) {
		ArrayList<TestExamBean> testBean = new ArrayList<TestExamBean>();
		try {
			String sql = "SELECT " + 
					"    ut.id AS testId," + 
					"    ut.testName " + 
					" FROM " + 
					"    exam.upgrad_test ut " + 
					"        INNER JOIN " + 
					"    acads.upgrad_sessionplan_module usm ON ut.referenceId = usm.id " + 
					"        INNER JOIN " + 
					"    acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = usm.sessionPlanId " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = map.timeboundId " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
					" WHERE " + 
					" ssc.examYear = ? AND ssc.examMonth =? AND ssc.acadYear =? AND ssc.acadMonth =? AND "+
					"    pss.id = ?";
			testBean  =  (ArrayList<TestExamBean>) jdbcTemplate.query(sql, new Object[] {
					examYear,
					examMonth,
					acadYear,
					acadMonth,
					pssId }, new BeanPropertyRowMapper<TestExamBean>(TestExamBean.class));	
		}catch(Exception e) {
			
		}	
		return testBean;
	}
	
	

	@Transactional(readOnly = false)
	  public void updateUpgradAssesmentScore(UpgradAssessmentExamBean upgradBean, Integer score) {
		  String sql = " UPDATE `exam`.`upgrad_student_assesmentscore` " + 
		  		" SET  " + 
		  		"    `score` = ?" + 
		  		" WHERE " + 
		  		"    	 `sapid`  = ? " + 
		  		"    AND `testId` = ? " ;
		  
		  jdbcTemplate.update(sql, new Object[] { 
				  score,
				  upgradBean.getSapid(),
				  upgradBean.getTestId()
				  });
		  
	  }
	  
	@Transactional(readOnly = true)
	  public Integer getMarksForValidationInUpdateMarksObtained(UpgradAssessmentExamBean upgradBean) {
		  Integer marks = 0 ;
		  String sql ="SELECT marks FROM exam.upgrad_test_questions WHERE testId = ? AND questionNo = ?" ;
		  marks = jdbcTemplate.queryForObject(sql, new Object[] {
				  upgradBean.getTestId(),
				  upgradBean.getQuestionNo()
		  }, Integer.class);
		  return marks;
	  }
	  
	@Transactional(readOnly = false)
	  public void updateMarksObtained(UpgradAssessmentExamBean upgradBean){
		 
		  String sql = "UPDATE `exam`.`upgrad_student_assementsdetails` "
		  		+ " SET beforeNormalizeScore = marksObtained,"
		  		+ "  `marksObtained` = ?"
		  		+ " WHERE `sapid` = ?"
		  		+ " AND `testId` = ?"
		  		+ " AND `questionNo` = ?";
		  
			  jdbcTemplate.update(sql, new Object[] {
					  upgradBean.getMarksObtained(), 
					  upgradBean.getSapid(),
					  upgradBean.getTestId(),
					  upgradBean.getQuestionNo()
					  });	
		  
	  }
	  
	@Transactional(readOnly = true)
	  public List<TestExamBean> getAllMBAXTestForLiveSetting(){
		  List<TestExamBean> testBean = new ArrayList<TestExamBean>();
		  
		  String sql = " SELECT  " + 
		  		"    test.id AS testId, " + 
		  		"    test.referenceId, " + 
		  		"    test.testName, " + 
		  		"    batch.examYear AS year, " + 
		  		"    batch.examMonth AS month, " + 
		  		"    batch.acadYear, " + 
		  		"    batch.acadMonth, " + 
		  		"    c_type.name AS consumerType, " + 
		  		"    p_structure.program_structure, " + 
		  		"    program.name AS program, " + 
		  		"    test.applicableType, " + 
		  		"    pss.subject, " + 
		  		"    batch.name AS name, " + 
		  		"    upgrad_module.topic AS referenceBatchOrModuleName, " + 
		  		"    test.startDate, " + 
		  		"    test.endDate, " + 
		  		"    test.showResultsToStudents " + 
		  		"FROM " + 
		  		"    exam.upgrad_test test " + 
		  		"        INNER JOIN " + 
		  		"    acads.upgrad_sessionplan_module upgrad_module ON test.referenceId = upgrad_module.id " + 
		  		"        INNER JOIN " + 
		  		"    acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = upgrad_module.sessionPlanId " + 
		  		"        INNER JOIN " + 
		  		"    lti.student_subject_config ssc ON ssc.id = map.timeboundId " + 
		  		"        INNER JOIN " + 
		  		"    exam.batch batch ON batch.id = ssc.batchId " + 
		  		"        INNER JOIN " + 
		  		"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
		  		"        INNER JOIN " + 
		  		"    exam.consumer_program_structure consumer_structure ON consumer_structure.id = pss.consumerProgramStructureId " + 
		  		"        INNER JOIN " + 
		  		"    exam.program program ON program.id = consumer_structure.programId " + 
		  		"        INNER JOIN " + 
		  		"    exam.program_structure p_structure ON p_structure.id = consumer_structure.programStructureId " + 
		  		"        INNER JOIN " + 
		  		"    exam.consumer_type c_type ON c_type.id = consumer_structure.consumerTypeId " + 
		  		"WHERE " + 
		  		"    consumer_structure.id in (119,126,162) ";
		  testBean  =  (List<TestExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<TestExamBean>(TestExamBean.class));
		 
		  
		  return testBean;
		  
	  }
	  
	@Transactional(readOnly = false)
	  public void updateShowResultForMBAXTest(Long testId, Integer referenceId){
		  
		  String sql = "UPDATE `exam`.`upgrad_test` SET `showResultsToStudents`='Y' WHERE `id`= ? and`referenceId`= ?";
		  String sql2 = "UPDATE `exam`.`upgrad_student_assesmentscore` SET `showResult`='Y' WHERE `testId`= ? ";
		  
			  jdbcTemplate.update(sql, new Object[] {testId, referenceId });
			  jdbcTemplate.update(sql2, new Object[] {testId});
	  }
	  
	@Transactional(readOnly = false)
	  public void updateHideResultForMBAXTest(Long testId, Integer referenceId){
		  
		  String sql = "UPDATE `exam`.`upgrad_test` SET `showResultsToStudents`='N' WHERE `id`= ? and`referenceId`= ?";
		  String sql2 ="UPDATE `exam`.`upgrad_student_assesmentscore` SET `showResult`='N' WHERE `testId`= ? ";
		  
			  jdbcTemplate.update(sql, new Object[] {testId, referenceId });
			  jdbcTemplate.update(sql2, new Object[] {testId});

	  }
	 
	@Transactional(readOnly = true)
	public List<UpgradAssessmentExamBean> getSubjectDetails( Integer batchId){
		List<UpgradAssessmentExamBean> subjectList = new ArrayList<>();
		try {
			String sql = " SELECT  " + 
					"	pss.id as subjectId, " + 
					"   pss.subject " + 
					" FROM " + 
					"    exam.program_sem_subject pss " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id " + 
					" WHERE " + 
					"    ssc.batchId = ? ";
			subjectList =  jdbcTemplate.query(sql, new Object[] {  batchId}, new BeanPropertyRowMapper<UpgradAssessmentExamBean>(UpgradAssessmentExamBean.class));
		}catch(Exception e) {
			
		}
		 
		
		return subjectList;
	}

	@Transactional(readOnly = false)
	  public void updateMarksProcessedFlagNForAllByTestId(Long testId) {
		  String sql = "UPDATE exam.mbax_marks marks " + 
		  				"SET " + 
		  				" marks.processed = 'N' " + 
		  				" WHERE " + 
		  				" marks.timebound_id = " + 
		  				" (SELECT ustm.timeboundId " + 
		  				" FROM " + 
		  				" exam.upgrad_test ut " + 
		  				" INNER JOIN " + 
		  				" acads.upgrad_sessionplan_module usm ON ut.referenceId = usm.id " + 
		  				" INNER JOIN " + 
		  				" acads.upgrad_sessionplanid_timeboundid_mapping ustm ON usm.sessionPlanId = ustm.sessionPlanId " + 
		  				" WHERE " + 
		  				" ut.id = ?) " + 
		  				" AND marks.sapid IN " + 
		  				" (SELECT usa.sapid " + 
		  				" FROM " + 
		  				" exam.upgrad_test test " + 
		  				" INNER JOIN " + 
		  				" exam.upgrad_student_assesmentscore usa ON test.id = usa.testId " + 
		  				" WHERE " + 
		  				" test.id = ?)";
		  String sql2 = "UPDATE exam.mbax_marks_history marks " + 
	  				"SET " + 
	  				" marks.processed = 'N' " + 
	  				" WHERE " + 
	  				" marks.timebound_id = " + 
	  				" (SELECT ustm.timeboundId " + 
	  				" FROM " + 
	  				" exam.upgrad_test ut " + 
	  				" INNER JOIN " + 
	  				" acads.upgrad_sessionplan_module usm ON ut.referenceId = usm.id " + 
	  				" INNER JOIN " + 
	  				" acads.upgrad_sessionplanid_timeboundid_mapping ustm ON usm.sessionPlanId = ustm.sessionPlanId " + 
	  				" WHERE " + 
	  				" ut.id = ?) " + 
	  				" AND marks.sapid IN " + 
	  				" (SELECT usa.sapid " + 
	  				" FROM " + 
	  				" exam.upgrad_test test " + 
	  				" INNER JOIN " + 
	  				" exam.upgrad_student_assesmentscore usa ON test.id = usa.testId " + 
	  				" WHERE " + 
	  				" test.id = ?)";
		  
		   jdbcTemplate.update(sql, new Object[] {testId, testId }) ;
		   jdbcTemplate.update(sql2, new Object[] {testId, testId }) ;
	  }
	  
	@Transactional(readOnly = false)
	  public void updatePassFailIsResultLiveFlagNForAllByTestId(Long testId) {
		  String sql = " UPDATE `exam`.`mbax_passfail`  " + 
		  		"SET  " + 
		  		"    `isResultLive` = 'N' " + 
		  		"WHERE " + 
		  		"    `timeboundId` = (SELECT  " + 
		  		"            ustm.timeboundId " + 
		  		"        FROM " + 
		  		"            exam.upgrad_test ut " + 
		  		"                INNER JOIN " + 
		  		"            acads.upgrad_sessionplan_module usm ON ut.referenceId = usm.id " + 
		  		"                INNER JOIN " + 
		  		"            acads.upgrad_sessionplanid_timeboundid_mapping ustm ON usm.sessionPlanId = ustm.sessionPlanId " + 
		  		"        WHERE " + 
		  		"            ut.id = ? ) " + 
		  		"        AND `sapid` IN (SELECT  " + 
		  		"            usa.sapid " + 
		  		"        FROM " + 
		  		"            exam.upgrad_test test " + 
		  		"                INNER JOIN " + 
		  		"            exam.upgrad_student_assesmentscore usa ON test.id = usa.testId " + 
		  		"        WHERE " + 
		  		"            test.id = ?) ";
		  jdbcTemplate.update(sql, new Object[] {testId, testId }) ;
	  }

	@Transactional(readOnly = true)
	public List<UpgradAssessmentExamBean> getBatchDetails(Integer examYear, String examMonth, Integer acadYear, String acadMonth){
		
		String sql = "SELECT id as batchId, name as batchName FROM exam.batch where id in  (SELECT  batchId FROM lti.student_subject_config ssc "
				+ "where ssc.acadYear = ? and ssc.acadMonth = ? AND ssc.examYear = ? AND ssc.examMonth = ? group by batchId) AND consumerProgramStructureId in (119,126,162)";
		 List<UpgradAssessmentExamBean> batchList = jdbcTemplate.query(sql, new Object[] { acadYear, acadMonth, examYear, examMonth}, new BeanPropertyRowMapper<UpgradAssessmentExamBean>(UpgradAssessmentExamBean.class));

		return batchList;
	}
	


	@Transactional(readOnly = true)	
	public ResponseBean getMbaXPassFailData() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ResponseBean bean = new ResponseBean();
		ArrayList<MBAXPassFailBean> mbaXPassFailList = new ArrayList<MBAXPassFailBean>();
		String sql = "SELECT  " + 
				"    stm.sessionPlanId AS sessionPlanId , " + 
				"    mp.timeboundId, " + 
				"    mp.sapid, " + 
				"    mp.schedule_id, " + 
				"    mp.attempt AS attempt, " +
				"    mp.sem AS sem, " +
				"    mp.iaScore AS  iaScore, " + 
				"    mp.teeScore AS teeScore, " +				
				"    mp.status, " + 
				"    mp.graceMarks AS graceMarks, " + 
				"    mp.isPass, " + 
				"    COALESCE(mp.failReason, 'NA') AS failReason, " + 
				"    mp.isResultLive, " + 
				"    mp.grade, " + 
				"    mp.points AS points, "
				+ "	mp.prgm_sem_subj_id AS courseId " + 
				"FROM " + 
				"    exam.mbax_passfail mp " + 
				"        INNER JOIN " + 
				"    acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.timeboundId = mp.timeboundId"; 			
		try {
			mbaXPassFailList = (ArrayList<MBAXPassFailBean>) 
					jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(MBAXPassFailBean.class));
			bean.setStatus("success");
			bean.setMbaXPassFailData(mbaXPassFailList);
			bean.setCode(200);
			bean.setMessage("");
		}catch(Exception e) {
			
			bean.setStatus("failed");
			bean.setMessage(e.getMessage());
			bean.setCode(422);

		}
		return bean;
		
	}
	
	@Transactional(readOnly = false)
	public List<EmbaPassFailBean> getMbaXPassFailByTimeBoundId(String commaSeparatedTimeBoundIds) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql ="SELECT  " + 
				"      pf.timeboundId, pf.sapid, pf.schedule_id, pf.attempt, pf.sem, pf.isPass, pf.failReason,"
				+ " pf.isResultLive, pf.createdBy,pf.createdDate, pf.lastModifiedBy, pf.lastModifiedDate, pf.grade, "
				+ "pf.points,pf.status,CONCAT (s.firstName ,' ',s.lastName) as studentName,ssc.batchId as batch_id,pss.subject,s.program , pss.id AS pssId, "
				+ " CASE " + 
				"    When pf.iaScore THEN pf.iaScore ELSE 0	     " + 
				"END AS iaScore, " + 
				"	CASE " + 
				"    When pf.teeScore THEN pf.teeScore ELSE 0	     " + 
				"END AS teeScore, " + 
				"	CASE " + 
				"    When pf.graceMarks THEN pf.graceMarks ELSE 0	     " + 
				"END AS graceMarks, " + 
				"	 CAST((COALESCE(iaScore, 0) + COALESCE(teeScore, 0) + COALESCE(graceMarks, 0)) AS UNSIGNED) AS total "+ 
				"FROM " + 
				"    exam.mbax_passfail pf "
				+ "INNER JOIN " + 
				"    exam.students s ON s.sapid = pf.sapid " + 
				"  INNER JOIN " + 
				" 	 lti.student_subject_config ssc ON ssc.id = pf.timeBoundId "
				+ "INNER JOIN  " + 
				"    exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				"WHERE " + 
				"    pf.timeBoundId in ("+commaSeparatedTimeBoundIds+")";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = false)
	public void updateMbaXGradeAndPointsList(final List<EmbaPassFailBean> mbaXPassFailList, final String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mbax_passfail` "
				+ " SET `grade` = ?, "
				+ " `points` = ?, "
				+ " `lastModifiedBy` = ? "
				+ " WHERE `timeboundId` = ? AND `sapid` = ?";
		

		try {
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					
					ps.setString(1, mbaXPassFailList.get(i).getGrade());
					ps.setString(2, mbaXPassFailList.get(i).getPoints());
					ps.setString(3, userId);
					ps.setString(4, mbaXPassFailList.get(i).getTimeboundId());
					ps.setString(5, mbaXPassFailList.get(i).getSapid());
				}

				@Override
				public int getBatchSize() {
					return mbaXPassFailList.size();
				}
			  });
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			

		}
		
	}

	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getMBAXAssessmentListByTimeBoundId(int time_bound_id){
		ArrayList<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT ea.* FROM exam.mbax_exams_assessments ea LEFT JOIN  exam.mbax_assessment_timebound_id ati on ati.assessments_id =ea.id where ati.timebound_id = ?;";
			assessmentList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql, new Object[] {time_bound_id},
					new BeanPropertyRowMapper(MettlResponseBean.class));
			return assessmentList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public List<MettlResponseBean> getMBAXScheduleListByAssessmentId(int assessment_id, int timebound_id){
		List<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT * FROM exam.mbax_exams_schedule where assessments_id = ? and timebound_id=?;";
			assessmentList = jdbcTemplate.query(sql, new Object[] {assessment_id, timebound_id},
					new BeanPropertyRowMapper<MettlResponseBean>(MettlResponseBean.class));
			return assessmentList;
		} catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<StudentSubjectConfigExamBean> getMBAXSubjectByBatchId(int batch_id){
		ArrayList<StudentSubjectConfigExamBean> subjectList = new ArrayList<StudentSubjectConfigExamBean>();
		try {
			String sql = "SELECT s_s_c.*,p_s_s.subject FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s where s_s_c.prgm_sem_subj_id = p_s_s.id and ( p_s_s.consumerProgramStructureId = 119 OR p_s_s.consumerProgramStructureId = 126 OR p_s_s.consumerProgramStructureId = 162 ) and s_s_c.batchId = ?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			subjectList = (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, new Object[] {batch_id},
					new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
			return subjectList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return subjectList;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamsAssessmentsBean> getExamAssessments(){
		ArrayList<ExamsAssessmentsBean> examsAssessmentsListBean = new ArrayList<ExamsAssessmentsBean>();
		try {
			String sql = /*"SELECT e_a.*, e_s.schedule_id, e_s.schedule_name, e_s.exam_start_date_time, "
					+ "e_s.exam_end_date_time, p_s_s.subject, s_s_c.batchId as batch_id FROM exam.exams_assessments e_a, "
					+ "exam.exams_schedule e_s, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s, exam.assessment_timebound_id e_t_i  "
					+ "WHERE e_s.assessments_id = e_a.id and s_s_c.id = e_t_i.timebound_id and "
					+ "s_s_c.prgm_sem_subj_id = p_s_s.id and e_t_i.assessments_id = e_a.id"*/
					"SELECT e_t_i.assessments_id, e_s.schedule_id, e_s.exam_start_date_time, " + 
					"		e_s.exam_end_date_time, e_s.schedule_name, s_s_c.batchId as batch_id, e_b.name as batch_name, " + 
					"		p_s_s.subject, e_a.customAssessmentName,  e_a.name " + 
					"FROM exam.mbax_assessment_timebound_id e_t_i " + 
					"LEFT JOIN " + 
					"exam.mbax_exams_schedule e_s on e_s.assessments_id = e_t_i.assessments_id and e_s.timebound_id = e_t_i.timebound_id  " + 
					"LEFT JOIN  " + 
					"lti.student_subject_config s_s_c on e_t_i.timebound_id = s_s_c.id " + 
					"LEFT JOIN  " + 
					"exam.program_sem_subject p_s_s on s_s_c.prgm_sem_subj_id = p_s_s.id   " + 
					"LEFT join " + 
					"exam.mbax_exams_assessments e_a on  e_a.id = e_t_i.assessments_id "+
					"LEFT join " + 
					"exam.batch e_b on  e_b.id = s_s_c.batchId ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			examsAssessmentsListBean = (ArrayList<ExamsAssessmentsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(ExamsAssessmentsBean.class));
			return examsAssessmentsListBean;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return examsAssessmentsListBean;
		}
	} 
	
	@Transactional(readOnly = true)
	public String getTimeBoundId(String subject,String batch_id){
		try {
			String sql = "SELECT s_s_c.id FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s "
					+ "where s_s_c.prgm_sem_subj_id = p_s_s.id and batchId = ? and p_s_s.subject=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate.queryForObject(sql, new Object[] {batch_id,subject},String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public int checkIfAssessmentTimeBoundMappingExists (ExamsAssessmentsBean examsAssessmentsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.mbax_assessment_timebound_id where timebound_id = ? and assessments_id = ?";
		int exists = 0;		
		try {
			exists = (int) jdbcTemplate.queryForObject(sql, new Object[] {examsAssessmentsBean.getTimebound_id(),examsAssessmentsBean.getAssessments_id()},Integer.class);
			if (exists > 0) {
				return 1;
			} else {
				return 0;
			}			
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	@Transactional(readOnly = true)
	public int checkIfAssessmentExists (ExamsAssessmentsBean examsAssessmentsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.mbax_exams_assessments where id = ? ";
		int exists = 0;		
		try {
			exists = (int) jdbcTemplate.queryForObject(sql, new Object[] {examsAssessmentsBean.getAssessments_id()},Integer.class);
			if (exists > 0) {
				return 1;
			} else {
				return 0;
			}			
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	public String insertIntoExamScheduleAndAssessmentTimebound(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.mbax_exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,`exam_end_date_time`,`createdBy`,`lastModifiedBy`,`max_score`) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score());

			String sql3 = "insert into exam.mbax_assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Schedule already exist in portal";
			}
			return e.getMessage();
		}

	}
	
	@Transactional(readOnly = false)
	public int getExamScheduleId(ExamsAssessmentsBean examsAssessmentsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select id from exam.mbax_exams_schedule where timebound_id=? and exam_start_date_time=? and exam_end_date_time=? and schedule_id=? ";
		int id = 0;		
		try {
			ExamsAssessmentsBean bean = jdbcTemplate.queryForObject(sql, 
				new Object[] {
					examsAssessmentsBean.getTimebound_id(),examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),examsAssessmentsBean.getSchedule_id()
				},
				new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class)
			);
			id=Integer.parseInt(bean.getId());
			
			if (id > 0) {
				return id;
			} else {
				return 0;
			}			
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	
	@Transactional(readOnly = false)
	public String updateScheduleIdInTimeTable(ExamsAssessmentsBean examsAssessmentsBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql4 = " "
					+ " UPDATE `exam`.`mba_x_time_table` `tt` "
					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
					+ " ON "
					        + " `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` "
					    + " AND `ssc`.`examYear` = `tt`.`examYear` "
					    + " AND `ssc`.`examMonth` = `tt`.`examMonth` "
					+ " SET `tt`.`scheduleId` = ? , `tt`.`lastModifiedBy` = ?, `tt`.`lastModifiedOn`=sysdate() "
					+ " WHERE `tt`.`examStartDateTime`=? and `tt`.`examEndDateTime`=? and `ssc`.`id` = ? ";

			jdbcTemplate.update(sql4,examsAssessmentsBean.getId(),
					examsAssessmentsBean.getLastModifiedBy(),
					
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getTimebound_id());

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return e.getMessage();
		}
	}
	
	public String insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.mbax_exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,`exam_end_date_time`,`createdBy`,`lastModifiedBy`,`max_score`) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score());

			//String sql2 = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?)";
			String sql2 = "insert into exam.mbax_exams_assessments(`id`,`name`,`customAssessmentName`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?)";

			jdbcTemplate.update(sql2,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getName(),
					examsAssessmentsBean.getCustomAssessmentName(),
					//examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			String sql3 = "insert into exam.mbax_assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		} catch (Exception e) {
			// TODO: handle exception
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Assessment already exist in portal";
			}
			return e.getMessage();
		}
	}
	
	// below method use for view Student IA details
	@Transactional(readOnly = true)
	public UpgradAssessmentExamBean getStudentIATestDetails(String sapid, Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		UpgradAssessmentExamBean upgradAssessmentBeanList = new UpgradAssessmentExamBean();
		String sql1 = "SELECT * FROM "
				+ " exam.upgrad_test ut " + 
				"	INNER JOIN  " + 
				"	exam.upgrad_student_assesmentscore usa ON usa.testId = ut.testId " + 
				 " where usa.testId = ? and usa.sapid = ? ";
		
		String sql2 = " SELECT  " + 
				"    semSub.subject, " +
				"  	config.acadYear, " + 
				"   config.acadMonth "+
				"FROM " + 
				"    exam.upgrad_test test " + 
				"        INNER JOIN " + 
				"    acads.upgrad_sessionplan_module module ON test.referenceId = module.sessionModuleNo " + 
				"        INNER JOIN " + 
				"    acads.upgrad_sessionplanid_timeboundid_mapping map ON map.sessionPlanId = module.sessionPlanId " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config config ON config.id = map.timeboundId " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject semSub ON semSub.id = config.prgm_sem_subj_id " + 
				"WHERE " + 
				"    test.testId = ? ";
		
		upgradAssessmentBeanList = (UpgradAssessmentExamBean) jdbcTemplate.queryForObject(sql1, new Object[] {
				testId, sapid}, new BeanPropertyRowMapper(UpgradAssessmentExamBean.class));

		UpgradAssessmentExamBean upgradAssessmentBean = (UpgradAssessmentExamBean)jdbcTemplate.queryForObject(sql2, new Object[] {
				testId }, new BeanPropertyRowMapper(UpgradAssessmentExamBean.class));
		
		upgradAssessmentBeanList.setSubject(upgradAssessmentBean.getSubject());
		upgradAssessmentBeanList.setAcadMonth(upgradAssessmentBean.getAcadMonth());
		upgradAssessmentBeanList.setAcadYear(upgradAssessmentBean.getAcadYear());

		return upgradAssessmentBeanList;
	}
	
	@Transactional(readOnly = true)
	public List<UpgradTestQuestionExamBean> getTestQuestionDetails(Long testId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<UpgradTestQuestionExamBean> upgradTestQuestionList = new ArrayList<>();
		String sql = " SELECT  * FROM exam.upgrad_test_questions WHERE testId = ? ";

		upgradTestQuestionList = (List<UpgradTestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {
				testId}, new BeanPropertyRowMapper<UpgradTestQuestionExamBean>(UpgradTestQuestionExamBean.class));
		
		return upgradTestQuestionList;
	}
	
	@Transactional(readOnly = true)
	public List<UpgradTestQuestionOptionExamBean> getOptionList( Long questionNo){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<UpgradTestQuestionOptionExamBean> upgradTestQuestionOptionList = new ArrayList<>();
		String sql = "SELECT " + 
				"    * " +
				" FROM " + 
				"    exam.upgrad_test_question_options " +
				" WHERE " + 
				"    questionNo = ?   ORDER BY optionId" ;
		upgradTestQuestionOptionList = (List<UpgradTestQuestionOptionExamBean>)jdbcTemplate.query(sql, new Object[] {
				questionNo}, new BeanPropertyRowMapper<UpgradTestQuestionOptionExamBean>(UpgradTestQuestionOptionExamBean.class));
		return upgradTestQuestionOptionList;
	}
	
	@Transactional(readOnly = true)
	public UpgradQuestionAnsweredDetailsBean getStudentAnsDetails(Long testId, String sapid, Long questionNo){
		jdbcTemplate = new JdbcTemplate(dataSource);
		UpgradQuestionAnsweredDetailsBean upgradQuestionAnsweredDetailsBean = new UpgradQuestionAnsweredDetailsBean();
		String sql = " SELECT " +
			"    * " +
			" FROM " + 
			"	exam.upgrad_student_assementsdetails  " +
			" WHERE " + 
			"    sapid = ? " + 
			"    AND testId = ? " + 
			"    AND questionNo = ? " ;
		
		upgradQuestionAnsweredDetailsBean = (UpgradQuestionAnsweredDetailsBean)jdbcTemplate.queryForObject(sql, new Object[] {
				sapid, testId, questionNo}, new BeanPropertyRowMapper(UpgradQuestionAnsweredDetailsBean.class));

		return upgradQuestionAnsweredDetailsBean;
	}
	
	@Transactional(readOnly = true)
	public List<UpgradQuestionAnsweredDetailsBean> getStudentAnswerDetailsList(Long testId, String sapid, Long questionNo){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<UpgradQuestionAnsweredDetailsBean> studentAnsDetailsList = new ArrayList<>();
		String sql = " SELECT * FROM exam.upgrad_student_assementsdetails "+
				 " WHERE sapid = ? AND testId = ? AND questionNo = ? ";
		studentAnsDetailsList = (List<UpgradQuestionAnsweredDetailsBean>) jdbcTemplate.query(sql, new Object[] {
				 sapid, testId, questionNo}, new BeanPropertyRowMapper<UpgradQuestionAnsweredDetailsBean>(UpgradQuestionAnsweredDetailsBean.class));
		return studentAnsDetailsList;
	}
	
	/**
	 * To update Exam Start Date Time and Exam End Date Time.
	 * @param examAssmtBean - bean having exam_start_date_time,exam_end_date_time and assessments_id as data
	 * @return String - As success message
	 * @throws Exception If any exception occurs while executing logic.
	 */
	@Transactional(readOnly = false)
	public String updateExamAssessmentEndTime(ExamsAssessmentsBean examAssmtBean) throws Exception{
		String updateDateTime=null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Frame SQL query
		updateDateTime="update exam.mbax_exams_schedule set exam_end_date_time=(addtime(?, ?)) where schedule_id=?";
		
		//Execute update() method with SQL query and assessment_id, extendEndTime and end date as argument
		jdbcTemplate.update(updateDateTime, examAssmtBean.getExam_end_date_time(),examAssmtBean.getExtendExamEndTime(),examAssmtBean.getId());
		
		//return success message as string
		return "success";
	}//updateExamAssessmentEndTime()
	
	public int checkIfScheduleExist(String assessments_id, String examStartDateTime, String examEndDateTime) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select count(*) from exam.mbax_exams_schedule " 
					+ "where assessments_id = ? and exam_start_date_time= ? and exam_end_date_time= ? ";
			return (int)jdbcTemplate.queryForObject(sql, new Object []{assessments_id,examStartDateTime,examEndDateTime}, Integer.class);
		}
	public ExamsAssessmentsBean getExistingScheduleDetails(String assessments_id, String examStartDateTime,
			String examEndDateTime) {
		ExamsAssessmentsBean bean = new ExamsAssessmentsBean();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select assessments_id,schedule_id, schedule_name, schedule_accessKey, schedule_accessUrl," 
				+ "schedule_status, exam_start_date_time, exam_end_date_time,reporting_start_date_time,reporting_finish_date_time, createdBy, lastModifiedBy, max_score from exam.mbax_exams_schedule "
				+ "where assessments_id = ? and exam_start_date_time= ? and exam_end_date_time= ? limit 1";
		bean=jdbcTemplate.queryForObject(sql, new Object[] {assessments_id,examStartDateTime,examEndDateTime}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));
		return bean;
	}
	public String insertIntoExamScheduleAndAssessmentTimeboundWithReportTime(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
System.out.println("examsAssessmentsBean "+examsAssessmentsBean);
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.mbax_exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,`exam_end_date_time`,`createdBy`,`lastModifiedBy`,`max_score`,`reporting_start_date_time`,`reporting_finish_date_time`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score(),
					examsAssessmentsBean.getReporting_start_date_time(),
					examsAssessmentsBean.getReporting_finish_date_time()
					);

			String sql3 = "insert into exam.mbax_assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Schedule already exist in portal";
			}
			return e.getMessage();
		}

	}
	
	public String insertIntoExamScheduleAndAssessmentAndAssessmentTimeboundWithReport(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.mbax_exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,`exam_end_date_time`,`createdBy`,`lastModifiedBy`,`max_score`,`reporting_start_date_time`,`reporting_finish_date_time`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score(),
					examsAssessmentsBean.getReporting_start_date_time(),
					examsAssessmentsBean.getReporting_finish_date_time()
					);
			//String sql2 = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?)";
			String sql2 = "insert into exam.mbax_exams_assessments(`id`,`name`,`customAssessmentName`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?)";

			jdbcTemplate.update(sql2,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getName(),
					examsAssessmentsBean.getCustomAssessmentName(),
					//examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			String sql3 = "insert into exam.mbax_assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Assessment already exist in portal";
			}
			return e.getMessage();
		}
	}
	public List<String> getNotRegisterStudentOfExcel(String timebound_id,
			String schedule_accessKey, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select sapid "
				+ "from exam.mbax_exams_scheduleinfo_mettl where timebound_id=? and acessKey=? "
				+ "and sapid in ("+sapid+")";
		return jdbcTemplate.queryForList(sql, new Object[] {timebound_id,schedule_accessKey},String.class);
	}
	
	public List<String> getRegisterStudentForScheduleAndTimeBound(String timebound_id,
			String scheduleId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select sapid from exam.mbax_exams_scheduleinfo_mettl where timebound_id=? and schedule_id=?";
		return jdbcTemplate.queryForList(sql, new Object[] {timebound_id,scheduleId},String.class);	
	}
	
	public List<String> checkTimeBoundMapping(String timebound_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select userId from lti.timebound_user_mapping where timebound_subject_config_id = ?";
		return jdbcTemplate.queryForList(sql, new Object[] {timebound_id},String.class);	
	}
	
	
	
}
