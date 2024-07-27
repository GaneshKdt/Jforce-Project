package com.nmims.coursera.helpers.openSAML;

import java.util.Base64;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.core.config.InitializationException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.coursera.beans.CourseraSSODetails;
import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.dao.CourseraUsersDao;
import com.nmims.coursera.helpers.openSAML.CourseraMetadataGenerator;;


@Component
public class CourseraSamlRequestHelper {
	
	private static final Logger courseraSamlSSOLogger = LoggerFactory.getLogger("coursera_saml_sso");

	@Autowired
	CourseraUsersDao courseraUsersDao;
	
	@Autowired
	ArtifactResolver artifactResolver;
	
	@Autowired
	CourseraMetadataGenerator courseraMetadataGenerator;
	
	public void generateSamlRequestForCoursera(String samlRequest, HttpServletRequest servletRequest, ModelAndView modelAndView, StudentCourseraBean studentInfo) {
		
		// Initialize at top level.
		ArtifactResolver.init();
		MessageContext<SAMLObject> context = new MessageContext<SAMLObject>();
		CourseraSSODetails authnRequestFields = artifactResolver.getCourseraSSOFields(samlRequest, servletRequest);
		
		authnRequestFields.setStudentInfo(studentInfo);

		courseraSamlSSOLogger.info("CourseraSSO authnRequestFields : "+authnRequestFields);
		
		modelAndView.addObject("redirectURL" , authnRequestFields.getDestinationURL());
		
		Response response = artifactResolver.generateCourseraSAMLResponse(authnRequestFields);
		context.setMessage(response);
		
		String responseString = OpenSAMLUtils.getSAMLString(response);

		modelAndView.addObject("SAMLResponse" , encodeBase64(responseString));
		modelAndView.addObject("SAMLResponseString" , responseString);

		System.out.println("SAMLResponse" + encodeBase64(responseString));
		System.out.println("SAMLResponseString" + responseString);
		
		courseraSamlSSOLogger.info("coursera_sso SAMLResponseString : "+responseString);
	}
	
	public String generateCourseraMetadata() throws InitializationException {
		CourseraMetadataGenerator.init();

		EntityDescriptor entityDescriptor = courseraMetadataGenerator.generateMetadata();
		String responseString = OpenSAMLUtils.getSAMLString(entityDescriptor);
		return responseString;
	}
	
	private String encodeBase64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}
}
