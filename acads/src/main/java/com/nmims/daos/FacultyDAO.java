package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FacultyUnavailabilityBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.TimetableAcadsBean;
import com.nmims.helpers.PaginationHelper;

import java.sql.ResultSet;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

@Repository("facultyDAO")
public class FacultyDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

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

	@Transactional(readOnly = true)
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
	public ArrayList<String> batchUpdateFacultyDates(final List<FacultyUnavailabilityBean> facultyDates) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < facultyDates.size(); i++) {
			try{
				FacultyUnavailabilityBean bean = facultyDates.get(i);
				upsertFacultyDates(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertFacultyCourseMapping(final List<FacultyCourseMappingBean> facultyCourseMapingList) {

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

	
	
	@Transactional(readOnly = false)
	private void upsertFacultyCourseMapping(FacultyCourseMappingBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO acads.facultyCourseMapping(year, month, subject, facultyIdPref1,facultyIdPref2,"
				+ " facultyIdPref3, session, duration, createdBy, createdDate, lastModifiedBy,"
				+ " lastModifiedDate  ) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "	    facultyIdPref1 = ?,"
				+ "	    facultyIdPref2 = ?,"
				+ "	    facultyIdPref3 = ?,"
				+ "	    session = ?,"
				+ "	    duration = ?,"
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

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				facultyIdPref1,
				facultyIdPref2,
				facultyIdPref3,
				session,
				duration,
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

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public PageAcads<FacultyUnavailabilityBean> getFacultyUnavailabilityDatesPage(int pageNo, int pageSize, FacultyUnavailabilityBean searchBean) {
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
		PageAcads<FacultyUnavailabilityBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyUnavailabilityBean.class));


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

	@Transactional(readOnly = false)
	public void deleteFacultyUnavailability(String id) {
		String sql = "Delete from acads.facultyUnavailabilityDates where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});

	}

	@Transactional(readOnly = false)
	public void deleteSessionDayTime(String id) {
		String sql = "Delete from acads.session_days where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});

	}


	@Transactional(readOnly = false)
	private void upsertFacultyDates(FacultyUnavailabilityBean bean, JdbcTemplate jdbcTemplate) {
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

	}


	@Transactional(readOnly = true)
	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
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
	public List<String> getProgramTypes(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT distinct(programtype) FROM exam.programs order by program asc";

		List<String> programTypeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));

		return programTypeList;
	}

	@Transactional(readOnly = true)
	public List<String> getAllProgramNames(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT distinct(programname) FROM exam.programs order by program asc";

		List<String> programNameList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));

		return programNameList;
	}

	@Transactional(readOnly = true)
	public List<String> getProgramNames(String type){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT distinct(programname) FROM exam.programs where programtype in (";
		String[] types = (String[])type.split(",");
		for(int i=0;i<types.length;i++){
			sql += "'"+ types[i] + "',";
		}
		sql = sql.substring(0, sql.length()-1) + ") order by program asc";		
		
		List<String> programNames = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(String.class));

		return programNames;
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
	
	@Transactional(readOnly = false)
	public void saveFacultyRating(String rating,String facultyId,long recordId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " update acads.facultyrolemapping set rating = ?, lastModifiedDate = sysdate(), lastModifiedBy = ? where id = ? ";
		try{
			jdbcTemplate.update(sql, new Object[]{rating,facultyId,recordId});
		}catch(Exception e){
			  
		}
	}
	
	@Transactional(readOnly = true)
	public PageAcads<FacultyAcadsBean> getFacultyAllocationPage(int pageNo , int pageSize , FacultyAcadsBean faculty)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = " SELECT afr.*,af.firstName,af.lastName FROM acads.facultyrolemapping afr,acads.faculty af "
				+ " where afr.facultyAllocated = af.facultyId ";

		String countSql = " SELECT count(*) FROM acads.facultyrolemapping afr,acads.faculty af "
				+ " where afr.facultyAllocated = af.facultyId ";

		if(faculty.getAcadMonth() !=null && !"".equalsIgnoreCase(faculty.getAcadMonth().trim()))
		{
			sql = sql +" and afr.acadMonth = ? ";
			countSql = countSql +" and afr.acadMonth = ? ";
			parameters.add(faculty.getAcadMonth());
		}

		if(faculty.getAcadYear() !=null && !"".equalsIgnoreCase(faculty.getAcadYear().trim()))
		{
			sql = sql +" and afr.acadYear = ? ";
			countSql = countSql +" and afr.acadYear = ? ";
			parameters.add(faculty.getAcadYear());
		}

		if(faculty.getExamMonth() !=null && !"".equalsIgnoreCase(faculty.getExamMonth().trim()))
		{
			sql = sql +" and afr.examMonth = ? ";
			countSql = countSql +" and afr.examMonth = ? ";
			parameters.add(faculty.getExamMonth());
		}

		if(faculty.getExamYear() !=null && !"".equalsIgnoreCase(faculty.getExamYear().trim()))
		{
			sql = sql +" and afr.examYear = ? ";
			countSql = countSql +" and afr.examYear = ? ";
			parameters.add(faculty.getExamYear());
		}

		if(faculty.getRoleForAllocation() !=null && !"".equalsIgnoreCase(faculty.getRoleForAllocation().trim()))
		{
			sql = sql +" and afr.roleForAllocation = ? ";
			countSql = countSql +" and afr.roleForAllocation = ? ";
			parameters.add(faculty.getRoleForAllocation());
		}

		if(faculty.getFacultyAllocated() !=null && !"".equalsIgnoreCase(faculty.getFacultyAllocated().trim()))
		{
			sql = sql +" and afr.facultyAllocated = ? ";
			countSql = countSql +" and afr.facultyAllocated = ? ";
			parameters.add(faculty.getFacultyAllocated());
		}


		Object[] args = parameters.toArray();

		PaginationHelper<FacultyAcadsBean> pagingHelper = new PaginationHelper<FacultyAcadsBean>();
		PageAcads<FacultyAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(FacultyAcadsBean.class));

		return page;
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

		centerCode = (String) jdbcTemplate.queryForObject(tempSql,new Object[] { sapId, sapId },String.class);

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
	public List<SessionDayTimeAcadsBean> getAvailableDatesForFaculty(String facultyId, String subject) {
		if(facultyId == null || "".equals(facultyId.trim())){
			return null;
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from acads.calendar c, acads.session_days sd "
				+ " where  c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and  (DATEDIFF(c.date, (select max(date) from acads.sessions where year = 2015 and month = 'Jan' and subject = ?)) >= 7 OR "
				+ " DATEDIFF(c.date, (select max(date) from acads.sessions where year = 2015 and month = 'Jan' and subject = ?)) is null) "//Ensure session is not within 7 days of last session of same subject
				+ " and c.date not in (select unavailabilityDate from acads.facultyunavailabilitydates where facultyId = ?) " //And date is not which faculty is not going to be available
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' group by date, startTime having count(*) = 5 ) " //Ensure that time slot does not already have 5 sessions scheduled
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' and facultyId = ?) " //Ensure that faculty is not taking any other session at same time
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' "
				+ " and subject in (select distinct subject  from exam.program_subject where concat(program,sem,prgmStructApplicable)"
				+ "  in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ?) )) " // No other subject of same semester is scheduled at that time
				+ " order by c.date, sd.startTime";


		ArrayList<SessionDayTimeAcadsBean> datesList = (ArrayList<SessionDayTimeAcadsBean>)jdbcTemplate.query(sql, new Object[]{subject, subject, facultyId, facultyId, subject}, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
		return datesList;
	}

	@Transactional(readOnly = false)
	public void insertSession(SessionDayTimeAcadsBean bean, String sessionToScheduleId) {
		String sql = "INSERT INTO acads.sessions ( "
				+ " year, month, date, startTime, day, subject, sessionName, "
				+ " facultyId, createdBy, createdDate, lastModifiedBy, lastModifiedDate "
				+ ") "
				+ " VALUES( ?,?,?,?,?,?,?,?,?,sysdate(),?, sysdate()) ";


		String year = bean.getYear();
		String month = bean.getMonth();
		String date = bean.getDate();
		String startTime = bean.getStartTime();
		String day = bean.getDay();
		String subject = bean.getSubject();
		String sessionName = bean.getSessionName();
		String facultyId = bean.getFacultyId();
		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				date,
				startTime,
				day,
				subject,
				sessionName,
				facultyId,
				createdBy,
				createdBy

		});


		sql = " Update acads.facultycoursemapping set scheduled = 'Y' where id = ? ";
		jdbcTemplate.update(sql, new Object[] { sessionToScheduleId	});

	}

	@Transactional(readOnly = true)
	public PageAcads<SessionDayTimeAcadsBean> getScheduledSessionPage(int pageNo, int pageSize, SessionDayTimeAcadsBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.faculty f , acads.sessions s where s.facultyId = f.facultyId ";
		String countSql = "SELECT count(*) FROM acads.faculty f , acads.sessions s where s.facultyId = f.facultyId";

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
		sql = sql + " order by date, startTime asc";

		Object[] args = parameters.toArray();

		PaginationHelper<SessionDayTimeAcadsBean> pagingHelper = new PaginationHelper<SessionDayTimeAcadsBean>();
		PageAcads<SessionDayTimeAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

		return page;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return facultyList;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getAllFacultiesListNameAndId() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' ";
		ArrayList<String> facultyNameAndIdList = null;
		try{
			facultyNameAndIdList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return facultyNameAndIdList;
		}catch(Exception e){
			  
			return null;
		}


	}

	@Transactional(readOnly = true)
	public ArrayList<FacultyAcadsBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";
		ArrayList<FacultyAcadsBean> facultyNameAndIdList = null;
		try{
			facultyNameAndIdList = (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(FacultyAcadsBean.class));
			return facultyNameAndIdList;
		}catch(Exception e){
			  
			return null;
		}

	}
	
	@Transactional(readOnly = false)
	public void batchInsertRecordFacultyRole(final ArrayList<FacultyAcadsBean> facultyList){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO acads.facultyrolemapping (facultyAllocated,roleForAllocation,acadYear,acadMonth,examYear,examMonth,createdDate,lastModifiedDate) VALUES(?,?,?,?,?,?,sysdate(),sysdate()) ";

		try{
			int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FacultyAcadsBean facultyBean = facultyList.get(i);
					ps.setString(1,facultyBean.getFacultyAllocated());
					ps.setString(2,facultyBean.getRoleForAllocation());
					ps.setString(3,facultyBean.getAcadYear());
					ps.setString(4,facultyBean.getAcadMonth());
					ps.setString(5,facultyBean.getExamYear());
					ps.setString(6,facultyBean.getExamMonth());
				}

				@Override
				public int getBatchSize() {
					return facultyList.size();
				}
			});
		}catch(Exception e){
			  
		}

	}

	@Transactional(readOnly = true)
	public SessionDayTimeAcadsBean findScheduledSessionById(String id) {
		String sql = "SELECT * FROM acads.sessions where id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionDayTimeAcadsBean session = (SessionDayTimeAcadsBean) jdbcTemplate.queryForObject(
				sql, new Object[] { id }, new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));

		return session;
	}

	@Transactional(readOnly = true)
	public boolean isNewDateTimeValid(SessionDayTimeAcadsBean session) {

		String date = session.getDate();
		String time = session.getStartTime();
		String subject = session.getSubject();
		String facultyId = session.getFacultyId();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and c.date not in (select unavailabilityDate from acads.facultyunavailabilitydates where facultyId = ?) " //And date is not which faculty is not going to be available
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' group by date, startTime having count(*) = 5 ) " //Ensure that time slot does not already have 5 sessions scheduled
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' and facultyId = ?) " //Ensure that faculty is not taking any other session at same time
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' "
				+ " and subject in (select distinct subject  from exam.program_subject where concat(program,sem,prgmStructApplicable)"
				+ "  in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ?) )) " // No other subject of same semester is scheduled at that time
				+ " order by c.date, sd.startTime";


		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, facultyId, facultyId, subject},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}

	}

	@Transactional(readOnly = false)
	public void updateScheduledSession(SessionDayTimeAcadsBean session) {
		String sql = "Update acads.sessions set "
				+ "facultyId=?,"
				+ "date=?,"
				+ "startTime=?,"
				+ "day = (select dayName from acads.calendar where date = ?),"
				+ "lastModifiedBy=?,"
				+ "lastModifiedDate=sysdate()"
				+ "  where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				session.getFacultyId(),
				session.getDate(),
				session.getStartTime(),
				session.getDate(),
				session.getLastModifiedBy(),
				session.getId()
		});

	}

	@Transactional(readOnly = true)
	public boolean isFacultyAvailable(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String facultyId = session.getFacultyId();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and c.date not in (select unavailabilityDate from acads.facultyunavailabilitydates where facultyId = ?) "; //And date is not which faculty is not going to be available

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, facultyId},Integer.class);

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
		String facultyId = session.getFacultyId();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' and facultyId = ?) " //Ensure that faculty is not taking any other session at same time
				+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, facultyId},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)
	public boolean isSlotAvailable(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' group by date, startTime having count(*) = 5 ) " //Ensure that time slot does not already have 5 sessions scheduled
				+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)
	public boolean isNoSubjectClashing(SessionDayTimeAcadsBean session) {
		String date = session.getDate();
		String time = session.getStartTime();
		String subject = session.getSubject();

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " and CONCAT(c.date, sd.startTime) not in (select CONCAT(date, startTime) from acads.sessions where year = 2015 and month = 'Jan' "
				+ " and subject in (select distinct subject  from exam.program_subject where concat(program,sem,prgmStructApplicable)"
				+ "  in (Select concat(program,sem,prgmStructApplicable) from exam.program_subject where subject = ?) )) " // No other subject of same semester is scheduled at that time
				+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time, subject},Integer.class);

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

		String sql = "select count(*) from acads.calendar c, acads.session_days sd "
				+ " where c.date = ? and sd.startTime =  ? "
				+ " and c.dayName = sd.day and " // Those days mentioned in Session_days for academic year (i.e. Monday, Saturday, Sunday etc.)
				+ " sd.year = 2015 and sd.month = 'Jan' " //Academic calendar
				+ " and c.date >= (select startDate from acads.academic_calendar where year = 2015 and month = 'Jan') "
				+ " and c.date <= (select endDate from acads.academic_calendar where year = 2015 and month = 'Jan') " //Only those dates that lie between start and end day of Academic Calendar
				+ " and c.date > curdate() "//Date is NOT in past
				+ " order by c.date, sd.startTime";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{date, time},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = false)
	public void updateFacultyUnavailability(FacultyUnavailabilityBean faculty) {
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

	}

	@Transactional(readOnly = false)
	public String insertFaculty(FacultyAcadsBean faculty) {
		final String sql = " INSERT INTO acads.faculty "
					     + " (facultyId, title, firstname, lastname, email, mobile, active, createdBy, createdDate, lastModifiedBy, lastModifiedDate, facultyDescription, imgUrl, isConsentForm, consentFormUrl, programGroup, programName, approvedInSlab, dateOfECMeetingApprovalTaken, linkedInProfileUrl, comments,areaOfSpecialisation,otherAreaOfSpecialisation, salutation, country_code, ecApprovalDate, ecApprovalProofUrl, ecApprovalComment, auditStatus, facultyStatus) "
					     + " VALUES( ?,?,?,?,?,?,'Y',?,sysdate(),?, sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		final FacultyAcadsBean bean = faculty;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				ps.setString(1, bean.getFacultyId());
				ps.setString(2, bean.getTitle());
				ps.setString(3, bean.getFirstName());
				ps.setString(4, bean.getLastName());
				ps.setString(5, bean.getEmail());
				ps.setString(6, bean.getMobile());
				ps.setString(7, bean.getCreatedBy());
				ps.setString(8, bean.getCreatedBy());
				ps.setString(9, bean.getFacultyDescription());
				ps.setString(10, bean.getImgUrl());
				ps.setString(11, bean.getIsConsentForm());
				ps.setString(12, bean.getConsentFormUrl());
				ps.setString(13, bean.getProgramGroup());
				ps.setString(14, bean.getProgramName());
				ps.setString(15, bean.getApprovedInSlab());
				ps.setString(16, bean.getDateOfECMeetingApprovalTaken());
				ps.setString(17, bean.getLinkedInProfileUrl());
				ps.setString(18, bean.getComments());
				ps.setString(19,bean.getAreaOfSpecialisation());
				ps.setString(20, bean.getOtherAreaOfSpecialisation());
				ps.setString(21, bean.getSalutation());
				ps.setString(22, bean.getCountryCode());
				ps.setString(23, bean.getEcApprovalDate());
				ps.setString(24, bean.getEcApprovalProofUrl());
				ps.setString(25, bean.getEcApprovalComment());
				ps.setString(26, bean.getAuditStatus());
				ps.setString(27, bean.getFacultyStatus());

				return ps;
			}
		};
		

		try {
			//jdbcTemplate.update(psc);
			jdbcTemplate.update(psc, keyHolder);
			
			int id = keyHolder.getKey().intValue();

			faculty.setId(id+"");
			return "success";
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			return e.getMessage();
		}

	}

	@Transactional(readOnly = true)
	public FacultyAcadsBean findByName(String id) {
		String sql = "SELECT * FROM acads.faculty WHERE id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyAcadsBean faculty = (FacultyAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(FacultyAcadsBean.class));

		return faculty;
	}

	@Transactional(readOnly = true)
	public FacultyAcadsBean findfacultyByFacultyId(String facultyId) {
		String sql = "SELECT * FROM acads.faculty WHERE facultyId = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyAcadsBean faculty = (FacultyAcadsBean) jdbcTemplate.queryForObject(sql, new Object[] {facultyId}
		, new BeanPropertyRowMapper(FacultyAcadsBean.class));

		return faculty;
	}

	@Transactional(readOnly = false)
	public void updateFaculty(FacultyAcadsBean faculty) {
		String sql = "Update acads.faculty set "
				+ "title = ?,"
				+ "firstName = ?,"
				+ "lastName = ?,"
				+ "mobile = ?,"
				+ "email = ?,"
				+ "facultyDescription =?,"
				+ "imgUrl =?,"
				+ "isConsentForm =?,"
				+ "consentFormUrl =?,"
				+ "programGroup =?,"
				+ "programName =?,"
				+ "approvedInSlab =?,"
				+ "dateOfECMeetingApprovalTaken =?,"
				+ "linkedInProfileUrl =?,"
				+ "areaOfSpecialisation =?,"
				+ "otherAreaOfSpecialisation =?,"
				+ "comments =?,"
				+ "lastModifiedBy =?,"
				+ "lastModifiedDate=SYSDATE(), "
				+ "salutation =?, "
				+ "country_code =?,"
				+ "ecApprovalDate = ?, "
				+ "ecApprovalProofUrl =?, "
				+ "ecApprovalComment =?, "
				+ "auditStatus =?, "
				+ "facultyStatus =?"

				+ " where id= ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);


		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,faculty.getTitle());
				preparedStatement.setString(2,faculty.getFirstName());
				preparedStatement.setString(3,faculty.getLastName());
				preparedStatement.setString(4,faculty.getMobile());
				preparedStatement.setString(5,faculty.getEmail());
				preparedStatement.setString(6,faculty.getFacultyDescription());
				preparedStatement.setString(7,faculty.getImgUrl());
				preparedStatement.setString(8,faculty.getIsConsentForm());
				preparedStatement.setString(9,faculty.getConsentFormUrl());
				preparedStatement.setString(10,faculty.getProgramGroup());
				preparedStatement.setString(11,faculty.getProgramName());
				preparedStatement.setString(12,faculty.getApprovedInSlab());
				preparedStatement.setString(13,faculty.getDateOfECMeetingApprovalTaken());
				preparedStatement.setString(14,faculty.getLinkedInProfileUrl());
				preparedStatement.setString(15,faculty.getAreaOfSpecialisation());
				preparedStatement.setString(16,faculty.getOtherAreaOfSpecialisation());
				preparedStatement.setString(17,faculty.getComments());
				preparedStatement.setString(18,faculty.getLastModifiedBy());
				preparedStatement.setString(19,faculty.getSalutation());
				preparedStatement.setString(20,faculty.getCountryCode());
				preparedStatement.setString(21,faculty.getEcApprovalDate());
				preparedStatement.setString(22,faculty.getEcApprovalProofUrl());
				preparedStatement.setString(23,faculty.getEcApprovalComment());
				preparedStatement.setString(24,faculty.getAuditStatus());
				preparedStatement.setString(25,faculty.getFacultyStatus());
				
				preparedStatement.setString(26,faculty.getId());
			}
			});

	}

	@Transactional(readOnly = false)
	public void saveFacultyProfile(FacultyAcadsBean faculty) {
		String sql = "Update acads.faculty set "
				+ " title = ?,"
				+ " firstName = ?,"
				+ " middleName = ?,"
				+ " lastName = ?,"
				+ " mobile = ?,"
				+ " email = ? , "
				+ " secondaryEmail = ?,"
				+ " dob = ? ,"
				+ " altContact = ?,"
				+ " officeContact = ?,"
				+ " homeContact = ?,"
				+ " location = ? ,"
				+ " address = ? ,"
				+ " graduationDetails = ? ,"
				+ " yearOfPassingGraduation = ? ,"
				+ " phdDetails = ? ,"
				+ " yearOfPassingPhd = ? ,"
				+ " net = ? ,"
				+ " setDetail = ? ,"
				+ " teachingExp = ? ,"
				+ " corporateExp = ? ,"
				+ " ngasceExp = ? ,"
				+ " cvUrl = ? ,"
				+ " imgUrl = ? ,"
				+ " subjectPref1 = ? , "
				+ " subjectPref2 = ? , "
				+ " subjectPref3 = ? ,"
				+ " currentOrganization = ? ,"
				+ " designation = ? ,"
				+ " natureOfAppointment = ?,"
				+ " areaOfSpecialisation = ?,"
				+ " otherAreaOfSpecialisation = ?,"
				+ " aadharNumber = ?,"
//				+ " approvedInSlab = ?,"
//				+ " dateOfECMeetingApprovalTaken = ?,"
				+ " consentForMarketingCollateralsOrPhotoAndProfileRelease = ?,"
				+ " consentForMarketingCollateralsOrPhotoAndProfileReleaseReason = ?,"
				+ " honorsAndAwards = ?,"
				+ " memberships = ?,"
				+ " researchInterest = ?,"
				+ " articlesPublishedInInternationalJournals = ?,"
				+ " articlesPublishedInNationalJournals = ?,"
				+ " summaryOfPapersPublishedInABDCJournals = ?,"
				+ " paperPresentationsAtInternationalConference = ?,"
				+ " paperPresentationAtNationalConference = ?,"
				+ " caseStudiesPublished = ?,"
				+ " booksPublished = ?,"
				+ " bookChaptersPublished = ?,"
				+ " listOfPatents = ?,"
				+ " consultingProjects = ?,"
				+ " researchProjects = ?,"
				+ " programGroup = ?,"
				+ " programName = ?,"
				+ " linkedInProfileUrl = ?,"
				+ " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate()"


				+ " where id= ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,faculty.getTitle());
				preparedStatement.setString(2,faculty.getFirstName());
				preparedStatement.setString(3,faculty.getMiddleName());
				preparedStatement.setString(4,faculty.getLastName());
				preparedStatement.setString(5,faculty.getMobile());
				preparedStatement.setString(6,faculty.getEmail());
				preparedStatement.setString(7,faculty.getSecondaryEmail());
				preparedStatement.setString(8,faculty.getDob());
				preparedStatement.setString(9,faculty.getAltContact());
				preparedStatement.setString(10,faculty.getOfficeContact());
				preparedStatement.setString(11,faculty.getHomeContact());
				preparedStatement.setString(12,faculty.getLocation());
				preparedStatement.setString(13,faculty.getAddress());
				preparedStatement.setString(14,faculty.getGraduationDetails());
				preparedStatement.setString(15,faculty.getYearOfPassingGraduation());
				preparedStatement.setString(16,faculty.getPhdDetails());
				preparedStatement.setString(17,faculty.getYearOfPassingPhd());
				preparedStatement.setString(18,faculty.getNet());
				preparedStatement.setString(19,faculty.getSetDetail());
				preparedStatement.setString(20,faculty.getTeachingExp());
				preparedStatement.setString(21,faculty.getCorporateExp());
				preparedStatement.setString(22,faculty.getNgasceExp());
				preparedStatement.setString(23,faculty.getCvUrl());
				preparedStatement.setString(24,faculty.getImgUrl());
				preparedStatement.setString(25,faculty.getSubjectPref1());
				preparedStatement.setString(26,faculty.getSubjectPref2());
				preparedStatement.setString(27,faculty.getSubjectPref3());
				preparedStatement.setString(28,faculty.getCurrentOrganization());
				preparedStatement.setString(29,faculty.getDesignation());
				preparedStatement.setString(30,faculty.getNatureOfAppointment());
				preparedStatement.setString(31,faculty.getAreaOfSpecialisation());
				preparedStatement.setString(32,faculty.getOtherAreaOfSpecialisation());
				preparedStatement.setString(33,faculty.getAadharNumber());
//				faculty.getApprovedInSlab());
//				faculty.getDateOfECMeetingApprovalTaken());
				preparedStatement.setString(34,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileRelease());
				preparedStatement.setString(35,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason());
				preparedStatement.setString(36,faculty.getHonorsAndAwards());
				preparedStatement.setString(37,faculty.getMemberships());
				preparedStatement.setString(38,faculty.getResearchInterest());
				preparedStatement.setString(39,faculty.getArticlesPublishedInInternationalJournals());
				preparedStatement.setString(40,faculty.getArticlesPublishedInNationalJournals());
				preparedStatement.setString(41,faculty.getSummaryOfPapersPublishedInABDCJournals());
				preparedStatement.setString(42,faculty.getPaperPresentationsAtInternationalConference());
				preparedStatement.setString(43,faculty.getPaperPresentationAtNationalConference());
				preparedStatement.setString(44,faculty.getCaseStudiesPublished());
				preparedStatement.setString(45,faculty.getBooksPublished());
				preparedStatement.setString(46,faculty.getBookChaptersPublished());
				preparedStatement.setString(47,faculty.getListOfPatents());
				preparedStatement.setString(48,faculty.getConsultingProjects());
				preparedStatement.setString(49,faculty.getResearchProjects());
				preparedStatement.setString(50,faculty.getProgramGroup());
				preparedStatement.setString(51,faculty.getProgramName());
				preparedStatement.setString(52,faculty.getLinkedInProfileUrl());
				preparedStatement.setString(53,faculty.getLastModifiedBy());
				preparedStatement.setString(54,faculty.getId());
		}
		});

	}
	
	@Transactional(readOnly = false)
	public void deactivateFaculty(FacultyAcadsBean faculty) {
		String sql = "Update acads.faculty set "
				+ "active = 'N' "
				+ " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new PreparedStatementSetter(){
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,faculty.getId());
			}
			});

	}

	@Transactional(readOnly = true)
	public Map<String,Double> getAttendanceGrandAvgBasedUponFaculty()
	{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " select saf.facultyId , "
				+" ROUND((AVG(saf.q1Response)+AVG(saf.q2Response)+AVG(saf.q5Response) + AVG(saf.q6Response) + AVG(saf.q7Response) + AVG(saf.q8Response))/6,2) as grandFacultyAverage "
				+" from acads.session_attendance_feedback saf,acads.faculty f,exam.students st , acads.sessions s "
				+" where st.sapid = saf.sapid and saf.facultyId = f.facultyId and saf.sessionId = s.id "
				+" and saf.attended = 'Y' and saf.studentConfirmationForAttendance = 'Y' "
				+" and s.year = ? and s.month = ? and saf.q1Response <> '' "
				+" group by saf.facultyId ";

		ArrayList<SessionAttendanceFeedbackAcads> lstAttendanceGrandAvg = (ArrayList<SessionAttendanceFeedbackAcads>)jdbcTemplate.query(sql, new Object[]{CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH},new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		Map<String,Double> valueMap = new HashMap<String,Double>();

		for(SessionAttendanceFeedbackAcads sessionBean : lstAttendanceGrandAvg){
			valueMap.put(sessionBean.getFacultyId(),(sessionBean.getGrandFacultyAverage() !="" && sessionBean.getGrandFacultyAverage() !=null) ?Double.valueOf(sessionBean.getGrandFacultyAverage()) : 0.0);
		}
		return valueMap;
	}

	@Transactional(readOnly = true)
	public Map<String,Double> getSessionPeerReviewGrandAvgBasedUponFaculty()
	{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select sr.facultyId,ROUND(((AVG(sr.q5Response)+AVG(sr.q6Response))/2),2) as grandFacultyAverage"
				+" from acads.session_review sr,acads.sessions s, acads.faculty f"
				+" where sr.facultyId = f.facultyId and sr.sessionId = s.id "
				+" and s.year = ? and s.month = ? "
				+" and sr.reviewed ='Reviewed' group by sr.facultyId ";

		ArrayList<SessionAttendanceFeedbackAcads> lstAttendanceGrandAvg = (ArrayList<SessionAttendanceFeedbackAcads>)jdbcTemplate.query(sql, new Object[]{CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH},new BeanPropertyRowMapper(SessionAttendanceFeedbackAcads.class));
		Map<String,Double> valueMap = new HashMap<String,Double>();

		for(SessionAttendanceFeedbackAcads sessionBean : lstAttendanceGrandAvg){
			valueMap.put(sessionBean.getFacultyId(),(sessionBean.getGrandFacultyAverage() !="" && sessionBean.getGrandFacultyAverage() !=null) ?Double.valueOf(sessionBean.getGrandFacultyAverage()) : 0.0);
		}
		return valueMap;
	}
	//Edited on 12/9/2017 ----Start
	@Transactional(readOnly = true)
		public List<FacultyAcadsBean> getFacultyPage(FacultyAcadsBean searchBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<Object> parameters = new ArrayList<Object>();

			String sql = "SELECT f.* "
					+ " FROM acads.faculty f "
					+ "  WHERE  1 = 1";
			/*String countSql = "SELECT count(distinct f.facultyId) "
					+ " FROM acads.faculty f "
					+ " WHERE  1 = 1";*/

			if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
				sql = sql + " and f.facultyId =  ? ";
				/*countSql = countSql + " and f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyId());
			}

			if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
			{
				sql += " and f.facultyId = ? ";
				/*countSql = countSql + " and f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyFullName());
			}

			if( searchBean.getLocation() != null &&   !("".equals(searchBean.getLocation()))){
				sql = sql + " and f.location like  ? ";
				/*	countSql = countSql + " and f.location like  ? ";*/
				parameters.add("%"+searchBean.getLocation()+"%");
			}

			if( searchBean.getNgasceExp() != null &&   !("".equals(searchBean.getNgasceExp()))){
				sql = sql + " and f.ngasceExp like  ? ";
				/*countSql = countSql + " and f.ngasceExp like  ? ";*/
				parameters.add("%"+searchBean.getNgasceExp()+"%");
			}

			if( searchBean.getSubjectPref1() != null &&   !("".equals(searchBean.getSubjectPref1()))){
				sql = sql + " and f.subjectPref1 = ? ";
				/*countSql = countSql + " and f.subjectPref1 = ? ";*/
				parameters.add(searchBean.getSubjectPref1());
			}

			if( searchBean.getSubjectPref2() != null &&   !("".equals(searchBean.getSubjectPref2()))){
				sql = sql + " and f.subjectPref2 = ? ";
				/*	countSql = countSql + " and f.subjectPref2 = ? ";*/
				parameters.add(searchBean.getSubjectPref2());
			}

			if( searchBean.getSubjectPref3() != null &&   !("".equals(searchBean.getSubjectPref3()))){
				sql = sql + " and  f.subjectPref3 = ? ";
				/*countSql = countSql + " and f.subjectPref3 = ? ";*/
				parameters.add(searchBean.getSubjectPref3());
			}
			
			if( searchBean.getApprovedInSlab() != null &&   !("".equals(searchBean.getApprovedInSlab()))){
				sql = sql + " and f.approvedInSlab = ? ";
				/*	countSql = countSql + " and f.subjectPref2 = ? ";*/
				parameters.add(searchBean.getApprovedInSlab());
			}

			sql = sql + " group by f.facultyId order by id";

			Object[] args = parameters.toArray();

			/*	PaginationHelper<FacultyBean> pagingHelper = new PaginationHelper<FacultyBean>();
			Page<FacultyBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(FacultyBean.class));*/
			List<FacultyAcadsBean> facultyList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(FacultyAcadsBean.class) );

			return facultyList;
		}

	@Transactional(readOnly = true)
		public List<FacultyAcadsBean>  getFacultyPageWithPeerRatings(FacultyAcadsBean searchBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<Object> parameters = new ArrayList<Object>();

			String sql = "select round(Avg(peerReviewAvg),2) as peerReviewAvg , f.* from acads.faculty f "
					+ " LEFT JOIN acads.session_review sr "
					+ " on f.facultyId = sr.facultyId "
					+ " where 1=1 ";
			/*String countSql = "select count(distinct f.facultyId) from acads.faculty f "
					+ " LEFT JOIN acads.session_review sr "
					+ " on f.facultyId = sr.facultyId "
					+ " where 1=1 ";*/

			if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
				sql = sql + " and f.facultyId =  ? ";
				/*countSql = countSql + " f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyId());
			}


			if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
			{
				sql += " and f.facultyId = ? ";
				/*countSql = countSql + " and f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyFullName());
			}

			if( searchBean.getLocation() != null &&   !("".equals(searchBean.getLocation()))){
				sql = sql + " and f.location like  ? ";
				/*countSql = countSql + " and f.location like  ? ";*/
				parameters.add("%"+searchBean.getLocation()+"%");
			}

			if( searchBean.getNgasceExp() != null &&   !("".equals(searchBean.getNgasceExp()))){
				sql = sql + " and f.ngasceExp like  ? ";
				/*countSql = countSql + " and f.ngasceExp like  ? ";*/
				parameters.add("%"+searchBean.getNgasceExp()+"%");
			}

			if( searchBean.getSubjectPref1() != null &&   !("".equals(searchBean.getSubjectPref1()))){
				sql = sql + " and f.subjectPref1 = ? ";
				/*countSql = countSql + " and f.subjectPref1 = ? ";*/
				parameters.add(searchBean.getSubjectPref1());
			}

			if( searchBean.getSubjectPref2() != null &&   !("".equals(searchBean.getSubjectPref2()))){
				sql = sql + " and f.subjectPref2 = ? ";
				/*countSql = countSql + " and f.subjectPref2 = ? ";*/
				parameters.add(searchBean.getSubjectPref2());
			}

			if( searchBean.getSubjectPref3() != null &&   !("".equals(searchBean.getSubjectPref3()))){
				sql = sql + " and  f.subjectPref3 = ? ";
				/*	countSql = countSql + " and f.subjectPref3 = ? ";*/
				parameters.add(searchBean.getSubjectPref3());
			}


			if(!StringUtils.isBlank(searchBean.getMinPeerReviewAvg()))
			{
				sql += " and sr.peerReviewAvg >= ? ";
				/*countSql = countSql + " and sr.peerReviewAvg >= ? ";*/
				parameters.add(searchBean.getMinPeerReviewAvg());
			}

			if(!StringUtils.isBlank(searchBean.getMaxPeerReviewAvg()))
			{
				sql += " and sr.peerReviewAvg <= ? ";
				/*countSql = countSql + " and sr.peerReviewAvg <= ? ";*/
				parameters.add(searchBean.getMaxPeerReviewAvg());
			}


			sql = sql + " and sr.peerReviewAvg is not null group by f.facultyId";

			Object[] args = parameters.toArray();

			/*	PaginationHelper<FacultyBean> pagingHelper = new PaginationHelper<FacultyBean>();
			Page<FacultyBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(FacultyBean.class));

			return page;*/
			List<FacultyAcadsBean> facultyList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(FacultyAcadsBean.class) );

			return facultyList;

		}

	@Transactional(readOnly = true)
		public List<FacultyAcadsBean> getFacultyPageWithStudentRatings(FacultyAcadsBean searchBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<Object> parameters = new ArrayList<Object>();

			String sql = "select ROUND(((AVG(saf.q1Response)+AVG(saf.q2Response)+AVG(saf.q3Response)+AVG(saf.q4Response)+AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/8),2) as studentReviewAvg , f.* from acads.faculty f "
					+ " LEFT JOIN acads.session_attendance_feedback saf "
					+ " on f.facultyId = saf.facultyId "
					+ " where 1=1 " ;
			/*	
			String countSql = "select count(distinct f.facultyId) from acads.faculty f "
					+ " LEFT JOIN acads.session_attendance_feedback saf "
					+ " on f.facultyId = saf.facultyId "
					+ " where 1=1 ";*/
			if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
				sql = sql + " and f.facultyId =  ? ";
				/*countSql = countSql + " f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyId());
			}


			if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
			{
				sql += " and f.facultyId = ? ";
				/*countSql = countSql + " and f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyFullName());
			}

			if( searchBean.getLocation() != null &&   !("".equals(searchBean.getLocation()))){
				sql = sql + " and f.location like  ? ";
				/*	countSql = countSql + " and f.location like  ? ";*/
				parameters.add("%"+searchBean.getLocation()+"%");
			}

			if( searchBean.getNgasceExp() != null &&   !("".equals(searchBean.getNgasceExp()))){
				sql = sql + " and f.ngasceExp like  ? ";
				/*countSql = countSql + " and f.ngasceExp like  ? ";*/
				parameters.add("%"+searchBean.getNgasceExp()+"%");
			}

			if( searchBean.getSubjectPref1() != null &&   !("".equals(searchBean.getSubjectPref1()))){
				sql = sql + " and f.subjectPref1 = ? ";
				/*	countSql = countSql + " and f.subjectPref1 = ? ";*/
				parameters.add(searchBean.getSubjectPref1());
			}

			if( searchBean.getSubjectPref2() != null &&   !("".equals(searchBean.getSubjectPref2()))){
				sql = sql + " and f.subjectPref2 = ? ";
				/*countSql = countSql + " and f.subjectPref2 = ? ";*/
				parameters.add(searchBean.getSubjectPref2());
			}

			if( searchBean.getSubjectPref3() != null &&   !("".equals(searchBean.getSubjectPref3()))){
				sql = sql + " and  f.subjectPref3 = ? ";
				/*countSql = countSql + " and f.subjectPref3 = ? ";*/
				parameters.add(searchBean.getSubjectPref3());
			}

			if(!StringUtils.isBlank(searchBean.getMinStudentReviewAvg()))
			{
				sql += " and saf.studentReviewAvg >= ? ";
				/*countSql = countSql + " and saf.studentReviewAvg >= ? ";*/
				parameters.add(searchBean.getMinStudentReviewAvg());
			}

			if(!StringUtils.isBlank(searchBean.getMaxStudentReviewAvg()))
			{
				sql += " and saf.studentReviewAvg <= ? ";
				/*countSql = countSql + " and saf.studentReviewAvg <= ? ";*/
				parameters.add(searchBean.getMaxStudentReviewAvg());
			}

			sql = sql + " and saf.studentReviewAvg is not null group by f.facultyId";


			Object[] args = parameters.toArray();
			/*
			PaginationHelper<FacultyBean> pagingHelper = new PaginationHelper<FacultyBean>();
			Page<FacultyBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(FacultyBean.class));

			return page;*/
			List<FacultyAcadsBean> facultyList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(FacultyAcadsBean.class) );

			return facultyList;
		}

	@Transactional(readOnly = true)
		public List<FacultyAcadsBean>  getFacultyPageWithBothRatings(FacultyAcadsBean searchBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			ArrayList<Object> parameters = new ArrayList<Object>();

			String sql = "SELECT f.*, Round (AVG(sr.peerReviewAvg),2) as peerReviewAvg, ROUND(((AVG(saf.q1Response)+AVG(saf.q2Response)+AVG(saf.q3Response)+AVG(saf.q4Response)+AVG(saf.q5Response)+AVG(saf.q6Response)+AVG(saf.q7Response)+AVG(saf.q8Response))/8),2) as studentReviewAvg "
					+ " FROM acads.faculty f, acads.session_review sr, acads.session_attendance_feedback saf"
					+ " WHERE f.facultyId = sr.facultyId"
					+ " AND sr.facultyId = saf.facultyId";
			/*String countSql = "SELECT count(distinct f.facultyId)"
					+ " FROM acads.faculty f, acads.session_review sr, acads.session_attendance_feedback saf"
					+ " WHERE f.facultyId = sr.facultyId"
					+ " AND sr.facultyId = saf.facultyId";*/
			if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
				sql = sql + " and f.facultyId =  ? ";
				/*countSql = countSql + " f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyId());
			}

			if(!StringUtils.isBlank(searchBean.getFacultyFullName()))
			{
				sql += " and f.facultyId = ? ";
				/*	countSql = countSql + " and f.facultyId = ? ";*/
				parameters.add(searchBean.getFacultyFullName());
			}

			if( searchBean.getLocation() != null &&   !("".equals(searchBean.getLocation()))){
				sql = sql + " and f.location like  ? ";
				/*countSql = countSql + " and f.location like  ? ";*/
				parameters.add("%"+searchBean.getLocation()+"%");
			}

			if( searchBean.getNgasceExp() != null &&   !("".equals(searchBean.getNgasceExp()))){
				sql = sql + " and f.ngasceExp like  ? ";
				/*countSql = countSql + " and f.ngasceExp like  ? ";*/
				parameters.add("%"+searchBean.getNgasceExp()+"%");
			}

			if( searchBean.getSubjectPref1() != null &&   !("".equals(searchBean.getSubjectPref1()))){
				sql = sql + " and f.subjectPref1 = ? ";
				/*countSql = countSql + " and f.subjectPref1 = ? ";*/
				parameters.add(searchBean.getSubjectPref1());
			}

			if( searchBean.getSubjectPref2() != null &&   !("".equals(searchBean.getSubjectPref2()))){
				sql = sql + " and f.subjectPref2 = ? ";
				/*	countSql = countSql + " and f.subjectPref2 = ? ";*/
				parameters.add(searchBean.getSubjectPref2());
			}

			if( searchBean.getSubjectPref3() != null &&   !("".equals(searchBean.getSubjectPref3()))){
				sql = sql + " and  f.subjectPref3 = ? ";
				/*	countSql = countSql + " and f.subjectPref3 = ? ";*/
				parameters.add(searchBean.getSubjectPref3());
			}


			if(!StringUtils.isBlank(searchBean.getMinPeerReviewAvg()))
			{
				sql += " and sr.peerReviewAvg >= ? ";
				/*countSql = countSql + " and sr.peerReviewAvg >= ? ";*/
				parameters.add(searchBean.getMinPeerReviewAvg());
			}

			if(!StringUtils.isBlank(searchBean.getMaxPeerReviewAvg()))
			{
				sql += " and sr.peerReviewAvg <= ? ";
				/*	countSql = countSql + " and sr.peerReviewAvg <= ? ";*/
				parameters.add(searchBean.getMaxPeerReviewAvg());
			}

			if(!StringUtils.isBlank(searchBean.getMinStudentReviewAvg()))
			{
				sql += " and saf.studentReviewAvg >= ? ";
				/*countSql = countSql + " and saf.studentReviewAvg >= ? ";*/
				parameters.add(searchBean.getMinStudentReviewAvg());
			}

			if(!StringUtils.isBlank(searchBean.getMaxStudentReviewAvg()))
			{
				sql += " and saf.studentReviewAvg <= ? ";
				/*countSql = countSql + " and saf.studentReviewAvg <= ? ";*/
				parameters.add(searchBean.getMaxStudentReviewAvg());
			}

			sql = sql + " and saf.studentReviewAvg is not null and sr.peerReviewAvg is not null group by f.facultyId";

			Object[] args = parameters.toArray();
			/*	PaginationHelper<FacultyBean> pagingHelper = new PaginationHelper<FacultyBean>();
			Page<FacultyBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
					new BeanPropertyRowMapper(FacultyBean.class));
			return page*/

			List<FacultyAcadsBean> facultyList = jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(FacultyAcadsBean.class) );

			return facultyList;

		}
		//Edited on 12/9/2017 ----End
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFacultyNameList(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql= "select f.firstName,f.lastName from acads.faculty f where f.active='Y'";
		ArrayList<FacultyAcadsBean> facultyNameList= (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(FacultyAcadsBean.class));
		ArrayList<String> facultyName = new ArrayList<String>();

		for(FacultyAcadsBean faculty : facultyNameList){
			facultyName.add(faculty.getFullName());
		}

		return facultyName;
	}

	@Transactional(readOnly = true)
	public Map<String,String> getFacultyMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql= "select * from acads.faculty  where active='Y' ";
		ArrayList<FacultyAcadsBean> facultyNameList= (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(FacultyAcadsBean.class));
		Map<String,String> facultyIdMap = new HashMap<String,String>();
		for(FacultyAcadsBean faculty : facultyNameList){
			facultyIdMap.put(faculty.getFacultyId(), faculty.getFullName());
		}

		return facultyIdMap;
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateFaculty(final List<FacultyAcadsBean> facultyList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < facultyList.size(); i++) {
			try{
				FacultyAcadsBean bean = facultyList.get(i);
				
				//check duplicate EmailId
				if(checkIfEmailPresent(bean.getEmail(),bean.getFacultyId()) > 0)
				{
					errorList.add(i+"- Duplicate Email Id.");
					continue;
				}
				//check duplicate mobile No
				
				if(bean.getTitle().equals("Grader")) {
				if(checkIfDuplicateMobilePresent(bean.getMobile(),bean.getFacultyId()) > 0)
				{
					errorList.add(i+"- Duplicate Mobile Number.");
					continue;
				}
				}	
				upsertFaculty(bean, jdbcTemplate);
				
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false)
	private void upsertFaculty(FacultyAcadsBean faculty, JdbcTemplate jdbcTemplate) {

		String searchSql = "SELECT count(*) as count from acads.faculty where facultyId=?";
		int count = (int) jdbcTemplate.queryForObject(searchSql, new Object[] { faculty.getFacultyId() },
				new SingleColumnRowMapper(Integer.class));

		if (count == 0) {
			String insertSql = "INSERT INTO acads.faculty(facultyId, title, firstName, middleName, lastName, mobile, email, "
					+ "secondaryEmail, dob, altContact, officeContact, homeContact, location, address, graduationDetails, "
					+ "yearOfPassingGraduation, phdDetails, yearOfPassingPhd, net, setDetail, teachingExp, corporateExp, ngasceExp, "
					+ "cvUrl, imgUrl, subjectPref1, subjectPref2, subjectPref3, currentOrganization, designation, natureOfAppointment, "
					+ "areaOfSpecialisation, otherAreaOfSpecialisation, aadharNumber, approvedInSlab, dateOfECMeetingApprovalTaken, "
					+ "consentForMarketingCollateralsOrPhotoAndProfileRelease, consentForMarketingCollateralsOrPhotoAndProfileReleaseReason, "
					+ "honorsAndAwards, memberships, researchInterest, articlesPublishedInInternationalJournals, articlesPublishedInNationalJournals, "
					+ "summaryOfPapersPublishedInABDCJournals, paperPresentationsAtInternationalConference, paperPresentationAtNationalConference, "
					+ "caseStudiesPublished, booksPublished, bookChaptersPublished, listOfPatents, consultingProjects, researchProjects, "
					+ "programGroup, programName, "
					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?, sysdate())";

			jdbcTemplate.update(insertSql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,faculty.getFacultyId());
					preparedStatement.setString(2,faculty.getTitle());
					preparedStatement.setString(3,faculty.getFirstName());
					preparedStatement.setString(4,faculty.getMiddleName());
					preparedStatement.setString(5,faculty.getLastName());
					preparedStatement.setString(6,faculty.getMobile());
					preparedStatement.setString(7,faculty.getEmail());
					preparedStatement.setString(8,faculty.getDob());
					preparedStatement.setString(9,faculty.getSecondaryEmail());
					preparedStatement.setString(10,faculty.getAltContact());
					preparedStatement.setString(11,faculty.getOfficeContact());
					preparedStatement.setString(12,faculty.getHomeContact());
					preparedStatement.setString(13,faculty.getLocation());
					preparedStatement.setString(14,faculty.getAddress());
					preparedStatement.setString(15,faculty.getGraduationDetails());
					preparedStatement.setString(16,faculty.getYearOfPassingGraduation());
					preparedStatement.setString(17,faculty.getPhdDetails());
					preparedStatement.setString(18,faculty.getYearOfPassingPhd());
					preparedStatement.setString(19,faculty.getNet());
					preparedStatement.setString(20,faculty.getSetDetail());
					preparedStatement.setString(21,faculty.getTeachingExp());
					preparedStatement.setString(22,faculty.getCorporateExp());
					preparedStatement.setString(23,faculty.getNgasceExp());
					preparedStatement.setString(24,faculty.getCvUrl());
					preparedStatement.setString(25,faculty.getImgUrl());
					preparedStatement.setString(26,faculty.getSubjectPref1());
					preparedStatement.setString(27,faculty.getSubjectPref2());
					preparedStatement.setString(28,faculty.getSubjectPref3());
					preparedStatement.setString(29,faculty.getCurrentOrganization());
					preparedStatement.setString(30,faculty.getDesignation());
					preparedStatement.setString(31,faculty.getNatureOfAppointment());
					preparedStatement.setString(32,faculty.getAreaOfSpecialisation());
					preparedStatement.setString(33,faculty.getOtherAreaOfSpecialisation());
					preparedStatement.setString(34,faculty.getAadharNumber());
					preparedStatement.setString(35,faculty.getApprovedInSlab());
					preparedStatement.setString(36,faculty.getDateOfECMeetingApprovalTaken());
					preparedStatement.setString(37,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileRelease());
					preparedStatement.setString(38,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason());
					preparedStatement.setString(39,faculty.getHonorsAndAwards());
					preparedStatement.setString(40,faculty.getMemberships());
					preparedStatement.setString(41,faculty.getResearchInterest());
					preparedStatement.setString(42,faculty.getArticlesPublishedInInternationalJournals());
					preparedStatement.setString(43,faculty.getArticlesPublishedInNationalJournals());
					preparedStatement.setString(44,faculty.getSummaryOfPapersPublishedInABDCJournals());
					preparedStatement.setString(45,faculty.getPaperPresentationsAtInternationalConference());
					preparedStatement.setString(46,faculty.getPaperPresentationAtNationalConference());
					preparedStatement.setString(47,faculty.getCaseStudiesPublished());
					preparedStatement.setString(48,faculty.getBooksPublished());
					preparedStatement.setString(49,faculty.getBookChaptersPublished());
					preparedStatement.setString(50,faculty.getListOfPatents());
					preparedStatement.setString(51,faculty.getConsultingProjects());
					preparedStatement.setString(52,faculty.getResearchProjects());
					preparedStatement.setString(53,faculty.getProgramGroup());
					preparedStatement.setString(54,faculty.getProgramName());
					preparedStatement.setString(55,faculty.getLinkedInProfileUrl());
					preparedStatement.setString(56,faculty.getLastModifiedBy());
			}});
		} else {
			String updateSql = "UPDATE acads.faculty SET " + " title = ?," + " firstName = ?," + " middleName = ?,"
					+ " lastName = ?," + " mobile = ?," + " email = ? , " + " secondaryEmail = ?," + " dob = ? ,"
					+ " altContact = ?," + " officeContact = ?," + " homeContact = ?," + " location = ? ,"
					+ " address = ? ," + " graduationDetails = ? ," + " yearOfPassingGraduation = ? ,"
					+ " phdDetails = ? ," + " yearOfPassingPhd = ? ," + " net = ? ," + " setDetail = ? ,"
					+ " teachingExp = ? ," + " corporateExp = ? ," + " ngasceExp = ? ," + " cvUrl = ? ,"
					+ " imgUrl = ? ," + " subjectPref1 = ? , " + " subjectPref2 = ? , " + " subjectPref3 = ? ,"
					+ " currentOrganization = ? ," + " designation = ? ," + " natureOfAppointment = ?,"
					+ " areaOfSpecialisation = ?," + " otherAreaOfSpecialisation = ?," + " aadharNumber = ?,"
					+ " approvedInSlab = ?," + " dateOfECMeetingApprovalTaken = ?,"
					+ " consentForMarketingCollateralsOrPhotoAndProfileRelease = ?,"
					+ " consentForMarketingCollateralsOrPhotoAndProfileReleaseReason = ?," + " honorsAndAwards = ?,"
					+ " memberships = ?," + " researchInterest = ?," + " articlesPublishedInInternationalJournals = ?,"
					+ " articlesPublishedInNationalJournals = ?," + " summaryOfPapersPublishedInABDCJournals = ?,"
					+ " paperPresentationsAtInternationalConference = ?,"
					+ " paperPresentationAtNationalConference = ?," + " caseStudiesPublished = ?,"
					+ " booksPublished = ?," + " bookChaptersPublished = ?," + " listOfPatents = ?,"
					+ " consultingProjects = ?," + " researchProjects = ?," + " programGroup = ?," + " programName = ?,"
					+ " lastModifiedBy = ?," + " lastModifiedDate = sysdate()"

					+ " where facultyId= ? ";

			jdbcTemplate.update(updateSql, new PreparedStatementSetter(){
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,faculty.getTitle());
					preparedStatement.setString(2,faculty.getFirstName());
					preparedStatement.setString(3,faculty.getMiddleName());
					preparedStatement.setString(4,faculty.getLastName());
					preparedStatement.setString(5,faculty.getMobile());
					preparedStatement.setString(6,faculty.getEmail());
					preparedStatement.setString(7,faculty.getDob());
					preparedStatement.setString(8,faculty.getSecondaryEmail());
					preparedStatement.setString(9,faculty.getAltContact());
					preparedStatement.setString(10,faculty.getOfficeContact());
					preparedStatement.setString(11,faculty.getHomeContact());
					preparedStatement.setString(12,faculty.getLocation());
					preparedStatement.setString(13,faculty.getAddress());
					preparedStatement.setString(14,faculty.getGraduationDetails());
					preparedStatement.setString(15,faculty.getYearOfPassingGraduation());
					preparedStatement.setString(16,faculty.getPhdDetails());
					preparedStatement.setString(17,faculty.getYearOfPassingPhd());
					preparedStatement.setString(18,faculty.getNet());
					preparedStatement.setString(19,faculty.getSetDetail());
					preparedStatement.setString(20,faculty.getTeachingExp());
					preparedStatement.setString(21,faculty.getCorporateExp());
					preparedStatement.setString(22,faculty.getNgasceExp());
					preparedStatement.setString(23,faculty.getCvUrl());
					preparedStatement.setString(24,faculty.getImgUrl());
					preparedStatement.setString(25,faculty.getSubjectPref1());
					preparedStatement.setString(26,faculty.getSubjectPref2());
					preparedStatement.setString(27,faculty.getSubjectPref3());
					preparedStatement.setString(28,faculty.getCurrentOrganization());
					preparedStatement.setString(29,faculty.getDesignation());
					preparedStatement.setString(30,faculty.getNatureOfAppointment());
					preparedStatement.setString(31,faculty.getAreaOfSpecialisation());
					preparedStatement.setString(32,faculty.getOtherAreaOfSpecialisation());
					preparedStatement.setString(33,faculty.getAadharNumber());
					preparedStatement.setString(34,faculty.getApprovedInSlab());
					preparedStatement.setString(35,faculty.getDateOfECMeetingApprovalTaken());
					preparedStatement.setString(36,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileRelease());
					preparedStatement.setString(37,faculty.getConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason());
					preparedStatement.setString(38,faculty.getHonorsAndAwards());
					preparedStatement.setString(39,faculty.getMemberships());
					preparedStatement.setString(40,faculty.getResearchInterest());
					preparedStatement.setString(41,faculty.getArticlesPublishedInInternationalJournals());
					preparedStatement.setString(42,faculty.getArticlesPublishedInNationalJournals());
					preparedStatement.setString(43,faculty.getSummaryOfPapersPublishedInABDCJournals());
					preparedStatement.setString(44,faculty.getPaperPresentationsAtInternationalConference());
					preparedStatement.setString(45,faculty.getPaperPresentationAtNationalConference());
					preparedStatement.setString(46,faculty.getCaseStudiesPublished());
					preparedStatement.setString(47,faculty.getBooksPublished());
					preparedStatement.setString(48,faculty.getBookChaptersPublished());
					preparedStatement.setString(49,faculty.getListOfPatents());
					preparedStatement.setString(50,faculty.getConsultingProjects());
					preparedStatement.setString(51,faculty.getResearchProjects());
					preparedStatement.setString(52,faculty.getProgramGroup());
					preparedStatement.setString(53,faculty.getProgramName());
					preparedStatement.setString(54,faculty.getLastModifiedBy());
					preparedStatement.setString(55,faculty.getFacultyId() );
					
				}});
		}

	}
	
	@Transactional(readOnly = true) 
	public int checkIfEmailPresent(String emailId,String facultyId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(email) from acads.faculty where email = ? and facultyId not in (?) and active = 'Y' ";
		int count=jdbcTemplate.query(sql,new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,emailId);
				preparedStatement.setString(2,facultyId);
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
		
		
		return count;		 
	}	
	
	@Transactional(readOnly = true) 
	public int checkIfDuplicateMobilePresent(String mobileNumber,String facultyId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(mobile) from acads.faculty where mobile = ? and facultyId not in (?) and active = 'Y' ";
		int count=jdbcTemplate.query(sql,new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,mobileNumber);
				preparedStatement.setString(2,facultyId);
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
		
		
		return count;		 
	}
	
	@Transactional(readOnly = true)
	public List<FacultyCourseBean> getallFacultyCourseList(boolean subjectCode){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuffer  sql = new StringBuffer("SELECT * FROM acads.faculty_course ");
		if(subjectCode)
			sql.append("where subjectcode = ''  ");
		List<FacultyCourseBean> programNameList = (ArrayList<FacultyCourseBean>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(FacultyCourseBean.class));
		return programNameList;
	}
	
	@Transactional(readOnly = false)
	public int deleteFacultySubjectBYMonthAndYear(String facultyId,String year,String month,String subject) {
		String sql = "Delete from acads.faculty_course where facultyId = ? and year = ? and month = ? and subject = ? and subjectcode = '' ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.update(sql, new Object[] { 
				facultyId,year,month,subject
		});
	}
	
	@Transactional(readOnly = false)
	public int deleteFacultyCourseBYMonthAndYear(String facultyId,String year,String month,String subjectcode) {
		String sql = "Delete from acads.faculty_course where facultyId = ? and year = ? and month = ? and subjectcode = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.update(sql, new Object[] { 
				facultyId,year,month,subjectcode
		});
	}
	
	@Transactional(readOnly = true)
	public List<String> getSubjectCodeBySubjectName(String subjectname){
		String sql = " SELECT subjectcode FROM exam.mdm_subjectcode WHERE subjectname = ? ";
		List<String> subjectName = ( ArrayList<String> ) jdbcTemplate.query(sql,new Object[]{subjectname},new SingleColumnRowMapper(String.class));
		return subjectName;
	}
	
	@Transactional(readOnly = false)
	public int upsertFacultyCourse(FacultyCourseBean bean,List<String> subjectcodes) {

		StringBuffer sql = new StringBuffer("INSERT INTO acads.faculty_course(year, month, facultyId, subject, createdBy, createdDate, lastModifiedBy,");
				sql.append(" lastModifiedDate,subjectcode) VALUES ");
				sql.append(" (?,?,?,?,?,?,?,sysdate(),?) ");
		try{
			int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					String subjectcode = subjectcodes.get(i);
					ps.setString(1,bean.getYear());
					ps.setString(2,bean.getMonth());
					ps.setString(3,bean.getFacultyId());
					ps.setString(4,bean.getSubject());
					ps.setString(5, bean.getCreatedBy());
					if(StringUtils.isBlank(bean.getCreatedBy()))
						ps.setString(6,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					else
						ps.setString(6,bean.getCreatedDate().toString() );
				
					ps.setString(7,bean.getLastModifiedBy());
					ps.setString(8,subjectcode);
				}
				@Override
				public int getBatchSize() {
					return subjectcodes.size();
				}
			});
			
			return batchUpdateDocumentRecordsResultSize.length;
		}catch(Exception e){
			 // e.printStackTrace();
		}
		return 0;
	}
	
	@Transactional(readOnly = true)
	public Map<String,FacultyAcadsBean> getAllFacultyMap(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);

		String sql= "select * from acads.faculty  ";
		ArrayList<FacultyAcadsBean> facultyNameList= (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(FacultyAcadsBean.class));
		Map<String,FacultyAcadsBean> facultyIdMap = new HashMap<String,FacultyAcadsBean>();
		for(FacultyAcadsBean faculty : facultyNameList){
			facultyIdMap.put(faculty.getFacultyId(), faculty);
		}

		return facultyIdMap;
	}

}
