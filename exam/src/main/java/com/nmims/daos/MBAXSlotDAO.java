package com.nmims.daos;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.MBASlotBean;

@Repository("mbaxSlotDAO")
public class MBAXSlotDAO extends BaseDAO {
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
	public List<BatchExamBean> getListOfBatches(){
		String sql = " SELECT * FROM `exam`.`batch` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<BatchExamBean> batchList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class)
		);
		return batchList;
	}
	
	@Transactional(readOnly = true)
	public List<MBASlotBean> getSlotsListByTimeTableId(String slotId){

		String sql = ""
			+ " SELECT "
				+ " `slot`.*, "
			
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime`, "
				
				+ " `centers`.`name` AS `centerName` "
			
			+ " FROM `exam`.`mba_x_slots` `slot` "

			+ " INNER JOIN `exam`.`mba_x_time_table` `tt` "
			+ " ON `slot`.`timeTableId` = `tt`.`timeTableId` "

			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `tt`.`programSemSubjectId` "
			
			+ " INNER JOIN `exam`.`mba_x_centers` `centers` "
			+ " ON `slot`.`centerId` = `centers`.`centerId` "
				+ " WHERE `slot`.`timeTableId` = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBASlotBean> mbaxCentersBeansList = jdbcTemplate.query(
			sql,
			new Object[] { slotId },
			new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
		);
		return mbaxCentersBeansList;
	}
	
	@Transactional(readOnly = true)
	public List<MBASlotBean> getSlotsList(){
		String sql = ""
			+ " SELECT "
				+ " `slot`.*, "
			
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime`, "
				
				+ " `centers`.`name` AS `centerName` "
			
			+ " FROM `exam`.`mba_x_slots` `slot` "

			+ " INNER JOIN `exam`.`mba_x_time_table` `tt` "
			+ " ON `slot`.`timeTableId` = `tt`.`timeTableId` "

			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `tt`.`programSemSubjectId` "
			
			+ " INNER JOIN `exam`.`mba_x_centers` `centers` "
			+ " ON `slot`.`centerId` = `centers`.`centerId` ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBASlotBean> mbaxCentersBeansList = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
				);
		return mbaxCentersBeansList;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfSlotExists(Long centerId, Long timeTableId){
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_x_slots` `slot` "
			+ " WHERE `centerId` = ? AND `timeTableId` = ? ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = jdbcTemplate.queryForObject(
					sql,
					new Object[] { centerId, timeTableId },
					Integer.class
				);
		return count > 0;
	}
	
	@Transactional(readOnly = false)
	public void createSlot(MBASlotBean slot) {
		// Insert tracking numbers for current interaction
		String sql = " "
				+ " INSERT INTO `exam`.`mba_x_slots` "
				+ " ( "
					+ " `centerId`, `timetableId`, `capacity`, `active`, "
					+ " `createdBy`, `lastModifiedBy` "
				+ " ) "
				+ " VALUES "
				+ " ( "
					+ " ?, ?, ?, ?, "
					+ " ?, ? "
				+ " ) ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(
			sql, 
			new Object[] {
				slot.getCenterId(), slot.getTimeTableId(), slot.getCapacity(), slot.getActive(),
				slot.getCreatedBy(), slot.getLastModifiedBy()
			}
		);
	}
	
	@Transactional(readOnly = false)
	public void updateSlot(MBASlotBean slotBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_x_slots` "
				+ " SET `capacity` = ?, `active` = ?, `lastModifiedBy` = ? "
				+ " WHERE `slotId` = ? ";
		
		jdbcTemplate.update(
			sql, 
			new Object[] { 
				slotBean.getCapacity(), slotBean.getActive(), slotBean.getLastModifiedBy(), 
				slotBean.getSlotId() 
			}
		);
	}
	
	@Transactional(readOnly = true)
	public MBASlotBean getSlotById(Long slotId) {
		String sql = ""
			+ " SELECT "
				+ " `slot`.*, "
				
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime`, "
				
				+ " `centers`.`name` AS `centerName` "
			
			+ " FROM `exam`.`mba_x_slots` `slot` "
	
			+ " INNER JOIN `exam`.`mba_x_time_table` `tt` "
			+ " ON `slot`.`timeTableId` = `tt`.`timeTableId` "
	
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `pss`.`id` = `tt`.`programSemSubjectId` "
			
			+ " INNER JOIN `exam`.`mba_x_centers` `centers` "
			+ " ON `slot`.`centerId` = `centers`.`centerId` "
			
			+ " WHERE `slot`.`slotId` = ? ";
		

		jdbcTemplate = new JdbcTemplate(dataSource);
		MBASlotBean slot = jdbcTemplate.queryForObject(
			sql,
			new Object[] { slotId },
			new BeanPropertyRowMapper<MBASlotBean>(MBASlotBean.class)
		);
		return slot;
	}
	
	@Transactional(readOnly = false)
	public String deleteSlotById(String slotId){
		try {
			String sql = "DELETE FROM `exam`.`mba_x_slots` WHERE `slotId` = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = jdbcTemplate.update(
				sql, 
				new Object[] { slotId }
			);
			if(count > 0) {
				return "Successfully record deleted, slotId: " + slotId;
			}
			return "Failed to delete record, slotId: " + slotId;
		}
		catch (Exception e) {
			
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public void toggleSlotActive(String slot, String active, String userId) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_x_slots` "
				+ " SET `active` = ?, `lastModifiedBy` = ? "
				+ " WHERE `slotId` = ? ";
		
		jdbcTemplate.update(
				sql, 
				new Object[] { active, userId, slot }
		);
	}
}
