package com.nmims.daos;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.ModuleContentAcadsBean;




public class LtiDao extends BaseDAO{
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	final String providerNot =  "21";
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Transactional(readOnly = true)
	public List<LTIConsumerRequestBean> getLtiResources(String user_id, String roles, String provider_name,String isStudent){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String endDate_sql = "";
		if(isStudent.equals("N")) 
			endDate_sql = 	" AND CURRENT_TIMESTAMP() BETWEEN urm.startDate AND urm.endDate ";
	
		
		List<LTIConsumerRequestBean> lti_resources = new ArrayList<LTIConsumerRequestBean>();
		String sql =" SELECT " +  
			    	" lr.contextId AS context_id, " + 
			    	" lr.contextLabel AS context_label, " + 
			    	" lr.contextTitle AS context_title, " +
			    	" lr.contextType AS context_type, " +
			    	" lr.resource_link_description, " +
			    	" lr.resource_link_id, " +
			    	" lr.resource_link_title, " + 
			    	" lr.id AS resource_id, " +
			    	" p.name AS provider_name, " +
			    	" p.launchUrl As launch_url, "+
			    	" urm.accessCode AS access_token " +
			    	"	FROM " +
			        " lti.lti_user_resourse_mapping urm " +
			        "	INNER JOIN " +
			        " lti.lti_users u ON urm.userId = u.id " + 
			        "	INNER JOIN " +
			        " lti.lti_resources lr ON urm.resourceId = lr.id " +
			        "	INNER JOIN " +
			        "lti.lti_providers p ON p.id = lr.providerId " +
			        "	WHERE " +
			        "  u.userId = ? " +
			        " AND u.roles = ?  " +
			        " AND p.name = ? "+endDate_sql;
		try {
			
		lti_resources = (List<LTIConsumerRequestBean>) jdbcTemplate.query(sql, new Object[] {
				user_id, 
				roles,
				provider_name
		}, new BeanPropertyRowMapper(LTIConsumerRequestBean.class));
		
		} catch (DataAccessException e) {
			  
		}
		return lti_resources;
	}
	
	@Transactional(readOnly = true)
	public List<LTIConsumerRequestBean> getLtiResources(String user_id, String provider,String isStudent){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String endDate_sql = "";
		
		if(isStudent.equals("N")) 
			endDate_sql = 	" AND p.id not in ("+providerNot+") AND CURRENT_TIMESTAMP() BETWEEN urm.startDate AND urm.endDate ";

		
		List<LTIConsumerRequestBean> lti_resources = new ArrayList<LTIConsumerRequestBean>();
		String sql =" SELECT " +  
				" lr.contextId AS context_id, " + 
				" lr.contextLabel AS context_label, " + 
				" lr.contextTitle AS context_title, " +
				" lr.contextType AS context_type, " +
				" lr.resource_link_description, " +
				" lr.resource_link_id, " +
				" lr.resource_link_title, " + 
				" lr.id AS resource_id, " +
				" p.name AS provider_name, " +
				" p.launchUrl As launch_url, "+
				" urm.accessCode AS access_token, " +
				" CAST(endDate AS date) as endDate  " +  
				"	FROM " +
				" lti.lti_user_resourse_mapping urm " +
				"	INNER JOIN " +
				" lti.lti_users u ON urm.userId = u.id " + 
				"	INNER JOIN " +
				" lti.lti_resources lr ON urm.resourceId = lr.id " +
				"	INNER JOIN " +
				"lti.lti_providers p ON p.id = lr.providerId " +
				"	WHERE " +
				"  u.userId = ? " +
				" AND p.name = ?  "+endDate_sql;
			
		try {
		
			lti_resources = (List<LTIConsumerRequestBean>) jdbcTemplate.query(sql, 
					new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,user_id);
					preparedStatement.setString(2,provider);
				}
			}, new BeanPropertyRowMapper<LTIConsumerRequestBean>(LTIConsumerRequestBean.class));
				
		} catch (DataAccessException e) {
			  
		}
		return lti_resources;
	}
	
	@Transactional(readOnly = true)
	public String getMasterKeyOfStudents(String sapid) {
		try {
			String sql = "select consumerProgramStructureId from exam.students where sapid = ? ";
			String consumerProgramStructureId = jdbcTemplate.queryForObject(sql, new Object[] {sapid}, String.class);
			return consumerProgramStructureId;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
}
