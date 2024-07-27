package com.nmims.controllers;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nmims.beans.CSResponse;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.SamlRequest;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.StudentEntitlement;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.openSAML.OpenSAMLUtils;
import com.nmims.helpers.openSAML.idp.ArtifactResolver;

@Controller
public class LearningPortal extends CSPortalBaseController{

	@Autowired
	ArtifactResolver artifactResolver;

	@Autowired
	EntitlementActivationDAO entitlementActivationDAO;

	private static final Logger logger = LoggerFactory.getLogger(LearningPortal.class);
 
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${REV_SSO_REV_DESTINATION}")
	private String REV_SSO_REV_DESTINATION;
	
    Gson gson = new Gson();

	@RequestMapping(value = "/learning_portal", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8" )
	public String learningPortal(Locale locale,HttpServletRequest request, Model model) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		if(!checkCSAccess(request)) {
			return "redirect:showAllProducts"; 
		}
		StudentCareerservicesBean student = (StudentCareerservicesBean) request.getSession().getAttribute("student_careerservices");
		String saml = getSAMLString(student);
		model.addAttribute("PostTo", REV_SSO_REV_DESTINATION);
		model.addAttribute("SAMLResponse",saml);

		return "portal/learning_portal/learning_portal_home";
	}	
	

	@RequestMapping(value = "/learning_portal_mobileAccess", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	public String learningPortal(Locale locale,HttpServletRequest request, Model model, @RequestParam("RequestObject") String requestObject) {
	
		try {
			if(requestObject.length() == 0) {
				return "portal/learning_portal/learning_portal_home_mobile";
			}

			String sapid = AESencrp.decrypt(requestObject);
			StudentCareerservicesBean student = getStudent(sapid);
			
			if(!checkCSAccess(student)) {
				return "portal/learning_portal/learning_portal_home_mobile";
			}
			
			String saml = getSAMLString(student);
			model.addAttribute("PostTo", REV_SSO_REV_DESTINATION);
			model.addAttribute("SAMLResponse",saml);
			return "portal/learning_portal/learning_portal_home_mobile";
		} catch (Exception e) {
			logger.info("in LearningPortal class got exception : "+e.getMessage());
		}
		return "portal/learning_portal/learning_portal_home_mobile";
	}	

	@RequestMapping(value = "/getLearningPortalLink", method = RequestMethod.POST)
	public ResponseEntity<String> getLearningPortalLink(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
		Gson gson = new GsonBuilder()
			    .disableHtmlEscaping()
			    .create();
		
		CSResponse csResponse = new CSResponse();
		
		if(!requestParams.containsKey("sapid")) {
			csResponse.setNotValidRequest();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		String sapid = requestParams.get("sapid");
		StudentCareerservicesBean student = getStudent(sapid);
		if(student == null) {
			csResponse.setInvalidSapid();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		if(!checkCSAccess(student)) {
			csResponse.setNoCSAccess();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		
		StudentEntitlement entitlement = entitlementActivationDAO.getApplicableEntitlementForPurchase(student.getSapid(), FeatureTypes.LEARNING_PORTAL);

		if(entitlement == null) {
			csResponse.setStatusFailure();
			csResponse.setMessage("No Learning Portal Access");
		}
		if(entitlement.isEnded()) {
			csResponse.setStatusFailure();
			csResponse.setMessage("No Learning Portal Access");
		}
		try {

			String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(sapid), "UTF-8"); 
			csResponse.setResponse(SERVER_PATH + "careerservices/learning_portal_mobileAccess?RequestObject=" + encryptedSapId);
			csResponse.setStatusSuccess();
		} catch (Exception e) {
			logger.info("in LearningPortal class got exception : "+e.getMessage());
			csResponse.setStatusFailure();
			csResponse.setMessage("Error generating url.");
		}
		return ResponseEntity.ok(gson.toJson(csResponse));
	}

	private String getSAMLString(StudentCareerservicesBean student) {
		MessageContext<SAMLObject> context = new MessageContext<SAMLObject>();

		SamlRequest samlReq = getSamlRequest(student);
		if(samlReq == null) {
			return null;
		}
		Response response = artifactResolver.generateSAMLResponse(samlReq);
		context.setMessage(response);
		String responseString = OpenSAMLUtils.getSAMLString(response);
		return encodeBase64(responseString);
	}
	private SamlRequest getSamlRequest(StudentCareerservicesBean student){

		StudentEntitlement entitlement = entitlementActivationDAO.getApplicableEntitlementForPurchase(student.getSapid(), FeatureTypes.LEARNING_PORTAL);

		if(entitlement == null) {
			return null;
		}
		if(entitlement.isEnded()) {
			return null;
		}
		SamlRequest samlReq = new SamlRequest();
		samlReq.setEmailId(student.getEmailId());
        samlReq.setFirstName(student.getFirstName());
        samlReq.setLastName(student.getLastName());

        if(entitlement.getDurationType() == null) {
        	return null;
        }
        switch(entitlement.getDurationType().toLowerCase()){
			case "slow":
				samlReq.setReg_Code24Month();
				break;
			case "normal":
				samlReq.setReg_Code12Month();
				break;
			case "fast":
				samlReq.setReg_Code6Month();
				break;
        }
        samlReq.setUserId(student.getSapid());
        return samlReq;
	}
	
	private String encodeBase64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
}
