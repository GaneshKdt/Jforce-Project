package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExamResultChecklistBean;
import com.nmims.beans.StudentExamBean;

@Repository("examResultChecklistDao")
public class ExamResultChecklistDao {
	
	public static final Logger logger = LoggerFactory.getLogger("examResultsChecklist");
	
	@Value("${PROJECT_APPLICABLE_PROGRAM_SEM_LIST}")
	private String PROJECT_APPLICABLE_PROGRAM_SEM_LIST;
	
	private final int LIST_PARTITION_SIZE = 1000;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	protected void startTransaction(String transactionName, boolean readOnly, int propagationBehaviour) {
		
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		transactionDefinition.setName(transactionName);
		transactionDefinition.setPropagationBehavior(propagationBehaviour);
		transactionDefinition.setReadOnly(readOnly);
		this.status = transactionManager.getTransaction(transactionDefinition);
		
	}
	
	private void start_db_write_transaction(String transactionName) {
		
		this.startTransaction(transactionName, Boolean.FALSE, TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	protected void endTransaction(boolean toCommit) {
		if(toCommit) 
			transactionManager.commit(this.status);
		 else 
			transactionManager.rollback(this.status);
		
		this.status = null;
	}

	@Transactional(readOnly = true)
	public int getExamBookedCount(String year, String month) {
		String sql = "SELECT COUNT(*) AS 'count' FROM `exam`.`exambookings` WHERE `booked` = 'Y' AND `centerId` <> '-1' "
				+ "AND `year` = ? AND `month` = ?";
		
		return jdbcTemplate.query(sql, new Object[] { year, month }, new ExamResultCountResultExtractor("count"));
	}
	
	@Transactional(readOnly = true)
	public int getProjectBookedCount(String year, String month) {
		String sql = "SELECT COUNT(*) AS 'count' FROM `exam`.`exambookings` WHERE `booked` = 'Y' AND `centerId` = '-1' "
				+ "AND `year` = ? AND `month` = ?";
		
		return jdbcTemplate.query(sql, new Object[] { year, month },new ExamResultCountResultExtractor("count"));
	}
	
	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getAssignmentApplicableRecords(String examYear, String examMonth) {
		
		String sql = "SELECT  " + 
				"    s.`sapid`, ps.`subject` " + 
				" FROM " + 
				"    exam.`students` s, " + 
				"    exam.`program_sem_subject` ps, " + 
				"    exam.`registration` r, " +
				"    exam.`examorder` eo " +	
				" WHERE " + 
				"    r.consumerProgramStructureId = ps.consumerProgramStructureId " + 
				"        AND s.sapid = r.sapid " +
				"        AND eo.year = r.year " +
				"        AND eo.acadMonth = r.month " + 
				"        AND r.sem = ps.sem " + 
				"        AND eo.year = ? " + 
				"        AND eo.month = ? " + 
				"        AND ps.subject NOT IN ('Design Thinking','Soft Skills for Managers','Start your Start up'," +
				"        'Employability Skills - II Tally','Project','Module 4 - Project') " +
//				"        AND (s.sapid, ps.subject) NOT IN  " + 
//				"        (SELECT sapid, subject FROM exam.assignmentsubmission WHERE year = ? AND month = ?) " + 
				"        AND ps.hasAssignment = 'Y' " + 
				"        AND (s.programStatus <=> NULL OR s.programStatus = '') " + 
				"        AND (s.programRemarks <=> NULL OR s.programRemarks = '') " 
//				+ 
//				"        AND (s.sapid , ps.subject) NOT IN (SELECT  sapid, subject " + 
//				"        FROM exam.passfail WHERE isPass = 'Y') AND (s.sapid, ps.subject) NOT IN " +
//				"        (SELECT sapid, subject FROM exam.exambookings WHERE booked = 'Y' AND centerId <> '-1'"
//				+ "			AND `year` = ? AND `month` = ? ) "
				;
		
		return jdbcTemplate.query(sql, new Object[] { examYear, examMonth
//				, examYear, examMonth,examYear,examMonth 
				},
				new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
	}
	
	public Set<ExamResultChecklistBean> getAllPassedRecords(){
		String sql = "SELECT `sapid`, `subject` FROM `exam`.`passfail` WHERE `isPass` = 'Y' ";
		
		return jdbcTemplate.query(sql, new ExamResultchecklistSetRowMapper());
	}
	
	public Set<ExamResultChecklistBean> getAssignmentSubmittedRecords(String examYear, String examMonth){
		String sql = "SELECT `sapid`, `subject` FROM `exam`.`assignmentsubmission` WHERE `year` = ? AND `month` = ? ";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth}, new ExamResultchecklistSetRowMapper());
	}
	
	@Transactional(readOnly = true)
	public int getOnlyAssignmentSubmittedCount(String examYear, String examMonth) {
		
		String sql = "SELECT  " + 
				"    COUNT(*) AS 'count' " + 
				" FROM " + 
				"    exam.assignmentsubmission " + 
				" WHERE " + 
				"    year = ? AND month = ? " + 
				"        AND status = 'Submitted' " + 
				"        AND subject NOT IN ('Design Thinking','Soft Skills for Managers','Start your Start up'," +
				"     'Employability Skills - II Tally') " + 
				"        AND (sapid , subject) NOT IN (SELECT  " + 
				"            sapid, subject " + 
				"        FROM " + 
				"            exam.exambookings " + 
				"        WHERE " + 
				"            booked = 'Y' AND centerId <> '-1' " + 
				"                AND year = ? " + 
				"                AND month = ?)";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth,examYear, examMonth},new ExamResultCountResultExtractor("count"));
	}

	@Transactional(readOnly = true)
	public int getProjectNotBookedCount(String examYear, String examMonth) {
		
		String sql = "  SELECT  " + 
				"    COUNT(*) AS 'count' " + 
				" FROM " + 
				"    `exam`.`registration` `reg` " + 
				"        INNER JOIN " + 
				"    `exam`.`examorder` `order` ON `reg`.`year` = `order`.`year` " + 
				"        AND `reg`.`month` = `order`.`acadMonth` " + 
				"        INNER JOIN " + 
				"    `exam`.`program_sem_subject` `pss` ON `pss`.`sem` = `reg`.`sem` " + 
				"        AND `reg`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` " + 
				" WHERE " + 
				"    `order`.`month` = ? " + 
				"        AND `order`.`year` = ? " + 
				"        AND `pss`.`subject` IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " + 
				"        AND (reg.sapid , pss.subject) NOT IN (SELECT  " + 
				"            sapid, subject " + 
				"        FROM " + 
				"            exam.exambookings " + 
				"        WHERE " + 
				"            booked = 'Y' AND centerId = '-1' AND year = ? AND month = ?)";
		
		return jdbcTemplate.query(sql, new Object[] {examMonth,examYear,examYear,examMonth},new ExamResultCountResultExtractor("count"));
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getStudentdata(String sapid) {
		String sql = "SELECT sapid, previousStudentId, isLateral, PrgmStructApplicable, programChanged, program "
				+ "  FROM `exam`.`students` WHERE `sapid` = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getExamBookingRecords(String year, String month) {
		String sql = "SELECT `sapid`, `subject` FROM `exam`.`exambookings` WHERE `booked` = 'Y' AND `centerId` <> '-1' AND `year` = ? AND `month` = ?";
		
		return jdbcTemplate.query(sql, new Object[] { year, month },
				new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getProjectBookingRecords(String year, String month) {
		String sql = "SELECT `sapid`, `subject` FROM `exam`.`exambookings` WHERE `booked` = 'Y' AND `centerId` = '-1' AND `year` = ? AND `month` = ?";
		
		return jdbcTemplate.query(sql, new Object[] { year, month },
				new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
	}

	@Transactional(readOnly = true)
	public ExamOrderExamBean getExamOrder(String examYear, String examMonth) {
		String sql = "SELECT * FROM `exam`.`examorder` WHERE `year` = ? AND `month` = ?";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { examYear, examMonth },
				new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public Set<ExamResultChecklistBean> getExistingBaseRecords(String examYear, String examMonth) {
		String sql = "SELECT `sapid`, `subject` FROM `exam`.`exam_result_checklist` WHERE `year` = ? AND `month` = ?";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth}, new ExamResultchecklistSetRowMapper());
	}

	public synchronized Integer insertExamChecklistRecords(List<ExamResultChecklistBean> checklistRecords,
			String examYear, String examMonth, String userId) {

		start_db_write_transaction("insertIntoExamResultsChecklist");

		boolean isSuccess = Boolean.FALSE;

		Integer batchInsertExamChecklistRecords = 0;

		try {
			batchInsertExamChecklistRecords = batchInsertExamChecklistRecords(checklistRecords,examYear,examMonth,userId);

			if (batchInsertExamChecklistRecords.intValue() == checklistRecords.size())
				isSuccess = Boolean.TRUE;
			else
				throw new RuntimeException("Mismatch while populating data expected count : " + checklistRecords.size()
						+ " actual count :  " + batchInsertExamChecklistRecords);

		} catch (RuntimeException e) {
			logger.info(Throwables.getStackTraceAsString(e));
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			logger.info("ERROR : while trying to batch insert into checklist table : {}",Throwables.getStackTraceAsString(e));
		}
		finally {
			endTransaction(isSuccess);
		}
		return batchInsertExamChecklistRecords;
	}
	
	public synchronized Integer batchInsertExamChecklistRecords(List<ExamResultChecklistBean> checklistRecords,
			String examYear, String examMonth, String userId) {
		
		
		List<List<ExamResultChecklistBean>> listInbatches = Lists.partition(checklistRecords, LIST_PARTITION_SIZE);
		
		int count = 0;
		
		String sql = "INSERT INTO exam.exam_result_checklist  " + 
				"	(sapid, subject, `year`, `month`, `category`, created_by, created_at, updated_by, updated_at) " + 
				" VALUES (?,?,?,?,?,?,SYSDATE(),?,SYSDATE())";
		
		for (int v = 0; v < listInbatches.size(); v++) {
			
			List<ExamResultChecklistBean> recordsToInsert = listInbatches.get(v);

			int[] updatedRowsCount = jdbcTemplate.batchUpdate(sql, new InsertChecklistBatchPreparedStatementSetter(recordsToInsert,
					examYear,examMonth,userId));
			
			count = count + Arrays.stream(updatedRowsCount).sum();
		}
		return new Integer(count);
	}
	
	class InsertChecklistBatchPreparedStatementSetter implements BatchPreparedStatementSetter{
		
		private List<ExamResultChecklistBean> checklistBeans;
		private String examYear;
		private String examMonth;
		private String userid;
		
		InsertChecklistBatchPreparedStatementSetter(List<ExamResultChecklistBean> checklistBeans){
			this.checklistBeans = checklistBeans;
		}
		
		InsertChecklistBatchPreparedStatementSetter(List<ExamResultChecklistBean> checklistBeans,String examYear,
				String examMonth ,String userId){
			this.checklistBeans = checklistBeans;
			this.examMonth = examMonth;
			this.examYear = examYear;
			this.userid = userId;
		}
		
		@Override
		public void setValues(PreparedStatement ps, int i) throws SQLException {
			int k = 1;
			ExamResultChecklistBean checklistBean = checklistBeans.get(i);
			ps.setString(k++, checklistBean.getSapid());
			ps.setString(k++, checklistBean.getSubject());
			ps.setString(k++, this.examYear);
			ps.setString(k++, this.examMonth);
			ps.setString(k++, checklistBean.getCategory());
			ps.setString(k++, userid);
			ps.setString(k++, userid);
		}

		@Override
		public int getBatchSize() {
			return checklistBeans.size();
		}
	}
	
	class ExamResultchecklistSetRowMapper implements ResultSetExtractor<Set<ExamResultChecklistBean>>{

		@Override
		public Set<ExamResultChecklistBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Set<ExamResultChecklistBean> mappedSetObjects = new HashSet<>(rs.getFetchSize());
			
			while(rs.next())
				mappedSetObjects.add(new ExamResultChecklistBean(rs.getString("sapid"), rs.getString("subject")));
			
			return mappedSetObjects;
		}
		
	}
	
	class ExamResultCountResultExtractor implements ResultSetExtractor<Integer>{

		private String columnName;
		
		ExamResultCountResultExtractor(String columnName){
			this.columnName = columnName;
		}
		
		@Override
		public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
			return rs.next() ? rs.getInt(columnName) : 0;
		}
		
	}

	@Transactional(readOnly = true)
	public Set<ExamResultChecklistBean> getApplicableStudentsForProject(String acadYear, String acadMonth) {
		
	String sql = "SELECT  " + 
			"   r.sapid, pss.subject " + 
			" FROM " + 
			"    exam.registration r " + 
			"        INNER JOIN " + 
			"    exam.program_sem_subject pss ON pss.sem = r.sem " + 
			"        AND pss.consumerProgramStructureId = r.consumerProgramStructureId " + 
			" WHERE " + 
			"    r.year = ? AND r.month = ? " + 
			"        AND pss.subject IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " + 
			"        AND r.sem IN ("+PROJECT_APPLICABLE_PROGRAM_SEM_LIST+") ";
	
	return jdbcTemplate.query(sql, new Object[] {acadYear, acadMonth}, new ExamResultchecklistSetRowMapper());
	}

	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getProjectSubmissionStudents(String examYear, String examMonth){
		String sql = "SELECT sapid, `subject`, `year`, `month` FROM `exam`.`projectsubmission` WHERE `year` = ? AND `month` = ? ";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth}, 
				new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
		
	}
	
	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getExemptedStudentsList(String examYear, String examMonth){
		String sql = "SELECT sapid, `subject`, `year`, `month` FROM `exam`.`examfeeexemptsubject` WHERE"
				+ " `subject` IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " AND `year` = ? AND `month` = ? ";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth}, 
				new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
		
	}
	
	@Transactional(readOnly = true)
	public List<ExamResultChecklistBean> getOnlyAssignmentSubmittedRecords(String examYear, String examMonth) {
		
		String sql = "SELECT  " + 
				"    sapid, subject" + 
				" FROM " + 
				"    exam.assignmentsubmission " + 
				" WHERE " + 
				"    year = ? AND month = ? " + 
				"        AND status = 'Submitted' " + 
				"        AND subject NOT IN ('Design Thinking','Soft Skills for Managers','Start your Start up'," +
				"     'Employability Skills - II Tally') " + 
				"        AND (sapid , subject) NOT IN (SELECT  " + 
				"            sapid, subject " + 
				"        FROM " + 
				"            exam.exambookings " + 
				"        WHERE " + 
				"            booked = 'Y' AND centerId <> '-1' " + 
				"                AND year = ? " + 
				"                AND month = ?)";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth,examYear, examMonth},new BeanPropertyRowMapper<ExamResultChecklistBean>(ExamResultChecklistBean.class));
	}


	public int getExamResultChecklistCount(String examYear, String examMonth) {
		String sql = "SELECT COUNT(*) as 'count' FROM `exam`.`exam_result_checklist` WHERE `year` = ? AND `month` = ? ";
		return jdbcTemplate.query(sql, new Object[] {examYear,examMonth}, new ExamResultCountResultExtractor("count") );
	}
	
	
}
	