package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("m")
public class LinkedInRestController {
	
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

	private static final Logger logger = LoggerFactory.getLogger(LinkedInRestController.class);
	
    @PostMapping(value="/shareCertificateLinkedIn")
    public ResponseEntity<ServiceRequestStudentPortal> shareCertificateLinkedIn(@RequestBody ServiceRequestStudentPortal sr, HttpServletRequest request, HttpServletResponse response) {
    	
    	try {
    		
        	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
    		sr = certificate.shareCertificate(sr);
    		sr.setError("false");

    	} catch (Throwable t) {
    		
			if ("http://localhost:8080/".equals(SERVER_PATH)) {
				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
						+ "; Path=/studentportal; HttpOnly; SameSite=none;");
			} else {
				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId()
						+ "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
			}
			sr.setReturn_url("https://www.linkedin.com/oauth/v2/authorization?client_id=" + LINKED_IN_CLIENT_ID
					+ "&scope=" + LINKED_IN_SCOPE + "&response_type=" + LINKED_IN_CODE + "&redirect_uri=" + SERVER_PATH
					+ LINKED_IN_REDIRECT_URI);
			request.getSession().setAttribute("sr", sr);
    		sr.setError("true");

			logger.error(ExceptionUtils.getFullStackTrace(t));
    	}
    	
		return new ResponseEntity<>(sr, HttpStatus.OK);
    }

    
	@PostMapping(value = "/shareCycleWiseRankAsPostOnLinkedIn", consumes ="application/json", produces = "application/json" )
	public ResponseEntity<LinkedInAddCertToProfileBean> shareCycleWiseRankAsPostOnLinkedIn( HttpServletRequest request, @RequestBody StudentRankBean bean ) {

		LinkedInAddCertToProfileBean linkedInBean = new LinkedInAddCertToProfileBean();
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		String userId = bean.getSapid();
		String sem = bean.getSem();
		String encryptedUserId = "", encryptedSem = "", encryptedSubject = "", link_path = "";
		
		try {
			encryptedUserId = encryptWithOutSpecialCharacters( userId ) ;
			encryptedSem = encryptWithOutSpecialCharacters( sem );
			encryptedSubject = encryptWithOutSpecialCharacters( "NA" );
		} catch (Throwable t) {
			//e.printStackTrace();
			logger.error(ExceptionUtils.getFullStackTrace(t));
		}

		try {
			link_path = createRankPDF.createCycleWiseRankPDF(userId, encryptedSubject, sem);
			request.getSession().setAttribute("link_path" , link_path);
		} catch (Throwable t) {
			//e.printStackTrace();
			logger.error(ExceptionUtils.getFullStackTrace(t));
		}
		
		String share_text = "In my quest to become a better professional, I have achieved the below rank for my #SEM"+ bean.getSem()
				+ " from NMIMS Global Access School for Continuing Education - India\'s Largest Ed-Tech University "
				+ "#NMIMSGlobalAccess #ContinuingEducation";

		request.getSession().setAttribute("shareText", share_text);
		
		try {
			linkedInBean = serviceRequestDao.getLinkedInProfile( userId );
			linkedInManager.registerUploadPDF( userId, linkedInBean.getPersonId(), link_path, linkedInBean.getAccess_token(), share_text);
			linkedInBean.setLinkedInShareStatus(true);
			return new ResponseEntity<>( linkedInBean, header, HttpStatus.OK);
		}catch (Throwable t) {
			//e.printStackTrace();
			logger.error(ExceptionUtils.getFullStackTrace(t));
			linkedInBean.setLinkedInShareStatus(false);
			linkedInBean.setLinkedInOauthRedirectURL("https://www.linkedin.com/oauth/v2/authorization?response_type=" + LINKED_IN_CODE + 
					"&client_id=" + LINKED_IN_CLIENT_ID + "&redirect_uri=" + SERVER_PATH + LINKED_IN_RANK_REDIRECT_URI + "&scope=" + LINKED_IN_SCOPE);
			return new ResponseEntity<>( linkedInBean, header, HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	@PostMapping(value = "/shareSubjectWiseRankAsPostOnLinkedIn", consumes ="application/json", produces = "application/json")
	public  ResponseEntity<LinkedInAddCertToProfileBean> shareSubjectWiseRankAsPostOnLinkedIn( HttpServletRequest request, @RequestBody StudentRankBean bean ) throws Exception{
		
		LinkedInAddCertToProfileBean linkedInBean = new LinkedInAddCertToProfileBean();
    	PortalDao portalDao = (PortalDao)act.getBean("portalDAO");
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		String userId = bean.getSapid();
		String link_path = "";
		
		String encryptedUserId = encryptWithOutSpecialCharacters( userId );
		String encryptedSem = encryptWithOutSpecialCharacters( bean.getSem() );
		String encryptedSubject = encryptWithOutSpecialCharacters( bean.getSubjectcodeMappingId() );

		bean.setSubject( portalDao.getSubjectnameForId( bean.getSubjectcodeMappingId() ) );

		try {
			link_path = createRankPDF.createSubjectWiseRankPDF(userId, bean.getSubject(), bean.getSem());
			request.getSession().setAttribute("link_path" , link_path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		//linkedInBean = linkedInManager.sharePostAsURLOnLinkedIn( shareText, shareURL, userId );
		
		String subjectWithoutSpeciChar = bean.getSubject().replaceAll("'", "");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(",", "");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll("&", "And");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(" ", "");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll(":", "");
		subjectWithoutSpeciChar = subjectWithoutSpeciChar.replaceAll("'", "");
		
		String share_text = "In my quest to become a better professional, I have achieved the below rank for the #"+ subjectWithoutSpeciChar + " in #SEM" + bean.getSem()
				+ " from NMIMS Global Access School for Continuing Education - India\'s Largest Ed-Tech University "
				+ "#NMIMSGlobalAccess #ContinuingEducation";

		request.getSession().setAttribute("shareText", share_text);
		
		try {
			linkedInBean = serviceRequestDao.getLinkedInProfile( userId );
			linkedInManager.registerUploadPDF( userId, linkedInBean.getPersonId(), link_path, linkedInBean.getAccess_token(), share_text );
			linkedInBean.setLinkedInShareStatus(true);
			return new ResponseEntity<>( linkedInBean, header, HttpStatus.OK);
		}catch (Throwable t) {
			//e.printStackTrace();
			logger.error(ExceptionUtils.getFullStackTrace(t));
			linkedInBean.setLinkedInShareStatus(false);
			linkedInBean.setLinkedInOauthRedirectURL("https://www.linkedin.com/oauth/v2/authorization?response_type=" + LINKED_IN_CODE + 
					"&client_id=" + LINKED_IN_CLIENT_ID + "&redirect_uri=" + SERVER_PATH + LINKED_IN_RANK_REDIRECT_URI + "&scope=" + LINKED_IN_SCOPE);
			return new ResponseEntity<>( linkedInBean, header, HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	public String encryptWithOutSpecialCharacters(String stringToBeEncrypted) throws Exception{

		return AESencrp.encrypt(stringToBeEncrypted).replaceAll("\\+", "_plus_");
	}
	
	public String decryptWithOutSpecialCharacters(String stringToBeDecrypted) throws Exception{
		
		return AESencrp.decrypt(stringToBeDecrypted.replaceAll("_plus_", "\\+"));
	}
}
