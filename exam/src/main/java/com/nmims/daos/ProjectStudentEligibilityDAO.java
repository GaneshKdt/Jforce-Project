package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProjectStudentEligibilityDAO extends BaseDAO{
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
	public String getProgramSemSubjectIdForStudent(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pss`.`id` "
			+ " FROM `exam`.`students` `s` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`consumerProgramStructureId` = `s`.`consumerProgramStructureId`"
			+ " WHERE `sapid` = ? "
			+ " AND `subject` = ? ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { sapid, subject }, 
            String.class
	    );
	}
	@Transactional(readOnly = true)
	public boolean checkIfSOPLive(String pssId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ "  ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { pssId }, 
            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfVivaLive(String pssId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ "  ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { pssId }, 
            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfTitleLive(String pssId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ "  ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { pssId }, 
            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfSynopsisLive(String pssId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ "  ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { pssId }, 
            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfProjectSubmissionLive(String pssId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ "  ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { pssId }, 
            Integer.class
	    );
		return count > 0;
	}
	
	
}