package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;

public class TestDAO  extends BaseDAO{
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

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
	public List<Long> getUpgradModuleIdByProgramConfigYearMonthBatchIdNId(String consumerTypeId, String programStructureId,
			String programId, Integer acadYear, String acadMonth, long referenceId,String subject, Integer batchId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<Long> moduleIdList = new ArrayList<>();
		

		String sql =  "SELECT distinct m.sessionModuleNo "
				+ " FROM exam.consumer_program_structure as c_p_s, "
				+ "exam.program_sem_subject as p_s_s, "
				+ " lti.student_subject_config ssc ," + 
				"    acads.upgrad_sessionplanid_timeboundid_mapping stm, " + 
				"    acads.upgrad_sessionplan s, " + 
				"    acads.upgrad_sessionplan_module m "  
				+ "where "
				+ "c_p_s.programId in ("+ programId +") "
				+ "and c_p_s.programStructureId in ("+ programStructureId +") "
				+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
				+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
				+ " and ssc.prgm_sem_subj_id = p_s_s.id "+ 
				"    and ssc.id = stm.timeboundId " + 
				"    AND s.id = stm.sessionPlanId " + 
				"    AND s.id = m.sessionPlanId "  
				+ " "
				+ " and ssc.acadYear ="+acadYear+" and acadMonth='"+acadMonth+"' ";
		

		if(batchId == 0) {
			//Do nothing, As we'll get all subjects by program config
		}else {
			sql += " and ssc.batchId = "+batchId+" ";
		}
		
		
		if("All".equalsIgnoreCase(subject)) {
			//Do nothing
		}else {
			sql += " and p_s_s.subject = '"+subject+"' ";
		}
		
		
			if( referenceId == 0) {
				//Do nothing
			}else {
				sql += " and m.sessionModuleNo = "+referenceId+" ";
			}
		
		
		
		try {
			moduleIdList = (List<Long>) jdbcTemplate.query(sql, new SingleColumnRowMapper(Long.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		return moduleIdList;
		
	}
	

	@Transactional(readOnly = true)
	public ArrayList<String> getUpgradProgramSemSubjectIdsBySubjectNProgramConfig(String programId,
			String programStructureId,
			String consumerTypeId,
			String subject) {
jdbcTemplate = new JdbcTemplate(dataSource);

ArrayList<Object> parameters = new ArrayList<Object>();

String sql =  "SELECT p_s_s.id "
+ " FROM exam.consumer_program_structure as c_p_s, "
+ "		 exam.program_sem_subject as p_s_s "
+ "where c_p_s.programId in ("+ programId +") "
+ "and c_p_s.programStructureId in ("+ programStructureId +") "
+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
+ "and c_p_s.id = p_s_s.consumerProgramStructureId ";

if("All".equalsIgnoreCase(subject)) {
// Do nothing get all PSS IDs
}else {
sql += " and p_s_s.subject=?";
parameters.add(subject);

}
Object[] args = parameters.toArray();

ArrayList<String> programSemSubjectIds = new  ArrayList<>();
try {
programSemSubjectIds = (ArrayList<String>) jdbcTemplate.query(
sql, args,  new SingleColumnRowMapper(
String.class));
} catch (DataAccessException e) {
// TODO Auto-generated catch block
  
}


return programSemSubjectIds;
}
	
	

	@Transactional(readOnly = true)
	public List<Long> getUpgradTimeboundIdsByProgramSemSubjectIdsNBatchId(ArrayList<String> programSemSubjectIds, Integer acadYear, String acadMonth, Integer batchId) {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
		String sql =  "SELECT id FROM lti.student_subject_config "
				+ " where prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
				+ " and acadYear = "+acadYear+" and acadMonth = '"+acadMonth+"' ";
		
		if(batchId == 0) {
			//Do nothing, we'll get All batches as "0" means "All" 
		}else {
			sql += " and batchId = "+batchId+" ";
		}

		List<Long> timeboundIds= new ArrayList<>();
		try {
			timeboundIds = (List<Long>) jdbcTemplate.query(
					sql,  new SingleColumnRowMapper(Long.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		
		return timeboundIds;
	}
		
}
