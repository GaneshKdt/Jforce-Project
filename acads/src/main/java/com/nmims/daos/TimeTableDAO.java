package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import org.springframework.jdbc.core.ResultSetExtractor;

import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AcadsCalenderBean;
import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.EndPointBean;
import com.nmims.beans.EventBean;
import com.nmims.beans.ExamBookingTransactionAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAvailabilityBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.MailAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.Post;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.RecordingStatus;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.SessionReviewBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TimetableAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.helpers.PaginationHelper;
import com.nmims.helpers.ZoomManager;
import com.nmims.util.ContentUtil;

@Repository("timeTableDAO")
public class TimeTableDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;
	private static HashMap<String, BigDecimal> orderMap = null;
	private ArrayList<String> commonGroup1SubjectList = null;
	private ArrayList<String> commonGroup2SubjectList = null; 

	@Autowired
	private ZoomManager zoomManger;
	
	@Autowired
	private ConferenceDAO conferenceBookingDAO;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${CURRENT_ACAD_MONTH_SAS}" )
	private String CURRENT_ACAD_MONTH_SAS;
	
	@Value( "${CURRENT_ACAD_YEAR_SAS}" )
	private String CURRENT_ACAD_YEAR_SAS;

	@Value( "${MAX_WEBEX_USERS}" )
	private int MAX_WEBEX_USERS2;
	private int MAX_WEBEX_USERS=1000;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	private static final Logger loggerForSessionScheduling = LoggerFactory.getLogger("sessionSchedulingService");
	
	private String sessionBufferLowerLimit = "-00:25:00";
	private String sessionBufferUpperLimit = "02:25:00";

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void endTransaction(boolean activity) {
		if(activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}

	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}

	@Transactional(readOnly = true)
	public HashMap<String, BigDecimal> getExamOrderMapper(){

		if(orderMap == null || orderMap.size() == 0){

			final String sql = " Select * from exam.examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderAcadsBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
			orderMap = new HashMap<String, BigDecimal>();
			for (ExamOrderAcadsBean row : rows) {
				orderMap.put(row.getMonth()+row.getYear(), BigDecimal.valueOf(Double.parseDouble(row.getOrder())));
			
				orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			}
		}
		return orderMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderAcadsBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
			hashMap = new HashMap<String, Integer>();
			for (ExamOrderAcadsBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear(), Integer.valueOf(row.getOrder()));
			}
		}
		return hashMap;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}


	public ArrayList<String> getGroup1SubjectList(){
		if(this.commonGroup1SubjectList == null){
			this.commonGroup1SubjectList = getGroupSubjects("G1");
		}
		return commonGroup1SubjectList;
	}
	
	@Transactional(readOnly = true)
	public double getExamOrderFromAcadMonthAndYear(String acadMonth,String acadYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		try{

			String sql = "SELECT examorder.order FROM exam.examorder where acadMonth=? and year=? ";

			examOrder = (double) jdbcTemplate.queryForObject(sql, new Object[]{acadMonth,acadYear},Integer.class);


		}catch(Exception e){
			  
		}

		return examOrder;
	}

	@Transactional(readOnly = true)
	public double getExamOrderFromExamMonthAndYear(String examMonth,String examYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		try{
			String sql = "SELECT examorder.order FROM exam.examorder where month=? and year=? ";
			examOrder = (double) jdbcTemplate.queryForObject(sql, new Object[]{examMonth,examYear},Double.class);
		}catch(Exception e){
			  
		}

		return examOrder;
	}

	public ArrayList<String> getGroup2SubjectList(){
		if(this.commonGroup2SubjectList == null){
			this.commonGroup2SubjectList = getGroupSubjects("G2");
		}
		return commonGroup2SubjectList;
	}

	public String getExamOrder(String month, String year) throws SQLException{
		String examOrder = "0";
		HashMap<String, Integer> hashMap = getExamOrderMap();
		Integer examOrderInteger = hashMap.get(month+year);
		if(examOrderInteger != null){
			examOrder = examOrderInteger.toString();
		}
		if("0".equals(examOrder)){
			throw new SQLException("Exam order not found");
		}
		return examOrder;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateFacultyDates(final List<FacultyAvailabilityBean> facultyDates, String userId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		ArrayList<FacultyAvailabilityBean> facultyList = new ArrayList<FacultyAvailabilityBean>();
		for (i = 0; i < facultyDates.size(); i++) {
			try{
				FacultyAvailabilityBean bean = facultyDates.get(i);
				ArrayList<FacultyAvailabilityBean> sessionDayTimeList = getDateDayTimeMapping(bean.getYear(), bean.getMonth());
				if (sessionDayTimeList.size() > 0) {
					for (FacultyAvailabilityBean session : sessionDayTimeList) {
						session.setFacultyId(bean.getFacultyId());
						session.setCreatedBy(userId);
						facultyList.add(session);
					}					
				}else {
					errorList.add(i+1+"");
					break;
				}
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		
		insertFacultyAvailabilityBatch(facultyList, jdbcTemplate);
		
		return errorList;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateFacultyCourseMapping(final List<FacultyCourseMappingBean> facultyCourseMapingList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < facultyCourseMapingList.size(); i++) {
			try{
				FacultyCourseMappingBean bean = facultyCourseMapingList.get(i);
				upsertFacultyCourseMapping(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateCorporateSessionMapping(final List<SessionDayTimeAcadsBean> corporateSessionList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < corporateSessionList.size(); i++) {
			try{
				SessionDayTimeAcadsBean bean = corporateSessionList.get(i);
				upsertCorporateSessionMapping(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false)
	private void upsertCorporateSessionMapping(SessionDayTimeAcadsBean bean, JdbcTemplate jdbcTemplate) {
		StringBuffer sql = new StringBuffer(" INSERT INTO acads.sessions ( date , startTime , endTime , day , subject , sessionName, createdBy , createdDate ,");
		sql.append(" year , month , facultyId , ciscoStatus , meetingKey , meetingPwd , hostUrl , hostKey , hostId , hostPassword , room , corporateName ) ");
		sql.append(" VALUES ");
		sql.append(" ( ? ,? ,? ,? ,? ,? , ? , sysdate() , ? , ? , ?, '', ? , ? ,?, ? ,? ,? , ? , ? ) ");

		jdbcTemplate.update(sql.toString(), new Object[] { 
				bean.getDate(),
				bean.getStartTime(),
				bean.getEndTime(),
				bean.getDay(),
				bean.getSubject(),
				bean.getSessionName(),
				bean.getCreatedBy(),
				bean.getYear(),
				bean.getMonth(),
				bean.getFacultyId(),
				bean.getMeetingKey(),
				bean.getMeetingPwd(),
				bean.getHostUrl(),
				bean.getHostKey(),
				bean.getHostId(),
				bean.getHostPassword(),
				bean.getRoom(),
				bean.getCorporateName()
		});
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateSessionReviewFacultyMapping(final List<SessionReviewBean> sessionReviewFacultyList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (SessionReviewBean sessionReview : sessionReviewFacultyList) {
			try{
				upsertSessionReviewFacultyMapping(sessionReview, jdbcTemplate);

			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}

/*Commented by Stef and moved to SessionReviewDAO
 * 
 * 
 * 	public HashMap<String,SessionDayTimeBean> mapOfSessionIdAndSessionBeanFromGivenSubjectList(Set<String> setOfSubject ,String Current_Acad_Month,int Current_Acad_Year){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("setOfSubject",setOfSubject);
		paramSource.addValue("Current_Acad_Month",Current_Acad_Month);
		paramSource.addValue("Current_Acad_Year",Current_Acad_Year);

		String sql = " select * from acads.sessions where subject in (:setOfSubject) and month=:Current_Acad_Month and year=:Current_Acad_Year";
		List<SessionDayTimeBean> sessionListFromId = new ArrayList<SessionDayTimeBean>();
		HashMap<String,SessionDayTimeBean> mapOfSessionIdAndSessionBean = new HashMap<String,SessionDayTimeBean>();
		try{
			sessionListFromId = namedParameterJdbcTemplate.query(sql, paramSource,new BeanPropertyRowMapper(SessionDayTimeBean.class));
			for(SessionDayTimeBean s : sessionListFromId){
				mapOfSessionIdAndSessionBean.put(s.getId(),s);
			}
			return mapOfSessionIdAndSessionBean;
		}catch(Exception e){
			  
			return null;
		}

	}*/
	
	protected BeanPropertySqlParameterSource getParameterSource(SessionReviewBean reviewBean) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(reviewBean);
		return parameterSource;
	}
	
	/*Commented by Stef and moved to SessionReviewDAO
	 * 
	 * public void updateFacultyReview(SessionReviewBean reviewBean){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		SqlParameterSource parameterSource = getParameterSource(reviewBean);
		String sql = " update acads.session_review set q1Response=:q1Response,q2Response=:q2Response,q3Response=:q3Response, "
				+ " q4Response=:q4Response,q5Response=:q5Response,q6Response=:q6Response,q1Remarks=:q1Remarks, "
				+ " q2Remarks=:q2Remarks,q3Remarks=:q3Remarks,q4Remarks=:q4Remarks,q5Remarks=:q5Remarks, q6Remarks=:q6Remarks,peerReviewAvg=:peerReviewAvg, "
				+ " lastModifiedDate=:lastModifiedDate,reviewed=:reviewed,lastModifiedBy=:lastModifiedBy where id=:id ";
		
		namedParameterJdbcTemplate.update(sql, parameterSource);

	}*
	
	 * 
	 * public List<SessionDayTimeBean> reviewListByFacultyId(String facultyId,String action){
		String sql = " select asr.reviewed,asr.id,f.firstName,f.lastName,asi.sessionName,asi.date,asi.subject,asi.corporateName "
				+ "  from acads.session_review asr,acads.faculty f,acads.sessions asi where "
				+ " asr.facultyId = f.facultyId and asr.sessionId = asi.id ";
		
		if("view".equals(action))
		{
			sql +=" and asr.facultyId = ?  order by asi.subject ,asi.sessionName,asi.date";
		}else{
			sql +=" and asr.reviewerFacultyId = ?  order by asi.subject ,asi.sessionName,asi.date";
		}
		
		return jdbcTemplate.query(sql, new Object[]{facultyId},new BeanPropertyRowMapper(SessionDayTimeBean.class));
	}*/

	@Transactional(readOnly = false)
	private void upsertSessionReviewFacultyMapping(SessionReviewBean bean, JdbcTemplate jdbcTemplate) {
		StringBuffer sql = new StringBuffer(" INSERT INTO acads.session_review ( sessionId,facultyId,reviewed,reviewerFacultyId, createdBy , createdDate )");
		sql.append(" VALUES ");
		sql.append(" ( ? , ? , ? , ? , ? , sysdate() ) ");
		sql.append(" on duplicate key update ");
		sql.append(" reviewerFacultyId = ? , lastModifiedBy = ? , sessionId = ? , facultyId = ? , lastModifiedDate = sysdate() ");

		jdbcTemplate.update(sql.toString(), new Object[] { 
				bean.getSessionId(),
				bean.getFacultyId(),
				bean.getReviewed(),
				bean.getReviewerFacultyId(),
				bean.getCreatedBy(),
				bean.getReviewerFacultyId(),
				bean.getCreatedBy(),
				bean.getSessionId(),
				bean.getFacultyId()

		});
	}

	@Transactional(readOnly = false)
	private void upsertFacultyCourseMapping(FacultyCourseMappingBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.facultyCourseMapping(year, month, subject, facultyIdPref1,facultyIdPref2,"
				+ " facultyIdPref3, session, duration, isAdditionalSession, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate  ) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "	    facultyIdPref1 = ?,"
				+ "	    facultyIdPref2 = ?,"
				+ "	    facultyIdPref3 = ?,"
				+ "	    session = ?,"
				+ "	    duration = ?,"
				+ "		isAdditionalSession = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate() ";


		String year = bean.getYear();
		String month = bean.getMonth();
		String subject = bean.getSubject();
		String facultyIdPref1 = bean.getFacultyIdPref1();
		String facultyIdPref2 = bean.getFacultyIdPref2();
		String facultyIdPref3 = bean.getFacultyIdPref3();
		String session = bean.getSession();
		String duration = bean.getDuration();
		String createdBy = bean.getCreatedBy();
		String isAdditionalSession = bean.getIsAdditionalSession();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				facultyIdPref1,
				facultyIdPref2,
				facultyIdPref3,
				session,
				duration,
				isAdditionalSession,
				createdBy,
				createdBy,
				year,
				month,
				subject,
				facultyIdPref1,
				facultyIdPref2,
				facultyIdPref3,
				session,
				duration,
				isAdditionalSession,
				createdBy
		});


	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateSessionDayTime(final List<SessionDayTimeAcadsBean> sessionDayTimeList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < sessionDayTimeList.size(); i++) {
			try{
				SessionDayTimeAcadsBean bean = sessionDayTimeList.get(i);
				upsertSessionDayTime(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false)
	private void upsertSessionDayTime(SessionDayTimeAcadsBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.session_days (year, month, day, startTime, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate  ) VALUES "
				+ "(?,?,?,?,?,sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    day = ?,"
				+ "	    startTime = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String day = bean.getDay();
		String startTime = bean.getStartTime();
		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				day,
				startTime,
				createdBy,
				createdBy,
				year,
				month,
				day,
				startTime,
				createdBy
		});

	}

	/*@SuppressWarnings("unchecked")
	public Page<FacultyUnavailabilityBean> getFacultyUnavailabilityDatesPage(int pageNo, int pageSize, FacultyUnavailabilityBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.facultyUnavailabilityDates where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.facultyUnavailabilityDates where 1 = 1 ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and facultyId like  ? ";
			countSql = countSql + " and facultyId like  ? ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getUnavailabilityDate() != null &&   !("".equals(searchBean.getUnavailabilityDate()))){
			sql = sql + " and unavailabilityDate = ? ";
			countSql = countSql + " and unavailabilityDate = ? ";
			parameters.add(searchBean.getUnavailabilityDate());
		}
		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}


		Object[] args = parameters.toArray();

		PaginationHelper<FacultyUnavailabilityBean> pagingHelper = new PaginationHelper<FacultyUnavailabilityBean>();
		Page<FacultyUnavailabilityBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyUnavailabilityBean.class));


		return page;
	}*/

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PageAcads<FacultyAvailabilityBean> getFacultyAvailabilityDatesPage(int pageNo, int pageSize, FacultyAvailabilityBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.facultyAvailability where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.facultyAvailability where 1 = 1 ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and facultyId like  ? ";
			countSql = countSql + " and facultyId like  ? ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getDate()!= null &&   !("".equals(searchBean.getDate()))){
			sql = sql + " and date = ? ";
			countSql = countSql + " and date = ? ";
			parameters.add(searchBean.getDate());
		}
		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}


		Object[] args = parameters.toArray();

		PaginationHelper<FacultyAvailabilityBean> pagingHelper = new PaginationHelper<FacultyAvailabilityBean>();
		PageAcads<FacultyAvailabilityBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyAvailabilityBean.class));


		return page;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PageAcads<FacultyCourseMappingBean> getCourseFacultyMappingPage(int pageNo, int pageSize, FacultyCourseMappingBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.facultycoursemapping where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.facultycoursemapping where 1 = 1 ";

		if( searchBean.getFacultyIdPref1() != null &&   !("".equals(searchBean.getFacultyIdPref1()))){
			sql = sql + " and facultyIdPref1 like  ? ";
			countSql = countSql + " and facultyIdPref1 like  ? ";
			parameters.add("%"+searchBean.getFacultyIdPref1()+"%");
		}
		if( searchBean.getFacultyIdPref2() != null &&   !("".equals(searchBean.getFacultyIdPref2()))){
			sql = sql + " and facultyIdPref2 like  ? ";
			countSql = countSql + " and facultyIdPref2 like  ? ";
			parameters.add("%"+searchBean.getFacultyIdPref2()+"%");
		}
		if( searchBean.getFacultyIdPref3() != null &&   !("".equals(searchBean.getFacultyIdPref3()))){
			sql = sql + " and facultyIdPref3 like  ? ";
			countSql = countSql + " and facultyIdPref3 like  ? ";
			parameters.add("%"+searchBean.getFacultyIdPref3()+"%");
		}
		if( searchBean.getSession() != null &&   !("".equals(searchBean.getSession()))){
			sql = sql + " and session like  ? ";
			countSql = countSql + " and session like  ? ";
			parameters.add("%"+searchBean.getSession()+"%");
		}

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}


		Object[] args = parameters.toArray();

		PaginationHelper<FacultyCourseMappingBean> pagingHelper = new PaginationHelper<FacultyCourseMappingBean>();
		PageAcads<FacultyCourseMappingBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyCourseMappingBean.class));


		return page;
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PageAcads<SessionDayTimeAcadsBean> getSessionDayTime(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.session_days where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.session_days where 1 = 1 ";


		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}


		Object[] args = parameters.toArray();

		PaginationHelper<SessionDayTimeAcadsBean> pagingHelper = new PaginationHelper<SessionDayTimeAcadsBean>();
		PageAcads<SessionDayTimeAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));


		return page;
	}

	/*public void deleteFacultyUnavailability(String id) {
		String sql = "Delete from acads.facultyUnavailabilityDates where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});

	}*/

	@Transactional(readOnly = false)
	public void deleteFacultyAvailability(String id) {
		String sql = "Delete from acads.facultyAvailability where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {id});
	}

	@Transactional(readOnly = false)
	public void deleteSessionDayTime(String id) {
		String sql = "Delete from acads.session_days where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {id});
	}

	/*private void upsertFacultyDates(FacultyUnavailabilityBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.facultyUnavailabilityDates(year, month, facultyId, unavailabilityDate, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate  ) VALUES "
				+ "(?,?,?,?,?,sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    facultyId = ?,"
				+ "	    unavailabilityDate = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String facultyId = bean.getFacultyId();
		String unavailabilityDate = bean.getUnavailabilityDate();
		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				facultyId,
				unavailabilityDate,
				createdBy,
				createdBy,
				year,
				month,
				facultyId,
				unavailabilityDate,
				createdBy
		});

	}*/

	@Transactional(readOnly = false)
	private void InsertAvailabilityFaculty(FacultyAvailabilityBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.facultyAvailability(year, month, facultyId, date, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate,time  ) VALUES "
				+ "(?,?,?,?,?,sysdate(),?, sysdate(),?)";

		String year = bean.getYear();
		String month = bean.getMonth();
		String dates =bean.getDate();
		String time =bean.getTime();
		String facultyId = bean.getFacultyId();

		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				facultyId,
				dates,
				createdBy,
				createdBy,
				time
		});
	}
	
	@Transactional(readOnly = false)
	private void insertFacultyAvailabilityBatch(ArrayList<FacultyAvailabilityBean> facultyList, JdbcTemplate jdbcTemplate) {
		int count = 0;
		final int batchSize = 1000;
		
		String sql = " INSERT INTO acads.facultyAvailability (year, month, facultyId, date, time, " +
					 " createdBy, createdDate, lastModifiedBy, lastModifiedDate  ) VALUES " +
				 	 " (?,?,?,?,?,?,sysdate(),?, sysdate()) " +
					 " ON DUPLICATE KEY UPDATE lastModifiedDate = sysdate(), lastModifiedBy = ? ";
		
		for (int j = 0; j < facultyList.size(); j += batchSize) {
			count ++;
			final List<FacultyAvailabilityBean> batchList = facultyList.subList(j, j + batchSize > facultyList.size() ? facultyList.size() : j + batchSize);
			try {
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						FacultyAvailabilityBean fc = batchList.get(i);
//						if (isAlreadyAdded(fc)) {
							ps.setString(1, fc.getYear());
							ps.setString(2, fc.getMonth());
							ps.setString(3, fc.getFacultyId());
							ps.setString(4, fc.getDate());
							ps.setString(5, fc.getTime());
							ps.setString(6, fc.getCreatedBy());
							ps.setString(7, fc.getCreatedBy());
							ps.setString(8, fc.getCreatedBy());
//						}
					}
					
					public int getBatchSize() {
						return batchList.size();
					}
				});
			} catch (Exception e) {
				  
			}	
		}
	}

	public HashMap<String,Integer> getMapOfFacultyIdAndRemainingSeats(String sessionId,SessionDayTimeAcadsBean session) {

		HashMap<String, Integer> sessionAttendanceMap = new LinkedHashMap<>();

		//Initialize with all available seats
		/*
		sessionAttendanceMap.put(session.getFacultyId(),MAX_WEBEX_USERS);
		if(StringUtils.isNotBlank(session.getAltFacultyId())) {
		sessionAttendanceMap.put(session.getAltFacultyId(),MAX_WEBEX_USERS);
		}
		if(StringUtils.isNotBlank(session.getAltFacultyId2())) {
			sessionAttendanceMap.put(session.getAltFacultyId2(),MAX_WEBEX_USERS);
		}
		if(StringUtils.isNotBlank(session.getAltFacultyId3())) {
					sessionAttendanceMap.put(session.getAltFacultyId3(),MAX_WEBEX_USERS);
		}
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId,(" + MAX_WEBEX_USERS + " - count(sapId)) as remainingSeats FROM acads.session_attendance_feedback where sessionId = ? group by facultyId ";

		ArrayList<SessionAttendanceFeedback> attendance = (ArrayList<SessionAttendanceFeedback>)jdbcTemplate.query(sql, new Object[]{sessionId},new BeanPropertyRowMapper(SessionAttendanceFeedback.class));

		for (SessionAttendanceFeedback sessionAttendanceFeedback : attendance) {
			sessionAttendanceMap.put(sessionAttendanceFeedback.getFacultyId(),sessionAttendanceFeedback.getRemainingSeats());
		}
		*/
		
		sessionAttendanceMap.put(session.getFacultyId(), getAttendanceMap(session.getFacultyId(), sessionId).getRemainingSeats());
		
		if(StringUtils.isNotBlank(session.getAltFacultyId()) && !StringUtils.isBlank(session.getAltMeetingKey())) {
			sessionAttendanceMap.put(session.getAltFacultyId(), getAttendanceMap(session.getAltFacultyId(), sessionId).getRemainingSeats());
		}
		
		if(StringUtils.isNotBlank(session.getAltFacultyId2()) && !StringUtils.isBlank(session.getAltMeetingKey2())) {
			sessionAttendanceMap.put(session.getAltFacultyId2(), getAttendanceMap(session.getAltFacultyId2(), sessionId).getRemainingSeats());
		}
		
		if(StringUtils.isNotBlank(session.getAltFacultyId3()) && !StringUtils.isBlank(session.getAltMeetingKey3())) {
			sessionAttendanceMap.put(session.getAltFacultyId3(), getAttendanceMap(session.getAltFacultyId3(), sessionId).getRemainingSeats());
		}

		return sessionAttendanceMap;
	}
	
	@Transactional(readOnly = true)
	public SessionAttendanceFeedbackAcads getAttendanceMap(String facultyId, String sessionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int capacity = 500;
        if (sessionId.equalsIgnoreCase("14339")) {
            capacity = MAX_WEBEX_USERS;
        }
        
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();
		String sql =  " SELECT facultyId,(" + capacity + " - count(sapId)) as remainingSeats FROM acads.session_attendance_feedback "
					+ " WHERE sessionId = ? AND facultyId = ? group by facultyId ";
		try {
			attendance = (SessionAttendanceFeedbackAcads) jdbcTemplate.queryForObject(sql, new Object[]{sessionId, facultyId}, 
							new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		} catch (Exception e) {
//			  
			attendance.setRemainingSeats(capacity);
		}
		return attendance;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateFacultyCourse(final List<FacultyCourseBean> facultyCourseList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < facultyCourseList.size(); i++) {
			try{
				FacultyCourseBean bean = facultyCourseList.get(i);
				upsertFacultyCourse(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false)
	private void upsertFacultyCourse(FacultyCourseBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.faculty_course(year, month, facultyId, subject, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate,subjectcode ) VALUES "
				+ "(?,?,?,?,?,sysdate(),?, sysdate(),?)"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    facultyId = ?,"
				+ "	    subject = ?,"
				+ "	    subjectcode = ?,"
				+ "	    lastModifiedBy = ? ,"
				+ "		lastModifiedDate = sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String facultyId = bean.getFacultyId();
		String subject = bean.getSubject();
		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,year);
				preparedStatement.setString(2,month);
				preparedStatement.setString(3,facultyId);
				preparedStatement.setString(4,subject);
				preparedStatement.setString(5,createdBy);
				preparedStatement.setString(6,createdBy);
				preparedStatement.setString(7,bean.getSubjectcode());
				preparedStatement.setString(8,year);
				preparedStatement.setString(9,month);
				preparedStatement.setString(10,facultyId);
				preparedStatement.setString(11,subject);
				preparedStatement.setString(12,bean.getSubjectcode());
				preparedStatement.setString(13,createdBy);
			}});
		
		

	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.mdm_subjectcode GROUP BY subjectname ORDER BY subjectname";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;
	}


	@Transactional(readOnly = true)
	public ArrayList<String> getGroupSubjects(String groupName) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects where commonSubject = ?";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{groupName}, new SingleColumnRowMapper(String.class));
		return subjectList;

	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		//String sql = "SELECT program FROM exam.programs order by program asc";
		String sql = "SELECT code FROM exam.program order by code asc";
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;

	}

	@Transactional(readOnly = true)
	public List<ExamOrderAcadsBean> getExamsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examorder order by examorder.order asc";
		List<ExamOrderAcadsBean> examsList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
		return examsList;
	}

	@Transactional(readOnly = true)
	public List<TimetableAcadsBean> getTimetableList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by program, prgmStructApplicable, sem, date, startTime asc";
		List<TimetableAcadsBean> timeTableList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TimetableAcadsBean.class));
		return timeTableList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamStats(ExamOrderAcadsBean exam){
		String sql = "Update exam.examorder set "
				+ "live=? , "
				+ "declareDate = sysdate()   "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTimetableStats(ExamOrderAcadsBean exam){
		String sql = "Update exam.examorder set "
				+ " timeTableLive = ?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getTimeTableLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}


	@Transactional(readOnly = true)
	public String getMostRecentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderAcadsBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
		for (ExamOrderAcadsBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}

	@Transactional(readOnly = true)
	public String getMostRecentTimeTablePeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderAcadsBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
		for (ExamOrderAcadsBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}

	@Transactional(readOnly = true)
	public String getRecentExamDeclarationDate() {

		String declareDate = null,decDate="";
		Date d = new Date();
		final String sql = "Select declareDate from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			d = sdfr.parse(decDate);
			declareDate = sdfr.format(d);
		} catch (Exception e) {
			declareDate = "";
		}
		return declareDate;
	}
	
	@Transactional(readOnly = true)
	public String getStudentCenterDetails(String sapId) {
		String centerCode = null;
		//final String sql = "Select centerCode, centerName from exam.students where sapid = ? and students.sem = (Select max(students.sem) from exam.students where sapid=?)";

		//final String tempSql = "Select centerCode, centerName from exam.student_center where sapid = ? ";
		final String tempSql = "Select centerCode, centerName from exam.students where sapid = ? "
				+ " and sem = (Select max(sem) from exam.students where sapid=?)";
		jdbcTemplate = new JdbcTemplate(dataSource);

		centerCode = (String) jdbcTemplate.queryForObject(tempSql,new Object[] { 
				sapId, sapId
		},String.class);

		return centerCode;
	}

	@Transactional(readOnly = true)
	public HashMap<String, String> getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		//String sql = "SELECT * FROM exam.programs";
		String sql = "SELECT * FROM exam.program";
		List<ProgramBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramBean.class));
		HashMap<String, String> programCodeNameMap = new HashMap<String, String>();

		for (int i = 0; i < programList.size(); i++) {
			//programCodeNameMap.put(programList.get(i).getProgram(), programList.get(i).getProgramname());
			programCodeNameMap.put(programList.get(i).getCode(), programList.get(i).getName());
		}

		return programCodeNameMap;
	}

	@Transactional(readOnly = true)
	public ArrayList<FacultyCourseMappingBean> getAllSessionsToSchedule() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.facultycoursemapping where ( scheduled <> 'Y' or scheduled is null)  ";
		ArrayList<FacultyCourseMappingBean> sessionsList = (ArrayList<FacultyCourseMappingBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyCourseMappingBean.class));
		return sessionsList;
	}

	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> getAvailableDatesForFaculty(String facultyId, String subject, String isAdditionalSession) {
		try{
			if(facultyId == null || "".equals(facultyId.trim())){
				return null;
			}
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(facultyId);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(subject);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(subject);
			parameters.add(facultyId);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(CURRENT_ACAD_YEAR);
			parameters.add(CURRENT_ACAD_MONTH);
			parameters.add(facultyId);

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " select * from acads.calendar c, acads.session_days sd "
						+ " where c.dayName = sd.day " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
						+ " and sd.year = ? and sd.month = ? " //Academic calendar
						+ " and c.date >= (select startDate from acads.academic_calendar where year = ? and month = ? ) "
						+ " and c.date <= (select endDate from acads.academic_calendar where year = ? and month = ?) " //Only those dates that lie between start and end day of Academic Calendar
						+ " and c.date >= curdate() "//Date is NOT in past
						+ " and (c.isHoliday is null or c.isHoliday <> 'Y' ) ";//Date is NOT a HOliday

			sql = sql + " and c.date not in (select date from acads.sessions where facultyId = ? group by date having count(date) > 1) "//Ensure faculty is not taking more than 2 sessions on same day
					  + " and  (DATEDIFF(c.date, (select max(date) from acads.sessions where year = ? and month = ? and subject = ?)) >= 7 OR "
					  + " DATEDIFF(c.date, (select max(date) from acads.sessions where year = ? and month = ? and subject = ?)) is null) "//Ensure session is not within 7 days of last session of same subject
					  + " and CONCAT(c.date, sd.startTime) in (select CONCAT(date, time)  from acads.facultyAvailability where facultyId = ? and year = ? and month = ?) " //And date is not which faculty is not going to be available
					  + " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = ? and month = ? group by date, startTime having count(*) = 5 ) " //Ensure that time slot does not already have 5 sessions scheduled
					  + " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = ? and month = ? and facultyId = ?) "; //Ensure that faculty is not taking any other session at same time

			if(!"Y".equalsIgnoreCase(isAdditionalSession)){		
				//Additional session can be held along with same semester subjects
				if(getGroup1SubjectList().contains(subject)){
					sql = sql + " and c.date not in (select date from acads.sessions where subject in (select subjectname from exam.subjects where commonSubject = 'G1') group by date having count(date) > 1) "; //Not more than 2 sessions of common subjects
				}else if(getGroup2SubjectList().contains(subject)){
					sql = sql +" and c.date not in (select date from acads.sessions where subject in (select subjectname from exam.subjects where commonSubject = 'G2' ) group by date having count(date) > 1) "; //Not more than 2 sessions of common subjects
				}

				sql = sql + " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = ? and month = ? "
						+ " and subject in (select distinct subject  from exam.program_subject where concat(program,sem,prgmStructApplicable)"
						+ " in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ? and active = 'Y') )) "; // No other subject of same semester is scheduled at that time
				parameters.add(CURRENT_ACAD_YEAR);
				parameters.add(CURRENT_ACAD_MONTH);
				parameters.add(subject);
			}
			sql = sql + " order by c.date, sd.startTime";

			ArrayList<SessionDayTimeAcadsBean> datesList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return datesList;
		}catch(Exception e){
			  
		}

		return null;
	}

	@Transactional(readOnly = false)
	public void insertSession(SessionDayTimeAcadsBean bean, String sessionToScheduleId) {
		int sessionTime = getSessionTime(bean);
		String dateTime = bean.getDate() + " " + bean.getStartTime();

		String sql = " INSERT INTO acads.sessions ( "
				+ " year, month, date, startTime, endTime, day, subject, sessionName, "
				+ " facultyId, room, hostId, hostPassword, createdBy, createdDate, lastModifiedBy, lastModifiedDate ) "
				+ " VALUES( ?,?,?,?,ADDDATE('" + dateTime + "', INTERVAL " + sessionTime
				+ " MINUTE),?,?,?,?,?,?,?,?,sysdate(),?, sysdate()) ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String date = bean.getDate();
		String startTime = bean.getStartTime();
		String day = bean.getDay();
		String subject = bean.getSubject();
		String sessionName = bean.getSessionName();
		String facultyId = bean.getFacultyId();
		String createdBy = bean.getCreatedBy();
		String room = bean.getRoom();
		String hostId = bean.getHostId();
		String hostPassword = bean.getHostPassword();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				date,
				startTime,
				day,
				subject,
				sessionName,
				facultyId,
				room,
				hostId,
				hostPassword,
				createdBy,
				createdBy

		});

		sql = " Update acads.facultycoursemapping set scheduled = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[] { sessionToScheduleId	});

	}

	@Transactional(readOnly = false)
	public void insertSingleCommonSession(SessionDayTimeAcadsBean bean) {
		int sessionTime = getSessionTime(bean);
		String dateTime = bean.getDate()+ " " +bean.getStartTime();
		
		String sql = "INSERT INTO acads.sessions ( "
				+ " year, month, date, startTime, endTime, day, subject, sessionName, "
				+ " facultyId, room, hostId, hostPassword, createdBy, createdDate, lastModifiedBy, lastModifiedDate, "
				+ " sem, isCommon,programList) "
				+ " VALUES( ?,?,?,?, ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE),"
				+ " (Select dayName from acads.calendar where date = ? ) "
				+ ",?,?,?,?,?,?,?,sysdate(),?, sysdate(),?,'Y',?) ";


		String year = bean.getYear();
		String month = bean.getMonth();
		String date = bean.getDate();
		String startTime = bean.getStartTime();
		String day = bean.getDay();
		String subject = bean.getSubject();
		String sessionName = bean.getSessionName();
		String facultyId = bean.getFacultyId();
		String createdBy = bean.getCreatedBy();
		String room = bean.getRoom();
		String hostId = bean.getHostId();
		String hostPassword = bean.getHostPassword();
		String sem=bean.getSem();
		String programList = bean.getProgramList();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				date,
				startTime,
				date,
				subject,
				sessionName,
				facultyId,
				room,
				hostId,
				hostPassword,
				createdBy,
				createdBy,
				sem,
				programList
		});
	}

	@Transactional(readOnly = false)
	public void insertSingleSession(SessionDayTimeAcadsBean bean) {
		
		int sessionTime = getSessionTime(bean);
		String dateTime = bean.getDate()+ " " +bean.getStartTime();
		
		String sql = "INSERT INTO acads.sessions ( "
				+ " year, month, date, startTime, endTime, day, subject, sessionName, "
				+ " facultyId, room, hostId, hostPassword, createdBy, createdDate, lastModifiedBy, lastModifiedDate ) "
				+ " VALUES( ?,?,?,?, ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE), "
				+ " (Select dayName from acads.calendar where date = ? ) "
				+ ",?,?,?,?,?,?,?,sysdate(),?, sysdate()) ";


		String year = bean.getYear();
		String month = bean.getMonth();
		String date = bean.getDate();
		String startTime = bean.getStartTime();
		String day = bean.getDay();
		String subject = bean.getSubject();
		String sessionName = bean.getSessionName();
		String facultyId = bean.getFacultyId();
		String createdBy = bean.getCreatedBy();
		String room = bean.getRoom();
		String hostId = bean.getHostId();
		String hostPassword = bean.getHostPassword();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				date,
				startTime,
				date,
				subject,
				sessionName,
				facultyId,
				room,
				hostId,
				hostPassword,
				createdBy,
				createdBy

		});

	}
	
	//insertDuplicateSession start
	@Transactional(readOnly = false)
	public Boolean insertDuplicateSession(final SessionDayTimeAcadsBean bean) {
		
		if (StringUtils.isBlank(bean.getHasModuleId())) {
			bean.setHasModuleId("N");
		}
		
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		boolean toCommit = Boolean.FALSE;
		long primaryKey = 0;
		int isSessionMapping = 1;
		boolean isInserted = false;
		
		int sessionTime = getSessionTime(bean);
		String dateTime = bean.getDate()+ " " +bean.getStartTime();
		
		final String sql =" INSERT INTO acads.sessions ( "
						+ " year, month, date, startTime, endTime, day, subject, sessionName, "
						+ " room, hostId, hostPassword, facultyId, facultyLocation, "
						+ " ciscoStatus, meetingKey, meetingPwd, joinUrl, hostUrl, hostKey, "
						+ " isCommon, corporateName, "
						+ " altMeetingKey, altMeetingPwd, altFacultyId, altFacultyLocation, "
						+ " altHostId, altHostKey, altHostPassword, "
						+ " altMeetingKey2, altMeetingPwd2, altFacultyId2, altFaculty2Location, "
						+ " altHostId2, altHostKey2, altHostPassword2, "
						+ " altMeetingKey3, altMeetingPwd3, altFacultyId3, altFaculty3Location, "
						+ " altHostId3, altHostKey3, altHostPassword3, "
						+ " isCancelled, track, sem, "
						+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
						+ " programList, hasModuleId, moduleId, sessionType"
						+ " ) "
						+ " VALUES( ?,?,?,?,ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE), (Select dayName from acads.calendar where date = ? ), ?,?,"
						+ " ?,?,?,?,?,"
						+ " ?,?,?,?,?,?,"
						+ " ?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,"
						+ " ?,sysdate(),?, sysdate(),"
						+ " ?,?,?,?"
						+ " ) ";

		
		try {
		/*
			String year = bean.getYear();
			String month = bean.getMonth();
			String date = bean.getDate();
			String startTime = bean.getStartTime();
			//String day = bean.getDay();
			String subject = bean.getSubject();
			String sessionName = bean.getSessionName();
			String facultyId = bean.getFacultyId();
			String createdBy = bean.getCreatedBy();
			String room = bean.getRoom();
			String hostId = bean.getHostId();
			String hostPassword = bean.getHostPassword();

			String ciscoStatus =bean.getCiscoStatus();
			String meetingKey= bean.getMeetingKey();
			String meetingPwd=bean.getMeetingPwd();
			String joinUrl=bean.getJoinUrl();
			String hostUrl=bean.getHostUrl();
			String hostKey=bean.getHostKey(); 
			String altMeetingKey=bean.getAltMeetingKey();
			String altMeetingPwd=bean.getAltMeetingPwd();
			String altFacultyId=bean.getAltFacultyId(); 
			String isCommon=bean.getIsCommon();
			String corporateName=bean.getCorporateName(); 
			String altMeetingKey2=bean.getAltMeetingKey2();
			String altMeetingPwd2=bean.getAltMeetingPwd2();
			String altFacultyId2=bean.getAltFacultyId2(); 
			String altMeetingKey3=bean.getAltMeetingKey3();
			String altMeetingPwd3=bean.getAltMeetingPwd3();
			String altFacultyId3=bean.getAltFacultyId3();
			String altHostId=bean.getAltHostId();
			String altHostKey=bean.getAltHostKey();
			String altHostPassword=bean.getAltHostPassword(); 
			String altHostId2=bean.getAltHostId2();
			String altHostKey2=bean.getAltHostKey2();
			String altHostPassword2=bean.getAltHostPassword2(); 
			String altHostId3=bean.getAltHostId3();
			String altHostKey3=bean.getAltHostKey3();
			String altHostPassword3=bean.getAltHostPassword3(); 
			String isCancelled=bean.getIsCancelled();
			
			String facultyLocation=bean.getFacultyLocation();
			String altFacultyLocation=bean.getAltFacultyLocation();
			String altFaculty2Location=bean.getAltFaculty2Location();
			String altFaculty3Location=bean.getAltFaculty3Location();
			
			String track = bean.getTrack();
			
			String sem = bean.getSem();
			String programList = bean.getProgramList();
			
			String hasModuleId = bean.getHasModuleId();
			String moduleId = bean.getSessionModuleNo();
		*/
			startTransaction("sessionInsertion");
			
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					statement.setString(1,bean.getYear());
					statement.setString(2, bean.getMonth());
					statement.setString(3, bean.getDate());
					statement.setString(4, bean.getStartTime());
					//statement.setString(5, bean.getStartTime());
					statement.setString(5, bean.getDate());
					statement.setString(6, bean.getSubject());
					statement.setString(7, bean.getSessionName());
					
					statement.setString(8, bean.getRoom());
					statement.setString(9, bean.getHostId());
					statement.setString(10, bean.getHostPassword());
					statement.setString(11, bean.getFacultyId());
					statement.setString(12, bean.getFacultyLocation());
					
					statement.setString(13, bean.getCiscoStatus());
					statement.setString(14, bean.getMeetingKey());
					statement.setString(15, bean.getMeetingPwd());
					statement.setString(16, bean.getJoinUrl());
					statement.setString(17, bean.getHostUrl());
					statement.setString(18, bean.getHostKey());
					
					statement.setString(19, bean.getIsCommon());
					statement.setString(20, bean.getCorporateName());
					
					statement.setString(21, bean.getAltMeetingKey());
					statement.setString(22, bean.getAltMeetingPwd());
					statement.setString(23, bean.getAltFacultyId());
					statement.setString(24, bean.getAltFacultyLocation());
					statement.setString(25, bean.getAltHostId());
					statement.setString(26, bean.getAltHostKey());
					statement.setString(27, bean.getAltHostPassword());
					
					statement.setString(28, bean.getAltMeetingKey2());
					statement.setString(29, bean.getAltMeetingPwd2());
					statement.setString(30, bean.getAltFacultyId2());
					statement.setString(31, bean.getAltFaculty2Location());
					statement.setString(32, bean.getAltHostId2());
					statement.setString(33, bean.getAltHostKey2());
					statement.setString(34, bean.getAltHostPassword2());
					
					statement.setString(35, bean.getAltMeetingKey3());
					statement.setString(36, bean.getAltMeetingPwd3());
					statement.setString(37, bean.getAltFacultyId3());
					statement.setString(38, bean.getAltFaculty3Location());
					statement.setString(39, bean.getAltHostId3());
					statement.setString(40, bean.getAltHostKey3());
					statement.setString(41, bean.getAltHostPassword3());
					
					statement.setString(42, bean.getIsCancelled());
					statement.setString(43, bean.getTrack());
					statement.setString(44, bean.getSem());
					
					statement.setString(45, bean.getCreatedBy());
					statement.setString(46, bean.getCreatedBy());
					statement.setString(47, bean.getProgramList());
					statement.setString(48, bean.getHasModuleId());
					statement.setString(49, bean.getSessionModuleNo());
					statement.setString(50, bean.getSessionType());
					
					return statement;
				}
				
			}, holder);

			primaryKey = holder.getKey().longValue();
			loggerForSessionScheduling.info("In insertDuplicateSession inserted session into acads.session table, got Primary key :"+primaryKey);
			
			//Session Subject Mapping Start
			
			ArrayList<ConsumerProgramStructureAcads> applicableMasterKeysWithSubjectData = getDataForSessionSubjectMapping(bean);
			SessionDayTimeAcadsBean newSession = new SessionDayTimeAcadsBean();
			newSession.setSessionId(String.valueOf(primaryKey));
			newSession.setCreatedBy(bean.getCreatedBy());
			newSession.setLastModifiedBy(bean.getCreatedBy());
			
			
			for (ConsumerProgramStructureAcads data : applicableMasterKeysWithSubjectData) {
				newSession.setConsumerProgramStructureId(data.getConsumerProgramStructureId());
				newSession.setPrgmSemSubId(Integer.valueOf(data.getProgramSemSubjectId()));
				newSession.setSubjectCodeId(Integer.valueOf(data.getSubjectCodeId()));
				isSessionMapping = insertSessionSubjectMapping(newSession);
			}
			loggerForSessionScheduling.info("In insertDuplicateSession inserted session into acads.session_subject_mapping table. isSessionMapping = "+isSessionMapping);
			//Session Subject Mapping End
			
			//Insert Quick Session Start
			ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds = getAllPSSSessionsMapping(primaryKey);
			isInserted = insertQuickSession(sessionWithPSSIds, primaryKey);
			loggerForSessionScheduling.info("In insertDuplicateSession inserted session into acads.quick_sessions table. isInserted = "+isInserted);
			//Insert Quick Session End
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			toCommit = primaryKey != 0 && isSessionMapping == 0 && isInserted;
			endTransaction(toCommit);
		}
		
		return toCommit;
	}

	//insertDuplicateSession end
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentRegistrationData(String sapId) {
		StudentAcadsBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ";

			studentRegistrationData = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
		} catch (Exception e) {
			  
		}
		return studentRegistrationData;
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentRegistrationDataNew(String sapId) {
		StudentAcadsBean studentRegistrationData = null;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			StudentAcadsBean studentForConsuemerTypeCheck = getSingleStudentsData(sapId);
			/* Temp Fix to be removed later by PS 18th May
			 *  Newly added check for diageo to enable session for sem 2 students by PS
			 * */
			String sql = "";
			if("Diageo".equalsIgnoreCase(studentForConsuemerTypeCheck.getConsumerType())) {
				
				 sql = 	  "	Select re.*, s.consumerType from exam.registration re , exam.students s, exam.examorder eo  "
				 		+ " where re.sapid = ? and s.sapid = re.sapid "
						+ " and eo.order = (select max(eeo.order) from exam.examorder eeo where eeo.acadSessionLive = 'Y') "
						+ " and re.month = eo.acadMonth "
						+ " and re.year = eo.year"
						+ " and re.sem = (select max(r2.sem) "
						+ "					from exam.registration r2 "
						+ "					where r2.sapid = re.sapid "
						+ "					and r2.program = re.program  ) ";
			
			}else if (TIMEBOUND_PORTAL_LIST.contains(studentForConsuemerTypeCheck.getConsumerProgramStructureId())){
				
				sql = " SELECT * FROM exam.registration WHERE sapid = ? ORDER BY sem DESC LIMIT 1 ";
				
			}else {
				sql = 	  " Select re.*, s.consumerType from exam.registration re , exam.students s, exam.examorder eo  "
			 			+ " where re.sapid = ? and s.sapid = re.sapid "
						+ " and eo.order = (select max(eeo.order) from exam.examorder eeo where eeo.acadSessionLive = 'Y') "
						+ " and re.month = eo.acadMonth "
						+ " and re.year = eo.year ";
			}
			
			studentRegistrationData = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
		} catch (Exception e) {
			  
		}
		return studentRegistrationData;
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentRegistrationDataForExecutive(String sapId) {
		StudentAcadsBean studentRegistrationData = null;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "	select re.* from exam.registration re , exam.executive_examorder eo, exam.students s   where re.sapid = ? "
						+ " and eo.order = (select max(eeo.order) from exam.executive_examorder eeo where eeo.acadMonth = s.enrollmentMonth and eeo.acadYear = s.enrollmentYear ) "
						+ " and re.month = eo.acadMonth "
						+ " and re.year = eo.acadYear "
						+ " and s.sapid = re.sapid "
						+ " and s.program = re.program ";
			studentRegistrationData = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
		} catch (Exception e) {
			  
		}
		return studentRegistrationData;
	}

	@Transactional(readOnly = false)
	public StudentAcadsBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentAcadsBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			student = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{ sapid, sapid }, new BeanPropertyRowMapper(StudentAcadsBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		}catch(Exception e){
			  
		}
		return student;
	}

	@Transactional(readOnly = true)
	public PageAcads<SessionDayTimeAcadsBean> getScheduledSessionPage(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean, String searchOption) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql =" SELECT  " + 
					"    s.*, " + 
					//"    f.*, " +  //modified by Riya as it was giving error out of memory due to faculty description
					"  f.facultyId,f.location,f.firstName,f.lastName, "+
					"   s_m.createdDate as sessionPlanModuleCreatedDate, " +
					"   s_m.lastModifiedDate as sessionPlanModuleLastModifiedDate, " +
//					"	vc.videoLink as videoLink, " +
//					"	vc.duration as sessionDuration, " +
					"	 s.id AS id, " +
					"	 s.facultyId AS facultyId, " +
					"	 scm.sem, " +
					"	 sc.subjectcode, " +
					"    ssm.consumerProgramStructureId, " + 
					"    ct.name AS consumerType, " + 
					"    p.code AS program, " + 
					"    ps.program_structure AS programStructure ";
		
		String countSql = "";
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " , COUNT(DISTINCT CONCAT(s.subject, s.sessionName, ssm.consumerProgramStructureId)) AS count, " + 
						"   CONCAT(s.date,s.startTime,s.subject,s.sessionName,s.facultyId,IFNULL(s.track, ''), IFNULL(s.moduleid, ''), IFNULL(s.isCancelled, ''),  sc.subjectCode) AS distinctSession, " + 
						"   GROUP_CONCAT(distinct ssm.consumerProgramStructureId) consumerProgramStructureId ";
			
			countSql = countSql + " SELECT COUNT(DISTINCT CONCAT(s.date,s.startTime,s.subject,s.sessionName,s.facultyId,IFNULL(s.track, ''), IFNULL(s.moduleid, ''), IFNULL(s.isCancelled, ''), sc.subjectCode)) ";
		}else {
			countSql = countSql + " SELECT COUNT(DISTINCT CONCAT(ssm.sessionId, ssm.consumerProgramStructureId)) ";
									
		}
		
		sql = sql + " FROM " + 
				"    acads.sessions s " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON s.facultyId = f.facultyId OR s.altFacultyId = f.facultyId OR s.altFacultyId2 = f.facultyId OR s.altFacultyId3 = f.facultyId " + 
				"        INNER JOIN " + 
				"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
				"        INNER JOIN " + 
				"    exam.consumer_program_structure cps ON cps.id = ssm.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"        INNER JOIN " + 
				"    exam.program p ON p.id = cps.programId " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " +
//				" 	 	 INNER JOIN  exam.program_sem_subject pss ON pss.consumerProgramStructureId = cps.id and s.subject = pss.subject " + 
				"	 	 INNER JOIN " +
				"	 exam.mdm_subjectcode_mapping scm ON cps.id = scm.consumerProgramStructureId " +
				"	 	 INNER JOIN " +
				"	  exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id AND s.subject = sc.subjectname "+
				"		LEFT JOIN " +
				"	  acads.sessionplan_module s_m ON s_m.id = s.moduleid " + 
//				"	     LEFT JOIN " +
//				"	 acads.video_content vc ON vc.sessionId = s.id " +
				"	 WHERE 1 = 1 ";	
		
		countSql = countSql + " FROM " + 
				"    acads.sessions s " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON s.facultyId = f.facultyId OR s.altFacultyId = f.facultyId OR s.altFacultyId2 = f.facultyId OR s.altFacultyId3 = f.facultyId " +
				"        INNER JOIN " + 
				"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
				"        INNER JOIN " + 
				"    exam.consumer_program_structure cps ON cps.id = ssm.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"        INNER JOIN " + 
				"    exam.program p ON p.id = cps.programId " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " +
//				" 	 	 INNER JOIN exam.program_sem_subject pss ON pss.consumerProgramStructureId = cps.id and s.subject = pss.subject " +
				"	 	 INNER JOIN " +
				"	 exam.mdm_subjectcode_mapping scm ON cps.id = scm.consumerProgramStructureId " +
				"	 	 INNER JOIN " +
				"	  exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id AND s.subject = sc.subjectname "+
				"		LEFT JOIN " +
				"	  acads.sessionplan_module s_m ON s_m.id = s.moduleid " +  
//				"	     LEFT JOIN " +
//				"	 acads.video_content vc ON vc.sessionId = s.id " +
				"	WHERE 1 = 1 ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and (s.facultyId like ? OR s.altFacultyId like ? OR s.altFacultyId2 like ? OR s.altFacultyId3 like ?) ";
			countSql = countSql + " and (s.facultyId like ? OR s.altFacultyId like ? OR s.altFacultyId2 like ? OR s.altFacultyId3 like ?) ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getSessionName() != null &&   !("".equals(searchBean.getSessionName()))){
			sql = sql + " and sessionName like  ? ";
			countSql = countSql + " and sessionName like  ? ";
			parameters.add("%"+searchBean.getSessionName()+"%");
		}

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and s.subject = ? ";
			countSql = countSql + " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and s.year = ? ";
			countSql = countSql + " and s.year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and s.month = ? ";
			countSql = countSql + " and s.month = ? ";
			parameters.add(searchBean.getMonth());
		}
		//Commented as Not Required 
		/*
		if( searchBean.getDate() != null &&   !("".equals(searchBean.getDate()))){
			sql = sql + " and s.date = ? ";
			countSql = countSql + " and s.date = ? ";
			parameters.add(searchBean.getDate());
		}
		*/
		
		if(!StringUtils.isBlank(searchBean.getFromDate())){
			sql = sql + " and s.date >= ?";
			countSql = countSql + " and s.date >= ?";
			parameters.add(searchBean.getFromDate());
		}
		
		if(!StringUtils.isBlank(searchBean.getToDate())){
			sql = sql + " and s.date <= ?";
			countSql = countSql + " and s.date <= ?";
			parameters.add(searchBean.getToDate());
		}
		
		if( searchBean.getDay() != null &&   !("".equals(searchBean.getDay()))){
			sql = sql + " and s.day = ? ";
			countSql = countSql + " and s.day = ? ";
			parameters.add(searchBean.getDay());
		}

		if( searchBean.getId() != null &&   !("".equals(searchBean.getId()))){
			sql = sql + " and s.Id = ? ";
			countSql = countSql + " and s.Id = ? ";
			parameters.add(searchBean.getId());
		}

		if( searchBean.getCorporateName() != null &&   !("".equals(searchBean.getCorporateName()))){
			sql = sql + " and s.corporateName = ? ";
			countSql = countSql + " and s.corporateName = ? ";
			parameters.add(searchBean.getCorporateName());
		}
		
		if( searchBean.getFacultyLocation() != null &&   !("".equals(searchBean.getFacultyLocation()))){
			sql = sql + " and s.facultyLocation like  ? ";
			countSql = countSql + " and s.facultyLocation like  ? ";
			parameters.add("%"+searchBean.getFacultyLocation()+"%");
		}
		
		if( searchBean.getSubjectCode() != null &&   !("".equals(searchBean.getSubjectCode()))){
			sql = sql + " and sc.subjectcode = ? ";
			countSql = countSql + " and sc.subjectcode = ? ";
			parameters.add(searchBean.getSubjectCode());
		}
		
		if (searchBean.getConsumerProgramStructureId() != null
				&& !("".equals(searchBean.getConsumerProgramStructureId()))) {
			sql = sql + " and ssm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			countSql = countSql + " and ssm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			
		}
		
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " group by distinctSession ";
		}else {
			sql = sql + " group by concat(ssm.sessionId, ssm.consumerProgramStructureId) ";
		}

		sql = sql + " order by s.date, s.startTime asc";
		Object[] args = parameters.toArray();

		PaginationHelper<SessionDayTimeAcadsBean> pagingHelper = new PaginationHelper<SessionDayTimeAcadsBean>();
		PageAcads<SessionDayTimeAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

		return page;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' ";
		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,FacultyAcadsBean> getAllFacultyMapper() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";

		ArrayList<FacultyAcadsBean> facultyList = (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyAcadsBean.class));
		HashMap<String,FacultyAcadsBean> facultyIdAndFacultyBeanMap = new HashMap<String,FacultyAcadsBean>();
		for(FacultyAcadsBean bean :facultyList)
		{
			facultyIdAndFacultyBeanMap.put(bean.getFacultyId(), bean);
		}
		return facultyIdAndFacultyBeanMap;
	}

	@Transactional(readOnly = true)
	public ArrayList<FacultyAcadsBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";

		ArrayList<FacultyAcadsBean> facultyList = (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyAcadsBean.class));
		return facultyList;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllSessions() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT id FROM acads.sessions  ";

		ArrayList<String> sessionList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sessionList;
	}

	@Transactional(readOnly = true)
	public HashMap<String, CenterAcadsBean> getICLCMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterAcadsBean> centers = (ArrayList<CenterAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterAcadsBean.class));

		HashMap<String, CenterAcadsBean> icLcMap = new HashMap<>();
		for (CenterAcadsBean center : centers) {
			icLcMap.put(center.getCenterCode(), center);
		}

		return icLcMap;
	}

	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean findScheduledSessionById(String id) {
		String sql = "SELECT * FROM acads.faculty f , acads.sessions s where s.facultyId = f.facultyId and s.id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeAcadsBean session = null;
		try {
			session= (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(
					sql, new Object[] { id }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			
		} catch (Exception e) {
			  
		}
		return session;
	}
	
	@Transactional(readOnly = false)
	public Boolean findScheduledSessionByDateTimeSubject(SessionDayTimeAcadsBean session) {
		String sql = "SELECT * FROM  acads.sessions s where s.date=? and s.startTime=? and s.subject=? and s.track=?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeAcadsBean sessionReturn=null;
		try {
			sessionReturn= (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(
					sql, new Object[] { session.getDate(),session.getStartTime(), session.getSubject(), session.getTrack() }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (DataAccessException e) {
			//  
		}
		if(sessionReturn==null) {
			return false;
		}else {
			return true;
		}
		
	}
	
	@Transactional(readOnly = true)
	public boolean getOnlineEventRegistrationBySapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = " select count(*) from portal.event_registration where response ='Yes' and sapid = ? ";
		try{
			int rowCount = (int)jdbcTemplate.queryForObject(query,new Object[] {sapid}, Integer.class);
			if(rowCount > 0)
			{
				return true;
			}
		}catch(Exception e){
			  
		}
		return false;
	}
	
	@Transactional(readOnly = true)
	public SessionQueryAnswer findSessionQueryAnswerById(String id) {
		String sql = "SELECT * FROM acads.session_query_answer where id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionQueryAnswer sessionQuery = (SessionQueryAnswer) jdbcTemplate.queryForObject(
				sql, new Object[] { id }, new BeanPropertyRowMapper(SessionQueryAnswer.class));

		return sessionQuery;
	}
	
	@Transactional(readOnly = false)
	public int updateSessionQueryAnsById(String id , String answer) {
		String sql = "Update acads.session_query_answer  set answer = ? , isAnswered ='Y' where id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		int result = jdbcTemplate.update(sql, new Object[]{answer,id});
		return result;
	}

	@Transactional(readOnly = false)
	public boolean updateScheduledSession(SessionDayTimeAcadsBean session) {
		
		boolean toCommit = Boolean.FALSE;
		long sessionId = Long.parseLong(session.getId());
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		
		int sessionTime = getSessionTime(session);
		String dateTime = session.getDate()+ " " +session.getStartTime();
		
		try {
			startTransaction("updateScheduledSession");
			String sql = "Update acads.sessions set "
					+ "facultyId=?,"
					+ "date=?,"
					+ "startTime=?,"
					+ "day=(Select dayName from acads.calendar where date = ? ),"
					+ "endTime= ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE),"
					+ "smsSent='N',"
					+ "emailSent='N',"
					+ "lastModifiedBy=?,"
					+ "lastModifiedDate=sysdate(),"
					+ "corporateName=?,"
					+ "facultyLocation=?,"
					+ "track = ? "
					+ "where id = ? ";

			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] { 
					session.getFacultyId(),
					session.getDate(),
					session.getStartTime(),
					session.getDate(),
					session.getLastModifiedBy(),
					session.getCorporateName(),
					session.getFacultyLocation(),
					session.getTrack(),
					session.getId()
			});

			//Delete old mapping from Quick_Session
			loggerForSessionScheduling.info("Calling deleteQuickMappingByCPSId session.getId(): "+session.getId()+" consumerProgramStructureId: "+consumerProgramStructureId);
			deleteQuickMappingByCPSId(session.getId(), consumerProgramStructureId);
			
			//Insert new SessionId in Quick_Session
			ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds = getAllPSSSessionsMapping(sessionId);
			boolean isInserted = insertQuickSession(sessionWithPSSIds, sessionId);
			if (isInserted) {
				toCommit = true;
			}
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in updateScheduledSession "+e.getMessage());
			toCommit = false;
		}  finally {
			endTransaction(toCommit);
		}
		return toCommit;
		
	}
	/*
	 * id, date, startTime, day, subject, sessionName, createdBy, createdDate, lastModifiedBy, lastModifiedDate,
	 *  year, month, facultyId, endTime, ciscoStatus, tmsConfId, tmsConfLink, meetingKey, meetingPwd, joinUrl, hostUrl, hostKey,
	 *   localTollNumber, localTollFree, globalCallNumber, pstnDialNumber, participantCode, room, smsSent, emailSent, hostId, hostPassword,
	 *    altMeetingKey, altMeetingPwd, altFacultyId, sem, isCommon, corporateName, altMeetingKey2, altMeetingPwd2, altFacultyId2,
	 *     altMeetingKey3, altMeetingPwd3, altFacultyId3, programList, altHostId, altHostPassword, altHostId2, altHostPassword2, altHostId3, altHostPassword3, 
	 *     isCancelled, isVerified, reasonForCancellation, cancellationSMSBody, cancellationEmailBody, cancellationSubject, cancellationSmsSent,
	 *     cancellationEmailSent, eventId, facultyLocation, altFacultyLocation, altFaculty2Location, altFaculty3Location, track
	 * */
	
	@Transactional(readOnly = false)
	public void updateScheduledSessionForWrongHostid(SessionDayTimeAcadsBean session) {
		String sql = "Update acads.sessions set "
				+ " hostId=?, hostPassword=?,"
				+ " hostKey=?,"
				+ " altHostId=?, altHostPassword=?,"
				+ " altHostId2=?, altHostPassword2=?,"
				+ " altHostId3=?, altHostPassword3=?, "
				+ " ciscoStatus=?,"
				+ " room =? "
				+ "  where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				session.getHostId(),session.getHostPassword(),
				session.getHostKey(),
				session.getAltHostId(),session.getAltHostPassword(),
				session.getAltHostId2(),session.getAltHostPassword2(),
				session.getAltHostId3(),session.getAltHostPassword3(),
				session.getCiscoStatus(),
				session.getRoom(),
				session.getId()
		});
	}

	@Transactional(readOnly = false)
	public boolean updateSessionName(SessionDayTimeAcadsBean session, String userId) {
		
		boolean toCommit = Boolean.FALSE;
		String sql = "Update acads.sessions set "
				+ " sessionName= ?, "
				+ " lastModifiedBy=?,"
				+ " lastModifiedDate=sysdate()"
				+ " where id = ? ";
		try {
			startTransaction("updateSessionName");
			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] {session.getSessionName(), userId, session.getId()});
			updateQuickSessionName(session.getId(), session.getSessionName());
			toCommit = true;
		} catch (Exception e) {
			  
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
	}
	
	@Transactional(readOnly = false)
	public void updateTrack(SessionDayTimeAcadsBean session, String userId , String sessionDay) {
		
		startTransaction("updateTrack");
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		
		try {
			sql = "Update acads.sessions set "
					+ " track= ?, "
					+ " lastModifiedBy=?,"
					+ " lastModifiedDate=sysdate()"
					+ " where id = ? ";
			jdbcTemplate.update(sql, new Object[] {session.getTrack(),userId,session.getId()});
			
		} catch (Exception e) {
			  
		}
	}
	
	@Transactional(readOnly = true)
	public String getSessionDay(SessionDayTimeAcadsBean session){
		
		String sql = null;		
		try {
			sql = " SELECT c.dayName FROM acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day "
				+ " and sd.year = ? and sd.month = ? ";
			
			String sessionDay = (String) jdbcTemplate.queryForObject(sql,new Object[]{session.getDate(),session.getStartTime(),
								session.getYear(), session.getMonth()},String.class);
			return sessionDay;
			
		} catch (Exception e) {
			  
			return null;
		}
	}

	@Transactional(readOnly = true)
	public boolean isFacultyAvailable(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String facultyId = session.getFacultyId();

		String sql =  " SELECT COUNT(*) FROM acads.calendar c, acads.session_days sd "
					+ " WHERE c.date = ? "
					+ " AND CONCAT(c.date, sd.startTime) IN "
					+ " (SELECT CONCAT(date, time) FROM acads.facultyAvailability WHERE facultyId = ? ) "; //And date is not which faculty is not going to be available

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, facultyId},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> isFacultyNotTakingEarlyOrLastSession(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String facultyId = session.getFacultyId();
		ArrayList<SessionDayTimeAcadsBean> sessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		String sql = "";
		if (session.getStartTime().equalsIgnoreCase("19:00:00")) {
			sql = " SELECT * FROM acads.sessions WHERE " + 
				  " startTime = '07:30:00' AND date = ? "+ //Early Session Of Day"
				  " AND (facultyId = ?  OR altFacultyId = ? OR altFacultyId2 = ?  OR altFacultyId3 = ? ) ";
		}else if(session.getStartTime().equalsIgnoreCase("07:30:00")) {
			sql = " SELECT * FROM acads.sessions WHERE " + 
				  " startTime = '19:00:00' AND date = ? " + //Last Session Of Day
				  " AND (facultyId = ?  OR altFacultyId = ? OR altFacultyId2 = ?  OR altFacultyId3 = ? ) ";
		}else {
			return sessionsList;
		}
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			sessionsList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{date,facultyId,facultyId,facultyId,facultyId},
							new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return sessionsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> isFacultyNotTakingEarlyOrLastSessionNextDay(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String facultyId = session.getFacultyId();
		ArrayList<SessionDayTimeAcadsBean> sessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		String sql = "";
		if (session.getStartTime().equalsIgnoreCase("19:00:00")) {
			sql = " SELECT * FROM acads.sessions WHERE " + 
				  " startTime = '07:30:00' " + //Early Session Of Day
				  " AND date = ADDDATE( ? , INTERVAL 1 DAY) " + //Next Day
				  " AND (facultyId = ?  OR altFacultyId = ? OR altFacultyId2 = ?  OR altFacultyId3 = ? ) ";
		}else if(session.getStartTime().equalsIgnoreCase("07:30:00")) {
			sql = " SELECT * FROM acads.sessions WHERE " + 
				  " startTime = '19:00:00' " + //Last Session Of Day
				  " AND date = SUBDATE( ? , INTERVAL 1 DAY) " + //Previous Day
				  " AND (facultyId = ?  OR altFacultyId = ? OR altFacultyId2 = ?  OR altFacultyId3 = ? ) ";
		}else {
			return sessionsList;
		}
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			sessionsList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{date,facultyId,facultyId,facultyId,facultyId},
							new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return sessionsList;
	}

	@Transactional(readOnly = true)
	public boolean isNotMoreThan3CommonSubjectsSameDayByCorporateName(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String isAdditionalSession = session.getIsAdditionalSession();
		String corporateName = session.getCorporateName();
		if("Y".equalsIgnoreCase(isAdditionalSession)){
			//Additional session can be held along with same semester subjects
			return true;
		}
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(date);
		

		String sql = "select count(*) from acads.calendar c "
				+ " where c.date = ? ";

		if(getGroup1SubjectList().contains(session.getSubject())){
			sql = sql + " and c.date not in (select date from acads.sessions "
					+ "						 where subject in (select subjectname from exam.subjects where commonSubject = 'G1')"
					+ "						 and corporateName = ?  and hasModuleId = ? "
					+ "						 and year = ? and month = ? "
					+ "						 group by date having count(date) > 2) "; //Not more than 3 sessions of common subjects
			parameters.add(corporateName);
			parameters.add(session.getHasModuleId());
			parameters.add(session.getYear());
			parameters.add(session.getMonth());
			
		}else if(getGroup1SubjectList().contains(session.getSubject())){
			sql = sql +" and c.date not in (select date from acads.sessions "
					+ "						 where subject in (select subjectname from exam.subjects where commonSubject = 'G2' ) "
					+ "						 and corporateName = ?   and hasModuleId = ? "
					+ "						 and year = ? and month = ? "
					+ "						 group by date having count(date) > 2) "; //Not more than 3 sessions of common subjects
			parameters.add(corporateName);
			parameters.add(session.getHasModuleId());
			parameters.add(session.getYear());
			parameters.add(session.getMonth());
		}

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrackV1(SessionDayTimeAcadsBean session){ 

		String date = session.getDate();
		String isAdditionalSession = session.getIsAdditionalSession();
		String subject = session.getSubject();
		String track = session.getTrack();
		
		String consumer_type = session.getCorporateName();
	
		ArrayList<Object> parameters = new ArrayList<Object>();
		int subject_limit = 2;
		//Check this
		if("Y".equalsIgnoreCase(isAdditionalSession)){
			//Additional session can be held along with same semester subjects
			return true;
		}
		 
		
		String sql =  " Select count(*) from acads.calendar c "
					+ " where c.date = ?  AND c.date NOT IN(SELECT  s.date FROM acads.sessions s " 
					+ " join exam.program_sem_subject pss on "
					+ " s.subject = pss.subject  where s.date = ? ";
						
		parameters.add(date);
		parameters.add(date);
		
		if(StringUtils.isBlank(session.getHasModuleId())) {
			sql = sql +" and (s.hasModuleId is null or s.hasModuleId <> 'Y') ";
		}else {
			sql = sql +" and s.hasModuleId = ? ";
			parameters.add(session.getHasModuleId());
		}
		
		//Added to check session is cancelled or Not
		sql = sql + " AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";
		
		if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type) ) {
			sql = sql + " and (corporateName = '' or corporateName = 'All')  ";
		}else{
			sql = sql + " and corporateName = ? ";
			parameters.add(consumer_type);

		}
			sql = sql   + " and pss.active = 'Y' " 
						+ " and pss.consumerProgramStructureId in "
						+ " (select distinct concat(pss.consumerProgramStructureId) from exam.program_sem_subject pss, " 
						+ " exam.consumer_type ct, exam.consumer_program_structure cps "  
						+ " where pss.active = 'Y' " 
						+ " and pss.consumerProgramStructureId = cps.id " 
						+ " and cps.consumerTypeId = ct.id  and ct.name = ? "
						+ " ) "; 

		if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type)) {
			consumer_type = "Retail";
		}
			parameters.add(consumer_type);
			
		//Check studentType Regular or TimeBound
		if(StringUtils.isBlank(session.getStudentType())){
		sql = sql + " and (studentType is null or studentType = 'Regular') ";
			//parameters.add(null);
 		}else{
			sql = sql + "and studentType = ? ";
 			parameters.add(session.getStudentType());
 		}
			
		//Check session Track
		if (!StringUtils.isBlank(track)) {
			sql = sql + "and (s.track = '' or  s.track = ?) "; 
			parameters.add(track);
		}else{
			sql = sql + "and s.track = '' "; 	
		}
		
		//Checks For MBA Batch
		if("Y".equalsIgnoreCase(session.getHasModuleId())) {
			sql = sql +"and s.moduleId  in (" + 
					"					SELECT " + 
					"						aspm.id" + 
					"					FROM " + 
					"						acads.sessionplan_module aspm" + 
					"					WHERE " + 
					"						aspm.sessionPlanId in(" + 
					"							SELECT " + 
					"								sessionPlanId " + 
					"							FROM " + 
					"								acads.sessionplanid_timeboundid_mapping  astm" + 
					"							where" + 
					"								astm.timeboundId in(" + 
					"										select " + 
					"												lssc.id" + 
					"										from  " + 
					"											 lti.student_subject_config lssc 	" + 
					"										where " + 
					"											concat(lssc.batchId,'-',lssc.sequence) in (" + 
					"												select " + 
					"													concat(lssc.batchId,'-',lssc.sequence)  " + 
					"												from  " + 
					"													lti.student_subject_config lssc " + 
					"												where " + 
					"													lssc.id in ( " + 
					"															SELECT " + 
					"																timeboundId " + 
					"															FROM " + 
					"																acads.sessionplanid_timeboundid_mapping  " + 
					"															where " + 
					"																sessionPlanId in ( " + 
					"																		SELECT" + 
					"																			asm.sessionPlanId  " + 
					"																		FROM " + 
					"																			acads.sessionplan_module asm  " + 
					"																		where " + 
					"																			asm.id=? " + 
					"																		) " + 
					"															) " + 
					"												)" + 
					"										)" + 
					"								)" + 
					"                            )";
			parameters.add(session.getSessionModuleNo());
			sql = sql + "group by concat(pss.consumerProgramStructureId,pss.sem) having count(concat(pss.consumerProgramStructureId,pss.sem)) > 0) ";
	
		}else {
		sql = sql  + "and s.subject in( ";
		/*if("Y".equalsIgnoreCase(session.getHasModuleId())) {
			sql = sql + "SELECT " + 
	"						epss.subject" + 
	"					FROM " + 
	"						exam.program_sem_subject epss " + 
	"					WHERE " + 
	"						epss.active ='Y' " + 
	"                            and epss.id in(" + 
	"								select " + 
	"										lssc.prgm_sem_subj_id  " + 
	"								from  " + 
	"									 lti.student_subject_config lssc " + 
	"										" + 
	"								where " + 
	"									lssc.batchId in (" + 
	"										select " + 
	"											lssc.batchId  " + 
	"										from  " + 
	"											lti.student_subject_config lssc " + 
	"										where " + 
	"											lssc.id in ( " + 
	"													SELECT " + 
	"														timeboundId " + 
	"													FROM " + 
	"														acads.sessionplanid_timeboundid_mapping  " + 
	"													where " + 
	"														sessionPlanId in ( " + 
	"																SELECT" + 
	"																	asm.sessionPlanId  " + 
	"																FROM " + 
	"																	acads.sessionplan_module asm  " + 
	"																where " + 
	"																	asm.id=? " + 
	"																) " + 
	"													) " + 
	"										)" + 
	"								)" + 
	"						)";
			parameters.add(session.getSessionModuleNo());
		}else {*/
			sql = sql + " select subject from exam.program_sem_subject " 
							+ " where active = 'Y' and concat(consumerProgramStructureId,sem) in ( "
							+ " select distinct concat(pss.consumerProgramStructureId,pss.sem) "
							+ " from exam.program_sem_subject pss, " 
							+ " exam.consumer_type ct, exam.consumer_program_structure cps "
							+ " where pss.subject =? and pss.active = 'Y' " 
							+ " and pss.consumerProgramStructureId = cps.id " 
							+ " and cps.consumerTypeId = ct.id  and ct.name = ? ";
			parameters.add(subject);
			parameters.add(consumer_type);
			if(StringUtils.isBlank(session.getStudentType())){
				sql = sql + " and (studentType is null or studentType = 'Regular')) and (studentType is null or studentType = 'Regular'))";
//				parameters.add(null);
//				parameters.add(null);
 			}else{
				sql = sql + " and studentType=? ) and studentType=? ) ";
 				parameters.add(session.getStudentType());
 				parameters.add(session.getStudentType());
 			}
		//}
		
		sql = sql + " group by concat(pss.consumerProgramStructureId,pss.sem) having count(concat(pss.consumerProgramStructureId,pss.sem)) > ?) ";
		
		parameters.add(subject_limit);
		}
		try {
			Object[] args = parameters.toArray();
			jdbcTemplate = new JdbcTemplate(dataSource);

			int count = (int) jdbcTemplate.queryForObject(sql,args,Integer.class);

			if(count == 0){
				return false;
			}else{
				return true;
			}			
		} catch (DataAccessException e) {
			  
			return false;
		}	
	}
	
	@Transactional(readOnly = true)
	public List<String> getTracks(SessionDayTimeAcadsBean session){
		String sql = "SELECT track FROM acads.sessions WHERE year = ? AND month = ? GROUP BY track";
		List<String> tracks = null;
		try {
			tracks = jdbcTemplate.queryForList(sql, new Object[]{session.getYear(), session.getMonth()}, String.class);
			return tracks;
		} catch (DataAccessException e) {
			  
			return tracks;
		}
	}

	@Transactional(readOnly = true)
	public boolean isNotMoreThan3CommonSubjectsSameDayByTrack(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String isAdditionalSession = session.getIsAdditionalSession();
		ArrayList<Object> parameters = new ArrayList<Object>();

		if("Y".equalsIgnoreCase(isAdditionalSession)){
			//Additional session can be held along with same semester subjects
			return true;
		}

		String sql = "select count(*) from acads.calendar c where c.date = ? ";
		
		parameters.add(date);
		
		if(getGroup1SubjectList().contains(session.getSubject())){
			sql = sql + " and c.date not in (select date from acads.sessions"
											+ " where track = ? "
											+ " and (isCancelled <> 'Y' OR isCancelled IS NULL)"
											+ "	and subject in (select subjectname from exam.subjects"
											+ "					 where commonSubject = 'G1') and hasModuleId =?"
											+ " group by date having count(date) > 2) "; //Not more than 3 sessions of common subjects
			parameters.add(session.getTrack());
			parameters.add(session.getHasModuleId());
		}
		//else if(getGroup1SubjectList().contains(session.getSubject())){ commented by Pranit on 26 Dec, ask Sir/Steffi why this was added?
		else {
			sql = sql +" and c.date not in (select date from acads.sessions"
					+ "						 where track = ? "
					+ "						 and (isCancelled <> 'Y' OR isCancelled IS NULL) "
					+ "						 and subject in (select subjectname from exam.subjects"
					+ "										  where commonSubject = 'G2' ) and hasModuleId =?"
					+ " group by date having count(date) > 2) "; //Not more than 3 sessions of common subjects
			parameters.add(session.getTrack());
			parameters.add(session.getHasModuleId());
		}
		Object[] args = parameters.toArray();
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql,args,Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)
	public boolean isFacultyTakingLessThan2SubjectsSameDay(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();

		String sql =  " SELECT COUNT(*) FROM acads.calendar c "
					+ " WHERE c.date = ? "
					+ " AND c.date NOT IN "
					+ " (SELECT date FROM acads.sessions WHERE (isCancelled <> 'Y' OR isCancelled IS NULL) "
					+ " AND (facultyId = ? OR altFacultyId = ? OR altFacultyId2 = ? OR altFacultyId3 = ? )"
					+ " GROUP BY date HAVING COUNT(date) > 1) "; //Ensure faculty is not taking more than 2 sessions on same day

		jdbcTemplate = new JdbcTemplate(dataSource);
//		System.out.println("sql isFacultyTakingLessThan2SubjectsSameDay "+sql);
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date,facultyId,facultyId,facultyId,facultyId},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)
	public boolean isFacultyFree(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String year = session.getYear();
		String month = session.getMonth();
		String facultyId = session.getFacultyId();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions "
				+ " where year = ? and month = ? and facultyId = ?) " //Ensure that faculty is not taking any other session at same time
				+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, year, month, facultyId},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean isFacultyFreeAllChecks(SessionDayTimeAcadsBean session) {
		
		String year = session.getYear();
		String month = session.getMonth();
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();
		String corporate = session.getCorporateName();
		int sessionTime = getSessionTime(session);
		
		/*
		 * Updated for Zoom by Pranit on 26 Dec
		 * Getting the count of sessions for which the faculty is allocated for given date time
		 * here sessions startime and endtime is checked with the uploaded date and time 
		 * */
		String sql = " select count(*) from acads.sessions s " ;
			
			//Commented by Somesh
			//Check year month from UI Inputs
			/*
			if ("SAS".equalsIgnoreCase(corporate)) {
				sql = sql + " where s.year =  "+CURRENT_ACAD_YEAR_SAS+"  " + 
						"	  and s.month = '"+CURRENT_ACAD_MONTH_SAS+"' " ;
			} else {
				sql = sql+	" where s.year =  "+CURRENT_ACAD_YEAR+"  " + 
						"	  and s.month = '"+CURRENT_ACAD_MONTH+"'  " ;
			}
			*/
		
			sql = sql + " WHERE s.year = ? AND s.month = ? "
					  + " AND (s.facultyId = ? OR s.altFacultyId= ? OR s.altFacultyId2= ? OR s.altFacultyId3= ?) "
					  + " and ( "
					  + " cast(CONCAT(date,' ', startTime) as datetime)   "
					  + " between  "
					  + " (select cast(CONCAT(c.date,' ', sd.startTime) as datetime) as dt from acads.calendar c, acads.session_days sd     "
					  + " where c.date =  ? and sd.startTime =   ?  group by dt )  "
					  + " and  "
					  + " (select DATE_ADD(cast(CONCAT(c.date,' ', sd.startTime) as datetime), INTERVAL "+sessionTime+" MINUTE) as dt from acads.calendar c, acads.session_days sd    "
					  + " where c.date =  ? and sd.startTime =   ?   group by dt )  "
					  + " AND (s.isCancelled <> 'Y' or s.isCancelled is null))  ";
			
		/* Commented by Pranit on 26 Dec kept for refrence
		 * String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) "
				+ "											 	from acads.sessions "
				+ "													where year = " + CURRENT_ACAD_YEAR + " "
						+ "											and month = '" + CURRENT_ACAD_MONTH + "' "
								+ "									and (facultyId = ? or altFacultyId=? or altFacultyId2=? or altFacultyId3=?)"
								+ "							) " //Ensure that faculty is not taking any other session at same time
				+ " order by c.date, sd.startTime";*/

		jdbcTemplate = new JdbcTemplate(dataSource);
//		System.out.println("sql isFacultyFreeAllChecks "+sql);
		int count = 1;
		try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{year, month, facultyId, facultyId, facultyId, facultyId, 
													date, time, date, time},Integer.class);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			count = 1;
		}
		
		if(count == 0){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean isFacultyFreeAllChecksV2(SessionDayTimeAcadsBean session) {
		String year = session.getYear();
		String month = session.getMonth();
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();
		String corporate = session.getCorporateName();
		int sessionTime = getSessionTime(session);
		int sessionTimeInSec = sessionTime * 60;
		
		String sql =" SELECT " +
					"	COUNT(*) " + 
					" FROM " + 
					"    acads.sessions s " + 
					" WHERE " + 
//					"    s.year = ? AND s.month = ? " + 
					"        (s.facultyId = ? OR s.altFacultyId = ? OR s.altFacultyId2 = ? OR s.altFacultyId3 = ?) " + 
					"        AND (CAST(CONCAT(date, ' ', startTime) AS DATETIME)  " + 
					"			BETWEEN (CAST(CONCAT(?, ' ', ?) AS DATETIME))  " + 
					"            AND (DATE_ADD(CAST(CONCAT(?, ' ', ?) AS DATETIME), INTERVAL "+sessionTime+" MINUTE)) " + 
					"        OR CAST(CONCAT(date, ' ', endTime) AS DATETIME)  " + 
					"			BETWEEN (DATE_SUB(CAST(CONCAT(?, ' ', ADDTIME(?, SEC_TO_TIME("+sessionTimeInSec+"))) AS DATETIME), INTERVAL "+sessionTime+" MINUTE))  " + 
					"            AND (CAST(CONCAT(?, ' ', ADDTIME(?, SEC_TO_TIME("+sessionTimeInSec+"))) AS DATETIME))) ";
	
		int count = 1;
		try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{ facultyId, facultyId, facultyId, facultyId, 
													date, time, date, time, date, time, date, time},Integer.class);
		} catch (DataAccessException e) {
			loggerForSessionScheduling.info("Error in isFacultyFreeAllChecksV2 "+e.getMessage());
			e.printStackTrace();
			count = 1;
		}
//		System.out.println("sql isFacultyFreeAllChecksV2 "+sql);
		if(count == 0){
			return true;
		}else{
			return false;
		}
	
	}

	@Transactional(readOnly = true)
	public boolean isNotHoliday(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();

		String sql = "select count(*) from acads.calendar c where  (c.isHoliday is null or c.isHoliday <> 'Y' )  and c.date = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

/* 
 * stef	commented
 * public List<SessionReviewBean> reviewListBasedOnCriteria(SessionReviewBean reviewBean){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = " select asr.*,s.subject from acads.session_review asr,acads.faculty af , acads.sessions s"
				+ " where asr.reviewerFacultyId = af.facultyId and s.id = asr.sessionId";
		StringBuffer sBuffer = new StringBuffer(sql);
		MapSqlParameterSource namedParams= new MapSqlParameterSource();

		if(!StringUtils.isBlank(reviewBean.getReviewerFacultyId())){
			namedParams.addValue("reviewerFacultyId",reviewBean.getReviewerFacultyId().trim());
			sBuffer.append(" and asr.reviewerFacultyId = :reviewerFacultyId ");
		}
		if(!StringUtils.isBlank(reviewBean.getReviewed())){
			namedParams.addValue("reviewed",reviewBean.getReviewed().trim());
			sBuffer.append(" and asr.reviewed =:reviewed ");
		}
		if(!StringUtils.isBlank(reviewBean.getReviewerName())){
			sBuffer.append(" and ( af.firstName Like :firstName  or af.lastName Like :lastName ) " );
			namedParams.addValue("firstName","%"+reviewBean.getReviewerName().trim()+"%");
			namedParams.addValue("lastName","%"+reviewBean.getReviewerName().trim()+"%");
		}
		
		sBuffer.append(" order by asr.id ");
		return namedParameterJdbcTemplate.query(sBuffer.toString(),namedParams,new BeanPropertyRowMapper(SessionReviewBean.class));
	}*
	*
	*/

	
/*stef added on Sep-2017
	public List<SessionReviewBean> reviewListBasedOnCriteria(SessionReviewBean reviewBean){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = " select asr.*,s.subject from acads.session_review asr,acads.faculty af , acads.sessions s"
				+ " where asr.reviewerFacultyId = af.facultyId and s.id = asr.sessionId";
		StringBuffer sBuffer = new StringBuffer(sql);
		MapSqlParameterSource namedParams= new MapSqlParameterSource();

		if(!StringUtils.isBlank(reviewBean.getSubject())){
			sBuffer.append(" and s.subject = :subject ");
			namedParams.addValue("subject",reviewBean.getSubject().trim());
		}
		if(!StringUtils.isBlank(reviewBean.getMonth())){
			sBuffer.append(" and s.month = :month ");
			namedParams.addValue("month",reviewBean.getMonth().trim());
		}
		if(!StringUtils.isBlank(reviewBean.getYear())){
			sBuffer.append(" and s.year = :year ");
			namedParams.addValue("year",reviewBean.getYear().trim());
		}
	
		if(!StringUtils.isBlank(reviewBean.getFirstName())){
			sBuffer.append(" and ( af.firstName Like :firstName ) " );
			namedParams.addValue("firstName","%"+reviewBean.getFirstName().trim()+"%");
			
		}
		
		if(!StringUtils.isBlank(reviewBean.getLastName())){
			sBuffer.append(" and ( af.lastName Like :lastName ) " );
			namedParams.addValue("lastName","%"+reviewBean.getLastName().trim()+"%");
		}
		
		if(!StringUtils.isBlank(reviewBean.getQ1Response())){
			sBuffer.append(" and asr.q1Response = :q1Response " );
			namedParams.addValue("q1Response",reviewBean.getQ1Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ2Response())){
			sBuffer.append(" and asr.q2Response  = :q2Response " );
			namedParams.addValue("q2Response",reviewBean.getQ2Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ3Response())){
			sBuffer.append(" and asr.q3Response =  :q3Response " );
			namedParams.addValue("q3Response",reviewBean.getQ3Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ4Response())){
			sBuffer.append(" and asr.q4Response = :q4Response " );
			namedParams.addValue("q4Response",reviewBean.getQ4Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ5Response())){
			sBuffer.append(" and asr.q5Response = :q5Response  " );
			namedParams.addValue("q5Response",reviewBean.getQ5Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ6Response())){
			sBuffer.append(" and asr.q6Response = :q6Response " );
			namedParams.addValue("q6Response",reviewBean.getQ6Response().trim());	
		}
		
		sBuffer.append(" order by asr.id ");

		return namedParameterJdbcTemplate.query(sBuffer.toString(),namedParams,new BeanPropertyRowMapper(SessionReviewBean.class));
	}---------------------*/

	@Transactional(readOnly = true)
	public boolean isSlotAvailable(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String year = session.getYear();
		String month = session.getMonth();

		String sql =  " select count(*) from acads.calendar c, acads.session_days sd "
					+ " where c.date = ? and sd.startTime =  ? "
					+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions "
					+ " WHERE year = ? AND month = ? GROUP BY date, startTime having count(*) = 6 ) " //Ensure that time slot does not already have 5 sessions scheduled
					+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, year, month},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)
	public boolean isNoSubjectClashingV1(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String subject = session.getSubject();
		String isAdditionalSession = session.getIsAdditionalSession();
		String corporateName = session.getCorporateName(); //added on 24Jan19 by Pranit to check clashes corporate wise.
		String consumerType = corporateName;
		
		if("Y".equalsIgnoreCase(isAdditionalSession)){
			//Additional session can be held along with same semester subjects
			return true;
		}
		
		String sqlForCorporateName = "";
		
		if ("".equalsIgnoreCase(consumerType) || "All".equalsIgnoreCase(consumerType)) {
			consumerType = "Retail";
		}

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(date);
		parameters.add(time);
		if ("".equalsIgnoreCase(corporateName) || "All".equalsIgnoreCase(corporateName)) {
			sqlForCorporateName = " and (corporateName = '' or corporateName ='All') ";
		}else{
			sqlForCorporateName = " and corporateName = ? ";
			parameters.add(corporateName);
		}
		String track = session.getTrack();
		String track_check_sql =  " "; 
		if (!StringUtils.isBlank(track)) {
			track_check_sql = "  and (track = '' or  track = ?) "; 
			parameters.add(track);
		}
		
		String sql = "";
				if ("SAS".equalsIgnoreCase(corporateName)) {
			
			sql = "select count(*) from acads.calendar c, acads.session_days sd "
					+ " where c.date = ? and sd.startTime =  ? and year = " + CURRENT_ACAD_YEAR_SAS + " and month = '" + CURRENT_ACAD_MONTH_SAS + "' "
					+ " and CONCAT(c.date, sd.startTime) not in"
					+ "  (select CONCAT(date, startTime) "
					+ "		from acads.sessions "
					+ "		where year = " + CURRENT_ACAD_YEAR_SAS + " and month = '" + CURRENT_ACAD_MONTH_SAS + "' "
					+ sqlForCorporateName
					+ track_check_sql
					+ " 	and (isCancelled <> 'Y' OR isCancelled IS NULL)"
					+ "		and subject in "
					+ "			(select distinct subject from exam.program_sem_subject  "
					+ "				where active = 'Y' and concat(consumerProgramStructureId,sem) in "
					+ "  				(select distinct concat(pss.consumerProgramStructureId,pss.sem) from exam.program_sem_subject pss,  "
					+ "						exam.consumer_type ct, exam.consumer_program_structure cps "
					+ "						where pss.subject = ? and pss.active = 'Y' "
					+ "						and pss.consumerProgramStructureId = cps.id "
					+ "						and cps.consumerTypeId = ct.id  and ct.name = ? )"
					+ "	 		)"
					+ "	 ) " // No other subject of same semester is scheduled at that time
					+ " order by c.date, sd.startTime";
			parameters.add(subject);
		}else{
			sql = " select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? and year = " + CURRENT_ACAD_YEAR + " and month = '" + CURRENT_ACAD_MONTH + "' "
				+ " and CONCAT(c.date, sd.startTime) not in "
				+ " (select CONCAT(date, startTime) "
				+ "	from acads.sessions s "
				+ "	where s.year = " + CURRENT_ACAD_YEAR + " and s.month = '" + CURRENT_ACAD_MONTH + "' "
				+ sqlForCorporateName
				+ track_check_sql
				+ " and (isCancelled <> 'Y' OR isCancelled IS NULL) ";
			
				if("Y".equalsIgnoreCase(session.getHasModuleId())) {
					sql = sql +"and s.moduleId  in (" + 
							"					SELECT " + 
							"						aspm.id" + 
							"					FROM " + 
							"						acads.sessionplan_module aspm" + 
							"					WHERE " + 
							"						aspm.sessionPlanId in(" + 
							"							SELECT " + 
							"								sessionPlanId " + 
							"							FROM " + 
							"								acads.sessionplanid_timeboundid_mapping  astm" + 
							"							where" + 
							"								astm.timeboundId in(" + 
							"										select " + 
							"												lssc.id" + 
							"										from  " + 
							"											 lti.student_subject_config lssc 	" + 
							"										where " + 
							"											concat(lssc.batchId,'-',lssc.sequence) in (" + 
							"												select " + 
							"													concat(lssc.batchId,'-',lssc.sequence)  " + 
							"												from  " + 
							"													lti.student_subject_config lssc " + 
							"												where " + 
							"													lssc.id in ( " + 
							"															SELECT " + 
							"																timeboundId " + 
							"															FROM " + 
							"																acads.sessionplanid_timeboundid_mapping  " + 
							"															where " + 
							"																sessionPlanId in ( " + 
							"																		SELECT" + 
							"																			asm.sessionPlanId  " + 
							"																		FROM " + 
							"																			acads.sessionplan_module asm  " + 
							"																		where " + 
							"																			asm.id=? " + 
							"																		) " + 
							"															) " + 
							"												)" + 
							"										)" + 
							"								)" + 
							"                            )";
					parameters.add(session.getSessionModuleNo());
					
			}else {
					
					// No other subject of same semester is scheduled at that time
					sql = sql + "and s.subject in "
							+ " 	(select distinct subject from exam.program_sem_subject  "
							+ "				where active = 'Y' and concat(consumerProgramStructureId,sem) in "
							+ "  				(select distinct concat(pss.consumerProgramStructureId,pss.sem) from exam.program_sem_subject pss, "
							+ "						exam.consumer_type ct, exam.consumer_program_structure cps "
							+ "						where pss.subject = ? and pss.active = 'Y'  "
							//Added for avoid subject clashing with MBA-X(119) subjects
							+ "						and pss.consumerProgramStructureId NOT IN ('119' , '126') "
							+ "						and pss.consumerProgramStructureId = cps.id "
							+ "						and cps.consumerTypeId = ct.id  and ct.name = ? ) "
							+ " 	)";
					parameters.add(subject);
					parameters.add(consumerType);
				}
				
				if (StringUtils.isBlank(session.getHasModuleId())) {
					sql = sql + " and (hasModuleId is null or hasModuleId = 'N') ";
				} else {
					sql = sql + " and hasModuleId = ? ";
					parameters.add(session.getHasModuleId());
				}
					
				sql = sql + " ) order by c.date, sd.startTime";
				
		}
				
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count;
		try {
			count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
			if(count == 0){
				return false;
			}else{
				return true;
			}
			
		} catch (DataAccessException e) {
			  
			return false;
		}
	}

	@Transactional(readOnly = true)
	public boolean isNoSubjectClashingByTrack(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String subject = session.getSubject();
		String isAdditionalSession = session.getIsAdditionalSession();

		if("Y".equalsIgnoreCase(isAdditionalSession)){
			//Additional session can be held along with same semester subjects
			return true;
		}
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(date);
		parameters.add(time);
		parameters.add(session.getTrack());
		parameters.add(session.getYear());
		parameters.add(session.getMonth());
		parameters.add(subject);

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where track = ? and year = ? and month = ? "
				+ " and subject in (select distinct subject  from exam.program_subject where concat(program,sem,prgmStructApplicable)"
				+ "  in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ? and active = 'Y') )) " // No other subject of same semester is scheduled at that time
				+ " order by c.date, sd.startTime";
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	//isSubjectClashingWithSameSubjectOfOtherTrack
	@Transactional(readOnly = true)
		public boolean isSubjectClashingWithSameSubjectOfOtherTrack(SessionDayTimeAcadsBean session) {
			String date = session.getDate();
			String time = session.getStartTime();
			String subject = session.getSubject();
			String isAdditionalSession = session.getIsAdditionalSession();
			int sessionTime = getSessionTime(session);

			if("Y".equalsIgnoreCase(isAdditionalSession)){
				//Additional session can be held along with same semester subjects
				return true;
			}
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(session.getTrack());
			parameters.add(session.getYear());
			parameters.add(session.getMonth());
			parameters.add(subject);
			parameters.add(date);
			
			String sql = "select count(*) from acads.sessions "
					+ " where track <> ? and year = ? and month = ? "
					+ " and subject = ? and date = ? "
					+ " and "
					+ "	("
					+ "		cast(CONCAT(date,' ', startTime) as datetime)"
					+ "		 between cast(CONCAT(\""+date+"\",' ', \""+time+"\") as datetime)"
						+ "  and DATE_ADD(cast(CONCAT(\""+date+"\",' ', \""+time+"\") as datetime), INTERVAL \"+sessionTime+\" MINUTE)		"
					+ "	 or  "
							+ "	cast(CONCAT(date,' ', endTime) as datetime)"
							+ "	 between cast(CONCAT(\""+date+"\",' ', \""+time+"\") as datetime)"
							+ "  and DATE_ADD(cast(CONCAT(\""+date+"\",' ', \""+time+"\") as datetime), INTERVAL \"+sessionTime+\" MINUTE)		"
					+ " ) ";
			
			jdbcTemplate = new JdbcTemplate(dataSource);

			int count= 1;
			try {
				count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				  
			}

			if(count == 0){
				return false;
			}else{
				return true;
			}
		}
		
		@Transactional(readOnly = true)
		public boolean isDateTimeValid(SessionDayTimeAcadsBean session) {
			String date = session.getDate();
			String time = session.getStartTime();
			String year = session.getYear();
			String month = session.getMonth();
			String corporateType = session.getCorporateName();
			String sql = "";
			
			if ("SAS".equalsIgnoreCase(corporateType)) {
				
				sql = "select count(*) from acads.calendar c, acads.session_days sd "
						+ " where c.date = ? and sd.startTime =  ? "
						+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
						+ " sd.year = ? and sd.month = ? " //Academic calendar
						+ " and c.date >= (select startDate from acads.academic_calendar where year = ? and month = ? ) "
						+ " and c.date <= (select endDate from acads.academic_calendar where year = ? and month = ? ) " //Only those dates that lie between start and end day of Academic Calendar
						+ " and c.date >= curdate() "//Date is NOT in past
						+ " order by c.date, sd.startTime";
				
			} else {
					sql = "select count(*) from acads.calendar c, acads.session_days sd "
						+ " where c.date = ? and sd.startTime =  ? "
						+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
						+ " sd.year = ?  and sd.month = ? " //Academic calendar
						+ " and c.date >= (select startDate from acads.academic_calendar where year = ? and month = ?) "
						+ " and c.date <= (select endDate from acads.academic_calendar where year = ? and month = ?) " //Only those dates that lie between start and end day of Academic Calendar
						+ " and c.date >= curdate() "//Date is NOT in past
						+ " order by c.date, sd.startTime";
			}
			jdbcTemplate = new JdbcTemplate(dataSource);

			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, year, month, year, month, year, month },Integer.class);

			if(count == 0){
				return false;
			}else{
				return true;
			}
		}

	/*public void updateFacultyUnavailability(FacultyUnavailabilityBean faculty) {
		String sql = "Update acads.facultyunavailabilitydates set "
				+ " facultyId = ?,"
				+ " unavailabilityDate = ?"
				+ " where id= ? ";



		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				faculty.getFacultyId(),
				faculty.getUnavailabilityDate(),
				faculty.getId()

		});

	}*/

	@Transactional(readOnly = false)
	public void updateFacultyAvailability(FacultyAvailabilityBean faculty, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " Update acads.facultyAvailability set facultyId = ?, date = ?, time = ?, "
					+ " lastModifiedBy= ?, lastModifiedDate=sysdate() where id = ? ";
		jdbcTemplate.update(sql, new Object[] {faculty.getFacultyId(),faculty.getDate(),faculty.getTime(),userId, faculty.getId()});
	}

	@Transactional(readOnly = true)
	public ArrayList<EndPointBean> getAllFacultyRoomEndPoints() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.endpoint where type = 'Faculty Room'";
		ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(EndPointBean.class));
		return endPointList;
	}

	@Transactional(readOnly = true)
	public ArrayList<EndPointBean> getAvailableRoom(SessionDayTimeAcadsBean sessionBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT * FROM acads.endpoint where type = 'Faculty Room' and name not in "
					+ " (Select  COALESCE(room,'') from acads.sessions "
					+ " where( " 
					+ " 	(cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
					+ "			or   " 
					+ "		(cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and addtime(?,   \""+sessionBufferUpperLimit+"\") )  " 
					+ "		) " 
					+ " and date =  ? " 
					+ " ) ";
		try{
			  String startTime =sessionBean.getStartTime(); 
			  String date = sessionBean.getDate();
		      ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new Object[]{startTime,startTime,startTime,startTime, date}, new BeanPropertyRowMapper(EndPointBean.class));
			  return endPointList;
		}catch(Exception e){
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public ArrayList<EndPointBean> getAvailableRoomByLoaction2(String date, String startTime, String endTime, String location, String sessionType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " SELECT *  " + 
				" FROM acads.endpoint e  " + 
				" where e.type = 'Faculty Room' AND e.sessionType = ?  " + 
				"	and  e.hostId not in (  " + 
				"		select COALESCE(hostId,'') from acads.sessions    " + 
				"			where ( " + 
				"				  (cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
				+ "					 or   " + 
				"				  (cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and addtime(?,   \""+sessionBufferUpperLimit+"\") )  " + 
				"				  ) " + 
				" 				  and date =  ?    " + 
				"   ) " + 
				"    and e.hostId not in (  " + //altHostId
				"		select COALESCE(altHostId,'') from acads.sessions    " + 
				"			where ( " + 
				"				  (cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
				+ "					 or   " + 
				"				  (cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and  addtime(?,   \""+sessionBufferUpperLimit+"\") )  " + 
				"				  ) " + 
				" 				  and date =  ?    " + 
				"   ) " + 
				"    and e.hostId not in (  " + //altHostId2
				"		select COALESCE(altHostId2,'') from acads.sessions    " + 
				"			where ( " + 
				"				  (cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
				+ "					 or   " + 
				"				  (cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and  addtime(?,   \""+sessionBufferUpperLimit+"\") )  " + 
				"				  ) " + 
				" 				  and date =  ?    " + 
				"   ) " + 
				"    and e.hostId not in (  " + //altHostId3
				"		select COALESCE(altHostId3,'') from acads.sessions    " + 
				"			where ( " + 
				"				  (cast(startTime as time) between addtime(?,  \""+sessionBufferLowerLimit+"\") and  addtime(?,  \""+sessionBufferUpperLimit+"\") )"
				+ "					 or   " + 
				"				  (cast(endTime as time) between addtime(?,   \""+sessionBufferLowerLimit+"\") and  addtime(?,   \""+sessionBufferUpperLimit+"\") )  " + 
				"				  ) " + 
				" 				  and date =  ?    " + 
				"   ) "  
				;
		try{
		      ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new Object[]
		    		  											{sessionType, startTime,startTime,startTime,startTime, date,
		    		  											 startTime,startTime,startTime,startTime, date,
		    		  											 startTime,startTime,startTime,startTime, date,
		    		  											 startTime,startTime,startTime,startTime, date}, 
		    		  													new BeanPropertyRowMapper(EndPointBean.class));
			 return endPointList;
		}catch(Exception e){
			loggerForSessionScheduling.info("Error in getAvailableRoomByLoaction2 : "+e.getMessage());
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EndPointBean> getAvailableRoomByLoaction(String date, String startTime, String location) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.endpoint where type = 'Faculty Room' and name not in " + 
				"			 (Select  COALESCE(room,'') from acads.sessions where "
				+ "			 (concat(?,\" \",?) between date_add(concat(date,\" \",startTime), interval 30 minute ) and date_add(concat(date,\" \",startTime), interval 30 minute) ) " + 
				"            and (select room_capacity from acads.location_room_capacity where location = ?)" + 
				"				<= ( " + //adding all the count of no of rooms already taken and checking with capacity of location
				"				   	(Select  count(*) from acads.sessions " + 
				"						where date = ?" + 
				"							  and startTime = ?" + 
				"                          	  and facultyLocation=?) " +
				"					+ 								 "+
				"				   	(Select  count(*) from acads.sessions " + 
				"						where date = ?" + 
				"							  and startTime = ?" + 
				"                          	  and altFacultyLocation=?) "+
				"					+ 									"+
				"				   	(Select  count(*) from acads.sessions " + 
				"						where date = ?" + 
				"							  and startTime = ?" + 
				"                          	  and altFaculty2Location=?) "+
				"					+ 									 "+
				"				   	(Select  count(*) from acads.sessions " + 
				"						where date = ?" + 
				"							  and startTime = ?" + 
				"                          	  and altFaculty3Location=?) "+
				"				   )"+
				"              )";
		try{
		      ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new Object[]{date,startTime,location,
		    		  																							   date,startTime,location,
		    		  																							   date,startTime,location,
		    		  																							   date,startTime,location,
		    		  																							   date,startTime,location}, 
		    		  																			new BeanPropertyRowMapper(EndPointBean.class));
			  
			 return endPointList;
		}catch(Exception e){
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public int getCapacityOfLocation(String location) {
		int count= 0;
		try {
			//id, location, room_capacity
			String sql = "select room_capacity from acads.location_room_capacity where location = ? ";
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{location},Integer.class);
		} catch (DataAccessException e) {
			  
		}
		return count;
	}
	
	@Transactional(readOnly = true)
	public int getNoOfSessionAtSameDateTimeLocation(String date, String time , String location, String sessionId) {
		
		//Added for Edit Session to avoid selected session which you're editing
		String sqlForEditSession = " ";
		if (!StringUtils.isBlank(sessionId)) {
			sqlForEditSession = " AND id <> '"+sessionId+"' ";
		}
		
		int count= 0;
		try {
			//id, location, room_capacity
			String sql = "select count(*) from acads.sessions "
					+ " 	where date = ? "
					+ "			and ( "+
					"				  (cast(startTime as time) between addtime(?,  \"00:00:00\") and  addtime(?,   \"02:00:00\") )"
					+ "					 or   " + 
					"				  (cast(endTime as time) between addtime(?,   \"00:00:00\") and  addtime(?,   \"02:00:00\") )  " + 
					"				) " 
					+ "			and facultyLocation = ? "
					+ sqlForEditSession ;
			count += (int) jdbcTemplate.queryForObject(sql, new Object[]{date,
																		time,time,time,time,
																		location},Integer.class);
			//altFacultyLocation
			String sql2 = "select count(*) from acads.sessions "
					+ " 	where date = ? "
					+ "			and ( "+
					"				  (cast(startTime as time) between addtime(?,  \"00:00:00\") and  addtime(?,   \"02:00:00\") )"
					+ "					 or   " + 
					"				  (cast(endTime as time) between addtime(?,   \"00:00:00\") and  addtime(?,   \"02:00:00\") )  " + 
					"				) " 
					+ "			and altFacultyLocation = ? ";
			count += (int) jdbcTemplate.queryForObject(sql2, new Object[]{date,
																			time,time,time,time,
																			location},Integer.class);
			//altFaculty2Location
			String sql3 = "select count(*) from acads.sessions "
					+ " 	where date = ? "
					+ "			and ( "+
					"				  (cast(startTime as time) between addtime(?,  \"00:00:00\") and  addtime(?,   \"02:00:00\") )"
					+ "					 or   " + 
					"				  (cast(endTime as time) between addtime(?,   \"00:00:00\") and  addtime(?,   \"02:00:00\") )  " + 
					"				) "  
					+ "			and altFaculty2Location = ? ";
			count += (int) jdbcTemplate.queryForObject(sql3, new Object[]{date,
																			time,time,time,time,
																			location},Integer.class);
			
			//altFaculty3Location
			String sql4 = "select count(*) from acads.sessions "
					+ " 	where date = ? "
					+ "			and ( "+
					"				  (cast(startTime as time) between addtime(?,  \"00:00:00\") and  addtime(?,   \"02:00:00\") )"
					+ "					 or   " + 
					"				  (cast(endTime as time) between addtime(?,   \"00:00:00\") and  addtime(?,   \"02:00:00\") )  " + 
					"				) " 
					+ "			and altFaculty3Location = ? ";
			count += (int) jdbcTemplate.queryForObject(sql4, new Object[]{date,
																			time,time,time,time,
																			location},Integer.class);
																	
		} catch (DataAccessException e) {
			loggerForSessionScheduling.info("Error getNoOfSessionAtSameDateTimeLocation :"+e.getMessage());
			count = 0;
		}
		return count;
	}

	@Transactional(readOnly = true)
	public ArrayList<EndPointBean> getLeastAllocateRoom(SessionDayTimeAcadsBean sessionBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String finalDate = "";
		String sql ="";
		try {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = (Date) formatter.parse(sessionBean.getDate());
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
		finalDate= newFormat.format(date);
		
		sql = "SELECT * FROM acads.endpoint where type = 'Faculty Room' and hostId in "
				+ " (Select  COALESCE(hostId,'') from acads.sessions where date = ? and not (hostId is null or hostId = '' ) "
				+ " group by hostId having count(hostId) < 3 ) ";
			
		ArrayList<EndPointBean> endPointList = (ArrayList<EndPointBean>)jdbcTemplate.query(sql, new Object[]{finalDate}, new BeanPropertyRowMapper(EndPointBean.class));
			return endPointList;
		
		}catch(Exception e){
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudents(ArrayList<String> subjects,StudentAcadsBean student,String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		StringBuffer sql = new StringBuffer(" ");
		
		//Querey Change For MBA Oct-2018 Sessions.
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql.append(" SELECT * FROM "
					 + " 	acads.faculty f, "
					 + " 	acads.sessions s, "
					 + " 	exam.examorder eo "
					 + " WHERE "
					 + "	(s.isCommon = 'N' or s.isCommon is null ) "
					 + "		AND  s.facultyId = f.facultyId "
					 + "		AND s.month = eo.acadmonth AND s.year = eo.year "
					 + " 		AND eo.order = ("
					 + "			SELECT o.order FROM exam.examorder o "
					 + "				WHERE acadMonth = '" + month + "' AND year = '" + year + "' AND acadSessionLive = 'Y') ");

		}else {
			sql.append( " SELECT * FROM " 
					  + "    acads.faculty f, " 
					  + "    acads.sessions s " + 
					  	" WHERE " 
					  + "    (s.isCommon = 'N' OR s.isCommon IS NULL) " 
					  + "  	 AND s.facultyId = f.facultyId");
		}
		
		
		//if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV")){
		//}else{
		//sql.append( " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ");
		//}
		
		sql.append(" and s.subject in ("+subjectCommaSeparated+") ");
		
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql.append(" and (s.corporateName = 'M.sc') ");
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql.append( " and (s.corporateName = 'Verizon' or s.corporateName = 'All') ");
				break;
				
			case "Diageo":
				sql.append( " and (s.corporateName = 'Diageo') ");
				break;
				
			case "BAJAJ":
				sql.append(" and (s.corporateName = 'BAJAJ') ");
				break;
			
			default :
				sql.append(" and ((s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' "
						 + " and s.corporateName <> 'M.sc' ) " //Added Temporary to avoid normal session in calendar
						 + " or s.corporateName is null or s.corporateName = '') ");
				break;
			}
		}

		if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())){
			sql.append( " and s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
						" INNER JOIN  acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
						" INNER JOIN acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
						" WHERE tum.userId = " + student.getSapid() + " and tum.role='Student')" );	
		}else{
			sql.append(" and (s.hasModuleId is null or s.hasModuleId <> 'Y') ");
		}
		
		sql.append(" order by s.date,s.startTime,s.subject ");
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
	//	if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV")){
		scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		//}else{
		//scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
		//}
		
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsForExecutive(ArrayList<String> subjects,StudentAcadsBean student,String year,String month) {
		  namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

	
		StringBuffer sql = new StringBuffer("SELECT * FROM acads.faculty f , acads.sessions s");
		sql.append(" where (s.isCommon = 'N' or s.isCommon is null ) and  s.facultyId = f.facultyId and s.month = :month and s.year = :year ");
		sql.append(" and s.subject in (:subjects) ");
		sql.append( "and (s.corporateName = 'SAS') ");
		sql.append(" order by s.date,s.startTime,s.subject ");
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("subjects", subjects);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month", month);
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			//scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]{month,year}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql.toString(), queryParams, new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return scheduledSessionList;
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsForExecutiveFromToday(ArrayList<String> subjects,StudentAcadsBean student,String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM acads.faculty f , acads.sessions s");
		sql.append(" where (s.isCommon = 'N' or s.isCommon is null ) and  s.facultyId = f.facultyId and s.month = ? and s.year = ? ");
		sql.append(" and s.subject in ("+subjectCommaSeparated+") ");
		sql.append( "and (s.corporateName = 'SAS') ");
		sql.append(" order by s.date,s.startTime,s.subject ");
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]{month,year}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return scheduledSessionList;
	}

	//Delete this method later

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsFromToday(ArrayList<String> subjects) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		String sql = "SELECT * FROM acads.faculty f , acads.sessions s, exam.examorder eo "
				+ " where s.facultyId = f.facultyId and s.month = eo.acadmonth and s.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
				+ " and s.subject in ("+subjectCommaSeparated+") "
				+ " and s.date >= CURDATE()";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsFromTodayNew(ArrayList<String> subjects,String year,String month) {
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    /*    String subjectCommaSeparated = "''";
        for (int i = 0; i < subjects.size(); i++) {
            if(i == 0){
                subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
            }else{
                subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
            }
        }
*/
        String sql = "SELECT * FROM acads.faculty f , acads.sessions s, exam.examorder eo "
                + " where s.facultyId = f.facultyId and s.month = eo.acadmonth and s.year = eo.year "
                + " and eo.order = (select o.order from exam.examorder o where acadMonth = :acadMonth and year = :year and acadSessionLive = 'Y')"
                + " and s.subject in (:subjects) "
                + " and s.date >= CURDATE()";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("acadMonth", month);
        parameters.addValue("year", year);
        parameters.addValue("subjects", subjects);

        ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)nameJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
        return scheduledSessionList;
    }
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> mgetScheduledSessionForStudentsFromToday(ArrayList<String> subjects,String centerName,String consumerProgramStructureId, String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		StringBuffer sql = new StringBuffer("SELECT * FROM acads.faculty f , acads.sessions s, exam.examorder eo "
				+ " where s.facultyId = f.facultyId and s.month = eo.acadmonth and s.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
				+ " and s.subject in ("+subjectCommaSeparated+") "
				+ " and s.date >= CURDATE() ");
		
		if ("113".equalsIgnoreCase(consumerProgramStructureId)) {
			sql.append(" and (s.corporateName = 'M.sc') ");
			
		}else {
		
		switch(centerName){
		case "Verizon":
			sql.append( "and (s.corporateName = 'Verizon' or s.corporateName = 'All') ");
			break;
			
		case "Diageo":
			sql.append( "and (s.corporateName = 'Diageo') ");
			break;
			
		case "BAJAJ":
			sql.append(" and (s.corporateName = 'BAJAJ') ");
			break;
		
		default :
			sql.append(" and ((s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' "
					 + " and s.corporateName <> 'M.sc' ) " //Added Temporary to avoid normal session in calendar
					 + " or s.corporateName is null or s.corporateName = '') ");
			break;
		}
		}
		
		if (TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
			sql.append(" and s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
					"INNER JOIN  acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
					"INNER JOIN acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
					"where tum.userId = " + sapid + " and tum.role='Student')" );
		}else{
			sql.append(" and (s.hasModuleId is null or s.hasModuleId <> 'Y') ");
		}
		sql.append(" order by s.date, s.startTime LIMIT 10");
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject order by program, sem, subject";

		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		return programSubjectMappingList;
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForFaculty(	String facultyId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM acads.faculty f , acads.sessions s, exam.examorder eo "
				+ " where s.facultyId = f.facultyId and s.month = eo.acadmonth and s.year = eo.year "
				+ " and eo.order in (select examorder.order from exam.examorder where acadSessionLive = 'Y') "
				+ " and (s.facultyId = ? or s.altFacultyId = ? or altFacultyId2 = ? or altFacultyId3 = ? )";

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{facultyId,facultyId, facultyId, facultyId}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return scheduledSessionList;
	}

	@Transactional(readOnly = false)
	public void deleteScheduledSession(String id) {
		String sql = "DELETE FROM acads.sessions WHERE id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { id});
		
		deleteSessionSubjectMapping(id, "All");
		
		Post post = findSessionPostByReferenceId(id);
		if(post!=null ) {
			 loggerForSessionScheduling.info("Calling deleteSessionPost id : "+post.getPost_id());
			 deleteSessionPost(post.getPost_id());
			 //deleteFromRedis(post);
			 loggerForSessionScheduling.info("Calling refreshRedis id : "+post.getPost_id());
			 refreshRedis( post);
		}
	}
	
	@Transactional(readOnly = true)
	public Post findSessionPostByReferenceId(String id) {  
		jdbcTemplate = new JdbcTemplate(dataSource);
		Post post = null;
		try {
			String sql = "Select * from lti.post where referenceId =? and type='Session'";
			post = (Post) jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(Post.class));
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error In findSessionPostByReferenceId : "+e.getMessage());
		}
		return post;  
	}
	
	@Transactional(readOnly = false)
	public void deleteSessionPost(int id) {  
		String sql1 = "Delete from lti.post where post_id=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql1, new Object[] {id});
	}
	
/*	public String deleteFromRedis(Post posts) {
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
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			  
			return "Error IN rest call got "+e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public void deleteCourseFacultyMapping(String id) {
		String sql = "Delete from acads.facultycoursemapping where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {id});
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList() {

		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();
		String sql =" SELECT  " + 
					"    GROUP_CONCAT(program) AS program, " + 
					"    sem, " + 
					"    prgmStructApplicable, " + 
					"    subject " + 
					" FROM " + 
					"    exam.program_subject " + 
					" GROUP BY subject , sem , prgmStructApplicable " + 
					" ORDER BY subject , prgmStructApplicable";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			subjectProgramList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, 
					new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return subjectProgramList;
	}
	
	@Transactional(readOnly = true)
	public ProgramSubjectMappingAcadsBean getSubjectProgramListForMBAWX(String subject, String moduleid) {
		
		ProgramSubjectMappingAcadsBean subjectProgram = new ProgramSubjectMappingAcadsBean();
		
		String semSql = " ( SELECT  " + 
				"    pss.sem " + 
				" FROM " + 
				"    exam.program_sem_subject pss " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.prgm_sem_subj_id = pss.id " + 
				"        INNER JOIN " + 
				"    acads.sessionplanid_timeboundid_mapping stm ON stm.timeboundId = ssc.id " + 
				"        INNER JOIN " + 
				"    acads.sessionplan_module sm ON sm.sessionPlanId = stm.sessionPlanId " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON s.moduleid = sm.id AND s.moduleid = ? ) ";
		
		String sql =" SELECT  " + 
					"    GROUP_CONCAT(program) AS program, " + 
					"    sem, " + 
					"    prgmStructApplicable, " + 
					"    subject " + 
					" FROM " + 
					"    exam.program_subject " + 
					" WHERE " + 
					"    program = 'MBA - WX' " + 
					" 	 	AND sem = "+ semSql +
					"       AND subject = ? " + 
					" GROUP BY subject , sem , prgmStructApplicable " + 
					" ORDER BY subject , prgmStructApplicable";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			subjectProgram = (ProgramSubjectMappingAcadsBean) jdbcTemplate.queryForObject(sql, new Object[]{moduleid, subject}, 
					new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return subjectProgram;
	}

	@Transactional(readOnly = false)
	public int findUsersJoined(String id) {
		String sql = "select count(sapid) from acads.session_attendance_feedback where attended = 'Y' and sessionId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{id},Integer.class);
		return count;
	}

	@Transactional(readOnly = false)
	public SessionAttendanceFeedbackAcads checkSessionAttendance(String userId, String id, String acadDateFormat) {
		String sql = "select * from acads.session_attendance_feedback where attended = 'Y' and sessionId = ? and sapId = ? and acadDateFormat = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			SessionAttendanceFeedbackAcads attendance = (SessionAttendanceFeedbackAcads) jdbcTemplate.queryForObject(sql, new Object[]{id, userId, acadDateFormat},new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));

			return attendance;
		} catch (Exception e) {
			  
			return null;
		}
	}

	@Transactional(readOnly = false)
	public SessionAttendanceFeedbackAcads getSessionWithLeastNumberOfAttendees(String sessionId) {
		String sql = "select *, count(sapid) as numberOfAttendees from acads.session_attendance_feedback where sessionId = ? group by  facultyId order by count(sapid) limit 1 ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			SessionAttendanceFeedbackAcads attendance = (SessionAttendanceFeedbackAcads) jdbcTemplate.queryForObject(sql, new Object[]{sessionId},new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
			return attendance;
		} catch (Exception e) {
			  
			return null;
		}
	}

	@Transactional(readOnly = false)
	public boolean checkIfAttendedPreviousSession(SessionDayTimeAcadsBean session,String sessionName, String userId) {
		String sql = "select count(sapid) from acads.session_attendance_feedback saf, acads.sessions s where s.subject = ? and s.sessionName = ? "
				+ " and s.id = saf.sessionId and attended = 'Y' and  saf.sapId = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getSubject(), sessionName, userId},Integer.class);
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			  
		}
		return false;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsForCurrentCycle(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public List<String> getListOfLocations() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select location from acads.location_room_capacity group by location ";

		try {
			List<String> locationList = (List<String>)jdbcTemplate.query(sql, 
					new Object[]{}, new SingleColumnRowMapper(String.class));

			return locationList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			return null;
		}
	}
	
	//to be implemneted later by Pranit on 27 dec 18
	@Transactional(readOnly = true)
	public List<String> getListOfTrackNames() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select trackName from acads.trackNames group by trackName ";
		List<String> trackNamesList = new ArrayList<>();
		try {
			trackNamesList = (List<String>)jdbcTemplate.query(sql, 
					new Object[]{}, new SingleColumnRowMapper(String.class));

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return trackNamesList;
		
	}

	/*public boolean checkIfHasParallelAlternateSession(String id,String alternateConfiguration) {
		String sql = "";
		if("ALT1".equals(alternateConfiguration)){
			sql = "select count(*) from acads.sessions s "
					+ " where s.id = ? "
					+ " and s.altMeetingKey is not null and s.altMeetingKey <> '' " ;
		}
		if("ALT2".equals(alternateConfiguration)){
			sql = "select count(*) from acads.sessions s "
					+ " where s.id = ? "
					+ " and s.altMeetingKey2 is not null and s.altMeetingKey2 <> '' " ;
		}
		if("ALT3".equals(alternateConfiguration)){
			sql = "select count(*) from acads.sessions s "
					+ " where s.id = ? "
					+ " and s.altMeetingKey3 is not null and s.altMeetingKey3 <> '' " ;
		}


		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{id},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}*/

	@Transactional(readOnly = false)
	public void addAltMeetingKey(String id, String altMeetingKey,String meetingNumberParameter) {
		String sql = "";
		if("1".equals(meetingNumberParameter)){
			sql = "Update acads.sessions set altMeetingKey = ? ";
		}
		if("2".equals(meetingNumberParameter)){
			sql = "Update acads.sessions set altMeetingKey2 = ? ";
		}
		if("3".equals(meetingNumberParameter)){
			sql = "Update acads.sessions set altMeetingKey3 = ? ";
		}
		sql = sql + " where id = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { altMeetingKey, id});

	}
	
	@Transactional(readOnly = false)
	public void addAltFacultyId(String id, String altFacultyId,String facultyParameter) {
		String sql = "";
		if("1".equals(facultyParameter)){
			sql = "Update acads.sessions set altFacultyId = ? ";
		}
		if("2".equals(facultyParameter)){
			sql = "Update acads.sessions set altFacultyId2 = ? ";
		}
		if("3".equals(facultyParameter)){
			sql = "Update acads.sessions set altFacultyId3 = ? ";
		}
		sql = sql + " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { altFacultyId, id});

	}

	@Transactional(readOnly = false)
	public void addAltMeetingPwd(String id, String altMeetingPwd,String meetingPassWordParameter) {
		String sql = "";
		if("1".equals(meetingPassWordParameter)){
			sql = "Update acads.sessions set altMeetingPwd = ? ";
		}
		if("2".equals(meetingPassWordParameter)){
			sql = "Update acads.sessions set altMeetingPwd2 = ? ";
		}
		if("3".equals(meetingPassWordParameter)){
			sql = "Update acads.sessions set altMeetingPwd3 = ? ";
		}
		sql = sql + " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { altMeetingPwd, id});

	}

	@Transactional(readOnly = true)
	public ArrayList<AcadsCalenderBean> getAllAcadsCalender(){
		String sql ="select * from acads.academic_calendar";
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<AcadsCalenderBean> calenderList = (ArrayList<AcadsCalenderBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(AcadsCalenderBean.class));
		return calenderList;
	}
	
	@Transactional(readOnly = false)
	public void upsertAcadsCalender(AcadsCalenderBean acadCalender){
		String sql = "Insert INTO acads.academic_calendar "
				+" (month,year,startDate,endDate) "
				+" VALUES (?,?,?,?) "
				+" on duplicate key update "
				+" startDate=?,endDate=? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				acadCalender.getMonth(),acadCalender.getYear(),acadCalender.getStartDate(),acadCalender.getEndDate(),acadCalender.getStartDate(),acadCalender.getEndDate()
		});
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getCommonSessionsSemesterBased(String semester,String program,String consumerProgramStructureId){

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		String sql =" SELECT  " + 
					"    asi.*, f.firstName, f.lastName " + 
					" FROM " + 
					"    acads.sessions asi, " + 
					"    exam.examorder eo, " +
					"	 acads.faculty f " +
					" WHERE " + 
					"    isCommon = 'Y' " + 
					"        AND (sem = :sem OR sem = 'All') " + 
					"		 AND asi.facultyId = f.facultyId " +
					"        AND eo.order = (SELECT  " + 
					"            MAX(examorder.order) " + 
					"        FROM " + 
					"            exam.examorder " + 
					"        WHERE " + 
					"            acadSessionLive = 'Y') " + 
					"        AND ";
		
		if (!TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
				sql +=	" ((programList like (:program)) OR (programList='All')) " +
						" AND (asi.hasModuleId is null OR asi.hasModuleId <> 'Y') " +
						" AND eo.acadMonth = asi.month AND eo.year = asi.year "; //Added to get only current cycle data
			
			}else{
				sql +=	" ((programList LIKE (:program))) " +
						" AND (asi.hasModuleId is null or asi.hasModuleId = 'Y') ";
			}
		
			sql += " ORDER BY asi.date,asi.startTime,asi.subject ";

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sem", semester);
		mapSource.addValue("program", "%"+program+"%");
		ArrayList<SessionDayTimeAcadsBean> getCommonSessionsList = (ArrayList<SessionDayTimeAcadsBean>)namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return getCommonSessionsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getCommonSessionsSemesterBasedForUG(String semester,String program,String consumerProgramStructureId){

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		String sql =" SELECT asi.*, f.firstName, f.lastName FROM " + 
					"    acads.sessions asi " +
					" 		INNER JOIN " +
					"	 exam.examorder eo ON eo.acadMonth = asi.month AND eo.year = asi.year " +
					" 		INNER JOIN " +
					"	 acads.faculty f ON asi.facultyId = f.facultyId " +
					" WHERE " + 
					"    isCommon = 'Y' AND (sem = :sem OR sem = 'All') " + 
					"        AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder WHERE acadSessionLive = 'Y') " + 
					"        AND (programList like (:program)) " +
					" 		 AND eo.acadMonth = asi.month AND eo.year = asi.year "; //Added to get only current cycle data
		
			sql += " ORDER BY asi.date,asi.startTime,asi.subject ";

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sem", semester);
		mapSource.addValue("program", "%"+program+"%");
		ArrayList<SessionDayTimeAcadsBean> getCommonSessionsList = (ArrayList<SessionDayTimeAcadsBean>)namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return getCommonSessionsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getCommonSessionsSemesterBasedFromToday(String semester,String program,String consumerProgramStructureId){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		String sql = " select * from acads.faculty f, acads.sessions asi, exam.examorder eo "
				+ " where asi.facultyId = f.facultyId and isCommon = 'Y' and (sem=:sem or sem ='All') and  "
				+ " eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') "
				+ "and asi.date >= CURDATE() and";
		 if (!TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
			sql +=" ((programList like (:program)) or (programList='All')) and (asi.hasModuleId is null or asi.hasModuleId <> 'Y') ";
			}else{
				sql +=" ((programList like (:program))) and (asi.hasModuleId is null or asi.hasModuleId <> 'Y') ";
			}
		sql+= " order by asi.date,asi.startTime,asi.subject ";

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sem", semester);
		mapSource.addValue("program", "%"+program+"%");
		ArrayList<SessionDayTimeAcadsBean> getCommonSessionsList = (ArrayList<SessionDayTimeAcadsBean>)namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		
		return getCommonSessionsList;
	}
/*Commented by Stef and moved to SessionReviewDAO
 * 	public SessionReviewBean findSessionReviewById(String reviewId) {
		String sql ="select * from acads.session_review where id = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionReviewBean reviewBean = (SessionReviewBean)jdbcTemplate.queryForObject(sql, new Object[]{reviewId},new BeanPropertyRowMapper(SessionReviewBean.class));
		return reviewBean;
	}*/
	
	@Transactional(readOnly = false)
	public void updateCancelledSession(SessionDayTimeAcadsBean session,String userId) {
		
		String sql =  " UPDATE acads.sessions SET date = ?, startTime = ?, endTime = ?, isCancelled = ?, reasonForCancellation = ?, "
					+ " cancellationSMSBody = ?, cancellationEmailBody = ?, cancellationSubject = ?, lastModifiedBy = ?, lastModifiedDate = sysdate() "
				    + " WHERE id = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] {session.getDate(),session.getStartTime(),session.getEndTime(),session.getIsCancelled(),session.getReasonForCancellation(),
							session.getCancellationSMSBody(),session.getCancellationEmailBody(),session.getCancellationSubject(),userId,session.getId() });
	}

	@Transactional(readOnly = false)
	public void createAnnouncement(SessionDayTimeAcadsBean session, String userId) {
		String sql = "INSERT INTO portal.announcements "
				+ "(subject,"
				+ "description,"
				+ "startDate,"
				+ "endDate,"
				+ "active, "
				+ "category,"
				+ "createdBy,"
				+ "createdDate)"
				+ "VALUES ( ?,?,?,?,?,'Academics',?,sysdate())";


		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				session.getCancellationSubject(),session.getCancellationEmailBody(),session.getStartDate(),session.getEndDate(),session.getIsCancelled(),userId
		});
	}
	
	@Transactional(readOnly = false)
	public long createReScheduleSession(final SessionDayTimeAcadsBean session, final String userId) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		final StringBuffer sql = new StringBuffer(" INSERT INTO acads.sessions ( date , startTime , endTime , day , subject , sessionName, createdBy , createdDate ,");
		sql.append(" year , month , facultyId , meetingKey , meetingPwd , hostId , hostPassword , room , corporateName , altFacultyId , altMeetingPwd ,");
		sql.append(" altMeetingKey , altFacultyId2 , altMeetingPwd2 , altMeetingKey2 , altFacultyId3 , altMeetingPwd3 , altMeetingKey3 , isCommon , programList ) ");
		sql.append(" VALUES ");
		sql.append(" ( ? ,? ,? ,DAYNAME( date) ,? ,? , ? , sysdate() , ? , ? , ?, ? , ? ,?, ? ,? ,? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ) ");
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				statement.setString(1,session.getDate());
				statement.setString(2,session.getStartTime());
				statement.setString(3,session.getEndTime());
				statement.setString(4,session.getSubject());
				statement.setString(5,session.getSessionName());
				statement.setString(6,userId);
				statement.setString(7,session.getYear());
				statement.setString(8,session.getMonth());
				statement.setString(9,session.getFacultyId());
				statement.setString(10,session.getMeetingKey());
				statement.setString(11,session.getMeetingPwd());
				statement.setString(12,session.getHostId());
				statement.setString(13,session.getHostPassword());
				statement.setString(14,session.getRoom());
				statement.setString(15,session.getCorporateName());
				statement.setString(16,session.getAltFacultyId());
				statement.setString(17,session.getAltMeetingPwd());
				statement.setString(18,session.getAltMeetingKey());
				statement.setString(19,session.getAltFacultyId2());
				statement.setString(20,session.getAltMeetingPwd2());
				statement.setString(21,session.getAltMeetingKey2());
				statement.setString(22,session.getAltFacultyId3());
				statement.setString(23,session.getAltMeetingPwd3());
				statement.setString(24,session.getAltMeetingKey3());
				statement.setString(25,session.getIsCommon());
				statement.setString(26,session.getProgramList());
				return statement;
			}
		},holder);
		
		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public long insertMailRecord(final MailAcadsBean successfullMailList, final String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,body,fromEmailId) VALUES(?,?,sysdate(),?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, successfullMailList.getSubject());
				statement.setString(2, userId);
				statement.setString(3,successfullMailList.getBody());
				statement.setString(4,successfullMailList.getFromEmailId());
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public void insertUserMailRecord(MailAcadsBean mailBean, String userId, String fromEmailID,
			long insertedMailId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId) VALUES(?,?,sysdate(),?,?,?) ";
		try{
			int batchUpdateDocumentRecordsResultSize = jdbcTemplate.update(sql, new Object[]{
					StringUtils.join(mailBean.getSapIdRecipients(),","),
					StringUtils.join(mailBean.getMailIdRecipients(),","),
					userId,
					fromEmailID,
					String.valueOf(insertedMailId)
			});
			
		}catch(Exception e){
			  
		}
	}

	//For getting Registered Exam Dates Start
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionAcadsBean> getBookedExams(String sapid){ 

	jdbcTemplate = new JdbcTemplate(dataSource);
	String sql="select * from exam.exambookings where booked='Y'  and sapid=? and year = ? and month = ? ";
//	List<ExamBookingTransactionBean> bookedExams = jdbcTemplate.query(sql, new Object[] {sapid, getLiveExamYear(), getLiveExamMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	List<ExamBookingTransactionAcadsBean> bookedExams = jdbcTemplate.query(sql, new PreparedStatementSetter() {
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setString(1, sapid);
			ps.setString(2, getLiveExamYear());
			ps.setString(3, getLiveExamMonth());
		}}, new BeanPropertyRowMapper(ExamBookingTransactionAcadsBean .class)); 
	return bookedExams;

	}
	//For getting Registered Exam Dates End
	
	//For getting Key Events Dates Start
	@Transactional(readOnly = true)
	public List<EventBean> getEventsList(){ 
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select * from acads.events Order By id desc ";
		List<EventBean> eventsList = jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(EventBean.class));
		return eventsList;
	}
	//For getting Key Events Dates End	
	
	//Check if faculty is taking another session at the sametime Start
	@Transactional(readOnly = true)
	public boolean isFacutlyTakingAnotherSessionAtSametime(String pk,String value) {
		SessionDayTimeAcadsBean session=new SessionDayTimeAcadsBean();
		try {
			 session= findScheduledSessionById(pk);
		} catch (Exception e) {
			  
			return true;
		}	
		
		String sql="SELECT COUNT(*) "+ 
						" FROM acads.sessions "+ 
						"	where date=? and startTime=? "+ 
						"    and (facultyId=? " + 
						"    or altFacultyId=? " + 
						"    or altFacultyId2=? " + 
						"    or altFacultyId3=?);";
			jdbcTemplate = new JdbcTemplate(dataSource);

			int count = 0;
			try {
				count = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getDate(),session.getStartTime(),value,value,value,value},Integer.class);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				  
				return true;
			}

			if(count == 0){
				return false;
			}else{
				return true;
			}		
	}
	//Check if faculty is taking another session at the sametime End 

	@Transactional(readOnly = true)
	public ArrayList<String> getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(String month,String year){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select distinct subject from exam.program_subject "
				+ " where active = 'Y' "
				+ " and concat(program,prgmStructApplicable,sem) in "
				+ " ( "
				+ " select concat(r.program,st.PrgmStructApplicable,r.sem) from exam.registration r ,exam.students st "
				+ " where "
				+ " r.sapid = st.sapid "
				+ " and st.existingStudentNoForDiscount <> '' and existingStudentNoForDiscount is not null "
				+ " and (r.program = 'EPBM' || r.program = 'MPDV') "
				+ " and r.month = ? "
				+ " and r.year = ? "
				+ " ) ";
		ArrayList<String> studentSubjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {month,year}, new SingleColumnRowMapper(String.class));
		return studentSubjectList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getStudentsSubjectListOfHavingSaSAndOtherProgramActive(String month,String year){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select distinct subject from exam.program_subject"
				+ " where active = 'Y'"
				+ " and concat(program,prgmStructApplicable,sem) in"
				+ " ("
				+ " select concat(r.program,st.PrgmStructApplicable,r.sem) from exam.registration r ,exam.students st"
				+ " where"
				+ " r.sapid = st.sapid"
				+ " and st.sapid in (select existingStudentNoForDiscount from exam.students where"
				+ " existingStudentNoForDiscount <> '' and existingStudentNoForDiscount is not null"
				+ " and (program = 'EPBM' || program = 'MPDV')"
				+ " )"
				+ " and r.month = ?"
				+ " and r.year = ?"
				+ " )";
		ArrayList<String> studentSubjectList =(ArrayList<String>) jdbcTemplate.query(sql, new Object[] {month,year}, new SingleColumnRowMapper(String.class));
		return studentSubjectList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSessionScheduledOnSameDayTime(String date,String startTime,List<String> studentSubjectList, SessionDayTimeAcadsBean bean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = "";
		int sessionTime = getSessionTime(bean);
		String dateTime = bean.getDate()+ " " +bean.getStartTime();
		
		ArrayList<String> lstOfClashingSubjects = new ArrayList<String>();
		for (int i = 0; i < studentSubjectList.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +studentSubjectList.get(i).replaceAll("'", "''").replaceAll(",","\\,") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + studentSubjectList.get(i).replaceAll("'", "''").replaceAll(",","\\,") + "'";
			}
		}
		String sql= " select subject from acads.sessions"
				+ " where date = ? "
				+ " and ((startTime between ? and ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE)) "
				+ " or (endTime between ? and ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE)))"
				+ " and subject in ("+subjectCommaSeparated+") ";
		lstOfClashingSubjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{date,startTime,startTime},new SingleColumnRowMapper(String.class));
		
		return lstOfClashingSubjects;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllSASSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct subject FROM exam.program_subject where (program = 'MPDV' or program = 'EPBM') order by subject asc";
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getEventsRegisteredByStudent(
			String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.sessions where id in (select sessionId from portal.online_event oe, "
				+ " portal.event_registration er where sapid = ? and response = 'Yes' and oe.id = er.Online_EventId);";
		

//		ArrayList<SessionDayTimeBean> eventSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
		ArrayList<SessionDayTimeAcadsBean> eventSessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,  new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, sapId);
			}}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		

		

		return eventSessionList;
	}
	
	
	
	//added because of event details :START
	@Transactional(readOnly = false)
		public void insertEventDetails(SessionDayTimeAcadsBean bean) {			
			String sql="INSERT INTO  portal.online_event ("
					+ " eventName , startDate , endDate , program , sem , PrgmStructApplicable, createdDate"
					+ " , createdBy , lastModifiedDate , lastModifiedBy ,sessionId) "
					+ " VALUES (?,?,?,'All',?,'All',sysdate(),?,sysdate(),?,?)";
			String eventName=bean.getSessionName();
			String startDate= bean.getStartDate();
			String endDate= bean.getEndDate();
			String sem=bean.getSem();
			String createdBy=bean.getCreatedBy();
			String lastModifiedBy=bean.getLastModifiedBy();
			String sessionId= bean.getId();
			
			jdbcTemplate.update(sql , new Object[] {
					eventName,
					startDate,
					endDate,
					sem,
					createdBy,
					lastModifiedBy,
					sessionId
			});
			
		}
		
		@Transactional(readOnly = false)
		public String getSessionId(SessionDayTimeAcadsBean bean) {
			
			String sessionId = null;
			String sqlForTrack = "";
			String sqlForSessionPlan = "";
			if (!StringUtils.isBlank(bean.getTrack())) {
				sqlForTrack = " AND s.track = '"+bean.getTrack()+"' ";
			}
			if (!StringUtils.isBlank(bean.getModuleId())) {
				sqlForSessionPlan = " AND moduleid = "+bean.getModuleId();
			}
			final String sql =  " SELECT  " + 
								"    s.id " + 
								" FROM " + 
								"    acads.sessions s " + 
								" WHERE " + 
								"    s.date = ? AND startTime = ? " + 
								"		 AND s.sessionName = ? " + 
								"        AND s.subject = ? " + 
								"        AND corporateName = ? "
								+ sqlForTrack
								+ sqlForSessionPlan;

			jdbcTemplate = new JdbcTemplate(dataSource);

			sessionId = (String) jdbcTemplate.queryForObject(sql,new Object[]{bean.getDate(),bean.getStartTime(), bean.getSessionName(),
															 bean.getSubject(), bean.getCorporateName()},String.class);
			return sessionId;
		}
		
		//added because of event details :END
		
		/*public SessionDayTimeBean getAllRegistrationsFromSAPID(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select year,month,sem from exam.registration where sapid = ? and sem in (select max(sem) from exam.registration where sapid = ? ) order by sem  ";
			SessionDayTimeBean getAllRegistrationsFromSAPID = (SessionDayTimeBean)jdbcTemplate.queryForObject(sql,new Object[]{sapid,sapid},new BeanPropertyRowMapper(SessionDayTimeBean.class));
			return getAllRegistrationsFromSAPID;
		}*/
		
		@Transactional(readOnly = true)
		public PageAcads<SessionDayTimeAcadsBean> getScheduledSessionPageNew(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql = "SELECT s.*, f.*, s.id FROM acads.sessions s INNER JOIN acads.faculty f ON s.facultyId = f.facultyId ";
			String countSql = "SELECT COUNT(*) FROM acads.sessions s INNER JOIN acads.faculty f ON s.facultyId = f.facultyId ";

			//Commented by Somesh as Now no dependency on current acad year/month
			//String sql = "SELECT * FROM acads.faculty f , acads.sessions s , exam.examorder eo where s.facultyId = f.facultyId and s.year = eo.year and s.month = eo.acadMonth and eo.order in (select o.order from exam.examorder o where acadSessionLive = 'Y') ";
			//String countSql = "SELECT count(*) FROM acads.faculty f , acads.sessions s , exam.examorder eo where s.facultyId = f.facultyId and s.year = eo.year and s.month = eo.acadMonth and  eo.order in (select o.order from exam.examorder o where acadSessionLive = 'Y')";

			/*if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
				sql = sql + " and s.facultyId like  ? ";
				countSql = countSql + " and s.facultyId like  ? ";
				parameters.add("%"+searchBean.getFacultyId()+"%");
			}
			if( searchBean.getSessionName() != null &&   !("".equals(searchBean.getSessionName()))){
				sql = sql + " and sessionName like  ? ";
				countSql = countSql + " and sessionName like  ? ";
				parameters.add("%"+searchBean.getSessionName()+"%");
			}

			if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
				sql = sql + " and subject = ? ";
				countSql = countSql + " and subject = ? ";
				parameters.add(searchBean.getSubject());
			}
			if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
				sql = sql + " and year = ? ";
				countSql = countSql + " and year = ? ";
				parameters.add(searchBean.getYear());
			}
			if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
				sql = sql + " and month = ? ";
				countSql = countSql + " and month = ? ";
				parameters.add(searchBean.getMonth());
			}
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
			}*/

			if( searchBean.getCorporateName() != null &&   !("".equals(searchBean.getCorporateName()))){
				sql = sql + " and (s.corporateName = 'All' or s.corporateName = ? ) ";
				countSql = countSql + " and (s.corporateName = 'All' or s.corporateName = ? ) ";
				parameters.add(searchBean.getCorporateName());
			}

			sql = sql + " order by date, startTime asc";

			Object[] args = parameters.toArray();

			PaginationHelper<SessionDayTimeAcadsBean> pagingHelper = new PaginationHelper<SessionDayTimeAcadsBean>();
			PageAcads<SessionDayTimeAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

			return page;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getAllLocations() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT location FROM acads.location_room_capacity order by location asc";
			ArrayList<String> locationList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return locationList;
		}
		
		@Transactional(readOnly = false)
		public SessionDayTimeAcadsBean getSessionById(String id){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select * from acads.sessions where id = ?";
			SessionDayTimeAcadsBean sessionDetails = (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return sessionDetails;
		}
		
		@Transactional(readOnly = false)
		public void updateSessionLocation(String sessionId, String altLocation, String facultyParameter) {
			
			String sql = "";
			switch (facultyParameter) {
			
				case "1":
					sql = "Update acads.sessions SET altFacultyLocation= ? WHERE id = ? ";
				break;
				
				case "2":
					sql = "Update acads.sessions SET altFaculty2Location= ? WHERE id = ? ";
				break;
				
				case "3":
					sql = "Update acads.sessions SET altFaculty3Location= ? WHERE id = ? ";
				break;
			}
				jdbcTemplate = new JdbcTemplate(dataSource);
				jdbcTemplate.update(sql, new Object[] {altLocation,sessionId} );
		}

		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getSessionAfterDate(String corporateName){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql =  " SELECT * FROM acads.sessions "
						+ " where year = 2018 and month = 'Jul' and corporateName = ? "
						+ " and date > curdate() ";
			
		
			ArrayList<SessionDayTimeAcadsBean> sessions = new ArrayList<>();
			try {
				sessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{corporateName}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return sessions;
		}
		
		@Transactional(readOnly = true)
		public HashMap<String, String> getAttendanceForSessionMap(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			HashMap<String, String> attendanceForSessionMap = new HashMap<>();
			List<SessionAttendanceFeedbackAcads> sessionList = null;
			String key = "";
			
			String sql = "select * from acads.session_attendance_feedback where sapid = ? ";
			try {
				sessionList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
				
				for (SessionAttendanceFeedbackAcads attendList : sessionList) {
					key = sapid + " - "+attendList.getSessionId();
					attendanceForSessionMap.put(key, attendList.getSessionId());
				}
				
			} catch (Exception e) {
				  
			}
			
			return attendanceForSessionMap;
		}
		
		@Transactional(readOnly = false)
		public SessionDayTimeAcadsBean scheduledSessionById(String date,String startTime,String sem){
			jdbcTemplate = new JdbcTemplate(dataSource);
			SessionDayTimeAcadsBean bean =new SessionDayTimeAcadsBean();
			
			String sql = "select * from acads.sessions where date = ? and startTime = ? and sem = ? ";
			try {
				bean = (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(sql, new Object[]{date,startTime,sem}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return bean;
		}
		
		/**
		 * Get list of webinar filter by date
		 * */
//		public List<SessionDayTimeBean> getSessionsForVimeoStatus(String date){
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			List<SessionDayTimeBean> sessionList = null;
//			//String sql = "SELECT DISTINCT hostId FROM acads.sessions where `date` = ? and (uploadStatus IS NULL or  uploadStatus = '');";
//			//String sql = "SELECT * FROM acads.sessions as s,acads.video_content as v_c where s.id = v_c.sessionId and s.`date` = ? and (v_c.uploadStatus IS NULL or  v_c.uploadStatus = '');";
//			String sql = "SELECT * FROM acads.sessions as s where  s.`date` = ? and isCancelled <> 'Y';";
//			try {
//				sessionList = (List<SessionDayTimeBean>) jdbcTemplate.query(sql, new Object[]{date}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
//				
//			}catch (Exception e) {
//				  
//			}
//			return sessionList;
//		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getSessionRecordingList(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `acads`.`recording_status` as r_s,`acads`.`sessions` as s where s.id = r_s.sessionId;";
			List<SessionDayTimeAcadsBean> recordingList = null;
			try {
				recordingList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}
			catch (Exception e) {
				// TODO: handle exception
				  
			}
			return recordingList;
			
		}
		
		// recording not uploaded in 3 hour from session end.
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getOverTimeRecordingList(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `acads`.`recording_status` as r_s,`acads`.`sessions` as s where s.id = r_s.sessionId and (TIME_TO_SEC( TIMEDIFF( SYSDATE(), CAST(CONCAT(created_at) AS DATETIME) ) ) > 14400 AND TIME_TO_SEC( TIMEDIFF( SYSDATE(), CAST(CONCAT(created_at) AS DATETIME) ) ) < 43200 ) AND status <> 'success' AND isErrorNotificationSend = 0;";
			List<SessionDayTimeAcadsBean> recordingList = null;
			try {
				recordingList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}
			catch (Exception e) {
				// TODO: handle exception
				  
			}
			return recordingList;
			
		}
		
		@Transactional(readOnly = false)
		public void updateIsNotificationSendFlag(SessionDayTimeAcadsBean sessionDayTimeBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.recording_status set status='pending',vimeoId='0', isErrorNotificationSend = 1 where meetingId = ? ";
			try {
				jdbcTemplate.update(sql, new Object[] {
					sessionDayTimeBean.getMeetingId()
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		@Transactional(readOnly = true)
		public List<VideoContentAcadsBean> getVideoContentTop50(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT v_s.*,s.meetingKey as meetingKey FROM acads.video_content v_s left join acads.sessions s on s.id = v_s.sessionId where processFlag IS NULL and sessionDate > '2019-06-01' limit 200";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		}
		
		@Transactional(readOnly = false)
		public void deleteFailedRecording() {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "delete from acads.deleted_meetingids where message = 'SSL peer shut down incorrectly' or message = 'Remote host closed connection during handshake' or message like 'Error: I/O error on GET request for%' or message='Failed to delete';";
				jdbcTemplate.update(sql);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getLastToLastMonthVideo(){			
			jdbcTemplate = new JdbcTemplate(dataSource);	
			String sql = "SELECT s.* FROM (SELECT id, date, meetingKey AS meetingKey FROM acads.sessions s WHERE s.meetingKey IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < (CURDATE() - 2) UNION ALL SELECT id,date, altMeetingKey AS meetingKey FROM acads.sessions s WHERE s.altMeetingKey IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < (CURDATE() - 2) UNION ALL SELECT id,date, altMeetingKey2 AS meetingKey FROM acads.sessions s WHERE s.altMeetingKey2 IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < (CURDATE() - 2) UNION ALL SELECT id,date, altMeetingKey3 AS meetingKey FROM acads.sessions s WHERE s.altMeetingKey3 IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < (CURDATE() - 2)) s INNER JOIN acads.video_content vc ON s.id = vc.sessionId WHERE CONCAT(s.id, s.meetingKey) NOT IN (SELECT CONCAT(sessionId, meetingId) FROM acads.deleted_meetingids) LIMIT 20;";
		    return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getLastMonthVideoUploadedRecording(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT s.id, s.date, s.meetingKey AS meetingKey FROM acads.sessions s INNER JOIN acads.video_content vc ON s.id = vc.sessionId WHERE s.meetingKey IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND DATE(s.date) >= DATE_SUB(CURDATE(), INTERVAL 365 DAY);";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getLastMonthVideoUploadedAltRecording(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT s.id,s.date, s.altMeetingKey AS meetingKey FROM acads.sessions s INNER JOIN acads.video_content vc ON s.id = vc.sessionId WHERE s.altMeetingKey IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND DATE(s.date) >= DATE_SUB(CURDATE(), INTERVAL 365 DAY);";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getLastMonthVideoUploadedAlt2Recording(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT s.id,s.date, s.altMeetingKey2 AS meetingKey FROM acads.sessions s INNER JOIN acads.video_content vc ON s.id = vc.sessionId WHERE s.altMeetingKey2 IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND DATE(s.date) >= DATE_SUB(CURDATE(), INTERVAL 365 DAY);";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getLastMonthVideoUploadedAlt3Recording(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT s.id,s.date, s.altMeetingKey3 AS meetingKey FROM acads.sessions s INNER JOIN acads.video_content vc ON s.id = vc.sessionId WHERE s.altMeetingKey3 IS NOT NULL AND (joinUrl IS NOT NULL AND joinUrl <> '') AND DATE(s.date) < DATE_SUB(CURDATE(), INTERVAL 2 DAY) AND DATE(s.date) >= DATE_SUB(CURDATE(), INTERVAL 365 DAY);";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = true)
		public List<String> getDeletedMeetingList(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select concat(dm.sessionId,dm.meetingId) as sessionIdMeetingKey from acads.deleted_meetingids dm;";
			return jdbcTemplate.query(sql,new SingleColumnRowMapper<String>(String.class));
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getVideoContentFromTodayTop100(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.sessions where date > CURDATE() and (processFlag <> 'Y' or processFlag IS NULL) order by date asc limit 100;";
			//String sql = "SELECT * FROM acads.sessions where date = '2020-06-05' and (processFlag <> 'Y' or processFlag IS NULL) order by date asc limit 100;";
			return jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		
		@Transactional(readOnly = false)
		public void updateSessionPasswordAndProccessFlag(SessionDayTimeAcadsBean sessionDayTimeBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.sessions set processFlag = 'Y',meetingPwd=? where id = ?";
			jdbcTemplate.update(sql,new Object[] {sessionDayTimeBean.getMeetingPwd(),sessionDayTimeBean.getId()});
		}
		
		@Transactional(readOnly = false)
		public void updateSessionProccessFlagFalse(SessionDayTimeAcadsBean sessionDayTimeBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.sessions set processFlag = 'N' where id = ?";
			jdbcTemplate.update(sql,new Object[] {sessionDayTimeBean.getId()});
		}
		
		@Transactional(readOnly = false)
		public void markAsProcess(Long id,String audioFile) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.video_content set processFlag = 'Y',audioFile=? where id = ?";
			jdbcTemplate.update(sql,new Object[] {audioFile,id});
		}
		
		@Transactional(readOnly = false)
		public void markAsDeleted(String sessionId,String webinarId) {
		     jdbcTemplate = new JdbcTemplate(dataSource);
		     String sql = "insert into acads.deleted_meetingids(`sessionId`,`meetingId`,`message`) values(?,?,'success')";	
		     jdbcTemplate.update(sql,new Object[] {sessionId,webinarId});
		}
		
		@Transactional(readOnly = false)
		public void markAsProcessFailed(Long id,String message) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.video_content set processFlag = 'Y',errorMessage=? where id = ?";
			jdbcTemplate.update(sql,new Object[] {message,id});
		}
		
		@Transactional(readOnly = false)
		public void markAsDelete(String sessionId,String id,String status,String sessionDate) {
			 try {
				 jdbcTemplate = new JdbcTemplate(dataSource);
				 String sql = "insert into acads.deleted_meetingids(`sessionId`,`meetingId`,`message`,`sessionDate`) values(?,?,?,?)";
				 jdbcTemplate.update(sql,new Object[] {sessionId,id,status,sessionDate});
			}catch (Exception e) {
				  
			}
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getSessionsByDate(String status){ 
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<SessionDayTimeAcadsBean> sessionList = null;
			// only for mba Wx
			String sql = "SELECT s.`id`,s.`date`,s.`startTime`,s.`day`,s.`subject`,s.`sessionName`,s.`createdBy`,s.`createdDate`,s.`lastModifiedBy`,s.`lastModifiedDate`,s.`year`,s.`month`,s.`facultyId`,s.`endTime`,s.`ciscoStatus`,s.`tmsConfId`,s.`tmsConfLink`,s.`meetingKey` as meetingKey,s.`meetingPwd`,s.`joinUrl`,s.`hostUrl`,s.`hostKey`,s.`localTollNumber`,s.`localTollFree`,s.`globalCallNumber`,s.`pstnDialNumber`,s.`participantCode`,s.`room`,s.`smsSent`,s.`emailSent`,s.`hostId`,s.`hostPassword`,s.`altMeetingKey`,s.`altMeetingPwd`,s.`altFacultyId`,s.`sem`,s.`isCommon`,s.`corporateName`,s.`altMeetingKey2`,s.`altMeetingPwd2`,s.`altFacultyId2`,s.`altMeetingKey3`,s.`altMeetingPwd3`,s.`altFacultyId3`,s.`programList`,s.`altHostId`,s.`altHostPassword`,s.`altHostId2`,s.`altHostPassword2`,s.`altHostId3`,s.`altHostPassword3`,s.`isCancelled`,s.`isVerified`,s.`reasonForCancellation`,s.`cancellationSMSBody`,s.`cancellationEmailBody`,s.`cancellationSubject`,s.`cancellationSmsSent`,s.`cancellationEmailSent`,s.`eventId`,s.`facultyLocation`,s.`altFacultyLocation`,s.`altFaculty2Location`,s.`altFaculty3Location`,s.`track`,s.`hasModuleId`,s.`moduleid`,s.`processFlag` FROM acads.sessions as s where (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) "
					+ "and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 "
					+ "and  (s.isCancelled <> 'Y' or s.isCancelled IS NULL) and s.meetingKey NOT IN (select meetingId from acads.recording_status where vimeoId IS NOT NULL) ";
			if("PG".equalsIgnoreCase(status)) {
				sql = sql + " and s.hasModuleId = 'N'";
			}else {
				sql = sql + " and s.hasModuleId = 'Y'";
			}
			
			String sql1 = "SELECT s.`id`,s.`date`,s.`startTime`,s.`day`,s.`subject`,s.`sessionName`,s.`createdBy`,s.`createdDate`,s.`lastModifiedBy`,s.`lastModifiedDate`,s.`year`,s.`month`,s.`facultyId`,s.`endTime`,s.`ciscoStatus`,s.`tmsConfId`,s.`tmsConfLink`,s.`altMeetingKey` as meetingKey,s.`meetingPwd`,s.`joinUrl`,s.`hostUrl`,s.`hostKey`,s.`localTollNumber`,s.`localTollFree`,s.`globalCallNumber`,s.`pstnDialNumber`,s.`participantCode`,s.`room`,s.`smsSent`,s.`emailSent`,s.`hostId`,s.`hostPassword`,s.`altMeetingKey`,s.`altMeetingPwd`,s.`altFacultyId`,s.`sem`,s.`isCommon`,s.`corporateName`,s.`altMeetingKey2`,s.`altMeetingPwd2`,s.`altFacultyId2`,s.`altMeetingKey3`,s.`altMeetingPwd3`,s.`altFacultyId3`,s.`programList`,s.`altHostId`,s.`altHostPassword`,s.`altHostId2`,s.`altHostPassword2`,s.`altHostId3`,s.`altHostPassword3`,s.`isCancelled`,s.`isVerified`,s.`reasonForCancellation`,s.`cancellationSMSBody`,s.`cancellationEmailBody`,s.`cancellationSubject`,s.`cancellationSmsSent`,s.`cancellationEmailSent`,s.`eventId`,s.`facultyLocation`,s.`altFacultyLocation`,s.`altFaculty2Location`,s.`altFaculty3Location`,s.`track`,s.`hasModuleId`,s.`moduleid`,s.`processFlag` FROM acads.sessions as s where (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) "
					+ "and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 "
					+ "and s.altMeetingKey IS NOT NULL and  (s.isCancelled <> 'Y' or s.isCancelled IS NULL) and s.altMeetingKey NOT IN (select meetingId from acads.recording_status where vimeoId IS NOT NULL) ";
			if("PG".equalsIgnoreCase(status)) {
				sql1 = sql1 + " and s.hasModuleId = 'N'";
			}else {
				sql1 = sql1 + " and s.hasModuleId = 'Y'";
			}
			
			String sql2 = "SELECT s.`id`,s.`date`,s.`startTime`,s.`day`,s.`subject`,s.`sessionName`,s.`createdBy`,s.`createdDate`,s.`lastModifiedBy`,s.`lastModifiedDate`,s.`year`,s.`month`,s.`facultyId`,s.`endTime`,s.`ciscoStatus`,s.`tmsConfId`,s.`tmsConfLink`,s.`altMeetingKey2` as meetingKey,s.`meetingPwd`,s.`joinUrl`,s.`hostUrl`,s.`hostKey`,s.`localTollNumber`,s.`localTollFree`,s.`globalCallNumber`,s.`pstnDialNumber`,s.`participantCode`,s.`room`,s.`smsSent`,s.`emailSent`,s.`hostId`,s.`hostPassword`,s.`altMeetingKey`,s.`altMeetingPwd`,s.`altFacultyId`,s.`sem`,s.`isCommon`,s.`corporateName`,s.`altMeetingKey2`,s.`altMeetingPwd2`,s.`altFacultyId2`,s.`altMeetingKey3`,s.`altMeetingPwd3`,s.`altFacultyId3`,s.`programList`,s.`altHostId`,s.`altHostPassword`,s.`altHostId2`,s.`altHostPassword2`,s.`altHostId3`,s.`altHostPassword3`,s.`isCancelled`,s.`isVerified`,s.`reasonForCancellation`,s.`cancellationSMSBody`,s.`cancellationEmailBody`,s.`cancellationSubject`,s.`cancellationSmsSent`,s.`cancellationEmailSent`,s.`eventId`,s.`facultyLocation`,s.`altFacultyLocation`,s.`altFaculty2Location`,s.`altFaculty3Location`,s.`track`,s.`hasModuleId`,s.`moduleid`,s.`processFlag` FROM acads.sessions as s where (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) "
					+ "and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 "
					+ "and s.altMeetingKey2 IS NOT NULL and  (s.isCancelled <> 'Y' or s.isCancelled IS NULL) and s.altMeetingKey2 NOT IN (select meetingId from acads.recording_status where vimeoId IS NOT NULL) ";
			if("PG".equalsIgnoreCase(status)) {
				sql2 = sql2 + " and s.hasModuleId = 'N'";
			}else {
				sql2 = sql2 + " and s.hasModuleId = 'Y'";
			}
			
			String sql3 = "SELECT s.`id`,s.`date`,s.`startTime`,s.`day`,s.`subject`,s.`sessionName`,s.`createdBy`,s.`createdDate`,s.`lastModifiedBy`,s.`lastModifiedDate`,s.`year`,s.`month`,s.`facultyId`,s.`endTime`,s.`ciscoStatus`,s.`tmsConfId`,s.`tmsConfLink`,s.`altMeetingKey3` as meetingKey,s.`meetingPwd`,s.`joinUrl`,s.`hostUrl`,s.`hostKey`,s.`localTollNumber`,s.`localTollFree`,s.`globalCallNumber`,s.`pstnDialNumber`,s.`participantCode`,s.`room`,s.`smsSent`,s.`emailSent`,s.`hostId`,s.`hostPassword`,s.`altMeetingKey`,s.`altMeetingPwd`,s.`altFacultyId`,s.`sem`,s.`isCommon`,s.`corporateName`,s.`altMeetingKey2`,s.`altMeetingPwd2`,s.`altFacultyId2`,s.`altMeetingKey3`,s.`altMeetingPwd3`,s.`altFacultyId3`,s.`programList`,s.`altHostId`,s.`altHostPassword`,s.`altHostId2`,s.`altHostPassword2`,s.`altHostId3`,s.`altHostPassword3`,s.`isCancelled`,s.`isVerified`,s.`reasonForCancellation`,s.`cancellationSMSBody`,s.`cancellationEmailBody`,s.`cancellationSubject`,s.`cancellationSmsSent`,s.`cancellationEmailSent`,s.`eventId`,s.`facultyLocation`,s.`altFacultyLocation`,s.`altFaculty2Location`,s.`altFaculty3Location`,s.`track`,s.`hasModuleId`,s.`moduleid`,s.`processFlag` FROM acads.sessions as s where (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) "
					+ "and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 "
					+ "and s.altMeetingKey3 IS NOT NULL and  (s.isCancelled <> 'Y' or s.isCancelled IS NULL) and s.altMeetingKey2 NOT IN (select meetingId from acads.recording_status where vimeoId IS NOT NULL) ";
			if("PG".equalsIgnoreCase(status)) {
				sql3 = sql3 + " and s.hasModuleId = 'N'";
			}else {
				sql3 = sql3 + " and s.hasModuleId = 'Y'";
			}
			sql = sql + " UNION ALL " + sql1 + " UNION ALL " + sql2 + " UNION ALL " + sql3;
			//sql = sql + " and s.meetingKey ='93786243781'"; 	//for testing 
			try {
				sessionList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
				
			}catch (Exception e) {
				  
			}
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public List<RecordingStatus> getPendingRecordingData(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<RecordingStatus> recordingStatus = null;
			String sql = "SELECT * FROM acads.recording_status where (status = 'initiated' or status = 'pending' ) and vimeoId IS NOT NULL and created_at >= DATE_ADD(CURDATE(), INTERVAL -5 DAY);";
			try {
				recordingStatus = (List<RecordingStatus>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(RecordingStatus.class));
			}catch (Exception e) {
				  
			}
			return recordingStatus;
		}
		
		@Transactional(readOnly = false)
		public SessionBean getSessionDataById(int id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.sessions where id = ?";
			try {
				return (SessionBean) jdbcTemplate.queryForObject(sql,new Object[] {
						id
				},new BeanPropertyRowMapper(SessionBean.class));
			}
			catch (Exception e) {
				// TODO: handle exception
				  
				return null;
			}
		}
		
		@Transactional(readOnly = false)
		public void pendingRecordingStatus(String meetingId,String vimeoId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.recording_status set status = 'pending',error='',vimeoId = ? where meetingId = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
					vimeoId,
					meetingId
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		@Transactional(readOnly = false)
		public void updateVimeoStatus(String meetingId,String status) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.recording_status set vimeoStatus = ? where meetingId = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
					status,
					meetingId
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		public void errorRecordingStatus(String meetingId,String message) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.recording_status set status = 'pending',error = ? where meetingId = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
					message,
					meetingId
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		@Transactional(readOnly = false)
		public void errorDeleteRecordingStatus(String meetingId,String message) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.deleted_meetingids set message = ? where meetingId = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
					message,
					meetingId
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		@Transactional(readOnly = false)
		public void successRecordingStatus(String meetingId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update acads.recording_status set status = 'success',error='',vimeoStatus = 'successfully video uploaded' where meetingId = ?";
			try {
				jdbcTemplate.update(sql, new Object[] {
					meetingId
				});
			}
			catch (Exception e) {
				  
			}
		}
		
		/**
		 * Updating session status used by zoom and vimeo integration module
		 * */
		@Transactional(readOnly = false)
		public void updateStatus(String statusId,String status,String message,String date,String meetingKey,String vimeoId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			//updating recordingStatus table record based on session table.
			String sql = "update acads.recordingstatus as r join acads.sessions as s on r.sessionId = s.id set ";
			String vimeoIdField = "";
			String meetingIdField = "";
			switch (statusId) {
			case "primary":
				vimeoIdField = "r.vimeoId";
				meetingIdField = "s.meetingKey";
				sql = sql + " r.uploadStatus = '" + status + "', r.uploadErrorMessage = '" + message + "'";
				break;
			case "alt":
				vimeoIdField = "r.altvimeoId";
				meetingIdField = "s.altMeetingKey";
				sql = sql + " r.altuploadStatus = '" + status + "', r.altuploadErrorMessage = '" + message + "'";
				break;
			case "alt2":
				vimeoIdField = "r.altvimeoId2";
				meetingIdField = "s.altMeetingKey2";
				sql = sql + " r.altuploadStatus2 = '" + status + "', r.altuploadErrorMessage2 = '" + message + "'";
				break;
			case "alt3":
				vimeoIdField = "r.altvimeoId3";
				meetingIdField = "s.altMeetingKey3";
				sql = sql + " r.altuploadStatus3 = '" + status + "', r.altuploadErrorMessage3 = '" + message + "'";
				break;	
			}
			
			if(vimeoId != null) {
				sql = sql + " , " + vimeoIdField + " = " + vimeoId;
			}
			sql = sql + " where s.`date` = ? ";
			if(meetingKey != null) {
				sql = sql + " and "+ meetingIdField +" = '" + meetingKey +"'";
			}
			
			try {
				jdbcTemplate.update(sql, new Object[] {date});
			}
			catch (Exception e) {
				  
			}
			//return null;
		}
		
		@Transactional(readOnly = true)
		public String getVimeoIdFromMeetingId(String meetingId) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT * FROM acads.recording_status where meetingId = ? limit 1;";
				RecordingStatus recordingStatus = jdbcTemplate.queryForObject(sql, new Object[] {meetingId},new BeanPropertyRowMapper<RecordingStatus>(RecordingStatus.class));
				if(recordingStatus == null) {
					return "false";
				}
				if(recordingStatus.getVimeoId() == null || "0".equalsIgnoreCase(recordingStatus.getVimeoId()) || "".equalsIgnoreCase(recordingStatus.getVimeoId())) {
					return "pending";
				}
				return recordingStatus.getVimeoId();
				
			}
			catch (Exception e) {
				// TODO: handle exception
				  
				return null;
			}
		}
		
		@Transactional(readOnly = false)
		public boolean createRecordingStatusEntry(String meetingId,String sessionId) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "insert into acads.recording_status(`meetingId`,`sessionId`,`status`) values(?,?,'initiated') on duplicate key update status = 'initiated'";
				jdbcTemplate.update(sql,new Object[] {
					meetingId,
					sessionId
				});
				return true;
			}
			catch (Exception e) {
				// TODO: handle exception
				  
				return false;
			}
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getAllSessionsMappedWithVideoContent() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT  " + 
					"    s.* " + 
					"FROM " + 
					"    acads.sessions s, " + 
					"    acads.video_content vc " + 
					"WHERE " + 
					"    s.id = vc.sessionId " + 
					"GROUP BY s.id";

			List<SessionDayTimeAcadsBean> allSessionsMappedWithVideoContent = new ArrayList<>();
			 try {
				allSessionsMappedWithVideoContent = (List<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				  
			}
			
			
			return allSessionsMappedWithVideoContent;
		}
		

		//insert session moduleId mapping
		@Transactional(readOnly = false)
		public Boolean insertSessionModuleMapping(SessionDayTimeAcadsBean bean) {
			String sql = "INSERT INTO acads.session_moduleid ( "
					+ " sessionId,moduleId,createdBy, createdDate, lastModifiedBy, lastModifiedDate"
					+ ") "
					+ " VALUES( ?,?,"
					+ "?,sysdate(),?, sysdate()"
					+ ") ";
			try {
				String sessionId = bean.getId(); 
				String moduleId =  bean.getSessionModuleNo();
				String createdBy = bean.getCreatedBy();
				
				jdbcTemplate.update(sql, new Object[] { 
						sessionId,
						moduleId,
						createdBy,
						createdBy
					});
				return true;
			} catch (Exception e) {
				  
				return false;
			}
		}
		
		@Transactional(readOnly = false)
		public Boolean insertSessionPost(SessionDayTimeAcadsBean bean) {
			int timeboundId =Integer.parseInt(getTimeboundIdByModuleID(bean.getSessionModuleNo())) ;
			
			String sql = "INSERT INTO lti.post ( "
					+ " userId,subject_config_id, role, type, content, referenceId, "
					+ " visibility, acadYear,acadMonth,session_plan_module_id,createdBy,"
					+ " createdDate, subject,"
					+ " active,scheduledDate,scheduleFlag,startDate"
					+ ") "
					+ " VALUES( "
					+ "?,?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "sysdate(),?,"
					+ " ?,DATE_SUB(?, INTERVAL 1 HOUR),'Y',?"  
					+ ") ";
			try {
				String userId = "System" ;
				String role = "System"; 
				String type = "Session";
				String content = bean.getSessionName();
				String referenceId = bean.getId();
				String visibility = "1"; 
				String acadYear = bean.getYear();
				String acadMonth = bean.getMonth();
				String session_plan_module_id = bean.getSessionModuleNo();
				String createdBy = "System";
				String subject = bean.getSubject();
				String active = "Y";
				Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(bean.getDate()+" "+bean.getStartTime());    
				jdbcTemplate.update(sql, new Object[] {   
						 userId,
						 timeboundId,
						 role ,
						 type ,
						 content,
						 referenceId ,
						 visibility ,
						 acadYear,
						 acadMonth ,
						 session_plan_module_id ,
						 createdBy ,
						 subject ,
						 active ,
						 startTime,
						 startTime
					});
				return true;
			} catch (Exception e) {
				loggerForSessionScheduling.info("Error in insertSessionPost : "+e.getMessage());
				return false;
			}
		}
		
		@Transactional(readOnly = true)
		public ArrayList<ConsumerProgramStructureAcads> getConsumerTypes() {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT id,name FROM exam.consumer_type order by id asc";
			ArrayList<ConsumerProgramStructureAcads> consumerTypeList = (ArrayList<ConsumerProgramStructureAcads>) 
												jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
			return consumerTypeList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getAllSubjectsSemWiseAndConsumerType(String sem,String consumerType) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			if (StringUtils.isBlank(consumerType)) {
				consumerType = "retail";
			}
			String sql =  " SELECT DISTINCT (subject) FROM " 
						+ " exam.program_sem_subject " 
						+ " WHERE " 
						+ " consumerProgramStructureId IN "
						+ " (SELECT c_p_s.id FROM exam.consumer_program_structure c_p_s left join exam.consumer_type c_t" 
						+ " on c_t.id = c_p_s.consumerTypeId" 
						+ " WHERE c_t.name = ? and " 
						+ " programId IN (SELECT id FROM exam.program " 
						+ " WHERE " 
						+ " name LIKE 'Post Graduate%' OR name LIKE 'Diploma%')) " 
						+ " AND sem = ? AND active = 'Y' ";
			ArrayList<String> subjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{consumerType,sem}, new SingleColumnRowMapper(String.class));
			subjects.add("Orientation");
			return subjects;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getAllSubjectsSemWise(String sem) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT DISTINCT (subject) FROM " 
						+ " exam.program_sem_subject " 
						+ " WHERE " 
						+ " consumerProgramStructureId IN "
						+ " (SELECT id FROM exam.consumer_program_structure " 
						+ " WHERE " 
						+ " programId IN (SELECT id FROM exam.program " 
						+ " WHERE " 
						+ " name LIKE 'Post Graduate%' OR name LIKE 'Diploma%')) " 
						+ " AND sem = ? AND active = 'Y' ";
			ArrayList<String> subjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sem}, new SingleColumnRowMapper(String.class));
			subjects.add("Orientation");
			return subjects;
		}
		
		@Transactional(readOnly = true)
		public String getTimeboundIdByModuleID(String moduleID) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT timeboundId FROM acads.sessionplanid_timeboundid_mapping where sessionPlanId in(" + 
						 " SELECT sessionPlanId FROM acads.sessionplan_module where id=? ) ";
			String TimeboundId="";
			try {
				TimeboundId = (String) jdbcTemplate.queryForObject(sql,new Object[]{moduleID}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   
			}
			return TimeboundId;
		}
		
		@Transactional(readOnly = true)
		public String getSessionPlanSubjectFromModuleId(String moduleID) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT subject FROM acads.sessionplan where id in (SELECT sessionPlanId FROM acads.sessionplan_module where id =?)";
			String subject="";
			try {
				subject = (String) jdbcTemplate.queryForObject(sql,new Object[]{moduleID}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				  
			}
			return subject;
		}
		
		@Transactional(readOnly = false)
		public void updateSessionPost(SessionDayTimeAcadsBean session) {
			String sql = "Update lti.post set "
					+ "userId=?,"
					+ "scheduledDate=DATE_SUB(?, INTERVAL 1 HOUR),"
					+ "startDate=?,"
					+ "endDate=ADDTIME(?, '02:30:00'),"
					+ "lastModifiedBy=?,"
					+ "lastModifiedDate=sysdate()"
					+ "  where referenceId = ? ";
			try {
				Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(session.getDate()+" "+session.getStartTime());
				
				jdbcTemplate = new JdbcTemplate(dataSource);

				jdbcTemplate.update(sql, new Object[] { 
						session.getFacultyId(),
						startTime,
						startTime,
						startTime,
						session.getLastModifiedBy(),
						session.getId()
				});
			} catch (ParseException e) {
				loggerForSessionScheduling.info("Error in updateSessionPost "+e.getMessage());
			} 
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getSessionFromNow(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " Select * from acads.sessions where year = '"+CURRENT_ACAD_YEAR+"' and month = '"+CURRENT_ACAD_MONTH+"' "
						+ " and id in (5873, 6087, 6088, 6090, 6091, 6092, 6093, 6094, 6095, 6098, 6099, 6101, 6102, 6283, 6427, " 
						+ " 6609, 6761, 6915, 6916, 6917, 6918, 6919, 6920, 6921, 6922) "; 
			ArrayList<SessionDayTimeAcadsBean> sessionList = new ArrayList<SessionDayTimeAcadsBean>();
			try {
				sessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllCancelledSession(FileAcadsBean file){
			
			ArrayList<SessionDayTimeAcadsBean> sessionList = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT * FROM acads.sessions WHERE date between '"+file.getStartDate()+"' "
					 	+ " AND '"+file.getEndDate()+"' and isCancelled = 'Y' "
					 	+ " AND year = ? AND month = ? ";
			try {
				sessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {file.getYear(), file.getMonth()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSession(FileAcadsBean file){
			
			ArrayList<SessionDayTimeAcadsBean> sessionList = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " SELECT * FROM acads.sessions s "
						+ " INNER JOIN acads.session_subject_mapping ssm ON s.id = ssm.sessionId "
						+ " INNER JOIN exam.mdm_subjectcode msc ON ssm.subjectCodeId = msc.id "
						+ " WHERE date between '"+file.getStartDate()+"' AND '"+file.getEndDate()+"' "
						+ " AND year = ? AND month = ? "
						+ " GROUP BY s.id,ssm.subjectCodeId ";
			try {
				sessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {file.getYear(), file.getMonth()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public int getSessionVideoId(String sessionId, String facultyId) {
			String sql = "SELECT id FROM acads.video_content WHERE sessionId = ? AND facultyId = ? ORDER BY duration DESC LIMIT 1 ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			int videoId = 0;
			try {
				videoId = (int) jdbcTemplate.queryForObject(sql, new Object[]{sessionId, facultyId},Integer.class);
			} catch (Exception e) {
//				  
			}
			return videoId;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getCommonSessionsForMBAWx(StudentAcadsBean student, String year, String month, String type){

			String mbaAcadMonth = "";
			
			// Checking year/month as MBA-WX acad months are different
			if (month.equalsIgnoreCase("Apr")) {
				mbaAcadMonth = "Jan";
			}else if(month.equalsIgnoreCase("Oct")) {
				mbaAcadMonth = "Jul";
			}else {
				mbaAcadMonth = month;
			}
			
			namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			String sql =" SELECT * FROM " + 
						"    acads.faculty f, " + 
						"    acads.sessions asi " + 
						"WHERE " + 
						"    asi.facultyId = f.facultyId " + 
						"        AND (sem = :sem OR sem = 'All') " + 
						"        AND asi.year = :year AND month = :month " + 
						"        AND isCommon = 'Y' AND ((programList LIKE (:program))) " + 
						"        AND (asi.hasModuleId IS NULL OR asi.hasModuleId = 'Y') ";
			
			//Added temporary check for Apr-2022 
			if (month.equalsIgnoreCase("Apr") && (student.getSem().equalsIgnoreCase("2") || student.getSem().equalsIgnoreCase("4"))) {
				sql = sql + " AND asi.id NOT IN ('27817', '27818') ";
			}
			
			//Added temporary check for Apr-2022 
			if (!month.equalsIgnoreCase("Apr") && student.getSem().equalsIgnoreCase("1")) {
				sql = sql + " AND asi.id NOT IN ('28033', '28229') ";
			}
			
			if (type.equalsIgnoreCase("Upcoming")) {
				sql = sql + " AND asi.date >= CURDATE() ";
			}
			
			MapSqlParameterSource mapSource = new MapSqlParameterSource();
			mapSource.addValue("sem", student.getSem());
			mapSource.addValue("year", year);
			mapSource.addValue("month", mbaAcadMonth);
			mapSource.addValue("program", "%"+student.getProgram()+"%");
			ArrayList<SessionDayTimeAcadsBean> getCommonSessionsList = (ArrayList<SessionDayTimeAcadsBean>)namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return getCommonSessionsList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllScheduledSessions(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " Select * from acads.sessions where year = 2019 and month = 'Jan' order by date ";
			ArrayList<SessionDayTimeAcadsBean> sessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getAllPSSIdBySubjectName(String subject, String hasModuleId, int consumerProgramStructureId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String studentType = "and (studentType = 'Regular' or studentType is null) ";
			if ("Y".equalsIgnoreCase(hasModuleId) && (TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId))) {
				studentType = "and studentType ='TimeBound' ";
			}
			String sql = " SELECT id FROM exam.program_sem_subject where subject = ? and consumerProgramStructureId = ? and active = 'Y' "+studentType;
			List<Integer> idList = jdbcTemplate.query(sql, new Object[]{subject,consumerProgramStructureId}, new SingleColumnRowMapper(Integer.class));
			return idList;
		}
		
		@Transactional(readOnly = false)
		public int insertSessionSubjectMapping (SessionDayTimeAcadsBean session) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = 1;
			String sql =  " INSERT INTO acads.session_subject_mapping "
						+ " (sessionId, program_sem_subject_id, consumerProgramStructureId, subjectCodeId, createdBy, createdDate) " 
						+ " VALUES (?,?,?,?,?,sysdate()) ";
			
			String sessionId = session.getSessionId();
			int subjectCodeId = session.getSubjectCodeId();
			int program_sem_subject_id = session.getPrgmSemSubId();
			String consumerProgramStructureId = session.getConsumerProgramStructureId();
			String createdBy = session.getCreatedBy();
			String lastModifiedBy = session.getLastModifiedBy();
			
			try {
				jdbcTemplate.update(sql, new Object[] {sessionId, program_sem_subject_id, consumerProgramStructureId, subjectCodeId, createdBy});
				count = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return count;
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getConsumerType(String corporateName) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			if (StringUtils.isBlank(corporateName) || corporateName.equalsIgnoreCase("Retail")) {
				corporateName = "'Retail'";
			}else if ("All".equalsIgnoreCase(corporateName)) {
				corporateName = "'Retail','Verizon','CIPLA','VERTIV'";
			}else if ("Diageo".equalsIgnoreCase(corporateName)) {
				corporateName = "'Diageo'";
			}else if ("Verizon".equalsIgnoreCase(corporateName)) {
				corporateName = "'Verizon'";
			}else if ("BAJAJ".equalsIgnoreCase(corporateName)) {
				corporateName = "'BAJAJ'";
			}else if ("SAS".equalsIgnoreCase(corporateName)) {
				corporateName = "'SAS'";
			}else if ("UG".equalsIgnoreCase(corporateName) || "M.Sc".equalsIgnoreCase(corporateName)) {
				corporateName = "'Retail'";
			}else if ("Concentrix".equalsIgnoreCase(corporateName)) {
				corporateName = "'Concentrix'";
			}else if ("Multiplier".equalsIgnoreCase(corporateName)) {
				corporateName = "'Multiplier'";
			}
			
			List<Integer> consumerType = new ArrayList<Integer>();
			String sql = "Select id from exam.consumer_type where name in ("+corporateName+")";
			consumerType = jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(Integer.class));
			return consumerType;
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getconsumerProgramStructureIdList(List<Integer> consumerType){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String list = consumerType.toString();
			list = list.substring(1, list.length()-1);
			List<Integer> consumerProgramStructureIdList = new ArrayList<Integer>();
			String sql = "Select id from exam.consumer_program_structure where consumerTypeId in ("+list+")";
				consumerProgramStructureIdList = jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(Integer.class));	
			return consumerProgramStructureIdList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String>  getconsumerProgramStructureIds(String programId,String programStructureId, String consumerTypeId){
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =  " SELECT id FROM exam.consumer_program_structure "
						+ " Where programId in ("+ programId +") and "
						+ " programStructureId in ("+ programStructureId +") and "
						+ " consumerTypeId in ("+ consumerTypeId +")";

			ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			
			return consumerProgramStructureIds;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getCommonGroupProgramList(SessionDayTimeAcadsBean sessionBean) {
			try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT  " + 
					"    s.*, " + 
					"    p.code AS program, " + 
					"    p_s.program_structure AS programStructure, " + 
					"    c_t.name AS consumerType, " + 
					" 	 consumerProgramStructureId " + 
					" FROM " + 
					"    acads.sessions s " + 
					"        LEFT JOIN " + 
					"    acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					"        LEFT JOIN " + 
					"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = ssm.consumerProgramStructureId " + 
					"        LEFT JOIN " + 
					"    exam.program AS p ON p.id = c_p_s.programId " + 
					"        LEFT JOIN " + 
					"    exam.program_structure AS p_s ON p_s.id = c_p_s.programStructureId " + 
					"        LEFT JOIN " + 
					"    exam.consumer_type AS c_t ON c_t.id = c_p_s.consumerTypeId " + 
					" WHERE 1 = 1 " +
					"		AND s.year = ? AND s.month = ? " + 
					"		AND s.date = ? AND s.startTime = ? " +
					"       AND subject = ? "
					
					+ "and consumerProgramStructureId in ("+ sessionBean.getConsumerProgramStructureId() +")";
			
			return (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] 
						{sessionBean.getYear(),sessionBean.getMonth(),sessionBean.getDate(), sessionBean.getStartTime(), sessionBean.getSubject()},
					new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}
			catch(Exception e) {
				  
				return null;
			}
			
		}
		
		@Transactional(readOnly = false)
		public long insertNewSession(final SessionDayTimeAcadsBean session, final String userId) {
			
			int sessionTime = getSessionTime(session);
			String dateTime = session.getDate()+ " " +session.getStartTime();
			
			if (StringUtils.isBlank(session.getHasModuleId())) {
				session.setHasModuleId("N");
			}
			
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			final String sql =" INSERT INTO acads.sessions (date, startTime, endTime, day, subject, sessionName, createdBy, createdDate, "
							+ " lastModifiedBy, lastModifiedDate, year, month, facultyId, meetingKey, meetingPwd, ciscoStatus, "
							+ " joinUrl, hostUrl, hostKey, room, hostId, hostPassword, isCommon, corporateName, programList, "
							+ " facultyLocation, track, hasModuleId, moduleid ) "
							+ " VALUES (?,?,ADDDATE('"+dateTime+"', INTERVAL "+sessionTime+" MINUTE), DAYNAME(date),?,?,?,sysdate(), "
							+ " ?, sysdate(), ?,?,?,?,?,?, "
							+ " ?,?,?,?,?,?,?,?,?, "
							+ " ?,?,?,? ) ";
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			long primaryKey = 0;
			
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {

					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
						statement.setString(1,session.getDate());
						statement.setString(2,session.getStartTime());
						statement.setString(3,session.getSubject());
						statement.setString(4,session.getSessionName());
						statement.setString(5,userId);
						statement.setString(6,userId);
						statement.setString(7,session.getYear());
						statement.setString(8,session.getMonth());
						statement.setString(9,session.getFacultyId());
						statement.setString(10,session.getMeetingKey());
						statement.setString(11,session.getMeetingPwd());
						statement.setString(12,session.getCiscoStatus());
						statement.setString(13,session.getJoinUrl());
						statement.setString(14,session.getHostUrl());
						statement.setString(15,session.getHostKey());
						statement.setString(16,session.getRoom());
						statement.setString(17,session.getHostId());
						statement.setString(18,session.getHostPassword());
						statement.setString(19,session.getIsCommon());
						statement.setString(20,session.getCorporateName());
						statement.setString(21,session.getProgramList());
						statement.setString(22,session.getFacultyLocation());
						statement.setString(23,session.getTrack());
						statement.setString(24,session.getHasModuleId());
						statement.setString(25,session.getModuleId());
						
						return statement;
					}
					
				},holder);
				
				primaryKey = holder.getKey().longValue();
				
			} catch (Exception e) {
				  
			}
			return primaryKey;
		}
		
		@Transactional(readOnly = false)
		public void updateSessionSubjectMapping (long sessionId,String userId, String oldSessionId, String consumerProgramStructureId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  " UPDATE acads.session_subject_mapping SET sessionId = ?, lastModifiedBy = ?, lastModifiedDate=sysdate() "
						+ " WHERE sessionId = ? and consumerProgramStructureId in ( "+consumerProgramStructureId+" ) ";
		
			jdbcTemplate.update(sql, new Object[] {sessionId,userId,oldSessionId});
		
		}
		
		@Transactional(readOnly = false)
		public int deleteSessionSubjectMapping(String sessionId, String consumerProgramStructureId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;
			String sql = "DELETE FROM acads.session_subject_mapping WHERE sessionId = ? ";
			if (!consumerProgramStructureId.equalsIgnoreCase("All")) {
				sql = sql + " AND consumerProgramStructureId in ( "+consumerProgramStructureId+" )";
			}
			
			try {
				jdbcTemplate.update(sql, new Object[] {sessionId});
			} catch (Exception e) {
				loggerForSessionScheduling.info("Error in deleteSessionSubjectMapping : "+e.getMessage());
				return -1;
			}
			return row;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsByCPSIdV1(StudentAcadsBean student, String year, String month, ArrayList<String> subjects) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String subjectCommaSeparated = "''";
			for (int i = 0; i < subjects.size(); i++) {
				if(i == 0){
					subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			
			String sql = " SELECT *, s.id as id FROM " + 
						 "    acads.sessions s" + 
						 "        INNER JOIN " + 
						 "    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
						 "        INNER JOIN " + 
						 "    exam.program_sem_subject pss " +
						 "	  	  	ON ssm.consumerProgramStructureId = pss.consumerProgramStructureId " + 
						 "        	AND ssm.program_sem_subject_id = pss.id " + 
						 "        INNER JOIN " + 
						 "	  acads.faculty f ON s.facultyId = f.facultyId" +
						 "        INNER JOIN " + 
						 " 	  exam.examorder eo ON s.month = eo.acadMonth and s.year = eo.year " +
						 " 		  AND (s.isCommon = 'N' or s.isCommon is null ) " +
						 "        AND ssm.consumerProgramStructureId = ? " + 
						 "        AND pss.sem = ? " +
						 "		  AND pss.subject in ("+subjectCommaSeparated+") ";
			
			if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
				sql = sql + " AND s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
							" INNER JOIN  acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
							" INNER JOIN acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
							" WHERE tum.userId = " + student.getSapid() + " and tum.role='Student')" ;	
			}else{
				sql = sql + " AND eo.order = (SELECT o.order FROM exam.examorder o WHERE acadMonth = '"+month+"'  AND year = "+year+" AND acadSessionLive = 'Y') " +
							" AND (s.hasModuleId is null or s.hasModuleId <> 'Y') ";
			}
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			try {
				scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]{student.getConsumerProgramStructureId(),student.getSem()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return scheduledSessionList;
		}

		@Transactional(readOnly = true)
		public LinkedHashMap<String, String> getProgramNameAndCodeMap(String consumerType){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ProgramBean> programList = new ArrayList<ProgramBean>();
			LinkedHashMap<String, String> programNameNCodeMap = new LinkedHashMap<String, String>();
			String sql =" SELECT  " + 
						"    p.code, p.name " + 
						" FROM " + 
						"    exam.consumer_program_structure cps " + 
						"        INNER JOIN " + 
						"    exam.program p ON p.id = cps.programId " + 
						"        AND cps.consumerTypeId = ? " + 
						" GROUP BY p.id order by p.name ";
			try {
				programList = (ArrayList<ProgramBean>) jdbcTemplate.query(sql,new Object[]{consumerType}, new BeanPropertyRowMapper(ProgramBean.class));
			} catch (Exception e) {
				  
			}
			
			for (ProgramBean program : programList) {
				programNameNCodeMap.put(program.getName(), program.getCode());
			}
			return programNameNCodeMap;
		}
		
		@Transactional(readOnly = true)
		public String getCorporateName(String id) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String corporateName = "";
			String sql = "Select name from exam.consumer_type where id = ? " ;
			try {
				corporateName =  jdbcTemplate.queryForObject(sql,new Object[]{id}, String.class);
			} catch (Exception e) {
				  
			}
			return corporateName;
		}
		
		@Transactional(readOnly = true)
		public String getProgramNames(String id){

			jdbcTemplate = new JdbcTemplate(dataSource);
			String programList = "";
			String sql = "select code from exam.program where id in ("+id+")";
			
			try {
				List<String> allPrograms = jdbcTemplate.query(sql,new Object[]{},new SingleColumnRowMapper(String.class));
				if (allPrograms.size() > 0) {
					programList = String.join(",", allPrograms);
				}
			} catch (Exception e) {
				  
			}
			return programList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSessionOnSameDateTime(SessionDayTimeAcadsBean session){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String hasModuleId = " (hasModuleId is null OR hasModuleId ='N') ";
			if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
				hasModuleId = " hasModuleId ='Y' ";
			}
			
			String sql = "SELECT * FROM acads.sessions WHERE date = ? and startTime = ? AND "+hasModuleId;
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] 
									{session.getDate(),session.getStartTime()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			
			return scheduledSessionList;	
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSessionOnSameDate(SessionDayTimeAcadsBean session){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.sessions WHERE date = ? and (hasModuleId is null or hasModuleId ='N') ";
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] 
									{session.getDate()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			
			return scheduledSessionList;	
		}
		
		@Transactional(readOnly = true)
		public ArrayList<ProgramSubjectMappingAcadsBean> getAllApplicableConsumerProgramStructureId(String subject, String hasModuleId, String corporateName){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			ArrayList<Integer> consumerTypeIds = (ArrayList<Integer>) getConsumerType(corporateName);
			String consumerIds = StringUtils.join(consumerTypeIds, ",");
			
			String studentType = " AND (studentType = 'Regular' OR studentType IS NULL) ";
			if ("Y".equalsIgnoreCase(hasModuleId)) {
				studentType = " AND studentType = 'TimeBound' ";
			}
			String sql =  " SELECT * FROM exam.program_sem_subject pss "
						+ "	INNER JOIN  exam.consumer_program_structure cps ON pss.consumerProgramStructureId = cps.id"
						+ " WHERE subject = ? AND active = 'Y' " 
						+ " AND cps.consumerTypeId IN (" +consumerIds+ ")"
						+ studentType ;
			
			ArrayList<ProgramSubjectMappingAcadsBean> consumerProgramStructureIds = (ArrayList<ProgramSubjectMappingAcadsBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
			
			return consumerProgramStructureIds;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllActiveDayTime(SessionDayTimeAcadsBean session, String sessionDays, String viewMore){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String daysCheck = "";
			if (!StringUtils.isBlank(sessionDays)) {
				daysCheck = " AND dayName in("+sessionDays+") ";	
			}
			
			String dateCheck = "";
			if (viewMore.equalsIgnoreCase("Y")) {
				dateCheck = " AND (select endDate from acads.academic_calendar " +
							" WHERE year = '"+session.getYear()+"' AND month = '"+session.getMonth()+"') ";
			}else {
				dateCheck = " AND DATE_ADD('"+session.getDate()+"' , INTERVAL 15 DAY) ";
			}
			
			String sql =" SELECT c.date, DATE_ADD(startTime, interval 120 minute) as endTime, sd.* FROM " + 
						"    acads.session_days sd " + 
						"        INNER JOIN " + 
						"    acads.calendar c ON c.dayName = sd.day " + 
						" WHERE " + 
						"    sd.year = ? AND sd.month = ? " + 
						"    AND c.date BETWEEN DATE_ADD(? , INTERVAL 1 DAY) " +
						dateCheck +
						daysCheck +
						" GROUP BY CONCAT(date, day, startTime) "+
						" ORDER BY date, startTime";
			
			ArrayList<SessionDayTimeAcadsBean> dayTimeList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getYear(), session.getMonth(), 
														session.getDate()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));			
			return dayTimeList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getProgramDetails(String consumerProgramStructureId) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<SessionDayTimeAcadsBean> programDetails = new ArrayList<SessionDayTimeAcadsBean>();
			String sql = "SELECT  " + 
					"    p.code AS program, " + 
					"    ps.program_structure AS programStructure, " + 
					"    ct.name AS consumerType " + 
					"FROM " + 
					"    exam.consumer_program_structure cps " + 
					"        INNER JOIN " + 
					"    exam.program p ON p.id = cps.programId " + 
					"        INNER JOIN " + 
					"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
					"        INNER JOIN " + 
					"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
					"        AND cps.id in (" +consumerProgramStructureId+ ") ";
			try {
				programDetails = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				loggerForSessionScheduling.info("Error in getProgramDetails : "+e.getMessage());
			}
			
			return programDetails;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getFacultyClashDeatils(SessionDayTimeAcadsBean session) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.sessions WHERE date = ? AND facultyId = ?  ";
			ArrayList<SessionDayTimeAcadsBean> facultySession = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getDate(), session.getFacultyId()}, 
					new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return facultySession;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getSameDaySessionsForFaculty(SessionDayTimeAcadsBean session){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.sessions WHERE date = ? AND facultyId = ? ";
			ArrayList<SessionDayTimeAcadsBean> facultySessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getDate(), session.getFacultyId()}, 
					new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return facultySessions;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getMoreThan3ClashSessionsV1(SessionDayTimeAcadsBean session){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =" SELECT  " + 
						"    s.date, s.startTime, s.subject, s.sessionName, s.facultyId, s.track, pss.sem, " +
						"	 p.code as program, ps.program_structure as programStructure,  " + 
						"    ct.name as consumerType, CONCAT(pss.sem, pss.consumerProgramStructureId) as uniqueSemSubject " + 
						"FROM " + 
						"    acads.sessions s " + 
						"        INNER JOIN " + 
						"    acads.session_subject_mapping ss ON ss.sessionId = s.id " + 
						"        INNER JOIN " + 
						"    exam.program_sem_subject pss ON pss.id = ss.program_sem_subject_id " + 
						"		INNER JOIN " + 
						"	exam.consumer_program_structure cps on cps.id = pss.consumerProgramStructureId " + 
						"		INNER JOIN " + 
						"	exam.program p on p.id = cps.programId " + 
						"		INNER JOIN " + 
						"	exam.program_structure ps on ps.id = cps.programStructureId " + 
						"		INNER JOIN " + 
						"	exam.consumer_type ct on ct.id = cps.consumerTypeId " + 
						"        AND pss.consumerProgramStructureId = ss.consumerProgramStructureId " + 
						" WHERE " + 
						"    s.date = ? " + 
						"		AND pss.active = 'Y' " + 
						"        AND concat(pss.sem, pss.consumerProgramStructureId) IN (SELECT  " + 
						"            CONCAT(sem,consumerProgramStructureId) " + 
						"        FROM " + 
						"            exam.program_sem_subject " + 
						"        WHERE " + 
						"            subject = ? AND active = 'Y' AND (studentType IS NULL OR studentType = 'regular') " + 
						"             " + 
						"            and CONCAT(sem,consumerProgramStructureId) in " + 
						"            (SELECT  " + 
						"            CONCAT(pss.sem, pss.consumerProgramStructureId) AS subjectCount " + 
						"        FROM " + 
						"            acads.session_subject_mapping ss " + 
						"                INNER JOIN " + 
						"            acads.sessions s ON s.id = ss.sessionId " + 
						"                INNER JOIN " + 
						"            exam.program_sem_subject pss ON pss.id = ss.program_sem_subject_id " + 
						"                AND pss.consumerProgramStructureId = ss.consumerProgramStructureId " + 
						"                AND pss.active = 'Y' " + 
						"                AND s.date = ? " + 
						"                AND (track = '' OR track = ? ) " + 
						"        GROUP BY pss.sem , pss.consumerProgramStructureId " + 
						"        HAVING COUNT(subjectCount) > 2) " + 
						"            ) " + 
						"        AND (track = '' OR track = ? ) " +
						"		 ORDER BY uniqueSemSubject" ;
			ArrayList<SessionDayTimeAcadsBean> clashingSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getDate(), session.getSubject(), 
											session.getDate(), session.getTrack(), session.getTrack()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return clashingSessions;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<FacultyAvailabilityBean> getDateDayTimeMapping(String year, String month){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<FacultyAvailabilityBean> sessionDayTimeList = new ArrayList<FacultyAvailabilityBean>();
			
			String sql =" SELECT  " + 
						"    c.date, sd.*, startTime AS time " + 
						" FROM " + 
						"    acads.session_days sd " + 
						"        INNER JOIN acads.calendar c ON c.dayName = sd.day " + 
						" WHERE " + 
						"    sd.year = ? AND sd.month = ? AND c.date  " + 
						"		BETWEEN  " + 
						"			(SELECT startDate FROM acads.academic_calendar WHERE year = ? AND month = ? )  " + 
						"        AND  " + 
						"			(SELECT endDate FROM acads.academic_calendar WHERE year = ? AND month = ? ) " + 
						" GROUP BY CONCAT(date, day, startTime)";
			try {
				sessionDayTimeList = (ArrayList<FacultyAvailabilityBean>) jdbcTemplate.query(sql, new Object[] {year,month,year,month,year,month}, 
									new BeanPropertyRowMapper(FacultyAvailabilityBean.class));
			} catch (Exception e) {
				  
			}
			return sessionDayTimeList;
		}
		
		@Transactional(readOnly = true)
		public boolean isAlreadyAdded(FacultyAvailabilityBean bean) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =" SELECT count(*) FROM acads.facultyavailability " + 
						" WHERE facultyId = ? AND year = ? AND month  = ? " + 
						" AND time = ?  AND date = ? ";
			
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{bean.getFacultyId(), bean.getYear(),bean.getMonth(),
					bean.getTime(), bean.getDate()}, Integer.class);
			if(count == 0){
				return true;
			}else{
				return false;
			}
		}
		
		@Transactional(readOnly = true)
		public FacultyAvailabilityBean findFacultyAvailabilityById(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			FacultyAvailabilityBean session = new FacultyAvailabilityBean();
			String sql = "SELECT * FROM acads.facultyavailability WHERE id = ? ";
			try {
				session= (FacultyAvailabilityBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(FacultyAvailabilityBean.class));
			} catch (Exception e) {
				  
			}
			return session;
		}
		
		@Transactional(readOnly = true)
		public int getPSSid(SessionDayTimeAcadsBean session) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT id FROM exam.program_sem_subject where subject = ? and consumerProgramStructureId = ? and active = 'Y'";
			int pssid = 0;
			try {
				pssid = jdbcTemplate.queryForObject(sql, new Object[]{session.getSubject(),session.getConsumerProgramStructureId()}, Integer.class);
			} catch (Exception e) {
				  
			}
			return pssid;
		}
		
		@Transactional(readOnly = true)
		public boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrackNew(SessionDayTimeAcadsBean session, String isTrack){ 
			
			ArrayList<SessionDayTimeAcadsBean> sessions = new ArrayList<SessionDayTimeAcadsBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			//Check session Track
			String trackCheck = "";
			if (isTrack.equalsIgnoreCase("Y")) {
				if (!StringUtils.isBlank(session.getTrack())) {
					trackCheck = " AND (s.track = '' or  s.track = '"+session.getTrack()+"') "; 
				}else{
					trackCheck = " AND s.track = '' "; 	
				}
			}
			
			String sql =" SELECT s.* FROM acads.sessions s " + 
						"        INNER JOIN " + 
						"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
						"        INNER JOIN " + 
						"    exam.program_sem_subject pss ON ssm.program_sem_subject_id = pss.id " + 
						"        AND ssm.consumerProgramStructureId = pss.consumerProgramStructureId " + 
						"        AND s.month = ? AND year = ? " + 
						"        AND s.date = ? AND s.startTime = ? " + 
						"        AND ssm.consumerProgramStructureId = ? " + 
						trackCheck +
						" GROUP BY CONCAT(pss.consumerProgramStructureId, pss.sem) " + 
						" HAVING COUNT(CONCAT(pss.consumerProgramStructureId, pss.sem)) > 2";
			try {
				sessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getMonth(),session.getYear(),
						session.getDate(),session.getStartTime(),session.getConsumerProgramStructureId()},new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			if (sessions.size() > 0 || sessions != null) {
				return false;
			}else {
				return true;
			}
		}
		
		@Transactional(readOnly = true)
		public boolean isNoSubjectClashingNew(SessionDayTimeAcadsBean session) {
			
			//Check session Track
			String trackCheck = "";
			if (!StringUtils.isBlank(session.getTrack())) {
				trackCheck = " AND (s.track = '' or  s.track = '"+session.getTrack()+"') "; 
			}else{
				trackCheck = " AND s.track = '' "; 	
			}
			
			String moduleCheck = "";
			if (StringUtils.isBlank(session.getHasModuleId())) {
				moduleCheck = " AND (hasModuleId is null or hasModuleId = 'N') ";
			} else {
				moduleCheck = " AND hasModuleId = '"+session.getHasModuleId()+"'" ;
			}
			
			String sql =  " SELECT COUNT(*) FROM " 
						+ "    acads.sessions s "  
						+ "        INNER JOIN " 
						+ "    acads.session_subject_mapping ssm ON s.id = ssm.sessionId "
						+ "        AND s.month = ? AND year = ? " 
						+ "		 AND date = ? AND s.startTime = ? "
						+ "		 AND ssm.consumerProgramStructureId = ? " 
						+ " 	 AND (isCancelled <> 'Y' OR isCancelled IS NULL) "
						+ trackCheck
						+ moduleCheck;
			
			int count = 1;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				
				count = (int) jdbcTemplate.queryForObject(sql, new Object[] {session.getMonth(),session.getYear(),
						session.getDate(), session.getStartTime(), session.getConsumerProgramStructureId()},Integer.class);
				if(count == 0){
					return true;
				}else{
					return false;
				}
				
			} catch (DataAccessException e) {
				  
				return false;

			}
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getUpcomingScheduledSessionForStudentsByCPSIdV1(StudentAcadsBean student, ArrayList<String> subjects) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String subjectCommaSeparated = "''";
			for (int i = 0; i < subjects.size(); i++) {
				if(i == 0){
					subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			
			String sql = " SELECT s.*, f.*, s.id as id FROM " + 
						 "    acads.sessions s" + 
						 "        INNER JOIN " + 
						 "    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
						 "        INNER JOIN " + 
						 "    exam.program_sem_subject pss ON ssm.consumerProgramStructureId = pss.consumerProgramStructureId " +
						 "        	AND ssm.program_sem_subject_id = pss.id " +
						 "        INNER JOIN " + 
						 "	  acads.faculty f ON s.facultyId = f.facultyId " +
						 "        INNER JOIN " + 
						 " 	  exam.examorder eo ON s.month = eo.acadmonth AND s.year = eo.year " +
						 "    AND eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') " +
						 "        AND ssm.consumerProgramStructureId = ? " + 
						 "        AND pss.sem = ? " + 
						 "		  AND pss.subject in ("+subjectCommaSeparated+") " +
						 "		  AND concat(date,' ',endTime) >= sysdate()";
			
			if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
						sql = sql + " AND s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
									" 		INNER JOIN  " +
									" acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
									" 		INNER JOIN " +
									" acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
									" 		WHERE tum.userId = " + student.getSapid() + " and tum.role='Student')" ;
					}
					sql = sql + " ORDER BY date, startTime asc LIMIT 10 ";
			
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			try {
				scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]
										{student.getConsumerProgramStructureId(), student.getSem()},  new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return scheduledSessionList;
		}
		
		@Transactional(readOnly = true)
		public String getAcademicCalendarEndDate() {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String endDate = "";
			String sql = "SELECT endDate FROM acads.academic_calendar WHERE year = '"+CURRENT_ACAD_YEAR+"' AND month = '"+CURRENT_ACAD_MONTH+"'" ;
			try {
				endDate =  jdbcTemplate.queryForObject(sql,new Object[]{}, String.class);
			} catch (Exception e) {
				  
			}
			return endDate;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionBySubject(SessionDayTimeAcadsBean session) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =" SELECT * FROM acads.sessions s " + 
						" INNER JOIN acads.faculty f ON s.facultyId = f.facultyId " + 
						" AND s.year = ? AND month = ? AND s.subject = ? " +
						" AND (hasModuleId = 'N' or hasModuleId  is null) " ;

			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{session.getYear(), session.getMonth(), 
																	session.getSubject()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return scheduledSessionList;
		}
		
		@Transactional(readOnly = true)
		public int getSessionApplicableCount(String sessionId) {
			String sql = "SELECT COUNT(*) FROM acads.session_subject_mapping WHERE sessionId = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = 0;
			try {
				count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sessionId},Integer.class);
			} catch (Exception e) {
				loggerForSessionScheduling.info("Error in getSessionApplicableCount "+e.getMessage());
			}
			return count;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> searchCommonSession(SessionDayTimeAcadsBean session){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<Object> parameters = new ArrayList<Object>();
			ArrayList<SessionDayTimeAcadsBean> sessionList = new ArrayList<SessionDayTimeAcadsBean>();
			String sessionTable = "";
			try {
				
			String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(acadDateFormat);
			Date historyDate=formatter.parse("2020-07-01");
			
			if (date.compareTo(historyDate) >= 0){
				sessionTable = " acads.sessions ";
			}else{
				sessionTable = " acads.sessions_history ";
			}
			
			String sql =" SELECT s.*, f.*, s.id AS id, s.facultyId AS facultyId " +
						" FROM "+sessionTable+" s " +
						" 		INNER JOIN " + 
						"    acads.faculty f ON s.facultyId = f.facultyId OR s.altFacultyId = f.facultyId OR s.altFacultyId2 = f.facultyId OR s.altFacultyId3 = f.facultyId " + 
						" WHERE isCommon = 'Y' ";
						
			if (!StringUtils.isBlank(session.getYear())) {
				sql = sql + " AND s.year = ? ";
				parameters.add(session.getYear());
			}
			
			if (!StringUtils.isBlank(session.getMonth())) {
				sql = sql + " AND s.month = ? ";
				parameters.add(session.getMonth());
			}
			
			if (!StringUtils.isBlank(session.getSubject())) {
				sql = sql + " AND s.subject = ? ";
				parameters.add(session.getSubject());
			}
			
			if (!StringUtils.isBlank(session.getDate())) {
				sql = sql + " AND date = ? ";
				parameters.add(session.getDate());
			}
			
			if (!StringUtils.isBlank(session.getFacultyId())) {
				sql = sql + " AND (s.facultyId = ? OR s.altFacultyId = ? OR s.altFacultyId2 = ? OR s.altFacultyId3 = ? ) ";
				parameters.add(session.getFacultyId());
				parameters.add(session.getFacultyId());
				parameters.add(session.getFacultyId());
				parameters.add(session.getFacultyId());
			}
			
			if (!StringUtils.isBlank(session.getFacultyLocation())) {
				sql = sql + " AND (s.facultyLocation = ? OR altFacultyLocation = ? OR altFaculty2Location = ? OR altFaculty3Location = ? ) ";
				parameters.add(session.getFacultyLocation());
				parameters.add(session.getFacultyLocation());
				parameters.add(session.getFacultyLocation());
				parameters.add(session.getFacultyLocation());
			}
			
			sql = sql + " GROUP BY s.id ";
			


			
				sessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

			} catch (Exception e) {
				  
			}
			
			return sessionList;
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getConsumerProgramStructureData(){
			
			List<SessionDayTimeAcadsBean> consumerProgramStructureList = new ArrayList<SessionDayTimeAcadsBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql=" SELECT " + 
						"    cps.id," + 
						"    p.code AS program," + 
						"    ps.program_structure AS programStructure," + 
						"    ct.name AS consumerType " + 
						" FROM " + 
						"    exam.consumer_program_structure cps " + 
						"        INNER JOIN " + 
						"    exam.program p ON cps.programId = p.id " + 
						"        INNER JOIN " + 
						"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
						"        INNER JOIN " + 
						"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
						" ORDER BY ct.name , ps.program_structure , p.code  ";			
			
			try {
				consumerProgramStructureList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}catch(Exception e){
				   
			}
			return consumerProgramStructureList;
		}
		
		@Transactional(readOnly = true)
		public List<SessionDayTimeAcadsBean> getConsumerProgramStructureDataBySubject(String subject){
			
			List<SessionDayTimeAcadsBean> consumerProgramStructureList = new ArrayList<SessionDayTimeAcadsBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql=" SELECT DISTINCT " + 
						"    cps.id," + 
						"    p.code AS program," + 
						"    ps.program_structure AS programStructure," + 
						"    ct.name AS consumerType " + 
						" FROM " + 
						"    exam.consumer_program_structure cps " + 
						"        INNER JOIN " + 
						"    exam.program p ON cps.programId = p.id " + 
						"        INNER JOIN " + 
						"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
						"        INNER JOIN " + 
						"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
						"        INNER JOIN " + 
						"	 exam.program_sem_subject pss on cps.id = pss.consumerProgramStructureId " +
						" 	 	 WHERE pss.subject = ? AND pss.active= 'Y' " +
						" ORDER BY ct.name , ps.program_structure , p.code  ";			
			
			try {
				consumerProgramStructureList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {subject}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}catch(Exception e){
				   
			}
			return consumerProgramStructureList;
		}
		
		@Transactional(readOnly = true)
		public boolean isSubjectApplicable(String masterKeys, String subject){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String query = "SELECT COUNT(*) FROM exam.program_sem_subject WHERE consumerProgramStructureId in ("+masterKeys+") AND subject = ? ";
			try{
				int rowCount = (int)jdbcTemplate.queryForObject(query,new Object[] {subject}, Integer.class);
				if(rowCount > 0){
					return true;
				}
			}catch(Exception e){
				  
			}
			return false;
		}
		
		@Transactional(readOnly = false)
		public boolean deleteParallelSession(String sessionId, String type) {
			String sql = "";
			if("1".equals(type)){
				sql = " UPDATE acads.sessions SET altMeetingKey=NULL, altMeetingPwd=NULL, altFacultyId=NULL, altFacultyLocation=NULL, altHostId=NULL, altHostKey=NULL WHERE id = ? ";
			}
			if("2".equals(type)){
				sql = " UPDATE acads.sessions SET altMeetingKey2=NULL, altMeetingPwd2=NULL, altFacultyId2=NULL, altFaculty2Location=NULL, altHostId2=NULL, altHostKey2=NULL WHERE id = ? ";
			}
			if("3".equals(type)){
				sql = " UPDATE acads.sessions SET altMeetingKey3=NULL, altMeetingPwd3=NULL, altFacultyId3=NULL, altFaculty3Location=NULL, altHostId3=NULL, altHostKey3=NULL WHERE id = ? ";
			}
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			try {
				jdbcTemplate.update(sql, new Object[] {sessionId});
				return true;
			} catch (Exception e) {
				  
				return false;
			}
			
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getAllCoreSubjects() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  "SELECT subjectname FROM exam.subjects WHERE commonSubject IN ('G1' , 'G2') ";
			ArrayList<String> subjects = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			subjects.add("Orientation");
			subjects.add("Business Communication");
			
			return subjects;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSessionsByCourseMapping(String userId){
			jdbcTemplate = new JdbcTemplate(dataSource);

		
			ArrayList<SessionDayTimeAcadsBean> allSessions = new ArrayList<SessionDayTimeAcadsBean>();
			String sql =  " SELECT s.*, f.firstName, f.lastName, ssm.program_sem_subject_id as programSemSubjectId "

						+ " 	FROM exam.student_course_mapping scm "
				        + " 		INNER JOIN "
				        + " 	exam.examorder eo ON scm.acadMonth = eo.acadMonth AND scm.acadYear = eo.year "
				        + "			INNER JOIN "
				        + "		acads.sessions s ON eo.acadMonth = s.month AND eo.year = s.year "
				        + "			INNER JOIN "
				        + "		acads.session_subject_mapping ssm ON s.id = ssm.sessionId AND scm.program_sem_subject_id = ssm.program_sem_subject_id "
				        + "			INNER JOIN "
				        + "		acads.faculty f ON s.facultyId = f.facultyId"
				        + "			WHERE role = 'Student' "
				        + "				AND eo.order = (SELECT MAX(eo.order) FROM exam.examorder eo WHERE acadSessionLive = 'Y') "
				        + "				AND userId = ? ";
			try {
//				allSessions = (ArrayList<SessionDayTimeBean>) jdbcTemplate.query(sql, new Object[] {userId }, new BeanPropertyRowMapper(SessionDayTimeBean.class));
				allSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, userId);
			        }}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}catch(Exception e){
				   
			}
			return allSessions;
		}
		
		@Transactional(readOnly = true)
		public List<Integer> getPSSIdFromCourseMapping(String sapid){
			List<Integer> PSSIdsList = new ArrayList<Integer>();
			
			//Prepare SQL query.
			String GET_PSS_SQL = "select program_sem_subject_id from exam.student_course_mapping where userId = ?";
			
			try {
				//Execute namedJdbcTemplate query method
				PSSIdsList = jdbcTemplate.query(GET_PSS_SQL,new BeanPropertyRowMapper<Integer>(Integer.class),sapid);
			} catch (Exception e) {
				  
			}
			
			//return PSS id's list
			return PSSIdsList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSessionsByCourseMappingForUpcoming(String userId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<SessionDayTimeAcadsBean> allSessions = new ArrayList<SessionDayTimeAcadsBean>();
			String sql =  " SELECT s.*, f.firstName, f.lastName "
						+ " 	FROM exam.student_course_mapping scm "
				        + " 		INNER JOIN "
				        + " 	exam.examorder eo ON scm.acadMonth = eo.acadMonth AND scm.acadYear = eo.year "
				        + "			INNER JOIN "
				        + "		acads.sessions s ON eo.acadMonth = s.month AND eo.year = s.year "
				        + "			INNER JOIN "
				        + "		acads.session_subject_mapping ssm ON s.id = ssm.sessionId AND scm.program_sem_subject_id = ssm.program_sem_subject_id "
				        + "			INNER JOIN "
				        + "		acads.faculty f ON s.facultyId = f.facultyId"
				        + "			WHERE role = 'Student' "
				        + "				AND eo.order = (SELECT MAX(eo.order) FROM exam.examorder eo WHERE acadSessionLive = 'Y') "
				        + "				AND userId = ? "
				        + "				AND CONCAT(date,' ',endTime) >= sysdate() "
				        + "				ORDER BY date, startTime asc LIMIT 10 ";
			try {
				allSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {userId }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}catch(Exception e){
				   
			}
			return allSessions;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getApplicableCPSIdForSession(String sessionId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<String> couconsumerProgramStructureIds = new ArrayList<String>();
			String sql = "SELECT consumerProgramStructureId FROM acads.session_subject_mapping WHERE sessionId = ? ";
			
			try {
				couconsumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sessionId}, new SingleColumnRowMapper(String.class));
			} catch (Exception e) {
				  
			}
			return couconsumerProgramStructureIds;
		}
		
		@Transactional(readOnly = true)
		public String getSessionIdByDateTimeSubjectTrack(SessionDayTimeAcadsBean session) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sessionId = null;
			String sql = "SELECT id FROM acads.sessions s WHERE s.date=? AND s.startTime=? AND s.subject=? AND s.track=? ";
			try {
				sessionId = (String) jdbcTemplate.queryForObject(sql, new Object[]{session.getDate(),session.getStartTime(), session.getSubject(), session.getTrack()}, String.class);
			} catch (Exception e) {
				//  
			}
			return sessionId;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<SessionDayTimeAcadsBean> getAllSessionsByCourseMappingFromToday(String userId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<SessionDayTimeAcadsBean> allSessions = new ArrayList<SessionDayTimeAcadsBean>();
			String sql =  " SELECT s.*, f.firstName, f.lastName "
						+ " 	FROM exam.student_course_mapping scm "
				        + " 		INNER JOIN "
				        + " 	exam.examorder eo ON scm.acadMonth = eo.acadMonth AND scm.acadYear = eo.year "
				        + "			INNER JOIN "
				        + "		acads.sessions s ON eo.acadMonth = s.month AND eo.year = s.year "
				        + "			INNER JOIN "
				        + "		acads.session_subject_mapping ssm ON s.id = ssm.sessionId AND scm.program_sem_subject_id = ssm.program_sem_subject_id "
				        + "			INNER JOIN "
				        + "		acads.faculty f ON s.facultyId = f.facultyId"
				        + "			WHERE role = 'Student' "
				        + "				AND eo.order = (SELECT MAX(eo.order) FROM exam.examorder eo WHERE acadSessionLive = 'Y') "
				        + "				AND userId = ? "
				        + "				AND s.date >= CURDATE() ";
			try {
//				allSessions = (ArrayList<SessionDayTimeBean>) jdbcTemplate.query(sql, new Object[] {userId }, new BeanPropertyRowMapper(SessionDayTimeBean.class));
				allSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, userId);
					}}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			}catch(Exception e){
				   
			}
			return allSessions;
		}
		
		@Transactional(readOnly = true)
		public boolean isCourseMappingApplicableForCurrentAcadCycle(String sapid){
				
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =" SELECT COUNT(*) FROM " + 
						"    exam.student_course_mapping " + 
						" WHERE " + 
						"    acadMonth = '"+CURRENT_ACAD_MONTH+"' AND acadYear = "+CURRENT_ACAD_YEAR+" " + 
						"    AND userId = ? ";
			try{
//				int rowCount = (int)jdbcTemplate.queryForObject(sql,new Object[] {sapid}, Integer.class);
				int rowCount = jdbcTemplate.query(sql,  new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, sapid);
					}}, new ResultSetExtractor<Integer>() {

					@Override
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
	  						  return rs.getInt(1);
	  					}
	  					return 0;
					}
	            });	
				if(rowCount > 0){
					return true;
				}
			}catch(Exception e){
				  
			}
				return false;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramListBySessionId(String id) {
			ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =" SELECT  " + 
						"    GROUP_CONCAT(p.code SEPARATOR ', ') AS program, sem, program_structure AS prgmStructApplicable" + 
						" FROM " + 
						"    acads.session_subject_mapping ssm " + 
						"        INNER JOIN " + 
						"    exam.program_sem_subject pss ON ssm.consumerProgramStructureId = pss.consumerProgramStructureId " + 
						"        AND ssm.program_sem_subject_id = pss.id " + 
						"        INNER JOIN " + 
						"    exam.consumer_program_structure cps ON pss.consumerProgramStructureId = cps.id " + 
						"        INNER JOIN " + 
						"    exam.program p ON cps.programId = p.id " + 
						"        INNER JOIN " + 
						"    exam.program_structure ps ON cps.programStructureId = ps.id " + 
						" WHERE " + 
						"    sessionId = ? " + 
						" GROUP BY subject , sem , program_structure " + 
						" ORDER BY subject , program_structure ";
			try {
				subjectProgramList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new Object[]{id}, 
						new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
			} catch (Exception e) {
				  
			}
			
			return subjectProgramList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getAllSubjectCodes() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT subjectcode FROM exam.mdm_subjectcode ";
			ArrayList<String> subjectCodeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return subjectCodeList;
		}
		
		@Transactional(readOnly = true)
		public String getSubjectNameBySubjectCode(String subjectCode){
			try {
				String sql = " SELECT subjectname FROM exam.mdm_subjectcode WHERE subjectcode = ? ";
				String subjectName = (String) jdbcTemplate.queryForObject(sql,new Object[]{subjectCode},String.class);
				return subjectName;
			} catch (Exception e) {
				return null;
			}
		}
		
		@Transactional(readOnly = false)
		public ArrayList<ConsumerProgramStructureAcads> getDataForSessionSubjectMapping(SessionDayTimeAcadsBean session) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ConsumerProgramStructureAcads> consumerTypeList = new ArrayList<ConsumerProgramStructureAcads>();
			
			try {
				String sql =" SELECT " + 
							"    scm.subjectCodeId, " + 
							"    sc.subjectcode, " + 
							"    scm.consumerProgramStructureId, " + 
							"    scm.id AS programSemSubjectId, " + 
							"    sc.subjectname AS subject " + 
							" FROM " + 
							"    exam.mdm_subjectcode_mapping scm " + 
							"        INNER JOIN " + 
							"    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id " + 
							" WHERE ";
				
				if (!StringUtils.isBlank(session.getMasterKey())) {
					sql += " scm.id IN (" +session.getMasterKey()+ ") ";
					consumerTypeList = (ArrayList<ConsumerProgramStructureAcads>)jdbcTemplate.query(sql, new Object[] {}, 
							new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				}else {
					sql += " sc.subjectcode = ? ";
					consumerTypeList = (ArrayList<ConsumerProgramStructureAcads>)jdbcTemplate.query(sql, new Object[] {session.getSubjectCode()}, 
							new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
				}
				
			} catch (Exception e) {
				  e.printStackTrace();
			}
			
			return consumerTypeList;
		}
		
		@Transactional(readOnly = true)
		public boolean isNoSubjectClashingV2(SessionDayTimeAcadsBean session) {
			String year = session.getYear();
			String month = session.getMonth();
			String date = session.getDate();
			String time = session.getStartTime();
			String subject = session.getSubject();
			String consumerType = session.getCorporateName();
			String isAdditionalSession = session.getIsAdditionalSession();
			
			if("Y".equalsIgnoreCase(isAdditionalSession)){
				//Additional session can be held along with same semester subjects
				return true;
			}
			
			String sqlForCorporateName = "";
			
			if ("".equalsIgnoreCase(consumerType) || "All".equalsIgnoreCase(consumerType)) {
				consumerType = "Retail";
			}
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(date);
			parameters.add(time);
			parameters.add(year);
			parameters.add(month);
			parameters.add(year);
			parameters.add(month);
			
			String corporateName = session.getCorporateName(); //added on 24Jan19 by Pranit to check clashes corporate wise.
			if ("".equalsIgnoreCase(corporateName) || "All".equalsIgnoreCase(corporateName)) {
				sqlForCorporateName = " AND (corporateName = '' OR corporateName = 'All') ";
			}else{
				sqlForCorporateName = " AND corporateName = ? ";
				parameters.add(corporateName);
			}
			
			String track = session.getTrack();
			String track_check_sql =  " "; 
			if (!StringUtils.isBlank(track)) {
				track_check_sql = " AND (track = '' OR track = ? ) "; 
				parameters.add(track);
			}
			
			String sql = "";
			if ("SAS".equalsIgnoreCase(corporateName)) {
		
				sql = 	  " SELECT COUNT(*) FROM"
						+ " 	acads.calendar c, acads.session_days sd "
						+ " WHERE "
						+ " 	c.date = ? and sd.startTime =  ? and year = ? and month = ? "
						+ " 		AND CONCAT(c.date, sd.startTime) NOT IN "
						+ "			(SELECT CONCAT(date, startTime) FROM"
						+ " 			acads.sessions "
						+ "			WHERE year = ? AND month = ? "
						+ 	sqlForCorporateName
						+ 	track_check_sql
						+ " 			AND (isCancelled <> 'Y' OR isCancelled IS NULL)"
						+ "				AND subject IN "
						+ "				(SELECT DISTINCT subject FROM "
						+ "						exam.mdm_subjectcode msc "
						+ "					INNER JOIN exam.mdm_subjectcode_mapping mscm ON msc.id = mscm.subjectCodeId AND mscm.active = 'Y' "
						+ "				 WHERE msc.active = 'Y' "
						+ "					AND CONCAT(consumerProgramStructureId,sem) IN "
						+ "					(SELECT DISTINCT CONCAT(mscm2.consumerProgramStructureId, mscm2.sem) FROM "
						+ "						exam.mdm_subjectcode msc2 "
						+ " 					INNER JOIN exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId AND mscm2.active = 'Y' "
						+ "						INNER JOIN exam.consumer_program_structure cps ON mscm2.consumerProgramStructureId = cps.id "
						+ "						INNER JOIN exam.consumer_type ct ON cps.consumerTypeId = ct.id "
						+ "					WHERE msc2.subjectname = ? AND msc2.active = 'Y' AND ct.name = ? "
						+ "					)"
						+ "				) "
						+ "	 		) " // No other subject of same semester is scheduled at that time
						+ " ORDER BY c.date , sd.startTime ";
				
			}else{
				sql = " SELECT COUNT(*) FROM "
					+ " 	acads.calendar c,  acads.session_days sd "
					+ " WHERE "
					+ " 	c.date = ? AND sd.startTime = ? AND year = ?  AND month = ? "
					+ " 		AND CONCAT(c.date, sd.startTime) NOT IN "
					+ "				(SELECT CONCAT(date, startTime) FROM "
					+ "					acads.sessions s "
					+ "				 WHERE s.year = ? AND s.month = ? "
					+ 	sqlForCorporateName
					+ 	track_check_sql
					+ "					AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";
				
				if("Y".equalsIgnoreCase(session.getHasModuleId())) {
					sql = sql + " AND s.moduleId IN (" + 
								"		SELECT " + 
								"			aspm.id " + 
								"				FROM " + 
								"					acads.sessionplan_module aspm" + 
								"				WHERE " + 
								"					aspm.sessionPlanId IN (" + 
								"						SELECT " + 
								"							sessionPlanId " + 
								"						FROM " + 
								"							acads.sessionplanid_timeboundid_mapping astm " + 
								"						WHERE " + 
								"							astm.timeboundId IN (" + 
								"								SELECT " + 
								"									lssc.id" + 
								"								FROM " + 
								"									lti.student_subject_config lssc 	" + 
								"								WHERE " + 
								"									CONCAT(lssc.batchId,'-',lssc.sequence) in (" + 
								"										SELECT " + 
								"											CONCAT(lssc.batchId,'-',lssc.sequence)  " + 
								"										FROM " + 
								"											lti.student_subject_config lssc " + 
								"										WHERE " + 
								"											lssc.id IN ( " + 
								"												SELECT " + 
								"													timeboundId " + 
								"												FROM " + 
								"													acads.sessionplanid_timeboundid_mapping " + 
								"												WHERE " + 
								"													sessionPlanId in ( " + 
								"														SELECT" + 
								"															asm.sessionPlanId  " + 
								"														FROM " + 
								"															acads.sessionplan_module asm  " + 
								"														WHERE" + 
								"															asm.id=? " + 
								"													) " + 
								"											) " + 
								"									)" + 
								"							)" + 
								"					)" + 
								"     		)";
					parameters.add(session.getSessionModuleNo());
				}else {
					sql = sql + " AND s.subject IN "
					+ "					(SELECT DISTINCT subjectname FROM "
					+ "						exam.mdm_subjectcode msc "
					+ "						INNER JOIN exam.mdm_subjectcode_mapping mscm ON msc.id = mscm.subjectCodeId AND mscm.active = 'Y' "
					+ "					 WHERE msc.active = 'Y' "
					+ "						AND CONCAT(consumerProgramStructureId, sem) IN "
					+ "							(SELECT DISTINCT "
					+ "								CONCAT(mscm2.consumerProgramStructureId, mscm2.sem) FROM "
					+ "									exam.mdm_subjectcode msc2 "
					+ " 								INNER JOIN exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId AND mscm2.active = 'Y' "
					+ "									INNER JOIN exam.consumer_program_structure cps ON mscm2.consumerProgramStructureId = cps.id "
					+ "									INNER JOIN exam.consumer_type ct ON cps.consumerTypeId = ct.id "
					+ "								WHERE msc2.subjectname = ? "
					+ "									AND msc2.active = 'Y'  "
					+ "									AND mscm2.consumerProgramStructureId NOT IN ('119' , '126') "
					+ "									AND ct.name = ? "
					+ "							)"
					+ "					) ";

					parameters.add(subject);
					parameters.add(consumerType);
				}
				
				if (StringUtils.isBlank(session.getHasModuleId())) {
					sql = sql + " AND (hasModuleId IS NULL OR hasModuleId = 'N') ";
				} else {
					sql = sql + " AND hasModuleId = ? ";
					parameters.add(session.getHasModuleId());
				}
				sql = sql + " ) ORDER BY c.date , sd.startTime ";
			}
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count;
			try {
				count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
				if(count == 0){
					return false;
				}else{
					return true;
				}
			} catch (DataAccessException e) {
				  
				return false;
			}
		}
		
		@Transactional(readOnly = true)
		public boolean isNoSubjectClashingV3(SessionDayTimeAcadsBean session) {
			String date = session.getDate();
			String year = session.getYear();
			String month = session.getMonth();
			String time = session.getStartTime();
			String subjectCode = session.getSubjectCode();
			String consumerType = session.getCorporateName();
			String isAdditionalSession = session.getIsAdditionalSession();
			
			if("Y".equalsIgnoreCase(isAdditionalSession)){
				//Additional session can be held along with same semester subjects
				return true;
			}
			
			if ("".equalsIgnoreCase(consumerType) || "All".equalsIgnoreCase(consumerType)) {
				consumerType = "Retail";
			}
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(year);
			parameters.add(month);
			parameters.add(date);
			parameters.add(time);

			String sqlForCorporateName = "";
			String corporateName = session.getCorporateName();
			if ("".equalsIgnoreCase(corporateName) || "All".equalsIgnoreCase(corporateName)) {
				sqlForCorporateName = " AND (corporateName = '' OR corporateName = 'All') ";
			}else{
				sqlForCorporateName = " AND corporateName = ? ";
				parameters.add(corporateName);
			}
			
			String track = session.getTrack();
			String track_check_sql =  " "; 
			if (!StringUtils.isBlank(track)) {
				track_check_sql = " AND (track = '' OR track = ? ) "; 
				parameters.add(track);
			}
			
			String sql = 
				  " SELECT COUNT(*) FROM "
				+ " 	acads.sessions s "
				+ " WHERE "
				+ " 	s.year = ? AND s.month = ? "
				+ "		AND (isCancelled <> 'Y' OR isCancelled IS NULL) "
				+ "		AND date = ? AND startTime = ? "
				+ 		sqlForCorporateName
				+ 		track_check_sql;
				
				if("Y".equalsIgnoreCase(session.getHasModuleId())) {
					sql = sql + " AND s.moduleId IN (" + 
								"		SELECT " + 
								"			aspm.id " + 
								"				FROM " + 
								"					acads.sessionplan_module aspm" + 
								"				WHERE " + 
								"					aspm.sessionPlanId IN (" + 
								"						SELECT " + 
								"							sessionPlanId " + 
								"						FROM " + 
								"							acads.sessionplanid_timeboundid_mapping astm " + 
								"						WHERE " + 
								"							astm.timeboundId IN (" + 
								"								SELECT " + 
								"									lssc.id" + 
								"								FROM " + 
								"									lti.student_subject_config lssc 	" + 
								"								WHERE " + 
								"									CONCAT(lssc.batchId,'-',lssc.sequence) in (" + 
								"										SELECT " + 
								"											CONCAT(lssc.batchId,'-',lssc.sequence)  " + 
								"										FROM " + 
								"											lti.student_subject_config lssc " + 
								"										WHERE " + 
								"											lssc.id IN ( " + 
								"												SELECT " + 
								"													timeboundId " + 
								"												FROM " + 
								"													acads.sessionplanid_timeboundid_mapping " + 
								"												WHERE " + 
								"													sessionPlanId in ( " + 
								"														SELECT" + 
								"															asm.sessionPlanId  " + 
								"														FROM " + 
								"															acads.sessionplan_module asm  " + 
								"														WHERE" + 
								"															asm.id=? " + 
								"													) " + 
								"											) " + 
								"									)" + 
								"							)" + 
								"					)" + 
								"     		)";
					parameters.add(session.getSessionModuleNo());
				}else {
					sql = sql + 
						" AND s.subject IN (SELECT DISTINCT "
					  + "	subject "
					  + " FROM "
					  + "	acads.sessions s "
					  + "		INNER JOIN "
					  + "	acads.session_subject_mapping ssm ON s.id = ssm.sessionId "
					  + "		INNER JOIN "
					  + "	exam.mdm_subjectcode_mapping scm ON ssm.subjectCodeId = scm.subjectCodeId "
					  + "		AND ssm.consumerProgramStructureId = scm.consumerProgramStructureId "
					  + " WHERE "
					  + "	date = ? AND startTime = ? "
					  + "		AND CONCAT(scm.consumerProgramStructureId, '-', scm.sem) IN (SELECT "
					  + "			CONCAT(consumerProgramStructureId, '-', sem) "
					  + "		FROM "
					  + "			exam.mdm_subjectcode sc "
					  + "				INNER JOIN "
					  + "			exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId "
					  + "		WHERE "
					  + "			subjectcode = ? "
					  + "				AND scm.active = 'Y' "
					  + "				AND (studentType = 'Regular' OR studentType IS NULL) "
					  + "				AND scm.consumerProgramStructureId NOT IN ('119', '126'))) ";

					parameters.add(date);
					parameters.add(time);
					parameters.add(subjectCode);					
				}
				
				if (StringUtils.isBlank(session.getHasModuleId())) {
					sql = sql + " AND (hasModuleId IS NULL OR hasModuleId = 'N') ";
				} else {
					sql = sql + " AND hasModuleId = ? ";
					parameters.add(session.getHasModuleId());
				}
				sql = sql + " ORDER BY s.date , s.startTime ";
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count;
//			System.out.println("isNoSubjectClashingV3 sql "+sql);
			try {
				count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
				if(count == 0){
					return true;
				}else{
					return false;
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Transactional(readOnly = true)
		public boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrackV2(SessionDayTimeAcadsBean session){

			int subject_limit = 2;
			String year = session.getYear();
			String date = session.getDate();
			String track = session.getTrack();
			String month = session.getMonth();
			String subject = session.getSubject();
			String consumer_type = session.getCorporateName();
			String isAdditionalSession = session.getIsAdditionalSession();
			
			if("Y".equalsIgnoreCase(isAdditionalSession)){
				//Additional session can be held along with same semester subjects
				return true;
			}
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql =" SELECT COUNT(*) FROM acads.calendar c WHERE " + 
						"    c.date = ? AND c.date NOT IN (SELECT s.date FROM acads.sessions s " + 
						"    		INNER JOIN exam.mdm_subjectcode msc ON s.subject = msc.subjectname " + 
						"           INNER JOIN exam.mdm_subjectcode_mapping mscm ON msc.id = mscm.subjectCodeId AND mscm.active = 'Y' " + 
						"   	WHERE " + 
						"            s.date = ? " +
						"		AND year = ? AND month = ? ";
			parameters.add(date);
			parameters.add(date);
			parameters.add(year);
			parameters.add(month);
			
			
			if(StringUtils.isBlank(session.getHasModuleId())) {
				sql = sql +" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
			}else {
				sql = sql +" AND s.hasModuleId = ? ";
				parameters.add(session.getHasModuleId());
			}
			
			//Added to check session is cancelled or Not
			sql = sql + " AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";
			
			if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type) ) {
				sql = sql + " AND (corporateName = '' OR corporateName = 'All') ";
			}else{
				sql = sql + " AND corporateName = ? ";
				parameters.add(consumer_type);
			}
			
			sql = sql + " AND msc.active = 'Y' "
					  + " AND mscm.consumerProgramStructureId IN "
					  + " (SELECT DISTINCT CONCAT(mscm2.consumerProgramStructureId) "
					  + " 	FROM "
					  + " 		exam.mdm_subjectcode msc2 "
					  + "		INNER JOIN exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId AND mscm2.active = 'Y' "  
					  + "		INNER JOIN exam.consumer_program_structure cps ON mscm2.consumerProgramStructureId = cps.id "
					  + "		INNER JOIN exam.consumer_type ct ON cps.consumerTypeId = ct.id " 
					  +	" 	WHERE "
					  + " 		msc2.active = 'Y' AND ct.name = ? "
					  + " ) ";
			
			//Check consumer_type
			if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type)) {
				consumer_type = "Retail";
			}
			parameters.add(consumer_type);
			
			//Check studentType Regular or TimeBound
			if(StringUtils.isBlank(session.getStudentType())){
			sql = sql + " AND (studentType IS NULL OR studentType = 'Regular') ";
	 		}else{
				sql = sql + " AND studentType = ? ";
	 			parameters.add(session.getStudentType());
	 		}
			
			//Check session Track
			if (!StringUtils.isBlank(track)) {
				sql = sql + " AND (s.track = '' OR s.track = ? ) "; 
				parameters.add(track);
			}else{
				sql = sql + " AND s.track = '' "; 	
			}
			
			//Checks For MBA Batch
			if("Y".equalsIgnoreCase(session.getHasModuleId())) {
				sql = sql +	"AND s.moduleId IN (" + 
							"	SELECT " + 
							"		aspm.id" + 
							"	FROM " + 
							"		acads.sessionplan_module aspm" + 
							"	WHERE " + 
							"		aspm.sessionPlanId IN (" + 
							"			SELECT " + 
							"				sessionPlanId " + 
							"			FROM " + 
							"				acads.sessionplanid_timeboundid_mapping  astm" + 
							"			WHERE" + 
							"				astm.timeboundId IN (" + 
							"					SELECT " + 
							"						lssc.id" + 
							"					FROM " + 
							"						lti.student_subject_config lssc 	" + 
							"					WHERE " + 
							"						CONCAT(lssc.batchId,'-',lssc.sequence) IN (" + 
							"							SELECT " + 
							"								CONCAT(lssc.batchId,'-',lssc.sequence)  " + 
							"							FROM " + 
							"								lti.student_subject_config lssc " + 
							"							WHERE " + 
							"								lssc.id IN ( " + 
							"									SELECT " + 
							"										timeboundId " + 
							"									FROM " + 
							"										acads.sessionplanid_timeboundid_mapping  " + 
							"									WHERE " + 
							"										sessionPlanId IN ( " + 
							"												SELECT" + 
							"													asm.sessionPlanId  " + 
							"												FROM " + 
							"													acads.sessionplan_module asm  " + 
							"												WHERE" + 
							"													asm.id = ? " + 
							"											) " + 
							"									) " + 
							"							)" + 
							"					)" + 
							"			)" + 
							"	)";
				parameters.add(session.getSessionModuleNo());
				sql = sql + " GROUP BY CONCAT(mscm.consumerProgramStructureId, mscm.sem) "
						  + " HAVING COUNT(CONCAT(mscm.consumerProgramStructureId, mscm.sem)) > 0) ";

			}else{
				sql = sql + " AND s.subject IN (SELECT subjectname " + 
							" 	FROM exam.mdm_subjectcode msc3  " + 
							"		INNER JOIN exam.mdm_subjectcode_mapping mscm3 ON msc3.id = mscm3.subjectCodeId AND mscm3.active = 'Y' " + 
							"	WHERE " + 
							" 		msC3.active = 'Y'" +
							"			AND CONCAT(consumerProgramStructureId, sem) IN (SELECT DISTINCT " + 
							"           	CONCAT(mscm4.consumerProgramStructureId, mscm4.sem) " + 
							"          	FROM " + 
							"           	exam.mdm_subjectcode msc4  " + 
							"				INNER JOIN exam.mdm_subjectcode_mapping mscm4 ON msc4.id = mscm4.subjectCodeId AND mscm4.active = 'Y' " + 
							"               INNER JOIN exam.consumer_program_structure cps ON mscm4.consumerProgramStructureId = cps.id " + 
							"               INNER JOIN exam.consumer_type ct ON cps.consumerTypeId = ct.id " + 
							"          	WHERE " + 
							"				msc4.active = 'Y' " +
							"           		AND msc4.subjectname = ? " + 
							"					AND ct.name = ? " ;
				parameters.add(subject);
				parameters.add(consumer_type);
				
				if(StringUtils.isBlank(session.getStudentType())){
					sql = sql + " 		AND (studentType IS NULL OR studentType = 'Regular') ) "
							  + "	AND (studentType IS NULL OR studentType = 'Regular') ) ";
				}else{
					sql = sql + " AND studentType = ? ) AND studentType = ? ) ";
	 				parameters.add(session.getStudentType());
	 				parameters.add(session.getStudentType());
				}
				
				sql = sql + " GROUP BY CONCAT(mscm.consumerProgramStructureId, mscm.sem) "
						  + " HAVING COUNT(CONCAT(mscm.consumerProgramStructureId, mscm.sem)) > ? ) ";
				parameters.add(subject_limit);
		}
		
		try {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(), Integer.class);
			
			if(count == 0){
				return false;
			}else{
				return true;
			}	
			
		} catch (Exception e) {
			  
			return false;
		}
		
	}
		
		@Transactional(readOnly = true)
		public boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3(SessionDayTimeAcadsBean session){

			int subject_limit = 2;
			String year = session.getYear();
			String date = session.getDate();
			String track = session.getTrack();
			String month = session.getMonth();
			String subject = session.getSubject();
			String subjectCode = session.getSubjectCode();
			String consumer_type = session.getCorporateName();
			String isAdditionalSession = session.getIsAdditionalSession();
			
			if("Y".equalsIgnoreCase(isAdditionalSession)){
				//Additional session can be held along with same semester subjects
				return true;
			}
			
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql =" SELECT count(*) FROM " +
						" 	acads.sessions s " +
						"		INNER JOIN " +
						"	acads.session_subject_mapping ssm ON s.id = ssm.sessionId " +
						"		INNER JOIN " +
						"	exam.mdm_subjectcode_mapping mscm ON ssm.consumerProgramStructureId = mscm.consumerProgramStructureId " +
						"		INNER JOIN " +
						"	exam.mdm_subjectcode msc ON mscm.subjectCodeId = msc.id AND s.subject = msc.subjectname " +
						"		INNER JOIN " +
						"	exam.consumer_program_structure cps ON cps.id = mscm.consumerProgramStructureId " +
						"		INNER JOIN " +
						"	exam.program p ON p.id = cps.programId " +
						"		INNER JOIN " +
						"	exam.program_structure ps ON ps.id = cps.programStructureId " +
						"		INNER JOIN " +
						"	exam.consumer_type ct ON ct.id = cps.consumerTypeId " +
						" WHERE " +
						"  	s.date = ? " +
						"		AND year = ? AND month = ? ";
			parameters.add(date);
			parameters.add(year);
			parameters.add(month);
			
			if(StringUtils.isBlank(session.getHasModuleId())) {
				sql = sql +" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
			}else {
				sql = sql +" AND s.hasModuleId = ? ";
				parameters.add(session.getHasModuleId());
			}
			
			//Added to check session is cancelled or Not
			sql = sql + " AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";
			
			//Added to check consumer_type
			if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type) ) {
				sql = sql + " AND (corporateName = '' OR corporateName = 'All') ";
			}else{
				sql = sql + " AND corporateName = ? ";
				parameters.add(consumer_type);
			}
			
			sql = sql + " AND mscm.active = 'Y' AND msc.active = 'Y' "
					  + " AND CONCAT(mscm.sem, mscm.consumerProgramStructureId) IN (SELECT "
					  + " 	CONCAT(sem, consumerProgramStructureId) "
					  + " FROM "
					  + "	exam.mdm_subjectcode msc2 "
					  + "		INNER JOIN "
					  + "	exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId "
					  + " WHERE "
					  + "	mscm2.active = 'Y' AND msc2.active = 'Y' "
					  + "		AND subjectcode = ? ";
			parameters.add(subjectCode);
			
			//Added to check student type i.e. Regular or TimeBound
			if("Y".equalsIgnoreCase(session.getHasModuleId())) {
				sql = sql + " AND studentType = 'TimeBound' ";
			}else{
				sql = sql + " AND (studentType IS NULL OR studentType = 'Regular') ";
			}
					
			sql = sql + " 	AND CONCAT(sem, consumerProgramStructureId) IN (SELECT "
		              + "    	CONCAT(mscm3.sem, mscm3.consumerProgramStructureId) AS subjectCount "
		              + " 	FROM "
		              + "     	acads.session_subject_mapping ssm3 "
		              + "       	INNER JOIN "
		              + "      acads.sessions s3 ON ssm3.sessionId = s3.id "
		              + "          INNER JOIN "
		              + "      exam.mdm_subjectcode_mapping mscm3 ON ssm3.consumerProgramStructureId = mscm3.consumerProgramStructureId "
		              + "          INNER JOIN "
		              + "      exam.mdm_subjectcode msc3 ON mscm3.subjectCodeId = msc3.id "
		              + "          AND s3.subject = msc3.subjectname "
		              + "          AND mscm3.active = 'Y'  AND msc3.active = 'Y' "
		              + "          AND s3.date = ? ";
			parameters.add(date);
					
			//Check session Track
			if (!StringUtils.isBlank(track)) {
				sql = sql + " AND (track = '' OR track = ? ) "; 
				parameters.add(track);
			}else{
				sql = sql + " AND track = '' "; 	
			}
			
			//Checks For MBA Batch
			if("Y".equalsIgnoreCase(session.getHasModuleId())) {
				sql = sql +	"AND s3.moduleId IN (" + 
							"	SELECT " + 
							"		aspm.id" + 
							"	FROM " + 
							"		acads.sessionplan_module aspm" + 
							"	WHERE " + 
							"		aspm.sessionPlanId IN (" + 
							"			SELECT " + 
							"				sessionPlanId " + 
							"			FROM " + 
							"				acads.sessionplanid_timeboundid_mapping  astm" + 
							"			WHERE" + 
							"				astm.timeboundId IN (" + 
							"					SELECT " + 
							"						lssc.id" + 
							"					FROM " + 
							"						lti.student_subject_config lssc 	" + 
							"					WHERE " + 
							"						CONCAT(lssc.batchId,'-',lssc.sequence) IN (" + 
							"							SELECT " + 
							"								CONCAT(lssc.batchId,'-',lssc.sequence)  " + 
							"							FROM " + 
							"								lti.student_subject_config lssc " + 
							"							WHERE " + 
							"								lssc.id IN ( " + 
							"									SELECT " + 
							"										timeboundId " + 
							"									FROM " + 
							"										acads.sessionplanid_timeboundid_mapping  " + 
							"									WHERE " + 
							"										sessionPlanId IN ( " + 
							"												SELECT" + 
							"													asm.sessionPlanId  " + 
							"												FROM " + 
							"													acads.sessionplan_module asm  " + 
							"												WHERE" + 
							"													asm.id = ? " + 
							"											) " + 
							"									) " + 
							"							)" + 
							"					)" + 
							"			)" + 
							"	)";
				parameters.add(session.getSessionModuleNo());
				sql = sql + " GROUP BY mscm3.sem , mscm3.consumerProgramStructureId "
						  + " HAVING COUNT(subjectCount) > 0)) ";

			}else{
				sql = sql + " GROUP BY mscm3.sem , mscm3.consumerProgramStructureId "
						  + " HAVING COUNT(subjectCount) > ? )) ";
				parameters.add(subject_limit);
				
				if (!StringUtils.isBlank(track)) {
					sql = sql + " AND (track = '' OR track = ? ) "; 
					parameters.add(track);
				}else{
					sql = sql + " AND track = '' "; 	
				}
		}
		
		try {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(), Integer.class);
			if(count == 0){
				return true;
			}else{
				return false;
			}	
			
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3 "+e.getMessage());
			return false;
		}
	}
		
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getMoreThan3ClashSessionsV2(SessionDayTimeAcadsBean session){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionDayTimeAcadsBean> clashingSessions = new ArrayList<SessionDayTimeAcadsBean>();
		String studentType = " AND (studentType = 'Regular' OR studentType IS NULL) ";
		if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
			studentType = " AND studentType = 'TimeBound' ";
		}
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql =" SELECT " + 
					"    s.date, s.startTime, s.subject, s.sessionName,  s.facultyId, s.track, mscm.sem, " + 
					"    p.code AS program, ps.program_structure AS programStructure, ct.name AS consumerType, " + 
					"    CONCAT(mscm.sem, mscm.consumerProgramStructureId) AS uniqueSemSubject " + 
					" FROM acads.sessions s " + 
					"      	INNER JOIN 	acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"		INNER JOIN 	exam.mdm_subjectcode_mapping mscm ON ssm.consumerProgramStructureId = mscm.consumerProgramStructureId " + 
					"		INNER JOIN 	exam.mdm_subjectcode msc on mscm.subjectCodeId = msc.id AND s.subject = msc.subjectname " + 
					"     	INNER JOIN  exam.consumer_program_structure cps ON cps.id = mscm.consumerProgramStructureId " + 
					"       INNER JOIN  exam.program p ON p.id = cps.programId " + 
					"       INNER JOIN  exam.program_structure ps ON ps.id = cps.programStructureId " + 
					"       INNER JOIN  exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
					" WHERE " + 
					"    s.date = ? " + 
					"        AND mscm.active = 'Y' and msc.active = 'Y' " + 
					"        AND CONCAT(mscm.sem, mscm.consumerProgramStructureId) IN (SELECT  " + 
					"            CONCAT(sem, consumerProgramStructureId) " + 
					"        FROM " + 
					"           	exam.mdm_subjectcode msc2 " + 
					"			INNER JOIN exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId " + 
					"                WHERE " + 
					"                	mscm2.active = 'Y' and msc2.active = 'Y' " + 
					"					AND subjectname = ? " + 
					 					studentType+ 
					"                	AND CONCAT(sem, consumerProgramStructureId) IN (SELECT  " + 
					"                    	CONCAT(mscm3.sem, mscm3.consumerProgramStructureId) AS subjectCount " + 
					"                	FROM " + 
					"                    	acads.session_subject_mapping ssm3 " + 
					"                       	INNER JOIN acads.sessions s3 ON ssm3.sessionId = s3.id " + 
					"                        	INNER JOIN exam.mdm_subjectcode_mapping mscm3 ON ssm3.consumerProgramStructureId  = mscm3.consumerProgramStructureId " + 
					"                        	INNER JOIN exam.mdm_subjectcode msc3 ON mscm3.subjectCodeId = msc3.id and s3.subject = msc3.subjectname " + 
					"								AND mscm3.active = 'Y' and msc3.active = 'Y' " + 
					"                        AND s3.date = ? ";
		
					parameters.add(session.getDate());
					parameters.add(session.getSubject());
					parameters.add(session.getDate());
					
					if (!StringUtils.isBlank(session.getTrack())) {
					sql =sql+ "  AND (track = '' OR track = ?) "; 
					parameters.add(session.getTrack());
					}
					sql =sql+" GROUP BY mscm3.sem , mscm3.consumerProgramStructureId ";
					
					if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
						sql = sql + " HAVING COUNT(subjectCount) > 0)) "; 
					}else{
						sql = sql + " HAVING COUNT(subjectCount) > 2)) ";
					}
					if (!StringUtils.isBlank(session.getTrack())) {
					sql = sql + " AND (track = '' OR track = ? ) ";
					parameters.add(session.getTrack());
					}
							
					sql = sql +	" ORDER BY uniqueSemSubject";
		
		try {
			clashingSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return clashingSessions;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsByCPSIdV2(StudentAcadsBean student, String year, String month, ArrayList<String> subjects) {
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
	     namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String sql =" SELECT s.*,f.*,ssm.*, s.id AS id FROM " +
					"		acads.sessions s " + 
					"	 		INNER JOIN " + 
					"		acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"    		INNER JOIN " +
					"		exam.mdm_subjectcode msc ON ssm.subjectCodeId = msc.id AND s.subject = msc.subjectname " + 
					"    		INNER JOIN " +
					"		exam.mdm_subjectcode_mapping mscm ON ssm.consumerProgramStructureId = mscm.consumerProgramStructureId AND msc.id = mscm.subjectCodeId " + 
					"    		INNER JOIN " +
					"		acads.faculty f ON s.facultyId = f.facultyId " + 
					"    		INNER JOIN "+
					"		exam.examorder eo ON s.month = eo.acadMonth  AND s.year = eo.year " + 
					"       	AND (s.isCommon = 'N' OR s.isCommon IS NULL) " + 
					"        	AND mscm.consumerProgramStructureId = :cps " + 
					"        	AND mscm.sem = :sem " +
					"		  	AND msc.subjectName in (:subjects) ";
		
		if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql = sql + " AND s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
						" INNER JOIN  acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
						" INNER JOIN acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
						" WHERE tum.userId = :userId and tum.role='Student') GROUP BY s.id " ;
		}else{
			sql = sql + " AND eo.order = (SELECT o.order FROM exam.examorder o WHERE acadMonth = :month AND year = :year AND acadSessionLive = 'Y') " + 
						" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
		}
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("subjects", subjects);
		queryParams.addValue("userId", student.getSapid());
		queryParams.addValue("month", month);
		queryParams.addValue("year", year);
	    queryParams.addValue("sem", student.getSem());
	    queryParams.addValue("cps", student.getConsumerProgramStructureId());
		
		try {
//			scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]
//									{student.getConsumerProgramStructureId(),student.getSem()}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, queryParams,
	                new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getUpcomingScheduledSessionForStudentsByCPSIdV2(StudentAcadsBean student, ArrayList<String> subjects) {
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		   namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
	
		
		String sql =" SELECT  " + 
					"    s.*, f.*, s.id AS id " + 
					" FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode msc ON ssm.subjectCodeId = msc.id " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode_mapping mscm ON ssm.consumerProgramStructureId = mscm.consumerProgramStructureId " + 
					"        AND msc.id = mscm.subjectCodeId " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON s.facultyId = f.facultyId " + 
					"        INNER JOIN " + 
					"    exam.examorder eo ON s.month = eo.acadmonth AND s.year = eo.year " + 
					"        AND mscm.consumerProgramStructureId = :cps " + 
					"        AND mscm.sem = :sem " + 
					"		 AND msc.subjectName in (:subjects) " +
					"        AND CONCAT(date, ' ', endTime) >= SYSDATE() ";
		
		if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql = sql + " AND s.hasModuleId = 'Y' and s.moduleId in (SELECT sm.id FROM lti.timebound_user_mapping  tum " + 
						" 		INNER JOIN  " +
						" acads.sessionplanid_timeboundid_mapping stm on tum.timebound_subject_config_id = stm.timeboundId " + 
						" 		INNER JOIN " +
						" acads.sessionplan_module sm on sm.sessionPlanId = stm.sessionPlanId " + 
						" 		WHERE tum.userId = :userId and tum.role='Student') GROUP BY s.id " ;
		}else {
			sql = sql + " AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder  WHERE  acadSessionLive = 'Y') " +
						" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
		}

		sql = sql + " ORDER BY date , startTime ASC LIMIT 10 ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("subjects", subjects);
		queryParams.addValue("userId", student.getSapid());
	    queryParams.addValue("sem", student.getSem());
	    queryParams.addValue("cps", student.getConsumerProgramStructureId());
		
		try {

			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, queryParams,
	                new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public SessionDayTimeAcadsBean findScheduledSessionBySessionId(String id) {
		String sql =" SELECT s.*, f.firstName,f.lastName, subjectcode FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON s.facultyId = f.facultyId " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id " + 
					" WHERE s.id = ? GROUP BY s.subject " ;

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeAcadsBean session = null;
		try {
			session = (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return session;
	}
	
	@Transactional(readOnly = true)
	public Map<String,String> getsubjectCodeMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT subjectcode, subjectname AS subject FROM exam.mdm_subjectcode ORDER BY subjectcode ";
		ArrayList<SessionDayTimeAcadsBean> subjectCodeList= (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		LinkedHashMap<String,String> subjectCodeMap = new LinkedHashMap<String,String>();
		for(SessionDayTimeAcadsBean bean : subjectCodeList){
			subjectCodeMap.put(bean.getSubjectCode(), bean.getSubject());
		}

		return subjectCodeMap;
	}
	
	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> getConsumerProgramStructureSubjectCodeData(){
		
		List<SessionDayTimeAcadsBean> consumerProgramStructureList = new ArrayList<SessionDayTimeAcadsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= " SELECT DISTINCT " + 
					"    mscm.id," + 
					"    p.code AS program," + 
					"    ps.program_structure AS programStructure," + 
					"    ct.name AS consumerType, " +
					"	 msc.subjectcode, " +
					"	 msc.subjectname AS subject " +
					" FROM " + 
					"    exam.consumer_program_structure cps " + 
					"        INNER JOIN " + 
					"    exam.program p ON cps.programId = p.id " + 
					"        INNER JOIN " + 
					"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
					"        INNER JOIN " + 
					"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
					"        INNER JOIN " + 
					"	 exam.mdm_subjectcode_mapping mscm ON cps.id = mscm.consumerProgramStructureId " +
					"		 INNER JOIN " +
					"	 exam.mdm_subjectcode msc ON mscm.subjectCodeId = msc.id " +
					" 	 	 WHERE msc.active= 'Y' " +
					" ORDER BY ct.name , ps.program_structure , p.code  ";			
		
		try {
			consumerProgramStructureList = (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}catch(Exception e){
			   
		}
		return consumerProgramStructureList;
	}
	
	@Transactional(readOnly = true)
	public String getSubjectNameBySubCodeMappingId(String pssIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql =" SELECT subjectname FROM exam.mdm_subjectcode_mapping msm " + 
						" INNER JOIN  exam.mdm_subjectcode ms ON msm.subjectCodeId = ms.id " + 
						" WHERE msm.id IN ("+pssIds+") " + 
						" GROUP BY subjectname ";
			String subjectName = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);
			return subjectName;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public String getSubjectCodeBySubjectName(SessionDayTimeAcadsBean session){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCode = "";
		try {
			String sql =  " SELECT sc.subjectcode FROM exam.mdm_subjectcode sc "
						+ " INNER JOIN mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId "
						+ " WHERE subjectname = ? ";
			if (!StringUtils.isBlank(session.getModuleId())) {
				sql += " AND sc.studentType = 'TimeBound' AND scm.consumerProgramStructureId in "
					 + " (111,131,151,154,155,156,157,158,142,143,144,145,146,147,148,149 )  ";
			}
			sql += " GROUP BY sc.subjectcode ";
			subjectCode = (String) jdbcTemplate.queryForObject(sql,new Object[]{session.getSessionplanSubject()},String.class);
			
		} catch (Exception e) {
			  
		}
		return subjectCode;
	}
	
	@Transactional(readOnly = true)
	public PageAcads<SessionDayTimeAcadsBean> getScheduledSessionPageFromHistory(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean, String searchOption) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql =" SELECT  " + 
					"    s.*, " + 
					//"    f.*, " + 
					"	f.facultyId,f.location,f.firstName,f.lastName, "+
					"	 s.id AS id, " +
					"	 s.facultyId AS facultyId, " +
					"	 scm.sem, " +
					"	 sc.subjectcode, " +
					"    ssm.consumerProgramStructureId, " + 
					"    ct.name AS consumerType, " + 
					"    p.code AS program, " + 
					"    ps.program_structure AS programStructure ";
		
		String countSql = "";
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " , COUNT(DISTINCT CONCAT(s.subject, s.sessionName, ssm.consumerProgramStructureId)) AS count, " + 
						"   CONCAT(s.date,s.startTime,s.subject,s.sessionName,s.facultyId,IFNULL(s.track, ''), IFNULL(s.moduleid, ''), IFNULL(s.isCancelled, ''), sc.subjectCode) AS distinctSession, " + 
						"   GROUP_CONCAT(distinct ssm.consumerProgramStructureId) consumerProgramStructureId ";
			
			countSql = countSql + " SELECT COUNT(DISTINCT CONCAT(s.date,s.startTime,s.subject,s.sessionName,s.facultyId,IFNULL(s.track, ''), IFNULL(s.moduleid, ''), IFNULL(s.isCancelled, ''), sc.subjectCode)) ";
		}else {
			countSql = countSql + " SELECT COUNT(DISTINCT CONCAT(sessionId, ssm.consumerProgramStructureId)) ";
									
		}
		
		sql = sql + " FROM " + 
				"    acads.sessions_history s " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON s.facultyId = f.facultyId OR s.altFacultyId = f.facultyId OR s.altFacultyId2 = f.facultyId OR s.altFacultyId3 = f.facultyId " + 
				"        INNER JOIN " + 
				"    acads.session_subject_mapping_history ssm ON s.id = ssm.sessionId " + 
				"        INNER JOIN " + 
				"    exam.consumer_program_structure cps ON cps.id = ssm.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"        INNER JOIN " + 
				"    exam.program p ON p.id = cps.programId " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " +
//				" 	 	 INNER JOIN  exam.program_sem_subject pss ON pss.consumerProgramStructureId = cps.id and s.subject = pss.subject " + 
				"	 	 INNER JOIN " +
				"	 exam.mdm_subjectcode_mapping scm ON cps.id = scm.consumerProgramStructureId " +
				"	 	 INNER JOIN " +
				"	  exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id AND s.subject = sc.subjectname "+
				"	 WHERE 1 = 1 ";	
		
		countSql = countSql + " FROM " + 
				"    acads.sessions_history s " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON s.facultyId = f.facultyId OR s.altFacultyId = f.facultyId OR s.altFacultyId2 = f.facultyId OR s.altFacultyId3 = f.facultyId " +
				"        INNER JOIN " + 
				"    acads.session_subject_mapping_history ssm ON s.id = ssm.sessionId " + 
				"        INNER JOIN " + 
				"    exam.consumer_program_structure cps ON cps.id = ssm.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"        INNER JOIN " + 
				"    exam.program p ON p.id = cps.programId " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " +
//				" 	 	 INNER JOIN exam.program_sem_subject pss ON pss.consumerProgramStructureId = cps.id and s.subject = pss.subject " +
				"	 	 INNER JOIN " +
				"	 exam.mdm_subjectcode_mapping scm ON cps.id = scm.consumerProgramStructureId " +
				"	 	 INNER JOIN " +
				"	  exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id AND s.subject = sc.subjectname "+
				"	WHERE 1 = 1 ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and (s.facultyId like ? OR s.altFacultyId like ? OR s.altFacultyId2 like ? OR s.altFacultyId3 like ?) ";
			countSql = countSql + " and (s.facultyId like ? OR s.altFacultyId like ? OR s.altFacultyId2 like ? OR s.altFacultyId3 like ?) ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getSessionName() != null &&   !("".equals(searchBean.getSessionName()))){
			sql = sql + " and sessionName like  ? ";
			countSql = countSql + " and sessionName like  ? ";
			parameters.add("%"+searchBean.getSessionName()+"%");
		}

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and s.subject = ? ";
			countSql = countSql + " and s.subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}
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

		if( searchBean.getCorporateName() != null &&   !("".equals(searchBean.getCorporateName()))){
			sql = sql + " and s.corporateName = ? ";
			countSql = countSql + " and s.corporateName = ? ";
			parameters.add(searchBean.getCorporateName());
		}
		
		if( searchBean.getFacultyLocation() != null &&   !("".equals(searchBean.getFacultyLocation()))){
			sql = sql + " and s.facultyLocation like  ? ";
			countSql = countSql + " and s.facultyLocation like  ? ";
			parameters.add("%"+searchBean.getFacultyLocation()+"%");
		}
		
		if( searchBean.getSubjectCode() != null &&   !("".equals(searchBean.getSubjectCode()))){
			sql = sql + " and sc.subjectcode = ? ";
			countSql = countSql + " and sc.subjectcode = ? ";
			parameters.add(searchBean.getSubjectCode());
		}
		
		if (searchBean.getConsumerProgramStructureId() != null
				&& !("".equals(searchBean.getConsumerProgramStructureId()))) {
			sql = sql + " and ssm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			countSql = countSql + " and ssm.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			
		}
		
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " group by distinctSession ";
		}else {
			sql = sql + " group by concat(sessionId, ssm.consumerProgramStructureId) ";
		}

		sql = sql + " order by date, startTime asc";

		Object[] args = parameters.toArray();

		PaginationHelper<SessionDayTimeAcadsBean> pagingHelper = new PaginationHelper<SessionDayTimeAcadsBean>();
		PageAcads<SessionDayTimeAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramListBySessionIdFromHistory(String id) {
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT  " + 
					"    GROUP_CONCAT(p.code SEPARATOR ', ') AS program, sem, program_structure AS prgmStructApplicable" + 
					" FROM " + 
					"    acads.session_subject_mapping_history ssm " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON ssm.consumerProgramStructureId = pss.consumerProgramStructureId " + 
					"        AND ssm.program_sem_subject_id = pss.id " + 
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps ON pss.consumerProgramStructureId = cps.id " + 
					"        INNER JOIN " + 
					"    exam.program p ON cps.programId = p.id " + 
					"        INNER JOIN " + 
					"    exam.program_structure ps ON cps.programStructureId = ps.id " + 
					" WHERE " + 
					"    sessionId = ? " + 
					" GROUP BY subject , sem , program_structure " + 
					" ORDER BY subject , program_structure ";
		try {
			subjectProgramList = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new Object[]{id}, 
					new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		return subjectProgramList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getCommonGroupProgramListFromHistory(SessionDayTimeAcadsBean sessionBean) {
		try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    s.*, " + 
				"    p.code AS program, " + 
				"    p_s.program_structure AS programStructure, " + 
				"    c_t.name AS consumerType, " + 
				" 	 consumerProgramStructureId " + 
				" FROM " + 
				"    acads.sessions_history s " + 
				"        LEFT JOIN " + 
				"    acads.session_subject_mapping_history ssm ON ssm.sessionId = s.id " + 
				"        LEFT JOIN " + 
				"    exam.consumer_program_structure AS c_p_s ON c_p_s.id = ssm.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    exam.program AS p ON p.id = c_p_s.programId " + 
				"        LEFT JOIN " + 
				"    exam.program_structure AS p_s ON p_s.id = c_p_s.programStructureId " + 
				"        LEFT JOIN " + 
				"    exam.consumer_type AS c_t ON c_t.id = c_p_s.consumerTypeId " + 
				" WHERE 1 = 1 " +
				"		AND s.year = ? AND s.month = ? " + 
				"		AND s.date = ? AND s.startTime = ? " +
				"       AND subject = ? "
				
				+ "and consumerProgramStructureId in ("+ sessionBean.getConsumerProgramStructureId() +")";
		 
		return (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] 
					{sessionBean.getYear(),sessionBean.getMonth(),sessionBean.getDate(), sessionBean.getStartTime(), sessionBean.getSubject()},
				new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}
		catch(Exception e) {
			   
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getAllClashingSubjectsList(SessionDayTimeAcadsBean session){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionDayTimeAcadsBean> sessionList = new ArrayList<SessionDayTimeAcadsBean>();
		
		String sqlForstudentType = " AND (studentType = 'Regular' or studentType is null) ";
		if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
			sqlForstudentType = " AND studentType = 'TimeBound' ";
		}
		
		String sqlForCorporateName = "";
		if ("".equalsIgnoreCase(session.getCorporateName()) || "All".equalsIgnoreCase(session.getCorporateName()) ) {
			sqlForCorporateName = sqlForCorporateName + " AND (corporateName = '' OR corporateName = 'All')  ";
		}else{
			sqlForCorporateName = sqlForCorporateName + " AND corporateName = '" +session.getCorporateName()+ "' ";
		}
		
		String sql =" SELECT s.*, IFNULL(sc.specializationType, '') AS specializationType FROM acads.sessions s " + 
					"		INNER JOIN " +
					"	acads.session_subject_mapping ssm ON s.id = ssm.sessionId " +
					"		INNER JOIN " +	
					"	exam.mdm_subjectcode sc ON ssm.subjectCodeId = sc.id " +
					"        WHERE " + 
					"            s.year = ? AND s.month = ? " + 
									 sqlForCorporateName +
					"				 AND (isCancelled <> 'Y' OR isCancelled IS NULL) " + 
					"                AND date = ? " + 
					"                AND s.subject IN (SELECT DISTINCT subject FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode_mapping scm ON ssm.subjectCodeId = scm.subjectCodeId " + 
					"        AND ssm.consumerProgramStructureId = scm.consumerProgramStructureId " + 
					" WHERE " + 
					"    date = ? AND startTime = ? " + 
					"        AND CONCAT(scm.consumerProgramStructureId,'-',scm.sem) IN (SELECT  " + 
					"            CONCAT(consumerProgramStructureId, '-', sem) " + 
					"        FROM " + 
					"            exam.mdm_subjectcode sc " + 
					"                INNER JOIN " + 
					"            exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId " + 
					"        WHERE " + 
					"            subjectcode = ? AND scm.active = 'Y' "
					+ "				AND scm.consumerProgramStructureId NOT IN ('119' , '126') " + sqlForstudentType +
					"		)) ";
					if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
						sql = sql + " AND hasModuleId = 'Y' ";
					}else{
						sql = sql + " AND (hasModuleId IS NULL OR hasModuleId = 'N') ";
					}
					
					sql = sql + " GROUP BY s.id ";
					
		sessionList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {session.getYear(), session.getMonth(), session.getDate(), 
						session.getDate(), session.getStartTime(), session.getSubjectCode()}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return sessionList;
		
	}
	
	@Transactional(readOnly = true)
	public StudentAcadsBean getStudentRegistrationDetails(String sapId) {
		StudentAcadsBean studentRegistrationDetails = null;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			StudentAcadsBean studentForConsuemerTypeCheck = getSingleStudentsData(sapId);
			String sql = "";

			if("Diageo".equalsIgnoreCase(studentForConsuemerTypeCheck.getConsumerType())) {
				
				 sql = 	  "	Select re.*, s.consumerType from exam.registration re , exam.students s, exam.examorder eo  "
				 		+ " where re.sapid = ? and s.sapid = re.sapid "
						+ " and eo.order = (select max(eeo.order) from exam.examorder eeo where eeo.acadSessionLive = 'Y') "
						+ " and re.month = eo.acadMonth "
						+ " and re.year = eo.year"
						+ " and re.sem = (select max(r2.sem) "
						+ "					from exam.registration r2 "
						+ "					where r2.sapid = re.sapid "
						+ "					and r2.program = re.program  ) ";
			
			}else if (TIMEBOUND_PORTAL_LIST.contains(studentForConsuemerTypeCheck.getConsumerProgramStructureId())){
				
				sql = " SELECT * FROM exam.registration WHERE sapid = ? ORDER BY sem DESC LIMIT 1 ";
				
			}else {
				sql = 	" SELECT r.*, s.consumerType, s.consumerProgramStructureId FROM exam.registration r " + 
						" INNER JOIN exam.students s ON r.sapid = s.sapid " + 
						" WHERE r.sapid = ? " + 
						" ORDER BY sem DESC LIMIT 1";
			}
			
			studentRegistrationDetails = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentAcadsBean .class));
		} catch (Exception e) {
			  
		}
		return studentRegistrationDetails;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsByCPSIdV3(StudentAcadsBean student, String year, String month, List<Integer> currentSemPSSId) {
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String pssIdsCommaSeparated = "''";
		pssIdsCommaSeparated = StringUtils.join(currentSemPSSId, ",");;
//		pssIdsCommaSeparated = String.join(",", currentSemPSSId);
		
		String sql =" SELECT s.*, f.firstName, f.lastName, s.id AS id, ssm.program_sem_subject_id AS prgmSemSubId FROM " +
					"		acads.sessions s " + 
					"	 		INNER JOIN " + 
					"		acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"    		INNER JOIN " +
					"		acads.faculty f ON s.facultyId = f.facultyId " + 
					"       	AND (s.isCommon = 'N' OR s.isCommon IS NULL) " + 
					"		  	AND ssm.program_sem_subject_id IN ("+pssIdsCommaSeparated+") ";
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql = sql + " 		AND s.hasModuleId = 'Y' and s.moduleId IN " +
						"			(SELECT sm.id FROM lti.timebound_user_mapping tum " + 
						" 		INNER JOIN  " +
						"	acads.sessionplanid_timeboundid_mapping stm ON tum.timebound_subject_config_id = stm.timeboundId " + 
						" 		INNER JOIN acads.sessionplan_module sm ON sm.sessionPlanId = stm.sessionPlanId " + 
						" 	WHERE tum.userId = " + student.getSapid() + " and tum.role='Student') GROUP BY s.id " ;
		}else{
			sql = sql + " AND s.year = '"+year+"' AND s.month = '"+month+"' " + 
						" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
		}
		
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]{}, 
									new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return scheduledSessionList;
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getScheduledSessionForStudentsByCPSIdV3(String year, String month, ArrayList<String> currentSemPSSId) {
		
	     namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String sql =" SELECT  " + 
					"    qs.*, qs.program_sem_subject_id AS prgmSemSubId " + 
					" FROM " + 
					"    acads.quick_sessions qs " + 
					"        WHERE " + 
					"  	 program_sem_subject_id IN  (:currentSemPSSId) " + 
					"        AND CONCAT(date, ' ', endTime) >= SYSDATE() " +
					"		 AND year = :year AND month = :month " +
					" ORDER BY date , startTime ASC ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("currentSemPSSId", currentSemPSSId);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month", month);
	
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, queryParams,
	                new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
	
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> viewScheduleSessionByPSSId(String pssId,String year,String month,String track){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select s.* from acads.sessions s inner join acads.session_subject_mapping ssm ON ssm.sessionId = s.id "
					+ "where (s.isCancelled IS NULL or s.isCancelled <> 'Y') and s.year = ? and s.month = ? ";
			if(track != null && !"".equalsIgnoreCase(track)) {
				sql = sql + " and s.track = '" + track + "' ";
			}
			sql = sql + " and ssm.program_sem_subject_id = ? order by s.date asc;";
			return jdbcTemplate.query(sql,new Object[] {year,month,pssId},new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getAllScheduledSessionsFromQuickSessions(String year, String month, List<Integer> currentSemPSSId) {
		
		 namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		
		
		String sql =" SELECT  " + 
					"    qs.*, qs.program_sem_subject_id AS prgmSemSubId " + 
					" FROM " + 
					"    acads.quick_sessions qs " + 
					"        WHERE " + 
					"  	 program_sem_subject_id IN  (:currentSemPSSId) " + 
					"		 AND year = :year AND month = :month " ;
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("currentSemPSSId", currentSemPSSId);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month", month);
		
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
//			scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]
//									{year, month}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
//			scheduledSessionList = (ArrayList<SessionDayTimeBean>) jdbcTemplate.query(sql, new PreparedStatementSetter() {
//				public void setValues(PreparedStatement ps) throws SQLException {
//					ps.setString(1, year);
//					ps.setString(2, month);
//				}}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			
			scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, queryParams,
	                new BeanPropertyRowMapper<SessionDayTimeAcadsBean>(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
	
		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getAllCommonSessionsFromCommonQuickSessions(String consumerProgramStructureId, 
				String year, String month, String sem) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT " + 
					"	 qcs.* " + 
					" FROM " + 
					"    acads.quick_common_sessions qcs " + 
					"        WHERE " + 
					"  	 consumerProgramStructureId = ? " + 
					"		 AND year = ? AND month = ? " +
					"		 AND sem = ? ";
		
		ArrayList<SessionDayTimeAcadsBean> commonSessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
//			commonSessionsList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]
//							{consumerProgramStructureId, year, month, sem}, new BeanPropertyRowMapper(SessionDayTimeBean.class));
			commonSessionsList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,  new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, consumerProgramStructureId);
					ps.setString(2, year);
					ps.setString(3, month);
					ps.setString(4, sem);
		        }}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
	
		return commonSessionsList;
	}
	
	public HashMap<String,Integer> getMapOfFacultyIdAndRemainingSeatsV2(SessionDayTimeAcadsBean session) {

		HashMap<String, Integer> sessionAttendanceMap = new LinkedHashMap<>();

		sessionAttendanceMap.put(session.getFacultyId(), getAttendanceByMeetingKey(session.getMeetingKey()));
		
		if(StringUtils.isNotBlank(session.getAltFacultyId()) && !StringUtils.isBlank(session.getAltMeetingKey())) {
			sessionAttendanceMap.put(session.getAltFacultyId(), getAttendanceByMeetingKey(session.getAltMeetingKey()));
		}
		
		if(StringUtils.isNotBlank(session.getAltFacultyId2()) && !StringUtils.isBlank(session.getAltMeetingKey2())) {
			sessionAttendanceMap.put(session.getAltFacultyId2(), getAttendanceByMeetingKey(session.getAltMeetingKey2()));
		}
		
		if(StringUtils.isNotBlank(session.getAltFacultyId3()) && !StringUtils.isBlank(session.getAltMeetingKey3())) {
			sessionAttendanceMap.put(session.getAltFacultyId3(), getAttendanceByMeetingKey(session.getAltMeetingKey3()));
		}

		return sessionAttendanceMap;
	}
	
	@Transactional(readOnly = false)
	public int getAttendanceByMeetingKey(String meetingKey) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		int remainingSeats = 0;
		String sql =  " SELECT remainingSeats FROM "
					+ "		acads.session_attendance_counter "
					+ " WHERE "
					+ "		meetingkey = ? ";
		try {
			remainingSeats = jdbcTemplate.queryForObject(sql, new Object[]{meetingKey}, Integer.class);
		} catch (Exception e) {
//			  
		}
		return remainingSeats;
	}

	@Transactional(readOnly = true)
 	public ArrayList<SessionDayTimeAcadsBean> getUpcomingCommonSessionsFromCommonQuickSessions(String consumerProgramStructureId, 
			String year, String month, String sem) {
	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT " + 
					"	 qcs.* " + 
					" FROM " + 
					"    acads.quick_common_sessions qcs " + 
					"        WHERE " + 
					"  	 consumerProgramStructureId = ? " + 
					"		 AND year = ? AND month = ? " +
					"		 AND sem = ? " +
					"		 AND qcs.date >= CURDATE() ";
		
		ArrayList<SessionDayTimeAcadsBean> commonSessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			commonSessionsList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql.toString(), new Object[]
							{consumerProgramStructureId, year, month, sem}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		} catch (Exception e) {
			  
		}
	
		return commonSessionsList;
	}
 	
	@Transactional(readOnly = true)
 	public String getSubjectCodeBySessionPlanId(int sessionPlanId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCode = "";
		try {
			String sql =" SELECT  " + 
						"    subjectcode " + 
						" FROM " + 
						"    acads.sessionplanid_timeboundid_mapping stm " + 
						"        INNER JOIN " + 
						"    lti.student_subject_config ssc ON stm.timeboundId = ssc.id " + 
						"        INNER JOIN " + 
						"    exam.mdm_subjectcode_mapping scm ON ssc.prgm_sem_subj_id = scm.id " + 
						"        INNER JOIN " + 
						"    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id " + 
						" WHERE " + 
						"    stm.sessionPlanId = ? ";
			subjectCode = (String) jdbcTemplate.queryForObject(sql,new Object[]{sessionPlanId},String.class);
			
		} catch (Exception e) {
			  
		}
		return subjectCode;
	}
 	
	@Transactional(readOnly = true)
 	public ExamOrderAcadsBean getExamOrderByYearMonth(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  "SELECT * FROM exam.examorder WHERE acadSessionLive = 'Y' AND year = ? AND acadMonth = ? ";
		ExamOrderAcadsBean order = (ExamOrderAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{year, month}, new BeanPropertyRowMapper(ExamOrderAcadsBean .class));
		return order;
	}

	@Transactional(readOnly = false)
	public ArrayList<SessionDayTimeAcadsBean> getAllPSSSessionsMapping(long primaryKey){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionDayTimeAcadsBean> allSessions = new ArrayList<SessionDayTimeAcadsBean>();
		String sql =" SELECT  " + 
					"    s.id, program_sem_subject_id AS prgmSemSubId, " + 
					"    date, startTime, day, " + 
					"    subject, sessionName, s.month, s.year, s.facultyId, endTime,  " + 
					"    track, f.firstName, f.lastName, s.isCancelled, s.reasonForCancellation, s.sessionType " + 
					" FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssc ON s.id = ssc.sessionId " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON s.facultyId = f.facultyId " + 
					"    AND s.id = ? ";
		try {
			allSessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new Object[] {primaryKey }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		}catch(Exception e){
			loggerForSessionScheduling.info("Error in getAllPSSSessionsMapping "+e.getMessage());
		}
		return allSessions;
	}
	
	@Transactional(readOnly = false)
	public Boolean insertQuickSession(final ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds, final long sessionId) {
		
		String sql =" INSERT INTO acads.quick_sessions (id, program_sem_subject_id, date, startTime, day, subject, " +
				 	" sessionName, month, year, facultyId, endTime, track, firstName, lastName, isCancelled, reasonForCancellation, sessionType ) " + 
					" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		
		try {
			int[] batchInsertQuicksession = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
	
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					SessionDayTimeAcadsBean session = sessionWithPSSIds.get(i);
					
//					session.setSessionType(session.getSessionType().replace("1", "Webinar"));
//                  session.setSessionType(session.getSessionType().replace("2", "Meeting"));
					
					ps.setLong(1, sessionId);
					ps.setInt(2, session.getPrgmSemSubId());
					ps.setString(3, session.getDate());
					ps.setString(4, session.getStartTime());
					ps.setString(5, session.getDay());
					ps.setString(6, session.getSubject());
					ps.setString(7, session.getSessionName());
					ps.setString(8, session.getMonth());
					ps.setString(9, session.getYear());
					ps.setString(10, session.getFacultyId());
					ps.setString(11, session.getEndTime());
					ps.setString(12, session.getTrack());
					ps.setString(13, session.getFirstName());
					ps.setString(14, session.getLastName());
					ps.setString(15, session.getIsCancelled());
					ps.setString(16, session.getReasonForCancellation());
					ps.setString(17, session.getSessionType());
				}
				
				@Override
				public int getBatchSize() {
					return sessionWithPSSIds.size();
				}
				
			});
			return true;
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in insertQuickSession "+e.getMessage());
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public void updateQuickSessionName(String sessionId, String sessionName) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE acads.quick_sessions SET sessionName = ? WHERE id = ? ";
		jdbcTemplate.update(sql, new Object[] {sessionName, sessionId});
	}
	
	@Transactional(readOnly = false)
	public boolean deleteQuickSessions (long sessionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "DELETE FROM acads.quick_sessions WHERE id = ? ";
			jdbcTemplate.update(sql, new Object[] {sessionId});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public void deleteQuickMappingByCPSId (String sessionId, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " DELETE FROM acads.quick_sessions WHERE id = ? AND program_sem_subject_id IN (" +
					 " SELECT program_sem_subject_id FROM acads.session_subject_mapping WHERE " +
					 " sessionId = ? and consumerProgramStructureId In (" +consumerProgramStructureId+ "))";
		jdbcTemplate.update(sql, new Object[] {sessionId, sessionId});
	}
	
	@Transactional(readOnly = false)
	public Boolean insertNewSessionAfterCancellation(final SessionDayTimeAcadsBean existingSession, SessionDayTimeAcadsBean session, final String userId) {
		
		if (StringUtils.isBlank(existingSession.getHasModuleId())) {
			existingSession.setHasModuleId("N");
		}
		
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql =" INSERT INTO acads.sessions (date, startTime, endTime, day, subject, sessionName, createdBy, createdDate, "
						+ " lastModifiedBy, lastModifiedDate, year, month, facultyId, meetingKey, meetingPwd, ciscoStatus, "
						+ " joinUrl, hostUrl, hostKey, room, hostId, hostPassword, isCommon, corporateName, programList, "
						+ " facultyLocation, track, hasModuleId, moduleid ) "
						+ " VALUES (?,?,ADDTIME(?, '02:00:00'),DAYNAME(date),?,?,?,sysdate(), "
						+ " ?, sysdate(), ?,?,?,?,?,?, "
						+ " ?,?,?,?,?,?,?,?,?, "
						+ " ?,?,?,? ) ";
		
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		String oldSessionId = existingSession.getId();
		boolean toCommit = Boolean.FALSE;
		long newSessionId = 0;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			startTransaction("sessionCancellation");
			
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					statement.setString(1,existingSession.getDate());
					statement.setString(2,existingSession.getStartTime());
					statement.setString(3,existingSession.getStartTime());
					statement.setString(4,existingSession.getSubject());
					statement.setString(5,existingSession.getSessionName());
					statement.setString(6,userId);
					statement.setString(7,userId);
					statement.setString(8,existingSession.getYear());
					statement.setString(9,existingSession.getMonth());
					statement.setString(10,existingSession.getFacultyId());
					statement.setString(11,existingSession.getMeetingKey());
					statement.setString(12,existingSession.getMeetingPwd());
					statement.setString(13,existingSession.getCiscoStatus());
					statement.setString(14,existingSession.getJoinUrl());
					statement.setString(15,existingSession.getHostUrl());
					statement.setString(16,existingSession.getHostKey());
					statement.setString(17,existingSession.getRoom());
					statement.setString(18,existingSession.getHostId());
					statement.setString(19,existingSession.getHostPassword());
					statement.setString(20,existingSession.getIsCommon());
					statement.setString(21,existingSession.getCorporateName());
					statement.setString(22,existingSession.getProgramList());
					statement.setString(23,existingSession.getFacultyLocation());
					statement.setString(24,existingSession.getTrack());
					statement.setString(25,existingSession.getHasModuleId());
					statement.setString(26,existingSession.getModuleId());
					
					return statement;
				}
				
			},holder);
			
			newSessionId = holder.getKey().longValue();
			
			//Set new id
			session.setId(String.valueOf(newSessionId));
			
			//Update Into Session table
			updateCancelledSession(session, userId);
			
			//Delete old mapping from Quick_Session
			deleteQuickMappingByCPSId(oldSessionId, consumerProgramStructureId);
			
			//Update old mapping with new session Id
			updateSessionSubjectMapping(newSessionId, userId, oldSessionId, consumerProgramStructureId);
			
			//Insert new SessionId in Quick_Session
			ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds = getAllPSSSessionsMapping(newSessionId);
			boolean isInserted = insertQuickSession(sessionWithPSSIds, newSessionId);
			if (isInserted) {
				toCommit = true;
			}
			
		} catch (Exception e) {
			  
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
	}
	
	@Transactional(readOnly = false)
	public boolean updateSessionAfterCancellation(SessionDayTimeAcadsBean session, String userId) {
		
		boolean toCommit = Boolean.FALSE;
		long sessionId = Long.parseLong(session.getId());
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		
		try {
			startTransaction("updateSessionAfterCancellation");
			
			//Update Into Session table
			updateCancelledSession(session, userId);
			
			//Delete old mapping from Quick_Session
			deleteQuickMappingByCPSId(session.getId(), consumerProgramStructureId);
			
			//Insert new SessionId in Quick_Session
			ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds = getAllPSSSessionsMapping(sessionId);
			boolean isInserted = insertQuickSession(sessionWithPSSIds, sessionId);
			if (isInserted) {
				toCommit = true;
			}
		} catch (Exception e) {
			  
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
	}
	
	public boolean insertNewSessionAfterSessionUpdate(final SessionDayTimeAcadsBean session, final String userId, String oldSessionId) {
		loggerForSessionScheduling.info("In insertNewSessionAfterSessionUpdate");
		boolean isInserted = insertNewSessionAfterCRUD(session, userId, oldSessionId, "insertNewSessionAfterSessionUpdate");
		return isInserted;
	}

	public boolean insertNewSessionAfterSessionNameUpdate(final SessionDayTimeAcadsBean session, final String userId, String oldSessionId) {
		loggerForSessionScheduling.info("In insertNewSessionAfterSessionNameUpdate");
		boolean isInserted = insertNewSessionAfterCRUD(session, userId, oldSessionId, "insertNewSessionAfterSessionNameUpdate");
		return isInserted;
	}

	public boolean insertNewSessionAfterFacultyUpdate(final SessionDayTimeAcadsBean session, final String userId, String oldSessionId) {
		loggerForSessionScheduling.info("In insertNewSessionAfterFacultyUpdate");
		boolean isInserted = insertNewSessionAfterCRUD(session, userId, oldSessionId, "insertNewSessionAfterFacultyUpdate");
		return isInserted;
	}

	public boolean insertNewSessionAfterTrackUpdate(final SessionDayTimeAcadsBean session, final String userId, String oldSessionId) {
		loggerForSessionScheduling.info("In insertNewSessionAfterTrackUpdate");
		boolean isInserted = insertNewSessionAfterCRUD(session, userId, oldSessionId, "insertNewSessionAfterTrackUpdate");
		return isInserted;
	}
	
	@Transactional(readOnly = false)
	public boolean insertNewSessionAfterCRUD(final SessionDayTimeAcadsBean session, final String userId, String oldSessionId, String transactionName) {
		
		if (StringUtils.isBlank(session.getHasModuleId())) {
			session.setHasModuleId("N");
		}
		
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql =" INSERT INTO acads.sessions (date, startTime, endTime, day, subject, sessionName, createdBy, createdDate, "
						+ " lastModifiedBy, lastModifiedDate, year, month, facultyId, meetingKey, meetingPwd, ciscoStatus, "
						+ " joinUrl, hostUrl, hostKey, room, hostId, hostPassword, isCommon, corporateName, programList, "
						+ " facultyLocation, track, hasModuleId, moduleid ) "
						+ " VALUES (?,?,ADDTIME(?, '02:00:00'),(Select dayName from acads.calendar where date = ? ),?,?,?,sysdate(), "
						+ " ?, sysdate(), ?,?,?,?,?,?, "
						+ " ?,?,?,?,?,?,?,?,?, "
						+ " ?,?,?,? ) ";
		
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		boolean toCommit = Boolean.FALSE;
		long newSessionId = 0;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			startTransaction(transactionName);
			
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					statement.setString(1,session.getDate());
					statement.setString(2,session.getStartTime());
					statement.setString(3,session.getStartTime());
					statement.setString(4,session.getDate());
					statement.setString(5,session.getSubject());
					statement.setString(6,session.getSessionName());
					statement.setString(7,userId);
					statement.setString(8,userId);
					statement.setString(9,session.getYear());
					statement.setString(10,session.getMonth());
					statement.setString(11,session.getFacultyId());
					statement.setString(12,session.getMeetingKey());
					statement.setString(13,session.getMeetingPwd());
					statement.setString(14,session.getCiscoStatus());
					statement.setString(15,session.getJoinUrl());
					statement.setString(16,session.getHostUrl());
					statement.setString(17,session.getHostKey());
					statement.setString(18,session.getRoom());
					statement.setString(19,session.getHostId());
					statement.setString(20,session.getHostPassword());
					statement.setString(21,session.getIsCommon());
					statement.setString(22,session.getCorporateName());
					statement.setString(23,session.getProgramList());
					statement.setString(24,session.getFacultyLocation());
					statement.setString(25,session.getTrack());
					statement.setString(26,session.getHasModuleId());
					statement.setString(27,session.getModuleId());
					
					return statement;
				}
				
			},holder);
			
			newSessionId = holder.getKey().longValue();
			loggerForSessionScheduling.info("In insertNewSessionAfterCRUD newSessionId "+newSessionId);
			
			//Set new id
			session.setId(String.valueOf(newSessionId));
			
			if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
				zoomManger.scheduleWebinarBatchJob(session);
			}
			
			if (!session.isErrorRecord()) {
				String dbUpdateError = conferenceBookingDAO.updateZoomDetails(session, "0");
				if (dbUpdateError != null) {
					toCommit = false;
					return toCommit;
				}
			} else {
				toCommit = false;
				return toCommit;
			}
			
			//Delete old mapping from Quick_Session
			loggerForSessionScheduling.info("Calling deleteQuickMappingByCPSId oldSessionId: "+oldSessionId+" consumerProgramStructureId: "+consumerProgramStructureId);
			deleteQuickMappingByCPSId(oldSessionId, consumerProgramStructureId);
			
			//Update old mapping with new session Id
			loggerForSessionScheduling.info("Calling updateSessionSubjectMapping");
			updateSessionSubjectMapping(newSessionId, userId, oldSessionId, consumerProgramStructureId);
			
			//Insert new SessionId in Quick_Session
			ArrayList<SessionDayTimeAcadsBean> sessionWithPSSIds = getAllPSSSessionsMapping(newSessionId);
			boolean isInserted = insertQuickSession(sessionWithPSSIds, newSessionId);
			if (isInserted) {
				toCommit = true;
			}
			
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error insertNewSessionAfterCRUD : "+e.getMessage());
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
	}
	
	@Transactional(readOnly = false)
	public void deleteScheduledSessionBySessionId(String id) {
		String sql = "DELETE FROM acads.sessions WHERE id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { id});
	}
	
	@Transactional(readOnly = false)
	public void deleteSessionSubjectMappings(String sessionId, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" DELETE FROM acads.session_subject_mapping WHERE sessionId = ? " + 
					" AND consumerProgramStructureId IN ( "+consumerProgramStructureId+" )";
		jdbcTemplate.update(sql, new Object[] {sessionId});
	}
	
	@Transactional(readOnly = false)
	public boolean deleteSessionMappings(String sessionId, String consumerProgramStructureId, String forAll) {
		
		loggerForSessionScheduling.info("In deleteSessionMappings sessionId: "+sessionId+" consumerProgramStructureId: "+consumerProgramStructureId+" forAll: "+forAll);
		boolean toCommit = Boolean.FALSE;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			startTransaction("deleteSessionMappings");
		
		//If Deleted whole session then delete session from Sessions
		if ("Y".equalsIgnoreCase(forAll)) {
			deleteScheduledSessionBySessionId(sessionId);
		}
		
		//Delete old mapping from Quick_Session
		deleteQuickMappingByCPSId(sessionId, consumerProgramStructureId);
				
		//Delete mapping from Session Mapping
		deleteSessionSubjectMappings(sessionId, consumerProgramStructureId);
		
		toCommit = true;
		
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error In deleteSessionMappings : "+e.getMessage());
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
		
	}
	
	@Transactional(readOnly = false)
	public List<Integer> getConsumerProgramStructureId(String programList, String corporateName) {
		
		programList = "'" + programList.replace(",", "', '") + "'";
		
		if (StringUtils.isBlank(corporateName) || corporateName.equalsIgnoreCase("Retail")) {
			corporateName = "'6'";
		}else if ("All".equalsIgnoreCase(corporateName)) {
			corporateName = "'6','8','2','9'";
		}else if ("BAJAJ".equalsIgnoreCase(corporateName)) {
			corporateName = "'1'";
		}else if ("CIPLA".equalsIgnoreCase(corporateName)) {
			corporateName = "'2'";
		}else if ("Diageo".equalsIgnoreCase(corporateName)) {
			corporateName = "'3'";
		}else if ("EMERSON".equalsIgnoreCase(corporateName)) {
			corporateName = "'5'";
		}else if ("SAS".equalsIgnoreCase(corporateName)) {
			corporateName = "'7'";
		}else if ("Verizon".equalsIgnoreCase(corporateName)) {
			corporateName = "'8'";
		}else if ("Vertiv".equalsIgnoreCase(corporateName)) {
			corporateName = "'9'";
		}else if ("Concentrix".equalsIgnoreCase(corporateName)) {
			corporateName = "'10'";
		}else if ("Multiplier".equalsIgnoreCase(corporateName)) {
			corporateName = "'11'";
		}
			
		String sql =" SELECT id FROM " + 
					"    exam.consumer_program_structure " + 
					" WHERE " + 
					"    programId IN (SELECT id FROM exam.program WHERE code IN (" +programList+ "))" +
					"	 AND consumerTypeId IN (" +corporateName+ ")";
		List<Integer> consumerProgramStructureIdList = jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(Integer.class));
		return consumerProgramStructureIdList;
	}
	
	@Transactional(readOnly = false)
	public SessionDayTimeAcadsBean getSessionByIdNew(long primaryKey){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= " select s.id, date, startTime, day, subject, sessionName, month, year, sem, s.facultyId, endTime, track, " + 
					" f.firstName, f.lastName, s.sessionType from acads.sessions s " + 
					" inner join acads.faculty f on s.facultyId = f.facultyId " + 
					" where s.id = ? ";
		try {
			SessionDayTimeAcadsBean sessionDetails = (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {primaryKey}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return sessionDetails;
		} catch (Exception e) {
			  
			return null;
		}
	}
	
	//insertCommonSession start
	@Transactional(readOnly = false)
	public boolean insertCommonSession(final SessionDayTimeAcadsBean bean) {
		
		if (StringUtils.isBlank(bean.getHasModuleId())){
            if ((!bean.getProgramList().contains("MBA - WX")) && (!bean.getProgramList().contains("M.Sc. (AI & ML Ops)"))) {
                bean.setHasModuleId("N");
            }
        }
		
		loggerForSessionScheduling.info("In insertCommonSession ProgramList: "+bean.getProgramList()+" and Sem: "+bean.getSem());
		
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		boolean toCommit = Boolean.FALSE;
		long primaryKey = 0;
		boolean isInserted = false;
		
		final String sql =" INSERT INTO acads.sessions ( "
						+ " year, month, date, startTime, endTime, day, subject, sessionName, "
						+ " room, hostId, hostPassword, facultyId, facultyLocation, "
						+ " ciscoStatus, meetingKey, meetingPwd, joinUrl, hostUrl, hostKey, "
						+ " isCommon, corporateName, "
						+ " altMeetingKey, altMeetingPwd, altFacultyId, altFacultyLocation, "
						+ " altHostId, altHostKey, altHostPassword, "
						+ " altMeetingKey2, altMeetingPwd2, altFacultyId2, altFaculty2Location, "
						+ " altHostId2, altHostKey2, altHostPassword2, "
						+ " altMeetingKey3, altMeetingPwd3, altFacultyId3, altFaculty3Location, "
						+ " altHostId3, altHostKey3, altHostPassword3, "
						+ " isCancelled, track, sem, "
						+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
						+ " programList, hasModuleId, moduleId, sessionType "
						+ " ) "
						+ " VALUES( ?,?,?,?,ADDTIME(?, '02:00:00'), (Select dayName from acads.calendar where date = ? ), ?,?,"
						+ " ?,?,?,?,?,"
						+ " ?,?,?,?,?,?,"
						+ " ?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,?,"
						+ " ?,?,?,"
						+ " ?,?,?,"
						+ " ?,sysdate(),?, sysdate(),"
						+ " ?,?,?,?"
						+ " ) ";

		
		try {
	
			startTransaction("insertCommonSession");
			
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					statement.setString(1,bean.getYear());
					statement.setString(2, bean.getMonth());
					statement.setString(3, bean.getDate());
					statement.setString(4, bean.getStartTime());
					statement.setString(5, bean.getStartTime());
					statement.setString(6, bean.getDate());
					statement.setString(7, bean.getSubject());
					statement.setString(8, bean.getSessionName());
					
					statement.setString(9, bean.getRoom());
					statement.setString(10, bean.getHostId());
					statement.setString(11, bean.getHostPassword());
					statement.setString(12, bean.getFacultyId());
					statement.setString(13, bean.getFacultyLocation());
					
					statement.setString(14, bean.getCiscoStatus());
					statement.setString(15, bean.getMeetingKey());
					statement.setString(16, bean.getMeetingPwd());
					statement.setString(17, bean.getJoinUrl());
					statement.setString(18, bean.getHostUrl());
					statement.setString(19, bean.getHostKey());
					
					statement.setString(20, bean.getIsCommon());
					statement.setString(21, bean.getCorporateName());
					
					statement.setString(22, bean.getAltMeetingKey());
					statement.setString(23, bean.getAltMeetingPwd());
					statement.setString(24, bean.getAltFacultyId());
					statement.setString(25, bean.getAltFacultyLocation());
					statement.setString(26, bean.getAltHostId());
					statement.setString(27, bean.getAltHostKey());
					statement.setString(28, bean.getAltHostPassword());
					
					statement.setString(29, bean.getAltMeetingKey2());
					statement.setString(30, bean.getAltMeetingPwd2());
					statement.setString(31, bean.getAltFacultyId2());
					statement.setString(32, bean.getAltFaculty2Location());
					statement.setString(33, bean.getAltHostId2());
					statement.setString(34, bean.getAltHostKey2());
					statement.setString(35, bean.getAltHostPassword2());
					
					statement.setString(36, bean.getAltMeetingKey3());
					statement.setString(37, bean.getAltMeetingPwd3());
					statement.setString(38, bean.getAltFacultyId3());
					statement.setString(39, bean.getAltFaculty3Location());
					statement.setString(40, bean.getAltHostId3());
					statement.setString(41, bean.getAltHostKey3());
					statement.setString(42, bean.getAltHostPassword3());
					
					statement.setString(43, bean.getIsCancelled());
					statement.setString(44, bean.getTrack());
					statement.setString(45, bean.getSem());
					
					statement.setString(46, bean.getCreatedBy());
					statement.setString(47, bean.getCreatedBy());
					statement.setString(48, bean.getProgramList());
					statement.setString(49, bean.getHasModuleId());
					statement.setString(50, bean.getSessionModuleNo());
					statement.setString(51, bean.getSessionType());
					
					return statement;
				}
				
			}, holder);

			primaryKey = holder.getKey().longValue();

			loggerForSessionScheduling.info("In insertCommonSession got primary Key: "+primaryKey);
			
			List<Integer> consumerProgramStructureIdList = getConsumerProgramStructureId(bean.getProgramList(), bean.getCorporateName());
			loggerForSessionScheduling.info("In insertCommonSession got consumerProgramStructureIdList: "+consumerProgramStructureIdList);
			
			ArrayList<SessionDayTimeAcadsBean> sessionWithCPSIds = new ArrayList<SessionDayTimeAcadsBean>();
			for (Integer cpsId : consumerProgramStructureIdList) {
				
				SessionDayTimeAcadsBean newSession = getSessionByIdNew(primaryKey);
				newSession.setCreatedBy(bean.getCreatedBy());
				newSession.setLastModifiedBy(bean.getCreatedBy());
				newSession.setConsumerProgramStructureId(String.valueOf(cpsId));
				sessionWithCPSIds.add(newSession);
			}

			//Insert Quick Session Start
			isInserted = insertQuickCommonSession(sessionWithCPSIds, primaryKey);
			//Insert Quick Session End
			
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in insertCommonSession: "+e.getMessage());
			return false;
		} finally {
			toCommit = primaryKey != 0 && isInserted;
			endTransaction(toCommit);
		}

		return toCommit;
	}
	
	@Transactional(readOnly = false)
	public boolean insertQuickCommonSession(final ArrayList<SessionDayTimeAcadsBean> sessionWithCPSIds, long primaryKey) {
			
		String sql =" INSERT INTO acads.quick_common_sessions " +
				 	" (id, consumerProgramStructureId, date, startTime, day, subject, sessionName, " + 
					" month, year, sem, facultyId, endTime, track, firstName, lastName, sessionType, " +
					" createdBy, createdDate, lastModifiedBy, lastModifiedDate)  " + 
					" VALUES (?,?,?,?,(Select dayName from acads.calendar where date = ? ),?,?,?,?,?,?,?,?,?,?,?,?, sysdate(),?, sysdate()) ";
		
		try {
			int[] batchInsertQuickCommonSession = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
	
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					SessionDayTimeAcadsBean session = sessionWithCPSIds.get(i);
					
//					session.setSessionType(session.getSessionType().replace("1", "Webinar"));
//                  session.setSessionType(session.getSessionType().replace("2", "Meeting"));
					
					ps.setLong(1, primaryKey);
					ps.setString(2, session.getConsumerProgramStructureId());
					ps.setString(3, session.getDate());
					ps.setString(4, session.getStartTime());
					ps.setString(5, session.getDate());
					ps.setString(6, session.getSubject());
					ps.setString(7, session.getSessionName());
					ps.setString(8, session.getMonth());
					ps.setString(9, session.getYear());
					ps.setString(10, session.getSem());
					ps.setString(11, session.getFacultyId());
					ps.setString(12, session.getEndTime());
					ps.setString(13, session.getTrack());
					ps.setString(14, session.getFirstName());
					ps.setString(15, session.getLastName());
					ps.setString(16, session.getSessionType());
					ps.setString(17, session.getCreatedBy());
					ps.setString(18, session.getLastModifiedBy());
				}
				
				@Override
				public int getBatchSize() {
					return sessionWithCPSIds.size();
				}
				
			});
			return true;
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in insertQuickCommonSession: "+e.getMessage());
		}
		return false;
	}
	
	public boolean deleteCommonSession(String sessionId) {
		boolean toCommit = Boolean.FALSE;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			startTransaction("deleteCommonSession");
			
			deleteScheduledSessionBySessionId(sessionId);
			deleteCommonQuickMappingByCPSId(sessionId);
			
			toCommit = true;
		} catch (Exception e) {
			  
			toCommit = false;
		} finally {
			endTransaction(toCommit);
		}
		return toCommit;
	}
	
	@Transactional(readOnly = false)
	public void deleteCommonQuickMappingByCPSId (String sessionId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " DELETE from acads.quick_common_sessions where id = ? ";
		jdbcTemplate.update(sql, new Object[] {sessionId});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllSessionTime() {
		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = "select startTime from acads.session_days where year = 2021 and month = 'Jul' group by startTime ";
		String sql =" SELECT CONCAT(day, ' - ', startTime) FROM acads.session_days " +
					" GROUP BY CONCAT(day, ' - ', startTime) ORDER BY CONCAT(day, ' - ', startTime) ";
		
		ArrayList<String> sessionTimeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sessionTimeList;

	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getDateAndTimeForSesssionToBeSchedule(SessionDayTimeAcadsBean session){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<SessionDayTimeAcadsBean> sessionList = new ArrayList<SessionDayTimeAcadsBean>();
//		List<String> convertedTimeList = Arrays.asList(session.getStartTime().split(",", -1));
//		List<String> convertedDaysList = Arrays.asList(session.getDay().split(",", -1));
		List<String> convertedDaysStarttimeList = Arrays.asList(session.getDayTime().split(",", -1));
		String sql =" SELECT  " + 
					"    c.date, sd.startTime, DATE_ADD(startTime, INTERVAL 120 MINUTE) AS endTime, dayName AS day, year, month " + 
					" FROM " + 
					"    acads.session_days sd " + 
					"        INNER JOIN " + 
					"    acads.calendar c ON c.dayName = sd.day " + 
					" WHERE " + 
					"    sd.year = :year AND sd.month = :month " + 
					"        AND c.date BETWEEN :startDate AND :endDate " + 
//					"	   	 AND startTime IN ( :time ) AND dayName IN ( :days) " + 
					"		 AND CONCAT(day, ' - ', startTime) IN (:dayTime) " +
					" GROUP BY CONCAT(date, day, startTime) " + 
					" ORDER BY date , startTime ";
		
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("year", session.getYear());
		param.addValue("month", session.getMonth());
		param.addValue("startDate", session.getFromDate());
		param.addValue("endDate", session.getToDate());
//		param.addValue("time", convertedTimeList);
//		param.addValue("days", convertedDaysList);
		param.addValue("dayTime", convertedDaysStarttimeList);
		
		sessionList =  (ArrayList<SessionDayTimeAcadsBean>) namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return sessionList;
	}
	
	@Transactional(readOnly = true)
	public int getSessionTime(SessionDayTimeAcadsBean session) {
 		int sessionTime = 120;
		String sql = "";
		try {
			if(!StringUtils.isBlank(session.getSubjectCode())) {
				sql = "SELECT sessionTime FROM exam.mdm_subjectcode WHERE subjectcode = ? ";
				sessionTime = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getSubjectCode()},Integer.class);
			}else if(session.getHasModuleId().equalsIgnoreCase("Y")){
				sql = "SELECT sessionTime FROM exam.program_sem_subject WHERE studentType = 'TimeBound' LIMIT 1 ";
				sessionTime = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getSubject()},Integer.class);
			}else if(!StringUtils.isBlank(session.getMasterKey())){
				sql = " SELECT sessionTime FROM exam.mdm_subjectcode sc " +
					  " INNER JOIN exam.mdm_subjectcode_mapping msc ON sc.id = msc.subjectCodeId" +
					  " WHERE subjectname = ? AND msc.id in (" +session.getMasterKey()+ ") LIMIT 1 ";
				sessionTime = (int) jdbcTemplate.queryForObject(sql, new Object[]{session.getSubject()},Integer.class);
			}
		} catch (Exception e) {
			e.printStackTrace();  
		}
		return sessionTime;
	}

	@Transactional(readOnly = true)
	public ProgramSubjectMappingAcadsBean getcommonProgramList(String sessionId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= " SELECT REPLACE(programList, ',', ', ') AS program, sem FROM acads.sessions WHERE id = ? ";
		try {
			ProgramSubjectMappingAcadsBean sessionDetails = (ProgramSubjectMappingAcadsBean) jdbcTemplate.queryForObject(sql, 
					new Object[] {sessionId}, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
			return sessionDetails;
		} catch (Exception e) {
			  
			return null;
		}
	}
	
	@Transactional(readOnly = true)
    public ArrayList<String> getAllSessionTypes() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT sessionType FROM acads.session_types";
        ArrayList<String> sessionTypeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
        return sessionTypeList;
    }
    
    @Transactional(readOnly = true)
    public Map<String,String> getSessionTypesMap(){
        jdbcTemplate = new JdbcTemplate(dataSource);
        String sql= "SELECT * FROM acads.session_types ";
        ArrayList<SessionDayTimeAcadsBean> sessionTypesList= (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
        LinkedHashMap<String,String> sessionTypesMap = new LinkedHashMap<String,String>();
        for(SessionDayTimeAcadsBean bean : sessionTypesList){
                sessionTypesMap.put(bean.getId(), bean.getSessionType());
        }
        return sessionTypesMap;
    }
    
    @Transactional(readOnly = true)
	public String getspecializationTypeBySubjectCode(String subjectcode){
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql =" SELECT IFNULL(specializationType, '') FROM exam.mdm_subjectcode WHERE subjectcode = ? ";
			String specializationType = (String) jdbcTemplate.queryForObject(sql,new Object[]{subjectcode},String.class);
			return specializationType;
		} catch (Exception e) {
			return "";
		}
	}
    
    
    @Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getMoreThan3ClashSessionsV3(SessionDayTimeAcadsBean session){
    	ArrayList<SessionDayTimeAcadsBean> clashingList=new ArrayList<SessionDayTimeAcadsBean>();
		int subject_limit = 2;
		String year = session.getYear();
		String date = session.getDate();
		String track = session.getTrack();
		String month = session.getMonth();
		String subject = session.getSubject();
		String subjectCode = session.getSubjectCode();
		String consumer_type = session.getCorporateName();
		String isAdditionalSession = session.getIsAdditionalSession();
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql =" SELECT s.*, mscm.sem,  p.code AS program, ps.program_structure AS programStructure, ct.name AS consumerType, " +
					" CONCAT(mscm.sem, mscm.consumerProgramStructureId) AS uniqueSemSubject " +
					" 	FROM acads.sessions s " +
					"		INNER JOIN " +
					"	acads.session_subject_mapping ssm ON s.id = ssm.sessionId " +
					"		INNER JOIN " +
					"	exam.mdm_subjectcode_mapping mscm ON ssm.consumerProgramStructureId = mscm.consumerProgramStructureId " +
					"		INNER JOIN " +
					"	exam.mdm_subjectcode msc ON mscm.subjectCodeId = msc.id AND s.subject = msc.subjectname " +
					"		INNER JOIN " +
					"	exam.consumer_program_structure cps ON cps.id = mscm.consumerProgramStructureId " +
					"		INNER JOIN " +
					"	exam.program p ON p.id = cps.programId " +
					"		INNER JOIN " +
					"	exam.program_structure ps ON ps.id = cps.programStructureId " +
					"		INNER JOIN " +
					"	exam.consumer_type ct ON ct.id = cps.consumerTypeId " +
					" WHERE " +
					"  	s.date = ? " +
					"		AND year = ? AND month = ? ";
		parameters.add(date);
		parameters.add(year);
		parameters.add(month);
		
		if(StringUtils.isBlank(session.getHasModuleId())) {
			sql = sql +" AND (s.hasModuleId IS NULL OR s.hasModuleId <> 'Y') ";
		}else {
			sql = sql +" AND s.hasModuleId = ? ";
			parameters.add(session.getHasModuleId());
		}
		
		//Added to check session is cancelled or Not
		sql = sql + " AND (isCancelled <> 'Y' OR isCancelled IS NULL) ";
		
		//Added to check consumer_type
		if ("".equalsIgnoreCase(consumer_type) || "All".equalsIgnoreCase(consumer_type) ) {
			sql = sql + " AND (corporateName = '' OR corporateName = 'All') ";
		}else{
			sql = sql + " AND corporateName = ? ";
			parameters.add(consumer_type);
		}
		
		sql = sql + " AND mscm.active = 'Y' AND msc.active = 'Y' "
				  + " AND CONCAT(mscm.sem, mscm.consumerProgramStructureId) IN (SELECT "
				  + " 	CONCAT(sem, consumerProgramStructureId) "
				  + " FROM "
				  + "	exam.mdm_subjectcode msc2 "
				  + "		INNER JOIN "
				  + "	exam.mdm_subjectcode_mapping mscm2 ON msc2.id = mscm2.subjectCodeId "
				  + " WHERE "
				  + "	mscm2.active = 'Y' AND msc2.active = 'Y' "
				  + "		AND subjectcode = ? ";
		parameters.add(subjectCode);
		
		//Added to check student type i.e. Regular or TimeBound
		if("Y".equalsIgnoreCase(session.getHasModuleId())) {
			sql = sql + " AND studentType = 'TimeBound' ";
		}else{
			sql = sql + " AND (studentType IS NULL OR studentType = 'Regular') ";
		}
				
		sql = sql + " 	AND CONCAT(sem, consumerProgramStructureId) IN (SELECT "
	              + "    	CONCAT(mscm3.sem, mscm3.consumerProgramStructureId) AS subjectCount "
	              + " 	FROM "
	              + "     	acads.session_subject_mapping ssm3 "
	              + "       	INNER JOIN "
	              + "      acads.sessions s3 ON ssm3.sessionId = s3.id "
	              + "          INNER JOIN "
	              + "      exam.mdm_subjectcode_mapping mscm3 ON ssm3.consumerProgramStructureId = mscm3.consumerProgramStructureId "
	              + "          INNER JOIN "
	              + "      exam.mdm_subjectcode msc3 ON mscm3.subjectCodeId = msc3.id "
	              + "          AND s3.subject = msc3.subjectname "
	              + "          AND mscm3.active = 'Y'  AND msc3.active = 'Y' "
	              + "          AND s3.date = ? ";
		parameters.add(date);
				
		//Check session Track
		if (!StringUtils.isBlank(track)) {
			sql = sql + " AND (track = '' OR track = ? ) "; 
			parameters.add(track);
		}else{
			sql = sql + " AND track = '' "; 	
		}
		
		//Checks For MBA Batch
		if("Y".equalsIgnoreCase(session.getHasModuleId())) {
			sql = sql +	"AND s3.moduleId IN (" + 
						"	SELECT " + 
						"		aspm.id" + 
						"	FROM " + 
						"		acads.sessionplan_module aspm" + 
						"	WHERE " + 
						"		aspm.sessionPlanId IN (" + 
						"			SELECT " + 
						"				sessionPlanId " + 
						"			FROM " + 
						"				acads.sessionplanid_timeboundid_mapping  astm" + 
						"			WHERE" + 
						"				astm.timeboundId IN (" + 
						"					SELECT " + 
						"						lssc.id" + 
						"					FROM " + 
						"						lti.student_subject_config lssc 	" + 
						"					WHERE " + 
						"						CONCAT(lssc.batchId,'-',lssc.sequence) IN (" + 
						"							SELECT " + 
						"								CONCAT(lssc.batchId,'-',lssc.sequence)  " + 
						"							FROM " + 
						"								lti.student_subject_config lssc " + 
						"							WHERE " + 
						"								lssc.id IN ( " + 
						"									SELECT " + 
						"										timeboundId " + 
						"									FROM " + 
						"										acads.sessionplanid_timeboundid_mapping  " + 
						"									WHERE " + 
						"										sessionPlanId IN ( " + 
						"												SELECT" + 
						"													asm.sessionPlanId  " + 
						"												FROM " + 
						"													acads.sessionplan_module asm  " + 
						"												WHERE" + 
						"													asm.id = ? " + 
						"											) " + 
						"									) " + 
						"							)" + 
						"					)" + 
						"			)" + 
						"	)";
			parameters.add(session.getSessionModuleNo());
			sql = sql + " GROUP BY mscm3.sem , mscm3.consumerProgramStructureId "
					  + " HAVING COUNT(subjectCount) > 0)) ";

		}else{
			sql = sql + " GROUP BY mscm3.sem , mscm3.consumerProgramStructureId "
					  + " HAVING COUNT(subjectCount) > ? )) ";
			parameters.add(subject_limit);
			
			if (!StringUtils.isBlank(track)) {
				sql = sql + " AND (track = '' OR track = ? ) "; 
				parameters.add(track);
			}else{
				sql = sql + " AND track = '' "; 	
			}
		}
		
		sql = sql +	" ORDER BY uniqueSemSubject";
	
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			clashingList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			return clashingList;
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in getListMoreThanLimitSubjectsSameDayByProgSemTrackV3 "+e.getMessage());
			e.printStackTrace();
			return clashingList;
		}
    }

    @Transactional(readOnly = true)
	public Map<String,String> getsubjectCodeMapForReport(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql= "SELECT id, subjectcode, subjectname AS subject FROM exam.mdm_subjectcode ORDER BY subjectcode ";
		ArrayList<SessionDayTimeAcadsBean> subjectCodeList= (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		LinkedHashMap<String,String> subjectCodeMap = new LinkedHashMap<String,String>();
		for(SessionDayTimeAcadsBean bean : subjectCodeList){
			subjectCodeMap.put(bean.getId(),bean.getSubjectCode()+" (" +bean.getSubject()+")");
		}

		return subjectCodeMap;
	}
    
    @Transactional(readOnly = true)
	public ArrayList<SessionDayTimeAcadsBean> getModuleIdAndBatchNameList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =" SELECT smp.id, b.name AS batchName FROM acads.sessionplan_module smp " + 
					"        INNER JOIN " + 
					"    acads.sessionplanid_timeboundid_mapping stm ON smp.sessionPlanId = stm.sessionPlanId " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON stm.timeboundId = ssc.id " + 
					"        INNER JOIN " + 
					"    exam.batch b ON ssc.batchId = b.id ";
		
		ArrayList<SessionDayTimeAcadsBean> moduleIdAndBatchNameList = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return moduleIdAndBatchNameList;

	}
    
	@Transactional(readOnly = true)
	public ArrayList<String> getProgramIdListByConsumertypeId(String consumerType){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT programId FROM exam.consumer_program_structure WHERE consumerTypeId = ? ";
		return (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{consumerType}, new SingleColumnRowMapper(String.class)); 
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramBean> getProgramListByProgramId(ArrayList<String> listOfProgramId){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql ="SELECT code, name FROM exam.program WHERE id IN (:listOfProgramId) ORDER BY name ";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("listOfProgramId", listOfProgramId);
		return (ArrayList<ProgramBean>) namedParameterJdbcTemplate.query(sql,queryParams, new BeanPropertyRowMapper(ProgramBean.class)); 
	}

	@Transactional(readOnly = true)
    public ArrayList<SessionDayTimeAcadsBean> getAllsessionsDetails(){
    	jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT * FROM acads.sessions WHERE year IN (2022, 2023) ORDER BY date, startTime ASC");
		ArrayList<SessionDayTimeAcadsBean> allsessions = (ArrayList<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return allsessions;	
    }
    
    @Transactional(readOnly = true)
    public HashMap<String,Integer> getSubjectCodeDetails(){
    	jdbcTemplate = new JdbcTemplate(dataSource);
		
		StringBuffer sql = new StringBuffer("select sessionId,subjectCodeId from acads.session_subject_mapping ssm group by sessionId ");
		HashMap<String,Integer> allsessions = (HashMap<String,Integer>) jdbcTemplate.query(sql.toString(), new ResultSetExtractor<HashMap>(){
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Integer> mapRet = new HashMap<String,Integer>();
		        while(rs.next()){
		            mapRet.put(rs.getString("sessionId"),rs.getInt("subjectCodeId"));
		        }
		        return mapRet;
		    }
		});
		return allsessions;	
    }
    
	@Transactional(readOnly = true)
 	public List<Integer> getMasterKeyByProgramId(String programId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT id FROM exam.consumer_program_structure where programId = ? ";
		List<Integer> subjectCodes = (List<Integer>) jdbcTemplate.queryForList(sql,new Object[]{programId},Integer.class);
		return subjectCodes;
	}
	
	@Transactional(readOnly = true)
 	public List<Integer> getSubjectCodeIdBySem(String sem){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT subjectCodeId FROM exam.mdm_subjectcode_mapping where sem = ? ";
		List<Integer> subjectCodes = (List<Integer>) jdbcTemplate.queryForList(sql,new Object[]{sem},Integer.class);
		return subjectCodes;
	}
	
	@Transactional(readOnly = true)
 	public List<Integer> getSubjectCodeIdByMasterkey(List<Integer> masterkeys){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("masterkey", masterkeys);
		String sql ="SELECT subjectCodeId FROM exam.mdm_subjectcode_mapping where consumerProgramStructureId IN (:masterkey) ";
		List<Integer> subjectCodes = (List<Integer>) namedParameterJdbcTemplate.queryForList(sql,queryParams,Integer.class);
		return subjectCodes;
	}

	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> getsubjectCodeMapWithId(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= "SELECT id, subjectcode, subjectname AS subject FROM exam.mdm_subjectcode ORDER BY subjectcode;";
		List<SessionDayTimeAcadsBean> subjectCodeList= (List<SessionDayTimeAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return subjectCodeList;
	}
}