package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.AssignmentLiveSettingStudentPortal;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.helpers.PaginationHelper;

@Component
public class AssignmentsDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	//private HashMap<String, FacultyBean> facultyMap = new HashMap<>();
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful";
	private final String TRANSACTION_FAILED = "Transaction Failed";
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private final String BAJAJ_PROGRAM = "ACBM";
	
	/*@Autowired
	StudentMarksDAO marksDao;

	@Autowired
	FacultyDAO facultyDAO;*/

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
	public void saveAssignmentDetails(AssignmentStudentPortalFileBean bean, String year, String month) {
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

		//System.out.println("questionFilePreviewPath = "+questionFilePreviewPath);
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
	public PageStudentPortal<AssignmentStudentPortalFileBean> getAssignmentFilesPage(int pageNo, int pageSize,	AssignmentStudentPortalFileBean searchBean) {
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
		////System.out.println("SQL = "+sql);

		PaginationHelper<AssignmentStudentPortalFileBean> pagingHelper = new PaginationHelper<AssignmentStudentPortalFileBean>();
		PageStudentPortal<AssignmentStudentPortalFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));


		return page;
	}
	/**
	 * Don't call these function it based on old logic exam_order table for assignment reference from exam project same function
	 * */
	/*public AssignmentFileBean findById(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
				+ " subject = ? and a.year = eo.year and a.month = eo.month and"
				+ " eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";

		assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getSubject()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		//System.out.println("Bean: "+assignmentFile);
		String startDate = assignmentFile.getStartDate();
		String endDate = assignmentFile.getEndDate();
		if(startDate != null){
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if(endDate != null){
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}*/
	@Transactional(readOnly = true)
	public AssignmentStudentPortalFileBean findAssignment(AssignmentStudentPortalFileBean assignmentFile) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM exam.assignments  a where "
				+ " subject = ? and a.year = ? and a.month = ?";

		assignmentFile = (AssignmentStudentPortalFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getSubject(), assignmentFile.getYear(), assignmentFile.getMonth()
		}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));

		String startDate = assignmentFile.getStartDate();
		String endDate = assignmentFile.getEndDate();

		if(startDate != null){
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if(endDate != null){
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}

	/*public AssignmentFileBean findResitAssignmentById(AssignmentFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM exam.assignments  a, exam.examorder eo where "
				+ " subject = ? and a.year = eo.year and a.month = eo.month and"
				+ " eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') ";

		assignmentFile = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getSubject()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		//System.out.println("Bean: "+assignmentFile);
		String startDate = assignmentFile.getStartDate();
		String endDate = assignmentFile.getEndDate();
		if(startDate != null){
			assignmentFile.setStartDate(startDate.replaceFirst(" ", "T"));
		}
		if(endDate != null){
			assignmentFile.setEndDate(endDate.replaceFirst(" ", "T"));
		}
		return assignmentFile;
	}*/
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDataForAssignment(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";

			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAssignmentsForSubjects(ArrayList<String> subjects, StudentStudentPortalBean student) {


		List<AssignmentStudentPortalFileBean> assignmentFiles = null;
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
			/**
			 * Commented by sagar,
			 * Reason: Added new ConsumerProgramStructureId for filter
			 * */
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject in ("+subjectCommaSeparated+") and a.startDate <= sysdate() ";

			////System.out.println("Program = "+student.getProgram());
			if(BAJAJ_PROGRAM.equals(student.getProgram())){
				sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";//Fetch assignment set for Bajaj
			}else{
				sql = sql + " and a.program = 'All' "; //Fetch assignments for other students
			}
			
			////System.out.println("SQL = "+sql);
			assignmentFiles = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(AssignmentFileBean .class));*/
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where "
					+ "a.month = eo.month and a.year = eo.year and eo.order = "
					+ "( select max(examorder.order) from exam.examorder where assignmentLive = 'Y' ) "
					+ "and subject in ("+ subjectCommaSeparated +") and "
							+ "a.startDate <= sysdate() and "
							+ "a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();

			// //System.out.println("SQL = "+sql);
			assignmentFiles = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(AssignmentFileBean.class));*/
			/**
			 * Get Assignment query added based on assignment_live_setting table. 
			 * */
			String sql="SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "
					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and  "
					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
					+ "a.subject in ("+ subjectCommaSeparated +") and "
							+ "a.startDate <= sysdate() and a.consumerProgramStructureId = ? and a_l_s.liveType = 'Regular'";
			 //System.out.println("SQL = "+sql);
			assignmentFiles = jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId()},
					new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return assignmentFiles;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getResitAssignmentsForSubjects(ArrayList<String> subjects, StudentStudentPortalBean student) {

		List<AssignmentStudentPortalFileBean> assignmentFiles = null;
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
			/**
			 * Commented by sagar,
			 * Reason: Added new ConsumerProgramStructureId for filter
			 * */
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
					+ " and subject in ("+subjectCommaSeparated+") and a.startDate <= sysdate() ";

			//System.out.println("Program = "+student.getProgram());
			if(BAJAJ_PROGRAM.equals(student.getProgram())){
				sql = sql + " and a.program = '" + BAJAJ_PROGRAM + "' ";//Fetch assignment set for Bajaj
			}else{
				sql = sql + " and a.program = 'All' "; //Fetch assignments for other students
			}
			
			////System.out.println("SQL = "+sql);
			assignmentFiles = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(AssignmentFileBean .class));*/
			
			/*String sql = "SELECT * FROM exam.assignments a, exam.examorder eo where "
					+ "a.month = eo.month and a.year = eo.year and eo.order = "
					+ "( select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y' ) and "
					+ "subject in ("+ subjectCommaSeparated +") and a.startDate <= sysdate() and "
							+ "a.consumerProgramStructureId = " + student.getConsumerProgramStructureId();

			// //System.out.println("SQL = "+sql);
			assignmentFiles = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(AssignmentFileBean.class));*/
			/**
			 * Get Resit Assignment query added based on assignment_live_setting table. 
			 * */
//			gsk changing og query code for handling program change issue---

//			String sql="SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "

//					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and  "

//					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "

//					+ "a.subject in ("+ subjectCommaSeparated +") and "

//							+ "a.startDate <= sysdate() and a.consumerProgramStructureId = ? and a_l_s.liveType = 'Resit'";

//			assignmentFiles = jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId()},

//					new BeanPropertyRowMapper(AssignmentFileBean.class));

//			gsk comment end

			

			List<String> consumerProgramStructureIds = getConsumerProgStructForOldProgramsFromRegistration(student.getSapid());
			if(!consumerProgramStructureIds.contains(student.getConsumerProgramStructureId())) {
				consumerProgramStructureIds.add(student.getConsumerProgramStructureId());
			}	
			String cpsiInString = consumerProgramStructureIds.toString();
			String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);
			////System.out.println(" inside student assnm dao-----consumerProgramStructureId--"+cpsiCommaSeparated);

 			String sql="SELECT a.* FROM exam.assignments a, exam.assignment_live_setting as a_l_s where "
 					+ "a.month = a_l_s.examMonth and a.year = a_l_s.examYear and  "
 					+ "a.consumerProgramStructureId = a_l_s.consumerProgramStructureId and "
 					+ "a.subject in ("+ subjectCommaSeparated +") and "
					+ "a.startDate <= sysdate() and a.consumerProgramStructureId in (" + cpsiCommaSeparated +") and "
					+ "a_l_s.liveType = 'Resit' group by a.subject";

			//System.out.println("SQL = "+sql);

			assignmentFiles = jdbcTemplate.query(sql,new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return assignmentFiles;
	}
	@Transactional(readOnly = true)
	public List<String> getConsumerProgStructForOldProgramsFromRegistration(String sapid){
				List masterKeys = new ArrayList<String>();
				////System.out.println(" inside getConsumerProgStructForOldProgramsFromRegistration dao---------111111111----" );
				try {
				jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "select consumerProgramStructureId from exam.registration where sapid=" + sapid;
					////System.out.println(" inside getConsumerProgStructForOldProgramsFromRegistration dao-------222222222----" );
					masterKeys = jdbcTemplate.queryForList(sql,String.class);
				}
				catch(Exception e) {
					
				}
				////System.out.println(" inside getConsumerProgStructForOldProgramsFromRegistration dao----masterKeys--" +masterKeys);
				return masterKeys;
			}
	@Transactional(readOnly = false)
	public void saveAssignmentSubmissionDetails(AssignmentStudentPortalFileBean bean, int maxAttempts) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO exam.assignmentsubmission (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, status, attempts,  createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (?,?,?,?,?,?,?,1,?,sysdate(),?,sysdate())"
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



		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				sapId,
				studentFilePath,
				previewPath,
				status,
				createdBy,
				lastModifiedBy,

				studentFilePath,
				previewPath,
				status,
				lastModifiedBy
		});


		sql = " INSERT INTO exam.assignmentsubmissionhistory (year, month, subject, sapId,  "
				+ " studentFilePath, previewPath, uploadDate , createdBy, createdDate , "
				+ "lastModifiedBy , lastModifiedDate) VALUES (?,?,?,?,?,?,sysdate(),"
				+ "?, sysdate(),?,sysdate()";

		jdbcTemplate.update(sql, new Object[] { 
				year,
				month,
				subject,
				sapId,
				studentFilePath,
				previewPath,
				createdBy,
				lastModifiedBy

		});

	}

	@Transactional(readOnly = true)
	public HashMap<String, AssignmentStudentPortalFileBean> getSubmissionStatus(	ArrayList<String> subjects, String sapId) {
		HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();
		if(subjects == null || subjects.size() == 0){
			return subjectSubmissionMap;
		}
		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/**
		 * Get assignment status as per new logic from exam.assignment_live_setting table.
		 * */
		/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
				+ " and subject in ("+subjectCommaSeparated+") and a.sapid = ? ";*/

		String sql = "select * from exam.assignmentsubmission abs,exam.assignment_live_setting a_l_s where "
				+ "abs.year = a_l_s.examYear and abs.month = a_l_s.examMonth and "
				+ "a_l_s.liveType = 'Regular' and abs.subject in ("+subjectCommaSeparated+") and "
				+ "abs.sapid = ?;";
		////System.out.println("SQL = "+sql);
		List<AssignmentStudentPortalFileBean> assignmentFiles = jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		if(assignmentFiles!= null && assignmentFiles.size() > 0){
			for (int i = 0; i < assignmentFiles.size(); i++) {
				subjectSubmissionMap.put(assignmentFiles.get(i).getSubject(), assignmentFiles.get(i));
			}
		}
		return subjectSubmissionMap;
	}
	@Transactional(readOnly = true)
	public HashMap<String, AssignmentStudentPortalFileBean> getResitSubmissionStatus(ArrayList<String> subjects, String sapId, StudentStudentPortalBean student) {
		HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();
		if(subjects == null || subjects.size() == 0){
			return subjectSubmissionMap;
		}
		
		String subjectCommaSeparated = "";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}

		List<String> consumerProgramStructureIds = getConsumerProgStructForOldProgramsFromRegistration(student.getSapid());
		if(!consumerProgramStructureIds.contains(student.getConsumerProgramStructureId())) {
			consumerProgramStructureIds.add(student.getConsumerProgramStructureId());
		}	
		String cpsiInString = consumerProgramStructureIds.toString();
		String cpsiCommaSeparated = cpsiInString.substring(1, cpsiInString.length()-1);
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		/**
		 * Get Resit assignment status as per new logic from exam.assignment_live_setting table.
		 * */
		/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
				+ " and subject in ("+subjectCommaSeparated+") and a.sapid = ? ";*/
		
		String sql = "select * from exam.assignmentsubmission abs,exam.assignment_live_setting a_l_s where "
				+ "abs.year = a_l_s.examYear and abs.month = a_l_s.examMonth and "
				+ "a_l_s.liveType = 'Resit' and abs.subject in ("+subjectCommaSeparated+") and "
				+ "abs.sapid = ? and a_l_s.consumerProgramStructureId in ("+ cpsiCommaSeparated +")";

		////System.out.println("SQL = "+sql);
		List<AssignmentStudentPortalFileBean> assignmentFiles = jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		if(assignmentFiles!= null && assignmentFiles.size() > 0){
			for (int i = 0; i < assignmentFiles.size(); i++) {
				subjectSubmissionMap.put(assignmentFiles.get(i).getSubject(), assignmentFiles.get(i));
			}
		}
		return subjectSubmissionMap;
	}
	@Transactional(readOnly = true)
	public AssignmentStudentPortalFileBean getAssignmentStatusForASubject(String subject, String sapId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			/**
			 * Get assignment status as per new logic from exam.assignment_live_setting table.
			 * */
			/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject = ? and a.sapid = ? ";*/

			String sql = "select * from exam.assignmentsubmission abs, exam.assignment_live_setting a_l_s where "
					+ "abs.year = a_l_s.examYear and abs.month = a_l_s.examMonth and a_l_s.liveType = 'Regular' and "
					+ "abs.sapid = ? and abs.subject = ? ;";
			////System.out.println("SQL = "+sql);
			AssignmentStudentPortalFileBean assignmentFile = (AssignmentStudentPortalFileBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,subject}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));

			return assignmentFile;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
	@Transactional(readOnly = true)
	public AssignmentStudentPortalFileBean getResitAssignmentStatusForASubject(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			/**
			 * Get Resit assignment status as per new logic from exam.assignment_live_setting table.
			 * */
			/*String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
					+ " and subject = ? and a.sapid = ? ";*/

			String sql = "select * from exam.assignmentsubmission abs, exam.assignment_live_setting a_l_s where "
					+ "abs.year = a_l_s.examYear and abs.month = a_l_s.examMonth and a_l_s.liveType = 'Resit' and "
					+ "abs.sapid = ? and abs.subject = ? ;";
			////System.out.println("SQL = "+sql);
			AssignmentStudentPortalFileBean assignmentFile = (AssignmentStudentPortalFileBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,subject}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));

			return assignmentFile;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
	@Transactional(readOnly = true)
	public int getPCPBookingCountFromSAPID(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select count(*) from acads.pcpbookings where sapid = ? ";
		int numberOfBookings = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid}, Integer.class);
		return numberOfBookings;
		
	}
	/**
	 * Don't used these function it based on old logic.
	 * */
	/*public AssignmentFileBean getAssignmentDetailsForStudent(	AssignmentFileBean assignmentFile) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
				+ " where asb.month = eo.month and asb.year = eo.year "
				+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
				+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? ";

		AssignmentFileBean assignment = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getSubject(),
				assignmentFile.getYear(),
				assignmentFile.getMonth(),
				assignmentFile.getSapId()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignment;
	}*/
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionStudentPortalBean> getAllConfirmedBookingsFromSapId(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.exambookings eb, exam.examorder eo where eb.sapid = ? and eb.booked = 'Y' and eb.year = eo.year and eb.month = eo.month and eo.order = (SELECT max(examorder.order) from exam.examorder where examorder.timeTableLive = 'Y') group by subject order by tranDateTime asc"; 
		ArrayList<ExamBookingTransactionStudentPortalBean> getAllConfirmedBookingsFromSapIdList = (ArrayList<ExamBookingTransactionStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(ExamBookingTransactionStudentPortalBean.class));
		return getAllConfirmedBookingsFromSapIdList;
	}
	/**
	 * Don't used these function it based on old logic.
	 * */
	/*public AssignmentFileBean getResitAssignmentDetailsForStudent(	AssignmentFileBean assignmentFile) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM exam.assignments a , exam.examorder eo,  exam.assignmentsubmission asb"
				+ " where asb.month = eo.month and asb.year = eo.year "
				+ " and asb.subject = a.subject and asb.year = a.year and asb.month = a.month "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y') "
				+ " and asb.subject = ? and asb.year = ? and asb.month = ? and asb.sapid = ? ";

		AssignmentFileBean assignment = (AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
				assignmentFile.getSubject(),
				assignmentFile.getYear(),
				assignmentFile.getMonth(),
				assignmentFile.getSapId()
		}, new BeanPropertyRowMapper(AssignmentFileBean .class));
		return assignment;
	}*/
	@Transactional(readOnly = true)
	public PageStudentPortal<AssignmentStudentPortalFileBean> getAssignmentSubmissionPage(int pageNo,	int pageSize, AssignmentStudentPortalFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;
		String sql = "SELECT * FROM exam.assignmentsubmission sb, exam.students s where sb.sapId = s.sapId  ";
		String countSql = "SELECT count(*) FROM exam.assignmentsubmission sb, exam.students s where sb.sapId = s.sapId  ";

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
		////System.out.println("SQL = "+sql);

		PaginationHelper<AssignmentStudentPortalFileBean> pagingHelper = new PaginationHelper<AssignmentStudentPortalFileBean>();
		PageStudentPortal<AssignmentStudentPortalFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));


		return page;
	}
	@Transactional(readOnly = true)
	public ArrayList<AssignmentStudentPortalFileBean> getAllANSRecordsForSAPId(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM exam.students s, exam.program_subject ps, exam.registration r "
				+ " where r.program = ps.program and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid and r.sem = ps.sem and s.sapid = ? "
				+ " and concat(s.sapid, ps.subject) not in (select concat(sapid, subject) from exam.assignmentsubmission where sapid = ? ) "
				+ " order by s.sapid asc ";
		ArrayList<AssignmentStudentPortalFileBean> allANSRecordsList = (ArrayList<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{sapid, sapid},new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));
		
		return allANSRecordsList;
	}
	@Transactional(readOnly = true)
	public PageStudentPortal<AssignmentStudentPortalFileBean> getANS(int pageNo, int pageSize, AssignmentStudentPortalFileBean searchBean, String authorizedCenterCodes) {
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
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}


		sql = sql + " order by s.sapid asc";
		Object[] args = parameters.toArray();
		////System.out.println("SQL = "+sql);

		PaginationHelper<AssignmentStudentPortalFileBean> pagingHelper = new PaginationHelper<AssignmentStudentPortalFileBean>();
		PageStudentPortal<AssignmentStudentPortalFileBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));


		return page;
	}
	@Transactional(readOnly = false)
	public void upsertAssignmentStatus(AssignmentStudentPortalFileBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

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
				createdBy
		});


	}
	@Transactional(readOnly = true)
	public int getNumberOfAssignments(AssignmentStudentPortalFileBean assignmentFile, String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;

		if("1".equalsIgnoreCase(level)){
			sql = "select count(*) from exam.assignmentsubmission where year = ? and month = ?  and (facultyId is null or facultyId = '' ) and toBeEvaluated = 'Y' ";
		}else if("2".equalsIgnoreCase(level)){
			sql = "select count(*) from exam.assignmentsubmission where year = ? and month = ?  and (faculty2 is null or faculty2 = '' ) and toBeEvaluated = 'Y' ";
		}else if("3".equalsIgnoreCase(level)){
			return getNumberOfAssignmentsForLevel3(assignmentFile).size();
		}

		sql += " and concat(sapid, subject) not in (select concat(sapid, subject) from exam.passfail where isPass = 'Y')";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());

		if(assignmentFile.getSubject() != null && !"".equals(assignmentFile.getSubject().trim())){
			sql = sql + " and subject = ?   ";
			parameters.add(assignmentFile.getSubject());
		}
		//System.out.println("getNumberOfAssignments: sql : "+sql);
		int numberOfSubjects = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(), Integer.class);
		return numberOfSubjects;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getNumberOfAssignmentsForLevel3(AssignmentStudentPortalFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "select *, (abs((score-faculty2Score))/GREATEST(score, faculty2Score)) as percentDifference "
				+ " from exam.assignmentsubmission where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
				+ " having percentDifference > 0.2";
		 */

		String sql = "select *  from exam.assignmentsubmission "
				+ " where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
				+ " and "
				+ " ("
				+ " (score > -1 and score < 6) "
				+ " or "
				+ " (score > 25 and score < 31) "
				+ " or "
				+ " (faculty2score > 25 and faculty2score < 31)"
				+ " or "
				+ " (faculty2score > -1 and faculty2score < 6) "
				+ " or "
				+ " (abs(faculty2score - score) > 20) "
				+ " )"
				+ " and (score + faculty2score) <> 0 ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());
		parameters.add(assignmentFile.getSubject());

		//System.out.println("getNumberOfAssignmentsForLevel3: sql : "+sql);

		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAssignmentsForReval(AssignmentStudentPortalFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.passfail b, exam.assignmentsubmission a  "
				+ " where year = ? and month = ?  and  a.subject = ? "
				+ "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) "
				+ " and a.sapid = b.sapid and a.subject = b.subject";*/
		
		String sql = "select * from exam.assignmentsubmission   "
				+ " where year = ? and month = ?  and  subject = ? "
				+ "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) ";
		

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());
		parameters.add(assignmentFile.getSubject());

		//System.out.println("getAssignmentsForReval: sql : "+sql);

		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public ArrayList<StudentMarksBean> listOfMarkRecordsFromSapid(StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer("SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and sapid = ? ");
		switch(student.getExamMode())
		{
		case "Online":
			sql.append(" and b.live = 'Y' ");
			break;
		case "Offline":
			sql.append(" and b.oflineResultslive = 'Y' ");
			break;
		}
		sql.append(" order by sem asc  ");
		
		ArrayList<StudentMarksBean> listOfMarkRecordsFromSapid = (ArrayList<StudentMarksBean>)jdbcTemplate.query(sql.toString(),new Object[]{student.getSapid()},new BeanPropertyRowMapper(StudentMarksBean.class));
		return listOfMarkRecordsFromSapid;
	}
	@Transactional(readOnly = true)
	public ArrayList<PassFailBean> listOfPassFailRecordsFromSapid(StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
	/* Commented by Steffi---->	StringBuffer sql = new StringBuffer(" select ps.* from exam.passfail ps, exam.examorder eo where ps.sapid = ? ");
		sql.append(" and ps.writtenyear = eo.year and ps.writtenmonth = eo.month ");
		sql.append(" and concat(ps.assignmentyear,ps.assignmentmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ");
		sql.append(" and concat(ps.writtenyear,ps.writtenmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ");
		sql.append(" group by ps.sapid, ps.subject ");
		sql.append(" order by ps.sem asc");*/
		
		StringBuffer sql = new StringBuffer(" select ps.* from exam.passfail ps, exam.examorder eo where 1=1 and ps.sapid = ? ");
		sql.append(" and (((ps.writtenyear = eo.year or ps.writtenyear = '') and (ps.writtenmonth = eo.month or ps.writtenmonth = '') )");
		sql.append(" or ((ps.assignmentyear = eo.year or ps.assignmentyear = '') and (ps.assignmentmonth = eo.month or ps.assignmentmonth = '') )) ");
		sql.append(" and eo.live = 'Y'");
		sql.append(" group by ps.sapid, ps.subject ");
		sql.append(" order by ps.sem asc");
		
		
		/*switch(student.getExamMode())
		{
		case"Online":
			sql.append(" and concat(ps.assignmentyear,ps.assignmentmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ");
			sql.append(" and concat(ps.writtenyear,ps.writtenmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ");
			sql.append(" group by ps.sapid, ps.subject ");
			sql.append(" order by ps.sem asc");
			break;
		case "Offline":
			sql.append(" and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y') ");
			sql.append(" and concat(sapid, subject) in  ");
			sql.append(" ( ");
			sql.append(" select concat(sapid, subject) from exam.passfail p, exam.examorder eo where sapid = ?  ");
			sql.append(" and ");
			sql.append(" ( ");
			sql.append(" (p.assignmentyear = eo.year and p.assignmentmonth = eo.month ");
			sql.append(" and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')) ");
			sql.append("  OR ");
			sql.append(" (p.writtenyear = eo.year and p.writtenmonth = eo.month ");
			sql.append(" and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')) ");
			sql.append(" ) ) order by p.sem asc");
			break;
		}*/
		ArrayList<PassFailBean> listOfPassFailRecordsFromSapid = (ArrayList<PassFailBean>)jdbcTemplate.query(sql.toString(),new Object[]{student.getSapid()},new BeanPropertyRowMapper(PassFailBean.class));
		return listOfPassFailRecordsFromSapid;
	}
	@Transactional(readOnly = true)
	public ArrayList<PassFailBean> listOfRegistrationsMadeFromSapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.registration where sapid = ? ";
		ArrayList<PassFailBean> listOfPassFailRecordsFromSapid = (ArrayList<PassFailBean>)jdbcTemplate.query(sql,new Object[]{sapid},new BeanPropertyRowMapper(PassFailBean.class));
		return listOfPassFailRecordsFromSapid;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAssignmentsForReReval(AssignmentStudentPortalFileBean assignmentFile) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.passfail b, exam.assignmentsubmission a  "
				+ " where year = ? and month = ?  and  a.subject = ? "
				+ "  and markedForRevaluation = 'Y' and (facultyIdRevaluation is null or facultyIdRevaluation = '' ) "
				+ " and a.sapid = b.sapid and a.subject = b.subject";*/
		
		String sql = "select s.*, m.assignmentscore from exam.assignmentsubmission s, exam.marks m  "
				+ " where s.year = ? and s.month = ?   "
				+ " and s.markedForRevaluation = 'Y'  "
				+ " and s.year = m.year and s.month = m.month "
				+ " and s.sapid = m.sapid and s.subject = m.subject"
				+ " and (ABS((revaluationScore - assignmentscore ))/ assignmentscore ) >= 0.2 "
				+ " and (faculty2 is null or faculty2 = '' ) "
				+ " order by s.subject asc";
		

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(assignmentFile.getYear());
		parameters.add(assignmentFile.getMonth());

		//System.out.println("getAssignmentsForReval: sql : "+sql);

		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, parameters.toArray(), new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAssignments(AssignmentStudentPortalFileBean assignmentFile, String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		if("1".equalsIgnoreCase(level)){
			sql = "select year, month, sapid, subject from exam.assignmentsubmission where year = ? and "
					+ "month = ? and subject = ?  and (facultyId is null or facultyId = '' )  and toBeEvaluated = 'Y' ";
		}else if("2".equalsIgnoreCase(level)){
			sql = "select year, month, sapid, subject from exam.assignmentsubmission where year = ? and "
					+ "month = ? and subject = ?  and (faculty2 is null or faculty2 = '' )  and toBeEvaluated = 'Y' ";
		}else if("3".equalsIgnoreCase(level)){
			/*sql = "select *, (abs((score-faculty2Score))/GREATEST(score, faculty2Score)) as percentDifference "
					+ " from exam.assignmentsubmission where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
					+ " having percentDifference > 0.2";*/

			sql = "select *  from exam.assignmentsubmission "
					+ " where year = ? and month = ?  and  subject = ?   and (faculty3 is null or faculty3 = '' ) "
					+ " and "
					+ " ("
					+ " (score > -1 and score < 6) "
					+ " or "
					+ " (score > 25 and score < 31) "
					+ " or "
					+ " (faculty2score > 25 and faculty2score < 31)"
					+ " or "
					+ " (faculty2score > -1 and faculty2score < 6) "
					+ " or "
					+ " (abs(faculty2score - score) > 20) "
					+ " )"
					+ " and (score + faculty2score) <> 0 ";
		}

		sql += " and concat(sapid, subject) not in (select concat(sapid, subject) from exam.passfail where isPass = 'Y')";

		//System.out.println("getNumberOfAssignments(): sql : "+sql);
		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{assignmentFile.getYear(), assignmentFile.getMonth(), assignmentFile.getSubject()}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = false)
	public void allocateAssignment(final List<AssignmentStudentPortalFileBean> assignmentsSubSet, final	String facultyId, final String level) {
		String sql = null;

		if("1".equalsIgnoreCase(level)){
			sql = "update exam.assignmentsubmission "
					+ " set facultyId = ? "
					+ " where year = ? "
					+ " and month = ?"
					+ " and sapid = ?"
					+ " and subject = ? ";
		}else if("2".equalsIgnoreCase(level)){
			sql = "update exam.assignmentsubmission "
					+ " set faculty2 = ? "
					+ " where year = ? "
					+ " and month = ?"
					+ " and sapid = ?"
					+ " and subject = ? ";
		}else if("3".equalsIgnoreCase(level)){
			sql = "update exam.assignmentsubmission "
					+ " set faculty3 = ? "
					+ " where year = ? "
					+ " and month = ?"
					+ " and sapid = ?"
					+ " and subject = ? ";
		}

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				AssignmentStudentPortalFileBean a = assignmentsSubSet.get(i);
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
	public void allocateAssignmentForReval(final List<AssignmentStudentPortalFileBean> assignmentsSubSet) {
		
		String sql = "update exam.assignmentsubmission "
				+ " set facultyIdRevaluation = ? "
				+ " where year = ? "
				+ " and month = ?"
				+ " and sapid = ?"
				+ " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				AssignmentStudentPortalFileBean a = assignmentsSubSet.get(i);
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
public void allocateAssignmentForReReval(final List<AssignmentStudentPortalFileBean> assignmentsSubSet) {
		
		String sql = "update exam.assignmentsubmission "
				+ " set faculty2 = ? "
				+ " where year = ? "
				+ " and month = ?"
				+ " and sapid = ?"
				+ " and subject = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				AssignmentStudentPortalFileBean a = assignmentsSubSet.get(i);
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
	public List<AssignmentStudentPortalFileBean> getAllAsignments() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission a, exam.students s where year = 2015 and month = 'Dec' "
				+ " and facultyId is not null and facultyId <> '' "
				+ " and faculty2 is not null and faculty2 <> ''"
				+ " and a.sapid = s.sapid ";
		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	
	
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAllAsignmentsForLevel1(AssignmentStudentPortalFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission a, exam.students s where year = ? and month = ? "
				+ " and facultyId is not null and facultyId <> '' "
				+ " and a.sapid = s.sapid ";
		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{
				searchBean.getYear(), searchBean.getMonth()
		},new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
		return assignments;
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getCopyCases(AssignmentStudentPortalFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission a, exam.students s where year = ? and month = ? "
				+ " and a.sapid = s.sapid "
				+ " and finalReason like '%Copy Case%' ";
		List<AssignmentStudentPortalFileBean> assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{searchBean.getYear(), searchBean.getMonth()},new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));

		return assignments;
	}
	@Transactional(readOnly = true)
	public int getPastCycleAssignmentAttempts(String subject, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.assignmentsubmission asb, exam.examorder eo where sapid = ? "
				+ " and subject = ? and asb.year = eo.year and asb.month = eo.month and eo.order >= 15.5"; //15.5 means April-2016 onwards
		
		int numberOfAttempts = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,subject },Integer.class);
		return numberOfAttempts;
	}
	@Transactional(readOnly = true)
	public boolean checkIfAssignmentFeesPaid(String subject,	String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.assignmentpayment where sapid = ? "
				+ " and subject = ? and booked = 'Y' "; 
		
		int bookingCount = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapId,subject },Integer.class);
		if(bookingCount > 0 ){
			return true;
		}else{
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsNeedingAssignmentPayments(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select distinct asb.subject from exam.assignmentsubmission asb, exam.examorder eo "
				+ " where sapid = ? and asb.year = eo.year and asb.month = eo.month "
				+ " and eo.order >= 15.5 "
				+ " and subject in (Select subject from exam.passfail where isPass = 'N' and sapid = ? )"
				+ " group by subject "
				+ " having count(subject)  >= 2 ";
				

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid, sapid}, new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsMadeAssignmentPayments(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select distinct subject from exam.assignmentpayment ap,exam.examorder eo where sapid = ? and booked = 'Y'"
				+ " and ap.month = eo.month and ap.year = eo.year  and eo.order = (select max(examorder.order) from exam.examorder where resitAssignmentLive = 'Y')";

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getSingleStudentWithValidity(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentStudentPortalBean student = null;
		try{
			String sql = "select * from exam.students s, exam.examorder eo where"
					+ "		s.sapid = ?"
					+ "     and s.sem = (Select max(sem) from exam.students where sapid = ? )"
					+ " 	and s.validityendyear = eo.year"
					+ " 	and s.validityendmonth = eo.month"
					+ "		and eo.order >= (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					;

			student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));

		}catch(Exception e){
			//System.out.println("getSingleStudentWithValidity :"+e.getMessage());
		}
		return student;
	}
	@Transactional(readOnly = true)
	public int getTotalNumberOfSessionsAttended(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select count(*) from acads.session_attendance_feedback where attended = 'Y' and sapid = ? group by sapid ";
		int countOfSessionsAttended;
		try{
			countOfSessionsAttended = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
			return countOfSessionsAttended;
		}catch(Exception e){
			return 0;
		}
		
		
	}
	@Transactional(readOnly = true)
	public ArrayList<SessionQueryAnswerStudentPortal> getSessionAttendedDetailList(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*
		 * Commented by Steffi
		 * String sql = " select saf.sessionId,asi.facultyId,sq.query,sq.answer,asi.subject,asi.sessionName from acads.session_attendance_feedback saf,acads.sessions asi,acads.session_query_answer sq "
				+ " where saf.sessionId = asi.id and sq.sessionId = asi.id and saf.sapid = ? ";*/
		
		 String sql = " select sq.query,sq.answer,sq.sessionId,sq.assignedToFacultyId as facultyId,asi.subject,asi.sessionName"
		 		+ " from  acads.session_query_answer sq , acads.sessions asi "
		 		+ " where sq.sapId = ? "
		 		+ " and sq.sessionId = asi.id ";
		ArrayList<SessionQueryAnswerStudentPortal> sessionAttendedDetailList = (ArrayList<SessionQueryAnswerStudentPortal>)jdbcTemplate.query(sql,new Object[]{sapid},new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		
		return sessionAttendedDetailList;
		
	}
	
	@Transactional(readOnly = true)
	public boolean isResultLiveForLastAssignmentSubmissionCycle(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " select eo.live from exam.examorder eo where eo.order = "
				+ "(select max(examorder.order)-0.5 from exam.examorder where resitAssignmentLive = 'Y')";
		String Live = null;
		try{
			Live =  (String) jdbcTemplate.queryForObject(
		            sql, new Object[] {  }, String.class);

		}catch(Exception e){
			
		}

		if("Y".equalsIgnoreCase(Live)){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<AssignmentStudentPortalFileBean> getResultAwaitedAssignmentSubmittedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.assignmentsubmission where sapid = ? and concat(sapid,subject) not in (select concat(sapid,subject) from exam.passfail where sapid = ?)";
		
		ArrayList<AssignmentStudentPortalFileBean> assignmentList = (ArrayList<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, 
				new Object[] {sapid,sapid},	new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));
		
		return assignmentList;
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
			
			//System.out.println(sql);
			
			ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
			if(subjectsList == null){
				subjectsList = new ArrayList<String>();
			}
			
			//System.out.println("subjectsList = " + subjectsList);
			return subjectsList;
		} catch (Exception e) {
//			
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
					+ " and eb.sapid = ?  and eb.booked = 'Y'";
					//+ " and eb.subject in (" + subjectCommaSeparated + ") ";
			
			//System.out.println(sql);
			
			ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
			if(subjectsList == null){
				subjectsList = new ArrayList<String>();
			}
			
			//System.out.println("subjectsList = " + subjectsList);
			return subjectsList;
		} catch (Exception e) {
//			
		}
		
		return new ArrayList<String>(); 
		
	}
	@Transactional(readOnly = true)
	public ArrayList<String> assignmentExtendedSubmissionTime(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select concat(sapid,subject) as extendedAssignmentTime from  exam.extended_assignment_submission  ";
		ArrayList<String> timeExtendedStudentList = (ArrayList<String>)jdbcTemplate.query(sql,new Object[] {},new SingleColumnRowMapper(String.class));
	    //System.out.println("list----"+timeExtendedStudentList);
		return timeExtendedStudentList;
	
	}
	@Transactional(readOnly = true)
	public AssignmentLiveSettingStudentPortal getCurrentLiveAssignment(String consumerProgramStructureId,String liveType){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "select * from exam.assignment_live_setting where consumerProgramStructureId = ? and liveType = ? limit 1";
			//System.out.println("****************** " + consumerProgramStructureId + " | " + liveType);
			AssignmentLiveSettingStudentPortal assignmentLiveSetting = (AssignmentLiveSettingStudentPortal)jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId,liveType},new BeanPropertyRowMapper(AssignmentLiveSettingStudentPortal.class) );
		    ////System.out.println("list----"+timeExtendedStudentList);
			return assignmentLiveSetting;
		}
		catch (Exception e) {
			return null;
		}
	
	}
	@Transactional(readOnly = true)
	public int checkHasAssignment(String consumerProgramStructureId) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT count(hasAssignment) as count FROM exam.program_sem_subject where consumerProgramStructureId=? and hasAssignment='Y';";
			return (int) jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(Integer.class));
		}
		catch (Exception e) {
//			
			return 0;
		}
	}
	@Transactional(readOnly = true)
	public List<AssignmentStudentPortalFileBean> getAllSubmittedAsignmentsBySubject(String sapid,String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<AssignmentStudentPortalFileBean> assignments = new ArrayList<AssignmentStudentPortalFileBean>();
		return assignments;
	}
	
	public List<AssignmentStudentPortalFileBean> getQuickAssignmentsForSingleStudent(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<AssignmentStudentPortalFileBean> assignments = new ArrayList<AssignmentStudentPortalFileBean>();
			String sql = "select * from exam.quick_assignmentsubmission where sapid=?";
			try {
				assignments = (List<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(AssignmentStudentPortalFileBean .class));
			} catch (Exception e) {
			} 
			return assignments;
	}
}
