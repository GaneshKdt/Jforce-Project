package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentRankBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.factory.CertificateFactory;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.CreateRankPDF;
import com.nmims.helpers.LinkedInManager;
import com.nmims.interfaces.CertificateInterface;

@Controller
public class LinkedInController {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	LinkedInManager linkedInManager; 
	
	@Autowired
	CertificateFactory certificateFactory;

	@Autowired
	CreateRankPDF createRankPDF;
	
	@Autowired
	ServiceRequestDao serviceRequestDao;

	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	
	@Value("${LINKED_IN_CLIENT_SECRET}")
	private String LINKED_IN_CLIENT_SECRET;
	
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;
	
	@Value("${LINKED_IN_REDIRECT_URI}")
	private String LINKED_IN_REDIRECT_URI;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${LINKED_IN_RANK_REDIRECT_URI}")
	private String LINKED_IN_RANK_REDIRECT_URI; 
	

//	to be deleted, api shifted to rest controller
//    @RequestMapping (value="/m/shareCertificateLinkedIn", method= RequestMethod.POST)
//    public ResponseEntity<ServiceRequest> shareCertificateLinkedIn(@RequestBody ServiceRequest sr, HttpServletRequest request, HttpServletResponse response) {
//    	
//
//    	try {
//    		
//        	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
//    		sr = certificate.shareCertificate(sr);
//    		sr.setError("false");
//
//    	} catch (Exception e) {
//    		
//			if ("http://localhost:8080/".equals(SERVER_PATH)) {
//				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
//						+ "; Path=/studentportal; HttpOnly; SameSite=none;");
//			} else {
//				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
//						+ "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
//			}
//			sr.setReturn_url("https://www.linkedin.com/oauth/v2/authorization?client_id=" + LINKED_IN_CLIENT_ID
//					+ "&scope=" + LINKED_IN_SCOPE + "&response_type=" + LINKED_IN_CODE + "&redirect_uri=" + SERVER_PATH
//					+ LINKED_IN_REDIRECT_URI);
//			request.getSession().setAttribute("sr", sr);
//    		sr.setError("true");
//			e.printStackTrace();
//    	}
//    	
//		return new ResponseEntity<>(sr, HttpStatus.OK);
//    }

//    @RequestMapping (value="/m/shareCertificateLinkedIn", method= RequestMethod.POST)
//    public ResponseEntity<ServiceRequest> shareCertificateLinkedIn(@RequestBody ServiceRequest sr, HttpServletRequest request, HttpServletResponse response) {
//    	
//
//    	try {
//    		
//        	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
//    		sr = certificate.shareCertificate(sr);
//    		sr.setError("false");
//			request.getSession().setAttribute("sr", sr);
//
//    	} catch (Exception e) {
//    		
//			if ("http://localhost:8080/".equals(SERVER_PATH)) {
//				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
//						+ "; Path=/studentportal; HttpOnly; SameSite=none;");
//			} else {
//				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
//						+ "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
//			}
//			
//			sr.setReturn_url("https://www.linkedin.com/oauth/v2/authorization?client_id=" + LINKED_IN_CLIENT_ID
//					+ "&scope=" + LINKED_IN_SCOPE + "&response_type=" + LINKED_IN_CODE + "&redirect_uri=" + SERVER_PATH
//					+ LINKED_IN_REDIRECT_URI);
//
//    		sr.setError("true");
//			request.getSession().setAttribute("sr", sr);
//			//e.printStackTrace();
//			
//    	}
//    	
//		return new ResponseEntity<>(sr, HttpStatus.OK);
//    }
//    
    
	@RequestMapping(value ="/linkedin/auth/callback/registration", method= RequestMethod.GET)
	public String linkedinAuthCallback(HttpServletRequest request,@RequestParam String code, HttpServletResponse response){
		
		//pass code to helper 
		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
		String redirectURI = "";
		
		try {

			linkedInManager.linkedinAuthCallbackV2(code,  sr.getSapId());
			CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
		 	sr = certificate.shareCertificate(sr);
		 	
		 	switch(sr.getProductType()) {
		 		case "MBAWX":
					redirectURI = SERVER_PATH+"studentportal/response?success="+true+"&productType=MBAWX";
		 			break;
		 		case "PG":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+true+"&productType=PG";
		 			break;
		 		case "MBAX":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+true+"&productType=MBAX";
		 			break;
		 		case "LEAD":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+true+"&productType=LEAD";
		 			break;
		 		
		 	}
		 	
			return "redirect:"+redirectURI;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

		 	switch(sr.getProductType()) {
		 		case "MBAWX":
					redirectURI = SERVER_PATH+"studentportal/response?success="+false+"&productType=MBAWX";
		 			break;
		 		case "PG":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+false+"&productType=PG";
		 			break;
		 		case "MBAX":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+false+"&productType=MBAX";
		 			break;
		 		case "LEAD":
				 	redirectURI = SERVER_PATH+"studentportal/response?success="+false+"&productType=LEAD";
		 			break;
		 		
		 	}
		 	
			return "redirect:"+redirectURI;
		} 

	}

	@RequestMapping(value ="/linkedin/auth/callbackforrank/registration", method= RequestMethod.GET)
	public ModelAndView linkedinAuthCallback(HttpServletRequest request, @RequestParam String code){

		ModelAndView modelAndView = new ModelAndView("redirect: /studentportal/student/ranks");
		String sapid = (String)request.getSession().getAttribute("userId");
		String shareURL = (String)request.getSession().getAttribute("shareLinkURL");
		String shareText = (String)request.getSession().getAttribute("shareText");
		String redirect_uri = SERVER_PATH + LINKED_IN_RANK_REDIRECT_URI;
		String link_path = (String)request.getSession().getAttribute("link_path");
		
		try {
			
			linkedInManager.linkedinAuthCallback( code, sapid, shareURL, shareText, redirect_uri, link_path );
			modelAndView.addObject("alreadyLinkedAccount", false);
			
		}catch (Exception e) {
			
			//e.printStackTrace();
			modelAndView.addObject("alreadyLinkedAccount", true);
			
		}
		
		return modelAndView;
	}

	@RequestMapping( value="/response", method = RequestMethod.GET )
	public ModelAndView response(@RequestParam Boolean success, @RequestParam String productType) {
		
		ModelAndView modelAndView = new ModelAndView("jsp/response");
		modelAndView.addObject("success", success);
		modelAndView.addObject("successMessage", "Successfully shared Final Certificate on LinkedIn.");
		modelAndView.addObject("errorMessage", "An error occured while attempting to share the Final Certificate. Please try again later.");
		
		switch (productType) {
			case "MBAWX":
				modelAndView.addObject("redirectURI", SERVER_PATH+"timeline/selectSR" );
				break;
			case "PG":
				modelAndView.addObject("redirectURI", SERVER_PATH+"studentportal/student/addSRForm");
				break;
			case "MBAX":
				modelAndView.addObject("redirectURI", SERVER_PATH+"redirectURIForMBAX" );
				break;
			case "LEAD":
				modelAndView.addObject("redirectURI", SERVER_PATH+"redirectURIForLEAD");
				break;
		}
		return modelAndView; 
		
	}

	public String encryptWithOutSpecialCharacters(String stringToBeEncrypted) throws Exception{

		return AESencrp.encrypt(stringToBeEncrypted).replaceAll("\\+", "_plus_");
	}
	
	public String decryptWithOutSpecialCharacters(String stringToBeDecrypted) throws Exception{
		
		return AESencrp.decrypt(stringToBeDecrypted.replaceAll("_plus_", "\\+"));
	}
}
