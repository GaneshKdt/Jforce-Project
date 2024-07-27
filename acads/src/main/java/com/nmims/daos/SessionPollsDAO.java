package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionPollReportBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsQuestionsBean;
import com.nmims.beans.WebinarPollsResultsBean;
import com.nmims.beans.WebinarPollsResultsQuestionDetailsBean;
import com.nmims.beans.WebinarPollsResultsQuestionsBean;
import com.nmims.helpers.PaginationHelper;

public class SessionPollsDAO extends BaseDAO {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	private NamedParameterJdbcTemplate nameJdbcTemplate;

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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int saveSessionPolls(final WebinarPollsBean webinarPollsBean) {
		
		
		String sql = "INSERT INTO acads.session_polls VALUES(?,?,?,?,sysdate(),?,sysdate())";
		jdbcTemplate.update(sql,new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                preparedStatement.setString(1, webinarPollsBean.getId() );
	                preparedStatement.setString(2, webinarPollsBean.getWebinarId());
	                preparedStatement.setString(3, webinarPollsBean.getTitle() );
	                preparedStatement.setString(4, webinarPollsBean.getCreatedBy());
	                preparedStatement.setString(5, webinarPollsBean.getLastModifiedBy() );
	            }});
		

		for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
			String questionSql = "INSERT INTO acads.session_polls_questions(pollId, name, type, "
					+ "answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9, answer10, "
					+ "createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";

			/*jdbcTemplate.update(questionSql,
					new Object[] { webinarPollsBean.getId(), webinarPollsQuestionsBean.getName(),
							webinarPollsQuestionsBean.getType(), webinarPollsQuestionsBean.getAnswer1(),
							webinarPollsQuestionsBean.getAnswer2(), webinarPollsQuestionsBean.getAnswer3(),
							webinarPollsQuestionsBean.getAnswer4(), webinarPollsQuestionsBean.getAnswer5(),
							webinarPollsQuestionsBean.getAnswer6(), webinarPollsQuestionsBean.getAnswer7(),
							webinarPollsQuestionsBean.getAnswer8(), webinarPollsQuestionsBean.getAnswer9(),
							webinarPollsQuestionsBean.getAnswer10(), webinarPollsQuestionsBean.getCreatedBy(),
							webinarPollsQuestionsBean.getLastModifiedBy() });*/
			PreparedStatementCreator psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(questionSql,  Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, webinarPollsBean.getId());
					ps.setString(2, webinarPollsQuestionsBean.getName());
					ps.setString(3, webinarPollsQuestionsBean.getType());
					ps.setString(4, webinarPollsQuestionsBean.getAnswer1());
					ps.setString(5, webinarPollsQuestionsBean.getAnswer2());
					ps.setString(6, webinarPollsQuestionsBean.getAnswer3());
					ps.setString(7, webinarPollsQuestionsBean.getAnswer4());
					ps.setString(8, webinarPollsQuestionsBean.getAnswer5());
					ps.setString(9, webinarPollsQuestionsBean.getAnswer6());
					ps.setString(10, webinarPollsQuestionsBean.getAnswer7());
					ps.setString(11, webinarPollsQuestionsBean.getAnswer8());
					ps.setString(12, webinarPollsQuestionsBean.getAnswer9());
					ps.setString(13, webinarPollsQuestionsBean.getAnswer10());
					ps.setString(14, webinarPollsQuestionsBean.getCreatedBy());
					ps.setString(15, webinarPollsQuestionsBean.getLastModifiedBy());

					return ps;
				}
			};
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(psc, keyHolder);
			}
		
		

		return 1;
		
		
	}

	public int updateSessionPolls(final WebinarPollsBean webinarPollsBean) {
		
		try{
		String sql = "UPDATE acads.session_polls set title=?, lastModifiedDate=sysdate() where id=?";
		jdbcTemplate.update(sql,  new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                preparedStatement.setString(1, webinarPollsBean.getTitle());
	                preparedStatement.setString(2, webinarPollsBean.getId() );
	            }});
		
		
		String deleteSql = "DELETE FROM acads.session_polls_questions where pollId=?";
		jdbcTemplate.update(deleteSql,  new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                preparedStatement.setString(1, webinarPollsBean.getId() );
	            }});

		for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
			String questionSql = "INSERT INTO acads.session_polls_questions(pollId, name, type, "
					+ "answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9, answer10, "
					+ "createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";

			jdbcTemplate.update(questionSql,
					new PreparedStatementSetter() {
		           public void setValues(PreparedStatement ps) throws SQLException {
		        	   ps.setString(1, webinarPollsBean.getId());
						ps.setString(2, webinarPollsQuestionsBean.getName());
						ps.setString(3, webinarPollsQuestionsBean.getType());
						ps.setString(4, webinarPollsQuestionsBean.getAnswer1());
						ps.setString(5, webinarPollsQuestionsBean.getAnswer2());
						ps.setString(6, webinarPollsQuestionsBean.getAnswer3());
						ps.setString(7, webinarPollsQuestionsBean.getAnswer4());
						ps.setString(8, webinarPollsQuestionsBean.getAnswer5());
						ps.setString(9, webinarPollsQuestionsBean.getAnswer6());
						ps.setString(10, webinarPollsQuestionsBean.getAnswer7());
						ps.setString(11, webinarPollsQuestionsBean.getAnswer8());
						ps.setString(12, webinarPollsQuestionsBean.getAnswer9());
						ps.setString(13, webinarPollsQuestionsBean.getAnswer10());
						ps.setString(14, webinarPollsQuestionsBean.getCreatedBy());
						ps.setString(15, webinarPollsQuestionsBean.getLastModifiedBy());
		            }});
			
			}
		return 1;
		}catch(Exception e)
		{
			  
			return 0;
		}
		
	}

	public List<WebinarPollsBean> getSessionPolls(String webinarId) {
		String sql = "Select * from acads.session_polls where webinarId=? order by createdDate asc ";
		List<WebinarPollsBean> sessionPolls = (List<WebinarPollsBean>) jdbcTemplate.query(sql,
				new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                preparedStatement.setString(1, webinarId );
	            }}, new BeanPropertyRowMapper(WebinarPollsBean.class));
		return sessionPolls;
	}

	public List<WebinarPollsQuestionsBean> getSessionPollsQuestions(String pollId) {
		String sql = "Select * from acads.session_polls_questions where pollId=? order by id asc";
		List<WebinarPollsQuestionsBean> sessionPollsQuestions = (List<WebinarPollsQuestionsBean>) jdbcTemplate
				.query(sql, new PreparedStatementSetter() {
			           public void setValues(PreparedStatement preparedStatement) throws SQLException {
			                preparedStatement.setString(1,pollId );
			            }}, new BeanPropertyRowMapper(WebinarPollsQuestionsBean.class));
		return sessionPollsQuestions;
	}

	public void deleteSessionPolls(String webinarId, String pollId) {
		String questionsSql = "Delete from acads.session_polls_questions where pollId = ?";
		jdbcTemplate.update(questionsSql, new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                preparedStatement.setString(1, pollId );
	            }});

		String sql = "Delete from acads.session_polls where webinarId=? and id=?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	        	   preparedStatement.setString(1, webinarId );
	                preparedStatement.setString(2, pollId );
	            }});
	}

	public void saveSessionPollsResults(final WebinarPollsResultsBean webinarPollsResultsBean) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		
	
		// delete old data
		String deleteResultsQuestionsDetailsSql = "DELETE FROM acads.session_polls_results_questions_details "
				+ "where session_polls_results_questions_id in (select id from acads.session_polls_results_questions where webinarId=?)";
		jdbcTemplate.update(deleteResultsQuestionsDetailsSql, new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	        	   preparedStatement.setString(1, webinarPollsResultsBean.getId());
	               
	            }});
		
		String deleteResultsQuestionsSql = "DELETE FROM acads.session_polls_results_questions where webinarId=?";
		jdbcTemplate.update(deleteResultsQuestionsSql,new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	        	   preparedStatement.setString(1, webinarPollsResultsBean.getId());
	               
	            }});

		String deleteResultsSql = "DELETE FROM acads.session_polls_results where webinarId=?";
		jdbcTemplate.update(deleteResultsSql, new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	        	   preparedStatement.setString(1, webinarPollsResultsBean.getId());
	               
	            }});

		// insert upateded data
		String resultsSql = "INSERT INTO acads.session_polls_results(webinarId,uuid,start_time,title,createdBy,createdDate,lastModifiedBy,lastModifiedDate) VALUES(?,?,?,?,?,sysdate(),?,sysdate())";
		jdbcTemplate.update(resultsSql,
				new PreparedStatementSetter() {
	           public void setValues(PreparedStatement preparedStatement) throws SQLException {
	        	   preparedStatement.setString(1, webinarPollsResultsBean.getId());
	        	   preparedStatement.setString(2, webinarPollsResultsBean.getUuid());
	        	   preparedStatement.setString(3,webinarPollsResultsBean.getStart_time());
	        	   preparedStatement.setString(4,webinarPollsResultsBean.getTitle());
	        	   preparedStatement.setString(5, webinarPollsResultsBean.getCreatedBy());
	        	   preparedStatement.setString(6,  webinarPollsResultsBean.getLastModifiedBy());
	               
	            }});
		

		for (final WebinarPollsResultsQuestionsBean webinarPollsResultsQuestionsBean : webinarPollsResultsBean
				.getQuestions()) {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(
							"INSERT INTO acads.session_polls_results_questions(webinarId, name, email, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES(?,?,?,?,sysdate(),?,sysdate())",
							Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, webinarPollsResultsQuestionsBean.getWebinarId());
					statement.setString(2, webinarPollsResultsQuestionsBean.getName());
					statement.setString(3, webinarPollsResultsQuestionsBean.getEmail());
					statement.setString(4, webinarPollsResultsQuestionsBean.getCreatedBy());
					statement.setString(5, webinarPollsResultsQuestionsBean.getLastModifiedBy());
					return statement;
				}
			}, holder);

			long primaryKey = holder.getKey().longValue();

			for (WebinarPollsResultsQuestionDetailsBean webinarPollsResultsQuestionDetailsBean : webinarPollsResultsQuestionsBean
					.getQuestion_details()) {
				String resultsQuestionsDetailsSql = "INSERT INTO acads.session_polls_results_questions_details"
						+ "(session_polls_results_questions_id, question, answer,polling_id,pollName,createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
						+ "VALUES(?,?,?,?,?,?,sysdate(),?,sysdate())";
				jdbcTemplate.update(resultsQuestionsDetailsSql,
						new PreparedStatementSetter() {
			           public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        	   preparedStatement.setLong(1, primaryKey);
			        	   preparedStatement.setString(2, webinarPollsResultsQuestionDetailsBean.getQuestion());
			        	   preparedStatement.setString(3,webinarPollsResultsQuestionDetailsBean.getAnswer());
			        	   preparedStatement.setString(4,webinarPollsResultsQuestionDetailsBean.getPolling_id());
			        	   preparedStatement.setString(5, webinarPollsResultsQuestionDetailsBean.getPollName());
			        	   preparedStatement.setString(6,  webinarPollsResultsQuestionDetailsBean.getCreatedBy());
			        	   preparedStatement.setString(7,  webinarPollsResultsQuestionDetailsBean.getLastModifiedBy() );
			               
			            }});
			}
		}
		
	}
	
	//Find Title of Particular Poll id
	public String getNameOfPollId(String pollid)
	{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String name = "";
		String sql="SELECT title FROM acads.session_polls WHERE id= ?";
		try{
		 name =  jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
  				public void setValues(PreparedStatement preparedStatement) throws SQLException {
  					preparedStatement.setString(1,pollid);
  				}
  				},new ResultSetExtractor<String>() {
  				@Override
  				public String extractData(ResultSet rs) throws SQLException, DataAccessException {
  					if (rs.next()) {
  	                      return rs.getString(1);
  	                  }
  					return null;
  				}
  	            });
		}catch(Exception e){
			  
		}
		return name;
	}
	
	
	

	//Find the Poll report for the data entered in the view Page
		public PageAcads<SessionPollReportBean> getSessionPollReport(int pageNo, int pageSize, SessionPollReportBean searchBean) 
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			//Find the polls
			StringBuffer sql= new StringBuffer("SELECT "
					+ "s.id as sessionId,  sp.*,sp.createdBy as facultyIdPoll, "
					+ "	s.*, "
					+ "	CONCAT(f.firstName, '', f.lastName) AS facultyName, "
					//+ " mdms.subjectcode, "
					+ "case "
					+ "when exists "
					+ "(select * from acads.session_polls_results_questions_details sprqd "
					+ "where sprqd.polling_id = sp.id) "
					+ "then 'Y' "
					+ "else 'N' "
					+ "end as "
					+ "isLaunched ,count(spq.pollId) as noofQuest "
					+ " FROM "
					+ " acads.sessions s "
					+ " INNER JOIN "
					+ " acads.session_polls sp ON s.meetingKey = sp.webinarId "
					+ " INNER JOIN "
					+ " acads.faculty AS f ON sp.createdBy = f.facultyId "
					+ "inner join "
					+ "acads.session_polls_questions as spq  on sp.id = spq.pollId "
					/*+ " INNER JOIN "
					+ " acads.session_subject_mapping sesm ON s.id = sesm.sessionId "
					+ " INNER JOIN "
					+ " exam.mdm_subjectcode AS mdms ON sesm.subjectCodeId = mdms.id "11*/
					+ " WHERE 1 = 1 ");
			
			
			
			StringBuffer countsql=new StringBuffer( "select count(*) from ( select count(*) from acads.sessions s INNER JOIN "
							+ " acads.session_polls sp on s.meetingKey = sp.webinarId "
					        +"  INNER JOIN  acads.faculty AS f ON sp.createdBy = f.facultyId  "
					        + "  inner join acads.session_polls_questions as spq  on sp.id = spq.pollId "
							+ " where 1= 1 ");
			
			if(!StringUtils.isBlank(searchBean.getSubject()))
			{
				sql.append(" and s.subject  IN ("+searchBean.getSubject()+") ");
				countsql.append(" and s.subject IN ("+searchBean.getSubject()+") ");
						
			
			}
			if(!StringUtils.isBlank(searchBean.getDate()))
			{
				sql.append(" and s.date = ? ");
				countsql.append(" and s.date=? ");
						
				parameters.add(searchBean.getDate());
			}
			if(!StringUtils.isBlank(searchBean.getMonth()))
			{
				sql.append(" and s.month=? ");
				countsql.append(" and s.month=? ");
						
				parameters.add(searchBean.getMonth());
			}
			if(!StringUtils.isBlank(searchBean.getYear()))
			{
				sql.append(" and s.year=? ");
				countsql.append(" and s.year=? ");
						
				parameters.add(searchBean.getYear());
			}
			if(!StringUtils.isBlank(searchBean.getFacultyId()))
			{
				sql.append(" and s.facultyId=?  ");
				countsql.append(" and s.facultyId=?  ");
						
				parameters.add(searchBean.getFacultyId());
				
			}
			
			sql.append("group by(sp.id) ");
			countsql.append(" group by(sp.id) ) t;");
			
			Object[] args = parameters.toArray();
				
			PaginationHelper<SessionPollReportBean> pagingHelper = new PaginationHelper<SessionPollReportBean>();
			PageAcads<SessionPollReportBean> page =  pagingHelper.fetchPage(jdbcTemplate,countsql.toString(),sql.toString(), args, pageNo, pageSize, 
						new BeanPropertyRowMapper(SessionPollReportBean.class));
				
					
			for(int i =0;i<page.getRowCount();i++){
				
				String sql1 = "select distinct(mdms.subjectcode) "
						+ "from "
						+ "acads.session_subject_mapping sesm "
						+ "INNER JOIN "
						+ "exam.mdm_subjectcode AS mdms ON sesm.subjectCodeId = mdms.id "
						+ "where sesm.sessionId =  "+page.getPageItems().get(i).getSessionId();
			
				List<String> subjectcode = jdbcTemplate.queryForList(sql1,String.class);
				String listString = String.join(", ", subjectcode);
			
				page.getPageItems().get(i).setSubjectcode(listString);
			}
			return page;
				
		}

	
		@Transactional(readOnly = true)
	    public ArrayList<String> getSubjectsCodeInCurrent(String userId,String month,String year)
	    {
	    	ArrayList<String> subjectcodeList  = new ArrayList<String>();
	    	StringBuffer sql = new StringBuffer();
	    		
	    	nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	
		    	 sql = sql.append(" SELECT distinct(s.subject) FROM "
		    			+ " acads.sessions s where s.month =:month and s.year =:year   ");
		 
	    	try {
	    		MapSqlParameterSource parameters = new MapSqlParameterSource();
	    		
	    		if(!StringUtils.isBlank(userId) && !userId.equals("NMSCEMUADMIN01"))
	    		{
	    			sql.append(" AND  s.facultyId =:userId ");
	    			parameters.addValue("userId", userId);
	    		}
	    		
	    		parameters.addValue("month", month);
	    		parameters.addValue("year", year);
	    	subjectcodeList = (ArrayList<String>) nameJdbcTemplate.query(sql.toString(),parameters,new SingleColumnRowMapper(String.class));
	    	}catch(Exception e) {
	    		  
	    	}
	    	return subjectcodeList;
	    }
	
	
		@Transactional
		public HashMap<String,String> getTheSapidsFromEmail(String meetingkey) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			 HashMap<String,String>	sapids = new HashMap<String,String>();

				String sql1 = "SELECT sapid FROM acads.session_attendance_feedback where meetingKey = ?  ";
				
		 
				ArrayList<String> sapid = (ArrayList<String>) jdbcTemplate.query(sql1,new Object[] {meetingkey},new SingleColumnRowMapper(String.class));
				
				if(sapid.size() > 0) {
				 String str = String.join("\",\"", sapid);
				 
				 sapids = 	jdbcTemplate.query("select distinct emailId,sapid from exam.students where sapid IN ( \""+str+"\")", (ResultSet rs) -> {
				    HashMap<String,String> results = new HashMap<>();
				    while (rs.next()) {
				        results.put(rs.getString("emailId"), rs.getString("sapid"));
				    }
				    return results;
				});
				}
			
			
			return sapids;
		}
		
}	

