package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.LoginBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionCountBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.helpers.PaginationHelper;
import com.nmims.util.ContentUtil;

public class AttendanceFeedbackDAO extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	private NamedParameterJdbcTemplate nameJdbcTemplate;
	
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

	@Transactional(readOnly = false)
	public void recordAttendance(SessionDayTimeAcadsBean session, String sapId, String facultyId, String joinUrl) {
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		String sql = "INSERT INTO acads.session_attendance_feedback(sapId, sessionId, attended, attendTime, meetingKey, meetingPwd, facultyId, device, joinurl, acadDateFormat) VALUES "
				+ "(?,?,'Y', sysdate(),?,?,?,?,?,?)"
				+ " on duplicate key "
				+ " update "
				+ " facultyId = ?,"
				+ " meetingKey = ?, "
				+ " meetingPwd = ? , "
				+ "	reAttendTime = sysdate(), "
				+ " device = ? ";


		String sessionId = session.getId();
		

		jdbcTemplate.update(sql, new Object[] { 
				sapId,
				sessionId,
				session.getMeetingKey(),
				session.getMeetingPwd(),
				facultyId,
				session.getDevice(),
				joinUrl,
				acadDateFormat,
				//Duplicate
				facultyId,
				session.getMeetingKey(),
				session.getMeetingPwd(),
				session.getDevice()
		});
		
	}
	
	@Transactional(readOnly = true)
	public PageAcads<LoginBean> getStudentLogins(int pageNo, int pageSize, LoginBean searchBean) {

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select pl.sapid,pl.logintime ,es.enrollmentMonth, es.enrollmentYear from portal.logins pl,exam.students es where pl.sapid=es.sapid";
		
		String countSql = "select count(*) from portal.logins pl,exam.students es where pl.sapid=es.sapid";
		
		

		if( searchBean.getSapid() != null  &&  !("".equals(searchBean.getSapid()))){
			sql = sql + " and pl.sapid =  ? ";
			countSql = countSql + " and pl.sapid =  ? ";
			parameters.add(searchBean.getSapid());
		}
		
		
		

		Object[] args = parameters.toArray();
		sql = sql +" order by pl.logintime desc";
		PaginationHelper<LoginBean> pagingHelper = new PaginationHelper<LoginBean>();
		PageAcads<LoginBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(LoginBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,SessionAttendanceFeedbackAcads> getMapOfSessionIdAndFeedBackBean(){
		String sql = " select  sessionId,ROUND(AVG(q1Response),2) as q1Average,ROUND(AVG(q2Response),2) as q2Average,ROUND(AVG(q3Response),2) as q3Average, "
				+ "ROUND( AVG(q4Response),2) as q4Average,ROUND(AVG(q5Response),2) as q5Average,ROUND(AVG(q6Response),2) as q6Average,ROUND(AVG(q7Response),2) as q7Average, "
				+ " ROUND(AVG(q8Response),2) as q8Average from acads.session_attendance_feedback group by sessionId ";
		
		ArrayList<SessionAttendanceFeedbackAcads> attendanceList = (ArrayList<SessionAttendanceFeedbackAcads>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSessFeedbackBean = new HashMap<String,SessionAttendanceFeedbackAcads>();
		for(SessionAttendanceFeedbackAcads bean : attendanceList)
		{
			mapOfSessFeedbackBean.put(bean.getSessionId(), bean);
		}
		
		return mapOfSessFeedbackBean;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,SessionAttendanceFeedbackAcads> getMapOfSubjectFacultySessionWiseAverage(SessionAttendanceFeedbackAcads searchBean, HttpServletRequest request){
		String sql =  " SELECT COUNT(s.id) AS totalResponse,s.id as sessionId,s.hasModuleId,s.corporateName,s.subject, s.sessionName, s.date, f.facultyId, "
					+ "  f.firstName as facultyFirstName, f.lastName as facultyLastName, s.sem, s.programlist, "
					+ " 	ROUND(AVG(saf.q1Response),2) as q1Average, "
					+ " 	ROUND(AVG(saf.q2Response),2) as q2Average, "
					+ " 	ROUND(AVG(saf.q3Response),2) as q3Average, "
					+ " 	ROUND(AVG(saf.q4Response),2) as q4Average, "
					+ " 	ROUND(AVG(saf.q5Response),2) as q5Average, "
					+ " 	ROUND(AVG(saf.q6Response),2) as q6Average, "
					+ " 	ROUND(AVG(saf.q7Response),2) as q7Average, "
					+ " 	ROUND(AVG(saf.q8Response),2) as q8Average, "
					+ " 	ROUND(((AVG(saf.q1Response)+AVG(saf.q2Response)+AVG(saf.q3Response)+AVG(saf.q4Response)+AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/8),2) as grandSessionAverage , "
					+ " 	ROUND (((AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/4),2) as grandFacultyAverage "
					+ " FROM acads.session_attendance_feedback saf"
					+ "		INNER JOIN acads.sessions s ON saf.sessionId = s.Id "
					+ "		LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+ " WHERE saf.feedbackgiven = 'Y' AND saf.studentConfirmationForAttendance = 'Y' ";
				
		ArrayList<String> parameters = new ArrayList<String>();
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		
		if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
		{
			sql += " and s.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth()))
		{
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			sql += " and year = ? and month = ? ";
//			sql +=  "AND s.date > DATE_FORMAT(STR_TO_DATE(CONCAT(?, ?, '01'), '%Y %M %d'),'%Y-%m-%d')";
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
			sql = sql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
		}
		
		sql += " group by s.Id,saf.facultyId";
		
		Object [] args = parameters.toArray();
		
		ArrayList<SessionAttendanceFeedbackAcads> attendanceList = (ArrayList<SessionAttendanceFeedbackAcads>) jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		request.getSession().setAttribute("sessionList", attendanceList);
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSessFeedbackBean = new HashMap<String,SessionAttendanceFeedbackAcads>();
		for(SessionAttendanceFeedbackAcads bean : attendanceList)
		{
			mapOfSessFeedbackBean.put(bean.getSessionId()+"-"+bean.getFacultyId(), bean);
		}
		
		return mapOfSessFeedbackBean;
	}
	
	@Transactional(readOnly = true)
	public PageAcads<SessionAttendanceFeedbackAcads> getAttendance(int pageNo, int pageSize, SessionAttendanceFeedbackAcads searchBean,String authorizedCenterCodes) {

		ArrayList<Object> parameters = new ArrayList<Object>();

		/*Old query
		 * String sql = "select st.firstname, st.lastname, s.year, s.month, s.subject, s.sessionName, saf.*,"
				+ " f.firstName as facultyFirstName, f.lastName as facultyLastName, f.facultyId "
				+ " from acads.session_attendance_feedback saf, acads.sessions s, exam.students st, acads.faculty f "
				+ " where saf.sessionId = s.id "
				+ " and year = ? and month = ? "
				+ " and saf.sapid = st.sapid "
				+ " and saf.facultyId = f.facultyId";*/
		String sql = "SELECT "
					+"		st.firstname," 
					+"	    st.lastname, "
					+"	    r.year, "
					+"	    r.month, "
					+"	    s.subject, " 
					+"	    s.sessionName, "
					+"		s.date,"
					+"		r.program, "
					+"    	r.sem, "
					+"	    saf.*, "
					+"		f.firstName as facultyFirstName," 
					+"	    f.lastName as facultyLastName, "
					+"	    f.facultyId,"
					+"	    st.consumerType,"
					+"	    s.track,"
					+"	    saf.device"
					+"	FROM "
					+"		acads.session_attendance_feedback saf " 
					+"	    INNER JOIN acads.sessions s ON saf.sessionId = s.id "
					+"	    INNER JOIN exam.students st ON saf.sapid = st.sapid "
					+"	    LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+" 		INNER JOIN exam.registration r ON saf.sapid = r.sapid ";
			
					if (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Jan")) {
							sql = sql +" AND s.year = r.year AND  s.month = r.month ";
					}
					sql = sql +" WHERE 1 = 1 ";
					
		
		String countSql = "SELECT COUNT(*) "
				+ " FROM acads.session_attendance_feedback saf "
				+"	    INNER JOIN acads.sessions s ON saf.sessionId = s.id "
				+"	    INNER JOIN exam.students st ON saf.sapid = st.sapid "
				+"	    LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
				+" 		INNER JOIN exam.registration r ON saf.sapid = r.sapid ";
				
				if (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Jan")) {
					countSql = countSql +" AND s.year = r.year AND  s.month = r.month ";
				}
				countSql = countSql +" WHERE 1 = 1 ";
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth())){
			
			if ("Y".equalsIgnoreCase(searchBean.getHasModuleId()) && (searchBean.getMonth().equalsIgnoreCase("Apr") || searchBean.getMonth().equalsIgnoreCase("Oct"))) {
				sql = sql + " AND r.year = ? AND r.month = ? ";
				countSql = countSql + " AND r.year = ? AND r.month = ? ";
				
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
				
				//Added For MBA-WX Check Start date and End date
				sql = sql + " AND s.date BETWEEN (SELECT MIN(startDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? )"
						  + " 				AND (SELECT MAX(endDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? ) ";
				countSql = countSql + " AND s.date BETWEEN (SELECT MIN(startDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? )"
						  			+ " 			AND (SELECT MAX(endDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? ) ";
				
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
			}
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			
			sql = sql + " AND s.year = ? AND s.month = ? ";
			countSql = countSql + " AND s.year = ? AND s.month = ? ";
			
//			sql = sql + " AND s.date > DATE_FORMAT(STR_TO_DATE(CONCAT(?, ?, '01'), '%Y %M %d'),'%Y-%m-%d') ";
//			countSql = countSql + " AND s.date > DATE_FORMAT(STR_TO_DATE(CONCAT(?, ?, '01'), '%Y %M %d'),'%Y-%m-%d') ";
			
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
		
			sql = sql + " AND (s.moduleid IS NOT NULL or s.moduleid !='') ";
			countSql = countSql + " and (s.moduleid IS NOT NULL or s.moduleid != '') ";
		//	sql = sql + " and s.hasModuleId = 'Y' ";
		//	countSql = countSql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.moduleid IS NULL or s.moduleid = '') ";
			countSql = countSql + " and (s.moduleid IS NULL or s.moduleid = '') ";
//			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
//			countSql = countSql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}

		if( searchBean.getSubject() != null  &&  !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject =  ? ";
			countSql = countSql + " and subject =  ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getFacultyFullName() != null  &&  !("".equals(searchBean.getFacultyFullName())))
		{
			sql += " and f.facultyId = ? ";
			countSql = countSql + " and f.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			countSql = countSql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
			countSql = countSql + " AND s.isCommon = 'Y' ";
		}
		
		/*
		 * added by stef on 6-Nov
		 * if( searchBean.getSessionName() != null  &&  !("".equals(searchBean.getSessionName()))){
			sql = sql + " and sessionName =  ? ";
			countSql = countSql + " and sessionName =  ? ";
			parameters.add(searchBean.getSessionName());
		}*/
		
		sql = sql + " order by subject, sessionName asc";

		Object[] args = parameters.toArray();

		
		PaginationHelper<SessionAttendanceFeedbackAcads> pagingHelper = new PaginationHelper<SessionAttendanceFeedbackAcads>();
		PageAcads<SessionAttendanceFeedbackAcads> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));


		return page;
	}

	@Transactional(readOnly = true)
	public List<SessionAttendanceFeedbackAcads> getFacultyFeedbackForSession(String id,String facultyId) {
		List<SessionAttendanceFeedbackAcads> facultyFeedbackList = null;
		String sql ="";
		//Made conditional since this Id should be able to view all the feedbacks associated with session//
		if("HODAcads".equals(facultyId)){
			sql = "Select saf.*, s.subject, s.sessionName from acads.session_attendance_feedback saf, acads.sessions s"
					+ " where saf.sessionId = s.id  and  sessionid = ? and feedbackGiven = 'Y' and saf.studentConfirmationForAttendance = 'Y' ";
			facultyFeedbackList = jdbcTemplate.query(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		}else{
			sql = "Select saf.*, s.subject, s.sessionName from acads.session_attendance_feedback saf, acads.sessions s"
					+ " where saf.sessionId = s.id  and  sessionid = ? and feedbackGiven = 'Y' and saf.facultyId = ? and studentConfirmationForAttendance = 'Y'  ";
			facultyFeedbackList = jdbcTemplate.query(sql, new Object[]{id,facultyId}, new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		}
		
		return facultyFeedbackList;
	}
	
	@Transactional(readOnly = true)
	public List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverage(SessionAttendanceFeedbackAcads searchBean){
		String sql =  " SELECT "
					+ " 	ROUND((AVG(saf.q1Response)),2) as q1Average, "
					+ " 	ROUND((AVG(saf.q2Response)),2) as q2Average, "
					+ " 	ROUND((AVG(saf.q3Response)),2) as q3Average, "
					+ " 	ROUND((AVG(saf.q4Response)),2) as q4Average, "
					+ " 	ROUND((AVG(saf.q5Response)),2) as q5Average, "
					+ " 	ROUND((AVG(saf.q6Response)),2) as q6Average, "
					+ " 	ROUND((AVG(saf.q7Response)),2) as q7Average, "
					+ " 	ROUND((AVG(saf.q8Response)),2) as q8Average "
					+ " FROM "
					+ "		acads.session_attendance_feedback saf"
					+ " 	INNER JOIN acads.sessions s ON saf.sessionId = s.id "
					+ " 	LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+ " WHERE "
					+ " 	saf.feedbackgiven = 'Y' "
					+ " AND saf.studentConfirmationForAttendance = 'Y' " ;
		
		ArrayList<String> parameters = new ArrayList<String>();
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
		{
			sql += " and f.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth()))
		{
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			sql += " and year = ? and month = ? ";
//			sql +=  "AND s.date > DATE_FORMAT(STR_TO_DATE(CONCAT(?, ?, '01'), '%Y %M %d'),'%Y-%m-%d')";
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
			sql = sql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
		}
		
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " group by s.subject";
		}
		
		if(!StringUtils.isBlank(searchBean.getFacultyLastName()) || !StringUtils.isBlank(searchBean.getFacultyFirstName()))
		{
			sql += " group by f.firstName, f.LastName";
		}
		
		
		Object [] args = parameters.toArray();
			
		List<SessionAttendanceFeedbackAcads>	facultyFeedbackList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		return facultyFeedbackList;
	}

	@Transactional(readOnly = true)
	public PageAcads<SessionAttendanceFeedbackAcads> getAttendanceForMbaWx(int pageNo, int pageSize, SessionAttendanceFeedbackAcads searchBean,String authorizedCenterCodes) {

		ArrayList<Object> parameters = new ArrayList<Object>();

		/*Old query
		 * String sql = "select st.firstname, st.lastname, s.year, s.month, s.subject, s.sessionName, saf.*,"
				+ " f.firstName as facultyFirstName, f.lastName as facultyLastName, f.facultyId "
				+ " from acads.session_attendance_feedback saf, acads.sessions s, exam.students st, acads.faculty f "
				+ " where saf.sessionId = s.id "
				+ " and year = ? and month = ? "
				+ " and saf.sapid = st.sapid "
				+ " and saf.facultyId = f.facultyId";*/
		
		String sql = "SELECT " + 
				"    st.firstname," + 
				"    st.lastname," + 
				"    s.year," + 
				"    s.month," + 
				"    s.subject," + 
				"    s.sessionName," + 
				"    saf.*," + 
				"    f.firstName AS facultyFirstName," + 
				"    f.lastName AS facultyLastName," + 
				"    f.facultyId," + 
				"    st.consumerType," + 
				"    s.track," + 
				"    s.hasModuleId," + 
				"    s.moduleid," + 
				"    saf.device " + 
				"FROM" + 
				"    acads.session_attendance_feedback saf" + 
				"        INNER JOIN" + 
				"    acads.sessions s ON saf.sessionId = s.id" + 
				"        INNER JOIN" + 
				"    exam.students st ON saf.sapid = st.sapid" + 
				"        INNER JOIN" + 
				"    acads.faculty f ON saf.facultyId = f.facultyId " + 
				"WHERE" +
				"        s.year  = ?" + 
				"        AND s.month  = ?" + 
				"        AND (s.moduleid IS NOT NULL or s.moduleid != '' ) " ;
		String countSql = "select count(*) "
				+ " from acads.session_attendance_feedback saf, acads.sessions s, exam.students st, acads.faculty f "
				+ " where saf.sessionId = s.id "
				+ " and year = ? and month = ? "
				+ " and saf.sapid = st.sapid "
				+ " and saf.facultyId = f.facultyId "+
				"   and (s.moduleid IS NOT NULL or s.moduleid != '' ) " ;

		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
		}
		parameters.add(searchBean.getYear());
		parameters.add(searchBean.getMonth());

		if( searchBean.getSubject() != null  &&  !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject =  ? ";
			countSql = countSql + " and subject =  ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getFacultyFullName() != null  &&  !("".equals(searchBean.getFacultyFullName())))
		{
			sql += " and f.facultyId = ? ";
			countSql = countSql + " and f.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		sql = sql + " order by subject, sessionName asc";
		Object[] args = parameters.toArray();

		PaginationHelper<SessionAttendanceFeedbackAcads> pagingHelper = new PaginationHelper<SessionAttendanceFeedbackAcads>();
		PageAcads<SessionAttendanceFeedbackAcads> page;
		try {
			page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
			return page;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			return  page = new PageAcads<SessionAttendanceFeedbackAcads>();
		}

		
	}
	
	//Session History
	@Transactional(readOnly = true)
	public PageAcads<SessionAttendanceFeedbackAcads> getAttendanceFromHistory(int pageNo, int pageSize, SessionAttendanceFeedbackAcads searchBean,String authorizedCenterCodes) {

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT "
					+"		st.firstname," 
					+"	    st.lastname, "
					+"	    r.year, "
					+"	    r.month, "
					+"	    s.subject, " 
					+"	    s.sessionName, "
					+"		s.date,"
					+"		r.program, "
					+"    	r.sem, "
					+"	    saf.*, "
					+"		f.firstName as facultyFirstName," 
					+"	    f.lastName as facultyLastName, "
					+"	    f.facultyId,"
					+"	    st.consumerType,"
					+"	    s.track,"
					+"	    saf.device"
					+"	FROM "
					+"		acads.session_attendance_feedback_history saf " 
					+"	    INNER JOIN acads.sessions_history s ON saf.sessionId = s.id "
					+"	    INNER JOIN exam.students st ON saf.sapid = st.sapid "
					+"	    LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+" 		INNER JOIN exam.registration r ON saf.sapid = r.sapid ";
			
					if (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Jan")) {
							sql = sql +" AND s.year = r.year AND  s.month = r.month ";
					}
					sql = sql +" WHERE 1 = 1 ";
					
		
		String countSql = "SELECT COUNT(*) "
				+ " FROM acads.session_attendance_feedback_history saf "
				+"	    INNER JOIN acads.sessions_history s ON saf.sessionId = s.id "
				+"	    INNER JOIN exam.students st ON saf.sapid = st.sapid "
				+"	    LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
				+" 		INNER JOIN exam.registration r ON saf.sapid = r.sapid ";
				
				if (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Jan")) {
					countSql = countSql +" AND s.year = r.year AND  s.month = r.month ";
				}
				countSql = countSql +" WHERE 1 = 1 ";
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth())){
			
			if ("Y".equalsIgnoreCase(searchBean.getHasModuleId()) && (searchBean.getMonth().equalsIgnoreCase("Apr") || searchBean.getMonth().equalsIgnoreCase("Oct"))) {
				sql = sql + " AND r.year = ? AND r.month = ? ";
				countSql = countSql + " AND r.year = ? AND r.month = ? ";
				
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
				
				//Added For MBA-WX Check Start date and End date
				sql = sql + " AND s.date BETWEEN (SELECT MIN(startDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? )"
						  + " 				AND (SELECT MAX(endDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? ) ";
				countSql = countSql + " AND s.date BETWEEN (SELECT MIN(startDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? )"
						  			+ " 			AND (SELECT MAX(endDate) FROM lti.student_subject_config WHERE acadYear = ? AND acadMonth = ? ) ";
				
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
				parameters.add(searchBean.getYear());
				parameters.add(searchBean.getMonth());
			}
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			
			sql = sql + " AND s.year = ? AND s.month = ? ";
			countSql = countSql + " AND s.year = ? AND s.month = ? ";
			
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
		
			sql = sql + " AND (s.moduleid IS NOT NULL or s.moduleid !='') ";
			countSql = countSql + " and (s.moduleid IS NOT NULL or s.moduleid != '') ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.moduleid IS NULL or s.moduleid = '') ";
			countSql = countSql + " and (s.moduleid IS NULL or s.moduleid = '') ";
		}

		if( searchBean.getSubject() != null  &&  !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject =  ? ";
			countSql = countSql + " and subject =  ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getFacultyFullName() != null  &&  !("".equals(searchBean.getFacultyFullName()))){
			sql += " and f.facultyId = ? ";
			countSql = countSql + " and f.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			countSql = countSql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
			countSql = countSql + " AND s.isCommon = 'Y' ";
		}
		
		sql = sql + " order by subject, sessionName asc";

		Object[] args = parameters.toArray();

		PaginationHelper<SessionAttendanceFeedbackAcads> pagingHelper = new PaginationHelper<SessionAttendanceFeedbackAcads>();
		PageAcads<SessionAttendanceFeedbackAcads> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverageFromHistory(SessionAttendanceFeedbackAcads searchBean){
		String sql =  " SELECT "
					+ " 	ROUND((AVG(saf.q1Response)),2) as q1Average, "
					+ " 	ROUND((AVG(saf.q2Response)),2) as q2Average, "
					+ " 	ROUND((AVG(saf.q3Response)),2) as q3Average, "
					+ " 	ROUND((AVG(saf.q4Response)),2) as q4Average, "
					+ " 	ROUND((AVG(saf.q5Response)),2) as q5Average, "
					+ " 	ROUND((AVG(saf.q6Response)),2) as q6Average, "
					+ " 	ROUND((AVG(saf.q7Response)),2) as q7Average, "
					+ " 	ROUND((AVG(saf.q8Response)),2) as q8Average "
					+ " FROM "
					+ "		acads.session_attendance_feedback_history saf"
					+ " 	INNER JOIN acads.sessions_history s ON saf.sessionId = s.id "
					+ " 	LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+ " WHERE "
					+ " 	saf.feedbackgiven = 'Y' "
					+ " AND saf.studentConfirmationForAttendance = 'Y' " ;
		
		ArrayList<String> parameters = new ArrayList<String>();
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
		{
			sql += " and f.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth()))
		{
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			sql += " and year = ? and month = ? ";
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
			sql = sql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
		}
		
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " group by s.subject";
		}
		
		if(!StringUtils.isBlank(searchBean.getFacultyLastName()) || !StringUtils.isBlank(searchBean.getFacultyFirstName()))
		{
			sql += " group by f.firstName, f.LastName";
		}
		
		Object [] args = parameters.toArray();
			
		List<SessionAttendanceFeedbackAcads>	facultyFeedbackList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		return facultyFeedbackList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,SessionAttendanceFeedbackAcads> getMapOfSubjectFacultySessionWiseAverageFromHistory(SessionAttendanceFeedbackAcads searchBean, HttpServletRequest request){
		String sql =  " SELECT COUNT(s.id) AS totalResponse,s.id as sessionId,s.corporateName,s.subject, s.sessionName, s.date, f.facultyId, "
					+ "  f.firstName as facultyFirstName, f.lastName as facultyLastName, "
					+ " 	ROUND(AVG(saf.q1Response),2) as q1Average, "
					+ " 	ROUND(AVG(saf.q2Response),2) as q2Average, "
					+ " 	ROUND(AVG(saf.q3Response),2) as q3Average, "
					+ " 	ROUND(AVG(saf.q4Response),2) as q4Average, "
					+ " 	ROUND(AVG(saf.q5Response),2) as q5Average, "
					+ " 	ROUND(AVG(saf.q6Response),2) as q6Average, "
					+ " 	ROUND(AVG(saf.q7Response),2) as q7Average, "
					+ " 	ROUND(AVG(saf.q8Response),2) as q8Average, "
					+ " 	ROUND(((AVG(saf.q1Response)+AVG(saf.q2Response)+AVG(saf.q3Response)+AVG(saf.q4Response)+AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/8),2) as grandSessionAverage , "
					+ " 	ROUND (((AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/4),2) as grandFacultyAverage "
					+ " FROM acads.session_attendance_feedback_history saf"
					+ "		INNER JOIN acads.sessions_history s ON saf.sessionId = s.Id "
					+ "		LEFT JOIN acads.faculty f ON saf.facultyId = f.facultyId "
					+ " WHERE saf.feedbackgiven = 'Y' AND saf.studentConfirmationForAttendance = 'Y' ";
				
		ArrayList<String> parameters = new ArrayList<String>();
		if(!StringUtils.isBlank(searchBean.getSubject()))
		{
			sql += " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		
		if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
		{
			sql += " and s.facultyId = ? ";
			parameters.add(searchBean.getFacultyFullName());
		}
		
		if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth()))
		{
			//For MBA-WX
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
			sql += " and year = ? and month = ? ";
			parameters.add(searchBean.getYear());
			parameters.add(searchBean.getMonth());
		}
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
			sql = sql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}
		
		if (!StringUtils.isBlank(searchBean.getDate())) {
			sql = sql + " AND s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		
		if (!StringUtils.isBlank(searchBean.getIsCommon()) && searchBean.getIsCommon().equalsIgnoreCase("Y")) {
			sql = sql + " AND s.isCommon = 'Y' ";
		}
		
		sql += " group by s.Id,saf.facultyId";
		
		Object [] args = parameters.toArray();
		
		ArrayList<SessionAttendanceFeedbackAcads> attendanceList = (ArrayList<SessionAttendanceFeedbackAcads>) jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		request.getSession().setAttribute("sessionList", attendanceList);
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSessFeedbackBean = new HashMap<String,SessionAttendanceFeedbackAcads>();
		for(SessionAttendanceFeedbackAcads bean : attendanceList)
		{
			mapOfSessFeedbackBean.put(bean.getSessionId()+"-"+bean.getFacultyId(), bean);
		}
		
		return mapOfSessFeedbackBean;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,SessionAttendanceFeedbackAcads> getMapOfSessionIdAndFeedBackBeanFromHistory(){
		String sql =  " select  sessionId,ROUND(AVG(q1Response),2) as q1Average,ROUND(AVG(q2Response),2) as q2Average,ROUND(AVG(q3Response),2) as q3Average, "
					+ " ROUND( AVG(q4Response),2) as q4Average,ROUND(AVG(q5Response),2) as q5Average,ROUND(AVG(q6Response),2) as q6Average,ROUND(AVG(q7Response),2) as q7Average, "
					+ " ROUND(AVG(q8Response),2) as q8Average from acads.session_attendance_feedback_history group by sessionId ";
		
		ArrayList<SessionAttendanceFeedbackAcads> attendanceList = (ArrayList<SessionAttendanceFeedbackAcads>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSessFeedbackBean = new HashMap<String,SessionAttendanceFeedbackAcads>();
		for(SessionAttendanceFeedbackAcads bean : attendanceList){
			mapOfSessFeedbackBean.put(bean.getSessionId(), bean);
		}
		
		return mapOfSessFeedbackBean;
	}
	
	@Transactional(readOnly = false)
	public void updateSessionAttendanceCounter(String meetingkey) {
		String sql = " UPDATE acads.session_attendance_counter SET remainingSeats = remainingSeats - 1 WHERE meetingkey = ? ";
		jdbcTemplate.update(sql, new Object[] {meetingkey});
	}
	
//	public HashMap<String, SessionAttendanceFeedback> getMapOfSessionWiseAttendaceCount(SessionAttendanceFeedback searchBean, ArrayList<SessionAttendanceFeedback> sessionList,  ArrayList<String> nonPG_ProgramList){
//		HashMap<String, SessionAttendanceFeedback> mapOfSessionWiseAttendaceCount=new HashMap<>();
//		
//		mapOfSessionWiseAttendaceCount=getApplicableStudentCountForSession(sessionList,nonPG_ProgramList, searchBean);
//	
//		return mapOfSessionWiseAttendaceCount;
//	}
	
//	public HashMap<String, SessionAttendanceFeedback> getApplicableStudentCountForSession(ArrayList<SessionAttendanceFeedback> sessionList,  ArrayList<String> nonPG_ProgramList, SessionAttendanceFeedback searchBean){
//		HashMap<String, SessionAttendanceFeedback> mapOfSessionWiseAttendaceCount=new HashMap<>();
//		for(SessionAttendanceFeedback bean : sessionList) {
//			int applicableStudentsCountForSession=0;
//			int attendedStudentsCountForSession=0;
//			
//	
//			if("Y".equals(bean.getHasModuleId())) {
//				//for all students and MBA-WX students
//				ArrayList<StudentBean> listOfTimeBoundStudentsForSession=getRegisteredTimeBoundStudentForSession(bean.getSessionId());
//				applicableStudentsCountForSession+=listOfTimeBoundStudentsForSession.size();
//				attendedStudentsCountForSession=getSessionAttendaceCount(bean.getSessionId());
//				
//				}else{
//					ArrayList<StudentBean> listOfPGStudentForSessionAfterJul21=getRegisteredPGStudentForSessionAfterJul21(bean.getSessionId());
//					//ArrayList<StudentBean> listOfPGStudentForSessionBeforeJul21=getRegisteredPGStudentForSessionBeforeJul21(bean.getSessionId());
//					ArrayList<StudentBean> listOfNonPGStudentForSession=getRegisteredNonPGStudentForSession(bean.getSessionId(), nonPG_ProgramList);
//					
//					applicableStudentsCountForSession=listOfPGStudentForSessionAfterJul21.size()+listOfPGStudentForSessionBeforeJul21.size()+listOfNonPGStudentForSession.size();
//					attendedStudentsCountForSession=getSessionAttendaceCount(bean.getSessionId());
//				}
//			bean.setApplicableStudentsForSession(applicableStudentsCountForSession);
//			bean.setAttendedStudentForSession(attendedStudentsCountForSession);
//
//			mapOfSessionWiseAttendaceCount.put(bean.getSessionId()+"-"+bean.getFacultyId(), bean);
//		}
//		return mapOfSessionWiseAttendaceCount;
//	}
	
	@Transactional(readOnly = true)
	public int getRegisteredPGStudentForSessionAfterJul21(String sessionId, String month, String year ) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count=0;

		String sql="select count(*) from exam.student_session_courses_mapping sscm " + 
				" inner join acads.session_subject_mapping ssm on sscm.program_sem_subject_id=ssm.program_sem_subject_id " + 
				" where ssm.sessionId=? and sscm.acadYear=? and sscm.acadMonth=?";

		try {
			count = jdbcTemplate.queryForObject(sql, new Object[] {sessionId, year, month   },Integer.class);
		} catch (Exception e) {
			  
		}
		return count;
	}

	@Transactional(readOnly = true)
	public int getRegisteredPGStudentForSessionBeforeJul21(String sessionId, String month, String year ) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int count=0;
		
		String sql="select count(s.sapid) from exam.registration r " + 
				"inner join exam.program_sem_subject pss on r.sem = pss.sem and r.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"inner join acads.session_subject_mapping ssm on pss.consumerProgramStructureId =ssm.consumerProgramStructureId and pss.id = ssm.program_sem_subject_id " + 
				"inner join exam.students s on r.sapid = s.sapid " + 
				"where r.year = ? and r.month = ?  and ssm.sessionId = ? " + 
				"and  STR_TO_DATE(CONCAT(enrollmentMonth, '30', enrollmentYear), '%b %d %Y') < '2021-05-01'";
		try {
			count =jdbcTemplate.queryForObject(sql, new Object[] {year,month,sessionId},Integer.class);
		} catch (Exception e) {
			  
		}

		return count;
	}

	@Transactional(readOnly = true)
	public int getRegisteredNonPGStudentForSession(String sessionId,
			ArrayList<String> nonPG_ProgramList, String month, String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		int count=0;
		
		String programList = "'" + StringUtils.join(nonPG_ProgramList, "','") + "'";
		
		String sql="select count(s.sapid) from exam.registration r " + 
				"inner join exam.program_sem_subject pss on r.sem = pss.sem and r.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"inner join acads.session_subject_mapping ssm on pss.consumerProgramStructureId =ssm.consumerProgramStructureId and pss.id = ssm.program_sem_subject_id " +
				"inner join exam.students s on r.sapid = s.sapid " + 
				"where r.year = ? and r.month = ?  and ssm.sessionId = ? " + 
				"and  r.program IN (" + programList + ")";

		try {
			count = jdbcTemplate.queryForObject(sql, new Object[] {year, month, sessionId },Integer.class);
		} catch (Exception e) {
			  
		}

		return count;
	}

	@Transactional(readOnly = true)
	public int getRegisteredTimeBoundStudentForSession(String sessionId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		int count=0;

		String sql = "select count(*) from( SELECT s.sapid FROM   " + "   acads.sessions ses   " + "       INNER JOIN  "
				+ " 	acads.sessionplan_module m ON ses.moduleId = m.id   " + "       INNER JOIN  "
				+ "	acads.sessionplan sp ON m.sessionPlanId = sp.id   " + "       INNER JOIN  "
				+ "	acads.sessionplanid_timeboundid_mapping stm ON sp.id = stm.sessionPlanId   " + "       INNER JOIN  "
				+ "	lti.timebound_user_mapping tum ON stm.timeboundId = tum.timebound_subject_config_id   "
				+ "       INNER JOIN  "
				+ "	lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id   "
				+ "       INNER JOIN  " + "	exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   "
				+ "       INNER JOIN  " + "	exam.students s on tum.userId = s.sapid INNER JOIN exam.registration r on s.sapid=r.sapid " 
				+ " WHERE	ses.hasModuleId = 'Y' AND ses.id = ? AND role = 'Student' "
				+ "		AND (s.programStatus NOT IN ('Program Terminated', 'Program Withdrawal', 'Program Suspension') OR s.programStatus IS NULL) and r.createdDate <= ses.date "
				+ "	GROUP BY s.sapid) as ses";

		try {
			count = jdbcTemplate.queryForObject(sql, new Object[] { sessionId },Integer.class);
		} catch (Exception e) {
			  
		}

		return count;
	}
	
	@Transactional(readOnly = true)
	public int getSessionAttendaceCount(String sessionId) {
		int count = 0;
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from acads.session_attendance_feedback where sessionId=?";
		try {
			count = jdbcTemplate.queryForObject(sql, new Object[] { sessionId }, Integer.class);

		} catch (Exception e) {
			  
		}

		return count;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getMasterKeyListForSessionId(String sessionId){
		ArrayList<String> masterKeyList =new ArrayList<>();
		String sql = "select consumerProgramStructureId from acads.session_subject_mapping where sessionId=?";
		
			try {
			masterKeyList = (ArrayList<String>) jdbcTemplate.query(sql,new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setString(1, sessionId);
			}
		},new SingleColumnRowMapper(String.class));
			}catch (Exception e) {
				// TODO: handle exception
			}
		return masterKeyList;
	}
	
	@Transactional(readOnly = true)
	public int getSemForMasterKeyAndSubject(String consumerProgramStructureId, String subject){
		int sem=0;
		String sql="Select sem from exam.program_sem_subject where subject=? and consumerProgramStructureId=?";
		try {
		sem=jdbcTemplate.queryForObject(sql, new Object[] {subject,consumerProgramStructureId}, Integer.class);
		}catch (Exception e) {
			  
			// TODO: handle exception
		}
		return sem;
	}
	
	@Transactional(readOnly = true)
	public int getStudentsCountBeforeJul2021(String consumerProgramStructureId, int sem, String year, String month){
		int count=0;
		String sql = "select count(*) from exam.registration r " + 
				" inner join exam.students s on  r.sapid=s.sapid " + 
				" where r.consumerProgramStructureId=? and r.year=? and r.month=? " + 
				" and r.sem=? and STR_TO_DATE(CONCAT(enrollmentMonth, '30', enrollmentYear), '%b %d %Y') < '2021-05-01'";
		
		try {
		count = jdbcTemplate.queryForObject(sql, new Object[] { consumerProgramStructureId, sem, year, month }, Integer.class);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return count;
	}
	
	@Transactional(readOnly = true)
	public int getNonPGStudentsCountBeforeJul2021(String consumerProgramStructureId, int sem, String year, String month){
		int count=0;
		String sql = "select count(*) from exam.registration r " + 
				" inner join exam.students s on  r.sapid=s.sapid " + 
				" where r.consumerProgramStructureId=? " + 
				" and r.sem=? and r.year=? and r.month=? and STR_TO_DATE(CONCAT(enrollmentMonth, '30', enrollmentYear), '%b %d %Y') < '2021-05-01' and "+
				" s.program IN ('BBA','B.Com','PD - WM','PD - DM','M.Sc. (App. Fin.)','CP-WL')";
		
		try {
		count = jdbcTemplate.queryForObject(sql, new Object[] { consumerProgramStructureId, sem, year, month }, Integer.class);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return count;
	}
	
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getMapOfSessionIdAndAttendaceCount(String year, String month){
		ArrayList<SessionCountBean> list = new ArrayList<>();
		 HashMap<String, Integer> mapOfSessionIdAndAttendaceCount=new HashMap<>();

		String sql = "select sessionId, count(*) as count from acads.session_attendance_feedback saf " + 
				"inner join acads.sessions s on saf.sessionId = s.id " + 
				"where year = ? and month = ? group by sessionid";

			list = (ArrayList<SessionCountBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, year);
					ps.setString(2, month);
				}
			}, new BeanPropertyRowMapper<>(SessionCountBean.class));
		
		for(SessionCountBean bean: list){
			mapOfSessionIdAndAttendaceCount.put(bean.getSessionId(), bean.getCount());
		}
		
		return mapOfSessionIdAndAttendaceCount;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getMapOfPGCountBeforeJul21ForMasterKeyAndSem(String year, String month){
		ArrayList<SessionCountBean> list = new ArrayList<>();
		 HashMap<String, Integer> mapOfSessionIdAndAttendaceCount=new HashMap<>();

		String sql = "select r.consumerProgramStructureId, r.sem,count(*) as count from exam.registration r inner join exam.students s " + 
				"on r.sapid=s.sapid where s.program not IN ('BBA','B.Com','PD - WM','PD - DM','M.Sc. (App. Fin.)','CP-WL', 'CP-ME') and " + 
				"STR_TO_DATE(CONCAT(enrollmentMonth, '30', enrollmentYear), '%b %d %Y') < '2021-05-01' and r.year=? and r.month=? group by r.consumerProgramStructureId, r.sem";

			list=(ArrayList<SessionCountBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, year);
					ps.setString(2, month);
				}
			},new BeanPropertyRowMapper<SessionCountBean>(SessionCountBean.class));
			
		for(SessionCountBean bean: list){
			mapOfSessionIdAndAttendaceCount.put(bean.getConsumerProgramStructureId()+"-"+bean.getSem(), bean.getCount());
			}
		return mapOfSessionIdAndAttendaceCount;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getMapOfNonPGCountBeforeJul21ForMasterKeyAndSem(String year, String month, ArrayList<String> liveSessionMasterKeyList){
		ArrayList<SessionCountBean> list = new ArrayList<>();
		 HashMap<String, Integer> mapOfSessionIdAndAttendaceCount=new HashMap<>();
		 nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select r.consumerProgramStructureId, r.sem,count(*) as count from exam.registration r inner join exam.students s " + 
				"on r.sapid=s.sapid where " + 
				"r.consumerProgramStructureId IN (:masterkeysList) and r.year=:year and r.month=:month group by r.consumerProgramStructureId, r.sem";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("masterkeysList", liveSessionMasterKeyList);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);
		
			list=(ArrayList<SessionCountBean>) nameJdbcTemplate.query(sql,queryParams, new BeanPropertyRowMapper<SessionCountBean>(SessionCountBean.class));
		for(SessionCountBean bean: list){
			mapOfSessionIdAndAttendaceCount.put(bean.getConsumerProgramStructureId()+"-"+bean.getSem(), bean.getCount());
			}
		return mapOfSessionIdAndAttendaceCount;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getMapOfSubjectSemAndMasterKey(){
		ArrayList<SessionCountBean> list = new ArrayList<>();
		 HashMap<String, Integer> mapOfSubjectSemAndMasterKey=new HashMap<>();

		String sql = "select pss.consumerProgramStructureId,pss.subject,pss.sem " + 
				"from exam.program_sem_subject pss " + 
				"group by pss.consumerProgramStructureId,pss.subject";

		
			list=(ArrayList<SessionCountBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionCountBean>(SessionCountBean.class));
		for(SessionCountBean bean: list){
			mapOfSubjectSemAndMasterKey.put(bean.getConsumerProgramStructureId()+"-"+bean.getSubject(), bean.getSem());
			}
		return mapOfSubjectSemAndMasterKey;
	}
	
	@Transactional(readOnly = true)
	public int getCommonSessionApplicableStudentsCount(ArrayList<String> programList, String sem, String year, String month){
		int count=0;
		
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select count(sapid) from exam.registration r where program in (:programList) and r.sem=:sem and r.year=:year and r.month=:month");
		
		parameters.addValue("programList",programList);
		parameters.addValue("sem",sem);
		parameters.addValue("year",year);
		parameters.addValue("month",month);

		
		try {
			count=nameJdbcTemplate.queryForObject(sql.toString(), parameters, Integer.class);
		//count = jdbcTemplate.queryForObject(sql, new Object[] {  sem, year, month }, Integer.class);
		}catch (Exception e) {
			// TODO: handle exception
			  
		}
		return count;
	}
	
	public HashMap<String,FacultyAcadsBean> getMapOfFacultyDetails(){
		ArrayList<FacultyAcadsBean> listOfFaculty=new ArrayList<>();
		HashMap<String,FacultyAcadsBean> mapOfFacultyBean= new HashMap<>();
		
		String sql="select facultyId, firstName, lastName from acads.faculty";
		
		try {
			listOfFaculty=(ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<FacultyAcadsBean>(FacultyAcadsBean.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		for(FacultyAcadsBean bean : listOfFaculty){
			mapOfFacultyBean.put(bean.getFacultyId(), bean);
		}
		
		return mapOfFacultyBean;
	}
	
	public HashMap<String,StudentAcadsBean> getMapOfStudentDetails(String year, String month, String authorizedCenterCodes){
		ArrayList<StudentAcadsBean> listOfStudents=new ArrayList<>();
		HashMap<String,StudentAcadsBean> mapOfStudentBean= new HashMap<>();
		
		String sql= " select st.sapid,st.firstname,st.lastname,r.year,r.month, r.program,r.sem, st.consumerType " + 
					" from exam.registration r inner join exam.students st " + 
					" on r.sapid=st.sapid where r.year=? and r.month=?";
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and st.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		try {
			listOfStudents=(ArrayList<StudentAcadsBean>) jdbcTemplate.query(sql,new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, year);
					ps.setString(2, month);
				}
			} ,new BeanPropertyRowMapper<StudentAcadsBean>(StudentAcadsBean.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		for(StudentAcadsBean bean: listOfStudents){
			mapOfStudentBean.put(bean.getSapid(), bean);
		}
		
		return mapOfStudentBean;
	}
	
	public ArrayList<SessionAttendanceFeedbackAcads> getListSessionAttendaceDetailsForYearMonth(SessionAttendanceFeedbackAcads searchBean, String acadDateFormat){
		ArrayList<SessionAttendanceFeedbackAcads> listOfSessionAttendanceFeedback=new ArrayList<>();
		
		String sql=	" select saf.*,s.subject,s.sessionName,s.date,s.track,s.hasModuleId,s.moduleid FROM acads.session_attendance_feedback saf " +
					" inner join acads.sessions s on s.id=saf.sessionId " + 
					" where saf.acadDateFormat=? ";

		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(acadDateFormat);
		
		if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
			sql = sql + " and s.hasModuleId = 'Y' ";
			
		}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
			sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
		}
		
		if( searchBean.getSubject() != null  &&  !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject =  ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getFacultyFullName() != null  &&  !("".equals(searchBean.getFacultyFullName()))){
			sql += " and (s.facultyId = ? OR s.altFacultyId=? OR s.altFacultyId2=? OR s.altFacultyId3=?)";
			parameters.add(searchBean.getFacultyFullName());
			parameters.add(searchBean.getFacultyFullName());
			parameters.add(searchBean.getFacultyFullName());
			parameters.add(searchBean.getFacultyFullName());
		}
		
		Object[] args = parameters.toArray();

		try {
			listOfSessionAttendanceFeedback=(ArrayList<SessionAttendanceFeedbackAcads>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(SessionAttendanceFeedbackAcads.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return listOfSessionAttendanceFeedback;
	}
	
	public ArrayList<SessionAttendanceFeedbackAcads> getListSessionAttendaceDetailsFromHistory(SessionAttendanceFeedbackAcads searchBean, String acadDateFormat){
		ArrayList<SessionAttendanceFeedbackAcads> listOfSessionAttendanceFeedback=new ArrayList<>();
		String sql="select saf.*,s.subject,s.sessionName,s.date,s.track,s.hasModuleId,s.moduleid FROM acads.session_attendance_feedback_history saf inner join acads.sessions_history s " + 
				"on s.id=saf.sessionId " + 
				"where saf.acadDateFormat=? ";

				ArrayList<String> parameters = new ArrayList<String>();
				parameters.add(acadDateFormat);
				
				if ("Y".equalsIgnoreCase(searchBean.getHasModuleId())) {
					sql = sql + " and s.hasModuleId = 'Y' ";
					
				}else if ("N".equalsIgnoreCase(searchBean.getHasModuleId())){
					sql = sql + " and (s.hasModuleId = 'N' or s.hasModuleId is null) ";
				}
				
				if( searchBean.getSubject() != null  &&  !("".equals(searchBean.getSubject()))){
					sql = sql + " and subject =  ? ";
					parameters.add(searchBean.getSubject());
				}
				
				if( searchBean.getFacultyFullName() != null  &&  !("".equals(searchBean.getFacultyFullName())))
				{
					sql += " and (s.facultyId = ? OR s.altFacultyId=? OR s.altFacultyId2=? OR s.altFacultyId3=?)";
					parameters.add(searchBean.getFacultyFullName());
					parameters.add(searchBean.getFacultyFullName());
					parameters.add(searchBean.getFacultyFullName());
					parameters.add(searchBean.getFacultyFullName());
				}
				
				Object[] args = parameters.toArray();
		
				try {
					listOfSessionAttendanceFeedback=(ArrayList<SessionAttendanceFeedbackAcads>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(SessionAttendanceFeedbackAcads.class));
				}catch (Exception e) {
					// TODO: handle exception
				}
		return listOfSessionAttendanceFeedback;
	}
	
	@Transactional(readOnly = true)
		public SessionAttendanceFeedbackAcads getPostSessionFeedback(String sapId,String sessionId, String acadDateFormat){
		SessionAttendanceFeedbackAcads pendingFeeback = new SessionAttendanceFeedbackAcads();
			jdbcTemplate = new JdbcTemplate(dataSource);
			String query =  " select * from acads.session_attendance_feedback saf inner join acads.sessions s " + 
							" on saf.sessionId=s.id inner join acads.faculty f on saf.facultyId=f.facultyId where saf.sapid=? and sessionId=? and acadDateFormat=?";
			
				pendingFeeback = (SessionAttendanceFeedbackAcads)jdbcTemplate.queryForObject(query, new Object[]{sapId, sessionId, acadDateFormat },
						new BeanPropertyRowMapper<>(SessionAttendanceFeedbackAcads.class));
			
				return pendingFeeback;
		}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentAcadsBean student = null;
		try{
			String sql =  " SELECT *   FROM exam.students s where "
						+ " s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";

			student = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid, sapid}, new BeanPropertyRowMapper<>(StudentAcadsBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
			
			return student;
		}catch(Exception e){
			return null;
			//  
		}

	}
	
	 @Transactional(readOnly = false)
		public void saveFeedback(SessionAttendanceFeedbackAcads feedback) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			// checking Session Confirmation as getting Incorrect value for null for q1Response integer 
			if("Y".equals(feedback.getStudentConfirmationForAttendance())){
				String sql = "Update acads.session_attendance_feedback set "
						+ "feedbackGiven='Y',"					
						+ "q1Response=?,"
						+ "q2Response=?,"
						+ "q3Response=?,"
						+ "q4Response=?,"
						+ "q5Response=?,"
						+ "q6Response=?,"
						+ "q7Response=?,"
						+ "q8Response=?,"
						+ "q1Remark=?,"
						+ "q2Remark=?,"
						+ "q3Remark=?,"
						+ "q4Remark=?,"
						+ "q5Remark=?,"
						+ "q6Remark=?,"
						+ "q7Remark=?,"
						+ "q8Remark=?,"
						+ "feedbackRemarks=?, "
						+ " studentReviewAvg = ?"
						
						+ " where sapid= ? and sessionId = ? ";

				jdbcTemplate.update(sql, new Object[] {					
						feedback.getQ1Response(),
						feedback.getQ2Response(),
						feedback.getQ3Response(),
						feedback.getQ4Response(),
						feedback.getQ5Response(),
						feedback.getQ6Response(),
						feedback.getQ7Response(),
						feedback.getQ8Response(),
						feedback.getQ1Remark(),
						feedback.getQ2Remark(),
						feedback.getQ3Remark(),
						feedback.getQ4Remark(),
						feedback.getQ5Remark(),
						feedback.getQ6Remark(),
						feedback.getQ7Remark(),
						feedback.getQ8Remark(),
						feedback.getFeedbackRemarks(),
						feedback.getStudentReviewAvg(),
						
						feedback.getSapId(),
						feedback.getSessionId()
				});
			}else{
				String sql = "Update acads.session_attendance_feedback set "
						+ " feedbackGiven='Y',"
						+ " studentConfirmationForAttendance=?,"
						+ " reasonForNotAttending=?,"
						+ " otherReasonForNotAttending=?"
						+ " where sapid= ? and sessionId = ? ";
				jdbcTemplate.update(sql, new Object[] { 
						feedback.getStudentConfirmationForAttendance(),
						feedback.getReasonForNotAttending(),
						feedback.getOtherReasonForNotAttending(),
						feedback.getSapId(),
						feedback.getSessionId()
				});
			}
		}
}
