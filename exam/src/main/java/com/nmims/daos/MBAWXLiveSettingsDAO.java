package com.nmims.daos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.MBAWXExamRegistrationExtensionBean;
import com.nmims.beans.StudentSubjectConfigExamBean;

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

	@Transactional(readOnly = true)
	public List<MBALiveSettings> getMBALiveSettingsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " "
				+ " SELECT "
					+ " `els`.`id` AS `id`, "
					+ " `els`.`type` AS `type`, "
					+ " `ct`.`name` AS `consumerType`, "
					+ " `p`.`code` AS `program`, "
					+ " `ps`.`program_structure` AS `programStructure`, "
					+ " `els`.`acadsYear` AS `acadsYear`, "
					+ " `els`.`acadsMonth` AS `acadsMonth`, "
					+ " `els`.`examYear` AS `examYear`, "
					+ " `els`.`examMonth` AS `examMonth`, "
					+ " `els`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
					+ " DATE(`els`.`startTime`) AS `startDateStr`, "
					+ " DATE(`els`.`endTime`) AS `endDateStr`, "
					+ " TIME(`els`.`startTime`) AS `startTimeStr`, "
					+ " TIME(`els`.`endTime`) AS `endTimeStr` "
				+ " FROM `exam`.`mba_wx_exam_live_setting` `els` "
				+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `els`.`consumerProgramStructureId` = `cps`.`id` "
				+ " INNER JOIN `exam`.`consumer_type` `ct` ON `ct`.`id` = `cps`.`consumerTypeId` "
				+ " INNER JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " INNER JOIN `exam`.`program` `p` ON `p`.`id` = `cps`.`programId` ";

		List<MBALiveSettings> settingsList = jdbcTemplate.query(
			sql, 
			new BeanPropertyRowMapper<MBALiveSettings>(MBALiveSettings.class)
		);
		
		return settingsList;
	}

	@Transactional(readOnly = true)
	public String getConsumerProgramStructure( String programStructure, String program ) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " SELECT `cps`.`id` "
				+ " FROM `exam`.`consumer_program_structure` `cps` "
				+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `ct`.`id` = `cps`.`consumerTypeId` "
				+ " LEFT JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " LEFT JOIN `exam`.`program` `p` ON `p`.`id` = `cps`.`programId` "
				+ " WHERE `ct`.`name` = 'Retail' AND `ps`.`program_structure` = ? AND `p`.`code` = ? "
				+ " LIMIT 1 ";
		
		try {
			String consumerProgramStructureId = jdbcTemplate.queryForObject(
				sql,
				new Object[] { programStructure, program },
				String.class
			);
			return consumerProgramStructureId;
		} catch (Exception e) {
			
			return null;
		}
	}

	public void upsertLiveSetting(MBALiveSettings mbawxLiveSetting) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " INSERT INTO "
				+ " `exam`.`mba_wx_exam_live_setting` ( "
					+ " `acadsYear`, `acadsMonth`, "
					+ " `examYear`, `examMonth`, "
					+ " `consumerProgramStructureId`, `type`, "
					+ " `startTime`, `endTime`, "
					+ " `createdBy`, `lastModifiedBy` "
				+ " ) VALUES ( "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ? "
				+ " ) "
				+ " ON DUPLICATE KEY UPDATE "
				+ " `startTime` = ?, `endTime` = ?, "
				+ " `acadsYear` = ?, `acadsMonth` = ?, "
				+ " `examYear` = ?, `examMonth` = ?, "
				+ " `lastModifiedBy` = ? ";
	
		jdbcTemplate.update(
			sql,
			new Object[] {
				mbawxLiveSetting.getAcadsYear(), mbawxLiveSetting.getAcadsMonth(),
				mbawxLiveSetting.getExamYear(), mbawxLiveSetting.getExamMonth(),
				mbawxLiveSetting.getConsumerProgramStructureId(), mbawxLiveSetting.getType(),
				mbawxLiveSetting.getStartTime(), mbawxLiveSetting.getEndTime(),
				mbawxLiveSetting.getCreatedBy(), mbawxLiveSetting.getLastModifiedBy(),
				mbawxLiveSetting.getStartTime(), mbawxLiveSetting.getEndTime(), 
				mbawxLiveSetting.getAcadsYear(), mbawxLiveSetting.getAcadsMonth(),
				mbawxLiveSetting.getExamYear(), mbawxLiveSetting.getExamMonth(),
				mbawxLiveSetting.getLastModifiedBy()
			}
		);
	}
	
	public void upsertLiveSettingHistory(MBALiveSettings mbawxLiveSetting) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " INSERT INTO "
				+ " `exam`.`mba_wx_exam_live_setting_history` ("
					+ " `acadsYear`, `acadsMonth`, "
					+ " `examYear`, `examMonth`, "
					+ " `consumerProgramStructureId`, `type`, "
					+ " `startTime`, `endTime`, "
					+ " `createdBy`, `lastModifiedBy` "
				+ " ) VALUES ( "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " ?, ? "
				+ " ) "
				+ " ON DUPLICATE KEY UPDATE "
				+ " `startTime` = ?, `endTime` = ?, `lastModifiedBy` = ? ";
	
		jdbcTemplate.update(
			sql,
			new Object[] {
				mbawxLiveSetting.getAcadsYear(), mbawxLiveSetting.getAcadsMonth(),
				mbawxLiveSetting.getExamYear(), mbawxLiveSetting.getExamMonth(),
				mbawxLiveSetting.getConsumerProgramStructureId(), mbawxLiveSetting.getType(),
				mbawxLiveSetting.getStartTime(), mbawxLiveSetting.getEndTime(),
				mbawxLiveSetting.getCreatedBy(), mbawxLiveSetting.getLastModifiedBy(),
				mbawxLiveSetting.getStartTime(), mbawxLiveSetting.getEndTime(), 
				mbawxLiveSetting.getLastModifiedBy()
			}
		);
	}

	public String deleteLiveSetting(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " DELETE FROM `exam`.`mba_wx_exam_live_setting` WHERE ID = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { id }
			);
			return "Successfully deleted setting";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}

	@Transactional(readOnly = true)
	public MBALiveSettings getLiveSettingsById(Long id) {
		String sql = ""
				+ " SELECT "
					+ " `els`.`id` AS `id`, "
					+ " `els`.`type` AS `type`, "
					+ " `ct`.`name` AS `consumerType`, "
					+ " `p`.`code` AS `program`, "
					+ " `ps`.`program_structure` AS `programStructure`, "
					+ " `els`.`acadsYear` AS `acadsYear`, "
					+ " `els`.`acadsMonth` AS `acadsMonth`, "
					+ " `els`.`examYear` AS `examYear`, "
					+ " `els`.`examMonth` AS `examMonth`, "
					+ " `els`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
					+ " DATE(`els`.`startTime`) AS `startDateStr`, "
					+ " DATE(`els`.`endTime`) AS `endDateStr`, "
					+ " TIME(`els`.`startTime`) AS `startTimeStr`, "
					+ " TIME(`els`.`endTime`) AS `endTimeStr` "
				+ " FROM `exam`.`mba_wx_exam_live_setting` `els` "
				+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `els`.`consumerProgramStructureId` = `cps`.`id` "
				+ " INNER JOIN `exam`.`consumer_type` `ct` ON `ct`.`id` = `cps`.`consumerTypeId` "
				+ " INNER JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " INNER JOIN `exam`.`program` `p` ON `p`.`id` = `cps`.`programId` "
				
				+ " WHERE `els`.`id` = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		MBALiveSettings liveSettings = jdbcTemplate.queryForObject(
			sql,
			new Object[] { id },
			new BeanPropertyRowMapper<MBALiveSettings>(MBALiveSettings.class)
		);
		return liveSettings;
	}

	public void updateLiveSettings(MBALiveSettings liveSettings) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_wx_exam_live_setting` "
				+ " SET "
					+ " `startTime` = ?, "
					+ " `endTime` = ?, "
					+ " `lastModifiedBy` = ? "
				+ " WHERE `id` = ? ";
		
		jdbcTemplate.update(
			sql, 
			new Object[] {
				liveSettings.getStartTime(), liveSettings.getEndTime(), liveSettings.getLastModifiedBy(), 
				liveSettings.getId()
			}
		);
	}

	@Transactional(readOnly = true)
	public boolean checkIfLiveSettingExists(MBALiveSettings liveSettings) {
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM `exam`.`mba_wx_exam_live_setting` `els` "
				+ " WHERE "
					+ " `acadsYear` = ? "
				+ " AND `acadsMonth`= ? "
				+ " AND `examYear`= ? "
				+ " AND `examMonth`= ? "
				+ " AND `consumerProgramStructureId`= ? "
				+ " AND `type` = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = jdbcTemplate.queryForObject(
			sql,
			new Object[] { 
				liveSettings.getAcadsYear(), liveSettings.getAcadsMonth(),
				liveSettings.getExamYear(), liveSettings.getExamMonth(), 
				liveSettings.getConsumerProgramStructureId(), liveSettings.getType()
			},
			Integer.class
		);
		return count > 0;
	}

	@Transactional(readOnly = true)
	public List<StudentSubjectConfigExamBean> getAllStudentSubjectConfig() {
		String sql = " SELECT * FROM `lti`.`student_subject_config` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentSubjectConfigExamBean> sscList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
		);
		return sscList;
	}

	@Transactional(readOnly = true)
	public List<String> getAllProgramStructures() {
		String sql = " SELECT `program_structure` FROM `exam`.`program_structure` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> programStructures = jdbcTemplate.queryForList( sql, String.class );
		
		return programStructures;
	}
	
	@Transactional(readOnly = true)
	public List<MBAWXExamRegistrationExtensionBean> getAllExtendedRegistrationStudents() {

		List<MBAWXExamRegistrationExtensionBean> beans = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM `exam`.`mba_wx_registration_extension` WHERE `active` = 'Y' ORDER BY `lastModifiedDate` DESC";

		beans = jdbcTemplate.query(sql, new BeanPropertyRowMapper<MBAWXExamRegistrationExtensionBean>(
				MBAWXExamRegistrationExtensionBean.class));
		
		return beans;
	}
	
	@Transactional(readOnly = false)
	public int insertIntoRegistrationExtensionTable(MBAWXExamRegistrationExtensionBean extensionBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO `exam`.`mba_wx_registration_extension` " + 
				"(`sapid`, `consumerProgramStructureId`, `examYear`, `examMonth`, `extendStartDateTime`, `extendEndDateTime`,"
				+ " `createdDate`, `createdBy`,`lastModifiedBy`,`lastModifiedDate`,`active`) " + 
				" VALUES (?, ?, ?, ?, ?, ?, SYSDATE(), ?, ? , SYSDATE(), 'Y')";
		
		return jdbcTemplate.update(sql,
				new Object[] { extensionBean.getSapid(),
						extensionBean.getConsumerProgramStructureId(),
						extensionBean.getExamYear(),
						extensionBean.getExamMonth(),
						extensionBean.getExtendStartDateTime(),
						extensionBean.getExtendEndDateTime(),
						extensionBean.getCreatedBy(),
						extensionBean.getCreatedBy()
						});
	}
	
	@Transactional(readOnly = true)
	public String returnMasterKeyifStudentIsTimebound(MBAWXExamRegistrationExtensionBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    `pss`.`consumerProgramStructureId` " + 
				"FROM " + 
				"    `lti`.`timebound_user_mapping` `tum` " + 
				"        INNER JOIN " + 
				"    `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `tum`.`timebound_subject_config_id` " + 
				"        INNER JOIN " + 
				"    `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` " + 
				"WHERE " + 
				"    `userId` = ? " + 
				"        AND `ssc`.`examYear` = ? " + 
				"        AND `ssc`.`examMonth` = ? LIMIT 1";
		
		return jdbcTemplate.queryForObject(sql, new Object[] 
				{
						bean.getSapid(),
						bean.getExamYear(),
						bean.getExamMonth()
						},
				String.class );
	}
	
	@Transactional(readOnly = false)
	public int deleteRegistrationExtension(MBAWXExamRegistrationExtensionBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE `exam`.`mba_wx_registration_extension` " + 
				"SET " + 
				"    `active` = 'N', " + 
				"    `lastModifiedDate` = SYSDATE(), " + 
				"    `lastModifiedBy` = ? " + 
				"WHERE " + 
				"    (`sapid` = ? ) " + 
				"        AND (`examYear` = ? ) " + 
				"        AND (`examMonth` = ?) " + 
				"        AND (`extendEndDateTime` = ? ) " + 
				"        AND (`active` = 'Y')";
		
		return jdbcTemplate.update(sql, new Object[] 
				{ 
					bean.getLastModifiedBy(),
					bean.getSapid(),
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getExtendEndDateTime()
				});
	}
	
	@Transactional(readOnly = true)
	public boolean isRegistrationExtendedForStudent(String sapid, String examMonth, String examYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT count(sapid) FROM " + 
				"	`exam`.`mba_wx_registration_extension` " + 
				"WHERE " + 
				"	`sapid` = ? " + 
				"	AND `examMonth` = ? " + 
				"    AND `examYear` = ? " + 
				"    AND `active` = 'Y' " + 
				"    AND CURRENT_TIMESTAMP() BETWEEN `extendStartDateTime` AND `extendEndDateTime` ";
		
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] {sapid, examMonth, examYear}, Integer.class);
		
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public MBALiveSettings liveSettingsTypeStartEndTime(final String acadYear, final String acadMonth, final String examYear, final String examMonth, 
														final Integer consumerProgramStructureId, final String type) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT type, startTime, endTime " + 
						"FROM exam.mba_wx_exam_live_setting " + 
						"WHERE acadsYear = ?" + 
						"	AND acadsMonth = ?" + 
						"	AND examYear = ?" + 
						"	AND examMonth = ?" + 
						"	AND consumerProgramStructureId = ?" + 
						"	AND type = ?" + 
						"	AND (sysdate() BETWEEN startTime AND endTime)";
		
		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(MBALiveSettings.class), acadYear, acadMonth, 
											examYear, examMonth, consumerProgramStructureId, type);
	}
}
