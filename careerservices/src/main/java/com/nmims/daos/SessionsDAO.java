package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.nmims.beans.MailCareerservicesBean;
import com.nmims.beans.SessionAttendance;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;

public class SessionsDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(SessionsDAO.class);
 
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	public SessionQueryAnswerCareerservicesBean findSessionQueryAnswerById(String id) {
		String sql = "SELECT * FROM products.session_query_answer where id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionQueryAnswerCareerservicesBean sessionQuery = (SessionQueryAnswerCareerservicesBean) jdbcTemplate.queryForObject(
				sql, new Object[] { id }, new BeanPropertyRowMapper<SessionQueryAnswerCareerservicesBean>(SessionQueryAnswerCareerservicesBean.class));

		return sessionQuery;
	}
	
	public int updateSessionQueryAnsById(String id , String answer) {
		String sql = "Update products.session_query_answer  set answer = ? , isAnswered ='Y' where id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		int result = jdbcTemplate.update(sql, new Object[]{answer,id});
		return result;
	}

	public SessionDayTimeBean findScheduledSessionById(String id) {
		String sql = ""
				+ "SELECT "
					+ "concat(`f`.`firstName` , ' ',  `f`.`lastName`) `facultyName`, "
					+ "`s`.`date` AS `sessionDate`, "
					+ "`s`.*,"
					+ "`vc`.`thumbnailUrl` "
				+ "FROM "
				+ "`products`.`sessions` `s` "
					+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
					+ "ON "
					+ "`f`.`facultyId` = `s`.`facultyId` "
				
					+ "LEFT JOIN "
					+ "`products`.`video_content` `vc` "
					+ "ON "
					+ "`vc`.`sessionId` = `s`.`id` "
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

	public List<SessionDayTimeBean> findAllScheduledSessions() {
		String sql = ""
				+ "SELECT "
					+ "concat(`f`.`firstName` , ' ',  `f`.`lastName`) `facultyName`, "
					+ "`s`.`date` AS `sessionDate`, "
					+ "`s`.*,"
					+ "`vc`.`thumbnailUrl` "
				+ "FROM "
				+ "`products`.`sessions` `s` "
					+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
					+ "ON "
					+ "`f`.`facultyId` = `s`.`facultyId` "
				
					+ "LEFT JOIN "
					+ "`products`.`video_content` `vc` "
					+ "ON "
					+ "`vc`.`sessionId` = `s`.`id` ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SessionDayTimeBean> sessions = null;
		try {
			sessions = jdbcTemplate.query(
					sql, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return sessions;
	}
	

	public List<SessionDayTimeBean> findScheduledSessionsByFacultyId(String facultyId) {
		String sql = ""
				+ "SELECT "
					+ "concat(`f`.`firstName` , ' ',  `f`.`lastName`) `facultyName`, "
					+ "`s`.`date` AS `sessionDate`, "
					+ "`s`.*,"
					+ "`vc`.`thumbnailUrl` "
				+ "FROM "
				+ "`products`.`sessions` `s` "
					+ "LEFT JOIN "
					+ "`acads`.`faculty` `f` "
					+ "ON "
					+ "`f`.`facultyId` = `s`.`facultyId` "
				
					+ "LEFT JOIN "
					+ "`products`.`video_content` `vc` "
					+ "ON "
					+ "`vc`.`sessionId` = `s`.`id` "
				+ "WHERE "
					+ "`s`.`facultyId` = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SessionDayTimeBean> sessions = null;
		try {
			sessions = jdbcTemplate.query(
					sql, new Object[] { facultyId }, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}

		return sessions;
	}
	

	public StudentCareerservicesBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentCareerservicesBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			student = (StudentCareerservicesBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
		return student;
	}
	

	public SessionAttendance checkSessionAttendance(String userId, String id) {
		String sql = "select * from products.session_attendance where attended = 'Y' and sessionId = ? and sapId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			SessionAttendance attendance = (SessionAttendance) jdbcTemplate.queryForObject(sql, new Object[]{id, userId},new BeanPropertyRowMapper<SessionAttendance>(SessionAttendance.class));
			return attendance;
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}

	}
	
	public void updateCancelledSession(SessionDayTimeBean session,String userId) {
		
		String sql = "Update products.sessions set date = ? , startTime = ? , endTime = ? ,isCancelled = ? , reasonForCancellation = ?, cancellationSMSBody = ?,cancellationEmailBody = ? , cancellationSubject = ? , lastModifiedBy = ? ,lastModifiedDate = sysdate() "
				    +" where id = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {session.getDate(),session.getStartTime(),session.getEndTime(),session.getIsCancelled(),session.getReasonForCancellation(),session.getCancellationSMSBody(),session.getCancellationEmailBody(),session.getCancellationSubject(),userId,session.getId() });
	}

	public void createAnnouncement(SessionDayTimeBean session, String userId) {
		String sql = "INSERT INTO portal.announcements "
				+ "(subject,"
				+ "description,"
				+ "startDate,"
				+ "endDate,"
				+ "active, "
				+ "category,"
				+ "createdBy,"
				+ "createdDate)"
				+ "VALUES ( ?,?,?,?,?,'Career Services',?,sysdate())";

		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				session.getCancellationSubject(),session.getCancellationEmailBody(),session.getStartDate(),session.getEndDate(),session.getIsCancelled(),userId
		});
	}
	

	public long insertMailRecord(final MailCareerservicesBean successfullMailList, final String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,body,fromEmailId) VALUES(?,?,sysdate(),?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, successfullMailList.getSubject());
				statement.setString(2, userId);
				statement.setString(3,successfullMailList.getBody());
				statement.setString(4,successfullMailList.getFromEmailId());
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}

	@SuppressWarnings("unused")
	public void insertUserMailRecord(MailCareerservicesBean mailBean, String userId, String fromEmailID,
			long insertedMailId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId) VALUES(?,?,sysdate(),?,?,?) ";
		try{
			int batchUpdateDocumentRecordsResultSize = jdbcTemplate.update(sql, new Object[]{
					StringUtils.join(mailBean.getSapIdRecipients(),","),
					StringUtils.join(mailBean.getMailIdRecipients(),","),
					userId,
					fromEmailID,
					String.valueOf(insertedMailId)
			});
			
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
		
	}
	
	
	public List<StudentCareerservicesBean> getAllStudentsForSessionType(int featureId) {
		String sql = ""
				+ "SELECT "
			    + " * "
			    + "FROM "
			    + "`products`.`package_features` `pf` "
			    + "LEFT JOIN "
                    + "`products`.`entitlements_info` `ei` "
                    + "ON "
                    + "`pf`.`uid` = `ei`.`packageFeaturesId` "
			    + "LEFT JOIN "
                    + "`products`.`entitlements_student_data` `esd` "
                    + "ON "
                    + "`esd`.`entitlementId` = `ei`.`entitlementId` "
			    + "LEFT JOIN "
                    + "`exam`.`students` `students` "
                    + "ON "
                    + "`esd`.`sapid` = `students`.`sapid` "
			    + "WHERE "
			    + "`activationsLeft` > 0 "
			    + "AND "
			    + "`ended` = 0 "
			    + "AND "
			    + "`activated` = 1 "
			    + "AND "
			    + "`featureId` = ? ";

	    try {
			List<StudentCareerservicesBean> listOfStudents = jdbcTemplate.query(
			    sql, 
			    new Object[] { featureId }, 
			    new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));
			return listOfStudents;
	    }catch (Exception e) {
			return new ArrayList<StudentCareerservicesBean>();
		}
	}


	public long createReScheduleSession(final SessionDayTimeBean session, final String userId)
	{
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		
		final StringBuffer sql = new StringBuffer(" "
				+ "INSERT INTO "
				+ "acads.sessions "
				+ "( "
				+ "date, startTime , endTime , "
				+ "day, sessionName, "
				+ "createdBy, createdDate, "
				+ "facultyId , meetingKey , "
				+ "hostId, hostPassword , room"
				+ ") "
				+ "VALUES "
				+ "( "
				+ "?,				?,			?,"
				+ "DAYNAME(date),	?, "
				+ "?, 				sysdate(), 	"
				+ "?, 				?,"
				+ "?, 				?,"
				+ "?,				?, 			?, "
				+ ") ");
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				statement.setString(1,session.getDate());
				statement.setString(2,session.getStartTime());
				statement.setString(3,session.getEndTime());
				statement.setString(4,session.getSessionName());
				statement.setString(5,userId);
				statement.setString(6,session.getFacultyId());
				statement.setString(7,session.getMeetingKey());
				statement.setString(8,session.getHostId());
				statement.setString(9,session.getHostPassword());
				statement.setString(10,session.getRoom());
				return statement;
			}
		},holder);
		
		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}

}
