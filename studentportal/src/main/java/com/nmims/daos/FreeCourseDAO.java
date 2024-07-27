package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.beans.leadsProgramMapping;

@Repository("FreeCourseDAO")
public class FreeCourseDAO extends BaseDAO{
	
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
	


	@Transactional(readOnly = true)
public List<ProgramsStudentPortalBean> getFreeCourseList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.programs where programType = 'Modular Program' and active='Y'";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProgramsStudentPortalBean>(ProgramsStudentPortalBean.class));
	}
	

	@Transactional(readOnly = true)
	public List<String> getEnrolledCourseList(leadsProgramMapping leadsProgramMapping){
		jdbcTemplate = new JdbcTemplate(dataSource);
		//String sql = "select programs_id from lead.leads_program_mapping where leads_id = ? and accessTillDate = ?";
		String sql = "select consumerProgramStructureId from lead.leads_master_key_mapping where leads_id = ?";
		return jdbcTemplate.queryForList(sql, new Object[] {leadsProgramMapping.getLeads_id()}, String.class);
	}
	

	@Transactional(readOnly = false)
	public String insertIntoLeadsMasterKeyMapping(leadsProgramMapping leadsProgramMapping) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "insert into lead.leads_master_key_mapping (`leads_id`,`consumerProgramStructureId`,`accessTillDate`) values(?,?,?)";
			int resultCount = jdbcTemplate.update(sql,new Object[] {leadsProgramMapping.getLeads_id(),leadsProgramMapping.getConsumerProgramStructureId(),leadsProgramMapping.getAccessTillDate()});
			if(resultCount > 0) {
				return "true";
			}
			return "false";
		}
		catch (Exception e) {
			return e.getMessage();
		}
		
	}
	
//	public String insertIntoLeadsRegistration(leadsProgramMapping leadsProgramMapping) {
//		try {
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql = "insert into lead.leads_registration (`leads_id`,`consumerProgramStructureId`,`sem`) values(?,?,?)";
//			int resultCount = jdbcTemplate.update(sql,new Object[] {leadsProgramMapping.getLeads_id(),leadsProgramMapping.getConsumerProgramStructureId(),"1"});
//			if(resultCount > 0) {
//				return "true";
//			}
//			return "false";
//		}
//		catch (Exception e) {
//			return e.getMessage();
//		}
//		
//	}
//	

	
	@Transactional(readOnly = true)
public LeadStudentPortalBean getLeadBean(leadsProgramMapping leadsProgramMapping) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `lead`.`leads` where leadId=? limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {leadsProgramMapping.getLeads_id()},new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		}catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
//	public List<String> getSemRegistration(leadsProgramMapping leadsProgramMapping) {
//		try {
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql = "select sem from `lead`.`leads_registration` where leads_id =? and consumerProgramStructureId=? order by sem asc";
//			return jdbcTemplate.query(sql, new Object[] {leadsProgramMapping.getLeads_id(),leadsProgramMapping.getConsumerProgramStructureId()}, new SingleColumnRowMapper<String>(String.class));
//		}catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}


	@Transactional(readOnly = true)
public List<ProgramSubjectMappingStudentPortalBean> getSubjectList(leadsProgramMapping leadsProgramMapping){

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.program_sem_subject where consumerProgramStructureId = ? and active='Y' order by sem asc";
			return jdbcTemplate.query(sql, new Object[] {leadsProgramMapping.getConsumerProgramStructureId()}, new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class));
		}catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}


	@Transactional(readOnly = true)
public TestStudentPortalBean getTestForProgramAndStudent(int pssId, String leadId, String examMonth, String examYear, String acadMonth, String acadYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT t.*, max(attempt) AS `attempt`, max(score) AS `score` "
				+ " FROM lead.leads_test t "
				+ " INNER JOIN lead.leads_test_testid_configuration_mapping tcm ON t.id = tcm.testId "
				+ " INNER JOIN lead.leads_test_live_settings tls ON tcm.referenceId = tls.referenceId "
				+ " LEFT JOIN lead.leads_test_student_testdetails tsd ON tsd.testId = t.id "
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
				+ " AND tls.referenceId = ? "
				
				+ " AND tsd.sapid = ? "
				+ " AND tsd.testCompleted = 'Y' "
				+ " AND tsd.showResult = 'Y' ";
		System.out.println(pssId + " " + leadId);
		TestStudentPortalBean test = jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				acadYear, acadMonth, 
				examYear, examMonth, 
				pssId, leadId
			}, 
			new BeanPropertyRowMapper<TestStudentPortalBean>(TestStudentPortalBean.class)
		);
		return test;
	}


	@Transactional(readOnly = true)
public ArrayList<ContentStudentPortalBean> getResourceContentList (String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ContentStudentPortalBean> resourceContentList = new ArrayList<ContentStudentPortalBean>();
		String sql =  " SELECT * FROM acads.content WHERE subject = ? "
					+ " AND year = '"+LEAD_CURRENT_ACAD_YEAR+"' AND month = '"+LEAD_CURRENT_ACAD_MONTH+"' "
					+ " ORDER BY createdDate";
		try {
			resourceContentList = (ArrayList<ContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper<ContentStudentPortalBean>(ContentStudentPortalBean.class));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return resourceContentList;
	}
	


	@Transactional(readOnly = true)
public ArrayList<VideoContentStudentPortalBean> getVideoContentList (String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<VideoContentStudentPortalBean> videoContentList = new ArrayList<VideoContentStudentPortalBean>();
		String sql =  " SELECT * FROM acads.video_content WHERE subject = ? "
					+ " AND year = '"+LEAD_CURRENT_ACAD_YEAR+"' AND month = '"+LEAD_CURRENT_ACAD_MONTH+"' "
					+ " ORDER BY createdDate";
		try {
			videoContentList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper<VideoContentStudentPortalBean>(VideoContentStudentPortalBean.class));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return videoContentList;
	}


	@Transactional(readOnly = true)
public ArrayList<TestStudentPortalBean> getQuizList (String pssId, String leadId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<TestStudentPortalBean> videoContentList = new ArrayList<TestStudentPortalBean>();
		String joinWithTestDeatils = "";
		if (!StringUtils.isBlank(leadId)) {
			joinWithTestDeatils =" LEFT JOIN " + 
								 " lead.leads_test_student_testdetails tst ON tst.testId = t.id AND tst.sapid = '"+leadId+"' ";
		}
		
		String sql =" SELECT  t.* " ;
					if (!StringUtils.isBlank(leadId)) {
						sql = sql + ", COALESCE(MAX(tst.attempt), 0) AS attempt ";
					}
					sql = sql + " FROM " + 
					"    lead.leads_test t " + 
					"        INNER JOIN " + 
					"    lead.leads_test_testid_configuration_mapping tcm ON t.id = tcm.testId " + 
					"        INNER JOIN " + 
					"    lead.leads_test_live_settings tls ON tcm.referenceId = tls.referenceId " + 
					joinWithTestDeatils +
					" WHERE " + 
					"    tls.acadYear = t.acadYear " + 
					"        AND tls.acadMonth = t.acadMonth " + 
					"        AND tls.examYear = t.year " + 
					"        AND tls.examMonth = t.month " + 
					"		 AND t.active = 'Y' " +
					"        AND tls.acadYear = '"+LEAD_CURRENT_ACAD_YEAR+"' " + 
					"        AND tls.acadMonth = '"+LEAD_CURRENT_ACAD_MONTH+"' " + 
					"        AND tls.examYear = '"+LEAD_CURRENT_EXAM_YEAR+"' " + 
					"        AND tls.examMonth = '"+LEAD_CURRENT_EXAM_MONTH+"' " + 
					"        AND tls.referenceId = ? ";
		
		System.out.println("In getQuizList Sql : "+sql);
		try {
			videoContentList = (ArrayList<TestStudentPortalBean>) jdbcTemplate.query(sql, new Object[]{pssId}, new BeanPropertyRowMapper<TestStudentPortalBean>(TestStudentPortalBean.class));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return videoContentList;
	}

	@Transactional(readOnly = true)
	public String getSubjectName(String pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subject = "";
		String sql = "SELECT subject FROM exam.program_sem_subject WHERE id = ? ";
		try {
			subject = jdbcTemplate.queryForObject(sql, new Object[] {pssId}, String.class);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		System.out.println("subject  "+subject);
		return subject;
	}

	@Transactional(readOnly = true)
	public String getConsumerProgramStructureId(String pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String consumerProgramStructureId = "";
		String sql = "SELECT consumerProgramStructureId FROM exam.program_sem_subject WHERE id = ? ";
		try {
			consumerProgramStructureId = jdbcTemplate.queryForObject(sql, new Object[] {pssId}, String.class);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return consumerProgramStructureId;
	}


	@Transactional(readOnly = true)
public ProgramSubjectMappingStudentPortalBean getLastSemSubjectForProgram(String consumerProgramStructureId){
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
			new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class)
		);
	}
}
