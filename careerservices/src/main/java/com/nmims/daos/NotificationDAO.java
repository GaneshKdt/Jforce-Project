package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.nmims.beans.MailCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;


public class NotificationDAO {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(NotificationDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ArrayList<SessionDayTimeBean> getScheduledSessionForDayForEmail() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		//Get sessions scheduled to start in next 2 hours next day
		String sql = "select  *, date_format(date,'%d-%b-%Y') date from products.sessions "
				+ " where date = DATE_ADD(curdate(), INTERVAL 1 day)  "
				+ " and timediff(starttime, time(sysdate())) > '00:00:00' "
				+ " and  timediff(starttime, time(sysdate())) < '02:00:00'  "
				+ " and (emailSent is null or emailSent <> 'Y') "
				+ " and (isCancelled <> 'Y' or isCancelled is null) ";

		ArrayList<SessionDayTimeBean> scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		return scheduledSessionList;
	}
	
	public ArrayList<StudentCareerservicesBean> getRegisteredStudentForSubject(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<Object> parameters = new ArrayList<>();
		// Query from new CS registration table
		String sql =  ""
				+ "SELECT "
					+ "`students`.* "
				+ "FROM "
					+ "	`products`.`package_features` `pf` "
				+ "LEFT JOIN "
					+ "	`products`.`entitlements_info` `ei` "
				+ "ON "
					+ "	`pf`.`uid` = `ei`.`packageFeaturesId` "
				+ "LEFT JOIN "
					+ "	`products`.`entitlements_student_data` `esd` "
				+ "ON  "
					+ "	`esd`.`entitlementId` = `ei`.`entitlementId` "
				+ "LEFT JOIN "
					+ "	`exam`.`students` `students` "
				+ "ON "
					+ "	`esd`.`sapid` = `students`.`sapid` "
				+ "LEFT JOIN "
					+ "	`products`.`features` `f` "
				+ "ON "
					+ "	`pf`.`featureId` = `f`.`featureId` "
				+ "WHERE "
					+ "	`activationsLeft` > 0 "
				+ "AND  "
					+ "	`ended` = 0 "
				+ "AND  "
					+ "	`activated` = 1 "
				+ "AND "
					+ "	`f`.`featureName` = 'Career Forum'";

		Object [] arg = parameters.toArray();

		ArrayList<StudentCareerservicesBean> studentList = (ArrayList<StudentCareerservicesBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));
		
		return studentList;
	}
	
	public long insertMailRecord(final ArrayList<MailCareerservicesBean> mailList,final String fromUserId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, mailList.get(0).getSubject());
		        statement.setString(2, fromUserId);
		        statement.setString(3,mailList.get(0).getFilterCriteria());
		        statement.setString(4,mailList.get(0).getBody());
		        statement.setString(5,mailList.get(0).getFromEmailId());
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
		
	}
	
	public void insertUserMailRecord(final ArrayList<MailCareerservicesBean> mailList, final String fromUserId,final String fromEmailId,final long mailTemplateId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ";
		try{
//			int[] batchUpdateDocumentRecordsResultSize = 
					jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MailCareerservicesBean mailBean = mailList.get(i);

					ps.setString(1,StringUtils.join(mailBean.getSapIdRecipients(),","));
					ps.setString(2,StringUtils.join(mailBean.getMailIdRecipients(),","));
					ps.setString(3,fromUserId);
					ps.setString(4,fromEmailId);
					ps.setString(5,String.valueOf(mailTemplateId));
					ps.setString(6, fromUserId);
					
				}

				@Override
				public int getBatchSize() {
					return mailList.size();
				}
			  });
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
	}
	
	public void updateEmailStatus(SessionDayTimeBean session,String emailSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update products.sessions set emailSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{emailSent,session.getId()});
		
	}
	
	public ArrayList<SessionDayTimeBean> getScheduledSessionForDayForSMS() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start in next 2 hours next day
		String sql = "select  *, date_format(date,'%d-%b-%Y') date from products.sessions "
				+ " where date = DATE_ADD(curdate(), INTERVAL 1 day)  "
				+ " and timediff(starttime, time(sysdate())) > '00:00:00' "
				+ " and  timediff(starttime, time(sysdate())) < '02:00:00'  "
				+ " and (smsSent is null or smsSent <> 'Y') "
				+ " and (isCancelled <> 'Y' or isCancelled is null) ";

		ArrayList<SessionDayTimeBean> scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		return scheduledSessionList;
	}
	
	public void updateSMSStatus(SessionDayTimeBean session,String smsSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update products.sessions set smsSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{smsSent,session.getId()});
		
	}
	public void updateCancellationEmailStatus(SessionDayTimeBean session) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update products.sessions set cancellationEmailSent = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[]{session.getId()});
	}

	public void updateCancellationSMSStatus(SessionDayTimeBean session) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update prodcuts.sessions set cancellationSmsSent = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[]{session.getId()});
		
	}
	
}
