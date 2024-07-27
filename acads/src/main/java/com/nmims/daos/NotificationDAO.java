package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.MailAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;


public class NotificationDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private String TIMEBOUND_PORTAL_LIST;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForDay() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from acads.sessions where date = date(sysdate()) + 1 ";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllCorporateCenterNames() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select DISTINCT(corporateName) from acads.sessions where (corporateName <> '' and corporateName is not null and corporateName <> 'NULL' and corporateName <> 'All') ";

		ArrayList<String> allCorporateCenters = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(String.class));
		return allCorporateCenters;
	} 

	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSubject(String subject,String corporateCenterName ,String hasModuleId, ArrayList<String> excludeCorporateCenters) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String corporateCentersNamesSeparated = "''";
		for (int i = 0; i < excludeCorporateCenters.size(); i++) {
			
			if (i == 0) {
				corporateCentersNamesSeparated = "'" +excludeCorporateCenters.get(i)+ "'";
			} else {
				corporateCentersNamesSeparated = corporateCentersNamesSeparated + ", '" + excludeCorporateCenters.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		ArrayList<Object> parameters = new ArrayList<>();
		String sql = "select * "
				+ " from exam.students s, exam.registration r, exam.examorder eo  "
				+ " where r.month = eo.acadMonth and r.year = eo.year  "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
				//added not condition for avoiding lecture notification of BS for SAS students temporarily.
				+ " and s.sapid = r.sapid and s.program <> 'MPDV' and s.program <> 'EPBM' "
				//added not condition for avoiding lecture notification to be sent for admission cancelled students.
				+ " and (s.programStatus not in ('Program Terminated', 'Program Withdrawal', 'Program Suspension') or s.programStatus is null) " 
				+ " and concat(r.program,r.sem, s.prgmStructApplicable) in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ? and active = 'Y')";
		
		parameters.add(subject);
		if(!excludeCorporateCenters.isEmpty()){
			sql += " and s.centerName not In ("+corporateCentersNamesSeparated+") ";
//			parameters.add(corporateCentersNamesSeparated);
		}
		
		if ("M.Sc".equalsIgnoreCase(corporateCenterName)) {
			sql = sql + " and s.consumerProgramStructureId = 113 ";
		
		}else if(!StringUtils.isBlank(corporateCenterName) && (!("All".equalsIgnoreCase(corporateCenterName)))){
			sql += " and s.centerName = ? ";
			parameters.add(corporateCenterName);
			
		}else {
			sql = sql + " and s.consumerProgramStructureId not in( 113, 127, 128) ";
		}
		
		if("Y".equalsIgnoreCase(hasModuleId)) { 
			sql += " and s.consumerProgramStructureId IN ('111','151') "; 
			sql += " and s.sapid in ( SELECT DISTINCT "
         + "  tum.userId "
        + " FROM "
           + " acads.sessions ses "
           + "    INNER JOIN acads.sessionplan_module m on ses.moduleId = m.id "
           + "    INNER JOIN acads.sessionplan sp on m.sessionPlanId = sp.id "
           + "    INNER JOIN acads.sessionplanid_timeboundid_mapping stm on sp.id = stm.sessionPlanId "
           + "    INNER JOIN  lti.timebound_user_mapping tum on stm.timeboundId = tum.timebound_subject_config_id "
           + "    INNER JOIN lti.student_subject_config ssc on tum.timebound_subject_config_id = ssc.id "
           + "    INNER JOIN exam.program_sem_subject pss on ssc.prgm_sem_subj_id = pss.id "
           + "    WHERE ses.hasModuleId = 'Y' "
           + "    AND pss.subject ='"+ subject +"' " 	
           + "    AND current_timestamp between ssc.startDate AND ssc.endDate "
           + ") ";
		 }else {
			sql += " and s.consumerProgramStructureId NOT IN ('111', '151') ";
		}
		
		Object [] arg = parameters.toArray();
		ArrayList<StudentAcadsBean> studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper(StudentAcadsBean.class));
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSubjectOld(String sessionId,String hasModuleId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<>();
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql = " SELECT s.* FROM " + 
					"    exam.students s " + 
					"        INNER JOIN " + 
					"    exam.registration r ON s.sapid = r.sapid " + 
					"        INNER JOIN " + 
					"    exam.examorder eo ON r.month = eo.acadMonth " + 
					"        AND r.year = eo.year " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId " + 
					"        AND r.sem = pss.sem " + 
					"        INNER JOIN " + 
					"    acads.sessions asi ON pss.subject = asi.subject " + 
					"        AND r.month = asi.month " + 
					"        AND r.year = asi.year " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON asi.id = ssm.sessionId " + 
					"        AND pss.id = ssm.program_sem_subject_id " + 
					" WHERE 1 = 1 " + 
					"   	 AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder WHERE acadSessionLive = 'Y') " + 
					"        AND s.program <> 'MPDV' " + 
					"        AND s.program <> 'EPBM' " + 
					"        AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " + 
					"        AND asi.id = ? ";
			parameters.add(sessionId);
			
		if("Y".equalsIgnoreCase(hasModuleId)) { 
			sql +=  "        AND s.sapid IN ( SELECT DISTINCT " + 
					"            tum.userId " + 
					"        FROM " + 
					"            acads.sessions ses " + 
					"                INNER JOIN " + 
					"            acads.sessionplan_module m ON ses.moduleId = m.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplan sp ON m.sessionPlanId = sp.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
					"                INNER JOIN " + 
					"            lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id " + 
					"                INNER JOIN " + 
					"            lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
					"                INNER JOIN " + 
					"            exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
					"        WHERE " + 
					"            ses.hasModuleId = 'Y' AND ses.id = ? " + 
					"                AND CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate ) ";
			parameters.add(sessionId);
		}else {
			sql += " 		AND s.consumerProgramStructureId NOT IN ( '111','151' ) ";
		}

		try {
			Object [] arg = parameters.toArray();
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSubjectNew(String sessionId,String hasModuleId, String year, String month) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<>();
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql =" SELECT s.* FROM exam.students s " + 
					" INNER JOIN exam.registration r ON s.sapid = r.sapid ";
					
					if(!"Y".equalsIgnoreCase(hasModuleId)) { 
						sql += " INNER JOIN exam.examorder eo ON r.month = eo.acadMonth AND r.year = eo.year "; 
					}
					
					sql += " INNER JOIN exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId AND r.sem = pss.sem " + 
							" INNER JOIN acads.sessions asi ON pss.subject = asi.subject ";
					
					if(!"Y".equalsIgnoreCase(hasModuleId)) { 
						sql += " AND r.month = asi.month AND r.year = asi.year " ;
					}
							
					sql += 	" INNER JOIN acads.session_subject_mapping ssm ON asi.id = ssm.sessionId AND pss.id = ssm.program_sem_subject_id " + 
							" WHERE 1 = 1 " ;
					
					if(!"Y".equalsIgnoreCase(hasModuleId)) { 
						sql += " AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder WHERE acadSessionLive = 'Y') ";
					}
					 
					sql+=	"   AND s.program <> 'MPDV' AND s.program <> 'EPBM' " + 
							" 	AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " + 
							"   AND asi.id = ? ";
			
					parameters.add(sessionId);
			
		if("Y".equalsIgnoreCase(hasModuleId)) { 
			sql +=  "        AND s.sapid IN ( SELECT DISTINCT " + 
					"            tum.userId " + 
					"        FROM " + 
					"            acads.sessions ses " + 
					"                INNER JOIN " + 
					"            acads.sessionplan_module m ON ses.moduleId = m.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplan sp ON m.sessionPlanId = sp.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
					"                INNER JOIN " + 
					"            lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id " + 
					"                INNER JOIN " + 
					"            lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
					"                INNER JOIN " + 
					"            exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
					"        WHERE " + 
					"            ses.hasModuleId = 'Y' AND ses.id = ? AND role = 'Student'" + 
					"                AND CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate ) ";
			parameters.add(sessionId);
		}else {
			sql +=	"	AND s.consumerProgramStructureId NOT IN ( '111','151' ) ";
			
			if (!year.equalsIgnoreCase("2021") && !month.equalsIgnoreCase("Jan")) {
				sql +=	" 	AND s.sapid IN (SELECT " + 
						"    userid " + 
						" FROM " + 
						"    exam.student_session_courses_mapping " + 
						" WHERE " + 
						"    acadYear = asi.year AND acadMonth = asi.month " + 
						"        AND program_sem_subject_id IN (SELECT " + 
						"            program_sem_subject_id " + 
						"        FROM " + 
						"            acads.session_subject_mapping " + 
						"        WHERE " + 
						"            sessionId = ?))";
				parameters.add(sessionId);
			}
		}
		
		sql += " GROUP BY s.sapid";
	
		try {
			Object [] arg = parameters.toArray();
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForDayForSMS() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start in next 2 hours next day
		String sql = "select  *, date_format(date,'%d-%b-%Y') date from acads.sessions "
				+ " where date = DATE_ADD(curdate(), INTERVAL 1 day)  "
				+ " and timediff(starttime, time(sysdate())) > '00:00:00' "
				+ " and  timediff(starttime, time(sysdate())) < '02:00:00'  "
				+ " and (smsSent is null or smsSent <> 'Y') "
				+ " and (isCancelled <> 'Y' or isCancelled is null) ";

		//Get sessions scheduled to start tomorrow
		//String sql = "select  *, date_format(date,'%d-%b-%Y') date from acads.sessions where date = DATE_ADD(curdate(), INTERVAL 1 day)  and (smsSent is null or smsSent <> 'Y') ";

	
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForFirebase() {	
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start in next 2 hours next day
		String sql = "SELECT * FROM acads.sessions AS s WHERE "
				+ "(TIME_TO_SEC(TIMEDIFF( CAST(CONCAT(date, ' ', startTime) AS DATETIME),SYSDATE())) < 1800) "
				+ "AND (TIME_TO_SEC(TIMEDIFF( CAST(CONCAT(date, ' ', startTime) AS DATETIME),SYSDATE())) > 0) AND "
				+ "(s.isCancelled <> 'Y' OR s.isCancelled IS NULL);";

		//Get sessions scheduled to start tomorrow
		//String sql = "select  *, date_format(date,'%d-%b-%Y') date from acads.sessions where date = DATE_ADD(curdate(), INTERVAL 1 day)  and (smsSent is null or smsSent <> 'Y') ";

	
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForDayForEmail() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start tomorrow
		//String sql = "select  *, date_format(date,'%d-%b-%Y') date from acads.sessions where date = DATE_ADD(curdate(), INTERVAL 1 day)  and (emailSent is null or emailSent <> 'Y') ";
		
		//Get sessions scheduled to start in next 2 hours next day
		String sql = "select  *, date_format(date,'%d-%b-%Y') date from acads.sessions "
				+ " where date = DATE_ADD(curdate(), INTERVAL 1 day)  "
				+ " and timediff(starttime, time(sysdate())) > '00:00:00' "
				+ " and  timediff(starttime, time(sysdate())) < '02:00:00'  "
				+ " and (emailSent is null or emailSent <> 'Y') "
				+ " and (isCancelled <> 'Y' or isCancelled is null) "; 

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}

	@Transactional(readOnly = false)
	public void updateSMSStatus(SessionDayTimeAcadsBean session,String smsSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update acads.sessions set smsSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{smsSent,session.getId()});
	}

	@Transactional(readOnly = false)
	public void updateTestSMSStatus(TestAcadsBean test,String smsSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update exam.test set smsSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{smsSent,test.getId()});
	}
	
	@Transactional(readOnly = false)
	public void updateEmailStatus(SessionDayTimeAcadsBean session,String emailSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update acads.sessions set emailSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{emailSent,session.getId()});
	}
	
	@Transactional(readOnly = false)
	public void updateTestNotificationEmailStatus(TestAcadsBean test,String emailSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update exam.test set emailSent = ? where id = ? ";
		jdbcTemplate.update(sql, new Object[]{emailSent,test.getId()});
	}
	
	@Transactional(readOnly = false)
	public void updateCancellationSMSStatus(SessionDayTimeAcadsBean session) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update acads.sessions set cancellationSmsSent = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[]{session.getId()});
	}
	
	@Transactional(readOnly = false)
	public void updateCancellationEmailStatus(SessionDayTimeAcadsBean session) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Update acads.sessions set cancellationEmailSent = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[]{session.getId()});
		
	}
	
	//Added from PortalDAO for storing lecture scehduled mails: START//
	@Transactional(readOnly = false)
	public void insertUserMailRecord(final ArrayList<MailAcadsBean> mailList, final String fromUserId,final String fromEmailId,final long mailTemplateId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ";
		try{
			int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MailAcadsBean mailBean = mailList.get(i);
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
			  
		}
		
		
	}
	
	@Transactional(readOnly = false)
	public long insertMailRecord(final ArrayList<MailAcadsBean> mailList,final String fromUserId){
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
		/*String sql = " INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ";
		jdbcTemplate.update(sql,new Object[]{mailList.get(0).getSubject(),fromUserId,mailList.get(0).getFilterCriteria(),mailList.get(0).getBody(),mailList.get(0).getFromEmailId()});*/
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getNextWeekSessions (){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  " SELECT * FROM acads.sessions "
					+ " WHERE date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY) "
					+ " And (isCancelled <> 'Y' or isCancelled is null)";
		
	
		ArrayList<SessionDayTimeAcadsBean> nextWeekSessions = null;
		try {
			nextWeekSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return nextWeekSessions;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getNextWeekWebinars(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT * FROM acads.sessions "
					+ " WHERE date BETWEEN CURDATE() AND DATE_ADD(NOW(), INTERVAL 3 DAY) "
					+ " And (isCancelled <> 'Y' or isCancelled is null) AND ciscostatus IS NOT NULL ";
		
		ArrayList<SessionDayTimeAcadsBean> nextWeekWebinars = null;
		try {
			nextWeekWebinars = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return nextWeekWebinars;
	}
	
	@Transactional(readOnly = true)
	public List<TestAcadsBean> getTestsScheduledForTomorrowNSMSNotSent() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TestAcadsBean> testsScheduledForTomorrow= new ArrayList<>();
		String sql="SELECT  " + 
				"    * " + 
				"FROM " + 
				"    exam.test " + 
				"WHERE " + 
				"    DATEDIFF(startDate, SYSDATE()) = 1 " + 
				"    AND (smsSent <> 'Y' OR smsSent IS NULL); ";
		try {
			testsScheduledForTomorrow = (List<TestAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return testsScheduledForTomorrow;
	}
	
	@Transactional(readOnly = true)
	public List<StudentAcadsBean> getStudentsEligibleForTestByTestid(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentAcadsBean> studentsEligibleForTest = new ArrayList<>();
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
				" WHERE  " + 
				"    tls.liveType = 'Regular'  " + 
				"    AND tls.applicableType = 'module'  " + 
				" 	 AND tum.role = 'Student' " + 
				"	 AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
				"    AND t.id = ? ";
		try {
			studentsEligibleForTest = (List<StudentAcadsBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return studentsEligibleForTest;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSubjectByCPSId(String sessionId, String hasModuleId, String consumerProgramStructureId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<>();
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql = " SELECT s.* FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.registration r ON s.sapid = r.sapid " + 
				"        INNER JOIN " + 
				"    exam.examorder eo ON r.month = eo.acadMonth AND r.year = eo.year " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId AND r.sem = pss.sem " + 
				"        INNER JOIN " + 
				"    acads.sessions asi ON pss.subject = asi.subject AND r.month = asi.month  AND r.year = asi.year " + 
				"        INNER JOIN " + 
				"    acads.session_subject_mapping ssm ON asi.id = ssm.sessionId AND pss.id = ssm.program_sem_subject_id " + 
				" WHERE 1 = 1 " + 
				"   	 AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder WHERE acadSessionLive = 'Y') " + 
				"        AND s.program <> 'MPDV' AND s.program <> 'EPBM' " + 
				"        AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " + 
				"        AND asi.id = ? ";
		parameters.add(sessionId);
		
	if("Y".equalsIgnoreCase(hasModuleId)) { 
		sql +=  "        AND s.sapid IN ( SELECT DISTINCT " + 
				"            tum.userId " + 
				"        FROM " + 
				"            acads.sessions ses " + 
				"                INNER JOIN " + 
				"            acads.sessionplan_module m ON ses.moduleId = m.id " + 
				"                INNER JOIN " + 
				"            acads.sessionplan sp ON m.sessionPlanId = sp.id " + 
				"                INNER JOIN " + 
				"            acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
				"                INNER JOIN " + 
				"            lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id " + 
				"                INNER JOIN " + 
				"            lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"                INNER JOIN " + 
				"            exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				"        WHERE " + 
				"            ses.hasModuleId = 'Y' AND ses.id = ? " + 
				"                AND CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate ) ";
		parameters.add(sessionId);
	}else {
		sql += " 		AND s.consumerProgramStructureId in (" + consumerProgramStructureId + ") ";
	}
	
	try {
		Object [] arg = parameters.toArray();
		studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
	} catch (Exception e) {
		  
	}
	return studentList;
	
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getPGScheduledSessionForDayForEmail() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get PG, MBA(D) sessions scheduled to start in next 2 hours next day
		String sql =" SELECT " + 
					"    *, DATE_FORMAT(date, '%d-%b-%Y') date" + 
					" FROM" + 
					"    acads.sessions" + 
					" WHERE" + 
					"    date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)" + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) > '00:00:00'" + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) < '02:00:00'" + 
					"        AND (emailSent <> 'Y' OR emailSent IS NULL)" + 
					"        AND (isCancelled <> 'Y' OR isCancelled IS NULL)" + 
					"        AND (hasModuleId = 'N' OR hasModuleId IS NULL) "; 

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getMBAScheduledSessionForDayForEmail() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get MBA-WX, M.sc ML sessions scheduled to start in next 2 hours next day
		String sql =" SELECT " + 
					"    *, DATE_FORMAT(date, '%d-%b-%Y') date" + 
					" FROM" + 
					"    acads.sessions" + 
					" WHERE" + 
					"    date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)" + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) > '00:00:00'" + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) < '02:00:00'" + 
					"        AND (emailSent <> 'Y' OR emailSent IS NULL)" + 
					"        AND (isCancelled <> 'Y' OR isCancelled IS NULL)" + 
					"        AND hasModuleId = 'Y' "; 

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getPGScheduledSessionForDayForSMS() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get PG, MBA(D) sessions scheduled to start in next 2 hours next day
		String sql =" SELECT  " + 
					"    *, DATE_FORMAT(date, '%d-%b-%Y') date " + 
					" FROM " + 
					"    acads.sessions " + 
					" WHERE " + 
					"    date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) " + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) > '00:00:00' " + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) < '02:00:00' " + 
					"        AND (smsSent IS NULL OR smsSent <> 'Y') " + 
					"        AND (isCancelled <> 'Y' OR isCancelled IS NULL) " + 
					"        AND (hasModuleId = 'N' OR hasModuleId IS NULL)";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getMBAScheduledSessionForDayForSMS() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get MBA-WX, M.sc ML sessions scheduled to start in next 2 hours next day
		String sql =" SELECT  " + 
					"    *, DATE_FORMAT(date, '%d-%b-%Y') date " + 
					" FROM " + 
					"    acads.sessions " + 
					" WHERE " + 
					"    date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) " + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) > '00:00:00' " + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) < '02:00:00' " + 
					"        AND (smsSent IS NULL OR smsSent <> 'Y') " + 
					"        AND (isCancelled <> 'Y' OR isCancelled IS NULL) " + 
					"        AND hasModuleId = 'Y' ";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getPGScheduledSessionForFirebase() {	
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start in next 2 hours next day
		String sql =" SELECT  " + 
					"    * " + 
					" FROM " + 
					"    acads.sessions AS s " + 
					" WHERE " + 
					"    (TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(date, ' ', startTime) AS DATETIME),  SYSDATE())) < 1800) " + 
					"        AND (TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(date, ' ', startTime) AS DATETIME), SYSDATE())) > 0) " + 
					"        AND (s.isCancelled <> 'Y' OR s.isCancelled IS NULL) " + 
					"		 AND (hasModuleId = 'N' OR hasModuleId IS NULL)";
	
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getMBAScheduledSessionForFirebase() {	
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get sessions scheduled to start in next 2 hours next day
		String sql =" SELECT  " + 
					"    * " + 
					" FROM " + 
					"    acads.sessions AS s " + 
					" WHERE " + 
					"    (TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(date, ' ', startTime) AS DATETIME),  SYSDATE())) < 1800) " + 
					"        AND (TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(date, ' ', startTime) AS DATETIME), SYSDATE())) > 0) " + 
					"        AND (s.isCancelled <> 'Y' OR s.isCancelled IS NULL) " + 
					"		 AND hasModuleId = 'Y' ";
	
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredPGStudentForSessionAfterJul21(String sessionId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql =" SELECT s.* FROM " + 
					"    acads.sessions asi " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON asi.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"    exam.examorder eo ON asi.year = eo.year " + 
					"        AND asi.month = eo.acadmonth " + 
					"        INNER JOIN" + 
					"    exam.student_session_courses_mapping sscm ON sscm.program_sem_subject_id = ssm.program_sem_subject_id " + 
					"        AND sscm.acadYear = asi.year AND sscm.acadMonth = asi.month" + 
					"        INNER JOIN " + 
					"    exam.students s ON sscm.userId = s.sapid " + 
					" WHERE " + 
					"    asi.id = ? " + 
					"       AND eo.acadSessionLive = 'Y' " +
					"		AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
					"	GROUP BY s.sapid ";
	
		try {
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new Object[] {sessionId}, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredPGStudentForSessionBeforeJul21(String sessionId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql =" SELECT s.* FROM " + 
					"    acads.sessions asi " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON asi.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"	 exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id " +
					"        INNER JOIN " + 
					"	 exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId " +
					"		AND ssm.consumerProgramStructureId = scm.consumerProgramStructureId AND ssm.program_sem_subject_id = scm.id " +
					"        INNER JOIN " + 
					"	 exam.registration r ON asi.year = r.year AND asi.month = r.month AND scm.sem = r.sem" +
					"        INNER JOIN " + 
					"	 exam.students s ON r.sapid = s.sapid AND ssm.consumerProgramStructureId = s.consumerProgramStructureId " +
					"        INNER JOIN " + 
					"	 exam.examorder eo ON asi.year = eo.year AND asi.month = eo.acadmonth " +
					" WHERE " + 
					"    asi.id = ? " + 
					"       AND eo.acadSessionLive = 'Y' " +
					"		AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
					"		AND STR_TO_DATE(CONCAT(enrollmentMonth, '30', enrollmentYear), '%b %d %Y') < '2021-05-01' " +
					"	GROUP BY s.sapid ";
	
		try {
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new Object [] {sessionId}, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredNonPGStudentForSession(String sessionId, ArrayList<String> nonPG_ProgramList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		String consumerProgramStructureIdList = "'" + StringUtils.join(nonPG_ProgramList, "','") + "'";
		String sql =" SELECT s.* FROM " + 
					"    acads.sessions asi " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON asi.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"	 exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id " +
					"        INNER JOIN " + 
					"	 exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId " +
					"		AND ssm.consumerProgramStructureId = scm.consumerProgramStructureId AND ssm.program_sem_subject_id = scm.id " +
					"        INNER JOIN " + 
					"	 exam.registration r ON asi.year = r.year AND asi.month = r.month AND scm.sem = r.sem" +
					"        INNER JOIN " + 
					"	 exam.students s ON r.sapid = s.sapid AND ssm.consumerProgramStructureId = s.consumerProgramStructureId " +
					"        INNER JOIN " + 
					"	 exam.examorder eo ON asi.year = eo.year AND asi.month = eo.acadmonth " +
					" WHERE " + 
					"    asi.id = ? " + 
					"       AND eo.acadSessionLive = 'Y' " +
					"		AND r.consumerProgramStructureId IN (" +consumerProgramStructureIdList+ ") " +
					"		AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
					"	GROUP BY s.sapid ";
	
		try {
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new Object [] {sessionId}, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredTimeBoundStudentForSession(String sessionId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql =" SELECT s.* FROM   " + 
					"   acads.sessions ses   " + 
					"       INNER JOIN  " +
					" 	acads.sessionplan_module m ON ses.moduleId = m.id   " + 
					"       INNER JOIN  " +
					"	acads.sessionplan sp ON m.sessionPlanId = sp.id   " + 
					"       INNER JOIN  " +
					"	acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId   " + 
					"       INNER JOIN  " +
					"	lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id   " + 
					"       INNER JOIN  " +
					"	lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id   " + 
					"       INNER JOIN  " +
					"	exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   " + 
					"       INNER JOIN  " +
					"	exam.students s on tum.userId = s.sapid " + 
					" WHERE   " + 
					"  	ses.hasModuleId = 'Y' AND ses.id = ? AND role = 'Student' " + 
					"   	AND CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate " +
					"		AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
					"	GROUP BY s.sapid ";
	
		try {
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new Object [] {sessionId}, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSessionCancellation(String sessionId, String hasModuleId, String consumerProgramStructureId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<>();
		ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		
		String sql =" SELECT  s.* FROM exam.students s " + 
					" INNER JOIN acads.session_subject_mapping ssm ON s.consumerProgramStructureId = ssm.consumerProgramStructureId ";
		
		if(!"Y".equalsIgnoreCase(hasModuleId)) {
			sql +=  " INNER JOIN exam.registration r ON s.sapid = r.sapid ";
		}
			sql +=	" INNER JOIN acads.sessions asi ON ssm.sessionId = asi.id ";
			
		if(!"Y".equalsIgnoreCase(hasModuleId)) {
			sql +=	" AND r.month = asi.month AND r.year = asi.year ";
		}
		
			sql +=	" INNER JOIN exam.mdm_subjectcode sc ON asi.subject = sc.subjectname and ssm.subjectCodeId = sc.id " +
					" INNER JOIN exam.mdm_subjectcode_mapping scm ON s.consumerProgramStructureId = scm.consumerProgramStructureId " +
					" AND scm.id = ssm.program_sem_subject_id ";
		
		if(!"Y".equalsIgnoreCase(hasModuleId)) {
			sql +=	" AND r.sem = scm.sem " +
					" INNER JOIN exam.examorder eo ON asi.month = eo.acadMonth AND asi.year = eo.year ";
		}
		
			sql +=	" WHERE 1 = 1 " +
					" AND s.program <> 'MPDV' AND s.program <> 'EPBM' " +
					" AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) " +
					" AND s.consumerProgramStructureId IN (" +consumerProgramStructureId+ ") " +
					" AND asi.id = ? ";
			parameters.add(sessionId);
			
		if(!"Y".equalsIgnoreCase(hasModuleId)) {
			sql +=  " AND eo.acadSessionLive = 'Y' ";
		}
					
		if("Y".equalsIgnoreCase(hasModuleId)) { 
			sql +=  "        AND s.sapid IN ( SELECT DISTINCT " + 
					"            tum.userId " + 
					"        FROM " + 
					"            acads.sessions ses " + 
					"                INNER JOIN " + 
					"            acads.sessionplan_module m ON ses.moduleId = m.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplan sp ON m.sessionPlanId = sp.id " + 
					"                INNER JOIN " + 
					"            acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId " + 
					"                INNER JOIN " + 
					"            lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id " + 
					"                INNER JOIN " + 
					"            lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
					"                INNER JOIN " + 
					"            exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
					"        WHERE " + 
					"            ses.hasModuleId = 'Y' AND ses.id = ? AND role = 'Student' " + 
					"                AND CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate ) ";
			parameters.add(sessionId);
		}else {
			sql +="	AND s.consumerProgramStructureId NOT IN ("+TIMEBOUND_PORTAL_LIST+") ";
		}
		
		sql += " GROUP BY s.sapid ";
		
		try {
			Object [] arg = parameters.toArray();
			studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, arg, new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return studentList;
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForFaculty() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Get next 2 hours sessions for Faculty 
		String sql =" SELECT  " + 
					"    *, DATE_FORMAT(date, '%d-%b-%Y') date " + 
					" FROM " + 
					"    acads.sessions " + 
					" WHERE date = curdate() " + 
					"		AND TIMEDIFF(starttime, TIME(SYSDATE())) > '00:00:00' " + 
					"        AND TIMEDIFF(starttime, TIME(SYSDATE())) < '02:00:00' " + 
					"        AND (facultySmsSent IS NULL OR facultySmsSent <> 'Y') " + 
					"        AND (altFacultySmsSent IS NULL OR altFacultySmsSent <> 'Y') " + 
					"        AND (altFaculty2SmsSent IS NULL OR altFaculty2SmsSent <> 'Y') " + 
					"        AND (altFaculty3SmsSent IS NULL OR altFaculty3SmsSent <> 'Y') " + 
					"        AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	public ArrayList<FacultyAcadsBean> getFacultyDetails(ArrayList<String> facultyIdsList) {
		
		ArrayList<FacultyAcadsBean> listOfFaculty = new ArrayList<FacultyAcadsBean>();
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		//Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("SELECT firstName, lastName, mobile FROM acads.faculty WHERE facultyId IN (:FacultyIdsList) ").toString();
		
		//Adding parameters in SQL parameter map.
		queryParams.addValue("FacultyIdsList", facultyIdsList);
		try {
			listOfFaculty = (ArrayList<FacultyAcadsBean>) namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<FacultyAcadsBean>(FacultyAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return listOfFaculty;
	}
	
	@Transactional(readOnly = false)
	public void updateFacultySMSStatus(SessionDayTimeAcadsBean session,String smsSent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE acads.sessions SET facultySmsSent = ?, altFacultySmsSent = ?, altFaculty2SmsSent = ?, altFaculty3SmsSent = ? WHERE (id = ?) ";
		jdbcTemplate.update(sql, new Object[]{smsSent, smsSent, smsSent, smsSent, session.getId()});
		
	}
}
