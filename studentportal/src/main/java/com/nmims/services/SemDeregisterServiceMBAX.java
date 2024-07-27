package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.SFConnection;
import com.nmims.interfaces.SemDeregisterInterface;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.ws.ConnectionException;


@Service("semDeregisterServiceMBAX")
public class SemDeregisterServiceMBAX implements SemDeregisterInterface {

	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH;
	
	private PartnerConnection connection;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;
	
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private MBAWXLiveSettingsDAO mbaWxLiveSettingsDAO;

	public SemDeregisterServiceMBAX () {
		this.connection = SFConnection.getConnection();
	}
	
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = SFConnection.getConnection();
	}
	
	@Override
	public ServiceRequestStudentPortal checkSemDeregisterEligibility(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		String sapId = sr.getSapId();		
		ArrayList<ServiceRequestStudentPortal> serviceRequestList = new ArrayList<ServiceRequestStudentPortal>();
		ArrayList<StudentStudentPortalBean> studentList = new ArrayList<StudentStudentPortalBean>();
		//System.out.println(" sapid---%%%%%%%%%%%%%%"+sapId);
		sr.setError("");
//		1. Time range check
//		to be added
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd"); 
		try {
			Date today = sdfo.parse(sdfo.format(new Date()));
			Date till = sdfo.parse("2021-06-30");		
			//System.out.println("today--"+today+"---till--"+till);
			if (today.after(till)) { 
	            // When Date d1 > Date d2 
	            //System.out.println("today is after till"); 
	            sr.setError("Program De-Registration date lapsed(Jun. 30th 2021).");
	            return sr;
	        } 
		} catch (ParseException e1) {
			//e1.printStackTrace();
			sr.setError("Error in checking last date of De-registration.");
		} 
        
		
//		2. Count check : for De-reg SR should be < 2
		try {
			serviceRequestList =  serviceRequestDao.getDeregSRForStudent(sapId,CURRENT_MBAX_ACAD_MONTH,CURRENT_MBAX_ACAD_YEAR);
			if(serviceRequestList.size() >= 0 && serviceRequestList.size() < 2) {
//		3. Check validity in SFDC
				boolean canDereg = checkValidityInSFDCForDeReg(sapId,CURRENT_MBAX_ACAD_MONTH,CURRENT_MBAX_ACAD_YEAR);				
				if(canDereg) {
					try {
						studentList = serviceRequestDao.getProgramsRegisteredByStudent(sapId,CURRENT_MBAX_ACAD_MONTH,CURRENT_MBAX_ACAD_YEAR);
						sr.setStudentRegistrationList(studentList);
					}
					catch(Exception e) {
						sr.setError("Error in getting registration records.");
						sr.setErrorMessage(e.getMessage());
					}
				}
				else {
					sr.setStudentRegistrationList(studentList);
					sr.setError("Can not De-register due to failure of validity expiration check.");
				}
			}
			else {
				sr.setStudentRegistrationList(studentList);
				sr.setError("Program De-register SR already raised. Can not De-register for Current Sem for more than Once.");
			}
		}
		catch(Exception e) {
			sr.setError("Error in getting service request count for student.");
			sr.setErrorMessage(e.getMessage());
		}
		return sr;
	}

	@Override
	public ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean checkValidityInSFDCForDeReg(String sapId,String currentAcadMonth,String currentAcadYear) {
		//System.out.println("inside----1-----checkValidityInSFDCForDeReg---");
		boolean isValid = false;
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 */
		//System.out.println("Updating records back in SFDC");
		System.out.println("SFDC_USERID = "+SFDC_USERID);
		System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		QueryResult qResult = new QueryResult();
		try {
			//connection = Connector.newConnection(config);
			String query = "select id,Sem__c,Student_Number__c,StageName, "
					+ " nm_Session__c,nm_Year__c  from opportunity  "
					+ " where  nm_Session__c like '" + currentAcadMonth + "%' and nm_Year__c=" + currentAcadYear
					+ " and StageName ='Closed Won' and Student_Number__c ='"+ sapId+"' ORDER BY Sem__c desc limit 1";
			//System.out.println(connection.);
			qResult = connection.query(query);
			//System.out.println("inside----2----checkValidityInSFDCForDeReg---"+query);
			if (qResult.getSize() > 0) {
				//System.out.println("inside----3----checkValidityInSFDCForDeReg----"+qResult);
				isValid = true;
			}
		} catch (ApiFault e) {
            init();
        } catch (ConnectionException e2){
        	
        } catch (Exception e3) {
        	//e3.printStackTrace();
        }
		return isValid;
	}


}