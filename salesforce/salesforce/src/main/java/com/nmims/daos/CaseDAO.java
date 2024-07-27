package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.nmims.beans.CaseBean;
import com.nmims.controllers.CaseController;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class CaseDAO{

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

	private static final Logger logger = LoggerFactory.getLogger(CaseController.class);	
	
	private JdbcTemplate jdbcTemplate;

	private DataSource dataSource;
	
	@Autowired 
    @Qualifier("analyticsDataSource") 
	private DataSource analyticsDataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ArrayList<CaseBean> getCaseFromSFCD(CaseBean parentId, PartnerConnection connection) throws ConnectionException, Exception {
		
		ArrayList<CaseBean> caseList = new ArrayList<CaseBean>();
		
		try {

			String query = "SELECT Parent.Id, Parent.CaseNumber, Parent.Status,  CreatedById, CreatedDate,  LastModifiedById, LastModifiedDate, "
					+ "SystemModstamp, TextBody, HtmlBody, Headers,  Subject, FromName,  FromAddress, ToAddress, CcAddress, BccAddress, Incoming, "
					+ "HasAttachment, Status, MessageDate, IsDeleted, ReplyToEmailMessageId, IsExternallyVisible, "
					+ "ICemailaddress__c FROM EmailMessage where Id='"+parentId.getId()+"'";
			QueryResult qResult = connection.query(query);
			SObject[] records = qResult.getRecords();
			for(SObject record : records) {
				CaseBean cases = new CaseBean();

				cases.setId(parentId.getId());
				cases.setParentId((String)record.getChild("Parent").getField("Id"));
				cases.setCaseNumber((String)record.getChild("Parent").getField("CaseNumber"));
				cases.setCaseStatus((String)record.getChild("Parent").getField("Status"));
				cases.setCreatedById((String)record.getField("CreatedById"));
				cases.setCreatedDate((String)record.getField("CreatedDate"));
				cases.setLastModifiedById((String)record.getField("LastModifiedById"));
				cases.setLastModifiedDate((String)record.getField("LastModifiedDate"));
				cases.setSystemModstamp((String)record.getField("SystemModstamp"));
				cases.setTextBody((String)record.getField("TextBody"));
				cases.setHtmlBody((String)record.getField("HtmlBody"));
				cases.setHeaders((String)record.getField("Headers"));
				cases.setSubject((String)record.getField("Subject"));
				cases.setFromName((String)record.getField("FromName"));
				cases.setFromAddress((String)record.getField("FromAddress"));
				cases.setToAddress((String)record.getField("ToAddress"));
				cases.setCcAddress((String)record.getField("CcAddress"));
				cases.setBccAddress((String)record.getField("BccAddress"));
				cases.setIncoming(Boolean.getBoolean((String)record.getField("Incoming")));
				cases.setHasAttachment(Boolean.getBoolean((String)record.getField("HasAttachment")));
				cases.setStatus((String)record.getField("Status"));
				cases.setMessageDate((String)record.getField("MessageDate"));
				cases.setDeleted(Boolean.getBoolean((String)record.getField("IsDeleted")));
				cases.setReplyToEmailMessageId((String)record.getField("ReplyToEmailMessageId"));
				cases.setExternallyVisible(Boolean.getBoolean((String)record.getField("IsExternallyVisible")));
				cases.setiCEmail((String)record.getField("ICemailaddress__c"));

				insertCases(cases);
			}

			return caseList;
		}catch (Exception e) {
			e.printStackTrace();
			return caseList;
		}
	}
	
	
	public void getCaseAttachmentsFromSFCD(CaseBean parentId, PartnerConnection connection) throws ConnectionException, Exception {
		
		boolean done = false;
		int count = 0;
		try {
			
			done = false;
			String query = "SELECT Id, Name, Body, BodyLength, ContentType, Description, IsPrivate, OwnerId FROM Attachment WHERE ParentId='"+parentId.getId()+"'";
			QueryResult qResult = connection.query(query);
			count = count + qResult.getSize();
				
			if (qResult.getSize() > 0) {

				while (!done) {
					SObject[] records = qResult.getRecords();
					for(SObject record : records) {
							
						CaseBean attachment = new CaseBean();

						attachment.setAttachmentId((String)record.getField("Id"));
						attachment.setName((String)record.getField("Name"));
						attachment.setAttachmentBody((String)record.getField("Body"));
						attachment.setBodyLength((String)record.getField("BodyLength"));
						attachment.setContentType((String)record.getField("ContentType"));
						attachment.setDescription((String)record.getField("Description"));
						attachment.setPrivate(Boolean.getBoolean((String)record.getField("IsPrivate")));
						attachment.setOwnerId((String)record.getField("OwnerId"));
						attachment.setParentId(parentId.getId());

						if(!checkIfAttachmentPresent(attachment.getAttachmentId())) {
								
							insertCaseAttachments(attachment);
							updateHasAttachments(attachment);
								
						}else {
							System.out.println("alreadyPresent: "+attachment.getAttachmentId());
						}
					}
					if (qResult.isDone()) {
						done = true;
					} else {
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
			}
			
		}catch (Exception e) {
			
			e.printStackTrace();
			logger.info(""+e.getStackTrace());
			
		}
		
		System.out.print("#attachment: "+count+"\n");
		logger.info("#attachment: "+count);
	}
	
	private void insertCases(CaseBean bean) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE `salesforce`.`emailmessage` " + 
				"SET " + 
				"`parentId` = ?, "+
				"`caseNumber` = ?, " + 
				"`caseStatus` = ?, " + 
				"`createdById` = ?, " + 
				"`createdDate` = ?, " + 
				"`lastModifiedById` = ?, " + 
				"`lastModifiedDate` = ?, " + 
				"`systemModstamp` = ?, " + 
				"`textBody` = ?, " + 
				"`htmlBody` = ?, " + 
				"`headers` = ?, " + 
				"`subject` = ?, " + 
				"`fromName` = ?, " + 
				"`fromAddress` = ?, " + 
				"`toAddress` = ?, " + 
				"`ccAddress` = ?, " + 
				"`bccAddress` = ?, " + 
				"`incoming` = ?, " + 
				"`hasAttachment` = ?, " + 
				"`status` = ?, " + 
				"`messageDate` = ?, " + 
				"`isDeleted` = ?, " + 
				"`replyToEmailMessageId` = ?, " + 
				"`isExternallyVisible` = ?, " + 
				"`iCEmail` = ? " + 
				"WHERE id = ?";

		String parentId = bean.getParentId();
		String caseNumber = bean.getCaseNumber();
		String caseStatus = bean.getCaseStatus();
		String createdById = bean.getCreatedById();
		String createdDate = bean.getCreatedDate();
		String lastModifiedById = bean.getLastModifiedById();
		String lastModifiedDate = bean.getLastModifiedDate();
		String systemModstamp = bean.getSystemModstamp();
		String textBody = bean.getTextBody();
		String htmlBody = bean.getHtmlBody();
		String header = bean.getHeaders();
		String subject = bean.getSubject();
		String fromAddress = bean.getFromAddress();
		String fromName = bean.getFromName();
		String toAddress = bean.getToAddress();
		String ccAddress=  bean.getCcAddress();
		String bccAddress = bean.getBccAddress();
		boolean incoming = bean.isIncoming();
		boolean hasAttachment = bean.isHasAttachment();  
		String status = bean.getStatus(); 
		String messageDate = bean.getMessageDate(); 
		boolean isDeleted = bean.isDeleted();
		String replyToEmailMessageId = bean.getReplyToEmailMessageId(); 
		boolean isExternallyVisible = bean.isExternallyVisible(); 
		String icEmail = bean.getiCEmail();
		String id = bean.getId();
		
		jdbcTemplate.update(sql, new Object[] { 
				parentId, caseNumber, caseStatus, createdById,createdDate, lastModifiedById, lastModifiedDate, systemModstamp, textBody, htmlBody, header, 
				subject, fromAddress, fromName, toAddress, ccAddress, bccAddress, incoming, hasAttachment, status, messageDate, isDeleted,
				replyToEmailMessageId, isExternallyVisible, icEmail, id
		});
		return;
	}

	
	private void insertCaseAttachments(CaseBean bean) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO `salesforce`.`attachment` " + 
				"(`attachmentId`, `parentId`, `name`, `attachmentBody`, `bodyLength`, `contentType`, `description`, `isPrivate`, `ownerId`) " + 
				"VALUES " + 
				"(?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String attachmentId = bean.getAttachmentId();
		String parentId = bean.getParentId();
		String name = bean.getName();
		String attachmentBody = bean.getAttachmentBody();
		String bodyLength = bean.getBodyLength();
		String contentType = bean.getContentType();
		String description = bean.getDescription();
		boolean isPrivate = bean.isPrivate();
		String ownerId = bean.getOwnerId();
		
		jdbcTemplate.update(sql, new Object[] { 
				attachmentId, parentId, name, attachmentBody, bodyLength, contentType, description, isPrivate, ownerId
		});
		return;
	}
	
	public ArrayList<CaseBean> getIdList(CaseBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(analyticsDataSource);

		String sql = "SELECT " + 
				"    id " + 
				"FROM " + 
				"    salesforce.emailmessage " + 
				"WHERE " + 
				"    parentId = ? ";

		ArrayList<CaseBean> idList = (ArrayList<CaseBean>) jdbcTemplate.query(sql, new Object[] {bean.getParentId()},
				new BeanPropertyRowMapper<CaseBean>(CaseBean.class));
		
		return idList;
		
	}
	
	public CaseBean getEmailMessages(CaseBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(analyticsDataSource);

		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    salesforce.emailmessage  " + 
				"WHERE " + 
				"    id = ? ";

		CaseBean emailMessage = jdbcTemplate.queryForObject(sql, new Object[] { bean.getId() },
				new BeanPropertyRowMapper<CaseBean>(CaseBean.class));
		
		return emailMessage;
		
	}
	
	public ArrayList<CaseBean> getAttachments(CaseBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(analyticsDataSource);

		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    salesforce.attachment  " + 
				"WHERE " + 
				"    parentId = ? ";

		ArrayList<CaseBean> attachments = (ArrayList<CaseBean>) jdbcTemplate.query(sql, new Object[] { bean.getId() },
				new BeanPropertyRowMapper<CaseBean>(CaseBean.class));
		
		return attachments;
	}
	
	public ArrayList<CaseBean> getIdFromDatabase(int offset) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    id " + 
				"FROM " + 
				"    salesforce.emailmessage " + 
				"    where createdDate > '2018-12-31T24:59:99' " + 
				"LIMIT 500 OFFSET ? ";

		ArrayList<CaseBean> caseList = (ArrayList<CaseBean>) jdbcTemplate.query(sql ,new Object[] {offset} ,new BeanPropertyRowMapper<CaseBean>(CaseBean.class));
		return caseList;
	}
	
	public int getSize() throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT " + 
				"    count(*) " + 
				"FROM " + 
				"    salesforce.emailmessage";

		int size = (int) jdbcTemplate.queryForObject(sql, Integer.class);
		return size;
	}
	
	private void updateHasAttachments(CaseBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE salesforce.emailmessage  " + 
				"SET  " + 
				"    hasAttachment = 1 " + 
				"WHERE " + 
				"    id = ?";


		jdbcTemplate.update(sql, new Object[] { bean.getParentId() });
		return;
		
	}
	
	private boolean checkIfAttachmentPresent(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(attachmentId) > 0 " + 
				"FROM " + 
				"    salesforce.attachment " + 
				"WHERE " + 
				"    attachmentId = ?";
		
		boolean isPresent = (boolean) jdbcTemplate.queryForObject(sql, new Object[] {id} ,Boolean.class);
		return isPresent;
	}
}
