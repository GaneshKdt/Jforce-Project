package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.itextpdf.text.log.SysoCounter;

import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.DissertationResultDTO;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamResultsBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEERescheduleExamBookingExcelBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.dto.TEEResultDTO;

@Repository("examsAssessmentsDAO")
public class ExamsAssessmentsDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate= new JdbcTemplate(dataSource);
		this.namedJdbcTemplate= new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Value("${TEST_USER_SAPIDS}")
	private String TEST_USER_SAPIDS;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
 	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
 	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
 	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAX_ACAD_MONTH}")
 	private String CURRENT_MBAX_ACAD_MONTH;
	
	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamsAssessmentsBean> getExamAssessments(){
		ArrayList<ExamsAssessmentsBean> examsAssessmentsListBean = new ArrayList<ExamsAssessmentsBean>();
		try {
			String sql = /*"SELECT e_a.*, e_s.schedule_id, e_s.schedule_name, e_s.exam_start_date_time, "
					+ "e_s.exam_end_date_time, p_s_s.subject, s_s_c.batchId as batch_id FROM exam.exams_assessments e_a, "
					+ "exam.exams_schedule e_s, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s, exam.assessment_timebound_id e_t_i  "
					+ "WHERE e_s.assessments_id = e_a.id and s_s_c.id = e_t_i.timebound_id and "
					+ "s_s_c.prgm_sem_subj_id = p_s_s.id and e_t_i.assessments_id = e_a.id"*/
					"SELECT e_t_i.assessments_id, e_s.schedule_id, e_s.exam_start_date_time, e_b.name as batchName, " + 
					"		e_s.exam_end_date_time, e_s.schedule_name, s_s_c.batchId as batch_id, " + 
					"		p_s_s.subject, e_a.customAssessmentName,  e_a.name " + 
					"FROM exam.assessment_timebound_id e_t_i " + 
					"LEFT JOIN " + 
					"exam.exams_schedule e_s on e_s.assessments_id = e_t_i.assessments_id and e_s.timebound_id = e_t_i.timebound_id  " + 
					"LEFT JOIN  " + 
					"lti.student_subject_config s_s_c on e_t_i.timebound_id = s_s_c.id " + 
					"LEFT JOIN  " + 
					"exam.program_sem_subject p_s_s on s_s_c.prgm_sem_subj_id = p_s_s.id   " + 
					"LEFT join " + 
					"exam.exams_assessments e_a on  e_a.id = e_t_i.assessments_id "+
					"LEFT JOIN " +  
					"exam.batch e_b on e_b.id = s_s_c.batchId ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			examsAssessmentsListBean = (ArrayList<ExamsAssessmentsBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(ExamsAssessmentsBean.class));
			return examsAssessmentsListBean;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return examsAssessmentsListBean;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<ExamsAssessmentsBean> getExamAssessmentsForActiveBatches(String mbaWxAcadMonth,String mbaWxAcadYear,String pddmAcadMonth,String pddmAcadYear){
		ArrayList<ExamsAssessmentsBean> examsAssessmentsListBean = new ArrayList<ExamsAssessmentsBean>();
		try {
			String sql = /*"SELECT e_a.*, e_s.schedule_id, e_s.schedule_name, e_s.exam_start_date_time, "
					+ "e_s.exam_end_date_time, p_s_s.subject, s_s_c.batchId as batch_id FROM exam.exams_assessments e_a, "
					+ "exam.exams_schedule e_s, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s, exam.assessment_timebound_id e_t_i  "
					+ "WHERE e_s.assessments_id = e_a.id and s_s_c.id = e_t_i.timebound_id and "
					+ "s_s_c.prgm_sem_subj_id = p_s_s.id and e_t_i.assessments_id = e_a.id"*/
					"SELECT e_t_i.assessments_id, e_s.schedule_id, e_s.exam_start_date_time, e_b.name as batchName, " + 
					"		e_s.exam_end_date_time, e_s.schedule_name, s_s_c.batchId as batch_id, " + 
					"		p_s_s.subject, e_a.customAssessmentName,  e_a.name " + 
					"FROM exam.assessment_timebound_id e_t_i " + 
					"LEFT JOIN " + 
					"exam.exams_schedule e_s on e_s.assessments_id = e_t_i.assessments_id and e_s.timebound_id = e_t_i.timebound_id  " + 
					"LEFT JOIN  " + 
					"lti.student_subject_config s_s_c on e_t_i.timebound_id = s_s_c.id " + 
					"LEFT JOIN  " + 
					"exam.program_sem_subject p_s_s on s_s_c.prgm_sem_subj_id = p_s_s.id   " + 
					"LEFT join " + 
					"exam.exams_assessments e_a on  e_a.id = e_t_i.assessments_id "+
					"LEFT JOIN " +  
					"exam.batch e_b on e_b.id = s_s_c.batchId "+
					"WHERE (e_b.acadMonth,e_b.acadYear) in ((?,?),(?,?)) ";
					
			jdbcTemplate = new JdbcTemplate(dataSource);
			examsAssessmentsListBean = (ArrayList<ExamsAssessmentsBean>) jdbcTemplate.query(sql, new Object[] {mbaWxAcadMonth,mbaWxAcadYear,pddmAcadMonth,pddmAcadYear},
					new BeanPropertyRowMapper(ExamsAssessmentsBean.class));
			return examsAssessmentsListBean;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return examsAssessmentsListBean;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getAssessmentListByTimeBoundId(int time_bound_id){
		ArrayList<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT ea.*, ea.name AS `customAssessmentName` FROM exam.exams_assessments ea LEFT JOIN  exam.assessment_timebound_id ati on ati.assessments_id =ea.id where ati.timebound_id = ?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			assessmentList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql, new Object[] {time_bound_id},
					new BeanPropertyRowMapper(MettlResponseBean.class));
			return assessmentList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getMBXAssessmentListByTimeBoundId(int time_bound_id){
		ArrayList<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT ea.* FROM exam.mbax_assessments ea LEFT JOIN  exam.mbax_assessment_timebound_id ati on ati.assessments_id =ea.id where ati.timebound_id = ?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			assessmentList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql, new Object[] {time_bound_id},
					new BeanPropertyRowMapper(MettlResponseBean.class));
			return assessmentList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getScheduleListByAssessmentId(int assessment_id,int time_id){
		ArrayList<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT * FROM exam.exams_schedule where assessments_id = ? and timebound_id=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			assessmentList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql, new Object[] {assessment_id,time_id},
					new BeanPropertyRowMapper(MettlResponseBean.class));
			return assessmentList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getMBAXScheduleListByAssessmentId(int assessment_id,int time_id){
		ArrayList<MettlResponseBean> assessmentList = new ArrayList<MettlResponseBean>();
		try {
			String sql = "SELECT * FROM exam.mbax_schedule where assessments_id = ? and timebound_id=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			assessmentList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql, new Object[] {assessment_id,time_id},
					new BeanPropertyRowMapper(MettlResponseBean.class));
			return assessmentList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return assessmentList;
		}
	} 
	
	@Transactional(readOnly = true)
	public ArrayList<StudentSubjectConfigExamBean> getSubjectByBatchId(int batch_id){
		ArrayList<StudentSubjectConfigExamBean> subjectList = new ArrayList<StudentSubjectConfigExamBean>();
		try {
//			String sql = "SELECT s_s_c.*,p_s_s.subject FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s where s_s_c.prgm_sem_subj_id = p_s_s.id and s_s_c.batchId = ?;";
			String sql = " SELECT  " + 
					"    s_s_c.*, " + 
					"    sc.subjectname AS `subject`, " + 
					"    scmap.hasIA, " + 
					"    scmap.hasTEE " + 
					"FROM " + 
					"    lti.student_subject_config s_s_c " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode_mapping scmap ON scmap.id = s_s_c.prgm_sem_subj_id " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode sc ON sc.id = scmap.subjectCodeId " + 
					"WHERE " + 
					"    s_s_c.batchId = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			subjectList = (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, new Object[] {batch_id},
					new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
			return subjectList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return subjectList;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentSubjectConfigExamBean> getMBAXSubjectByBatchId(int batch_id){
		ArrayList<StudentSubjectConfigExamBean> subjectList = new ArrayList<StudentSubjectConfigExamBean>();
		try {
			String sql = "SELECT s_s_c.*,p_s_s.subject FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s where s_s_c.prgm_sem_subj_id = p_s_s.id and p_s_s.consumerProgramStructureId = 119 and s_s_c.batchId = ?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			subjectList = (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, new Object[] {batch_id},
					new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
			return subjectList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return subjectList;
		}
	}
	
	@Transactional(readOnly = true)
	public String getTimeBoundId(String subject,String batch_id, String startDate){
		try {
			String sql = "SELECT s_s_c.id FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s "
					+ "where s_s_c.prgm_sem_subj_id = p_s_s.id and batchId = ? and p_s_s.subject=? and s_s_c.startDate = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate.queryForObject(sql, new Object[] {batch_id,subject, startDate},String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchesList(int consumerProgramStructureId){
		ArrayList<BatchExamBean> batchList = new ArrayList<BatchExamBean>();
		try {
			String sql = "SELECT * FROM exam.batch where consumerProgramStructureId = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {
				consumerProgramStructureId
			},
					new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class));
			return batchList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return batchList;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchesListForMbaWx(){
		String sql = "SELECT * FROM exam.batch where consumerProgramStructureId in (111, 131, 151, 158, 148, 144, 149, 142, 143, 147, 145, 146, 160 ) order by id desc ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return (ArrayList<BatchExamBean>) jdbcTemplate.query(
			sql, 
			new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<BatchExamBean> getBatchesList(List<Integer> consumerProgramStructureId){
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("consumerProgramStructureId", consumerProgramStructureId);
		String sql = "SELECT * FROM exam.batch where consumerProgramStructureId in ( :consumerProgramStructureId ) order by id desc ";
		return namedJdbcTemplate.query(
			sql, queryParams,
			new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class)
		);
	}
	
	@Transactional(readOnly = false)
	public String insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,"
					+ "`exam_start_date_time`,`exam_end_date_time`,`reporting_start_date_time`,`reporting_finish_date_time`,`createdBy`,`lastModifiedBy`,`max_score`) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getReporting_start_date_time(),
					examsAssessmentsBean.getReporting_finish_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score());

			//String sql2 = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?)";
			String sql2 = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`, `duration`, `programType`, `createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?,?)";

			jdbcTemplate.update(sql2,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getName(),
					examsAssessmentsBean.getCustomAssessmentName(),
					examsAssessmentsBean.getDuration(),
					examsAssessmentsBean.getProgramType(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			String sql3 = "insert into exam.assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("Error:"+e.getMessage());
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Assessment already exist in portal";
			}
			return e.getMessage();
		}

	}
	
	@Transactional(readOnly = false)
	public String insertIntoExamSchedule(ExamsAssessmentsBean examsAssessmentsBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "insert into exam.exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,`exam_end_date_time`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql,new Object[] {
					examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy()
			});
			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String insertIntoExamAssessment(ExamsAssessmentsBean examsAssessmentsBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			//String sql = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?,?)";
			String sql = "insert into exam.exams_assessments(`id`,`name`,`customAssessmentName`,`createdBy`,`lastModifiedBy`) values(?,?,?,?,?)";

			jdbcTemplate.update(sql,new Object[] {
					examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getName(),
					examsAssessmentsBean.getCustomAssessmentName(),
					//examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy()
			});
			return "success";
		}
		catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				return "Assessment already exist in portal";
			}
			// TODO: handle exception
			return e.getMessage();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public int upsertTEEMarks(final ArrayList<TEEResultBean> teeResultBeans) {
		try {
			// Insert tracking numbers for current interaction
			String sql = " "
					+ " INSERT INTO `exam`.`tee_marks` ( "
						+ " `timebound_id`, `schedule_id`, `student_name`, "
						+ " `sapid`, `score`, `max_score`, "
						+ " `createdBy`, `lastModifiedBy`, `processed` "
					+ " ) VALUES ( "
						+ " ?, ?, ?, "
						+ " ?, ?, ?, "
						+ " ?, ?, 'N' "
					+ " ) ON DUPLICATE KEY "
					+ " UPDATE `score` = ?, `processed` = 'N', `schedule_id` = ? ";

			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] teeMarksIds = jdbcTemplate.batchUpdate(sql,
					new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					TEEResultBean bean = teeResultBeans.get(i);
					ps.setString(1, bean.getTimebound_id());
					ps.setString(2, bean.getSchedule_id());
					ps.setString(3, bean.getStudentname());
					ps.setString(4, bean.getSapId());
					ps.setInt(5, bean.getScore());
					ps.setString(6, bean.getMax_score());
					ps.setString(7, bean.getCreatedBy());
					ps.setString(8, bean.getLastModifiedBy());
					ps.setInt(9, bean.getScore());
					ps.setString(10, bean.getSchedule_id());
				}

				public int getBatchSize() {
					return teeResultBeans.size();
				}
			});
			return teeMarksIds.length;
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	
	
	@Transactional(readOnly = true)
	public String getSapidByEmailIDAndMasterKey(String emailId,int masterKey) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select sapid from exam.students where emailId = ? and consumerProgramStructureId = ?";
			return (String) jdbcTemplate.queryForObject(sql,new Object[] {emailId,masterKey},String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<TEEResultBean> getTeeMarksStudentList(){
		ArrayList<TEEResultBean> teeResultBeans = new ArrayList<TEEResultBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT t_m.*, s_s_c.batchId, p_s_s.subject FROM exam.tee_marks t_m, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s WHERE s_s_c.id = t_m.timebound_id AND s_s_c.prgm_sem_subj_id = p_s_s.id;";
		teeResultBeans = (ArrayList<TEEResultBean>) jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(TEEResultBean.class));
		return teeResultBeans;
	}
	
	@Transactional(readOnly = true)
	public List<TEEResultBean> getTeeMarksStudentListForCapstone(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `tm`.*, `ssc`.`batchId`, `pss`.`subject` "
				+ " FROM `exam`.`tee_marks` `tm` "
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` ON `tm`.`timebound_id` = `ssc`.`id` "
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
				+ " WHERE `pss`.`subject` = 'Capstone Project' ";
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
		);
	}
	
	//Get time-bound student project marks based on the subject or time-bound id. 
	@Transactional(readOnly = true)
	public List<TEEResultBean> getStudentProjectMarks(String subjectName, Integer timeboundId){
		List<Object> parameters = new ArrayList<Object>(2);
		//Prepare SQL query
		StringBuilder GET_PROJECT_MARKS = new StringBuilder("SELECT `ccm`.*, `ssc`.`batchId`, `pss`.`subject` FROM `exam`.`capstone_component_marks` `ccm` ")
					.append(" INNER JOIN `lti`.`student_subject_config` `ssc` ON `ccm`.`timebound_id` = `ssc`.`id` ")
						.append(" INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` WHERE 1=1 ");
		
		//append subject check to the SQL query and add its value in parameter list.
		if(!StringUtils.isEmpty(subjectName)) {
			GET_PROJECT_MARKS.append("AND `pss`.`subject` = ? ");
			parameters.add(subjectName);
		}
		//append timeboundId check to the SQL query and add its value in parameter list.
		if(timeboundId!=null) {
			GET_PROJECT_MARKS.append("AND `ssc`.`id` = ? ");
			parameters.add(timeboundId);
		}
		
		//Execute jdbcTemplate query method and return list of project marks record.
		return jdbcTemplate.query( GET_PROJECT_MARKS.toString(),parameters.toArray(),
			new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
		);
	}//getStudentProjectMarks(-,-)
	
	@Transactional(readOnly = true)
	public String getTEEScore (String sapid, Long timebound_id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String TEEScore = "";

		String sql = "SELECT score as TEEScore FROM exam.tee_marks where sapid = ? and timebound_id = ? "; 
		try {
			TEEScore = (String)jdbcTemplate.queryForObject(sql, new Object[] {sapid,timebound_id}, String.class);

		} catch (Exception e) {
			//
		}
		return TEEScore;
	}
	
	@Transactional(readOnly = true)
	public List<TEEResultBean> getAllTEEScoresForStudent (String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TEEResultBean> TEEScoresList = new ArrayList<TEEResultBean>();

		String sql = ""
				+ "SELECT "
				+ "`tm`.*, subject "
				+ "FROM "
				+ "exam.tee_marks `tm`"
				+ "LEFT JOIN "
				+ "`exam`.`exams_schedule` `es` "
				+ "ON "
				+ "`tm`.`timebound_id` = `es`.`timebound_id` "
				+ "Inner join " 
				+ "lti.student_subject_config ssc " 
				+ "On tm.timebound_id = ssc.id   " 
				+ "Inner join   " 
				+ "exam.program_sem_subject pss    " 
				+ "on pss.id = ssc.prgm_sem_subj_id "
				+ "WHERE "
				+ "sapid = ? "
				+ "AND "
				+ "isResultLive = 'Y'"; 
		try {
			TEEScoresList = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));

		} catch (Exception e) {
			//
		}
		return TEEScoresList;
	}
	
	@Transactional(readOnly = true)
	public String isResultLiveFlag (Long timebound_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.exams_schedule where timebound_id = ? and isResultLive = 'Y'";
		int isResultLive = 0;		
		try {
			isResultLive = (int) jdbcTemplate.queryForObject(sql, new Object[] {timebound_id},Integer.class);
			if (isResultLive > 0) {
				return "Y";
			} else {
				return "N";
			}			
		} catch (Exception e) {
			
			return "N";
		}
	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getTeeAssessmentsBySapid(String sapId) {
		List<ExamsAssessmentsBean> teeAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = ""
				+ " SELECT es.*,ea.name,ea.customAssessmentName "
				+ " FROM exam.exams_assessments ea "
				
				+ " INNER JOIN exam.exams_schedule es "
				+ " ON es.assessments_id = ea.id "
				
				+ " INNER JOIN exam.assessment_timebound_id ati "
				+ " ON ati.assessments_id = ea.id "
				
				+ " INNER JOIN  lti.timebound_user_mapping tum "
				+ " ON tum.timebound_subject_config_id = es.timebound_id "
				
				+ " WHERE "
				+ " tum.userId = ?"
				+ " AND ( tum.role = 'Student' OR es.max_score = 100 ) ";
		 try {
			 teeAssessmentsList = jdbcTemplate.query(
				 sql, 
				 new Object[] {
					 sapId
				 }, 
				 new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class)
			 );
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicable(sapId, teeAssessmentsList);
		
		 
	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getTeeAssessmentsBySapidFromScheduleInfo(String sapId) {
		List<ExamsAssessmentsBean> teeAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = "select es.id,es.assessments_id,sm.timebound_id,sm.schedule_id,sm.scheduleName as schedule_name,sm.acessKey as schedule_accessKey "
                +" ,sm.joinURL as schedule_accessUrl,es.schedule_status,sm.accessStartDateTime as exam_start_date_time,sm.accessEndDateTime as exam_end_date_time, "
                +" es.active,es.isResultLive, sm.max_score, sm.accessEndDateTime as endDate,sm.accessStartDateTime as startDate, ea.name, ea.customAssessmentName " 
                +" from exam.exams_scheduleinfo_mettl sm INNER JOIN exam.exams_schedule es ON sm.acessKey=es.schedule_accessKey and sm.timebound_id=es.timebound_id "
				+" INNER JOIN exam.exams_assessments ea ON es.assessments_id=ea.id where sm.sapid= ?";
		 try {
			 teeAssessmentsList = jdbcTemplate.query(
				 sql, 
				 new Object[] {
					 sapId
				 }, 
				 new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class)
			 );
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicableFromScheduleInfo(sapId, teeAssessmentsList);
		
		 
	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getPendingTeeAssessmentsBySapid(String sapId) {
		List<ExamsAssessmentsBean> teePendingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "SELECT  " + 
				"    es.*, " + 
				"    es.exam_end_date_time AS endDate, " + 
				"    es.exam_start_date_time AS startDate, " + 
				"    ea.name, " + 
				"    ea.customAssessmentName AS testName, " + 
				"    'assessments' AS type, " + 
				"    pss.subject " + 
				"FROM " + 
				"				     exam.exams_assessments ea " + 
				"				         INNER JOIN " + 
				"				     exam.exams_schedule es ON es.assessments_id = ea.id " + 
				"				         INNER JOIN " + 
				"						exam.assessment_timebound_id ati ON  ati.assessments_id = ea.id " + 
				"                        INNER JOIN " + 
				"				     lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ati.timebound_id " + 
				"				 		INNER JOIN " + 
				"				     lti.student_subject_config ssc ON ssc.id =  ati.timebound_id " + 
				"				 		INNER JOIN " + 
				"				   exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				"WHERE " + 
				"    tum.userId = ? "+
				"   AND ( tum.role =  'Student' OR (es.max_score = 100 AND tum.role in ( 'Resit', 'Student' )) ) "+
				"        AND SYSDATE() <= es.exam_end_date_time " + 
//				"        AND SYSDATE() BETWEEN ssc.startDate AND ssc.endDate " + 
				"ORDER BY es.exam_start_date_time";
		 try {
			 teePendingAssessmentsList = jdbcTemplate.query(sql,new Object[] {sapId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicable(sapId, teePendingAssessmentsList);
		
		 
		}
	
	

	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getPendingTeeAssessmentsFromScheduleInfo(String sapId) {
		List<ExamsAssessmentsBean> teePendingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "select es.id,es.assessments_id,sm.timebound_id,sm.schedule_id,sm.scheduleName as schedule_name,sm.acessKey as schedule_accessKey "
                +" ,sm.joinURL as schedule_accessUrl,es.schedule_status,sm.accessStartDateTime as exam_start_date_time,sm.accessEndDateTime as exam_end_date_time, "
                +" es.active,es.isResultLive, sm.max_score, sm.accessEndDateTime as endDate,sm.accessStartDateTime as startDate, ea.name, ea.customAssessmentName as testName, " 
                +" 'assessments' AS type, sm.subject "
                +" from exam.exams_scheduleinfo_mettl sm INNER JOIN exam.exams_schedule es ON sm.acessKey=es.schedule_accessKey and sm.timebound_id=es.timebound_id "
				+" INNER JOIN exam.exams_assessments ea ON es.assessments_id=ea.id where sm.sapid= ? and SYSDATE() <= sm.accessEndDateTime order by sm.accessStartDateTime"; 
		 try {
			 teePendingAssessmentsList =  jdbcTemplate.query(sql,new Object[] {sapId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicableFromScheduleInfo(sapId, teePendingAssessmentsList);
		
		 
		}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getUpcomingTeeAssessmentsBySapid(String sapId) {
		List<ExamsAssessmentsBean> teeUpcomingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "SELECT " + 
				"    es.*,es.exam_end_date_time AS endDate,es.exam_start_date_time AS startDate,ea.name,ea.customAssessmentName AS testName,'assessments' AS type ,pss.subject " + 
				" FROM " + 
				"				     exam.exams_assessments ea " + 
				"				         INNER JOIN " + 
				"				     exam.exams_schedule es ON es.assessments_id = ea.id " + 
//				"				         INNER JOIN " + 
//				"						exam.assessment_timebound_id ati ON  ati.assessments_id = ea.id " + 
				"                        INNER JOIN " + 
				"				     lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = es.timebound_id " + 
				"				 		INNER JOIN " + 
				"				     lti.student_subject_config ssc ON ssc.id =  es.timebound_id " + 
				"				 		INNER JOIN " + 
				"				   exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				" WHERE " + 
				"    tum.userId = ? " + 
				"   AND ( tum.role =  'Student' OR (es.max_score = 100 AND tum.role in ( 'Resit', 'Student' )) ) "+
//				"        AND  exam_start_date_time >= CURDATE()"
				"		 AND current_timestamp <= es.exam_end_date_time " + 
				" ORDER BY es.exam_start_date_time";
		 try {
			 teeUpcomingAssessmentsList = jdbcTemplate.query(sql,new Object[] {sapId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicable(sapId, teeUpcomingAssessmentsList);
		
		 
		}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getUpcomingTeeAssessmentsBySapidFromScheduleInfo(String sapId) {
		List<ExamsAssessmentsBean> teeUpcomingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "select es.id,es.assessments_id,sm.timebound_id,sm.schedule_id,sm.scheduleName as schedule_name,sm.acessKey as schedule_accessKey "
                +" ,sm.joinURL as schedule_accessUrl,es.schedule_status,sm.accessStartDateTime as exam_start_date_time,sm.accessEndDateTime as exam_end_date_time, "
                +" es.active,es.isResultLive, sm.max_score, sm.accessEndDateTime as endDate,sm.accessStartDateTime as startDate, ea.name, ea.customAssessmentName as testName, " 
                +" 'assessments' AS type, sm.subject "
                +" from exam.exams_scheduleinfo_mettl sm INNER JOIN exam.exams_schedule es ON sm.acessKey=es.schedule_accessKey and sm.timebound_id=es.timebound_id "
				+" INNER JOIN exam.exams_assessments ea ON es.assessments_id=ea.id where sm.sapid= ? and SYSDATE() <= sm.accessEndDateTime order by sm.accessStartDateTime";
		 try {
			 teeUpcomingAssessmentsList = jdbcTemplate.query(sql,new Object[] {sapId},new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }
		 return checkIfAssessmentsApplicableFromScheduleInfo(sapId, teeUpcomingAssessmentsList);
		
		 
		}
	
	
	private List<ExamsAssessmentsBean> checkIfAssessmentsApplicable(String sapid, List<ExamsAssessmentsBean> assessments) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<ExamsAssessmentsBean> teeApplicableAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		
		try {
			for (ExamsAssessmentsBean examsAssessmentsBean : assessments) {
				if("100".equals(examsAssessmentsBean.getMax_score())) {
					if(checkIfExamBookedByStudent(examsAssessmentsBean.getTimebound_id(), sapid, examsAssessmentsBean.getSchedule_id())) {
						teeApplicableAssessmentsList.add(examsAssessmentsBean);
					}
				} else {
					if(checkIfExamTakenByStudent(examsAssessmentsBean.getTimebound_id(), sapid)) {
						teeApplicableAssessmentsList.add(examsAssessmentsBean);
					}
				}
			}
		} catch (Exception e) {
			
		}
		return teeApplicableAssessmentsList;
	}
	
	private List<ExamsAssessmentsBean> checkIfAssessmentsApplicableFromScheduleInfo(String sapid, List<ExamsAssessmentsBean> assessments) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<ExamsAssessmentsBean> teeApplicableAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		
		try {
			for (ExamsAssessmentsBean examsAssessmentsBean : assessments) {
				if("100".equals(examsAssessmentsBean.getMax_score())) {
						teeApplicableAssessmentsList.add(examsAssessmentsBean);
				} else {
					if(checkIfExamTakenByStudentFromScheduleInfo(examsAssessmentsBean.getTimebound_id(), sapid)) {
						teeApplicableAssessmentsList.add(examsAssessmentsBean);
					}
				}
			}
		} catch (Exception e) {
			
		}
		return teeApplicableAssessmentsList;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamBookedByStudent(String timeboundId, String sapid, String scheduleId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_wx_bookings` `b` "
			+ " LEFT JOIN `exam`.`mba_wx_slots` `s` "
			+ " ON `s`.`slotId` = `b`.`slotId` "
			+ " LEFT JOIN `exam`.`mba_wx_time_table` `tt` "
			+ " ON `tt`.`timeTableId` = `s`.`timeTableId` "
			+ " LEFT JOIN `exam`.`exams_schedule` `es` "
			+ " ON `tt`.`scheduleId` = `es`.`id` "
			+ " WHERE `b`.`timeboundId` = ? AND `b`.`sapid` = ? AND `b`.`bookingStatus` = 'Y' AND `es`.`schedule_id` = ?";
		
		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				timeboundId, sapid, scheduleId
			},
			Integer.class
		);
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamTakenByStudent(String timeboundId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`tee_marks` "
			+ " WHERE `timebound_id` = ? AND `sapid` = ? AND `status` in ('Attempted', 'RIA', 'AB', 'NV', 'CC')";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				timeboundId, sapid
			},
			Integer.class
		);
		return count == 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamTakenByStudentFromScheduleInfo(String timeboundId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  "SELECT count(*)"
				  +" FROM  exam.exams_scheduleinfo_mettl "
				  +" WHERE timebound_id = ? AND sapid = ? and testTaken='Attempted' ";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				timeboundId, sapid
			},
			Integer.class
		);
		return count == 0;
	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getTeeAssessmentsByExamScheduleId(String id) {
		List<ExamsAssessmentsBean> teeAssessmentList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = " SELECT " + 
				"    es.*, ea.name, ea.customAssessmentName , es.exam_end_date_time AS endDate,es.exam_start_date_time AS startDate " + 
				" FROM " + 
				"    exam.exams_assessments ea " + 
				"        INNER JOIN " + 
				"    exam.exams_schedule es ON es.assessments_id = ea.id " + 
				" WHERE " + 
				"    es.id = ? ";
				
				
		 try {
			 teeAssessmentList = jdbcTemplate.query(sql,new Object[] {id} ,new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));
		 }
		 catch(Exception e){
			 
			
		 }
		 return teeAssessmentList;
		
		 
		}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getTeeAssessmentsByExamScheduleIdFromScheduleInfo(String id) {
		List<ExamsAssessmentsBean> teeAssessmentList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql =  "select es.id,es.assessments_id,sm.timebound_id,sm.schedule_id,sm.scheduleName as schedule_name,sm.acessKey as schedule_accessKey "
                +" ,sm.joinURL as schedule_accessUrl,es.schedule_status,sm.accessStartDateTime as exam_start_date_time,sm.accessEndDateTime as exam_end_date_time, "
                +" es.active,es.isResultLive, sm.max_score, sm.accessEndDateTime as endDate,sm.accessStartDateTime as startDate, ea.name, ea.customAssessmentName " 
                +" from exam.exams_scheduleinfo_mettl sm INNER JOIN exam.exams_schedule es ON sm.acessKey=es.schedule_accessKey and sm.timebound_id=es.timebound_id "
				+" INNER JOIN exam.exams_assessments ea ON es.assessments_id=ea.id where es.id= ? limit 1";
		 try {
			 teeAssessmentList = jdbcTemplate.query(sql,new Object[] {id} ,new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));
		 }
		 catch(Exception e){
			 
			
		 }
		 return teeAssessmentList;
		
		 
		}

	//emba exam results processing started
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean>  getAllStudentsEligibleForPassFail( TEEResultBean resultBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
	
		String sql= "select t.sapid , t.timebound_id,t.schedule_id , es.max_score, t.score,t.status,t.processed,t.prgm_sem_subj_id,s.consumerProgramStructureId, ea.name as assessmentName " + 
				"from exam.tee_marks t , exam.exams_schedule es , exam.students s , exam.exams_assessments ea  " + 
				"where " + 
				"t.sapid = s.sapid and " +
				"t.timebound_id = es.timebound_id and " + 
				"t.schedule_id = es.schedule_id and " +
				"ea.id=es.assessments_id and " +
				"t.processed = 'N' and " + 
				"t.timebound_id =? and " + 
				"t.schedule_id=? and " + 
				"t.sapid not in ("+TEST_USER_SAPIDS+") and " + 
				"t.score <> '' and "+
				"t.score is not null and "
				+ "t.status<>'Not Attempted'";
		try {
		eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(sql, new Object[] {resultBean.getTimebound_id(),resultBean.getSchedule_id()}, new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		}catch(Exception e) {
			
		}
		return eligibleList;
	}

	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean>  getAllStudentsEligibleForProjectForPassFail( TEEResultBean resultBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
	
		String sql= ""
				+ " SELECT * "
				+ " FROM `exam`.`tee_marks` "
				+ " WHERE `processed` = 'N'"
				+ " and `timebound_id` = ? "
				+ " and `sapid` NOT IN ("+TEST_USER_SAPIDS+") "
				+ " AND `score` <> '' "
				+ " AND `score` IS NOT NULL "
				+ " AND `status` <> 'Not Attempted' ";
		try {
			eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(
				sql, 
				new Object[] { resultBean.getTimebound_id() }, 
				new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
			);
		}catch(Exception e) {
			
		}
		return eligibleList;
	}
	
	//Get all eligible students for processing pass-fail.
	@Transactional(readOnly = true)
	public List<TEEResultBean>  getEligibleStudentsForProjectPassFail(String timeboundId){
		List<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
		//Prepare SQL query
		StringBuilder GET_PROJECT_MARKS = new StringBuilder("SELECT sapid,student_name,timebound_id,prgm_sem_subj_id,simulation_score,simulation_max_score,compXM_score,compXM_max_score, ") 
				.append("processed,simulation_previous_score,compXM_previous_score,simulation_status,compXM_status FROM exam.capstone_component_marks WHERE ")
				.append("timebound_id = ? AND `processed` = 'N' AND sapid NOT IN ("+TEST_USER_SAPIDS+") ");
		//Execute SQL query using jdbcTemplate query() method and return project marks record.
		eligibleList = jdbcTemplate.query(
				GET_PROJECT_MARKS.toString(), new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class), timeboundId);
		//return eligible students list.	
		return eligibleList;
	}//getEligibleStudentsForProjectPassFail(-)
	
	/*
	 * public ArrayList<String> getSingleStudentTimeBoundIdListForPassFail(String
	 * batchId,String sapid){ jdbcTemplate = new JdbcTemplate(dataSource);
	 * ArrayList<String> timeboundIdList = new ArrayList<String>(); String
	 * sql="select tum.timebound_subject_config_id as timebound_id " +
	 * " from  lti.timebound_user_mapping tum, lti.student_subject_config ssc " +
	 * " where tum.role='Student' " +
	 * " and ssc.id = tum.timebound_subject_config_id " +
	 * " and ssc.startDate='2019-05-29 00:00:00' " +
	 * " and ssc.endDate='2019-08-11 23:59:00' " + " and ssc.batchId = ? " +
	 * " and tum.userId=? "; timeboundIdList =
	 * (ArrayList<String>)jdbcTemplate.query(sql, new Object[] {batchId,sapid}, new
	 * SingleColumnRowMapper(String.class)); return timeboundIdList; }
	 */
	
	@Transactional(readOnly = true)
	public String getTEEScoresForStudentSubject(String sapid ,String timeboundId,String schedule_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String teeScore="";
		String sql="SELECT score FROM exam.tee_marks where sapid=? and timebound_id=? and schedule_id=?";
		try {
			teeScore = jdbcTemplate.queryForObject(sql, new Object[] {sapid,timeboundId,schedule_id},String.class);
		}catch(Exception e) {
			//
		}
		return teeScore;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentsTestDetailsExamBean> getIAScoresForStudentSubject(String sapid,String timeboundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentsTestDetailsExamBean> iaScoreList = new ArrayList<StudentsTestDetailsExamBean>();
		String sql="SELECT tst.*, t.testName, t.startDate, t.endDate , COALESCE(tst.score,0) as scoreInInteger , t.showResultsToStudents FROM exam.test_student_testdetails tst " + 
				" INNER JOIN exam.test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId" + 
				" INNER JOIN acads.sessionplan_module spm ON spm.id = ttcm.referenceId" + 
				" INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId" + 
				" INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId " + 
				" INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
				" INNER JOIN exam.test t ON t.id= tst.testId" + 
				" WHERE stm.timeboundId = ? and tst.sapid=? "
				+ " and spm.topic <> 'Generic Module For Session Plan ' "; //added to avoid generic module IA Scores
		try {
		iaScoreList = (ArrayList<StudentsTestDetailsExamBean>)jdbcTemplate.query(sql, new Object[] {timeboundId,sapid}, new BeanPropertyRowMapper<StudentsTestDetailsExamBean>(StudentsTestDetailsExamBean.class));
		}catch(Exception e) {
			//
		}
		return iaScoreList;
	}
	
	@Transactional(readOnly = true)
	public List<StudentsTestDetailsExamBean> getUniqueIAScoresForStudentSubject(String sapid,String timeboundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentsTestDetailsExamBean> iaScoreList = new ArrayList<StudentsTestDetailsExamBean>();
		String sql="SELECT tst.*, t.startDate, t.endDate , COALESCE(tst.score,0) as scoreInInteger , t.showResultsToStudents, t.testName FROM exam.test_student_testdetails tst " + 
				" INNER JOIN exam.test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId" + 
				" INNER JOIN acads.sessionplan_module spm ON spm.id = ttcm.referenceId" + 
				" INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId" + 
				" INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId " + 
				" INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
				" INNER JOIN exam.test t ON t.id= tst.testId" + 
				" WHERE stm.timeboundId = ? and tst.sapid=? "
				+ " and spm.topic <> 'Generic Module For Session Plan ' group by t.id"; //added to avoid generic module IA Scores
		try {
		iaScoreList = jdbcTemplate.query(sql, new Object[] {timeboundId,sapid}, new BeanPropertyRowMapper<StudentsTestDetailsExamBean>(StudentsTestDetailsExamBean.class));
		}catch(Exception e) {
			//
		}
		return iaScoreList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertEmbaPassFail(ArrayList<EmbaPassFailBean> finalListforPassFail) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < finalListforPassFail.size(); i++) {
			try{
				EmbaPassFailBean bean = finalListforPassFail.get(i);
				upsertEmbaPassFailStatus(bean, jdbcTemplate);
			}catch(Exception e){
				//
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	public void upsertEmbaPassFailStatus(EmbaPassFailBean bean, JdbcTemplate jdbcTemplate) {
		boolean recordExists = checkIfRecordExistsInPassFail(bean, jdbcTemplate);
		if(recordExists){
			updateEmbaPassFailStatus(bean, jdbcTemplate);
		}else{
			insertEmbaPassFailStatus(bean, jdbcTemplate);
		}
	}
	private boolean checkIfRecordExistsInPassFail(EmbaPassFailBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.mba_passfail where  sapid = ? and prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				bean.getPrgm_sem_subj_id(),
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	//Get student pass-fail record based on the based on sapId and PSSId
	public Optional<EmbaPassFailBean> getPassFailRecord(String sapId, Integer pssId) {
		//Prepare SQL query
		String sql = "SELECT * FROM exam.mba_passfail where  sapid = ? and prgm_sem_subj_id = ? ";
		try {
			//Execute query and return pass-fail record.
			return Optional.of(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class), sapId, pssId));
		}catch (EmptyResultDataAccessException e) {
			//If any exception occurs then return empty object.
			return Optional.empty();
		}//catch block
	}//getPassFailRecord(-,-)

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateEmbaPassFailStatus(EmbaPassFailBean m, JdbcTemplate jdbcTemplate){
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			String sql = "Update exam.mba_passfail set "
					//+ " sem=?, "
					//+ " attempt=?, "
					+ " timeboundId = ? ,"
					+ " schedule_id= ? , "
					+ " iaScore = ?, "
					+ " teeScore = ?, "
					+ " graceMarks = ?, "
					+ " isPass = ?, "
					+ " failReason = ?, "
					+ " lastModifiedBy = ?, "
					+ " lastModifiedDate = sysdate(),"
					+ " status = ?,"
					+ " grade = ?, "
					+ " points = ?, "
					+ " isResultLive = 'N' "
					+ " where prgm_sem_subj_id = ? and sapid = ?";
			jdbcTemplate.update(sql, new Object[] { 
					//m.getSem(),
					//m.getAttempt(),
					m.getTimeboundId(),
					m.getSchedule_id(),
					m.getIaScore(),
					m.getTeeScore(),
					m.getGraceMarks(),
					m.getIsPass(),
					m.getFailReason(),
					m.getLastModifiedBy(),
					m.getStatus(),
					m.getGrade(),
					m.getPoints(),
					m.getPrgm_sem_subj_id(),
					m.getSapid()
			});

			String sql2 = "Update exam.tee_marks set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql2, new Object[] { 
					m.getLastModifiedBy(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			String sql3 = "Update exam.tee_marks_history set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where schedule_id=? and timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql3, new Object[] { 
					m.getLastModifiedBy(),
					m.getSchedule_id(),
					m.getTimeboundId(),
					m.getSapid()
			});

			transactionManager.commit(status);

		}
		catch (Exception e) {
			// TODO: handle exception
			transactionManager.rollback(status);
			
		}
	}
	
	//Update existing pass-fail record of a student based on the sapId and PSSId
	public int updateEmbaPassFailStatus(EmbaPassFailBean m) throws Exception{
		//Prepare SQL query
		StringBuilder UPDATE_PASS_FAIL = new StringBuilder("Update exam.mba_passfail set timeboundId = ?, schedule_id= ? , ")
				.append("iaScore = ?, teeScore = ?, graceMarks = ?, isPass = ?, failReason = ?, lastModifiedBy = ?, lastModifiedDate = sysdate(), ")
					.append("status = ?, grade = ?, points = ?, isResultLive = 'N' where prgm_sem_subj_id = ? and sapid = ? ");

		////Execute jdbcTemplate update method and return the updated count.
		return jdbcTemplate.update(UPDATE_PASS_FAIL.toString(),
				m.getTimeboundId(),	m.getSchedule_id(),	m.getIaScore(),	m.getTeeScore(), m.getGraceMarks(),
				m.getIsPass(), m.getFailReason(), m.getLastModifiedBy(), m.getStatus(),	m.getGrade(),
				m.getPoints(), m.getPrgm_sem_subj_id(),	m.getSapid());
	}//updateEmbaPassFailStatus(-)
	
	//Insert pass-fail record in 'mba_passfail' table.
	public int insertEmbaPassFailStatus(EmbaPassFailBean passFailBean) throws Exception{
		//Prepare SQL query
		StringBuilder INSERT_PASSFAIL = new StringBuilder("Insert into exam.mba_passfail (timeboundId, sapid,prgm_sem_subj_id,schedule_id,iaScore,teeScore, ")
				.append("graceMarks,isPass,failReason,status,createdBy,lastModifiedBy,lastModifiedDate,createdDate,isResultLive) ")
					.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),'N') ");
		
		//Execute jdbcTemplate update method and return the affected count.
		return jdbcTemplate.update(INSERT_PASSFAIL.toString(), 
				passFailBean.getTimeboundId(), passFailBean.getSapid(), passFailBean.getPrgm_sem_subj_id(), passFailBean.getSchedule_id(), passFailBean.getIaScore(),
				passFailBean.getTeeScore(), passFailBean.getGraceMarks(),passFailBean.getIsPass(), passFailBean.getFailReason(),
				passFailBean.getStatus(), passFailBean.getCreatedBy(), passFailBean.getLastModifiedBy());
	}//insertEmbaPassFailStatus(-)
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertEmbaPassFailStatus(EmbaPassFailBean m, JdbcTemplate jdbcTemplate){
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try {

			String sql = "Insert into exam.mba_passfail ( "
					+ " timeboundId,"
					+ " sapid,"
					+ " prgm_sem_subj_id,"
					//+ " sem, "
					//+ " attempt, "
					+ " schedule_id, "
					+ " iaScore, "
					+ " teeScore, "
					+ " graceMarks, "
					+ " isPass, "
					+ " failReason, "
					+ " status,"
					+ " createdBy, "
					+ " lastModifiedBy, "
					+ " lastModifiedDate,"
					+ " createdDate,"
					+ " isResultLive)"
					+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),'N')";
			jdbcTemplate.update(sql, new Object[] { 
					m.getTimeboundId(),
					m.getSapid(),
					m.getPrgm_sem_subj_id(),
					m.getSchedule_id(),
					m.getIaScore(),
					m.getTeeScore(),
					m.getGraceMarks(),
					m.getIsPass(),
					m.getFailReason(),
					m.getStatus(),
					m.getCreatedBy(),
					m.getLastModifiedBy()

			});

			String sql2 = "Update exam.tee_marks set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql2, new Object[] { 
					m.getLastModifiedBy(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			
			String sql3 = "Update exam.tee_marks_history set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where schedule_id=? and timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql3, new Object[] { 
					m.getLastModifiedBy(),
					m.getSchedule_id(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			
			transactionManager.commit(status);

		}
		catch (Exception e) {
			// TODO: handle exception
			transactionManager.rollback(status);
			
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getAllFailedStudentsForGrace(String assessmentId, String scheduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> eligibleList = new ArrayList<EmbaPassFailBean>();
		/*
		 * String sql ="select mbp.*" +
		 * " from  exam.mba_passfail mbp,lti.timebound_user_mapping tum, exam.exams_schedule es ,exam.exams_assessments ea  "
		 * + " where " + " mbp.sapid=tum.userId and " +
		 * " mbp.timeboundId=tum.timebound_subject_config_id and" +
		 * " tum.timebound_subject_config_id = es.timebound_id and  " +
		 * " es.timebound_id = ea.timebound_id and   " +
		 * " es.assessments_id = ea.id and  " + " mbp.isPass ='N' and  " +
		 * " tum.role='Student' and  " + " es.timebound_id = ? and " +
		 * " mbp.schedule_id = ?";
		 */
		String sql ="select mbp.*,es.max_score" + 
				" from  exam.mba_passfail mbp ,exam.exams_schedule es " + 
				" where " + 
				" mbp.timeboundId=? and" + 
				" mbp.isPass ='N' and  " + 
				" mbp.timeboundId=es.timebound_id and" + 
				" mbp.schedule_id=es.schedule_id and" + 
				" mbp.schedule_id = ?";

		eligibleList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {assessmentId,scheduleId}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return eligibleList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getAllFailedStudentsForGraceProject(String assessmentId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> eligibleList = new ArrayList<EmbaPassFailBean>();
		/*
		 * String sql ="select mbp.*" +
		 * " from  exam.mba_passfail mbp,lti.timebound_user_mapping tum, exam.exams_schedule es ,exam.exams_assessments ea  "
		 * + " where " + " mbp.sapid=tum.userId and " +
		 * " mbp.timeboundId=tum.timebound_subject_config_id and" +
		 * " tum.timebound_subject_config_id = es.timebound_id and  " +
		 * " es.timebound_id = ea.timebound_id and   " +
		 * " es.assessments_id = ea.id and  " + " mbp.isPass ='N' and  " +
		 * " tum.role='Student' and  " + " es.timebound_id = ? and " +
		 * " mbp.schedule_id = ?";
		 */
		String sql =""
				+ " SELECT * "
				+ " FROM `exam`.`mba_passfail` `mbp` "
				+ " WHERE `timeboundId` = ? "
				+ " AND `mbp`.`isPass` ='N' "
				/* TODO : change logic to test for project subject */
				+ " AND `timeboundId` IN ( "
					+ " SELECT `ssc`.`id` "
					+ " FROM `lti`.`student_subject_config` `ssc` "
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
					+ " WHERE `pss`.`subject` LIKE '%Capstone%' "
				+ " )";

		eligibleList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {assessmentId}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return eligibleList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getPassFailResultsForReport(TEEResultBean resultBean, String authorizedCenterCodes){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> passFailRecords = new ArrayList<EmbaPassFailBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select mp.*, (COALESCE(mp.iaScore, 0) + COALESCE(mp.teeScore,0)) as total , ps.subject, sc.batchId as batch_id, b.name as batchName,cnt.lc as lc "
				+ " FROM exam.mba_passfail mp , lti.student_subject_config sc, exam.program_sem_subject ps, exam.batch b, exam.students s,exam.centers cnt"
				+ " where mp.timeboundId = sc.id"
				+ " and sc.prgm_sem_subj_id = ps.id "
				+ " and b.id = sc.batchId "
				+ " and s.sapid = mp.sapid and cnt.centerCode = s.centerCode ";

		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql+ " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and mp.schedule_id = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and sc.batchId = ?";
			parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and mp.timeboundId = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		Object[] args = parameters.toArray();
		passFailRecords = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return passFailRecords;
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsForEmbaLive(TEEResultBean resultBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="update exam.mba_passfail set isResultLive ='Y' where schedule_id = ? and timeboundId =?";
		parameters.add(resultBean.getSchedule_id());
		parameters.add(resultBean.getTimebound_id());
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsForEmbaProjectLive(TEEResultBean resultBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		// TODO: add check for project subjects after project flag added to subject level
		String sql = ""
			+ " UPDATE `exam`.`mba_passfail` "
			+ " SET `isResultLive` ='Y' "
			+ " WHERE `timeboundId` = ? "
			+ " AND `timeboundId` IN ( "
				+ " SELECT `ssc`.`id` "
				+ " FROM `lti`.`student_subject_config` `ssc` "
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
				+ " WHERE `pss`.`subject` LIKE '%Capstone%' OR `pss`.`subject` like 'Hands-On Data Science Project%' OR `pss`.`subject` like '%Product Deployment Bootcamp%' "
			+ " )";
		parameters.add(resultBean.getTimebound_id());
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}

	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean>  getAllStudentDetailsForMBAWX(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		String sql ="select s.* from exam.students s , lti., where s.program='MBA - WX'";
		Object[] args = parameters.toArray();
		studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean>  getAllStudentDetailsForBatchAndTimebound(String batchId,String timeBoundId,String program, boolean isResultProcessed){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		
		String processedCheck = " ";
		if(isResultProcessed) {
			processedCheck = " OR mk.processed = 'N' ";
		}
		String sql ="SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    lti.timebound_user_mapping tum ON tum.userId = s.sapid " + 
				"        AND tum.timebound_subject_config_id = ? " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"        LEFT JOIN " + 
				"    exam.tee_marks mk ON mk.sapid = tum.userId " + 
				"        AND mk.timebound_id = ssc.id " + 
				"WHERE " + 
//				"    s.program = ? AND " + 
				"         ssc.batchId = ? " + 
				"        AND (mk.status = 'Not Attempted' OR mk.status IS NULL "+processedCheck+" ) ";
		parameters.add(timeBoundId);
//		parameters.add(program);
		parameters.add(batchId);
		Object[] args = parameters.toArray();
		try {
			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		}catch(Exception e) {
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean>  getReExamStudentDetails(String batchId,String timeBoundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		String sql ="SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    lti.timebound_user_mapping tum ON tum.userId = s.sapid " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"WHERE " + 
				//"    s.program = ? AND " + 
				" ssc.batchId = ? " + 
				" AND tum.timebound_subject_config_id = ? " ;
		//parameters.add(program);
		parameters.add(batchId);
		parameters.add(timeBoundId);
		Object[] args = parameters.toArray();
		try {
			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		}catch(Exception e) {
			
		}
		return studentList;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertTeeMarks(ArrayList<MettlResponseBean> mettlResponseBeanList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < mettlResponseBeanList.size(); i++) {
			MettlResponseBean bean = mettlResponseBeanList.get(i);
			updatePssIdForUser(bean);
			
			try{
				upsertTeeMarksHistoryStatus(bean, jdbcTemplate);
				upsertTeeMarksStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(bean.getEmail()+"");
			}
		}
		return errorList;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertTeeMarksForProjectSubject(ArrayList<MettlResponseBean> mettlResponseBeanList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < mettlResponseBeanList.size(); i++) {
			MettlResponseBean bean = mettlResponseBeanList.get(i);
			updatePssIdForUser(bean);
			try{
				// Dont insert into marks history for Project
				// upsertTeeMarksHistoryStatus(bean, jdbcTemplate);
				upsertTeeMarksStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(bean.getSapid());
			}
		}
		return errorList;

	}
	
	private void updatePssIdForUser(MettlResponseBean bean) {
		String sql = ""
				+ " SELECT `pss2`.`id`  "
				
				+ " FROM `lti`.`timebound_user_mapping` `tum` "
				
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
				+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				
				+ " INNER JOIN `exam`.`registration` `r` "
				+ " ON `r`.`sapid` = `tum`.`userId` "
				+ " AND `r`.`year` = `ssc`.`acadYear` "
				+ " AND `r`.`month` = `ssc`.`acadMonth` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss1` "
				+ " ON `pss1`.`id` = `ssc`.`prgm_sem_subj_id` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss2` "
				+ " ON `pss2`.`subject` = `pss1`.`subject` "
				+ " AND `r`.`sem` = `pss2`.`sem` "
				+ " AND `pss2`.`consumerProgramStructureId` = `r`.`consumerProgramStructureId` "
				
				+ " WHERE `tum`.`timebound_subject_config_id` = ? AND `tum`.`userId` = ? ";
		try {
			int pssId = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { bean.getTimebound_id(), bean.getSapid() },
				Integer.class
			);
			bean.setPrgm_sem_subj_id(pssId);
		} catch (Exception e) {
			
		}
	}
	
	//Get program sem subject Id(pssId) based on the sapId and timeboundId.
	@Transactional(readOnly = true)
	public Optional<Integer> getSubjectPSSId(String sapId, String timeboundId) {
		//Prepare SQL query
		StringBuilder GET_PSSID_SQL = new StringBuilder("SELECT `pss2`.`id` FROM `lti`.`timebound_user_mapping` `tum` ")
				.append("INNER JOIN `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` ")
				.append("INNER JOIN `exam`.`registration` `r` ON `r`.`sapid` = `tum`.`userId` AND `r`.`year` = `ssc`.`acadYear` AND `r`.`month` = `ssc`.`acadMonth` ")
				.append("INNER JOIN `exam`.`program_sem_subject` `pss1` ON `pss1`.`id` = `ssc`.`prgm_sem_subj_id` ")
				.append("INNER JOIN `exam`.`program_sem_subject` `pss2` ON `pss2`.`subject` = `pss1`.`subject` ")
				.append("AND `r`.`sem` = `pss2`.`sem` AND `pss2`.`consumerProgramStructureId` = `r`.`consumerProgramStructureId` ")
				.append("WHERE `tum`.`timebound_subject_config_id` = ? AND `tum`.`userId` = ? ");
		try {
			//Execute SQL query using queryForObject method and return project marks record.
			return Optional.of(jdbcTemplate.queryForObject(GET_PSSID_SQL.toString(), Integer.class, timeboundId, sapId));
		}catch (EmptyResultDataAccessException  e) {
			//return empty object if exception occurs.
			return Optional.empty();
		}
	}//getSubjectPSSId(-,-)

	public void upsertTeeMarksStatus(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		boolean recordExists = checkIfRecordExistsInTeeMarks(bean, jdbcTemplate);
		if(recordExists){
			TEEResultBean mbean = getStudentsPreviousScore(bean, jdbcTemplate);
			if(mbean != null) {
				//bean.setPrevious_schedule_id(mbean.getSchedule_id());
				bean.setPrevious_score(""+mbean.getScore());
			}
			updateTeeMarksStatus(bean, jdbcTemplate);
		}else{
			insertTeeMarksStatus(bean, jdbcTemplate);
		}
	}

	private boolean checkIfRecordExistsInTeeMarks(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.tee_marks where  sapid = ? and  prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				//bean.getTimebound_id(),
				bean.getPrgm_sem_subj_id()
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	
	private TEEResultBean getStudentsPreviousScore(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {

		TEEResultBean mbean = new TEEResultBean();
		String sql = "SELECT score,schedule_id FROM exam.tee_marks where  sapid = ? and prgm_sem_subj_id = ?";
		try {
			mbean = (TEEResultBean) jdbcTemplate.queryForObject(sql, new Object[] { 
					bean.getSapid(),
					//bean.getTimebound_id()
					bean.getPrgm_sem_subj_id()
			},new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		}catch(Exception e) {
			
		}
		return mbean;

	}
	
	//Get single student project marks based on the sapId and passId.
	@Transactional(readOnly = true)
	public Optional<TEEResultBean> getTimeboundStudentProjectMarks(String sapId, int pssId) {
		//Prepare SQL query
		StringBuilder GET_PROJECT_MARKS = new StringBuilder("SELECT sapid,student_name,timebound_id,prgm_sem_subj_id,simulation_score,simulation_max_score, ") 
				.append("compXM_score,compXM_max_score,processed,simulation_previous_score,compXM_previous_score,simulation_status,compXM_status ")
					.append("FROM exam.capstone_component_marks WHERE sapid = ? and prgm_sem_subj_id = ? ");
		try {
			//Execute queryForObject method and return student project marks.
			return Optional.of(jdbcTemplate.queryForObject(GET_PROJECT_MARKS.toString(),
					new BeanPropertyRowMapper<>(TEEResultBean.class), sapId, pssId));
		}catch (EmptyResultDataAccessException  e) {
			//return empty object if exception occurs.
			return Optional.empty();
		}
	}//getTimeboundStudentProjectMarks(-)
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTeeMarksStatus(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.tee_marks set "
				//+ " sem=?, "
				//+ " attempt=?, "
				+ " schedule_id=?, "
				+ " student_name=?, "
				+ " score=?, "
				+ " max_score=?, "
				//+ " previous_schedule_id=?, "
				+ " processed='N', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate(),"
				+ " status=? ,"
				+ " timebound_id= ? "
				+ " where  prgm_sem_subj_id = ? and sapid = ?";
		jdbcTemplate.update(sql, new Object[] { 
				//m.getSem(),
				//m.getAttempt(),
				m.getSchedule_id(),
				m.getStudent_name(),
				m.getTotalMarks(),
				m.getMax_marks(),
				//m.getPrevious_score(),
				//m.getPrevious_schedule_id(),
				m.getLastModifiedBy(),
				m.getStatus(),
				m.getTimebound_id(),
				m.getPrgm_sem_subj_id(),
				m.getSapid()
		});
	}
	
	//Update the time-bound project details in 'capstone_component_marks' table based on the sapId and pssId 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int updateTimeboundProjectMarks(TEEResultBean resultBean) throws Exception {
		//Prepare SQL Query
		StringBuilder UPDATE_PROJECT_MARKS = new StringBuilder("UPDATE exam.capstone_component_marks SET student_name=?,timebound_id=?,simulation_score=?, ")
				.append("simulation_max_score=?,compXM_score=?,compXM_max_score=?,processed='N',simulation_previous_score=?,compXM_previous_score=?, ")
				.append("simulation_status=?, compXM_status=?, lastModifiedBy=?,lastModified_at=sysdate() where sapid=? AND prgm_sem_subj_id=? ");
		
		//Execute SQL query usingjdbcTemplate update method and return the updated count.
		return jdbcTemplate.update(UPDATE_PROJECT_MARKS.toString(), resultBean.getStudent_name(),resultBean.getTimebound_id(),resultBean.getSimulation_score(),
				resultBean.getSimulation_max_score(),resultBean.getCompXM_score(),resultBean.getCompXM_max_score(),resultBean.getSimulation_previous_score(),
				resultBean.getCompXM_previous_score(), resultBean.getSimulation_status(), resultBean.getCompXM_status(),
				resultBean.getLastModifiedBy(), resultBean.getSapid(), resultBean.getPrgm_sem_subj_id());
	}//updateTimeboundProjectMarks(-)
	
	//Update time-bound project processed status in 'capstone_component_marks' table based on the sapId and timeboundId 
	public int updateProcessedTimeboundProjectStatus(EmbaPassFailBean passFailBean) throws Exception{
		//Prepare SQL Query
		StringBuilder UPDATE_PROJECT_MARKS = new StringBuilder("UPDATE exam.capstone_component_marks SET processed='Y',lastModifiedBy=?,lastModified_at=sysdate() ")
						.append("where sapid=? AND timebound_id =? ");
		
		//Execute SQL query using jdbcTemplate update method and return the updated count.
		return jdbcTemplate.update(UPDATE_PROJECT_MARKS.toString(), passFailBean.getLastModifiedBy(), passFailBean.getSapid(), passFailBean.getTimeboundId());
	}//updateProcessedTimeboundProjectStatus(-)
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertTeeMarksStatus(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = "Insert into exam.tee_marks ( "
				+ " timebound_id,"
				+ " schedule_id,"
				+ " prgm_sem_subj_id,"
				//+ " sem, "
				//+ " attempt, "
				+ " student_name, "
				+ " sapid, "
				+ " score, "
				+ " max_score, "
				+ " processed,"
				+ " status, "
				+ " createdBy, "
				+ " created_at )"
				+ "	VALUES(?,?,?,?,?,?,?,'N',?,?,sysdate())";
		jdbcTemplate.update(sql, new Object[] { 
				m.getTimebound_id(),
				m.getSchedule_id(),
				m.getPrgm_sem_subj_id(),
				m.getStudent_name(),
				m.getSapid(),
				//m.getSem(),
				//m.getAttempt(),
				m.getTotalMarks(),
				m.getMax_marks(),
				m.getStatus(),
				m.getCreatedBy(),
		});
	}

	//Insert single time-bound student project record. 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertTimeboundProjectMarks(TEEResultBean resultBean) throws Exception{
		//Prepare SQL query
		StringBuilder INSERT_PROJECT_MARKS = new StringBuilder("INSERT INTO `exam`.`capstone_component_marks` (`sapid`, `student_name`, `timebound_id`, `prgm_sem_subj_id`, ")
				.append("`simulation_score`, `simulation_max_score`, `compXM_score`, `compXM_max_score`, `processed`, `simulation_status`, `compXM_status`, `createdBy`, ")
				.append("`created_at`, `lastModifiedBy`, `lastModified_at`) VALUES (?, ?, ?, ?, ?,?, ?, ?, 'N', ?, ?, ?, sysdate(), ?, sysdate()) "); 
		
		//Execute SQL query using jdbcTemplate update() method and return the updated count.
		return jdbcTemplate.update(INSERT_PROJECT_MARKS.toString(), resultBean.getSapid(), resultBean.getStudent_name(),resultBean.getTimebound_id(), 
				resultBean.getPrgm_sem_subj_id(), resultBean.getSimulation_score(),resultBean.getSimulation_max_score(), resultBean.getCompXM_score(),  
				resultBean.getCompXM_max_score(), resultBean.getSimulation_status(), resultBean.getCompXM_status(), resultBean.getCreatedBy(), resultBean.getLastModifiedBy());
	}//insertTimeboundProjectMarks(-)
	
	public void upsertTeeMarksHistoryStatus(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		boolean recordExists = checkIfRecordExistsInTeeMarksHistory(bean, jdbcTemplate);
		if(recordExists){
			updateTeeMarksStatusHistory(bean, jdbcTemplate);
		}else{
			insertTeeMarksStatusHistory(bean, jdbcTemplate);
		}
	}
	
	private boolean checkIfRecordExistsInTeeMarksHistory(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.tee_marks_history where  sapid = ? and timebound_id = ? and schedule_id = ? and prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				bean.getTimebound_id(),
				bean.getSchedule_id(),
				bean.getPrgm_sem_subj_id()
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTeeMarksStatusHistory(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.tee_marks_history set "
				//+ " sem=?, "
				//+ " attempt=?, "
				+ " student_name=?, "
				+ " score=?,"
				+ " status=?,"
				+ " processed= ? , "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where timebound_id =? and sapid = ? and schedule_id=? and prgm_sem_subj_id = ?";
		jdbcTemplate.update(sql, new Object[] { 
				//m.getSem(),
				//m.getAttempt(),
				
				m.getStudent_name(),
				m.getTotalMarks(),
				m.getStatus(),
				"N",
				m.getLastModifiedBy(),
				m.getTimebound_id(),
				m.getSapid(),
				m.getSchedule_id(),
				m.getPrgm_sem_subj_id()
		});
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertTeeMarksStatusHistory(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = "Insert into exam.tee_marks_history ( "
				+ " timebound_id,"
				+ " schedule_id,"
				//+ " sem, "
				//+ " attempt, "
				+ " student_name, "
				+ " sapid, "
				+ " score,"
				+ " status, "
				+ " processed,"
				+ " prgm_sem_subj_id, "
				+ " createdBy, "
				+ " created_at )"
				+ "	VALUES(?,?,?,?,?,?,?,?,?,sysdate())";
		jdbcTemplate.update(sql, new Object[] { 
				m.getTimebound_id(),
				m.getSchedule_id(),
				m.getStudent_name(),
				m.getSapid(),
				//m.getSem(),
				//m.getAttempt(),
				m.getTotalMarks(),
				m.getStatus(),
				"N",
				m.getPrgm_sem_subj_id(),
				m.getCreatedBy()
		});
	}
	
	@Transactional(readOnly = true)
	public String getScheduleKeyFromScheduleId(String id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="Select schedule_accessKey from exam.exams_schedule where schedule_id=?"; 
		String scheduleId="";
		try {
			scheduleId = (String) jdbcTemplate.queryForObject(sql, new Object[] {id},String.class);
		}catch(Exception e) {
			
		}
		return scheduleId;
	}
	
	@Transactional(readOnly = true)
	public ExamsAssessmentsBean getScheduleKeyFromScheduleIdAndTimeBoundId(String id,String timeBoundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="Select schedule_accessKey, max_score from exam.exams_schedule where schedule_id=? and timebound_id=?"; 
		ExamsAssessmentsBean scheduleDetails = new ExamsAssessmentsBean();
		try {
			scheduleDetails =  jdbcTemplate.queryForObject(sql, new Object[] {id, timeBoundId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));
			return scheduleDetails;
		}catch(Exception e) {
			
			return scheduleDetails;
		}
		
	}
	@Transactional(readOnly = true)
	public List<TEEResultBean> readScoreFromTeeMarks(TEEResultBean resultBean){
		ArrayList<TEEResultBean> teeResultBeans = new ArrayList<TEEResultBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT "
				+ " t_m.*, s_s_c.batchId, b.name AS 'batch', p_s_s.subject,es.schedule_name "
				+ " FROM "
				+ " exam.tee_marks t_m, lti.student_subject_config s_s_c, exam.batch b, exam.program_sem_subject p_s_s, exam.exams_schedule es "
				+ " WHERE"
				+ " s_s_c.batchId = b.id AND "
				+ " s_s_c.id = t_m.timebound_id AND "
				+ " s_s_c.prgm_sem_subj_id = p_s_s.id AND"
				+ " es.timebound_id = t_m.timebound_id AND" 
				+ " es.schedule_id = t_m.schedule_id AND"
				+ " b.id = s_s_c.batchId";
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and t_m.schedule_id = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and s_s_c.batchId = ?";
			parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and t_m.timebound_id = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		if(!StringUtils.isBlank(resultBean.getSapid()) ) {
			sql= sql+ " and t_m.sapid = ?";
			parameters.add(resultBean.getSapid());
		}
		Object[] args = parameters.toArray();
		try {
			teeResultBeans = (ArrayList<TEEResultBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(TEEResultBean.class));
		}catch(Exception e) {
			
		}
		return teeResultBeans;
	}
	//emba exam results processing ended

	@Transactional(readOnly = true)
	public int checkIfAssessmentTimeBoundMappingExists (ExamsAssessmentsBean examsAssessmentsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.assessment_timebound_id where timebound_id = ? and assessments_id = ?";
		int exists = 0;		
		try {
			exists = (int) jdbcTemplate.queryForObject(sql, new Object[] {examsAssessmentsBean.getTimebound_id(),examsAssessmentsBean.getAssessments_id()},Integer.class);
			if (exists > 0) {
				return 1;
			} else {
				return 0;
			}			
		} catch (Exception e) {
			
			return -1;
		}
	}
	
	@Transactional(readOnly = true)
	public int checkIfAssessmentExists (String assessmentId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.exams_assessments where id = ? ";
		int exists = 0;		
		try {
			exists = (int) jdbcTemplate.queryForObject(sql, new Object[] {assessmentId},Integer.class);
			if (exists > 0) {
				return 1;
			} else {
				return 0;
			}			
		} catch (Exception e) {
			logger.error("Error:"+e.getMessage());
			return -1;
		}
	}
	
	@Transactional(readOnly = false)
	public String insertIntoExamScheduleAndAssessmentTimebound(ExamsAssessmentsBean examsAssessmentsBean) {

		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1 = "insert into exam.exams_schedule(`assessments_id`,`timebound_id`,"
					+ "`schedule_id`,`schedule_name`,`schedule_accessKey`,`schedule_accessUrl`,`schedule_status`,`exam_start_date_time`,"
					+ "`exam_end_date_time`,`reporting_start_date_time`,`reporting_finish_date_time`,`createdBy`,`lastModifiedBy`,`max_score`) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					jdbcTemplate.update(sql1,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getSchedule_id(),
					examsAssessmentsBean.getSchedule_name(),
					examsAssessmentsBean.getSchedule_accessKey(),
					examsAssessmentsBean.getSchedule_accessUrl(),
					examsAssessmentsBean.getSchedule_status(),
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getReporting_start_date_time(),
					examsAssessmentsBean.getReporting_finish_date_time(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy(),
					examsAssessmentsBean.getMax_score());

			String sql3 = "insert into exam.assessment_timebound_id(`assessments_id`,`timebound_id`,`createdBy`,`lastModifiedBy`) values(?,?,?,?)";

			jdbcTemplate.update(sql3,examsAssessmentsBean.getAssessments_id(),
					examsAssessmentsBean.getTimebound_id(),
					examsAssessmentsBean.getCreatedBy(),
					examsAssessmentsBean.getLastModifiedBy());

			transactionManager.commit(status);
			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("Error:"+e.getMessage());
			transactionManager.rollback(status);
			if(e instanceof DuplicateKeyException) {
				return "Schedule already exist in portal";
			}
			return e.getMessage();
		}

	}
	
	private TimeBoundUserMapping getTimeboundDetails(String timeboundId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT * "
				+ " FROM `lti`.`student_subject_config` `ssc` "
				
				+ " LEFT JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
				+ " WHERE `ssc`.`id` = ? ";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				timeboundId
			},
			new BeanPropertyRowMapper<TimeBoundUserMapping>(TimeBoundUserMapping.class)
		);
	}
	
	@Transactional(readOnly = false)
	public boolean insertAssessmentInPost(ExamsAssessmentsBean assessmentInfo){
		jdbcTemplate = new JdbcTemplate(dataSource);
		//insert in post table
		
		String sql = ""
			+ " INSERT INTO `lti`.`post` "
			+ " ( "
				+ " `userId`, `subject_config_id`, `role`, "
				+ " `type`, `content`, `referenceId`, "
				+ " `visibility`, `acadYear`, `acadMonth`, "
				+ " `examYear`, `examMonth`, `scheduledDate`, "
				+ " `scheduleFlag`, `hashtags`, `createdBy`, "
				+ " `createdDate`, `lastModifiedBy`, `lastModifiedDate`, "
				+ " `subject`, `startDate`, `endDate`, "
				+ " `contentType` "
			+ " ) "
			+ " VALUES "
			+ " ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " sysdate(), ?, sysdate(), "
				+ " ?, ?, ?, "
				+ " ? "
			+ " ) ";
		
		try {
			TimeBoundUserMapping subjectInfo = getTimeboundDetails(assessmentInfo.getTimebound_id());
			jdbcTemplate.update(
				sql,
				new Object[] {
					"System", assessmentInfo.getTimebound_id(), "System",
					"TEE", assessmentInfo.getName(), assessmentInfo.getSchedule_id(),
					1, subjectInfo.getAcadYear(), subjectInfo.getAcadMonth(),
					subjectInfo.getExamYear(),subjectInfo.getExamMonth(), assessmentInfo.getExam_start_date_time(),
					"Y", subjectInfo.getSubject(), subjectInfo.getCreatedBy(),
					subjectInfo.getCreatedBy(),
					subjectInfo.getSubject(), assessmentInfo.getExam_start_date_time(), assessmentInfo.getExam_end_date_time(),
					"TEE"
				}
			);
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public int saveEmbaStudentMarksBeforeRIANV(TEEResultBean m){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql2 = "Update exam.tee_marks set "
				+ " processed='N', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate(),"
				+ "	previous_score=? "
				+ " where timebound_id =? and sapid = ?";
		int i = jdbcTemplate.update(sql2, new Object[] { 
				m.getLastModifiedBy(),
				m.getScore(),
				m.getTimebound_id(),
				m.getSapid()
		});

		return i;
	}

	@Transactional(readOnly = true)
	public String getEmbaStudentPreviousScore(TEEResultBean m){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql2 = "Select previous_score from  exam.tee_marks  "
				+ " where prgm_sem_subj_id =? and sapid = ?";
		String i = (String)jdbcTemplate.queryForObject(sql2, new Object[] { 
				m.getPrgm_sem_subj_id(),
				m.getSapid()
		},new SingleColumnRowMapper(String.class));

		return i;
	}
	
	@Transactional(readOnly = false)
	public int updateEmbaSubjectScore(TEEResultBean m,String status){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql2 = "Update exam.tee_marks set "
				+ " processed='N', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate(),"
				+ "	score=?,"
				+ " status=? "
				+ " where prgm_sem_subj_id =? and sapid = ?";
		int i = jdbcTemplate.update(sql2, new Object[] { 
				m.getLastModifiedBy(),
				m.getScore(),
				status,
				m.getPrgm_sem_subj_id(),
				m.getSapid()
		});

		return i;
	}
		
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailByTimeBoundId(String commaSeparatedTimeBoundIds){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql ="SELECT  " + 
				"      pf.timeboundId, pf.sapid, pf.schedule_id, pf.attempt, pf.sem, pf.isPass, pf.failReason,"
				+ " pf.isResultLive, pf.createdBy,pf.createdDate, pf.lastModifiedBy, pf.lastModifiedDate, pf.grade, "
				+ "pf.points,pf.status,CONCAT (s.firstName ,' ',s.lastName) as studentName,ssc.batchId as batch_id,pss.subject,s.program , "
				+ " CASE " + 
				"    When pf.iaScore THEN pf.iaScore ELSE 0	     " + 
				"END AS iaScore, " + 
				"	CASE " + 
				"    When pf.teeScore THEN pf.teeScore ELSE 0	     " + 
				"END AS teeScore, " + 
				"	CASE " + 
				"    When pf.graceMarks THEN pf.graceMarks ELSE 0	     " + 
				"END AS graceMarks, " + 
				"	CAST((COALESCE(iaScore, 0) + COALESCE(teeScore, 0) ) AS UNSIGNED) AS total "+ 
				"FROM " + 
				"    exam.mba_passfail pf "
				+ "INNER JOIN " + 
				"    exam.students s ON s.sapid = pf.sapid " + 
				"  INNER JOIN " + 
				" 	 lti.student_subject_config ssc ON ssc.id = pf.timeBoundId "
				+ "INNER JOIN  " + 
				"    exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				"WHERE " + 
				"    pf.timeBoundId in ("+commaSeparatedTimeBoundIds+")";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public EmbaGradePointBean getGradeAndPointByTotalScore(int totalScore) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		EmbaGradePointBean egpData = null ;
		
		String sql ="SELECT  " + 
				"    * " + 
				"FROM " + 
				"    exam.mba_gradepoint gp " + 
				"WHERE " + 
				"    ? BETWEEN gp.marksFrom AND gp.marksTill";
		
		try {
			egpData = (EmbaGradePointBean) jdbcTemplate.queryForObject(sql, new Object[] {totalScore}, new BeanPropertyRowMapper<EmbaGradePointBean>(EmbaGradePointBean.class));
		}catch(Exception e) {
			
		}
		return egpData;
		
	}
	
	@Transactional(readOnly = false)
	public int updateEmbaGradeAndPoints(EmbaPassFailBean m){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE `exam`.`mba_passfail`  " + 
				"SET  " + 
				"    `grade` = ?, " + 
				"    `points` = ? " + 
				"WHERE " + 
				"    `timeboundId` = ? AND `sapid` = ?";
		
		int i = jdbcTemplate.update(sql, new Object[] { 
				m.getGrade(),
				m.getPoints(),
				m.getTimeboundId(),
				m.getSapid()
		});

		return i;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean> getAllStudentNotProcessedList(){
		ArrayList<TEEResultBean> studentsList = new ArrayList<TEEResultBean>();
		try {
			String sql = "SELECT * FROM exam.tee_marks where processed='N';";
			jdbcTemplate = new JdbcTemplate(dataSource);
			studentsList = (ArrayList<TEEResultBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(TEEResultBean.class));
			return studentsList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return studentsList;
		}
	}
	
	
	
	@Transactional(readOnly = false)
	public String updateScheduleIdInTimeTable(ExamsAssessmentsBean examsAssessmentsBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql4 = " "
					+ " UPDATE `exam`.`mba_wx_time_table` `tt` "
					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
					+ " ON "
					        + " `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` "
					    + " AND `ssc`.`examYear` = `tt`.`examYear` "
					    + " AND `ssc`.`examMonth` = `tt`.`examMonth` "
					+ " SET `tt`.`scheduleId` = ? , `tt`.`lastModifiedBy` = ?, `tt`.`lastModifiedOn`=sysdate() "
					+ " WHERE `tt`.`examStartDateTime`=? and `tt`.`examEndDateTime`=? and `ssc`.`id` = ? ";

			jdbcTemplate.update(sql4,examsAssessmentsBean.getId(),
					examsAssessmentsBean.getLastModifiedBy(),
					
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getTimebound_id());

			return "success";
		}
		catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}

	}
	
	@Transactional(readOnly = false)
	public String updateScheduleIdInTimeTableForRegistration(ExamsAssessmentsBean examsAssessmentsBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql4 = " "
					+ " UPDATE `exam`.`mba_wx_time_table` `tt` "
					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
					+ " ON "
					        + " `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` "
					    + " AND `ssc`.`examYear` = `tt`.`examYear` "
					    + " AND `ssc`.`examMonth` = `tt`.`examMonth` "
					+ " SET `tt`.`scheduleId` = ? , `tt`.`lastModifiedBy` = ?, `tt`.`lastModifiedOn`=sysdate() "
					+ " WHERE `tt`.`examStartDateTime`=? and `tt`.`examEndDateTime`=? and `ssc`.`id` = ? ";

			int rowsAffected = jdbcTemplate.update(sql4,examsAssessmentsBean.getId(),
					examsAssessmentsBean.getLastModifiedBy(),
					
					examsAssessmentsBean.getExam_start_date_time(),
					examsAssessmentsBean.getExam_end_date_time(),
					examsAssessmentsBean.getTimebound_id());
			if(rowsAffected>0)
			{
				return "success";
			}
			else
			{
				return "No record found in mba_wx_time_table";
			}
			
		}
		catch (Exception e) {
			logger.error("Error:"+e.getMessage());
			// TODO: handle exception
			return e.getMessage();
		}

	}
	
	
//	public String checkIfSlotExistsInTimeTable(ExamsAssessmentsBean examsAssessmentsBean){
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		int i=0;
//		String sql = ""
//				+ " SELECT COUNT(*) "
//				+ " FROM `exam`.`mba_wx_time_table` `tt` "
//				+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
//				+ " ON "
//				        + " `ssc`.`prgm_sem_subj_id` = `tt`.`programSemSubjectId` "
//				    + " AND `ssc`.`examYear` = `tt`.`examMonth` "
//				    + " AND `ssc`.`examMonth` = `tt`.`examYear` "
//				+ " WHERE `examStartDateTime`=? and `examEndDateTime`=? and `ssc`.`id` = ? ";
//		
//		try {
//		 i = (int)jdbcTemplate.queryForObject(sql, new Object[] { 
//				examsAssessmentsBean.getExam_start_date_time(),
//				examsAssessmentsBean.getExam_end_date_time(),
//				examsAssessmentsBean.getTimebound_id()
//		},Integer.class);
//		}catch(Exception e) {
//			return "Error in slot check";
//		}
//		 
//		if(i>0) {
//			return "success";
//		}
//		return "Exam Start/End DateTime not matching with TimeTable entries of this subject. Assessment not inserted.";
//	}
	
	@Transactional(readOnly = false)
	public int getExamScheduleId(ExamsAssessmentsBean examsAssessmentsBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select id from exam.exams_schedule where timebound_id=? and exam_start_date_time=? and exam_end_date_time=? and schedule_id=? ";
			int id = 0;		
			try {
				ExamsAssessmentsBean	bean = (ExamsAssessmentsBean) jdbcTemplate.queryForObject(sql, 
						new Object[] {examsAssessmentsBean.getTimebound_id(),examsAssessmentsBean.getExam_start_date_time(),
								examsAssessmentsBean.getExam_end_date_time(),examsAssessmentsBean.getSchedule_id()},
					new BeanPropertyRowMapper(ExamsAssessmentsBean.class) );
				id=Integer.parseInt(bean.getId());
				if (id > 0) {
					return id;
				} else {
					return 0;
				}			
			} catch (Exception e) {
				logger.info("Exception is:"+e.getMessage());
				return -1;
			}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentSubjectConfigExamBean> getSubjectByBatchIdAndAcadYearAndAcadMonth(String batch_id,String acadYear , String acadMonth){

		ArrayList<StudentSubjectConfigExamBean> subjectList = new ArrayList<StudentSubjectConfigExamBean>();
	
			String sql = "SELECT s_s_c.*,p_s_s.subject FROM lti.student_subject_config s_s_c,exam.program_sem_subject p_s_s where s_s_c.prgm_sem_subj_id = p_s_s.id and acadYear = ? and acadMonth = ? ";			
			ArrayList<String> parameters = new ArrayList<String>();
			parameters.add(acadYear);
			parameters.add(acadMonth);			
			if(batch_id != null){				
				sql += " and s_s_c.batchId = ? ";
				parameters.add(batch_id);
			}			
			Object [] args = parameters.toArray();
			jdbcTemplate = new JdbcTemplate(dataSource);			
		try {
			subjectList = (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
			
			return subjectList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return subjectList;
		}
	}
	
//	to get passfail/exam details for MBAWX
	@Transactional(readOnly = true)
	public boolean  hasAppearedForExamForGivenSemMonthYearPreviewMarksheet_MBAWX(EmbaPassFailBean passFail){
		String sql = "SELECT   " + 
				"	count(*)  " + 
				"FROM   " + 
				"	exam.mba_passfail pf  " + 
				"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
				"	INNER JOIN  exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   " + 
				"WHERE   " + 
				"	pf.sapid = ? ";
		
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{passFail.getSapid()},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
//	to get passfail/exam details for MBAWX
	@Transactional(readOnly = true)
	public boolean  hasAppearedForExamForGivenSemMonthYearPreviewMarksheet_MBAX(EmbaPassFailBean passFail){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT   " + 
				"	count(*)  " + 
				"FROM   " + 
				"	exam.mbax_passfail pf  " + 
				"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
				"	INNER JOIN  exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   " + 
				"WHERE   " + 
				"	pf.sapid = ? ";
		
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{passFail.getSapid()},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public List<EmbaMarksheetBean> getStudentsForSRForMBAWX(EmbaPassFailBean bean) {
//		jdbcTemplate = new JdbcTemplate(dataSource);
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());
		List<Integer> serviceRequestIdList = Stream.of(commaSeparatedList.split(",", -1))
				.map(s -> Integer.parseInt(s))
				.collect(Collectors.toList());
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("serviceRequestIdList", serviceRequestIdList);
		//commenetd by sachin
	/*	String sql = "SELECT " + 
				" s.*, sr.sem, r.year year, r.month month, sr.year examYear, sr.month examMonth, sr.additionalInfo1 " + 
				"FROM " + 
				"    exam.students s, " + 
				"    portal.service_request sr, " +
				"    exam.registration r " + 
				"WHERE " + 
				"    sr.sapid = s.sapid AND " + 
				"  sr.sapid = r.sapid  AND " +
                " r.sem = sr.sem " +
				"        AND s.sem = (SELECT " + 
				"            MAX(s2.sem) " + 
				"        FROM " + 
				"            exam.students s2 " + 
				"        WHERE " + 
				"            s2.sapid = s.sapid and s.program = 'MBA - WX') AND sr.id IN ( " + commaSeparatedList + ") order by s.centerCode asc ";*/
		// added by sachin for ordering
String sql="SELECT " + 
		" s.*, sr.sem, r.year year, r.month month, sr.year examYear, sr.month examMonth, sr.additionalInfo1 " + 
		"FROM " + 
		"    exam.students s, " + 
		"    portal.service_request sr, " +
		"    exam.registration r " + 
		"WHERE " + 
		"    sr.sapid = s.sapid AND " + 
		"  sr.sapid = r.sapid  AND " +
        " r.sem = sr.sem " +
		"        AND s.sem = (SELECT " + 
		"            MAX(s2.sem) " + 
		"        FROM " + 
		"            exam.students s2 " + 
		"        WHERE " + 
		"            s2.sapid = s.sapid ) and (s.consumerProgramStructureId in ('111','131','151','158','160','156','157','155')) AND sr.id IN ( :serviceRequestIdList ) order by field(id, :serviceRequestIdList )";

		ArrayList<EmbaMarksheetBean> studentList = (ArrayList<EmbaMarksheetBean>)namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<EmbaMarksheetBean>(EmbaMarksheetBean.class));
		return studentList;
	 }
	
	
	
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}

	
	@Transactional(readOnly = true)
	public List<EmbaMarksheetBean> getStudentsForSRForMBAX(EmbaPassFailBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());
		// commented by sachin
	/*	String sql = "SELECT " + 
				" s.*, sr.sem, r.year year, r.month month, sr.year examYear, sr.month examMonth, sr.additionalInfo1 " + 
				"FROM " + 
				"    exam.students s, " + 
				"    portal.service_request sr, " +
				"    exam.registration r " + 
				"WHERE " + 
				"    sr.sapid = s.sapid AND " + 
				"  sr.sapid = r.sapid  AND " +
                " r.sem = sr.sem " +
				"        AND s.sem = (SELECT " + 
				"            MAX(s2.sem) " + 
				"        FROM " + 
				"            exam.students s2 " + 
				"        WHERE " + 
				"            s2.sapid = s.sapid and s.program = 'MBA - X') AND sr.id IN ( " + commaSeparatedList + ") order by s.centerCode asc ";*/

		// added by sachin for generating pdf in ordr of SR ids
		String sql= "SELECT " + 
				" s.*, sr.sem, r.year year, r.month month, sr.year examYear, sr.month examMonth, sr.additionalInfo1 " + 
				"FROM " + 
				"    exam.students s, " + 
				"    portal.service_request sr, " +
				"    exam.registration r " + 
				"WHERE " + 
				"    sr.sapid = s.sapid AND " + 
				"  sr.sapid = r.sapid  AND " +
                " r.sem = sr.sem " +
				"        AND s.sem = (SELECT " + 
				"            MAX(s2.sem) " + 
				"        FROM " + 
				"            exam.students s2 " + 
				"        WHERE " + 
				"            s2.sapid = s.sapid and s.program = 'MBA - X') AND sr.id IN ( " + commaSeparatedList + ") order by field(id, " + commaSeparatedList + " ) ";
		
		ArrayList<EmbaMarksheetBean> studentList = (ArrayList<EmbaMarksheetBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(EmbaMarksheetBean.class));
		return studentList;
	}
	//MBA - X
	@Transactional(readOnly = true)
	public ArrayList<StudentsTestDetailsExamBean> getIAScoresForMbaxStudentSubject(String sapid,String timeboundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentsTestDetailsExamBean> iaScoreList = new ArrayList<StudentsTestDetailsExamBean>();
		String sql="SELECT tst.*, t.startDate, t.endDate , COALESCE(tst.score,0) as scoreInInteger , t.showResultsToStudents FROM exam.upgrad_student_assesmentscore tst " + 
				" INNER JOIN exam.upgrad_test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId" + 
				" INNER JOIN acads.upgrad_sessionplan_module spm ON spm.id = ttcm.referenceId" + 
				" INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId" + 
				" INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId " + 
				" INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
				" INNER JOIN exam.upgrad_test t ON t.id= tst.testId" + 
				" WHERE stm.timeboundId = ? and tst.sapid=? "
				+ " and spm.topic <> 'Generic Module For Session Plan ' "; //added to avoid generic module IA Scores
		try {
		iaScoreList = (ArrayList<StudentsTestDetailsExamBean>)jdbcTemplate.query(sql, new Object[] {timeboundId,sapid}, new BeanPropertyRowMapper<StudentsTestDetailsExamBean>(StudentsTestDetailsExamBean.class));
		}catch(Exception e) {
			//
		}
		return iaScoreList;
	}

	@Transactional(readOnly = false)
	public void updateEmbaGradeAndPointsList(final List<EmbaPassFailBean> embaPassFailList, final String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_passfail` " 
				+ " SET `grade` = ?, "
				+ " `points` = ?, "
				+ " `lastModifiedBy` = ? "
				+ " WHERE `timeboundId` = ? AND `sapid` = ?";
		

		try {
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					
					ps.setString(1, embaPassFailList.get(i).getGrade());
					ps.setString(2, embaPassFailList.get(i).getPoints());
					ps.setString(3, userId);
					ps.setString(4, embaPassFailList.get(i).getTimeboundId());
					ps.setString(5, embaPassFailList.get(i).getSapid());
				}

				@Override
				public int getBatchSize() {
					return embaPassFailList.size();
				}
			  });
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			

		}
		
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailBySapid(String sapid, String sem){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql ="SELECT " + 
				"    pf.*," + 
				"    pss.subject," + 
				"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total " + 
				" FROM " + 
				"    exam.mba_passfail pf " + 
				"        INNER JOIN" + 
				"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN" + 
				"    exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				" WHERE " + 
				"    pf.sapid = ? and pss.sem = ? ";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sapid, sem}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getMBAXPassFailBySapid(String sapid, String sem){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql ="SELECT " + 
				"    pf.*," + 
				"    pss.subject," + 
				"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total " + 
				" FROM " + 
				"    exam.mbax_passfail pf " + 
				"        INNER JOIN" + 
				"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN" + 
				"    exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				" WHERE " + 
				"    pf.sapid = ? and pss.sem = ? ";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sapid, sem}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public int getProgramSemSubjectIdFromTimeboundId(String timeboundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT prgm_sem_subj_id FROM lti.student_subject_config where id=?"; 
		int pssi=0;
		try {
			pssi = (int) jdbcTemplate.queryForObject(sql, new Object[] {timeboundId},Integer.class);
		}catch(Exception e) {
			
		}
		return pssi;
	}
	
	@Transactional(readOnly = true)
public List<EmbaPassFailBean> getEmbaPassFailByAllSapids(String sapids){
//	jdbcTemplate = new JdbcTemplate(dataSource);
	NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
	sapids = generateCommaSeparatedList(sapids);
	
	String sql ="SELECT " + 
			"    pf.*," + 
			" pss.sem psssem,  " +
			" pss.sem sem,  " +
			"    pss.subject, " + 
			" ssc.acadYear as year, " + 
			" ssc.acadMonth as month, " + 
			" ssc.examYear, " + 
			" ssc.examMonth, " +  
			"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total " + 
			" FROM " + 
			"    exam.mba_passfail pf " + 
			"        INNER JOIN" + 
			"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
			"        INNER JOIN" + 
			"    exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id " + 
			" WHERE " + 
			"    pf.sapid in ( :sapidList )  " +
            " AND pf.grade is not null AND pf.points is not null " +
			" AND  pf.isResultLive = 'Y' " + 
			" order by pf.sapid, pss.sem, pss.subject; ";
	
	try { 
		List<String> sapidList = Stream.of(sapids.split(",", -1))
				.collect(Collectors.toList());
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapidList", sapidList);
		passfailList = (ArrayList<EmbaPassFailBean>)namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
	}catch(Exception e) {
		
	}		
	return passfailList;
}


	@Transactional(readOnly = true)
public List<EmbaPassFailBean> getMBAXPassFailByAllSapids(String sapid){
	jdbcTemplate = new JdbcTemplate(dataSource);
	List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
	String sql ="SELECT " + 
			"    pf.*," + 
			" pss.sem psssem,  " +
			" pss.sem sem,  " +
			"    pss.subject, " + 
			" ssc.acadYear as year, " + 
			" ssc.acadMonth as month, " + 
			" ssc.examYear, " + 
			" ssc.examMonth, " +  
			"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total " + 
			" FROM " + 
			"    exam.mbax_passfail pf " + 
			"        INNER JOIN" + 
			"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
			"        INNER JOIN" + 
			"    exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id " + 
			" WHERE " + 
			"    pf.sapid = ?  " +
            " AND pf.grade is not null AND pf.points is not null " +
			" AND  pf.isResultLive = 'Y' " + 
			" order by pf.sapid, pss.sem,  pss.subject; ";
	
	try {
		passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] { sapid } , new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
	}catch(Exception e) {
		
	}		
	return passfailList;
}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSemsFromRegistration(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> sems = new ArrayList<>();
//		sems = (ArrayList<String>)jdbcTemplate.query("SELECT sem FROM exam.registration where sapid =" + sapid +
//				" AND DATE_FORMAT(STR_TO_DATE(CONCAT(`year`, `month`, '01'), " +
//	           "         '%Y %M %d'), " +
//	           " '%Y-%m-%d') < DATE_FORMAT(STR_TO_DATE(CONCAT('" + CURRENT_MBAWX_ACAD_YEAR + "', '" + CURRENT_MBAWX_ACAD_MONTH + "', '01'), '%Y %M %d'), " +
//	           " '%Y-%m-%d') " 
	          String sql = "SELECT sem FROM exam.registration where sapid = ? ";  
	sems = (ArrayList<String>)jdbcTemplate.query(sql , new Object[] { sapid }
				,new SingleColumnRowMapper(String.class));
		return sems;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getSemsFromRegistrationMBAX(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> sems = new ArrayList<>();
//		String sql = "SELECT sem FROM exam.registration where sapid =" + sapid +
//				" AND DATE_FORMAT(STR_TO_DATE(CONCAT(`year`, `month`, '01'), " +
//		           "         '%Y %M %d'), " +
//		           " '%Y-%m-%d') < DATE_FORMAT(STR_TO_DATE(CONCAT('" + CURRENT_MBAX_ACAD_YEAR + "', '" + CURRENT_MBAX_ACAD_MONTH + "', '01'), '%Y %M %d'), " +
//		           " '%Y-%m-%d') ";
		
		sems = (ArrayList<String>)jdbcTemplate.query( "SELECT sem FROM exam.registration where sapid = " + sapid 
				,new SingleColumnRowMapper(String.class));
		return sems;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getSubjectToClearBySem(String sem, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> subjectsToClearBySemList = new ArrayList<>();
		ArrayList<String> semList = new ArrayList<>();
		String sql =" SELECT " +  
			    " id, examYear, examMonth " +
			    " FROM " +
			        " lti.student_subject_config " +
			    " WHERE " +
			        " prgm_sem_subj_id IN (SELECT " + 
			                " id " +
			            " FROM " +
			                " exam.program_sem_subject " +
			            " WHERE " +
			                " (consumerProgramStructureId = 111 OR consumerProgramStructureId = 151 " +
			                    " OR consumerProgramStructureId = 119 OR consumerProgramStructureId = 131 OR consumerProgramStructureId = 160 OR consumerProgramStructureId = 158 ) " +
			            " ) AND id IN (SELECT " + 
			                " max(pf.timeboundId) " +
			            " FROM " +
			                " lti.timebound_user_mapping tum " +
			                " inner join lti.student_subject_config ssc on tum.timebound_subject_config_id = ssc.id " +
			                " INNER JOIN " +
			                " exam.mba_passfail pf ON tum.userId = pf.sapid " +
			                " INNER JOIN  exam.program_sem_subject pss on pss.id = pf.prgm_sem_subj_id " +
			            " WHERE " +
			               " userId = ? " +
			               " AND pf.grade is not null " + 
			               " AND pf.points is not null " +
		                    " AND pss.sem = ? "  +			                    
			            " GROUP BY pf.prgm_sem_subj_id "  +
			            "    ) " +
			            " AND examYear IS NOT NULL " +
			            " AND examMonth IS NOT NULL " +
			            " AND examYear <> '' " +
			            " AND examMonth <> '' " +
			    		" group by prgm_sem_subj_id " +
			            " ORDER BY DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), " + 
			            "                '%Y %M %d'), " + 
			            "        '%Y-%m-%d') DESC; " 
			    		
			            
			            ;
		  
		subjectsToClearBySemList =  (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,
				new Object[] {sapid, sem},
				new BeanPropertyRowMapper(EmbaPassFailBean.class));
		 
		
		return subjectsToClearBySemList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getSubjectToClearBySem(String sem, String sapid,String nonGradedMasterKeys) {
		ArrayList<EmbaPassFailBean> subjectsToClearBySemList = new ArrayList<>();
		ArrayList<String> semList = new ArrayList<>();
		
		//Prepare SQL query
		String sql =new StringBuilder("SELECT  id, examYear, examMonth  FROM  lti.student_subject_config WHERE ")
			           .append("prgm_sem_subj_id IN (SELECT  id  FROM  exam.program_sem_subject  WHERE  consumerProgramStructureId in ("+nonGradedMasterKeys+")  )  ")
			           .append("AND id IN (SELECT  max(pf.timeboundId)  FROM lti.timebound_user_mapping tum ")
			           .append("INNER JOIN lti.student_subject_config ssc on tum.timebound_subject_config_id = ssc.id ")
			           .append("INNER JOIN  exam.mba_passfail pf ON tum.userId = pf.sapid ")
			           .append("INNER JOIN  exam.program_sem_subject pss on pss.id = pf.prgm_sem_subj_id ")
			           .append("WHERE  userId = ?  AND pss.sem = ?  GROUP BY pf.prgm_sem_subj_id  ) ")
			           .append("AND examYear IS NOT NULL  AND examMonth IS NOT NULL  AND examYear <> '' " )
			           .append("AND examMonth <> ''  group by prgm_sem_subj_id ")
			           .append("ORDER BY DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') DESC ").toString() ;
		
		//Execute SQL query with JdbcTemplate query method(-,-,...)
		subjectsToClearBySemList =  (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(EmbaPassFailBean.class),sapid,sem);
		
		//return applicable mark sheet details like semester, year and month. 
		return subjectsToClearBySemList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getSubjectToClearBySemMBAX(String sem, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> subjectsToClearBySemList = new ArrayList<>();
		ArrayList<String> semList = new ArrayList<>();
		String sql =" SELECT " +  
			    " id, examYear, examMonth " +
			    " FROM " +
			        " lti.student_subject_config " +
			    " WHERE " +
			        " prgm_sem_subj_id IN (SELECT " + 
			                " id " +
			            " FROM " +
			                " exam.program_sem_subject " +
			            " WHERE " +
			                " (consumerProgramStructureId = 111 OR consumerProgramStructureId = 151 " +
			                    " OR consumerProgramStructureId = 119 OR consumerProgramStructureId = 126"
			                    + " OR consumerProgramStructureId = 162) " +
			            " ) AND id IN (SELECT " + 
			                " max(pf.timeboundId) " +
			            " FROM " +
			                " lti.timebound_user_mapping tum " +
			                " inner join lti.student_subject_config ssc on tum.timebound_subject_config_id = ssc.id " +
			                " INNER JOIN " +
			                " exam.mbax_passfail pf ON tum.userId = pf.sapid " +
			                " INNER JOIN  exam.program_sem_subject pss on pss.id = pf.prgm_sem_subj_id " +
			            " WHERE " +
			               " userId = " + sapid +
			               " AND pf.grade is not null " + 
			               " AND pf.points is not null " +
		                    " AND pss.sem = "  +  sem +			                    
			            " GROUP BY pf.prgm_sem_subj_id "  +
			            "    ) " +
			            " AND examYear IS NOT NULL " +
			            " AND examMonth IS NOT NULL " +
			            " AND examYear <> '' " +
			            " AND examMonth <> '' " +
			    		" group by prgm_sem_subj_id " +
			            " ORDER BY DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), " + 
			            "                '%Y %M %d'), " + 
			            "        '%Y-%m-%d') DESC; " 
			    		
			            
			            ;
		  
		subjectsToClearBySemList =  (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(EmbaPassFailBean.class));
		 
		
		return subjectsToClearBySemList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getPassFailDataByTimeboundIdAndSapid(String timeBoundIdList, String sapid, String sem){
//		jdbcTemplate = new JdbcTemplate(dataSource);
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		timeBoundIdList = generateCommaSeparatedList(timeBoundIdList);
		ArrayList<EmbaPassFailBean> passFailData = new ArrayList<>();
		List<Integer> timeBoundId = Stream.of(timeBoundIdList.split(",", -1))
				 .map(s -> Integer.parseInt(s))
				 .collect(Collectors.toList());
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("timeboundId", timeBoundId);
		queryParams.addValue("sem", sem);
		
		String sql = " SELECT " +
	    " * " +
	" FROM " + 
	    " `exam`.`mba_passfail` mp " + 
	        " INNER JOIN " + 
	    " `exam`.`program_sem_subject` pss ON mp.prgm_sem_subj_id = pss.id " + 
	" WHERE " + 
	    " sapId = :sapid  " +
	        " AND timeboundId IN ( :timeboundId ) " +
	        " AND pss.sem = :sem " +
		 	" AND grade is not null " +
	        " AND points is not null " +
	        " AND isPass = 'Y' ";
		
		
		passFailData = (ArrayList<EmbaPassFailBean>)namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(EmbaPassFailBean.class));
		return passFailData;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getNonGradePassFailDataByTimeboundIdAndSapid(String timeBoundIdList, String sapid, String sem){
		ArrayList<EmbaPassFailBean> passFailData = new ArrayList<>();
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		//Prepare SQL query.
		StringBuilder GET_PASSFAIL_DETAILS = new StringBuilder("SELECT  * FROM  `exam`.`mba_passfail` mp  ")
						.append("INNER JOIN  `exam`.`program_sem_subject` pss ON mp.prgm_sem_subj_id = pss.id ")
						.append("WHERE  sapId = :sapid AND timeboundId IN ( :timeboundId ) AND pss.sem = :sem AND isPass = 'Y' ");
		
		timeBoundIdList = generateCommaSeparatedList(timeBoundIdList);
		
		//Create a list of timeBound id's from a comma separated string. 
		List<Integer> timeBoundId = Stream.of(timeBoundIdList.split(",", -1)).map(s -> Integer.parseInt(s))
				.collect(Collectors.toList());
		
		//create MapSqlParameterSource map and add SQL query parameter name and it's value. 
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("timeboundId", timeBoundId);
		queryParams.addValue("sem", sem);
		
		//Execute a query with NamedJdbcTemplate query method.   
		passFailData = (ArrayList<EmbaPassFailBean>) namedJdbcTemplate.query(GET_PASSFAIL_DETAILS.toString(),
				queryParams, new BeanPropertyRowMapper(EmbaPassFailBean.class));
		
		//Return pass fail details
		return passFailData;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean> getPassFailDataByTimeboundIdAndSapidMBAX(String timeBoundIdList, String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);	
		timeBoundIdList = generateCommaSeparatedList(timeBoundIdList);
		ArrayList<EmbaPassFailBean> passFailData = new ArrayList<>();
		
		String sql = "SELECT * FROM exam.mbax_passfail where sapId="+ sapid +" and timeboundId in (" + timeBoundIdList + ")";
		passFailData = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(EmbaPassFailBean.class));
		return passFailData;
	}

	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailBySapidTermMonthYearAllSem(String sapid, String term, String acadsMonth, String acadsYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject, pss.consumerProgramStructureId, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pss.id = pf.prgm_sem_subj_id "
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem <= ? "
			//	+ " AND ssc.acadMonth = ? "
			//	+ " AND ssc.acadYear = ? "
				+ " AND pf.grade is not null; ";
		
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term, 
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailBySapidTermMonthYearAllSem(String sapid, String term){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject, pss.consumerProgramStructureId, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pss.id = pf.prgm_sem_subj_id "
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem <= ? ";
		
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term, 
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		}catch(Exception e) {
			
		}		
		return passfailList;
	}

	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailBySapidTermMonthYear(String sapid, String term, String acadsMonth, String acadsYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject,pss.consumerProgramStructureId,  "
					+ " ssc.* ,"
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pf.prgm_sem_subj_id = pss.id " 
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem = ? "
//				+ " AND ssc.acadMonth = ? "
//				+ " AND ssc.acadYear = ? "
				+ " AND pf.grade is not null; ";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getEmbaPassFailBySapidTermMonthYear(String sapid, String term){
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		//Prepare SQL query
		StringBuilder sql  =new StringBuilder("SELECT  pf.*, pss.subject, ssc.* , (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS total, ")
				.append(" COALESCE(pf.iaScore, 0) AS iaScore, COALESCE(pf.teeScore, 0) AS teeScore  ")
				.append(" FROM  exam.mba_passfail pf  INNER JOIN lti.student_subject_config ssc  ON ssc.id = pf.timeBoundId ")
				.append(" INNER JOIN exam.program_sem_subject pss  ON pf.prgm_sem_subj_id = pss.id ") 
				.append(" WHERE  pf.sapid = ? AND pss.sem = ? ");
		
		try {
			//Execute SQL query with JdbcTemplate query() method.
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql.toString(),new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class),sapid,term);
			
		}catch(Exception e) {
			
		}	
		//Return pass fail details. 
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getMbaXPassFailBySapidTermMonthYear(String sapid, String term, String acadsMonth, String acadsYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql =""
				+ " SELECT "
					+ " `pf`.`timeboundId`, "
					+ " `pf`.`sapid`, "
					+ " `pf`.`schedule_id`, "
					+ " `pf`.`attempt`, "
					+ " `pf`.`sem`, "
					+ " `pf`.`graceMarks`, "
					+ " `pf`.`isPass`, "
					+ " `pf`.`failReason`, "
					+ " `pf`.`isResultLive`, "
					+ " `pf`.`status`, "
					+ " `pf`.`grade`, "
					+ " `pf`.`points`, "
					+ " `pf`.`prgm_sem_subj_id`, "
					+ " COALESCE(`pf`.`iaScore`, 0) AS `iaScore`, "
					+ " COALESCE(`pf`.`teeScore`, 0) AS `teeScore`, "
					+ " pss.subject, "
					+ " ssc.* ,"
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total "
				+ " FROM  exam.mbax_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pf.prgm_sem_subj_id = pss.id " 
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem = ? "
//				+ " AND ssc.acadMonth = ? "
//				+ " AND ssc.acadYear = ? "
				+ " AND pf.grade is not null; ";

		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		}catch(Exception e) {
			
		}		
		return passfailList;

	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getMbaXPassFailBySapidTermMonthYearAllSem(String sapid, String term, String acadsMonth, String acadsYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		String sql =""
				+ " SELECT "
					+ " `pf`.`timeboundId`, "
					+ " `pf`.`sapid`, "
					+ " `pf`.`schedule_id`, "
					+ " `pf`.`attempt`, "
					+ " `pf`.`sem`, "
					+ " `pf`.`graceMarks`, "
					+ " `pf`.`isPass`, "
					+ " `pf`.`failReason`, "
					+ " `pf`.`isResultLive`, "
					+ " `pf`.`status`, "
					+ " `pf`.`grade`, "
					+ " `pf`.`points`, "
					+ " `pf`.`prgm_sem_subj_id`, "
					+ " COALESCE(`pf`.`iaScore`, 0) AS `iaScore`, "
					+ " COALESCE(`pf`.`teeScore`, 0) AS `teeScore`, "
					+ " pss.subject, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total "
				+ " FROM  exam.mbax_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pss.id = pf.prgm_sem_subj_id "
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem <= ? "
			//	+ " AND ssc.acadMonth = ? "
			//	+ " AND ssc.acadYear = ? "
				+ " AND pf.grade is not null; ";
		
		try {
			passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term, 
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		}catch(Exception e) {
			
		}		
		return passfailList;
	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getFinishedTeeAssessmentsBySapid(String sapId) {
		List<ExamsAssessmentsBean> teeFinishedAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "SELECT  " + 
				"    es.*, " + 
				"    es.exam_end_date_time AS endDate, " + 
				"    es.exam_start_date_time AS startDate, " + 
				"    ea.name, " + 
				"    ea.customAssessmentName AS testName, " + 
				"    'assessments' AS type, " + 
				"    pss.subject " + 
				"FROM " + 
				"				     exam.exams_assessments ea " + 
				"				         INNER JOIN " + 
				"				     exam.exams_schedule es ON es.assessments_id = ea.id " + 
				"				         INNER JOIN " + 
				"						exam.assessment_timebound_id ati ON  ati.assessments_id = ea.id " + 
				"                        INNER JOIN " + 
				"				     lti.timebound_user_mapping tum ON tum.timebound_subject_config_id = ati.timebound_id " + 
				"				 		INNER JOIN " + 
				"				     lti.student_subject_config ssc ON ssc.id =  ati.timebound_id " + 
				"				 		INNER JOIN " + 
				"				   exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id " + 
				"WHERE " + 
				"    tum.userId = ? "+
				"   AND ( tum.role =  'Student' OR (es.max_score = 100 AND tum.role in ( 'Resit', 'Student' )) ) "+
				"        AND SYSDATE() > es.exam_end_date_time " + 
				"        AND SYSDATE() BETWEEN ssc.startDate AND ssc.endDate " + 
				"ORDER BY es.exam_start_date_time";
		 try {
			 teeFinishedAssessmentsList = jdbcTemplate.query(sql,new Object[] {sapId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }

		 return checkIfAssessmentsApplicable(sapId, teeFinishedAssessmentsList);

	}
	
	@Transactional(readOnly = true)
	public List<ExamsAssessmentsBean> getFinishedTeeAssessmentsBySapidFromScheduleInfo(String sapId) {
		List<ExamsAssessmentsBean> teeFinishedAssessmentsList = new ArrayList<ExamsAssessmentsBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "select es.id,es.assessments_id,sm.timebound_id,sm.schedule_id,sm.scheduleName as schedule_name,sm.acessKey as schedule_accessKey "
                +" ,sm.joinURL as schedule_accessUrl,es.schedule_status,sm.accessStartDateTime as exam_start_date_time,sm.accessEndDateTime as exam_end_date_time, "
                +" es.active,es.isResultLive, sm.max_score, sm.accessEndDateTime as endDate,sm.accessStartDateTime as startDate, ea.name, ea.customAssessmentName as testName, " 
                +" 'assessments' AS type, sm.subject "
                +" from exam.exams_scheduleinfo_mettl sm INNER JOIN exam.exams_schedule es ON sm.acessKey=es.schedule_accessKey and sm.timebound_id=es.timebound_id "
				+" INNER JOIN exam.exams_assessments ea ON es.assessments_id=ea.id INNER JOIN lti.student_subject_config ssc ON ssc.id=sm.timebound_id "
                +" where sm.sapid= ? and  SYSDATE() > sm.accessEndDateTime "
				+" AND SYSDATE() BETWEEN ssc.startDate AND ssc.endDate order by sm.accessStartDateTime"; 
		 try {
			 teeFinishedAssessmentsList = jdbcTemplate.query(sql,new Object[] {sapId}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));			 
		 }
		 catch(Exception e){
			 
			
		 }

		 return checkIfAssessmentsApplicableFromScheduleInfo(sapId, teeFinishedAssessmentsList);

	}
	
	@Transactional(readOnly = true)
	public Boolean isResitResultLive(String sapid,String term) {
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();

		String sql = "SELECT " + 
			    "pf.* " +
			    "FROM " +
			        "exam.mbax_passfail pf " + 
			            "INNER JOIN " +
			        "exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id " +
			            "INNER JOIN " +
			        "lti.student_subject_config ssc ON pf.timeboundId = ssc.id " +
			            "INNER JOIN " +
			        "(SELECT  " +
			            "timebound_id, MAX(max_score) max_score " +
			        "FROM " + 
			            "exam.mbax_exams_schedule " +
			        "GROUP BY timebound_id) es ON ssc.id = es.timebound_id " +
			    "WHERE " +
			        "sapid = ? AND max_score = 40 " +  
			        "and isPass='N' " +
			        "and grade is not null " + 
			        "and  isResultLive = 'Y' " +
			        "and  pss.sem = ?; ";
		passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
				sql, 
				new Object[] {
					sapid, term
				}, 
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
			);
		if(passfailList.size() > 0) {
			return false;
		}else {
			return true;
		}
		 
	}

	
	
	@Transactional(readOnly = true)
	public ArrayList<MettlResponseBean> getMettlTeeStudentDataForSchedular(String examDate){
		ArrayList<MettlResponseBean> mettlResponseList = new ArrayList<MettlResponseBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		String sql = " SELECT  " + 
				"    es.sapid, " + 
				"    es.email, " + 
				"    es.student_name, " + 
				"    esm.schedule_id, " + 
				"    esm.schedule_accessKey, " + 
				"    pss.id AS prgm_sem_subj_id " + 
				"FROM " + 
				"    exam.exams_schedule_mettl esm " + 
				"        INNER JOIN " + 
				"    exam.pg_assessment pa ON pa.assessmentId = esm.assessments_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.sifySubjectCode = pa.sifyCode " + 
				"        INNER JOIN " + 
				"    (SELECT  " + 
				"        CONCAT(st.firstName, ' ', st.lastName) AS student_name, " + 
				"            eb.sapid, " + 
				"            eb.sem, " + 
				"            eb.subject, " + 
				"            eb.emailId  AS email, " + 
				"            CONCAT(DATE_FORMAT(eb.examDate, '%Y%m%d'), DATE_FORMAT(eb.examTime, '%H%i%s')) AS examDateTime, " + 
				"            eb.examDate, " + 
				"            st.consumerProgramStructureId " + 
				"    FROM " + 
				"        exam.exambookings eb " + 
				"    INNER JOIN exam.students st ON st.sapid = eb.sapid AND eb.booked = 'Y' " ;
		if(examDate != null) {
			sql +=  "  AND DATE_FORMAT(eb.examDate, '%Y-%m-%d') = '"+examDate+"' " ; 
		}else {
			sql +=  "  AND DATE_FORMAT(eb.examDate, '%Y-%m-%d') = CURRENT_DATE() " ;
		}
		sql +=	" 	AND eb.emailId <> '' " + 
				"   AND eb.emailId IS NOT NULL "+
				"   GROUP BY eb.sapid , eb.subject " + 
				"   ORDER BY eb.lastModifiedDate DESC) es ON pss.sem = es.sem " + 
				"        AND pss.subject = es.subject " + 
				"        AND es.consumerProgramStructureId = pss.consumerProgramStructureId  " + 
				"        AND es.examDateTime = DATE_FORMAT(esm.exam_start_date_time, '%Y%m%d%H%i%s') " + 
				"WHERE " + 
				"    esm.active = 'Y' ";

// optional sql by Abhay
//		String sql = " SELECT  " + 
//				"    t.sapid, " + 
//				"    t.email, " + 
//				"    t.student_name, " + 
//				"    esm.schedule_id, " + 
//				"    esm.schedule_accessKey, " + 
//				"    t.prgm_sem_subj_id " + 
//				"FROM " + 
//				"    exam.exams_schedule_mettl esm " + 
//				"        INNER JOIN " + 
//				"    exam.pg_assessment pa ON pa.assessmentId = esm.assessments_id " + 
//				"        INNER JOIN " + 
//				"    (SELECT  " + 
//				"        eb.sapid, " + 
//				"            IFNULL(eb.emailId, s.emailId) AS email, " + 
//				"            CONCAT(s.firstName, ' ', s.lastName) AS student_name, " + 
//				"            pss.sifySubjectCode, " + 
//				"            pss.id AS prgm_sem_subj_id " + 
//				"    FROM " + 
//				"        exam.exambookings eb " + 
//				"    INNER JOIN exam.students s ON eb.sapid = s.sapid " + 
//				"    INNER JOIN exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
//				"        AND pss.subject = eb.subject " + 
//				"        AND pss.sem = eb.sem " + 
//				"    WHERE " + 
//				"        eb.examDate = CURRENT_DATE() " + 
//				"            AND eb.booked = 'Y') t ON t.sifySubjectCode = pa.sifyCode " + 
//				"WHERE " + 
//				"    DATE_FORMAT(esm.exam_start_date_time, '%Y-%m-%d') = CURRENT_DATE() " + 
//				"        AND esm.active = 'Y' ";
		
		
		mettlResponseList = (ArrayList<MettlResponseBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<MettlResponseBean>(MettlResponseBean.class));
		
		return mettlResponseList;
	}
	
	@Transactional(readOnly = true)
	public List<TEERescheduleExamBookingExcelBean> getTeeRescheduleExamBookingStudent(String examDate){
		List<TEERescheduleExamBookingExcelBean> list = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  "  SELECT  " + 
				"        exambooking.sapid, " + 
				"            exambooking.subject, " + 
				"            exambooking.examDate AS updatedExamDate, " + 
				"            exambooking.examTime AS updatedExamTime, " + 
				"            exambooking.booked AS updatedBooked, " + 
				"            reschedule.examDate AS oldExamDate, " + 
				"            reschedule.examTime AS oldExamTime, " + 
				"            reschedule.booked AS oldBooked, " + 
				"            exambooking.subjectCode, " + 
				"            reschedule.emailId, " + 
				"            reschedule.schedule_id AS oldScheduleId, " + 
				"            exambooking.schedule_id AS newScheduleId, " + 
				"            reschedule.oldScheduleAccessKey, " + 
				"            exambooking.newScheduleAccessKey, " + 
				"            exambooking.assessmentId, " + 
				"            exambooking.testName, " + 
				"            reschedule.programType " + 
				"    FROM " + 
				"        (SELECT  " + 
				"        pg.assessmentId, " + 
				"            eb.examDate, " + 
				"            eb.examTime, " + 
				"            eb.booked, " + 
				"            eb.subject, " + 
				"            eb.sapid, " + 
				"            pss.sifySubjectCode AS SubjectCode, " + 
				"            sm.schedule_id, " + 
				"            eb.trackId, " + 
				"            eb.testTaken, " + 
				"            eb.emailId, " + 
				"            sm.schedule_accessKey AS oldScheduleAccessKey, " + 
				"            ps.programType " + 
				"    FROM " + 
				"        exam.exambookings eb " + 
				"    INNER JOIN exam.students s ON s.sapid = eb.sapid " + 
				"    INNER JOIN exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        AND pss.sem = eb.sem " + 
				"        AND pss.subject = eb.subject " + 
				"    INNER JOIN exam.programs ps ON ps.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"    INNER JOIN exam.pg_assessment pg ON pg.sifyCode = pss.sifySubjectCode " + 
				"    INNER JOIN exam.exams_schedule_mettl sm ON sm.assessments_id = pg.assessmentId " + 
				"        AND CONCAT(DATE_FORMAT(eb.examDate, '%Y%m%d'), DATE_FORMAT(eb.examTime, '%H%i%s')) = DATE_FORMAT(sm.exam_start_date_time, '%Y%m%d%H%i%s') " + 
				"    WHERE " + 
				"        eb.trackId LIKE '%_Reschedule_%' " + 
				"            AND eb.booked = 'N' " + 
				"    GROUP BY eb.sapid , eb.subject " + 
				"    ORDER BY eb.lastModifiedDate DESC) reschedule " + 
				"    INNER JOIN (SELECT  " + 
				"        pg.assessmentId, " + 
				"            eb.examDate, " + 
				"            eb.examTime, " + 
				"            eb.booked, " + 
				"            eb.subject, " + 
				"            eb.sapid, " + 
				"            pss.sifySubjectCode AS subjectCode, " + 
				"            sm.schedule_id, " + 
				"            eb.lastModifiedDate, " + 
				"            CONCAT(sm.schedule_name, '_', pg.name) AS testName, " + 
				"            eb.emailId, " + 
				"            sm.schedule_accessKey AS newScheduleAccessKey " + 
				"    FROM " + 
				"        exam.exambookings eb " + 
				"    INNER JOIN exam.students s ON s.sapid = eb.sapid " + 
				"    INNER JOIN exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        AND pss.sem = eb.sem " + 
				"        AND pss.subject = eb.subject " + 
				"    INNER JOIN exam.programs ps ON ps.consumerProgramStructureId = s.consumerProgramStructureId " + 

				"    INNER JOIN exam.pg_assessment pg ON pg.sifyCode = pss.sifySubjectCode " + 
				"    INNER JOIN exam.exams_schedule_mettl sm ON sm.assessments_id = pg.assessmentId " + 
				"        AND CONCAT(DATE_FORMAT(eb.examDate, '%Y%m%d'), DATE_FORMAT(eb.examTime, '%H%i%s')) = DATE_FORMAT(sm.exam_start_date_time, '%Y%m%d%H%i%s') " + 
				"    WHERE " + 
				"        eb.booked = 'Y') exambooking ON exambooking.sapid = reschedule.sapid " + 
				"        AND exambooking.subject = reschedule.subject " + 
				"    WHERE " + 
				"        DATE_FORMAT(reschedule.examDate, '%Y-%m-%d') = '"+examDate+"' ";
		
		list = (List<TEERescheduleExamBookingExcelBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<TEERescheduleExamBookingExcelBean>(TEERescheduleExamBookingExcelBean.class));
		
		return list;

	} 
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getTestTakenStatusOfExamBookingStudent(String examDate){
		List<ExamBookingTransactionBean> list = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  " SELECT  " + 
				"    sapid, subject, examStartDateTime, testTaken " + 
				"FROM " + 
				"    (SELECT  " + 
				"        sapid, subject, examStartDateTime, testTaken " + 
				"    FROM " + 
				"        `exam`.`exams_pg_scheduleinfo_mettl`  " + 
				"        UNION ALL  " + 
				"	SELECT  " + 
				"        sapid, subject, examStartDateTime, testTaken " + 
				"    FROM " + 
				"        `exam`.`exams_pg_scheduleinfo_history_mettl`) AS examStatus " + 
				"WHERE " + 
				"    (testTaken = 'Mettl Started' " + 
				"        OR testTaken = 'Mettl Completed') " + 
				"        AND DATE_FORMAT(examStartDateTime, '%Y%m%d') = DATE_FORMAT('"+examDate+"', '%Y%m%d')";
		
		list = (List<ExamBookingTransactionBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamBookingTransactionBean>(ExamBookingTransactionBean.class));
		return list;

	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `exam`.`students` "
				+ " WHERE `sapid` = ? "
					+ " AND `sem` = ( "
					+ " SELECT MAX(`sem`) "
					+ " FROM `exam`.`students` "
					+ " WHERE `sapid` = ? "
				+ " )";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapid, sapid }, 
			new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
		);
	}
	

	@Transactional(readOnly = true)
	public String getProgarmTypeFromScheduleId(String scheduleId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =""
				+ " SELECT `programType` "
				+ " FROM `exam`.`exams_assessments` "
				+ " WHERE `id` IN ( "
					+ " SELECT `assessments_id` "
					+ " FROM `exam`.`exams_schedule` "
					+ " WHERE `schedule_id` = ? "
				+ " ) "; 
		try {
			return jdbcTemplate.queryForObject(
				sql, 
				new Object[] { scheduleId },
				String.class
			);
		}catch(Exception e) {
			
		}
		return scheduleId;
	}
	
	
	/**
	 * To update MBA-WX Exam Assessment Exam End Time
	 * @param examAssmtBean - bean having extendedExamEndTime,exam_end_date_time and assessments_id as data.
	 * @return String - As success message
	 * @throws Exception If any exception occurred during the method execution.
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String updateExamAssessmentEndTime(ExamsAssessmentsBean examAssmtBean) throws Exception{
		String updateDateTime=null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Frame SQL query
		updateDateTime="update exam.exams_schedule set exam_end_date_time=(addtime(?, ?)) where schedule_id=?";
		
		//Execute update() method with SQL query and assessment_id,extendedTime and end date as argument
		jdbcTemplate.update(updateDateTime, examAssmtBean.getExam_end_date_time(),examAssmtBean.getExtendExamEndTime(),examAssmtBean.getId());
		
		//return success message as string
		return "success";
	}//updateExamAssessmentDateTime()

	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getMbaXPassFailForStructureChangeStudent(final String sapid, final String term){
	jdbcTemplate = new JdbcTemplate(dataSource);
	List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
	
	String sql =" SELECT  " + 
			"    `pf`.`timeboundId`, " + 
			"    `pf`.`sapid`, " + 
			"    `pf`.`schedule_id`, " + 
			"    `pf`.`attempt`, " + 
			"    `pf`.`sem`, " + 
			"    `pf`.`graceMarks`, " + 
			"    `pf`.`isPass`, " + 
			"    `pf`.`failReason`, " + 
			"    `pf`.`isResultLive`, " + 
			"    `pf`.`status`, " + 
			"    `pf`.`grade`, " + 
			"    `pf`.`points`, " + 
			"    COALESCE(`pf`.`iaScore`, 0) AS `iaScore`, " + 
			"    COALESCE(`pf`.`teeScore`, 0) AS `teeScore`, " + 
			"    pss.subject, " + 
			"    ssc.*, " + 
			" 	 IFNULL(map.acadYear, ssc.acadYear) AS acadYear , " + 
			"    IFNULL(map.acadMonth, ssc.acadMonth) AS acadMonth , " + 
			"    IFNULL(map.examYear, ssc.examYear) AS examYear , " + 
			"    IFNULL(map.examMonth, ssc.examMonth) AS examMonth ,	"+
			"    map.newPssId AS prgm_sem_subj_id , " + 
			"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total " + 
			"FROM " + 
			"    `exam`.`program_sem_subject` `pss` " + 
			"        INNER JOIN " + 
			"    exam.mbax_change_structure_mapping map ON map.newPssId = `pss`.`id` " + 
			"        INNER JOIN " + 
			"    `exam`.`mbax_passfail` `pf` ON map.oldPssId = `pf`.`prgm_sem_subj_id` " + 
			"        AND map.sapid = pf.sapid " + 
			"        INNER JOIN " + 
			"    `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `pf`.`timeboundId` " + 
			"WHERE " + 
			"    pf.sapid = ? AND map.sem = ? " + 
			"        AND pf.grade IS NOT NULL ";

	
	try {
		passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
			sql, 
			new PreparedStatementSetter() {
				 public void setValues(PreparedStatement preparedStatement) throws SQLException {
					 preparedStatement.setString(1,sapid);
					 preparedStatement.setString(2,term);
				 }
			}, 
			new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
		);
		return passfailList;
	}catch(Exception e) {
		
		return passfailList;
	}		
	

}

	@Transactional(readOnly = true)
public List<EmbaPassFailBean> getMbaXPassFailAllSemForStructureChangeStudent(final String sapid,final String term ){
	jdbcTemplate = new JdbcTemplate(dataSource);
	List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
	
	String sql =" SELECT " + 
			"    `pf`.`timeboundId`, " + 
			"    `pf`.`sapid`, " + 
			"    `pf`.`schedule_id`, " + 
			"    `pf`.`attempt`, " + 
			"    `pf`.`sem`, " + 
			"    `pf`.`graceMarks`, " + 
			"    `pf`.`isPass`, " + 
			"    `pf`.`failReason`, " + 
			"    `pf`.`isResultLive`, " + 
			"    `pf`.`status`, " + 
			"    `pf`.`grade`, " + 
			"    `pf`.`points`, " + 
			"    COALESCE(`pf`.`iaScore`, 0) AS `iaScore`, " + 
			"    COALESCE(`pf`.`teeScore`, 0) AS `teeScore`, " + 
			"    pss.subject, " +
			"    map.newPssId AS prgm_sem_subj_id , " + 
			"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total " + 
			"FROM " + 
			"    `exam`.`program_sem_subject` `pss` " + 
			"        INNER JOIN " + 
			"    exam.mbax_change_structure_mapping map ON map.newPssId = `pss`.`id` " + 
			"        INNER JOIN " + 
			"    `exam`.`mbax_passfail` `pf` ON map.oldPssId = `pf`.`prgm_sem_subj_id` " + 
			"        AND map.sapid = pf.sapid " + 
			"        INNER JOIN " + 
			"    `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `pf`.`timeboundId` " + 
			"WHERE " + 
			"    pf.sapid = ? " + 
			"        AND map.sem <= ? " + 
			"        AND pf.grade IS NOT NULL ";
			
	
	try {
		passfailList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(
			sql, 
			new PreparedStatementSetter() {
				 public void setValues(PreparedStatement preparedStatement) throws SQLException {
					 preparedStatement.setString(1,sapid);
					 preparedStatement.setString(2,term);
				 }
			}, 
			new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class)
		);
	}catch(Exception e) {
		
	}		
	return passfailList;
}

	@Transactional(readOnly = true)
public ArrayList<EmbaPassFailBean> getSubjectToClearBySemMBAXForStructureChangeStudent(final String sem,final String sapid) {
	
	ArrayList<EmbaPassFailBean> subjectsToClearBySemList = new ArrayList<>();
	String sql = "  SELECT  " + 
			"    ssc.id, ssc.examYear, ssc.examMonth " + 
			"FROM " + 
			"    lti.timebound_user_mapping tum " + 
			"        INNER JOIN " + 
			"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
			"        INNER JOIN " + 
			"    exam.mbax_passfail pf ON tum.userId = pf.sapid " + 
			"        AND pf.timeboundId = tum.timebound_subject_config_id " + 
			"        INNER JOIN " + 
			"    exam.mbax_change_structure_mapping map ON map.sapid = pf.sapid " + 
			"        AND map.oldPssId = pf.prgm_sem_subj_id " + 
			"        INNER JOIN " + 
			"    exam.program_sem_subject pss ON pss.id = map.newPssId " + 
			"WHERE " + 
			"    tum.userId = ? " + 
			"        AND pf.grade IS NOT NULL " + 
			"        AND pf.points IS NOT NULL " + 
			"        AND map.sem = ? " + 
			"        AND ssc.examYear IS NOT NULL " + 
			"        AND ssc.examMonth IS NOT NULL " + 
			"        AND ssc.examYear <> '' " + 
			"        AND ssc.examMonth <> '' " + 
			"GROUP BY ssc.prgm_sem_subj_id " + 
			"ORDER BY DATE_FORMAT(STR_TO_DATE(CONCAT(ssc.examYear, ssc.examMonth, '01'), " + 
			"                '%Y %M %d'), " + 
			"        '%Y-%m-%d') DESC " ;  
	try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		subjectsToClearBySemList =  (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,
				new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
						 preparedStatement.setString(2,sem);
					 }
				}, 
				new BeanPropertyRowMapper(EmbaPassFailBean.class));
		
		return subjectsToClearBySemList;
	}catch (Exception e) {
		// TODO: handle exception
		
		return subjectsToClearBySemList;
	}
}

	@Transactional(readOnly = true)	
public List<EmbaPassFailBean> getMBAXPassFailByAllSapidsForStructureChangeStudent(String sapid){
	NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
	String sql =" SELECT  " + 
			"    pf.*, " + 
			"    map.sem AS psssem, " + 
			"    map.sem AS sem, " + 
			"    pss.subject, " + 
			"	IFNULL(map.acadYear, ssc.acadYear) AS year , " + 
			"    IFNULL(map.acadMonth, ssc.acadMonth) AS month , " + 
			"    IFNULL(map.examYear, ssc.examYear) AS examYear , " + 
			"    IFNULL(map.examMonth, ssc.examMonth) AS examMonth , " +
			"    map.newPssId AS prgm_sem_subj_id , " +
			"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) ) AS total " + 
			"FROM " + 
			"    exam.mbax_passfail pf " + 
			"        INNER JOIN " + 
			"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
			"		INNER JOIN " + 
			"    exam.mbax_change_structure_mapping map ON map.sapid = pf.sapid " + 
			"        AND map.oldPssId = pf.prgm_sem_subj_id " + 
			"        INNER JOIN " + 
			"    exam.program_sem_subject pss ON pss.id = map.newPssId " + 
			"WHERE " + 
			"    pf.sapid =  :sapid " + 
			"        AND pf.grade IS NOT NULL " + 
			"        AND pf.points IS NOT NULL " + 
			"        AND pf.isResultLive = 'Y' " + 
			"ORDER BY pf.sapid , map.sem , pss.subject " ;
	
	try {
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		passfailList = namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return passfailList;
	}catch(Exception e) {
		
		return passfailList;
	}		
	
}
	
	@Transactional(readOnly = true)
	public List<EmbaMarksheetBean> getStudentsServiceRequestDetails(List<Integer> serviceRequestIdList,
			List<Integer> masterKeys) throws SQLException {
	
		//Prepare SQL query.
		StringBuilder SQL = new StringBuilder("SELECT  s.*, sr.sem, r.year year, r.month month, sr.year examYear, sr.month examMonth, sr.additionalInfo1 ") 
				.append("FROM exam.students s,  portal.service_request sr,  exam.registration r ")
				.append("WHERE sr.sapid = s.sapid AND sr.sapid = r.sapid  AND r.sem = sr.sem ")
				.append("AND s.sem = (SELECT  MAX(s2.sem) FROM exam.students s2 WHERE s2.sapid = s.sapid ) ")
				.append("and s.consumerProgramStructureId in (:consumerProgramStructureId) AND sr.id IN ( :serviceRequestIdList ) order by field(id, :serviceRequestIdList ) ");

		//Create MapSqlParameterSource map and add query parameters values.
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("serviceRequestIdList", serviceRequestIdList);
		queryParams.addValue("consumerProgramStructureId", masterKeys);
		
		//Execute SQL using namedJdbcTemplate's query method.
		List<EmbaMarksheetBean> studentList = namedJdbcTemplate.query(SQL.toString(),
		queryParams, new BeanPropertyRowMapper<EmbaMarksheetBean>(EmbaMarksheetBean.class));
		
		//return students and service request details as a list.
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public List<EmbaPassFailBean> getNonGradedEmbaPassFailByAllSapids(List<String> sapidList) throws SQLException {
		List<EmbaPassFailBean> passfailList = new ArrayList<EmbaPassFailBean>();
		
		//Prepare SQL query.
		StringBuilder GET_PASSFAIL = new StringBuilder("SELECT   pf.*, pss.sem psssem, pss.sem sem, pss.subject, ssc.acadYear as year, ssc.acadMonth as month, ")
				.append("ssc.examYear,  ssc.examMonth, CAST((COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0) + COALESCE(pf.graceMarks, 0)) AS UNSIGNED) AS total, ")
				.append("COALESCE(pf.graceMarks, 0) as graceMarks FROM exam.mba_passfail pf INNER JOIN lti.student_subject_config ssc ON ssc.id = pf.timeBoundId ") 
				.append("INNER JOIN exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id ")
				.append("WHERE  pf.sapid in ( :sapidList )  AND  pf.isResultLive = 'Y' order by pf.sapid, pss.sem, pss.subject ");

		//Create MapSqlParameterSource map and add query parameters values.
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapidList", sapidList);
		//Execute SQL using namedJdbcTemplate's query method.
		passfailList = namedJdbcTemplate.query(GET_PASSFAIL.toString(), queryParams,
				new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));

		//return students pass fail details.
		return passfailList;
	}

	@Transactional(readOnly = true)
	public Map<String, ExamResultsBean> getResultDeclarationsMap() throws SQLException {
		Map<String, ExamResultsBean> resultsMap = new HashMap<String, ExamResultsBean>();
		
		//Prepare SQL query.
		String SQL = "select acadYear,acadMonth,examYear,examMonth,resultDeclareDate from exam.exam_results";
		
		//Execute SQL Query.
		List<ExamResultsBean> examOrderList = jdbcTemplate.query(SQL, new BeanPropertyRowMapper<ExamResultsBean>(ExamResultsBean.class));
		
		//Prepare map
		for(ExamResultsBean resultsBean : examOrderList) {
			String str = new StringBuilder(resultsBean.getAcadMonth()).append(resultsBean.getAcadYear()).append("-")
					.append(resultsBean.getExamMonth()).append(resultsBean.getExamYear()).toString();
			
			resultsMap.put(str, resultsBean);
		}
			
		//return map
		return resultsMap;
	}
	
	@Transactional(readOnly = true)
	public int checkIfScheduleExist(String assessmentId,String examStartDateTime,String examEndDateTime)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select count(*) from exam.exams_schedule " 
				+ "where assessments_id = ? and exam_start_date_time= ? and exam_end_date_time= ? ";
		return (int)jdbcTemplate.queryForObject(sql, new Object []{assessmentId,examStartDateTime,examEndDateTime}, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public ExamsAssessmentsBean getExistingScheduleDetails(String assessmentId,String examStartDateTime,String examEndDateTime)
	{
			ExamsAssessmentsBean bean = new ExamsAssessmentsBean();
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select assessments_id,schedule_id, schedule_name, schedule_accessKey, schedule_accessUrl," 
					+ "schedule_status, exam_start_date_time, exam_end_date_time,reporting_start_date_time,reporting_finish_date_time, createdBy, lastModifiedBy, max_score from exam.exams_schedule "
					+ "where assessments_id = ? and exam_start_date_time= ? and exam_end_date_time= ? limit 1";
			bean=jdbcTemplate.queryForObject(sql, new Object[] {assessmentId,examStartDateTime,examEndDateTime}, new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class));
			return bean;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsers(final String timeBoundId)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId,st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag "+
		            "from exam.students st inner join lti.timebound_user_mapping tum on st.sapid=tum.userId where " + 
					"tum.timebound_subject_config_id= ? and tum.role in ('Student') and tum.userId not like '777777%' and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setInt(1, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersRetry(final String timeBoundId,final String scheduleAccessKey)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId," + 
					" st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					" from exam.students st inner join lti.timebound_user_mapping tum on st.sapid=tum.userId where " + 
					" tum.userid not in(select sapid from exam.exams_scheduleinfo_mettl where timebound_id= ? and acessKey= ?) and " + 
					" tum.timebound_subject_config_id= ? and tum.role in ('Student') and tum.userId not like '777777%' " + 
					" and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setInt(1, Integer.parseInt(timeBoundId));
					ps.setString(2, scheduleAccessKey);
					ps.setInt(3, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersForReExam(final String timeBoundId,final String examStartDateTime)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId,st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					"from exam.students st inner join exam.mba_wx_bookings booking on st.sapid=booking.sapid " +
					"inner join exam.mba_wx_slots slot "+ 
                    "on slot.slotId=booking.slotId  inner join exam.mba_wx_time_table tmt on tmt.timeTableId=slot.timetableId where tmt.examStartDateTime = ? and "+
					" booking.timeboundId= ? and booking.bookingStatus='Y' and booking.sapid not like '777777%' and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid) ";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setString(1,examStartDateTime);
					ps.setInt(2, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersForReExamRetry(final String timeBoundId,final String scheduleAccessKey,final String examStartDateTime)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId,st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					" from exam.students st inner join exam.mba_wx_bookings booking on st.sapid=booking.sapid " +
					" inner join exam.mba_wx_slots slot "+ 
                    " on slot.slotId=booking.slotId  inner join exam.mba_wx_time_table tmt on tmt.timeTableId=slot.timetableId where tmt.examStartDateTime = ? and "+
					" booking.sapid not in(select sapid from exam.exams_scheduleinfo_mettl where timebound_id= ? and acessKey= ?) " + 
					" and booking.timeboundId= ? and booking.bookingStatus='Y' and booking.sapid not like '777777%'" + 
					" and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setString(1, examStartDateTime);
					ps.setInt(2, Integer.parseInt(timeBoundId));
					ps.setString(3, scheduleAccessKey);
					ps.setInt(4, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = false)
	public void saveTimeBoundCandidateRegisteredInfo(String sapid,String scheduleAccessKey,int statusCode,String message,String createdBy,String url)
	{
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1="insert into exam.exams_candidates_register_mettl (`sapId`,`scheduleAccessKey`,`registrationStatus`,`registrationMessage`,`registeredUrl`,`createdBy`)"
					+ " values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,sapid,scheduleAccessKey,statusCode,message,url,createdBy);
			
			transactionManager.commit(status);
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	@Transactional (readOnly = false)
	public void saveCandidateRegisteredInfoAndScheduleInfo(MettlRegisterCandidateBean candidate,ExamsAssessmentsBean examBean,int statusCode,String message,String urlSuccess,String urlGeneral,String subjectName,String endTime)
	{
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql1="insert into exam.exams_candidates_register_mettl (`sapId`,`scheduleAccessKey`,`registrationStatus`,`registrationMessage`,`registeredUrl`,`createdBy`)"
					+ " values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql1,candidate.getSapId(),examBean.getSchedule_accessKey(),statusCode,message,urlSuccess,examBean.getCreatedBy());
			
			String sql2="insert into exam.exams_scheduleinfo_mettl (`subject`,`timebound_id`,`sapid`,`firstname`,`lastname`,`emailId`,`examStartDateTime`,`examEndDateTime`,`accessStartDateTime`,`accessEndDateTime`,"
					+ "`reporting_start_date_time`,`reporting_finish_date_time`,`assessmentName`,`assessmentDuration`,`schedule_id`,`scheduleName`,`acessKey`,`joinURL`,`max_score`,`createdBy`,`modifiedBy`) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql2, subjectName,examBean.getTimebound_id(),candidate.getSapId(),candidate.getFirstName(),candidate.getLastName(),candidate.getEmailAddress(),examBean.getExam_start_date_time(),endTime,examBean.getExam_start_date_time(),examBean.getExam_end_date_time(),examBean.getReporting_start_date_time(),examBean.getReporting_finish_date_time(),examBean.getName(),examBean.getDuration(),examBean.getSchedule_id(),examBean.getSchedule_name(),examBean.getSchedule_accessKey(),urlGeneral,examBean.getMax_score(),examBean.getCreatedBy(),examBean.getLastModifiedBy());			
			transactionManager.commit(status);
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getRegisteredStudentsCount(final String batchName,final String timeBoundId,final String scheduleAccessKey)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select sapid,firstname,lastname,emailId,subject,assessmentName,scheduleName,acessKey,examStartDateTime from exam.exams_scheduleinfo_mettl where timebound_id=? and acessKey=?";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setInt(1, Integer.parseInt(timeBoundId));
					ps.setString(2, scheduleAccessKey);
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstname"));
						bean.setLastName(rs.getString("lastname"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setSubject(rs.getString("subject"));
						bean.setAssessmentName(rs.getString("assessmentName"));
						bean.setScheduleName(rs.getString("scheduleName"));
						bean.setScheduleAccessKey(rs.getString("acessKey"));
						bean.setExamStartDateTime(rs.getString("examStartDateTime"));
						bean.setBatchName(batchName);
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	
	@Transactional(readOnly = true)
	public int getRegisteredStudentsCountForExcelUpload(String timeboundId,String scheduleAccessKey,ArrayList<MettlRegisterCandidateBean> userList)
	{
		String sapid="";
		String commaSeparatedSapid="";
		for(int i=0;i<=userList.size()-2;i++)
		{
			sapid=userList.get(i).getSapId();
			commaSeparatedSapid = commaSeparatedSapid + sapid + ",";
		}
		commaSeparatedSapid = commaSeparatedSapid + userList.get(userList.size()-1).getSapId();
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select count(sapid) from exam.exams_scheduleinfo_mettl where timebound_id=? and acessKey=? and sapid in ("+commaSeparatedSapid+")";
		return (int)jdbcTemplate.queryForObject(sql, new Object []{timeboundId,scheduleAccessKey}, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> fetchTodayExamStudents(final String todayDate,final ArrayList<Integer> consumerProgramStructureIdList)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try 
		{
			String cpsid="";
			String commaSeparatedCPSid="";
			for(int i=0;i<=consumerProgramStructureIdList.size()-2;i++)
			{
				cpsid=Integer.toString(consumerProgramStructureIdList.get(i));
				commaSeparatedCPSid = commaSeparatedCPSid + cpsid + ",";
			}
			commaSeparatedCPSid = commaSeparatedCPSid + consumerProgramStructureIdList.get(consumerProgramStructureIdList.size()-1);
			
			String sql="select sm.sapid,sm.emailId,sm.acessKey,sm.examStartDateTime from exam.exams_scheduleinfo_mettl sm inner join lti.student_subject_config ssc "+
				    "on ssc.id=sm.timebound_id inner join exam.batch b "+
				    "on b.id=ssc.batchId where sm.examStartDateTime like '"+todayDate+"%' and sm.testTaken is null and b.consumerProgramStructureId in ("+commaSeparatedCPSid+")";
			
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setScheduleAccessKey(rs.getString("acessKey"));
						bean.setExamStartDateTime(rs.getString("examStartDateTime"));
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			pullTimeBoundMettlMarksLogger.error("Error:"+e.getMessage());
			return null;
		}
	}
	
	public ArrayList<String> updateAttemptStatusForSchedule(ArrayList<MettlResponseBean> mettlResponseBeanList)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> errorList = new ArrayList<String>();
		for(MettlResponseBean bean:mettlResponseBeanList)
		{
			try
			{
				updateAttemptStatus(bean, jdbcTemplate);
			}
			catch(Exception e)
			{
				pullTimeBoundMettlMarksLogger.error(" Error while updating in db for Student Email:"+bean.getEmail()+",Access Key:"+bean.getSchedule_accessKey()+" Error:"+e.getMessage());
				errorList.add("Student Email: "+bean.getEmail()+" - Schedule Access Key: "+bean.getSchedule_accessKey());
			}
		}
		return errorList;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAttemptStatus(MettlResponseBean bean,JdbcTemplate jdbcTemplate)
	{
		String sql = "update exam.exams_scheduleinfo_mettl set "+
				"testTaken=?,modifiedBy=?, "+
				"modifiedDateTime=sysdate() " +
				"where sapid=? and acessKey=? and emailId=?";
		
		jdbcTemplate.update(sql, new Object[] {bean.getStatus(),bean.getLastModifiedBy(),bean.getSapid(),bean.getSchedule_accessKey(),bean.getEmail()});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean> getIAComponentOnlyEligibleStudentsForPassFail(Integer timeboundId) {
		String sql = " SELECT  " + 
				"    map.userId AS sapid, " + 
				"    pss.passScore, " + 
				"    pss.id AS prgm_sem_subj_id, " + 
				"    conf.id AS timebound_id " + 
				"FROM " + 
				"    lti.timebound_user_mapping map " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config conf ON conf.id = map.timebound_subject_config_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = conf.prgm_sem_subj_id " + 
				"WHERE " + 
				"    map.timebound_subject_config_id = ? "+
				"    AND map.userId NOT LIKE '777777%'  " + 
				"    AND map.role IN ('Student' , 'Resit') ";
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
		eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(sql, new Object[] {timeboundId}, new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		return eligibleList;
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsLiveForIAComponentSubject(Integer timeboundId){
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="update exam.mba_passfail set isResultLive ='Y' where timeboundId = ? ";
		parameters.add(timeboundId);
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = true)
	public List<Integer> getSessionPlanByTimebound(String timeboundId) {
		String query =  "SELECT sessionPlanId " + 
						"FROM acads.sessionplanid_timeboundid_mapping " + 
						"WHERE timeboundId = ?";
		
		return jdbcTemplate.query(query, new SingleColumnRowMapper(Integer.class), timeboundId);
	}
	
	@Transactional(readOnly = true)
	public Map<Integer, Integer> getApplicableTestBySessionPlan(List<Integer> sessionPlanIdList) {
		String query =  "SELECT t.id, t.maxScore " + 
						"FROM exam.test t " + 
						"INNER JOIN exam.test_live_settings tls" + 
						"	ON t.referenceId = tls.referenceId" + 
						"	AND t.year = tls.examYear" + 
						"	AND t.month = tls.examMonth" + 
						"   AND t.acadYear = tls.acadYear" + 
						"   AND t.acadMonth = tls.acadMonth " + 
						"INNER JOIN acads.sessionplan_module spm" + 
						"	ON tls.referenceId = spm.id " + 
						"WHERE spm.sessionPlanId IN (:sessionPlanIds)";
		
		return namedJdbcTemplate.query(query, new MapSqlParameterSource("sessionPlanIds", sessionPlanIdList), (ResultSet rs) ->  resultSetMapper(rs, Integer.class, Integer.class));
	}
	
	/**
	 * A ResultSet Mapper which returns a HashMap wherein the first ResultSet value is set as the Key, and second as it's Value 
	 * @param rs - ResultSet consisting of the result returned from the database query
	 * @param keyClass - Class to be set for Key
	 * @param valueClass - Class to be set for Value
	 * @return - HashMap containing the values from the ResultSet provided
	 * @throws SQLException
	 */
	private <K, V> HashMap<K, V> resultSetMapper(ResultSet rs, Class<K> keyClass, Class<V> valueClass) throws SQLException {
		HashMap<K, V> resultMap= new HashMap<>();
        while(rs.next()) {
        	resultMap.put(keyClass.cast(rs.getObject(1)), valueClass.cast(rs.getObject(2)));
        }
        
        return resultMap;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllScheduleBasedOnTimeBoundId(String timebound_id)
	{
		ArrayList<String> scheduleIdsArr =new ArrayList<String>();
		try
		{
			String sql="select schedule_id from exam.exams_schedule where timebound_id=? order by schedule_id";
			scheduleIdsArr=(ArrayList<String>)jdbcTemplate.query(sql, new Object[] {timebound_id}, new SingleColumnRowMapper<>(String.class));
		}
		catch(Exception e)
		{
			
		}
		return scheduleIdsArr;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchesList(String consumerProgramStructureId,int sem){
		ArrayList<BatchExamBean> batchList = new ArrayList<BatchExamBean>();
		try {
		

			String sql = "SELECT id,sem,name FROM exam.batch where consumerProgramStructureId in ("+consumerProgramStructureId+") and sem = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {
				sem
			},
					new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class));
	
			return batchList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return batchList;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getActiveBatchList(List<String> programList,String acadMonth, String acadYear){
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			
			queryParams.addValue("programList", programList);
			queryParams.addValue("acadMonth", acadMonth);
			queryParams.addValue("acadYear", acadYear);
			
			String sql="select b.id,b.name from exam.consumer_program_structure cps inner join exam.program p on p.id=cps.programId inner join exam.batch b on b.consumerProgramStructureId=cps.id where p.code in( :programList ) and b.acadMonth= :acadMonth and b.acadYear= :acadYear ";
			return (ArrayList<BatchExamBean>)namedJdbcTemplate.query(sql,queryParams,new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class));
	}
	
	public ArrayList<BatchExamBean> getBatchesForAcadMonthYear(String acadMonth,String acadYear){
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		queryParams.addValue("acadMonth", acadMonth);
		queryParams.addValue("acadYear", acadYear);
		
		String sql="select id,name,consumerProgramStructureId from exam.batch where acadMonth= :acadMonth and acadYear= :acadYear";
		return (ArrayList<BatchExamBean>) namedJdbcTemplate.query(sql,queryParams,new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getConsumerProgramStructureId(){
		
		String sql="select id,programId from exam.consumer_program_structure";
		return (ArrayList<ConsumerProgramStructureExam>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramsBean> getProgramsList(List<String> programList){
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		queryParams.addValue("programList", programList);
		
		String sql = "select id from exam.program where code in (:programList)";
		return (ArrayList<ProgramsBean>)namedJdbcTemplate.query(sql,queryParams,new BeanPropertyRowMapper<ProgramsBean>(ProgramsBean.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getPassFailResultForReport(TEEResultBean resultBean, String authorizedCenterCodes,String consumerProgramStructureId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> passFailRecords = new ArrayList<EmbaPassFailBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select mp.*, (COALESCE(mp.iaScore, 0) + COALESCE(mp.teeScore,0)) as total , ps.subject, sc.batchId as batch_id, b.name as batchName,cnt.lc as lc "
				+ " FROM exam.mba_passfail mp , lti.student_subject_config sc, exam.program_sem_subject ps, exam.batch b, exam.students s,exam.centers cnt"
				+ " where mp.timeboundId = sc.id"
				+ " and sc.prgm_sem_subj_id = ps.id "
				+ " and b.id = sc.batchId "
				+ " and s.sapid = mp.sapid and cnt.centerCode = s.centerCode ";

		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql+ " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and mp.schedule_id = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and sc.batchId in ( "+resultBean.getBatchId()+") ";
			//parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and mp.timeboundId = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		if(!StringUtils.isBlank(resultBean.getSapid()) ) {
			sql= sql+ " and mp.sapid = ?";
			parameters.add(resultBean.getSapid());
		}
		if(!StringUtils.isBlank(consumerProgramStructureId) ) {
			sql= sql+ " and b.consumerProgramStructureId in ("+consumerProgramStructureId+")";
		}
		if(!StringUtils.isBlank(resultBean.getExamMonth()) ) {
			sql= sql+ " and b.examMonth = ? ";
			parameters.add(resultBean.getExamMonth());
		}
		if(!StringUtils.isBlank(resultBean.getExamYear()) ) {
			sql= sql+ " and b.examYear = ? ";
			parameters.add(resultBean.getExamYear());
		}
		
		Object[] args = parameters.toArray();
		passFailRecords = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return passFailRecords;
	}

	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getPassFailResultForReport(TEEResultDTO resultBean, String authorizedCenterCodes,String consumerProgramStructureId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmbaPassFailBean> passFailRecords = new ArrayList<EmbaPassFailBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select mp.*, (COALESCE(mp.iaScore, 0) + COALESCE(mp.teeScore,0)) as total , ps.subject, sc.batchId as batch_id, b.name as batchName,cnt.lc as lc "
				+ " FROM exam.mba_passfail mp , lti.student_subject_config sc, exam.program_sem_subject ps, exam.batch b, exam.students s,exam.centers cnt"
				+ " where mp.timeboundId = sc.id"
				+ " and sc.prgm_sem_subj_id = ps.id "
				+ " and b.id = sc.batchId "
				+ " and s.sapid = mp.sapid and cnt.centerCode = s.centerCode ";

		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql+ " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and mp.schedule_id = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and sc.batchId in ( "+resultBean.getBatchId()+") ";
			//parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and mp.timeboundId = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		if(!StringUtils.isBlank(resultBean.getSapid()) ) {
			sql= sql+ " and mp.sapid = ?";
			parameters.add(resultBean.getSapid());
		}
		if(!StringUtils.isBlank(consumerProgramStructureId) ) {
			sql= sql+ " and b.consumerProgramStructureId in ("+consumerProgramStructureId+")";
		}
		if(!StringUtils.isBlank(resultBean.getExamMonth()) ) {
			sql= sql+ " and b.examMonth = ? ";
			parameters.add(resultBean.getExamMonth());
		}
		if(!StringUtils.isBlank(resultBean.getExamYear()) ) {
			sql= sql+ " and b.examYear = ? ";
			parameters.add(resultBean.getExamYear());
		}
		
		Object[] args = parameters.toArray();
		passFailRecords = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return passFailRecords;
	}

	
	@Transactional(readOnly = true)
	public List<String> getMsterKey(String consumerType,String programStructureId,String program){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " select id from exam.consumer_program_structure where 1=1 ";
		
		if (!StringUtils.isBlank(consumerType)) {
			sql = sql + " and consumerTypeId in ( " + consumerType + ") ";
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
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeByMsterKey(List<String> masterKeyList) {
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select distinct ct.id,ct.name from consumer_type ct,consumer_program_structure cps "
						+ " where ct.id=cps.consumerTypeId and cps.id in (:masterKeyList) ").toString();

		queryParams.addValue("masterKeyList", masterKeyList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
			sql, 
			queryParams,
			new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
		);  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramStructureByMsterKey(List<String> masterKeyList) {
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select distinct ps.id,ps.program_structure from program_structure ps,consumer_program_structure cps " + 
				" where ps.id=cps.programStructureId and cps.id in  (:masterKeyList) ").toString();
		
		queryParams.addValue("masterKeyList", masterKeyList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByMsterKey(List<String> masterKeyList) {
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select distinct p.id,p.code from program p,consumer_program_structure cps " + 
				" where p.id=cps.programId and cps.id in (:masterKeyList) ").toString();
		
		queryParams.addValue("masterKeyList", masterKeyList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramStructurByid(Set<String> programStructureIdList) throws Exception{
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select ps.id,ps.program_structure from program_structure ps " + 
				" where  ps.id in (:programStructureIdList)").toString();
		
		queryParams.addValue("programStructureIdList", programStructureIdList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeid(Set<String> ConsumerTypeIdList) throws Exception{
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select  ct.id,ct.name from consumer_type ct " + 
				" where  ct.id in (:ConsumerTypeIdList)").toString();
		
		queryParams.addValue("ConsumerTypeIdList", ConsumerTypeIdList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByid(Set<String> programIdList) throws Exception{
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select p.id,p.code from program p " + 
				" where  p.id in (:programIdList)").toString();
		
		queryParams.addValue("programIdList", programIdList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getMsterKeyDetailsByid(List<String> masterKeyList) throws Exception{
		
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("select programId,programStructureId,consumerTypeId from consumer_program_structure " + 
				" where id in (:masterKeyList) ").toString();
		
		queryParams.addValue("masterKeyList", masterKeyList);
		return (ArrayList<ConsumerProgramStructureExam>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
				);  
		
	}
	
	@Transactional(readOnly = true)
	public List<String> getMsterKeysByBatch(ConsumerProgramStructureExam consumerProgramStructure){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " select consumerProgramStructureId from exam.batch b where ";
		
		if(!StringUtils.isBlank(consumerProgramStructure.getExamMonth()) ) {
			sql= sql+ " b.examMonth = ? ";
			parameters.add(consumerProgramStructure.getExamMonth());
		}
		if(!StringUtils.isBlank(consumerProgramStructure.getExamYear()) && !StringUtils.isBlank(consumerProgramStructure.getExamMonth()) ) {
			sql= sql+ " and b.examYear = ? ";
			parameters.add(consumerProgramStructure.getExamYear());
		} else if (!StringUtils.isBlank(consumerProgramStructure.getExamYear())) {
			sql = sql + " b.examYear = ? ";
			parameters.add(consumerProgramStructure.getExamYear());
		}
		Object[] args = parameters.toArray();
		
		return  jdbcTemplate.queryForList(
				sql,
				args,
				String.class
				);  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getBatchesByMsterKey(ConsumerProgramStructureExam consumerProgramStructure,String masterKeys){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " SELECT b.id,b.name FROM exam.batch b where  b.consumerProgramStructureId in ( "
					+masterKeys+") ";
		
		if(!StringUtils.isBlank(consumerProgramStructure.getExamMonth()) ) {
			sql= sql+ " and b.examMonth = ? ";
			parameters.add(consumerProgramStructure.getExamMonth());
		}
		if(!StringUtils.isBlank(consumerProgramStructure.getExamYear())) {
			sql= sql+ " and b.examYear = ? ";
			parameters.add(consumerProgramStructure.getExamYear());
		} 
		Object[] args = parameters.toArray();		
		return (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(
			sql, 
			args,
			new BeanPropertyRowMapper<ConsumerProgramStructureExam>(ConsumerProgramStructureExam.class)
		);  
	}

	public void saveTimeBoundCandidateRegisteredInfoForMBAX(String sapId, String schedule_accessKey, int statusCode,
			String message, String createdBy, String  url) {
		
			TransactionDefinition def = new DefaultTransactionDefinition();
			TransactionStatus status = transactionManager.getTransaction(def);

			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql1="insert into exam.mbax_exams_candidates_register_mettl (`sapid`,`scheduleAccessKey`,`registrationStatus`,`registrationMessage`,`registeredUrl`,`createdBy`)"
						+ " values(?,?,?,?,?,?)";
				jdbcTemplate.update(sql1,sapId,schedule_accessKey,statusCode,message,url,createdBy);
				
				transactionManager.commit(status);
			}
			catch(Exception e)
			{
				logger.error("Error:"+e.getMessage());
				transactionManager.rollback(status);
				throw e;
			}
	}

	public void saveCandidateRegisteredInfoAndScheduleInfoForMBAX(MettlRegisterCandidateBean candidate,
			ExamsAssessmentsBean examBean, int statusCode, String message, String urlSuccess, String urlGeneral,
			String subjectName, String endTime) {
		
			TransactionDefinition def = new DefaultTransactionDefinition();
			TransactionStatus status = transactionManager.getTransaction(def);

			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql1="insert into exam.mbax_exams_candidates_register_mettl (`sapid`,`scheduleAccessKey`,`registrationStatus`,`registrationMessage`,`registeredUrl`,`createdBy`)"
						+ " values(?,?,?,?,?,?)";
				jdbcTemplate.update(sql1,candidate.getSapId(),examBean.getSchedule_accessKey(),statusCode,message,urlSuccess,examBean.getCreatedBy());
				
				String sql2="insert into exam.mbax_exams_scheduleinfo_mettl (`subject`,`timebound_id`,`sapid`,`firstname`,`lastname`,`emailId`,`examStartDateTime`,`examEndDateTime`,`accessStartDateTime`,`accessEndDateTime`,"
						+ "`reporting_start_date_time`,`reporting_finish_date_time`,`assessmentName`,`assessmentDuration`,`schedule_id`,`scheduleName`,`acessKey`,`joinURL`,`max_score`,`createdBy`,`modifiedBy`) "
						+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				jdbcTemplate.update(sql2, subjectName,examBean.getTimebound_id(),candidate.getSapId(),candidate.getFirstName(),candidate.getLastName(),candidate.getEmailAddress(),examBean.getExam_start_date_time(),examBean.getExam_end_date_time(),examBean.getExam_start_date_time(),examBean.getExam_end_date_time(),examBean.getReporting_start_date_time(),examBean.getReporting_finish_date_time(),examBean.getName(),examBean.getDuration(),examBean.getSchedule_id(),examBean.getSchedule_name(),examBean.getSchedule_accessKey(),urlGeneral,examBean.getMax_score(),examBean.getCreatedBy(),examBean.getLastModifiedBy());			
				transactionManager.commit(status);
			}
			catch(Exception e)
			{
				logger.error("Error:"+e.getMessage());
				transactionManager.rollback(status);
				throw e;
			}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getRegisteredStudentsCountForMBAX(final String batchName,final String timeBoundId,final String scheduleAccessKey)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select sapid,firstname,lastname,emailId,subject,assessmentName,scheduleName,acessKey,examStartDateTime from exam.mbax_exams_scheduleinfo_mettl where timebound_id=? and acessKey=?";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setInt(1, Integer.parseInt(timeBoundId));
					ps.setString(2, scheduleAccessKey);
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstname"));
						bean.setLastName(rs.getString("lastname"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setSubject(rs.getString("subject"));
						bean.setAssessmentName(rs.getString("assessmentName"));
						bean.setScheduleName(rs.getString("scheduleName"));
						bean.setScheduleAccessKey(rs.getString("acessKey"));
						bean.setExamStartDateTime(rs.getString("examStartDateTime"));
						bean.setBatchName(batchName);
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersForReExamForMBAX(final String timeBoundId,final String examStartDateTime)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId,st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					"from exam.students st inner join exam.mba_x_bookings booking on st.sapid=booking.sapid " +
					"inner join exam.mba_x_slots slot "+ 
                    "on slot.slotId=booking.slotId  inner join exam.mba_x_time_table tmt on tmt.timeTableId=slot.timeTableId where tmt.examStartDateTime = ? and "+
					" booking.timeboundId= ? and booking.bookingStatus='Y' and booking.sapid not like '777777%' and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid) ";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setString(1,examStartDateTime);
					ps.setInt(2, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public int getRegisteredStudentsCountForExcelUploadForMBAX(String timeboundId,String scheduleAccessKey,ArrayList<MettlRegisterCandidateBean> userList)
	{
		String sapid="";
		String commaSeparatedSapid="";
		for(int i=0;i<=userList.size()-2;i++)
		{
			sapid=userList.get(i).getSapId();
			commaSeparatedSapid = commaSeparatedSapid + sapid + ",";
		}
		commaSeparatedSapid = commaSeparatedSapid + userList.get(userList.size()-1).getSapId();
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select count(sapid) from exam.mbax_exams_scheduleinfo_mettl where timebound_id=? and acessKey=? and sapid in ("+commaSeparatedSapid+")";
		return (int)jdbcTemplate.queryForObject(sql, new Object []{timeboundId,scheduleAccessKey}, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersForReExamRetryForMBAX(final String timeBoundId,final String scheduleAccessKey,final String examStartDateTime)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId,st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					" from exam.students st inner join exam.mba_x_bookings booking on st.sapid=booking.sapid " +
					" inner join exam.mba_x_slots slot "+ 
                    " on slot.slotId=booking.slotId  inner join exam.mba_x_time_table tmt on tmt.timeTableId=slot.timetableId where tmt.examStartDateTime = ? and "+
					" booking.sapid not in(select sapid from exam.mbax_exams_scheduleinfo_mettl where timebound_id= ? and acessKey= ?) " + 
					" and booking.timeboundId= ? and booking.bookingStatus='Y' and booking.sapid not like '777777%'" + 
					" and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setString(1, examStartDateTime);
					ps.setInt(2, Integer.parseInt(timeBoundId));
					ps.setString(3, scheduleAccessKey);
					ps.setInt(4, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBean> getTimeBoundUsersRetryForMBAX(final String timeBoundId,final String scheduleAccessKey)
	{
		ArrayList<MettlRegisterCandidateBean> userList = null;
		try {
			String sql="select st.firstName,IFNULL(if(trim(st.lastName) = '','.',trim(st.lastName)), '.') as lastName,st.sapid,trim(st.emailId) as emailId," + 
					" st.imageUrl,IFNULL(st.imageUrl, '0') as openLinkFlag " + 
					" from exam.students st inner join lti.timebound_user_mapping tum on st.sapid=tum.userId where " + 
					" tum.userid not in(select sapid from exam.mbax_exams_scheduleinfo_mettl where timebound_id= ? and acessKey= ?) and " + 
					" tum.timebound_subject_config_id= ? and tum.role in ('Student') and tum.userId not like '777777%' " + 
					" and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
			userList=jdbcTemplate.execute(sql, new PreparedStatementCallback<ArrayList<MettlRegisterCandidateBean>>() {
				@Override
				public ArrayList<MettlRegisterCandidateBean> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
				{
					ps.setInt(1, Integer.parseInt(timeBoundId));
					ps.setString(2, scheduleAccessKey);
					ps.setInt(3, Integer.parseInt(timeBoundId));
					ResultSet rs = ps.executeQuery();
					
					ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
					MettlRegisterCandidateBean bean = null;
					while(rs.next())
					{
						bean = new MettlRegisterCandidateBean();
						bean.setSapId(rs.getString("sapid"));
						bean.setFirstName(rs.getString("firstName"));
						bean.setLastName(rs.getString("lastName"));
						bean.setEmailAddress(rs.getString("emailId"));
						bean.setCandidateImage(rs.getString("imageUrl"));
						bean.setRegistrationImage(rs.getString("imageUrl"));
						if("0".equals(rs.getString("openLinkFlag"))) {
							bean.setOpenLinkFlag(Boolean.TRUE);
						} else {
							bean.setOpenLinkFlag(Boolean.FALSE);
						}
						userList.add(bean);
					}
					return userList;
				}
			});
			return userList;
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			return null;
		}
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramExamBean> getPSSBySubjectForQ7Q8(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " select id,consumerProgramStructureId,subject as name from exam.program_sem_subject where subject in ('Masters Dissertation Part - I','Masters Dissertation Part - II') ";
		return  (ArrayList<ProgramExamBean>) jdbcTemplate.query(
				sql,
				new BeanPropertyRowMapper<ProgramExamBean>(ProgramExamBean.class)
				);  
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentSubjectConfigExamBean> getTimeboubdIdByPSSAndBatch(String pssIds,String batchIds,String examMonth,String ExamYear){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = " select  id,prgm_sem_subj_id,examYear,examMonth,batchId  from lti.student_subject_config where prgm_sem_subj_id in ( "
				+pssIds+") ";
		
		if(!StringUtils.isBlank(batchIds) ) {
			sql= sql+ " and batchId in ("
					+batchIds+ ") ";
			//parameters.add(batchIds);
		}
		
		if(!StringUtils.isBlank(examMonth) ) {
			sql= sql+ " and examMonth = ? ";
			parameters.add(examMonth);
		}
		if(!StringUtils.isBlank(ExamYear)) {
			sql= sql+ " and examYear = ? ";
			parameters.add(ExamYear);
		} 
		Object[] args = parameters.toArray();		
		return (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(
				sql, 
				args,
				new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
				);  
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DissertationResultDTO> getReportFromQ7ByTimeBoundId(List<String> timboundIdList , String sapid){
		//		jdbcTemplate = new JdbcTemplate(dataSource);
		 namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "	select  timeboundId,prgm_sem_subj_id,sapid,component_a_score,component_b_score,	"
				   + "	isPass,component_a_status,component_b_status,									"
				   + "	failReason,isResultLive															"
				   + "  from exam.mscaiml_md_q7_passfail where 1=1 										";
		
		// Create MapSqlParameterSource object
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
		// Adding parameters in SQL parameter map.
					if(!(timboundIdList.isEmpty()) && timboundIdList!=null) {
						sql+=" and timeboundId in (:timboundIdList) ";
					queryParams.addValue("timboundIdList", timboundIdList);
					}
					
					if(StringUtils.isNotEmpty(sapid))
						sql+=" and sapid= "+sapid;
					
		return (ArrayList<DissertationResultDTO>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<DissertationResultDTO>(DissertationResultDTO.class)
				);  
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DissertationResultDTO> getReportFromQ8ByTimeBoundId(List<String> timboundIdList, String sapid){
		//		jdbcTemplate = new JdbcTemplate(dataSource);
		 namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =  "	select  timeboundId,prgm_sem_subj_id,sapid,component_c_score,status as component_c_status,	"
					+ "	graceMarks,total,isPass,failReason,isResultLive												"
					+ " from exam.mscaiml_md_q8_passfail where 1=1													";
		// Create MapSqlParameterSource object
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
		// Adding parameters in SQL parameter map.
			if(!(timboundIdList.isEmpty()) && timboundIdList!=null) {
				sql+=" and timeboundId in (:timboundIdList) ";
			queryParams.addValue("timboundIdList", timboundIdList);
			}
			
			if(StringUtils.isNotEmpty(sapid)) 
				sql+=" and sapid= "+sapid;
			
		return (ArrayList<DissertationResultDTO>) namedJdbcTemplate.query(
			sql, 
			queryParams,
			new BeanPropertyRowMapper<DissertationResultDTO>(DissertationResultDTO.class)
			);   
			}
	
	@Transactional(readOnly = true)
	public ArrayList<DissertationResultDTO> getStudentInfoBySapId(List<Long> sapIdList){
		//		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =  "select sapid,firstName,lastName,centerName,centerCode,program 	"
					+ " from exam.students where sapid in (:sapIdList)			";
		// Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		// Adding parameters in SQL parameter map.
		queryParams.addValue("sapIdList", sapIdList);
		return (ArrayList<DissertationResultDTO>) namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<DissertationResultDTO>(DissertationResultDTO.class)
				);   
	}
	
	@Transactional(readOnly = true)
	public Map<String, String> getBatchIdAndBatchNameMap(List<Integer> batchIds){
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =  "select id as batchId,name as batchName,sem from exam.batch where id in (:batchIds)";
		// Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		// Adding parameters in SQL parameter map.
		queryParams.addValue("batchIds", batchIds);
		List<DissertationResultDTO> list =  namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<DissertationResultDTO>(DissertationResultDTO.class)
				);
		return list.stream().collect(Collectors.toMap(key->key.getBatchId(), value->value.getBatchName()+"~"+value.getSem()));
	}
	
	@Transactional(readOnly = true)
	public Map<String, DissertationResultDTO> getBatchIdAndBatchNameBeanMap(List<Integer> batchIds){
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =  "select id as batchId,name as batchName,sem from exam.batch where id in (:batchIds)";
		// Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		// Adding parameters in SQL parameter map.
		queryParams.addValue("batchIds", batchIds);
		List<DissertationResultDTO> list =  namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<DissertationResultDTO>(DissertationResultDTO.class)
				);
		return list.stream().collect(Collectors.toMap(key->key.getBatchId(),Function.identity()));
	}
	
	@Transactional(readOnly = true)
	public String getPssIdByTimeBoundId(String timeboundId){
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =   "	select prgm_sem_subj_id		    "
					 + "	from lti.student_subject_config	"
					 + "	where id= :timeboundId			";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("timeboundId", timeboundId);
		return namedJdbcTemplate.queryForObject(
				sql, 
				queryParams,
				String.class
				);
	}
	
	@Transactional(readOnly = true)
	public List<String> getPssIdByBatchId(String batchId){
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql =	"	select prgm_sem_subj_id		    "
				+ "	from lti.student_subject_config			"
				+ "	where batchId = :batchId				";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("batchId", batchId);
		return namedJdbcTemplate.queryForList(
				sql, 
				queryParams,
				String.class
				);
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getBatchIdByTimeBoundId(List<Integer> batchId) {
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="select id,batchId,prgm_sem_subj_id from lti.student_subject_config where id in (:batchId)";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("batchId", batchId);
		return namedJdbcTemplate.query(
				sql, 
				queryParams,
				new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
				);
	}
	
	
}
