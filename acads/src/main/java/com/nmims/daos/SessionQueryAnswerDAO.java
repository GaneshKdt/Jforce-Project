package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.helpers.PaginationHelper;

public class SessionQueryAnswerDAO extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Transactional(readOnly = true)
	public Integer checkForSameAskQuery(SessionQueryAnswer sessionQuery) {
		String sql = " SELECT  " + 
				"    COUNT(*) " + 
				"FROM " + 
				"    acads.session_query_answer " + 
				"WHERE " + 
				"    assignedToFacultyId = ? " + 
				"        AND query = ? " + 
				"        AND sapid = ? " +
				"        AND subject = ? " +
				"        AND queryType = ? " ;
		Integer count =	jdbcTemplate.queryForObject(sql, new Object[] {
				sessionQuery.getAssignedToFacultyId(),
				sessionQuery.getQuery(), 
				sessionQuery.getSapId(),
				sessionQuery.getSubject(),
				sessionQuery.getQueryType()
				}, Integer.class);
		return count;
	}
	
	@Transactional(readOnly = false)
	public long saveQuery(final SessionQueryAnswer sessionQuery) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		/*String sql = "INSERT INTO acads.session_query_answer(sapId, sessionId, query,queryType, assignedToFacultyId, createdBy, createdDate) VALUES "
				+ "(?,?,?,?,?,?, sysdate())";
      
		jdbcTemplate.update(sql, new Object[] { sessionQuery.getSapId(),
				sessionQuery.getSessionId(), sessionQuery.getQuery(), sessionQuery.getQueryType(),assignedToFacultyId, 
				sessionQuery.getSapId() });*/
		if(sessionQuery.getHasTimeBoundId() == null) {
			sessionQuery.setHasTimeBoundId("N");
		}
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO acads.session_query_answer(sapId, sessionId, query,queryType, assignedToFacultyId, createdBy, createdDate, subject, month, year,timeBoundId,hasTimeBoundId,programSemSubjectId) VALUES (?,?,?,?,?,?, sysdate(),?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, sessionQuery.getSapId());
				statement.setString(2, sessionQuery.getSessionId());
				statement.setString(3,sessionQuery.getQuery());
				statement.setString(4,sessionQuery.getQueryType());
				statement.setString(5,sessionQuery.getAssignedToFacultyId());
				statement.setString(6,sessionQuery.getSapId());
				statement.setString(7,sessionQuery.getSubject());
				statement.setString(8,sessionQuery.getMonth());
				statement.setString(9,sessionQuery.getYear());
				statement.setString(10,sessionQuery.getTimeBoundId());
				statement.setString(11,sessionQuery.getHasTimeBoundId());
				statement.setString(12,sessionQuery.getProgramSemSubjectId());
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForSessionByStudent(
			SessionQueryAnswer sessionQuery) {
		String sql = "Select * from acads.session_query_answer where sessionId = ? and sapid = ? order by createdDate desc";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { sessionQuery.getSessionId(),
						sessionQuery.getSapId() }, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return myQueries;
	}

	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicQueriesForSession(
			SessionQueryAnswer sessionQuery) {
		String sql = "Select * from acads.session_query_answer where sessionId = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
		List<SessionQueryAnswer> publicQueries = jdbcTemplate.query(
				sql,
				new Object[] { sessionQuery.getSessionId(),
						sessionQuery.getSapId() }, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return publicQueries;
	}

	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForSessionByFaculty(
			SessionQueryAnswer sessionQuery, String userId) {
		String sql = "Select sqa.* from acads.session_query_answer sqa,"
				+ " acads.session_attendance_feedback saf,acads.faculty f where saf.sessionId = sqa.sessionId and saf.facultyId = f.facultyId  and  saf.sapid = sqa.sapid and sqa.sessionId = ?  and saf.facultyId = ? order by createdDate desc";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(sql,
				new Object[] { sessionQuery.getSessionId(), userId },
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForSession(
			SessionQueryAnswer sessionQuery, String facultyId , PersonAcads user) {
		/*
		 * String sql =
		 * " select * from acads.session_query_answer where  sessionId=? and sapid in "
		 * +
		 * " (select sapid from acads.session_query_answer where sessionId=? and sapId not in "
		 * +
		 * " (select sapid from acads.session_attendance_feedback where attended='Y' and sessionId= ?) "
		 * + " OR " +
		 * " sapId in(select sapid from acads.session_attendance_feedback where attended='Y' and sessionId= ?"
		 * + " and facultyId=? )) " + " OR sapId is null and sessionId=?";
		 */
		
		ArrayList<String> parameters = new ArrayList<String>();
		String sql = " select * from acads.session_query_answer sqa"
				+ " where sqa.sessionId = ? ";
		
		parameters.add(sessionQuery.getSessionId());
		
		// show only Academic Query to faculty
		if(user.getRoles().contains("Faculty")){
			sql += " and sqa.queryType = 'Academic' "
				 + " and ( sqa.assignedToFacultyId = ? or sqa.answeredByFacultyId = ?  )";
			parameters.add(facultyId);
			parameters.add(facultyId);
		}
		
		Object [] arg = parameters.toArray();
		
		List<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		try {
			allQueries = (List<SessionQueryAnswer>)jdbcTemplate.query(sql,arg,
					new BeanPropertyRowMapper(SessionQueryAnswer.class)
			);
		} catch (Exception e) {
			  
		}
		return allQueries;

	}
	
	@Transactional(readOnly = false)
	public void updateAnswer(SessionQueryAnswer sessionQuery) {
		String sql = "Update acads.session_query_answer" 
				+ " set isAnswered = 'Y', "
				+ " answer = ?,"
				+ " isPublic = ? ," 
				+ " lastModifiedBy = ?,"
				+ " answeredByFacultyId = ?,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";
		jdbcTemplate.update(sql, 
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException { 
						// TODO Auto-generated method stub
						ps.setString(1, sessionQuery.getAnswer());
						ps.setString(2, sessionQuery.getIsPublic());
						ps.setString(3, sessionQuery.getLastModifiedBy());
						ps.setString(4, sessionQuery.getAnsweredByFacultyId());
						ps.setString(5, sessionQuery.getId());
						
					}
				}
		);

	}

	@Transactional(readOnly = false)
	public void updateSalesforceErrorMessage(SessionQueryAnswer sessionQuery) {
		String sql = "Update acads.session_query_answer" 
				+ " set errorMessage = ? , "
				+ " caseId = ?,"
				+ " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";
		jdbcTemplate.update(sql, 
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, sessionQuery.getErrorMessage());
						ps.setString(2, sessionQuery.getCaseId());
						ps.setString(3, sessionQuery.getLastModifiedBy());
						ps.setString(4, sessionQuery.getId());
					}
				});

	}
	
	@Transactional(readOnly = false)
	public void allocateFacultyToAnswer(final List<Long> recordIdList,
			final SessionQueryAnswer sqa) {
		String sql = "Update acads.session_query_answer"
				+ " set assignedFaculty = ?," + " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate() where id = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Long id = recordIdList.get(i);
				ps.setString(1, sqa.getFacultyId());
				ps.setString(2, sqa.getLastModifiedBy());
				ps.setLong(3, id);

			}

			@Override
			public int getBatchSize() {
				return recordIdList.size();
			}
		});
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getListOfSessionQueryAnswerForMonthAndYear(
			SessionQueryAnswer sqa) {
		String sql = " select sqa.*,s.sessionName,s.subject,s.facultyId,s.altFacultyId,f.firstName,f.lastName from acads.session_query_answer sqa,acads.sessions s,acads.faculty f "
				+ " where s.id = sqa.sessionId and s.facultyId = f.facultyId and (sqa.answer is null or sqa.answer = '') and s.month = ? and s.year = ? ";
		return jdbcTemplate.query(sql,
				new Object[] { sqa.getMonth(), sqa.getYear() },
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
	}

	@Transactional(readOnly = true)
	public PageAcads<SessionQueryAnswer> getQueries(int pageNo, int pageSize,
			SessionQueryAnswer searchBean) {
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select sqa.*,f.facultyId,f.firstName,f.lastName,f.email,s.sessionName,s.subject,s.year,s.month, HOUR(TIMEDIFF(sysdate(), sqa.createdDate)) as hoursSinceQuestions, IFNULL(HOUR(TIMEDIFF(sqa.createdDate, sqa.lastModifiedDate)),0) as hoursSinceFacultyReply from acads.faculty f, acads.sessions s,  acads.session_query_answer sqa "
				+ "  where sqa.sessionId = s.id "
				+ " and sqa.assignedToFacultyId = f.facultyId "
				+ " and s.year = ? and s.month = ? ";

		String countSql = "select count(*) from acads.faculty f, acads.sessions s,  acads.session_query_answer sqa  "
				+ "  where sqa.sessionId = s.id "
				+ " and sqa.assignedToFacultyId = f.facultyId "
				+ " and s.year = ? and s.month = ? ";

		parameters.add(searchBean.getYear());
		parameters.add(searchBean.getMonth());

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and s.Subject =  ? ";
			countSql = countSql + " and s.Subject =  ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getIsAnswered() != null
				&& ("Y".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and sqa.answer is not null and sqa.answer  <> '' ";
			countSql = countSql + " and sqa.answer is not null and sqa.answer  <> '' ";
		}

		if (searchBean.getIsAnswered() != null
				&& ("N".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and ( sqa.answer is null OR sqa.answer = '' )  ";
			countSql = countSql + " and ( sqa.answer is null OR sqa.answer = '' ) ";
		}

		sql = sql + " order by s.subject, s.sessionName asc";

		Object[] args = parameters.toArray();
		PaginationHelper<SessionQueryAnswer> pagingHelper = new PaginationHelper<SessionQueryAnswer>();
		PageAcads<SessionQueryAnswer> page = new PageAcads<SessionQueryAnswer>();
		try {
			page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args,
					pageNo, pageSize, new BeanPropertyRowMapper(
							SessionQueryAnswer.class));
		} catch (Exception e) {
			  
		}

		return page;
	}
	// Dao methods for Course Query Start
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForCourseByStudent(String sapId, String subject) {
		String sql = "Select * from acads.session_query_answer where subject = ? and sapid = ? and (hasTimeBoundId <> 'Y' or hasTimeBoundId IS NULL) order by createdDate desc";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { subject,sapId },
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getMyPostQueries(String sapId, String timeBoundId,int startFrom,int pagination) {
		String sql = "Select s_q_w.*,concat('Prof. ',f.firstName,' ',f.lastName) facultyName,"
				+ "concat(s.firstName,' ',s.lastName) `name` from acads.session_query_answer as s_q_w,"
				+ "exam.students as s,acads.faculty as f where f.facultyId = s_q_w.assignedToFacultyId and "
				+ "s.sapId = s_q_w.sapId and timeBoundId = ? and s_q_w.sapid = ? and isPublic ='N' "
				+ "order by createdDate desc limit ? offset ?";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { timeBoundId,sapId ,pagination,startFrom},
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicPostQueries(String timeBoundId,int startFrom,int pagination) {
		String sql = "Select s_q_w.*,concat('Prof ',f.firstName,' ',f.lastName) facultyName,concat(s.firstName,' ',s.lastName) `name`"
				+ " from acads.session_query_answer as s_q_w,exam.students as s,acads.faculty as f "
				+ "where f.facultyId = s_q_w.assignedToFacultyId and s.sapId = s_q_w.sapId and "
				+ "timeBoundId = ? and isPublic ='Y' order by createdDate desc limit ? offset ?";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { timeBoundId,pagination,startFrom },
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myQueries;
	}

	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicQueriesForCourse(
			String sapId , String subject, String consumerProgramStructureId ) {
	
//		String sql = "Select * from acads.session_query_answer where (hasTimeBoundId <> 'Y' or hasTimeBoundId IS NULL) and subject = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
		String sql = " SELECT  " + 
				"    a.* " + 
				"FROM " + 
				"    acads.session_query_answer a " + 
				"        INNER JOIN " + 
				"    exam.students s ON s.sapid = a.sapid " + 
				"WHERE " + 
				"    (a.hasTimeBoundId <> 'Y' " + 
				"        OR a.hasTimeBoundId IS NULL) " + 
				"        AND a.subject = ? " + 
				"        AND s.consumerProgramStructureId = ? " + 
				"        AND a.sapid <> ?  " + 
				"        AND a.isPublic = 'Y'" + 
				"ORDER BY a.createdDate DESC ";
		List<SessionQueryAnswer> publicQueries = jdbcTemplate.query(
				sql,
				new Object[] { subject,
						consumerProgramStructureId,
						sapId }, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return publicQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicQueriesForCourseMobile(
			String sapId , String subject, String consumerProgramStructureId ) {
	
//		String sql = "Select * from acads.session_query_answer where (hasTimeBoundId <> 'Y' or hasTimeBoundId IS NULL) and subject = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
		String sql = " SELECT  " + 
				"    a.* , f.firstName,f.lastName " + 
				"FROM " + 
				"    acads.session_query_answer a " + 
				"        INNER JOIN " + 
				"    exam.students s ON s.sapid = a.sapid " +
				"INNER JOIN " +
				"acads.faculty f ON f.facultyId = a.answeredByFacultyId " + 
				"WHERE " + 
				"    (a.hasTimeBoundId <> 'Y' " + 
				"        OR a.hasTimeBoundId IS NULL) " + 
				"        AND a.subject = ? " + 
				"        AND s.consumerProgramStructureId = ? " + 

				"        AND a.isPublic = 'Y'" + 
				" ORDER BY a.createdDate DESC ";
		List<SessionQueryAnswer> publicQueries = jdbcTemplate.query(
				sql,
				new Object[] { subject,
						consumerProgramStructureId }, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return publicQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getAllQueriresBySapId(String sapId) {
		
		String sql = "Select * from acads.session_query_answer where sapId = ? order by createdDate desc ";
		List<SessionQueryAnswer> allCourseQueries = jdbcTemplate.query(
				sql,
				new Object[] {sapId}, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return allCourseQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getAllCourseQueriresByFaculty(String facultyId) {
		
		// added static year month by Abhay 
		String sql = "Select * from acads.session_query_answer where assignedToFacultyId = ? and year in (2022,2023) and month in ('Jan', 'Jul')  and queryType <> 'Technical' order by createdDate desc ";
		
		List<SessionQueryAnswer> allCourseQueries = jdbcTemplate.query(
				sql,
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, facultyId);
						
					}
				}, new BeanPropertyRowMapper<SessionQueryAnswer>(
						SessionQueryAnswer.class));
		return allCourseQueries;
	}
	

	//Get students List by criteeria given in Post query Start
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getAllQueriesForPostQueryReport(SessionQueryAnswer searchBean, String authorizedCenterCodes) {

		ArrayList<Object> parameters = new ArrayList<Object>();
         
		String sql = "SELECT sqa.* , b.name AS batchName, f.firstName, f.lastName, f.email FROM acads.faculty f, acads.session_query_answer sqa "
				+ "LEFT JOIN lti.student_subject_config ssc ON sqa.timeBoundId = ssc.id "
				+ "LEFT JOIN exam.batch b ON  ssc.batchId = b.id "
				+ " where  f.facultyId = sqa.assignedToFacultyId "
				+ " and sqa.queryType <> 'Technical' "
				+ " and sqa.year=? "
				+ " and sqa.month=? ";
		parameters.add(searchBean.getYear());
		parameters.add(searchBean.getMonth());
		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and sqa.subject =  ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getIsAnswered() != null
				&& ("Y".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and sqa.answer is not null and sqa.answer  <> '' ";
		}

		if (searchBean.getIsAnswered() != null
				&& ("N".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and ( sqa.answer is null OR sqa.answer = '' )  ";
		}

		Object[] args = parameters.toArray();
		
		List<SessionQueryAnswer> allQueries= jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SessionQueryAnswer.class));


		return allQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getAllQnAReport(SessionQueryAnswer searchBean) {

		ArrayList<Object> parameters = new ArrayList<Object>();
         
		String sql = "SELECT " + 
				"    q.status AS isAnswered, " + 
				"    q.query, " + 
				"    q.answer, " + 
				"    q.sapId, " + 
				"    s.subject, " + 
				"    s.facultyId, " + 
				"	 concat(f1.firstName,' ',f1.lastName) as facultyName, " +
				"	 f1.email as email, " + 
				"	 q.createdDate as qacreatedDate, " + 
				"	 q.dateModified as dateModified, " +
				"    b.name AS batchName, "+ 
				"    s.year,s.month " + 
				"FROM " + 
				"    acads.session_question_answer q " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON s.id = q.sessionId " + 
				"        LEFT JOIN " + 
				"    acads.sessionplan_module m ON m.id = s.moduleid " + 
				"        LEFT JOIN " + 
				"    acads.sessionplanid_timeboundid_mapping st ON m.sessionPlanId = st.sessionPlanId " + 
				"        LEFT JOIN " + 
				"    lti.student_subject_config ssc ON st.timeboundId = ssc.id " + 
				"    	 LEFT JOIN " + 
				"	 acads.faculty f1 ON f1.facultyId = s.facultyId" +
				"        LEFT JOIN " + 
				"    exam.batch b ON ssc.batchId = b.id " + 
				"WHERE " + 
				"    s.year = ? AND s.month = ? AND LENGTH(q.query) > 10"; 
		parameters.add(searchBean.getYear());
		parameters.add(searchBean.getMonth());
		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and s.subject =  ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getIsAnswered() != null
				&& ("Y".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and q.status= 'Answered'";
		}

		if (searchBean.getIsAnswered() != null
				&& ("N".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and q.status= 'Open' ";
		}

		Object[] args = parameters.toArray();
		
		List<SessionQueryAnswer> allQueries= jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SessionQueryAnswer.class));


		return allQueries;
	}
	
	@Transactional(readOnly = true)
	public ExamOrderAcadsBean getExamOrderBeanWhereContentLive(){
		ExamOrderAcadsBean examOrder = null;
		try{

			String sql = "SELECT * FROM exam.examorder e where e.order = (select max(eo.order) from exam.examorder eo where eo.acadContentLive = 'Y')";

			examOrder =  (ExamOrderAcadsBean)jdbcTemplate.queryForObject(sql,new Object[]{}, new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
			return examOrder;

		}catch(Exception e){
			  
			return null;
		}
	}

	@Transactional(readOnly = true)
	public String getFaultyIdToAnswerCourseQuery(String subject) {
		

		try{

			String sql="select  facultyId, altFacultyId,altFacultyId2, altFacultyId3 " + 
					"	from acads.sessions where subject= ? "
					+ " and year= ? and month= ? ";
			
			List<SessionDayTimeAcadsBean> listOfSessions = (List<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql,
													  		  new Object[]{subject, getLiveAcadConentYear(),
															  getLiveAcadConentMonth()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			
			HashSet<String> listOfFacultyId = new HashSet<String>();
			
			if(listOfSessions!=null && !listOfSessions.isEmpty()) {
			for(SessionDayTimeAcadsBean session :listOfSessions){
				listOfFacultyId.add(session.getFacultyId());
				listOfFacultyId.add(session.getAltFacultyId());
				listOfFacultyId.add(session.getAltFacultyId2());
				listOfFacultyId.add(session.getAltFacultyId3());
			}
			String facultyId = null;
			for(String f :listOfFacultyId){
				if(!StringUtils.isBlank(f)) {
					facultyId=f;
					break;
				}
			}
			return facultyId;
			}else {
				return null;
			}
		}catch(Exception e){
			  
			return null;
		}
		
	}
	
//	get list of faculty 
	@Transactional(readOnly = true)
	public List<FacultyAcadsBean> getFacultyForASubject (String subject, String consumerProgramStructureId,
			String currentAcadYear, String currentAcadMonth
			){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FacultyAcadsBean> facultyForASubject = new ArrayList<>();
		
		
		/*
		 * String sql ="SELECT f.* FROM acads.faculty_course fc, acads.faculty f " +
		 * " where fc.subject = ? " + "and fc.year = '"+CURRENT_ACAD_YEAR+"'  " +
		 * "and fc.month = '"+CURRENT_ACAD_MONTH+"' " +
		 * " and f.facultyId = fc.facultyId";
		 */
		
		String sql = "  SELECT  " + 
				"    f.* " + 
				"FROM " + 
				"    acads.session_subject_mapping ssm " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON ssm.sessionId = s.id " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON f.facultyId = s.facultyId " + 
				"        OR f.facultyId = s.altFacultyId " + 
				"        OR f.facultyId = s.altFacultyId2 " + 
				"        OR f.facultyId = s.altFacultyId3 " + 
				"WHERE " + 
				"         s.year = ? " + 
				"        AND s.month = ? " + 
				"    	 AND s.subject = ? " +
				"        AND ssm.consumerProgramStructureId = ? " +  
				"        AND f.facultyId <> 'NGASCE7777' " + 
				"GROUP BY f.facultyId  ";
		try {
			facultyForASubject = (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql, 
					new Object[]{currentAcadYear, currentAcadMonth, subject, consumerProgramStructureId},
					new BeanPropertyRowMapper<FacultyAcadsBean>(FacultyAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return facultyForASubject;
		
	}

	

	//Dao methods for Course Query End
	
	/*
	 * added by stef on 6-Nov
	 * 
	 * public ArrayList<String> getSessionList(){
		String sql="SELECT DISTINCT sessionName from acads.sessions";
		
	
		ArrayList<String> sessionList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sessionList;
	}*/
	
	@Transactional(readOnly = true)
	public String getConsumerProgramStructureIdBySapId(String sapId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String consumerProgramStructureId = "";
		try {
			String sql = "select consumerProgramStructureId from exam.students where sapid=?";

			consumerProgramStructureId = jdbcTemplate.queryForObject(sql, new Object[] { sapId }, (String.class));

		} catch (Exception e) {
			// TODO: handle exception
			  

		}
		return consumerProgramStructureId;
	}

	//Added by Saurabh
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicQueriesForCourseV2(
			String sapId , String sessionId) {
		String sql = "SELECT a.* FROM acads.session_query_answer a " + 
				"WHERE a.sessionId = ? " + 
				"AND a.sapid <> ? AND a.isPublic = 'Y' " + 
				"ORDER BY a.createdDate DESC";
		List<SessionQueryAnswer> publicQueries = jdbcTemplate.query(
				sql,
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, sessionId);
						ps.setString(2, sapId);
					}
				}, new BeanPropertyRowMapper(
						SessionQueryAnswer.class));
		return publicQueries;
	}
	
	@Transactional(readOnly = true)
	public SessionQueryAnswer getQueryById(String id) {
		SessionQueryAnswer sessionQueryAnswer = null;
		String sql = "Select * from acads.session_query_answer where id = ? ";

		try {
			sessionQueryAnswer = (SessionQueryAnswer)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
		} catch (Exception e) {
			  
		}
		return sessionQueryAnswer;
	}
	

	@Transactional(readOnly = false)
	public void postQueryAsForum(SessionQueryAnswer sessionQuery, String year, String month) {
		final String sql = "INSERT INTO collaborate.forum_thread "
				+ " (year,month,subject,title,description,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
				+ " VALUES "
				+ " (?,?,?,?,?,?,sysdate(),?,sysdate(),?)";

		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, sessionQuery.getSubject());
				ps.setString(4, sessionQuery.getTitle());
				ps.setString(5, sessionQuery.getQuery());
				ps.setString(6, sessionQuery.getAssignedToFacultyId());
				ps.setString(7, sessionQuery.getAssignedToFacultyId());
				ps.setString(8, "Active");
				return ps;
			}
		};
		jdbcTemplate.update(psc);
	}
	
	@Transactional(readOnly = false)
	public void updateForumStatus(String facultyId,String sessionQueryId) {
		String sql = "Update acads.session_query_answer" 
				+ " set isForumThread = 'Y', lastModifiedBy = ?," +  
				"	lastModifiedDate = sysdate() where id = ? ";
		jdbcTemplate.update(sql, 
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException { 
						// TODO Auto-generated method stub
						ps.setString(1, facultyId);
						ps.setString(2, sessionQueryId);
						
					}
				}
		);

	}
	
	@Transactional(readOnly = true)
	public String getProgramSemSubjectIdOfSubjectAndSapId(String sapId, String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String programSemSubjectId = "";
		try {
			String sql = "SELECT  pss.id as programSemSubjectId FROM exam.registration r INNER JOIN " + 
					" exam.program_sem_subject pss ON pss.sem = r.sem " + 
					"AND pss.consumerProgramStructureId = r.consumerProgramStructureId " + 
					"WHERE sapid = ? AND pss.subject = ?";

			programSemSubjectId = jdbcTemplate.queryForObject(sql, new Object[] { sapId , subject }, (String.class));

		} catch (Exception e) {
			  
		}
		return programSemSubjectId;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramBean> getProgramCodeAndMaterKeyList() {
		String sql =" SELECT code, cps.id FROM exam.consumer_program_structure cps " + 
					" INNER JOIN  exam.program p ON cps.programId = p.id ";
		List<ProgramBean> programAndMasterKey = new ArrayList<ProgramBean>();
		try {
			programAndMasterKey = (ArrayList<ProgramBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<ProgramBean>(ProgramBean.class));
		} catch (Exception e) {
			  
		}
		return programAndMasterKey;
	}
	
	@Transactional(readOnly = true)
	public String getProgramSemSubjectIdByTimeBoundId(String timeBoundId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String programSemSubjectId = "";
		try {
			String sql = "SELECT prgm_sem_subj_id FROM lti.student_subject_config where id=?";

			programSemSubjectId = jdbcTemplate.queryForObject(sql, new Object[] { timeBoundId }, (String.class));

		} catch (Exception e) {
			  
		}
		return programSemSubjectId;
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentDataBySapId(String sapId){
		StudentAcadsBean studentBean = null;

			String sql = "SELECT program, enrollmentMonth, enrollmentYear, consumerProgramStructureId FROM exam.students where sapid=?";

			studentBean =  (StudentAcadsBean)jdbcTemplate.queryForObject(sql,new Object[]{sapId}, new BeanPropertyRowMapper<>(StudentAcadsBean.class));
			return studentBean;

	}
	
	@Transactional(readOnly = true)
	public boolean checkForPaidStudentOnsapIdAndPssId(String sapId, String pssId){
			
			String sql = "SELECT count(*) FROM exam.student_session_courses_mapping where userId=? and program_sem_subject_id=?;";

			Integer count =  jdbcTemplate.queryForObject(sql,new Object[]{sapId,pssId}, Integer.class);
			
			if(count>0) {
				return true;
			}else {
				return false;
			}

		
	}
	
	@Transactional(readOnly = true)
	public List<String> getPssIdBySubjectCodeId(String pssId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> pssIdList = new ArrayList<String>();
		String sql="SELECT id FROM exam.mdm_subjectcode_mapping WHERE subjectCodeId=(SELECT subjectCodeId FROM exam.mdm_subjectcode_mapping where id=?)";
		pssIdList = (List<String>)jdbcTemplate.query(sql,new Object[]{pssId}, new SingleColumnRowMapper<>(String.class));

		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getPublicQueriesForCourseV2(String sapId ,List<String> pssIdList, String year, String month ) {
		
		List<SessionQueryAnswer> publicQueries=new ArrayList<SessionQueryAnswer>();
		namedParameterJdbcTemplate= new NamedParameterJdbcTemplate(dataSource);
		String sql="Select * from acads.session_query_answer where sapid <> (:sapId ) and (hasTimeBoundId <> 'Y' OR hasTimeBoundId IS NULL) and year=(:year ) and month=(:month ) and programSemSubjectId in (:pssIdList ) AND isPublic = 'Y' ORDER BY createdDate DESC";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		parameters.addValue("sapId", sapId);
		parameters.addValue("year", year);
		parameters.addValue("month", month);
		parameters.addValue("pssIdList", pssIdList);
		publicQueries = namedParameterJdbcTemplate.query(
				sql, parameters, new BeanPropertyRowMapper<>(
						SessionQueryAnswer.class));
		return publicQueries;
	}
}
