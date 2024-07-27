package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ConfigurationExam;
import com.nmims.beans.EMBABatchSubjectBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TEEResultStudentDetailsBean;
import com.nmims.beans.TimetableBean;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.PaginationHelper;
import com.nmims.services.ProjectStudentEligibilityService;


public class StudentMarksDAO extends BaseDAO{
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentMarksDAO.class);//
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private static HashMap<String, BigDecimal> hashMap = null;
	
	//start - DB Constants - added on 2022-02-25 by Vilpesh
	
	private static final String DB_CURRENT_TIMESTAMP = "current_timestamp";
	private static final String MARKS_NOTPROCESSED = "N";
	//end - DB Constants
	
	@Value("${TEST_USER_SAPIDS}")
	private String TEST_USER_SAPIDS;
	
	@Value("${PROJECT_APPLICABLE_PROGRAM_SEM_LIST}")
	private String PROJECT_APPLICABLE_PROGRAM_SEM_LIST;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	//added  on 13/4/2018
	@Transactional(readOnly = true)// 
	public ArrayList<String> getProgramStructureList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " Select distinct ep.programStructure from  exam.programs ep order by ep.programStructure ";

		ArrayList<String> programStructureList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programStructureList;
	}
	//ends
	
	@Transactional(readOnly = true)//
	public HashMap<String, BigDecimal> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
			hashMap = new HashMap<String, BigDecimal>();
			for (ExamOrderExamBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear(), BigDecimal.valueOf(Double.parseDouble(row.getOrder())));
			}
		}

		return hashMap;
	}
	
	@Transactional(readOnly = true)//
	public List<ExamOrderExamBean> getLiveFlagDetails(){
		List<ExamOrderExamBean> liveFlagList = new ArrayList<ExamOrderExamBean>();

		final String sql = " Select * from exam.examorder order by examorder.order ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		liveFlagList = (ArrayList<ExamOrderExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));

		return liveFlagList;
	}
	
	@Transactional(readOnly = true)//
	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Integer.class);


		}catch(Exception e){
			//
		}
		
		return examOrder;
		
	}
	
	@Transactional(readOnly = true)//
	public boolean isResultLiveForLastAssignmentSubmissionCycle(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.examorder eo where eo.order = "
				+ "(select max(examorder.order)-0.5 from exam.examorder where resitAssignmentLive = 'Y')";
		
		ExamOrderExamBean examOrder = (ExamOrderExamBean) jdbcTemplate.queryForObject(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		if("Y".equals(examOrder.getLive())){
			return true;
		}else{
			return false;
		}
		
	}
	
	/*public double getMaxOrderWhereResultLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where live = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Integer.class);


		}catch(Exception e){
			//
		}
		
		return examOrder;
		
	}
	
	public double getOrderForGivenYearMonth(String year, String month){
		return getExamOrderMap().get( month + year).doubleValue();
		
	}*/

	public String getExamOrder(String month, String year) throws SQLException{

		String examOrder = "0";
		HashMap<String, BigDecimal> hashMap = getExamOrderMap();

		BigDecimal examOrderDecimal = hashMap.get(month+year);
		BigDecimal toSubtract;

		if(examOrderDecimal != null){
			examOrder = examOrderDecimal.toString();

			return examOrder;
		}else {
			BigDecimal tempExamOrderDecimal = hashMap.get("Dec"+year);
			if("Jan".equals(month)) {
				toSubtract= new BigDecimal("1.9");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("Feb".equals(month)) {
				toSubtract= new BigDecimal("1.8");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("Mar".equals(month)) {
				toSubtract= new BigDecimal("1.7");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("May".equals(month)) {
				toSubtract= new BigDecimal("1.4");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Jul".equals(month)) {
				toSubtract= new BigDecimal("0.9");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Aug".equals(month)) {
				toSubtract= new BigDecimal("0.8");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Oct".equals(month)) {
				toSubtract= new BigDecimal("0.4");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Nov".equals(month)) {
				toSubtract= new BigDecimal("0.3");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}
		}
		if("0".equals(examOrder)){
			throw new SQLException("Exam order not found");
		}
		return examOrder;
	}
	
	@Transactional(readOnly = true)//
	public List<MarksheetBean> findStudentsFromCommaSeperatedSapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.students where sapid in ( "+ sapid +" ) order by field(sapid, " + sapid + " )";

		return jdbcTemplate.query(sql,new BeanPropertyRowMapper(MarksheetBean.class));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertStudentMarks(StudentMarksBean marksBean){

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


		jdbcTemplate = new JdbcTemplate(dataSource);


		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentMarks(StudentMarksBean m) throws SQLException{
		String sql = "Update exam.marks set "
				+ "year=?,"
				+ "month=?,"
				+ "examorder=?,"
				+ "grno=?,"
				+ "sapid=?,"
				+ "studentname=?,"
				+ "program=?,"
				+ "sem=?,"
				+ "subject=?,"
				+ "writenscore=?,"
				+ "assignmentscore=?,"
				+ "gracemarks=?,"
				+ "total=?,"
				+ "attempt=?,"
				+ "source=?,"
				+ "location=?,"
				+ "centercode=?,"
				+ "remarks=?, "
				+ "processed = 'N', "
				+ "syllabusYear=?, "
				+ "lastModifiedBy=?, "
				+ "lastModifiedDate=sysdate()  "
				+ " where id="+m.getId();



		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				m.getYear(),
				m.getMonth(),
				getExamOrder(m.getMonth().trim(),m.getYear().trim()),
				m.getGrno(),
				m.getSapid(),
				m.getStudentname(),
				m.getProgram(),
				m.getSem(),
				m.getSubject(),
				m.getWritenscore(),
				m.getAssignmentscore(),
				m.getGracemarks(),
				m.getTotal(),
				m.getAttempt(),
				m.getSource(),
				m.getLocation(),
				m.getCentercode(),
				m.getRemarks(),
				m.getSyllabusYear(),
				m.getLastModifiedBy()
		});
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAllStudentMarks(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.marks";
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentMarksList;
	}	
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public int getNumberOfsubjectsCleared(String sapid, boolean isLateral){

		String sql = "Select count(*) from exam.passfail where isPass = 'Y' and sapid = ? ";

		if(isLateral){
			sql = sql + " and (sem = '3' or sem = '4') ";
		}
		

		int numberOfsubjectsCleared = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);

		return numberOfsubjectsCleared;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<StudentMarksBean> getStudentMarksPage(int pageNo, int pageSize, StudentMarksBean studentMarks, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		/* old query
		 * String sql =
		 * "SELECT m.*,psub.sifySubjectCode FROM exam.marks m, exam.students s,exam.program_subject psub where m.sapid = s.sapid "
		 * ; String countSql =
		 * "SELECT count(*) FROM exam.marks m, exam.students s,exam.program_subject psub where m.sapid = s.sapid "
		 * ;
		 */
		//updating query as the old query fails when students program structure updates when the program is changed.
		String sql = "SELECT m.*,psub.sifySubjectCode FROM exam.marks m, exam.students s,exam.program_sem_subject psub ,exam.registration r where m.sapid = s.sapid  ";
		String countSql = "SELECT count(*) FROM exam.marks m, exam.students s,exam.program_sem_subject psub ,exam.registration r where m.sapid = s.sapid  ";
		
		// added to show sifySubjectCode by PS
				sql = sql +"  "
						+ " and m.sapid = r.sapid "
						+ " and psub.sem=m.sem "
					//	+ "	and psub.prgmStructApplicable = s.PrgmStructApplicable "
					//	+ " and psub.program = m.program "
						+ " and psub.subject = m.subject "
						+ " and psub.consumerProgramStructureId = r.consumerProgramStructureId "
						+ " and m.sem=r.sem "
						;
				countSql = countSql +"  "
						+ " and m.sapid = r.sapid "
						+ " and psub.sem=m.sem "
					//	+ "	and psub.prgmStructApplicable = s.PrgmStructApplicable "
					//	+ " and psub.program = m.program "
						+ " and psub.subject = m.subject "
						+ " and psub.consumerProgramStructureId = r.consumerProgramStructureId "
						+ " and m.sem=r.sem "
						;
				
				// added to show sifySubjectCode by PS
				
		
		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and m.year = ? ";
			countSql = countSql + " and m.year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and m.month = ? ";
			countSql = countSql + " and m.month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( studentMarks.getGrno() != null &&   !("".equals(studentMarks.getGrno()))){
			sql = sql + " and m.grno = ? ";
			countSql = countSql + " and m.grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and m.sapid = ? ";
			countSql = countSql + " and m.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		
		if (!CollectionUtils.isEmpty(studentMarks.getWrittenScoreType())) {
			if (studentMarks.getWrittenScoreType().contains("Attempted")) {
				String commaSepratedOfWrittenScoreType = join(studentMarks.getWrittenScoreType());
				sql = sql + " AND (m.writenscore REGEXP '^[0-9]+$' OR  m.writenscore in ("
						+ commaSepratedOfWrittenScoreType + ")) ";
				countSql = countSql + " AND (m.writenscore REGEXP '^[0-9]+$' OR  m.writenscore in ("
						+ commaSepratedOfWrittenScoreType + "))  ";
			}

			else {
				String commaSepratedOfWrittenScoreType = join(studentMarks.getWrittenScoreType());
				sql = sql + "and m.writenscore in (" + commaSepratedOfWrittenScoreType + ")";
				countSql = countSql + "and m.writenscore in (" + commaSepratedOfWrittenScoreType + ")";
			}
		}

		if( studentMarks.getStudentname() != null &&   !("".equals(studentMarks.getStudentname()))){
			sql = sql + " and m.studentname like  ? ";
			countSql = countSql + " and m.studentname like  ? ";
			parameters.add("%"+studentMarks.getStudentname()+"%");
		}
		if( studentMarks.getProgram() != null &&   !("".equals(studentMarks.getProgram()))){
			sql = sql + " and m.program = ? ";
			countSql = countSql + " and m.program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and m.sem = ? ";
			countSql = countSql + " and m.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and m.subject = ? ";
			countSql = countSql + " and m.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if( studentMarks.getAttempt() != null &&   !("".equals(studentMarks.getAttempt()))){
			sql = sql + " and m.attempt = ? ";
			countSql = countSql + " and m.attempt = ? ";
			parameters.add(studentMarks.getAttempt());
		}
		if( studentMarks.getMarkedForRevaluation() != null &&   !("".equals(studentMarks.getMarkedForRevaluation()))){
			sql = sql + " and m.MarkedForRevaluation = ? ";
			countSql = countSql + " and m.MarkedForRevaluation = ? ";
			parameters.add(studentMarks.getMarkedForRevaluation());
		}
		if( studentMarks.getMarkedForPhotocopy() != null &&   !("".equals(studentMarks.getMarkedForPhotocopy()))){
			sql = sql + " and m.MarkedForPhotocopy = ? ";
			countSql = countSql + " and m.MarkedForPhotocopy = ? ";
			parameters.add(studentMarks.getMarkedForPhotocopy());
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			
			sql = sql + " and m.examorder <= (select max(examorder.order) from exam.examorder where live = 'Y' ) ";
			countSql = countSql + " and m.examorder <= (select max(examorder.order) from exam.examorder where live = 'Y' ) ";
		}

		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " group by m.month,m.year,m.subject,m.program order by m.sem, m.subject, m.program, m.sapid asc";
			
		} 
		
		Object[] args = parameters.toArray();

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));


		return page;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<OnlineExamMarksBean> getOnlineExamMarksPage(int pageNo, int pageSize, OnlineExamMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.online_marks where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.online_marks where 1 = 1 ";

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
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getName() != null &&   !("".equals(studentMarks.getName()))){
			sql = sql + " and name like  ? ";
			countSql = countSql + " and name like  ? ";
			parameters.add("%"+studentMarks.getName()+"%");
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		sql = sql + " order by sapid, subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<OnlineExamMarksBean> pagingHelper = new PaginationHelper<OnlineExamMarksBean>();
		Page<OnlineExamMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(OnlineExamMarksBean.class));


		return page;
	}

	@Transactional(readOnly = true)//
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

	@Transactional(readOnly = true)//
	private boolean checkIfRecordExists(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}

		//String sql = "SELECT count(*) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";
		String sql = "SELECT count(id) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";//by Vilpesh 2022-02-25

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getYear(),
				bean.getMonth(),
				bean.getSapid(),
				bean.getSubject()
		},Integer.class);

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

		StudentMarksBean bean = null;//by Vilpesh 2022-02-25
		for (i = 0; i < marksBeanList.size(); i++) {
			try{
				bean = marksBeanList.get(i);
				upsertMarks(bean, jdbcTemplate, type);
				/*if(i%100 == 0){

				}*/
			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void UpdateABRecords(StudentMarksBean studentMarksBean, String type) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		upsertMarks(studentMarksBean, jdbcTemplate, type);
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateCopyCases(final List<StudentMarksBean> marksBeanList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < marksBeanList.size(); i++) {
			try{
				StudentMarksBean bean = marksBeanList.get(i);
				insertCopyCases(bean, jdbcTemplate);
				if(i%100 == 0){

				}
			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertCopyCases(StudentMarksBean marksBean, JdbcTemplate jdbcTemplate){

		final String sql = " INSERT INTO exam.copycase  "
				 + " ( "
				 + " subject, "
				 + " sapid1, "
				 + " sapid1FName, "
				 + " sapid1LName, "
				 + " program1, "
				 + " ic1, "
				 + " sapid2, "
				 + " sapid2FName, "
				 + " sapid2LName, "
				 + " program2, "
				 + " ic2, "
				 + " matchPercent, "
				 + " consecutiveLines, "
				 + " file1Lines, "
				 + " file2Lines, "
				 + " totalLinesMatched) "
				 + " VALUES "
				 + " ( "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?, "
				 + " ?) ";


		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, m.getSubject());
				ps.setString(2, m.getSapid1());
				ps.setString(3, m.getSapid1FName());
				ps.setString(4, m.getSapid1LName());
				ps.setString(5, m.getProgram1());
				ps.setString(6, m.getIc1());
				ps.setString(7, m.getSapid2());
				ps.setString(8, m.getSapid2FName());
				ps.setString(9, m.getSapid2LName());
				ps.setString(10, m.getProgram2());
				ps.setString(11, m.getIc2());
				ps.setString(12, m.getMatchPercent());
				ps.setString(13, m.getConsecutiveLines());
				ps.setString(14, m.getFile1Lines());
				ps.setString(15, m.getFile2Lines());
				ps.setString(16, m.getTotalLinesMatched());

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(psc, keyHolder);

		int id = keyHolder.getKey().intValue();

		return id;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateTimeTable(final List<TimetableBean> timeTableList,FileBean fileBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < timeTableList.size(); i++) {
			try{
				TimetableBean bean = timeTableList.get(i);
				upsertTimeTable(bean, jdbcTemplate,fileBean);

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
				+ "(examYear, examMonth, sapid, subject, submitted, createdBy, createdDate, lastModifiedBy , lastModifiedDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,?,sysdate() , ?, sysdate())"
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
				createdBy, bean.getLastModifiedBy()
		});

	}
	
	private void upsertTimeTable(TimetableBean bean, JdbcTemplate jdbcTemplate,FileBean fileBean) {
		String sql = "";

		if(!"All".equals(fileBean.getIc())){
			sql = "INSERT INTO exam.corporate_timetable (examYear, examMonth, prgmStructApplicable, program, "
					+ " subject, date, startTime, endTime, sem,mode,ic,createdBy, lastModifiedBy,createdDate, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())"
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
					+ "	    mode = ?,"
					+ "	    ic = ?,"
					+ "	    createdBy = ?,"
					+ "	    lastModifiedBy = ?,"
					+ "	    createdDate = sysdate(),"
					+ "	    lastModifiedDate = sysdate()";
			
			jdbcTemplate.update(sql, new Object[] { 
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getProgram(),
					bean.getSubject(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getSem(),
					bean.getMode(),
					fileBean.getIc(),
					bean.getCreatedBy(),
					bean.getLastModifiedBy(),
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getProgram(),
					bean.getSubject(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getSem(),
					bean.getMode(),
					fileBean.getIc(),
					bean.getCreatedBy(),
					bean.getLastModifiedBy()
			});

		}else{
			sql = "INSERT INTO exam.timetable (examYear, examMonth, prgmStructApplicable, program,"
					+ "subject, date, startTime, endTime, sem,mode, createdBy, lastModifiedBy,createdDate, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())"
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
					+ "	    mode = ?,"
					+ "	    createdBy = ?,"
					+ "	    lastModifiedBy = ?,"
					+ "	    lastModifiedDate = sysdate()";
			
			jdbcTemplate.update(sql, new Object[] { 
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getProgram(),
					bean.getSubject(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getSem(),
					bean.getMode(),
					bean.getCreatedBy(),
					bean.getLastModifiedBy(),
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getProgram(),
					bean.getSubject(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getSem(),
					bean.getMode(),
					bean.getCreatedBy(),
					bean.getLastModifiedBy()
			});
		}

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

	@Transactional(readOnly = true)//
	public void isValidEntry(StudentMarksBean bean, JdbcTemplate jdbcTemplate){
		

		String programsql = "Select count(*) from programs where program = ? ";
		String subjectSql = "Select count(*) from subjects where subjectname = ? ";

		int validProgramCount = (int) jdbcTemplate.queryForObject(programsql,new Object[]{bean.getProgram()},Integer.class);
		int validSubjectCount = (int) jdbcTemplate.queryForObject(subjectSql,new Object[]{bean.getSubject()},Integer.class);

		ArrayList<String> errorMessageList = new ArrayList<>();
		if(validProgramCount == 0){
			errorMessageList.add("Invalid Program "+bean.getProgram()+" entered for Student "+bean.getSapid()+ " under subject "+bean.getSubject());
		}
	}

	@Transactional(readOnly = true)//
	public Page<StudentMarksBean> getAllStudentMarksPage(final int pageNo,	final int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sqlCountRows = "SELECT count(*) FROM exam.marks";
		String sqlFetchRows = "SELECT * FROM exam.marks";

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, sqlCountRows, sqlFetchRows, new Object[]{}, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));

		return page;
	}

	@Transactional(readOnly = true)//
	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;

	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*
		 * String sql =
		 * "select distinct subject from exam.program_sem_subject where prgmStructApplicable = 'Jul2014' or "
		 * +
		 * " prgmStructApplicable = 'Jul2009' or prgmStructApplicable = 'Jul2013' or prgmStructApplicable = 'Jul2017' or prgmStructApplicable = 'Jan2018' or prgmStructApplicable = 'Jul2018' or prgmStructApplicable = 'Jan2019' order by subject"
		 * ;
		 */
		String sql = "select distinct subject from exam.program_sem_subject order by subject";
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;
		
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<Integer> getSubjectsCodeList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct sifySubjectCode "
				+ " from exam.program_subject "
				+ " where prgmStructApplicable in ('Jul2014' ,'Jul2009' ,'Jul2013' ,'Jul2017' ,'Jan2018','Jul2018') "
				+ " and sifySubjectCode <> '0'"
				+ " order by sifySubjectCode";

		ArrayList<Integer> subjectCodeList = (ArrayList<Integer>) jdbcTemplate.queryForList(sql, Integer.class);

		return subjectCodeList;
		
	}

	@Transactional(readOnly = true)//
	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct program FROM exam.programs order by program asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate, String type){
		if("written".equalsIgnoreCase(type)){
			updateWrittenBeforeRIANV(m, jdbcTemplate);
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
				+ " program=?, "
				+ " sem=?, "
				//+ " processed = 'N', "
				+ " processed = '"+MARKS_NOTPROCESSED+"', " //by Vilpesh 2022-02-25
				+ " studentType = ?,"
				+ " lastModifiedBy=?, "
				+ " remarks=?, "
				//+ " lastModifiedDate=sysdate() "
				+ " lastModifiedDate="+DB_CURRENT_TIMESTAMP  //by Vilpesh 2022-02-25
				+ " where year =? and month=? and sapid = ? and subject = ?";

		jdbcTemplate.update(sql, new Object[] { 
				m.getWritenscore(),
				m.getGrno(),
				m.getProgram(),
				m.getSem(),
				m.getStudentType(),
				m.getLastModifiedBy(),
				m.getRemarks(),
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
				//+ "gracemarks=?,"
				//+ "total=?,"
				+ "attempt=?,"
				+ "source=?,"
				+ "location=?,"
				+ "centercode=?,"
				+ "remarks=?, "
				+ "syllabusYear=?, "
				//+ "processed = 'N', "
				+ "processed = '"+MARKS_NOTPROCESSED+"', " //by Vilpesh 2022-02-25
				+ "studentType=?, "
				+ "lastModifiedBy=?, "
				//+ "lastModifiedDate=sysdate() "
				+ "lastModifiedDate="+DB_CURRENT_TIMESTAMP //by Vilpesh 2022-02-25
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
				//m.getGracemarks(),
				//m.getTotal(),
				m.getAttempt(),
				m.getSource(),
				m.getLocation(),
				m.getCentercode(),
				m.getRemarks(),
				m.getSyllabusYear(),
				m.getStudentType(),
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
				+ "studentType,"
				+ "createdBy,"
				+ "createdDate,"
				+ "lastModifiedBy,"
				+ "lastModifiedDate,"
				+ " processed)"
				//+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),'N')";
				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+DB_CURRENT_TIMESTAMP+",?,"+DB_CURRENT_TIMESTAMP+",'"+MARKS_NOTPROCESSED+"')";//by Vilpesh 2022-02-25


		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
				ps.setString(20, m.getStudentType());
				ps.setString(21, m.getCreatedBy());
				ps.setString(22, m.getLastModifiedBy());

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(psc, keyHolder);

		int id = keyHolder.getKey().intValue();

		return id;
	}

	@Transactional(readOnly = true)//
	public List<ExamOrderExamBean> getExamsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examorder order by examorder.order asc";
		List<ExamOrderExamBean> examsList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamOrderExamBean.class));
		return examsList;
	}
	
	
	@Transactional(readOnly = true)//
	public List<ConfigurationExam> getCurrentConfigurationList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.configuration order by configurationType asc";
		List<ConfigurationExam> currentConfList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConfigurationExam.class));
		return currentConfList;
	}

	@Transactional(readOnly = true)//
	public List<ConfigurationExam> getCurrentConfigurationListForExamRegistration() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.configuration where configurationType ='Exam Registration' order by configurationType asc";
		List<ConfigurationExam> currentConfList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConfigurationExam.class));
		return currentConfList;
	}
	
	@Transactional(readOnly = true)//
	public List<TimetableBean> getTimetableList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by program, prgmStructApplicable, sem, date, startTime asc";
		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}
	
	@Transactional(readOnly = true)//
	public List<TimetableBean> getStudentTimetableList(StudentExamBean student,boolean isCorporate) {
		String program = student.getProgram();
		String prgmStructApplicable = student.getPrgmStructApplicable();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		sql="SELECT " + 
				"    CASE" + 
				"        WHEN" + 
				"            enrollmentMonth = 'Jan'" + 
				"                AND enrollmentYear = '2023'" + 
				"                AND (sem = 1 OR isLateral = 'Y')" + 
				"        THEN" + 
				"            '2'" + 
				"        ELSE '1'" + 
				"    END AS phase" + 
				"    FROM" + 
				"    exam.students where sapid = ? ";
		
		String phase=jdbcTemplate.queryForObject(sql, new Object[] {student.getSapid()},new SingleColumnRowMapper<>(String.class));
		
		if(isCorporate){
			sql = "SELECT * FROM exam.corporate_timetable t, exam.examorder eo where t.examMonth = eo.month "
					+ " and t.examYear = eo.year and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? "
					+ " and eo.order = (select max(eo.order) from exam.corporate_timetable t, exam.examorder eo where t.examMonth = eo.month and t.examYear = eo.year "
					+ " and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? ) order by program, prgmStructApplicable, sem, date, startTime asc";
		}else{
			sql = "SELECT * FROM exam.timetable t, exam.examorder eo, exam.timetable_phase_mapping tp where t.examMonth = eo.month "
					+ " and t.examYear = eo.year and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? "
					+ " AND tp.timetableId = t.id AND tp.phaseId = ? "
					+ " and eo.order = (select max(eo.order) from exam.timetable t, exam.examorder eo where t.examMonth = eo.month and t.examYear = eo.year "
					+ " and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? ) order by program, prgmStructApplicable, sem, date, startTime asc";
		}

		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new Object[]{program, prgmStructApplicable,phase ,program, prgmStructApplicable}, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}
	
	@Transactional(readOnly = true)//
	public List<TimetableBean> getAdminTimetableList(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a where  a.examyear = ? and  a.examMonth = ? order by program, prgmStructApplicable, sem, date, startTime asc";
		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new Object[]{year, month}, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}
	
	@Transactional(readOnly = true)//
	public List<TimetableBean> getAdminExecutiveTimetableList(TimetableBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select et.* ,les.program,les.subject,les.prgmStructApplicable,les.acadYear,les.acadMonth "
				+ " from exam.sas_timetable et, exam.live_exam_subjects les"
				+ " where et.examYear = ? and et.examMonth = ?"
				+ " and et.enrollmentYear=? and et.enrollmentMonth=? "
				+ " and  et.examYear = les.examYear and et.examMonth = les.examMonth "
				+ " and et.enrollmentYear = les.acadYear and et.enrollmentMonth = les.acadMonth "
				+ " order by et.date,et.startTime,les.program,les.prgmStructApplicable asc";
		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new Object[]{bean.getExamYear(), bean.getExamMonth(),bean.getBatchYear(),bean.getBatchMonth()}, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}
	
	//START//
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateWrittenRevalStats(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ "writtenRevalLive = ? , "
				+ "writtenRevalLiveDate = sysdate()   "
				+ " where year = ? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getWrittenRevalLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAssignmentRevalStats(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ "assignmentRevalLive = ? , "
				+ "assignmentRevalLiveDate = sysdate()   "
				+ " where year = ? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAssignmentRevalLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateRevaulationResultDeclaredFlagOnMarks(ExamOrderExamBean exam){
		String sql = "Update exam.marks set revaulationResultDeclared = ? where year = ? and month = ? and markedForRevaluation = 'Y'";


		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					exam.getWrittenRevalLive(),
					exam.getYear(),
					exam.getMonth()
			});	
		}catch(Exception e){
			
		}
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateRevaulationResultDeclaredFlagOnAssignmentSubmission(ExamOrderExamBean exam){
		String sql = "Update exam.assignmentsubmission set revaulationResultDeclared = ? where year = ? and month = ? and markedForRevaluation = 'Y'";
		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAssignmentRevalLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateRevaulationResultDeclaredFlagOnAssignmentSubmissionQuickTable(ExamOrderExamBean exam){
		String sql = "Update exam.quick_assignmentsubmission set revaulationResultDeclared = ? where year = ? and month = ? and markedForRevaluation = 'Y'";
		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAssignmentRevalLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	//END//

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamStats(final ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ "live=? , "
				+ "declareDate = sysdate()   "
				+ " where year =? and month = ? AND declareDate is null ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});

		
		/*

		//Code to update individual records of exam.marks and exam.passfail start
		String updateExamMarksSql = "Update exam.marks set "
				+ "isResultLive=?  "
				+ " where year =? and month = ? and program not in ('EPBM','MPDV') ";

		jdbcTemplate.update(updateExamMarksSql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});

		String updateExamOnlineMarksSql = "Update exam.online_marks set "
				+ "isResultLive=?  "
				+ " where year =? and month = ? and program not in ('EPBM','MPDV') ";

		jdbcTemplate.update(updateExamOnlineMarksSql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});

		String getMarksEntriesMadeLiveSql = " select sapid,subject from exam.marks  "
				+ " where year =? and month = ? and program not in ('EPBM','MPDV') and isResultLive=? "; 
		
		String getOnlineMarksEntriesMadeLiveSql = " select sapid,subject from exam.online_marks  "
				+ " where year =? and month = ? and program not in ('EPBM','MPDV') and isResultLive=?  "; 
		final List<StudentMarksBean> listOfEntriesJustMadeLive = new ArrayList();
		List<StudentMarksBean> temp1 = new ArrayList();
		List<StudentMarksBean> temp2 = new ArrayList();
		
		temp1 = jdbcTemplate.query(getMarksEntriesMadeLiveSql,new Object[]{
				exam.getYear(),
				exam.getMonth(),
				"Y".equalsIgnoreCase(exam.getLive()) ? "Y" : "N" 
				}, new BeanPropertyRowMapper(StudentMarksBean.class));

		temp2 = jdbcTemplate.query(getOnlineMarksEntriesMadeLiveSql,new Object[]{
				exam.getYear(),
				exam.getMonth(),
				"Y".equalsIgnoreCase(exam.getLive()) ? "Y" : "N"
				}, new BeanPropertyRowMapper(StudentMarksBean.class));
		listOfEntriesJustMadeLive.addAll(temp1);
		listOfEntriesJustMadeLive.addAll(temp2);
		

		String updateExamPassFailSql = "Update exam.passfail p set "
				+ "p.isResultLive=?  "
				+ " where  p.program not in ('EPBM','MPDV') and p.sapid=? and p.subject=? ";
				
		*/
		/*	oldCommnentd	jdbcTemplate.batchUpdate(updateExamPassFailSql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				StudentMarksBean a = listOfEntriesJustMadeLive.get(i);
				ps.setString(1, exam.getLive());
				ps.setString(2, a.getSapid());
				ps.setString(3, a.getSubject());
			}

			public int getBatchSize() {
				return listOfEntriesJustMadeLive.size();
			}

		});*/
		/*
		
		int count=0;
		for(StudentMarksBean a : listOfEntriesJustMadeLive) {

			jdbcTemplate.update(updateExamPassFailSql, new Object[] { 
					exam.getLive(),
					a.getSapid(),
					a.getSubject()
			});	
			if(count%1000 == 0) {

			}
			count++;
		}
		/*
		
/*	oldcomment	String updateExamPassFailSql = "Update exam.passfail p set "
				+ "p.isResultLive=?  "
				+ " where  p.program not in ('EPBM','MPDV') "
				+ "	and concat(p.sapid,p.subject) in ("
				+ " 	select concat(m.sapid,m.subject) from exam.marks m "
				+ "			where m.year =? and m.month = ? "
				+ "			and m.program not in ('EPBM','MPDV')"
				+ "			and m.isResultLive='Y'   "
				+ ") ";

		jdbcTemplate.update(updateExamPassFailSql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});*/

		//Code to update individual records of exam.marks and exam.passfail end
		
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateConfiguration(ConfigurationExam conf){
		String sql = "Update exam.configuration set "
				+ " startTime=? , "
				+ " endTime = ? ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() "
				+ " where configurationType = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				conf.getStartTime(),
				conf.getEndTime(),
				conf.getLastModifiedBy(),
				conf.getConfigurationType()
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExtendedExamRegistrtaionDates(ConfigurationExam conf){
		String sql = "Update exam.configuration set "
				+ " extendStartTime=? , "
				+ " extendEndTime = ? ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() "
				+ " where configurationType = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				conf.getExtendStartTime(),
				conf.getExtendEndTime(),
				conf.getLastModifiedBy(),
				conf.getConfigurationType()
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTimetableStats(ExamOrderExamBean exam){
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
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAssignmentLiveStatus(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " assignmentMarksLive = ?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAssignmentMarksLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateProjectSubmissionLiveStatus(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " projectSubmissionLive = ?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getProjectSubmissionLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAssignmentSubmissionLiveStatus(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " assignmentLive = ?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAssignmentLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateResitAssignmentSubmissionLiveStatus(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " resitAssignmentLive = ?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getResitAssignmentLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAcadSessionStats(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " acadSessionLive = ?  "
				+ " where year =? and acadMonth = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAcadSessionLive(),
				exam.getYear(),
				exam.getAcadMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateForumStats(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " forumLive = ?  "
				+ " where year =? and acadMonth = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getForumLive(),
				exam.getYear(),
				exam.getAcadMonth()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAcadContentStatus(ExamOrderExamBean exam){
		String sql = "Update exam.examorder set "
				+ " acadContentLive = ?  "
				+ " where year =? and acadMonth = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getAcadContentLive(),
				exam.getYear(),
				exam.getAcadMonth()
		});
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMarksForOnline(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and "
				+ " b.live = 'Y' and a.sapid = ? order by a.sem  asc";
		
	/*Added by Stef	
	 * String sql = "SELECT * FROM exam.marks a where resultLive = 'Y' and a.sapid = ? order by a.sem  asc";*/


		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentRevalWithYearAndMonth(studentMarks.getSapid());
		//Iterating through records and making the written score pending since the Revaluation Result is not declared//
		for(StudentMarksBean studentBean : allStudentsMarksList){
			String key = studentBean.getSubject()+studentBean.getYear()+studentBean.getMonth();
			if("Y".equals(studentBean.getMarkedForRevaluation()) && !"Y".equals(studentBean.getRevaulationResultDeclared())){
				studentBean.setWritenscore("Pending For Reval");
			}
			if(subjectsPendingForAssignmentReval.contains(key)){
				studentBean.setAssignmentscore("Pending For Reval");
			}
			studentsMarksList.add(studentBean);
		}
		
		//Added by Vilpesh to order MarksHistory fetched from DB on 2022-03-17
		if(null != studentsMarksList && !studentsMarksList.isEmpty()) {
			studentsMarksList = this.orderSemForMarksHistory(studentsMarksList);
		}
		return studentsMarksList;
	}
	
	protected List<StudentMarksBean> orderSubjectInSemForMarksHistory(List<StudentMarksBean> listBean2) {
		Set<String> setSubject;
		List<StudentMarksBean> listOrdered2 = null;
		List<StudentMarksBean> listOrderedSubjectWise = null;
		Map<String, List<StudentMarksBean>> mapSubjectMarksHistory = null;
		
		//Distinct Subjects
		setSubject = listBean2.stream().map(g -> g.getSubject()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderSubjectInSemForMarksHistory : Distinct Subject(s): "+ setSubject.size());
		if(null != setSubject && !setSubject.isEmpty()) {
			//Create Map of each Subject and its list of beans
			mapSubjectMarksHistory = listBean2.stream().collect(Collectors.groupingBy(h -> h.getSubject()));
			
			listOrderedSubjectWise = new LinkedList<StudentMarksBean>();
			for(String subject : setSubject) {
				listOrdered2 = mapSubjectMarksHistory.get(subject);
				listOrderedSubjectWise.addAll(listOrdered2);
			}
			
			if(null != mapSubjectMarksHistory) {
				mapSubjectMarksHistory.clear();
			}
			setSubject.clear();
		}
		return listOrderedSubjectWise;
	}
	
	protected List<StudentMarksBean> orderExamMonthInSemForMarksHistory(List<StudentMarksBean> listBean1) {
		Set<Integer> setExamMonth;
		List<StudentMarksBean> tempList1 = null;
		List<StudentMarksBean> listOrdered1 = null;
		List<StudentMarksBean> listOrderedExamMonthWise = null;
		Map<Integer, List<StudentMarksBean>> mapExamMonthMarksHistory = null;
		
		//Distinct ExamMonth
		setExamMonth = listBean1.stream().map(e -> (DateHelper.monthNameOnly3CharacterMap.get(e.getMonth().toLowerCase()))).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderExamMonthInSemForMarksHistory : Distinct ExamMonth(s): "+ setExamMonth.size());
		if(null != setExamMonth && !setExamMonth.isEmpty()) {
			//Create Map of each ExamMonth and its list of beans
			mapExamMonthMarksHistory = listBean1.stream().collect(Collectors.groupingBy(f -> (DateHelper.monthNameOnly3CharacterMap.get(f.getMonth().toLowerCase())) ));
			
			listOrderedExamMonthWise = new LinkedList<StudentMarksBean>();
			for(Integer examMonth : setExamMonth) {
				listOrdered1 = mapExamMonthMarksHistory.get(examMonth);
				tempList1 = this.orderSubjectInSemForMarksHistory(listOrdered1);
				listOrderedExamMonthWise.addAll(tempList1);
			}
			
			if(null != mapExamMonthMarksHistory) {
				mapExamMonthMarksHistory.clear();
			}
			setExamMonth.clear();
		}
		return listOrderedExamMonthWise;
	}
	
	protected List<StudentMarksBean> orderExamYearInSemForMarksHistory(List<StudentMarksBean> listBean1) {
		Set<String> setExamYear;
		List<StudentMarksBean> tempList1 = null;
		List<StudentMarksBean> listOrdered1 = null;
		List<StudentMarksBean> listOrderedExamYearWise = null;
		Map<String, List<StudentMarksBean>> mapExamYearMarksHistory = null;
		
		//Distinct ExamYear
		setExamYear = listBean1.stream().map(c -> c.getYear()).collect(Collectors.toCollection(TreeSet::new));
		//logger.info("orderExamYearInSemForMarksHistory : Distinct ExamYear(s): "+ setExamYear.size());
		if(null != setExamYear && !setExamYear.isEmpty()) {
			//Create Map of each ExamYear and its list of beans
			mapExamYearMarksHistory = listBean1.stream().collect(Collectors.groupingBy(d -> d.getYear()));
			
			listOrderedExamYearWise = new LinkedList<StudentMarksBean>();
			for(String examYear : setExamYear) {
				listOrdered1 = mapExamYearMarksHistory.get(examYear);
				tempList1 = this.orderExamMonthInSemForMarksHistory(listOrdered1);
				listOrderedExamYearWise.addAll(tempList1);
			}
			
			if(null != mapExamYearMarksHistory) {
				mapExamYearMarksHistory.clear();
			}
			setExamYear.clear();
		}
		return listOrderedExamYearWise;
	}
	
	protected List<StudentMarksBean> orderSemForMarksHistory(List<StudentMarksBean> listBean) {
		Set<String> setSemester;
		List<StudentMarksBean> tempList = null;
		List<StudentMarksBean> listOrdered = null;
		List<StudentMarksBean> listOrderedSemWise = null;
		Map<String, List<StudentMarksBean>> mapSemesterMarksHistory;
		
		if(null != listBean && !listBean.isEmpty()) {
			//Collect distinct semester in TreeSet ordered in ascending order
			setSemester = listBean.stream().map(a -> a.getSem()).collect(Collectors.toCollection(TreeSet::new));
			logger.info("orderSemForMarksHistory : Distinct Semester(s): "+ setSemester.size());
			
			if(null != setSemester && !setSemester.isEmpty()) {
				//Create Map of each Semester and its list of beans
				mapSemesterMarksHistory = listBean.stream().collect(Collectors.groupingBy(b -> b.getSem()));
				
				listOrderedSemWise = new ArrayList<StudentMarksBean>();//LinkedList<StudentMarksBean>();
				
				for(String sem : setSemester) {
					//lowest semester picked up and added in LinkedList
					tempList = mapSemesterMarksHistory.get(sem);
					listOrdered = this.orderExamYearInSemForMarksHistory(tempList);
					listOrderedSemWise.addAll(listOrdered);
				}
				if (null != mapSemesterMarksHistory) {
					mapSemesterMarksHistory.clear();
				}
				setSemester.clear();
			}
		} else {
			listOrderedSemWise = listBean;
		}
		return listOrderedSemWise;
	}
	
	// getAStudentsMarksForExecutive Start
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMarksForExecutive(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		
		String sql = "SELECT * "
				   + " FROM exam.marks a, exam.executive_examorder b, exam.executive_exam_bookings eeb,exam.students s "
				   + " where 1 = 1 "+ 
					"	and s.sapid = a.sapid " + 
					"	and s.sem = (select max(sem) from exam.students where sapid = s.sapid) " + 
					"	and b.acadMonth=s.enrollmentMonth and b.acadYear =s.enrollmentYear " + 
					""
				   + " and a.month = b.month "
				   + " and a.year = b.year "
				   + " and a.month = eeb.month "
				   + " and a.year = eeb.year "
				   + " and a.sapid = eeb.sapId "
				   + " and a.subject = eeb.subject "
				   + " and b.resultLive = 'Y' "
				   + " and a.sapid = ? "
				   + " and eeb.booked='Y' "
				   + " order by a.sem  asc";


		try {
			List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
			
			return allStudentsMarksList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	//End
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<String> getSubjectsPendingForTEEReval(String sapid){
		String sql = "SELECT distinct subject FROM exam.marks where markedForRevaluation ='Y' and revaulationResultDeclared ='N' and sapid = ? ";
		List<String> subjectList = jdbcTemplate.query(sql,new Object[]{sapid},new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<String> getSubjectsPendingForAssigmentReval(String sapid){
		String sql = "SELECT distinct subject FROM exam.assignmentsubmission where markedForRevaluation ='Y' and revaulationResultDeclared ='N' and sapid = ? ";
		List<String> subjectList = jdbcTemplate.query(sql,new Object[]{sapid},new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<String> getSubjectsPendingForAssigmentRevalWithYearAndMonth(String sapid){
		String sql = "SELECT CONCAT(subject,year,month) FROM exam.assignmentsubmission where markedForRevaluation ='Y' and revaulationResultDeclared ='N' and sapid = ? ";
		List<String> subjectList = jdbcTemplate.query(sql,new Object[]{sapid},new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMarksForOffline(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		 String sql = "SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and "
				+ " b.oflineResultslive = 'Y' and a.sapid = ? order by a.sem asc";
/*	Added by Stef
 * 	String sql = "SELECT * FROM exam.marks where sapid = ? and month = ? and year = ? and "
				+ " resultlive = 'Y' order by sem asc";*/

		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		//Iterating through records and making the written score pending since the Revaluation Result is not declared//
		for(StudentMarksBean studentBean : allStudentsMarksList){
			if("Y".equals(studentBean.getMarkedForRevaluation()) && !"Y".equals(studentBean.getRevaulationResultDeclared())){
				studentBean.setWritenscore("Pending For Reval");
			}
			studentsMarksList.add(studentBean);
		}
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<String> getAStudentsMarksForSubject(String sapId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT assignmentscore FROM exam.marks where sapid = ? and subject = ? ";



		List<String> assignmentMarksMarksList = jdbcTemplate.query(sql,new Object[]{sapId, subject}, new SingleColumnRowMapper(String.class));
		return assignmentMarksMarksList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMostRecentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where live='Y') order by sem, subject asc";*/
		
		/*String sql = "select a.*, round((part1marks + part2marks + part3marks), 2) as mcq, round(part4marks, 2)  as part4marks , "
				+ " roundedTotal from exam.marks a, exam.examorder b, exam.online_marks c "
				+ "  where a.sapid = ? "
				+ "  and a.year = b.year"
				+ "  and  a.month = b.month"
				+ "  and a.sapid = c.sapid"
				+ "  and a.month = c.month"
				+ "  and a.year = c.year"
				+ "  and a.subject = c.subject "
				+ "  and b.order = (Select max(examorder.order) from exam.examorder where live='Y')"
				+ "  order by a.sem, a.subject asc";*/
		
		 String sql = "select a.*, round((part1marks + part2marks + part3marks), 2) as mcq, round(part4marks, 2)  as part4marks ,  "
				+ " roundedTotal "
				 + " from  exam.examorder b, exam.marks a  left join exam.online_marks c "
				 + " on    a.sapid = c.sapid "
				 + " and a.month = c.month "
				 + " and a.year = c.year "
				 + " and a.subject = c.subject  "
				 + " where a.sapid = ? "
				 + " and a.year = b.year "
				 + " and  a.month = b.month "
				 + " and b.order = (Select max(examorder.order) from exam.examorder where live='Y') "
				 + " order by a.sem, a.subject asc ";
		
		/*Added by Stef
		 * String sql = "select a.*, round((part1marks + part2marks + part3marks), 2) as mcq, round(part4marks, 2)  as part4marks ,  "
				+ " roundedTotal "
				 + " from  exam.examorder b, exam.marks a  left join exam.online_marks c "
				 + " on    a.sapid = c.sapid "
				 + " and a.month = c.month "
				 + " and a.year = c.year "
				 + " and a.subject = c.subject  "
				 + " where a.sapid = ? "
				 + " and a.year = ? "
				 + " and  a.month = ? "
				 + " and a.resultLive = 'Y'"
				 + " and c.resultLive = 'Y'"
				 + " order by a.sem, a.subject asc ";*/


		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()/*,studentMarks.getYear(),studentMarks.getMonth()*/}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(studentMarks.getSapid());
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if("Y".equals(marksBean.getMarkedForRevaluation()) && !"Y".equals(marksBean.getRevaulationResultDeclared())){
				marksBean.setWritenscore("Subject under Revaluation");
				marksBean.setPart4marks("Subject under Revaluation");
			}
			if(subjectsPendingForAssignmentReval.contains(marksBean.getSubject())){
				marksBean.setAssignmentscore("Subject under Revaluation");
			}
			studentsMarksList.add(marksBean);
		}
		return studentsMarksList;
	}
	
	//Start
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMostRecentMarksForExecutive(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select a.*, 0 as mcq  " 
				 + " from  exam.executive_examorder b, exam.marks a, exam.executive_exam_bookings eeb, exam.students s  "
				 + " where a.sapid = ? "
				 + " and a.sapid = eeb.sapId "
				 + " and a.subject = eeb.subject "
				 + " and eeb.booked='Y' "
				 + " and a.year = b.year "
				 + " and  a.month = b.month "
				 + " and eeb.year = b.year "
				 + " and eeb.month = b.month "+ 
					"	and s.sapid = a.sapid " + 
					"	and s.sem = (select max(sem) from exam.students where sapid = s.sapid) " + 
					"	and b.acadMonth=s.enrollmentMonth and b.acadYear =s.enrollmentYear " + 
					""
				 + " and b.order = (Select max(eo.order) from exam.executive_examorder eo where eo.resultLive='Y') "
				 + " order by a.sem, a.subject asc ";


		try {
			List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
			
			return allStudentsMarksList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	//End
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getAStudentsMostRecentPassFailMarks(StudentMarksBean studentMarks, String type) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		
		String sql = "";
		
		if("Online".equalsIgnoreCase(type)){
			 /* Old query comment by PS on 6Feb, kept for refrence 
			  * sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y') "
					+ " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and "
					+ " ("
					+ "		(p.assignmentyear = eo.year and p.assignmentmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y'))"
					+ "				 OR"
					+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y'))"
					+ "		)"
					+ "	)"
					+ "	order by p.sem asc";*/
			
			sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					//+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and p.resultProcessedYear = eo.year and p.resultProcessedMonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y') "
					/* + " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and "
					+ " ("
					+ "		(p.assignmentyear = eo.year and p.assignmentmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y'))"
					+ "				 OR"
					+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y'))"
					+ "		)"
					+ "	)"*/
					+ "	order by p.sem asc";
			
			/*Added by stef
			 * sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y') "
					+ " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p where sapid = ? "
					+ " and p.resultLive = 'Y'"
					+ " )"
					+ " order by p.sem asc";*/
			
			
		}else{
			/*sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y') "
					+ " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and "
					+ " ("
					+ "		(p.assignmentyear = eo.year and p.assignmentmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y'))"
					+ "				 OR"
					+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y'))"
					+ "		)"
					+ "	)"
					+ "	order by p.sem asc";*/
			
			sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					//+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and p.resultProcessedYear = eo.year and p.resultProcessedMonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y') "
					/*+ " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and "
					+ " ("
					+ "		(p.assignmentyear = eo.year and p.assignmentmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y'))"
					+ "				 OR"
					+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
					+ "		and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y'))"
					+ "		)"
					+ "	)"*/
					+ "	order by p.sem asc";
			
			/*Added by stef
			 * sql = "select p.* from exam.passfail p, exam.examorder eo where sapid = ? "
					+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y') "
					+ " and concat(sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p where sapid = ? "
					+ " and p.resultLive = 'Y'"
					+ " )"
					+ " order by p.sem asc";*/
		}

		List<PassFailExamBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(PassFailExamBean.class));
		List<PassFailExamBean> studentMarksList = new ArrayList<PassFailExamBean>();
		List<String> subjectsPendingForTEEReval = getSubjectsPendingForTEEReval(studentMarks.getSapid());
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(studentMarks.getSapid());
		//Iterating through records and setting written score as Pending For reval since the subjects is in the pending for reval subject list//
		for(PassFailExamBean passFail : allStudentsMarksList){
			if(subjectsPendingForTEEReval.contains(passFail.getSubject())){
				passFail.setWrittenscore("Subject under Revaluation");
				passFail.setTotal("Subject under Revaluation");
			}
			if(subjectsPendingForAssignmentReval.contains(passFail.getSubject())){
				passFail.setAssignmentscore("Subject under Revaluation");
				passFail.setTotal("Subject under Revaluation");
			}
			//hide soft skill subjects
			ArrayList<String> softSkillSubjects= new ArrayList<String>(Arrays.asList("Soft Skills for Managers","Employability Skills - II Tally","Start your Start up","Design Thinking"));
			 if(softSkillSubjects.contains(passFail.getSubject())) {
				 if(null != passFail.getTotal() && Integer.parseInt(passFail.getTotal())<15) {
					 // do not show soft skill subjects 
				 }else {
					 studentMarksList.add(passFail); 
				 }
			 }else {
				 studentMarksList.add(passFail);
			 }
			
		}
		return studentMarksList;
	}

	//Start
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getAStudentsMostRecentPassFailMarksForExecutive(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/*String sql = "select p.* from exam.passfail p, exam.executive_examorder eo, exam.students s"
					+ " where s.sapid = ? " + 
					"	and s.sapid = p.sapid " + 
					"	and s.sem = (select max(sem) from exam.students where sapid = s.sapid) " + 
					"	and eo.acadMonth=s.enrollmentMonth and eo.acadYear =s.enrollmentYear " + 
					""
					+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
					+ " and eo.order <= (select max(eo2.order) from exam.executive_examorder eo2 where eo2.timeTableLive = 'Y' and eo2.resultLive='Y') "
					+ " and concat(s.sapid, subject) in "
					+ " ("
					+ " select concat(sapid, subject) from exam.passfail p, exam.executive_examorder eo where sapid = ? "
					+ " and "
					+ " ("
					+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
					+ "		and eo.order <= (select max(eo3.order) from exam.executive_examorder eo3 where eo3.timeTableLive = 'Y'))"
					+ "		)"
					+ "	)"
					+ "	order by p.sem asc";*/
		
		String sql = "select p.* from exam.passfail p, exam.executive_examorder eo, exam.students s"
				+ " where s.sapid = ? " + 
				"	and s.sapid = p.sapid " + 
				"	and s.sem = (select max(sem) from exam.students where sapid = s.sapid) " + 
				"	and eo.acadMonth=s.enrollmentMonth and eo.acadYear =s.enrollmentYear " + 
				""
				//+ " and p.writtenyear = eo.year and p.writtenmonth = eo.month "
				+ " and p.resultProcessedYear = eo.year and p.resultProcessedMonth = eo.month "
				+ " and eo.order <= (select max(eo2.order) from exam.executive_examorder eo2 where eo2.timeTableLive = 'Y' and eo2.resultLive='Y') "
				/*+ " and concat(s.sapid, subject) in "
				+ " ("
				+ " select concat(sapid, subject) from exam.passfail p, exam.executive_examorder eo where sapid = ? "
				+ " and "
				+ " ("
				+ "		(p.writtenyear = eo.year and p.writtenmonth = eo.month"
				+ "		and eo.order <= (select max(eo3.order) from exam.executive_examorder eo3 where eo3.timeTableLive = 'Y'))"
				+ "		)"
				+ "	)"*/
				+ "	order by p.sem asc";
			
		try {
			List<PassFailExamBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(PassFailExamBean.class));
			
			return allStudentsMarksList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	//End
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMostRecentAssignmentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y') order by sem, subject asc";*/
		
	 	String sql = "select a.*, c.reason, c.markedForRevaluation, c.revaluationRemarks , c.revaluationScore from exam.marks a, exam.examorder b, exam.assignmentsubmission c "
				+ " where b.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y') "
				+ " and a.sapid = ? and a.year = b.year and a.month = b.month "
				+ " and a.year = c.year and a.month = c.month and a.sapid = c.sapid and a.subject = c.subject "
				+ " order by sem, subject asc";
		/*Added by Stef
		 * String sql = "select a.*, c.reason, c.markedForRevaluation, c.revaluationRemarks , c.revaluationScore"
				+ " from exam.marks a, exam.assignmentsubmission c "
				+ " where  "
				+ " a.sapid = ? and a.year = ? and a.month = ? "
				+ " and a.year = c.year and a.month = c.month and a.sapid = c.sapid and a.subject = c.subject "
				+ " order by sem, subject asc";*/

		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()/*,studentMarks.getYear(),studentMarks.getMonth()*/}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(studentMarks.getSapid());
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if(subjectsPendingForAssignmentReval.contains(marksBean.getSubject())){
				marksBean.setAssignmentscore("Pending For Reval");
			}
			studentsMarksList.add(marksBean);
		}
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> searchSingleStudentMarks(StudentMarksBean studentMarks, String examMode) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "";
		String countSql = "";

		if("Online".equalsIgnoreCase(examMode)){
			sql = "SELECT * FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";
			countSql = "SELECT count(*) FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";
		}else if("Offline".equalsIgnoreCase(examMode)){
			sql = "SELECT * FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.oflineResultslive = 'Y' ";
			countSql = "SELECT count(*) FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.oflineResultslive = 'Y' ";
		}
		
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

	@Transactional(readOnly = true)//
	public String getMostRecentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)//
	public String getMostRecentResultPeriodForExecutive(StudentExamBean s ){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.executive_examorder e "
						   + " where e.order = (Select max(eo.order) from exam.executive_examorder eo where eo.resultLive='Y')"
						   + " and e.acadMonth = ? and e.acadYear = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{s.getEnrollmentMonth(),s.getEnrollmentYear()},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}

		return recentPeriod;
	}

	@Transactional(readOnly = true)//
	public String getMostRecentAssignmentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)//
	public String getMostRecentTimeTablePeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)//
	public String getMostRecentResitAssignmentPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where resitAssignmentLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)//
	public ExamOrderExamBean getUpcomingExam(){

		String recentPeriod = null;
		final String sql = "Select * from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		ExamOrderExamBean exam = (ExamOrderExamBean)jdbcTemplate.queryForObject(sql, new Object[]{}, new BeanPropertyRowMapper(ExamOrderExamBean.class));

		return exam;
	}
	
	@Transactional(readOnly = true)//
	public ExamOrderExamBean getUpcomingResitAssignmentExam(){

		String recentPeriod = null;
		final String sql = "Select * from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where resitAssignmentLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		ExamOrderExamBean exam = (ExamOrderExamBean)jdbcTemplate.queryForObject(sql, new Object[]{}, new BeanPropertyRowMapper(ExamOrderExamBean.class));

		return exam;
	}

	@Transactional(readOnly = true)//
	public String getRecentExamDeclarationDate() {

		String declareDate = null,decDate="";
		Date d = new Date();
		final String sql = "Select declareDate from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);
		
		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			d = sdfr1.parse(decDate);
			declareDate = sdfr2.format(d);
		} catch (Exception e) {
			
			declareDate = "";
		}

		return declareDate;
	}

	//Start
	@Transactional(readOnly = true)//
	public String getRecentExamDeclarationDateForExecutive(StudentExamBean s) {

		String declareDate = "",decDate="";
		Date d = new Date();
		final String sql = "Select resultDeclareDate from exam.executive_examorder eo"
						   + " where eo.order = (Select max(e.order) from exam.executive_examorder e where e.timeTableLive='Y')"
						   + " and eo.acadMonth=? and eo.acadYear=? ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
		decDate = (String) jdbcTemplate.queryForObject(sql,new Object[]{s.getEnrollmentMonth(),s.getEnrollmentYear()},String.class);
		
		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");
		
			d = sdfr1.parse(decDate.split(" ",-1)[0]);
			declareDate = sdfr2.format(d);
			
		} catch (Exception e) {
			
			declareDate = "";
		}

		return declareDate;
	}
	//End
	
	@Transactional(readOnly = true)//
	public String getStudentCenterDetails(String sapId) {
		String centerCode = null;
		//final String sql = "Select centerCode, centerName from exam.students where sapid = ? and students.sem = (Select max(students.sem) from exam.students where sapid=?)";

		//final String tempSql = "Select centerCode, centerName from exam.student_center where sapid = ? ";
		final String tempSql = "Select centerCode from exam.students where sapid = ? "
				+ " and sem = (Select max(sem) from exam.students where sapid=?)";
		jdbcTemplate = new JdbcTemplate(dataSource);


		centerCode = (String) jdbcTemplate.queryForObject(tempSql,new Object[] { 
				sapId, sapId
		},String.class);
		
		return centerCode;
	}

	@Transactional(readOnly = true)//
	public ArrayList<CenterExamBean> getAllCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers  order by centerName asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterExamBean> centers = (ArrayList<CenterExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterExamBean.class));
		return centers;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<CenterExamBean> getAuthrorizedCenters(String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers  ";
		
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " where centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		sql = sql + "  order by centerName asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterExamBean> centers = (ArrayList<CenterExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterExamBean.class));
		return centers;
	}
	
	/*public HashMap<String,String> mapOfCurrentSemAndSapid(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select MAX(sem),sapid from exam.registration group by sapid ";
		HashMap<String,String> mapOfCurrentSemAndSapid = new HashMap<String,String>();
		ArrayList<StudentBean> listOfStudents = (ArrayList<StudentBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentBean.class));
		if(listOfStudents!=null && listOfStudents.size()>0){
			for(StudentBean student : listOfStudents){
				mapOfCurrentSemAndSapid.put(student.getSapid(), student.getSem());
			}
			
		}
		return mapOfCurrentSemAndSapid;
	}*/
	
	@Transactional(readOnly = true)//
	public HashMap<String, CenterExamBean> getICLCMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterExamBean> centers = (ArrayList<CenterExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterExamBean.class));
		
		HashMap<String, CenterExamBean> icLcMap = new HashMap<>();
		for (CenterExamBean center : centers) {
			icLcMap.put(center.getCenterCode(), center);
		}
		
		return icLcMap;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateStudentCenter(String centerCode, String centerName, String sapId) {
		/*String sql = "Update exam.students set "
				+ "centerCode=? ,"
				+ "centerName=? "
				+ " where sapid = ? ";*/


		String tempSql = "Insert into exam.student_center (sapid, centerCode, centerName) values (?, ?, ?)";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(tempSql, new Object[] { 
				sapId,
				centerCode,
				centerName

		});

	}

	public ArrayList<String> batchUpsertStudentMaster(List<StudentExamBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < studentList.size(); i++) {
			try{
				StudentExamBean bean = studentList.get(i);
				upsertStudenMaster(bean, jdbcTemplate);

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	private void upsertStudenMaster(StudentExamBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.students (sapid, sem, lastName, firstName,"
				+ "middleName, fatherName, husbandName, motherName, gender, program, oldProgram,  enrollmentMonth, enrollmentYear,"
				+ "emailId, mobile, altPhone, dob, regDate, isLateral, isReReg, address, city, state, "
				+ "country, pin, centerCode, centerName, validityEndMonth, validityEndYear, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate,PrgmStructApplicable, imageurl,previousStudentId) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(), ?,?,?)"
				+ " on duplicate key update "
				+ "	    lastName = ?,"
				+ "	    firstName = ?,"
				//+ "	    middleName = ?," Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal 
				//+ "	    fatherName = ?," Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal
				+ "	    husbandName = ?,"
				//+ "	    motherName = ?," Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal
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
				+ "		PrgmStructApplicable = ?,"
				+ "     imageUrl = ? ,"
				+ "     previousStudentId = ? ";


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
		String oldProgram = bean.getOldProgram();
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
		String imageUrl = bean.getImageUrl();
		String previousStudentId = bean.getPreviousStudentId();
		
		jdbcTemplate2.update(sql, new Object[] { 
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
				oldProgram,
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
				imageUrl,
				previousStudentId,
				lastName,
				firstName,
				//middleName, Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal
				//fatherName, Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal
				husbandName,
				//motherName, Not to be updated by Exam Dept from excel file , since student would have updated this one time from portal
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
				PrgmStructApplicable,
				imageUrl,
				previousStudentId
		});

	}

	public ArrayList<String> batchUpsertOnlineExamMarks(List<OnlineExamMarksBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < studentList.size(); i++) {
			try{
				OnlineExamMarksBean bean = studentList.get(i);
				//Upsert in staging table
				upsertOnlineExamMarks(bean, jdbcTemplate);

				//Upsert in Final table
				StudentMarksBean studentMarksBean = convertBean(bean);
				upsertMarks(studentMarksBean, jdbcTemplate, "written");

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}
	
	public ArrayList<String> batchUpsertOnlineRevalMarks(List<OnlineExamMarksBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		
		
		for (i = 0; i < studentList.size(); i++) {
			try{
				OnlineExamMarksBean bean = studentList.get(i);
				//Upsert in staging table
				upsertOnlineRevalMarks(bean, jdbcTemplate);
				// Upsert Remarks and Total marks in Marks Table 

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}

	private StudentMarksBean convertBean(OnlineExamMarksBean bean) {
		StudentMarksBean studentMarksBean = new StudentMarksBean();
		studentMarksBean.setSapid(bean.getSapid());
		studentMarksBean.setGrno("Not Available");
		studentMarksBean.setStudentname(bean.getName());
		studentMarksBean.setSubject(bean.getSubject());
		studentMarksBean.setWritenscore(bean.getRoundedTotal());
		studentMarksBean.setYear(bean.getYear());
		studentMarksBean.setMonth(bean.getMonth());
		studentMarksBean.setCreatedBy(bean.getCreatedBy());
		studentMarksBean.setLastModifiedBy(bean.getLastModifiedBy());
		studentMarksBean.setProgram(bean.getProgram());
		studentMarksBean.setSem(bean.getSem());
		studentMarksBean.setStudentType(bean.getStudentType());
		return studentMarksBean;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void upsertExamRemarksAndWritenScore(OnlineExamMarksBean bean, JdbcTemplate jdbcTemplate2,int roundOfTotal)
	{
		String sql = " Update exam.marks set "
				    + "     remarks = ? ," 
				    + " 	writenscore = ?,"	
				    + "	    lastModifiedBy = ?,"
					+ "	    lastModifiedDate = sysdate(),"
					+ "		processed = 'N' "
					+ "     where sapid = ? "
					+ "		and subject = ? "
					+ "		and year = ? "
					+ "		and month = ? ";
					
		
		jdbcTemplate2.update(sql, new Object[] { 
				bean.getRemarks(),
				roundOfTotal,
				bean.getLastModifiedBy(),
				bean.getSapid(),
				bean.getSubject(),
				bean.getYear(),
				bean.getMonth()
				
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void upsertOnlineRevalMarks(OnlineExamMarksBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "Update exam.online_marks set "
				+ "	    part4marks = ?,"
				+ "	    total = (part1Marks + part2Marks + part3Marks + part4Marks),"
				+ "     roundedTotal = CEIL(part1Marks + part2Marks + part3Marks + part4Marks),"
				+ "	    lastModifiedBy = ?,"
				+ "	    lastModifiedDate = sysdate()"
				+ "     where sapid = ? "
				+ "		and subject = ? "
				+ "		and year = ? "
				+ "		and month = ? ";

		int totalScoreToBeUpdatedInMarksTable = 0;
		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String name = bean.getName();
		double part4marks = bean.getPart4marks();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		jdbcTemplate2.update(sql, new Object[] { 

				part4marks,
				lastModifiedBy,
				sapid,
				subject,
				year,
				month
		});
		
		int roundOfTotal = getRoundedTotal(bean);

		upsertExamRemarksAndWritenScore(bean,jdbcTemplate2,roundOfTotal);

	}
	
	@Transactional(readOnly = true)//
	public int getRoundedTotal(OnlineExamMarksBean bean){
		String sql = "select roundedTotal from exam.online_marks where sapid = ? and subject = ? and year = ? and month = ? ";
		int onlineExamMarksBean = (int)jdbcTemplate.queryForObject(sql, new Object[]{bean.getSapid(),bean.getSubject(),bean.getYear(),bean.getMonth()},Integer.class);
		return onlineExamMarksBean;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	private void upsertOnlineExamMarks(OnlineExamMarksBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.online_marks (year, month, sapid, subject,program, sem, name, total, roundedTotal, "
				+ "part1marks, part2marks, part3marks, part4marks, studentType, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    subject = ?,"
				+ "	    name = ?,"
				+ "	    program = ?,"
				+ "	    sem = ?,"
				+ "	    total = ?,"
				+ "	    roundedTotal = ?,"
				+ "	    part1marks = ?,"
				+ "	    part2marks = ?,"
				+ "	    part3marks = ?,"
				+ "	    part4marks = ?,"
				+ "     studentType = ?,"
				+ "	    lastModifiedBy = ?,"
				+ "	    lastModifiedDate = sysdate()";
      
		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String name = bean.getName();
		double total = bean.getTotal();
		String roundedTotal = bean.getRoundedTotal();
		double part1marks = bean.getPart1marks();
		double part2marks = bean.getPart2marks();
		double part3marks = bean.getPart3marks();
		double part4marks = bean.getPart4marks();
		String studentType = bean.getStudentType();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		jdbcTemplate2.update(sql, new Object[] { 
				year,
				month,
				sapid,
				subject,
				program,
				sem,
				name,
				total,
				roundedTotal,
				part1marks,
				part2marks,
				part3marks,
				part4marks,
				studentType,
				createdBy,
				lastModifiedBy,
				sapid,
				subject,
				name,
				program,
				sem,
				total,
				roundedTotal,
				part1marks,
				part2marks,
				part3marks,
				part4marks,
				studentType,
				lastModifiedBy
		});

	}

	public ArrayList<String> batchUpsertStudentImage(List<StudentExamBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < studentList.size(); i++) {
			try{
				StudentExamBean bean = studentList.get(i);
				upsertStudenImage(bean, jdbcTemplate);

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	private void upsertStudenImage(StudentExamBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "Update exam.students set imageurl = ?  where sapid = ? ";


		String sapid = bean.getSapid();
		String imageUrl = bean.getImageUrl();


		jdbcTemplate2.update(sql, new Object[] { 
				imageUrl,
				sapid			

		});

	}

	public ArrayList<String> batchUpsertRegistration(List<StudentMarksBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < studentList.size(); i++) {
			try{
				StudentMarksBean bean = studentList.get(i);
				upsertRegistration(bean, jdbcTemplate);

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	private void upsertRegistration(StudentMarksBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.registration (sapid, program, sem, month, year, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate) VALUES "
				+ "(?,?,?,?,?,?,sysdate(),?,sysdate())"

				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    program = ?,"
				+ "	    sem = ?,"
				+ "	    month = ?,"
				+ "	    year = ?,"
				+ "	    lastModifiedBy = ?";

		String sapid = bean.getSapid();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String month = bean.getMonth();
		String year = bean.getYear();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		jdbcTemplate.update(sql, new Object[] { 
				sapid,
				program,
				sem,
				month,
				year,
				createdBy,
				lastModifiedBy,
				sapid,
				program,
				sem,
				month,
				year,
				lastModifiedBy
		});

	}

	@Transactional(readOnly = true)//
	public HashMap<String, String> getStudentsProgramMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT sapid, program , max(sem) FROM exam.students  group by sapid order by sapid";

		List<StudentExamBean> studentsList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, String> studentProgramMap = new HashMap<>();
		for (int i = 0; i < studentsList.size(); i++) {
			StudentExamBean b = studentsList.get(i);
			studentProgramMap.put(b.getSapid().trim(), b.getProgram());

		}
		return studentProgramMap;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, String> getStudentsCentersMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT sapid, centerCode , max(sem) FROM exam.students  group by sapid order by sapid";


		List<StudentExamBean> studentsList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, String> studentCentersMap = new HashMap<>();
		for (int i = 0; i < studentsList.size(); i++) {
			StudentExamBean b = studentsList.get(i);
			studentCentersMap.put(b.getSapid().trim(), b.getCenterCode());

		}
		return studentCentersMap;
	}

	public void execute(String sql) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);
	}

	@Transactional(readOnly = true)//
	public HashMap<String, String> getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.program";
		List<ProgramExamBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		HashMap<String, String> programCodeNameMap = new HashMap<String, String>();

		for (int i = 0; i < programList.size(); i++) {
			String key = programList.get(i).getCode();
			if(!programCodeNameMap.containsKey(key)){
				programCodeNameMap.put(programList.get(i).getCode(), programList.get(i).getName());
			}
		}

		return programCodeNameMap;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, ProgramExamBean> getProgramMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql=" SELECT " + 
			"    `ps`.*," + 
			"    `p`.`name` AS `programname`," + 
			"    `ps`.`program` AS `programCode`," + 
			"    `specializationType` AS 'specializationName'," + 
			"    `modeOfLearning` AS 'modeOfLearning'" + 
			"  FROM " + 
			"    `exam`.`programs` ps" + 
			"        INNER JOIN" + 
			"    `exam`.`program` p ON `ps`.`program` = `p`.`code` ";
//		String sql = ""
//				+ " SELECT ps.*, p.name as programname,ps.program as programCode "
//				+ " FROM exam.programs ps "
//				+ " INNER JOIN exam.program p ON ps.program = p.code ";
		List<ProgramExamBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		HashMap<String, ProgramExamBean> programMap = new HashMap<String, ProgramExamBean>();

		for (int i = 0; i < programList.size(); i++) {
			programMap.put(programList.get(i).getProgram()+"-"+programList.get(i).getProgramStructure(), programList.get(i));
		}

		return programMap;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<AssignmentStatusBean> getAssignmentStatusPage(int pageNo, int pageSize, AssignmentFileBean assignmentStatus, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT a.*, s.*,ps.sem "
				+ " FROM exam.assignmentstatus a, exam.students s, exam.program_sem_subject ps "
				+ " where a.sapid = s.sapid and s.consumerProgramStructureId = ps.consumerProgramStructureId	"
				+ " and a.subject = ps.subject ";
		String countSql = "SELECT count(*) "
				+ " FROM exam.assignmentStatus  a, exam.students s, exam.program_sem_subject ps "
				+ " WHERE a.sapid = s.sapid and a.subject = ps.subject  "
				+ " and s.consumerProgramStructureId = ps.consumerProgramStructureId  ";

		if( assignmentStatus.getConsumerProgramStructureId() != null &&   !("".equals(assignmentStatus.getConsumerProgramStructureId()))){
			sql = sql + " and ps.consumerProgramStructureId in ("+ assignmentStatus.getConsumerProgramStructureId() +")  ";
			countSql = countSql + "and ps.consumerProgramStructureId in ("+ assignmentStatus.getConsumerProgramStructureId() +")  ";
			}
		
		
		if( assignmentStatus.getYear() != null &&   !("".equals(assignmentStatus.getYear()))){
			sql = sql + " and a.examYear = ? ";
			countSql = countSql + " and a.examYear = ? ";
			parameters.add(assignmentStatus.getYear());
		}

		if( assignmentStatus.getMonth() != null &&   !("".equals(assignmentStatus.getMonth()))){
			sql = sql + " and a.examMonth = ? ";
			countSql = countSql + " and a.examMonth = ? ";
			parameters.add(assignmentStatus.getMonth());
		}

		if( assignmentStatus.getSapId() != null &&   !("".equals(assignmentStatus.getSapId()))){
			sql = sql + " and a.sapid = ? ";
			countSql = countSql + " and a.sapid = ? ";
			parameters.add(assignmentStatus.getSapId());
		}

		if( assignmentStatus.getSubject() != null &&   !("".equals(assignmentStatus.getSubject()))){
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and a.subject = ? ";
			parameters.add(assignmentStatus.getSubject());
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " order by a.sapid, a.submitted asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentStatusBean> pagingHelper = new PaginationHelper<AssignmentStatusBean>();
		Page<AssignmentStatusBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentStatusBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<StudentExamBean> getAllActiveStudentBean(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		
		String validityEndMonth = "";
		if(month <= 6){
			validityEndMonth = "Jun";
		}else{
			validityEndMonth = "Dec";
		}
		
		// remove Program Terminated Student from Active Student List 
		String sql = " SELECT distinct sapid FROM exam.students s, exam.examorder  eo  where 1 = 1 and programCleared = 'N' "
				+ " and s.validityEndMonth = eo.month and s.validityEndYear = eo.year and NOT(s.programStatus <=> 'Program Terminated') "
				+ " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year + "' and month = '" + validityEndMonth+ "') ";
		
		ArrayList<StudentExamBean> listOfAllActiveStudents = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));
		return listOfAllActiveStudents;
	}
	
	@Transactional(readOnly = true)//
	public Page<StudentExamBean> getStudentPage(int pageNo, int pageSize, StudentExamBean student, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM exam.students "
					+ "WHERE sapid NOT LIKE '777777%' "		//replaced WHERE 1 = 1
						+ "AND sapid NOT LIKE '7700000000%' "
						+ "AND sapid NOT IN ('77999999999', '88888888888') ";
		String countSql = "SELECT count(*) FROM exam.students "
						+ "WHERE sapid NOT LIKE '777777%' "	//replaced WHERE 1 = 1
							+ "AND sapid NOT LIKE '7700000000%' "
							+ "AND sapid NOT IN ('77999999999', '88888888888') ";
		
		if( student.isValidStudent()){
			//For active students we need to query across two tables
			sql = "SELECT * FROM exam.students s, exam.examorder  eo "
				+ "WHERE sapid NOT LIKE '777777%' "
					+ "AND sapid NOT LIKE '7700000000%' "
					+ "AND sapid NOT IN ('77999999999', '88888888888') "
					+ "AND programCleared = 'N' ";
			countSql = "SELECT count(*) FROM exam.students  s, exam.examorder  eo "
					 + "WHERE sapid NOT LIKE '777777%' "
					 	+ "AND sapid NOT LIKE '7700000000%' "
					 	+ "AND sapid NOT IN ('77999999999', '88888888888') "
					 	+ "AND programCleared = 'N' ";
		}
		
		if( student.getEnrollmentYear() != null &&   !("".equals(student.getEnrollmentYear()))){
			sql = sql + " and enrollmentYear in ("+student.getEnrollmentYear()+") ";
			countSql = countSql + " and enrollmentYear in ("+student.getEnrollmentYear()+") ";
			//parameters.add(student.getEnrollmentYear());
		}
		
		if( student.getEnrollmentMonth() != null &&   !("".equals(student.getEnrollmentMonth()))){
			sql = sql + " and enrollmentMonth = ? ";
			countSql = countSql + " and enrollmentMonth = ? ";
			parameters.add(student.getEnrollmentMonth());
		}
		if( student.getProgramCleared() != null &&   !("".equals(student.getProgramCleared()))){
			sql = sql + " and programCleared = ? ";
			countSql = countSql + " and programCleared = ? ";
			parameters.add(student.getProgramCleared());
		}
		if( student.getValidityEndYear() != null &&   !("".equals(student.getValidityEndYear()))){
			sql = sql + " and validityEndYear = ? ";
			countSql = countSql + " and validityEndYear = ? ";
			parameters.add(student.getValidityEndYear());
		}
		if( student.getValidityEndMonth() != null &&   !("".equals(student.getValidityEndMonth()))){
			sql = sql + " and validityEndMonth = ? ";
			countSql = countSql + " and validityEndMonth = ? ";
			parameters.add(student.getValidityEndMonth());
		}

		if( student.getSapid() != null &&   !("".equals(student.getSapid()))){
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(student.getSapid());
		}

		if( student.getProgram() != null &&   !("".equals(student.getProgram()))){
			sql = sql + " and program = ? ";
			countSql = countSql + " and program = ? ";
			parameters.add(student.getProgram());
		}
		if( student.getSem() != null &&   !("".equals(student.getSem()))){
			sql = sql + " and sem = ? ";
			countSql = countSql + " and sem = ? ";
			parameters.add(student.getSem());
		}

		if( student.getFirstName() != null &&   !("".equals(student.getFirstName()))){
			sql = sql + " and firstName like  ? ";
			countSql = countSql + " and firstName like  ? ";
			parameters.add("%"+student.getFirstName()+"%");
		}

		if( student.getLastName() != null &&   !("".equals(student.getLastName()))){
			sql = sql + " and lastName like  ? ";
			countSql = countSql + " and lastName like  ? ";
			parameters.add("%"+student.getLastName()+"%");
		}
		if( student.getPrgmStructApplicable() != null &&   !("".equals(student.getPrgmStructApplicable()))){
			sql = sql + " and PrgmStructApplicable = ? ";
			countSql = countSql + " and PrgmStructApplicable = ? ";
			parameters.add(student.getPrgmStructApplicable());
		}
		
		if( student.getEmailId() != null &&   !("".equals(student.getEmailId()))){
			sql = sql + " and emailId = ? ";
			countSql = countSql + " and emailId = ? ";
			parameters.add(student.getEmailId());
		}
		
//		if (org.apache.commons.lang.StringUtils.isBlank(student.getProgramStatus())) {
//			sql = sql + " and (programStatus = '' OR programStatus is null ')";
//			countSql = countSql + " and (programStatus = '' OR programStatus is null') ";
//		} else {
//			sql = sql + " and programStatus = ? ";
//			countSql = countSql + " and programStatus = ? ";
//			parameters.add(student.getProgramStatus());
//		}
		
		if (!org.apache.commons.lang.StringUtils.isBlank(student.getProgramStatus())) {
			if("Program Active".equals(student.getProgramStatus())) {
				sql = sql + " and (programStatus IS NULL OR programStatus = '') ";
				countSql = countSql + " and (programStatus IS NULL OR programStatus = '') ";
			}
			else {
				sql = sql + " and programStatus = ? ";
				countSql = countSql + " and programStatus = ? ";
				parameters.add(student.getProgramStatus());
			}
		}
		
		if( student.isValidStudent()){
			//Find what should be validity end period to be considered on Today's date
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
			
			String validityEndMonth = "";
			if(month <= 6){
				validityEndMonth = "Jun";
			}else{
				validityEndMonth = "Dec";
			}
			
			// remove Program Terminated Student from Active Student List 

			sql = sql + " and s.validityEndMonth = eo.month and s.validityEndYear = eo.year and (programStatus = '' OR programStatus is null) ";
			countSql = countSql + " and s.validityEndMonth = eo.month and s.validityEndYear = eo.year and (programStatus = '' OR programStatus is null)";
			
			sql = sql + " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year + "' and month = '" + validityEndMonth+ "') ";
			countSql = countSql + " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year + "' and month = '" + validityEndMonth+ "') ";
		}
		
		if( student.getSapIdList() != null &&   !("".equals(student.getSapIdList()))){
			
			String commaSeparatedList = generateCommaSeparatedList(student.getSapIdList());
			sql = sql + " and sapid in ( "+ commaSeparatedList +" ) ";
			countSql = countSql + " and sapid in (  "+ commaSeparatedList +" ) ";
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if(student.getCenterCode() != null && !"".equals(student.getCenterCode().trim())){
			sql = sql + " and centerCode = ?  ";
			countSql = countSql + " and centerCode = ?  ";
			parameters.add(student.getCenterCode());
		}

		sql = sql + " order by sem, program, sapid asc  ";
		Object[] args = parameters.toArray();

		PaginationHelper<StudentExamBean> pagingHelper = new PaginationHelper<StudentExamBean>();
		Page<StudentExamBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(StudentExamBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getAllValidStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		
		String validityEndMonth = "";
		if(month <= 6){
			validityEndMonth = "Jun";
		}else{
			validityEndMonth = "Dec";
		}
		
		String sql = "SELECT s.sapId FROM exam.students s, exam.examorder eo "
				+ " where s.validityEndMonth = eo.month and s.validityEndYear = eo.year "
				+ " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year + "' and month = '" + validityEndMonth+ "')";
		
		ArrayList<String> sapIdList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sapIdList;
	}

	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}

	@Transactional(readOnly = true)//
	public StudentExamBean findStudentBySAPnSem(String sapid, String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		String sql = " Select * from exam.students where sapid = ? and sem = ? ";

		student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
				sapid, sem
		}, new BeanPropertyRowMapper(StudentExamBean.class));

		return student;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateStudent(StudentExamBean bean, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "Update exam.students set "
				+ "  	enrollmentMonth = ?,"
				+ "	    enrollmentYear = ?,"
				+ "	    lastName = ?,"
				+ "	    husbandName = ?,"
				+ "	    firstName = ?,"
				+ "	    middleName = ?,"
				+ "	    fatherName = ?,"
				+ "	    motherName = ?,"
				+ "	    gender = ?,"
				+ "	    emailId = ?,"
				+ "	    mobile = ?,"
				+ "	    program = ?,"
				+ "	    dob = ?,"
				+ "	    oldProgram = ?,"
				+ "	    programChanged = ?,"
				+ "	    prgmStructApplicable = ?,"
				//+ "	    altPhone = ?,"
				+ "	    dob = ?,"
				//+ "	    regDate = ?,"
				//+ "	    isLateral = ?,"
				//+ "	    isReReg = ?,"
				+ "	    address = ?,"
				//+ "	    city = ?,"
				//+ "	    state = ?,"
				//+ "	    country = ?,"
				//+ "	    pin = ?,"
				+ "	    centerCode = ?,"
				+ "	    centerName = ?,"
				+ "	    validityEndMonth = ?,"
				+ "	    validityEndYear = ?,"
				+ "	    lastModifiedBy = ?,"
				+ "	    lastModifiedDate = sysdate(), "
				+ "     programStatus = ? ,"
				+"      programRemarks = ?"
				+ " 	where sapid = ? "
				+ "		and sem = ? ";

		String sapid = bean.getSapid();
		String sem = bean.getSem();
		String lastName = bean.getLastName();
		String firstName = bean.getFirstName();
		String middleName = bean.getMiddleName();
		String fatherName = bean.getFatherName();
		//String husbandName = bean.getHusbandName();
		String motherName = bean.getMotherName();
		String gender = bean.getGender();
		gender = gender.trim();
		String program = bean.getProgram();
		String dateOfBirth = bean.getDob();
		String oldprogram = bean.getOldProgram();
		String programChanged = bean.getProgramChanged();
		String enrollmentMonth = bean.getEnrollmentMonth();
		String enrollmentYear = bean.getEnrollmentYear();
		String emailId = bean.getEmailId();
		String mobile = bean.getMobile();
		String spouseName = bean.getHusbandName();
		//String altPhone = bean.getAltPhone();
		String dob = bean.getDob();
		//String regDate = bean.getRegDate();
		//String isLateral = bean.getIsLateral();
		//String isReReg = bean.getIsReReg();
		String address = bean.getAddress();
		//String city = bean.getCity();
		//String state = bean.getState();
		//String country = bean.getCountry();
		//String pin = bean.getPin();
		String centerCode = bean.getCenterCode();
		String centerName = bean.getCenterName();
		String validityEndMonth = bean.getValidityEndMonth();
		String validityEndYear = bean.getValidityEndYear();
		//String createdBy = bean.getCreatedBy();
		String lastModifiedBy = userId;
		String PrgmStructApplicable = bean.getPrgmStructApplicable();
		String programStatus =bean.getProgramStatus();
		String programRemarks =bean.getProgramRemarks();

		jdbcTemplate.update(sql, new Object[] { 

				enrollmentMonth,
				enrollmentYear,
				lastName,
				spouseName,
				firstName,
				middleName,
				fatherName,
				motherName,
				gender,
				emailId,
				mobile,
				program,
				dateOfBirth,
				oldprogram,
				programChanged,
				PrgmStructApplicable,
				dob,
				address,
				centerCode,
				centerName,
				validityEndMonth,
				validityEndYear,
				lastModifiedBy,
				programStatus,
				programRemarks,
				sapid,
				sem
		});

	}

	@Transactional(readOnly = true)//
	public ArrayList<ExamBookingTransactionBean> getConfirmedBooking(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and subject = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid, subject}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingForGivenYearMonth(String sapid, String subject, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings  where year = ? and month = ?  "
					+ " and sapid = ? and subject = ? and booked = 'Y' "
				+ " UNION "
				+ " SELECT * FROM exam.exambookings_history  where year = ? and month = ? and sapid = ? and subject = ? and booked = 'Y'  ";
		
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{year, month, sapid, subject, year, month, sapid, subject}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)//
	public StudentExamBean getSingleStudentWithValidity(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "select * from exam.students s, exam.examorder eo where"
					+ "		s.sapid = ?"
					+ "     and s.sem = (Select max(sem) from exam.students where sapid = ? )"
					+ " 	and s.validityendyear = eo.year"
					+ " 	and s.validityendmonth = eo.month"
					+ "		and eo.order >= (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					;

			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
			//
		}
		return student;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<StudentMarksBean> getRegisteredStudentMarksPage(int pageNo, int pageSize, StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.exambookings eb, exam.examcenter ec, exam.students s, exam.marks m  where m.sapid = eb.sapid and m.subject = eb.subject and m.year = eb.year and m.month = eb.month and eb.booked = 'Y' and eb.centerId = ec.centerId and s.sapid = m.sapid  ";
		String countSql = "SELECT count(*) FROM exam.marks m, exam.exambookings eb, exam.examcenter ec, exam.students s  where m.sapid = eb.sapid and m.subject = eb.subject and m.year = eb.year and m.month = eb.month and eb.booked = 'Y' and eb.centerId = ec.centerId and s.sapid = m.sapid  ";

		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and m.year = ? ";
			countSql = countSql + " and m.year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and m.month = ? ";
			countSql = countSql + " and m.month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( studentMarks.getGrno() != null &&   !("".equals(studentMarks.getGrno()))){
			sql = sql + " and m.grno = ? ";
			countSql = countSql + " and m.grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and m.sapid = ? ";
			countSql = countSql + " and m.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getStudentname() != null &&   !("".equals(studentMarks.getStudentname()))){
			sql = sql + " and m.studentname like  ? ";
			countSql = countSql + " and m.studentname like  ? ";
			parameters.add("%"+studentMarks.getStudentname()+"%");
		}
		if( studentMarks.getProgram() != null &&   !("".equals(studentMarks.getProgram()))){
			sql = sql + " and m.program = ? ";
			countSql = countSql + " and m.program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and m.sem = ? ";
			countSql = countSql + " and m.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and m.subject = ? ";
			countSql = countSql + " and m.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if( studentMarks.getExamMode() != null &&   !("".equals(studentMarks.getExamMode()))){
			sql = sql + " and examMode = ? ";
			countSql = countSql + " and examMode = ? ";
			parameters.add(studentMarks.getExamMode());
		}
		
		if( studentMarks.getCenterId() != null &&   !("".equals(studentMarks.getCenterId()))){
			sql = sql + " and ec.centerId = ? ";
			countSql = countSql + " and ec.centerId = ? ";
			parameters.add(studentMarks.getCenterId());
		}

		sql = sql + " group by s.sapid, m.subject order by s.lastName, s.firstName ";
		//countSql = countSql + " group by s.sapid, m.subject order by s.lastName, s.firstName ";
		Object[] args = parameters.toArray();

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));

		return page;
	}

	public ArrayList<String> batchUpsertCenters(List<CenterExamBean> centerList) {// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < centerList.size(); i++) {
			try{
				CenterExamBean bean = centerList.get(i);
				upsertCenters(bean, jdbcTemplate);

			}catch(Exception e){
				
				errorList.add(i+"");
			}
		}
		return errorList;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	private void upsertCenters(CenterExamBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.centers "
				+ "(centerCode, centerName, sfdcId, address, state, city, lc, active)"
				+ " VALUES "
				+ "(?,?,?,?,?,?,?,? )"
				+ " on duplicate key update "
				+ "	    centerName = ?,"
				+ "	    sfdcId = ?,"
				+ "	    address = ?,"
				+ "	    state = ?,"
				+ "	    city = ?,"
				+ "	    lc = ?, "
				+ "	    active = ? ";

		String centerCode = bean.getCenterCode();
		String centerName = bean.getCenterName();
		String sfdcId = bean.getSfdcId();
		String address = bean.getAddress();
		String state = bean.getState();
		String city = bean.getCity();
		String lc = bean.getLc();
		String active = bean.getActive();

		jdbcTemplate.update(sql, new Object[] { 
				centerCode,
				centerName,
				sfdcId,
				address,
				state,
				city,
				lc,
				active,
				centerName,
				sfdcId,
				address,
				state,
				city,
				lc,
				active
		});
		
	}

	@Transactional(readOnly = true)//
	public Page<StudentMarksBean> getStudentRegistrationsPage(int pageNo, int pageSize, StudentMarksBean student, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT r.* FROM exam.registration r, exam.students s where R.sapid = s.sapid ";
		String countSql = "SELECT count(*) FROM exam.registration r, exam.students s where r.sapid = s.sapid ";

		if( student.getYear() != null &&   !("".equals(student.getYear()))){
			sql = sql + " and r.year = ? ";
			countSql = countSql + " and r.year = ? ";
			parameters.add(student.getYear());
		}
		if( student.getMonth() != null &&   !("".equals(student.getMonth()))){
			sql = sql + " and r.month = ? ";
			countSql = countSql + " and r.month = ? ";
			parameters.add(student.getMonth());
		}

		if( student.getSapid() != null &&   !("".equals(student.getSapid()))){
			sql = sql + " and r.sapid = ? ";
			countSql = countSql + " and r.sapid = ? ";
			parameters.add(student.getSapid());
		}

		if( student.getProgram() != null &&   !("".equals(student.getProgram()))){
			sql = sql + " and r.program = ? ";
			countSql = countSql + " and r.program = ? ";
			parameters.add(student.getProgram());
		}
		if( student.getSem() != null &&   !("".equals(student.getSem()))){
			sql = sql + " and r.sem = ? ";
			countSql = countSql + " and r.sem = ? ";
			parameters.add(student.getSem());
		}
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " order by r.sapid, r.program, r.sem asc";
		Object[] args = parameters.toArray();

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));

		return page;
	}

	@Transactional(readOnly = true)//
	public StudentExamBean findRegistrationBySAPnSem(String sapid, String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		String sql = " Select * from exam.registration where sapid = ? and sem = ? ";


		student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
				sapid, sem
		}, new BeanPropertyRowMapper(StudentExamBean.class));

		return student;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentRegistration(StudentExamBean student, String userId) {
		String sql = "Update exam.registration set "
				+ "program= '"+student.getProgram()+"' , "
				+ "month='"+student.getMonth()+"' , "
				+ "year="+student.getYear()+" , "
				+ "sem=   " + student.getSem() 
				+ " where sapid = " + student.getSapid()+" and sem =  "+student.getOldSem();

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void deleteRegistration(String sapid, String sem) {
		String sql = "Delete from exam.registration where sapid = ? and sem = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				sapid, sem
		});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateOfflineExamResultStatus(ExamOrderExamBean exam) {
		String sql = "Update exam.examorder set "
				+ "oflineResultslive =? , "
				+ "oflineResultsDeclareDate = sysdate()   "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getOflineResultslive(),
				exam.getYear(),
				exam.getMonth()
		});
	}

	@Transactional(readOnly = true)//
	public StudentExamBean getSingleStudentsData(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students s where "
					+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";


			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapId, sapId
			}, new BeanPropertyRowMapper(StudentExamBean.class));
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
		}catch(Exception e){

			
		}
		return student;
	}

	@Transactional(readOnly = true)//
	public String getMostRecentOfflineResultPeriod() {
		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}

	@Transactional(readOnly = true)//
	public String getRecentOfflineExamDeclarationDate() {
		String declareDate = null,decDate="";
		Date d = new Date();
		final String sql = "Select oflineResultsDeclareDate from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);
		
		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			d = sdfr1.parse(decDate);
			declareDate = sdfr2.format(d);
		} catch (Exception e) {
			
			declareDate = "";
		}

		return declareDate;
	}

	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAStudentsMostRecentOfflineMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		 String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y') order by sem, subject asc";
	
		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{studentMarks.getSapid()}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(studentMarks.getSapid());
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if("Y".equals(marksBean.getMarkedForRevaluation()) && !"Y".equals(marksBean.getRevaulationResultDeclared())){
				marksBean.setWritenscore("Pending For Reval");
			}
			if(subjectsPendingForAssignmentReval.contains(marksBean.getSubject())){
				marksBean.setAssignmentscore("Pending For Reval");
			}
			studentsMarksList.add(marksBean);
		}
		return studentsMarksList;
	}

	@Transactional(readOnly = true)//
	public List<ExamBookingTransactionBean> getABRecords(int i, int maxValue,	ExamBookingTransactionBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String year = searchBean.getYear();
		String month = searchBean.getMonth();

		String sql = "select eb.* from exam.exambookings eb "
				+ " left outer join exam.marks m "
				+ " on m.sapid = eb.sapid and m.subject = eb.subject "
				+ " and m.year = eb.year and m.month = eb.month "
				+ " where"
				+ " eb.booked = 'Y' and eb.exammode = 'Online' "
				+ " and eb.year = ? and eb.month = ? "
				+ " and (m.writenscore is null or m.writenscore = '' or m.writenscore ='AB') "
				+ " and eb.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking') "; // Added by discussion with Abhay Sir
				
		Object[] args = new Object[]{year, month};

		return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	}
	
	@Transactional(readOnly = true)//
	public List<ExamBookingTransactionBean> getProjectABRecordsForOffline(ExamBookingTransactionBean searchBean)
	{
	   jdbcTemplate = new JdbcTemplate(dataSource);
		
		String year = searchBean.getYear();
		String month = searchBean.getMonth();
		
		StringBuffer sql = new StringBuffer("select eb.* from exam.exambookings eb ");
		sql.append(" left outer join exam.marks m ");
		sql.append(" on m.sapid = eb.sapid and m.subject = eb.subject  "); 
		sql.append(" and m.year = eb.year and m.month = eb.month   ");
		sql.append(" where ");
		sql.append(" eb.booked = 'Y' and eb.exammode = 'Offline'  ");
		//sql.append(" and eb.subject IN ('Project', 'Module 4 - Project') ");
		sql.append(" and eb.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') ");//Vilpesh on 2022-03-04
		sql.append(" and eb.year = ? and eb.month = ?  ");
		sql.append(" and (m.writenscore is null or m.writenscore = '' or m.writenscore ='AB') ");

		return jdbcTemplate.query(sql.toString(),new Object[]{year,month},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, StudentExamBean> getAllStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));
		
		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}
		
		return studentsMap;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, StudentExamBean> getAllStudentProgramMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));
		
		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid().trim()+student.getProgram().trim(), student);
		}
		
		return studentsMap;
	}

	@Transactional(readOnly = true)//
	public ArrayList<StudentMarksBean> getStudentMarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from exam.marks where sapid = ? order by sem, subject asc";

		ArrayList<StudentMarksBean> studentsMarksList = (ArrayList<StudentMarksBean>)jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}

	@Transactional(readOnly = true)//
	public HashMap<String, String> getStudentRegistrationMonthYearMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT sapid, year, month FROM exam.registration";

		List<StudentMarksBean> registrationList = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentMarksBean.class));

		HashMap<String, String> studentRegistrationMonthYearMap = new HashMap<>();
		for (int i = 0; i < registrationList.size(); i++) {
			StudentMarksBean b = registrationList.get(i);
			studentRegistrationMonthYearMap.put(b.getSapid().trim()+ b.getYear() + b.getMonth(), null);

		}
		return studentRegistrationMonthYearMap;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateStudentValidityHistory(StudentExamBean student,String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO exam.student_validity_extension(oldValidityEndMonth, oldValidityEndYear, "
				+ "newValidityEndMonth, newValidityEndYear, sapid, lastModifiedBy, lastModifiedDate) VALUES "
		     + "(?,?,?,?,?,?, sysdate())";
/*		     + " on duplicate key "
		     + " update "
		     + " oldValidityEndMonth = ?, "
		     + " oldValidityEndYear = ?, "
		     + " newValidityEndMonth = ?, "
		     + " newValidityEndYear = ?,"
		     + " lastModifiedBy = ? ";*/

		jdbcTemplate.update(sql, new Object[] { 
				student.getOldValidityEndMonth(),
				student.getOldValidityEndYear(),
				student.getNewValidityEndMonth(),
				student.getNewValidityEndYear(),
				student.getSapid(),
				userId
/*				student.getOldValidityEndMonth(),
				student.getOldValidityEndYear(),
				student.getNewValidityEndMonth(),
				student.getNewValidityEndYear(),
				userId*/
				
		});
		
	}
	
	@Transactional(readOnly = true)//
	public StudentExamBean studentDetails(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select s.*, sve.oldValidityEndYear, sve.oldValidityEndMonth, sve.newValidityEndYear, sve.newValidityEndMonth  from exam.students s left join exam.student_validity_extension sve on s.sapid = sve.sapid where s.sapid = ?";
		 StudentExamBean student = (StudentExamBean) jdbcTemplate.queryForObject(sql,new Object[] {sapid},new BeanPropertyRowMapper(StudentExamBean.class));
		return student;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<StudentExamBean> getValidityExtensions(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "Select * from exam.student_validity_extension where sapid = ? ";
		ArrayList<StudentExamBean> extensionList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(StudentExamBean.class));
		
		return extensionList;
	}
	
	//Added for SAS-->
	@Transactional(readOnly = true)//
	public ArrayList<String> getAllApplicableSubjects(String program,String prgmStructApp) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.program_subject where program = ? and active = 'Y' and prgmStructApplicable = ? ";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{program, prgmStructApp}, new SingleColumnRowMapper(String.class));
		return subjectList;
	}	
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getAllSubjectsSAS() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.program_subject where (program = 'MPDV' or program = 'EPBM') and active = 'Y'";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getAllProgramsSAS() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT program FROM exam.programs where programType = 'Executive Programs' order by program asc";
		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String,ProgramsBean> getProgramDetailsMap() {
		String sql = "select * from exam.programs" ;
		ArrayList<ProgramsBean> beanList = (ArrayList<ProgramsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsBean.class));
		HashMap<String,ProgramsBean> programsInfoList = new HashMap<String,ProgramsBean>();
		for(ProgramsBean bean: beanList){
			//String key = bean.getProgram()+"-"+bean.getProgramStructure();
			String key = bean.getConsumerProgramStructureId();
			if(!programsInfoList.containsKey(key)){
				programsInfoList.put(key, bean);
			}
		}
		return programsInfoList;
	}
	
	/*Added by Stef
	 * public void updateResultLiveIndividualStudentStats(ExamOrderBean exam){
	 try{
	 updateExamMarksStats(exam);

	 updateExamOnlineMarksStats(exam);

	 updateExamPassfailMarksStats(exam);

	 }catch(Exception e){

		 
	 }
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamMarksStats(ExamOrderBean exam){
		String sql = "Update exam.marks set "
				+ " resultlive=?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamOnlineMarksStats(ExamOrderBean exam){
		String sql = "Update exam.online_marks set "
				+ " resultlive=?  "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getLive(),
				exam.getYear(),
				exam.getMonth()
		});
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamPassfailMarksStats(ExamOrderBean exam){
		String yearMonth = ""+exam.getYear()+exam.getMonth();


		String sql = "Update exam.passfail set "
				+ " resultlive=?  "
				+ " where "
				+ " concat(writtenYear,writtenMonth) = ? or concat(assignmentYear,assignmentMonth) =? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				exam.getLive(),
				yearMonth,
				yearMonth
		});
	}*/
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int updateSubjectAsRIANV(String sapid,String program,String sem,String subject,String year,String month,String status,String studentType,String lastModifiedBy ){
		try{
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "Update exam.marks set "
				+ " writenscore = ?, "
				+ " processed = 'N', "
				+ " lastModifiedBy=?, "
				+ " lastModifiedDate=sysdate() "
				+ " where sapid = ? and year =? and month = ? and program = ? and writenscore not in ('','AB') and subject NOT IN ('Project', 'Module 4 - Project','Simulation: Mimic Pro','Simulation: Mimic Social') ";//2 Simulation added Vilpesh 20220514 
		parameters.add(status);
		parameters.add(lastModifiedBy);
		parameters.add(sapid);
		parameters.add(year);
		parameters.add(month);
		parameters.add(program);
		
		if( sem != null &&   !("".equals(sem))){
			sql = sql + " and sem = ? ";
			parameters.add(sem);
		}
		if( subject != null &&   !("".equals(subject))){
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
			/* commented temporarily as value is showing null in db
			 * if( studentType != null && !("".equals(studentType))){ sql = sql +
			 * " and studentType = ? "; parameters.add(studentType); }
			 */
	    Object [] args = parameters.toArray();
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = jdbcTemplate.update(sql, args);
		return i;
		}catch(Exception e){
			
			return 0;
		}
	}
	
	@Transactional(readOnly = true)//
	public String getSingleStudentSubjectScore(String sapid,String program,String sem,String year,String month,String subject){
		String sql = "SELECT roundedTotal FROM exam.online_marks "
				+ " where sapid = ? and year =? and month = ? and program = ? and sem = ? and subject = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		String writtenScore = (String)jdbcTemplate.queryForObject(sql, new Object[] { 
				sapid,
				year,
				month,
				program,
				sem,
				subject
				
		}, new SingleColumnRowMapper<String>(String.class));

		return writtenScore;
	}
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> searchSingleStudentMarksforRiaNv(StudentMarksBean studentMarks, String examMode) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "";
		//String countSql = "";//Unused so commented by Vilpesh on 2021-09-21.

		if("Online".equalsIgnoreCase(examMode)){
			sql = "SELECT * FROM exam.marks a where  a.writenscore not in ('','AB') and subject NOT IN ('Project', 'Module 4 - Project','Simulation: Mimic Pro','Simulation: Mimic Social')";//2 Simulation added by Vilpesh on 20220514
			//countSql = "SELECT count(*) FROM exam.marks a where a.writenscore not in ('','AB') and subject NOT IN ('Project', 'Module 4 - Project')";
		}else if("Offline".equalsIgnoreCase(examMode)){
			sql = "SELECT * FROM exam.marks a where a.writenscore not in ('','AB')  and subject NOT IN ('Project', 'Module 4 - Project','Simulation: Mimic Pro','Simulation: Mimic Social')";//2 Simulation added by Vilpesh on 20220514
			//countSql = "SELECT count(*) FROM exam.marks a where  a.writenscore not in ('','AB') and subject NOT IN ('Project', 'Module 4 - Project')";
		}
		
		if( studentMarks.getYear() != null &&   !("".equals(studentMarks.getYear()))){
			sql = sql + " and a.year = ? ";
			//countSql = countSql + " and a.year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( studentMarks.getMonth() != null &&   !("".equals(studentMarks.getMonth()))){
			sql = sql + " and a.month = ? ";
			//countSql = countSql + " and a.month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and a.sapid = ? ";
			//countSql = countSql + " and a.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and a.sem = ? ";
			//countSql = countSql + " and a.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and a.subject = ? ";
			//countSql = countSql + " and a.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}

		sql = sql + " order by sem, subject asc";

		Object[] args = parameters.toArray();

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,args, new BeanPropertyRowMapper(StudentMarksBean.class));

		return studentsMarksList;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String,StudentMarksBean> getStudentMarksBeforeRIANV(String sapid,String year,
			String month,String program) {
		HashMap<String,StudentMarksBean> studentMarks = new HashMap<String,StudentMarksBean>();
		try{
			String sql = "select * from exam.marks where sapid = ? and year =? and month = ? and program = ? and writenscore not in ('','AB','RIA','NV') and writenscore is not null and subject NOT IN ('Project', 'Module 4 - Project','Simulation: Mimic Pro','Simulation: Mimic Social');" ;//2 Simulation added by Vilpesh 20220514
			ArrayList<StudentMarksBean> beanList = (ArrayList<StudentMarksBean>)jdbcTemplate.query(sql, new Object[]{sapid,year,month,program}, new BeanPropertyRowMapper(StudentMarksBean.class));
		
			for(StudentMarksBean bean: beanList){
				String key = bean.getSubject()+"|"+bean.getYear()+"|"+bean.getMonth();
				if(!studentMarks.containsKey(key)){
					studentMarks.put(key, bean);
				}
			}
			return studentMarks;
		}catch(Exception e){
			
			return studentMarks;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int saveStudentMarksBeforeRIANV(StudentMarksBean studentMarks){
		try{
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "Update exam.marks set "
				+ " writtenBeforeRIANV = ? "
				+ " where sapid = ? and year =? and month = ? and program = ? and subject =? and writenscore=?";
		parameters.add(studentMarks.getWritenscore());
		parameters.add(studentMarks.getSapid());
		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());
		parameters.add(studentMarks.getProgram());
		parameters.add(studentMarks.getSubject());
		parameters.add(studentMarks.getWritenscore());
		
	    Object [] args = parameters.toArray();
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = jdbcTemplate.update(sql, args);
		return i;
		}catch(Exception e){
			
			return 0;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int updateSubjectToPreviousScore(String sapid,String program,String sem,String subject,String year,String month,String status,String studentType,String lastModifiedBy ){
		try{
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "Update exam.marks set "
				+ " writenscore = ?, "
				+ " processed = 'N', "
				+ " lastModifiedBy=?, "
				+ " lastModifiedDate=sysdate() "
				+ " where sapid = ? and year =? and month = ? and program = ? and writenscore not in ('','AB') and subject NOT IN ('Project', 'Module 4 - Project','Simulation: Mimic Pro','Simulation: Mimic Social')";//2 Simulation added by Vilpesh 20220514
		parameters.add(status);
		parameters.add(lastModifiedBy);
		parameters.add(sapid);
		parameters.add(year);
		parameters.add(month);
		parameters.add(program);
		
		if( sem != null &&   !("".equals(sem))){
			sql = sql + " and sem = ? ";
			parameters.add(sem);
		}
		if( subject != null &&   !("".equals(subject))){
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if( studentType != null &&   !("".equals(studentType))){
			sql = sql + " and studentType = ? ";
			parameters.add(studentType);
		}
	    Object [] args = parameters.toArray();
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = jdbcTemplate.update(sql, args);
		return i;
		}catch(Exception e){
			
			return 0;
		}
	}
	
	@Transactional(readOnly = true)//
	public String getStudentPreviousScore(String sapid,String year,
			String month,String program,String subject) {
		HashMap<String,StudentMarksBean> studentMarks = new HashMap<String,StudentMarksBean>();
		String str="";
		try{
			String sql = "select writtenBeforeRIANV from exam.marks where sapid = ? and year =? and month = ? and program = ? and subject = ?" ;
			str = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapid,year,month,program,subject}, new SingleColumnRowMapper(String.class));
			return str;
		}catch(Exception e){
			
			return str;
		}
	}	
	
	@Transactional(readOnly = true)//
	public List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmitted(ExamBookingTransactionBean searchBean)
	{
	   jdbcTemplate = new JdbcTemplate(dataSource);
	   List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmittedList1 = new ArrayList<ExamBookingTransactionBean>();
	   List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmittedList2 = new ArrayList<ExamBookingTransactionBean>();
		String year = searchBean.getYear();
		String month = searchBean.getMonth();
		
		String sql = "select e.sapid,e.year,e.month,e.subject,e.createdBy,e.createdDate,e.lastModifiedBy,e.lastModifiedDate, r.program,r.sem "
				+ " from exam.examfeeexemptsubject e ,exam.registration r "
				+ " where "
				+ " r.sapid = e.sapid and "
				+ " r.sem in ("+PROJECT_APPLICABLE_PROGRAM_SEM_LIST+") and "
				+ " e.month = ? and "
				+ " e.year = ?  and "
				//+ " e.subject IN ('Project', 'Module 4 - Project')"
				+ " e.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Vilpesh on 2022-03-04
				+ " and e.sapid  not in (select distinct ps.sapid from exam.projectsubmission ps where ps.year = ? and ps.month = ?)"
				+ " and e.sapid not in (select distinct eb.sapid from exam.exambookings eb where eb.year=? and eb.month=? and eb.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " and eb.booked ='Y')";
		
		
		String sql2 = "select e.sapid,e.year,e.month,e.subject,e.createdBy,e.createdDate,e.lastModifiedBy,e.lastModifiedDate, s.program "
				+ " from exam.examfeeexemptsubject e ,exam.students s "
				+ " where "
				+ " s.sapid = e.sapid and "
				+ " e.month = ? and "
				+ " e.year = ?  and "
				//+ " e.subject IN ('Project', 'Module 4 - Project')"
				+ " e.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Vilpesh on 2022-03-04
				+ " and e.sapid  not in (select distinct ps.sapid from exam.projectsubmission ps where ps.year = ? and ps.month = ?)"
				+ " and e.sapid not in (select distinct eb.sapid from exam.exambookings eb where eb.year=? and eb.month=? and eb.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " and eb.booked ='Y')"
				+ " and e.sapid not in (select distinct e.sapid "
				+ " from exam.examfeeexemptsubject e ,exam.registration r "
				+ " where "
				+ " r.sapid = e.sapid and "
				+ " r.sem in ("+PROJECT_APPLICABLE_PROGRAM_SEM_LIST+") and "
				+ " e.month = ? and "
				+ " e.year = ?  and "
				//+ " e.subject IN ('Project', 'Module 4 - Project')"
				+ " e.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Vilpesh on 2022-03-04
				+ " and e.sapid  not in (select distinct ps.sapid from exam.projectsubmission ps where ps.year = ? and ps.month = ?)"
				+ " and e.sapid not in (select distinct eb.sapid from exam.exambookings eb where eb.year=? and eb.month=? and eb.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " and eb.booked ='Y'))";
		
		projectFeeExemptAndNotSubmittedList1 = jdbcTemplate.query(sql.toString(),new Object[]{month,year,year,month,year,month},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		projectFeeExemptAndNotSubmittedList2 = jdbcTemplate.query(sql2.toString(),new Object[]{month,year,year,month,year,month,month,year,year,month,year,month},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		if(projectFeeExemptAndNotSubmittedList2.size()>0){
			for(ExamBookingTransactionBean b : projectFeeExemptAndNotSubmittedList2){
				b.setSem(eligibilityService.getProjectApplicableProgramSem(b.getProgram()));
			}
			
			projectFeeExemptAndNotSubmittedList1.addAll(projectFeeExemptAndNotSubmittedList2);
		}
		return projectFeeExemptAndNotSubmittedList1;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, ProgramExamBean> getProgramProgramStructureMap(String programStructure) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.programs where programStructure = ? AND program not in ('EPBM','MPDV')";
		List<ProgramExamBean> programList = jdbcTemplate.query(sql,new Object[]{programStructure}, new BeanPropertyRowMapper(ProgramExamBean.class));
		HashMap<String, ProgramExamBean> programMap = new HashMap<String, ProgramExamBean>();

		for (int i = 0; i < programList.size(); i++) {
			programMap.put(programList.get(i).getProgram()+"-"+programList.get(i).getProgramStructure(), programList.get(i));
		}
		
		return programMap;
	}
	
	@Transactional(readOnly = true)//
	public CenterExamBean findSingleCenterDetails(String centerCode) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		CenterExamBean centerDetail = new CenterExamBean();
		String sql = " Select * from exam.centers where centerCode = ? ";

		try{
				centerDetail = (CenterExamBean)jdbcTemplate.queryForObject(sql, new Object[]{centerCode}, new BeanPropertyRowMapper(CenterExamBean.class));
		}catch(Exception e){

			return centerDetail;
		}
		return centerDetail;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public int updateStudentsCenterName(String centerCode, String centerName) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE exam.students set centerName = ? , lastModifiedBy = 'Salesforce Admin',  lastModifiedDate = sysdate() where centerCode = ? ";
		int i = 0;
		i	= jdbcTemplate.update(sql, new Object[] { 
				centerName,
				centerCode
		});
		return i;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, BigDecimal> getExecutiveExamOrderMap(){
				
		HashMap<String, BigDecimal> hashMap = new HashMap<>();
		try {
			final String sql = " select * from exam.executive_examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExecutiveExamOrderBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));
			hashMap = new HashMap<String, BigDecimal>();
			for (ExecutiveExamOrderBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear()+row.getAcadMonth()+row.getAcadYear(), BigDecimal.valueOf(Double.parseDouble(row.getOrder())));
			}

			return hashMap;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return new HashMap<>();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			
			return new HashMap<>();
		}
				
	}

	//added to show corporate timetable in view timetable page
	@Transactional(readOnly = true)//
	public List<TimetableBean> getAdminTimetableList(String year, String month, String corporateType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.corporate_timetable a where  a.examyear = ? and  a.examMonth = ? and a.ic = ? order by program, PrgmStructApplicable, sem, date, startTime asc";
		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new Object[]{year, month, corporateType}, new BeanPropertyRowMapper(TimetableBean.class));
		return timeTableList;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getListOfStudentApplicableforMax8Grace(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select m.* , p.total from exam.marks m ,exam.passfail p   " + 
				"where  " + 
				" m.subject='Business Statistics'   " + 
				"and m.year=p.writtenYear   " + 
				"and m.month=p.writtenMonth   " + 
				"and m.subject=p.subject  " + 
				"and m.sapid=p.sapid  " + 
				"and p.resultProcessedYear=2019   " + 
				"and p.resultProcessedMonth='Jun' " + 
				"and p.writtenYear=2019   " + 
				"and p.writtenMonth='Jun'  " + 
				"and p.writtenscore not in ('RIA','NV','AB','') " + 
				"and p.writtenscore > 0  " + 
				"and p.assignmentscore not in ('ANS') " + 
				"and p.isPass='N'   " + 
				"and p.total between 41 and 48 ";// + 
				//"and m.writenscore <> p.writtenscore";
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentMarksList;
	}	
	
	@Transactional(readOnly = true)//
	public boolean isProjectApplicable(String program,String sem, String programStructure,String sapid){
		String sql = "select count(ps.subject) from exam.program_subject ps"
				+ " inner join" + 
				"   exam.students s" + 
				"   on" + 
				"   s.program=ps.program"
				+ " where s.sapid = ? "
				+ " and ps.subject IN ('Project')"
				+ " and ps.program=? "
				+ " and ps.sem=? "
				+ " and ps.prgmStructApplicable = ?"
				+ " and  str_to_date(concat(s.enrollmentYear+'-'+s.enrollmentMonth+'-01'), '%Y,%b,%d') >= '2011-07-01' " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid,program,sem,programStructure},Integer.class);
		if (count>0) {
			return true;
		}else {
			return false;
		}
	}

	@Transactional(readOnly = true)//
	public boolean isModuleProjectApplicable(String program,String sem, String programStructure,String sapid){
		String sql = "select count(ps.subject) from exam.program_subject ps"
				+ " inner join" + 
				"   exam.students s" + 
				"   on" + 
				"   s.program=ps.program"
				+ " where s.sapid = ? "
				+ " and ps.subject='Module 4 - Project' "
				+ " and ps.program=? "
				+ " and ps.sem=? "
				+ " and ps.prgmStructApplicable = ?"
				+ " and  str_to_date(concat(s.enrollmentYear+'-'+s.enrollmentMonth+'-01'), '%Y,%b,%d') >= '2011-07-01' " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid,program,sem,programStructure},Integer.class);
		if (count>0) {
			return true;
		}else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)//
	public boolean isMimicProApplicable(String program,String sem, String programStructure,String sapid){
		String sql = "select count(ps.subject) from exam.program_subject ps"
				+ " inner join" + 
				"   exam.students s" + 
				"   on" + 
				"   s.program=ps.program"
				+ " where s.sapid = ? "
				+ " and ps.subject='Simulation: Mimic Pro' "
				+ " and ps.program=? "
				+ " and ps.sem=? "
				+ " and ps.prgmStructApplicable = ?"
				+ " and  str_to_date(concat(s.enrollmentYear+'-'+s.enrollmentMonth+'-01'), '%Y,%b,%d') >= '2011-07-01' " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid,program,sem,programStructure},Integer.class);
		if (count>0) {
			return true;
		}else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)//
	public boolean isMimicSocialApplicable(String program,String sem, String programStructure,String sapid){
		String sql = "select count(ps.subject) from exam.program_subject ps"
				+ " inner join" + 
				"   exam.students s" + 
				"   on" + 
				"   s.program=ps.program"
				+ " where s.sapid = ? "
				+ " and ps.subject='Simulation: Mimic Social' "
				+ " and ps.program=? "
				+ " and ps.sem=? "
				+ " and ps.prgmStructApplicable = ?"
				+ " and  str_to_date(concat(s.enrollmentYear+'-'+s.enrollmentMonth+'-01'), '%Y,%b,%d') >= '2011-07-01' " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid,program,sem,programStructure},Integer.class);
		if (count>0) {
			return true;
		}else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)//
	public boolean isStukentApplicable(String userId) {
			
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from lti.lti_users where userId = ? and roles = 'Instructor' ";
		int count = 0;

		try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{userId},Integer.class);
		} catch (Exception e) {
			
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = true)//
	public List<MettlResponseBean> getExamScheduleListForMbaWx(Integer assessmentId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MettlResponseBean> scheduleList = new ArrayList<MettlResponseBean>();

		String sql = " SELECT * FROM `exam`.`exams_schedule` WHERE `assessments_id` = ? ";
		
		try {
			scheduleList = jdbcTemplate.query(
				sql, 
				new Object[] {
					assessmentId
				}, 
				new BeanPropertyRowMapper<MettlResponseBean>(MettlResponseBean.class)
			);
		}catch (Exception e) {
			
		}
				
		return scheduleList;
	}
	
	@Transactional(readOnly = true)//
	public List<MettlResponseBean> getExamAssessmentsListForMbaWx() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();

		String sql = " SELECT * FROM `exam`.`exams_assessments` ";

		try {
			assessmentList = jdbcTemplate.query(
				sql, 
				new BeanPropertyRowMapper<MettlResponseBean>(MettlResponseBean.class)
			);
		}catch (Exception e) {
			
		}

		return assessmentList;
	}

	@Transactional(readOnly = true)//
	public List<TEEResultStudentDetailsBean> getABRecordsMBAWX( MettlResponseBean bean ) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ "SELECT "
			    +" `tum`.`timebound_subject_config_id` AS `timeboundId`, "
			    +" `tm`.`schedule_id` AS `scheduleId`, "
			    +" `tum`.`userId` AS `sapid`, "
			    +" `students`.`firstName` AS `firstName`, "
			    +" `students`.`lastName` AS `lastName`, "
			    +" `ssc`.`batchId` AS `batchId`, "
			    +" `batch`.`name` AS `batchName`, "
			    +" `students`.`program` AS `program`, "
			    +" `pss`.`sem` AS `sem`, "
			    +" `pss`.`subject` AS `subject`, "
			    +" 'AB' AS `score`, "
			    // If max score is present use that, else use a default of 30
			    +" coalesce(`tm`.`max_score`, '30') AS `maxScore`, "
			    +" `pss`.`id` AS `programSemSubjectId` "
		    +" FROM `lti`.`timebound_user_mapping` `tum` "

		    /* get students with marks. */
		    +" LEFT JOIN `exam`.`tee_marks` `tm` "
		    +" ON `tum`.`timebound_subject_config_id` = `tm`.`timebound_id` AND `tum`.`userId` = `tm`.`sapid` "

		    /* get Subject and semester details */
		    +" INNER JOIN  `lti`.`student_subject_config` `ssc` "
		    +" ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

		    /* get Subject and semester details */
		    +" INNER JOIN `exam`.`program_sem_subject` `pss` "
		    +" ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

		    /* get Student details */
		    +" INNER JOIN `exam`.`students` `students` "
		    +" ON `students`.`sapid` = `tum`.`userId` "

		    /* get batch details */
		    +" INNER JOIN `exam`.`batch` `batch` "
		    +" ON `batch`.`id` = `ssc`.`batchId` "

		    +" WHERE "
		    /* Get only students */
		    +" `role` = 'Student' "

		    +" AND `tum`.`timebound_subject_config_id` = ? "
		    +" AND ( "
			    +" `tm`.`score` IS NULL "
			    +" OR `tm`.`score` = '' "
				+" OR `tm`.`status` IN ( 'Not Attempted', 'AB' ) "
				+" OR `tm`.`status` IS NULL "
		    +" ) "
		    /* Remove test users from the list */
    			+ " AND `tum`.`userId` NOT IN (  " + TEST_USER_SAPIDS + ")"
		    /* Remove terminated users from the list */
			+ " AND NOT(`students`.`programStatus` <=> 'Program Terminated') "
			/* Remove RIA, NV */
			+ " AND (`tm`.`status` NOT IN ( 'RIA', 'NV' ) OR `tm`.`status` IS NULL ) ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getTimebound_id() );
		//parameters.add( TEST_USER_SAPIDS );
		
		return jdbcTemplate.query(
			sql, 
			parameters.toArray(), 
			new BeanPropertyRowMapper<TEEResultStudentDetailsBean>(TEEResultStudentDetailsBean.class)
		);
	}
	
	@Transactional(readOnly = true)//
	public List<TEEResultStudentDetailsBean> getABRecordsFor100MarksExamMBAWX( MettlResponseBean bean ) {
		String sql = ""
			+ "SELECT "
			    +" `b`.`timeboundId` AS `timeboundId`, "
			    +" `tm`.`schedule_id` AS `scheduleId`, "
			    +" `b`.`sapid` AS `sapid`, "
			    +" `students`.`firstName` AS `firstName`, "
			    +" `students`.`lastName` AS `lastName`, "
			    +" `ssc`.`batchId` AS `batchId`, "
			    +" `batch`.`name` AS `batchName`, "
			    +" `students`.`program` AS `program`, "
			    +" `pss`.`sem` AS `sem`, "
			    +" `pss`.`subject` AS `subject`, "
			    +" 'AB' AS `score`, "
			    +" coalesce(`tm`.`max_score`, '100') AS `maxScore`, "
			    +" `pss`.`id` AS `programSemSubjectId` "
			   
		    +" FROM `exam`.`mba_wx_bookings` `b` "

		    /* get students with marks. */
		    +" LEFT JOIN `exam`.`tee_marks` `tm` "
		    +" ON `b`.`timeboundId` = `tm`.`timebound_id` AND `b`.`sapid` = `tm`.`sapid` "

		    /* get Subject and semester details */
		    +" INNER JOIN  `lti`.`student_subject_config` `ssc` "
		    +" ON `ssc`.`id` = `b`.`timeboundId` "

		    /* get Subject and semester details */
		    +" INNER JOIN `exam`.`program_sem_subject` `pss` "
		    +" ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

		    /* get Student details */
		    +" INNER JOIN `exam`.`students` `students` "
		    +" ON `students`.`sapid` = `b`.`sapid` "

		    /* get batch details */
		    +" INNER JOIN `exam`.`batch` `batch` "
		    +" ON `batch`.`id` = `ssc`.`batchId` "
		   
		    +" WHERE "
		    /* Get only students */
		    +" `b`.`bookingStatus` = 'Y' "
		   // +" AND `tm`.`prgm_sem_subj_id` = ? "
		    +" AND `b`.`timeboundId` = ? "
		    +" AND ( "
			    +" `tm`.`score` IS NULL "
			    + "OR `tm`.`status` = 'Not Attempted' "
		    +" ) AND " 
			    + " ( `tm`.`status` not in ('AB','RIA','NV','')  or `tm`.`status` IS NULL) "
		    /* Remove test users from the list */
    			+ " AND `b`.`sapid` NOT IN (  " + TEST_USER_SAPIDS + ")"
		    /* Remove terminated users from the list */
			+ " AND NOT(`students`.`programStatus` <=> 'Program Terminated') ";

		return jdbcTemplate.query(
			sql, 
			new Object[] { bean.getTimebound_id() }, 
			new BeanPropertyRowMapper<TEEResultStudentDetailsBean>(TEEResultStudentDetailsBean.class)
		);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void UpdateABRecordsMBAWX(TEEResultStudentDetailsBean studentMarksBean, String userId) {
		upsertMarksMBAWX( studentMarksBean, userId );
		if(!studentMarksBean.getTimeboundId().equals("253") || !studentMarksBean.getTimeboundId().equals("271")) {
			upsertMarksHistoryMBAWX( studentMarksBean, userId );
		}
		
	}

	@Transactional(readOnly = true)//
	public List<TEEResultStudentDetailsBean> getProjectABRecordsMBAWX( MettlResponseBean bean ) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ "SELECT "
			    +" `tum`.`timebound_subject_config_id` AS `timeboundId`, "
			    +" `tm`.`schedule_id` AS `scheduleId`, "
			    +" `tum`.`userId` AS `sapid`, "
			    +" `students`.`firstName` AS `firstName`, "
			    +" `students`.`lastName` AS `lastName`, "
			    +" `ssc`.`batchId` AS `batchId`, "
			    +" `batch`.`name` AS `batchName`, "
			    +" `students`.`program` AS `program`, "
			    +" `pss`.`sem` AS `sem`, "
			    +" `pss`.`subject` AS `subject`, "
			    +" 'AB' AS `score`, "
			    // If max score is present use that, else use a default of 30
			    +" coalesce(`tm`.`max_score`, '60') AS `maxScore`, "
			    +" `pss`.`id` AS `programSemSubjectId` "
		    +" FROM `lti`.`timebound_user_mapping` `tum` "

		    /* get students with marks. */
		    +" LEFT JOIN `exam`.`tee_marks` `tm` "
		    +" ON `tum`.`timebound_subject_config_id` = `tm`.`timebound_id` AND `tum`.`userId` = `tm`.`sapid` "

		    /* get Subject and semester details */
		    +" INNER JOIN  `lti`.`student_subject_config` `ssc` "
		    +" ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

		    /* get Subject and semester details */
		    +" INNER JOIN `exam`.`program_sem_subject` `pss` "
		    +" ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

		    /* get Student details */
		    +" INNER JOIN `exam`.`students` `students` "
		    +" ON `students`.`sapid` = `tum`.`userId` "

		    /* get batch details */
		    +" INNER JOIN `exam`.`batch` `batch` "
		    +" ON `batch`.`id` = `ssc`.`batchId` "

		    +" WHERE "
		    /* Get only students */
		    +" `role` = 'Student' "

		    +" AND `tum`.`timebound_subject_config_id` = ? "
		    +" AND ( "
			    +" `tm`.`score` IS NULL "
			    +" OR `tm`.`score` = '' "
				+" OR `tm`.`status` IN ( 'Not Attempted', 'AB' ) "
				+" OR `tm`.`status` IS NULL "
		    +" ) "
		    /* Remove test users from the list */
    			+ " AND `tum`.`userId` NOT IN (  " + TEST_USER_SAPIDS + ")"
		    /* Remove terminated users from the list */
			+ " AND NOT(`students`.`programStatus` <=> 'Program Terminated') "
			/* Remove RIA, NV */
			+ " AND (`tm`.`status` NOT IN ( 'RIA', 'NV' ) OR `tm`.`status` IS NULL ) "
			
			// TODO: Remove this check when subject level check is added for project
			+ " AND `tum`.`timebound_subject_config_id` IN (253, 271) ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getTimebound_id() );
		//parameters.add( TEST_USER_SAPIDS );
		
		return jdbcTemplate.query(
			sql, 
			parameters.toArray(), 
			new BeanPropertyRowMapper<TEEResultStudentDetailsBean>(TEEResultStudentDetailsBean.class)
		);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void UpdateProjectABRecordsMBAWX(TEEResultStudentDetailsBean studentMarksBean, String userId) {
		upsertMarksMBAWX( studentMarksBean, userId );
	}

	public void upsertMarksMBAWX(TEEResultStudentDetailsBean bean, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`tee_marks`"
			+ " ("
				+ " `prgm_sem_subj_id`, `timebound_id`, `schedule_id`, `sapid`, "
				+ " `student_name`, `score`, `max_score`, "
				+ " `createdBy`, `lastModifiedBy`,`processed`,`status` "
			+ " )"
			+ " VALUES"
			+ " ("
				+ " ?, ?, ?, ?, "
				+ " ?, 0, ?, "
				+ " ?, ? ,'N',"
				+ " ?"
			+ " ) ON DUPLICATE KEY UPDATE `score` = 0, `lastModifiedBy` = ?, `processed` = 'N', status = ?";
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getProgramSemSubjectId() );
		parameters.add( bean.getTimeboundId() );
		parameters.add( bean.getScheduleId() );
		parameters.add( bean.getSapid() );
		parameters.add( bean.getFirstName() + " " + bean.getLastName() );
		
		parameters.add( bean.getMaxScore() );
		parameters.add( userId );
		parameters.add( userId );
		parameters.add( bean.getScore() );
		parameters.add( userId );
		parameters.add( bean.getScore() );
		
		jdbcTemplate.update(sql, parameters.toArray());
		
	}
	
	public void upsertMarksHistoryMBAWX(TEEResultStudentDetailsBean bean, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`tee_marks_history`"
			+ " ("
				+ " `prgm_sem_subj_id`, `timebound_id`, `schedule_id`, `sapid`, "
				+ " `student_name`, `score`, `max_score`, "
				+ " `createdBy`, `lastModifiedBy`,`processed`,`status` "
			+ " )"
			+ " VALUES"
			+ " ("
				+ " ?, ?, ?, ?, "
				+ " ?, 0, ?, "
				+ " ?, ? ,'N',"
				+ " ?"
			+ " ) ON DUPLICATE KEY UPDATE `score` = 0, `lastModifiedBy` = ?, `processed` = 'N', status = ?";
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getProgramSemSubjectId() );
		parameters.add( bean.getTimeboundId() );
		parameters.add( bean.getScheduleId() );
		parameters.add( bean.getSapid() );
		parameters.add( bean.getFirstName() + " " + bean.getLastName() );
		
		parameters.add( bean.getMaxScore() );
		parameters.add( userId );
		parameters.add( userId );
		parameters.add( bean.getScore() );
		parameters.add( userId );
		parameters.add( bean.getScore() );
		
		jdbcTemplate.update(sql, parameters.toArray());
		
	}

	@Transactional(readOnly = true)//
	public List<EMBABatchSubjectBean> getBatchesForMBAWX() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EMBABatchSubjectBean> batchesList = new ArrayList<EMBABatchSubjectBean>();

		String sql = " SELECT `name` AS `batchName`,`id` AS `batchId` FROM `exam`.`batch` ";

		try {
			batchesList = jdbcTemplate.query(
				sql, 
				new BeanPropertyRowMapper<EMBABatchSubjectBean>(EMBABatchSubjectBean.class)
			);
		}catch (Exception e) {
			
		}
		return batchesList;
	}
	
	@Transactional(readOnly = true)//
	public List<EMBABatchSubjectBean> getSubjectsForBatch(String batchId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EMBABatchSubjectBean> subjectsList = new ArrayList<EMBABatchSubjectBean>();

		String sql = " SELECT "
				+ " `ssc`.`batchId` AS `batchId`, "
				+ " `ssc`.`id` AS `timeboundId`, "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`id` AS `subjectId` "
				+ " FROM "
				+ " lti.student_subject_config `ssc` "
				
	    		/* get Subject details */
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
		    		+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				+ " WHERE `ssc`.`batchId` = ?";

		try {
			subjectsList = jdbcTemplate.query(
				sql, 
				new Object[] {batchId},
				new BeanPropertyRowMapper<EMBABatchSubjectBean>(EMBABatchSubjectBean.class)
			);
		}catch (Exception e) {
			
		}
		return subjectsList;
	}
	
	//get corporate names from db
	@Transactional(readOnly = true)//
	public List<String> getAllCorporateNames(){
		List<String> corporateList= new ArrayList<String>();
		String sql ="select name from exam.consumer_type where isCorporate=1";
		try {
			corporateList = jdbcTemplate.query(
				sql,
				new SingleColumnRowMapper(String.class)
			);
		}catch (Exception e) {
			
	}
		return corporateList;
	}
	
	@Transactional(readOnly = true)//
	public FacultyExamBean isFaculty(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? group by facultyId";
		FacultyExamBean faculty = (FacultyExamBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(FacultyExamBean.class));
		return faculty;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<ProgramSubjectMappingExamBean> getWaivedInSubjects(StudentExamBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,subject from exam.program_sem_subject where consumerProgramStructureId = ? and "
				+ " subject not in ( select if(subject = 'Business Communication and Etiquette','Business Communication',subject) as subject from exam.passfail where sapid in (?,?) and isPass = 'Y') and sem < ? ";
		return (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId(), student.getPreviousStudentId(), student.getSapid(), student.getSem()}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<StudentExamBean> getOct2020Students() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =" select * from exam.students where enrollmentYear = 2020 and enrollmentMonth = 'Oct' " + 
					" and sapid in (select sapid from exam.registration where month = 'Jul' and year = 2021 " +
					" and sem = 4 and consumerProgramStructureId =111) order by sapid limit 1";
		ArrayList<StudentExamBean> extensionList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{}, 
				new BeanPropertyRowMapper(StudentExamBean.class));
		return extensionList;
		
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, ProgramSubjectMappingExamBean> getMapOfSubjectDetailsAndPssId111(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_sem_subject where consumerProgramStructureId = 111";
		ArrayList<ProgramSubjectMappingExamBean> subjectList = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new Object[]{}, 
				new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
	
		HashMap<String, ProgramSubjectMappingExamBean> mapOfSubjectDetailsAndPssId = new HashMap<>();
		for (ProgramSubjectMappingExamBean subject : subjectList) {
			mapOfSubjectDetailsAndPssId.put(Integer.toString(subject.getId()), subject);
		}
		return mapOfSubjectDetailsAndPssId;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, ProgramSubjectMappingExamBean> getMapOfSubjectDetailsAndSubject151(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_sem_subject where consumerProgramStructureId = 151";
		ArrayList<ProgramSubjectMappingExamBean> subjectList = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new Object[]{}, 
				new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
	
		HashMap<String, ProgramSubjectMappingExamBean> mapOfSubjectDetailsAndPssId = new HashMap<>();
		for (ProgramSubjectMappingExamBean subject : subjectList) {
			mapOfSubjectDetailsAndPssId.put(subject.getSubject(), subject);
		}
		return mapOfSubjectDetailsAndPssId;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<ProgramSubjectMappingExamBean> getAllPSSIds(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" select *, prgm_sem_subj_id as id from lti.timebound_user_mapping tum " + 
					" inner join lti.student_subject_config ssc on tum.timebound_subject_config_id = ssc.id " + 
					" where userId = ?";
		ArrayList<ProgramSubjectMappingExamBean> pssIdList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[]{sapid}, 
				new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void upsertNewStructureMapping(String sapid, String oldPss, int newPss, String sem, String acadYear, String acadsMonth, String examYear, String examMonth) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" INSERT INTO exam.mbawx_change_structure_mapping (sapid, oldPssId, newPssId, sem, acadYear, acadMonth, examYear, examMonth) " + 
					" VALUES (?,?,?,?,?,?,?,?) " +
					" ON DUPLICATE KEY UPDATE sapid = ?, oldPssId = ?, newPssId = ?, sem = ?, acadYear = ?, acadMonth = ?, examYear = ?, examMonth = ?";
		jdbcTemplate.update(sql, new Object[] {sapid, oldPss, newPss, sem, acadYear, acadsMonth, examYear, examMonth,
											   sapid, oldPss, newPss, sem, acadYear, acadsMonth, examYear, examMonth});

	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<AssignmentStatusBean> getQuickAssignmentStatus(int pageNo, int pageSize, AssignmentFileBean assignmentStatus, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select qas.year as examYear, qas.month as examMonth, qas.sem, qas.sapid, qas.subject, s.centerName, qas.status as submitted "
				+ " from exam.quick_assignmentsubmission qas,exam.students s "
				+ " where qas.sapid = s.sapid and qas.status = 'Submitted' ";
		
		String countSql = "select count(*) from exam.quick_assignmentsubmission qas,exam.students s "
				+ " where qas.sapid = s.sapid and qas.status = 'Submitted' ";

		if( assignmentStatus.getConsumerProgramStructureId() != null &&   !("".equals(assignmentStatus.getConsumerProgramStructureId()))){
			sql = sql + " and s.consumerProgramStructureId in ("+ assignmentStatus.getConsumerProgramStructureId() +")  ";
			countSql = countSql + "and s.consumerProgramStructureId in ("+ assignmentStatus.getConsumerProgramStructureId() +")  ";
			}
		
		
		if( assignmentStatus.getYear() != null &&   !("".equals(assignmentStatus.getYear()))){
			sql = sql + " and qas.year = ? ";
			countSql = countSql + " and qas.year = ? ";
			parameters.add(assignmentStatus.getYear());
		}

		if( assignmentStatus.getMonth() != null &&   !("".equals(assignmentStatus.getMonth()))){
			sql = sql + " and qas.month = ? ";
			countSql = countSql + " and qas.month = ? ";
			parameters.add(assignmentStatus.getMonth());
		}

		if( assignmentStatus.getSapId() != null &&   !("".equals(assignmentStatus.getSapId()))){
			sql = sql + " and qas.sapid = ? ";
			countSql = countSql + " and qas.sapid = ? ";
			parameters.add(assignmentStatus.getSapId());
		}

		if( assignmentStatus.getSubject() != null &&   !("".equals(assignmentStatus.getSubject()))){
			sql = sql + " and qas.subject = ? ";
			countSql = countSql + " and qas.subject = ? ";
			parameters.add(assignmentStatus.getSubject());
		}
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " order by qas.sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentStatusBean> pagingHelper = new PaginationHelper<AssignmentStatusBean>();
		Page<AssignmentStatusBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(AssignmentStatusBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)//
	public StudentExamBean getStudentDetails(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    s.*, p.programname AS programForHeader " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.programs p ON p.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"WHERE " + 
				"    s.sapid = ? ";
		StudentExamBean student = jdbcTemplate.queryForObject(sql, new Object[] {sapid}, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		return student ;
	}
	
	private int updateWrittenBeforeRIANV(StudentMarksBean m, JdbcTemplate jdbcTemplate2) {
		String sql="Update exam.marks set writtenBeforeRIANV=writenscore "
				+ " where year =? and month=? and sapid = ? and subject = ? ";
		
		
		return jdbcTemplate2.update(sql,new Object[] {
			m.getYear(),m.getMonth(),m.getSapid(),m.getSubject()	
		});
		
	}

	public StudentMarksBean updateWrittenScoreDataByWrittenBeforeRIANV(StudentMarksBean m) {
		JdbcTemplate jdbcTemplate= new JdbcTemplate(dataSource);
		StudentMarksBean studentMarksBean=  new StudentMarksBean();
		try {
		String sql="Update exam.marks set writenscore=writtenBeforeRIANV "
				+ " where year =? and month=? and sapid = ? and subject = ? ";
		
		jdbcTemplate.update(sql,new Object[] {
				m.getYear(),m.getMonth(),m.getSapid(),m.getSubject()	
			});
		
		String sql1="select * from exam.marks "
				+ " where year =? and month=? and sapid = ? and subject = ? ";
		return (StudentMarksBean) jdbcTemplate.queryForObject(sql1,new Object[] {
				m.getYear(),m.getMonth(),m.getSapid(),m.getSubject()	
			},new BeanPropertyRowMapper(StudentMarksBean.class));
	
	}
	catch(Exception e) {
		return  studentMarksBean;
	}
}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getApplicableStudentForProject(StudentMarksBean studentDetails){
		JdbcTemplate jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql ="SELECT " + 
				"    rs.sapid, pss.subject , CONCAT(st.firstName ,st.lastName) as studentname, eo.order AS examorder, rs.program,rs.sem,eo.month,eo.year " + 
				" FROM " + 
				"   exam.registration rs  " + 
				"        INNER JOIN " + 
				"   exam.examorder eo ON " + 
				"		rs.month = eo.acadMonth " + 
				"        AND rs.`year` = eo.`year` " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON  " + 
				"    pss.consumerProgramStructureId = rs.consumerProgramStructureId " + 
				"    and rs.sem = pss.sem " +
				"    INNER JOIN exam.students st on "+
				"     st.sapid = rs.sapid " +
				"	WHERE " + 
				"    pss.subject in ('Project' , 'Module 4 - Project') " + 
				"        AND eo.month = ? " + 
				"        AND eo.year = ? ";
		
		List<StudentMarksBean> student =	jdbcTemplate.query(sql,new Object[] {studentDetails.getMonth(),studentDetails.getYear()},new BeanPropertyRowMapper<>(StudentMarksBean.class));
		return student;
	}

	@Transactional(readOnly = true)
	public List<ExamBookingExamBean> getExamBookingDetails(StudentMarksBean bean) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select sapid from exam.exambookings where month = ? and year = ? and centerId = '-1' and booked = 'Y' ";
		List<ExamBookingExamBean> examBean = jdbcTemplate.query(sql,new Object[] {bean.getMonth(),bean.getYear()},new BeanPropertyRowMapper(ExamBookingExamBean.class));
		
		return examBean;
	}


	public int insertProjectNotBookedRecords(List<StudentMarksBean> finalStudnetListForNotBooked) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO exam.marks( year,month,examorder,sapid,studentname,program,sem,writenScore,"
				+ "remarks,createdby,createdDate,"
				+ "lastModifiedby,lastModifiedDate,processed,grno,subject) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?)";
				
		int count[]=	jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				StudentMarksBean bean = finalStudnetListForNotBooked.get(i);
				ps.setString(1, bean.getYear());
				ps.setString(2, bean.getMonth());
				ps.setString(3, bean.getExamorder());
				ps.setString(4, bean.getSapid());
				ps.setString(5, bean.getStudentname());
				ps.setString(6, bean.getProgram());
				ps.setInt(7, Integer.valueOf(bean.getSem()));
				ps.setString(8, "");
				ps.setString(9, bean.getRemarks());
				ps.setString(10, "admin");
				ps.setString(11, "admin");
				ps.setString(12, "N");
				ps.setString(13,"Not Available");
				ps.setString(14,bean.getSubject());
			}
			
			@Override
			public int getBatchSize() {
				
				return finalStudnetListForNotBooked.size();
			}
		});
			return count.length;
	
		
	}

	@Transactional(readOnly = true)
	public List<StudentMarksBean> getMarksDetailsFroProjectStudent(StudentMarksBean bean, String commaSepratedSubject) {
		JdbcTemplate jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql =" select sapid from exam.marks where subject in ("+commaSepratedSubject+")  and month =?  and year =?";
		List<StudentMarksBean> marks = jdbcTemplate.query(sql, new Object[] {bean.getMonth(),bean.getYear()},new BeanPropertyRowMapper<>(StudentMarksBean.class));
		return marks;
	}
	
	@Transactional(readOnly = true)
	public ExamOrderExamBean getExamOrderDetailsByExamMonthAndYear(StudentMarksBean bean) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="Select eo.order,eo.acadmonth,eo.month,eo.year from exam.examorder eo where eo.month =? and eo.year = ?";
		return (ExamOrderExamBean) jdbcTemplate.queryForObject(sql,
				new Object[] {bean.getMonth(),bean.getYear()}, new BeanPropertyRowMapper(ExamOrderExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getStudentRegistrationDetailsByExamMonthAndYear(String month,String year){
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    sapid, program, month, year, sem, consumerProgramStructureId " + 
				" FROM " + 
				"    exam.registration " + 
				" WHERE " + 
				"    month = ? AND year = ? ";
		return jdbcTemplate.query(sql, new Object[] {month,year},new BeanPropertyRowMapper(StudentMarksBean.class));
	}

	@Transactional(readOnly = true)
	public List<StudentMarksBean> getProgramSemSubjectDetailsByConsumerStructureId(String subject){
	
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT  " + 
				"    consumerProgramStructureId,subject,sem " + 
				" FROM " + 
				"    exam.program_sem_subject " + 
				" WHERE " + 		
				"      subject IN ("+subject+") ";
	
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(StudentMarksBean.class));
	}

	private String join(List<String> namesList) {
		return String.join(",", namesList.stream()
				.map(name -> ("'" + name + "'")).collect(Collectors.toList()));
	}


	public List<StudentMarksBean>getStudentMarks(StudentMarksBean studentMarks){
		
		jdbcTemplate=new JdbcTemplate(dataSource);
		ArrayList<Object> parameters=new ArrayList<Object>();
		
		String sql="SELECT " + 
				"    * " + 
				"FROM " + 
				"    exam.marks " + 
				"WHERE 1 = 1 " ;
		
		if(!StringUtils.isEmpty(studentMarks.getYear())){
			sql = sql + " and year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if( !StringUtils.isEmpty(studentMarks.getMonth())){
			sql = sql + " and month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if( !StringUtils.isEmpty(studentMarks.getGrno())){
			sql = sql + " and grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if( !StringUtils.isEmpty(studentMarks.getSapid())){
			sql = sql + " and sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		
		if (!CollectionUtils.isEmpty(studentMarks.getWrittenScoreType())) {
			if (studentMarks.getWrittenScoreType().contains("Attempted")) {
				String commaSepratedOfWrittenScoreType = join(studentMarks.getWrittenScoreType());
				sql = sql + " AND (writenscore REGEXP '^[0-9]+$' OR  writenscore in ("
						+ commaSepratedOfWrittenScoreType + ")) ";	
			}

			else {
				String commaSepratedOfWrittenScoreType = join(studentMarks.getWrittenScoreType());
				sql = sql + "and writenscore in (" + commaSepratedOfWrittenScoreType + ")";
			}
		}

		if( !StringUtils.isEmpty(studentMarks.getStudentname())){
			sql = sql + " and studentname like  ? ";
			parameters.add("%"+studentMarks.getStudentname()+"%");
		}
		if( !StringUtils.isEmpty(studentMarks.getProgram())){
			sql = sql + " and program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if( !StringUtils.isEmpty(studentMarks.getSem())){
			sql = sql + " and sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( !StringUtils.isEmpty(studentMarks.getSubject())){
			sql = sql + " and subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if( !StringUtils.isEmpty(studentMarks.getAttempt())){
			sql = sql + " and attempt = ? ";
			parameters.add(studentMarks.getAttempt());
		}
		if( !StringUtils.isEmpty(studentMarks.getMarkedForRevaluation())){
			sql = sql + " and MarkedForRevaluation = ? ";
			parameters.add(studentMarks.getMarkedForRevaluation());
		}
		if( !StringUtils.isEmpty(studentMarks.getMarkedForPhotocopy())){
			sql = sql + " and MarkedForPhotocopy = ? ";
			parameters.add(studentMarks.getMarkedForPhotocopy());
		}
		
		Object args[]=parameters.toArray();
		
		List<StudentMarksBean> getAllMarks=jdbcTemplate.query(sql, args,new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
		
		return getAllMarks;
	}
	
	public List<StudentMarksBean>getSapidnCPSIdFromStudent(){
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql="SELECT sapid,consumerProgramStructureId FROM exam.Students";
		List<StudentMarksBean>list=jdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
		return list;
	}
	
	public List<StudentMarksBean>getSapidnCPSIdFromRegistration(){
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql="SELECT sapid,sem,consumerProgramStructureId FROM exam.registration";
		List<StudentMarksBean>list=jdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
		return list;
	}
	
	public List<StudentMarksBean>getCPSidSubjectAndCode(){
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql=" SELECT subject, consumerProgramStructureId, sifySubjectCode, sem FROM exam.program_sem_subject " ;
		List<StudentMarksBean>list=jdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramExamBean> getModProgramList()
	{
		jdbcTemplate=new JdbcTemplate(dataSource);
		List<ProgramExamBean> modProgramList = new ArrayList<>();
		String query = "SELECT code,modeOfLearning,name,specializationType FROM `exam`.`program`";
		modProgramList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ProgramExamBean.class));
		return modProgramList;
	}
}
