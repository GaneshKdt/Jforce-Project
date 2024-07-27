package com.nmims.daos;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.transaction.annotation.Transactional;


import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.util.ContentUtil;


public class ContentDAO extends BaseDAO{
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	@Qualifier("slave1")
	private DataSource slaveDataSource;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String acadMonth;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String acadYear;
	

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
		String sql = "select distinct subject from exam.program_subject where prgmStructApplicable = 'Jul2014' or "
				+ " prgmStructApplicable = 'Jul2009' or prgmStructApplicable = 'Jul2013' or prgmStructApplicable = 'Jul2017' order by subject";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		
		
		
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
	public StudentStudentPortalBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentStudentPortalBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";


			////System.out.println("SQL = "+sql);

			ArrayList<StudentStudentPortalBean> studentList = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			
			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);

				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			
			////System.out.println("STUDENT :"+student);
			return student;
		}catch(Exception e){
			
			//System.out.println("Acads App:No student information foud for this user. Must be Admin!");
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
	public StudentStudentPortalBean getStudentRegistrationData(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
			//System.out.println("getStudentRegistrationData :"+e.getMessage());
		}
		return studentRegistrationData;
	}
	
	@Transactional(readOnly = true)
	public UserAuthorizationStudentPortalBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationStudentPortalBean user = (UserAuthorizationStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationStudentPortalBean.class));
			return user;
		} catch (Exception e) {
			//System.out.println("getUserAuthorization :"+e.getMessage());
		}

		return null;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAuthorizedCenterCodes(UserAuthorizationStudentPortalBean userAuthorization) {
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
		
		////System.out.println("Acads Portal: Authorized centers = "+centers);
		return centers;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getFacultySubjects(String facultyId) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select fc.* from acads.faculty_course fc, exam.examorder eo where fc.facultyId = ? and  fc.month = eo.acadMonth "
					+ " and fc.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			programSubjectMappingList = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{facultyId},
					new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));
		} catch (Exception e) {
			
		}
		return programSubjectMappingList;
	}
	
	/*@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoContent(final List<VideoContentBean> videoContentList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				upsertVideoContent(bean, jdbcTemplate);
				////System.out.println("Upserted row "+i);
			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;

	}*/
	// batchUpdateVideoTopic Start
	/*@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateVideoTopic(final List<VideoContentBean> videoContentList) {

		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		
		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentBean bean = videoContentList.get(i);
				long key = dao.saveVideoSubTopic(bean);
				//System.out.println(key + "<== key For Added Video Topic");
						
			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	// batchUpdateVideoTopic End
	
	private void upsertVideoContent(VideoContentBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.video_content(year, month, subject, fileName, facultyId, keywords , description , defaultVideo, sessionDate, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate, videoLink, addedOn, thumbnailUrl, mobileUrlHd, mobileUrlSd1, mobileUrlSd2, sessionId) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,?,?,?,?,?,?)"
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

		String year = bean.getYear();
		String month = bean.getMonth();
		String subject = bean.getSubject();
		String fileName = bean.getFileName();
		String keyWords = bean.getKeywords();
		String description = bean.getDescription();
		String defaultVideo = bean.getDefaultVideo();
		String createdBy = bean.getCreatedBy();  
		String videoLink = bean.getVideoLink();  
		String mobileUrlHd = bean.getMobileUrlHd();  
		String mobileUrlSd1 = bean.getMobileUrlSd1();  
		String mobileUrlSd2 = bean.getMobileUrlSd2();
		String facultyId =bean.getFacultyId();
		String sessionDate= bean.getSessionDate();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
		String addedOn = sdf.format(new Date());
		String thumbnailUrl=bean.getThumbnailUrl();
		Integer sessionId = bean.getSessionId();
		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				fileName,
				facultyId,
				keyWords,
				description,
				defaultVideo,
				sessionDate,
				createdBy,
				createdBy, 
				videoLink,
				addedOn,
				thumbnailUrl,
				mobileUrlHd,
				mobileUrlSd1,
				mobileUrlSd2,
				sessionId,
				
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

	}
	*/
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
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject order by program, sem, subject";

		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingStudentPortalBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));
		return programSubjectMappingList;
	}
	
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjects(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content where subject = ? ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjectsForCurrentSession(String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and "
				+ " c.year = eo.year where subject = ? "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContents(ContentStudentPortalBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where subject = ? and year = ? and month = ? ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{searchBean.getSubject(), searchBean.getYear(), searchBean.getMonth()}, 
				new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForIds(ContentStudentPortalBean searchBean) {
		String contentIds = StringUtils.join(searchBean.getContentToTransfer(), ',');
		////System.out.println("contentIds = "+contentIds);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.content c where id in ( " + contentIds + ")";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{}, 
				new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForMultipleSubjects(ArrayList<String> subjectList) {
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

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<ProgramSubjectMappingStudentPortalBean> subjectsList = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));

		System.out.println("Subject List In Dao Msc"+subjectsList);
		return subjectsList;
	}

	@Transactional(readOnly = false)
	public void saveContentDetails(ContentStudentPortalBean bean, String subject, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO acads.content (year, month, subject, name, description, "
				+ " filePath, previewPath, webFileurl, urlType, contentType, programStructure, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


		String description = bean.getDescription();
		String filePath = bean.getFilePath();
		String previewPath = bean.getPreviewPath();
		String webFileurl = bean.getWebFileurl();
		String name = bean.getName();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String urlType = bean.getUrlType();
		String programStructure = bean.getProgramStructure();
		String contentType = bean.getContentType();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				name,
				description,
				
				filePath,
				previewPath,
				webFileurl,
				urlType,
				contentType,
				programStructure,
				createdBy,
				lastModifiedBy,
		});
		
		////System.out.println("INserted...");
		
	}

	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentFiles(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where subject = ? ";
		
		List<ContentStudentPortalBean> contentFiles = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));
		return contentFiles;
	}

	@Transactional(readOnly = false)
	public void deleteContent(String id) {
		String sql = "Delete from acads.content where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});
		
	}

	@Transactional(readOnly = true)
	public ContentStudentPortalBean findById(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.content where id = ? ";
		
		ContentStudentPortalBean content = (ContentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));
		return content;
	}

	@Transactional(readOnly = false)
	public void updateContent(ContentStudentPortalBean content) {
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
		
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getUnAttemptedSubjects(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select r.program, r.sem, ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and ps.subject not in (select subject from exam.passfail where sapid = ?)";

		ArrayList<ProgramSubjectMappingStudentPortalBean> subjectsList = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId, sapId}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));

		return subjectsList;
	}

	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getRecordingForLastCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and "
				+ " c.year = eo.year where subject = ? "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<AnnouncementStudentPortalBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<AnnouncementStudentPortalBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	//added to check user in auth:START
	@Transactional(readOnly = true)
		public ArrayList<StudentStudentPortalBean> getAllUserList() {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT distinct auth.userId FROM portal.user_authorization auth ";
				ArrayList<StudentStudentPortalBean> userList = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
			//System.out.println("userList sql "+userList);
			return userList;
		}
		
	@Transactional(readOnly = true)
		public boolean checkUserExists(String userId) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT distinct auth.userId FROM portal.user_authorization auth where auth.userId=? ";
				String userData = (String)jdbcTemplate.queryForObject(sql, new Object[]{userId}, new SingleColumnRowMapper(String .class));
				return true;
			}
			catch(Exception e ) {
				
			}
			return false;
		}
		//END
		
	@Transactional(readOnly = true)
		public ArrayList<VideoContentStudentPortalBean> getSessionOnHome(ArrayList<Integer> currentSemPSSId, StudentStudentPortalBean studentRegistrationForAcademicSession) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String pssIdCommaSeparated = "''";
			
			if(currentSemPSSId==null) {
				pssIdCommaSeparated = "''";
			}else {
				for (int i = 0; i < currentSemPSSId.size(); i++) {
					if (i == 0) {
						pssIdCommaSeparated = "'"+ currentSemPSSId.get(i) + "'";
					} else {
						pssIdCommaSeparated = pssIdCommaSeparated + ", '"+ currentSemPSSId.get(i) + "'";
					}
				}
			}
			
			String acadYearMonth = studentRegistrationForAcademicSession.getMonth()+studentRegistrationForAcademicSession.getYear();

			String sql="SELECT " + 
					"    s.track AS track, " + 
					"    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    v.description, " + 
					"    v.subject, " + 
					"    v.sessionDate, " + 
					"    v.thumbnailUrl, " + 
					"    v.id " + 
					" FROM " + 
					"    acads.video_content v " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON f.facultyId = v.facultyId " + 
					"        INNER JOIN " + 
					"    acads.sessions s ON s.id = v.sessionId " + 
					"        INNER JOIN " + 
					"	 acads.session_subject_mapping ssm on s.id = ssm.sessionId" +
					" WHERE " + 
					"     ssm.program_sem_subject_id IN ("+pssIdCommaSeparated+") " + 
					"        AND CONCAT(v.month, v.year) IN ('"+acadYearMonth+"') " + 
					" ORDER BY v.createdDate DESC " + 
					" LIMIT 4";

		ArrayList<VideoContentStudentPortalBean> videoList = (ArrayList<VideoContentStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		return videoList;
	}
		
	/**
	 * @param passIds - contains comma separated program sem subject id's.
	 * @param acadDateFormat - contains academic cycle in YYYY-MM-DD format. 
	 * @return List - return the video contents list for the applicable PSS id's and academic cycle.
	 * */
	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getSessionRecordingOnHome(String passIds, String acadDateFormat) {
		StringBuilder GET_VIDEOS_BY_PSS = null;
		List<VideoContentStudentPortalBean> videoList = null;
		
		// Inject DataSource object to JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(slaveDataSource);

		// Create StringBuilder object
		GET_VIDEOS_BY_PSS = new StringBuilder();

		//Create empty array list
		videoList = new ArrayList<VideoContentStudentPortalBean>();
		
		// Prepare SQL Query
		GET_VIDEOS_BY_PSS.append("SELECT track, CONCAT('Prof. ', firstName, ' ', lastName) AS facultyName, ");
		GET_VIDEOS_BY_PSS.append("description, subject, sessionDate, thumbnailUrl, id, program_sem_subject_id as programSemSubjectId FROM acads.quick_video_content ");
		GET_VIDEOS_BY_PSS.append("WHERE  program_sem_subject_id IN (" + passIds + ") ");
		GET_VIDEOS_BY_PSS.append("AND acadDateFormat = '"+acadDateFormat+"' ");
		
		try {
			// Execute JdbTemplate method
			videoList = jdbcTemplate.query(GET_VIDEOS_BY_PSS.toString(),new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			
		}catch (Exception e) {
			
		}
		
		// return videoList
		return videoList;
	}// getSessionRecordingOnHome()
		
	@Transactional(readOnly = true)
		public ArrayList<String> getProgramSemSubjectId(ArrayList<String> subjects,String consumerProgramStructureId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String subjectCommaSeparated = "''";
			for (int i = 0; i < subjects.size(); i++) {
				if(i == 0){
					subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			
			String sql =" SELECT id " + 
						"    FROM exam.program_sem_subject " + 
						" WHERE " + 
						"    subject IN (" + subjectCommaSeparated + ") " + 
						"        AND consumerProgramStructureId = ? ";
			
			ArrayList<String> programSemSubjectIdList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{consumerProgramStructureId}, 
																new SingleColumnRowMapper(String.class));

			return programSemSubjectIdList;
			
		}
	
	@Transactional(readOnly = true)
		public boolean checkIfEntryInPassfail(String sapid,int sem) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT * from exam.passfail where sem=? and sapid=? ";
				ArrayList<PassFailBean> passfailBean = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{sem,sapid}, new BeanPropertyRowMapper(PassFailBean.class));
				if(passfailBean.size()>0) {
					return true;
				} 
			}catch(Exception e ) {
				
			}
			return false;
		}
		
	@Transactional(readOnly = true)
		public ArrayList<VideoContentStudentPortalBean> getSessionsByCourseMapping(String userId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();
			
			String sql =" SELECT s.track AS track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " +
						" v.description, v.subject, v.sessionDate, v.thumbnailUrl, v.id " +
						"	FROM exam.student_course_mapping scm " + 
						"		INNER JOIN " +
						"			exam.examorder eo ON scm.acadMonth = eo.acadMonth AND scm.acadYear = eo.year " + 
						"		INNER JOIN " +
						"			acads.sessions s ON eo.acadMonth = s.month AND eo.year = s.year " + 
						"    	INNER JOIN " +
						"			acads.session_subject_mapping ssm on s.id = ssm.sessionId and scm.program_sem_subject_id = ssm.program_sem_subject_id " + 
						"    	INNER JOIN " +
						"			acads.video_content v on s.id = v.sessionId " + 
						"    	INNER JOIN " +
						"			acads.faculty f on v.facultyId = f.facultyId " + 
						"		WHERE role = 'Student'" + 
						"			AND eo.order = (SELECT MAX(eo.order) FROM  exam.examorder eo WHERE acadSessionLive = 'Y') "+
						"			AND userId = ? " +
						"		ORDER BY v.createdDate DESC " +
						" 		LIMIT 4";
			try {
				videoList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {userId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return videoList;
		}
		
		@Transactional(readOnly = true)
		public List<ContentStudentPortalBean> getBookmarksOfContent(String ContentIds, String sapId) {
			
			 namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			List<ContentStudentPortalBean> ContentsList = new ArrayList<ContentStudentPortalBean>();
			
			String sql ="select content_id as id,bookmarked from bookmarks.content_bookmarks where content_id in (:ContentIds) and sapid = :sapid";
			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("ContentIds", ContentIds);
		    queryParams.addValue("sapid", sapId);
		    
			try {
			
				ContentsList = (ArrayList<ContentStudentPortalBean>) namedParameterJdbcTemplate.query(sql, queryParams,
		                new BeanPropertyRowMapper<ContentStudentPortalBean>(ContentStudentPortalBean.class));
			} catch (Exception e) {
						
			}
			return ContentsList;
		}
		
		@Transactional(readOnly = true)
		public List<VideoContentStudentPortalBean> getBookmarksForVideo(String ContentIds, String sapId) {

			 namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			List<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();

			String sql ="select content_id as id,bookmarked from bookmarks.content_bookmarks where content_id in (:ContentIds) and sapid = :sapid";
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("ContentIds", ContentIds);
		    queryParams.addValue("sapid", sapId);
			try {
//				videoList =jdbcTemplate.query(sql, new Object[] {sapId},
//						new BeanPropertyRowMapper<VideoContentBean>(VideoContentBean.class));
				videoList = (ArrayList<VideoContentStudentPortalBean>) namedParameterJdbcTemplate.query(sql, queryParams,
		                new BeanPropertyRowMapper<VideoContentStudentPortalBean>(VideoContentStudentPortalBean.class));
			} catch (Exception e) {
						
			}
			return videoList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<ProgramSubjectMappingStudentPortalBean> getFailSubjectsForaLateralStudent(StudentStudentPortalBean student) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and (sapid = ? or sapid=?) order by sem  asc ";

			ArrayList<ProgramSubjectMappingStudentPortalBean> subjectsList = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{student.getSapid(),student.getPreviousStudentId()}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));

			return subjectsList;
		}
		
		@Transactional(readOnly = true)
		public boolean checkIfEntryInPassfailLateral(String sapid,String prevSapid,int sem) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT * from exam.passfail where sem=? and (sapid=? or sapid=?) ";
				ArrayList<PassFailBean> passfailBean = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{sem,sapid,prevSapid}, new BeanPropertyRowMapper(PassFailBean.class));
				if(passfailBean.size()>0) {
					return true;
				} 
			}catch(Exception e ) {
				
			}
			return false;
		}
				
}
