package com.nmims.daos;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.log.SysoCounter;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentQuestionMarksBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.PCPBookingTransactionExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.WebCopycaseBean;
import com.nmims.controllers.AssignmentController; 
import com.nmims.controllers.AssignmentPaymentController;
import com.nmims.helpers.PaginationHelper;
import com.nmims.beans.ConsumerType;

@Repository("asignmentsDAO")
public class AssignmentsDAO  extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private HashMap<String, FacultyExamBean> facultyMap = new HashMap<>();
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful";
	private final String TRANSACTION_FAILED = "Transaction Failed";
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private final String BAJAJ_PROGRAM = "ACBM";
	private static final Logger logger = LoggerFactory.getLogger(AssignmentsDAO.class);

	@Autowired
	@Qualifier("adminReports")
	private DataSource adminReports;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${CURRENT_EXAM_MONTH}")
	private String CURRENT_EXAM_MONTH;
	
	@Value("${CURRENT_EXAM_YEAR}")
	private String CURRENT_EXAM_YEAR;

	@Autowired
	StudentMarksDAO marksDao;

	@Autowired
	FacultyDAO facultyDAO;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	@Transactional(readOnly = true)
	public int checkHasAssignment(String consumerProgramStructureId) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT count(hasAssignment) as count FROM exam.program_sem_subject where consumerProgramStructureId=? and hasAssignment='Y';";
			return (int) jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(Integer.class));
		}
		catch (Exception e) {
			
			return 0;
		}

	}
	@Transactional(readOnly = false)
	public void saveAssignmentDetails(AssignmentFileBean bean, String year,
			String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO exam.assignments (year, month, subject, startDate, endDate, instructions, "
				+ " filePath, questionFilePreviewPath, createdBy, createdDate, lastModifiedBy, lastModifiedDate, program, consumerProgramStructureId) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?)"
				+ " on duplicate key update "
				+ "	    startDate = ?,"
				+ "	    endDate = ?,"
				+ "	    instructions = ?,"
				+ "	    filePath = ?,"
				+ "	    questionFilePreviewPath = ?,"
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate(), "
				+ "	    consumerProgramStructureId = ? ";

		String subject = bean.getSubject();
		
		String consumerProgramStructureId = bean.getConsumerProgramStructureId();
		
		String startDate = bean.getStartDate();
		String endDate = bean.getEndDate();
		String instructions = bean.getInstructions();
		String filePath = bean.getFilePath();
		String questionFilePreviewPath = bean.getQuestionFilePreviewPath();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String program = bean.getProgram();
		jdbcTemplate.update(sql, new Object[] { year, month, subject,
				startDate, endDate, instructions, filePath,
				questionFilePreviewPath, createdBy, lastModifiedBy, "All", consumerProgramStructureId,

				startDate, endDate, instructions, filePath,
				questionFilePreviewPath, lastModifiedBy,consumerProgramStructureId });

	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentFilesPage(int pageNo,
			int pageSize, AssignmentFileBean searchBean,String searchOption) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "select a.*,p.code as program,p_s.program_structure as programStructure,"
				+ "c_t.name as consumerType";
	
		String countSql = "";

		if(searchOption.equalsIgnoreCase("distinct")) {
			 countSql = "SELECT count(distinct concat(filePath,startDate,endDate)) FROM exam.assignments where 1 = 1 ";
			 sql = sql + " ,count(a.filePath) as count,concat(a.filePath,a.startDate,a.endDate) as distinctFile,group_concat(distinct a.consumerProgramStructureId) consumerProgramStructureId ";
		}else {
			 countSql = "SELECT count(*) FROM exam.assignments where 1 = 1 ";
		}
		sql = sql + " from exam.assignments as a "
				+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = a.consumerProgramStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId where 1 = 1 ";
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and a.year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and a.month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		if (searchBean.getConsumerProgramStructureId() != null
				&& !("".equals(searchBean.getConsumerProgramStructureId()))) {
			sql = sql + " and a.consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			countSql = countSql + " and consumerProgramStructureId in ("+searchBean.getConsumerProgramStructureId() +") ";
			
		}
		
		if(searchOption.equalsIgnoreCase("distinct")) {
			sql = sql + " group by distinctFile ";	
			
		}

		sql = sql + " order by subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public ArrayList<AssignmentFileBean> getCommonGroupProgramList(AssignmentFileBean assignmentFiles) {
		try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select a.*,p.code as program,p_s.program_structure as programStructure,c_t.name as consumerType  from exam.assignments as a " + 
				"left join exam.consumer_program_structure as c_p_s on c_p_s.id = a.consumerProgramStructureId " + 
				"left join exam.program as p on p.id = c_p_s.programId " + 
				"left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId " + 
				"left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId " + 
				"where 1 = 1  and a.year = ?  and a.month = ?  and filePath= ? and consumerProgramStructureId in ("+ assignmentFiles.getConsumerProgramStructureId() +")";
		return (ArrayList<AssignmentFileBean>) jdbcTemplate.query(sql, new Object[] {assignmentFiles.getYear(),assignmentFiles.getMonth(),assignmentFiles.getFilePath()},
				new BeanPropertyRowMapper(AssignmentFileBean.class));
		}
		catch(Exception e) {
			
			return null;
		}
		
	}
	@Transactional(readOnly = false)
	public int deleteAssignmentRecordByFilter(AssignmentFileBean assignmentFiles) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "delete from exam.assignments where consumerProgramStructureId=? and subject=? and year=? and month=?;";
			int row = jdbcTemplate.update(sql, new Object[] {
					assignmentFiles.getConsumerProgramStructureId(),
					assignmentFiles.getSubject(),
					assignmentFiles.getYear(),
					assignmentFiles.getMonth()
			});
			return row;
		}
		catch(Exception e) {
			
			return 0;
		}
		
	}
	@Transactional(readOnly = false)
	public int deleteCommongAssignmentRecordByFilter(AssignmentFileBean assignmentFiles) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "delete from exam.assignments where consumerProgramStructureId IN (" + assignmentFiles.getConsumerProgramStructureId() + ") and filePath=? and year=? and month=?;";
			int row = jdbcTemplate.update(sql, new Object[] {
					assignmentFiles.getFilePath(),
					assignmentFiles.getYear(),
					assignmentFiles.getMonth()
			});
			return row;
		}
		catch(Exception e) {
			
			return 0;
		}
		
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findById(AssignmentFileBean assignmentFile,
			StudentExamBean student) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		/**
		 * Commented by sagar,
		 * Reason: Added new ConsumerProgramStructureId for filter
		 * */
		/*String sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
				+ " subject = ? and a.year = eo.year and a.month = eo.month and"
				+ " eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";

		
		if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";// Fetch
																	// assignment
																	// set for
																	// Bajaj
		} else {
			sql = sql + " and a.program = 'All' "; // Fetch assignments for
													// other students
		}

		assignmentFile = (AssignmentFileBean) jdbcTemplate.queryForObject(sql,
				new Object[] { assignmentFile.getSubject() },
				new BeanPropertyRowMapper(AssignmentFileBean.class));*/
		
		/**
		 * Get Assignment query added based on assignment_live_setting table. 
		 * */
		/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where subject = ? and "
				+ "a.month = eo.month and a.year = eo.year and eo.order = "
				+ "( select max(examorder.order) from exam.examorder where assignmentLive = 'Y' ) "
				+ "and a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();*/
		String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "
				+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
				+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
				+ "a.subject =? and a.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" and a_l_s.liveType = 'Regular'";
		//ArrayList<AssignmentFileBean> assignmentsFile1 = new ArrayList<AssignmentFileBean>();
		assignmentFile = (AssignmentFileBean) jdbcTemplate.queryForObject(sql, new Object[] {assignmentFile.getSubject()},
				new BeanPropertyRowMapper(AssignmentFileBean.class)); 
		//assignmentFile = assignmentsFile1.get(0);
		String startDate = assignmentFile.getStartDate();
		String endDate = assignmentFile.getEndDate();
		if (startDate != null) {
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if (endDate != null) {
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}

	@SuppressWarnings("unchecked")
	public AssignmentFileBean findAssignment(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM exam.assignments  a where "
				+ " subject = ? and a.year = ? and a.month = ? and a.consumerProgramStructureId in ("+ assignmentFile.getConsumerProgramStructureId() +")";

		ArrayList<AssignmentFileBean> assignmentFiles = (ArrayList<AssignmentFileBean>) jdbcTemplate.query(
				sql,
				new Object[] { assignmentFile.getSubject(),
						assignmentFile.getYear(), assignmentFile.getMonth()
						 },
				new BeanPropertyRowMapper(AssignmentFileBean.class));
		String startDate = assignmentFiles.get(0).getStartDate();
		String endDate = assignmentFiles.get(0).getEndDate();
		String tmp_consumerProgramStructureId = assignmentFile.getConsumerProgramStructureId();
		assignmentFile = assignmentFiles.get(0);
		if(assignmentFiles.size() > 1) {
			assignmentFile.setConsumerProgramStructureId(tmp_consumerProgramStructureId);
		}
		if (startDate != null) {
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if (endDate != null) {
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findResitAssignmentById(
			AssignmentFileBean assignmentFile, StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/**
		 * Commented by sagar,
		 * Reason: Added new ConsumerProgramStructureId for filter
		 * */
		/*String sql = "";
		
		// Bajaj Student not allowed more than one resit attempt//
		if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
					+ " subject = ? and a.year = eo.year and a.month = eo.month and"
					+ " eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' and (month = 'Apr' or month = 'Sep')) and a.program = '"
					+ BAJAJ_PROGRAM + "' ";
		} else {
			sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
					+ " subject = ? and a.year = eo.year and a.month = eo.month and"
					+ " eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') and a.program = 'All' ";

		}*/
		/**
		 * Get Resit Assignment query added based on assignment_live_setting table. 
		 * */
		/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where "
				+ "subject = ? and a.month = eo.month and a.year = eo.year and eo.order = "
				+ "( select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' ) and "
				+ "a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();*/
//		gsk changing query for program change issue
//		String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "
//				+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
//				+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
//				+ "a.subject =? and a.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" and a_l_s.liveType = 'Resit'";
//		comment end
		List<String> consumerProgramStructureIds = getConsumerProgStructForOldProgramsFromRegistration(student.getSapid());
		if(!consumerProgramStructureIds.contains(student.getConsumerProgramStructureId())) {
			consumerProgramStructureIds.add(student.getConsumerProgramStructureId());
		}	
		String cpsiInString = consumerProgramStructureIds.toString();
		String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);
		String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "
				+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
				+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
				+ "a.subject =? and a.consumerProgramStructureId in ("+ cpsiCommaSeparated +") and a_l_s.liveType = 'Resit' group by a.subject";
		
		//AssignmentFileBean assignmentsFile1 = new ArrayList<AssignmentFileBean>();
		assignmentFile = (AssignmentFileBean) jdbcTemplate.queryForObject(sql, new Object[] {assignmentFile.getSubject()},
				new BeanPropertyRowMapper(AssignmentFileBean.class));
		//assignmentFile = assignmentsFile1.get(0);
		/*assignmentFile = (AssignmentFileBean) jdbcTemplate.queryForObject(sql,
				new Object[] { assignmentFile.getSubject() },
				new BeanPropertyRowMapper(AssignmentFileBean.class));*/
		String startDate = assignmentFile.getStartDate();
		String endDate = assignmentFile.getEndDate();
		if (startDate != null) {
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if (endDate != null) {
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}

	@Transactional(readOnly = true)
	public StudentExamBean getStudentRegistrationData(String sapId) {
		StudentExamBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			/*String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";*/
			
			String sql = "select * from exam.registration r where r.sapid = ? and  r.month = ? "
					+ " and r.year = ?  ";
			studentRegistrationData = (StudentExamBean) jdbcTemplate.queryForObject(sql, new Object[] 
					{ sapId , getLiveAssignmentMonth(), getLiveAssignmentYear()},
							new BeanPropertyRowMapper(StudentExamBean.class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}
	@Transactional(readOnly = true)
	public StudentExamBean getDiageoStudentRegistrationData(String sapId) {
		StudentExamBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			/*String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";*/
			
			String sql = "select * from exam.registration r where r.sapid = ? and  r.month = ? "
					+ " and r.year = ?  and r.sem = (select max(er.sem) from exam.registration er where er.sapid=?)";
			studentRegistrationData = (StudentExamBean) jdbcTemplate.queryForObject(sql, new Object[] 
					{ sapId , getLiveAssignmentMonth(), getLiveAssignmentYear(), sapId},
							new BeanPropertyRowMapper(StudentExamBean.class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	} 
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignmentsForSubjects(
			ArrayList<String> subjects, StudentExamBean student) {

		List<AssignmentFileBean> assignmentFiles = null;
		try {

			String subjectCommaSeparated = "";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			jdbcTemplate = new JdbcTemplate(dataSource);
			/**
			 * Commented by sagar,
			 * Reason: Added new ConsumerProgramStructureId for filter
			 * */
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject in ("
					+ subjectCommaSeparated
					+ ") and a.startDate <= sysdate() ";

			if (BAJAJ_PROGRAM.equals(student.getProgram())) {
				sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";// Fetch
																		// assignment
																		// set
																		// for
																		// Bajaj
			} else {
				sql = sql + " and a.program = 'All' "; // Fetch assignments for
														// other students
			}

			assignmentFiles = jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(AssignmentFileBean.class));*/
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where "
					+ "a.month = eo.month and a.year = eo.year and eo.order = "
					+ "( select max(examorder.order) from exam.examorder where assignmentLive = 'Y' ) and "
					+ "subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
							+ "a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();*/
			/**
			 * Get Assignment query added based on assignment_live_setting table. 
			 * */ 
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s "
					+ " where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ " a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " a.subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
					+ " a.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" and a_l_s.liveType = 'Regular';";

			assignmentFiles = jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(AssignmentFileBean.class));
		} catch (Exception e) {
			
		}
		return assignmentFiles;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getResitAssignmentsForSubjects(
			ArrayList<String> subjects, StudentExamBean student) {

		List<AssignmentFileBean> assignmentFiles = null;
		try {

			String subjectCommaSeparated = "";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			jdbcTemplate = new JdbcTemplate(dataSource);
			/**
			 * Commented by sagar,
			 * Reason: Added new ConsumerProgramStructureId for filter
			 * */
			/*String sql = "";

			if (BAJAJ_PROGRAM.equals(student.getProgram())) {
				// For ACBM take live flag of Apr or Sep
				// Bajaj Student not allowed more than one resit attempt//
				sql = sql
						+ "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
						+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' and (month = 'Apr' or month = 'Sep')) "
						+ " and subject in (" + subjectCommaSeparated
						+ ") and a.startDate <= sysdate() ";

				sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";// Fetch
																		// assignment
																		// set
																		// for
																		// Bajaj
			} else {
				// For other program take live flag which is latest
				sql = sql
						+ "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
						+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
						+ " and subject in (" + subjectCommaSeparated
						+ ") and a.startDate <= sysdate() ";

				sql = sql + " and a.program = 'All' "; // Fetch assignments for
														// other students
			}

			assignmentFiles = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(AssignmentFileBean.class));*/
			/**
			 * Get Resit Assignment query added based on assignment_live_setting table. 
			 * */
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where "
					+ "a.month = eo.month and a.year = eo.year and eo.order = "
					+ "( select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' ) and "
					+ "subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
							+ "a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();*/
			
			
//			gsk changing og query code for handling program change issue---
//			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s "
//					+ " where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
//					+ " a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
//					+ " a.subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
//					+ " a.consumerProgramStructureId = "+ student.getConsumerProgramStructureId() +" and a_l_s.liveType = 'Resit';";
//			assignmentFiles = jdbcTemplate.query(sql, new Object[] {},
//					new BeanPropertyRowMapper(AssignmentFileBean.class));
//		comment end
			List<String> consumerProgramStructureIds = getConsumerProgStructForOldProgramsFromRegistration(student.getSapid());
			if(!consumerProgramStructureIds.contains(student.getConsumerProgramStructureId())) {
				consumerProgramStructureIds.add(student.getConsumerProgramStructureId());
			}	
			String cpsiInString = consumerProgramStructureIds.toString();
			String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);
			
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s "
					+ " where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ " a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " a.subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
					+ " a.consumerProgramStructureId in (" + cpsiCommaSeparated +") and a_l_s.liveType = 'Resit' group by a.subject;";
			
			assignmentFiles = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(AssignmentFileBean.class));
			
		} catch (Exception e) {
			
		}
		return assignmentFiles;
	}

	//usedIN : DemoExamReportExcelView
	@Transactional(readOnly = true)
	public HashMap<String, CenterExamBean> getICLCMap() {
		String sql = "SELECT * FROM exam.centers where centerCode!='' ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		// List<StudentMarksBean> studentMarksList = new
		// ArrayList<StudentMarksBean>();

		ArrayList<CenterExamBean> centers = (ArrayList<CenterExamBean>) jdbcTemplate
				.query(sql, new BeanPropertyRowMapper(CenterExamBean.class));
		HashMap<String, CenterExamBean> icLcMap = new HashMap<>();
		for (CenterExamBean center : centers) {
			icLcMap.put(center.getCenterCode(), center);
		}

		return icLcMap;
	}
	@Transactional(readOnly = true)
	public HashMap<String, StudentExamBean> getAllStudents() {
		String sql = "select * from exam.students";
		HashMap<String, StudentExamBean> mapOfSapIdAndStudent = new HashMap<String, StudentExamBean>();
		ArrayList<StudentExamBean> allStudents = (ArrayList<StudentExamBean>) jdbcTemplate
				.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));
		if (allStudents != null && allStudents.size() > 0) {
			for (StudentExamBean student : allStudents) {
				mapOfSapIdAndStudent.put(student.getSapid(), student);
			}
		}
		return mapOfSapIdAndStudent;

	}
	@Transactional(readOnly = false)
	public void saveAssignmentSubmissionDetails(AssignmentFileBean bean,
			int maxAttempts, StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO exam.assignmentsubmission (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, status, attempts,  createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate, program) VALUES (?,?,?,?,?,?,?,1,?,sysdate(),?,sysdate(),?)"
				+ " on duplicate key update "

				+ "	    studentFilePath = ?,"
				+ "	    previewPath = ?,"
				+ "	    status = ?,"
				+ "	    lastModifiedBy = ?, "
				+ " 	attempts = attempts + 1, "
				+ "	    lastModifiedDate = sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String subject = bean.getSubject();
		String sapId = bean.getSapId();
		String studentFilePath = bean.getStudentFilePath();
		String previewPath = bean.getPreviewPath();
		String status = bean.getStatus();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		String program = "All";// Program set against assignment question file and
							// NOT program of student
		//Commented by sagar,As discussed with sanket sir,
		/*if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			program = BAJAJ_PROGRAM; // Program to be saved in submission table
										// that corresponds to program against
										// question file
		} else {
			program = "All"; // Program to be saved in submission table that
								// corresponds to program against question file
		}*/

		jdbcTemplate.update(sql, new Object[] { year, month, subject, sapId,
				studentFilePath, previewPath, status, createdBy,
				lastModifiedBy, program,

				studentFilePath, previewPath, status, lastModifiedBy });

		sql = " INSERT INTO exam.assignmentsubmissionhistory (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, uploadDate) VALUES (?,?,?,?,?,?,sysdate())";

		jdbcTemplate.update(sql, new Object[] { year, month, subject, sapId,
				studentFilePath, previewPath,

		});

	}
	@Transactional(readOnly = true)
	public HashMap<String, AssignmentFileBean> getSubmissionStatus(
			ArrayList<String> subjects, String sapId) {
		HashMap<String, AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
		if (subjects == null || subjects.size() == 0) {
			return subjectSubmissionMap;
		}
		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'"
						+ subjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '"
						+ subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
				+ " and subject in ("
				+ subjectCommaSeparated
				+ ") and a.sapid = ? ";*/
		/**
		 * Get Assignment Submission query added based on assignment_live_setting table. 
		 * */
		String sql = "select a.* from exam.assignmentsubmission a, exam.assignment_live_setting as a_l_s where "
				+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
				+ "a_l_s.liveType = 'Regular' and "
				+ "a.subject in ("+ subjectCommaSeparated +") and a.sapid = ? ;";

		List<AssignmentFileBean> assignmentFiles = jdbcTemplate.query(sql,
				new Object[] { sapId }, new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		if (assignmentFiles != null && assignmentFiles.size() > 0) {
			for (int i = 0; i < assignmentFiles.size(); i++) {
				subjectSubmissionMap.put(assignmentFiles.get(i).getSubject(),
						assignmentFiles.get(i));
			}
		}
		return subjectSubmissionMap;
	}
	@Transactional(readOnly = true)
	public HashMap<String, AssignmentFileBean> getResitSubmissionStatus(
			ArrayList<String> subjects, StudentExamBean student) {
		HashMap<String, AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
		if (subjects == null || subjects.size() == 0) {
			return subjectSubmissionMap;
		}

		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			if (i == 0) {
				subjectCommaSeparated = "'"
						+ subjects.get(i).replaceAll("'", "''") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '"
						+ subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		/**
		 * Get Assignment Submission query added based on assignment_live_setting table. 
		 * */
		/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
				+ " and subject in ("
				+ subjectCommaSeparated
				+ ") and a.sapid = ? ";*/
		String sql = "SELECT a.* FROM exam.assignmentsubmission a, exam.assignment_live_setting as a_l_s, exam.students s "
				+ "where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
				+ "a.sapid =  s.sapid and a_l_s.consumerProgramStructureId = s.consumerProgramStructureId and "
				+ "a_l_s.liveType = 'Resit' and "
				+ "a.subject in ("+ subjectCommaSeparated +") and a.sapid = ? ;";
		// Commented by sagar, BAJAJ program removed as discussed with sanket sir, Date: 04/02/2019
		/*if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			
			 * sql =
			 * "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
			 * +
			 * " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' and (month = 'Apr' or month = 'Sep'))  "
			 * + " and subject = ? and a.sapid = ? ";
			 
			// Bajaj Student not allowed more than one resit attempt//
			sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y'  and (month = 'Apr' or month = 'Sep')) "
					+ " and subject in ("
					+ subjectCommaSeparated
					+ ") and a.sapid = ? ";
		} else {
			sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
					+ " and subject in ("
					+ subjectCommaSeparated
					+ ") and a.sapid = ? ";
		}*/

		List<AssignmentFileBean> assignmentFiles = jdbcTemplate.query(sql,
				new Object[] { student.getSapid() }, new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		if (assignmentFiles != null && assignmentFiles.size() > 0) {
			for (int i = 0; i < assignmentFiles.size(); i++) {
				subjectSubmissionMap.put(assignmentFiles.get(i).getSubject(),
						assignmentFiles.get(i));
			}
		}
		return subjectSubmissionMap;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getAssignmentStatusForASubject(String subject,
			String sapId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			/**
			 * Get Assignment Submission query added based on assignment_live_setting table. 
			 * */
			/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject = ? and a.sapid = ? ";*/
			String sql = "SELECT a.* FROM exam.assignmentsubmission a, exam.assignment_live_setting as a_l_s, exam.students s "
					+ "where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.sapid =  s.sapid and a_l_s.consumerProgramStructureId = s.consumerProgramStructureId and "
					+ "a_l_s.liveType = 'Regular' and a.subject = ? and a.sapid = ?;";

			AssignmentFileBean assignmentFile = (AssignmentFileBean) jdbcTemplate
					.queryForObject(sql, new Object[] { subject, sapId },
							new BeanPropertyRowMapper(AssignmentFileBean.class));

			return assignmentFile;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getResitAssignmentStatusForASubject(
			String subject, StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/**
		 * Get Resit Assignment Submission query added based on assignment_live_setting table. 
		 * */
		String sql = "SELECT a.* FROM exam.assignmentsubmission a, exam.assignment_live_setting as a_l_s, exam.students s "
				+ "where a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
				+ "a.sapid =  s.sapid and a_l_s.consumerProgramStructureId = s.consumerProgramStructureId and "
				+ "a_l_s.liveType = 'Resit' and a.subject = ? and a.sapid = ?;";
		/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
				+ " and subject = ? and a.sapid = ? ";*/
		
		try {
			//commented by sagar, BAJAJ program removed as discussed with Sanket sir, Date: 04/02/2019
			/*if (BAJAJ_PROGRAM.equals(student.getProgram())) {
				// Bajaj Student not allowed more than one resit attempt//
				sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
						+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' and (month = 'Apr' or month = 'Sep'))  "
						+ " and subject = ? and a.sapid = ? ";

			} else {
				sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
						+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
						+ " and subject = ? and a.sapid = ? ";
			}*/
			AssignmentFileBean assignmentFile = (AssignmentFileBean) jdbcTemplate
					.queryForObject(sql,
							new Object[] { subject, student.getSapid() },
							new BeanPropertyRowMapper(AssignmentFileBean.class));

			return assignmentFile;
		} catch (Exception e) {
			return null;
		}

	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getAssignmentDetailsForStudent(
			AssignmentFileBean assignmentFile, StudentExamBean student) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		/**
		 * Get Assignment details query added based on assignment_live_setting table. 
		 * */
		/*String sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
				+ " where asb.month = eo.month and asb.year = eo.year "
				+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
				+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? "
				+ "and a.consumerProgramStructureId = ?";*/
		String sql = "SELECT * FROM exam.assignments a , exam.assignment_live_setting a_l_s, "
				+ "exam.assignmentsubmission asb where asb.month = a_l_s.examMonth and asb.year = a_l_s.examYear and "
				+ "a_l_s.examMonth = a.month and a_l_s.examYear = a.year and asb.subject = a.subject and a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
				+ "a.consumerProgramStructureId = ? and a_l_s.liveType = 'Regular' and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ?;";
		/**
		 * Commented by sagar,
		 * Reason: Added new ConsumerProgramStructureId for filter
		 * */
		//commented by sagar, As discussed with sanket sir, Date: 04/02/2019
		/*if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";// Fetch
																	// assignment
																	// set for
																	// Bajaj
		} else {
			sql = sql + " and a.program = 'All' "; // Fetch assignments for
													// other students
		}*/

		AssignmentFileBean assignment = (AssignmentFileBean) jdbcTemplate
				.queryForObject(sql, new Object[] {
						student.getConsumerProgramStructureId(),
						assignmentFile.getSubject(), assignmentFile.getYear(),
						assignmentFile.getMonth(), assignmentFile.getSapId()},
						new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignment;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getResitAssignmentDetailsForStudent(
			AssignmentFileBean assignmentFile, StudentExamBean student) {
		// For Bajaj students
		jdbcTemplate = new JdbcTemplate(dataSource);
		/**
		 * Get Resit Assignment details query added based on assignment_live_setting table. 
		 * */
		String sql = "SELECT * FROM exam.assignments a , exam.assignment_live_setting a_l_s, "
				+ "exam.assignmentsubmission asb where asb.month = a_l_s.examMonth and "
				+ "asb.year = a_l_s.examYear and asb.year = a.year and asb.month = a.month and "
				+ "asb.subject = a.subject and a_l_s.liveType = 'Resit' and a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
				+ "a_l_s.consumerProgramStructureId = ? and asb.subject = ? and "
				+ "asb.year = ? and asb.month = ? and asb.sapid = ?;";
		/*String sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
				+ " where asb.month = eo.month and asb.year = eo.year "
				+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
				+ " and eo.order = ( select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' ) "
				+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? "
				+ "and a.consumerProgramStructureId = ?";*/

		/**
		 * Commented by sagar,
		 * Reason: Added new ConsumerProgramStructureId for filter
		 * */
		/*if (BAJAJ_PROGRAM.equals(student.getProgram())) {
			// Bajaj Student not allowed more than one resit attempt//
			sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
					+ " where asb.month = eo.month and asb.year = eo.year "
					+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' and (month = 'Apr' or month = 'Sep')) "
					+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? ";
			sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";// Fetch
																	// assignment
																	// set for
																	// Bajaj
		} else {
			sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
					+ " where asb.month = eo.month and asb.year = eo.year "
					+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
					+ " and eo.order = ( select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' ) "
					+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? ";
			sql = sql + " and a.program = 'All' "; // Fetch assignments for
													// other students
		}*/
		AssignmentFileBean assignment = (AssignmentFileBean) jdbcTemplate
				.queryForObject(sql, new Object[] {
						student.getConsumerProgramStructureId(),
						assignmentFile.getSubject(), assignmentFile.getYear(),
						assignmentFile.getMonth(), assignmentFile.getSapId() },
						new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignment;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentSubmissionPage(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "";
		String countSql = "";
		if(searchBean.getConsumerProgramStructureId() == null) {
			sql = "select sb.*,ps.sem,s.* from exam.assignmentsubmission sb,exam.program_sem_subject ps,exam.students s  where sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId "
					+ " and sb.sapId = s.sapId";
			countSql = "select count(*) from exam.assignmentsubmission sb,exam.program_sem_subject ps ,exam.students s  where sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId "
					+ " and sb.sapId = s.sapId";
		}
		else {
			sql = "SELECT sb.*,s.*,ps.sem FROM exam.assignmentsubmission sb, exam.students s ,exam.program_sem_subject ps where sb.sapId = s.sapId and sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId and ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +") ";
			countSql = "SELECT count(*) FROM exam.assignmentsubmission sb, exam.students s ,exam.program_sem_subject ps where sb.sapId = s.sapId and sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId and ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +") ";
		}

		// By Pass Checks

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and sb.year = ? ";
			countSql = countSql + " and sb.year = ? ";
			parameters.add(searchBean.getYear());
		}
		
		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and sb.month = ? ";
			countSql = countSql + " and sb.month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and sb.subject = ? ";
			countSql = countSql + " and sb.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sb.sapId = ? ";
			countSql = countSql + " and sb.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		// Added by Ashutosh to catch the students who were later terminated/suspended
		sql = sql + " and (s.programStatus NOT IN('Program Terminated', 'Program Suspension') or s.programStatus is null or s.programStatus = '') ";
		countSql = countSql + " and (s.programStatus NOT IN('Program Terminated', 'Program Suspension') or s.programStatus is null or s.programStatus = '') ";
		
		sql = sql + " order by sb.subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getANS(int pageNo, int pageSize,
			AssignmentFileBean searchBean, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String examYear = searchBean.getYear();
		String examMonth = searchBean.getMonth();
		String acadYear = searchBean.getAcadYear();
		String acadMonth = searchBean.getAcadMonth();
		
		
		if ("".equals(examYear)) {
			examYear = "2018";
		}  

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		//query changed as per consumerProgramStructureId 
		/*String sql = "SELECT * FROM exam.students s, exam.program_subject ps, exam.registration r "
				+ " where  "
				+ "  r.program = ps.program "
				+ " and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid "
				+ " and r.sem = ps.sem "
				+ " and r.year = "+ acadYear+ " "
				+ " and r.month = '"+ acadMonth+ "' "
				+ " and concat(s.sapid, ps.subject) not in "
					+ "  (select concat(sapid, subject) from exam.assignmentsubmission where year = "+ examYear + "" + " and month = '" + examMonth + "' )"
				+ " and ps.hasAssignment = 'Y' ";*/
		
		String sql = " SELECT * FROM exam.students s, exam.program_sem_subject ps, exam.registration r "
				+ " where "
				+ " s.consumerProgramStructureId = ps.consumerProgramStructureId and "
				+ " s.sapid = r.sapid and "
				+ " r.sem = ps.sem and "
				+ " r.year = "+ acadYear+ " and "
				+ " r.month = '"+ acadMonth+ "' and "
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission "
				+ " where year = "+ examYear + " and month = '" + examMonth + "' ) and "
				+ " ps.hasAssignment = 'Y' and "
				+ " (s.programStatus <=> NULL or s.programStatus = '') and (s.programRemarks <=> NULL or s.programRemarks = '') and "
				/*+ " ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +") and "*/
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.passfail where isPass ='Y' )  "
				+ "and s.sapid not in (select sapid from exam.students where enrollmentMonth='Jan' and enrollmentYear='2023') ";
		//consumerProgramStructureId added inside query
		/*String countSql = "SELECT count(*) FROM exam.students s, exam.program_subject ps, exam.registration r "
                + " where  " 
				+ "  r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid and r.sem = ps.sem and r.year = "
				+ acadYear
				+ " and r.month = '"
				+ acadMonth
				+ "' and "
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where year = "

				+ examYear + "" + " and month = '" + examMonth + "' )"
				+ " and ps.hasAssignment = 'Y'  ";*/
		String countSql = "SELECT count(*) FROM exam.students s, exam.program_sem_subject ps, exam.registration r "
				+ " where  " 
				+ "  s.consumerProgramStructureId = ps.consumerProgramStructureId "
				+ " and s.sapid = r.sapid"
				+ " and r.sem = ps.sem"
				+ " and r.year = "+ acadYear
				+ " and r.month = '"+ acadMonth + "' and "
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where year = "+ examYear + "" 
				+ " and month = '" + examMonth + "' )"
				+ " and ps.hasAssignment = 'Y' "
				+ " and (s.programStatus <=> NULL or s.programStatus = '') and (s.programRemarks <=> NULL or s.programRemarks = '') "
				+ " and concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.passfail where isPass ='Y' ) "
				+ " and s.sapid not in (select sapid from exam.students where enrollmentMonth='Jan' and enrollmentYear='2023') ";
		/*
		 * if( searchBean.getYear() != null &&
		 * !("".equals(searchBean.getYear()))){ sql = sql + " and r.year = ? ";
		 * countSql = countSql + " and r.year = ? ";
		 * parameters.add(searchBean.getYear()); }
		 * 
		 * if( searchBean.getMonth() != null &&
		 * !("".equals(searchBean.getMonth()))){ sql = sql + " and month = ? ";
		 * countSql = countSql + " and month = ? ";
		 * parameters.add(searchBean.getMonth()); }
		 */

		if (searchBean.getConsumerProgramStructureId() != null && !("".equals(searchBean.getConsumerProgramStructureId()))){
			sql = sql + " and ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +") ";
			countSql = countSql + " and ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +")";
			
		}
		
		
		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and ps.subject = ? ";
			countSql = countSql + " and ps.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and s.sapId = ? ";
			countSql = countSql + " and s.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (authorizedCenterCodes != null
				&& !"".equals(authorizedCenterCodes.trim())) {
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in ("
					+ authorizedCenterCodes + ") ";
		}

		sql = sql + " order by s.sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = false)
	public void upsertAssignmentStatus(AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO exam.assignmentStatus "
				+ "(examYear, examMonth, sapid, subject, submitted, createdBy, createdDate)"
				+ " VALUES " + "(?,?,?,?,?,?,sysdate() )"
				+ " on duplicate key update " + "	    examYear = ?,"
				+ "	    examMonth = ?," + "	    sapid = ?,"
				+ "	    subject = ?," + "	    submitted = ?,"
				+ "	    createdBy = ?, " + "	    createdDate = sysdate() ";

		String examYear = bean.getYear();
		String examMonth = bean.getMonth();
		String sapid = bean.getSapId();
		String subject = bean.getSubject();
		String submitted = "Y";
		String createdBy = sapid;

		jdbcTemplate.update(sql, new Object[] { examYear, examMonth, sapid,
				subject, submitted, createdBy, examYear, examMonth, sapid,
				subject, submitted, createdBy });

	}

	
	
	@Transactional(readOnly = true)
	public int getNumberOfAssignments(AssignmentFileBean assignmentFile,
			String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.assignmentsubmission a,exam.students s where a.sapid = s.sapid and s.consumerProgramStructureId in ("+ assignmentFile.getConsumerProgramStructureId() +") and a.year = ? and a.month = ? "
				+ " and (a.sapid , a.subject) in (select sapid,subject from exam.quick_assignmentsubmission where year = '"+CURRENT_EXAM_YEAR+"' AND month = '"+CURRENT_EXAM_MONTH+"' AND endDate < current_date() "
						+ " and sapid not in (select sapid from exam.students where enrollmentMonth='Jan' and enrollmentYear='2023') "
						+ " ) ";

		if ("1".equalsIgnoreCase(level)) {
			sql = sql + " and (a.facultyId is null or a.facultyId = '' ) ";
					//+ " and toBeEvaluated = 'Y' and program = ? ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = sql + " and (a.faculty2 is null or a.faculty2 = '' ) ";
					//+ " and toBeEvaluated = 'Y' and program = ?  ";
		} else if ("3".equalsIgnoreCase(level)) {// Not used anymore
			return getNumberOfAssignmentsForLevel3(assignmentFile).size();
		}
		
		sql += " and a.toBeEvaluated = 'Y' and concat(a.sapid, a.subject) not in (select concat(sapid, subject) from exam.passfail where isPass = 'Y')";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());
		if (assignmentFile.getSubject() != null
				&& !"".equals(assignmentFile.getSubject().trim())) {
			sql = sql + " and a.subject = ?   ";
			parameters.add(assignmentFile.getSubject());
		}

		int numberOfSubjects = (int) jdbcTemplate.queryForObject(sql,
				parameters.toArray(),Integer.class);
		return numberOfSubjects;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getNumberOfAssignmentsForLevel3(
			AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*
		 * String sql =
		 * "select *, (abs((score-faculty2Score))/GREATEST(score, faculty2Score)) as percentDifference "
		 * +
		 * " from exam.assignmentsubmission where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
		 * + " having percentDifference > 0.2";
		 */

		/*String sql = "select *  from exam.assignmentsubmission "
				+ " where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
				+ " and " + " (" + " (score > -1 and score < 6) " + " or "
				+ " (score > 25 and score < 31) " + " or "
				+ " (faculty2score > 25 and faculty2score < 31)" + " or "
				+ " (faculty2score > -1 and faculty2score < 6) " + " or "
				+ " (abs(faculty2score - score) > 20) " + " )"
				+ " and (score + faculty2score) <> 0 ";*/
		String sql = "select a.* from exam.assignmentsubmission a,exam.students s where "
				+ "a.sapid = s.sapid and s.consumerProgramStructureId in "
				+ "("+ assignmentFile.getConsumerProgramStructureId() +") and a.year = ? and a.month = ? and "
				+ "a.subject = ? and (a.faculty3 is null or a.faculty3 = '' ) and "
				+ "( (a.score > -1 and a.score < 6) or (a.score > 25 and a.score < 31) or "
				+ "(a.faculty2score > 25 and a.faculty2score < 31) or (a.faculty2score > -1 and a.faculty2score < 6) "
				+ "or (abs(a.faculty2score - a.score) > 20) ) and (a.score + a.faculty2score) <> 0;";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());
		parameters.add(assignmentFile.getSubject());


		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, parameters.toArray(), new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignmentsForReval(
			AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*
		 * String sql =
		 * "select * from exam.passfail b, exam.assignmentsubmission a  " +
		 * " where year = ? and month = ?  and  a.subject = ? " +
		 * "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) "
		 * + " and a.sapid = b.sapid and a.subject = b.subject";
		 */

		String sql = "select * from exam.assignmentsubmission   "
				+ " where year = ? and month = ?  and  subject = ? "
				+ "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());
		parameters.add(assignmentFile.getSubject());


		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, parameters.toArray(), new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getProjectsForReval(
			AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from exam.marks  m , exam.projectsubmission p "
				+ " where m.year = p.year and m.month = p.month and m.sapid = p.sapid "
				+ " and m.year = ? and m.month = ?  and  m.subject IN ('Project', 'Module 4 - Project') "
				+ "  and m.markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());


		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, parameters.toArray(), new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignmentsForReReval(
			AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*
		 * String sql =
		 * "select * from exam.passfail b, exam.assignmentsubmission a  " +
		 * " where year = ? and month = ?  and  a.subject = ? " +
		 * "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) "
		 * + " and a.sapid = b.sapid and a.subject = b.subject";
		 */

		/*String sql = "select s.*, m.assignmentscore from exam.assignmentsubmission s, exam.marks m  "
				+ " where s.year = ? and s.month = ?   "
				+ " and s.markedForRevaluation = 'Y'  "
				+ " and s.year = m.year and s.month = m.month "
				+ " and s.sapid = m.sapid and s.subject = m.subject"
				+ " and (ABS((revaluationScore - assignmentscore ))/ assignmentscore ) >= 0.2 "
				+ " and (faculty2 is null or faculty2 = '' ) "
				+ " order by s.subject asc";*/
		String sql = "select a.*, m.assignmentscore from exam.assignmentsubmission a, exam.marks m,exam.students s "
				+ "where a.year = ? and a.month = ? and a.markedForRevaluation = 'Y' and a.year = m.year and "
				+ "a.month = m.month and a.sapid = m.sapid and a.subject = m.subject and a.sapid = s.sapid and "
				+ "s.consumerProgramStructureId in (" + assignmentFile.getConsumerProgramStructureId() +") and "
				/* Condition added by Ashutosh on 14-05-2021 because of the issue where if assignment score field is 0 the (ABS()/ assignmentscore) function returns null. */
				+ "( "
					+ "(ABS((revaluationScore - assignmentscore ))/ assignmentscore ) >= 0.2 "
					+ " OR (revaluationScore > assignmentscore && assignmentscore = 0 ) "
				+ ") and "
				+ "(a.faculty2 is null or a.faculty2 = '' ) ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());

		if (assignmentFile.getSubject() != null && !("".equals(assignmentFile.getSubject()))) {
			sql = sql + " and a.subject = ? ";
			parameters.add(assignmentFile.getSubject());
		}
		
		sql += " order by a.subject asc;";
		

		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, parameters.toArray(), new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignments(
			AssignmentFileBean assignmentFile, String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		if ("1".equalsIgnoreCase(level)) {
			sql = "SELECT a_s.year, a_s.month, a_s.sapid, a_s.subject FROM exam.assignmentsubmission a_s,exam.students s "
					+ "WHERE s.sapid = a_s.sapid and a_s.year = ? AND a_s.month = ? AND "
					+ "a_s.subject = ? "
					+ "AND (a_s.facultyId IS NULL OR a_s.facultyId = '') AND a_s.toBeEvaluated = 'Y' AND "
					+ "s.consumerProgramStructureId in (" + assignmentFile.getConsumerProgramStructureId()  + ")"
					+ "and (a_s.sapid , a_s.subject) in (select sapid,subject from exam.quick_assignmentsubmission where year = '"+CURRENT_EXAM_YEAR+"' AND month = '"+CURRENT_EXAM_MONTH+"' AND endDate < current_date() "
					+ " and sapid not in (select sapid from exam.students where enrollmentMonth='Jan' and enrollmentYear='2023') "
					+ " ) ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "select year, month, sapid, subject from exam.assignmentsubmission where year = ? and "
					+ "month = ? and subject = ?  and (faculty2 is null or faculty2 = '' )  and toBeEvaluated = 'Y' "
					+ " and program = ? ";
		} else if ("3".equalsIgnoreCase(level)) {// Not used anymore
			/*
			 * sql =
			 * "select *, (abs((score-faculty2Score))/GREATEST(score, faculty2Score)) as percentDifference "
			 * +
			 * " from exam.assignmentsubmission where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
			 * + " having percentDifference > 0.2";
			 */

			sql = "select *  from exam.assignmentsubmission "
					+ " where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
					+ " and " + " (" + " (score > -1 and score < 6) " + " or "
					+ " (score > 25 and score < 31) " + " or "
					+ " (faculty2score > 25 and faculty2score < 31)" + " or "
					+ " (faculty2score > -1 and faculty2score < 6) " + " or "
					+ " (abs(faculty2score - score) > 20) " + " )"
					+ " and (score + faculty2score) <> 0 ";
		}

		sql += " and concat(a_s.sapid, a_s.subject) not in (select concat(p.sapid, p.subject) from exam.passfail p where isPass = 'Y')";

		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, new Object[] { assignmentFile.getYear(),
						assignmentFile.getMonth(), assignmentFile.getSubject()
						},
						new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = false)
	public void allocateAssignment(
			final List<AssignmentFileBean> assignmentsSubSet,
			final String facultyId, final String level) {
		String sql = null;
		if ("1".equalsIgnoreCase(level)) {
			sql = "update exam.assignmentsubmission " + " set facultyId = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "update exam.assignmentsubmission " + " set faculty2 = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		} else if ("3".equalsIgnoreCase(level)) {
			sql = "update exam.assignmentsubmission " + " set faculty3 = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		}

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, facultyId);
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	} 
	@Transactional(readOnly = false)
	public void allocateAssignmentInQuickTable(
			final List<AssignmentFileBean> assignmentsSubSet,
			final String facultyId, final String level) {
		String sql = null;
		if ("1".equalsIgnoreCase(level)) {
			sql = "update exam.quick_assignmentsubmission " + " set facultyId = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "update exam.quick_assignmentsubmission " + " set faculty2 = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		} else if ("3".equalsIgnoreCase(level)) {
			sql = "update exam.quick_assignmentsubmission " + " set faculty3 = ? "
					+ " where year = ? " + " and month = ?" + " and sapid = ?"
					+ " and subject = ? ";
		}

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, facultyId);
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	} 
	@Transactional(readOnly = false)
    public void allocateAssignmentForReval(
			final List<AssignmentFileBean> assignmentsSubSet) {

		String sql = "update exam.assignmentsubmission "
				+ " set facultyIdRevaluation = ? " + " where year = ? "
				+ " and month = ?" + " and sapid = ?" + " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, a.getFacultyId());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	} 
	@Transactional(readOnly = false)
	public void allocateAssignmentForRevalQuickTable(
			final List<AssignmentFileBean> assignmentsSubSet) {

		String sql = "update exam.quick_assignmentsubmission "
				+ " set facultyIdRevaluation = ? " + " where year = ? "
				+ " and month = ?" + " and sapid = ?" + " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, a.getFacultyId());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	} 
	@Transactional(readOnly = false)
    public void allocateProjectForReval(
			final List<AssignmentFileBean> assignmentsSubSet) {

		String sql = "update exam.projectsubmission "
				+ " set facultyIdRevaluation = ? " + " where year = ? "
				+ " and month = ?" + " and sapid = ?";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, a.getFacultyId());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	}
	@Transactional(readOnly = false)
	public void allocateAssignmentForReReval(
			final List<AssignmentFileBean> assignmentsSubSet) {

		String sql = "update exam.assignmentsubmission " + " set faculty2 = ? "
				+ " where year = ? " + " and month = ?" + " and sapid = ?"
				+ " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, a.getFacultyId());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	}
	@Transactional(readOnly = false)
	public void allocateAssignmentForReRevalQuickTable(
			final List<AssignmentFileBean> assignmentsSubSet) {

		String sql = "update exam.quick_assignmentsubmission " + " set faculty2 = ? "
				+ " where year = ? " + " and month = ?" + " and sapid = ?"
				+ " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = assignmentsSubSet.get(i);
				ps.setString(1, a.getFacultyId());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getMonth());
				ps.setString(4, a.getSapId());
				ps.setString(5, a.getSubject());
			}

			public int getBatchSize() {
				return assignmentsSubSet.size();
			}

		});

	} 
	@Transactional(readOnly = true)
    public List<AssignmentFileBean> getAssignmentsForFaculty(String facultyId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.assignmentsubmission where facultyId = ? and  ( evaluated is null or evaluated <> 'Y') ";
		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, new Object[] { facultyId },
						new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForFacultyPage(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		// Each facultyId matched from same row, will return separate row due to
		// condition of a.facultyId = f.facultyId or a.faculty2 = f.facultyId or
		// a.faculty3 = f.facultyId
		String sql = "SELECT a.*, f.*, s.firstName as sfName, s.lastName as slName,"
				+ "m.assignmentscore FROM exam.assignmentsubmission a inner join exam.students s"
				+ " on s.sapid = a.sapid inner join acads.faculty f ON  "
				+ "(a.facultyId = f.facultyId or a.faculty2 = f.facultyId or a.faculty3 = f.facultyId or a.facultyIdRevaluation = f.facultyId) "
				+ "left join exam.marks m ON m.sapid = a.sapid and m.year = a.year and m.month = a.month and m.subject = a.subject where 1=1 ";

		String countSql = "SELECT count(*) FROM exam.assignmentsubmission a "
				+ "inner join exam.students s on s.sapid = a.sapid "
				+ "inner join acads.faculty f ON  "
				+ "(a.facultyId = f.facultyId or a.faculty2 = f.facultyId or a.faculty3 = f.facultyId or a.facultyIdRevaluation = f.facultyId) "
				+ "left join exam.marks m ON m.sapid = a.sapid and m.year = a.year and m.month = a.month and m.subject = a.subject where 1=1 ";

		
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and a.year = ? ";
			countSql = countSql + " and a.year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and a.month = ? ";
			countSql = countSql + " and a.month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and a.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql
					+ " and (a.facultyId = ? or a.faculty2 = ? or a.faculty3 = ? or a.facultyIdRevaluation = ? ) ";
			countSql = countSql
					+ " and (a.facultyId = ? or a.faculty2 = ? or a.faculty3 = ? or a.facultyIdRevaluation = ? ) ";
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql + " and (  evaluated = ? or evaluated is null )";
			countSql = countSql
					+ " and (  evaluated = ? or evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("N".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql
					+ " and (  Faculty2Evaluated = ? or Faculty2Evaluated is null )";
			countSql = countSql
					+ " and (  Faculty2Evaluated = ? or Faculty2Evaluated is null ) ";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("N".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql
					+ " and (  Faculty3Evaluated = ? or Faculty3Evaluated is null )";
			countSql = countSql
					+ " and (  Faculty3Evaluated = ? or Faculty3Evaluated is null ) ";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and evaluated = ? ";
			countSql = countSql + " and  evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql + " and Faculty2Evaluated = ? ";
			countSql = countSql + " and  Faculty2Evaluated = ?  ";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql + " and Faculty3Evaluated = ? ";
			countSql = countSql + " and  Faculty3Evaluated = ?  ";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getRevaluated() != null
				&& !("".equals(searchBean.getRevaluated()))) {
			sql = sql + " and a.Revaluated = ? ";
			countSql = countSql + " and  a.Revaluated = ?  ";
			parameters.add(searchBean.getRevaluated());
		}

		if (searchBean.getMarkedForRevaluation() != null
				&& !("".equals(searchBean.getMarkedForRevaluation()))) {
			sql = sql + " and a.MarkedForRevaluation = ? ";
			countSql = countSql + " and  a.MarkedForRevaluation = ?  ";
			parameters.add(searchBean.getMarkedForRevaluation());
		}

		if (searchBean.getReason() != null
				&& !("".equals(searchBean.getReason()))) {
			sql = sql + " and a.reason = ? ";
			countSql = countSql + " and a.reason = ? ";
			parameters.add(searchBean.getReason());
		}
		if (searchBean.getRevisited() != null
				&& !("".equals(searchBean.getRevisited()))) {
			sql = sql + " and Revisited = ? ";
			countSql = countSql + " and  Revisited = ?  ";
			parameters.add(searchBean.getRevisited());
		}

		sql = sql + "  group by a.subject, a.sapid order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		HashMap<String, FacultyExamBean> facultyMap = facultyDAO.getFacultiesMap();
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		for (AssignmentFileBean assignmentFileBean : assignmentFilesList) {

		}
		return page;
	}
	@Transactional(readOnly = true)
	public ArrayList<AssignmentFileBean> getAssignmentsForEvaluations2(AssignmentFileBean searchBean){
		String	sql = "SELECT a.*, s.firstName as sfName, s.lastName as slName,"
					+ " aqm.q1Marks, aqm.q1Remarks ,aqm.q2Marks, aqm.q2Remarks,aqm.q3Marks,aqm.q3Remarks,"
					+ " aqm.q4Marks,aqm.q4Remarks,aqm.q5Marks,aqm.q5Remarks,aqm.q6Marks,aqm.q6Remarks,aqm.q7Marks,aqm.q7Remarks,"
					+ " aqm.q8Marks,aqm.q8Remarks,aqm.q9Marks,aqm.q9Remarks,aqm.q10Marks,aqm.q10Remarks , aqm.facultyId as evaluatedFaculty"
					+ " from exam.assignmentsubmission a,exam.students s, exam.assignmentquestionmarks aqm   "
					+ " where  a.sapid = s.sapid and a.sapid= aqm.sapid and a.subject = aqm.subject and a.month = aqm.month and a.year = aqm.year ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and a.year = ? ";
			
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and a.month = ? ";
			
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and a.subject = ? ";
		
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql
					+ " and (a.facultyId = ? or a.faculty2 = ? or a.faculty3 = ? or a.facultyIdRevaluation = ? ) ";
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql + " and (  evaluated = ? or evaluated is null )";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("N".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql
					+ " and (  Faculty2Evaluated = ? or Faculty2Evaluated is null )";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("N".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql
					+ " and (  Faculty3Evaluated = ? or Faculty3Evaluated is null )";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and evaluated = ? ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql + " and Faculty2Evaluated = ? ";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql + " and Faculty3Evaluated = ? ";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getRevaluated() != null
				&& !("".equals(searchBean.getRevaluated()))) {
			sql = sql + " and Revaluated = ? ";
			parameters.add(searchBean.getRevaluated());
		}

		if (searchBean.getMarkedForRevaluation() != null
				&& !("".equals(searchBean.getMarkedForRevaluation()))) {
			sql = sql + " and MarkedForRevaluation = ? ";
			parameters.add(searchBean.getMarkedForRevaluation());
		}

		if (searchBean.getReason() != null
				&& !("".equals(searchBean.getReason()))) {
			sql = sql + " and a.reason = ? ";
			parameters.add(searchBean.getReason());
		}
		if (searchBean.getRevisited() != null
				&& !("".equals(searchBean.getRevisited()))) {
			sql = sql + " and Revisited = ? ";
			parameters.add(searchBean.getRevisited());
		}
		sql = sql + "  order by a.subject asc ";
		Object[] args = parameters.toArray();
		
		ArrayList<AssignmentFileBean> assignmentEvaluatedList2 = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, args,new BeanPropertyRowMapper(AssignmentFileBean.class));
		
		return assignmentEvaluatedList2;
	}
	
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForEvaluations(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = null;
		String countSql = null;
		if(pageNo == 1){
		sql = "SELECT a.*, s.firstName as sfName, s.lastName as slName,m.assignmentscore FROM exam.assignmentsubmission a inner join "
				+ " exam.students s ON  a.sapid = s.sapid left join exam.marks m ON m.year = a.year and m.month = a.month and m.sapid = a.sapid and m.subject = a.subject where 1=1 ";

		countSql = "SELECT count(*) FROM exam.assignmentsubmission a inner join exam.students s  ON  a.sapid = s.sapid left join exam.marks m ON m.year = a.year and m.month = a.month and m.sapid = a.sapid and m.subject = a.subject where 1=1";
		}
		
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and a.year = ? ";
			countSql = countSql + " and a.year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and a.month = ? ";
			countSql = countSql + " and a.month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and a.subject = ? ";
			countSql = countSql + " and a.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql
					+ " and (a.facultyId = ? or a.faculty2 = ? or a.faculty3 = ? or a.facultyIdRevaluation = ? ) ";
			countSql = countSql
					+ " and (a.facultyId = ? or a.faculty2 = ? or a.faculty3 = ? or a.facultyIdRevaluation = ? ) ";
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql + " and (  a.evaluated = ? or a.evaluated is null )";
			countSql = countSql
					+ " and (  a.evaluated = ? or a.evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("N".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql
					+ " and (  a.Faculty2Evaluated = ? or a.Faculty2Evaluated is null )";
			countSql = countSql
					+ " and (  a.Faculty2Evaluated = ? or a.Faculty2Evaluated is null ) ";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("N".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql
					+ " and (  a.Faculty3Evaluated = ? or a.Faculty3Evaluated is null )";
			countSql = countSql
					+ " and (  a.Faculty3Evaluated = ? or a.Faculty3Evaluated is null ) ";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and a.evaluated = ? ";
			countSql = countSql + " and  a.evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFaculty2Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty2Evaluated()))) {
			sql = sql + " and a.Faculty2Evaluated = ? ";
			countSql = countSql + " and  a.Faculty2Evaluated = ?  ";
			parameters.add(searchBean.getFaculty2Evaluated());
		}

		if (searchBean.getFaculty3Evaluated() != null
				&& ("Y".equals(searchBean.getFaculty3Evaluated()))) {
			sql = sql + " and a.Faculty3Evaluated = ? ";
			countSql = countSql + " and  a.Faculty3Evaluated = ?  ";
			parameters.add(searchBean.getFaculty3Evaluated());
		}

		if (searchBean.getRevaluated() != null
				&& !("".equals(searchBean.getRevaluated()))) {
			sql = sql + " and a.Revaluated = ? ";
			countSql = countSql + " and  a.Revaluated = ?  ";
			parameters.add(searchBean.getRevaluated());
		}

		if (searchBean.getMarkedForRevaluation() != null
				&& !("".equals(searchBean.getMarkedForRevaluation()))) {
			sql = sql + " and a.MarkedForRevaluation = ? ";
			countSql = countSql + " and  a.MarkedForRevaluation = ?  ";
			parameters.add(searchBean.getMarkedForRevaluation());
		}

		if (searchBean.getReason() != null
				&& !("".equals(searchBean.getReason()))) {
			sql = sql + " and a.reason = ? ";
			countSql = countSql + " and a.reason = ? ";
			parameters.add(searchBean.getReason());
		}
		if (searchBean.getRevisited() != null
				&& !("".equals(searchBean.getRevisited()))) {
			sql = sql + " and a.Revisited = ? ";
			countSql = countSql + " and  a.Revisited = ?  ";
			parameters.add(searchBean.getRevisited());
		}

		sql = sql + " group by a.subject, a.sapid order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));
      if(pageNo == 1){
		// Populate names of each faculty
		HashMap<String, FacultyExamBean> facultyMap = facultyDAO.getFacultiesMap();
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		if (assignmentFilesList != null) {
			for (AssignmentFileBean assignmentFileBean : assignmentFilesList) {
				String faculty1 = assignmentFileBean.getFacultyId();
				String faculty2 = assignmentFileBean.getFaculty2();
				String faculty3 = assignmentFileBean.getFaculty3();
				String facultyReval = assignmentFileBean
						.getFacultyIdRevaluation();
				if (!StringUtils.isEmpty(faculty1) && faculty1 !=null) {
					assignmentFileBean.setFaculty1Name(facultyMap.get(faculty1)
							.getFirstName()
							+ " "
							+ facultyMap.get(faculty1).getLastName());
				}
				if (!StringUtils.isEmpty(faculty2) && faculty2 !=null) {
					assignmentFileBean.setFaculty2Name(facultyMap.get(faculty2)
							.getFirstName()
							+ " "
							+ facultyMap.get(faculty2).getLastName());
				}
				if (!StringUtils.isEmpty(faculty3) && faculty3 !=null) {
					assignmentFileBean.setFaculty3Name(facultyMap.get(faculty3)
							.getFirstName()
							+ " "
							+ facultyMap.get(faculty3).getLastName());
				}
				if (!StringUtils.isEmpty(facultyReval) && facultyReval !=null) {
					assignmentFileBean.setFacultyRevaluationName(facultyMap
							.get(facultyReval).getFirstName()
							+ " "
							+ facultyMap.get(facultyReval).getLastName());
				}
			}
		}
		}
		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForFaculty1(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
			String sql = "SELECT a.*,  f.firstName,f.lastName" + 
					"	  from exam.quick_assignmentsubmission a  " +  
					"     inner join acads.faculty f on a.facultyId = f.facultyId  where a.status='Submitted'  ";

		String countSql = "SELECT count(*) "+
				"	  from exam.quick_assignmentsubmission a  " +
				"     inner join acads.faculty f on a.facultyId = f.facultyId  where a.status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and (a.facultyId = ?  ) ";
			countSql = countSql + " and (a.facultyId = ?  ) ";
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql + " and (  evaluated = ? or evaluated is null )";
			countSql = countSql
					+ " and (  evaluated = ? or evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and evaluated = ? ";
			countSql = countSql + " and  evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		sql = sql + "  group by a.subject, a.sapid order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForFaculty2(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT a.*, a.faculty2Evaluated as evaluated, a.faculty2EvaluationDate as evaluationDate, a.facultyId, f.firstName,f.lastName "+
				"	  from exam.quick_assignmentsubmission a  " +  
				"     inner join acads.faculty f on a.faculty2 = f.facultyId  where a.status='Submitted' ";

		String countSql = "SELECT count(*) FROM exam.quick_assignmentsubmission a "+ 
				"     inner join acads.faculty f on a.faculty2 = f.facultyId  where a.status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and (a.faculty2 = ?  ) ";
			countSql = countSql + " and (a.faculty2 = ?  ) ";
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql
					+ " and (  faculty2Evaluated = ? or faculty2Evaluated is null )";
			countSql = countSql
					+ " and (  faculty2Evaluated = ? or faculty2Evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and faculty2Evaluated = ? ";
			countSql = countSql + " and  faculty2Evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		sql = sql + " group by a.subject, a.sapid  order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForFaculty3(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT a.*, a.faculty3Evaluated as evaluated, a.faculty3EvaluationDate as evaluationDate, f.firstName,f.lastName "+
				"	  from exam.quick_assignmentsubmission a  " +  
				"     inner join acads.faculty f on a.faculty3 = f.facultyId  where a.status='Submitted' ";

		String countSql =  "SELECT count(*)  "+
				"	  from exam.quick_assignmentsubmission a  " +  
				"     inner join acads.faculty f on a.faculty3 = f.facultyId  where a.status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and (a.faculty3 = ?  ) ";
			countSql = countSql + " and (a.faculty3 = ?  ) ";
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql
					+ " and (  faculty3Evaluated = ? or faculty3Evaluated is null )";
			countSql = countSql
					+ " and (  faculty3Evaluated = ? or faculty3Evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and faculty3Evaluated = ? ";
			countSql = countSql + " and  faculty3Evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		sql = sql + " group by a.subject, a.sapid  order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsForReval(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT a.*,a.revaluated as evaluated, a.revaluationDate as evaluationDate, f.*"
				+ " FROM exam.quick_assignmentsubmission a "+
				"   inner join acads.faculty f on a.facultyIdRevaluation = f.facultyId  where status='Submitted'";

		String countSql = "SELECT count(*) FROM exam.quick_assignmentsubmission a "+
				"   inner join acads.faculty f on a.facultyIdRevaluation = f.facultyId  where status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and (a.facultyIdRevaluation = ?  ) ";
			countSql = countSql + " and (a.facultyIdRevaluation = ?  ) ";
			parameters.add(searchBean.getFacultyId());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql + " and (  revaluated = ? or revaluated is null )";
			countSql = countSql
					+ " and (  revaluated = ? or revaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and revaluated = ? ";
			countSql = countSql + " and  revaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		sql = sql + " group by a.subject, a.sapid  order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getSingleAssignmentForFaculty(
			AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> consumerProgramStructureIds = getConsumerProgStructForOldProgramsFromRegistration(assignmentFile.getSapId());
		if(!consumerProgramStructureIds.contains(assignmentFile.getConsumerProgramStructureId())) {
			consumerProgramStructureIds.add(assignmentFile.getConsumerProgramStructureId());
		}	
		String cpsiInString = consumerProgramStructureIds.toString();
		String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);
		String sql = "select * from exam.assignmentsubmission a, exam.assignments s where a.year = ? and a.month = ? "
				+ " and a.subject = ?  and sapid = ? and (facultyId = ?  or faculty2 = ? or faculty3 = ? or facultyIdRevaluation = ? ) and "
				+ "s.year = a.year and s.month = a.month and s.subject = a.subject and a.program = s.program and s.consumerProgramStructureId  in ("+ cpsiCommaSeparated +") "
						+ " group by a.sapid";


		AssignmentFileBean assignment = (AssignmentFileBean) jdbcTemplate
				.queryForObject(
						sql,
						new Object[] { assignmentFile.getYear(),
								assignmentFile.getMonth(),
								assignmentFile.getSubject(),
								assignmentFile.getSapId(),
								assignmentFile.getFacultyId(),
								assignmentFile.getFacultyId(),
								assignmentFile.getFacultyId(),
								assignmentFile.getFacultyId() 
								//assignmentFile.getConsumerProgramStructureId()
						},
						new BeanPropertyRowMapper(AssignmentFileBean.class));

		sql = "select * from exam.assignmentquestionmarks a where year = ? and month = ? "
				+ " and subject = ?  and sapid = ? and facultyId = ?  ";

		try {
			AssignmentQuestionMarksBean marks = (AssignmentQuestionMarksBean) jdbcTemplate
					.queryForObject(
							sql,
							new Object[] { assignmentFile.getYear(),
									assignmentFile.getMonth(),
									assignmentFile.getSubject(),
									assignmentFile.getSapId(),
									assignmentFile.getFacultyId() },
							new BeanPropertyRowMapper(
									AssignmentQuestionMarksBean.class));

			if (marks != null) {
				// Copy individual question marks into main bean
				assignment.setQ1Marks(marks.getQ1Marks());
				assignment.setQ1Remarks(marks.getQ1Remarks());

				assignment.setQ2Marks(marks.getQ2Marks());
				assignment.setQ2Remarks(marks.getQ2Remarks());

				assignment.setQ3Marks(marks.getQ3Marks());
				assignment.setQ3Remarks(marks.getQ3Remarks());

				assignment.setQ4Marks(marks.getQ4Marks());
				assignment.setQ4Remarks(marks.getQ4Remarks());

				assignment.setQ5Marks(marks.getQ5Marks());
				assignment.setQ5Remarks(marks.getQ5Remarks());

				assignment.setQ6Marks(marks.getQ6Marks());
				assignment.setQ6Remarks(marks.getQ6Remarks());

				assignment.setQ7Marks(marks.getQ7Marks());
				assignment.setQ7Remarks(marks.getQ7Remarks());

				assignment.setQ8Marks(marks.getQ8Marks());
				assignment.setQ8Remarks(marks.getQ8Remarks());

				assignment.setQ9Marks(marks.getQ9Marks());
				assignment.setQ9Remarks(marks.getQ9Remarks());

				assignment.setQ10Marks(marks.getQ10Marks());
				assignment.setQ10Remarks(marks.getQ10Remarks());
			}
		} catch (Exception e) {
			//
		}

		return assignment;
	}

	@Transactional
	public void evaluateAssignment(AssignmentFileBean assignment, String level,
			String facultyId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "";

		if ("1".equalsIgnoreCase(level)) {
			sql = "Update exam.assignmentsubmission set evaluated = 'Y', score = ? , evaluationDate = sysdate() , remarks = ? , reason = ?, "
					+ " evaluationCount = evaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "Update exam.assignmentsubmission set faculty2Evaluated = 'Y', faculty2Score = ? , "
					+ "faculty2EvaluationDate = sysdate() , faculty2Remarks = ? , faculty2Reason = ?, "
					+ " faculty2EvaluationCount = faculty2EvaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("3".equalsIgnoreCase(level)) {
			sql = "Update exam.assignmentsubmission set faculty3Evaluated = 'Y', faculty3Score = ? , "
					+ "faculty3EvaluationDate = sysdate() , faculty3Remarks = ? , faculty3Reason = ?, "
					+ " faculty3EvaluationCount = faculty3EvaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("4".equalsIgnoreCase(level)) {// Revaluation
			sql = "Update exam.assignmentsubmission set revaluated = 'Y', revaluationScore = ? , "
					+ "revaluationDate = sysdate() , revaluationRemarks = ? , revaluationReason = ?, "
					+ " revaluationCount = revaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		}

		jdbcTemplate.update(sql,
				new Object[] { assignment.getScore(), assignment.getRemarks(),
						assignment.getReason(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() });

		sql = "insert into exam.assignmentevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate,lastModifiedBy , lastModifiedDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate(),?, ?,?)";

		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getYear(), assignment.getMonth(),
						assignment.getSapId(), assignment.getSubject(),
						facultyId, facultyId,facultyId, assignment.getScore(),
						assignment.getRemarks(), assignment.getReason() });

		sql = "insert into exam.assignmentquestionmarks (year, month, sapid, subject, facultyId,"
				+ " q1Marks, q1Remarks, "
				+ " q2Marks, q2Remarks, "
				+ " q3Marks, q3Remarks, "
				+ " q4Marks, q4Remarks, "
				+ " q5Marks, q5Remarks, "
				+ " q6Marks, q6Remarks, "
				+ " q7Marks, q7Remarks, "
				+ " q8Marks, q8Remarks, "
				+ " q9Marks, q9Remarks, "
				+ " q10Marks, q10Remarks) values "
				+ "(?, ?, ?, ?, ?, "
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?,"
				+ "?,?) on duplicate key update "
				+ "q1Marks = ?, q1Remarks = ?, "
				+ "q2Marks = ?, q2Remarks = ?, "
				+ "q3Marks = ?, q3Remarks = ?, "
				+ "q4Marks = ?, q4Remarks = ?, "
				+ "q5Marks = ?, q5Remarks = ?, "
				+ "q6Marks = ?, q6Remarks = ?, "
				+ "q7Marks = ?, q7Remarks = ?, "
				+ "q8Marks = ?, q8Remarks = ?, "
				+ "q9Marks = ?, q9Remarks = ?, "
				+ "q10Marks = ?, q10Remarks = ? " + "";

		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getYear(), assignment.getMonth(),
						assignment.getSapId(), assignment.getSubject(),
						facultyId, assignment.getQ1Marks(),
						assignment.getQ1Remarks(), assignment.getQ2Marks(),
						assignment.getQ2Remarks(), assignment.getQ3Marks(),
						assignment.getQ3Remarks(), assignment.getQ4Marks(),
						assignment.getQ4Remarks(), assignment.getQ5Marks(),
						assignment.getQ5Remarks(), assignment.getQ6Marks(),
						assignment.getQ6Remarks(), assignment.getQ7Marks(),
						assignment.getQ7Remarks(), assignment.getQ8Marks(),
						assignment.getQ8Remarks(), assignment.getQ9Marks(),
						assignment.getQ9Remarks(), assignment.getQ10Marks(),
						assignment.getQ10Remarks(), assignment.getQ1Marks(),
						assignment.getQ1Remarks(), assignment.getQ2Marks(),
						assignment.getQ2Remarks(), assignment.getQ3Marks(),
						assignment.getQ3Remarks(), assignment.getQ4Marks(),
						assignment.getQ4Remarks(), assignment.getQ5Marks(),
						assignment.getQ5Remarks(), assignment.getQ6Marks(),
						assignment.getQ6Remarks(), assignment.getQ7Marks(),
						assignment.getQ7Remarks(), assignment.getQ8Marks(),
						assignment.getQ8Remarks(), assignment.getQ9Marks(),
						assignment.getQ9Remarks(), assignment.getQ10Marks(),
						assignment.getQ10Remarks() });
	} 
	@Transactional(readOnly = false)
	public void evaluateAssignmentQuickTable(AssignmentFileBean assignment, String level ) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "";

		if ("1".equalsIgnoreCase(level)) {
			sql = "Update exam.quick_assignmentsubmission set evaluated = 'Y', score = ? , evaluationDate = sysdate() , remarks = ? , reason = ?, "
					+ " evaluationCount = evaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "Update exam.quick_assignmentsubmission set faculty2Evaluated = 'Y', faculty2Score = ? , "
					+ "faculty2EvaluationDate = sysdate() , faculty2Remarks = ? , faculty2Reason = ?, "
					+ " faculty2EvaluationCount = faculty2EvaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("3".equalsIgnoreCase(level)) {
			sql = "Update exam.quick_assignmentsubmission set faculty3Evaluated = 'Y', faculty3Score = ? , "
					+ "faculty3EvaluationDate = sysdate() , faculty3Remarks = ? , faculty3Reason = ?, "
					+ " faculty3EvaluationCount = faculty3EvaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		} else if ("4".equalsIgnoreCase(level)) {// Revaluation
			sql = "Update exam.quick_assignmentsubmission set revaluated = 'Y', revaluationScore = ? , "
					+ "revaluationDate = sysdate() , revaluationRemarks = ? , revaluationReason = ?, "
					+ " revaluationCount = revaluationCount + 1 "
					+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		}

		jdbcTemplate.update(sql,
				new Object[] { assignment.getScore(), assignment.getRemarks(),
						assignment.getReason(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() });
	} 
	@Transactional(readOnly = false)
    public void reEvaluateAssignment(AssignmentFileBean assignment) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.assignmentsubmission set revaluated = 'Y', revaluationScore = ? , revaluationRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getRevaluationScore(),
						assignment.getRevaluationRemarks(),
						assignment.getFacultyId(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() });

		sql = "insert into exam.assignmentevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate,lastModifiedBy , lastModifiedDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?,sysdate(),?, ?,?)";

		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getYear(), assignment.getMonth(),
						assignment.getSapId(), assignment.getSubject(),
						assignment.getFacultyId(), assignment.getFacultyId(),
						assignment.getRevaluationScore(),
						assignment.getRemarks(), assignment.getReason() });
	} 
	@Transactional(readOnly = false)
	public void reEvaluateAssignmentQuickTable(AssignmentFileBean assignment) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.quick_assignmentsubmission set revaluated = 'Y', revaluationScore = ? , revaluationRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getRevaluationScore(),
						assignment.getRevaluationRemarks(),
						assignment.getFacultyId(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() }); 
	} 
	@Transactional(readOnly = true)
    public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try {
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			student = (StudentExamBean) jdbcTemplate.queryForObject(sql,
					new Object[] { sapid, sapid }, new BeanPropertyRowMapper(
							StudentExamBean.class));

			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		} catch (Exception e) {
			
		}
		return student;
	}
	@Transactional(readOnly = false)
	public void upsertAssignmentMarks(StudentMarksBean marksBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		marksDao.upsertMarks(marksBean, jdbcTemplate, "Assignment");
	}
	@Transactional(readOnly = false)
	public void changeEvaluationCount(AssignmentFileBean assignment,
			String evalutionCount) {
		String sql = " update exam.assignmentsubmission set evaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and facultyId = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

		sql = " update exam.assignmentsubmission set faculty2EvaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and faculty2 = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

		sql = " update exam.assignmentsubmission set faculty3EvaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and faculty3 = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

	}
	
	@Transactional(readOnly = false)
	public void changeEvaluationCountQuickTable(AssignmentFileBean assignment,
			String evalutionCount) {
		String sql = " update exam.quick_assignmentsubmission set evaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and facultyId = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

		sql = " update exam.quick_assignmentsubmission set faculty2EvaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and faculty2 = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

		sql = " update exam.quick_assignmentsubmission set faculty3EvaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? and faculty3 = ? ";

		jdbcTemplate.update(
				sql,
				new Object[] { evalutionCount, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject(), assignment.getFacultyId() });

	}
		
 
	@Transactional(readOnly = false)
    public void changeSubmissionCount(AssignmentFileBean assignment,
			int attempts) {
		String sql = " update exam.assignmentsubmission set attempts = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ?  ";

		jdbcTemplate.update(
				sql,
				new Object[] { attempts, assignment.getYear(),
						assignment.getMonth(), assignment.getSapId(),
						assignment.getSubject() });
	}

	@Transactional(readOnly = false)
	public void resetFacultyAssignmentAllocation(AssignmentFileBean searchBean) {

		String sql = "";
		if ("1".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.assignmentsubmission set facultyId = null "
					+ " where year =? and month = ? and facultyId = ? and subject = ? and ( evaluated = 'N' or evaluated is null) ";
		} else if ("2".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.assignmentsubmission set faculty2 = null "
					+ " where year =? and month = ? and faculty2 = ? and subject = ? and ( faculty2Evaluated = 'N' or faculty2Evaluated is null) ";
		} else if ("3".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.assignmentsubmission set faculty3 = null "
					+ " where year =? and month = ? and faculty3 = ? and subject = ? and ( faculty3Evaluated = 'N' or faculty3Evaluated is null) ";
		}

		int rowsUpdated = jdbcTemplate.update(sql,
				new Object[] { searchBean.getYear(), searchBean.getMonth(),
						searchBean.getFacultyId(), searchBean.getSubject() });


	}
	@Transactional(readOnly = false)
	public void resetFacultyAssignmentAllocationQuickTable(AssignmentFileBean searchBean) {

		String sql = "";
		if ("1".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.quick_assignmentsubmission set facultyId = null "
					+ " where year =? and month = ? and facultyId = ? and subject = ? and ( evaluated = 'N' or evaluated is null) ";
		} else if ("2".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.quick_assignmentsubmission set faculty2 = null "
					+ " where year =? and month = ? and faculty2 = ? and subject = ? and ( faculty2Evaluated = 'N' or faculty2Evaluated is null) ";
		} else if ("3".equalsIgnoreCase(searchBean.getLevel())) {
			sql = " update exam.quick_assignmentsubmission set faculty3 = null "
					+ " where year =? and month = ? and faculty3 = ? and subject = ? and ( faculty3Evaluated = 'N' or faculty3Evaluated is null) ";
		}

		int rowsUpdated = jdbcTemplate.update(sql,
				new Object[] { searchBean.getYear(), searchBean.getMonth(),
						searchBean.getFacultyId(), searchBean.getSubject() });


	}

 
	@Transactional(readOnly = false)
    public void revisitAssignment(AssignmentFileBean assignment) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.assignmentsubmission set revisited = 'Y', revisitScore = ? , revisitRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getRevisitScore(),
						assignment.getRevisitRemarks(),
						assignment.getFacultyId(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() });

		sql = "insert into exam.assignmentevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate,lastModifiedBy, lastModifiedDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(),?,sysdate(), ?, ?,?)";

		jdbcTemplate
				.update(sql,
						new Object[] { assignment.getYear(),
								assignment.getMonth(), assignment.getSapId(),
								assignment.getSubject(),
								assignment.getFacultyId(),
								assignment.getFacultyId(),
								assignment.getRevisitScore(),
								assignment.getRevisitRemarks(),
								assignment.getReason() });

	}
	@Transactional(readOnly = false)
    public void revisitAssignmentQuickTable(AssignmentFileBean assignment) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.quick_assignmentsubmission set revisited = 'Y', revisitScore = ? , revisitRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(
				sql,
				new Object[] { assignment.getRevisitScore(),
						assignment.getRevisitRemarks(),
						assignment.getFacultyId(), assignment.getYear(),
						assignment.getMonth(), assignment.getSubject(),
						assignment.getSapId() }); 

	}
 
	@Transactional(readOnly = true)
    public String getMostRecentAssignmentSubmissionPeriod() {

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where assignmentLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	@Transactional(readOnly = true)
	public String getMostRecentOfflineAssignmentSubmissionPeriod() {

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order "
				+ " = (Select max(examorder.order) from exam.examorder where offlineAssignmentLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	@Transactional(readOnly = true)
	public ExamOrderExamBean getAssignmentLiveCycle(){

		String recentPeriod = null;
		final String sql = "Select * from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where assignmentLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		ExamOrderExamBean exam = (ExamOrderExamBean)jdbcTemplate.queryForObject(sql, new Object[]{}, new BeanPropertyRowMapper(ExamOrderExamBean.class));

		return exam;
	}
	@Transactional(readOnly = true)
	public boolean isValidFaculty(String facultyId, AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String level = bean.getLevel();
		String sql = "";

		if ("1".equals(level)) {
			sql = "Select count(*) from exam.assignmentsubmission where year = ? and month = ? and subject = ? "
					+ " and ( faculty2 = ?  or faculty3 = ? or facultyIdRevaluation = ? ) ";
		} else if ("2".equals(level)) {
			sql = "Select count(*) from exam.assignmentsubmission where year = ? and month = ? and subject = ? "
					+ " and ( facultyId = ?  or faculty3 = ? or facultyIdRevaluation = ? ) ";
		} else if ("3".equals(level)) {
			sql = "Select count(*) from exam.assignmentsubmission where year = ? and month = ? and subject = ? "
					+ " and ( facultyId = ?  or faculty2 = ? or facultyIdRevaluation = ? ) ";
		}

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {
				bean.getYear(), bean.getMonth(), bean.getSubject(), facultyId,
				facultyId, facultyId },Integer.class);

		if (count == 0) {
			return true;
		} else {
			return false;
		}

	}
	@Transactional(readOnly = true)
	public boolean isValidFacultyForReval(AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from exam.assignmentsubmission where year = ? and month = ? and subject = ? and sapid = ? "
				+ " and ( facultyId = ?  or faculty2 = ? or faculty3 = ? or facultyIdRevaluation = ? ) ";

		int count = (int) jdbcTemplate.queryForObject(
				sql,
				new Object[] { bean.getYear(), bean.getMonth(),
						bean.getSubject(), bean.getSapId(),
						bean.getFacultyId(), bean.getFacultyId(),
						bean.getFacultyId(), bean.getFacultyId() },Integer.class);

		if (count == 0) {
			return true;
		} else {
			return false;
		}

	}
	@Transactional(readOnly = true)
	public boolean isValidFacultyForProjectReval(AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from exam.projectsubmission where year = ? and month = ?  and sapid = ? "
				+ " and ( facultyId = ?   ) ";

		int count = (int) jdbcTemplate.queryForObject(sql,
				new Object[] { bean.getYear(), bean.getMonth(),
						bean.getSapId(), bean.getFacultyId(), },Integer.class);

		if (count == 0) {
			return true;
		} else {
			return false;
		}

	}
	@Transactional(readOnly = false)
	public void markCopyCases(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String commaSeparatedSapIds = generateCommaSeparatedList(searchBean
				.getSapIdList());
		/*
		 * String sql =
		 * "Update exam.assignmentsubmission set toBeEvaluated = 'N', finalScore = '0' , finalReason = 'Copy Case-Other Student' "
		 * + " where year = ? and month = ? and subject = ?  and sapid in  ( "+
		 * commaSeparatedSapIds +" ) ";
		 */

		String sql = "Update exam.assignmentsubmission set toBeEvaluated = 'N', score = '0', finalScore = '0' , reason = 'Copy Case', finalReason = 'Copy Case' "
				+ " where year = ? and month = ? and subject = ?  and sapid in  ( "
				+ commaSeparatedSapIds + " ) ";

		jdbcTemplate.update(sql, new Object[] { searchBean.getYear(),
				searchBean.getMonth(), searchBean.getSubject() });
	}
	@Transactional(readOnly = false)
	public void markCopyCasesInQuickTable(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String commaSeparatedSapIds = generateCommaSeparatedList(searchBean
				.getSapIdList());
		/*
		 * String sql =
		 * "Update exam.assignmentsubmission set toBeEvaluated = 'N', finalScore = '0' , finalReason = 'Copy Case-Other Student' "
		 * + " where year = ? and month = ? and subject = ?  and sapid in  ( "+
		 * commaSeparatedSapIds +" ) ";
		 */

		String sql = "Update exam.quick_assignmentsubmission set toBeEvaluated = 'N', score = '0', finalScore = '0' , reason = 'Copy Case', finalReason = 'Copy Case' "
				+ " where year = ? and month = ? and subject = ?  and sapid in  ( "
				+ commaSeparatedSapIds + " ) ";

		jdbcTemplate.update(sql, new Object[] { searchBean.getYear(),
				searchBean.getMonth(), searchBean.getSubject() });
	}
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+",
				",");
		if (commaSeparatedList.endsWith(",")) {
			commaSeparatedList = commaSeparatedList.substring(0,
					commaSeparatedList.length() - 1);
		}
		return commaSeparatedList;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> searchCopyCases(int pageNo, int pageSize,
			AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.assignmentsubmission a, exam.students b where a.sapid = b.sapid and  finalReason = 'Copy Case' ";
		String countSql = "SELECT count(*) FROM exam.assignmentsubmission a, exam.students b where a.sapid = b.sapid and finalReason = 'Copy Case' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		sql = sql + " order by subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getMeanStdDev(String level) {
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);

		if ("1".equalsIgnoreCase(level)) {
			sql = "SELECT year, month, sapid , subject, facultyId,count(sapid) as populationCount, "
					+ " avg(score) as mean , stddev(score) as stddev , ((score-avg(score))/stddev(score)) "
					+ " FROM exam.assignmentsubmission where year = 2015 and month = 'Dec' and facultyId is not null and facultyId <> '' "
					+ " and tobeEvaluated <> 'N' "
					+ " and (score + faculty2score) > 0 "
					+ " group by facultyId, subject order by sapid asc";
		} else if ("2".equalsIgnoreCase(level)) {
			sql = "SELECT year, month, sapid , subject, faculty2, count(sapid) as populationCount,"
					+ " avg(faculty2score) as mean, stddev(faculty2score) as stddev, ((faculty2score-avg(faculty2score))/stddev(faculty2score))"
					+ " FROM exam.assignmentsubmission where year = 2015 and month = 'Dec' and faculty2 is not null and faculty2 <> '' "
					+ " and tobeEvaluated <> 'N' "
					+ " and (score + faculty2score) > 0 "
					+ " group by faculty2, subject order by sapid asc";
		} else if ("3".equalsIgnoreCase(level)) {
			sql = "SELECT year, month, sapid , subject, faculty3, count(sapid) as populationCount,"
					+ " avg(faculty3score) as mean, stddev(faculty3score) as stddev, ((faculty3score-avg(faculty3score))/stddev(faculty3score))"
					+ " FROM exam.assignmentsubmission where year = 2015 and month = 'Dec' and faculty3 is not null and faculty3 <> '' "
					+ " and tobeEvaluated <> 'N' "
					+ " and (score + faculty3score) > 0 "
					+ " group by faculty3, subject order by sapid asc";
		}

		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, new Object[] {}, new BeanPropertyRowMapper(
						AssignmentFileBean.class));

		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getMeanStdDevPerSubjectFaculty(
			AssignmentFileBean searchBean) {
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);

		sql = "SELECT year, month, sapid , subject, facultyId,count(sapid) as populationCount, "
				+ " avg(score) as mean , stddev(score) as stddev , ((score-avg(score))/stddev(score)) "
				+ " FROM exam.assignmentsubmission where year = ? and month = ? and facultyId is not null and facultyId <> '' "
				+ " and tobeEvaluated <> 'N' and program = 'All' "
				+ " group by facultyId, subject order by sapid asc";

		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql,
						new Object[] { searchBean.getYear(),
								searchBean.getMonth() },
						new BeanPropertyRowMapper(AssignmentFileBean.class));

		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getMeanStdDevPerSubject(
			AssignmentFileBean searchBean) {
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*
		 * sql =
		 * "Select subject,  (sum(mean * populationCount)/sum(populationCount)) as weightedMean, "
		 * +
		 * " (sum(stddev * populationCount)/sum(populationCount)) as weightedstddev from "
		 * +
		 * " (SELECT year, month, sapid , subject, facultyId,count(sapid) as populationCount, "
		 * + " avg(score) as mean , stddev(score) as stddev ," +
		 * " ((score-avg(score))/stddev(score)) " +
		 * " FROM exam.assignmentsubmission where " +
		 * " year = ? and month = ? and facultyId is not null and " +
		 * " facultyId <> ''  and tobeEvaluated <> 'N' " +
		 * " group by facultyId, subject order by subject asc) a group by subject"
		 * ;
		 */

		sql = "SELECT  subject, count(sapid) as populationCount, "
				+ " avg(score) as mean , stddev(score) as stddev , ((score-avg(score))/stddev(score)) "
				+ " FROM exam.assignmentsubmission where year = ? and month = ? and facultyId is not null and facultyId <> '' "
				+ " and tobeEvaluated <> 'N' and program = 'All' "
				+ " group by  subject order by sapid asc";

		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql,
						new Object[] { searchBean.getYear(),
								searchBean.getMonth() },
						new BeanPropertyRowMapper(AssignmentFileBean.class));

		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAllAsignments() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission a, exam.students s where year = 2015 and month = 'Dec' "
				+ " and facultyId is not null and facultyId <> '' "
				+ " and faculty2 is not null and faculty2 <> ''"
				+ " and a.sapid = s.sapid ";
		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql, new Object[] {}, new BeanPropertyRowMapper(
						AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAllAsignmentsForLevel1(
			AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission a, (select * from exam.students ss where sem = (select max(sem) from exam.students where sapid = ss.sapid)) s where year = ? and month = ? "
				+ " and facultyId is not null and facultyId <> '' "
				+ " and a.sapid = s.sapid ";
		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>) jdbcTemplate
				.query(sql,
						new Object[] { searchBean.getYear(),
								searchBean.getMonth() },
						new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getCopyCases(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<AssignmentFileBean> assignments = new ArrayList<AssignmentFileBean>();
		sql = "Select * from exam.assignmentsubmission a, exam.students s where year = ? and month = ? "
				+ " and a.sapid = s.sapid "
				+ " and finalReason like '%Copy Case%' ";
		assignments = (List<AssignmentFileBean>) jdbcTemplate.query(sql,
				new Object[] { searchBean.getYear(), searchBean.getMonth() },
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return assignments;

	}
	@Transactional(readOnly = true)
	public int getPastCycleAssignmentAttempts(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.assignmentsubmission asb, exam.examorder eo where sapid = ? "
				+ " and subject = ? and asb.year = eo.year and asb.month = eo.month and eo.order >= 15.5"; // 15.5
																											// means
																											// April-2016
																											// onwards

		int numberOfAttempts = (int) jdbcTemplate.queryForObject(sql, new Object[] {
				sapId, subject },Integer.class);
		return numberOfAttempts;
	}
	@Transactional(readOnly = true)
	public boolean checkIfAssignmentFeesPaid(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// String sql =
		// "select count(*) from exam.assignmentpayment where sapid = ? "
		// + " and subject = ? and booked = 'Y' and year = ? and month = ? ";
		//Change made on 20/06/2017 :- take only resitAssignment flag into consideration since assignmentfees paid is only checked for failed attempt//
		String sql = "select count(*) from exam.assignmentpayment a , exam.examorder eo where "
				+ " a.year = eo.year and a.month = eo.month "
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where resitAssignmentLive='Y') "
				+ " and a.subject = ? and a.booked = 'Y' and a.sapid = ?";

		int bookingCount = (int) jdbcTemplate.queryForObject(sql, new Object[] {
				subject, sapId },Integer.class);
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	// Added additional clause by vikas on eoorder < MAX in order to exclude
	// current assignments from payment 01/04/2017//
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsNeedingAssignmentPayments(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select distinct asb.subject from exam.assignmentsubmission asb, exam.examorder eo "
				+ " where sapid = ? and asb.year = eo.year and asb.month = eo.month "
				+ " and eo.order >= 15.5 "
				+ " and subject in (Select subject from exam.passfail where isPass = 'N' and sapid = ? )"
				+ " group by subject " + " having count(subject)  >= 2 ";

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] { sapid, sapid }, new SingleColumnRowMapper(
						String.class));
		return subjectList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsMadeAssignmentPayments(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select distinct subject from exam.assignmentpayment ap,exam.examorder eo where sapid = ? and booked = 'Y'"
				+ " and ap.month = eo.month and ap.year = eo.year  and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y')";

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] { sapid }, new SingleColumnRowMapper(
						String.class));
		return subjectList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsMadeTestPaymentsForTestIdAndAttempt(String sapid,String testId,String attempt) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select distinct subject from exam.assignmentpayment ap,exam.examorder eo where sapid = ? and booked = 'Y'"
				+ " and ap.month = eo.month and ap.year = eo.year  and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y')"
				+ " and ap.testId =? and ap.testAttempt = ? ";

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] { sapid,testId,attempt }, new SingleColumnRowMapper(
						String.class));
		return subjectList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public String  upsertOnlineInitiationTransaction(String sapid,
			final List<ExamBookingTransactionBean> bookingList) {
		try {
			// Insert tracking numbers for current interaction
			String sql = " INSERT INTO exam.assignmentpayment " + "(sapid,"
					+ " subject," + " year, " + " month, " + " trackId, "
					+ " amount, " + " tranDateTime, " + " tranStatus, "
					+ " booked," + " program," + " sem," + " deviceType)"

					+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?)";

			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql,
					new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int i)
								throws SQLException {
							ExamBookingTransactionBean bean = bookingList
									.get(i);
							ps.setString(1, bean.getSapid());
							ps.setString(2, bean.getSubject());
							ps.setString(3, bean.getYear());
							ps.setString(4, bean.getMonth());
							ps.setString(5, bean.getTrackId());
							ps.setString(6, bean.getAmount());
							ps.setString(7, bean.getTranStatus());
							ps.setString(8, bean.getBooked());
							ps.setString(9, bean.getProgram());
							ps.setString(10, bean.getSem());
							ps.setString(11, bean.getDeviceType());

						}

						public int getBatchSize() {
							return bookingList.size();
						}
					});
			return "success";

		} catch (Exception e) {
			return e.getMessage();
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public void upsertOnlineInitiationTransactionWithTestIdAttempt(String sapid,
			final List<ExamBookingTransactionBean> bookingList) {
		try {
			// Insert tracking numbers for current interaction
			String sql = " INSERT INTO exam.assignmentpayment " + "(sapid,"
					+ " subject," + " year, " + " month, " + " trackId, "
					+ " amount, " + " tranDateTime, " + " tranStatus, "
					+ " booked," + " program," + " sem,testId,testAttempt)"

					+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?)";

			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql,
					new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int i)
								throws SQLException {
							ExamBookingTransactionBean bean = bookingList
									.get(i);
							ps.setString(1, bean.getSapid());
							ps.setString(2, bean.getSubject());
							ps.setString(3, bean.getYear());
							ps.setString(4, bean.getMonth());
							ps.setString(5, bean.getTrackId());
							ps.setString(6, bean.getAmount());
							ps.setString(7, bean.getTranStatus());
							ps.setString(8, bean.getBooked());
							ps.setString(9, bean.getProgram());
							ps.setString(10, bean.getSem());
							ps.setLong(11, bean.getTestId());
							ps.setLong(12, bean.getTestAttempt());

						}

						public int getBatchSize() {
							return bookingList.size();
						}
					});


		} catch (Exception e) {
			
		}

	}
	@Transactional(readOnly = false)
	public void updateSeatsForOnlineUsingSingleConnection(
			ExamBookingTransactionBean responseBean) throws Exception {

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {

			String sql = "Update exam.assignmentpayment"
					+ " set booked = 'Y' ," + " ResponseMessage = ? ,"
					+ " transactionID = ? ," + " requestID = ? ,"
					+ " merchantRefNo = ? ," + " secureHash = ? ,"
					+ " respAmount = ? ," + " description = ? ,"
					+ " responseCode = ? ," + " respPaymentMethod = ? ,"
					+ " isFlagged = ? ," + " paymentID = ? ," + " error = ? ,"
					+ " respTranDateTime = ? ," + " tranStatus = '"
					+ ONLINE_PAYMENT_SUCCESSFUL + "',"
					+ " bookingCompleteTime = sysdate(), paymentOption=? "

					+ " where sapid = ? " + " and trackId = ? "
					+ " and booked <> 'Y' ";


			jdbcTemplate
					.update(sql,
							new Object[] { responseBean.getResponseMessage(),
									responseBean.getTransactionID(),
									responseBean.getRequestID(),
									responseBean.getMerchantRefNo(),
									responseBean.getSecureHash(),
									responseBean.getRespAmount(),
									responseBean.getDescription(),
									responseBean.getResponseCode(),
									responseBean.getRespPaymentMethod(),
									responseBean.getIsFlagged(),
									responseBean.getPaymentID(),
									responseBean.getError(),
									responseBean.getRespTranDateTime(),
									responseBean.getPaymentOption(),

									responseBean.getSapid(),
									responseBean.getTrackId() });

		} catch (Exception e) {
			
		}
	}
	
	@Transactional(readOnly = false)
	public int saveAssignmentPaymentTransactionError(ExamBookingTransactionBean responseBean){
		//Prepare SQL
		String UPDATE_SQL="update exam.assignmentpayment set error = ?, paymentOption = ? where sapid = ? and trackId = ? and booked <> 'Y'";
		
		//Execute query and return updated count.
		return jdbcTemplate.update(UPDATE_SQL,responseBean.getError(),responseBean.getPaymentOption(),responseBean.getSapid(),responseBean.getTrackId());
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllUnSuccessfulAssignmentPayments() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.assignmentpayment a, exam.examorder b, exam.students s where a.year = b.year and a.month = b.month "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where resitAssignmentLive='Y') "
				+ " and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800) "
				+ " and a.booked <> 'Y' "
				+ " and a.tranStatus <> '"
				+ TRANSACTION_FAILED
				+ "'"
				+ " and a.sapid = s.sapid "
				+ " group by a.trackId " + " order by a.tranDateTime asc";
		

		// List<StudentMarksBean> studentMarksList = new
		// ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] {}, new BeanPropertyRowMapper(
						ExamBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = false)
	public List<ExamBookingTransactionBean> updateSeatsForConflictUsingSingleConnection(
			ExamBookingTransactionBean responseBean) throws Exception {

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {

			String sql = "Update exam.assignmentpayment"
					+ " set booked = 'Y' ," + " ResponseMessage = ? ,"
					+ " transactionID = ? ," + " requestID = ? ,"
					+ " merchantRefNo = ? ," + " secureHash = ? ,"
					+ " respAmount = ? ," + " description = ? ,"
					+ " responseCode = ? ," + " respPaymentMethod = ? ,"
					+ " isFlagged = ? ," + " paymentID = ? ," + " error = ? ,"
					+ " respTranDateTime = ? ," + " tranStatus = '"
					+ ONLINE_PAYMENT_MANUALLY_APPROVED + "',"
					+ " bookingCompleteTime = sysdate(), paymentOption=? "

					+ " where sapid = ? " + " and trackId = ?";


			jdbcTemplate
					.update(sql,
							new Object[] { responseBean.getResponseMessage(),
									responseBean.getTransactionID(),
									responseBean.getRequestID(),
									responseBean.getMerchantRefNo(),
									responseBean.getSecureHash(),
									responseBean.getRespAmount(),
									responseBean.getDescription(),
									responseBean.getResponseCode(),
									responseBean.getRespPaymentMethod(),
									responseBean.getIsFlagged(),
									responseBean.getPaymentID(),
									responseBean.getError(),
									responseBean.getRespTranDateTime(),
									responseBean.getPaymentOption(),

									responseBean.getSapid(),
									responseBean.getTrackId() });

			
		} catch (Exception e) {
			
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentpayment "
				+ " where sapid = ? " + " and trackId = ? ";

		List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(
				sql,
				new Object[] { responseBean.getSapid(),
						responseBean.getTrackId() }, new BeanPropertyRowMapper(
						ExamBookingTransactionBean.class));
		
		try {
			logger.info("calling assg temp table update qry..");
			//Prepare SQL query 
			String UPDATE_QUICK_ASSES = "update exam.quick_assignmentsubmission set paymentDone='Y' where sapid = ? and subject = ? "
					+ " and year = ? and month = ? and paymentApplicable='Y' ";
			
			//Update the payment done status in the quick assignment submission table.
			jdbcTemplate.update(UPDATE_QUICK_ASSES,completeBookings.get(0).getSapid(),completeBookings.get(0).getSubject(),
					completeBookings.get(0).getYear(),completeBookings.get(0).getMonth());
		}
		catch(Exception e) {
			logger.info("auto booking scheduler error in updating assg temp table>"+e.getMessage());
		}
		return completeBookings;
	}
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentWithValidity(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try {
			String sql = "select * from exam.students s, exam.examorder eo where"
					+ "		s.sapid = ?"
					+ "     and s.sem = (Select max(sem) from exam.students where sapid = ? )"
					+ " 	and s.validityendyear = eo.year"
					+ " 	and s.validityendmonth = eo.month"
					+ "		and eo.order >= (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

			student = (StudentExamBean) jdbcTemplate.queryForObject(sql,
					new Object[] { sapid, sapid }, new BeanPropertyRowMapper(
							StudentExamBean.class));

		} catch (Exception e) {
		}
		return student;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public void markTransactionsFailed(
			final List<ExamBookingTransactionBean> bookingList) {
		try {
			// Insert tracking numbers for current interaction
			String sql = "Update exam.assignmentpayment "
					+ " set tranStatus = '" + TRANSACTION_FAILED + "'"
					+ " where trackid = ? " + " and booked = 'N'";

			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql,
					new BatchPreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps, int i)
								throws SQLException {
							ExamBookingTransactionBean bean = bookingList
									.get(i);
							ps.setString(1, bean.getTrackId());
						}

						public int getBatchSize() {
							return bookingList.size();
						}
					});


		} catch (Exception e) {
			
		}

	}
	
	@Transactional(readOnly = false)
	public void markTransactionsFailed(ExamBookingTransactionBean bean) {
		try {
			String sql = "Update exam.assignmentpayment "
					+ " set tranStatus = '" + TRANSACTION_FAILED + "', error=?, paymentOption=? "
					+ " where trackid = ? " + " and booked = 'N'";
			
			int updatedCount = jdbcTemplate.update(sql,bean.getError(),bean.getPaymentOption(),bean.getTrackId());

		} catch (Exception e) {
			
		}

	}
	
	@Transactional(readOnly = false)
	public void updatePendingTxnDetails(ExamBookingTransactionBean bean) {
		try {
			String sql = "Update exam.assignmentpayment "
					+ " set error=?, paymentOption=? "
					+ " where trackid = ? " + " and booked = 'N'";
			
			int updatedCount = jdbcTemplate.update(sql,bean.getError(),bean.getPaymentOption(),bean.getTrackId());

		} catch (Exception e) {
			
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFailedSubjectsSubmittedInLastCycle(String sapId, ArrayList<String> subjects) {
		try {
			/*String subjectCommaSeparated = "";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}*/
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "SELECT subject FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where resitAssignmentLive = 'Y') "
					+ " and sapid = ?  ";
				//	+ " and subject in (" + subjectCommaSeparated + ") ";
			
			/*String currentResitAssignmentLiveCycle = getLiveResitAssignmentMonth() + getLiveResitAssignmentYear();
			
			String sql = "SELECT subject FROM exam.assignmentsubmission a where concat(a.month,a.year) <> ? "
					+ " and sapid = ?  "
					+ " and subject in (" + subjectCommaSeparated + ") ";*/
			
			
			ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
			if(subjectsList == null){
				subjectsList = new ArrayList<String>();
			}
			
			return subjectsList;
		} catch (Exception e) {
			
		}
		
		return new ArrayList<String>(); 
		
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getFailedSubjectsExamBookedInLastCycle(String sapId, ArrayList<String> subjects) {
		try {
			/*String subjectCommaSeparated = "";
			for (int i = 0; i < subjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjects.get(i).replaceAll("'", "''") + "'";
				}
			}*/
			
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "SELECT subject FROM exam.exambookings eb, exam.examorder eo where eb.month = eo.month and eb.year = eo.year "
					+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where resitAssignmentLive = 'Y') "
					+ " and eb.sapid = ? and eb.booked = 'Y' ";
			
					//+ " and eb.subject in (" + subjectCommaSeparated + ") ";
			
			
			ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
			if(subjectsList == null){
				subjectsList = new ArrayList<String>();
			}
			
			return subjectsList;
		} catch (Exception e) {
			
		}
		
		return new ArrayList<String>(); 
		
	}
	@Transactional(readOnly = false)
	public void updateExtendedAssignmentSubmission(AssignmentFileBean bean,String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO exam.extended_assignment_submission (sapid, subject, createdBy, createdDate , lastModifiedBy , lastModifiedDate ) "
				+ " Values (?,?,?,sysdate(),?,sysdate())"
				+ " on duplicate key update"
				+ " sapid = ? , subject = ? , lastModifiedBy= ? , lastModifiedDate= sysdate()";
		
		jdbcTemplate.update(sql, new Object[] { 
				bean.getSapId().trim(),
				bean.getSubject(),
				userId,
				userId,
				
			bean.getSapId().trim(),
			bean.getSubject(),
			userId 
			
		});
	}   
	@Transactional(readOnly = false) 
	public int deleteExtendedAssignmentSubmission(AssignmentFileBean bean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		String sql = "delete from  exam.extended_assignment_submission "
				+ " where id=? ";
		
		int row = jdbcTemplate.update(sql, new Object[] { 
				bean.getSapId().trim(),
				bean.getSubject(),
		});
		return row;
		
	}   
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> assignmentAttemptsSearch(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select * from  exam.extended_assignment_submission ";
		List<AssignmentFileBean> timeExtendedStudentList = jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(AssignmentFileBean.class));
		return timeExtendedStudentList;
	
	}
	@Transactional(readOnly = true)
	public ArrayList<String> assignmentExtendedSubmissionTime(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select concat(sapid,subject) as extendedAssignmentTime from  exam.extended_assignment_submission  ";
		ArrayList<String> timeExtendedStudentList = (ArrayList<String>)jdbcTemplate.query(sql,new Object[] {},new SingleColumnRowMapper(String.class));
		return timeExtendedStudentList;
	
	}
	@Transactional(readOnly = true)
	public ArrayList<AssignmentFileBean> getResultAwaitedAssignmentSubmittedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission where sapid = ? and concat(sapid,subject) not in (select concat(sapid,subject) from exam.passfail where sapid = ?)";
		
		ArrayList<AssignmentFileBean> assignmentList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, 
				new Object[] {sapid,sapid},	new BeanPropertyRowMapper(AssignmentFileBean.class));
		
		return assignmentList;
	}
	
	//added to make missing entries in assignment status table: START
	
		public List<AssignmentFileBean> getMissingSapIdsList(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "select distinct a.subject,a.year,a.month,a.sapid,a.createdBy,a.createdDate,a.lastModifiedBy,a.lastModifiedDate from exam.assignmentsubmission a " + 
					"				  where CONCAT(a.month,a.year,a.sapid,a.subject) " + 
					"				  not in (select CONCAT(b.examMonth,b.examYear,b.sapid,b.subject) from  exam.assignmentstatus b) ";
			List<AssignmentFileBean> missingsSapIdsList = jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(AssignmentFileBean.class));
			return missingsSapIdsList;
		
		}
		
		
		@Transactional(readOnly = false)
		public boolean insertIntoAssignmentStatusMissingSapIds(AssignmentFileBean assignmentFileBean) {
				jdbcTemplate = new JdbcTemplate(dataSource);

				String sql = "INSERT INTO exam.assignmentstatus "
						+ "(examMonth, examYear,submitted, sapid, subject, createdBy, createdDate , lastModifiedBy , lastModifiedDate)"
						+ " VALUES " + "(?,?,'Y',?,?,?,?, ?,? )" ;

			
				try {
				
				jdbcTemplate.update(sql, new Object[] {assignmentFileBean.getMonth(),
						assignmentFileBean.getYear(),assignmentFileBean.getSapId(),
						assignmentFileBean.getSubject(),assignmentFileBean.getCreatedBy(),
						assignmentFileBean.getCreatedDate(),assignmentFileBean.getLastModifiedBy(),
						assignmentFileBean.getLastModifiedDate()});

				return true;
				}
				catch(Exception e) {
					
				}
				
				return false;
				
			}

		
		//END
		//project submission checks start
		@Transactional(readOnly = true)
		public ArrayList<AssignmentFileBean> getResultAwaitedProjectSubmittedList(String sapid) {
			try{
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "SELECT * FROM exam.projectsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where projectSubmissionLive = 'Y') "
					+ " and sapid = ?";
			ArrayList<AssignmentFileBean> projectList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, 
					new Object[] {sapid},	new BeanPropertyRowMapper(AssignmentFileBean.class));
			if(projectList == null){
				projectList = new ArrayList<AssignmentFileBean>();
			}
			return projectList;
			}catch(Exception e){
				
			}
			return new ArrayList<AssignmentFileBean>();
		}
		@Transactional(readOnly = true)
		public ArrayList<String> getAllResultAwaitedProjectList() {
			try{
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "SELECT distinct sapid FROM exam.exambookings a, exam.examorder eo "
					+ " where subject IN ('Project', 'Module 4 - Project') and a.booked = 'Y' and a.month = eo.month and a.year = eo.year  "
					+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where projectSubmissionLive = 'Y') ";
			ArrayList<String> projectList = (ArrayList<String>)jdbcTemplate.query(sql, 
					new Object[] {},new SingleColumnRowMapper(String.class));
			if(projectList == null){
				projectList = new ArrayList<String>();
			}
			return projectList;
			}catch(Exception e){
				
			}
			return new ArrayList<String>();
		}
		@Transactional(readOnly = true)
		public ArrayList<ExamBookingExamBean> getProjectExamBookedInLastCycleButNotSubmitted(String sapId) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				
				String sql = "SELECT eb.* FROM exam.exambookings eb, exam.examorder eo"
						+ " where"
						+ " eb.month = eo.month and eb.year = eo.year"
						+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where projectSubmissionLive = 'Y')"
						+ " and eb.sapid = ? "
						+ " and eb.subject IN ('Project', 'Module 4 - Project')"
						+ " and eb.booked = 'Y'"
						+ " and concat(eb.sapid,eb.subject,eb.year,eb.month) "
						+ " not in (select concat(ps.sapid,ps.subject,ps.year,ps.month)"
						+ "           from exam.projectSubmission ps,exam.examorder eo"
						+ "           where ps.month = eo.month and ps.year = eo.year"
						+ "           and eo.order = (select max(examorder.order)-0.5 from exam.examorder where projectSubmissionLive = 'Y')"
						+ "           and ps.sapid = ? and ps.subject IN ('Project', 'Module 4 - Project'))";
				
				ArrayList<ExamBookingExamBean> subjectsList = (ArrayList<ExamBookingExamBean>)jdbcTemplate.query(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(ExamBookingExamBean.class));
				if(subjectsList == null){
					subjectsList = new ArrayList<ExamBookingExamBean>();
				}
				return subjectsList;
			} catch (Exception e) {
				
			}
			
			return new ArrayList<ExamBookingExamBean>(); 
			
		}
		//project submission checks end
		@Transactional(readOnly = true)
		public ArrayList<ExamBookingTransactionBean> getSccessfulAssignmentPayment(){
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ExamBookingTransactionBean> assignmentBookingList = null;
			
			String sql =  " select *, count(*) as bookedCount from exam.assignmentpayment a, exam.examorder eo "
					+ "	where a.year = eo.year and a.month = eo.month "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y' "
					+ " or resitAssignmentLive ='Y') "
					+ " and tranStatus = '"+ONLINE_PAYMENT_SUCCESSFUL+"' "
					+ " and a.sentEmailTwicePayment = 'N' "
					+ " group by subject, sapid having count(*) > 1 ";
			
			try {
				assignmentBookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[] {}, 
						new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
				
			} catch (Exception e) {
				
			}
			
			return assignmentBookingList;
			
		}
		@Transactional(readOnly = false)
		public void updateSentEmailForTwiceAssignmentFess(ExamBookingTransactionBean examBean){
			jdbcTemplate = new JdbcTemplate(dataSource);
						
			String sql =  " update exam.assignmentpayment SET sentEmailTwicePayment='Y'"
						+ " where year = ? and month = ? and sapid = ? "
						+ " and subject = ? ";
			
			try {
				jdbcTemplate.update(sql, new Object[] {examBean.getYear(), examBean.getMonth(), examBean.getSapid(), examBean.getSubject()});
			} catch (Exception e) {
				
			}
			
		}
		


	
	
	//for Getting AssignmentKey From Consumer Id and Programs Id
	@Transactional(readOnly = true)
	public String getAssignmentKey(String programId,String programStructureId, String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String assignmentKey = null;
		
		String sql =  "SELECT id FROM exam.consumer_program_structure where consumerTypeId = ? and programId = ? and programStructureId = ?";
		
		try {
			
		assignmentKey = (String) jdbcTemplate.queryForObject(sql,new Object [] {consumerTypeId,programId,programStructureId},String.class);
			
			
			
		} catch (Exception e) {
			
		}
		
		return assignmentKey;
		
	}
	

	
	

	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramStructureByConsumerType(String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsStructureByConsumerType = null;
		
		String sql =  "select p_s.program_structure as name,p_s.id as id "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "where c_p_s.consumerTypeId = ? group by p_s.id";
		
		try {
			programsStructureByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsStructureByConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerType(String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
		
		String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
				+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
				+ "where c_p_s.consumerTypeId = ? group by p.id";
		
		try {
			programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsByConsumerType;  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
		
		String sql =  "select p_s_s.subject as name,id from  exam.program_sem_subject as p_s_s "
				+ "where p_s_s.consumerProgramStructureId in "
				+ "(select c_p_s.id as id from consumer_program_structure as c_p_s where c_p_s.programId in("+programId+") "
						+ "and c_p_s.programStructureId in("+programStructureId+") and c_p_s.consumerTypeId in("+consumerTypeId+")) group by p_s_s.subject";
		
		try {
			programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsByConsumerType;  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerTypeAndPrgmStructure = null;
		
		String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
				+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
				+ "where c_p_s.consumerTypeId = ? and c_p_s.programStructureId in ("+ programStructureId+") group by p.id";
		
		try {
			programsByConsumerTypeAndPrgmStructure = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsByConsumerTypeAndPrgmStructure;  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeList(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> ConsumerType = null;
		String sql =  "SELECT id,name FROM exam.consumer_type";
		
		try {
			ConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return ConsumerType;  
		
	}
	@Transactional(readOnly = true)
	public List<String> getListByConsumerTypeId(String consumerTypeId){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select id from exam.consumer_program_structure where consumerTypeId=?";
			return jdbcTemplate.query(sql,new Object[] {consumerTypeId},new SingleColumnRowMapper(String.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	@Transactional(readOnly = true)
	public List<String> getListByConsumerTypeProgramStructure(String consumerTypeId,String programStructureId){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select id from exam.consumer_program_structure where consumerTypeId=? and programStructureId=?";
			return jdbcTemplate.query(sql,new Object[] {consumerTypeId,programStructureId},new SingleColumnRowMapper(String.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsList(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> subjectList = null;
		String sql =  "SELECT subjectname FROM exam.subjects";
		
		try {
			subjectList = (ArrayList<String>) jdbcTemplate.query(sql, 
					new SingleColumnRowMapper(String.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return subjectList;  
		
	}
	
	//////////////////////////
	//For Inserting data into DB If Any Option Is Selected Id "All"(Assignment Upload)
	//Start
	
	//Fetch consumerProgramStructureId's From consumer_program_structure Based On Values selected in (programId,ProgramStructureId,ConsumerId)

	
	@Transactional(readOnly = true)
	public ArrayList<String>  getconsumerProgramStructureIds(String programId,String programStructureId, String consumerTypeId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  "SELECT id FROM exam.consumer_program_structure "
				+ "where programId in ("+ programId +") and "
				+ "programStructureId in ("+ programStructureId +") and "
				+ "consumerTypeId in ("+ consumerTypeId +")";

		ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
				sql,  new SingleColumnRowMapper(
						String.class));
		
		
		return consumerProgramStructureIds;
	}
	@Transactional(readOnly = true)
	public ArrayList<String>  getConsumerProgramStructureIdsForANS(String programId,String programStructureId, String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT DISTINCT " + 
					"    (cps.id) " + 
					" FROM " + 
					"    exam.consumer_program_structure cps " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON cps.id = pss.consumerProgramStructureId" + 
					"		AND programId in ("+ programId +") " +
					"		AND programStructureId in ("+ programStructureId +") " +
					"		AND consumerTypeId in ("+ consumerTypeId +") " + 
					"		AND pss.hasAssignment = 'Y' ";

		ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		
		
		return consumerProgramStructureIds;
	}
	@Transactional(readOnly = true)
	public ArrayList<String>  getconsumerProgramStructureIdsWithSubject(String programId,String programStructureId, String consumerTypeId, String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  "SELECT c_p_s.id FROM exam.consumer_program_structure as c_p_s, "
				+ "exam.program_sem_subject as p_s_s "
				+ "where c_p_s.programId in ("+ programId +") "
				+ "and c_p_s.programStructureId in ("+ programStructureId +") "
				+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
				+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
				+ "and p_s_s.subject=?";

		ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] {subject},  new SingleColumnRowMapper(
						String.class));
		
		
		return consumerProgramStructureIds;
	}
	
	
	
	//Insert Data In Table Via BatchUpdate
	
	@Transactional(readOnly = false)
	public void batchInsertOfAssignmentsIfAll(final AssignmentFileBean bean,final String year,final String month,final List<String> consumerProgramStructureIds){
		String sql = " INSERT INTO exam.assignments (year, month, subject, startDate, endDate, instructions, "
				+ " filePath, questionFilePreviewPath, createdBy, createdDate, lastModifiedBy, lastModifiedDate, program, consumerProgramStructureId) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?)"
				+ " on duplicate key update "
				+ "	    startDate = ?,"
				+ "	    endDate = ?,"
				+ "	    instructions = ?,"
				+ "	    filePath = ?,"
			 	+ "	    questionFilePreviewPath = ?,"
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate(), "
				+ "	    consumerProgramStructureId = ? ";
		
		int[] batchInsertOfAssignmentsIfAll = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				
				ps.setString(1,year);
				ps.setString(2,month);
				ps.setString(3,bean.getSubject());
				ps.setString(4,bean.getStartDate());
				ps.setString(5,bean.getEndDate());
				ps.setString(6,bean.getInstructions());
				ps.setString(7,bean.getFilePath());
				ps.setString(8,bean.getQuestionFilePreviewPath());
				ps.setString(9,bean.getCreatedBy());
				ps.setString(10,bean.getLastModifiedBy());
				ps.setString(11,"All");
				ps.setString(12,consumerProgramStructureIds.get(i));
				
				//On Update
				ps.setString(13,bean.getStartDate());
				ps.setString(14,bean.getEndDate());
				ps.setString(15,bean.getInstructions());
				ps.setString(16,bean.getFilePath());
				ps.setString(17,bean.getQuestionFilePreviewPath());
				ps.setString(18,bean.getLastModifiedBy());
				ps.setString(19,consumerProgramStructureIds.get(i));
			
				
				
			}

			@Override
			public int getBatchSize() {
				return consumerProgramStructureIds.size();
			}
		  });
	}
	
	//Make Assignment Live Data Insert
	@Transactional(readOnly = false)
	public void batchInsertOfMakeAssignmentLive(final  AssignmentFilesSetbean bean, final ArrayList<String> consumerProgramStructureIds){
		String sql = "INSERT INTO exam.assignment_live_setting "
				+ "(acadsYear,acadsMonth,examYear,examMonth,liveType,consumerProgramStructureId) "
				+ "VALUES(?,?,?,?,?,?) "
				+ "on duplicate key update "
				+ "acadsYear=?, acadsMonth=?, examYear=?, examMonth=?";
		
		int[] batchInsertOfMakeAssignmentLiveIfAll = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1,bean.getAcadsYear());
				ps.setString(2,bean.getAcadsMonth());
				ps.setString(3,bean.getExamYear());
				ps.setString(4,bean.getExamMonth());
				ps.setString(5,bean.getLiveType());
				ps.setString(6,consumerProgramStructureIds.get(i));
				
				//On Update
				ps.setString(7,bean.getAcadsYear());
				ps.setString(8,bean.getAcadsMonth());
				ps.setString(9,bean.getExamYear());
				ps.setString(10,bean.getExamMonth());
				
				
				
			}

			@Override
			public int getBatchSize() {
				return consumerProgramStructureIds.size();
			}
		  });
	}
	@Transactional(readOnly = true)
	public ArrayList<AssignmentFilesSetbean> getAssignmentList(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<AssignmentFilesSetbean> assignmentList = null;
		
		;
		
		String sql =  "select a_l_s.acadsYear,a_l_s.acadsMonth,a_l_s.examYear,a_l_s.examMonth,a_l_s.liveType,p.code as program,"
				+ "p_s.program_structure as programStructure,c_t.name as consumerType "
				+ "from exam.assignment_live_setting as a_l_s "
				+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = a_l_s.consumerProgramStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId";
		
		try {
			assignmentList = (ArrayList<AssignmentFilesSetbean>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
		} catch (Exception e) {
			
			
			return null;
		}
		
		return assignmentList;  
		
	}
	
	
	//End
	//////////////////////////
	@Transactional(readOnly = false)
	public void insertIntoProgramSemSubject(ProgramSubjectMappingExamBean data) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into exam.program_sem_subject (consumerProgramStructureId,"
		+ "subject,sem,active,passScore,hasAssignment,assignmentNeededBeforeWritten,"
		+ "writtenScoreModel,assignmentScoreModel,isGraceApplicable,maxGraceMarks,createCaseForQuery,"
		+ "assignQueryToFaculty,sifySubjectCode,createdBy,createdDate,lastModifiedBy,lastModifiedDate,hasTest)"
		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";
		jdbcTemplate.update(sql,new Object[] {
		data.getConsumerProgramStructureId(),
		data.getSubject(),
		data.getSem(),
		data.getActive(),
		data.getPassScore(),
		data.getHasAssignment(),
		data.getAssignmentNeededBeforeWritten(),
		data.getWrittenScoreModel(),
		data.getAssignmentScoreModel(),
		data.getIsGraceApplicable(),
		data.getMaxGraceMarks(),
		data.getCreateCaseForQuery(),
		data.getAssignQueryToFaculty(),
		data.getSifySubjectCode(),
		data.getCreatedBy(),
		data.getLastModifiedBy(),
		data.getHasTest()
		});
		// return timeExtendedStudentList;
		}
	    @Transactional(readOnly = true)
		public ArrayList<String> getConsumerTypeId(String program,String prgmStructure) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select c_p_s.id as id from exam.consumer_program_structure as c_p_s where "
		+ "(c_p_s.programId,c_p_s.programStructureId) in "
		+ "( select p.id as programId,p_s.id as programStructureId from exam.program_subject as p_sub,"
		+ "exam.program as p,exam.program_structure as p_s where "
		+ "p.code = p_sub.program and "
		+ "p_s.program_structure = p_sub.prgmStructApplicable "
		+ "and p_sub.program = ? and p_sub.prgmStructApplicable = ?);";
		ArrayList<String> data = (ArrayList<String>)jdbcTemplate.query(sql,new Object[] {program,prgmStructure},new SingleColumnRowMapper(String.class));
		    return data;
		}
	    @Transactional(readOnly = true)
		public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectData() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject";
		ArrayList<ProgramSubjectMappingExamBean> data = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return data;
		}
		
		////////////////////////////////////////////////////////////////////
		//for populating assignments table with new logic(consumerPorgramStructureId)
		

		
			
	        @Transactional(readOnly = true)
			public ArrayList<AssignmentFileBean> getNewAssignmentsData() {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select * from exam.new_assignments";
				ArrayList<AssignmentFileBean> data = (ArrayList<AssignmentFileBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFileBean.class));
				return data;
			}
	        @Transactional(readOnly = false)
			public void insertIntoNewAssignments_Backup(AssignmentFileBean data) {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "insert into exam.assignments (year,month,subject,startDate,endDate,instructions,filePath,"
						+ "createdBy,createdDate,lastModifiedBy,lastModifiedDate,questionFilePreviewPath,program,consumerProgramStructureId) "
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				jdbcTemplate.update(sql,new Object[] {
						data.getYear(),
						data.getMonth(),
						data.getSubject(),
						data.getStartDate(),
						data.getEndDate(),
						data.getInstructions(), 
						data.getFilePath(),
						data.getCreatedBy(),
						data.getCreatedDate(),
						data.getLastModifiedBy(),
						data.getLastModifiedDate(),
						data.getQuestionFilePreviewPath(),
						"All",
						data.getConsumerProgramStructureId()
				});
				// return timeExtendedStudentList;
				}
			@Transactional(readOnly = true)
			public AssignmentLiveSetting getCurrentLiveAssignment(String consumerProgramStructureId,String liveType){
				try {
					jdbcTemplate = new JdbcTemplate(dataSource);
					
					String sql = "select * from exam.assignment_live_setting where consumerProgramStructureId = ? and liveType = ? limit 1";
					AssignmentLiveSetting assignmentLiveSetting = (AssignmentLiveSetting)jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId,liveType},new BeanPropertyRowMapper(AssignmentLiveSetting.class) );
					return assignmentLiveSetting;
				}
				catch (Exception e) {
					// TODO: handle exception
					
					return null;
				}
			
			}
		@Transactional(readOnly = true)
		public ArrayList<AssignmentFileBean> getAssignmentsData() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.assignments where program = 'All'";
			ArrayList<AssignmentFileBean> data = (ArrayList<AssignmentFileBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFileBean.class));
			return data;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getConsumerProgramStructureIdsNew(String subject) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select p_s_s.consumerProgramStructureId from exam.program_sem_subject p_s_s where "
					+ " p_s_s.subject = ? and p_s_s.consumerProgramStructureId in "
					+ " ( select c_p_s.id from exam.consumer_program_structure c_p_s left join "
					+ " exam.consumer_type c_t on c_t.id = c_p_s.consumerTypeId where c_t.id in (2,6,8) )";
			ArrayList<String> data = (ArrayList<String>)jdbcTemplate.query(sql,new Object[] {subject}, new SingleColumnRowMapper(String.class));
			    return data;
		}
		@Transactional(readOnly = false)
		public void insertIntoAssignments_Backup(AssignmentFileBean data) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into exam.new_assignments (year,month,subject,startDate,endDate,instructions,filePath,"
				+ "createdBy,createdDate,lastModifiedBy,lastModifiedDate,questionFilePreviewPath,consumerProgramStructureId) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql,new Object[] {
				data.getYear(),
				data.getMonth(),
				data.getSubject(),
				data.getStartDate(),
				data.getEndDate(),
				data.getInstructions(), 
				data.getFilePath(),
				data.getCreatedBy(),
				data.getCreatedDate(),
				data.getLastModifiedBy(),
				data.getLastModifiedDate(),
				data.getQuestionFilePreviewPath(),
				data.getConsumerProgramStructureId()
		});
		// return timeExtendedStudentList;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getProgramSemSubjectData() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select concat(consumerProgramStructureId,'|',subject,'|',sem) as pss_key from exam.program_sem_subject";
			ArrayList<String> data = (ArrayList<String>) jdbcTemplate.query(sql,new SingleColumnRowMapper(String.class));
			return data;
			}
		
		@Transactional(readOnly = false)
		public void updateConsumerPrgStructIdForProgChange() {
			ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select s.sapid ,s.program ,s.consumerProgramStructureId, s.prgmStructApplicable , " + 
						"	s.enrollmentMonth, s.enrollmentYear, s.consumerType," + 
						"	reg.sapid Rsapid,reg.program Rprogram" + 
						"    from" + 
						"    exam.students s INNER JOIN exam.registration reg on s.sapid = reg.sapid where " + 
						"    s.sapid in(SELECT sapid FROM exam.registration where sapid in (" + 
						"                select sapid from (" + 
						"                                select sapid, program from exam.registration group by sapid, program" + 
						"                ) a group by sapid having count(*) > 1" + 
						") group by sapid, program ) and reg.consumerProgramStructureId is null  group by reg.sapid, reg.program";
				
//				StudentList contains students data where there are more than 1 programs in registration
				studentList =  (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(StudentExamBean.class));
				
				for(StudentExamBean student : studentList) {
					
					String programId = "" , programStructure="Jul2014", programStructureId = "",consumerTypeId= "";
					
					programId = (String)jdbcTemplate.queryForObject("select id from exam.program where code ='"+ student.getRprogram() + "'",new SingleColumnRowMapper(String.class));
					

					if(student.getEnrollmentMonth().equals("Jul") && Integer.parseInt(student.getEnrollmentYear()) < 2014) {
						programStructure = "Jul2009" ;
					}
					if(student.getEnrollmentMonth().equals("Jan") && Integer.parseInt(student.getEnrollmentYear()) == 2019) {
						programStructure = "Jul2014" ;
					}
					if(student.getEnrollmentMonth().equals("Jul") && Integer.parseInt(student.getEnrollmentYear()) >= 2019) {
						programStructure = "Jul2019" ;
					}
					programStructureId = (String)jdbcTemplate.queryForObject("select id from exam.program_structure where program_structure ='"+ programStructure + "'",new SingleColumnRowMapper(String.class));
					
					consumerTypeId = (String)jdbcTemplate.queryForObject("select id from exam.consumer_type where name ='"+ student.getConsumerType() + "'",new SingleColumnRowMapper(String.class));
					
					ArrayList<String> oldPrgStrucId  = new ArrayList<String>();
					oldPrgStrucId = getconsumerProgramStructureIds(programId, programStructureId, consumerTypeId);
					
					if(!oldPrgStrucId.isEmpty()) {
						String sql1 = "update exam.registration set consumerProgramStructureId = " 
									+ oldPrgStrucId.get(0)+ " "
									+ "where sapid="+ student.getSapid() + " "
									+ "and program='" + student.getRprogram() + "' "
									+"and consumerProgramStructureId is null";
					
//						jdbcTemplate.update(sql1);
					}
				}
				
				
				
				
				
			}
			catch (Exception e) {
				
				
			}
		}
		@Transactional(readOnly = true)
		public List<String> getConsumerProgStructForOldProgramsFromRegistration(String sapid){
			List masterKeys = new ArrayList<String>();
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select consumerProgramStructureId from exam.registration where sapid=" + sapid;
				masterKeys = jdbcTemplate.queryForList(sql,String.class);
			}
			catch(Exception e) {
				
			}
			return masterKeys;
		}
		@Transactional(readOnly = true)
		public Page<AssignmentFileBean> downloadEvaluationReport(AssignmentFileBean assignmentStatus,int pageNo, int pageSize) {
			Page<AssignmentFileBean> page = null;
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "SELECT " + 
						
						"      a.year, a.month,a.subject,a.sapid as sapId , " + 
						 
						"    concat(s.firstName,' ',s.lastName) as studentName, s.program, " + 
						
						"    a.facultyId as faculty1 ,concat(f.firstName,' ',f.lastName) as facultyName, a.evaluated as faculty1Evaluated, a.score as faculty1Score, " + 
						
						"    a.remarks as faculty1Remarks, a.reason as faculty1Reason, " + 
						
						"    a.faculty2 as faculty2 , a.faculty2Evaluated as faculty2Evaluated, a.faculty2Score as faculty2Score, " + 
						
						"    a.faculty2Remarks as faculty2Remarks, a.faculty2Reason as faculty2Reason, " + 
						
						"    a.faculty3 as faculty3 , a.faculty3Evaluated as faculty3Evaluated, a.faculty3Score as faculty3Score, " + 
					
						"    a.faculty3Remarks as faculty3Remarks, a.faculty3Reason as faculty3Reason, " + 
						
						"    a.markedForRevaluation as OptedForReval, a.revaluationScore,a.revaluationRemarks,a.revaluationReason, " + 
						
						"    b.q1Marks, b.q1Remarks,b.q2Marks, b.q2Remarks,b.q3Marks, b.q3Remarks, " + 
						
						"    b.q4Marks, b.q4Remarks,b.q5Marks, b.q5Remarks,b.q6Marks, b.q6Remarks, " + 
						
						"    b.q7Marks, b.q7Remarks,b.q8Marks, b.q8Remarks,b.q9Marks, b.q9Remarks, " + 
						
						"    b.q10Marks, b.q10Remarks " +   
						"FROM exam.assignmentsubmission a, exam.assignmentquestionmarks b, exam.students s,acads.faculty f " + 
						"where " +  
						"               a.facultyId = f.facultyid " +  
						"    and b.facultyId = f.facultyId " +  
						"               and a.sapid = b.sapid " +  
						"    and a.subject = b.subject " +  
						"    and a.year=b.year " +  
						"    and a.year= b.year " +  
						"    and a.month= b.month " +  
						"    and a.year = ? " +  
						"    and a.month = ? " +  
						"    and s.sapid = a.sapid ";
				
				
				String countSql ="SELECT count(*) " + 
						"FROM exam.assignmentsubmission a, exam.assignmentquestionmarks b, exam.students s,acads.faculty f " + 
						"where " +  
						"               a.facultyId = f.facultyid " +  
						"    and b.facultyId = f.facultyId " +  
						"               and a.sapid = b.sapid " +  
						"    and a.subject = b.subject " +  
						"    and a.year=b.year " +  
						"    and a.year= b.year " +  
						"    and a.month= b.month " +  
						"    and a.year = ? " +  
						"    and a.month = ? " +  
						"    and s.sapid = a.sapid ";
				
				ArrayList<Object> parameters = new ArrayList<Object>(); 
				parameters.add(assignmentStatus.getYear());
				parameters.add(assignmentStatus.getMonth());
				Object[] args = parameters.toArray();
				PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
				PaginationHelper<AssignmentFileBean> evaluationList = new PaginationHelper<AssignmentFileBean>();
				page = pagingHelper.fetchPage(jdbcTemplate,
						countSql, sql, args, pageNo, pageSize,
						new BeanPropertyRowMapper(AssignmentFileBean.class));  
			}
			catch(Exception e) {
				
			}
			return page;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFileBean> timeExtendedAssignmentForStudent(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "select * from  exam.extended_assignment_submission where sapid=? ";
			List<AssignmentFileBean> timeExtendedStudentList = jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper(AssignmentFileBean.class));
			return timeExtendedStudentList;
		
		}
		@Transactional(readOnly = true)
		public AssignmentFileBean getAssignmentSubmission(AssignmentFileBean bean){
			jdbcTemplate = new JdbcTemplate(dataSource);
			AssignmentFileBean timeExtendedStudentList = null;	
			String sql = "select * from  exam.assignmentsubmission where year=? and month=? and sapid=? and subject=? limit 1";
			
			try {
				timeExtendedStudentList = (AssignmentFileBean) jdbcTemplate.queryForObject(sql,new Object[] {bean.getYear(),bean.getMonth(),bean.getSapId(),bean.getSubject()},new BeanPropertyRowMapper(AssignmentFileBean.class));
			} catch (DataAccessException e) { 
				
			}
		    
			return timeExtendedStudentList;
		
		}
		@Transactional(readOnly = true)
		public AssignmentFileBean getSingleAssignmentWithMasterkeyAndYearMonth(
				AssignmentFileBean assignmentFile, StudentExamBean student) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			AssignmentFileBean assignment = null;
			try {
				String sql = "SELECT a.* FROM exam.assignments a   where   " + 
						"		  a.month =? and a.year =? and " + 
						"		a.consumerProgramStructureId = ? and a.subject =?  ";
				assignment = (AssignmentFileBean) jdbcTemplate
						.queryForObject(sql, new Object[] {  
								assignmentFile.getMonth(),
								assignmentFile.getYear(),
								student.getConsumerProgramStructureId(),
								assignmentFile.getSubject()},
								new BeanPropertyRowMapper(AssignmentFileBean.class));
			} catch (DataAccessException e) { 
				
			}
			return assignment;
		}
		@Transactional(readOnly = false)
		public int saveAssignmentSubjectFacultyMapping(AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = " INSERT INTO exam.assignment_faculty_qp_creation_mapping "
					+ "(examYear,examMonth,startDate,endDate,"
					+ "pss_id,facultyId,reviewer,dueDate,createdDate,lastModifiedDate) VALUES (?,?,?,?,?,?,?,?,sysdate(),sysdate()) ";
				
 
			int row =jdbcTemplate.update(sql, new Object[] { 
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getStartDate(),
					bean.getEndDate(),
					bean.getPss_id(),
					bean.getFacultyId(),
					bean.getReviewer(), 
					bean.getDueDate()
					  });

			return row;

		}
		@Transactional(readOnly = true)
		public String getPssId(AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select id from exam.program_sem_subject " + 
					"where consumerProgramStructureId= " + 
					"(select id from exam.consumer_program_structure " + 
					"where " + 
					"programId=(SELECT id FROM exam.program where code=?) " + 
					"and programStructureId=(SELECT id FROM exam.program_structure where program_structure=?) " + 
					"and consumerTypeId=(select id from exam.consumer_type where name =?)) " + 
					"and subject=? ";
			String data =  (String) jdbcTemplate.queryForObject(sql,new Object[] {
					bean.getProgram(),bean.getProgramStructure(),bean.getConsumerType(),bean.getSubject()
							},new SingleColumnRowMapper(String.class));
			return data;
			}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getQpAssignedForFaculty(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT " + 
					"    qp.qpId,qp.examYear," + 
					"    qp.examMonth,qp.dueDate,qp.uploadStatus,qp.reviewStatus,qp.feedback,qp.remark," + 
					"    qp.facultyId,qp.startDate, qp.endDate,GROUP_CONCAT(\"'\",qp.pss_id,\"'\") as pss_id, "+
					"    c.subjectcode as subject,qp.filePath " + 
					"FROM " + 
					"    exam.assignment_faculty_qp_creation_mapping qp " + 
					" left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "+
					"WHERE " +  
					"    facultyId = ? GROUP BY qp.examYear,qp.examMonth,c.subjectcode order by dueDate desc";
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new Object[] {facultyId},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
			}
		@Transactional(readOnly = true)
		public Integer getQpNotUploadedCount(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where facultyId =? and uploadStatus!='Y'";

			Integer count = (int) jdbcTemplate.queryForObject(sql, new Object[] {
					 facultyId },Integer.class);
			return count;
		}
		@Transactional(readOnly = true)
		public Integer getQpApprovedCount(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where facultyId =? and approve='Y'";

			Integer count = (int) jdbcTemplate.queryForObject(sql, new Object[] {
					 facultyId },Integer.class);
			return count;
		}
		@Transactional(readOnly = true)
		public int getQpNotReviewedCount(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where reviewer =? and reviewStatus!='Y'";

			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {
					 facultyId },Integer.class);
			return count;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getQpToReviewForFaculty(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT " + 
					"    qp.qpId,qp.examYear, " + 
					"    qp.examMonth,qp.reviewStatus,qp.dueDate,qp.remark,qp.feedback,uploadStatus, " + 
					"    qp.facultyId,qp.startDate, qp.endDate,GROUP_CONCAT(\"'\",qp.pss_id,\"'\") as pss_id, "+
					"    c.subjectcode as subject,qp.filePath  " + 
					"FROM " + 
					"    exam.assignment_faculty_qp_creation_mapping qp " + 
					" left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "+
					"WHERE " + 
					"    qp.reviewer = ? GROUP BY qp.examYear,qp.examMonth,c.subjectcode,qp.facultyId order by dueDate desc";   
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new Object[] {facultyId},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = true)
		public int getTotalQpNotUploadedCount() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where uploadStatus!='Y' order by createdDate desc";

			int count = (int) jdbcTemplate.queryForObject(sql,Integer.class);
			return count;
		}
		@Transactional(readOnly = true)
		public int getTotalQpNotReviewedCount() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where reviewStatus!='Y' and uploadStatus='Y' and feedback is null order by createdDate desc";

			int count = (int) jdbcTemplate.queryForObject(sql,Integer.class);
			return count;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getAllPendingListofQpUpload() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select qp.*,concat(f.firstName,' ',f.lastName) as faculty,f.facultyId,c.subjectcode as subject"
					+ " from exam.assignment_faculty_qp_creation_mapping qp"
					+ " left join acads.faculty f on f.facultyId= qp.facultyId "
					+ " left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "
					+ "where qp.uploadStatus!='Y'"
					+ " GROUP BY qp.examYear,qp.examMonth,qp.facultyId,c.subjectcode";
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getAllPendingListofQpReview() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select qp.*,concat(f.firstName,' ',f.lastName) as faculty,f.facultyId,c.subjectcode as subject"
					+ " from exam.assignment_faculty_qp_creation_mapping qp " + 
					"	left join acads.faculty f on f.facultyId= qp.reviewer " + 
					" left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "
					+ "	where qp.uploadStatus='Y' and qp.reviewStatus!='Y' and feedback is null"
					+ " GROUP BY qp.examYear,qp.examMonth,qp.facultyId,c.subjectcode";

			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = false)
		public int saveQpFeedback(AssignmentFilesSetbean fileSet) {
			String reviewer=fileSet.getReviewer();
	        String pss_id=fileSet.getPss_id();
	        String examYear = fileSet.getExamYear();
	        String examMonth =fileSet.getExamMonth();
	        String feedback = fileSet.getFeedback();  
	        try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping set feedback=? where "
						+ "  examYear=? and examMonth=? and pss_id in("+pss_id+") and reviewer=?";
				
				int row = jdbcTemplate.update(
						sql, 
						new Object[] { feedback,examYear,examMonth,reviewer});
				return row;
	        } catch (DataAccessException e) {
				
				return 0;
			}
		}
		@Transactional(readOnly = false)
		public int saveOverallRemark(AssignmentFilesSetbean fileSet) {
	        String pss_id=fileSet.getPss_id();
	        String examYear = fileSet.getExamYear();
	        String examMonth =fileSet.getExamMonth();
	        String remark = fileSet.getRemark();  
	        try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping set remark=? where "
						+ "  examYear=? and examMonth=? and pss_id in("+pss_id+") ";
				int row = jdbcTemplate.update(
						sql, 
						new Object[] { remark,examYear,examMonth});
				return row;
	        } catch (DataAccessException e) {
				
				return 0;
			}
		}
		@Transactional(readOnly = false)
		public int approveQp(AssignmentFilesSetbean fileSet) {
			String reviewer=fileSet.getReviewer();
	        String pss_id=fileSet.getPss_id();
	        String examYear = fileSet.getExamYear();
	        String examMonth =fileSet.getExamMonth();
	        String approve = fileSet.getApprove(); 
	        try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping set reviewStatus=? where "
						+ "  examYear=? and examMonth=? and pss_id in("+fileSet.getPss_id()+") and reviewer=?";
				int row = jdbcTemplate.update(
						sql,  
						new Object[] { approve,examYear,examMonth,reviewer});
				return row;
	        } catch (DataAccessException e) {
				
				return 0;
			}
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getAllQpReviewCompletedList() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select qp.qpId,qp.examYear,qp.examMonth,qp.startDate,qp.endDate,qp.dueDate,qp.studentStartDate,qp.studentEndDate,qp.feedback,qp.approve,"+ 
					"GROUP_CONCAT(\"'\",qp.pss_id,\"'\") as pss_id, "
					+ "concat(f.firstName,' ',f.lastName) as faculty,f.facultyId,c.subjectcode as subject, "
					+" qp.filePath,qp.questionFilePreviewPath,qp.remark " + 
					"from exam.assignment_faculty_qp_creation_mapping qp " + 
					"	left join acads.faculty f on f.facultyId= qp.reviewer " + 
					" left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "+
					"	where qp.reviewStatus='Y' GROUP BY qp.examYear,qp.examMonth,c.subjectcode,qp.facultyId order by qp.createdDate desc"; 

			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = false)
		public boolean adminApproveQp(final List<AssignmentFilesSetbean> list, final String previewPath,final String filePath) {
			try { 
				jdbcTemplate = new JdbcTemplate(dataSource); 
				String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping set approve=?,filePath=?,questionFilePreviewPath=? "
						+ " where "
						+ "  qpId=? ";
				
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						AssignmentFilesSetbean a = list.get(i);
						ps.setString(1, "Y"); 
						ps.setString(2, filePath);
						ps.setString(3, previewPath);
						ps.setString(4, a.getQpId()); 
					}

					public int getBatchSize() {
						return list.size();
					}

				});
				return true;
	        } catch (DataAccessException e) {
				
				return false;
			} 
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> searchQpById(AssignmentFilesSetbean fileset) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT " + 
					"    a.* " + 
					"FROM" + 
					"    exam.assignments a" + 
					"        LEFT JOIN" + 
					"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = a.consumerProgramStructureId" + 
					"        AND pss.subject = a.subject " + 
					"WHERE 1=1 " ;
					if(fileset.getExamYear() !=null) {
						sql=sql+" AND  a.year = "+fileset.getExamYear();
					}
					if(fileset.getExamMonth().length() >0 ) {
						sql=sql+" AND  a.month = '"+fileset.getExamMonth()+"'";
					}
					if(fileset.getPss_id()!=null) {
						sql=sql+" AND  pss.id = "+fileset.getPss_id();
					}
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
					sql,new Object[] {},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = false)
		public void updateAssignmentStartDateEndDate(AssignmentFilesSetbean fileset) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			fileset.setStartDate(fileset.getStartDate().replaceAll("T", " "));
			fileset.setEndDate(fileset.getEndDate().replaceAll("T", " ")); 
			String sql = "UPDATE exam.assignments " + 
					"SET " + 
					"    startDate = ?,endDate = ? " + 
					"WHERE" + 
					"    year = ? AND month = ? " + 
					"        AND subject = ? " + 
					"        AND consumerProgramStructureId = ?";
			jdbcTemplate.update(
					sql,
					new Object[] { 
							fileset.getStartDate(),fileset.getEndDate(),
							fileset.getExamYear(),fileset.getExamMonth(),fileset.getSubject(),fileset.getConsumerProgramStructureId() });
		}
		@Transactional(readOnly = false)
		public void updateAssignmentQpFile(AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping " + 
					"SET " + 
					"    filePath = ?,questionFilePreviewPath = ?,uploadStatus='Y' " + 
					"WHERE" + 
					"    examYear = ? AND examMonth = ? " + 
					"        AND pss_id in("+bean.getPss_id()+")";
			jdbcTemplate.update(
					sql,
					new Object[] { 
							bean.getFilePath(),bean.getQuestionFilePreviewPath(),
							bean.getYear(),bean.getMonth()});
		}
		@Transactional(readOnly = false)
		public void updateUploadStatusInAsgStagingTable(AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping " + 
					"SET " + 
					"    uploadStatus='Y', reviewStatus='N' , approve='N' " + 
					"WHERE" + 
					"    examYear = ? AND examMonth = ? " + 
					"        AND pss_id in("+bean.getPss_id()+")";
			jdbcTemplate.update(
					sql,
					new Object[] { 
							bean.getYear(),bean.getMonth()});
		}
		@Transactional(readOnly = true)
		public String getPssIdByConsumerProgramStructureId(AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select id from exam.program_sem_subject " + 
					"where consumerProgramStructureId=? and subject=? ";
			String data =  (String) jdbcTemplate.queryForObject(sql,new Object[] {
					bean.getConsumerProgramStructureId() , bean.getSubject()
							},new SingleColumnRowMapper(String.class));
			return data;
			}
		@Transactional(readOnly = true)
		public ArrayList<String>  getPssIdForAll(String programId,String programStructureId, String consumerTypeId, String subject){
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =  "SELECT p_s_s.id FROM exam.consumer_program_structure as c_p_s, "
					+ "exam.program_sem_subject as p_s_s "
					+ "where c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ "and p_s_s.subject=?";
			ArrayList<String> pssIds = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {subject},  new SingleColumnRowMapper(String.class));
			
			
			return pssIds;
		}
		@Transactional(readOnly = true)
		public ArrayList<String>  getPssIdAllTypeForExcelUpload(AssignmentFilesSetbean bean){
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql =  "select pss.id from " + 
					"                    exam.program_sem_subject  pss," + 
					"                    exam.consumer_program_structure cps," + 
					"                    exam.program p , " + 
					"                    exam.program_structure ps," + 
					"                    exam.consumer_type c " + 
					"                    where " + 
					"                    c.name =?  " + 
					"                    and pss.subject=? " + 
					"                    and cps.programId=p.id  " + 
					"                    and cps.consumerTypeId=c.id   " + 
					"                    and cps.programStructureId=ps.id  " + 
					"                    and pss.consumerProgramStructureId= cps.id";
			if(!bean.getProgramStructure().equalsIgnoreCase("All")) {
				sql=sql+" and ps.program_structure ='"+bean.getProgramStructure()+"'";
			}
			if(!bean.getProgram().equalsIgnoreCase("All")) {
				sql=sql+" and p.code ='"+bean.getProgram()+"'";
			}
			ArrayList<String> pssIds = (ArrayList<String>) jdbcTemplate.query(
					sql, new Object[] {bean.getConsumerType(),bean.getSubject()},  new SingleColumnRowMapper(
							String.class));
			
			return pssIds;
		}
		@Transactional(readOnly = false)
		public void updateQpDateForStudent(AssignmentFilesSetbean fileset) {
			jdbcTemplate = new JdbcTemplate(dataSource);		
			String sql = "UPDATE exam.assignment_faculty_qp_creation_mapping " + 
					"SET " + 
					"    studentStartDate = ?,studentEndDate = ? " + 
					"WHERE " + 
					"    examYear = ? AND examMonth = ? " + 
					"        AND pss_id in("+fileset.getPss_id()+")";
			jdbcTemplate.update(
					sql,
					new Object[] { 
							fileset.getStudentStartDate(),fileset.getStudentEndDate(),
							fileset.getExamYear(),fileset.getExamMonth()});
		}
		@Transactional(readOnly = false)
		public void saveQpInAssignmentTable(List<AssignmentFilesSetbean> list, String previewPath, String filePath) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			for(AssignmentFilesSetbean bean:list) {
				
				String sql = " INSERT INTO exam.assignments "
						+ "(year,month,subject,startDate,endDate,"
						+ "questionFilePreviewPath,filePath,consumerProgramStructureId,"
						+ "createdDate,createdBy,lastModifiedBy,lastModifiedDate) VALUES (?,?,?,?,?,?,?,?,sysdate(),?,?,sysdate()) "
						+ " on duplicate key update	 startDate=? ,  endDate=? , " 
						+ " questionFilePreviewPath=?,filePath = ? , lastModifiedDate = sysdate()";
					
	 
				int row =jdbcTemplate.update(sql, new Object[] { 
						bean.getExamYear(),
						bean.getExamMonth(),
						bean.getSubject(),
						bean.getStudentStartDate(),
						bean.getStudentEndDate(),
						previewPath,
						filePath,
						bean.getConsumerProgramStructureId(),
						bean.getFaculty(),
						bean.getFaculty(),
						bean.getStudentStartDate(),
						bean.getStudentEndDate(), 
						previewPath,
						filePath
						  });
			}
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getAllPendingListofQpResolution() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select qp.*,concat(f.firstName,' ',f.lastName) as faculty,f.facultyId,c.subjectcode as subject, "
					+ "GROUP_CONCAT(DISTINCT(qp.pss_id)) as pss_id "
					+ "from exam.assignment_faculty_qp_creation_mapping qp "
					+ " left join acads.faculty f on f.facultyId= qp.facultyId "
					+ " left join exam.mdm_subjectcode_mapping m on m.id = qp.pss_id "
					+ " left join exam.mdm_subjectcode c on m.subjectCodeId=c.id "
					+ "where qp.uploadStatus='Y' and qp.reviewStatus='N' and qp.feedback is not null"
					+ " GROUP BY qp.examYear,qp.examMonth,qp.facultyId,c.subjectcode";

			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			return data;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> findQpFileFromStagingTable(String year,String month,String pss_id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT " + 
					"    qp.*,pss.consumerProgramStructureId,pss.subject " + 
					"FROM" + 
					"    exam.assignment_faculty_qp_creation_mapping qp" + 
					" left join exam.program_sem_subject pss on pss.id=qp.pss_id "+
					"       where  qp.examYear = ?  AND  qp.examMonth =?  AND  qp.pss_id in("+pss_id+") ";
					
 
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
					sql,new Object[] {year,month},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			
			return data;
		}
		@Transactional(readOnly = true)
		public Integer getQpNotResolvedCount(String facultyId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.assignment_faculty_qp_creation_mapping " + 
					"where facultyId =? and uploadStatus='Y' and reviewStatus!='Y' and feedback is not null";
			
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {
					facultyId },Integer.class);
			return count;
		}
		@Transactional(readOnly = false)
		public String saveAssignmentQuestions(final AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource); 
			String sql = "insert into exam.assignment_qp_questions(qpId,qnNo,question,mark) values(?,?,?,?) ";
			
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i)
						throws SQLException {
					List<String> questions = bean.getQuestions();
					List<String> marks = bean.getMarks();
					List<String> qnNo = bean.getQnNos();
					ps.setString(1, bean.getQpId());
					ps.setString(2, qnNo.get(i));
					ps.setString(3, questions.get(i));
					ps.setString(4, marks.get(i));   
				}

				public int getBatchSize() {
					return bean.getQuestions().size();
				}

			});
			return null;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getQuestionsForApproval(String id,String year,String month) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT q.* FROM exam.assignment_qp_questions q where q.qpId in " + 
					"(select m.qpId from exam.assignment_faculty_qp_creation_mapping m where  m.pss_id in("+id+") and  " + 
					" m.examYear=? and m.examMonth=?  group by qpId)";
			
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
					sql,new Object[] {year,month},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			
			return data;
		}
		@Transactional(readOnly = false)
		public int deleteAssignmentQuestions(String id) {
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "delete from exam.assignment_qp_questions where qpId=?";
				int row = jdbcTemplate.update(sql, new Object[] {
						id
				});
				return row;
			}
			catch(Exception e) {
				
				return 0;
			}
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> getAssgStagingTableByQpId(String id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT qp.*,pss.subject,pss.consumerProgramStructureId FROM "
					+ "exam.assignment_faculty_qp_creation_mapping qp "
					+ "left join exam.program_sem_subject pss on pss.id=qp.pss_id "
					+ " where qpId=? ";
			
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
					sql,new Object[] {id},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			
			return data;
		}
		@Transactional(readOnly = true)
		public List<AssignmentFilesSetbean> checkIfEntryExistsInQpTable(String year,String month,String pss_id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM "
					+ "exam.assignment_faculty_qp_creation_mapping  " 
					+ " where examYear=? and examMonth=? and pss_id=?";
			
			List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
					sql,new Object[] {year,month,pss_id},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
			
			return data;
		}
		@Transactional(readOnly = false)
		public long saveAssignmentQP(final AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource); 
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			        PreparedStatement statement = con.prepareStatement("INSERT INTO exam.assignment_qp  (name) VALUES (?) ", Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, bean.getYear()+" "+bean.getMonth()+" "+bean.getSubject());
			        return statement;
			    }
			}, holder);

			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		}
		@Transactional(readOnly = false)
		public void updateQpIdInAsgStagingTable(final AssignmentFilesSetbean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource); 
			String sql = "update exam.assignment_faculty_qp_creation_mapping set qpId=? "
					+ " where examYear=? and examMonth=? and pss_id in("+bean.getPss_id()+") and facultyId=? ";
			
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i)
						throws SQLException { 
					ps.setString(1, bean.getQpId());
					ps.setString(2, bean.getYear());
					ps.setString(3, bean.getMonth()); 
					ps.setString(4, bean.getFacultyId());  
				}

				public int getBatchSize() {
					return bean.getQuestions().size();
				}

			});
		}
	
		@Transactional(readOnly = false)
		public void setPaymentStatusInQuickTable(ExamBookingTransactionBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource); 
			try { 
				logger.info("sapid:"+ bean.getSapid()+",trackid:"+ bean.getTrackId()); 
				logger.info("quick_assignmentsubmission dao"); 
				String sql = "update exam.quick_assignmentsubmission set paymentDone='Y' where   " + 
						"		   sapid=? and trackId =?"; 
				 
				jdbcTemplate.update(
						sql,
						new Object[] { 
								bean.getSapid(),bean.getTrackId()   });
			} catch (Exception e) { 
				logger.info("message ---->>>>>"+e.getMessage()); 
				// TODO Auto-generated catch block
				 
			} 

		}
		@Transactional(readOnly = false)
		public void upsertPaymentStatusInQuickTable(String sapid, List<ExamBookingTransactionBean> bookingsList) {
			try {
				// Insert tracking numbers for current interaction
				String sql = " update exam.quick_assignmentsubmission set trackId=? "
						+ " where sapid=? and  subject=? and year=? and month=? ";

				jdbcTemplate = new JdbcTemplate(dataSource);
				int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql,
						new BatchPreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps, int i)
									throws SQLException {
								ExamBookingTransactionBean bean = bookingsList
										.get(i);
								ps.setString(1, bean.getTrackId());
								ps.setString(2, bean.getSapid());
								ps.setString(3, bean.getSubject());
								ps.setString(4, bean.getYear());
								ps.setString(5, bean.getMonth()); 
							}

							public int getBatchSize() {
								return bookingsList.size();
							}
						});


			} catch (Exception e) {
				
			}
		} 
		@Transactional(readOnly = true)
		public ArrayList<StudentExamBean> getStudentsApplicableForAssignments(AssignmentFilesSetbean bean,
				ArrayList<String> consumerProgramStructureIds) { 
			
			ArrayList<StudentExamBean> students = new ArrayList<StudentExamBean>();
			String cpsiInString = consumerProgramStructureIds.toString();
			String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);

			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = " select * from exam.students " 
						+ " where    " + 
						"   consumerProgramStructureId in("+cpsiCommaSeparated+") ";
								//+ "and sapid not in (select sapid from exam.quick_assignmentsubmission where year=2022 and month='Sep') ";
				
				students = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,
						new Object[] {}
				,new BeanPropertyRowMapper(StudentExamBean.class));
				
			}
			catch(Exception e) {
				
			}
			return students; 
			
		}
		@Transactional(readOnly = false)
		public void insertIntoQuickAssignments(final String userId,final String sapid, final List<AssignmentFileBean> allAssignmentFilesList, final String examYear,
				final String examMonth) {
			String sql = "INSERT INTO exam.quick_assignmentsubmission "
					+ "(year,month,subject,sem,sapid,status,endDate,startDate,createdDate,createdBy,lastModifiedDate,lastModifiedBy,currentSemSubject,attempts,attemptsLeft,submissionDate,previewPath,studentFilePath,questionFilePreviewPath,submissionAllow,paymentDone,paymentApplicable) "
					+ "VALUES(?,?,?,?,?,?,?,?,now(),?,now(),?,?,?,?,?,?,?,?,?,?,?) "
					+ "on duplicate key update "
					+ "year=?, month=? , paymentDone=?,paymentApplicable=?";
			
			int[] batchInsertOfMakeAssignmentLiveIfAll = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setString(1,examYear);
					ps.setString(2,examMonth);  
					ps.setString(3,allAssignmentFilesList.get(i).getSubject());
					ps.setString(4,allAssignmentFilesList.get(i).getSem()); 
					ps.setString(5,sapid); 
					ps.setString(6,allAssignmentFilesList.get(i).getStatus()); 
					ps.setString(7,allAssignmentFilesList.get(i).getEndDate()); 
					ps.setString(8,allAssignmentFilesList.get(i).getStartDate()); 
					ps.setString(9,userId); 
					ps.setString(10,userId); 
					ps.setString(11,allAssignmentFilesList.get(i).getCurrentSemSubject());
					ps.setString(12,allAssignmentFilesList.get(i).getAttempts());
					ps.setString(13,allAssignmentFilesList.get(i).getAttemptsLeft());
					ps.setString(14,allAssignmentFilesList.get(i).getSubmissionDate()); 
					ps.setString(15,allAssignmentFilesList.get(i).getPreviewPath());
					ps.setString(16,allAssignmentFilesList.get(i).getStudentFilePath());
					ps.setString(17,allAssignmentFilesList.get(i).getQuestionFilePreviewPath());
					ps.setString(18,allAssignmentFilesList.get(i).getSubmissionAllow());
					ps.setString(19,allAssignmentFilesList.get(i).getPaymentDone());
					ps.setString(20,allAssignmentFilesList.get(i).getPaymentApplicable());
					//On Update
					ps.setString(21,examYear);
					ps.setString(22,examMonth); 
					ps.setString(23,allAssignmentFilesList.get(i).getPaymentDone());
					ps.setString(24,allAssignmentFilesList.get(i).getPaymentApplicable());
				}

				@Override
				public int getBatchSize() {
					return allAssignmentFilesList.size();
				}
			  });
		}
		@Transactional(readOnly = true)
		public List<AssignmentFileBean> getQuickAssignmentsForSingleStudent(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<AssignmentFileBean> assignments = new ArrayList<AssignmentFileBean>();
			String sql = "select * from exam.quick_assignmentsubmission where sapid=?";
			try {
				assignments = (List<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(AssignmentFileBean .class));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			} 
			return assignments;
		}
		@Transactional(readOnly = false)
		public List<AssignmentFileBean> getQuickAssignmentsForSingleStudent(String sapid,String subject,String year,String month) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<AssignmentFileBean> assignments = new ArrayList<AssignmentFileBean>();
			String sql = "select * from exam.quick_assignmentsubmission where sapid=? and subject=? and year=? and month=?";
			try {
				assignments = (List<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid,subject,year,month},new BeanPropertyRowMapper(AssignmentFileBean .class));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}  
			return assignments;
		}
		@Transactional(readOnly = false)
		public void saveQuickAssignmentSubmissionDetails(AssignmentFileBean bean, StudentExamBean student) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = " update exam.quick_assignmentsubmission set  "
					+ "	    studentFilePath = ?,"
					+ "	    previewPath = ?,"
					+ "	    status = ?,"
					+ "	    lastModifiedBy = ?, "
					+ " 	attempts = attempts + 1,"
					+ "     attemptsLeft = attemptsLeft-1,"
					+ "	    lastModifiedDate = sysdate(),submissionDate = sysdate()  where sapid=? and year=? and month=? and subject=?";

			String studentFilePath = bean.getStudentFilePath();
			String previewPath = bean.getPreviewPath();
			String status = bean.getStatus();
			String lastModifiedBy = bean.getLastModifiedBy(); 
			
			jdbcTemplate.update(sql, new Object[] { 
					studentFilePath, previewPath, status, lastModifiedBy,student.getSapid(),bean.getYear(),bean.getMonth(),bean.getSubject() });
			
		}
			
	@Transactional(readOnly = true)	
	public List<AssignmentFilesSetbean> getQuestionsByQpId(String id) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	String sql = "SELECT * FROM exam.assignment_qp_questions where qpId=?";
	
	List<AssignmentFilesSetbean> data = (List<AssignmentFilesSetbean>) jdbcTemplate.query(
			sql,new Object[] {id},new BeanPropertyRowMapper(AssignmentFilesSetbean.class));
	
	return data;
		
		}
	@Transactional(readOnly = false)
		public void changeSubmissionCountQuickTable(String year, String month, String sapid, String subject,
				int attempts) {
			// TODO Auto-generated method stub
		
			String sql = " update exam.quick_assignmentsubmission set attempts = ?,attemptsLeft = ? "
					+ " where year =? and month = ? and sapid = ? and subject = ?   ";

			jdbcTemplate.update(
					sql,
					new Object[] { attempts,3-attempts, year,
							month, sapid, subject });
		}
	@Transactional(readOnly = false)
	public List<AssignmentFileBean> getAssignmentsForACycle(String year, String month ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// TODO Auto-generated method stub
		String sql = " select * from exam.assignmentsubmission   "
				+ " where year =? and month = ?  and status='Submitted' ";

		List<AssignmentFileBean> data = (List<AssignmentFileBean>) jdbcTemplate.query(
				sql,new Object[] {year,month},new BeanPropertyRowMapper(AssignmentFileBean.class));
		
		return data;
	}
	
	@Transactional(readOnly = false)
	public int changeSubmissionTableFilePath(AssignmentFileBean bean ) {
		logger.info(bean.getStudentFilePath());
		logger.info(bean.getYear()+"--"+bean.getMonth()+"--"+bean.getSapId()+"--"+bean.getSubject());
		jdbcTemplate = new JdbcTemplate(dataSource);
		// TODO Auto-generated method stub
		String sql = " update exam.assignmentsubmission set studentFilePath = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ?  ";
 
		return jdbcTemplate.update(sql, new PreparedStatementSetter() {
										
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				 	preparedStatement.setString(1,bean.getStudentFilePath());
	                preparedStatement.setString(2,bean.getYear());
	                preparedStatement.setString(3,bean.getMonth());
	                preparedStatement.setString(4,bean.getSapId());
	                preparedStatement.setString(5,bean.getSubject());
			}
		});
	}
	
	
	//MBA sem 2 lateral fix query
	@Transactional(readOnly = true)
 	public ArrayList<String> getSubjectsForSem1and2(String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subject from exam.program_sem_subject where sem in (1,2) and consumerProgramStructureId=?";
		ArrayList<String> data =new ArrayList<String>();
 		try {
 		data = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(String.class));
 		}
 		catch (DataAccessException e) { 
// 			e.printStackTrace();
 		}
 		return data;
 	}

	
	public ArrayList<String> getSubjectsUsingTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT subject FROM exam.assignmentpayment where trackId=?;";

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] { trackId }, new SingleColumnRowMapper(
						String.class));
		return subjectList;
	}

	@Transactional(readOnly = true)
 	public ArrayList<String> getAllMasterKeysWithProject() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select consumerProgramStructureId from exam.program_sem_subject where subject = 'Project' group by consumerProgramStructureId";
		ArrayList<String> data =new ArrayList<String>();
 		try {
 		data = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {},new SingleColumnRowMapper(String.class));
 		}
 		catch (DataAccessException e) { 
// 			e.printStackTrace();
 		}
 		return data;
 	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getResultAwaitedAssgForSingleStudent(String sapid,String subject,String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select * from exam.quick_assignmentsubmission where sapid=? and subject=? and year=? and month=? and status in ('Results Awaited','Not Submitted') ";
		try {
			AssignmentFileBean assignments = new AssignmentFileBean();
			assignments = (AssignmentFileBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,subject,year,month},
					new BeanPropertyRowMapper(AssignmentFileBean.class));
			return assignments;
		} catch (Exception e) {
			return null;
		}   
	}

	@Transactional(readOnly = false)
	public void batchDeleteAssignmentFromQuickTable(List<AssignmentFileBean> beanList) {
		
		
		String sql = "DELETE FROM exam.quick_assignmentsubmission WHERE sapid = ? AND subject = ? AND year = ? AND month=? and status in ('Results Awaited','Not Submitted') ";
		int[] argTypes = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

		List<Object[]> batchArgs = new ArrayList<>();
		
		for(AssignmentFileBean bean : beanList) {
			batchArgs.add(new Object[] { bean.getSapId(), bean.getSubject(), bean.getYear(),bean.getMonth()});
		} 
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] rowCounts = jdbcTemplate.batchUpdate(sql, batchArgs, argTypes); 
		
	}

	@Transactional(readOnly = false)
	public void batchUpdateResultAwaitedAssgs(List<AssignmentFileBean> assignments) {
		
		
		String sql = "update exam.quick_assignmentsubmission   "
				+ "set submissionAllow='Y',status='Not Submitted', "
				+ " remarks=null,  reason=null, score=null,  evaluationDate=null,"
				+ " facultyId=null,evaluated='N' "
				+ " where sapid = ?  and subject = ? and year = ? and month = ? ";
	 
		try {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i)
						throws SQLException {
					AssignmentFileBean a = assignments.get(i);
					ps.setString(1, a.getSapId());
					ps.setString(2, a.getSubject());
					ps.setString(3, a.getYear());
					ps.setString(4, a.getMonth()); 
				}

				public int getBatchSize() {
					return assignments.size();
				}

			});
		} catch (DataAccessException e) { 
			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty1(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
			String sql = "SELECT *" + 
					"	  from exam.quick_assignmentsubmission  " +  
					"     where status='Submitted'  ";

		String countSql = "SELECT count(*) "+
				"	  from exam.quick_assignmentsubmission   " +
				"     where status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sapId = ? ";
			countSql = countSql + " and sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		
		  if (searchBean.getEvaluated() != null &&
		  ("N".equals(searchBean.getEvaluated()))) { 
			  sql = sql + " and (  evaluated = ? or evaluated is null )"; 
			  countSql = countSql + " and (  evaluated = ? or evaluated is null ) ";
		  parameters.add(searchBean.getEvaluated()); 
		  }
		 
		
		
		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and evaluated = ? ";
			countSql = countSql + " and  evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and facultyId = ?   ";
			countSql = countSql + " and facultyId = ?   ";
			parameters.add(searchBean.getFacultyId());
		}
		
		sql = sql + "  group by subject, sapid order by subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty2(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT *, faculty2Evaluated as evaluated, faculty2EvaluationDate as evaluationDate "+
				"	  from exam.quick_assignmentsubmission   " +  
				"     where status='Submitted' ";

		String countSql = "SELECT count(*) FROM exam.quick_assignmentsubmission  "+ 
				"     where status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sapId = ? ";
			countSql = countSql + " and sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql
					+ " and ( faculty2Evaluated = ? or faculty2Evaluated is null ) ";
			countSql = countSql
					+ " and ( faculty2Evaluated = ? or faculty2Evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and faculty2Evaluated = ? ";
			countSql = countSql + " and  faculty2Evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and faculty2 = ?   ";
			countSql = countSql + " and faculty2 = ?   ";
			parameters.add(searchBean.getFacultyId());
		}
		
		sql = sql + " group by subject, sapid  order by subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty3(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT *, faculty3Evaluated as evaluated, faculty3EvaluationDate as evaluationDate "+
				"	  from exam.quick_assignmentsubmission   " +  
				"     where status='Submitted' ";

		String countSql =  "SELECT count(*)  "+
				"	  from exam.quick_assignmentsubmission   " +  
				"     where status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sapId = ? ";
			countSql = countSql + " and sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if (searchBean.getEvaluated() != null
				&& ("N".equals(searchBean.getEvaluated()))) {
			sql = sql
					+ " and ( faculty3Evaluated = ? or faculty3Evaluated is null ) ";
			countSql = countSql
					+ " and ( faculty3Evaluated = ? or faculty3Evaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and faculty3Evaluated = ? ";
			countSql = countSql + " and  faculty3Evaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and faculty3 = ?   ";
			countSql = countSql + " and faculty3 = ?   ";
			parameters.add(searchBean.getFacultyId());
		}
		
		sql = sql + " group by subject, sapid  order by subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentsSubmissionForReval(int pageNo,
			int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);

		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT *,revaluated as evaluated, revaluationDate as evaluationDate"
				+ " FROM exam.quick_assignmentsubmission "+
				"   where status='Submitted'";

		String countSql = "SELECT count(*) FROM exam.quick_assignmentsubmission  "+
				"   where status='Submitted' ";

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}

		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sapId = ? ";
			countSql = countSql + " and sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		
		  if (searchBean.getEvaluated() != null &&
		  ("N".equals(searchBean.getEvaluated()))) { 
			  sql = sql + " and  ( revaluated = ? or revaluated is null) "; 
			  countSql = countSql + " and ( revaluated = ? or revaluated is null ) ";
		  parameters.add(searchBean.getEvaluated()); 
		  }
		 

		if (searchBean.getEvaluated() != null
				&& ("Y".equals(searchBean.getEvaluated()))) {
			sql = sql + " and revaluated = ? ";
			countSql = countSql + " and  revaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}
		
		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and facultyIdRevaluation = ?   ";
			countSql = countSql + " and facultyIdRevaluation = ?   ";
			parameters.add(searchBean.getFacultyId());
		}

		sql = sql + " group by subject, sapid  order by subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getMonthYearForEvaluateAssignment(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(adminReports);
		List<AssignmentFileBean> assignments = new ArrayList<AssignmentFileBean>();
		try {
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
			String sql = "SELECT month,year from exam.quick_assignmentsubmission where  status='Submitted'  ";
			
		if (searchBean.getEvaluated() != null &&
		  ("N".equals(searchBean.getEvaluated()))) { 
			  sql = sql + " and (  evaluated = ? or evaluated is null )"; 
		  parameters.add(searchBean.getEvaluated()); 
		  }
		 
		if (searchBean.getFacultyId() != null
				&& !("".equals(searchBean.getFacultyId()))) {
			sql = sql + " and facultyId = ?   ";	
			parameters.add(searchBean.getFacultyId());
		}
		
		sql = sql + "  group by month,year";
		Object[] args = parameters.toArray();

		
		
		assignments = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, args,new BeanPropertyRowMapper(AssignmentFileBean.class));
		}catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return assignments;
	}
	
	public boolean checkIfProjectFeesPaid(AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT count(*) FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and b.order = (Select `order` from exam.examorder where projectSubmissionLive='Y' and year = ? and month = ? )  "
				+ " and a.subject = ? and a.booked = 'Y' and a.sapid = ? limit 1";

		int bookingCount = (int) jdbcTemplate.queryForObject(sql, new Object[] {
				bean.getYear(),bean.getMonth(),bean.getSubject(),bean.getSapId() },Integer.class);
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public Map<String, String> getAssignmentPaymentStatusByTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT tranStatus, subject from exam.assignmentpayment where trackId = ?";
		Map<String, String> statusAndSubject = null;
		try {
			statusAndSubject = jdbcTemplate.query(sql, new Object[] { trackId }, (rs) -> {

				Map<String, String> temp = new HashMap<String, String>();
				while (rs.next()) {

					temp.put("status", rs.getString("tranStatus"));
					temp.put("subject", rs.getString("subject"));
				}
				return temp;

			});
		} catch (Exception e) {
		}
		return statusAndSubject;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSapidList(List<String> consumerProgramStructureIds) {
		
		List<String> data = new ArrayList<String>();
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		//Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("SELECT `sapid` FROM `exam`.`registration` where `consumerProgramStructureId` in (:consumerProgramStructureIds) group by sapid").toString();
		
		//Adding parameters in SQL parameter map.
		queryParams.addValue("consumerProgramStructureIds", consumerProgramStructureIds);
		try {
			data = (List<String>) namedJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	@Transactional(readOnly = false)
	public void updateQPFileAssignmentTemp(List<String> sapidIds, AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
		String sapidIdList = String.join(",", sapidIds);
		
		String sql = ""
				+ " UPDATE `exam`.`quick_assignmentsubmission` "
				
				+ " SET `questionFilePreviewPath` = ? "
				+ " WHERE "
					+ " `subject` = ? and `year` = ? and `month` = ? "
					+ " AND `sapid` IN ( " + sapidIdList + " ) ";
		jdbcTemplate.update(
			sql,
			new Object[] {
					assignmentFile.getQuestionFilePreviewPath(),assignmentFile.getSubject(),assignmentFile.getYear(),assignmentFile.getMonth()
			}
		);
		}catch(Exception e){
			
			throw e;
		}
	}
	
	
	@Transactional(readOnly = false)
	public int getdeletedList(ArrayList<String> ids) {
		 int data = 0;
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		//Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String sql = new StringBuilder("delete  from  exam.extended_assignment_submission where id in (:ids)").toString();
		//Adding parameters in SQL parameter map.
		queryParams.addValue("ids", ids);
			data =  namedJdbcTemplate.update(sql, queryParams);
		return data;
	}
	
	
	@Transactional(readOnly = true)
	public boolean getAssignmentsForFaculty1Count(String facultyId,String month, String year) {
		jdbcTemplate = new JdbcTemplate(adminReports);
		try {
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " SELECT count(*) from `exam`.`quick_assignmentsubmission`  where `status`='Submitted'  ";
		
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			
			parameters.add(year);
		}

		if (month != null
				&& !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		
		if (facultyId != null
				&& !("".equals(facultyId))) {
			sql = sql + " and `facultyId` = ? ";
			parameters.add(facultyId);
		}
		Object[] args = parameters.toArray();
		

		int bookingCount = jdbcTemplate.queryForObject(sql,parameters.toArray(),Integer.class);
		
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
			// TODO: handle exception
		}
	}
	
	@Transactional(readOnly = true)
	public boolean getAssignmentsForFaculty2Count(String FacultyId2,String month, String year) {
		jdbcTemplate = new JdbcTemplate(adminReports);
		
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT count(*) from `exam`.`quick_assignmentsubmission`  where `status`='Submitted'  ";
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			
			parameters.add(year);
		}

		if (month != null
				&& !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		
		if (FacultyId2 != null
				&& !("".equals(FacultyId2))) {
			sql = sql + " and `faculty2` = ?  ";
			parameters.add(FacultyId2);
		}
		Object[] args = parameters.toArray();
		
		int bookingCount = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean getAssignmentsForFaculty3Count(String FacultyId3,String month, String year) {
		jdbcTemplate = new JdbcTemplate(adminReports);
		
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT count(*) from `exam`.`quick_assignmentsubmission`  where `status`='Submitted' ";
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			
			parameters.add(year);
		}

		if (month != null
				&& !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		
		if (FacultyId3 != null
				&& !("".equals(FacultyId3))) {
			sql = sql + "  and `faculty3` = ? ";
			parameters.add(FacultyId3);
		}
		Object[] args = parameters.toArray();
		
		int bookingCount = (int) jdbcTemplate.queryForObject(sql, parameters.toArray() ,Integer.class);
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean getAssignmentsForRevalCount(String facultyIdRevaluation,String month, String year) {
		jdbcTemplate = new JdbcTemplate(adminReports);
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT count(*) from `exam`.`quick_assignmentsubmission`  where `status`='Submitted' ";
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			
			parameters.add(year);
		}

		if (month != null
				&& !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		
		if (facultyIdRevaluation != null
				&& !("".equals(facultyIdRevaluation))) {
			sql = sql + "and `facultyIdRevaluation` = ? ";
			parameters.add(facultyIdRevaluation);
		}
		Object[] args = parameters.toArray();

		int bookingCount = (int) jdbcTemplate.queryForObject(sql , parameters.toArray() ,Integer.class);
		if (bookingCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getCurrentCycleSubjects(String sapid,String year,String month) {
		String sql =" SELECT subject FROM exam.student_current_subject  WHERE sapid =? and year =? and month =?  ";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {sapid,year,month}, new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@Transactional(readOnly = false)
	public void insertDetailedThresholdSheetData(int thresholdNO, String subject, String month, String year, String sapId1, String firstName1,
			String lastName1, String program1, String centerName1, String sapId2, String firstName2, String lastName2,
			String program2, String centerName2, double matching, String matchingFor80to90,
			int maxConseutiveLinesMatched, int numberOfLinesInFirstFile, int numberOfLinesInSecondFile,
			int noOfMatches, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		if(thresholdNO == 1) {
			sql ="INSERT INTO `copycase`.`detailed_threshold_1` (`subject`, `month`, `year`, "
					+ " `sapId1`, `firstName1`, `lastName1`,`program1`, `centerName1`, "
					+ " `sapId2`, `firstName2`, `lastName2`, `program2`, `centerName2`, "
					+ " `matching`, `matchingFor80to90`, `maxConseutiveLinesMatched`, `numberOfLinesInFirstFile`, `numberOfLinesInSecondFile`, `noOfMatches`, "
					+ " `createdBy`, `lastModifiedBy`) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " program1 = ?, centerName1 = ?, "
					+ " program2 = ?, centerName2 = ?, "
					+ " matching = ?, matchingFor80to90 = ?, maxConseutiveLinesMatched = ?, numberOfLinesInFirstFile = ?, numberOfLinesInSecondFile = ?, noOfMatches = ?, "
					+ " lastModifiedBy = ? ";
		}else {
			sql ="INSERT INTO `copycase`.`detailed_threshold_2` (`subject`, `month`, `year`, "
					+ " `sapId1`, `firstName1`, `lastName1`,`program1`, `centerName1`, "
					+ " `sapId2`, `firstName2`, `lastName2`, `program2`, `centerName2`, "
					+ " `matching`, `matchingFor80to90`, `maxConseutiveLinesMatched`, `numberOfLinesInFirstFile`, `numberOfLinesInSecondFile`, `noOfMatches`, "
					+ " `createdBy`, `lastModifiedBy`) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " program1 = ?, centerName1 = ?, "
					+ " program2 = ?, centerName2 = ?, "
					+ " matching = ?, matchingFor80to90 = ?, maxConseutiveLinesMatched = ?, numberOfLinesInFirstFile = ?, numberOfLinesInSecondFile = ?, noOfMatches = ?, "
					+ " lastModifiedBy = ? ";
		}
		 
		jdbcTemplate.update(sql, new Object[] {
				subject, month, year,  
				sapId1,  firstName1, lastName1,  program1,  centerName1,
				sapId2,  firstName2,  lastName2, program2,  centerName2,  
				matching,  matchingFor80to90, maxConseutiveLinesMatched,  numberOfLinesInFirstFile,  numberOfLinesInSecondFile, noOfMatches,
				userId,	userId,
				
				//on update
				program1,  centerName1,
				program2,  centerName2,
				matching,  matchingFor80to90, maxConseutiveLinesMatched,  numberOfLinesInFirstFile,  numberOfLinesInSecondFile, noOfMatches,
				userId
		});
		
	}

	@Transactional(readOnly = false)	
	public void insertUniqueThresholdSheetData(int thresholdNO, String subject, String month, String year, String sapId, String name, String userId) {
	// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		if(thresholdNO == 1) {
			sql ="INSERT INTO `copycase`.`unique_threshold_1` (`subject`, `month`, `year`, `sapid`, `name`, `createdBy`, `lastModifiedBy`) VALUES (?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " lastModifiedBy = ? ";
		}else {
			sql = "INSERT INTO `copycase`.`unique_threshold_2` (`subject`, `month`, `year`, `sapid`, `name`, `createdBy`, `lastModifiedBy`) VALUES (?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " lastModifiedBy = ? ";
		}
		
		jdbcTemplate.update(sql, new Object[] {
				subject, month, year, sapId, name, userId, userId,
				//on update
				userId
		});
	}
	
	@Transactional(readOnly = false)	
	public void insertBelowThresholdSheetData(int thresholdNO, String subject, String month, String year, String sapId, String name, String userId) {
	// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		if(thresholdNO == 1) {
			sql ="INSERT INTO `copycase`.`below_threshold_1` (`subject`, `month`, `year`, `sapid`, `name`, `createdBy`, `lastModifiedBy`) VALUES (?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " lastModifiedBy = ? ";
		}else {
			sql = "INSERT INTO `copycase`.`below_threshold_2` (`subject`, `month`, `year`, `sapid`, `name`, `createdBy`, `lastModifiedBy`) VALUES (?,?,?,?,?,?,?) "
					+ " on duplicate key update "
					+ " lastModifiedBy = ? ";
		}
		
		jdbcTemplate.update(sql, new Object[] {
				subject, month, year, sapId, name, userId, userId,
				//on update
				userId
		});
	}

	@Transactional(readOnly = false)
	public void insertStudentsAbove90(String subject, String sapid, String name, String month, String year, String userId) {
		// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO `copycase`.`students_above_90` (`subject`, `month`, `year`, `sapid`, `name`, `createdBy`, `lastModifiedBy`) VALUES (?,?,?,?,?,?,?) "
				+ " on duplicate key update "
				+ " lastModifiedBy = ? ";
		jdbcTemplate.update(sql, new Object[] {
				subject, month, year, sapid, name, userId, userId,
				//on update
				userId
		});
	}

	@Transactional(readOnly = true)
	public List<ResultDomain> getCopyCaseReport(AssignmentFileBean searchBean, String tableName) {
		List<ResultDomain> CCStudentList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select * from copycase.";
		if (tableName != null && !("".equals(tableName))) {
			sql = sql + tableName ;
		}
		sql = sql + " where ";
		if (searchBean.getMonth() != null && !("".equals(searchBean.getMonth()))) {
			sql = sql + " month = ? ";
			parameters.add(searchBean.getMonth());
		}
		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and year = ? ";
			parameters.add(searchBean.getYear());
		}
		if (searchBean.getSubject() != null && !("".equals(searchBean.getSubject()))) {
			sql = sql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		Object[] args = parameters.toArray();
		CCStudentList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return CCStudentList;
	}

	@Transactional(readOnly = true)
	public List<ResultDomain> getDetailedThreshold1CC(final String month, final String year, final String subject, final String sapId1) throws SQLException{
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ResultDomain> detailedThreshold1List = null;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "select * from copycase.detailed_threshold_1";
		if((year!= null && !("".equals(year))) || (month!= null && !("".equals(month))) || (subject!= null && !("".equals(subject))) || (sapId1!= null && !("".equals(sapId1)))) {
			sql = sql + " where ";
		}
		if(year!= null && !("".equals(year))) {
			sql = sql + " year = ? ";
			parameters.add(year);
		}
		if(month!= null && !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		if(subject!= null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if(sapId1!= null && !("".equals(sapId1))) {
			sql = sql + " and ? in (sapid1,sapId2) ";
			parameters.add(sapId1);
		}
		Object[] args= parameters.toArray();
		detailedThreshold1List = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return detailedThreshold1List;
	}
	
	@Transactional(readOnly = true)
	public List<ResultDomain> getDetailedThreshold2CC(final String month, final String year, final String subject, final String sapId1) throws SQLException{
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ResultDomain> detailedThreshold2List = null;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "select * from copycase.detailed_threshold_2";
		if((year!= null && !("".equals(year))) || (month!= null && !("".equals(month))) || (subject!= null && !("".equals(subject))) || (sapId1!= null && !("".equals(sapId1)))) {
			sql = sql + " where ";
		}
		if(year!= null && !("".equals(year))) {
			sql = sql + " year = ? ";
			parameters.add(year);
		}
		if(month!= null && !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		if(subject!= null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if(sapId1!= null && !("".equals(sapId1))) {
			sql = sql + " and ? in (sapid1,sapId2) ";
			parameters.add(sapId1);
		}
		Object[] args= parameters.toArray();
		detailedThreshold2List = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return detailedThreshold2List;
	}

	@Transactional(readOnly = true)
	public List<ResultDomain> getStudentAbove90CCList(final String month, final String year, final String subject, final String sapid) throws SQLException{
		List<ResultDomain> above90CCList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select * from copycase.students_above_90 ";
		if((year!= null && !("".equals(year))) || (month!= null && !("".equals(month))) || (subject!= null && !("".equals(subject))) || (sapid!= null && !("".equals(sapid)))) {
			sql = sql + " where ";
		}
		if (month != null && !("".equals(month))) {
			sql = sql + " month = ? ";
			parameters.add(month);
		}
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			parameters.add(year);
		}
		if (subject != null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if (sapid != null && !("".equals(sapid))) {
			sql = sql + " and sapid = ? ";
			parameters.add(sapid);
		}
		Object[] args = parameters.toArray();
		above90CCList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return above90CCList;
	}
	
	@Transactional(readOnly = true)
	public List<ResultDomain> getUnique1CCList(final String month, final String year, final String subject, final String sapid) throws SQLException{
		List<ResultDomain> unique1CCList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select * from copycase.unique_threshold_1 ";
		if((year!= null && !("".equals(year))) || (month!= null && !("".equals(month))) || (subject!= null && !("".equals(subject))) || (sapid!= null && !("".equals(sapid)))) {
			sql = sql + " where ";
		}
		if (month != null && !("".equals(month))) {
			sql = sql + " month = ? ";
			parameters.add(month);
		}
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			parameters.add(year);
		}
		if (subject != null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if (sapid != null && !("".equals(sapid))) {
			sql = sql + " and sapid = ? ";
			parameters.add(sapid);
		}
		Object[] args = parameters.toArray();
		unique1CCList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return unique1CCList;
	}
	
	@Transactional(readOnly = true)
	public List<ResultDomain> getUnique2CCList(final String month, final String year, final String subject, final String sapid) throws SQLException{
		List<ResultDomain> unique2CCList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select * from copycase.unique_threshold_2 ";
		if((year!= null && !("".equals(year))) || (month!= null && !("".equals(month))) || (subject!= null && !("".equals(subject))) || (sapid!= null && !("".equals(sapid)))) {
			sql = sql + " where ";
		}
		if (month != null && !("".equals(month))) {
			sql = sql + " month = ? ";
			parameters.add(month);
		}
		if (year != null && !("".equals(year))) {
			sql = sql + " and year = ? ";
			parameters.add(year);
		}
		if (subject != null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		if (sapid != null && !("".equals(sapid))) {
			sql = sql + " and sapid = ? ";
			parameters.add(sapid);
		}
		Object[] args = parameters.toArray();
		unique2CCList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ResultDomain.class));
		return unique2CCList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingExamBean> getProjectExamBookedInLastCycle(String sapId) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "SELECT eb.* FROM exam.exambookings eb, exam.examorder eo "
					+ " where "
					+ " eb.month = eo.month and eb.year = eo.year "
					+ " and eo.order = (select max(examorder.order)-0.5 from exam.examorder where projectSubmissionLive = 'Y') "
					+ " and eb.sapid = ? "
					+ " and eb.subject IN ('Project', 'Module 4 - Project') "
					+ " and eb.booked = 'Y' ";
			
			ArrayList<ExamBookingExamBean> subjectsList = (ArrayList<ExamBookingExamBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(ExamBookingExamBean.class));
			if(subjectsList == null){
				subjectsList = new ArrayList<ExamBookingExamBean>();
			}
			return subjectsList;
		} catch (Exception e) {
			
		}
		
		return new ArrayList<ExamBookingExamBean>(); 
		
	}

	
	
	@Transactional(readOnly = true)
	public List<String> getUGmasterKeys() throws Exception{
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " select distinct(consumerProgramStructureId) from exam.programs where programType in('Certificate','Diploma','Professional Diploma','Bachelor Programs','Certificate Programs')";
			
			List<String> ugmastrkeyList =  jdbcTemplate.queryForList(sql,String.class);
			if(ugmastrkeyList == null){
				ugmastrkeyList = new ArrayList<String>();
			}
			return ugmastrkeyList;
		} catch (Exception e) {
			
		}
		
		return new ArrayList<String>(); 
		
	}
	
	@Transactional(readOnly = true)
	public List<String> getQPS(String month,String year,String subject,String subjectCodeId) throws Exception{
			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql = " SELECT distinct(questionFilePreviewPath) FROM exam.assignments where month=? and year=? and subject=? and  consumerProgramStructureId in (" + params + ")";
//			List<String> Qpfiles =  jdbcTemplate.queryForList(sql,new Object[]{month,year,subject},String.class);
			String sql="select distinct(questionFilePreviewPath) from exam.assignments where  subjectCodeId=? and month=? and year=? limit 1";
			List<String> Qpfiles =  jdbcTemplate.queryForList(sql,new Object[]{subjectCodeId,month,year},String.class);
			return Qpfiles;
	}
	
	
	@Transactional(readOnly = true)
	public ExamOrderExamBean getCurrentLiveCycle() throws Exception{
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			ExamOrderExamBean bean=null;
			String sql = " select * from exam.examorder eo where eo.order = (select max(examorder.order)from exam.examorder where  resitAssignmentLive = 'Y') limit 1 ";
			bean = (ExamOrderExamBean ) jdbcTemplate.queryForObject(sql,	new BeanPropertyRowMapper(ExamOrderExamBean.class)); 
			return bean;
		} catch (Exception e) {
			
		}
		
		return null; 
		
	}
	
	
	
	
	@Transactional(readOnly = true)
	public ArrayList<ExamOrderExamBean> getLastFiveCycle(Double fithOrder,Double currentOrder) throws Exception{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.examorder  where `order` between ? and ?";
			ArrayList<ExamOrderExamBean> subjectsList = (ArrayList<ExamOrderExamBean>)jdbcTemplate.query(sql, new Object[]{fithOrder,currentOrder}, new BeanPropertyRowMapper(ExamOrderExamBean.class));
		return subjectsList; 
		
	}
	
//	@Transactional(readOnly = true)
//	public List<String> getSubjects() throws Exception{
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql = "SELECT distinct(subjectname) FROM exam.mdm_subjectcode where studentType='Regular' order by subjectname asc";
//			List<String> Qpfiles =  jdbcTemplate.queryForList(sql,String.class);
//			return Qpfiles;
//	}
//	
	
	@Transactional(readOnly = true)
	public List<String> getSubjectCidId(String masterKey,String subject,String year,String month) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct(subjectCodeId) from exam.assignments where consumerProgramStructureId in (" + masterKey + ") and subject=? and month=? and year=? ";
		List<String> Qpfiles =  jdbcTemplate.queryForList(sql,new Object[] {subject,month,year},String.class);
		return Qpfiles;
	}
	
	@Transactional(readOnly = false)
	public String deleteAssigmentfile(final String questionFilePreviewPath) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM exam.assignments WHERE questionFilePreviewPath in ("+questionFilePreviewPath+")  ";
		int rowsAffected = jdbcTemplate.update(sql);
		if(rowsAffected > 0) {
		return "Successfully deleted";
		} else {
		return "Failure";
		}
			
	}
	


	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getMarkedCopyCases(final String month, final String year, final String subject, final String sapIdList) throws SQLException{
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<AssignmentFileBean> assignmentFilesList = new ArrayList<AssignmentFileBean>();
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql = "select * from exam.assignmentsubmission where finalReason = 'Copy Case' ";
			if(year!= null && !("".equals(year))) {
				sql = sql + "and year = ? ";
				parameters.add(year);
			}
			if(month!= null && !("".equals(month))) {
				sql = sql + " and month = ? ";
				parameters.add(month);
			}
			if(subject!= null && !("".equals(subject))) {
				sql = sql + " and subject = ? ";
				parameters.add(subject);
			}
			if(sapIdList!= null && !("".equals(sapIdList))) {
				String commaSeparatedSapIds = generateCommaSeparatedList(sapIdList);
				sql = sql + " and sapid in ("+ commaSeparatedSapIds+ ")";
			}
			Object[] args= parameters.toArray();
			assignmentFilesList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(AssignmentFileBean.class));
			return assignmentFilesList;
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedCCToUnmarkInTempTable(final List<AssignmentFileBean> unMarkCCList) {

		String sql = "UPDATE `exam`.`quick_assignmentsubmission` SET `evaluated`= 'Y', `evaluationCount`= 1, `facultyId` = 'Sanket', `remarks`= ?, `reason` = ?, `finalReason` = ? "
						+ " where `month` = ? and `year` = ? and `subject` = ? and `sapid`= ? ";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = unMarkCCList.get(i);
				ps.setString(1, a.getReason());
				ps.setString(2, a.getReason());
				ps.setString(3, a.getReason());
				ps.setString(4, a.getMonth());
				ps.setString(5, a.getYear());
				ps.setString(6, a.getSubject());
				ps.setString(7, a.getSapId());
			}
			public int getBatchSize() {
				return unMarkCCList.size();
			}
		});
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedCCToUnmarkInSubmissionTable(final List<AssignmentFileBean> unMarkCCList) {
		
		String sql = "UPDATE `exam`.`assignmentsubmission` SET `evaluated` = 'Y', `evaluationCount` = 1, `facultyId` = 'Sanket', `remarks` = ?, `reason` = ?, `finalReason` = ? "
						+ " where `month` = ? and `year` = ? and `subject` = ? and `sapid`= ? ";
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = unMarkCCList.get(i);
				ps.setString(1, a.getReason());
				ps.setString(2, a.getReason());
				ps.setString(3, a.getReason());
				ps.setString(4, a.getMonth());
				ps.setString(5, a.getYear());
				ps.setString(6, a.getSubject());
				ps.setString(7, a.getSapId());
			}
			public int getBatchSize() {
				return unMarkCCList.size();
			}
		});
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedCCToAllocateInTempTable(final List<AssignmentFileBean> unMarkCCList) {
		String sql = "UPDATE `exam`.`quick_assignmentsubmission` SET `reason` = NULL, `finalScore` = NULL, `finalReason` = NULL, `toBeEvaluated` = 'Y' "
						+ " where `month` = ? and `year` = ? and `subject` = ? and `sapid`= ? ";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = unMarkCCList.get(i);
				ps.setString(1, a.getMonth());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getSubject());
				ps.setString(4, a.getSapId());
			}
			public int getBatchSize() {
				return unMarkCCList.size();
			}
		});
	}
	
	@Transactional(readOnly = false)
	public void updateMarkedCCToAllocateInSubmissionTable(final List<AssignmentFileBean> unMarkCCList) {
		String sql = "UPDATE `exam`.`assignmentsubmission` SET `reason` = NULL, `finalScore` = NULL, `finalReason` = NULL, `toBeEvaluated` = 'Y' "
						+ " where `month` = ? and `year` = ? and `subject` = ? and `sapid`= ? ";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = unMarkCCList.get(i);
				ps.setString(1, a.getMonth());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getSubject());
				ps.setString(4, a.getSapId());
			}
			public int getBatchSize() {
				return unMarkCCList.size();
			}
		});
	}
	
	@Transactional(readOnly = true)
	public boolean getResultLiveExamYear(final String month, final String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql = "select count(*) from exam.examorder where live = 'Y' ";
			
			if(month!= null && !("".equals(month))) {
				sql = sql + " and month = ? ";
				parameters.add(month);
			}
			
			if(year!= null && !("".equals(year))) {
				sql = sql + "and year = ? ";
				parameters.add(year);
			}
			
			int count = jdbcTemplate.queryForObject(sql,parameters.toArray(),Integer.class);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		}catch (Exception e) {
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public void updateToUnprocessedInMarksTable(final List<AssignmentFileBean> unMarkCCList) {
		String sql = " UPDATE `exam`.`marks` SET `processed` = 'N' where `month` = ? and `year` = ? and `subject` = ? and `sapid`= ? ";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AssignmentFileBean a = unMarkCCList.get(i);
				ps.setString(1, a.getMonth());
				ps.setString(2, a.getYear());
				ps.setString(3, a.getSubject());
				ps.setString(4, a.getSapId());
			}
			public int getBatchSize() {
				return unMarkCCList.size();
			}
		});
	}

	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getCCMarkStudentCountSubjectWise(final String month, final String year, final List<String> subjectList) throws NullPointerException, SQLException {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<AssignmentFileBean> assignmentFilesList = new ArrayList<AssignmentFileBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql = "select month,year,subject,count(sapid) as sapid from exam.assignmentsubmission where finalReason = 'Copy Case' ";
		if(year!= null && !("".equals(year))) {
			sql = sql + "and year = ? ";
			parameters.add(year);
		}
		if(month!= null && !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		if(subjectList!= null && subjectList.size() > 0) {
			String subjectCommaSeparated = "";
			for (int i = 0; i < subjectList.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ subjectList.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ subjectList.get(i).replaceAll("'", "''") + "'";
				}
			}
			sql = sql + " and subject in ("+ subjectCommaSeparated+ ") group by subject order by subject asc";
		}
		Object[] args= parameters.toArray();
		assignmentFilesList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(AssignmentFileBean.class));
		return assignmentFilesList;
	} 

	@Transactional(readOnly = false)
	public int markCCInTempTable(final String month, final String year, final String subject, final String sapid, final String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
			sql =" Update exam.quick_assignmentsubmission set "
					+ " toBeEvaluated = 'N', "
					+ " score = '0', finalScore = '0' , "
					+ " reason = 'Copy Case', finalReason = 'Copy Case', "
					+ " lastModifiedBy = ?, "
					+ " lastModifiedDate = sysDate() " 
					+ " where year = ? and month = ? and subject = ?  and sapid = ? ";
		int count = jdbcTemplate.update(sql, new Object[] {
				lastModifiedBy, year, month, subject, sapid
		});
		 return count;
	}

	@Transactional(readOnly = false)
	public int markCCInSubmissionTable(final String month, final String year, final String subject, final String sapid, final String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
			sql =" Update exam.assignmentsubmission set "
					+ " toBeEvaluated = 'N', "
					+ " score = '0', finalScore = '0' , "
					+ " reason = 'Copy Case', finalReason = 'Copy Case', "
					+ " lastModifiedBy = ?, "
					+ " lastModifiedDate = sysDate() " 
					+ " where year = ? and month = ? and subject = ?  and sapid = ? ";
		int count =jdbcTemplate.update(sql, new Object[] {
				lastModifiedBy, year, month, subject, sapid
		});
		return count;
	}
	


	@Transactional(readOnly = false)
	public void saveInWebcopyCaseDetailedResult(WebCopycaseBean bean) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO webcopycase.webcopycasedetailedresult (subject, month, year, sapid, name, createdDate, "
				+ " aggregatedScore, totalWords, identicalWords, minorChangedWords, relatedMeaningWords, webReportPdfPath, threshold,responseJson) "
				+ " VALUES (?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?)";
		
		String subject = bean.getSubject();
		String month=bean.getMonth();
		String year=bean.getYear();
		String sapid=bean.getSapid();
		String name=bean.getName();
		String aggregatedScore=bean.getAggregatedScore();
		String totleWords=bean.getTotalWords();
		String identicalWords=bean.getIdenticalWords();
		String minorChangedWords=bean.getMinorChangedWords();
		String relatedMeaningWords=bean.getRelatedMeaningWords();
		String webReportPdfPath=bean.getWebReportPdfPath();
		String threshold=bean.getThreshold();
		String responseJson=bean.getResponseJson();
		
		
	
		jdbcTemplate.update(sql, new Object[] {subject,month,year,sapid,name,aggregatedScore ,totleWords,identicalWords,minorChangedWords,relatedMeaningWords,webReportPdfPath,threshold,responseJson });

	}
	
	@Transactional(readOnly = true)
	public List<WebCopycaseBean> getwebPlagiarismDetectedStudetns(final String month, final String year ,final String subject) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<WebCopycaseBean> ccStudebtsList = new ArrayList<WebCopycaseBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql = "SELECT subject,sapid,name FROM webcopycase.webcopycasedetailedresult where ";
		if(year!= null && !("".equals(year))) {
			sql = sql + " year = ? ";
			parameters.add(year);
		}
		if(month!= null && !("".equals(month))) {
			sql = sql + " and month = ? ";
			parameters.add(month);
		}
		if(subject!= null && !("".equals(subject))) {
			sql = sql + " and subject = ? ";
			parameters.add(subject);
		}
		
		Object[] args= parameters.toArray();
		ccStudebtsList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(WebCopycaseBean.class));
		return ccStudebtsList;
}


	public List<AssignmentFileBean> getAssignmentSubmissionPage(AssignmentFileBean searchBean) throws Exception {
		System.out.println("AssignmentsDAO.getAssignmentSubmissionPage()");
		jdbcTemplate = new JdbcTemplate(dataSource);
		// Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "";
		if(searchBean.getConsumerProgramStructureId() == null) {
			sql = "select sb.*,ps.sem,s.* from exam.assignmentsubmission sb,exam.program_sem_subject ps,exam.students s  where sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId "
					+ " and sb.sapId = s.sapId";
		}
		else {
			sql = "SELECT sb.*,s.*,ps.sem FROM exam.assignmentsubmission sb, exam.students s ,exam.program_sem_subject ps where sb.sapId = s.sapId and sb.subject = ps.subject and s.consumerProgramStructureId = ps.consumerProgramStructureId and ps.consumerProgramStructureId in ("+ searchBean.getConsumerProgramStructureId() +") ";
		}

		// By Pass Checks

		if (searchBean.getYear() != null && !("".equals(searchBean.getYear()))) {
			sql = sql + " and sb.year = ? ";
			parameters.add(searchBean.getYear());
		}
		
		if (searchBean.getMonth() != null
				&& !("".equals(searchBean.getMonth()))) {
			sql = sql + " and sb.month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if (searchBean.getSubject() != null
				&& !("".equals(searchBean.getSubject()))) {
			sql = sql + " and sb.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if (searchBean.getSapId() != null
				&& !("".equals(searchBean.getSapId()))) {
			sql = sql + " and sb.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		// Added by Ashutosh to catch the students who were later terminated/suspended
		sql = sql + " and (s.programStatus NOT IN('Program Terminated', 'Program Suspension') or s.programStatus is null or s.programStatus = '') ";
		sql = sql + " order by sb.subject asc";
		Object[] args = parameters.toArray();

		List<AssignmentFileBean> studentList=jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(AssignmentFileBean.class));
		return studentList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct subject from exam.program_sem_subject order by subject";
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;
		
	}

	
	@Transactional(readOnly = true)
    public ArrayList<AssignmentFileBean> getstudentEndDate(String month,String year) throws Exception{
        jdbcTemplate = new JdbcTemplate(dataSource);

        String sql = "select distinct endDate,sapid from exam.quick_assignmentsubmission where month=? and year=?";

        ArrayList<AssignmentFileBean> subjectList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{month,year}, new BeanPropertyRowMapper<AssignmentFileBean>(AssignmentFileBean.class));
        return subjectList;
    }
	
	

	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramStructureByConsumerTypeForMBAWx(String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsStructureByConsumerType = null;
		
		String sql =  "select p_s.program_structure as name,p_s.id as id "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "where c_p_s.consumerTypeId = ?"
				+ " and c_p_s.id in () group by p_s.id";
		
		try {
			programsStructureByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsStructureByConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public List<String> getMsterKey(String consumerType,String programStructureId,String program){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " select id from exam.consumer_program_structure where ";
		
		if (!StringUtils.isBlank(consumerType)) {
			sql = sql + "  consumerTypeId in ( " + consumerType + ") ";
		}

		if (!StringUtils.isBlank(programStructureId)) {
			sql = sql + " and  programStructureId in ( " + programStructureId + ") ";
		}

		if (!StringUtils.isBlank(program)) {
			sql = sql + " and  programId in (" + program + ") ";
		}
		return  jdbcTemplate.queryForList(
				sql,
				String.class
				);  
		
	}

}

