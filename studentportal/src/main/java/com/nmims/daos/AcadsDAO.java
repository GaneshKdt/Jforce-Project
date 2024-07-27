/**
	 * Author: sagar shinde
	 * Date: 26th Oct 2018
	 * */
package com.nmims.daos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ELearnResourcesStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.SessionDayTimeStudentPortal;

@Component
public class AcadsDAO {
	
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
	      this.dataSource = dataSource;
	      this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Transactional(readOnly = true)
	public int getNotAnsQueryCount(String facultyId,String year,String month) {
		try {
			//System.out.println("year : " + year + " | month : " + month); 
			// added static year month by Abhay 
			String SQL = "Select count(id) from acads.session_query_answer where  assignedToFacultyId = ? and year in (2022,2023) and month in ('Jan', 'Jul') and queryType <> 'Technical' and isAnswered = 'N'";
			return (int) jdbcTemplate.queryForObject(SQL,new Object[] {facultyId },Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}
	
	@Transactional(readOnly = true)
	public int getAssignmentNotRevalutedCount(String facultyId,String year,String month) {
		try {
			String SQL = "SELECT count(*) FROM exam.assignmentsubmission where year=? and revaluated = 'N' and ((faculty3 IS NULL and faculty2 = ?) or (faculty3 = ?));"; 
			return (int) jdbcTemplate.queryForObject(SQL, new Object[] {year,facultyId,facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}
	
	public int getAssignmentNotEvaluatedCount(String facultyId) {
		try {
			final String sql = "Select year, month from exam.examorder where examorder.order = (Select min(examorder.order) from exam.examorder where live='N')";
			jdbcTemplate = new JdbcTemplate(dataSource);
			ExamOrderStudentPortalBean exam_order_bean = jdbcTemplate.queryForObject(sql,new Object[]{},new BeanPropertyRowMapper<ExamOrderStudentPortalBean>(ExamOrderStudentPortalBean.class));
			String SQL = "SELECT count(*) FROM exam.assignmentsubmission where year = ? and month = ? and facultyId = ? and evaluated = 'N';";
			return (int) jdbcTemplate.queryForObject(SQL, new Object[] {exam_order_bean.getYear(), exam_order_bean.getMonth(), facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}
	
	
	@Transactional(readOnly = true)
	public int getProjectNotRevalutedCount(String facultyId,String year,String month) {
		try {
			String SQL = "SELECT count(*) FROM exam.projectsubmission where year=? and revaluated = 'N' and facultyIdRevaluation = ?;"; 
			return (int) jdbcTemplate.queryForObject(SQL, new Object[] {year,facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}
	
	@Transactional(readOnly = true)
	public int getProjectNotEvaluatedCount(String facultyId,String year,String month) {
		try {
			String SQL = "SELECT count(*) FROM exam.projectsubmission where year=? and facultyId = ? and evaluated = 'N';";
			return (int) jdbcTemplate.queryForObject(SQL, new Object[] {year,facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SessionDayTimeStudentPortal> getUpComingSessions(String facultyId){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		//System.out.println("*********** system timing ********************");
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		try {
			String SQL =  " SELECT id,date,startTime,endTime,day,subject,track FROM acads.sessions "
						+ " WHERE facultyId = ? AND (isCancelled = 'N' or isCancelled is null) "
						+ " AND timestamp(date, startTime) > ? order by date,startTime asc limit 4 ";
			return jdbcTemplate.query(SQL, new Object[] {facultyId,dateFormat.format(date)},new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public int getCaseStudyNotEvaluatedCount(String facultyId) {
		try {
			String SQL = "SELECT count(*) FROM exam.case_study_submission where facultyId = ? and evaluated = 'N';";
			return (int) jdbcTemplate.queryForObject(SQL, new Object[] {facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			return 0;
		}
	}


	@Transactional(readOnly = true)
	public int getSessionQueriesNotAnsweredCount(String facultyId) {
		int totalCount = 0;
		int parallelAndMainSessionCount = 0;
		int mirrorSessionCount = 0;
		try {
//			String SQL = "SELECT count(q.query) FROM acads.sessions s, exam.examorder eo, acads.session_question_answer q "
//					+ " where  s.month = eo.acadmonth and s.year = eo.year "
//					+ " and eo.order in (select examorder.order from exam.examorder where acadSessionLive = 'Y') "
//					+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )    "
//					+ "and s.id=q.sessionId and q.status='Open' and LENGTH(q.query) > 10"; 
			
			String sql = " SELECT  " + 
					"    COUNT(q.query) " + 
					"FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    (SELECT  " + 
					"        o.order, o.acadmonth, o.year " + 
					"    FROM " + 
					"        exam.examorder o " + 
					"    WHERE " + 
					"        o.acadSessionLive = 'Y' " + 
					"    ORDER BY o.order DESC " + 
					"    LIMIT 2) eo ON s.month = eo.acadmonth " + 
					"        AND s.year = eo.year " + 
					"        INNER JOIN " + 
					"    acads.session_question_answer q ON s.id = q.sessionId " + 
					"WHERE " + 
					" ((s.facultyId = ?  AND s.meetingKey = q.meetingKey) " + 
					" OR (s.altFacultyId = ? AND s.altMeetingKey = q.meetingKey) " + 
					" OR (s.altFacultyId2 = ? AND s.altMeetingKey2 = q.meetingKey) " + 
					" OR (s.altFacultyId3 = ? AND s.altMeetingKey3 = q.meetingKey) " + 
					"    ) AND q.status = 'Open'  " + 
					"AND LENGTH(q.query) > 10 ";
			
			parallelAndMainSessionCount  = (int) jdbcTemplate.queryForObject(sql, new Object[] {facultyId,facultyId,facultyId,facultyId},Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			parallelAndMainSessionCount  = 0;
		}
		
		try {
			
			String sql = " SELECT  " + 
					"    COUNT(q.query) " + 
					"FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    (SELECT  " + 
					"        o.order, o.acadmonth, o.year " + 
					"    FROM " + 
					"        exam.examorder o " + 
					"    WHERE " + 
					"        o.acadSessionLive = 'Y' " + 
					"    ORDER BY o.order DESC " + 
					"    LIMIT 2) eo ON s.month = eo.acadmonth " + 
					"        AND s.year = eo.year " + 
					"        INNER JOIN " + 
					"    acads.session_question_answer q ON s.id = q.sessionId " + 
					"WHERE " + 
					" s.facultyId = ? " + 
					"AND ((s.altFacultyId IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey = q.meetingKey) " + 
					" OR (s.altFacultyId2 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey2 = q.meetingKey) " + 
					" OR (s.altFacultyId3 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey3 = q.meetingKey)) " + 
					"AND q.status = 'Open' AND LENGTH(q.query) > 10 ";
			
			mirrorSessionCount = (int) jdbcTemplate.queryForObject(sql, new Object[] {facultyId },Integer.class);
		}
		catch(Exception e) {
			//System.out.println("*********** Exception ********************");
			//System.out.println(e.getMessage());
			mirrorSessionCount = 0;
		}
		totalCount = parallelAndMainSessionCount + mirrorSessionCount;
		return totalCount;
	}
	
	@Transactional(readOnly = true)
	public ELearnResourcesStudentPortalBean isStukentApplicable(String userId) {
		ELearnResourcesStudentPortalBean eLearnResourcesBean = new ELearnResourcesStudentPortalBean();
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
		String sql = " SELECT " + 
				"    count(u.id) as userId_count, " + 
				"    u.roles as roles, " + 
				"    p.name as provider_name " + 
				" FROM " + 
				"    lti.lti_users u " + 
				"        INNER JOIN " + 
				"    lti.lti_user_resourse_mapping urm ON u.id = urm.userId " + 
				"        INNER JOIN " + 
				"    lti.lti_resources r ON r.id = urm.resourceId " + 
				"        INNER JOIN " + 
				"    lti.lti_providers p ON r.providerId = p.id " + 
				" WHERE " + 
				"    u.userId = ? AND u.roles = 'Instructor' AND p.name ='Stukent' group by u.roles, p.name";
		 eLearnResourcesBean =  (ELearnResourcesStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper (ELearnResourcesStudentPortalBean.class) );
		
		 return eLearnResourcesBean;
		}catch(Exception e) {
			
			return eLearnResourcesBean;
		}
	}
	
	@Transactional(readOnly = true)
	public ELearnResourcesStudentPortalBean isHarvardApplicable(String userId) {
		ELearnResourcesStudentPortalBean eLearnResourcesBean = new ELearnResourcesStudentPortalBean();
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
		String sql = " SELECT " +  
				"    count(u.id) as userId_count, " + 
				"    u.roles as roles, " + 
				"    p.name as provider_name " + 
				" FROM " + 
				"    lti.lti_users u " + 
				"        INNER JOIN " + 
				"    lti.lti_user_resourse_mapping urm ON u.id = urm.userId " + 
				"        INNER JOIN " + 
				"    lti.lti_resources r ON r.id = urm.resourceId " + 
				"        INNER JOIN " + 
				"    lti.lti_providers p ON r.providerId = p.id " + 
				" WHERE " + 
				"    u.userId = ? AND (u.roles = 'Instructor' OR u.roles = 'Administrator' ) AND ( p.name ='Harvard' OR p.name = 'Capsim') group by u.roles, p.name ";
		 eLearnResourcesBean =  (ELearnResourcesStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(ELearnResourcesStudentPortalBean.class) );
		 return eLearnResourcesBean;
		}catch(Exception e) {
			
			return eLearnResourcesBean;
		}
	}
}
