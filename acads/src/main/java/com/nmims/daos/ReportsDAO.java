package com.nmims.daos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.LoginBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.helpers.PaginationHelper;
import com.sforce.ws.wsdl.Part;


public class ReportsDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;

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

	@Transactional(readOnly = true)
	public StudentAcadsBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentAcadsBean student = null;
		try{
			String sql = "select * from exam.students where sapid = ? ";
			student = (StudentAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(StudentAcadsBean.class));
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		}catch(Exception e){
			  
		}
		return student;
	} 
	/*@SuppressWarnings("rawtypes")
	public List<PassFailBean> getProgramCompletedReport(StudentMarksBean studentMarks, String programType, 
			int numberOfSubjectsToPass, String programStructure, boolean isLateral){

		ArrayList<Object> parameters = new ArrayList<Object>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where ispass = 'Y' group by sapid "
				+ " having (count(subject) = ?)"
				+ " and sapid not in (select distinct sapid from exam.passfail where ispass = 'N')"
				+ " and program like ? "
				+ " and sapid in (select distinct sapid from exam.students where PrgmStructApplicable = ? ";
		
		String sql = "select * from exam.passfail where ispass = 'Y' and program like ? group by sapid "
				+ " having (count(subject) = ?)"
				+ " and sapid not in (select distinct sapid from exam.passfail where ispass = 'N' and program like ? )"
				+ " and sapid in (select distinct sapid from exam.students where PrgmStructApplicable = ? and program like ? ";

		if(isLateral){
			sql = sql + " and isLateral = 'Y'";
		}
		sql = sql +")";


		parameters.add(programType+"%");
		parameters.add(numberOfSubjectsToPass);
		parameters.add(programType+"%");
		parameters.add(programStructure);
		parameters.add(programType+"%");

		if( studentMarks.getYear() != null  &&   !("".equals(studentMarks.getYear()))  &&
				studentMarks.getMonth() != null  &&   !("".equals(studentMarks.getMonth())) ){
			sql = sql + " and sapid in (select distinct sapid from exam.marks where month = ? and year = ? and program like ?)";
			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
			parameters.add(programType+"%");
		}





		Object[] args = parameters.toArray();

		List<PassFailBean> studentMarksList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(PassFailBean.class));
		return studentMarksList;
	}	

	@SuppressWarnings("rawtypes")
	public List<PassFailBean> getGraceToCompleteProgramReport(StudentMarksBean studentMarks, String programType, 
			int numberOfSubjectsToPass, String programStructure, boolean isLateral){

		ArrayList<Object> parameters = new ArrayList<Object>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		parameters.add(programType+"%");
		parameters.add(numberOfSubjectsToPass);
		parameters.add(programType+"%");
		parameters.add(programStructure);

		
		
		String innerQuery = "select distinct sapid from exam.students where PrgmStructApplicable = ?  "; 
		
		if( studentMarks.getYear() != null  &&   !("".equals(studentMarks.getYear()))  &&
				studentMarks.getMonth() != null  &&   !("".equals(studentMarks.getMonth())) ){
			innerQuery += " and validityendmonth = ? and validityendyear = ? ";
			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
			
			if(isLateral){
				innerQuery = innerQuery + " and isLateral = 'Y' ";
			}
		}
		
		String sql = "select sapid, name, program, cast(total as Unsigned)  t, sum(50 - cast(total as Unsigned)) as gracemarks from exam.passfail "
				+ " where ispass = 'N' and "
				+ " sapid in (select distinct sapid from exam.passfail where program like ? group by sapid having (count(subject) = ?) ) "
				+ " and program like ? "
				+ " and sapid in ( " + innerQuery + " ) "
				+ " group by sapid having gracemarks <= 10 ";

		if( studentMarks.getYear() != null  &&   !("".equals(studentMarks.getYear()))  &&
				studentMarks.getMonth() != null  &&   !("".equals(studentMarks.getMonth())) ){
			sql = sql + " and sapid in (select distinct sapid from exam.marks where month = ? and year = ?) ";
			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
		}



		Object[] args = parameters.toArray();

		List<PassFailBean> studentMarksList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(PassFailBean.class));
		return studentMarksList;
	}	
	

	@SuppressWarnings("unchecked")
	public Page<StudentMarksBean> getStudentMarksPage(int pageNo, int pageSize, StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.marks where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.marks where 1 = 1 ";

		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( studentMarks.getGrno() != null &&   !("".equals(studentMarks.getGrno()))){
			sql = sql + " and grno = ? ";
			countSql = countSql + " and grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getStudentname() != null &&   !("".equals(studentMarks.getStudentname()))){
			sql = sql + " and studentname like  ? ";
			countSql = countSql + " and studentname like  ? ";
			parameters.add("%"+studentMarks.getStudentname()+"%");
		}
		if( studentMarks.getProgram() != null &&   !("".equals(studentMarks.getProgram()))){
			sql = sql + " and program = ? ";
			countSql = countSql + " and program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and sem = ? ";
			countSql = countSql + " and sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if( studentMarks.getAttempt() != null &&   !("".equals(studentMarks.getAttempt()))){
			sql = sql + " and attempt = ? ";
			countSql = countSql + " and attempt = ? ";
			parameters.add(studentMarks.getAttempt());
		}

		sql = sql + " order by sem, subject, program, sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));


		return page;
	}


	public StudentMarksBean findById(String id){

		String sql = "SELECT * FROM exam.marks WHERE id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentMarksBean marksBean = (StudentMarksBean) jdbcTemplate.queryForObject(
				sql, new Object[] { id }, new BeanPropertyRowMapper(StudentMarksBean.class));

		return marksBean;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteStudentMarks(String id) {
		String sql = "Delete from exam.marks where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});

	}

	public void upsertMarks(StudentMarksBean bean, JdbcTemplate jdbcTemplate, String type) {
		boolean recordExists = checkIfRecordExists(bean, jdbcTemplate);
		if(recordExists){
			updateStudentMarksForUpsert(bean, jdbcTemplate, type);
		}else{
			insertStudentMarksForUpsert(bean, jdbcTemplate);
		}
	}



	private boolean checkIfRecordExists(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}

		String sql = "SELECT count(*) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";

		int count = jdbcTemplate.queryForInt(sql, new Object[] { 
				bean.getYear(),
				bean.getMonth(),
				bean.getSapid(),
				bean.getSubject()
		});

		if(count == 0){
			return false;
		}else{
			return true;
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdate(final List<StudentMarksBean> marksBeanList, String type) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < marksBeanList.size(); i++) {
			try{
				StudentMarksBean bean = marksBeanList.get(i);
				upsertMarks(bean, jdbcTemplate, type);
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateTimeTable(final List<TimetableBean> timeTableList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < timeTableList.size(); i++) {
			try{
				TimetableBean bean = timeTableList.get(i);
				upsertTimeTable(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateAssignmentStatus(final List<AssignmentStatusBean> timeTableList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < timeTableList.size(); i++) {
			try{
				AssignmentStatusBean bean = timeTableList.get(i);
				upsertAssignmentStatus(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}

	private void upsertAssignmentStatus(AssignmentStatusBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.assignmentStatus "
				+ "(examYear, examMonth, sapid, subject, submitted, createdBy, createdDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,?,sysdate() )"
				+ " on duplicate key update "
				+ "	    examYear = ?,"
				+ "	    examMonth = ?,"
				+ "	    sapid = ?,"
				+ "	    subject = ?,"
				+ "	    submitted = ?,"
				+ "	    createdBy = ?, "
				+ "	    createdDate = sysdate() ";


		String examYear = bean.getExamYear();
		String examMonth = bean.getExamMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String submitted = bean.getSubmitted();
		String createdBy = bean.getCreatedBy();



		jdbcTemplate.update(sql, new Object[] { 
				examYear,
				examMonth,
				sapid,
				subject,
				submitted,
				createdBy,
				examYear,
				examMonth,
				sapid,
				subject,
				submitted,
				createdBy
		});


	}



	private void upsertTimeTable(TimetableBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.timetable (examYear, examMonth, prgmStructApplicable, program,"
				+ "subject, date, startTime, endTime, sem, createdBy, lastModifiedBy,createdDate, lastModifiedDate) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())"
				+ " on duplicate key update "
				+ "	    examYear = ?,"
				+ "	    examMonth = ?,"
				+ "	    prgmStructApplicable = ?,"
				+ "	    program = ?,"
				+ "	    subject = ?,"
				+ "	    date = ?,"
				+ "	    startTime = ?,"
				+ "	    endTime = ?,"
				+ "	    sem = ?,"
				+ "	    createdBy = ?,"
				+ "	    lastModifiedBy = ?,"
				+ "	    lastModifiedDate = sysdate()";






		String examYear = bean.getExamYear();
		String examMonth = bean.getExamMonth();
		String prgmStructApplicable = bean.getPrgmStructApplicable();
		String program = bean.getProgram();
		String subject = bean.getSubject();
		String date = bean.getDate();
		String startTime = bean.getStartTime();
		String endTime = bean.getEndTime();
		String sem = bean.getSem();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();


		jdbcTemplate.update(sql, new Object[] { 
				examYear,
				examMonth,
				prgmStructApplicable,
				program,
				subject,
				date,
				startTime,
				endTime,
				sem,
				createdBy,
				lastModifiedBy,
				examYear,
				examMonth,
				prgmStructApplicable,
				program,
				subject,
				date,
				startTime,
				endTime,
				sem,
				createdBy,
				lastModifiedBy
		});


	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateOldData(final List<StudentMarksBean> marksBeanList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < marksBeanList.size(); i++) {
			try{
				StudentMarksBean bean = marksBeanList.get(i);
				insertStudentMarksForUpsert(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;



	}

	public void isValidEntry(StudentMarksBean bean, JdbcTemplate jdbcTemplate){
		String programsql = "Select count(*) from programs where program = '" + bean.getProgram()+"'";
		String subjectSql = "Select count(*) from subjects where subjectname = '" + bean.getSubject()+"'";

		int validProgramCount = jdbcTemplate.queryForInt(programsql);
		int validSubjectCount = jdbcTemplate.queryForInt(subjectSql);

		ArrayList<String> errorMessageList = new ArrayList<>();
		if(validProgramCount == 0){
			errorMessageList.add("Invalid Program "+bean.getProgram()+" entered for Student "+bean.getSapid()+ " under subject "+bean.getSubject());
		}
	}

	public Page<StudentMarksBean> getAllStudentMarksPage(final int pageNo,	final int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sqlCountRows = "SELECT count(*) FROM exam.marks";
		String sqlFetchRows = "SELECT * FROM exam.marks";

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, sqlCountRows, sqlFetchRows, new Object[]{}, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));


		return page;
	}


	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;

	}


	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT program FROM exam.programs order by program asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate, String type){
		if("written".equalsIgnoreCase(type)){
			updateWrittenMarksForUpsert(m, jdbcTemplate);
		}else{
			updateAsignmentMarksForUpsert(m, jdbcTemplate);
		}


	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateWrittenMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.marks set "
				+ " writenscore=?, "
				+ " grno=?, "
				+ " processed = 'N', "
				+ " lastModifiedBy=?, "
				+ " lastModifiedDate=sysdate() "
				+ " where year =? and month=? and sapid = ? and subject = ?";

		jdbcTemplate.update(sql, new Object[] { 
				m.getWritenscore(),
				m.getGrno(),
				m.getLastModifiedBy(),
				m.getYear(),
				m.getMonth(),
				m.getSapid(),
				m.getSubject()
		});

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAsignmentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.marks set "
				+ "year=?,"
				+ "month=?,"
				//	+ "examorder=?,"
				+ "grno=?,"
				+ "sapid=?,"
				+ "studentname=?,"
				+ "program=?,"
				+ "sem=?,"
				+ "subject=?,"
				+ "assignmentscore=?,"
				+ "gracemarks=?,"
				+ "total=?,"
				+ "attempt=?,"
				+ "source=?,"
				+ "location=?,"
				+ "centercode=?,"
				+ "remarks=?, "
				+ "syllabusYear=?, "
				+ "processed = 'N', "
				+ "lastModifiedBy=?, "
				+ "lastModifiedDate=sysdate() "
				+ " where year =? and month=? and sapid = ? and subject = ?";


		jdbcTemplate.update(sql, new Object[] { 
				m.getYear(),
				m.getMonth(),
				//		getExamOrder(m.getMonth().trim(),m.getYear().trim()),
				m.getGrno(),
				m.getSapid(),
				m.getStudentname(),
				m.getProgram(),
				m.getSem(),
				m.getSubject(),
				m.getAssignmentscore(),
				m.getGracemarks(),
				m.getTotal(),
				m.getAttempt(),
				m.getSource(),
				m.getLocation(),
				m.getCentercode(),
				m.getRemarks(),
				m.getSyllabusYear(),
				m.getLastModifiedBy(),
				m.getYear(),
				m.getMonth(),
				m.getSapid(),
				m.getSubject()
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertStudentMarksForUpsert(StudentMarksBean marksBean, JdbcTemplate jdbcTemplate){

		final String sql = " INSERT INTO exam.marks(		"
				+ "year,"
				+ "month,"
				+ "examorder,"
				+ "grno,"
				+ "sapid,"
				+ "studentname,"
				+ "program,"
				+ "sem,"
				+ "subject,"
				+ "writenscore,"
				+ "assignmentscore,"
				+ "gracemarks,"
				+ "total,"
				+ "attempt,"
				+ "source,"
				+ "location,"
				+ "centercode,"
				+ "remarks,"
				+ "syllabusYear,"
				+ "createdBy,"
				+ "createdDate,"
				+ "lastModifiedBy,"
				+ "lastModifiedDate)"
				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, m.getYear());
				ps.setString(2, m.getMonth());
				ps.setString(3, getExamOrder(m.getMonth().trim(),m.getYear().trim()));
				ps.setString(4, m.getGrno());
				ps.setString(5, m.getSapid());
				ps.setString(6, m.getStudentname());
				ps.setString(7, m.getProgram());
				ps.setString(8, m.getSem());
				ps.setString(9, m.getSubject());
				ps.setString(10, m.getWritenscore());
				ps.setString(11, m.getAssignmentscore());
				ps.setString(12, m.getGracemarks());
				ps.setString(13, m.getTotal());
				ps.setString(14, m.getAttempt());
				ps.setString(15, m.getSource());
				ps.setString(16, m.getLocation());
				ps.setString(17, m.getCentercode());
				ps.setString(18, m.getRemarks());
				ps.setString(19, m.getSyllabusYear());
				ps.setString(20, m.getCreatedBy());
				ps.setString(21, m.getLastModifiedBy());

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();


		jdbcTemplate.update(psc, keyHolder);

		int id = keyHolder.getKey().intValue();
		return id;

	}

	public List<ExamOrderBean> getExamsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examorder order by examorder.order asc";
		List<ExamOrderBean> examsList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamOrderBean.class));
		return examsList;
	}

	public List<TimetableBean> getTimetableList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by program, prgmStructApplicable, sem, date, startTime asc";
		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamStats(ExamOrderBean exam){
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
	public void updateTimetableStats(ExamOrderBean exam){
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

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAStudentsMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and "
				+ " b.live = 'Y' and a.sapid = ? order by a.subject, a.examorder desc";

				if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and a.sapid = ? order by a.examorder desc";
			parameters.add(studentMarks.getSapid());
		}
		Object[] args = parameters.toArray();

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAStudentsMostRecentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where live='Y') order by sem, subject asc";


		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> searchSingleStudentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		 * 
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";
		String countSql = "SELECT count(*) FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";

		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and a.year = ? ";
			countSql = countSql + " and a.year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and a.month = ? ";
			countSql = countSql + " and a.month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and a.sapid = ? ";
			countSql = countSql + " and a.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and a.sem = ? ";
			countSql = countSql + " and a.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and a.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}

		sql = sql + " order by sem, subject asc";

		Object[] args = parameters.toArray();

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,args, new BeanPropertyRowMapper(StudentMarksBean.class));

		return studentsMarksList;
	}

	public String getMostRecentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map> rows = jdbcTemplate.queryForList(sql);
		hashMap = new HashMap<String, Integer>();
		for (Map row : rows) {
			recentPeriod = (String)row.get("month")+"-"+(String)row.get("year");
		}
		return recentPeriod;
	}

	public String getMostRecentTimeTablePeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map> rows = jdbcTemplate.queryForList(sql);
		hashMap = new HashMap<String, Integer>();
		for (Map row : rows) {
			recentPeriod = (String)row.get("month")+"-"+(String)row.get("year");
		}
		return recentPeriod;
	}

	public String getRecentExamDeclarationDate() {

		String declareDate = null;
		Date d = new Date();
		final String sql = "Select declareDate from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map> rows = jdbcTemplate.queryForList(sql);
		hashMap = new HashMap<String, Integer>();
		for (Map row : rows) {
			d = (Date)row.get("declareDate");
		}
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			declareDate = sdfr.format(d);
		} catch (Exception e) {
			declareDate = "";
		}


		return declareDate;
	}
	public String getStudentCenterDetails(String sapId) {
		String centerCode = null;
		//final String sql = "Select centerCode, centerName from exam.students where sapid = ? and students.sem = (Select max(students.sem) from exam.students where sapid=?)";

		final String tempSql = "Select centerCode, centerName from exam.student_center where sapid = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map> rows = jdbcTemplate.queryForList(tempSql,new Object[] { 
				sapId
		});
		hashMap = new HashMap<String, Integer>();
		for (Map row : rows) {
			centerCode = (String)row.get("centerCode");
			break;
		}
		return centerCode;
	}

	public ArrayList<CenterBean> getAllCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers where active = '1' order by centerCode asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterBean> centers = (ArrayList<CenterBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterBean.class));
		return centers;
	}

	public void updateStudentCenter(String centerCode, String centerName, String sapId) {
		String sql = "Update exam.students set "
				+ "centerCode=? ,"
				+ "centerName=? "
				+ " where sapid = ? ";


		String tempSql = "Insert into exam.student_center (sapid, centerCode, centerName) values (?, ?, ?)";


		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(tempSql, new Object[] { 
				sapId,
				centerCode,
				centerName

		});

	}

	public ArrayList<String> batchUpsertStudentMaster(List<StudentBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < studentList.size(); i++) {
			try{
				StudentBean bean = studentList.get(i);
				upsertStudenMaster(bean, jdbcTemplate);
			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;
	}

	private void upsertStudenMaster(StudentBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.students (sapid, sem, lastName, firstName,"
				+ "middleName, fatherName, husbandName, motherName, gender, program, enrollmentMonth, enrollmentYear,"
				+ "emailId, mobile, altPhone, dob, regDate, isLateral, isReReg, address, city, state, "
				+ "country, pin, centerCode, centerName, validityEndMonth, validityEndYear, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate,PrgmStructApplicable) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(), ?)"
				+ " on duplicate key update "
				+ "	    lastName = ?,"
				+ "	    firstName = ?,"
				+ "	    middleName = ?,"
				+ "	    fatherName = ?,"
				+ "	    husbandName = ?,"
				+ "	    motherName = ?,"
				+ "	    gender = ?,"
				+ "	    program = ?,"
				+ "	    enrollmentMonth = ?,"
				+ "	    enrollmentYear = ?,"
				+ "	    emailId = ?,"
				+ "	    mobile = ?,"
				+ "	    altPhone = ?,"
				+ "	    dob = ?,"
				+ "	    regDate = ?,"
				+ "	    isLateral = ?,"
				+ "	    isReReg = ?,"
				+ "	    address = ?,"
				+ "	    city = ?,"
				+ "	    state = ?,"
				+ "	    country = ?,"
				+ "	    pin = ?,"
				+ "	    centerCode = ?,"
				+ "	    centerName = ?,"
				+ "	    validityEndMonth = ?,"
				+ "	    validityEndYear = ?,"
				+ "	    lastModifiedBy = ?,"
				+ "	    lastModifiedDate = sysdate(),"
				+ "		PrgmStructApplicable = ?";





		String sapid = bean.getSapid();
		String sem = bean.getSem();
		String lastName = bean.getLastName();
		String firstName = bean.getFirstName();
		String middleName = bean.getMiddleName();
		String fatherName = bean.getFatherName();
		String husbandName = bean.getHusbandName();
		String motherName = bean.getMotherName();
		String gender = bean.getGender();
		String program = bean.getProgram();
		String enrollmentMonth = bean.getEnrollmentMonth();
		String enrollmentYear = bean.getEnrollmentYear();
		String emailId = bean.getEmailId();
		String mobile = bean.getMobile();
		String altPhone = bean.getAltPhone();
		String dob = bean.getDob();
		String regDate = bean.getRegDate();
		String isLateral = bean.getIsLateral();
		String isReReg = bean.getIsReReg();
		String address = bean.getAddress();
		String city = bean.getCity();
		String state = bean.getState();
		String country = bean.getCountry();
		String pin = bean.getPin();
		String centerCode = bean.getCenterCode();
		String centerName = bean.getCenterName();
		String validityEndMonth = bean.getValidityEndMonth();
		String validityEndYear = bean.getValidityEndYear();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String PrgmStructApplicable = bean.getPrgmStructApplicable();

		jdbcTemplate.update(sql, new Object[] { 
				sapid,
				sem,
				lastName,
				firstName,
				middleName,
				fatherName,
				husbandName,
				motherName,
				gender,
				program,
				enrollmentMonth,
				enrollmentYear,
				emailId,
				mobile,
				altPhone,
				dob,
				regDate,
				isLateral,
				isReReg,
				address,
				city,
				state,
				country,
				pin,
				centerCode,
				centerName,
				validityEndMonth,
				validityEndYear,
				createdBy,
				lastModifiedBy,
				PrgmStructApplicable,
				lastName,
				firstName,
				middleName,
				fatherName,
				husbandName,
				motherName,
				gender,
				program,
				enrollmentMonth,
				enrollmentYear,
				emailId,
				mobile,
				altPhone,
				dob,
				regDate,
				isLateral,
				isReReg,
				address,
				city,
				state,
				country,
				pin,
				centerCode,
				centerName,
				validityEndMonth,
				validityEndYear,
				lastModifiedBy,
				PrgmStructApplicable
		});


	}

	public void execute(String sql) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);

	}

	public HashMap<String, String> getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.programs";
		List<ProgramBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramBean.class));
		HashMap<String, String> programCodeNameMap = new HashMap<String, String>();

		for (int i = 0; i < programList.size(); i++) {
			programCodeNameMap.put(programList.get(i).getProgram(), programList.get(i).getProgramname());
		}

		return programCodeNameMap;
	}


	@SuppressWarnings("unchecked")
	public Page<AssignmentStatusBean> getAssignmentStatusPage(int pageNo, int pageSize, AssignmentStatusBean assignmentStatus) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.assignmentStatus where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.assignmentStatus where 1 = 1 ";

		if( assignmentStatus.getExamYear() != null &&   !("".equals(assignmentStatus.getExamYear()))){
			sql = sql + " and examYear = ? ";
			countSql = countSql + " and examYear = ? ";
			parameters.add(assignmentStatus.getExamYear());
		}

		if( assignmentStatus.getExamMonth() != null &&   !("".equals(assignmentStatus.getExamMonth()))){
			sql = sql + " and examMonth = ? ";
			countSql = countSql + " and examMonth = ? ";
			parameters.add(assignmentStatus.getExamMonth());
		}

		if( assignmentStatus.getSapid() != null &&   !("".equals(assignmentStatus.getSapid()))){
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(assignmentStatus.getSapid());
		}

		if( assignmentStatus.getSubject() != null &&   !("".equals(assignmentStatus.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(assignmentStatus.getSubject());
		}


		sql = sql + " order by sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentStatusBean> pagingHelper = new PaginationHelper<AssignmentStatusBean>();
		Page<AssignmentStatusBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentStatusBean.class));


		return page;
	}
	
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedBookings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ExamCenterSlotMappingBean getSlotsBooked() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sum(COALESCE(booked, 0)) booked , sum(COALESCE(onHold, 0)) onHold from exam.examcenter_slot_mapping a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";


		ExamCenterSlotMappingBean bean = (ExamCenterSlotMappingBean) jdbcTemplate.queryForObject(sql, new Object[]{}, new BeanPropertyRowMapper(ExamCenterSlotMappingBean.class));
		return bean;
	}
	
	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and a.booked = 'Y' "
				+ " and a.sapid = s.sapid"
				+ " group by a.sapid, a.subject "
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	public ArrayList<ExamBookingTransactionBean> getConfirmedOnlineExamBookingForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and a.booked = 'Y' "
				+ " and examMode = 'Online' "
				+ " and a.sapid = s.sapid"
				+ " group by a.sapid, a.subject "
				+ " order by a.sapid,  a.examDate,  a.examTime, a.sem, a.program, a.subject , a.centerId asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	public HashMap<String, String> getStudentExamPasswordsForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT sapid,  substring(max(trackid), 12, 8) password FROM exam.exambookings "
				+ " where booked = 'Y' and year = ? and month = ? "
				+ " group by sapid order by sapid";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		
		HashMap<String, String> studentPasswordMap = new HashMap<>();
		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			studentPasswordMap.put(bean.getSapid(), bean.getPassword());
		}
		return studentPasswordMap;
	}
	
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBookingForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL' or a.booked = 'RF') "
				+ " and a.sapid = s.sapid "
				+ " group by a.sapid, a.subject, a.booked "
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	public ArrayList<ExamBookingTransactionBean> getDatewiseAmount(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sum(amount) amount, date(respTranDateTime) respTranDateTime from ( "
				+ "  select distinct trackid, amount, paymentmode , respTranDateTime from exam.exambookings "
				+ " where (booked = 'Y' or booked = 'RL' or booked = 'RF') and paymentmode = 'Online' and year = ?  and month = ? ) abc group by date(respTranDateTime) ";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBookingForGivenYearMonth(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL')  "
				+ " and a.sapid = s.sapid"
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{fileBean.getYear(), fileBean.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	public ArrayList<ExamBookingTransactionBean> getExpiredBookingsForGivenYearMonth(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and a.booked <> 'Y' and a.booked <> 'RL' and a.booked <> 'RF' and paymentMode = 'Online'  "
				+ " and a.sapid = s.sapid"
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		//sql = "select * from exam.exambookings where booked <> 'Y' and a.booked <> 'RL' and paymentMode = 'Online' and a.year = ? and a.month = ? ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{fileBean.getYear(), fileBean.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		//ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	public ArrayList<ExamBookingTransactionBean> getDDBookings(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a  where a.year = ? and a.month =? "
				+ " and paymentMode = 'DD' ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{
				studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	
	public ArrayList<ExamCenterSlotMappingBean> getCenterBookings(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.examcenterId, a.date, a.starttime, a.capacity, a.year, a.month, COALESCE(a.onhold, 0) onHold, "
				+ " COALESCE(a.booked, 0) booked, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available,"
				+ " b.examCenterName, b.city, b.address  "
				+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b where b.centerId = a.examcenterId "
				+ "and a.year = ? and a.month = ? order by  a.examcenterId, a.date asc";

		
		ArrayList<ExamCenterSlotMappingBean> centerBookingsList = (ArrayList<ExamCenterSlotMappingBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamCenterSlotMappingBean.class));
		return centerBookingsList;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AssignmentStatusBean> getAssignmentSubmittedList(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM exam.assignmentStatus a, exam.students s where a.sapid = s.sapid and submitted = 'Y' ";

		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and a.examYear = ? ";
			parameters.add(studentMarks.getYear());
		}
		
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and a.examMonth = ? ";
			parameters.add(studentMarks.getMonth());
		}
		
		

		sql = sql + " group by a.sapid, subject order by a.sapid, a.subject asc";
		Object[] args = parameters.toArray();

		ArrayList<AssignmentStatusBean> studentsList = (ArrayList<AssignmentStatusBean>)jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		
		return studentsList;
	}

	public ArrayList<ExamBookingTransactionBean> getQuestionPaperCountForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(subject) subjectCount, subject,  eb.centerid, examdate, examtime , examcentername, "
				+ " city, address, program , eb.year , eb.month "
				+ " from exam.exambookings eb, exam.examcenter ec where booked = 'Y' and exammode = 'Offline' "
				+ " and eb.centerId = ec.centerid and eb.year = ? and eb.month = ? "
				+ " group by subject, centerId, examdate, examtime order by ec.city, eb.centerId";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	*/
	
	@Transactional(readOnly = true)
	public List<ParticipantReportBean> getTodaysWebinarIdsForReport(String startDate, String endDate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ParticipantReportBean> webinarIdList=new ArrayList<ParticipantReportBean>();
		String sql="select * from (select ssm.subjectCodeId,s.meetingKey as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  s.date between ? AND ? and s.meetingKey IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId " + 
				"UNION ALL " + 
				"select ssm.subjectCodeId,s.altMeetingKey as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  s.date between ? AND ? and s.altMeetingKey IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId " + 
				"UNION ALL " + 
				"select ssm.subjectCodeId,s.altMeetingKey2 as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  s.date between ? AND ? and s.altMeetingKey2 IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId " + 
				"UNION ALL " + 
				"select ssm.subjectCodeId,s.altMeetingKey3 as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  s.date between ? AND ? and s.altMeetingKey3 IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId) " + 
				"ss where webinarId not  in (select webinarId from acads.webinar_participants_logs where status = 'success')";
		webinarIdList=jdbcTemplate.query(sql,new Object[] {startDate,endDate,startDate,endDate,startDate,endDate,startDate,endDate}, new BeanPropertyRowMapper<ParticipantReportBean>(ParticipantReportBean.class));
		return webinarIdList;
	}
	
	@Transactional(readOnly = true)
	public List<ParticipantReportBean> getTodaysWebinarIdsForBatchJob() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ParticipantReportBean> webinarIdList=new ArrayList<ParticipantReportBean>();
		String sql="select * from (select ssm.subjectCodeId,s.meetingKey as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3  and s.meetingKey IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId  " + 
				"UNION ALL " + 
				"select ssm.subjectCodeId,s.altMeetingKey as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 and s.altMeetingKey IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId " + 
				"UNION ALL " + 
				"select ssm.subjectCodeId,s.altMeetingKey2 as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where  (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 and s.altMeetingKey2 IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId " + 
				"UNION ALL   " + 
				"select ssm.subjectCodeId,s.altMeetingKey3 as webinarId,s.sessionType, s.id as sessionId,s.date as sessionDate,s.year,s.month from acads.sessions s  inner join acads.session_subject_mapping ssm on s.id=ssm.sessionId where (time_to_sec(timediff(sysdate(), cast(concat(date,' ',endTime) as datetime))) > 1800) and DATEDIFF(cast(concat(date,' ',endTime) as datetime), sysdate()) > -3 and s.altMeetingKey3 IS NOT NULL group by ssm.sessionId,ssm.subjectCodeId)  ss where webinarId not  in (select webinarId from acads.webinar_participants_logs where status = 'success')";
		webinarIdList=jdbcTemplate.query(sql,new Object[] {}, new BeanPropertyRowMapper<ParticipantReportBean>(ParticipantReportBean.class));
		return webinarIdList;
	}
	
	@Transactional(readOnly = true)
	public String getsapIdforWebinarParticipant(String emailId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select sapid from exam.students where emailId=?";
		String sapId=(String)jdbcTemplate.queryForObject(sql, new Object[] {emailId},String.class);
		return sapId;
	}
	
	public void insertParticipantReport( ParticipantReportBean participantReportBean, String webinarId,String sessionId,String sessionDate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//int duration=(participantReportBean.getDuration()/60);
		
			String sql="insert ignore into acads.webinar_participants_details(participant_id, participant_user_id, name, user_email, sapId, join_time, leave_time, duration,webinarId,sessionId,sessionDate,subjectCodeId,acadDateFormat) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//on duplicate key update participant_id=?
			jdbcTemplate.update(sql, new Object[] {participantReportBean.getId(), participantReportBean.getUser_id(), 
					participantReportBean.getName(), participantReportBean.getUser_email(), participantReportBean.getSapId(),
					participantReportBean.getJoin_time(), participantReportBean.getLeave_time(),participantReportBean.getDuration(), 
					webinarId,sessionId,sessionDate,participantReportBean.getSubjectCodeId(),participantReportBean.getAcadDateFormat()
					});
	}
	
	public void insertparticipantsLogs(String webinarId, int participants_count, int inserted_count) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String status="failed";
		if(participants_count==inserted_count) {
			status="success";
		}
		String sql="insert into acads.webinar_participants_logs(webinarId, participants_count, inserted_count, status) values(?,?,?,?) on duplicate key update status='success'";
		jdbcTemplate.update(sql, new Object [] {webinarId,participants_count, inserted_count, status});
	}	
	
	public List<ParticipantReportBean> getTotalDuration(String acadDateFormat, SessionDayTimeAcadsBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);		
		String sql="select sapId,webinarId,sum(duration) as totalDuration from acads.webinar_participants_details where acadDateFormat=? ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(acadDateFormat);
		if ((searchBean.getDateFrom() != null && searchBean.getDateTo() != null) && (!"".equals(searchBean.getDateFrom()) && !"".equals(searchBean.getDateTo()))) {
			sql= sql+"  and sessionDate between ? AND ? ";
			parameters.add(searchBean.getDateFrom());
			parameters.add(searchBean.getDateTo());
		}
		
		if ((searchBean.getDateFrom() != null && searchBean.getDateTo() == null) || (!"".equals(searchBean.getDateFrom()) && "".equals(searchBean.getDateTo()))) {
			sql= sql+"  and sessionDate between ? AND curdate() ";
			parameters.add(searchBean.getDateFrom());
		}
		
		if ((searchBean.getDateFrom() == null && searchBean.getDateTo() != null) || ("".equals(searchBean.getDateFrom()) && !"".equals(searchBean.getDateTo()))) {
			sql= sql+"  and sessionDate <= ? ";
			parameters.add(searchBean.getDateTo());
		}
		
		sql=sql+" group by sapId,webinarId";
		Object[] args = parameters.toArray();

		List<ParticipantReportBean> list=jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<ParticipantReportBean>(ParticipantReportBean.class));
		return list;
	}
	
	public List<ParticipantReportBean> getSessionReportDetailsBySearchFilters(SessionDayTimeAcadsBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql="SELECT spd.sapId,r.sem,spd.name,s.subject,s.sessionName, spd.join_time,spd.leave_time,ROUND((spd.duration / 60), 3) AS duration, spd.webinarId, spd.sessionId " + 
				"FROM acads.webinar_participants_details spd INNER JOIN " + 
				" acads.sessions s ON spd.sessionId = s.id " + 
				" inner join exam.registration r " + 
				"on spd.sapId=r.sapId WHERE 1 = 1 ";
			
			if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
				sql= sql+" and s.year=? ";
						parameters.add(searchBean.getYear());
			}
			
			if (searchBean.getMonth() != null && !("".equals(searchBean.getMonth()))) {
				String month=searchBean.getMonth();
				if("Oct".equalsIgnoreCase(searchBean.getMonth()) || "Apr".equalsIgnoreCase(searchBean.getMonth())) {
				month=searchBean.getMonth().replace("Apr", "Jan");
				month=searchBean.getMonth().replace("Oct", "Jul");
				}
				sql= sql+" and s.month=? ";
				parameters.add(month);
				
				sql = sql + " AND r.month = ? ";
				parameters.add(searchBean.getMonth());
			}
			
			if (searchBean.getSubjectCodeId() != null) {
				sql= sql+" and spd.subjectCodeId=? ";
						parameters.add(searchBean.getSubjectCodeId());
			}
			
			if ((searchBean.getDateFrom() != null && searchBean.getDateTo() != null) && (!"".equals(searchBean.getDateFrom()) && !"".equals(searchBean.getDateTo()))) {
				sql= sql+"  and spd.sessionDate between ? AND ? ";
				parameters.add(searchBean.getDateFrom());
				parameters.add(searchBean.getDateTo());
			}
			
			if ((searchBean.getDateFrom() != null && searchBean.getDateTo() == null) || (!"".equals(searchBean.getDateFrom()) && "".equals(searchBean.getDateTo()))) {
				sql= sql+"  and spd.sessionDate between ? AND curdate() ";
				parameters.add(searchBean.getDateFrom());
			}


			
			if ((searchBean.getDateFrom() == null && searchBean.getDateTo() != null) || ("".equals(searchBean.getDateFrom()) && !"".equals(searchBean.getDateTo()))) {
				sql= sql+"  and spd.sessionDate <= ? ";
				parameters.add(searchBean.getDateTo());
			}
			
			sql=sql+" AND spd.sapId <> 'NA' GROUP BY spd.sapId,spd.join_time";
			
			Object[] args = parameters.toArray();
			
			List<ParticipantReportBean> list = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<ParticipantReportBean>(ParticipantReportBean.class));
			
		return list;
	}
}
