package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.nmims.beans.ExamAdhocPaymentBean;
import com.nmims.beans.ExamBookingCancelBean;
import com.netflix.discovery.util.StringUtil;
import com.nmims.beans.AssignmentPaymentBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.OperationsRevenueBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramCompleteReportBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.ReRegistrationReportBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentLearningMetricsBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TCSExamBookingDataBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.UGConsentExcelReportBean;
import com.nmims.helpers.PaginationHelper;
import com.nmims.services.ProjectStudentEligibilityService;
import com.sforce.soap.partner.sobject.SObject;

public class ReportsDAO extends BaseDAO {
	private static final Logger logger = LoggerFactory.getLogger(ReportsDAO.class);//Vilpesh 2022-04-28
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Value("${PROJECT_APPLICABLE_PROGRAM_SEM_LIST}")
	private String PROJECT_APPLICABLE_PROGRAM_SEM_LIST;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	public HashMap<String, Integer> getExamOrderMap() {

		if (hashMap == null || hashMap.size() == 0) {

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderExamBean> rows = jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(ExamOrderExamBean.class));
			hashMap = new HashMap<String, Integer>();
			for (ExamOrderExamBean row : rows) {
				hashMap.put(row.getMonth() + row.getYear(), Integer.valueOf(row.getOrder()));
			}
		}
		return hashMap;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	protected Class<T> genericType;

	@SuppressWarnings("unchecked")
	public ReportsDAO() {
		super();
		// Get the generic type class
		this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), ReportsDAO.class);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<StudentExamBean> getActiveStudentsWithRecentRegistrations(String authrorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		String validityEndMonth = "";
		if (month <= 6) {
			validityEndMonth = "Jun";
		} else {
			validityEndMonth = "Dec";
		}
		String sql = " SELECT es.sapid, es.centerCode, es.emailId, es.mobile, es.centerName, es.program, es.enrollmentMonth, es.enrollmentYear, es.validityEndMonth, "
				+ " es.validityEndYear, MAX(er.sem) as 'mostRecentRegistration', DATEDIFF(CURDATE(),er.createdDate) AS 'gapInMostRecentRegistrationAndCurrentDateInDays' "
				+ " FROM exam.students  es, exam.examorder eo, exam.registration er where 1 = 1 "
				+ " and es.validityEndMonth = eo.month and es.validityEndYear = eo.year "
				+ " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year
				+ "' and month = '" + validityEndMonth + "') "
				+ " and es.sapid = er.sapid and es.programCleared <> 'Y' ";
		if (authrorizedCenterCodes != null && !"".equals(authrorizedCenterCodes.trim())) {
			sql = sql + " and es.centerCode in (" + authrorizedCenterCodes + ") ";
		}
		sql = sql + " group by er.sapid ";
		ArrayList<StudentExamBean> activeStudentWithRegistrationsList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		return activeStudentWithRegistrationsList;
	}

	public String getExamOrder(String month, String year) throws SQLException {
		String examOrder = "0";
		HashMap<String, Integer> hashMap = getExamOrderMap();
		Integer examOrderInteger = hashMap.get(month + year);
		if (examOrderInteger != null) {
			examOrder = examOrderInteger.toString();
		}
		if ("0".equals(examOrder)) {
			throw new SQLException("Exam order not found");
		}
		return examOrder;
	}

	/*
	 * @SuppressWarnings("rawtypes") public List<PassFailBean>
	 * getProgramCompletedReport(StudentMarksBean studentMarks, String programType,
	 * int numberOfSubjectsToPass, String programStructure, boolean isLateral,String
	 * authorizedCenterCodes){
	 * 
	 * ArrayList<Object> parameters = new ArrayList<Object>(); jdbcTemplate = new
	 * JdbcTemplate(dataSource);
	 * 
	 * 
	 * String sql =
	 * "select p.*, s.program, s.gender , round(avg(p.total),2) as passPercentage from exam.passfail p, exam.students s where p.sapid = s.sapid and p.ispass = 'Y' "
	 * ;
	 * 
	 * if(authorizedCenterCodes != null &&
	 * !"".equals(authorizedCenterCodes.trim())){ sql = sql +
	 * " and s.centerCode in (" + authorizedCenterCodes + ") "; }
	 * 
	 * sql = sql + " and p.program like ? group by p.sapid" +
	 * " having (count(p.subject) = ?)" +
	 * " and p.sapid not in (select distinct sapid from exam.passfail where ispass = 'N' and program like ? )"
	 * +
	 * " and p.sapid in (select distinct sapid from exam.students where PrgmStructApplicable = ? and program like ? "
	 * ;
	 * 
	 * 
	 * if(isLateral){ sql = sql + " and isLateral = 'Y'"; } sql = sql +")";
	 * 
	 * 
	 * parameters.add(programType+"%"); parameters.add(numberOfSubjectsToPass);
	 * parameters.add(programType+"%"); parameters.add(programStructure);
	 * parameters.add(programType+"%");
	 * 
	 *  
	 * if( studentMarks.getYear() != null && !("".equals(studentMarks.getYear())) &&
	 * studentMarks.getMonth() != null && !("".equals(studentMarks.getMonth())) ){
	 * sql = sql +
	 * " and p.sapid in (select distinct sapid from exam.marks where month = ? and year = ? "
	 * + " and program like ? and assignmentscore <> 'ANS') ";
	 * 
	 * sql = sql +
	 * " and p.sapid in (select distinct sapid from exam.marks where month = ? and year = ? "
	 * + " and program like ?) ";
	 * 
	 * sql = sql +
	 * " and p.sapid not in (select distinct sapid from exam.marks where " +
	 * " (examorder * 1) > (Select (examorder* 1) from exam.marks where month = ? and year = ? limit 1)) "
	 * ; parameters.add(studentMarks.getMonth());
	 * parameters.add(studentMarks.getYear()); parameters.add(programType+"%");
	 * 
	 * parameters.add(studentMarks.getMonth());
	 * parameters.add(studentMarks.getYear()); }
	 * 
	 * 
	 * 
	 * Object[] args = parameters.toArray(); 
	 * 
	 * List<PassFailBean> studentMarksList = jdbcTemplate.query(sql, args, new
	 * BeanPropertyRowMapper(PassFailBean.class)); for (PassFailBean passFailBean :
	 * studentMarksList) { passFailBean.setCompletionMonth(studentMarks.getMonth());
	 * passFailBean.setCompletionYear(studentMarks.getYear()); } return
	 * studentMarksList; }
	 */

	@SuppressWarnings("rawtypes")
	public List<PassFailExamBean> getProgramCompletedReport(StudentMarksBean studentMarks, String programType,
			int numberOfSubjectsToPass, String programStructure, boolean isLateral, String authorizedCenterCodes) {
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select p.*, s.program, s.gender , round(avg(p.total),2) as passPercentage"
				+ "		 ,max(STR_TO_DATE(concat('01,',p.writtenMonth,',',p.writtenYear), \"%d,%b,%Y\")) as latestTeeClearingDate "
				+ "		 ,max(STR_TO_DATE(concat('01,',p.assignmentMonth,',',p.assignmentYear), \"%d,%b,%Y\")) as latestAssignClearingDate "
				+ " from exam.passfail p, " + " exam.students s where p.sapid = s.sapid and p.ispass = 'Y' ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " and s.program = ? group by p.sapid" + " having (count(p.subject) = ?)"
				+ " and p.sapid not in (select distinct sapid from exam.passfail where ispass = 'N' and program = ? )"
				+ " and p.sapid in (select distinct sapid from exam.students where PrgmStructApplicable = ? and program = ? ";

		if (isLateral) {
			sql = sql + " and isLateral = 'Y'";
		}
		sql = sql + ")";

		parameters.add(programType);
		parameters.add(numberOfSubjectsToPass);
		parameters.add(programType);
		parameters.add(programStructure);
		parameters.add(programType);

		if (studentMarks.getYear() != null && !("".equals(studentMarks.getYear())) && studentMarks.getMonth() != null
				&& !("".equals(studentMarks.getMonth()))) {

			sql = sql + " and p.sapid in (select distinct sapid from exam.marks where month = ? and year = ? "
					+ " and program = ?) ";

			sql = sql + " and p.sapid not in (select distinct sapid from exam.marks where "
					+ " (examorder * 1) > (Select (examorder* 1) from exam.marks where month = ? and year = ? limit 1)) ";
			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
			parameters.add(programType);

			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
		}
	
		Object[] args = parameters.toArray();
		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, args,
				new BeanPropertyRowMapper(PassFailExamBean.class));
		return studentMarksList;
	}

	// Old Code
	/*
	 * @SuppressWarnings("rawtypes") public List<PassFailBean>
	 * getGraceToCompleteProgramReport(StudentMarksBean studentMarks, String
	 * programType, int numberOfSubjectsToPass, String programStructure, boolean
	 * isLateral, String authorizedCenterCodes){
	 * 
	 * ArrayList<Object> parameters = new ArrayList<Object>(); jdbcTemplate = new
	 * JdbcTemplate(dataSource);
	 * parameters.add(programType+"%"); parameters.add(numberOfSubjectsToPass);
	 * parameters.add(programType+"%"); parameters.add(programStructure);
	 * 
	 * 
	 * 
	 * String innerQuery =
	 * "select distinct sapid from exam.students where PrgmStructApplicable = ?  ";
	 * if( studentMarks.getYear()
	 * != null && !("".equals(studentMarks.getYear())) && studentMarks.getMonth() !=
	 * null && !("".equals(studentMarks.getMonth())) ){ innerQuery +=
	 * " and validityendmonth = ? and validityendyear = ? ";
	 * parameters.add(studentMarks.getMonth());
	 * parameters.add(studentMarks.getYear());
	 * 
	 * if(isLateral){ innerQuery = innerQuery + " and isLateral = 'Y' "; } }
	 * 
	 * String sql =
	 * "select distinct s.centerCode, p.sapid, p.name, p.program, cast(p.total as Unsigned)  t, sum(50 - cast(p.total as Unsigned)) as gracemarks "
	 * + " from exam.passfail p, exam.students s " + " where p.ispass = 'N' and" +
	 * " p.sapid = s.sapid and " + " p.program = s.program and  " +
	 * " p.sapid in (select distinct sapid from exam.passfail where program like ? group by sapid having (count(subject) = ?) ) "
	 * + " and p.program like ? " + " and p.sapid in ( " + innerQuery + " ) " +
	 * " group by p.sapid having gracemarks <= 10 ";
	 * 
	 * if( studentMarks.getYear() != null && !("".equals(studentMarks.getYear())) &&
	 * studentMarks.getMonth() != null && !("".equals(studentMarks.getMonth())) ){
	 * sql = sql +
	 * " and p.sapid in (select distinct sapid from exam.marks where month = ? and year = ?) "
	 * ; parameters.add(studentMarks.getMonth());
	 * parameters.add(studentMarks.getYear()); }
	 * 
	 * if(authorizedCenterCodes != null &&
	 * !"".equals(authorizedCenterCodes.trim())){ sql = sql +
	 * " and s.centerCode in (" + authorizedCenterCodes + ") ";
	 * 
	 * }
	 * 
	 * Object[] args = parameters.toArray();
	 * 
	 * List<PassFailBean> studentMarksList = jdbcTemplate.query(sql, args, new
	 * BeanPropertyRowMapper(PassFailBean.class));
	 * return studentMarksList; }
	 */

	// Addded by Stef

	@SuppressWarnings("rawtypes")
	public List<PassFailExamBean> getGraceToCompleteProgramReport(StudentMarksBean studentMarks, String programType,
			int numberOfSubjectsToPass, String programStructure, boolean isLateral, String authorizedCenterCodes,
			String consumerProgramStructureId) {

		ArrayList<Object> parameters = new ArrayList<Object>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		parameters.add(programType);
		parameters.add(consumerProgramStructureId);
		parameters.add(numberOfSubjectsToPass);
		// parameters.add(programType);
		parameters.add(consumerProgramStructureId);
		// parameters.add(studentMarks.getGracemarks());

		String innerQuery = "select distinct sapid from exam.students where consumerProgramStructureId = ?  "
				+ " AND STR_TO_DATE(CONCAT(validityEndYear, '-', validityEndMonth ), '%Y-%b') < STR_TO_DATE(current_date(), '%Y-%m') ";

		if (studentMarks.getYear() != null && !("".equals(studentMarks.getYear())) && studentMarks.getMonth() != null
				&& !("".equals(studentMarks.getMonth()))) {
//			innerQuery += " and validityendmonth = ? and validityendyear = ? ";
			innerQuery += " AND STR_TO_DATE(CONCAT(validityEndYear, '-', validityEndMonth ), '%Y-%b') >= STR_TO_DATE(CONCAT(?, '-', ?), '%Y-%b') ";
			parameters.add(studentMarks.getYear());
			parameters.add(studentMarks.getMonth());
			if (isLateral) {
				innerQuery = innerQuery + " and isLateral = 'Y' ";
			}
		}

		String sql = "select distinct s.centerCode, p.sapid, p.name, p.program, cast(p.total as Unsigned)  t, "
				+ " sum(50 - cast(p.total as Unsigned)) as gracemarks, p.remarks " + " from exam.passfail p, exam.students s "
				+ " where p.ispass = 'N' and p.total < 50  and" + " p.sapid = s.sapid and " + " s.program =? and "
				+ " s.consumerProgramStructureId=? and "
				+ " p.sapid in (select distinct sapid from exam.passfail group by sapid having (count(subject) = ?) ) and"
				+ " p.sapid in ( " + innerQuery + " ) " + " group by p.sapid having gracemarks <= '"
				+ studentMarks.getGracemarks() + "' ";

		if (studentMarks.getYear() != null && !("".equals(studentMarks.getYear())) && studentMarks.getMonth() != null
				&& !("".equals(studentMarks.getMonth()))) {
			sql = sql + " and p.sapid in (select distinct sapid from exam.marks " + " where month = ? and year = ? "
					+ " and ("// added by PS on 5Feb19 to check if student has numeric entry for Tee/assign
					// for year-month which end of validity grace is been applied.
					+ "		  writenscore REGEXP '^[+-]?[0-9]*([0-9]\\\\.|[0-9]|\\\\.[0-9])[0-9]*(e[+-]?[0-9]+)?$'	"
					+ "			or "
					+ "		  assignmentscore REGEXP '^[+-]?[0-9]*([0-9]\\\\.|[0-9]|\\\\.[0-9])[0-9]*(e[+-]?[0-9]+)?$' "
					+ "		) "// added by PS on 5Feb19 end
					+ ") ";
			parameters.add(studentMarks.getMonth());
			parameters.add(studentMarks.getYear());
		}

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		Object[] args = parameters.toArray();

		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, args,
				new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
		return studentMarksList;
	}

	@SuppressWarnings("unchecked")
	public Page<StudentMarksBean> getStudentMarksPage(int pageNo, int pageSize, StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.marks where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.marks where 1 = 1 ";

		if (studentMarks.getYear() != null && !("".equals(studentMarks.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if (studentMarks.getMonth() != null && !("".equals(studentMarks.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if (studentMarks.getGrno() != null && !("".equals(studentMarks.getGrno()))) {
			sql = sql + " and grno = ? ";
			countSql = countSql + " and grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if (studentMarks.getSapid() != null && !("".equals(studentMarks.getSapid()))) {
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if (studentMarks.getStudentname() != null && !("".equals(studentMarks.getStudentname()))) {
			sql = sql + " and studentname like  ? ";
			countSql = countSql + " and studentname like  ? ";
			parameters.add("%" + studentMarks.getStudentname() + "%");
		}
		if (studentMarks.getProgram() != null && !("".equals(studentMarks.getProgram()))) {
			sql = sql + " and program = ? ";
			countSql = countSql + " and program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if (studentMarks.getSem() != null && !("".equals(studentMarks.getSem()))) {
			sql = sql + " and sem = ? ";
			countSql = countSql + " and sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if (studentMarks.getSubject() != null && !("".equals(studentMarks.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if (studentMarks.getAttempt() != null && !("".equals(studentMarks.getAttempt()))) {
			sql = sql + " and attempt = ? ";
			countSql = countSql + " and attempt = ? ";
			parameters.add(studentMarks.getAttempt());
		}

		sql = sql + " order by sem, subject, program, sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(StudentMarksBean.class));

		return page;
	}

	public StudentMarksBean findById(String id) {

		String sql = "SELECT * FROM exam.marks WHERE id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentMarksBean marksBean = (StudentMarksBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
				new BeanPropertyRowMapper(StudentMarksBean.class));

		return marksBean;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteStudentMarks(String id) {
		String sql = "Delete from exam.marks where id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { id });

	}

	public void upsertMarks(StudentMarksBean bean, JdbcTemplate jdbcTemplate, String type) {
		boolean recordExists = checkIfRecordExists(bean, jdbcTemplate);
		if (recordExists) {
			updateStudentMarksForUpsert(bean, jdbcTemplate, type);
		} else {
			insertStudentMarksForUpsert(bean, jdbcTemplate);
		}
	}

	private boolean checkIfRecordExists(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		if ("Not Available".equalsIgnoreCase(bean.getSapid().trim())) {
			return false;
		}

		String sql = "SELECT count(*) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";

		int count = (int) jdbcTemplate.queryForObject(sql,
				new Object[] { bean.getYear(), bean.getMonth(), bean.getSapid(), bean.getSubject() }, Integer.class);

		if (count == 0) {
			return false;
		} else {
			return true;
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdate(final List<StudentMarksBean> marksBeanList, String type) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < marksBeanList.size(); i++) {
			try {
				StudentMarksBean bean = marksBeanList.get(i);
				upsertMarks(bean, jdbcTemplate, type);
			} catch (Exception e) {
				
				errorList.add(i + "");
				// return i;
			}
		}
		return errorList;

	}

	public void setStudentListToAlumni(final List<PassFailExamBean> studentMarksList) {
		if (studentMarksList == null) {
			return;
		}
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE exam.students set programCleared = 'Y' where sapid = ? ";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PassFailExamBean m = studentMarksList.get(i);
					ps.setString(1, m.getSapid());
				}

				public int getBatchSize() {
					return studentMarksList.size();
				}
			});
		} catch (Exception e) {
			
		}

	}

	// start ------Added to send email for Alumni Students.

	public List<String> getEmailofPCStudents(final List<String> studentMarksList) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("studentMarksList", studentMarksList);
		String sql = "Select emailId from exam.students where programCleared = 'Y' and sapid in (:studentMarksList) ";
		List<String> studentEmailList = null;
		try {
			studentEmailList = namedParameterJdbcTemplate.query(sql, paramSource,
					new SingleColumnRowMapper(String.class));

		} catch (Exception e) {
			
		}
		return studentEmailList;

	}

	public void getSentEmailToPCStudents(final List<PassFailExamBean> studentMarksList) {
		if (studentMarksList == null) {
			return;
		}

		String sql = "UPDATE exam.students set emailSentProgramCleared = 'Y' where sapid = ? ";
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PassFailExamBean m = studentMarksList.get(i);
					ps.setString(1, m.getSapid());
				}

				public int getBatchSize() {
					return studentMarksList.size();
				}
			});
		} catch (Exception e) {
			
		}
	}

	// end
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateTimeTable(final List<TimetableBean> timeTableList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < timeTableList.size(); i++) {
			try {
				TimetableBean bean = timeTableList.get(i);
				upsertTimeTable(bean, jdbcTemplate);
			} catch (Exception e) {
				
				errorList.add(i + "");
				// return i;
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
			try {
				AssignmentStatusBean bean = timeTableList.get(i);
				upsertAssignmentStatus(bean, jdbcTemplate);
			} catch (Exception e) {
				
				errorList.add(i + "");
				// return i;
			}
		}
		return errorList;

	}

	private void upsertAssignmentStatus(AssignmentStatusBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.assignmentStatus "
				+ "(examYear, examMonth, sapid, subject, submitted, createdBy, createdDate, lastModifiedBy ,lsatModifiedDate)"
				+ " VALUES " + "(?,?,?,?,?,?,sysdate() , ? , sysdate())" + " on duplicate key update "
				+ "	    examYear = ?," + "	    examMonth = ?," + "	    sapid = ?," + "	    subject = ?,"
				+ "	    submitted = ?," + "	    createdBy = ?, " + "	    createdDate = sysdate() ";

		String examYear = bean.getExamYear();
		String examMonth = bean.getExamMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String submitted = bean.getSubmitted();
		String createdBy = bean.getCreatedBy();

		jdbcTemplate.update(sql, new Object[] { examYear, examMonth, sapid, subject, submitted, createdBy, examYear,
				examMonth, sapid, subject, submitted, createdBy, bean.getLastModifiedBy() });

	}

	private void upsertTimeTable(TimetableBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.timetable (examYear, examMonth, prgmStructApplicable, program,"
				+ "subject, date, startTime, endTime, sem, createdBy, lastModifiedBy,createdDate, lastModifiedDate) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())" + " on duplicate key update " + "	    examYear = ?,"
				+ "	    examMonth = ?," + "	    prgmStructApplicable = ?," + "	    program = ?,"
				+ "	    subject = ?," + "	    date = ?," + "	    startTime = ?," + "	    endTime = ?,"
				+ "	    sem = ?," + "	    createdBy = ?," + "	    lastModifiedBy = ?,"
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

		jdbcTemplate.update(sql,
				new Object[] { examYear, examMonth, prgmStructApplicable, program, subject, date, startTime, endTime,
						sem, createdBy, lastModifiedBy, examYear, examMonth, prgmStructApplicable, program, subject,
						date, startTime, endTime, sem, createdBy, lastModifiedBy });

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateOldData(final List<StudentMarksBean> marksBeanList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < marksBeanList.size(); i++) {
			try {
				StudentMarksBean bean = marksBeanList.get(i);
				insertStudentMarksForUpsert(bean, jdbcTemplate);
			} catch (Exception e) {
				
				errorList.add(i + "");
				// return i;
			}
		}
		return errorList;

	}

	public void isValidEntry(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		String programsql = "Select count(*) from programs where program = ? ";
		String subjectSql = "Select count(*) from subjects where subjectname = ? ";

		int validProgramCount = (int) jdbcTemplate.queryForObject(programsql, new Object[] { bean.getProgram() },
				Integer.class);
		int validSubjectCount = (int) jdbcTemplate.queryForObject(subjectSql, new Object[] { bean.getSubject() },
				Integer.class);

		ArrayList<String> errorMessageList = new ArrayList<>();
		if (validProgramCount == 0) {
			errorMessageList.add("Invalid Program " + bean.getProgram() + " entered for Student " + bean.getSapid()
					+ " under subject " + bean.getSubject());
		}
	}

	public Page<StudentMarksBean> getAllStudentMarksPage(final int pageNo, final int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sqlCountRows = "SELECT count(*) FROM exam.marks";
		String sqlFetchRows = "SELECT * FROM exam.marks";

		PaginationHelper<StudentMarksBean> pagingHelper = new PaginationHelper<StudentMarksBean>();
		Page<StudentMarksBean> page = pagingHelper.fetchPage(jdbcTemplate, sqlCountRows, sqlFetchRows, new Object[] {},
				pageNo, pageSize, new BeanPropertyRowMapper(StudentMarksBean.class));

		return page;
	}

	public ArrayList<String> getAllSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";

		// List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql,
				new SingleColumnRowMapper(String.class));
		return subjectList;

	}

	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct program FROM exam.programs order by program asc";

		// List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql,
				new SingleColumnRowMapper(String.class));
		return programList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate, String type) {
		if ("written".equalsIgnoreCase(type)) {
			updateWrittenMarksForUpsert(m, jdbcTemplate);
		} else {
			updateAsignmentMarksForUpsert(m, jdbcTemplate);
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateWrittenMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate) {
		String sql = "Update exam.marks set " + " writenscore=?, " + " grno=?, " + " processed = 'N', "
				+ " lastModifiedBy=?, " + " lastModifiedDate=sysdate() "
				+ " where year =? and month=? and sapid = ? and subject = ?";

		jdbcTemplate.update(sql, new Object[] { m.getWritenscore(), m.getGrno(), m.getLastModifiedBy(), m.getYear(),
				m.getMonth(), m.getSapid(), m.getSubject() });

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAsignmentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate) {
		String sql = "Update exam.marks set " + "year=?," + "month=?,"
		// + "examorder=?,"
				+ "grno=?," + "sapid=?," + "studentname=?," + "program=?," + "sem=?," + "subject=?,"
				+ "assignmentscore=?," + "gracemarks=?," + "total=?," + "attempt=?," + "source=?," + "location=?,"
				+ "centercode=?," + "remarks=?, " + "syllabusYear=?, " + "processed = 'N', " + "lastModifiedBy=?, "
				+ "lastModifiedDate=sysdate() " + " where year =? and month=? and sapid = ? and subject = ?";

		jdbcTemplate.update(sql, new Object[] { m.getYear(), m.getMonth(),
				// getExamOrder(m.getMonth().trim(),m.getYear().trim()),
				m.getGrno(), m.getSapid(), m.getStudentname(), m.getProgram(), m.getSem(), m.getSubject(),
				m.getAssignmentscore(), m.getGracemarks(), m.getTotal(), m.getAttempt(), m.getSource(), m.getLocation(),
				m.getCentercode(), m.getRemarks(), m.getSyllabusYear(), m.getLastModifiedBy(), m.getYear(),
				m.getMonth(), m.getSapid(), m.getSubject() });

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertStudentMarksForUpsert(StudentMarksBean marksBean, JdbcTemplate jdbcTemplate) {

		final String sql = " INSERT INTO exam.marks(		" + "year," + "month," + "examorder," + "grno," + "sapid,"
				+ "studentname," + "program," + "sem," + "subject," + "writenscore," + "assignmentscore,"
				+ "gracemarks," + "total," + "attempt," + "source," + "location," + "centercode," + "remarks,"
				+ "syllabusYear," + "createdBy," + "createdDate," + "lastModifiedBy," + "lastModifiedDate)"
				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";

		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, m.getYear());
				ps.setString(2, m.getMonth());
				ps.setString(3, getExamOrder(m.getMonth().trim(), m.getYear().trim()));
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

	public List<ExamOrderExamBean> getExamsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examorder order by examorder.order asc";
		List<ExamOrderExamBean> examsList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamOrderExamBean.class));
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
	public void updateExamStats(ExamOrderExamBean exam) {
		String sql = "Update exam.examorder set " + "live=? , " + "declareDate = sysdate()   "
				+ " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { exam.getLive(), exam.getYear(), exam.getMonth() });
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTimetableStats(ExamOrderExamBean exam) {
		String sql = "Update exam.examorder set " + " timeTableLive = ?  " + " where year =? and month = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { exam.getTimeTableLive(), exam.getYear(), exam.getMonth() });
	}

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAStudentsMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and "
				+ " b.live = 'Y' and a.sapid = ? order by a.subject, a.examorder desc";

		/*
		 * if( studentMarks.getSapid() != null &&
		 * !("".equals(studentMarks.getSapid()))){ sql = sql +
		 * " and a.sapid = ? order by a.examorder desc";
		 * parameters.add(studentMarks.getSapid()); } Object[] args =
		 * parameters.toArray();
		 */

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql, new Object[] { studentMarks.getSapid() },
				new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAStudentsMostRecentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where live='Y') order by sem, subject asc";


		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql, new Object[] { studentMarks.getSapid() },
				new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}

	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> searchSingleStudentMarks(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";
		String countSql = "SELECT count(*) FROM exam.marks a, exam.examorder b where a.year = b.year and a.month = b.month and b.live = 'Y' ";

		if (studentMarks.getYear() != null && !("".equals(studentMarks.getYear()))) {
			sql = sql + " and a.year = ? ";
			countSql = countSql + " and a.year = ? ";
			parameters.add(studentMarks.getYear());
		}
		if (studentMarks.getMonth() != null && !("".equals(studentMarks.getMonth()))) {
			sql = sql + " and a.month = ? ";
			countSql = countSql + " and a.month = ? ";
			parameters.add(studentMarks.getMonth());
		}
		if (studentMarks.getSapid() != null && !("".equals(studentMarks.getSapid()))) {
			sql = sql + " and a.sapid = ? ";
			countSql = countSql + " and a.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if (studentMarks.getSem() != null && !("".equals(studentMarks.getSem()))) {
			sql = sql + " and a.sem = ? ";
			countSql = countSql + " and a.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if (studentMarks.getSubject() != null && !("".equals(studentMarks.getSubject()))) {
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and a.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}

		sql = sql + " order by sem, subject asc";

		Object[] args = parameters.toArray();

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql, args,
				new BeanPropertyRowMapper(StudentMarksBean.class));

		return studentsMarksList;
	}

	public String getMostRecentResultPeriod() {

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth() + "-" + row.getYear();
		}
		return recentPeriod;
	}

	public String getMostRecentTimeTablePeriod() {

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth() + "-" + row.getYear();
		}
		return recentPeriod;
	}

	public String getRecentExamDeclarationDate() {

		String declareDate = null, decDate = "";
		Date d = new Date();
		final String sql = "Select declareDate from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql, new Object[] {}, String.class);

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			d = sdfr.parse(decDate);
			declareDate = sdfr.format(d);
		} catch (Exception e) {
			declareDate = "";
		}

		return declareDate;
	}

	public String getStudentCenterDetails(String sapId) {
		String centerCode = null;
		// final String sql = "Select centerCode, centerName from exam.students where
		// sapid = ? and students.sem = (Select max(students.sem) from exam.students
		// where sapid=?)";

		final String tempSql = "Select centerCode from exam.student_center where sapid = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		centerCode = (String) jdbcTemplate.queryForObject(tempSql, new Object[] { sapId }, String.class);

		return centerCode;
	}

	public ArrayList<CenterExamBean> getAllCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers where active = '1' order by centerCode asc";

		// List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterExamBean> centers = (ArrayList<CenterExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(CenterExamBean.class));
		return centers;
	}

	public void updateStudentCenter(String centerCode, String centerName, String sapId) {
		/*
		 * String sql = "Update exam.students set " + "centerCode=? ," + "centerName=? "
		 * + " where sapid = ? ";
		 */

		String tempSql = "Insert into exam.student_center (sapid, centerCode, centerName) values (?, ?, ?)";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(tempSql, new Object[] { sapId, centerCode, centerName

		});

	}

	public ArrayList<String> batchUpsertStudentMaster(List<StudentExamBean> studentList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < studentList.size(); i++) {
			try {
				StudentExamBean bean = studentList.get(i);
				upsertStudenMaster(bean, jdbcTemplate);
			} catch (Exception e) {
				
				errorList.add(i + "");
			}
		}
		return errorList;
	}

	private void upsertStudenMaster(StudentExamBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.students (sapid, sem, lastName, firstName,"
				+ "middleName, fatherName, husbandName, motherName, gender, program, enrollmentMonth, enrollmentYear,"
				+ "emailId, mobile, altPhone, dob, regDate, isLateral, isReReg, address, city, state, "
				+ "country, pin, centerCode, centerName, validityEndMonth, validityEndYear, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate,PrgmStructApplicable) VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(), ?)"
				+ " on duplicate key update " + "	    lastName = ?," + "	    firstName = ?," + "	    middleName = ?,"
				+ "	    fatherName = ?," + "	    husbandName = ?," + "	    motherName = ?," + "	    gender = ?,"
				+ "	    program = ?," + "	    enrollmentMonth = ?," + "	    enrollmentYear = ?,"
				+ "	    emailId = ?," + "	    mobile = ?," + "	    altPhone = ?," + "	    dob = ?,"
				+ "	    regDate = ?," + "	    isLateral = ?," + "	    isReReg = ?," + "	    address = ?,"
				+ "	    city = ?," + "	    state = ?," + "	    country = ?," + "	    pin = ?,"
				+ "	    centerCode = ?," + "	    centerName = ?," + "	    validityEndMonth = ?,"
				+ "	    validityEndYear = ?," + "	    lastModifiedBy = ?," + "	    lastModifiedDate = sysdate(),"
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

		jdbcTemplate.update(sql, new Object[] { sapid, sem, lastName, firstName, middleName, fatherName, husbandName,
				motherName, gender, program, enrollmentMonth, enrollmentYear, emailId, mobile, altPhone, dob, regDate,
				isLateral, isReReg, address, city, state, country, pin, centerCode, centerName, validityEndMonth,
				validityEndYear, createdBy, lastModifiedBy, PrgmStructApplicable, lastName, firstName, middleName,
				fatherName, husbandName, motherName, gender, program, enrollmentMonth, enrollmentYear, emailId, mobile,
				altPhone, dob, regDate, isLateral, isReReg, address, city, state, country, pin, centerCode, centerName,
				validityEndMonth, validityEndYear, lastModifiedBy, PrgmStructApplicable });

	}

	public void execute(String sql) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);

	}

	public HashMap<String, String> getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.program";
		List<ProgramExamBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		HashMap<String, String> programCodeNameMap = new HashMap<String, String>();

		for (int i = 0; i < programList.size(); i++) {
			programCodeNameMap.put(programList.get(i).getCode(), programList.get(i).getName());
		}

		return programCodeNameMap;
	}

	@SuppressWarnings("unchecked")
	public Page<AssignmentStatusBean> getAssignmentStatusPage(int pageNo, int pageSize,
			AssignmentStatusBean assignmentStatus) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.assignmentStatus where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.assignmentStatus where 1 = 1 ";

		if (assignmentStatus.getExamYear() != null && !("".equals(assignmentStatus.getExamYear()))) {
			sql = sql + " and examYear = ? ";
			countSql = countSql + " and examYear = ? ";
			parameters.add(assignmentStatus.getExamYear());
		}

		if (assignmentStatus.getExamMonth() != null && !("".equals(assignmentStatus.getExamMonth()))) {
			sql = sql + " and examMonth = ? ";
			countSql = countSql + " and examMonth = ? ";
			parameters.add(assignmentStatus.getExamMonth());
		}

		if (assignmentStatus.getSapid() != null && !("".equals(assignmentStatus.getSapid()))) {
			sql = sql + " and sapid = ? ";
			countSql = countSql + " and sapid = ? ";
			parameters.add(assignmentStatus.getSapid());
		}

		if (assignmentStatus.getSubject() != null && !("".equals(assignmentStatus.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(assignmentStatus.getSubject());
		}

		sql = sql + " order by sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentStatusBean> pagingHelper = new PaginationHelper<AssignmentStatusBean>();
		Page<AssignmentStatusBean> page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentStatusBean.class));

		return page;
	}

	public ArrayList<ExamBookingTransactionBean> getAllConfirmedBookings(String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b,exam.students s where a.year = b.year "
				+ " and a.month = b.month and a.sapid = s.sapid "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " order by a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] {}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ExamCenterSlotMappingBean getSlotsBooked() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sum(COALESCE(booked, 0)) booked , sum(COALESCE(onHold, 0)) onHold from exam.examcenter_slot_mapping a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";

		ExamCenterSlotMappingBean bean = (ExamCenterSlotMappingBean) jdbcTemplate.queryForObject(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterSlotMappingBean.class));
		return bean;
	}

	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingForGivenYearMonth(StudentMarksBean studentMarks,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> completeBookingList = new ArrayList<ExamBookingTransactionBean>();

		/*
		 * old query String sql =
		 * "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,a.examEndTime,a.booked,"
		 * +
		 * "a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentMode,a.ddno,a.bank,a.ddAmount,a.examMode,a.transactionID,"
		 * +
		 * "a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,a.secureHash,a.respAmount,a.description,a.responseCode,"
		 * +
		 * "a.respPaymentMethod,a.isFlagged,a.responseMessage,a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,"
		 * +
		 * "b.examCenterName,ps.sifySubjectCode,s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral, "
		 * + "s.validityEndYear,s.validityEndMonth  " +
		 * "from exam.students s, exam.examcenter b, exam.exambookings a,exam.program_subject ps "
		 * +
		 * "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
		 * +
		 * "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
		 * + " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid not in " +
		 * "( " +
		 * "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu,exam.examorder ee "
		 * + "		where ecu.year = ee.year and ecu.month = ee.month and ee.order = " +
		 * "		( " +
		 * "			SELECT max(examorder.order) FROM exam.examorder where timeTableLive = 'Y'"
		 * + "		) " + ")";
		 */
		String oldSql = "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,a.examEndTime,a.booked,"
				+ "a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentOption,a.paymentMode,a.ddno,a.bank,a.ddAmount,a.examMode,a.transactionID,"
				+ "a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,a.secureHash,a.respAmount,a.description,a.responseCode,"
				+ "a.respPaymentMethod,a.isFlagged,a.responseMessage,a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,"
				+ "b.examCenterName,ps.sifySubjectCode,s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral, "
				+ "s.validityEndYear,s.validityEndMonth  "
				+ "from exam.students s, exam.examcenter_history b, exam.exambookings_history a,exam.program_subject ps "
				+ "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
				+ "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
				+ " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid not in " + "( "
				+ "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu"
				+ " where ecu.year = ? and ecu.month = ?" + ")";

		String newSql = "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,a.examEndTime,a.booked,"
				+ "a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentOption,a.paymentMode,a.ddno,a.bank,a.ddAmount,a.examMode,a.transactionID,"
				+ "a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,a.secureHash,a.respAmount,a.description,a.responseCode,"
				+ "a.respPaymentMethod,a.isFlagged,a.responseMessage,a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,"
				+ "b.examCenterName,ps.sifySubjectCode,s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral, "
				+ "s.validityEndYear,s.validityEndMonth  "
				+ "from exam.students s, exam.examcenter b, exam.exambookings a,exam.program_subject ps "
				+ "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
				+ "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
				+ " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid not in " + "( "
				+ "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu"
				+ " where ecu.year = ? and ecu.month = ?" + ")";

		/*
		 * old query String corporateSql =
		 * "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,"
		 * +
		 * "a.examEndTime,a.booked,a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentMode,a.ddno,"
		 * +
		 * "a.bank,a.ddAmount,a.examMode,a.transactionID,a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,"
		 * +
		 * "a.secureHash,a.respAmount,a.description,a.responseCode,a.respPaymentMethod,a.isFlagged,a.responseMessage,"
		 * +
		 * "a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,b.examCenterName,ps.sifySubjectCode,"
		 * +
		 * "s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral,"
		 * + "s.validityEndYear,s.validityEndMonth  " +
		 * " FROM exam.students s, exam.corporate_examcenter b, exam.exambookings a,exam.program_subject ps "
		 * +
		 * "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
		 * +
		 * "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
		 * + " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid in " + "( "
		 * +
		 * "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu,exam.examorder ee "
		 * + "		where ecu.year = ee.year and ecu.month = ee.month and ee.order = " +
		 * "		( " +
		 * "			SELECT max(examorder.order) FROM exam.examorder where timeTableLive = 'Y'"
		 * + "		) " + ")";
		 */
		String oldcorporateSql = "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,"
				+ "a.examEndTime,a.booked,a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentOption,a.paymentMode,a.ddno,"
				+ "a.bank,a.ddAmount,a.examMode,a.transactionID,a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,"
				+ "a.secureHash,a.respAmount,a.description,a.responseCode,a.respPaymentMethod,a.isFlagged,a.responseMessage,"
				+ "a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,b.examCenterName,ps.sifySubjectCode,"
				+ "s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral,"
				+ "s.validityEndYear,s.validityEndMonth  "
				+ " FROM exam.students s, exam.corporate_examcenter b, exam.exambookings_history a,exam.program_subject ps "
				+ "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
				+ "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
				+ " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid in " + "( "
				+ "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu"
				+ " where ecu.year = ? and ecu.month = ?" + ")";

		String newcorporateSql = "SELECT a.year,a.month,a.program,a.sem,a.subject,a.amount,a.examDate,a.examTime,"
				+ "a.examEndTime,a.booked,a.tranDateTime,a.bookingCompleteTime,a.trackId,a.paymentOption,a.paymentMode,a.ddno,"
				+ "a.bank,a.ddAmount,a.examMode,a.transactionID,a.tranStatus,a.paymentID,a.requestID,a.merchantRefNo,"
				+ "a.secureHash,a.respAmount,a.description,a.responseCode,a.respPaymentMethod,a.isFlagged,a.responseMessage,"
				+ "a.error,a.respTranDateTime,a.examMode,b.city,b.centerId,b.examCenterName,ps.sifySubjectCode,"
				+ "s.sapid,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral,"
				+ "s.validityEndYear,s.validityEndMonth  "
				+ " FROM exam.students s, exam.corporate_examcenter b, exam.exambookings a,exam.program_subject ps "
				+ "where a.centerId = b.centerId and a.year = ? and a.month = ? and a.booked = 'Y' and "
				+ "a.sapid = s.sapid and a.subject = ps.subject and a.program = ps.program and a.sem = ps.sem and"
				+ " ps.prgmStructApplicable = s.PrgmStructApplicable and s.sapid in " + "( "
				+ "		SELECT ecu.sapid FROM exam.corporate_center_usermapping ecu"
				+ " where ecu.year = ? and ecu.month = ?" + ")";
		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			oldSql = oldSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			newSql = newSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			oldcorporateSql = oldcorporateSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			newcorporateSql = newcorporateSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		oldSql += " group by a.sapid, a.subject order by a.centerId, a.examDate, a.examTime, a.sem, a.program, a.subject asc;";
		newSql += " group by a.sapid, a.subject order by a.centerId, a.examDate, a.examTime, a.sem, a.program, a.subject asc;";
		oldcorporateSql += " group by a.sapid, a.subject order by a.centerId, a.examDate, a.examTime, a.sem, a.program, a.subject asc";
		newcorporateSql += " group by a.sapid, a.subject order by a.centerId, a.examDate, a.examTime, a.sem, a.program, a.subject asc";

		ArrayList<ExamBookingTransactionBean> oldcorporateBookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(oldcorporateSql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(),
						studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		ArrayList<ExamBookingTransactionBean> newcorporateBookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(newcorporateSql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(),
						studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		ArrayList<ExamBookingTransactionBean> oldbookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(oldSql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth() }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		ArrayList<ExamBookingTransactionBean> newbookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(newSql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth() }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		if (oldbookingList != null && oldbookingList.size() > 0) {
			completeBookingList.addAll(oldbookingList);
		}
		if (newbookingList != null && newbookingList.size() > 0) {
			completeBookingList.addAll(newbookingList);
		}
		if (oldcorporateBookingList != null && oldcorporateBookingList.size() > 0) {
			completeBookingList.addAll(oldcorporateBookingList);
		}
		if (newcorporateBookingList != null && newcorporateBookingList.size() > 0) {
			completeBookingList.addAll(newcorporateBookingList);
		}
		return completeBookingList;
	}

	// Added by Vikas : 14/06/2016: START
	public ArrayList<AssignmentPaymentBean> getAssignmentPaymentsForGivenYearMonth(StudentMarksBean studentMarksBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sqlQuery = "SELECT *,es.firstname,es.lastName FROM exam.assignmentpayment ea,exam.students es where es.sapid = ea.sapid and year = ? and month = ? and booked = 'Y'";

		ArrayList<AssignmentPaymentBean> assignmentPaymentList = (ArrayList<AssignmentPaymentBean>) jdbcTemplate.query(
				sqlQuery, new Object[] { studentMarksBean.getYear(), studentMarksBean.getMonth() },
				new BeanPropertyRowMapper(AssignmentPaymentBean.class));
		return assignmentPaymentList;
	}
	// Added by Vikas : 14/06/2016: START

	public ArrayList<ExamBookingTransactionBean> getReleasedButNotBooked(ExamBookingTransactionBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.exambookings where year = ? and month = ? "
				+ " and booked = 'RL' and concat(sapid, subject) not in "
				+ "		(select concat(sapid, subject) from exam.exambookings where year = ? and month = ? and booked = 'Y')";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] { searchBean.getYear(), searchBean.getMonth(), searchBean.getYear(),
						searchBean.getMonth() }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ArrayList<ExamBookingTransactionBean> getConfirmedOnlineExamBookingForGivenYearMonth(
			StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> totalExamBookingList = new ArrayList<ExamBookingTransactionBean>();
		String sql = "SELECT s.*,b.*,a.*,ps.sifySubjectCode "
				+ " FROM   exam.students s, exam.examcenter b, exam.exambookings a,exam.program_subject ps "
				+ " where a.centerId = b.centerId " + " and a.year = ? " + " and a.month = ? " + " and a.booked = 'Y' "
				+ " and examMode = 'Online' " + " and a.subject NOT IN ('Project', 'Module 4 - Project') "
				+ " and a.program not in ('EPBM','MPDV') " + " and a.sapid = s.sapid "
				+ " and s.sapid not in (select distinct c.sapid from exam.corporate_center_usermapping c where year = ? and month = ?)"
				// add to have sifycode for each record by PS
				+ " and ps.sem=a.sem " + "	and ps.prgmStructApplicable = s.PrgmStructApplicable "
				// + " and ps.program = s.program " commented as was giving error for re-reg of
				// new program
				+ " and ps.subject = a.subject " + " and ps.program = a.program " + " and s.sapid = ( select s2.sapid "
				+ "						from exam.students s2 " + "						where s2.sapid=s.sapid "
				+ "						and s2.sem = (  select max(s3.sem) from exam.students s3 where s3.sapid=s.sapid  )  )"
				// add to have sifycode for each record by PS
				+ " group by a.sapid, a.subject "
				+ " order by a.sapid,  a.examDate,  a.examTime, a.sem, a.program, a.subject , a.centerId asc";

		String corporateSql = "SELECT s.*,b.*,a.*,ps.sifySubjectCode "
				+ "	FROM   exam.students s, exam.corporate_examcenter b, exam.exambookings a,exam.program_subject ps  "
				+ "  where a.centerId = b.centerId and a.year = ? " + " and a.month = ? and a.booked = 'Y' "
				+ " and examMode = 'Online' " + " and a.subject NOT IN ('Project', 'Module 4 - Project') "
				+ " and a.program not in ('EPBM','MPDV') " + " and a.sapid = s.sapid "
				+ " and s.sapid in  (select distinct c.sapid from exam.corporate_center_usermapping c where year = ? and month = ?)"
				// add to have sifycode for each record by PS
				+ " and ps.sem=a.sem " + "	and ps.prgmStructApplicable = s.PrgmStructApplicable "
				// + " and ps.program = s.program " commented as was giving error for re-reg of
				// new program
				+ " and ps.subject = a.subject " + " and ps.program = a.program " + " and s.sapid = ( select s2.sapid "
				+ "						from exam.students s2 " + "						where s2.sapid=s.sapid "
				+ "						and s2.sem = (  select max(s3.sem) from exam.students s3 where s3.sapid=s.sapid  )  )"
				// add to have sifycode for each record by PS
				+ " group by a.sapid, a.subject "
				+ " order by a.sapid,  a.examDate,  a.examTime, a.sem, a.program, a.subject , a.centerId asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth() }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		ArrayList<ExamBookingTransactionBean> corporateBookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(corporateSql, new Object[] { studentMarks.getYear(), studentMarks.getMonth(),
						studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		totalExamBookingList.addAll(bookingList);
		totalExamBookingList.addAll(corporateBookingList);
		return totalExamBookingList;
	}

	public HashMap<String, String> getStudentExamPasswordsForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT sapid, password FROM exam.exambookings "
				+ " where booked = 'Y' and year = ? and month = ? and subject NOT IN ('Project', 'Module 4 - Project')"
				+ " group by sapid order by sapid";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		HashMap<String, String> studentPasswordMap = new HashMap<>();
		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			studentPasswordMap.put(bean.getSapid(), bean.getPassword());
		}
		return studentPasswordMap;
	}

	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBookingForGivenYearMonth(
			StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL' or a.booked = 'RF') "
				+ " and a.sapid = s.sapid " + " group by a.sapid, a.subject, a.booked "
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		String sql2 = "SELECT * FROM   exam.students s, exam.examcenter_history b, exam.exambookings_history a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL' or a.booked = 'RF') "
				+ " and a.sapid = s.sapid " + " group by a.sapid, a.subject, a.booked "
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingListHistory = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql2, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		if (!bookingListHistory.isEmpty()) {
			bookingList.addAll(bookingListHistory);
		}

		return bookingList;
	}

	public double getBulkPayments() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * from portal.ad_hoc_payment pa,exam.examorder eo "
				+ " where paymenttype = 'Exam Registration' and tranStatus = 'Payment Successful' "
				+ " and eo.year = pa.year and eo.month = pa.month and "
				+ " eo.order  = (select MAX(examorder.order)  from exam.examorder where timeTableLive = 'Y') ";
		double bulkPayments = 0;
		ArrayList<ExamAdhocPaymentBean> listOfAdHocPayments = (ArrayList<ExamAdhocPaymentBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ExamAdhocPaymentBean.class));
		if (listOfAdHocPayments != null && listOfAdHocPayments.size() > 0) {
			for (ExamAdhocPaymentBean adHocBean : listOfAdHocPayments) {
				bulkPayments = bulkPayments + Double.parseDouble(adHocBean.getAmount());
			}
		}
		return bulkPayments;
	}

	/*
	 * public ArrayList<ExamBookingTransactionBean>
	 * getDatewiseAmount(StudentMarksBean studentMarks) { jdbcTemplate = new
	 * JdbcTemplate(dataSource); String sql =
	 * "select sum(amount) amount, date(respTranDateTime) respTranDateTime from ( "
	 * + "  select distinct trackid, amount, paymentmode , respTranDateTime " +
	 * "		from exam.exambookings " +
	 * " 	where (booked = 'Y' or booked = 'RL' or booked = 'RF') " +
	 * "		and paymentmode = 'Online' " + "		and year = ?  " +
	 * "		and month = ?" + "		and respTranDateTime is not null )" +
	 * "		 abc group by date(respTranDateTime) ";
	 * 
	 * "  select sum(amount) as amount, respTranDateTime  " + " from ( " +
	 * " select sum(amount) as amount, date(respTranDateTime) as respTranDateTime  "
	 * + " from (  " +
	 * " select distinct trackid, amount, paymentmode , respTranDateTime " +
	 * " from exam.exambookings  " + " where " +
	 * " (booked = 'Y' or booked = 'RL' or booked = 'RF')  " +
	 * " and paymentmode = 'Online'  " + " and year = ? " + " and month = ?  " +
	 * " and respTranDateTime is not null  ) t1 " +
	 * " group by date(respTranDateTime)  " + " UNION ALL " +
	 * " select sum(amount) as amount, date(respTranDateTime) as respTranDateTime  "
	 * + " from (  " +
	 * " select distinct trackid, amount, paymentmode , respTranDateTime  " +
	 * " from exam.exambookings_history  " + " where " +
	 * " (booked = 'Y' or booked = 'RL' or booked = 'RF')  " +
	 * " and paymentmode = 'Online'  " + " and year = ? " + " and month = ?" +
	 * " and respTranDateTime is not null  ) t2 " +
	 * " group by date(respTranDateTime) " + " UNION ALL " +
	 * " SELECT sum(amount) as amount,date(respTranDateTime) as respTranDateTime  "
	 * + " from (  " +
	 * " select distinct mwp.trackId, mwp.amount , mwp.paymentOption , mwp.respTranDateTime"
	 * + " FROM exam.mba_wx_bookings mwb " +
	 * " INNER JOIN exam.mba_wx_payment_records mwp on mwb.paymentRecordId = mwp.id and mwb.sapid = mwp.sapid  "
	 * + " WHERE (" +
	 * " mwb.bookingStatus = 'Y' or mwb.bookingStatus = 'RL' or mwb.bookingStatus = 'RF')  "
	 * + " and mwb.year = ? " + " and mwb.month = ?  " +
	 * " and mwp.respTranDateTime is not null  ) t3 " +
	 * " group by date(respTranDateTime) " +
	 * " UNION ALL SELECT  sum(amount) as amount,date(respTranDateTime) as respTranDateTime  "
	 * + " from (  " +
	 * " select distinct mxp.trackId, mxp.amount, mxp.paymentOption , mxp.respTranDateTime "
	 * + " FROM exam.mba_x_bookings mxb " +
	 * " INNER JOIN exam.mba_x_payment_records mxp on mxb.paymentRecordId = mxp.id "
	 * + " and mxb.sapid = mxp.sapid  " + " WHERE (" +
	 * " mxb.bookingStatus = 'Y' or mxb.bookingStatus = 'RL' or  mxb.bookingStatus = 'RF')   "
	 * + " and mxb.year = ?" + " and mxb.month = ? " +
	 * " and mxp.respTranDateTime is not null  ) t4 " +
	 * " group by date(respTranDateTime) " + " )" +
	 * " t5 group by date(respTranDateTime)" + "";
	 * 
	 * 
	 * ArrayList<ExamBookingTransactionBean> bookingList =
	 * (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query( sql, new Object[]
	 * { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
	 * studentMarks.getMonth(), studentMarks.getYear(), studentMarks.getMonth(),
	 * studentMarks.getYear(), studentMarks.getMonth() }, new
	 * BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	 * 
	 * 
	 * try { String sql2 =
	 * "select sum(amount) amount, date(respTranDateTime) respTranDateTime from ( "
	 * + "  select distinct trackid, amount, paymentmode , respTranDateTime " +
	 * "		from exam.exambookings_history " +
	 * " 	where (booked = 'Y' or booked = 'RL' or booked = 'RF') " +
	 * "		and paymentmode = 'Online' " + "		and year = ?  " +
	 * "		and month = ?" + "		and respTranDateTime is not null )" +
	 * "		 abc group by date(respTranDateTime) ";
	 * 
	 * 
	 * ArrayList<ExamBookingTransactionBean> bookingListHistory =
	 * (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql2, new
	 * Object[]{studentMarks.getYear(), studentMarks.getMonth()}, new
	 * BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	 * if(!bookingListHistory.isEmpty()) { bookingList.addAll(bookingListHistory); }
	 * }catch(Exception e) {
	 * 
	 * }
	 * 
	 * 
	 * return bookingList; }
	 */
	public ArrayList<ExamBookingTransactionBean> getDatewiseAmountPG(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> param = new ArrayList<Object>();
		String sql =
					" SELECT " + 
					"    sum(amount) as amount,coalesce(respTranDateTime,'-')  as respTranDateTime" + 
					" FROM" + 
					"    (SELECT " + 
					"        trackid, amount , respTranDateTime " + 
					"    FROM" + 
					"        exam.exambookings eb" + 
					"    INNER JOIN exam.students s ON s.sapid = eb.sapid" + 
					"    INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + 
					"    WHERE" + 
					"        (eb.booked = 'Y' or eb.booked = 'RL' or eb.booked = 'RF') "; 
					if (!operationsRevenue.getYear().isEmpty() && !StringUtils.isBlank(operationsRevenue.getYear())) {
						sql = sql + " and eb.year =  ?";
						param.add(operationsRevenue.getYear());
					}
					if (!operationsRevenue.getMonth().isEmpty() && !StringUtils.isBlank(operationsRevenue.getMonth())) {
						sql = sql + " and eb.month =  ?";
						param.add(operationsRevenue.getMonth());
					}
					if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
						sql = sql + " and date(eb.tranDateTime) >=  ?";
						param.add(operationsRevenue.getStartDate());
					}
					if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
						sql = sql + " and date(eb.tranDateTime) <=  ?";
						param.add(operationsRevenue.getEndDate());
					}
					if (operationsRevenue.getLc_list() != null && operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
							&& operationsRevenue.getLc_list() != null) {
						operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
						sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
					}
					if (operationsRevenue.getLc_list() != null && operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
							&& operationsRevenue.getIc_list() != null) {
						operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
						sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
					}
					if (!operationsRevenue.getPaymentOption().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
						sql = sql + " and eb.paymentOption = ? ";
						param.add(operationsRevenue.getPaymentOption());
					}
					if (!operationsRevenue.getProgramType().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
						if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
							sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
						}
					}
					sql = sql + "    GROUP BY trackId" + 
					"    UNION ALL" + 
					"    SELECT " + 
					"        trackid, amount , respTranDateTime " + 
					"    FROM" + 
					"        exam.exambookings_history ebh" + 
					"    INNER JOIN exam.students s ON s.sapid = ebh.sapid" + 
					"    INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + 
					"    WHERE" + 
					"        (ebh.booked = 'Y' OR ebh.booked = 'RL' OR ebh.booked = 'RF')";
					if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
						sql = sql + " and date(ebh.tranDateTime) >=  ?";
						param.add(operationsRevenue.getStartDate());
					}
					if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
						sql = sql + " and date(ebh.tranDateTime) <=  ?";
						param.add(operationsRevenue.getEndDate());
					}
					if (operationsRevenue.getIc_list() != null && operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
							&& operationsRevenue.getLc_list() != null) {
						operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
						sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
					}
					if (operationsRevenue.getIc_list() != null && operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
							&& operationsRevenue.getIc_list() != null) {
						operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
						sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
					}
					if (!operationsRevenue.getPaymentOption().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
						sql = sql + " and ebh.paymentOption = ? ";
						param.add(operationsRevenue.getPaymentOption());
					}
					if (!operationsRevenue.getProgramType().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
						if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
							sql = sql + " AND s.program not in ('MBA - X','MBA - WX')";
						}
					}
					sql = sql + "    GROUP BY trackId  ) t1" + 
					"	GROUP BY date(respTranDateTime)";
				
		Object[] args = param.toArray();
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql,args,
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ArrayList<ExamBookingTransactionBean> getDatewiseAmountMBAx(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> param = new ArrayList<Object>();
		String sql =
					" SELECT " + 
					"    sum(amount) as amount,coalesce(respTranDateTime,'-')  as respTranDateTime" + 
					" FROM" + 
					"    (SELECT " + 
					"        trackid, amount , respTranDateTime " + 
					"    FROM" + 
					"        exam.mba_x_bookings eb" + 
					"    INNER JOIN exam.students s ON s.sapid = eb.sapid" + 
					"    INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + 
					"    INNER JOIN exam.mba_x_payment_records mwp on eb.paymentRecordId = mwp.id and eb.sapid = mwp.sapid "+ 
					"    WHERE" + 
					"        (eb.bookingStatus = 'Y' or eb.bookingStatus = 'RL' or eb.bookingStatus = 'RF') "; 
					if (!operationsRevenue.getYear().isEmpty() && !StringUtils.isBlank(operationsRevenue.getYear())) {
						sql = sql + " and eb.year =  ?";
						param.add(operationsRevenue.getYear());
					}
					if (!operationsRevenue.getMonth().isEmpty() && !StringUtils.isBlank(operationsRevenue.getMonth())) {
						sql = sql + " and eb.month =  ?";
						param.add(operationsRevenue.getMonth());
					}
					if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
						sql = sql + " and date(mwp.tranDateTime) >=  ?";
						param.add(operationsRevenue.getStartDate());
					}
					if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
						sql = sql + " and date(mwp.tranDateTime) <=  ?";
						param.add(operationsRevenue.getEndDate());
					}
					if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
							&& operationsRevenue.getLc_list() != null) {
						operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
						sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
					}
					if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
							&& operationsRevenue.getIc_list() != null) {
						operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
						sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
					}
					if (!operationsRevenue.getPaymentOption().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
						sql = sql + " and mwp.paymentOption = ? ";
						param.add(operationsRevenue.getPaymentOption());
					}
					if (!operationsRevenue.getProgramType().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
						if ("MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
							sql = sql + " and s.program = ? ";
						}
							param.add(operationsRevenue.getProgramType());
					}
					
					sql = sql + "    GROUP BY trackId  ) t1" + 
					"	GROUP BY date(respTranDateTime)";
				
		Object[] args = param.toArray();
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql,args,
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	public ArrayList<ExamBookingTransactionBean> getDatewiseAmountMBAwx(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> param = new ArrayList<Object>();
		String sql =
					" SELECT " + 
					"    sum(amount) as amount,coalesce(respTranDateTime,'-')  as respTranDateTime" + 
					" FROM" + 
					"    (SELECT " + 
					"        trackid, amount , respTranDateTime " + 
					"    FROM" + 
					"        exam.mba_wx_bookings eb" + 
					"    INNER JOIN exam.students s ON s.sapid = eb.sapid" + 
					"    INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + 
					"    INNER JOIN exam.mba_wx_payment_records mwp on eb.paymentRecordId = mwp.id and eb.sapid = mwp.sapid "+ 
					"    WHERE" + 
					"        (eb.bookingStatus = 'Y' or eb.bookingStatus = 'RL' or eb.bookingStatus = 'RF') ";
					if (!operationsRevenue.getYear().isEmpty() && !StringUtils.isBlank(operationsRevenue.getYear())) {
						sql = sql + " and eb.year =  ?";
						param.add(operationsRevenue.getYear());
					}
					if (!operationsRevenue.getMonth().isEmpty() && !StringUtils.isBlank(operationsRevenue.getMonth())) {
						sql = sql + " and eb.month =  ?";
						param.add(operationsRevenue.getMonth());
					}
					if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
						sql = sql + " and date(mwp.tranDateTime) >=  ?";
						param.add(operationsRevenue.getStartDate());
					}
					if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
						sql = sql + " and date(mwp.tranDateTime) <=  ?";
						param.add(operationsRevenue.getEndDate());
					}
					if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
							&& operationsRevenue.getLc_list() != null) {
						operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
						sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
					}
					if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
							&& operationsRevenue.getIc_list() != null) {
						operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
						sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
					}
					if (!operationsRevenue.getPaymentOption().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
						sql = sql + " and mwp.paymentOption = ? ";
						param.add(operationsRevenue.getPaymentOption());
					}
					if (!operationsRevenue.getProgramType().isEmpty()
							&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
						if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())) {
							sql = sql + " and s.program = ? ";
						}
							param.add(operationsRevenue.getProgramType());
					}
					
					sql = sql + "    GROUP BY trackId  ) t1" + 
					"	GROUP BY date(respTranDateTime)";
				
		Object[] args = param.toArray();
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql,args,
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBookingForGivenYearMonth(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL')  " + " and a.sapid = s.sapid"
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { fileBean.getYear(), fileBean.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ArrayList<String> getConfirmedOrRelesedBookingTrackIdsForGivenYearMonth(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT trackId FROM    exam.exambookings a where  a.year = ? "
				+ " and a.month = ? and (a.booked = 'Y' or a.booked = 'RL')  ";

		ArrayList<String> trackIdList = (ArrayList<String>) jdbcTemplate.query(sql,
				new Object[] { fileBean.getYear(), fileBean.getMonth() }, new SingleColumnRowMapper(String.class));
		return trackIdList;
	}

	public ArrayList<String> getAllSRTrackId() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT trackId FROM    portal.service_request  where  tranStatus = 'Payment Successful'";

		ArrayList<String> trackIdList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {},
				new SingleColumnRowMapper(String.class));
		return trackIdList;
	}

	public ArrayList<String> getAllAdhocPaymentTrackId() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT trackId FROM   portal.ad_hoc_payment  where  tranStatus = 'Payment Successful'";

		ArrayList<String> trackIdList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {},
				new SingleColumnRowMapper(String.class));
		return trackIdList;
	}

	public ArrayList<String> getAllAssignmentPaymentTrackId(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT trackId FROM    exam.assignmentpayment where  year = ?  and month = ?   ";

		ArrayList<String> trackIdList = (ArrayList<String>) jdbcTemplate.query(sql,
				new Object[] { fileBean.getYear(), fileBean.getMonth() }, new SingleColumnRowMapper(String.class));
		return trackIdList;
	}

	public ArrayList<String> getAllPCPTrackId() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT trackId FROM   acads.pcpbookings  where  booked = 'Y' ";

		ArrayList<String> trackIdList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {},
				new SingleColumnRowMapper(String.class));
		return trackIdList;
	}

	public ArrayList<ExamBookingTransactionBean> getExpiredBookingsForGivenYearMonth(FileBean fileBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and a.booked <> 'Y' and a.booked <> 'RL' and a.booked <> 'RF' and paymentMode = 'Online'  "
				+ " and a.sapid = s.sapid"
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		// sql = "select * from exam.exambookings where booked <> 'Y' and a.booked <>
		// 'RL' and paymentMode = 'Online' and a.year = ? and a.month = ? ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { fileBean.getYear(), fileBean.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		// ArrayList<ExamBookingTransactionBean> bookingList =
		// (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new
		// Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ArrayList<ExamBookingTransactionBean> getDDBookings(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a  where a.year = ? and a.month =? " + " and paymentMode = 'DD' ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		String sql2 = "SELECT * FROM exam.exambookings_history a  where a.year = ? and a.month =? "
				+ " and paymentMode = 'DD' ";

		ArrayList<ExamBookingTransactionBean> bookingListHistory = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql2, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		if (!bookingListHistory.isEmpty()) {
			bookingList.addAll(bookingListHistory);
		}

		return bookingList;
	}

	public ArrayList<ExamCenterSlotMappingBean> getCenterBookings(StudentMarksBean studentMarks, boolean isCorporate) {
		long start = System.currentTimeMillis();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String examcenter_slot_mapping = "examcenter_slot_mapping_view";
		String examcenter = "examcenter_view";
//		int year_tmp = Integer.parseInt(studentMarks.getYear());
//		if(year_tmp == 2019 && "Dec".equalsIgnoreCase(studentMarks.getMonth())) {
//			//do nothing
//		}
//		else if(year_tmp <= 2019) {
//			examcenter_slot_mapping = "examcenter_slot_mapping_history";
//			examcenter = "examcenter_history";
//		}
		String sql = "";
		if (isCorporate) {
			sql = "SELECT a.examcenterId, a.date, a.starttime, a.capacity, a.year, a.month, COALESCE(a.onhold, 0) onHold, "
					+ " COALESCE(a.booked, 0) booked, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available,"
					+ " b.examCenterName, b.city, b.address  "
					+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b where b.centerId = a.examcenterId "
					+ "and a.year = ? and a.month = ? and b.mode <> 'Offline' order by  a.examcenterId, a.date asc";
		} else {
			sql = ""
				+ " SELECT `esm`.`examcenterId`, `esm`.`date`, `esm`.`starttime`, `esm`.`capacity`, `esm`.`year`, `esm`.`month`, "
				+ " (COALESCE(`esm`.`onHold`, 0)) AS `onHold`, "
				+ " COALESCE(`bookedCount`, 0) AS `booked`, "
				+ " (`esm`.`capacity` - COALESCE(`bookedCount`, 0) - (COALESCE(`esm`.`onHold`, 0))) AS `available`, `ev`.`examCenterName`, `ev`.`city`, `ev`.`address` "
				+ " FROM `exam`.`examcenter_slot_mapping` `esm` "
				
				// Sub-query to fetch number of booked records by centerId-examDate-examTime
				+ " LEFT join ( "
					+ " SELECT `centerId`, `examDate`, `examTime`, `year`, `month`, count(*) AS `bookedCount` "
					+ " FROM `exam`.`exambookings` "
					+ " WHERE `booked` = 'Y' "
					+ " GROUP BY `centerId`, `examDate`, `examTime` "
				+ " ) `e` "
					+ " ON `esm`.`examcenterId` = `e`.`centerId` "
					+ " AND `esm`.`date` = `e`.`examDate` "
					+ " AND `esm`.`starttime` = `e`.`examTime` "
					+ " AND `esm`.`month` = `e`.`month` AND `esm`.`year` = `e`.`year` "
				+ " INNER JOIN `exam`.`examcenter_view` `ev` "
					+ " ON `ev`.`centerId` = `esm`.`examCenterId` "
					+ " AND `ev`.`year` = `esm`.`year` "
					+ " AND `ev`.`month` = `esm`.`month` "
					+ " AND `ev`.`mode` <> 'Offline' "
				+ " WHERE `esm`.`year` = ? "
				+ " AND `esm`.`month` = ? "
				+ " GROUP BY `esm`.`examCenterId`, `esm`.`date`, `esm`.`starttime` "
				+ " ORDER BY `esm`.`examCenterId`, `esm`.`date` ASC";
		}

		ArrayList<ExamCenterSlotMappingBean> centerBookingsList = (ArrayList<ExamCenterSlotMappingBean>) jdbcTemplate
				.query(sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
						new BeanPropertyRowMapper(ExamCenterSlotMappingBean.class));
		
		long elapsedTime = System.currentTimeMillis() -  start;
		 System.out.println("ServicesProfiler.profile(): Method execution time: " + elapsedTime + " milliseconds.");
		 System.out.println("in dao getCenterBookings " );
		return centerBookingsList;
	}

	public HashMap<String, StudentExamBean> getAllStudents() {
		String sql = "Select * from exam.students ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}

	public HashMap<String, StudentExamBean> getAllStudentsWithLC() {
		String sql = "Select s.*, c.lc from exam.students s, exam.centers c where s.centerCode = c.centercode and s.centerCode is not null and s.centerCode <> '' ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}

	public HashMap<String, StudentExamBean> getAllStudentsWithCenter(String authorizedCenterCodes) {
		String sql = "Select * from exam.students where 1=1 ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and centerCode in (" + authorizedCenterCodes + ") ";
		}

		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AssignmentStatusBean> getStudentsSubjectsForExamBooking(StudentMarksBean studentMarks,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select r.sapid , ps.subject from exam.students s, exam.registration r, exam.program_subject ps where "
				+ " s.sapid = r.sapid and s.program = ps.program " + " and r.sem = ps.sem " + " and r.year = ? "
				+ " and r.month = ? " + " and s.PrgmStructApplicable = ps.prgmStructApplicable ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		ArrayList<AssignmentStatusBean> studentsList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] { getLiveAcadConentYear(), getLiveAcadConentMonth() },
				new BeanPropertyRowMapper(AssignmentStatusBean.class));

		return studentsList;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AssignmentStatusBean> getValidStudentSubjectList(String year, String month,
			String authorizedCenterCodes) {
//		jdbcTemplate = new JdbcTemplate(dataSource);
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		authorizedCenterCodes = authorizedCenterCodes.replaceAll("\'","");
		List<String> authorizedCenterCodesList = Stream.of(authorizedCenterCodes.split(",", -1))
				  .collect(Collectors.toList());
		
		// Object[] args = new Object[]{};
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		 queryParams.addValue("year", year);
		 queryParams.addValue("month", month);
		String sql = "select s.*,r.sem, ps.subject from exam.students s , exam.registration r, "
				+ " exam.program_subject ps, exam.examorder eo " + " where   "
				+ " STR_TO_DATE(concat('30-',s.validityEndMonth,'-',s.validityEndYear), '%d-%b-%Y') >= curdate()"
				+ " and STR_TO_DATE(concat(r.year,'-',r.month,'-01'), '%Y-%b-%d') <= DATE_ADD(STR_TO_DATE(concat(:year,'-',:month,'-01'), '%Y-%b-%d'), INTERVAL -5 MONTH)"
				+ " and s.sapid = r.sapid and r.sem = ps.sem and r.program = ps.program and r.sem = ps.sem "
				+ " and s.PrgmStructApplicable = ps.prgmStructApplicable " + " and s.validityEndMonth = eo.month "
				+ " and s.validityEndYear = eo.year "
				+ " and eo.order >= (Select max(examorder.order) from exam.examorder where acadSessionLive='Y')  "
				+ " AND ps.subject NOT IN ( " + 
				"		'Soft Skills for Managers' , 'Design Thinking', " + 
				"        'Start your Start up', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally') ";
		if (authorizedCenterCodesList != null && authorizedCenterCodesList.size() > 0 ) {
			sql = sql + " and s.centerCode in ( :authorizedCenterCodes ) ";
			queryParams.addValue("authorizedCenterCodes", authorizedCenterCodesList);
		}

		ArrayList<AssignmentStatusBean> studentsList = (ArrayList<AssignmentStatusBean>) namedJdbcTemplate.query(sql, queryParams,
				new BeanPropertyRowMapper(AssignmentStatusBean.class));

		return studentsList;
	}

	public ArrayList<ExamBookingTransactionBean> getQuestionPaperCountForGivenYearMonth(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(subject) subjectCount, subject,  eb.centerid, examdate, examtime , examcentername, "
				+ " city, address, program , eb.year , eb.month "
				+ " from exam.exambookings eb, exam.examcenter ec where booked = 'Y' and exammode = 'Offline' "
				+ " and eb.centerId = ec.centerid and eb.year = ? and eb.month = ? "
				+ " group by subject, centerId, examdate, examtime order by ec.city, eb.centerId";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public ArrayList<StudentExamBean> getStudentClearingCertainNoOfSubjects(StudentMarksBean studentMarks,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select *, count(p.subject) as subjectsCleared from exam.students s, exam.passfail p where s.sapid = p.sapid "
				+ " and p.writtenYear = ? and p.writtenMonth = ? and p.isPass = 'Y'  and p.sem = ? ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " group by p.sapid having count(p.subject) >= ? ";

		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getSem(),
						studentMarks.getSubjetsCleared() },
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<ProgramCompleteReportBean> getProgramCompleteStudentsDetails(
			List<PassFailExamBean> programCompleteList, String year, String month) {
		ArrayList<ProgramCompleteReportBean> programCompleteStudentsList = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select *, p.programname from exam.students s, exam.programs p , exam.examorder eo "
				+ " where s.sapid = ? and p.program = s.program and p.consumerProgramStructureId = s.consumerProgramStructureId and s.sem = (select max(sem) from exam.students st where st.sapid = ?) "
				+ " and eo.month = ? and eo.year = ? ";

		for (PassFailExamBean passFailBean : programCompleteList) {
			ProgramCompleteReportBean student = (ProgramCompleteReportBean) jdbcTemplate.queryForObject(sql,
					new Object[] { passFailBean.getSapid(), passFailBean.getSapid(), month, year },
					new BeanPropertyRowMapper(ProgramCompleteReportBean.class));
			programCompleteStudentsList.add(student);
		}

		return programCompleteStudentsList;

	}

	public ArrayList<StudentExamBean> getStudentsNeverRegisteredForExam(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// Change on 27/05/2017 for including certificate students/
		/*
		 * String sql =
		 * "select ps.subject, s.sapid, c.city from exam.program_subject ps, exam.students s, exam.registration r, exam.centers c "
		 * +
		 * " where ps.PrgmStructApplicable = 'Jul2014' and s.PrgmStructApplicable = 'Jul2014' and "
		 * + " s.PrgmStructApplicable = ps.prgmStructApplicable and s.sapid = r.sapid "
		 * + " and ps.program = r.program and ps.sem = r.sem " +
		 * " and s.centerCode = c.centerCode " +
		 * " and concat(s.sapid, ps.subject) not in " +
		 * "(select concat(s.sapid, eb.subject) from exam.exambookings eb, exam.students s where booked = 'Y' and s.sapid = eb.sapid "
		 * + "and s.PrgmStructApplicable = 'Jul2014' ) ";
		 */

		String sql = "select ps.subject, s.sapid, c.city, s.program, s.PrgmStructApplicable from exam.program_subject ps, exam.students s, exam.registration r, exam.centers c "
				+ " where " + " s.PrgmStructApplicable = ps.prgmStructApplicable and s.sapid = r.sapid "
				+ " and ps.program = r.program and ps.sem = r.sem " + " and s.centerCode = c.centerCode "
				+ " and concat(s.sapid, ps.subject) not in "
				+ "(select concat(s.sapid, eb.subject) from exam.exambookings eb, exam.students s where booked = 'Y' and s.sapid = eb.sapid )";

		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<StudentExamBean> getStudentsRegisteredButFailed(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// Change on 27/05/2017 for including certificate students/
		/*
		 * String sql =
		 * "select subject,s.sapid, c.city  from exam.passfail pf, exam.students s, exam.centers c where "
		 * +
		 * " pf.sapid = s.sapid and s.prgmStructApplicable = 'Jul2014' and s.centerCode = c.centerCode and pf.ispass = 'N' "
		 * ;
		 */

		String sql = "select subject,s.sapid, c.city, s.prgmStructApplicable, s.program  from exam.passfail pf, exam.students s, exam.centers c where "
				+ " pf.sapid = s.sapid  and s.centerCode = c.centerCode and pf.ispass = 'N' ";

		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<StudentExamBean> getStudentsAssignmentSubmitted(StudentMarksBean studentMarks) {

		String examMonth = "";
		if ("Jan".equalsIgnoreCase(studentMarks.getMonth())) {
			examMonth = "Jun";
		} else if ("Jul".equalsIgnoreCase(studentMarks.getMonth())) {
			examMonth = "Dec";
		}
		// Change on 27/05/2017 for including certificate students/
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*
		 * String sql =
		 * "select subject,s.sapid, c.city  from exam.assignmentsubmission a, exam.students s, exam.centers c where "
		 * +
		 * " a.sapid = s.sapid and s.prgmStructApplicable = 'Jul2014' and s.centerCode = c.centerCode and a.month = ? and a.year = ?  "
		 * ;
		 */

		String sql = "select subject,s.sapid, c.city, s.prgmStructApplicable, s.program  from exam.assignmentsubmission a, exam.students s, exam.centers c where "
				+ " a.sapid = s.sapid and s.centerCode = c.centerCode and a.month = ? and a.year = ?  ";


		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new Object[] { examMonth, studentMarks.getYear() }, new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<StudentExamBean> getStudentsNewlyRegistered(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// Change on 27/05/2017 for including certificate students/
		/*
		 * String sql =
		 * "select subject, s.sapid, c.city from exam.program_subject ps, exam.students s, exam.registration r, exam.centers c "
		 * +
		 * " where ps.PrgmStructApplicable = 'Jul2014' and s.PrgmStructApplicable = 'Jul2014' and "
		 * +
		 * " r.month = ? and r.year = ? and s.PrgmStructApplicable = ps.prgmStructApplicable and s.sapid = r.sapid "
		 * +
		 * " and ps.program = r.program and ps.sem = r.sem and s.centerCode = c.centerCode "
		 * ;
		 */

		String sql = "select subject, s.sapid, c.city, s.PrgmStructApplicable, s.program from exam.program_subject ps, exam.students s, exam.registration r, exam.centers c "
				+ " where "
				+ " r.month = ? and r.year = ? and s.PrgmStructApplicable = ps.prgmStructApplicable and s.sapid = r.sapid "
				+ " and ps.program = r.program and ps.sem = r.sem and s.centerCode = c.centerCode ";


		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new Object[] { studentMarks.getMonth(), studentMarks.getYear() },
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<String> getStudentsWithValidity(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct sapid from exam.examorder eo, exam.students s "
				+ " where s.validityEndMonth = eo.month " + " and s.validityEndYear = eo.year "
				+ " and eo.order >= (Select max(examorder.order) from exam.examorder where acadSessionLive='Y') ";


		ArrayList<String> studentList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {},
				new SingleColumnRowMapper(String.class));

		return studentList;
	}

	public ArrayList<StudentExamBean> getSemWiseSubjectsCleared(StudentMarksBean studentMarks,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select s.*, count(p.sapid) as subjectsCleared, p.sapid, p.sem , p.program "
				+ " from exam.passfail p, exam.students s where s.sapid = p.sapid "
				+ " and s.sem = (select max(sem) from exam.students where sapid = s.sapid) "
				+ " and isPass='Y' and  p.sapid in (select sapid from exam.exambookings where year = ? and month = ? "
				+ " and booked = 'Y' ) ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql += " group by p.sapid, p.sem order by p.sapid ";

		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;
	}

	public ArrayList<StudentExamBean> getToppers(StudentMarksBean studentMarks, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select s.*, sum(total) totalMarks, count(p.sapid) as subjectsCleared, "
				+ " p.sapid, p.sem, p.program" + " from exam.passfail p, exam.students s where p.sapid = s.sapid and "
				+ " s.sem = (select max(sem) from exam.students where sapid = s.sapid)"
				+ " and ispass = 'Y' and p.sapid <> 'Not Available'" + " and writtenyear  = ? and writtenmonth =?"
				+ " and p.sapid not in (select sapid from exam.passfail where ispass = 'N' and sapid <> 'Not Available'"
				+ " and writtenyear  = ? and writtenmonth =?)";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " group by p.sapid,p.sem , p.program having subjectsCleared >= 5"
				+ " order by  p.program, p.sem ,subjectsCleared, totalMarks desc";


		ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {
				studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(StudentExamBean.class));

		return studentList;

	}

	public ArrayList<AssignmentStatusBean> getFailRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail pf, exam.students s where pf.sapid = s.sapid and pf.isPass = 'N' order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] {}, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	public ArrayList<AssignmentStatusBean> getValidStudentsFailRecords(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail pf, exam.students s where pf.sapid = s.sapid and pf.isPass = 'N' "
				+ " and s.sapid in (SELECT sapid FROM exam.students s, exam.examorder eo "
				+ "	where s.validityEndMonth = eo.month and s.validityendyear = eo.year "
				+ " and eo.order >= (select examorder.order from exam.examorder where month = ? and year = ?)) "
				+ " order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] { month, year }, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	public ArrayList<AssignmentStatusBean> getValidStudentsFailRecordsWithoutANS(String year, String month,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.students s, exam.passfail pf where pf.sapid = s.sapid and pf.isPass = 'N' "
				+ " and s.sapid in (SELECT sapid FROM exam.students s, exam.examorder eo "
				+ "	where s.validityEndMonth = eo.month and s.validityendyear = eo.year "
				+ " and pf.assignmentscore <> 'ANS' "
				+ " and eo.order >= (select examorder.order from exam.examorder where month = ? and year = ?)) ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql += " order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] { month, year }, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	public ArrayList<AssignmentStatusBean> getPassRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail pf, exam.students s where pf.sapid = s.sapid and pf.isPass = 'Y' order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] {}, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	public ArrayList<AssignmentStatusBean> getPassRecordsExecutive() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail pf, exam.students s where pf.sapid = s.sapid and pf.isPass = 'Y' and pf.program in ('EPBM','MPDV') order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] {}, new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	// DML operations required for re-reg report//
	public ArrayList<String> listOfSapIdOfActiveStudents(String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		String sql = "";
		String validityEndMonth = "";
		if (month <= 6) {
			validityEndMonth = "Jun";
		} else {
			validityEndMonth = "Dec";
		}
		sql = "SELECT sapid FROM exam.students s, exam.examorder  eo  where 1 = 1 and "
				+ " programCleared = 'N'  and s.validityEndMonth = eo.month and s.validityEndYear = eo.year "
				+ " and NOT(s.programStatus <=> 'Program Terminated')  and eo.order >= (Select examorder.order from exam.examorder where year = '"
				+ year + "' and month = '" + validityEndMonth + "') " + " and s.program not like 'C%' ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql = sql + " order by sem, program, sapid asc ";
		ArrayList<String> listOfSapId = (ArrayList<String>) jdbcTemplate.query(sql,
				new SingleColumnRowMapper(String.class));
		return listOfSapId;

	}
	/*
	 * public ArrayList<CenterBean> getCentersOnAuthorizedCenterCodes(String
	 * authorizedCenterCodes,String centerType) { jdbcTemplate = new
	 * JdbcTemplate(dataSource); String sql = "";
	 * 
	 * 
	 * sql =
	 * " SELECT centerName, lc FROM exam.centers where centerName <> '' and active = 1 "
	 * ;
	 * 
	 * if(authorizedCenterCodes != null &&
	 * !"".equals(authorizedCenterCodes.trim())){ sql = sql + " and centerCode in ("
	 * + authorizedCenterCodes + ") "; }
	 * 
	 * ArrayList<CenterBean> centerList=
	 * (ArrayList<CenterBean>)jdbcTemplate.query(sql, new
	 * BeanPropertyRowMapper(CenterBean.class)); return centerList; }
	 */

	public ArrayList<String> listOfSapIdOfActiveStudents(ReRegistrationReportBean reRegBean,
			String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> listOfActiveStudentSapIdList = new ArrayList<String>();
		String sql = "SELECT sapid FROM exam.students s, exam.examorder  eo  where 1 = 1 and programCleared = 'N' ";
		if (reRegBean.getSapid() != null && !("".equals(reRegBean.getSapid()))) {
			sql = sql + " and sapid = ? ";
		}
		if (reRegBean.getProgram() != null && !("".equals(reRegBean.getProgram()))) {
			sql = sql + " and program = ? ";
		}
		if (reRegBean.getSem() != null && !("".equals(reRegBean.getSem()))) {
			sql = sql + " and sem = ? ";
		}
		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and centerCode in (" + authorizedCenterCodes + ") ";
		}
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!

		String validityEndMonth = "";
		if (month <= 6) {
			validityEndMonth = "Jun";
		} else {
			validityEndMonth = "Dec";
		}

		sql = sql
				+ " and s.validityEndMonth = eo.month and s.validityEndYear = eo.year and NOT(s.programStatus <=> 'Program Terminated') ";
		sql = sql + " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year
				+ "' and month = '" + validityEndMonth + "') ";
		sql = sql + " order by sem, program, sapid asc";

		listOfActiveStudentSapIdList = (ArrayList<String>) jdbcTemplate.query(sql,
				new SingleColumnRowMapper(String.class));

		return listOfActiveStudentSapIdList;
	}

	public HashMap<String, String> getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(String commaSeperatedSapid,
			String category) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberSemAndCountOfFailedSubjets = new HashMap<String, String>();
		String sql = "";
		if ("FAIL".equals(category)) {
			sql = "select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester' , count(subject) as 'count' from exam.passfail "
					+ " where isPass = 'N' " + " and sapid in (  " + commaSeperatedSapid + " )  ";
		} else if ("PASS".equals(category)) {
			sql = "select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester' , count(subject) as 'count' from exam.passfail "
					+ " where isPass = 'Y' " + " and sapid in (  " + commaSeperatedSapid + " ) ";
		}

		// For IC and LC, check if pass fail records year, month is less than equal to
		// result declared exam month and year
		// This is done to enure that they do not see results when exam team is
		// uploading marks
		// They will be able to see results only after it is made live
		sql += " and STR_TO_DATE(concat('01-',assignmentmonth,'-',assignmentyear), '%d-%b-%Y') <=   STR_TO_DATE( '01-"
				+ getLiveOnlineExamResultMonth() + "-" + getLiveOnlineExamResultYear() + "', '%d-%b-%Y') "
				+ " and STR_TO_DATE(concat('01-',writtenmonth,'-',writtenyear), '%d-%b-%Y') <=   STR_TO_DATE( '01-"
				+ getLiveOnlineExamResultMonth() + "-" + getLiveOnlineExamResultYear() + "', '%d-%b-%Y')";

		sql += " group by sapid,sem ";

		ArrayList<StudentExamBean> getListOfFailedRecords = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : getListOfFailedRecords) {

			mapOfStudentNumberSemAndCountOfFailedSubjets.put(report.getKeyOfSapidAndSemester(), report.getCount());
		}
		return mapOfStudentNumberSemAndCountOfFailedSubjets;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndCountOfANSSubjects(String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfANSSubjects = new HashMap<String, String>();

		String sql = "SELECT CONCAT(s.sapid,'-',ps.sem) as 'keyOfSapidAndSemester' , count(ps.subject) as 'count' "
				+ " FROM exam.students s, exam.program_subject ps, exam.registration r "
				+ "  where r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ "  and s.sapid = r.sapid and r.sem = ps.sem and s.sapid in (" + commaSeperatedSapid + " ) "
				+ "  and concat(s.sapid, ps.subject) "
				+ "  not in (select concat(sapid, subject) from exam.assignmentsubmission where sapid in ("
				+ commaSeperatedSapid + " ))" + "  group by s.sapid,ps.sem " + " order by s.sapid asc";

		ArrayList<StudentExamBean> getListOfANSSubjects = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : getListOfANSSubjects) {
			mapOfStudentNumberAndSemAndCountOfANSSubjects.put(report.getKeyOfSapidAndSemester(), report.getCount());
		}
		return mapOfStudentNumberAndSemAndCountOfANSSubjects;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndCountOfAssignmentSubmitted(
			String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted = new HashMap<String, String>();

		String sql = "SELECT CONCAT(s.sapid,'-',ps.sem) as 'keyOfSapidAndSemester' , count(ps.subject) as 'count' "
				+ " FROM exam.students s, exam.program_subject ps, exam.registration r "
				+ "  where r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ "  and s.sapid = r.sapid and r.sem = ps.sem and s.sapid in (" + commaSeperatedSapid + " ) "
				+ "  and concat(s.sapid, ps.subject) "
				+ "  in (select concat(sapid, subject) from exam.assignmentsubmission where sapid in ("
				+ commaSeperatedSapid + " ))" + "  group by s.sapid,ps.sem " + " order by s.sapid asc";

		ArrayList<StudentExamBean> getListOfAssignmentSubmitted = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : getListOfAssignmentSubmitted) {
			mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted.put(report.getKeyOfSapidAndSemester(),
					report.getCount());
		}
		return mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(
			String commaSeperatedSapid, String category) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfCountOfTEEMissingOrAB = new HashMap<String, String>();
		String sql = "";
		if ("TEE".equals(category)) {
			sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester',count(subject) as 'count' from exam.marks where (writenscore = null or writenscore = '') "
					+ " and sapid in (  " + commaSeperatedSapid + " ) group by sapid,sem ";
		} else if ("AB".equals(category)) {
			sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester',count(subject) as 'count' from exam.marks where (writenscore = 'AB') "
					+ " and sapid in (  " + commaSeperatedSapid + " ) group by sapid,sem ";
		}

		ArrayList<StudentExamBean> listOfTEEMissingOrABSubjects = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : listOfTEEMissingOrABSubjects) {
			mapOfStudentNumberAndSemAndCountOfCountOfTEEMissingOrAB.put(report.getKeyOfSapidAndSemester(),
					report.getCount());
		}
		return mapOfStudentNumberAndSemAndCountOfCountOfTEEMissingOrAB;
	}

	protected BeanPropertySqlParameterSource getParameterSource(StudentLearningMetricsBean bean) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(bean);
		return parameterSource;
	}

	public int[] executeSQLBatch(final List<StudentLearningMetricsBean> beans, final String sql) {

		int UPDATE_BATCH_SIZE = 100;
		int[] recordsUpdated = null;
		for (int i = 0; i < beans.size(); i = i + UPDATE_BATCH_SIZE) {
			int lastIndex = (i + UPDATE_BATCH_SIZE) < beans.size() ? (i + UPDATE_BATCH_SIZE) : beans.size();

			ArrayList<StudentLearningMetricsBean> recorsToUpdateSubList = new ArrayList<StudentLearningMetricsBean>(
					beans.subList(i, lastIndex));

			List<SqlParameterSource> parameters = new ArrayList<SqlParameterSource>(recorsToUpdateSubList.size());
			for (StudentLearningMetricsBean bean : recorsToUpdateSubList) {
				parameters.add(getParameterSource(bean));
			}

			recordsUpdated = namedParameterJdbcTemplate.batchUpdate(sql,
					parameters.toArray(new SqlParameterSource[parameters.size()]));
		}
		return recordsUpdated;
	}

	public void batchUpsertStudentMetrics(final List<StudentLearningMetricsBean> metricsList) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		final String sql = " INSERT INTO portal.student_learning_metrics ( sapid , sem1NoOfANS , sem1NoOfAssignSubmitted , sem1ANSSubjects , sem1SubmittedSubjects ,"
				+ " sem2NoOfANS , sem2NoOfAssignSubmitted , sem2ANSSubjects , sem2SubmittedSubjects , sem3NoOfANS , sem3NoOfAssignSubmitted , sem3ANSSubjects ,sem3SubmittedSubjects , "
				+ " sem4NoOfANS , sem4NoOfAssignSubmitted , sem4ANSSubjects , sem4SubmittedSubjects ,totalNoOfANS ,  totalNoOfAssignSubmitted , allANSSubjects , allSubmittedSubjects , "
				+ " sem1NoOfPassedSubjects , sem1NoOfFailedSubjects , sem1PassedSubjects , sem1FailedSubjects , sem2NoOfPassedSubjects , sem2NoOfFailedSubjects , sem2PassedSubjects , sem2FailedSubjects , "
				+ " sem3NoOfPassedSubjects , sem3NoOfFailedSubjects , sem3PassedSubjects , sem3FailedSubjects , sem4NoOfPassedSubjects , sem4NoOfFailedSubjects , sem4PassedSubjects , sem4FailedSubjects , "
				+ " totalNoOfPassedSubjects , totalNoOfFailedSubjects , allPassedSubjects , allFailedSubjects , "
				+ " sem1NoOfBookedSubjects , sem1NoOfBookingPendingSubjects , sem1BookedSubjects , sem1BookingPendingSubjects  , sem2NoOfBookedSubjects , sem2NoOfBookingPendingSubjects , sem2BookedSubjects , sem2BookingPendingSubjects ,"
				+ " sem3NoOfBookedSubjects , sem3NoOfBookingPendingSubjects , sem3BookedSubjects , sem3BookingPendingSubjects  , sem4NoOfBookedSubjects , sem4NoOfBookingPendingSubjects , sem4BookedSubjects , sem4BookingPendingSubjects , "
				+ " totalNoOfBookedSubjects , totalNoOfBookingPendingSubjects , allBookedSubjects , allBookingPendingSubjects , sem1NoOfLecturesAttended , sem2NoOfLecturesAttended , sem3NoOfLecturesAttended , sem4NoOfLecturesAttended , "
				+ " reRegProbability , numberOfSubjectsApplicable,numberOfSubjectsExceptCurrentCycle, totalNoOfLecturedAttended ) "
				+ " VALUES ( :sapid , :sem1NoOfANS , :sem1NoOfAssignSubmitted , :sem1ANSSubjects , :sem1SubmittedSubjects , :sem2NoOfANS , :sem2NoOfAssignSubmitted , :sem2ANSSubjects , :sem2SubmittedSubjects , :sem3NoOfANS , :sem3NoOfAssignSubmitted , :sem3ANSSubjects , :sem3SubmittedSubjects , "
				+ " :sem4NoOfANS , :sem4NoOfAssignSubmitted , :sem4ANSSubjects , :sem4SubmittedSubjects , :totalNoOfANS , :totalNoOfAssignSubmitted , :allANSSubjects , :allSubmittedSubjects , "
				+ " :sem1NoOfPassedSubjects , :sem1NoOfFailedSubjects , :sem1PassedSubjects , :sem1FailedSubjects , :sem2NoOfPassedSubjects , :sem2NoOfFailedSubjects , :sem2PassedSubjects , :sem2FailedSubjects , "
				+ " :sem3NoOfPassedSubjects , :sem3NoOfFailedSubjects , :sem3PassedSubjects , :sem3FailedSubjects , :sem4NoOfPassedSubjects , :sem4NoOfFailedSubjects , :sem4PassedSubjects , :sem4FailedSubjects , "
				+ " :totalNoOfPassedSubjects , :totalNoOfFailedSubjects , :allPassedSubjects , :allFailedSubjects , "
				+ " :sem1NoOfBookedSubjects , :sem1NoOfBookingPendingSubjects , :sem1BookedSubjects , :sem1BookingPendingSubjects , :sem2NoOfBookedSubjects , :sem2NoOfBookingPendingSubjects , :sem2BookedSubjects , :sem2BookingPendingSubjects ,"
				+ " :sem3NoOfBookedSubjects , :sem3NoOfBookingPendingSubjects , :sem3BookedSubjects , :sem3BookingPendingSubjects ,  :sem4NoOfBookedSubjects , :sem4NoOfBookingPendingSubjects , :sem4BookedSubjects , :sem4BookingPendingSubjects , "
				+ " :totalNoOfBookedSubjects , :totalNoOfBookingPendingSubjects , :allBookedSubjects , :allBookingPendingSubjects , :sem1NoOfLecturesAttended , :sem2NoOfLecturesAttended , :sem3NoOfLecturesAttended , :sem4NoOfLecturesAttended , "
				+ " :reRegProbability , :numberOfSubjectsApplicable, :numberOfSubjectsExceptCurrentCycle, :totalNoOfLecturedAttended ) "
				+ " on duplicate key update  "
				+ " sem1NoOfANS = :sem1NoOfANS , sem1NoOfAssignSubmitted = :sem1NoOfAssignSubmitted , sem1ANSSubjects = :sem1ANSSubjects , sem1SubmittedSubjects = :sem1SubmittedSubjects , "
				+ " sem2NoOfANS = :sem2NoOfANS , sem2NoOfAssignSubmitted = :sem2NoOfAssignSubmitted , sem2ANSSubjects = :sem2ANSSubjects , sem2SubmittedSubjects = :sem2SubmittedSubjects , "
				+ " sem3NoOfANS = :sem3NoOfANS , sem3NoOfAssignSubmitted = :sem3NoOfAssignSubmitted , sem3ANSSubjects = :sem3ANSSubjects , sem3SubmittedSubjects = :sem3SubmittedSubjects , "
				+ " sem4NoOfANS = :sem4NoOfANS , sem4NoOfAssignSubmitted = :sem4NoOfAssignSubmitted , sem4ANSSubjects = :sem4ANSSubjects , sem4SubmittedSubjects = :sem4SubmittedSubjects , "
				+ " totalNoOfANS = :totalNoOfANS , totalNoOfAssignSubmitted = :totalNoOfAssignSubmitted , allANSSubjects = :allANSSubjects , allSubmittedSubjects = :allSubmittedSubjects , "
				+ " sem1NoOfPassedSubjects = :sem1NoOfPassedSubjects , sem1NoOfFailedSubjects = :sem1NoOfFailedSubjects , sem1PassedSubjects = :sem1PassedSubjects , sem1FailedSubjects = :sem1FailedSubjects , sem2NoOfPassedSubjects = :sem2NoOfPassedSubjects , sem2NoOfFailedSubjects = :sem2NoOfFailedSubjects , sem2PassedSubjects = :sem2PassedSubjects , sem2FailedSubjects = :sem2FailedSubjects , "
				+ " sem3NoOfPassedSubjects = :sem3NoOfPassedSubjects , sem3NoOfFailedSubjects = :sem3NoOfFailedSubjects , sem3PassedSubjects = :sem3PassedSubjects , sem3FailedSubjects = :sem3FailedSubjects , sem4NoOfPassedSubjects = :sem4NoOfPassedSubjects , sem4NoOfFailedSubjects = :sem4NoOfFailedSubjects , sem4PassedSubjects = :sem4PassedSubjects , sem4FailedSubjects = :sem4FailedSubjects , "
				+ " totalNoOfPassedSubjects = :totalNoOfPassedSubjects , totalNoOfFailedSubjects = :totalNoOfFailedSubjects , allPassedSubjects = :allPassedSubjects , allFailedSubjects = :allFailedSubjects , "
				+ " sem1NoOfBookedSubjects = :sem1NoOfBookedSubjects , sem1NoOfBookingPendingSubjects =:sem1NoOfBookingPendingSubjects ,sem1BookedSubjects = :sem1BookedSubjects , sem1BookingPendingSubjects = :sem1BookingPendingSubjects ,  sem2NoOfBookedSubjects = :sem2NoOfBookedSubjects , sem2NoOfBookingPendingSubjects =:sem2NoOfBookingPendingSubjects, sem2BookedSubjects = :sem2BookedSubjects , sem2BookingPendingSubjects = :sem2BookingPendingSubjects , "
				+ " sem3NoOfBookedSubjects = :sem3NoOfBookedSubjects , sem3NoOfBookingPendingSubjects =:sem3NoOfBookingPendingSubjects ,sem3BookedSubjects = :sem3BookedSubjects , sem3BookingPendingSubjects = :sem3BookingPendingSubjects ,  sem4NoOfBookedSubjects = :sem4NoOfBookedSubjects , sem4NoOfBookingPendingSubjects =:sem4NoOfBookingPendingSubjects , sem4BookedSubjects = :sem4BookedSubjects , sem4BookingPendingSubjects = :sem4BookingPendingSubjects , "
				+ " totalNoOfBookedSubjects = :totalNoOfBookedSubjects , totalNoOfBookingPendingSubjects = :totalNoOfBookingPendingSubjects , allBookedSubjects = :allBookedSubjects , allBookingPendingSubjects = :allBookingPendingSubjects , sem1NoOfLecturesAttended = :sem1NoOfLecturesAttended , sem2NoOfLecturesAttended = :sem2NoOfLecturesAttended , sem3NoOfLecturesAttended = :sem3NoOfLecturesAttended , sem4NoOfLecturesAttended = :sem4NoOfLecturesAttended , "
				+ " reRegProbability = :reRegProbability , numberOfSubjectsApplicable = :numberOfSubjectsApplicable, numberOfSubjectsExceptCurrentCycle = :numberOfSubjectsExceptCurrentCycle, totalNoOfLecturedAttended = :totalNoOfLecturedAttended ";

		executeSQLBatch(metricsList, sql);
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndGAPInReReg(String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndGAPInReReg = new HashMap<String, String>();
		String sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester' , DATEDIFF(CURDATE(),createdDate) AS 'gap' from exam.registration where sapid in (  "
				+ commaSeperatedSapid + " ) group by sapid ";

		ArrayList<StudentExamBean> listOfSemAndGAPInReReg = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : listOfSemAndGAPInReReg) {
			mapOfStudentNumberAndSemAndGAPInReReg.put(report.getKeyOfSapidAndSemester(), report.getGap());
		}
		return mapOfStudentNumberAndSemAndGAPInReReg;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndNumberOfExamsBookings(String commaSeperatedSapid,
			String booked) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndNumberOfExamsBookings = new HashMap<String, String>();
		String sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester' , count(distinct subject) AS 'count' from exam.exambookings where sapid in (  "
				+ commaSeperatedSapid + " ) and booked = ? group by sapid,sem ";

		ArrayList<StudentExamBean> listOfExamBookings = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new Object[] { booked }, new BeanPropertyRowMapper(StudentExamBean.class));
		for (StudentExamBean report : listOfExamBookings) {
			mapOfStudentNumberAndNumberOfExamsBookings.put(report.getKeyOfSapidAndSemester(), report.getCount());
		}
		return mapOfStudentNumberAndNumberOfExamsBookings;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndPendingNumberOfExamBookings(
			String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndPendingNumberOfExamsBookings = new HashMap<String, String>();

		String sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester', er.sapid as s, er.sem as semester, "
				+ " ((select count(ps.subject) from exam.students es,exam.program_subject ps,exam.registration ereg "
				+ " where es.program = ps.program and es.PrgmStructApplicable = ps.prgmStructApplicable and ereg.sem = ps.sem "
				+ " and ereg.program = ps.program and es.sapid = ereg.sapid and ereg.sem = semester and  es.sapid = s) "
				+ " - (select count(distinct eb.subject) from exam.exambookings eb where eb.booked = 'Y' and eb.sapid = s and eb.sem = semester)) as 'count' "
				+ " from exam.registration er where er.sapid in (  " + commaSeperatedSapid + " ) group by er.sapid ";

		ArrayList<StudentExamBean> listOfPendingExamBookings = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		for (StudentExamBean report : listOfPendingExamBookings) {
			mapOfStudentNumberAndPendingNumberOfExamsBookings.put(report.getKeyOfSapidAndSemester(), report.getCount());
		}

		return mapOfStudentNumberAndPendingNumberOfExamsBookings;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndCountOfSessionsAttended(String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = new HashMap<String, String>();

		String sql = " select CONCAT(saf.sapid,'-',ep.sem) as 'keyOfSapidAndSemester' , count(saf.sapid) as 'count' "
				+ " from acads.session_attendance_feedback saf,exam.students es,acads.sessions asi,exam.program_subject ep where saf.sapid = es.sapid and saf.sessionId = asi.id and "
				+ " asi.subject = ep.subject and es.program = ep.program and es.PrgmStructApplicable = ep.prgmStructApplicable and saf.sapid in (  "
				+ commaSeperatedSapid + " ) " + " group by saf.sapid,ep.sem ";

		ArrayList<StudentExamBean> listOfSessionsAttended = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		for (StudentExamBean report : listOfSessionsAttended) {
			mapOfStudentNumberAndSemAndCountOfSessionsAttended.put(report.getKeyOfSapidAndSemester(),
					report.getCount());
		}

		return mapOfStudentNumberAndSemAndCountOfSessionsAttended;
	}

	public HashMap<String, String> getMapOfStudentNumberAndSemAndDriveMonthYear(String commaSeperatedSapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> mapOfStudentNumberAndSemAndDriveMonthYear = new HashMap<String, String>();
		String sql = " select CONCAT(sapid,'-',sem) as 'keyOfSapidAndSemester', CONCAT(month,year) as 'driveMonthYear' "
				+ " from exam.registration where sapid in (  " + commaSeperatedSapid + " ) group by sapid,sem ";
		ArrayList<StudentExamBean> listOfRegistrationsMade = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentExamBean.class));

		for (StudentExamBean report : listOfRegistrationsMade) {
			mapOfStudentNumberAndSemAndDriveMonthYear.put(report.getKeyOfSapidAndSemester(),
					report.getDriveMonthYear());
		}
		return mapOfStudentNumberAndSemAndDriveMonthYear;

	}

	public String commaSeperatedSubjectListBasedOnType(String type, String sapid, String semester) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "", commaSeperatedSubjectList = "";
		if ("FS".equals(type)) {
			sql = "select GROUP_CONCAT(subject) as 'commaSeperatedSubjects' from exam.passfail "
					+ " where isPass = 'N' " + " and sapid = ? and sem = ? group by sapid,sem ";
			commaSeperatedSubjectList = (String) jdbcTemplate.queryForObject(sql, new Object[] { sapid, semester },
					new SingleColumnRowMapper(String.class));
		}
		if ("PS".equals(type)) {
			sql = "select GROUP_CONCAT(subject) as 'commaSeperatedSubjects' from exam.passfail "
					+ " where isPass = 'Y' " + " and sapid = ? and sem = ? group by sapid,sem ";
			commaSeperatedSubjectList = (String) jdbcTemplate.queryForObject(sql, new Object[] { sapid, semester },
					new SingleColumnRowMapper(String.class));
		}
		if ("ANS".equals(type)) {
			sql = " SELECT GROUP_CONCAT(ps.subject) as 'commaSeperatedSubjects' FROM exam.students s, exam.program_subject ps, exam.registration r where r.program = ps.program "
					+ " and s.PrgmStructApplicable = ps.prgmStructApplicable and s.sapid = r.sapid and r.sem = ps.sem and r.year = '"
					+ CURRENT_ACAD_YEAR + "' and r.month = '" + CURRENT_ACAD_MONTH + "' "
					+ " and concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where year = '"
					+ CURRENT_ACAD_YEAR + "' and month = '" + CURRENT_ACAD_MONTH + "' ) "
					+ " and s.sapid = ? and  r.sem = ? group by s.sapid,r.sem ";
			commaSeperatedSubjectList = (String) jdbcTemplate.queryForObject(sql, new Object[] { sapid, semester },
					new SingleColumnRowMapper(String.class));
		}
		if ("AB".equals(type)) {
			sql = " select GROUP_CONCAT(subject) as 'commaSeperatedSubjects' from exam.marks where (writenscore = 'AB') "
					+ " and sapid = ? and sem = ? group by sapid,sem ";
			commaSeperatedSubjectList = (String) jdbcTemplate.queryForObject(sql, new Object[] { sapid, semester },
					new SingleColumnRowMapper(String.class));
		}
		if ("PB".equals(type)) {
			sql = " select ps.subject "
					+ " ((select count(ps.subject) from exam.students es,exam.program_subject ps,exam.registration ereg "
					+ " where es.program = ps.program and es.PrgmStructApplicable = ps.prgmStructApplicable and ereg.sem = ps.sem "
					+ " and ereg.program = ps.program and es.sapid = ereg.sapid and ereg.sem = semester and  es.sapid = ?) "
					+ " - (select count(distinct eb.subject) from exam.exambookings eb where eb.booked = 'Y' and eb.sapid = ? and eb.sem = ?)) as 'count' "
					+ " from exam.registration er where er.sapid = ? group by er.sapid ";
			commaSeperatedSubjectList = (String) jdbcTemplate.queryForObject(sql,
					new Object[] { sapid, sapid, sapid, semester, sapid }, new SingleColumnRowMapper(String.class));
		}
		return commaSeperatedSubjectList;
	}
	// added by stef on 27-Sep-2017
	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getAdhocRefundPaymentRevenueList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource); ArrayList<OperationsRevenueBean>
	 * srRefundRevenueList = new ArrayList<OperationsRevenueBean>();
	 * 
	 * try {
	 * 
	 * String sql =
	 * "select ad.*,st.firstName,st.lastName from exam.ad_hoc_refund ad ,exam.students st where st.sapid = ad.sapid "
	 * + " and date(ad.createdDate) >= ? and date(ad.createdDate) <= ? ";
	 * 
	 * srRefundRevenueList = (ArrayList<OperationsRevenueBean>)
	 * jdbcTemplate.query(sql, new Object[] { operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate() }, new
	 * BeanPropertyRowMapper(OperationsRevenueBean.class));
	 * 
	 * if (!srRefundRevenueList.isEmpty()) { for (OperationsRevenueBean revenue :
	 * srRefundRevenueList) { revenue.setRevenueSource("Adhoc Refund Payment");
	 * revenue.setTranDateTime(revenue.getCreatedDate());// Added as tranDate not
	 * available in // ad_hoc_refund } } } catch (Exception e) {
	 *  }
	 * 
	 * return srRefundRevenueList; }
	 */

	//

	// ------------------ queries for revenue list download start
	// ----------------------------
	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getSRRevenueList(OperationsRevenueBean operationsRevenue) {
	 * getStartDate()); jdbcTemplate = new JdbcTemplate(dataSource);
	 * ArrayList<OperationsRevenueBean> srRevenueList = new
	 * ArrayList<OperationsRevenueBean>(); try{
	 * 
	 * old query String sql =
	 * "select sr.*,st.firstName,st.lastName from portal.service_request sr, exam.students st where sr.transtatus = 'Payment Successful' and sr.sapid = st.sapid "
	 * +
	 * " and date(sr.createdDate) >= ? and date(sr.createdDate) <= ?  group by trackId "
	 * ; String sql =
	 * "select sr.*,st.firstName,st.lastName,c.lc from portal.service_request sr, exam.students st,exam.centers c"
	 * + "  where " + " sr.transtatus = 'Payment Successful' and" +
	 * " sr.sapid = st.sapid and " + " date(sr.createdDate) >= ? and " +
	 * " date(sr.createdDate) <= ?  and" + " c.centerCode = st.centerCode " +
	 * " group by trackId "; srRevenueList = (ArrayList<OperationsRevenueBean>)
	 * jdbcTemplate.query(sql, new Object[] { operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate() },new BeanPropertyRowMapper(
	 * OperationsRevenueBean.class));
	 * 
	 * if(!srRevenueList.isEmpty()) { for(OperationsRevenueBean revenue :
	 * srRevenueList){ revenue.setRevenueSource("Service Request"); } }
	 * }catch(Exception e){ //
	 * }
	 * 
	 * return srRevenueList; }
	 */
	public ArrayList<OperationsRevenueBean> getSRRevenueDownloadList(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> srRevenueList = new ArrayList<OperationsRevenueBean>();
		try {
			String sql = "";
			ArrayList<Object> param = new ArrayList<Object>();
			sql = " SELECT " + " sr.tranDateTime,sr.amount,s.firstName,s.lastName,c.lc,COALESCE(s.sapid,0) as sapid    "
					+ " from " + " portal.service_request sr" + " inner join exam.students s on s.sapid = sr.sapid "
					+ " inner join exam.centers c on c.centerCode = s.centerCode " + " where"
					+ " transtatus = 'Payment Successful'";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(sr.createdDate) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(sr.createdDate) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and sr.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
				if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())
						|| "MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId";
			Object[] args = param.toArray();

			srRevenueList = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));
			if (!srRevenueList.isEmpty()) {
				for (OperationsRevenueBean revenue : srRevenueList) {
					revenue.setRevenueSource("Service Request");
				}
			}

		} catch (Exception e) {
			
		}

		return srRevenueList;
	}

	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getAssignmentPaymentRevenueList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource); ArrayList<OperationsRevenueBean>
	 * srRevenue = new ArrayList<OperationsRevenueBean>(); try {
	 * 
	 * 
	 * String sql =
	 * "select asg.*,st.lastName,st.firstName  from exam.assignmentpayment asg ,exam.students st  where (asg.booked = 'Y')  "
	 * +
	 * " and date(asg.tranDateTime) >= ? and date(asg.tranDateTime) <= ? and st.sapid =asg.sapid group by trackId"
	 * ;
	 * 
	 * String sql = "select asg.*,st.lastName,st.firstName,c.lc  " +
	 * " from exam.assignmentpayment asg ,exam.students st, exam.centers c  " +
	 * " where " + " (asg.booked = 'Y')  and " + " date(asg.tranDateTime) >= ? and "
	 * + " date(asg.tranDateTime) <= ? and " + " st.sapid = asg.sapid and" +
	 * " c.centerCode = st.centerCode" + " group by trackId"; srRevenue =
	 * (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, new Object[] {
	 * operationsRevenue.getStartDate(), operationsRevenue.getEndDate() }, new
	 * BeanPropertyRowMapper(OperationsRevenueBean.class));
	 * 
	 * if (!srRevenue.isEmpty()) { for (OperationsRevenueBean revenue : srRevenue) {
	 * revenue.setRevenueSource("Assignment"); } } } catch (Exception e) {
	 *  }
	 * 
	 * return srRevenue; }
	 */
	public ArrayList<OperationsRevenueBean> getAssignmentPaymentRevenueDownloadList(
			OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> revenue = new ArrayList<OperationsRevenueBean>();
		try {
			String sql = "";
			ArrayList<Object> param = new ArrayList<Object>();
			sql = " select  "
					+ " asg.tranDateTime,asg.amount,s.lastName,s.firstName,c.lc,COALESCE(s.sapid,0) as sapid  "
					+ " from " + " exam.assignmentpayment asg" + " inner join exam.students s on s.sapid = asg.sapid "
					+ " inner join exam.centers c on c.centerCode = s.centerCode " + " where" + " booked = 'Y' ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(asg.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(asg.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and asg.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
			}
			sql = sql + " group by trackId";
			Object[] args = param.toArray();
			revenue = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));
			if (!revenue.isEmpty()) {
				for (OperationsRevenueBean bean : revenue) {
					bean.setRevenueSource("Assignment");
				}
			}
		} catch (Exception e) {
			
		}
		return revenue;
	}

	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getExamBookingsRevenueList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource); ArrayList<OperationsRevenueBean>
	 * srRevenue = new ArrayList<OperationsRevenueBean>(); try {
	 * 
	 * 
	 * String sql =
	 * " select eb.*,st.firstName,st.lastName from exam.exambookings eb,exam.students st where (eb.booked = 'Y' or eb.booked = 'RL')  "
	 * +
	 * " and date(eb.tranDateTime) >= ? and date(eb.tranDateTime) <= ? and st.sapid = eb.sapid group by trackId "
	 * ;
	 * 
	 * 
	 * 
	 * String sql = " select eb.*,st.firstName,st.lastName,c.lc " +
	 * " from exam.exambookings eb,exam.students st,exam.centers c " + " where " +
	 * " (eb.booked = 'Y' or eb.booked = 'RL') and " +
	 * " date(eb.tranDateTime) >= ? and " + " date(eb.tranDateTime) <= ? and " +
	 * " st.sapid = eb.sapid and" + " c.centerCode = st.centerCode" +
	 * " group by trackId ";
	 * 
	 * 
	 * String sql = " SELECT * " + " FROM" + " (" +
	 * "	SELECT st.firstName,st.lastName,c.lc,eb.tranDateTime,eb.amount,eb.sapid , eb.trackId"
	 * + "	FROM exam.exambookings eb,exam.students st , exam.centers c" +
	 * "	WHERE (eb.booked = 'Y' or eb.booked = 'RL')  and" +
	 * "	date(eb.tranDateTime) >= ? and" + "	date(eb.tranDateTime) <=  ? and" +
	 * "	st.sapid = eb.sapid and" + "	c.centerCode = st.centerCode" +
	 * " GROUP BY trackId" + " UNION " +
	 * "	SELECT st.firstName,st.lastName,c.lc,eb.tranDateTime,eb.amount,eb.sapid, eb.trackId"
	 * + "	FROM exam.exambookings_history eb,exam.students st , exam.centers  c" +
	 * "	WHERE (eb.booked = 'Y' or eb.booked = 'RL')  and" +
	 * "	date(eb.tranDateTime) >= ? and" + "	date(eb.tranDateTime) <=  ? and" +
	 * "	st.sapid = eb.sapid and" + "	c.centerCode = st.centerCode " +
	 * " GROUP BY trackId" + " UNION " +
	 * "	SELECT st.firstName,st.lastName,c.lc as lc,mwp.tranDateTime,mwp.amount as amount,st.sapid  ,  mwp.trackId"
	 * + "	FROM exam.mba_wx_bookings mwb" +
	 * "	INNER JOIN exam.mba_wx_payment_records mwp on mwb.paymentRecordId = mwp.id and mwb.sapid = mwp.sapid "
	 * + "	INNER JOIN exam.students st on st.sapid = mwb.sapid " +
	 * "	INNER JOIN exam.centers  c on  c.centerCode = st.centerCode" +
	 * "	WHERE" + "	(mwb.bookingStatus = 'Y' or mwb.bookingStatus = 'RL')  " +
	 * "	and date(mwp.tranDateTime) >= ? " +
	 * "	and date(mwp.tranDateTime) <=  ?" + " GROUP BY mwp.trackId" + " UNION "
	 * +
	 * "	SELECT st.firstName,st.lastName,c.lc as lc,mxp.tranDateTime,mxp.amount as amount,st.sapid  , mxp.trackId"
	 * + "	FROM exam.mba_x_bookings mxb" +
	 * "	INNER JOIN exam.mba_x_payment_records mxp on mxb.paymentRecordId = mxp.id and mxb.sapid = mxp.sapid "
	 * + "	INNER JOIN exam.students st on st.sapid = mxb.sapid " +
	 * "	INNER JOIN exam.centers  c on  c.centerCode = st.centerCode" +
	 * "	WHERE" + "	(mxb.bookingStatus = 'Y' or mxb.bookingStatus = 'RL')  " +
	 * "	and date(mxp.tranDateTime) >= ? " +
	 * "	and date(mxp.tranDateTime) <=  ?" + " GROUP BY mxp.trackId" + " )t1 " +
	 * " GROUP BY trackId;"; srRevenue = (ArrayList<OperationsRevenueBean>)
	 * jdbcTemplate.query(sql, new Object[] { operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate(), operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate(), operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate(), operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate() }, new
	 * BeanPropertyRowMapper(OperationsRevenueBean.class));
	 * 
	 * if (!srRevenue.isEmpty()) { for (OperationsRevenueBean revenue : srRevenue) {
	 * revenue.setRevenueSource("Exam Registration"); } }
	 * } catch
	 * (Exception e) {  }
	 * 
	 * return srRevenue; }
	 */
	public ArrayList<OperationsRevenueBean> getExamBookingsRevenueDownloadPG(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> revenue = new ArrayList<OperationsRevenueBean>();
		try {
			ArrayList<Object> param = new ArrayList<Object>();
			String sql = "";

			sql = "select eb.tranDateTime,eb.amount,s.firstName,s.lastName,c.lc ,COALESCE(s.sapid,0) as sapid "
					+ "	 from " + "		exam.exambookings eb" + "     INNER JOIN exam.students s ON s.sapid = eb.sapid"
					+ "		INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + "	 where "
					+ "	  (eb.booked = 'Y' or eb.booked = 'RL')    ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(eb.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(eb.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and ep.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
			}
			sql = sql + " group by trackId ";

			sql = sql + "	 UNION ALL   " + "	 select "
					+ "	 ebh.tranDateTime,ebh.amount,s.firstName,s.lastName,c.lc ,COALESCE(s.sapid,0) as sapid	"
					+ "	 from " + "		exam.exambookings_history ebh"
					+ "        INNER JOIN exam.students s ON s.sapid = ebh.sapid"
					+ "		INNER JOIN exam.centers c ON c.centerCode = s.centerCode" + "	 where "
					+ "		(ebh.booked = 'Y' or ebh.booked = 'RL')    ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(ebh.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(ebh.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and ep.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " AND s.program not in ('MBA - X','MBA - WX')";
				}
			}
			sql = sql + "	 group by trackId ";

			Object[] args = param.toArray();

			revenue = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));

			if (!revenue.isEmpty()) {
				for (OperationsRevenueBean b : revenue) {
					b.setRevenueSource("Exam Registration");
				}
			}
		} catch (Exception e) {
			
		}

		return revenue;
	}

	public ArrayList<OperationsRevenueBean> getExamBookingsRevenueDownloadMBAx(
			OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> revenue = new ArrayList<OperationsRevenueBean>();
		try {
			ArrayList<Object> param = new ArrayList<Object>();
			String sql = "";

			sql = "select "
					+ "		 mwp.tranDateTime,mwp.amount,s.firstName,s.lastName,c.lc ,COALESCE(s.sapid,0) as sapid "
					+ "	 from " + "		exam.mba_x_bookings eb"
					+ "      INNER JOIN exam.students s ON s.sapid = eb.sapid"
					+ "		 INNER JOIN exam.centers c ON c.centerCode = s.centerCode"
					+ "      INNER JOIN exam.mba_x_payment_records mwp on eb.paymentRecordId = mwp.id and eb.sapid = mwp.sapid "
					+ "	 where " + "	  (eb.bookingStatus = 'Y' or eb.bookingStatus = 'RL')    ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(mwp.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(mwp.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and mwp.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId ";

			Object[] args = param.toArray();
			revenue = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));

			if (!revenue.isEmpty()) {
				for (OperationsRevenueBean b : revenue) {
					b.setRevenueSource("Exam (MBAx)");
				}
			}
		} catch (Exception e) {
			
		}
		return revenue;
	}

	public ArrayList<OperationsRevenueBean> getExamBookingsRevenueDownloadMBAwx(
			OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> revenue = new ArrayList<OperationsRevenueBean>();

		try {
			ArrayList<Object> param = new ArrayList<Object>();
			String sql = "";

			sql = "select "
					+ "		 mwp.tranDateTime,mwp.amount,s.firstName,s.lastName,c.lc ,COALESCE(s.sapid,0) as sapid "
					+ "	 from " + "		exam.mba_wx_bookings eb"
					+ "      INNER JOIN exam.students s ON s.sapid = eb.sapid"
					+ "		 INNER JOIN exam.centers c ON c.centerCode = s.centerCode"
					+ "      INNER JOIN exam.mba_wx_payment_records mwp on eb.paymentRecordId = mwp.id and eb.sapid = mwp.sapid "
					+ "	 where " + "	  (eb.bookingStatus = 'Y' or eb.bookingStatus = 'RL')    ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(mwp.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(mwp.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and mwp.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId ";

			Object[] args = param.toArray();
			revenue = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));
			if (!revenue.isEmpty()) {
				for (OperationsRevenueBean b : revenue) {
					b.setRevenueSource("Exam (MBAwx)");
				}
			}

		} catch (Exception e) {

			
		}

		return revenue;
	}

	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getPCPBookingsRevenueList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource); ArrayList<OperationsRevenueBean>
	 * pcpRevenueList = new ArrayList<OperationsRevenueBean>(); try {
	 * 
	 * 
	 * String sql =
	 * "select pcp.*,st.firstName,st.lastName from acads.pcpbookings pcp,exam.students st where pcp.booked = 'Y' "
	 * +
	 * " and date(pcp.tranDateTime) >= ? and date(pcp.tranDateTime) <= ?  and st.sapid = pcp.sapid"
	 * + "  group by trackId";
	 * 
	 * String sql = "select pcp.*,st.firstName,st.lastName,c.lc " +
	 * " from acads.pcpbookings pcp,exam.students st,exam.centers c " + " where " +
	 * " pcp.booked = 'Y' and " + " date(pcp.tranDateTime) >= ? and " +
	 * " date(pcp.tranDateTime) <= ?  and " + " st.sapid = pcp.sapid and" +
	 * " c.centerCode = st.centerCode" + " group by trackId"; pcpRevenueList =
	 * (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, new Object[] {
	 * operationsRevenue.getStartDate(), operationsRevenue.getEndDate() }, new
	 * BeanPropertyRowMapper(OperationsRevenueBean.class)); if
	 * (!pcpRevenueList.isEmpty()) { for (OperationsRevenueBean revenue :
	 * pcpRevenueList) { revenue.setRevenueSource("PCP Registration"); } }
	 * 
	 * } catch (Exception e) {  }
	 * 
	 * return pcpRevenueList; }
	 */
	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getAdhocPaymentRevenueList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource); ArrayList<OperationsRevenueBean>
	 * srRevenueList = new ArrayList<OperationsRevenueBean>();
	 * 
	 * try {
	 * 
	 * 
	 * String sql =
	 * "select ad.*,st.firstName,st.lastName from portal.ad_hoc_payment ad ,exam.students st where ad.transtatus = 'Payment Successful' and st.sapid = ad.sapid "
	 * + " and date(ad.createdDate) >= ? and date(ad.createdDate) <= ? ";
	 * 
	 * 
	 * 
	 * String sql = "select ad.*,st.firstName,st.lastName,c.lc " +
	 * " from portal.ad_hoc_payment ad ,exam.students st, exam.centers c " +
	 * " where " + " ad.transtatus = 'Payment Successful' and " +
	 * " date(ad.createdDate) >= ? and " + " date(ad.createdDate) <= ? and" +
	 * " st.sapid = ad.sapid and " + " c.centerCode = st.centerCode" +
	 * " group by trackId";
	 * 
	 * 
	 * // updated query as it was giving incorrect count
	 * 
	 * String sql = "" + " SELECT" +
	 * "	ad.tranDateTime,ad.amount,st.firstName,st.lastName,c.lc,COALESCE(st.sapid,0) as sapid "
	 * + " FROM" + "	portal.ad_hoc_payment ad " + " LEFT JOIN " +
	 * "	exam.students st on st.sapid = ad.sapid" + " LEFT JOIN " +
	 * "	exam.centers c on c.centerCode = st.centerCode" + " WHERE" +
	 * "	ad.transtatus = 'Payment Successful' AND" +
	 * "	date(ad.createdDate) >= ? AND" + "	date(ad.createdDate) <= ? " +
	 * " GROUP BY trackId";
	 * 
	 * srRevenueList = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql,
	 * new Object[] { operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate() }, new
	 * BeanPropertyRowMapper(OperationsRevenueBean.class));
	 * 
	 * if (!srRevenueList.isEmpty()) { for (OperationsRevenueBean revenue :
	 * srRevenueList) { revenue.setRevenueSource("Adhoc Payment"); } } } catch
	 * (Exception e) {  }
	 * 
	 * return srRevenueList; }
	 */
	public ArrayList<OperationsRevenueBean> getPCPBookingsRevenueDownload(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> pcpRevenueList = new ArrayList<OperationsRevenueBean>();
		try {
			String sql = "";
			ArrayList<Object> param = new ArrayList<Object>();
			sql = "select  pcp.tranDateTime,pcp.amount,s.firstName,s.lastName,c.lc ,COALESCE(s.sapid,0) as sapid   "
					+ " from " + " acads.pcpbookings pcp" + " inner join exam.students s on s.sapid = pcp.sapid "
					+ " inner join exam.centers c on c.centerCode = s.centerCode " + " where" + " booked = 'Y' ";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(pcp.tranDateTime) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(pcp.tranDateTime) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			/*
			 * Payment option not available for PCP
			 * if(!operationsRevenue.getPaymentOption().isEmpty() &&
			 * !StringUtils.isBlank(operationsRevenue.getPaymentOption())) { sql =
			 * sql+" and sr.paymentOption = ? ";
			 * param.add(operationsRevenue.getPaymentOption()); }
			 */
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
				if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())
						|| "MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId";
			Object[] args = param.toArray();
			pcpRevenueList = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));
			if (!pcpRevenueList.isEmpty()) {
				for (OperationsRevenueBean revenue : pcpRevenueList) {
					revenue.setRevenueSource("PCP Registration");
				}
			}
		} catch (Exception e) {
			
		}
		return pcpRevenueList;
	}

	public ArrayList<OperationsRevenueBean> getAdhocPaymentRevenueDownloadList(
			OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<OperationsRevenueBean> revenueList = new ArrayList<OperationsRevenueBean>();
		try {
			String sql = "";
			ArrayList<Object> param = new ArrayList<Object>();
			sql = " select  adp.tranDateTime,adp.amount,s.firstName,s.lastName,c.lc,COALESCE(s.sapid,0) as sapid "
					+ " from " + " portal.ad_hoc_payment adp" + " inner join exam.students s on s.sapid = adp.sapid "
					+ " inner join exam.centers c on c.centerCode = s.centerCode " + " where"
					+ " tranStatus = 'Payment Successful'";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(adp.createdDate) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(adp.createdDate) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and adp.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
				if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())
						|| "MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId";
			Object[] args = param.toArray();
			revenueList = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));
			if (!revenueList.isEmpty()) {
				for (OperationsRevenueBean revenue : revenueList) {
					revenue.setRevenueSource("Adhoc Payment");
				}
			}
		} catch (Exception e) {
			
		}
		return revenueList;
	}
	// -------------------- queries for revenue list download end
	// ------------------------------

	// added by stef on 27-Sep-2017
	public OperationsRevenueBean getAdhocRefundPaymentRevenue(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		OperationsRevenueBean srRevenue = new OperationsRevenueBean();
		srRevenue.setRevenueSource("Adhoc Refund Payment");
		long amount = 0;
		try {

			String sql = "select sum(amount) from exam.ad_hoc_refund where "
					+ " date(createdDate) >= ? and date(createdDate) <= ? ";

			amount = (Long) jdbcTemplate.queryForObject(sql,
					new Object[] { operationsRevenue.getStartDate(), operationsRevenue.getEndDate() }, Long.class);

			srRevenue.setAmount(Double.valueOf(amount));

		} catch (Exception e) {
			
		}

		return srRevenue;
	}
	//

	public HashMap<String, Integer> getMapOfStudentNumberAndNoOfSubjectsApplicable() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid, count(subject) as numberOfSubjectsApplicable from exam.students s, exam.program_subject ps "
				+ " where s.program = ps.program " + " and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and  ps.sem <= (select max(sem) from exam.registration r where r.sapid = s.sapid) "
				+ " group by s.sapid";

		List<StudentLearningMetricsBean> metricsList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentLearningMetricsBean.class));

		HashMap<String, Integer> mapOfStudentNumberAndNoOfSubjectsApplicable = new HashMap<>();

		for (StudentLearningMetricsBean bean : metricsList) {
			mapOfStudentNumberAndNoOfSubjectsApplicable.put(bean.getSapid(), bean.getNumberOfSubjectsApplicable());
		}

		return mapOfStudentNumberAndNoOfSubjectsApplicable;
	}

	// Added by Steffi : 2/04/2018 Added to show a report of payments which were
	// recieved twice from student
	/*
	 * public ArrayList<AssignmentPaymentBean>
	 * getDoubleAssignmentPaymentsForGivenYearMonth(StudentMarksBean
	 * studentMarksBean){ jdbcTemplate = new JdbcTemplate(dataSource); String
	 * sqlQuery =
	 * "SELECT *, count(*) FROM exam.assignmentpayment where year = ? and month = ?  and booked ='Y'"
	 * + " group by sapid,subject having count(*) > 1";
	 * 
	 * ArrayList<AssignmentPaymentBean> assignmentPaymentList
	 * =(ArrayList<AssignmentPaymentBean>)jdbcTemplate.query(sqlQuery,new
	 * Object[]{studentMarksBean.getYear(),studentMarksBean.getMonth()},new
	 * BeanPropertyRowMapper(AssignmentPaymentBean.class)); return
	 * assignmentPaymentList; }
	 */

	// pass generation for executive_exam_bookings table :-START
	public ArrayList<ExamBookingTransactionBean> listOfExecutiveExamBookings(StudentMarksBean executiveBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select e.*, ps.sifySubjectCode"
				+ "	from exam.executive_exam_bookings e,exam.program_subject ps,exam.students s  " + "where e.year=? "
				+ "and e.month=?" + "and e.booked = 'Y'" + "and  (e.password = '' OR e.password IS NULL)"
				// add to have sifycode for each record by PS
				+ " and s.sapid = e.sapid " + " and ps.sem = e.sem "
				+ "	and ps.prgmStructApplicable = s.PrgmStructApplicable " + " and ps.program = s.program"
				+ " and e.program = s.program" + " and ps.subject = e.subject " + " and s.sapid = ( select s2.sapid "
				+ "						from exam.students s2 " + "						where s2.sapid=s.sapid "
				+ "						and s2.sem = (  select max(s3.sem) from exam.students s3 where s3.sapid=s.sapid  )  )"
		// add to have sifycode for each record by PS
		;
		ArrayList<ExamBookingTransactionBean> bookedList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { executiveBean.getYear(), executiveBean.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookedList;
	}

	public boolean assignPassword(StudentMarksBean executiveBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		try {
			sql = "update exam.executive_exam_bookings e " + " set e.password= ? " + " where e.sapId= ? "
					+ " and e.year=? " + " and e.month=? ";

			jdbcTemplate.update(sql, new Object[] { executiveBean.getPassword(), executiveBean.getSapid(),
					executiveBean.getYear(), executiveBean.getMonth() });
		}

		catch (Exception e) {
			
			return false;
		}
		return true;
	}

	public ArrayList<StudentMarksBean> listOfBookings(StudentMarksBean executiveBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select distinct e.sapId," + " s.firstname,s.lastName,e.program,e.sem, "
				+ " e.subject,e.password,s.emailId,s.mobile,s.altPhone,s.PrgmStructApplicable as programStructApplicable,e.booked , "
				+ " e.month,e.year ,ec.city, ec.examCenterName, e.examDate,e.examTime,e.examTime, "
				+ " e.createdBy,e.createdDate,e.lastModifiedBy,e.lastModifiedDate,s.enrollmentYear,s.enrollmentMonth,"
				+ " ps.sifySubjectCode " + " from  exam.executive_examcenter ec," + " exam.program_subject ps, "
				+ " exam.executive_exam_bookings e " + "						LEFT JOIN exam.students s "
				+ "						on e.sapId=s.sapid "
				+ "						where e.programStructApplicable=s.PrgmStructApplicable "
				+ "						and e.centerId=ec.centerId "
				+ "						and e.program=s.program " + "						and e.year=? "
				+ "						and e.month=? " + "						and e.booked='Y'"
				// add to have sifycode for each record by PS
				+ " and ps.sem=s.sem " + "	and ps.prgmStructApplicable = s.PrgmStructApplicable "
				+ " and ps.program = s.program " + " and ps.subject = e.subject " + " and ps.program = e.program "
				+ " and s.sapid = ( select s2.sapid " + "						from exam.students s2 "
				+ "						where s2.sapid=s.sapid "
				+ "						and s2.sem = (  select max(s3.sem) from exam.students s3 where s3.sapid=s.sapid  )  )"
		// add to have sifycode for each record by PS
		;
		ArrayList<StudentMarksBean> confirmedPassList = (ArrayList<StudentMarksBean>) jdbcTemplate.query(sql,
				new Object[] { executiveBean.getYear(), executiveBean.getMonth() },
				new BeanPropertyRowMapper(StudentMarksBean.class));
		return confirmedPassList;
	}
	// END

	// -------- Pending Bookings report for Executive Programs Start -------

	public ArrayList<ExamBookingTransactionBean> getExecutiveConfirmedBookingForGivenYearMonth(
			StudentMarksBean studentMarks, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> completeBookingList = new ArrayList<ExamBookingTransactionBean>();

		String sql = "SELECT * FROM   exam.students s, exam.executive_examcenter b, exam.executive_exam_bookings a "
				+ " 	where a.centerId = b.centerId " + "		 and s.enrollmentYear = ? "
				+ " 	 and s.enrollmentMonth = ? " + "		 and a.year = ? " + " 	 and a.month = ? "
				+ "		 and a.booked = 'Y' " + " 	 and a.sapId = s.sapid ";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";

		}

		sql += " group by a.sapId, a.subject "
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql,
				new Object[] { studentMarks.getEnrollmentYear(), studentMarks.getEnrollmentMonth(),
						studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		completeBookingList.addAll(bookingList);

		return completeBookingList;
	}

	public String getCommaSepareatedSubjects(ArrayList<String> subjects) {
		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			String str = subjects.get(i);
			if (i == 0) {
				subjectCommaSeparated = "'" + str.replace("'", "\\'") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + str.replace("'", "\\'") + "'";
			}
		}

		return subjectCommaSeparated;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AssignmentStatusBean> getValidExecutiveStudentSubjectList(StudentMarksBean studentMarks,
			ArrayList<String> examSubjectsList, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = getCommaSepareatedSubjects(examSubjectsList);
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "select s.*,r.sem, ps.subject from exam.students s , exam.registration r, "
				+ " exam.program_subject ps"
				+ " where s.sapid = r.sapid and r.sem = ps.sem and r.program = ps.program and r.sem = ps.sem "
				+ " and s.PrgmStructApplicable = ps.prgmStructApplicable " + " and  (" + "           ( "
				+ "              (s.validityEndYear >= Year(sysdate()) "
				+ "              and s.validityEndMonth >= concat('\\\'',substring(monthname(sysdate()),1,3),'\\\'')) "
				+ "			) " + "           or " + "           ( "
				+ "               (s.validityEndYear > Year(sysdate()) "
				+ "               and s.validityEndMonth < concat('\\\'',substring(monthname(sysdate()),1,3),'\\\'')) "
				+ "				) " + "           ) " + "                     " + " and s.program in ('EPBM','MPDV')"
				+ " and ps.subject in (" + subjectCommaSeparated + ")"
				+ " and (s.enrollmentYear <> ? or s.enrollmentMonth <> ? ) ";

		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());

		if ((studentMarks.getEnrollmentYear() != null && !"".equals(studentMarks.getEnrollmentYear()))
				&& (studentMarks.getEnrollmentMonth() != null && !"".equals(studentMarks.getEnrollmentMonth()))) {
			sql = sql + " and s.enrollmentYear = ? and s.enrollmentMonth = ?  ";
			parameters.add(studentMarks.getEnrollmentYear());
			parameters.add(studentMarks.getEnrollmentMonth());

		}

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		Object[] args = parameters.toArray();

		ArrayList<AssignmentStatusBean> studentsList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql, args,
				new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return studentsList;
	}

	public ArrayList<AssignmentStatusBean> getValidExecutiveStudentsFailRecords(StudentMarksBean studentMarks,
			ArrayList<String> examSubjectsList, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = getCommaSepareatedSubjects(examSubjectsList);
		String sql = "select s.*,pf.* from exam.students s, exam.passfail pf " + " where " + " pf.sapid = s.sapid "
				+ " and pf.isPass = 'N' " + " and s.sapid " + " in " + "( SELECT sapid FROM exam.students s "
				+ " where " + " (" + "           ( " + "              (s.validityEndYear >= Year(sysdate()) "
				+ "              and s.validityEndMonth >= concat('\\\'',substring(monthname(sysdate()),1,3),'\\\'')) "
				+ "			) " + "           or " + "           ( "
				+ "               (s.validityEndYear > Year(sysdate()) "
				+ "               and s.validityEndMonth < concat('\\\'',substring(monthname(sysdate()),1,3),'\\\'')) "
				+ "				) " + "           ) " + "  ) " + "                     "
				+ " and s.program in ('EPBM','MPDV')" + " and pf.subject in (" + subjectCommaSeparated + ") ";

		if ((studentMarks.getEnrollmentYear() != null && !"".equals(studentMarks.getEnrollmentYear()))
				&& (studentMarks.getEnrollmentMonth() != null && !"".equals(studentMarks.getEnrollmentMonth()))) {
			sql = sql + " and s.enrollmentYear = ? and s.enrollmentMonth = ?  ";
		}

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql += " order by pf.sem, subject  asc ";

		ArrayList<AssignmentStatusBean> passFailList = (ArrayList<AssignmentStatusBean>) jdbcTemplate.query(sql,
				new Object[] { studentMarks.getEnrollmentYear(), studentMarks.getEnrollmentMonth() },
				new BeanPropertyRowMapper(AssignmentStatusBean.class));
		return passFailList;
	}

	public ArrayList<String> getSubjectsListWhoseExamHasBeenConducted(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select les.subject from exam.live_exam_subjects les " + " where "
				+ " les.program in ('EPBM','MPDV')" + " and " + " les.acadYear = ? and les.acadMonth = ? " + " and "
				+ " les.examYear = ? and les.examMonth = ? ";

		ArrayList<String> examSubjectsList = (ArrayList<String>) jdbcTemplate
				.query(sql,
						new Object[] { studentMarks.getEnrollmentYear(), studentMarks.getEnrollmentMonth(),
								studentMarks.getYear(), studentMarks.getMonth() },
						new SingleColumnRowMapper(String.class));
		return examSubjectsList;

	}
	// -------- Pending Bookings report for Executive Programs End -------

	// ---------- Report for Checking Project Submission Status ------
	public ArrayList<StudentMarksBean> projectSubmissionStatusReport() {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "";

		ArrayList<StudentMarksBean> marksBean = (ArrayList<StudentMarksBean>) jdbcTemplate.queryForObject(sql,
				new Object[] {}, new BeanPropertyRowMapper(StudentMarksBean.class));

		return marksBean;
	}

	public ArrayList<StudentExamBean> getValidNotBookedProjectList(String year, String month) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean> marksBean = new ArrayList<StudentExamBean>();
		try {
			StringBuffer sql = new StringBuffer("select distinct es.sapid,es.firstName,es.lastName,es.program "
					+ " from exam.students es, exam.registration r " + " where r.sapid = es.sapid "
					+ " and r.sem  in (select sem from exam.program_sem_subject where subject IN ('Project', 'Module 4 - Project') and consumerProgramStructureId=es.consumerProgramStructureId) "
					+ " and STR_TO_DATE(concat('30-',es.validityEndMonth,'-',es.validityEndYear), '%d-%b-%Y') >= curdate()"
					+ " and es.sapid not in (select distinct sapid from exam.exambookings where booked = 'Y' "
					+ " and subject IN ('Project', 'Module 4 - Project')) "
					+ " and es.sapid not in (select distinct sapid from exam.exambookings_history where booked = 'Y' "
					+ " and subject IN ('Project', 'Module 4 - Project')) "
					+ " and es.program in (select distinct program from exam.program_subject where subject IN ('Project', 'Module 4 - Project') )");
			if (!year.isEmpty() && !month.isEmpty()) {
				sql.append(
						"and STR_TO_DATE(concat(r.year,'-',r.month,'-01'), '%Y-%b-%d') <= DATE_ADD(STR_TO_DATE(concat(?,'-',?,'-01'), '%Y-%b-%d'), INTERVAL -5 MONTH)");
				marksBean = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql.toString(), new Object[] { year, month },
						new BeanPropertyRowMapper(StudentExamBean.class));
			} else {
				marksBean = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql.toString(), new Object[] {},
						new BeanPropertyRowMapper(StudentExamBean.class));
			}

			return marksBean;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	public ArrayList<PassFailExamBean> getValidStudentsNotClearedProjectList(String year, String month) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> marksBean = new ArrayList<PassFailExamBean>();
		try {
			String sql = "select distinct sapid, writtenscore, writtenMonth ,writtenYear, subject,program,name "
					+ " from " + " exam.passfail " + " where subject IN ('Project', 'Module 4 - Project') "
					+ " and (writtenscore in ('0','AB','') or writtenscore < 50) ";
			if (!year.isEmpty() && !month.isEmpty()) {
				sql += " and sapid in " + " (" + " select " + " distinct es.sapid " + " from "
						+ " exam.students es, exam.registration r" + " where " + " r.sapid = es.sapid and "
						+ " r.sem = (select sem from exam.program_sem_subject where subject IN ('Project', 'Module 4 - Project') and consumerProgramStructureId=es.consumerProgramStructureId) and"
						+ " STR_TO_DATE(concat('30-',es.validityEndMonth,'-',es.validityEndYear), '%d-%b-%Y') >= curdate()"
						+ " and STR_TO_DATE(concat(r.year,'-',r.month,'-01'), '%Y-%b-%d') <= DATE_ADD(STR_TO_DATE(concat(?,'-',?,'-01'), '%Y-%b-%d'), INTERVAL -5 MONTH)"
						+ " )";
			} else {
				sql += " and sapid in " + " (" + " select " + " distinct es.sapid " + " from "
						+ " exam.students es, exam.registration r" + " where " + " r.sapid = es.sapid and "
						+ " r.sem = (select sem from exam.program_sem_subject where subject IN ('Project', 'Module 4 - Project') and consumerProgramStructureId=es.consumerProgramStructureId) and"
						+ " STR_TO_DATE(concat('30-',es.validityEndMonth,'-',es.validityEndYear), '%d-%b-%Y') >= curdate()"
						+ " )";
			}

			if (!year.isEmpty() && !month.isEmpty()) {
				marksBean = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql, new Object[] { year, month },
						new BeanPropertyRowMapper(PassFailExamBean.class));
			} else {
				marksBean = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql, new Object[] {},
						new BeanPropertyRowMapper(PassFailExamBean.class));
			}
			return marksBean;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}

	public ArrayList<ExamBookingExamBean> getStudentPassedInRevalAndRegistered(ExamBookingExamBean examBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<ExamBookingExamBean> studentList = new ArrayList<ExamBookingExamBean>();

		String sql = "	SELECT b.* FROM exam.passfail p,exam.marks m , exam.exambookings b "
				+ " where ( (p.writtenYear=? and p.writtenMonth = ? and m.year=p.writtenYear and m.month = p.writtenMonth) "
				+ " or "
				+ " (p.assignmentYear=? and p.assignmentMonth = ? and m.year=p.assignmentYear and m.month = p.assignmentMonth)) "
				+ " and m.markedForRevaluation='Y' and p.sapid = m.sapid and p.subject = m.subject "
				+ " and b.month = ? and b.year = ? and b.subject=p.subject and p.sapid=b.sapid "
				+ " and p.isPass = 'Y' " + " and b.booked ='Y'";

		try {
			studentList = (ArrayList<ExamBookingExamBean>) jdbcTemplate.query(sql,
					new Object[] { examBean.getYear(), examBean.getMonth(), examBean.getYear(), examBean.getMonth(),
							examBean.getBookedMonth(), examBean.getBookedYear() },
					new BeanPropertyRowMapper(ExamBookingExamBean.class));
		} catch (Exception e) {
			
		}
		return studentList;

	}

	public void assignPasswordRetailAndCorporate(String sapid, String password, StudentMarksBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		try {
			sql = "update exam.exambookings e " + " set e.password=? " + " where e.sapid=? " + " and e.year=? "
					+ " and e.month=? " + " and e.booked = 'Y'";

			jdbcTemplate.update(sql, new Object[] { password, sapid, bean.getYear(), bean.getMonth() });

		}

		catch (Exception e) {
			

		}

	}

	public HashMap<String, String> getStudentExamPasswordsNotBlank(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT sapid, password FROM exam.exambookings " + " where booked = 'Y' "
				+ " and password <> '' and password is not null " + " and year = ? " + " and month = ? "
				+ " and examMode = 'Online' " + " and subject NOT IN ('Project', 'Module 4 - Project') " + " and program not in ('MPDV','EPBM')"
				+ " group by sapid order by sapid";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		HashMap<String, String> studentPasswordMap = new HashMap<>();
		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			studentPasswordMap.put(bean.getSapid(), bean.getPassword());
		}
		return studentPasswordMap;
	}

	public ArrayList<String> getStudentExamPasswordsBlank(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT sapid FROM exam.exambookings " + " where booked = 'Y' "
				+ " and (password = '' or password is null) " + " and year = ? " + " and month = ? "
				+ " and examMode = 'Online' " + " and subject NOT IN ('Project', 'Module 4 - Project') " + " and program not in ('MPDV','EPBM')"
				+ " group by sapid order by sapid";

		ArrayList<String> bookingListForBlankPassword = new ArrayList<String>();
		try {
			bookingListForBlankPassword = (ArrayList<String>) jdbcTemplate.query(sql,
					new Object[] { studentMarks.getYear(), studentMarks.getMonth() },
					new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			
			return bookingListForBlankPassword;
		}
		return bookingListForBlankPassword;
	}

	/*
	 * Added by Stef
	 */ public List<PassFailExamBean> getProgramCompleteGraceAppliedForAStudent(String program, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> studentGraceAppliedDetails = new ArrayList<PassFailExamBean>();
		String sql = "SELECT * FROM exam.passfail WHERE sapid = ? and remarks = 'End of Program validity grace given' and program = ?";
		try {
			studentGraceAppliedDetails = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql,
					new Object[] { sapid, program }, new BeanPropertyRowMapper(PassFailExamBean.class));
		} catch (Exception e) {
			
		}
		return studentGraceAppliedDetails;
	}

	public ProgramExamBean getSingleProgramDetails(String program, String programStructure) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.programs where program = ? and programStructure = ?";
		ProgramExamBean beanList = new ProgramExamBean();
		try {
			beanList = (ProgramExamBean) jdbcTemplate.queryForObject(sql, new Object[] { program, programStructure },
					new BeanPropertyRowMapper(ProgramExamBean.class));
		} catch (Exception e) {
			
		}
		return beanList;
	}

	public ProgramExamBean getSingleProgramDetailsFromMasterKey(String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.programs where consumerProgramStructureId = ?";
		ProgramExamBean beanList = new ProgramExamBean();
		try {
			beanList = (ProgramExamBean) jdbcTemplate.queryForObject(sql, new Object[] { consumerProgramStructureId },
					new BeanPropertyRowMapper(ProgramExamBean.class));
		} catch (Exception e) {
			
		}
		return beanList;
	}

	public List<PassFailExamBean> getProgramCompleteGraceToBeAppliedForAStudent(String program, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> studentGraceToBeAppliedDetails = new ArrayList<PassFailExamBean>();
		String sql = "SELECT p.*,s.PrgmStructApplicable as prgmStructApplicable FROM exam.passfail p , exam.students s WHERE s.sapid = p.sapid and p.sapid = ? and p.program = ? and p.isPass='N' ";
		try {
			studentGraceToBeAppliedDetails = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql,
					new Object[] { sapid, program }, new BeanPropertyRowMapper(PassFailExamBean.class));
		} catch (Exception e) {
			
		}
		return studentGraceToBeAppliedDetails;
	}
	// */

	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingForGivenYearMonthProjectFeeExemptList(
			StudentMarksBean studentMarks, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> completeBookingList = new ArrayList<ExamBookingTransactionBean>();

		// for online students sql and for offline sql2
		String sql = "SELECT es.*,r.program,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral, "
				+ " s.validityEndYear,s.validityEndMonth,r.sem"
				+ "  FROM exam.examfeeexemptsubject es , exam.students s, exam.registration r " + " where "
				+ " es.year = ? and es.month =? and es.subject IN ('Project', 'Module 4 - Project') "
				+ " and es.sapid not in (select distinct e.sapid from exam.exambookings e where e.year=? and e.month=? and e.subject  IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')"
				+ " and es.sapid not in (select distinct eh.sapid from exam.exambookings_history eh where eh.year=? and eh.month=? and eh.subject  IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')" + " and s.sapid = es.sapid" + " and s.sapid = r.sapid" + " and r.sem in ("+PROJECT_APPLICABLE_PROGRAM_SEM_LIST+") ";

		String sql2 = "SELECT es.*,s.program,s.firstName,s.lastName,s.emailId,s.mobile,s.altPhone,s.enrollmentMonth,s.enrollmentYear,s.isLateral, "
				+ " s.validityEndYear,s.validityEndMonth" + " FROM exam.examfeeexemptsubject es , exam.students s "
				+ " where " + " es.year = ? and es.month =? and es.subject IN ('Project', 'Module 4 - Project') "
				+ " and es.sapid not in (select distinct e.sapid from exam.exambookings e where e.year=? and e.month=? and e.subject IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')"
				+ " and es.sapid not in (select distinct eh.sapid from exam.exambookings_history eh where eh.year=? and eh.month=? and eh.subject IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')" + " and s.sapid = es.sapid"
				+ " and s.sapid not in (SELECT es.sapid FROM exam.examfeeexemptsubject es , exam.students s, exam.registration r "
				+ " where " + " es.year = ? and es.month =? and es.subject IN ('Project', 'Module 4 - Project') "
				+ " and es.sapid not in (select distinct e.sapid from exam.exambookings e where e.year=? and e.month=? and e.subject IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')"
				+ " and es.sapid not in (select distinct eh.sapid from exam.exambookings_history eh where eh.year=? and eh.month=? and eh.subject IN ('Project', 'Module 4 - Project') "
				+ " and booked ='Y')" + " and s.sapid = es.sapid" + " and s.sapid = r.sapid" + " and r.sem in ("+PROJECT_APPLICABLE_PROGRAM_SEM_LIST+") )";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql += " group by es.sapid, es.subject;";

		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
			sql2 = sql2 + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}

		sql2 += " group by es.sapid, es.subject;";


		ArrayList<ExamBookingTransactionBean> bookingList1 = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql,
				new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth(), studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		ArrayList<ExamBookingTransactionBean> bookingList2 = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql2,
				new Object[] { studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth(), studentMarks.getYear(), studentMarks.getMonth(),
						studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getYear(),
						studentMarks.getMonth(), studentMarks.getYear(), studentMarks.getMonth() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		completeBookingList.addAll(bookingList1);

		if (bookingList2 != null && bookingList2.size() > 0) {
			completeBookingList.addAll(bookingList2);
		}

		for (ExamBookingTransactionBean b : completeBookingList) {
			b.setSem(eligibilityService.getProjectApplicableProgramSem(b.getProgram()));
			b.setCenterId("-1");
			b.setAmount("0");
		}

		return completeBookingList;
	}

	/*
	 * public ArrayList<OperationsRevenueBean>
	 * getSRRevenueAmountList(OperationsRevenueBean operationsRevenue) {
	 * jdbcTemplate = new JdbcTemplate(dataSource);
	 * ArrayList<OperationsRevenueBean>
	 * srRevenueList = new ArrayList<OperationsRevenueBean>(); OperationsRevenueBean
	 * revenueError = new OperationsRevenueBean(); try{ String sql ="";
	 * if("All".equalsIgnoreCase(operationsRevenue.getLc())){ sql =
	 * "select sum(amount) as amount from (select amount from portal.service_request where transtatus = 'Payment Successful' "
	 * + " and date(createdDate) >= ? and date(createdDate) <= ? " +
	 * " group by trackId) t1"; srRevenueList = (ArrayList<OperationsRevenueBean>)
	 * jdbcTemplate.query(sql, new Object[] { operationsRevenue.getStartDate(),
	 * operationsRevenue.getEndDate() },new BeanPropertyRowMapper(
	 * OperationsRevenueBean.class)); }else{ sql =
	 * "select sum(amount) as amount , lc " + " from " +
	 * " (select  amount , lc from portal.service_request sr, exam.students s, exam.centers c"
	 * + " where " + " transtatus = 'Payment Successful' and " +
	 * " s.sapid = sr.sapid and " + " c.centerCode = s.centerCode and " +
	 * " date(sr.createdDate) >= ? and " + " date(sr.createdDate) <= ? and " +
	 * " c.lc=? " + " group by trackId) t1;"; srRevenueList =
	 * (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, new Object[] {
	 * operationsRevenue.getStartDate(), operationsRevenue.getEndDate()
	 * ,operationsRevenue.getLc()},new BeanPropertyRowMapper(
	 * OperationsRevenueBean.class)); } if(!srRevenueList.isEmpty()) {
	 * for(OperationsRevenueBean revenue : srRevenueList){
	 * revenue.setRevenueSource("Service Request"); } }else{
	 * revenueError.setRevenueSource("Service Request");
	 * revenueError.setAmount(0.0); srRevenueList.add(revenueError); }
	 * }catch(Exception e){ //
	 * revenueError.setRevenueSource("Service Request");
	 * revenueError.setAmount(0.0); srRevenueList.add(revenueError);
	 * }
	 * 
	 * return srRevenueList; }
	 */

	public ArrayList<OperationsRevenueBean> getSRRevenueAmountList(OperationsRevenueBean operationsRevenue) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		double srRevenueAmount = 0.0;
		ArrayList<OperationsRevenueBean> srRevenueList = new ArrayList<OperationsRevenueBean>();
		try {
			String sql = "";
			ArrayList<Object> param = new ArrayList<Object>();
			sql = "select coalesce(sum(amount),0) as amount , serviceRequestType " + " from"
					+ " (select  amount,  sr.serviceRequestType  " + " from " + " portal.service_request sr"
					+ " inner join exam.students s on s.sapid = sr.sapid "
					+ " inner join exam.centers c on c.centerCode = s.centerCode " + " where"
					+ " transtatus = 'Payment Successful'";
			if (!operationsRevenue.getStartDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getStartDate())) {
				sql = sql + " and date(sr.createdDate) >=  ?";
				param.add(operationsRevenue.getStartDate());
			}
			if (!operationsRevenue.getEndDate().isEmpty() && !StringUtils.isBlank(operationsRevenue.getEndDate())) {
				sql = sql + " and date(sr.createdDate) <=  ?";
				param.add(operationsRevenue.getEndDate());
			}
			if (operationsRevenue.getLc_list().size() > 0 && !operationsRevenue.getLc_list().isEmpty()
					&& operationsRevenue.getLc_list() != null) {
				operationsRevenue.setLc(String.join("','", operationsRevenue.getLc_list()));
				sql = sql + " and c.lc in ('" + operationsRevenue.getLc() + "') ";
			}
			if (operationsRevenue.getIc_list().size() > 0 && !operationsRevenue.getIc_list().isEmpty()
					&& operationsRevenue.getIc_list() != null) {
				operationsRevenue.setIc(String.join("','", operationsRevenue.getIc_list()));
				sql = sql + " and c.centerName in ('" + operationsRevenue.getIc() + "') ";
			}
			if (!operationsRevenue.getPaymentOption().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getPaymentOption())) {
				sql = sql + " and sr.paymentOption = ? ";
				param.add(operationsRevenue.getPaymentOption());
			}
			if (!operationsRevenue.getProgramType().isEmpty()
					&& !StringUtils.isBlank(operationsRevenue.getProgramType())) {
				if ("PG".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + "    AND s.program not in ('MBA - X','MBA - WX')";
				}
				if ("MBA - WX".equalsIgnoreCase(operationsRevenue.getProgramType())
						|| "MBA - X".equalsIgnoreCase(operationsRevenue.getProgramType())) {
					sql = sql + " and s.program = ? ";
					param.add(operationsRevenue.getProgramType());
				}
			}
			sql = sql + " group by trackId) t1 group by serviceRequestType;";
			Object[] args = param.toArray();

			srRevenueList = (ArrayList<OperationsRevenueBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(OperationsRevenueBean.class));

		} catch (Exception e) {
			
		}

		return srRevenueList;
	}

	public ArrayList<String> getListOfLCNames() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> lclist = new ArrayList<String>();

		try {
			String sql = "Select distinct lc from exam.centers where active = 1 ";
			lclist = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
		}
		return lclist;
	}

	public ArrayList<String> getListOfICNames() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> iclist = new ArrayList<String>();

		try {
			String sql_ic = "Select distinct centerName from exam.centers where active = 1 ";

			iclist = (ArrayList<String>) jdbcTemplate.query(sql_ic, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
		}

		return iclist;
	}

	public ArrayList<String> getListOfPaymentOptions() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> paymentOptions = new ArrayList<String>();

		try {
			String sql_pay = "SELECT name  FROM portal.payment_options  where active = 'Y'";

			paymentOptions = (ArrayList<String>) jdbcTemplate.query(sql_pay, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
		}
		return paymentOptions;
	}

//	public ArrayList<TcsOnlineExamBean> getListofTcsOnlineExamBooking(StudentMarksBean studentBean) {
//
//		jdbcTemplate = new JdbcTemplate(dataSource);
//
//		ArrayList<TcsOnlineExamBean> tcsOnlineExamList = new ArrayList<TcsOnlineExamBean>();
//		String sql = "SELECT  " + "    CONCAT(s.sapid, 12, 2019, pss.sifySubjectCode) AS 'uniqueRequestId', "
//				+ "    eb.year AS 'examYear', " + "    eb.month AS 'examMonth', " + "    eb.sapid AS 'userId', "
//				+ "    eb.password AS 'password', " + "    pss.sifySubjectCode AS 'subjectId', "
//				+ "    pss.subject AS 'subject', " + "    s.firstName AS 'firstName', "
//				+ "    s.lastName AS 'lastName', " + "    s.program AS 'program', " + "    eb.examDate AS 'examDate', "
//				+ "    eb.examTime AS 'examTime', " + "    c.centerId AS 'centerId' " + "FROM "
//				+ "    exam.exambookings eb " + "        LEFT JOIN " + "    (SELECT  " + "        * " + "    FROM "
//				+ "        exam.students es " + "    WHERE " + "        es.sem = (SELECT  "
//				+ "                MAX(sem) " + "            FROM " + "                exam.students "
//				+ "            WHERE " + "                sapid = es.sapid)) s ON eb.sapid = s.sapid "
//				+ "        LEFT JOIN " + "    exam.examcenter c ON c.centerId = eb.centerId " + "        LEFT JOIN "
//				+ "    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId "
//				+ "        AND pss.sem = eb.sem " + "        AND eb.subject = pss.subject " + "        LEFT JOIN "
//				+ "    (SELECT  " + "        examcenterId, SUM(booked) AS count2 " + "    FROM "
//				+ "        exam.examcenter_slot_mapping "
//				+ "    GROUP BY examcenterId) esm ON esm.examcenterId = eb.centerId " + "WHERE "
//				+ "    eb.booked = 'Y' " + "        AND eb.subject <> 'Project'"
//				+ "		AND eb.year = ? AND eb.month = ? " + "        AND s.sapid NOT IN (SELECT DISTINCT "
//				+ "            c.sapid " + "        FROM " + "            exam.corporate_center_usermapping c "
//				+ "        WHERE " + "            year = 2019 AND month = 'Dec' "
//				+ "                AND (s.programStatus <> 'Program Suspension' "
//				+ "                OR s.programStatus IS NULL " + "                OR s.programStatus = '')) "
//				+ "GROUP BY eb.sapid , eb.subject "
//				+ "ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC; ";
//		try {
//			tcsOnlineExamList = (ArrayList<TcsOnlineExamBean>) jdbcTemplate.query(sql,
//					new Object[] { studentBean.getYear(), studentBean.getMonth() },
//					new BeanPropertyRowMapper(TcsOnlineExamBean.class));
//		} catch (Exception e) {
//			
//		}
//
//		return tcsOnlineExamList;
//
//	}
//	

	public ArrayList<TcsOnlineExamBean> getListofTcsOnlineExamBooking(StudentMarksBean studentBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<TcsOnlineExamBean> tcsOnlineExamList = new ArrayList<TcsOnlineExamBean>();
		String sql = "SELECT  " + 
//				"    CONCAT(s.sapid, 12, 2019, pss.sifySubjectCode) AS 'uniqueRequestId', " + 
				"					     @concatMonth:=(CASE   " + 
				"				        WHEN eb.month = 'Apr' THEN 4   " + 
				"				        WHEN eb.month = 'Jun' THEN 6   " + 
				"				        WHEN eb.month = 'Sep' THEN 9   " + 
				"				        WHEN eb.month = 'Dec' THEN 12  " + 
				"				    END) AS concatMonth," + 
				"				    CONCAT(s.sapid," + 
				"				            @concatMonth," + 
				"				            eb.year,   " + 
				"				            pss.sifySubjectCode) AS 'uniqueRequestId'," + 
				"    eb.year AS 'examYear', " + 
				"    eb.month AS 'examMonth', " + 
				"    eb.sapid AS 'userId', " + 
				"    eb.password AS 'password', " + 
				"    pss.sifySubjectCode AS 'subjectId', " + 
				"    pss.subject AS 'subject', " + 
				"    s.firstName AS 'firstName', " + 
				"    s.lastName AS 'lastName', " + 
				"    s.program AS 'program', " + 
				"    eb.examDate AS 'examDate', " + 
				"    eb.examTime AS 'examTime', " + 
				"    c.centerId AS 'centerId' " + 
				"FROM " + 
				"    exam.exambookings eb " + 
				"        LEFT JOIN " + 
				"    (SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        exam.students es " + 
				"    WHERE " + 
				"        es.sem = (SELECT  " + 
				"                MAX(sem) " + 
				"            FROM " + 
				"                exam.students " + 
				"            WHERE " + 
				"                sapid = es.sapid)) s ON eb.sapid = s.sapid " + 
				"        LEFT JOIN " + 
				"    exam.examcenter c ON c.centerId = eb.centerId " + 
				"        LEFT JOIN " + 
				"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        AND pss.sem = eb.sem " + 
				"        AND eb.subject = pss.subject " + 
				"        LEFT JOIN " + 
				"    (SELECT  " + 
				"        examcenterId, SUM(booked) AS count2 " + 
				"    FROM " + 
				"        exam.examcenter_slot_mapping " + 
				"    GROUP BY examcenterId) esm ON esm.examcenterId = eb.centerId " + 
				"WHERE " + 
				"    eb.booked = 'Y' " + 
				"        AND eb.subject NOT IN ('Project', 'Module 4 - Project')"
				+ "		AND eb.year = ? AND eb.month = ? " + 
				"        AND s.sapid NOT IN (SELECT DISTINCT " + 
				"            c.sapid " + 
				"        FROM " + 
				"            exam.corporate_center_usermapping c " + 
				"        WHERE " + 
				"            year = ? AND month = ? " + 
				"                AND (s.programStatus <> 'Program Suspension' " + 
				"                OR s.programStatus IS NULL " + 
				"                OR s.programStatus = '')) " + 
				"GROUP BY eb.sapid , eb.subject " + 
				"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC; ";	
		try {
			tcsOnlineExamList = (ArrayList<TcsOnlineExamBean>) 
					jdbcTemplate.query(sql, new Object[]{studentBean.getYear(), studentBean.getMonth(),studentBean.getYear(), studentBean.getMonth()}, new BeanPropertyRowMapper(TcsOnlineExamBean.class));
		}catch(Exception e) {
			
		}
		return tcsOnlineExamList;
		
	}
	public ArrayList<StudentExamBean> getPendingSubjectsForLateralEntries(String sapIds) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean> pendingSubjectsForLateralEntries = new ArrayList<StudentExamBean>();
		String sql = "SELECT  " + "    current_student.*, " + "    s.program AS 'oldProgram', "
				+ "    s.PrgmStructApplicable AS 'previousPrgmStructApplicable' " + "FROM " + "    (SELECT  "
				+ "        s.sapid, " + "            s.firstName, " + "            s.lastName, "
				+ "            pss.subject, " + "            pss.sem, " + "            s.previousStudentId, "
				+ "            s.program, " + "            s.PrgmStructApplicable " + "    FROM "
				+ "        exam.students s " + "    INNER JOIN (SELECT  " + "        * " + "    FROM "
				+ "        exam.registration r " + "    WHERE " + "        r.sem = (SELECT  "
				+ "                MAX(sem) " + "            FROM " + "                exam.registration er "
				+ "            WHERE " + "                er.sapid = r.sapid)) r ON r.sapid = s.sapid "
				+ "    INNER JOIN exam.program_sem_subject pss ON r.consumerProgramStructureId = pss.consumerProgramStructureId "
				+ "        AND pss.sem < r.sem " + "    WHERE " + "        s.previousStudentId IN (" + sapIds
				+ ")) AS current_student " + "        LEFT JOIN " + "    (SELECT  " + "        s.sapid, "
				+ "            s.firstName, " + "            s.lastName, " + "            pss.subject, "
				+ "            pss.sem, " + "            pss.consumerProgramStructureId, " + "            s.program, "
				+ "            s.PrgmStructApplicable " + "    FROM " + "        exam.students s "
				+ "    INNER JOIN (SELECT  " + "        * " + "    FROM " + "        exam.registration r "
				+ "    WHERE " + "        r.sem = (SELECT  " + "                MAX(sem) " + "            FROM "
				+ "                exam.registration " + "            WHERE "
				+ "                sapid = r.sapid)) r ON r.sapid = s.sapid "
				+ "    INNER JOIN exam.program_sem_subject pss ON r.consumerProgramStructureId = pss.consumerProgramStructureId "
				+ "        AND pss.sem <= r.sem " + "    WHERE " + "        s.sapid IN (" + sapIds
				+ ")) past_student ON current_student.subject = past_student.subject "
				+ "        AND current_student.previousStudentId = past_student.sapid " + "        INNER JOIN "
				+ "    exam.students s ON s.sapid = current_student.previousStudentId " + "WHERE "
				+ "    past_student.sapid IS NULL; " + "";
		try {
			pendingSubjectsForLateralEntries = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(StudentExamBean.class));
		} catch (Exception e) {
			
		}
		return pendingSubjectsForLateralEntries;
	}
	
	public List<PassFailExamBean> getMBAXProgramCompletedReport(int numberOfSubjectsToPass, String authorizedCenterCodes,String PrgmStructApplicable,String year,String month){
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "select ps.*,'MBA - X' as program,CONCAT(s.firstName,s.lastName) as name from exam.mbax_passfail ps inner join lti.student_subject_config ssc ON ssc.id = ps.timeboundId inner join exam.students s ON s.sapid = ps.sapid where s.program = 'MBA - X' and s.PrgmStructApplicable = ? and ps.isPass = 'Y' and ps.isResultLive='Y' and (s.programCleared = 'N' or s.programCleared IS NULL)  group by ps.sapid having count(ps.timeboundId) = ? and ps.sapid not in (select sapid from exam.mbax_passfail where isPass = 'N') and ps.sapid in (select ps.sapid from exam.mbax_passfail ps inner join lti.student_subject_config ssc ON ssc.id = ps.timeboundId where ssc.examYear=? and ssc.examMonth = ?) ";
				if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
					sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				}
				sql = sql +" limit 40";
				return jdbcTemplate.query(sql, new Object[] {PrgmStructApplicable,numberOfSubjectsToPass,year,month},new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
			}
			catch (Exception e) {
				// TODO: handle exception
				return new ArrayList<PassFailExamBean>();
			}
	}
	
	public List<PassFailExamBean> getMBAWXProgramCompletedReport(int numberOfSubjectsToPass, String authorizedCenterCodes, String PrgmStructApplicable,String year,String month){
			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "select ps.*,'MBA - WX' as program,CONCAT(s.firstName,s.lastName) as name from exam.mba_passfail ps inner join lti.student_subject_config ssc ON ssc.id = ps.timeboundId inner join exam.students s ON s.sapid = ps.sapid where s.program = 'MBA - WX' and s.PrgmStructApplicable = ? and ps.isPass = 'Y' and ps.isResultLive='Y' and (s.programCleared = 'N' or s.programCleared IS NULL)  group by ps.sapid having count(ps.timeboundId) = ? and ps.sapid not in (select sapid from exam.mba_passfail where isPass = 'N') and ps.sapid in (select ps.sapid from exam.mba_passfail ps inner join lti.student_subject_config ssc ON ssc.id = ps.timeboundId where ssc.examYear=? and ssc.examMonth = ?) ";
				if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
					sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				} 
				return jdbcTemplate.query(sql, new Object[] {PrgmStructApplicable,numberOfSubjectsToPass,year,month},new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
			}
			catch (Exception e) {
				// TODO: handle exception
				return new ArrayList<PassFailExamBean>();
			}
	}
	
	public StudentExamBean getStudentBySapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM `exam`.`students` WHERE `sapid` = ?";
		return jdbcTemplate.queryForObject(
			sql,
			new Object[] { sapid },
			new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
		);
	}
	
	public List<AssignmentStatusBean> getPendingExambookingStudentSubjectListFromTemp(String year, String month,
			String authorizedCenterCodes) {

		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		authorizedCenterCodes = authorizedCenterCodes.replaceAll("\'","");
		List<String> authorizedCenterCodesList = Stream.of(authorizedCenterCodes.split(",", -1))
				  .collect(Collectors.toList());
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		 queryParams.addValue("year", year);
		 queryParams.addValue("month", month);
		String sql = "SELECT " + 
				"    s.*, conf.sem, conf.subject " + 
				"FROM " + 
				"    exam.students s " + 
				"    INNER JOIN " + 
				"    exam.student_cycle_subject_config conf on conf.sapid = s.sapid  " + 
				"WHERE " + 
				"    STR_TO_DATE(CONCAT('30-', " + 
				"                    s.validityEndMonth, " + 
				"                    '-', " + 
				"                    s.validityEndYear), " + 
				"            '%d-%b-%Y') >= CURDATE() " + 
				"        AND conf.year = :year and conf.month = :month "; 
		if (authorizedCenterCodesList != null && authorizedCenterCodesList.size() > 0 ) {
			sql = sql + " and s.centerCode in ( :authorizedCenterCodes ) ";
			queryParams.addValue("authorizedCenterCodes", authorizedCenterCodesList);
		}

		List<AssignmentStatusBean> studentsList = namedJdbcTemplate.query(sql, queryParams,
				new BeanPropertyRowMapper<AssignmentStatusBean>(AssignmentStatusBean.class));
		return studentsList;
	}
	
	public List<AssignmentStatusBean> getProjectExambookingStudentSubjectList(String year, String month,
			String authorizedCenterCodes) {
		List<AssignmentStatusBean> studentsList =new ArrayList<>();
		try {	
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			authorizedCenterCodes = authorizedCenterCodes.replaceAll("\'","");
			List<String> authorizedCenterCodesList = Stream.of(authorizedCenterCodes.split(",", -1))
					  .collect(Collectors.toList());
			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			 queryParams.addValue("year", year);
			 queryParams.addValue("month", month);
			String sql = "SELECT " + 
					"    s.*, r.sem, ps.subject " + 
					"FROM " + 
					"    exam.students s " + 
					"    inner join " + 
					"    exam.program_subject ps on  s.PrgmStructApplicable = ps.prgmStructApplicable " + 
					"    inner join " + 
					"    exam.registration r on s.sapid = r.sapid " + 
					"        AND r.sem = ps.sem " + 
					"        AND r.program = ps.program " + 
					"        AND r.sem = ps.sem " + 
					"    left join " + 
					"    exam.passfail pf on pf.sapid = s.sapid and pf.subject = ps.subject  " + 
					"    inner join " + 
					"    exam.examorder eo on  s.validityEndMonth = eo.month " + 
					"        AND s.validityEndYear = eo.year " + 
					"WHERE " + 
					"    STR_TO_DATE(CONCAT('30-', " + 
					"                    s.validityEndMonth, " + 
					"                    '-', " + 
					"                    s.validityEndYear), " + 
					"            '%d-%b-%Y') >= CURDATE() " + 
					"        AND STR_TO_DATE(CONCAT(r.year, '-', r.month, '-01'), " + 
					"            '%Y-%b-%d') <= DATE_ADD(STR_TO_DATE(CONCAT(:year, '-', :month, '-01'), " + 
					"                '%Y-%b-%d'), " + 
					"        INTERVAL - 5 MONTH) " + 
					"        And (pf.isPass <> 'Y' or  pf.sapid is null) " + 
					"        AND eo.order >= (SELECT  " + 
					"            MAX(examorder.order) " + 
					"        FROM " + 
					"            exam.examorder " + 
					"        WHERE " + 
					"            acadSessionLive = 'Y') " + 
					"        AND ps.subject  IN ('Project','Module 4 - Project' ) ";
			if (authorizedCenterCodesList != null && authorizedCenterCodesList.size() > 0 ) {
				sql = sql + " and s.centerCode in ( :authorizedCenterCodes ) ";
				queryParams.addValue("authorizedCenterCodes", authorizedCenterCodesList);
			}
	
			studentsList = namedJdbcTemplate.query(sql, queryParams,
					new BeanPropertyRowMapper<AssignmentStatusBean>(AssignmentStatusBean.class));
			return studentsList;
		}catch (Exception e) {
			// TODO: handle exception
			return studentsList;
		}
	}
	
	public ArrayList<UGConsentExcelReportBean> getugcStudentDetailedList(String program,String authorizedCenterCodes){
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("	SELECT s.sapid,concat(firstName,' ',lastName) AS studentName,emailId,mobile,dob,program,programStatus,consent_option,ugc.createdDate AS dateOfSubmission,centerCode from exam.students s	"
											+ "	INNER JOIN	"
											+ "	portal.ugc_consentform ugc ON s.sapid = ugc.sapid	WHERE  s.program IN ("+program+") " );
		
		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) 
			sql.append(" and s.centerCode in (" + authorizedCenterCodes + ") ");
		
		ArrayList<UGConsentExcelReportBean> ugsubmittedList = (ArrayList<UGConsentExcelReportBean>) nameJdbcTemplate.query(sql.toString(),
					new BeanPropertyRowMapper(UGConsentExcelReportBean.class));
		
		return ugsubmittedList;
	}
	
	public ArrayList<String> getugcStudentPendingList(ArrayList<String> sapids,String program,String sem,String month,String year){
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT r.sapid from exam.registration r WHERE  r.sem =:sem and r.month =:month and r.year =:year and sapid not in (:sapids) AND r.program IN ("+program+") " );
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapids",sapids);
		parameters.addValue("sem",sem);
		parameters.addValue("month",month);
		parameters.addValue("year",year);
		
		ArrayList<String> ugsubmittedList = (ArrayList<String>) nameJdbcTemplate.query(sql.toString(), parameters,new SingleColumnRowMapper(String.class));
		return ugsubmittedList;
	}
	
	public ArrayList<String> getugcStudents(){
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT sapid from portal.ugc_consentform	");
		ArrayList<String> ugsubmittedList = (ArrayList<String>) nameJdbcTemplate.query(sql.toString(),new SingleColumnRowMapper(String.class));
		return ugsubmittedList;
	}
	
	public ArrayList<UGConsentExcelReportBean> getStudentsDetails(ArrayList<String> sapids,String authorizedCenterCodes){
		
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("select s.sapid,concat(firstName,' ',lastName) as studentName,emailId,mobile,dob,program,centerCode  from exam.students s WHERE s.sapid IN (:sapids) AND (s.programStatus IS NULL OR s.programStatus = '') ");
		
		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) 
			sql.append(" and s.centerCode in (" + authorizedCenterCodes + ") ");
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapids",sapids);
	
		ArrayList<UGConsentExcelReportBean> ugsubmittedList = (ArrayList<UGConsentExcelReportBean>) nameJdbcTemplate.query(sql.toString(), parameters,new BeanPropertyRowMapper(UGConsentExcelReportBean.class));
		return ugsubmittedList;
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingCancelBean> getCancelledExamBookings(String year, String month)
	{
		List<ExamBookingCancelBean> canceledBookingList = new ArrayList<ExamBookingCancelBean>();
		List<ExamBookingCancelBean> canceledBookingHistoryList = new ArrayList<ExamBookingCancelBean>();
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select year,month,subject,sapid,releaseReason,program,sem,respAmount,lastModifiedDate from exam.exambookings where year=? and month=? and ((booked='CL' and tranStatus='Cancellation Without Refund') or (booked='N' and tranStatus='Cancellation With Refund'))  ";
		canceledBookingList=jdbcTemplate.query(sql, new Object[] {year,month},new BeanPropertyRowMapper<ExamBookingCancelBean>(ExamBookingCancelBean.class));
		
		sql = " select year,month,subject,sapid,releaseReason,program,sem,respAmount,lastModifiedDate  from exam.exambookings_history where year=? and month=? and ((booked='CL' and tranStatus='Cancellation Without Refund') or (booked='N' and tranStatus='Cancellation With Refund'))";
		canceledBookingHistoryList=jdbcTemplate.query(sql, new Object[] {year,month},new BeanPropertyRowMapper<ExamBookingCancelBean>(ExamBookingCancelBean.class));
		
		canceledBookingList.addAll(canceledBookingHistoryList);
		
		return canceledBookingList;
	}
	
	public HashMap<String,StudentExamBean> getLateralStudentsByProgram(String program,String authorizedCenterCodes,String sapid,boolean isLateral,String programStructure){
	
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		StringBuffer sql = new StringBuffer("SELECT s.* FROM exam.students s WHERE program = ? AND prgmstructapplicable = ? ");
		
		if(isLateral)
			sql.append(" AND isLateral = 'Y' ");
		
		if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) 
			sql.append(" AND s.centerCode in (" + authorizedCenterCodes + ") ");
	
		HashMap<String,StudentExamBean>lateralStudentList = jdbcTemplate.query(sql.toString(),new Object[] {program,programStructure}, new ResultSetExtractor<HashMap>(){
			
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,StudentExamBean> mapRet= new HashMap<String,StudentExamBean>();
		        while(rs.next()){
		        	StudentExamBean bean = new StudentExamBean();
		        	bean.setProgram(rs.getString("program"));
		        	bean.setEnrollmentMonth(rs.getString("enrollmentMonth"));
		        	bean.setEnrollmentYear(rs.getString("enrollmentYear"));
		        	bean.setGender(rs.getString("gender"));
		        	bean.setPreviousStudentId(rs.getString("previousStudentId"));
		        	bean.setPrgmStructApplicable(rs.getString("PrgmStructApplicable"));
		        	bean.setProgramChanged(rs.getString("programChanged"));
		        	bean.setIsLateral(rs.getString("isLateral"));
		        	bean.setSapid(rs.getString("sapid"));
		        	bean.setConsumerProgramStructureId(rs.getString("ConsumerProgramStructureId"));
		            mapRet.put(rs.getString("sapid"),bean);
		        }
		        return mapRet;
		    }
		});

		
		return lateralStudentList;
	}
	
	public ArrayList<PassFailExamBean> getPassSubjectDetails(List<String> sapid,String month,String year){
		
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT p.*,GROUP_CONCAT('\"',subject,'\"') AS subject, round(avg(p.total),2) as passpercentage,	" + 
				"			DATE_FORMAT(max(str_to_date(concat('01,',writtenmonth,',',writtenyear), \"%d,%b,%Y\")) , '%b') as completionMonth,	" + 
				"			DATE_FORMAT(max(str_to_date(concat('01,',writtenmonth,',',writtenyear), \"%d,%b,%Y\")) , '%Y') as completionYear	" + 
				"			FROM exam.passfail p where sapid IN (:sapid) and isPass = 'Y'	" + 
				"			AND ( STR_TO_DATE(CONCAT('01-',resultProcessedMonth,'-',resultProcessedYear), '%d-%b-%Y')"	+
				"			 <= STR_TO_DATE(CONCAT('01-',:month,'-',:year), '%d-%b-%Y'))	group by sapid 	"
				+ "			HAVING 	" + 
				"    COUNT(CASE WHEN p.resultProcessedMonth =:month AND p.resultProcessedYear =:year THEN 1 END) > 0 ");
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapid",sapid);
		parameters.addValue("month",month);
		parameters.addValue("year",year);
	
		ArrayList<PassFailExamBean> lateralStudentPassSubjectsList = (ArrayList<PassFailExamBean>) nameJdbcTemplate.query(sql.toString(), parameters,new BeanPropertyRowMapper(PassFailExamBean.class));
		return lateralStudentPassSubjectsList;
	}	
	
	public List<ExamBookingCancelBean> getCancelStudentBySapid(List<String> sapids){
		
		NamedParameterJdbcTemplate nameJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "" +
				"SELECT  " + 
				"    sapid, " + 
				"    firstName, " + 
				"    lastName, " + 
				"    emailId, " + 
				"    mobile, " + 
				"    centerCode, " + 
				"    centerName, " + 
				"    enrollmentYear, " + 
				"    enrollmentMonth, " +
				"    validityEndYear, " + 
				"    validityEndMonth " + 
				"FROM " + 
				"    exam.students " + 
				"WHERE " + 
				"    sapid IN (:sapids)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapids",sapids);
	
		List<ExamBookingCancelBean> cancelStudentList = nameJdbcTemplate.query(sql, parameters,new BeanPropertyRowMapper<ExamBookingCancelBean>(ExamBookingCancelBean.class));
		return cancelStudentList;
	}
}