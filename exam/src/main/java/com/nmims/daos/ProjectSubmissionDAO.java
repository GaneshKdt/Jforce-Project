package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.helpers.PaginationHelper;

@Component
public class ProjectSubmissionDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private static final String subject = "Project";


	@Autowired
	StudentMarksDAO marksDao;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	@Transactional(readOnly = false)
	public void saveAssignmentDetails(AssignmentFileBean bean, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO exam.assignments (year, month, subject, startDate, endDate, instructions, "
				+ " filePath, questionFilePreviewPath, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ " on duplicate key update "
				+ "	    startDate = ?,"
				+ "	    endDate = ?,"
				+ "	    instructions = ?,"
				+ "	    filePath = ?,"
				+ "	    questionFilePreviewPath = ?,"
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate() ";

		String subject = bean.getSubject();
		String startDate = bean.getStartDate();
		String endDate = bean.getEndDate();
		String instructions = bean.getInstructions();
		String filePath = bean.getFilePath();
		String questionFilePreviewPath = bean.getQuestionFilePreviewPath();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();



		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				startDate,
				endDate,
				instructions,
				filePath,
				questionFilePreviewPath,
				createdBy,
				lastModifiedBy,

				startDate,
				endDate,
				instructions,
				filePath,
				questionFilePreviewPath,
				lastModifiedBy
		});

	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getAssignmentFilesPage(int pageNo, int pageSize,	AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.assignments where 1 = 1 ";
		String countSql = "SELECT count(*) FROM exam.assignments where 1 = 1 ";

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

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}


		sql = sql + " order by subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentFileBean .class));


		return page;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findById(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId ";
			
			
			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getSubject()
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}

		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return assignmentFile;
	}
	@Transactional(readOnly = true)
	public StudentExamBean getStudentRegistrationData(String sapId) {
		StudentExamBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') ";

			studentRegistrationData = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentExamBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignmentsForSubjects(ArrayList<String> subjects) {

		List<AssignmentFileBean> assignmentFiles = null;
		try {


			String subjectCommaSeparated = "";
			for (int i = 0; i < subjects.size(); i++) {
				if(i == 0){
					subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
				}
			}

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject in ("+subjectCommaSeparated+") and a.startDate <= sysdate() ";

			assignmentFiles = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return assignmentFiles;
	}
	@Transactional(readOnly = false)
	public void saveProjectSubmissionDetails(AssignmentFileBean bean, int maxAttempts) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO exam.projectsubmission (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, status, attempts,title,  createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (?,?,?,?,?,?,?,1,?,?,sysdate(),?,sysdate())"
				+ " on duplicate key update "

				+ "	    studentFilePath = ?,"
				+ "	    previewPath = ?,"
				+ "	    status = ?,"
				+ "     title = ?,"
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
		String title = bean.getTitle();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();



		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				sapId,
				studentFilePath,
				previewPath,
				status,
				title,
				createdBy,
				lastModifiedBy,

				studentFilePath,
				previewPath,
				status,
				title,
				lastModifiedBy
		});


		sql = " INSERT INTO exam.assignmentsubmissionhistory (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, uploadDate) VALUES (?,?,?,?,?,?,sysdate())";

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				sapId,
				studentFilePath,
				previewPath,

		});

	}

	@Transactional(readOnly = true)
	public HashMap<String, AssignmentFileBean> getSubmissionStatus(	ArrayList<String> subjects, String sapId) {
		HashMap<String,AssignmentFileBean> subjectSubmissionMap = new HashMap<>();

		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
				+ " and subject in ("+subjectCommaSeparated+") and a.sapid = ? ";


		List<AssignmentFileBean> assignmentFiles = jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		if(assignmentFiles!= null && assignmentFiles.size() > 0){
			for (int i = 0; i < assignmentFiles.size(); i++) {
				subjectSubmissionMap.put(assignmentFiles.get(i).getSubject(), assignmentFiles.get(i));
			}
		}
		return subjectSubmissionMap;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getProjectSubmissionStatus(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT * FROM exam.projectsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') "
					+ " and subject = ? and a.sapid = ? ";


			AssignmentFileBean assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{subject, sapId}, new BeanPropertyRowMapper(AssignmentFileBean .class));

			return assignmentFile;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	} 
	@Transactional(readOnly = true)
	public AssignmentFileBean getProjectDetailsForStudent(	AssignmentFileBean assignmentFile) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		
		
		String sql = "SELECT * FROM exam.assignments a , exam.assignment_live_setting a_l_s, exam.projectsubmission asb "
				+ " where asb.month = a_l_s.examMonth "
				+ " and asb.year = a_l_s.examYear "
				+ " and a_l_s.examMonth = a.month "
				+ " and a_l_s.examYear = a.year "
				+ " and asb.subject = a.subject "
				+ " and a.consumerProgramStructureId = a_l_s.consumerProgramStructureId "
				+ " and a.consumerProgramStructureId = ? and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ?"
				+ " GROUP BY a.consumerProgramStructureId";

		AssignmentFileBean assignment = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getConsumerProgramStructureId(),
				assignmentFile.getSubject(),
				assignmentFile.getYear(),
				assignmentFile.getMonth(),
				assignmentFile.getSapId()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignment;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getProjectSubmissionPage(int pageNo,	int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.projectsubmission sb, exam.students s where sb.sapId = s.sapId  ";
		String countSql = "SELECT count(*) FROM exam.projectsubmission sb, exam.students s where sb.sapId = s.sapId  ";

		if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and sb.year = ? ";
			countSql = countSql + " and sb.year = ? ";
			parameters.add(searchBean.getYear());
		}

		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and sb.month = ? ";
			countSql = countSql + " and sb.month = ? ";
			parameters.add(searchBean.getMonth());
		}

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and sb.subject = ? ";
			countSql = countSql + " and sb.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if( searchBean.getSapId() != null &&   !("".equals(searchBean.getSapId()))){
			sql = sql + " and sb.sapId = ? ";
			countSql = countSql + " and sb.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}


		sql = sql + " order by sb.subject asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentFileBean .class));


		return page;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getANS(int pageNo, int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String year = searchBean.getYear();
		String examMonth = searchBean.getMonth();
		String acadMonth = "";

		if("Jun".equals(examMonth)){
			acadMonth = "Jan";
		}else{
			acadMonth = "Jul";
		}

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.students s, exam.program_subject ps, exam.registration r where r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid and r.sem = ps.sem and r.year = " + year + " and r.month = '" + acadMonth + "' and "
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where year = " + year + ""
				+ " and month = '" + examMonth + "' )";

		String countSql = "SELECT count(*) FROM exam.students s, exam.program_subject ps, exam.registration r where r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid and r.sem = ps.sem and r.year = " + year + " and r.month = '" + acadMonth + "' and "
				+ " concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where year = " + year + ""
				+ " and month = '" + examMonth + "' )";


		/*if( searchBean.getYear() != null &&   !("".equals(searchBean.getYear()))){
			sql = sql + " and r.year = ? ";
			countSql = countSql + " and r.year = ? ";
			parameters.add(searchBean.getYear());
		}

		if( searchBean.getMonth() != null &&   !("".equals(searchBean.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(searchBean.getMonth());
		}*/

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and ps.subject = ? ";
			countSql = countSql + " and ps.subject = ? ";
			parameters.add(searchBean.getSubject());
		}

		if( searchBean.getSapId() != null &&   !("".equals(searchBean.getSapId()))){
			sql = sql + " and s.sapId = ? ";
			countSql = countSql + " and s.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}


		sql = sql + " order by s.sapid asc";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentFileBean .class));


		return page;
	}
	@Transactional(readOnly = false)
	public void upsertAssignmentStatus(AssignmentFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "INSERT INTO exam.assignmentStatus "
				+ "(examYear, examMonth, sapid, subject, submitted, createdBy, createdDate, lastModifiedBy , lastModifiedDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,?,sysdate() , ? , sysdate())"
				+ " on duplicate key update "
				+ "	    examYear = ?,"
				+ "	    examMonth = ?,"
				+ "	    sapid = ?,"
				+ "	    subject = ?,"
				+ "	    submitted = ?,"
				+ "	    createdBy = ?, "
				+ "	    createdDate = sysdate() ";


		String examYear = bean.getYear();
		String examMonth = bean.getMonth();
		String sapid = bean.getSapId();
		String subject = bean.getSubject();
		String submitted = "Y";
		String createdBy = sapid;



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
				createdBy, 
				bean.getLastModifiedBy()
		});


	}
	@Transactional(readOnly = true)
	public int getNumberOfProjects(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.projectsubmission psb, exam.students s where "
				+ " year = ? and month = ? and psb.reason is null  and psb.sapid = s.sapid and (s.program like 'P%' or s.program like 'C%' or s.program like 'B%' or s.program like 'MBA%') and  (facultyId is null or facultyId = '' ) ";
		ArrayList<Object> parameters = new ArrayList<Object>(); 
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());

		if(assignmentFile.getProgram() != null && !"".equals(assignmentFile.getProgram().trim())){
			sql = sql + " and s.program = ?   ";
			parameters.add(assignmentFile.getProgram());
		}
		int numberOfSubjects = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
		return numberOfSubjects;
	}

	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getProjects(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select psb.year, psb.month, psb.sapid, psb.subject from exam.projectsubmission psb , exam.students s "
				+ " where s.sapid = psb.sapid and year = ? and month = ? and s.program = ? and psb.reason is null  and (facultyId is null or facultyId = '' )";
		
		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{assignmentFile.getYear(), assignmentFile.getMonth(), assignmentFile.getProgram()}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = false)
	public void allocateProject(final List<AssignmentFileBean> assignmentsSubSet, final	String facultyId) {
		String sql = "update exam.projectsubmission "
				+ " set facultyId = ? "
				+ " where year = ? "
				+ " and month = ?"
				+ " and sapid = ?"
				+ " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
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
	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getAssignmentsForFaculty(String facultyId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.assignmentsubmission where facultyId = ? and  ( evaluated is null or evaluated <> 'Y') ";
		List<AssignmentFileBean> assignments = (List<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{
				facultyId}, 
				new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> getProjectsForFacultyPage(int pageNo, int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT a.*,f.*, s.firstName as sfName, s.lastName as slName,s.program as program  "
				+ "FROM exam.projectsubmission a, exam.students s, acads.faculty f  "
				+ "where a.facultyId = f.facultyId and a.sapid = s.sapid  ";
		String countSql = "SELECT count(*) "
				+ "FROM exam.projectsubmission a, acads.faculty f, exam.students s "
				+ "where a.facultyId = f.facultyId and a.sapid = s.sapid  ";
		
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

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getProgram() != null &&   !("".equals(searchBean.getProgram()))){
			sql = sql + " and s.program = ? ";
			countSql = countSql + " and s.program = ? ";
			parameters.add(searchBean.getProgram());
		}

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and (a.facultyId = ? or a.facultyIdRevaluation = ?)";
			countSql = countSql + " and (a.facultyId = ? or a.facultyIdRevaluation = ?)";
			parameters.add(searchBean.getFacultyId());
			parameters.add(searchBean.getFacultyId());
		}

		if( searchBean.getSapId() != null &&   !("".equals(searchBean.getSapId()))){
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if( searchBean.getEvaluated() != null  &&   ("N".equals(searchBean.getEvaluated())) &&   !("".equals(searchBean.getEvaluated())) ){
			sql = sql + " and ( evaluated = ? or evaluated is null )";
			countSql = countSql + " and ( evaluated = ? or evaluated is null )  ";
			parameters.add(searchBean.getEvaluated());
			
		}

		if( searchBean.getEvaluated() != null &&   ("Y".equals(searchBean.getEvaluated()))  &&   !("".equals(searchBean.getEvaluated()))){
			//removed revaluated check as it was giving incorrect count for total evaluated projects
			sql = sql + " and (evaluated = ?)";
			countSql = countSql + " and  (evaluated = ?)  ";
			parameters.add(searchBean.getEvaluated());
			
		}

		if( searchBean.getRevaluated() != null &&   !("".equals(searchBean.getRevaluated()))){
			
			if( "N".equals(searchBean.getRevaluated())){
			sql = sql + " and revaluated = ? ";
			countSql = countSql + " and  revaluated = ?  ";
			}
			if( "Y".equals(searchBean.getRevaluated())){
				sql = sql + " and revaluated = ? ";
				countSql = countSql + " and  revaluated = ?  ";
				}
			parameters.add(searchBean.getRevaluated());
		}

		if( searchBean.getMarkedForRevaluation() != null &&   !("".equals(searchBean.getMarkedForRevaluation()))){
			sql = sql + " and MarkedForRevaluation = ? ";
			countSql = countSql + " and  MarkedForRevaluation = ?  ";
			parameters.add(searchBean.getMarkedForRevaluation());
		}

		if( searchBean.getReason() != null &&   !("".equals(searchBean.getReason()))){
			sql = sql + " and a.reason = ? ";
			countSql = countSql + " and a.reason = ? ";
			parameters.add(searchBean.getReason());
		}
		
		if( searchBean.getRevisited() != null &&   !("".equals(searchBean.getRevisited()))){
			sql = sql + " and Revisited = ? ";
			countSql = countSql + " and  Revisited = ?  ";
			parameters.add(searchBean.getRevisited());
		}


		sql = sql + " group by a.sapid, a.subject order by a.subject asc ";
		if(!searchBean.getLastModifiedBy().equalsIgnoreCase(searchBean.getFacultyId())){
			parameters.remove(parameters.size()-1);//added to remove extra comma from para list.
		}
		
		Object[] args = parameters.toArray();
		for(Object arg: parameters){
		}
		
		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentFileBean .class));
		

		return page;
	}
	
/*	public Page<AssignmentFileBean> getProjectsForFacultyRevaluationPage(int pageNo, int pageSize, AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT *, s.firstName as sfName, s.lastName as slName FROM exam.projectsubmission a, exam.students s, acads.faculty f  where a.facultyId = f.facultyId and a.sapid = s.sapid  ";
		String countSql = "SELECT count(*) FROM exam.projectsubmission a, acads.faculty f, exam.students s where a.facultyIdRevaluation = f.facultyId and a.sapid = s.sapid  ";

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

		if( searchBean.getSubject() != null &&   !("".equals(searchBean.getSubject()))){
			sql = sql + " and subject = ? ";
			countSql = countSql + " and subject = ? ";
			parameters.add(searchBean.getSubject());
		}
		
		if( searchBean.getProgram() != null &&   !("".equals(searchBean.getProgram()))){
			sql = sql + " and s.program = ? ";
			countSql = countSql + " and s.program = ? ";
			parameters.add(searchBean.getProgram());
		}

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and a.facultyIdRevaluation = ? ";
			countSql = countSql + " and a.facultyIdRevaluation = ? ";
			parameters.add(searchBean.getFacultyId());
		}

		if( searchBean.getSapId() != null &&   !("".equals(searchBean.getSapId()))){
			sql = sql + " and a.sapId = ? ";
			countSql = countSql + " and a.sapId = ? ";
			parameters.add(searchBean.getSapId());
		}

		if( searchBean.getEvaluated() != null  &&   ("N".equals(searchBean.getEvaluated()))){
			sql = sql + " and (  revaluated = ? or revaluated is null )";
			countSql = countSql + " and (  revaluated = ? or revaluated is null ) ";
			parameters.add(searchBean.getEvaluated());
		}

		if( searchBean.getEvaluated() != null &&   ("Y".equals(searchBean.getEvaluated()))){
			sql = sql + " and revaluated = ? ";
			countSql = countSql + " and  revaluated = ?  ";
			parameters.add(searchBean.getEvaluated());
		}

		if( searchBean.getRevaluated() != null &&   !("".equals(searchBean.getRevaluated()))){
			sql = sql + " and Revaluated = ? ";
			countSql = countSql + " and  Revaluated = ?  ";
			parameters.add(searchBean.getRevaluated());
		}

		if( searchBean.getMarkedForRevaluation() != null &&   !("".equals(searchBean.getMarkedForRevaluation()))){
			sql = sql + " and MarkedForRevaluation = ? ";
			countSql = countSql + " and  MarkedForRevaluation = ?  ";
			parameters.add(searchBean.getMarkedForRevaluation());
		}

		if( searchBean.getReason() != null &&   !("".equals(searchBean.getReason()))){
			sql = sql + " and a.revalreason = ? ";
			countSql = countSql + " and a.revalreason = ? ";
			parameters.add(searchBean.getReason());
		}
		


		sql = sql + " group by a.sapid, a.subject order by a.subject asc ";
		Object[] args = parameters.toArray();

		PaginationHelper<AssignmentFileBean> pagingHelper = new PaginationHelper<AssignmentFileBean>();
		Page<AssignmentFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentFileBean .class));


		return page;
	}*/
	@Transactional(readOnly = true)
	public AssignmentFileBean getSingleProjectForFaculty(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/* old query
		 * String sql = "select * from exam.projectsubmission a, exam.assignments s where a.year = ? and a.month = ? "
				+ " and a.subject = ?  and sapid = ? and (facultyId = ? or facultyIdRevaluation = ? ) and "
				+ "s.year = a.year and s.month = a.month and s.subject = a.subject";*/
		//updated as incorrect number of records were obtained without consumerId 
		String sql= "select "
				+" 	a.*,st.program as program "
				+" from" 
				+" 		exam.projectsubmission a," 
				+" 	    exam.assignments s,"
				+" 	    exam.students st"
				+" 	where "
				+" 		a.year = ?"
				+" 	    and a.month = ?"
				+" 		and a.subject = ?" 
				+" 	    and a.sapid =? " 
				+" 	    and (facultyId = ? or facultyIdRevaluation = ? ) "
				+" 	    and s.year = a.year "
				+" 	    and s.month = a.month" 
				+" 	    and s.subject = a.subject"
				+" 	    and st.consumerProgramStructureId = s.consumerProgramStructureId"
				+" 	group "
				+ " by a.sapid";
		AssignmentFileBean  assignment = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, 
				new Object[]{
				assignmentFile.getYear(), 
				assignmentFile.getMonth(), 
				assignmentFile.getSubject(),
				assignmentFile.getSapId(),
				assignmentFile.getFacultyId(),
				assignmentFile.getFacultyId()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignment;
	}
	@Transactional(readOnly = false)
	public void evaluateProject(AssignmentFileBean assignment) {	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.projectsubmission set evaluated = 'Y', score = ? , evaluationDate = sysdate() , remarks = ? , reason = ?, "
				+ " evaluationCount = evaluationCount + 1 ,"
				+ "q1Marks = ?, q1Remarks = ?, "
				+ "q2Marks = ?, q2Remarks = ?, "
				+ "q3Marks = ?, q3Remarks = ?, "
				+ "q4Marks = ?, q4Remarks = ?, "
				+ "q5Marks = ?, q5Remarks = ?, "
				+ "q6Marks = ?, q6Remarks = ?, "
				+ "q7Marks = ?, q7Remarks = ?, "
				+ "q8Marks = ?, q8Remarks = ?, "
				+ "q9Marks = ?, q9Remarks = ? "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(sql, new Object[]{
				assignment.getScore(),
				assignment.getRemarks(),
				assignment.getReason(),
				
				assignment.getQ1Marks(),assignment.getQ1Remarks(),
				assignment.getQ2Marks(),assignment.getQ2Remarks(),
				assignment.getQ3Marks(),assignment.getQ3Remarks(),
				assignment.getQ4Marks(),assignment.getQ4Remarks(),
				assignment.getQ5Marks(),assignment.getQ5Remarks(),
				assignment.getQ6Marks(),assignment.getQ6Remarks(),
				assignment.getQ7Marks(),assignment.getQ7Remarks(),
				assignment.getQ8Marks(),assignment.getQ8Remarks(),
				assignment.getQ9Marks(),assignment.getQ9Remarks(),
				
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSubject(),
				assignment.getSapId()
		});

		sql = "insert into exam.projectevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?,?)";

		jdbcTemplate.update(sql, new Object[]{
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSapId(),
				assignment.getSubject(),
				assignment.getFacultyId(),
				assignment.getFacultyId(),
				assignment.getScore(),
				assignment.getRemarks(),
				assignment.getReason()
		});
	}
	@Transactional(readOnly = false)
	public void revaluateProject(AssignmentFileBean assignment) {	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.projectsubmission set revaluated = 'Y', revaluationscore = ? , revaluationDate = sysdate() , revaluationremarks = ? , revaluationreason = ?, "
				+ " revaluationCount = revaluationCount + 1 ,"
				+ "q1RevalMarks = ?, q1RevalRemarks = ?, "
				+ "q2RevalMarks = ?, q2RevalRemarks = ?, "
				+ "q3RevalMarks = ?, q3RevalRemarks = ?, "
				+ "q4RevalMarks = ?, q4RevalRemarks = ?, "
				+ "q5RevalMarks = ?, q5RevalRemarks = ?, "
				+ "q6RevalMarks = ?, q6RevalRemarks = ?, "
				+ "q7RevalMarks = ?, q7RevalRemarks = ?, "
				+ "q8RevalMarks = ?, q8RevalRemarks = ?, "
				+ "q9RevalMarks = ?, q9RevalRemarks = ? "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(sql, new Object[]{
				assignment.getRevaluationScore(),
				assignment.getRevaluationRemarks(),
				assignment.getRevaluationReason(),
				
				assignment.getQ1RevalMarks(),assignment.getQ1RevalRemarks(),
				assignment.getQ2RevalMarks(),assignment.getQ2RevalRemarks(),
				assignment.getQ3RevalMarks(),assignment.getQ3RevalRemarks(),
				assignment.getQ4RevalMarks(),assignment.getQ4RevalRemarks(),
				assignment.getQ5RevalMarks(),assignment.getQ5RevalRemarks(),
				assignment.getQ6RevalMarks(),assignment.getQ6RevalRemarks(),
				assignment.getQ7RevalMarks(),assignment.getQ7RevalRemarks(),
				assignment.getQ8RevalMarks(),assignment.getQ8RevalRemarks(),
				assignment.getQ9RevalMarks(),assignment.getQ9RevalRemarks(),
				
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSubject(),
				assignment.getSapId()
		});

		sql = "insert into exam.projectevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?,?)";

		jdbcTemplate.update(sql, new Object[]{
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSapId(),
				assignment.getSubject(),
				assignment.getFacultyId(),
				assignment.getFacultyId(),
				assignment.getRevaluationScore(),
				assignment.getRevaluationRemarks(),
				assignment.getRevaluationReason()
		});
	}

	@Transactional(readOnly = false)
	public void reEvaluateAssignment(AssignmentFileBean assignment) {	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.assignmentsubmission set revaluated = 'Y', revaluationScore = ? , revaluationRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(sql, new Object[]{
				assignment.getRevaluationScore(),
				assignment.getRevaluationRemarks(),
				assignment.getFacultyId(),
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSubject(),
				assignment.getSapId()
		});

		sql = "insert into exam.assignmentevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?,?)";

		jdbcTemplate.update(sql, new Object[]{
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSapId(),
				assignment.getSubject(),
				assignment.getFacultyId(),
				assignment.getFacultyId(),
				assignment.getRevaluationScore(),
				assignment.getRemarks(),
				assignment.getReason()
		});
	}
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());


		}catch(Exception e){
			//
		}
		return student;
	}

	public void upsertProjectMarks(StudentMarksBean marksBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		marksDao.upsertMarks(marksBean, jdbcTemplate, "written");
	}
	@Transactional(readOnly = false)
	public void changeEvaluationCount(AssignmentFileBean assignment, String evalutionCount) {
		String sql = " update exam.assignmentsubmission set evaluationCount = ? "
				+ " where year =? and month = ? and sapid = ? and subject = ? " ;

		jdbcTemplate.update(sql, new Object[] { 
				evalutionCount,
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSapId(),
				assignment.getSubject()
		});


	}
	@Transactional(readOnly = false)
	public void resetFacultyAssignmentAllocation(AssignmentFileBean searchBean) {
		String sql = " update exam.assignmentsubmission set facultyId = null "
				+ " where year =? and month = ? and facultyId = ? and subject = ? and ( evaluated = 'N' or evaluated is null) " ;

		int rowsUpdated = jdbcTemplate.update(sql, new Object[] { 
				searchBean.getYear(),
				searchBean.getMonth(),
				searchBean.getFacultyId(),
				searchBean.getSubject()
		});


	}
	@Transactional(readOnly = false)
	public void revisitAssignment(AssignmentFileBean assignment) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.assignmentsubmission set revisited = 'Y', revisitScore = ? , revisitRemarks = ? , evaluationDate = sysdate() , lastModifiedBy = ?  "
				+ " where year = ? and month = ? and subject = ?  and sapid = ?  ";
		jdbcTemplate.update(sql, new Object[]{
				assignment.getRevisitScore(),
				assignment.getRevisitRemarks(),
				assignment.getFacultyId(),
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSubject(),
				assignment.getSapId()
		});

		sql = "insert into exam.assignmentevaluationhistory (year, month, sapid, subject, facultyId, createdBy, createdDate, score, remarks, reason) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?,?)";

		jdbcTemplate.update(sql, new Object[]{
				assignment.getYear(),
				assignment.getMonth(),
				assignment.getSapId(),
				assignment.getSubject(),
				assignment.getFacultyId(),
				assignment.getFacultyId(),
				assignment.getRevisitScore(),
				assignment.getRevisitRemarks(),
				assignment.getReason()
		});

	}
	@Transactional(readOnly = true)
	public String getMostRecentAssignmentSubmissionPeriod(){

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
	public boolean isProjectLive(String subject) {
	
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		boolean live = false;
		try {
			String sql = "SELECT count(*) FROM exam.assignments  a, exam.examorder eo where "
					+ " subject = ? and a.year = eo.year and a.month = eo.month and startDate <= sysdate() and endDate >= sysdate() and "
					+ " eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') ";
			count = (int)jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
			if( count>0 ){
				live = true;
			}
		} catch (Exception e) {
			//
			return live;
		}
		return live;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findProjectGuidelinesCurrentLiveCycle(AssignmentFileBean assignmentFile) {	// Make two live cycle code 
	//public AssignmentFileBean findProjectGuidelines(AssignmentFileBean assignmentFile) {					// Normal single live cycle code
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
//			String sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
//					+ " subject = ? and a.year = eo.year and a.month = eo.month and "
//					+ " eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') ";
//			
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId";

			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getSubject()
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}

		} catch (Exception e) {
			//
			return null;
		}
		return assignmentFile;
	}
	private List<String> generateSapidList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+",
				",");
		if (commaSeparatedList.endsWith(",")) {
			commaSeparatedList = commaSeparatedList.substring(0,
					commaSeparatedList.length() - 1);
		}
		List<String> sapidList = new ArrayList<String>(Arrays.asList(commaSeparatedList.split(",")));
		return sapidList;
	}
	@Transactional(readOnly = false)
	public void markCopyCases(final AssignmentFileBean searchBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="Update exam.projectsubmission set  score = '0', reason = 'Copy Case',lastModifiedBy=?,lastModifiedDate=now() "
				+ " where year = ? and month = ? and subject =? and sapid =?"; 
		jdbcTemplate.update(sql, new Object[] { searchBean.getLastModifiedBy(),searchBean.getYear(),
				searchBean.getMonth(), searchBean.getSubject(),searchBean.getSapId() });
	} 
	@Transactional(readOnly = true)
	public Page<AssignmentFileBean> searchCopyCases(int pageNo, int pageSize,
			AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.projectsubmission p, exam.students b where p.sapid = b.sapid and  reason = 'Copy Case' ";
		String countSql = "SELECT count(*) FROM exam.projectsubmission p, exam.students b where p.sapid = b.sapid and reason = 'Copy Case' ";
 
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
	public List<AssignmentFileBean> getCopyCases(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<AssignmentFileBean> assignments = new ArrayList<AssignmentFileBean>();
		sql = "Select * from exam.projectsubmission p, exam.students s where year = ? and month = ? "
				+ " and p.sapid = s.sapid "
				+ " and reason like '%Copy Case%' ";
		assignments = (List<AssignmentFileBean>) jdbcTemplate.query(sql,
				new Object[] { searchBean.getYear(), searchBean.getMonth() },
				new BeanPropertyRowMapper(AssignmentFileBean.class));

		return assignments;
	}
	@Transactional(readOnly = true)
	public String getCurrentSemForStudent(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select max(sem) from exam.registration where sapid =? ";
		String sem = (String) jdbcTemplate.queryForObject(
	            sql, new Object[] { sapId }, String.class);
		return sem;
	}
	@Transactional(readOnly = true)
	public String getProjectTitleForStudent(AssignmentFileBean assignmentFile, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = " SELECT `title` "
				+ " FROM `exam`.`project_title_student_mapping` `ptsm` "
				+ " INNER JOIN `exam`.`project_titles` `pt` ON `pt`.`id` = `ptsm`.`titleId` "
				+ " WHERE `ptsm`.`examYear` = ? AND `ptsm`.`examMonth` = ? and `ptsm`.`sapid` = ? ";
			return jdbcTemplate.queryForObject(
				sql, 
				new Object[]{
					assignmentFile.getYear(), assignmentFile.getMonth(), sapId
				}, 
				String.class
			);
		} catch (Exception e) {
			
		}
		return null;
	}
	@Transactional(readOnly = true)
	public String getProjectTitleForStudent(String year,String month, String sapId,String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = " SELECT `title` "
				+ " FROM `exam`.`project_title_student_mapping` `ptsm` "
				+ " INNER JOIN `exam`.`project_titles` `pt` ON `pt`.`id` = `ptsm`.`titleId` INNER JOIN `exam`.`program_sem_subject` `pss` ON pss.id = pt.prgm_sem_subj_id "
				+ " WHERE `ptsm`.`examYear` = ? AND `ptsm`.`examMonth` = ? and `ptsm`.`sapid` = ? and `pss`.`subject` = ? ";
			return jdbcTemplate.queryForObject(
				sql, 
				new Object[]{
					year, month, sapId, subject
				}, 
				String.class
			);
		} catch (Exception e) {
			
		}
		return null;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getProjectApplicableStudentList(String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select concat(sapid,subject) as applicableStudent from `exam`.`student_guided_project_applicable` where year=? and month=?";
			return (ArrayList<String>)jdbcTemplate.query(sql,new Object[] {year,month},new SingleColumnRowMapper<String>(String.class));
		}catch (Exception e) {
			// TODO: handle exception
			return new ArrayList<String>();
		}
	}
	@Transactional(readOnly = true)
	public List<ProjectTitle> getProjectTitleListForStudent(String year, String month, String consumerProgramStructureId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = " SELECT "
				+ " `pt`.*, "
				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_titles` `pt` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`prgm_sem_subj_id` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `pt`.`active` = 'Y' AND `consumerProgramStructureId` = ? AND `subject` = ? ";
			List<ProjectTitle> list = jdbcTemplate.query(
				sql, 
				new Object[]{
					consumerProgramStructureId, subject
				}, 
				new BeanPropertyRowMapper<ProjectTitle>(ProjectTitle.class)
			);
			return list;
		} catch (Exception e) {
			
		}
		return new ArrayList<ProjectTitle>();
	}
	@Transactional(readOnly = false)
	public void saveProjectTitleSelectionForStudent(ProjectTitle title) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO `exam`.`project_title_student_mapping` ( "
					+ " `sapid`, `titleId`, `examYear`, "
					+ " `examMonth`, `createdBy`, `updatedBy` "
				+ " ) VALUES  ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ? "
				+ " ) ON DUPLICATE KEY UPDATE titleId = ?; ";
		jdbcTemplate.update(sql, new Object[] { 
			title.getSapid(), title.getTitleId(), title.getExamYear(),
			title.getExamMonth(), title.getCreatedBy(), title.getUpdatedBy(), title.getTitleId()
		});
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findProjectById(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select examorder.order  from exam.examorder  where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=?) and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId ";
			
			
			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getYear(),assignmentFile.getMonth(),assignmentFile.getSubject(),
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}

		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return assignmentFile;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean findProjectGuidelinesFromLiveSettingForApplicableCycle(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
//			String sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
//					+ " subject = ? and a.year = eo.year and a.month = eo.month and "
//					+ " eo.order = (select max(examorder.order) from exam.examorder where projectSubmissionLive = 'Y') ";
//			
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select examorder.order  from exam.examorder where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=? ) and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId";

			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getYear(),assignmentFile.getMonth(),assignmentFile.getSubject(),
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return assignmentFile;
	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getProjectSubmissionStatusForCycle(String subject, String sapId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT * FROM exam.projectsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select examorder.order  from exam.examorder where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=?) "
					+ " and subject = ? and a.sapid = ? ";


			AssignmentFileBean assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{year,month,subject, sapId}, new BeanPropertyRowMapper(AssignmentFileBean .class));

			return assignmentFile;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return null;

	}
	@Transactional(readOnly = true)
	public AssignmentFileBean getProjectSubmissionStatusForACycle(String subject, String sapId,String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT * FROM exam.projectsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select examorder.order  from exam.examorder where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=?) "
					+ " and subject = ? and a.sapid = ? ";


			AssignmentFileBean assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{year,month,subject, sapId}, new BeanPropertyRowMapper(AssignmentFileBean .class));

			return assignmentFile;
		} catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return null;

	} 
	@Transactional(readOnly = true)
	public StudentExamBean getRecentRegisterationByStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  "SELECT * FROM exam.registration where sapid=? "
				+ "and sem = (Select max(sem) from exam.registration where sapid = ? )";
		StudentExamBean studentList = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid,sapid}, new BeanPropertyRowMapper(StudentExamBean.class));
		
		return studentList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getProjectBookingforCurrentLiveExamNew(String sapid,String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (select examorder.order  from exam.examorder where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=?) "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid,year,month}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getProjectExemptStudentListNew(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean>	studentList = new ArrayList<>();
		try{
			String sql = "select * from exam.examfeeexempt s, exam.examorder eo where"
					+ " 	s.year = eo.year"
					+ " 	and s.month = eo.month"
					+ "		and eo.order = (select examorder.order  from exam.examorder where projectSubmissionLive = 'Y' and examorder.year=? and examorder.month=?)";

			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{year,month}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
			//
		}
		return studentList;
	} 
	@Transactional(readOnly = true)
	public HashMap<String, ArrayList<String>> getStudentFreeProjectMapNew(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, ArrayList<String>>	studentFreeSubjectsMap = new HashMap<String, ArrayList<String>>();

		try{
			/*
			 * String year = getLiveProjectExamYear(); String month =
			 * getLiveProjectExamMonth();
			 */
			String sql = "select * from exam.examfeeexemptsubject  where"
					+ " 	year = ?"
					+ " 	and month = ?";
			ArrayList<ExamBookingTransactionBean> feeExepmtStudents = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{ year, month}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			for (ExamBookingTransactionBean bean : feeExepmtStudents) {
				if(studentFreeSubjectsMap.containsKey(bean.getSapid())){
					studentFreeSubjectsMap.get(bean.getSapid()).add(bean.getSubject());
				}else{
					ArrayList<String> subjects = new ArrayList<String>();
					subjects.add(bean.getSubject());
					studentFreeSubjectsMap.put(bean.getSapid(), subjects);
				}
			}
		}catch(Exception e){
			
		}
		return studentFreeSubjectsMap;
	}
	
	@Transactional(readOnly = true)
	public AssignmentFileBean findPDWMById(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			String sql = "SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select `order` from exam.examorder where projectSubmissionLive = 'Y' and year=? and month = ?) and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId "; 
			
			
			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getYear(),assignmentFile.getMonth(),assignmentFile.getSubject()
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return assignmentFile;
	}
	
	@Transactional(readOnly = true)
	public String getProjectApplicableSem(String cpsId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sem="";
		try {
			String sql = "select sem from exam.program_sem_subject  where  subject ='Project' and consumerProgramStructureId=? limit 1 ";
				 
			sem = (String)jdbcTemplate.queryForObject(sql, new Object[]{cpsId}, String.class);
			
		}catch (Exception e) { 
		}
		return sem;
	}
 
	@Transactional(readOnly = true)
	public AssignmentFileBean findProjectGuidelinesForApplicableCycle(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//System.out.println("assignmentFile.getConsumerProgramStructureId() "+assignmentFile.getConsumerProgramStructureId().toString());
		try {
			String sql = "SELECT a.* FROM exam.assignments a, exam.examorder eo where "
					+ " a.year = eo.year and a.month = eo.month and "
					+ " "
					//+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ " eo.order = (select `order` from exam.examorder where projectSubmissionLive = 'Y' and year=? and month = ?) and "
					+ " a.subject = ? and a.consumerProgramStructureId = " + assignmentFile.getConsumerProgramStructureId()
					+ " group by a.consumerProgramStructureId ";
			
			
			assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					assignmentFile.getYear(),assignmentFile.getMonth(),assignmentFile.getSubject()
			}, new BeanPropertyRowMapper(AssignmentFileBean .class));
			String startDate = assignmentFile.getStartDate();
			String endDate = assignmentFile.getEndDate();
			if(startDate != null){
				assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
			}
			if(endDate != null){
				assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return assignmentFile;
	}

	@Transactional(readOnly = true)
	public ExamOrderExamBean getLastCycleProjectLiveDetail() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= "select * from exam.examorder eo where eo.order = (select max(examorder.order)-0.5 from exam.examorder where  projectSubmissionLive = 'Y') ";
		ExamOrderExamBean bean = (ExamOrderExamBean) jdbcTemplate.queryForObject(sql, new Object[]{}, new BeanPropertyRowMapper(ExamOrderExamBean .class));
		return bean;
	}

	@Transactional(readOnly = true)
	public List<AssignmentFileBean> getProjectSubmission(final String month, final String year, final String subject, final String sapId) throws SQLException{
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		List<AssignmentFileBean> projectList = null;
		String sql = "SELECT * FROM exam.projectsubmission sb, exam.students s where sb.sapId = s.sapId ";
		if( year != null &&   !("".equals(year))){
			sql = sql + "and sb.year = ? ";
			parameters.add(year);
		}
		if( month != null &&   !("".equals(month))){
			sql = sql + " and sb.month = ? ";
			parameters.add(month);
		}
		if( subject != null &&   !("".equals(subject))){
			sql = sql + " and sb.subject = ? ";
			parameters.add(subject);
		}
		if( sapId != null &&   !("".equals(sapId))){
			sql = sql + " and sb.sapid = ? ";
			parameters.add(sapId);
		}
		sql = sql + " order by sb.subject asc";
		Object[] args = parameters.toArray();
		projectList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(AssignmentFileBean.class));
		return projectList;
	}
	
	@Transactional(readOnly = true)
	/* This query returns a list of SAPIDs whose payments have been received*/
	public List<String> getProjectPaymentStatus(String month, String year) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid from exam.exambookings where  "
				+ " subject=? and month=? and year=? and booked='Y'";
		List<String> paymentStatus = jdbcTemplate.queryForList(sql, new Object[] { subject, month, year },String.class);
		return paymentStatus;

	}
	@Transactional(readOnly = true)
	/* This query returns a list of SAPIDs whose payments have been received not in current live year*/
	public List<String> getProjectPaymentStatusFromHistory(String month, String year) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid from exam.exambookings_history where  "
				+ " subject=? and month=? and year=? and booked='Y'";
		List<String> paymentStatus = jdbcTemplate.queryForList(sql, new Object[] { subject, month, year },String.class);
		return paymentStatus;

	}
	
	@Transactional(readOnly = true)
	/* The following query returns a list of SAPIDs whose projects have been submitted*/
	public List<String> getProjectSubmissionlist(String year, String month) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT sapid FROM exam.projectsubmission where subject = ?  and year= ? and month= ? ";
			List<String> assignmentFile = jdbcTemplate.queryForList(sql, new Object[] {subject, year, month },String.class);
			return assignmentFile;
		
	}
	
}
