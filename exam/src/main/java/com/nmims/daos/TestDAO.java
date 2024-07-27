package com.nmims.daos;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ExamAnalyticsObject;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.IAReportsBean;
import com.nmims.beans.LogFileAnalysisBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.MailBean;
import com.nmims.beans.PageVisitsBean;
import com.nmims.beans.PostExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.SectionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionCaseStudyBean;
import com.nmims.beans.TestQuestionConfigBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.TestTypeBean;
import com.nmims.beans.TestWeightageBean;
import com.nmims.dto.DissertationResultProcessingDTO;

public class TestDAO  extends BaseDAO{
	
private static final Logger logger = LoggerFactory.getLogger(TestDAO.class);
	
	@Autowired
	ApplicationContext act;

	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${CURRENT_MBAWX_ACAD_MONTH}" )
	private String current_mbawx_acad_month;

	@Value( "${CURRENT_MBAWX_ACAD_YEAR}" )
	private String current_mbawx_acad_year;
	
	private static final String BOD_ACTIVE_TRUE = "Y";

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	public List<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT program FROM exam.programs order by program asc";
		try {
			List<String> programList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return programList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
		
	}
	public List<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = "select distinct subject from exam.program_subject where prgmStructApplicable = 'Jul2014' or "
//				+ " prgmStructApplicable = 'Jul2009' or prgmStructApplicable = 'Jul2013' or prgmStructApplicable = 'Jul2017' or prgmStructApplicable = 'Jan2018' order by subject";
		String sql = "SELECT distinct subject from exam.program_subject where active = 'Y'";
		try {
			List<String> subjectList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return subjectList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	public List<FacultyExamBean> getFaculties() {
	jdbcTemplate = new JdbcTemplate(dataSource);
	String sql = "SELECT * FROM acads.faculty where active = 'Y' order by firstname, lastname asc ";
	
	List<FacultyExamBean> facultyList = (List<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
	return facultyList;
	
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getTestFacultyIdFullNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT t.facultyId, CONCAT(f.firstName, ' ', f.lastName) " + 
					"FROM exam.test t " + 
					"INNER JOIN acads.faculty f " + 
					"	ON t.facultyId = f.facultyId " + 
					"GROUP BY t.facultyId";
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> { 
										    return resultSetMapper(rs, String.class, String.class);
										});
	}
	
	@Transactional(readOnly = true)
	public HashMap<Integer, String> getTestConsumerTypeIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT c.id, c.`name` " + 
					"FROM exam.test t " + 
					"INNER JOIN exam.consumer_type c " + 
					"	ON t.consumerTypeIdFormValue = c.id " + 
					"GROUP BY t.consumerTypeIdFormValue";
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> { 
										    return resultSetMapper(rs, Integer.class, String.class);
										});
	}
	
	@Transactional(readOnly = true)
	public HashMap<Integer, String> getTestProgramIdCodeMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT p.id, p.`code` " + 
					"FROM exam.test t " + 
					"INNER JOIN exam.program p " + 
					"	ON t.programIdFormValue = p.id " + 
					"GROUP BY t.programIdFormValue";
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> { 
										    return resultSetMapper(rs, Integer.class, String.class);
										});
	}
	
	@Transactional(readOnly = true)
	public HashMap<Integer, String> getProgramStructureMapByProgram(int programId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT ps.id, ps.program_structure " + 
					"FROM exam.consumer_program_structure cps " + 
					"INNER JOIN exam.program_structure ps " + 
					"	ON cps.programStructureId = ps.id " + 
					"WHERE cps.programId = ?";
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> { 
											return resultSetMapper(rs, Integer.class, String.class);
										}, programId);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getTestNameList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT testName " + 
					"FROM exam.test " + 
					"ORDER BY testName";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(sql, String.class);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getTestCpsIdList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT id  " + 
					"FROM exam.consumer_program_structure AS cps " + 
					"INNER JOIN (	SELECT consumerTypeIdFormValue, programIdFormValue, programStructureIdFormValue " + 
					"				FROM exam.test " + 
					"				GROUP BY consumerTypeIdFormValue, programIdFormValue, programStructureIdFormValue	) AS t " + 
					"ON cps.consumerTypeId = t.consumerTypeIdFormValue " + 
					"	AND cps.programId = t.programIdFormValue " + 
					"   AND cps.programStructureId = t.programStructureIdFormValue";
		
		return (ArrayList<Integer>) jdbcTemplate.queryForList(sql, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsByCpsIdList(ArrayList<Integer> cpsIdList) {
		SqlParameterSource paramSource = new MapSqlParameterSource("ids", cpsIdList);
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String sql= "SELECT `subject` " + 
					"FROM exam.program_sem_subject " + 
					"WHERE consumerProgramStructureId IN (:ids)";
		
		return (ArrayList<String>) namedParameterJdbcTemplate.queryForList(sql, paramSource, String.class);
	}
	
	@Transactional(readOnly = true)
	public HashMap<Integer, String> getTestQuestionTypeMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT `id`, `type` " + 
					"FROM `exam`.`test_question_type`";
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> resultSetMapper(rs, Integer.class, String.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SectionBean> getTestSectionQuestionsConfigList(long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT qc.`type`, qc.minNoOfQuestions AS sectionQnCount, qc.questionMarks, s.sectionName " + 
					"FROM exam.test_questions_configuration qc " + 
					"LEFT JOIN exam.test_sections s " + 
					"	ON qc.sectionId = s.id " + 
					"WHERE qc.testId = ?";
		
		return (ArrayList<SectionBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SectionBean.class), testId);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SectionBean> getTestSectionQuestionsUploadedList(long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT tq.`type`, COUNT(*) AS sectionQnCount, tq.marks AS questionMarks, ts.sectionName  " + 
					"FROM exam.test_questions tq  " + 
					"LEFT JOIN exam.test_sections ts  " + 
					"	ON tq.sectionId = ts.id  " + 
					"WHERE tq.testId = ? " + 
					"GROUP BY tq.`type`, tq.marks";
		
		return (ArrayList<SectionBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SectionBean.class), testId);
	}
	
	private <K, V> HashMap<K, V> resultSetMapper(ResultSet rs, Class<K> keyClass, Class<V> valueClass) throws SQLException {
		HashMap<K, V> resultMap= new HashMap<>();
        while(rs.next()) {
        	resultMap.put(keyClass.cast(rs.getObject(1)), valueClass.cast(rs.getObject(2)));
        }
        
        return resultMap;
	}
	
	public List<StudentsTestDetailsExamBean> getTestResultsById(Long id) {
		List<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = "select  t.subject,s.sapid,s.firstName,s.lastName,t.testName, t.startDate, ts.score, t.maxScore  from exam.students s, exam.test_student_testdetails ts, exam.test t where s.sapid = ts.sapid and ts.testId = "+id+" and ts.testId = t.id "; 
		 try {
			 testDetailsList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
		 }
		 catch(Exception e){
			 
		 }
		 return testDetailsList;
		
		 
		}
	public List<StudentsTestDetailsExamBean> getTestResultsByListId(String ids, String authorizedCenterCodes) {
		List<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		//String sql = "select s.sapid,s.firstName,s.lastName,t.testName, t.startDate, ts.score, t.maxScore ,  t.subject  from exam.students s, exam.test_student_testdetails ts, exam.test t where s.sapid = ts.sapid and ts.testId in("+ids+") and ts.testId = t.id "; 
		String sql="SELECT "+ 
			    "t.testName,"+
			    "s.sapid,"+
			    "s.firstName,"+
			    "s.lastName,"+
			    "t.testName,"+
			    "t.startDate,"+
			    "COALESCE(ts.score, 'AB') AS scoreInString,"+
			    "t.maxScore,"+
			    "t.subject "+
			"FROM "+
			    "exam.test t "+
			        "inner JOIN "+
				"exam.test_testid_configuration_mapping tcm ON tcm.testId = t.id "+
			        "inner JOIN "+
			    "acads.sessionplan_module spm ON spm.id = tcm.referenceId "+
			        "inner JOIN "+
			    "acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = spm.sessionPlanId "+
			        "inner join "+
			    "lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = stm.timeboundId "+
			        "inner JOIN "+
			    "exam.students s ON tum.userId = s.sapid "+
			        "LEFT JOIN "+
			    "exam.test_student_testdetails ts ON ts.testId = t.id AND ts.sapId = s.sapId "+
			"WHERE "+
			    " t.id IN ("+ids+") "+
			        " AND tum.userId <> 77777777777 "+
			        " AND (tum.userId LIKE '77%' "+
			        " OR tum.userId LIKE '79%') ";
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " AND s.centerCode in (" + authorizedCenterCodes + ") ";
		}
	
		
		try {
		testDetailsList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
		 }
		 catch(Exception e){
			 
			
		 }
		 return testDetailsList;
		
		}

	public List<StudentsTestDetailsExamBean> getTestAttendenceResultsById(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentsTestDetailsExamBean>  testAttendenceDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
	
		
		String sql = "select  s.sapid,s.firstName,s.lastName,t.testName, t.startDate," + 
				"	COALESCE(tst.score,0) as score," + 							
				"	case " + 
				"		when tst.score >=0 then 'Y'" + 
				"		when tst.score is null then 'N'" + 
				"        else 'NA'" + 
				"    end as isTestGiven" + 
				" from exam.students s" + 
				" inner join" + 
				" lti.timebound_user_mapping tum on s.sapid = tum.userId" + 
				" inner join " + 
				" lti.student_subject_config ssc  on ssc.id = tum.timebound_subject_config_id" + 
				" inner join " + 
				" acads.sessionplanid_timeboundid_mapping stm   on ssc.id = stm.timeboundId" + 
				" inner join " + 
				" acads.sessionplan sp   on sp.id = stm.sessionPlanId" + 
				" inner join " + 
				" acads.sessionplan_module m   on sp.id = m.sessionPlanId" + 
				" inner join " + 
				" exam.test t   on m.id = t.referenceId" + 
				" left join " + 
				" exam.test_student_testdetails tst  on t.id = tst.testId " + 
				" and s.sapid = tst.sapid " + 
				" where " + 
				" t.id in ("+id+")" + 
				" and s.sapid <> 77777777777" + 
				" order by t.testName";
		


	try {
		testAttendenceDetailsList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
	}
	catch(Exception e) {
		
	}		
		return testAttendenceDetailsList;
		}
	
	
	public HashMap<String,TestTypeBean> getTestTypesMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.test_question_type ";
				try {
			List<TestTypeBean> testTypeList = (List<TestTypeBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeBean.class));
			HashMap<String,TestTypeBean> testTypeMap = new HashMap<>();
			for(TestTypeBean bean : testTypeList) {
				testTypeMap.put(bean.getType(), bean);
			}
			
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	

	public HashMap<Long,TestTypeBean> getTypeIdNTypeMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.test_question_type ";
				try {
			List<TestTypeBean> testTypeList = (List<TestTypeBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeBean.class));
			HashMap<Long,TestTypeBean> testTypeMap = new HashMap<>();
			for(TestTypeBean bean : testTypeList) {
				testTypeMap.put(bean.getId(), bean);
			}
			
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	
	//CRUD for tests Start
	public long saveTest(final TestExamBean test) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO exam.test "
				+ " (year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate,  subject,"
				+ "  maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, randomQuestion,"
				+ " testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert, maxScore, duration, passScore, testType,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
				+ " consumerTypeIdFormValue, programStructureIdFormValue, programIdFormValue, "
				+ " applicableType, referenceId,"
				+ " proctoringEnabled,showCalculator  "
				+ ") "
        		+ " VALUES(?,?,?,?,?,?,?,?,?" //9 ?
        		+ "		   ,?,?,?,?,?,?" // 6 ? 
        		+ "		   ,?,?,?,?,?,?,?,?" // 8 ?
        		+ "		   ,?,sysdate(),?,sysdate()"
        		+ "		   ,?,?,?"
        		+ "		   ,?,?"
        		+ "		   ,?,?"
        		+ ") ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, test.getYear());
			        statement.setString(2, test.getMonth());
			        statement.setInt(3, test.getAcadYear());
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

			        statement.setString(31, test.getProctoringEnabled()); 
			        statement.setString(32, test.getShowCalculator());
			        return statement;
			    }
			}, holder);
			final long primaryKey = holder.getKey().longValue();

			TestExamBean testToshow = getTestById(primaryKey) ;
			

			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

		}
		
		/* id, year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate, program,
		 *  subject, courseId, maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, 
		 *  randomQuestion, testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert,
		 *   maxScore, duration, passScore, testType, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		 * */
	public void insertMCQInPost(final TestExamBean test) throws ParseException{
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		

		//get timebound id to insert in post table
		final Long timeBoundId = getTimeBoundIdByTestId(test.getId());
		


		//insert in post table
		final String sql2 = "INSERT INTO lti.post " 
				+ " (userId, subject_config_id, role, type, content,referenceId, acadYear, acadMonth, "
				+ "examYear,examMonth,scheduledDate, scheduleFlag,createdBy,createdDate,"
				+ "subject,startDate,endDate,duration,"
				+ " session_plan_module_id "
				+ ") "
        		+ " VALUES(?,?,?,?,?,?,?,?" //8 ?

        		+ "		   ,?,?,DATE_SUB(?, INTERVAL 1 HOUR),?,?,sysdate()" // 6 ?    
     

        		+ "		   ,?,?,?,?"
        		+ "        ,?" //  ?
        		+ ") ";
		
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, test.getFacultyId());
			        statement.setLong(2, timeBoundId);
			        statement.setString(3, "Faculty");
			        statement.setString(4, "MCQ");  
			        
			        statement.setString(5, test.getTestName());  
			        statement.setLong(6, test.getId());  
			        statement.setString(7, test.getYear()); 
			        statement.setString(8,test.getAcadMonth()); 
			        statement.setString(9, test.getYear()); 
			        statement.setString(10, test.getMonth());  
			        
			        statement.setString(11,test.getStartDate()); 
			        

			        statement.setString(12, "Y"); 

			        statement.setString(13, test.getCreatedBy()); 
			        statement.setString(14, test.getSubject()); 
			       
			        statement.setString(15, test.getStartDate()); 
			        statement.setString(16, test.getEndDate()); 
			        statement.setLong(17, test.getDuration());  
			        statement.setInt(18, test.getReferenceId()); 
			        
			        return statement;
			    }
			}, holder);
		} catch (Exception e) {
			
		}
	}
		public boolean updateTest(TestExamBean test) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test set "
							+ " year=?, "
							+ " month=?, "
							+ "testName=?, " 
							+ "testDescription=?, " 
							+ "startDate=?, "
							+ "endDate=?, " 
							+ "maxQuestnToShow=?, "
							+ "showResultsToStudents=?, "
							+ " active=?, "
							+ " facultyId=?, "
							+ "maxAttempt=?, " 
							+ "randomQuestion=?, " 
							+ "testQuestionWeightageReq=?, "
							+ "allowAfterEndDate=?, "
							+ "sendEmailAlert=?, " 
							+ "sendSmsAlert=?, "
							+ "maxScore=?, "
							+ "duration=?, " 
							+ "passScore=?, "
							+ "testType=?, " 
							+ "lastModifiedBy=?,"
							+ " lastModifiedDate = sysdate(),"
							+ " consumerTypeIdFormValue=?, programStructureIdFormValue=?, programIdFormValue=?,"
							+ " applicableType=?, referenceId=?, "
//							adding missing acad yr/month for update
							+ " acadMonth=?, acadYear=? ,testType=?,"
							+ " proctoringEnabled=?, showCalculator=? "  
							
								
							+ "where id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {test.getYear(),test.getMonth(),test.getTestName(),
													  test.getTestDescription(),test.getStartDate(),test.getEndDate(),
													  test.getMaxQuestnToShow(),
													  test.getShowResultsToStudents(),test.getActive(),test.getFacultyId(), 
													  test.getMaxAttempt(),test.getRandomQuestion(),test.getTestQuestionWeightageReq(),
													  test.getAllowAfterEndDate(),test.getSendEmailAlert(),test.getSendSmsAlert(),
													  test.getMaxScore(),test.getDuration(),test.getPassScore(),test.getTestType(),test.getLastModifiedBy(),
													  test.getConsumerTypeIdFormValue(),test.getProgramStructureIdFormValue(),test.getProgramIdFormValue(),
													  test.getApplicableType(),test.getReferenceId(),
													  test.getAcadMonth(),test.getAcadYear(),test.getTestType(),
													  test.getProctoringEnabled(),test.getShowCalculator(),
													  test.getId()  
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		public List<TestExamBean> getAllTests(){//get all live tests
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			String sql="SELECT t.* FROM exam.test t  , exam.test_live_settings l " + 
					"where t.referenceId =l.referenceId and t.year=l.examYear and t.month=l.examMonth and " + 
					"t.acadYear=l.acadYear  and t.acadMonth=l.acadMonth order by t.id desc ";
			try {  
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}

		public List<TestExamBean> getTestsBySearchBean(TestExamBean searchBean){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=new ArrayList<>();

			final String consumerTypeId = searchBean.getConsumerTypeId();
			final String programStructureId = searchBean.getProgramStructureId();
			final String programId= searchBean.getProgramId();
			final String year = searchBean.getYear();
			final String month = searchBean.getMonth();
			String sql="SELECT " + 
					"    count(t.id) as countOfProgramsApplicableTo ,t.* " + 
					"FROM " + 
					"    exam.test t," + 
					"    exam.test_testid_consumerprogramstructureid_mapping tcm," + 
					"    exam.consumer_program_structure cps " + 
					"WHERE " + 
					"    t.id = tcm.testId " + 
					"        AND tcm.consumerProgramStructureId = cps.id "
					+ "		 AND t.year = "+year+" "  
					+ "		 AND t.month = '"+month+"' " + 
					"        AND cps.consumerTypeId IN ("+consumerTypeId+") " + 
					"        AND cps.programStructureId IN ("+programStructureId+") " + 
					"        AND cps.programId IN ("+programId+") ";
			
			if(!StringUtils.isBlank(searchBean.getSubject())) {
				sql += " AND  t.subject = '"+searchBean.getSubject()+"' ";
			}
			
					sql += " GROUP BY t.id " + 
					"ORDER BY t.id DESC";
			try {

				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
				 
			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		public List<TestExamBean> getTestsForFaculty(String facultyId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList = new ArrayList<>(); 
			
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			FacultyExamBean faculty = sDao.isFaculty( facultyId );
			
			if( "Insofe Faculty".equals( faculty.getTitle() ) ) {

				/*
				 * fetching all the test that are allocated to the faculty at test level
				 * */
				List<TestExamBean> testAssignedForFaculty = getTestsAssignedForFaculty( facultyId );
				testsList.addAll( testAssignedForFaculty );

				/*
				 * fetching all the test that are allocated to the faculty at answer level for evaluation
				 * */
				List<TestExamBean> testAssignedForEvaluation = getTestsAssignedForEvaluationToFaculty( facultyId );

				if( testAssignedForFaculty.size() > 0 ) {

					/*
					 * updated to streams instead of iterating and checking
					 * checking the testAssignedForEvaluation list, if there are any test that are already present in 
					 * testAssignedForFaculty, if not only then we return all of the beans as list and then 
					 * add that list of not present beans to the final list
					 * */
					List<TestExamBean> testNotPresentInAssignedList = testAssignedForEvaluation.stream()
							.filter(evaluation -> testAssignedForFaculty.stream()
							.noneMatch(assigned -> assigned.getId().equals(evaluation.getId())))
		            		.collect(Collectors.toList());

					testsList.addAll( testNotPresentInAssignedList );
					
				}else 
					testsList.addAll( testAssignedForEvaluation );
				
			}else { 

				testsList.addAll( getTestsAssignedForEvaluationToFaculty( facultyId ) );
				return testsList;
				
			}
				
			return testsList;
			
		}

		private Integer getNoOfAnswersToEvaluateByFacultyId(String facultyId, Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			Integer count=0;
			String sql=" select count(*) from exam.test_students_answers a "
					+ " inner join exam.test_questions q "
					+ " on a.questionId = q.id "
					+ " inner join exam.test t "
					+ " on a.testId = t.id  "
					+ " where  a.facultyId=? and a.isChecked = 0 and q.type in (4,8) and a.testId = ? "
					+ " AND a.sapid NOT LIKE '777777%' "
					+ " Order By a.id ";
			try {
				count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {facultyId,testId}, Integer.class);
				return count;
			} catch (Exception e) {
				
			}
			return count;
		}
		public List<TestExamBean> getTestsForFaculty(Set<Long> testIds){
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = 1;
			
			StringBuilder sb = new StringBuilder();
			List<TestExamBean> testsList=null;
			try {
				for(Long id : testIds) {
					if(count==1) {
						sb.append(id+"");
					}else {
						sb.append(","+id);
					}
				}
				String ids=sb.toString();

				String sql="select * from exam.test where id in ("+ids+") Order By id ";
				
				testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		public TestExamBean getTestById(Long id){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestExamBean test=null;
			String sql="select * from exam.test where id=?";
			try {
				 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestExamBean.class));

				 test = getReference_Batch_Or_Module_Name(test);
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+"IN getTestById got id "+id+", Error :"+e.getMessage());
			}
			return test;
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
				module = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {referenceId,referenceId}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				//
			}
			return module;
		}
		
		public String getModuleTopicById(long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT topic " + 
						"FROM acads.sessionplan_module " + 
						"WHERE id = ?";
		
			return jdbcTemplate.queryForObject(sql, String.class, id);
		}

		private TestExamBean getBatchByBatchId(Integer referenceId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestExamBean batch=new TestExamBean();
			String sql="select * from exam.batch where id=?";
			try {
				batch = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {referenceId}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return batch;
		}
		
		@Transactional(readOnly = true)
		public String getBatchNameById(long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT `name` " + 
						"FROM exam.batch " + 
						"WHERE id = ?";
		
			return jdbcTemplate.queryForObject(sql, String.class, id);
		}
		
		@Transactional(readOnly = true)
		public Integer getTimeboundIdByModuleId(long moduleId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT stm.timeboundId " + 
						"FROM acads.sessionplanid_timeboundid_mapping stm " + 
						"INNER JOIN acads.sessionplan_module sm " + 
						"	ON sm.sessionPlanId = stm.sessionPlanId " + 
						"WHERE sm.id = ? ";
		
			return jdbcTemplate.queryForObject(sql, Integer.class, moduleId);
		}
		
		@Transactional(readOnly = true)
		public String getBatchNameByTimeboundId(long timeboundId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT b.`name` " + 
						"FROM exam.batch b " + 
						"INNER JOIN lti.student_subject_config ssc " + 
						"	ON b.id = ssc.batchId " + 
						"WHERE ssc.id = ? ";
		
			return jdbcTemplate.queryForObject(sql, String.class, timeboundId);
		}

		public TestExamBean getTestByIdAndAttempts(Long id,String sapId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestExamBean test=null;
			String sql="select * from exam.test t " + 
					"   LEFT JOIN exam.test_student_testdetails s " + 
					"	on t.id = s.testId " + 
					"	where s.sapid=? and t.id=?";
			try {
				 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {sapId,id}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return test;
		}
		
		public int deleteTest(Long id) {
			int madeLiveCount = checkTestMadeLive(id);							//Checks if test is made live
			if(madeLiveCount > 0)
				throw new IllegalArgumentException("Test cannot be deleted as the test is made live.");
			
			int studentAttemptedCount = getTestStudentAttemptedCount(id);		//Checks if test was conducted
			if(studentAttemptedCount > 0)
				throw new IllegalArgumentException("Test cannot be deleted as " + studentAttemptedCount + " students have appeared for the test.");
			
			int resultsLiveCount = checkTestResultsLive(id);					//Checks if test results are live
			if(resultsLiveCount > 0)
				throw new IllegalArgumentException("Test cannot be deleted as the test results are live.");
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from exam.test where id=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id});



		} catch (Exception e) {
			
			return -1;
		}
		PostExamBean post  = findPostByReferenceId(id);
		if(post != null ) {
			 deleteTestPost(post.getPost_id());
			 //deleteFromRedis(post);
			 refreshRedis(post);
		}
		return row;
		}
		public PostExamBean findPostByReferenceId(Long id) {  
			jdbcTemplate = new JdbcTemplate(dataSource);  
			String sql = "Select * from lti.post where referenceId =? and type='MCQ'";          
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<PostExamBean>(PostExamBean.class));  
		}
		public void deleteTestPost(int id) {  
			String sql1="delete from lti.post where post_id=? ";
			try {
				 jdbcTemplate.update(sql1, new Object[] {id});  
			} catch (Exception e) {
			}
		}
/*		public String deleteFromRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
		  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";

				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				
				return "Error IN rest call got "+e.getMessage();
			}
		}*/
		public String refreshRedis(PostExamBean posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
		  	     String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";

				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<PostExamBean> entity = new HttpEntity<PostExamBean>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				
				return "Error IN rest call got "+e.getMessage();
			}
		}
		public List<TestExamBean> getAllTestsForStudent(List<String> allsubjects){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			String subjectCommaSeparated = "''";
			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			String sql="select t.* from exam.test t " + 
					" where  "
					+ " "
					+ " subject in ("+subjectCommaSeparated+") "
					+ "Order By id desc ";
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		
	//CRUD for tests End
		

		//CRUD for Students test details start
		//id, sapid, testId, attempt, active, testStartedOn, testEndedOn, testCompleted, score, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public long saveStudentsTestDetails(final StudentsTestDetailsExamBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			final String sql = "INSERT INTO exam.test_student_testdetails "
					+ " ( sapid, testId, attempt, active, testStartedOn,  testCompleted, score, testQuestions, showResult,"
					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
					+ " remainingTime) "
	        		+ " VALUES(?,?,?,?,sysdate(),?,?,?,?,"
	        		+ "		   ?,sysdate(),?,sysdate(),"
	        		+ "		   ?) ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setString(1, bean.getSapid());
				        statement.setLong(2, bean.getTestId());
				        statement.setInt(3, bean.getAttempt());
				        statement.setString(4, bean.getActive()); 
				        statement.setString(5, bean.getTestCompleted()); 
				        statement.setDouble(6, bean.getScore()); 
				        statement.setString(7, bean.getTestQuestions()); 
				        statement.setString(8, bean.getShowResult()); 
				        statement.setString(9, bean.getCreatedBy());
				        statement.setString(10, bean.getLastModifiedBy());
				        statement.setInt(11, bean.getRemainingTime());     
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {
				//

				logger.info("\n"+SERVER+": "+" IN saveStudentsTestDetails  got testId : "+bean.getTestId()+" sapId:  "+bean.getSapid()+", Error :  "+e.getMessage());
				
				return 0;
			}

			}
		
		//id, sapid, testId, attempt, active, testStartedOn, testEndedOn, testCompleted, score, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public boolean updateStudentsTestDetails(StudentsTestDetailsExamBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ " "
							+ "	testEndedOn=sysdate(),"
							+ "	testCompleted=?, "
							+ "	score=?, "
							+ "	testQuestions=?, "
							+ " lastModifiedBy=?, "
							+ " lastModifiedDate = sysdate(),"
							+ " testEndedStatus = ?   "    
								
							+ " where testId=? and sapid=? and attempt=? ";
			try {
				jdbcTemplate.update(sql,new Object[] {
													  bean.getTestCompleted(),
													  bean.getScore(),
													  bean.getTestQuestions(),
													  bean.getLastModifiedBy(),
													  bean.getTestEndedStatus(),
													  
													  bean.getTestId(),bean.getSapid(),bean.getAttempt()
									});

				return true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/updateStudentsTestDetails";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+bean.getTestId() +
						",questions: " + bean.getTestQuestions() + ",score: "+bean.getScore()+",attempt: "+ bean.getAttempt() + 
						",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,bean.getSapid());

//				
			}
			return false;
		}
		
		//id, sapid, testId, attempt, active, testStartedOn, testEndedOn, testCompleted, score, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public boolean updateStudentsTestDetailsAfterShowResults(StudentsTestDetailsExamBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ " "
							+ "	score=?, "
							+ " lastModifiedBy=?, "
							+ " lastModifiedDate = sysdate()  "
//							+ " resultDeclaredOn = sysdate()  "    
								
							+ " where testId=? and sapid=? and attempt=? ";
			try {
				jdbcTemplate.update(sql,new Object[] {
													  bean.getScore(),
													  bean.getLastModifiedBy(),
													  
													  bean.getTestId(),bean.getSapid(),bean.getAttempt()
									});

				return true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/updateStudentsTestDetailsAfterShowResults";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+bean.getTestId() +
						",questions: " + bean.getTestQuestions() + ",score: "+bean.getScore()+",attempt: "+ bean.getAttempt() + 
						",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,bean.getSapid());

//				
			}
			return false;
		}
		
			//id, sapid, testId, attempt, active, testStartedOn, remainingTime, testEndedOn, testCompleted, score, testQuestions, showResult, createdBy, createdDate, lastModifiedBy, lastModifiedDate	
			public boolean updateStudentsTestDetailsRemainingTime(int remaingTime,Long id) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="update exam.test_student_testdetails set "
									+ " "
									+ "	remainingTime=?,"
									+ " lastModifiedDate = sysdate()  "    
										
									+ " where id=?  ";
					try {
						jdbcTemplate.update(sql,new Object[] {remaingTime,id});

						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//
						logger.info("\n"+SERVER+": "+" IN updateStudentsTestDetailsRemainingTime remaingTime:  "+remaingTime+" id : "+id);
						
					}
					return false;
				}
		
			//id, sapid, testId, attempt, active, testStartedOn, remainingTime, testEndedOn, testCompleted, score, testQuestions, showResult, createdBy, createdDate, lastModifiedBy, lastModifiedDate	
			public boolean updateStudentsTestDetailsNoOfQuestionsAttempted(Long testId, String sapId, int attempt,Long questionId) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="update exam.test_student_testdetails set "
									+ "	noOfQuestionsAttempted = noOfQuestionsAttempted + 1,"
									+ " lastModifiedDate = sysdate()  "    
									+ " where testId=? and sapid=? and attempt = ? and "+ questionId +" not in (select id from exam.test_questions where isSubQuestion = 1) ";
					try {


						jdbcTemplate.update(sql,new Object[] {testId,sapId,attempt});

						return true;						 
					} catch (Exception e) {
						// TODO Auto-generated catch block

						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						String apiCalled = "testDao/updateStudentsTestDetailsNoOfQuestionsAttempted";
						String stackTrace = "apiCalled="+ apiCalled + ",data= questionid: " +questionId + 
								",testId: "+ testId + ",attempt: " + attempt  + ",errors=" + errors.toString();
						setObjectAndCallLogError(stackTrace,sapId);

//						
					}
					return false;
				}
			//currentQuestion
			//id, sapid, testId, attempt, active, testStartedOn, remainingTime, testEndedOn, testCompleted, score, testQuestions, showResult, createdBy, createdDate, lastModifiedBy, lastModifiedDate	
			public boolean updateStudentsTestDetailsCurrentQuestion(Long currentQuestion,Long testId, String sapId, int attempt) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="update exam.test_student_testdetails set "
									+ "	currentQuestion = ?,"
									+ " lastModifiedDate = sysdate()  "    
									+ " where testId=? and sapid=? and attempt = ?  ";
					try {
						jdbcTemplate.update(sql,new Object[] {currentQuestion,testId,sapId,attempt});

						return true;

					} catch (Exception e) {
						// TODO Auto-generated catch block

						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						String apiCalled = "testDao/updateStudentsTestDetailsCurrentQuestion";
						String stackTrace = "apiCalled="+ apiCalled + ",data= currentQuestion: "+currentQuestion + 
								", testId: "+ testId + ", attempt: "+ attempt  +",errors=" + errors.toString();
						setObjectAndCallLogError(stackTrace,sapId);

//						
					}
					return false;
				}
		
		public boolean updateStudentsTestsResultStatusByTestId(String resultStatus,Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ " showResult = ?" 
								
							+ " where testId=?";
			try {
				jdbcTemplate.update(sql,new Object[] {resultStatus,testId});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		
		
		public List<StudentsTestDetailsExamBean> getStudentsTestDetailsBySapid(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=null;
			String sql="select * from exam.test_student_testdetails where sapid=?  Order By id ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}

		public List<StudentsTestDetailsExamBean> getStudentsTestDetailsByTestId(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByTestId=new ArrayList<>();
			String sql="select * from exam.test_student_testdetails where testId=?  Order By id ";
			try {
				testsByTestId = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			} catch (Exception e) {
				
			}
			return testsByTestId;
		}
		
		public List<StudentsTestDetailsExamBean> getAttemptsDetailsBySapidNTestId(String sapid,Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=null;
			String sql="select * from exam.test_student_testdetails where sapid=? and testId = ?  Order By id ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+" IN getAttemptsDetailsBySapidNTestId   got testId : "+testId+" sapId : "+sapid+", Error :  "+e.getMessage());
			}
			return testsByStudent;
		}
		public HashMap<Long, StudentsTestDetailsExamBean> getStudentsTestDetailsAndTestIdMapBySapid(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=null;
			HashMap<Long, StudentsTestDetailsExamBean> testIdAndTestByStudentsMap = new HashMap<>(); 
			String sql="select * from exam.test_student_testdetails where sapid=?  Order By id asc ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
				
				for(StudentsTestDetailsExamBean test : testsByStudent) {
					testIdAndTestByStudentsMap.put(test.getTestId(), test);
				}
				
			} catch (Exception e) {
				
			}
			return testIdAndTestByStudentsMap;
		}
		public StudentsTestDetailsExamBean getStudentsTestDetailsBySapidAndTestId(String sapid, Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			StudentsTestDetailsExamBean testByStudent= new StudentsTestDetailsExamBean();
			String sql="select * from exam.test_student_testdetails where sapid=? and testId=? Order By id desc limit 1 ";//get latest entry of students attempts
			try {
				testByStudent = (StudentsTestDetailsExamBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
				
			} catch (Exception e) {
			/*
			 * StringWriter errors = new StringWriter(); e.printStackTrace(new
			 * PrintWriter(errors)); String apiCalled =
			 * "testDao/getStudentsTestDetailsBySapidAndTestId"; String stackTrace =
			 * "apiCalled="+ apiCalled + ",data= testId: " + testId + ",errors=" +
			 * errors.toString(); setObjectAndCallLogError(stackTrace,sapid); //

			 */		
				logger.info("\n"+SERVER+": "+"IN getStudentsTestDetailsBySapidAndTestId got sapid  "+sapid+" testId: "+testId+" Error: "+e.getMessage());
					
			}
			return testByStudent;
		}
		public int deleteStudentsTestDetailsById(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_student_testdetails where id=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});

			} catch (Exception e) {
				
			}
			return row;
			}
		//CRUD for Students test details end
		
		//CRUD for tests questions start
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ArrayList<String> batchUpdateTestQuestion(final ArrayList<TestQuestionExamBean> testQuestiontList) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			int i = 0;
			ArrayList<String> errorList = new ArrayList<>();

			for (TestQuestionExamBean bean : testQuestiontList) {
				try{
					long returnedKey = saveTestQuestion(bean);
					if(returnedKey == 0) {
						errorList.add(i+"");
					}else {
						//adding option
						List<String> optionErrorList = batchUpdateTestQuestionOption(bean.getOptionsList(),returnedKey);
						if(!optionErrorList.isEmpty()) {
							errorList.add(i+"");
						}
						
						//add entry if question is casestudy
						if(bean.getIsSubQuestion() == 1) {
							TestQuestionCaseStudyBean csd = new TestQuestionCaseStudyBean();
							csd.setQuestionId(bean.getMainQuestionId());
							csd.setSubQuestionId(returnedKey);
							long caseStudySaved = saveTestCaseStudyDetails(csd);
							if(caseStudySaved == 0) {
								errorList.add(i+"");
							}
						}
					}
				}catch(Exception e){
					
					errorList.add(i+"");
				}
				i++;
			}
			return errorList;

		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public String addSingleTestQuestion(final TestQuestionExamBean question) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			int i = 0;
			String errorMessage = "";
			List<String> optionErrorList = new ArrayList<>();
			try{
					long returnedKey = saveTestQuestion(question);
					if(returnedKey == 0) {
						errorMessage = "Error in saving question to DB. ";
						return errorMessage;
					}else { 
						errorMessage = ""+returnedKey;
						//adding option
						
						if((question.getType() != 3) && (question.getType() != 4) && (question.getType() != 8)){
							optionErrorList = batchUpdateTestQuestionOption(question.getOptionsList(),returnedKey);
						}

						if(!optionErrorList.isEmpty()) {
							for(String e : optionErrorList) {
								errorMessage = " "+e+" ";
							}
							return errorMessage;
						}
						
						//add entry if question is casestudy
						if(question.getIsSubQuestion() == 1) {
							TestQuestionCaseStudyBean csd = new TestQuestionCaseStudyBean();
							csd.setQuestionId(question.getMainQuestionId());
							csd.setSubQuestionId(returnedKey);
							long caseStudySaved = saveTestCaseStudyDetails(csd);
							if(caseStudySaved == 0) {
								errorMessage = "Error in saving question to DB. ";
								return errorMessage;
								
							}
						}
						
						//save url in other table for image and video and assignment type question
						if((question.getType() == 6) || (question.getType() == 7) || (question.getType() == 8)){
							question.setId(returnedKey);

							if(question.getType() == 8){
								String url = question.getUrl();
								url=url.replace(SERVER_PATH,"");
								question.setUrl(url);

							}
							long returnedUrlKey = saveTestQuestionAdditionalcontent(question);
							if(returnedUrlKey == 0) {
								errorMessage = "Error in saving Url of question to DB. ";
								return errorMessage;
							}
						}

					} // End else of return key == 0
				}catch(Exception e){
					
					errorMessage = "Error in saving question. ";
					return errorMessage;
					
				}
			
			return errorMessage;
		}

		
		public long saveTestQuestion(final TestQuestionExamBean testQuestion) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			
			if(testQuestion.getCopyCaseThreshold() == null) {
				testQuestion.setCopyCaseThreshold(40);
			}
			
			//id, testId, description, marks, type, chapter, option1, option2, option3, option4, option5, option6, option7, option8, correctOption, active, createdBy, createdDate, lastModifiedBy, lastModifiedDate
//			final String sql = "INSERT INTO exam.test_questions "
//					+ " (testId, description, marks, type,  "
//					+ " chapter, question, correctOption,active,isSubQuestion, "
//					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
//	        		+ " VALUES(?,?,?,?,"
//	        		+ "		   ?,?,?,'Y',?,"
//	        		+ "		   ?,sysdate(),?,sysdate()) ";
			final String sql = "INSERT INTO exam.test_questions "
					+ " (testId, description, marks, type,  "
					+ " chapter, question, correctOption,active,isSubQuestion, "
					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,uploadType, sectionId"
					+ " ,copyCaseThreshold) "
	        		+ " VALUES(?,?,?,?,"
	        		+ "		   ?,?,?,?,?,"
	        		+ "		   ?,?,?,?,?, ?"
	        		+ "		   ,?) ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setLong(1, testQuestion.getTestId());
				        statement.setString(2, testQuestion.getDescription().trim());
				        statement.setDouble(3, testQuestion.getMarks());
				        statement.setInt(4, testQuestion.getType()); 
				        statement.setString(5, testQuestion.getChapter()); 
				        statement.setString(6, testQuestion.getQuestion().trim());    
				        statement.setString(7, testQuestion.getCorrectOption());
				        statement.setString(8, testQuestion.getActive());
				        statement.setInt(9, testQuestion.getIsSubQuestion());
				        statement.setString(10, testQuestion.getCreatedBy());
				        statement.setString(11, testQuestion.getCreatedDate());
				        statement.setString(12, testQuestion.getLastModifiedBy());
				        statement.setString(13, testQuestion.getLastModifiedDate());
				        statement.setString(14, testQuestion.getUploadType());
				        statement.setInt(15, testQuestion.getSectionId());
				        statement.setInt(16, testQuestion.getCopyCaseThreshold());
				        
				        
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {
				
				return 0;
			}

			}
		

		
		public String updateTestQusetion(TestQuestionExamBean question) {
			String errorMessage ="";
			boolean updated = updateTestQuestionById(question);
			if(!updated) {
				return errorMessage+" Error in updating in DB.";
			}else {

				if(question.getOptionsList() != null) {


					try {
						for(TestQuestionOptionExamBean o : question.getOptionsList()) {
						updated=updateTestQuestionOptionById(o);
						if(!updated) {
							return errorMessage+" Error in updating option in DB.";
						}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}
				}

				if(question.getType()==3) {
					String subQuestionError="";
					

					try {
						for(TestQuestionExamBean subquestion : question.getSubQuestionsList()) {

							subQuestionError= updateTestQusetion(subquestion);

							if(!StringUtils.isBlank(subQuestionError)) {
								errorMessage = errorMessage+ subQuestionError;
								break;
							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}

				}
				//update url in other table for image and video type question
				if((question.getType() == 6) && (question.getType() == 7)){
					boolean updatedAdditionalContent = updateTestQuestionAdditionalcontent(question);
					if(!updatedAdditionalContent) {
						return errorMessage+" Error in updating additional content details in DB.";
					}
				}
			}
			
			return errorMessage;
		}
		
		/*
		 * 	id, testId, marks, type, chapter, question, description,
		 *  option1, option2, option3, option4, option5, option6, option7, option8,
		 *  correctOption, isSubQuestion, active, 
		 *  createdBy, createdDate, lastModifiedBy, lastModifiedDate
		 * copyCaseThreshold
		 * */
		public boolean updateTestQuestionById(TestQuestionExamBean testQuestion) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_questions set "
							+ " marks=?, "
							+ " chapter=?,"
							+ " question=?,"
							+ " description=?,"
							+ " isSubQuestion=?,"
							+ " lastModifiedBy=?,"
							+ " copyCaseThreshold=?,"
							+ " sectionId=?,"
							+ " lastModifiedDate=sysdate() "    
								
							+ " where id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {testQuestion.getMarks(),
													  testQuestion.getChapter(),
													  testQuestion.getQuestion().trim(),
													  testQuestion.getDescription().trim(),
													  testQuestion.getIsSubQuestion(),
													  testQuestion.getLastModifiedBy(),
													  testQuestion.getCopyCaseThreshold(),
													  testQuestion.getSectionId(),
													  
													  
													  testQuestion.getId()
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		
		public TestQuestionExamBean getTestQuestionById(Long id){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestQuestionExamBean question=null;
			String sql="select * from exam.test_questions where id=?";
			try {
				question = (TestQuestionExamBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				if(question!=null) {
					question.setOptionsList(getTestQuestionOptionByQuestionId(id));
					if(question.getType()==3) {
						question.setSubQuestionsList(getTestSubQuestions(question.getId()));
					}
				}
			} catch (Exception e) {
				
			}
			return question;
		}
		
		
		public String deleteTestQuestionById(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;
			int optionRow=0;
			int additionalContentRow=0;	
			String sql="delete from exam.test_questions where id=?";
			try {
				additionalContentRow= deleteTestQuestionAdditionalcontentByQuestionId(id);
				optionRow=deleteTestQuestionOptionByQuestionId(id);
				row = jdbcTemplate.update(sql, new Object[] {id});

			} catch (Exception e) {
				
				return "Error";
			}
			return "Deleted  "+additionalContentRow+" additionalContent , "+optionRow+" options and "+row+" rows of TEST  questionid " +id;//to change
			}
		public int deleteTestQuestionByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;
			int optionRow=0;
			String sql="delete from exam.test_questions where testId=?";
			String sqlDeleteOptions="delete from exam.test_question_options where "
					+ " questionId in ( select q.id from exam.test_questions q where q.testId=? )	";
			
			try {
				optionRow=jdbcTemplate.update(sqlDeleteOptions, new Object[] {id});
				 row = jdbcTemplate.update(sql, new Object[] {id});

			} catch (Exception e) {
				
			}
			return row;
			}
		
		public int deleteTestSubQuestionByTestId(Long testId, Long questionId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;
			int optionRow=0;
			String sql="delete from exam.test_questions where testId=?"
					+ " and isSubQuestion = 1 "
					+ " and id in ( select c.subQuestionsId from exam.test_question_casestudydetails c where c.questionId=? ) ";
			String sqlDeleteOptions="delete from exam.test_question_options where "
					+ " questionId in ( select c.subQuestionsId from exam.test_question_casestudydetails c where c.questionId=? )	";
			
			try {
				optionRow=jdbcTemplate.update(sqlDeleteOptions, new Object[] {questionId});
				 row = jdbcTemplate.update(sql, new Object[] {testId,questionId});

			} catch (Exception e) {
				
			}
			return row;
			}
		public List<TestQuestionExamBean> getTestQuestions(Long testId){
			
			//return null on error.
			List<TestQuestionExamBean> testQuestionsList=null;
			
			
			//1.get data from redis 
			TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			
			testQuestionsList = daoForRedis.findAllTestQuestionsByTestId(testId);
			
			
			//2. check if questions are present 
			if(testQuestionsList != null && testQuestionsList.size() > 0) {
				return testQuestionsList;
			}
			
			//3. if not present get question from mysql db
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select q.*,c.url as url, s.sectionName "
					+ " from exam.test_questions q " + 
					"   LEFT JOIN exam.test_question_additionalcontent c " + 
					"	on q.id=c.questionId " + 
					"   LEFT JOIN exam.test_sections s " + 
					"	on q.sectionId = s.id " + 
					"	where q.testId=? and q.isSubQuestion = 0 "
					+ " Order By q.sectionId, q.id ";
			
			try {

				testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				
				if(testQuestionsList !=null) {

				}else {

				}
				for(TestQuestionExamBean question : testQuestionsList) {

					if(question.getType() == 6 || question.getType() == 7) {

					}
					List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionId(question.getId());
					question.setOptionsList(options);
					if(question.getType()==3) {
						question.setSubQuestionsList(getTestSubQuestions(question.getId()));
					}
				}
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+" IN getTestQuestions   got testId : "+testId+" , Error :  "+e.getMessage());
				
			}
			return testQuestionsList;
		}
		
		
		public List<TestQuestionExamBean> getTestQuestionsPerAttempt(String questionIds){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionExamBean> testQuestionsList=new ArrayList<>();
			if(StringUtils.isBlank(questionIds)) {
				return testQuestionsList;
			}
			//String sql="select * from exam.test_questions where id in ("+questionIds+") and isSubQuestion = 0 Order By id ";
			String sql="select q.*,c.url as url, s.sectionName "
					+ " from exam.test_questions q " + 
					"   LEFT JOIN exam.test_question_additionalcontent c " + 
					"	on q.id = c.questionId " + 
					"   LEFT JOIN exam.test_sections s " + 
					"	on q.sectionId = s.id " + 
					"	where q.id in ("+questionIds+") and q.isSubQuestion = 0 "
					+ " Order By q.sectionId, q.id ";
			
			try {
				testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				
				if(testQuestionsList !=null) {

				}else {

				}
				for(TestQuestionExamBean question : testQuestionsList) {
					List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionId(question.getId());
					question.setOptionsList(options);
					if(question.getType()==3) {
						question.setSubQuestionsList(getTestSubQuestions(question.getId()));
					}
				}
			} catch (Exception e) {
				
			}
			return testQuestionsList;
		}
		
		public Integer getNoOfTestQuestionsByTestIdNType(Long testId,Long type){
			jdbcTemplate = new JdbcTemplate(dataSource);
			Integer count=0;
			String sql="select count(*) from exam.test_questions where testId=? and isSubQuestion = 0 and type=? Order By id ";
			try {
				count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId,type}, Integer.class);
				return count;
			} catch (Exception e) {
				
			}
			return count;
		}

		public List<Long> getQuestionTypesByTestId(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<Long> typesList=new ArrayList<>();
			String sql="select * from exam.test_questions where testId=? group by type ";
			try {
				List<TestQuestionConfigBean> temp = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
				
				for(TestQuestionConfigBean t : temp) {
					typesList.add(new Long(t.getType()));
				}
				
			} catch (Exception e) {
				
			}
			return typesList;
		}
		
		
		
		public List<TestQuestionExamBean> getTestSubQuestions(Long questionId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionExamBean> testQuestionsList=null;
			String sql="select * from exam.test_questions "
					+ " where id in ( select c.subQuestionsId from exam.test_question_casestudydetails c where c.questionId=? ) "
					+ " and isSubQuestion = 1 "
					+ " Order By id ";
			try {
				testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {questionId}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				
				if(testQuestionsList !=null) {

				}else {

				}
				
				for(TestQuestionExamBean question : testQuestionsList) {
					List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionId(question.getId());
					question.setOptionsList(options);
				}
			} catch (Exception e) {
				
			}
			return testQuestionsList;
		}
		
		public List<TestQuestionExamBean> getStudentSpecifcTestQuestions(String testQuestionIds){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionExamBean> testQuestionsList=null;
			String sql="select * from exam.test_questions where id in ("+testQuestionIds+") ";
			try {
				testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				for(TestQuestionExamBean question : testQuestionsList) {
					question.setOptionsList(getTestQuestionOptionByQuestionId(question.getId()));
					question.setSubQuestionsList(getTestSubQuestions(question.getId()));
				}
			} catch (Exception e) {
				
			}
			return testQuestionsList;
		}
		
		public Integer getNoOfQuestionsByTestId(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select count(*) from exam.test_questions where testId=? and isSubQuestion = 0 Order By id desc ";
			try {
				Integer count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId}, Integer.class);
				return count;
			} catch (Exception e) {
				
				return 0;
			} 
		}
		//CRUD for tests questions end
		
		//CRUD for tests question option start
				@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
				public List<String> batchUpdateTestQuestionOption(final List<TestQuestionOptionExamBean> optionList,long questionId) {

					jdbcTemplate = new JdbcTemplate(dataSource);
					int i = 0;
					List<String> errorList = new ArrayList<>();

					for (TestQuestionOptionExamBean bean : optionList) {
						try{
							bean.setQuestionId(questionId);
							long returnedKey = saveTestQuestionOption(bean);
							if(returnedKey == 0) {
								errorList.add("Error in adding "+i+" option to DB.");
							}
						}catch(Exception e){
							
							errorList.add("Error in adding "+i+" option to DB.");
						}
						i++;
					}
					return errorList;

				}
				
				public long saveTestQuestionOption(final TestQuestionOptionExamBean option) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					GeneratedKeyHolder holder = new GeneratedKeyHolder();
					try {
						//id, questionId, option, isCorrect
						final String sql = "INSERT INTO exam.test_question_options "
								+ " (questionId, optionData, isCorrect) "
				        		+ " VALUES("+option.getQuestionId().toString()+",'"+option.getOptionData().replaceAll("'", "").trim()+"','"+option.getIsCorrect()+"') ";

						jdbcTemplate.update(new PreparedStatementCreator() {
						    @Override
						    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						        return statement;
						    }
						}, holder);
						long primaryKey = holder.getKey().longValue();

						return primaryKey;
					} catch (Exception e) {
						
						return 0;
					}

					}
				/*
				 * id, questionId, optionData, isCorrect
				 * */
				public boolean updateTestQuestionOptionById(TestQuestionOptionExamBean option) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="update exam.test_question_options set "
									+ " optionData=?, "
									+ " isCorrect=? "    
										
									+ " where id=?";
					try {
						jdbcTemplate.update(sql,new Object[] {option.getOptionData().trim(),
															  option.getIsCorrect(),
															  option.getId()
											});

						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
					}
					return false;
				}
				
				public List<TestQuestionOptionExamBean> getTestQuestionOptionByQuestionId( Long questionId){
					jdbcTemplate = new JdbcTemplate(dataSource);
					List<TestQuestionOptionExamBean> optionList=null;
					String sql="select * from exam.test_question_options where questionId=? Order By id ";
					try {
						optionList = (List<TestQuestionOptionExamBean>) jdbcTemplate.query(sql, new Object[] {questionId}, new BeanPropertyRowMapper(TestQuestionOptionExamBean.class));
						
						if(optionList != null) {

						}else {

							optionList = getRandomizedOptions(optionList);
						}
					
					} catch (Exception e) {
						
					}
					return optionList;
				}
				public List<TestQuestionOptionExamBean> getRandomizedOptions(List<TestQuestionOptionExamBean> optionList){
					int noOfQuestions = optionList.size();
					TestQuestionOptionExamBean tempBean;
					List<TestQuestionOptionExamBean> tempList=new ArrayList<>();
					for(int i=noOfQuestions; i>0; i-- ) {
						int random = new Random().nextInt(i);

						tempBean=optionList.get(random);
						optionList.remove(tempBean);
						tempList.add(tempBean);


					}
					return tempList;
				}
				
				public String deleteTestQuestionOptionById(Long id) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					int row=0;	
					String sql="delete from exam.test_question_options where id=?";
					try {
						 row = jdbcTemplate.update(sql, new Object[] {id});
						 return row+"";
					} catch (Exception e) {
						
						return "Error in deleting option. Error : "+e.toString();
					}
				}

				public int deleteTestQuestionOptionByQuestionId(Long questionId) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					int row=0;	
					String sql="delete from exam.test_question_options where questionId=?";
					try {
						 row = jdbcTemplate.update(sql, new Object[] {questionId});

					} catch (Exception e) {
						
					}
					return row;
					}
		//CRUD for tests question options end
				
		//crud for casestudydetails start
				public long saveTestCaseStudyDetails(final TestQuestionCaseStudyBean csd) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					GeneratedKeyHolder holder = new GeneratedKeyHolder();
					try {
						//id, questionId, subQuestionsId
						final String sql = "INSERT INTO exam.test_question_casestudydetails "
								+ " (questionId, subQuestionsId ) "
				        		+ " VALUES("+csd.getQuestionId()+","+csd.getSubQuestionId()+") ";
						
						jdbcTemplate.update(new PreparedStatementCreator() {
						    @Override
						    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						        return statement;
						    }
						}, holder);
						long primaryKey = holder.getKey().longValue();

						return primaryKey;
					} catch (Exception e) {
						
						return 0;
					}

					}
		//crud for casestudydetailsend
				
		//CRUD for tests questions weightage start
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ArrayList<String> batchUpdateTestWeightage(final ArrayList<TestWeightageBean> testWeightagetList) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			int i = 0;
			ArrayList<String> errorList = new ArrayList<>();

			for (TestWeightageBean bean : testWeightagetList) {
				try{
					long returnedKey = saveTestWeightage(bean);
					if(returnedKey == 0) {
						errorList.add(i+"");
					}
				}catch(Exception e){
					
					errorList.add(i+"");
				}
				i++;
			}
			return errorList;

		}
		
		public long saveTestWeightage(final TestWeightageBean tesWeightageBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			//id, testId, chapter, maxMarks, noOfQuestionToMarks, createdBy, createdDate, lastModifiedBy, lastModifiedDate, active
			final String sql = "INSERT INTO exam.test_questions_weightage "
					+ " (testId, chapter, maxMarks, noOfQuestionToMarks, active,"
					+ "  createdBy, createdDate, lastModifiedBy, lastModifiedDate)"
					+ " VALUES(?,?,?,?,'Y',"
	        		+ "		   ?,sysdate(),?,sysdate()) ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setLong(1, tesWeightageBean.getTestId());
				        statement.setString(2, tesWeightageBean.getChapter());
				        statement.setString(3, tesWeightageBean.getMaxMarks());
				        statement.setString(4, tesWeightageBean.getNoOfQuestionToMarks()); 
				        statement.setString(5, tesWeightageBean.getCreatedBy()); 
				        statement.setString(6, tesWeightageBean.getLastModifiedBy()); 
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {
				
				return 0;
			}

			}
		
		public int deleteTestWeightageById(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_questions_weightage where id=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});

			} catch (Exception e) {
				
			}
			return row;
			}
		public int deleteTestWeightageByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_questions_weightage where testId=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});

			} catch (Exception e) {
				
			}
			return row;
			}
		

		public Integer getNoOfQuestionsWeightageEntriesByTestId(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select count(*) from exam.test_questions_weightage where testId=? Order By id ";
			try {
				Integer count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId},  Integer.class);
				return count;
			} catch (Exception e) {
				
				return 0;
			} 
		}
		//CRUD for tests questions end
		
		//CRUD for saveAnswer start
		//id, sapid, testId, questionId, answer, marks, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public long saveStudentsTestAnswer(final StudentQuestionResponseExamBean studentsAnswer) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			String answer = studentsAnswer.getAnswer();
			String answerWithRemovedSpecialCharacters ="";
			answerWithRemovedSpecialCharacters = answer.replaceAll("'", " ");
			answerWithRemovedSpecialCharacters =answerWithRemovedSpecialCharacters.replaceAll("\'", " ");
			answerWithRemovedSpecialCharacters = answerWithRemovedSpecialCharacters.replaceAll("\"", " ");
			answerWithRemovedSpecialCharacters = answerWithRemovedSpecialCharacters.replaceAll(";", " ");
			
			
			


			try {
				final String sql = "INSERT INTO exam.test_students_answers "
						+ " (sapid, testId, questionId, attempt, answer, "
						+ "  createdBy, createdDate, lastModifiedBy, lastModifiedDate, "
						+ "  facultyId )"
						+ " VALUES( "
						+ " '"+studentsAnswer.getSapid()+"','"+studentsAnswer.getTestId()+"', "+studentsAnswer.getQuestionId()+", "+studentsAnswer.getAttempt()+", '"+answerWithRemovedSpecialCharacters+"', "
						+ " '"+studentsAnswer.getSapid()+"',sysdate(), '"+studentsAnswer.getSapid()+"',sysdate(), "
						+ " '' "
						+ ") ";

				
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							return statement;
				    }
				}, holder);
				
				 
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/saveStudentsTestAnswer";
				String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+studentsAnswer.toString()  +
						",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,studentsAnswer.getSapid());

//				
				return 0;
			}

		}
		public boolean updateStudentsQuestionResponse(StudentQuestionResponseExamBean studentsAnswer) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_students_answers set "
							+ " answer=?, "
							+ " lastModifiedBy=?, "
							+ " lastModifiedDate = sysdate() "    
								
							+ " where questionId=? and sapid=? and attempt = ?";
			try {
				jdbcTemplate.update(sql,new Object[] {studentsAnswer.getAnswer(),
													  studentsAnswer.getSapid(),
													  studentsAnswer.getQuestionId(),
													  studentsAnswer.getSapid(),
													  studentsAnswer.getAttempt()
									});

				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/updateStudentsQuestionResponse";
				String stackTrace = "apiCalled="+ apiCalled +  ",data= StudentQuestionResponseBean: "+ studentsAnswer.toString() +
						",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,studentsAnswer.getSapid());

//				
			}
			return false;
		}
		public boolean updateQuestionMarks(StudentQuestionResponseExamBean studentsAnswer) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			if(studentsAnswer.getRemark() == null) {
				studentsAnswer.setRemark("");
			}
			
			String sql="update exam.test_students_answers set "
							+ " isChecked = 1,"
							+ " marks=?, "
							+ " lastModifiedBy=?, "
							+ " lastModifiedDate = sysdate(),"
							+ " remark = ? "    
								
							+ " where id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {studentsAnswer.getMarks(),
						studentsAnswer.getUserId(), studentsAnswer.getRemark(),
						studentsAnswer.getId()
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		public HashMap<Long,List<StudentQuestionResponseExamBean>> getTestAnswerBySapid(String sapid, Long testId, int attempt){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
			String sql="select * from exam.test_students_answers where sapid=? and testId=? and attempt = ? Order By id ";
			HashMap<Long,List<StudentQuestionResponseExamBean>> questionIdAndAnswersByStudentMap = new HashMap<>();
			List<StudentQuestionResponseExamBean> answers =null;
			try {
				testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId,attempt}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				for(StudentQuestionResponseExamBean bean : testAnswersByStudent) {
					if(questionIdAndAnswersByStudentMap.containsKey(bean.getQuestionId())) {
						answers = questionIdAndAnswersByStudentMap.get(bean.getQuestionId());
						answers.add(bean);
						
					}else {
						answers = new ArrayList<>();
						answers.add(bean);
					}
					questionIdAndAnswersByStudentMap.put(bean.getQuestionId(), answers);
					
				}
			} catch (Exception e) {
				
			}
			return questionIdAndAnswersByStudentMap;
		}
		public List<StudentQuestionResponseExamBean> getTestAnswerBySapidAndQuestionId(String sapid, Long questionId, int attempt){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
			String sql="select * from exam.test_students_answers where sapid=? and questionId=? and attempt = ? Order By id ";
			try {
				testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,questionId,attempt}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));

				
			} catch (Exception e) {


				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/getTestAnswerBySapidAndQuestionId";
				String stackTrace = "apiCalled="+ apiCalled + ",data= questionid: " +questionId + 
						",answer: " + testAnswersByStudent.toString()  +  ",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,sapid);

			}

			return testAnswersByStudent;
		}
		
		public int getCountOfAnswersBySapidAndQuestionIdNAttempt(String sapid, Long questionId, int attempt){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
			String sql="select count(id) from exam.test_students_answers where sapid=? and questionId=? and attempt = ? Order By id ";
			try {
				//testAnswersByStudent = (List<StudentQuestionResponseBean>) jdbcTemplate.query(sql, new Object[] {sapid,questionId,attempt}, new BeanPropertyRowMapper(StudentQuestionResponseBean.class));
				int count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {sapid,questionId,attempt}, Integer.class);
				return count;	
			} catch (Exception e) {
				logger.info("\n"+SERVER+": "+" IN getCountOfAnswersBySapidAndQuestionIdNAttempt sapid:  "+sapid+" questionId : "+questionId);
				return 0;
			}

		}
		public List<StudentQuestionResponseExamBean> getTestAnswersBySapid(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
			String sql="select * from exam.test_students_answers where sapid=?  Order By id ";
			try {
				testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
			} catch (Exception e) {
				
			}
			return testAnswersByStudent;
		}
		
		public HashMap<Integer,List<StudentQuestionResponseExamBean>> getAttemptAnswersMapBySapidNTestId(String sapid,Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
			HashMap<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = new HashMap<>();
			String sql="";
			
			/*
			//check if test is for current active subject or not
			String isTestForCurrnetActiveSubject = isTestForCurrnetActiveSubject(testId); 
			if(StringUtils.isNumeric(isTestForCurrnetActiveSubject)) {
			
				Integer countToCheckIfTestIsForCurrentActiveSubject = Integer.parseInt(isTestForCurrnetActiveSubject);
				if(countToCheckIfTestIsForCurrentActiveSubject > 0) {
					sql="select * from exam.test_students_answers where sapid=? and testId = ? Order By id ";
				}else {
					sql="select * from exam.test_students_answers_archive where sapid=? and testId = ? Order By id ";
				}
				
			}else {
				sql="select * from exam.test_students_answers_archive where sapid=? and testId = ? Order By id ";
			}
			*/
			sql="SELECT  " + 
					"    * " + 
					"FROM " + 
					"    (SELECT  " + 
					"        * " + 
					"    FROM " + 
					"        exam.test_students_answers " + 
					"    WHERE " + 
					"        sapid = ? AND testId = ? " + 
					"	UNION ALL "
					+ "  SELECT  " + 
					"        * " + 
					"    FROM " + 
					"        exam.test_students_answers_archive " + 
					"    WHERE " + 
					"        sapid = ? AND testId = ? " + 
					"    ) a order by a.id";
			
			try {
				testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId,sapid,testId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				List<StudentQuestionResponseExamBean> tempList= null;
				for(StudentQuestionResponseExamBean a : testAnswersByStudent) {
					if(attemptsAnswerMap.containsKey(a.getAttempt())) {
						tempList= attemptsAnswerMap.get(a.getAttempt());
					}else {
						tempList = new ArrayList<>();
					}
					tempList.add(a);
					attemptsAnswerMap.put(a.getAttempt(), tempList);
				}
			
				attemptsAnswerMap = addAnswersFromRedis(sapid,testId,attemptsAnswerMap);
			
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+" IN getAttemptAnswersMapBySapidNTestId   got testId : "+testId+" sapId : "+sapid+", Error :  "+e.getMessage());
				
			}
			return attemptsAnswerMap;
		}

	public List<StudentQuestionResponseExamBean> getAttemptAnswers(String sapid, Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentQuestionResponseExamBean> testAnswersByStudent = new ArrayList<StudentQuestionResponseExamBean>();
		
		String sql = "SELECT  " + 
				"    a.questionId, a.answer, a.marks, a.isChecked, a.createdDate, a.lastModifiedDate, a.remark, a.facultyId,  " + 
				"    q.question, q.type, qo.optionData, f.firstName, f.lastName, qt.type as typeString " + 
				"FROM " + 
				"    (SELECT * FROM exam.test_students_answers " + 
				"		WHERE sapid = ? AND testId = ? UNION ALL  " + 
				"	SELECT * FROM exam.test_students_answers_archive " + 
				"		WHERE sapid = ? AND testId = ?) a " + 
				"	INNER JOIN " + 
				"		exam.test_questions q ON a.questionId = q.id " + 
				"	INNER JOIN " + 
				"		exam.test_question_type qt on q.type=qt.id " + 
				"	LEFT JOIN " + 
				"		exam.test_question_options qo ON a.answer = qo.id " + 
				"	LEFT JOIN " + 
				"		acads.faculty f on a.facultyId = f.facultyId " + 
				"ORDER BY a.id";
		
		testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId,sapid,testId}, 
				new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));

		return testAnswersByStudent;
	}
	
		private HashMap<Integer, List<StudentQuestionResponseExamBean>> addAnswersFromRedis(String sapid, Long testId,
				HashMap<Integer, List<StudentQuestionResponseExamBean>> attemptsAnswerMap) {
			
			TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			
			StudentsTestDetailsExamBean studentsTestDetails =  getStudentsTestDetailsBySapidAndTestId(sapid,testId);
			
			List<StudentQuestionResponseExamBean> answersFromReds = new ArrayList<>();
			
			if("N".equalsIgnoreCase(studentsTestDetails.getAnswersMovedFromCacheToDB()) && "N".equals(studentsTestDetails.getShowResult())) {
				answersFromReds = daoForRedis.getAnswersFromRedisByStudentsTestDetails(studentsTestDetails);
			}
			List<StudentQuestionResponseExamBean> answersFromDB = new ArrayList<>();
			
			if(attemptsAnswerMap.get(studentsTestDetails.getAttempt()) != null) {
				answersFromDB = attemptsAnswerMap.get(studentsTestDetails.getAttempt());
			}
			

			List<StudentQuestionResponseExamBean> answersAfterSettingDBNRedis = createAnswersListFromDBNRedis(answersFromDB,answersFromReds);
			
			//answersFromDB.addAll(answersFromReds);
			attemptsAnswerMap.put(studentsTestDetails.getAttempt(),answersAfterSettingDBNRedis);
		
			return attemptsAnswerMap;
		}

		private List<StudentQuestionResponseExamBean> createAnswersListFromDBNRedis(
				List<StudentQuestionResponseExamBean> answersFromDB, List<StudentQuestionResponseExamBean> answersFromRedis) {
			
			List<StudentQuestionResponseExamBean> answersAfterSettingDBNRedis = new ArrayList<>();
			
			Map<Long,StudentQuestionResponseExamBean> questionIdNAnswersMap = new HashMap<>();
			for(StudentQuestionResponseExamBean answerFromDB : answersFromDB) {

					boolean addAnswerToList = false;
					for(StudentQuestionResponseExamBean answerFormRedisToCheck : answersFromRedis) {
						if(answerFromDB.getQuestionId().equals(answerFormRedisToCheck.getQuestionId())) {
							
							boolean isAnswerFromRedisIsLatest = checkIfAnswerFromRedisIsLatest(answerFormRedisToCheck,answerFromDB);
							
							if(isAnswerFromRedisIsLatest) {
								answersAfterSettingDBNRedis.add(answerFormRedisToCheck);
							}else {

								answersAfterSettingDBNRedis.add(answerFromDB);
							}
							addAnswerToList = true;
							break;
							
						}else {
							
						}
					}
					if(!addAnswerToList) {
						answersAfterSettingDBNRedis.add(answerFromDB);
					}
					questionIdNAnswersMap.put(answerFromDB.getQuestionId(),answerFromDB);
			}
			for(StudentQuestionResponseExamBean answerFromRedis : answersFromRedis) {
				
				if(!questionIdNAnswersMap.containsKey(answerFromRedis.getQuestionId())) {
					
					answersAfterSettingDBNRedis.add(answerFromRedis);
					questionIdNAnswersMap.put(answerFromRedis.getQuestionId(),answerFromRedis);
				}
				
			}
			
			return answersAfterSettingDBNRedis;
		}

		private boolean checkIfAnswerFromRedisIsLatest(StudentQuestionResponseExamBean answerFromRedis,
				StudentQuestionResponseExamBean answerFromDB) {
			
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date lastModifiedDateInRedis = sdf.parse(answerFromRedis.getLastModifiedDate());
				Date lastModifiedDateInDB = sdf.parse(answerFromDB.getLastModifiedDate());
					
				if(lastModifiedDateInRedis.after(lastModifiedDateInDB)) {
					return true;
				}else {
					return false;
				}
				
				} catch (ParseException e) {
				// TODO Auto-generated catch block
				
			}
			
			
			return true;
		}

		private String isTestForCurrnetActiveSubject(Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count=0;	
			String sql=" SELECT  " + 
					"	count(t.id) " + 
					"FROM " + 
					"    exam.test t " + 
					"        INNER JOIN " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"        INNER JOIN " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId " + 
					"        AND tls.applicableType = tcm.type " + 
					"        INNER JOIN " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId " + 
					"        INNER JOIN " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId " + 
					"        INNER JOIN " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					"         " + 
					"WHERE " + 
					"    tls.liveType = 'Regular' " + 
					"        AND tls.applicableType = 'module' " + 
					"        AND t.id = ? " + 
					"        AND ssc.startDate <= sysdate() " + 
					"        AND ssc.endDate >= sysdate() " + 
					"					;";
			try {
				 count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId}, Integer.class);

				 return count+"";
			} catch (Exception e) {
				
				return e.getMessage();
			}
		}

		public boolean deleteStudentsAnswersBySapidAndTestId(String sapId,Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_students_answers where sapid=? and testId=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {sapId,testId});

			} catch (Exception e) {
				
				return false;
			}
			return true;
			}
		public boolean deleteStudentsAnswersBySapidQuestionId(String sapId,Long questionId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_students_answers where sapid=? and ( questionId=? "
					+ " or ( questionId in ( select q.subQuestionsId from exam.test_question_casestudydetails q where q.questionId= ? ) ) "
					+ ")";
			
			
			try {
				 row = jdbcTemplate.update(sql, new Object[] {sapId,questionId,questionId});
				

			} catch (Exception e) {

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/deleteStudentsAnswersBySapidQuestionId";
				String stackTrace = "apiCalled="+ apiCalled + ",data= questionid: " +questionId+  ",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,sapId);

//				
				return false;
			}

			return true;
			}
		

		public boolean updateNoOfQuestionAttemptedBySapidTestId(String sapId, Long testId, int attempt) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			
			String sqlForTestDetailsUpdate=" update exam.test_student_testdetails "
					+ "	set noOfQuestionsAttempted = noOfQuestionsAttempted - 1 "
					+ " where sapid = ? and testId = ? and attempt = ?  ";
			
			
			try {
			
				 jdbcTemplate.update(sqlForTestDetailsUpdate, new Object[] {sapId,testId,attempt});
				

				
			} catch (Exception e) {

				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "testDao/updateNoOfQuestionAttemptedBySapidTestId";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+testId+ ",attempt:"+ attempt 
						+ ",errors=" + errors.toString();
				setObjectAndCallLogError(stackTrace,sapId);

//				
				return false;
			}
			return true;
			}
			
		//CRUD for saveAnswer end
		
		//check test answers related methods start
		public HashMap<Long,List<StudentQuestionResponseExamBean>> getTestAnswerByFacultyId(String userId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answuersAssignedToFaculty=null;
			String sql="select *, q.marks as maxMarks, q.question from exam.test_students_answers a ,exam.test_questions q "
					+ " where a.facultyId=? "
					+ " and a.isChecked=0 "
					+ " and a.questionId = q.id "
					+ " Order By a.id ";
			HashMap<Long,List<StudentQuestionResponseExamBean>> testIdAndAnswersToCheckMap = new HashMap<>();
			List<StudentQuestionResponseExamBean> answers =null;
			try {
				answuersAssignedToFaculty = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {userId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				for(StudentQuestionResponseExamBean bean : answuersAssignedToFaculty) {
					if(testIdAndAnswersToCheckMap.containsKey(bean.getTestId())) {
						answers = testIdAndAnswersToCheckMap.get(bean.getTestId());
						answers.add(bean);
						
					}else {
						answers = new ArrayList<>();
						answers.add(bean);
					}
					testIdAndAnswersToCheckMap.put(bean.getTestId(), answers);
					
				}
			} catch (Exception e) {
				
			}
			return testIdAndAnswersToCheckMap;
		}
		
		//check test answers related methods end
		


		public StudentExamBean getSingleStudentsData(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			StudentExamBean student = null;
			try{
				String sql = "SELECT *   FROM exam.students s where "
						+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";




				student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
						sapid, sapid
				}, new BeanPropertyRowMapper(StudentExamBean.class));
				
				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
				
				return student;
			}catch(Exception e){

				return null;
				//
			}

		}
		
		public StudentExamBean getStudentRegistrationData(String sapId) {
			StudentExamBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = "select * from exam.registration r where r.sapid = ? and  r.month = ? "
						+ " and r.year = ?  ";

				studentRegistrationData = (StudentExamBean) jdbcTemplate.queryForObject(sql, new Object[] 
						{ sapId , getLiveRegularTestMonth(),getLiveRegularTestYear()},
								new BeanPropertyRowMapper(StudentExamBean.class));
			} catch (Exception e) {
				// TODO: handle exception
				
				
			}
			return studentRegistrationData;
		}

		//crud for question config start
		public HashMap<Long,TestQuestionConfigBean>  getQuestionConfigsByTestId(Long testId,List<Long> applicableTypes) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionConfigBean> typesList=new ArrayList<>();
			/*String sql = "select c.*, count(q.id) as noOfQuestions " + 
					" from exam.test_questions_configuration c, exam.test_questions q  " + 
					"where c.testId=? " + 
					"and c.testId=q.testId " + 
					"and q.type = c.type;";
			*/
			String sql = "select * from exam.test_questions_configuration where testId=? and type=?";
			HashMap<Long,TestQuestionConfigBean> testIdNConfigMap = new HashMap<>();
			try {
				for(Long type: applicableTypes) {
					TestQuestionConfigBean c=null;
					try {
						c = (TestQuestionConfigBean) jdbcTemplate.queryForObject(sql, new Object[] {testId,type}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));



					} catch (Exception e) {
						// TODO Auto-generated catch block
						//
					}
					if(c==null) {
						c = new TestQuestionConfigBean();
					}
					Integer count = getNoOfTestQuestionsByTestIdNType(testId, type);

					c.setNoOfQuestions(count);
					c.setType(type.intValue());
					c.setTestId(testId);
					typesList.add(c);
				}
				for(TestQuestionConfigBean config : typesList ) {
					testIdNConfigMap.put(new Long(config.getType()), config);
				}
				
			} catch (Exception e) {
				
			}
			return testIdNConfigMap;
		}
		
		public List<TestQuestionConfigBean>  getQuestionConfigsListByTestId(Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionConfigBean> typesList=new ArrayList<>();
			String sql = "select * from exam.test_questions_configuration where testId=? ";
			try {
				typesList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
 
			} catch (Exception e) {
				
			}
			return typesList;
		}

		
		//id, testId, type, minNoOfQuestions, maxNoOfQuestions
		public long saveTestConfig(final TestQuestionConfigBean config) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			
			try {
				/*
				 	INSERT INTO world.city ( Name, CountryCode, District, Population)
					
					SELECT * FROM (SELECT  'abcd' as Name ,'AFG' CountryCode,'a' District, 1 Population) AS tmp
					WHERE NOT EXISTS (
					    SELECT Name FROM world.city WHERE Name = 'abcd'
					) LIMIT 1; 
				 * */
				
				/*
				final String sql = "INSERT INTO exam.test_questions_configuration "
						+ " ( testId, type, minNoOfQuestions, maxNoOfQuestions )"
						+ " VALUES("
						+ " "+config.getTestId()+", "+config.getType()+", "+config.getMinNoOfQuestions()+", "+config.getMaxNoOfQuestions()+"  "
						+ ") ";
				*/
				

				final String sql = "INSERT INTO exam.test_questions_configuration "
						+ " ( testId, type,questionMarks, minNoOfQuestions, maxNoOfQuestions,sectionId )"
						+ " " + 
						"		SELECT * FROM (SELECT  "+config.getTestId()+" testId , "+config.getType()+" type, "+config.getQuestionMarks()+" questionMarks, "+config.getMinNoOfQuestions()+" minNoOfQuestions, "+config.getMaxNoOfQuestions()+" maxNoOfQuestions,"+config.getSectionId()+" sectionId) AS tmp" + 
						"		WHERE NOT EXISTS (" + 
						"		    SELECT id FROM exam.test_questions_configuration "
						+ "				WHERE testId = "+config.getTestId()+" and type = "+config.getType()+" and questionMarks= " + config.getQuestionMarks()+
						" and sectionId="+config.getSectionId()+ " 		) LIMIT 1 "; 
				

				
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							return statement;
				    }
				}, holder);
				
				 
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {
				
				return 0;
			}

		}
		//id, testId, type, minNoOfQuestions, maxNoOfQuestions
		public boolean updateTestConfig(TestQuestionConfigBean config) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_questions_configuration set "
							+ " minNoOfQuestions=?, "
							+ " maxNoOfQuestions=? "    
								
							+ " where testId=? and type=?";
			try {
				jdbcTemplate.update(sql,new Object[] {config.getMinNoOfQuestions(),
													  config.getMaxNoOfQuestions(),
													  config.getTestId(),
													  config.getType()
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		
		public TestQuestionConfigBean getTestConfigByTestIdNQuestionType(Long testId, int type){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestQuestionConfigBean config=null;
			String sql="select * from exam.test_questions_configuration where testId=? and type=?";
			try {
				 config = (TestQuestionConfigBean) jdbcTemplate.queryForObject(sql, new Object[] {testId,type}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
			} catch (Exception e) {
				//
			}
			return config;
		}

		//crud for question config end
		
		//crud for faculty answers allocations start
		public List<StudentQuestionResponseExamBean> getFacultyIdNNoOfAllocatedAnswers(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> facultyNAllocatedAnswers=null;
			String sql ="SELECT a.facultyId, count(a.facultyId) as allocatedAnswers , concat(f.firstName,' ',f.lastName) as facutlyName  " + 
					" FROM exam.test_students_answers a, acads.faculty f    " + 
					" where  f.facultyId = a.facultyId " + 
					" AND a.sapid NOT LIKE '777777%' "+
					" and a.isChecked = 0   " + 
					" and a.questionId in (SELECT q.id FROM exam.test_questions q where q.type in (4,8) AND q.testId =?)" + 
					" group by a.facultyId";
			try {
				facultyNAllocatedAnswers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
			} catch (Exception e) {
				
			}
			return facultyNAllocatedAnswers;
		}
		public boolean updateFacultyAllocations(StudentQuestionResponseExamBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql=" UPDATE exam.test_students_answers a " + 
					" SET " + 
					"    a.isChecked = 0, " + 
					"    a.facultyId = ? " + 
					" WHERE " + 
					"    (a.facultyId = '' OR a.facultyId IS NULL) " + 
					"        AND a.sapid NOT LIKE '777777%' "+
					"        AND a.questionId IN "+
					"  (SELECT q.id FROM exam.test_questions q " + 
					"   WHERE q.type in (4,8) AND q.testId = ?) ORDER BY a.id ASC LIMIT ? ";
			try {

				jdbcTemplate.update(sql,new Object[] {
						bean.getFacultyId(),
						bean.getTestId(),
						bean.getAllocatedAnswers()
						});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		
		public boolean clearFacultyAllocation(StudentQuestionResponseExamBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_students_answers set "
							+ " facultyId='', isChecked=0 "
							+ " where questionId IN " + 
							"   (SELECT q.id " + 
							"        FROM " + 
							"            exam.test_questions q " + 
							"        WHERE " + 
							"            q.type IN (4 , 8) AND q.testId = ?) and facultyId=?  AND sapid NOT LIKE '777777%' AND sapid NOT IN  "+
						    "  (SELECT " + 
							"                t.sapid " + 
							"            FROM " + 
							"                exam.test_student_testdetails t " + 
							"            WHERE " + 
							"                t.testId = ? AND t.attemptStatus = 'CopyCase')";
			try {

				jdbcTemplate.update(sql,new Object[] {bean.getTestId(),
													  bean.getFacultyId(),
													  bean.getTestId()
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		public Integer getNoOfAnswersNotAllocated(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			Integer count=0;
			String sql="SELECT  " + 
					"    COUNT(*) " + 
					"FROM " + 
					"    exam.test_students_answers a " + 
					"WHERE " + 
					"    a.isChecked = 0 " + 
					"    AND a.sapid NOT LIKE '777777%' "+
					"    AND a.questionId IN " +
					" (SELECT   q.id  FROM  exam.test_questions q WHERE q.type in (4,8) AND  q.testId = ?) " + 
					"        AND (a.facultyId = '' OR a.facultyId IS NULL) ";
			try {
				count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId}, Integer.class);
				return count;
			} catch (Exception e) {
				
			}
			return count;
		}
		
		public Integer getNoOfAnsweresAllocatedToFaculty(String facultyId, Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			Integer count=0;
			String sql = "SELECT  count(a.facultyId) as allocatedAnswers " + 
					" FROM exam.test_students_answers a " + 
					" where  " + 
					"  a.questionId in (SELECT q.id FROM exam.test_questions q where q.type in (4,8) AND q.testId =?)" + 
					" AND a.facultyId = ?  AND a.sapid NOT LIKE '777777%' ";
			try {
				count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId, facultyId}, Integer.class);
				return count;
			} catch (Exception e) {
				
				return count;
			}
		}
		
		//crud for faculty answers allocations end
		
		public List<String> getFailSubjectsForAStudent(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<String> subjectsList = new ArrayList<>();
			String sql = "select subject from exam.passfail where isPass = 'N' and sapid = ? order by subject ";

			 try {
				subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}

			return subjectsList;
		}
		
		public int getPastCycleTestAttempts(String subject, String sapId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=new ArrayList<>();
			String sql = "select tst.* from exam.test_student_testdetails tst, exam.test t, exam.examorder eo  " + 
						"	 where tst.sapid = ?  " + 
						"	 and t.subject = ?  " + 
						"    and t.id= tst.testId " + 
						"    and t.year = eo.year  " + 
						"    and t.month = eo.month  " + 
						"    and eo.order >= 15.5 " + //15.5 means Apr-16 onwards
						"    and  " + 
						"    ( " + 
						"    concat(t.year,t.month) <> (select concat(year,month) from exam.examorder eo2"
						+ "								 where eo2.order = (select max(eo3.order) from exam.examorder eo3"
						+ "													 where eo3.assignmentLive ='Y')"
						+ "							   ) " + 
						"    ) " + 
						"   group by tst.sapid,tst.testId	";
			int numberOfAttempts = 0;
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapId,subject}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
				numberOfAttempts = testsByStudent !=null ? testsByStudent.size() : 0;
			} catch (Exception e) {
				
			}
			return numberOfAttempts;
		}
		
		public boolean checkIfTestFeesPaidForAttempt(String subject, String sapId,Long testId,int testAttempt) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignmentpayment a , exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month "
					+ " and eo.order = (Select max(examorder.order) from exam.examorder where assignmentLive='Y') "
					+ " and a.subject = ? and a.booked = 'Y' and a.sapid = ?"
					+ " and a.testId = ? and a.testAttempt = ?";

			int bookingCount = (int) jdbcTemplate.queryForObject(sql, new Object[] {
					subject, sapId,testId,testAttempt },Integer.class);
			if (bookingCount > 0) {
				return true;
			} else {
				return false;
			}
		}
		
		//crud for additionalContent start
		public long saveTestQuestionAdditionalcontent(final TestQuestionExamBean testQuestion) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			// id, questionId, url : exam.test_question_additionalcontent
			final String sql = "INSERT INTO exam.test_question_additionalcontent "
					+ " (questionId, url ) "
	        		+ " VALUES(?,?) ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setLong(1, testQuestion.getId());
				        statement.setString(2, testQuestion.getUrl());     
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (Exception e) {
				
				return 0;
			}

			}
		public boolean updateTestQuestionAdditionalcontent(TestQuestionExamBean testQuestion) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			// id, questionId, url : exam.test_question_additionalcontent
			String sql="update exam.test_question_additionalcontent set "
							+ " url=? "  
								
							+ " where questionId = ?";
			try {
				jdbcTemplate.update(sql,new Object[] {testQuestion.getUrl(),
						
													  testQuestion.getId()
									});

				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}

		public int deleteTestQuestionAdditionalcontentByQuestionId(Long questionId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_question_additionalcontent where questionId=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {questionId});

			} catch (Exception e) {
				
			}
			return row;
			}
		//crud for additionalContent end
		
		public ArrayList<String> getAllFaculties() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' ";

			ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return facultyList;
		}
		
		// To be deleted later start
		public ArrayList<ConsumerProgramStructureExam> getConsumerTypeListDelete(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> ConsumerType = null;
			
			
			String sql =  "SELECT id,name FROM exam.consumer_type";
			
			try {
				ConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				
			} catch (Exception e) {
				
				
			}
			
			return ConsumerType;  
			
		}
		
		public ArrayList<ConsumerProgramStructureExam> getProgramStructureByConsumerType(String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programsStructureByConsumerType = null;
			

			String sql =  "select p_s.program_structure as name,p_s.id as id "
					+ "from exam.consumer_program_structure as c_p_s "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "where c_p_s.consumerTypeId = ? group by p_s.id";
			
			try {
				programsStructureByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				
			} catch (Exception e) {
				
				
				return null;
			}
			
			return programsStructureByConsumerType;  
			
		}
		
		public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerType(String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
			

			String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
					+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
					+ "where c_p_s.consumerTypeId = ? group by p.id";
			
			try {
				programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				
			} catch (Exception e) {
				
				
				return null;
			}
			
			return programsByConsumerType;  
			
		}
		
		public ArrayList<ConsumerProgramStructureExam> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
			

			String sql =  "select p_s_s.subject as name from exam.program_sem_subject as p_s_s "
					+ "where p_s_s.consumerProgramStructureId in "
					+ "(select c_p_s.id as id from consumer_program_structure as c_p_s where c_p_s.programId in("+programId+") "
							+ "and c_p_s.programStructureId in("+programStructureId+") and c_p_s.consumerTypeId in("+consumerTypeId+")) group by p_s_s.subject";
			
			try {
				programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql,
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

			} catch (Exception e) {
				
				
				return null;
			}
			
			return programsByConsumerType;  
			
		}
		
		public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programsByConsumerTypeAndPrgmStructure = null;
			

			String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
					+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
					+ "where c_p_s.consumerTypeId = ? and c_p_s.programStructureId in ("+ programStructureId+") group by p.id";
			
			try {
				programsByConsumerTypeAndPrgmStructure = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				
			} catch (Exception e) {
				
				
				return null;
			}
			
			return programsByConsumerTypeAndPrgmStructure;  
			
		}
		
		public ArrayList<ConsumerProgramStructureExam> getConsumerTypeList(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> ConsumerType = null;
			
			;
			
			String sql =  "SELECT id,name FROM exam.consumer_type";
			
			try {
				ConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				
			} catch (Exception e) {
				
				
				return null;
			}
			
			return ConsumerType;  
			
		}
		
		//////////////////////////
		//For Inserting data into DB If Any Option Is Selected Id "All"(Assignment Upload)
		//Start
		
		//Fetch consumerProgramStructureId's From consumer_program_structure Based On Values selected in (programId,ProgramStructureId,ConsumerId)

		
		
		public ArrayList<String>  getconsumerProgramStructureIds(String programId,String programStructureId, String consumerTypeId){
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =  "SELECT id FROM exam.consumer_program_structure "
					+ "where programId in ("+ programId +") and "
					+ "programStructureId in ("+ programStructureId +") and "
					+ "consumerTypeId in ("+ consumerTypeId +")";

			ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
					sql,  new SingleColumnRowMapper(
							String.class));
			

			return consumerProgramStructureIds;
		}
		
		public ArrayList<String>  getconsumerProgramStructureIdsWithSubject(String programId,String programStructureId, String consumerTypeId, String subject){
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =  "SELECT c_p_s.id FROM exam.consumer_program_structure as c_p_s, "
					+ "exam.program_sem_subject as p_s_s "
					+ "where c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ "and p_s_s.subject=?";

			ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
					sql, new Object[] {subject},  new SingleColumnRowMapper(
							String.class));
			

			return consumerProgramStructureIds;
		}
		

		public void batchInsertOfAssignmentsIfAll(final AssignmentFileBean bean,final String year,final String month,final List<String> consumerProgramStructureIds){
			String sql = " INSERT INTO exam.assignments (year, month, subject, startDate, endDate, instructions, "
					+ " filePath, questionFilePreviewPath, createdBy, createdDate, lastModifiedBy, lastModifiedDate, program, consumerProgramStructureId) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?)"
					+ " on duplicate key update "
					+ "	    startDate = ?,"
					+ "	    endDate = ?,"
					+ "	    instructions = ?,"
					+ "	    filePath = ?,"
				 	+ "	    questionFilePreviewPath = ?,"
					+ "	    lastModifiedBy = ?, "
					+ "	    lastModifiedDate = sysdate(), "
					+ "	    consumerProgramStructureId = ? ";
			
			int[] batchInsertOfAssignmentsIfAll = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					
					ps.setString(1,year);
					ps.setString(2,month);
					ps.setString(3,bean.getSubject());
					ps.setString(4,bean.getStartDate());
					ps.setString(5,bean.getEndDate());
					ps.setString(6,bean.getInstructions());
					ps.setString(7,bean.getFilePath());
					ps.setString(8,bean.getQuestionFilePreviewPath());
					ps.setString(9,bean.getCreatedBy());
					ps.setString(10,bean.getLastModifiedBy());
					ps.setString(11,"All");
					ps.setString(12,consumerProgramStructureIds.get(i));
					
					//On Update
					ps.setString(13,bean.getStartDate());
					ps.setString(14,bean.getEndDate());
					ps.setString(15,bean.getInstructions());
					ps.setString(16,bean.getFilePath());
					ps.setString(17,bean.getQuestionFilePreviewPath());
					ps.setString(18,bean.getLastModifiedBy());
					ps.setString(19,consumerProgramStructureIds.get(i));
				
					
					
				}

				@Override
				public int getBatchSize() {
					return consumerProgramStructureIds.size();
				}
			  });

		}
		
		//for Getting AssignmentKey From Consumer Id and Programs Id
		public String getAssignmentKey(String programId,String programStructureId, String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String assignmentKey = null;
			
			String sql =  "SELECT id FROM exam.consumer_program_structure where consumerTypeId = ? and programId = ? and programStructureId = ?";
			
			try {
				
			assignmentKey = (String) jdbcTemplate.queryForObject(sql,new Object [] {consumerTypeId,programId,programStructureId},String.class);
				
				
			} catch (Exception e) {
				
			}
			
			return assignmentKey;
			
		}
		

		/*public void saveAssignmentDetails(AssignmentFileBean bean, String year,
				String month) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = " INSERT INTO exam.assignments (year, month, subject, startDate, endDate, instructions, "
					+ " filePath, questionFilePreviewPath, createdBy, createdDate, lastModifiedBy, lastModifiedDate, program, consumerProgramStructureId) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?)"
					+ " on duplicate key update "
					+ "	    startDate = ?,"
					+ "	    endDate = ?,"
					+ "	    instructions = ?,"
					+ "	    filePath = ?,"
					+ "	    questionFilePreviewPath = ?,"
					+ "	    lastModifiedBy = ?, "
					+ "	    lastModifiedDate = sysdate(), "
					+ "	    consumerProgramStructureId = ? ";

			String subject = bean.getSubject();
			
			String consumerProgramStructureId = bean.getConsumerProgramStructureId();
			
			String startDate = bean.getStartDate();
			String endDate = bean.getEndDate();
			String instructions = bean.getInstructions();
			String filePath = bean.getFilePath();
			String questionFilePreviewPath = bean.getQuestionFilePreviewPath();


			String createdBy = bean.getCreatedBy();
			String lastModifiedBy = bean.getLastModifiedBy();
			String program = bean.getProgram();

			jdbcTemplate.update(sql, new Object[] { year, month, subject,
					startDate, endDate, instructions, filePath,
					questionFilePreviewPath, createdBy, lastModifiedBy, "All", consumerProgramStructureId,

					startDate, endDate, instructions, filePath,
					questionFilePreviewPath, lastModifiedBy,consumerProgramStructureId });

		}*/
		
		// To be deleted later end
		
		public String batchInsertTestIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(final TestExamBean bean,final List<String> consumerProgramStructureIds){
			try {
				String sql = " INSERT INTO exam.test_testid_consumerprogramstructureid_mapping (testId, consumerProgramStructureId,createdBy,createdDate) "
						+ " VALUES (?,?,?,sysdate())"
						;
				
				int[] batchInsertTestIdConsumerTypeIdMappingsForMultipleConsumerIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {

						ps.setLong(1,bean.getId());
						ps.setString(2,consumerProgramStructureIds.get(i));
						ps.setString(3,bean.getLastModifiedBy());				
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });

				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
				return "Error in batchInsertTestIdConsumerTypeIdMappingsForMultipleConsumerIds : "+e.getMessage();
			}
		
		}
		
		
		public String insertSingleTestIdConsumerProgramStructureIdMapping(final TestExamBean bean,final String consumerProgramStructureId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			final String sql = "INSERT INTO exam.test_testid_consumerprogramstructureid_mapping "
					+ " (testId, consumerProgramStructureId,createdBy,createdDate) "
					+ " VALUES (?,?,?,sysdate())";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							statement.setLong(1,bean.getId());
							statement.setString(2,consumerProgramStructureId);
							statement.setString(3,bean.getLastModifiedBy());				
							   
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();

				return "";
			} catch (Exception e) {
				
				return "Error in insertSingleTestIdConsumerProgramStructureIdMapping : "+e.getMessage();
			}

			}
		public String deleteTestIdNConfigMappingbyTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_testid_configuration_mapping where testId=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});

				 return "";
			} catch (Exception e) {
				
				return "Error in deleteTestIdNConfigMappingbyTestId : "+e.getMessage();
			}
			}	
		public String getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(String programId,String programStructureId, String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String consumerProgramStructureId = null;
			
			String sql =  "SELECT id FROM exam.consumer_program_structure where consumerTypeId = ? and programId = ? and programStructureId = ?";
			
			try {
				
				consumerProgramStructureId = (String) jdbcTemplate.queryForObject(sql,new Object [] {consumerTypeId,programId,programStructureId},String.class);
				
				
			} catch (Exception e) {
				
			}
			
			return consumerProgramStructureId;
			
		}
		
		public Map<String,String> getConsumerTypeIdNameMap(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> ConsumerType = null;
			
			Map<String,String> consumerTypeIdNameMap = new HashMap<>();
			
			String sql =  "SELECT id,name FROM exam.consumer_type";
			
			try {
				ConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				for(ConsumerProgramStructureExam c : ConsumerType) {
					consumerTypeIdNameMap.put(c.getId(), c.getName());
				}

			} catch (Exception e) {
				
				
			}
			
			return consumerTypeIdNameMap;  
			
		}
		
		public Map<String,String> getProgramStructureIdNameMap(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programStructureList = null;
			
			Map<String,String> programStructureIdNameMap = new HashMap<>();
			
			String sql =  "SELECT id,program_structure as name FROM exam.program_structure";
			
			try {
				programStructureList = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				for(ConsumerProgramStructureExam c : programStructureList) {
					programStructureIdNameMap.put(c.getId(), c.getName());
				}

			} catch (Exception e) {
				
				
			}
			
			return programStructureIdNameMap;  
			
		}
		public Map<String,String> getProgramIdNameMap(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureExam> programList = null;
			
			Map<String,String> programIdNameMap = new HashMap<>();
			
			String sql =  "SELECT id,code as name FROM exam.program";
			
			try {
				programList = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));

				for(ConsumerProgramStructureExam c : programList) {
					programIdNameMap.put(c.getId(), c.getName());
				}

				
			} catch (Exception e) {
				
				
			}
			
			return programIdNameMap;  
			
		}
		public List<TestExamBean> getTestsLiveConfigList(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsLiveConfigList = new ArrayList<>();
			List<TestExamBean> testsLiveConfigListForOld = new ArrayList<>();
			List<TestExamBean> testsLiveConfigListForModule = new ArrayList<>();
			List<TestExamBean> testsLiveConfigListForBatch = new ArrayList<>();
			
			String sqlForOldConfig =  "select tls.acadYear,tls.acadMonth,tls.examYear,tls.examMonth,tls.liveType,tls.applicableType, "
					+ " pss.subject,"
					+ "p.code as program, p.id as programId,"
					+ "p_s.program_structure as programStructure, p_s.id as programStructureId,"
					+ "c_t.name as consumerType, c_t.id as consumerTypeId "
					+ "from exam.test_live_settings as tls "+
					"        LEFT JOIN " + 
					"    exam.program_sem_subject AS pss ON pss.id = tls.referenceId " + 
					"        LEFT JOIN " + 
					"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = pss.consumerProgramStructureId "
					+ "left join exam.program as p on p.id = c_p_s.programId "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
					+ " where tls.applicableType = 'old'  order by tls.lastModifiedDate desc";
			
			try {
				testsLiveConfigListForOld = (List<TestExamBean>) jdbcTemplate.query(sqlForOldConfig, 
						new BeanPropertyRowMapper(TestExamBean.class));
				


			} catch (Exception e) {
				
			}

			String sqlForBatchConfig =  "SELECT  " + 
					"    tls.acadYear, " + 
					"    tls.acadMonth, " + 
					"    tls.examYear, " + 
					"    tls.examMonth, " + 
					"    tls.liveType, " + 
					"    tls.applicableType, " + 
					"    p.code AS program, " + 
					"    p.id AS programId, " + 
					"    p_s.program_structure AS programStructure, " + 
					"    p_s.id AS programStructureId, " + 
					"    c_t.name AS consumerType, " + 
					"    c_t.id AS consumerTypeId, " + 
					"    b.name, " + 
					"    b.id AS referenceId, " + 
					"    pss.subject " + 
					"FROM " + 
					"    exam.test_live_settings AS tls " + 
					"        LEFT JOIN " + 
					"    lti.student_subject_config AS ssc ON ssc.id = tls.referenceId " + 
					"        LEFT JOIN " + 
					"    exam.program_sem_subject AS pss ON pss.id = ssc.prgm_sem_subj_id " + 
					"        LEFT JOIN " + 
					"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = pss.consumerProgramStructureId " + 
					"        LEFT JOIN " + 
					"    exam.program AS p ON p.id = c_p_s.programId " + 
					"        LEFT JOIN " + 
					"    exam.program_structure AS p_s ON p_s.id = c_p_s.programStructureId " + 
					"        LEFT JOIN " + 
					"    exam.consumer_type AS c_t ON c_t.id = c_p_s.consumerTypeId " + 
					"        LEFT JOIN " + 
					"    exam.batch AS b ON b.id = ssc.batchId " + 
					"WHERE " + 
					"    tls.applicableType = 'batch'  order by tls.lastModifiedDate desc" + 
					"      " + 
					"";
			
			try {
				testsLiveConfigListForBatch = (List<TestExamBean>) jdbcTemplate.query(sqlForBatchConfig, 
						new BeanPropertyRowMapper(TestExamBean.class));
				

			} catch (Exception e) {
				
			}

			
			String sqlForModuleConfig =  "select tls.acadYear,tls.acadMonth,tls.examYear,tls.examMonth,tls.liveType,tls.applicableType,"
					+ "p.code as program, p.id as programId,"
					+ "p_s.program_structure as programStructure, p_s.id as programStructureId,"
					+ "c_t.name as consumerType, c_t.id as consumerTypeId, "
					+ "pss.subject,"
					+ "m.topic,m.id as referenceId, "
					+ "b.name,b.id as moduleBatchId "
					+ "from exam.test_live_settings as tls "
					+ "left join acads.sessionplan_module m on m.id = tls.referenceId "
					+ "left join acads.sessionplan s on s.id = m.sessionPlanId "
					+ "left join acads.sessionplanid_timeboundid_mapping stm on stm.sessionPlanId = s.id "
					+ "left join lti.student_subject_config as ssc on ssc.id = stm.timeboundId "
					+ "left join exam.program_sem_subject as pss on pss.id = ssc.prgm_sem_subj_id "
					+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = pss.consumerProgramStructureId "
					+ "left join exam.program as p on p.id = c_p_s.programId "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
					+ "left join exam.batch as b on b.id = ssc.batchId "
					+ " where tls.applicableType = 'module' order by tls.lastModifiedDate desc";
			
			try {
				testsLiveConfigListForModule = (List<TestExamBean>) jdbcTemplate.query(sqlForModuleConfig, 
						new BeanPropertyRowMapper(TestExamBean.class));
				

			} catch (Exception e) {
				
			}
			
			testsLiveConfigList.addAll(testsLiveConfigListForModule);

			testsLiveConfigList.addAll(testsLiveConfigListForBatch);

			testsLiveConfigList.addAll(testsLiveConfigListForOld);

			
			
			return testsLiveConfigList;  
			
		}
		public String batchInsertOfMakeTestsLiveConfigs(final  TestExamBean  bean, final ArrayList<String> consumerProgramStructureIds){
			String sql = "INSERT INTO exam.test_live_settings "
					+ "(acadsYear,acadsMonth,examYear,examMonth,liveType,consumerProgramStructureId) "
					+ "VALUES(?,?,?,?,?,?) "
					+ "on duplicate key update "
					+ "acadsYear=?, acadsMonth=?, examYear=?, examMonth=?";
			String errorMessage = "";
			try {
				int[] batchInsertOfMakeTestsLiveConfigs = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {

						ps.setInt(1,bean.getAcadsYear());
						ps.setString(2,bean.getAcadsMonth());
						ps.setInt(3,bean.getExamYear());
						ps.setString(4,bean.getExamMonth());
						ps.setString(5,bean.getLiveType());
						ps.setString(6,consumerProgramStructureIds.get(i));
						
						//On Update
						ps.setInt(7,bean.getAcadsYear());
						ps.setString(8,bean.getAcadsMonth());
						ps.setInt(9,bean.getExamYear());
						ps.setString(10,bean.getExamMonth());
						
						
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });

			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				

				return "Error in creating configurations , Error : "+e.getMessage();
			}
			return errorMessage;
		}
		 

		public Map<Long,Integer> getTypeIdNCountOfProgramsApplicableToMap(String testIds) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "SELECT  " + 
					"    count(t.id) as countOfProgramsApplicableTo ,t.id " + 
					"FROM " + 
					"    exam.test t, " + 
					"    exam.test_testid_consumerprogramstructureid_mapping tcm " + 
					"WHERE " + 
					"    t.id = tcm.testId " + 
					"    AND t.id in ("+testIds+") " + 
					"GROUP BY t.id  " + 
					"ORDER BY id DESC ";
					try {
				List<TestExamBean> testList = (List<TestExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestExamBean.class));
				Map<Long,Integer> testMap = new HashMap<>();
				for(TestExamBean bean : testList) {
					testMap.put(bean.getId(), bean.getCountOfProgramsApplicableTo());
				}
			
				return testMap;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				return null;
			}
		}

		public List<TestExamBean> getProgramsListForCommonTest(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=new ArrayList<>();

			String sql="SELECT   " + 
					"    t.id,t.year,t.month,t.subject,"
					+ "  ct.id as consumerTypeId,ct.name as consumerType,"
					+ "  p.id as programId,p.code as program,"
					+ "  ps.id as programStructureId,ps.program_structure as programStructure,"
					+ "  tcm.consumerProgramStructureId  " + 
					"FROM  " + 
					"    exam.test t,  " + 
					"    exam.test_testid_consumerprogramstructureid_mapping tcm,  " + 
					"    exam.consumer_type ct,  " + 
					"    exam.consumer_program_structure cps,  " + 
					"    exam.program p,  " + 
					"    exam.program_structure ps  " + 
					"      " + 
					"WHERE  " + 
					"    t.id = tcm.testId  " + 
					"        AND tcm.consumerProgramStructureId = cps.id  " + 
					"        AND cps.consumerTypeId = ct.id  " + 
					"        AND cps.programId = p.id  " + 
					"        AND cps.programStructureId = ps.id  " + 
					"        AND t.id = ?  " + 
					"   " + 
					"ORDER BY id DESC";
			
			try {

				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		public TestExamBean getTestByIdNConsumerProgramStructureId(Long testId,Integer consumerProgramStructureId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestExamBean test=new TestExamBean();

			String sql="SELECT   " + 
					"    t.*, ct.name as consumerType,"
					+ "  p.code as program,ps.program_structure as programStructure, tcm.consumerProgramStructureId  " + 
					"FROM  " + 
					"    exam.test t,  " + 
					"    exam.test_testid_consumerprogramstructureid_mapping tcm,  " + 
					"    exam.consumer_type ct,  " + 
					"    exam.consumer_program_structure cps,  " + 
					"    exam.program p,  " + 
					"    exam.program_structure ps  " + 
					"      " + 
					"WHERE  " + 
					"    t.id = tcm.testId  " + 
					"        AND tcm.consumerProgramStructureId = cps.id  " + 
					"        AND cps.consumerTypeId = ct.id  " + 
					"        AND cps.programId = p.id  " + 
					"        AND cps.programStructureId = ps.id  " + 
					"        AND t.id = ? "
					+ "		 AND tcm.consumerProgramStructureId = ? " + 
					"   " + 
					"ORDER BY id DESC";
			
			try {

				 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {testId,consumerProgramStructureId}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return test;
		}

		public int deleteTestIdNConsumerProgramStructureIdMapping(Long id,
																  Integer consumerProgramStructureId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_testid_consumerprogramstructureid_mapping "
					+ "	 where testId=? and consumerProgramStructureId=? ";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id,consumerProgramStructureId});

			} catch (Exception e) {
				
			}
			return row;
			
		}
		

		public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.program_sem_subject order by consumerProgramStructureId, sem, subject";

			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
			return programSubjectMappingList;
		}

		public List<TestExamBean> getLiveTestForCurrentSemSubjectsBySubjectsAndMasterkey(
				ArrayList<String> subjects, StudentExamBean student) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			String subjectCommaSeparated = "''";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			String sql = " SELECT t.* "
					+ "	FROM "
					+ "		exam.test t,"
					+ "		exam.test_testid_consumerprogramstructureid_mapping ttcm,"
					+ "		exam.test_live_settings as t_l_s  "
					+ " where "
					+ "		t.month = t_l_s.examMonth "
					+ "		and t.year = t_l_s.examYear"
					+ "		and t.id = ttcm.testId "
					+ "		and ttcm.consumerProgramStructureId = t_l_s.consumerProgramStructureId "
					+ "		and t.subject in ("+ subjectCommaSeparated +") "
					+ "		and t.startDate <= sysdate() "
					+ "		and ttcm.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" "
					+ "		and t_l_s.liveType = 'Regular';";


			
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			return testsList;
		
			
		}

		public List<TestExamBean> getLiveTestForFailedSubjecsBySubjectsAndMasterkey(
				ArrayList<String> subjects, StudentExamBean student) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			String subjectCommaSeparated = "''";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			
			String sql = " SELECT t.* "
					+ "	FROM "
					+ "		exam.test t,"
					+ "		exam.test_testid_consumerprogramstructureid_mapping ttcm,"
					+ "		exam.test_live_settings as t_l_s  "
					+ " where "
					+ "		t.month = t_l_s.examMonth "
					+ "		and t.year = t_l_s.examYear"
					+ "		and t.id = ttcm.testId "
					+ "		and ttcm.consumerProgramStructureId = t_l_s.consumerProgramStructureId "
					+ "		and t.subject in ("+ subjectCommaSeparated +") "
					+ "		and t.startDate <= sysdate() "
					+ "		and ttcm.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" "
					+ "		and t_l_s.liveType = 'Resit';";


			
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			return testsList;
		
			
}
		public boolean checkHasTest(String consumerProgramStructureId) {
			int count = 0;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT count(hasTest) as count FROM exam.program_sem_subject where consumerProgramStructureId=? and hasIA='Y' and hasTest='Y';";
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(Integer.class));
			}
			catch (Exception e) {
				
				return false;
			}
			if(count > 0) {
				return true;
			}else {
				return false;
			}

		}
		
		public TestExamBean getCurrentLiveTestConfigByMasterKeyAndLivetype(String consumerProgramStructureId,String liveType){
			TestExamBean liveConfig = new TestExamBean();
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = "select * from exam.test_live_settings where consumerProgramStructureId = ? and liveType = ? limit 1";

				 liveConfig = (TestExamBean)jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId,liveType},new BeanPropertyRowMapper(TestExamBean.class) );
			}
			catch (Exception e) {
				//
			}
			return liveConfig;
			
		}

		public StudentExamBean getStudentRegistrationDataForTest(String sapId) {
			StudentExamBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				/*String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
						+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";*/
				
				String sql = "select * from exam.registration r where r.sapid = ? and  r.month = ? "
						+ " and r.year = ?  ";

				studentRegistrationData = (StudentExamBean) jdbcTemplate.queryForObject(sql, new Object[] 
						{ sapId , getLiveRegularTestMonth(), getLiveRegularTestYear()},
								new BeanPropertyRowMapper(StudentExamBean.class));
			} catch (Exception e) {
				// TODO: handle exception
			}
			return studentRegistrationData;
		}

		public ArrayList<AssignmentFileBean> getFailSubjectsForAStudentApplicableForTest(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			ArrayList<AssignmentFileBean> subjectsList = new ArrayList<>(); 
			
			try {
				String sql = "select pf.subject,pf.program,pf.sem, assignmentscore "
							+ " from exam.passfail pf,"
							+ "		 exam.students s,"
							+ "		 exam.program_sem_subject pss "
							+ " where isPass = 'N' "
							+ "	and pf.sapid = ? "
							+ " and pf.sapid = s.sapid "
							+ " and s.sem = (select max(sem) from exam.students where sapid = s.sapid ) "
							+ " and s.consumerProgramStructureId = pss.consumerProgramStructureId"
							+ " and pss.subject = pf.subject"
							+ " and pss.hasTest = 'Y' "
							+ " and pss.hasIA = 'Y' ";

				subjectsList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentFileBean.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
			
			return subjectsList;
		}

		public List<TestExamBean> getBatchDetailsByMasterkeys(List<Long> timeboundIds, Integer acadYear, String acadMonth) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> batchList=new ArrayList<>();
			String commaSeparated = "''";
			for (int i = 0; i < timeboundIds.size(); i++) {
				if (i == 0) {
					commaSeparated = "'"
							+ timeboundIds.get(i) + "'";
				} else {
					commaSeparated = commaSeparated + ", '"
							+ timeboundIds.get(i) + "'";
				}
			}

			String sql = " SELECT b.* "
					+ "	FROM "
					+ " 	exam.batch b,"
					+ " 	lti.student_subject_config s"
					+ " where "
					+ "  s.id in ("+commaSeparated+") "
					+ "  and s.acadYear = ? "
					+ "  and s.acadMonth = ?"
					+ "  and b.id = s.batchId "
					+ " group by b.id ";


			
			try {
				batchList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {acadYear,acadMonth}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			return batchList;
		
			
}


		public ArrayList<String> getProgramSemSubjectIdsBySubjectNProgramConfig(String programId,
																				String programStructureId,
																				String consumerTypeId,
																				String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql =  "SELECT p_s_s.id "
					+ " FROM exam.consumer_program_structure as c_p_s, "
					+ "		 exam.program_sem_subject as p_s_s "
					+ "where c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId ";
			
			if("All".equalsIgnoreCase(subject)) {
				// Do nothing get all PSS IDs
			}else {
				sql += " and p_s_s.subject=?";
				parameters.add(subject);
				
			}
			Object[] args = parameters.toArray();
			
			ArrayList<String> programSemSubjectIds = new  ArrayList<>();
			try {
				programSemSubjectIds = (ArrayList<String>) jdbcTemplate.query(
						sql, args,  new SingleColumnRowMapper(
								String.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
			

			return programSemSubjectIds;
		}
		
		public ArrayList<String> getProgramSemSubjectIdsInTimeboundIdTable() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			
			String sql =  "select distinct prgm_sem_subj_id from lti.student_subject_config";
			
			
			ArrayList<String> programSemSubjectIds = new  ArrayList<>();
			try {
			programSemSubjectIds = (ArrayList<String>) jdbcTemplate.query(
			sql,   new SingleColumnRowMapper(
			String.class));
			} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			}
			

			
			return programSemSubjectIds;
			}


		/*public Map<String,String> getProgramIdNameMap(){
			
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			ArrayList<ConsumerProgramStructure> programList = null;
			
			Map<String,String> programIdNameMap = new HashMap<>();
			
			String sql =  "SELECT id,code as name FROM exam.program";
			
			try {
				programList = (ArrayList<ConsumerProgramStructure>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ConsumerProgramStructure.class));

				for(ConsumerProgramStructure c : programList) {
					programIdNameMap.put(c.getId(), c.getName());
				}

				
			} catch (Exception e) {
				
				
			}
			
			return programIdNameMap;  
		}*/

		
		public List<Long> getTimeboundIdsByProgramSemSubjectIds(ArrayList<String> programSemSubjectIds, Integer acadYear, String acadMonth) {
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			
			String sql =  "SELECT id FROM lti.student_subject_config "
					+ " where prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
					+ " and acadYear = "+acadYear+" and acadMonth = '"+acadMonth+"' ";


			List<Long> timeboundIds= new ArrayList<>();
			try {
				timeboundIds = (List<Long>) jdbcTemplate.query(
						sql,  new SingleColumnRowMapper(Long.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
			

			
			return timeboundIds;
		}
		public List<Long> getTimeboundIdsByProgramSemSubjectIdsNBatchId(ArrayList<String> programSemSubjectIds, Integer acadYear, String acadMonth, Integer batchId) {
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			
			String sql =  "SELECT id FROM lti.student_subject_config "
					+ " where prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
					+ " and acadYear = "+acadYear+" and acadMonth = '"+acadMonth+"' ";
			
			if(batchId == 0) {
				//Do nothing, we'll get All batches as "0" means "All" 
			}else {
				sql += " and batchId = "+batchId+" ";
			}


			List<Long> timeboundIds= new ArrayList<>();
			try {
				timeboundIds = (List<Long>) jdbcTemplate.query(
						sql,  new SingleColumnRowMapper(Long.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
			

			
			return timeboundIds;
		}

		public List<TestExamBean> getModulesDetailsByMasterkeys(List<Long> timeboundIds, Integer batchId) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> moduleList=new ArrayList<>();
			String commaSeparated = "''";
			for (int i = 0; i < timeboundIds.size(); i++) {
				if (i == 0) {
					commaSeparated = "'"
							+ timeboundIds.get(i) + "'";
				} else {
					commaSeparated = commaSeparated + ", '"
							+ timeboundIds.get(i) + "'";
				}
			}

			String sql = " SELECT  " + 
					"    s.subject,s.year,s.month,m.* " + 
					"FROM " + 
					"    acads.sessionplanid_timeboundid_mapping stm, " + 
					"    acads.sessionplan s, " + 
					"    acads.sessionplan_module m, " + 
					"    lti.student_subject_config t " + 
					"WHERE " + 
					"    t.id = stm.timeboundId " + 
					"        AND s.id = stm.sessionPlanId " + 
					"        AND s.id = m.sessionPlanId " + 
					"        AND t.id IN ("+commaSeparated+") "
					+ "		 AND t.batchId = "+batchId+""
					+ " GROUP BY m.id ";


			
			try {
				moduleList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			return moduleList;
		
			
		}

		public String insertTestIdNConfigurationMappings(final TestExamBean bean,final List<Long> configIds) {
			try {

				String sql = " INSERT INTO exam.test_testid_configuration_mapping "
						+ " (testId, type, referenceId, iaType, createdBy, createdDate) "
						+ " VALUES (?,?,?,?,?,sysdate()) "
						+ " "
						;
				
				int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {

						ps.setLong(1,bean.getId());
						ps.setString(2,bean.getApplicableType());
						
						if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
							ps.setLong(3,configIds.get(i));
							ps.setString(4,bean.getIaType()); 
						}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(3,configIds.get(i));
							ps.setString(4,bean.getIaType());
						}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(3,configIds.get(i));
							ps.setString(4,"Test"); //temporary
						}else {
							ps.setLong(3, (long)0);
						}
						ps.setString(5,bean.getCreatedBy());		
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
		
		/*
		acadYear, acadMonth, examYear, examMonth, liveType, applicableType, referenceId, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		 * */
		public String insertTestLiveConfig(final TestExamBean bean,final List<Long> configIds) {
			try {
				String sql = " INSERT INTO exam.test_live_settings "
						+ " (acadYear, acadMonth, examYear, examMonth, liveType, applicableType, referenceId, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
						+ " VALUES (?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
						+ " on duplicate key update "
						+ " acadYear=?, acadMonth=?, examYear=?, examMonth=?, createdBy=?, createdDate=sysdate(), lastModifiedBy=?, lastModifiedDate=sysdate()"
						;
				
				int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ps.setInt(1,bean.getAcadYear());
						ps.setString(2,bean.getAcadMonth());
						ps.setInt(3,bean.getExamYear());
						ps.setString(4,bean.getExamMonth());
						ps.setString(5,bean.getLiveType());
						ps.setString(6,bean.getApplicableType());
						
						if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
							ps.setLong(7,configIds.get(i));
						}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(7,configIds.get(i));
						}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(7,configIds.get(i));
						}else {
							ps.setLong(7, (long)0);
						}
						ps.setString(8,bean.getCreatedBy());
						ps.setString(9,bean.getLastModifiedBy());
						
						//on update
						ps.setInt(10,bean.getAcadYear());
						ps.setString(11,bean.getAcadMonth());
						ps.setInt(12,bean.getExamYear());
						ps.setString(13,bean.getExamMonth());
						ps.setString(14,bean.getCreatedBy());
						ps.setString(15,bean.getLastModifiedBy());
					
					}

					@Override
					public int getBatchSize() {
						return configIds.size();
					}
				  });

				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
				return "Error in insertTestLiveConfig : "+e.getMessage();
			}
		
		}

		
		public List<String> getSubjectsListByMastKeyAndBatch(String consumerTypeId, String programStructureId,
				String programId, Integer acadYear, String acadMonth, Integer referenceId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<String> subjectsList = new ArrayList<>();
			

			String sql =  "SELECT distinct p_s_s.subject FROM exam.consumer_program_structure as c_p_s, "
					+ "exam.program_sem_subject as p_s_s, "
					+ " lti.student_subject_config ssc "
					+ "where c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ " and ssc.prgm_sem_subj_id = p_s_s.id"
					+ " and ssc.acadYear ="+acadYear+" and acadMonth='"+acadMonth+"' ";
			if(referenceId == 0) {
				//Do nothing, As we'll get all subjects by program config
			}else {

				sql += " and batchId = "+referenceId+" ";
			}	


			try {
				 subjectsList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return subjectsList;
			
		}

		public List<Long> getBatchIdsByTimeboundId(List<Long> timeboundIds, Integer acadYear, String acadMonth,
				Integer referenceId) {
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			
			String commaSeparated = "''";
			for (int i = 0; i < timeboundIds.size(); i++) {
				if (i == 0) {
					commaSeparated = "'"
							+ timeboundIds.get(i) + "'";
				} else {
					commaSeparated = commaSeparated + ", '"
							+ timeboundIds.get(i) + "'";
				}
			}

			String sql = " SELECT b.id "
			+ "	FROM "
			+ " 	exam.batch b,"
			+ " 	lti.student_subject_config s"
			+ " where "
			+ "  s.id in ("+commaSeparated+") "
			+ "  and s.acadYear = ? "
			+ "  and s.acadMonth = ?"
			+ "  and b.id = s.batchId "
			+ " group by b.id ";



			List<Long> batchIds= new ArrayList<>();
			try {
				batchIds = (List<Long>) jdbcTemplate.query(
						sql,  new SingleColumnRowMapper(Long.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
			

			return timeboundIds;
		}


		public List<Long> getModuleIdByProgramConfigYearMonthBatchIdNId(String consumerTypeId, String programStructureId,
				String programId, Integer acadYear, String acadMonth, Integer referenceId,String subject, Integer batchId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<Long> moduleIdList = new ArrayList<>();
			

			String sql =  "SELECT distinct m.id "
					+ " FROM exam.consumer_program_structure as c_p_s, "
					+ "exam.program_sem_subject as p_s_s, "
					+ " lti.student_subject_config ssc ," + 
					"    acads.sessionplanid_timeboundid_mapping stm, " + 
					"    acads.sessionplan s, " + 
					"    acads.sessionplan_module m "  
					+ "where "
					+ "c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ " and ssc.prgm_sem_subj_id = p_s_s.id "+ 
					"    and ssc.id = stm.timeboundId " + 
					"    AND s.id = stm.sessionPlanId " + 
					"    AND s.id = m.sessionPlanId "  
					+ " "
					+ " and ssc.acadYear ="+acadYear+" and acadMonth='"+acadMonth+"' ";
			

			if(batchId == 0) {
				//Do nothing, As we'll get all subjects by program config
			}else {
				sql += " and ssc.batchId = "+batchId+" ";
			}
			
			
			if("All".equalsIgnoreCase(subject)) {
				//Do nothing
			}else {
				sql += " and p_s_s.subject = '"+subject+"' ";
			}
			
			if( referenceId != null) {
				if( referenceId == 0) {
					//Do nothing
				}else {
					sql += " and m.id = "+referenceId+" ";
				}
			}
			
			

			try {

				moduleIdList = (List<Long>) jdbcTemplate.query(sql, new SingleColumnRowMapper(Long.class));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}

			return moduleIdList;
			
		}

		public String deleteTestLiveConfig(TestExamBean test, List<Long> configIds) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_live_settings "
					+ " where acadYear = ?"
					+ " and acadMonth = ? "
					+ " and examYear = ?"
					+ " and examMonth = ? "
					+ " and liveType = ?"
					+ " and applicableType = ?"
					+ " and referenceId in ("+StringUtils.join(configIds, ", ")+") ";
			

			try {
				 row = jdbcTemplate.update(sql,
						 new Object[] {test.getAcadYear(),
								 	  test.getAcadMonth(),
								 	  test.getExamYear(),
								 	  test.getExamMonth(),
								 	  test.getLiveType(),
								 	  test.getApplicableType()});

			} catch (Exception e) {
				
				return "Error in delteing test live config."+e.getMessage();
			}
			return row+"";
			}



		public List<TestExamBean> getTestsBySapIdNModuleId(String userId, Integer referenceId) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			
			String sql = " SELECT t.*  " + 
					"FROM " + 
					"exam.test t " + 
					" " + 
					"inner join exam.test_testid_configuration_mapping tcm " + 
					"on  t.id = tcm.testId " + 
					" " + 
					"inner join exam.test_live_settings tls " + 
					"on  tls.referenceId = tcm.referenceId   and tls.applicableType = tcm.type " + 
					" " + 
					"inner join acads.sessionplan_module m " + 
					"on  m.id = tcm.referenceId " + 
					" " + 
					"inner join acads.sessionplan s " + 
					"on  s.id = m.sessionPlanId " + 
					" " + 
					"inner join acads.sessionplanid_timeboundid_mapping stm " + 
					"on   stm.sessionPlanId = s.id " + 
					" " + 
					"inner join lti.student_subject_config ssc " + 
					"on   ssc.id = stm.timeboundId " + 
					" " + 
					"inner join lti.timebound_user_mapping tum " + 
					"on   tum.timebound_subject_config_id = ssc.id  " + 
					"  " + 
					" where  " + 
					"   tls.liveType = 'Regular' " + 
					"   and tls.applicableType = 'module' " + 
					"   and m.id = "+referenceId +   
					"   and tum.userId = '"+userId+"' " + 
					"   and tum.role = 'Student' " + 
					"";


			
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}

				
			return testsList;
		
			
}

	public boolean checkIfAnyTestsActive(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT   " + 
                "    count(*)  " + 
                "FROM  " + 
                "    exam.students s  " + 
                "        INNER JOIN  " + 
                "    lti.timebound_user_mapping tum ON s.sapid = tum.userId  " + 
                "        INNER JOIN  " + 
                "    acads.sessionplanid_timeboundid_mapping stm ON stm.timeboundId = tum.timebound_subject_config_id  " + 
                "        INNER JOIN  " + 
                "    acads.sessionplan s ON s.id = stm.sessionPlanId  " + 
                "        INNER JOIN  " + 
                "    acads.sessionplan_module m ON s.id = m.sessionPlanId  " + 
                "        INNER JOIN  " + 
                "    exam.test_live_settings tls ON tls.referenceId = m.id  " + 
                "        INNER JOIN  " + 
                "    exam.test_testid_configuration_mapping tcm ON tls.referenceId = tcm.referenceId  and tls.applicableType = tcm.type  " + 
                "        INNER JOIN  " + 
                "    exam.test t ON t.id = tcm.testId  " + 
                "WHERE  " + 
                "    tls.liveType = 'Regular'  " + 
                "    AND tls.applicableType = 'module'  " + 
                "        AND s.sapid = ? ";

		int numberOfActiveTests = jdbcTemplate.queryForObject(sql, new Object[] { sapid }, Integer.class);
		
		if(numberOfActiveTests == 0) {
			return false;
		}
		return true;
	}
		public List<TestExamBean> getSubjectsForIAResultsBySapid(String userId) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=new ArrayList<>();
			
			String sql = " SELECT pss.subject,pss.sem,ssc.*  " + 
					"FROM lti.timebound_user_mapping tum  " + 
					" inner join " + 
					" lti.student_subject_config ssc  " + 
					" on tum.timebound_subject_config_id = ssc.id " + 
					"  " + 
					" inner join " + 
					" exam.program_sem_subject pss  " + 
					" on pss.id = ssc.prgm_sem_subj_id " + 
					"  " + 
					"where  " + 
					" tum.userId = ? " + 
					/* Order the subjects in order of applicability */
					" ORDER BY startDate ;";


			
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {userId}, new BeanPropertyRowMapper(TestExamBean.class));


			} catch (Exception e) {
				
			}


				
			return testsList;
		
			
}
		public TestExamBean getSubjectForIAResultsBySapidAndTimeboundId(String userId, String timeboundId) {
					
					
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = " SELECT pss.subject,ssc.*  " + 
							"FROM lti.timebound_user_mapping tum  " + 
							" inner join " + 
							" lti.student_subject_config ssc  " + 
							" on tum.timebound_subject_config_id = ssc.id " + 
							"  " + 
							" inner join " + 
							" exam.program_sem_subject pss  " + 
							" on pss.id = ssc.prgm_sem_subj_id " + 
							"  " + 
							"where  " + 
							" tum.userId = ? AND ssc.id = ? LIMIT 1";
		
		
					
					try {
						 return (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {userId, timeboundId}, new BeanPropertyRowMapper<TestExamBean>(TestExamBean.class));


					} catch (Exception e) {
						
					}

					return null;
				
					
		}
		
		//id, sessionPlanId, sessionModuleNo, topic, outcomes, pedagogicalTool, chapter, createdBy, lastModifiedBy, createdDate, lastModifiedDate
		public List<StudentsTestDetailsExamBean> getApplicableTestsWithAttemptDetailsBySapidNSubject(TestExamBean subject, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*,"
					+ " m.id as referenceId, m.sessionPlanId, m.sessionModuleNo, m.topic, m.outcomes, m.pedagogicalTool, m.chapter,"
					+ " COALESCE(tst.attemptStatus,'Not-Attempted') as attemptStatus   " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId and tls.applicableType = tcm.type  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? "
					+ "		 AND ssc.id = ?  "
					+ "		AND TRIM(m.topic) <> 'Generic Module For Session Plan' " + 
					"					 ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,subject.getId()}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));


			} catch (Exception e) {
				
			}

			return testsByStudent;
		}
		
		public List<StudentsTestDetailsExamBean> getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(Long timeboundId, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT "
					+ " COALESCE(tst.attempt,0) as attempt, "
					+ " tst.testStartedOn, "
						+ " COALESCE(tst.remainingTime,0) as remainingTime, "
						+ " tst.testEndedOn, "
						+ " tst.testCompleted, "
						+ " tst.attemptStatus, "
						+ " COALESCE(tst.score,0) as score, "
						+ " COALESCE(tst.score,0) as scoreInInteger, "
						+ " COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , "
						+ " COALESCE(tst.currentQuestion,0) as currentQuestion, "
						+ " t.*,"
						+ " m.id as referenceId, "
						+ " m.sessionPlanId, "
						+ " m.sessionModuleNo, "
						+ " m.topic, "
						+ " m.outcomes, "
						+ " m.pedagogicalTool, "
						+ " m.chapter "
					+ " FROM "
					+ " exam.test t "
						+ " INNER JOIN "
							+ " exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId "
						+ " INNER JOIN "
							+ " exam.test_live_settings tls ON tls.referenceId = tcm.referenceId and tls.applicableType = tcm.type "
						+ " INNER JOIN "
							+ " acads.sessionplan_module m ON m.id = tcm.referenceId "
						+ " INNER JOIN "
							+ " acads.sessionplan s ON s.id = m.sessionPlanId "
						+ " INNER JOIN "
							+ " acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id "
						+ " INNER JOIN "
							+ " lti.student_subject_config ssc ON ssc.id = stm.timeboundId "
						+ " INNER JOIN "
							+ " lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id "
						+ " LEFT JOIN "
							+ " exam.test_student_testdetails tst ON tst.sapid = tum.userId "
							+ " AND tst.testId = t.id "
					+ " WHERE "
							+ " tls.liveType = 'Regular' "
						+ " AND tls.applicableType = 'module' "
						+ " AND tum.userId = ? "
						+ " AND tum.role = 'Student' "
						+ " AND ssc.id = ? "
						+ " AND TRIM(m.topic) <> 'Generic Module For Session Plan' "
						
						/* Check if we we can show results for this test. */
						+ " AND t.showResultsToStudents = 'Y' "
						+ " AND sysdate() > t.endDate ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid, timeboundId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));


			} catch (Exception e) {
				
			}

			return testsByStudent;
		}

		public List<StudentsTestDetailsExamBean> getMbaXApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(Long timeboundId, String sapid) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=new ArrayList<>();
			
			String sql="SELECT  "
					+ "    t.id AS id, "
					+ "    COALESCE(tst.attempt, 0) AS attempt, "
					+ "    tst.testStartedOn, "
					+ "    COALESCE(tst.remainingTime, 0) AS remainingTime, "
					+ "    tst.testEndedOn, tst.testCompleted, "
					+ "    COALESCE(tst.score, 0) AS score, "
					+ "    COALESCE(tst.score, 0) AS scoreInInteger, "
					+ "    COALESCE(tst.noOfQuestionsAttempted, 0) AS noOfQuestionsAttempted, "
					+ "    t.*, m.sessionModuleNo AS referenceId, m.sessionPlanId, m.sessionModuleNo, "
					+ "    m.topic, m.outcomes, m.pedagogicalTool, m.chapter, ssc.acadYear AS acadsYear, ssc.acadMonth AS acadsMonth "
					+ "FROM "
					+ "    exam.upgrad_test t "
					+ "        INNER JOIN "
					+ "    exam.upgrad_test_testid_configuration_mapping tcm ON t.id = tcm.testId "
					+ "        INNER JOIN "
					+ "    acads.upgrad_sessionplan_module m ON m.id = tcm.referenceId "
					+ "        INNER JOIN "
					+ "    acads.upgrad_sessionplan s ON s.id = m.sessionPlanId "
					+ "        INNER JOIN "
					+ "    acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id "
					+ "        INNER JOIN "
					+ "    lti.student_subject_config ssc ON ssc.id = stm.timeboundId "
					+ "        INNER JOIN "
					+ "    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id "
					+ "        INNER JOIN "
					+ "    exam.upgrad_student_assesmentscore tst ON tst.sapid = tum.userId "
					+ "        AND tst.testId = t.id "
					+ "WHERE "
					+ "    t.applicableType = 'module' "
					+ "        AND tum.userId = ? "
					+ "        AND ssc.id = ? "
					+ "        AND TRIM(m.topic) <> 'Generic Module For Session Plan' "
					+ "        AND t.showResultsToStudents = 'Y' "
					+ "        AND SYSDATE() > t.endDate;";
			
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid, timeboundId}, 
						new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));

			} catch (Exception e) {
				
			}

			return testsByStudent;
		}
		
		public TestExamBean getExtendedTimeBySapidNUserId(String userId, Long id) {
			TestExamBean extendedTime = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = "select * from exam.test_testextended_sapids where sapid = "+userId+" and testId = "+id+" limit 1";

				extendedTime = (TestExamBean)jdbcTemplate.queryForObject(sql,new Object[] {},new BeanPropertyRowMapper(TestExamBean.class) );
			}
			catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+"IN getExtendedTimeBySapidNUserId got id "+id+" userId: "+userId+", Error :"+e.getMessage());
				
			}
			return extendedTime;
		}

		public List<TestExamBean> getExtendedStartEndTimeSapidListByTestId(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=null;
			String sql="select * from exam.test_testextended_sapids where testId = ? ";
			try {
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		public int deleteExtendedTestTmeByTestIdNSapid(Long id,String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_testextended_sapids where testId=? and sapid = ?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id,sapid});

			} catch (Exception e) {
				
			}
			return row;
			}

		public List<TestExamBean> getApplicableTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? "
					+ "      AND tum.role = 'Student' "
					+ "		 AND ssc.id in("+id+")  and sysdate() between ssc.startDate and ssc.endDate" + 
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}
			

			return testsByStudent;
		}

		public List<TestExamBean> getTestsScheduledForTomorrowNEmailNotSent() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsScheduledForTomorrow= new ArrayList<>();
			String sql="SELECT  " + 
					"    * " + 
					"FROM " + 
					"    exam.test " + 
					"WHERE " + 
					"    DATEDIFF(startDate, SYSDATE()) = 1 " + 
					"    AND (emailSent <> 'Y' OR emailSent IS NULL); ";
			try {
				testsScheduledForTomorrow = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}

			return testsScheduledForTomorrow;
		}
		public List<TestExamBean> getTestsScheduledForTomorrowNSMSNotSent() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsScheduledForTomorrow= new ArrayList<>();
			String sql="SELECT  " + 
					"    * " + 
					"FROM " + 
					"    exam.test " + 
					"WHERE " + 
					"    DATEDIFF(startDate, SYSDATE()) = 1 " + 
					"    AND (smsSent <> 'Y' OR smsSent IS NULL); ";
			try {
				testsScheduledForTomorrow = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}

			return testsScheduledForTomorrow;
		}

		public List<StudentExamBean> getStudentsEligibleForTestByTestid(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentExamBean> studentsEligibleForTest = new ArrayList<>();
			String sql=" SELECT   " + 
					"    s.*  " + 
					"FROM  " + 
					"    exam.students s  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON s.sapid = tum.userId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.timeboundId = tum.timebound_subject_config_id  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = stm.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = m.id  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON tls.referenceId = tcm.referenceId  and tls.applicableType = tcm.type  " + 
					"        INNER JOIN  " + 
					"    exam.test t ON t.id = tcm.testId  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"    AND tls.applicableType = 'module'  " + 
					"        AND t.id = ? ";
			try {
				studentsEligibleForTest = (List<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(StudentExamBean.class));
			} catch (Exception e) {
				
			}

			return studentsEligibleForTest;

		}

		public void updateMCQInPost(final TestExamBean test) throws ParseException{
	  
				jdbcTemplate = new JdbcTemplate(dataSource);
				GeneratedKeyHolder holder = new GeneratedKeyHolder();
				
				//get timebound id to insert in post table
				final Long timeBoundId = getTimeBoundIdByTestId(test.getId());
		
				// updating post for mcq
				String sql="update lti.post set "
						+ " userId=?, "
						+ " subject_config_id=?, "
						+ "role=?, " 
						+ "type=?, " 
						+ "content=?, "
						+ "acadYear=?, "
						+ "acadMonth=?, "
						+ "examYear=?, "
						+ "examMonth=?, "
						+ "scheduledDate=DATE_SUB(?, INTERVAL 1 HOUR), "
						+ " scheduleFlag=?, "
						+ "lastModifiedBy=?, " 
						+ "lastModifiedDate=sysdate(), "     
						+ "subject=?, "
						+ "startDate=?, "
						+ "endDate=?, " 
						+ "duration=? "
						+ "where referenceId=? ";   
		try {
		
			jdbcTemplate.update(sql,new Object[] {test.getFacultyId(),timeBoundId,"Faculty",
					"MCQ",test.getTestName(),
					test.getYear(),test.getAcadMonth(),
					test.getYear(),test.getMonth(),test.getStartDate(), 
					"Y",test.getCreatedBy(),
					test.getSubject(),test.getStartDate(),test.getEndDate(),
					test.getDuration(),
												  test.getId()
								});
			//
				
		}catch (Exception e) {
			
		}
		}
		public Long getTimeBoundIdByTestId(Long id ){
			Long timeboundId= null;
			try { 
				String sql3 = "select stm.timeboundId from acads.sessionplanid_timeboundid_mapping stm,"
										+ "  acads.sessionplan s, acads.sessionplan_module m,exam.test_testid_configuration_mapping tcm,exam.test t " 
										+ " where stm.sessionPlanId = s.id and  s.id = m.sessionPlanId and m.id = tcm.referenceId and t.id = tcm.testId and t.id= ? ";   
			        
				timeboundId = (Long) jdbcTemplate.queryForObject(sql3, new Object[] {id}, Long.class); 

				
			} catch (Exception e) {
				
			}
			return timeboundId;
		}
		
		
		public List<TestExamBean> getApplicableDueTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? " +
					"        AND tum.role = 'Student' " +
					"		 AND ssc.id = ?  " + 
					"		 AND sysdate() <= ssc.endDate  " + 
					"		 AND sysdate() <= t.endDate  " +
					"		 AND tst.testCompleted is null  " +
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,id}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}

			return testsByStudent;
		}
		public List<TestExamBean> getApplicablePendingTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? " +
					"        AND tum.role = 'Student' " +
					"		 AND ssc.id = ?  " + 
					"		 AND sysdate() between ssc.startDate and ssc.endDate  " + 
					"		 AND sysdate() <= t.endDate  " +
					"		 AND (tst.testCompleted is null || tst.testCompleted ='N')  " +
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,id}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}

			return testsByStudent;
		}

		public List<TestExamBean> getOngoingTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        INNER JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? " +
					"        AND tum.role = 'Student' " +
					"		 AND ssc.id = ?  " + 
					"		 AND sysdate() between ssc.startDate and ssc.endDate  " + 
					"		 AND sysdate() >= t.endDate  " +
					"		 AND sysdate() <= DATE_ADD(t.endDate, INTERVAL t.duration MINUTE)  " +
					"		 AND (tst.testCompleted is null || tst.testCompleted ='N')  " +
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,id}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}
		

		public List<TestExamBean> getExtendedTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*,"
					+ " tes.extendedStartTime as startDate, tes.extendedEndTime as endDate  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        INNER JOIN  " + 
					"    exam.test_testextended_sapids tes ON tum.userId = tes.sapid  " + 
					"        AND t.id = tes.testId  " +
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? " +
					"        AND tum.role = 'Student' " +
					"		 AND ssc.id = ?  " + 
					"		 AND sysdate() between ssc.startDate and ssc.endDate  " + 
					"		 AND sysdate() <= DATE_ADD(tes.extendedEndTime, INTERVAL t.duration MINUTE)  " +
					"		 AND (tst.testCompleted is null || tst.testCompleted ='N')  " +
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,id}, new BeanPropertyRowMapper(TestExamBean.class));
				/*
				for(TestBean t  : testsByStudent) {
				}
				*/
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}
		
		
		public List<TestExamBean> getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject_todo(Long id, String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsByStudent=new ArrayList<>();
			String sql="SELECT   " + 
					"  COALESCE(tst.attempt,0) as attempt,  tst.testStartedOn,  COALESCE(tst.remainingTime,0) as remainingTime,  " + 
					" tst.testEndedOn, tst.testCompleted, COALESCE(tst.score,0) as score, COALESCE(tst.score,0) as scoreInInteger,    " + 
					" COALESCE(tst.noOfQuestionsAttempted,0) as noOfQuestionsAttempted , COALESCE(tst.currentQuestion,0) as currentQuestion ,  " + 
					" t.*  " + 
					"FROM  " + 
					"    exam.test t  " + 
					"        INNER JOIN  " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
					"        INNER JOIN  " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
					"        INNER JOIN  " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
					"        INNER JOIN  " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
					"        INNER JOIN  " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " + 
					"        LEFT JOIN  " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId  " + 
					"        AND tst.testId = t.id  " + 
					"WHERE  " + 
					"    tls.liveType = 'Regular'  " + 
					"        AND tls.applicableType = 'module'  " + 
					"        AND tum.userId = ? " +
					"        AND tum.role = 'Student' " +
					"		 AND ssc.id = ?  " + 
					"		 AND sysdate() between ssc.startDate and ssc.endDate  " + 
					"		 AND (sysdate() > t.endDate ||  tst.testCompleted ='Y')  " +
					"					; ";
			try {
				testsByStudent = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,id}, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (Exception e) {
				
			}

			return testsByStudent;
		}
		
		public String insertTestLiveConfig2(final TestExamBean bean,final List<Long> configIds) {
			try {
				String sql = " INSERT INTO exam.test_live_settings "
						+ " (acadYear, acadMonth, examYear, examMonth, liveType, applicableType, referenceId, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
						+ " VALUES (?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
						+ " on duplicate key update "
						+ " acadYear=?, acadMonth=?, examYear=?, examMonth=?, createdBy=?, createdDate=sysdate(), lastModifiedBy=?, lastModifiedDate=sysdate()"
						;
				
				int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ps.setInt(1,bean.getAcadYear());
						ps.setString(2,bean.getAcadMonth());
						ps.setInt(3,bean.getExamYear());
						ps.setString(4,bean.getExamMonth());
						ps.setString(5,bean.getLiveType());
						ps.setString(6,bean.getApplicableType());
						
						if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
							ps.setLong(7,configIds.get(i));
						}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(7,configIds.get(i));
						}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
							ps.setLong(7,configIds.get(i));
						}else {
							ps.setLong(7, (long)0);
						}
						ps.setString(8,bean.getCreatedBy());
						ps.setString(9,bean.getLastModifiedBy());
						
						//on update
						ps.setInt(10,bean.getAcadYear());
						ps.setString(11,bean.getAcadMonth());
						ps.setInt(12,bean.getExamYear());
						ps.setString(13,bean.getExamMonth());
						ps.setString(14,bean.getCreatedBy());
						ps.setString(15,bean.getLastModifiedBy());
					
					}

					@Override
					public int getBatchSize() {
						return configIds.size();
					}
				  });

				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
				return "Error in insertTestLiveConfig : "+e.getMessage();
			}
			
		
		}
		
		public List<TestExamBean> getTestListDetailsByConfigIdList(String commaSeperatedConfigList) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testList=new ArrayList<>();
			String sql="SELECT * FROM exam.test_testid_configuration_mapping e,acads.sessionplan_module m where e.type='module' AND e.referenceId in ("+commaSeperatedConfigList+") AND e.referenceId = m.id AND m.topic <> 'Generic Module For Session Plan'";


			try {
				testList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));



			} catch (Exception e) {
				
			}

			return testList;
		}



		public List<StudentQuestionResponseExamBean> getAnswersToBeEvaluatedByFacultyIdNTestId(String userId, Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
			

			/* Previous code
			String sql="select a.*,q.question, t.testName, q.marks as questionMarks "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t,"
					+ "		 exam.test_student_testdetails tst "
					+ " where a.testId=? "
					+ "  and q.type=4 "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.isChecked = 0 "
					+ "  and a.facultyId=? "
					+ "  and a.testId = t.id "
					+ "  and tst.sapid = a.sapid "
					+ "  and tst.testId = t.id "
					+ "  and tst.attempt =a.attempt "
					+ "  and (tst.attemptStatus <> 'CopyCase' or tst.attemptStatus is null) "
					+ "  Order By a.id ";
			

		 * String
		 * sql="select a.*,q.question, t.testName, q.marks as questionMarks, q.type " +
		 * " from exam.test_students_answers a , " + "		 exam.test_questions q, " +
		 * "		 exam.test t" + " where a.testId=? " + "  and q.type in (4,8) " +
		 * "  and a.questionId=q.id " + "  and a.testId=q.testId " +
		 * "  and a.isChecked = 0 " + "  and a.facultyId=? " + "  and a.testId = t.id "
		 * + "  Order By a.id ";
		 */
			String sql="select a.*,q.question, t.testName, q.marks as questionMarks, q.type ,tqa.url, q.uploadType" + 
					"					 from exam.test_students_answers a  " + 
					"					 INNER JOIN exam.test_questions q on a.questionId=q.id  " + 
					"					 INNER JOIN exam.test t on a.testId = t.id" + 
					"					 LEFT JOIN exam.test_question_additionalcontent tqa on  tqa.questionId = q.id" + 
					"					 where a.testId=?" + 
					"    				 AND a.sapid NOT LIKE '777777%' "+
					"					  and q.type in (4,8) " + 
					"					  and a.testId=q.testId " + 
					"					  and a.isChecked = 0 " + 
					"					  and a.facultyId=? " + 
					"					  Order By a.id ";

			
			try {
				answers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {id,userId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (Exception e) {
				
			}
			return answers;
		}

		public List<StudentQuestionResponseExamBean> getEvaluatedAnswersByFacultyIdNTestId(String userId, Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();

			//attemptStatus
			String sql = "SELECT " +
		    "a.*, " +
		    "q.question, " +
		    "t.testName, " +
		    "q.marks AS questionMarks, " +
		    "q.type, " +
		    "tqa.url, " +
		    "q.uploadType " +
		"FROM " +
		    "exam.test_students_answers a " +
		        "INNER JOIN " +
		    "exam.test_questions q ON a.questionId = q.id " +
		        "AND a.testId = q.testId " +
		        "INNER JOIN " +
		    "exam.test t ON a.testId = t.id " +
		        "INNER JOIN " +
		    "exam.test_student_testdetails tst ON tst.sapid = a.sapid " +
		        "AND tst.testId = t.id " +
		        "AND tst.attempt = a.attempt " +
		        "LEFT JOIN " +
		    "exam.test_question_additionalcontent tqa ON tqa.questionId = q.id " +
		"WHERE " +
		    "a.testId = ? AND q.type IN (4 , 8) " +
		        "AND a.isChecked = 1 " +
		        "AND a.facultyId = ? " +
		        "AND (tst.attemptStatus <> 'CopyCase' " +
		        "OR tst.attemptStatus IS NULL) " +
		"ORDER BY a.id ";
			
			
			try {
				answers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {id,userId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (Exception e) {
				
			}
			return answers;
		}
		

		public List<StudentQuestionResponseExamBean> getCopyCaseAnswersByFacultyIdNTestId(String userId, Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();

			/*
			String sql="select a.*,q.question, t.testName, q.marks as questionMarks "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t,"
					+ "		 exam.test_student_testdetails tst "
					+ " where a.testId=? "
					+ "  and q.type=4 "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.isChecked = 1 "
					+ "  and a.facultyId=? "
					+ "  and a.testId = t.id "
					+ "  and a.remark like 'Marked For Copy Case%' "
					+ "  and tst.sapid = a.sapid "
					+ "  and tst.testId = t.id "
					+ "  and tst.attempt =a.attempt "
					+ "  and (tst.attemptStatus = 'CopyCase') "
					+ "  Order By a.id ";
			

			
		 * String sql="select a.*,q.question, t.testName, q.marks as questionMarks " +
		 * " from exam.test_students_answers a , " + "		 exam.test_questions q, " +
		 * "		 exam.test t" + " where a.testId=? " + "  and q.type in (4,8) " +
		 * "  and a.questionId=q.id " + "  and a.testId=q.testId " +
		 * "  and a.isChecked = 1 " + "  and a.facultyId=? " + "  and a.testId = t.id "
		 * + "  Order By a.id ";
		 */
			String sql="select a.*,q.question, t.testName, q.marks as questionMarks, q.type ,tqa.url,q.uploadType" + 
					"					 from exam.test_students_answers a  " + 
					"					 INNER JOIN exam.test_questions q on a.questionId=q.id  " + 
					"					 INNER JOIN exam.test t on a.testId = t.id"
					+ "					 INNER JOIN exam.test_student_testdetails tst on tst.testId=t.id and a.sapid=tst.sapid and a.attempt=tst.attempt " + 
					"					 LEFT JOIN exam.test_question_additionalcontent tqa on  tqa.questionId = q.id" + 
					"					 where a.testId=?" + 
					"					  and q.type in (4,8) " + 
					"					  and a.testId=q.testId " + 
					"					  and a.isChecked = 1 " + 
					"					  and a.facultyId=? " + 
					 "  				  and (tst.attemptStatus = 'CopyCase') " +
					"					  Order By a.id ";

			
			try {
				answers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {id,userId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (Exception e) {
				
			}
			return answers;
		}


		public double caluclateTestScore(String sapid, Long testId) {
			double score=0.0;
			TestExamBean test = getTestById(testId);
			List<Integer> bodQuestionIdList = getBodQuestionsByTestIdActive(testId, BOD_ACTIVE_TRUE);
			
			StudentsTestDetailsExamBean studentsTestDetails =  getStudentsTestDetailsBySapidAndTestId(sapid, testId);
			
			if(studentsTestDetails !=null) {
				if("CopyCase".equalsIgnoreCase(studentsTestDetails.getAttemptStatus()) ) {
					return score;
				}
			}
			
			List<TestQuestionExamBean> testQuestions = getStudentSpecifcTestQuestions(studentsTestDetails.getTestQuestions());
			HashMap<Long,List<StudentQuestionResponseExamBean>> questionIdAndAnswersByStudentMap =  getTestAnswerBySapid(sapid, testId,studentsTestDetails.getAttempt());
			for(TestQuestionExamBean question : testQuestions){
				if(bodQuestionIdList.contains(question.getId().intValue())) {
					score += question.getMarks();
					continue;
				}
				
				List<StudentQuestionResponseExamBean> answers = questionIdAndAnswersByStudentMap.get(question.getId());
				if(answers!=null) {
					if(question.getType() == 3 ) { // 3 is for CaseStudy
						score+=checkType3Question(question, questionIdAndAnswersByStudentMap);
					}else if(question.getType() == 2 || question.getType() == 6 || question.getType() == 7  ) { //  for MULTISELECT,Image,Video 
						score+=checkType1n2Question(question,answers);
					}
					else if(question.getType() == 1 || question.getType() == 5 ) { // 1 is for SINGLESELECT, 5 for TrueOrFalse
						if(answers.size()==1) {
							score+=checkType1n2Question(question,answers);
						}
					}
					else if(question.getType() == 4 || question.getType() == 8  ) { // 4 is for DESCRIPTIVE
						if(answers.size()==1) {

							score+=answers.get(0).getMarks();

						}
					}
					
					
				}


			}

			if(score > test.getMaxScore()) {
				score = test.getMaxScore();
			}
			return score;
		}
		
		
		public double checkType1n2Question(TestQuestionExamBean question, List<StudentQuestionResponseExamBean> answers) {
			
			int noOfCorrectOptions=0;
			int noOfCorrectAnswers=0;
			int noOfAnswers = answers!=null ? answers.size() : 0;

			try {
				for(TestQuestionOptionExamBean option : question.getOptionsList()) {
					if("Y".equalsIgnoreCase(option.getIsCorrect())) {
						noOfCorrectOptions++;
					}
					for(StudentQuestionResponseExamBean answer : answers) {
									
						if( (option.getId()==Long.parseLong(answer.getAnswer())) && ("Y".equalsIgnoreCase(option.getIsCorrect())) ) {
							noOfCorrectAnswers++;
						}
					}
				}

				if(noOfCorrectAnswers==noOfCorrectOptions && noOfAnswers==noOfCorrectOptions) {

					return question.getMarks();
					
				}
			} catch (NumberFormatException e) {
				
			}
		
			return 0.0 ;
		}
		
		//checkType3Question start
		public double checkType3Question(TestQuestionExamBean question, HashMap<Long,List<StudentQuestionResponseExamBean>> questionIdAndStudentsAnswuersMap) {
			try {
				List<TestQuestionExamBean> subQuestions = question.getSubQuestionsList();
				StudentQuestionResponseExamBean mainAnswer = questionIdAndStudentsAnswuersMap.get(question.getId()).get(0);
				double marksOfQuestion = question.getMarks();
				double calculatedMarks = 0;
				

				if("attempted".equalsIgnoreCase(mainAnswer.getAnswer())) {
					for(TestQuestionExamBean bean : subQuestions) {
						if(bean.getType()==1 || bean.getType()==2) {
							calculatedMarks+=checkType1n2Question(bean,questionIdAndStudentsAnswuersMap.get(bean.getId()));
						}

					}

					if(marksOfQuestion<calculatedMarks) {
						return marksOfQuestion;
					}else {
						return calculatedMarks; 
					}
				}else {
				return 0;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				return 0;
			}
		}
		//checkType3Question end
		
		//method to set object for logErrorsREST
		public void setObjectAndCallLogError(String stackTrace,String sapid) {
			String st = stackTrace.replaceAll("\"", " ");
			st = st.replaceAll("\'", " ");
			st = st.replaceAll("'", " ");
			st = st.replaceAll(",", " ");
			

			String data = "{" +
					"stackTrace :'" + st + "'," +
					"sapid :" + sapid + "," +
					"module:" + "'Test'"  +
			"}";

			JsonObject analyticsObject = new JsonParser().parse(data).getAsJsonObject();

			logErrorsREST(analyticsObject, "studentportal/m/logError");
		}
		
//		rest api for catching test errors
		@Async
		public void logErrorsREST(JsonObject parametersFromApi,String apiPath) {
			
			CloseableHttpClient client = HttpClientBuilder.create().build();
			String url = SERVER_PATH + apiPath;

			List<ExamAnalyticsObject> analyticsObject = new ArrayList<>();
			
			try {
				HttpHeaders headers =  new HttpHeaders();
				headers.add("Content-Type", "application/json");
				
				RestTemplate restTemplate = new RestTemplate();
				HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

				Gson gson= new Gson();
				analyticsObject.add(gson.fromJson(parametersFromApi, ExamAnalyticsObject.class));
				ExamAnalyticsObject response = restTemplate.postForObject(url, analyticsObject.get(0), ExamAnalyticsObject.class);

			}catch(Exception e) {

					
				}
			finally{
				     //Important: Close the connect
					 try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
					}
				 }
				
		}

		
		
		public List<TestExamBean> getTestsBySapIdNTimeBoundIds(String userId) {
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList = new ArrayList<TestExamBean>();
			
//			String sql = "SELECT tst.*, t.startDate, t.endDate, t.testName,t.facultyId  FROM exam.test_student_testdetails tst " + 
//					" INNER JOIN exam.test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId" + 
//					" INNER JOIN acads.sessionplan_module spm ON spm.id = ttcm.referenceId" + 
//					" INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId " + 
//					" INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId " + 
//					" INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id"+ 
//					" INNER JOIN exam.test t ON t.id= tst.testId" + 
//					" WHERE stm.timeboundId in (SELECT tum.timebound_subject_config_id FROM lti.timebound_user_mapping tum" + 
//					" INNER JOIN lti.student_subject_config  ssc ON ssc.id=tum.timebound_subject_config_id" + 
//					" WHERE   tum.userId = ? and role='Student') and tst.sapid=?;";

			String sql = "  SELECT  " + 
					"    CASE " + 
					" WHEN current_timestamp() > ifnull(tts.extendedEndTime ,t.endDate)  && tst.attemptStatus is null "+
					" THEN 'Not Attempted' " + 
					" WHEN current_timestamp() < ifnull(tts.extendedEndTime ,t.endDate)  && tst.attemptStatus is null "+
					" THEN 'Upcoming' " + 
					" WHEN tst.testCompleted = 'N' && DATE_ADD(tst.testStartedOn, INTERVAL t.duration MINUTE) > current_timestamp() "+
					" THEN 'Resume' "+
					" ELSE tst.attemptStatus " + 
					" END AS attemptStatus, t.id as testId, "+
					" t.subject, ifnull(tts.extendedStartTime,  t.startDate) as startDate, ifnull(tts.extendedEndTime, t.endDate) as endDate, t.testName, "+
					" IFNULL(tst.score, 0) AS score, tst.showResult, t.maxScore, "+
					" t.showResultsToStudents, t.duration, t.testType, t.referenceId " + 
					"FROM " + 
					"    exam.test t " + 
					"        INNER JOIN " + 
					"    exam.test_testid_configuration_mapping ttcm ON t.id = ttcm.testId " + 
					"        INNER JOIN " + 
					"    exam.test_live_settings tls ON tls.referenceId = ttcm.referenceId  " + 
					"        INNER JOIN " + 
					"    acads.sessionplan_module spm ON spm.id = tls.referenceId " + 
					"        INNER JOIN " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = spm.sessionPlanId " + 
					"        LEFT JOIN " + 
					"    (select * from exam.test_student_testdetails where sapid = ?) tst ON tst.testId = t.id  " + 
					"    	 LEFT JOIN " + 
					"    (SELECT * FROM exam.test_testextended_sapids where sapid = ? ) tts ON tts.testId = t.id "+
					"WHERE " + 
					"    stm.timeboundId IN (SELECT  " + 
					"            tum.timebound_subject_config_id " + 
					"        FROM " + 
					"            lti.timebound_user_mapping tum " + 
					"                INNER JOIN " + 
					"            lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " + 
					"        WHERE " + 
					"            tum.userId = ? " + 
					"                AND role = 'Student') ";

			
				 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {userId,userId,userId}, new BeanPropertyRowMapper(TestExamBean.class));
				 return testsList;
			
			}



		public MBAWXPassFailStatus attemptedTestsBySapidNTimeboundId(String sapid, Long id) {
			MBAWXPassFailStatus  attemptedTest = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = " "
						+ " SELECT "
							+ " `pf`.*, "
							+ " `s`.`max_score` AS `max_score`, "
							+ " `history`.`score` AS `oldScore`,"
							+ " `history`.`max_score` AS `oldMaxScore`, "
							+ " `history`.`status` AS `oldStatus` "
						+ " FROM `exam`.`mba_passfail` `pf` "
						+ " LEFT JOIN `exam`.`exams_schedule` `s` ON `s`.`schedule_id` = `pf`.`schedule_id` "
						
						+ " LEFT JOIN ( "
							+ " SELECT "
								+ " `tmh`.`score`, "
								+ " `s`.`max_score` AS `max_score`, "
								+ " `tmh`.`status`, "
								+ " `tmh`.`sapid`, "
								+ " `tmh`.`timebound_id`, "
								+ " `tmh`.`schedule_id` "
							+ " FROM `exam`.`tee_marks_history` `tmh` "
							+ " LEFT JOIN `exam`.`exams_schedule` `s` ON `s`.`schedule_id` = `tmh`.`schedule_id` "
							+ " WHERE `s`.`max_score` = 30 "
							/* get only the latest */
							+ " ORDER BY `tmh`.`created_at` desc "
						+ " ) `history` "
						+ " ON "
							+ " `history`.`sapid` = `pf`.`sapid` "
						+ " AND `history`.`timebound_id` = `pf`.`timeboundId` "
						+ " AND `history`.`schedule_id` <> `pf`.`schedule_id` "
						
						+ " WHERE "
							+ " `pf`.`sapid` = ? "
						+ " AND `pf`.`timeboundId` = ? "
						+ " AND `pf`.`isResultLive` = 'Y' "
						+ " AND `pf`.`status` IS NOT NULL "
						/* get only 1 */
						+ " LIMIT 1 ";
				 attemptedTest = jdbcTemplate.queryForObject(
					 sql,
					 new Object[] { sapid, id },
					 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
				 );
			} catch (Exception e) {   
				
			}
			
			return attemptedTest;
		}
		
		public int countOfAnswersToBeEvaluatedByFacultyIdNTestId(String userId, Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="select count(*) "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t"
					+ " where a.testId=? "
					+ "  and q.type in (4,8) "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.isChecked = 0 "
					+ "  and a.facultyId=? "
					+ "  and a.testId = t.id "
					+ "  Order By a.id ";
			
			int count=0;
			try {
				
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {id,userId},new SingleColumnRowMapper(Integer.class));

				
			} catch (Exception e) {
				
			}
			return count;
		}
		
		public int countOfEvaluatedAnswersByFacultyIdNTestId(String userId, Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="select count(*) "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t"
					+ " where a.testId=? "
					+ "  and q.type in (4,8) "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.isChecked = 1 "
					+ "  and a.facultyId=? "
					+ "  and a.testId = t.id "
					+ "  Order By a.id ";
			
			int count = 0;
			try {
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {id,userId},new SingleColumnRowMapper(Integer.class));
				
			} catch (Exception e) {
				
			}
			return count;
		}
		
		public int getNoOfTestsForFaculty(String facultyId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = 0;
			String sql=" SELECT " + 
					"    COUNT(DISTINCT testId) " + 
					"FROM " + 
					"    exam.test_students_answers " + 
					"WHERE " + 
					"    facultyId = ? ";
			try {
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {facultyId},new SingleColumnRowMapper(Integer.class));

			} catch (Exception e) {
				
			}
			return count;
		}
		
		//getIaForFaculty DAO call has been moved to internal-assessment microservice
		/*
		public List<TestExamBean> getIaForFaculty(TestExamBean testBean, String authorizedCenterCodes) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			
			// Object[] args = new Object[]{};
			ArrayList<Object> parameters = new ArrayList<Object>();

			String sql = "SELECT  " + 
					"    t.id, " + 
					"    t.subject, t.testName, tst.showResult, " +
//					"	 COALESCE(tst.resultDeclaredOn,'Not Available') as resultDeclaredOn , " + 		//Commented for IA Evaluation Report Results ReRun Card: 12418
					"	 COALESCE(t.initialResultLiveDateTime,'Not Available') AS initialResultLiveDateTime," +		//Added for IA Evaluation Report Results ReRun Card: 12418
					"	 COALESCE(t.lastModifiedResultLiveDate,'Not Available') AS lastModifiedResultLiveDate," +	//Added for IA Evaluation Report Results ReRun Card: 12418
					"    CONCAT(f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    t.facultyId, " + 
					"   tsa.evalFacultyName,"+
					"  	tsa.facultyId AS evalFacultyId,"+
//					"    s.sessionName, " +  Commented by Abhay for re-test evaluation report 
					"    concat(m.sessionModuleNo,' - ',m.topic) AS sessionName, " + 
					"    t.startDate, " + 
					"    tsa.sapid AS sapid, " + 
					"    st.firstName AS firstName, " + 
					"    st.lastName AS lastName, " + 
					"    SUM(q.marks) AS maxScore, " + 
					" SUM(tsa.marks) as score, " +
					"    COUNT(*) AS noOfQuestions, " + 
					"    b.name AS batch, " + 
					"    t.acadMonth, " + 
					"    t.acadYear, " + 
					"    t.month, " + 
					"    t.year,"+
					"	 tst.attemptStatus,"+
					"	 tsa.answer,"+					
					"    tsa.remark," + 
					"    q.question, " + 
					"    q.marks as questionsMarks, " + 
					"    CASE " + 
					"        WHEN tsa.isChecked = 1 THEN 'Y' " + 
					"        ELSE 'N' " + 
					"    END AS evaluated, "	+ 
					" 	 CASE " + 
					"		WHEN q.type = 4 THEN 'Descriptive' " + 
					" 		ELSE 'Assignment' " + 
					" 	 END AS questionType  " + 					
					"FROM " + 
					"    exam.test t " + 
//					"        INNER JOIN " +  Commented by Abhay for re-test evaluation report 
//					"    acads.sessions s ON s.moduleid = t.referenceId " +  Commented by Abhay for re-test evaluation report 
					"        INNER JOIN " + 
					"    acads.faculty f ON f.facultyId = t.facultyId " + 
					"         INNER JOIN( " + 
					"    SELECT  " + 
					"        arc.*, " +
					"      CONCAT(fa.firstName, ' ', fa.lastName) AS evalFacultyName"+
					"    FROM " + 
					"        exam.test_students_answers_archive arc " +
					"          LEFT JOIN  "+
					"      acads.faculty fa ON fa.facultyId = arc.facultyId "+
					"	UNION ALL " + 
					"	SELECT  " + 
					"        ans.* , " + 
					"     CONCAT(fac.firstName, ' ', fac.lastName) AS evalFacultyName"+
					"    FROM " + 
					"        exam.test_students_answers ans " + 
					"         LEFT JOIN "+
					"        acads.faculty fac ON fac.facultyId = ans.facultyId"+
					"	) AS tsa ON tsa.testId = t.id " + 
					"        INNER JOIN " + 
					"    exam.test_questions q ON tsa.questionId = q.id " + 
					"        INNER JOIN " + 
					"    exam.students st ON st.sapid = tsa.sapid " + 
					"        INNER JOIN " + 
					"    acads.sessionplan_module m ON m.id = t.referenceId " + 
					"        INNER JOIN " + 
					"    acads.sessionplan sp ON sp.id = m.sessionPlanId " + 
					"        INNER JOIN " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					"        INNER JOIN " + 
					"    exam.batch b ON b.id = ssc.batchId "+
					"		INNER JOIN " + 
					"	exam.test_student_testdetails tst ON tst.testId = t.id AND  tst.sapid = tsa.sapid " +	
					"WHERE " + 
					"    t.applicableType = 'module' " + 
					"        AND q.type in(4,8) " +
					"		 AND tsa.sapid NOT LIKE '777777%' "+
					"        AND t.year = ? " + 
					"        AND t.month = ? ";

								
//			String sql = "SELECT  " + 
//					"    t.id, " + 
//					"    t.subject, " + 
//					"    CONCAT(f.firstName, ' ', f.lastName) AS facultyName, " + 
//					"    t.facultyId, " + 
//					"    s.sessionName, " + 
//					"    t.startDate, " + 
//					"    tsa.sapid AS sapid, " + 
//					"    t.maxScore AS score,    " + 
//					"    s.firstName AS firstName, " + 
//					"    s.lastName AS lastName, " + 
//					"    count(*) AS noOfDescriptive, " + 
//					"    CASE " + 
//					"        WHEN tsa.isChecked = 1 THEN 'Y' " + 
//					"        ELSE 'N' " + 
//					"    END AS evaluated " + 
//					"FROM " + 
//					"    exam.test t " + 
//					"        INNER JOIN " + 
//					"    acads.sessions s ON s.moduleid = t.referenceId " + 
//					"        INNER JOIN " + 
//					"    acads.faculty f ON f.facultyId = t.facultyId " + 
//					"        INNER JOIN " + 
//					"    exam.test_students_answers tsa ON tsa.testId = t.id " + 
//					"        INNER JOIN " + 
//					"    exam.test_questions q ON tsa.questionId = q.id " + 
//					"        INNER JOIN " + 
//					"    exam.students s ON s.sapid = tsa.sapid " + 
//					"WHERE " + 
//					"    t.applicableType = 'module' " + 
//					"        AND q.type = 4  " + 
//					"        AND t.year = ? " + 
//					"        AND t.month = ?     " ;
			
//			String countSql = "SELECT " + 
//					"    COUNT(*) as count " + 
//					"FROM  " + 
//					"    ( " + 
//					"       SELECT  " + 
//					"    t.id, " + 
//					"    t.subject, " + 
//					"    CONCAT(f.firstName, ' ', f.lastName) AS facultyName, " + 
//					"    t.facultyId, " + 
//					"    s.sessionName, " + 
//					"    t.startDate, " + 
//					"    tsa.sapid AS sapid, " + 
//					"    t.maxScore AS score,    " + 
//					"    s.firstName AS firstName, " + 
//					"    s.lastName AS lastName, " + 
//					"    count(*) AS noOfDescriptive, " + 
//					"    CASE " + 
//					"        WHEN tsa.isChecked = 1 THEN 'Y' " + 
//					"        ELSE 'N' " + 
//					"    END AS evaluated " + 
//					"FROM " + 
//					"    exam.test t " + 
//					"        INNER JOIN " + 
//					"    acads.sessions s ON s.moduleid = t.referenceId " + 
//					"        INNER JOIN " + 
//					"    acads.faculty f ON f.facultyId = t.facultyId " + 
//					"        INNER JOIN " + 
//					"    exam.test_students_answers tsa ON tsa.testId = t.id " + 
//					"        INNER JOIN " + 
//					"    exam.test_questions q ON tsa.questionId = q.id " + 
//					"        INNER JOIN " + 
//					"    exam.students s ON s.sapid = tsa.sapid " + 
//					"WHERE " + 
//					"    t.applicableType = 'module' " + 
//					"        AND q.type = 4  " + 
//					"        AND t.year = ? " + 
//					"        AND t.month = ?     " ;		
					
					
			
	
			parameters.add(testBean.getYear());
			parameters.add(testBean.getMonth());
			
			if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
				sql = sql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
			}
			
			if( testBean.getSubject() != null  &&  !("".equals(testBean.getSubject()))){
				sql = sql + "  AND t.subject =  ? ";
//				countSql = countSql + "  AND t.subject  =  ? ";
				parameters.add(testBean.getSubject());
			}
	
			if( testBean.getFacultyId()  != null  &&  !("".equals(testBean.getFacultyId() ))){
				sql = sql + "  AND t.facultyId = ? ";
//				countSql = countSql + "  AND t.facultyId = ? ";
				parameters.add(testBean.getFacultyId());
			}
			
			if( testBean.getSapid()  != null  &&  !("".equals(testBean.getSapid() ))){
				sql = sql + " AND  tsa.sapid  = ? ";
//				countSql = countSql + " AND  tsa.sapid = ? ";
				parameters.add(testBean.getSapid());
			}
			
			sql = sql + "  group by t.id , st.sapid, q.id , tsa.isChecked  ";
//			countSql = countSql + " group by t.id , s.sapid , tsa.isChecked  ) AS DerivedTableAlias ";

			
			
			

			
		//	sql = sql + "  group by a.subject, a.sapid order by a.subject asc ";
			Object[] args = parameters.toArray();	
		 
//			PaginationHelper<TestBean> pagingHelper = new PaginationHelper<TestBean>();			
//			Page<TestBean> page = pagingHelper.fetchPage(jdbcTemplate,
//					countSql, sql, args, pageNo, pageSize,
//					new BeanPropertyRowMapper(TestBean.class));
			

		
		 List<TestExamBean> iaList = new ArrayList<TestExamBean>();
		 try {
		 iaList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(TestExamBean.class));
		 }catch(Exception e) {
			 
		 }	
		 
		 return iaList;
		}
		*/

		public boolean updateProcessedFlagInTEEMarks(TEEResultBean bean,String userId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="update exam.tee_marks set "
							+ " processed = 'N',"
							+ " lastModifiedBy=?, "
							+ " updated_at = sysdate()"
								
							+ " where sapid=? and timebound_id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {userId, 
						bean.getSapid(),
						bean.getTimebound_id()
									});

				return true;
			} catch (Exception e) {
				
			}
			return false;
		}
		
		public TEEResultBean getStudentsTimeboundIdFromTest(final long testId, final String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			TEEResultBean bean= new TEEResultBean();
			String sql="SELECT sptm.timeboundId as timebound_id,tsa.sapid FROM exam.test t " + 
					"inner join exam.test_students_answers tsa on t.id=tsa.testId " + 
					"inner join acads.sessionplan_module spm on t.referenceId = spm.id " + 
					"inner join acads.sessionplanid_timeboundid_mapping sptm on sptm.sessionPlanId=spm.sessionPlanId " + 
					"WHERE tsa.testId = ?" + 
					"	AND tsa.sapid = ? " + 
					"GROUP BY sptm.timeboundId, tsa.sapid";
			
			try {

				bean = (TEEResultBean)jdbcTemplate.queryForObject(sql,new Object[] {testId, sapid},new BeanPropertyRowMapper(TEEResultBean.class));
				return bean;
			}catch (Exception e) {
				
			}
			return bean;
		}
		
		public boolean checkIfTEEMarksPresent(TEEResultBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="SELECT count(*) from exam.tee_marks where sapid=? and timebound_id=? ";
			
			try {
				int count = (int)jdbcTemplate.queryForObject(sql,new Object[] {bean.getSapid(),bean.getTimebound_id()},new SingleColumnRowMapper(Integer.class));
				if(count >0) {

					return true;
				}else {
					return false;
				}
				
			}catch (Exception e) {
				
			}
			return false;
		}

		

		//For plagiarism check start
		public List<TestExamBean> getTestsApplicableForPlagiarismCheck() {
			List<TestExamBean> tests = new ArrayList<TestExamBean>();
			jdbcTemplate = new JdbcTemplate(dataSource); 
			
			try {
				 String sql="SELECT  " + 
				 		"    t.*, tcm.iaType " + 
				 		"FROM " + 
				 		"    exam.test t " + 
				 		"        INNER JOIN " + 
				 		"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
				 		"WHERE " + 
				 		"    TIMESTAMPDIFF(MINUTE, t.endDate, NOW()) > 180 " + 
				 		"        AND (t.isPlagiarismCheckDone <> 'Y' " + 
				 		"        OR t.isPlagiarismCheckDone IS NULL)";
					tests = jdbcTemplate.query(sql,new BeanPropertyRowMapper(TestExamBean.class));
			 }catch(Exception e) {
				 
			 }
			
			 return tests;
		}

		public List<StudentQuestionResponseExamBean> getDescriptiveAnswersForPlagiarismCheckByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
			
			String sql="select a.*,"
					+ " tst.attemptStatus ,"
					+ " q.question, q.copyCaseThreshold ,  t.testName, q.marks as questionMarks,s.firstName,s.lastName "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t,"
					+ "		 exam.students s,"
					+ "      exam.test_student_testdetails tst "
					+ " where a.testId=? "
					+ "  and q.type=4 "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.testId = t.id"
					+ "  and s.sapid = a.sapid"
					+ "  and tst.testId=a.testId"
					+ "  and tst.sapid=a.sapid"
					+ "  and tst.attempt=a.attempt "
					+ "  Order By a.id"
					+ "  ";
			
			
			try {
				answers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (Exception e) {
				
			}
			return answers;
		}
		

		public List<StudentQuestionResponseExamBean> getPDFAnswersForPlagiarismCheckByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
			
			String sql="select a.*,"
					+ " tst.attemptStatus ,"
					+ " q.question, q.copyCaseThreshold ,  t.testName, q.marks as questionMarks,s.firstName,s.lastName "
					+ " from exam.test_students_answers a , "
					+ "		 exam.test_questions q, "
					+ "		 exam.test t,"
					+ "		 exam.students s,"
					+ "      exam.test_student_testdetails tst "
					+ " where a.testId=? "
					+ "  and q.type=8 "
					+ "  and a.questionId=q.id "
					+ "  and a.testId=q.testId "
					+ "  and a.testId = t.id"
					+ "  and s.sapid = a.sapid"
					+ "  and tst.testId=a.testId"
					+ "  and tst.sapid=a.sapid"
					+ "  and tst.attempt=a.attempt "
					+ "  Order By a.id"
					+ "  ";
			
			
			try {
				answers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (Exception e) {
				
			}
			return answers;
		}

		public String markForCopyCaseByTestIdAndAttemptAndSapid(Long testId, int attempt, String sapid, String attemptStatus, double matching) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql="update exam.test_student_testdetails set "
							+ "	attemptStatus=?,"
							+ "	copyCaseMatchedPercentage=?,"
							+ " attemptStatusModifiedDate = sysdate()  "    
								
							+ " where testId=? and attempt=? and sapid=?  ";
			try {
				jdbcTemplate.update(sql,new Object[] {attemptStatus,matching,testId,attempt,sapid});

			} catch (Exception e) {
				
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				
				return "IN markForCopyCaseByTestIdAndAttemptAndSapid with attemptStatus : "+attemptStatus+" testId :  "+testId+" attempt : "+attempt+" attempt : "+sapid +". "+
						errors.toString();
			}
			return "";
		}
		

		public List<StudentsTestDetailsExamBean> getStudentsTestDetailsByTestIdHavingCopyCaseMatchedPercentageAbove70(Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsExamBean> testsByStudent=null;
			String sql="select * "
					+ "	from exam.test_student_testdetails "
					+ " where testId=? and copyCaseMatchedPercentage >= 70.0 "
					+ " Order By copyCaseMatchedPercentage desc ";
			try {
				testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}

		public String toggleIATestsCopyCaseStatus(Long testId, String sapid, String attempt) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ "	attemptStatus= CASE WHEN attemptStatus = 'CopyCase' THEN 'Attempted' ELSE 'CopyCase' END  ,"
							+ " attemptStatusModifiedDate = sysdate()  "
							+ " where testId=? and attempt=? and sapid=?  ";
			
			//peerToPeerMatchingPercentage
			String sqlToUpdateAnswer ="update exam.test_students_answers set "
					+ "	isChecked= CASE WHEN isChecked = 1 THEN 0 ELSE 1 END  ,"
					+ " lastModifiedDate = sysdate()  "
					+ " where testId=? and attempt=? and sapid=? "
					+ " and peerToPeerMatchingPercentage >= 70.0 ";
			
			try {
				jdbcTemplate.update(sql,new Object[] {testId,attempt,sapid});
				jdbcTemplate.update(sqlToUpdateAnswer,new Object[] {testId,attempt,sapid});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}

		public String markIATestsCopyCaseFromAdminView(Long testId, String sapid, String attempt, Long questionId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ "	attemptStatus= 'CopyCase'   ,"
							+ " attemptStatusModifiedDate = sysdate()  "
							+ " where testId=? and attempt=? and sapid=?  ";
			
			//peerToPeerMatchingPercentage
			String sqlToUpdateAnswer ="update exam.test_students_answers set "
					+ " facultyId = ? ,	isChecked= 1, remark= 'Marked For Copy Case',marks= 0, "
					+ " lastModifiedDate = sysdate()  "
					+ " where testId=? and attempt=? and sapid=? "
					+ "  ";
			
			String sqlForFacultyId = " SELECT facultyId FROM exam.test where id = ? ";
			
			
			
			try {
			String facultyId = (String)	jdbcTemplate.queryForObject(sqlForFacultyId, new Object[] {testId}, String.class);
				jdbcTemplate.update(sql,new Object[] {testId,attempt,sapid});
				jdbcTemplate.update(sqlToUpdateAnswer,new Object[] {facultyId, testId,attempt,sapid});
			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}
		
		public String unMarkIATestsCopyCaseFromAdminView(Long testId, String sapid, String attempt, Long questionId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ "	attemptStatus= 'Attempted'   ,"
							+ " attemptStatusModifiedDate = sysdate()  "
							+ " where testId=? and attempt=? and sapid=?  ";
			
			//peerToPeerMatchingPercentage
			String sqlToUpdateAnswer ="update exam.test_students_answers set "
					+ " facultyId = '',	isChecked= 0,remark= '',"
					+ " lastModifiedDate = sysdate()  "
					+ " where testId=? and attempt=? and sapid=? "
					+ " ";
			
			try {
				jdbcTemplate.update(sql,new Object[] {testId,attempt,sapid});
				jdbcTemplate.update(sqlToUpdateAnswer,new Object[] {testId,attempt,sapid});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}

		public String setIsPlagiarismCheckDoneToYByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test set "
							+ "	isPlagiarismCheckDone = 'Y' "
							+ " where id=?   ";
			try {
				jdbcTemplate.update(sql,new Object[] {id});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}
		public String setIsPlagiarismCheckDoneToNByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test set "
							+ "	isPlagiarismCheckDone = 'N' "
							+ " where id=?   ";
			try {
				jdbcTemplate.update(sql,new Object[] {id});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}

		public String setCopyCaseRemarkToAnswerById(Long id, String message, double matching) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_students_answers set "
							+ "	isChecked = 1, "
							+ " remark = ?,"
							+ " marks = 0, "
							+ " peerToPeerMatchingPercentage = ? "
							+ " where id=?   ";
			try {
				jdbcTemplate.update(sql,new Object[] {message,matching,id});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}
		
		
		//testId, questionId, sapId1, lastName1, firstName1, sapId2, lastName2, firstName2, matching, matchingFor80to90, maxConseutiveLinesMatched, numberOfLinesInFirstFile, numberOfLinesInSecondFile, firstTestDescriptiveAnswer, secondTestDescriptiveAnswer, firstSapidTestAnswerCreatedDate, secondSapidTestAnswerCreatedDate, markedForCopyCase
		public String saveCopyCaseEntryInTable(final ResultDomain copyResult) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			// id, questionId, url : exam.test_question_additionalcontent
			final String sql = "INSERT INTO exam.test_copycases "
					+ " (testId, questionId, sapId1, lastName1, firstName1,"
					+ "  sapId2, lastName2, firstName2, matching, matchingFor80to90,"
					+ "  maxConseutiveLinesMatched, numberOfLinesInFirstFile, numberOfLinesInSecondFile, firstTestDescriptiveAnswer, secondTestDescriptiveAnswer,"
					+ "  firstSapidTestAnswerCreatedDate, secondSapidTestAnswerCreatedDate, markedForCopyCase ) "
	        		+ " VALUES(?,?,?,?,?,"
	        		+ "        ?,?,?,?,?,"
	        		+ "        ?,?,?,?,?,"
	        		+ "        ?,?,?)"
	        		+ " on duplicate key update "
					+ "  matching=?, matchingFor80to90=?,"
					+ "  maxConseutiveLinesMatched=?, numberOfLinesInFirstFile=?, numberOfLinesInSecondFile=?, firstTestDescriptiveAnswer=?, secondTestDescriptiveAnswer=?,"
					+ "  firstSapidTestAnswerCreatedDate=?, secondSapidTestAnswerCreatedDate=?, markedForCopyCase=?  "
	        		+ "  ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setLong(1, copyResult.getTestId());
				        statement.setLong(2, copyResult.getQuestionId());
				        statement.setString(3, copyResult.getSapId1());  
				        statement.setString(4, copyResult.getLastName1());
				        statement.setString(5, copyResult.getFirstName1());

				        statement.setString(6, copyResult.getSapId2());  
				        statement.setString(7, copyResult.getLastName2());
				        statement.setString(8, copyResult.getFirstName2());
				        statement.setDouble(9, copyResult.getMatching());
				        statement.setString(10, copyResult.getMatchingFor80to90());

				        statement.setInt(11, copyResult.getMaxConseutiveLinesMatched());
				        statement.setInt(12, copyResult.getNumberOfLinesInFirstFile());
				        statement.setInt(13, copyResult.getNumberOfLinesInSecondFile());
				        statement.setString(14, copyResult.getFirstTestDescriptiveAnswer());
				        statement.setString(15, copyResult.getSecondTestDescriptiveAnswer());

				        statement.setString(16, copyResult.getFirstSapidTestAnswerCreatedDate());
				        statement.setString(17, copyResult.getSecondSapidTestAnswerCreatedDate());
				        statement.setString(18, copyResult.getMarkedForCopyCase());
				        

				        statement.setDouble(19, copyResult.getMatching());
				        statement.setString(20, copyResult.getMatchingFor80to90());
				        statement.setInt(21, copyResult.getMaxConseutiveLinesMatched());
				        statement.setInt(22, copyResult.getNumberOfLinesInFirstFile());
				        statement.setInt(23, copyResult.getNumberOfLinesInSecondFile());
				        statement.setString(24, copyResult.getFirstTestDescriptiveAnswer());
				        statement.setString(25, copyResult.getSecondTestDescriptiveAnswer());
				        statement.setString(26, copyResult.getFirstSapidTestAnswerCreatedDate());
				        statement.setString(27, copyResult.getSecondSapidTestAnswerCreatedDate());
				        statement.setString(28, copyResult.getMarkedForCopyCase());
				        
				        return statement;
				    }
				}, holder);
				//Long primaryKey = holder.getKey().longValue();

			} catch (Exception e) {
				
				return e.getMessage();
			}

				return "";
		}

		public List<ResultDomain> getCopyCasesByTestId(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ResultDomain> copyCases=null;
			Map<String,String> sapidNAttemptedStatusMap = new HashMap<>();
			String sql="select * "
					+ "	from exam.test_copycases "
					+ " where testId=?  "
					+ " Order By sapId1 desc ";
			try {
				copyCases = (List<ResultDomain>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(ResultDomain.class));
				for(ResultDomain c : copyCases) {
					if(sapidNAttemptedStatusMap.containsKey(c.getSapId1())) {
						c.setSapid1AttemptStatus(sapidNAttemptedStatusMap.get(c.getSapId1()));
					}else {
						String attemptStatus1 = getAttemptStatusBySapidAndTestIdAndAttempt(c.getSapId1(),id,1);
						c.setSapid1AttemptStatus(attemptStatus1);
						sapidNAttemptedStatusMap.put(c.getSapId1(), attemptStatus1);
					}
					if(sapidNAttemptedStatusMap.containsKey(c.getSapId2())) {
						c.setSapid2AttemptStatus(sapidNAttemptedStatusMap.get(c.getSapId2()));
					}else {
						String attemptStatus2 = getAttemptStatusBySapidAndTestIdAndAttempt(c.getSapId2(),id,1);
						c.setSapid2AttemptStatus(attemptStatus2);
						sapidNAttemptedStatusMap.put(c.getSapId2(), attemptStatus2);
					}
				}
			} catch (Exception e) {
				
			}  
			return copyCases;
		}

		private String getAttemptStatusBySapidAndTestIdAndAttempt(String sapId, Long id, int attempt) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String attemptStatus="";
			String sql=" select attemptStatus from exam.test_student_testdetails "
					+ " where sapid=? "
					+ " and testId = ? "
					+ " and attempt = ?  "
					+ " ";
			try {
				attemptStatus = (String) jdbcTemplate.queryForObject(sql, new Object[] {sapId,id,attempt}, String.class);

			} catch (Exception e) {
				
				attemptStatus = "Error in getting attemptStatus, "+e.getMessage();
			}
			return attemptStatus;
		}

		public String updateStatusInCopyCaseTable(Long testId, String sapid, String attempt, Long questionId, String markedForCopyCase) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_copycases set "
							+ "	markedForCopyCase= ?   "
							+ " where testId=? and attempt=? and questionId = ?  and (sapId1=? or sapId2=?)  ";
			
			
			try {
				jdbcTemplate.update(sql,new Object[] {markedForCopyCase,testId,attempt,questionId,sapid,sapid});

			} catch (Exception e) {
				
				return e.getMessage();
			}
			return "";
		}
		
		//For plagiarism check end


		public int getBatchForTestStudentByTestId(Long testId) {
			int timeboundId = 0;
			try {
				String sql="SELECT " + 
						"    timeboundId " + 
						" FROM " + 
						"    acads.sessionplanid_timeboundid_mapping " + 
						" WHERE " + 
						"    sessionPlanId = (SELECT " + 
						"            sessionPlanId " + 
						"        FROM " + 
						"            acads.sessionplan_module " + 
						"        WHERE " + 
						"            id = (SELECT " + 
						"                    referenceId " + 
						"                FROM " + 
						"                    exam.test " + 
						"                WHERE " + 
						"                    id = "+testId+"))" ;
				timeboundId = (int)jdbcTemplate.queryForObject(sql,new SingleColumnRowMapper(Integer.class));

			}
			catch(Exception e) {
				throw e;
			}
			
			return timeboundId;
		}

		public String getTestStudentForTest(String userId, int timeboundId) {
			String sapid = "";
			try {
				String sql = "select userId from lti.timebound_user_mapping where userId like '%"
						+ userId+ "%' and timebound_subject_config_id = "+ timeboundId;

				sapid = (String)jdbcTemplate.queryForObject(sql,new SingleColumnRowMapper(String.class));

			}catch(Exception e) {
				throw e;
			}
			
			return sapid;
		}
		
		public boolean updatePreviewedByFaculty(TestExamBean test) {
			boolean updated= false;
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test set previewedByFaculty='Y' where id=? ";
			try {

				jdbcTemplate.update(sql,new Object[] {test.getId()});
				updated = true;
			}
			catch(Exception e) {

				updated = false;
				throw e;
			}
			
			return updated;
		}
		
		public ArrayList<String> checkIfTestPreviewedByFaculty(TestExamBean test) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<TestExamBean> testList = new ArrayList<TestExamBean>();
			ArrayList<String> notPreviewedList = new ArrayList<String>();
			Object[] queryParams = new Object[] {test.getLiveType(), test.getApplicableType(), 
					test.getYear(), test.getMonth(), test.getAcadYear(), test.getAcadMonth()};
			String sql="SELECT " + 
					"    * " + 
					" FROM " + 
					"   exam.test t" + 
					"       INNER JOIN " + 
					"   exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"       INNER JOIN " + 
					"   exam.test_live_settings tls ON tls.referenceId = tcm.referenceId " + 
					"       AND tls.applicableType = tcm.type " + 
					"       INNER JOIN " + 
					"   acads.sessionplan_module m ON m.id = tcm.referenceId " + 
					"       INNER JOIN " + 
					"   acads.sessionplan s ON s.id = m.sessionPlanId " + 
					"       INNER JOIN " + 
					"   acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
					"       INNER JOIN " + 
					"   lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					" WHERE " + 
					"   tls.liveType = ? " + 
					"	AND tls.applicableType = ? " + 
					"    AND t.year = ? " + 
					"    AND t.month = ? " + 
					"    AND t.acadYear = ? " + 
					"    AND t.acadMonth = ? " ;
			
			if(test.getReferenceId() != null) {
				sql = sql + "    AND tcm.referenceId = ? ";
				queryParams = new Object[] {test.getLiveType(), test.getApplicableType(), 
						test.getExamYear(), test.getExamMonth(), test.getAcadYear(), test.getAcadMonth(), test.getReferenceId()};

			}
			try {

				testList = (ArrayList<TestExamBean>) jdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(TestExamBean.class));

				if(testList.size() > 0) {
					for(TestExamBean testBean : testList) {
						if(testBean.getPreviewedByFaculty() == null) {
							notPreviewedList.add(testBean.getTestName());
						}
					}
				}

			}
			catch(Exception e) {

				throw e;
			}
			return notPreviewedList;
			
		}
		
		public String checkIfQuestionsAndQuestionConfigMatches(TestExamBean test) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<TestExamBean> testList = new ArrayList<TestExamBean>();
			int maxQuestnToShow = 0;
			String sumOfTestQsConfig = null;
			String countOfTestQs = null;
			ArrayList<String> incorrectConfigTestList = new ArrayList<String> ();
			String sqlForConfig = "";
			String sqlForTestQs = "";
			String errorMsg = "Kindly Configure all questions added for Test(s) : ";
			String errorMsg1 = "Mismatch in Configured questions and maxQuestnToShow:  ";
			String errorMsg2 = "Kindly check maxQuestnToShow/Test config/Total added qustions for Test(s): ";
			String errorMsgToReturn = "";
			ArrayList<String> errorMsgToShow = new ArrayList<String> ();
			Object[] queryParams = new Object[] {test.getLiveType(), test.getApplicableType(), 
					test.getYear(), test.getMonth(), test.getAcadYear(), test.getAcadMonth()};
			String sql="SELECT " + 
					"    * " + 
					" FROM " + 
					"   exam.test t" + 
					"       INNER JOIN " + 
					"   exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"       INNER JOIN " + 
					"   exam.test_live_settings tls ON tls.referenceId = tcm.referenceId " + 
					"       AND tls.applicableType = tcm.type " + 
					"       INNER JOIN " + 
					"   acads.sessionplan_module m ON m.id = tcm.referenceId " + 
					"       INNER JOIN " + 
					"   acads.sessionplan s ON s.id = m.sessionPlanId " + 
					"       INNER JOIN " + 
					"   acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
					"       INNER JOIN " + 
					"   lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					" WHERE " + 
					"    tls.liveType = ? " + 
					"	 AND tls.applicableType = ? " + 
					"    AND t.year = ? " + 
					"    AND t.month = ? " + 
					"    AND t.acadYear = ? " + 
					"    AND t.acadMonth = ? " ;
			
			if(test.getReferenceId() != null) {
				sql = sql + "    AND tcm.referenceId = ? ";
				queryParams = new Object[] {test.getLiveType(), test.getApplicableType(), 
						test.getExamYear(), test.getExamMonth(), test.getAcadYear(), test.getAcadMonth(), test.getReferenceId()};

			}
			try {

				testList = (ArrayList<TestExamBean>) jdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(TestExamBean.class));

				if(testList.size() > 0) {
					for(TestExamBean testBean : testList) {
						maxQuestnToShow = testBean.getMaxQuestnToShow();
						sqlForConfig = "SELECT sum(maxNoOfQuestions) FROM exam.test_questions_configuration where testId="+testBean.getTestId();
						sumOfTestQsConfig = (String) jdbcTemplate.queryForObject(sqlForConfig, new SingleColumnRowMapper(String.class));
						sqlForTestQs = "SELECT count(*) FROM exam.test_questions where testId ="+testBean.getTestId();
						countOfTestQs = (String) jdbcTemplate.queryForObject(sqlForTestQs, new SingleColumnRowMapper(String.class));
						
						
						if(sumOfTestQsConfig == null) {
							sumOfTestQsConfig = "0";
						}
						if(countOfTestQs == null) {
							countOfTestQs = "0";
						}
						

						if(maxQuestnToShow == Integer.parseInt(sumOfTestQsConfig) && Integer.parseInt(sumOfTestQsConfig) == Integer.parseInt(countOfTestQs) && Integer.parseInt(countOfTestQs) == maxQuestnToShow) {
//							no errors..do nothing

						}else {
							if(Integer.parseInt(sumOfTestQsConfig) == Integer.parseInt(countOfTestQs) && maxQuestnToShow != Integer.parseInt(countOfTestQs)) {

								if(maxQuestnToShow < Integer.parseInt(countOfTestQs)) {
									errorMsgToShow.add(errorMsg1 + " maxQuestnToShow is less than configured questions for Test: "+ testBean.getTestName() + "\n");
									incorrectConfigTestList.add(testBean.getTestName());
								}
								else if(maxQuestnToShow > Integer.parseInt(countOfTestQs)){
									errorMsgToShow.add(errorMsg1 + " maxQuestnToShow is greater than configured questions for Test: "+ testBean.getTestName() + "\n");
									incorrectConfigTestList.add(testBean.getTestName());
								}
							}
							else if(Integer.parseInt(sumOfTestQsConfig) == maxQuestnToShow && maxQuestnToShow != Integer.parseInt(countOfTestQs)) {

								if(maxQuestnToShow < Integer.parseInt(countOfTestQs)) {
									errorMsgToShow.add(errorMsg1 + " maxQuestnToShow is less than configured questions for Test: "+ testBean.getTestName() + "\n");
									incorrectConfigTestList.add(testBean.getTestName());
								}
								else if(maxQuestnToShow > Integer.parseInt(countOfTestQs)){
									errorMsgToShow.add(errorMsg1 + " maxQuestnToShow is greater than configured questions for Test: "+ testBean.getTestName() + "\n");
									incorrectConfigTestList.add(testBean.getTestName());
								}
							}
							else if(Integer.parseInt(countOfTestQs) == maxQuestnToShow && Integer.parseInt(countOfTestQs) != Integer.parseInt(sumOfTestQsConfig)) {

								errorMsgToShow.add(errorMsg + testBean.getTestName() + "\n");
								incorrectConfigTestList.add(testBean.getTestName());
							}
							else {

								errorMsgToShow.add(errorMsg2 + testBean.getTestName() + "\n");
								incorrectConfigTestList.add(testBean.getTestName());
							}
							
						}
					}
					errorMsgToReturn = errorMsgToShow.toString().substring(1,errorMsgToShow.toString().length()-1);
				}
				else {
					errorMsgToReturn = "No Tests found for Current Config.";
				}
			}catch(Exception e) {

				throw e;
			}

			return errorMsgToReturn;
		}
		
		
		public ArrayList<String> checkIfTestPreviewedByFacultyByQuestions(ArrayList<TestExamBean> testList,HttpServletRequest request) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sapid = "";
			String sql="";
			ArrayList<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
			ArrayList<TestQuestionExamBean> testQuestionsList = new ArrayList<TestQuestionExamBean>();
			ArrayList<StudentQuestionResponseExamBean> questionResponseList = new ArrayList<StudentQuestionResponseExamBean>();
			ArrayList<String> notPreviewedList = new ArrayList<String>();
			
			boolean isDetailsMatch = false;
			
			try {

				if(testList.size() > 0) {
					for(TestExamBean testBean : testList) {
//						get test student id for test
						sapid = getTestStudentForTestByTestId(testBean.getTestId());
//                     						
						testDetailsList = getTestDetails(testBean.getTestId(),sapid);

						testQuestionsList = getTestQuestionsByTestId(testBean.getTestId());

						questionResponseList = getQuestionResponse(testBean.getTestId(),sapid);

						//check if  sum of total questions mark and maxscore is same
						ResponseBean response =  checkIfMarksMismatchInConfiguration(testBean);
						if(response.getStatus()=="error") {
							request.setAttribute("error","true");
							request.setAttribute("errorMessage", response.getMessage());
							return notPreviewedList;	
						}
						
//						check if count total questions and total answers is same
						if(testDetailsList.size() > 0 && questionResponseList.size() > 0 && questionResponseList.size() == testQuestionsList.size()) {

								for(StudentsTestDetailsExamBean detailsBean : testDetailsList) {
									if(detailsBean.getTestCompleted().equals("Y")) {

										isDetailsMatch = true;
										break;
									}
								}
								if(!isDetailsMatch) {

									notPreviewedList.add(testBean.getTestName());
								}
						}
						else {

							notPreviewedList.add(testBean.getTestName());
						}
					}
				}else {

					NullPointerException npe = new NullPointerException("No Tests found for given configuration.");
					throw npe;
				}

			}
			catch(Exception e) {

				throw e;
			}
			return notPreviewedList;
			
		}
		
		private ResponseBean checkIfMarksMismatchInConfiguration(TestExamBean testbean) {
			ResponseBean response= new ResponseBean();
			/*int marks=0;
			//check if template is 3 discriptive type
			String sql1 = "SELECT type,minNoOfQuestions FROM exam.test_questions_configuration "
					+ "where testId=? and type=4 and minNoOfQuestions=3";
			StudentQuestionResponseBean template=null;
			try {
				template = (StudentQuestionResponseBean) jdbcTemplate.queryForObject(sql1,new Object[] { testbean.getTestId() }, new BeanPropertyRowMapper(StudentQuestionResponseBean.class));
			} catch (DataAccessException e) {}
			if( template!=null) {
				marks = 10;
				String sql2 = "SELECT * FROM exam.test_questions where testId=? and type=4 and marks=4";
				ArrayList<StudentQuestionResponseBean> questions = (ArrayList<StudentQuestionResponseBean>) jdbcTemplate.query(sql2,new Object[] {testbean.getTestId() }, new BeanPropertyRowMapper(StudentQuestionResponseBean.class));
			    if(questions.size()==0) {
			    	response.setStatus("error");
					response.setMessage("Please add 1 Descriptive Question with 4 Marks");
					return response;
				}
			    validateMaxScore(testbean,response);
			}
			//end of check if template is 3 discriptive type
			//for other templates
			else {*/
				validateMaxScore(testbean,response);
				
			//}
			validateQuestionsUploadedMarks(testbean,response);
			validateQnsUploadedAndConfiguredMarksPerSection(testbean,response);
			return response;
		} 
		
		public String getTestStudentForTestByTestId(Long id) {
			

//			TestDAO dao = (TestDAO)act.getBean("testDao");
//			TestBean test = dao.getTestById(id);
			TestExamBean test = getTestById(id);

			String userId = "";
			String acadMonth = "";
			Date date = null;

			int timeboundId = 0;
			try {

				userId = "777777";
				date = new SimpleDateFormat("MMM").parse(test.getAcadMonth());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				acadMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);

				if(acadMonth.length() < 2) {
					acadMonth = "0" + acadMonth;
				}
				

				userId = userId + acadMonth + test.getAcadYear().toString().substring(2,4);

				timeboundId = getBatchForTestStudentByTestId(id);

				if(userId.length() == 10 && timeboundId != 0) {
					userId = getTestStudentForTest(userId, timeboundId);

				}
				
			} catch (Exception e) {
				userId = null;
				
			}
			
			
			return userId;
			
		}


		public List<TestExamBean> getTestsBySapid(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsList=new ArrayList<>();
			
/* Commented out by Harsh, to get all the test applicable for the student.
 * 
			String sql=" " + 
					"SELECT  " + 
					"    t.* " + 
					"FROM " + 
					"    exam.test t " + 
					"        INNER JOIN " + 
					"    exam.test_student_testdetails tst ON t.id = tst.testId " + 
					"WHERE " + 
					"    tst.sapid = ? " + 
					"ORDER BY id DESC; ";
*
*/
			String sql="SELECT  " + 
					"    tst.id as testDetailsId, t.* " + 
					"FROM " + 
					"    exam.test t " + 
					"        INNER JOIN " + 
					"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"        INNER JOIN " + 
					"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId " + 
					"        INNER JOIN " + 
					"    acads.sessionplan_module m ON m.id = tcm.referenceId " + 
					"        INNER JOIN " + 
					"    acads.sessionplan s ON s.id = m.sessionPlanId " + 
					"        INNER JOIN " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					"        INNER JOIN " + 
					"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id " + 
					"        LEFT JOIN " + 
					"    exam.test_student_testdetails tst ON tst.sapid = tum.userId " + 
					"        AND tst.testId = t.id " + 
					"WHERE " + 
					"    tls.liveType = 'Regular' " + 
					"        AND tls.applicableType = 'module' " + 
					"        AND tum.userId = ? " + 
					"		 AND t.startDate < SYSDATE() " +
					"ORDER BY t.startDate DESC";
			try {
				testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}


		public TestExamBean getTestDetailsFromSessionPlanId(String sessionPlanId) {
	TestExamBean testBean = null;
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = "SELECT  " + 
						"    ssc.acadYear, ssc.acadMonth, ssc.examYear as year, ssc.examMonth as month,sp.subject,"+ 
					    "	 sp.consumerTypeId as consumerTypeIdFormValue,sp.programStructureId as programStructureIdFormValue,sp.programId as programIdFormValue " + 
						"FROM " + 
						"    acads.sessionplan sp " + 
						"        LEFT JOIN " + 
						"    acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
						"        LEFT JOIN " + 
						"    lti.student_subject_config ssc ON stm.timeboundId = ssc.id " + 
						"WHERE " + 
						"    sp.id = ? ";
				testBean = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] { sessionPlanId }, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (Exception e) {
				// TODO: handle exception
				
				
			}
			return testBean;
		}	

		public void deleteQuestionConfig(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from exam.test_questions_configuration where testId=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id});



		} catch (Exception e) {
			
		}
		}
		public List<TestExamBean> getTestsNotLive(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestExamBean> testsNotLiveList = new ArrayList<>();
			String sql =  "SELECT " + 
					"    t.id,t.year AS examYear," + 
					"    t.month AS examMonth," + 
					"    t.acadYear," + 
					"    t.acadMonth," + 
					"    applicableType,subject," + 
				    "	 c.id as consumerTypeId,c.name AS consumerType, "+
				    "    ps.id as programStructureId,ps.program_structure, "+
				    "    p.id as programId,p.code as program, "+
				    "    b.id as batchId,b.name, "+
				    "    spm.id as sessionModuleNo,spm.topic "+
					"FROM " + 
					"    exam.test t" + 
					"        LEFT JOIN" + 
					"    exam.consumer_type c ON t.consumerTypeIdFormValue = c.id" + 
					"        LEFT JOIN" + 
					"    exam.program_structure ps ON t.programStructureIdFormValue = ps.id" + 
					"        LEFT JOIN" + 
					"    exam.program p ON t.programIdFormValue = p.id" + 
					"        LEFT JOIN" + 
					"    exam.test_testid_configuration_mapping tcm ON tcm.testId = t.id" + 
					"        LEFT JOIN" + 
					"    acads.sessionplan_module spm ON tcm.referenceId = spm.id" + 
					"        LEFT JOIN" + 
					"    acads.sessionplanid_timeboundid_mapping stm ON spm.sessionPlanId = stm.sessionPlanId" + 
					"        LEFT JOIN" + 
					"    lti.student_subject_config ssc ON stm.timeboundId = ssc.id" + 
					"        LEFT JOIN" + 
					"    exam.batch b ON ssc.batchId = b.id " + 
					"WHERE " + 
					"    t.referenceId NOT IN (SELECT " + 
					"            referenceId " + 
					"        FROM " + 
					"            exam.test_live_settings)";
			
			try {
				testsNotLiveList = (List<TestExamBean>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(TestExamBean.class));
				
			} catch (Exception e) {
				
			}
			
			return testsNotLiveList;  
			
		}
		
		@Transactional(readOnly = true)
		public Integer getLiveTestByModuleId(long referenceId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT COUNT(*) " + 
						"FROM exam.test_live_settings " + 
						"WHERE referenceId = ? ";

			return jdbcTemplate.queryForObject(sql, Integer.class, referenceId);
		} 

		public int checkNumberOfDescriptiveQuestions(Long testId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql=" SELECT count(*) FROM exam.test_questions WHERE testId = ?    AND type= 4 ";
			
			int count=0;
			try {
				
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {testId},new SingleColumnRowMapper(Integer.class));

			} catch (Exception e) {
				
			}
			return count;
		}
		
		public int getTestByReferenceId(int referenceId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql=" SELECT count(*) FROM exam.test WHERE referenceId = ?  ";
			int count=0;
			try {
				 count = (int) jdbcTemplate.queryForObject(sql,new Object[] {referenceId},new SingleColumnRowMapper(Integer.class));

			} catch (Exception e) {
				
			}
			return count;
		}
		public String checkIfQuestionsConfigured(ArrayList<TestExamBean> testList) {
			String errorMsgToShow = "";
			ArrayList<String> testsWithIncompleteQns=new ArrayList<String>();
			ArrayList<String> testsNotConfigured=new ArrayList<String>();
			ArrayList<String> testsWithConfigAndQuestionsMismatch=new ArrayList<String>();
			ArrayList<String> testsQnsUploadedNotMatchingTemplate=new ArrayList<String>();
			boolean numberOfDescriptiveQuestionsExceeded=false;
			boolean configured=true;
			for(TestExamBean test : testList) {
				int maxQuestnToShow = test.getMaxQuestnToShow();
				int noOfQuestionsConfigured =getNoOfQuestionsConfigured(test.getTestId());
				int noOfQuestionsUploaded = getNoOfQuestionsUploaded(test.getTestId());
				if(maxQuestnToShow > noOfQuestionsUploaded) {
					testsWithIncompleteQns.add(test.getTestName());
				}
				
				if(noOfQuestionsConfigured  != maxQuestnToShow) {
					testsNotConfigured.add(test.getTestName());
				}
				/*if(checkNumberOfDescriptiveQuestions(test.getTestId())>3) {
					numberOfDescriptiveQuestionsExceeded=true;
				} */
				if(checkIfQnsUploadedTypeAndMarksMatchesConfigured(test.getTestId())) {
					testsQnsUploadedNotMatchingTemplate.add(test.getTestName());
				}
				
			}
			if(testsWithIncompleteQns.size()>0) {
				errorMsgToShow=errorMsgToShow+" Kindly add more questions to match maxQuestnToShow for tests "+testsWithIncompleteQns.toString();
			}
			if(testsNotConfigured.size()>0) {
				errorMsgToShow=errorMsgToShow+" Kindly configure questions to match maxQuestnToShow for tests"+testsNotConfigured.toString(); 
			}
			/*if(numberOfDescriptiveQuestionsExceeded) {
				errorMsgToShow=errorMsgToShow+ "Couldnot configure descriptive questions more than 3";
		    }*/
			/*if(testsWithConfigAndQuestionsMismatch.size()>0) {
				errorMsgToShow=errorMsgToShow+ "Please Check if enough questions uploaded for all question types configured per Section for tests"+testsWithConfigAndQuestionsMismatch.toString(); 
		    }*/
			if(testsQnsUploadedNotMatchingTemplate.size()>0) {
				errorMsgToShow=errorMsgToShow+ "Please Check if enough questions uploaded for all question types and marks configured per Section for tests"+testsQnsUploadedNotMatchingTemplate.toString(); 
			}
			return errorMsgToShow;
		}

		private boolean checkIfQnsUploadedTypeAndMarksMatchesConfigured(Long testId) {
			boolean mismatch=false;
			ArrayList<SectionBean> sectionList = getSectionsByTestId(testId);
			//get configurations for each sections
			if(sectionList.size()>0) {
				search: {
					for(SectionBean sectionBean : sectionList){ 
						ArrayList<SectionBean> con =getQuestionsConfiguredBasedOnSection(sectionBean.getId(),testId);
						//sectionBean.setSectionQnTypeConfigbean(s);
						for(SectionBean c : con) {
							TestQuestionConfigBean qnBean= checkUploadedQnsMarksForATypeInSection(testId,sectionBean.getId(),c.getType(),c.getQuestionMarks());
							if(qnBean==null) {
								mismatch=true;
								break search;
							}else {
								if( (qnBean.getNoOfQuestions()<Integer.parseInt(c.getSectionQnCount()))
									|| (qnBean.getQuestionMarks()!=Double.parseDouble(c.getQuestionMarks()))	) {
									mismatch=true;
									break search;
								}
							}
						}
					}
				}
			}
			return mismatch;
		}

		private TestQuestionConfigBean checkUploadedQnsMarksForATypeInSection(Long testId, String id, String type,String qnMarks) {
			String sql =  "SELECT count(*) as noOfQuestions,testId,type,marks as questionMarks FROM exam.test_questions  " + 
					"where testId=? and sectionId=? and type=? and marks=? limit 1";
			TestQuestionConfigBean qnBean=null;
			try {
				qnBean = (TestQuestionConfigBean) jdbcTemplate.queryForObject(sql,new Object[] {testId,id,type,qnMarks}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
				 
			} catch (DataAccessException e) {
				
			}
			return qnBean;
			
		}

		public ArrayList<TestExamBean> getTestsToMakeLiveBySearchBean(TestExamBean test) {
			
			Object[] queryParams = new Object[] {test.getLiveType(), test.getApplicableType(), 
					test.getYear(), test.getMonth(), test.getAcadYear(), test.getAcadMonth()};
			ArrayList<TestExamBean> testList = new ArrayList<TestExamBean>();
			String sql="SELECT " + 
					"    * " + 
					" FROM " + 
					"   exam.test t" + 
					"       INNER JOIN " + 
					"   exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"       INNER JOIN " + 
					"   acads.sessionplan_module m ON m.id = tcm.referenceId " + 
					"       INNER JOIN " + 
					"   acads.sessionplan s ON s.id = m.sessionPlanId " + 
					"       INNER JOIN " + 
					"   acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
					"       INNER JOIN " + 
					"   lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
					" WHERE " + 
					"    t.year = ? " + 
					"    AND t.month = ? " + 
					"    AND t.acadYear = ? " + 
					"    AND t.acadMonth = ? " ;
			
			if(test.getReferenceId() != null) {
				sql = sql + "    AND tcm.referenceId = ? ";
				queryParams = new Object[] { test.getExamYear(), test.getExamMonth(), test.getAcadYear(), test.getAcadMonth(), test.getReferenceId()};

			}
			try {
				testList = (ArrayList<TestExamBean>) jdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(TestExamBean.class));
			} catch (DataAccessException e) {
				
			}
			return testList;
		}
		private Integer getNoOfQuestionsConfigured(Long testId) {
			String sql =  "SELECT sum(maxNoOfQuestions) FROM exam.test_questions_configuration where testId="+testId;
			Integer sumOfTestQsConfig=0;
			try {
				sumOfTestQsConfig	 = (Integer) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
			} catch (DataAccessException e) {
				
			}
			return sumOfTestQsConfig;
		}
		private Integer getNoOfQuestionsUploaded(Long testId) {
			String sql1 = "SELECT count(*) FROM exam.test_questions where testId ="+testId;
			Integer noOfUploadedQuestions=0;
			try {
				noOfUploadedQuestions = (Integer) jdbcTemplate.queryForObject(sql1, new SingleColumnRowMapper(Integer.class));
			} catch (DataAccessException e) {
				
			}
			return noOfUploadedQuestions;
		}
		
		

		private ArrayList<StudentsTestDetailsExamBean> getTestDetails(Long testId,String sapid) {
			ArrayList<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
			String sql = "SELECT * FROM exam.test_student_testdetails where testId=? and sapid=?" ;
			
			try {
				testDetailsList = (ArrayList<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql,new Object[] {testId,sapid}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			} catch (DataAccessException e) {
			}
			return testDetailsList;
		}
		private ArrayList<TestQuestionExamBean> getTestQuestionsByTestId(Long testId) {
			ArrayList<TestQuestionExamBean> testQuestions = new ArrayList<TestQuestionExamBean>();
			String sql = "SELECT * FROM exam.test_questions where testId=?" ;
			
			try {
				testQuestions = (ArrayList<TestQuestionExamBean>) jdbcTemplate.query(sql,new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
			} catch (DataAccessException e) {
			}
			return testQuestions;
		}
		private ArrayList<StudentQuestionResponseExamBean> getQuestionResponse(Long testId,String sapid) {
			ArrayList<StudentQuestionResponseExamBean> questionResponseList = new ArrayList<StudentQuestionResponseExamBean>();
			String sql = "SELECT * FROM exam.test_students_answers where testId=? and sapid = ? group by questionId;";
			
			try {
				questionResponseList = (ArrayList<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql,new Object[] {testId,sapid}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
				
			} catch (DataAccessException e) {
			}
			return questionResponseList;
		}
	
		
		private boolean checkIfValidDateTime(TestExamBean bean) throws Exception{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql= "SELECT " + 
					"    COUNT(*)>0 " + 
					"FROM " + 
					"    exam.test " + 
					"WHERE " + 
					"    id = ? " + 
					"        AND ? BETWEEN startDate AND endDate";
			boolean isValid = (boolean)jdbcTemplate.queryForObject(sql, new Object[] {
					bean.getTestId(), 
					bean.getTestStartedOn()}, Boolean.class);
			return isValid;
		}

public List<PageVisitsBean> getPageVisitsWebBySapidCreatedDate(String sapid, String testStartedOnDate) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	List<PageVisitsBean> pVList=new ArrayList<>();
	String sql="  " + 
			"SELECT  " + 
			"    p.path, " + 
			"    pv.initialTimeStamp AS visiteddate, " + 
			"    pv.timeSpent AS totalTimeSpent,"
			+ "  browserName, browserVersion, ipAddress,deviceName, deviceOS " + 
			"FROM " + 
			"    lti.pages p, " + 
			"    lti.page_visits pv " + 
			"WHERE " + 
			"    pv.pageId = p.id "
			+ "  AND sapid = ? " + 
			"    AND FROM_UNIXTIME(pv.initialTimeStamp / 1000) BETWEEN DATE_SUB('"+testStartedOnDate+"', INTERVAL 1 HOUR) " + 
			"	 AND DATE_ADD('"+testStartedOnDate+"', INTERVAL 1 HOUR) ";
	try {

		pVList = (List<PageVisitsBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(PageVisitsBean.class));
	} catch (Exception e) {
		
	}
	return pVList;
}

	public List<TestQuestionConfigBean>  getAllTemplates() { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> templateList = new ArrayList<TestQuestionConfigBean> ();
		String sql = "SELECT c.id,c.templateId,t.type as typeName,c.minNoOfQuestions,c.type as type,c.name,c.questionMarks FROM "
				+ "exam.test_question_config_template c " + 
				"left join exam.test_question_type t on t.id = c.type order by c.templateId desc"; 
		try {
			templateList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return templateList;
		
	}
	public List<String> getAllTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> typeIdList=new ArrayList<>();
		String sql=" SELECT id FROM exam.test_question_type group by type ";
		try { 

			typeIdList = (List<String>) jdbcTemplate.query(sql,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			
		} 
		return typeIdList; 
	}
	public List<TestQuestionConfigBean> getTemplateTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> typeIdList=new ArrayList<>(); 
		String sql="  SELECT templateId,name,testType,duration,sum(minNoOfQuestions) as minNoOfQuestions,SUM(`minNoOfQuestions` * questionMarks) AS questionMarks " +  
				"  FROM exam.test_question_config_template group by templateId";
		try {    

			typeIdList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (Exception e) {
			
		} 
		return typeIdList; 
	}

	public String createNewTemplate(final TestQuestionConfigBean bean) {
		final ArrayList<TestQuestionConfigBean> template = bean.getTestQuestionConfigBean(); 
		final ArrayList<SectionBean> sectionBeanArray = bean.getSectionBean(); 
		for(final SectionBean sectionBean : sectionBeanArray ) {
			final ArrayList<SectionBean>	sBean = sectionBean.getSectionQnTypeConfigbean();
		String sql1 = "SELECT max(templateId) as templateId from exam.test_question_config_template";
		int id=0;
		if(bean.getTemplateId()==null) { 
			try {
				id = (int) jdbcTemplate.queryForObject(sql1,new SingleColumnRowMapper(Integer.class));
			
			} catch (Exception e) {
				
			} 
		bean.setTemplateId(id+1+"");
		} 

		String sql = " INSERT INTO exam.test_question_config_template ( name,testType,templateId, type, "
				+ "minNoOfQuestions,maxNoOfQuestions,duration,questionMarks,sectionId) "
				+ " VALUES (?,?,?,?,?,?,?,?,?)"; 
		 
		int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException { 
				ps.setString(1,bean.getName());
				ps.setString(2,bean.getTestType()); 
				ps.setString(3,bean.getTemplateId());  
				ps.setString(4,sBean.get(i).getSectionQnType());
				ps.setString(5,sBean.get(i).getSectionQnCount());				
				ps.setString(6,sBean.get(i).getSectionQnCount());
				ps.setString(7,bean.getDuration());   
				ps.setString(8,sBean.get(i).getQuestionMarks());     
				ps.setString(9,sectionBean.getId()); 
			} 

			@Override
			public int getBatchSize() {
				return sBean.size();
			}
			
		});
	}
		return bean.getTemplateId();
	}
	public List<TestQuestionConfigBean> getAllQuestionTypes() {
		List<TestQuestionConfigBean> typeList=new ArrayList<TestQuestionConfigBean>();
		String sql = "SELECT id,type as typeName from exam.test_question_type"; 
		try {   
			typeList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (Exception e) {
			 
		}
		return typeList;
	}

	public void editQnTemplate(final TestQuestionConfigBean configBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//deleteQnTemplate(configBean.getTemplateId());
		//createNewTemplate(configBean); 
		String sql = "update exam.test_question_config_template set name=?,testType=?,duration=? where templateId=?"; 
		jdbcTemplate.update(sql,new Object[] {configBean.getName(),configBean.getTestType(),configBean.getDuration(),configBean.getTemplateId()
		});   
	}
	public void deleteQnTemplate( String templateId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="delete from exam.test_question_config_template where templateId=?";
		jdbcTemplate.update(sql, new Object[] {templateId});  
	}


	public ArrayList<TestExamBean> getTestsByExamYearMonthAcadsYearMonthReferenceId(TestExamBean test){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<TestExamBean> testList = new ArrayList<>();
		Object[] queryParams = new Object[] {test.getYear(), test.getMonth(), test.getAcadYear(), test.getAcadMonth()};
		
		String sql="SELECT " + 
				"    * " + 
				" FROM " + 
				"   exam.test t" + 
				"       INNER JOIN " + 
				"   exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
				"       INNER JOIN " + 
				"   acads.sessionplan_module m ON m.id = tcm.referenceId " + 
				"       INNER JOIN " + 
				"   acads.sessionplan s ON s.id = m.sessionPlanId " + 
				"       INNER JOIN " + 
				"   acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id " + 
				"       INNER JOIN " + 
				"   lti.student_subject_config ssc ON ssc.id = stm.timeboundId " + 
				" WHERE " + 
				"    t.year = ? " + 
				"    AND t.month = ? " + 
				"    AND t.acadYear = ? " + 
				"    AND t.acadMonth = ? " ;
		
		
		if(test.getReferenceId() != null) {
			sql = sql + "    AND tcm.referenceId = ? ";
			queryParams = new Object[] {test.getExamYear(), test.getExamMonth(), test.getAcadYear(), 
					test.getAcadMonth(), test.getReferenceId()};

		}
		
		try {

				testList = (ArrayList<TestExamBean>) jdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(TestExamBean.class));

			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				
			}
		
		
		
		return testList;
	}
		public List<TestQuestionConfigBean>  getTemplateById(String templateId) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> templateList = new ArrayList<TestQuestionConfigBean> ();
		String sql = "SELECT * FROM exam.test_question_config_template    " + 
				"				where templateId=? "; 
		try {
			templateList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] {templateId}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return templateList;
		
	}
	public List<TestQuestionConfigBean>  getQuestionsConfigured(Long id) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> templateList = new ArrayList<TestQuestionConfigBean> ();
		String sql = "SELECT  c.*, t.type as typeName,questionMarks " + 
				"FROM " + 
				"    exam.test_questions_configuration c " + 
				"        LEFT JOIN " + 
				"    exam.test_question_type t ON t.id = c.type " + 
				"WHERE " + 
				"    testId = ?"; 
		try {
			templateList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] {id}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return templateList;
		
	}
	public List<TestQuestionConfigBean>  getQuestionsUploadedCount(Long id) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> templateList = new ArrayList<TestQuestionConfigBean> ();
		String sql = "SELECT t.type as typeName,count(t.type) as minNoOfQuestions FROM exam.test_questions q " + 
				"left join exam.test_question_type t on t.id=q.type " + 
				"  where q.testId = ? group by q.type";  
		try { 
			templateList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] {id}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return templateList;
		
	}
	public void  validateMaxScore(TestExamBean testbean,ResponseBean response) {
		String sql = "select * from exam.test_questions_configuration WHERE " + 
				"    testId=?"; 
		
		ArrayList<StudentQuestionResponseExamBean> typeMarkMap = new ArrayList<StudentQuestionResponseExamBean> () ;
		try {
			typeMarkMap = (ArrayList<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql,new Object[] { testbean.getTestId() }, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
		} catch (DataAccessException e) {
		}
		double marks=0.0;
		if(typeMarkMap.size()>0) {
			//add marks of uploaded questions based on template
			for(StudentQuestionResponseExamBean typeMark : typeMarkMap) {
				marks = marks +typeMark.getMinNoOfQuestions()*typeMark.getQuestionMarks();
			}
		}

		if(marks==testbean.getMaxScore()) {
			response.setStatus("success");
		}else {
			response.setStatus("error");
			response.setMessage("Error! Mismatch in Test's MaxScore and Question Configured MaxScore");
		}
	}
	public void  validateQuestionsUploadedMarks(TestExamBean testbean,ResponseBean response) {
		String sql = "select sum(marks) from exam.test_questions where " + 
				"    testId=?"; 
		
		Integer qnmarks =  jdbcTemplate.queryForObject(sql,new Object[] { testbean.getTestId() }, new SingleColumnRowMapper<Integer>(Integer.class));
		

		if(qnmarks>=testbean.getMaxScore()) {
			response.setStatus("success");
		}else {
			response.setStatus("error");
			response.setMessage("Error! Mismatch in Test's MaxScore and Total marks of Questions Uploaded");
		}
	}

	//getAllStudentsByMasterKey start

	public List<StudentExamBean> getAllStudentsByMasterKey(Integer id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentExamBean> students= new ArrayList<>();
		String sql=" SELECT   " + 
				"    s.*  " + 
				"FROM  " + 
				"    exam.students s  " +
				"WHERE  " + 
				"    s.consumerProgramStructureId = ? ";
		try {
			students = (List<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(StudentExamBean.class));
		} catch (Exception e) {
			
		}

		return students;

	}

	public String incrementCountOfRefreshPage(String sapId, Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update exam.test_student_testdetails set "
						+ " "
						+ " countOfRefreshPage=countOfRefreshPage+1,"
					+ " noOfRefreshAuditTrails =concat( COALESCE(noOfRefreshAuditTrails,' '),sysdate(),' > CountOfRefreshPage : ',countOfRefreshPage,' ~ '), "
						+ " lastModifiedDate = sysdate()  "    
							
						+ " where testId=? and sapid=? and attempt=? ";
		try {
			jdbcTemplate.update(sql,new Object[] {testId,sapId,1
								});
			return "";
			
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/incrementCountOfRefreshPage";
			String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+testId + 
					",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);
			

			return "Error in saving to db : Error "+e.getMessage();
		}
	}

	//getAllStudentsByMasterKey end
	
	public long insertMailRecord(final MailBean mail){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String sql = "INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ";
		try {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, mail.getSubject());
			        statement.setString(2, mail.getCreatedBy());
			        statement.setString(3,mail.getFilterCriteria());
			        statement.setString(4,mail.getBody());
			        statement.setString(5,mail.getFromEmailId());
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		} catch (Exception e) {
			logger.info("\n"+"IN insertMailRecord error got sapid: "+mail.getCreatedBy()+". Error: "+e.getMessage());
			return 0;
		}
	}
	
	public long insertUserMailRecord(final MailBean mail, final long mailId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ";
		try {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, mail.getSapid());
			        statement.setString(2, mail.getMailId());
			        statement.setString(3, mail.getCreatedBy());
			        statement.setString(4, mail.getFromEmailId());
			        statement.setLong(5, mailId);
			        statement.setString(6, mail.getCreatedBy());
			        return statement;
			    }
			}, holder);
			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		} catch (Exception e) {
			logger.info("\n"+"IN insertUserMailRecord error got sapid: "+mail.getCreatedBy()+". Error: "+e.getMessage());
			return 0;
		}
	}

	public ArrayList<LostFocusLogExamBean> getSubjectList() throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT "
				+ "    id AS program_sem_subject_id, subject "
				+ "FROM "
				+ "    exam.program_sem_subject "
				+ "WHERE "
				+ "    consumerProgramStructureId IN (111 , 131, 151) "
				+ "        AND active = 'Y'";
		ArrayList<LostFocusLogExamBean> subjectList = (ArrayList<LostFocusLogExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(LostFocusLogExamBean.class));
		
		return subjectList;
	}
	
	//Code for saveTestForLeads Start
	public long saveTestForLeads(final TestExamBean test) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO lead.leads_test "
				+ " (year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate,  subject,"
				+ "  maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, randomQuestion,"
				+ " testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert, maxScore, duration, passScore, testType,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
				+ " consumerTypeIdFormValue, programStructureIdFormValue, programIdFormValue, "
				+ " applicableType, referenceId,"
				+ " proctoringEnabled,showCalculator  "
				+ ") "
        		+ " VALUES(?,?,?,?,?,?,?,?,?" //9 ?
        		+ "		   ,?,?,?,?,?,?" // 6 ? 
        		+ "		   ,?,?,?,?,?,?,?,?" // 8 ?
        		+ "		   ,?,sysdate(),?,sysdate()"
        		+ "		   ,?,?,?"
        		+ "		   ,?,?"
        		+ "		   ,?,?"
        		+ ") ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, test.getYear());
			        statement.setString(2, test.getMonth());
			        statement.setInt(3, test.getAcadYear());
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

			        statement.setString(31, test.getProctoringEnabled()); 
			        statement.setString(32, test.getShowCalculator());
			        return statement;
			    }
			}, holder);
			final long primaryKey = holder.getKey().longValue();

			TestExamBean testToshow = getTestById(primaryKey) ;
			

			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

		}
	//Code for saveTestForLeads end
	

	public String insertTestIdNConfigurationMappingsForLeads(final TestExamBean bean,final List<Long> configIds) {
		try {

			String sql = " INSERT INTO lead.leads_test_testid_configuration_mapping "
					+ " (testId, type, referenceId, iaType, createdBy, createdDate) "
					+ " VALUES (?,?,?,?,?,sysdate()) "
					+ " "
					;
			
			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setLong(1,bean.getId());
					ps.setString(2,bean.getApplicableType());
					
					if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
						ps.setLong(3,configIds.get(i));
						ps.setString(4,bean.getIaType()); 
					}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(3,configIds.get(i));
						ps.setString(4,bean.getIaType());
					}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(3,configIds.get(i));
						ps.setString(4,"Test"); //temporary
					}else {
						ps.setLong(3, (long)0);
					}
					ps.setString(5,bean.getCreatedBy());		
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
	
	
	public int deleteTestForLeads(Long id) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	int row=0;	
	String sql="delete from lead.leads_test where id=?";
	try {
		 row = jdbcTemplate.update(sql, new Object[] {id});



	} catch (Exception e) {
		
		return -1;
	}
	
	return row;
	}
	

	public Integer getNoOfQuestionsByTestIdForLeads(Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select count(*) from lead.leads_test_questions where testId=? and isSubQuestion = 0 Order By id desc ";
		try {
			Integer count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {testId}, Integer.class);
			return count;
		} catch (Exception e) {
			
			return 0;
		} 
	}

	public TestExamBean getTestByIdForLeads(Long id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		TestExamBean test=null;
		String sql="select * from lead.leads_test where id=?";
		try {
			 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestExamBean.class));

			 //test = getReference_Batch_Or_Module_Name(test);
		} catch (Exception e) {
			//
			logger.info("\n"+SERVER+": "+"IN getTestById got id "+id+", Error :"+e.getMessage());
		}
		return test;
	}
	

	public HashMap<Long,TestTypeBean> getTypeIdNTypeMapForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from lead.leads_test_question_type ";
				try {
			List<TestTypeBean> testTypeList = (List<TestTypeBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeBean.class));
			HashMap<Long,TestTypeBean> testTypeMap = new HashMap<>();
			for(TestTypeBean bean : testTypeList) {
				testTypeMap.put(bean.getId(), bean);
			}
			
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	public List<TestQuestionExamBean> getTestQuestionsForLeads(Long testId){
		
		//return null on error.
		List<TestQuestionExamBean> testQuestionsList=new ArrayList<>();
		
		
		//1.get data from redis 
		//TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		
		//testQuestionsList = daoForRedis.findAllTestQuestionsByTestId(testId);
		
		
		//2. check if questions are present 
		/*if(testQuestionsList != null && testQuestionsList.size() > 0) {
			return testQuestionsList;
		}*/
		
		//3. if not present get question from mysql db
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select q.*,c.url as url "
				+ " from lead.leads_test_questions q " + 
				"   LEFT JOIN lead.leads_test_question_additionalcontent c " + 
				"	on q.id=c.questionId " + 
				"	where q.testId=? and q.isSubQuestion = 0 "
				+ " Order By q.id ";
		
		try {

			testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
			
			if(testQuestionsList !=null) {

			}else {

			}
			for(TestQuestionExamBean question : testQuestionsList) {

				if(question.getType() == 6 || question.getType() == 7) {

				}
				List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionIdForLeads(question.getId());
				question.setOptionsList(options);
				/*if(question.getType()==3) {
					question.setSubQuestionsList(getTestSubQuestions(question.getId()));
				}*/
			}
		} catch (Exception e) {
			//
			logger.info("\n"+SERVER+": "+" IN getTestQuestions   got testId : "+testId+" , Error :  "+e.getMessage());
			
		}
		return testQuestionsList;
	}
	

	public List<TestQuestionOptionExamBean> getTestQuestionOptionByQuestionIdForLeads( Long questionId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionOptionExamBean> optionList=null;
		String sql="select * from lead.leads_test_question_options where questionId=? Order By id ";
		try {
			optionList = (List<TestQuestionOptionExamBean>) jdbcTemplate.query(sql, new Object[] {questionId}, new BeanPropertyRowMapper(TestQuestionOptionExamBean.class));
			
			if(optionList != null) {

			}else {

				optionList = getRandomizedOptions(optionList);
			}
		
		} catch (Exception e) {
			
		}
		return optionList;
	}
	

	public HashMap<String,TestTypeBean> getTestTypesMapForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from lead.leads_test_question_type ";
				try {
			List<TestTypeBean> testTypeList = (List<TestTypeBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeBean.class));
			HashMap<String,TestTypeBean> testTypeMap = new HashMap<>();
			for(TestTypeBean bean : testTypeList) {
				testTypeMap.put(bean.getType(), bean);
			}
			
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateTestQuestionForLeads(final ArrayList<TestQuestionExamBean> testQuestiontList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (TestQuestionExamBean bean : testQuestiontList) {
			try{
				long returnedKey = saveTestQuestionForLeads(bean);
				if(returnedKey == 0) {
					errorList.add(i+"");
				}else {
					//adding option
					List<String> optionErrorList = batchUpdateTestQuestionOptionForLeads(bean.getOptionsList(),returnedKey);
					if(!optionErrorList.isEmpty()) {
						errorList.add(i+"");
					}
					
					//add entry if question is casestudy
					/*
					if(bean.getIsSubQuestion() == 1) {
						TestQuestionCaseStudyBean csd = new TestQuestionCaseStudyBean();
						csd.setQuestionId(bean.getMainQuestionId());
						csd.setSubQuestionId(returnedKey);
						long caseStudySaved = saveTestCaseStudyDetails(csd);
						if(caseStudySaved == 0) {
							errorList.add(i+"");
						}
					}*/
				}
			}catch(Exception e){
				
				errorList.add(i+"");
			}
			i++;
		}
		return errorList;

	}

	
	public long saveTestQuestionForLeads(final TestQuestionExamBean testQuestion) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		//id, testId, description, marks, type, chapter, option1, option2, option3, option4, option5, option6, option7, option8, correctOption, active, createdBy, createdDate, lastModifiedBy, lastModifiedDate
//		final String sql = "INSERT INTO exam.test_questions "
//				+ " (testId, description, marks, type,  "
//				+ " chapter, question, correctOption,active,isSubQuestion, "
//				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
//        		+ " VALUES(?,?,?,?,"
//        		+ "		   ?,?,?,'Y',?,"
//        		+ "		   ?,sysdate(),?,sysdate()) ";
		final String sql = "INSERT INTO lead.leads_test_questions "
				+ " (testId, description, marks, type,  "
				+ " chapter, question, correctOption,active,isSubQuestion, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,uploadType) "
        		+ " VALUES(?,?,?,?,"
        		+ "		   ?,?,?,?,?,"
        		+ "		   ?,?,?,?,?) ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setLong(1, testQuestion.getTestId());
			        statement.setString(2, testQuestion.getDescription().trim());
			        statement.setDouble(3, testQuestion.getMarks());
			        statement.setInt(4, testQuestion.getType()); 
			        statement.setString(5, testQuestion.getChapter()); 
			        statement.setString(6, testQuestion.getQuestion().trim());    
			        statement.setString(7, testQuestion.getCorrectOption());
			        statement.setString(8, testQuestion.getActive());
			        statement.setInt(9, testQuestion.getIsSubQuestion());
			        statement.setString(10, testQuestion.getCreatedBy());
			        statement.setString(11, testQuestion.getCreatedDate());
			        statement.setString(12, testQuestion.getLastModifiedBy());
			        statement.setString(13, testQuestion.getLastModifiedDate());
			        statement.setString(14, testQuestion.getUploadType());
			        
			        
			        return statement;
			    }
			}, holder);
			long primaryKey = holder.getKey().longValue();

			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

		}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<String> batchUpdateTestQuestionOptionForLeads(final List<TestQuestionOptionExamBean> optionList,long questionId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		List<String> errorList = new ArrayList<>();

		for (TestQuestionOptionExamBean bean : optionList) {
			try{
				bean.setQuestionId(questionId);
				long returnedKey = saveTestQuestionOptionForLeads(bean);
				if(returnedKey == 0) {
					errorList.add("Error in adding "+i+" option to DB.");
				}
			}catch(Exception e){
				
				errorList.add("Error in adding "+i+" option to DB.");
			}
			i++;
		}
		return errorList;

	}
	
	
	public long saveTestQuestionOptionForLeads(final TestQuestionOptionExamBean option) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			//id, questionId, option, isCorrect
			final String sql = "INSERT INTO lead.leads_test_question_options "
					+ " (questionId, optionData, isCorrect) "
	        		+ " VALUES("+option.getQuestionId().toString()+",'"+option.getOptionData().replaceAll("'", "").trim()+"','"+option.getIsCorrect()+"') ";

			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        return statement;
			    }
			}, holder);
			long primaryKey = holder.getKey().longValue();

			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

		}

	public List<TestExamBean> getTestsLiveConfigListForLeads(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestExamBean> testsLiveConfigList = new ArrayList<>();
		List<TestExamBean> testsLiveConfigListForOld = new ArrayList<>();
		//List<TestBean> testsLiveConfigListForModule = new ArrayList<>();
		//List<TestBean> testsLiveConfigListForBatch = new ArrayList<>();
		
		String sqlForOldConfig =  "select tls.acadYear,tls.acadMonth,tls.examYear,tls.examMonth,tls.liveType,tls.applicableType, "
				+ " pss.subject,"
				+ "p.code as program, p.id as programId,"
				+ "p_s.program_structure as programStructure, p_s.id as programStructureId,"
				+ "c_t.name as consumerType, c_t.id as consumerTypeId "
				+ "from lead.leads_test_live_settings as tls "+
				"        LEFT JOIN " + 
				"    exam.program_sem_subject AS pss ON pss.id = tls.referenceId " + 
				"        LEFT JOIN " + 
				"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = pss.consumerProgramStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ " where tls.applicableType = 'old'  order by tls.lastModifiedDate desc";
		
		try {
			testsLiveConfigListForOld = (List<TestExamBean>) jdbcTemplate.query(sqlForOldConfig, 
					new BeanPropertyRowMapper(TestExamBean.class));
			

		} catch (Exception e) {
			
		}
		
		/*
		String sqlForBatchConfig =  "SELECT  " + 
				"    tls.acadYear, " + 
				"    tls.acadMonth, " + 
				"    tls.examYear, " + 
				"    tls.examMonth, " + 
				"    tls.liveType, " + 
				"    tls.applicableType, " + 
				"    p.code AS program, " + 
				"    p.id AS programId, " + 
				"    p_s.program_structure AS programStructure, " + 
				"    p_s.id AS programStructureId, " + 
				"    c_t.name AS consumerType, " + 
				"    c_t.id AS consumerTypeId, " + 
				"    b.name, " + 
				"    b.id AS referenceId, " + 
				"    pss.subject " + 
				"FROM " + 
				"    exam.test_live_settings AS tls " + 
				"        LEFT JOIN " + 
				"    lti.student_subject_config AS ssc ON ssc.id = tls.referenceId " + 
				"        LEFT JOIN " + 
				"    exam.program_sem_subject AS pss ON pss.id = ssc.prgm_sem_subj_id " + 
				"        LEFT JOIN " + 
				"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = pss.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    exam.program AS p ON p.id = c_p_s.programId " + 
				"        LEFT JOIN " + 
				"    exam.program_structure AS p_s ON p_s.id = c_p_s.programStructureId " + 
				"        LEFT JOIN " + 
				"    exam.consumer_type AS c_t ON c_t.id = c_p_s.consumerTypeId " + 
				"        LEFT JOIN " + 
				"    exam.batch AS b ON b.id = ssc.batchId " + 
				"WHERE " + 
				"    tls.applicableType = 'batch'  order by tls.lastModifiedDate desc" + 
				"      " + 
				"";
		
		try {
			testsLiveConfigListForBatch = (List<TestBean>) jdbcTemplate.query(sqlForBatchConfig, 
					new BeanPropertyRowMapper(TestBean.class));
			

		} catch (Exception e) {
			
		}

		
		String sqlForModuleConfig =  "select tls.acadYear,tls.acadMonth,tls.examYear,tls.examMonth,tls.liveType,tls.applicableType,"
				+ "p.code as program, p.id as programId,"
				+ "p_s.program_structure as programStructure, p_s.id as programStructureId,"
				+ "c_t.name as consumerType, c_t.id as consumerTypeId, "
				+ "pss.subject,"
				+ "m.topic,m.id as referenceId, "
				+ "b.name,b.id as moduleBatchId "
				+ "from exam.test_live_settings as tls "
				+ "left join acads.sessionplan_module m on m.id = tls.referenceId "
				+ "left join acads.sessionplan s on s.id = m.sessionPlanId "
				+ "left join acads.sessionplanid_timeboundid_mapping stm on stm.sessionPlanId = s.id "
				+ "left join lti.student_subject_config as ssc on ssc.id = stm.timeboundId "
				+ "left join exam.program_sem_subject as pss on pss.id = ssc.prgm_sem_subj_id "
				+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = pss.consumerProgramStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "left join exam.batch as b on b.id = ssc.batchId "
				+ " where tls.applicableType = 'module' order by tls.lastModifiedDate desc";
		
		try {
			testsLiveConfigListForModule = (List<TestBean>) jdbcTemplate.query(sqlForModuleConfig, 
					new BeanPropertyRowMapper(TestBean.class));
			

		} catch (Exception e) {
			
		}*/
		
		//testsLiveConfigList.addAll(testsLiveConfigListForModule);

		//testsLiveConfigList.addAll(testsLiveConfigListForBatch);

		testsLiveConfigList.addAll(testsLiveConfigListForOld);

		return testsLiveConfigList;  
		
	}
	
	
	public String insertTestLiveConfigForLeads(final TestExamBean bean,final List<Long> configIds) {
		try {
			String sql = " INSERT INTO lead.leads_test_live_settings "
					+ " (acadYear, acadMonth, examYear, examMonth, liveType, applicableType, referenceId, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
					+ " VALUES (?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
					+ " on duplicate key update "
					+ " acadYear=?, acadMonth=?, examYear=?, examMonth=?, createdBy=?, createdDate=sysdate(), lastModifiedBy=?, lastModifiedDate=sysdate()"
					;
			
			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					
					ps.setInt(1,bean.getAcadYear());
					ps.setString(2,bean.getAcadMonth());
					ps.setInt(3,bean.getExamYear());
					ps.setString(4,bean.getExamMonth());
					ps.setString(5,bean.getLiveType());
					ps.setString(6,bean.getApplicableType());
					
					if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
						ps.setLong(7,configIds.get(i));
					}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(7,configIds.get(i));
					}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
						ps.setLong(7,configIds.get(i));
					}else {
						ps.setLong(7, (long)0);
					}
					ps.setString(8,bean.getCreatedBy());
					ps.setString(9,bean.getLastModifiedBy());
					
					//on update
					ps.setInt(10,bean.getAcadYear());
					ps.setString(11,bean.getAcadMonth());
					ps.setInt(12,bean.getExamYear());
					ps.setString(13,bean.getExamMonth());
					ps.setString(14,bean.getCreatedBy());
					ps.setString(15,bean.getLastModifiedBy());
				
				}

				@Override
				public int getBatchSize() {
					return configIds.size();
				}
			  });

			return "";
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return "Error in insertTestLiveConfig : "+e.getMessage();
		}
	
	}
	
	public List<TestExamBean> getAllTestsForLeads(){//get all live tests
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestExamBean> testsList=null;
		String sql="SELECT t.* FROM lead.leads_test t " + 
				" order by t.id desc ";
		try {  
			 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestExamBean.class));
		} catch (Exception e) {
			
		}
		return testsList;
	}
	public StudentsTestDetailsExamBean getStudentsTestDetailsBySapidAndTestIdForLeads(String sapid, Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentsTestDetailsExamBean testByStudent= new StudentsTestDetailsExamBean();
		String sql="select * from lead.leads_test_student_testdetails where sapid=? and testId=? Order By id desc limit 1 ";//get latest entry of students attempts
		try {
			testByStudent = (StudentsTestDetailsExamBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			
		} catch (Exception e) {
		/*
		 * StringWriter errors = new StringWriter(); e.printStackTrace(new
		 * PrintWriter(errors)); String apiCalled =
		 * "testDao/getStudentsTestDetailsBySapidAndTestId"; String stackTrace =
		 * "apiCalled="+ apiCalled + ",data= testId: " + testId + ",errors=" +
		 * errors.toString(); setObjectAndCallLogError(stackTrace,sapid); //

		 */		
			logger.info("\n"+SERVER+": "+"IN getStudentsTestDetailsBySapidAndTestId got sapid  "+sapid+" testId: "+testId+" Error: "+e.getMessage());
				
		}
		return testByStudent;
	}
	

	public List<StudentsTestDetailsExamBean> getAttemptsDetailsBySapidNTestIdForLeads(String sapid,Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentsTestDetailsExamBean> testsByStudent=null;
		String sql="select * from lead.leads_test_student_testdetails where sapid=? and testId = ?  Order By id ";
		try {
			testsByStudent = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
		} catch (Exception e) {
			//
			logger.info("\n"+SERVER+": "+" IN getAttemptsDetailsBySapidNTestId   got testId : "+testId+" sapId : "+sapid+", Error :  "+e.getMessage());
		}
		return testsByStudent;
	}
	

	public HashMap<Integer,List<StudentQuestionResponseExamBean>> getAttemptAnswersMapBySapidNTestIdForLeads(String sapid,Long testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
		HashMap<Integer,List<StudentQuestionResponseExamBean>> attemptsAnswerMap = new HashMap<>();
		String sql="";
		
		/*
		//check if test is for current active subject or not
		String isTestForCurrnetActiveSubject = isTestForCurrnetActiveSubject(testId); 
		if(StringUtils.isNumeric(isTestForCurrnetActiveSubject)) {
		
			Integer countToCheckIfTestIsForCurrentActiveSubject = Integer.parseInt(isTestForCurrnetActiveSubject);
			if(countToCheckIfTestIsForCurrentActiveSubject > 0) {
				sql="select * from exam.test_students_answers where sapid=? and testId = ? Order By id ";
			}else {
				sql="select * from exam.test_students_answers_archive where sapid=? and testId = ? Order By id ";
			}
			
		}else {
			sql="select * from exam.test_students_answers_archive where sapid=? and testId = ? Order By id ";
		}
		*/
		sql="SELECT  " + 
				"    * " + 
				"FROM " + 
				"    (SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        lead.leads_test_students_answers " + 
				"    WHERE " + 
				"        sapid = ? AND testId = ? " + 
				"	UNION ALL "
				+ "  SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        lead.leads_test_students_answers_archive " + 
				"    WHERE " + 
				"        sapid = ? AND testId = ? " + 
				"    ) a order by a.id";
		
		try {
			testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId,sapid,testId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
			List<StudentQuestionResponseExamBean> tempList= null;
			for(StudentQuestionResponseExamBean a : testAnswersByStudent) {
				if(attemptsAnswerMap.containsKey(a.getAttempt())) {
					tempList= attemptsAnswerMap.get(a.getAttempt());
				}else {
					tempList = new ArrayList<>();
				}
				tempList.add(a);
				attemptsAnswerMap.put(a.getAttempt(), tempList);
			}
		} catch (Exception e) {
			//
			logger.info("\n"+SERVER+": "+" IN getAttemptAnswersMapBySapidNTestId   got testId : "+testId+" sapId : "+sapid+", Error :  "+e.getMessage());
			
		}
		return attemptsAnswerMap;
	}


	public List<TestQuestionExamBean> getTestQuestionsPerAttemptForLeads(String questionIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionExamBean> testQuestionsList=new ArrayList<>();
		if(StringUtils.isBlank(questionIds)) {
			return testQuestionsList;
		}
		//String sql="select * from exam.test_questions where id in ("+questionIds+") and isSubQuestion = 0 Order By id ";
		String sql="select q.*,c.url as url "
				+ " from lead.leads_test_questions q " + 
				"   LEFT JOIN lead.leads_test_question_additionalcontent c " + 
				"	on q.id = c.questionId " + 
				"	where q.id in ("+questionIds+") and q.isSubQuestion = 0 "
				+ " Order By q.id ";
		
		try {
			testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
			
			if(testQuestionsList !=null) {

			}else {


			}
			for(TestQuestionExamBean question : testQuestionsList) {
				List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionIdForLeads(question.getId());
				question.setOptionsList(options);
				if(question.getType()==3) {
					question.setSubQuestionsList(getTestSubQuestions(question.getId()));
				}
			}
		} catch (Exception e) {
			
		}
		return testQuestionsList;
	}
	

	public String incrementCountOfRefreshPageForLeads(String sapId, Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_student_testdetails set "
						+ " "
						+ " countOfRefreshPage=countOfRefreshPage+1, "
						+ " lastModifiedDate = sysdate()  "    
							
						+ " where testId=? and sapid=? and attempt=? ";
		try {
			jdbcTemplate.update(sql,new Object[] {testId,sapId,1
								});
			return "";
			
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/incrementCountOfRefreshPageForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+testId + 
					",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);
			

			return "Error in saving to db : Error "+e.getMessage();
		}
	}
	
	public List<TestQuestionConfigBean>  getQuestionConfigsListByTestIdForLeads(Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> typesList=new ArrayList<>();
		String sql = "select * from lead.leads_test_questions_configuration where testId=? ";
		try {
			typesList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));

		} catch (Exception e) {
			
		}
		return typesList;
	}
	
	public long saveStudentsTestDetailsForLeads(final StudentsTestDetailsExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO lead.leads_test_student_testdetails "
				+ " ( sapid, testId, attempt, active, testStartedOn,  testCompleted, score, testQuestions, showResult,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
				+ " remainingTime) "
        		+ " VALUES(?,?,?,?,sysdate(),?,?,?,?,"
        		+ "		   ?,sysdate(),?,sysdate(),"
        		+ "		   ?) ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, bean.getSapid());
			        statement.setLong(2, bean.getTestId());
			        statement.setInt(3, bean.getAttempt());
			        statement.setString(4, bean.getActive()); 
			        statement.setString(5, bean.getTestCompleted()); 
			        statement.setDouble(6, bean.getScore()); 
			        statement.setString(7, bean.getTestQuestions()); 
			        statement.setString(8, bean.getShowResult()); 
			        statement.setString(9, bean.getCreatedBy());
			        statement.setString(10, bean.getLastModifiedBy());
			        statement.setInt(11, bean.getRemainingTime());     
			        return statement;
			    }
			}, holder);
			long primaryKey = holder.getKey().longValue();

			return primaryKey;
		} catch (Exception e) {
			//

			logger.info("\n"+SERVER+": "+" IN saveStudentsTestDetailsForLeads  got testId : "+bean.getTestId()+" sapId:  "+bean.getSapid()+", Error :  "+e.getMessage());
			
			return 0;
		}

		}
	

	public boolean updateStudentsTestDetailsRemainingTimeForLeads(int remaingTime,Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_student_testdetails set "
						+ " "
						+ "	remainingTime=?,"
						+ " lastModifiedDate = sysdate()  "    
							
						+ " where id=?  ";
		try {
			jdbcTemplate.update(sql,new Object[] {remaingTime,id});

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//
			logger.info("\n"+SERVER+": "+" IN updateStudentsTestDetailsRemainingTime remaingTime:  "+remaingTime+" id : "+id);
			
		}
		return false;
	}


	
	public int getCountOfAnswersBySapidAndQuestionIdNAttemptForLeads(String sapid, Long questionId, int attempt){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
		String sql="select count(id) from lead.leads_test_students_answers where sapid=? and questionId=? and attempt = ? Order By id ";
		try {
			//testAnswersByStudent = (List<StudentQuestionResponseBean>) jdbcTemplate.query(sql, new Object[] {sapid,questionId,attempt}, new BeanPropertyRowMapper(StudentQuestionResponseBean.class));
			int count = (Integer) jdbcTemplate.queryForObject(sql, new Object[] {sapid,questionId,attempt}, Integer.class);
			return count;	
		} catch (Exception e) {
			logger.info("\n"+SERVER+": "+" IN getCountOfAnswersBySapidAndQuestionIdNAttempt sapid:  "+sapid+" questionId : "+questionId);
			return 0;
		}

	}

	public long saveStudentsTestAnswerForLeads(final StudentQuestionResponseExamBean studentsAnswer) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		String answer = studentsAnswer.getAnswer();
		String answerWithRemovedSpecialCharacters ="";
		answerWithRemovedSpecialCharacters = answer.replaceAll("'", " ");
		answerWithRemovedSpecialCharacters =answerWithRemovedSpecialCharacters.replaceAll("\'", " ");
		answerWithRemovedSpecialCharacters = answerWithRemovedSpecialCharacters.replaceAll("\"", " ");
		answerWithRemovedSpecialCharacters = answerWithRemovedSpecialCharacters.replaceAll(";", " ");
		
		
		

		try {
			final String sql = "INSERT INTO lead.leads_test_students_answers "
					+ " (sapid, testId, questionId, attempt, answer, "
					+ "  createdBy, createdDate, lastModifiedBy, lastModifiedDate, "
					+ "  facultyId )"
					+ " VALUES( "
					+ " '"+studentsAnswer.getSapid()+"','"+studentsAnswer.getTestId()+"', "+studentsAnswer.getQuestionId()+", "+studentsAnswer.getAttempt()+", '"+answerWithRemovedSpecialCharacters+"', "
					+ " '"+studentsAnswer.getSapid()+"',sysdate(), '"+studentsAnswer.getSapid()+"',sysdate(), "
					+ " '' "
					+ ") ";

			
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						return statement;
			    }
			}, holder);
			
			 
			long primaryKey = holder.getKey().longValue();

			return primaryKey;
		} catch (Exception e) {

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/saveStudentsTestAnswerForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= StudentQuestionResponseBean: "+studentsAnswer.toString()  +
					",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,studentsAnswer.getSapid());

//			
			return 0;
		}

	}
	
	public boolean updateStudentsTestDetailsNoOfQuestionsAttemptedForLeads(Long testId, String sapId, int attempt,Long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_student_testdetails set "
						+ "	noOfQuestionsAttempted = noOfQuestionsAttempted + 1,"
						+ " lastModifiedDate = sysdate()  "    
						+ " where testId=? and sapid=? and attempt = ? and "+ questionId +" not in (select id from exam.test_questions where isSubQuestion = 1) ";
		try {

			jdbcTemplate.update(sql,new Object[] {testId,sapId,attempt});

			return true;						 
		} catch (Exception e) {
			// TODO Auto-generated catch block

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/updateStudentsTestDetailsNoOfQuestionsAttemptedForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= questionid: " +questionId + 
					",testId: "+ testId + ",attempt: " + attempt  + ",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);

//			
		}
		return false;
	}
	
	public boolean deleteStudentsAnswersBySapidQuestionIdForLeads(String sapId,Long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from lead.leads_test_students_answers where sapid=? and ( questionId=? "
				+ " or ( questionId in ( select q.subQuestionsId from lead.leads_test_question_casestudydetails q where q.questionId= ? ) ) "
				+ ")";
		
		
		try {
			 row = jdbcTemplate.update(sql, new Object[] {sapId,questionId,questionId});
			

		} catch (Exception e) {

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/deleteStudentsAnswersBySapidQuestionIdForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= questionid: " +questionId+  ",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);

//			
			return false;
		}

		return true;
		}
	
	public boolean updateStudentsQuestionResponseForLeads(StudentQuestionResponseExamBean studentsAnswer) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_students_answers set "
						+ " answer=?, "
						+ " lastModifiedBy=?, "
						+ " lastModifiedDate = sysdate() "    
							
						+ " where questionId=? and sapid=? and attempt = ?";
		try {
			jdbcTemplate.update(sql,new Object[] {studentsAnswer.getAnswer(),
												  studentsAnswer.getSapid(),
												  studentsAnswer.getQuestionId(),
												  studentsAnswer.getSapid(),
												  studentsAnswer.getAttempt()
								});

			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/updateStudentsQuestionResponseForLeads";
			String stackTrace = "apiCalled="+ apiCalled +  ",data= StudentQuestionResponseBean: "+ studentsAnswer.toString() +
					",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,studentsAnswer.getSapid());

//			
		}
		return false;
	}

	public boolean updateStudentsTestDetailsCurrentQuestionForLeads(Long currentQuestion,Long testId, String sapId, int attempt) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_student_testdetails set "
						+ "	currentQuestion = ?,"
						+ " lastModifiedDate = sysdate()  "    
						+ " where testId=? and sapid=? and attempt = ?  ";
		try {
			jdbcTemplate.update(sql,new Object[] {currentQuestion,testId,sapId,attempt});

			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/updateStudentsTestDetailsCurrentQuestionForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= currentQuestion: "+currentQuestion + 
					", testId: "+ testId + ", attempt: "+ attempt  +",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);

//			
		}
		return false;
	}


	public boolean updateNoOfQuestionAttemptedBySapidTestIdForLeads(String sapId, Long testId, int attempt) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		String sqlForTestDetailsUpdate=" update lead.leads_test_student_testdetails "
				+ "	set noOfQuestionsAttempted = noOfQuestionsAttempted - 1 "
				+ " where sapid = ? and testId = ? and attempt = ?  ";
		
		
		try {
		
			 jdbcTemplate.update(sqlForTestDetailsUpdate, new Object[] {sapId,testId,attempt});
			

		} catch (Exception e) {

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/updateNoOfQuestionAttemptedBySapidTestIdForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+testId+ ",attempt:"+ attempt 
					+ ",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,sapId);


//			
			return false;
		}
		return true;
		}

	public boolean updateStudentsTestDetailsForLeads(StudentsTestDetailsExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update lead.leads_test_student_testdetails set "
						+ " "
						+ "	testEndedOn=sysdate(),"
						+ "	testCompleted=?, "
						+ "	score=?, "
						+ "	testQuestions=?, "
						+ " lastModifiedBy=?, "
						+ " showResult=?, "
						+ " lastModifiedDate = sysdate()  "    
							
						+ " where testId=? and sapid=? and attempt=? ";
		try {
			jdbcTemplate.update(sql,new Object[] {
												  bean.getTestCompleted(),
												  bean.getScore(),
												  bean.getTestQuestions(),
												  bean.getLastModifiedBy(),
												  bean.getShowResult(),
												  
												  bean.getTestId(),bean.getSapid(),bean.getAttempt()
								});



			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block



			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String apiCalled = "testDao/updateStudentsTestDetailsForLeads";
			String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+bean.getTestId() +
					",questions: " + bean.getTestQuestions() + ",score: "+bean.getScore()+",attempt: "+ bean.getAttempt() + 
					",errors=" + errors.toString();
			setObjectAndCallLogError(stackTrace,bean.getSapid());



//			
		}
		return false;
	}

	public StudentExamBean getLeadsDataByLeadId(String leadId) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `lead`.`leads` where leadId=? limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {leadId},new BeanPropertyRowMapper<>(StudentExamBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
		
	}


	public int caluclateTestScoreForLeads(String sapid, Long testId) {
		int score=0;
		TestExamBean test = getTestByIdForLeads(testId);
		
		StudentsTestDetailsExamBean studentsTestDetails =  getStudentsTestDetailsBySapidAndTestIdForLeads(sapid, testId);
		
		if(studentsTestDetails !=null) {
			if("CopyCase".equalsIgnoreCase(studentsTestDetails.getAttemptStatus()) ) {
				return score;
			}
		}
		
		List<TestQuestionExamBean> testQuestions = getStudentSpecifcTestQuestionsForLeads(studentsTestDetails.getTestQuestions());
		HashMap<Long,List<StudentQuestionResponseExamBean>> questionIdAndAnswersByStudentMap =  getTestAnswerBySapidForLeads(sapid, testId,studentsTestDetails.getAttempt());
		for(TestQuestionExamBean question : testQuestions){
			List<StudentQuestionResponseExamBean> answers = questionIdAndAnswersByStudentMap.get(question.getId());
			if(answers!=null) {
				if(question.getType() == 3 ) { // 3 is for CaseStudy
					score+=checkType3Question(question, questionIdAndAnswersByStudentMap);
				}else if(question.getType() == 2 || question.getType() == 6 || question.getType() == 7  ) { //  for MULTISELECT,Image,Video 
					score+=checkType1n2Question(question,answers);
				}
				else if(question.getType() == 1 || question.getType() == 5 ) { // 1 is for SINGLESELECT, 5 for TrueOrFalse
					if(answers.size()==1) {
						score+=checkType1n2Question(question,answers);
					}
				}
				else if(question.getType() == 4 || question.getType() == 8  ) { // 4 is for DESCRIPTIVE
					if(answers.size()==1) {



						score+=answers.get(0).getMarks();



					}
				}
				
				
			}



		}



		if(score > test.getMaxScore()) {
			score = test.getMaxScore();
		}
		return score;
	}


	public List<TestQuestionExamBean> getStudentSpecifcTestQuestionsForLeads(String testQuestionIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionExamBean> testQuestionsList=null;
		String sql="select * from lead.leads_test_questions where id in ("+testQuestionIds+") ";
		try {
			testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
			for(TestQuestionExamBean question : testQuestionsList) {
				question.setOptionsList(getTestQuestionOptionByQuestionIdForLeads(question.getId()));
				question.setSubQuestionsList(getTestSubQuestions(question.getId()));
			}
		} catch (Exception e) {
			
		}
		return testQuestionsList;
	}


	public HashMap<Long,List<StudentQuestionResponseExamBean>> getTestAnswerBySapidForLeads(String sapid, Long testId, int attempt){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentQuestionResponseExamBean> testAnswersByStudent=null;
		String sql="select * from lead.leads_test_students_answers where sapid=? and testId=? and attempt = ? Order By id ";
		HashMap<Long,List<StudentQuestionResponseExamBean>> questionIdAndAnswersByStudentMap = new HashMap<>();
		List<StudentQuestionResponseExamBean> answers =null;
		try {
			testAnswersByStudent = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId,attempt}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));
			for(StudentQuestionResponseExamBean bean : testAnswersByStudent) {
				if(questionIdAndAnswersByStudentMap.containsKey(bean.getQuestionId())) {
					answers = questionIdAndAnswersByStudentMap.get(bean.getQuestionId());
					answers.add(bean);
					
				}else {
					answers = new ArrayList<>();
					answers.add(bean);
				}
				questionIdAndAnswersByStudentMap.put(bean.getQuestionId(), answers);
				
			}
		} catch (Exception e) {
			
		}
		return questionIdAndAnswersByStudentMap;
	}
	
	// Start getTestForLeadsById for editTest By Abhay
		public TestExamBean getTestForLeadsById(Long id){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestExamBean test=null;
			String sql="select * from lead.leads_test where id=?";
			try {
				 test = (TestExamBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestExamBean.class));




				 test = getReference_Batch_Or_Module_Name(test);
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+"IN getTestForLeadsById got id "+id+", Error :"+e.getMessage());
			}
			return test;
		}
		// End getTestForLeadsById for editTest By Abhay
		
		// Start getQuestionsConfiguredForLeads for editTest By Abhay
		public List<TestQuestionConfigBean>  getQuestionsConfiguredForLeads(Long id) { 
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestQuestionConfigBean> templateList = new ArrayList<TestQuestionConfigBean> ();
			String sql = "SELECT  c.*, t.type as typeName " + 
					"FROM " + 
					"    lead.leads_test_questions_configuration c " + 
					"        LEFT JOIN " + 
					"    lead.leads_test_question_type t ON t.id = c.type " + 
					"WHERE " + 
					"    testId = ?"; 
			try {
				templateList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] {id}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
			} catch (DataAccessException e) { 
				  
			} 
			
			return templateList;
			
		}
		
		// End getQuestionsConfiguredForLeads for editTest By Abhay
		
		// Start updateTestForLeads for editTest By Abhay
		public boolean updateTestForLeads(TestExamBean test) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update lead.leads_test set "
							+ " year=?, "
							+ " month=?, "
							+ "testName=?, " 
							+ "testDescription=?, " 
							+ "startDate=?, "
							+ "endDate=?, " 
							+ "maxQuestnToShow=?, "
							+ "showResultsToStudents=?, "
							+ " active=?, "
							+ " facultyId=?, "
							+ "maxAttempt=?, " 
							+ "randomQuestion=?, " 
							+ "testQuestionWeightageReq=?, "
							+ "allowAfterEndDate=?, "
							+ "sendEmailAlert=?, " 
							+ "sendSmsAlert=?, "
							+ "maxScore=?, "
							+ "duration=?, " 
							+ "passScore=?, "
							+ "testType=?, " 
							+ "lastModifiedBy=?,"
							+ " lastModifiedDate = sysdate(),"
							+ " consumerTypeIdFormValue=?, programStructureIdFormValue=?, programIdFormValue=?,"
							+ " applicableType=?, referenceId=?, "
//							adding missing acad yr/month for update
							+ " acadMonth=?, acadYear=? ,testType=?,"
							+ " proctoringEnabled=?, showCalculator=?, "  
							+ " subject = ? "	
							+ "where id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {test.getYear(),test.getMonth(),test.getTestName(),
													  test.getTestDescription(),test.getStartDate(),test.getEndDate(),
													  test.getMaxQuestnToShow(),
													  test.getShowResultsToStudents(),test.getActive(),test.getFacultyId(), 
													  test.getMaxAttempt(),test.getRandomQuestion(),test.getTestQuestionWeightageReq(),
													  test.getAllowAfterEndDate(),test.getSendEmailAlert(),test.getSendSmsAlert(),
													  test.getMaxScore(),test.getDuration(),test.getPassScore(),test.getTestType(),test.getLastModifiedBy(),
													  test.getConsumerTypeIdFormValue(),test.getProgramStructureIdFormValue(),test.getProgramIdFormValue(),
													  test.getApplicableType(),test.getReferenceId(),
													  test.getAcadMonth(),test.getAcadYear(),test.getTestType(),
													  test.getProctoringEnabled(),test.getShowCalculator(),
													  test.getSubject(),// added By Abhay
													  test.getId()  
									});



				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		// End updateTestForLeads for editTest By Abhay
	
		// Start deleteTestIdNConfigMappingbyTestIdForLeads for editTest By Abhay
		public String deleteTestIdNConfigMappingbyTestIdForLeads(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from lead.leads_test_testid_configuration_mapping where testId=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});



				 return "";
			} catch (Exception e) {
				
				return "Error in deleteTestIdNConfigMappingbyTestIdForLeads : "+e.getMessage();
			}
		}
		// End deleteTestIdNConfigMappingbyTestIdForLeads for editTest By Abhay
		
		// Start deleteTestQuestionForLeads by Abhay
		public String deleteTestQuestionForLeads(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql1 = "DELETE FROM `lead`.`leads_test_questions` WHERE `id`= ? ";
				String sql2 = "DELETE FROM `lead`.`leads_test_question_options` WHERE `questionId`= ? ";
				String sql3 = "DELETE FROM `lead`.`leads_test_question_additionalcontent` WHERE `questionId`= ? ";
				jdbcTemplate.update(sql3, new Object[] {id});
				jdbcTemplate.update(sql2, new Object[] {id});
				jdbcTemplate.update(sql1, new Object[] {id});
				return "true";
			}catch(Exception e) {
				
				return "Error in deleteTestQuestionForLeads : "+e.getMessage();
			}
		}
		// End deleteTestQuestionForLeads by Abhay	

		public List<TestQuestionExamBean> getTestQuestionsWithOutQuestionFromOldAttmptForLeads(Long id, String sapid) {
			List<StudentsTestDetailsExamBean> attemptsList= getAllAttemptsListByTestIdAndSapid(id,sapid);
			
			String attemptedQuestionIds="";
			int count = 0;
			for(StudentsTestDetailsExamBean attempt : attemptsList) {
				if(count == 0) {
					attemptedQuestionIds = attempt.getTestQuestions();
				}else {
					attemptedQuestionIds = attemptedQuestionIds +","+ attempt.getTestQuestions();
				}
			}
			
			List<TestQuestionExamBean> testQuestions = getTestQuestionsByTestIdAndNotInQuestionIdsForLeads(id, attemptedQuestionIds);
			
			TestExamBean test = getTestByIdForLeads(id);
			
			int noOfQuestions = (testQuestions != null) ? (testQuestions.size()) : 0;
			
			if(test.getMaxQuestnToShow() <= noOfQuestions ) {
				return testQuestions;
			}else {

				return getTestQuestionsForLeads(id);
			}
			
			
		}
		

		public List<StudentsTestDetailsExamBean> getAllAttemptsListByTestIdAndSapid(Long testId, String sapid) {
			List<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
		
			String sql = "select  *  "
					+ " from lead.leads_test_student_testdetails ts "
					+ " where  ts.testId = "+testId+" and ts.sapid = '"+sapid+"' "; 
			 try {
				 testDetailsList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			 }
			 catch(Exception e){
				 
			 }
			 return testDetailsList;
			
			 
			}
		

		public List<TestQuestionExamBean> getTestQuestionsByTestIdAndNotInQuestionIdsForLeads(Long testId, String attemptedQuestionIds){
			
			//return null on error.
			List<TestQuestionExamBean> testQuestionsList=new ArrayList<>();
			
			
			//1.get data from redis 
			//TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
			
			//testQuestionsList = daoForRedis.findAllTestQuestionsByTestId(testId);
			
			
			//2. check if questions are present 
			/*if(testQuestionsList != null && testQuestionsList.size() > 0) {
				return testQuestionsList;
			}*/
			
			//3. if not present get question from mysql db
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select q.*,c.url as url "
					+ " from lead.leads_test_questions q " + 
					"   LEFT JOIN lead.leads_test_question_additionalcontent c " + 
					"	on q.id=c.questionId " + 
					"	where q.testId=? and q.isSubQuestion = 0 "
					+ " and q.id not in ("+attemptedQuestionIds+") "
					+ " Order By q.id ";
			
			try {



				testQuestionsList = (List<TestQuestionExamBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionExamBean.class));
				
				if(testQuestionsList !=null) {



				}else {



				}
				for(TestQuestionExamBean question : testQuestionsList) {



					if(question.getType() == 6 || question.getType() == 7) {



					}
					List<TestQuestionOptionExamBean> options = getTestQuestionOptionByQuestionIdForLeads(question.getId());
					question.setOptionsList(options);
					/*if(question.getType()==3) {
						question.setSubQuestionsList(getTestSubQuestions(question.getId()));
					}*/
				}
			} catch (Exception e) {
				//
				logger.info("\n"+SERVER+": "+" IN getTestQuestions   got testId : "+testId+" , Error :  "+e.getMessage());
				
			}
			return testQuestionsList;
		}

		public List<StudentsTestDetailsExamBean> getAnswerInCacheNDBMismatch() {
			List<StudentsTestDetailsExamBean> testDetailsList = new ArrayList<StudentsTestDetailsExamBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
		
			String sql = "SELECT  " + 
					"    tst.*, tsa.noOfAnswersInDB " + 
					"FROM " + 
					"    exam.test_student_testdetails tst " + 
					"        LEFT JOIN " + 
					"    (SELECT  " + 
					"        COUNT(a.id) AS noOfAnswersInDB, a.sapid, a.testId, a.attempt " + 
					"    FROM " + 
					"        exam.test_students_answers a " + 
					"    INNER JOIN exam.test_questions q ON q.id = a.questionId " + 
					"    WHERE " + 
					"        q.type = 4 " + 
					"    GROUP BY sapid , testId , attempt) tsa ON tst.sapid = tsa.sapid " + 
					"        AND tst.testId = tsa.testId " + 
					"        AND tst.attempt = tsa.attempt " + 
					"WHERE " + 
					"    tst.noOfAnswersInCache > 0 " + 
					"		 AND sysdate() >= DATE_ADD(tst.testStartedOn, INTERVAL 210 MINUTE)  " +
					"        AND ((tst.noOfAnswersInCache <> tsa.noOfAnswersInDB) " + 
					"        OR (tsa.noOfAnswersInDB IS NULL)) " + 
					"    "; 
			 try {
				 testDetailsList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
			 }
			 catch(Exception e){
				 
			 }
			 return testDetailsList;
			
			 
			}

		public String deleteTestAttemptDataBySapidAndTestIdAndAttempt(String sapid, Long testId, Integer attempt) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sqlForTestDetails="delete from exam.test_student_testdetails "
					+ " where sapid=? and testId=? and attempt=?";
			try {
				 row = jdbcTemplate.update(sqlForTestDetails, new Object[] {sapid,testId,attempt});
			} catch (Exception e) {
				
				return "Error in deleting testdetails, Error : "+e.toString();
			}
			

			String sqlForAnswers="delete from exam.test_students_answers where sapid=? and testId=? and attempt=?";
			try {
				 row = jdbcTemplate.update(sqlForAnswers, new Object[] {sapid,testId,attempt});
			} catch (Exception e) {
				
				return "Error in deleting answrs, Error : "+e.toString();
			}
			
			
			
			return "";
		}

	public ArrayList<LogFileAnalysisBean> getQuestionId(String testId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    tq.id AS questionId " + 
				"FROM " + 
				"    exam.test_questions tq " + 
				"        INNER JOIN " + 
				"    exam.test t ON tq.testId = t.id " + 
				"WHERE " + 
				"    t.id = ?";
		ArrayList<LogFileAnalysisBean> questionIdList = (ArrayList<LogFileAnalysisBean>) jdbcTemplate.query(sql, new Object[] {testId}, new BeanPropertyRowMapper(LogFileAnalysisBean.class));
		
		return questionIdList;
	}
	
	public List<StudentQuestionResponseExamBean> getAccumulatedAnswerFromDbAndRedis( StudentsTestDetailsExamBean studentsTestDetails, 
			List<StudentQuestionResponseExamBean> answersFromDB ) {
		
		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		String questionIdList = "";
		List<StudentQuestionResponseExamBean> answersFromRedsWithoutQuestionAndtype = new ArrayList<>();
		List<StudentQuestionResponseExamBean> answersFromReds = new ArrayList<>();
		List<StudentQuestionResponseExamBean> questionList = new ArrayList<>();
		
		if("N".equalsIgnoreCase(studentsTestDetails.getAnswersMovedFromCacheToDB())) {
			
			answersFromRedsWithoutQuestionAndtype = daoForRedis.getAnswersFromRedisByStudentsTestDetails(studentsTestDetails);

			for( StudentQuestionResponseExamBean bean : answersFromRedsWithoutQuestionAndtype) {

				questionIdList = questionIdList + bean.getQuestionId() + ", ";
				
			}

			questionIdList = questionIdList.substring(0 , (questionIdList.length()-2));
			questionList = getQuetionAndTypeForQuestionId( questionIdList );
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for( StudentQuestionResponseExamBean bean : answersFromRedsWithoutQuestionAndtype) {

				StudentQuestionResponseExamBean updatedBean = new StudentQuestionResponseExamBean();
				for( StudentQuestionResponseExamBean question : questionList) {
					
					if( bean.getQuestionId().equals(question.getQuestionId()) ) {

						updatedBean.setQuestionId( bean.getQuestionId() );
						updatedBean.setQuestion( question.getQuestion() );
						updatedBean.setType(4);
						updatedBean.setTypeString("DESCRIPTIVE");
						updatedBean.setAnswer( bean.getAnswer() );
						updatedBean.setCreatedDate( bean.getCreatedDate() );
						if( StringUtils.isBlank(bean.getLastModifiedDate()))
							updatedBean.setLastModifiedDate( formater.format(Calendar.getInstance().getTime()) );
						else {
							try {
								updatedBean.setLastModifiedDate( formater.format(parser.parse( bean.getLastModifiedDate() )) );
							}catch (Exception e) {
								
							}
						}
						updatedBean.setIsChecked( bean.getIsChecked() );
						updatedBean.setMarks( 0.0 ); 
						updatedBean.setRemark("NA");

					}
						
				}
				answersFromReds.add(updatedBean);
				
			}
			
		}
		
		List<StudentQuestionResponseExamBean> answersAfterSettingDBNRedis = createAnswersListFromDBNRedis( answersFromDB, answersFromReds );

		return answersAfterSettingDBNRedis;
	}
	
	private List<StudentQuestionResponseExamBean> getQuetionAndTypeForQuestionId( String questionIdList ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentQuestionResponseExamBean> details = new ArrayList<StudentQuestionResponseExamBean>();
		
		String sql = "SELECT "
				+ "    id as questionId, question "
				+ "FROM "
				+ "    exam.test_questions tq "
				+ "WHERE "
				+ "    id IN ( "+questionIdList+" ) ";
		

		details = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));

		return details;
		
	}

	public List<TestQuestionConfigBean> getSectionQuestionTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestQuestionConfigBean> typeList=new ArrayList<TestQuestionConfigBean>();
		String sql = "SELECT id,type as typeName from exam.test_question_type"; 
		try {   
			typeList = (List<TestQuestionConfigBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (Exception e) {
			 
		}
		return typeList;
	}
	public void saveSectionsForTemplate(final ArrayList<SectionBean> bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = " INSERT INTO exam.test_sections( sectionName,sectionDur,instructions, allQnsMandatory, "
				+ "sectionRandQns) " 
				+ " VALUES (?,?,?,?,?)"; 
		
		for(final SectionBean section : bean) { 
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							statement.setString(1,section.getSectionName());
							statement.setString(2,section.getSectionDur()); 
							statement.setString(3,section.getInstructions());  
							statement.setString(4,section.getAllQnsMandatory());
							statement.setString(5,section.getSectionRandQns());		 
				            return statement;
				    }
				}, holder);
			} catch (Exception e) {
				 
			}
			final long primaryKey = holder.getKey().longValue();
			section.setId(primaryKey+"");
		}
	}

	public void saveToTestQnConfigTemplate(final TestQuestionConfigBean templateBean) {
		for(final SectionBean section : templateBean.getSectionBean()) { 
			final ArrayList<SectionBean> bean = section.getSectionQnTypeConfigbean();
			
			
			String sql = " INSERT INTO exam.test_question_config_template(name,templateId,type,minNoOfQuestions,maxNoOfQuestions,testType,duration,questionMarks,sectionId) "
					+ " VALUES (?,?,?,?,?,?,?,?,?)";  
			 
			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
				
					ps.setString(1,templateBean.getName());
					ps.setString(2,templateBean.getTemplateId());
					ps.setString(3,bean.get(i).getSectionQnType());
					ps.setString(4,bean.get(i).getSectionQnCount());
					ps.setString(5,bean.get(i).getSectionQnCount()); 
					ps.setString(6,templateBean.getTestType()); 
					ps.setString(7,templateBean.getDuration()); 	
					ps.setString(8,bean.get(i).getQuestionMarks());  
					ps.setString(9,section.getId());
				} 

				@Override
				public int getBatchSize() {
					return bean.size();
				}
				
			}); 
		}
		
	}
 
	public int deleteSectionbyId(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from exam.test_sections where id=?"; 
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id}); 
		}catch(Exception e) {}
		
		String sql1="delete from exam.test_question_config_template where sectionId=? "; 
		try {
			 row = jdbcTemplate.update(sql1, new Object[] {id});  
		}catch(Exception e) {}
		return row;
	}

	public ArrayList<SectionBean> getSectionQuestionConfig(String id) {
		ArrayList<SectionBean> sections=new ArrayList<SectionBean>();
		String sql = "SELECT c.id,t.type as sectionQnType,c.minNoOfQuestions as sectionQnCount,c.questionMarks   "
				+ "from exam.test_question_config_template c "
				+ "left join exam.test_question_type t on t.id=c.type" 
				+ " where c.sectionId=?";   
		try {   
			sections = (ArrayList<SectionBean>) jdbcTemplate.query(sql,new Object[] {id},new BeanPropertyRowMapper(SectionBean.class));
		} catch (Exception e) {
			 
		}
		
		return sections;
	}
	
	public List<SectionBean> getApplicableSectionList(Long testId){
		String sql = "SELECT " + 
				"    ts.id, ts.sectionName " + 
				"FROM " + 
				"    exam.test_questions_configuration qconfig " + 
				"        INNER JOIN " + 
				"    exam.test_sections ts ON qconfig.sectionId = ts.id " + 
				"WHERE " + 
				"    qconfig.testId = ? group by ts.id";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SectionBean> list = new ArrayList<SectionBean>();
		list = jdbcTemplate.query(sql,new Object[] {testId}, new BeanPropertyRowMapper<SectionBean>(SectionBean.class));
		return list;
	}
	public ArrayList<SectionBean> getSectionsFromTemplate(String id) {
		ArrayList<SectionBean> sections=new ArrayList<SectionBean>(); 
		if(id!=null ) {
		
		String sql = "select * from exam.test_sections where id in" 
				+ "(SELECT sectionId from exam.test_question_config_template where templateId=? group by sectionId)"; 
		try {   
			sections = (ArrayList<SectionBean>) jdbcTemplate.query(sql,new Object[] {id},new BeanPropertyRowMapper(SectionBean.class));
		} catch (Exception e) {
			 
		}  
		}
		return sections;
	}

	public ArrayList<SectionBean>  getQuestionsConfiguredBasedOnSection(String id,Long testId) { 
		jdbcTemplate = new JdbcTemplate(dataSource); 
		
			ArrayList<SectionBean> qnConfig = new ArrayList<SectionBean> (); 
			String sql ="SELECT c.minNoOfQuestions as sectionQnCount,c.questionMarks,t.type as sectionQnType,c.type "
					+ "FROM exam.test_questions_configuration c "
					+ " LEFT JOIN exam.test_question_type t ON t.id = c.type  "
					+ " where c.sectionId=? and c.testId=?"; 
			try { 
				qnConfig = (ArrayList<SectionBean>) jdbcTemplate.query(sql,new Object[] {id,testId}, new BeanPropertyRowMapper(SectionBean.class));
			} catch (DataAccessException e) { 
				  
			}  
		 
		return qnConfig;
		
	}
	public ArrayList<SectionBean>  getSectionsByTestId(Long id) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SectionBean> sectionList = new ArrayList<SectionBean> ();
		String sql ="SELECT  s.id ,s.sectionName " + 
				"FROM " + 
				"    exam.test_questions_configuration c " +  
				"        LEFT JOIN "
				+ " exam.test_sections s on s.id=c.sectionId " + 
				"WHERE " + 
				"    c.testId = ? group by sectionId"; 
		try {
			sectionList = (ArrayList<SectionBean>) jdbcTemplate.query(sql,new Object[] {id}, new BeanPropertyRowMapper(SectionBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		return sectionList;
	}
	

	private ArrayList<TestQuestionConfigBean> getAllSectionsForATest(Long testId) {
		String sql = "select * from exam.test_questions_configuration "
				+ " where testId=? group by sectionId ";
		ArrayList<TestQuestionConfigBean> sections = new ArrayList<TestQuestionConfigBean>();
		
		try {
			sections = (ArrayList<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] {testId}, new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) {
			
		}
		return sections;
	}

	private void validateQnsUploadedAndConfiguredMarksPerSection(TestExamBean testbean, ResponseBean response) {

		ArrayList<TestQuestionConfigBean> qnList=new ArrayList<TestQuestionConfigBean>();
		ArrayList<TestQuestionConfigBean> configList=new ArrayList<TestQuestionConfigBean>();
		boolean error =false;
		
		String sql ="select sum(minNoOfQuestions*questionMarks) as questionMarks ,sectionId from exam.test_questions_configuration "+ 
				 "where testId=? group by sectionId";

		try {
			configList = (ArrayList<TestQuestionConfigBean>) jdbcTemplate.query(sql,new Object[] { testbean.getTestId() }, 
					new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) {}	
		
		String sql1 ="select sectionId, sum(questionMarks) as questionMarks from "
				+ "(select count(*)*marks as questionMarks, id, marks, type, sectionId from exam.test_questions where "
				+ "testId=? group by sectionid, type, marks) as questions";
		
		try {
			qnList = (ArrayList<TestQuestionConfigBean>) jdbcTemplate.query(sql1,new Object[] { testbean.getTestId() }, 
					new BeanPropertyRowMapper(TestQuestionConfigBean.class));
		} catch (DataAccessException e) {}
		
		if( (configList!=null)&& ( qnList!=null)) {
			search: {
				for(TestQuestionConfigBean config : configList) {   
					for(TestQuestionConfigBean qn : qnList) {
						if(config.getSectionId().equalsIgnoreCase(qn.getSectionId()) && config.getQuestionMarks()> qn.getQuestionMarks() ) {
							error= true;
							break search;
						}	
					} 
				}
			}
		}
		
		if(error==true) {
			response.setStatus("error");
			response.setMessage("Error! Mismatch in Total Marks per Sections and Total marks for Questions Uploaded ");
		}else {
			response.setStatus("success");
		}
	}//test
		
	public List<SectionBean>  getSectionsInQuestionsUploaded(Long id) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SectionBean> sections = new ArrayList<SectionBean> ();
		String sql = "select s.id,s.sectionName from exam.test_questions q  " + 
				"left join exam.test_sections s on s.id=q.sectionId " + 
				"where q.testId = ? group by sectionId";  
		try { 
			sections = (List<SectionBean>) jdbcTemplate.query(sql,new Object[] {id}, new BeanPropertyRowMapper(SectionBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return sections;
		
	}
	public ArrayList<SectionBean>  getQuestionsUploadedBySection(Long testId,String sectionId) { 
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SectionBean> qns = new ArrayList<SectionBean> ();
		String sql = "select t.type as sectionQnType,count(*) as sectionQnCount from exam.test_questions q   " + 
				"left join exam.test_question_type t on t.id=q.type " + 
				"where q.testId = ? and sectionId=? group by q.type ";  
		try { 
			qns = (ArrayList<SectionBean>) jdbcTemplate.query(sql,new Object[] {testId,sectionId}, new BeanPropertyRowMapper(SectionBean.class));
		} catch (DataAccessException e) { 
			  
		} 
		
		return qns;
		
	}
	
	public List<TestExamBean> getTestsAssignedForEvaluationToFaculty(String facultyId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestExamBean> testsList = new ArrayList<TestExamBean>();
		
		String sql=" SELECT " + 
				"    t.* " + 
				"FROM " + 
				"    exam.test t " + 
				"        INNER JOIN " + 
				"    exam.test_students_answers tsa ON t.id = tsa.testId " + 
				"WHERE " + 
				"    tsa.facultyId = ? " + 
				"GROUP BY tsa.testId " + 
				"ORDER BY t.id DESC ";
		
		try {
			 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {facultyId}, new BeanPropertyRowMapper(TestExamBean.class));
		} catch (Exception e) {
			
		}
		
		try {
			for(TestExamBean test : testsList) {
				test.setNoOfAnswersToEvaluate(getNoOfAnswersToEvaluateByFacultyId(facultyId,test.getId()));

			} 
		} catch (Exception e) {
			
		}
		
		return testsList;
		
	}
	
	public List<TestExamBean> getTestsAssignedForFaculty(String facultyId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestExamBean> testsList = new ArrayList<TestExamBean>();
		
		String sql=" SELECT " + 
				"    t.* " + 
				"FROM " + 
				"    exam.test t " + 
				"WHERE " + 
				"    t.facultyId = ? " + 
				"ORDER BY t.id DESC ";
		
		try {
			 testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {facultyId}, new BeanPropertyRowMapper(TestExamBean.class));
		} catch (Exception e) {
			
		}
		
		try {
			for(TestExamBean test : testsList) {
				test.setNoOfAnswersToEvaluate( getNoOfAnswersToEvaluateByFacultyId(facultyId,test.getId()) );

			} 
		} catch (Exception e) {
			
		}

		return testsList;
		
	}

	public List<StudentsTestDetailsExamBean> getIAANSAttemptsByTestId(Long testId) {
		List<StudentsTestDetailsExamBean> iaANSList = new ArrayList<StudentsTestDetailsExamBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//id, sapid, testId, attempt, attemptStatus, active, 
		//testStartedOn, remainingTime, testEndedOn, testCompleted, 
		//score, testQuestions, noOfQuestionsAttempted, currentQuestion, 
		//showResult, createdBy, createdDate, lastModifiedBy, lastModifiedDate, 
		//resultDeclaredOn, attemptStatusModifiedDate, copyCaseMatchedPercentage, countOfRefreshPage, answersMovedFromCacheToDB, noOfAnswersInCache, noOfRefreshAuditTrails, testEndedStatus
		
		String sql="SELECT   " + 
				" tum.userId as sapid, tcm.testId, '1' as attempt , 'ANS' as attemptStatus, 'Y' as active, "
				+ " t.startDate as testStartedOn, t.duration as remainingTime, 'Y' as testCompleted, "
				+ " '0' as score, 'NA' as testQuestions, '0' as noOfQuestionsAttempted, '0' as currentQuestion, "
				+ " 'Y' as answersMovedFromCacheToDB, '0' as noOfAnswersInCache  " + 
				"FROM  " + 
				"    exam.test t  " + 
				"        INNER JOIN  " + 
				"    exam.test_testid_configuration_mapping tcm ON t.id = tcm.testId  " + 
				"        INNER JOIN  " + 
				"    exam.test_live_settings tls ON tls.referenceId = tcm.referenceId  " + 
				"        INNER JOIN  " + 
				"    acads.sessionplan_module m ON m.id = tcm.referenceId  " + 
				"        INNER JOIN  " + 
				"    acads.sessionplan s ON s.id = m.sessionPlanId  " + 
				"        INNER JOIN  " + 
				"    acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = s.id  " + 
				"        INNER JOIN  " + 
				"    lti.student_subject_config ssc ON ssc.id = stm.timeboundId  " + 
				"        INNER JOIN  " + 
				"    lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ssc.id  " +  
				"WHERE  " + 
				"    tls.liveType = 'Regular'  " + 
				"        AND tls.applicableType = 'module'  " + 
				"        AND tum.role = 'Student' " + 
				"		 AND sysdate() >= t.endDate  " +
				"        AND t.id = ? "
				+ "		 AND tum.userId not in (select sapid from exam.test_student_testdetails where testId = ? and attempt = 1 )	" +
				"					; ";
		try {
			 iaANSList = (List<StudentsTestDetailsExamBean>) jdbcTemplate.query(sql, new Object[] {testId,testId} ,new BeanPropertyRowMapper(StudentsTestDetailsExamBean.class));
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 return iaANSList;
		
		 
		}
	
	public ArrayList<IAReportsBean> getFilteredTestsData(IAReportsBean iaReportsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuilder sbSql= new StringBuilder("SELECT * FROM exam.test " + 
												"WHERE 1 = 1 ");
		
		ArrayList<Object> paramArray = new ArrayList<>();		//ArrayList of Object created to add parameters according to the fields present
		
		if(iaReportsBean.getYear() != 0) {
			sbSql.append("AND year = ? ");
			paramArray.add(iaReportsBean.getYear());
		}
		
		if(iaReportsBean.getMonth() != null && !iaReportsBean.getMonth().isEmpty()) {
			sbSql.append("AND month = ? ");
			paramArray.add(iaReportsBean.getMonth());
		}
		
		if(iaReportsBean.getAcadYear() != 0) {
			sbSql.append("AND acadYear = ? ");
			paramArray.add(iaReportsBean.getAcadYear());
		}
		
		if(iaReportsBean.getAcadMonth() != null && !iaReportsBean.getAcadMonth().isEmpty()) {
			sbSql.append("AND acadMonth = ? ");
			paramArray.add(iaReportsBean.getAcadMonth());
		}
		
		if(iaReportsBean.getTestName() != null && !iaReportsBean.getTestName().isEmpty()) {
			sbSql.append("AND testName = ? ");
			paramArray.add(iaReportsBean.getTestName());
		}
		
		if(iaReportsBean.getSubject() != null && !iaReportsBean.getSubject().isEmpty()) {
			sbSql.append("AND subject = ? ");
			paramArray.add(iaReportsBean.getSubject());
		}
		
		if(iaReportsBean.getStartDate() != null && !iaReportsBean.getStartDate().isEmpty()) {
			sbSql.append("AND startDate BETWEEN ? AND ? ");
			
			//Parsing String of startDate as LocalDate
			LocalDate startDate = LocalDate.parse(iaReportsBean.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");	//Format of expected DateTime
			String minTimeOfStartDate = startDate.atTime(LocalTime.MIN).format(formatter);		//Creating String of DateTime with 00:00:00 as Time and date as passed by LocalDate
			String maxTimeOfStartDate = startDate.atTime(LocalTime.MAX).format(formatter);		//Creating String of DateTime with 23:59:59 as Time and date as passed by LocalDate
			
			paramArray.add(minTimeOfStartDate);
			paramArray.add(maxTimeOfStartDate);
		}
		
		if(iaReportsBean.getTestType() != null && !iaReportsBean.getTestType().isEmpty()) {
			sbSql.append("AND testType = ? ");
			paramArray.add(iaReportsBean.getTestType());
		}
		
		if(iaReportsBean.getFacultyId() != null && !iaReportsBean.getFacultyId().isEmpty()) {
			String[] facultyNameIdSeparatedArray = iaReportsBean.getFacultyId().split("-");		//Separating Faculty Name & Id
			String facultyId = facultyNameIdSeparatedArray[facultyNameIdSeparatedArray.length - 1].trim();		//Storing the faculty Name from the Array
			
			sbSql.append("AND facultyId = ? ");
			paramArray.add(facultyId);
		}
		
		return (ArrayList<IAReportsBean>) jdbcTemplate.query(sbSql.toString(), new BeanPropertyRowMapper(IAReportsBean.class), paramArray.toArray());
	}
	
	@Transactional(readOnly = true)
	public Integer getPssIdFromTimeboundId(Long timeboundId, String sapid) {
		String sql = ""
				+ " SELECT `pss2`.`id`  "
				
				+ " FROM `lti`.`timebound_user_mapping` `tum` "
				
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
				+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				
				+ " INNER JOIN `exam`.`registration` `r` "
				+ " ON `r`.`sapid` = `tum`.`userId` "
				+ " AND `r`.`year` = `ssc`.`acadYear` "
				+ " AND `r`.`month` = `ssc`.`acadMonth` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss1` "
				+ " ON `pss1`.`id` = `ssc`.`prgm_sem_subj_id` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss2` "
				+ " ON `pss2`.`subject` = `pss1`.`subject` "
				+ " AND `r`.`sem` = `pss2`.`sem` "
				+ " AND `pss2`.`consumerProgramStructureId` = `r`.`consumerProgramStructureId` "
				
				+ " WHERE `tum`.`timebound_subject_config_id` = ? AND `tum`.`userId` = ? ";
		try {
			return  jdbcTemplate.queryForObject(
				sql, 
				new Object[] {  timeboundId, sapid },
				Integer.class
			);
		}catch(Exception e) {
			return 0;
		}
			
	}
public List<StudentQuestionResponseExamBean> getFacultyAndAnswerDetailsForTestId(Long testId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql ="SELECT  "
				+ "    a.facultyId, "
				+ "    COUNT(a.facultyId) AS allocatedAnswers, "
				+ "    CONCAT(f.firstName, ' ', f.lastName) AS facutlyName "
				+ "FROM "
				+ "    exam.test_students_answers a "
				+ "        INNER JOIN "
				+ "    acads.faculty f ON a.facultyId = f.facultyId "
				+ "WHERE "
				+ "    a.sapid NOT LIKE '777777%' "
				+ "        AND (a.facultyId IS NOT NULL "
				+ "        OR TRIM(a.facultyId) <> '') "
				+ "        AND a.questionId IN (SELECT  "
				+ "            q.id "
				+ "        FROM "
				+ "            exam.test_questions q "
				+ "        WHERE "
				+ "            q.type IN (4 , 8) "
				+ "                AND q.testId = ?) "
				+ "GROUP BY a.facultyId";
		

		List<StudentQuestionResponseExamBean> facultyNAllocatedAnswers = (List<StudentQuestionResponseExamBean>) jdbcTemplate.query(sql, 
				new Object[] {testId}, new BeanPropertyRowMapper(StudentQuestionResponseExamBean.class));

		return facultyNAllocatedAnswers;
	}

	public void upsertFacultyRightsForTest(final Long testId, String userid) throws Exception{
		
		List<TestExamBean> facultylist = getFacultyForTest(testId, userid);

		facultylist.stream().forEach(faculty -> {
			try {
				upsertFacultyEditRights(faculty);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		});
		
		
	}
	

	public void saveFacultyEditRights(TestExamBean bean) throws Exception {
		
		bean.setLastModifiedBy(bean.getUserId());
		upsertFacultyEditRights(bean);
		
	}
	
	private List<TestExamBean> getFacultyForTest(Long testId, String userid) throws Exception{

		TestExamBean faculty = getFacultyMappingForTest(testId, userid);
		List<TestExamBean> facultyList = getFacultyMappingForTestAnswers(testId, userid);
		facultyList.add(faculty);
		
		return facultyList;
		
	}

	private TestExamBean getFacultyMappingForTest(Long testid, String userid) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql ="SELECT  "
				+ "    id, facultyId, "
				+ "    CASE "
				+ "        WHEN "
				+ "            showResultsToStudents = 'N' "
				+ "                OR showResultsToStudents IS NULL "
				+ "        THEN "
				+ "            'Y' "
				+ "        ELSE 'N' "
				+ "    END AS canFacultyEditIA "
				+ "FROM exam.test WHERE id = ?";
		
		TestExamBean faculty = jdbcTemplate.queryForObject(sql, new Object[] {testid}, 
				new BeanPropertyRowMapper<>(TestExamBean.class));
		faculty.setLastModifiedBy(userid);
		
		return faculty;
		
	}
	
	private List<TestExamBean> getFacultyMappingForTestAnswers(Long testid, String userid) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql ="SELECT  "
				+ "    t.id, tsa.facultyId, "
				+ "    CASE "
				+ "        WHEN "
				+ "            showResultsToStudents = 'N' "
				+ "                OR showResultsToStudents IS NULL "
				+ "        THEN "
				+ "            'Y' "
				+ "        ELSE 'N' "
				+ "    END AS canFacultyEditIA "
				+ "FROM exam.test_students_answers tsa "
				+ "        INNER JOIN "
				+ "    exam.test t ON tsa.testid = t.id "
				+ "WHERE testid = ? AND TRIM(tsa.facultyId) <> '' "
				+ "        AND tsa.facultyId IS NOT NULL "
				+ "GROUP BY tsa.facultyId";
		
		List<TestExamBean> facultyList = jdbcTemplate.query(sql, new Object[] {testid}, 
				new BeanPropertyRowMapper<>(TestExamBean.class));
		
		facultyList.forEach(faculty -> faculty.setLastModifiedBy(userid));
		
		return facultyList;
		
	}

	private void upsertFacultyEditRights(final TestExamBean test) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();

		final String sql = "insert into `exam`.`test_faculty_edit_rights` "
				+ "(`testId`, `facultyId`, `canFacultyEditIA`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) "
				+ "values "
				+ "(?,?,?,?,sysdate(),?,sysdate()) "
				+ "on duplicate key update "
				+ "`canFacultyEditIA` = ?, lastModifiedBy = ?, lastModifiedDate = sysdate()";

		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				statement.setLong( 1, test.getId());
				statement.setString( 2, test.getFacultyId() );
				statement.setString( 3, test.getCanFacultyEditIA() );
				statement.setString( 4, test.getUserId() );
				statement.setString( 5, test.getLastModifiedBy() );
				statement.setString( 6, test.getCanFacultyEditIA() );
				statement.setString( 7, test.getLastModifiedBy() );

				return statement;
			}
		}, holder);

	}

	public void updateTestFacultyEditRights(String userId, Long testId, String facultyId ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="update exam.test_faculty_edit_rights set canFacultyEditIA = 'N', lastModifiedBy = ?, lastModifiedDate = sysdate() "
				+ "where testid = ? and facultyid = ?";
		
		jdbcTemplate.update(sql,new Object[] {userId, testId, facultyId});

	}
	
	public String canFacultyEditIA( Long testId, String facultyId ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql ="select ifnull((select canFacultyEditIA from exam.test_faculty_edit_rights where testid = ? and facultyid = ?), 'Y')";
		
		String canFacultyEditIA = jdbcTemplate.queryForObject(sql, new Object[] {testId, facultyId}, 
				new SingleColumnRowMapper<>(String.class));

		return canFacultyEditIA;
	}

	public List<TestExamBean> getCurrentLiveTests() throws Exception{//get all current cycle live tests
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select t.* from exam.test t "
				+ "inner join exam.test_live_settings l on t.referenceId =l.referenceId and t.year=l.examYear and t.month=l.examMonth and    "
				+ "	t.acadYear=l.acadYear  and t.acadMonth=l.acadMonth "
				+ "where t.acadMonth = ? and t.acadYear = ? "
				+ "order by t.id desc";
		
		List<TestExamBean> testsList = (List<TestExamBean>) jdbcTemplate.query(sql, new Object[] {current_mbawx_acad_month, current_mbawx_acad_year}, 
				new BeanPropertyRowMapper(TestExamBean.class));

		return testsList;
	}



	public boolean updateInitialResultLiveTest(TestExamBean testBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.test " + 
						"SET year = ?," + 
						"	month = ?," + 
						"	acadYear = ?," + 
						"	acadMonth = ?," + 
						"	testName = ?," + 
						"	testDescription = ?," + 
						"	startDate = ?," + 
						"	endDate = ?," + 
						"	maxQuestnToShow = ?," + 
						"	showResultsToStudents = ?," + 
						"	active = ?," + 
						"	facultyId = ?," + 
						"	maxAttempt = ?," + 
						"	randomQuestion = ?," + 
						"	testQuestionWeightageReq = ?," + 
						"	allowAfterEndDate = ?," + 
						"	sendEmailAlert = ?," + 
						"	sendSmsAlert = ?," + 
						"	maxScore = ?," + 
						"	duration = ?," + 
						"	passScore = ?," + 
						"	testType = ?," + 
						"	lastModifiedBy = ?," + 
						"	lastModifiedDate = sysdate()," + 
						"	consumerTypeIdFormValue = ?," + 
						"   programStructureIdFormValue = ?," + 
						"   programIdFormValue = ?," + 
						"	applicableType = ?," + 
						"   referenceId = ?," + 
						"	proctoringEnabled = ?," + 
						"   showCalculator = ?," + 
						"   initialResultLiveDateTime = sysdate()," + 
						"	lastModifiedResultLiveDate = sysdate() " +
						"WHERE id = ?";
		
		try {
			jdbcTemplate.update(query, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, testBean.getYear());
					ps.setString(2, testBean.getMonth());
					ps.setInt(3, testBean.getAcadYear());
					ps.setString(4, testBean.getAcadMonth());
					ps.setString(5, testBean.getTestName());
					ps.setString(6, testBean.getTestDescription());
					ps.setString(7, testBean.getStartDate());
					ps.setString(8, testBean.getEndDate());
					ps.setInt(9, testBean.getMaxQuestnToShow());
					ps.setString(10, testBean.getShowResultsToStudents());
					ps.setString(11, testBean.getActive());
					ps.setString(12, testBean.getFacultyId());
					ps.setInt(13, testBean.getMaxAttempt());
					ps.setString(14, testBean.getRandomQuestion());
					ps.setString(15, testBean.getTestQuestionWeightageReq());
					ps.setString(16, testBean.getAllowAfterEndDate());
					ps.setString(17, testBean.getSendEmailAlert());
					ps.setString(18, testBean.getSendSmsAlert());
					ps.setInt(19, testBean.getMaxScore());
					ps.setInt(20, testBean.getDuration());
					ps.setInt(21, testBean.getPassScore());
					ps.setString(22, testBean.getTestType());
					ps.setString(23, testBean.getLastModifiedBy());
					ps.setString(24, testBean.getConsumerTypeIdFormValue());
					ps.setString(25, testBean.getProgramStructureIdFormValue());
					ps.setString(26, testBean.getProgramIdFormValue());
					ps.setString(27, testBean.getApplicableType());
					ps.setInt(28, testBean.getReferenceId());
					ps.setString(29, testBean.getProctoringEnabled());
					ps.setString(30, testBean.getShowCalculator());
					ps.setLong(31, testBean.getId());
				}
			});
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.error("Error while updating test with initial Result Live DateTime for testId: {}, Exception thrown: {}", testBean.getId(), ex.toString());
			return false;
		}
		
		return true;
	}
	
	public boolean updateLastModifiedResultLiveTest(TestExamBean testBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.test " + 
						"SET year = ?," + 
						"	month = ?," + 
						"	acadYear = ?," + 
						"	acadMonth = ?," + 
						"	testName = ?," + 
						"	testDescription = ?," + 
						"	startDate = ?," + 
						"	endDate = ?," + 
						"	maxQuestnToShow = ?," + 
						"	showResultsToStudents = ?," + 
						"	active = ?," + 
						"	facultyId = ?," + 
						"	maxAttempt = ?," + 
						"	randomQuestion = ?," + 
						"	testQuestionWeightageReq = ?," + 
						"	allowAfterEndDate = ?," + 
						"	sendEmailAlert = ?," + 
						"	sendSmsAlert = ?," + 
						"	maxScore = ?," + 
						"	duration = ?," + 
						"	passScore = ?," + 
						"	testType = ?," + 
						"	lastModifiedBy = ?," + 
						"	lastModifiedDate = sysdate()," + 
						"	consumerTypeIdFormValue = ?," + 
						"   programStructureIdFormValue = ?," + 
						"   programIdFormValue = ?," + 
						"	applicableType = ?," + 
						"   referenceId = ?," + 
						"	proctoringEnabled = ?," + 
						"   showCalculator = ?," + 
						"	lastModifiedResultLiveDate = sysdate() " +
						"WHERE id = ?";
		
		try {
			jdbcTemplate.update(query, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, testBean.getYear());
					ps.setString(2, testBean.getMonth());
					ps.setInt(3, testBean.getAcadYear());
					ps.setString(4, testBean.getAcadMonth());
					ps.setString(5, testBean.getTestName());
					ps.setString(6, testBean.getTestDescription());
					ps.setString(7, testBean.getStartDate());
					ps.setString(8, testBean.getEndDate());
					ps.setInt(9, testBean.getMaxQuestnToShow());
					ps.setString(10, testBean.getShowResultsToStudents());
					ps.setString(11, testBean.getActive());
					ps.setString(12, testBean.getFacultyId());
					ps.setInt(13, testBean.getMaxAttempt());
					ps.setString(14, testBean.getRandomQuestion());
					ps.setString(15, testBean.getTestQuestionWeightageReq());
					ps.setString(16, testBean.getAllowAfterEndDate());
					ps.setString(17, testBean.getSendEmailAlert());
					ps.setString(18, testBean.getSendSmsAlert());
					ps.setInt(19, testBean.getMaxScore());
					ps.setInt(20, testBean.getDuration());
					ps.setInt(21, testBean.getPassScore());
					ps.setString(22, testBean.getTestType());
					ps.setString(23, testBean.getLastModifiedBy());
					ps.setString(24, testBean.getConsumerTypeIdFormValue());
					ps.setString(25, testBean.getProgramStructureIdFormValue());
					ps.setString(26, testBean.getProgramIdFormValue());
					ps.setString(27, testBean.getApplicableType());
					ps.setInt(28, testBean.getReferenceId());
					ps.setString(29, testBean.getProctoringEnabled());
					ps.setString(30, testBean.getShowCalculator());
					ps.setLong(31, testBean.getId());
				}
			});
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.error("Error while updating test with lastModified Result Live DateTime for testId: {}, Exception thrown: {}", testBean.getId(), ex.toString());
			return false;
		}
		
		return true;
	}

	
	public List<DissertationResultProcessingDTO> getTestScoreForIA(String sapid, String commaSepratedTestIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select sapid,showResult,testId,score,COALESCE(score,0) as scoreInInteger from exam.test_student_testdetails where sapid = ? and testId in ("+commaSepratedTestIds+") ";
		return jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class));
	}
	
	public List<Integer> getTestIds(String refId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select testId from exam.test_testid_configuration_mapping where referenceId in ("+refId+")";
		return jdbcTemplate.queryForList(sql,Integer.class);
	}

	public List<TestExamBean> getExamTest(String commaSepratedTestIds) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="  select id, startDate, endDate,showResultsToStudents,testName,maxScore  from exam.test where id in ("+commaSepratedTestIds+") ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TestExamBean.class));
	}

	
	@Transactional(readOnly = true)
	public int getTypeByQuestionId(Long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT type " + 
						"FROM exam.test_questions " + 
						"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, questionId);
	}
	
	@Transactional(readOnly = true)
	public List<String> getAnswersBySapidTestIdQuestionId(String sapid, Long testId, Long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT answer " + 
						"FROM exam.test_students_answers " + 
						"WHERE sapid = ?" + 
						"	AND testId = ?" + 
						"	AND questionId = ?";
		
		return jdbcTemplate.query(query, new SingleColumnRowMapper<>(String.class), sapid, testId, questionId);
	}
	
	@Transactional(readOnly = true)
	public int checkTestMadeLive(final long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT count(*) " + 
						"FROM exam.test t " + 
						"INNER JOIN exam.test_live_settings tls" + 
						"	ON t.acadYear = tls.acadYear" + 
						"	AND t.acadMonth = tls.acadMonth" + 
						"	AND t.year = tls.examYear" + 
						"	AND t.month = tls.examMonth" + 
						"	AND t.applicableType = tls.applicableType" + 
						"	AND t.referenceId = tls.referenceId " + 
						"WHERE t.id = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, testId);
	}
	
	@Transactional(readOnly = true)
	public int getTestStudentAttemptedCount(final long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT count(*) " + 
						"FROM exam.test_student_testdetails " + 
						"WHERE testId = ?" + 
						"	AND sapid NOT LIKE '777777%'";
		
		return jdbcTemplate.queryForObject(query, Integer.class, testId);
	}

	@Transactional(readOnly = true)
	public int checkTestResultsLive(final long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT count(*) " + 
						"FROM exam.test " + 
						"WHERE id = ?" + 
						"	AND showResultsToStudents = 'Y'";
		
		return jdbcTemplate.queryForObject(query, Integer.class, id);
	}
	
	@Transactional(readOnly = true)
	public List<TestQuestionExamBean> getQuestionsByTestId(final Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT * FROM exam.test_questions " + 
						"WHERE testId = ? " + 
						"ORDER BY sectionId, type";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TestQuestionExamBean.class), testId);
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, String> getSectionIdNameMapByTestId(final Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT ts.id, ts.sectionName " + 
						"FROM exam.test_sections ts " + 
						"INNER JOIN exam.test_questions_configuration tqc" + 
						"	ON ts.id = tqc.sectionId " + 
						"WHERE tqc.testId = ? " + 
						"GROUP BY tqc.sectionId";
		
		return jdbcTemplate.query(query, (ResultSet rs) -> resultSetMapper(rs, Integer.class, String.class), testId);
	}
	
	@Transactional(readOnly = true)
	public List<Integer> getBodQuestionsByTestIdActive(final Long testId, final String isActive) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT questionId " + 
						"FROM exam.test_question_bod " + 
						"WHERE testId = ?" + 
						"	AND active = ?";
		
		return jdbcTemplate.queryForList(query, Integer.class, testId, isActive);
	}
	
	@Transactional(readOnly = true)
	public TestExamBean getTestTypeEndDateDurationByTestId(final Long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT endDate, duration, testType " + 
						"FROM exam.test " + 
						"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(TestExamBean.class), testId);
	}
	
	@Transactional(readOnly = true)
	public int checkBodByTestIdQuestionId(final Long testId , final Long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT count(*) " + 
						"FROM exam.test_question_bod " + 
						"WHERE testId = ?" + 
						"	AND questionId = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, testId, questionId);
	}
	
	@Transactional(readOnly = false)
	public int insertBenefitOfDoubt(final long testId, final long questionId, final String active, final String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "INSERT INTO exam.test_question_bod " + 
				"(`testId`, `questionId`, `active`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
				"VALUES(?, ?, ?, ?, sysdate(), ?, sysdate())";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, testId);
				ps.setLong(2, questionId);
				ps.setString(3, active);
				ps.setString(4, userId);
				ps.setString(5, userId);
			}
		});
	}
	
	@Transactional(readOnly = false)
	public int updateBenefitOfDoubt(final long testId, final long questionId, final String active, final String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE `exam`.`test_question_bod` " + 
						"SET `active` = ?," + 
						"	`lastModifiedBy` = ?," + 
						"	`lastModifiedDate` = sysdate() " + 
						"WHERE `testId` = ?" + 
						"	AND `questionId` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, active);
				ps.setString(2, userId);
				ps.setLong(3, testId);
				ps.setLong(4, questionId);
			}
		});
	}
	
	@Transactional(readOnly = true)
	public List<StudentsTestDetailsExamBean> getStudentTestAttemptQuestions(final long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT `id`, `sapid`, `attempt`, `testQuestions` " + 
						"FROM `exam`.`test_student_testdetails` " + 
						"WHERE `testId` = ?" +
						"	AND `sapid` NOT LIKE '777777%'";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(StudentsTestDetailsExamBean.class), testId);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentTestScore(final long id, final double score, final String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE `exam`.`test_student_testdetails` " + 
						"SET `score` = ?," + 
						"	`lastModifiedBy` = ?," + 
						"	`lastModifiedDate` = sysdate() " + 
						"WHERE `id` = ?";
		
		return jdbcTemplate.update(query, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setDouble(1, score);
				ps.setString(2, userId);
				ps.setLong(3, id);
			}
		});
	}
	
	@Transactional(readOnly = true)
	public int getStudentAttemptCountByTestId(final long testId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT COUNT(DISTINCT(`sapid`)) " + 
						"FROM `exam`.`test_student_testdetails` " + 
						"WHERE `testId` = ?" + 
						"	AND `sapid` NOT LIKE '777777%'";
		
		return jdbcTemplate.queryForObject(query, Integer.class, testId);
	}
	
	@Transactional(readOnly = true)
	public int getMaxAttemptById(final long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT maxAttempt " + 
						"FROM exam.test " + 
						"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, Integer.class, id);
	}
	
	@Transactional(readOnly = true)
	public List<String> getCorrectOptionsByQuestionId(final long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT id " + 
						"FROM exam.test_question_options " + 
						"WHERE questionId = ?" + 
						"	AND isCorrect = 'Y'";
		
		return jdbcTemplate.queryForList(query, String.class, questionId);
	}
	
	@Transactional(readOnly = true)
	public List<StudentQuestionResponseExamBean> getAnswerAttemptsByTestIdQuestionId(final long testId, final long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT `sapid`, `attempt`, `answer` " + 
						"FROM `exam`.`test_students_answers` " + 
						"WHERE `testId` = ?" + 
						"	AND `questionId` = ?" + 
						"	AND `sapid` NOT LIKE '777777%'";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(StudentQuestionResponseExamBean.class), testId, questionId);
	}
	
	@Transactional(readOnly = true)
	public String getTestNameById(final long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT testName " + 
						"FROM exam.test " + 
						"WHERE id = ?";
		
		return jdbcTemplate.queryForObject(query, String.class, id);
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, String> getOptionDataByQuestionId(final long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT id, optionData " + 
						"FROM exam.test_question_options " + 
						"WHERE questionId = ?";
		
		return jdbcTemplate.query(query, (ResultSet rs) -> resultSetMapper(rs, Integer.class, String.class), questionId);
	}
	
	@Transactional(readOnly = true)
	public List<StudentQuestionResponseExamBean> getAnswerAttemptDataByTestIdQuestionId(final long testId, final long questionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT `sapid`, `attempt`, `answer`, `marks`, `facultyId`, `isChecked` " + 
						"FROM `exam`.`test_students_answers` " + 
						"WHERE `testId` = ?" + 
						"	AND `questionId` = ?" + 
						"	AND `sapid` NOT LIKE '777777%'";
		
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(StudentQuestionResponseExamBean.class), testId, questionId);
	}
	
	@Transactional(readOnly = true)
	public int getOptionSelectedCount(final long testId, final long questionId, final String answer) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT COUNT(*) " + 
						"FROM `exam`.`test_students_answers` " + 
						"WHERE `testId` = ?" + 
						"	AND `questionId` = ?" + 
						"	AND `answer` = ?" + 
						"	AND `sapid` NOT LIKE '777777%'";
		
		return jdbcTemplate.queryForObject(query, Integer.class, testId, questionId, answer);
	}
}
