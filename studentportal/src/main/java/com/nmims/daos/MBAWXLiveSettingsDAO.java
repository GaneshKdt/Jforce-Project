package com.nmims.daos;


import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAStudentDetailsStudentPortalBean;

@Repository("mbawxLiveSettingsDAO")
public class MBAWXLiveSettingsDAO extends BaseDAO{

	
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
	
	
	public boolean checkIfSettingLiveForStudent(String sapid, String type) {
		MBAStudentDetailsStudentPortalBean studentDetails = getLatestRegistrationForStudent(sapid);
		return checkIfLiveByType(studentDetails, type, studentDetails.getConsumerProgramStructureId());
	}
	// Get current registration/timebound details for the student
	@Transactional(readOnly = true)
	private MBAStudentDetailsStudentPortalBean getLatestRegistrationForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " "
				+ " SELECT "
					+ " `r`.`sapid` AS `sapid`, "
					+ " `r`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
					+ " `ssc`.`acadYear` AS `currentAcadYear`, "
					+ " `ssc`.`acadMonth` AS `currentAcadMonth`, "
					+ " `ssc`.`examYear` AS `currentExamYear`, "
					+ " `ssc`.`examMonth` AS `currentExamMonth` "
				+ " FROM `exam`.`registration` `r` "

				+ " LEFT JOIN "
				+ " ( "
					+ " SELECT `acadYear`, `acadMonth`, `examYear`, `examMonth`, pss.consumerProgramStructureId  "
					+ " FROM `lti`.`student_subject_config` conf "
					+ " INNER JOIN "
					+ " exam.program_sem_subject pss ON pss.id = conf.prgm_sem_subj_id "
					/* 
					 * This will group the subjects from each sem.
					 * This is done to group and get the latest month/year for the students current registration 
					*/
					+ " GROUP BY `acadYear`, `acadMonth`, `examYear`, `examMonth`, pss.consumerProgramStructureId  "
				+ " ) `ssc` "
				+ " ON `ssc`.`acadYear` = `r`.`year` AND `ssc`.`acadMonth` = `r`.`month` AND ssc.consumerProgramStructureId= r.consumerProgramStructureId "
				
				/* Latest registration details */
				+ " WHERE (`sapid`, `sem`) IN ( "
					+ " SELECT "
					+ " `sapid`, "
					+ " MAX(`sem`) AS `sem` "
					+ " FROM `exam`.`registration` "
					+ " WHERE `sapid` = ? "
					+ " GROUP BY `sapid` "
				+ " ) ";


		MBAStudentDetailsStudentPortalBean currentRegistrationDetails = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ sapid }, 
				new BeanPropertyRowMapper<MBAStudentDetailsStudentPortalBean>(MBAStudentDetailsStudentPortalBean.class));
		
		return currentRegistrationDetails;
	}
	
	@Transactional(readOnly = true)
	private boolean checkIfLiveByType(MBAStudentDetailsStudentPortalBean mbaStudentDetailsBean, String type, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Check if Exam Booking is live
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_wx_exam_live_setting` `mels` "
			+ " WHERE "
				+ " (sysdate() BETWEEN `startTime` AND `endTime`)"
		    + " AND `acadsMonth` = ? AND `acadsYear` = ? "
		    + " AND `examMonth` = ? AND `examYear` = ? "
			+ " AND `type` = ? "
			+ " AND `consumerProgramStructureId` = ? ";
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				mbaStudentDetailsBean.getCurrentAcadMonth(), mbaStudentDetailsBean.getCurrentAcadYear(), 
				mbaStudentDetailsBean.getCurrentExamMonth(), mbaStudentDetailsBean.getCurrentExamYear(),
				type, consumerProgramStructureId
			},
			Integer.class
		);
		
		
		System.out.println(sql);
		System.out.println(count);
		return count > 0;
	}
}
