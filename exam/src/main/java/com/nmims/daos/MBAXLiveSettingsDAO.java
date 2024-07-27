package com.nmims.daos;


import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.StudentSubjectConfigExamBean;

public class MBAXLiveSettingsDAO extends BaseDAO{

	
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
				+ " FROM `exam`.`mba_x_exam_live_setting` `els` "
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

	public String getConsumerProgramStructure( String programStructure ) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " SELECT `cps`.`id` "
				+ " FROM `exam`.`consumer_program_structure` `cps` "
				+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `ct`.`id` = `cps`.`consumerTypeId` "
				+ " LEFT JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " LEFT JOIN `exam`.`program` `p` ON `p`.`id` = `cps`.`programId` "
				+ " WHERE `ct`.`name` = 'Retail' AND `ps`.`program_structure` = ? AND `p`.`code` = 'MBA - X' "
				+ " LIMIT 1 ";
		
		try {
			String consumerProgramStructureId = jdbcTemplate.queryForObject(
				sql,
				new Object[] { programStructure },
				String.class
			);
			return consumerProgramStructureId;
		} catch (Exception e) {
			
			return null;
		}
	}

	public void upsertLiveSetting(MBALiveSettings mbaxLiveSetting) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " INSERT INTO "
				+ " `exam`.`mba_x_exam_live_setting` ( "
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
				mbaxLiveSetting.getAcadsYear(), mbaxLiveSetting.getAcadsMonth(),
				mbaxLiveSetting.getExamYear(), mbaxLiveSetting.getExamMonth(),
				mbaxLiveSetting.getConsumerProgramStructureId(), mbaxLiveSetting.getType(),
				mbaxLiveSetting.getStartTime(), mbaxLiveSetting.getEndTime(),
				mbaxLiveSetting.getCreatedBy(), mbaxLiveSetting.getLastModifiedBy(),
				mbaxLiveSetting.getStartTime(), mbaxLiveSetting.getEndTime(), 
				mbaxLiveSetting.getAcadsYear(), mbaxLiveSetting.getAcadsMonth(),
				mbaxLiveSetting.getExamYear(), mbaxLiveSetting.getExamMonth(),
				mbaxLiveSetting.getLastModifiedBy()
			}
		);
	}
	
	public void upsertLiveSettingHistory(MBALiveSettings mbaxLiveSetting) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " "
				+ " INSERT INTO "
				+ " `exam`.`mba_x_exam_live_setting_history` ("
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
				mbaxLiveSetting.getAcadsYear(), mbaxLiveSetting.getAcadsMonth(),
				mbaxLiveSetting.getExamYear(), mbaxLiveSetting.getExamMonth(),
				mbaxLiveSetting.getConsumerProgramStructureId(), mbaxLiveSetting.getType(),
				mbaxLiveSetting.getStartTime(), mbaxLiveSetting.getEndTime(),
				mbaxLiveSetting.getCreatedBy(), mbaxLiveSetting.getLastModifiedBy(),
				mbaxLiveSetting.getStartTime(), mbaxLiveSetting.getEndTime(), 
				mbaxLiveSetting.getLastModifiedBy()
			}
		);
	}

	public String deleteLiveSetting(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " DELETE FROM `exam`.`mba_x_exam_live_setting` WHERE ID = ? ";
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
				+ " FROM `exam`.`mba_x_exam_live_setting` `els` "
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
				+ " UPDATE `exam`.`mba_x_exam_live_setting` "
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

	public boolean checkIfLiveSettingExists(MBALiveSettings liveSettings) {
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM `exam`.`mba_x_exam_live_setting` `els` "
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

	public List<StudentSubjectConfigExamBean> getAllStudentSubjectConfig() {
		String sql = " SELECT * FROM `lti`.`student_subject_config` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentSubjectConfigExamBean> sscList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<StudentSubjectConfigExamBean>(StudentSubjectConfigExamBean.class)
		);
		return sscList;
	}

	public List<String> getAllProgramStructures() {
		String sql = " SELECT `program_structure` FROM `exam`.`program_structure` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> programStructures = jdbcTemplate.queryForList( sql, String.class );
		
		return programStructures;
	}
}
