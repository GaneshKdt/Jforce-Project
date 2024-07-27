package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.nmims.beans.ExamOrderCareerservicesBean;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.PageCareerservicesBean;
import com.nmims.beans.PersonCareerservicesBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.helpers.PaginationHelper;

public class SessionQueryAnswerDAO extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	private static final Logger logger = LoggerFactory.getLogger(SessionQueryAnswerDAO.class);
 
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

	public long saveQuery(final SessionQueryAnswerCareerservicesBean sessionQuery) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		/*String sql = "INSERT INTO products.session_query_answer(sapId, sessionId, query,queryType, assignedToFacultyId, createdBy, createdDate) VALUES "
				+ "(?,?,?,?,?,?, sysdate())";
      
		jdbcTemplate.update(sql, new Object[] { sessionQuery.getSapId(),
				sessionQuery.getSessionId(), sessionQuery.getQuery(), sessionQuery.getQueryType(),assignedToFacultyId, 
				sessionQuery.getSapId() });*/
		
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(""
							+ "INSERT INTO "
							+ "products.session_query_answer"
							+ "("
								+ "sapId, sessionId, query, "
								+ "queryType, assignedToFacultyId, createdBy, "
								+ "createdDate"
							+ ") VALUES ("
								+ "?,?,?, "
								+ "?,?,?,"
							+ "sysdate()) ", Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, sessionQuery.getSapId());
					statement.setString(2, sessionQuery.getSessionId());
					statement.setString(3,sessionQuery.getQuery());
					statement.setString(4,sessionQuery.getQueryType());
					statement.setString(5,sessionQuery.getAssignedToFacultyId());
					statement.setString(6, sessionQuery.getSapId());
					return statement;
				}
			}, holder);

			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return 0;
		}
	}
	
	
	public List<SessionQueryAnswerCareerservicesBean> getQueriesForSessionByStudent(
			SessionQueryAnswerCareerservicesBean sessionQuery) {

		String sql = "Select * from products.session_query_answer where sessionId = ? and sapid = ? order by createdDate desc";
		List<SessionQueryAnswerCareerservicesBean> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { 
							sessionQuery.getSessionId(),
							sessionQuery.getSapId() 
						}, 
						new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class)
				);
		return myQueries;
	}

	public List<SessionQueryAnswerCareerservicesBean> getPublicQueriesForSession(
			SessionQueryAnswerCareerservicesBean sessionQuery) {
		String sql = "Select * from products.session_query_answer where sessionId = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
		List<SessionQueryAnswerCareerservicesBean> publicQueries = jdbcTemplate.query(
				sql,
				new Object[] { sessionQuery.getSessionId(),
						sessionQuery.getSapId() }, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(
						SessionQueryAnswerCareerservicesBean.class));
		return publicQueries;
	}

	public List<SessionQueryAnswerCareerservicesBean> getQueriesForSessionByFaculty(
			SessionQueryAnswerCareerservicesBean sessionQuery, String userId) {
		String sql = "Select sqa.* from products.session_query_answer sqa,"
				+ " products.session_attendance saf,acads.faculty f where saf.sessionId = sqa.sessionId and saf.facultyId = f.facultyId  and  saf.sapid = sqa.sapid and sqa.sessionId = ?  and saf.facultyId = ? order by createdDate desc";
		List<SessionQueryAnswerCareerservicesBean> myQueries = jdbcTemplate.query(sql,
				new Object[] { sessionQuery.getSessionId(), userId },
				new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class));
		return myQueries;
	}
	
	
	public List<SessionQueryAnswerCareerservicesBean> getQueriesForSession(
			SessionQueryAnswerCareerservicesBean sessionQuery, String facultyId , PersonCareerservicesBean user) {

		/*
		 * String sql =
		 * " select * from products.session_query_answer where  sessionId=? and sapid in "
		 * +
		 * " (select sapid from products.session_query_answer where sessionId=? and sapId not in "
		 * +
		 * " (select sapid from products.session_attendance where attended='Y' and sessionId= ?) "
		 * + " OR " +
		 * " sapId in(select sapid from products.session_attendance where attended='Y' and sessionId= ?"
		 * + " and facultyId=? )) " + " OR sapId is null and sessionId=?";
		 */
		
		ArrayList<String> parameters = new ArrayList<String>();
		String sql = " select * from products.session_query_answer sqa"
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
		
		List<SessionQueryAnswerCareerservicesBean> allQueries = new ArrayList<SessionQueryAnswerCareerservicesBean>();
		try {
			allQueries = (List<SessionQueryAnswerCareerservicesBean>)jdbcTemplate.query(sql,arg,
					new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class)
			);
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return allQueries;

	}

	public void updateAnswer(SessionQueryAnswerCareerservicesBean sessionQuery) {
		String sql = "Update products.session_query_answer" 
				+ " set isAnswered = 'Y', "
				+ " answer = ?,"
				+ " isPublic = ? ," 
				+ " lastModifiedBy = ?,"
				+ " answeredByFacultyId = ?,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";
		jdbcTemplate.update(sql, 
				new Object[] { 
					sessionQuery.getAnswer(),
					sessionQuery.getIsPublic(), 
					sessionQuery.getLastModifiedBy(),
					sessionQuery.getAnsweredByFacultyId(),
					sessionQuery.getId() 
				}
		);

	}

	public void updateSalesforceErrorMessage(SessionQueryAnswerCareerservicesBean sessionQuery) {
		String sql = "Update products.session_query_answer" 
				+ " set errorMessage = ? , "
				+ " caseId = ?,"
				+ " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";
		jdbcTemplate.update(sql, 
				new Object[] { 
					sessionQuery.getErrorMessage(),
					sessionQuery.getCaseId(),
					sessionQuery.getLastModifiedBy(),
					sessionQuery.getId() 
				}
		);

	}
	
	public void allocateFacultyToAnswer(final List<Long> recordIdList,
			final SessionQueryAnswerCareerservicesBean sqa) {
		String sql = "Update products.session_query_answer"
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

	public List<SessionQueryAnswerCareerservicesBean> getListOfSessionQueryAnswer(
			SessionQueryAnswerCareerservicesBean sqa) {
		String sql = " "
				+ "SELECT "
					+ "`sqa`.*,"
					+ "`s`.`sessionName`,"
					+ "`s`.`subject`,"
					+ "`s`.`facultyId`,"
					+ "`f`.`firstName`,"
					+ "`f`.`lastName` "
				+ "FROM "
					+ "`products`.`session_query_answer` `sqa` "
				+ "LEFT JOIN "
					+ "`products`.`sessions` `s`"
				+ "ON "
					+ "`s`.`id` = `sqa`.`sessionId` "
				+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
				+ "ON "
					+ "s.facultyId = f.facultyId "
				+ "WHERE (sqa.answer is null or sqa.answer = '')";
		return jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class));
	}

	public PageCareerservicesBean<SessionQueryAnswerCareerservicesBean> getQueries(int pageNo, int pageSize,
			SessionQueryAnswerCareerservicesBean searchBean) {
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select sqa.*,f.facultyId,f.firstName,f.lastName,f.email,s.sessionName, HOUR(TIMEDIFF(sysdate(), sqa.createdDate)) as hoursSinceQuestions, IFNULL(HOUR(TIMEDIFF(sqa.createdDate, sqa.lastModifiedDate)),0) as hoursSinceFacultyReply from acads.faculty f, products.sessions s,  products.session_query_answer sqa "
				+ "  where sqa.sessionId = s.id "
				+ " and sqa.assignedToFacultyId = f.facultyId ";

		String countSql = "select count(*) from acads.faculty f, products.sessions s,  products.session_query_answer sqa  "
				+ "  where sqa.sessionId = s.id "
				+ " and sqa.assignedToFacultyId = f.facultyId ";


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

		PaginationHelper<SessionQueryAnswerCareerservicesBean> pagingHelper = new PaginationHelper<SessionQueryAnswerCareerservicesBean>();
		PageCareerservicesBean<SessionQueryAnswerCareerservicesBean> page = new PageCareerservicesBean<SessionQueryAnswerCareerservicesBean>();
		try {
			page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args,
					pageNo, pageSize, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(
							SessionQueryAnswerCareerservicesBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}

		return page;
	}
	// Dao methods for Course Query Start
	
	public List<SessionQueryAnswerCareerservicesBean> getQueriesForCourseByStudent(String sapId, String subject) {

		String sql = "Select * from products.session_query_answer where subject = ? and sapid = ? order by createdDate desc";
		List<SessionQueryAnswerCareerservicesBean> myQueries = jdbcTemplate.query(
				sql,
				new Object[] { subject,sapId },
				new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class));
		return myQueries;
	}

	public List<SessionQueryAnswerCareerservicesBean> getPublicQueriesForCourse(
			String sapId , String subject ) {
	
		String sql = "Select * from products.session_query_answer where subject = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
		List<SessionQueryAnswerCareerservicesBean> publicQueries = jdbcTemplate.query(
				sql,
				new Object[] { subject,
						sapId }, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(
						SessionQueryAnswerCareerservicesBean.class));
		return publicQueries;
	}
	
	public List<SessionQueryAnswerCareerservicesBean> getAllQueriresBySapId(String sapId) {
		
		String sql = "Select * from products.session_query_answer where sapId = ? order by createdDate desc ";
		List<SessionQueryAnswerCareerservicesBean> allCourseQueries = jdbcTemplate.query(
				sql,
				new Object[] {sapId}, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(
						SessionQueryAnswerCareerservicesBean.class));
		return allCourseQueries;
	}
	public List<SessionQueryAnswerCareerservicesBean> getAllCourseQueriresByFaculty(String facultyId) {
		
		String sql = "Select * from products.session_query_answer where  assignedToFacultyId = ? and queryType <> 'Technical' order by createdDate desc ";
		
		List<SessionQueryAnswerCareerservicesBean> allCourseQueries = jdbcTemplate.query(
				sql,
				new Object[] {facultyId}, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(
						SessionQueryAnswerCareerservicesBean.class));
		return allCourseQueries;
	}
	

	//Get students List by criteeria given in Post query Start
	public List<SessionQueryAnswerCareerservicesBean> getAllQueriesForPostQueryReport(SessionQueryAnswerCareerservicesBean searchBean) {

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM products.session_query_answer sqa "
				+ " where sqa.queryType <> 'Technical' ";
		
		if (searchBean.getIsAnswered() != null
				&& ("Y".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and sqa.answer is not null and sqa.answer  <> '' ";
		}

		if (searchBean.getIsAnswered() != null
				&& ("N".equals(searchBean.getIsAnswered()))) {
			sql = sql + " and ( sqa.answer is null OR sqa.answer = '' )  ";
		}

		Object[] args = parameters.toArray();
		
		List<SessionQueryAnswerCareerservicesBean> allQueries= jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class));


		return allQueries;
	}

	
	
	public ExamOrderCareerservicesBean getExamOrderBeanWhereContentLive(){
		ExamOrderCareerservicesBean examOrder = null;
		try{

			String sql = "SELECT * FROM exam.examorder e where e.order = (select max(eo.order) from exam.examorder eo where eo.acadContentLive = 'Y')";

			examOrder =  (ExamOrderCareerservicesBean)jdbcTemplate.queryForObject(sql,new Object[]{}, new BeanPropertyRowMapper<ExamOrderCareerservicesBean>(ExamOrderCareerservicesBean.class));
			return examOrder;

		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}

	
//	get list of faculty 
	public List<FacultyCareerservicesBean> getFacultyForASubject (String subject){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FacultyCareerservicesBean> facultyForASubject = new ArrayList<>();
		
		String sql ="SELECT f.* FROM acads.faculty_course fc, acads.faculty f "
				+ " where fc.subject = ? "
						+ " and f.facultyId = fc.facultyId";
				
		try {
			facultyForASubject = (ArrayList<FacultyCareerservicesBean>) jdbcTemplate.query(sql, new Object[]{subject},new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return facultyForASubject;
		
	}

	

	//Dao methods for Course Query End
	
	/*
	 * added by stef on 6-Nov
	 * 
	 * public ArrayList<String> getSessionList(){
		String sql="SELECT DISTINCT sessionName from products.sessions";
		
	
		ArrayList<String> sessionList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sessionList;
	}*/
	

	public List<SessionQueryAnswerCareerservicesBean> getAllQueries() {
		String sql = ""
				+ "SELECT "
					+ "`sqa`.*, "
					+ "`s`.`sessionName`, "
					+ "`s`.`facultyId`, "
					+ "CONCAT(`students`.`firstName` , ' ',  `students`.`lastName`) `studentName`, "
					+ "CONCAT(`f`.`firstName` , ' ',  `f`.`lastName`) `facultyName` "
				+ "FROM "
					+ "`products`.`session_query_answer` `sqa` "
				+ "LEFT JOIN "
					+ "`products`.`sessions` `s` "
				+ "ON "
					+ "`s`.`id` = `sqa`.`sessionId` "
				+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
				+ "ON "
					+ "`f`.`facultyId` = `s`.`facultyId` "
				+ "LEFT JOIN "
					+ "`exam`.`students` `students` "
				+ "ON "
					+ "`students`.`sapid` = `sqa`.`sapId` ";
		List<SessionQueryAnswerCareerservicesBean> myQueries = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class)
				);
		return myQueries;
	}

}
