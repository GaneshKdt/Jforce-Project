package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyAvailabilityBean;
import com.nmims.beans.SessionDayTimeAcadsBean;

public class ConferenceDAO extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSource dataSource;
	private HashMap<String, Integer> mapOfZoomIdAndCapacity;
	
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
	
	public HashMap<String, Integer> mapOfFacultyIdAndFacultyRecord() {
		ArrayList<EndPointBean> listOfEndpoints = getAllFacultyRoomEndPoints();
		if (this.mapOfZoomIdAndCapacity == null) {
			this.mapOfZoomIdAndCapacity = new HashMap<String, Integer>();
			for (EndPointBean bean : listOfEndpoints) {
				this.mapOfZoomIdAndCapacity.put(bean.getHostId(), bean.getCapacity());
			}
		}
		return mapOfZoomIdAndCapacity;
	}

	@Transactional(readOnly = true) 
	public List<SessionDayTimeAcadsBean> getPendingConferenceList() {
		//String sql = "select id, subject, sessionName, SUBTIME(starttime, '05:30:00') startTime,  SUBTIME(endTime, '05:30:00') endTime, date from acads.sessions where (ciscoStatus is null or ciscoStatus = ? ) ";
		
		// Commented By Somesh For Zoom Integration
		/*String sql = "select year, month, id, subject, sessionName, day, facultyId,  startTime,  endTime, hostId, hostPassword, DATE_FORMAT(date,'%m/%d/%Y') as date from acads.sessions "
				+ "where  (ciscoStatus <> 'B' or ciscoStatus is null or ciscoStatus = '') ";*/
		
		String sql = "select * from acads.sessions "
				+ "where (ciscoStatus <> 'B' or ciscoStatus is null or ciscoStatus= '')  ";
		
		List<SessionDayTimeAcadsBean> pendingConferenceList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return pendingConferenceList;
	}
	
	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean getSessionForRefresh(String id){
//		String sql = "select *, DATE_FORMAT(date,'%m/%d/%Y') as date from acads.sessions where id = ? ";
		String sql = "select * from acads.sessions where id = ? ";
		SessionDayTimeAcadsBean bean = (SessionDayTimeAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return bean;
	}

	@Transactional(readOnly = false)
	public int[] updateBookedConference(final List<SessionDayTimeAcadsBean> conferenceList) {
		
		//Commented By Somesh For Zoom Integration
		String sql = "update acads.sessions "
				+ " set "
				+ " ciscoStatus = 'B',"
				+ " meetingKey = ?,"
				+ " meetingPwd = ?,"
				+ " hostUrl = ?," 
				+ " joinUrl = ?" 
				+ " where id = ?";
		
		int[] confBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SessionDayTimeAcadsBean bean = conferenceList.get(i);
				
				ps.setString(1, bean.getMeetingKey());
				ps.setString(2, bean.getMeetingPwd());
				ps.setString(3, bean.getHostUrl());
				ps.setString(4, bean.getJoinUrl());
				ps.setString(5, bean.getId());
				
				insertIntoCounterTable(bean.getMeetingKey(), bean.getHostId(), jdbcTemplate);

			}

			public int getBatchSize() {
				return conferenceList.size();
			}
		});


		return confBookingDBUpdateResults;
	}

	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> getAllConferenceList() {	
		String sql = "select *  from acads.sessions  ";
		List<SessionDayTimeAcadsBean> pendingConferenceList = jdbcTemplate.query(sql, new Object[] { }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return pendingConferenceList;
	}

	@Transactional(readOnly = false)
	public void updateRooms(SessionDayTimeAcadsBean session) {
		String sql = "update acads.sessions "
				+ " set "  
				+ " room = ?,"
				+ " hostId = ?," 
				+ " hostPassword = ?,"
				+ " hostKey = ? " 

				+ " where id = ?";

		jdbcTemplate.update(sql, new Object[]{session.getRoom(), session.getHostId(), session.getHostPassword(), session.getHostKey(), session.getId()});
	}

	@Transactional(readOnly = false)
	public String updateWebExDetails(SessionDayTimeAcadsBean session,String type) {
		
		String sql = null;
		try {
			if("0".equalsIgnoreCase(type)){//Original session
				sql = "update acads.sessions "
						+ " set "  
						+ " meetingKey = ?,"
						+ " meetingPwd = ?,"
						+ " hostId = ?," 
						+ " hostPassword = ?" 

						+ " where id = ?";
				
			}else if("1".equalsIgnoreCase(type)){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey = ?,"
						+ " altMeetingPwd = ?,"
						+ " altHostId = ?," 
						+ " altHostPassword = ?" 

						+ " where id = ?";
				
			}else if("2".equalsIgnoreCase(type)){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey2 = ?,"
						+ " altMeetingPwd2 = ?,"
						+ " altHostId2 = ?," 
						+ " altHostPassword2 = ?" 

						+ " where id = ?";
			}else if("3".equalsIgnoreCase(type)){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey3 = ?,"
						+ " altMeetingPwd3 = ?,"
						+ " altHostId3 = ?," 
						+ " altHostPassword3 = ?" 

						+ " where id = ?";
			}
			
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(),session.getMeetingPwd(), session.getHostId(), session.getHostPassword(), session.getId()});
		} catch (Exception e) {
			  
			return e.getMessage();
		}
		
		return null;
	}
	
	@Transactional(readOnly = false)
	public String updateZoomDetails(SessionDayTimeAcadsBean session,String type) {
			
			String sql = null;
			try {
				if("0".equalsIgnoreCase(type)){//Original session
					sql = "update acads.sessions "
							+ " set "  
							+ " meetingKey = ?,"
							+ " meetingPwd = ?,"
							+ " hostId = ?," 
							+ " hostKey = ?"
							
							+ " where id = ?";
					
				}else if("1".equalsIgnoreCase(type)){
					sql = "update acads.sessions "
							+ " set "  
							+ " altMeetingKey = ?,"
							+ " altMeetingPwd = ?,"
							+ " altHostId = ?," 
							+ " altHostKey = ?"

							+ " where id = ?";
					
				}else if("2".equalsIgnoreCase(type)){
					sql = "update acads.sessions "
							+ " set "  
							+ " altMeetingKey2 = ?,"
							+ " altMeetingPwd2 = ?,"
							+ " altHostId2 = ?," 
							+ " altHostKey2 = ?"

							+ " where id = ?";
				}else if("3".equalsIgnoreCase(type)){
					sql = "update acads.sessions "
							+ " set "  
							+ " altMeetingKey3 = ?,"
							+ " altMeetingPwd3 = ?,"
							+ " altHostId3 = ?," 
							+ " altHostKey3 = ?"

							+ " where id = ?";
				}
				
				jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(), session.getMeetingPwd(), session.getHostId(), session.getHostKey(), session.getId()});
				insertIntoCounterTable(session.getMeetingKey(), session.getHostId(), jdbcTemplate);
			} catch (Exception e) {
				  
				return e.getMessage();
			}
			
			return null;
	}

	@Transactional(readOnly = false)
	public void updateAttendanceForOldMeeting(SessionDayTimeAcadsBean session,String oldMeetingKey) {
		try {
			String sql = "update acads.session_attendance_feedback set meetingKey = ?, meetingPwd = ? where  meetingKey = ? ";
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(),session.getMeetingPwd(), oldMeetingKey});
		} catch (Exception e) {
			  
		}
		
	}
	
	@Transactional(readOnly = false)
	public void updateIsVerified (SessionDayTimeAcadsBean session){
		try{
		String sql = "update acads.sessions set isVerified = 'Y' where id = ?";
		jdbcTemplate.update(sql, new Object[]{session.getId()});
		}
		catch (Exception e) {
			  
		}

	}
	
//	update meeting key if old meeting key is invalid
	@Transactional(readOnly = false)
	public String updateWebExDetail(SessionDayTimeAcadsBean session, String id, String meetingKey, String hostId ) {
		
		String sql = null;
		try {
			
			if(hostId.equalsIgnoreCase(session.getHostId())){
				sql = "update acads.sessions "
						+ " set "
						+ "meetingKey = ? where id = ?";
				
			}else if(hostId.equalsIgnoreCase(session.getAltHostId())){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey = ? where id = ?";
				
			}else if(hostId.equalsIgnoreCase(session.getAltHostId2())){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey2 = ?  where id = ?";
				
			}else if(hostId.equalsIgnoreCase(session.getAltHostId3())){
				sql = "update acads.sessions "
						+ " set "  
						+ " altMeetingKey3 = ?  where id = ?";
			}
			
			
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(),session.getId()});
		} catch (Exception e) {
			  
			return e.getMessage();
		}
		
		return null;
	}
	
	@Transactional(readOnly = false)
	public String updateMeetingPwd(SessionDayTimeAcadsBean session, String id, String hostId){
		
		String sql = null;
		
		try {
			
			if (hostId.equalsIgnoreCase(session.getHostId())) {
				sql = "update acads.sessions set meetingPwd = 'NMIMS' where id = ?";
				
			}else if (hostId.equalsIgnoreCase(session.getAltHostId())) {
				sql = "update acads.sessions set altMeetingPwd = 'NMIMS' where id = ?";
				
			}else if (hostId.equalsIgnoreCase(session.getAltHostId2())) {
				sql = "update acads.sessions set altMeetingPwd2 = 'NMIMS' where id = ?";
				
			}else if (hostId.equalsIgnoreCase(session.getAltHostId3())) {
				sql = "update acads.sessions set altMeetingPwd3 = 'NMIMS' where id = ?";
			}
						
			jdbcTemplate.update(sql, new Object[]{session.getId()});
			
		} catch (Exception e) {
			  
		}
		return null;
		
	}
	
	@Transactional(readOnly = false)
	public boolean updateBookCommonSession(SessionDayTimeAcadsBean bean) {
		boolean status = false;
		try{
			
		/* String sql = "update acads.sessions "
				+ " set "  
				+ " ciscoStatus = 'B',"
				+ " meetingKey = ?," 
				+ " meetingPwd = ?" 
				+ " where id = ?"; */
		
		String sql = "update acads.sessions "
				+ " set "
				+ " ciscoStatus = 'B',"
				+ " meetingKey = ?," 
				+ " meetingPwd = ?,"
				+ " hostUrl = ?," 
				+ " joinUrl = ?"
				+ " where id = ?";

		jdbcTemplate.update(sql, new Object[]{bean.getMeetingKey(),bean.getMeetingPwd(),bean.getHostUrl(),bean.getJoinUrl(),bean.getId()});
		insertIntoCounterTable(bean.getMeetingKey(), bean.getHostId(), jdbcTemplate);
		status = true;
		}catch(Exception e){
			  
		}
		return status;
	}
	
	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean getZoomSessionForRefresh(String id){
		String sql = "select *, DATE_FORMAT(date,'%m/%d/%Y') as date from acads.sessions where id = ? ";
		SessionDayTimeAcadsBean bean = (SessionDayTimeAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return bean;
	}
	
	@Transactional(readOnly = false)
	public List<SessionDayTimeAcadsBean> getPendingWebinarList() {
		
		String sql = "select year, month, id, subject, sessionName, day, facultyId,  startTime,  endTime, zoomHostID, hostPassword, date from acads.sessions "
				+ "where  (zoomStatus <> 'B' or zoomStatus is null or zoomStatus= '')  ";

		List<SessionDayTimeAcadsBean> pendingWebinarList = jdbcTemplate.query(sql, new Object[] {},new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		
		return pendingWebinarList;
	}
	
	public void updateBookedWebinars (final List<SessionDayTimeAcadsBean> sessions){
		
		String sql = "update acads.sessions "
					+ " set "
					+ " zoomStatus = 'B',"
					+ " zoomMeetingID = ?,"
					+ " hostJoinUrl = ?, "
					+ " studentJoinUrl = ?" 		
					+ " where id = ? ";
		
		int[] updateDBResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SessionDayTimeAcadsBean bean = sessions.get(i);
				ps.setString(1, bean.getZoomMeetingID());
				ps.setString(2, bean.getHostJoinUrl());
				ps.setString(3, bean.getStudentJoinUrl());
				ps.setString(4, bean.getId());
				
			}
			
			@Override
			public int getBatchSize() {
				
				return sessions.size();
			}
		});
		
	}
	
	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean getWebinarForRefresh(String id){
		String sql = "select * from acads.sessions where id = ? ";
		SessionDayTimeAcadsBean bean = (SessionDayTimeAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return bean;
	}
	
	@Transactional(readOnly = false)
	public String updateZoomWebinarDeatils(SessionDayTimeAcadsBean session, String type){
		String sql =null;
		
		try {
			if("0".equalsIgnoreCase(type)){//Original session
				sql = " UPDATE acads.sessions SET "
					+ " meetingKey = ?, meetingPwd = ?, "
					+ " hostKey = ?, hostId = ? "
					+ " WHERE id = ? ";
				
			}else if("1".equalsIgnoreCase(type)){
				sql = " UPDATE acads.sessions SET "
					+ " altMeetingKey = ?, altMeetingPwd = ?, "
					+ " altHostKey = ?, altHostId = ?"
					+ " WHERE id = ? ";
				
			}else if("2".equalsIgnoreCase(type)){
				sql = " UPDATE acads.sessions SET "
					+ " altMeetingKey2 = ?, altMeetingPwd2 = ?, "
					+ " altHostKey2 = ?, altHostId2 = ? "
					+ " WHERE id = ? ";

			}else if("3".equalsIgnoreCase(type)){
				sql = " UPDATE acads.sessions SET "
					+ " altMeetingKey3 = ?, altMeetingPwd3 = ?, "
					+ " altHostKey3 = ?, altHostId3 = ? "
					+ " WHERE id = ? ";
			}
			
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(), session.getMeetingPwd(),
												  session.getHostKey(), session.getHostId(), session.getId()});
			insertIntoCounterTable(session.getMeetingKey(), session.getHostId(), jdbcTemplate);
			
		} catch (Exception e) {
			  
			return e.getMessage();
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean getSessionForCheck(String id){
		String sql = "select * from acads.sessions where id = ? ";
		SessionDayTimeAcadsBean bean = null;
		try {
			bean = (SessionDayTimeAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

		} catch (Exception e) {
			  
		}
		return bean;
	}
	
	@Transactional(readOnly = false)
	public String updateWebinarDetails(SessionDayTimeAcadsBean session,String sessionId, String hostId){
		String sql = null;
		
		try {
			if(hostId.equalsIgnoreCase(session.getHostId())){
				sql = "update acads.sessions "
						+ " set "
						+ "zoomMeetingID = ? where id = ?";
			}
			jdbcTemplate.update(sql, new Object[]{session.getZoomMeetingID(), sessionId});
		} catch (Exception e) {
			  
		}
		return null;
	}
	
	public String updateZoomHostKey(SessionDayTimeAcadsBean session) {
		String sql =  " update acads.sessions "
					+ " set "  
					+ " meetingKey = ?,"
					+ " meetingPwd = ?,"
					+ " joinUrl = ?,"
					+ " hostUrl = ?," 
					+ " hostKey = ?,"
					+ " room = ?,"
					+ " hostId = ?" 
					
					+ " where id = ?";
		try {
			jdbcTemplate.update(sql, new Object[]{session.getMeetingKey(),session.getMeetingPwd(), session.getJoinUrl(),session.getHostUrl(),  session.getHostKey(), session.getRoom(), session.getHostId(), session.getId()});
		} catch (Exception e) {
			  
			return e.getMessage();
		}
		
		return null;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EndPointBean> getAllFacultyRoomEndPoints() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.endpoint where type = 'Faculty Room'";
		ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(EndPointBean.class));
		return endPointList;
	}

	@Transactional(readOnly = false)
	private void insertIntoCounterTable(String meetingKey, String hostId, JdbcTemplate jdbcTemplate) {
		int capacity = mapOfFacultyIdAndFacultyRecord().get(hostId);
		String sql = " INSERT INTO acads.session_attendance_counter (meetingkey, remainingSeats) VALUES (?,?) ";
		jdbcTemplate.update(sql, new Object[] {meetingKey, capacity});
	}
	
	@Transactional(readOnly = false)
	public void updateAllRooms(SessionDayTimeAcadsBean session) {
		String sql = "UPDATE acads.sessions SET room = ?, hostId = ?, hostKey = ?, hostPassword = ? WHERE id = ? ";
		jdbcTemplate.update(sql, new Object[]{session.getRoom(), session.getHostId(), session.getHostKey(), session.getHostPassword(), session.getId()});
	
		if (!StringUtils.isBlank(session.getAltFacultyId())) {
			sql = "UPDATE acads.sessions SET altHostId = ?, altHostKey = ?, altHostPassword = ? WHERE id = ? ";
			jdbcTemplate.update(sql, new Object[]{session.getAltHostId(), session.getAltHostKey(), session.getAltHostPassword(), session.getId()});
		}
		
		if (!StringUtils.isBlank(session.getAltFacultyId2())) {
			sql = "UPDATE acads.sessions SET altHostId2 = ?, altHostKey2 = ?, altHostPassword2 = ? WHERE id = ? ";
			jdbcTemplate.update(sql, new Object[]{session.getAltHostId2(), session.getAltHostKey2(), session.getAltHostPassword2(), session.getId()});
		}
		
		if (!StringUtils.isBlank(session.getAltFacultyId3())) {
			sql = "UPDATE acads.sessions SET altHostId3 = ?, altHostKey3 = ?, altHostPassword3 = ? WHERE id = ? ";
			jdbcTemplate.update(sql, new Object[]{session.getAltHostId3(), session.getAltHostKey3(), session.getAltHostPassword3(), session.getId()});
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getAllSessionsToScheduleBySessionId(List<String> sessionIdsList) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<SessionDayTimeAcadsBean> sessionsList=new ArrayList<>();
		String sql = "select * from acads.sessions where id IN (:sessionIdsList)";
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("sessionIdsList", sessionIdsList);
		
		try {
			sessionsList =  (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}catch (Exception e) {
//			e.printStackTrace();
		}
		return sessionsList;
	}
	
}


