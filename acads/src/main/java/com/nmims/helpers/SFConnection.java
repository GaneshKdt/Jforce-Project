package com.nmims.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
 
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
@Component  
public class SFConnection {
	private static PartnerConnection connection; 
	@Value("${SFDC_USERID}")
	private String SFDC_USERID;
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN; 
	private long connectionAttempts=0;
	private static final Logger logger = LoggerFactory.getLogger(SFConnection.class); 
	
	public SFConnection(@Value( "${SFDC_USERID}" ) String SFDC_USERID,
			@Value( "${SFDC_PASSWORD_TOKEN}" ) String SFDC_PASSWORD_TOKEN) {
		logger.info("setting up connection ...");
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);
		config.setPassword(SFDC_PASSWORD_TOKEN);
		logger.info("SFDC_USERID = "+SFDC_USERID);
		logger.info("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		connectionAttempts++;
		try { 
			connection = Connector.newConnection(config);
			logger.info("connection successful");
		} catch (ConnectionException e) { 
			logger.info(SFDC_USERID);
			logger.info(SFDC_PASSWORD_TOKEN);
			logger.info("connection failed");
			logger.info(e.getMessage()); 
		}
	} 
	public PartnerConnection getConnection() {
		return connection;
	}  
}
