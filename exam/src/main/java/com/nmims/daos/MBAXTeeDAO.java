package com.nmims.daos;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAScheduleInfoBean;

@Repository("mbaXTeeDAO")
public class MBAXTeeDAO extends BaseDAO{

	
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
	public MBAScheduleInfoBean getScheduleInfo(MBAScheduleInfoBean inputInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT "
				+ " UNIX_TIMESTAMP(`s`.`exam_start_date_time`) AS `startTimestamp`, "
				+ " UNIX_TIMESTAMP(`s`.`exam_end_date_time`) AS `endTimestamp`, "
				+ " `pss`.`subject`, "
				+ " `s`.`timebound_id` AS `timeboundId`, "
				+ " `s`.`schedule_id` AS `scheduleId`, "
				+ " `s`.`max_score` AS `maxMarks`, "
				+ " `a`.`name` AS `testName` "
			+ " FROM `exam`.`mbax_exams_schedule` `s` "
				
			+ " LEFT JOIN `exam`.`mbax_exams_assessments` `a` "
			+ " ON `a`.`id` = `s`.`assessments_id` "

			+ " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			+ " ON `ssc`.`id` = `s`.`timebound_id` "

			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
			
			+ " WHERE `s`.`timebound_id` = ? AND `s`.`schedule_id` = ? ";
		

		MBAScheduleInfoBean scheduleInfo = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				inputInfo.getTimeboundId(), inputInfo.getScheduleId()
			},
			new BeanPropertyRowMapper<MBAScheduleInfoBean>(MBAScheduleInfoBean.class)
		);
		scheduleInfo.setSapid(inputInfo.getSapid());
		return scheduleInfo;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamApplicableForStudent(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `lti`.`timebound_user_mapping` "
			+ " WHERE `timebound_subject_config_id` = ? AND `userId` = ? ";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				scheduleInfo.getTimeboundId(), scheduleInfo.getSapid() 
			},
			Integer.class
		);
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamBookedByStudent(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_x_bookings` "
			+ " WHERE `timeboundId` = ? AND `sapid` = ? AND `bookingStatus` = 'Y'";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				scheduleInfo.getTimeboundId(), scheduleInfo.getSapid()
			},
			Integer.class
		);
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public List<MBAScheduleInfoBean> getAssessmentsForSapid(String sapid) {
		List<MBAScheduleInfoBean> teeFinishedAssessmentsList = new ArrayList<MBAScheduleInfoBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = ""
				+ " SELECT "
					
					+ " UNIX_TIMESTAMP(`es`.`exam_start_date_time`) AS `startTimestamp`, "
					+ " UNIX_TIMESTAMP(`es`.`exam_end_date_time`) AS `endTimestamp`, "
					+ " `pss`.`subject`, "
					+ " `es`.`timebound_id` AS `timeboundId`, "
					+ " `es`.`schedule_id` AS `scheduleId`, "
					+ " `es`.`max_score` AS `maxMarks`, "
					+ " `ea`.`name` AS `testName` "
				
				+ " FROM exam.mbax_exams_assessments ea "
				
				+ " INNER JOIN exam.mbax_exams_schedule es "
				+ " ON es.assessments_id = ea.id "
				
				+ " INNER JOIN exam.mbax_assessment_timebound_id ati "
				+ " ON ati.assessments_id = ea.id "
				
				+ " INNER JOIN lti.timebound_user_mapping tum "
				+ " ON tum.timebound_subject_config_id = ati.timebound_id "
				
				+ " INNER JOIN lti.student_subject_config ssc "
				+ " ON ssc.id =  ati.timebound_id "
				
				+ " INNER JOIN exam.program_sem_subject pss "
				+ " ON ssc.prgm_sem_subj_id = pss.id "
				+ " WHERE "
					+ " tum.userId =  ? "
					+ " AND ( tum.role =  'Student' OR (es.max_score = 100 AND tum.role in ( 'Resit', 'Student' )) ) "
					+ " AND es.exam_end_date_time >= sysdate() "
				+ " ORDER BY es.exam_start_date_time DESC";
		
		teeFinishedAssessmentsList = jdbcTemplate.query(
			sql, 
			new Object[] { sapid },
			new BeanPropertyRowMapper<MBAScheduleInfoBean>(MBAScheduleInfoBean.class)
		);
		return checkIfAssessmentsApplicable(sapid, teeFinishedAssessmentsList);
	}
	
	private List<MBAScheduleInfoBean> checkIfAssessmentsApplicable(String sapid, List<MBAScheduleInfoBean> assessments) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<MBAScheduleInfoBean> teeApplicableAssessmentsList = new ArrayList<MBAScheduleInfoBean>();
		
		try {
			for (MBAScheduleInfoBean examsAssessmentsBean : assessments) {
				if(examsAssessmentsBean.getMaxMarks() == 100) {
					examsAssessmentsBean.setSapid(sapid);
					if(checkIfExamBookedByStudent(examsAssessmentsBean)) {
						teeApplicableAssessmentsList.add(examsAssessmentsBean);
					}
				} else if(checkIfExamTakenByStudent(examsAssessmentsBean.getTimeboundId(), sapid)) {
					teeApplicableAssessmentsList.add(examsAssessmentsBean);
				}
			}
		} catch (Exception e) {
			
		}
		return teeApplicableAssessmentsList;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamTakenByStudent(String timeboundId, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mbax_marks` "
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
}
