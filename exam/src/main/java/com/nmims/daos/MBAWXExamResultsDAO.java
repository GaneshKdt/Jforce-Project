package com.nmims.daos;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;

@Repository("mbaWxExamResultsDAO")
public class MBAWXExamResultsDAO extends BaseDAO{
	
	private static final Logger trasncript_logger = LoggerFactory.getLogger("trasncript");

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public boolean checkIfProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 int count = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 Integer.class
			 );
			 if(count > 0) {
				 return true;
			 }
			 
		} catch (Exception e) {   
			
		}
		 return false;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfComponectProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
					+ " AND `pf`.`timeboundId` = ? ";
			 int count = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 Integer.class
			 );
			 if(count > 0) {
				 return true;
			 }
			 
		} catch (Exception e) {   
			
		}
		 return false;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfPassFailResultsBySapidTimeboundId(String sapid, String id) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`exams_schedule` `s` "
					+ " ON `s`.`schedule_id` = `pf`.`schedule_id` "
					+ " AND `s`.`timebound_id` = `pf`.`timeboundId` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 int count = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 Integer.class
			 );
			 if(count > 0) {
				 return true;
			 }
			 
		} catch (Exception e) {   
			
		}
		 return false;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfMbaXPassFailResultsBySapidTimeboundId(String sapid, String id) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mbax_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`mbax_exams_schedule` `s` "
					+ " ON `s`.`schedule_id` = `pf`.`schedule_id` "
					+ " AND `s`.`timebound_id` = `pf`.`timeboundId` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 int count = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 Integer.class
			 );
			 if(count > 0) {
				 return true;
			 }
			 
		} catch (Exception e) {   
			
		}
		 return false;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfMbaXProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " count(*) "
				+ " FROM `exam`.`mbax_passfail` `pf` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 int count = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 Integer.class
			 );
			 if(count > 0) {
				 return true;
			 }
			 
		} catch (Exception e) {   
			
		}
		 return false;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getPassFailResultsBySapidTimeboundId(String sapid, String id) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `s`.`max_score` AS `max_score`, "
					+ " `s`.`exam_start_date_time` AS `examStartTime`, "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`exams_schedule` `s` "
					+ " ON `s`.`schedule_id` = `pf`.`schedule_id` "
					+ " AND `s`.`timebound_id` = `pf`.`timeboundId` "
					
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `tm`.`max_score` AS `max_score`, "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
				
				+ " INNER JOIN `exam`.`tee_marks` `tm` "
					+ " ON `tm`.`sapid` = `pf`.`sapid` AND `tm`.`timebound_id` = `pf`.`timeboundId` "
				
				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getComponentProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `ccm`.`simulation_score` , "
					+ " `ccm`.`compXM_score` , "
					+ " `ccm`.`simulation_max_score` , "
					+ " `ccm`.`compXM_max_score` , "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
				
				+ " INNER JOIN `exam`.`capstone_component_marks` `ccm` "
					+ " ON `ccm`.`sapid` = `pf`.`sapid` AND `ccm`.`timebound_id` = `pf`.`timeboundId` "
				
				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
					+ " AND `pf`.`timeboundId` = ? ";
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getMbaXPassFailResultsBySapidTimeboundId(String sapid, String id) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `s`.`max_score` AS `max_score`, "
					+ " `s`.`exam_start_date_time` AS `examStartTime` "
				+ " FROM `exam`.`mbax_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`mbax_exams_schedule` `s` "
					+ " ON `s`.`schedule_id` = `pf`.`schedule_id` "
					+ " AND `s`.`timebound_id` = `pf`.`timeboundId` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getMbaXProjectPassFailResultsBySapidTimeboundId(String sapid, String id) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " if( timeboundId < 746,75, 60)  AS `max_score`, " //  update the weightage for Capstone Subject max marks 60 in Capstone Simulation from Dec21 acad cycle
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mbax_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, id },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public List<MBAWXPassFailStatus> getAllPassFailForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `s`.`max_score` AS `max_score`, "
					+ " `s`.`exam_start_date_time` AS `examStartTime` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`exams_schedule` `s` "
					+ " ON `s`.`schedule_id` = `pf`.`schedule_id` "
					+ " AND `s`.`timebound_id` = `pf`.`timeboundId` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL ";
			List<MBAWXPassFailStatus> passFailDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
			return passFailDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllTimeboundSubjectDetailsForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `userId` = ? ";
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getCurrentTimeboundSubjectDetailsForStudent(String sapid, String acadMonth, String acadYear) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `r`.`sem`, ssc.prgm_sem_subj_id, scmap.hasIA, scmap.hasTEE  "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					+ "	INNER JOIN " 
						+ "    exam.mdm_subjectcode_mapping scmap on scmap.id = pss.id "
					+	"INNER JOIN `exam`.`registration` `r` ON  `tum`.`userId` = `r`.`sapid` "	
					+ " WHERE `userId` = ? "
					+ " AND `r`.`month` = ? "
					+ " AND `r`.`year` = ? "
					+ " AND `ssc`.`acadMonth` = ? "
					+ " AND `ssc`.`acadYear` = ? "
					+ " AND `pss`.`passScore` > 0 ";
	
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { 
					 sapid, acadMonth, acadYear,acadMonth, acadYear  
				 },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllTimeboundSubjectDetailsForStudentYearMonth(String sapid, String year, String month) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `userId` = ?, `acadYear` = ?, `acadMonth` = ? ";
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid, year, month },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}

	@Transactional(readOnly = true)
	public StudentSubjectConfigExamBean getTimeboundSubjectDetailsForTimeboundId(String timeboundId) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`student_subject_config` `ssc` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `ssc`.`id` = ? ";
			StudentSubjectConfigExamBean timeboundSubjectDetails = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { timeboundId },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentExamBean> getAllRegistrationsForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT * FROM `exam`.`registration` WHERE `sapid` = ?";
			List<StudentExamBean> studentRegistrations = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
			 );
			return studentRegistrations;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getLatestRegistrationForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT * "
					+ " FROM `exam`.`registration` "
					+ " WHERE (`sapid`, `sem`) "
					+ " IN ( "
						+ " SELECT `sapid`, MAX(sem) "
						+ " FROM `exam`.`registration` "
						+ " WHERE `sapid` = ? "
					+ " )";
			StudentExamBean studentRegistration = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
			 );
			return studentRegistration;
		} catch (Exception e) {   
			
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<MBAWXPassFailStatus> getAllTeeMarksForSubject(String sapid, String timeboundId) {

		String sql = ""
			+ " SELECT "
				+ " `tmh`.`score` AS `teeScore`, "
				+ " `s`.`max_score` AS `max_score`, "
				+ " `tmh`.`status`, "
				+ " `tmh`.`sapid`, "
				+ " `tmh`.`timebound_id`, "
				+ " `tmh`.`schedule_id`,"
				+ " `s`.`exam_start_date_time` AS `examStartTime`, "
				+ " `pss`.`sem` AS `sem` "
			+ " FROM `exam`.`tee_marks_history` `tmh` "
				
			+ " LEFT JOIN `exam`.`exams_schedule` `s` "
				+ " ON `s`.`schedule_id` = `tmh`.`schedule_id` "
				+ " AND `s`.`timebound_id` = `tmh`.`timebound_id` "
				
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `tmh`.`prgm_sem_subj_id` "
			
			+ " WHERE "
				+ " `s`.`isResultLive` = 'Y' "
			+ " AND `sapid` = ? "
			+ " AND `tmh`.`timebound_id` = ? "
			+ " AND `tmh`.`status` <> 'Not Attempted' ";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<MBAWXPassFailStatus> history = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid, timeboundId },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
			return history;
		} catch (Exception e) {   
			
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getAllProgramSemSubjectsStudent(String sapid) {

		String sql = ""
			+ " SELECT * "
			+ " FROM exam.program_sem_subject "
			+ " WHERE "
				+ " `active` = 'Y' "
			+ " AND `consumerProgramStructureId` = ( "
				+ "	SELECT `consumerProgramStructureId` "
				+ " FROM `exam`.`students` "
				+ " WHERE `sapid` = ? "
			+ " ) "
			+ " AND `passScore` > 0 ";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ProgramSubjectMappingExamBean> pss = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
			 );
			return pss;
		} catch (Exception e) {   
			 trasncript_logger.error(" Error for fetching data " + e.getMessage());
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getAllSubjectsForProgram(String consumerProgramStructureId) {

		String sql = ""
			+ " SELECT * "
			+ " FROM exam.program_sem_subject "
			+ " WHERE "
				+ " `active` = 'Y' "
			+ " AND `consumerProgramStructureId` = ? "
			+ " AND `passScore` > 0 ";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ProgramSubjectMappingExamBean> pss = jdbcTemplate.query(
				 sql,
				 new Object[] { consumerProgramStructureId },
				 new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
			 );
			return pss;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getAllSubjectsForTermForProgram(String consumerProgramStructureId, int term) {

		String sql = ""
			+ " SELECT * "
			+ " FROM exam.program_sem_subject "
			+ " WHERE "
				+ " `active` = 'Y' "
			+ " AND `consumerProgramStructureId` = ? "
			+ " AND `passScore` > 0 "
			+ " AND `sem` = ? ";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<ProgramSubjectMappingExamBean> pss = jdbcTemplate.query(
				 sql,
				 new Object[] { consumerProgramStructureId, term },
				 new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
			 );
			return pss;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public int getNumberOfPassFailForTermYearMonth(String sapid, String year, String month, String sem) {
		

		String sql = ""
			+ " SELECT count(*) "
			
			+ " FROM `exam`.`mba_passfail` `pf` "
			
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
			
			+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
			+ " ON `ssc`.`id` = `pf`.`timeboundId` "

			+ " WHERE `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`sapid` = ? "
				//+ " AND `ssc`.`acadYear` = ? "
				//+ " AND `ssc`.`acadMonth` = ? "
				+ " AND `pss`.`sem` = ? "
				+ " AND `pf`.`grade` is not null;";


		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int numberOfExamsGiven = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, sem },
				 Integer.class
			 );
			return numberOfExamsGiven;
		} catch (Exception e) {   
			
		}
		return 0;
	}
	
	@Transactional(readOnly = true)
	public int getNonGradedNumberOfPassFailForTermYearMonth(String sapid, String sem) {
		String sql = ""
			+ " SELECT count(*) "
			
			+ " FROM `exam`.`mba_passfail` `pf` "
			
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
			
			+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
			+ " ON `ssc`.`id` = `pf`.`timeboundId` "

			+ " WHERE `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`sapid` = ? "
				+ " AND `pss`.`sem` = ? ";


		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int numberOfExamsGiven = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, sem },
				 Integer.class
			 );
			return numberOfExamsGiven;
		} catch (Exception e) {   
			
		}
		return 0;
	}
	
	@Transactional(readOnly = true)
	public int getNumberOfMbaXPassFailForTermYearMonth(String sapid, String year, String month, String sem) {
		

		String sql = ""
			+ " SELECT count(*) "
			
			+ " FROM `exam`.`mbax_passfail` `pf` "
			
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
			
			+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
			+ " ON `ssc`.`id` = `pf`.`timeboundId` "
			
			+ " WHERE `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`sapid` = ? "
				//+ " AND `ssc`.`acadYear` = ? "
				//+ " AND `ssc`.`acadMonth` = ? "
				+ " AND `pss`.`sem` = ? " 
				+ " AND `pf`.`grade` is not null;";
				

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int numberOfExamsGiven = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, sem },
				 Integer.class
			 );
			return numberOfExamsGiven;
		} catch (Exception e) {   
			
		}
		return 0;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllApplicableSubjectDetailsForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			/*String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `userId` = ? ;"; 
					+ " AND (`ssc`.`acadYear`, `ssc`.`acadMonth`) IN ( "
						+ " SELECT `year`, `month` FROM `exam`.`registration` WHERE `sapid` = ? "

					+ " ) ";*/
			String sql;
		sql = " " 
				+ " SELECT " 
				+ " * "
				+ " FROM "
				+     " (SELECT " 
				+         " `ssc`.*, `pss`.`subject`, `pss`.`sem` "
				+    " FROM "
				+        " `lti`.`timebound_user_mapping` `tum` "
				+    " INNER JOIN (SELECT  "
				+        " *, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+    " FROM "
				+        " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mba_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` and `pf`.`grade` is not null  "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+    " WHERE "
				+        " `userId` = ? "
				+    " GROUP BY sem , exam) v "
				+" WHERE "
				+    " v.exam = (SELECT " 
				+            " MAX(exam) "
				+        " FROM "
				+            " `lti`.`timebound_user_mapping` `tum` "
				+                " INNER JOIN "
				+            " (SELECT "
				+                " *, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+            " FROM "
				+                " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mba_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` and `pf`.`grade` is not null  "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+        " WHERE "
				+            " `userId` = ? "
				+                " AND `v`.`sem` = `pss`.`sem`) "  
				+                " order by sem ; ";
			
			
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid, sapid },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getNonGradedAllApplicableSubjectDetailsForStudent(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			/*String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `userId` = ? ;"; 
					+ " AND (`ssc`.`acadYear`, `ssc`.`acadMonth`) IN ( "
						+ " SELECT `year`, `month` FROM `exam`.`registration` WHERE `sapid` = ? "

					+ " ) ";*/
			String sql;
		sql = " " 
				+ " SELECT " 
				+ " * "
				+ " FROM "
				+     " (SELECT " 
				+         " `ssc`.*, `pss`.`subject`, `pss`.`sem` "
				+    " FROM "
				+        " `lti`.`timebound_user_mapping` `tum` "
				+    " INNER JOIN (SELECT  "
				+        " *, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+    " FROM "
				+        " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mba_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+    " WHERE "
				+        " `userId` = ? "
				+    " GROUP BY sem , exam) v "
				+" WHERE "
				+    " v.exam = (SELECT " 
				+            " MAX(exam) "
				+        " FROM "
				+            " `lti`.`timebound_user_mapping` `tum` "
				+                " INNER JOIN "
				+            " (SELECT "
				+                " *, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+            " FROM "
				+                " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mba_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id`  "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+        " WHERE "
				+            " `userId` = ? "
				+                " AND `v`.`sem` = `pss`.`sem`) "  
				+                " order by sem ; ";
			
			
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid, sapid },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllApplicableSubjectDetailsForStudentMBAX(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			/*String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem` "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					
					+ " WHERE `userId` = ? ;"; 
					+ " AND (`ssc`.`acadYear`, `ssc`.`acadMonth`) IN ( "
						+ " SELECT `year`, `month` FROM `exam`.`registration` WHERE `sapid` = ? "

					+ " ) ";*/
			String sql;
		sql = " " 
				+ " SELECT " 
				+ " * "
				+ " FROM "
				+     " (SELECT " 
				+         "  r.year as acadYear, r.month as acadMonth, ssc.examYear, ssc.examMonth, ssc.exam, ssc.acad, `pss`.`subject`, `pss`.`sem` "
				+    " FROM "
				+        " `lti`.`timebound_user_mapping` `tum` "
				+    " INNER JOIN (SELECT  "
				+        " *, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+            " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+    " FROM "
				+        " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mbax_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` and `pf`.`grade` is not null  "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+    " INNER JOIN exam.registration r on r.sapid = tum.userid and r.sem = pss.sem "
				+    " WHERE "
				+        " `userId` = ? "
				+    " GROUP BY sem , exam) v "
				+" WHERE "
				+    " v.exam = (SELECT " 
				+            " MAX(exam) "
				+        " FROM "
				+            " `lti`.`timebound_user_mapping` `tum` "
				+                " INNER JOIN "
				+            " (SELECT "
				+                " *, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, "
				+                    " DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam "
				+            " FROM "
				+                " `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				+    " INNER JOIN `exam`.`mbax_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` and `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` and `pf`.`grade` is not null  "
				+    " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pf`.`prgm_sem_subj_id` = `pss`.`id` "
				+        " WHERE "
				+            " `userId` = ? "
				+                " AND `v`.`sem` = `pss`.`sem`) "  
				+                " order by sem ; ";
			
			
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid, sapid },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllApplicableSubjectDetailsForStudentPassfail(String sapid) {

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " "
					+ " SELECT `ssc`.*, `pss`.`subject`, `pss`.`sem`, scmap.hasIA, scmap.hasTEE  "
					+ " FROM `lti`.`timebound_user_mapping` `tum` "

					+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
						+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
						+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
					+ " INNER JOIN " 
					+ "    exam.mdm_subjectcode_mapping scmap on scmap.id = pss.id "
					+ " WHERE `userId` = ? ";
			List<StudentSubjectConfigExamBean> timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getTEERecordForStudent(String sapid, String timeboundId) {
		
		String sql = ""
		+ " SELECT "
			+ " `tmh`.`score` AS `teeScore`, "
			+ " `s`.`max_score` AS `max_score`, "
			+ " `tmh`.`status`, "
			+ " `tmh`.`sapid`, "
			+ " `tmh`.`timebound_id`, "
			+ " `tmh`.`schedule_id` "
		+ " FROM `exam`.`tee_marks_history` `tmh` "
		+ " INNER JOIN `exam`.`exams_schedule` `s` "
			+ " ON `s`.`schedule_id` = `tmh`.`schedule_id` "
			+ " AND `s`.`timebound_id` = `tmh`.`timebound_id` "
		+ " WHERE "
			+ " `s`.`max_score` = 30 "
		+ " AND `s`.`isResultLive` = 'Y' "
		+ " AND `sapid` = ? "
		+ " AND `tmh`.`timebound_id` = ? "
		+ " AND `tmh`.`status` <> 'Not Attempted' ";
	
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			MBAWXPassFailStatus teeMarks = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, timeboundId },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
			return teeMarks;
		} catch (Exception e) {   
			
			return new MBAWXPassFailStatus();
		}
//		return null;
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getTEERecordForMbaXStudent(String sapid, String timeboundId) {
		
		String sql = ""
			+ " SELECT "
			+ " `tmh`.`score` AS `teeScore`, "
			+ " `s`.`max_score` AS `max_score`, "
			+ " `tmh`.`status`, "
			+ " `tmh`.`sapid`, "
			+ " `tmh`.`timebound_id`, "
			+ " `tmh`.`schedule_id` "
		+ " FROM `exam`.`mbax_marks_history` `tmh` "
		+ " INNER JOIN `exam`.`mbax_exams_schedule` `s` "
			+ " ON `s`.`schedule_id` = `tmh`.`schedule_id` "
			+ " AND `s`.`timebound_id` = `tmh`.`timebound_id` "
		+ " WHERE "
			+ " `s`.`max_score` = 40 "
		+ " AND `s`.`isResultLive` = 'Y' "
		+ " AND `tmh`.`timebound_id` = ? "
		+ " AND `sapid` = ? "
		+ " AND `tmh`.`status` <> 'Not Attempted' ";		
	
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			MBAWXPassFailStatus teeMarks = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] {  timeboundId ,sapid },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
			return teeMarks;
		} catch (Exception e) {   
//			
			return new MBAWXPassFailStatus();
		}
//		return null;
	}
	
	@Transactional(readOnly = true)
	public List<MBAWXPassFailStatus> getAllTeeMarksForStudent(String sapid) {

		String sql = ""
			+ " SELECT "
				+ " `tmh`.`score` AS `teeScore`, "
				+ " `s`.`max_score` AS `max_score`, "
				+ " `tmh`.`status`, "
				+ " `tmh`.`sapid`, "
				+ " `tmh`.`timebound_id`, "
				+ " `tmh`.`schedule_id`,"
				+ " `s`.`exam_start_date_time` AS `examStartTime`, "
				+ " `pss`.`sem` AS `sem` "
			+ " FROM `exam`.`tee_marks_history` `tmh` "

			+ " INNER JOIN `exam`.`exams_schedule` `s` "
				+ " ON `s`.`schedule_id` = `tmh`.`schedule_id` "
				+ " AND `s`.`timebound_id` = `tmh`.`timebound_id` "

			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `tmh`.`prgm_sem_subj_id` "
			
			+ " WHERE `s`.`isResultLive` = 'Y' AND `sapid` = ? "
			+ " AND `tmh`.`status` <> 'Not Attempted' "
			+ " GROUP BY `schedule_id`, `timebound_id` "
			+ " ORDER BY `tmh`.`created_at` DESC";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<MBAWXPassFailStatus> history = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			);
			return history;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<MBAWXPassFailStatus> getAllTeeMarksForMbaXStudent(String sapid) {

		String sql = ""
			+ " SELECT "
				+ " `tmh`.`score` AS `teeScore`, "
				+ " `s`.`max_score` AS `max_score`, "
				+ " `tmh`.`status`, "
				+ " `tmh`.`sapid`, "
				+ " `tmh`.`timebound_id`, "
				+ " `tmh`.`schedule_id`,"
				+ " `s`.`exam_start_date_time` AS `examStartTime` "
			+ " FROM `exam`.`mbax_marks_history` `tmh` "

			+ " INNER JOIN `exam`.`mbax_exams_schedule` `s` "
				+ " ON `s`.`schedule_id` = `tmh`.`schedule_id` "
				+ " AND `s`.`timebound_id` = `tmh`.`timebound_id` "
			
			+ " WHERE `s`.`isResultLive` = 'Y' AND `sapid` = ? "
			+ " GROUP BY `schedule_id`, `timebound_id` "
			+ " ORDER BY `tmh`.`created_at` DESC";

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<MBAWXPassFailStatus> history = jdbcTemplate.query(
				 sql,
				 new Object[] { sapid },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			);
			return history;
		} catch (Exception e) {   
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailBySapidForTerm(String sapid, int term)throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject, "
					+ " pss.sem AS `term`, "
					+ " ssc.examYear, "
					+ " ssc.examMonth, "
					+ " ssc.acadYear, "
					+ " ssc.acadMonth, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pf.prgm_sem_subj_id = pss.id " 
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem = ? "
				+ " AND pf.grade is not null "
				+ " ORDER BY ssc.startDate ASC ";
	
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid, term
			}, 
			new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailForCGPACalculation(String sapid, int term)throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.sem AS `term`, "
					+ " pss.subject, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pss.id = pf.prgm_sem_subj_id "
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem <= ? "
				+ " AND pf.grade is not null; ";
		
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid, term, 
			}, 
			new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public String getStudentSpecialisations(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		/*
		 * commented out by harsh
		 * updated the query to fetch alphabetically ordered specialization
		 * and return the details in formated string 
		String sql = ""
				+ " SELECT `st`.`specializationType` "
				+ " FROM `lti`.`mba_specialisation_details` `spd` "
				+ " INNER JOIN `exam`.`specialization_type` `st` ON `st`.`id` = `specialisation1` OR `st`.`id` = `specialisation2` "
				+ " WHERE `sapid` = ? ";
				
				updated the query to have specialization to be ordered by nomenclature card #9897
		*/
		String sql = "select group_concat(specializationType order by specializationType separator ' | ') from exam.specialization_type where id in ( "
				+ "select `st`.`id` from `lti`.`mba_specialisation_details` `spd` inner join `exam`.`specialization_type` `st` on `st`.`id` = `specialisation1` "
				+ "or `st`.`id` = `specialisation2` where `sapid` = ?)";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { sapid }, String.class);
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailBySapidForTermMBAX(String sapid, int term){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject, "
					+ " pss.sem AS `term`, "
					+ " ssc.examYear, "
					+ " ssc.examMonth, "
					+ " ssc.acadYear, "
					+ " ssc.acadMonth, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total "
				+ " FROM  exam.mbax_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pf.prgm_sem_subj_id = pss.id " 
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem = ? "
				+ " AND pf.grade is not null "
				+ " ORDER BY ssc.startDate ASC ";
	
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid, term
			}, 
			new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailForCGPACalculationMBAX(String sapid, int term){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.sem AS `term`, "
					+ " pss.subject, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total "
				+ " FROM  exam.mbax_passfail pf "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pss.id = pf.prgm_sem_subj_id "
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pss.sem <= ? "
				+ " AND pf.grade is not null; ";
		
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid, term, 
			}, 
			new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getBopProjectPassFailResult(String sapid, String timeboundId) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " '50' AS `max_score`, "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mbax_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, timeboundId },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
	@Transactional(readOnly = true)
	public int getNumberOfMbaXPassFailForStructureChangeStudent(String sapid, String sem) {
		

		String sql = " SELECT " + 
				"    count(*) " + 
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
				"    `pf`.`isResultLive` = 'Y' " + 
				"        AND `pf`.`sapid` = ? " + 
				"        AND `map`.`sem` = ? " + 
				"        AND `pf`.`grade` IS NOT NULL ";
				

		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int numberOfExamsGiven = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, sem },
				 Integer.class
			 );
			return numberOfExamsGiven;
		} catch (Exception e) {   
			
		}
		return 0;
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllApplicableSubjectDetailsForStructureChangeStudent(final String sapid) {
		List<StudentSubjectConfigExamBean> timeboundSubjectDetails = new ArrayList<StudentSubjectConfigExamBean>();
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = " SELECT " + 
					"    `ssc`.*, `pss`.`subject`, `map`.`sem`, " + 
					"   IFNULL(map.acadYear, ssc.acadYear) AS acadYear , " + 
					"    IFNULL(map.acadMonth, ssc.acadMonth) AS acadMonth , " + 
					"    IFNULL(map.examYear, ssc.examYear) AS examYear , " + 
					"    IFNULL(map.examMonth, ssc.examMonth) AS examMonth  "+
					"FROM " + 
					"    `lti`.`timebound_user_mapping` `tum` " + 
					"        INNER JOIN " + 
					"    `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` " + 
					"        INNER JOIN " + 
					"    exam.mbax_change_structure_mapping map ON map.oldPssId = `ssc`.`prgm_sem_subj_id` " + 
					"        AND tum.userId = map.sapid " + 
					"        INNER JOIN " + 
					"    `exam`.`program_sem_subject` `pss` ON map.newPssId = `pss`.`id` " + 
					"WHERE " + 
					"    `userId` = ? ";
					
			timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
					 }
				},
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
			return timeboundSubjectDetails;
		}
		
	}
	
	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllSemOfAcadYearMonthForStructureChangeStudent(final String sapid) {
		List<StudentSubjectConfigExamBean> timeboundSubjectDetails = new ArrayList<StudentSubjectConfigExamBean>();
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			

			String sql = " SELECT  " + 
					"    * " + 
					"FROM " + 
					"    (SELECT  " + 
					"        r.year as acadYear, r.month as acadMonth, ssc.examYear, ssc.examMonth, ssc.exam, ssc.acad, `pss`.`subject`, `map`.`sem` " + 
					"    FROM " + 
					"        `lti`.`timebound_user_mapping` `tum` " + 
					"    INNER JOIN (SELECT  " + 
					"        *, " + 
					"            DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, " + 
					"            DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam " + 
					"    FROM " + 
					"        `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` " + 
					"    INNER JOIN `exam`.`mbax_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` " + 
					"        AND `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` " + 
					"        AND `pf`.`grade` IS NOT NULL " + 
					"    INNER JOIN exam.mbax_change_structure_mapping map ON map.sapid = pf.sapid " + 
					"        AND map.oldPssId = `pf`.`prgm_sem_subj_id` " + 
					"    INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = map.newPssId " + 
					"    INNER JOIN exam.registration r on r.sapid = tum.userid and r.sem = pss.sem " + 
					"    WHERE " + 
					"        `userId` = ? " + 
					"    GROUP BY sem , exam) v " + 
					"WHERE " + 
					"    v.exam = (SELECT  " + 
					"            MAX(exam) " + 
					"        FROM " + 
					"            `lti`.`timebound_user_mapping` `tum` " + 
					"                INNER JOIN " + 
					"            (SELECT  " + 
					"                *, " + 
					"                    DATE_FORMAT(STR_TO_DATE(CONCAT(`acadYear`, `acadMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS acad, " + 
					"                    DATE_FORMAT(STR_TO_DATE(CONCAT(`examYear`, `examMonth`, '01'), '%Y %M %d'), '%Y-%m-%d') AS exam " + 
					"            FROM " + 
					"                `lti`.`student_subject_config`) `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` " + 
					"                INNER JOIN " + 
					"            `exam`.`mbax_passfail` `pf` ON `tum`.`userId` = `pf`.`sapid` " + 
					"                AND `pf`.`timeboundId` = `tum`.`timebound_subject_config_id` " + 
					"                AND `pf`.`grade` IS NOT NULL " + 
					"                INNER JOIN " + 
					"            exam.mbax_change_structure_mapping map ON map.oldPssId = `pf`.`prgm_sem_subj_id` " + 
					"                AND map.sapid = pf.sapid " + 
					"                INNER JOIN " + 
					"            `exam`.`program_sem_subject` `pss` ON `pss`.`id` = map.newPssId " + 
					"        WHERE " + 
					"            `userId` = ? " + 
					"                AND `v`.`sem` = `map`.`sem`) " + 
					"ORDER BY sem " ;			
			
			timeboundSubjectDetails = jdbcTemplate.query(
				 sql,
				 new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
						 preparedStatement.setString(2,sapid);
					 }
				},
				 new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
			 );
			return timeboundSubjectDetails;
		} catch (Exception e) {   
			
			return timeboundSubjectDetails;
		}
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getAllProgramSemSubjectsForStructureChangeStudent(final String sapid) {

		String sql = " SELECT  " + 
				"    pss.subject, map.sem " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.mbax_change_structure_mapping map ON map.newPssId = pss.id " + 
				"        AND s.sapid = map.sapid " + 
				"WHERE " + 
				"    `active` = 'Y' " + 
				"        AND s.sapid = ? " + 
				"        AND `passScore` > 0 ";

		List<ProgramSubjectMappingExamBean> pss = new ArrayList<ProgramSubjectMappingExamBean>(); 
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			pss = jdbcTemplate.query(
				 sql,
				 new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
					 }
				},
				 new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
			 );
			return pss;
		} catch (Exception e) {   
			
			return pss;
		}
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailBySapidForTermMBAXForStructureChangeStudent(final String sapid,final int term){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =" SELECT " + 
				"    pf.*, " + 
				"    pss.subject, " + 
				"    map.sem AS `term`, " + 
				"    IFNULL(map.acadYear, ssc.acadYear) AS acadYear , " + 
				"    IFNULL(map.acadMonth, ssc.acadMonth) AS acadMonth , " + 
				"    IFNULL(map.examYear, ssc.examYear) AS examYear , " + 
				"    IFNULL(map.examMonth, ssc.examMonth) AS examMonth , " + 
				"    map.newPssId AS prgm_sem_subj_id , " + 
				"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total " + 
				"FROM " + 
				"    lti.student_subject_config ssc " + 
				"        INNER JOIN " + 
				"    exam.mbax_passfail pf ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN " + 
				"    exam.mbax_change_structure_mapping map ON map.sapid = pf.sapid " + 
				"        AND map.oldPssId = pf.prgm_sem_subj_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = map.newPssId " + 
				"WHERE " + 
				"    pf.sapid = ? AND map.sem = ? " + 
				"        AND pf.grade IS NOT NULL " + 
				"ORDER BY ssc.startDate ASC ";
		List<MBAPassFailBean> list = new ArrayList<MBAPassFailBean>();
		try {
			list = 	jdbcTemplate.query(
				sql, 
				new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
						 preparedStatement.setInt(2,term);
					 }
				}, 
				new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
			);
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			
			return list;
		}
	}
	
	@Transactional(readOnly = true)
	public List<MBAPassFailBean> getPassFailForCGPACalculationMBAXForStructureChangeStudent(final String sapid,final int term){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " SELECT  " + 
				"    pf.*, " + 
				"    map.sem AS `term`, " + 
				"    pss.subject, " + 
				"    map.newPssId AS prgm_sem_subj_id , " +
				"    (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total " + 
				"FROM " + 
				"    exam.mbax_passfail pf " + 
				"        INNER JOIN " + 
				"    exam.mbax_change_structure_mapping map ON map.sapid = pf.sapid " + 
				"        AND map.oldPssId = pf.prgm_sem_subj_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = map.newPssId " + 
				"WHERE " + 
				"    pf.sapid = ? " + 
				"        AND map.sem <= ? " + 
				"        AND pf.grade IS NOT NULL ";
		
		List<MBAPassFailBean> list = new ArrayList<MBAPassFailBean>();
		try {
			list = 	jdbcTemplate.query(
				sql, 
				new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
						 preparedStatement.setInt(2,term);
					 }
				}, 
				new BeanPropertyRowMapper<MBAPassFailBean>(MBAPassFailBean.class)
			);
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			
			return list;
		}
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getSubjectsForTermForProgramForStructureChangeStudent(final String sapid,final int term) {

		String sql = "  SELECT  " + 
				"    pss.subject, map.sem " + 
				"FROM " + 
				"    exam.program_sem_subject pss " + 
				"        INNER JOIN " + 
				"    exam.mbax_change_structure_mapping map ON map.newPssId = pss.id " + 
				"WHERE " + 
				"    pss.active = 'Y' " + 
				"        AND map.sapid = ? " + 
				"        AND map.sem = ? " + 
				"        AND pss.passScore > 0 ";
		List<ProgramSubjectMappingExamBean> pss = new ArrayList<ProgramSubjectMappingExamBean>();
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			pss = jdbcTemplate.query(
				 sql,
				 new PreparedStatementSetter() {
					 public void setValues(PreparedStatement preparedStatement) throws SQLException {
						 preparedStatement.setString(1,sapid);
						 preparedStatement.setInt(2,term);
					 }
				},
				 new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
			 );
			return pss;
		} catch (Exception e) {   
			
			return pss;
		}
	}
	
	@Transactional(readOnly = true)
	public MBAWXPassFailStatus getIAOnlyComponentPassFailResult(String sapid, String timeboundId) {
		MBAWXPassFailStatus  attemptedTest = null;
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = ""
				+ " SELECT "
					+ " `pf`.*, "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mba_passfail` `pf` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "

				+ " WHERE "
					+ " `pf`.`sapid` = ? "
				+ " AND `pf`.`isResultLive` = 'Y' "
				+ " AND `pf`.`status` IS NOT NULL "
					+ " AND `pf`.`timeboundId` = ? ";
			
			 attemptedTest = jdbcTemplate.queryForObject(
				 sql,
				 new Object[] { sapid, timeboundId },
				 new BeanPropertyRowMapper<MBAWXPassFailStatus>(MBAWXPassFailStatus.class)
			 );
		} catch (Exception e) {   
			
		}
		return attemptedTest;
	}
	
}
