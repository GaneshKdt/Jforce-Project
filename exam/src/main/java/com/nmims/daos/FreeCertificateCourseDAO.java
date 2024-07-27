package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.nmims.beans.ProgramSubjectMappingExamBean;

@Component
public class FreeCertificateCourseDAO extends BaseDAO{
	
	@Autowired
	ApplicationContext act;

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Value( "${LEAD_CURRENT_ACAD_YEAR}" )
	private String LEAD_CURRENT_ACAD_YEAR;
	
	@Value( "${LEAD_CURRENT_ACAD_MONTH}" )
	private String LEAD_CURRENT_ACAD_MONTH;
	
	@Value( "${LEAD_CURRENT_EXAM_YEAR}" )
	private String LEAD_CURRENT_EXAM_YEAR;
	
	@Value( "${LEAD_CURRENT_EXAM_MONTH}" )
	private String LEAD_CURRENT_EXAM_MONTH;

	public boolean checkIfStudentEnrolledForProgram(String leadId, String consumerProgramStructureId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from lead.leads_master_key_mapping where leads_id = ? AND consumerProgramStructureId = ?";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] { leadId, consumerProgramStructureId }, 
			Integer.class
		) > 0;
	}

	public String getTestIdForProgram(int pssId, String examMonth, String examYear, String acadMonth, String acadYear) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT testId "
				+ " FROM lead.leads_test t "
				+ " INNER JOIN lead.leads_test_testid_configuration_mapping tcm ON t.id = tcm.testId "
				+ " INNER JOIN lead.leads_test_live_settings tls ON tcm.referenceId = tls.referenceId "
				+ " WHERE "
					+ " t.showResultsToStudents = 'Y' "
				+ " AND tls.acadYear = t.acadYear "
				+ " AND tls.acadMonth = t.acadMonth "
				+ " AND tls.examYear = t.year "
				+ " AND tls.examMonth = t.month "
				+ " AND tls.acadYear = ? "
				+ " AND tls.acadMonth = ? "
				+ " AND tls.examYear = ? "
				+ " AND tls.examMonth = ? "
				+ " AND tls.referenceId = ? ";
		
		String testId = jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				acadYear, acadMonth, 
				examYear, examMonth, 
				pssId
			}, 
			String.class
		);
		return testId;
	}
	
	public int getNumberOfTestsTakenByStudents(String quizId, String leadId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM lead.leads_test_student_testdetails tsd "
				+ " WHERE testId = ? AND sapid = ? AND tsd.testCompleted = 'Y'  AND tsd.testCompleted = 'Y' ";
		
		try {
			int attempts = jdbcTemplate.queryForObject(
				sql, 
				new Object[] {
						quizId, leadId
				}, 
				Integer.class
			);
			return attempts;
		}catch (Exception e) {
			
			return 0;
		}
	}

	public boolean getTestStatusForStudent(String quizId, String leadId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM lead.leads_test_student_testdetails tsd "
				+ " LEFT JOIN lead.leads_test t ON tsd.testId = t.id "
				+ " WHERE tsd.testId = ? AND tsd.sapid = ? AND tsd.testCompleted = 'Y'  AND tsd.showResult = 'Y' "
				+ " AND tsd.score >= t.passScore";
		
		try {
			int attempts = jdbcTemplate.queryForObject(
				sql, 
				new Object[] {
					quizId, leadId
				}, 
				Integer.class
			);
			return attempts > 0;
		}catch (Exception e) {
			
			return false;
		}
	}
	
	/**
	 * This method getting the completion date test 
	 * @param quizId	
	 * @param leadId
	 * @return String 	This returns the test completion date only if test completed and result shown
	 * @author 
	 */
	public String getCompletionDate(String quizId, String leadId){
		String GET_COMPLETION_DATE=null;
		StringBuilder sb=null;
		String complectionDate=null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		sb=new StringBuilder();
		
		//SQL query Creation
		sb.append("SELECT max(testEndedOn) ");
		sb.append("FROM lead.leads_test_student_testdetails tsd ");
		sb.append("LEFT JOIN lead.leads_test t ON tsd.testId = t.id ");
		sb.append("WHERE tsd.testId = ? AND tsd.sapid = ? AND tsd.testCompleted = 'Y'  AND tsd.showResult = 'Y' ");
		
		//Converting to string
		GET_COMPLETION_DATE=sb.toString();
		
		try {
			complectionDate=jdbcTemplate.queryForObject(GET_COMPLETION_DATE,new Object[] {quizId, leadId},String.class);
			//Returning complectionDate
			return complectionDate;
		}
		catch (Exception e) {
			
		}
		return complectionDate;
	}

	public ProgramSubjectMappingExamBean getLastSemSubjectForProgram(String consumerProgramStructureId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM exam.program_sem_subject "
				+ " WHERE (consumerProgramStructureId, sem) IN ( "
					+ " SELECT consumerProgramStructureId, MAX(sem) "
					+ " FROM exam.program_sem_subject "
					+ " WHERE consumerProgramStructureId = ? "
					+ " GROUP BY consumerProgramStructureId"
				+ " ) ";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] { consumerProgramStructureId }, 
			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
		);
	}

	public String getProgramName(String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `name` FROM `exam`.`consumer_program_structure` `cps`  "
				+ " INNER JOIN `exam`.`program` `p` ON `p`.`id` = `cps`.`programId`  "
				+ " WHERE `cps`.`id` = ? ";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] { consumerProgramStructureId }, 
			String.class
		);
	}
}
