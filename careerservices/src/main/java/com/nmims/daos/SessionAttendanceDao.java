package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import java.sql.Statement;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.SessionAttendance;
import com.nmims.beans.SessionDayTimeBean;


public class SessionAttendanceDao {

	@Autowired
	ApplicationContext appContext;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
		
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(SessionAttendanceDao.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
//	private final int MaxWebinar = 12;
	
	public List<SessionAttendance> getAllAttendedSessions(String subject,String userId){
	//	jdbcTemplate = new JdbcTemplate(dataSource);
		List<SessionAttendance> listOfAttendedSessions = null;
		
		String	sql = " select distinct count(attended) as attended "
				+ "  from products.session_attendance saf, products.sessions s"
				+ "  where saf.sessionId = s.id and s.subject = ? and saf.sapid = ?"
				+ "  and s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"' "
				+ "  and (s.isCancelled <> 'Y' or s.isCancelled is null) and saf.attended = 'Y'";
	
	try {
		listOfAttendedSessions = jdbcTemplate.query(sql, new Object[]{subject, userId}, new BeanPropertyRowMapper<SessionAttendance>(SessionAttendance.class));
		
	} catch (Exception e) {
		logger.info("exception : "+e.getMessage());
	}
		return listOfAttendedSessions;
	}


	public List<SessionAttendance> getAllSessions(){
	//	jdbcTemplate = new JdbcTemplate(dataSource);
		List<SessionAttendance> countOfScheduledSessions = null;
		
		String	sql = " select distinct count(s.id) as scheduled  from  acads.sessions s "
				+ " and (s.isCancelled <> 'Y' or s.isCancelled is null)";

		try {
			countOfScheduledSessions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionAttendance>(SessionAttendance.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return countOfScheduledSessions;
	}
	
	
	
	public int getAllPendingSessions(String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		int countOfPendingSessions = 0;

		String	sql = " select distinct count(s.id) as pending from acads.sessions s"
				+ "  where s.date >= sysdate() and s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"' "
				+ "  and s.subject = ?"
				+ "  and (s.isCancelled <> 'Y' or s.isCancelled is null)";

		try {
			countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return countOfPendingSessions;
	}
	
	public int getAllConductedSessionsforSubject(String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		int countOfConductedSessions = 0;
		
		String	sql = " select distinct count(s.id) as conducted  from  acads.sessions s "
				+ " where s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"'  and s.subject = ? "
				+ " and (s.isCancelled <> 'Y' or s.isCancelled is null) and date <= sysdate()";

		try {
			countOfConductedSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
		return countOfConductedSessions;
	}
	
	public boolean isNoClashingWithPGlecture(SessionDayTimeBean session){
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		
		String sql =  " select * from acads.calendar c, acads.session_days sd  "
					+ " where c.date = ? and sd.startTime = ? "
					+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions )";
		try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getDate(), session.getStartTime()},Integer.class);
			if(count == 0){
				return false;
			}else{
				return true;
			}
			
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return false;

		}
	}
	
	public SessionDayTimeBean findScheduledSessionById(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeBean session = new SessionDayTimeBean();

		String sql = "SELECT * FROM acads.faculty f , products.sessions s where s.facultyId = f.facultyId and s.id = ?";
		try {
			session= (SessionDayTimeBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}

		return session;
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
	
	public int findUsersJoined(String id) {
		String sql = "select count(sapid) from products.session_attendance where attended = 'Y' and sessionId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{id},Integer.class);
		return count;
	}
	
	public int recordAttendance(SessionDayTimeBean session, String sapId, String purchaseId ) {


		String sessionId = session.getId();

		try {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement("INSERT INTO products.session_attendance("
							+ "sapid, purchaseId, sessionId, attended, attendTime, device, createdBy, createdDate) VALUES "
							+ "(?, ?, ?,'Y', sysdate(),?,?,sysdate())"
							+ " on duplicate key "
							+ " update "
							+ " reAttendTime = sysdate(), "
							+ " device = ?, "
							+ " lastModifiedBy = ?, "
							+ " lastModifiedDate = sysdate() ", Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, sapId);
			        statement.setString(2, purchaseId);
			        statement.setString(3, sessionId);
			        statement.setString(4, session.getDevice());
			        statement.setString(5, session.getCreatedBy());
			        statement.setString(6, session.getDevice());
			        statement.setString(7, session.getLastModifiedBy());
			        return statement;
			    }
			}, holder);

			return holder.getKey().intValue();
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return 0;
		}
	}
	
	public StudentCareerservicesBean getstudentData(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.students where sapid = ?";
		return  jdbcTemplate.queryForObject(sql,new Object[] {sapId} ,new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));

 	}
}
