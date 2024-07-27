package com.nmims.daos;

import java.util.ArrayList;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.LeadAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Component
public class LeadDAO extends BaseDAO{
	
	public LeadDAO() {  
	} 
	
	public LeadDAO(SFConnection sf) {  
		this.connection = sf.getConnection();
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
	
	StudentAcadsBean bean = new StudentAcadsBean();
	
	public StudentAcadsBean getLeadsFromSalesForce(String email, StudentAcadsBean bean) throws ConnectionException {
		 
		
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
		LeadAcadsBean lead = new LeadAcadsBean();
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
		subjects = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {}, new SingleColumnRowMapper<String>(String.class));
		return subjects;	
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getSessionForLeads(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.video_content_forleads";
		ArrayList<VideoContentAcadsBean> sessionList = (ArrayList<VideoContentAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		return sessionList;
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getSubejctViseSessionForLead(VideoContentAcadsBean bean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    acads.video_content_forleads " + 
				"WHERE " + 
				"    subject = ?";
		
		ArrayList<VideoContentAcadsBean> sessionList = (ArrayList<VideoContentAcadsBean>)jdbcTemplate.query(sql, new Object[] {bean.getSubject()},
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		
		return sessionList;
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentAcadsBean> getAllSessionForLead(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    acads.video_content_forleads ";
		
		ArrayList<VideoContentAcadsBean> sessionList = (ArrayList<VideoContentAcadsBean>)jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<VideoContentAcadsBean>(VideoContentAcadsBean.class));
		
		return sessionList;
	}
}
