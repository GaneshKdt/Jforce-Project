package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.SessionAttendanceFeedbackReportBean;
import com.nmims.beans.SessionFeedback;
import com.nmims.beans.SessionFeedbackAnswer;
import com.nmims.beans.SessionFeedbackAnswers;
import com.nmims.beans.SessionFeedbackQuestion;
import com.nmims.beans.SessionFeedbackQuestionGroup;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;

public class SessionFeedbackDAO {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(SessionFeedbackDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	

	public List<SessionFeedbackQuestionGroup> getFeedbackQuestionsList(String sessionId){
		return getFeedbackQuestionsListQuery();
	}
	
	private List<SessionFeedbackQuestionGroup> getFeedbackQuestionsListQuery() {
		String sql = ""
				+ "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`session_feedback_question_groups` ";
		try {
			List<SessionFeedbackQuestionGroup> feedbackQuestionGroups = jdbcTemplate.query(
					sql, 
					new BeanPropertyRowMapper<SessionFeedbackQuestionGroup>(SessionFeedbackQuestionGroup.class));
			List<SessionFeedbackQuestionGroup> feedbackQuestionGroupsToReturn = new ArrayList<SessionFeedbackQuestionGroup>();
			
			for (SessionFeedbackQuestionGroup sessionFeedbackQuestionGroup : feedbackQuestionGroups) {
				sessionFeedbackQuestionGroup.setQuestions(getFeedbackQuestionsForGroup(sessionFeedbackQuestionGroup.getFeedbackQuestionGroupId()));
				feedbackQuestionGroupsToReturn.add(sessionFeedbackQuestionGroup);
			}
			
			return feedbackQuestionGroupsToReturn;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return new ArrayList<SessionFeedbackQuestionGroup>();
	}
		
	private List<SessionFeedbackQuestion> getFeedbackQuestionsForGroup(String groupId){
		String sql = ""
				+ "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`session_feedback_questions` "
				+ "WHERE "
				+ "`feedbackQuestionGroupId` = ?";
		try {
			List<SessionFeedbackQuestion> feedbackQuestions = jdbcTemplate.query(
					sql, 
					new Object[] { groupId },
					new BeanPropertyRowMapper<SessionFeedbackQuestion>(SessionFeedbackQuestion.class));
			return feedbackQuestions;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return new ArrayList<SessionFeedbackQuestion>();
	}
	
	public boolean addSessionFeedback(SessionFeedbackAnswers feedback) {
		
		feedback.setSessionAttendanceId(getSessionAttendanceId(feedback.getSapid(), feedback.getSessionId()));
		if(
				feedback.getAnswers() != null && 
				feedback.getAnswers().size() > 0 && 
				feedback.getSessionAttendanceId().length() > 0
			) {
			deletePreviousFeedback(feedback.getSessionAttendanceId());
			if(!insertSessionFeedback(feedback)) {
				return false;
			}
		}else {
			return false;
		}
		return true;
	}
	

	public boolean addSessionFeedbackForNotSuccessfullyViewed(SessionFeedbackAnswers feedback) {
		
		String sessionAttendanceId = getSessionAttendanceId(feedback.getSapid(), feedback.getSessionId());
		feedback.setSessionAttendanceId(sessionAttendanceId);
		
		deletePreviousFeedback(feedback.getSessionAttendanceId());
		if(updateFeedbackInAttendanceTableForNotAttended(sessionAttendanceId, feedback.getNotAttendedReason())) {
			return true;
		}
		return false;
	}
	private boolean updateFeedbackInAttendanceTableForNotAttended(String sessionAttendanceId, String reason) {
		String sql = ""
				+ "UPDATE "
				+ "`products`.`session_attendance` "
				+ "SET "
					+ "`feedbackSubmitted` = 1, "
					+ "`notAttendedReason` = ?, "
					+ "`successfullyAttended` = 0 "
				+ "WHERE "
					+ "`id` = ? ";
		try {
			jdbcTemplate.update(sql,new Object[] { reason, sessionAttendanceId });
			return true;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

	private boolean insertSessionFeedback(SessionFeedbackAnswers feedback) {
		
		for (SessionFeedbackAnswer answer : feedback.getAnswers()) {
			if(!insertFeedback(answer, feedback.getSessionAttendanceId())) {
				return false;
			}
		}
		if(!updateFeedbackInAttendanceTable(feedback.getSessionAttendanceId())) {
			return false;
		}
		return true;
	}
	
	private boolean insertFeedback(SessionFeedbackAnswer answer, String sessionFeedbackId) {
		if(answer.getValue() == null) {
			answer.setValue("");
		}if(answer.getComment() == null) {
			answer.setComment("");
		}
		String sql = ""
				+ "INSERT INTO "
				+ "`products`.`session_feedback` "
				+ "("
					+ "`feedbackQuestionId`, `sessionAttendanceId`, `value`, "
					+ "`comment` "
				+ ") "
				+ "VALUES "
				+ "("
					+ "?, ?, ?, "
					+ "?"
				+ ")";
		try {
			jdbcTemplate.update(
					sql, 
					new Object[] {
							answer.getFeedbackQuestionId(), 
							sessionFeedbackId, 
							answer.getValue(),
							answer.getComment()
					}
			);
			return true;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return false;
	}
	
	private void deletePreviousFeedback(String sessionAttendanceId) {
		String sql = ""
				+ "DELETE "
				+ "FROM "
				+ "`products`.`session_feedback` "
				+ "WHERE "
				+ "`sessionAttendanceId` = ? ";
		try {
			jdbcTemplate.update(sql,new Object[] { sessionAttendanceId });
			
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
	}

	public boolean checkIfStudentSubmittedFeedback(String sapid, String sessionId) {
		return studentSubmittedFeedback(sapid, sessionId);
	}
	private boolean studentSubmittedFeedback(String sapid, String sessionId) {
		String sql = "SELECT `feedbackSubmitted` FROM `products`.`session_attendance` WHERE `id`=? AND `sapid`=?";
		
		try {
			boolean feedbackSubmitted = jdbcTemplate.queryForObject(sql,new Object[] { sessionId, sapid }, boolean.class);
			return feedbackSubmitted;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

	private boolean updateFeedbackInAttendanceTable(String sessionAttendanceId) {
		String sql = ""
				+ "UPDATE "
				+ "`products`.`session_attendance` "
				+ "SET "
					+ "`feedbackSubmitted` = 1, "
					+ "`successfullyAttended` = 1 "
				+ "WHERE "
					+ "`id` = ? ";
		try {
			jdbcTemplate.update(sql,new Object[] { sessionAttendanceId });
			return true;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
	
	private String getSessionAttendanceId(String sapid, String sessionId) {
		String sql = ""
				+ "SELECT "
				+ "`id` "
				+ "FROM "
				+ "`products`.`session_attendance` "
				+ "WHERE "
					+ "sapid=? "
					+ "AND "
					+ "sessionId=? "
				+ "LIMIT 1";
		try {
			String attendanceId = jdbcTemplate.queryForObject(
					sql,
					new Object[] {
							sapid,
							sessionId
					},
					String.class);
			return attendanceId;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return "";
	}
	
	public List<String> getListOfSessionsWithoutFeedback(String sapid) {
		String sql = "SELECT `sessionId` FROM `products`.`session_attendance` WHERE `sapid` = ? AND `feedbackSubmitted` = 0";
		
		try {
			List<String> listOfSessions = jdbcTemplate.query(sql, new Object[] { sapid }, new RowMapper<String>(){
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				   return rs.getString(1);
				}
			});
			
			return listOfSessions;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return new ArrayList<String>();
	}
	

	public int numberOfFeedbackRequired(String sapid) {
		String sql = "SELECT count(*) FROM `products`.`session_attendance` WHERE `sapid` = ? AND `feedbackSubmitted` = 0";
		
		try {
			int numberOfSessions = jdbcTemplate.queryForObject(sql, new Object[] { sapid }, Integer.class);
			
			return numberOfSessions;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return 0;
	}

	public FacultyCareerservicesBean getFacultyDetails(String sapid) {
		String sql = "SELECT `sessionId` FROM `products`.`session_attendance` WHERE `sapid` = ? AND `feedbackSubmitted` = 0";
		
		try {
			FacultyCareerservicesBean facultyDetails = jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
			
			return facultyDetails;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return new FacultyCareerservicesBean();
	}
	


	public List<SessionFeedbackQuestionGroup> getFeedbackGroups() {
		String sql = "SELECT * FROM products.session_feedback_question_groups";
		
		try {
			List<SessionFeedbackQuestionGroup> groups = jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionFeedbackQuestionGroup>(SessionFeedbackQuestionGroup.class));
			
			return groups;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}

	public List<SessionFeedback> getAllFeedback() {
		String sql = ""
				+ "SELECT "
					+ "`s`.`id`, "
					+ "count(`sa`.`sessionId`) AS `numberOfFeedback` "
					+ "FROM "
					+ "`products`.`sessions` `s` "
					+ "LEFT JOIN "
					+ "("
						+ "SELECT * FROM "
						+ "`products`.`session_attendance` `sat` "
						+ "LEFT JOIN "
						+ "`products`.`session_feedback` `sf` "
						+ "ON "
						+ "`sat`.`id` = `sf`.`sessionAttendanceId` "
						+ "GROUP BY `sessionId`"
					+ ") `sa`"
					+ "ON "
					+ "`sa`.`sessionId` = `s`.`id` "
				+ "GROUP BY "
					+ "`s`.`id` ";
		
		try {
			List<SessionFeedback> feedbacks = jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionFeedback>(SessionFeedback.class));
			
			return feedbacks;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}
	
	public boolean addFeedbackQuestion(SessionFeedbackQuestion feedbackQuestion) {
		String sql = ""
				+ "INSERT INTO `products`.`session_feedback_questions`"
				+ "("
					+ "`feedbackQuestionGroupId`,"
					+ "`questionString`,"
					+ "`questionType`,"
					+ "`giveAdditionalComment`)"
				+ "VALUES"
				+ "("
					+ "?,"
					+ "?,"
					+ "?,"
					+ "?"
				+ ")";
		
		try {
			jdbcTemplate.update(sql, new Object[] {
					feedbackQuestion.getFeedbackQuestionGroupId(), feedbackQuestion.getQuestionString(), feedbackQuestion.getQuestionType(), 
					feedbackQuestion.isGiveAdditionalComment()
			});
			
			return true;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

	public List<SessionFeedbackQuestion> getAllFeedbackQuestions(){
		return getAllFeedbackQuestionsQuery();
	}
	
	private List<SessionFeedbackQuestion> getAllFeedbackQuestionsQuery() {
		String sql = ""
				+ "SELECT "
					+ "*, "
					+ "`groupName` AS `feedbackQuestionGroupName` "
				+ "FROM "
				+ "`products`.`session_feedback_questions` `q` "
					+ "LEFT JOIN "
					+ "`products`.`session_feedback_question_groups` `qg` "
					+ "ON "
					+ "`q`.`feedbackQuestionGroupId` = `qg`.`feedbackQuestionGroupId`";
		try {
			List<SessionFeedbackQuestion> feedbackQuestions = jdbcTemplate.query(
					sql, 
					new BeanPropertyRowMapper<SessionFeedbackQuestion>(SessionFeedbackQuestion.class));
			return feedbackQuestions;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return new ArrayList<SessionFeedbackQuestion>();
	}
	
	public boolean deleteFeedbackQuestion(String feedbackQuestionId) {
		String sql = "DELETE FROM `products`.`session_feedback_questions` WHERE `feedbackQuestionId` = ?";
		
		try {
			jdbcTemplate.update(sql, new Object[] {
					feedbackQuestionId
			});
			
			return true;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}


	public List<SessionAttendanceFeedbackReportBean> getAllSessionAndFeedback() {
		String sql = ""
				+ "SELECT "
					+ "CONCAT(`students`.`firstName` , ' ',  `students`.`lastName`) `studentName`, "
					+ "`sa`.*, "
					+ "`feedback`.*, "
					+ "`sp`.*, "
					+ "`s`.* "
				+ "FROM "
				+ "`products`.`session_attendance` `sa` "
					+ "LEFT JOIN "
					+ "("
						+ "SELECT "
						+ "`sf`.`sessionAttendanceId`, "
						+ "`sfqg`.`groupName` AS `questionType`,"
						+ "`sfq`.`questionString` AS `question`, "
						+ "`sf`.`value` AS `answer`,  "
						+ "`sfq`.`questionString` AS `additionalCommentRequired`, "
						+ "`sf`.`comment` AS `additionalComment`"
						//get feedback
						+ "FROM `products`.`session_feedback` `sf` "
						//get question
						+ "LEFT JOIN "
						+ "`products`.`session_feedback_questions` `sfq` "
						+ "ON "
						+ "`sf`.`feedbackQuestionId` = `sfq`.`feedbackQuestionId` "
						//get question type
						+ "LEFT JOIN "
						+ "`products`.`session_feedback_question_groups` `sfqg` "
						+ "ON "
						+ "`sfqg`.`feedbackQuestionGroupId` = `sfq`.`feedbackQuestionGroupId`"
					+ ") `feedback` "
					+ "ON `feedback`.`sessionAttendanceId` = `sa`.`id` "
	
					//Get the session details
						+ "LEFT JOIN "
						+ "`products`.`sessions` `s`"
						+ "ON "
						+ "`sa`.`sessionId` = `s`.`id`"
	
					//Get the student name
						+ "LEFT JOIN "
						+ "`exam`.`students` `students`"
						+ "ON "
						+ "`sa`.`sapid` = `students`.`sapid`"
	
					//Get the package name and details
						+ "LEFT JOIN "
						+ "`products`.`student_packages` `sp`"
						+ "ON "
						+ "`sp`.`paymentId` = `sa`.`purchaseId`"
						+ "LEFT JOIN "
						+ "`products`.`packages` `p`"
						+ "ON "
						+ "`sp`.`salesForceUID` = `p`.`salesForceUID` ";
		
		List<SessionAttendanceFeedbackReportBean> myQueries = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<SessionAttendanceFeedbackReportBean>(
				SessionAttendanceFeedbackReportBean.class
			)
		);
		return myQueries;
	}
}
