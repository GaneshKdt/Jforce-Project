package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.LeadExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ReRegistrationBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaginationHelper;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
public class LeadDAO extends BaseDAO{
	public LeadDAO(SFConnection sf) {  
		this.connection = sf.getConnection();
	}
	public LeadDAO() {
		// TODO Auto-generated constructor stub
	}
	@Autowired
	ApplicationContext act;
	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;
	
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	private PartnerConnection connection;	

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	StudentExamBean bean = new StudentExamBean();
	
	public StudentExamBean getLeadsFromSalesForce(String email, StudentExamBean bean) throws ConnectionException {
		 QueryResult qResult = new QueryResult();
		
			try { 
				String sql = "select Name, nm_MiddleName__c, Mobile_No__c, nm_LeadId__c , nm_RegistrationNo__c, nm_DateOfBirth__c, Email, nm_Program__c, nm_Session__c, nm_StudentImageUrl__c, nm_Year__c from Lead where Email='"+email+"' ";
				qResult = connection.query(sql);
				SObject[] records = qResult.getRecords();
				SObject s = (SObject) records[0]; 
					if(records.length>0) {
						bean.setError(false); 
					}
		
					String name = (String)s.getField("Name");
					String[] splited = name.split("\\s+");
					String firstName= splited[0];
					String lastName= splited[1];
					bean.setFirstName(firstName);
					bean.setLastName(lastName);
					bean.setMiddleName((String)s.getField("nm_MiddleName__c"));
					bean.setLeadId((String)s.getField("nm_LeadId__c"));
					bean.setRegistrationNum((String)s.getField("nm_RegistrationNo__c"));
					bean.setEmailId((String)s.getField("Email"));
					bean.setMobile((String)s.getField("Mobile_No__c"));
					if(StringUtils.isBlank((CharSequence) s.getField("nm_Program__c")))
						bean.setProgram("PGDBM");
					else
						bean.setProgram((String)s.getField("nm_Program__c"));
					bean.setImageUrl((String)s.getField("nm_StudentImageUrl__c"));
					return bean;
			} catch (Exception e) {
				
				bean.setError(true);
				return bean;
			}
	}
	
	@Transactional(readOnly = true)
	public String getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT program_structure FROM exam.program_structure ORDER BY program_structure DESC LIMIT 1;";
		String programStructure =  (String) jdbcTemplate.queryForObject(sql, new Object[] {}, String.class);
		LeadExamBean lead = new LeadExamBean();
		lead.setProgramStructure(programStructure);
		return programStructure;
	}

	@Transactional(readOnly = true)
	public String getConsumerProgramStructureIdForLeads() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String consumerProgramStructureId = null, name = "Retail", code="PGDBM";
		String sql ="SELECT id FROM exam.consumer_program_structure WHERE " + 
					"consumerTypeId = (SELECT id FROM exam.consumer_type WHERE name = 'Retail') AND " + 
					"programStructureId = (SELECT id FROM exam.program_structure ORDER BY program_structure DESC LIMIT 1) AND " + 
					"programId = (SELECT id FROM exam.program WHERE code = 'PGDBM')";
		consumerProgramStructureId = (String)jdbcTemplate.queryForObject(sql, new Object[] {}, String.class); 
		return consumerProgramStructureId;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsForLeads(){
		ArrayList<String> subjects = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT " + 
				"subject " + 
				"FROM " + 
				"exam.program_subject " + 
				"WHERE " + 
				"sem = '1' AND program = 'pgdbm' " + 
				"AND active = 'y' " + 
				"AND prgmStructApplicable = (SELECT " + 
				"program_structure " + 
				"FROM " + 
				"exam.program_structure " + 
				"ORDER BY program_structure DESC " + 
				"LIMIT 1) " + 
				"ORDER BY program , sem , subject;";
		subjects = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {}, new SingleColumnRowMapper(String.class));
		return subjects;	
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getLeadById(String leadId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT * FROM lead.leads WHERE leadId = ?";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] {leadId}, 
			new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
		);
	}
}
