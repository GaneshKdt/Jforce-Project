package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.xml.crypto.dsig.XMLObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.helpers.SFConnection;
import com.nmims.util.ContentUtil;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.XmlObject;

@Component
public class LeadDAO extends BaseDAO{
	
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
	
	@Autowired
	SFConnection sfc;
	
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

	public LeadDAO() { 
		this.connection = SFConnection.getConnection();
	}
	/*
	 * public void init(){ SFConnection sf= new
	 * SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN); this.connection =
	 * sf.getConnection(); }
	 */
	/*
	 * public void setSFDCConnection() {
	 * 
	 * try { config.setUsername(SFDC_USERID);
	 * config.setPassword(SFDC_PASSWORD_TOKEN); this.connection =
	 * Connector.newConnection(config);
	 * 
	 * } catch (ConnectionException e) { e.printStackTrace(); } }
	 */
	
	
	public ArrayList<LeadStudentPortalBean> getLeadForMobileFromSalesForce(String leadUserId, StudentStudentPortalBean bean, String loginType) 
			throws ConnectionException, Exception {
		
		boolean done = false;
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		System.out.println("inGetLeadForMobileFromSalesForce leadUserId: "+bean.getUserId());
		
		String sql = "SELECT Name, nm_MiddleName__c, Mobile_No__c, nm_LeadId__c , nm_RegistrationNo__c, Email, nm_DateOfBirth__c, "
				+ "nm_Program__r.StudentZoneProgramCode__c, nm_StudentImageUrl__c, nm_MothersName__c, nm_FathersName__c, nm_Gender__c, "
				+ "nm_SpouseName__c ,nm_SelectedIC__c FROM Lead WHERE Mobile_No__c = '"+bean.getUserId()+"' ";

		try {
			QueryResult qResult = connection.query(sql);
			
			if( qResult.getSize() > 0 ) {
				
				while (!done) {
					
					SObject[] records = qResult.getRecords();

					for(SObject record : records) {
						
						LeadStudentPortalBean lead = new LeadStudentPortalBean();
						String name = (String)record.getField("Name");
						String[] splited = name.split("\\s+");
						String firstName= splited[0];
						String lastName= splited[1];
						lead.setFirstName(firstName);
						lead.setLastName(lastName);
						lead.setMiddleName((String)record.getField("nm_MiddleName__c"));
						lead.setLeadId((String)record.getField("nm_LeadId__c"));
						lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
						lead.setEmailId((String)record.getField("Email"));
						lead.setMobile((String)record.getField("Mobile_No__c"));
						XmlObject program = (XmlObject) record.getField("nm_Program__r");
						
						try {
							lead.setProgram( (String)program.getChild("StudentZoneProgramCode__c").getValue() );
						}catch (Exception e) {
							lead.setProgram("PGDBM");
						}

						lead.setDob((String)(String)record.getField("nm_DateOfBirth__c"));
						lead.setMotherName((String)record.getField("nm_MothersName__c"));
						lead.setFatherName((String)record.getField("nm_FathersName__c"));
						lead.setGender((String)record.getField("nm_Gender__c"));
						lead.setSpouseName((String)record.getField("nm_SpouseName__c"));
						lead.setImageUrl("nm_StudentImageUrl__c");
						lead.setLocality((String)record.getField("nm_SelectedIC__c"));

						leadDetails.add(lead);
						
						if (qResult.isDone()) {
							done = true;
						} else {
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return leadDetails;

	}

	public ArrayList<LeadStudentPortalBean> getLeadForEmailFromSalesForce( LeadStudentPortalBean bean ) throws ConnectionException, Exception {
		
		boolean done = false;
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		System.out.println("inGetLeadForEmailFromSalesForce leadUserId: "+bean.getUserId());
		
		String sql = "SELECT Name, nm_MiddleName__c, Mobile_No__c, nm_LeadId__c , nm_RegistrationNo__c, Email, nm_DateOfBirth__c, "
				+ "nm_Program__r.StudentZoneProgramCode__c, nm_StudentImageUrl__c, nm_MothersName__c, nm_FathersName__c, nm_Gender__c, "
				+ "nm_SpouseName__c ,nm_SelectedIC__c FROM Lead WHERE Email = '"+bean.getUserId()+"' ";

		try {
			QueryResult qResult = connection.query(sql);
			
			if( qResult.getSize() > 0 ) {
				
				while (!done) {
					
					SObject[] records = qResult.getRecords();

					for(SObject record : records) {
						
						LeadStudentPortalBean lead = new LeadStudentPortalBean();
						String name = (String)record.getField("Name");
						String[] splited = name.split("\\s+");
						String firstName= splited[0];
						String lastName= splited[1];
						lead.setFirstName(firstName);
						lead.setLastName(lastName);
						lead.setMiddleName((String)record.getField("nm_MiddleName__c"));
						lead.setLeadId((String)record.getField("nm_LeadId__c"));
						lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
						lead.setEmailId((String)record.getField("Email"));
						lead.setMobile((String)record.getField("Mobile_No__c"));
						XmlObject program = (XmlObject) record.getField("nm_Program__r");
						
						try {
							lead.setProgram( (String)program.getChild("StudentZoneProgramCode__c").getValue() );
						}catch (Exception e) {
							lead.setProgram("PGDBM");
						}

						lead.setDob((String)(String)record.getField("nm_DateOfBirth__c"));
						lead.setMotherName((String)record.getField("nm_MothersName__c"));
						lead.setFatherName((String)record.getField("nm_FathersName__c"));
						lead.setGender((String)record.getField("nm_Gender__c"));
						lead.setSpouseName((String)record.getField("nm_SpouseName__c"));
						lead.setImageUrl("nm_StudentImageUrl__c");
						lead.setLocality((String)record.getField("nm_SelectedIC__c"));

						leadDetails.add(lead);
						
						if (qResult.isDone()) {
							done = true;
						} else {
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return leadDetails;

	}

	public ArrayList<LeadStudentPortalBean> getLeadForRegistrationIdFromSalesForce( LeadStudentPortalBean bean ) throws ConnectionException, Exception {
		
		boolean done = false;
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		System.out.println("inGetLeadForRegistrationIdFromSalesForce leadUserId: "+bean.getUserId());
		
		String sql = "SELECT Name, nm_MiddleName__c, Mobile_No__c, nm_LeadId__c , nm_RegistrationNo__c, Email, nm_DateOfBirth__c, "
				+ "nm_Program__r.StudentZoneProgramCode__c, nm_StudentImageUrl__c, nm_MothersName__c, nm_FathersName__c, nm_Gender__c, "
				+ "nm_SpouseName__c ,nm_SelectedIC__c FROM Lead WHERE nm_RegistrationNo__c = '"+bean.getUserId()+"' ";
	
		try {
			QueryResult qResult = connection.query(sql);
			
			if( qResult.getSize() > 0 ) {
				
				while (!done) {
					
					SObject[] records = qResult.getRecords();
	
					for(SObject record : records) {
						
						LeadStudentPortalBean lead = new LeadStudentPortalBean();
						String name = (String)record.getField("Name");
						String[] splited = name.split("\\s+");
						String firstName= splited[0];
						String lastName= splited[1];
						lead.setFirstName(firstName);
						lead.setLastName(lastName);
						lead.setMiddleName((String)record.getField("nm_MiddleName__c"));
						lead.setLeadId((String)record.getField("nm_LeadId__c"));
						lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
						lead.setEmailId((String)record.getField("Email"));
						lead.setMobile((String)record.getField("Mobile_No__c"));
						XmlObject program = (XmlObject) record.getField("nm_Program__r");
						
						try {
							lead.setProgram( (String)program.getChild("StudentZoneProgramCode__c").getValue() );
						}catch (Exception e) {
							lead.setProgram("PGDBM");
						}
	
						lead.setDob((String)(String)record.getField("nm_DateOfBirth__c"));
						lead.setMotherName((String)record.getField("nm_MothersName__c"));
						lead.setFatherName((String)record.getField("nm_FathersName__c"));
						lead.setGender((String)record.getField("nm_Gender__c"));
						lead.setSpouseName((String)record.getField("nm_SpouseName__c"));
						lead.setImageUrl("nm_StudentImageUrl__c");
						lead.setLocality((String)record.getField("nm_SelectedIC__c"));

						leadDetails.add(lead);
						
						if (qResult.isDone()) {
							done = true;
						} else {
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return leadDetails;
	
	}

	@Transactional(readOnly = true)
	public String getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT program_structure FROM exam.program_structure ORDER BY program_structure DESC LIMIT 1;";
		String programStructure =  (String) jdbcTemplate.queryForObject(sql, String.class);
//		LeadBean lead = new LeadBean();
//		lead.setProgramStructure(programStructure);
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
	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT code FROM exam.program where `code` like '%PGD%' order by code asc ";

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		return programList;
	}

	@Transactional(readOnly = true)
	public boolean checkIfLeadExists(StudentStudentPortalBean bean) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT count(*)>0 FROM lead.leads where leadId= ?";
		boolean isPresent =  (boolean) jdbcTemplate.queryForObject(sql, new Object[] { bean.getLeadId() }, Boolean.class);
		return isPresent;
	}

	@Transactional(readOnly = false)
	public void insertLeadDetailsLocally( LeadStudentPortalBean bean ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		System.out.println("bean: "+bean.getSapid());

		String sql = "INSERT INTO `lead`.`leads` " + 
				"(`leadId`, `registrationId`, `sapidMapped`, `location`, `consumerProgramStructureId`, `emailId`, `firstName`, " + 
				"`lastName`, `gender`, `dob`, `mobile`, `program`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`, `perspective`) " + 
				"VALUES " + 
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,sysdate(), ?, sysdate(), 'free')";
		
		jdbcTemplate.update(sql, new Object[] { 
				bean.getLeadId(),
				bean.getRegistrationId(),
				bean.getSapid(),
				bean.getLocality(),
				bean.getConsumerProgramStructureId(),
				bean.getEmailId(),
				bean.getFirstName(),
				bean.getLastName(),
				bean.getGender(),
				bean.getDob(),
				bean.getMobile(),
				bean.getProgram(),
				bean.getFirstName(),
				bean.getFirstName()
		});
	}

	@Transactional(readOnly = true)
	public int leadCount() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select count(*) from exam.leads";
		int count = (int)jdbcTemplate.queryForObject(sql, Integer.class);
		
		return count;
	}

	@Transactional(readOnly = true)
	public List<String> getLeadList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select emailId from exam.leads";
		List<String> leadList = (List<String>)jdbcTemplate.queryForList(sql, String.class);
		
		return leadList;
	}

	@Transactional(readOnly = false)
	public void insertMailRecord(final ArrayList<MailStudentPortalBean> mailList,final String fromUserId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO portal.leadMails(subject,createdBy,createdDate,body,"
		        		+ "fromEmailId) VALUES(?,?,sysdate(),?,?) ");
		        statement.setString(1, mailList.get(0).getSubject());
		        statement.setString(2, fromUserId);
		        statement.setString(3,mailList.get(0).getBody());
		        statement.setString(4,mailList.get(0).getFromEmailId());
		        return statement;
		    }
		});

		return;
	}

	@Transactional(readOnly = true)
	public ArrayList<MailStudentPortalBean> getCommunicationForLeads(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    id, subject, fromEmailId, body, createdDate " + 
				"FROM " + 
				"    portal.leadmails";
		ArrayList<MailStudentPortalBean> communicatoinList = (ArrayList<MailStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<MailStudentPortalBean>(MailStudentPortalBean.class));
		return communicatoinList;
	}

	@Transactional(readOnly = true)
	public ArrayList<VideoContentStudentPortalBean> getSessionForLead() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql="SELECT "
				+ "s.track as track, "
				+ "CONCAT('Prof. ',f.firstName,' ', f.lastName) as facultyName, "
				+ "v.description, v.subject , v.sessionDate, v.thumbnailUrl, v.id "
				+ "FROM "
				+ "acads.video_content_forleads v "
				+ "LEFT JOIN "
				+ "acads.faculty f ON f.facultyId = v.facultyId "
				+ "LEFT JOIN "
				+ "acads.sessions s ON s.id = v.sessionId ";

		ArrayList<VideoContentStudentPortalBean> videoList = (ArrayList<VideoContentStudentPortalBean>)jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(VideoContentStudentPortalBean.class));

		return videoList;
	}

	@Transactional(readOnly = false)
	public boolean insertIntoLead(LeadStudentPortalBean leadBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into `lead`.`otp_verify` (mobile, deviceType, otp, verify) values(?,?,?,'N')";
		try {
			jdbcTemplate.update(sql,new Object[] {leadBean.getMobile(),leadBean.getDeviceType(),leadBean.getOtp()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return false;
		}
		
	}

	@Transactional(readOnly = false)
	public LeadStudentPortalBean verifyOTP(LeadStudentPortalBean leadBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from `lead`.`otp_verify` where mobile=? and otp=? and verify = 'N' limit 1";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] {leadBean.getMobile(),leadBean.getOtp()}, 
					new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}

	@Transactional(readOnly = false)
	public boolean updateVerify(LeadStudentPortalBean leadBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update `lead`.`otp_verify` set verify = 'Y' where mobile = ?";
		try {
			jdbcTemplate.update(sql,new Object[] {leadBean.getMobile()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean updateOTP(LeadStudentPortalBean leadBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update `lead`.`otp_verify` set otp = ? where mobile = ?";
		try {
			jdbcTemplate.update(sql,new Object[] {leadBean.getOtp(),leadBean.getMobile()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
	public String getRole(String userId) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  roles FROM portal.user_authorization WHERE userId = ?";
		String role = jdbcTemplate.queryForObject(sql, new Object[] {userId}, String.class);
		
		return role;
	}

	@Transactional(readOnly = true)
	public ArrayList<LeadStudentPortalBean> getLeadDetailsForReport(String userId) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""; String role = "";
		try {
			role = getRole(userId);
		}catch (Exception e) {
			//e.printStackTrace();
		}
		
		if( "Information Center".equals(role) || "Information Center,SAS Team".equals(role) ) {

			sql = "SELECT * FROM lead.leads " + 
				"WHERE " + 
				"    location IN (SELECT c.lc  FROM " + 
				"    	portal.user_authorization ua " + 
				"    	   INNER JOIN " + 
				"       exam.centers c ON ua.authorizedCenters = c.centerCode " + 
				"       WHERE " + 
				"            userId = ?)";
			
		}else if( "Learning Center".equals(role) || "Learning Center,SAS Team".equals(role) ){

			sql = "SELECT * FROM lead.leads " + 
				  "WHERE " + 
				  "    location IN (SELECT authorizedLC FROM portal.user_authorization WHERE userId = ?)";
			
		}else {

			sql = "SELECT * FROM lead.leads ";
			ArrayList<LeadStudentPortalBean> bean = (ArrayList<LeadStudentPortalBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
			return bean;
			
		}
		
		ArrayList<LeadStudentPortalBean> bean = (ArrayList<LeadStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {userId},
				new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		
		return bean;
	}

	@Transactional(readOnly = true)
	public boolean checkIfLeadPresentForMobile( LeadStudentPortalBean bean ) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    count(leadId) " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE mobile = ?";
		boolean isPresent =  (boolean) jdbcTemplate.queryForObject(sql, new Object[] { bean.getUserId() }, Boolean.class);
		return isPresent;
	}

	@Transactional(readOnly = true)
	public boolean checkIfLeadPresentForEmail( LeadStudentPortalBean bean ) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    count(leadId) " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE emailId = ?";
		boolean isPresent =  (boolean) jdbcTemplate.queryForObject(sql, new Object[] { bean.getUserId() }, Boolean.class);
		return isPresent;
	}

	@Transactional(readOnly = true)
	public boolean checkIfLeadPresentForRegistrationId( LeadStudentPortalBean bean ) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    count(leadId) " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE registrationId = ?";
		boolean isPresent =  (boolean) jdbcTemplate.queryForObject(sql, new Object[] { bean.getUserId() }, Boolean.class);
		return isPresent;
	}

	@Transactional(readOnly = true)
	public LeadStudentPortalBean getLeadDetailsLocallyForMobile( LeadStudentPortalBean bean ) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    * " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE mobile = ? "+
				"GROUP BY mobile";

		bean = jdbcTemplate.queryForObject(sql, new Object[] {bean.getUserId()},new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		
		return bean;
		
	}

	@Transactional(readOnly = true)
	public LeadStudentPortalBean getLeadDetailsLocallyForEmail( LeadStudentPortalBean bean ) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    * " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE emailId = ? "+
				"GROUP BY emailId";

		bean = jdbcTemplate.queryForObject(sql, new Object[] {bean.getUserId()},new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		
		return bean;
		
	}	
	
	@Transactional(readOnly = true)
	public LeadStudentPortalBean getLeadDetailsLocallyForRegistrationId( LeadStudentPortalBean bean ) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    * " + 
				"FROM " + 
				"	lead.leads " +
				"WHERE registrationId = ? "+
				"GROUP BY registrationId";

		bean = jdbcTemplate.queryForObject(sql, new Object[] {bean.getUserId()},new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));
		
		return bean;
		
	}
	
	public void updateLeadLocation() throws ConnectionException {

		//setSFDCConnection();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		int count = 1;
		String sql = "SELECT leadId FROM lead.leads";
		ArrayList<LeadStudentPortalBean> leadIdList = (ArrayList<LeadStudentPortalBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LeadStudentPortalBean.class));

		for(LeadStudentPortalBean bean : leadIdList) {
			
			String soql="SELECT LC_Name__c, Mobile_No__c FROM lead WHERE nm_LeadId__c='"+bean.getLeadId()+"'";

			QueryResult qResult = connection.query(soql);
			SObject[] records = qResult.getRecords();
			if( records.length>0 ) {
				
				SObject s = (SObject) records[0]; 
				//System.out.println("mappingDetails");  
				String location = (String)s.getField("LC_Name__c");
				String mobile = (String)s.getField("Mobile_No__c");
				
				String update = "UPDATE `lead`.`leads` SET `location` = ?, mobile = ? WHERE `leadId` = ?;";
				try {
					jdbcTemplate.update(update, new Object[] { location, mobile, bean.getLeadId()});
					System.out.println("#count: "+count);
				}catch (Exception e) {
					//e.printStackTrace();
				}
				
			}
			count++;
		}
		System.out.println("#done");
	}

	@Transactional(readOnly = false)
	 public void setPerspectiveForLead(String leadId,String perspective) {
		 String sql = "Update lead.leads set "
		+ "perspective=? "  
		+ " where leadId= ? ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				perspective,leadId
		});
	}
	
	@Transactional(readOnly = false)
	public LeadStudentPortalBean getLeadById(String leadId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT * FROM lead.leads WHERE leadId = ?";
		return jdbcTemplate.queryForObject(
		sql, 
		new Object[] {leadId}, 
		new BeanPropertyRowMapper<LeadStudentPortalBean>(LeadStudentPortalBean.class)
		);
	} 
	
	public ArrayList<LeadStudentPortalBean> getLeadsFromSalesForceForLogin(String leadUserId, LeadStudentPortalBean bean) throws ConnectionException, Exception {
		
		boolean done = false;
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		String sql = "SELECT Mobile_No__c,  nm_RegistrationNo__c, Email "
				+ "FROM Lead WHERE "+bean.getLoginType()+" = '"+leadUserId+"' ";

		try {
			QueryResult qResult = connection.query(sql);
			
			if( qResult.getSize() > 0 ) {
				
				while (!done) {
					
					SObject[] records = qResult.getRecords();

					for(SObject record : records) {
						
						LeadStudentPortalBean lead = new LeadStudentPortalBean();

						lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
						lead.setEmailId((String)record.getField("Email"));
						lead.setMobile((String)record.getField("Mobile_No__c"));

						leadDetails.add(lead);
						
						if (qResult.isDone()) {
							done = true;
						} else {
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return leadDetails;

	}

	@Transactional(readOnly = true)
	public boolean checkIfLeadExists(LeadStudentPortalBean bean) throws Exception{
		
		boolean present = false;
		int count = 0;
		
		String soql = "SELECT count(nm_RegistrationNo__c) FROM Lead WHERE Email = '"+bean.getEmailId()+
				"' OR Mobile_No__c = '"+bean.getMobile()+"'";

		System.out.println("connection: "+connection);
		QueryResult qResult = connection.query(soql);
			
		if( qResult.getSize() > 0 ) {
				
			SObject[] records = qResult.getRecords();
			SObject record = (SObject) records[0];
			count = (Integer)record.getField("expr0");
			if( count > 0 )
				present = true;
				
		}

		return present;

	}

	@Transactional(readOnly = true)
	public ArrayList<LeadStudentPortalBean> getDetailsForAlreadyRegisteredUser(LeadStudentPortalBean bean) {
		
		ArrayList<LeadStudentPortalBean> details = new ArrayList<>();
		
		String soql = "SELECT nm_RegistrationNo__c, Email, Mobile_No__c FROM Lead WHERE Email = '"+bean.getEmailId()+
				"' OR Mobile_No__c = '"+bean.getMobile()+"'";

		try {
			QueryResult qResult = connection.query(soql);
			
			if( qResult.getSize() > 0 ) {
				
				SObject[] records = qResult.getRecords();
				for(SObject record : records) {

					LeadStudentPortalBean lead = new LeadStudentPortalBean();
					
					lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
					lead.setEmailId((String)record.getField("Email"));
					lead.setMobile((String)record.getField("Mobile_No__c"));
					
					details.add( lead );
				}
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return details;

	}

	public ArrayList<LeadStudentPortalBean> getLeadForMobileFromSalesForce( LeadStudentPortalBean bean ) throws ConnectionException, Exception {
		
		boolean done = false;
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		System.out.println("inGetLeadForMobileFromSalesForce leadUserId: "+bean.getUserId());
		
		String sql = "SELECT Name, nm_MiddleName__c, Mobile_No__c, nm_LeadId__c , nm_RegistrationNo__c, Email, nm_DateOfBirth__c, "
				+ "nm_Program__r.StudentZoneProgramCode__c, nm_StudentImageUrl__c, nm_MothersName__c, nm_FathersName__c, nm_Gender__c, nm_SpouseName__c ,nm_SelectedIC__c "
				+ "FROM Lead WHERE Mobile_No__c = '"+bean.getUserId()+"' ";

		try {
			QueryResult qResult = connection.query(sql);
			
			if( qResult.getSize() > 0 ) {
				
				while (!done) {
					
					SObject[] records = qResult.getRecords();

					for(SObject record : records) {
						
						LeadStudentPortalBean lead = new LeadStudentPortalBean();
						String name = (String)record.getField("Name");
						String[] splited = name.split("\\s+");
						String firstName= splited[0];
						String lastName= splited[1];
						lead.setFirstName(firstName);
						lead.setLastName(lastName);
						lead.setMiddleName((String)record.getField("nm_MiddleName__c"));
						lead.setLeadId((String)record.getField("nm_LeadId__c"));
						lead.setRegistrationId((String)record.getField("nm_RegistrationNo__c"));
						lead.setEmailId((String)record.getField("Email"));
						lead.setMobile((String)record.getField("Mobile_No__c"));
						XmlObject program = (XmlObject) record.getField("nm_Program__r");
						
						try {
							lead.setProgram( (String)program.getChild("StudentZoneProgramCode__c").getValue() );
						}catch (Exception e) {
							lead.setProgram("PGDBM");
						}

						lead.setDob((String)(String)record.getField("nm_DateOfBirth__c"));
						lead.setMotherName((String)record.getField("nm_MothersName__c"));
						lead.setFatherName((String)record.getField("nm_FathersName__c"));
						lead.setGender((String)record.getField("nm_Gender__c"));
						lead.setSpouseName((String)record.getField("nm_SpouseName__c"));
						lead.setImageUrl("nm_StudentImageUrl__c");
						lead.setLocality((String)record.getField("nm_SelectedIC__c"));

						leadDetails.add(lead);
						
						if (qResult.isDone()) {
							done = true;
						} else {
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return leadDetails;

	}


	/**
	 * Fetching session recording for the lead students based on their subjects applicable and program structure.
	 * @param subjectList - contains subject applicable to lead student.
	 * @param programStructure - program structure of a lead student.
	 * @return List - returns the sorted video contents list in the descending order.
	 */
	public List<VideoContentStudentPortalBean> getVideosForLead(List<String> subjectList, String programStructure){
		
		List<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();
		
		//Fetch session recording for lead students
		videoList = getSessionForLead();
		
		//Sort the video content list in the descending order based on session date
		videoList = ContentUtil.sortInDesc(videoList);
		
		//return sorted list
		return videoList;
		
	}
	
}
