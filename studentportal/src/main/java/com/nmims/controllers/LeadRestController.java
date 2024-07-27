package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.factory.LeadFactory;
import com.nmims.interfaces.LeadInterface;
import com.sforce.ws.ConnectionException;

@RestController
@RequestMapping("m")
public class LeadRestController {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	LeadDAO leadDAO;
	
	@Autowired
	LeadFactory leadFactory;
	
	@RequestMapping(value = "/getLeadDetailsForMobile", method = RequestMethod.POST, consumes = "application/json", 
	produces = "application/json")
	public ResponseEntity<ArrayList<StudentStudentPortalBean>> getLeadDetailsForMobile(@RequestBody LeadStudentPortalBean input) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 

		String userId = input.getUserId();
		String loginType = "";
		
		ArrayList<StudentStudentPortalBean> details = new ArrayList<>();

		userId="77999999999";

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");

		switch ( input.getLoginType() ) {
			case "Email":
				loginType = "EMAIL";
				break;
			case "nm_RegistrationNo__c":
				loginType = "REGISTATIONID";
				break;
			case "Mobile_No__c":
				loginType = "MOBILE";
				break;
			default:
				break;
		}
		
		LeadInterface lead = leadFactory.getLoginType(LeadFactory.LoginType.valueOf(loginType));
		
		try {
			ArrayList<LeadStudentPortalBean> leadDetails = lead.getLeadFromSalesForce( input );
			for( LeadStudentPortalBean bean: leadDetails ) {

				StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
				
				student.setLeadId( bean.getLeadId() );
				student.setMobile( bean.getMobile() );
				student.setEmail( bean.getEmailId() );
				student.setRegistrationNum( bean.getRegistrationId() );
				student.setFirstName( bean.getFirstName());
				student.setMiddleName( bean.getFirstName() );
				student.setLastName( bean.getLastName() );
				student.setDob( bean.getDob() );
				student.setFatherName( bean.getFatherName() );
				student.setMotherName( bean.getMotherName());
				student.setGender( bean.getGender() );
				student.setSpouseName( bean.getSpouseName() );
				
				details.add( student );
				
			}
		}catch (Exception e) {
			return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<>(details, headers,  HttpStatus.OK);

		
	}
	
	   @RequestMapping(value = "/getLocation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
		public ResponseEntity<StudentStudentPortalBean> getLocation(@RequestBody StudentStudentPortalBean student){

	    	StudentStudentPortalBean response = new StudentStudentPortalBean();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			try {
				
				leadDAO.updateLeadLocation();
				response.setError(false);
				response.setErrorMessage("Completed");		
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
				
			} catch (ConnectionException e) {
				
				response.setError(true);
				response.setErrorMessage("Error");
				//e.printStackTrace();
				return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}


		}
	   
	   
		@RequestMapping(value = "/getContent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
		public ResponseEntity<List<ContentStudentPortalBean>> getContent(HttpServletRequest request,
				@RequestBody StudentStudentPortalBean bean) throws Exception {

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			List<ContentStudentPortalBean> allContentListForSubject = new ArrayList<ContentStudentPortalBean>();
			
			if(!StringUtils.isBlank(bean.getSubject())) {
				allContentListForSubject = pDao.getContentsForLeads(bean);
			}else {
				allContentListForSubject = pDao.getAllContentsForLeads();
			}

			return new ResponseEntity<>(allContentListForSubject,headers, HttpStatus.OK);

		}

}
