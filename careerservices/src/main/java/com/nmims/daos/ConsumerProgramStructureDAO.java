package com.nmims.daos;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.ConsumerProgramStructureCareerservicesBean;

public class ConsumerProgramStructureDAO {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(ConsumerProgramStructureDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<ConsumerProgramStructureCareerservicesBean> getAllConsumerProgramStructures(){
		return getAllConsumerProgramStructuresQuery();
	}
	
	public List<ConsumerProgramStructureCareerservicesBean> getAllConsumerProgramStructuresQuery(){
		String sql = "SELECT "
				+ "`cps`.`id` AS `consumerProgramStructureId`, "
				+ "`cps`.`programId` AS `programId`, "
				+ "`cps`.`programStructureId` AS `programStructureId`, "
				+ "`cps`.`consumerTypeId` AS `consumerTypeId`, "
				+ "`ct`.`name` AS `consumerTypeName`, "
				+ "`ct`.`isCorporate` AS `consumerTypeIsCorporate`, "
				+ "`ps`.`program_structure` AS `programStructureName`, "
				+ "`p`.`code` AS `programCode`, "
				+ "`p`.`name` AS `programName`"
				+ "FROM `exam`.`consumer_program_structure` `cps` "
				//get consumer type table details
					+ "LEFT JOIN "
					+ "`exam`.`consumer_type` `ct` "
					+ "ON "
					+ "`ct`.`id`= `cps`.`consumerTypeId` "
				//End;
				//get program structure table details
					+ "LEFT JOIN "
					+ "`exam`.`program_structure` `ps` "
					+ "ON "
					+ "`ps`.`id`= `cps`.`programStructureId` "
				//End;
				//get program table details
					+ "LEFT JOIN "
					+ "`exam`.`program` `p` "
					+ "ON "
					+ "`p`.`id`= `cps`.`programId` ";
				//End;
		
		try {

			List<ConsumerProgramStructureCareerservicesBean> courseDetailsList = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<ConsumerProgramStructureCareerservicesBean>(ConsumerProgramStructureCareerservicesBean.class)
			);
			return courseDetailsList;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}
	

	public List<ConsumerProgramStructureCareerservicesBean> getAllProgramStructuresQuery(){
		String sql = "SELECT "
				+ "`id` AS `programStructureId`, "
				+ "`program_structure` AS `programStructureName` "
				+ "FROM "
					+ "`exam`.`program_structure`";
		
		try {

			List<ConsumerProgramStructureCareerservicesBean> courseDetailsList = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<ConsumerProgramStructureCareerservicesBean>(ConsumerProgramStructureCareerservicesBean.class)
			);
			return courseDetailsList;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}

	public List<ConsumerProgramStructureCareerservicesBean> getConsumerTypeList(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureCareerservicesBean> consumerTypes = null;
		
		;
		
		String sql =  "SELECT "
				+ "`id` AS `consumerTypeId`,"
				+ "`name` AS `consumerTypeName` "
				+ "FROM exam.consumer_type";
		
		try {
			consumerTypes = jdbcTemplate.query(
					sql,
					new BeanPropertyRowMapper<ConsumerProgramStructureCareerservicesBean>(ConsumerProgramStructureCareerservicesBean.class)
			);
			
		} catch (Exception e) {
			
			logger.info("exception : "+e.getMessage());
			return null;
		}
		
		return consumerTypes;  
		
	}

}
