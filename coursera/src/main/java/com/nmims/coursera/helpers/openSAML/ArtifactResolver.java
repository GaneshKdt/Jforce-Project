package com.nmims.coursera.helpers.openSAML;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.zip.Inflater;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nmims.coursera.beans.CourseraSSODetails;
import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.helpers.openSAML.OpenSAMLSignatureFieldsGenerator;

@Component
public class ArtifactResolver {
	
	@Autowired
	private OpenSAMLSignatureFieldsGenerator signatureFieldsGenerator;
	
	@Value("${COURSERA_METADATA_LOCATION}")
	private String COURSERA_METADATA_LOCATION;

	@Value("${COURSERA_IDP_SERVICE_LOCATION}")
	private String COURSERA_IDP_SERVICE_LOCATION;	

	public static void init(){
		try {
			InitializationService.initialize();
		} catch (InitializationException e) {
//			System.out.println("error!!!!!!!!!!!!!!!!" + e);		
		}
	}
	
	public CourseraSSODetails getCourseraSSOFields(String samlRequest, HttpServletRequest servletRequest) {
		CourseraSSODetails inputDetails = new CourseraSSODetails();
		try {
			AuthnRequest authnRequest = getAuthnRequest(samlRequest, servletRequest);
			inputDetails.setDestinationURL(authnRequest.getAssertionConsumerServiceURL());
			inputDetails.setID(authnRequest.getID());
			inputDetails.setAudience(authnRequest.getIssuer().getValue());
		} catch (Exception e) {
			e.printStackTrace();
			inputDetails.setError("Error : " + e);
		}
		return inputDetails;
	}
	
	public AuthnRequest getAuthnRequest(String samlRequest, HttpServletRequest servletRequest) throws Exception {
		byte[] decodedBytes = Base64.getDecoder().decode(samlRequest.getBytes());
		String SAMLRequest = new String(decodedBytes, "UTF-8");
		if (servletRequest.getHeader("Accept-Encoding").contains("deflate")) {
			try {
				Inflater inflater = new Inflater(true);
				inflater.setInput(decodedBytes);
				byte[] xmlMessageBytes = new byte[5000];
				int resultLength = inflater.inflate(xmlMessageBytes);
				inflater.end();
				SAMLRequest = new String(xmlMessageBytes, 0, resultLength, "UTF-8");
				
			} catch (Exception e) {
//				System.out.println("Exception during inflation attempt. Data might be already deflated."+e);
			}
		}
		AuthnRequest samlRequestObject = getSamlRequestObjectFromSAMLRequest(SAMLRequest);	
		return samlRequestObject;
	}
	
	public AuthnRequest getSamlRequestObjectFromSAMLRequest(String SAMLRequest) {
		AuthnRequest samlRequestObject = null; 
		
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(SAMLRequest.getBytes("UTF-8"));
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(is);
			Element element = document.getDocumentElement();
			samlRequestObject = getSamlRequestObjectFromElement(element);
			
		}catch(Exception e) {
			e.printStackTrace();			
		}
		return samlRequestObject;
	}
	
	private AuthnRequest getSamlRequestObjectFromElement(Element element) {
		UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
		XMLObject responseXmlObj = null;
		try {
			responseXmlObj = unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			e.printStackTrace();
		}
		return (AuthnRequest) responseXmlObj;					
	}
	
	public Response generateCourseraSAMLResponse(CourseraSSODetails authnRequestFields){
		Response response = buildCourseraResponse(authnRequestFields);
		return response;
	}
	
	private Response buildCourseraResponse(CourseraSSODetails authnRequestFields) {

		//build response using format sent by rev
		Response response = OpenSAMLUtils.buildSAMLObject(Response.class);
		response.setDestination(authnRequestFields.getDestinationURL());
		response.setIssueInstant(new DateTime());
		response.setInResponseTo(authnRequestFields.getID());
		response.setID(OpenSAMLUtils.generateSecureRandomId());
		
		Issuer issuer2 = OpenSAMLUtils.buildSAMLObject(Issuer.class);
		issuer2.setValue(COURSERA_METADATA_LOCATION);
		response.setIssuer(issuer2);
		
		Status status2 = OpenSAMLUtils.buildSAMLObject(Status.class);
		StatusCode statusCode2 = OpenSAMLUtils.buildSAMLObject(StatusCode.class);
		statusCode2.setValue(StatusCode.SUCCESS);
		status2.setStatusCode(statusCode2);

		response.setStatus(status2);
		
		Assertion assertion = buildCourseraAssertion(authnRequestFields);
		courseraSignAssertion(assertion);
//		EncryptedAssertion encryptedAssertion = encryptAssertion(assertion);
		response.getAssertions().add(assertion);
//		response.getEncryptedAssertions().add(encryptedAssertion);

		return response;
	}
	
	private Assertion buildCourseraAssertion(CourseraSSODetails authnRequestFields) {
		Assertion assertion = OpenSAMLUtils.buildSAMLObject(Assertion.class);
		Issuer issuer = OpenSAMLUtils.buildSAMLObject(Issuer.class);
		issuer.setValue(COURSERA_METADATA_LOCATION);
		assertion.setIssuer(issuer);
		assertion.setIssueInstant(new DateTime());
		assertion.setID(OpenSAMLUtils.generateSecureRandomId());
		Subject subject = OpenSAMLUtils.buildSAMLObject(Subject.class);
		assertion.setSubject(subject);
		NameID nameID = OpenSAMLUtils.buildSAMLObject(NameID.class);
		nameID.setValue("ngasceRevKey");
		subject.setNameID(nameID);
		subject.getSubjectConfirmations().add(buildSubjectConfirmation(authnRequestFields));
		assertion.setConditions(buildConditions(authnRequestFields));
		assertion.getAuthnStatements().add(buildAuthnStatement(authnRequestFields));
		assertion.getAttributeStatements().add(buildAttributeStatement(authnRequestFields));
		return assertion;
	}
	
	private SubjectConfirmation buildSubjectConfirmation(CourseraSSODetails authnRequestFields) {
		SubjectConfirmation subjectConfirmation = OpenSAMLUtils.buildSAMLObject(SubjectConfirmation.class);
		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

		SubjectConfirmationData subjectConfirmationData = OpenSAMLUtils.buildSAMLObject(SubjectConfirmationData.class);
		subjectConfirmationData.setNotOnOrAfter(new DateTime().plusDays(2));

		subjectConfirmationData.setInResponseTo(authnRequestFields.getID());
		subjectConfirmationData.setRecipient(authnRequestFields.getDestinationURL());
		
		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

		return subjectConfirmation;
	}
	
	private Conditions buildConditions(CourseraSSODetails authnRequestFields) {
		Conditions conditions = OpenSAMLUtils.buildSAMLObject(Conditions.class);
		conditions.setNotBefore(new DateTime().minusDays(2));
		conditions.setNotOnOrAfter(new DateTime().plusDays(2));
		AudienceRestriction audienceRestriction = OpenSAMLUtils.buildSAMLObject(AudienceRestriction.class);
		Audience audience = OpenSAMLUtils.buildSAMLObject(Audience.class);
		audience.setAudienceURI(authnRequestFields.getAudience());
		audienceRestriction.getAudiences().add(audience);
		conditions.getAudienceRestrictions().add(audienceRestriction);
		return conditions;
	}
	
	private AuthnStatement buildAuthnStatement(CourseraSSODetails authnRequestFields) {
		AuthnStatement authnStatement = OpenSAMLUtils.buildSAMLObject(AuthnStatement.class);
		AuthnContext authnContext = OpenSAMLUtils.buildSAMLObject(AuthnContext.class);
		AuthnContextClassRef authnContextClassRef = OpenSAMLUtils.buildSAMLObject(AuthnContextClassRef.class);
		authnContextClassRef.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);
		authnContext.setAuthnContextClassRef(authnContextClassRef);
		authnStatement.setAuthnContext(authnContext);

		authnStatement.setAuthnInstant(new DateTime());

		return authnStatement;
	}
	
	private AttributeStatement buildAttributeStatement(CourseraSSODetails authnRequestFields) {
		AttributeStatement attributeStatement = OpenSAMLUtils.buildSAMLObject(AttributeStatement.class);
		StudentCourseraBean studentInfo = authnRequestFields.getStudentInfo();
		attributeStatement.getAttributes().add(OpenSAMLUtils.createAttribute("firstName", studentInfo.getFirstName()));
		attributeStatement.getAttributes().add(OpenSAMLUtils.createAttribute("lastName", studentInfo.getLastName()));
		attributeStatement.getAttributes().add(OpenSAMLUtils.createAttribute("email", studentInfo.getEmailId()));
		attributeStatement.getAttributes().add(OpenSAMLUtils.createAttribute("eppn", studentInfo.getSapid()));
		return attributeStatement;
	}
	
	private void courseraSignAssertion(Assertion assertion) {
		
		Signature signature = OpenSAMLUtils.buildSAMLObject(Signature.class);
		Credential credential = signatureFieldsGenerator.getCredential();
//		signature.setSigningCredential(credential);
//		signature.setKeyInfo(credentialGenerator.getKeyInfo(credential));
		signature.setKeyInfo(signatureFieldsGenerator.getKeyInfo(credential));
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

		// Need to supply an org.opensaml.security.credential.Credential;
		signature.setSigningCredential(credential);
		assertion.setSignature(signature);
		try {
			XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
		} catch (MarshallingException e) {
			throw new RuntimeException(e);
		}

		try {
			Signer.signObject(signature);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}
}
