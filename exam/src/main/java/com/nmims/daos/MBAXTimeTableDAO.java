package com.nmims.daos;



import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBATimeTableBean;

@Repository("mbaxTimeTableDAO")
public class MBAXTimeTableDAO extends BaseDAO {
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
	public List<MBATimeTableBean> getUpcomingTimeTableList(){
		String sql = ""
				+ " SELECT "
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`examStartDateTime`, "
				+ " `tt`.`examEndDateTime`, "
				
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime` "

				+ " FROM `exam`.`mba_x_time_table` `tt` "

				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `tt`.`programSemSubjectId` "
				
				+ " WHERE `tt`.`examStartDateTime` >= CURDATE() ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBATimeTableBean> timeTableList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MBATimeTableBean>(MBATimeTableBean.class)
		);
		return timeTableList;
	}
	
	@Transactional(readOnly = true)
	public List<MBATimeTableBean> getTimeTableList(){
		String sql = ""
				+ " SELECT  "
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`scheduleId`, "
				+ " `tt`.`examStartDateTime`,  "
				+ " `tt`.`examEndDateTime`,  "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime` "

				+ " FROM `exam`.`mba_x_time_table` `tt` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `tt`.`programSemSubjectId` ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBATimeTableBean> timeTableList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MBATimeTableBean>(MBATimeTableBean.class)
		);
		return timeTableList;
	}
	
	@Transactional(readOnly = false)
	public String deleteTimeTableById(String timeTableId){
		try {
			String sql = "delete from exam.mba_x_time_table where timeTableId=?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = jdbcTemplate.update(sql, new Object[] {
					timeTableId
			});
			if(count > 0) {
				return "Successfully record deleted, centerId: " + timeTableId;
			}
			return "Failed to delete record, centerId: " + timeTableId;
		}
		catch (Exception e) {
			
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public List<MBATimeTableBean> batchInsertTimeTables(final List<MBATimeTableBean> timeTableBeansList) {
		// Insert tracking numbers for current interaction
		String sql = ""
				+ " INSERT INTO `exam`.`mba_x_time_table` "
				+ " ( "
					+ " `examStartDateTime`, `examEndDateTime`, `programSemSubjectId`, "
					+ " `examMonth`, `examYear`, "
					+ " `createdBy`, `lastModifiedBy` "
				+ " ) "
				+ " VALUES "
				+ " ( "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " ?, ? "
				+ " ) ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		List<MBATimeTableBean> toReturn = new ArrayList<MBATimeTableBean>();
		for (MBATimeTableBean bean : timeTableBeansList) {
			try {
				jdbcTemplate.update(
					sql, 
					new Object[] {
						bean.getExamStartDateTime(), bean.getExamEndDateTime(), bean.getProgramSemSubjectId(),
						bean.getExamMonth(), bean.getExamYear(),
						bean.getCreatedBy(), bean.getLastModifiedBy()
					}
				);
			}catch (Exception e) {
				
				bean.setError("Error inserting record : " + e.getLocalizedMessage());
			}
			toReturn.add(bean);
		}
		return toReturn;
	}

	@Transactional(readOnly = true)
	public boolean checkIfTimeTableExists(MBATimeTableBean timeTable) {
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM `exam`.`mba_x_time_table` "
				+ " WHERE "
					+ " `programSemSubjectId` = ? "
				+ " AND `examMonth` = ? "
				+ " AND `examYear` = ? "
				+ " AND `examStartDateTime` = ? "
				+ " AND `examEndDateTime` = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = jdbcTemplate.queryForObject(
				sql,
				new Object[] {
					timeTable.getProgramSemSubjectId(), 
					timeTable.getExamMonth(), 
					timeTable.getExamYear(),
					timeTable.getExamStartDateTime(), 
					timeTable.getExamEndDateTime()
				},
				Integer.class
			);
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public MBATimeTableBean getTimeTableById(Long timeTableId) {
		String sql = ""
				+ " SELECT  "
				+ " `tt`.`timeTableId`, "
				+ " `tt`.`examMonth`, "
				+ " `tt`.`examYear`, "
				+ " `tt`.`scheduleId`, "
				+ " `tt`.`examStartDateTime`,  "
				+ " `tt`.`examEndDateTime`,  "
				+ " `pss`.`subject` AS `subjectName`, "
				+ " `pss`.`sem` AS `term`, "
				+ " DATE(`tt`.`examStartDateTime`) AS `examDate`, "
				+ " TIME(`tt`.`examStartDateTime`) AS `examStartTime`, "
				+ " TIME(`tt`.`examEndDateTime`) AS `examEndTime` "

				+ " FROM `exam`.`mba_x_time_table` `tt` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `tt`.`programSemSubjectId` "
				
				+ " WHERE `timeTableId` = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		MBATimeTableBean timeTable = jdbcTemplate.queryForObject(
			sql,
			new Object[] { timeTableId },
			new BeanPropertyRowMapper<MBATimeTableBean>(MBATimeTableBean.class)
		);
		return timeTable;
	}
	
	@Transactional(readOnly = false)
	public void updateTimeTable(MBATimeTableBean timeTableBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_x_time_table` "
				+ " SET "
					+ " `examStartDateTime` = ?, "
					+ " `examEndDateTime` = ?, "
					+ " `lastModifiedBy` = ? "
				+ " WHERE `timeTableId` = ? ";
		
		jdbcTemplate.update(
			sql, 
			new Object[] {
				timeTableBean.getExamStartDateTime(), timeTableBean.getExamEndDateTime(), timeTableBean.getLastModifiedBy(), 
				timeTableBean.getTimeTableId()
			}
		);
	}

	@Transactional(readOnly = true)
	public Long getProgramSemSubjectId(MBATimeTableBean bean) {
		try {
			String sql = ""
					+ " SELECT "
					
					+ " `pss`.`id` "
					
					+ " FROM `lti`.`student_subject_config` `ssc` "
					
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
					
					+ " INNER JOIN `exam`.`consumer_program_structure` `cps` "
					+ " ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
					
					+ " INNER JOIN `exam`.`program` `p` "
					+ " ON `cps`.`programId` = `p`.`id` "
					
					+ " WHERE "
						+ " `pss`.`sem` = ? "
					+ " AND `pss`.`subject` = ? "
					+ " AND `ssc`.`examMonth` = ? "
					+ " AND `ssc`.`examYear` = ? "
					+ " AND `p`.`code` = 'MBA - X' "
					
					+ " GROUP BY `pss`.`id` ";
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			Long timeboundId = jdbcTemplate.queryForObject(
					sql,
					new Object[] {
						bean.getTerm(), bean.getSubjectName(), 
						bean.getExamMonth(), bean.getExamYear()
					},
					Long.class
				);
			return timeboundId;
		} catch (Exception e) {
			
			return null;
		}
	}

}
