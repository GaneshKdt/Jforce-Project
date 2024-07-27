package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.PageCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.helpers.PaginationHelper;


public class SessionSchedulerDao {

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	private String sessionBufferLowerLimit = "-00:25:00";
	private String sessionBufferUpperLimit = "02:25:00";
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(SessionSchedulerDao.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		return subjectList;

	}
	
	public ArrayList<String> getAllLocations() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT location FROM acads.location_room_capacity order by location asc";
		ArrayList<String> locationList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		return locationList;

	}
	
	public ArrayList<String> getAllFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' ";
		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		return facultyList;
	}
	
	public boolean isFacultyAvailable(SessionDayTimeBean session) {
		String date = session.getDate();
		String facultyId = session.getFacultyId();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select count(*) from acads.calendar c,acads.session_days sd "
				+ " where c.date = ? "
				+ " and CONCAT(c.date, sd.startTime) in (select CONCAT(date, time)  from acads.facultyAvailability where facultyId = ? ) "; //And date is not which faculty is not going to be available
		
		try {
			
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, facultyId},Integer.class);
			
			if(count > 0){
				if(checkIfFacultyIsForCS(facultyId)) {
					return true;
				}
			}
			
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}


	public boolean checkIfFacultyIsForCS(String facultyId) {
		String sql = ""
				+ "SELECT "
					+ "count(*) "
				+ "FROM "
				+ "`acads`.`faculty` `f` "
					+ "LEFT JOIN "
					+ "`products`.`speakers` `s` "
					+ "ON "
					+ "`s`.`facultyTableId` = `f`.`id` "
				+ "WHERE "
					+ "`f`.`facultyId` = ?";
		
		try {
			int faculty = jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, Integer.class);

			if(faculty == 0) {
				return false;
			}else {
				return true;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

	
	public boolean isNotClashingWithPGSessions(SessionDayTimeBean session) {
		String date = session.getDate();
		String time = session.getStartTime();

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  " select count(*) from acads.calendar c, acads.session_days sd "
					+ " where c.date = ? and sd.startTime = ? "
					+ " and year =  "+CURRENT_ACAD_YEAR+" and month = '"+CURRENT_ACAD_MONTH+"' "
					+ " and CONCAT(c.date, sd.startTime) in "
					+ " (select CONCAT(date, startTime) from acads.sessions "
					+ " where year =  "+CURRENT_ACAD_YEAR+" and month = '"+CURRENT_ACAD_MONTH+"' )";

		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date,time},Integer.class);

			if(count == 0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}

	}
	
	public boolean isNotClashingWithCSSessions(SessionDayTimeBean session) {
		String date = session.getDate();
		String time = session.getStartTime();

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  " select count(*) from acads.calendar c, acads.session_days sd "
					+ " where c.date = ? and sd.startTime = ? "
					+ " and CONCAT(c.date, sd.startTime) in "
					+ " (select CONCAT(date, startTime) from `products`.`sessions`)";

		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date,time},Integer.class);
			if(count == 0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}

	}
	
	public boolean isFacultyFreeAllChecks(SessionDayTimeBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/*
		 * Updated for Zoom by Pranit on 26 Dec
		 * Getting the count of sessions for which the faculty is allocated for given date time
		 * here sessions startime and endtime is checked with the uploaded date and time 
		 * */
		
		String sql = "select count(*) from acads.sessions s "
				+ " where s.year =  "+CURRENT_ACAD_YEAR+" and s.month = '"+CURRENT_ACAD_MONTH+"' "
				+ " and (s.facultyId = ?  "
				+ " or s.altFacultyId= ?  or s.altFacultyId2= ?  or s.altFacultyId3= ?) "
				+ " and ( "
				+ "	cast(CONCAT(date,' ', startTime) as datetime)   "
				+ " between  "
				+ " (select cast(CONCAT(c.date,' ', sd.startTime) as datetime) as dt from acads.calendar c, acads.session_days sd  "
				+ " where c.date =  ? and sd.startTime =  ?  group by dt )  "
				+ "  and  "
				+ " (select DATE_ADD(cast(CONCAT(c.date,' ', sd.startTime) as datetime), INTERVAL 2 HOUR) as dt from acads.calendar c, acads.session_days sd     "
				+ "  where c.date =  ? and sd.startTime =   ? group by dt )  "
				+ "  or  "
				+ "  cast(CONCAT(date,' ', endTime) as datetime)  "
				+ "  between "
				+ "  (select cast(CONCAT(c.date,' ', sd.startTime) as datetime) as dt from acads.calendar c, acads.session_days sd     "
				+ "  where c.date =  ? and sd.startTime =   ?   group by dt )  "
				+ "  and  "
				+ " (select DATE_ADD(cast(CONCAT(c.date,' ', sd.startTime) as datetime), INTERVAL 2 HOUR) as dt from acads.calendar c, acads.session_days sd     "
				+ "  where c.date =  ? and sd.startTime =   ?  group by dt )  "
				+ "  ) "
				+ "  ;";
		

		int count = 1;
		try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{facultyId,facultyId,facultyId,facultyId,
																			date, time,
																			date, time,
																			date, time,
																			date, time},Integer.class);
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			count = 1;
		}
		
		if(count == 0){
			if(checkIfFacultyIsForCS(facultyId)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isFacultyTakingLessThan2SubjectsSameDay(SessionDayTimeBean session) {
		String date = session.getDate();
		String facultyId = session.getFacultyId();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select count(*) from acads.calendar c "
				+ " where c.date = ? "
				+ " and c.date not in (select date from acads.sessions where facultyId = ? group by date having count(date) > 1) "; //Ensure faculty is not taking more than 2 sessions on same day

		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, facultyId},Integer.class);
			if(count == 0){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}		
	}
	
	public boolean isNotHoliday(SessionDayTimeBean session) {
		String date = session.getDate();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select count(*) from acads.calendar c where  (c.isHoliday is null or c.isHoliday <> 'Y' )  and c.date = ? ";

		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date},Integer.class);

			if(count == 0){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}

	}
	
	public ArrayList<EndPointBean> getAvailableRoomByLoaction(String date, String startTime, String endTime, String location) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT *  " + 
				"FROM products.endpoint e  " + 
				"where e.type = 'Faculty Room'  " + 
				"	and  e.hostId not in (  " + 
				"		select COALESCE(hostId,'') from products.sessions    " + 
				"			where ( " + 
				"				  (cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
				+ "					 or   " + 
				"				  (cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and addtime(?,   \""+sessionBufferUpperLimit+"\") )  " + 
				"				  ) " + 
				" 				  and date =  ?    " + 
				"   ) " + 
				"     "  
				;
		
		try{
			ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new Object[]
															{startTime,startTime,startTime,startTime, date }, 
																	new BeanPropertyRowMapper<EndPointBean>(EndPointBean.class));
		
			 return endPointList;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public int getCapacityOfLocation(String location) {
		int count= 0;
		try {
			//id, location, room_capacity
			String sql = "select room_capacity from acads.location_room_capacity where location = ? ";
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{location},Integer.class);
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
		}

		return count;
	}
	
	public int getNoOfSessionAtSameDateTimeLocation(String date, String time , String location) {
		int count= 0;
		try {
			//id, location, room_capacity
			String sql = "select count(*) from products.sessions "
					+ " 	where date = ? "
					+ "			and ( "+
					"				  (cast(startTime as time) between addtime(?,  \"00:00:00\") and  addtime(?,   \"02:00:00\") )"
					+ "					 or   " + 
					"				  (cast(endTime as time) between addtime(?,   \"00:00:00\") and  addtime(?,   \"02:00:00\") )  " + 
					"				) " 
					+ "			and facultyLocation = ? ";
			count += (int) jdbcTemplate.queryForObject(sql, new Object[]{date,
																		time,time,time,time,
																		location},Integer.class);
			
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			count = 0;
		}

		return count;
	}
	
	//insertDuplicateSession start
	public Boolean insertDuplicateSession(SessionDayTimeBean bean) {

		String sql = "INSERT INTO products.sessions ( "
				+ "date, startTime, endTime, "
				+ "day, sessionName, room, "
				+ "hostId, hostPassword, bookingStatus, "
				+ "meetingKey, hostUrl, hostKey, "
				+ "facultyId, facultyLocation, isCancelled, "
				+ "description, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate "
				+ ") "
				+ " VALUES"
				+ "( "
				+ " ?, ?, ADDTIME(?, '02:00:00'),"
				+ "(Select dayName from acads.calendar where date = ? ), ?, ?,"
				+ "?, ?, ?,"
				+ "?, ?, ?,"
				+ "?, ?, ?,"
				+ "?, ?, sysdate(),"
				+ "?, sysdate()"
				+ " ) ";

		
		try {
			String date = bean.getDate();
			String startTime = bean.getStartTime();
			String sessionName = bean.getSessionName();
			String room = bean.getRoom();
			String hostId = bean.getHostId();
			String hostPassword = bean.getHostPassword();
			String bookingStatus =bean.getBookingStatus();
			String meetingKey= bean.getMeetingKey();
			String hostUrl=bean.getHostUrl();
			String hostKey=bean.getHostKey(); 
			
			String facultyId = bean.getFacultyId();
			String facultyLocation=bean.getFacultyLocation();
			
			String isCancelled="N";
			String description=bean.getDescription();
			String createdBy = bean.getCreatedBy();
					
			jdbcTemplate.update(sql, new Object[] { 
								date, 		startTime, 		startTime, 
								date, 		sessionName, 	room, 
								hostId, 	hostPassword, 	bookingStatus, 
								meetingKey, hostUrl, 		hostKey, 
								facultyId, 	facultyLocation, isCancelled, 
								description, createdBy, 
								createdBy
				});

			return true;
		
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}

	}

	public PageCareerservicesBean<SessionDayTimeBean> getScheduledSessionPage(int pageNo, int pageSize, SessionDayTimeBean searchBean) {
		

		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.faculty f , products.sessions s where s.facultyId = f.facultyId ";
		String countSql = "SELECT count(*) FROM acads.faculty f , products.sessions s where s.facultyId = f.facultyId";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and s.facultyId like  ? ";
			countSql = countSql + " and s.facultyId like  ? ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getSessionName() != null &&   !("".equals(searchBean.getSessionName()))){
			sql = sql + " and sessionName like  ? ";
			countSql = countSql + " and sessionName like  ? ";
			parameters.add("%"+searchBean.getSessionName()+"%");
		}

//		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
//			sql = sql + " and year = ? ";
//			countSql = countSql + " and year = ? ";
//			parameters.add(searchBean.getYear());
//		}
//		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
//			sql = sql + " and month = ? ";
//			countSql = countSql + " and month = ? ";
//			parameters.add(searchBean.getMonth());
//		}
		if( searchBean.getDate() != null &&   !("".equals(searchBean.getDate()))){
			sql = sql + " and date = ? ";
			countSql = countSql + " and date = ? ";
			parameters.add(searchBean.getDate());
		}
		if( searchBean.getDay() != null &&   !("".equals(searchBean.getDay()))){
			sql = sql + " and day = ? ";
			countSql = countSql + " and day = ? ";
			parameters.add(searchBean.getDay());
		}

		if( searchBean.getId() != null &&   !("".equals(searchBean.getId()))){
			sql = sql + " and s.Id = ? ";
			countSql = countSql + " and s.Id = ? ";
			parameters.add(searchBean.getId());
		}

		if( searchBean.getFacultyLocation() != null &&   !("".equals(searchBean.getFacultyLocation()))){
			sql = sql + " and s.facultyLocation like  ? ";
			countSql = countSql + " and s.facultyLocation like  ? ";
			parameters.add("%"+searchBean.getFacultyLocation()+"%");
		}

		sql = sql + " order by date, startTime asc";

		Object[] args = parameters.toArray();

		PaginationHelper<SessionDayTimeBean> pagingHelper = new PaginationHelper<SessionDayTimeBean>();
		PageCareerservicesBean<SessionDayTimeBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));

		return page;
	
	}
	
	public ArrayList<FacultyCareerservicesBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";
		ArrayList<FacultyCareerservicesBean> facultyNameAndIdList = null;
		try{
			facultyNameAndIdList = (ArrayList<FacultyCareerservicesBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
			return facultyNameAndIdList;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public List<String> getListOfLocations() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select location from acads.location_room_capacity group by location ";

		try {
			List<String> locationList = (List<String>)jdbcTemplate.query(sql, 
					new Object[]{}, new SingleColumnRowMapper<String>(String.class));

			return locationList;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public List<SessionDayTimeBean> getPendingConferenceList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from products.sessions "
				+ "where (bookingStatus <> 'B' or bookingStatus is null or bookingStatus= '')  ";
		
		try {
			List<SessionDayTimeBean> pendingConferenceList = jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
			return pendingConferenceList;
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public SessionDayTimeBean findScheduledSessionById(String id) {
		String sql = "SELECT * FROM acads.faculty f , products.sessions s where s.facultyId = f.facultyId and s.id = ?";

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
	
	public void updateRooms(SessionDayTimeBean session) {
		String sql = "update products.sessions "
				+ " set "  
				+ " room = ?,"
				+ " hostId = ?," 
				+ " hostPassword = ?,"
				+ " hostKey = ? " 

				+ " where id = ?";

		jdbcTemplate.update(sql, new Object[]{session.getRoom(), session.getHostId(), session.getHostPassword(), session.getHostKey(), session.getId()});
		
	}
	
	public void updateScheduledSession(SessionDayTimeBean session) {
		 String sql = " Update products.sessions set "
					+ " facultyId=?,"
					+ " date=?,"
					+ " startTime=?,"
					+ " endTime=ADDTIME(?, '02:00:00'),"
					+ " day = (select dayName from acads.calendar where date = ?),"
					+ " smsSent='N',"
					+ " emailSent='N',"
					+ " lastModifiedBy=?,"
					+ " lastModifiedDate=sysdate(),"
					+ " description = ?,"
					+ " facultyLocation=?"
					+ " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				session.getFacultyId(),
				session.getDate(),
				session.getStartTime(),
				session.getStartTime(),
				session.getDate(),
				session.getLastModifiedBy(),
				session.getDescription(),
				session.getFacultyLocation(),
				session.getId()
		});

	}
	
	public SessionDayTimeBean getWebinarForRefresh(String id){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from products.sessions where id = ? ";
		SessionDayTimeBean bean = (SessionDayTimeBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
		return bean;
	}
	
	public String updateZoomDetails(SessionDayTimeBean session) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			String sql =  " update products.sessions "
						+ " set "  
						+ " meetingKey = ?,"
						+ " hostId = ?," 
						+ " hostKey = ?,"
						+ " joinUrl = ?,"
						+ " hostUrl = ?" 

						+ " where id = ?";
				
			
			
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(), session.getHostId(), session.getHostKey(),session.getJoinUrl(),session.getHostUrl(), session.getId()});
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return e.getMessage();
		}
		
		return null;
	}
	
	public void updateAttendanceForOldMeeting(SessionDayTimeBean session,String oldMeetingKey) {
		try {
			String sql = "update products.session_attendance set meetingKey = ? where  meetingKey = ? ";
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(),oldMeetingKey});
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
	}
	
	public int[] updateBookedConference(final List<SessionDayTimeBean> conferenceList) {
		
		String sql =  " update products.sessions "
					+ " set "
					+ " bookingStatus = 'B',"
					+ " meetingKey = ? ," 
					+ " hostUrl = ?, " 
					+ " joinUrl = ? " 
					+ " where id = ?";
		
		int[] confBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SessionDayTimeBean bean = conferenceList.get(i);
				
				ps.setString(1, bean.getMeetingKey());
				ps.setString(2, bean.getHostUrl());
				ps.setString(3, bean.getJoinUrl());
				ps.setString(4, bean.getId());
				
			}

			public int getBatchSize() {
				return conferenceList.size();
			}
		});

		return confBookingDBUpdateResults;

	}
	
	public SessionDayTimeBean getSessionForCheck(String id){
		String sql = "select * from products.sessions where id = ? ";
		SessionDayTimeBean bean = null;
		try {
			bean = (SessionDayTimeBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));

		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return bean;
	}
	
	public void deleteScheduledSession(String id) {
		String sql = "Delete from products.sessions where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});

	}


}
