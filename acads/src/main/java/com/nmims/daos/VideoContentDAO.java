package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyFilterBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.Post;
import com.nmims.beans.SearchBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.helpers.PaginationHelper;
import com.nmims.util.ContentUtil;

public class VideoContentDAO extends BaseDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	 
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	@Autowired(required = false)
	ApplicationContext act;
	
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	public static final String TEMP_SEARCH_TYPE = "distinct";
	public static final String HISTORY_DATE = "2020-07-01";
	private HashMap<String, VideoContentAcadsBean> mapOfSessionIdAndVideoContentRecord = null;
	
	private static final Logger logger = LoggerFactory.getLogger(VideoContentDAO.class);	

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	@Transactional(propagation = Propagation.REQUIRED ,readOnly = false, rollbackFor= Exception.class )
	public long saveVideoContent(final VideoContentAcadsBean VideoContent)  throws Exception  {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();

			final String sql = "INSERT INTO acads.video_content(year, month, subject, fileName , keywords ,"
					+ " description , defaultVideo, createdBy, createdDate, lastModifiedBy,"
					+ " lastModifiedDate, videoLink, addedOn, thumbnailUrl, mobileUrlHd, "
					+ "mobileUrlSd1, mobileUrlSd2, sessionId, facultyId, sessionDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,?,?,?,?,?,?,?,?)";

			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

					statement.setString(1, VideoContent.getYear());
					statement.setString(2, VideoContent.getMonth());
					statement.setString(3, VideoContent.getSubject());
					statement.setString(4, VideoContent.getFileName());
					statement.setString(5, VideoContent.getKeywords());
					statement.setString(6, VideoContent.getDescription());
					statement.setString(7, VideoContent.getDefaultVideo());
					statement.setString(8, VideoContent.getCreatedBy());
					statement.setString(9, VideoContent.getCreatedBy());
					statement.setString(10, VideoContent.getVideoLink());
					statement.setString(11, VideoContent.getAddedOn());
					statement.setString(12, VideoContent.getThumbnailUrl());
					statement.setString(13, VideoContent.getMobileUrlHd());
					statement.setString(14, VideoContent.getMobileUrlSd1());
					statement.setString(15, VideoContent.getMobileUrlSd2());
					statement.setInt(16, VideoContent.getSessionId());
					statement.setString(17, VideoContent.getFacultyId());
					statement.setString(18, VideoContent.getSessionDate());
					return statement;
				}
			}, holder);
			long primaryKey = holder.getKey().longValue();
						
			//Insert entry into quick video content table if generated primary key id not zero.
			if(primaryKey != 0) {
				//Get ContentDAO class object
				ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
				boolean result = dao.insertQuickVideoContent(dao.prepareVideoContenList(VideoContent), (int)primaryKey);	
			}
			
			return primaryKey;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public boolean updateVideoContent(VideoContentAcadsBean VideoContent) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.video_content set " + " fileName=?, " + " facultyId=?, " + " keywords=?, "
				+ " description=?, " + " subject=?, " + " defaultVideo=?, " + " duration=?, " + " sessionDate=?, "
				+ " lastModifiedBy = ? ," + " lastModifiedDate = sysdate(), " + " videoLink = ? , "
				+ " thumbnailUrl = ?, " + " mobileUrlHd = ?, " + " mobileUrlSd1 = ?, " + " mobileUrlSd2 = ?, "
				+ " year = ?, " + " month = ?, " + " sessionId = ?, " + " facultyId = ?, " + " sessionDate = ? "
				+ " where id=?";
	
			jdbcTemplate.update(sql,
					new Object[] { VideoContent.getFileName(), VideoContent.getFacultyId(), VideoContent.getKeywords(),
							VideoContent.getDescription(), VideoContent.getSubject(), VideoContent.getDefaultVideo(),
							VideoContent.getDuration(), VideoContent.getSessionDate(), VideoContent.getLastModifiedBy(),
							VideoContent.getVideoLink(), VideoContent.getThumbnailUrl(), VideoContent.getMobileUrlHd(),
							VideoContent.getMobileUrlSd1(), VideoContent.getMobileUrlSd2(), VideoContent.getYear(),
							VideoContent.getMonth(), VideoContent.getSessionId(), VideoContent.getFacultyId(),
							VideoContent.getSessionDate(), VideoContent.getId() });

			// Add in post table only if Session plan module is exists for Subject.
			if (VideoContent.getSessionPlanModuleId() != null && VideoContent.getSessionPlanModuleId() != 0) {
				ContentDAO contentDao = (ContentDAO) act.getBean("contentDAO");
				contentDao.insertRecordingsPostEMBA(VideoContent, VideoContent.getId().intValue(), "update");
			}
			
			//Update Quick Video Content Table
			int result = updateQuickVideoContent(VideoContent);

			return true;
	}

	
	/**
	 * @param vdoCntBean
	 * @return int Return the count of affected rows in database because of update
	 *         query
	 * @throws java.sql.SQLException
	 */
	public int updateQuickVideoContent(VideoContentAcadsBean vdoCntBean) throws SQLException {
		int result = 0;

		//Create JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Create StringBuilder object
		StringBuilder UPDATE_VIDEO_CONTENT  = new StringBuilder();

		//Prepare acadDateFormat
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(vdoCntBean.getMonth(),vdoCntBean.getYear());
		
		//Prepare SQL Query
		UPDATE_VIDEO_CONTENT.append("UPDATE acads.quick_video_content SET thumbnailUrl = ?, subject=?, description=?, ");
		UPDATE_VIDEO_CONTENT.append("sessionId = ?, sessionDate = ?, lastModifiedDate=sysdate(),acadMonth =?, acadYear=?, ");
		UPDATE_VIDEO_CONTENT.append("fileName =?,videoLink=?,mobileUrlHd=?,mobileUrlSd1=?,mobileUrlSd2=?,acadDateFormat=? WHERE id=? ");
		
		//Execute update method
		result = jdbcTemplate.update(UPDATE_VIDEO_CONTENT.toString(),vdoCntBean.getThumbnailUrl(), 
				vdoCntBean.getSubject(), vdoCntBean.getDescription(), vdoCntBean.getSessionId(),
				vdoCntBean.getSessionDate(), vdoCntBean.getMonth(),vdoCntBean.getYear(),vdoCntBean.getFileName(),
				vdoCntBean.getVideoLink(),vdoCntBean.getMobileUrlHd(),vdoCntBean.getMobileUrlSd1(),vdoCntBean.getMobileUrlSd2(),
				acadDateFormat,vdoCntBean.getId());

		//return affected count
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAllVideoContentList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		String sql =" SELECT v.*, s.meetingKey FROM acads.video_content v " + 
					"	INNER JOIN acads.sessions s ON s.id = v.sessionId " + 
					" ORDER BY v.id DESC ";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAllVideoContentListForLeads(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList=null;

		String sql =  "select * from acads.video_content_forleads v "
				+ " where v.year = '2019' and v.month in ('Jul')";
				//+ "Order By v.id desc";
		try {
			 VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideosWithDefaultThumbnails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		// String sql="select * from acads.video_content where thumbnailUrl like
		// '%studentzone%' Order By id desc limit 45";
		String sql = "select v.*,s.meetingKey from acads.video_content v "
				+ "inner join acads.sessions s on s.id = v.sessionId "
				+ "where v.thumbnailUrl like '%studentzone%' Order By v.id desc limit 45";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getRelatedVideoContentList(String searchItem, String subject,
			ArrayList<String> allsubjects, StudentAcadsBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		List<VideoContentAcadsBean> VideoTopicsList = null;
		String sql = "";
		String sqlTopics = "";
		String subjectCommaSeparated = "''";
		String tempSubject = "";

		// subject = StringEscapeUtils.escapeJava(subject);
		for (int i = 0; i < allsubjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		if (!"".equals(searchItem) && subject == null) {

//			sql="select * from acads.video_content where subject in ("+subjectCommaSeparated+") and (subject Like '%"+ searchItem.trim() +"%' or keywords Like '%"+ searchItem.trim() +"%' or description Like '%"+ searchItem.trim() +"%' or fileName Like '%"+searchItem+"%' ) Order By id desc";
			sql = "select v.*,s.meetingKey from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject in ("
					+ subjectCommaSeparated + ") and " + "(v.subject Like '%" + searchItem.trim()
					+ "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%" + searchItem.trim()
					+ "%' or v.fileName Like '%" + searchItem + "%' )";

//			sqlTopics = "select * from acads.video_content_subtopics where subject in (" + subjectCommaSeparated
//					+ ") and (keywords Like '%" + searchItem.trim() + "%' or description Like '%" + searchItem.trim()
//					+ "%'or fileName Like '%" + searchItem + "%' ) Order By id desc";

//			sql = "select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm "
//					+ "where subject in (" + subjectCommaSeparated + ") " + " and (subject Like '%" + searchItem.trim()
//					+ "%' " + " or keywords Like '%" + searchItem.trim() + "%' " + " or description Like '%"
//					+ searchItem.trim() + "%' " + " or fileName Like '%" + searchItem + "%' ) "
//					+ " and v.id = vcm.videoContentId ";
//			if (student != null) {
//				sql += " and vcm.consumerProgramStructureId= " + student.getConsumerProgramStructureId() + " ";
//			}
			sql += " group By v.id " + " Order By v.id desc";
			sqlTopics = "select * from acads.video_content_subtopics " + " where subject in (" + subjectCommaSeparated
					+ ") " + " and (keywords Like '%" + searchItem.trim() + "%' " + " or description Like '%"
					+ searchItem.trim() + "%' " + " or fileName Like '%" + searchItem + "%' ) " + "Order By id desc";
		}
		if ("".equals(searchItem) && !"".equals(subject) && subject!=null) {
			subject.replaceAll("&", "_");
			subject = subject.replaceAll("'", "''");
			// sql="select * from acads.video_content where subject like '"+ subject +"'
			// Order By id desc";
			sql = "select v.*,s.meetingKey,s.track from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject like '" + subject
					+ "' Order By v.id desc";
			/*
			 * commented by PS 29May
			 * sql="select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm "
			 * + " where subject like '"+ subject +"' " + " and v.id = vcm.videoContentId ";
			 * if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; } sql += " group By v.id " + " Order By v.id desc";
			 */

			sqlTopics = "select * from acads.video_content_subtopics where subject like '" + subject
					+ "' and id=0 Order By id desc";

		}
		if (!"".equals(searchItem) && !"".equals(subject) && subject!=null) {
			subject = subject.replaceAll("'", "''");
			// sql="select * from acads.video_content where subject in
			// ("+subjectCommaSeparated+") and (subject Like '%"+ subject +"%' or subject
			// Like '%"+ searchItem +"%' or keywords Like '%"+ searchItem.trim() +"%' or
			// description Like '%"+ searchItem.trim() +"%' or fileName Like
			// '%"+searchItem+"%') Order By id desc";
			sql = "select v.*,s.meetingKey,s.track from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject in ("
					+ subjectCommaSeparated + ") " + "and (v.subject Like '%" + subject + "%' or v.subject Like '%"
					+ searchItem + "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%"
					+ searchItem.trim() + "%' or v.fileName Like '%" + searchItem + "%') Order By v.id desc";
			/*
			 * sql="select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm  "
			 * + " where subject in ("+subjectCommaSeparated+") " + " and (subject Like '%"+
			 * subject +"%' " + " or subject Like '%"+ searchItem +"%' " +
			 * " or keywords Like '%"+ searchItem.trim() +"%' " + " or description Like '%"+
			 * searchItem.trim() +"%' " + " or fileName Like '%"+searchItem+"%') " +
			 * " and v.id = vcm.videoContentId "; if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; } sql += " group By v.id " + " Order By v.id desc";
			 */

			sqlTopics = "select * from acads.video_content_subtopics where subject in (" + subjectCommaSeparated
					+ ") and  (subject Like '%" + subject + "%' or subject Like '%" + searchItem
					+ "%' or keywords Like '%" + searchItem.trim() + "%' or description Like '%" + searchItem.trim()
					+ "%' or fileName Like '%" + searchItem + "%') Order By id desc";
		}
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			VideoTopicsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sqlTopics, new Object[] {},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			VideoContentsList.addAll(VideoTopicsList);
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}
	
	/**
	 * Get all session videos matching for a search keyword.
	 * @param searchKeyword - search keyword to fetch matching session videos. 
	 * @return List return the list of session videos matching for a given search keyword.
	 * @throws java.lang.Exception If any exception occurs while executing the query.
	 */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getSessionVideosBySearch(String searchKeyword) throws Exception{
		List<VideoContentAcadsBean> sessionVideosList = null;
		StringBuilder GET_SESSION_VIDEOS = null;
		
		//Create StringBuilder object
		GET_SESSION_VIDEOS = new StringBuilder();
		
		//Creating empty list of video content bean
		sessionVideosList = new ArrayList<VideoContentAcadsBean>();
		
		//Prepare SQL query
		GET_SESSION_VIDEOS.append("SELECT v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ")
		.append("s.track AS track FROM acads.video_content v ")
		.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ")
		.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ")
		.append("WHERE v.subject LIKE '%"+searchKeyword+"%' OR v.keywords LIKE '%"+searchKeyword+"%' ")
		.append("OR v.description LIKE '%"+searchKeyword+"%' OR v.fileName LIKE '%"+searchKeyword+"%' ");
		
		//Execute JdbcTemplate query method
		sessionVideosList = jdbcTemplate.query(GET_SESSION_VIDEOS.toString(), 
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		
		//return session video list matching for search keyword
		return sessionVideosList;
	}
	
	/**
	 * Get all session videos matching for a search keyword.
	 * @param searchKeyword - search keyword to fetch matching session videos. 
	 * @param PSSIds - program sem subject id's in single string for fetching applicable session videos only.
	 * @param commonSubjects - common subjects to fetch matching session videos.
	 * @param program - contains the program of a student.
	 * @return List return the list of session videos matching for a given search keyword.
	 * @throws java.lang.Exception If any exception occurs while executing the query.
	 */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getSessionVideosBySearch(String searchKeyword, String PSSIds, 
			String commonSubjects, String program) throws Exception{
		List<VideoContentAcadsBean> sessionVideosList = null;
		List<VideoContentAcadsBean> commonVideosList = null;
		StringBuilder GET_SESSION_VIDEOS = null;
		StringBuilder GET_COMMON_VIDEOS = null;
		
		//Create StringBuilder object
		GET_SESSION_VIDEOS = new StringBuilder();
		GET_COMMON_VIDEOS = new StringBuilder();
		
		//Creating empty list of video content bean
		sessionVideosList = new ArrayList<VideoContentAcadsBean>();
		commonVideosList = new ArrayList<VideoContentAcadsBean>();
		
		//Prepare SQL query
		GET_SESSION_VIDEOS.append("SELECT v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ")
		.append("s.track AS track, ssm.subjectCodeId, ssm.program_sem_subject_id as programSemSubjectId FROM acads.video_content v ")
		.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ")
		.append("INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id ")
		.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ")
		.append("WHERE ssm.program_sem_subject_id IN ("+PSSIds+") ")
		.append("AND (v.subject LIKE '%"+searchKeyword+"%' OR v.keywords LIKE '%"+searchKeyword+"%' ")
		.append("OR v.description LIKE '%"+searchKeyword+"%' OR v.fileName LIKE '%"+searchKeyword+"%') ");
		
		GET_COMMON_VIDEOS.append("SELECT  v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ")
		.append("s.track AS track FROM acads.video_content v ")
		.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ")
		.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ")
		.append("WHERE v.subject IN ("+commonSubjects+") ");
		
		if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program)){
			GET_COMMON_VIDEOS.append(" AND ((programList like ('%"+program+"%')) OR (programList='All')) ");			
		}else {
			GET_COMMON_VIDEOS.append(" AND programList LIKE ('%"+program+"%') ");
		}
		
		GET_COMMON_VIDEOS.append("AND (v.subject LIKE '%"+searchKeyword+"%' OR v.keywords LIKE '%"+searchKeyword+"%' ")
		.append("OR v.description LIKE '%"+searchKeyword+"%' OR v.fileName LIKE '%"+searchKeyword+"%') ");
		
		try {
			//Execute JdbcTemplate query method
			sessionVideosList = jdbcTemplate.query(GET_SESSION_VIDEOS.toString(), 
					new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));

			//Execute JdbcTemplate query method
			commonVideosList = jdbcTemplate.query(GET_COMMON_VIDEOS.toString(), 
					new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));

		}catch (Exception e) {
			  
		}
		
		//Merge applicable and common session videos
		sessionVideosList.addAll(commonVideosList);
		
		//return session video list matching for search keyword
		return sessionVideosList;
	}

	/*
	 * //get videos for searchbyfilter start public List<VideoContentBean>
	 * getVideoForSearchByFilter(String searchItem, String faculty,
	 * ArrayList<String> subjects,String cycle, StudentBean student){ jdbcTemplate =
	 * new JdbcTemplate(dataSource); List<VideoContentBean> VideoContentsList=null;
	 * String sql=""; String subjectCommaSeparated = "''"; String tempSubject="";
	 * for (int i = 0; i < subjects.size(); i++) { if (i == 0) {
	 * subjectCommaSeparated = "'" + subjects.get(i).replaceAll("'", "''") + "'"; }
	 * else { subjectCommaSeparated = subjectCommaSeparated + ", '" +
	 * subjects.get(i).replaceAll("'", "''") + "'"; } } if(!"".equals(searchItem)) {
	 * sql="select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
	 * + "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId "
	 * + "left join " + "	acads.sessions s " + "	on s.id = v.sessionId   " +
	 * " LEFT JOIN  " +
	 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
	 * + " where v.subject in ("+subjectCommaSeparated+") " +
	 * " and (v.subject Like '%"+ searchItem.trim() +"%' " + " or keywords Like '%"+
	 * searchItem.trim() +"%' " + " or description Like '%"+ searchItem.trim()
	 * +"%' " + " or fileName Like '%"+searchItem+"%' ) "; if(student != null) { sql
	 * +=
	 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
	 * ()+" "; } }else {
	 * sql="select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
	 * + "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId "
	 * + "left join " + "	acads.sessions s " + "	on s.id = v.sessionId" +
	 * " LEFT JOIN  " +
	 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
	 * + " where v.subject in ("+subjectCommaSeparated+") "; if(student != null) {
	 * sql +=
	 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
	 * ()+" "; }
	 * 
	 * } if(!"All".equals(faculty)) { sql=sql+" and v.facultyId='"+faculty+"'"; }
	 * if(!"All".equals(cycle)) { sql=sql+" and concat(v.month,v.year)='"+cycle+"'";
	 * }
	 * 
	 * sql += " group by v.id "; sql=sql+" Order By v.id desc ";
	 * VideoContentsList = (List<VideoContentBean>) jdbcTemplate.query(sql, new
	 * Object[] {}, new BeanPropertyRowMapper(VideoContentBean.class)); } catch
	 * (Exception e) {    } return VideoContentsList; }
	 * 
	 * //end
	 */

	// get videos for searchbyfilter start
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoForSearchByFilter(String searchItem, String faculty,
			ArrayList<String> subjects, String cycle, String batch, StudentAcadsBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		String sql = "";
		String subjectCommaSeparated = "''";
		String tempSubject = "";
		String programCheck = "";
		
		try {
			if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
				programCheck = " AND s.corporateName = 'M.sc'";
			} else {
				programCheck = " AND (s.corporateName <> 'M.sc' or s.corporateName is null or s.corporateName = '')";
			}
		} catch (Exception ex) {
		
		}
		
		for (int i = 0; i < subjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + subjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		if (!"".equals(searchItem)) {
			sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
					+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
					+ "	acads.sessions s " + "	on s.id = v.sessionId   "
					/*
					 * + " LEFT JOIN  " +
					 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
					 */
					+ " where v.subject in (" + subjectCommaSeparated + ") " + " and (v.subject Like '%"
					+ searchItem.trim() + "%' " + " or keywords Like '%" + searchItem.trim() + "%' "
					+ " or description Like '%" + searchItem.trim() + "%' " + " or fileName Like '%" + searchItem
					+ "%' ) ";
			/*
			 * if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; }
			 */
		} else {
			sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
					+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
					+ "	acads.sessions s " + "	on s.id = v.sessionId"
					/*
					 * + " LEFT JOIN  " +
					 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
					 */
					+ " where v.subject in (" + subjectCommaSeparated + ") ";
			/*
			 * if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; }
			 */

		}
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//for batch track filter
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}

		sql = sql + programCheck ;
		sql = sql + " group by v.id ";
		sql = sql + " Order By v.id desc ";
		
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return VideoContentsList;
	}

	// end

	@Transactional(readOnly = true)
	public VideoContentAcadsBean getVideoContentById(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		VideoContentAcadsBean VideoContent = null;
		String sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.*,SUBSTR(v.month,1,3) as month, s.meetingKey from acads.video_content v "
				+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
				+ "	acads.sessions s " + "	on s.id = v.sessionId" + " where v.id=?";
		
		/*String sql = "SELECT s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,"
				+"v.*,SUBSTR(v.month,1,3) as month, s.meetingKey FROM acads.video_content v "
				+"INNER JOIN acads.faculty f ON f.facultyId = v.facultyId "
				+"INNER JOIN acads.sessions s ON s.id = v.sessionId "
				+"WHERE v.id= ? GROUP BY v.id ";*/
		
		try {
			VideoContent = (VideoContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContent;
	}
	
	@Transactional(readOnly = true)
	public VideoContentAcadsBean getVideoContentForLeadById(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		VideoContentAcadsBean VideoContent = null;
		String sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.*,SUBSTR(v.month,1,3) as month, s.meetingKey from acads.video_content_forleads v "
				+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
				+ "	acads.sessions s " + "	on s.id = v.sessionId" + " where v.id=?";
		try {
			VideoContent = (VideoContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			//UI breaks if VideoContent is returned as null, dao call to search video by id in acads.video_content table 
			VideoContent = getVideoContentById(id);
		}
		return VideoContent;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public int deleteVideoContent(int id) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sessionPlanModuleId = null;
		int row = 0;
		
		//Get the Session Plan Module Id based on the VideoContentID 
		sessionPlanModuleId = this.getSessionPlanModuleId(id);
		
		String sql = "delete from acads.video_content where id=?";
		
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
		} catch (DataAccessException e) {
			  
		}
		int resultCount = 0;
		
		if(!"0".equalsIgnoreCase(sessionPlanModuleId) && sessionPlanModuleId != null) {
			Post post = findSessionPostByReferenceId(id);
			if (post != null) {
				deleteVideoContentPost(post.getPost_id());
				// deleteFromRedis(post);
				refreshRedis(post);
			}
		}
		
		//Delete QuickVideoContent details only if entry deleted from video_content based on videoContentId
		if(row != 0)
			resultCount = deleteQuickVideoContent(id);
				
		return row;
	}
	
	/**
	 * @param	videoContentId 	Video Content Id to get the Video Content related info.
	 * @return	String		return the Session Plan Module Id which is applicable for MBA students.
	 * @throws	java.sql.SQLException	If query fails to execute then this exception will be thrown.
	*/
	@Transactional(readOnly = true)
	private String getSessionPlanModuleId(int videoContentId) throws SQLException{
		String sessionPlanModuleId = null;
		
		//Create JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Prepare SQL query
		String GET_SESSION_PLAN_MODULE_ID = "SELECT sessionPlanModuleId FROM acads.video_content WHERE id = ? ";
		
		//Execute JdbcTemplate query method
		sessionPlanModuleId = jdbcTemplate.queryForObject(GET_SESSION_PLAN_MODULE_ID, String.class, videoContentId);
		
		//Module Id
		return sessionPlanModuleId;
	}//getSessionPlanModuleId()
	
	@Transactional(readOnly = true)
	public Post findSessionPostByReferenceId(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from lti.post where referenceId =? and type='SessionVideo'";

		return (Post) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(Post.class));
	}

	public void deleteVideoContentPost(int id) {
		String sql1 = "Delete from lti.post where post_id=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql1, new Object[] { id });
	}

	/*
	 * public String deleteFromRedis(Post posts) { RestTemplate restTemplate = new
	 * RestTemplate(); try { String url =
	 * SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId"; System.out.
	 * println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	 * HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
	 * 
	 * return restTemplate.exchange( url, HttpMethod.POST, entity,
	 * String.class).getBody(); } catch (RestClientException e) {
	 *    return "Error IN rest call got "+e.getMessage(); } }
	 */
	public String refreshRedis(Post posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
			String url = SERVER_PATH + "timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<Post> entity = new HttpEntity<Post>(posts, headers);

			return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			  
			return "Error IN rest call got " + e.getMessage();
		}
	}

	public int deleteVideoContentIdAndMasterKeyMapping(Long id, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.videocontentid_consumerprogramstructureid_mapping"
				+ " where videoContentId=? and consumerProgramStructureId=?";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id, consumerProgramStructureId });
		} catch (DataAccessException e) {
			  
		}
		return row;
	}

	@Transactional(readOnly = true)
	public int getCountOfProgramsContentApplicableToByVideoId(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "   	count(*)  " + "FROM "
				+ "    acads.videocontentid_consumerprogramstructureid_mapping ccm " + "WHERE "
				+ "    ccm.videoContentId = " + id + " ";
		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
			return count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			return 0;
		}
	}
	/*
	 * commmented and kept as will require later by PS 28May public
	 * Page<VideoContentBean> getVideoContentPage(int pageNo, int pageSize,
	 * ArrayList<String> allsubjects, ArrayList<String> academicCycleListForDb,
	 * StudentBean student ) { jdbcTemplate = new JdbcTemplate(dataSource); String
	 * subjectCommaSeparated = "''"; if(allsubjects==null) { subjectCommaSeparated =
	 * "''"; }else { for (int i = 0; i < allsubjects.size(); i++) { if (i == 0) {
	 * subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
	 * } else { subjectCommaSeparated = subjectCommaSeparated + ", '" +
	 * allsubjects.get(i).replaceAll("'", "''") + "'"; } } }
	 * 
	 * String cycleCommaSeparated = "''"; if(academicCycleListForDb==null) {
	 * cycleCommaSeparated = "''"; }else { for (int i = 0; i <
	 * academicCycleListForDb.size(); i++) {
	 * if(StringUtils.isBlank(academicCycleListForDb.get(i))) { continue; } if (i ==
	 * 0) { cycleCommaSeparated = "'" +
	 * academicCycleListForDb.get(i).replaceAll("'", "''") + "'"; } else {
	 * cycleCommaSeparated = cycleCommaSeparated + ", '" +
	 * academicCycleListForDb.get(i).replaceAll("'", "''") + "'"; } } }
	 * 
	 * String
	 * sql="select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.* "
	 * + " from acads.video_content v " + "left join " + "	acads.faculty f " +
	 * "    on f.facultyId = v.facultyId " + "left join " + "	acads.sessions s " +
	 * "	on s.id = v.sessionId" + " where " +
	 * " v.subject in ("+subjectCommaSeparated+") " +
	 * " and concat(v.month,v.year) in ("+cycleCommaSeparated+") " +
	 * " Order By v.id desc "; String sql = "SELECT  " + "    s.track AS track, " +
	 * "    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " +
	 * "    v.* " + "FROM " + "    acads.video_content v " + "        LEFT JOIN " +
	 * "    acads.faculty f ON f.facultyId = v.facultyId " + "        LEFT JOIN " +
	 * "    acads.sessions s ON s.id = v.sessionId " + "        LEFT JOIN " +
	 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
	 * + "WHERE " + "    v.subject IN ("+subjectCommaSeparated+") " +
	 * "        AND CONCAT(v.month, v.year) IN ("+cycleCommaSeparated+") ";
	 * if(student != null) { sql +=
	 * "        AND vcm.consumerProgramStructureId = "+student.
	 * getConsumerProgramStructureId()+" "; } sql += " GROUP BY v.id " +
	 * "ORDER BY v.id DESC";
	 * 
	 * String countSql="select count(*) from acads.video_content v " + "left join "
	 * + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join " +
	 * "	acads.sessions s " + "	on s.id = v.sessionId" + " where v.subject in ("
	 * +subjectCommaSeparated+") and concat(v.month,v.year) in ("
	 * +cycleCommaSeparated+")  Order By v.id desc";
	 * 
	 * String countSql = "SELECT  count(v.id) " + "FROM " +
	 * "    acads.video_content v " + "        LEFT JOIN " +
	 * "    acads.faculty f ON f.facultyId = v.facultyId " + "        LEFT JOIN " +
	 * "    acads.sessions s ON s.id = v.sessionId " + "        LEFT JOIN " +
	 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
	 * + "WHERE " + "    v.subject IN ("+subjectCommaSeparated+") " +
	 * "        AND CONCAT(v.month, v.year) IN ("+cycleCommaSeparated+") ";
	 * if(student != null) { countSql +=
	 * "        AND vcm.consumerProgramStructureId = "+student.
	 * getConsumerProgramStructureId()+" "; } countSql += "  " +
	 * "ORDER BY v.id DESC";
	 * 
	 * PaginationHelper<VideoContentBean> pagingHelper = new
	 * PaginationHelper<VideoContentBean>(); Page<VideoContentBean> page; try { page
	 * = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, null, pageNo, pageSize,
	 * new BeanPropertyRowMapper(VideoContentBean.class)); } catch (Exception e) {
	 * page =new Page<VideoContentBean>();    }
	 * 
	 * return page; }
	 */

	// getVideoContentPage Old Start

	@Transactional(readOnly = true)
	public PageAcads<VideoContentAcadsBean> getVideoContentPage(int pageNo, int pageSize, ArrayList<String> allsubjects,
			ArrayList<String> academicCycleListForDb, StudentAcadsBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		if (allsubjects == null) {
			subjectCommaSeparated = "''";
		} else {
			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''")
							+ "'";
				}
			}
		}

		String cycleCommaSeparated = "''";
		if (academicCycleListForDb == null) {
			cycleCommaSeparated = "''";
		} else {
			for (int i = 0; i < academicCycleListForDb.size(); i++) {
				if (i == 0) {
					cycleCommaSeparated = "'" + academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
				} else {
					cycleCommaSeparated = cycleCommaSeparated + ", '"
							+ academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
				}
			}
		}
		
		String programCheck = "";
		if (student != null) {
			if(student.getConsumerProgramStructureId().equalsIgnoreCase("113")) {
				programCheck = " AND s.corporateName = 'M.sc'";
			}else {
				programCheck = " AND (s.corporateName <> 'M.sc' or s.corporateName is null or s.corporateName = '')";
			}
		}
		

		/*String sql = "select s.track as track, concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.id, v.sessionId, "
				+ "v.fileName, v.keywords, v.description, v.subject, v.defaultVideo, v.duration, v.sessionDate, v.addedOn, "
				+ "v.addedBy, v.year, v.month, v.createdBy, v.createdDate, v.lastModifiedDate, v.lastModifiedBy, v.videoLink, "
				+ "v.thumbnailUrl, v.mobileUrlHd, v.mobileUrlSd1, v.mobileUrlSd2, v.sessionPlanModuleId, v.videoTranscriptUrl,"
				+ "s.meetingKey, f.facultyId as facultyId from acads.video_content v "
				+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
				+ "	acads.sessions s " + "	on s.id = v.sessionId" + " where v.subject in (" + subjectCommaSeparated
				+ ") and concat(v.month,v.year) in (" + cycleCommaSeparated + ") Order By v.id desc"; */
		
		String sql =" SELECT  " + 
					"    s.track AS track, " + 
					"    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    v.id, v.sessionId,v.fileName,v.keywords,v.description,v.subject,v.defaultVideo,v.duration,v.sessionDate, " +
					"	 v.addedOn,v.addedBy,v.year,v.month, v.createdBy,v.createdDate,v.lastModifiedDate,v.lastModifiedBy, " +
					" 	 v.videoLink,v.thumbnailUrl, v.mobileUrlHd,v.mobileUrlSd1,v.mobileUrlSd2,v.sessionPlanModuleId, " +
					" 	 v.videoTranscriptUrl,s.meetingKey,f.facultyId AS facultyId " + 
					" FROM " + 
					"    acads.video_content v " + 
					"        LEFT JOIN " + 
					"    acads.faculty f ON f.facultyId = v.facultyId " + 
					"        LEFT JOIN " + 
					"    acads.sessions s ON s.id = v.sessionId " + 
					" WHERE " + 
					"    v.subject IN (" + subjectCommaSeparated + ") " + 
					" 	AND concat(v.month,v.year) in (" + cycleCommaSeparated + ") " +
						programCheck +
					" ORDER BY v.id DESC" ;
		
		String countSql = "select count(*) from acads.video_content v " + "left join " + "	acads.faculty f "
						+ "    on f.facultyId = v.facultyId " + "left join " + "	acads.sessions s "
						+ "	on s.id = v.sessionId" + " where v.subject in (" + subjectCommaSeparated
						+ ") and concat(v.month,v.year) in (" + cycleCommaSeparated + ") "
						+ programCheck
						+ "Order BY v.id DESC";
		
		
		PaginationHelper<VideoContentAcadsBean> pagingHelper = new PaginationHelper<VideoContentAcadsBean>();
		PageAcads<VideoContentAcadsBean> page;
		try {
			page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, null, pageNo, pageSize,
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			page = new PageAcads<VideoContentAcadsBean>();
			  
		}		
		return page;
	}
	// getVideoContentPage Old end

	@Transactional(readOnly = true)
	public ArrayList<String> getAcademicCycleList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT concat(month,year) FROM acads.video_content"
				+ " where (month is not null) and (year is not null AND year <> '') " + " group by concat(month,year)";
		ArrayList<String> academicCycleList = new ArrayList<String>();
		try {
			academicCycleList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {},
					new SingleColumnRowMapper(String.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return academicCycleList;
	}
	
	/***
	 * This method is used to fetch unique month and year for academic cycle
	 * @return list of year and month
	 */
	@Transactional(readOnly = true)
	public List<SearchBean> getAcademicCycle(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<SearchBean> academicCycleList = new ArrayList<SearchBean>();
		
		//Prepare SQL query
		String SQL = "SELECT month,year from acads.video_content WHERE (month='Jan' OR month='Jul') "
				+ " and (year is not null AND year <> '') group by month,year ";
		
		academicCycleList = jdbcTemplate.query(SQL,new Object[] {},new BeanPropertyRowMapper<SearchBean>(SearchBean.class));
		
		return academicCycleList;
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForSubjectYearMonth(String subject, Integer year, String month) {
		// String sql = "select * from acads.video_content where subject=? and year=?
		// and month=?";
		String sql = "select v.*,s.meetingKey, s.track from acads.video_content v "
				+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject=? and v.year=? and v.month=?";
		ArrayList<VideoContentAcadsBean> videoContentList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql,
				new Object[] { subject, year, month }, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		return videoContentList;
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForSubject(String subject, ContentAcadsBean content) {
		
		String sql =" SELECT v.*, s.meetingKey, s.track, s.startTime  " + 
					"	FROM acads.video_content v " + 
					"        INNER JOIN  acads.sessions s ON s.id = v.sessionId " + 
					" WHERE " + 
					"    v.subject = ? " + 
					"        AND v.year = ?  AND v.month = ? " + 
					" ORDER BY sessionId DESC , s.startTime DESC ";

		ArrayList<VideoContentAcadsBean> videoContentList = new ArrayList<VideoContentAcadsBean>();
		try {
			videoContentList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { subject, content.getYear(),content.getMonth() },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return videoContentList;
	}
	
	//________________________________________ Changes for leads, so that they will have their own video content _______________________________________________
	
	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForSubjectForLeads(String subject){

		String sql = "select v.* from acads.video_content_forleads v "
				+ "where v.subject = ? and v.year="+getLiveAcadConentYear()+" and v.month='"+getLiveAcadConentMonth()+"' order by sessionId DESC , s.startTime DESC";
				
		ArrayList<VideoContentAcadsBean> videoContentList=new ArrayList<VideoContentAcadsBean>();		
		try {
			videoContentList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql, 
					new Object[]{subject}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));					
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return videoContentList;
	}
	
	/* CRUD for video Subtopics Start */
	public long saveVideoSubTopic(final VideoContentAcadsBean VideoContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement("INSERT INTO acads.video_content_subtopics"
							+ " (parentVideoId,fileName,startTime,endTime,duration,description,keywords,videoLink,addedBy,addedOn,subject,thumbnailUrl,sessionId) "
							+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
					statement.setLong(1, VideoContent.getParentVideoId());
					statement.setString(2, VideoContent.getFileName());
					statement.setString(3, VideoContent.getStartTime());
					statement.setString(4, VideoContent.getEndTime());
					statement.setString(5, VideoContent.getDuration());
					statement.setString(6, VideoContent.getDescription());
					statement.setString(7, VideoContent.getKeywords());
					statement.setString(8, VideoContent.getVideoLink());
					statement.setString(9, VideoContent.getAddedBy());
					statement.setString(10, VideoContent.getAddedOn());
					statement.setString(11, VideoContent.getSubject());
					statement.setString(12, VideoContent.getThumbnailUrl());
					statement.setInt(13, VideoContent.getSessionId());
					return statement;
				}
			}, holder);
		} catch (DataAccessException e) {
			  
		}

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}

	public boolean updateVideoSubTopic(VideoContentAcadsBean VideoContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.video_content_subtopics set " + " parentVideoId=?, " + "fileName=?, "
				+ "startTime=?, " + "endTime=?, " + "duration=?, " + "keywords=?, " + "description=?, "
				+ "videoLink=?, " + "subject=?, " + "thumbnailUrl=? " + "where id=?";
		try {
			jdbcTemplate.update(sql,
					new Object[] { VideoContent.getParentVideoId(), VideoContent.getFileName(),
							VideoContent.getStartTime(), VideoContent.getEndTime(), VideoContent.getDuration(),
							VideoContent.getKeywords(), VideoContent.getDescription(), VideoContent.getVideoLink(),
							VideoContent.getSubject(), VideoContent.getThumbnailUrl(), VideoContent.getId() });
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return false;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAllVideoSubTopicsList(Long parentVideoId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		String sql = "select * from acads.video_content_subtopics where parentVideoId=? Order By id ";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { parentVideoId },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public VideoContentAcadsBean getVideoSubTopicById(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		VideoContentAcadsBean VideoContent = null;
		String sql = "select * from acads.video_content_subtopics where id=?";
		try {
			VideoContent = (VideoContentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContent;
	}

	public int deleteVideoSubTopic(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.video_content_subtopics where id=?";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
		} catch (DataAccessException e) {
			  
		}
		return row;
	}
	/* CRUD for video Subtopics End */

	/* Queries related code Start */
	//Passed session_id by Saurabh
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForSessionByStudent(String sapId, String session_id) {
		String sql = "Select sqa.* from acads.session_query_answer sqa " + 
				"where sqa.sapid = ? " + 
				"and sqa.sessionId=? " + 
				"order by sqa.createdDate desc;";
		List<SessionQueryAnswer> myQueries = jdbcTemplate.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setString(1, sapId);
				ps.setString(2, session_id);
			}
		}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswer> getQueriesForCourseByStudent(String subject, String sapId) {
		String sql = "Select * from acads.session_query_answer where " + " sapid = ? " + " and subject = ? "
				+ " and queryType = 'Course Query' " + " order by createdDate desc";
		List<SessionQueryAnswer> myCourseQueries = jdbcTemplate.query(sql, new Object[] { sapId, subject },
				new BeanPropertyRowMapper(SessionQueryAnswer.class));
		return myCourseQueries;
	}
	
	/* Queries related code End */

	// for mobile api
	@Transactional(readOnly = true)
	public List<String> getSubjectsWithVideos(ArrayList<String> allsubjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		if (allsubjects == null) {
			subjectCommaSeparated = "''";
		} else {
			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''")
							+ "'";
				}
			}
		}

		String sql = "select subject from acads.video_content where subject in (" + subjectCommaSeparated
				+ ") AND sessionId <> 0 Group By subject";
		List<String> subjectsWithVideo = null;
		try {
			subjectsWithVideo = (List<String>) jdbcTemplate.queryForList(sql, String.class);
		} catch (Exception e) {
			  
		}

		return subjectsWithVideo;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideosForSession(String sessionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT "
					+ "		CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, "
					+ "		v.*,s.meetingKey,s.track "
					+ " FROM "
					+ "		acads.video_content v "
					+ " 		INNER JOIN "
					+ "		acads.sessions s ON s.id = v.sessionId " 
					+ "			INNER JOIN " 
					+ "		acads.faculty f ON f.facultyId = v.facultyId " 
					+ " WHERE v.sessionId = ?";

		List<VideoContentAcadsBean> sessionVideos = new ArrayList<VideoContentAcadsBean>();
		try {
			sessionVideos = jdbcTemplate.query(sql, new Object[] { sessionId }, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return sessionVideos;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideosForSessionFromTemp(String sessionId,List<Integer> currentSemPSSId, String acadDateFormat) {
		List<VideoContentAcadsBean> sessionVideos = new ArrayList<VideoContentAcadsBean>();
		
		//Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		//Prepare SQL Query
		String sql =  new StringBuilder("select firstName,lastName,id,sessionId,subject,facultyId from acads.quick_video_content ")
				.append("where sessionId=:sessionId and program_sem_subject_id in (:PSSIdsList) and acadDateFormat = :acadDateFormat ").toString();
	
		//Adding parameters in SQL parameter map.
		queryParams.addValue("sessionId", sessionId);
		queryParams.addValue("PSSIdsList", currentSemPSSId);
		queryParams.addValue("acadDateFormat", acadDateFormat);
		
		try {
			//Execute namedJdbcTemplate query method
			sessionVideos = namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}

		//return session videos list.
		return sessionVideos;
	}

	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentsMostRecentRegistrationData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentAcadsBean student = null;
		try {
			String sql = "SELECT *   FROM exam.registration r where "
					+ "    r.sapid = ?  and r.sem = (Select max(sem) from exam.registration where sapid = ? )  ";


			student = (StudentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid, sapid },
					new BeanPropertyRowMapper(StudentAcadsBean.class));

			// set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

			return student;
		} catch (Exception e) {
			return null;
			//   
		}

	}

	// Get all faculties by students subjects start
	@Transactional(readOnly = true)
	public List<FacultyAcadsBean> getFacultiesForSubjects(ArrayList<String> allsubjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = "''";
		for (int i = 0; i < allsubjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		String sql = "select * from acads.faculty  " + "	where active='Y'  " + "		and "
				+ "			facultyId in " + "			(SELECT facultyId FROM acads.video_content "
				+ "				where " + "                 subject in (" + subjectCommaSeparated + ") "
				+ "                group by facultyId)";
		List<FacultyAcadsBean> facultyList = null;
		try {
			facultyList = (List<FacultyAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(FacultyAcadsBean.class));
		} catch (Exception e) {
			  
		}

		return facultyList;
	}
	// Get all faculties by students subjects end

	@Transactional(readOnly = true)
	public HashMap<String, SessionDayTimeAcadsBean> getAllSessionsDetails() {
		String sql = "select * from acads.sessions";
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, SessionDayTimeAcadsBean> sessionMap = new HashMap<String, SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> sessionList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		if (!sessionList.isEmpty()) {
			for (SessionDayTimeAcadsBean bean : sessionList) {
				if (!sessionMap.containsKey(bean.getId())) {
					sessionMap.put(bean.getId(), bean);
				}
			}
		}

		return sessionMap;
	}

	@Transactional(readOnly = true)
	public List<String> getMasterKeysToSessionIdBySubjectAndCorporateName(String subject, String corporateName) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "    pss.consumerProgramStructureId " + "FROM " + "    exam.program_sem_subject pss, "
				+ "    exam.consumer_type ct, " + "    exam.consumer_program_structure cps " + "WHERE "
				+ "    pss.subject = ? " + "        AND ct.name = ? "
				+ "        AND pss.consumerProgramStructureId = cps.id " + "        AND cps.consumerTypeId = ct.id";


		List<String> data = new ArrayList<>();
		try {
			data = (List<String>) jdbcTemplate.query(sql, new Object[] { subject, corporateName },
					new SingleColumnRowMapper(String.class));
		} catch (DataAccessException e) {
			  
		}
		return data;
	}

	public String batchInsertVideoCententIdAndMasterKeyMappings(
			final List<VideoContentAcadsBean> videoCententIdAndMasterKeyMappings) {
		String sql = "INSERT INTO  acads . videocontentid_consumerprogramstructureid_mapping  " + "( videoContentId , "
				+ " consumerProgramStructureId , " + " createdBy , " + " createdDate ) " + "VALUES " + "( ?, " + " ?, "
				+ " ?, " + " sysdate()) " + " on duplicate key " + " update "
				+ "		createdBy =?, createdDate = sysdate() " + "";

		jdbcTemplate = new JdbcTemplate(dataSource);
		String errorMessage = "";
		try {
			if (videoCententIdAndMasterKeyMappings != null) {
			}
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					VideoContentAcadsBean m = videoCententIdAndMasterKeyMappings.get(i);
					ps.setLong(1, m.getId());
					ps.setString(2, m.getConsumerProgramStructureId());
					ps.setString(3, m.getCreatedBy());

					ps.setString(4, m.getCreatedBy());
					if (i % 500 == 0) {
					}
				}

				public int getBatchSize() {
					return videoCententIdAndMasterKeyMappings.size();
				}
			});
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			errorMessage += e.getMessage();
		}

		return errorMessage;
	}

	@Transactional(readOnly = true)
	public Map<Long, Integer> getVideoContentIdNCountOfProgramsApplicableToMap(String ids) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "    count(c.id) as countOfProgramsApplicableTo ,c.id " + "FROM "
				+ "    acads.video_content c, " + "    acads.videocontentid_consumerprogramstructureid_mapping ccm "
				+ "WHERE " + "    c.id = ccm.videoContentId " + "    AND c.id in (" + ids + ") " + "GROUP BY c.id  "
				+ "ORDER BY c.id DESC ";
		try {
			List<VideoContentAcadsBean> list = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			Map<Long, Integer> map = new HashMap<>();
			for (VideoContentAcadsBean bean : list) {
				map.put(bean.getId(), bean.getCountOfProgramsApplicableTo());
			}

			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			return new HashMap<>();
		}
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getProgramsListForCommonVideoContent(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> contentList = new ArrayList<>();
		String sql = "SELECT   " + "    c.id,c.year,c.month,c.subject,"
				+ "  ct.id as consumerTypeId,ct.name as consumerType," + "  p.id as programId,p.code as program,"
				+ "  ps.id as programStructureId,ps.program_structure as programStructure,"
				+ "  ccm.consumerProgramStructureId  " + "FROM  " + "    acads.video_content c,  "
				+ "    acads.videocontentid_consumerprogramstructureid_mapping ccm,  " + "    exam.consumer_type ct,  "
				+ "    exam.consumer_program_structure cps,  " + "    exam.program p,  "
				+ "    exam.program_structure ps  " + "      " + "WHERE  " + "    c.id = ccm.videoContentId  "
				+ "        AND ccm.consumerProgramStructureId = cps.id  " + "        AND cps.consumerTypeId = ct.id  "
				+ "        AND cps.programId = p.id  " + "        AND cps.programStructureId = ps.id  "
				+ "        AND c.id = ?  " + "   " + "ORDER BY c.id DESC";

		try {
			contentList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return contentList;
	}
	// editSingleVideoContentFromCommonSetup

	public String deleteVideoContentAndMasterKeyMappingsById(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.videocontentid_consumerprogramstructureid_mapping where videoContentId=?";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
			return "";
		} catch (Exception e) {
			  
			return e.getMessage();
		}
	}

	@Transactional(readOnly = true)
	public String getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(String programId,
			String programStructureId, String consumerTypeId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String consumerProgramStructureId = null;

		String sql = "SELECT id FROM exam.consumer_program_structure where consumerTypeId = ? and programId = ? and programStructureId = ?";

		try {

			consumerProgramStructureId = (String) jdbcTemplate.queryForObject(sql,
					new Object[] { consumerTypeId, programId, programStructureId }, String.class);

		} catch (Exception e) {
			  
		}

		return consumerProgramStructureId;

	}

	public int deleteIdNConsumerProgramStructureIdMapping(Long id, String consumerProgramStructureId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.videocontentid_consumerprogramstructureid_mapping "
				+ "	 where videoContentId=? and consumerProgramStructureId=? ";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id, consumerProgramStructureId });
		} catch (Exception e) {
			  
		}
		return row;

	}

	public String deleteVideoContentByListOfIds(List<Long> idsOfAddedContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String ids = "";
		if (idsOfAddedContent != null && idsOfAddedContent.size() > 0) {
			ids = StringUtils.join(idsOfAddedContent, ",");
		}
		if (StringUtils.isBlank(ids)) {
			return "Unable to create ids list.";
		}
		String sql = "delete from acads.video_content where videoContentId in (" + ids + ") ";
		try {
			row = jdbcTemplate.update(sql, new Object[] {});
			return "";
		} catch (Exception e) {
			  
			return e.getMessage();
		}
	}

	public String deleteVideoContentIdAndMasterKeyMappingByListOfIds(List<Long> listOfIds) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String ids = "";
		if (listOfIds != null && listOfIds.size() > 0) {
			ids = StringUtils.join(listOfIds, ",");
		}
		if (StringUtils.isBlank(ids)) {
			return "Unable to create ids list.";
		}
		String sql = "delete from acads.videocontentid_consumerprogramstructureid_mapping where videoContentId in ("
				+ ids + ") ";
		try {
			row = jdbcTemplate.update(sql, new Object[] {});
			return "";
		} catch (Exception e) {
			  
			return e.getMessage();
		}
	}

	@Transactional(readOnly = true)
	public Long getPostIdByVideoId(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "   	post_id  " + "FROM " + "    lti.post " + "WHERE "
				+ "  type='SessionRecording'  " + " and referenceId = " + id + " ";
		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
			return (long) count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			return null;
		}
	}

		@Transactional(readOnly = true)
		public ArrayList<VideoContentAcadsBean> getAllSessionRecording(VideoContentAcadsBean bean){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<VideoContentAcadsBean> sessionRecordingList = null;
			
			String checkProgramType = "";
			if (bean.getProgramType().equalsIgnoreCase("MBA - WX")) {
				checkProgramType = " AND sessionPlanModuleId IS NOT NULL AND sessionPlanModuleId <> 0 ";
			}else {
				checkProgramType = " AND ( sessionPlanModuleId IS NULL OR sessionPlanModuleId = 0 ) ";
			}
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql = "select * from acads.video_content where 1 = 1 " ;
			
			if (!StringUtils.isBlank(bean.getSessionDate())) {
				sql = sql + " AND sessionDate = ? ";
				parameters.add(bean.getSessionDate());
			}
			
			if (!StringUtils.isBlank(bean.getYear())) {
				sql = sql + " AND year = ? ";
				parameters.add(bean.getYear());
			}
			
			if (!StringUtils.isBlank(bean.getMonth())) {
				sql = sql + " AND month = ? ";
				parameters.add(bean.getMonth());
			}
			
			sql = sql + checkProgramType;
			
			Object[] args = parameters.toArray();
			
			sessionRecordingList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			
			return sessionRecordingList;

	}
		
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAcadsSessionsList(List<VideoContentAcadsBean> videoList,
			VideoContentAcadsBean bean) throws RuntimeException, Exception {

		logger.info("Entering getAcadsSessionsList() method of VideoContentDAO");

		List<VideoContentAcadsBean> sessionRecordingListFromSessions = Collections.synchronizedList(new ArrayList<>());

		RowMapper<VideoContentAcadsBean> sessionsRowMapper = (rs, rowNum) -> {
			VideoContentAcadsBean videoContentAcadsBean = new VideoContentAcadsBean();
			videoContentAcadsBean.setId(rs.getLong("id"));
			videoContentAcadsBean.setSessionName(rs.getString("sessionName"));
			videoContentAcadsBean.setTrack(rs.getString("track"));
			return videoContentAcadsBean;
		};

		List<Object> parameters = new ArrayList<Object>();

		parameters.add(bean.getYear());
		parameters.add(bean.getMonth());
		
		String sqlSessions = "SELECT id, sessionName, track FROM acads.sessions WHERE year = ? AND month = ?";
						
		if (bean.getSessionDate() != "") {
			sqlSessions = sqlSessions + " AND date = ? ";
			parameters.add(bean.getSessionDate());
		}

		if (bean.getProgramType().equalsIgnoreCase("MBA - WX")) {
			sqlSessions = sqlSessions + " AND hasModuleId = 'Y'";
		} else {
			sqlSessions = sqlSessions + " AND ( hasModuleId IS NULL OR hasModuleId = 'N' )";
		}		
		
		Object[] args = parameters.toArray();

		sessionRecordingListFromSessions = (List<VideoContentAcadsBean>) jdbcTemplate.query(sqlSessions, args,
				sessionsRowMapper);

		for (VideoContentAcadsBean videoElement : videoList) {
			for (VideoContentAcadsBean sessionElement : sessionRecordingListFromSessions) {
				if (Integer.toString(videoElement.getSessionId())
						.equalsIgnoreCase(Long.toString(sessionElement.getId()))) {
					videoElement.setSessionName(sessionElement.getSessionName());
					videoElement.setTrack(sessionElement.getTrack());
				}
			}
		}

		logger.info("Exiting getAcadsSessionsList() method of VideoContentDAO");

		return sessionRecordingListFromSessions;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAcadsFacultyList(List<VideoContentAcadsBean> videoList,
			VideoContentAcadsBean bean) throws RuntimeException, Exception {

		logger.info("Entering getAcadsFacultyList() method of VideoContentDAO");

		List<VideoContentAcadsBean> sessionRecordingListFromFaculty = Collections.synchronizedList(new ArrayList<>());

		RowMapper<VideoContentAcadsBean> facultyRowMapper = (rs, rowNum) -> {
			VideoContentAcadsBean videoContentAcadsBean = new VideoContentAcadsBean();
			videoContentAcadsBean.setFacultyId(rs.getString("facultyId"));
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			videoContentAcadsBean.setFacultyName(firstName + " " + lastName);
			return videoContentAcadsBean;
		};

		String sqlFaculty = "SELECT facultyId, firstName, lastName FROM acads.faculty;";

		sessionRecordingListFromFaculty = (List<VideoContentAcadsBean>) jdbcTemplate.query(sqlFaculty,
				facultyRowMapper);

		for (VideoContentAcadsBean videoElement : videoList) {
			for (VideoContentAcadsBean facultyElement : sessionRecordingListFromFaculty) {
				if (videoElement.getFacultyId().equalsIgnoreCase(facultyElement.getFacultyId())) {
					videoElement.setProgramName(facultyElement.getProgramName());
					videoElement.setFacultyName(facultyElement.getFacultyName());
				}
			}
		}

		logger.info("Exiting getAcadsFacultyList() method of VideoContentDAO");

		return sessionRecordingListFromFaculty;
	}
	
		
		

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVimeoId(String acadYear, String acadMonth) {
		String sql = "select id, SUBSTRING(videoLink, 32) AS vimeoId from acads.video_content where duration is NULL and year = ? and month = ? ";
		return (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { acadYear, acadMonth },
				new BeanPropertyRowMapper(VideoContentAcadsBean.class));
	}

	public void updateVimeoDuration(String vimeoId, Long id) {
		String sql = "UPDATE `acads`.`video_content` SET `duration`= ? where id = ?";
		jdbcTemplate.update(sql, new Object[] { vimeoId, id });
	}

	@Transactional(readOnly = true)
	public List<String> getBatchTracks(){
		String sql= "select distinct(track) from acads.sessions where track is not null and track !=''";
		return (ArrayList<String>) jdbcTemplate.query(sql,  new SingleColumnRowMapper(String.class));
	}

//	public boolean checkIfBookmarked(String sapId, String contentId){
//		jdbcTemplate = new JdbcTemplate(dataSource);
//
//		String sql = "select count(*) from bookmarks.content_bookmarks cb where cb.sapid=? and cb.content_id=?";
//
//		int count = (int)jdbcTemplate.queryForObject(sql, new Object[]{sapId, contentId}, new SingleColumnRowMapper(Integer.class));
//		if(count==0){
//			return false;
//		} else{
//			return true;
//		}
//	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForSubjectYearMonthAndSapId(String subject, Integer year, String month, String sapId) {
		// String sql = "select * from acads.video_content where subject=? and year=?
		// and month=?";
		String sql = "select v.*,s.meetingKey, s.track, cb.bookmarked from acads.video_content v "
				+ "inner join acads.sessions s on s.id = v.sessionId "
				+ "left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=v.id "
				+ "where v.subject=? and v.year=? and v.month=?";
		ArrayList<VideoContentAcadsBean> videoContentList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql,
				new Object[] { sapId, subject, year, month }, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		
		return videoContentList;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getRelatedVideoContentListBySapId(String searchItem, String subject,
															 ArrayList<String> allsubjects, StudentAcadsBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		List<VideoContentAcadsBean> VideoTopicsList = null;
		String sql = "";
		String sqlTopics = "";
		String subjectCommaSeparated = "''";
		String tempSubject = "";

		// subject = StringEscapeUtils.escapeJava(subject);
		for (int i = 0; i < allsubjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		if (!"".equals(searchItem) && subject == null) {

//			sql="select * from acads.video_content where subject in ("+subjectCommaSeparated+") and (subject Like '%"+ searchItem.trim() +"%' or keywords Like '%"+ searchItem.trim() +"%' or description Like '%"+ searchItem.trim() +"%' or fileName Like '%"+searchItem+"%' ) Order By id desc";
			sql = "select v.*,s.meetingKey, cb.bookmarked from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId "
					+ "left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=v.id "
					+ "where v.subject in (" + subjectCommaSeparated + ") and " + "(v.subject Like '%" + searchItem.trim()
					+ "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%" + searchItem.trim()
					+ "%' or v.fileName Like '%" + searchItem + "%' )";

//			sqlTopics = "select * from acads.video_content_subtopics where subject in (" + subjectCommaSeparated
//					+ ") and (keywords Like '%" + searchItem.trim() + "%' or description Like '%" + searchItem.trim()
//					+ "%'or fileName Like '%" + searchItem + "%' ) Order By id desc";

//			sql = "select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm "
//					+ "where subject in (" + subjectCommaSeparated + ") " + " and (subject Like '%" + searchItem.trim()
//					+ "%' " + " or keywords Like '%" + searchItem.trim() + "%' " + " or description Like '%"
//					+ searchItem.trim() + "%' " + " or fileName Like '%" + searchItem + "%' ) "
//					+ " and v.id = vcm.videoContentId ";
//			if (student != null) {
//				sql += " and vcm.consumerProgramStructureId= " + student.getConsumerProgramStructureId() + " ";
//			}
			sql += " group By v.id " + " Order By v.id desc";
			sqlTopics = "select * from acads.video_content_subtopics " + " where subject in (" + subjectCommaSeparated
					+ ") " + " and (keywords Like '%" + searchItem.trim() + "%' " + " or description Like '%"
					+ searchItem.trim() + "%' " + " or fileName Like '%" + searchItem + "%' ) " + "Order By id desc";
		}
		if ("".equals(searchItem) && !"".equals(subject) && subject!=null) {
			subject.replaceAll("&", "_");
			subject = subject.replaceAll("'", "''");
			// sql="select * from acads.video_content where subject like '"+ subject +"'
			// Order By id desc";
			sql = "select v.*,s.meetingKey,s.track, cb.bookmarked from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId "
					+ "left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=v.id "
					+ "where v.subject like '" + subject + "' Order By v.id desc";
			/*
			 * commented by PS 29May
			 * sql="select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm "
			 * + " where subject like '"+ subject +"' " + " and v.id = vcm.videoContentId ";
			 * if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; } sql += " group By v.id " + " Order By v.id desc";
			 */

			sqlTopics = "select * from acads.video_content_subtopics where subject like '" + subject
					+ "' and id=0 Order By id desc";

		}
		if (!"".equals(searchItem) && !"".equals(subject) && subject!=null) {
			subject = subject.replaceAll("'", "''");
			// sql="select * from acads.video_content where subject in
			// ("+subjectCommaSeparated+") and (subject Like '%"+ subject +"%' or subject
			// Like '%"+ searchItem +"%' or keywords Like '%"+ searchItem.trim() +"%' or
			// description Like '%"+ searchItem.trim() +"%' or fileName Like
			// '%"+searchItem+"%') Order By id desc";
			sql = "select v.*,s.meetingKey,s.track, cb.bookmarked from acads.video_content v "
					+ "inner join acads.sessions s on s.id = v.sessionId "
					+ "left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=v.id "
					+ "where v.subject in (" + subjectCommaSeparated + ") " + "and (v.subject Like '%" + subject + "%' or v.subject Like '%"
					+ searchItem + "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%"
					+ searchItem.trim() + "%' or v.fileName Like '%" + searchItem + "%') Order By v.id desc";
			/*
			 * sql="select v.* from acads.video_content v, acads.videocontentid_consumerprogramstructureid_mapping vcm  "
			 * + " where subject in ("+subjectCommaSeparated+") " + " and (subject Like '%"+
			 * subject +"%' " + " or subject Like '%"+ searchItem +"%' " +
			 * " or keywords Like '%"+ searchItem.trim() +"%' " + " or description Like '%"+
			 * searchItem.trim() +"%' " + " or fileName Like '%"+searchItem+"%') " +
			 * " and v.id = vcm.videoContentId "; if(student != null) { sql +=
			 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
			 * ()+" "; } sql += " group By v.id " + " Order By v.id desc";
			 */

			sqlTopics = "select * from acads.video_content_subtopics where subject in (" + subjectCommaSeparated
					+ ") and  (subject Like '%" + subject + "%' or subject Like '%" + searchItem
					+ "%' or keywords Like '%" + searchItem.trim() + "%' or description Like '%" + searchItem.trim()
					+ "%' or fileName Like '%" + searchItem + "%') Order By id desc";
		}
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {student.getSapid()},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			VideoTopicsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sqlTopics, new Object[] {},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			VideoContentsList.addAll(VideoTopicsList);
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideosForSessionBySapId(String sessionId, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT "
					+ "		CONCAT('Prof. ',f.firstName,' ', f.lastName) AS facultyName, "
					+ " 	v.*, s.meetingKey, s.track, cb.bookmarked "
					+ "	FROM "
					+ "		acads.video_content v "
					+ " 		LEFT JOIN "
					+ " 	bookmarks.content_bookmarks cb ON cb.content_id=v.id AND cb.sapid = ? "
					+ " 		INNER JOIN "
					+ "		acads.sessions s ON s.id = v.sessionId " 
					+ "			INNER JOIN "
					+ "		acads.faculty f ON f.facultyId = v.facultyId " 
					+ " WHERE v.sessionId = ?";

		List<VideoContentAcadsBean> sessionVideos = new ArrayList<VideoContentAcadsBean>();
		try {
			sessionVideos = jdbcTemplate.query(sql, new Object[] { sapId, sessionId },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return sessionVideos;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcads> getProgramSemSubjectId(ArrayList<String> subjects,String consumerProgramStructureId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String sql =" SELECT consumerProgramStructureId, id AS programSemSubjectId, subject " + 
					"    FROM exam.program_sem_subject " + 
					" WHERE " + 
					"    subject IN (" + subjectCommaSeparated + ") " + 
					"        AND consumerProgramStructureId = ? ";
		
		ArrayList<ConsumerProgramStructureAcads> programSemSubjectIdList = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query
					(sql, new Object[]{consumerProgramStructureId}, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		

		return programSemSubjectIdList;
		
	}
	
	/***
	 * This method is used to get the subjectCodeIds based on applicable subjects
	 * @param applicableSubjects	Applicable subjects of a current student in single string
	 * @param consumerProgramStructureId current student's consumerProgramStructureId
	 * @return List<ConsumerProgramStructure> having the subjectCodeId, consumerProgramStructureId and subjectName 
	 * 										  as bean into the list.
	 * @throws java.sql.SQLException If there is problem {@code GET_SUBJECTCODEIDS}} while executing the DB query 
	 */
	@Transactional(readOnly = true)
	public List<ConsumerProgramStructureAcads> getSubjectCodeIdsByApplicableSub(String applicableSubjects,
															String consumerProgramStructureId) {

		List<ConsumerProgramStructureAcads> subjectCodeIdsList = null;
		StringBuilder GET_SUBJECTCODEIDS = null;

		// Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Creating StringBuilder Object
		GET_SUBJECTCODEIDS = new StringBuilder();
		
		// Preparing SQL Query
		GET_SUBJECTCODEIDS.append("SELECT scm.consumerProgramStructureId,scm.subjectCodeId,  scm.id as programSemSubjectId, ");
		GET_SUBJECTCODEIDS.append("sc.subjectname AS subjectName FROM exam.mdm_subjectcode sc ");
		GET_SUBJECTCODEIDS.append("INNER JOIN exam.mdm_subjectcode_mapping scm ON scm.subjectCodeId = sc.id WHERE ");
		GET_SUBJECTCODEIDS.append("sc.subjectname IN ( "+applicableSubjects+" ) AND scm.consumerProgramStructureId = ? ");
		
		try {
			// use JdbcTemplate
			subjectCodeIdsList = jdbcTemplate.query(GET_SUBJECTCODEIDS.toString(), rs -> {
				List<ConsumerProgramStructureAcads> consumerPrgStrgList = null;

				// Creating consumerPrgStrgList ArrayList for storing multiple ConsumerProgramStructure objects
				consumerPrgStrgList = new ArrayList<ConsumerProgramStructureAcads>();

				while (rs.next()) {
					ConsumerProgramStructureAcads consumerPrgStrg = new ConsumerProgramStructureAcads();
					consumerPrgStrg.setConsumerProgramStructureId(rs.getString(1));
					consumerPrgStrg.setSubjectCodeId(rs.getString(2));
					consumerPrgStrg.setProgramSemSubjectId(rs.getString(3));
					consumerPrgStrg.setSubjectName(rs.getString(4));

					// add consumerPrgStrg to list
					consumerPrgStrgList.add(consumerPrgStrg);
				}
				return consumerPrgStrgList;

			}, consumerProgramStructureId);
		} catch (Exception e) {
			subjectCodeIdsList = new ArrayList<ConsumerProgramStructureAcads>();
			  
		}
		
		//return subjectCodeIdsList having consumerProgramStructureId,subjectCodeId and subjectName
		return subjectCodeIdsList;
		
	}//getSubjectCodeIdsByApplicableSub()
	
	@Transactional(readOnly = true)
	public PageAcads<VideoContentAcadsBean> getVideoContentPageNew(int pageNo, int pageSize, ArrayList<String> programSemSubjectIds,
			ArrayList<String> academicCycleListForDb, StudentAcadsBean student, List<String> commonSubjects) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		if (programSemSubjectIds == null) {
			subjectCommaSeparated = "''";
		} else {
			for (int i = 0; i < programSemSubjectIds.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '" + programSemSubjectIds.get(i).replaceAll("'", "''")
							+ "'";
				}
			}
		}

		String cycleCommaSeparated = "''";
		if (academicCycleListForDb == null) {
			cycleCommaSeparated = "''";
		} else {
			for (int i = 0; i < academicCycleListForDb.size(); i++) {
				if (i == 0) {
					cycleCommaSeparated = "'" + academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
				} else {
					cycleCommaSeparated = cycleCommaSeparated + ", '"
							+ academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
				}
			}
		}
		
		String commonSubjectCommaSeparated = "''";
		for (int i = 0; i < commonSubjects.size(); i++) {
			if (i == 0) {
				commonSubjectCommaSeparated = "'" + commonSubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				commonSubjectCommaSeparated = commonSubjectCommaSeparated + ", '"
						+ commonSubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String sqlForCommonSub = "";
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())
				&& !"MBA - WX".equalsIgnoreCase(student.getProgram())){
			sqlForCommonSub = " AND ((programList like ('%"+student.getProgram()+"%')) OR (programList='All')) ";			
		}else {
			sqlForCommonSub = " AND programList LIKE ('%"+student.getProgram()+"%') ";
		}
		
		String sql =" SELECT v.id, v.sessionId, v.sessionDate, v.subject, v.fileName, v.keywords, " + 
					"    v.year, v.month, s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    v.description, v.defaultVideo, v.duration, v.addedOn, v.addedBy, " + 
					"    v.sessionPlanModuleId, v.videoTranscriptUrl, s.meetingKey, f.facultyId AS facultyId,  " + 
					"    v.createdBy,  v.createdDate, v.lastModifiedDate,  v.lastModifiedBy, " + 
					"    v.videoLink, v.thumbnailUrl, v.mobileUrlHd, v.mobileUrlSd1, v.mobileUrlSd2 " + 
					" FROM acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionid " + 
					"        INNER JOIN " + 
					"    acads.video_content v ON s.id = v.sessionId " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON v.facultyId = f.facultyId " + 
					" WHERE 1 = 1 " + 
					"    	 AND ssm.program_sem_subject_id IN ( " + subjectCommaSeparated + ") " + 
					" 		 AND concat(v.month,v.year) in (" + cycleCommaSeparated + ") " ;
		
		//For Common Sessions
		sql +=  " UNION " +
				" SELECT vcc.id, vcc.sessionId, vcc.sessionDate, vcc.subject, vcc.fileName, vcc.keywords, " + 
				"    vcc.year, vcc.month, s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
				"    vcc.description, vcc.defaultVideo, vcc.duration, vcc.addedOn, vcc.addedBy, " + 
				"    vcc.sessionPlanModuleId, vcc.videoTranscriptUrl, s.meetingKey, f.facultyId AS facultyId, " + 
				"    vcc.createdBy, vcc.createdDate, vcc.lastModifiedDate, vcc.lastModifiedBy, " + 
				"    vcc.videoLink, vcc.thumbnailUrl, vcc.mobileUrlHd, vcc.mobileUrlSd1, vcc.mobileUrlSd2 " + 
				" FROM acads.sessions s " + 
				"        INNER JOIN " + 
				"    acads.video_content vcc ON s.id = vcc.sessionId " + 
				"		 INNER JOIN " + 
				"	acads.faculty f on vcc.facultyId = f.facultyId " + 
				" WHERE 1 = 1 " + 
				"    AND vcc.subject IN (" + commonSubjectCommaSeparated + ") " + 
				  sqlForCommonSub +
				" ORDER BY id DESC" ;
		
		String countSql=" SELECT SUM(rowCount) FROM ( " +
						" SELECT COUNT(*) as rowCount " + 
						" FROM acads.sessions s " + 
						"        INNER JOIN " + 
						"    acads.session_subject_mapping ssm ON s.id = ssm.sessionid " + 
						"        INNER JOIN " + 
						"    acads.video_content v ON s.id = v.sessionId " + 
						"        INNER JOIN " + 
						"    acads.faculty f ON v.facultyId = f.facultyId " + 
						" WHERE 1 = 1 " + 
						"    	 AND ssm.program_sem_subject_id IN (" + subjectCommaSeparated + ") " + 
						" 		 AND concat(v.month,v.year) in (" + cycleCommaSeparated + ") " ;
		
		//For Common Sessions
		countSql += " UNION " +
					" SELECT COUNT(*) as rowCount " + 
					" FROM acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.video_content vcc ON s.id = vcc.sessionId " + 
					"		 INNER JOIN " + 
					"	acads.faculty f on vcc.facultyId = f.facultyId " + 
					" WHERE 1 = 1 " + 
					"    AND vcc.subject IN (" + commonSubjectCommaSeparated + ") " + 
					  sqlForCommonSub +
					"		) videoCount ";
		
		PaginationHelper<VideoContentAcadsBean> pagingHelper = new PaginationHelper<VideoContentAcadsBean>();
		PageAcads<VideoContentAcadsBean> page;
		try {
			page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, null, pageNo, pageSize, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			page = new PageAcads<VideoContentAcadsBean>();
			  
		}		
		return page;
	}
	
	/***
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param subjectCodeIds
	 * @param academicCycles
	 * @param program
	 * @param commonSubjects
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageAcads<VideoContentAcadsBean> getSessionVideoBySubjectCodeId(int pageNo, int pageSize, 
																String subjectCodeIds,
																String academicCycles, 
																String program, String commonSubjects ){
		StringBuilder GET_VIDEO_CONTENT = null, VIDEO_CONTENT_COUNT = null;
		PaginationHelper<VideoContentAcadsBean> pagingHelper =null;
		PageAcads<VideoContentAcadsBean> page = null;
		String PROGRM_LIKE_SQL = null;
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		VIDEO_CONTENT_COUNT = new StringBuilder();
		
		
		if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program))
			PROGRM_LIKE_SQL = "AND ((programList LIKE ('%"+program+"%')) OR (programList='All')) ";
		else	
			PROGRM_LIKE_SQL = "AND programList LIKE ('%"+program+"%') ";
		
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT.append("SELECT v.id,v.sessionId,v.sessionDate,v.subject,v.fileName,v.keywords,v.year, ");
		GET_VIDEO_CONTENT.append("v.month,s.track AS track,CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("v.description,v.defaultVideo,v.duration,v.addedOn,v.addedBy,v.sessionPlanModuleId, ");
		GET_VIDEO_CONTENT.append("v.videoTranscriptUrl,s.meetingKey,f.facultyId AS facultyId,v.createdBy,v.createdDate, ");
		GET_VIDEO_CONTENT.append("v.lastModifiedDate,v.lastModifiedBy,v.videoLink,v.thumbnailUrl, v.mobileUrlHd, ");
		GET_VIDEO_CONTENT.append("v.mobileUrlSd1,v.mobileUrlSd2 FROM  acads.sessions s ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionid ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.video_content v ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON v.facultyId = f.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE ssm.subjectCodeId IN ("+subjectCodeIds+") ");
		GET_VIDEO_CONTENT.append("AND CONCAT(v.month, v.year) IN ("+academicCycles+") ");
		GET_VIDEO_CONTENT.append("UNION SELECT vcc.id,vcc.sessionId,vcc.sessionDate,vcc.subject,vcc.fileName,vcc.keywords,vcc.year, ");
		GET_VIDEO_CONTENT.append("vcc.month,s.track AS track,CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("vcc.description,vcc.defaultVideo,vcc.duration,vcc.addedOn, vcc.addedBy,vcc.sessionPlanModuleId, ");
		GET_VIDEO_CONTENT.append("vcc.videoTranscriptUrl,s.meetingKey,f.facultyId AS facultyId,vcc.createdBy,vcc.createdDate, ");
		GET_VIDEO_CONTENT.append("vcc.lastModifiedDate,vcc.lastModifiedBy,vcc.videoLink,vcc.thumbnailUrl,vcc.mobileUrlHd, ");
		GET_VIDEO_CONTENT.append("vcc.mobileUrlSd1,vcc.mobileUrlSd2 FROM  acads.sessions s ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.video_content vcc ON s.id = vcc.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON vcc.facultyId = f.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE vcc.subject IN ("+commonSubjects+") ");
		GET_VIDEO_CONTENT.append(PROGRM_LIKE_SQL+" ORDER BY id DESC ");
		
		//Prepare SQL query to get count of records
		VIDEO_CONTENT_COUNT.append("SELECT count(*) FROM ( SELECT v.id FROM acads.sessions s ");
		VIDEO_CONTENT_COUNT.append("INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionid ");
		VIDEO_CONTENT_COUNT.append("INNER JOIN acads.video_content v ON s.id = v.sessionId ");
		VIDEO_CONTENT_COUNT.append("INNER JOIN acads.faculty f ON v.facultyId = f.facultyId WHERE ssm.subjectCodeId ");
		VIDEO_CONTENT_COUNT.append("IN ("+subjectCodeIds+") AND CONCAT(v.month,v.year) IN ("+academicCycles+") GROUP BY v.id ");
		
		VIDEO_CONTENT_COUNT.append("UNION  SELECT vcc.id FROM acads.sessions s ");
		VIDEO_CONTENT_COUNT.append("INNER JOIN acads.video_content vcc ON s.id = vcc.sessionId ");
		VIDEO_CONTENT_COUNT.append("INNER JOIN acads.faculty f ON vcc.facultyId = f.facultyId ");
		VIDEO_CONTENT_COUNT.append("WHERE vcc.subject IN ("+commonSubjects+") "+PROGRM_LIKE_SQL +" )as videoCount ");
		
		
		//Creating PaginationHelper<VideoContentBean> object
		pagingHelper = new PaginationHelper<VideoContentAcadsBean>();
		
		try {
			//Execute fetchPage method of PaginationHelper 
			page = pagingHelper.fetchPage(jdbcTemplate, VIDEO_CONTENT_COUNT.toString() , GET_VIDEO_CONTENT.toString(), 
									null, pageNo, pageSize, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			//returns empty Page of VideoContentBean
			page = new PageAcads<VideoContentAcadsBean>();
			  
		}		
		
		//return page having the Session Videos Details
		return page;
	}//getVideoContentBySubjectCodeId()
	

	
	/***
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param subjectCodeIds
	 * @param academicCycles 
	 * @param program
	 * @param commonSubjects
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageAcads<VideoContentAcadsBean> getSessionsVideoBySubjectCodeId(int pageNo, int pageSize, 
																String subjectCodeIds,
																String academicCycles, 
																String program, String commonSubjects ){
		StringBuilder GET_VIDEO_CONTENT = null, VIDEO_CONTENT_COUNT = null;
		PaginationHelper<VideoContentAcadsBean> pagingHelper =null;
		PageAcads<VideoContentAcadsBean> page = null;
		String PROGRM_LIKE_SQL = null;
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		VIDEO_CONTENT_COUNT = new StringBuilder();
		
		
		/*if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program))
			PROGRM_LIKE_SQL = "AND ((programList LIKE ('%"+program+"%')) OR (programList='All')) ";
		else	
			PROGRM_LIKE_SQL = "AND programList LIKE ('%"+program+"%') ";*/
		
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT.append("SELECT v.id,v.sessionId,v.sessionDate,v.subject,v.acadYear AS year,v.acadMonth AS month, ");
		GET_VIDEO_CONTENT.append("v.description,v.thumbnailUrl,v.track AS track,v.facultyId AS facultyId, ");
		GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName, v.fileName ");
		GET_VIDEO_CONTENT.append("FROM acads.quick_video_content v ");
		GET_VIDEO_CONTENT.append("WHERE v.subjectCodeId IN ("+subjectCodeIds+") ");
		GET_VIDEO_CONTENT.append("AND CONCAT(v.acadMonth, v.acadYear) IN ("+academicCycles+") ");
		
		GET_VIDEO_CONTENT.append("UNION SELECT qvc.id,qvc.sessionId,qvc.sessionDate,qvc.subject,qvc.acadYear AS year, ");
		GET_VIDEO_CONTENT.append("qvc.acadMonth AS month, qvc.description,qvc.thumbnailUrl,qvc.track AS track, ");
		GET_VIDEO_CONTENT.append("qvc.facultyId AS facultyId, CONCAT('Prof. ', qvc.firstName, ' ', qvc.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("qvc.fileName FROM acads.quick_video_content qvc ");
		GET_VIDEO_CONTENT.append("WHERE qvc.subject IN ("+commonSubjects+") ORDER BY id DESC ");
		
		//Prepare SQL query to get count of records
		VIDEO_CONTENT_COUNT.append("SELECT count(*) FROM ( SELECT v.id FROM acads.quick_video_content v ");
		VIDEO_CONTENT_COUNT.append("WHERE v.subjectCodeId IN ("+subjectCodeIds+") ");
		VIDEO_CONTENT_COUNT.append("AND CONCAT(v.acadMonth, v.acadYear) IN ("+academicCycles+") ");
		
		VIDEO_CONTENT_COUNT.append("UNION  SELECT qvc.id FROM acads.quick_video_content qvc ");
		VIDEO_CONTENT_COUNT.append("WHERE qvc.subject IN ("+commonSubjects+") )as videoCount ");
		 
		//Creating PaginationHelper<VideoContentBean> object
		pagingHelper = new PaginationHelper<VideoContentAcadsBean>();
		
		try {
			//Execute fetchPage method of PaginationHelper 
			page = pagingHelper.fetchPage(jdbcTemplate, VIDEO_CONTENT_COUNT.toString() , GET_VIDEO_CONTENT.toString(), 
									null, pageNo, pageSize, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			//returns empty Page of VideoContentBean
			page = new PageAcads<VideoContentAcadsBean>();
			  
		}		
		
		//return page having the Session Videos Details
		return page;
	}//getVideoContentBySubjectCodeId()
	

	//This method is used to get video content details from temporary table based on program sem subject Id's 
	@Transactional(readOnly = true)
	public PageAcads<VideoContentAcadsBean> getSessionVideos(int pageNo, int pageSize,  
													String programSemSubjectIds,
													String acadDateFormat,String commonSubjects ){
		StringBuilder GET_VIDEO_CONTENT = null, VIDEO_CONTENT_COUNT = null;
		PaginationHelper<VideoContentAcadsBean> pagingHelper =null;
		PageAcads<VideoContentAcadsBean> page = null;

		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		VIDEO_CONTENT_COUNT = new StringBuilder();

		//Preparing SQL Query
		GET_VIDEO_CONTENT.append("SELECT v.id,v.sessionId,v.sessionDate,v.subject,v.acadYear AS year,v.acadMonth AS month, ");
		GET_VIDEO_CONTENT.append("v.description,v.thumbnailUrl,v.track AS track,v.facultyId AS facultyId, ");

		GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName, v.fileName, v.program_sem_subject_id, ");
		GET_VIDEO_CONTENT.append("v.subjectCodeId FROM acads.quick_video_content v ");

		GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
		GET_VIDEO_CONTENT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");
		GET_VIDEO_CONTENT.append(" ORDER BY v.id DESC");
		
		//Prepare SQL query to get count of records
		VIDEO_CONTENT_COUNT.append("SELECT count(*) FROM acads.quick_video_content v ");
		VIDEO_CONTENT_COUNT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
		VIDEO_CONTENT_COUNT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");

		//Creating PaginationHelper<VideoContentBean> object
		pagingHelper = new PaginationHelper<VideoContentAcadsBean>();

		try {
			//Execute fetchPage method of PaginationHelper 
			page = pagingHelper.fetchPage(jdbcTemplate, VIDEO_CONTENT_COUNT.toString() , GET_VIDEO_CONTENT.toString(), 
					null, pageNo, pageSize, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			//returns empty Page of VideoContentBean
			page = new PageAcads<VideoContentAcadsBean>();
			  
		}		

		//return page having the Session Videos Details
		return page;
	}//getVideoContentBySubjectCodeId()
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoForSearchByFilterNew(String searchItem, String faculty,
			ArrayList<String> programSemSubjectIds, String cycle, String batch, StudentAcadsBean student, List<String> commonSubjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		String sql = "";
		String subjectCommaSeparated = "''";
		
		String sqlForCommonSub = "";
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())
				&& !"MBA - WX".equalsIgnoreCase(student.getProgram())){
			sqlForCommonSub = " AND ((programList like ('%"+student.getProgram()+"%')) OR (programList='All')) ";			
		}else {
			sqlForCommonSub = " AND programList LIKE ('%"+student.getProgram()+"%') ";
		}
		
		for (int i = 0; i < programSemSubjectIds.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String commonSubjectCommaSeparated = "''";
		for (int i = 0; i < commonSubjects.size(); i++) {
			if (i == 0) {
				commonSubjectCommaSeparated = "'" + commonSubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				commonSubjectCommaSeparated = commonSubjectCommaSeparated + ", '"
						+ commonSubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		sql =   " SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
				"    v.*, s.meetingKey " + 
				" FROM acads.video_content v " + 
				"		 INNER JOIN " +
				"	acads.sessions s ON s.id = v.sessionId " + 
				"        INNER JOIN " + 
				"	acads.session_subject_mapping ssm on s.id = ssm.sessionID " + 
				"        INNER JOIN " +
				"	acads.faculty f ON f.facultyId = v.facultyId " + 
				" WHERE program_sem_subject_id IN (" +subjectCommaSeparated+ ")";
		
		//If Faculty
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		
		//If Cycle
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//If Batch
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}
		
		//For Common Session
		sql +=  " UNION  " + 
				" SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
				"    v.*, s.meetingKey " + 
				" FROM acads.video_content v " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON s.id = v.sessionId " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON f.facultyId = v.facultyId " + 
				" WHERE v.subject in ("+commonSubjectCommaSeparated+") " + 
				sqlForCommonSub ;
		
		//If Faculty
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		
		//If Cycle
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//If Batch
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}
		
		sql = sql + " GROUP BY v.id ";
		sql = sql + " ORDER By id desc ";
		
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return VideoContentsList;
	}
	
	/***
	 * 
	 * @param searchItem
	 * @param faculty Contains Faculty Id for which student wants to filter session recording
	 * @param subjectCodeIds Contains subjectCodeIds for which student wants to filter session recording
	 * @param cycle Selected Cycle 
	 * @param batch
	 * @param student
	 * @param commonSubjects Common subjects of a student in a single string
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getSessionVideosForFilter(String searchItem, String faculty,
															String subjectCodeIds, 
															String cycle, String batch, String program, 
															String commonSubjects){
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> videoContentList = null;
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
				
		//Prepare SQL query
		GET_VIDEO_CONTENT.append("SELECT vc.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("s.track AS track FROM acads.video_content vc ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions s ON s.id = vc.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = vc.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE ssm.subjectCodeId IN ("+subjectCodeIds+") ");
		
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty)) 
			GET_VIDEO_CONTENT.append(" and vc.facultyId='" + faculty + "' ");

		//Add Cycle to SQL query if Cycle info is not All
		if (!"All".equals(cycle))
			GET_VIDEO_CONTENT.append(" and concat(vc.month,vc.year)='" + cycle + "' ");

		//Add Batch to SQL query if Batch info is not All
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
		
		
		GET_VIDEO_CONTENT.append(" UNION ");
		GET_VIDEO_CONTENT.append("SELECT  v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("s.track AS track FROM acads.video_content v ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubjects+") ");
		
		if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program))
			GET_VIDEO_CONTENT.append("AND ((programList LIKE ('%"+program+"%')) OR (programList='All')) ");
		else	
			GET_VIDEO_CONTENT.append("AND programList LIKE ('%"+program+"%') ");
		
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty))
			GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

		//Add Cycle to SQL query if Cycle info is not All
		if (!"All".equals(cycle))
			GET_VIDEO_CONTENT.append(" and concat(v.month,v.year)='" + cycle + "' ");

		//Add Batch to SQL query if Batch info is not All 
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
		
		GET_VIDEO_CONTENT.append("GROUP BY v.id ORDER BY id DESC ");

		//Execute JdbcTemplate query method
		videoContentList = (List<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
													new Object[]{},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		
		//return video Content details as a list
		return videoContentList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getSessionVideosByFilter(String searchItem, String faculty,
															String programSemSubjectIds, 
															String acadDateFormat, String batch){
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> videoContentList = null;

		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		
		//Create empty array list of video content bean
		videoContentList = new ArrayList<VideoContentAcadsBean>();
		
		//Prepare SQL query
		GET_VIDEO_CONTENT.append("SELECT v.id,v.sessionId,v.sessionDate,v.subject,v.acadYear AS year,v.acadMonth AS month, ");
		GET_VIDEO_CONTENT.append("v.description,v.thumbnailUrl,v.track AS track,v.facultyId AS facultyId, ");
		GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName,v.firstName, v.lastName, v.fileName, v.program_sem_subject_id AS programSemSubjectId, ");
		GET_VIDEO_CONTENT.append("v.subjectCodeId FROM acads.quick_video_content v ");
		GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
		GET_VIDEO_CONTENT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty)) 
			GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

		//Add Batch to SQL query if Batch info is not All
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and v.track='" + batch + "' ");
		
		try {
			//Execute JdbcTemplate query method to get videos by program sem subject id
			videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
					new Object[]{},new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
			
		}catch (Exception e) {
			  
		}
		
		//return video Content details as a list
		return videoContentList;
	}
	
	//Get then session recording for a common subject with filter
	@Transactional(readOnly = true)
	private List<VideoContentAcadsBean> getCommonSubjectSessionRecording(String commonSubjects,String acadDateFormat, 
															String batch, String faculty  ){
		StringBuilder GET_VIDEO_COMMON_CONTENT=null;
		List<VideoContentAcadsBean> commonSubVdoContentList = null;

		//Creating StringBuilder object
		GET_VIDEO_COMMON_CONTENT = new StringBuilder();
		
		GET_VIDEO_COMMON_CONTENT.append("SELECT vc.id,vc.sessionId,vc.sessionDate,vc.subject,vc.acadYear AS year, ");
		GET_VIDEO_COMMON_CONTENT.append("vc.acadMonth AS month,vc.thumbnailUrl,vc.track AS track,vc.facultyId AS facultyId, ");
		GET_VIDEO_COMMON_CONTENT.append("vc.description,CONCAT('Prof. ', vc.firstName, ' ', vc.lastName) AS facultyName,  ");
		GET_VIDEO_COMMON_CONTENT.append("vc.fileName FROM acads.quick_video_content vc ");
		GET_VIDEO_COMMON_CONTENT.append("WHERE vc.subject IN ("+commonSubjects+") ");
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty))
			GET_VIDEO_COMMON_CONTENT.append(" and vc.facultyId='" + faculty + "' ");
			
		//Add academic date format to SQL query if acadDateFormat info is not All
		if(!"All".equalsIgnoreCase(acadDateFormat))
			GET_VIDEO_COMMON_CONTENT.append("AND vc.acadDateFormat = '"+acadDateFormat+"' ");
		
		//Add Batch to SQL query if Batch info is not All 
		if (!"All".equals(batch))
			GET_VIDEO_COMMON_CONTENT.append(" and vc.track='" + batch + "' ");
		
		GET_VIDEO_COMMON_CONTENT.append(" GROUP BY vc.id ORDER BY vc.id DESC ");
		
		//Execute JdbcTemplate query method to get common subject videos 
		commonSubVdoContentList = (List<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_COMMON_CONTENT.toString(),
										new Object[]{},new BeanPropertyRowMapper(VideoContentAcadsBean.class));

		//return common video Content details as a list
		return commonSubVdoContentList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getCommonSubjectVideos(String commonSubjects,String faculty,
												String cycle,String batch,String program){
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> commonSubVdoContentList = null;

		//Creating StringBuilder object
		GET_VIDEO_CONTENT= new StringBuilder();
		
		//Creating empty array list of VideoContentBean
		commonSubVdoContentList = new ArrayList<VideoContentAcadsBean>();
		
		GET_VIDEO_CONTENT.append("SELECT  v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName,f.firstName,f.lastName, ");
		GET_VIDEO_CONTENT.append("s.track AS track FROM acads.video_content v ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubjects+") ");
		
		if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program)){
			GET_VIDEO_CONTENT.append(" AND ((programList like ('%"+program+"%')) OR (programList='All')) ");			
		}else {
			GET_VIDEO_CONTENT.append(" AND programList LIKE ('%"+program+"%') ");
		}
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty))
			GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

		//Add Cycle to SQL query if Cycle info is not All
		if (!"All".equals(cycle))
			GET_VIDEO_CONTENT.append(" and concat(v.month,v.year)='" + cycle + "' ");

		//Add Batch to SQL query if Batch info is not All 
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
		
		try {
		//Execute JdbcTemplate query method
		commonSubVdoContentList = (List<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
													new Object[]{},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		}catch (Exception e) {
			  
		}
		
		//return video Content details as a list
		return commonSubVdoContentList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getOldCycleVideoForSearchByFilter(String searchItem, String faculty,
			ArrayList<String> programSemSubjectIds, String cycle, String batch, StudentAcadsBean student, List<String> commonSubjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;
		String sql = "";
		String subjectCommaSeparated = "''";
		
		String sqlForCommonSub = "";
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())
				&& !"MBA - WX".equalsIgnoreCase(student.getProgram())){
			sqlForCommonSub = " AND ((programList like ('%"+student.getProgram()+"%')) OR (programList='All')) ";			
		}else {
			sqlForCommonSub = " AND programList LIKE ('%"+student.getProgram()+"%') ";
		}
		
		for (int i = 0; i < programSemSubjectIds.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String commonSubjectCommaSeparated = "''";
		for (int i = 0; i < commonSubjects.size(); i++) {
			if (i == 0) {
				commonSubjectCommaSeparated = "'" + commonSubjects.get(i).replaceAll("'", "''") + "'";
			} else {
				commonSubjectCommaSeparated = commonSubjectCommaSeparated + ", '"
						+ commonSubjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		sql =   " SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
				"    v.*, s.meetingKey " + 
				" FROM acads.video_content v " + 
				"		 INNER JOIN " +
				"	acads.sessions_history s ON s.id = v.sessionId " + 
				"        INNER JOIN " + 
				"	acads.session_subject_mapping_history ssm on s.id = ssm.sessionID " + 
				"        INNER JOIN " +
				"	acads.faculty f ON f.facultyId = v.facultyId " + 
				" WHERE program_sem_subject_id IN (" +subjectCommaSeparated+ ")";
		
		//If Faculty
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		
		//If Cycle
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//If Batch
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}
		
		//For Common Session
		sql +=  " UNION  " + 
				" SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
				"    v.*, s.meetingKey " + 
				" FROM acads.video_content v " + 
				"        INNER JOIN " + 
				"    acads.sessions_history s ON s.id = v.sessionId " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON f.facultyId = v.facultyId " + 
				" WHERE v.subject in ("+commonSubjectCommaSeparated+") " + 
				sqlForCommonSub ;
		
		//If Faculty
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		
		//If Cycle
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//If Batch
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}
		
		sql = sql + " GROUP BY v.id ";
		sql = sql + " ORDER By id desc ";
		
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return VideoContentsList;
	}
	
	/***
	 * 
	 * @param searchItem
	 * @param faculty
	 * @param subjectCodeIdList
	 * @param cycle
	 * @param batch
	 * @param student
	 * @param commonSubjectList
	 * @return
	 */
	@Transactional(readOnly = true)
	@Deprecated
	public List<VideoContentAcadsBean> getOldCycleSessionVideosForFilter(String searchItem, String faculty,
																	String subjectCodeIds, 
																	String cycle,String batch, String program, 
																	String commonSubjects){
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> videoContentList = null;
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		
		//Prepare SQL Query
		GET_VIDEO_CONTENT.append("SELECT vc.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("s.track AS track FROM acads.video_content vc ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions_history s ON s.id = vc.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.session_subject_mapping_history ssm ON ssm.sessionId = s.id ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = vc.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE ssm.subjectCodeId IN ("+subjectCodeIds+") ");
		GET_VIDEO_CONTENT.append(" AND concat(vc.month,vc.year)='" + cycle + "' ");
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty)) 
			GET_VIDEO_CONTENT.append(" and vc.facultyId='" + faculty + "' ");

		//Add Batch to SQL query if Batch info is not All
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
		
		
		GET_VIDEO_CONTENT.append(" UNION ");
		GET_VIDEO_CONTENT.append("SELECT  v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ");
		GET_VIDEO_CONTENT.append("s.track AS track FROM acads.video_content v ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions_history s ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ");
		GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubjects+") ");
		GET_VIDEO_CONTENT.append(" AND concat(v.month,v.year)='" + cycle + "' ");
		
		if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
				&& !"MBA - WX".equalsIgnoreCase(program))
			GET_VIDEO_CONTENT.append("AND ((programList LIKE ('%"+program+"%')) OR (programList='All')) ");
		else	
			GET_VIDEO_CONTENT.append("AND programList LIKE ('%"+program+"%') ");
		
		
		//Add faculty to SQL query if faculty info is not All
		if (!"All".equals(faculty))
			GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");
			
		//Add Batch to SQL query if Batch info is not All 
		if (!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
		
		GET_VIDEO_CONTENT.append("GROUP BY v.id ORDER BY id DESC ");
		
		
		//Execute JdbcTemplate query method
		videoContentList = (List<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
															new Object[]{},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
				
		//return video Content details as a list
		return videoContentList;
	}
	
	@Transactional(readOnly = true)
	public String getSubjectByProgramSemSubjectId(String programSemSubjectId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql=	" SELECT subject FROM exam.program_sem_subject WHERE id = ? ";
		String subject = "";
		try {	
			subject = jdbcTemplate.queryForObject(sql, new Object[] {programSemSubjectId}, String.class);
		}catch(Exception e) {
			  
		}
		
		return subject;
	}
	
	
	public void updateVideoContentAudioDetails(VideoContentAcadsBean videoContentBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.video_content set oldaudioUrl=?,audioFile=?,audioUrl_status=?,audioUrl_retry=? where id=?";
			jdbcTemplate.update(sql,new Object[] {videoContentBean.getOldaudioUrl(),videoContentBean.getAudioFile(),videoContentBean.getAudioUrl_status(),videoContentBean.getAudioUrl_retry(),videoContentBean.getId()});
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}	
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getListOfVideoContentWithZoomAudioLink(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.video_content where (audioFile IS NOT NULL and audioFile <> '') and (audioUrl_status <> 'success' or audioUrl_status IS NULL) and audioUrl_retry <= 6 and createdDate > '2019-01-01 00:00:00' order by id desc limit 100;";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public VideoContentAcadsBean getVideoContentWithZoomAudioLinkById(int id){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.video_content where id=? limit 10;";
			return jdbcTemplate.queryForObject(sql,new Object[] {id},new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	/***
	 * 
	 * @param subjectCodeId
	 * @return subjectName based on subjectCodeId
	 */
	@Transactional(readOnly = true)
	public String getSubjectBySubjectCodeId(String subjectCodeId) {
		String subject = "";
		String GET_SUBJECT = null;
		
		//Prepare SQL query
		GET_SUBJECT = "SELECT subjectname FROM exam.mdm_subjectcode where id = ? ";
		
		try {
		//Execute JdbcTemplate query
		subject = jdbcTemplate.queryForObject(GET_SUBJECT, new Object[] {subjectCodeId}, String.class);
		}
		catch (Exception e) {
			  
		}
		//return Subject
		return subject;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getRelatedVideoContentNewForMobile(String sapid, int programSemSubjectId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		String sql =" SELECT vc.*, s.meetingKey, s.track, cb.bookmarked, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName FROM " + 
					"    acads.video_content vc " + 
					"        INNER JOIN " + 
					"    acads.sessions s ON vc.sessionId = s.id " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"	 acads.faculty f on vc.facultyId = f.facultyId " +
					"        LEFT JOIN " + 
					"    bookmarks.content_bookmarks cb ON vc.id = cb.content_id " + 
					"        AND cb.sapid = ? " + 
					" WHERE " + 
					"    program_sem_subject_id = ? "+
					" ORDER BY vc.id DESC ";
		try {
			videoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {sapid, programSemSubjectId},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		return videoContentsList;
	}
	

	/***
	 * @param studentRegData StudentBean having the student registration data
	 * @param programSemSubjectId
	 * @param acadSessionLiveOrder 
	 * @param reg_order
	 * @param current_order
	 * @return ArrayList<VideoContentBean> */
	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForCurrentCycle(StudentAcadsBean studentRegData, 
													int programSemSubjectId,double acadSessionLiveOrder,
													double reg_order,double current_order) {
		
		ArrayList<VideoContentAcadsBean> videoContentsList = null; 
		StringBuilder GET_VIDEO_CONTENT_LIST = null;
		
		//Creating StringBuilder Object
		GET_VIDEO_CONTENT_LIST = new StringBuilder();
		
		//Creating empty list
		videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT_LIST.append("SELECT vc.*,s.meetingKey,s.track,cb.bookmarked,CONCAT('Prof. ',f.firstName,' ',f.lastName) ");
		GET_VIDEO_CONTENT_LIST.append("AS facultyName FROM acads.video_content vc ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.sessions s ON vc.sessionId = s.id ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN exam.examorder eo on s.year = eo.year AND s.month = eo.acadMonth ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.faculty f on vc.facultyId = f.facultyId ");
		GET_VIDEO_CONTENT_LIST.append("LEFT JOIN bookmarks.content_bookmarks cb ON vc.id = cb.content_id AND cb.sapid = ? ");
		GET_VIDEO_CONTENT_LIST.append("WHERE program_sem_subject_id = ? ");
		
		//When two academic cycle session content is live i.e current and coming cycle then 
		//If acadContentLive year and month order is equal to student registration year and month order
		//then show the video content to students based on student registration year and month
		//Else show based on CURRENT_ACAD_MONTH and CURRENT_ACAD_YEAR
		if(acadSessionLiveOrder == reg_order)
			GET_VIDEO_CONTENT_LIST.append("and vc.month = '"+studentRegData.getMonth()+"' and vc.year = "+ studentRegData.getYear());
		else
			GET_VIDEO_CONTENT_LIST.append("and vc.month = '"+CURRENT_ACAD_MONTH+"' and vc.year = "+ CURRENT_ACAD_YEAR);
			
		GET_VIDEO_CONTENT_LIST.append(" ORDER BY vc.id DESC  ");
		
		try {
			//Execute JdbcTemplate query method
			videoContentsList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_CONTENT_LIST.toString(), 
														new Object[] { studentRegData.getSapid(), programSemSubjectId},
														new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		//return the current cycle videonContentList
		return videoContentsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentForLastCycle(StudentAcadsBean studentRegData, 
															int programSemSubjectId, double acadSessionLiveOrder,
															double reg_order, double current_order) {
		
		ArrayList<VideoContentAcadsBean> videoContentsList = null; 
		StringBuilder GET_VIDEO_CONTENT_LIST = null;
		
		//Creating StringBuilder Object
		GET_VIDEO_CONTENT_LIST = new StringBuilder();
		
		//Creating empty list
		videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT_LIST.append("SELECT vc.*,s.meetingKey,s.track,cb.bookmarked,CONCAT('Prof. ',f.firstName,' ',f.lastName) ");
		GET_VIDEO_CONTENT_LIST.append("AS facultyName FROM acads.video_content vc ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.sessions s ON vc.sessionId = s.id ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN exam.examorder eo on s.year = eo.year AND s.month = eo.acadMonth ");
		GET_VIDEO_CONTENT_LIST.append("INNER JOIN acads.faculty f on vc.facultyId = f.facultyId ");
		GET_VIDEO_CONTENT_LIST.append("LEFT JOIN bookmarks.content_bookmarks cb ON vc.id = cb.content_id AND cb.sapid = ? ");
		GET_VIDEO_CONTENT_LIST.append("WHERE program_sem_subject_id = ? ");
		
		if(acadSessionLiveOrder == reg_order)
			GET_VIDEO_CONTENT_LIST.append("AND eo.order = "+(reg_order - 1));
		else
			GET_VIDEO_CONTENT_LIST.append("AND eo.order = "+(current_order - 1));
			
		GET_VIDEO_CONTENT_LIST.append(" ORDER BY vc.id DESC  ");
		
		
		try {
			//Execute JdbcTemplate query method
			videoContentsList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(GET_VIDEO_CONTENT_LIST.toString(),
														new Object[] { studentRegData.getSapid(), programSemSubjectId},
														new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		//return the last cycle videonContentList
		return videoContentsList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoForSearchByFilterForCommonNew(String searchItem, String faculty,
											List<String> programSemSubjectIds, String cycle, String batch, StudentAcadsBean student) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = null;

		String sql = "";
		String subjectCommaSeparated = "''";
		
		for (int i = 0; i < programSemSubjectIds.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + programSemSubjectIds.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String sqlForCommonSub = "";
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())
				&& !"MBA - WX".equalsIgnoreCase(student.getProgram())){
			sqlForCommonSub = " AND ((programList like ('%"+student.getProgram()+"%')) OR (programList='All')) ";			
		}else {
			sqlForCommonSub = " AND programList LIKE ('%"+student.getProgram()+"%') ";
		}
		
		String sqlForOrientation = "";
		if (programSemSubjectIds.contains("Orientation")) {
			sqlForOrientation = " INNER JOIN exam.registration r ON v.year = r.year AND v.month = r.month " +
								" AND r.sapid = "+student.getSapid()+ " AND r.sem = 1 ";
		}
		
		sql =   " SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, v.*, s.meetingKey,f.lastName,f.firstName " + 
				" FROM acads.video_content v " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON s.id = v.sessionId " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON f.facultyId = v.facultyId " + 
				sqlForOrientation +
				" WHERE " + 
				"    v.subject IN ("+subjectCommaSeparated+") " + 
				sqlForCommonSub ;
		
		//If Faculty
		if (!"All".equals(faculty)) {
			sql = sql + " and v.facultyId='" + faculty + "'";
		}
		
		//If Cycle
		if (!"All".equals(cycle)) {
			sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
		}

		//If Batch
		if(!"All".equals(batch)) {
			sql=sql+" and s.track='"+batch+"'";
		}
		
		sql = sql + " GROUP BY v.id ";
		sql = sql + " ORDER By v.id desc ";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return VideoContentsList;
	}	
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getCommonSessionSubjectVideos(String searchItem, String faculty,List<String> programSemSubjectIds,
															String acadCycleDateFormat, String batch, StudentAcadsBean student) {
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> videoContentList = null;

		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();

		//Frame common subject into a single string
		String commonSubject = ContentUtil.frameINClauseString(programSemSubjectIds);
		
		//Prepare SQL query
		GET_VIDEO_CONTENT.append("SELECT v.id,v.sessionId,v.sessionDate,v.subject,v.acadYear AS year,v.acadMonth AS month, ");
		GET_VIDEO_CONTENT.append("v.description,v.thumbnailUrl,v.track AS track,v.facultyId AS facultyId, ");
		GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName, v.fileName ");
		GET_VIDEO_CONTENT.append("FROM acads.quick_video_content v ");
		
		if (programSemSubjectIds.contains("Orientation"))
			GET_VIDEO_CONTENT.append(" INNER JOIN exam.registration r ON v.acadYear = r.year AND v.acadMonth = r.month " +
					" AND r.sapid = "+student.getSapid()+ " AND r.sem = 1 ");
		
		GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubject+") ");


		//append faculty to SQL If Faculty is not 'All'
		if (!"All".equals(faculty))
			GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

		//append acadCycleDateFormat to SQL If acadCycleDateFormat is not 'All'
		if (!"All".equals(acadCycleDateFormat))
			GET_VIDEO_CONTENT.append(" AND v.acadDateFormat = '" + acadCycleDateFormat+"' ");

		//append batch to SQL If Batch is not 'All'
		if(!"All".equals(batch))
			GET_VIDEO_CONTENT.append(" and s.track='"+batch+"' ");

		GET_VIDEO_CONTENT.append(" GROUP BY v.id ORDER BY v.id desc ");
		
		try {
			//Execute jdbcTemplate query method
			videoContentList =jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),new Object[] {},
													new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		//return common session videos
		return videoContentList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentTranscriptNotFound(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from ( SELECT v_c.*, s.meetingKey as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.facultyId = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL and v_c.videoTranscriptUrl_flag IS NULL) UNION ALL SELECT v_c.*, s.altMeetingKey as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL and v_c.videoTranscriptUrl_flag IS NULL) UNION ALL SELECT v_c.*, s.altMeetingKey2 as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId2 = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL and v_c.videoTranscriptUrl_flag IS NULL) UNION ALL SELECT v_c.*, s.altMeetingKey3 as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId3 = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL and v_c.videoTranscriptUrl_flag IS NULL)) v limit 100;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentWithOutVimeoUploadedTranscript(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from acads.video_content where sessionDate > '2020-12-01' and videoTranscriptUrl IS NOT NULL and videoTranscriptUrl_flag <> 'uploaded' and videoTranscriptUrl_retry <=5 order by sessionDate desc limit 50;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentTranscriptNotFoundData(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT v_c.*, s.meetingKey as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.facultyId = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL OR v_c.videoTranscriptUrl like '%https://studentzone-ngasce.nmims.edu%') and v_c.videoTranscriptUrl_retry <=5 order by s.`date` desc limit 300;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentTranscriptNotFoundData2(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT v_c.*, s.altMeetingKey as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL OR v_c.videoTranscriptUrl like '%https://studentzone-ngasce.nmims.edu%') and v_c.videoTranscriptUrl_retry <=5 order by s.`date` desc limit 50;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentTranscriptNotFoundData3(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT v_c.*, s.altMeetingKey2 as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId2 = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL OR v_c.videoTranscriptUrl like '%https://studentzone-ngasce.nmims.edu%') and v_c.videoTranscriptUrl_retry <=5 order by s.`date` desc limit 50;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentTranscriptNotFoundData4(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT v_c.*, s.altMeetingKey3 as meetingKey FROM acads.video_content v_c INNER JOIN acads.sessions s ON s.id = v_c.sessionId AND s.altFacultyId3 = v_c.facultyId WHERE s.date > '2020-12-01' and (v_c.videoTranscriptUrl IS NULL OR v_c.videoTranscriptUrl like '%https://studentzone-ngasce.nmims.edu%') and v_c.videoTranscriptUrl_retry <=5 order by s.`date` desc limit 50;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} 
		catch (Exception e) {
			// TODO: handle exception
			  
			return null;
		}
	}
	
	public void updateVideoTranscriptionUrl(String id,String transcriptUrl) {
		try {
			String sql = "UPDATE `acads`.`video_content` SET `videoTranscriptUrl`=?,`videoTranscriptPortal` = ?,`videoTranscriptUrl_flag`='success' WHERE `id`=?;";
			jdbcTemplate.update(sql,new Object[] {transcriptUrl,transcriptUrl,id});
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}
	}
	
	public void updateVideoTranscriptionUrlFlag(String id,String status) {
		try {
			String sql = "UPDATE `acads`.`video_content` SET `videoTranscriptUrl_retry` = videoTranscriptUrl_retry+1, `videoTranscriptUrl_flag`=? WHERE `id`=?;";
			jdbcTemplate.update(sql,new Object[] {status,id});
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}
	}
	
	/***
	 * @param	contentBean	Having the subjectCodeId, year and month
	 * @return	List<VideoContentBean> return the list of VideoContentBean having 
	 * 								   the filtered(based on above parameter) data   
	 */
	@Transactional(readOnly = true)
	public  List<VideoContentAcadsBean> getVideoContentForSubject(ContentAcadsBean contentBean)
	{
		StringBuilder GET_VIDEOS_BY_SUBJECTCODEID = null;
		List<VideoContentAcadsBean> videoContentList = null;
		
		//Creating empty list
		videoContentList = new ArrayList<VideoContentAcadsBean>();
		
		//Creating StringBuilder object
		GET_VIDEOS_BY_SUBJECTCODEID = new StringBuilder();
		
		//Preparing SQL query
		GET_VIDEOS_BY_SUBJECTCODEID.append("SELECT vc.*,s.meetingKey,s.track,ssm.subjectCodeId,s.startTime FROM acads.video_content vc ")
		.append("INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = vc.sessionId ")
		.append("INNER JOIN acads.sessions s ON s.id = vc.sessionId ")
		.append("WHERE ssm.subjectCodeId = ? and vc.year = ? and vc.month = ? GROUP BY vc.id order by s.startTime DESC");
		
		try {
			//Execute JdbcTemplate query method
			videoContentList = jdbcTemplate.query(GET_VIDEOS_BY_SUBJECTCODEID.toString(),new Object[] {
										contentBean.getSubjectCodeId(),contentBean.getYear(),contentBean.getMonth()}, 
										new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
			
		} catch (Exception e) {
			  
		}
		
		//return videoContentList
		return videoContentList;
	}

	
	/**
	 * @param	year	for this year recording we have to move. 
	 * @param	month	for this month recording we have to move.
	 * @return	List<VideoContentBean>	return list of sessions recording details. 
	 * @throws	java.lang.Exception	If any exception occurs during the insertion.
	 * */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentDetailsByYearMonth(String year, String month) throws Exception {
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> listVideoContents = null;
		List<VideoContentAcadsBean> listHistoryVideoContent = null;
				
		//Injecting DataSource object to JdbcTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT.append("SELECT ssm.subjectCodeId,v.fileName,s.track AS track,f.firstName,f.lastName,v.description,v.subject,v.sessionDate, ");
		GET_VIDEO_CONTENT.append("v.facultyId,v.thumbnailUrl,v.id,ssm.program_sem_subject_id,v.sessionId,v.createdDate, ");
		GET_VIDEO_CONTENT.append("v.audioFile,v.addedOn,v.videoLink,v.mobileUrlHd,v.mobileUrlSd1,v.mobileUrlSd2, ");
		GET_VIDEO_CONTENT.append("v.lastModifiedDate, v.year, v.month FROM acads.video_content v INNER JOIN acads.faculty f ON f.facultyId=v.facultyId ");
		GET_VIDEO_CONTENT.append("INNER JOIN  acads.sessions s ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN  acads.session_subject_mapping ssm on s.id = ssm.sessionId WHERE v.sessionId in ");
		GET_VIDEO_CONTENT.append("(SELECT id FROM acads.sessions where year is not null AND month is not null )  AND (v.sessionPlanModuleId IS NULL OR v.sessionPlanModuleId = 0)");
	
		//Creating empty array list
		listVideoContents = new ArrayList<VideoContentAcadsBean>();
		
		//Execute JdbcTemplate query method
		listVideoContents = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(), new Object[] {}, 
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));

		//Fetch old cycle session recording data 
		listHistoryVideoContent = this.getVideoContentHistoryByYearMonth(year,  month);
		
		//Merge current and old cycle data 
 		listVideoContents.addAll(listHistoryVideoContent);
		
		//return list of sessions recording details
		return listVideoContents;
	}//getVideoContentDetailsByYearMonth()
	
	/**
	 * @param	year	for this year recording we have to move. 
	 * @param	month	for this month recording we have to move.
	 * @return	List<VideoContentBean>	return list of sessions recording details. 
	 * @throws	java.lang.Exception	If any exception occurs during the insertion.
	 * */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideoContentHistoryByYearMonth(String year, String month) throws Exception {
		StringBuilder GET_VIDEO_CONTENT = null;
		List<VideoContentAcadsBean> listVideoContents = null;
		
		//Creating StringBuilder object
		GET_VIDEO_CONTENT = new StringBuilder();
		
		//Preparing SQL Query
		GET_VIDEO_CONTENT.append("SELECT ssm.subjectCodeId,v.fileName,s.track AS track,f.firstName,f.lastName,v.description,v.subject,v.sessionDate, ");
		GET_VIDEO_CONTENT.append("v.facultyId,v.thumbnailUrl,v.id,ssm.program_sem_subject_id,v.sessionId,v.createdDate, ");
		GET_VIDEO_CONTENT.append("v.audioFile,v.addedOn,v.videoLink,v.mobileUrlHd,v.mobileUrlSd1,v.mobileUrlSd2, ");
		GET_VIDEO_CONTENT.append("v.lastModifiedDate, v.year, v.month FROM acads.video_content v INNER JOIN acads.faculty f ON f.facultyId=v.facultyId ");
		GET_VIDEO_CONTENT.append("INNER JOIN  acads.sessions_history s ON s.id = v.sessionId ");
		GET_VIDEO_CONTENT.append("INNER JOIN  acads.session_subject_mapping_history ssm on s.id = ssm.sessionId WHERE v.sessionId in ");
		GET_VIDEO_CONTENT.append("(SELECT id FROM acads.sessions_history where year is not null AND month is not null )  AND (v.sessionPlanModuleId IS NULL OR v.sessionPlanModuleId = 0)");
	
		//Creating empty array list
		listVideoContents = new ArrayList<VideoContentAcadsBean>();
		
		//Execute JdbcTemplate query method
		listVideoContents = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(), new Object[] {}, 
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));

		//return list of sessions recording details
		return listVideoContents;
	}//getVideoContentHistoryByYearMonth()
	
	
	/**
	 * @param listVideoContent The List of VideoContentBean 
	 * @return integer 	return the affected rows count in database table
	 * @throws java.lang.Exception If any exception raises
	 */
	public int transferData(String year, String month) throws Exception {
		StringBuilder INSERT_QUICK_VIDEO_CONTENT = null;
		
		//fetch the recorded sessions details to insert into de-normalized table based on the year and month
		final List<VideoContentAcadsBean> listVideoContent = this.getVideoContentDetailsByYearMonth(year,  month);
		
		//Date format to insert into database
		final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// Creating StringBuilder object
		INSERT_QUICK_VIDEO_CONTENT = new StringBuilder();
			
		//Preparing SQL Query
		INSERT_QUICK_VIDEO_CONTENT.append("INSERT INTO acads.quick_video_content (id, sessionId, subject, sessionDate, ");
		INSERT_QUICK_VIDEO_CONTENT.append("description, thumbnailUrl, program_sem_subject_id, track, firstName, lastName, ");
		INSERT_QUICK_VIDEO_CONTENT.append("facultyId, acadYear, acadMonth,  createdDate, lastModifiedDate, fileName, subjectCodeId, acadDateFormat, ");
		INSERT_QUICK_VIDEO_CONTENT.append("audioFile, addedOn, videoLink, mobileUrlHd, mobileUrlSd1, mobileUrlSd2 ) ");
		INSERT_QUICK_VIDEO_CONTENT.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,DATE_FORMAT(STR_TO_DATE(CONCAT(? ,? , '01'), '%Y %M %d'), '%Y-%m-%d'), ");
		INSERT_QUICK_VIDEO_CONTENT.append(" ?,?,?,?,?,? ) ");
		
		//Insert with batch update
		int[] batchInsertQuickVideoContent = jdbcTemplate.batchUpdate(INSERT_QUICK_VIDEO_CONTENT.toString(),
				new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				VideoContentAcadsBean quickVdoCnt = listVideoContent.get(i);
				ps.setLong(1, quickVdoCnt.getId());
				ps.setInt(2, quickVdoCnt.getSessionId());
				ps.setString(3, quickVdoCnt.getSubject());
				ps.setString(4, quickVdoCnt.getSessionDate());
				ps.setString(5, quickVdoCnt.getDescription());
				ps.setString(6, quickVdoCnt.getThumbnailUrl());
				ps.setString(7, quickVdoCnt.getProgramSemSubjectId());
				ps.setString(8, quickVdoCnt.getTrack());
				ps.setString(9, quickVdoCnt.getFirstName());
				ps.setString(10, quickVdoCnt.getLastName());
				ps.setString(11, quickVdoCnt.getFacultyId());
				ps.setString(12, quickVdoCnt.getYear());
				ps.setString(13, quickVdoCnt.getMonth());
				//Converting java.util.Date to string object and set values to prepared statement.
				ps.setString(14, sdf.format(quickVdoCnt.getCreatedDate()));
				ps.setString(15, sdf.format(quickVdoCnt.getLastModifiedDate()));
				ps.setString(16, quickVdoCnt.getFileName());
				ps.setInt(17, quickVdoCnt.getSubjectCodeId());
				ps.setString(18, quickVdoCnt.getYear());
				ps.setString(19, quickVdoCnt.getMonth());
				
				ps.setString(20, quickVdoCnt.getAudioFile());
				ps.setString(21, quickVdoCnt.getAddedOn());
				ps.setString(22, quickVdoCnt.getVideoLink());
				ps.setString(23, quickVdoCnt.getMobileUrlHd());
				ps.setString(24, quickVdoCnt.getMobileUrlSd1());
				ps.setString(25, quickVdoCnt.getMobileUrlSd2());
			}

			@Override
			public int getBatchSize() {
				return listVideoContent.size();
			}

		});
		
		//return total rows affected count
		return batchInsertQuickVideoContent.length;
	}// transferData()
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getRelatedVideoContentUsingSubjectCodeForMobile(String subjectCode) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		String sql =" SELECT vc.*, s.meetingKey, s.track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName FROM " + 
					"    acads.video_content vc " + 
					"        INNER JOIN " + 
					"    acads.sessions s ON vc.sessionId = s.id " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"	 acads.faculty f on vc.facultyId = f.facultyId " +
					" WHERE " + 
					"    ssm.subjectCodeId = ? "+
					" group by vc.id"+
					" ORDER BY vc.id DESC ";
		try {
			videoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {subjectCode},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		return videoContentsList;
	}
	
	/**
	 * @param sessionId 	Session Id to get session related info.
	 * @return int 	Return the count of affected rows in database because of update query.
	 * @throws java.sql.SQlException	If query fails to execute then this exception will be thrown.
	 */
	public int deleteQuickVideoContent(long videoContentId) throws SQLException{
		int result = 0;
		
		//Create JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Prepare SQL Query
		String DELETE_QUICK_VIDEO_CONTENT = "DELETE FROM acads.quick_video_content WHERE id = ? ";
		
		//Execute update method
		result = jdbcTemplate.update(DELETE_QUICK_VIDEO_CONTENT, videoContentId);
		
		//return affected count
		return result;

	}//deleteQuickVideoContent()


	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getBookmarksOfVideoContent(String videoContentIds, String sapId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		String sql ="select content_id as id,bookmarked from bookmarks.content_bookmarks where content_id in ("+videoContentIds+") and sapid = ?";
		try {
			videoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {sapId},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		return videoContentsList;
	}
	
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getVideosUsingPSSIdForMobile(String pssId, String acadDateFormat) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> videoContentsList = new ArrayList<VideoContentAcadsBean>();
		
		String sql ="SELECT v.*, v.acadYear AS year, v.acadMonth AS month, CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName "
				+ "FROM acads.quick_video_content v "
				+ "WHERE v.program_sem_subject_id = ? ";

		if(!"All".equalsIgnoreCase(acadDateFormat))
			sql = sql + "AND v.acadDateFormat = '"+acadDateFormat+"' ";
		
		sql = sql + "ORDER BY v.id DESC";
		try {
			videoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {pssId},
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (Exception e) {
			  		
		}
		return videoContentsList;
	}
	
	@Transactional(readOnly = true)
	public int getVideosUsingPSSIdForMobileCount(String pssId, String acadDateFormat) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT count(*) "
				+ "FROM acads.quick_video_content  "
				+ "WHERE program_sem_subject_id in ("+pssId+") ";
		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
			return count;
		} catch (Exception e) {
			  
			return 0;
		}
		
	}
	
	//Get session videos based on the subject code Id and all academic date format.
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getSessionVideosForSubject(Integer subjectCodeId, String acadDateFormat) throws SQLException{
		StringBuilder GET_VIDEOS_BY_SUBJECTCODEID = null;
		List<VideoContentAcadsBean> sessionVideoList = null;
		
		//Create StringBuilder object
		GET_VIDEOS_BY_SUBJECTCODEID = new StringBuilder();
		
		//Create Empty list of video content bean
		sessionVideoList = new ArrayList<VideoContentAcadsBean>();
		
		//Prepare SQL query
		GET_VIDEOS_BY_SUBJECTCODEID.append("SELECT v.id,v.sessionId,v.subjectCodeId,v.sessionDate,v.subject,v.acadYear AS year, ")
		.append("v.acadMonth AS month, v.description, v.thumbnailUrl,v.track AS track,v.facultyId AS facultyId, ")
		.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName, v.fileName, v.program_sem_subject_id as programSemSubjectId FROM acads.quick_video_content v ")
		.append("WHERE v.subjectCodeId = ? AND v.acadDateFormat IN ("+acadDateFormat+") GROUP BY v.id ");
		
		//Execute JdbcTemplate query method
		sessionVideoList = jdbcTemplate.query(GET_VIDEOS_BY_SUBJECTCODEID.toString(), new Object[] {subjectCodeId},
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		
		//Return session videos 
		return sessionVideoList;
	}
	
	//Get subject code id of a given sessionId.
	@Transactional(readOnly = true)
	public Integer getSubjectCodeId(Integer sessionId) {
		Integer subjectCodeId = 0;
		String GET_SUBJECTCODEID = null;
		boolean isError = false;

		//Prepare SQL query
		GET_SUBJECTCODEID = "select subjectCodeId from acads.session_subject_mapping where sessionId = ? LIMIT 1";

		try {
			//Execute JdbcTemplate method
			subjectCodeId = jdbcTemplate.queryForObject(GET_SUBJECTCODEID, Integer.class, sessionId);
		}catch (Exception e) {
			isError = true;
		}

		if(isError) {
			String GET_SUBJECTCODEID_HISTORY=null;
			GET_SUBJECTCODEID_HISTORY = "select subjectCodeId from acads.session_subject_mapping_history where sessionId = ? LIMIT 1";
			try { 
				//Execute JdbcTemplate method
				subjectCodeId = jdbcTemplate.queryForObject(GET_SUBJECTCODEID_HISTORY, Integer.class, sessionId);
			}catch (Exception e) {
			}
		}
		
		//Return subject code id.
		return subjectCodeId;
	}
	
	//Get all videos having the mobileUrlHd, mobileUrlSd1, mobileUrlSd2 is null
	public List<VideoContentAcadsBean> getBlankLinksVideoDetails() throws Exception {
		String BLANK_LINKS_VIDEO_DETAILS = null; 
		List<VideoContentAcadsBean> blankLinksVideoList = null;
		
		//Prepare SQL query
		BLANK_LINKS_VIDEO_DETAILS = "select id,sessionId,videoLink,substring(videoLink,32) as vimeoId from acads.video_content "
					+"where mobileUrlHd is null and mobileUrlSd1 is null and mobileUrlSd2 is null and createdDate >= DATE_ADD(CURDATE(), INTERVAL -3 DAY)";
		
		//Execute JdbcTemplate method
		blankLinksVideoList = jdbcTemplate.query(BLANK_LINKS_VIDEO_DETAILS, new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
				
		//return list of video 		
		return blankLinksVideoList;
	}
	
	//update video download urls of different resolution. 
	public int updateVideoDownloadUrls(VideoContentAcadsBean sessionVideoDetails, String tableName) throws Exception{
		int updatedCount = 0;
		String UPDATE_DOWNLOAD_URLS=null;
		
		//Prepare SQL query
		UPDATE_DOWNLOAD_URLS = "update "+tableName+" set mobileUrlHd=?, mobileUrlSd1=?, mobileUrlSd2=? where id =? ";
		
		//Execute JdbcTemplate method
		updatedCount = jdbcTemplate.update(UPDATE_DOWNLOAD_URLS, sessionVideoDetails.getMobileUrlHd(),sessionVideoDetails.getMobileUrlSd1(),
				sessionVideoDetails.getMobileUrlSd2(),sessionVideoDetails.getId());
		
		//return affected count
		return updatedCount;
	}//acads.quick_video_content
	
	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getVideoContentEntriesForMasterKey(String consumerProgramStrctureId, String acadYear, String acadMonth) throws Exception{
		ArrayList<VideoContentAcadsBean> quickVideoContentList=new ArrayList<VideoContentAcadsBean>();
		String sql="SELECT qvc.* FROM acads.quick_video_content qvc " + 
				"inner join exam.mdm_subjectcode_mapping scm " + 
				"on qvc.program_sem_subject_id=scm.id " + 
				"where scm.consumerProgramStructureId=? and qvc.acadYear=? and qvc.acadMonth=? group by qvc.id";
		quickVideoContentList = (ArrayList<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {consumerProgramStrctureId, acadYear, acadMonth}, new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		return quickVideoContentList;
	}
	
	@Transactional(readOnly = true)
	public String getPssIdForMasterKeyAndSubject(String consumerProgramStrctureId, String subject) {
		String sql="SELECT scm.id FROM exam.mdm_subjectcode_mapping scm inner join exam.mdm_subjectcode sc on scm.subjectCodeId=sc.id " + 
				" where consumerProgramStructureId=? and subjectname=?";
		String pssId=jdbcTemplate.queryForObject(sql, new Object[] {consumerProgramStrctureId,subject}, new SingleColumnRowMapper<String>(String.class));
		return pssId;
	}
	
	/**
	 * @param VideoContentAcadsBean The VideoContentBean having the
	 *                              sessionId, facultyName, facultyId and
	 *                              program_sem_subject_id
	 * @param sessionId
	 * @return boolean
	 * @throws java.sql.SQlException
	 */
	@Transactional(readOnly = false)
	public boolean insertQuickVideoContentForNewMasterKey(VideoContentAcadsBean quickVdoCnt) throws SQLException {
		
		StringBuilder INSERT_QUICK_VIDEO_CONTENT = null;

		// Creating StringBuilder object
		INSERT_QUICK_VIDEO_CONTENT = new StringBuilder();
			
		// Preparing SQL Query
		INSERT_QUICK_VIDEO_CONTENT.append("INSERT INTO acads.quick_video_content (id, sessionId, subject, sessionDate, ");
		INSERT_QUICK_VIDEO_CONTENT.append("description, thumbnailUrl, program_sem_subject_id, track, firstName, lastName, ");
		INSERT_QUICK_VIDEO_CONTENT.append("facultyId, createdDate, lastModifiedDate,fileName,videoLink,addedOn,mobileUrlHd, ");
		INSERT_QUICK_VIDEO_CONTENT.append("mobileUrlSd1,mobileUrlSd2,audioFile,subjectCodeId,acadMonth,acadYear,acadDateFormat ) ");
		INSERT_QUICK_VIDEO_CONTENT.append("values (?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?,?,?,?,?,?,?,?,?, ");
		INSERT_QUICK_VIDEO_CONTENT.append("DATE_FORMAT(STR_TO_DATE(CONCAT(? ,? , '01'), '%Y %M %d'), '%Y-%m-%d') ) ");
		
		int insertQuickVideoContent = jdbcTemplate.update(INSERT_QUICK_VIDEO_CONTENT.toString(),
				new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setLong(1, quickVdoCnt.getId());
						ps.setLong(2, quickVdoCnt.getSessionId());
						ps.setString(3, quickVdoCnt.getSubject());
						ps.setString(4, quickVdoCnt.getSessionDate());
						ps.setString(5, quickVdoCnt.getDescription());
						ps.setString(6, quickVdoCnt.getThumbnailUrl());
						ps.setString(7, quickVdoCnt.getProgramSemSubjectId());
						ps.setString(8, quickVdoCnt.getTrack());
						ps.setString(9, quickVdoCnt.getFirstName());
						ps.setString(10, quickVdoCnt.getLastName());
						ps.setString(11, quickVdoCnt.getFacultyId());
						
						ps.setString(12, quickVdoCnt.getFileName());
						ps.setString(13, quickVdoCnt.getVideoLink());
						ps.setString(14, quickVdoCnt.getAddedOn());
						ps.setString(15, quickVdoCnt.getMobileUrlHd());
						ps.setString(16, quickVdoCnt.getMobileUrlSd1());
						ps.setString(17, quickVdoCnt.getMobileUrlSd2());
						ps.setString(18, quickVdoCnt.getAudioFile());
						ps.setInt(19, quickVdoCnt.getSubjectCodeId());
						
						ps.setString(20, quickVdoCnt.getAcadMonth());
						ps.setString(21, quickVdoCnt.getAcadYear());
						ps.setString(22, quickVdoCnt.getAcadYear());
						ps.setString(23, quickVdoCnt.getAcadMonth());
					}
				});
		return true;
	}// insertQuickVideoContent()
	
	public List<SearchBean> getApplicableCycles(String programSemSubjectIds) {
	     List<SearchBean> acadCycles = null;
	     
	     String QUERY ="SELECT acadMonth as month, acadYear as year FROM acads.quick_video_content WHERE program_sem_subject_id IN ("+programSemSubjectIds+")";
	     
	     //Execute JdbcTemplate method
	     acadCycles = jdbcTemplate.query(QUERY,
	                 new BeanPropertyRowMapper<SearchBean>(SearchBean.class));
	     
	     return acadCycles;
	 }
	
	//Get all entries in De-Normal table for a given input values.
		public List<VideoContentAcadsBean> getSessionVideoDropdownValues(String PSSIds,String acadDateFormat,String facultyId, String track){
			List<VideoContentAcadsBean> filterValuesList =null;
			StringBuilder GET_FILTER_VALUES = null;
			
			//Create empty filter value list
			filterValuesList = new ArrayList<VideoContentAcadsBean>();
			
			//Create StringBuilder class Object
			GET_FILTER_VALUES = new StringBuilder();
			
			//Prepare SQL query
			GET_FILTER_VALUES.append("SELECT facultyId, firstName, lastName, track, acadMonth as month, acadYear as year, ")
			.append("program_sem_subject_id as programSemSubjectId,subject FROM acads.quick_video_content ")
			.append("WHERE program_sem_subject_id IN ("+PSSIds+") ");
			
			//append acadDateFormat only if it is not 'All'
			if(!"All".equalsIgnoreCase(acadDateFormat)) {
				acadDateFormat = ContentUtil.prepareAcadDateFormat(acadDateFormat);
				GET_FILTER_VALUES.append("AND acadDateFormat ="+acadDateFormat);
			}
			
			//append facultyId only if it is not 'All'
			if(!"All".equalsIgnoreCase(facultyId))
				GET_FILTER_VALUES.append(" AND facultyId ='"+facultyId+"' ");
			
			//append track only if it is not 'All'
			if(!"All".equalsIgnoreCase(track))
				GET_FILTER_VALUES.append(" AND track ='"+track+"' ");
			
			try {
				//Execute JdbcTemplate query method.
				filterValuesList = jdbcTemplate.query(GET_FILTER_VALUES.toString(), 
						new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			//Return the list of records
			return filterValuesList;
		}
		
		@Transactional(readOnly = true)
		public List<String> getBatchTracksWithSubjectNames(ArrayList<String> allsubjects){
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			String sql= "select distinct(track) from acads.sessions where track is not null and track !='' and  subject in (:subjects) ";
			queryParams.addValue("subjects", allsubjects);
			return (ArrayList<String>) namedJdbcTemplate.query(sql,queryParams,  new SingleColumnRowMapper(String.class));
		}
		
		@Transactional(readOnly = true)
		public List<String> getCommonSubjectVideosList(String commonSubjects,String faculty,
													String cycle,String batch,String program){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<String> commonSubVdoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT= new StringBuilder();
			
			//Creating empty array list of VideoContentBean
			commonSubVdoContentList = new ArrayList<String>();
			
			GET_VIDEO_CONTENT.append("SELECT distinct v.subject FROM acads.video_content v ");
			GET_VIDEO_CONTENT.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ");
			GET_VIDEO_CONTENT.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ");
			GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubjects+") ");
			
			if(!"BBA".equalsIgnoreCase(program) && !"B.Com".equalsIgnoreCase(program)
					&& !"MBA - WX".equalsIgnoreCase(program)){
				GET_VIDEO_CONTENT.append(" AND ((programList like ('%"+program+"%')) OR (programList='All')) ");			
			}else {
				GET_VIDEO_CONTENT.append(" AND programList LIKE ('%"+program+"%') ");
			}
			
			//Add faculty to SQL query if faculty info is not All
			if (!"All".equals(faculty))
				GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

			//Add Cycle to SQL query if Cycle info is not All
			if (!"All".equals(cycle))
				GET_VIDEO_CONTENT.append(" and concat(v.month,v.year)='" + cycle + "' ");

			//Add Batch to SQL query if Batch info is not All 
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" and s.track='" + batch + "' ");
			
			try {
			//Execute JdbcTemplate query method
			commonSubVdoContentList = (List<String>) jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
														new Object[]{},new SingleColumnRowMapper(String.class));
			}catch (Exception e) {
				  e.printStackTrace();
			}
			
			//return video Content details as a list
			return commonSubVdoContentList;
		}
		
		@Transactional(readOnly = true)
		public List<String> getSessionVideosByTrack(String searchItem, String faculty,
																String programSemSubjectIds, 
																String acadDateFormat, String batch){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<String> videoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT = new StringBuilder();
			
			//Create empty array list of video content bean
			videoContentList = new ArrayList<String>();
			
			//Prepare SQL query
			GET_VIDEO_CONTENT.append("SELECT  v.track AS track ");
			GET_VIDEO_CONTENT.append(" FROM acads.quick_video_content v ");
			GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
			GET_VIDEO_CONTENT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");
			
			//Add faculty to SQL query if faculty info is not All
			if (!"All".equals(faculty)) 
				GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");
			
			GET_VIDEO_CONTENT.append(" AND v.track is not null AND v.track !='' Group by v.track ");
			
			try {
				//Execute JdbcTemplate query method to get videos by program sem subject id
				videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
						new Object[]{},new SingleColumnRowMapper(String.class));
			}catch (Exception e) {
				  e.printStackTrace();
			}
			
			//return video Content details as a list
			return videoContentList;
		}
		

		@Transactional(readOnly = true)
		public List<ConsumerProgramStructureAcads> getSessionVideosBySubject(String searchItem, String faculty,
																String programSemSubjectIds, 
																String acadDateFormat, String batch){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<ConsumerProgramStructureAcads> videoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT = new StringBuilder();
			
			//Create empty array list of video content bean
			videoContentList = new ArrayList<ConsumerProgramStructureAcads>();
			
			//Prepare SQL query
			GET_VIDEO_CONTENT.append("SELECT  v.program_sem_subject_id AS programSemSubjectId,v.subject ");
			GET_VIDEO_CONTENT.append(" FROM acads.quick_video_content v ");
			GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
			GET_VIDEO_CONTENT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");
			
			//Add faculty to SQL query if faculty info is not All
			if (!"All".equals(faculty)) 
				GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

			//Add Batch to SQL query if Batch info is not All
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" and v.track='" + batch + "' ");
			GET_VIDEO_CONTENT.append(" Group by v.program_sem_subject_id ");
			
			try {
				//Execute JdbcTemplate query method to get videos by program sem subject id
				videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
						new Object[]{},new BeanPropertyRowMapper<ConsumerProgramStructureAcads>(ConsumerProgramStructureAcads.class));
			}catch (Exception e) {
				  e.printStackTrace();
			}
			
			//return video Content details as a list
			return videoContentList;
		}
		
		@Transactional(readOnly = true)
		public List<FacultyFilterBean> getSessionVideosByFaculty(String searchItem, String faculty,
																String programSemSubjectIds, 
																String acadDateFormat, String batch){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<FacultyFilterBean> videoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT = new StringBuilder();
			
			//Create empty array list of video content bean
			videoContentList = new ArrayList<FacultyFilterBean>();
			
			//Prepare SQL query
			GET_VIDEO_CONTENT.append("SELECT v.facultyId AS facultyId, ");
			GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName,v.firstName, v.lastName ");
			GET_VIDEO_CONTENT.append(" FROM acads.quick_video_content v ");
			GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
			GET_VIDEO_CONTENT.append("AND v.acadDateFormat IN ("+acadDateFormat+") ");
			
			//Add faculty to SQL query if faculty info is not All
			//if (!"All".equals(faculty)) 
				//GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

			//Add Batch to SQL query if Batch info is not All
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" and v.track='" + batch + "' ");
			GET_VIDEO_CONTENT.append(" Group by v.facultyId ");
			
			try {
				//Execute JdbcTemplate query method to get videos by program sem subject id
				videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
						new Object[]{},new BeanPropertyRowMapper<FacultyFilterBean>(FacultyFilterBean.class));
				
			}catch (Exception e) {
				  //e.printStackTrace();
			}
			//return video Content details as a list
			return videoContentList;
		}
		
		@Transactional(readOnly = true)
		public List<SearchBean> getSessionVideosByAcadCycle(String faculty,
																String programSemSubjectIds,String batch){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<SearchBean> videoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT = new StringBuilder();
			
			//Create empty array list of video content bean
			videoContentList = new ArrayList<SearchBean>();
			
			//Prepare SQL query
			GET_VIDEO_CONTENT.append("SELECT acadYear as year ,acadMonth as month FROM acads.quick_video_content v ");
			GET_VIDEO_CONTENT.append("WHERE v.program_sem_subject_id IN ("+programSemSubjectIds+") ");
			
			//Add faculty to SQL query if faculty info is not All
			if (!"All".equals(faculty)) 
				GET_VIDEO_CONTENT.append(" and v.facultyId='" + faculty + "' ");

			//Add Batch to SQL query if Batch info is not All
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" and v.track='" + batch + "' ");
			GET_VIDEO_CONTENT.append(" AND (acadMonth='Jan' OR acadMonth='Jul') and (acadYear is not null AND acadYear <> '') group by acadMonth,acadYear ");
			
			try {
				//Execute JdbcTemplate query method to get videos by program sem subject id
			
				videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
						new Object[]{},new BeanPropertyRowMapper<SearchBean>(SearchBean.class));
				
			}catch (Exception e) {
				//  e.printStackTrace();
			}
			//return video Content details as a list
			return videoContentList;
		}
		
		@Transactional(readOnly = true)
		public List<SearchBean> getAcadCycleOfCommonSession(String commonSubjects,String faculty,String batch,String consumerProgramStructureId){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<SearchBean> commonSubVdoContentList = null;
			//Creating StringBuilder object
			GET_VIDEO_CONTENT= new StringBuilder();
			
			//Creating empty array list of VideoContentBean
			commonSubVdoContentList = new ArrayList<SearchBean>();
			
			GET_VIDEO_CONTENT.append("SELECT  v.month,v.year FROM ");
			GET_VIDEO_CONTENT.append(" acads.quick_common_sessions v	");
			GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonSubjects+") AND v.consumerProgramStructureId = ?  ");
			
			//Add faculty to SQL query if faculty info is not All
			if (!"All".equals(faculty))
				GET_VIDEO_CONTENT.append(" AND v.facultyId='" + faculty + "' ");

			//Add Batch to SQL query if Batch info is not All 
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" AND v.track='" + batch + "' ");
			
			GET_VIDEO_CONTENT.append("AND (month='Jan' OR month='Jul') and (year is not null AND year <> '') group by month,year " );
			
			try {
			//Execute JdbcTemplate query method
			
			commonSubVdoContentList = (List<SearchBean>) jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
														new Object[]{consumerProgramStructureId}, new BeanPropertyRowMapper<SearchBean>(SearchBean.class));
			}catch (Exception e) {
				  //e.printStackTrace();
			}
			
			//return video Content details as a list
			return commonSubVdoContentList;
		}
		
		@Transactional(readOnly = true)
		public List<FacultyFilterBean> getCommonSessionVideosByFaculty(String faculty,
																String commonsubjects, 
																String cycle, String batch,String consumerProgramStructureId){
			StringBuilder GET_VIDEO_CONTENT = null;
			List<FacultyFilterBean> videoContentList = null;

			//Creating StringBuilder object
			GET_VIDEO_CONTENT = new StringBuilder();
			
			//Create empty array list of video content bean
			videoContentList = new ArrayList<FacultyFilterBean>();
			
			//Prepare SQL query
			GET_VIDEO_CONTENT.append("SELECT  ");
			GET_VIDEO_CONTENT.append("v.facultyId AS facultyId, ");
			GET_VIDEO_CONTENT.append("CONCAT('Prof. ', v.firstName, ' ', v.lastName) AS facultyName,v.firstName, v.lastName ");
			GET_VIDEO_CONTENT.append(" FROM acads.quick_common_sessions v ");
			GET_VIDEO_CONTENT.append("WHERE v.subject IN ("+commonsubjects+") ");
			GET_VIDEO_CONTENT.append("AND v.consumerProgramStructureId = ? ");

			//Add Batch to SQL query if Batch info is not All
			if (!"All".equals(batch))
				GET_VIDEO_CONTENT.append(" and v.track='" + batch + "' ");
			
			if (!"All".equals(cycle))
				GET_VIDEO_CONTENT.append("AND concat(month,year)  IN ('"+cycle+"') ");
			
			GET_VIDEO_CONTENT.append(" Group by v.facultyId ");
			
			try {
				//Execute JdbcTemplate query method to get videos by program sem subject id
				videoContentList = jdbcTemplate.query(GET_VIDEO_CONTENT.toString(),
						new Object[]{consumerProgramStructureId},new BeanPropertyRowMapper<FacultyFilterBean>(FacultyFilterBean.class));
				
			}catch (Exception e) {
				//  e.printStackTrace();
			}
			//return video Content details as a list
			return videoContentList;
		}
		
		@Transactional(readOnly = true)
		public PageAcads<VideoContentAcadsBean> getVideoContentPageForFaculty(int pageNo, int pageSize, ArrayList<String> allsubjects,
				ArrayList<String> academicCycleListForDb, StudentAcadsBean student, String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String subjectCommaSeparated = "''";
			if (allsubjects == null) {
				subjectCommaSeparated = "''";
			} else {
				for (int i = 0; i < allsubjects.size(); i++) {
					if (i == 0) {
						subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
					} else {
						subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''")
								+ "'";
					}
				}
			}

			String cycleCommaSeparated = "''";
			if (academicCycleListForDb == null) {
				cycleCommaSeparated = "''";
			} else {
				for (int i = 0; i < academicCycleListForDb.size(); i++) {
					if (i == 0) {
						cycleCommaSeparated = "'" + academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
					} else {
						cycleCommaSeparated = cycleCommaSeparated + ", '"
								+ academicCycleListForDb.get(i).replaceAll("'", "''") + "'";
					}
				}
			}
			
			String programCheck = "";
			if (student != null) {
				if(student.getConsumerProgramStructureId().equalsIgnoreCase("113")) {
					programCheck = " AND s.corporateName = 'M.sc'";
				}else {
					programCheck = " AND (s.corporateName <> 'M.sc' or s.corporateName is null or s.corporateName = '')";
				}
			}
			

			/*String sql = "select s.track as track, concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.id, v.sessionId, "
					+ "v.fileName, v.keywords, v.description, v.subject, v.defaultVideo, v.duration, v.sessionDate, v.addedOn, "
					+ "v.addedBy, v.year, v.month, v.createdBy, v.createdDate, v.lastModifiedDate, v.lastModifiedBy, v.videoLink, "
					+ "v.thumbnailUrl, v.mobileUrlHd, v.mobileUrlSd1, v.mobileUrlSd2, v.sessionPlanModuleId, v.videoTranscriptUrl,"
					+ "s.meetingKey, f.facultyId as facultyId from acads.video_content v "
					+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
					+ "	acads.sessions s " + "	on s.id = v.sessionId" + " where v.subject in (" + subjectCommaSeparated
					+ ") and concat(v.month,v.year) in (" + cycleCommaSeparated + ") Order By v.id desc"; */
			
			String sql =" SELECT  " + 
						"    s.track AS track, " + 
						"    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
						"    v.id, v.sessionId,v.fileName,v.keywords,v.description,v.subject,v.defaultVideo,v.duration,v.sessionDate, " +
						"	 v.addedOn,v.addedBy,v.year,v.month, v.createdBy,v.createdDate,v.lastModifiedDate,v.lastModifiedBy, " +
						" 	 v.videoLink,v.thumbnailUrl, v.mobileUrlHd,v.mobileUrlSd1,v.mobileUrlSd2,v.sessionPlanModuleId, " +
						" 	 v.videoTranscriptUrl,s.meetingKey,f.facultyId AS facultyId " + 
						" FROM " + 
						"    acads.video_content v " + 
						"        LEFT JOIN " + 
						"    acads.faculty f ON f.facultyId = v.facultyId " + 
						"        LEFT JOIN " + 
						"    acads.sessions s ON s.id = v.sessionId " + 
						" WHERE " + 
						"    v.subject IN (" + subjectCommaSeparated + ") " + 
						" 	AND concat(v.month,v.year) in (" + cycleCommaSeparated + ") " +
							programCheck +
							 " AND (s.facultyId='"+facultyId+"' or s.altFacultyId='"+facultyId+"' or s.altFacultyId2='"+facultyId+"' or s.altFacultyId3='"+facultyId+"') "+
						" ORDER BY v.id DESC" ;
			
			String countSql = "select count(*) from acads.video_content v " + "left join " + "	acads.faculty f "
							+ "    on f.facultyId = v.facultyId " + "left join " + "	acads.sessions s "
							+ "	on s.id = v.sessionId" + " where v.subject in (" + subjectCommaSeparated
							+ ") and concat(v.month,v.year) in (" + cycleCommaSeparated + ") "
							+ programCheck
							+ " AND (s.facultyId='"+facultyId+"' or s.altFacultyId='"+facultyId+"' or s.altFacultyId2='"+facultyId+"' or s.altFacultyId3='"+facultyId+"') "
							+ "Order BY v.id DESC";
			
			PaginationHelper<VideoContentAcadsBean> pagingHelper = new PaginationHelper<VideoContentAcadsBean>();
			PageAcads<VideoContentAcadsBean> page;
			try {
				page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, null, pageNo, pageSize,
						new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			} catch (Exception e) {
				e.printStackTrace();
				page = new PageAcads<VideoContentAcadsBean>();
				  
			}		
			return page;
		}
		// getVideoContentPage Old end
		
		@Transactional(readOnly = true)
		public List<VideoContentAcadsBean> getVideoForSearchByFilterForFaculty(String searchItem, String faculty,
				ArrayList<String> subjects, String cycle, String batch, StudentAcadsBean student, String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<VideoContentAcadsBean> VideoContentsList = null;
			String sql = "";
			String subjectCommaSeparated = "''";
			String tempSubject = "";
			String programCheck = "";
			
			try {
				if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
					programCheck = " AND s.corporateName = 'M.sc'";
				} else {
					programCheck = " AND (s.corporateName <> 'M.sc' or s.corporateName is null or s.corporateName = '')";
				}
			} catch (Exception ex) {
			
			}
			
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'" + subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			if (!"".equals(searchItem)) {
				sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
						+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
						+ "	acads.sessions s " + "	on s.id = v.sessionId   "
						/*
						 * + " LEFT JOIN  " +
						 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
						 */
						+ " where v.subject in (" + subjectCommaSeparated + ") " + " and (v.subject Like '%"
						+ searchItem.trim() + "%' " + " or keywords Like '%" + searchItem.trim() + "%' "
						+ " or description Like '%" + searchItem.trim() + "%' " + " or fileName Like '%" + searchItem
						+ "%' ) "
						+" AND (s.facultyId='"+facultyId+"' or s.altFacultyId='"+facultyId+"' or s.altFacultyId2='"+facultyId+"' or s.altFacultyId3='"+facultyId+"') ";
				/*
				 * if(student != null) { sql +=
				 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
				 * ()+" "; }
				 */
			} else {
				sql = "select s.track as track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName,v.*,s.meetingKey from acads.video_content v  "
						+ "left join " + "	acads.faculty f " + "    on f.facultyId = v.facultyId " + "left join "
						+ "	acads.sessions s " + "	on s.id = v.sessionId"
						/*
						 * + " LEFT JOIN  " +
						 * "    acads.videocontentid_consumerprogramstructureid_mapping vcm ON v.id = vcm.videoContentId "
						 */
						+ " where v.subject in (" + subjectCommaSeparated + ") "
						+" AND (s.facultyId='"+facultyId+"' or s.altFacultyId='"+facultyId+"' or s.altFacultyId2='"+facultyId+"' or s.altFacultyId3='"+facultyId+"') ";
				/*
				 * if(student != null) { sql +=
				 * " and vcm.consumerProgramStructureId= "+student.getConsumerProgramStructureId
				 * ()+" "; }
				 */

			}
			if (!"All".equals(faculty)) {
				sql = sql + " and v.facultyId='" + faculty + "'";
			}
			if (!"All".equals(cycle)) {
				sql = sql + " and concat(v.month,v.year)='" + cycle + "'";
			}

			//for batch track filter
			if(!"All".equals(batch)) {
				sql=sql+" and s.track='"+batch+"'";
			}

			sql = sql + programCheck ;
			sql = sql + " group by v.id ";
			sql = sql + " Order By v.id desc ";
			
			try {
				VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
						new BeanPropertyRowMapper(VideoContentAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return VideoContentsList;
		}

		// end
		
		// Get all faculties by students subjects start
		@Transactional(readOnly = true)
		public List<FacultyAcadsBean> getFacultYByFacultyId(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select * from acads.faculty where active='Y' and facultyId = '"+facultyId+"'";
			List<FacultyAcadsBean> facultyList = new ArrayList<FacultyAcadsBean>();
			try {
				facultyList = (List<FacultyAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
						new BeanPropertyRowMapper(FacultyAcadsBean.class));
			} catch (Exception e) {
				  e.printStackTrace();
			}

			return facultyList;
		}
		// Get all faculties by students subjects end
		
		@Transactional(readOnly = true)
		public ArrayList<String> getFacultySubjectListForSessions(String facultyId) {
			ArrayList<String> subjectList = null;

			try {
				String sql = "select subject from acads.sessions " + 
						"where facultyId=? or altFacultyId=? or altFacultyId2=? or altFacultyId3=?" + 
						"group by subject";

			 subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{facultyId,facultyId,facultyId, facultyId} ,new SingleColumnRowMapper<>(String.class));
			} catch (Exception e) {
				  
			}
			return subjectList;
		}
		
		@Transactional(readOnly = true)
		public List<VideoContentAcadsBean> getRelatedVideoContentListForFaculty(String searchItem, String subject,
				ArrayList<String> allsubjects, String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<VideoContentAcadsBean> VideoContentsList = null;
			List<VideoContentAcadsBean> VideoTopicsList = null;
			String sql = "";
			String sqlTopics = "";
			String subjectCommaSeparated = "''";
			String tempSubject = "";

			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'" + allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '" + allsubjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			if (!"".equals(searchItem) && subject == null) {

				sql = "select v.*,s.meetingKey from acads.video_content v "
						+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject in ("
						+ subjectCommaSeparated + ") and " + "(v.subject Like '%" + searchItem.trim()
						+ "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%" + searchItem.trim()
						+ "%' or v.fileName Like '%" + searchItem + "%' )"
						+ " AND s.facultyId='" + facultyId + "' ";

				sql += " group By v.id " + " Order By v.id desc";
				sqlTopics = "select * from acads.video_content_subtopics " + " where subject in (" + subjectCommaSeparated
						+ ") " + " and (keywords Like '%" + searchItem.trim() + "%' " + " or description Like '%"
						+ searchItem.trim() + "%' " + " or fileName Like '%" + searchItem + "%' ) " + "Order By id desc";
			}
			if ("".equals(searchItem) && !"".equals(subject) && subject!=null) {
				subject.replaceAll("&", "_");
				subject = subject.replaceAll("'", "''");
				
				sql = "select v.*,s.meetingKey,s.track from acads.video_content v "
						+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject like '" + subject
						+ "' AND s.facultyId='" + facultyId
						+ "' Order By v.id desc";
				

				sqlTopics = "select * from acads.video_content_subtopics where subject like '" + subject
						+ "' and id=0 Order By id desc";

			}
			if (!"".equals(searchItem) && !"".equals(subject) && subject!=null) {
				subject = subject.replaceAll("'", "''");
				
				sql = "select v.*,s.meetingKey,s.track from acads.video_content v "
						+ "inner join acads.sessions s on s.id = v.sessionId " + "where v.subject in ("
						+ subjectCommaSeparated + ") " + "and (v.subject Like '%" + subject + "%' or v.subject Like '%"
						+ searchItem + "%' or v.keywords Like '%" + searchItem.trim() + "%' or v.description Like '%"
						+ searchItem.trim() + "%' or v.fileName Like '%" + searchItem + "%') AND s.facultyId='" + facultyId+"' Order By v.id desc";

				sqlTopics = "select * from acads.video_content_subtopics where subject in (" + subjectCommaSeparated
						+ ") and  (subject Like '%" + subject + "%' or subject Like '%" + searchItem
						+ "%' or keywords Like '%" + searchItem.trim() + "%' or description Like '%" + searchItem.trim()
						+ "%' or fileName Like '%" + searchItem + "%') Order By id desc";
			}
			try {
				VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] {},
						new BeanPropertyRowMapper(VideoContentAcadsBean.class));
				VideoTopicsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sqlTopics, new Object[] {},
						new BeanPropertyRowMapper(VideoContentAcadsBean.class));
				VideoContentsList.addAll(VideoTopicsList);
			} catch (DataAccessException e) {
				  
			}
			return VideoContentsList;
		}
		
		/**
		 * Get all session videos matching for a search keyword.
		 * @param searchKeyword - search keyword to fetch matching session videos. 
		 * @return List return the list of session videos matching for a given search keyword.
		 * @throws java.lang.Exception If any exception occurs while executing the query.
		 */
		@Transactional(readOnly = true)
		public List<VideoContentAcadsBean> getSessionVideosBySearchAndFaculty(String searchKeyword, String facultyId) throws Exception{
			List<VideoContentAcadsBean> sessionVideosList = null;
			StringBuilder GET_SESSION_VIDEOS = null;
			
			//Create StringBuilder object
			GET_SESSION_VIDEOS = new StringBuilder();
			
			//Creating empty list of video content bean
			sessionVideosList = new ArrayList<VideoContentAcadsBean>();
			
			//Prepare SQL query
			GET_SESSION_VIDEOS.append("SELECT v.*,s.meetingKey,CONCAT('Prof. ',f.firstName,' ',f.lastName) AS facultyName, ")
			.append("s.track AS track FROM acads.video_content v ")
			.append("INNER JOIN acads.sessions s ON s.id = v.sessionId ")
			.append("INNER JOIN acads.faculty f ON f.facultyId = v.facultyId ")
			.append("WHERE v.subject LIKE '%"+searchKeyword+"%' OR v.keywords LIKE '%"+searchKeyword+"%' ")
			.append("OR v.description LIKE '%"+searchKeyword+"%' OR v.fileName LIKE '%"+searchKeyword+"%' ")
			.append(" AND (s.facultyId='"+facultyId+"' or s.altFacultyId='"+facultyId+"' or s.altFacultyId2='"+facultyId+"' or s.altFacultyId3='"+facultyId+"' ");
			
			//Execute JdbcTemplate query method
			sessionVideosList = jdbcTemplate.query(GET_SESSION_VIDEOS.toString(), 
					new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
			
			//return session video list matching for search keyword
			return sessionVideosList;
		}
		
		/***
		 * @param	contentBean	Having the subjectCodeId, year and month
		 * @return	List<VideoContentBean> return the list of VideoContentBean having 
		 * 								   the filtered(based on above parameter) data   
		 */
		@Transactional(readOnly = true)
		public  List<VideoContentAcadsBean> getVideoContentForSubjectAndFacultyId(ContentAcadsBean contentBean, String facultyId)
		{
			StringBuilder GET_VIDEOS_BY_SUBJECTCODEID = null;
			List<VideoContentAcadsBean> videoContentList = null;
			
			//Creating empty list
			videoContentList = new ArrayList<VideoContentAcadsBean>();
			
			//Creating StringBuilder object
			GET_VIDEOS_BY_SUBJECTCODEID = new StringBuilder();
			
			//Preparing SQL query
			GET_VIDEOS_BY_SUBJECTCODEID.append("SELECT vc.*,s.meetingKey,s.track,ssm.subjectCodeId,s.startTime FROM acads.video_content vc ")
			.append("INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = vc.sessionId ")
			.append("INNER JOIN acads.sessions s ON s.id = vc.sessionId ")
			.append("WHERE ssm.subjectCodeId = ? and vc.year = ? and vc.month = ? and s.facultyId=? GROUP BY vc.id order by s.startTime DESC");
			
			try {
				videoContentList = jdbcTemplate.query(GET_VIDEOS_BY_SUBJECTCODEID.toString(),new Object[] {
											contentBean.getSubjectCodeId(),contentBean.getYear(),contentBean.getMonth(), facultyId}, 
											new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
				
			} catch (Exception e) {
				  
			}
			return videoContentList;
		}

		@Transactional(readOnly = true)
		public List<VideoContentAcadsBean> getAcadsVideoContent(String year, String month, String fromSessionDate, String toSessionDate, String facultyId) {
			namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			
			StringBuilder sqlString = new StringBuilder();
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
							
			sqlString.append("SELECT * FROM acads.video_content WHERE year=:year AND month=:month ");
			queryParams.addValue("year", year);
			queryParams.addValue("month", month);
			
			if(StringUtils.isNotBlank(fromSessionDate) || StringUtils.isNotEmpty(fromSessionDate)) {
				sqlString.append("AND sessionDate >=:fromSessionDate ");
				queryParams.addValue("fromSessionDate", fromSessionDate);
			}
			
			if(StringUtils.isNotBlank(toSessionDate) || StringUtils.isNotEmpty(toSessionDate)) {
				sqlString.append("AND sessionDate <=:toSessionDate ");
				queryParams.addValue("toSessionDate", toSessionDate);
			}
		
			if(StringUtils.isNotBlank(facultyId) || StringUtils.isNotEmpty(facultyId)) {
				sqlString.append("AND facultyId =:facultyId ");
				queryParams.addValue("facultyId", facultyId);
			}
			
			List<VideoContentAcadsBean> videoContents = new ArrayList<>();
			videoContents = namedJdbcTemplate.query(sqlString.toString(), queryParams, new BeanPropertyRowMapper<>(VideoContentAcadsBean.class));
			return videoContents;
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getAcadsVideoContentWithSubjectCode(List<Integer> sessionIdList, String subjectCodeId) {
			
			namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			String sql = "SELECT DISTINCT sessionID FROM acads.session_subject_mapping WHERE sessionID IN (:sessionIdList) AND subjectCodeId=(:subjectCodeId);";
			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("sessionIdList", sessionIdList);
			queryParams.addValue("subjectCodeId", subjectCodeId);
			
			List<Integer> videoContents = new ArrayList<>();
			videoContents = namedJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(Integer.class));
			return videoContents;
		}
}