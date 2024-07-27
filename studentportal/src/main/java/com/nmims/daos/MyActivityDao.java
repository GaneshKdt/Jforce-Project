package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.TimeSpentStudentBean;
import com.nmims.beans.TotalVideoDetailsStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.beans.VideoAndSessionAttendanceCountStudentBean;
import com.nmims.dto.PdfReadDetailsDto;

@Repository("myActivityDao")
public class MyActivityDao extends BaseDAO {
	
	@Autowired
    @Qualifier("analyticsDataSource")
    private DataSource analyticsDataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}

	//--- Get Time Spent Of Current Week By Sapid ---//
    @Transactional(readOnly = true)
    public List<TimeSpentStudentBean> getTimeSpentOfCurrentWeekBySapid(String sapid) {
    		
    	jdbcTemplate = new JdbcTemplate(analyticsDataSource);
    		
    	String sql = "SELECT DAYNAME(created_at) AS day_name, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60 * 60)) % 24 AS hours, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60)) % 60 AS minutes, "
    			+ "FLOOR(SUM(timeSpent) / 1000) % 60 AS seconds "
    			+ "FROM lti.page_visits "
    			+ "WHERE sapid = ? "
    			+ "AND YEAR(created_at) = YEAR(sysdate()) "
    			+ "AND WEEK(created_at) = WEEK(sysdate()) "
    			+ "GROUP BY sapid , DATE(created_at)";
    	
    	return jdbcTemplate.query(
    		sql, 
    		new Object[] {
    			sapid
    		}, new BeanPropertyRowMapper<TimeSpentStudentBean>(TimeSpentStudentBean.class)
    	);
    }
    	
    //--- Get Time Spent Of Current Month By Sapid ---//
    @Transactional(readOnly = true)
    public List<TimeSpentStudentBean> getTimeSpentOfCurrentMonthBySapid(String sapid) {
    		
    	jdbcTemplate = new JdbcTemplate(analyticsDataSource);
    			
    	String sql = "SELECT CONCAT('Week ', WEEK(created_at) - WEEK(DATE_SUB(created_at, INTERVAL DAYOFMONTH(created_at) - 1 DAY)) + 1) AS week_name, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60 * 60)) % 24 AS hours, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60)) % 60 AS minutes,"
    			+ "FLOOR(SUM(timeSpent) / 1000) % 60 AS seconds "
    			+ "FROM "
    			+ "lti.page_visits "
    			+ "WHERE sapid = ? "
    			+ "AND YEAR(created_at) = YEAR(sysdate()) "
    			+ "AND MONTH(created_at) = MONTH(sysdate()) "
    			+ "GROUP BY week_name ";
    	
    	return jdbcTemplate.query(
        	sql, 
        	new Object[] {
        		sapid
        	}, new BeanPropertyRowMapper<TimeSpentStudentBean>(TimeSpentStudentBean.class)
        );
    }
    		
    //--- Get Time Spent Of Last Month By Sapid ---//
    @Transactional(readOnly = true)
    public List<TimeSpentStudentBean> getTimeSpentOfLastMonthBySapid(String sapid) {
    			
    	jdbcTemplate = new JdbcTemplate(analyticsDataSource);
    			
    	String sql = "SELECT CONCAT('Week ', WEEK(created_at) - WEEK(DATE_SUB(created_at, INTERVAL DAYOFMONTH(created_at) - 1 DAY)) + 1) AS week_name, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60 * 60)) % 24 AS hours, "
    			+ "FLOOR(SUM(timeSpent) / (1000 * 60)) % 60 AS minutes, "
    			+ "FLOOR(SUM(timeSpent) / 1000) % 60 AS seconds "
    			+ "FROM lti.page_visits "
    			+ "WHERE sapid = ? "
    			+ "AND YEAR(created_at) = YEAR(DATE_SUB(sysdate(), INTERVAL 1 MONTH))"
    			+ "AND MONTH(created_at) = MONTH(DATE_SUB(sysdate(), INTERVAL 1 MONTH)) "
    			+ "GROUP BY week_name ";
    	
    	return jdbcTemplate.query(
    	    sql, 
    	    new Object[] {
    	    	sapid
    	    }, new BeanPropertyRowMapper<TimeSpentStudentBean>(TimeSpentStudentBean.class)
    	);
    }
    
    //--- Get Session Id List ---//
    public List<Integer> getSessionIdList(String sapid, String acadDateFormat) {
    	
    	jdbcTemplate = new JdbcTemplate(analyticsDataSource);
    	
    	String sql = "SELECT sessionId FROM acads.session_attendance_feedback WHERE sapid = ? AND acadDateFormat = ?";
    	
    	return jdbcTemplate.queryForList(
    		sql, 
    		new Object[] {
    			sapid,
    			acadDateFormat
    		}, 
    		Integer.class
    	);
    }
    
    //--- Get Session Attendance Count ---//
    public List<VideoAndSessionAttendanceCountStudentBean> getSessionAttendaceCount(String sessionIdList, String month, String year) {
    	
    	jdbcTemplate = new JdbcTemplate(analyticsDataSource);
    	
    	String sql = "SELECT subject AS subject_name, count(*) AS subject_count, track FROM acads.sessions WHERE id IN("+sessionIdList+") AND month = ? AND year = ? GROUP BY subject, track";
    	
    	return jdbcTemplate.query(
    		sql, 
    		new Object[] {
    			month,
    			year
    		},
    		new BeanPropertyRowMapper<VideoAndSessionAttendanceCountStudentBean>(VideoAndSessionAttendanceCountStudentBean.class)
    	);
    }
    
    //--- Get Track Details ---//
  	@Transactional(readOnly = true)
  	public List<TracksBean> getTrackDetails() {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT * FROM acads.tracks";
  		
  		return jdbcTemplate.query(
  			sql, 
  			new BeanPropertyRowMapper<TracksBean>(TracksBean.class)
  		);
  	}
  	
  	//--- Get Total Session Count ---//
  	@Transactional(readOnly = true)
  	public int getTotalSessionCount(String pss_id, String month, String year) {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT count(*) FROM acads.sessions s INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId "
  				+ "WHERE ssm.program_sem_subject_id IN("+pss_id+") AND s.month = ? AND s.year = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				month,
  				year
  			},
  			Integer.class
  		);
  	}
  	
  	//--- Get Attended Session Count ---//
  	@Transactional(readOnly = true)
  	public int getAttendedSessionCount(String sapid, String acadDateFormat) {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT count(*) AS attended_session FROM acads.session_attendance_feedback where sapid = ? and acadDateFormat = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				sapid,
  				acadDateFormat
  			},
  			Integer.class
  		);
  	}
  	
  	//--- Get Total Duration ---//
  	@Transactional(readOnly = true)
  	public List<Integer> getTotalDuration(String sapid, String acadDateFormat) {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT FLOOR(sum(duration) / 3600) AS hours," + 
  				"FLOOR((sum(duration) % 3600) / 60) AS minutes," + 
  				"sum(duration) % 60 AS seconds " + 
  				"FROM acads.webinar_participants_details WHERE sapid = ? AND acadDateFormat = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				sapid, 
  				acadDateFormat
  			}, 
  			(rs, rowNum) -> {
                List<Integer> durationList = new ArrayList<>();
                durationList.add(rs.getInt("hours"));
                durationList.add(rs.getInt("minutes"));
                durationList.add(rs.getInt("seconds"));
                return durationList;
            }
  		);
  	}
  	
  	//--- Get Subject Name And Id ---//
  	public List<Object> getSubjectNameAndId(String pss_id, String month, String year){
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT subject, subjectCodeId FROM acads.sessions s INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId " 
  				+ "WHERE ssm.program_sem_subject_id IN ("+pss_id+") AND s.month = ? AND s.year = ? GROUP BY subject";
  		
  		return jdbcTemplate.query(
  			sql, 
  			new Object[] {
  				month, 
  				year
  			}, 
  			(rs, rowNum) -> {
                Map<String, String> subjectNameAndIdList = new HashMap<>();
                subjectNameAndIdList.put("subject", rs.getString("subject"));
                subjectNameAndIdList.put("subjectCodeId", rs.getString("subjectCodeId"));
                return subjectNameAndIdList;
            }
  		);
  	}
  	
  	//--- Get Subject Total Session Count ---//
  	public int getSubjectTotalSessionCount(String pss_id, String month, String year, String subject_id) {
  	
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT count(*) FROM acads.sessions s INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId " 
  				+ "WHERE ssm.program_sem_subject_id IN ("+pss_id+") AND s.month = ? AND s.year = ? AND subjectCodeId = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				month,
  				year,
  				subject_id
  			}, 
  			Integer.class
  		);
  	}
  	
  	//--- Get Subject Attended Session Count ---//
  	public int getSubjectAttendedSessionCount(String sessionIdList, String month, String year, String subject) {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT count(*) FROM acads.sessions WHERE id in("+sessionIdList+") AND month = ? AND year = ? AND subject = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				month,
  				year,
  				subject
  			}, 
  			Integer.class
  		);
  	}
  	
  	//--- Get Subject Total Duration ---//
  	@Transactional(readOnly = true)
  	public List<Integer> getSubjectTotalDuration(String sapid, String acadDateFormat, String subject_id) {
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT FLOOR(sum(duration) / 3600) AS hours, "
  				+ "FLOOR((sum(duration) % 3600) / 60) AS minutes, "
  				+ "sum(duration) % 60 AS seconds "
  				+ "FROM acads.webinar_participants_details WHERE sapid = ? AND acadDateFormat = ? AND subjectCodeId = ?";
  		
  		return jdbcTemplate.queryForObject(
  			sql, 
  			new Object[] {
  				sapid, 
  				acadDateFormat,
  				subject_id
  			}, 
  			(rs, rowNum) -> {
                List<Integer> durationList = new ArrayList<>();
                durationList.add(rs.getInt("hours"));
                durationList.add(rs.getInt("minutes"));
                durationList.add(rs.getInt("seconds"));
                return durationList;
            }
  		);
  	}
  	
  	//--- Get Student Current Subject ---//
  	@Transactional(readOnly = true)
  	public List<VideoAndSessionAttendanceCountStudentBean> getStudentCurrentSubject(String sapid, String month, String year){
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT subject AS subject_name FROM exam.student_current_subject where sapid = ? and month = ? and year = ?";
  		
  		return jdbcTemplate.query(
  			sql, 
  			new Object[] {
  				sapid,
  				month,
    			year
  			},
  			new BeanPropertyRowMapper<VideoAndSessionAttendanceCountStudentBean>(VideoAndSessionAttendanceCountStudentBean.class)
  		);
  	}
  	
  	//--- Get Total Video Count ---//
  	public List<TotalVideoDetailsStudentBean> getTotalVideoCount(String sapid, String month, String year){
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT vc.subject AS subject_name, count(*) AS total_attempt, max(vr.lastViewedDuration) AS total_duration " 
  				+ "FROM analytics.vimeo_recordings_progress vr INNER JOIN acads.video_content vc ON vc.id = vr.videoContentId " 
  				+ "WHERE userId = ? AND month = ? AND year = ? GROUP BY vc.subject";
  		
  		return jdbcTemplate.query(
  			sql, 
  			new Object[] {
  				sapid,
  				month,
  				year
  			},
  			new BeanPropertyRowMapper<TotalVideoDetailsStudentBean>(TotalVideoDetailsStudentBean.class)
  		);
  	}
  	
  	//--- Get Student Current Subject By PssId ---//
  	public List<PdfReadDetailsDto> getStudentCurrentSubjectByPssId(String pss_id, String month, String year){
  		
  		jdbcTemplate = new JdbcTemplate(analyticsDataSource);
  		
  		String sql = "SELECT subject AS subject_name, count(*) As total_pdf FROM acads.content_denormalized "
  			+ "WHERE programSemSubjectId IN("+pss_id+") "
  			+ "AND month = ? AND year = ? GROUP BY programSemSubjectId";
  		
  		return jdbcTemplate.query(
  			sql, 
  			new Object[] {
  				month,
  				year
  			}, 
  			new BeanPropertyRowMapper<PdfReadDetailsDto>(PdfReadDetailsDto.class)
  		);
  	}
  	
}