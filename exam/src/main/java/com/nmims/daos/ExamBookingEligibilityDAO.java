package com.nmims.daos;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamBookingEligibileStudentBean;
import com.nmims.beans.ExamBookingStudentCycleSubjectConfig;
import com.nmims.beans.ProgramSubjectMappingExamBean;

@Repository("examBookingEligibilityDAO")
public class ExamBookingEligibilityDAO extends BaseDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingEligibileStudentBean> getCurrentlyEligibleStudentsList(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT "
				+ " `s`.`sapid`, "
				+ " `s`.`validityEndMonth`, "
				+ " `s`.`validityEndYear`, "
				+ " `r`.`sem`, "
				+ " `r`.`program`, "
				+ " `r`.`consumerProgramStructureId`, "
				+ " `s`.`isLateral` ,s.previousStudentId,s.programChanged,s.PrgmStructApplicable "
			+ " FROM `exam`.`students` `s` "
			+ " INNER JOIN `exam`.`registration` `r`"
				+ " ON `r`.`sapid` = `s`.`sapid` "
				+ " AND `r`.`sem` = ( "
					+ " SELECT MAX(`sem`) "
					+ " FROM `exam`.`registration` `r2` "
					+ " WHERE `r2`.`sapid` = `r`.`sapid` "
					/* Dont get any entries that might have been added for the upcoming cycles */
					+ " AND STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') >= STR_TO_DATE(concat(r.year,'-',r.month,'-31'), '%Y-%b-%d') "
				+ " ) "
			+ " WHERE 1 "
				/* Dont get any students whose validity has ended */
			+ " AND STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') <= STR_TO_DATE(concat(s.validityEndYear,'-',s.validityEndMonth,'-31'), '%Y-%b-%d') "
			+ " AND `s`.`program` NOT IN ('MBA - X', 'MBA - WX', 'M.Sc. (AI)', 'M.Sc. (AI & ML Ops)') AND concat(`s`.`enrollmentMonth`,`s`.`enrollmentYear`) <> ('Jan2023') ";
		
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				year, month,
				year, month
			},
			new BeanPropertyRowMapper<ExamBookingEligibileStudentBean>(ExamBookingEligibileStudentBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingEligibileStudentBean> getCurrentlyEligibleStudentsList(String year, String month, 
			String enrollmentMonthYear, boolean includeEnrollmentCycle) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT "
				+ " `s`.`sapid`, "
				+ " `s`.`validityEndMonth`, "
				+ " `s`.`validityEndYear`, "
				+ " `r`.`sem`, "
				+ " `r`.`program`, "
				+ " `r`.`consumerProgramStructureId`, "
				+ " `s`.`isLateral` ,"
				+ " `s`.`previousStudentId`,"
				+ " `s`.`programChanged`,"
				+ " `s`.`PrgmStructApplicable` "
				+ " FROM `exam`.`students` `s` "
				+ " INNER JOIN `exam`.`registration` `r`"
				+ " ON `r`.`sapid` = `s`.`sapid` "
				+ " AND `r`.`sem` = ( "
				+ " SELECT MAX(`sem`) "
				+ " FROM `exam`.`registration` `r2` "
				+ " WHERE `r2`.`sapid` = `r`.`sapid` "
				/* Dont get any entries that might have been added for the upcoming cycles */
				+ " AND STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') >= STR_TO_DATE(concat(r.year,'-',r.month,'-31'), '%Y-%b-%d') "
				+ " ) "
				+ " WHERE 1 "
				/* Dont get any students whose validity has ended */
				+ " AND STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') <= STR_TO_DATE(concat(s.validityEndYear,'-',s.validityEndMonth,'-31'), '%Y-%b-%d') "
				+ " AND `s`.`program` NOT IN ('MBA - X', 'MBA - WX', 'M.Sc. (AI)', 'M.Sc. (AI & ML Ops)') "
				// to not to take Jan's renrolled students for December's resit students for april exam cycle
				+ " AND concat(`s`.`enrollmentMonth`,`s`.`enrollmentYear`) <> ('Jun2023') "
				;
		
				// 	adding for Jan 2023 cycle to include only students who have registered for that enrollment cycle
		
				if(includeEnrollmentCycle) {
					sql = sql + " AND concat(`s`.`enrollmentMonth`,`s`.`enrollmentYear`) IN (" + enrollmentMonthYear + ") ";
					sql = sql + " AND ( `r`.`sem` = 1 OR `s`.`isLateral` = 'Y') ";
				}
				else {
					sql = sql + " AND ( concat(`s`.`enrollmentMonth`,`s`.`enrollmentYear`) NOT IN (" + enrollmentMonthYear + ") ";
					sql = sql + " OR ( concat(`s`.`enrollmentMonth`,`s`.`enrollmentYear`) IN (" + enrollmentMonthYear + ") ";
					sql = sql + " AND `r`.`sem` <> 1 AND `s`.`isLateral` <> 'Y' ) )";
				}
				
				//adding for testing
//				sql = sql + " LIMIT 500 ";
		
		return jdbcTemplate.query(
				sql, 
				new Object[] {
						year, month,
						year, month
				},
				new BeanPropertyRowMapper<ExamBookingEligibileStudentBean>(ExamBookingEligibileStudentBean.class)
				);
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getAllSubjectBeansForSem(String consumerProgramStructureId, String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
			+ " SELECT * "
			+ " FROM `exam`.`program_sem_subject` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `sem` <= ? "
			// Dont include free subjects/soft skills subjects
			+ " and `passScore` > 15";
		
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				consumerProgramStructureId, sem
			},
			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getBBATerm5AndTerm6SubjectForSapid(String sapid, String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
			+ " SELECT  " + 
			"    pss.* " + 
			"FROM " + 
			"    exam.student_current_subject scs " + 
			"        INNER JOIN " + 
			"    exam.program_sem_subject pss ON pss.id = scs.programSemSubjectId " + 
			"WHERE " + 
			"    scs.sapid = ? AND pss.sem <= ? ";
		
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid, sem
			},
			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
		);
	}

	
	@Transactional(readOnly = true)
	public List<String> getPastWaviedInSubjectsForCycle(String sapid, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " "
			+ " SELECT `pss`.`subject` "
			+ " FROM `exam`.`student_course_mapping` `scm` "
			+ " INNER JOIN `exam`.`examorder` `eo` "
				+ " ON `eo`.`year` = `scm`.`acadYear` "
				+ " AND `eo`.`acadMonth` = `scm`.`acadMonth` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `scm`.`program_sem_subject_id` "
			+ " WHERE 1 "
//			+ " AND STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') <= STR_TO_DATE(concat(`eo`.`year`,'-',`eo`.`month`,'-31'), '%Y-%b-%d')  "
			+ " AND STR_TO_DATE(concat(`eo`.`year`,'-',`eo`.`month`,'-31'), '%Y-%b-%d')  < STR_TO_DATE(concat(?,'-',?,'-31'), '%Y-%b-%d') "
			+ " AND `scm`.`userid` = ? ";
		
		return jdbcTemplate.queryForList(
			sql, 
			new Object[] {
				year, month, sapid
			},
			String.class
		);
	}
	
	@Transactional(readOnly = true)
	public List<String> getWaivedInSubjectsForCycle(String sapid, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
			+ " SELECT `pss`.`subject` "
			+ " FROM `exam`.`student_course_mapping` `scm` "
			+ " INNER JOIN `exam`.`examorder` `eo` "
				+ " ON `eo`.`year` = `scm`.`acadYear` "
				+ " AND `eo`.`acadMonth` = `scm`.`acadMonth` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `scm`.`program_sem_subject_id` "
			+ " WHERE `eo`.`year` = ? "
			+ " AND `eo`.`month` = ? "
			+ " AND `scm`.`userid` = ? ";
		
		return jdbcTemplate.queryForList(
			sql, 
			new Object[] {
				year, month, sapid
			},
			String.class
		);
	}
	
	@Transactional(readOnly = true)
	public List<String> getFailedSubjectsForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
			+ " SELECT `subject` "
			+ " FROM `exam`.`passfail` "
			+ " WHERE `sapid` = ? "
			+ " AND `isPass` <> 'Y' ";
		
		return jdbcTemplate.queryForList(
			sql, 
			new Object[] {
				sapid
			},
			String.class
		);
	}
	
	@Transactional(readOnly = false)
	public void insertBookingConfig(ExamBookingStudentCycleSubjectConfig bookingEligibleSubject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
			+ " INSERT INTO `exam`.`student_cycle_subject_config` ( "
				+ " `year`, `month`, `sapid`, "
				+ " `program`, `sem`, `subject`, "
				+ " `programSemSubjId`, `cycleType`,"
				+ " `bookingStartDateTime`, `bookingEndDateTime`, "
				+ " `createdBy`, `updatedBy` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ? "
			+ " )";
		
		jdbcTemplate.update(
			sql, 
			new Object[] {
				bookingEligibleSubject.getYear(), bookingEligibleSubject.getMonth(), bookingEligibleSubject.getSapid(),
				bookingEligibleSubject.getProgram(), bookingEligibleSubject.getSem(), bookingEligibleSubject.getSubject(),
				bookingEligibleSubject.getProgramSemSubjId(), bookingEligibleSubject.getRole(),bookingEligibleSubject.getBookingStartDateTime(),
				bookingEligibleSubject.getBookingEndDateTime(), "Testing", "Testing"
			}
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<String> gettWaivedOffSubjectsForLateralMbaDistance( String consumerProgramStructureId){
		String sql = " SELECT  " + 
				"    subject " + 
				"FROM " + 
				"    `exam`.`program_sem_subject` " + 
				"WHERE " + 
				"    `consumerProgramStructureId` = ? " + 
				"     AND sem > 2 ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForList(
				sql, 
				new Object[] {
					consumerProgramStructureId
				},
				String.class
			);
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfEntryAlreadyExists(String sapid , String subject, String year , String month){
		String sql = " SELECT  " + 
				"    COUNT(*) " + 
				"FROM " + 
				"    exam.student_cycle_subject_config " + 
				"WHERE " + 
				"    sapid = ? AND year = ? " + 
				"        AND month = ? " + 
				"        AND subject = ? " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		Integer count = jdbcTemplate.queryForObject(
				sql, 
				new Object[] {
						sapid, 
						year, month,
						subject
				},
				Integer.class
				)   ;
		return count > 0 ;
	}
}