package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBAWXConfigurationBean;

@Repository("mbawxReRegistrationDAO")
public class MBAWXReRegistrationDAO extends BaseDAO{

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Value("${MBA_WX_EXAM_BOOKING_CHARGES}")
	private String MBA_WX_EXAM_BOOKING_CHARGES;
	
	@Value("${MBA_WX_EXAM_BOOKING_SLOT_CHANGE_CHARGES}")
	private String MBA_WX_EXAM_BOOKING_SLOT_CHANGE_CHARGES;
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public boolean checkIfReRegistrationLiveForSapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Get list of failed subjects
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`students` `s` "

		    /* get Acad Year/Month details */
		    + " INNER JOIN  ( "
		    	+ " SELECT "
			    	+ " MAX(`sem`) as `currentSem`, "
			    	+ " `month` AS `acadsMonth`, "
			    	+ " `year` AS `acadsYear`, "
			    	+ " `sapid` "
		    	+ " FROM  `exam`.`registration` "
		    	+ " GROUP BY `sapid` "
		    + " ) `reg` "
		    + " ON `s`.`sapid` = `reg`.`sapid` "

		    /* get Exam Year/Month details */
				/* get timebound ids for student */
			    + " LEFT JOIN `lti`.`timebound_user_mapping` `tum` "
			    + " ON `tum`.`userId` = `s`.`sapid` "

				/* get ssc list for student */
			    + " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

		    /* get Live Settings details */
		    + " RIGHT JOIN `exam`.`mba_wx_exam_live_setting` `mels` "
		    + " ON ( "
			    	+ " `s`.`consumerProgramStructureId` = `mels`.`consumerProgramStructureId` "
			    + " AND `mels`.`acadsYear` = `reg`.`acadsYear` "
			    + " AND `mels`.`acadsMonth` = `reg`.`acadsMonth` "
			    + " AND `mels`.`examYear` = `ssc`.`examYear` "
			    + " AND `mels`.`examMonth` = `ssc`.`examMonth` "
		    + " ) "
			+ " WHERE "
				+ " `s`.`sapid` = ? "
				+ " AND (sysdate() BETWEEN `mels`.`startTime` AND `mels`.`endTime`)"
				+ " AND `mels`.`type` = 'Exam Re-Registration' ";
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapid },
			Integer.class
		);
		
		if(count == 0) {
			return false;
		}
		return true;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfReRegistrationApplicableForSapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Get number of failed subjects
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_passfail` "
			+ " WHERE "
				+ " `sapid` = ? "
			+ " AND `isResultLive` = 'Y' "
			+ " AND `isPass` = 'N'";
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapid },
			Integer.class
		);
		
		if(count == 0) {
			return false;
		}
		return true;
	}
	
	@Transactional(readOnly = true)
	public MBAWXConfigurationBean getReRegSettings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Get number of failed subjects
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`mba_wx_exam_live_setting` "
			+ " WHERE `type` = 'Exam Re-Registration' AND `consumerProgramStructureId` IN (111, 151)";
		
		try {
			MBAWXConfigurationBean bean = jdbcTemplate.queryForObject(
				sql, 
				new BeanPropertyRowMapper<MBAWXConfigurationBean>(MBAWXConfigurationBean.class)
			);
			return bean;
		} catch(Exception e) {
			
		}

		return new MBAWXConfigurationBean();
	}
	
	@Transactional(readOnly = false)
	public void upsertReRegistrationRecord( MBAWXConfigurationBean configurationBean ) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Get number of failed subjects
		String sql = ""
			+ " INSERT INTO `exam`.`mba_wx_exam_live_setting` ("
				+ " `acadsYear`,`acadsMonth`, "
				+ " `examYear`, `examMonth`, "
				+ " `consumerProgramStructureId`,`type`, "
				+ " `startTime`,`endTime`, "
				+ " `created_at`,`updated_at` "
			+ " ) "
			+ " VALUES ("
				+ " ?, ?, "
				+ " ?, ?, "
				+ " ?, ?, "
				+ " ?, ?, "
				+ " sysdate(), sysdate() "
			+ " )"
			+ " ON DUPLICATE KEY UPDATE "
			+ " `startTime` = ?, `endTime` = ?, "
			+ " `updated_at` = sysdate() ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				configurationBean.getAcadsYear(), configurationBean.getAcadsMonth(),
				configurationBean.getExamYear(), configurationBean.getExamMonth(),
				configurationBean.getConsumerProgramStructureId(), configurationBean.getType(),
				configurationBean.getStartTime(), configurationBean.getEndTime(),
				configurationBean.getStartTime(), configurationBean.getEndTime()
			}
		);
	}
	
	@Transactional(readOnly = true)
	public MBAWXConfigurationBean getReRegistrationBeanForSapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Get list of failed subjects
		String sql = ""
			+ " SELECT mels.* "
			+ " FROM `exam`.`students` `s` "

		    /* get Acad Year/Month details */
		    + " INNER JOIN  ( "
		    	+ " SELECT "
			    	+ " MAX(`sem`) as `currentSem`, "
			    	+ " `month` AS `acadsMonth`, "
			    	+ " `year` AS `acadsYear`, "
			    	+ " `sapid` "
		    	+ " FROM  `exam`.`registration` "
		    	+ " GROUP BY `sapid` "
		    + " ) `reg` "
		    + " ON `s`.`sapid` = `reg`.`sapid` "

		    /* get Exam Year/Month details */
				/* get timebound ids for student */
			    + " LEFT JOIN `lti`.`timebound_user_mapping` `tum` "
			    + " ON `tum`.`userId` = `s`.`sapid` "

				/* get ssc list for student */
			    + " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

		    /* get Live Settings details */
		    + " RIGHT JOIN `exam`.`mba_wx_exam_live_setting` `mels` "
		    + " ON ( "
			    	+ " `s`.`consumerProgramStructureId` = `mels`.`consumerProgramStructureId` "
			    + " AND `mels`.`acadsYear` = `reg`.`acadsYear` "
			    + " AND `mels`.`acadsMonth` = `reg`.`acadsMonth` "
			    + " AND `mels`.`examYear` = `ssc`.`examYear` "
			    + " AND `mels`.`examMonth` = `ssc`.`examMonth` "
		    + " ) "
			+ " WHERE "
				+ " `s`.`sapid` = ? "
				+ " AND (sysdate() BETWEEN `mels`.`startTime` AND `mels`.`endTime`)"
				+ " AND `mels`.`type` = 'Exam Re-Registration' "
				+ " LIMIT 1 ";
		MBAWXConfigurationBean conf = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapid },
			new BeanPropertyRowMapper<MBAWXConfigurationBean>(MBAWXConfigurationBean.class)
		);
		return conf;
	}
	
}
