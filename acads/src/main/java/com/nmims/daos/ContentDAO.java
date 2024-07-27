package com.nmims.daos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Scanner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;



import org.springframework.jdbc.core.ResultSetExtractor;

import org.springframework.jdbc.core.SingleColumnRowMapper;


import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;



import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import com.nmims.beans.AnnouncementAcadsBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ConsumerProgramStructureAcadsBean;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ELearnResourcesAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FileMigrationBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.Post;
import com.nmims.beans.PostsAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.ReRegistrationAcadsBean;

import com.nmims.beans.Post;


import com.nmims.beans.SearchTimeBoundContent;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.PaginationHelper;
import com.nmims.util.ContentUtil;
import com.nmims.util.DateTimeUtil;




public class ContentDAO extends BaseDAO{
	@Autowired(required = false)
	ApplicationContext act;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	

	private  NamedParameterJdbcTemplate nameJdbcTemplate;

	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private Logger logger = LoggerFactory.getLogger("session_recording_upload");
	
	public static final String GivenFORMAT_ddMMMyyyyHHmmss = "yyyy-MM-dd'T'HH:mm";
	
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
	public ArrayList<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT DISTINCT subjectname AS subject FROM exam.mdm_subjectcode ORDER BY subjectname";
		
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		subjectList.add("Orientation");
		subjectList.add("Assignment");
		
		return subjectList;

	}
	
	@Transactional(readOnly = true)
	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Double.class);


		}catch(Exception e){
			  
		}
		
		return examOrder;
		
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentAcadsBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			ArrayList<StudentAcadsBean> studentList = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new Object[]{sapid,sapid
			}, new BeanPropertyRowMapper(StudentAcadsBean.class));
			
			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);

				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			return student;
		}catch(Exception e){
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentRegistrationData(String sapId) {
		StudentAcadsBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
		} catch (Exception e) {
			  
		}
		return studentRegistrationData;
	}
	
	@Transactional(readOnly = true)
	public UserAuthorizationBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationBean user = (UserAuthorizationBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationBean.class));
			return user;
		} catch (Exception e) {
			  
		}

		return null;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAuthorizedCenterCodes(UserAuthorizationBean userAuthorization) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> centers = new ArrayList<String>();
		
		//Convert Mumbai, Kolkata, Delhi to 'Mumbai','Kolkata','Delhi'
		String authorizedLCWithQuotes = "'" + userAuthorization.getAuthorizedLC() + "'";
		authorizedLCWithQuotes = authorizedLCWithQuotes.replaceAll(",", "','");
		
		
		if(userAuthorization.getAuthorizedLC() != null && !"".equals(userAuthorization.getAuthorizedLC().trim())){
			String sql = "SELECT sfdcId FROM exam.centers where lc in (" + authorizedLCWithQuotes + ")";
			centers = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		}
		
		if(userAuthorization.getAuthorizedCenters() != null && !"".equals(userAuthorization.getAuthorizedCenters().trim())){
			//Add IC codes
			List<String> authorizedICs = Arrays.asList(userAuthorization.getAuthorizedCenters().split("\\s*,\\s*"));
			centers.addAll(authorizedICs);
		}
		
		return centers;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getFacultySubjects(String facultyId) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.* from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			programSubjectMappingList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new Object[]{facultyId},
					new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return programSubjectMappingList;
	}
	
	//@Transactional(readOnly = false)
	public String addIntoVideoContent(VideoContentAcadsBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			
			try {
				logger.info("Session recording insertion stated for EMBA having SessionID : "+ bean.getSessionId());
				
				return upsertVideoContent(bean, jdbcTemplate);
			} catch (Exception e) {
				logger.error("While inserting Session recording for EMBA - Error Message :"+e.getMessage());
			}
		}else {
			try {
				logger.info("Session recording  insertion stated for PG having SessionID : " + bean.getSessionId());
				
				return upsertVideoContentForPG(bean, jdbcTemplate);
			} catch (SQLException e) {
				logger.error("While inserting Session recording for PG - Error Message :"+e.getMessage());
			}
		}
		return null;
		
	}
	
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Transactional(readOnly = false)
	public ArrayList<String> batchUpdateVideoContent(final List<VideoContentAcadsBean> videoContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentAcadsBean bean = videoContentList.get(i);
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}
				upsertVideoContent(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	// batchUpdateVideoTopic Start
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoTopic(final List<VideoContentAcadsBean> videoContentList) {

		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		
		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentAcadsBean bean = videoContentList.get(i);
				long key = dao.saveVideoSubTopic(bean);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	// batchUpdateVideoTopic End
	
/*Commented and kept as will require later by PS
 * 	private void upsertVideoContent(VideoContentBean bean, JdbcTemplate jdbcTemplate) {

		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.video_content(year, month, subject, fileName , keywords , description , defaultVideo, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate, videoLink, addedOn, thumbnailUrl, mobileUrlHd, mobileUrlSd1, mobileUrlSd2, sessionId, facultyId, sessionDate,sessionPlanModuleId) VALUES "
				+ "(?,?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,?,?,?,?,?,?,?,?,?)"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate(), " 
				+ "	    videoLink = ?, "
				+ "	    thumbnailUrl = ?, "
				+ "	    mobileUrlHd = ?, "
				+ "	    mobileUrlSd1 = ?, "
				+ "	    mobileUrlSd2 = ? ";

		final String year = bean.getYear();
		final String month = bean.getMonth();
		final String subject = bean.getSubject();
		final String fileName = bean.getFileName();
		final String keyWords = bean.getKeywords();
		final String description = bean.getDescription();
		final String defaultVideo = bean.getDefaultVideo();
		final String createdBy = bean.getCreatedBy();  
		final String videoLink = bean.getVideoLink();  
		final String mobileUrlHd = bean.getMobileUrlHd();  
		final String mobileUrlSd1 = bean.getMobileUrlSd1();  
		final String mobileUrlSd2 = bean.getMobileUrlSd2();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
		final String addedOn = sdf.format(new Date());
		final String thumbnailUrl=bean.getThumbnailUrl();
		final Integer sessionId = bean.getSessionId();
		final String facultyId= bean.getFacultyId();
		final String sessionDate = bean.getSessionDate();	
		final Long sessionPlanModuleId = bean.getSessionPlanModuleId();
		
		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				fileName,
				keyWords,
				description,
				defaultVideo,
				createdBy,
				createdBy, 
				videoLink,
				addedOn,
				thumbnailUrl,
				mobileUrlHd,
				mobileUrlSd1,
				mobileUrlSd2,
				sessionId,
				facultyId,
				sessionDate,
				bean.getSessionPlanModuleId(),
				
				year,
				month,
				subject,
				createdBy, 
				videoLink,
				thumbnailUrl,
				mobileUrlHd,
				mobileUrlSd1,
				mobileUrlSd2 
		});

		
		// for inserting data into posts table
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//for insert
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, subject);
				ps.setString(4, fileName);
				ps.setString(5, keyWords);
				ps.setString(6, description);
				ps.setString(7, defaultVideo);
				ps.setString(8, createdBy);
				ps.setString(9, createdBy);
				ps.setString(10, videoLink);
				ps.setString(11, addedOn);
				ps.setString(12, thumbnailUrl);
				ps.setString(13, mobileUrlHd);
				ps.setString(14, mobileUrlSd1);
				ps.setString(15, mobileUrlSd2);
				ps.setInt(16, sessionId);
				ps.setString(17, facultyId);
				ps.setString(18, sessionDate);
				ps.setLong(19, sessionPlanModuleId);
				
				//for update
				ps.setString(20, year);
				ps.setString(21, month);
				ps.setString(22, subject);
				ps.setString(23, createdBy);
				ps.setString(24, videoLink);
				ps.setString(25, thumbnailUrl);
				ps.setString(26, mobileUrlHd);
				ps.setString(27, mobileUrlSd1);
				ps.setString(28, mobileUrlSd2);
		
				return ps;
			}
		}, holder);
		
		int id = holder.getKey().intValue();
		insertRecordingsPostEMBA(bean,id,"insert");
		
	}*/
	
	//upsertVideoContent Old Start

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public String upsertVideoContent(VideoContentAcadsBean bean, JdbcTemplate jdbcTemplate) throws Exception {
		logger.debug("START");

		
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.video_content(year, month, subject, fileName , keywords , description , defaultVideo, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate, videoLink, addedOn, thumbnailUrl, mobileUrlHd, mobileUrlSd1, mobileUrlSd2, sessionId, facultyId, sessionDate,sessionPlanModuleId,videoTranscriptUrl, duration, audioFile) VALUES "
				+ "(?,?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?)"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate(), " 
				+ "	    videoLink = ?, "
				+ "	    thumbnailUrl = ?, "
				+ "	    mobileUrlHd = ?, "
				+ "	    mobileUrlSd1 = ?, "
				+ "	    mobileUrlSd2 = ?, "
				+ "	    duration = ?, "
				+ "     audioFile = ? ";

		final String year = bean.getYear();
		final String month = bean.getMonth();
		final String subject = bean.getSubject();
		final String fileName = bean.getFileName();
		final String keyWords = bean.getKeywords();
		final String description = bean.getDescription();
		final String defaultVideo = bean.getDefaultVideo();
		final String createdBy = bean.getCreatedBy();  
		final String videoLink = bean.getVideoLink();  
		final String mobileUrlHd = bean.getMobileUrlHd();  
		final String mobileUrlSd1 = bean.getMobileUrlSd1();  
		final String mobileUrlSd2 = bean.getMobileUrlSd2();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
		final String addedOn = sdf.format(new Date());
		final String thumbnailUrl=bean.getThumbnailUrl();
		final Integer sessionId = bean.getSessionId();
		final String facultyId= bean.getFacultyId();
		final String sessionDate = bean.getSessionDate();	
		final Long sessionPlanModuleId = bean.getSessionPlanModuleId();
		final String videoTranscriptUrl = bean.getVideoTranscriptUrl();
		final String duration  = bean.getDuration();
		final String audioFile = bean.getAudioFile();
		
/*		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				fileName,
				keyWords,
				description,
				defaultVideo,
				createdBy,
				createdBy, 
				videoLink,
				addedOn,
				thumbnailUrl,
				mobileUrlHd,
				mobileUrlSd1,
				mobileUrlSd2,
				sessionId,
				facultyId,
				sessionDate,
				bean.getSessionPlanModuleId(),
				
				year,
				month,
				subject,
				createdBy, 
				videoLink,
				thumbnailUrl,
				mobileUrlHd,
				mobileUrlSd1,
				mobileUrlSd2 
		});
*/
		
		// for inserting data into posts table
		
			jdbcTemplate.update(new PreparedStatementCreator() {				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	//for insert

					ps.setString(1, year);
					ps.setString(2, month);
					ps.setString(3, subject);
					ps.setString(4, fileName);
					ps.setString(5, keyWords);
					ps.setString(6, description);
					ps.setString(7, defaultVideo);
					ps.setString(8, createdBy);
					ps.setString(9, createdBy);
					ps.setString(10, videoLink);
					ps.setString(11, addedOn);
					ps.setString(12, thumbnailUrl);
					ps.setString(13, mobileUrlHd);
					ps.setString(14, mobileUrlSd1);
					ps.setString(15, mobileUrlSd2);
					ps.setInt(16, sessionId);
					ps.setString(17, facultyId);
					ps.setString(18, sessionDate);
					ps.setLong(19, sessionPlanModuleId);
					ps.setString(20, videoTranscriptUrl);
					ps.setString(21, duration);	
					ps.setString(22, audioFile);
					
					//for update
					ps.setString(23, year);
					ps.setString(24, month);
					ps.setString(25, subject);
					ps.setString(26, createdBy);
					ps.setString(27, videoLink);
					ps.setString(28, thumbnailUrl);
					ps.setString(29, mobileUrlHd);
					ps.setString(30, mobileUrlSd1);
					ps.setString(31, mobileUrlSd2);
					ps.setString(32,duration);
					ps.setString(33, audioFile);
					
					return ps;
				}
			}, holder);
	
			int id = holder.getKey().intValue();
			
			logger.info("Session Recording of '"+sessionId+"' inserted in video_content successfully with videoContentId :"+id );
			
			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
				insertRecordingsPostEMBA(bean,id,"insert");
			}
			
			//Verify id is not zero
			if (id != 0 && (sessionPlanModuleId == null || sessionPlanModuleId == 0)) {
				insertQuickVideoContent(prepareVideoContenList(bean), id);
			}//if
			
			logger.debug("END");
			return "success";
	}
	

	/**
	 * @param facultyNameWithPSSIds The List of VideoContentBean having the
	 *                              sessionId, facultyName, facultyId and
	 *                              program_sem_subject_id
	 * @param sessionId
	 * @return boolean
	 * @throws java.sql.SQlException
	 */
	@Transactional(readOnly = false)
	public boolean insertQuickVideoContent(final List<VideoContentAcadsBean> facultyNameWithPSSIds, 
			final Integer videoContentId) throws SQLException {
		
		logger.debug("STRAT");
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
		
		int[] batchInsertQuickVideoContent = jdbcTemplate.batchUpdate(INSERT_QUICK_VIDEO_CONTENT.toString(),
				new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				VideoContentAcadsBean quickVdoCnt = facultyNameWithPSSIds.get(i);
				ps.setLong(1, videoContentId);
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
				
				ps.setString(20, quickVdoCnt.getMonth());
				ps.setString(21, quickVdoCnt.getYear());
				ps.setString(22, quickVdoCnt.getYear());
				ps.setString(23, quickVdoCnt.getMonth());
				
			}

			@Override
			public int getBatchSize() {
				return facultyNameWithPSSIds.size();
			}

		});
		logger.debug("END");
		// return batchInsertQuickVideoContent.length;
		return true;
	}// insertQuickVideoContent()
	
	
	/**
	 *@param	bean */

	public List<VideoContentAcadsBean> prepareVideoContenList(VideoContentAcadsBean bean){
		logger.debug("STRAT");
		
		List<VideoContentAcadsBean> quickVideoContentList = new ArrayList<VideoContentAcadsBean>();
		FacultyAcadsBean facultyDetails = new FacultyAcadsBean();
		List<VideoContentAcadsBean> pssIdsAndFaultyNameList = new ArrayList<VideoContentAcadsBean>();

		
		try {
			pssIdsAndFaultyNameList = fetchPSSIdAndFacultyNameBySessionId(bean.getSessionId());
		} catch (SQLException e) {
			  
		}
		
		try {
			facultyDetails = this.getFacultyDetails(bean.getFacultyId());
		} catch (Exception e) {
			  
		}
		
		for (VideoContentAcadsBean vcBean : pssIdsAndFaultyNameList) {
			VideoContentAcadsBean vdoCntBean = new VideoContentAcadsBean();
			
			vdoCntBean.setSessionId(bean.getSessionId());
			vdoCntBean.setSubject(bean.getSubject());
			vdoCntBean.setSessionDate(bean.getSessionDate());
			vdoCntBean.setFacultyId(bean.getFacultyId());
			vdoCntBean.setThumbnailUrl(bean.getThumbnailUrl());
			vdoCntBean.setDescription(bean.getDescription());
			
			vdoCntBean.setVideoLink(bean.getVideoLink());
			vdoCntBean.setAddedOn(bean.getAddedOn());
			vdoCntBean.setMobileUrlHd(bean.getMobileUrlHd());
			vdoCntBean.setMobileUrlSd1(bean.getMobileUrlSd1());
			vdoCntBean.setMobileUrlSd2(bean.getMobileUrlSd2());
			vdoCntBean.setFileName(bean.getFileName());
			vdoCntBean.setAudioFile(bean.getAudioFile());
			vdoCntBean.setMonth(bean.getMonth());
			vdoCntBean.setYear(bean.getYear());
			
			vdoCntBean.setTrack(vcBean.getTrack());
			vdoCntBean.setFirstName(facultyDetails.getFirstName());
			vdoCntBean.setLastName(facultyDetails.getLastName());
			vdoCntBean.setProgramSemSubjectId(vcBean.getProgramSemSubjectId());
			vdoCntBean.setSubjectCodeId(vcBean.getSubjectCodeId());
			
			quickVideoContentList.add(vdoCntBean);
			
			vdoCntBean = null;
		}//for
		
		logger.info(bean.getSessionId()+": SessionId is applicable for total no of program sem subject Ids :"+quickVideoContentList.size());
		logger.debug("END");
		
		return quickVideoContentList;
	}
	
	@Transactional(readOnly = true)
	private FacultyAcadsBean getFacultyDetails(String facultyId) throws Exception{
		String GET_FACULTY_DETAILS = null;
		
		// Create JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Preparing SQL Query
		GET_FACULTY_DETAILS = "SELECT facultyId,firstName,lastName,email,active,mobile FROM acads.faculty WHERE facultyId = ? and active = 'Y'";
		
		// Execute JdbcTemplate's query method
		FacultyAcadsBean facultyDetails = jdbcTemplate.queryForObject(GET_FACULTY_DETAILS, new Object[] {facultyId}, 
				new BeanPropertyRowMapper<FacultyAcadsBean>(FacultyAcadsBean.class));
		
		//return faculty details		
		return facultyDetails;
	}

	/**
	 * @param sessionId
	 * @return List<VideoContentBean> Return the List of VideoContentBean having the
	 *         firstName, lastName, program_sem_subject_id and track
	 * @throws java.sql.SQlException
	 */
	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> fetchPSSIdAndFacultyNameBySessionId(Integer sessionId) throws SQLException {
		List<VideoContentAcadsBean> facultyNameAndPSSList = null;
		StringBuilder GET_PSSID_FACULTY_NAME = null;

		// Create JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Creating empty ArrayList of VideoContentBean
		facultyNameAndPSSList = new ArrayList<VideoContentAcadsBean>();

		// Creating StringBuilder object
		GET_PSSID_FACULTY_NAME = new StringBuilder();
		
		// Preparing SQL Query
		GET_PSSID_FACULTY_NAME.append("SELECT program_sem_subject_id AS programSemSubjectId,ssc.subjectCodeId, s.track FROM acads.sessions s ");
		GET_PSSID_FACULTY_NAME.append("INNER JOIN acads.session_subject_mapping ssc ON s.id = ssc.sessionId WHERE s.id = ? ");

		// Execute JdbcTemplate's query method
		facultyNameAndPSSList = jdbcTemplate.query(GET_PSSID_FACULTY_NAME.toString(), new Object[] { sessionId },
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));

		// return facultyName and PSS list
		return facultyNameAndPSSList;
	}// fetchPSSIdAndFacultyNameBySessionId()


	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public String upsertVideoContentForPG(VideoContentAcadsBean bean, JdbcTemplate jdbcTemplate)  throws SQLException {
		logger.debug("START");

		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.video_content(year, month, subject, fileName , keywords , description , defaultVideo, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate, videoLink, addedOn, thumbnailUrl, mobileUrlHd, mobileUrlSd1, mobileUrlSd2, sessionId, facultyId, sessionDate,videoTranscriptUrl,duration,audioFile) VALUES "
				+ "(?,?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,?,?,?,?,?,?,?,?,?,?,?)"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate(), " 
				+ "	    videoLink = ?, "
				+ "	    thumbnailUrl = ?, "
				+ "	    mobileUrlHd = ?, "
				+ "	    mobileUrlSd1 = ?, "
				+ "	    mobileUrlSd2 = ?, "
				+ "	    duration = ?, "
				+ "		audioFile = ? ";

		final String year = bean.getYear();
		final String month = bean.getMonth();
		final String subject = bean.getSubject();
		final String fileName = bean.getFileName();
		final String keyWords = bean.getKeywords();
		final String description = bean.getDescription();
		final String defaultVideo = bean.getDefaultVideo();
		final String createdBy = bean.getCreatedBy();  
		final String videoLink = bean.getVideoLink();  
		final String mobileUrlHd = bean.getMobileUrlHd();  
		final String mobileUrlSd1 = bean.getMobileUrlSd1();  
		final String mobileUrlSd2 = bean.getMobileUrlSd2();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
		final String addedOn = sdf.format(new Date());
		final String thumbnailUrl=bean.getThumbnailUrl();
		final Integer sessionId = bean.getSessionId();
		final String facultyId= bean.getFacultyId();
		final String sessionDate = bean.getSessionDate();	
		//final Long sessionPlanModuleId = bean.getSessionPlanModuleId() == null ? 0 : bean.getSessionPlanModuleId();
		final String videoTranscriptUrl = bean.getVideoTranscriptUrl();
		final String duration  = bean.getDuration();
		final String audioFile = bean.getAudioFile();
		
		// for inserting data into posts table
		
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	//for insert
					ps.setString(1, year);
					ps.setString(2, month);
					ps.setString(3, subject);
					ps.setString(4, fileName);
					ps.setString(5, keyWords);
					ps.setString(6, description);
					ps.setString(7, defaultVideo);
					ps.setString(8, createdBy);
					ps.setString(9, createdBy);
					ps.setString(10, videoLink);
					ps.setString(11, addedOn);
					ps.setString(12, thumbnailUrl);
					ps.setString(13, mobileUrlHd);
					ps.setString(14, mobileUrlSd1);
					ps.setString(15, mobileUrlSd2);
					ps.setInt(16, sessionId);
					ps.setString(17, facultyId);
					ps.setString(18, sessionDate);
					//ps.setLong(19, sessionPlanModuleId);
					ps.setString(19, videoTranscriptUrl);
					ps.setString(20, duration);
					ps.setString(21, audioFile);

					//for update
					ps.setString(22, year);
					ps.setString(23, month);
					ps.setString(24, subject);
					ps.setString(25, createdBy);
					ps.setString(26, videoLink);
					ps.setString(27, thumbnailUrl);
					ps.setString(28, mobileUrlHd);
					ps.setString(29, mobileUrlSd1);
					ps.setString(30, mobileUrlSd2);
					ps.setString(31, duration);
					ps.setString(32, audioFile);
					return ps;
				}
			}, holder);
	
			int id = holder.getKey().intValue();
//			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
//				insertRecordingsPostEMBA(bean,id,"insert");
//			}
			if(id != 0) {
				logger.info("Session Recording of '"+sessionId+"' inserted in video_content successfully with videoContentId :"+id );
				boolean result = insertQuickVideoContent(prepareVideoContenList(bean), id);
			}
			logger.debug("END");
			return "success";
	}
	
	//upsetVideoContent Old End
	@Transactional(readOnly = true)
	public ArrayList<String> getFacultySubjectList(String facultyId) {
		ArrayList<String> subjectList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.subject from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y' and forumLive='Y') ";

		 subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{facultyId} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			  
		}
		return subjectList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject where active = 'Y' order by program, sem, subject";

		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		return programSubjectMappingList;
	}
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForSubjects(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content where subject = ? "
				   + " UNION "
				   + " SELECT * FROM acads.content_history where subject = ? ";;

		List<ContentAcadsBean> contents = jdbcTemplate.query(sql,  new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,subject);
				preparedStatement.setString(2,subject);
			}}, new BeanPropertyRowMapper(ContentAcadsBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForSubjectsForCurrentSession(String subject, ContentAcadsBean content) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM acads.content WHERE subject = ? AND month = ? AND year = ? "
				   + " UNION " 
				   + " SELECT * FROM acads.content_history WHERE subject = ? "
				   + " AND month = ? AND year = ? " ;
				
		/*String sql = "SELECT  " + 
				"    c.* " + 
				"FROM " + 
				"    acads.content c, " + 
				"    exam.content_live_settings cls, " + 
				"    acads.contentid_consumerprogramstructureid_mapping ccm " + 
				"WHERE " + 
				"    c.id = ccm.contentId " + 
				"        AND ccm.consumerProgramStructureId = cls.consumerProgramStructureId " + 
				"        AND cls.year = c.year " + 
				"        AND cls.month = c.month " + 
				"        AND c.subject = ? " + 
				"GROUP BY c.id";*/
		
		List<ContentAcadsBean> contents = jdbcTemplate.query(sql, new Object[]{subject, content.getMonth(), content.getYear(),subject, content.getMonth(), content.getYear()}, 
										new BeanPropertyRowMapper(ContentAcadsBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForLeads(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    acads.content_forleads " + 
				"WHERE " + 
				"    subject = ?";

		List<ContentAcadsBean> contents = jdbcTemplate.query(sql,  new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,subject);
			}}, new BeanPropertyRowMapper(ContentAcadsBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContents(ContentAcadsBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where subject = ? and year = ? and month = ? "
				  + " UNION "
			      + " SELECT * FROM acads.content_history c where subject = ? and year = ? and month = ? ";;

		List<ContentAcadsBean> contents = jdbcTemplate.query(sql, new Object[]{searchBean.getSubject(), searchBean.getYear(), searchBean.getMonth(),searchBean.getSubject(), searchBean.getYear(), searchBean.getMonth()}, 
				new BeanPropertyRowMapper(ContentAcadsBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getAllContents() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content "
				    + " UNION "
			        + " SELECT * FROM acads.content_history ";;

		List<ContentAcadsBean> contents= new ArrayList<>();
		try {
			contents = jdbcTemplate.query(sql ,new BeanPropertyRowMapper(ContentAcadsBean .class));
		} catch (DataAccessException e) {
			  
		}
		
		return contents;
	}
	

	public List<ContentAcadsBean> getContentsForIds(ArrayList<String> contentToTransfer) {
		//String contentIds = StringUtils.join(contentToTransfer, ',');
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where id in (:contentIds)";
		
		
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("contentIds", contentToTransfer);


		List<ContentAcadsBean> contents = nameJdbcTemplate.query(sql, parameters,new BeanPropertyRowMapper(ContentAcadsBean.class));
		if(contents.size() == 0)
			contents = getContentsForIdsInHistoryTable(contentToTransfer);
		return contents;
	}
	
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForMultipleSubjects(ArrayList<String> subjectList) {
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjectList.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjectList.get(i).replaceAll("'", "''")+ "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjectList.get(i).replaceAll("'", "''") + "'";
			}
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content where subject in ("+subjectCommaSeparated+") ";

		List<ContentAcadsBean> contents = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ContentAcadsBean .class));
		if(contents.size() == 0)
			contents = getContentsForMultipleSubjectsInHistoryTab(subjectCommaSeparated);
		
		return contents;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<ProgramSubjectMappingAcadsBean> subjectsList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));

		return subjectsList;
	}

	
	public long saveContentDetails(ContentAcadsBean bean,final  String createdBy,final String lastModifiedBy, final String year,final  String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,activeDate) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";


		final String description = bean.getDescription();
		final String filePath = bean.getFilePath();
		final String previewPath = bean.getPreviewPath();
		final String webFileurl = bean.getWebFileurl();
		final String name = bean.getName();
		final String urlType = bean.getUrlType();
		final String programStructure = bean.getProgramStructure();
		final String contentType = bean.getContentType();
		final String subjectname = bean.getSubject();
//		jdbcTemplate.update(sql, new Object[] { 
//				year,
//				month,
//				subject,
//				name,
//				description,
//				
//				filePath,
//				previewPath,
//				webFileurl,
//				urlType,
//				contentType,
//				programStructure,
//				createdBy,
//				lastModifiedBy,
//		});	
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, subjectname);
				ps.setString(4, name);
				ps.setString(5, description);
				ps.setString(6, filePath);
				ps.setString(7, previewPath);
				ps.setString(8, webFileurl);
				ps.setString(9, urlType);
				ps.setString(10, contentType);
				ps.setString(11, programStructure);
				ps.setString(12, createdBy);
				ps.setString(13, lastModifiedBy);
				ps.setString(14, bean.getActiveDate());
				return ps;
			}
		};
		


		jdbcTemplate.update(psc, keyHolder);

		long primaryKey = keyHolder.getKey().intValue();
		
		//Insertion into lti.Post Started
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert", subjectname, year, month);
		}
		return primaryKey;
	}
	
	//duplicate the above function so to return primary key after insert
	
	public long saveContentFileDetails(final ContentAcadsBean bean,final String subject, final String year, final String month) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,sessionPlanModuleId,activeDate) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?)";


		final String description = bean.getDescription();
		final String filePath = bean.getFilePath();
		final String previewPath = bean.getPreviewPath();
		final String webFileurl = bean.getWebFileurl();
		final String name = bean.getName();
		final String createdBy = bean.getCreatedBy();
		final String lastModifiedBy = bean.getLastModifiedBy();
		final String urlType = bean.getUrlType();
		final String programStructure = bean.getProgramStructure();
		final String contentType = bean.getContentType();
		final Long sessionPlanModuleId = bean.getSessionPlanModuleId();

		final Long newSessionPlanModuleId= sessionPlanModuleId == null ? 0 : sessionPlanModuleId;
		
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, year);
		        statement.setString(2, month);
		        statement.setString(3, subject);
		        statement.setString(4, name);
		        statement.setString(5, description);
		        
		        statement.setString(6, filePath);
		        statement.setString(7, previewPath);
		        statement.setString(8, webFileurl);
		        statement.setString(9, urlType);
		        statement.setString(10, contentType);
		        statement.setString(11, programStructure);
		        statement.setString(12, createdBy);
		        statement.setString(13, lastModifiedBy);
		        statement.setLong(14, newSessionPlanModuleId);
		        statement.setString(15, bean.getActiveDate());
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();
		
		if(newSessionPlanModuleId != null && newSessionPlanModuleId != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert",subject,year,month);
		}
		
		return primaryKey;
	

	}
	
	//saving content for leads 
	@Transactional(readOnly = false)
	public long saveContentFileDetailsForLeads(final ContentAcadsBean bean,final String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			final String sql = " INSERT INTO acads.content_forLeads (subject, name, description, "
					+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,sessionPlanModuleId) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";


			final String description = bean.getDescription();
			final String filePath = bean.getFilePath();
			final String previewPath = bean.getPreviewPath();
			final String webFileurl = bean.getWebFileurl();
			final String name = bean.getName();
			final String createdBy = bean.getCreatedBy();
			final String lastModifiedBy = bean.getLastModifiedBy();
			final String urlType = bean.getUrlType();
			final String programStructure = bean.getProgramStructure();
			final String contentType = bean.getContentType();
			final Long sessionPlanModuleId = bean.getSessionPlanModuleId();

			final Long newSessionPlanModuleId= sessionPlanModuleId == null ? 0 : sessionPlanModuleId;
			
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, subject);
			        statement.setString(2, name);
			        statement.setString(3, description);
			        
			        statement.setString(4, filePath);
			        statement.setString(5, previewPath);
			        statement.setString(6, webFileurl);
			        statement.setString(7, urlType);
			        statement.setString(8, contentType);
			        statement.setString(9, programStructure);
			        statement.setString(10, createdBy);
			        statement.setString(11, lastModifiedBy);
			        statement.setLong(12, newSessionPlanModuleId);
			        		        
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();
			
			return primaryKey;
		} catch (DataAccessException e) {
			  
			return 0;
		}

		}
	
	@Transactional(readOnly = true)
	public ConsumerProgramStructureAcads getConsumerDataForLeads(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructureAcads consumerTypeLeads = null;
		
		String sql =  "SELECT id,name FROM exam.consumer_type where name='retail'";
		
		try {
			consumerTypeLeads = (ConsumerProgramStructureAcads) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		} catch (Exception e) {
			
			  
			return null;
		}
		return consumerTypeLeads;  
	}

	@Transactional(readOnly = true)
	public ConsumerProgramStructureAcads getProgramrStructureForLeads() {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructureAcads programStructureForLeads = null;
		
		String sql =  "SELECT DISTINCT " + 
				"    ps.program_structure as name, " + 
				"    ps.id " + 
				" FROM " + 
				"    exam.program_structure ps " + 
				"        LEFT JOIN" + 
				"    exam.consumer_program_structure cps ON cps.programStructureId = ps.id " + 
				" WHERE " + 
				"    cps.consumerTypeId = 6" + 
				" ORDER BY ps.program_structure DESC " + 
				" LIMIT 1";
		
		try {
			programStructureForLeads = (ConsumerProgramStructureAcads) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		} catch (Exception e) {
			
			  
			return null;
		}
		
		return programStructureForLeads;  
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcadsBean> getProgramsForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "SELECT cps.programId,cps.programStructureId,cps.consumerTypeId, p.code FROM exam.consumer_program_structure cps left join exam.program p on cps.programId=p.id where cps.programStructureId=8 and cps.consumerTypeId=6 order by p.code;";

		ArrayList<ConsumerProgramStructureAcadsBean> subjectsList = (ArrayList<ConsumerProgramStructureAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcadsBean.class));
		return subjectsList;
	}
	
	
	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentFiles(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where subject = ? ";
		
		List<ContentAcadsBean> contentFiles = jdbcTemplate.query(sql,  new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,subject);
			}}, new BeanPropertyRowMapper(ContentAcadsBean.class));
		if(contentFiles.size() == 0)
			contentFiles = getContentFilesInHistoryTable(subject);
		return contentFiles;
	}
	

	
@Transactional(readOnly = false)
	  public void deleteContent(String id) {
	  
	  String sql = "Delete from acads.content where id = ?"; 
	  jdbcTemplate = new JdbcTemplate(dataSource); 
	  jdbcTemplate.update(sql, new PreparedStatementSetter() { public void setValues(PreparedStatement
	  preparedStatement) throws SQLException { preparedStatement.setString(1,id);
	  }});
	  
	  }
	 
	//duplicated above method to return no of rows deleted
	
	public int deleteContentById(long id) {
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		//try {
			int deletedRows =  jdbcTemplate.update(sql,  new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setLong(1,id);
				}});
			
			return deletedRows;
//		} catch (DataAccessException e) {
//			// TODO Auto-generated catch block
//			  
//			return 0;
//		}
		
	}
	
	
	public int deleteContentIdAndMasterKeyMappingByIdAndMasterkey(String contentId,String consumerProgramStructureId) {
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping "
					+ "where contentId = :contentId and consumerProgramStructureId in ( :consumerProgramStructureIds )";
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		
		List<Integer> consumerProgramStructureIds = Stream.of(consumerProgramStructureId.split("\\D+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("contentId", contentId);
		parameters.addValue("consumerProgramStructureIds", consumerProgramStructureIds);
		

		//try {

			int deletedRows =  nameJdbcTemplate.update(sql, parameters );

			if(deletedRows == 0)
				deletedRows = deleteContentIdAndMasterKeyMappingByIdAndMasterkeyInHistory(contentId,consumerProgramStructureId);
			return deletedRows;
			

//		} catch (DataAccessException e) {
//			// TODO Auto-generated catch block
//			  
//			return 0;
//		}

		
	}

	@Transactional(readOnly = true)
	public ContentAcadsBean findById(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("Select * from acads.content where id = ? "
				    + "union " 
				    + "Select * from acads.content_history where id = ? ");
		

		ContentAcadsBean content = null;
		
			content = (ContentAcadsBean)jdbcTemplate.queryForObject(sql.toString(), new Object[]{id,id}, new BeanPropertyRowMapper(ContentAcadsBean.class));
		

		return content;
	}
	
	@Transactional(readOnly = true)
	public Post findPostByReferenceId(String id) {  
		jdbcTemplate = new JdbcTemplate(dataSource);  
		String sql = "Select * from lti.post where referenceId =? and type='Resource'";          
		return (Post) jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(Post.class));  
		
	}

	public int updateContent(ContentAcadsBean content) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.content set "
				+ " name = ?, "
				+ " description = ?,"
				+ " programStructure = ?, "
				+ " webFileurl = ?, "
				+ " urlType = ?, "
				+ " contentType = ?, "
				+ " lastModifiedBy = ?, "
				+ " lastModifiedDate = current_timestamp() "
				+ " where id = ? ";
		
		int i = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,content.getName());
				preparedStatement.setString(2,content.getDescription());
				preparedStatement.setString(3,content.getProgramStructure());
				preparedStatement.setString(4,content.getWebFileurl());
				preparedStatement.setString(5,content.getUrlType());
				preparedStatement.setString(6,content.getContentType());
				preparedStatement.setString(7,content.getLastModifiedBy());
				preparedStatement.setString(8,content.getId());
			}
			});	
		
		if(content.getSessionPlanModuleId() != null && content.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(content, Integer.parseInt(content.getId()), "update",content.getSubject(),content.getYear(),content.getMonth());		}
		return i;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getUnAttemptedSubjects(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select r.program, r.sem, ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and ps.subject not in (select subject from exam.passfail where sapid = ?)";

		ArrayList<ProgramSubjectMappingAcadsBean> subjectsList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId, sapId}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getNotPassedSubjectsBasedOnSapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select er.program, er.sem,ps.subject from exam.registration er,exam.program_subject ps, exam.students s "
				+" where er.sapid = ? " 
				+" and s.sapid = er.sapid "
				+" and er.program = ps.program "
				+" and er.sem = ps.sem "
				+" and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+" and ps.subject not in (select subject from exam.passfail where sapid = ?)";
		ArrayList<ProgramSubjectMappingAcadsBean> notPassedSubjectsList = (ArrayList<ProgramSubjectMappingAcadsBean>) jdbcTemplate.query(sql, new Object[]{sapid,sapid}, 
				new BeanPropertyRowMapper<ProgramSubjectMappingAcadsBean>(ProgramSubjectMappingAcadsBean.class));
		return notPassedSubjectsList;
	}

	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getRecordingForLastCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c, exam.examorder eo where subject = ? "
				+ " and c.month = eo.acadMonth and c.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' ";

		List<ContentAcadsBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentAcadsBean .class));
		if(contents.size() == 0)
			contents = getRecordingForLastCycleInHistoryTable(subject);
		return contents;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<AnnouncementAcadsBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<AnnouncementAcadsBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementAcadsBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	//	Added for SAS
	  @SuppressWarnings("rawtypes")
	  @Transactional(readOnly = true)
		public List<AnnouncementAcadsBean> getAllActiveAnnouncements(String program,String progrmStructure){
			String sql = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";

			}else{
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			}
			
			List<AnnouncementAcadsBean> announcements = jdbcTemplate.query(sql, new Object[]{program,progrmStructure}, new BeanPropertyRowMapper(AnnouncementAcadsBean.class));

			return announcements;
		}
//		Added for SAS end
	  
	  @Transactional(readOnly = true)
		public StudentAcadsBean getStudentMaxSemRegistrationData(String sapId) {
			StudentAcadsBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select * from exam.registration r where r.sapid = ?  "
						+ "and r.sem = (select max(registration.sem) from exam.registration where sapid = ?) ";

				studentRegistrationData = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
			} catch (Exception e) {
				  
			}
			return studentRegistrationData;
		}
		
	  @Transactional(readOnly = true)
		public List<ExamOrderAcadsBean> getLiveFlagDetails(){
			List<ExamOrderAcadsBean> liveFlagList = new ArrayList<ExamOrderAcadsBean>();

			final String sql = " Select * from exam.examorder order by examorder.order ";
			jdbcTemplate = new JdbcTemplate(dataSource);

			liveFlagList = (ArrayList<ExamOrderAcadsBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderAcadsBean>(ExamOrderAcadsBean.class));

			return liveFlagList;
		}
	  
	  @Transactional(readOnly = false)
		public boolean saveSessionQA(SessionDayTimeAcadsBean sessionQA, Logger logger){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;
			
			String sql="insert into acads.session_question_answer(meetingKey,sessionId,sapId,query,answer,status,createdDate) VALUES (?,?,?,?,?,?,sysdate())";
			try {
				row=jdbcTemplate.update(sql, new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, sessionQA.getMeetingKey());
						ps.setString(2, sessionQA.getSession_id());
						ps.setString(3, sessionQA.getSapId());
						ps.setString(4, sessionQA.getQuestion());
						ps.setString(5, sessionQA.getAnswer());
						ps.setString(6, sessionQA.getIsAnswered());
					}
					});
				return row > 0;
			} catch (DataAccessException e) {
				StringWriter sw = new StringWriter();
				 e.printStackTrace(new PrintWriter(sw));
				 String exceptionAsString = sw.toString();
				 logger.error("Exception occur "+exceptionAsString);
				 return false;
			}
		}

	  @Transactional(readOnly = true)
		public String getstudentByEmail(String emailId) {
			StudentAcadsBean studentList = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  distinct *  from exam.students  where emailId = ? limit 1"	;
				studentList = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{emailId}, new BeanPropertyRowMapper(StudentAcadsBean.class));
				 return studentList.getSapid();
			} catch (Exception e) {
				  
				return "";
			}
		}
		
	  @Transactional(readOnly = true)
		public String getFacultyEmailId(String facultyId) {
			
			String emailid = "";
			try {
				
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select email  from acads.faculty   where facultyId= ? "	;
				emailid = jdbcTemplate.queryForObject(sql, new Object[]{facultyId},(String.class));
				return emailid;
			} catch (Exception e) {
				  
				return emailid;
			}
		}
		
	  @Transactional(readOnly = true)
		public ArrayList<SessionBean> getSessionsHeldPerDay(String sessionDate) {
			
			ArrayList<SessionBean> sessionsList = new ArrayList<SessionBean>();
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select *  from acads.sessions  where date = ?"	;

				sessionsList = (ArrayList<SessionBean>)jdbcTemplate.query(sql, new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, sessionDate);
					}
				}, new BeanPropertyRowMapper(SessionBean.class));
			} catch (Exception e) {
				  
			}
			
			return sessionsList;
		}
		
	  @Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getCompletedSessionsOfFacultyForPg(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new  ArrayList<SessionDayTimeAcadsBean>();
			try {
//				String sql = "SELECT s.* FROM acads.sessions s, exam.examorder eo "
//						+ " where  s.month = eo.acadmonth and s.year = eo.year and s.moduleid is null"
//						+ " and eo.order in (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
//						+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )  and  EXISTS(SELECT q.* FROM acads.session_question_answer q where  s.id=q.sessionId)";
				
				String sql = " SELECT  " + 
						"    s.*, " + 
						"    (SELECT " + 
						"            COUNT(q.query) " + 
						"        FROM " + 
						"            acads.session_question_answer q " + 
						"        WHERE " + 
						"            s.id = q.sessionId "+
						"  AND ((s.facultyId = ? AND s.meetingKey = q.meetingKey) " + 
						"                OR (s.altFacultyId = ? AND s.altMeetingKey = q.meetingKey) " + 
						"                OR (s.altFacultyId2 = ? AND s.altMeetingKey2 = q.meetingKey) " + 
						"                OR (s.altFacultyId3 = ? AND s.altMeetingKey3 = q.meetingKey)) "+
						" AND q.status = 'Open' AND LENGTH(q.query) > 10 ) AS count " + 
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
						"WHERE " + 
						"    s.moduleid IS NULL " + 
//						"        AND eo.order IN (SELECT " + 
//						"            MAX(examorder.order) " + 
//						"        FROM " + 
//						"            exam.examorder " + 
//						"        WHERE " + 
//						"            acadSessionLive = 'Y') " + 
						"        AND (s.facultyId =  ? " + 
						"        OR s.altFacultyId = ? " + 
						"        OR altFacultyId2 =  ? " + 
						"        OR altFacultyId3 =  ? ) " + 
						"        AND s.id IN (SELECT  " + 
						"            q.sessionId " + 
						"        FROM " + 
						"            acads.session_question_answer q " + 
						"        WHERE " + 
						"            s.id = q.sessionId) ";

				scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{
						facultyId,facultyId, facultyId, facultyId,
						facultyId,facultyId, facultyId, facultyId
						}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (DataAccessException e) {
				  
			}
			return scheduledSessionList;
		}
	  
	  @Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getCompletedSessionsOfFacultyForMbaWx(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new  ArrayList<SessionDayTimeAcadsBean>();
			try {
//				String sql = "SELECT s.* FROM acads.sessions s, exam.examorder eo "
//						+ " where  s.month = eo.acadmonth and s.year = eo.year and s.moduleid is not null"
//						+ " and eo.order in (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
//						+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )  and  EXISTS(SELECT q.* FROM acads.session_question_answer q where  s.id=q.sessionId)";
				String sql = " SELECT  " + 
						"    s.*, " + 
						"    (SELECT " + 
						"            COUNT(q.query) " + 
						"        FROM " + 
						"            acads.session_question_answer q " + 
						"        WHERE " + 
						"            s.id = q.sessionId "+
						"  AND ((s.facultyId = ? AND s.meetingKey = q.meetingKey) " + 
						"                OR (s.altFacultyId = ? AND s.altMeetingKey = q.meetingKey) " + 
						"                OR (s.altFacultyId2 = ? AND s.altMeetingKey2 = q.meetingKey) " + 
						"                OR (s.altFacultyId3 = ? AND s.altMeetingKey3 = q.meetingKey)) "+
						"  AND q.status = 'Open' AND LENGTH(q.query) > 10 ) AS count " + 
						"FROM " + 
						"    acads.sessions s " +
						"WHERE " + 
						"    s.moduleid IS NOT NULL " + 
						"   AND s.month = ? " + 
						"   AND s.year = ? " +  
						"        AND (s.facultyId =  ? " + 
						"        OR s.altFacultyId = ? " + 
						"        OR altFacultyId2 =  ? " + 
						"        OR altFacultyId3 =  ? ) " + 
						"        AND s.id IN (SELECT  " + 
						"            q.sessionId " + 
						"        FROM " + 
						"            acads.session_question_answer q " + 
						"        WHERE " + 
						"            s.id = q.sessionId) ";
				
				scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{
						facultyId,facultyId, facultyId, facultyId,
						CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR,
						facultyId,facultyId, facultyId, facultyId
				}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (DataAccessException e) {
				  
			}
			return scheduledSessionList;
		}

	  	@Transactional(readOnly = true)
		public Integer getMirrorSessionQnACount(String facultyId, String sessionId) {
			String sql = " SELECT " +
					"    (SELECT  " + 
					"            COUNT(q.query) " + 
					"        FROM " + 
					"            acads.session_question_answer q " + 
					"        WHERE " + 
					"            s.id = q.sessionId " + 
					"AND ((s.altFacultyId IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey = q.meetingKey) " + 
					" OR (s.altFacultyId2 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey2 = q.meetingKey) " + 
					" OR (s.altFacultyId3 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey3 = q.meetingKey)) " + 
					"AND q.status = 'Open' AND LENGTH(q.query) > 10) AS count " + 
					"FROM " + 
					"    acads.sessions s " + 
					"WHERE " + 
					"    s.facultyId = ? " + 
					"	AND s.id = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			Integer count = jdbcTemplate.queryForObject(sql, new Object[]{facultyId, sessionId}, Integer.class);
			return count;
		}
		
	  	@Transactional(readOnly = true)
		public List<SessionQueryAnswer> getMirrorSessionQnA(String session_id, String facultyId) {
				String sql = " SELECT  " + 
						"    q.*, st.* " + 
						"FROM " + 
						"	acads.sessions s " + 
						"     INNER JOIN " + 
						"   acads.session_question_answer q ON q.sessionId = s.id " + 
						"        INNER JOIN " + 
						"    exam.students st ON st.sapId = q.sapId " + 
						"WHERE " + 
						"    q.sessionId = ? " + 
						" AND s.facultyId = ? " + 
						" AND ((s.altFacultyId IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey = q.meetingKey) " + 
						" OR (s.altFacultyId2 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey2 = q.meetingKey) " + 
						" OR (s.altFacultyId3 IN ('NGASCE7777' , 'NGASCE9999') AND s.altMeetingKey3 = q.meetingKey)) " + 
						"    AND LENGTH(q.query) > 10 "	;
				
				jdbcTemplate = new JdbcTemplate(dataSource);
				List<SessionQueryAnswer> sessionQA = new ArrayList<SessionQueryAnswer>();
				sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,facultyId
				 }, new BeanPropertyRowMapper<SessionQueryAnswer>(SessionQueryAnswer.class));
				 
			return sessionQA;
		}
		
	  	@Transactional(readOnly = true)
		public List<SessionQueryAnswer> getSingleSessionsQA(String session_id, String facultyId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {
				
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = " SELECT " + 
						"    q.*, st.* " + 
						"FROM " + 
						"	acads.sessions s " + 
						"    INNER JOIN " + 
						"   acads.session_question_answer q ON q.sessionId = s.id " + 
						"        INNER JOIN " + 
						"    exam.students st ON st.sapId = q.sapId " + 
						"WHERE " + 
						"    q.sessionId = ? " + 
						"AND ((s.facultyId = ?  AND s.meetingKey = q.meetingKey) " + 
						" OR (s.altFacultyId = ? AND s.altMeetingKey = q.meetingKey) " + 
						" OR (s.altFacultyId2 = ? AND s.altMeetingKey2 = q.meetingKey) " + 
						" OR (s.altFacultyId3 = ? AND s.altMeetingKey3 = q.meetingKey) " + 
						"    )     " + 
						"    AND LENGTH(q.query) > 10 "	;
				sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,
						facultyId,facultyId, facultyId, facultyId
				}, new BeanPropertyRowMapper(SessionQueryAnswer.class));

			} catch (Exception e) {
				  
			}
			return sessionQA;
		}
	  	
	  	@Transactional(readOnly = false)
		public void updateAnswer(SessionQueryAnswer sessionQn) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "update acads.session_question_answer set "
						+ " answer= ?, "
						+ " status = 'Answered', "
						+ " isPublic = ? ," 
						+ " answeredByFacultyId = ?"
						+ " where id = ? ";
				
				jdbcTemplate.update(sql, new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, sessionQn.getAnswer());
						ps.setString(2, sessionQn.getIsPublic());
						ps.setString(3, sessionQn.getFacultyId());
						ps.setString(4, sessionQn.getId());
					}
				});
			} catch (DataAccessException e) {
				  
			}
			 
		}
	  	
	  	@Transactional(readOnly = true)
		public List<SessionQueryAnswer> getStudentQAforSession(String session_id,String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.sapId = ? and LENGTH(q.query) >10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, session_id);
						ps.setString(2, sapId);
					}
				}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
				  
			}
			return sessionQA;
		}

	  	@Transactional(readOnly = true)
		public List<SessionQueryAnswer> getPublicQAsforSession(String session_id, String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.isPublic = 'Y' and (q.answer IS NOT NULL AND q.answer != '')  and q.sapId != ? and LENGTH(q.query) > 10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						// TODO Auto-generated method stub
						ps.setString(1, session_id);
						ps.setString(2, sapId);
					}
				}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
				  
			}
			return sessionQA;
		}
	  	
	  	@Transactional(readOnly = true)
		public List<String> getConsumerProgramStructureIdsBySubjectAndProgramStructure(String subject,String programStructure) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select p_s_s.consumerProgramStructureId "
					+ "	  from exam.program_sem_subject p_s_s "
					+ "	  where  p_s_s.subject = ? ";
			
			if( !StringUtils.isBlank(programStructure)
					  && !"All".equalsIgnoreCase(programStructure)
			  ) {
				sql += "and  p_s_s.consumerProgramStructureId in "
						+ " ( "
						+ "		SELECT id FROM exam.consumer_program_structure "
						+ "		where "
						+ "		programStructureId in "
						+ "		( "
						+ "		SELECT id FROM exam.program_structure where program_structure = '"+programStructure+"'"
						+ "     ) "
						+ " ) ";
			}			   
			
			List<String> data = new ArrayList<>();
			try {
				data = (List<String>)jdbcTemplate.query(sql,new Object[] {subject}, new SingleColumnRowMapper(String.class));
			} catch (DataAccessException e) {
				  
			}
			 return data;
		}

	  	@Transactional(readOnly = false)
		public String batchInsertCententIdAndMasterKeyMappings(final List<ContentAcadsBean> cententIdAndMasterKeyMappings) {
			String sql = "INSERT INTO  acads . contentid_consumerprogramstructureid_mapping  " + 
					"( contentId, consumerProgramStructureId, programSemSubjectId, createdBy, createdDate ) " + 
					" VALUES " + 
					"( ?, ?, ?, ?, sysdate()) "
					+ " on duplicate key "
					+ " update "
					+ "	createdBy =?, createdDate = sysdate() ";

			jdbcTemplate = new JdbcTemplate(dataSource);
			String errorMessage = "";
			try {
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						ContentAcadsBean m = cententIdAndMasterKeyMappings.get(i);
						ps.setString(1, m.getId());
						ps.setString(2, m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						ps.setString(4, m.getCreatedBy());
						
						ps.setString(5, m.getCreatedBy());
					}
					public int getBatchSize() {
						return cententIdAndMasterKeyMappings.size();
					}
				});
			} catch (DataAccessException e) {
				  
				errorMessage += e.getMessage();
			}
			
			return errorMessage;
		}
		
	  
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(final long contentId,final List<ContentAcadsBean> consumerProgramStructureIds){
			
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentAcadsBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });

				return "";
			
		
		}

	  	@Transactional(readOnly = false)
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(final long contentId,final List<ContentAcadsBean> consumerProgramStructureIds){
			try {
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping_forLeads "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentAcadsBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
				return "";
			} catch (DataAccessException e) {
				  
				return "Error in batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads : "+e.getMessage();
			}
		}
		
	  	@Transactional(readOnly = false)
		public String batchInsertOfMakeContentLiveConfigs(final ContentAcadsBean searchBean,
				final List<String> consumerProgramStructureIds) {
			String sql = " INSERT INTO exam.content_live_settings " + 
					"(year, " + 
					"month, " + 
					"consumerProgramStructureId, " + 
					"createdDate, " + 
					"lastModifiedDate) " + 
					"VALUES " + 
					"(? , " + 
					" ? , " + 
					" ?  , " + 
					" sysdate() , " + 
					" sysdate() )  " 
					+ " on duplicate key update "
					+ "year=?, month=?, lastModifiedDate = sysdate() ";
			String errorMessage = "";
			try {
				int[] batchInsertOfMakeContentLiveConfigs = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {

						ps.setString(1,searchBean.getYear());
						ps.setString(2,searchBean.getMonth());
						ps.setString(3,consumerProgramStructureIds.get(i));
						
						//On Update
						ps.setString(4,searchBean.getYear());
						ps.setString(5,searchBean.getMonth());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
			} catch (DataAccessException e) {
				  
				return "Error in creating configurations , Error : "+e.getMessage();
			}
			return errorMessage;
		}
		
	  	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentLiveConfigList(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentAcadsBean> contentLiveConfigList = new ArrayList<>();
			
			String sql =  "select cls.year,cls.month,p.code as program,"
					+ "p_s.program_structure as programStructure,c_t.name as consumerType "
					+ "from exam.content_live_settings as cls "
					+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = cls.consumerProgramStructureId "
					+ "left join exam.program as p on p.id = c_p_s.programId "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId";
			
			try {
				contentLiveConfigList = (List<ContentAcadsBean>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ContentAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return contentLiveConfigList;  
			
		}
		
	  	@Transactional(readOnly = true)
		public Map<String,Integer> getContentIdNCountOfProgramsApplicableToMap(List<Integer> ids) {
			
			nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			List<ContentAcadsBean> list =  null;
			Map<String,Integer> map = new HashMap<String,Integer>();

			String sql = "SELECT  " + 
					"    count(c.id) as countOfProgramsApplicableTo ,c.id " + 
					"FROM " + 
					"    acads.content c, " + 
					"    acads.contentid_consumerprogramstructureid_mapping ccm " + 
					"WHERE " + 
					"    c.id = ccm.contentId " + 
					"    AND c.id  IN (:contentIds) " + 
					"GROUP BY c.id  " + 
					"ORDER BY c.id DESC ";
					try {
						MapSqlParameterSource parameters = new MapSqlParameterSource();
						parameters.addValue("contentIds", ids);
						
						
						list = nameJdbcTemplate.query(sql,parameters, new BeanPropertyRowMapper(ContentAcadsBean.class));
						
						for(ContentAcadsBean bean : list) {
							map.put(bean.getId(), bean.getCountOfProgramsApplicableTo());
						}
						
				
			} catch (Exception e) {
				  
			}
			return map;
		}

	  	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getProgramsListForCommonContent(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentAcadsBean> contentList=new ArrayList<>();
			StringBuffer sql= new StringBuffer("SELECT   " + 
					"    c.id,c.year,c.month,c.subject,"
					+ "  ct.id as consumerTypeId,ct.name as consumerType,"
					+ "  p.id as programId,p.code as program,"
					+ "  ps.id as programStructureId,ps.program_structure as programStructure,"
					+ "  ccm.consumerProgramStructureId,  " 
					+ "  ccm.programSemSubjectId  " + 
					"FROM  " + 
					"    acads.content c,  " + 
					"    acads.contentid_consumerprogramstructureid_mapping ccm,  " + 
					"    exam.consumer_type ct,  " + 
					"    exam.consumer_program_structure cps,  " + 
					"    exam.program p,  " + 
					"    exam.program_structure ps  " + 
					"      " + 
					"WHERE  " + 
					"    c.id = ccm.contentId  " + 
					"        AND ccm.consumerProgramStructureId = cps.id  " + 
					"        AND cps.consumerTypeId = ct.id  " + 
					"        AND cps.programId = p.id  " + 
					"        AND cps.programStructureId = ps.id  " + 
					"        AND c.id = ?  " + 
					"   ORDER BY c.id DESC");

			
				 contentList = (List<ContentAcadsBean>) jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {

						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,id);
						}}  , new BeanPropertyRowMapper(ContentAcadsBean.class));


			return contentList;
		}

	  	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentsListWithMasterKeyDetailsBySubject(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentAcadsBean> contentList=new ArrayList<>();
			String sql="SELECT   " + 
					"    c.*,"
					+ "  ct.id as consumerTypeId,ct.name as consumerType,"
					+ "  p.id as programId,p.code as program,"
					+ "  ps.id as programStructureId,ps.program_structure as programStructure,"
					+ "  ccm.consumerProgramStructureId  " + 
					"FROM  " + 
					"    acads.content c,  " + 
					"    acads.contentid_consumerprogramstructureid_mapping ccm,  " + 
					"    exam.consumer_type ct,  " + 
					"    exam.consumer_program_structure cps,  " + 
					"    exam.program p,  " + 
					"    exam.program_structure ps  " + 
					"      " + 
					"WHERE  " + 
					"    c.id = ccm.contentId  " + 
					"        AND ccm.consumerProgramStructureId = cps.id  " + 
					"        AND cps.consumerTypeId = ct.id  " + 
					"        AND cps.programId = p.id  " + 
					"        AND cps.programStructureId = ps.id  " + 
					"        AND c.subject = ?  " + 
					"   " + 
					"ORDER BY c.id DESC";
			
			try {
				 contentList = (List<ContentAcadsBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,subject);}}, new BeanPropertyRowMapper(ContentAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return contentList;
		}

	  
		public int deleteContentIdMasterkeyMappingsById(String id) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);

			//try {
				int deletedRows =  jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,id);
					}
					});
				return deletedRows;
			/*} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				  
				return 0;
			}*/

			
		}
	  	
	  	@Transactional(readOnly = false)
		public int deleteContentIdMasterkeyMappingsByIdNMasterKey(String id,String consumerProgramStructureId) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			//try {
				int deletedRows =  jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,id);
					preparedStatement.setString(2,consumerProgramStructureId);
					}});
				return deletedRows;
			/*} catch (DataAccessException e) {
				  
				return 0;
			}*/
			
		}
		
	  	
		public int getCountOfProgramsContentApplicableToById(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			StringBuffer sql = new StringBuffer();
			
			sql.append("SELECT   count(*)  FROM   acads.contentid_consumerprogramstructureid_mapping ccm ");
			sql.append(" WHERE   ccm.contentId =  ? ");
			
			int mappingCount = jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,id);
				}
				},new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
	                      return rs.getInt(1);
	                  }
					return 0;
				}
	            });
			return mappingCount;
	    	
		}
		
		//System generated learning resources (insert data into posts table)
		//start
	  	@Transactional(readOnly = false)
		public void insertResourcesPostTable(ContentAcadsBean bean,long id,String type,String subject,String year,String month) {
		PostsAcads posts = new PostsAcads();
			posts.setUserId("System");
			posts.setRole("System");
			posts.setType("Resource");
			posts.setSubject(subject);   
			posts.setFileName(bean.getName());
			posts.setContent(bean.getDescription());
			posts.setFilePath(bean.getPreviewPath());
			posts.setContentType(bean.getContentType());
			posts.setSession_plan_module_id(bean.getSessionPlanModuleId());
			posts.setReferenceId(id);
			
			posts.setAcadYear(CURRENT_ACAD_YEAR);
			posts.setAcadMonth(CURRENT_ACAD_MONTH);
			posts.setScheduledDate(bean.getActiveDate());
			
		
			try {
			
				if(DateTimeUtil.compareActiveDateWithCurrentDate(bean.getActiveDate())) 
					posts.setScheduleFlag("N");
				else
					posts.setScheduleFlag("Y");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
			}
			String extention =bean.getFilePath().substring(bean.getFilePath().lastIndexOf("."), bean.getFilePath().length());
			 switch (extention.toLowerCase()) {
			 case ".doc":
			 case ".docx":
				 posts.setFileType("Doc");
				 break;
				 
			 case ".pdf": 
				 posts.setFileType("PDF");
				 break;
				 
			 case ".ppt":
			 case ".pptx":
				 posts.setFileType("PPT");
				 break;
				 
			 case ".xls": 
			 case ".xlsx":
			 case ".csv":
				 posts.setFileType("Excel");
				 break;
			
			 default: 
				 break;
			 }
			List<Integer> timeBoundIds = getTimeBoundIdEMBABySessionPlan(bean.getSessionPlanModuleId());
			posts.setHashtags(subject+","+bean.getSessionPlanModuleName());
			
			if (timeBoundIds.size() != 0) {
				if(type=="insert") {
					posts.setCreatedBy(bean.getCreatedBy());
					posts.setLastModifiedBy(bean.getCreatedBy());		
					posts.setVisibility(1);
					
					for(Integer programSemSubjectIds : timeBoundIds) {
						Integer postsId = insertIntoPostsTableEMBAResource(posts,programSemSubjectIds);
						posts.setSubject_config_id(programSemSubjectIds);
						refreshRedis( posts);
						//insertIntoPostProgSemSubEMBAResource(programSemSubjectIds, postsId);	
					}
				}else {
					posts.setLastModifiedBy(bean.getLastModifiedBy());	
					updateResourcePostTable(posts);
					refreshRedis( posts);
				}
			}
		}
		
	  	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getconsumerProgramStructureIdList(String subject,String programStructure, Long moduleId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentAcadsBean> consumerProgramStructureIdList = new ArrayList<ContentAcadsBean>();
			
			try {
				
			String sql =" SELECT  " + 
						"    pss.id as programSemSubjectId, pss.consumerProgramStructureId " + 
						" FROM " + 
						"    exam.program_sem_subject pss " + 
						" WHERE " + 
						"    pss.subject = ? " ;
			
			if (moduleId != null && moduleId > 0) {
				sql = sql + " AND pss.studentType = 'TimeBound' ";
				
			}else {
				sql = sql + " AND (pss.studentType = 'Regular' OR pss.studentType is null) ";
			}
			
			if( !StringUtils.isBlank(programStructure) && !"All".equalsIgnoreCase(programStructure)){
				sql = sql +	" AND pss.consumerProgramStructureId IN (SELECT  " + 
							" 	id " + 
							" FROM " + 
							" 	exam.consumer_program_structure " + 
							" WHERE " + 
							" 	programStructureId IN (SELECT  " + 
							"   id " + 
							" FROM " + 
							"  	exam.program_structure " + 
							" WHERE " + 
							" 	program_structure = '"+ programStructure +"' ))";
				}
			
				consumerProgramStructureIdList = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return consumerProgramStructureIdList;
		}

	  	@Transactional(readOnly = true)
		public List<Integer> getTimeBoundIdEMBABySessionPlan(long sessionPlanId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT t.timeboundId FROM acads.sessionplanid_timeboundid_mapping t,acads.sessionplan_module m where t.sessionPlanId = m.sessionPlanId  and  m.id =? ";
			List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,new Object[] { sessionPlanId }, new SingleColumnRowMapper(Integer.class));
			return id;
		}

	  	@Transactional(readOnly = false)
		public void insertIntoPostProgSemSubEMBAResource(Integer programSemSubjectId, Integer postsId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
			jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

		}

	  			@Transactional(readOnly = false)
				public int insertIntoPostsTableEMBAResource(final PostsAcads bean,final int timeBoundId) {
	  				
					jdbcTemplate = new JdbcTemplate(dataSource);
					KeyHolder holder = new GeneratedKeyHolder();
			  
					final String sql = " INSERT INTO lti.post ( userId, role, type, content, fileName,subject_config_id, filePath,fileType, referenceId, "
							 + " session_plan_module_id, subject, contentType, visibility, acadYear, acadMonth,scheduledDate, scheduleFlag,hashtags, "
							 + " createdBy, createdDate, lastModifiedBy, lastModifiedDate ) "
							 + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) ";

					jdbcTemplate.update(new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

							ps.setString(1, bean.getUserId());
							ps.setString(2, bean.getRole());
							ps.setString(3, bean.getType());
							ps.setString(4, bean.getContent());
							ps.setString(5, bean.getFileName());
							ps.setInt(6, timeBoundId);
							ps.setString(7, bean.getFilePath());
							ps.setString(8, bean.getFileType());
							ps.setLong(9, bean.getReferenceId());
							ps.setLong(10, bean.getSession_plan_module_id());
							ps.setString(11, bean.getSubject());
							ps.setString(12, bean.getContentType());
							ps.setInt(13, bean.getVisibility());
							ps.setString(14, bean.getAcadYear());
							ps.setString(15, bean.getAcadMonth());
							ps.setString(16, bean.getScheduledDate());
							ps.setString(17, bean.getScheduleFlag());    
							ps.setString(18, bean.getHashtags()); 
							ps.setString(19, bean.getCreatedBy());
							ps.setString(20, bean.getLastModifiedBy());
							return ps;
						}
					}, holder);

					int postId = holder.getKey().intValue();

					return postId;
				}
				
	  			@Transactional(readOnly = false)
				public void updateResourcePostTable(final PostsAcads bean) {
					jdbcTemplate = new JdbcTemplate(dataSource);
				
					 String sql = "UPDATE lti.post SET "
							+ "userId = ?, "
							+ "role = ?, "
							+ "type = ?, "
							+ "content = ?, "
							+ "contentType = ?, "
							+ "fileName = ?, "
							+ "filePath = ?, "
							+ "lastModifiedBy = ?, "
							+ "lastModifiedDate = sysdate() "
							+ "WHERE referenceId = ? order by lastModifiedDate desc limit 1";

					jdbcTemplate.update(sql, new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,bean.getUserId());
							preparedStatement.setString(2,bean.getRole());
							preparedStatement.setString(3,bean.getType());
							preparedStatement.setString(4,bean.getContent());
							preparedStatement.setString(5,bean.getContentType());
							preparedStatement.setString(6,bean.getFileName());
							preparedStatement.setString(7,bean.getFilePath());
							preparedStatement.setString(8,bean.getLastModifiedBy());
							preparedStatement.setLong(9,bean.getReferenceId());
						}});
				}

		//delete post from post table

		@Transactional(readOnly = false)
		public int deleteContentFromPost(String id) {

			String sql = "Delete from lti.post where post_id = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);

			return jdbcTemplate.update(sql, new Object[] { 
					id
			});

		}
		
		
		//for inserting data into post table for MBA-WX
		//start
	  	//@Transactional(readOnly = false)
		public void insertRecordingsPostEMBA(VideoContentAcadsBean bean,int id,String type) {
	  		logger.debug("START");
	  		
			PostsAcads posts = new PostsAcads();

			posts.setAcadYear(bean.getYear());
			posts.setAcadMonth(bean.getMonth());
			posts.setSubject(bean.getSubject());
			posts.setUrl(bean.getVideoLink());
			posts.setFileName(bean.getFileName());
			posts.setContent(bean.getDescription());
			posts.setVideoLink(bean.getVideoLink());
			posts.setThumbnailUrl(bean.getThumbnailUrl());
			posts.setMobileUrlHd(bean.getMobileUrlHd());
			posts.setUserId(bean.getFacultyId());
			posts.setSessionDate(bean.getSessionDate());
			
			posts.setReferenceId(id);
			posts.setSubject(bean.getSubject().replace("_SesssionPlan_Video", ""));
			posts.setCreatedBy(bean.getCreatedBy());
			
			posts.setVisibility(1);
			posts.setScheduleFlag("N");
			posts.setRole("Faculty");
			posts.setType("SessionVideo");
			SessionPlanDAO sDao = (SessionPlanDAO) act.getBean("sessionPlanDAO");
			String moduleName = sDao.getSessionPlanModuleById(bean.getSessionPlanModuleId()).getTopic();
			posts.setHashtags(posts.getSubject()+","+moduleName);
			
			//List<Integer> timeBoundIds = getTimeBoundIdEMBA(bean.getSubject().replace("_SesssionPlan_Video", ""));
			if(type=="insert") {
				long timeBoundId = getTimeBoundIdByModuleId(bean.getSessionPlanModuleId());
				if (timeBoundId != 0) {
					posts.setSubject_config_id(timeBoundId);
					posts.setSession_plan_module_id(bean.getSessionPlanModuleId());
					logger.info("Inserting Post in post table for TimeboundID :"+timeBoundId);
					int post_id = insertIntoPostsTableEMBA(posts);
					//insert post into Redis
					posts.setPost_id(post_id+"");
					//insertToRedis(posts);  
					//refreshRedis(posts);
				}
			}else {
				try {
					posts.setLastModifiedBy(bean.getLastModifiedBy());
					logger.info("Upading Post details for ReferenceID :"+posts.getReferenceId());
					updateSessionPostTable(posts);
				} catch (Exception e) {
					logger.error("While updating the Post in post table - Error Message :"+e.getMessage());
				} 
			}
			//refresh cache on insert/update.
			String redisRefreshStatus = refreshRedis(posts);
			logger.info("After refreshing the redis cache - Call Back : "+redisRefreshStatus);
			logger.debug("END");
			
			
		//Commented Old Code for insert SessionRecording into post table
		/*		 
			if (timeBoundIds.size() != 0) {
			  if(type=="insert") {
				  posts.setCreatedBy(bean.getCreatedBy());
				  posts.setLastModifiedBy(bean.getCreatedBy()); 
				  posts.setVisibility(1);
		  
				  	for(Integer timeBoundId2 : timeBoundIds) { 
				  		insertIntoPostsTableEMBA(posts,timeBoundId2); //insertIntoPostProgSemSubEMBA(programSemSubjectIds, postsId);
				  	} 
				  }else { 
					  posts.setLastModifiedBy(bean.getLastModifiedBy());
					  updateSessionPostTable(posts); 
					}
		  		}
		 */ 

		}
/*		public String insertToRedis(Posts posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
		  	    String url = SERVER_PATH+"timeline/api/post/savePostInRedis";
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
				    
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				  
				return "Error IN rest call got "+e.getMessage();
			}
		}
	public String deleteFromRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
		  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				  
				return "Error IN rest call got "+e.getMessage();
			}
		}*/
		public String refreshRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
				String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				  
				return "Error IN rest call got "+e.getMessage();
			}
		}
		

		public String refreshRedis(PostsAcads posts) {
			logger.debug("START");
			

			RestTemplate restTemplate = new RestTemplate();
			try {
				logger.info("Refreshing redis for Post : "+posts);
				
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()+""));
				String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
				logger.info("Refreshing redis for Post URL : "+url);
				logger.info("Refreshing redis for TimeboundId : "+posts.getTimeboundId());
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

				HttpEntity<PostsAcads> entity = new HttpEntity<PostsAcads>(posts,headers);
				
				logger.debug("END");

				return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				logger.error("While refreshing the redis cache - Error Message :"+e.getMessage());
				return "Error IN rest call got "+e.getMessage();
			}
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getTimeBoundIdEMBA(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			//String sql = "SELECT id FROM exam.program_sem_subject where subject=? ";
			String sql =  " select id from lti.student_subject_config where prgm_sem_subj_id in "
						+ " (SELECT id FROM exam.program_sem_subject where subject= ? and studentType = 'TimeBound') "
						+ " and acadMonth = '" + CURRENT_ACAD_MONTH + "' and acadYear = "+ CURRENT_ACAD_YEAR +" ";
			
			List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,
					new Object[] { subject },
					new SingleColumnRowMapper(Integer.class));
			return id;
		}
				@Transactional(readOnly = false)
				public void insertIntoPostProgSemSubEMBA(Integer programSemSubjectId, Integer postsId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
					jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

				}
				
				//@Transactional(readOnly = false)
				public int insertIntoPostsTableEMBA(final PostsAcads bean) {
					logger.debug("START");
					logger.info("Post Bean Before insert into Post Table :"+bean);

					jdbcTemplate = new JdbcTemplate(dataSource);
					KeyHolder holder = new GeneratedKeyHolder();
			  
					final String sql =" INSERT INTO lti.post ( userId, role, type, content, fileName, "
							+ " videolink, thumbnailUrl, mobileUrlHd, sessionDate, subject_config_id, referenceId, "
							+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate, "
							+ " visibility, acadYear, acadMonth, session_plan_module_id,url,scheduledDate,scheduleFlag,hashtags,subject )"
							+ " values(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?,sysdate(),?,?,?)";   

					jdbcTemplate.update(new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

							ps.setString(1, bean.getUserId());
							ps.setString(2, bean.getRole());
							ps.setString(3, bean.getType());
							ps.setString(4, bean.getContent());
							ps.setString(5, bean.getFileName());
							ps.setString(6, bean.getVideoLink());
							ps.setString(7, bean.getThumbnailUrl());
							ps.setString(8, bean.getMobileUrlHd());
							ps.setString(9, bean.getSessionDate());
							ps.setLong(10, bean.getSubject_config_id());
							ps.setLong(11, bean.getReferenceId());
							ps.setString(12, bean.getCreatedBy());
							ps.setString(13, bean.getCreatedBy());
							ps.setInt(14, bean.getVisibility());
							ps.setString(15, bean.getAcadYear());
							ps.setString(16, bean.getAcadMonth());
							ps.setLong(17, bean.getSession_plan_module_id());
							ps.setString(18, bean.getUrl());
							ps.setString(19, bean.getScheduleFlag());
							ps.setString(20, bean.getHashtags());
							ps.setString(21, bean.getSubject());
							return ps;
						}
					}, holder);
					int postId = 0;
					try {
						postId = holder.getKey().intValue();
						logger.info("Post has been inserted successfully for timeboundId :"+bean.getSubject_config_id() +" with PostId :"+postId);
					} catch (Exception e) {
						logger.error("While inserting Post into post table - Error Message :"+e.getMessage());
					}
					
					logger.debug("END");
					return postId;
				}
				
				//@Transactional(readOnly = false)
				public void updateSessionPostTable(final PostsAcads bean) {
					logger.debug("START");
					

					jdbcTemplate = new JdbcTemplate(dataSource);
				
					 String sql = "UPDATE lti.post SET "
							+ "userId = ?, "
							+ "role = ?, "
							+ "type = ?, "
							+ "content = ?, "
							+ "fileName = ?, "
							+ "videolink = ?, "
							+ "thumbnailUrl = ?, "
							+ "mobileUrlHd = ?, "
							+ "sessionDate = ?, "
							+ "referenceId = ?, "
							+ "lastModifiedBy = ?, "
							+ "lastModifiedDate = sysdate(), "
							+ "acadYear = ?, "
							+ "acadMonth = ? "
							+ "WHERE referenceId = ? ";

					jdbcTemplate.update(sql, new Object[] { bean.getUserId(),
					bean.getRole(),
					bean.getType(),
					bean.getContent(),
					bean.getFileName(),
					bean.getVideoLink(),
					bean.getThumbnailUrl(),
					bean.getMobileUrlHd(),
					bean.getSessionDate(),
					bean.getReferenceId(),
					bean.getLastModifiedBy(),
					bean.getAcadYear(),
					bean.getAcadMonth(),
					bean.getReferenceId()
					
					});
					
					logger.info("Post has been updated successfully for referenceID :"+bean.getReferenceId());
					logger.debug("END");
				}
		//end Insert into post table
	//@Transactional(readOnly = true)
	public long getTimeBoundIdByModuleId(long moduleId) {
		logger.debug("START");
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT timeboundId FROM acads.sessionplanid_timeboundid_mapping "
					+ " where sessionPlanId = (SELECT sessionPlanId FROM acads.sessionplan_module where id = ? ) ";
		try {
			int id =  (int) jdbcTemplate.queryForObject(sql, new Object[] {moduleId}, new SingleColumnRowMapper(Integer.class));
			Long timeBoundId = Long.valueOf(id);
			
			logger.info("TimeboundID :"+ timeBoundId+" for ModuleId :"+moduleId);
			logger.debug("END");
			
			return timeBoundId;
		} catch (DataAccessException e) {
			logger.error("While fetching the timebound ID - Error Message :"+e.getMessage());
			return 0;
		}
	}
	
	@Transactional(readOnly = true)
	public ELearnResourcesAcadsBean isStukentApplicable(String userId) {
		ELearnResourcesAcadsBean eLearnResourcesBean = new ELearnResourcesAcadsBean();
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
		 eLearnResourcesBean =  (ELearnResourcesAcadsBean) jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper (ELearnResourcesAcadsBean.class) );
		
		 return eLearnResourcesBean;
		}catch(Exception e) {
			  
			return eLearnResourcesBean;
		}
	}
	
	@Transactional(readOnly = true)
	public ELearnResourcesAcadsBean isHarvardApplicable(String userId) {
		ELearnResourcesAcadsBean eLearnResourcesBean = new ELearnResourcesAcadsBean();
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
		 eLearnResourcesBean =  (ELearnResourcesAcadsBean) jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(ELearnResourcesAcadsBean.class) );
		 return eLearnResourcesBean;
		}catch(Exception e) {
			  
			return eLearnResourcesBean;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT id,name FROM exam.consumer_type order by id asc ";
		ArrayList<ConsumerProgramStructureAcads> consumerTypeList = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		return consumerTypeList;

	}
	
	@Transactional(readOnly = true)
	public PageAcads<ContentAcadsBean> getResourcesContent(int pageNo, int pageSize, ContentAcadsBean searchBean, String searchOption){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql = " SELECT c.*, " + 
					 " ccm.consumerProgramStructureId, " + 
					 " p.code AS program, cps.programId, " + 
					 " ps.program_structure AS programStructure, cps.programStructureId, " + 
					 " ct.name AS consumerType, cps.consumerTypeId " ;
		String countSql = " ";
		
		if (searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " ,COUNT(c.filepath) AS count, " + 
						" CONCAT(c.name, c.filepath, c.description) AS distinctFile, " + 
						" GROUP_CONCAT(DISTINCT ccm.consumerProgramStructureId) consumerProgramStructureId ";
			
			countSql = countSql + " SELECT count( distinct(concat(c.name,c.filepath,c.description)) )  ";
		}else {
			countSql = countSql + " SELECT count(*) " ;
		}
		
		if(ContentUtil.findContentHistoryValidDate(searchBean.getMonth(),searchBean.getYear()) > 0 )
		{
			sql = sql + " FROM acads.content c ";
			countSql = countSql + " FROM acads.content c " ;
		}
		else
		{
			sql = sql + " FROM acads.content_history c ";
			countSql = countSql + " FROM acads.content_history c " ;
		}
		
		
		sql = sql +	" LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " +
				    " LEFT JOIN exam.consumer_program_structure cps ON cps.id = ccm.consumerProgramStructureId " + 
				    " LEFT JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				    " LEFT JOIN exam.program p ON p.id = cps.programId " + 
				    " LEFT JOIN exam.program_structure ps ON ps.id = cps.programStructureId " + 
				    " LEFT JOIN exam.program_sem_subject pss ON pss.subject = c.subject AND pss.consumerProgramStructureId = cps.id " + 
				    " WHERE 1 = 1 ";
		
		
		countSql = countSql + " LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " + 
							  "	where 1 = 1 " ;
		
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and c.year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		
		if (searchBean.getMonth() != null && !("".equals(searchBean.getMonth()))) {
			sql = sql + " and c.month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}
		
		if (searchBean.getSubject() != null && !("".equals(searchBean.getSubject()))) {
			sql = sql + " and c.subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if (searchBean.getConsumerProgramStructureId() != null && !("".equals(searchBean.getConsumerProgramStructureId()))) {
			sql = sql + " and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			countSql = countSql + " and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
		}
		
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " group by distinctFile ";		
		}
		
		Object[] args = parameters.toArray();
		PaginationHelper<ContentAcadsBean> pagingHelper = new PaginationHelper<ContentAcadsBean>();
		PageAcads<ContentAcadsBean> page = pagingHelper.fetchPage(jdbcTemplate,countSql, sql, args, pageNo, pageSize,
														new BeanPropertyRowMapper(ContentAcadsBean.class));
		return page;
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ContentAcadsBean> getCommonGroupProgramList(ContentAcadsBean bean) {
		
		List<Integer> longIds = Stream.of(bean.getConsumerProgramStructureId().split("\\D+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
		
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		ArrayList<ContentAcadsBean> commonGroupList = new ArrayList<ContentAcadsBean>();
		try {
			String sql =" SELECT  " + 
						"   c.*, " + 
						"   p.code AS program, cps.programId, " + 
						"   ps.program_structure AS programStructure, cps.programStructureId, " + 
						"  	ct.name AS consumerType, cps.consumerTypeId, " + 
						" 	consumerProgramStructureId, " + 
						" 	ccm.programSemSubjectId " + 
						" FROM " + 
						"    acads.content c " + 
						"        LEFT JOIN " + 
						"    acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " + 
						"        LEFT JOIN " + 
						"    exam.consumer_program_structure AS cps ON cps.id = ccm.consumerProgramStructureId " + 
						"        LEFT JOIN " + 
						"    exam.program AS p ON p.id = cps.programId " + 
						"        LEFT JOIN " + 
						"    exam.program_structure AS ps ON ps.id = cps.programStructureId " + 
						"        LEFT JOIN " + 
						"    exam.consumer_type AS ct ON ct.id = cps.consumerTypeId " + 
						" WHERE 1 = 1 " + 
						"		AND c.year = :year " + 
						"       AND c.month = :month " + 
						"       AND subject = :subject " +
						"		AND filePath = :filePath " +
						"		AND consumerProgramStructureId in (:masterKeys)";
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("year", bean.getYear());
			parameters.addValue("month", bean.getMonth());
			parameters.addValue("subject", bean.getSubject());
			parameters.addValue("filePath", bean.getFilePath());
			parameters.addValue("masterKeys", longIds);
			
			commonGroupList = (ArrayList<ContentAcadsBean>) nameJdbcTemplate.query(sql,parameters ,
							  new BeanPropertyRowMapper(ContentAcadsBean.class));
		}
		catch(Exception e) {
			  
		}
		return commonGroupList;
	}

	@Transactional(readOnly = false)
	public void deleteContentById(String id) {
		String sql = " Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql,new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,id);
				
			}});
	}
	
	@Transactional(readOnly = false)
	public int deleteContentIdConsumerPrgmStrIdMapping(String contentId, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,contentId);
					preparedStatement.setString(2,consumerProgramStructureId);
				}});
		} catch (Exception e) {
			  
			return -1;
		}
		return row;
	}

	@Transactional(readOnly = true)
	public boolean ifStudentAlreadyRegisteredForNextSem(String sapid,ReRegistrationAcadsBean activeRegistration) {
		boolean registered = true;
		try {
			String sql =  " SELECT count(*) "
				+ " FROM  `exam`.`registration`"
				+ " where `sapid`=? and month=? and year=?   ";
			  int count = jdbcTemplate.queryForObject(sql,new Object[] {sapid,activeRegistration.getAcadMonth(),activeRegistration.getAcadYear()} ,Integer.class);
			  
			 String sql1 =  " SELECT count(*) "
					    	+ " FROM  exam.registration_staging_future_records"
					    	+ " where `sapid`=? and month=? and year=?   ";
			 int count1 = jdbcTemplate.queryForObject(sql,new Object[] {sapid,activeRegistration.getAcadMonth(),activeRegistration.getAcadYear()} ,Integer.class);
			if(count==0 && count1==0) {
				registered=false;
			}
		} catch (DataAccessException e) {
			  
		} 
		return registered;
 	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProgramStrutureList() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> programStrutureList = new ArrayList<String>();

		try {
			String sql = "SELECT program_structure FROM exam.program_structure ";
			programStrutureList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			  
		}
		return programStrutureList;
	}
	
	@Transactional(readOnly = true)
	public FacultyAcadsBean isFaculty(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? group by facultyId";
		FacultyAcadsBean faculty = (FacultyAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(FacultyAcadsBean.class));
		return faculty;
	}
	
	@Transactional(readOnly = true)
	public int getPssIdBySubject(String subject, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int pssId = 0;
		String sql = "SELECT id FROM exam.program_sem_subject WHERE subject = ? AND consumerProgramStructureId = ? " ;
		try {
			pssId = (int) jdbcTemplate.queryForObject(sql,new Object[] {subject, consumerProgramStructureId}, new SingleColumnRowMapper(Integer.class));
		} catch (Exception e) {
			  
		}
		return pssId;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getWaivedInSubjects(StudentAcadsBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,subject from exam.program_sem_subject where consumerProgramStructureId = ? and "
				+ " subject not in ( select if(subject = 'Business Communication and Etiquette','Business Communication',subject) as subject from exam.passfail where sapid in (?,?) and isPass = 'Y') and sem < ? ";
		return (ArrayList<ProgramSubjectMappingAcadsBean>) jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId(), student.getPreviousStudentId(), student.getSapid(), student.getSem()}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
	}

	public ArrayList<ProgramSubjectMappingAcadsBean> getAllPreviousSubjectsForSapid(StudentAcadsBean student) {
		// Check if this sapid is lateral as well.
		boolean studentLateral = "Y".equals(student.getIsLateral());		
		ArrayList<ProgramSubjectMappingAcadsBean> clearedSubjectsList = getWaivedInSubjects(student);
		if(studentLateral) {
			// If lateral, get the subjects cleared for this sapid and check if it was lateral; repeat
			StudentAcadsBean previousStudent = getSingleStudentsData(student.getPreviousStudentId());
			ArrayList<ProgramSubjectMappingAcadsBean> clearedSubjectsForPreviousStudentNumber = getAllPreviousSubjectsForSapid(previousStudent);
			clearedSubjectsList.addAll(clearedSubjectsForPreviousStudentNumber);
		}
		return clearedSubjectsList;
	}
	
	   //For Content History table
		@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentsForIdsInHistoryTable(ArrayList<String> contentToTransfer) {

			
			nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.content_history c where id in (:contentIds)";
			
		
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("contentIds", contentToTransfer);
			
			List<ContentAcadsBean> contents = nameJdbcTemplate.query(sql,parameters,new BeanPropertyRowMapper(ContentAcadsBean.class));
			
			return contents;
		}
		
		
	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getRecordingForLastCycleInHistoryTable(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.content_history c, exam.examorder eo where subject = ? "
					+ " and c.month = eo.acadMonth and c.year = eo.year "
					+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
					+ " and contentType = 'Session Recording' ";

			List<ContentAcadsBean> contents = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,subject);
				}}, new BeanPropertyRowMapper(ContentAcadsBean .class));
			
			return contents;
		}
		
		@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentFilesInHistoryTable(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "Select * from acads.content_history where subject = ? ";
			
			List<ContentAcadsBean> contentFiles = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,subject);
				}}, new BeanPropertyRowMapper(ContentAcadsBean.class));
			
			return contentFiles;
		}
		
		@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentsForMultipleSubjectsInHistoryTab(String subjectList) {
			
			nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			
			List<String> subject = Arrays.asList(subjectList);
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("subjectIds", subject);
			
			String sql = "SELECT * FROM acads.content_history where subject in (:subjectIds) ";
			List<ContentAcadsBean> contents = nameJdbcTemplate.query(sql, new BeanPropertyRowMapper(ContentAcadsBean.class));
			
			return contents;
		}
		
  
	
	//Added By Riya for content update using subjectcodeList
	
    @Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcads> getSubjectCodeLists(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<ConsumerProgramStructureAcads> subjectCode = new ArrayList<ConsumerProgramStructureAcads>();
		StringBuffer sql =  new StringBuffer("SELECT DISTINCT id as subjectCodeId ,subjectcode ,subjectname as subjectName FROM exam.mdm_subjectcode  where active = 'Y' and studentType = 'Regular' order by subjectname asc;");
		
	
			subjectCode = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		

		return subjectCode;  
		
	}
	
    @Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcads> getMasterKeyMapSubjectCode(){
		jdbcTemplate =  new JdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT DISTINCT     "
				+ "mscm.id as pssId,    "
				+ "p.code AS program,    "
				+ "ps.program_structure AS programStructure,    "
				+ "ct.name AS consumerType,    "
				+ "msc.subjectcode,   "
				+ "msc.subjectname AS subject    "
				+ "FROM     "
				+ " exam.consumer_program_structure cps     "
				+ "INNER JOIN     "
				+ "exam.program p ON cps.programId = p.id     "
				+ "INNER JOIN     "
				+ "exam.program_structure ps ON ps.id = cps.programStructureId     "
				+ "INNER JOIN     "
				+ "exam.consumer_type ct ON ct.id = cps.consumerTypeId     "
				+ "INNER JOIN     "
				+ "exam.mdm_subjectcode_mapping mscm ON cps.id = mscm.consumerProgramStructureId    "
				+ "INNER JOIN    "
				+ "exam.mdm_subjectcode msc ON mscm.subjectCodeId = msc.id   "
				+ " WHERE msc.active= 'Y'     "
				+ "  ORDER BY ct.name , ps.program_structure , p.code ");
		
		ArrayList<ConsumerProgramStructureAcads> masterKeywithCode =(ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		return masterKeywithCode;
	}
	
 
	public ArrayList<ContentAcadsBean> getMasterKeyForContentMapping(String subjectCodeId,String pssId)
	{
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<ContentAcadsBean>();

		StringBuffer sql = new StringBuffer("select consumerProgramStructureId,id as programSemSubjectId from exam.mdm_subjectcode_mapping where ");
		
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		
				
		try {
			if(!StringUtils.isBlank(pssId)) {
				
				List<Integer> longIds = Stream.of(pssId.split("\\D+"))
		                .map(Integer::parseInt) 
		                .collect(Collectors.toList());
				
				
				sql.append("id in (:pssIds)");
				parameters.addValue("pssIds", longIds);		
				}
			else {
				sql.append("subjectCodeId = :subjectCodeId ");
				parameters.addValue("subjectCodeId", subjectCodeId);		
			}
			
			consumerProgramStructureIds =(ArrayList<ContentAcadsBean>) nameJdbcTemplate.query( sql.toString(),parameters, new BeanPropertyRowMapper(ContentAcadsBean.class));
			
			
		}catch(Exception e)
		{
			  
		}
		return consumerProgramStructureIds;
	}
	

	//Get the subject name from pssId
    @Transactional(readOnly = true)
	public String getSubjectNameByPssId(String pssIds){
		nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		List<Integer> longIds = Stream.of(pssIds.split("\\D+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("pssId", longIds);
		
		try {
			StringBuffer sql = new StringBuffer(" SELECT ms.subjectname FROM exam.mdm_subjectcode_mapping msm " + 
							" INNER JOIN  exam.mdm_subjectcode ms ON msm.subjectCodeId = ms.id " + 
							" WHERE msm.id IN (:pssId) " + 
							" GROUP BY subjectname ");
			String subjectName = (String) nameJdbcTemplate.queryForObject(sql.toString(),parameters,String.class);
			return subjectName;
		} catch (Exception e) {
				return "";
		}
	}
	

	//get the subject name by subject code Id 
		@Transactional(readOnly = true)
		public String getSubjectNameBySubjectCodeId(final String subjectcodeId){

			jdbcTemplate = new JdbcTemplate(dataSource);
			
			return  jdbcTemplate.query(
			           "select subjectname  FROM exam.mdm_subjectcode WHERE id = ?",
			            new PreparedStatementSetter() {
			              public void setValues(PreparedStatement preparedStatement) throws
			                SQLException {
			                  preparedStatement.setString(1, subjectcodeId);
			              }
			            }, 
			            new ResultSetExtractor<String>() {
			              public String extractData(ResultSet resultSet) throws SQLException,
			                DataAccessException {
			                  if (resultSet.next()) {
			                      return resultSet.getString(1);
			                  }
			                  return null;
			              }
			            }
			        );
		}
		
		//get content by subjectcode Id
		@Transactional(readOnly = true)
		public List<ContentAcadsBean> getContentsBySubjectCodeId(String subjectCodeId, String month, String year)

		{
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ContentAcadsBean> contents = new ArrayList<ContentAcadsBean>();

			StringBuffer sql = new StringBuffer("SELECT scm.id as subjectCodeId,c.* FROM " + 
					"exam.mdm_subjectcode_mapping scm " + 
					"INNER JOIN " + 
					"acads.contentid_consumerprogramstructureid_mapping cpm on scm.id = cpm.programSemSubjectId  " + 
					"INNER JOIN " + 
					"acads.content c on cpm.contentId = c.id  " + 
					"WHERE scm.subjectCodeId = ? and c.year = ? and c.month = ?  GROUP BY (cpm.contentId) ORDER BY c.id DESC");
			
		
				contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,subjectCodeId);
						preparedStatement.setString(2,year);
						preparedStatement.setString(3,month);
				}},new BeanPropertyRowMapper(ContentAcadsBean.class));
				
				if(contents.size() == 0)
					contents = getContentsBySubjectCodeIdInHistoryTable(subjectCodeId,month,year);

			return contents;
			
		}
		

		
		/*
		 * Get the Mapping of (Masterkey,ProgramSemSubjectId) from Content Mapping table 
		 */

    	@Transactional(readOnly = true)
		public List<ContentAcadsBean> getMappingOfContentId(String id){
			

			List<ContentAcadsBean> contentList = new ArrayList<ContentAcadsBean>();

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT consumerProgramStructureId,programSemSubjectId FROM acads.contentid_consumerprogramstructureid_mapping WHERE contentId = ?";

			
			try {
			 contentList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,id);
				}},new BeanPropertyRowMapper(ContentAcadsBean.class));
			 
			 if(contentList.size() == 0)
					contentList = getMappingOfContentIdHistoryTable(id);
				

			}catch(Exception e)
			{
				  
			}
			
			return contentList;
		}
		
    	@Transactional(readOnly = true)
	    public List<ContentAcadsBean> getMappingOfContentIdHistoryTable(String id){

	       jdbcTemplate = new JdbcTemplate(dataSource);

	       String sql = "select consumerProgramStructureId,programSemSubjectId from acads.contentid_consumerprogramstructureid_mapping_history where contentId = ?";
	       List<ContentAcadsBean> contentList = jdbcTemplate.query(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,id);
				}},new BeanPropertyRowMapper(ContentAcadsBean.class));

	      return contentList;
	}
	    

	    //Get The Subject applicable for faculty using subjectcode
    	@Transactional(readOnly = true)
	    public ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodes(String userId,String examOrder)
	    {

	    	ArrayList<ConsumerProgramStructureAcads> subjectcodeList = new ArrayList<ConsumerProgramStructureAcads>();

	    	jdbcTemplate = new JdbcTemplate(dataSource);
	    	
	    	
	    	StringBuffer sql = new StringBuffer("select msc.id as subjectCodeId,subjectname,subjectcode,fc.month,fc.year from " 
	    			+ "exam.mdm_subjectcode as msc  "  
	    			+"INNER JOIN  " 
	    			+"acads.faculty_course as fc on msc.subjectname = fc.subject  " 
	    			+"INNER JOIN  " 
	    			+"exam.examorder eo on fc.month = eo.acadMonth and fc.year = eo.year  "  
	    			//+"and eo.order =  (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') "  
	    			+"and eo.order in  ("+examOrder+")" 
	    			+"where fc.facultyId = ? and active = 'Y'; ");

	    	try {
	    	subjectcodeList = (ArrayList<ConsumerProgramStructureAcads>)jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,userId);
				}},new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
	    	
	    	}catch(Exception e)
	    	{
	    		  
	    	}
	    	return subjectcodeList;
	    }
	    
	    //Update the contentId-MasterKey mapping table

	    public int updateTheMappingTable(String oldContentId,long newContentId,String masterKey)
	    {
	    	nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    	StringBuffer sql = new StringBuffer("UPDATE acads.contentid_consumerprogramstructureid_mapping SET contentId= :newContentId WHERE contentId= :oldContentId and consumerProgramStructureId in (:masterKey)");
	    	
	    	List<Integer> longIds = Stream.of(masterKey.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("newContentId", newContentId);
			parameters.addValue("oldContentId", oldContentId);
			parameters.addValue("masterKey", longIds);
			
			return nameJdbcTemplate.update(sql.toString(),parameters);	
	    
	    }
	    
	  //Copied for LiveSessionAccess
    	@Transactional(readOnly = true)
		public ArrayList<String> getPSSIds(String consumerProgramStructureId, String sem, ArrayList<String> waivedOffSubjects) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String subjectCommaSeparated = "''";
			String waivedOffSubjectSQL = "";
			if (waivedOffSubjects != null && waivedOffSubjects.size() > 0) {
				
				for (int i = 0; i < waivedOffSubjects.size(); i++) {
					if(i == 0){
						subjectCommaSeparated = "'" +waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
					}else{
						subjectCommaSeparated = subjectCommaSeparated + ", '" + waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
					}
				}
				waivedOffSubjectSQL = " AND subjectname NOT IN (" +subjectCommaSeparated+ ") ";
			}
			
			String sql =" SELECT scm.id FROM exam.mdm_subjectcode sc " + 
						" INNER JOIN exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectcodeId " + 
						" WHERE scm.consumerProgramStructureId = ? AND scm.sem = ? " + 
						  waivedOffSubjectSQL;
			ArrayList<String> pssIdList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[] {consumerProgramStructureId, sem}, new SingleColumnRowMapper(String.class));
			return pssIdList;
		}
		

		public ArrayList<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureAcads> programsStructureByConsumerType = new ArrayList<ConsumerProgramStructureAcads>();

			
			
			String sql =  "select p_s.program_structure as name,p_s.id as id "
					+ "from exam.consumer_program_structure as c_p_s "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "where c_p_s.consumerTypeId = ? group by p_s.id";
			

		
				programsStructureByConsumerType = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new PreparedStatementSetter() {

					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,consumerTypeId);
					}},new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				

			return programsStructureByConsumerType;  
			
		}
	    
	    public ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId){
			
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<ConsumerProgramStructureAcads> programsByConsumerType = new ArrayList<ConsumerProgramStructureAcads>();

			
			
			String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
					+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
					+ "where c_p_s.consumerTypeId = ? and p.id not in (52,64) group by p.id";

	
				programsByConsumerType = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,consumerTypeId);
					}},new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				

			return programsByConsumerType;  
			
		}
	    
	    public ArrayList<ConsumerProgramStructureAcads> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId){
			
			nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			ArrayList<ConsumerProgramStructureAcads> programsByConsumerType = new ArrayList<ConsumerProgramStructureAcads>();

		
			
			String sql =  "select  msc.subjectname as subjectName from exam.mdm_subjectcode as msc  " 
					+" inner join "
					+ "exam.mdm_subjectcode_mapping p_s_s on p_s_s.subjectCodeId = msc.id "
					+ "where p_s_s.consumerProgramStructureId in "
					+ "(select c_p_s.id as id from exam.consumer_program_structure as c_p_s "
					+ "where c_p_s.programId in (:programIds) "
					+ "and c_p_s.programStructureId in (:programStructureIds) "
					+ "and c_p_s.consumerTypeId in (:consumerTypeIds) ) and msc.active = 'Y' group by msc.subjectname";
			
	    	List<Integer> programIds = Stream.of(programId.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
	    	
	    	List<Integer> programStructureIds = Stream.of(programStructureId.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
			
	    	List<Integer> consumerTypeIds = Stream.of(consumerTypeId.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
	    	
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("programIds",programIds);
			parameters.addValue("programStructureIds",programStructureIds);
			parameters.addValue("consumerTypeIds",consumerTypeIds);
			
			

	
				programsByConsumerType = (ArrayList<ConsumerProgramStructureAcads>) nameJdbcTemplate.query(sql,parameters,
						new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				
				

			return programsByConsumerType;  
			
		}
	    
	    public ArrayList<ConsumerProgramStructureAcads> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId){
			
	    	nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			ArrayList<ConsumerProgramStructureAcads> programsByConsumerTypeAndPrgmStructure = new ArrayList<ConsumerProgramStructureAcads>();

			
			String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
					+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
					+ "where c_p_s.consumerTypeId = :consumerTypeId and c_p_s.programStructureId in (:programStructureIds) and p.id not in (52,64) group by p.id";
			
			
			List<Integer> programStructureIds = Stream.of(programStructureId.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("consumerTypeId", consumerTypeId);
			parameters.addValue("programStructureIds", programStructureIds);
			

		
				programsByConsumerTypeAndPrgmStructure = (ArrayList<ConsumerProgramStructureAcads>) nameJdbcTemplate.query(sql, parameters,
						new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				
		

			
			return programsByConsumerTypeAndPrgmStructure;  
			
		}
		

	    public PageAcads<ContentAcadsBean> getResourcesContentBySubjectCodeOrPssIdCurrent(int pageNo, int pageSize, ContentAcadsBean searchBean, String searchOption){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<Object> parameters = new ArrayList<Object>();
			StringBuffer sql = new StringBuffer();
			StringBuffer countSql = new StringBuffer();
			
			sql.append(" SELECT sc.subjectcode, c.*, " + 
						 " ccm.consumerProgramStructureId, " + 
						 " ccm.programSemSubjectId, " + 
						 " p.code AS program, cps.programId, " + 
						 " ps.program_structure AS programStructure, cps.programStructureId, " + 
						 " ct.name AS consumerType, cps.consumerTypeId ") ;
			
			
			if (searchOption.equalsIgnoreCase("distinct")) {
				sql.append(" ,COUNT(c.filepath) AS count, " + 
							" CONCAT(c.name, c.filepath, c.description) AS distinctFile, " + 
							" GROUP_CONCAT(DISTINCT ccm.consumerProgramStructureId) consumerProgramStructureId ");
				
				countSql.append(" SELECT count( distinct(concat(c.name,c.filepath,c.description)) )  ");
			}else {
				countSql.append(" SELECT count(*) ") ;
			}
			
			
			sql.append(" FROM acads.content c "+
						" INNER JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id ") ;
			countSql.append(" FROM acads.content c " +
						   " INNER JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id ");
			
				
			sql.append(" INNER JOIN exam.consumer_program_structure cps ON cps.id = ccm.consumerProgramStructureId " + 
					    " INNER JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
					    " INNER JOIN exam.program p ON p.id = cps.programId " + 
					    " INNER JOIN exam.program_structure ps ON ps.id = cps.programStructureId " + 
					    " INNER JOIN exam.mdm_subjectcode_mapping pss  ON  ccm.programSemSubjectId = pss.id  " + 
					    " INNER join exam.mdm_subjectcode sc on pss.subjectCodeId = sc.id  " + 
					    " WHERE 1 = 1 ");
			
			
			
			countSql.append(" INNER JOIN exam.mdm_subjectcode_mapping pss  ON  ccm.programSemSubjectId = pss.id  " +
					        " INNER join exam.mdm_subjectcode sc on pss.subjectCodeId = sc.id  " + 
							"	where 1 = 1 ") ;
			
			if(!(StringUtils.isBlank(searchBean.getYear()))) {
				sql.append(" and c.year = ? ");
				countSql.append(" and year = ? ");
				parameters.add(searchBean.getYear());
			}
			
			if (!(StringUtils.isBlank(searchBean.getMonth()))) {
				sql.append(" and c.month = ? ");
				countSql.append(" and month = ? ");
				parameters.add(searchBean.getMonth());
			}
			
			if (!(StringUtils.isBlank(searchBean.getSubjectCodeId()))) {
				sql.append(" and sc.id = ? ");
				countSql.append(" and sc.id = ? ");
				parameters.add(searchBean.getSubjectCodeId());
			}
			
			if(!(StringUtils.isBlank(searchBean.getSubject()))) {
				sql.append(" and sc.subjectName  = ? ");
				countSql.append(" and sc.subjectName  = ? ");
				parameters.add(searchBean.getSubject());
			}
			
			if (!(StringUtils.isBlank(searchBean.getConsumerProgramStructureId()))) {
				sql.append("  and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ");
				countSql.append("  and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ");
			}
			
			if(searchOption.equalsIgnoreCase("distinct")) {
				sql.append(" group by distinctFile ");		
			}
			
			sql.append(" order by c.id desc ");
			countSql.append(" order by c.id desc");
			
			Object[] args = parameters.toArray();
			
			PageAcads<ContentAcadsBean> page = new PageAcads<ContentAcadsBean>();
			PaginationHelper<ContentAcadsBean> pagingHelper = new PaginationHelper<ContentAcadsBean>();
			page = pagingHelper.fetchPage(jdbcTemplate,countSql.toString(), sql.toString(), args, pageNo, pageSize,
															new BeanPropertyRowMapper(ContentAcadsBean.class));
		
			return page;
			
		}
	    
	    public PageAcads<ContentAcadsBean> getResourcesContentBySubjectCodeOrPssIdHistory(int pageNo, int pageSize, ContentAcadsBean searchBean, String searchOption){
			
	    	jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<Object> parameters = new ArrayList<Object>();
			StringBuffer sql = new StringBuffer();
			StringBuffer countSql = new StringBuffer();
			
			sql.append(" SELECT sc.subjectcode, c.*, " + 
						 " ccm.consumerProgramStructureId, " + 
						 " p.code AS program, cps.programId, " + 
						 " ps.program_structure AS programStructure, cps.programStructureId, " + 
						 " ct.name AS consumerType, cps.consumerTypeId ") ;
			
			
			if (searchOption.equalsIgnoreCase("distinct")) {
				sql.append(" ,COUNT(c.filepath) AS count, " + 
							" CONCAT(c.name, c.filepath, c.description) AS distinctFile, " + 
							" GROUP_CONCAT(DISTINCT ccm.consumerProgramStructureId) consumerProgramStructureId ");
				
				countSql.append(" SELECT count( distinct(concat(c.name,c.filepath,c.description)) )  ");
			}else {
				countSql.append(" SELECT count(*) ") ;
			}
			
			
			sql.append(" FROM acads.content_history c "+
						" INNER JOIN acads.contentid_consumerprogramstructureid_mapping_history ccm ON ccm.contentId = c.id ") ;
			countSql.append(" FROM acads.content_history c " +
						   " INNER JOIN acads.contentid_consumerprogramstructureid_mapping_history ccm ON ccm.contentId = c.id ");
			
				
			sql.append(" INNER JOIN exam.consumer_program_structure cps ON cps.id = ccm.consumerProgramStructureId " + 
					    " INNER JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
					    " INNER JOIN exam.program p ON p.id = cps.programId " + 
					    " INNER JOIN exam.program_structure ps ON ps.id = cps.programStructureId " + 
					    " INNER JOIN exam.mdm_subjectcode_mapping pss  ON  ccm.programSemSubjectId = pss.id  " + 
					    " INNER join exam.mdm_subjectcode sc on pss.subjectCodeId = sc.id  " + 
					    " WHERE 1 = 1 ");
			
			
			
			countSql.append(" INNER JOIN exam.mdm_subjectcode_mapping pss  ON  ccm.programSemSubjectId = pss.id  " +
					        " INNER join exam.mdm_subjectcode sc on pss.subjectCodeId = sc.id  " + 
							"	where 1 = 1 ") ;
			
			if(!(StringUtils.isBlank(searchBean.getYear()))) {
				sql.append(" and c.year = ? ");
				countSql.append(" and year = ? ");
				parameters.add(searchBean.getYear());
			}
			
			if (!(StringUtils.isBlank(searchBean.getMonth()))) {
				sql.append(" and c.month = ? ");
				countSql.append(" and month = ? ");
				parameters.add(searchBean.getMonth());
			}
			
			if (!(StringUtils.isBlank(searchBean.getSubjectCodeId()))) {
				sql.append(" and sc.id = ? ");
				countSql.append(" and sc.id = ? ");
				parameters.add(searchBean.getSubjectCodeId());
			}
			
			if(!(StringUtils.isBlank(searchBean.getSubject()))) {
				sql.append(" and sc.subjectName  = ? ");
				countSql.append(" and sc.subjectName  = ? ");
				parameters.add(searchBean.getSubject());
			}
			
			if (!(StringUtils.isBlank(searchBean.getConsumerProgramStructureId()))) {
				sql.append("  and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ");
				countSql.append("  and ccm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ");
			}
			
			if(searchOption.equalsIgnoreCase("distinct")) {
				sql.append(" group by distinctFile ");		
			}
			
			sql.append(" order by c.id desc ");
			countSql.append(" order by c.id desc");
			
			Object[] args = parameters.toArray();
			
			PageAcads<ContentAcadsBean> page = new PageAcads<ContentAcadsBean>();
			PaginationHelper<ContentAcadsBean> pagingHelper = new PaginationHelper<ContentAcadsBean>();
		
			page = pagingHelper.fetchPage(jdbcTemplate,countSql.toString(), sql.toString(), args, pageNo, pageSize,
															new BeanPropertyRowMapper(ContentAcadsBean.class));
		
			return page;
			
		}
		
	  
	    
	    

	    public List<ContentAcadsBean> getRecordingForLastCycleBySubjectCode(String subjectCodeId,String month,String year) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentAcadsBean> contents = new ArrayList<ContentAcadsBean>();

			StringBuffer sql = new StringBuffer("select c.*,eo.* from  " + 
					"acads.content c   " + 
					"inner join   " + 
					"acads.contentid_consumerprogramstructureid_mapping ccm on ccm.contentId = c.id  " + 
					"inner join  " + 
					"exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year  " + 
					"and eo.order = (select (eo.order)-1 from exam.examorder eo where eo.acadMonth = ? and eo.year = ? )  " + 
					"inner join  " + 
					"exam.mdm_subjectcode_mapping mdm on mdm.id = ccm.programSemSubjectId  " + 
					"where c.contentType = 'Session Recording' and mdm.subjectCodeId = ?  group by (ccm.contentId) order by c.id desc;");


			
			 contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {

					
					preparedStatement.setString(1,month);
					preparedStatement.setString(2,year);
					preparedStatement.setString(3,subjectCodeId);
				}}, new BeanPropertyRowMapper(ContentAcadsBean.class));
		
			

			if(contents.size() == 0)
				contents = getRecordingForLastCycleBySubjectCodeInHistoryTable(subjectCodeId,month,year);
			
			return contents;
		}
	    
	    

	    public List<ContentAcadsBean> getRecordingForLastCycleBySubjectCodeInHistoryTable(String subjectCodeId,String month,String year) {


			jdbcTemplate = new JdbcTemplate(dataSource);
			StringBuffer sql = new StringBuffer("select c.*,eo.* from  " + 
					"acads.content_history c   " + 
					"inner join   " + 
					"acads.contentid_consumerprogramstructureid_mapping_history ccm on ccm.contentId = c.id  " + 
					"inner join  " + 
					"exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year  " + 
					"and eo.order = (select (eo.order)-1 from exam.examorder eo where eo.acadMonth = ? and eo.year = ? )  " + 
					"inner join  " + 
					"exam.mdm_subjectcode_mapping mdm on mdm.id = ccm.programSemSubjectId  " + 
					"where c.contentType = 'Session Recording' and mdm.subjectCodeId = ? group by (ccm.contentId) order by c.id desc;");

			
			List<ContentAcadsBean> contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,month);
					preparedStatement.setString(2,year);
					preparedStatement.setString(3,subjectCodeId);
				}}, new BeanPropertyRowMapper(ContentAcadsBean.class));

			
			return contents;
		}
		
	    
	    //Get the mapping count of contentId using masterKeys
	   	public int getNosMappingsByMasterKeys(final String contentId)
	    {
	    	jdbcTemplate = new JdbcTemplate(dataSource);
	    	
	    	StringBuffer sql  = new StringBuffer("select count(*) from acads.contentid_consumerprogramstructureid_mapping  " + 
	    			                             "where contentId = ?;");
	    	int mappingCount = jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,contentId);
				}
				},new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
	                      return rs.getInt(1);
	                  }
					return null;
				}
	            });
	    	if(mappingCount == 0)
	    		mappingCount = getNosMappingsByMasterKeysInHistoryTable(contentId);
	    	return mappingCount;
	    }
	    
	   	
	    public int getNosMappingsByMasterKeysInHistoryTable(String contentId)
	    {
	    	jdbcTemplate = new JdbcTemplate(dataSource);
	    	
	    	StringBuffer sql  = new StringBuffer("select count(*) from acads.contentid_consumerprogramstructureid_mapping_history  " + 
	    			                             "where contentId = ?;");
	    	int mappingCount = jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,contentId);
				}
				},new ResultSetExtractor<Integer>() {
				@Override
				public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
	                      return rs.getInt(1);
	                  }
					return null;
				}
	            });
	    	
	    	return mappingCount;
	    }
		
	    //Delete the  content history mapping table 
	    public int deleteContentIdAndMasterKeyMappingByIdAndMasterkeyInHistory(String contentId,String consumerProgramStructureId) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping_history "
						+ "where contentId = :contentId and consumerProgramStructureId in (:consumerProgramStructureIds )";
			nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			
			
			List<Integer> consumerProgramStructureIds = Stream.of(consumerProgramStructureId.split("\\D+"))
	                .map(Integer::parseInt)
	                .collect(Collectors.toList());
			
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("contentId", contentId);
			parameters.addValue("consumerProgramStructureIds", consumerProgramStructureIds);
			
			
			
			int deletedRows =  nameJdbcTemplate.update(sql, parameters);
			return deletedRows;
			
			}
			
		
	    public List<ContentAcadsBean> getMappingListWithSessionPlan(String id)
	    {

	    	List<ContentAcadsBean> mappingList = new ArrayList<ContentAcadsBean>();

	    	jdbcTemplate = new JdbcTemplate(dataSource);
	    	
	    	StringBuffer sql = new StringBuffer();
	    	
	    	sql.append("select scm.consumerProgramStructureId,scm.id as programSemSubjectId from  "
	    			+ "acads.sessionplan_module spm   "
	    			+ "INNER JOIN  "
	    			+ "acads.sessionplan sp on spm.sessionPlanId = sp.id  "
	    			+ "INNER JOIN  "
	    			+ "acads.sessionplanid_timeboundid_mapping sptm on sp.id = sptm.sessionPlanId  "
	    			+ "INNER JOIN  "
	    			+ "lti.student_subject_config ssc on sptm.timeboundId = ssc.id  "
	    			+ "INNER JOIN  "
	    			+ "exam.mdm_subjectcode_mapping scm on ssc.prgm_sem_subj_id = scm.id  "
	    			+ "where spm.id = ? ");
	    	
	    		
	    		try {
	    		mappingList = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,id);
				}},new BeanPropertyRowMapper(ContentAcadsBean.class));
	    	
	    		}catch(Exception e)
	    		{
	    			  
	    		}
	    	return mappingList;
	    }
		
	  
		//Get the No. of Mappings of that contentId in History table
	    public int getCountOfProgramsContentApplicableToByIdInHistory(String id){
				jdbcTemplate = new JdbcTemplate(dataSource);

				StringBuffer sql = new StringBuffer();
				
				sql.append(" SELECT  count(*)   FROM    acads.contentid_consumerprogramstructureid_mapping_history ccm ");
				sql.append(" WHERE   ccm.contentId =  ?");
				
				int mappingCount = jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,id);
					}
					},new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
		                      return rs.getInt(1);
		                  }
						return 0;
					}
		            });
					return mappingCount;

			}
			
	    	
	   
		

		//Get content by subjectcode Id in history table
				public List<ContentAcadsBean> getContentsBySubjectCodeIdInHistoryTable(String subjectCodeId,String month,String year)
				{
					jdbcTemplate = new JdbcTemplate(dataSource);
					StringBuffer sql = new StringBuffer("select scm.id as subjectCodeId,c.* from " + 
							"exam.mdm_subjectcode_mapping scm " + 
							"inner join " + 
							"acads.contentid_consumerprogramstructureid_mapping_history cpm on scm.id = cpm.programSemSubjectId  " + 
							"inner join " + 
							"acads.content_history c on cpm.contentId = c.id  " + 
							"where scm.subjectCodeId = ? and c.year = ? and c.month = ? group by (cpm.contentId)");
					
					List<ContentAcadsBean> contents = null;
					
					
					contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,subjectCodeId);
							preparedStatement.setString(2,year);
							preparedStatement.setString(3,month);
						}
						},new BeanPropertyRowMapper(ContentAcadsBean.class));
					
					return contents;
							
					
				}





				//Get The Active Contents
    			@Transactional(readOnly = true)
				public List<ContentAcadsBean> getAllActivePGContents() {

					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "SELECT c.*,ccm.consumerProgramStructureId,ccm.programSemSubjectId FROM acads.content c "
							+ "INNER JOIN  "
							+ "acads.contentid_consumerprogramstructureid_mapping ccm ON c.id = ccm.contentId  "
							+ "where c.sessionPlanModuleId = 0 or c.sessionPlanModuleId is null;";


					List<ContentAcadsBean> contents= new ArrayList<>();
				
						contents = jdbcTemplate.query(sql,new BeanPropertyRowMapper(ContentAcadsBean .class));
						if(contents != null) {
//							System.out.println("In getAllContents got contents : "+contents.size()); 
						}
					return contents;
				}
				
			
    			
				
				public int insertIntoContentTempTable(final ContentAcadsBean content) 
				{
					jdbcTemplate = new JdbcTemplate(dataSource);
					try {
						final StringBuffer sql =  new StringBuffer(" INSERT INTO acads.content_denormalized "+
										" (contentId,programSemSubjectId,consumerProgramStructureId,name,description,filePath,year,month,webFileurl,urlType,previewPath,acadDateFormat"
										+ ",subject,contentType,createdDate) "+
										" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
						
						final String contentId = content.getId();
						final String programSemSubjectId = content.getProgramSemSubjectId();
						final String consumerProgramStructureId=  content.getConsumerProgramStructureId();
						final String name = content.getName();
						final String description =  content.getDescription();
						final String filePath = content.getFilePath();
						final String year = content.getYear();
						final String month = content.getMonth();
						final String webFileurl = content.getWebFileurl();
						final String urlType = content.getUrlType();
						final String previewPath = content.getPreviewPath();
						final java.sql.Date acadDateFormat = DateTimeHelper.findAcadDate(content.getYear(), content.getMonth());
						final String subject = content.getSubject();
						final String contentType = content.getContentType();
						final String createdDate =content.getCreatedDate();
						
						
						PreparedStatementCreator psc = new PreparedStatementCreator() {
							
						
						public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement ps = con.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
							ps.setString(1, contentId);
							ps.setString(2, programSemSubjectId);
							ps.setString(3, consumerProgramStructureId);
							ps.setString(4, name);
							ps.setString(5, description);
							ps.setString(6, filePath);
							ps.setString(7, year);
							ps.setString(8, month);
							ps.setString(9, webFileurl);
							ps.setString(10, urlType);
							ps.setString(11, previewPath);
							ps.setDate(12, acadDateFormat);
							ps.setString(13, subject);
							ps.setString(14, contentType);
							ps.setString(15, createdDate);

							return ps;
						}
						};
						jdbcTemplate.update(psc);
				}catch(Exception e)
				{
					throw e;	
				}

					
//				
				return 1;


				}

				
		
				 //Get The Subject applicable for faculty using subjectcode
				@Transactional(readOnly = true)
			    public ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodesBySession(String userId,String month,String year,String hasModuleid)
			    {
			    	ArrayList<ConsumerProgramStructureAcads> subjectcodeList  = new ArrayList<ConsumerProgramStructureAcads>();
			    	StringBuffer sql = new StringBuffer();
			   
			    	
			    	nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			    	if(ContentUtil.findContentHistoryValidDate(month,year) > 0) {
				    	 sql = sql.append(" SELECT ssm.subjectCodeId,s.subject as subjectName,sc.subjectcode,s.month,s.year FROM "
				    			+ " acads.sessions s  "  
				    			+"  INNER JOIN  " 
				    			+"  acads.session_subject_mapping ssm ON s.id = ssm.sessionId  " 
				    			+"  INNER JOIN "
				    			+"  exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id "
				    			+"  WHERE (s.facultyId = :userId or altFacultyId = :userId or altFacultyId2 = :userId "
				    			+" or altFacultyId3 = :userId ) AND s.month = :month AND s.year = :year  ");
			    	}
			    	else {
			    		 sql = sql.append(" SELECT ssm.subjectCodeId,s.subject as subjectName,sc.subjectcode,s.month,s.year FROM "
					    			+ " acads.sessions_history s  "  
					    			+"  INNER JOIN  " 
					    			+"  acads.session_subject_mapping_history ssm ON s.id = ssm.sessionId  " 
					    			+"  INNER JOIN "
					    			+"  exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id "
					    			+"  WHERE (s.facultyId = :userId or altFacultyId = :userId or altFacultyId2 = :userId "
					    			+ " or altFacultyId3 = :userId )   AND s.month = :month AND s.year = :year  ");

			    	}
			    	
			 
			    		
			    		
			    		MapSqlParameterSource parameters = new MapSqlParameterSource();
			    		parameters.addValue("userId", userId);
			    		parameters.addValue("userId", userId);
			    		parameters.addValue("userId", userId);
			    		parameters.addValue("userId", userId);
			    		
			    		parameters.addValue("month", month);
			    		parameters.addValue("year", year);
			    		
			    		if(hasModuleid.equals("Y")) {
			    			sql.append(" AND hasModuleId = :hasModuleId group by ssm.subjectCodeId");
				    	}else {
				    		sql.append(" AND (hasModuleId = :hasModuleId OR hasModuleId is null ) group by ssm.subjectCodeId ");
				    	} 
			    		
			    		parameters.addValue("hasModuleId", hasModuleid);
			    		

			    	subjectcodeList = (ArrayList<ConsumerProgramStructureAcads>) nameJdbcTemplate.query(sql.toString(),parameters,new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

			    	return subjectcodeList;
			    }

				@Transactional(readOnly = true)
				public boolean getQueryOccurenceForSapIdInMeeting(SessionDayTimeAcadsBean sessionQA) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					int queryCount=0;
					String sql = "SELECT count(*) FROM acads.session_question_answer where meetingKey=? and sapId=? and query=? " ;
					try {
						queryCount =(int) jdbcTemplate.queryForObject(sql,
						new Object[] {sessionQA.getMeetingKey(),sessionQA.getSapId(),sessionQA.getQuestion()}
						, new SingleColumnRowMapper(Integer.class));
						return queryCount > 0;
					} catch (Exception e) {
						  
						return false;
					}
				}
				
				@Transactional(readOnly = false)
				public int insertAdhocDocument(String file_path, String userId, String title, Logger logger) {
					ArrayList<String> subjectList = null;
					int count=0;
						
						jdbcTemplate = new JdbcTemplate(dataSource);
						String sql = "insert into acads.academics_upload(title,webFileurl,createdBy,createdDate) values(?,?,?, current_timestamp())";
						count = jdbcTemplate.update(sql, new PreparedStatementSetter() {
							
							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								// TODO Auto-generated method stub
								ps.setString(1, title);
								ps.setString(2, file_path);
								ps.setString(3, userId);
							}
						});
						return count;
				}
				
				@Transactional(readOnly = true)
				public ArrayList<ContentAcadsBean> getAllUploadedDocumentsURL(String userId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "select * from acads.academics_upload where createdBy=? ORDER BY createdDate DESC";


					ArrayList<ContentAcadsBean> urlList = (ArrayList<ContentAcadsBean>)jdbcTemplate.query(sql, new PreparedStatementSetter() {
												
												@Override
												public void setValues(PreparedStatement ps) throws SQLException {
													// TODO Auto-generated method stub
													ps.setString(1, userId);
												}
											},new BeanPropertyRowMapper(ContentAcadsBean.class));

					return urlList;
				}
				
				@Transactional(readOnly = false)
				public boolean deletAdhocFile(String id, Logger logger) {
					boolean flag=false;
					String sql = "Delete from acads.academics_upload where id = ?";
					jdbcTemplate = new JdbcTemplate(dataSource);
					try {
						int count=jdbcTemplate.update(sql, new PreparedStatementSetter() {
							
							@Override
							public void setValues(PreparedStatement ps) throws SQLException {
								// TODO Auto-generated method stub
								ps.setString(1, id);
							}
						});
						
						if(count>0) {
							flag=true;
						}
					}catch (Exception e) {
							// TODO: handle exception
							  
							StringWriter errors = new StringWriter();
				            e.printStackTrace(new PrintWriter(errors));
				            logger.info("Error in Getting data from api (uploadFile) "+errors.toString());
						}
						return flag;
				}
				
				@Transactional(readOnly = true)
				public boolean checkAdhocFileTitle(String title) {
					boolean flag=false;
					String sql = "Select count(*) from acads.academics_upload where title = ?";
					jdbcTemplate = new JdbcTemplate(dataSource);
						int count=jdbcTemplate.queryForObject(sql, new Object[] {title}, Integer.class);
						
						if(count<=0) {
							flag=true;
						}else {
							DuplicateKeyException duplicatException= new DuplicateKeyException("Duplicate Title value!");
							throw duplicatException;
						}
						return flag;
				}
				
				@Transactional(readOnly = true)
				public String getAdhocFilePath(String id) {
					boolean flag=false;
					String sql = "Select webFileurl from acads.academics_upload where id = ?";
					jdbcTemplate = new JdbcTemplate(dataSource);
						String filePath=jdbcTemplate.queryForObject(sql, new Object[] {id}, String.class);
						return filePath;
						
				}

				
				//Get subject from faculty course mapping table
				@Transactional(readOnly = true)
			    public ArrayList<ConsumerProgramStructureAcads> getFacultySubjectsCodesFromCMapping(String userId,String month,String year,String studentType)
			    {
			    	ArrayList<ConsumerProgramStructureAcads> subjectcodeList = new ArrayList<ConsumerProgramStructureAcads>();
			    	
			    	jdbcTemplate = new JdbcTemplate(dataSource);
			    	StringBuffer sql = new StringBuffer("select msc.id as subjectCodeId,subjectname,msc.subjectcode,fc.month,fc.year from " 
			    			+ "exam.mdm_subjectcode as msc  "  
			    			+"inner join  " 
			    			+"acads.faculty_course as fc on msc.subjectname = fc.subject  AND fc.subjectcode = msc.subjectcode "
			    			+"where  msc.active = 'Y' and fc.facultyId = ? and fc.month = ? and fc.year = ?  and studentType = ? ; "	);

			    	
			    	subjectcodeList = (ArrayList<ConsumerProgramStructureAcads>)jdbcTemplate.query(sql.toString(),new Object[]{userId,month,year,studentType},new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
			    	
			    	

			    	return subjectcodeList;
			    }

			    
			    
			  //Get The Active Contents not present in content denormalized table
				@Transactional(readOnly = false)
				public List<ContentAcadsBean> getAllActivePGContentsX() {

					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "SELECT c.*,ccm.consumerProgramStructureId,ccm.programSemSubjectId FROM acads.content c "
							+ "INNER JOIN  "
							+ "acads.contentid_consumerprogramstructureid_mapping ccm ON c.id = ccm.contentId  "
							+ "where (c.sessionPlanModuleId = 0 or c.sessionPlanModuleId is null) and c.id not in (select contentId from acads.content_denormalized group by contentId);";

					List<ContentAcadsBean> contents= new ArrayList<>();
					contents = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ContentAcadsBean .class));
					return contents;
				}
				
				//Get TimeBound Subject List
				
				@Transactional(readOnly = true)
				public List<ConsumerProgramStructureAcads> getConsumerProgramStructureData(){
					
					List<ConsumerProgramStructureAcads> consumerProgramStructureList = new ArrayList<ConsumerProgramStructureAcads>();
					jdbcTemplate = new JdbcTemplate(dataSource);
					
					 
					String sql="SELECT " + 
							"    cps.id," + 
							"    p.code AS program," + 
							"    ps.program_structure AS programStructure," + 
							"    ct.name AS consumerType " + 
							"FROM " + 
							"    exam.consumer_program_structure cps " + 
							"        INNER JOIN " + 
							"    exam.program p ON cps.programId = p.id " + 
							"        INNER JOIN " + 
							"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
							"        INNER JOIN " + 
							"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
							"ORDER BY ct.name , ps.program_structure , p.code  ";			
					
					
					try {
						consumerProgramStructureList = (List<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
					 }
					 catch(Exception e){
						   
						
					 }
					return consumerProgramStructureList;
				}

				@Transactional(readOnly = true)
				public ArrayList<ConsumerProgramStructureAcads> getTimeBoundSubjectCodeLists(){
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					ArrayList<ConsumerProgramStructureAcads> subjectCode = new ArrayList<ConsumerProgramStructureAcads>();
					StringBuffer sql =  new StringBuffer("SELECT sc.subjectcode,subjectname,group_concat(scm.id) As programSemSubjectId FROM exam.mdm_subjectcode sc "  
										+"  INNER JOIN " 
										+"  exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId  "  
										+"  where studentType = 'TimeBound' GROUP BY sc.id ");
					
					
						subjectCode = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
						
					
					return subjectCode;  
					
				}
				
				
				
				
				//Get Content List For Particular Timebound Subject 
				@Transactional(readOnly = true)
				public List<SearchTimeBoundContent> getTimeBoundContentPageCurrent(String year,String month,String batchId,String programSemSubjectId,String facultyId,String date) {
					
					
					nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

					MapSqlParameterSource parameters = new MapSqlParameterSource();
					
					StringBuffer sql = new StringBuffer();
					
					
					List<SearchTimeBoundContent> contentList= new ArrayList<SearchTimeBoundContent>();
					
					
					sql.append(" SELECT s.id as sessionId,s.month,s.year,s.date,s.startTime,s.day,s.subject,sc.subjectcode,s.sessionName,  "	
								+ " s.facultyId,s.facultyLocation,s.altFacultyId, "
								+ " s.track,s.moduleid, " 
								+"  c.createdDate,c.lastModifiedDate,DATEDIFF(c.lastModifiedDate,STR_TO_DATE(concat(date,' ',startTime), \"%Y-%c-%d %H:%i:%s \")) as delayDays"
								+ " ,c.name as contentName  " 
								+"   FROM "
								+"  exam.batch b  "  
								+"	INNER JOIN   " 
								+"	lti.student_subject_config ssc ON ssc.batchId = b.id  " 
								+"	INNER JOIN  " 
								+" 	acads.sessionplanid_timeboundid_mapping sstm on ssc.id = sstm.timeboundId" 
								+" 	INNER JOIN   " 
								+" 	acads.sessionplan_module sm on sstm.sessionPlanId = sm.sessionPlanId  "
								+"	INNER JOIN      " 
								+"	exam.mdm_subjectcode sc   "
								+ " INNER JOIN  ");
						
					sql.append(" acads.session_subject_mapping ssm ON sc.id = ssm.subjectCodeId " + 
								"   INNER JOIN  " + 
								"	acads.sessions s ON ssm.sessionId = s.id AND sm.id = s.moduleid" + 
								"	INNER JOIN  " + 
								"	acads.content c ON s.moduleid = c.sessionPlanModuleId  WHERE 1=1  ");
						
						
						
							
					if(!(StringUtils.isBlank(facultyId)) && !("No".equals(facultyId))){
						sql.append(" AND (s.facultyId IN ("+facultyId+") OR s.altFacultyId IN ("+facultyId+") OR s.altFacultyId2 IN ("+facultyId+")  OR s.altFacultyId3 IN ("+facultyId+") ) ");
					
					}
				

					if(!(StringUtils.isBlank(year))){
						sql.append(" AND s.year = :year ");
						parameters.addValue("year",year);
					}
					if(!(StringUtils.isBlank(month))){
						sql.append(" AND s.month = :month ");
						parameters.addValue("month",month);
					}
					if(!(StringUtils.isBlank(date))){
						sql.append(" AND s.date = :date ");
						parameters.addValue("date",date);
					}
				
				
					if(!(StringUtils.isBlank(programSemSubjectId))){
						List<Integer> pssIds = Stream.of(programSemSubjectId.split("\\D+")).map(Integer::parseInt).collect(Collectors.toList());
						sql.append(" AND ssm.program_sem_subject_id IN (:pssIds) ");
						 parameters.addValue("pssIds",pssIds);
					}
					
				
	

					if(!(StringUtils.isBlank(batchId)) && !("No".equals(batchId))){
						List<Integer> batchIds = Stream.of(batchId.split("\\D+")).map(Integer::parseInt).collect(Collectors.toList());
						sql.append(" AND b.id IN (:batchIds) ");
						parameters.addValue("batchIds",batchIds);
					}
			
					sql.append(" GROUP BY c.id ORDER BY s.date, s.startTime asc");
					

					
				
					
					
					
					contentList = (ArrayList<SearchTimeBoundContent>) nameJdbcTemplate.query(sql.toString(),parameters,new BeanPropertyRowMapper(SearchTimeBoundContent.class));
		    	
				
				
					
					return contentList;
				}
				
				@Transactional(readOnly = true)
				public List<SearchTimeBoundContent> getTimeBoundContentPageHistory(String year,String month,String batchId,String programSemSubjectId,String facultyId,String date) {
					
					
					nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

					MapSqlParameterSource parameters = new MapSqlParameterSource();
					
					StringBuffer sql = new StringBuffer();
					
					
					List<SearchTimeBoundContent> contentList= new ArrayList<SearchTimeBoundContent>();
					
					
					sql.append(" SELECT s.id as sessionId,s.month,s.year,s.date,s.startTime,s.day,s.subject,sc.subjectcode,s.sessionName,  "	
								+ " s.facultyId,s.facultyLocation,s.altFacultyId, "
								+ " s.track,s.moduleid, " 
								+"  c.createdDate,c.lastModifiedDate,DATEDIFF(c.lastModifiedDate,STR_TO_DATE(concat(date,' ',startTime), \"%Y-%c-%d %H:%i:%s \")) as delayDays"
								+ " ,c.name as contentName  " 
								+"   FROM "
								+"  exam.batch b  "  
								+"	INNER JOIN   " 
								+"	lti.student_subject_config ssc ON ssc.batchId = b.id  " 
								+"	INNER JOIN  " 
								+" 	acads.sessionplanid_timeboundid_mapping sstm on ssc.id = sstm.timeboundId" 
								+" 	INNER JOIN   " 
								+" 	acads.sessionplan_module sm on sstm.sessionPlanId = sm.sessionPlanId  "
								+"	INNER JOIN      " 
								+"	exam.mdm_subjectcode sc   "
								+ " INNER JOIN  ");
						
				
					sql.append(" acads.session_subject_mapping_history ssm ON sc.id = ssm.subjectCodeId " + 
								"   INNER JOIN  " + 
								"	acads.sessions_history s ON ssm.sessionId = s.id AND sm.id = s.moduleid" + 
								"	INNER JOIN  " + 
								"	acads.content_history c ON s.moduleid = c.sessionPlanModuleId  WHERE 1=1 ");
					
					
				
					
					if(!(StringUtils.isBlank(facultyId)) && !("No".equals(facultyId))){
						sql.append(" AND (s.facultyId IN ("+facultyId+") OR s.altFacultyId IN ("+facultyId+") OR s.altFacultyId2 IN ("+facultyId+")  OR s.altFacultyId3 IN ("+facultyId+") ) ");
					
					}
				

					if(!(StringUtils.isBlank(year))){
						sql.append(" AND s.year = :year ");
						parameters.addValue("year",year);
					}
					if(!(StringUtils.isBlank(month))){
						sql.append(" AND s.month = :month ");
						parameters.addValue("month",month);
					}
					if(!(StringUtils.isBlank(date))){
						sql.append(" AND s.date = :date ");
						parameters.addValue("date",date);
					}
				
				
					if(!(StringUtils.isBlank(programSemSubjectId))){
						List<Integer> pssIds = Stream.of(programSemSubjectId.split("\\D+")).map(Integer::parseInt).collect(Collectors.toList());
						sql.append(" AND ssm.program_sem_subject_id IN (:pssIds) ");
						 parameters.addValue("pssIds",pssIds);
					}
					
				
	

					if(!(StringUtils.isBlank(batchId)) && !("No".equals(batchId))){
						List<Integer> batchIds = Stream.of(batchId.split("\\D+")).map(Integer::parseInt).collect(Collectors.toList());
						sql.append(" AND b.id IN (:batchIds) ");
						parameters.addValue("batchIds",batchIds);
					}
			
					sql.append(" GROUP BY c.id ORDER BY s.date, s.startTime asc");
					

					
				
					
					
					
					contentList = (ArrayList<SearchTimeBoundContent>) nameJdbcTemplate.query(sql.toString(),parameters,new BeanPropertyRowMapper(SearchTimeBoundContent.class));
		    	
				
				
					
					return contentList;
				}
				
				@Transactional(readOnly = true)
				public List<SearchTimeBoundContent> getbatchDetails(String year,String month,String programSemSubjectId)
				{
					nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

					MapSqlParameterSource parameters = new MapSqlParameterSource();
					
					StringBuffer sql = new StringBuffer();
					
					
					List<SearchTimeBoundContent> batchList= new ArrayList<SearchTimeBoundContent>();
				
						sql.append("    SELECT b.name AS batchName ,b.id AS batchId  FROM  " + 
									" 	lti.student_subject_config ssc  " + 
									"	INNER JOIN  " + 
									"	exam.batch b ON ssc.batchId = b.id  " + 
									"	WHERE ssc.prgm_sem_subj_id IN (:pssIds) ");
						
						
						if(!(StringUtils.isBlank(year))){
							sql.append(" AND b.acadYear = :year ");
							parameters.addValue("year",year);
						}
						if(!(StringUtils.isBlank(month))){
							sql.append(" AND b.acadMonth = :month ");
							parameters.addValue("month",month);
						}
						
						
						
						   List<String> pssIds	= Arrays.asList(programSemSubjectId);
						   parameters.addValue("pssIds",pssIds);
						   batchList = (ArrayList<SearchTimeBoundContent>) nameJdbcTemplate.query(sql.toString(),parameters,new BeanPropertyRowMapper(SearchTimeBoundContent.class));
						
						   
					
					return batchList;
				}
				
				@Transactional(readOnly = true)
				public List<String> getFacultyIdsByPssIdsCurrent(String year,String month,String programSemSubjectId)
				{
					nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					
					MapSqlParameterSource parameters = new MapSqlParameterSource();
					
					StringBuffer sql = new StringBuffer();
				
					List<String> facultyIds= new ArrayList<String>();
					
							
					sql.append("	SELECT distinct(s.facultyId) FROM ");
					sql.append(" 	acads.session_subject_mapping ssm "); 
					sql.append("   	INNER JOIN  ");
					sql.append("	acads.sessions s ON ssm.sessionId = s.id ");
					sql.append("	WHERE  ssm.program_sem_subject_id in (:pssIds) ");
						
					
						
						List<String> pssIds	= Arrays.asList(programSemSubjectId);
						   parameters.addValue("pssIds",pssIds); 
						   
						
						if(!(StringUtils.isBlank(year))) {
							
							sql.append(" AND s.year = :year ");
							parameters.addValue("year",year);
						}
						if(!(StringUtils.isBlank(month))){
							sql.append(" AND s.month = :month ");
							parameters.addValue("month",month);
						}
					
						
						

						   
						facultyIds = (ArrayList<String>) nameJdbcTemplate.queryForList(sql.toString(),parameters,String.class);
						
					
					return facultyIds;
				}
				
				@Transactional(readOnly = true)
				public List<String> getFacultyIdsByPssIdsHistory(String year,String month,String programSemSubjectId)
				{
					nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					
					MapSqlParameterSource parameters = new MapSqlParameterSource();
					
					StringBuffer sql = new StringBuffer();
					
					List<String> facultyIds= new ArrayList<String>();
					
					
					sql.append("	SELECT distinct(s.facultyId) FROM ");
					sql.append(" 	acads.session_subject_mapping_history ssm "); 
					sql.append("   	INNER JOIN  ");
					sql.append("	acads.sessions_history s ON ssm.sessionId = s.id ");
					sql.append("	WHERE  ssm.program_sem_subject_id in (:pssIds) ");
						
						List<String> pssIds	= Arrays.asList(programSemSubjectId);
						   parameters.addValue("pssIds",pssIds); 
						   
						if(!(StringUtils.isBlank(year))) {
							sql.append(" AND s.year = :year ");
							parameters.addValue("year",year);
						}
						if(!(StringUtils.isBlank(month))){
							sql.append(" AND s.month = :month ");
							parameters.addValue("month",month);
						}
					
						
						

						   
						facultyIds = (ArrayList<String>) nameJdbcTemplate.queryForList(sql.toString(),parameters,String.class);
						
					
					return facultyIds;
				}
				
				
				//Get The FilePath
				public List<FileMigrationBean> getContentFromLocal()
				{
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "	SELECT id,filePath FROM acads.content  where filePath like 'E:/%'; ";
					
					List<FileMigrationBean> tickets = new ArrayList<FileMigrationBean>();
					try {		
						tickets = jdbcTemplate.query(sql,new BeanPropertyRowMapper(FileMigrationBean.class));
					
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					return tickets;
				}
				
				//Get The FilePath
				public List<FileMigrationBean> getContentHistoryFromLocal()
				{
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "	SELECT id,filePath FROM acads.content_history   where filePath like 'E:/%'	";
					List<FileMigrationBean> tickets = new ArrayList<FileMigrationBean>();
					try {		
						tickets = jdbcTemplate.query(sql,new BeanPropertyRowMapper(FileMigrationBean.class));
					
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					return tickets;
				}
				
				
				public int updateContentUrlLink(final FileMigrationBean fileData)
				{
					
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "UPDATE acads.content SET filePath = ? WHERE id = ?";
					
							
						return jdbcTemplate.update(sql, new PreparedStatementSetter() {
							
							@Override
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								 	preparedStatement.setString(1,fileData.getFilePath());
					                preparedStatement.setInt(2,fileData.getId());
					   		
							}
						});
					 
					
					
				}
				

				public int updateContentHistoryUrlLink(final FileMigrationBean fileData)
				{
					
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "UPDATE acads.content_history SET filePath = ? WHERE id = ?";
					
							
						return jdbcTemplate.update(sql, new PreparedStatementSetter() {
							
							@Override
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								 	preparedStatement.setString(1,fileData.getFilePath());
					                preparedStatement.setInt(2,fileData.getId());
					   		
							}
						});
					 
					
					
				}
				
				public int updateContentUrlLinkInDenormalized(final FileMigrationBean fileData)
				{
					
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "UPDATE acads.content_denormalized SET filePath = ? WHERE contentId = ?";
					
							
						return jdbcTemplate.update(sql, new PreparedStatementSetter() {
							
							@Override
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								 	preparedStatement.setString(1,fileData.getFilePath());
					                preparedStatement.setInt(2,fileData.getId());
					   		
							}
						});
					 
					
					
				}
				

				@Transactional(readOnly = true)
				public ArrayList<String> getSubjectsForSem1and2(String consumerProgramStructureId) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "select subject from exam.program_sem_subject where sem in (1,2) and consumerProgramStructureId=?";
					ArrayList<String> data =new ArrayList<String>();
			 		try {
			 		data = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(String.class));
			 		}
			 		catch (DataAccessException e) { 
//			 			e.printStackTrace();
			 		}
			 		return data;
			 	}
				
				@Transactional(readOnly = true)
			 	public String getSemFromStudentDetail(String sapid) {
			 		jdbcTemplate = new JdbcTemplate(dataSource);
			 		//System.out.println(sapid);
			 		String sql = "select sem from exam.students where sapid=? ";
			 		
			 		String sem="";
			 		try {
			 			jdbcTemplate = new JdbcTemplate(dataSource);
			 			StudentAcadsBean student = (StudentAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(StudentAcadsBean.class));

			 			sem = student.getSem();
			 		} catch (DataAccessException e) { 
//			 			e.printStackTrace();
			 		}
			 		 return sem;
			 	}
				
				@Transactional(readOnly = true)
				public List<ContentAcadsBean> getWXContentsBySubjectCodeId(String subjectCodeId, String month, String year)
				{
					
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					List<ContentAcadsBean> contents = null;
					StringBuffer sql = new StringBuffer("	SELECT subjectCodeId,c.* FROM     " + 
														"	acads.session_subject_mapping ssm	" + 
														"   INNER JOIN	" + 
														"	acads.sessions s on ssm.sessionId = s.id	" + 
														"	INNER JOIN    " + 
														"	acads.content c on  s.moduleid = c.sessionPlanModuleId	" + 
														"	WHERE ssm.subjectCodeId = ? and s.year = ? and s.month = ?  GROUP BY (ssm.sessionId) ORDER BY c.id DESC; ");
					
					try {
						contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								preparedStatement.setString(1,subjectCodeId);
								preparedStatement.setString(2,year);
								preparedStatement.setString(3,month);
						}},new BeanPropertyRowMapper(ContentAcadsBean.class));
						
						if(contents.size() == 0)
							contents = getWXContentsBySubjectCodeIdHistory(subjectCodeId,month,year);
					}catch(Exception e)
					{
							  
					}
					return contents;
					
				}
				
				@Transactional(readOnly = true)
				public List<ContentAcadsBean> getWXContentsBySubjectCodeIdHistory(String subjectCodeId, String month, String year)

				{
					jdbcTemplate = new JdbcTemplate(dataSource);
					List<ContentAcadsBean> contents = null;
					StringBuffer sql = new StringBuffer("	SELECT subjectCodeId,c.* FROM     " + 
							"	acads.session_subject_mapping_history ssm	" + 
							"   INNER JOIN	" + 
							"	acads.sessions_history s on ssm.sessionId = s.id	" + 
							"	INNER JOIN    " + 
							"	acads.content_history c on  s.moduleid = c.sessionPlanModuleId	" + 
							"	WHERE ssm.subjectCodeId = ? and s.year = ? and s.month = ?  GROUP BY (ssm.sessionId) ORDER BY c.id DESC; ");
					
					try {
						contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								preparedStatement.setString(1,subjectCodeId);
								preparedStatement.setString(2,year);
								preparedStatement.setString(3,month);
						}},new BeanPropertyRowMapper(ContentAcadsBean.class));
						
					
					}catch(Exception e)
					{
							  
					}
					return contents;
					
				}
				

				//Inserting In Temporary Content Table
				public int	insertionContentWithMappingInTemp(final ContentAcadsBean bean,long contentId,List<ContentAcadsBean> consumerProgramStructureIds,String year,String month)
				{
					StringBuffer sql =  new StringBuffer(" INSERT INTO acads.content_denormalized "
							+ " (contentId,programSemSubjectId,consumerProgramStructureId,name,description,filePath,year,month,webFileurl,urlType,previewPath,acadDateFormat,subject,contentType,createdDate,activeDate) "
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?) ");

					int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
		
							ContentAcadsBean m = consumerProgramStructureIds.get(i);
							ps.setLong(1,contentId);
							ps.setString(2, m.getProgramSemSubjectId());
							ps.setString(3,m.getConsumerProgramStructureId());
							ps.setString(4, bean.getName());
							ps.setString(5,bean.getDescription());
							ps.setString(6, bean.getFilePath());
							ps.setString(7, year);
							ps.setString(8,month);
							ps.setString(9, bean.getWebFileurl());
							ps.setString(10,bean.getUrlType());
							ps.setString(11, bean.getPreviewPath());
							ps.setDate(12, DateTimeHelper.findAcadDate(year,month));
							ps.setString(13,bean.getSubject());
							ps.setString(14, bean.getContentType());
							ps.setString(15, bean.getActiveDate());
		
						}


						@Override
						public int getBatchSize() {
							return consumerProgramStructureIds.size();
						}
					});

					return batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds.length;

				}

				//Update Single Content In the temp table
				public int updateSingleContentWithMappingInTemp(ContentAcadsBean content,long contentId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					StringBuffer sql = new StringBuffer("update acads.content_denormalized set "
							+ " name = ?, "
							+ " description = ?,"
							+ " webFileurl = ?, "
							+ " urlType = ?, "
							+ " contentType = ?, "
							+ " contentId = ?"
							+ " where contentId = ? and consumerProgramStructureId = ?");
	
					int i = jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {

						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,content.getName());
							preparedStatement.setString(2,content.getDescription());
							preparedStatement.setString(3,content.getWebFileurl());
							preparedStatement.setString(4,content.getUrlType());
							preparedStatement.setString(5,content.getContentType());
							preparedStatement.setLong(6,contentId);
							preparedStatement.setString(7,content.getId());
							preparedStatement.setString(8,content.getConsumerProgramStructureId());
						}


					});	

	 
					return i;
				}



	
	

				//Updation Whole Content In Temp Table
				public int updateWholeContentWithMappingInTemp(ContentAcadsBean content) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					StringBuffer sql =new StringBuffer("update acads.content_denormalized set "
							+ " name = ?, "
							+ " description = ?,"
							+ " webFileurl = ?, "
							+ " urlType = ?, "
							+ " contentType = ? "
							+ " where contentId = ? ");
	
					int i = jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,content.getName());
							preparedStatement.setString(2,content.getDescription());
							preparedStatement.setString(3,content.getWebFileurl());
							preparedStatement.setString(4,content.getUrlType());
							preparedStatement.setString(5,content.getContentType());
				preparedStatement.setString(6,content.getId());
			
						}
					});	
	
					return i;
				}


				/*Delete Single Content In Temp Table*/
 
				public int deleteSingleContentIdInTemp(String contentId,String consumerProgramStructureId) {
					StringBuffer sql =new StringBuffer("Delete from acads.content_denormalized "
							+ "where contentId = ? and consumerProgramStructureId in ( " +consumerProgramStructureId+ " )");

					int deletedRows =  jdbcTemplate.update(sql.toString(),  new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,contentId);
						}});
					return deletedRows;
				}

				/*Delete Whole Content In Temporary Table */
				public int deleteWholeContentIdInTemp(String contentId) {
					StringBuffer sql = new StringBuffer("Delete from acads.content_denormalized "
							+ "where contentId = ? ");
					jdbcTemplate = new JdbcTemplate(dataSource);
	
					int deletedRows =  jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,contentId);
						}});
					return deletedRows;
	
	 
				}
				
				@Transactional(readOnly = true)
				public ArrayList<SessionBean> getSessionListForMeetingKey(String meetingkey) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="select * from acads.sessions where meetingKey=?";
					ArrayList<SessionBean> scheduledSessionList = new  ArrayList<SessionBean>();
					try {
						scheduledSessionList = (ArrayList<SessionBean>)jdbcTemplate.query(sql, new Object[]{
								meetingkey }, new BeanPropertyRowMapper(SessionBean.class));
					} catch (Exception e) {
						logger.error("While fetching Session list for MeetingKey - Error Message :"+e.getMessage());
					}
					return scheduledSessionList;
				}
				
				@Transactional(readOnly = true)
				public SessionBean getSessionDetailsBySessionId(Long sessionId) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="SELECT date, startTime, facultyId FROM acads.sessions WHERE id=?;";
					SessionBean sessionObject = (SessionBean) jdbcTemplate.queryForObject(sql, new Object[]{ sessionId }, new BeanPropertyRowMapper(SessionBean.class));
					return sessionObject;
				}

				public SessionBean getBatchNameFromBatchId(Long batchId) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql="SELECT id,name FROM exam.batch WHERE id=?;";
					SessionBean sessionObject = (SessionBean) jdbcTemplate.queryForObject(sql, new Object[]{ batchId }, new BeanPropertyRowMapper(SessionBean.class));
					return sessionObject;
				}			
				
			    @Transactional(readOnly = true)
				public ArrayList<ConsumerProgramStructureAcads> getAllSubjectCodeLists(){
					jdbcTemplate = new JdbcTemplate(dataSource);
					ArrayList<ConsumerProgramStructureAcads> subjectCode = new ArrayList<ConsumerProgramStructureAcads>();
					StringBuffer sql =  new StringBuffer(" SELECT id AS subjectCodeId ,subjectcode ,subjectname AS subjectName FROM exam.mdm_subjectcode " 
														+" WHERE active = 'Y' ORDER BY subjectcode, subjectname ASC");
					subjectCode = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
					return subjectCode;  
				}

			    @Transactional(readOnly = true)
				public List<ProgramBean> getProgramStructureDetails() {
					
					jdbcTemplate = new JdbcTemplate(dataSource);
					
					List<ProgramBean> programDetailsList = new ArrayList<ProgramBean>();
					
					String sql="SELECT cps.id AS id, p.code AS code, ps.program_structure AS programStructure, c.name AS consumerType, p.name AS program "
							+ "FROM exam.consumer_program_structure cps "
							+ "INNER JOIN exam.program_structure ps "
							+ "ON cps.programStructureId = ps.id "
							+ "INNER JOIN exam.program p "
							+ "ON cps.programId = p.id "
							+ "INNER JOIN exam.consumer_type c "
							+ "ON cps.consumerTypeId = c.id "
							+ "ORDER BY code;";
					
					programDetailsList = (List<ProgramBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramBean.class));					
					return programDetailsList;
				}
}
