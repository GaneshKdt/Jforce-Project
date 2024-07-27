package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.CaseBean;
import com.nmims.daos.CaseDAO;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class CaseController {

	
	@Autowired	
	CaseDAO caseDAO;

	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;
	
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	private PartnerConnection connection;	
	
	private static final Logger logger = LoggerFactory.getLogger(CaseController.class);	

	public CaseController(SFConnection sf) { 
		this.connection = sf.getConnection();
	}
	@RequestMapping(value = "/migrateEmailMessages", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<CaseBean> migrateEmailMessages(@RequestBody List<CaseBean> cases){
		
		CaseBean responseBean = new CaseBean();
		ConnectorConfig config = new ConnectorConfig();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		//config.setUsername(SFDC_USERID);  
		//config.setPassword(SFDC_PASSWORD_TOKEN);  

		int count = cases.size();

		if(count != 0 && cases != null) {		
			
			logger.info("#count: "+count+", fetching emailMessages");
			
			/*
			 * try {
			 * 
			 * //connection = Connector.newConnection(config);
			 * 
			 * } catch (ConnectionException e2) {
			 * 
			 * e2.printStackTrace(); responseBean.setStatus("#connectionFailed");
			 * 
			 * }
			 */
			
			for(CaseBean bean : cases) {
				
				try {

					caseDAO.getCaseFromSFCD(bean, connection);
					caseDAO.getCaseAttachmentsFromSFCD(bean, connection); 
					
				} catch (ConnectionException e) {
					
					logger.info(""+e.getStackTrace());
					responseBean.setErrorMessage(e.getMessage());
					responseBean.setStatus("#failed");
					
				} catch (Exception e) {
					
					logger.info(""+e.getStackTrace());
					responseBean.setErrorMessage(e.getMessage());
					responseBean.setStatus("#failed");
					
				}
					
			}

			responseBean.setStatus("#completed count: "+count);
			
		}else {
			
			logger.info("Empty list");
			responseBean.setErrorMessage("Empty list");
			responseBean.setStatus("#failed");
		}
		
		logger.info("#completed");
		
		return new ResponseEntity<>(responseBean, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getEmailMessages", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<CaseBean>> getEmailMessages(@RequestBody CaseBean bean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<CaseBean> idList = new ArrayList<>();
		ArrayList<CaseBean> emailMessage = new ArrayList<>();
		
		try {
			idList = caseDAO.getIdList(bean);
			System.out.println("idList: "+idList);
			for(CaseBean id : idList) {
				CaseBean message = new CaseBean();
				
				message = caseDAO.getEmailMessages(id);
				message.setAttachments(caseDAO.getAttachments(id));
				
				emailMessage.add(message);
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(emailMessage, headers, HttpStatus.OK);
	}

}
