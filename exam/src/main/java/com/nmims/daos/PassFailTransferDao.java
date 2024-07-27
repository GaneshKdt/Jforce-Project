package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.base.Throwables;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.dto.PGGraceMarksDTO;

/**
 * Transactional DAO Layer for Staging to pass fail transfer feature
 * @author swarup.rajpurohit.EX
 */
public class PassFailTransferDao extends BaseDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus transactionStatus;

	private final String TRANSFER_PASSFAIL_TRANSACTION = "transferPassFail";
	private final String BYSAPID = "BYSAPID";
	private final String BYGRNO = "BYGRNO";
	private final String GRNO_NOTAVAILABLE = "Not Available";
	
	private static final Logger logger = LoggerFactory.getLogger("pg-passfail-process");
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		baseDataSource = this.dataSource;
	}
	
	private void startTransaction(String transactionName, int propagation, boolean readOnly) {
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(propagation);
		transactionDefinition.setName(transactionName);
		transactionDefinition.setReadOnly(readOnly);
		this.transactionStatus = transactionManager.getTransaction(transactionDefinition);
	}
	
	private void endTransaction(boolean commitTransaction) {
		
		if (commitTransaction)
			this.transactionManager.commit(transactionStatus);
		else
			this.transactionManager.rollback(transactionStatus);

		transactionStatus = null;
	}
	
	public synchronized Integer transferFromStagingToPassFailTransactional(List<PassFailExamBean> studentsToTransfer,
			Set<String> existingRecords) {

		boolean isTransactionSuccess = Boolean.FALSE;
		int toTransferCount = studentsToTransfer.size();
		int returnCount = 0;

		logger.info("inside tranfer transactional logic now with size of students to transfer : {}", toTransferCount);
		
		startTransaction(TRANSFER_PASSFAIL_TRANSACTION, TransactionDefinition.PROPAGATION_REQUIRED, Boolean.FALSE);
		try {

			Integer upsertedData = upsertDataInPassFail(studentsToTransfer, existingRecords);

			if (upsertedData != toTransferCount)
				
				throw new RuntimeException("UPSERTED RECORD COUNT " + upsertedData
						+ " DOES NOT MATCH TOTAL STUDENTS TO TRANSFER COUNT " + toTransferCount);

			Integer deletedRecords = batchDeleteFromPassFailStaging(studentsToTransfer);
			
			if(deletedRecords != toTransferCount)
				
				throw new RuntimeException("DELETED RECORD COUNT " + upsertedData
						+ " DOES NOT MATCH TOTAL STUDENTS TO TRANSFER COUNT " + toTransferCount);
			
			returnCount = upsertedData;
			isTransactionSuccess = Boolean.TRUE;
			
		} finally {
			endTransaction(isTransactionSuccess);
		}
		

		return returnCount;
	}
	
	public synchronized Integer batchDeleteFromPassFailStaging(List<PassFailExamBean> studentsToTransfer) {

		int numberOfRowsAffected = 0;
		
		String sql = "DELETE FROM `exam`.`passfail_staging` WHERE `sapid` = ? AND `subject` = ?";
		
		try {

			int[] batchUpdateResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, studentsToTransfer.get(i).getSapid());
					ps.setString(2, studentsToTransfer.get(i).getSubject());
				}

				@Override
				public int getBatchSize() {
					return studentsToTransfer.size();
				}

			});
			
			numberOfRowsAffected =Arrays.stream(batchUpdateResult).sum();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return numberOfRowsAffected;
	}
	
	public synchronized Integer upsertDataInPassFail(List<PassFailExamBean> studentsToTransfer, Set<String> existingRecords) {
		
		List<PassFailExamBean> toUpdateRecords = new ArrayList<>();
		List<PassFailExamBean> toInsertRecords = new ArrayList<>();
		
		studentsToTransfer.stream().forEach(k -> {
			String key = k.getSapid().trim() + k.getSubject().trim();
			if(existingRecords.contains(key))
				toUpdateRecords.add(k);
			else toInsertRecords.add(k);
		});
		
		logger.info("Total numbers of record to update : {}", toUpdateRecords.size());
		logger.info("Total numbers of record to insert : {}", toInsertRecords.size());
		
		Integer updatePassfailRecords = updatePassfailRecords(toUpdateRecords);
		Integer insertPassFailBatch = insertPassFailBatch(toInsertRecords);
		logger.info("total number of records inserted : {}", insertPassFailBatch);
		logger.info("total number of records updated : {}", updatePassfailRecords);
		return updatePassfailRecords + insertPassFailBatch;
		
	}
	
	private Integer updatePassfailRecords(List<PassFailExamBean> toUpdateRecords) {
		
		List<PassFailExamBean> updateBySapid = new ArrayList<>();
		List<PassFailExamBean> updateByGrno = new ArrayList<>();
		
		
		toUpdateRecords.stream().forEach(k -> {
			if(GRNO_NOTAVAILABLE.equalsIgnoreCase(k.getGrno())) 
				updateBySapid.add(k);
			else updateByGrno.add(k);
		});
		logger.info("number of records to update by sapid : {}", updateBySapid.size());
		logger.info("number of records to update by grno : {}", updateByGrno.size());
		Integer updatedBySapid = updatePassFailBatch(updateBySapid, BYSAPID);
		Integer updatedByGrNo = updatePassFailBatch(updateByGrno, BYGRNO);
		logger.info("number of records updated by sapid : {}", updatedBySapid);
		logger.info("number of records updated by grno : {}", updatedByGrNo);
		return updatedByGrNo + updatedBySapid;
		
	}

	public synchronized Integer updatePassFailBatch(List<PassFailExamBean> studentList, final String type){
		final int batchSize = 1000;
		
		int integerToReturn = 0;
		
		String sql = "Update exam.passfail set "
				+ " grno = ?,"
				+ " writtenYear = ?,"
				+ " writtenMonth = ?,"
				+ " assignmentYear = ?,"
				+ " assignmentMonth = ?,"
				+ " name = ?,"
				+ " program = ?,"
				+ " sem = ?,"
				+ " writtenscore = ?,"
				+ " assignmentscore = ?,"
				+ " total = ?,"
				+ " failReason = ?,"
				+ " remarks = ?,"
				+ " isPass = ?,"
				+ " gracemarks = ?,"
				+ " subjectCutoff = ?,"
				+ " studentType = ?,"
				+ " resultProcessedYear = ?,"
				+ " resultProcessedMonth = ? ";

		if(BYGRNO.equals(type)){
			sql = sql + " where  grno = ? and subject = ?";
		}else{
			sql = sql + " where  sapid = ? and subject = ?";
		}


		for (int j = 0; j < studentList.size(); j += batchSize) {

			final List<PassFailExamBean> batchList = studentList.subList(j,
					j + batchSize > studentList.size() ? studentList.size() : j + batchSize);
			int[] numberOfRowsAffected = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PassFailExamBean bean = batchList.get(i);
					ps.setString(1, bean.getGrno());
					ps.setString(2, bean.getWrittenYear());
					ps.setString(3, bean.getWrittenMonth());
					ps.setString(4, bean.getAssignmentYear());
					ps.setString(5, bean.getAssignmentMonth());
					ps.setString(6, bean.getName());
					ps.setString(7, bean.getProgram());
					ps.setString(8, bean.getSem());
					ps.setString(9, bean.getWrittenscore());
					ps.setString(10, bean.getAssignmentscore());
					ps.setString(11, bean.getTotal());
					ps.setString(12, bean.getFailReason());
					ps.setString(13, bean.getRemarks());
					ps.setString(14, bean.getIsPass());
					ps.setString(15, bean.getGracemarks());
					ps.setString(16, bean.getSubjectCutoffCleared());
					ps.setString(17, bean.getStudentType());
					ps.setString(18, bean.getResultProcessedYear());
					ps.setString(19, bean.getResultProcessedMonth());

					if (BYGRNO.equals(type)) {
						ps.setString(20, bean.getGrno());
					} else {
						ps.setString(20, bean.getSapid());
					}
					ps.setString(21, bean.getSubject());
				}

				public int getBatchSize() {
					return batchList.size();
				}

			});

			integerToReturn = integerToReturn + Arrays.stream(numberOfRowsAffected).sum();
		}
		
		return integerToReturn;
	}
	
	public synchronized Integer insertPassFailBatch(List<PassFailExamBean> studentList){
		int toReturnCount = 0;
		final int batchSize = 1000;
		final String sql = " INSERT INTO exam.passfail "
				+ "(sapid,"
				+ "subject,"
				+ "grno,"
				+ "writtenYear,"
				+ "writtenMonth,"
				+ "assignmentYear,"
				+ "assignmentMonth,"
				+ "name,"
				+ "program,"
				+ "sem,"
				+ "writtenscore,"
				+ "assignmentscore,"
				+ "total,"
				+ "failReason,"
				+ "remarks,"
				+ "isPass,"
				+ "gracemarks,"
				+ "subjectCutoff,"
				+ "studentType,"
				+ "resultProcessedYear,"
				+ "resultProcessedMonth)"

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


		for (int j = 0; j < studentList.size(); j += batchSize) {
			final List<PassFailExamBean> batchList = studentList.subList(j,j + batchSize > studentList.size() 
					? studentList.size() : j + batchSize);
			
			int[] batchUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PassFailExamBean m = batchList.get(i);
					ps.setString(1, m.getSapid());
					ps.setString(2, m.getSubject());
					ps.setString(3, m.getGrno());
					ps.setString(4, m.getWrittenYear());
					ps.setString(5, m.getWrittenMonth());
					ps.setString(6, m.getAssignmentYear());
					ps.setString(7, m.getAssignmentMonth());
					ps.setString(8, m.getName());
					ps.setString(9, m.getProgram());
					ps.setString(10, m.getSem());
					ps.setString(11, m.getWrittenscore());
					ps.setString(12, m.getAssignmentscore());
					ps.setString(13, m.getTotal());
					ps.setString(14, m.getFailReason());
					ps.setString(15, m.getRemarks());
					ps.setString(16, m.getIsPass());
					ps.setString(17, m.getGracemarks());
					ps.setString(18, m.getSubjectCutoffCleared());
					ps.setString(19, m.getStudentType());
					ps.setString(20, m.getResultProcessedYear());
					ps.setString(21, m.getResultProcessedMonth());
				}

				public int getBatchSize() {
					return batchList.size();
				}
			});

			toReturnCount = toReturnCount + Arrays.stream(batchUpdateResults).sum();
		}
		
		return toReturnCount;
		
	}

	public Integer getProjectCount(String year, String month) {
		
		String sql = "SELECT COUNT(*) as 'count' FROM `exam`.`passfail_staging` WHERE `subject` in ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " AND `resultProcessedYear` = ? AND `resultProcessedMonth` = ?";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getTEEAbsentStudentsCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally', " + 
				"        'Start your Start up', " + 
				"        'Design Thinking') " + 
				"        AND writtenscore = 'AB'";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getProjectAbsentStudentsCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 	
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social') " + 
				"        AND writtenscore = 'AB'";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getNVRIARecordsCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND writtenscore IN ('NV' , 'RIA')";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getAllTransferCount(String year, String month) {
		String sql = "SELECT COUNT(*)  FROM exam.passfail_staging WHERE resultProcessedYear = ? and resultProcessedMonth = ? "
				+ " AND subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getProjectNotBookedCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social') " + 
				"        AND writtenscore = ''";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {year, month}, Integer.class);
	}

	public Integer getWrittenScoreCount(String year, String month) {
	String sql = "SELECT  " + 
			"    COUNT(*) " + 
			" FROM " + 
			"    exam.passfail_staging ps " + 
			" WHERE " + 
			"    resultProcessedYear = ? " + 
			"        AND resultProcessedMonth = ? " + 
			"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
			"        'Simulation: Mimic Pro', " + 
			"        'Simulation: Mimic Social', " + 
			"        'Soft Skills for Managers', " + 
			"        'Employability Skills - II Tally', " + 
			"        'Start your Start up', " + 
			"        'Design Thinking') " + 
			"        AND writtenscore NOT IN ('AB' , '#CC', 'NV', 'RIA', 'WH','') " + 
			"        AND writtenscore IS NOT NULL ";
	
	return jdbcTemplate.queryForObject(sql, new Object[] { year, month }, Integer.class);
	}

	public Integer getAssignmentSubmittedCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND assignmentscore IS NOT NULL " + 
				"        AND assignmentscore <> '' " + 
				"        AND assignmentscore <> 'ANS' ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { year, month }, Integer.class);
	}

	public Integer getAssignmentANSCount(String year, String month) {
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND assignmentscore = 'ANS' " + 
				"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally', " + 
				"        'Start your Start up', " + 
				"        'Design Thinking') ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { year, month }, Integer.class);
	}
	
	public List<PassFailExamBean> getProjectRecords(String year, String month) {
		
		String sql = "SELECT *  FROM `exam`.`passfail_staging` WHERE `subject` in ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')"
				+ " AND `resultProcessedYear` = ? AND `resultProcessedMonth` = ?";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getTEEAbsentStudents(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally', " + 
				"        'Start your Start up', " + 
				"        'Design Thinking') " + 
				"        AND writtenscore = 'AB'";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getProjectAbsentStudents(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 	
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social') " + 
				"        AND writtenscore = 'AB'";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getNVRIARecords(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND writtenscore IN ('NV' , 'RIA')";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getAllTransferRecords(String year, String month) {
		String sql = "SELECT *  FROM exam.passfail_staging WHERE resultProcessedYear = ? and resultProcessedMonth = ? "
				+ " AND subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getProjectNotBookedStudents(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social') " + 
				"        AND writtenscore = ''";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getWrittenScoreRecords(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally', " + 
				"        'Start your Start up', " + 
				"        'Design Thinking') " + 
				"        AND writtenscore NOT IN ('AB' , '#CC', 'NV', 'RIA', 'WH','') " + 
				"        AND writtenscore IS NOT NULL ";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getAssignmentSubmittedRecords(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND assignmentscore IS NOT NULL " + 
				"        AND assignmentscore <> '' " + 
				"        AND assignmentscore <> 'ANS' ";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	public List<PassFailExamBean> getAssignmentANSRecords(String year, String month) {
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedYear = ? " + 
				"        AND resultProcessedMonth = ? " + 
				"        AND assignmentscore = 'ANS' " + 
				"        AND subject NOT IN ('Project' , 'Module 4 - Project', " + 
				"        'Simulation: Mimic Pro', " + 
				"        'Simulation: Mimic Social', " + 
				"        'Soft Skills for Managers', " + 
				"        'Employability Skills - II Tally', " + 
				"        'Start your Start up', " + 
				"        'Design Thinking') ";
		
		return jdbcTemplate.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}

	@Transactional(readOnly = false)
	public List<PGGraceMarksDTO> getValidityEndApplicableRecords(String examYear, String examMonth, int graceMarks){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " SELECT  " + 
				" pf.sapid, " + 
				"    st.consumerType, " + 
				"    st.PrgmStructApplicable, " + 
				"    pf.program, " + 
				"    CAST(pf.total AS UNSIGNED) AS total, " + 
				"    SUM(50 - CAST(pf.total AS UNSIGNED)) AS gracemarks, " + 
				"    pf.remarks, " + 
				"    p.noOfSubjectsToClear, " + 
				"    p.noOfSubjectsToClearLateral, " + 
				"    pfs.subject_count AS subjects, " + 
				"    p.noOfSubjectsToClearSem, " + 
				"    st.isLateral" + 
				" FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    exam.students st ON st.sapid = pf.sapid " + 
				"        INNER JOIN " + 
				"    exam.programs p ON p.consumerProgramStructureId = st.consumerProgramStructureId " + 
				"        AND p.program = st.program " + 
				"        INNER JOIN " + 
				"    (SELECT  sapid, COUNT(DISTINCT subject) AS subject_count  " + 
				"    FROM exam.passfail GROUP BY sapid) pfs ON pf.sapid = pfs.sapid " + 
				" WHERE pf.ispass = 'N' AND pf.total < 50 " + 
//				"        AND STR_TO_DATE(CONCAT(st.validityEndYear,'-',st.validityEndMonth),'%Y-%b') < STR_TO_DATE(CURRENT_DATE(), '%Y-%m') " + 
//				"        AND STR_TO_DATE(CONCAT(st.validityEndYear,'-',st.validityEndMonth),'%Y-%b') >= STR_TO_DATE(CONCAT(?, '-', ?), '%Y-%b') " + 
				" AND STR_TO_DATE(CONCAT(st.validityEndYear,'-',st.validityEndMonth),'%Y-%b') <= DATE_ADD(STR_TO_DATE(CONCAT(?, '-', ?,'-','01'), '%Y-%b-%d'), INTERVAL 60 DAY) " + 
				" GROUP BY pf.sapid " + 
				" HAVING gracemarks <= ? " + 
				"    AND pf.sapid IN (SELECT DISTINCT sapid FROM exam.marks " + 
				"    WHERE month = ? AND year = ? AND (writenscore REGEXP '^[+-]?[0-9]*([0-9].|[0-9]|.[0-9])[0-9]*(e[+-]?[0-9]+)?$' " + 
				"            OR assignmentscore REGEXP '^[+-]?[0-9]*([0-9].|[0-9]|.[0-9])[0-9]*(e[+-]?[0-9]+)?$')) " + 
				"    AND pfs.subject_count =  " + 
				"    (CASE WHEN st.isLateral = 'Y' THEN p.noOfSubjectsToClearLateral " + 
				"    ELSE p.noOfSubjectsToClear END) ";
		
		return jdbcTemplate.query(sql,
				new Object[] {examYear, examMonth, graceMarks, examMonth, examYear}, 
				new BeanPropertyRowMapper<PGGraceMarksDTO>(PGGraceMarksDTO.class));
	}
	
	public List<PassFailExamBean> getGraceAppliedRecords(String year, String month) {
		
		String sql = "SELECT  " + 
				"    * " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedMonth = ? " + 
				"        AND resultProcessedYear = ? " + 
				"        AND gracemarks > 0";
		
		return jdbcTemplate.query(sql, new Object[] {month, year}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}

	public Integer getGraceMarksCount(String year, String month) {
		
		String sql = "SELECT  " + 
				"    COUNT(*) " + 
				" FROM " + 
				"    exam.passfail_staging ps " + 
				" WHERE " + 
				"    resultProcessedMonth = ? " + 
				"        AND resultProcessedYear = ? " + 
				"        AND gracemarks > 0";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {month, year}, Integer.class);
	}

}

