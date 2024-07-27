package com.nmims.daos;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.BatchBean;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.DummyUserBean;

public class DummyUsersDAO extends BaseDAO{
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
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
	public ArrayList<DummyUserBean> getDummyUsersByBatchId(String batchId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<DummyUserBean> dummyUsersList = new ArrayList<DummyUserBean>();
		String sql="SELECT  " + 
				"    tum.userId, " + 
				"    ssc.batchId, " + 
				"    ssc.acadYear, " + 
				"    ssc.acadMonth, " + 
				"    ssc.examYear, " + 
				"    ssc.examMonth, " + 
				"    b.name AS batchName " + 
				"FROM " + 
				"    lti.timebound_user_mapping tum " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"        INNER JOIN " + 
				"    exam.batch b ON ssc.batchId = b.id " + 
				"WHERE " + 
				"    b.id = ? AND role = 'dummyUser' " + 
				"GROUP BY userId " + 
				"" ;			
		try {
			dummyUsersList = (ArrayList<DummyUserBean>) jdbcTemplate.query(sql, new Object[] {batchId}, new BeanPropertyRowMapper(DummyUserBean.class));
		} catch (DataAccessException e) {
			  
		}
		return dummyUsersList;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<BatchBean> getBatchList(){
		ArrayList<BatchBean> batchList = new ArrayList<BatchBean>();
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql =" select `name` AS `batchName`,`id` AS `batchId` from exam.batch " ;
		try {
		batchList = (ArrayList<BatchBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(BatchBean.class));
		}catch(Exception e) {
			  
		}
		return batchList;
	}


	/*public String getRoleByUserId(String userId) {
=======
	@Transactional(readOnly = true)
	public String getRoleByUserId(String userId) {
>>>>>>> branch 'master' of https://ngasce@bitbucket.org/ngasce/acads.git
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    role " + 
				"FROM " + 
				"    lti.timebound_user_mapping " + 
				"WHERE " + 
				"    userId = ? " + 
				"GROUP BY userId ";
		String role="";
		try {
			role = (String) jdbcTemplate.queryForObject(sql,new Object[]{userId}, new SingleColumnRowMapper(String.class));
		}catch(Exception e) {
			   
		}
		return role;
		
	}*/
	
	public ArrayList<DummyUserBean> getDummyUsers(BatchBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<DummyUserBean> dummyOtherUsersList = new ArrayList<DummyUserBean>();
		ArrayList<DummyUserBean> dummyMasterUsersList = new ArrayList<DummyUserBean>();
		ArrayList<DummyUserBean> dummyLeadUsersList = new ArrayList<DummyUserBean>();
		ArrayList<DummyUserBean> dummyAllUsersList = new ArrayList<DummyUserBean>();
		if(bean.getProgramType().equals("Master")) {
			String sql="SELECT  " + 
					"    tum.userId, " + 
					"    ct.name as consumerType, " + 
					"    ps.programType, " + 
					"    p_s.program_structure as programStructure, " + 
					"    ps.program " + 					
					"FROM " + 
					"    lti.timebound_user_mapping tum " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " +  
					"        INNER JOIN " + 
					"    exam.batch b ON ssc.batchId = b.id " + 
					"        INNER JOIN " +
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " +
					"        INNER JOIN " +
					"    exam.consumer_program_structure cps ON cps.id = pss.consumerProgramStructureId " +
					"        INNER JOIN " +
					"    exam.program_structure p_s ON p_s.id = cps.programStructureId " +					
					"        INNER JOIN " +
					"    exam.programs ps ON ps.consumerProgramStructureId = cps.id " +
					"        INNER JOIN " +
					"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " +
					"WHERE 1=1 ";				
			
			if(bean.getConsumerTypeId()!=null && !("").equals(bean.getConsumerTypeId())) {
				sql += " AND ct.id = "+bean.getConsumerTypeId()+"";
			}
			
			if(bean.getProgramType()!=null && !("").equals(bean.getProgramType())) {
				sql += " AND ps.programType in ( ";
				List<String> values = Arrays.asList(bean.getProgramType().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramStructureId()!=null && !("").equals(bean.getProgramStructureId())) {
				sql += " AND p_s.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramStructureId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramId()!=null && !("").equals(bean.getProgramId())) {
				sql += " AND ps.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getSem()!=null && !("").equals(bean.getSem())) {
				sql += " AND pss.sem in ( ";
				List<String> values = Arrays.asList(bean.getSem().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getBatchId()!=null && !("").equals(bean.getBatchId())) {
				sql += " AND b.id in ( ";
				List<String> values = Arrays.asList(bean.getBatchId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			sql +=" GROUP BY userId";	
			
			try {
				dummyMasterUsersList = (ArrayList<DummyUserBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(DummyUserBean.class));
			} catch (DataAccessException e) {
				  
			}
		} 
		
		if(!bean.getProgramType().equals("Master") && !bean.getProgramType().equals("Modular Program")){
			String sql="SELECT  " + 
					"    s.sapid as userId, " +  
					"    ct.name as consumerType, " + 
					"    ps.programType, " + 
					"    p_s.program_structure as programStructure, " + 
					"    ps.program " + 	
					"FROM " + 
					"    exam.students s" + 					 										
					"        INNER JOIN " +
					"    exam.consumer_program_structure cps ON cps.id = s.consumerProgramStructureId " +
					"        INNER JOIN " +
					"    exam.program_structure p_s ON p_s.id = cps.programStructureId " +					
					"        INNER JOIN " +
					"    exam.programs ps ON ps.consumerProgramStructureId = cps.id " +
					"        INNER JOIN " +
					"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " +
					"WHERE 1=1 ";				
			
			if(bean.getConsumerTypeId()!=null && !("").equals(bean.getConsumerTypeId())) {
				sql += " AND ct.id = "+bean.getConsumerTypeId()+"";
			}
			
			if(bean.getProgramType()!=null && ("").equals(bean.getProgramType())) {
				sql += " AND ps.programType not in ('Master')";
//				List<String> values = Arrays.asList(bean.getProgramType().split(","));
//				for(String val : values) {
//					sql += "'"+val+ "',";
//				}
//				if (sql.endsWith(",")) {
//					sql = sql.substring(0, sql.length() - 1);
//				}
//				sql += " ) ";
			}else {
				sql += " AND ps.programType in ( ";
				List<String> values = Arrays.asList(bean.getProgramType().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramStructureId()!=null && !("").equals(bean.getProgramStructureId())) {
				sql += " AND p_s.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramStructureId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramId()!=null && !("").equals(bean.getProgramId())) {
				sql += " AND ps.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getSem()!=null && !("").equals(bean.getSem())) {
				sql += " AND s.sem in ( ";
				List<String> values = Arrays.asList(bean.getSem().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
//			if(bean.getBatchId()!=null && !("").equals(bean.getBatchId())) {
//				sql += " AND b.id in ( ";
//				List<String> values = Arrays.asList(bean.getBatchId().split(","));
//				for(String val : values) {
//					sql += "'"+val+ "',";
//				}
//				if (sql.endsWith(",")) {
//					sql = sql.substring(0, sql.length() - 1);
//				}
//				sql += " ) ";
//			}
			
			sql +=" GROUP BY sapid";	
			
			try {
				dummyOtherUsersList = (ArrayList<DummyUserBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(DummyUserBean.class));
			} catch (DataAccessException e) {
				  
			}			
		}			
		
		if(bean.getProgramType().equals("Modular Program")){
			String sql="SELECT  " + 
					"    l.emailId as userId, " +  
					"    ct.name as consumerType, " + 
					"    ps.programType, " + 
					"    p_s.program_structure as programStructure, " + 
					"    ps.program " + 	
					"FROM " + 
					"    lead.leads l" +
					"        INNER JOIN " +					
					"    lead.leads_master_key_mapping lmkm ON lmkm.leads_id = l.leadId " +
					"        INNER JOIN " +
					"    exam.consumer_program_structure cps ON cps.id = lmkm.consumerProgramStructureId " +
					"        INNER JOIN " +
					"    exam.program_structure p_s ON p_s.id = cps.programStructureId " +					
					"        INNER JOIN " +
					"    exam.programs ps ON ps.consumerProgramStructureId = cps.id " +
					"        INNER JOIN " +
					"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " +
					"WHERE 1=1 ";				
			
			if(bean.getConsumerTypeId()!=null && !("").equals(bean.getConsumerTypeId())) {
				sql += " AND ct.id = "+bean.getConsumerTypeId()+"";
			}
			
			if(bean.getProgramType()!=null && !("").equals(bean.getProgramType())) {
				sql += " AND ps.programType in ( ";
				List<String> values = Arrays.asList(bean.getProgramType().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramStructureId()!=null && !("").equals(bean.getProgramStructureId())) {
				sql += " AND p_s.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramStructureId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getProgramId()!=null && !("").equals(bean.getProgramId())) {
				sql += " AND ps.id in ( ";
				List<String> values = Arrays.asList(bean.getProgramId().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
			if(bean.getSem()!=null && !("").equals(bean.getSem())) {
				sql += " AND s.sem in ( ";
				List<String> values = Arrays.asList(bean.getSem().split(","));
				for(String val : values) {
					sql += "'"+val+ "',";
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				sql += " ) ";
			}
			
//			if(bean.getBatchId()!=null && !("").equals(bean.getBatchId())) {
//				sql += " AND b.id in ( ";
//				List<String> values = Arrays.asList(bean.getBatchId().split(","));
//				for(String val : values) {
//					sql += "'"+val+ "',";
//				}
//				if (sql.endsWith(",")) {
//					sql = sql.substring(0, sql.length() - 1);
//				}
//				sql += " ) ";
//			}
			
			sql +=" GROUP BY leadId";	
			
			try {
				dummyLeadUsersList = (ArrayList<DummyUserBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(DummyUserBean.class));
			} catch (DataAccessException e) {
				  
			}			
		}			
		
		
		if(bean.getProgramType().equals("")) {
			dummyAllUsersList.addAll(dummyMasterUsersList);
			dummyAllUsersList.addAll(dummyOtherUsersList);
			dummyAllUsersList.addAll(dummyLeadUsersList);
		}else {
			if(bean.getProgramType().equals("Master")) {
				dummyAllUsersList.addAll(dummyMasterUsersList);
			}else if(bean.getProgramType().equals("Modular Program")){
				dummyAllUsersList.addAll(dummyLeadUsersList);
			}else {
				dummyAllUsersList.addAll(dummyOtherUsersList);
			}
		}
		
		return dummyAllUsersList;
		
	}
	
	public String setConsumerPrograrmStructureId(String id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String masterKey="";
		String programType="";
		String sql = "";
		if(id.contains("@") || id.contains(".")) {
			sql = "SELECT  " + 
					"    ps.programType " + 
					"FROM " + 
					"    lead.leads l " +
					"        INNER JOIN " + 
					"    lead.leads_master_key_mapping lmkm ON lmkm.leads_id = l.leadId " +
					"        INNER JOIN " + 
					"    exam.programs ps ON ps.consumerProgramStructureId = lmkm.consumerProgramStructureId " + 
					"WHERE " + 
					"    l.emailId = ? " + 
					"GROUP BY l.emailId;";
			try {
				programType = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   				
			}
		}else {
			sql = "SELECT  " + 
					"    ps.programType " + 
					"FROM " + 
					"    exam.students s" +
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps on cps.id = s.consumerProgramStructureId " +
					"        INNER JOIN " + 
					"    exam.programs ps ON ps.consumerProgramStructureId = cps.id " + 
					"WHERE " + 
					"    s.sapid = ? " + 
					"GROUP BY s.sapid;";
			try {
				programType = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   				
			}
		}
		
		if(!programType.equals("Modular Program")) {
			sql = "SELECT  " + 
					"    consumerProgramStructureId " + 
					"FROM " + 
					"    exam.students " + 
					"WHERE " + 
					"    sapid = ?";
			try {
				masterKey = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   				
			}
			
			if(!masterKey.equals("")) {
				sql = "UPDATE exam.students  " + 
						"SET  " + 
						"    consumerProgramStructureId = ? " + 
						"WHERE " + 
						"    sapid = '77999999999'";
				try {
					jdbcTemplate.update(sql,new Object[]{masterKey});					
				}catch(Exception e) {
					   					
				}
			}
			return "77999999999";
		}else {
			return id;
		}
	}

	public String getRoleByUserId(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String programType="";
		String role="";
		int checkIfExist = 0;
		String sql = "SELECT  " + 
				"    EXISTS( SELECT  " + 
				"            * " + 
				"        FROM " + 
				"            exam.students " + 
				"        WHERE " + 
				"            sapid = ?)";
		
		try {
			checkIfExist = (int) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(Integer.class));
		}catch(Exception e) {
			   
		}
		
		if(checkIfExist == 1) {		
			sql = "SELECT programType from exam.programs ps "
				+ " where consumerProgramStructureId = (select consumerProgramStructureId from exam.students where sapid = ? group by sapid)";
		} else {
			sql = "SELECT programType from exam.programs ps where consumerProgramStructureId = "
					+ "(select consumerProgramStructureId from lead.leads_master_key_mapping where leads_id = "
					+ "(select leadId from lead.leads where emailId = ? group by emailId))";
		}
		
		try {
			programType = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
		}catch(Exception e) {
			   
		}
		
		if(programType.equals("Master")) {
		
			sql = "SELECT  " + 
					"    role " + 
					"FROM " + 
					"    lti.timebound_user_mapping " + 
					"WHERE " + 
					"    userId = ? " + 
					"GROUP BY userId ";		
			try {
				role = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   
			}
		} else if(programType.equals("Modular Program")){
			sql = "SELECT " + 
					"    CASE " + 
					"        WHEN " + 
					"            firstName = 'Test' " + 
					"                OR emailId = 'jforce.solutions@gmail.com' " + 
					"        THEN " + 
					"            'Test User' " + 
					"        ELSE 'User' " + 
					"    END AS role " + 
					"FROM " + 
					"    lead.leads " +
					"WHERE " + 
					"    emailId = ? " + 
					"GROUP BY emailId ";		
			try {
				role = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   
			}
		} else {
			sql = "SELECT " + 
					"    CASE " + 
					"        WHEN " + 
					"            firstName = 'Test' " + 
					"                OR emailId = 'jforce.solutions@gmail.com' " + 
					"        THEN " + 
					"            'Test User' " + 
					"        ELSE 'User' " + 
					"    END AS role " + 
					"FROM " + 
					"    exam.students " + 
					"WHERE " + 
					"    sapid = ? " + 
					"GROUP BY sapid ";		
			try {
				role = (String) jdbcTemplate.queryForObject(sql,new Object[]{id}, new SingleColumnRowMapper(String.class));
			}catch(Exception e) {
				   
			}
		}
		return role;
		
	}

	public List<ConsumerProgramStructureAcads> getProgramStructureByConsumerType(String consumerTypeId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programsStructureByConsumerType = null;

		String sql =  "select p_s.program_structure as name,p_s.id as id "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "where c_p_s.consumerTypeId = ? group by p_s.id";

		try {
			programsStructureByConsumerType = jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return programsStructureByConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getProgramByConsumerType(String consumerTypeId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programsByConsumerType = null;

		String sql =  "select ps.program as name,ps.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on ps.consumerProgramStructureId = c_p_s.id "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_p_s.consumerTypeId = ?  group by ps.id";

		try {
			programsByConsumerType = jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return programsByConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getProgramTypeByConsumerType(String consumerTypeId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programTypeByConsumerType = null;

		String sql =  "select ps.programType as name from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "where c_p_s.consumerTypeId = ?  and ps.programType is not null  group by ps.programType";

		try {
			programTypeByConsumerType = jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return programTypeByConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getBatchByConsumerType(String consumerTypeId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> batchByConsumerType = null;

		String sql =  "select b.name as name, b.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "left join exam.batch as b on b.consumerProgramStructureId = c_p_s.id "
				+ "where c_p_s.consumerTypeId = ? and b.name is not null group by b.id";

		try {
			batchByConsumerType = jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return batchByConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getProgramStructureByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure,String programTypes){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programStructureByProgramTypeAndConsumerType = null;

		/*List<String> values = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		String sqlValues = "";
		for(String val : values){
			sqlValues += "'"+val+"',";
		}
		if(sqlValues.endsWith(",")){
			sqlValues = sqlValues.substring(0,sqlValues.length()-1);
		}*/

		String sql =  "select p_s.program_structure as name,p_s.id as id "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.programs as ps on ps.consumerProgramStructureId = c_p_s.id "
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+programTypes+") group by p_s.id";

		try {
			programStructureByProgramTypeAndConsumerType = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}
		return programStructureByProgramTypeAndConsumerType;
	}

	public List<ConsumerProgramStructureAcads> getProgramByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure,String programTypes){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programByProgramTypeAndConsumerType = null;

		/*List<String> values = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		String sqlValues = "";
		for(String val : values){
			sqlValues += "'"+val+"',";
		}
		if(sqlValues.endsWith(",")){
			sqlValues = sqlValues.substring(0,sqlValues.length()-1);
		}
		*/
		String sql =  "select ps.program as name,ps.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on ps.consumerProgramStructureId = c_p_s.id "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+programTypes+")  group by ps.id";

		try {
			programByProgramTypeAndConsumerType = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return programByProgramTypeAndConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getBatchByProgramTypeAndConsumerType(ConsumerProgramStructureAcads consumerProgramStructure,String programTypes){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> batchByProgramTypeAndConsumerType = null;

		/*List<String> values = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		String sqlValues = "";
		for(String val : values){
			sqlValues += "'"+val+"',";
		}
		if(sqlValues.endsWith(",")){
			sqlValues = sqlValues.substring(0,sqlValues.length()-1);
		}*/

		String sql =  "select b.name as name, b.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "left join exam.batch as b on b.consumerProgramStructureId = c_p_s.id "
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+programTypes+") and b.name is not null group by b.id";

		try {
			batchByProgramTypeAndConsumerType = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return batchByProgramTypeAndConsumerType;

	}

	public List<ConsumerProgramStructureAcads> getProgramByMasterKey(ConsumerProgramStructureAcads consumerProgramStructure){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> programByProgramTypeAndConsumerTypeAndProgramStructure = null;

		/*List<String> prgmTypeValues = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		List<String> prgmStructValues = Arrays.asList(consumerProgramStructure.getProgramStructureId().split(","));
		
		String sqlPrgmTypeValues = "";
		String sqlPrgmStructValues = "";
		for(String val : prgmTypeValues){
			sqlPrgmTypeValues += "'"+val+"',";
		}
		for(String val : prgmStructValues){
			sqlPrgmStructValues += "'"+val+"',";
		}
		if(sqlPrgmTypeValues .endsWith(",")){
			sqlPrgmTypeValues = sqlPrgmTypeValues.substring(0,sqlPrgmTypeValues.length()-1);
		}
		if(sqlPrgmStructValues.endsWith(",")){
			sqlPrgmStructValues = sqlPrgmStructValues.substring(0,sqlPrgmStructValues.length()-1);
		}*/

		String sql =  "select ps.program as name,ps.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on ps.consumerProgramStructureId = c_p_s.id "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+consumerProgramStructure.getProgramType()+") "
				+ "and c_p_s.programStructureId in ("+consumerProgramStructure.getProgramStructureId()+") group by ps.id";

		try {
			programByProgramTypeAndConsumerTypeAndProgramStructure = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return programByProgramTypeAndConsumerTypeAndProgramStructure;

	}

	public List<ConsumerProgramStructureAcads> getBatchByMasterKey(ConsumerProgramStructureAcads consumerProgramStructure){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> batchByProgramTypeAndConsumerTypeAndProgramStructure = null;

		/*@CommitedBy	:Siddheshwar Khanse
		 *@Date			:January 8, 2021
		 *@Reason		:This logic shifted to service layer */
		
		/*List<String> prgmTypeValues = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		List<String> prgmStructValues = Arrays.asList(consumerProgramStructure.getProgramStructureId().split(","));
		
		String sqlPrgmTypeValues = "";
		String sqlPrgmStructValues = "";
		for(String val : prgmTypeValues){
			sqlPrgmTypeValues += "'"+val+"',";
		}
		for(String val : prgmStructValues){
			sqlPrgmStructValues += "'"+val+"',";
		}
		if(sqlPrgmTypeValues.endsWith(",")){
			sqlPrgmTypeValues = sqlPrgmTypeValues.substring(0,sqlPrgmTypeValues.length()-1);
		}
		if(sqlPrgmStructValues.endsWith(",")){
			sqlPrgmStructValues = sqlPrgmStructValues.substring(0,sqlPrgmStructValues.length()-1);
		}*/

		String sql =  "select b.name as name, b.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "left join exam.batch as b on b.consumerProgramStructureId = c_p_s.id "
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+consumerProgramStructure.getProgramType()+") "
				+ "and c_p_s.programStructureId in ("+consumerProgramStructure.getProgramStructureId()+") and b.name is not null group by b.id";

		try {
			batchByProgramTypeAndConsumerTypeAndProgramStructure = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return batchByProgramTypeAndConsumerTypeAndProgramStructure;

	}

	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgram(ConsumerProgramStructureAcads consumerProgramStructure){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgram = null;

		/*@CommitedBy	:Siddheshwar Khanse
		 *@Date			:January 8, 2021
		 *@Reason		:This logic shifted to service layer */
		
		/*List<String> prgmTypeValues = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		List<String> prgmStructValues = Arrays.asList(consumerProgramStructure.getProgramStructureId().split(","));
		List<String> prgmValues = Arrays.asList(consumerProgramStructure.getProgramId().split(","));
		
		String sqlPrgmTypeValues = "";
		String sqlPrgmStructValues = "";
		String sqlPrgmValues = "";
		for(String val : prgmTypeValues){
			sqlPrgmTypeValues += "'"+val+"',";
		}
		for(String val : prgmStructValues){
			sqlPrgmStructValues += "'"+val+"',";
		}
		for(String val : prgmValues){
			sqlPrgmValues += "'"+val+"',";
		}
		if(sqlPrgmTypeValues.endsWith(",")){
			sqlPrgmTypeValues = sqlPrgmTypeValues.substring(0,sqlPrgmTypeValues.length()-1);
		}
		if(sqlPrgmStructValues.endsWith(",")){
			sqlPrgmStructValues = sqlPrgmStructValues.substring(0,sqlPrgmStructValues.length()-1);
		}
		if(sqlPrgmValues.endsWith(",")){
			sqlPrgmValues = sqlPrgmValues.substring(0,sqlPrgmValues.length()-1);
		}*/

		String sql =  "select b.name as name, b.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "left join exam.batch as b on b.consumerProgramStructureId = c_p_s.id "
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+consumerProgramStructure.getProgramType()+") "
				+ "and c_p_s.programStructureId in ("+consumerProgramStructure.getProgramStructureId()+") and ps.id in ("+consumerProgramStructure.getProgramId()+") and b.name is not null group by b.id";

		try {
			batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgram = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgram;

	}

	public List<ConsumerProgramStructureAcads> getBatchByMasterKeyAndProgramAndSem(ConsumerProgramStructureAcads consumerProgramStructure){

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ConsumerProgramStructureAcads> batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgramAndSem = null;

		/*@CommitedBy	:Siddheshwar Khanse
		 *@Date			:January 9, 2021
		 *@Reason		:This logic shifted to service layer */
		
		/*List<String> prgmTypeValues = Arrays.asList(consumerProgramStructure.getProgramType().split(","));
		List<String> prgmStructValues = Arrays.asList(consumerProgramStructure.getProgramStructureId().split(","));
		List<String> prgmValues = Arrays.asList(consumerProgramStructure.getProgramId().split(","));
		List<String> semValues = Arrays.asList(consumerProgramStructure.getSem().split(","));
		
		String sqlPrgmTypeValues = "";
		String sqlPrgmStructValues = "";
		String sqlPrgmValues = "";
		String sqlSemValues = "";
		for(String val : prgmTypeValues){
			sqlPrgmTypeValues += "'"+val+"',";
		}
		for(String val : prgmStructValues){
			sqlPrgmStructValues += "'"+val+"',";
		}
		for(String val : prgmValues){
			sqlPrgmValues += "'"+val+"',";
		}
		for(String val : semValues){
			sqlSemValues += "'"+val+"',";
		}
		if(sqlPrgmTypeValues.endsWith(",")){
			sqlPrgmTypeValues = sqlPrgmTypeValues.substring(0,sqlPrgmTypeValues.length()-1);
		}
		if(sqlPrgmStructValues.endsWith(",")){
			sqlPrgmStructValues = sqlPrgmStructValues.substring(0,sqlPrgmStructValues.length()-1);
		}
		if(sqlPrgmValues.endsWith(",")){
			sqlPrgmValues = sqlPrgmValues.substring(0,sqlPrgmValues.length()-1);
		}
		if(sqlSemValues.endsWith(",")){
			sqlSemValues = sqlSemValues.substring(0,sqlSemValues.length()-1);
		}*/

		String sql =  "select b.name as name, b.id as id from exam.consumer_program_structure "
				+ "as c_p_s left join exam.programs as ps on c_p_s.id = ps.consumerProgramStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "				
				+ "left join exam.batch as b on b.consumerProgramStructureId = c_p_s.id "				
				+ "where c_p_s.consumerTypeId = ? and ps.programType in ("+consumerProgramStructure.getProgramType()+") "
				+ "and c_p_s.programStructureId in ("+consumerProgramStructure.getProgramStructureId()+") and ps.id in ("+consumerProgramStructure.getProgramId()+") "
				+ "and b.sem in ("+consumerProgramStructure.getSem()+") and b.name is not null group by b.id";

		try {
			batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgramAndSem = jdbcTemplate.query(sql,
					new Object[] {consumerProgramStructure.getConsumerTypeId()},
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));

		} catch (Exception e) {

			  
			return null;
		}

		return batchByProgramTypeAndConsumerTypeAndProgramStructureAndProgramAndSem;

	}	
	

}
