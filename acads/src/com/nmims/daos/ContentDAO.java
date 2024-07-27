<<<<<<< HEAD
package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AnnouncementBean;
import com.nmims.beans.ConsumerProgramStructure;
import com.nmims.beans.ConsumerProgramStructureBean;
import com.nmims.beans.ContentBean;
import com.nmims.beans.ELearnResourcesBean;
import com.nmims.beans.ExamOrderBean;
import com.nmims.beans.Page;
import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.Post;
import com.nmims.beans.Posts;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingBean;
import com.nmims.beans.ReRegistrationBean;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentBean;
import com.nmims.helpers.PaginationHelper;





public class ContentDAO extends BaseDAO{
	@Autowired(required = false)
	ApplicationContext act;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	public ArrayList<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname as subject from exam.subjects where subjectname <> '' order by subject ";
		
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		subjectList.add("Orientation");
		subjectList.add("Assignment");
		
		return subjectList;

	}
	

	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Double.class);


		}catch(Exception e){
			e.printStackTrace();
		}
		
		return examOrder;
		
	}
	
	public StudentBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";


			//System.out.println("SQL = "+sql);

			ArrayList<StudentBean> studentList = (ArrayList<StudentBean>)jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentBean.class));
			
			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);

				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			
			//System.out.println("STUDENT :"+student);
			return student;
		}catch(Exception e){
			e.printStackTrace();
//			System.out.println("Acads App:No student information foud for this user. Must be Admin!");
			return null;
		}
		
	}
	
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	
	public StudentBean getStudentRegistrationData(String sapId) {
		StudentBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentBean .class));
		} catch (Exception e) {
//			System.out.println("getStudentRegistrationData :"+e.getMessage());
		}
		return studentRegistrationData;
	}
	
	public UserAuthorizationBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationBean user = (UserAuthorizationBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationBean.class));
			return user;
		} catch (Exception e) {
//			System.out.println("getUserAuthorization :"+e.getMessage());
		}

		return null;
	}
	
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
		
		//System.out.println("Acads Portal: Authorized centers = "+centers);
		return centers;
	}
	
	public ArrayList<ProgramSubjectMappingBean> getFacultySubjects(String facultyId) {
		ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.* from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			programSubjectMappingList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, new Object[]{facultyId},
					new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programSubjectMappingList;
	}
	
	public String addIntoVideoContent(VideoContentBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			return upsertVideoContent(bean, jdbcTemplate);
		}else {
			return upsertVideoContentForPG(bean, jdbcTemplate);
		}
		
	}
	
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoContent(final List<VideoContentBean> videoContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}
				upsertVideoContent(bean, jdbcTemplate);
				//System.out.println("Upserted row "+i);
			}catch(Exception e){
				e.printStackTrace();
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	// batchUpdateVideoTopic Start
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoTopic(final List<VideoContentBean> videoContentList) {

		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		
		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				long key = dao.saveVideoSubTopic(bean);
//				System.out.println(key + "<== key For Added Video Topic");
						
			}catch(Exception e){
				e.printStackTrace();
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


	public String upsertVideoContent(VideoContentBean bean, JdbcTemplate jdbcTemplate) {
		
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
		
//		System.out.println("--------------->>>>>>>> audioFile : " + audioFile);
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
			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
				insertRecordingsPostEMBA(bean,id,"insert");
			}
			return "success";
		
		
	}
	
	public String upsertVideoContentForPG(VideoContentBean bean, JdbcTemplate jdbcTemplate) {
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
	
//			int id = holder.getKey().intValue();
//			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
//				insertRecordingsPostEMBA(bean,id,"insert");
//			}
			return "success";
		
		
	}
	
	//upsetVideoContent Old End
	
	public ArrayList<String> getFacultySubjectList(String facultyId) {
		ArrayList<String> subjectList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.subject from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y' and forumLive='Y') ";

		 subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{facultyId} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectList;
	}

	public ArrayList<ProgramSubjectMappingBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject order by program, sem, subject";

		ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
		return programSubjectMappingList;
	}
	
	public List<ContentBean> getContentsForSubjects(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content where subject = ? ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContentsForSubjectsForCurrentSession(String subject, ContentBean content) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM acads.content WHERE subject = ? AND month = ? AND year = ? "; 
				
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
		
		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject, content.getMonth(), content.getYear()}, 
										new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContentsForLeads(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    acads.content_forleads " + 
				"WHERE " + 
				"    subject = ?";

		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContents(ContentBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where subject = ? and year = ? and month = ? ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{searchBean.getSubject(), searchBean.getYear(), searchBean.getMonth()}, 
				new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getAllContents() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content ";

		List<ContentBean> contents= new ArrayList<>();
		try {
			contents = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ContentBean .class));
			if(contents != null) {
//				System.out.println("In getAllContents got contents : "+contents.size()); 
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contents;
	}
	
	public List<ContentBean> getContentsForIds(ContentBean searchBean) {
		String contentIds = StringUtils.join(searchBean.getContentToTransfer(), ',');
		//System.out.println("contentIds = "+contentIds);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where id in ( " + contentIds + ")";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	
	
	public List<ContentBean> getContentsForMultipleSubjects(ArrayList<String> subjectList) {
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

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	
	public ArrayList<ProgramSubjectMappingBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<ProgramSubjectMappingBean> subjectsList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));

		return subjectsList;
	}


	public void saveContentDetails(ContentBean bean,final  String subject, final String year,final  String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


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
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, subject);
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

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();


		jdbcTemplate.update(psc, keyHolder);

		int primaryKey = keyHolder.getKey().intValue();
		
		//Insertion into lti.Post Started
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert", subject, year, month);
		}
		
	}
	
	//duplicate the above function so to return primary key after insert
	public long saveContentFileDetails(final ContentBean bean,final String subject, final String year, final String month) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	try {
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,sessionPlanModuleId) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";


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
		        		        
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();
		
		if(newSessionPlanModuleId != null && newSessionPlanModuleId != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert",subject,year,month);
		}
		
		return primaryKey;
	} catch (DataAccessException e) {
		e.printStackTrace();
		return 0;
	}

	}
	
	//saving content for leads 
	public long saveContentFileDetailsForLeads(final ContentBean bean,final String subject) {
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
			e.printStackTrace();
			return 0;
		}

		}
	
	
	public ConsumerProgramStructure getConsumerDataForLeads(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructure consumerTypeLeads = null;
		
		String sql =  "SELECT id,name FROM exam.consumer_type where name='retail'";
		
		try {
			consumerTypeLeads = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//			System.out.println("____________________________getConsumerDataForLeads"+consumerTypeLeads+"____________________________");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		return consumerTypeLeads;  
	}

	public ConsumerProgramStructure getProgramrStructureForLeads() {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructure programStructureForLeads = null;
		
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
			programStructureForLeads = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//			System.out.println("____________________________programStructureForLeads"+programStructureForLeads+"____________________________");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
		return programStructureForLeads;  
	}
	
	public ArrayList<ConsumerProgramStructureBean> getProgramsForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "SELECT cps.programId,cps.programStructureId,cps.consumerTypeId, p.code FROM exam.consumer_program_structure cps left join exam.program p on cps.programId=p.id where cps.programStructureId=8 and cps.consumerTypeId=6 order by p.code;";

		ArrayList<ConsumerProgramStructureBean> subjectsList = (ArrayList<ConsumerProgramStructureBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureBean.class));
//		System.out.println("__________________"+ subjectsList+ "__________________");
		return subjectsList;
	}
	
	
	public List<ContentBean> getContentFiles(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where subject = ? ";
		
		List<ContentBean> contentFiles = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean.class));
		return contentFiles;
	}
	public void deleteContent(String id) {
		
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});
		
	}
	
	//duplicated above method to return no of rows deleted
	public int deleteContentById(long id) {
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			int deletedRows =  jdbcTemplate.update(sql, new Object[] {id});
			return deletedRows;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	public int deleteContentIdAndMasterKeyMappingByIdAndMasterkey(String contentId,String consumerProgramStructureId) {
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping "
					+ "where contentId = ? and consumerProgramStructureId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			int deletedRows =  jdbcTemplate.update(sql, new Object[] {contentId,consumerProgramStructureId});
//			System.out.println("In deleteContentIdAndMasterKeyMappingByIdAndMasterkey deletedRows :"+deletedRows);
			return deletedRows;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}

	public ContentBean findById(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where id = ? ";
		
		ContentBean content = (ContentBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ContentBean.class));
		return content;
	}
	public Post findPostByReferenceId(String id) {  
		jdbcTemplate = new JdbcTemplate(dataSource);  
		String sql = "Select * from lti.post where referenceId =? and type='Resource'";          
		return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(Post.class));  
		
	}
	public void updateContent(ContentBean content) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.content set "
				+ " name = ?, "
				+ " description = ?,"
				+ " programStructure = ?, "
				+ " webFileurl = ?, "
				+ " urlType = ?, "
				+ " contentType = ? "
				+ " where id = ? ";
		
		jdbcTemplate.update(sql, new Object[] { 
				content.getName(),
				content.getDescription(),
				content.getProgramStructure(),
				content.getWebFileurl(),
				content.getUrlType(),
				content.getContentType(),
				content.getId()
		});
		
		if(content.getSessionPlanModuleId() != null && content.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(content, Integer.parseInt(content.getId()), "update",content.getSubject(),content.getYear(),content.getMonth());		}
		
	}

	public ArrayList<ProgramSubjectMappingBean> getUnAttemptedSubjects(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select r.program, r.sem, ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and ps.subject not in (select subject from exam.passfail where sapid = ?)";

		ArrayList<ProgramSubjectMappingBean> subjectsList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId, sapId}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));

		return subjectsList;
	}

	public List<ContentBean> getRecordingForLastCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c, exam.examorder eo where subject = ? "
				+ " and c.month = eo.acadMonth and c.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	@SuppressWarnings("rawtypes")
	public List<AnnouncementBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<AnnouncementBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	//	Added for SAS
	  @SuppressWarnings("rawtypes")
		public List<AnnouncementBean> getAllActiveAnnouncements(String program,String progrmStructure){
			String sql = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";

			}else{
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			}
			
			List<AnnouncementBean> announcements = jdbcTemplate.query(sql, new Object[]{program,progrmStructure}, new BeanPropertyRowMapper(AnnouncementBean.class));

			return announcements;
		}
//		Added for SAS end
	  
		public StudentBean getStudentMaxSemRegistrationData(String sapId) {
			StudentBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select * from exam.registration r where r.sapid = ?  "
						+ "and r.sem = (select max(registration.sem) from exam.registration where sapid = ?) ";

				studentRegistrationData = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentBean .class));
			} catch (Exception e) {
//				System.out.println("getStudentRegistrationData :"+e.getMessage());
			}
			return studentRegistrationData;
		}
		
		public List<ExamOrderBean> getLiveFlagDetails(){
			List<ExamOrderBean> liveFlagList = new ArrayList<ExamOrderBean>();

			final String sql = " Select * from exam.examorder order by examorder.order ";
			jdbcTemplate = new JdbcTemplate(dataSource);

			liveFlagList = (ArrayList<ExamOrderBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderBean>(ExamOrderBean.class));

			return liveFlagList;
		}
		public void saveSessionQA(SessionDayTimeBean sessionQA){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="insert into acads.session_question_answer(meetingKey,sessionId,sapId,query,answer,status,createdDate) VALUES (?,?,?,?,?,?,sysdate())";
			try {
				jdbcTemplate.update(sql, new Object[] { sessionQA.getMeetingKey(),sessionQA.getSession_id(),sessionQA.getSapId(),sessionQA.getQuestion(),sessionQA.getAnswer(),sessionQA.getIsAnswered()
					});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 
		}

		public String getstudentByEmail(String emailId) {
			StudentBean studentList = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  distinct *  from exam.students  where emailId = ? limit 1"	;
				studentList = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{emailId}, new BeanPropertyRowMapper(StudentBean.class));
				 return studentList.getSapid();
			} catch (Exception e) {
//				System.out.println("getStudentRegistrationData :"+e.getMessage());
				return "";
			}
			
		}
		public SessionBean getSessionByMeetingKey(String id) {
			
			SessionBean sessionDetails = null;
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select s.*,f.facultyId,f.email  from acads.sessions s, acads.faculty f  where meetingKey = ?  and f.facultyId=s.facultyId"	;

				sessionDetails = (SessionBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionBean.class));
				
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			
			return sessionDetails;
			
		}
		
		public ArrayList<SessionBean> getSessionsHeldPerDay() {
			
			ArrayList<SessionBean> sessionsList = new ArrayList<SessionBean>();
			try {
//                System.out.println("calling get sessions..");
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select *  from acads.sessions  where date = DATE(NOW())"	;

				sessionsList = (ArrayList<SessionBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionBean.class));
//				System.out.println("sessionsList:"+sessionsList);
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			
			return sessionsList;
			
		}
		
		public ArrayList<SessionDayTimeBean> getCompletedSessionsOfFacultyForPg(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeBean> scheduledSessionList = new  ArrayList<SessionDayTimeBean>();
			try {
				String sql = "SELECT s.* FROM acads.sessions s, exam.examorder eo "
						+ " where  s.month = eo.acadmonth and s.year = eo.year and s.moduleid is null"
						+ " and eo.order in (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
						+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )  and  EXISTS(SELECT q.* FROM acads.session_question_answer q where  s.id=q.sessionId)";

				scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{facultyId,facultyId, facultyId, facultyId}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			return scheduledSessionList;
		}
		public ArrayList<SessionDayTimeBean> getCompletedSessionsOfFacultyForMbaWx(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeBean> scheduledSessionList = new  ArrayList<SessionDayTimeBean>();
			try {
				String sql = "SELECT s.* FROM acads.sessions s, exam.examorder eo "
						+ " where  s.month = eo.acadmonth and s.year = eo.year and s.moduleid is not null"
						+ " and eo.order in (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
						+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )  and  EXISTS(SELECT q.* FROM acads.session_question_answer q where  s.id=q.sessionId)";

				scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{facultyId,facultyId, facultyId, facultyId}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			return scheduledSessionList;
		}
		public List<SessionQueryAnswer> getSingleSessionsQA(String session_id) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q, exam.students s  where q.sessionId = ?   and s.sapId=q.sapId and LENGTH(q.query) > 10"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

		public void updateAnswer(SessionQueryAnswer sessionQn) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "update acads.session_question_answer set "
						+ " answer= ?, "
						+ " status = 'Answered', "
						+ " isPublic = ? ," 
						+ " answeredByFacultyId = ?"
						+ " where id = ? ";
				
				jdbcTemplate.update(sql, new Object[] {sessionQn.getAnswer() ,sessionQn.getIsPublic(), sessionQn.getFacultyId(), sessionQn.getId()});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 
		}
		public List<SessionQueryAnswer> getStudentQAforSession(String session_id,String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.sapId = ? and LENGTH(q.query) >10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,sapId}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

		public List<SessionQueryAnswer> getPublicQAsforSession(String session_id, String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.isPublic = 'Y' and (q.answer IS NOT NULL AND q.answer != '')  and q.sapId != ? and LENGTH(q.query) > 10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,sapId}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

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
			
//			 System.out.println("In getConsumerProgramStructureIdsBySubjectAndProgramStructure sql-\n"+sql);
			   
			
			List<String> data = new ArrayList<>();
			try {
				data = (List<String>)jdbcTemplate.query(sql,new Object[] {subject}, new SingleColumnRowMapper(String.class));
//				 System.out.println("list----"+data);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 return data;
		}

		public String batchInsertCententIdAndMasterKeyMappings(final List<ContentBean> cententIdAndMasterKeyMappings) {
//			System.out.println("Enterred batchInsertCententIdAndMasterKeyMappings()");
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
				if(cententIdAndMasterKeyMappings !=null) {
					//System.out.println("IN batchUpdate cententIdAndMasterKeyMappings size : \n"+cententIdAndMasterKeyMappings.size());
				}
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						ContentBean m = cententIdAndMasterKeyMappings.get(i);
						ps.setString(1, m.getId());
						ps.setString(2, m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						ps.setString(4, m.getCreatedBy());
						
						ps.setString(5, m.getCreatedBy());
						if(i % 500 == 0) {
//							System.out.println("IN batchUpdate processsed :"+i);
						}
					}
					public int getBatchSize() {
						return cententIdAndMasterKeyMappings.size();
					}
				});
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorMessage += e.getMessage();
			}
			
//			System.out.println("Exit batchInsertCententIdAndMasterKeyMappings()");
			return errorMessage;
		}
		
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(final long contentId,final List<ContentBean> consumerProgramStructureIds){
			try {
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
//				System.out.println("batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds : "+batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds.toString());
				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Error in batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds : "+e.getMessage();
			}
		
		}

		
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(final long contentId,final List<ContentBean> consumerProgramStructureIds){
			try {
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping_forLeads "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
//				System.out.println("batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads : "+batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads.toString());
				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Error in batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads : "+e.getMessage();
			}
		
		}
		
		
		public String batchInsertOfMakeContentLiveConfigs(final ContentBean searchBean,
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
//				System.out.println("batchInsertOfMakeContentLiveConfigs "+batchInsertOfMakeContentLiveConfigs);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				return "Error in creating configurations , Error : "+e.getMessage();
			}
			return errorMessage;
		}
		
		public List<ContentBean> getContentLiveConfigList(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentLiveConfigList = new ArrayList<>();
			
			String sql =  "select cls.year,cls.month,p.code as program,"
					+ "p_s.program_structure as programStructure,c_t.name as consumerType "
					+ "from exam.content_live_settings as cls "
					+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = cls.consumerProgramStructureId "
					+ "left join exam.program as p on p.id = c_p_s.programId "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId";
			
			try {
				contentLiveConfigList = (List<ContentBean>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return contentLiveConfigList;  
			
		}
		
		public Map<String,Integer> getContentIdNCountOfProgramsApplicableToMap(String ids) {
			jdbcTemplate = new JdbcTemplate(dataSource);
//			System.out.println("In getContentIdNCountOfProgramsApplicableToMap ids :"+ids);
			String sql = "SELECT  " + 
					"    count(c.id) as countOfProgramsApplicableTo ,c.id " + 
					"FROM " + 
					"    acads.content c, " + 
					"    acads.contentid_consumerprogramstructureid_mapping ccm " + 
					"WHERE " + 
					"    c.id = ccm.contentId " + 
					"    AND c.id in ("+ids+") " + 
					"GROUP BY c.id  " + 
					"ORDER BY c.id DESC ";
					try {
				List<ContentBean> list = (List<ContentBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContentBean.class));
				Map<String,Integer> map = new HashMap<>();
				for(ContentBean bean : list) {
					map.put(bean.getId(), bean.getCountOfProgramsApplicableTo());
				}
			
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		public List<ContentBean> getProgramsListForCommonContent(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentList=new ArrayList<>();
//			System.out.println("In getProgramsListForCommonContent got id : "+id);
			String sql="SELECT   " + 
					"    c.id,c.year,c.month,c.subject,"
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
					"        AND c.id = ?  " + 
					"   " + 
					"ORDER BY c.id DESC";
			
			try {
//				 System.out.println("IN getProgramsListForCommonContent sql : \n"+sql);
				 contentList = (List<ContentBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(ContentBean.class));
				 
				 if(contentList != null)
//				 System.out.println("IN getProgramsListForCommonContent contentList size : "+contentList.size());
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contentList;
		}

		public List<ContentBean> getContentsListWithMasterKeyDetailsBySubject(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentList=new ArrayList<>();
//			System.out.println("In getProgramsListForCommonContent got subject : "+subject);
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
//				 System.out.println("IN getProgramsListForCommonContent sql : \n"+sql);
				 contentList = (List<ContentBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contentList;
		}

		public int deleteContentIdMasterkeyMappingsById(String id) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				int deletedRows =  jdbcTemplate.update(sql, new Object[] {id});
				return deletedRows;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
		}
		public int deleteContentIdMasterkeyMappingsByIdNMasterKey(String id,String consumerProgramStructureId) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				int deletedRows =  jdbcTemplate.update(sql, new Object[] {id,consumerProgramStructureId});
//				System.out.println("In deleteContentIdMasterkeyMappingsByIdNMasterKey deletedRows : "+deletedRows);
				return deletedRows;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
		}
		

		public int getCountOfProgramsContentApplicableToById(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
//			System.out.println("In getCountOfProgramsContentApplicableTo id :"+id);
			String sql = " SELECT  " + 
						 "   	count(*)  " + 
						 " FROM " + 
						 "    acads.contentid_consumerprogramstructureid_mapping ccm " + 
						 " WHERE " +  
						 "    ccm.contentId = "+id+" " ;
			try {
				int count = (int) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
//				System.out.println("In getCountOfProgramsContentApplicableToById got count : "+count);
				return count;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		
		//System generated learning resources (insert data into posts table)
		//start
		
		public void insertResourcesPostTable(ContentBean bean,long id,String type,String subject,String year,String month) {
//			System.out.println("++++++++  in insertResourcesPostTable : EMBA post acad conent data entry   ++++++++++++++++");
//			System.out.println("post subject-->"+bean.getSubject());
			Posts posts = new Posts();
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
//			System.out.println("bean------->"+bean);
			posts.setHashtags(subject+","+bean.getSessionPlanModuleName());
//			System.out.println("hashtags-->"+posts.getHashtags());
			
			if (timeBoundIds.size() != 0) {
				if(type=="insert") {
					posts.setCreatedBy(bean.getCreatedBy());
					posts.setLastModifiedBy(bean.getCreatedBy());		
					posts.setVisibility(1);
					
					for(Integer programSemSubjectIds : timeBoundIds) {
//						System.out.println("programSemSubjectId----- " + programSemSubjectIds);
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
			} else {
//				System.out.println("No need to add in Post table.");
			}

		}
		
		public List<ContentBean> getconsumerProgramStructureIdList(String subject,String programStructure, Long moduleId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> consumerProgramStructureIdList = new ArrayList<ContentBean>();
			
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
			
				consumerProgramStructureIdList = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return consumerProgramStructureIdList;
		}

		public List<Integer> getTimeBoundIdEMBABySessionPlan(long sessionPlanId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT t.timeboundId FROM acads.sessionplanid_timeboundid_mapping t,acads.sessionplan_module m where t.sessionPlanId = m.sessionPlanId  and" + 
					"   m.id =? ";
			
			List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,new Object[] { sessionPlanId }, new SingleColumnRowMapper(Integer.class));
//			System.out.println("timeBound id list : " + id);
//			System.out.println("sessionPlanId  : " + sessionPlanId);
			return id;
		}

				public void insertIntoPostProgSemSubEMBAResource(Integer programSemSubjectId, Integer postsId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
					jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

				}

				public int insertIntoPostsTableEMBAResource(final Posts bean,final int timeBoundId) {
//					   System.out.println("before insert in post table");
					jdbcTemplate = new JdbcTemplate(dataSource);
					KeyHolder holder = new GeneratedKeyHolder();
			  
					final String sql = " INSERT INTO lti.post ( userId, role, type, content, fileName,subject_config_id, filePath,fileType, referenceId, "
							 + " session_plan_module_id, subject, contentType, visibility, acadYear, acadMonth,scheduledDate, scheduleFlag,hashtags, "
							 + " createdBy, createdDate, lastModifiedBy, lastModifiedDate ) "
							 + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,?,?,sysdate(),?,sysdate()) ";

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
							ps.setString(16, "Y");    
							ps.setString(17, bean.getHashtags()); 
							ps.setString(18, bean.getCreatedBy());
							ps.setString(19, bean.getLastModifiedBy());
							return ps;
						}
					}, holder);

					int postId = holder.getKey().intValue();

					return postId;
				}
				
				public void updateResourcePostTable(final Posts bean) {
					jdbcTemplate = new JdbcTemplate(dataSource);
				
			  
					 String sql = "UPDATE lti.post SET "
							+ "userId = ?, "
							+ "role = ?, "
							+ "type = ?, "
							+ "content = ?, "
							+ "contentType = ?, "
							+ "fileName = ?, "
							+ "filePath = ?, "
							+ "referenceId = ?, "
							+ "lastModifiedBy = ?, "
							+ "lastModifiedDate = sysdate() "
							+ "WHERE referenceId = ? order by lastModifiedDate desc limit 1";

					jdbcTemplate.update(sql, new Object[] {
					bean.getUserId(),
					bean.getRole(),
					bean.getType(),
					bean.getContent(),
					bean.getFileName(),
					bean.getFilePath(),
					bean.getReferenceId(),
					bean.getLastModifiedBy(),
					bean.getReferenceId()
					
					
					});
				}

		//delete post from post table
		public void deleteContentFromPost(String id) {
			String sql = "Delete from lti.post where post_id = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql, new Object[] { 
					id
			});
		}

		//for inserting data into post table for MBA-WX
		//start
		
		public void insertRecordingsPostEMBA(VideoContentBean bean,int id,String type) {
//			System.out.println("in ContentDao insertRecordingsPostEMBA Called.");
			
			Posts posts = new Posts();
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
//			System.out.println("IN bean.getSubject().replace(\"_SesssionPlan_Video\", \"\") : ");
//			System.out.println(bean.getSubject().replace("_SesssionPlan_Video", ""));
			//List<Integer> timeBoundIds = getTimeBoundIdEMBA(bean.getSubject().replace("_SesssionPlan_Video", ""));
			if(type=="insert") {
				long timeBoundId = getTimeBoundIdByModuleId(bean.getSessionPlanModuleId());
				if (timeBoundId != 0) {
					posts.setSubject_config_id(timeBoundId);
					posts.setSession_plan_module_id(bean.getSessionPlanModuleId());
					int post_id = insertIntoPostsTableEMBA(posts);
					//insert post into Redis
					posts.setPost_id(post_id+"");
					//insertToRedis(posts);  
					//refreshRedis(posts);
				}
			}else {
				
				try {
					posts.setLastModifiedBy(bean.getLastModifiedBy());
					updateSessionPostTable(posts);
					
				} catch (Exception e) {
				} 
				
			}
			//refresh cache on insert/update.
			refreshRedis(posts);
			
			
			
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
//		    	  System.out.println("IN savePostInRedisToCache() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
				    
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
	public String deleteFromRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
		  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";
//		    	  System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}*/
		public String refreshRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
				 String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
//		    	 System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
		public String refreshRedis(Posts posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()+""));
				 String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
//		    	  System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
				public List<Integer> getTimeBoundIdEMBA(String subject) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					//String sql = "SELECT id FROM exam.program_sem_subject where subject=? ";
					String sql =  " select id from lti.student_subject_config where prgm_sem_subj_id in "
								+ " (SELECT id FROM exam.program_sem_subject where subject= ? and studentType = 'TimeBound') "
								+ " and acadMonth = '" + CURRENT_ACAD_MONTH + "' and acadYear = "+ CURRENT_ACAD_YEAR +" ";
					
					List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,
							new Object[] { subject },
							new SingleColumnRowMapper(Integer.class));
//					System.out.println("list----" + id);
					return id;
				}

				public void insertIntoPostProgSemSubEMBA(Integer programSemSubjectId, Integer postsId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
					jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

				}

				public int insertIntoPostsTableEMBA(final Posts bean) {
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
					} catch (Exception e) {
						e.printStackTrace();
					}
//					System.out.println("in insertIntoPostsTableEMBA -> postId : "+postId);
					return postId;
				}
				
				public void updateSessionPostTable(final Posts bean) {
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
				}
		//end Insert into post table

	public long getTimeBoundIdByModuleId(long moduleId) {
		
//		System.out.println("moduleId : "+moduleId);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT timeboundId FROM acads.sessionplanid_timeboundid_mapping "
					+ " where sessionPlanId = (SELECT sessionPlanId FROM acads.sessionplan_module where id = ? ) ";
		try {
			int id =  jdbcTemplate.queryForObject(sql, new Object[] {moduleId}, new SingleColumnRowMapper(Integer.class));
			Long timeBoundId = Long.valueOf(id);
			return timeBoundId;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
		
	public ELearnResourcesBean isStukentApplicable(String userId) {
		ELearnResourcesBean eLearnResourcesBean = new ELearnResourcesBean();
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
		 eLearnResourcesBean =  jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper (ELearnResourcesBean.class) );
		
		 return eLearnResourcesBean;
		}catch(Exception e) {
			e.printStackTrace();
			return eLearnResourcesBean;
		}
	}
	
	public ELearnResourcesBean isHarvardApplicable(String userId) {
		ELearnResourcesBean eLearnResourcesBean = new ELearnResourcesBean();
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
				"    u.userId = ? AND (u.roles = 'Instructor' OR u.roles = 'Administrator' ) AND p.name ='Harvard' group by u.roles, p.name ";
		 eLearnResourcesBean =  jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(ELearnResourcesBean.class) );
		 return eLearnResourcesBean;
		}catch(Exception e) {
			e.printStackTrace();
			return eLearnResourcesBean;
		}
	}
	
	public ArrayList<ConsumerProgramStructure> getConsumerTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT id,name FROM exam.consumer_type order by id asc ";
		ArrayList<ConsumerProgramStructure> consumerTypeList = (ArrayList<ConsumerProgramStructure>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
		return consumerTypeList;

	}
	
	public Page<ContentBean> getResourcesContent(int pageNo, int pageSize, ContentBean searchBean, String searchOption){
		
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
		
		sql = sql + " FROM acads.content c " + 
					" LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " +
				    " LEFT JOIN exam.consumer_program_structure cps ON cps.id = ccm.consumerProgramStructureId " + 
				    " LEFT JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				    " LEFT JOIN exam.program p ON p.id = cps.programId " + 
				    " LEFT JOIN exam.program_structure ps ON ps.id = cps.programStructureId " + 
				    " LEFT JOIN exam.program_sem_subject pss ON pss.subject = c.subject AND pss.consumerProgramStructureId = cps.id " + 
				    " WHERE 1 = 1 ";
		
		countSql = countSql + " FROM acads.content c " + 
							  " LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " + 
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
//		System.out.println("In getResourcesContent sql = " + sql);
//		System.out.println("In getResourcesContent Countsql = " + countSql);
//		System.out.println("In getResourcesContent parameters = " +parameters );
		
		PaginationHelper<ContentBean> pagingHelper = new PaginationHelper<ContentBean>();
		Page<ContentBean> page = pagingHelper.fetchPage(jdbcTemplate,countSql, sql, args, pageNo, pageSize,
														new BeanPropertyRowMapper(ContentBean.class));
		return page;
		
	}
	
	public ArrayList<ContentBean> getCommonGroupProgramList(ContentBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ContentBean> commonGroupList = new ArrayList<ContentBean>();
		try {
			String sql =" SELECT  " + 
						"   c.*, " + 
						"   p.code AS program, cps.programId, " + 
						"   ps.program_structure AS programStructure, cps.programStructureId, " + 
						"  	ct.name AS consumerType, cps.consumerTypeId, " + 
						" 	consumerProgramStructureId " + 
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
						"		AND c.year = ? " + 
						"       AND c.month = ? " + 
						"       AND subject = ? " +
						"		AND filePath = ? " +
						"		AND consumerProgramStructureId in ("+ bean.getConsumerProgramStructureId() +")";
			
			commonGroupList = (ArrayList<ContentBean>) jdbcTemplate.query(sql, new Object[] {bean.getYear(), bean.getMonth(), bean.getSubject(), bean.getFilePath()},
							  new BeanPropertyRowMapper(ContentBean.class));
//			System.out.println("getCommonGroupProgramList Query : " +sql);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
//		System.out.println("commonGroupList "+commonGroupList);
		return commonGroupList;
	}

	public void deleteContentById(String id) {
		String sql = " Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {id});
	}
	
	public int deleteContentIdConsumerPrgmStrIdMapping(String contentId, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] {contentId, consumerProgramStructureId});
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return row;
	}

	public boolean ifStudentAlreadyRegisteredForNextSem(String sapid,ReRegistrationBean activeRegistration) {
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
			e.printStackTrace();
		} 
		return registered;
 	}
	
	public ArrayList<String> getProgramStrutureList() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> programStrutureList = new ArrayList<String>();

		try {
			String sql = "SELECT program_structure FROM exam.program_structure ";
			programStrutureList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programStrutureList;
	}
	
	//Added By Riya
	public ArrayList<ContentBean> getMasterKeyMappedBySubjectCode(String subjectCodeId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select consumerProgramStructureId,id as programSemSubjectId from exam.mdm_subjectcode_mapping where subjectCodeId ="+subjectCodeId;

		ArrayList<ContentBean> consumerProgramStructureIds =(ArrayList<ContentBean>) jdbcTemplate.query(
				sql,  new BeanPropertyRowMapper(ContentBean.class));
		
//		System.out.println("In getconsumerProgramStructureIdsWithSubject consumerProgramStructureIds:--  "  + consumerProgramStructureIds);

		return consumerProgramStructureIds;
	}
	
	public String getSubjectNameById(String subjectcodeId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subjectname  FROM exam.mdm_subjectcode WHERE id = "+subjectcodeId;
		String subjectname = jdbcTemplate.queryForObject(sql, String.class);
		return subjectname;
	}
	
	
	public ArrayList<ConsumerProgramStructure> getMasterKeyMapSubjectCode(){
		jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql = "SELECT DISTINCT     "
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
				+ "exam.mdm_subjectcode msc ON mscm.subjectCodeId = msc.id    "
				+ "WHERE msc.active= 'Y'    "
				+ "ORDER BY ct.name , ps.program_structure , p.code ;";
		
		ArrayList<ConsumerProgramStructure> masterKeywithCode =(ArrayList<ConsumerProgramStructure>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//		System.out.println("MasterKey Map with subject code and name "+masterKeywithCode);
		return masterKeywithCode;
	}
	
	public String getSubjectNameByPssId(String pssIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql =" SELECT subjectname FROM exam.mdm_subjectcode_mapping msm " + 
						" INNER JOIN  exam.mdm_subjectcode ms ON msm.subjectCodeId = ms.id " + 
						" WHERE msm.id IN ("+pssIds+") " + 
						" GROUP BY subjectname ";
			String subjectName = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);
			return subjectName;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public ArrayList<ContentBean> getMasterKeyForContentMapping(String subjectCodeId,String pssId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select consumerProgramStructureId,id as programSemSubjectId from exam.mdm_subjectcode_mapping where ";
		
		if(!StringUtils.isBlank(pssId))
			sql += "id in("+pssId+")";
		else
			sql +=  "subjectCodeId ="+subjectCodeId;
		
//		System.out.println("Sql "+sql);
		ArrayList<ContentBean> consumerProgramStructureIds =(ArrayList<ContentBean>) jdbcTemplate.query(
						sql,  new BeanPropertyRowMapper(ContentBean.class));
				
//		System.out.println("In getconsumerProgramStructureIdsWithSubject consumerProgramStructureIds:--  "  + consumerProgramStructureIds);

		return consumerProgramStructureIds;
	}
}
=======
package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AnnouncementBean;
import com.nmims.beans.ConsumerProgramStructure;
import com.nmims.beans.ConsumerProgramStructureBean;
import com.nmims.beans.ContentBean;
import com.nmims.beans.ELearnResourcesBean;
import com.nmims.beans.ExamOrderBean;
import com.nmims.beans.FacultyBean;
import com.nmims.beans.Page;
import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.Post;
import com.nmims.beans.Posts;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingBean;
import com.nmims.beans.ReRegistrationBean;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentBean;
import com.nmims.helpers.PaginationHelper;





public class ContentDAO extends BaseDAO{
	@Autowired(required = false)
	ApplicationContext act;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	public ArrayList<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname as subject from exam.subjects where subjectname <> '' order by subject ";
		
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		subjectList.add("Orientation");
		subjectList.add("Assignment");
		
		return subjectList;

	}
	

	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Double.class);


		}catch(Exception e){
			e.printStackTrace();
		}
		
		return examOrder;
		
	}
	
	public StudentBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";


//			System.out.println("SQL = "+sql);

			ArrayList<StudentBean> studentList = (ArrayList<StudentBean>)jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentBean.class));
			
			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);

				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			
			//System.out.println("STUDENT :"+student);
			return student;
		}catch(Exception e){
			e.printStackTrace();
//			System.out.println("Acads App:No student information foud for this user. Must be Admin!");
			return null;
		}
		
	}
	
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	
	public StudentBean getStudentRegistrationData(String sapId) {
		StudentBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentBean .class));
		} catch (Exception e) {
//			System.out.println("getStudentRegistrationData :"+e.getMessage());
		}
		return studentRegistrationData;
	}
	
	public UserAuthorizationBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationBean user = (UserAuthorizationBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationBean.class));
			return user;
		} catch (Exception e) {
//			System.out.println("getUserAuthorization :"+e.getMessage());
		}

		return null;
	}
	
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
		
//		System.out.println("Acads Portal: Authorized centers = "+centers);
		return centers;
	}
	
	public ArrayList<ProgramSubjectMappingBean> getFacultySubjects(String facultyId) {
		ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.* from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			programSubjectMappingList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, new Object[]{facultyId},
					new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programSubjectMappingList;
	}
	
	public String addIntoVideoContent(VideoContentBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			return upsertVideoContent(bean, jdbcTemplate);
		}else {
			return upsertVideoContentForPG(bean, jdbcTemplate);
		}
		
	}
	
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoContent(final List<VideoContentBean> videoContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}
				upsertVideoContent(bean, jdbcTemplate);
//				System.out.println("Upserted row "+i);
			}catch(Exception e){
				e.printStackTrace();
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	// batchUpdateVideoTopic Start
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoTopic(final List<VideoContentBean> videoContentList) {

		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		
		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				long key = dao.saveVideoSubTopic(bean);
//				System.out.println(key + "<== key For Added Video Topic");
						
			}catch(Exception e){
				e.printStackTrace();
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


	public String upsertVideoContent(VideoContentBean bean, JdbcTemplate jdbcTemplate) {
		
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
		
//		System.out.println("--------------->>>>>>>> audioFile : " + audioFile);
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
			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
				insertRecordingsPostEMBA(bean,id,"insert");
			}
			return "success";
		
		
	}
	
	public String upsertVideoContentForPG(VideoContentBean bean, JdbcTemplate jdbcTemplate) {
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
	
//			int id = holder.getKey().intValue();
//			if(sessionPlanModuleId != null && sessionPlanModuleId != 0) {
//				insertRecordingsPostEMBA(bean,id,"insert");
//			}
			return "success";
		
		
	}
	
	//upsetVideoContent Old End
	
	public ArrayList<String> getFacultySubjectList(String facultyId) {
		ArrayList<String> subjectList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.subject from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y' and forumLive='Y') ";

		 subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{facultyId} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subjectList;
	}

	public ArrayList<ProgramSubjectMappingBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject where active = 'Y' order by program, sem, subject";

		ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
		return programSubjectMappingList;
	}
	
	public List<ContentBean> getContentsForSubjects(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content where subject = ? ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContentsForSubjectsForCurrentSession(String subject, ContentBean content) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM acads.content WHERE subject = ? AND month = ? AND year = ? "; 
				
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
		
		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject, content.getMonth(), content.getYear()}, 
										new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContentsForLeads(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    acads.content_forleads " + 
				"WHERE " + 
				"    subject = ?";

		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getContents(ContentBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where subject = ? and year = ? and month = ? ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{searchBean.getSubject(), searchBean.getYear(), searchBean.getMonth()}, 
				new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	public List<ContentBean> getAllContents() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content ";

		List<ContentBean> contents= new ArrayList<>();
		try {
			contents = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ContentBean .class));
			if(contents != null) {
//				System.out.println("In getAllContents got contents : "+contents.size()); 
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contents;
	}
	
	public List<ContentBean> getContentsForIds(ContentBean searchBean) {
		String contentIds = StringUtils.join(searchBean.getContentToTransfer(), ',');
		//System.out.println("contentIds = "+contentIds);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where id in ( " + contentIds + ")";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	
	
	public List<ContentBean> getContentsForMultipleSubjects(ArrayList<String> subjectList) {
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

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	
	public ArrayList<ProgramSubjectMappingBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<ProgramSubjectMappingBean> subjectsList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));

		return subjectsList;
	}


	public void saveContentDetails(ContentBean bean,final  String subject, final String year,final  String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


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
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, year);
				ps.setString(2, month);
				ps.setString(3, subject);
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

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();


		jdbcTemplate.update(psc, keyHolder);

		int primaryKey = keyHolder.getKey().intValue();
		
		//Insertion into lti.Post Started
		if(bean.getSessionPlanModuleId() != null && bean.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert", subject, year, month);
		}
		
	}
	
	//duplicate the above function so to return primary key after insert
	public long saveContentFileDetails(final ContentBean bean,final String subject, final String year, final String month) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	try {
		final String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,sessionPlanModuleId) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";


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
		        		        
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();
		
		if(newSessionPlanModuleId != null && newSessionPlanModuleId != 0) {
			insertResourcesPostTable(bean, primaryKey, "insert",subject,year,month);
		}
		
		return primaryKey;
	} catch (DataAccessException e) {
		e.printStackTrace();
		return 0;
	}

	}
	
	//saving content for leads 
	public long saveContentFileDetailsForLeads(final ContentBean bean,final String subject) {
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
			e.printStackTrace();
			return 0;
		}

		}
	
	
	public ConsumerProgramStructure getConsumerDataForLeads(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructure consumerTypeLeads = null;
		
		String sql =  "SELECT id,name FROM exam.consumer_type where name='retail'";
		
		try {
			consumerTypeLeads = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//			System.out.println("____________________________getConsumerDataForLeads"+consumerTypeLeads+"____________________________");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		return consumerTypeLeads;  
	}

	public ConsumerProgramStructure getProgramrStructureForLeads() {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ConsumerProgramStructure programStructureForLeads = null;
		
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
			programStructureForLeads = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//			System.out.println("____________________________programStructureForLeads"+programStructureForLeads+"____________________________");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
		return programStructureForLeads;  
	}
	
	public ArrayList<ConsumerProgramStructureBean> getProgramsForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "SELECT cps.programId,cps.programStructureId,cps.consumerTypeId, p.code FROM exam.consumer_program_structure cps left join exam.program p on cps.programId=p.id where cps.programStructureId=8 and cps.consumerTypeId=6 order by p.code;";

		ArrayList<ConsumerProgramStructureBean> subjectsList = (ArrayList<ConsumerProgramStructureBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureBean.class));
//		System.out.println("__________________"+ subjectsList+ "__________________");
		return subjectsList;
	}
	
	
	public List<ContentBean> getContentFiles(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where subject = ? ";
		
		List<ContentBean> contentFiles = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean.class));
		return contentFiles;
	}
	public void deleteContent(String id) {
		
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});
		
	}
	
	//duplicated above method to return no of rows deleted
	public int deleteContentById(long id) {
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			int deletedRows =  jdbcTemplate.update(sql, new Object[] {id});
			return deletedRows;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	public int deleteContentIdAndMasterKeyMappingByIdAndMasterkey(String contentId,String consumerProgramStructureId) {
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping "
					+ "where contentId = ? and consumerProgramStructureId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			int deletedRows =  jdbcTemplate.update(sql, new Object[] {contentId,consumerProgramStructureId});
//			System.out.println("In deleteContentIdAndMasterKeyMappingByIdAndMasterkey deletedRows :"+deletedRows);
			return deletedRows;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}

	public ContentBean findById(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where id = ? ";
		
		ContentBean content = (ContentBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ContentBean.class));
		return content;
	}
	public Post findPostByReferenceId(String id) {  
		jdbcTemplate = new JdbcTemplate(dataSource);  
		String sql = "Select * from lti.post where referenceId =? and type='Resource'";          
		return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(Post.class));  
		
	}
	public void updateContent(ContentBean content) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.content set "
				+ " name = ?, "
				+ " description = ?,"
				+ " programStructure = ?, "
				+ " webFileurl = ?, "
				+ " urlType = ?, "
				+ " contentType = ? "
				+ " where id = ? ";
		
		jdbcTemplate.update(sql, new Object[] { 
				content.getName(),
				content.getDescription(),
				content.getProgramStructure(),
				content.getWebFileurl(),
				content.getUrlType(),
				content.getContentType(),
				content.getId()
		});
		
		if(content.getSessionPlanModuleId() != null && content.getSessionPlanModuleId() != 0) {
			insertResourcesPostTable(content, Integer.parseInt(content.getId()), "update",content.getSubject(),content.getYear(),content.getMonth());		}
		
	}

	public ArrayList<ProgramSubjectMappingBean> getUnAttemptedSubjects(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select r.program, r.sem, ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and ps.subject not in (select subject from exam.passfail where sapid = ?)";

		ArrayList<ProgramSubjectMappingBean> subjectsList = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId, sapId}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));

		return subjectsList;
	}

	public List<ContentBean> getRecordingForLastCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c, exam.examorder eo where subject = ? "
				+ " and c.month = eo.acadMonth and c.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' ";

		//System.out.println("SQL = "+sql);
		List<ContentBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean .class));
		return contents;
	}
	
	@SuppressWarnings("rawtypes")
	public List<AnnouncementBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<AnnouncementBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	//	Added for SAS
	  @SuppressWarnings("rawtypes")
		public List<AnnouncementBean> getAllActiveAnnouncements(String program,String progrmStructure){
			String sql = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";

			}else{
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			}
			
			List<AnnouncementBean> announcements = jdbcTemplate.query(sql, new Object[]{program,progrmStructure}, new BeanPropertyRowMapper(AnnouncementBean.class));

			return announcements;
		}
//		Added for SAS end
	  
		public StudentBean getStudentMaxSemRegistrationData(String sapId) {
			StudentBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select * from exam.registration r where r.sapid = ?  "
						+ "and r.sem = (select max(registration.sem) from exam.registration where sapid = ?) ";

				studentRegistrationData = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentBean .class));
			} catch (Exception e) {
//				System.out.println("getStudentRegistrationData :"+e.getMessage());
			}
			return studentRegistrationData;
		}
		
		public List<ExamOrderBean> getLiveFlagDetails(){
			List<ExamOrderBean> liveFlagList = new ArrayList<ExamOrderBean>();

			final String sql = " Select * from exam.examorder order by examorder.order ";
			jdbcTemplate = new JdbcTemplate(dataSource);

			liveFlagList = (ArrayList<ExamOrderBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderBean>(ExamOrderBean.class));

			return liveFlagList;
		}
		public void saveSessionQA(SessionDayTimeBean sessionQA){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql="insert into acads.session_question_answer(meetingKey,sessionId,sapId,query,answer,status,createdDate) VALUES (?,?,?,?,?,?,sysdate())";
			try {
				jdbcTemplate.update(sql, new Object[] { sessionQA.getMeetingKey(),sessionQA.getSession_id(),sessionQA.getSapId(),sessionQA.getQuestion(),sessionQA.getAnswer(),sessionQA.getIsAnswered()
					});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 
		}

		public String getstudentByEmail(String emailId) {
			StudentBean studentList = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  distinct *  from exam.students  where emailId = ? limit 1"	;
				studentList = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{emailId}, new BeanPropertyRowMapper(StudentBean.class));
				 return studentList.getSapid();
			} catch (Exception e) {
//				System.out.println("getStudentRegistrationData :"+e.getMessage());
				return "";
			}
			
		}
		public SessionBean getSessionByMeetingKey(String id) {
			
			SessionBean sessionDetails = null;
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select s.*,f.facultyId,f.email  from acads.sessions s, acads.faculty f  where meetingKey = ?  and f.facultyId=s.facultyId"	;

				sessionDetails = (SessionBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(SessionBean.class));
				
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			
			return sessionDetails;
			
		}
		
		public ArrayList<SessionBean> getSessionsHeldPerDay() {
			
			ArrayList<SessionBean> sessionsList = new ArrayList<SessionBean>();
			try {
//                System.out.println("calling get sessions..");
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select *  from acads.sessions  where date = DATE(NOW())"	;

				sessionsList = (ArrayList<SessionBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionBean.class));
//				System.out.println("sessionsList:"+sessionsList);
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			
			return sessionsList;
			
		}
		
		public ArrayList<SessionDayTimeBean> getCompletedSessionsOfFacultyForPg(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeBean> scheduledSessionList = new  ArrayList<SessionDayTimeBean>();
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
						"            s.id = q.sessionId AND q.status = 'Open' AND LENGTH(q.query) > 10 ) AS count " + 
						"FROM " + 
						"    acads.sessions s " + 
						"        INNER JOIN " + 
						"    exam.examorder eo ON s.month = eo.acadmonth " + 
						"        AND s.year = eo.year " + 
						"WHERE " + 
						"    s.moduleid IS NULL " + 
						"        AND eo.order IN (SELECT " + 
						"            MAX(examorder.order) " + 
						"        FROM " + 
						"            exam.examorder " + 
						"        WHERE " + 
						"            acadSessionLive = 'Y') " + 
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

				scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{facultyId,facultyId, facultyId, facultyId}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			return scheduledSessionList;
		}
		public ArrayList<SessionDayTimeBean> getCompletedSessionsOfFacultyForMbaWx(	String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<SessionDayTimeBean> scheduledSessionList = new  ArrayList<SessionDayTimeBean>();
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
						"            s.id = q.sessionId AND q.status = 'Open' AND LENGTH(q.query) > 10 ) AS count " + 
						"FROM " + 
						"    acads.sessions s " + 
						"        INNER JOIN " + 
						"    exam.examorder eo ON s.month = eo.acadmonth " + 
						"        AND s.year = eo.year " + 
						"WHERE " + 
						"    s.moduleid IS NOT NULL " + 
						"        AND eo.order IN (SELECT " + 
						"            MAX(examorder.order) " + 
						"        FROM " + 
						"            exam.examorder " + 
						"        WHERE " + 
						"            acadSessionLive = 'Y') " + 
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

				scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{facultyId,facultyId, facultyId, facultyId}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			return scheduledSessionList;
		}
		public List<SessionQueryAnswer> getSingleSessionsQA(String session_id) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q, exam.students s  where q.sessionId = ?   and s.sapId=q.sapId and LENGTH(q.query) > 10"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

		public void updateAnswer(SessionQueryAnswer sessionQn) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "update acads.session_question_answer set "
						+ " answer= ?, "
						+ " status = 'Answered', "
						+ " isPublic = ? ," 
						+ " answeredByFacultyId = ?"
						+ " where id = ? ";
				
				jdbcTemplate.update(sql, new Object[] {sessionQn.getAnswer() ,sessionQn.getIsPublic(), sessionQn.getFacultyId(), sessionQn.getId()});
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 
		}
		public List<SessionQueryAnswer> getStudentQAforSession(String session_id,String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.sapId = ? and LENGTH(q.query) >10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,sapId}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

		public List<SessionQueryAnswer> getPublicQAsforSession(String session_id, String sapId) {
			List<SessionQueryAnswer> sessionQA=new ArrayList<SessionQueryAnswer>();
			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select  *  from acads.session_question_answer q "
						+ "where q.sessionId = ?  and q.isPublic = 'Y' and (q.answer IS NOT NULL AND q.answer != '')  and q.sapId != ? and LENGTH(q.query) > 10 order by q.createdDate desc"	;
				 sessionQA = jdbcTemplate.query(sql, new Object[]{session_id,sapId}, new BeanPropertyRowMapper(SessionQueryAnswer.class));
				 
			} catch (Exception e) {
//				System.out.println("getSessionData :"+e.getMessage());
			}
			return sessionQA;
		}

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
			
//			 System.out.println("In getConsumerProgramStructureIdsBySubjectAndProgramStructure sql-\n"+sql);
			   
			
			List<String> data = new ArrayList<>();
			try {
				data = (List<String>)jdbcTemplate.query(sql,new Object[] {subject}, new SingleColumnRowMapper(String.class));
//				 System.out.println("list----"+data);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			 return data;
		}

		public String batchInsertCententIdAndMasterKeyMappings(final List<ContentBean> cententIdAndMasterKeyMappings) {
//			System.out.println("Enterred batchInsertCententIdAndMasterKeyMappings()");
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
				if(cententIdAndMasterKeyMappings !=null) {
//					System.out.println("IN batchUpdate cententIdAndMasterKeyMappings size : \n"+cententIdAndMasterKeyMappings.size());
				}
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						ContentBean m = cententIdAndMasterKeyMappings.get(i);
						ps.setString(1, m.getId());
						ps.setString(2, m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						ps.setString(4, m.getCreatedBy());
						
						ps.setString(5, m.getCreatedBy());
						if(i % 500 == 0) {
//							System.out.println("IN batchUpdate processsed :"+i);
						}
					}
					public int getBatchSize() {
						return cententIdAndMasterKeyMappings.size();
					}
				});
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorMessage += e.getMessage();
			}
			
//			System.out.println("Exit batchInsertCententIdAndMasterKeyMappings()");
			return errorMessage;
		}
		
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(final long contentId,final List<ContentBean> consumerProgramStructureIds){
			try {
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
//				System.out.println("batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds : "+batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds.toString());
				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Error in batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds : "+e.getMessage();
			}
		
		}

		
		public String batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(final long contentId,final List<ContentBean> consumerProgramStructureIds){
			try {
				String sql =  " INSERT INTO acads.contentid_consumerprogramstructureid_mapping_forLeads "
							+ " (contentId, consumerProgramStructureId,programSemSubjectId, createdDate) "
							+ " VALUES (?,?,?,sysdate()) ";
				
				int[] batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						
						ContentBean m = consumerProgramStructureIds.get(i);
						ps.setLong(1,contentId);
						ps.setString(2,m.getConsumerProgramStructureId());
						ps.setString(3, m.getProgramSemSubjectId());
						
					}

					@Override
					public int getBatchSize() {
						return consumerProgramStructureIds.size();
					}
				  });
//				System.out.println("batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads : "+batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads.toString());
				return "";
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Error in batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads : "+e.getMessage();
			}
		
		}
		
		
		public String batchInsertOfMakeContentLiveConfigs(final ContentBean searchBean,
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
//				System.out.println("batchInsertOfMakeContentLiveConfigs "+batchInsertOfMakeContentLiveConfigs);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				return "Error in creating configurations , Error : "+e.getMessage();
			}
			return errorMessage;
		}
		
		public List<ContentBean> getContentLiveConfigList(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentLiveConfigList = new ArrayList<>();
			
			String sql =  "select cls.year,cls.month,p.code as program,"
					+ "p_s.program_structure as programStructure,c_t.name as consumerType "
					+ "from exam.content_live_settings as cls "
					+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = cls.consumerProgramStructureId "
					+ "left join exam.program as p on p.id = c_p_s.programId "
					+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
					+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId";
			
			try {
				contentLiveConfigList = (List<ContentBean>) jdbcTemplate.query(sql, 
						new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return contentLiveConfigList;  
			
		}
		
		public Map<String,Integer> getContentIdNCountOfProgramsApplicableToMap(String ids) {
			jdbcTemplate = new JdbcTemplate(dataSource);
//			System.out.println("In getContentIdNCountOfProgramsApplicableToMap ids :"+ids);
			String sql = "SELECT  " + 
					"    count(c.id) as countOfProgramsApplicableTo ,c.id " + 
					"FROM " + 
					"    acads.content c, " + 
					"    acads.contentid_consumerprogramstructureid_mapping ccm " + 
					"WHERE " + 
					"    c.id = ccm.contentId " + 
					"    AND c.id in ("+ids+") " + 
					"GROUP BY c.id  " + 
					"ORDER BY c.id DESC ";
					try {
				List<ContentBean> list = (List<ContentBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContentBean.class));
				Map<String,Integer> map = new HashMap<>();
				for(ContentBean bean : list) {
					map.put(bean.getId(), bean.getCountOfProgramsApplicableTo());
				}
			
				return map;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		public List<ContentBean> getProgramsListForCommonContent(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentList=new ArrayList<>();
//			System.out.println("In getProgramsListForCommonContent got id : "+id);
			String sql="SELECT   " + 
					"    c.id,c.year,c.month,c.subject,"
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
					"        AND c.id = ?  " + 
					"   " + 
					"ORDER BY c.id DESC";
			
			try {
//				 System.out.println("IN getProgramsListForCommonContent sql : \n"+sql);
				 contentList = (List<ContentBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(ContentBean.class));
				 
				 if(contentList != null)
//				 System.out.println("IN getProgramsListForCommonContent contentList size : "+contentList.size());
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contentList;
		}

		public List<ContentBean> getContentsListWithMasterKeyDetailsBySubject(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> contentList=new ArrayList<>();
//			System.out.println("In getProgramsListForCommonContent got subject : "+subject);
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
//				 System.out.println("IN getProgramsListForCommonContent sql : \n"+sql);
				 contentList = (List<ContentBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contentList;
		}

		public int deleteContentIdMasterkeyMappingsById(String id) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				int deletedRows =  jdbcTemplate.update(sql, new Object[] {id});
				return deletedRows;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
		}
		public int deleteContentIdMasterkeyMappingsByIdNMasterKey(String id,String consumerProgramStructureId) {
			String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				int deletedRows =  jdbcTemplate.update(sql, new Object[] {id,consumerProgramStructureId});
//				System.out.println("In deleteContentIdMasterkeyMappingsByIdNMasterKey deletedRows : "+deletedRows);
				return deletedRows;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
		}
		

		public int getCountOfProgramsContentApplicableToById(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
//			System.out.println("In getCountOfProgramsContentApplicableTo id :"+id);
			String sql = " SELECT  " + 
						 "   	count(*)  " + 
						 " FROM " + 
						 "    acads.contentid_consumerprogramstructureid_mapping ccm " + 
						 " WHERE " +  
						 "    ccm.contentId = "+id+" " ;
			try {
				int count = (int) jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper(Integer.class));
//				System.out.println("In getCountOfProgramsContentApplicableToById got count : "+count);
				return count;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
		
		//System generated learning resources (insert data into posts table)
		//start
		
		public void insertResourcesPostTable(ContentBean bean,long id,String type,String subject,String year,String month) {
//			System.out.println("++++++++  in insertResourcesPostTable : EMBA post acad conent data entry   ++++++++++++++++");
//			System.out.println("post subject-->"+bean.getSubject());
			Posts posts = new Posts();
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
//			System.out.println("bean------->"+bean);
			posts.setHashtags(subject+","+bean.getSessionPlanModuleName());
//			System.out.println("hashtags-->"+posts.getHashtags());
			
			if (timeBoundIds.size() != 0) {
				if(type=="insert") {
					posts.setCreatedBy(bean.getCreatedBy());
					posts.setLastModifiedBy(bean.getCreatedBy());		
					posts.setVisibility(1);
					
					for(Integer programSemSubjectIds : timeBoundIds) {
//						System.out.println("programSemSubjectId----- " + programSemSubjectIds);
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
			} else {
//				System.out.println("No need to add in Post table.");
			}

		}
		
		public List<ContentBean> getconsumerProgramStructureIdList(String subject,String programStructure, Long moduleId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ContentBean> consumerProgramStructureIdList = new ArrayList<ContentBean>();
			
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
			
				consumerProgramStructureIdList = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentBean.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return consumerProgramStructureIdList;
		}

		public List<Integer> getTimeBoundIdEMBABySessionPlan(long sessionPlanId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT t.timeboundId FROM acads.sessionplanid_timeboundid_mapping t,acads.sessionplan_module m where t.sessionPlanId = m.sessionPlanId  and" + 
					"   m.id =? ";
			
			List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,new Object[] { sessionPlanId }, new SingleColumnRowMapper(Integer.class));
//			System.out.println("timeBound id list : " + id);
//			System.out.println("sessionPlanId  : " + sessionPlanId);
			return id;
		}

				public void insertIntoPostProgSemSubEMBAResource(Integer programSemSubjectId, Integer postsId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
					jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

				}

				public int insertIntoPostsTableEMBAResource(final Posts bean,final int timeBoundId) {
//					   System.out.println("before insert in post table");
					jdbcTemplate = new JdbcTemplate(dataSource);
					KeyHolder holder = new GeneratedKeyHolder();
			  
					final String sql = " INSERT INTO lti.post ( userId, role, type, content, fileName,subject_config_id, filePath,fileType, referenceId, "
							 + " session_plan_module_id, subject, contentType, visibility, acadYear, acadMonth,scheduledDate, scheduleFlag,hashtags, "
							 + " createdBy, createdDate, lastModifiedBy, lastModifiedDate ) "
							 + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,?,?,sysdate(),?,sysdate()) ";

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
							ps.setString(16, "Y");    
							ps.setString(17, bean.getHashtags()); 
							ps.setString(18, bean.getCreatedBy());
							ps.setString(19, bean.getLastModifiedBy());
							return ps;
						}
					}, holder);

					int postId = holder.getKey().intValue();

					return postId;
				}
				
				public void updateResourcePostTable(final Posts bean) {
					jdbcTemplate = new JdbcTemplate(dataSource);
				
			  
					 String sql = "UPDATE lti.post SET "
							+ "userId = ?, "
							+ "role = ?, "
							+ "type = ?, "
							+ "content = ?, "
							+ "contentType = ?, "
							+ "fileName = ?, "
							+ "filePath = ?, "
							+ "referenceId = ?, "
							+ "lastModifiedBy = ?, "
							+ "lastModifiedDate = sysdate() "
							+ "WHERE referenceId = ? order by lastModifiedDate desc limit 1";

					jdbcTemplate.update(sql, new Object[] {
					bean.getUserId(),
					bean.getRole(),
					bean.getType(),
					bean.getContent(),
					bean.getFileName(),
					bean.getFilePath(),
					bean.getReferenceId(),
					bean.getLastModifiedBy(),
					bean.getReferenceId()
					
					
					});
				}

		//delete post from post table
		public void deleteContentFromPost(String id) {
			String sql = "Delete from lti.post where post_id = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql, new Object[] { 
					id
			});
		}

		//for inserting data into post table for MBA-WX
		//start
		
		public void insertRecordingsPostEMBA(VideoContentBean bean,int id,String type) {
//			System.out.println("in ContentDao insertRecordingsPostEMBA Called.");
			
			Posts posts = new Posts();
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
			//System.out.println("IN bean.getSubject().replace(\"_SesssionPlan_Video\", \"\") : ");
			//System.out.println(bean.getSubject().replace("_SesssionPlan_Video", ""));
			//List<Integer> timeBoundIds = getTimeBoundIdEMBA(bean.getSubject().replace("_SesssionPlan_Video", ""));
			if(type=="insert") {
				long timeBoundId = getTimeBoundIdByModuleId(bean.getSessionPlanModuleId());
				if (timeBoundId != 0) {
					posts.setSubject_config_id(timeBoundId);
					posts.setSession_plan_module_id(bean.getSessionPlanModuleId());
					int post_id = insertIntoPostsTableEMBA(posts);
					//insert post into Redis
					posts.setPost_id(post_id+"");
					//insertToRedis(posts);  
					//refreshRedis(posts);
				}
			}else {
				
				try {
					posts.setLastModifiedBy(bean.getLastModifiedBy());
					updateSessionPostTable(posts);
					
				} catch (Exception e) {
				} 
				
			}
			//refresh cache on insert/update.
			refreshRedis(posts);
			
			
			
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
		    	  System.out.println("IN savePostInRedisToCache() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
				    
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
	public String deleteFromRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
		  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";
		    	  System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}*/
		public String refreshRedis(Post posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
				 String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
//		    	 System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
		public String refreshRedis(Posts posts) {
			RestTemplate restTemplate = new RestTemplate();
			try {
				posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()+""));
				 String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
//		    	  System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
				HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
				  
				  return restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, String.class).getBody();
			} catch (RestClientException e) {
				e.printStackTrace();
				return "Error IN rest call got "+e.getMessage();
			}
		}
				public List<Integer> getTimeBoundIdEMBA(String subject) {
					jdbcTemplate = new JdbcTemplate(dataSource);
					//String sql = "SELECT id FROM exam.program_sem_subject where subject=? ";
					String sql =  " select id from lti.student_subject_config where prgm_sem_subj_id in "
								+ " (SELECT id FROM exam.program_sem_subject where subject= ? and studentType = 'TimeBound') "
								+ " and acadMonth = '" + CURRENT_ACAD_MONTH + "' and acadYear = "+ CURRENT_ACAD_YEAR +" ";
					
					List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,
							new Object[] { subject },
							new SingleColumnRowMapper(Integer.class));
//					System.out.println("list----" + id);
					return id;
				}

				public void insertIntoPostProgSemSubEMBA(Integer programSemSubjectId, Integer postsId) {
					jdbcTemplate = new JdbcTemplate(dataSource);

					String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
					jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

				}

				public int insertIntoPostsTableEMBA(final Posts bean) {
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
					} catch (Exception e) {
						e.printStackTrace();
					}
//					System.out.println("in insertIntoPostsTableEMBA -> postId : "+postId);
					return postId;
				}
				
				public void updateSessionPostTable(final Posts bean) {
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
				}
		//end Insert into post table

	public long getTimeBoundIdByModuleId(long moduleId) {
		
//		System.out.println("moduleId : "+moduleId);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT timeboundId FROM acads.sessionplanid_timeboundid_mapping "
					+ " where sessionPlanId = (SELECT sessionPlanId FROM acads.sessionplan_module where id = ? ) ";
		try {
			int id =  jdbcTemplate.queryForObject(sql, new Object[] {moduleId}, new SingleColumnRowMapper(Integer.class));
			Long timeBoundId = Long.valueOf(id);
			return timeBoundId;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
		
	public ELearnResourcesBean isStukentApplicable(String userId) {
		ELearnResourcesBean eLearnResourcesBean = new ELearnResourcesBean();
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
		 eLearnResourcesBean =  jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper (ELearnResourcesBean.class) );
		
		 return eLearnResourcesBean;
		}catch(Exception e) {
			e.printStackTrace();
			return eLearnResourcesBean;
		}
	}
	
	public ELearnResourcesBean isHarvardApplicable(String userId) {
		ELearnResourcesBean eLearnResourcesBean = new ELearnResourcesBean();
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
		 eLearnResourcesBean =  jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(ELearnResourcesBean.class) );
		 return eLearnResourcesBean;
		}catch(Exception e) {
			e.printStackTrace();
			return eLearnResourcesBean;
		}
	}
	
	public ArrayList<ConsumerProgramStructure> getConsumerTypes() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT id,name FROM exam.consumer_type order by id asc ";
		ArrayList<ConsumerProgramStructure> consumerTypeList = (ArrayList<ConsumerProgramStructure>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
		return consumerTypeList;

	}
	
	public Page<ContentBean> getResourcesContent(int pageNo, int pageSize, ContentBean searchBean, String searchOption){
		
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
		
		sql = sql + " FROM acads.content c " + 
					" LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " +
				    " LEFT JOIN exam.consumer_program_structure cps ON cps.id = ccm.consumerProgramStructureId " + 
				    " LEFT JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				    " LEFT JOIN exam.program p ON p.id = cps.programId " + 
				    " LEFT JOIN exam.program_structure ps ON ps.id = cps.programStructureId " + 
				    " LEFT JOIN exam.program_sem_subject pss ON pss.subject = c.subject AND pss.consumerProgramStructureId = cps.id " + 
				    " WHERE 1 = 1 ";
		
		countSql = countSql + " FROM acads.content c " + 
							  " LEFT JOIN acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " + 
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
//		System.out.println("In getResourcesContent sql = " + sql);
//		System.out.println("In getResourcesContent Countsql = " + countSql);
//		System.out.println("In getResourcesContent parameters = " +parameters );
		
		PaginationHelper<ContentBean> pagingHelper = new PaginationHelper<ContentBean>();
		Page<ContentBean> page = pagingHelper.fetchPage(jdbcTemplate,countSql, sql, args, pageNo, pageSize,
														new BeanPropertyRowMapper(ContentBean.class));
		return page;
		
	}
	
	public ArrayList<ContentBean> getCommonGroupProgramList(ContentBean bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ContentBean> commonGroupList = new ArrayList<ContentBean>();
		try {
			String sql =" SELECT  " + 
						"   c.*, " + 
						"   p.code AS program, cps.programId, " + 
						"   ps.program_structure AS programStructure, cps.programStructureId, " + 
						"  	ct.name AS consumerType, cps.consumerTypeId, " + 
						" 	consumerProgramStructureId " + 
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
						"		AND c.year = ? " + 
						"       AND c.month = ? " + 
						"       AND subject = ? " +
						"		AND filePath = ? " +
						"		AND consumerProgramStructureId in ("+ bean.getConsumerProgramStructureId() +")";
			
			commonGroupList = (ArrayList<ContentBean>) jdbcTemplate.query(sql, new Object[] {bean.getYear(), bean.getMonth(), bean.getSubject(), bean.getFilePath()},
							  new BeanPropertyRowMapper(ContentBean.class));
//			System.out.println("getCommonGroupProgramList Query : " +sql);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
//		System.out.println("commonGroupList "+commonGroupList);
		return commonGroupList;
	}

	public void deleteContentById(String id) {
		String sql = " Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {id});
	}
	
	public int deleteContentIdConsumerPrgmStrIdMapping(String contentId, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;
		String sql = "Delete from acads.contentid_consumerprogramstructureid_mapping where contentId = ? and consumerProgramStructureId = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] {contentId, consumerProgramStructureId});
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return row;
	}

	public boolean ifStudentAlreadyRegisteredForNextSem(String sapid,ReRegistrationBean activeRegistration) {
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
			e.printStackTrace();
		} 
		return registered;
 	}
	
	public ArrayList<String> getProgramStrutureList() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> programStrutureList = new ArrayList<String>();

		try {
			String sql = "SELECT program_structure FROM exam.program_structure ";
			programStrutureList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{} ,new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return programStrutureList;
	}
	
	public FacultyBean isFaculty(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? group by facultyId";
		FacultyBean faculty = (FacultyBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(FacultyBean.class));
		return faculty;
	}
	
	public int getPssIdBySubject(String subject, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int pssId = 0;
		String sql = "SELECT id FROM exam.program_sem_subject WHERE subject = ? AND consumerProgramStructureId = ? " ;
		try {
			pssId = (int) jdbcTemplate.queryForObject(sql,new Object[] {subject, consumerProgramStructureId}, new SingleColumnRowMapper(Integer.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pssId;
	}
	
	public ArrayList<ProgramSubjectMappingBean> getWaivedInSubjects(StudentBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,subject from exam.program_sem_subject where consumerProgramStructureId = ? and "
				+ " subject not in ( select if(subject = 'Business Communication and Etiquette','Business Communication',subject) as subject from exam.passfail where sapid in (?,?) and isPass = 'Y') and sem < ? ";
		return (ArrayList<ProgramSubjectMappingBean>) jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId(), student.getPreviousStudentId(), student.getSapid(), student.getSem()}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
	}

	public ArrayList<ProgramSubjectMappingBean> getAllPreviousSubjectsForSapid(StudentBean student) {
		// Check if this sapid is lateral as well.
		boolean studentLateral = "Y".equals(student.getIsLateral());		
		ArrayList<ProgramSubjectMappingBean> clearedSubjectsList = getWaivedInSubjects(student);
		if(studentLateral) {
			// If lateral, get the subjects cleared for this sapid and check if it was lateral; repeat
			StudentBean previousStudent = getSingleStudentsData(student.getPreviousStudentId());
			ArrayList<ProgramSubjectMappingBean> clearedSubjectsForPreviousStudentNumber = getAllPreviousSubjectsForSapid(previousStudent);
			clearedSubjectsList.addAll(clearedSubjectsForPreviousStudentNumber);
		}
		return clearedSubjectsList;
	}
	
	//Copied for LiveSessionAccess
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
}
>>>>>>> branch 'feature/liveSessionAccess' of https://ngasce@bitbucket.org/ngasce/acads.git
